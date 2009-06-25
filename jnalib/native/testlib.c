/* Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved
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

/* Native library implementation to support JUnit tests. */
#ifdef __cplusplus
extern "C" {
#endif

#include <wchar.h>
#include <stdio.h>
#include <stdarg.h>
#include <errno.h>

#ifdef _MSC_VER
typedef signed char int8_t;
typedef short int16_t;
typedef int int32_t;
typedef __int64 int64_t;
#else 
#include <stdint.h>
#endif

#ifdef _WIN32
#ifndef UNICODE
#define UNICODE
#endif
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#define EXPORT __declspec(dllexport)
#else
#define EXPORT
#endif

#ifdef _MSC_VER
#define LONG(X) X ## I64
#elif __GNUC__
#define LONG(X) X ## LL
#else
#error 64-bit type not defined for this compiler
#endif

#define MAGICSTRING "magic";
#define MAGICWSTRING L"magic"
#define MAGIC32 0x12345678L
#define MAGIC64 LONG(0x123456789ABCDEF0)
#define MAGICFLOAT -118.625
#define MAGICDOUBLE ((double)(-118.625))

#define MAGICDATA "0123456789"

EXPORT int test_global = MAGIC32;

// TODO: check more fields/alignments
struct CheckFieldAlignment {
  int8_t int8Field;
  int16_t int16Field;
  int32_t int32Field;
  int64_t int64Field;
  float floatField;
  double doubleField;
};

static int _callCount;

EXPORT void
setLastError(int err) {
#ifdef _WIN32  
  SetLastError(err);
#else
  errno = err;
#endif
}

EXPORT int
callCount() {
  return ++_callCount;
}

EXPORT int  
returnFalse() {
  return 0;
}

EXPORT int  
returnTrue() {
  return -1;
}

EXPORT int
returnBooleanArgument(int arg) {
  return arg;
}

EXPORT int8_t  
returnInt8Argument(int8_t arg) {
  return arg;
}

EXPORT wchar_t
returnWideCharArgument(wchar_t arg) {
  return arg;
}

EXPORT int16_t  
returnInt16Argument(int16_t arg) {
  return arg;
}

EXPORT int32_t  
returnInt32Zero() {
  int32_t value = 0;
  return value;
}

EXPORT int32_t  
returnInt32Magic() {
  int32_t value = MAGIC32;
  return value;
}

EXPORT int32_t  
returnInt32Argument(int32_t arg) {
  return arg;
}

EXPORT int64_t  
returnInt64Zero() {
  int64_t value = 0;
  return value;
}

EXPORT int64_t  
returnInt64Magic() {
  int64_t value = MAGIC64;
  return value;
}

EXPORT int64_t  
returnInt64Argument(int64_t arg) {
  return arg;
}

EXPORT long
returnLongZero() {
  long value = 0;
  return value;
}

EXPORT long  
returnLongMagic() {
  long value = sizeof(long) == 4 ? MAGIC32 : MAGIC64;
  return value;
}

EXPORT long  
returnLongArgument(long arg) {
  return arg;
}

EXPORT float  
returnFloatZero() {
  float value = 0.0;
  return value;
}

EXPORT float  
returnFloatMagic() {
  float value = MAGICFLOAT;
  return value;
}

EXPORT float  
returnFloatArgument(float arg) {
  return arg;
}

EXPORT double  
returnDoubleZero() {
  double value = (double)0.0;
  return value;
}

EXPORT double  
returnDoubleMagic() {
  double value = MAGICDOUBLE;
  return value;
}

EXPORT double  
returnDoubleArgument(double arg) {
  return arg;
}

EXPORT void* 
returnPointerArgument(void *arg) {
  return arg;
}

EXPORT char* 
returnStringMagic() {
  return MAGICSTRING;
}

EXPORT char* 
returnStringArgument(char *arg) {
  return arg;
}

EXPORT void*
returnObjectArgument(void* arg) {
  return arg;
}

EXPORT wchar_t* 
returnWStringMagic() {
  return MAGICWSTRING;
}

EXPORT wchar_t* 
returnWStringArgument(wchar_t *arg) {
  return arg;
}

EXPORT char*
returnStringArrayElement(char* args[], int which) {
  return args[which];
}

EXPORT wchar_t*
returnWideStringArrayElement(wchar_t* args[], int which) {
  return args[which];
}

EXPORT void*
returnPointerArrayElement(void* args[], int which) {
  return args[which];
}

EXPORT int
returnRotatedArgumentCount(char* args[]) {
  int count = 0;
  char* first = args[0];
  while (args[count] != NULL) {
    ++count;
    args[count-1] = args[count] ? args[count] : first;
  }
  return count;
}

typedef struct _TestStructure {
  double value;
} TestStructure;

EXPORT TestStructure*
returnStaticTestStructure() {
  static TestStructure test_structure;
  test_structure.value = MAGICDOUBLE;
  return &test_structure;
}

EXPORT TestStructure*
returnNullTestStructure() {
  return NULL;
}

typedef struct _VariableSizedStructure {
  int length;
  char buffer[1];
} VariableSizedStructure;

EXPORT char*
returnStringFromVariableSizedStructure(VariableSizedStructure* s) {
  return s->buffer;
}

typedef struct _TestAmallStructureByValue {
  int8_t c1;
  int8_t c2;
  int16_t s;
} TestSmallStructureByValue;

EXPORT TestSmallStructureByValue
returnSmallStructureByValue() {
  TestSmallStructureByValue v;
  v.c1 = 1;
  v.c2 = 2;
  v.s = 3;
  return v;
}

typedef struct _TestStructureByValue {
  int8_t c;
  int16_t s;
  int32_t i;
  int64_t j;
  TestStructure inner;
} TestStructureByValue;

EXPORT TestStructureByValue
returnStructureByValue() {
  TestStructureByValue v;
  v.c = 1;
  v.s = 2;
  v.i = 3;
  v.j = 4;
  v.inner.value = 5;
  return v;
}

typedef void (*callback_t)();
typedef int32_t (*int32_callback_t)(int32_t);
typedef callback_t (*cb_callback_t)(callback_t);

EXPORT int32_callback_t
returnCallback() {
  return &returnInt32Argument;
}

EXPORT int32_callback_t
returnCallbackArgument(int32_callback_t arg) {
  return arg;
}

EXPORT void 
incrementInt8ByReference(int8_t *arg) {
  if (arg) ++*arg;
}

EXPORT void 
incrementInt16ByReference(int16_t *arg) {
  if (arg) ++*arg;
}

EXPORT void 
incrementInt32ByReference(int32_t *arg) {
  if (arg) ++*arg;
}

EXPORT void 
incrementNativeLongByReference(long *arg) {
  if (arg) ++*arg;
}

EXPORT void 
incrementInt64ByReference(int64_t *arg) {
  if (arg) ++*arg;
}

EXPORT void 
complementFloatByReference(float *arg) {
  if (arg) *arg = -*arg;
}

EXPORT void 
complementDoubleByReference(double *arg) {
  if (arg) *arg = -*arg;
}

EXPORT void 
setPointerByReferenceNull(void **arg) {
  if (arg) *arg = NULL;
}

EXPORT int64_t 
checkInt64ArgumentAlignment(int32_t i, int64_t j, int32_t i2, int64_t j2) {
  if (i != 0x10101010 || j != LONG(0x1111111111111111)
      || i2 != 0x01010101 || j2 != LONG(0x2222222222222222))
    return -1;

  return i + j + i2 + j2;
}

EXPORT double 
checkDoubleArgumentAlignment(float f, double d, float f2, double d2) {
  // float:  1=3f800000 2=40000000 3=40400000 4=40800000
  // double: 1=3ff00... 2=40000... 3=40080... 4=40100...
  if (f != 1 || d != 2 || f2 != 3 || d2 != 4)
    return -1;

  return f + d + f2 + d2;
}

EXPORT void*
testStructurePointerArgument(struct CheckFieldAlignment* arg) {
  return arg;
}

EXPORT double
testStructureByValueArgument(struct CheckFieldAlignment arg) {
  return arg.int8Field + arg.int16Field + arg.int32Field
    + arg.int64Field + arg.floatField + arg.doubleField;
}

typedef struct ByValue8 { int8_t data; } ByValue8;
typedef struct ByValue16 { int16_t data; } ByValue16;
typedef struct ByValue32 { int32_t data; } ByValue32;
typedef struct ByValue64 { int64_t data; } ByValue64;
typedef struct ByValue128 { int64_t data, data1; } ByValue128;

EXPORT int8_t
testStructureByValueArgument8(struct ByValue8 arg){
  return arg.data;
}

EXPORT int16_t
testStructureByValueArgument16(struct ByValue16 arg){
  return arg.data;
}

EXPORT int32_t
testStructureByValueArgument32(struct ByValue32 arg){
  return arg.data;
}

EXPORT int64_t
testStructureByValueArgument64(struct ByValue64 arg){
  return arg.data;
}

EXPORT int64_t
testStructureByValueArgument128(struct ByValue128 arg){
  return arg.data + arg.data1;
}

typedef union _test_union_t {
  // Use non-primitive fields, doesn't matter what they are
  char* f1;
  int32_t f2;
} test_union_t;

typedef test_union_t (*test_union_cb_t)(test_union_t arg);

EXPORT test_union_t
testUnionByValueCallbackArgument(test_union_cb_t cb, test_union_t arg) {
  return (*cb)(arg);
}

typedef struct {
  int8_t field0;
  int16_t field1;
} Align16BitField8;
typedef struct {
  int8_t field0;
  int32_t field1;
} Align32BitField8;
typedef struct {
  int16_t field0;
  int32_t field1;
} Align32BitField16;
typedef struct {
  int32_t field0;
  int16_t field1;
  int32_t field2;
} Align32BitField16_2;
typedef struct {
  int32_t field0;
  int64_t field1;
  int32_t field2;
  int64_t field3;
} Align64BitField32;
typedef struct {
  int64_t field0;
  int8_t field1;
} PadTrailingSmallField;
static int STRUCT_SIZES[] = {
  sizeof(Align16BitField8),
  sizeof(Align32BitField8),
  sizeof(Align32BitField16),
  sizeof(Align32BitField16_2),
  sizeof(Align64BitField32),
  sizeof(PadTrailingSmallField),
};
EXPORT int32_t
getStructureSize(unsigned index) {
  if (index >= (int)sizeof(STRUCT_SIZES)/sizeof(STRUCT_SIZES[0]))
    return -1;
  return STRUCT_SIZES[index];
}

extern void exit(int);
#define FIELD(T,X,N) (((T*)X)->field ## N)
#define OFFSET(T,X,N) (int)(((char*)&FIELD(T,X,N))-((char*)&FIELD(T,X,0)))
#define V8(N) (N+1)
#define V16(N) ((((int32_t)V8(N))<<8)|V8(N))
#define V32(N) ((((int32_t)V16(N))<<16)|V16(N))
#define V64(N) ((((int64_t)V32(N))<<32)|V32(N))
#define VALUE(T,X,N) \
((sizeof(FIELD(T,X,N)) == 1) \
 ? V8(N)  : ((sizeof(FIELD(T,X,N)) == 2) \
 ? V16(N) : ((sizeof(FIELD(T,X,N)) == 4) \
 ? V32(N) : V64(N))))
#define VALIDATE_FIELDN(T,X,N) \
do { if (FIELD(T,X,N) != VALUE(T,X,N)) {*offsetp=OFFSET(T,X,N); *valuep=FIELD(T,X,N); return N;} } while (0)
#define VALIDATE1(T,X) VALIDATE_FIELDN(T,X,0)
#define VALIDATE2(T,X) do { VALIDATE1(T,X); VALIDATE_FIELDN(T,X,1); } while(0)
#define VALIDATE3(T,X) do { VALIDATE2(T,X); VALIDATE_FIELDN(T,X,2); } while(0)
#define VALIDATE4(T,X) do { VALIDATE3(T,X); VALIDATE_FIELDN(T,X,3); } while(0)

// returns the field index which failed, and the expected field offset
// returns -2 on success
EXPORT int32_t
testStructureAlignment(void* s, unsigned index, int* offsetp, int64_t* valuep) {
  if (index >= sizeof(STRUCT_SIZES)/sizeof(STRUCT_SIZES[0]))
    return -1;

  switch(index) {
  case 0: VALIDATE2(Align16BitField8,s); break;
  case 1: VALIDATE2(Align32BitField8,s); break;
  case 2: VALIDATE2(Align32BitField16,s); break;
  case 3: VALIDATE3(Align32BitField16_2,s); break;
  case 4: VALIDATE4(Align64BitField32,s); break;
  case 5: VALIDATE2(PadTrailingSmallField,s); break;
  }
  return -2;
}

EXPORT int32_t
testStructureArrayInitialization(struct CheckFieldAlignment arg[], int len) {
  int i;
  for (i=0;i < len;i++) {
    if (arg[i].int32Field != i)
      return i;
  }
  return -1;
}

EXPORT void
modifyStructureArray(struct CheckFieldAlignment arg[], int length) {
  int i;
  for (i=0;i < length;i++) {
    arg[i].int32Field = i;
    arg[i].int64Field = i+1;
    arg[i].floatField = (float)i+2;
    arg[i].doubleField = (double)i+3;
  }
}


EXPORT void
callVoidCallback(void (*func)(void)) {
  (*func)();
}

EXPORT int 
callBooleanCallback(int (*func)(int arg, int arg2),
                    int arg, int arg2) {
  return (*func)(arg, arg2);
}

EXPORT int8_t
callInt8Callback(int8_t (*func)(int8_t arg, int8_t arg2), int8_t arg, int8_t arg2) {
  return (*func)(arg, arg2);
}

EXPORT int16_t
callInt16Callback(int16_t (*func)(int16_t arg, int16_t arg2), int16_t arg, int16_t arg2) {
  return (*func)(arg, arg2);
}

EXPORT int32_t 
callInt32Callback(int32_t (*func)(int32_t arg, int32_t arg2),
                  int32_t arg, int32_t arg2) {
  return (*func)(arg, arg2);
}

EXPORT int32_t 
callInt32CallbackRepeatedly(int32_t (*func)(int32_t arg, int32_t arg2),
                            int32_t arg, int32_t arg2, int32_t count) {
  int i;
  int sum = 0;
  for (i=0;i < count;i++) {
    sum += (*func)(arg, arg2);
  }
  return sum;
}

EXPORT long 
callLongCallbackRepeatedly(long (*func)(long arg, long arg2),
                           long arg, long arg2, int32_t count) {
  int i;
  long sum = 0;
  for (i=0;i < count;i++) {
    sum += (*func)(arg, arg2);
  }
  return sum;
}

EXPORT long 
callNativeLongCallback(long (*func)(long arg, long arg2),
                       long arg, long arg2) {
  return (*func)(arg, arg2);
}

EXPORT int64_t 
callInt64Callback(int64_t (*func)(int64_t arg, int64_t arg2),
                  int64_t arg, int64_t arg2) {
  return (*func)(arg, arg2);
}

EXPORT float 
callFloatCallback(float (*func)(float arg, float arg2),
                  float arg, float arg2) {
  return (*func)(arg, arg2);
}

EXPORT double 
callDoubleCallback(double (*func)(double arg, double arg2),
                   double arg, double arg2) {
  return (*func)(arg, arg2);
}

EXPORT TestStructure*
callStructureCallback(TestStructure* (*func)(TestStructure*), TestStructure* arg) {
  return (*func)(arg);
}

EXPORT int
callCallbackWithByReferenceArgument(int (*func)(int arg, int* result), int arg, int* result) {
  return (*func)(arg, result);
}

EXPORT char*
callStringCallback(char* (*func)(char* arg), char* arg) {
  return (*func)(arg);
}

EXPORT char**
callStringArrayCallback(char** (*func)(char** arg), char** arg) {
  return (*func)(arg);
}

EXPORT wchar_t*
callWideStringCallback(wchar_t* (*func)(wchar_t* arg), wchar_t* arg) {
  return (*func)(arg);
}

struct cbstruct {
  void (*func)(void);
};

EXPORT void
callCallbackInStruct(struct cbstruct *cb) {
  (*cb->func)();
}

EXPORT TestStructureByValue
callCallbackWithStructByValue(TestStructureByValue (*func)(TestStructureByValue),
                              TestStructureByValue s) {
  return (*func)(s);
}

EXPORT callback_t
callCallbackWithCallback(cb_callback_t cb) {
  return (*cb)((callback_t)cb);
}

static int32_t 
structCallbackFunction(int32_t arg1, int32_t arg2) {
  return arg1 + arg2;
}

EXPORT void
setCallbackInStruct(struct cbstruct* cb) {
  cb->func = (void (*)(void))structCallbackFunction;
}


EXPORT int32_t 
fillInt8Buffer(int8_t *buf, int len, char value) {
  int i;

  for (i=0;i < len;i++) {
    buf[i] = value;
  }
  return len;
}

EXPORT int32_t 
fillInt16Buffer(int16_t *buf, int len, short value) {
  int i;
  for (i=0;i < len;i++) {
    buf[i] = value;
  }
  return len;
}

EXPORT int32_t 
fillInt32Buffer(int32_t *buf, int len, int32_t value) {
  int i;
  for (i=0;i < len;i++) {
    buf[i] = value;
  }
  return len;
}

EXPORT int32_t
fillInt64Buffer(int64_t *buf, int len, int64_t value) {
  int i;
  for (i=0;i < len;i++) {
    buf[i] = value;
  }
  return len;
}

EXPORT int32_t
addInt32VarArgs(const char *fmt, ...) {
  va_list ap;
  int32_t sum = 0;
  va_start(ap, fmt);
  
  while (*fmt) {
    switch (*fmt++) {
    case 'd':
      sum += va_arg(ap, int32_t);
      break;
    case 'l':
      sum += (int) va_arg(ap, int64_t);
      break;
    case 'c':
      sum += (int) va_arg(ap, int);
      break;
    default:
      break;
    }
  }
  va_end(ap);
  return sum;
}

EXPORT void
modifyStructureVarArgs(const char* fmt, ...) {
  struct _ss {
    int32_t magic;
  } *s;
  va_list ap;
  va_start(ap, fmt);
  while (*fmt) {
    switch(*fmt++) {
    case 's': 
      s = (struct _ss *)va_arg(ap, void*);
      s->magic = MAGIC32;
      break;
    default:
      break;
    }
  }

  va_end(ap);
}

EXPORT char *
returnStringVarArgs(const char *fmt, ...) {
  char* cp;
  va_list ap;
  va_start(ap, fmt);
  cp = va_arg(ap, char *);
  va_end(ap);
  return cp;
}

#if defined(_WIN32) && !defined(_WIN64)
///////////////////////////////////////////////////////////////////////
// stdcall tests
///////////////////////////////////////////////////////////////////////
EXPORT int32_t __stdcall
returnInt32ArgumentStdCall(int32_t arg) {
  return arg;
}

EXPORT TestStructureByValue __stdcall
returnStructureByValueArgumentStdCall(TestStructureByValue arg) {
  return arg;
}

EXPORT int32_t __stdcall
callInt32StdCallCallback(int32_t (__stdcall *func)(int32_t arg, int32_t arg2),
                         int32_t arg, int32_t arg2) {
  void* sp1 = NULL;
  void* sp2 = NULL;
  int value = -1;

#if defined(_MSC_VER)
  __asm mov sp1, esp;
  value = (*func)(arg, arg2);
  __asm mov sp2, esp;
#elif defined(__GNUC__)
  asm volatile (" movl %%esp,%0" : "=g" (sp1));
  value = (*func)(arg, arg2);
  asm volatile (" movl %%esp,%0" : "=g" (sp2));
#endif

  if (sp1 != sp2) {
    return -1;
  }
  return value;
}

#endif /* _WIN32 && !_WIN64 */

#include <jni.h>
#include <math.h>
JNIEXPORT jdouble JNICALL
Java_com_sun_jna_RawTest_00024JNI_cos(JNIEnv *env, jclass cls, jdouble x) {
  return cos(x);
}

#ifdef __cplusplus
}
#endif
