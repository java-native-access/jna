/* -----------------------------------------------------------------------
   eabi.S - Copyright (c) 2012  Anthony Green
   
   Moxie Assembly glue.

   Permission is hereby granted, free of charge, to any person obtaining
   a copy of this software and associated documentation files (the
   ``Software''), to deal in the Software without restriction, including
   without limitation the rights to use, copy, modify, merge, publish,
   distribute, sublicense, and/or sell copies of the Software, and to
   permit persons to whom the Software is furnished to do so, subject to
   the following conditions:

   The above copyright notice and this permission notice shall be included
   in all copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED ``AS IS'', WITHOUT WARRANTY OF ANY KIND, EXPRESS
   OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
   IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY CLAIM, DAMAGES OR
   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
   ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
   OTHER DEALINGS IN THE SOFTWARE.
   ----------------------------------------------------------------------- */

#define LIBFFI_ASM	
#include <fficonfig.h>
#include <ffi.h>

	.globl ffi_prep_args_EABI

	.text
	.p2align 4
	.globl ffi_call_EABI
	.type ffi_call_EABI, @function

	# $r0 :   ffi_prep_args
	# $r1 :   &ecif
	# $r2 :   cif->bytes
	# $r3 :   fig->flags
	# $r4 :   ecif.rvalue
	# $r5 :   fn
	
ffi_call_EABI:	
	dec	$sp, 24

	/* Store incoming args on stack.  */
	sto.l	0($sp), $r0
	sto.l	4($sp), $r1
	sto.l	8($sp), $r2
	sto.l	12($sp), $r3
	sto.l	16($sp), $r4
	sto.l	20($sp), $r5
	
	/* Call ffi_prep_args.  */
	jsr	$r0

	/* Call the target function.  */
	jsr	$r5

	/* Return.  */
	ret
	.size ffi_call_EABI, .-ffi_call_EABI
	
