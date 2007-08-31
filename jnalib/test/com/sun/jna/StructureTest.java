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

import java.util.Map;

import junit.framework.TestCase;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/** TODO: need more alignment tests, especially platform-specific behavior
 * @author twall@users.sf.net
 */
public class StructureTest extends TestCase {
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(StructureTest.class);
    }

    public void testSimpleSize() throws Exception {
        class TestStructure extends Structure {
            public int field;
        }
        Structure s = new TestStructure();
        assertEquals("Wrong size", 4, s.size());
    }
    
    public static class TestAllocStructure extends Structure {
        public int f0;
        public int f1;
        public int f2;
        public int f3;
    }
    public void testClearOnAllocate() {
        TestAllocStructure s = new TestAllocStructure();
        s.read();
        assertEquals("Memory not cleared on structure init", 0, s.f0);
        assertEquals("Memory not cleared on structure init", 0, s.f1);
        assertEquals("Memory not cleared on structure init", 0, s.f2);
        assertEquals("Memory not cleared on structure init", 0, s.f3);
        
        s = (TestAllocStructure)s.toArray(2)[1];
        assertEquals("Memory not cleared on array init", 0, s.f0);
        assertEquals("Memory not cleared on array init", 0, s.f1);
        assertEquals("Memory not cleared on array init", 0, s.f2);
        assertEquals("Memory not cleared on array init", 0, s.f3);
    }
    
    // cross-platform smoke test
    public void testGNUCAlignment() {
        class TestStructure extends Structure { 
            public byte b;
            public short s;
            public int i;
            public long l;
            public float f;
            public double d;
        }
        TestStructure s = new TestStructure();
        s.setAlignType(Structure.ALIGN_GNUC);
        boolean isSPARC = "sparc".equals(System.getProperty("os.arch"));
        final int SIZE = NativeLong.SIZE == 4 && !isSPARC ? 28 : 32;
        assertEquals("Wrong structure size", SIZE, s.size());
    }
    
    // cross-platform smoke test
    public void testMSVCAlignment() {
        class TestStructure extends Structure { 
            public byte b;
            public short s;
            public int i;
            public long l;
            public float f;
            public double d;
        }
        TestStructure s = new TestStructure();
        s.setAlignType(Structure.ALIGN_MSVC);
        assertEquals("Wrong structure size", 32, s.size());
    }
    
    public static class FilledStructure extends Structure {
        public FilledStructure() {
            for (int i=0;i < size();i++) {
                getPointer().setByte(i, (byte)0xFF);
            }
        }
    }
    // Do NOT change the order of naming w/o changing testlib.c
    public static class TestStructure0 extends FilledStructure {
        public byte field0 = 0x01;
        public short field1 = 0x0202;
    }
    public static class TestStructure1 extends FilledStructure {
        public byte field0 = 0x01;
        public int field1 = 0x02020202;
    }
    public static class TestStructure2 extends FilledStructure {
        public short field0 = 0x0101;
        public int field1 = 0x02020202;
    }
    public static class TestStructure3 extends FilledStructure {
        public int field0 = 0x01010101;
        public short field1 = 0x0202;
        public int field2 = 0x03030303;
    }
    public static class TestStructure4 extends FilledStructure {
        public int field0 = 0x01010101;
        public long field1 = 0x0202020202020202L;
        public int field2 = 0x03030303;
        public long field3 = 0x0404040404040404L;
    }
    public static class TestStructure5 extends FilledStructure {
        public long field0 = 0x0101010101010101L;
        public byte field1 = 0x02;
    }
    public interface SizeTest extends Library {
        int getStructureSize(int type);
    }
    private void testStructureSize(int index) {
        try {
            SizeTest lib = (SizeTest)Native.loadLibrary("testlib", SizeTest.class);
            Class cls = Class.forName(getClass().getName() + "$TestStructure" + index);
            Structure s = (Structure)cls.newInstance();
            assertEquals("Incorrect size: " + s, lib.getStructureSize(index), s.size());
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }
    public void testStructureSize0() {
        testStructureSize(0);
    }
    public void testStructureSize1() {
        testStructureSize(1);
    }
    public void testStructureSize2() {
        testStructureSize(2);
    }
    public void testStructureSize3() {
        testStructureSize(3);
    }
    public void testStructureSize4() {
        testStructureSize(4);
    }
    public void testStructureSize5() {
        testStructureSize(5);
    }
    
    public interface AlignmentTest extends Library {
        int testStructureAlignment(Structure s, int type, 
                                   IntByReference offsetp, LongByReference valuep);
    }
    
    private void testAlignStruct(int index) {
        AlignmentTest lib = (AlignmentTest)Native.loadLibrary("testlib", AlignmentTest.class);
        try {
            IntByReference offset = new IntByReference();
            LongByReference value = new LongByReference();
            Class cls = Class.forName(getClass().getName() + "$TestStructure" + index);
            Structure s = (Structure)cls.newInstance();
            int result = lib.testStructureAlignment(s, index, offset, value);
            assertEquals("Wrong native value at field " + result 
                         + "=0x" + Long.toHexString(value.getValue()) 
                         + " (actual native field offset=" + offset.getValue() 
                         + ") in " + s, -2, result);
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }
    public void testAlignStruct0() {
        testAlignStruct(0);
    }
    public void testAlignStruct1() {
        testAlignStruct(1);
    }
    public void testAlignStruct2() {
        testAlignStruct(2);
    }
    public void testAlignStruct3() {
        testAlignStruct(3);
    }
    public void testAlignStruct4() {
        testAlignStruct(4);
    }
    public void testAlignStruct5() {
        testAlignStruct(5);
    }
    
    public static class InnerStructure extends Structure {
        public int x, y;
    }
    public void testSizeWithNestedStructure() {
        class TestStructure extends Structure {
            public InnerStructure s1, s2;
            public int after;
        }
        TestStructure s = new TestStructure();
        assertNotNull("Inner structure should be initialized", s.s1);
        assertEquals("Wrong aggregate size", 
                     s.s1.size() + s.s2.size() + 4, s.size());
    }
    
    public void testNestArray() throws Exception {
        class TestStructure extends Structure {
            public byte[] buffer = new byte[1024];
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong size for nested array", 1024, s.size());
        assertNotNull("Array should be initialized", s.buffer);
    }

    public void testReadWriteStructure() {
        class TestStructure extends Structure {
            public InnerStructure s1;
            public byte[] buffer = new byte[8];
        }
        TestStructure s = new TestStructure();
        s.s1.x = 1;
        s.s1.y = 2;
        s.buffer[0] = 3;
        s.write();
        s.s1.x = s.s1.y = 0;
        s.buffer[0] = 0;
        s.read();
        assertEquals("Wrong nested struct field value after write/read (x)",
                     1, s.s1.x);
        assertEquals("Wrong nested struct field value after write/read (y)",
                     2, s.s1.y);
        assertEquals("Wrong nested array element value after write/read",
                     3, s.buffer[0]);
    }
    
    public void testNativeLongSize() throws Exception {
        class TestStructure extends Structure {
            public NativeLong l;
        }
        Structure s = new TestStructure();
        assertEquals("Wrong size", NativeLong.SIZE, s.size());
    }
    
    public void testNativeLongRead() throws Exception {
        class TestStructure extends Structure {
            public int i;
            public NativeLong l;
        }
        TestStructure s = new TestStructure();
        if (NativeLong.SIZE == 8) {
            final long MAGIC = 0x1234567887654321L;
            s.getPointer().setLong(8, MAGIC);
            s.read();
            assertEquals("NativeLong field mismatch", MAGIC, s.l.longValue());
        } 
        else {
            final int MAGIC = 0xABEDCF23;
            s.getPointer().setInt(4, MAGIC);
            s.read();
            assertEquals("NativeLong field mismatch", MAGIC, s.l.intValue());
        }
    }
    
    public void testNativeLongWrite() throws Exception {
        class TestStructure extends Structure {
            public int i;
            public NativeLong l;
        }
        TestStructure s = new TestStructure();
        if (NativeLong.SIZE == 8) {
            final long MAGIC = 0x1234567887654321L;
            s.l = new NativeLong(MAGIC);
            s.write();
            long l = s.getPointer().getLong(8);
            assertEquals("NativeLong field mismatch", MAGIC, l);
        } 
        else {
            final int MAGIC = 0xABEDCF23;
            s.l = new NativeLong(MAGIC);
            s.write();
            int i = s.getPointer().getInt(4);
            assertEquals("NativeLong field mismatch", MAGIC, i);
        }
    }
    
    static class BadFieldStructure extends Structure {
        public Function cb;
    }
    public void testDisallowFunctionPointerAsField() {
        try {
            BadFieldStructure s = new BadFieldStructure();
            fail("Function fields should not be allowed");
        }
        catch(IllegalArgumentException e) {
        }
    }

    public static class TestStructure extends Structure {
        public int value;
    }
    public void testToArray() {
        TestStructure s = new TestStructure();
        TestStructure[] array = (TestStructure[])s.toArray(new TestStructure[1]);
        assertEquals("Array should consist of a single element",
                     1, array.length);
        assertEquals("First element should be original", s, array[0]);
        assertEquals("Structure memory should be expanded", 2, s.toArray(2).length);
    }
    
    static class CbStruct extends Structure {
        public Callback cb;
    }
    static class CbStruct2 extends Structure {
        public static interface TestCallback extends Callback {
            int callback(int arg1, int arg2);
        }
        public TestCallback cb;
    }
    static interface CbTest extends Library {
        public void callCallbackInStruct(CbStruct cbstruct);
        public void setCallbackInStruct(CbStruct2 cbstruct);
    }
    public void testCallbackWrite() {
        final CbStruct s = new CbStruct();
        s.cb = new Callback() {
            public void callback() {
            }
        };
        s.write();
        Pointer func = s.getPointer().getPointer(0);
        assertNotNull("Callback trampoline not set", func);
        Map refs = CallbackReference.callbackMap;
        assertTrue("Callback not cached", refs.containsKey(s.cb));
        CallbackReference ref = (CallbackReference)refs.get(s.cb);
        assertEquals("Wrong trampoline", ref.getTrampoline(), func);
    }
    
    public void testCallCallbackInStructure() {
        final boolean[] flag = {false};
        final CbStruct s = new CbStruct();
        s.cb = new Callback() {
            public void callback() {
                flag[0] = true;
            }
        };
        CbTest lib = (CbTest)Native.loadLibrary("testlib", CbTest.class);
        lib.callCallbackInStruct(s);
        assertTrue("Callback not invoked", flag[0]);
    }
    
    public void testReadFunctionPointerAsCallback() {
        CbStruct2 s = new CbStruct2();
        CbTest lib = (CbTest)Native.loadLibrary("testlib", CbTest.class);
        lib.setCallbackInStruct(s);
        assertNotNull("Callback field not set", s.cb);
    }
    
    public void testCallProxiedFunctionPointer() {
        CbStruct2 s = new CbStruct2();
        CbTest lib = (CbTest)Native.loadLibrary("testlib", CbTest.class);
        lib.setCallbackInStruct(s);
        assertEquals("Proxy to native function pointer failed", 
                     3, s.cb.callback(1, 2));
    }
    
    public static class VolatileStructure extends Structure {
        public volatile int counter;
        public int value;
    }
    public void testVolatileStructureField() {
        VolatileStructure s = new VolatileStructure();
        s.counter = 1;
        s.value = 1;
        s.write();
        assertEquals("Volatile field should not be written", 0, s.getPointer().getInt(0));
        assertEquals("Non-volatile field should be written", 1, s.getPointer().getInt(4));
        s.writeField("counter");
        assertEquals("Explicit volatile field write failed", 1, s.getPointer().getInt(0));
    }
}