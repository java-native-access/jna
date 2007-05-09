/*
 * @(#)dispatch.c	1.9 98/03/22
 * 
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * See also the LICENSE file in this distribution.
 */

/*
 * JNI native methods supporting the infrastructure for shared
 * dispatchers.  Includes native methods for classes NativePointer, 
 * NativeFunction, and NativeMemory.
 */

#ifdef SOLARIS2
#include <dlfcn.h>
#define LOAD_LIBRARY(name) dlopen(name, RTLD_LAZY)
#define FIND_ENTRY(lib, name) dlsym(lib, name)
#endif

#ifdef WIN32
#include <windows.h>
#define LOAD_LIBRARY(name) LoadLibrary(name)
#define FIND_ENTRY(lib, name) GetProcAddress(lib, name)
#endif

#include <stdlib.h>
#include <string.h>

#include <jni.h>

#include "com_sun_jna_Pointer.h"
#include "com_sun_jna_Function.h"
#include "com_sun_jna_Memory.h"

/* Global references to frequently used classes and objects */
static jclass classString;
static jclass classInteger;
static jclass classFloat;
static jclass classDouble;
static jclass classNativePointer;
static jobject objectNativePointerNULL;

/* Cached field and method IDs */
static jmethodID String_getBytes_ID;
static jmethodID String_init_ID;
static jfieldID Integer_value_ID;
static jfieldID Float_value_ID;
static jfieldID Double_value_ID;
static jfieldID NativePointer_peer_ID;

/* Forward declarations */
static void throwByName(JNIEnv *env, const char *name, const char *msg);
static char * getJavaString(JNIEnv *env, jstring jstr);
static jstring newJavaString(JNIEnv *env, const char *str);
static jobject makeNativePointer(JNIEnv *env, void *p);


/********************************************************************/
/*		     Native methods of class NativeFunction		  
/********************************************************************/

/* These are the calling conventions NativeFunction can handle now */
typedef enum {
    CALLCONV_C = 0,
    CALLCONV_STDCALL,
} callconv_t;

/* These are the set of types NativeFunction can handle now */
typedef enum {
    TY_CPTR = 0,
    TY_INTEGER,
    TY_FLOAT,
    TY_DOUBLE,
    TY_DOUBLE2,
    TY_STRING,
} ty_t;

/* represent a machine word */
typedef union {
    jint i;
    jfloat f;
    void *p;
} word_t;

/* A CPU-dependent assembly routine that passes the arguments to C
 * stack and invoke the function.
 */
extern void asm_c_dispatch(void *func, int nwords, word_t *c_args, 
	ty_t ty, jvalue *resP);
extern void asm_stdcall_dispatch(void *func, int nwords, word_t *c_args, 
	ty_t ty, jvalue *resP);

/* invoke the real native function */
static void dispatch(JNIEnv *env, jobject self, jint callconv, jobjectArray arr, 
	ty_t ty, jvalue *resP)
{
#define MAX_NARGS 32
    int i, nargs, nwords;
    void *func;
    char argTypes[MAX_NARGS];
    word_t c_args[MAX_NARGS * 2];

    nargs = (*env)->GetArrayLength(env, arr);
    if (nargs > MAX_NARGS) 
    {
        throwByName(env,"java/lang/IllegalArgumentException",
			"Too many arguments (max 32)");
		return;
    }

	// Get the function pointer
    func = (void *)(*env)->GetLongField(env, self, NativePointer_peer_ID);

    for (nwords = 0, i = 0; i < nargs; i++) 
    {
        jobject arg = (*env)->GetObjectArrayElement(env, arr, i);

		if (arg == NULL) 
		{
		    throwByName(env,"java/lang/NullPointerException","bad argument");
		    goto cleanup;
		}

		if ((*env)->IsInstanceOf(env, arg, classInteger)) 
		{
		    c_args[nwords].i = (*env)->GetIntField(env, arg, Integer_value_ID);
		    argTypes[nwords++] = TY_INTEGER;
		}
		else 
		if ((*env)->IsInstanceOf(env, arg, classNativePointer)) 
		{
		    c_args[nwords].p = 
		        (void *)(*env)->GetLongField(env, arg, NativePointer_peer_ID);
		    argTypes[nwords++] = TY_CPTR;
		}
		else
		if ((*env)->IsInstanceOf(env, arg, classString)) 
		{
		    if ((c_args[nwords].p = getJavaString(env, arg)) == 0) 
		    {
		        goto cleanup;
		    }
		    argTypes[nwords++] = TY_STRING;
		} 
		else 
		if ((*env)->IsInstanceOf(env, arg, classFloat)) 
		{
		    c_args[nwords].f = (*env)->GetFloatField(env, arg, Float_value_ID);
		    argTypes[nwords++] = TY_FLOAT;
		}
		else 
		if ((*env)->IsInstanceOf(env, arg, classDouble)) 
		{
		    *(jdouble *)(c_args + nwords) = 
		        (*env)->GetDoubleField(env, arg, Double_value_ID);
		    argTypes[nwords] = TY_DOUBLE;
		    /* harmless with 64-bit machines*/
		    argTypes[nwords + 1] = TY_DOUBLE2;
		    /* make sure things work on 64-bit machines */
		    nwords += sizeof(jdouble) / sizeof(word_t);
		}
		else 
		{
		    throwByName(env,"java/lang/IllegalArgumentException","Unrecognized argument type");
		    goto cleanup;
		}

		(*env)->DeleteLocalRef(env, arg);
    }

	switch (callconv)
	{
		case CALLCONV_C:

			#ifdef DEBUG
			printf("Before dispatch_c\n");
			#endif

		    asm_c_dispatch(func, nwords, c_args, ty, resP);

			#ifdef DEBUG
			printf("After dispatch_c\n");
			#endif

			break;
			
#ifdef WIN32

		case CALLCONV_STDCALL:

			#ifdef DEBUG
			printf("Before dispatch_stdcall\n");
			#endif

		    asm_stdcall_dispatch(func, nwords, c_args, ty, resP);

			#ifdef DEBUG
			printf("After dispatch_stdcall\n");
			#endif

			break;

#endif // WIN32

		default:
		    asm_c_dispatch(func, nwords, c_args, ty, resP);
	}

cleanup:
    for (i = 0; i < nwords; i++) 
	{
        if (argTypes[i] == TY_STRING) 
		{
			free(c_args[i].p);
		}
    }

    return;
}

/*
 * Class:     NativeFunction
 * Method:    invokePointer
 * Signature: ([Ljava/lang/Object;)LNativePointer;
 */
JNIEXPORT jobject JNICALL 
Java_com_sun_jna_Function_invokePointer(JNIEnv *env, jobject self, 
	jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, TY_CPTR, &result);
    if ((*env)->ExceptionOccurred(env)) {
        return NULL;
    }
    return makeNativePointer(env, (void *)result.j);
}

/*
 * Class:     NativeFunction
 * Method:    invokeDouble
 * Signature: ([Ljava/lang/Object;)D
 */
JNIEXPORT jdouble JNICALL 
Java_com_sun_jna_Function_invokeDouble(JNIEnv *env, jobject self, 
	jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, TY_DOUBLE, &result);
    return result.d;
}

/*
 * Class:     NativeFunction
 * Method:    invokeFloat
 * Signature: ([Ljava/lang/Object;)F
 */
JNIEXPORT jfloat JNICALL
Java_com_sun_jna_Function_invokeFloat(JNIEnv *env, jobject self, 
	jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, TY_FLOAT, &result);
    return result.f;
}

/*
 * Class:     NativeFunction
 * Method:    invokeInt
 * Signature: ([Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL
Java_com_sun_jna_Function_invokeInt(JNIEnv *env, jobject self, 
	jint callconv, jobjectArray arr)
{
    jvalue result;
    dispatch(env, self, callconv, arr, TY_INTEGER, &result);
    return result.i;
}

/*
 * Class:     NativeFunction
 * Method:    invokeVoid
 * Signature: ([Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL
Java_com_sun_jna_Function_invokeVoid(JNIEnv *env, jobject self, 
	jint callconv, jobjectArray arr)
{
    jvalue result;

#ifdef DEBUG
	if (callconv==3)
	{
		int i;
		for (i=0; i<1000000; i++)
		    dispatch(env, self, 1, arr, TY_INTEGER, &result);
	}
	else
#endif
	    dispatch(env, self, callconv, arr, TY_INTEGER, &result);
}

/*
 * Class:     NativeFunction
 * Method:    find
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_jna_Function_find
  (JNIEnv *env, jobject self, jstring lib, jstring fun)
{
    void *handle;
    void *func;
    char *libname = 0;
    char *funname = 0;

    if ((libname = getJavaString(env, lib)) == 0) {
        goto ret;
    }
    if ((funname = getJavaString(env, fun)) == 0) {
        goto ret;
    }
    if ((handle = (void *)LOAD_LIBRARY(libname)) == NULL) {
        throwByName(env, "java/lang/UnsatisfiedLinkError", libname);
	goto ret;
    }
    if ((func = (void *)FIND_ENTRY(handle, funname)) == NULL) {
        throwByName(env, "java/lang/UnsatisfiedLinkError", funname);
	goto ret;
    }

 ret:
    free(libname);
    free(funname);
    return (jlong)func;
}


/********************************************************************/
/*		     Native methods of class NativePointer		    */
/********************************************************************/

/*
 * Class:     NativePointer
 * Method:    initIDs
 * Signature: (LNativePointer;)I
 */
JNIEXPORT jint JNICALL 
Java_com_sun_jna_Pointer_initIDs(JNIEnv *env, jclass cls, 
	jobject nullNativePointer)
{
    objectNativePointerNULL = (*env)->NewGlobalRef(env, nullNativePointer);
    if (objectNativePointerNULL == NULL) return 0;

    classString = (*env)->FindClass(env, "java/lang/String");
    if (classString == NULL) return 0;
    classString = (*env)->NewGlobalRef(env, classString);
    if (classString == NULL) return 0;

    classInteger = (*env)->FindClass(env, "java/lang/Integer");
    if (classInteger == NULL) return 0;
    classInteger = (*env)->NewGlobalRef(env, classInteger);
    if (classInteger == NULL) return 0;

    classFloat = (*env)->FindClass(env, "java/lang/Float");
    if (classFloat == NULL) return 0;
    classFloat = (*env)->NewGlobalRef(env, classFloat);
    if (classFloat == NULL) return 0;

    classDouble = (*env)->FindClass(env, "java/lang/Double");
    if (classDouble == NULL) return 0;
    classDouble = (*env)->NewGlobalRef(env, classDouble);
    if (classDouble == NULL) return 0;

    classNativePointer = (*env)->NewGlobalRef(env, cls);
    if (classNativePointer == NULL) return 0;

    String_getBytes_ID = 
        (*env)->GetMethodID(env, classString, "getBytes", "()[B");
    if (String_getBytes_ID == NULL) return 0;
    String_init_ID = (*env)->GetMethodID(env, classString, "<init>", "([B)V");
    if (String_init_ID == NULL) return 0;

    Integer_value_ID = (*env)->GetFieldID(env, classInteger, "value", "I");
    if (Integer_value_ID == NULL) return 0;

    Float_value_ID = (*env)->GetFieldID(env, classFloat, "value", "F");
    if (Float_value_ID == NULL) return 0;

    Double_value_ID = (*env)->GetFieldID(env, classDouble, "value", "D");
    if (Double_value_ID == NULL) return 0;

    NativePointer_peer_ID = (*env)->GetFieldID(env, classNativePointer, "peer", "J");
    return sizeof(void *);
}

/*
 * Class:     com_sun_jna_Pointer
 * Method:    getBoolSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_sun_jna_Pointer_getBoolSize
  (JNIEnv *env, jclass cls)
{
	return sizeof(BOOL);
}


/*
 * Class:     NativePointer
 * Method:    write
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3BII
  (JNIEnv *env, jobject self, jint boff, jbyteArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->GetByteArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    write
 * Signature: (I[CII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3CII
  (JNIEnv *env, jobject self, jint boff, jcharArray arr, jint off, jint n)
{
    jchar *peer = (jchar *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->GetCharArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    write
 * Signature: (I[DII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3DII
  (JNIEnv *env, jobject self, jint boff, jdoubleArray arr, jint off, jint n)
{
    jdouble *peer = (jdouble *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->GetDoubleArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    write
 * Signature: (I[FII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3FII
  (JNIEnv *env, jobject self, jint boff, jfloatArray arr, jint off, jint n)
{
    jfloat *peer = (jfloat *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->GetFloatArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    write
 * Signature: (I[III)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3III
  (JNIEnv *env, jobject self, jint boff, jintArray arr, jint off, jint n)
{
    jint *peer = (jint *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->GetIntArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    write
 * Signature: (I[JII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3JII
  (JNIEnv *env, jobject self, jint boff, jlongArray arr, jint off, jint n)
{
    jlong *peer = (jlong *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->GetLongArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    write
 * Signature: (I[SII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_write__I_3SII
  (JNIEnv *env, jobject self, jint boff, jshortArray arr, jint off, jint n)
{
    jshort *peer = (jshort *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->GetShortArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    read
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3BII
  (JNIEnv *env, jobject self, jint boff, jbyteArray arr, jint off, jint n)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->SetByteArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    read
 * Signature: (I[CII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3CII
  (JNIEnv *env, jobject self, jint boff, jcharArray arr, jint off, jint n)
{
    jchar *peer = (jchar *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->SetCharArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    read
 * Signature: (I[DII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3DII
  (JNIEnv *env, jobject self, jint boff, jdoubleArray arr, jint off, jint n)
{
    jdouble *peer = (jdouble *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->SetDoubleArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    read
 * Signature: (I[FII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3FII
  (JNIEnv *env, jobject self, jint boff, jfloatArray arr, jint off, jint n)
{
    jfloat *peer = (jfloat *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->SetFloatArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    read
 * Signature: (I[III)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3III
  (JNIEnv *env, jobject self, jint boff, jintArray arr, jint off, jint n)
{
    jint *peer = (jint *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->SetIntArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    read
 * Signature: (I[JII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_read__I_3JII
  (JNIEnv *env, jobject self, jint boff, jlongArray arr, jint off, jint n)
{
    jlong *peer = (jlong *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->SetLongArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    read
 * Signature: (I[SII)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_copyOut__I_3SII
  (JNIEnv *env, jobject self, jint boff, jshortArray arr, jint off, jint n)
{
    jshort *peer = (jshort *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    (*env)->SetShortArrayRegion(env, arr, off, n, peer + boff);
}

/*
 * Class:     NativePointer
 * Method:    getByte
 * Signature: (I)B
 */
JNIEXPORT jbyte JNICALL Java_com_sun_jna_Pointer_getByte
  (JNIEnv *env, jobject self, jint index)
{
    jbyte res;
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(&res, peer + index, sizeof(res));
    return res;
}

/*
 * Class:     NativePointer
 * Method:    getPointer
 * Signature: (I)LNativePointer;
 */
JNIEXPORT jobject JNICALL Java_com_sun_jna_Pointer_getPointer
  (JNIEnv *env, jobject self, jint index)
{
    void *ptr;
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(&ptr, peer + index, sizeof(ptr));
    return makeNativePointer(env, ptr);
}

/*
 * Class:     NativePointer
 * Method:    getDouble
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_com_sun_jna_Pointer_getDouble
  (JNIEnv *env, jobject self, jint index)
{
    jdouble res;
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(&res, peer + index, sizeof(res));
    return res;
}

/*
 * Class:     NativePointer
 * Method:    getFloat
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_com_sun_jna_Pointer_getFloat
  (JNIEnv *env, jobject self, jint index)
{
    jfloat res;
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(&res, peer + index, sizeof(res));
    return res;
}

/*
 * Class:     NativePointer
 * Method:    getInt
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_sun_jna_Pointer_getInt
  (JNIEnv *env, jobject self, jint index)
{
    jint res;
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(&res, peer + index, sizeof(res));
    return res;
}

/*
 * Class:     NativePointer
 * Method:    getLong
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_jna_Pointer_getLong
  (JNIEnv *env, jobject self, jint index)
{
    jlong res;
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(&res, peer + index, sizeof(res));
    return res;
}

/*
 * Class:     NativePointer
 * Method:    getShort
 * Signature: (I)S
 */
JNIEXPORT jshort JNICALL Java_com_sun_jna_Pointer_getShort
  (JNIEnv *env, jobject self, jint index)
{
    jshort res;
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(&res, peer + index, sizeof(res));
    return res;
}

/*
 * Class:     NativePointer
 * Method:    getString
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_sun_jna_Pointer_getString
  (JNIEnv *env, jobject self, jint index)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    return newJavaString(env, (const char *)peer + index);
}

/*
 * Class:     NativePointer
 * Method:    setByte
 * Signature: (IB)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setByte
  (JNIEnv *env, jobject self, jint index, jbyte value)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(peer + index, &value, sizeof(value));
}

/*
 * Class:     NativePointer
 * Method:    setPointer
 * Signature: (ILNativePointer;)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setPointer
  (JNIEnv *env, jobject self, jint index, jobject cptr)
{
    void *ptr = (void *)(*env)->GetLongField(env, cptr, NativePointer_peer_ID);
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(peer + index, &ptr, sizeof(ptr));
}

/*
 * Class:     NativePointer
 * Method:    setDouble
 * Signature: (ID)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setDouble
  (JNIEnv *env, jobject self, jint index, jdouble value)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(peer + index, &value, sizeof(value));
}

/*
 * Class:     NativePointer
 * Method:    setFloat
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setFloat
  (JNIEnv *env, jobject self, jint index, jfloat value)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(peer + index, &value, sizeof(value));
}

/*
 * Class:     NativePointer
 * Method:    setInt
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setInt
  (JNIEnv *env, jobject self, jint index, jint value)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(peer + index, &value, sizeof(value));
}

/*
 * Class:     NativePointer
 * Method:    setLong
 * Signature: (IJ)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setLong
  (JNIEnv *env, jobject self, jint index, jlong value)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(peer + index, &value, sizeof(value));
}

/*
 * Class:     NativePointer
 * Method:    setShort
 * Signature: (IS)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setShort
  (JNIEnv *env, jobject self, jint index, jshort value)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    memcpy(peer + index, &value, sizeof(value));
}

/*
 * Class:     NativePointer
 * Method:    setString
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Pointer_setString
  (JNIEnv *env, jobject self, jint index, jstring value)
{
    jbyte *peer = (jbyte *)(*env)->GetLongField(env, self, NativePointer_peer_ID);
    char *str = getJavaString(env, value);
    if (str == NULL) return;
    strcpy((char *)peer + index, str);
    free(str);
}


/********************************************************************/
/*		     Native methods of class NativeMemory		    */
/********************************************************************/

/*
 * Class:     NativeMemory
 * Method:    malloc
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_jna_Memory_malloc
  (JNIEnv *env, jclass cls, jint size)
{
    return (jlong)malloc(size);
}

/*
 * Class:     NativeMemory
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_sun_jna_Memory_free
  (JNIEnv *env, jclass cls, jlong ptr)
{
    free((void *)ptr);
}


/********************************************************************/
/*			   Utility functions			    */
/********************************************************************/

/* Throw an exception by name */
static void 
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
static char *
getJavaString(JNIEnv *env, jstring jstr)
{
    jbyteArray hab = 0;
    jthrowable exc;
    char *result = 0;

    hab = (*env)->CallObjectMethod(env, jstr, String_getBytes_ID);
    exc = (*env)->ExceptionOccurred(env);
    if (!exc) {
        jint len = (*env)->GetArrayLength(env, hab);
        result = (char *)malloc(len + 1);
	if (result == 0) {
	    throwByName(env, "java/lang/OutOfMemoryError", 0);
	    (*env)->DeleteLocalRef(env, hab);
	    return 0;
	}
	(*env)->GetByteArrayRegion(env, hab, 0, len, (jbyte *)result);
	result[len] = 0; /* NULL-terminate */
    } else {
        (*env)->DeleteLocalRef(env, exc);
    }
    (*env)->DeleteLocalRef(env, hab);
    return result;
}

/* Constructs a Java string from a C array using the String(byte [])
 * constructor, which uses default local encoding.
 */
static jstring
newJavaString(JNIEnv *env, const char *str)
{
    jstring result;
    jbyteArray hab = 0;
    int len;

    len = strlen(str);
    hab = (*env)->NewByteArray(env, len);
    if (hab != 0) {
        (*env)->SetByteArrayRegion(env, hab, 0, len, (jbyte *)str);
	result = (*env)->NewObject(env, classString,
				   String_init_ID, hab);
	(*env)->DeleteLocalRef(env, hab);
	return result;
    }
    return 0;
}

/* Canonicalize NULL pointers */
static jobject makeNativePointer(JNIEnv *env, void *p)
{
    jobject obj;
    if (p == NULL) {
        return objectNativePointerNULL;
    }
    obj = (*env)->AllocObject(env, classNativePointer);
    if (obj) {
        (*env)->SetLongField(env, obj, NativePointer_peer_ID, (jlong)p);
    }
    return obj;
}
