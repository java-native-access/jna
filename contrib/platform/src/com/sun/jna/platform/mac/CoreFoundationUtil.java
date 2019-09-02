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

import java.util.Collection;

import com.sun.jna.Memory;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberType;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
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
     * Convert a reference to a Core Foundations LongLong into its {@code long}
     *
     * @param theLong
     *            The pointer to a 64-bit integer
     * @return The corresponding {@code long}
     */
    public static long cfPointerToLong(CFTypeRef theLong) {
        LongByReference lbr = new LongByReference();
        CF.CFNumberGetValue(theLong, CFNumberType.kCFNumberLongLongType.ordinal(), lbr);
        return lbr.getValue();
    }

    /**
     * Convert a reference to a Core Foundations Int into its {@code int}
     *
     * @param p
     *            The pointer to an integer
     * @return The corresponding {@code int}
     */
    public static int cfPointerToInt(CFTypeRef p) {
        IntByReference ibr = new IntByReference();
        CF.CFNumberGetValue(p, CFNumberType.kCFNumberIntType.ordinal(), ibr);
        return ibr.getValue();
    }

    /**
     * Convert a reference to a Core Foundations Boolean into its {@code boolean}
     *
     * @param cfBoolean
     *            The pointer to a boolean
     * @return The corresponding {@code boolean}
     */
    public static boolean cfPointerToBoolean(CFTypeRef cfBoolean) {
        return CF.CFBooleanGetValue(cfBoolean);
    }

    /**
     * Convert a reference to a Core Foundations String into its
     * {@link java.lang.String}
     *
     * @param cfTypeRef
     *            The pointer to a CFString
     * @return The corresponding {@link java.lang.String}
     */
    public static String cfPointerToString(CFTypeRef cfTypeRef) {
        if (cfTypeRef == null) {
            return "null";
        }
        long length = CF.CFStringGetLength(cfTypeRef);
        long maxSize = CF.CFStringGetMaximumSizeForEncoding(length, kCFStringEncodingUTF8);
        if (maxSize == CoreFoundation.kCFNotFound) {
            maxSize = 4 * (length + 1);
        }
        Memory buf = new Memory(maxSize);
        CF.CFStringGetCString(cfTypeRef, buf, maxSize, kCFStringEncodingUTF8);
        return buf.getString(0);
    }

    /**
     * Releases a CF reference. If the retain count of {@code ref} becomes zero the
     * memory allocated to the object is deallocated and the object is destroyed. If
     * you create, copy, or explicitly retain (see the
     * {@link CoreFoundation#CFRetain} function) a Core Foundation object, you are
     * responsible for releasing it when you no longer need it.
     *
     * @param ref
     *            The reference to release
     */
    public static void release(CFTypeRef ref) {
        if (ref != null) {
            CF.CFRelease(ref);
        }
    }

    /**
     * Releases a collection of CF references. If the retain count of a reference
     * becomes zero the memory allocated to the object is deallocated and the object
     * is destroyed. If you create, copy, or explicitly retain (see the
     * {@link CoreFoundation#CFRetain} function) a Core Foundation object, you are
     * responsible for releasing it when you no longer need it.
     *
     * @param <T>
     *            A type extending {@link CFTypeRef}.
     * @param refs
     *            The collection of references to release
     */
    public static <T extends CFTypeRef> void releaseAll(Collection<T> refs) {
        for (CFTypeRef ref : refs) {
            release(ref);
        }
    }
}