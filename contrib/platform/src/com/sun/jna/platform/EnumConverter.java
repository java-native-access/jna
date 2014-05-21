/*
 * Copyright 2014 Martin Steiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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


