/*
 * @(#)dispatch.c       1.9 98/03/22
 * 
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * See also the LICENSE file in this distribution.
 */

/*
 * JNI native methods supporting the infrastructure for shared
 * dispatchers.  
 */

#if defined(_WIN32)
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
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
#include <string.h>
#include <wchar.h>
#include <jni.h>

// NOTE: while this is the canonical way to obtain a reference to a native
// window handle, it won't load easily on linux post-1.4 VMs due to 
// a bug loading libmawt.so.  We'd prefer pure-java access to native window
// IDs, but that isn't available through WindowPeer until 1.6.
#include <jawt.h>
// OSX needs to compile as objc for this to work; don't do that until
// OSX actually needs or can use a native window ID (NSView*)
#ifndef __APPLE__
#include <jawt_md.h>
#endif

static JAWT awt;
static int jawt_initialized;

#include "dispatch.h"
#include "com_sun_jna_Pointer.h"
#include "com_sun_jna_Memory.h"
#include "com_sun_jna_Function.h"
#include "com_sun_jna_Native.h"
#include "com_sun_jna_NativeLibrary.h"

#ifdef __cplusplus
extern "C"
#endif

/* Cached class, field and method IDs */
static jclass classObject;
static jclass classClass;
static jclass classMethod;
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
static jclass classByteBuffer;

static jmethodID MID_getClass;
static jmethodID MID_Class_getComponentType;
static jmethodID MID_String_getBytes;
static jmethodID MID_String_toCharArray;
static jmethodID MID_String_init_bytes;
static jmethodID MID_Pointer_init;
static jmethodID MID_Method_getReturnType;
static jmethodID MID_Method_getParameterTypes;

static jfieldID FID_Byte_value;
static jfieldID FID_Short_value;
static jfieldID FID_Integer_value;
static jfieldID FID_Long_value;
static jfieldID FID_Float_value;
static jfieldID FID_Double_value;
static jfieldID FID_Pointer_peer;

/* Forward declarations */
static char* newCString(JNIEnv *env, jstring jstr);
static wchar_t* newWideCString(JNIEnv *env, jstring jstr);
static jstring newJavaString(JNIEnv *env, const char *str, jboolean wide);

/* A CPU-dependent assembly routine that passes the arguments to C
 * stack and invoke the function.
 */
extern void asm_dispatch(void (*func)(), int nwords, word_t *c_args, 
                         type_t rt, jvalue *resP, int* arg_types);
static char getArrayComponentType(JNIEnv *, jobject);
static void *getNativeAddress(JNIEnv *, jobject);
static jboolean init_jawt(JNIEnv*);

/* invoke the real native function */
static void dispatch(JNIEnv *env, jobject self, jint callconv, 
                     jobjectArray arr, 
                     type_t rt, jvalue *resP)
{
    int i, nargs, nwords;
    void *func;
    word_t c_args[MAX_NARGS * 2];
    int arg_types[MAX_NARGS];
    char array_pt;
    struct _array_elements {
      char type;
      jobject array;
      void *elems;
    } array_elements[MAX_NARGS];
    int array_count = 0;

    nargs = (*env)->GetArrayLength(env, arr);
    if (nargs > MAX_NARGS) {
      throwByName(env,"java/lang/IllegalArgumentException",
                  "Too many arguments (max 32)");
      return;
    }

    // Get the function pointer
    func = getNativeAddress(env, self);

    for (nwords = 0, i = 0; i < nargs; i++) {
      jobject arg = (*env)->GetObjectArrayElement(env, arr, i);
      
      if (arg == NULL) {
        arg_types[i] = TYPE_PTR;
        c_args[nwords].p = NULL;
        nwords += sizeof(void *) / sizeof(word_t); 
      }
      else if ((*env)->IsInstanceOf(env, arg, classByte)) {
        arg_types[i] = TYPE_INT32;
        c_args[nwords++].i = (*env)->GetByteField(env, arg, FID_Byte_value);
      }
      else if ((*env)->IsInstanceOf(env, arg, classShort)) {
        arg_types[i] = TYPE_INT32;
        c_args[nwords++].i = (*env)->GetShortField(env, arg, FID_Short_value);
      }
      else if ((*env)->IsInstanceOf(env, arg, classInteger)) {
        arg_types[i] = TYPE_INT32;
        c_args[nwords++].i = (*env)->GetIntField(env, arg, FID_Integer_value);
      }
      else if ((*env)->IsInstanceOf(env, arg, classLong)) {
        arg_types[i] = TYPE_INT64;
        *(jlong *)(c_args + nwords) = 
          (*env)->GetLongField(env, arg, FID_Long_value);
        nwords += sizeof(jlong) / sizeof(word_t);
      }
      else if ((*env)->IsInstanceOf(env, arg, classFloat)) {
        arg_types[i] = TYPE_FP32;
        c_args[nwords++].f = (*env)->GetFloatField(env, arg, FID_Float_value);
      }
      else if ((*env)->IsInstanceOf(env, arg, classDouble)) {
        arg_types[i] = TYPE_FP64;
        *(jdouble *)(c_args + nwords) = 
          (*env)->GetDoubleField(env, arg, FID_Double_value);
        nwords += sizeof(jdouble) / sizeof(word_t);
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
        arg_types[i] = TYPE_PTR;
        c_args[nwords++].p = ptr;
        array_elements[array_count].type = array_pt;
        array_elements[array_count].array = arg;
        array_elements[array_count++].elems = ptr;
      }
      else if ((*env)->IsInstanceOf(env, arg, classPointer)) {
        arg_types[i] = TYPE_PTR;
        c_args[nwords].p = getNativeAddress(env, arg);
        nwords += sizeof(void *)/sizeof(word_t);
      }
      else if ((*env)->IsInstanceOf(env, arg, classByteBuffer)) {
        arg_types[i] = TYPE_PTR;
        c_args[nwords].p = (*env)->GetDirectBufferAddress(env, arg);
        if (c_args[nwords].p == NULL) {
          // TODO: treat as byte[]?
          throwByName(env,"java/lang/IllegalArgumentException",
                      "Non-direct ByteBuffer is not supported");
          goto cleanup;
        }
        nwords += sizeof(void *)/sizeof(word_t);
      }
      else {
        throwByName(env,"java/lang/IllegalArgumentException",
                    "Unrecognized argument type");
        goto cleanup;
      }
    }

    switch (callconv) {
    case CALLCONV_C:
      asm_dispatch(func, nwords, c_args, rt, resP, arg_types);
      break;
      
#if defined(_WIN32)
    case CALLCONV_STDCALL:
      asm_dispatch(func, nwords, c_args, rt, resP, NULL);
      break;
#endif // _WIN32
      
    default:
      throwByName(env,"java/lang/IllegalArgumentException","Unrecognized call type");
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
    dispatch(env, self, callconv, arr, TYPE_PTR, &result);
    if ((*env)->ExceptionCheck(env)) {
        return NULL;
    }
    return newJavaPointer(env, L2A(result.j));
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
    dispatch(env, self, callconv, arr, TYPE_FP64, &result);
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
    dispatch(env, self, callconv, arr, TYPE_FP32, &result);
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
    dispatch(env, self, callconv, arr, TYPE_INT32, &result);
    return result.i;
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
    dispatch(env, self, callconv, arr, TYPE_INT64, &result);
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
    dispatch(env, self, callconv, arr, TYPE_INT32, &result);
}

JNIEXPORT jobject JNICALL
Java_com_sun_jna_Function_createCallback(JNIEnv *env, jclass functionClass,
                                         jobject obj, jobject method,
                                         jobjectArray param_types,
                                         jclass return_type,
                                         jint call_conv) {
  callback* cb =
    create_callback(env, obj, method, param_types, return_type, call_conv);
  if (cb != NULL) {
    return newJavaPointer(env, cb);
  }
  return NULL;
}

JNIEXPORT void JNICALL
Java_com_sun_jna_Function_freeCallback(JNIEnv *env,
                                       jclass functionClass, jlong ptr) {
  callback* cb = (callback*)L2A(ptr);
  (*env)->DeleteWeakGlobalRef(env, cb->object);
  free(cb->insns);
  free(cb);
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

/*
 * Class:     Pointer
 * Method:    initIDs
 * Signature: (LPointer;)I
 */
// TODO: should throw error if any lookup fails
JNIEXPORT jint JNICALL 
Java_com_sun_jna_Pointer_initIDs(JNIEnv *env, jclass cls)
{
    if (!LOAD_CREF(env, Object, "java/lang/Object")) return 0;
    if (!LOAD_CREF(env, Class, "java/lang/Class")) return 0;
    if (!LOAD_CREF(env, Method, "java/lang/reflect/Method")) return 0;
    if (!LOAD_CREF(env, String, "java/lang/String")) return 0;
    if (!LOAD_CREF(env, ByteBuffer, "java/nio/ByteBuffer")) return 0;

    classPointer = cls;
    if (!LOAD_REF(env, classPointer)) return 0;

    if (!LOAD_PCREF(env, Boolean, "java/lang/Boolean")) return 0;
    if (!LOAD_PCREF(env, Byte, "java/lang/Byte")) return 0;
    if (!LOAD_PCREF(env, Character, "java/lang/Character")) return 0;
    if (!LOAD_PCREF(env, Short, "java/lang/Short")) return 0;
    if (!LOAD_PCREF(env, Integer, "java/lang/Integer")) return 0;
    if (!LOAD_PCREF(env, Long, "java/lang/Long")) return 0;
    if (!LOAD_PCREF(env, Float, "java/lang/Float")) return 0;
    if (!LOAD_PCREF(env, Double, "java/lang/Double")) return 0;

    if (!LOAD_MID(env, MID_Pointer_init, classPointer,
                  "<init>", "(J)V"))
      return 0;
    if (!LOAD_MID(env, MID_getClass, classObject,
                  "getClass", "()Ljava/lang/Class;"))
      return 0;
    if (!LOAD_MID(env, MID_Class_getComponentType, classClass,
                  "getComponentType", "()Ljava/lang/Class;"))
      return 0;
    if (!LOAD_MID(env, MID_String_getBytes, classString,
                  "getBytes", "()[B"))
      return 0;
    if (!LOAD_MID(env, MID_String_toCharArray, classString,
                  "toCharArray", "()[C"))
      return 0;
    if (!LOAD_MID(env, MID_String_init_bytes, classString,
                  "<init>", "([B)V"))
      return 0;
    if (!LOAD_MID(env, MID_Method_getParameterTypes, classMethod,
                  "getParameterTypes", "()[Ljava/lang/Class;"))
      return 0;
    if (!LOAD_MID(env, MID_Method_getReturnType, classMethod,
                  "getReturnType", "()Ljava/lang/Class;"))
      return 0;

    if (!LOAD_FID(env, FID_Byte_value, classByte, "value", "B"))
      return 0;
    if (!LOAD_FID(env, FID_Short_value, classShort, "value", "S"))
      return 0;
    if (!LOAD_FID(env, FID_Integer_value, classInteger, "value", "I"))
      return 0;
    if (!LOAD_FID(env, FID_Long_value, classLong, "value", "J"))
      return 0;
    if (!LOAD_FID(env, FID_Float_value, classFloat, "value", "F"))
      return 0;
    if (!LOAD_FID(env, FID_Double_value, classDouble, "value", "D"))
      return 0;
    if (!LOAD_FID(env, FID_Pointer_peer, classPointer, "peer", "J"))
      return 0;

    return sizeof(void *);
}

/*
 * Class:     Pointer
 * Method:    longSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_com_sun_jna_Pointer_longSize(JNIEnv *env, jclass cls) {
  return sizeof(long);
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
    memcpy(&res, peer + offset, sizeof(res));
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
    memcpy(&res, peer + offset, sizeof(res));
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
    memcpy(&ptr, peer + offset, sizeof(ptr));
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
    memcpy(&res, peer + offset, sizeof(res));
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
    memcpy(&res, peer + offset, sizeof(res));
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
    memcpy(&res, peer + offset, sizeof(res));
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
    memcpy(&res, peer + offset, sizeof(res));
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
    memcpy(&res, peer + offset, sizeof(res));
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
    memcpy(peer + offset, &value, sizeof(value));
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
    memcpy(peer + offset, &ptr, sizeof(void *));
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
    memcpy(peer + offset, &value, sizeof(value));
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
    memcpy(peer + offset, &value, sizeof(value));
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
    memcpy(peer + offset, &value, sizeof(value));
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
    memcpy(peer + offset, &value, sizeof(value));
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
    memcpy(peer + offset, &value, sizeof(value));
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
    if (wide) {
        int len = (*env)->GetStringLength(env, value);
        wchar_t* str = newWideCString(env, value);
        if (str == NULL) return;
        memcpy(peer + offset, str, (len + 1) * sizeof(wchar_t));
        free(str);
    }
    else {
        char *str = newCString(env, value);
        if (str == NULL) return;
        strcpy(peer + offset, str);
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
 * method, which uses default local encoding.
 */
// TODO: make sure encoding is correct
static char *
newCString(JNIEnv *env, jstring jstr)
{
    jbyteArray bytes = 0;
    char *result = 0;

    bytes = (*env)->CallObjectMethod(env, jstr, MID_String_getBytes);
    if (!(*env)->ExceptionCheck(env)) {
        jint len = (*env)->GetArrayLength(env, bytes);
        result = (char *)malloc(len + 1);
        if (result == 0) {
            throwByName(env, "java/lang/OutOfMemoryError", 0);
            (*env)->DeleteLocalRef(env, bytes);
            return 0;
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
// TODO: make any required encoding changes
static wchar_t *
newWideCString(JNIEnv *env, jstring str)
{
    jcharArray chars = 0;
    wchar_t *result = 0;

    if ((*env)->ExceptionCheck(env)) {
      return result;
    }

    chars = (*env)->CallObjectMethod(env, str, MID_String_toCharArray);
    if (!(*env)->ExceptionCheck(env)) {
        jint len = (*env)->GetArrayLength(env, chars);
        result = (wchar_t *)malloc(sizeof(wchar_t) * (len + 1));
        if (result == 0) {
            throwByName(env, "java/lang/OutOfMemoryError", 0);
            (*env)->DeleteLocalRef(env, chars);
            return 0;
        }
        // TODO: ensure proper conversion from jchar to native wchar_t
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
  jclass cls = (*env)->CallObjectMethod(env, obj, MID_getClass);
  jclass type = (*env)->CallObjectMethod(env, cls, MID_Class_getComponentType);
  if (type != NULL) {
    return get_jtype(env, type);
  }
  return 0;
}


JNIEXPORT jlong JNICALL
Java_com_sun_jna_Native_getWindowHandle0(JNIEnv *env, jobject unused, jobject w) {
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
        // FIXME this kills the VM if the window is not realized
        // wdsi might be a bogus, non-null value
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
      throwByName(env, "java/lang/UnsupportedOperationException",
                  "Native window handle access not supported on this platform");
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


static jboolean 
init_jawt(JNIEnv* env) {
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

#ifdef __cplusplus
}
#endif

