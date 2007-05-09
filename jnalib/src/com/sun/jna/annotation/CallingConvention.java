package com.sun.jna.annotation;

/**
 * The calling convention for a native function
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
public enum CallingConvention
{
	/** The standard C calling convention */
	C,

	/** The STDALL calling convention, conventionally used in the Win32 API */
	STDCALL
}
