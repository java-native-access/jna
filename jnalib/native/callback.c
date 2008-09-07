/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

static void callback_dispatch(ffi_cif*, void*, void**, void*);

static jclass classObject;

callback*
create_callback(JNIEnv* env, jobject obj, jobject method,
                jobjectArray param_types, jclass return_type,
                callconv_t calling_convention) {
  callback* cb;
  ffi_abi abi = FFI_DEFAULT_ABI;
  ffi_status status;
  jsize argc;
  JavaVM* vm;
  char rtype;
  char msg[64];
  int i;

  if ((*env)->GetJavaVM(env, &vm) != JNI_OK) {
    throwByName(env, EUnsatisfiedLink, "Can't get Java VM");
    return NULL;
  }
  argc = (*env)->GetArrayLength(env, param_types);
  cb = (callback *)malloc(sizeof(callback));
  cb->ffi_closure = ffi_closure_alloc(sizeof(ffi_closure), &cb->x_closure);
  cb->object = (*env)->NewWeakGlobalRef(env, obj);
  cb->methodID = (*env)->FromReflectedMethod(env, method);
  cb->vm = vm;
 
  for (i=0;i < argc;i++) {
    jclass cls = (*env)->GetObjectArrayElement(env, param_types, i);
    cb->param_jtypes[i] = get_jtype(env, cls);
    cb->ffi_args[i] = get_ffi_type(env, cls, cb->param_jtypes[i]);
    if (!cb->param_jtypes[i]) {
      snprintf(msg, sizeof(msg), "Unsupported type at parameter %d", i);
      throwByName(env, EIllegalArgument, msg);
      goto failure_cleanup;
    }
  }

#if defined(_WIN32) && !defined(_WIN64)
  if (calling_convention == CALLCONV_STDCALL) {
    abi = FFI_STDCALL;
  }
#endif // _WIN32

  rtype = get_jtype(env, return_type);
  if (!rtype) {
    throwByName(env, EIllegalArgument, "Unsupported return type");
    goto failure_cleanup;
  }
  status = ffi_prep_cif(&cb->ffi_cif, abi, argc,
                        get_ffi_rtype(env, return_type, rtype),
                        &cb->ffi_args[0]);
  switch(status) {
  case FFI_BAD_ABI:
    snprintf(msg, sizeof(msg),
             "Invalid calling convention: %d", (int)calling_convention);
    throwByName(env, EIllegalArgument, msg);
    break;
  case FFI_BAD_TYPEDEF:
    snprintf(msg, sizeof(msg),
             "Invalid structure definition (native typedef error)");
    throwByName(env, EIllegalArgument, msg);
    break;
  case FFI_OK: 
    ffi_prep_closure_loc(cb->ffi_closure, &cb->ffi_cif, callback_dispatch, cb,
                         cb->x_closure);

    return cb;
  default:
    snprintf(msg, sizeof(msg),
             "Native callback setup failure: error code %d", status);
    throwByName(env, EIllegalArgument, msg);
    break;
  }

 failure_cleanup:
  free_callback(env, cb);

  return NULL;
}
void 
free_callback(JNIEnv* env, callback *cb) {
  (*env)->DeleteWeakGlobalRef(env, cb->object);
  ffi_closure_free(cb->ffi_closure);
  free(cb);
}

static void
callback_invoke(JNIEnv* env, callback *cb, ffi_cif* cif, void *resp, void **cbargs) {
  jobject self;

  self = (*env)->NewLocalRef(env, cb->object);
  // Avoid calling back to a GC'd object
  if ((*env)->IsSameObject(env, self, NULL)) {
    fprintf(stderr, "JNA: callback object has been garbage collected\n");
    memset(resp, 0, cif->rtype->size); 
  }
  else {
    jobject result;
    jobjectArray array =
      (*env)->NewObjectArray(env, cif->nargs, classObject, NULL);
    unsigned int i;

    for (i=0;i < cif->nargs;i++) {
      jobject arg = new_object(env, cb->param_jtypes[i], cbargs[i]);
      (*env)->SetObjectArrayElement(env, array, i, arg);
    }
    result = (*env)->CallObjectMethod(env, self, cb->methodID, array);
    if ((*env)->ExceptionCheck(env)) {
      fprintf(stderr, "JNA: uncaught exception in callback, continuing\n");
      memset(resp, 0, cif->rtype->size);
    }
    else {
      extract_value(env, result, resp, cif->rtype->size);
    }
  }
}

static void
callback_dispatch(ffi_cif* cif, void* resp, void** cbargs, void* user_data) {
  JavaVM* jvm = ((callback *)user_data)->vm;
  JNIEnv* env;
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
    return;
  }
  callback_invoke(env, (callback *)user_data, cif, resp, cbargs);
  (*env)->PopLocalFrame(env, NULL);

  if (!attached) {
    (*jvm)->DetachCurrentThread(jvm);
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
