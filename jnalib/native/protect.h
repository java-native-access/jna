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
#ifndef PROTECT_H
#define PROTECT_H

// Native memory access protection
// 
// Enable or disable by changing the value of the PROTECT flag.
//
// Example usage:
//
// #define PROTECT _protect
// static int _protect;
// #include "protect.h"
// 
// void my_function() {
//   int variable_decls;
//   PROTECTED_START();
//   // do some dangerous stuff here
//   PROTECTED_END(fprintf(stderr, "Error!"));
// }
//
// The PROTECT_START() macro must immediately follow any variable declarations 
//
// The w32 implementation is courtesy of Ranjit Mathew
// http://gcc.gnu.org/ml/java/2003-03/msg00243.html
#ifndef PROTECT

#define PROTECTED_START()
#define PROTECTED_END(ONERR)

#else
#ifdef _WIN32
#include <excpt.h>
#include <setjmp.h>

typedef struct _exc_rec {
  EXCEPTION_REGISTRATION ex_reg;
  jmp_buf buf;
  struct _EXCEPTION_RECORD er;
} exc_rec;

static EXCEPTION_DISPOSITION __cdecl
__exc_handler(struct _EXCEPTION_RECORD* exception_record,
              void *establisher_frame,
              struct _CONTEXT *context_record,
              void* dispatcher_context) {
  exc_rec* xer = (exc_rec *)establisher_frame;
  xer->er = *exception_record;
  longjmp(xer->buf, exception_record->ExceptionCode);
  // Never reached
  return ExceptionContinueExecution;
}

#define PROTECTED_START() \
  exc_rec __er; \
  int __error = 0; \
  if (PROTECT) { \
    __er.ex_reg.handler = __exc_handler; \
    asm volatile ("movl %%fs:0, %0" : "=r" (__er.ex_reg.prev)); \
    asm volatile ("movl %0, %%fs:0" : : "r" (&__er)); \
    if ((__error = setjmp(__er.buf)) != 0) { \
      goto __exc_caught; \
    } \
  }

// The initial conditional is required to ensure GCC doesn't consider
// _exc_caught to be unreachable
#define PROTECTED_END(ONERR) do { \
  if (!__error) \
    goto __remove_handler; \
 __exc_caught: \
  ONERR; \
 __remove_handler: \
  if (PROTECT) { asm volatile ("movl %0, %%fs:0" : : "r" (__er.ex_reg.prev)); } \
} while(0)

#else // _WIN32
// Most other platforms support signals
// Catch both SIGSEGV and SIGBUS
#include <signal.h>
#include <setjmp.h>
static jmp_buf __context;
static volatile int __error;
static void _exc_handler(int sig) {
  if (sig == SIGSEGV || sig == SIGBUS) {
    longjmp(__context, sig);
  }
}

#define PROTECTED_START() \
  void* __old_segv_handler; \
  void* __old_bus_handler; \
  int __error = 0; \
  if (PROTECT) { \
    __old_segv_handler = signal(SIGSEGV, __exc_handler); \
    __old_bus_handler = signal(SIGBUS, __exc_handler); \
    if ((__error = setjmp(__context) != 0)) { \
      goto __exc_caught; \
    } \
  }

#define PROTECTED_END(ONERR) do { \
  if (!__error) \
    goto __remove_handler; \
 __exc_caught: \
  ONERR; \
 __remove_handler: \
  if (PROTECT) { \
    signal(SIGSEGV, __old_segv_handler); \
    signal(SIGBUS, __old_bus_handler); \
  } \
} while(0)
#endif

#endif // PROTECT

#endif // PROTECT_H
