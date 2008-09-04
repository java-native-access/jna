/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Map;

import junit.framework.TestCase;

/** General structure by value functionality tests. */
public class StructureByValueTest extends TestCase {

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(StructureByValueTest.class);
    }

    public static class TestNativeMappedInStructure extends Structure {
        public static class ByValue extends TestNativeMappedInStructure implements Structure.ByValue { }
        public NativeLong field;
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

    protected void setUp() {
        lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
    }

    protected void tearDown() {
        lib = null;
    }

    public static class ByValueStruct extends Structure implements Structure.ByValue { }
    public static class ByValue8 extends ByValueStruct {
        public byte data;
    }
    public static class ByValue16 extends ByValueStruct {
        public short data;
    }
    public static class ByValue32 extends ByValueStruct {
        public int data;
    }
    public static class ByValue64 extends ByValueStruct {
        public long data;
    }
    public static class ByValue128 extends ByValueStruct {
        public long data, data1;
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
        final long DATA = (long)MAGIC;
        data.data = DATA;
        assertEquals("Failed to pass 64-bit struct by value",
                     DATA, lib.testStructureByValueArgument64(data));
    }
    public void testStructureArgByValue128() {
        ByValue128 data = new ByValue128();
        final long DATA = (long)MAGIC;
        data.data = DATA;
        data.data1 = DATA;
        assertEquals("Failed to pass 128-bit struct by value",
                     2*DATA, lib.testStructureByValueArgument128(data));
    }
}