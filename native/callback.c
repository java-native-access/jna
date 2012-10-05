/* Copyright (c) 2007-2011 Timothy Wall, All Rights Reserved
 * Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <jni.h>

#if defined(_WIN32)
#  define WIN32_LEAN_AND_MEAN
#  include <windows.h>
#else
#  include <sys/types.h>
#  include <sys/param.h>
#endif
#include "dispatch.h"

#ifdef __cplusplus
extern "C" {
#endif

#if defined(_WIN32) && !defined(_WIN32_WCE)
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
// FIXME is "PROC NEAR" correct?
#define ASMFN(X) extern void asmfn ## X(); \
__asm asmfn ## X PROC NEAR \
__asm jmp fn[X]
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

static void callback_dispatch(ffi_cif*, void*, void**, void*);
static jclass classObject;

callback*
create_callback(JNIEnv* env, jobject obj, jobject method,
                jobjectArray param_types, jclass return_type,
                callconv_t calling_convention, jint options) {
  jboolean direct = options & CB_OPTION_DIRECT;
  jboolean in_dll = options & CB_OPTION_IN_DLL;
  callback* cb;
  ffi_abi abi = FFI_DEFAULT_ABI;
  ffi_abi java_abi = FFI_DEFAULT_ABI;
  ffi_type* ffi_rtype;
  ffi_status status;
  jsize argc;
  JavaVM* vm;
  int rtype;
  char msg[64];
  int i;
  int cvt = 0;
  const char* throw_type = NULL;
  const char* throw_msg = NULL;

  if ((*env)->GetJavaVM(env, &vm) != JNI_OK) {
    throwByName(env, EUnsatisfiedLink, "Can't get Java VM to create native callback");
    return NULL;
  }
  argc = (*env)->GetArrayLength(env, param_types);

  cb = (callback *)malloc(sizeof(callback));
  cb->closure = ffi_closure_alloc(sizeof(ffi_closure), &cb->x_closure);
  cb->saved_x_closure = cb->x_closure;
  cb->object = (*env)->NewWeakGlobalRef(env, obj);
  cb->methodID = (*env)->FromReflectedMethod(env, method);

  cb->vm = vm;
  cb->arg_types = (ffi_type**)malloc(sizeof(ffi_type*) * argc);
  cb->java_arg_types = (ffi_type**)malloc(sizeof(ffi_type*) * (argc + 3));
  cb->arg_jtypes = (char*)malloc(sizeof(char) * argc);
  cb->flags = (int *)malloc(sizeof(int) * argc);
  cb->rflag = CVT_DEFAULT;
  cb->arg_classes = (jobject*)malloc(sizeof(jobject) * argc);
 
  cb->direct = direct;
  cb->java_arg_types[0] = cb->java_arg_types[1] = cb->java_arg_types[2] = &ffi_type_pointer;

  for (i=0;i < argc;i++) {
    int jtype;
    jclass cls = (*env)->GetObjectArrayElement(env, param_types, i);
    if ((cb->flags[i] = get_conversion_flag(env, cls)) != CVT_DEFAULT) {
      cb->arg_classes[i] = (*env)->NewWeakGlobalRef(env, cls);
      cvt = 1;
    }
    else {
      cb->arg_classes[i] = NULL;
    }

    jtype = get_jtype(env, cls);
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
    if (cb->flags[i] == CVT_NATIVE_MAPPED
        || cb->flags[i] == CVT_POINTER_TYPE
        || cb->flags[i] == CVT_INTEGER_TYPE) {
      jclass ncls;
      ncls = getNativeType(env, cls);
      jtype = get_jtype(env, ncls);
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

    if (cb->arg_types[i]->type == FFI_TYPE_FLOAT) {
      // Java method is varargs, so promote floats to double
      cb->java_arg_types[i+3] = &ffi_type_double;
      cb->flags[i] = CVT_FLOAT;
      cvt = 1;
    }
    else if (cb->java_arg_types[i+3]->type == FFI_TYPE_STRUCT) {
      // All callback structure arguments are passed as a jobject
      cb->java_arg_types[i+3] = &ffi_type_pointer;
    }
  }
  if (!direct || !cvt) {
    free(cb->flags);
    cb->flags = NULL;
    free(cb->arg_classes);
    cb->arg_classes = NULL;
  }
  if (direct) {
    cb->rflag = get_conversion_flag(env, return_type);
    if (cb->rflag == CVT_NATIVE_MAPPED
        || cb->rflag == CVT_INTEGER_TYPE
        || cb->rflag == CVT_POINTER_TYPE) {
      return_type = getNativeType(env, return_type);
    }
  }

#if defined(_WIN32) && !defined(_WIN64) && !defined(_WIN32_WCE)
  if (calling_convention == CALLCONV_STDCALL) {
    abi = FFI_STDCALL;
  }
  // Calling into Java on win32 *always* uses stdcall
  java_abi = FFI_STDCALL;
#endif // _WIN32

  rtype = get_jtype(env, return_type);
  if (rtype == -1) {
    throw_type = EIllegalArgument;
    throw_msg = "Unsupported callback return type";
    goto failure_cleanup;
  }
  ffi_rtype = get_ffi_rtype(env, return_type, (char)rtype);
  if (!ffi_rtype) {
    throw_type = EIllegalArgument;
    throw_msg = "Error in callback return type";
    goto failure_cleanup;
  }
  status = ffi_prep_cif(&cb->cif, abi, argc, ffi_rtype, cb->arg_types);
  if (!ffi_error(env, "callback setup", status)) {
    ffi_type* java_ffi_rtype = ffi_rtype;

    if (cb->rflag == CVT_STRUCTURE_BYVAL
        || cb->rflag == CVT_NATIVE_MAPPED
        || cb->rflag == CVT_POINTER_TYPE
        || cb->rflag == CVT_INTEGER_TYPE) {
      // Java method returns a jobject, not a struct
      java_ffi_rtype = &ffi_type_pointer;
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
    status = ffi_prep_cif(&cb->java_cif, java_abi, argc+3, java_ffi_rtype, cb->java_arg_types);
    if (!ffi_error(env, "callback setup (2)", status)) {
      ffi_prep_closure_loc(cb->closure, &cb->cif, callback_dispatch, cb,
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
  int i;
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
  if (cb->flags) {
    free(cb->flags);
  }
  free(cb->arg_jtypes);
#ifdef DLL_FPTRS
  for (i=0;i < DLL_FPTRS;i++) {
    if (fn[i] == cb->saved_x_closure) {
      fn[i] = NULL;
    }
  }
#endif
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
callback_invoke(JNIEnv* env, callback *cb, ffi_cif* cif, void *resp, void **cbargs) {
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

    if (cb->flags) {
      for (i=0;i < cif->nargs;i++) {
        switch(cb->flags[i]) {
        case CVT_INTEGER_TYPE:
        case CVT_POINTER_TYPE:
        case CVT_NATIVE_MAPPED:
	  // Make sure we have space enough for the new argument
	  args[i+3] = alloca(sizeof(void *));
	  *((void **)args[i+3]) = fromNative(env, cb->arg_classes[i], cif->arg_types[i], cbargs[i], JNI_FALSE);
          break;
        case CVT_POINTER:
          *((void **)args[i+3]) = newJavaPointer(env, *(void **)cbargs[i]);
          break;
        case CVT_STRING:
          *((void **)args[i+3]) = newJavaString(env, *(void **)cbargs[i], JNI_FALSE);
          break;
        case CVT_WSTRING:
          *((void **)args[i+3]) = newJavaWString(env, *(void **)cbargs[i]);
          break;
        case CVT_STRUCTURE:
          *((void **)args[i+3]) = newJavaStructure(env, *(void **)cbargs[i], cb->arg_classes[i], JNI_FALSE);
          break;
        case CVT_STRUCTURE_BYVAL:
	  args[i+3] = alloca(sizeof(void *));
	  *((void **)args[i+3]) = newJavaStructure(env, cbargs[i], cb->arg_classes[i], JNI_TRUE);
          break;
        case CVT_CALLBACK:
          *((void **)args[i+3]) = newJavaCallback(env, *(void **)cbargs[i], cb->arg_classes[i]);
          break;
        case CVT_FLOAT:
	  args[i+3] = alloca(sizeof(double));
	  *((double *)args[i+3]) = *(float*)cbargs[i];
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
      toNative(env, *(void **)resp, oldresp, cb->cif.rtype->size, JNI_TRUE);
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
    default: break;
    }
    if (cb->flags) {
      for (i=0;i < cif->nargs;i++) {
        if (cb->flags[i] == CVT_STRUCTURE) {
          writeStructure(env, *(void **)cbargs[i]);
        }
      }
    }
  }
  else {
    jobject result;
    jobjectArray array =
      (*env)->NewObjectArray(env, cif->nargs, classObject, NULL);
    unsigned int i;

    for (i=0;i < cif->nargs;i++) {
      jobject arg = new_object(env, cb->arg_jtypes[i], cbargs[i], JNI_FALSE);
      (*env)->SetObjectArrayElement(env, array, i, arg);
    }
    result = (*env)->CallObjectMethod(env, self, cb->methodID, array);
    if ((*env)->ExceptionCheck(env)) {
      jthrowable throwable = (*env)->ExceptionOccurred(env);
      (*env)->ExceptionClear(env);
      if (!handle_exception(env, self, throwable)) {
        fprintf(stderr, "JNA: error handling callback exception, continuing\n");
      }
      if (cif->rtype->type != FFI_TYPE_VOID)
        memset(resp, 0, cif->rtype->size);
    }
    else {
      extract_value(env, result, resp, cif->rtype->size, JNI_TRUE);
    }
  }
}

// Handle automatic thread cleanup
static void detach_thread(void* data) {
  if (data != NULL) {
    JavaVM* jvm = (JavaVM *)data;
    (*jvm)->DetachCurrentThread(jvm);
  }
}

#ifdef _WIN32

static DWORD dwTlsIndex;
BOOL WINAPI DllMain(HINSTANCE hDLL, DWORD fdwReason, LPVOID lpvReserved) {
  switch (fdwReason) {
  case DLL_PROCESS_ATTACH:
    dwTlsIndex = TlsAlloc();
    if (dwTlsIndex == TLS_OUT_OF_INDEXES) {
      return FALSE;
    }
    break;
  case DLL_PROCESS_DETACH:
    TlsFree(dwTlsIndex);
    break;
  case DLL_THREAD_ATTACH:
    break;
  case DLL_THREAD_DETACH: {
    detach_thread(TlsGetValue(dwTlsIndex));
    break;
  }
  default:
    break;
  }
  return TRUE;
}

#else

#include <pthread.h>

static pthread_key_t key;
static void make_key() {
  pthread_key_create(&key, detach_thread);
}

#endif

/** Set up to detach the thread when it exits, or clear any handlers if the
    argument is NULL.
*/
static void 
jvm_detach_on_exit(JavaVM* jvm) {
#ifdef _WIN32
  TlsSetValue(dwTlsIndex, (void *)jvm);
#else
  static pthread_once_t key_once = PTHREAD_ONCE_INIT;
  pthread_once(&key_once, make_key);
  if (!jvm || pthread_getspecific(key) == NULL) {
    pthread_setspecific(key, jvm);
  }
#endif
}

static void
callback_dispatch(ffi_cif* cif, void* resp, void** cbargs, void* user_data) {
  callback* cb = ((callback *)user_data); 
  JavaVM* jvm = cb->vm;
  JNIEnv* env;
  int was_attached = (*jvm)->GetEnv(jvm, (void *)&env, JNI_VERSION_1_4) == JNI_OK;
  jboolean detach = was_attached ? JNI_FALSE : JNI_TRUE;

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
      detach = options.detach ? JNI_TRUE : JNI_FALSE;
      args.name = options.name;
    }
    if (daemon) {
      attach_status = (*jvm)->AttachCurrentThreadAsDaemon(jvm, (void*)&env, &args);
    }
    else {
      attach_status = (*jvm)->AttachCurrentThread(jvm, (void *)&env, &args);
    }
    if (attach_status != JNI_OK) {
      fprintf(stderr, "JNA: Can't attach native thread to VM for callback: %d\n", attach_status);
      return;
    }
    if (args.group) {
      (*env)->DeleteWeakGlobalRef(env, args.group);
    }
  }
  
  // Give the callback glue its own local frame to ensure all local references
  // are properly disposed
  if ((*env)->PushLocalFrame(env, 16) < 0) {
    fprintf(stderr, "JNA: Out of memory: Can't allocate local frame");
  }
  else {
    // Kind of a hack, use last error value rather than setting up our own TLS
    setLastError(0);
    callback_invoke(env, cb, cif, resp, cbargs);
    // Must be invoked immediately after return to avoid anything
    // stepping on errno/GetLastError
    switch(lastError()) {
    case THREAD_LEAVE_ATTACHED: detach = JNI_FALSE; break;
    case THREAD_DETACH: detach = JNI_TRUE; break;
    default: break; /* use default detach behavior */
    }
    (*env)->PopLocalFrame(env, NULL);
  }
  
  if (detach) {
    (*jvm)->DetachCurrentThread(jvm);
    jvm_detach_on_exit(NULL);
  }
  else if (!was_attached) {
    jvm_detach_on_exit(jvm);
  }
}

const char* 
jnidispatch_callback_init(JNIEnv* env) {

  if (!LOAD_CREF(env, Object, "java/lang/Object")) return "java.lang.Object";

  return NULL;
}
  
void
jnidispatch_callback_dispose(JNIEnv* env) {
  if (classObject) {
    (*env)->DeleteWeakGlobalRef(env, classObject);
    classObject = NULL;
  }
}

#ifdef __cplusplus
}
#endif
