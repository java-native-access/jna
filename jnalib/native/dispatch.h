/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
#ifndef DISPATCH_H
#define DISPATCH_H

#include "ffi.h"
#include "com_sun_jna_Function.h"
#include "com_sun_jna_Native.h"
#ifdef sun
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
#else
#include <malloc.h>
#endif /* _MSC_VER */
#endif /* _WIN32 */

#ifdef __cplusplus
extern "C" {
#endif

/* These are the calling conventions an invocation can handle. */
typedef enum _callconv {
    CALLCONV_C = com_sun_jna_Function_C_CONVENTION,
#ifdef _WIN32
    CALLCONV_STDCALL = com_sun_jna_Function_ALT_CONVENTION,
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
  CVT_WSTRING = com_sun_jna_Native_CVT_WSTRING,
  CVT_INTEGER_TYPE = com_sun_jna_Native_CVT_INTEGER_TYPE,
  CVT_POINTER_TYPE = com_sun_jna_Native_CVT_POINTER_TYPE,
  CVT_TYPE_MAPPER = com_sun_jna_Native_CVT_TYPE_MAPPER,
};

typedef struct _callback {
  // Location of this field must agree with CallbackReference.getTrampoline()
  void* x_closure;
  ffi_closure* closure;
  ffi_cif cif;
  ffi_cif java_cif;
  ffi_type** arg_types;
  ffi_type** java_arg_types;
  jobject* arg_classes;
  int* flags;
  int rflag;
  JavaVM* vm;
  jobject object;
  jmethodID methodID;
  char* arg_jtypes;
  jboolean direct;
  void* fptr;
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
#define L2A(X) ((void *)(X))
#define A2L(X) ((jlong)(X))
#define snprintf sprintf_s
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
extern int get_jtype(JNIEnv*, jclass);
extern ffi_type* get_ffi_type(JNIEnv*, jclass, char);
extern ffi_type* get_ffi_rtype(JNIEnv*, jclass, char);
extern const char* jnidispatch_callback_init(JNIEnv*);
extern void jnidispatch_callback_dispose(JNIEnv*);
extern callback* create_callback(JNIEnv*, jobject, jobject,
                                 jobjectArray, jclass, 
                                 callconv_t, jboolean);
extern void free_callback(JNIEnv*, callback*);
extern void extract_value(JNIEnv*, jobject, void*, size_t, jboolean);
extern jobject new_object(JNIEnv*, char, void*, jboolean);
extern jboolean is_protected();
extern int get_conversion_flag(JNIEnv*, jclass);
extern jboolean ffi_error(JNIEnv*,const char*,ffi_status);

extern jobject newJavaPointer(JNIEnv*, void*);
extern jstring newJavaString(JNIEnv*, const char*, jboolean);
extern jobject newJavaWString(JNIEnv*, const wchar_t*);
extern jobject newJavaStructure(JNIEnv*, void*, jclass, jboolean);
extern jobject newJavaCallback(JNIEnv*, void*, jclass);
extern void* getNativeString(JNIEnv*, jstring, jboolean);
extern void* getNativeAddress(JNIEnv*, jobject);
extern void* getStructureAddress(JNIEnv*, jobject);
extern void* getCallbackAddress(JNIEnv*, jobject);
extern jlong getIntegerTypeValue(JNIEnv*, jobject);
extern void* getPointerTypeAddress(JNIEnv*, jobject);
extern void writeStructure(JNIEnv*, jobject);
extern jclass getNativeType(JNIEnv*, jclass);
extern void toNative(JNIEnv*, jobject, void*, size_t, jboolean);
extern jclass fromNative(JNIEnv*, jclass, ffi_type*, void*, jboolean);

/* Native memory fault protection */
#ifdef HAVE_PROTECTION
#define PROTECT is_protected()
#endif
#include "protect.h"
#define ON_ERROR() throwByName(env, EError, "Invalid memory access")
#define PSTART() PROTECTED_START()
#define PEND() PROTECTED_END(ON_ERROR())

#ifdef __cplusplus
}
#endif
#endif /* DISPATCH_H */
