/* Area:	closure_call
   Purpose:	Check closures called with many args of mixed types
   Limitations:	none.
   PR:		none.
   Originator:	<david.schneider@picle.org> */

/* { dg-do run } */
#include "ffitest.h"
#include <float.h>
#include <math.h>

#define NARGS 12

static void cls_ret_double_fn(ffi_cif* cif __UNUSED__, void* resp, void** args,
			      void* userdata __UNUSED__)
{
  int i;
  double r;
  double t;
  for(i = 0; i < NARGS; i++)
    {
    if(i == 8) 
      {
      t = *(long int *)args[i];
      CHECK(t == i+1);
      }
    else
      {
      t = *(double *)args[i];
      CHECK(fabs(t - ((i+1) * 0.1)) < FLT_EPSILON);
      }
    r += t;
    }
  *(double *)resp = r;
}
typedef double (*cls_ret_double)(double, double, double, double, double, double, double, double, long int, double, double, double);

int main (void)
{
  ffi_cif cif;
  void *code;
  ffi_closure *pcl = ffi_closure_alloc(sizeof(ffi_closure), &code);
  ffi_type * cl_arg_types[NARGS];
  double res;
  int i;
  double expected = 15.9;

  for(i = 0; i < NARGS; i++)
    {
    if(i == 8) 
      cl_arg_types[i] = &ffi_type_slong;
    else
      cl_arg_types[i] = &ffi_type_double;
    }

  /* Initialize the cif */
  CHECK(ffi_prep_cif(&cif, FFI_DEFAULT_ABI, NARGS,
		     &ffi_type_double, cl_arg_types) == FFI_OK);

  CHECK(ffi_prep_closure_loc(pcl, &cif, cls_ret_double_fn, NULL, code) == FFI_OK);

  res = (((cls_ret_double)code))(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 9,
                                                                1.0, 1.1, 1.2);
  if (abs(res - expected) < FLT_EPSILON)
    exit(0);
  else
    abort();
}
