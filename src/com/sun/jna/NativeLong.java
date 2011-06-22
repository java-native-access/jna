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

/** Represents the <code>long</code> C data type, which may be 32 or 64 bits
 * on *nix-based systems.
 *
 * @author wmeissner@gmail.com
 */
public class NativeLong extends IntegerType {
    /** Size of a native long, in bytes. */
    public static final int SIZE = Native.LONG_SIZE;

    /** Create a zero-valued NativeLong. */
    public NativeLong() {
        this(0);
    }
    
    /** Create a NativeLong with the given value. */
    public NativeLong(long value) {
        super(SIZE, value);
    }
}
