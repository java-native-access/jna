/* -*-c-*- */
#include "ffitest.h"
#include <complex.h>
#include <stdarg.h>

_Complex long double
return_c(signed int a1,
...
//_Complex long double a2,
//_Complex T_C_TYPE a4,
//signed int a5,
//_Complex long double a6
)
{
  va_list ap;
  _Complex long double a2;
  _Complex T_C_TYPE a4;
  signed int a5;
  _Complex long double a6;
  va_start (ap, a1);
  a2 = va_arg (ap, _Complex long double);
  a4 = va_arg (ap, _Complex T_C_TYPE);
  a5 = va_arg (ap, signed int);
  a6 = va_arg (ap, _Complex long double);

  volatile _Complex long double r = a1 + a2 + a4 + a5 + a6;
  return r;
}

int main (void)
{
  ffi_cif cif;
  ffi_type *args[MAX_ARGS];
  void *values[MAX_ARGS];

  _Complex long double r1, r2;

  signed int a1;
  _Complex long double a2;
  _Complex T_C_TYPE a4;
  signed int a5;
  _Complex long double a6;

  args[0] = &ffi_type_sint;
  args[1] = &ffi_type_complex_longdouble;
  args[2] = &T_FFI_TYPE;
  args[3] = &ffi_type_sint;
  args[4] = &ffi_type_complex_longdouble;
  values[0] = &a1;
  values[1] = &a2;
  values[2] = &a4;
  values[3] = &a5;
  values[4] = &a6;

  /* Initialize the cif */
//  CHECK(ffi_prep_cif(&cif, FFI_DEFAULT_ABI, 5,
//		     &ffi_type_complex_longdouble, args) == FFI_OK);
  CHECK(ffi_prep_cif_var (&cif, FFI_DEFAULT_ABI, 1, 5,
			  &ffi_type_complex_longdouble, args) == FFI_OK);

  a1 = 5;
  a2 = 127.0 + 255.0 * I;
  a4 = 512.7 + 1024.1 * I;
  a5 = 7;
  a6 = 89 + 12 * I;

  ffi_call(&cif, FFI_FN(return_c), &r1, values);
  r2 = return_c(a1, a2, a4, a5, a6);
  CHECK(r1 == r2);
  exit(0);
}
