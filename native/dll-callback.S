/* Copyright (c) 2012 Timothy Wall, All Rights Reserved
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

/* Simple trampolines for MS compiler which can't do inline assembly */
EXTRN fn:NEAR	

_TEXT   SEGMENT

PUBLIC	asmfn0
asmfn0: 
 jmp fn[0]
	
PUBLIC	asmfn1
asmfn1: 
 jmp fn[1]

PUBLIC	asmfn2
asmfn2: 
 jmp fn[2]

PUBLIC	asmfn3
asmfn3: 
 jmp fn[3]

PUBLIC	asmfn4
asmfn4: 
 jmp fn[4]

PUBLIC	asmfn5
asmfn5: 
 jmp fn[5]

PUBLIC	asmfn6
asmfn6: 
 jmp fn[6]

PUBLIC	asmfn7
asmfn7: 
 jmp fn[7]

PUBLIC	asmfn8
asmfn8: 
 jmp fn[8]

PUBLIC	asmfn9
asmfn9: 
 jmp fn[9]

PUBLIC	asmfn10
asmfn10: 
 jmp fn[10]

PUBLIC	asmfn11
asmfn11: 
 jmp fn[11]

PUBLIC	asmfn12
asmfn12: 
 jmp fn[12]

PUBLIC	asmfn13
asmfn13: 
 jmp fn[13]

PUBLIC	asmfn14
asmfn14: 
 jmp fn[14]

PUBLIC	asmfn15
asmfn15: 
 jmp fn[15]

_TEXT   ENDS	
END
