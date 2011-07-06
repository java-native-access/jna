/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */

package com.sun.jna;

/** Define conversion from a native type to the appropriate Java type. */
public interface FromNativeConverter {
    /** Convert the given native object into its Java representation using
     * the given context. 
     */
    Object fromNative(Object nativeValue, FromNativeContext context);
    /** Indicate the native type used by this converter. */
    Class nativeType();
}
