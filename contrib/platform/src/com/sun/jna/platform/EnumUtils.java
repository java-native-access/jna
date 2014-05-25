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

import java.util.HashSet;
import java.util.Set;

import com.sun.jna.platform.win32.FlagEnum;

/**
 * Several helper methods to convert integer flag (sets)
 * into enum (sets)
 * @author Martin Steiger
 */
public class EnumUtils
{
	/**
	 * Uninitialized integer flag
	 */
	public static final int UNINITIALIZED = -1;
	
    /**
     * @param val the enum
     * @return the index of the enum in the enum list
     */
    public static <E extends Enum<E>> int toInteger(E val)
    {
        @SuppressWarnings("unchecked")
		E[] vals = (E[]) val.getClass().getEnumConstants();
        
    	for (int idx = 0; idx < vals.length; idx++)
    	{
    		if (vals[idx] == val)
    			return idx;
    	}
    	
    	throw new IllegalArgumentException();
    }
    
    /**
     * @param idx the enum index
     * @param clazz the enum class
     * @return the enum at position idx
     */
    public static <E extends Enum<E>> E fromInteger(int idx, Class<E> clazz)
    {
    	if (idx == UNINITIALIZED)
    		return null;
    	
    	E[] vals = clazz.getEnumConstants();
    	return vals[idx];
    }
    
    /**
     * @param flags the ORed flags
     * @param clazz the enum class
     * @return the representing set
     */
    public static <T extends FlagEnum> Set<T> setFromInteger(int flags, Class<T> clazz)
    {
        T[] vals = clazz.getEnumConstants();
        Set<T> result = new HashSet<T>();
        
        for (T val : vals)
        {
        	if ((flags & val.getFlag()) != 0)
        	{
        		result.add(val);
        	}
        }
        
        return result;
    }
    
	/**
	 * @param set the set to convert
	 * @return the flags combined into an integer
	 */
	public static <T extends FlagEnum> int setToInteger(Set<T> set) {
    	int sum = 0;
    	
    	for (T t : set)
    	{
    		sum |= t.getFlag();
    	}

    	return sum;
    }
}


