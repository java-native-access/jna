/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna;

/** Provides converters for conversion to and from native types. */
public interface TypeMapper {
    /** Return the {@link FromNativeConverter} appropriate for the given Java class. 
     * @param javaType Java class representation of the native type.
     * @return Converter from the native-compatible type.
     */
    FromNativeConverter getFromNativeConverter(Class javaType);

    /** Return the {@link ToNativeConverter} appropriate for the given Java class. 
     * @param javaType Java class representation of the native type.
     * @return Converter to the native-compatible type.
     */
    ToNativeConverter getToNativeConverter(Class javaType);
}
