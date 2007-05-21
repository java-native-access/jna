/* Standard C calling convention tests. */

#ifdef __cplusplus
extern "C" {
#endif

#include <wchar.h>
#include <stdio.h>

#ifdef _WIN32
#define EXPORT __declspec(dllexport)
#else
#define EXPORT
#endif

#ifdef _MSC_VER
#define int64 __int64
#define LONG(X) X ## I64
#elif __GNUC__
#define int64 long long
#define LONG(X) X ## LL
#else
#error 64-bit type not defined for this platform
#endif

// Force some local operations so that we don't get false
// test passes due to arguments and return values accidentally
// coinciding
volatile int __dummy__ = 0;
#undef NOP
#define NOP(X) ((X)+__dummy__)

#define MAGICSTRING "magic";
#define MAGICWSTRING L"magic"
#define MAGIC32 0x12345678L
#define MAGIC64 LONG(0x123456789ABCDEF0)
#define MAGICFLOAT -118.625
#define MAGICDOUBLE ((double)(-118.625))
#define int8 signed char
#define int16 short
#define int32 long
#define MAGICDATA "0123456789"

// TODO: check more fields/alignments
struct CheckFieldAlignment {
  int32 int32Field;
  int64 int64Field;
  float floatField;
  double doubleField;
};

static int _callCount;

static void nonleaf() {
  static int dummy = 1;
  dummy = dummy ^ 0x12345678;
  if (dummy == 0x12345678) 
    printf("%c", 0);
}

EXPORT int
callCount() {
  return ++_callCount;
}

EXPORT int  
returnFalse() {
  nonleaf();
  return 0;
}

EXPORT int  
returnTrue() {
  nonleaf();
  return -1;
}

EXPORT int
returnBooleanArgument(int arg) {
  nonleaf();
  return NOP(arg);
}

EXPORT int8  
returnInt8Argument(int8 arg) {
  nonleaf();
  return NOP(arg);
}

EXPORT int16  
returnInt16Argument(int16 arg) {
  nonleaf();
  return NOP(arg);
}

EXPORT int32  
returnInt32Zero() {
  int32 value = 0;
  nonleaf();
  return value;
}

EXPORT int32  
returnInt32Magic() {
  int32 value = MAGIC32;
  nonleaf();
  return value;
}

EXPORT int32  
returnInt32Argument(int32 arg) {
  nonleaf();
  return NOP(arg);
}

EXPORT int64  
returnInt64Zero() {
  int64 value = 0;
  nonleaf();
  return value;
}

EXPORT int64  
returnInt64Magic() {
  int64 value = MAGIC64;
  nonleaf();
  return value;
}

EXPORT int64  
returnInt64Argument(int64 arg) {
  nonleaf();
  return NOP(arg);
}

EXPORT float  
returnFloatZero() {
  float value = 0.0;
  nonleaf();
  return value;
}

EXPORT float  
returnFloatMagic() {
  float value = MAGICFLOAT;
  nonleaf();
  return value;
}

EXPORT float  
returnFloatArgument(float arg) {
  nonleaf();
  return NOP(arg);
}

EXPORT double  
returnDoubleZero() {
  double value = (double)0.0;
  nonleaf();
  return value;
}

EXPORT double  
returnDoubleMagic() {
  double value = MAGICDOUBLE;
  nonleaf();
  return NOP(value);
}

EXPORT double  
returnDoubleArgument(double arg) {
  nonleaf();
  return NOP(arg);
}

EXPORT void* 
returnPointerArgument(void *arg) {
  nonleaf();
  return arg;
}

EXPORT char* 
returnStringMagic() {
  nonleaf();
  return MAGICSTRING;
}

EXPORT char* 
returnStringArgument(char *arg) {
  nonleaf();
  return arg;
}

EXPORT wchar_t* 
returnWStringMagic() {
  nonleaf();
  return MAGICWSTRING;
}

EXPORT wchar_t* 
returnWStringArgument(wchar_t *arg) {
  nonleaf();
  return arg;
}

typedef struct _TestStructure {
  double value;
} TestStructure;

EXPORT TestStructure*
returnStaticTestStructure() {
  static TestStructure test_structure;
  nonleaf();
  test_structure.value = MAGICDOUBLE;
  return &test_structure;
}

EXPORT void 
incrementInt8ByReference(int8 *arg) {
  nonleaf();
  if (arg) ++*arg;
}

EXPORT void 
incrementInt16ByReference(int16 *arg) {
  nonleaf();
  if (arg) ++*arg;
}

EXPORT void 
incrementInt32ByReference(int32 *arg) {
  nonleaf();
  if (arg) ++*arg;
}

EXPORT void 
incrementInt64ByReference(int64 *arg) {
  nonleaf();
  if (arg) ++*arg;
}

EXPORT void 
complementFloatByReference(float *arg) {
  nonleaf();
  if (arg) *arg = -*arg;
}

EXPORT void 
complementDoubleByReference(double *arg) {
  nonleaf();
  if (arg) *arg = -*arg;
}

EXPORT void 
setPointerByReferenceNull(void **arg) {
  nonleaf();
  if (arg) *arg = NULL;
}

EXPORT int64 
checkInt64ArgumentAlignment(int32 i, int64 j, int32 i2, int64 j2) {
  nonleaf();
  if (i != 0x10101010 || j != LONG(0x1111111111111111)
      || i2 != 0x01010101 || j2 != LONG(0x2222222222222222))
    return -1;

  return i + j + i2 + j2;
}

EXPORT double 
checkDoubleArgumentAlignment(float f, double d, float f2, double d2) {
  // float:  1=3f800000 2=40000000 3=40400000 4=40800000
  // double: 1=3ff00... 2=40000... 3=40080... 4=40100...
  nonleaf();
  if (f != 1 || d != 2 || f2 != 3 || d2 != 4)
    return -1;

  return NOP(f) + NOP(d) + NOP(f2) + NOP(d2);
}

// TODO: not yet supported
EXPORT int32 
testSimpleStructureArgument(struct CheckFieldAlignment arg) {
  nonleaf();
  if (arg.int32Field != (int32)1) {
    return -1;
  }
  if (arg.int64Field != (int64)2) {
    return -2;
  }
  if (arg.floatField != (float)3) {
    return -3;
  }
  if (arg.doubleField != (double)4) {
    return -4;
  }
  return sizeof(arg);
}

EXPORT int32 
testSimpleStructurePointerArgument(struct CheckFieldAlignment* arg) {
  return testSimpleStructureArgument(*arg);
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
callVoidCallback(void (*func)()) {
  (*func)();
}

EXPORT int32 
callInt32Callback(int32 (*func)(int32 arg, int32 arg2),
                  int32 arg, int32 arg2) {
  nonleaf();
  return (*func)(NOP(arg), NOP(arg2));
}

EXPORT int64 
callInt64Callback(int64 (*func)(int64 arg, int64 arg2),
                  int64 arg, int64 arg2) {
  nonleaf();
  return (*func)(NOP(arg), NOP(arg2));
}

EXPORT float 
callFloatCallback(float (*func)(float arg, float arg2),
                  float arg, float arg2) {
  nonleaf();
  return (*func)(NOP(arg), NOP(arg2));
}

EXPORT double 
callDoubleCallback(double (*func)(double arg, double arg2),
                   double arg, double arg2) {
  nonleaf();
  return (*func)(NOP(arg), NOP(arg2));
}

EXPORT int32 
fillInt8Buffer(char *buf, int len, char value) {
  int i;
  for (i=0;i < len;i++) {
    buf[i] = value;
  }
  return len;
}

EXPORT int32 
fillInt16Buffer(short *buf, int len, short value) {
  int i;
  for (i=0;i < len;i++) {
    buf[i] = value;
  }
  return len;
}

EXPORT int32 
fillInt32Buffer(int32 *buf, int len, int32 value) {
  int i;
  for (i=0;i < len;i++) {
    buf[i] = value;
  }
  return len;
}

EXPORT int32
fillInt64Buffer(int64 *buf, int len, int64 value) {
  int i;
  for (i=0;i < len;i++) {
    buf[i] = value;
  }
  return len;
}

#ifdef _WIN32
///////////////////////////////////////////////////////////////////////
// stdcall tests
// All stdcall functions need to include undecorated symbols
///////////////////////////////////////////////////////////////////////
EXPORT int32 __stdcall
returnInt32ArgumentStdCall(int32 arg) {
  nonleaf();
  return NOP(arg);
}

EXPORT int32 __stdcall
callInt32StdCallCallback(int32 (__stdcall *func)(int32 arg, int32 arg2),
                         int32 arg, int32 arg2) {
  nonleaf();
  return (*func)(NOP(arg), NOP(arg2));
}
#endif /* _WIN32 */

#ifdef __cplusplus
}
#endif
