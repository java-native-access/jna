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

import java.lang.reflect.InvocationTargetException;

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
    private static final long serialVersionUID = 1L;

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

    /** Change the value for this data.
     * @param value value to set
     */
    public void setValue(long value) {
        long truncated = value;
        this.value = value;
        switch (size) {
            case 1:
                if (unsigned) {
                    this.value = value & 0xFFL;
                }
                truncated = (byte) value;
                this.number = Byte.valueOf((byte) value);
                break;
            case 2:
                if (unsigned) {
                    this.value = value & 0xFFFFL;
                }
                truncated = (short) value;
                this.number = Short.valueOf((short) value);
                break;
            case 4:
                if (unsigned) {
                    this.value = value & 0xFFFFFFFFL;
                }
                truncated = (int) value;
                this.number = Integer.valueOf((int) value);
                break;
            case 8:
                this.number = Long.valueOf(value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported size: " + size);
        }
        if (size < 8) {
            long mask = ~((1L << (size * 8)) - 1);
            if ((value < 0 && truncated != value)
                    || (value >= 0 && (mask & value) != 0)) {
                throw new IllegalArgumentException("Argument value 0x"
                        + Long.toHexString(value) + " exceeds native capacity ("
                        + size + " bytes) mask=0x" + Long.toHexString(mask));
            }
        }
    }

    @Override
    public Object toNative() {
        return number;
    }

    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        // be forgiving of null values read from memory
        long value = nativeValue == null
            ? 0 : ((Number) nativeValue).longValue();
        IntegerType number = Klass.newInstance(getClass());
        number.setValue(value);
        return number;
    }

    @Override
    public Class<?> nativeType() {
        return number.getClass();
    }

    @Override
    public int intValue() {
        return (int)value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return number.floatValue();
    }

    @Override
    public double doubleValue() {
        return number.doubleValue();
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof IntegerType
            && number.equals(((IntegerType)rhs).number);
    }

    @Override
    public String toString() {
        return number.toString();
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    /**
     * Compares 2 derived {@link IntegerType} values - <B>Note:</B> a
     * {@code null} value is considered <U>greater</U> than any non-{@code null}
     * one (i.e., {@code null} values are &quot;pushed&quot; to the end
     * of a sorted array / list of values)
     *
     * @param <T> the derived integer type
     * @param v1 The 1st value
     * @param v2 The 2nd value
     * @return 0 if values are equal - including if <U>both</U> are {@code null},
     * negative if 1st value less than 2nd one, positive otherwise. <B>Note:</B>
     * the comparison uses the {@link #longValue()}.
     * @see #compare(long, long)
     */
    public static <T extends IntegerType> int compare(T v1, T v2) {
        if (v1 == v2) {
            return 0;
        } else if (v1 == null) {
            return 1;   // v2 cannot be null or v1 == v2 would hold
        } else if (v2 == null) {
            return (-1);
        } else {
            return compare(v1.longValue(), v2.longValue());
        }
    }

    /**
     * Compares a IntegerType value with a {@code long} one. <B>Note:</B> if
     * the IntegerType value is {@code null} then it is consider <U>greater</U>
     * than any {@code long} value.
     *
     * @param v1 The {@link IntegerType} value
     * @param v2 The {@code long} value
     * @return 0 if values are equal, negative if 1st value less than 2nd one,
     * positive otherwise. <B>Note:</B> the comparison uses the {@link #longValue()}.
     * @see #compare(long, long)
     */
    public static int compare(IntegerType v1, long v2) {
        if (v1 == null) {
            return 1;
        } else {
            return compare(v1.longValue(), v2);
        }
    }

    // TODO if JDK 7 becomes the min. required use Long#compare(long,long)
    public static final int compare(long v1, long v2) {
        if (v1 == v2) {
            return 0;
        } else if (v1 < v2) {
            return (-1);
        } else {
            return 1;
        }
    }
}
