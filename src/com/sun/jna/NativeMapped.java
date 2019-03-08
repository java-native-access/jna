/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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
    Class<?> nativeType();
}
