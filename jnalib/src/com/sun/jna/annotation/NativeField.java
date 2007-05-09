package com.sun.jna.annotation;

import java.lang.annotation.*;
import com.sun.jna.*;

/**
 * Annotates a public field of a class as a field of the enclosing structure
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NativeField
{
//	/**
//	 * 
//	 *
//	 */
//	public int offset() default -1;


	/**
	 * 
	 *
	 */
	public int size() default -1;
}
