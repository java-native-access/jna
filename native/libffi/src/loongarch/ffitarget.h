/* -----------------------------------------------------------------*-C-*-
   ffitarget.h

   Target configuration macros for LoongArch.

   Permission is hereby granted, free of charge, to any person obtaining
   a copy of this software and associated documentation files (the
   ``Software''), to deal in the Software without restriction, including
   without limitation the rights to use, copy, modify, merge, publish,
   distribute, sublicense, and/or sell copies of the Software, and to
   permit persons to whom the Software is furnished to do so, subject to
   the following conditions:

   The above copyright notice and this permission notice shall be included
   in all copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED ``AS IS'', WITHOUT WARRANTY OF ANY KIND,
   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
   NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
   HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
   WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
   DEALINGS IN THE SOFTWARE.

   ----------------------------------------------------------------------- */

#ifndef LIBFFI_TARGET_H
#define LIBFFI_TARGET_H

#ifndef LIBFFI_H
#error \
  "Please do not include ffitarget.h directly into your source.  Use ffi.h instead."
#endif

#ifndef __loongarch__
#error \
  "libffi was configured for a LoongArch target but this does not appear to be a LoongArch compiler."
#endif

#ifndef LIBFFI_ASM

typedef unsigned long ffi_arg;
typedef signed long ffi_sarg;

typedef enum ffi_abi
{
  FFI_FIRST_ABI = 0,
  FFI_LP64_SOFT_FLOAT,
  FFI_LP64_SINGLE_FLOAT,
  FFI_LP64,
  FFI_UNUSED_1,
  FFI_UNUSED_2,
  FFI_UNUSED_3,
  FFI_LAST_ABI,

#ifdef __loongarch64
# ifdef __loongarch_soft_float
  FFI_DEFAULT_ABI = FFI_LP64_SOFT_FLOAT
# elif __loongarch_single_float
  FFI_DEFAULT_ABI = FFI_LP64_SINGLE_FLOAT
# else
  FFI_DEFAULT_ABI = FFI_LP64
# endif
#endif
} ffi_abi;

#endif /* LIBFFI_ASM */

/* ---- Definitions for closures ----------------------------------------- */

#define FFI_CLOSURES 1
#define FFI_GO_CLOSURES 1
#define FFI_TRAMPOLINE_SIZE 24
#define FFI_NATIVE_RAW_API 0
#define FFI_EXTRA_CIF_FIELDS \
  unsigned loongarch_nfixedargs; \
  unsigned loongarch_unused;
#define FFI_TARGET_SPECIFIC_VARIADIC
#endif
