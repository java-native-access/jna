/* Copyright (c) 2020 Daniel Widdis, All Rights Reserved
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

import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

import junit.framework.TestCase;

public class ByReferenceToStringTest extends TestCase {

    public void testToStrings() {
        LongByReference lbr = new LongByReference(42L);
        parseAndTest(lbr.toString(), "long", "0x2a (42)");
        lbr = new LongByReference(-42L);
        parseAndTest(lbr.toString(), "long", "0xffffffffffffffd6 (-42)");

        IntByReference ibr = new IntByReference(42);
        parseAndTest(ibr.toString(), "int", "0x2a (42)");
        ibr = new IntByReference(-42);
        parseAndTest(ibr.toString(), "int", "0xffffffd6 (-42)");

        ShortByReference sbr = new ShortByReference((short) 42);
        parseAndTest(sbr.toString(), "short", "0x2a (42)");
        sbr = new ShortByReference((short) -42);
        parseAndTest(sbr.toString(), "short", "0xffd6 (-42)");

        ByteByReference bbr = new ByteByReference((byte) 42);
        parseAndTest(bbr.toString(), "byte", "0x2a (42)");
        bbr = new ByteByReference((byte) -42);
        parseAndTest(bbr.toString(), "byte", "0xd6 (-42)");

        FloatByReference fbr = new FloatByReference(42f);
        parseAndTest(fbr.toString(), "float", "42.0");
        fbr = new FloatByReference(-42f);
        parseAndTest(fbr.toString(), "float", "-42.0");

        DoubleByReference dbr = new DoubleByReference(42d);
        parseAndTest(dbr.toString(), "double", "42.0");
        dbr = new DoubleByReference(-42d);
        parseAndTest(dbr.toString(), "double", "-42.0");

        NativeLongByReference nlbr = new NativeLongByReference(new NativeLong(42L));
        parseAndTest(nlbr.toString(), "NativeLong", "0x2a (42)");
        nlbr = new NativeLongByReference(new NativeLong(-42L));
        parseAndTest(nlbr.toString(), "NativeLong",
                NativeLong.SIZE > 4 ? "0xffffffffffffffd6 (-42)" : "0xffffffd6 (-42)");

        PointerByReference pbr = new PointerByReference(Pointer.NULL);
        assertTrue(pbr.toString().startsWith("null@0x"));
        pbr = new PointerByReference(new Pointer(42));
        parseAndTest(pbr.toString(), "Pointer", "native");
    }

    /**
     * Parses a string "foo@0x123=bar" testing equality of fixed parts of the string
     *
     * @param s
     *            The string to test
     * @param beforeAt
     *            The string which should match the portion before the first
     *            {@code @}
     * @param afterEquals
     *            The string which should match the portion after the {@code =}
     *            sign, before any additional {@code @}
     */
    private void parseAndTest(String s, String beforeAt, String afterEquals) {
        String[] atSplit = s.split("@");
        assertEquals("Incorrect type prefix", beforeAt, atSplit[0]);
        String[] equalsSplit = atSplit[1].split("=");
        assertEquals("Incorrect value string", afterEquals, equalsSplit[1]);
    }
}
