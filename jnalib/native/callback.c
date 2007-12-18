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
#  undef SLIST_ENTRY
#  define MMAP_CLOSURE
#  define roundup(x,y) ((((x) + ((y) - 1)) / (y)) * (y))
#  define XM_ALLOC(SIZE) VirtualAlloc(0, SIZE, MEM_COMMIT|MEM_RESERVE, PAGE_EXECUTE_READWRITE)
#  define XM_FREE(P,SIZE) VirtualFree(P,SIZE,MEM_RELEASE)
#  define PAGE_SIZE w32_page_size()
typedef void* caddr_t;
#else
#  include <sys/types.h>
#  include <sys/param.h>
#  if defined(__linux__)
#    include <sys/user.h> /* for PAGE_SIZE */
#  endif
#  include <sys/mman.h>
#  ifdef sun
#    include <sys/sysmacros.h>
#  endif
#  define MMAP_CLOSURE
#  define XM_ALLOC(SIZE) mmap(0, SIZE, PROT_EXEC|PROT_READ|PROT_WRITE,MAP_ANON|MAP_PRIVATE,-1,0)
#  define XM_FREE(P,SIZE) munmap(P,SIZE)
#endif
#include "dispatch.h"

#ifdef __cplusplus
extern "C" {
#endif

static void callback_dispatch(ffi_cif*, void*, void**, void*);
static ffi_closure* alloc_closure(JNIEnv *env);
static void free_closure(JNIEnv* env, ffi_closure *closure);

#ifdef MMAP_CLOSURE
#include "queue.h"
static LIST_HEAD(closure_list, list_entry) closure_list;
static LIST_HEAD(alloc_list, list_entry) alloc_list;
#endif

static jclass classObject;

callback*
create_callback(JNIEnv* env, jobject obj, jobject method,
                jobjectArray param_types, jclass return_type,
                callconv_t calling_convention) {
  callback* cb;
  ffi_abi abi = FFI_DEFAULT_ABI;
  ffi_status status;
  int args_size = 0;
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
  cb->ffi_closure = alloc_closure(env);
  cb->object = (*env)->NewWeakGlobalRef(env, obj);
  cb->methodID = (*env)->FromReflectedMethod(env, method);
  cb->vm = vm;
 
  for (i=0;i < argc;i++) {
    jclass cls = (*env)->GetObjectArrayElement(env, param_types, i);
    cb->param_jtypes[i] = get_jtype(env, cls);
    cb->ffi_args[i] = get_ffi_type(env, cls, cb->param_jtypes[i]);
    if (!cb->param_jtypes[i]) {
      sprintf(msg, "Unsupported type at parameter %d", i);
      throwByName(env, EIllegalArgument, msg);
      goto failure_cleanup;
    }
  }

#ifdef _WIN32
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
    sprintf(msg, "Invalid calling convention: %d", (int)calling_convention);
    throwByName(env, EIllegalArgument, msg);
    break;
  case FFI_BAD_TYPEDEF:
    sprintf(msg, "Invalid structure definition (native typedef error)");
    throwByName(env, EIllegalArgument, msg);
    break;
  case FFI_OK: 
    ffi_prep_closure(cb->ffi_closure, &cb->ffi_cif, callback_dispatch, cb);
    return cb;
  default:
    sprintf(msg, "Native callback setup failure: error code %d", status);
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
  free_closure(env, cb->ffi_closure);
  free(cb);
}

static void
callback_dispatch(ffi_cif* cif, void* resp, void** cbargs, void* user_data) {
  callback* cb = (callback *) user_data;
  JavaVM* jvm = cb->vm;
  jobject self;
  JNIEnv* env;
  int attached;
  unsigned int i;
  jobjectArray array;
  
  attached = (*jvm)->GetEnv(jvm, (void *)&env, JNI_VERSION_1_4) == JNI_OK;
  if (!attached) {
    if ((*jvm)->AttachCurrentThread(jvm, (void *)&env, NULL) != JNI_OK) {
      fprintf(stderr, "JNA: Can't attach to current thread\n");
      return;
    }
  }
  
  self = (*env)->NewLocalRef(env, cb->object);
  // Avoid calling back to a GC'd object
  if ((*env)->IsSameObject(env, self, NULL)) {
    fprintf(stderr, "JNA: callback object has been garbage collected\n");
    memset(resp, 0, cif->rtype->size); 
  }
  else {
    jobject result;
    array = (*env)->NewObjectArray(env, cif->nargs, classObject, NULL);
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

  if (!attached) {
    (*jvm)->DetachCurrentThread(jvm);
  }
}

// Use mmap for closure memory, if available.
// A page of memory is allocated and divided into closure-sized
// chunks managed in a queue.  The queue is protected by
// Java synchronization locks to ensure single-threaded access.
#ifdef MMAP_CLOSURE
# ifndef PAGE_SIZE
#  if defined(PAGESIZE)
#   define PAGE_SIZE PAGESIZE
#  elif defined(NBPG)
#   define PAGE_SIZE NBPG
#  endif   
# endif
typedef struct list_entry {
  LIST_ENTRY(list_entry) list;
} list_entry;
#endif

#ifdef _WIN32
static int w32_page_size() {
  static int page_size = 0;
  if (page_size == 0) {
    SYSTEM_INFO info;
    GetSystemInfo(&info);
    page_size = info.dwPageSize;
  }
  return page_size;
}
#endif

jboolean 
jnidispatch_callback_init(JNIEnv* env) {

  if (!LOAD_CREF(env, Object, "java/lang/Object")) return JNI_FALSE;

#ifdef MMAP_CLOSURE
  LIST_INIT(&closure_list);
  LIST_INIT(&alloc_list);
#endif

  return JNI_TRUE;
}
  
void
jnidispatch_callback_dispose(JNIEnv* env) {
  if (classObject) {
    (*env)->DeleteWeakGlobalRef(env, classObject);
    classObject = NULL;
  }
#ifdef MMAP_CLOSURE
  while (alloc_list.lh_first != NULL) {
    list_entry* entry = alloc_list.lh_first;
    XM_FREE((caddr_t)entry, PAGE_SIZE);
    LIST_REMOVE(entry, list);
  }
#endif
}

static ffi_closure*
alloc_closure(JNIEnv* env) {
#ifdef MMAP_CLOSURE
  list_entry* entry = NULL;
  
  if (closure_list.lh_first == NULL) {
    /*
     * Get a new page from the kernel and divvy that up
     */
    int clsize = roundup(sizeof(ffi_closure), sizeof(list_entry));
    int i;
    caddr_t ptr = XM_ALLOC(PAGE_SIZE);
    if (ptr == NULL) {
      return NULL;
    }
    LIST_INSERT_HEAD(&alloc_list, (list_entry*)ptr, list);

    for (i = 0; i <= (int)(PAGE_SIZE - clsize); i += clsize) {
      entry = (list_entry *)(ptr + i);
      LIST_INSERT_HEAD(&closure_list, entry, list);            
    }
  }
  entry = closure_list.lh_first;
  LIST_REMOVE(entry, list);
  
  memset(entry, 0, sizeof(ffi_closure));
  return (ffi_closure *)entry;
#else
  return (ffi_closure *)calloc(1, sizeof(ffi_closure));
#endif
}
  
static void
free_closure(JNIEnv* env, ffi_closure *ffi_closure) {
#ifdef MMAP_CLOSURE
  LIST_INSERT_HEAD(&closure_list, (list_entry*)ffi_closure, list);
#else
  free(closure);
#endif
}

#ifdef __cplusplus
}
#endif
