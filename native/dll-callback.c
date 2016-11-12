/* Copyright (c) 2007-2012 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
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
