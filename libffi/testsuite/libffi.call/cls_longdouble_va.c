/* Area:		ffi_call, closure_call
   Purpose:		Test long doubles passed in variable argument lists.
   Limitations:	none.
   PR:			none.
   Originator:	Blake Chaffin 6/6/2007	 */

/* { dg-do run { xfail mips*-*-* arm*-*-* strongarm*-*-* xscale*-*-* } } */
#include "ffitest.h"

static void
cls_longdouble_va_fn(ffi_cif* cif, void* resp, void** args, void* userdata)
{
	char*		format	= *(char**)args[0];
	long double	ldValue	= *(long double*)args[1];

	*(ffi_arg*)resp = printf(format, ldValue);
}

int main (void)
{
	ffi_cif cif;
#ifndef USING_MMAP
	static ffi_closure cl;
#endif
	ffi_closure *pcl;
	void* args[3];
	ffi_type* arg_types[3];

#ifdef USING_MMAP
	pcl = allocate_mmap (sizeof(ffi_closure));
#else
	pcl = &cl;
#endif

	char*		format	= "%L.1f\n";
	long double	ldArg	= 7;
	ffi_arg		res		= 0;

	arg_types[0] = &ffi_type_pointer;
	arg_types[1] = &ffi_type_longdouble;
	arg_types[2] = NULL;

	CHECK(ffi_prep_cif(&cif, FFI_DEFAULT_ABI, 2, &ffi_type_sint,
		arg_types) == FFI_OK);

	args[0] = &format;
	args[1] = &ldArg;
	args[2] = NULL;

	ffi_call(&cif, FFI_FN(printf), &res, args);
	// { dg-output "7.0" }
	printf("res: %d\n", res);
	// { dg-output "\nres: 4" }

	CHECK(ffi_prep_closure(pcl, &cif, cls_longdouble_va_fn, NULL) == FFI_OK);

	res	= ((int(*)(char*, long double))(pcl))(format, ldArg);
	// { dg-output "\n7.0" }
	printf("res: %d\n", res);
	// { dg-output "\nres: 4" }

	exit(0);
}
