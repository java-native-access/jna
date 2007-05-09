
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <jni.h>
#include "dispatch.h"

#ifdef __cplusplus
extern "C" {
#endif

#ifdef __linux__
#define NO_UNDERSCORE
#endif

static type_t get_type(char type);
static void callback_dispatch(JavaVM*, callback*, char*);

/* Template glue to translate native callback invocations into
 * callback_dispatch which can perform the java invocation.
 */
#define ARG_JVM 0x11111111
#define ARG_CB  0x22222222
#define ARG_DISPATCH 0xdeadbeef
#define CALLEE_SIZE 0x99999999

#ifdef _MSC_VER
static void
callback_asm_template() {

  /* Windows/MSVC -O1 */
  // push ebp
  // mov ebp,esp
  __asm {
    lea  eax,[ebp+8]
    push eax
    push ARG_CB
    push ARG_JVM
    lea  eax,callback_dispatch
    call eax
    add esp,12
    // if callback is stdcall, caller expects us to clean up stack
    mov ecx,CALLEE_SIZE
    cmp ecx,0
    je  not_stdcall
    pop ebp
    pop ecx
    add esp,CALLEE_SIZE
    push ecx
    push ebp
not_stdcall:
  }
  // pop ebp
  // ret
}
static void __template_end__() { }
#define TEMPLATE_SIZE \
  ((char*)__template_end__ - (char*)callback_asm_template)

#define RETURN_INT32(I) __asm mov eax,I
#define RETURN_INT64(L) { \
unsigned long _upper = (unsigned long)((L)>>32); \
unsigned long _lower = (unsigned long)(L); \
__asm mov eax,_lower __asm mov edx,_upper } 
#define RETURN_FP32(F) __asm fld F
#define RETURN_FP64(D) __asm fld D
#define RETURN_PTR(P) { \
unsigned long _lower = (unsigned long)(P); \
__asm mov eax,_lower } 

#endif /* _MSC_VER */

#ifdef __GNUC__

void __template_dummy__() {

#ifdef NO_UNDERSCORE
  asm(".globl callback_asm_template");
  asm("\ncallback_asm_template:");
#else
  asm(".globl _callback_asm_template");
  asm("\n_callback_asm_template:");
#endif

#ifdef __i386__

  asm("push %ebp");
  asm("mov %esp,%ebp");
  asm("lea 8(%ebp),%eax"); 
  asm("pushl %eax");
  asm("pushl %0" :: "i" (ARG_CB));
  asm("pushl %0" :: "i" (ARG_JVM));
  asm("movl %0,%%eax" :: "i" (ARG_DISPATCH));
  asm("call *%eax");
  asm("addl $12, %esp");
#ifdef _WIN32
  // if callback is stdcall, caller expects us to clean up stack
  asm("movl %0,%%ecx" :: "i" (CALLEE_SIZE));
  asm("cmpl $0,%ecx");
  asm("je  not_stdcall");
  asm("popl %ebp");
  asm("popl %ecx");
  asm("add %0,%%esp" :: "i" (CALLEE_SIZE));
  asm("pushl %ecx");
  asm("pushl %ebp");
  asm("\nnot_stdcall:");
#endif
  asm("popl %ebp");
  asm("ret");

#define RETURN_INT32(I) asm("movl %0,%%eax" :: "g" (I))
#define RETURN_VOID RETURN_INT32(0)
#define RETURN_INT64(L) \
asm("movl %0,%%edx\n\tmovl %1,%%eax" :: \
    "g" (*((unsigned long*)&(L)+1)), \
    "g" (*(unsigned long*)&(L)) : "%eax","%edx")
#define RETURN_FP32(F) asm("flds %0" :: "m" (F))
#define RETURN_FP64(D) asm("fldl %0" :: "m" (D))
#define RETURN_PTR(P) RETURN_INT32(((unsigned long)(P))&0xFFFFFFFF)

#elif __ppc__
  
#define LINKAGE 24
#define PARAMS (32*4) // callee params unknown, use max
#define REGS (0*4)
#define SPACE (LINKAGE+PARAMS+REGS)
#define ALIGN ((SPACE + 15) & -16)
#define ARGS (ALIGN+24)
#define ARG(N) (ARGS+((N)*4))
  asm("mflr r0");
  asm("stw r0,8(r1)");
  asm("stwu r1,%0(r1)" :: "i" (-ALIGN));
  asm("li r2,0");
  asm("\n.set CS_OFFSET,.-_callback_asm_template");
  asm("ori r2,r2,0");

#define STORE(GR,COUNT) \
  asm("cmpwi r2,%0" :: "i" (COUNT)); \
  asm("blt args_done"); \
  asm("stw " GR ",%0(r1)" :: "i" (ARG(COUNT-1)))

  // ensure any used registers are copied to the stack
  // callback_dispatch doesn't use any fp regs, so don't need to save any
  STORE("r3",1);
  STORE("r4",2);
  STORE("r5",3);
  STORE("r6",4);
  STORE("r7",5);
  STORE("r8",6);
  STORE("r9",7);
  STORE("r10",8);
  asm("\nargs_done:");
  asm("li r3,0");
  asm("\n.set VM_OFFSET,.-_callback_asm_template");
  asm("ori r3,r3,0");
  asm("oris r3,r3,0");
  asm("li r4,0");
  asm("\n.set CB_OFFSET,.-_callback_asm_template");
  asm("ori r4,r4,0");
  asm("oris r4,r4,0");
  asm("addi r5,r1,%0" :: "i" (ARGS));
  asm("li r12,0");
  asm("\n.set B_OFFSET,.-_callback_asm_template");
  asm("ori r12,r12,0");
  asm("oris r12,r12,0");
  asm("mtctr r12");
  asm("bctrl");

  asm("addi r1,r1,%0" :: "i" (ALIGN));
  asm("lwz r0,8(r1)");
  asm("mtlr r0");
  asm("blr");

#define RETURN_INT32(I) asm("lwz r3,%0" :: "m" (I))
#define RETURN_INT64(L) \
asm("mr r4,%0\n\tmr r3,%1" :: \
    "g" ((unsigned long)((L)&0xFFFFFFFF)), \
    "g" ((unsigned long)((L)>>32)) : "%r4","%r3")
#define RETURN_FP32(F) asm("lfs f1,%0" :: "m" (F) : "%f1")
#define RETURN_FP64(D) asm("lfd f1,%0" :: "m" (D) : "%f1")
#define RETURN_PTR(P) asm("lwz r3,%0" :: "m" ((unsigned long)(P)) : "%r3")

#endif /* __<arch>__ */

#ifdef NO_UNDERSCORE
  asm(".globl asm_template_end");
  asm("\nasm_template_end:");
#else
  asm(".globl _asm_template_end");
  asm("\n_asm_template_end:");
#endif
}
extern void callback_asm_template();
extern void asm_template_end();
#define TEMPLATE_SIZE (asm_template_end-callback_asm_template)

#endif /* __GNUC__ */

#ifndef RETURN_INT32
#define RETURN_INT32(I) return
#define RETURN_INT64(L) return
#define RETURN_FP32(F) return
#define RETURN_FP64(D) return
#define RETURN_PTR(P) return
#undef TEMPLATE_SIZE
#define TEMPLATE_SIZE (0)
#endif

callback*
create_callback(JNIEnv* env, jobject lib, jobject obj, jobject method,
                jobjectArray param_types, jclass return_type) {
  callback* cb;
  unsigned long* insns;
  int args_size = 0;
  jsize argc;
  JavaVM* vm;
  int len = TEMPLATE_SIZE;
  int i;

  if (TEMPLATE_SIZE == 0) {
    throwByName(env, "java/lang/UnsupportedOperationException",
                "Callbacks not supported on this platform");
    return NULL;
  }

  if ((*env)->GetJavaVM(env, &vm) != JNI_OK) {
    throwByName(env, "java/lang/UnsatisfiedLinkError",
                "Can't get Java VM");
    return NULL;
  }
  argc = (*env)->GetArrayLength(env, param_types);
  cb = (callback *)malloc(sizeof(callback));
  insns = (unsigned long*)malloc(len);
  cb->insns = insns;
  cb->object = (*env)->NewWeakGlobalRef(env, obj);
  cb->methodID = (*env)->FromReflectedMethod(env, method);
  cb->param_count = argc;
  for (i=0;i < argc;i++) {
    jclass cls = (*env)->GetObjectArrayElement(env, param_types, i);
    char jtype = get_jtype(env, cls);
    type_t type = get_type(jtype);
    cb->param_jtypes[i] = jtype;
    if (type == TYPE_INT64 || type == TYPE_FP64
        || (type == TYPE_PTR && sizeof(void*) == 8)) {
      args_size += 8;
    }
    else {
      args_size += 4;
    }
  }
  cb->return_jtype = get_jtype(env, return_type);
  cb->return_type = get_type(cb->return_jtype);

  // initialize and customize the callback template
  memcpy((void*)insns, (void*)callback_asm_template, len);

#ifdef __ppc__
  {
    // PPC can only load values 16 bits at a time, so it's easier
    // to track the offsets and insert values directly
    int vm_offset, cb_offset, cs_offset, b_offset;
    unsigned long* insn;

    asm("li %0,VM_OFFSET" : "=r" (vm_offset));
    asm("li %0,CB_OFFSET" : "=r" (cb_offset));
    asm("li %0,CS_OFFSET" : "=r" (cs_offset));
    asm("li %0,B_OFFSET" : "=r" (b_offset));

    insn = (unsigned long*)((char*)insns + cs_offset);
    *insn = (*insn & ~0xFFFF) | (args_size/sizeof(void*));
    
    insn = (unsigned long*)((char *)insns + cb_offset);
    *insn = (*insn & ~0xFFFF) | (((unsigned long)cb) & 0xFFFF);
    *(insn+1) = (*(insn+1) & ~0xFFFF) | (((unsigned long)cb) >> 16);

    insn = (unsigned long*)((char *)insns + vm_offset);
    *insn = (*insn & ~0xFFFF) | (((unsigned long)vm) & 0xFFFF);
    *(insn+1) = (*(insn+1) & ~0xFFFF) | (((unsigned long)vm) >> 16);

    insn = (unsigned long*)((char *)insns + b_offset);
    *insn = (*insn & ~0xFFFF) | (((unsigned long)callback_dispatch) & 0xFFFF);
    *(insn+1) = (*(insn+1) & ~0xFFFF) | (((unsigned long)callback_dispatch) >> 16);
  }
#endif

#if defined(__i386__) || defined(_WIN32)
  // lazy way to insert custom arguments, just look for our magic values
  // and replace them, knowing that they are not re-orged in the asm.
  for (i=0;i < len-3;i++) {
    unsigned long* addr = (unsigned long *)((char *)insns + i);
    unsigned long value = *addr;
    if (value == ARG_CB) {
      *addr = (unsigned long)cb;
      i += 3;
    }
    else if (value == ARG_JVM) {
      *addr = (unsigned long)vm;
      i += 3;
    }
    else if (value == ARG_DISPATCH) {
      *addr = (unsigned long)callback_dispatch;
      i += 3;
    }
#ifdef _WIN32
    else if (value == CALLEE_SIZE) {
      jclass cls = (*env)->FindClass(env, "com/sun/jna/win32/StdCall");
      jboolean stdcall = (*env)->IsInstanceOf(env, obj, cls)
        || (*env)->IsInstanceOf(env, lib, cls);
      *addr = stdcall ? args_size : 0;
    }
#endif
  }
#endif

  return cb;
}

static type_t
get_type(char type) {
  switch(type) {
  case 'Z': 
  case 'B': 
  case 'C': 
  case 'S':
  case 'I':
    return TYPE_INT32; 
  case 'J':
    return TYPE_INT64; 
  case 'F':
    return TYPE_FP32; 
  case 'D':
    return TYPE_FP64; 
  case 'L':
  default:
    return TYPE_PTR; 
  }
}

static void 
callback_dispatch(JavaVM* jvm, callback* cb, char* ap) {
  jobject obj;
  jmethodID mid;
  jvalue args[MAX_NARGS];
  jvalue result;
  JNIEnv* env;
  int attached;
  int i;

  attached = (*jvm)->GetEnv(jvm, (void *)&env, JNI_VERSION_1_4) == JNI_OK;

  if (!attached) {
    if ((*jvm)->AttachCurrentThread(jvm, (void *)&env, NULL) != JNI_OK) {
      fprintf(stderr, "Can't attach to current thread\n");
      return;
    }
  }

  // NOTE: some targets may require alignment of stack items...
  for (i=0;i < cb->param_count;i++) {
    switch(cb->param_jtypes[i]) {
    case 'L': {
      int ptr_size = sizeof(void*);
      jlong ptr = ptr_size == sizeof(jlong) ? *(jlong*)ap : *(jint*)ap;
      // TODO: create a corresponding java structure type
      // based on the callback argument type
      args[i].l = newJavaPointer(env, L2A(ptr));
      ap += ptr_size;
      break;
    }
    case 'J':
      args[i].j = *(jlong *)ap;
      ap += sizeof(jlong);
      break;
    case 'F':
      args[i].f = *(float *)ap;
      ap += sizeof(float);
      break;
    case 'D':
      args[i].d = *(double *)ap;
      ap += sizeof(double);
      break;
    case 'Z':
    case 'B':
    case 'S':
    case 'I':
    default:
      args[i].i = *(int *)ap;
      ap += sizeof(int);
      break;
    }
  }

  obj = (*env)->NewLocalRef(env, cb->object);
  mid = cb->methodID;
  // Avoid calling back to a GC'd object
  if ((*env)->IsSameObject(env, obj, NULL)) {
    result.j = 0;
  }
  else switch(cb->return_jtype) {
  case 'Z':
    result.i = (*env)->CallBooleanMethodA(env, obj, mid, args); break;
  case 'B':
    result.i = (*env)->CallByteMethodA(env, obj, mid, args); break;
  case 'C':
    result.i = (*env)->CallCharMethodA(env, obj, mid, args); break;
  case 'S':
    result.i = (*env)->CallShortMethodA(env, obj, mid, args); break;
  case 'I':
    result.i = (*env)->CallIntMethodA(env, obj, mid, args); break;
  case 'J':
    result.j = (*env)->CallLongMethodA(env, obj, mid, args); break;
  case 'F':
    result.f = (*env)->CallFloatMethodA(env, obj, mid, args); break;
  case 'D':
    result.d = (*env)->CallDoubleMethodA(env, obj, mid, args); break;
  case 'L':
  default:
    result.l = (*env)->CallObjectMethodA(env, obj, mid, args); break;
  }

  if (!attached) {
    (*jvm)->DetachCurrentThread(jvm);
  }
  
  switch(cb->return_type) {
  case TYPE_PTR:
    RETURN_PTR(result.l);
    return;
  case TYPE_INT32:
    RETURN_INT32(result.i);
    return;
  case TYPE_INT64:
    RETURN_INT64(result.j);
    return;
  case TYPE_FP32:
    RETURN_FP32(result.f);
    return;
  case TYPE_FP64:
    RETURN_FP64(result.d);
    return;
  }
}

#ifdef __cplusplus
}
#endif
