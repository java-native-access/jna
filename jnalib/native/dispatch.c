/*
 * @(#)dispatch.c       1.9 98/03/22
 * 
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright (c) 2007 Timothy Wall. All Rights Reserved.
 * Copyright (c) 2007 Wayn Meissner. All Rights Reserved.
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
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#ifdef _MSC_VER
#define alloca _alloca
#endif
#define LOAD_LIBRARY(name) LoadLibrary(name)
#define FREE_LIBRARY(handle) FreeLibrary(handle)
#define FIND_ENTRY(lib, name) GetProcAddress(lib, name)
#define dlerror() ""
#else
#include <dlfcn.h>
#define LOAD_LIBRARY(name) dlopen(name, RTLD_LAZY)
#define FREE_LIBRARY(handle) dlclose(handle)
#define FIND_ENTRY(lib, name) dlsym(lib, name)
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
#include "protect.h"
#endif

#ifdef __cplusplus
extern "C"
#endif

static JAWT awt;
static int jawt_initialized;

#ifdef PROTECTED_START
#define ON_ERROR() throwByName(env, "java/lang/Error", "Invalid memory access")
#define PSTART() PROTECTED_START()
#define PEND() PROTECTED_END(ON_ERROR())
#define MEMCPY(D,S,L) do { \
  PSTART(); memcpy(D,S,L); PEND(); \
} while(0)
#else
#define PSTART()
#define PEND()
#define MEMCPY(D,S,L) memcpy(D,S,L)
#endif

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
static jclass classPointer;
static jclass classBuffer;
static jclass classByteBuffer;
static jclass classCharBuffer;
static jclass classShortBuffer;
static jclass classIntBuffer;
static jclass classLongBuffer;
static jclass classFloatBuffer;
static jclass classDoubleBuffer;

static jmethodID MID_Class_getComponentType;
static jmethodID MID_String_getBytes;
static jmethodID MID_String_toCharArray;
static jmethodID MID_String_init_bytes;
static jmethodID MID_Pointer_init;
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

static jfieldID FID_Boolean_value;
static jfieldID FID_Byte_value;
static jfieldID FID_Short_value;
static jfieldID FID_Character_value;
static jfieldID FID_Integer_value;
static jfieldID FID_Long_value;
static jfieldID FID_Float_value;
static jfieldID FID_Double_value;
static jfieldID FID_Pointer_peer;

/* Forward declarations */
static char* newCString(JNIEnv *env, jstring jstr);
static wchar_t* newWideCString(JNIEnv *env, jstring jstr);
static jstring newJavaString(JNIEnv *env, const char *str, jboolean wide);

static void* getBufferArray(JNIEnv* env, jobject buf,
                            jobject* arrayp, char* typep, void** elemp);
static char getArrayComponentType(JNIEnv *, jobject);
static void *getNativeAddress(JNIEnv *, jobject);
static jboolean init_jawt(JNIEnv*);

/* invoke the real native function */
static void
dispatch(JNIEnv *env, jobject self, jint callconv, jobjectArray arr, 
         ffi_type *ffi_return, void *resP)
{
  int i, nargs, nwords;
  void *func;
  jvalue c_args[MAX_NARGS];
  char array_pt;
  struct _array_elements {
    char type;
    jobject array;
    void *elems;
  } array_elements[MAX_NARGS];
  int array_count = 0;
  ffi_cif cif;
  ffi_type* ffi_args[MAX_NARGS];
  void* ffi_values[MAX_NARGS];
  ffi_abi abi;
  ffi_status status;
  char msg[1024];
  
  nargs = (*env)->GetArrayLength(env, arr);
  if (nargs > MAX_NARGS) {
    sprintf(msg, "Too many arguments (max %d)", MAX_NARGS);
    throwByName(env,"java/lang/IllegalArgumentException", msg);
    return;
  }
  
  // Get the function pointer
  func = getNativeAddress(env, self);
  
  for (nwords = 0, i = 0; i < nargs; i++) {
    jobject arg = (*env)->GetObjectArrayElement(env, arr, i);
    
    if (arg == NULL) {
      c_args[i].l = NULL;
      ffi_args[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
    }
    else if ((*env)->IsInstanceOf(env, arg, classBoolean)) {
      c_args[i].i = (*env)->GetBooleanField(env, arg, FID_Boolean_value);
      ffi_args[i] = &ffi_type_sint32;
      ffi_values[i] = &c_args[i].i;
    }
    else if ((*env)->IsInstanceOf(env, arg, classByte)) {
      c_args[i].b = (*env)->GetByteField(env, arg, FID_Byte_value);
      ffi_args[i] = &ffi_type_sint8;
      ffi_values[i] = &c_args[i].b;
    }
    else if ((*env)->IsInstanceOf(env, arg, classShort)) {
      c_args[i].s = (*env)->GetShortField(env, arg, FID_Short_value);
      ffi_args[i] = &ffi_type_sint16;
      ffi_values[i] = &c_args[i].s;
    }
    else if ((*env)->IsInstanceOf(env, arg, classCharacter)) {
      if (sizeof(wchar_t) == 2) {
        c_args[i].s = (*env)->GetCharField(env, arg, FID_Character_value);
        ffi_args[i] = &ffi_type_sint16;
        ffi_values[i] = &c_args[i].s;
      }
      else {
        c_args[i].i = (*env)->GetCharField(env, arg, FID_Character_value);
        ffi_args[i] = &ffi_type_sint32;
        ffi_values[i] = &c_args[i].i;
      }
    }
    else if ((*env)->IsInstanceOf(env, arg, classInteger)) {
      c_args[i].i = (*env)->GetIntField(env, arg, FID_Integer_value);
      ffi_args[i] = &ffi_type_sint32;
      ffi_values[i] = &c_args[i].i;
    }
    else if ((*env)->IsInstanceOf(env, arg, classLong)) {
      c_args[i].j = (*env)->GetLongField(env, arg, FID_Long_value);
      ffi_args[i] = &ffi_type_sint64;
      ffi_values[i] = &c_args[i].j;
    }
    else if ((*env)->IsInstanceOf(env, arg, classFloat)) {
      c_args[i].f = (*env)->GetFloatField(env, arg, FID_Float_value);
      ffi_args[i] = &ffi_type_float;
      ffi_values[i] = &c_args[i].f;
    }
    else if ((*env)->IsInstanceOf(env, arg, classDouble)) {
      c_args[i].d = (*env)->GetDoubleField(env, arg, FID_Double_value);
      ffi_args[i] = &ffi_type_double;
      ffi_values[i] = &c_args[i].d;
    }
    else if ((*env)->IsInstanceOf(env, arg, classPointer)) {
      c_args[i].l = getNativeAddress(env, arg);
      ffi_args[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
    }
    else if ((*env)->IsInstanceOf(env, arg, classBuffer)) {
      c_args[i].l = (*env)->GetDirectBufferAddress(env, arg);
      ffi_args[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
      if (c_args[i].l == NULL) {
          c_args[i].l =
              getBufferArray(env, arg, &array_elements[array_count].array,
                             &array_elements[array_count].type,
                             &array_elements[array_count].elems);
          if (c_args[i].l == NULL) {
              throwByName(env,"java/lang/IllegalArgumentException",
                          "Buffer arguments must be direct or have a primitive backing array");
              goto cleanup;
          }
          ++array_count;
      }
    }
    else if ((array_pt = getArrayComponentType(env, arg)) != 0
             && array_pt != 'L') {
      void *ptr = NULL;
      jboolean cpy;
      
      switch(array_pt) {
      case 'Z': ptr = (*env)->GetBooleanArrayElements(env, arg, &cpy); break;
      case 'B': ptr = (*env)->GetByteArrayElements(env, arg, &cpy); break;
      case 'C': ptr = (*env)->GetCharArrayElements(env, arg, &cpy); break;
      case 'S': ptr = (*env)->GetShortArrayElements(env, arg, &cpy); break;
      case 'I': ptr = (*env)->GetIntArrayElements(env, arg, &cpy); break;
      case 'J': ptr = (*env)->GetLongArrayElements(env, arg, &cpy); break;
      case 'F': ptr = (*env)->GetFloatArrayElements(env, arg, &cpy); break;
      case 'D': ptr = (*env)->GetDoubleArrayElements(env, arg, &cpy); break;
      }
      if (!ptr) {
        throwByName(env,"java/lang/OutOfMemoryException",
                    "Could not obtain memory for primitive buffer");
        goto cleanup;
      }
      c_args[i].l = ptr;
      ffi_args[i] = &ffi_type_pointer;
      ffi_values[i] = &c_args[i].l;
      array_elements[array_count].type = array_pt;
      array_elements[array_count].array = arg;
      array_elements[array_count++].elems = ptr;
    }
    else {
      sprintf(msg, "Unsupported type at parameter %d", i);
      throwByName(env,"java/lang/IllegalArgumentException", msg);
      goto cleanup;
    }
  }
  
  switch (callconv) {
  case CALLCONV_C:
    abi = FFI_DEFAULT_ABI;
    break;
#if defined(_WIN32)
  case CALLCONV_STDCALL:
    abi = FFI_STDCALL;
    break;
#endif // _WIN32
  default:
    sprintf(msg, "Unrecognized calling convention: %d", (int)callconv);
    throwByName(env, "java/lang/IllegalArgumentException", msg);
    goto cleanup;
  }
  status = ffi_prep_cif(&cif, abi, nargs, ffi_return, ffi_args);
  switch(status) {
  case FFI_BAD_ABI:
    sprintf(msg, "Bad FFI ABI: %d", (int)callconv); 
    throwByName(env, "java/lang/IllegalArgumentException", msg);
    break;
  case FFI_OK: {
    PSTART();
    ffi_call(&cif, FFI_FN(func), resP, ffi_values);
    PEND();
    break;
  }
  default:
    sprintf(msg, "Native call setup failure: %d", status);
    throwByName(env, "java/lang/IllegalArgumentException", msg);
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

/*
 * Class:     Function
 * Method:    invokePointer
 * Signature: ([Ljava/lang/Object;)LPointer;
 */
JNIEXPORT jobject JNICALL 
Java_com_sun_jna_Function_invokePointer(JNIEnv *env, jobject self, 
                                        jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_pointer, &result.l);
    return newJavaPointer(env, result.l);
}

/*
 * Class:     Function
 * Method:    invokeDouble
 * Signature: ([Ljava/lang/Object;)D
 */
JNIEXPORT jdouble JNICALL 
Java_com_sun_jna_Function_invokeDouble(JNIEnv *env, jobject self, 
                                       jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_double, &result.d);
    return result.d;
}

/*
 * Class:     Function
 * Method:    invokeFloat
 * Signature: ([Ljava/lang/Object;)F
 */
JNIEXPORT jfloat JNICALL
Java_com_sun_jna_Function_invokeFloat(JNIEnv *env, jobject self, 
                                      jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_float, &result.f);
    return result.f;
}

/*
 * Class:     Function
 * Method:    invokeInt
 * Signature: ([Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL
Java_com_sun_jna_Function_invokeInt(JNIEnv *env, jobject self, 
                                    jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_sint32, &result.i);
    /* 
     * Big endian 64bit machines will put a 32bit return value in the 
     * upper 4 bytes of the memory area.
     */
#if defined (__LP64__)
    return result.j & 0xffffffff;
#else
    return result.i;
#endif
}

/*
 * Class:     Function
 * Method:    invokeLong
 * Signature: ([Ljava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL
Java_com_sun_jna_Function_invokeLong(JNIEnv *env, jobject self, 
                                     jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, &ffi_type_sint64, &result.j);
    return result.j;
}

/*
 * Class:     Function
 * Method:    invokeVoid
 * Signature: ([Ljava/lang/Object;)V
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
    char *libname = NULL;

    if ((libname = newCString(env, lib)) != NULL) {
	handle = (void *)LOAD_LIBRARY(libname);
	free(libname);
    }
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
JNIEXPORT jlong JNICALL Java_com_sun_jna_NativeLibrary_findSymbol(JNIEnv *env,
    jclass cls, jlong libHandle, jstring fun) {

    void *handle = L2A(libHandle);
    void *func = NULL;
    char *funname = NULL;

    if ((funname = newCString(env, fun)) != NULL) {
	func = (void *)FIND_ENTRY(handle, funname);
	free(funname);
    }
    return (jlong)A2L(func);
}

static jboolean
jnidispatch_init(JNIEnv* env) {
  if (!LOAD_CREF(env, Object, "java/lang/Object")) return JNI_FALSE;
  if (!LOAD_CREF(env, Class, "java/lang/Class")) return JNI_FALSE;
  if (!LOAD_CREF(env, Method, "java/lang/reflect/Method")) return JNI_FALSE;
  if (!LOAD_CREF(env, String, "java/lang/String")) return JNI_FALSE;
  if (!LOAD_CREF(env, Buffer, "java/nio/Buffer")) return JNI_FALSE;
  if (!LOAD_CREF(env, ByteBuffer, "java/nio/ByteBuffer")) return JNI_FALSE;
  if (!LOAD_CREF(env, CharBuffer, "java/nio/CharBuffer")) return JNI_FALSE;
  if (!LOAD_CREF(env, ShortBuffer, "java/nio/ShortBuffer")) return JNI_FALSE;
  if (!LOAD_CREF(env, IntBuffer, "java/nio/IntBuffer")) return JNI_FALSE;
  if (!LOAD_CREF(env, LongBuffer, "java/nio/LongBuffer")) return JNI_FALSE;
  if (!LOAD_CREF(env, FloatBuffer, "java/nio/FloatBuffer")) return JNI_FALSE;
  if (!LOAD_CREF(env, DoubleBuffer, "java/nio/DoubleBuffer")) return JNI_FALSE;
  
  if (!LOAD_PCREF(env, Void, "java/lang/Void")) return JNI_FALSE;
  if (!LOAD_PCREF(env, Boolean, "java/lang/Boolean")) return JNI_FALSE;
  if (!LOAD_PCREF(env, Byte, "java/lang/Byte")) return JNI_FALSE;
  if (!LOAD_PCREF(env, Character, "java/lang/Character")) return JNI_FALSE;
  if (!LOAD_PCREF(env, Short, "java/lang/Short")) return JNI_FALSE;
  if (!LOAD_PCREF(env, Integer, "java/lang/Integer")) return JNI_FALSE;
  if (!LOAD_PCREF(env, Long, "java/lang/Long")) return JNI_FALSE;
  if (!LOAD_PCREF(env, Float, "java/lang/Float")) return JNI_FALSE;
  if (!LOAD_PCREF(env, Double, "java/lang/Double")) return JNI_FALSE;
  
  if (!LOAD_MID(env, MID_Long_init, classLong,
                "<init>", "(J)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Integer_init, classInteger,
                "<init>", "(I)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Short_init, classShort,
                "<init>", "(S)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Character_init, classCharacter,
                "<init>", "(C)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Byte_init, classByte,
                "<init>", "(B)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Boolean_init, classBoolean,
                "<init>", "(Z)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Float_init, classFloat,
                "<init>", "(F)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Double_init, classDouble,
                "<init>", "(D)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Class_getComponentType, classClass,
                "getComponentType", "()Ljava/lang/Class;"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_String_getBytes, classString,
                "getBytes", "()[B"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_String_toCharArray, classString,
                "toCharArray", "()[C"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_String_init_bytes, classString,
                "<init>", "([B)V"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Method_getParameterTypes, classMethod,
                "getParameterTypes", "()[Ljava/lang/Class;"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_Method_getReturnType, classMethod,
                "getReturnType", "()Ljava/lang/Class;"))
    return JNI_FALSE;
  
  if (!LOAD_MID(env, MID_ByteBuffer_array, classByteBuffer, "array", "()[B"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_ByteBuffer_arrayOffset, classByteBuffer, "arrayOffset", "()I"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_CharBuffer_array, classCharBuffer, "array", "()[C"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_CharBuffer_arrayOffset, classCharBuffer, "arrayOffset", "()I"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_ShortBuffer_array, classShortBuffer, "array", "()[S"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_ShortBuffer_arrayOffset, classShortBuffer, "arrayOffset", "()I"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_IntBuffer_array, classIntBuffer, "array", "()[I"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_IntBuffer_arrayOffset, classIntBuffer, "arrayOffset", "()I"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_LongBuffer_array, classLongBuffer, "array", "()[J"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_LongBuffer_arrayOffset, classLongBuffer, "arrayOffset", "()I"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_FloatBuffer_array, classFloatBuffer, "array", "()[F"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_FloatBuffer_arrayOffset, classFloatBuffer, "arrayOffset", "()I"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_DoubleBuffer_array, classDoubleBuffer, "array", "()[D"))
    return JNI_FALSE;
  if (!LOAD_MID(env, MID_DoubleBuffer_arrayOffset, classDoubleBuffer, "arrayOffset", "()I"))
    return JNI_FALSE;


  if (!LOAD_FID(env, FID_Boolean_value, classBoolean, "value", "Z"))
    return JNI_FALSE;
  if (!LOAD_FID(env, FID_Byte_value, classByte, "value", "B"))
    return JNI_FALSE;
  if (!LOAD_FID(env, FID_Short_value, classShort, "value", "S"))
    return JNI_FALSE;
  if (!LOAD_FID(env, FID_Character_value, classCharacter, "value", "C"))
    return JNI_FALSE;
  if (!LOAD_FID(env, FID_Integer_value, classInteger, "value", "I"))
    return JNI_FALSE;
  if (!LOAD_FID(env, FID_Long_value, classLong, "value", "J"))
    return JNI_FALSE;
  if (!LOAD_FID(env, FID_Float_value, classFloat, "value", "F"))
    return JNI_FALSE;
  if (!LOAD_FID(env, FID_Double_value, classDouble, "value", "D"))
    return JNI_FALSE;

  return JNI_TRUE;
}

/*
 * Class:     Pointer
 * Method:    write
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3BII
    (JNIEnv *env, jobject self, jint boff, jbyteArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->GetByteArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     Pointer
 * Method:    write
 * Signature: (I[CII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3CII
    (JNIEnv *env, jobject self, jint boff, jcharArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->GetCharArrayRegion(env, arr, off, n, (jchar *)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    write
 * Signature: (I[DII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3DII
    (JNIEnv *env, jobject self, jint boff, jdoubleArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->GetDoubleArrayRegion(env, arr, off, n, (jdouble*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    write
 * Signature: (I[FII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3FII
    (JNIEnv *env, jobject self, jint boff, jfloatArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->GetFloatArrayRegion(env, arr, off, n, (jfloat*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    write
 * Signature: (I[III)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3III
    (JNIEnv *env, jobject self, jint boff, jintArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->GetIntArrayRegion(env, arr, off, n, (jint*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    write
 * Signature: (I[JII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3JII
    (JNIEnv *env, jobject self, jint boff, jlongArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->GetLongArrayRegion(env, arr, off, n, (jlong*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    write
 * Signature: (I[SII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3SII
    (JNIEnv *env, jobject self, jint boff, jshortArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->GetShortArrayRegion(env, arr, off, n, (jshort*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    indexOf
 * Signature: (IB)I
 */
JNIEXPORT jint JNICALL Java_com_sun_jna_Pointer_indexOf__IB
    (JNIEnv *env, jobject self, jint boff, jbyte value)
{
  jbyte *peer = (jbyte *)getNativeAddress(env, self) + boff;
  int i = 0;
  while (i >= 0) {
    if (peer[i] == value)
      return i;
    ++i;
  }
  return -1;
}

/*
 * Class:     Pointer
 * Method:    read
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3BII
    (JNIEnv *env, jobject self, jint boff, jbyteArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->SetByteArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     Pointer
 * Method:    read
 * Signature: (I[CII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3CII
    (JNIEnv *env, jobject self, jint boff, jcharArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->SetCharArrayRegion(env, arr, off, n, (jchar*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    read
 * Signature: (I[DII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3DII
    (JNIEnv *env, jobject self, jint boff, jdoubleArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->SetDoubleArrayRegion(env, arr, off, n, (jdouble*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    read
 * Signature: (I[FII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3FII
    (JNIEnv *env, jobject self, jint boff, jfloatArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->SetFloatArrayRegion(env, arr, off, n, (jfloat*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    read
 * Signature: (I[III)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3III
    (JNIEnv *env, jobject self, jint boff, jintArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->SetIntArrayRegion(env, arr, off, n, (jint*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    read
 * Signature: (I[JII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3JII
    (JNIEnv *env, jobject self, jint boff, jlongArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->SetLongArrayRegion(env, arr, off, n, (jlong*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    read
 * Signature: (I[SII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3SII
    (JNIEnv *env, jobject self, jint boff, jshortArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    (*env)->SetShortArrayRegion(env, arr, off, n, (jshort*)(peer + boff));
}

/*
 * Class:     Pointer
 * Method:    getByte
 * Signature: (I)B
 */
JNIEXPORT jbyte JNICALL Java_com_sun_jna_Pointer_getByte
    (JNIEnv *env, jobject self, jint offset)
{
    jbyte res;
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(&res, peer + offset, sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    getChar
 * Signature: (I)C
 */
JNIEXPORT jchar JNICALL Java_com_sun_jna_Pointer_getChar
    (JNIEnv *env, jobject self, jint offset)
{
    wchar_t res;
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(&res, peer + offset, sizeof(res));
    return (jchar)res;
}

/*
 * Class:     Pointer
 * Method:    getPointer
 * Signature: (I)LPointer;
 */
JNIEXPORT jobject JNICALL Java_com_sun_jna_Pointer_getPointer
    (JNIEnv *env, jobject self, jint offset)
{
    void *ptr;
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(&ptr, peer + offset, sizeof(ptr));
    return newJavaPointer(env, ptr);
}

/*
 * Class:     com_sun_jna_Pointer
 * Method:    getDirectByteBuffer
 * Signature: (II)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_sun_jna_Pointer_getDirectByteBuffer
    (JNIEnv *env, jobject self, jint offset, jint length)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    return (*env)->NewDirectByteBuffer(env, peer + offset, length);
}

/*
 * Class:     Pointer
 * Method:    getDouble
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_com_sun_jna_Pointer_getDouble
    (JNIEnv *env, jobject self, jint offset)
{
    jdouble res;
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(&res, peer + offset, sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    getFloat
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_com_sun_jna_Pointer_getFloat
    (JNIEnv *env, jobject self, jint offset)
{
    jfloat res;
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(&res, peer + offset, sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    getInt
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_sun_jna_Pointer_getInt
    (JNIEnv *env, jobject self, jint offset)
{
    jint res;
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(&res, peer + offset, sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    getLong
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_jna_Pointer_getLong
    (JNIEnv *env, jobject self, jint offset)
{
    jlong res;
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(&res, peer + offset, sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    getShort
 * Signature: (I)S
 */
JNIEXPORT jshort JNICALL Java_com_sun_jna_Pointer_getShort
    (JNIEnv *env, jobject self, jint offset)
{
    jshort res;
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(&res, peer + offset, sizeof(res));
    return res;
}

/*
 * Class:     Pointer
 * Method:    getString
 * Signature: (IB)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_sun_jna_Pointer_getString
    (JNIEnv *env, jobject self, jint offset, jboolean wide)
{
    char *peer = (char *)getNativeAddress(env, self);
    return newJavaString(env, peer + offset, wide);
}

/*
 * Class:     Pointer
 * Method:    setByte
 * Signature: (IB)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setByte
    (JNIEnv *env, jobject self, jint offset, jbyte value)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(peer + offset, &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    setPointer
 * Signature: (ILPointer;)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setPointer
    (JNIEnv *env, jobject self, jint offset, jobject value)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    void *ptr = value ? getNativeAddress(env, value) : NULL;
    MEMCPY(peer + offset, &ptr, sizeof(void *));
}

/*
 * Class:     Pointer
 * Method:    setDouble
 * Signature: (ID)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setDouble
    (JNIEnv *env, jobject self, jint offset, jdouble value)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(peer + offset, &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    setFloat
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setFloat
    (JNIEnv *env, jobject self, jint offset, jfloat value)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(peer + offset, &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    setInt
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setInt
    (JNIEnv *env, jobject self, jint offset, jint value)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(peer + offset, &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    setLong
 * Signature: (IJ)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setLong
    (JNIEnv *env, jobject self, jint offset, jlong value)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(peer + offset, &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    setShort
 * Signature: (IS)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setShort
    (JNIEnv *env, jobject self, jint offset, jshort value)
{
    jbyte *peer = (jbyte *)getNativeAddress(env, self);
    MEMCPY(peer + offset, &value, sizeof(value));
}

/*
 * Class:     Pointer
 * Method:    setString
 * Signature: (ILjava/lang/String;B)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setString
    (JNIEnv *env, jobject self, jint offset, jstring value, jboolean wide)
{
    char *peer = (char *)getNativeAddress(env, self);
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
      MEMCPY(peer + offset, str, size);
      free(str);
    }
}


/*
 * Class:     Memory
 * Method:    malloc
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_jna_Memory_malloc
    (JNIEnv *env, jclass cls, jint size)
{
    return A2L(malloc(size));
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
            throwByName(env, "java/lang/OutOfMemoryError", 0);
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
            throwByName(env, "java/lang/OutOfMemoryError", 0);
            (*env)->DeleteLocalRef(env, chars);
            return NULL;
        }
        // TODO: ensure proper encoding conversion from jchar to native wchar_t
        if (sizeof(jchar) == sizeof(wchar_t)) {
            (*env)->GetCharArrayRegion(env, chars, 0, len, (jchar*)result);
        }
        else {
            int i;
            jchar* buf = (jchar *)alloca(len * sizeof(jchar));
            (*env)->GetCharArrayRegion(env, chars, 0, len, buf);
            for (i=0;i < len;i++) {
                result[i] = buf[i];
            }
        }
        result[len] = 0; /* NUL-terminate */
    }
    (*env)->DeleteLocalRef(env, chars);
    return result;
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
        int len = wcslen((const wchar_t*)ptr);
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
        int len = strlen(ptr);

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
  // TODO: allow Pointer type or Structure type only
  // it'd be nice to proxy the method call and do the replacement there
  return 'L';
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
 * Method:    pointerSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_com_sun_jna_Native_pointerSize(JNIEnv *env, jclass cls)
{
  return sizeof(void *);
}

/*
 * Class:     Native
 * Method:    longSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_com_sun_jna_Native_longSize(JNIEnv *env, jclass cls) {
  return sizeof(long);
}

/*
 * Class:     Native
 * Method:    wideCharSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_com_sun_jna_Native_wideCharSize(JNIEnv *env, jclass cls) {
  return sizeof(wchar_t);
}

/** Initialize com.sun.jna classes separately from the library load to
 * avoid initialization inconsistencies.
 */
JNIEXPORT void JNICALL 
Java_com_sun_jna_Native_initIDs(JNIEnv *env, jclass cls) {
  if (!LOAD_CREF(env, Pointer, "com/sun/jna/Pointer")) {
    throwByName(env, "java/lang/UnsatisfiedLinkError",
                "Can't obtain class com.sun.jna.Pointer");
  }
  else if (!LOAD_REF(env, classPointer)) {
    throwByName(env, "java/lang/UnsatisfiedLinkError",
                "Can't obtain a reference to class com.sun.jna.Pointer");
  }
  else if (!LOAD_MID(env, MID_Pointer_init, classPointer,
                     "<init>", "(J)V")) {
    throwByName(env, "java/lang/UnsatisfiedLinkError",
                "Can't obtain constructor for class com.sun.jna.Pointer");
  }
  else if (!LOAD_FID(env, FID_Pointer_peer, classPointer, "peer", "J")) {
    throwByName(env, "java/lang/UnsatisfiedLinkError",
                "Can't obtain peer field ID for class com.sun.jna.Pointer");
  }
}
  
JNIEXPORT jlong JNICALL
Java_com_sun_jna_Native_getWindowHandle0(JNIEnv *env, jclass classp, jobject w) {
  jlong handle = 0;
  JAWT_DrawingSurface* ds;
  JAWT_DrawingSurfaceInfo* dsi;
  jint lock;
  
  if (!jawt_initialized) {
    if (!init_jawt(env)) {
      throwByName(env, "java/lang/UnsatisfiedLinkError",
                  "Can't initialize JAWT");
      return 0;
    }
    jawt_initialized = JNI_TRUE;
  }

  ds = awt.GetDrawingSurface(env, w);
  if (ds == NULL) {
    throwByName(env, "java/lang/Error", "Can't get drawing surface");
  }
  else {
    lock = ds->Lock(ds);
    if ((lock & JAWT_LOCK_ERROR) != 0) {
      throwByName(env, "java/lang/Error", "Can't get drawing surface lock");
      awt.FreeDrawingSurface(ds);
      return 0;
    }
    dsi = ds->GetDrawingSurfaceInfo(ds);
    if (dsi == NULL) {
        throwByName(env, "java/lang/Error",
                    "Can't get drawing surface info");
    }
    else {
#ifdef _WIN32
      JAWT_Win32DrawingSurfaceInfo* wdsi = 
        (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
      if (wdsi != NULL) {
        // FIXME this kills the VM if the window is not realized;
        // if not, wdsi might be a bogus, non-null value
        // TODO: fix JVM (right) or ensure window is realized
        handle = (jint)wdsi->hwnd;
        if (!handle) {
          throwByName(env, "java/lang/IllegalStateException",
                      "Can't get HWND");
        }
      }
      else {
        throwByName(env, "java/lang/Error", "Can't get w32 platform info");
      }
#elif __APPLE__
      // WARNING: the view ref is not guaranteed to be stable except during
      // component paint (see jni_md.h)
      JAWT_MacOSXDrawingSurfaceInfo* mdsi = 
        (JAWT_MacOSXDrawingSurfaceInfo*)dsi->platformInfo;
      if (mdsi != NULL) {
        handle = (unsigned long)mdsi->cocoaViewRef;
        if (!handle) {
          throwByName(env, "java/lang/IllegalStateException",
                      "Can't get Cocoa View");
        }
      }
      else {
        throwByName(env, "java/lang/UnsupportedOperationException",
                    "Native window handle access not supported on this platform");
      }
#else 
      JAWT_X11DrawingSurfaceInfo* xdsi =
        (JAWT_X11DrawingSurfaceInfo*)dsi->platformInfo;
      if (xdsi != NULL) {
        handle = xdsi->drawable;
        if (!handle) {
          throwByName(env, "java/lang/IllegalStateException",
                      "Can't get Drawable");
        }
      }
      else {
        throwByName(env, "java/lang/Error", "Can't get X11 platform info");
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
    throwByName(env,"java/lang/IllegalArgumentException",
                "Non-direct Buffer is not supported");
  }
  return newJavaPointer(env, addr);
}

JNIEXPORT void JNICALL
Java_com_sun_jna_Native_setProtected(JNIEnv *env, jclass classp, jboolean protect_access) {
#ifdef HAVE_PROTECTION
  protect = protect_access;
#endif
}

JNIEXPORT jboolean JNICALL
Java_com_sun_jna_Native_isProtected(JNIEnv *env, jclass classp) {
#ifdef HAVE_PROTECTION
  return protect ? JNI_TRUE : JNI_FALSE;
#else
  return JNI_FALSE;
#endif
}

static jboolean 
init_jawt(JNIEnv* env) {
  // NOTE: the canonical way to obtain a reference to a native
  // window handle involves loading JAWT, but unfortunately it won't load
  // easily on linux post-1.4 VMs due to a bug loading libmawt.so.  We'd
  // prefer pure-java access to native window IDs, but that isn't available
  // through WindowPeer until 1.6. 
#ifndef NEED_JAWT_HACK
  awt.version = JAWT_VERSION_1_4;
  if (!JAWT_GetAWT(env, &awt))
    return JNI_FALSE;
#else
  // Hackery to work around 1.5/1.6 bug linking directly to jawt.
  // Dynamically look up the function to avoid linkage errors on X11-based
  // platforms.
  // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6539705
  // The suggested workaround is to call System.loadLibrary("awt")
  // prior to loading the user library, but that fails when headless
  jboolean (JNICALL *get_jawt)(JNIEnv*,JAWT*);
  jstring JAVA_HOME = (*env)->NewStringUTF(env, "java.home");
  jstring OS_ARCH = (*env)->NewStringUTF(env, "os.arch");
  jclass system = (*env)->FindClass(env, "java/lang/System");
  if (!system) {
    throwByName(env, "java/lang/UnsatisfiedLinkError", 
                "Can't load java.lang.System");
    return 0;
  }
  jmethodID get_property =
    (*env)->GetStaticMethodID(env, system, "getProperty",
                              "(Ljava/lang/String;)Ljava/lang/String;");
  if (!get_property) {
    throwByName(env, "java/lang/UnsatisfiedLinkError",
                "Can't load java.lang.System.getProperty");
    return 0;
  }

  jstring java_home =
    (*env)->CallStaticObjectMethod(env, system, get_property, JAVA_HOME);
  jstring os_arch =
    (*env)->CallStaticObjectMethod(env, system, get_property, OS_ARCH);
  char* path = newCString(env, java_home);
  char* arch = newCString(env, os_arch);
  char* buf = alloca(strlen(path) + 1024);
  void* mawt = NULL;
  void* jawt = NULL;
  const char* PATHS[] = {
    "%s/lib/%s/xawt/libmawt.so",
    "%s/lib/%s/motif21/libmawt.so",
    "%s/lib/%s/headless/libmawt.so",
  };
  unsigned i;
  
  // First try in the JRE, then try the paths w/mawt
  sprintf(buf, "%s/lib/%s/libjawt.so", path, arch);
  jawt = LOAD_LIBRARY(buf);
  if (!jawt) {
    for (i=0;i < sizeof(PATHS)/sizeof(PATHS[0]);i++) {
      sprintf(buf, PATHS[i], path, arch);
      mawt = LOAD_LIBRARY(buf);
      if (mawt) {
        sprintf(buf, "%s/lib/%s/libjawt.so", path, arch);
        jawt = LOAD_LIBRARY(buf);
        if (jawt) 
          break;
        FREE_LIBRARY(mawt);
        mawt = NULL;
      }
    }
  }
  free(path);
  free(arch);
  if (!jawt) {
    throwByName(env, "java/lang/UnsatisfiedLinkError", dlerror());
    return JNI_FALSE;
  }
  get_jawt = (void *)FIND_ENTRY(jawt, "JAWT_GetAWT");
  if (!get_jawt) {
    throwByName(env, "java/lang/UnsatisfiedLinkError", dlerror());
    return JNI_FALSE;
  }
  awt.version = JAWT_VERSION_1_4;
  if (!get_jawt(env, &awt)) {
    throwByName(env, "java/lang/UnsatisfiedLinkError", 
                "Could not initialize JAWT");
    return JNI_FALSE;
  }

#endif // NEED_JAWT_HACK

  return JNI_TRUE;
}

// FIXME figure out the data layout FFI wants in the result pointer; may
// be affected by what is told to FFI about the return type size
void
extract_value(JNIEnv* env, jobject value, void* resp) {
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
  else if ((*env)->IsInstanceOf(env, value, classPointer)) {
    *(void **)resp = getNativeAddress(env, value);
  }
  else {
    *(void **)resp = value;
  }
}

jobject
new_object(JNIEnv* env, char jtype, void* valuep) {
    switch(jtype) {
    case 'L': 
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
    default:
      return (*env)->NewObject(env, classInteger, MID_Integer_init,
                               *(int *)valuep);
    }
}

JNIEXPORT jint JNICALL 
JNI_OnLoad(JavaVM *jvm, void *reserved) {
  JNIEnv* env;
  int result = JNI_VERSION_1_4;
  int attached = (*jvm)->GetEnv(jvm, (void *)&env, JNI_VERSION_1_4) == JNI_OK;
  if (!attached) {
    if ((*jvm)->AttachCurrentThread(jvm, (void *)&env, NULL) == JNI_OK) {
      fprintf(stderr, "JNA: Can't attach to current thread\n");
      return 0;
    }
  }

  if (!jnidispatch_init(env)
      || !jnidispatch_callback_init(env)) {
    result = 0;
  }

  if (!attached) {
    (*jvm)->DetachCurrentThread(jvm);
  }

  return result;
}

JNIEXPORT void JNICALL 
JNI_OnUnload(JavaVM *vm, void *reserved) {
  // empty
}

#ifdef __cplusplus
}
#endif
