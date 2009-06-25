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
// The w32 implementation is based on code by Ranjit Mathew
// http://gcc.gnu.org/ml/java/2003-03/msg00243.html
#ifndef PROTECT

#define PROTECTED_START()
#define PROTECTED_END(ONERR)

#else
#ifdef _WIN32
#ifdef __GNUC__
#include <excpt.h>
#else
// copied from mingw header
typedef EXCEPTION_DISPOSITION (*PEXCEPTION_HANDLER)
  (struct _EXCEPTION_RECORD*, void*, struct _CONTEXT*, void*);
typedef struct _EXCEPTION_REGISTRATION {
  struct _EXCEPTION_REGISTRATION*	prev;
  PEXCEPTION_HANDLER                    handler;
} EXCEPTION_REGISTRATION, *PEXCEPTION_REGISTRATION;
#endif
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

#ifdef _MSC_VER
#define PROTECTED_START() __try {
#define PROTECTED_END(ONERR) } __except((PROTECT)?EXCEPTION_EXECUTE_HANDLER:EXCEPTION_CONTINUE_SEARCH) { ONERR; }
#else
#ifdef _WIN64
#error "GCC does not implement SEh"
#else
#define SEH_TRY(ER) \
  __asm__ ("movl %%fs:0, %0" : "=r" ((ER).ex_reg.prev));  \
  __asm__ ("movl %0, %%fs:0" : : "r" (&(ER)))
#define SEH_CATCH(ER) \
  __asm__ ("movl %0, %%fs:0" : : "r" ((ER).ex_reg.prev))
#endif /* !_WIN64 */

#define PROTECTED_START() \
  exc_rec _er; \
  int _error = 0; \
  if (PROTECT) { \
    _er.ex_reg.handler = _exc_handler; \
    SEH_TRY(_er); \
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
  if (PROTECT) { SEH_CATCH(_er); } \
} while(0)

#endif /* !_MSC_VER */

#else // _WIN32

// Most other platforms support signals
// Catch both SIGSEGV and SIGBUS
#include <signal.h>
#include <setjmp.h>
static jmp_buf _context;
static void* _old_segv_handler = NULL;
static void* _old_bus_handler = NULL;
static volatile int _error;
static void _exc_handler(int sig) {
  if (sig == SIGSEGV || sig == SIGBUS) {
    longjmp(_context, sig);
  }
}

#define PROTECTED_START() \
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
