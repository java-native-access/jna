/* Copyright (c) 2018 Matthias Bläsing, All Rights Reserved
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

/**
 * Test native calls where the parameters and return types are bound as wrappers
 */
public class ArgumentsWrappersMarshalTest extends TestCase {

    public static interface TestLibrary extends Library {
        Boolean returnBooleanArgument(Boolean arg);
        Byte returnInt8Argument(Byte arg);
        Character returnWideCharArgument(Character arg);
        Short returnInt16Argument(Short arg);
        Integer returnInt32Argument(Integer i);
        Long returnInt64Argument(Long l);
        Float returnFloatArgument(Float f);
        Double returnDoubleArgument(Double d);
        Long checkInt64ArgumentAlignment(Integer i, Long j, Integer i2, Long j2);
        Double checkDoubleArgumentAlignment(Float i, Double j, Float i2, Double j2);
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

    public void testBooleanArgument() {
        assertTrue("True argument should be returned",
                   lib.returnBooleanArgument(true));
        assertFalse("False argument should be returned",
                    lib.returnBooleanArgument(false));
    }

    public void testInt8Argument() {
        Byte b = 0;
        assertEquals("Wrong value returned",
                     b, lib.returnInt8Argument(b));
        b = 127;
        assertEquals("Wrong value returned",
                     b, lib.returnInt8Argument(b));
        b = -128;
        assertEquals("Wrong value returned",
                     b, lib.returnInt8Argument(b));
    }

    public void testWideCharArgument() {
        Character c = 0;
        assertEquals("Wrong value returned",
                     c, lib.returnWideCharArgument(c));
        c = 0xFFFF;
        assertEquals("Wrong value returned",
                     c, lib.returnWideCharArgument(c));
        c = 0x7FFF;
        assertEquals("Wrong value returned",
                     c, lib.returnWideCharArgument(c));
    }

    public void testInt16Argument() {
        Short v = 0;
        assertEquals("Wrong value returned",
                     v, lib.returnInt16Argument(v));
        v = 32767;
        assertEquals("Wrong value returned",
                     v, lib.returnInt16Argument(v));
        v = -32768;
        assertEquals("Wrong value returned",
                     v, lib.returnInt16Argument(v));
    }

    public void testIntArgument() {
        Integer value = 0;
        assertEquals("Should return 32-bit argument",
                     value, lib.returnInt32Argument(value));
        value = 1;
        assertEquals("Should return 32-bit argument",
                     value, lib.returnInt32Argument(value));
        value = 0x7FFFFFFF;
        assertEquals("Should return 32-bit argument",
                     value, lib.returnInt32Argument(value));
        value = 0x80000000;
        assertEquals("Should return 32-bit argument",
                     value, lib.returnInt32Argument(value));
    }

    public void testLongArgument() {
        Long value = 0L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 1L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 0x7FFFFFFFL;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 0x80000000L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 0x7FFFFFFF00000000L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 0x8000000000000000L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
    }

    public void testFloatArgument() {
        Float value = 0f;
        assertEquals("Should return float argument",
                     value, lib.returnFloatArgument(value));
        value = 4223.4223f;
        assertEquals("Should return float argument",
                     value, lib.returnFloatArgument(value));
        value = -4223.4223f;
        assertEquals("Should return float argument",
                     value, lib.returnFloatArgument(value));
    }

    public void testDoubleArgument() {
        Double value = 0d;
        assertEquals("Should return double argument",
                     value, lib.returnDoubleArgument(value));
        value = 4223.4223d;
        assertEquals("Should return double argument",
                     value, lib.returnDoubleArgument(value));
        value = -4223.4223d;
        assertEquals("Should return double argument",
                     value, lib.returnDoubleArgument(value));
    }

    public void testInt64ArgumentAlignment() {
        long value = lib.checkInt64ArgumentAlignment(0x10101010, 0x1111111111111111L,
                                                     0x01010101, 0x2222222222222222L);
        assertEquals("Improper handling of interspersed int32/int64",
                     0x3333333344444444L, value);
    }

    public void testDoubleArgumentAlignment() {
        Double value = lib.checkDoubleArgumentAlignment(1f, 2d, 3f, 4d);
        assertEquals("Improper handling of interspersed float/double",
                     10d, value, 0);
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ArgumentsWrappersMarshalTest.class);
    }

}
