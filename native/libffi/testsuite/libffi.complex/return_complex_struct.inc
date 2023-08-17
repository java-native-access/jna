/* -*-c-*- */
#include "ffitest.h"
#include <complex.h>

typedef struct Cs {
  _Complex T_C_TYPE x;
} Cs;

static Cs return_c(_Complex T_C_TYPE c1, float fl2, unsigned int in3, _Complex T_C_TYPE c4)
{
  Cs r = {c1 + fl2 + in3 + c4};
  return r;
}
int main (void)
{
  ffi_cif cif;
  ffi_type *args[MAX_ARGS];
  void *values[MAX_ARGS];

  Cs rc, rc2;
  _Complex T_C_TYPE c1, c4;
  float fl2;
  unsigned int in3;
  args[0] = &T_FFI_TYPE;
  args[1] = &ffi_type_float;
  args[2] = &ffi_type_uint;
  args[3] = &T_FFI_TYPE;
  values[0] = &c1;
  values[1] = &fl2;
  values[2] = &in3;
  values[3] = &c4;

  ffi_type Cs_type;
  ffi_type* Cs_type_elements[4];
  Cs_type.size = 0;
  Cs_type.alignment = 0;
  Cs_type.type = FFI_TYPE_STRUCT;
  Cs_type.elements = Cs_type_elements;

  Cs_type_elements[0] = &T_FFI_TYPE;
  Cs_type_elements[1] = NULL;

  /* Initialize the cif */
  CHECK(ffi_prep_cif(&cif, FFI_DEFAULT_ABI, 4,
		     &Cs_type, args) == FFI_OK);
  c1 = 127.0 + 255.0 * I;
  fl2 = 128.0;
  in3 = 255;
  c4 = 512.7 + 1024.1 * I;

  ffi_call(&cif, FFI_FN(return_c), &rc, values);
  rc2 = return_c(c1, fl2, in3, c4);
  printf ("%f,%fi vs %f,%fi\n",
	  T_CONV creal (rc.x), T_CONV cimag (rc.x),
	  T_CONV creal (rc2.x), T_CONV cimag (rc2.x));
  CHECK(rc.x == rc2.x);
  exit(0);
}
