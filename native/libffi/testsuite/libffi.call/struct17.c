/* Area:	ffi_call
   Purpose:	Check structures.
   Limitations:	none.
   PR:		none.
   Originator:	From the original ffitest.c  */

/* { dg-do run } */
#include "ffitest.h"

typedef struct
{
  long double d1;
} test_structure_2;

static test_structure_2 ABI_ATTR struct2(test_structure_2 ts)
{
  ts.d1--;

  return ts;
}

int main (void)
{
  ffi_cif cif;
  ffi_type *args[MAX_ARGS];
  void *values[MAX_ARGS];
  test_structure_2 ts2_arg;
  ffi_type ts2_type;
  ffi_type *ts2_type_elements[2];

  /* This is a hack to get a properly aligned result buffer */
  test_structure_2 *ts2_result =
    (test_structure_2 *) malloc (sizeof(test_structure_2));

  ts2_type.size = 0;
  ts2_type.alignment = 0;
  ts2_type.type = FFI_TYPE_STRUCT;
  ts2_type.elements = ts2_type_elements;
  ts2_type_elements[0] = &ffi_type_longdouble;
  ts2_type_elements[1] = NULL;

  args[0] = &ts2_type;
  values[0] = &ts2_arg;
  
  /* Initialize the cif */
  CHECK(ffi_prep_cif(&cif, ABI_NUM, 1, &ts2_type, args) == FFI_OK);
  
  ts2_arg.d1 = 5.55;
  
  printf ("%Lg\n", ts2_arg.d1);
  
  ffi_call(&cif, FFI_FN(struct2), ts2_result, values);
  
  printf ("%Lg\n", ts2_result->d1);
  
  CHECK(ts2_result->d1 == (long double) 5.55 - 1);
  
  free (ts2_result);
  exit(0);
}
