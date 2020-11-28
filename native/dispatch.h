/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
#ifndef DISPATCH_H
#define DISPATCH_H

#define MSG_SIZE 1024

#include "ffi.h"
#include "com_sun_jna_Function.h"
#include "com_sun_jna_Native.h"
#if defined(__sun__) || defined(_AIX) || defined(__linux__)
#  include <alloca.h>
#endif
#ifdef _WIN32
#ifdef _MSC_VER
#define alloca _alloca
#pragma warning( disable : 4152 ) /* function/data conversion */
#pragma warning( disable : 4054 ) /* cast function pointer to data pointer */
#pragma warning( disable : 4055 ) /* cast data pointer to function pointer */
#pragma warning( disable : 4204 ) /* structure initializer */
#pragma warning( disable : 4710 ) /* swprintf not inlined */
#pragma warning( disable : 4201 ) /* nameless struct/union (jni_md.h) */
#else
#include <malloc.h>
#endif /* _MSC_VER */
#define GET_LAST_ERROR() GetLastError()
#define SET_LAST_ERROR(CODE) SetLastError(CODE)
#else
#ifndef _XOPEN_SOURCE /* AIX power-aix 1 7 00F84C0C4C00 defins 700 */
#define _XOPEN_SOURCE 600
#endif
#define GET_LAST_ERROR() errno
#define SET_LAST_ERROR(CODE) (errno = (CODE))
#endif /* _WIN32 */

#if !defined(UNUSED)
 #if defined(__GNUC__)
  #define UNUSED(x) UNUSED_ ## x __attribute__((unused))
 #elif defined(__LCLINT__)
  #define UNUSED(x) /*@unused@*/ x
 #else
  #define UNUSED(x) x
 #endif
#endif /* !defined(UNUSED) */

#ifdef NO_JAWT
 #define UNUSED_JAWT(X) UNUSED(X)
#else
 #define UNUSED_JAWT(X) X
#endif

#ifdef __cplusplus
extern "C" {
#endif

#define CB_OPTION_DIRECT com_sun_jna_Native_CB_OPTION_DIRECT
#define CB_OPTION_IN_DLL com_sun_jna_Native_CB_OPTION_IN_DLL

/* These are the calling conventions an invocation can handle. */
typedef enum _callconv {
    CALLCONV_C = com_sun_jna_Function_C_CONVENTION,
#ifdef _WIN32
    CALLCONV_STDCALL = com_sun_jna_Function_ALT_CONVENTION,
    CALLCONV_THISCALL = com_sun_jna_Function_THISCALL_CONVENTION,
    CALLCONV_FASTCALL = com_sun_jna_Function_FASTCALL_CONVENTION,
#endif
} callconv_t;

/* Maximum number of allowed arguments in libffi. */
#define MAX_NARGS com_sun_jna_Function_MAX_NARGS

enum {
  CVT_DEFAULT = com_sun_jna_Native_CVT_DEFAULT,
  CVT_POINTER = com_sun_jna_Native_CVT_POINTER,
  CVT_STRING = com_sun_jna_Native_CVT_STRING,
  CVT_STRUCTURE = com_sun_jna_Native_CVT_STRUCTURE,
  CVT_STRUCTURE_BYVAL = com_sun_jna_Native_CVT_STRUCTURE_BYVAL,
  CVT_BUFFER = com_sun_jna_Native_CVT_BUFFER,
  CVT_ARRAY_BYTE = com_sun_jna_Native_CVT_ARRAY_BYTE,
  CVT_ARRAY_SHORT = com_sun_jna_Native_CVT_ARRAY_SHORT,
  CVT_ARRAY_CHAR = com_sun_jna_Native_CVT_ARRAY_CHAR,
  CVT_ARRAY_INT = com_sun_jna_Native_CVT_ARRAY_INT,
  CVT_ARRAY_LONG = com_sun_jna_Native_CVT_ARRAY_LONG,
  CVT_ARRAY_FLOAT = com_sun_jna_Native_CVT_ARRAY_FLOAT,
  CVT_ARRAY_DOUBLE = com_sun_jna_Native_CVT_ARRAY_DOUBLE,
  CVT_ARRAY_BOOLEAN = com_sun_jna_Native_CVT_ARRAY_BOOLEAN,
  CVT_BOOLEAN = com_sun_jna_Native_CVT_BOOLEAN,
  CVT_CALLBACK = com_sun_jna_Native_CVT_CALLBACK,
  CVT_FLOAT = com_sun_jna_Native_CVT_FLOAT,
  CVT_NATIVE_MAPPED = com_sun_jna_Native_CVT_NATIVE_MAPPED,
  CVT_NATIVE_MAPPED_STRING = com_sun_jna_Native_CVT_NATIVE_MAPPED_STRING,
  CVT_NATIVE_MAPPED_WSTRING = com_sun_jna_Native_CVT_NATIVE_MAPPED_WSTRING,
  CVT_WSTRING = com_sun_jna_Native_CVT_WSTRING,
  CVT_INTEGER_TYPE = com_sun_jna_Native_CVT_INTEGER_TYPE,
  CVT_POINTER_TYPE = com_sun_jna_Native_CVT_POINTER_TYPE,
  CVT_TYPE_MAPPER = com_sun_jna_Native_CVT_TYPE_MAPPER,
  CVT_TYPE_MAPPER_STRING = com_sun_jna_Native_CVT_TYPE_MAPPER_STRING,
  CVT_TYPE_MAPPER_WSTRING = com_sun_jna_Native_CVT_TYPE_MAPPER_WSTRING,
  CVT_OBJECT = com_sun_jna_Native_CVT_OBJECT,
  CVT_JNIENV = com_sun_jna_Native_CVT_JNIENV,
};

/* callback behavior flags */
enum {
  CB_HAS_INITIALIZER = com_sun_jna_Native_CB_HAS_INITIALIZER,
};

typedef struct _callback {
  /* CallbackReference.getTrampoline() expects this field at offset 0. */
  void* x_closure;
  /* CallbackReference.setCallbackOptions() expects this field at offset Pointer.SIZE. */
  int behavior_flags;
  ffi_closure* closure;
  ffi_cif cif;
  ffi_cif java_cif;
  ffi_type** arg_types;
  ffi_type** java_arg_types;
  jobject* arg_classes;
  int* conversion_flags;
  int rflag;
  JavaVM* vm;
  jobject object;
  jmethodID methodID;
  char* arg_jtypes;
  jboolean direct;
  size_t fptr_offset;
  void* saved_x_closure;
  const char* encoding;
} callback;

#if defined(SOLARIS2) || defined(__GNUC__)
#if defined(_WIN64)
#define L2A(X) ((void *)(long long)(X))
#define A2L(X) ((jlong)(long long)(X))
#else
#define L2A(X) ((void *)(unsigned long)(X))
#define A2L(X) ((jlong)(unsigned long)(X))
#endif
#endif

#if defined(_MSC_VER)
#include "snprintf.h"
#define STRDUP _strdup
#if defined(_WIN64)
#define L2A(X) ((void *)(X))
#define A2L(X) ((jlong)(X))
#else
#define L2A(X) ((void *)(unsigned long)(X))
#define A2L(X) ((jlong)(unsigned long)(X))
#endif
#else
#include <stdio.h>
#define STRDUP strdup
#endif

/* Convenience macros */
#define LOAD_WEAKREF(ENV,VAR) \
  ((VAR == 0) \
   ? 0 : ((VAR = (*ENV)->NewWeakGlobalRef(ENV, VAR)) == 0 ? 0 : VAR))
#define FIND_CLASS(ENV,SIMPLE,NAME) \
  (class ## SIMPLE = (*ENV)->FindClass(ENV, NAME))
#define FIND_PRIMITIVE_CLASS(ENV,SIMPLE) \
  (classPrimitive ## SIMPLE = (*ENV)->GetStaticObjectField(ENV,class ## SIMPLE,(*ENV)->GetStaticFieldID(ENV,class ## SIMPLE,"TYPE","Ljava/lang/Class;")))
#define LOAD_CREF(ENV,SIMPLE,NAME) \
  (FIND_CLASS(ENV,SIMPLE,NAME) && LOAD_WEAKREF(ENV,class ## SIMPLE))
#define LOAD_PCREF(ENV,SIMPLE,NAME) \
  (LOAD_CREF(ENV,SIMPLE,NAME) \
   && FIND_PRIMITIVE_CLASS(ENV,SIMPLE) \
   && LOAD_WEAKREF(ENV,classPrimitive ## SIMPLE))
#define LOAD_MID(ENV,VAR,CLASS,NAME,SIG) \
   ((VAR = (*ENV)->GetMethodID(ENV, CLASS, NAME, SIG)) ? VAR : 0)
#define LOAD_FID(ENV,VAR,CLASS,NAME,SIG) \
   ((VAR = (*ENV)->GetFieldID(ENV, CLASS, NAME, SIG)) ? VAR : 0)

// Avoid typos in class names
#define EIllegalArgument "java/lang/IllegalArgumentException"
#define EOutOfMemory "java/lang/OutOfMemoryError"
#define EUnsatisfiedLink "java/lang/UnsatisfiedLinkError"
#define EIllegalState "java/lang/IllegalStateException"
#define EUnsupportedOperation "java/lang/UnsupportedOperationException"
#define ERuntime "java/lang/RuntimeException"
#define EError "java/lang/Error"
#define ELastError "com/sun/jna/LastErrorException"

extern void throwByName(JNIEnv *env, const char *name, const char *msg);
extern int get_java_type(JNIEnv*, jclass);
extern ffi_type* get_ffi_type(JNIEnv*, jclass, char);
extern ffi_type* get_ffi_return_type(JNIEnv*, jclass, char);
extern const char* JNA_callback_init(JNIEnv*);
extern void JNA_set_last_error(JNIEnv*,int);
extern int JNA_get_last_error(JNIEnv*);
extern void JNA_callback_dispose(JNIEnv*);
extern void JNA_detach(JNIEnv*,jboolean,void*);
extern callback* create_callback(JNIEnv*, jobject, jobject,
                                 jobjectArray, jclass,
                                 callconv_t, jint, jstring);
extern void free_callback(JNIEnv*, callback*);
extern void extract_value(JNIEnv*, jobject, void*, size_t, jboolean, const char*);
extern jobject new_object(JNIEnv*, char, void*, jboolean, const char*);
extern jboolean is_protected();
extern int get_conversion_flag(JNIEnv*, jclass);
extern jboolean ffi_error(JNIEnv*,const char*,ffi_status);

extern const char* newCStringUTF8(JNIEnv*, jstring);
extern jobject newJavaPointer(JNIEnv*, void*);
extern jstring newJavaString(JNIEnv*, const char*, const char*);
extern jobject newJavaWString(JNIEnv*, const wchar_t*);
extern jobject newJavaStructure(JNIEnv*, void*, jclass);
extern jobject newJavaCallback(JNIEnv*, void*, jclass);
extern void* getNativeString(JNIEnv*, jstring, jboolean);
extern void* getNativeAddress(JNIEnv*, jobject);
extern void* getStructureAddress(JNIEnv*, jobject);
extern void* getCallbackAddress(JNIEnv*, jobject);
extern jlong getIntegerTypeValue(JNIEnv*, jobject);
extern void* getPointerTypeAddress(JNIEnv*, jobject);
extern void writeStructure(JNIEnv*, jobject);
extern jclass getNativeType(JNIEnv*, jclass);
extern void toNative(JNIEnv*, jobject, void*, size_t, jboolean, const char*);
extern jclass fromNativeCallbackParam(JNIEnv*, jclass, ffi_type*, void*, jboolean, const char*);

typedef struct _AttachOptions {
  int daemon;
  int detach;
  char* name;
} AttachOptions;
extern jobject initializeThread(callback*,AttachOptions*);

#ifdef NO_WEAK_GLOBALS
#define NewWeakGlobalRef NewGlobalRef
#define DeleteWeakGlobalRef DeleteGlobalRef
#endif

/* Native memory fault protection */
#ifdef HAVE_PROTECTION
#define PROTECT is_protected()
#define UNUSED_ENV(X) X
#else
#define UNUSED_ENV(X) UNUSED(X)
#endif
#include "protect.h"
#define ON_ERROR(ENV) throwByName(ENV, EError, "Invalid memory access")
#define PSTART() PROTECTED_START()
#define PEND(ENV) PROTECTED_END(ON_ERROR(ENV))

#ifdef __cplusplus
}
#endif
#endif /* DISPATCH_H */
