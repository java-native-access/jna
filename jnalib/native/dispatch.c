/*
 * @(#)dispatch.c       1.9 98/03/22
 * 
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright (c) 2007 Timothy Wall. All Rights Reserved.
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
#ifndef UNICODE
#define UNICODE
#endif
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#ifdef _MSC_VER
#define alloca _alloca
#else
#include <malloc.h>
#endif
#define LIBNAMETYPE wchar_t*
#define LIBNAME2CSTR(ENV,JSTR) newWideCString(ENV,JSTR)
/* See http://msdn.microsoft.com/en-us/library/ms682586(VS.85).aspx:
 * "Note that the standard search strategy and the alternate search strategy  
 * specified by LoadLibraryEx with LOAD_WITH_ALTERED_SEARCH_PATH differ in    
 * just one way: The standard search begins in the calling application's      
 * directory, and the alternate search begins in the directory of the         
 * executable module that LoadLibraryEx is loading."                          
 */
#define LOAD_LIBRARY(NAME) LoadLibraryExW(NAME, NULL, LOAD_WITH_ALTERED_SEARCH_PATH)
#define LOAD_ERROR(BUF,LEN) w32_format_error(BUF, LEN)
#define FREE_LIBRARY(HANDLE) FreeLibrary(HANDLE)
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
#ifdef sun
#  include <alloca.h>
#endif
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
static jclass classString;
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

static jmethodID MID_Class_getComponentType;
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
static jmethodID MID_Structure_getTypeInfo;
static jmethodID MID_Structure_newInstance;

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

/* Forward declarations */
static char* newCString(JNIEnv *env, jstring jstr);
static char* newCStringUTF8(JNIEnv *env, jstring jstr);
static wchar_t* newWideCString(JNIEnv *env, jstring jstr);
static jstring newJavaString(JNIEnv *env, const char *str, jboolean wide);

static void* getBufferArray(JNIEnv* env, jobject buf,
                            jobject* arrayp, char* typep, void** elemp);
static char getArrayComponentType(JNIEnv *, jobject);
static void *getNativeAddress(JNIEnv *, jobject);
static void *getStructureAddress(JNIEnv *, jobject);
static ffi_type* getStructureType(JNIEnv *, jobject);
static void update_last_error(JNIEnv*, int);

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

/* invoke the real native function */
static void
dispatch(JNIEnv *env, jobject self, jint callconv, jobjectArray arr, 
         ffi_type *ffi_return_type, void *resP)
{
  int i, nargs;
  void *func;
  jvalue* c_args;
  char array_pt;
  struct _array_elements {
    char type;
    jobject array;
    void *elems;
  } *array_elements;
  int array_count = 0;
  ffi_cif cif;
  ffi_type** ffi_types;
  void** ffi_values;
  ffi_abi abi;
  ffi_status status;
  char msg[128];
  
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
        throwByName(env, EUnsupportedOperation, msg);
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
        throwByName(env, EIllegalState, msg);
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
                         &array_elements[array_count].type,
                         &array_elements[array_count].elems);
        if (c_args[i].l == NULL) {
          throwByName(env, EIllegalArgument,
                      "Buffer arguments must be direct or have a primitive backing array");
          goto cleanup;
        }
        ++array_count;
      }
    }
    else if ((array_pt = getArrayComponentType(env, arg)) != 0
             && array_pt != 'L') {
      void *ptr = NULL;

      switch(array_pt) {
      case 'Z': ptr = (*env)->GetBooleanArrayElements(env, arg, NULL); break;
      case 'B': ptr = (*env)->GetByteArrayElements(env, arg, NULL); break;
      case 'C': ptr = (*env)->GetCharArrayElements(env, arg, NULL); break;
      case 'S': ptr = (*env)->GetShortArrayElements(env, arg, NULL); break;
      case 'I': ptr = (*env)->GetIntArrayElements(env, arg, NULL); break;
      case 'J': ptr = (*env)->GetLongArrayElements(env, arg, NULL); break;
      case 'F': ptr = (*env)->GetFloatArrayElements(env, arg, NULL); break;
      case 'D': ptr = (*env)->GetDoubleArrayElements(env, arg, NULL); break;
      }
      if (!ptr) {
        throwByName(env, EOutOfMemory,
                    "Could not obtain memory for primitive buffer");
        goto cleanup;
      }
      c_args[i].l = ptr;
      ffi_types[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
      array_elements[array_count].type = array_pt;
      array_elements[array_count].array = arg;
      array_elements[array_count++].elems = ptr;
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
    throwByName(env, EIllegalArgument, msg);
    goto cleanup;
  }

  status = ffi_prep_cif(&cif, abi, nargs, ffi_return_type, ffi_types);
  switch(status) {
  case FFI_BAD_ABI:
    snprintf(msg, sizeof(msg),
            "Invalid calling convention: %d", (int)callconv); 
    throwByName(env, EIllegalArgument, msg);
    break;
  case FFI_BAD_TYPEDEF:
    snprintf(msg, sizeof(msg),
             "Invalid structure definition (native typedef error)");
    throwByName(env, EIllegalArgument, msg);
    break;
  case FFI_OK: {
    PSTART();
    ffi_call(&cif, FFI_FN(func), resP, ffi_values);
    if (preserve_last_error) {
      update_last_error(env, GET_LAST_ERROR());
    }
    PEND();
    break;
  }
  default:
    snprintf(msg, sizeof(msg),
             "Native call setup failure: error code %d", status);
    throwByName(env, EIllegalArgument, msg);
    break;
  }
  
 cleanup:
  // Release array elements
  for (i=0;i < array_count;i++) {
    switch (array_elements[i].type) {
    case 'Z':
      (*env)->ReleaseBooleanArrayElements(env, array_elements[i].array,
                                          array_elements[i].elems, 0);
      break;
    case 'B':
      (*env)->ReleaseByteArrayElements(env, array_elements[i].array,
                                       array_elements[i].elems, 0);
      break;
    case 'S':
      (*env)->ReleaseShortArrayElements(env, array_elements[i].array,
                                        array_elements[i].elems, 0);
      break;
    case 'C':
      (*env)->ReleaseCharArrayElements(env, array_elements[i].array,
                                       array_elements[i].elems, 0);
      break;
    case 'I':
      (*env)->ReleaseIntArrayElements(env, array_elements[i].array,
                                      array_elements[i].elems, 0);
      break;
    case 'J':
      (*env)->ReleaseLongArrayElements(env, array_elements[i].array,
                                       array_elements[i].elems, 0);
      break;
    case 'F':
      (*env)->ReleaseFloatArrayElements(env, array_elements[i].array,
                                        array_elements[i].elems, 0);
      break;
    case 'D':
      (*env)->ReleaseDoubleArrayElements(env, array_elements[i].array,
                                         array_elements[i].elems, 0);
      break;
    }
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
  ffi_type* type = getStructureType(env, result);
  if (!type) {
    throwByName(env, EIllegalState, "Return structure type info not initialized");
  }
  else {
    dispatch(env, self, callconv, arr, type, memory);
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
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_sint32, &result);
#if defined (__LP64__)
    /* 
     * Big endian 64bit machines will put a 32bit return value in the 
     * upper 4 bytes of the memory area.
     */
    return result.j & 0xffffffff;
#else
    return result.i;
#endif
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
                                                        jint call_conv) {
  callback* cb =
    create_callback(env, obj, method, param_types, return_type, call_conv);
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
    LIBNAMETYPE libname = NULL;

    /* dlopen on Unix allows NULL to mean "current process" */
    if (lib != NULL) {
      if ((libname = LIBNAME2CSTR(env, lib)) == NULL) {
        return (jlong)A2L(NULL);
      }
    }

    handle = (void *)LOAD_LIBRARY(libname);
    if (!handle) {
      char buf[1024];
      throwByName(env, EUnsatisfiedLink, LOAD_ERROR(buf, sizeof(buf)));
    }
    if (libname != NULL)
      free(libname);
    return (jlong)A2L(handle);
}

/*
 * Class:     com_sun_jna_NativeLibrary
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_sun_jna_NativeLibrary_close(JNIEnv *env, jclass cls, jlong handle)
{
    FREE_LIBRARY(L2A(handle));
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
    char *funname = NULL;

    if ((funname = newCString(env, fun)) != NULL) {
	func = (void *)FIND_ENTRY(handle, funname);
        if (!func) {
          char buf[1024];
          throwByName(env, EUnsatisfiedLink, LOAD_ERROR(buf, sizeof(buf)));
        }
	free(funname);
    }
    return (jlong)A2L(func);
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
    return "Class.getComponentType(Class)";
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
  jlong i = 0;
  jlong result = -1L;
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
    void* str;
    int size = len + 1;

    if (wide) {
      size *= sizeof(wchar_t);
      str = newWideCString(env, value);
    }
    else {
      str = newCString(env, value);
    }
    if (str != NULL) {
      MEMCPY(L2A(addr), str, size);
      free(str);
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
    jclass cls = (*env)->FindClass(env, name);

    if (cls != 0) /* Otherwise an exception has already been thrown */
        (*env)->ThrowNew(env, cls, msg);

    /* It's a good practice to clean up the local references. */
    (*env)->DeleteLocalRef(env, cls);
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
            throwByName(env, EOutOfMemory, "Can't allocate C string");
            (*env)->DeleteLocalRef(env, bytes);
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
    jbyteArray bytes = 0;
    char *result = NULL;

    bytes = (*env)->CallObjectMethod(env, jstr, MID_String_getBytes2,
                                     newJavaString(env, "UTF8", JNI_FALSE));
    if (!(*env)->ExceptionCheck(env)) {
        jint len = (*env)->GetArrayLength(env, bytes);
        result = (char *)malloc(len + 1);
        if (result == NULL) {
            throwByName(env, EOutOfMemory, "Can't allocate C string");
            (*env)->DeleteLocalRef(env, bytes);
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
            throwByName(env, EOutOfMemory, "Can't allocate wide C string");
            (*env)->DeleteLocalRef(env, chars);
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

/* Constructs a Java string from a char array (using the String(byte [])
 * constructor, which uses default local encoding) or a short array (using the
 * String(char[]) ctor, which uses the character values unmodified).  
 */
static jstring
newJavaString(JNIEnv *env, const char *ptr, jboolean wide) 
{
    jstring result = 0;
    PSTART();

    if (wide) {
        // TODO: proper conversion from native wchar_t to jchar
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

char
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
  if ((*env)->IsAssignableFrom(env, cls, classStructure)
      && (*env)->IsAssignableFrom(env, cls, classStructureByValue))
    return 's';
  if ((*env)->IsAssignableFrom(env, cls, classPointer))
    return '*';
  return 0;
}

static void *
getStructureAddress(JNIEnv *env, jobject obj) {
  jobject ptr = (*env)->GetObjectField(env, obj, FID_Structure_memory);
  return getNativeAddress(env, ptr);
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

static void *
getNativeAddress(JNIEnv *env, jobject obj) {
  return L2A((*env)->GetLongField(env, obj, FID_Pointer_peer));
}

static char
getArrayComponentType(JNIEnv *env, jobject obj) {
  jclass cls = (*env)->GetObjectClass(env, obj);
  jclass type = (*env)->CallObjectMethod(env, cls, MID_Class_getComponentType);
  if (type != NULL) {
    return get_jtype(env, type);
  }
  return 0;
}


static void*
getBufferArray(JNIEnv* env, jobject buf,
               jobject* arrayp, char* typep, void** elemp) {
  void *ptr = NULL;
  int offset = 0;
  jobject array = NULL;

#define GET_ARRAY(TYPE, ELEM_SIZE, JTYPE) \
do { jboolean cpy; \
  array = (*env)->CallObjectMethod(env, buf, MID_##TYPE##Buffer_array); \
  if (array != NULL) { \
    offset = \
       (*env)->CallIntMethod(env, buf, MID_##TYPE##Buffer_arrayOffset) \
       * ELEM_SIZE; \
    ptr = (*env)->Get##TYPE##ArrayElements(env, array, &cpy); \
    *typep = (JTYPE); \
  } \
} while(0)

  if ((*env)->IsInstanceOf(env, buf, classByteBuffer)) {
    GET_ARRAY(Byte, 1, 'B');
  }
  else if((*env)->IsInstanceOf(env, buf, classCharBuffer)) {
    GET_ARRAY(Char, 2, 'C');
  }
  else if((*env)->IsInstanceOf(env, buf, classShortBuffer)) {
    GET_ARRAY(Short, 2, 'S');
  }
  else if((*env)->IsInstanceOf(env, buf, classIntBuffer)) {
    GET_ARRAY(Int, 4, 'I');
  }
  else if((*env)->IsInstanceOf(env, buf, classLongBuffer)) {
    GET_ARRAY(Long, 8, 'J');
  }
  else if((*env)->IsInstanceOf(env, buf, classFloatBuffer)) {
    GET_ARRAY(Float, 4, 'F');
  }
  else if((*env)->IsInstanceOf(env, buf, classDoubleBuffer)) {
    GET_ARRAY(Double, 8, 'D');
  }
  if (ptr != NULL) {
    *arrayp = array;
    *elemp = ptr;
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
    wchar_t* prop = NULL;
    jclass classSystem = (*env)->FindClass(env, "java/lang/System");
    if (classSystem != NULL) {
      jmethodID mid = (*env)->GetStaticMethodID(env, classSystem, "getProperty",
                                                "(Ljava/lang/String;)Ljava/lang/String;");
      if (mid != NULL) {
        jstring propname = newJavaString(env, "java.home", JNI_FALSE);
        jstring java_home = (*env)->CallStaticObjectMethod(env, classSystem,
                                                           mid, propname);
        if (java_home != NULL) {
          if ((prop = newWideCString(env, java_home)) != NULL) {
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
        }
      }
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
      throwByName(env, EError, "Can't get drawing surface lock");
      awt.FreeDrawingSurface(ds);
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
extract_value(JNIEnv* env, jobject value, void* resp, size_t size) {
  if (value == NULL) {
    *(void **)resp = NULL;
  }
  else if ((*env)->IsInstanceOf(env, value, classVoid)) {
    // nothing to do
  }
  else if ((*env)->IsInstanceOf(env, value, classBoolean)) {
    *(word_t *)resp = (*env)->GetBooleanField(env, value, FID_Boolean_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classByte)) {
    *(word_t *)resp = (*env)->GetByteField(env, value, FID_Byte_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classShort)) {
    *(word_t *)resp = (*env)->GetShortField(env, value, FID_Short_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classCharacter)) {
    *(word_t *)resp = (*env)->GetCharField(env, value, FID_Character_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classInteger)) {
    *(word_t *)resp = (*env)->GetIntField(env, value, FID_Integer_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classLong)) {
    *(jlong *)resp = (*env)->GetLongField(env, value, FID_Long_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classFloat)) {
    *(jfloat *)resp = (*env)->GetFloatField(env, value, FID_Float_value);
  }
  else if ((*env)->IsInstanceOf(env, value, classDouble)) {
    *(jdouble *)resp = (*env)->GetDoubleField(env, value, FID_Double_value);
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

jobject
new_object(JNIEnv* env, char jtype, void* valuep) {
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
      return (*env)->NewObject(env, classBoolean, MID_Boolean_init,
                              (*(int *)valuep ? JNI_TRUE : JNI_FALSE));
    case 'B':
      return (*env)->NewObject(env, classByte, MID_Byte_init,
                               *(char *)valuep & 0xFF);
    case 'C':
      return (*env)->NewObject(env, classCharacter, MID_Character_init,
                              *(wchar_t *)valuep & 0xFFFF);
    case 'S':
      return (*env)->NewObject(env, classShort, MID_Short_init,
                              *(short *)valuep & 0xFFFF);
    case 'I':
      return (*env)->NewObject(env, classInteger, MID_Integer_init,
                               *(int *)valuep);
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
    &classPointer, &classNative,
    &classStructure, &classStructureByValue,
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

  if (!attached) {
    (*vm)->DetachCurrentThread(vm);
  }
}

ffi_type*
get_ffi_type(JNIEnv* env, jclass cls, char jtype) {
  switch (jtype) {
  case 'Z': 
    return &ffi_type_sint;
  case 'B':
    return &ffi_type_sint8;
  case 'C':
    return &ffi_type_sint;
  case 'S':
    return &ffi_type_sshort;
  case 'I':
    return &ffi_type_sint;
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
    return &ffi_type_pointer;
  default:
    return NULL;
  }
}

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

#ifdef __cplusplus
}
#endif
