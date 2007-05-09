package com.sun.jna.annotation;

import java.lang.annotation.*;
import com.sun.jna.*;

/**
 *
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NativeLibrary
{
	/**
	 * The name of the library
	 *
	 */
	public String name();


	/**
	 * The calling convention to use when accessing the library
	 *
	 */
	public CallingConvention convention() default CallingConvention.C;
}
