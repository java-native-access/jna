/* Copyright (c) 2007-2012 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
/* Must use mingw64 to compile this assembly code.  ml64 can't generate the
   RIP-relative jumps we need.
*/
#define ASMFN(X) extern void asmfn ## X (); asm(".globl asmfn" #X "\n\
asmfn" #X ":\n\
 jmp *fn+8*" #X "(%rip)")

#ifdef DEFINE_CALLBACKS
extern void (*fn[])();
ASMFN(0);ASMFN(1);ASMFN(2);ASMFN(3);ASMFN(4);ASMFN(5);ASMFN(6);ASMFN(7);
ASMFN(8);ASMFN(9);ASMFN(10);ASMFN(11);ASMFN(12);ASMFN(13);ASMFN(14);ASMFN(15);
#endif /* DEFINE_CALLBACKS */
