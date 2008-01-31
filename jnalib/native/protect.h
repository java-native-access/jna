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
_exc_handler(struct _EXCEPTION_RECORD* exception_record,
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
  exc_rec _er; \
  int _error = 0; \
  if (PROTECT) { \
    _er.ex_reg.handler = _exc_handler; \
    asm volatile ("movl %%fs:0, %0" : "=r" (_er.ex_reg.prev)); \
    asm volatile ("movl %0, %%fs:0" : : "r" (&_er)); \
    if ((_error = setjmp(_er.buf)) != 0) { \
      goto _exc_caught; \
    } \
  }

// The initial conditional is required to ensure GCC doesn't consider
// _exc_caught to be unreachable
#define PROTECTED_END(ONERR) do { \
  if (!_error) \
    goto _remove_handler; \
 _exc_caught: \
  ONERR; \
 _remove_handler: \
  if (PROTECT) { asm volatile ("movl %0, %%fs:0" : : "r" (_er.ex_reg.prev)); } \
} while(0)

#else // _WIN32
// Most other platforms support signals
// Catch both SIGSEGV and SIGBUS
#include <signal.h>
#include <setjmp.h>
static jmp_buf _context;
static volatile int _error;
static void _exc_handler(int sig) {
  if (sig == SIGSEGV || sig == SIGBUS) {
    longjmp(_context, sig);
  }
}

#define PROTECTED_START() \
  void* _old_segv_handler; \
  void* _old_bus_handler; \
  int _error = 0; \
  if (PROTECT) { \
    _old_segv_handler = signal(SIGSEGV, _exc_handler); \
    _old_bus_handler = signal(SIGBUS, _exc_handler); \
    if ((_error = setjmp(_context) != 0)) { \
      goto _exc_caught; \
    } \
  }

#define PROTECTED_END(ONERR) do { \
  if (!_error) \
    goto _remove_handler; \
 _exc_caught: \
  ONERR; \
 _remove_handler: \
  if (PROTECT) { \
    signal(SIGSEGV, _old_segv_handler); \
    signal(SIGBUS, _old_bus_handler); \
  } \
} while(0)
#endif

#endif // PROTECT

#endif // PROTECT_H
