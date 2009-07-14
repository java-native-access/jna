/*
 * @(#)dispatch.c       1.9 98/03/22
 * 
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright (c) 2007-2009 Timothy Wall. All Rights Reserved.
 * Copyright (c) 2007 Wayne Meissner. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */

/*
 * JNI native methods supporting the infrastructure for shared
 * dispatchers.  
 */

#if defined(_WIN32)
#ifdef _MSC_VER
#pragma warning( disable : 4201 ) /* nameless struct/union (jni_md.h) */
#endif
#ifndef UNICODE
#define UNICODE
#endif
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <psapi.h>
#define LIBNAMETYPE wchar_t*
#define LIBNAME2CSTR(ENV,JSTR) newWideCString(ENV,JSTR)
/* See http://msdn.microsoft.com/en-us/library/ms682586(VS.85).aspx:
 * "Note that the standard search strategy and the alternate search strategy  
 * specified by LoadLibraryEx with LOAD_WITH_ALTERED_SEARCH_PATH differ in    
 * just one way: The standard search begins in the calling application's      
 * directory, and the alternate search begins in the directory of the         
 * executable module that LoadLibraryEx is loading."                          
 */
#define LOAD_LIBRARY(NAME) (NAME ? LoadLibraryExW(NAME, NULL, LOAD_WITH_ALTERED_SEARCH_PATH) : GetModuleHandleW(NULL))
#define LOAD_ERROR(BUF,LEN) w32_format_error(BUF, LEN)
#define FREE_LIBRARY(HANDLE) (((HANDLE)==GetModuleHandleW(NULL) || FreeLibrary(HANDLE))?0:-1)
#define FIND_ENTRY(HANDLE, NAME) GetProcAddress(HANDLE, NAME)
#define GET_LAST_ERROR() GetLastError()
#define SET_LAST_ERROR(CODE) SetLastError(CODE)
static char*
w32_format_error(char* buf, int len) {
  FormatMessageA(FORMAT_MESSAGE_FROM_SYSTEM, NULL, GetLastError(),
                 0, buf, len, NULL);
  return buf;
}
#else
#include <dlfcn.h>
#include <errno.h>
#define LIBNAMETYPE char*
#ifdef __APPLE__
#define LIBNAME2CSTR(ENV,JSTR) newCStringUTF8(ENV,JSTR)
#else
#define LIBNAME2CSTR(ENV,JSTR) newCString(ENV,JSTR)
#endif
#define LOAD_LIBRARY(NAME) dlopen(NAME, RTLD_LAZY|RTLD_GLOBAL)
#define LOAD_ERROR(BUF,LEN) (snprintf(BUF, LEN, "%s", dlerror()), BUF)
#define FREE_LIBRARY(HANDLE) dlclose(HANDLE)
#define FIND_ENTRY(HANDLE, NAME) dlsym(HANDLE, NAME)
#define GET_LAST_ERROR() errno
#define SET_LAST_ERROR(CODE) (errno = (CODE))
#endif

#include <stdlib.h>
#include <string.h>
#include <wchar.h>
#include <jni.h>

#include <jawt.h>
#include <jawt_md.h>

#include "dispatch.h"
#include "com_sun_jna_Pointer.h"
#include "com_sun_jna_Memory.h"
#include "com_sun_jna_Function.h"
#include "com_sun_jna_Native.h"
#include "com_sun_jna_NativeLibrary.h"
#include "com_sun_jna_CallbackReference.h"

#ifdef HAVE_PROTECTION
static int _protect;
#undef PROTECT
#define PROTECT _protect
#endif

#ifdef __cplusplus
extern "C"
#endif

static jboolean preserve_last_error;

#define MEMCPY(D,S,L) do { \
  PSTART(); memcpy(D,S,L); PEND(); \
} while(0)
#define MEMSET(D,C,L) do { \
  PSTART(); memset(D,C,L); PEND(); \
} while(0)

#define MASK_CC          com_sun_jna_Function_MASK_CC
#define THROW_LAST_ERROR com_sun_jna_Function_THROW_LAST_ERROR

/* Cached class, field and method IDs */
static jclass classObject;
static jclass classClass;
static jclass classMethod;
static jclass classVoid, classPrimitiveVoid;
static jclass classBoolean, classPrimitiveBoolean;
static jclass classByte, classPrimitiveByte;
static jclass classCharacter, classPrimitiveCharacter;
static jclass classShort, classPrimitiveShort;
static jclass classInteger, classPrimitiveInteger;
static jclass classLong, classPrimitiveLong;
static jclass classFloat, classPrimitiveFloat;
static jclass classDouble, classPrimitiveDouble;
static jclass classString, classWString;
static jclass classBuffer;
static jclass classByteBuffer;
static jclass classCharBuffer;
static jclass classShortBuffer;
static jclass classIntBuffer;
static jclass classLongBuffer;
static jclass classFloatBuffer;
static jclass classDoubleBuffer;

static jclass classPointer;
static jclass classNative;
static jclass classStructure;
static jclass classStructureByValue;
static jclass classCallback;
static jclass classCallbackReference;
static jclass classNativeMapped;
static jclass classIntegerType;
static jclass classPointerType;
static jclass class_ffi_callback;

static jmethodID MID_Class_getComponentType;
static jmethodID MID_Object_toString;
static jmethodID MID_String_getBytes;
static jmethodID MID_String_getBytes2;
static jmethodID MID_String_toCharArray;
static jmethodID MID_String_init_bytes;
static jmethodID MID_Method_getReturnType;
static jmethodID MID_Method_getParameterTypes;
static jmethodID MID_Long_init;
static jmethodID MID_Integer_init;
static jmethodID MID_Short_init;
static jmethodID MID_Character_init;
static jmethodID MID_Byte_init;
static jmethodID MID_Boolean_init;
static jmethodID MID_Float_init;
static jmethodID MID_Double_init;
static jmethodID MID_ByteBuffer_array;
static jmethodID MID_ByteBuffer_arrayOffset;
static jmethodID MID_CharBuffer_array;
static jmethodID MID_CharBuffer_arrayOffset;
static jmethodID MID_ShortBuffer_array;
static jmethodID MID_ShortBuffer_arrayOffset;
static jmethodID MID_IntBuffer_array;
static jmethodID MID_IntBuffer_arrayOffset;
static jmethodID MID_LongBuffer_array;
static jmethodID MID_LongBuffer_arrayOffset;
static jmethodID MID_FloatBuffer_array;
static jmethodID MID_FloatBuffer_arrayOffset;
static jmethodID MID_DoubleBuffer_array;
static jmethodID MID_DoubleBuffer_arrayOffset;

static jmethodID MID_Pointer_init;
static jmethodID MID_Native_updateLastError;
static jmethodID MID_Native_fromNative;
static jmethodID MID_Native_nativeType;
static jmethodID MID_Native_toNativeTypeMapped;
static jmethodID MID_Native_fromNativeTypeMapped;
static jmethodID MID_Structure_getTypeInfo;
static jmethodID MID_Structure_newInstance;
static jmethodID MID_Structure_useMemory;
static jmethodID MID_Structure_read;
static jmethodID MID_Structure_write;
static jmethodID MID_CallbackReference_getCallback;
static jmethodID MID_CallbackReference_getFunctionPointer;
static jmethodID MID_CallbackReference_getNativeString;
static jmethodID MID_NativeMapped_toNative;
static jmethodID MID_WString_init;
static jmethodID MID_ToNativeConverter_nativeType;
static jmethodID MID_ffi_callback_invoke;

static jfieldID FID_Boolean_value;
static jfieldID FID_Byte_value;
static jfieldID FID_Short_value;
static jfieldID FID_Character_value;
static jfieldID FID_Integer_value;
static jfieldID FID_Long_value;
static jfieldID FID_Float_value;
static jfieldID FID_Double_value;

static jfieldID FID_Pointer_peer;
static jfieldID FID_Structure_memory;
static jfieldID FID_Structure_typeInfo;
static jfieldID FID_IntegerType_value;
static jfieldID FID_PointerType_pointer;

/* Value of System property jna.encoding. */
static const char* jna_encoding = NULL;

/* Forward declarations */
static char* newCString(JNIEnv *env, jstring jstr);
static char* newCStringUTF8(JNIEnv *env, jstring jstr);
static char* newCStringEncoding(JNIEnv *env, jstring jstr, const char* encoding);
static wchar_t* newWideCString(JNIEnv *env, jstring jstr);

static void* getBufferArray(JNIEnv*, jobject, jobject*, void **, void **);
static char getArrayComponentType(JNIEnv *, jobject);
static ffi_type* getStructureType(JNIEnv *, jobject);
static void update_last_error(JNIEnv*, int);

typedef void (JNICALL* release_t)(JNIEnv*,jarray,void*,jint);

/** Invokes System.err.println (for debugging only). */
static void
println(JNIEnv* env, const char* msg) {
  jclass cls = (*env)->FindClass(env, "java/lang/System");
  jfieldID fid = (*env)->GetStaticFieldID(env, cls, "err",
                                          "Ljava/io/PrintStream;");
  jobject err = (*env)->GetStaticObjectField(env, cls, fid);
  jclass pscls = (*env)->FindClass(env, "java/io/PrintStream");
  jmethodID mid = (*env)->GetMethodID(env, pscls, "println",
                                      "(Ljava/lang/String;)V");
  jstring str = newJavaString(env, msg, JNI_FALSE);
  (*env)->CallObjectMethod(env, err, mid, str);
}

jboolean
ffi_error(JNIEnv* env, const char* op, ffi_status status) {
  char msg[256];
  switch(status) {
  case FFI_BAD_ABI:
    snprintf(msg, sizeof(msg), "Invalid calling convention");
    throwByName(env, EIllegalArgument, msg);
    return JNI_TRUE;
  case FFI_BAD_TYPEDEF:
    snprintf(msg, sizeof(msg),
             "Invalid structure definition (native typedef error)");
    throwByName(env, EIllegalArgument, msg);
    return JNI_TRUE;
  default:
    snprintf(msg, sizeof(msg), "%s failed (%d)", op, status);
    throwByName(env, EError, msg);
    return JNI_TRUE;
  case FFI_OK:
    return JNI_FALSE;
  }
}

/* invoke the real native function */
static void
dispatch(JNIEnv *env, jobject self, jint flags, jobjectArray arr, 
         ffi_type *ffi_return_type, void *resP)
{
  int i, nargs;
  void *func;
  jvalue* c_args;
  char array_pt;
  struct _array_elements {
    jobject array;
    void *elems;
    release_t release;
  } *array_elements;
  volatile int array_count = 0;
  ffi_cif cif;
  ffi_type** ffi_types;
  void** ffi_values;
  ffi_abi abi;
  ffi_status status;
  char msg[128];
  callconv_t callconv = flags & MASK_CC;
  const char* volatile throw_type = NULL;
  const char* volatile throw_msg = NULL;
  
  nargs = (*env)->GetArrayLength(env, arr);

  if (nargs > MAX_NARGS) {
    snprintf(msg, sizeof(msg), "Too many arguments (max %ld)", MAX_NARGS);
    throwByName(env, EUnsupportedOperation, msg);
    return;
  }

  c_args = (jvalue*)alloca(nargs * sizeof(jvalue));
  array_elements = (struct _array_elements*)
    alloca(nargs * sizeof(struct _array_elements));
  ffi_types = (ffi_type**)alloca(nargs * sizeof(ffi_type*));
  ffi_values = (void**)alloca(nargs * sizeof(void*));
  
  // Get the function pointer
  func = getNativeAddress(env, self);
  
  for (i = 0; i < nargs; i++) {
    jobject arg = (*env)->GetObjectArrayElement(env, arr, i);
    
    if (arg == NULL) {
      c_args[i].l = NULL;
      ffi_types[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
    }
    else if ((*env)->IsInstanceOf(env, arg, classBoolean)) {
      c_args[i].i = (*env)->GetBooleanField(env, arg, FID_Boolean_value);
      ffi_types[i] = &ffi_type_sint32;
      ffi_values[i] = &c_args[i].i;
    }
    else if ((*env)->IsInstanceOf(env, arg, classByte)) {
      c_args[i].b = (*env)->GetByteField(env, arg, FID_Byte_value);
      ffi_types[i] = &ffi_type_sint8;
      ffi_values[i] = &c_args[i].b;
    }
    else if ((*env)->IsInstanceOf(env, arg, classShort)) {
      c_args[i].s = (*env)->GetShortField(env, arg, FID_Short_value);
      ffi_types[i] = &ffi_type_sint16;
      ffi_values[i] = &c_args[i].s;
    }
    else if ((*env)->IsInstanceOf(env, arg, classCharacter)) {
      if (sizeof(wchar_t) == 2) {
        c_args[i].c = (*env)->GetCharField(env, arg, FID_Character_value);
        ffi_types[i] = &ffi_type_uint16;
        ffi_values[i] = &c_args[i].c;
      }
      else if (sizeof(wchar_t) == 4) {
        c_args[i].i = (*env)->GetCharField(env, arg, FID_Character_value);
        ffi_types[i] = &ffi_type_uint32;
        ffi_values[i] = &c_args[i].i;
      }
      else {
        snprintf(msg, sizeof(msg), "Unsupported wchar_t size (%d)", (int)sizeof(wchar_t));
        throw_type = EUnsupportedOperation;
        throw_msg = msg;
        goto cleanup;
      }
    }
    else if ((*env)->IsInstanceOf(env, arg, classInteger)) {
      c_args[i].i = (*env)->GetIntField(env, arg, FID_Integer_value);
      ffi_types[i] = &ffi_type_sint32;
      ffi_values[i] = &c_args[i].i;
    }
    else if ((*env)->IsInstanceOf(env, arg, classLong)) {
      c_args[i].j = (*env)->GetLongField(env, arg, FID_Long_value);
      ffi_types[i] = &ffi_type_sint64;
      ffi_values[i] = &c_args[i].j;
    }
    else if ((*env)->IsInstanceOf(env, arg, classFloat)) {
      c_args[i].f = (*env)->GetFloatField(env, arg, FID_Float_value);
      ffi_types[i] = &ffi_type_float;
      ffi_values[i] = &c_args[i].f;
    }
    else if ((*env)->IsInstanceOf(env, arg, classDouble)) {
      c_args[i].d = (*env)->GetDoubleField(env, arg, FID_Double_value);
      ffi_types[i] = &ffi_type_double;
      ffi_values[i] = &c_args[i].d;
    }
    else if ((*env)->IsInstanceOf(env, arg, classPointer)) {
      c_args[i].l = getNativeAddress(env, arg);
      ffi_types[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
    }
    else if ((*env)->IsInstanceOf(env, arg, classStructure)) {
      c_args[i].l = getStructureAddress(env, arg);
      ffi_types[i] = getStructureType(env, arg);
      ffi_values[i] = c_args[i].l;
      if (!ffi_types[i]) {
        snprintf(msg, sizeof(msg),
                 "Structure type info not initialized at argument %d", i);
        throw_type = EIllegalState;
        throw_msg = msg;
        goto cleanup;
      }
    }
    else if ((*env)->IsInstanceOf(env, arg, classBuffer)) {
      c_args[i].l = (*env)->GetDirectBufferAddress(env, arg);
      ffi_types[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
      if (c_args[i].l == NULL) {
        c_args[i].l =
          getBufferArray(env, arg, &array_elements[array_count].array,
                         &array_elements[array_count].elems,
                         (void**)&array_elements[array_count].release);
        if (c_args[i].l == NULL) {
          throw_type = EIllegalArgument;
          throw_msg = "Buffer arguments must be direct or have a primitive backing array";
          goto cleanup;
        }
        ++array_count;
      }
    }
    else if ((array_pt = getArrayComponentType(env, arg)) != 0
             && array_pt != 'L') {
      void *ptr = NULL;
      release_t release = NULL;

#define GET_ELEMS(TYPE) do {ptr=(*env)->Get##TYPE##ArrayElements(env,arg,NULL); release=(void*)(*env)->Release##TYPE##ArrayElements; }while(0)
      switch(array_pt) {
      case 'Z': GET_ELEMS(Boolean); break;
      case 'B': GET_ELEMS(Byte); break;
      case 'C': GET_ELEMS(Char); break;
      case 'S': GET_ELEMS(Short); break;
      case 'I': GET_ELEMS(Int); break;
      case 'J': GET_ELEMS(Long); break;
      case 'F': GET_ELEMS(Float); break;
      case 'D': GET_ELEMS(Double); break;
      }
      if (!ptr) {
        throw_type = EOutOfMemory;
        throw_msg = "Could not obtain memory for primitive buffer";
        goto cleanup;
      }
      c_args[i].l = ptr;
      ffi_types[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
      array_elements[array_count].array = arg;
      array_elements[array_count].elems = ptr;
      array_elements[array_count++].release = release;
    }
    else {
      // Anything else, pass directly as a pointer
      c_args[i].l = (void*)arg;
      ffi_types[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
    }
  }
  
  switch (callconv) {
  case CALLCONV_C:
    abi = FFI_DEFAULT_ABI;
    break;
#ifdef _WIN32
  case CALLCONV_STDCALL:
#ifdef _WIN64
    // Ignore requests for stdcall on win64
    abi = FFI_DEFAULT_ABI;
#else
    abi = FFI_STDCALL;
#endif
    break;
#endif // _WIN32
  default:
    snprintf(msg, sizeof(msg), 
            "Unrecognized calling convention: %d", (int)callconv);
    throw_type = EIllegalArgument;
    throw_msg = msg;
    goto cleanup;
  }

  status = ffi_prep_cif(&cif, abi, nargs, ffi_return_type, ffi_types);
  if (!ffi_error(env, "Native call setup", status)) {
    PSTART();
    if (flags & THROW_LAST_ERROR) {
      SET_LAST_ERROR(0);
    }
    ffi_call(&cif, FFI_FN(func), resP, ffi_values);
    if (flags & THROW_LAST_ERROR) {
      int error = GET_LAST_ERROR();
      if (error) {
        snprintf(msg, sizeof(msg), "%d", error);
        throw_type = ELastError;
        throw_msg = msg;
      }
    }
    else if (preserve_last_error) {
      update_last_error(env, GET_LAST_ERROR());
    }
    PROTECTED_END(do { throw_type=EError;throw_msg="Invalid memory access";} while(0));
  }
  
 cleanup:

  // Release array elements
  for (i=0;i < array_count;i++) {
    array_elements[i].release(env, array_elements[i].array,
                              array_elements[i].elems, 0);
  }

  // Must raise any exception *after* all other JNI operations
  if (throw_type) {
    throwByName(env, throw_type, throw_msg);
  }
}

static void
getChars(JNIEnv* env, wchar_t* dst, jcharArray chars, jint off, jint len) {
  PSTART();
  if (sizeof(jchar) == sizeof(wchar_t)) {
    (*env)->GetCharArrayRegion(env, chars, 0, len, (jchar*)dst);
  }
  else {
    int i;
    jchar* buf = (jchar *)alloca(len * sizeof(jchar));
    (*env)->GetCharArrayRegion(env, chars, 0, len, buf);
    for (i=0;i < len;i++) {
      dst[i] = (wchar_t)buf[i];
    }
  }
  PEND();
}

static void
setChars(JNIEnv* env, wchar_t* src, jcharArray chars, jint off, jint len) {
  jchar* buf = (jchar*)src;
  PSTART();

  if (sizeof(jchar) != sizeof(wchar_t)) {
    int i;
    buf = (jchar *)alloca(len * sizeof(jchar));
    for (i=0;i < len;i++) {
      buf[i] = (jchar)src[i];
    }
  }
  (*env)->SetCharArrayRegion(env, chars, 0, len, buf);
  PEND();
}

/*
 * Class:     Function
 * Method:    invokePointer
 * Signature: (I[Ljava/lang/Object;)LPointer;
 */
JNIEXPORT jobject JNICALL 
Java_com_sun_jna_Function_invokePointer(JNIEnv *env, jobject self, 
                                        jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_pointer, &result);
    return newJavaPointer(env, result.l);
}


/*
 * Class:     Function
 * Method:    invokeObject
 * Signature: (I[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL 
Java_com_sun_jna_Function_invokeObject(JNIEnv *env, jobject self, 
                                       jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_pointer, &result);
    return result.l;
}


/*
 * Class:     Function
 * Method:    invokeStructure
 * Signature: (I[Ljava/lang/Object;Lcom/sun/jna/Structure)LStructure;
 */
JNIEXPORT jobject JNICALL 
Java_com_sun_jna_Function_invokeStructure(JNIEnv *env, jobject self, 
                                          jint callconv, jobjectArray arr,
                                          jobject result)
{
  void* memory = getStructureAddress(env, result);
  ffi_type* rtype = getStructureType(env, result);
  if (!rtype) {
    throwByName(env, EIllegalState, "Return structure type info not initialized");
  }
  else {
    dispatch(env, self, callconv, arr, rtype, memory);
  }
  return result;
}

/*
 * Class:     Function
 * Method:    invokeDouble
 * Signature: (I[Ljava/lang/Object;)D
 */
JNIEXPORT jdouble JNICALL 
Java_com_sun_jna_Function_invokeDouble(JNIEnv *env, jobject self, 
                                       jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_double, &result);
    return result.d;
}

/*
 * Class:     Function
 * Method:    invokeFloat
 * Signature: (I[Ljava/lang/Object;)F
 */
JNIEXPORT jfloat JNICALL
Java_com_sun_jna_Function_invokeFloat(JNIEnv *env, jobject self, 
                                      jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_float, &result);
    return result.f;
}

/*
 * Class:     Function
 * Method:    invokeInt
 * Signature: (I[Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL
Java_com_sun_jna_Function_invokeInt(JNIEnv *env, jobject self, 
                                    jint callconv, jobjectArray arr)
{
    ffi_arg result;
    dispatch(env, self, callconv, arr, &ffi_type_sint32, &result);
    return (jint)result;
}

/*
 * Class:     Function
 * Method:    invokeLong
 * Signature: (I[Ljava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL
Java_com_sun_jna_Function_invokeLong(JNIEnv *env, jobject self, 
                                     jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_sint64, &result);
    return result.j;
}

/*
 * Class:     Function
 * Method:    invokeVoid
 * Signature: (I[Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL
Java_com_sun_jna_Function_invokeVoid(JNIEnv *env, jobject self, 
                                     jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_void, &result);
}

JNIEXPORT jobject JNICALL
Java_com_sun_jna_CallbackReference_createNativeCallback(JNIEnv *env,
                                                        jclass clazz,
                                                        jobject obj,
                                                        jobject method,
                                                        jobjectArray param_types,
                                                        jclass return_type,
                                                        jint call_conv,
                                                        jboolean direct) {
  callback* cb =
    create_callback(env, obj, method, param_types, return_type, call_conv, direct);
  return cb == NULL ? NULL : newJavaPointer(env, cb);
}

JNIEXPORT void JNICALL
Java_com_sun_jna_CallbackReference_freeNativeCallback(JNIEnv *env,
                                                      jclass clazz,
                                                      jlong ptr) {
  free_callback(env, (callback*)L2A(ptr));
}

/*
 * Class:     com_sun_jna_NativeLibrary
 * Method:    open
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL
Java_com_sun_jna_NativeLibrary_open(JNIEnv *env, jclass cls, jstring lib){
    void *handle = NULL;
    const LIBNAMETYPE libname = NULL;

    /* dlopen on Unix allows NULL to mean "current process" */
    if (lib != NULL) {
      if ((libname = LIBNAME2CSTR(env, lib)) == NULL) {
        return A2L(NULL);
      }
    }

    handle = (void *)LOAD_LIBRARY(libname);
    if (!handle) {
      char buf[1024];
      throwByName(env, EUnsatisfiedLink, LOAD_ERROR(buf, sizeof(buf)));
    }
    if (libname != NULL)
      free((void *)libname);
    return A2L(handle);
}

/*
 * Class:     com_sun_jna_NativeLibrary
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_sun_jna_NativeLibrary_close(JNIEnv *env, jclass cls, jlong handle)
{
  if (FREE_LIBRARY(L2A(handle))) {
    char buf[1024];
    throwByName(env, EError, LOAD_ERROR(buf, sizeof(buf)));
  }
}

/*
 * Class:     com_sun_jna_NativeLibrary
 * Method:    findSymbol
 * Signature: (JLjava/lang/String;)J
 */
JNIEXPORT jlong JNICALL
Java_com_sun_jna_NativeLibrary_findSymbol(JNIEnv *env, jclass cls,
                                          jlong libHandle, jstring fun) {

    void *handle = L2A(libHandle);
    void *func = NULL;
    const char *funname = NULL;

    if ((funname = newCString(env, fun)) != NULL) {
#ifdef _WIN32
      if (handle == GetModuleHandleW(NULL)) {
        HANDLE cur_proc = GetCurrentProcess ();
        HMODULE *modules;
        DWORD needed, i;
        if (!EnumProcessModules (cur_proc, NULL, 0, &needed)) {
        fail:
          throwByName(env, EError, "Unexpected error enumerating modules");
          free((void *)funname);
          return 0;
        }
        modules = (HMODULE*) alloca (needed);
        if (!EnumProcessModules (cur_proc, modules, needed, &needed)) {
          goto fail;
        }
        for (i = 0; i < needed / sizeof (HMODULE); i++)
          if ((func = (void *) GetProcAddress (modules[i], funname)))
            break;
      }
      else
#endif
        func = (void *)FIND_ENTRY(handle, funname);
      if (!func) {
        char buf[1024];
        throwByName(env, EUnsatisfiedLink, LOAD_ERROR(buf, sizeof(buf)));
      }
      free((void *)funname);
    }
    return A2L(func);
}

static const void*
get_system_property(JNIEnv* env, const char* name, jboolean wide) {
  jclass classSystem = (*env)->FindClass(env, "java/lang/System");
  if (classSystem != NULL) {
    jmethodID mid = (*env)->GetStaticMethodID(env, classSystem, "getProperty",
                                              "(Ljava/lang/String;)Ljava/lang/String;");
    if (mid != NULL) {
      jstring propname = newJavaString(env, name, JNI_FALSE);
      jstring value = (*env)->CallStaticObjectMethod(env, classSystem,
                                                     mid, propname);
      if (value) {
        if (wide) 
          return newWideCString(env, value);
        return newCStringUTF8(env, value);
      }
    }
  }
  return NULL;
}

static const char*
jnidispatch_init(JNIEnv* env) {
  if (!LOAD_CREF(env, Object, "java/lang/Object")) return "java.lang.Object";
  if (!LOAD_CREF(env, Class, "java/lang/Class")) return "java.lang.Class";
  if (!LOAD_CREF(env, Method, "java/lang/reflect/Method")) return "java.lang.reflect.Method";
  if (!LOAD_CREF(env, String, "java/lang/String")) return "java.lang.String";
  if (!LOAD_CREF(env, Buffer, "java/nio/Buffer")) return "java.nio.Buffer";
  if (!LOAD_CREF(env, ByteBuffer, "java/nio/ByteBuffer")) return "java.nio.ByteBuffer";
  if (!LOAD_CREF(env, CharBuffer, "java/nio/CharBuffer")) return "java.nio.CharBuffer";
  if (!LOAD_CREF(env, ShortBuffer, "java/nio/ShortBuffer")) return "java.nio.ShortBuffer";
  if (!LOAD_CREF(env, IntBuffer, "java/nio/IntBuffer")) return "java.nio.IntBuffer";
  if (!LOAD_CREF(env, LongBuffer, "java/nio/LongBuffer")) return "java.nio.LongBuffer";
  if (!LOAD_CREF(env, FloatBuffer, "java/nio/FloatBuffer")) return "java.nio.FloatBuffer";
  if (!LOAD_CREF(env, DoubleBuffer, "java/nio/DoubleBuffer")) return "java.nio.DoubleBuffer";
  
  if (!LOAD_PCREF(env, Void, "java/lang/Void")) return "java.lang.Void";
  if (!LOAD_PCREF(env, Boolean, "java/lang/Boolean")) return "java.lang.Boolean";
  if (!LOAD_PCREF(env, Byte, "java/lang/Byte")) return "java.lang.Byte";
  if (!LOAD_PCREF(env, Character, "java/lang/Character")) return "java.lang.Character";
  if (!LOAD_PCREF(env, Short, "java/lang/Short")) return "java.lang.Short";
  if (!LOAD_PCREF(env, Integer, "java/lang/Integer")) return "java.lang.Integer";
  if (!LOAD_PCREF(env, Long, "java/lang/Long")) return "java.lang.Long";
  if (!LOAD_PCREF(env, Float, "java/lang/Float")) return "java.lang.Float";
  if (!LOAD_PCREF(env, Double, "java/lang/Double")) return "java.lang.Double";
  
  if (!LOAD_MID(env, MID_Long_init, classLong,
                "<init>", "(J)V"))
    return "java.lang.Long<init>(J)V";
  if (!LOAD_MID(env, MID_Integer_init, classInteger,
                "<init>", "(I)V"))
    return "java.lang.Integer<init>(I)V";
  if (!LOAD_MID(env, MID_Short_init, classShort,
                "<init>", "(S)V"))
    return "java.lang.Short<init>(S)V";
  if (!LOAD_MID(env, MID_Character_init, classCharacter,
                "<init>", "(C)V"))
    return "java.lang.Character<init>(C)V";
  if (!LOAD_MID(env, MID_Byte_init, classByte,
                "<init>", "(B)V"))
    return "java.lang.Byte<init>(B)V";
  if (!LOAD_MID(env, MID_Boolean_init, classBoolean,
                "<init>", "(Z)V"))
    return "java.lang.Boolean<init>(Z)V";
  if (!LOAD_MID(env, MID_Float_init, classFloat,
                "<init>", "(F)V"))
    return "java.lang.Float<init>(F)V";
  if (!LOAD_MID(env, MID_Double_init, classDouble,
                "<init>", "(D)V"))
    return "java.lang.Double<init>(D)V";
  if (!LOAD_MID(env, MID_Class_getComponentType, classClass,
                "getComponentType", "()Ljava/lang/Class;"))
    return "Class.getComponentType()";
  if (!LOAD_MID(env, MID_Object_toString, classObject,
                "toString", "()Ljava/lang/String;"))
    return "Object.toString()";
  if (!LOAD_MID(env, MID_String_getBytes, classString,
                "getBytes", "()[B"))
    return "String.getBytes()";
  if (!LOAD_MID(env, MID_String_getBytes2, classString,
                "getBytes", "(Ljava/lang/String;)[B"))
    return "String.getBytes(String)";
  if (!LOAD_MID(env, MID_String_toCharArray, classString,
                "toCharArray", "()[C"))
    return "String.toCharArray()";
  if (!LOAD_MID(env, MID_String_init_bytes, classString,
                "<init>", "([B)V"))
    return "String<init>([B)V";
  if (!LOAD_MID(env, MID_Method_getParameterTypes, classMethod,
                "getParameterTypes", "()[Ljava/lang/Class;"))
    return "Method.getParameterTypes()";
  if (!LOAD_MID(env, MID_Method_getReturnType, classMethod,
                "getReturnType", "()Ljava/lang/Class;"))
    return "Method.getReturnType()";
  
  if (!LOAD_MID(env, MID_ByteBuffer_array, classByteBuffer, "array", "()[B"))
    return "ByteBuffer.array";
  if (!LOAD_MID(env, MID_ByteBuffer_arrayOffset, classByteBuffer, "arrayOffset", "()I"))
    return "ByteBuffer.arrayOffset";
  if (!LOAD_MID(env, MID_CharBuffer_array, classCharBuffer, "array", "()[C"))
    return "CharBuffer.array";
  if (!LOAD_MID(env, MID_CharBuffer_arrayOffset, classCharBuffer, "arrayOffset", "()I"))
    return "CharBuffer.arrayOffset";
  if (!LOAD_MID(env, MID_ShortBuffer_array, classShortBuffer, "array", "()[S"))
    return "ShortBuffer.array";
  if (!LOAD_MID(env, MID_ShortBuffer_arrayOffset, classShortBuffer, "arrayOffset", "()I"))
    return "ShortBuffer.arrayOffset";
  if (!LOAD_MID(env, MID_IntBuffer_array, classIntBuffer, "array", "()[I"))
    return "IntBuffer.array";
  if (!LOAD_MID(env, MID_IntBuffer_arrayOffset, classIntBuffer, "arrayOffset", "()I"))
    return "IntBuffer.arrayOffset";
  if (!LOAD_MID(env, MID_LongBuffer_array, classLongBuffer, "array", "()[J"))
    return "LongBuffer.array";
  if (!LOAD_MID(env, MID_LongBuffer_arrayOffset, classLongBuffer, "arrayOffset", "()I"))
    return "LongBuffer.arrayOffset";
  if (!LOAD_MID(env, MID_FloatBuffer_array, classFloatBuffer, "array", "()[F"))
    return "FloatBuffer.array";
  if (!LOAD_MID(env, MID_FloatBuffer_arrayOffset, classFloatBuffer, "arrayOffset", "()I"))
    return "FloatBuffer.arrayOffset";
  if (!LOAD_MID(env, MID_DoubleBuffer_array, classDoubleBuffer, "array", "()[D"))
    return "DoubleBuffer.array";
  if (!LOAD_MID(env, MID_DoubleBuffer_arrayOffset, classDoubleBuffer, "arrayOffset", "()I"))
    return "DoubleBuffer.arrayOffset";


  if (!LOAD_FID(env, FID_Boolean_value, classBoolean, "value", "Z"))
    return "Boolean.value";
  if (!LOAD_FID(env, FID_Byte_value, classByte, "value", "B"))
    return "Byte.value";
  if (!LOAD_FID(env, FID_Short_value, classShort, "value", "S"))
    return "Short.value";
  if (!LOAD_FID(env, FID_Character_value, classCharacter, "value", "C"))
    return "Character.value";
  if (!LOAD_FID(env, FID_Integer_value, classInteger, "value", "I"))
    return "Integer.value";
  if (!LOAD_FID(env, FID_Long_value, classLong, "value", "J"))
    return "Long.value";
  if (!LOAD_FID(env, FID_Float_value, classFloat, "value", "F"))
    return "Float.value";
  if (!LOAD_FID(env, FID_Double_value, classDouble, "value", "D"))
    return "Double.value";

  // Cache jna.encoding value
  jna_encoding = get_system_property(env, "jna.encoding", JNI_FALSE);

  return NULL;
}

/*
 * Class:     Pointer
 * Method:    _write
 * Signature: (J[BII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1write__J_3BII
    (JNIEnv *env, jclass cls, jlong addr, jbyteArray arr, jint off, jint n)
{
  PSTART();
  (*env)->GetByteArrayRegion(env, arr, off, n, L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _write
 * Signature: (J[CII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1write__J_3CII
    (JNIEnv *env, jclass cls, jlong addr, jcharArray arr, jint off, jint n)
{
  getChars(env, (wchar_t*)L2A(addr), arr, off, n);
}

/*
 * Class:     Pointer
 * Method:    _write
 * Signature: (J[DII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1write__J_3DII
    (JNIEnv *env, jclass cls, jlong addr, jdoubleArray arr, jint off, jint n)
{
  PSTART();
  (*env)->GetDoubleArrayRegion(env, arr, off, n, (jdouble*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _write
 * Signature: (J[FII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1write__J_3FII
    (JNIEnv *env, jclass cls, jlong addr, jfloatArray arr, jint off, jint n)
{
  PSTART();
  (*env)->GetFloatArrayRegion(env, arr, off, n, (jfloat*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _write
 * Signature: (J[III)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1write__J_3III
    (JNIEnv *env, jclass cls, jlong addr, jintArray arr, jint off, jint n)
{
  PSTART();
  (*env)->GetIntArrayRegion(env, arr, off, n, (jint*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _write
 * Signature: (J[JII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1write__J_3JII
    (JNIEnv *env, jclass cls, jlong addr, jlongArray arr, jint off, jint n)
{
  PSTART();
  (*env)->GetLongArrayRegion(env, arr, off, n, (jlong*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _write
 * Signature: (J[SII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1write__J_3SII
    (JNIEnv *env, jclass cls, jlong addr, jshortArray arr, jint off, jint n)
{
  PSTART();
  (*env)->GetShortArrayRegion(env, arr, off, n, (jshort*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _indexOf
 * Signature: (JB)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_jna_Pointer__1indexOf__JB
    (JNIEnv *env, jclass cls, jlong addr, jbyte value)
{
  jbyte *peer = (jbyte *)L2A(addr);
  volatile jlong i = 0;
  volatile jlong result = -1L;
  PSTART();
  while (i >= 0 && result == -1L) {
    if (peer[i] == value) 
      result = i;
    ++i;
  }
  PEND();

  return result;
}

/*
 * Class:     Pointer
 * Method:    _read
 * Signature: (J[BII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1read__J_3BII
    (JNIEnv *env, jclass cls, jlong addr, jbyteArray arr, jint off, jint n)
{
  PSTART();
  (*env)->SetByteArrayRegion(env, arr, off, n, L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _read
 * Signature: (J[CII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1read__J_3CII
    (JNIEnv *env, jclass cls, jlong addr, jcharArray arr, jint off, jint n)
{
  setChars(env, (wchar_t*)L2A(addr), arr, off, n);
}

/*
 * Class:     Pointer
 * Method:    _read
 * Signature: (J[DII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1read__J_3DII
    (JNIEnv *env, jclass cls, jlong addr, jdoubleArray arr, jint off, jint n)
{
  PSTART();
  (*env)->SetDoubleArrayRegion(env, arr, off, n, (jdouble*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _read
 * Signature: (J[FII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1read__J_3FII
    (JNIEnv *env, jclass cls, jlong addr, jfloatArray arr, jint off, jint n)
{
  PSTART();
  (*env)->SetFloatArrayRegion(env, arr, off, n, (jfloat*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _read
 * Signature: (J[III)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1read__J_3III
    (JNIEnv *env, jclass cls, jlong addr, jintArray arr, jint off, jint n)
{
  PSTART();
  (*env)->SetIntArrayRegion(env, arr, off, n, (jint*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _read
 * Signature: (J[JII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1read__J_3JII
    (JNIEnv *env, jclass cls, jlong addr, jlongArray arr, jint off, jint n)
{
  PSTART();
  (*env)->SetLongArrayRegion(env, arr, off, n, (jlong*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _read
 * Signature: (J[SII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1read__J_3SII
    (JNIEnv *env, jclass cls, jlong addr, jshortArray arr, jint off, jint n)
{
  PSTART();
  (*env)->SetShortArrayRegion(env, arr, off, n, (jshort*)L2A(addr));
  PEND();
}

/*
 * Class:     Pointer
 * Method:    _getByte
 * Signature: (J)B
 */
JNIEXPORT jbyte JNICALL Java_com_sun_jna_Pointer__1getByte
    (JNIEnv *env, jclass cls, jlong addr)
{
    jbyte res = 0;
    MEMCPY(&res, L2A(addr), sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    _getChar
 * Signature: (J)C
 */
JNIEXPORT jchar JNICALL Java_com_sun_jna_Pointer__1getChar
    (JNIEnv *env, jclass cls, jlong addr)
{
    wchar_t res = 0;
    MEMCPY(&res, L2A(addr), sizeof(res));
    return (jchar)res;
}

/*
 * Class:     Pointer
 * Method:    _getPointer
 * Signature: (J)LPointer;
 */
JNIEXPORT jobject JNICALL Java_com_sun_jna_Pointer__1getPointer
    (JNIEnv *env, jclass cls, jlong addr)
{
    void *ptr = NULL;
    MEMCPY(&ptr, L2A(addr), sizeof(ptr));
    return newJavaPointer(env, ptr);
}

/*
 * Class:     com_sun_jna_Pointer
 * Method:    _getDirectByteBuffer
 * Signature: (JJ)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_sun_jna_Pointer__1getDirectByteBuffer
    (JNIEnv *env, jclass cls, jlong addr, jlong length)
{
    return (*env)->NewDirectByteBuffer(env, L2A(addr), length);
}

/*
 * Class:     Pointer
 * Method:    _getDouble
 * Signature: (J)D
 */
JNIEXPORT jdouble JNICALL Java_com_sun_jna_Pointer__1getDouble
    (JNIEnv *env, jclass cls, jlong addr)
{
    jdouble res = 0;
    MEMCPY(&res, L2A(addr), sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    _getFloat
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_com_sun_jna_Pointer__1getFloat
    (JNIEnv *env, jclass cls, jlong addr)
{
    jfloat res = 0;
    MEMCPY(&res, L2A(addr), sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    _getInt
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_sun_jna_Pointer__1getInt
    (JNIEnv *env, jclass cls, jlong addr)
{
    jint res = 0;
    MEMCPY(&res, L2A(addr), sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    _getLong
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_jna_Pointer__1getLong
    (JNIEnv *env, jclass cls, jlong addr)
{
    jlong res = 0;
    MEMCPY(&res, L2A(addr), sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    _getShort
 * Signature: (J)S
 */
JNIEXPORT jshort JNICALL Java_com_sun_jna_Pointer__1getShort
    (JNIEnv *env, jclass cls, jlong addr)
{
    jshort res = 0;
    MEMCPY(&res, L2A(addr), sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    _getString
 * Signature: (JB)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_sun_jna_Pointer__1getString
    (JNIEnv *env, jclass cls, jlong addr, jboolean wide)
{
  return newJavaString(env, L2A(addr), wide);
}

/*
 * Class:     Pointer
 * Method:    _setMemory
 * Signature: (JJB)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setMemory
    (JNIEnv *env, jclass cls, jlong addr, jlong count, jbyte value)
{
  MEMSET(L2A(addr), (int)value, (size_t)count);
}

/*
 * Class:     Pointer
 * Method:    _setByte
 * Signature: (JB)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setByte
    (JNIEnv *env, jclass cls, jlong addr, jbyte value)
{
  MEMCPY(L2A(addr), &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    _setChar
 * Signature: (JC)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setChar
    (JNIEnv *env, jclass cls, jlong addr, jchar value)
{
  wchar_t ch = value;
  MEMCPY(L2A(addr), &ch, sizeof(ch));
}

/*
 * Class:     Pointer
 * Method:    _setPointer
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setPointer
    (JNIEnv *env, jclass cls, jlong addr, jlong value)
{
  void *ptr = L2A(value);
  MEMCPY(L2A(addr), &ptr, sizeof(void *));
}

/*
 * Class:     Pointer
 * Method:    _setDouble
 * Signature: (JD)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setDouble
    (JNIEnv *env, jclass cls, jlong addr, jdouble value)
{
  MEMCPY(L2A(addr), &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    _setFloat
 * Signature: (JF)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setFloat
    (JNIEnv *env, jclass cls, jlong addr, jfloat value)
{
  MEMCPY(L2A(addr), &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    _setInt
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setInt
    (JNIEnv *env, jclass cls, jlong addr, jint value)
{
  MEMCPY(L2A(addr), &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    _setLong
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setLong
    (JNIEnv *env, jclass cls, jlong addr, jlong value)
{
  MEMCPY(L2A(addr), &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    _setShort
 * Signature: (JS)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setShort
    (JNIEnv *env, jclass cls, jlong addr, jshort value)
{
  MEMCPY(L2A(addr), &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    _setString
 * Signature: (JLjava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer__1setString
    (JNIEnv *env, jclass cls, jlong addr, jstring value, jboolean wide)
{
    int len = (*env)->GetStringLength(env, value);
    const void* volatile str;
    volatile int size = len + 1;

    if (wide) {
      size *= sizeof(wchar_t);
      str = newWideCString(env, value);
    }
    else {
      str = newCStringEncoding(env, value, jna_encoding);
    }
    if (str != NULL) {
      MEMCPY(L2A(addr), str, size);
      free((void*)str);
    }
}


/*
 * Class:     Memory
 * Method:    malloc
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_jna_Memory_malloc
    (JNIEnv *env, jclass cls, jlong size)
{
    return A2L(malloc((size_t)size));
}

/*
 * Class:     Memory
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Memory_free
    (JNIEnv *env, jclass cls, jlong ptr)
{
    free(L2A(ptr));
}


//*******************************************************************
//                         Utility functions                        
//*******************************************************************

/* Throw an exception by name */
void 
throwByName(JNIEnv *env, const char *name, const char *msg)
{
  jclass cls;

  (*env)->ExceptionClear(env);
  
  cls = (*env)->FindClass(env, name);
  
  if (cls != NULL) { /* Otherwise an exception has already been thrown */
    (*env)->ThrowNew(env, cls, msg);
    
    /* It's a good practice to clean up the local references. */
    (*env)->DeleteLocalRef(env, cls);
  }
}

/* Translates a Java string to a C string using the String.getBytes 
 * method, which uses default platform encoding.
 */
static char *
newCString(JNIEnv *env, jstring jstr)
{
    jbyteArray bytes = 0;
    char *result = NULL;

    bytes = (*env)->CallObjectMethod(env, jstr, MID_String_getBytes);
    if (!(*env)->ExceptionCheck(env)) {
        jint len = (*env)->GetArrayLength(env, bytes);
        result = (char *)malloc(len + 1);
        if (result == NULL) {
            (*env)->DeleteLocalRef(env, bytes);
            throwByName(env, EOutOfMemory, "Can't allocate C string");
            return NULL;
        }
        (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)result);
        result[len] = 0; /* NUL-terminate */
    }
    (*env)->DeleteLocalRef(env, bytes);
    return result;
}

/* Translates a Java string to a C string using the String.getBytes("UTF8") 
 * method, which uses UTF8 encoding.
 */
static char *
newCStringUTF8(JNIEnv *env, jstring jstr)
{
  return newCStringEncoding(env, jstr, "UTF8");
}

static char*
newCStringEncoding(JNIEnv *env, jstring jstr, const char* encoding)
{
    jbyteArray bytes = 0;
    char *result = NULL;

    if (!encoding) return newCString(env, jstr);

    bytes = (*env)->CallObjectMethod(env, jstr, MID_String_getBytes2,
                                     newJavaString(env, encoding, JNI_FALSE));
    if (!(*env)->ExceptionCheck(env)) {
        jint len = (*env)->GetArrayLength(env, bytes);
        result = (char *)malloc(len + 1);
        if (result == NULL) {
            (*env)->DeleteLocalRef(env, bytes);
            throwByName(env, EOutOfMemory, "Can't allocate C string");
            return NULL;
        }
        (*env)->GetByteArrayRegion(env, bytes, 0, len, (jbyte *)result);
        result[len] = 0; /* NUL-terminate */
    }
    (*env)->DeleteLocalRef(env, bytes);
    return result;
}

/* Translates a Java string to a wide C string using the String.toCharArray
 * method.
 */
// TODO: are any encoding changes required?
static wchar_t *
newWideCString(JNIEnv *env, jstring str)
{
    jcharArray chars = 0;
    wchar_t *result = NULL;

    chars = (*env)->CallObjectMethod(env, str, MID_String_toCharArray);
    if (!(*env)->ExceptionCheck(env)) {
        jint len = (*env)->GetArrayLength(env, chars);
        result = (wchar_t *)malloc(sizeof(wchar_t) * (len + 1));
        if (result == NULL) {
            (*env)->DeleteLocalRef(env, chars);
            throwByName(env, EOutOfMemory, "Can't allocate wide C string");
            return NULL;
        }
        // TODO: ensure proper encoding conversion from jchar to native wchar_t
        getChars(env, result, chars, 0, len);
        result[len] = 0; /* NUL-terminate */
    }
    (*env)->DeleteLocalRef(env, chars);
    return result;
}

/** Update the per-thread last error setting. */
static void
update_last_error(JNIEnv* env, int err) {
  (*env)->CallStaticVoidMethod(env, classNative,
                               MID_Native_updateLastError, err);
}

jobject
newJavaWString(JNIEnv *env, const wchar_t* ptr) {
  jstring s = newJavaString(env, (const char*)ptr, JNI_TRUE);
  return (*env)->NewObject(env, classWString, MID_WString_init, s);
}

/* Constructs a Java string from a char array (using the String(byte [])
 * constructor, which uses default local encoding) or a short array (using the
 * String(char[]) ctor, which uses the character values unmodified).  
 */
jstring
newJavaString(JNIEnv *env, const char *ptr, jboolean wide) 
{
    volatile jstring result = 0;
    PSTART();

    if (ptr) {
      if (wide) {
        // TODO: proper conversion from native wchar_t to jchar, if any
        int len = (int)wcslen((const wchar_t*)ptr);
        if (sizeof(jchar) != sizeof(wchar_t)) {
          jchar* buf = (jchar*)alloca(len * sizeof(jchar));
          int i;
          for (i=0;i < len;i++) {
            buf[i] =  *((const wchar_t*)ptr + i);
          }
          result = (*env)->NewString(env, buf, len);
        }
        else {
          result = (*env)->NewString(env, (const jchar*)ptr, len);
        }
      }
      else {
        jbyteArray bytes = 0;
        int len = (int)strlen(ptr);
        
        bytes = (*env)->NewByteArray(env, len);
        if (bytes != 0) {
          (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte *)ptr);
          result = (*env)->NewObject(env, classString,
                                     MID_String_init_bytes, bytes);
          (*env)->DeleteLocalRef(env, bytes);
        }
      }
    }
    PEND();

    return result;
}

jobject 
newJavaPointer(JNIEnv *env, void *p)
{
    jobject obj = NULL;
    if (p != NULL) {
      obj = (*env)->NewObject(env, classPointer, MID_Pointer_init, A2L(p));
    }
    return obj;
}

jobject
newJavaStructure(JNIEnv *env, void *data, jclass type, jboolean new_memory) 
{
  if (data != NULL) {
    volatile jobject obj = (*env)->CallStaticObjectMethod(env, classStructure, MID_Structure_newInstance, type);
    ffi_type* rtype = getStructureType(env, obj);
    if (new_memory) {
      MEMCPY(getStructureAddress(env, obj), data, rtype->size);
    }
    else {
      (*env)->CallVoidMethod(env, obj, MID_Structure_useMemory, newJavaPointer(env, data));
    }
    (*env)->CallVoidMethod(env, obj, MID_Structure_read);
    return obj;
  }
  return NULL;
}

jobject
newJavaCallback(JNIEnv* env, void* fptr, jclass type)
{
  if (fptr != NULL) {
    jobject ptr = newJavaPointer(env, fptr);
    return (*env)->CallStaticObjectMethod(env, classCallbackReference,
                                          MID_CallbackReference_getCallback,
                                          type, ptr, JNI_TRUE);
  }
  return NULL;
}

void*
getNativeString(JNIEnv* env, jstring s, jboolean wide) {
  if (s != NULL) {
    jobject ptr = (*env)->CallStaticObjectMethod(env, classCallbackReference,
                                                 MID_CallbackReference_getNativeString,
                                                 s, wide);
    return getNativeAddress(env, ptr);
  }
  return NULL;
}

int
get_conversion_flag(JNIEnv* env, jclass cls) {
  int type = get_jtype(env, cls);
  if (type == 's') {
    return CVT_STRUCTURE_BYVAL;
  }
  if (type == '*') {
    if ((*env)->IsAssignableFrom(env, cls, classPointer)) {
      return CVT_POINTER;
    }
    if ((*env)->IsAssignableFrom(env, cls, classStructure)) {
      return CVT_STRUCTURE;
    }    
    if ((*env)->IsAssignableFrom(env, cls, classString)) {
      return CVT_STRING;
    }
    if ((*env)->IsAssignableFrom(env, cls, classWString)) {
      return CVT_WSTRING;
    }
    if ((*env)->IsAssignableFrom(env, cls, classCallback)) {
      return CVT_CALLBACK;
    }
    if ((*env)->IsAssignableFrom(env, cls, classIntegerType)) {
      return CVT_INTEGER_TYPE;
    }
    if ((*env)->IsAssignableFrom(env, cls, classPointerType)) {
      return CVT_POINTER_TYPE;
    }
    if ((*env)->IsAssignableFrom(env, cls, classNativeMapped)) {
      return CVT_NATIVE_MAPPED;
    }
  }
  return CVT_DEFAULT;
}

int
get_jtype_from_ffi_type(ffi_type* type) {
  switch(type->type) {
    // FIXME aliases 'C' on *nix; this will cause problems if anyone
    // ever installs a type mapper for char/Character (not a common arg type)
  case FFI_TYPE_UINT32: return 'Z'; 
  case FFI_TYPE_SINT8: return 'B';
  case FFI_TYPE_SINT16: return 'S';
  case FFI_TYPE_UINT16: return 'C';
  case FFI_TYPE_SINT32: return 'I';
  case FFI_TYPE_SINT64: return 'J';
  case FFI_TYPE_FLOAT: return 'F';
  case FFI_TYPE_DOUBLE: return 'D';
  default: return '*';
  }
}

int
get_jtype(JNIEnv* env, jclass cls) {

  if ((*env)->IsSameObject(env, classVoid, cls)
      || (*env)->IsSameObject(env, classPrimitiveVoid, cls))
    return 'V';
  if ((*env)->IsSameObject(env, classBoolean, cls)
      || (*env)->IsSameObject(env, classPrimitiveBoolean, cls))
    return 'Z';
  if ((*env)->IsSameObject(env, classByte, cls)
      || (*env)->IsSameObject(env, classPrimitiveByte, cls))
    return 'B';
  if ((*env)->IsSameObject(env, classCharacter, cls)
      || (*env)->IsSameObject(env, classPrimitiveCharacter, cls))
    return 'C';
  if ((*env)->IsSameObject(env,classShort, cls)
      || (*env)->IsSameObject(env, classPrimitiveShort, cls))
    return 'S';
  if ((*env)->IsSameObject(env, classInteger, cls)
      || (*env)->IsSameObject(env, classPrimitiveInteger, cls))
    return 'I';
  if ((*env)->IsSameObject(env, classLong, cls)
      || (*env)->IsSameObject(env, classPrimitiveLong, cls))
    return 'J';
  if ((*env)->IsSameObject(env, classFloat, cls)
      || (*env)->IsSameObject(env, classPrimitiveFloat, cls))
    return 'F';
  if ((*env)->IsSameObject(env, classDouble, cls)
      || (*env)->IsSameObject(env, classPrimitiveDouble, cls))
    return 'D';
  if ((*env)->IsAssignableFrom(env, cls, classStructure)) {
    if ((*env)->IsAssignableFrom(env, cls, classStructureByValue))
      return 's';
    return '*';
  }
  if ((*env)->IsAssignableFrom(env, cls, classPointer)
      || (*env)->IsAssignableFrom(env, cls, classCallback)
      || (*env)->IsAssignableFrom(env, cls, classNativeMapped)
      || (*env)->IsAssignableFrom(env, cls, classWString)
      || (*env)->IsAssignableFrom(env, cls, classString))
    return '*';
  return -1;
}

jlong
getIntegerTypeValue(JNIEnv* env, jobject obj) {
  return (*env)->GetLongField(env, obj, FID_IntegerType_value);
}

void*
getPointerTypeAddress(JNIEnv* env, jobject obj) {
  return getNativeAddress(env, (*env)->GetObjectField(env, obj, FID_PointerType_pointer));
}

void *
getStructureAddress(JNIEnv *env, jobject obj) {
  if (obj != NULL) {
    jobject ptr = (*env)->GetObjectField(env, obj, FID_Structure_memory);
    return getNativeAddress(env, ptr);
  }
  return NULL;
}

void
writeStructure(JNIEnv *env, jobject s) {
  if (s != NULL) {
    (*env)->CallVoidMethod(env, s, MID_Structure_write);
  }
}

void *
getCallbackAddress(JNIEnv *env, jobject obj) {
  if (obj != NULL) {
    jobject ptr = (*env)->CallStaticObjectMethod(env, classCallbackReference, MID_CallbackReference_getFunctionPointer, obj, JNI_TRUE);
    return getNativeAddress(env, ptr);
  }
  return NULL;
}

jclass
getNativeType(JNIEnv* env, jclass cls) {
  return (*env)->CallStaticObjectMethod(env, classNative,
                                        MID_Native_nativeType, cls);
}

void*
getFFITypeTypeMapped(JNIEnv* env, jobject converter) {
  return L2A((*env)->CallStaticLongMethod(env, converter,
                                          MID_ToNativeConverter_nativeType));
}

void
toNative(JNIEnv* env, jobject obj, void* valuep, size_t size, jboolean promote) {
  if (obj != NULL) {
    jobject arg = (*env)->CallObjectMethod(env, obj, MID_NativeMapped_toNative);
    extract_value(env, arg, valuep, size, promote);
  }
  else {
    MEMSET(valuep, 0, size);
  }
}

static void
toNativeTypeMapped(JNIEnv* env, jobject obj, void* valuep, size_t size, jobject to_native) {
  if (obj != NULL) {
    jobject arg = (*env)->CallStaticObjectMethod(env, classNative, MID_Native_toNativeTypeMapped, to_native, obj);
    extract_value(env, arg, valuep, size, JNI_FALSE);
  }
  else {
    MEMSET(valuep, 0, size);
  }
}

static void
fromNativeTypeMapped(JNIEnv* env, jobject from_native, void* resp, ffi_type* type, jclass javaClass, void* result) {
  int jtype = get_jtype_from_ffi_type(type);
  jobject value = new_object(env, (char)jtype, resp, JNI_TRUE);
  jobject obj = (*env)->CallStaticObjectMethod(env, classNative,
                                               MID_Native_fromNativeTypeMapped,
                                               from_native, value, javaClass);
  // Must extract primitive types
  if (type->type != FFI_TYPE_POINTER) {
    extract_value(env, obj, result, type->size, JNI_TRUE);
  }
}

jobject
fromNative(JNIEnv* env, jclass javaClass, ffi_type* type, void* resp, jboolean promote) {
  int jtype = get_jtype_from_ffi_type(type);
  jobject value = new_object(env, (char)jtype, resp, promote);
  return (*env)->CallStaticObjectMethod(env, classNative,
                                        MID_Native_fromNative,
                                        javaClass, value);
}


static ffi_type*
getStructureType(JNIEnv *env, jobject obj) {
  jlong typeInfo = (*env)->GetLongField(env, obj, FID_Structure_typeInfo);
  if (!typeInfo) {
    (*env)->CallObjectMethod(env, obj, MID_Structure_getTypeInfo);
    typeInfo = (*env)->GetLongField(env, obj, FID_Structure_typeInfo);
  }
  return (ffi_type*)L2A(typeInfo);
}

void *
getNativeAddress(JNIEnv *env, jobject obj) {
  if (obj != NULL)
    return L2A((*env)->GetLongField(env, obj, FID_Pointer_peer));
  return NULL;
}

static char
getArrayComponentType(JNIEnv *env, jobject obj) {
  jclass cls = (*env)->GetObjectClass(env, obj);
  jclass type = (*env)->CallObjectMethod(env, cls, MID_Class_getComponentType);
  if (type != NULL) {
    return (char)get_jtype(env, type);
  }
  return 0;
}


static void*
getBufferArray(JNIEnv* env, jobject buf,
               jobject* arrayp, void **elemp,
               void **releasep) {
  void *ptr = NULL;
  int offset = 0;
  jobject array = NULL;

#define GET_ARRAY(TYPE, ELEM_SIZE) \
do { \
  array = (*env)->CallObjectMethod(env, buf, MID_##TYPE##Buffer_array); \
  if (array != NULL) { \
    offset = \
       (*env)->CallIntMethod(env, buf, MID_##TYPE##Buffer_arrayOffset) \
       * ELEM_SIZE; \
    ptr = (*env)->Get##TYPE##ArrayElements(env, array, NULL); \
    if (releasep) *releasep = (void*)(*env)->Release##TYPE##ArrayElements; \
  } \
  else if (releasep) *releasep = NULL; \
} while(0)

  if ((*env)->IsInstanceOf(env, buf, classByteBuffer)) {
    GET_ARRAY(Byte, 1);
  }
  else if((*env)->IsInstanceOf(env, buf, classCharBuffer)) {
    GET_ARRAY(Char, 2);
  }
  else if((*env)->IsInstanceOf(env, buf, classShortBuffer)) {
    GET_ARRAY(Short, 2);
  }
  else if((*env)->IsInstanceOf(env, buf, classIntBuffer)) {
    GET_ARRAY(Int, 4);
  }
  else if((*env)->IsInstanceOf(env, buf, classLongBuffer)) {
    GET_ARRAY(Long, 8);
  }
  else if((*env)->IsInstanceOf(env, buf, classFloatBuffer)) {
    GET_ARRAY(Float, 4);
  }
  else if((*env)->IsInstanceOf(env, buf, classDoubleBuffer)) {
    GET_ARRAY(Double, 8);
  }
  if (ptr != NULL) {
    if (elemp) *elemp = ptr;
    if (arrayp) *arrayp = array;
    ptr = (char *)ptr + offset;
  }

  return ptr;
}


/*
 * Class:     Native
 * Method:    sizeof
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL 
Java_com_sun_jna_Native_sizeof(JNIEnv *env, jclass cls, jint type)
{
  switch(type) {
  case com_sun_jna_Native_TYPE_VOIDP: return sizeof(void*);
  case com_sun_jna_Native_TYPE_LONG: return sizeof(long);
  case com_sun_jna_Native_TYPE_WCHAR_T: return sizeof(wchar_t);
  case com_sun_jna_Native_TYPE_SIZE_T: return sizeof(size_t);
  default:
    {
      char msg[1024];
      snprintf(msg, sizeof(msg), "Invalid sizeof type %d", (int)type);
      throwByName(env, EIllegalArgument, msg);
      return -1;
    }
  }
}

/** Initialize com.sun.jna classes separately from the library load to
 * avoid initialization inconsistencies.
 */
JNIEXPORT void JNICALL 
Java_com_sun_jna_Native_initIDs(JNIEnv *env, jclass cls) {
  preserve_last_error = JNI_TRUE;
  if (!LOAD_CREF(env, Pointer, "com/sun/jna/Pointer")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.Pointer");
  }
  else if (!LOAD_MID(env, MID_Pointer_init, classPointer,
                     "<init>", "(J)V")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain constructor for class com.sun.jna.Pointer");
  }
  else if (!LOAD_FID(env, FID_Pointer_peer, classPointer, "peer", "J")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain peer field ID for class com.sun.jna.Pointer");
  }
  else if (!(classNative = (*env)->NewWeakGlobalRef(env, cls))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain global reference for class com.sun.jna.Native");
  }
  else if (!(MID_Native_updateLastError
             = (*env)->GetStaticMethodID(env, classNative,
                                         "updateLastError", "(I)V"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain updateLastError method for class com.sun.jna.Native");
  }
  else if (!(MID_Native_fromNative
             = (*env)->GetStaticMethodID(env, classNative,
                                         "fromNative", "(Ljava/lang/Class;Ljava/lang/Object;)Lcom/sun/jna/NativeMapped;"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain static method fromNative from class com.sun.jna.Native");
  }
  else if (!(MID_Native_nativeType
             = (*env)->GetStaticMethodID(env, classNative,
                                         "nativeType", "(Ljava/lang/Class;)Ljava/lang/Class;"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain static method nativeType from class com.sun.jna.Native");
  }
  else if (!(MID_Native_toNativeTypeMapped
             = (*env)->GetStaticMethodID(env, classNative,
                                         "toNative", "(Lcom/sun/jna/ToNativeConverter;Ljava/lang/Object;)Ljava/lang/Object;"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain static method toNative from class com.sun.jna.Native");
  }
  else if (!(MID_Native_fromNativeTypeMapped
             = (*env)->GetStaticMethodID(env, classNative,
                                         "fromNative", "(Lcom/sun/jna/FromNativeConverter;Ljava/lang/Object;Ljava/lang/Class;)Ljava/lang/Object;"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain static method fromNative from class com.sun.jna.Native");
  }
  else if (!LOAD_CREF(env, Structure, "com/sun/jna/Structure")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.Structure");
  }
  else if (!LOAD_MID(env, MID_Structure_getTypeInfo, classStructure,
                     "getTypeInfo", "()Lcom/sun/jna/Pointer;")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain getTypeInfo method for class com.sun.jna.Structure");
  }
  else if (!(MID_Structure_newInstance
             = (*env)->GetStaticMethodID(env, classStructure,
                                         "newInstance", "(Ljava/lang/Class;)Lcom/sun/jna/Structure;"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain static newInstance method for class com.sun.jna.Structure");
  }
  else if (!LOAD_MID(env, MID_Structure_useMemory, classStructure,
                     "useMemory", "(Lcom/sun/jna/Pointer;)V")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain useMemory method for class com.sun.jna.Structure");
  }
  else if (!LOAD_MID(env, MID_Structure_read, classStructure,
                     "autoRead", "()V")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain read method for class com.sun.jna.Structure");
  }
  else if (!LOAD_MID(env, MID_Structure_write, classStructure,
                     "autoWrite", "()V")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain write method for class com.sun.jna.Structure");
  }
  else if (!LOAD_FID(env, FID_Structure_memory, classStructure, "memory", "Lcom/sun/jna/Pointer;")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain memory field ID for class com.sun.jna.Structure");
  }
  else if (!LOAD_FID(env, FID_Structure_typeInfo, classStructure, "typeInfo", "J")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain typeInfo field ID for class com.sun.jna.Structure");
  }
  else if (!LOAD_CREF(env, StructureByValue, "com/sun/jna/Structure$ByValue")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.Structure.ByValue");
  }
  else if (!LOAD_CREF(env, Callback, "com/sun/jna/Callback")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.Callback");
  }
  else if (!LOAD_CREF(env, CallbackReference, "com/sun/jna/CallbackReference")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.CallbackReference");
  }
  else if (!(MID_CallbackReference_getCallback
             = (*env)->GetStaticMethodID(env, classCallbackReference,
                                         "getCallback", "(Ljava/lang/Class;Lcom/sun/jna/Pointer;Z)Lcom/sun/jna/Callback;"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain static method getCallback from class com.sun.jna.CallbackReference");
  }
  else if (!(MID_CallbackReference_getFunctionPointer
             = (*env)->GetStaticMethodID(env, classCallbackReference,
                                         "getFunctionPointer", "(Lcom/sun/jna/Callback;Z)Lcom/sun/jna/Pointer;"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain static method getFunctionPointer from class com.sun.jna.CallbackReference");
  }
  else if (!(MID_CallbackReference_getNativeString
             = (*env)->GetStaticMethodID(env, classCallbackReference,
                                         "getNativeString", "(Ljava/lang/Object;Z)Lcom/sun/jna/Pointer;"))) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain static method getNativeString from class com.sun.jna.CallbackReference");
  }
  else if (!LOAD_CREF(env, WString, "com/sun/jna/WString")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.WString");
  }
  else if (!LOAD_CREF(env, NativeMapped, "com/sun/jna/NativeMapped")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.NativeMapped");
  }
  else if (!LOAD_MID(env, MID_NativeMapped_toNative, classNativeMapped,
                     "toNative", "()Ljava/lang/Object;")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain toNative method for class com.sun.jna.NativeMapped");
  }
  else if (!LOAD_CREF(env, IntegerType, "com/sun/jna/IntegerType")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.IntegerType");
  }
  else if (!LOAD_FID(env, FID_IntegerType_value, classIntegerType, "value", "J")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain value field ID for class com.sun.jna.IntegerType");
  }
  else if (!LOAD_CREF(env, PointerType, "com/sun/jna/PointerType")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.PointerType");
  }
  else if (!LOAD_FID(env, FID_PointerType_pointer, classPointerType, "pointer", "Lcom/sun/jna/Pointer;")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain typeInfo field ID for class com.sun.jna.Structure");
  }
  else if (!LOAD_MID(env, MID_WString_init, classWString,
                     "<init>", "(Ljava/lang/String;)V")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain constructor for class com.sun.jna.WString");
  }
  else if (!LOAD_CREF(env, _ffi_callback, "com/sun/jna/Native$ffi_callback")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain class com.sun.jna.Native$ffi_callback");
  }
  else if (!LOAD_MID(env, MID_ffi_callback_invoke, class_ffi_callback,
                     "invoke", "(JJJ)V")) {
    throwByName(env, EUnsatisfiedLink,
                "Can't obtain invoke method from class com.sun.jna.Native$ffi_callback");
  }
  // Initialize type fields within Structure.FFIType
  else {
#define CFFITYPE "com/sun/jna/Structure$FFIType$FFITypes"
    jclass cls = (*env)->FindClass(env, CFFITYPE);
    jfieldID fid;
    unsigned i;
    const char* fields[] = {
      "void", 
      "float", "double", "longdouble", 
      "uint8", "sint8", "uint16", "sint16",
      "uint32", "sint32", "uint64", "sint64",
      "pointer",
    };
    ffi_type* types[] = {
      &ffi_type_void, 
      &ffi_type_float, &ffi_type_double, &ffi_type_longdouble,
      &ffi_type_uint8, &ffi_type_sint8, &ffi_type_uint16, &ffi_type_sint16, 
      &ffi_type_uint32, &ffi_type_sint32, &ffi_type_uint64, &ffi_type_sint64, 
      &ffi_type_pointer,
    };
    char field[32];
    if (!cls) {
      throwByName(env, EUnsatisfiedLink, "Structure$FFIType missing");
      return;
    }
    for (i=0;i < sizeof(fields)/sizeof(fields[0]);i++) {
      snprintf(field, sizeof(field), "ffi_type_%s", fields[i]);
      fid = (*env)->GetStaticFieldID(env, cls, field, "Lcom/sun/jna/Pointer;");
      if (!fid) {
        throwByName(env, EUnsatisfiedLink, field);
        return;
      }
      (*env)->SetStaticObjectField(env, cls, fid, newJavaPointer(env, types[i]));
    }
  }
}
  
#if !defined(__APPLE__)
#define JAWT_HEADLESS_HACK
#ifdef _WIN32
#define JAWT_NAME "jawt.dll"
#define METHOD_NAME (sizeof(void*)==4?"_JAWT_GetAWT@8":"JAWT_GetAWT")
#else
#define JAWT_NAME "libjawt.so"
#define METHOD_NAME "JAWT_GetAWT"
#endif
static void* jawt_handle = NULL;
static jboolean (JNICALL *pJAWT_GetAWT)(JNIEnv*,JAWT*);
#define JAWT_GetAWT (*pJAWT_GetAWT)
#endif

JNIEXPORT jlong JNICALL
Java_com_sun_jna_Native_getWindowHandle0(JNIEnv *env, jclass classp, jobject w) {
  jlong handle = 0;
  JAWT_DrawingSurface* ds;
  JAWT_DrawingSurfaceInfo* dsi;
  jint lock;
  JAWT awt;
  
  // NOTE: AWT/JAWT must be loaded prior to this code's execution
  // See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6539705
  awt.version = JAWT_VERSION_1_4;
#ifdef JAWT_HEADLESS_HACK
  // Java versions 1.5/1.6 throw UnsatisfiedLinkError when run headless
  // Avoid the issue by dynamic linking
  if (!pJAWT_GetAWT) {
#ifdef _WIN32
    // Windows needs the full path to JAWT; calling System.loadLibrary("jawt") 
    // from Java adds it to the path so that a simple LoadLibrary("jawt.dll")
    // works, but may cause other attempts to load that library from Java to 
    // to get an UnsatisfiedLinkError, reporting that the library is already
    // loaded in a different class loader, since there is no way to force the
    // JAWT library by the system class loader.
    // Use Unicode strings in case the path to the library includes non-ASCII
    // characters. 
    wchar_t* path = L"jawt.dll";
    wchar_t* prop = (wchar_t*)get_system_property(env, "java.home", JNI_TRUE);
    if (prop != NULL) {
      const wchar_t* suffix = L"/bin/jawt.dll";
      size_t len = wcslen(prop) + wcslen(suffix) + 1;
      path = (wchar_t*)alloca(len * sizeof(wchar_t));
#ifdef _MSC_VER
      swprintf(path, len, L"%s%s", prop, suffix);
#else
      swprintf(path, L"%s%s", prop, suffix);
#endif
      free(prop);
    }
#undef JAWT_NAME
#define JAWT_NAME path
#endif
    if ((jawt_handle = LOAD_LIBRARY(JAWT_NAME)) == NULL) {
      char msg[1024];
      throwByName(env, EUnsatisfiedLink, LOAD_ERROR(msg, sizeof(msg)));
      return -1;
    }
    if ((pJAWT_GetAWT = (void*)FIND_ENTRY(jawt_handle, METHOD_NAME)) == NULL) {
      char msg[1024], buf[1024];
      snprintf(msg, sizeof(msg), "Error looking up %s: %s",
              METHOD_NAME, LOAD_ERROR(buf, sizeof(buf)));
      throwByName(env, EUnsatisfiedLink, msg);
      return -1;
    }
  }
#endif

  if (!JAWT_GetAWT(env, &awt)) {
    throwByName(env, EUnsatisfiedLink, "Can't load JAWT");
    return 0;
  }

  ds = awt.GetDrawingSurface(env, w);
  if (ds == NULL) {
    throwByName(env, EError, "Can't get drawing surface");
  }
  else {
    lock = ds->Lock(ds);
    if ((lock & JAWT_LOCK_ERROR) != 0) {
      awt.FreeDrawingSurface(ds);
      throwByName(env, EError, "Can't get drawing surface lock");
      return 0;
    }
    dsi = ds->GetDrawingSurfaceInfo(ds);
    if (dsi == NULL) {
      throwByName(env, EError, "Can't get drawing surface info");
    }
    else {
#ifdef _WIN32
      JAWT_Win32DrawingSurfaceInfo* wdsi = 
        (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
      if (wdsi != NULL) {
        // FIXME this kills the VM if the window is not realized;
        // if not, wdsi might be a bogus, non-null value
        // TODO: fix JVM (right) or ensure window is realized (done in Java)
        handle = A2L(wdsi->hwnd);
        if (!handle) {
          throwByName(env, EIllegalState, "Can't get HWND");
        }
      }
      else {
        throwByName(env, EError, "Can't get w32 platform info");
      }
#elif __APPLE__
      // WARNING: the view ref is not guaranteed to be stable except during
      // component paint (see jni_md.h)
      JAWT_MacOSXDrawingSurfaceInfo* mdsi = 
        (JAWT_MacOSXDrawingSurfaceInfo*)dsi->platformInfo;
      if (mdsi != NULL) {
        handle = (unsigned long)mdsi->cocoaViewRef;
        if (!handle) {
          throwByName(env, EIllegalState, "Can't get Cocoa View");
        }
      }
      else {
        throwByName(env, EError, "Can't get OS X platform info");
      }
#else 
      JAWT_X11DrawingSurfaceInfo* xdsi =
        (JAWT_X11DrawingSurfaceInfo*)dsi->platformInfo;
      if (xdsi != NULL) {
        handle = xdsi->drawable;
        if (!handle) {
          throwByName(env, EIllegalState, "Can't get Drawable");
        }
      }
      else {
        throwByName(env, EError, "Can't get X11 platform info");
      }
#endif        
      ds->FreeDrawingSurfaceInfo(dsi);
    }
    ds->Unlock(ds);
    awt.FreeDrawingSurface(ds);
  }

  return handle;
}

JNIEXPORT jobject JNICALL
Java_com_sun_jna_Native_getDirectBufferPointer(JNIEnv *env, jclass classp, jobject buffer) {
  void* addr = (*env)->GetDirectBufferAddress(env, buffer);
  if (addr == NULL) {
    throwByName(env, EIllegalArgument, "Non-direct Buffer is not supported");
    return NULL;
  }
  return newJavaPointer(env, addr);
}

JNIEXPORT void JNICALL
Java_com_sun_jna_Native_setProtected(JNIEnv *env, jclass classp, jboolean protect_access) {
#ifdef HAVE_PROTECTION
  _protect = protect_access;
#endif
}

jboolean
is_protected() {
#ifdef HAVE_PROTECTION  
  if (_protect) return JNI_TRUE;
#endif
  return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_sun_jna_Native_isProtected(JNIEnv *env, jclass classp) {
  return is_protected();
}

JNIEXPORT void JNICALL
Java_com_sun_jna_Native_setPreserveLastError(JNIEnv *env, jclass classp, jboolean preserve) {
  preserve_last_error = preserve;
}

JNIEXPORT jboolean JNICALL
Java_com_sun_jna_Native_getPreserveLastError(JNIEnv *env, jclass classp) {
  return preserve_last_error;
}

JNIEXPORT void JNICALL
Java_com_sun_jna_Native_setLastError(JNIEnv *env, jclass classp, jint code) {
  SET_LAST_ERROR(code);
  update_last_error(env, code);
}

JNIEXPORT jstring JNICALL
Java_com_sun_jna_Native_getNativeVersion(JNIEnv *env, jclass classp) {
#ifndef VERSION
#define VERSION "undefined"
#endif
  return newJavaString(env, VERSION, JNI_FALSE);
}

JNIEXPORT jstring JNICALL
Java_com_sun_jna_Native_getAPIChecksum(JNIEnv *env, jclass classp) {
#ifndef CHECKSUM
#define CHECKSUM "undefined"
#endif
  return newJavaString(env, CHECKSUM, JNI_FALSE);
}

void
extract_value(JNIEnv* env, jobject value, void* resp, size_t size, jboolean promote) {
  if (value == NULL) {
    *(void **)resp = NULL;
  }
  else if ((*env)->IsInstanceOf(env, value, classVoid)) {
    // nothing to do
  }
  else if ((*env)->IsInstanceOf(env, value, classBoolean)) {
    jboolean b = (*env)->GetBooleanField(env, value, FID_Boolean_value);
    if (promote) {
      *(ffi_arg*)resp = b;
    }
    else {
      *(jint*)resp = b;
    }
  }
  else if ((*env)->IsInstanceOf(env, value, classByte)) {
    jbyte b = (*env)->GetByteField(env, value, FID_Byte_value);
    if (promote) {
      *(ffi_arg*)resp = b;
    }
    else {
      *(jbyte*)resp = b;
    }
  }
  else if ((*env)->IsInstanceOf(env, value, classShort)) {
    jshort s = (*env)->GetShortField(env, value, FID_Short_value);
    if (promote) {
      *(ffi_arg*)resp = s;
    }
    else {
      *(jshort*)resp = s;
    }
  }
  else if ((*env)->IsInstanceOf(env, value, classCharacter)) {
    jchar c = (*env)->GetCharField(env, value, FID_Character_value);
    if (promote) {
      *(ffi_arg*)resp = c;
    }
    else {
      *(wchar_t*)resp = c;
    }
  }
  else if ((*env)->IsInstanceOf(env, value, classInteger)) {
    jint i = (*env)->GetIntField(env, value, FID_Integer_value);
    if (promote) {
      *(ffi_arg*)resp = i;
    }
    else {
      *(jint*)resp = i;
    }
  }
  else if ((*env)->IsInstanceOf(env, value, classLong)) {
    *(jlong *)resp = (*env)->GetLongField(env, value, FID_Long_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classFloat)) {
    *(float *)resp = (*env)->GetFloatField(env, value, FID_Float_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classDouble)) {
    *(double *)resp = (*env)->GetDoubleField(env, value, FID_Double_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classStructure)) {
    void* ptr = getStructureAddress(env, value);
    memcpy(resp, ptr, size);
  }
  else if ((*env)->IsInstanceOf(env, value, classPointer)) {
    *(void **)resp = getNativeAddress(env, value);
  }
  else {
    fprintf(stderr, "JNA: unrecognized return type, size %d\n", (int)size);
    memset(resp, 0, size);
  }
}

/** Construct a new Java object from a native value.  */
jobject
new_object(JNIEnv* env, char jtype, void* valuep, jboolean promote) {
    switch(jtype) {
    case 's':
      return newJavaPointer(env, valuep);
    case '*': 
      return newJavaPointer(env, *(void**)valuep);
    case 'J':
      return (*env)->NewObject(env, classLong, MID_Long_init,
                               *(jlong *)valuep);
    case 'F':
      return (*env)->NewObject(env, classFloat, MID_Float_init,
                               *(float *)valuep);
    case 'D':
      return (*env)->NewObject(env, classDouble, MID_Double_init,
                               *(double *)valuep);
    case 'Z':
      // Default mapping for boolean is int32_t
      return (*env)->NewObject(env, classBoolean, MID_Boolean_init,
                               (promote
				? (jint)*(ffi_arg*)valuep
				: (*(jint *)valuep)) ? JNI_TRUE : JNI_FALSE);
    case 'B':
      return (*env)->NewObject(env, classByte, MID_Byte_init,
                               promote
			       ? (jbyte)*(ffi_arg*)valuep
			       : (*(jbyte *)valuep));
    case 'C':
      return (*env)->NewObject(env, classCharacter, MID_Character_init,
                               promote
			       ? (jchar)*(ffi_arg*)valuep
			       : (jchar)(*(wchar_t *)valuep));
    case 'S':
      return (*env)->NewObject(env, classShort, MID_Short_init,
                               promote
			       ? (jshort)*(ffi_arg*)valuep
			       : (*(jshort *)valuep));
    case 'I':
      return (*env)->NewObject(env, classInteger, MID_Integer_init,
                               promote
			       ? (jint)*(ffi_arg*)valuep
			       : *(jint *)valuep);
    default:
      return NULL;
    }
}

JNIEXPORT jint JNICALL 
JNI_OnLoad(JavaVM *jvm, void *reserved) {
  JNIEnv* env;
  int result = JNI_VERSION_1_4;
  int attached = (*jvm)->GetEnv(jvm, (void *)&env, JNI_VERSION_1_4) == JNI_OK;
  const char* err;

  if (!attached) {
    if ((*jvm)->AttachCurrentThread(jvm, (void *)&env, NULL) != JNI_OK) {
      fprintf(stderr, "JNA: Can't attach to JVM thread on load\n");
      return 0;
    }
  }

  if ((err = jnidispatch_init(env)) != NULL) {
    fprintf(stderr, "JNA: Problems loading core IDs: %s\n", err);
    result = 0;
  }
  else if ((err = jnidispatch_callback_init(env)) != NULL) {
    fprintf(stderr, "JNA: Problems loading callback IDs: %s\n", err);
    result = 0;
  }

  if (!attached) {
    (*jvm)->DetachCurrentThread(jvm);
  }

  return result;
}

JNIEXPORT void JNICALL 
JNI_OnUnload(JavaVM *vm, void *reserved) {
  jobject* refs[] = {
    &classObject, &classClass, &classMethod,
    &classString, 
    &classBuffer, &classByteBuffer, &classCharBuffer,
    &classShortBuffer, &classIntBuffer, &classLongBuffer,
    &classFloatBuffer, &classDoubleBuffer,
    &classVoid, &classPrimitiveVoid,
    &classBoolean, &classPrimitiveBoolean,
    &classByte, &classPrimitiveByte,
    &classCharacter, &classPrimitiveCharacter,
    &classShort, &classPrimitiveShort,
    &classInteger, &classPrimitiveInteger,
    &classLong, &classPrimitiveLong,
    &classFloat, &classPrimitiveFloat,
    &classDouble, &classPrimitiveDouble,
    &classPointer, &classNative, &classWString,
    &classStructure, &classStructureByValue,
    &classCallbackReference, &classNativeMapped,
    &classIntegerType, &classPointerType,
  };
  unsigned i;
  JNIEnv* env;
  int attached = (*vm)->GetEnv(vm, (void*)&env, JNI_VERSION_1_4) == JNI_OK;
  if (!attached) {
    if ((*vm)->AttachCurrentThread(vm, (void*)&env, NULL) != JNI_OK) {
      fprintf(stderr, "JNA: Can't attach to JVM thread on unload\n");
      return;
    }
  }

  for (i=0;i < sizeof(refs)/sizeof(refs[0]);i++) {
    if (*refs[i]) {
      (*env)->DeleteWeakGlobalRef(env, *refs[i]);
      *refs[i] = NULL;
    }
  }
  
  jnidispatch_callback_dispose(env);

#ifdef JAWT_HEADLESS_HACK
  if (jawt_handle != NULL) {
    FREE_LIBRARY(jawt_handle);
    jawt_handle = NULL;
    pJAWT_GetAWT = NULL;
  }
#endif

  if (jna_encoding) {
    free((void*)jna_encoding);
  }

  if (!attached) {
    (*vm)->DetachCurrentThread(vm);
  }
}

/** Get the FFI type for the native type which will be converted to the given
    Java class. */
ffi_type*
get_ffi_type(JNIEnv* env, jclass cls, char jtype) {
  switch (jtype) {
  case 'Z': 
    return &ffi_type_uint32;
  case 'B':
    return &ffi_type_sint8;
  case 'C':
    return sizeof(wchar_t) == 2 ? &ffi_type_uint16 : &ffi_type_uint32;
  case 'S':
    return &ffi_type_sint16;
  case 'I':
    return &ffi_type_sint32;
  case 'J':
    return &ffi_type_sint64;
  case 'F':
    return &ffi_type_float;
  case 'D':
    return &ffi_type_double;
  case 'V':
    return &ffi_type_void;
  case 's': {
    jobject s = (*env)->CallStaticObjectMethod(env, classStructure,
                                               MID_Structure_newInstance, cls);
    return getStructureType(env, s);
  }
  case '*':
  default:
    return &ffi_type_pointer;
  }
}

/** Return the FFI type corresponding to the native equivalent of a
    callback function's return value. */
ffi_type*
get_ffi_rtype(JNIEnv* env, jclass cls, char jtype) {
  switch (jtype) {
  case 'Z': 
  case 'B': 
  case 'C': 
  case 'S':    
  case 'I':
    /*
     * Always use a return type the size of a cpu register.  This fixes up
     * callbacks on big-endian 64bit machines, and does not break things on
     * i386 or amd64. 
     */
    return &ffi_type_slong;
  default:
    return get_ffi_type(env, cls, jtype);
  }
}

typedef struct _method_data {
  ffi_cif cif;
  ffi_cif closure_cif;
  void*   fptr;
  ffi_type** arg_types;
  ffi_type** closure_arg_types;
  int*    flags;
  int     rflag;
  jclass  closure_rclass;
  jobject* to_native;
  jobject  from_native;
  jboolean throw_last_error;
} method_data;

// VM vectors to this callback, which calls native code
static void
method_handler(ffi_cif* cif, void* volatile resp, void** argp, void *cdata) {
  JNIEnv* env = (JNIEnv*)*(void **)argp[0];
  method_data *data = (method_data*)cdata;

  // ignore first two arguments, which are pointers
  void** args = argp + 2;
  void** volatile objects = NULL;
  release_t* volatile release = NULL;
  void** volatile elems = NULL;
  unsigned i;
  void* oldresp = resp;
  const char* volatile throw_type = NULL;
  const char* volatile throw_msg = NULL;
  char msg[64];

  if (data->flags) {
    objects = alloca(data->cif.nargs * sizeof(void*));
    release = alloca(data->cif.nargs * sizeof(release_t));
    elems = alloca(data->cif.nargs * sizeof(void*));
    for (i=0;i < data->cif.nargs;i++) {
      if (data->flags[i] != CVT_DEFAULT) {
        if (data->arg_types[i]->type == FFI_TYPE_POINTER
            && *(void **)args[i] == NULL) continue;
        switch(data->flags[i]) {
        case CVT_INTEGER_TYPE:
          {
            jlong value = getIntegerTypeValue(env, *(void **)args[i]);
            if (cif->arg_types[i+2]->size < data->cif.arg_types[i]->size) {
              args[i] = alloca(data->cif.arg_types[i]->size);
            }
            if (data->cif.arg_types[i]->size > sizeof(ffi_arg)) {
              *(jlong *)args[i] = value;
            }
            else {
              *(ffi_arg *)args[i] = (ffi_arg)value;
            }
          }
          break;
        case CVT_POINTER_TYPE:
          *(void **)args[i] = getPointerTypeAddress(env, *(void **)args[i]);
          break;
        case CVT_TYPE_MAPPER:
          {
            void* valuep = args[i];
            int jtype = get_jtype_from_ffi_type(data->closure_cif.arg_types[i+2]);
            jobject obj = jtype == '*'
              ? *(void **)valuep
              : new_object(env, (char)jtype, valuep, JNI_FALSE);
            if (cif->arg_types[i+2]->size < data->cif.arg_types[i]->size) {
              args[i] = alloca(data->cif.arg_types[i]->size);
            }
            toNativeTypeMapped(env, obj, args[i],
                               data->cif.arg_types[i]->size,
                               data->to_native[i]);
          }
          break;
        case CVT_NATIVE_MAPPED:
          toNative(env, *(void **)args[i], args[i], data->cif.arg_types[i]->size, JNI_FALSE);
          break;
        case CVT_POINTER:
          *(void **)args[i] = getNativeAddress(env, *(void **)args[i]);
          break;
        case CVT_STRUCTURE:
          objects[i] = *(void **)args[i];
          writeStructure(env, *(void **)args[i]);
          *(void **)args[i] = getStructureAddress(env, *(void **)args[i]);
          break;
        case CVT_STRUCTURE_BYVAL:
          objects[i] = *(void **)args[i];
          writeStructure(env, objects[i]);
          args[i] = getStructureAddress(env, objects[i]);
          break;
        case CVT_STRING:
          *(void **)args[i] = newCStringEncoding(env, (jstring)*(void **)args[i], jna_encoding);
          break;
        case CVT_WSTRING:
          {
            jstring s = (*env)->CallObjectMethod(env, *(void **)args[i], MID_Object_toString);
            *(void **)args[i] = newWideCString(env, s);
          }
          break;
        case CVT_CALLBACK:
          *(void **)args[i] = getCallbackAddress(env, *(void **)args[i]);
          break;
        case CVT_BUFFER:
          {
            void *ptr = (*env)->GetDirectBufferAddress(env, *(void **)args[i]);
            if (ptr != NULL) {
              objects[i] = NULL;
              release[i] = NULL;
            }
            else {
              ptr = getBufferArray(env, *(jobject *)args[i], (jobject *)&objects[i], &elems[i], (void**)&release[i]);
              if (ptr == NULL) {
                throw_type = EIllegalArgument;
                throw_msg = "Buffer arguments must be direct or have a primitive backing array";
                goto cleanup;
              }
            }
            *(void **)args[i] = ptr;
          }
          break;
#define ARRAY(Type) \
 do { \
   objects[i] = *(void **)args[i];                                      \
   release[i] = (void *)(*env)->Release##Type##ArrayElements;           \
   elems[i] = *(void **)args[i] = (*env)->Get##Type##ArrayElements(env, objects[i], NULL); } while(0)
        case CVT_ARRAY_BYTE: ARRAY(Byte); break;
        case CVT_ARRAY_SHORT: ARRAY(Short); break;
        case CVT_ARRAY_CHAR: ARRAY(Char); break;
        case CVT_ARRAY_INT: ARRAY(Int); break;
        case CVT_ARRAY_LONG: ARRAY(Long); break;
        case CVT_ARRAY_FLOAT: ARRAY(Float); break;
        case CVT_ARRAY_DOUBLE: ARRAY(Double); break;
        default:
          break;
        }
      }
    }
  }

  if (data->rflag == CVT_NATIVE_MAPPED) {
    resp = alloca(sizeof(jobject));
  }
  else if (data->rflag == CVT_TYPE_MAPPER) {
    // Ensure enough space for the inner call result
    resp = alloca(data->cif.rtype->size);
  }
  else if (data->rflag == CVT_STRUCTURE_BYVAL) {
    // In the case of returned structure by value, the inner and
    // outer calls have different return types; we pass the structure memory
    // to the inner call but return a Java object to the outer call.
    resp = alloca(data->cif.rtype->size);
  }

  {
    PSTART();
    if (data->throw_last_error) {
      SET_LAST_ERROR(0);
    }
    ffi_call(&data->cif, FFI_FN(data->fptr), resp, args);
    if (data->throw_last_error) {
      int error = GET_LAST_ERROR();
      if (error) {
        snprintf(msg, sizeof(msg), "%d", error);
        throw_type = ELastError;
        throw_msg = msg;
      }
    }
    PROTECTED_END(do { throw_type=EError;throw_msg="Invalid memory access"; } while(0));
  }

  switch(data->rflag) {
  case CVT_TYPE_MAPPER:
    fromNativeTypeMapped(env, data->from_native, resp, data->cif.rtype, data->closure_rclass, oldresp);
    break;
  case CVT_INTEGER_TYPE:
  case CVT_POINTER_TYPE:
  case CVT_NATIVE_MAPPED:
    *(void **)oldresp = fromNative(env, data->closure_rclass, data->cif.rtype, resp, JNI_TRUE);
    break;
  case CVT_POINTER:
    *(void **)resp = newJavaPointer(env, *(void **)resp);
    break;
  case CVT_STRING:
    *(void **)resp = newJavaString(env, *(void **)resp, JNI_FALSE);
    break;
  case CVT_WSTRING:
    *(void **)resp = newJavaWString(env, *(void **)resp);
    break;
  case CVT_STRUCTURE:
    *(void **)resp = newJavaStructure(env, *(void **)resp, data->closure_rclass, JNI_FALSE);
    break;
  case CVT_STRUCTURE_BYVAL:
    *(void **)oldresp = newJavaStructure(env, resp, data->closure_rclass, JNI_TRUE);
    break;
  case CVT_CALLBACK:
    *(void **)resp = newJavaCallback(env, *(void **)resp, data->closure_rclass);
    break;
  default:
    break;
  }

  cleanup:
  if (data->flags) {
    for (i=0;i < data->cif.nargs;i++) {
      switch(data->flags[i]) {
      case CVT_STRUCTURE:
        if (objects[i]) {
          (*env)->CallVoidMethod(env, objects[i], MID_Structure_read);
        }
        break;
      case CVT_STRING:
      case CVT_WSTRING:
        // Free allocated native strings
        free(*(void **)args[i]);
        break;
      case CVT_BUFFER:
      case CVT_ARRAY_BYTE:
      case CVT_ARRAY_SHORT:
      case CVT_ARRAY_CHAR:
      case CVT_ARRAY_INT:
      case CVT_ARRAY_LONG:
      case CVT_ARRAY_FLOAT:
      case CVT_ARRAY_DOUBLE:
        if (*(void **)args[i] && release[i])
          release[i](env, objects[i], elems[i], 0);
        break;
      }
    }
  }

  if (throw_type) {
    throwByName(env, throw_type, throw_msg);
  }
}

JNIEXPORT void JNICALL
Java_com_sun_jna_Native_unregister(JNIEnv *env, jclass ncls, jclass cls, jlongArray handles) {
  jlong* data = (*env)->GetLongArrayElements(env, handles, NULL);
  int count = (*env)->GetArrayLength(env, handles);

  while (count-- > 0) {
    method_data* md = (method_data*)L2A(data[count]);
    if (md->to_native) {
      unsigned i;
      for (i=0;i < md->cif.nargs;i++) {
        if (md->to_native[i])
          (*env)->DeleteWeakGlobalRef(env, md->to_native[i]);
      }
    }
    if (md->from_native) (*env)->DeleteWeakGlobalRef(env, md->from_native);
    if (md->closure_rclass) (*env)->DeleteWeakGlobalRef(env, md->closure_rclass);
    free(md->arg_types);
    free(md->closure_arg_types);
    free(md->flags);
    free(md);
  }
  (*env)->ReleaseLongArrayElements(env, handles, data, 0);
  (*env)->UnregisterNatives(env, cls);
}

JNIEXPORT jlong JNICALL
Java_com_sun_jna_Native_registerMethod(JNIEnv *env, jclass ncls,
                                       jclass cls, jstring name,
                                       jstring signature,
                                       jintArray conversions,
                                       jlongArray closure_atypes,
                                       jlongArray atypes,
                                       jint rconversion,
                                       jlong closure_return_type,
                                       jlong return_type,
                                       jclass closure_rclass,
                                       jlong function, jint cc,
                                       jboolean throw_last_error,
                                       jobjectArray to_native,
                                       jobject from_native)
{
  int argc = atypes ? (*env)->GetArrayLength(env, atypes) : 0;
  const char* cname = newCStringUTF8(env, name);
  const char* sig = newCStringUTF8(env, signature);
  void *code;
  void *closure;
  method_data* data = malloc(sizeof(method_data));
  ffi_cif* closure_cif = &data->closure_cif;
  int status;
  int i;
  int abi = FFI_DEFAULT_ABI; 
  ffi_type* rtype = (ffi_type*)L2A(return_type);
  ffi_type* closure_rtype = (ffi_type*)L2A(closure_return_type);
  jlong* types = atypes ? (*env)->GetLongArrayElements(env, atypes, NULL) : NULL;
  jlong* closure_types = closure_atypes ? (*env)->GetLongArrayElements(env, closure_atypes, NULL) : NULL;
  jint* cvts = conversions ? (*env)->GetIntArrayElements(env, conversions, NULL) : NULL;
#if defined(_WIN32) && !defined(_WIN64)
  if (cc == CALLCONV_STDCALL) abi = FFI_STDCALL;
#endif

  data->throw_last_error = throw_last_error;
  data->arg_types = malloc(sizeof(ffi_type*) * argc);
  data->closure_arg_types = malloc(sizeof(ffi_type*) * (argc + 2));
  data->closure_arg_types[0] = &ffi_type_pointer;
  data->closure_arg_types[1] = &ffi_type_pointer;
  data->closure_rclass = NULL;
  data->flags = cvts ? malloc(sizeof(jint)*argc) : NULL;
  data->rflag = rconversion;
  data->to_native = NULL;
  data->from_native = from_native ? (*env)->NewWeakGlobalRef(env, from_native) : NULL;

  for (i=0;i < argc;i++) {
    data->closure_arg_types[i+2] = (ffi_type*)L2A(closure_types[i]);
    data->arg_types[i] = (ffi_type*)L2A(types[i]);
    if (cvts) {
      data->flags[i] = cvts[i];
      // Type mappers only apply to non-primitive arguments
      if (cvts[i] == CVT_TYPE_MAPPER) {
        if (!data->to_native) {
          data->to_native = calloc(argc, sizeof(jweak));
        }
        data->to_native[i] = (*env)->NewWeakGlobalRef(env, (*env)->GetObjectArrayElement(env, to_native, i));
      }
    }
  }
  if (types) (*env)->ReleaseLongArrayElements(env, atypes, types, 0);
  if (closure_types) (*env)->ReleaseLongArrayElements(env, closure_atypes, closure_types, 0);
  if (cvts) (*env)->ReleaseIntArrayElements(env, conversions, cvts, 0);
  data->fptr = L2A(function);
  data->closure_rclass = (*env)->NewWeakGlobalRef(env, closure_rclass);

  status = ffi_prep_cif(closure_cif, abi, argc+2, closure_rtype, data->closure_arg_types);  
  if (ffi_error(env, "Native method mapping", status)) {
    goto cleanup;
  }

  status = ffi_prep_cif(&data->cif, abi, argc, rtype, data->arg_types);
  if (ffi_error(env, "Native method setup", status)) {
    goto cleanup;
  }

  closure = ffi_closure_alloc(sizeof(ffi_closure), &code);
  status = ffi_prep_closure_loc(closure, closure_cif, method_handler, data, code);
  if (status != FFI_OK) {
    throwByName(env, EError, "Native method linkage failed");
    goto cleanup;
  }

  {
    JNINativeMethod m = { (char*)cname, (char*)sig, code };
    (*env)->RegisterNatives(env, cls, &m, 1);
  }

 cleanup:
  if (status != FFI_OK) {
    free(data->arg_types);
    free(data->flags);
    free(data);
    data = NULL;
  }
  free((void *)cname);
  free((void *)sig);

  return A2L(data);
}

JNIEXPORT void JNICALL
Java_com_sun_jna_Native_ffi_1call(JNIEnv *env, jclass cls, jlong cif, jlong fptr, jlong resp, jlong args) 
{
  ffi_call(L2A(cif), FFI_FN(L2A(fptr)), L2A(resp), L2A(args));
}

JNIEXPORT jlong JNICALL
Java_com_sun_jna_Native_ffi_1prep_1cif(JNIEnv *env, jclass cls, jint abi, jint nargs, jlong ffi_return_type, jlong ffi_types) 
{
  ffi_cif* cif = malloc(sizeof(ffi_cif));
  ffi_status s = ffi_prep_cif(L2A(cif), abi, nargs, L2A(ffi_return_type), L2A(ffi_types));
  if (ffi_error(env, "ffi_prep_cif", s)) {
    return 0;
  }
  return A2L(cif);
}

static void
closure_handler(ffi_cif* cif, void* resp, void** argp, void *cdata)
{
  callback* cb = (callback *)cdata;
  JavaVM* jvm = cb->vm;
  JNIEnv* env;
  jobject obj;
  int attached;

  attached = (*jvm)->GetEnv(jvm, (void *)&env, JNI_VERSION_1_4) == JNI_OK;
  if (!attached) {
    if ((*jvm)->AttachCurrentThread(jvm, (void *)&env, NULL) != JNI_OK) {
      fprintf(stderr, "JNA: Can't attach to current thread\n");
      return;
    }
  }

  // Give the callback its own local frame to ensure all local references
  // are properly disposed
  if ((*env)->PushLocalFrame(env, 16) < 0) {
    fprintf(stderr, "JNA: Out of memory: Can't allocate local frame");
  }
  else {
    obj = (*env)->NewLocalRef(env, cb->object);
    if ((*env)->IsSameObject(env, obj, NULL)) {
      fprintf(stderr, "JNA: callback object has been garbage collected\n");
      if (cif->rtype->type != FFI_TYPE_VOID)
        memset(resp, 0, cif->rtype->size);
    }
    else {
      (*env)->CallVoidMethod(env, obj, MID_ffi_callback_invoke,
                             A2L(cif), A2L(resp), A2L(argp));
    }    
    
    (*env)->PopLocalFrame(env, NULL);
  }

  if (!attached) {
    (*jvm)->DetachCurrentThread(jvm);
  }
}

JNIEXPORT jlong JNICALL
Java_com_sun_jna_Native_ffi_1prep_1closure(JNIEnv *env, jclass cls, jlong cif, jobject obj) 
{
  callback* cb = (callback *)malloc(sizeof(callback));
  ffi_status s;

  if ((*env)->GetJavaVM(env, &cb->vm) != JNI_OK) {
    throwByName(env, EUnsatisfiedLink, "Can't get Java VM");
    return 0;
  }

  cb->object = (*env)->NewWeakGlobalRef(env, obj);
  cb->closure = ffi_closure_alloc(sizeof(ffi_closure), L2A(&cb->x_closure));

  s = ffi_prep_closure_loc(cb->closure, L2A(cif), &closure_handler, 
                           cb, cb->x_closure);
  if (ffi_error(env, "ffi_prep_cif", s)) {
    return 0;
  }
  return A2L(cb);
}

JNIEXPORT void JNICALL
Java_com_sun_jna_Native_ffi_1free_1closure(JNIEnv *env, jclass cls, jlong closure) {
  callback* cb = (callback *)L2A(closure);

  (*env)->DeleteWeakGlobalRef(env, cb->object);
  ffi_closure_free(cb->closure);
  free(cb);
}

JNIEXPORT jint JNICALL
Java_com_sun_jna_Native_initialize_1ffi_1type(JNIEnv *env, jclass cls, jlong type_info) {
  ffi_type* type = L2A(type_info);
  ffi_cif cif;
  ffi_status status = ffi_prep_cif(&cif, FFI_DEFAULT_ABI, 0, type, NULL);
  if (ffi_error(env, "ffi_prep_cif", status)) {
    return 0;
  }
  return (jint)type->size;
}

#ifdef __cplusplus
}
#endif
