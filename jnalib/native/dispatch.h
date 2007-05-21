#ifndef DISPATCH_H
#define DISPATCH_H

#ifdef __cplusplus
extern "C" {
#endif

/* C variable types for arguments and return values. */
typedef enum _vartype {
    /* pointer */
    TYPE_PTR = 0, 
    /* 32-bit or smaller integer */
    TYPE_INT32,
    TYPE_VOID = TYPE_INT32,
    /* 32-bit floating point */
    TYPE_FP32,
    /* 64-bit floating point */
    TYPE_FP64,
    /* 64-bit integer */
    TYPE_INT64,
} type_t;

#include "com_sun_jna_Function.h"
/* These are the calling conventions an invocation can handle. */
typedef enum _callconv {
    CALLCONV_C = com_sun_jna_Function_C_CONVENTION,
#if defined(_WIN32)
    CALLCONV_STDCALL = com_sun_jna_Function_ALT_CONVENTION,
#endif
} callconv_t;

/* Represents a machine word (one stack element). */
typedef union _word {
    jint i;
    jfloat f;
    void *p;
} word_t;

/* Maximum number of allowed arguments. */
#define MAX_NARGS 32

typedef struct _callback {
  void *insns;
  jobject object;
  jmethodID methodID;
  jsize param_count;
  char param_jtypes[MAX_NARGS];
  type_t return_type;
  char return_jtype;
} callback;

#if defined(SOLARIS2) || defined(__GNUC__)
#define L2A(X) ((void *)(unsigned long)(X))
#define A2L(X) ((jlong)(unsigned long)(X))
#endif

#if defined(_MSC_VER)
#define L2A(X) ((void *)(X))
#define A2L(X) ((jlong)(X))
#endif

#ifdef _WIN32
#ifdef _MSC_VER
#define alloca _alloca
#endif
#else
#include <alloca.h>
#endif

/* Convenience macros */
#define LOAD_REF(ENV,VAR) \
  ((VAR == 0) \
   ? 0 : ((VAR = (*ENV)->NewGlobalRef(ENV, VAR)) == 0 ? 0 : VAR))
#define FIND_CLASS(ENV,SIMPLE,NAME) \
  (class ## SIMPLE = (*ENV)->FindClass(ENV, NAME))
#define FIND_PRIMITIVE_CLASS(ENV,SIMPLE) \
  (classPrimitive ## SIMPLE = (*ENV)->GetStaticObjectField(ENV,class ## SIMPLE,(*ENV)->GetStaticFieldID(ENV,class ## SIMPLE,"TYPE","Ljava/lang/Class;")))
#define LOAD_CREF(ENV,SIMPLE,NAME) \
  (FIND_CLASS(ENV,SIMPLE,NAME) && LOAD_REF(ENV,class ## SIMPLE))
#define LOAD_PCREF(ENV,SIMPLE,NAME) \
  (LOAD_CREF(ENV,SIMPLE,NAME) \
   && FIND_PRIMITIVE_CLASS(ENV,SIMPLE) \
   && LOAD_REF(ENV,classPrimitive ## SIMPLE))
#define LOAD_MID(ENV,VAR,CLASS,NAME,SIG) \
   ((VAR = (*ENV)->GetMethodID(ENV, CLASS, NAME, SIG)) ? VAR : 0)
#define LOAD_FID(ENV,VAR,CLASS,NAME,SIG) \
   ((VAR = (*ENV)->GetFieldID(ENV, CLASS, NAME, SIG)) ? VAR : 0)

extern void throwByName(JNIEnv *env, const char *name, const char *msg);
extern jobject newJavaPointer(JNIEnv *, void *);
extern char get_jtype(JNIEnv*, jclass);
extern callback* create_callback(JNIEnv*, jobject, jobject,
                                 jobjectArray, jclass, callconv_t);

#ifdef __cplusplus
}
#endif
#endif /* DISPATCH_H */
