// Quick and dirty test of asm dispatch code

#include <jni.h>
#if defined(_WIN32)
#define alloca _alloca
#else
#include <alloca.h>
#endif
#include "dispatch.h"

extern void asm_c_dispatch(void (*func)(), int nwords, word_t *c_args,
                           type_t rt, jvalue *result, int* types);

int intfunc(int r3, int r4, int r5, int r6, int r7, int r8, int r9, int r10, int st0, int st1) {
  printf("\nint\n");
  printf("args: 0x%x 0x%x  0x%x 0x%x 0x%x 0x%x 0x%x 0x%x 0x%x 0x%x\n",
         r3, r4, r5, r6, r7, r8, r9, r10, st0, st1);
  return r4;
}

jlong longfunc(int r3, jlong r4, int r6, jlong r7, int r9, jlong st0, jlong st1) {
  printf("\nmixed\n");
  printf("args: 0x%x 0x%llx  0x%x 0x%llx 0x%x 0x%llx 0x%llx\n",
         r3, r4, r6, r7, r9, st0, st1);
  return st1;
}

float floatfunc(float f1, float f2, float f3, float f4, 
                float f5, float f6, float f7, float f8, 
                float f9, float f10, float f11, float f12, 
                float f13, float st0) {
  printf("\nfloat\n");
  printf("args: %f %f %f %f %f %f %f %f %f %f %f %f %f %f\n",
         f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,st0);
  return st0;
}

double doublefunc(float f1, float f2, double f3, float f4, 
                  double f5, float f6, double f7, float f8, 
                  double f9, float f10, double f11, double f12, 
                  double f13, float st0, double st1) {
  printf("\nmixed FP\n");
  printf("args: %f %f %lf %f %lf %f %lf %f %lf %f %lf %lf%lf %f %lf\n",
         f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,st0,st1);
  return st1;
}

int main(int argc, char* argv[]) {

  unsigned long args[] = {
    0x10000001,
    0x11111111,
    0x22222222,
    0x33333333,
    0x44444444,
    0x55555555,
    0x66666666,
    0x77777777,
    0x88888888,
    0x99999999,
    0xAAAAAAAA,
    0xBBBBBBBB,
  };
  jvalue result;
  {
    int types[] = {
      TYPE_INT32,
      TYPE_INT32,
      TYPE_INT32,
      TYPE_INT32,
      TYPE_INT32,
      TYPE_INT32,
      TYPE_INT32,
      TYPE_INT32,
      TYPE_INT32,
      TYPE_INT32,
    };
    asm_c_dispatch((void*)intfunc, sizeof(types)/sizeof(int),
                   (void *)args, TYPE_INT32, &result, types);
    printf("result=0x%lx\n", result.i);
  }

  {
    int types[] = {
      TYPE_INT32,
      TYPE_INT64,
      TYPE_INT64,
      TYPE_INT32,
      TYPE_INT64,
      TYPE_INT64,
      TYPE_INT32,
      TYPE_INT64,
      TYPE_INT64,
      TYPE_INT64,
      TYPE_INT64,
    };
    asm_c_dispatch((void*)longfunc, sizeof(types)/sizeof(int),
                   (void *)args, TYPE_INT64, &result, types);
    printf("result=0x%llx\n", result.j);
  }

  {
    int types[] = {
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP32, // on stack
    };
    float* fargs = (float*)alloca(sizeof(types)/sizeof(int));
    int i;
    for (i=0;i < sizeof(types)/sizeof(int);i++) {
      fargs[i] = (float)(i+1);
    }

    asm_c_dispatch((void*)floatfunc, sizeof(types)/sizeof(int),
                   (void *)fargs, TYPE_FP32, &result, types);
    printf("result=0x%f\n", result.f);
  }

  {
    int i;
    int types[] = {
      TYPE_FP32,
      TYPE_FP32,
      TYPE_FP64,
      TYPE_FP64,
      TYPE_FP32,

      TYPE_FP64,
      TYPE_FP64,
      TYPE_FP32,
      TYPE_FP64,
      TYPE_FP64,
      TYPE_FP32,

      TYPE_FP64,
      TYPE_FP64,
      TYPE_FP32,
      TYPE_FP64,
      TYPE_FP64,
      TYPE_FP64,
      TYPE_FP64,

      TYPE_FP64,
      TYPE_FP64,
      TYPE_FP32, // on stack
      TYPE_FP64,
      TYPE_FP64,
    };
    long* dargs = (long *)alloca(sizeof(types)/sizeof(int));

    int value = 0;
    for (i=0;i < sizeof(types)/sizeof(int);i++) {
      if (types[i] == TYPE_FP32) {
        *(float *)(dargs + i) = (float)(++value);
      }
      else {
        *((double*)(dargs + i)) = (double)(++value);
        ++i;
      }
    }
    asm_c_dispatch((void*)doublefunc, sizeof(types)/sizeof(int),
                   (void *)dargs, TYPE_FP64, &result, types);
    printf("result=0x%lf\n", result.d);
  }

  {
    callback* cb = create_callback(0, 0, 0, 0, 0, 0);
    int (*func)() = (void*)cb->insns;
    printf("callback created\n");
    printf("result=%d\n", (*func)(0x1111, 0x2222, 0x3333));
  }

  return 0;
}
