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
package com.sun.jna;

import junit.framework.TestCase;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class ByReferenceArgumentsTest extends TestCase {

    public static interface TestLibrary extends Library {

        void incrementInt8ByReference(ByteByReference b);
        void incrementInt16ByReference(ShortByReference s);
        void incrementInt32ByReference(IntByReference i);
        void incrementNativeLongByReference(NativeLongByReference l);
        void incrementInt64ByReference(LongByReference l);
        void complementFloatByReference(FloatByReference f);
        void complementDoubleByReference(DoubleByReference d);
        void setPointerByReferenceNull(PointerByReference p);
    }

    TestLibrary lib;
    @Override
    protected void setUp() {
        lib = Native.load("testlib", TestLibrary.class);
    }

    @Override
    protected void tearDown() {
        lib = null;
    }

    public void testByteByReference() {
        ByteByReference bref = new ByteByReference();
        lib.incrementInt8ByReference(bref);
        assertEquals("Byte argument not modified", 1, bref.getValue());
    }
    public void testShortByReference() {
        ShortByReference sref = new ShortByReference();
        lib.incrementInt16ByReference(sref);
        assertEquals("Short argument not modified", 1, sref.getValue());
    }
    public void testIntByReference() {
        IntByReference iref = new IntByReference();
        lib.incrementInt32ByReference(iref);
        assertEquals("Int argument not modified", 1, iref.getValue());
    }
    public void testNativeLongByReference() {
        NativeLongByReference iref = new NativeLongByReference();
        lib.incrementNativeLongByReference(iref);
        assertEquals("Native long argument not modified",
                     new NativeLong(1), iref.getValue());
    }
    public void testLongByReference() {
        LongByReference lref = new LongByReference();
        lib.incrementInt64ByReference(lref);
        assertEquals("Long argument not modified", 1, lref.getValue());
    }
    public void testFloatByReference() {
        FloatByReference fref = new FloatByReference(1f);
        lib.complementFloatByReference(fref);
        assertEquals("Float argument not modified", -1f, fref.getValue(), 0.0);
    }
    public void testDoubleByReference() {
        DoubleByReference dref = new DoubleByReference(1d);
        lib.complementDoubleByReference(dref);
        assertEquals("Int argument not modified", -1d, dref.getValue(), 0.0);
    }
    public void testPointerByReference() {
        PointerByReference pref = new PointerByReference();
        assertNull("Default pointer should be null", pref.getValue());
        pref = new PointerByReference(new Memory(16));
        assertNotNull("Explicit pointer should not be null", pref.getValue());
        lib.setPointerByReferenceNull(pref);
        assertNull("Default pointer should be NULL after call", pref.getValue());
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ByReferenceArgumentsTest.class);
    }

}
