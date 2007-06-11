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
    /** Return the {@link ResultConverter} appropriate for the given Java class. 
     */
    ResultConverter getResultConverter(Class javaType);

    /** Return the {@link ArgumentConverter} appropriate for the given Java class. 
     */
    ArgumentConverter getArgumentConverter(Class javaType);
}
