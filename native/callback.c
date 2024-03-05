/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
 * Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <jni.h>

#if defined(_WIN32)
#  define WIN32_LEAN_AND_MEAN
#  include <windows.h>
#  define TLS_SET(KEY,VALUE) TlsSetValue(KEY,VALUE)
#  define TLS_GET(KEY) TlsGetValue(KEY)
#  define TLS_KEY_T DWORD
#else
#  include <sys/types.h>
#  include <sys/param.h>
#  include <pthread.h>
#  define PTHREADS
#  define TLS_SET(KEY,VALUE) (pthread_setspecific(KEY,VALUE)==0)
#  define TLS_GET(KEY) pthread_getspecific(KEY)
#  define TLS_KEY_T pthread_key_t
#endif

#include "dispatch.h"

#ifdef __cplusplus
extern "C" {
#endif

#if defined(_WIN32) && !defined(_WIN32_WCE) && !defined(ASMFN_OFF)
#include "com_sun_jna_win32_DLLCallback.h"
#ifdef _WIN64
#ifdef _MSC_VER
/* See dll-callback.c (compiled with mingw64) for actual definitions; no
   inline asm support for MSVC and no RIP-relative instructions allowed in
   ML64. 
*/ 
#define ASMFN(X) extern void asmfn ## X ()
#else
#include "dll-callback.c"
#endif
#else /* _WIN64 */
#ifdef _MSC_VER
#define ASMFN(X) void __declspec(naked) asmfn ## X () { \
  __asm jmp DWORD PTR fn[4*X]                                     \
}
#else
#define ASMFN(X) extern void asmfn ## X (); asm(".globl _asmfn" #X "\n\
_asmfn" #X ":\n\
 jmp *(_fn+4*" #X ")")
#endif
#endif /* _WIN64 */

// Allocatable trampoline targets
#define DLL_FPTRS com_sun_jna_win32_DLLCallback_DLL_FPTRS
void (*fn[DLL_FPTRS])();

ASMFN(0);ASMFN(1);ASMFN(2);ASMFN(3);ASMFN(4);ASMFN(5);ASMFN(6);ASMFN(7);
ASMFN(8);ASMFN(9);ASMFN(10);ASMFN(11);ASMFN(12);ASMFN(13);ASMFN(14);ASMFN(15);

static void * const dll_fptrs[] = {
  &asmfn0, &asmfn1, &asmfn2, &asmfn3, &asmfn4, &asmfn5, &asmfn6, &asmfn7,
  &asmfn8, &asmfn9, &asmfn10, &asmfn11, &asmfn12, &asmfn13, &asmfn14, &asmfn15,
};

#endif /* _WIN32 && !_WIN32_WCE */

typedef struct _tls {
  JavaVM* jvm;
  jint last_error;
  // Contents set to JNI_TRUE if thread has terminated and detached properly
  int* termination_flag;
  jboolean jvm_thread;
  jboolean needs_detach;
  char name[256];
} thread_storage;

static void dispatch_callback(ffi_cif*, void*, void**, void*);
static jclass classObject;

extern void println(JNIEnv*, const char*);

callback*
create_callback(JNIEnv* env, jobject obj, jobject method,
                jobjectArray arg_classes, jclass return_class,
                callconv_t calling_convention, 
                jint options,
                jstring encoding) {
  jboolean direct = options & CB_OPTION_DIRECT;
  jboolean in_dll = options & CB_OPTION_IN_DLL;
  callback* cb;
  ffi_abi abi = (calling_convention == CALLCONV_C
		 ? FFI_DEFAULT_ABI : (ffi_abi)calling_convention);
  ffi_abi java_abi = FFI_DEFAULT_ABI;
  ffi_type* return_type;
  ffi_status status;
  jsize argc;
  JavaVM* vm;
  int rtype;
  char msg[MSG_SIZE];
  int i;
  int cvt = 0;
  const char* throw_type = NULL;
  const char* throw_msg = NULL;

  if ((*env)->GetJavaVM(env, &vm) != JNI_OK) {
    throwByName(env, EUnsatisfiedLink, "Couldn't obtain Java VM reference when creating native callback");
    return NULL;
  }
  argc = (*env)->GetArrayLength(env, arg_classes);

  cb = (callback *)malloc(sizeof(callback));
  cb->closure = ffi_closure_alloc(sizeof(ffi_closure), &cb->x_closure);
  cb->saved_x_closure = cb->x_closure;
  cb->object = (*env)->NewWeakGlobalRef(env, obj);
  cb->methodID = (*env)->FromReflectedMethod(env, method);

  cb->vm = vm;
  cb->arg_types = (ffi_type**)malloc(sizeof(ffi_type*) * argc);
  cb->java_arg_types = (ffi_type**)malloc(sizeof(ffi_type*) * (argc + 3));
  cb->arg_jtypes = (char*)malloc(sizeof(char) * argc);
  cb->conversion_flags = (int *)malloc(sizeof(int) * argc);
  cb->rflag = CVT_DEFAULT;
  cb->arg_classes = (jobject*)malloc(sizeof(jobject) * argc);
 
  cb->direct = direct;
  cb->java_arg_types[0] = cb->java_arg_types[1] = cb->java_arg_types[2] = &ffi_type_pointer;
  cb->encoding = newCStringUTF8(env, encoding);

  for (i=0;i < argc;i++) {
    int jtype;
    jclass cls = (*env)->GetObjectArrayElement(env, arg_classes, i);
    if (direct && ((cb->conversion_flags[i] = get_conversion_flag(env, cls)) != CVT_DEFAULT)) {
      cb->arg_classes[i] = (*env)->NewWeakGlobalRef(env, cls);
      cvt = 1;
    }
    else {
      cb->arg_classes[i] = NULL;
    }

    jtype = get_java_type(env, cls);
    if (jtype == -1) {
      snprintf(msg, sizeof(msg), "Unsupported callback argument at index %d", i);
      throw_type = EIllegalArgument;
      throw_msg = msg;
      goto failure_cleanup;
    }
    cb->arg_jtypes[i] = (char)jtype;
    cb->java_arg_types[i+3] = cb->arg_types[i] = get_ffi_type(env, cls, cb->arg_jtypes[i]);
    if (!cb->java_arg_types[i+3]) {
      goto failure_cleanup;
    }
    if (cb->conversion_flags[i] == CVT_NATIVE_MAPPED
        || cb->conversion_flags[i] == CVT_POINTER_TYPE
        || cb->conversion_flags[i] == CVT_INTEGER_TYPE) {
      jclass ncls;
      ncls = getNativeType(env, cls);
      jtype = get_java_type(env, ncls);
      if (jtype == -1) {
        snprintf(msg, sizeof(msg), "Unsupported NativeMapped callback argument native type at argument %d", i);
        throw_type = EIllegalArgument;
        throw_msg = msg;
        goto failure_cleanup;
      }
      cb->arg_jtypes[i] = (char)jtype;
      cb->java_arg_types[i+3] = &ffi_type_pointer;
      cb->arg_types[i] = get_ffi_type(env, ncls, cb->arg_jtypes[i]);
      if (!cb->arg_types[i]) {
        goto failure_cleanup;
      }
    }

    // Java callback method is called using varargs, so promote floats to 
    // double where appropriate for the platform
    if (cb->arg_types[i]->type == FFI_TYPE_FLOAT) {
      cb->java_arg_types[i+3] = &ffi_type_double;
      cb->conversion_flags[i] = CVT_FLOAT;
      cvt = 1;
    }
    else if (cb->arg_types[i]->type == FFI_TYPE_UINT16 || cb->arg_types[i]->type == FFI_TYPE_SINT16) {
      cb->java_arg_types[i+3] = &ffi_type_sint;
      cb->conversion_flags[i] = CVT_SHORT;
      cvt = 1;
    }
    else if (cb->arg_types[i]->type == FFI_TYPE_UINT8 || cb->arg_types[i]->type == FFI_TYPE_SINT8) {
      cb->java_arg_types[i+3] = &ffi_type_sint;
      cb->conversion_flags[i] = CVT_BYTE;
      cvt = 1;
    }
    else if (cb->java_arg_types[i+3]->type == FFI_TYPE_STRUCT) {
      // All callback structure arguments are passed as a jobject
      cb->java_arg_types[i+3] = &ffi_type_pointer;
    }
  }
  if (!direct || !cvt) {
    free(cb->conversion_flags);
    cb->conversion_flags = NULL;
    free(cb->arg_classes);
    cb->arg_classes = NULL;
  }
  if (direct) {
    cb->rflag = get_conversion_flag(env, return_class);
    if (cb->rflag == CVT_NATIVE_MAPPED
        || cb->rflag == CVT_INTEGER_TYPE
        || cb->rflag == CVT_POINTER_TYPE) {
      return_class = getNativeType(env, return_class);
    }
  }

#if defined(_WIN32)
  if (calling_convention == CALLCONV_STDCALL) {
#if defined(_WIN64) || defined(_WIN32_WCE)
    // Ignore requests for stdcall on win64/wince
    abi = FFI_DEFAULT_ABI;
#else
    abi = FFI_STDCALL;
    // All JNI entry points on win32 use stdcall
    java_abi = FFI_STDCALL;
#endif
  }
#endif // _WIN32

  if (!(abi > FFI_FIRST_ABI && abi < FFI_LAST_ABI)) {
    snprintf(msg, sizeof(msg), "Invalid calling convention %d", abi);
    throw_type = EIllegalArgument;
    throw_msg = msg;
    goto failure_cleanup;
  }

  rtype = get_java_type(env, return_class);
  if (rtype == -1) {
    throw_type = EIllegalArgument;
    throw_msg = "Unsupported callback return type";
    goto failure_cleanup;
  }
  return_type = get_ffi_return_type(env, return_class, (char)rtype);
  if (!return_type) {
    throw_type = EIllegalArgument;
    throw_msg = "Error in callback return type";
    goto failure_cleanup;
  }
  status = ffi_prep_cif(&cb->cif, abi, argc, return_type, cb->arg_types);
  if (!ffi_error(env, "callback setup", status)) {
    ffi_type* java_return_type = return_type;

    if (cb->rflag == CVT_STRUCTURE_BYVAL
        || cb->rflag == CVT_NATIVE_MAPPED
        || cb->rflag == CVT_POINTER_TYPE
        || cb->rflag == CVT_INTEGER_TYPE) {
      // Java method returns a jobject, not a struct
      java_return_type = &ffi_type_pointer;
      rtype = '*';
    }
    switch(rtype) {
#define OFFSETOF(ENV,METHOD) ((size_t)((char *)&(*(ENV))->METHOD - (char *)(*(ENV))))
    case 'V': cb->fptr_offset = OFFSETOF(env, CallVoidMethod); break;
    case 'Z': cb->fptr_offset = OFFSETOF(env, CallBooleanMethod); break;
    case 'B': cb->fptr_offset = OFFSETOF(env, CallByteMethod); break;
    case 'S': cb->fptr_offset = OFFSETOF(env, CallShortMethod); break;
    case 'C': cb->fptr_offset = OFFSETOF(env, CallCharMethod); break;
    case 'I': cb->fptr_offset = OFFSETOF(env, CallIntMethod); break;
    case 'J': cb->fptr_offset = OFFSETOF(env, CallLongMethod); break;
    case 'F': cb->fptr_offset = OFFSETOF(env, CallFloatMethod); break;
    case 'D': cb->fptr_offset = OFFSETOF(env, CallDoubleMethod); break;
    default: cb->fptr_offset = OFFSETOF(env, CallObjectMethod); break;
    }
    status = ffi_prep_cif_var(&cb->java_cif, java_abi, 3, argc+3, java_return_type, cb->java_arg_types);
    if (!ffi_error(env, "callback setup (2)", status)) {
      ffi_prep_closure_loc(cb->closure, &cb->cif, dispatch_callback, cb,
                           cb->x_closure);
#ifdef DLL_FPTRS
      // Find an available function pointer and assign it
      if (in_dll) {
        for (i=0;i < DLL_FPTRS;i++) {
          if (fn[i] == NULL) {
            fn[i] = cb->x_closure;
            cb->x_closure = dll_fptrs[i];
            break;
          }
        }
        if (i == DLL_FPTRS) {
          throw_type = EOutOfMemory;
          throw_msg = "No more DLL callback slots available";
          goto failure_cleanup;
        }
      }
#endif
      return cb;
    }
  }

 failure_cleanup:
  free_callback(env, cb);
  if (throw_type) {
    throwByName(env, throw_type, throw_msg);
  }

  return NULL;
}
void 
free_callback(JNIEnv* env, callback *cb) {
  (*env)->DeleteWeakGlobalRef(env, cb->object);
  ffi_closure_free(cb->closure);
  free(cb->arg_types);
  if (cb->arg_classes) {
    unsigned i;
    for (i=0;i < cb->cif.nargs;i++) {
      if (cb->arg_classes[i]) {
        (*env)->DeleteWeakGlobalRef(env, cb->arg_classes[i]);
      }
    }
    free(cb->arg_classes);
  }
  free(cb->java_arg_types);
  if (cb->conversion_flags) {
    free(cb->conversion_flags);
  }
  free(cb->arg_jtypes);
#ifdef DLL_FPTRS
  int i;
  for (i=0;i < DLL_FPTRS;i++) {
    if (fn[i] == cb->saved_x_closure) {
      fn[i] = NULL;
    }
  }
#endif
  free((void *)cb->encoding);
  free(cb);
}

static int
handle_exception(JNIEnv* env, jobject cb, jthrowable throwable) {
#define HANDLER_TYPE "com/sun/jna/Callback$UncaughtExceptionHandler"
#define HANDLER_SIG "Lcom/sun/jna/Callback$UncaughtExceptionHandler;"
  jclass classHandler = (*env)->FindClass(env, HANDLER_TYPE);
  if (classHandler) {
    jclass classNative = (*env)->FindClass(env, "com/sun/jna/Native");
    if (classNative) {
      jfieldID fid = (*env)->GetStaticFieldID(env, classNative, "callbackExceptionHandler", HANDLER_SIG);
      if (fid) {
        jobject handler = (*env)->GetStaticObjectField(env, classNative, fid);
        if (handler) {
          jmethodID mid = (*env)->GetMethodID(env, classHandler, "uncaughtException", "(Lcom/sun/jna/Callback;Ljava/lang/Throwable;)V");
          if (mid) {
            if (!(*env)->IsSameObject(env, handler, NULL)) {
              (*env)->CallVoidMethod(env, handler, mid, cb, throwable);
            }
            if ((*env)->ExceptionCheck(env) == 0) {
              return 1;
            }
          }
        }
      }
    }
  }
  (*env)->ExceptionDescribe(env);
  (*env)->ExceptionClear(env);
  return 0;
}

static void
invoke_callback(JNIEnv* env, callback *cb, ffi_cif* cif, void *resp, void **cbargs) {
  jobject self;
  void *oldresp = resp;

  self = (*env)->NewLocalRef(env, cb->object);
  // Avoid calling back to a GC'd object
  if ((*env)->IsSameObject(env, self, NULL)) {
    fprintf(stderr, "JNA: callback object has been garbage collected\n");
    if (cif->rtype->type != FFI_TYPE_VOID) {
      memset(resp, 0, cif->rtype->size); 
    }
  }
  else if (cb->direct) {
    unsigned int i;
    void **args = alloca((cif->nargs + 3) * sizeof(void *));
    args[0] = (void *)&env;
    args[1] = &self;
    args[2] = &cb->methodID;
    memcpy(&args[3], cbargs, cif->nargs * sizeof(void *));

    // Note that there is no support for CVT_TYPE_MAPPER here
    if (cb->conversion_flags) {
      for (i=0;i < cif->nargs;i++) {
        switch(cb->conversion_flags[i]) {
        case CVT_INTEGER_TYPE:
        case CVT_POINTER_TYPE:
        case CVT_NATIVE_MAPPED:
        case CVT_NATIVE_MAPPED_STRING:
        case CVT_NATIVE_MAPPED_WSTRING:
	  // Make sure we have space enough for the new argument
	  args[i+3] = alloca(sizeof(void *));
	  *((void **)args[i+3]) = fromNativeCallbackParam(env, cb->arg_classes[i], cif->arg_types[i], cbargs[i], JNI_FALSE, cb->encoding);
          break;
        case CVT_POINTER:
          *((void **)args[i+3]) = newJavaPointer(env, *(void **)cbargs[i]);
          break;
        case CVT_STRING:
          *((void **)args[i+3]) = newJavaString(env, *(void **)cbargs[i], cb->encoding);
          break;
        case CVT_WSTRING:
          *((void **)args[i+3]) = newJavaWString(env, *(void **)cbargs[i]);
          break;
        case CVT_STRUCTURE:
          *((void **)args[i+3]) = newJavaStructure(env, *(void **)cbargs[i], cb->arg_classes[i]);
          break;
        case CVT_STRUCTURE_BYVAL:
	  args[i+3] = alloca(sizeof(void *));
	  *((void **)args[i+3]) = newJavaStructure(env, cbargs[i], cb->arg_classes[i]);
          break;
        case CVT_CALLBACK:
          *((void **)args[i+3]) = newJavaCallback(env, *(void **)cbargs[i], cb->arg_classes[i]);
          break;
        case CVT_FLOAT:
	  args[i+3] = alloca(sizeof(double));
	  *((double *)args[i+3]) = *(float*)cbargs[i];
          break;
        case CVT_SHORT:
	  args[i+3] = alloca(sizeof(int));
	  *((int *)args[i+3]) = *(short*)cbargs[i];
          break;
        case CVT_BYTE:
	  args[i+3] = alloca(sizeof(int));
          *((int *)args[i+3]) = *(char*)cbargs[i];
          break;
        case CVT_DEFAULT:
          break;
        default:
          fprintf(stderr, "JNA: Unhandled arg conversion type %d\n", cb->conversion_flags[i]);
          break;
        }
      }
    }

    if (cb->rflag == CVT_STRUCTURE_BYVAL) {
      resp = alloca(sizeof(jobject));
    }
    else if (cb->cif.rtype->size > cif->rtype->size) {
      resp = alloca(cb->cif.rtype->size);
    }
#define FPTR(ENV,OFFSET) (*(void **)((char *)(*(ENV)) + OFFSET))
#define JNI_FN(X) ((void (*)(void))(X))
    ffi_call(&cb->java_cif, JNI_FN(FPTR(env, cb->fptr_offset)), resp, args);
    if ((*env)->ExceptionCheck(env)) {
      jthrowable throwable = (*env)->ExceptionOccurred(env);
      (*env)->ExceptionClear(env);
      if (!handle_exception(env, self, throwable)) {
        fprintf(stderr, "JNA: error handling callback exception, continuing\n");
      }
      if (cif->rtype->type != FFI_TYPE_VOID) {
        memset(oldresp, 0, cif->rtype->size);
      }
    }
    else switch(cb->rflag) {
    case CVT_INTEGER_TYPE:
      if (cb->cif.rtype->size > sizeof(ffi_arg)) {
        *(jlong *)oldresp = getIntegerTypeValue(env, *(void **)resp);
      }
      else {
        *(ffi_arg *)oldresp = (ffi_arg)getIntegerTypeValue(env, *(void **)resp);
      }
      break;
    case CVT_POINTER_TYPE:
      *(void **)resp = getPointerTypeAddress(env, *(void **)resp);
      break;
    case CVT_NATIVE_MAPPED:
      toNative(env, *(void **)resp, oldresp, cb->cif.rtype->size, JNI_TRUE, cb->encoding);
      break;
    case CVT_NATIVE_MAPPED_STRING:
    case CVT_NATIVE_MAPPED_WSTRING:
      // TODO: getNativeString rather than allocated memory
      fprintf(stderr, "JNA: Likely memory leak here\n");
      toNative(env, *(void **)resp, oldresp, cb->cif.rtype->size, JNI_TRUE, cb->encoding);
      break;
    case CVT_POINTER:
      *(void **)resp = getNativeAddress(env, *(void **)resp);
      break;
    case CVT_STRING: 
      *(void **)resp = getNativeString(env, *(void **)resp, JNI_FALSE);
      break;
    case CVT_WSTRING: 
      *(void **)resp = getNativeString(env, *(void **)resp, JNI_TRUE);
      break;
    case CVT_STRUCTURE:
      writeStructure(env, *(void **)resp);
      *(void **)resp = getStructureAddress(env, *(void **)resp);
      break;
    case CVT_STRUCTURE_BYVAL:
      writeStructure(env, *(void **)resp);
      memcpy(oldresp, getStructureAddress(env, *(void **)resp), cb->cif.rtype->size);
      break;
    case CVT_CALLBACK: 
      *(void **)resp = getCallbackAddress(env, *(void **)resp);
      break;
    case CVT_DEFAULT:
      break;
    default:
      fprintf(stderr, "JNA: Unhandled result conversion: %d\n", cb->rflag);
      break;
    }
    if (cb->conversion_flags) {
      for (i=0;i < cif->nargs;i++) {
        if (cb->conversion_flags[i] == CVT_STRUCTURE) {
          writeStructure(env, *(void **)cbargs[i]);
        }
      }
    }
  }
  else {
    jobject result;
    jobjectArray params =
      (*env)->NewObjectArray(env, cif->nargs, classObject, NULL);
    unsigned int i;

    for (i=0;i < cif->nargs;i++) {
      jobject arg = new_object(env, cb->arg_jtypes[i], cbargs[i], JNI_FALSE, cb->encoding);
      (*env)->SetObjectArrayElement(env, params, i, arg);
    }
    result = (*env)->CallObjectMethod(env, self, cb->methodID, params);
    if ((*env)->ExceptionCheck(env)) {
      jthrowable throwable = (*env)->ExceptionOccurred(env);
      (*env)->ExceptionClear(env);
      if (!handle_exception(env, self, throwable)) {
        fprintf(stderr, "JNA: error while handling callback exception, continuing\n");
      }
      if (cif->rtype->type != FFI_TYPE_VOID)
        memset(resp, 0, cif->rtype->size);
    }
    else {
      extract_value(env, result, resp, cif->rtype->size, JNI_TRUE, cb->encoding);
    }
  }
}

static TLS_KEY_T tls_thread_data_key;
static thread_storage* get_thread_storage(JNIEnv* env) {
  thread_storage* tls = (thread_storage *)TLS_GET(tls_thread_data_key);
  if (tls == NULL) {
    tls = (thread_storage*)malloc(sizeof(thread_storage));
    if (!tls) {
      throwByName(env, EOutOfMemory, "JNA: Can't allocate thread storage");
    }
    else {
      snprintf(tls->name, sizeof(tls->name), "<uninitialized thread name>");
      tls->jvm_thread = JNI_TRUE;
      tls->last_error = 0;
      tls->termination_flag = NULL;
      if ((*env)->GetJavaVM(env, &tls->jvm) != JNI_OK) {
        free(tls);
        throwByName(env, EIllegalState, "JNA: Could not get JavaVM");
        tls = NULL;
      }
      else if (!TLS_SET(tls_thread_data_key, tls)) {
        free(tls);
        throwByName(env, EOutOfMemory, "JNA: Internal TLS error");
        tls = NULL;
      }
    }
  }
  return tls;
}

static void dispose_thread_data(void* data) {
  thread_storage* tls = (thread_storage*)data;
  JavaVM* jvm = tls->jvm;
  JNIEnv* env;
  int is_attached = (*jvm)->GetEnv(jvm, (void*)&env, JNI_VERSION_1_4) == JNI_OK;
  jboolean detached = JNI_TRUE;
  if (is_attached) {
    if ((*jvm)->DetachCurrentThread(jvm) != 0) {
      fprintf(stderr, "JNA: could not detach native thread (automatic)\n");
      detached = JNI_FALSE;
    }
  }
  if (tls->termination_flag && detached) {
    *(tls->termination_flag) = JNI_TRUE;
  }
  free(data);
}

#ifdef _WIN32

BOOL WINAPI DllMain(HINSTANCE hDLL, DWORD fdwReason, LPVOID lpvReserved) {
  switch (fdwReason) {
  case DLL_PROCESS_ATTACH:
    tls_thread_data_key = TlsAlloc();
    if (tls_thread_data_key == TLS_OUT_OF_INDEXES) {
      return FALSE;
    }
    break;
  case DLL_PROCESS_DETACH:
    TlsFree(tls_thread_data_key);
    break;
  case DLL_THREAD_ATTACH:
    break;
  case DLL_THREAD_DETACH: {
    thread_storage* tls = (thread_storage*)TlsGetValue(tls_thread_data_key);
    if (tls) {
      dispose_thread_data(tls);
      TlsSetValue(tls_thread_data_key, 0);
    }
    break;
  }
  default:
    break;
  }
  return TRUE;
}

#endif

#ifdef PTHREADS
static void make_thread_data_key() {
  pthread_key_create(&tls_thread_data_key, dispose_thread_data);
}
#endif

/** Store the requested detach state for the current thread. */
void
JNA_detach(JNIEnv* env, jboolean needs_detach, void* termination_flag) {
  thread_storage* tls = get_thread_storage(env);
  if (tls) {
    tls->needs_detach = needs_detach;
    tls->termination_flag = (int *)termination_flag;
    if (needs_detach && tls->jvm_thread) {
      throwByName(env, EIllegalState, "Can not detach from a JVM thread");
    }
  }
}

/** Store the value of errno/GetLastError in TLS */
void
JNA_set_last_error(JNIEnv* env, int err) {
  thread_storage* tls = get_thread_storage(env);
  if (tls) {
    tls->last_error = err;
  }
}

/** Store the value of errno/GetLastError in TLS */
int
JNA_get_last_error(JNIEnv* env) {
  thread_storage* tls = get_thread_storage(env);
  if (tls) {
    return tls->last_error;
  }
  return 0;
}

static void
dispatch_callback(ffi_cif* cif, void* resp, void** cbargs, void* user_data) {
  callback* cb = ((callback *)user_data); 
  JavaVM* jvm = cb->vm;
  JNIEnv* env = NULL;
  int was_attached = (*jvm)->GetEnv(jvm, (void *)&env, JNI_VERSION_1_4) == JNI_OK;
  jboolean needs_detach = was_attached ? JNI_FALSE : JNI_TRUE;
  thread_storage* tls = was_attached ? get_thread_storage(env) : NULL;

  if (!was_attached) {
    int attach_status = 0;
    JavaVMAttachArgs args;
    int daemon = JNI_FALSE;

    args.version = JNI_VERSION_1_2;
    args.name = NULL;
    args.group = NULL;
    if (cb->behavior_flags & CB_HAS_INITIALIZER) {
      AttachOptions options;
      options.daemon = JNI_FALSE; // default non-daemon
      options.detach = JNI_TRUE; // default detach behavior
      options.name = NULL;
      args.group = initializeThread(cb, &options);
      daemon = options.daemon ? JNI_TRUE : JNI_FALSE;
      needs_detach = options.detach ? JNI_TRUE : JNI_FALSE;
      args.name = options.name;
    }
    if (daemon) {
      attach_status = (*jvm)->AttachCurrentThreadAsDaemon(jvm, (void*)&env, &args);
    }
    else {
      attach_status = (*jvm)->AttachCurrentThread(jvm, (void *)&env, &args);
    }
    if (attach_status != JNI_OK) {
      free((void *)args.name);
      if (args.group) {
        (*env)->DeleteWeakGlobalRef(env, args.group);
      }
      fprintf(stderr, "JNA: Can't attach native thread to VM for callback: %d (check stacksize for callbacks)\n", attach_status);
      return;
    }
    tls = get_thread_storage(env);
    if (tls) {
      snprintf(tls->name, sizeof(tls->name), "%s", args.name ? args.name : "<unconfigured native thread>");
      tls->needs_detach = needs_detach;
      tls->jvm_thread = JNI_FALSE;
    }
    // Dispose of allocated memory
    free((void *)args.name);
    if (args.group) {
      (*env)->DeleteWeakGlobalRef(env, args.group);
    }
  }

  if (!tls) {
    fprintf(stderr, "JNA: couldn't obtain thread-local storage\n");
    return;
  }

  // Give the callback glue its own local frame to ensure all local references
  // are properly disposed
  if ((*env)->PushLocalFrame(env, 16) < 0) {
    fprintf(stderr, "JNA: Out of memory: Can't allocate local frame\n");
  }
  else {
    invoke_callback(env, cb, cif, resp, cbargs);
    // Make note of whether the callback wants to avoid detach
    needs_detach = tls->needs_detach && !tls->jvm_thread;
    (*env)->PopLocalFrame(env, NULL);
  }
  
  if (needs_detach) {
    if ((*jvm)->DetachCurrentThread(jvm) != 0) {
      fprintf(stderr, "JNA: could not detach thread\n");
    }
  }
}

const char* 
JNA_callback_init(JNIEnv* env) {
#ifdef PTHREADS
  static pthread_once_t key_once = PTHREAD_ONCE_INIT;
  pthread_once(&key_once, make_thread_data_key);
#endif

  if (!LOAD_CREF(env, Object, "java/lang/Object")) return "java.lang.Object";

  return NULL;
}
  
void
JNA_callback_dispose(JNIEnv* env) {
  if (classObject) {
    (*env)->DeleteWeakGlobalRef(env, classObject);
    classObject = NULL;
  }
#ifdef PTHREADS
  pthread_key_delete(tls_thread_data_key);
#endif
}

#ifdef __cplusplus
}
#endif
