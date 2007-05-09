package com.sun.jna.annotation;

import java.lang.annotation.*;

/**
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface NativeStructure
{
	public int size() default -1;
	
	public boolean autoSync() default true;
}
