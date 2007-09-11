/*
 * Copyright (c) 2007 Wayne Meissner, All Rights Reserved This library
 * is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. <p/> This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 */
package com.sun.jna;

/**
 * Define conversion from a Java type to its corresponding native type.
 */
public interface ToNativeConverter {
    /**
     * Convert a Java type to an appropriate native type. The new type
     * must be one of the following classes:
     * <ul>
     * <li>{@link Pointer}
     * <li>Boolean
     * <li>Byte
     * <li>Short
     * <li>Character
     * <li>Integer
     * <li>{@link NativeLong}
     * <li>Long
     * <li>Float
     * <li>Double
     * <li>{@link Structure}
     * <li>String
     * <li>{@link WString}
     * <li>{@link java.nio.Buffer}
     * <li>primitive array
     * </ul>
     */
    Object toNative(Object value, ToNativeContext context);
    /** Indicate the type expected from {@link #toNative}. */
    Class nativeType();
}
