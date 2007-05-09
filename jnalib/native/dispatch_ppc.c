/*
 * Copies the arguments from the given array to C stack, invoke the
 * target function, and copy the result back.
 * Written for 32-bit PPC, tested on OSX 10.4
 * twall@users.sf.net
 */

#include <jni.h>
#include "dispatch.h"

extern void 
asm_dispatch(void (*func)(), // r3
             int nwords,     // r4
             word_t *c_args, // r5
             type_t rt,      // r6
             jvalue *result, // r7
             int* arg_types); // r8
#ifdef __ppc__
void __asm_dispatch_dummy__() {
  asm(".globl _asm_dispatch\n_asm_dispatch:");
  // GPR3-10 and FPR1-13 available for arguments
#define LINKAGE 24
#define PARAMS (32*16)
#define REGS (6*4)
#define SPACE (LINKAGE+PARAMS+REGS)
#define ALIGN ((SPACE + 15) & -16)
  asm("mflr r0");
  asm("stmw r26,%0(r1)" :: "i" (-REGS));
  asm("mr r12,r3");
  asm("mr r2,r5");
  asm("stw r0,8(r1)");
  asm("stwu r1,%0(r1)" :: "i" (-ALIGN));

#define ARGS (ALIGN+24)
#define FUNC (ARGS)
#define NWORDS (ARGS+4)
#define C_ARGS (ARGS+8)
#define RT (ARGS+12)
#define RESULT (ARGS+16)
#define ARG_TYPES (ARGS+20)

  asm("stw r4,%0(r1)" :: "i" (NWORDS));
  asm("stw r5,%0(r1)" :: "i" (C_ARGS));
  asm("stw r6,%0(r1)" :: "i" (RT));
  asm("stw r7,%0(r1)" :: "i" (RESULT));
  asm("stw r8,%0(r1)" :: "i" (ARG_TYPES));

  // register arguments (0-7)
  // callee parameters start at 24(r1)
  asm("\npush_arguments:");
  asm("mr r0,r4");
  asm("cmpwi r0, 0");
  asm("beq do_call");
  asm("lwz r3,0(r2)");
  asm("lwz r4,4(r2)");
  asm("lwz r5,8(r2)");
  asm("lwz r6,12(r2)");
  asm("lwz r7,16(r2)");
  asm("lwz r8,20(r2)");
  asm("lwz r9,24(r2)");
  asm("lwz r10,28(r2)");

  // push any additional arguments (beyond what fits in registers)
  // to the stack
  asm("addi r28,r2,32"); // dst: callee param area after first 8 regs 
  asm("addi r29,r1,56"); // src: original stack arguments after regs space
  asm("mr r2,r0");       // total word count
  asm("cmpwi r2,8");
  asm("ble push_fp");
  asm("addi r2,r2,-8");
  asm("\nstack_loop:");
  asm("lwz r0,0(r28)");
  asm("stw r0,0(r29)");
  asm("addi r28,r28,4");
  asm("addi r29,r29,4");
  asm("addi r2,r2,-1");
  asm("cmpwi r2,0");
  asm("bne stack_loop");

#define PUSHFP(FR) \
  asm("\nloop_" FR ":");\
  asm("lwz r29,%0(r1)" :: "i" (NWORDS));\
  asm("cmpw r26,r29");\
  asm("beq do_call");\
  asm("lwz r29,0(r28)");\
  asm("addi r26,r26,1");\
  asm("addi r28,r28,4");\
  asm("cmpwi r29,%0" :: "i" (TYPE_FP64));\
  asm("beq assign_" FR "d");\
  asm("cmpwi r29,%0" :: "i" (TYPE_FP32));\
  asm("beq assign_" FR "s");\
  asm("addi r27,r27,4");\
  asm("cmpwi r29,%0" :: "i" (TYPE_INT64));\
  asm("bne loop_" FR);\
  asm("addi r26,r26,1");\
  asm("addi r27,r27,4");\
  asm("b loop_" FR);\
  asm("\nassign_" FR "d:");\
  asm("lfd " FR ",0(r27)");\
  asm("addi r26,r26,1");\
  asm("addi r27,r27,8");\
  asm("b end_" FR);\
  asm("\nassign_" FR "s:");\
  asm("lfs " FR ",0(r27)");\
  asm("addi r27,r27,4");\
  asm("\nend_" FR ":")

  // now push FP arguments (if any)
  asm("\npush_fp:");
  asm("li r26,0"); // index into c_args
  asm("lwz r27,%0(r1)" :: "i" (C_ARGS));    // callee parameters
  asm("lwz r28,%0(r1)" :: "i" (ARG_TYPES)); // parameter types

  PUSHFP("f1");
  PUSHFP("f2");
  PUSHFP("f3");
  PUSHFP("f4");
  PUSHFP("f5");
  PUSHFP("f6");
  PUSHFP("f7");
  PUSHFP("f8");
  PUSHFP("f9");
  PUSHFP("f10");
  PUSHFP("f11");
  PUSHFP("f12");
  PUSHFP("f13");

  asm("\ndo_call:");
  asm("mtctr r12");
  asm("bctrl");

  /* Restore result pointer and return type from stack */
  asm("lwz r6,%0(r1)" :: "i" (RT));
  asm("lwz r7,%0(r1)" :: "i" (RESULT));
  asm("cmpwi r6,%0" :: "i" (TYPE_PTR));
  asm("bne not_ptr");
  asm("stw r3,4(r7)");
  asm("li r4,0");
  asm("ori r4,r4,0");
  asm("stw r4,0(r7)");
  asm("b cleanup");
  asm("\nnot_ptr:");
  asm("cmpwi r6,%0" :: "i" (TYPE_INT32));
  asm("bne not_i32");
  asm("stw r3,0(r7)");
  asm("b cleanup");
  asm("\nnot_i32:");
  asm("cmpwi r6,%0" :: "i" (TYPE_INT64));
  asm("bne not_i64");
  asm("stw r3,0(r7)");
  asm("stw r4,4(r7)");
  asm("b cleanup");
  asm("\nnot_i64:");
  asm("cmpwi r6,%0" :: "i" (TYPE_FP32));
  asm("bne not_f32");
  asm("stfs f1,0(r7)");
  asm("b cleanup");
  asm("nop");
  asm("nop");
  asm("\nnot_f32:");
  asm("stfd f1,0(r7)");
  asm("nop");
  asm("nop");
  asm("nop");

  asm("\ncleanup:");
  asm("addi r1, r1, %0" :: "i" (ALIGN));
  asm("lwz r0,8(r1)");
  asm("lmw r26,%0(r1)" :: "i" (-REGS));
  asm("mtlr r0");
  asm("blr");
}
#endif // __ppc__
