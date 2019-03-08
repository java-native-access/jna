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

import java.util.List;

import junit.framework.TestCase;

/** General structure by value functionality tests. */
public class StructureByValueTest extends TestCase {

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(StructureByValueTest.class);
    }

    public static class TestNativeMappedInStructure extends Structure {
        public static class ByValue extends TestNativeMappedInStructure implements Structure.ByValue { }
        public static final List<String> FIELDS = createFieldsOrder("field");
        public NativeLong field;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public void testNativeMappedInByValue() {
        new TestNativeMappedInStructure.ByValue();
    }

    public interface TestLibrary extends Library {
        byte testStructureByValueArgument8(ByValue8 arg);
        short testStructureByValueArgument16(ByValue16 arg);
        int testStructureByValueArgument32(ByValue32 arg);
        long testStructureByValueArgument64(ByValue64 arg);
        long testStructureByValueArgument128(ByValue128 arg);
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

    public static abstract class ByValueStruct extends Structure implements Structure.ByValue { }
    public static class ByValue8 extends ByValueStruct {
        public static final List<String> FIELDS = createFieldsOrder("data");
        public byte data;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public static class ByValue16 extends ByValueStruct {
        public static final List<String> FIELDS = createFieldsOrder("data");
        public short data;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public static class ByValue32 extends ByValueStruct {
        public static final List<String> FIELDS = createFieldsOrder("data");
        public int data;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public static class ByValue64 extends ByValueStruct {
        public static final List<String> FIELDS = createFieldsOrder("data");
        public long data;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public static class ByValue128 extends ByValueStruct {
        public static final List<String> FIELDS = createFieldsOrder("data", "data1");
        public long data, data1;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    final long MAGIC = 0x0123456789ABCDEFL;
    public void testStructureArgByValue8() {
        ByValue8 data = new ByValue8();
        final byte DATA = (byte)MAGIC;
        data.data = DATA;
        assertEquals("Failed to pass 8-bit struct by value",
                     DATA, lib.testStructureByValueArgument8(data));
    }
    public void testStructureArgByValue16() {
        ByValue16 data = new ByValue16();
        final short DATA = (short)MAGIC;
        data.data = DATA;
        assertEquals("Failed to pass 16-bit struct by value",
                     DATA, lib.testStructureByValueArgument16(data));
    }
    public void testStructureArgByValue32() {
        ByValue32 data = new ByValue32();
        final int DATA = (int)MAGIC;
        data.data = DATA;
        assertEquals("Failed to pass 32-bit struct by value",
                     DATA, lib.testStructureByValueArgument32(data));
    }
    public void testStructureArgByValue64() {
        ByValue64 data = new ByValue64();
        final long DATA = MAGIC;
        data.data = DATA;
        assertEquals("Failed to pass 64-bit struct by value",
                     DATA, lib.testStructureByValueArgument64(data));
    }
    public void testStructureArgByValue128() {
        ByValue128 data = new ByValue128();
        final long DATA = MAGIC;
        data.data = DATA;
        data.data1 = DATA;
        assertEquals("Failed to pass 128-bit struct by value",
                     2*DATA, lib.testStructureByValueArgument128(data));
    }
}