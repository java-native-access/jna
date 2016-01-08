/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

/** Provide conversion for a Java type to and from a native type.  
 * {@link Function} and {@link Structure} will use this interface to determine
 * how to map a given Java object into a native type.<p>
 * Implementations of this interface must provide a no-args constructor.</p>
 * <p>See {@link ToNativeConverter} for a list of allowable native types.</p>
 * @author wmeissner 
 */
public interface NativeMapped {
    /** Convert the given native object into its Java representation using
     * the given context. 
     * @param nativeValue Java representation of the native type to be converted.
     * @param context Context in which the conversion is taking place.
     * @return Converted object.
     */
    Object fromNative(Object nativeValue, FromNativeContext context);
    /** Convert this object into a supported native type.
     * @return Java representation of the original Java object converted to a native type.
     */
    Object toNative();
    /** Indicate the native type used by this converter.
     * @return Java class representation of the native type.
     */
    Class nativeType();
}
