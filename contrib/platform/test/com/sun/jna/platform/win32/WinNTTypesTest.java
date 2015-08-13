/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import org.junit.Test;

import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;

public class WinNTTypesTest extends AbstractWin32TestSupport {

    @Test
    public void testLargeIntegerLowHighLongValue() {
        for (long expected : new long[] {
                Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Byte.MIN_VALUE,
                0L,
                Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE, Byte.MAX_VALUE
            }) {
            assertEquals("Mismatched value", expected, new LARGE_INTEGER.LowHigh(expected).longValue());
        }
    }

    @Test
    public void testLargeIntegerUnionLongValue() {
        for (long expected : new long[] {
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
