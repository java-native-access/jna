/* Area:	ffi_call
   Purpose:	Check return value double, with many arguments
   Limitations:	none.
   PR:		none.
   Originator:	From the original ffitest.c  */

/* { dg-do run } */
#include "ffitest.h"

#include <stdlib.h>
#include <float.h>
#include <math.h>

typedef struct
{
  char c1, c2;
} Ss;

static int many(
double a1,
float a2, 
Ss a3,
long double a4,
float a5,
short a6,
int a7,
float a8)
{
return a1 + a2 + a3.c1 + a3.c2 + a4 + a5 + a6 + a7 + a8;
}

int main (void)
{
  ffi_cif cif;
  ffi_type *args[9];
  ffi_type Ss_type;
  ffi_type *Ss_type_elements[4];

  double a1 = 1;
  float a2 = 2;
  Ss a3 = {3, 4};
  long double a4 = 5;
  float a5 = 6;
  short a6 = 7;
  int a7 = 8;
  float a8 = 9;

  ffi_arg r1;
  int r2;

  void *values[9];

  Ss_type.size = 0;
  Ss_type.alignment = 0;
  Ss_type.type = FFI_TYPE_STRUCT;
  Ss_type.elements = Ss_type_elements;
  Ss_type_elements[0] = &ffi_type_uchar;
  Ss_type_elements[1] = &ffi_type_uchar;
  Ss_type_elements[2] = NULL;

  args[0] = &ffi_type_double;
  args[1] = &ffi_type_float;
  args[2] = &Ss_type;
  args[3] = &ffi_type_longdouble;
  args[4] = &ffi_type_float;
  args[5] = &ffi_type_sshort;
  args[6] = &ffi_type_sint;
  args[7] = &ffi_type_float;
  args[8] = NULL;

  values[0] = &a1;
  values[1] = &a2;
  values[2] = &a3;
  values[3] = &a4;
  values[4] = &a5;
  values[5] = &a6;
  values[6] = &a7;
  values[7] = &a8;
  values[8] = NULL;

    /* Initialize the cif */
    CHECK(ffi_prep_cif(&cif, FFI_DEFAULT_ABI, 8,
		       &ffi_type_sint, args) == FFI_OK);

    ffi_call(&cif, FFI_FN(many), &r1, values);

    r2 =  many(a1,a2,a3,a4,a5,a6,a7,a8);
    if ((int)r1 == r2)
      exit(0);
    else
      abort();
}
