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
 * @author wmeissner@gmail.com
 */
public abstract class IntegerType extends Number implements NativeMapped {

    private int size;
    private long value;
    private Number number;

    /** Create a zero-valued IntegerType. */
    public IntegerType(int size) {
        this(size, 0);
    }

    /** Create a IntegerType with the given value. */
    public IntegerType(int size, long value) {
        this.size = size;
        setValue(value);
    }

    /** Change the value for this data. */
    public void setValue(long value) {
        long truncated = value;
        this.value = value;
        switch (size) {
        case 1:
            truncated = (byte) value;
            this.number = new Byte((byte) value);
            break;
        case 2:
            truncated = (short) value;
            this.number = new Short((short) value);
            break;
        case 4:
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
        return number.intValue();
    }

    public long longValue() {
        return number.longValue();
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
