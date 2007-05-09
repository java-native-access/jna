/*
 * @(#)dispatch_x86.c	1.3 98/03/22
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * See also the LICENSE file in this distribution.
 */

/*
 * Copies the arguments from the given array to C stack, invoke the
 * target function, and copy the result back.
 */
void asm_c_dispatch(void *func, int nwords, long *c_args, int ty, long *resP)
{
    __asm {

	mov esi, c_args
	mov edx, nwords
	// word address -> byte address
	shl edx, 2

    sub edx, 4
	jc  args_done

	// Push the last argument first.
    args_loop:
		mov eax, DWORD PTR [esi+edx]
		push eax
		sub edx, 4
		jge SHORT args_loop

    args_done:
		call func

	// cdecl specifies that the calling function cleans up the stack
	// pop arguments
	mov edx, nwords
	shl edx, 2
	add esp, edx

	mov esi, resP
	mov edx, ty
	dec edx
	jge not_p64

	// p64
	mov [esi], eax
	mov [esi+4], 0
	jmp done

    not_p64:
		dec edx
		jge not_i32

	// i32
	mov [esi], eax
	jmp done

    not_i32:
		dec edx
	    jge not_f32

    // f32
	fstp DWORD PTR [esi]
	jmp done

    not_f32:
	    // f64
		fstp QWORD PTR [esi]

    done:
    }
}


/*
 * Copies the arguments from the given array to stdcall stack, invoke the
 * target function, and copy the result back.
 */
void asm_stdcall_dispatch(void *func, int nwords, long *c_args, int ty, long *resP)
{
    __asm {

	mov esi, c_args
	mov edx, nwords
	// word address -> byte address
	shl edx, 2

    sub edx, 4
	jc  args_done

	// Push the last argument first.
    args_loop:
		mov eax, DWORD PTR [esi+edx]
		push eax
		sub edx, 4
		jge SHORT args_loop

    args_done:
		call func

	// stdcall specifies that the called function cleans up the stack;
//	// do NOT pop arguments
//	mov edx, nwords
//	shl edx, 2
//	add esp, edx

	mov esi, resP
	mov edx, ty
	dec edx
	jge not_p64

	// p64
	mov [esi], eax
	mov [esi+4], 0
	jmp done

    not_p64:
		dec edx
		jge not_i32

	// i32
	mov [esi], eax
	jmp done

    not_i32:
		dec edx
	    jge not_f32

    // f32
	fstp DWORD PTR [esi]
	jmp done

    not_f32:
	    // f64
		fstp QWORD PTR [esi]

    done:
    }
}
