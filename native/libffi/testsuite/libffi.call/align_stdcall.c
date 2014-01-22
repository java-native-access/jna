/* Area:	ffi_call
   Purpose:	Check stdcall for argument alignment (always 4) on X86_WIN32 systems.
   Limitations:	none.
   PR:		none.
   Originator:	<twalljava@java.net> (from many_win32.c) */

/* { dg-do run { target i?86-*-cygwin* i?86-*-mingw* } } */

#include "ffitest.h"
#include <float.h>

static float __attribute__((stdcall)) stdcall_align(int i1,
                                                    double f2,
                                                    int i3,
                                                    double f4)
{
  return i1+f2+i3+f4;
}

int main (void)
{
  ffi_cif cif;
  ffi_type *args[4] = {&ffi_type_int, &ffi_type_double, &ffi_type_int, &ffi_type_double};
  float fa[2] = {1,2};
  int ia[2] = {1,2};
  void *values[4] = {&ia[0], &fa[0], &ia[1], &fa[1]};
  float f, ff;

  /* Initialize the cif */
  CHECK(ffi_prep_cif(&cif, FFI_STDCALL, 4,
		     &ffi_type_float, args) == FFI_OK);

  ff = stdcall_align(ia[0], fa[0], ia[1], fa[1]);

  ffi_call(&cif, FFI_FN(stdcall_align), &f, values);

  if (f - ff < FLT_EPSILON)
    printf("stdcall many arg tests ok!\n");
  else
    CHECK(0);
  exit(0);
}
