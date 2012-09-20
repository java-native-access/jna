/* Copyright (c) 20012 Timothy Wall, All Rights Reserved
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
#ifndef THREAD_H
#define THREAD_H

#ifndef _WIN32

#include <pthread.h>

#ifdef __APPLE__
struct _thread_cleanup_data {
  struct __darwin_pthread_handler_rec* __handler;
  JavaVM* __jvm;
};
#define THREAD_EXIT_CLEANUP(DATA) do { \
  struct _thread_cleanup_data* __data = (struct _thread_cleanup_data *)DATA; \
  pthread_t __self = pthread_self();                                    \
  fprintf(stderr, "detach thread %p\n", __self); \
  __self->__cleanup_stack = __data->__handler->__next;    \
  (*(__data->__jvm))->DetachCurrentThread(__data->__jvm); \
  /*free(__data->__handler);*/                            \
  free(__data);                                       \
  fprintf(stderr, "detach thread %p done\n", __self); \
} while(0)
#define ON_THREAD_EXIT(PROC,JVM) do { \
  struct _thread_cleanup_data *__data = (struct _thread_cleanup_data *)malloc(sizeof(struct _thread_cleanup_data)); \
  struct __darwin_pthread_handler_rec *__handler =                      \
    (struct __darwin_pthread_handler_rec *)malloc(sizeof(struct __darwin_pthread_handler_rec)); \
  pthread_t __self = pthread_self();                                    \
  __handler->__routine = PROC;                                          \
  __handler->__arg = __data;                                            \
  __handler->__next = __self->__cleanup_stack;                          \
  __self->__cleanup_stack = __handler;                                  \
  __data->__handler = __handler;                                        \
  __data->__jvm = JVM;                                                  \
} while(0)
#endif /* __APPLE__ */

#endif /* !_WIN32 */

#endif /* THREAD_H */
