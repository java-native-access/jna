/*
 * Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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
     * <li>{@link java.nio.Buffer} (unsupported in direct mode)
     * <li>primitive array (unsupported in direct mode)
     * </ul>
     */
    Object toNative(Object value, ToNativeContext context);
    /** Indicate the type expected from {@link #toNative}. */
    Class<?> nativeType();
}
