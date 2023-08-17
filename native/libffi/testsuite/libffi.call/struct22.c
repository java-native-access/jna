/* Area:	ffi_call
   Purpose:	Check structures.
   Limitations:	none.
   PR:		none.
   Originator:	From the original ffitest.c  */

/* { dg-do run } */
#include "ffitest.h"

struct Ls {
  double d;
  long double ld;
  unsigned char c1, c2;
  int i;
  unsigned char c3;
};

static struct Ls ABI_ATTR
struct1 (double a1, int a2, struct Ls a3, int a4, struct Ls a5)
{
  int res = a1 + a2 + a3.d + a3.ld + a3.c1 + a3.c2 + a3.i + a3.c3 + a4 + a5.d + a5.ld + a5.c1 + a5.c2 + a5.i + a5.c3;
  struct Ls ret = {res, res + 1, res + 2, res + 3, res + 4, res + 5};
  return ret;
}

int main (void)
{
  ffi_cif cif;
  ffi_type *args[MAX_ARGS];
  void *values[MAX_ARGS];
  ffi_type Ls_type;
  ffi_type *Ls_type_elements[7];

  double a1 = 1;
  int a2 = 2;
  struct Ls a3 = {3, 4, 5, 6, 7, 8};
  int a4 = 9;
  struct Ls a5 = {10, 11, 12, 13, 14, 15};

  struct Ls r1, r2;

  Ls_type.size = 0;
  Ls_type.alignment = 0;
  Ls_type.type = FFI_TYPE_STRUCT;
  Ls_type.elements = Ls_type_elements;
  Ls_type_elements[0] = &ffi_type_double;
  Ls_type_elements[1] = &ffi_type_longdouble;
  Ls_type_elements[2] = &ffi_type_uchar;
  Ls_type_elements[3] = &ffi_type_uchar;
  Ls_type_elements[4] = &ffi_type_sint;
  Ls_type_elements[5] = &ffi_type_uchar;
  Ls_type_elements[6] = NULL;
  
  args[0] = &ffi_type_double;
  args[1] = &ffi_type_sint;
  args[2] = &Ls_type;
  args[3] = &ffi_type_sint;
  args[4] = &Ls_type;
  values[0] = &a1;
  values[1] = &a2;
  values[2] = &a3;
  values[3] = &a4;
  values[4] = &a5;

  r1 = struct1 (a1, a2, a3, a4, a5);

  /* Initialize the cif */
  CHECK(ffi_prep_cif(&cif, ABI_NUM, 5,
		     &Ls_type, args) == FFI_OK);
  
  ffi_call(&cif, FFI_FN(struct1), &r2, values);

  CHECK(r1.d == r2.d);
  CHECK(r1.ld == r2.ld);
  CHECK(r1.c1 == r2.c1);
  CHECK(r1.c2 == r2.c2);
  CHECK(r1.i == r2.i);
  CHECK(r1.c3 == r2.c3);

  exit(0);
}
