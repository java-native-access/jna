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

import java.lang.reflect.Constructor;
import org.junit.Test;

import com.sun.jna.IntegerType;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDLONG;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGLONG;
import com.sun.jna.platform.win32.WinDef.SHORT;
import com.sun.jna.platform.win32.WinDef.UCHAR;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGLONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinDef.WORD;

public class WinDefTypesTest extends AbstractWin32TestSupport {

    @Test
    public void testComparableDWORD() throws Exception {
        testComparableIntegerType(DWORD.class, 7365L, 3777347L);
    }

    @Test
    public void testComparableUINT() throws Exception {
        testComparableIntegerType(UINT.class, 7365L, 3777347L);
    }

    @Test
    public void testComparableULONG() throws Exception {
        testComparableIntegerType(ULONG.class, 7365L, 3777347L);
    }

    @Test
    public void testComparableULONGLONG() throws Exception {
        testComparableIntegerType(ULONGLONG.class, 7365L, 3777347L);
    }

    @Test
    public void testComparableDWORDLONG() throws Exception {
        testComparableIntegerType(DWORDLONG.class, 7365L, 3777347L);
    }

    @Test
    public void testComparableWORD() throws Exception {
        testComparableIntegerType(WORD.class, 5637L, 7365L);
    }

    @Test
    public void testComparableUSHORT() throws Exception {
        testComparableIntegerType(USHORT.class, 5637L, 7365L);
    }

    @Test
    public void testComparableSHORT() throws Exception {
        testComparableIntegerType(SHORT.class, 5637L, 7365L);
    }

    @Test
    public void testComparableLONG() throws Exception {
        testComparableIntegerType(LONG.class, 7365L, 3777347L);
    }

    @Test
    public void testComparableLONGLONG() throws Exception {
        testComparableIntegerType(LONGLONG.class, 7365L, 3777347L);
    }

    @Test
    public void testComparableCHAR() throws Exception {
        testComparableIntegerType(CHAR.class, 'a', 'z');
    }

    @Test
    public void testComparableUCHAR() throws Exception {
        testComparableIntegerType(UCHAR.class, 'a', 'z');
    }

    @Test
    public void testComparableBOOL() {
        assertEquals("Mismatched null/null comparison", 0, BOOL.compare(null, null));

        BOOL FALSE = new BOOL(false);
        BOOL TRUE = new BOOL(true);

        for (BOOL v : new BOOL[] { FALSE, TRUE }) {
            assertEquals("Mismatched null/" + v + " object comparison", 1, BOOL.compare(null, v));
            assertEquals("Mismatched null/" + v + " value comparison", 1, BOOL.compare(null, v.booleanValue()));
            assertEquals("Mismatched self/" + v + " comparison", 0, BOOL.compare(v, v.booleanValue()));
            assertEquals("Mismatched self " + v + "#compareTo() result", 0, v.compareTo(v));
            assertEquals("Mismatched null " + v + "#compareTo() result", (-1), v.compareTo(null));
        }

        assertEquals("Mismatched natural order comparison", (-1), FALSE.compareTo(TRUE));
        assertEquals("Mismatched reversed order comparison", 1, TRUE.compareTo(FALSE));
    }

    public static <T extends IntegerType & Comparable<T>> void testComparableIntegerType(Class<T> clazz, long v1, long v2) throws Exception {
        assertTrue(clazz.getSimpleName() + " test values not in natural order: v1=" + v1 + ", v2=" + v2, v1 < v2);

        Constructor<T> ctor = clazz.getDeclaredConstructor(Long.TYPE);
        testComparableIntegerType(clazz, ctor.newInstance(v1), ctor.newInstance(v2));
    }

    public static <T extends IntegerType & Comparable<T>> void testComparableIntegerType(Class<T> clazz, T v1, T v2) throws Exception {
        assertTrue(clazz.getSimpleName() + " test values not in natural order: v1=" + v1 + ", v2=" + v2, v1.longValue() < v2.longValue());

        assertEquals("Mismatched null/null comparison", 0, IntegerType.compare(null, null));
        assertEquals("Mismatched null/value comparison", 1, IntegerType.compare(null, v1));

        assertEquals("Mismatched null/long comparison", 1, IntegerType.compare(null, v2.longValue()));
        assertEquals("Mismatched natural order value/long comparison", -1, IntegerType.compare(v1, v2.longValue()));
        assertEquals("Mismatched reversed order value/long comparison", 1, IntegerType.compare(v2, v1.longValue()));
        assertEquals("Mismatched self value/long comparison", 0, IntegerType.compare(v1, v1.longValue()));

        assertEquals("Mismatched value/null comparison", (-1), v1.compareTo(null));
        assertEquals("Mismatched self value comparison", 0, v1.compareTo(v1));
        assertEquals("Mismatched natural order comparison", (-1), v1.compareTo(v2));
        assertEquals("Mismatched reversed order comparison", 1, v2.compareTo(v1));
    }
}
