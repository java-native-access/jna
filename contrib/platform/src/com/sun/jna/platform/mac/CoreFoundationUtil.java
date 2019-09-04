/*
 * Copyright (c) 2019 Daniel Widdis
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
package com.sun.jna.platform.mac;

import static com.sun.jna.platform.mac.CoreFoundation.kCFStringEncodingUTF8;

import com.sun.jna.Memory;
import com.sun.jna.platform.mac.CoreFoundation.CFBooleanRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberType;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/**
 * Provides utilities for Core Foundations
 */
public class CoreFoundationUtil {

    private static final CoreFoundation CF = CoreFoundation.INSTANCE;

    private CoreFoundationUtil() {
    }

    /**
     * Convert a reference to a Core Foundations LongLong into its {@code long}.
     * <p>
     * This method assumes a 64-bit number is stored and does not do type checking.
     * If the argument type differs from the return type, and the conversion is
     * lossy or the return value is out of range, then this function passes back an
     * approximate value.
     *
     * @param theLong
     *            The pointer to a 64-bit integer
     * @return The corresponding {@code long}
     */
    public static long cfPointerToLong(CFNumberRef theLong) {
        LongByReference lbr = new LongByReference();
        CF.CFNumberGetValue(theLong, CFNumberType.kCFNumberLongLongType.ordinal(), lbr);
        return lbr.getValue();
    }

    /**
     * Convert a reference to a Core Foundations Int into its {@code int}
     * <p>
     * This method assumes a 32-bit number is stored and does not do type checking.
     * If the argument type differs from the return type, and the conversion is
     * lossy or the return value is out of range, then this function passes back an
     * approximate value.
     *
     * @param theInt
     *            The pointer to an integer
     * @return The corresponding {@code int}
     */
    public static int cfPointerToInt(CFNumberRef theInt) {
        IntByReference ibr = new IntByReference();
        CF.CFNumberGetValue(theInt, CFNumberType.kCFNumberIntType.ordinal(), ibr);
        return ibr.getValue();
    }

    /**
     * Convert a reference to a Core Foundations Boolean into its {@code boolean}
     *
     * @param theBoolean
     *            The pointer to a boolean
     * @return The corresponding {@code boolean}
     */
    public static boolean cfPointerToBoolean(CFBooleanRef theBoolean) {
        return 0 != CF.CFBooleanGetValue(theBoolean);
    }

    /**
     * Convert a reference to a Core Foundations String into its
     * {@link java.lang.String}
     *
     * @param theString
     *            The pointer to a CFString
     * @return The corresponding {@link java.lang.String}
     */
    public static String cfPointerToString(CFStringRef theString) {
        if (theString == null) {
            return "null";
        }
        long length = CF.CFStringGetLength(theString);
        long maxSize = CF.CFStringGetMaximumSizeForEncoding(length, kCFStringEncodingUTF8);
        if (maxSize == CoreFoundation.kCFNotFound) {
            maxSize = 4 * (length + 1);
        }
        Memory buf = new Memory(maxSize);
        CF.CFStringGetCString(theString, buf, maxSize, kCFStringEncodingUTF8);
        return buf.getString(0, "UTF8");
    }
}