/* Copyright (c) 2018 Sebastian Staudt, Matthias Bläsing, All Rights Reserved
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
import static junit.framework.TestCase.assertEquals;

public class NativedMappedTest extends TestCase {

    public void testDefaultValueForClass() {
        NativeMappedConverter converter = new NativeMappedConverter(NativeMappedTestClass.class);

        assertTrue(converter.defaultValue() instanceof NativeMappedTestClass);
    }

    public void testDefaultValueForEnum() {
        NativeMappedConverter converter = new NativeMappedConverter(TestEnum.class);

        assertSame(converter.defaultValue(), TestEnum.VALUE1);
    }

    public static interface EnumerationTestLibrary extends Library {
        TestEnum returnInt32Argument(TestEnum arg);

        @Structure.FieldOrder({"field"})
        class MinTestStructure extends Structure {
            public TestEnum field;
        }
        MinTestStructure testStructurePointerArgument(MinTestStructure s);
    }

    public void testEnumConversion() throws Exception {
        EnumerationTestLibrary lib = Native.load("testlib", EnumerationTestLibrary.class);
        assertEquals("Enumeration improperly converted", TestEnum.VALUE1, lib.returnInt32Argument(TestEnum.VALUE1));
        assertEquals("Enumeration improperly converted", TestEnum.VALUE2, lib.returnInt32Argument(TestEnum.VALUE2));
        EnumerationTestLibrary.MinTestStructure struct = new EnumerationTestLibrary.MinTestStructure();
        struct.field = TestEnum.VALUE1;
        assertEquals("Enumeration in structure improperly converted", TestEnum.VALUE1, lib.testStructurePointerArgument(struct).field);
        struct.field = TestEnum.VALUE2;
        assertEquals("Enumeration in structure improperly converted", TestEnum.VALUE2, lib.testStructurePointerArgument(struct).field);
    }

    public enum TestEnum implements NativeMapped {
        VALUE1, VALUE2;

        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            return values()[(Integer) nativeValue];
        }

        @Override
        public Object toNative() {
            return ordinal();
        }

        @Override
        public Class<?> nativeType() {
            return Integer.class;
        }
    }

    public static class NativeMappedTestClass implements NativeMapped {

        private String name;

        public NativeMappedTestClass() {
        }

        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            NativeMappedTestClass object = new NativeMappedTestClass();
            object.name = (String) nativeValue;

            return object;
        }

        @Override
        public Object toNative() {
            return name;
        }

        @Override
        public Class<?> nativeType() {
            return String.class;
        }
    }
}
