package com.sun.jna.annotation;

import java.lang.annotation.*;
import com.sun.jna.*;

/**
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NativeFunction
{
	/**
	 * The name of the native function, if different from the method name
	 *
	 */
	public String name() default "";

	/**
	 *
	 *
	 */
	public CallingConvention convention() default CallingConvention.C;
}
