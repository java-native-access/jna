/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform.win32;

import org.junit.Test;

import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;

public class WinNTTypesTest extends AbstractWin32TestSupport {

    @Test
    public void testLargeIntegerLowHighLongValue() {
        for (long expected : new long[]{
            Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Byte.MIN_VALUE,
            0L,
            Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE, Byte.MAX_VALUE
        }) {
            assertEquals("Mismatched value", expected, new LARGE_INTEGER.LowHigh(expected).longValue());
        }
    }

    @Test
    public void testLargeIntegerUnionLongValue() {
        for (long expected : new long[]{
            Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Byte.MIN_VALUE,
            0L,
            Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE, Byte.MAX_VALUE
        }) {
            LARGE_INTEGER large = new LARGE_INTEGER(expected);
            assertEquals("Mismatched large value", expected, large.getValue());

            LARGE_INTEGER.LowHigh loHi = new LARGE_INTEGER.LowHigh(expected);
            assertEquals("Mismatched low part", loHi.LowPart, large.getLow());
            assertEquals("Mismatched high part", loHi.HighPart, large.getHigh());
        }
    }
}
