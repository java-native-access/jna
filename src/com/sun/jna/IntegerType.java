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

/**
 * Represents a native integer value, which may have a platform-specific size
 * (e.g. <code>long</code> on unix-based platforms).
 * 
 * May optionally indicate an unsigned attribute, such that when a value is
 * extracted into a larger-sized container (e.g. <code>int</code> retrieved
 * via {@link Number#longValue}, the value will be unsigned.  Default behavior
 * is signed.
 * 
 * @author wmeissner@gmail.com
 * @author twalljava@java.net
 */
public abstract class IntegerType extends Number implements NativeMapped {

    private int size;
    private Number number;
    private boolean unsigned;
    // Used by native code
    private long value;

    /** Create a zero-valued signed IntegerType. */
    public IntegerType(int size) {
        this(size, 0, false);
    }

    /** Create a zero-valued optionally unsigned IntegerType. */
    public IntegerType(int size, boolean unsigned) {
        this(size, 0, unsigned);
    }

    /** Create a signed IntegerType with the given value. */
    public IntegerType(int size, long value) {
        this(size, value, false);
    }

    /** Create an optionally signed IntegerType with the given value. */
    public IntegerType(int size, long value, boolean unsigned) {
        this.size = size;
        this.unsigned = unsigned;
        setValue(value);
    }

    /** Change the value for this data. */
    public void setValue(long value) {
        long truncated = value;
        this.value = value;
        switch (size) {
        case 1:
            if (unsigned) this.value = value & 0xFFL;
            truncated = (byte) value;
            this.number = new Byte((byte) value);
            break;
        case 2:
            if (unsigned) this.value = value & 0xFFFFL;
            truncated = (short) value;
            this.number = new Short((short) value);
            break;
        case 4:
            if (unsigned) this.value = value & 0xFFFFFFFFL;
            truncated = (int) value;
            this.number = new Integer((int) value);
            break;
        case 8:
            this.number = new Long(value);
            break;
        default:
            throw new IllegalArgumentException("Unsupported size: " + size);
        }
        if (size < 8) {
            long mask = ~((1L << (size*8)) - 1);
            if ((value < 0 && truncated != value)
                || (value >= 0 && (mask & value) != 0)) {
                throw new IllegalArgumentException("Argument value 0x"
                        + Long.toHexString(value) + " exceeds native capacity ("
                        + size + " bytes) mask=0x" + Long.toHexString(mask));
            }
        }
    }

    public Object toNative() {
        return number;
    }

    public Object fromNative(Object nativeValue, FromNativeContext context) {
        // be forgiving of null values read from memory
        long value = nativeValue == null
            ? 0 : ((Number) nativeValue).longValue();
        try {
            IntegerType number = (IntegerType) getClass().newInstance();
            number.setValue(value);
            return number;
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Can't instantiate "
                    + getClass());
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Not allowed to instantiate "
                    + getClass());
        }
    }

    public Class nativeType() {
        return number.getClass();
    }

    public int intValue() {
        return (int)value;
    }

    public long longValue() {
        return value;
    }

    public float floatValue() {
        return number.floatValue();
    }

    public double doubleValue() {
        return number.doubleValue();
    }

    public boolean equals(Object rhs) {
        return rhs instanceof IntegerType
            && number.equals(((IntegerType)rhs).number);
    }

    public String toString() {
        return number.toString();
    }

    public int hashCode() {
        return number.hashCode();
    }
}
