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
public class NativeLong extends Number implements NativeMapped {
    /** Size of a native long, in bytes. */
    public static final int SIZE = Native.LONG_SIZE;
    private final Number value;

    /** Create a zero-valued NativeLong. */
    public NativeLong() {
        this(0);
    }
    
    /** Create a NativeLong with the given value. */
    public NativeLong(long value) {
        if (SIZE == 4) {
            long masked = value & 0xFFFFFFFF80000000L;
            if (masked != 0 && masked != 0xFFFFFFFF80000000L) {
                throw new IllegalArgumentException("Argument exceeds native long capacity");
            }
            this.value = new Integer((int) (value & 0xFFFFFFFF));
        } else {
            this.value = new Long(value);
        }
    }
    
    public Object toNative() {
        return value;
    }
    
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return new NativeLong(((Number)nativeValue).longValue());
    }
    
    public Class nativeType() {
        return SIZE == 4 ? Integer.class : Long.class;
    }
    
    public int intValue() {
        return value.intValue();
    }
    
    public long longValue() {
        return value.longValue();
    }
    
    public float floatValue() {
        return value.floatValue();
    }
    
    public double doubleValue() {
        return value.doubleValue();
    }
    public boolean equals(Object rhs) {
        return rhs instanceof NativeLong && value.equals(((NativeLong) rhs).value);
    }
    public String toString() {
        return value.toString();
    }
    public int hashCode() {
        return value.hashCode();
    }
}
