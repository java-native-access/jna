/*
 * Copyright 2014 Martin Steiger
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

package com.sun.jna.platform;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

/**
 * A {@link TypeConverter} that maps an integer enum value to
 * an actual Java enum.
 * @param <T> the enum type
 * @author Martin Steiger
 */
public class EnumConverter<T extends Enum<T>> implements TypeConverter {
	 
    private final Class<T> clazz;
 
    /**
     * @param clazz the enum class
     */
    public EnumConverter(Class<T> clazz)
    {
    	this.clazz = clazz;
    }
    
    @Override
	public T fromNative(Object input, FromNativeContext context) {
        Integer i = (Integer) input;
        
        T[] vals = clazz.getEnumConstants();
        return vals[i];
    }
 
    @Override
	public Integer toNative(Object input, ToNativeContext context) {
    	T t = clazz.cast(input);
    	
        return Integer.valueOf(t.ordinal());
    }
 
    @Override
	public Class<Integer> nativeType() {
        return Integer.class;
    }
}


