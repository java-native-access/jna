/*
 * Copyright (c) 1998 Sun Microsystems.  All Rights Reserved.
 * Copyright (c) 2007 Timothy Wall. All Rights Reserved.
 */

#include <jni.h>
#include "dispatch.h"

#if defined(__i386__) || defined(_MSC_VER)
/*
 * Copies the arguments from the given array to C stack, invoke the
 * target function, and copy the result back.
 */
void 
asm_dispatch(void (*func)(), int nwords, word_t *c_args, 
             type_t rtype, jvalue *resP, int* cdecl_flag) {
  /* linux/x86 */
#ifdef __GNUC__
#ifdef __x86_64__
#error No 64-bit support yet
#endif

  asm("movl %0, %%esi" : : "g" (c_args) : "%esi");
  asm("movl %0, %%edx" : : "g" (nwords*sizeof(word_t)) : "%edx");

  asm("subl $4, %edx");
  asm("jc  args_done");

  /* Push the last argument first. */
  asm("\nargs_loop:");
  asm("movl (%esi,%edx), %eax");
  asm("pushl %eax");
  asm("subl $4, %edx");
  asm("jge args_loop");

  asm("\nargs_done:");
  asm("call *%0" : : "g" (func) : "%eax", "%edx");
  
  asm("movl %0, %%esi" : : "g" (resP) : "%esi");
  asm("cmpl %1, %0" : : "g" (rtype), "i" (TYPE_PTR));
  asm("jne not_ptr");

  /* ptr */
  asm("movl %eax, (%esi)");
  asm("movl %edx, 4(%esi)");
  asm("jmp done");
  
  asm("\nnot_ptr:");
  asm("cmpl %1, %0" : : "g" (rtype), "i" (TYPE_INT32));
  asm("jne not_i32");
      
  /* i32 */
  asm("movl %eax, (%esi)");
  asm("jmp done");
  
  asm("\nnot_i32:");
  asm("cmpl %1, %0" : : "g" (rtype), "i" (TYPE_FP32));
  asm("jne not_f32");
  
  /* f32 */
  asm("fstps (%esi)");
  asm("jmp done");
  
  asm("\nnot_f32:");
  asm("cmpl %1, %0" :: "g" (rtype), "i" (TYPE_FP64));
  asm("jne not_f64");

  /* f64 */
  asm("fstpl (%esi)");
  asm("jmp done");

  asm("\nnot_f64:");
  asm("movl %eax, (%esi)");
  asm("movl %edx, 4(%esi)");
  
  asm("\ndone:");
  // pop callee arguments if cdecl; do nothing if stdcall
  asm("cmpl $0,%0" :: "g" (cdecl_flag));
  asm("je no_cleanup");
  asm("addl %0, %%esp" : : "g" (nwords*sizeof(word_t)) : "%esp");
  asm("\nno_cleanup:");

#endif
#ifdef _MSC_VER
  /* Windows/MSVC */
  __asm {
    
    mov esi, c_args
    mov edx, nwords
    // word address -> byte address
    shl edx, 2

    sub edx, 4
    jc  args_done

    // Push the last argument first.
args_loop:
    mov eax, DWORD PTR [esi+edx]
    push eax
    sub edx, 4
    jge SHORT args_loop

args_done:
    call func

    mov esi, resP
    cmp rtype, TYPE_PTR
    jne not_ptr
    // ptr
    mov [esi], eax
    mov [esi+4], edx
    jmp done

not_ptr:
    cmp rtype, TYPE_INT32
    jne not_i32
    // i32
    mov [esi], eax
    jmp done

not_i32:
    cmp rtype, TYPE_FP32
    jne not_f32
    // f32
    fstp DWORD PTR [esi]
    jmp done

not_f32:
    cmp rtype, TYPE_FP64
    jne not_f64
    // f64
    fstp QWORD PTR [esi]
    jmp done

not_f64:
    // i64
    mov [esi], eax
    mov [esi+4], edx

done:
    // pop callee arguments if cdecl; do nothing if stdcall
    cmp cdecl_flag,0
    je no_cleanup
    mov edx, nwords
    shl edx, 2
    add esp, edx
no_cleanup:
    }
#endif
}
#endif // __i386__ || _MSC_VER
