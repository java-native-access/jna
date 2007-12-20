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
    
    // must be public to populate array
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
    // Do NOT change the order of naming w/o changing testlib.c as well
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
            Structure s = Structure.newInstance(cls);
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
    
    public void testNestArray() {
        class TestStructure extends Structure {
            public byte[] buffer = new byte[1024];
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong size for nested array", 1024, s.size());
        assertNotNull("Array should be initialized", s.buffer);
    }
    
    public void testUninitializedNestedArrayFails() {
        class TestStructure extends Structure {
            public Pointer[] buffer;
        }
        TestStructure s = new TestStructure();
        try {
            s.size();
            fail("Size can't be calculated unless array fields are initialized");
        }
        catch(IllegalStateException e) {
        }
    }

    public void testReadWriteStructure() {
        class TestStructure extends Structure {
            public TestStructure() {
                // Have to do this due to inline primitive arrays
                allocateMemory();
            }
            public boolean z;
            public byte b;
            public char c;
            public short s;
            public int i;
            public long j;
            public float f;
            public double d;
            public byte[] ba = new byte[8];
            public char[] ca = new char[8];
            public short[] sa = new short[8];
            public int[] ia = new int[8];
            public long[] ja = new long[8];
            public float[] fa = new float[8];
            public double[] da = new double[8];
            public InnerStructure nested;
        }
        TestStructure s = new TestStructure();
        s.z = true;
        s.b = 1;
        s.s = 2;
        s.c = 'a';
        s.i = 3;
        s.j = 4;
        s.f = 5;
        s.d = 6;
        s.nested.x = 1;
        s.nested.y = 2;
        s.ba[0] = 3;
        s.write();
        s.z = false;
        s.b = 0;
        s.c = 0;
        s.s = 0;
        s.i = 0;
        s.j = 0;
        s.f = 0;
        s.d = 0;
        s.nested.x = s.nested.y = 0;
        s.ba[0] = 0;
        byte[] ref = s.ba;
        s.read();
        assertTrue("Wrong boolean field value after write/read", s.z);
        assertEquals("Wrong byte field value after write/read", (byte)1, s.b);
        assertEquals("Wrong char field value after write/read", 'a', s.c);
        assertEquals("Wrong short field value after write/read", 2, s.s);
        assertEquals("Wrong int field value after write/read", 3, s.i);
        assertEquals("Wrong long field value after write/read", 4, s.j);
        assertEquals("Wrong float field value after write/read", 5.0f, s.f);
        assertEquals("Wrong double field value after write/read", 6.0d, s.d);
        assertEquals("Wrong nested struct field value after write/read (x)",
                     1, s.nested.x);
        assertEquals("Wrong nested struct field value after write/read (y)",
                     2, s.nested.y);
        assertEquals("Wrong nested array element value after write/read",
                     3, s.ba[0]);
        assertSame("Array field reference should be unchanged", ref, s.ba);
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
    
    public void testDisallowFunctionPointerAsField() {
        class BadFieldStructure extends Structure {
            public Function cb;
        }
        try {
            BadFieldStructure s = new BadFieldStructure();
            fail("Function fields should not be allowed");
        }
        catch(IllegalArgumentException e) {
        }
    }

    // must be publicly accessible in order to create array elements
    public static class PublicTestStructure extends Structure {
        public int value;
    }
    public void testToArray() {
        PublicTestStructure s = new PublicTestStructure();
        PublicTestStructure[] array = (PublicTestStructure[])s.toArray(1);
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
    
    public void testUninitializedArrayField() {
        class UninitializedArrayFieldStructure extends Structure {
            public byte[] array;
        }
        try {
            Structure s = new UninitializedArrayFieldStructure();
            assertTrue("Invalid size: " + s.size(), s.size() > 0);
            fail("Uninitialized array field should cause write failure");
        }
        catch(IllegalStateException e) {
        }
    }
    
    public static class ArrayOfStructure extends Structure {
        public Structure[] array;
    }
    public void testStructureArrayField() {
        try {
            new ArrayOfStructure();
            fail("Structure[] not allowed as a field of Structure");
        }
        catch(IllegalArgumentException e) {
        }
        catch(Exception e) {
            fail("Wrong exception thrown on Structure[] field in Structure: " + e);
        }
    }
    
    public void testPointerArrayField() {
        class ArrayOfPointerStructure extends Structure {
            final static int SIZE = 10;
            public Pointer[] array = new Pointer[SIZE];
        }
        ArrayOfPointerStructure s = new ArrayOfPointerStructure();
        int size = s.size();
        assertEquals("Wrong size", ArrayOfPointerStructure.SIZE * Pointer.SIZE, size);
        s.array[0] = s.getPointer();
        s.write();
        s.array[0] = null;
        s.read();
        assertEquals("Wrong first element", s.getPointer(), s.array[0]);
    }
    
    public void testBufferField() {
        // NOTE: may support write-only Buffer fields in the future
        class BufferStructure extends Structure {
            public Buffer buffer;
            public BufferStructure(byte[] buf) {
                buffer = ByteBuffer.wrap(buf);
            }
        }
    	try {
    		new BufferStructure(new byte[1024]);
    		fail("Buffer fields should fail immediately");
    	}
    	catch(IllegalArgumentException e) { 
    	}
    }
    
    public void testVolatileStructureField() {
        class VolatileStructure extends Structure {
            public volatile int counter;
            public int value;
        }
        VolatileStructure s = new VolatileStructure();
        s.counter = 1;
        s.value = 1;
        s.write();
        assertEquals("Volatile field should not be written", 0, s.getPointer().getInt(0));
        assertEquals("Non-volatile field should be written", 1, s.getPointer().getInt(4));
        s.writeField("counter");
        assertEquals("Explicit volatile field write failed", 1, s.getPointer().getInt(0));
    }

    public static class StructureWithPointers extends Structure {
        public static class TestStructureByRef extends Structure implements ByReference {
            public int dummy;
        }
        public TestStructureByRef s1;
        public TestStructureByRef s2;
    }
    public void testStructureByReferenceSize() {
        StructureWithPointers s = new StructureWithPointers();
        assertEquals("Wrong size for structure with structure references",
                     Pointer.SIZE * 2, s.size());
        
        assertNull("Initial refs should be null", s.s1);
    }
    
    public void testRegenerateStructureByReferenceField() {
        StructureWithPointers s = new StructureWithPointers();
        StructureWithPointers.TestStructureByRef inner = 
            new StructureWithPointers.TestStructureByRef();
        s.s1 = inner;
        s.write();
        s.s1 = null;
        s.read();
        assertEquals("Inner structure not regenerated on read", inner, s.s1);
    }

    public void testPreserveStructureByReferenceWithUnchangedPointer() {
        StructureWithPointers s = new StructureWithPointers();
        StructureWithPointers.TestStructureByRef inner = 
            new StructureWithPointers.TestStructureByRef();
        
        s.s1 = inner;
        s.write();
        s.read();
        assertSame("Read should preserve structure object", inner, s.s1); 
        assertTrue("Read should preserve structure memory", 
                   inner.getPointer() instanceof Memory);
    }
    
    public void testOverwriteStructureByReferenceField() {
        StructureWithPointers s = new StructureWithPointers();
        StructureWithPointers.TestStructureByRef inner = 
            new StructureWithPointers.TestStructureByRef();
        StructureWithPointers.TestStructureByRef inner2 = 
            new StructureWithPointers.TestStructureByRef();
        s.s1 = inner2;
        s.write();
        s.s1 = inner;
        s.read();
        assertNotSame("Read should overwrite structure reference", inner, s.s1);
    }
    
    public static class PublicTestStructureByRef extends Structure implements Structure.ByReference {
        public int field;
    }
    public void testInnerByReferenceArray() {
        class TestStructure extends Structure {
            public PublicTestStructureByRef[] array = new PublicTestStructureByRef[2];
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong structure size", 2*Pointer.SIZE, s.size());
        
        PublicTestStructureByRef ref = new PublicTestStructureByRef();
        ref.field = 42;
        Object aref = s.array;
        s.array[0] = ref;
        s.array[1] = new PublicTestStructureByRef();
        
        s.write();
        s.read();
        
        assertSame("Array reference should not change", aref, s.array);
        assertSame("Elements should not be overwritten when unchanged", 
                   ref, s.array[0]);
        
        s.array[0] = null;
        s.read();
        assertNotSame("Null should be overwritten with a new ref", ref, s.array[0]);
        assertNotNull("New ref should not be null", s.array[0]);
        assertEquals("New ref should be equivalent", ref, s.array[0]);
    }

    static class NestedTypeInfoStructure extends Structure {
        public static class Inner extends Structure {
            public int dummy;
        }
        public Inner inner;
        public int dummy;
    }
    public static class size_t extends IntegerType { 
        public size_t() { this(0); }
        public size_t(long value) { super(Pointer.SIZE, value); }
    }
    public void testNestedStructureTypeInfo() {
        class FFIType extends Structure {
            public FFIType(Pointer p) { 
                useMemory(p); read();
            }
            public size_t size;
            public short alignment;
            public short type;
            public Pointer elements;
        }
        NestedTypeInfoStructure s = new NestedTypeInfoStructure();
        Pointer p = s.getTypeInfo();
        FFIType ffi_type = new FFIType(p);
        assertNotNull("Type info should not be null", p);
        Pointer els = ffi_type.elements;
        Pointer inner = s.inner.getTypeInfo();
        assertEquals("Wrong type information for 'inner' field",
                     inner, els.getPointer(0));
        assertEquals("Wrong type information for integer field",
                     Structure.getTypeInfo(new Integer(0)),
                     els.getPointer(Pointer.SIZE));
        assertNull("Type element list should be null-terminated", 
                   els.getPointer(Pointer.SIZE*2));
    }
    
    public void testInnerArrayTypeInfo() {
        class TestStructure extends Structure {
            public int[] inner = new int[5];
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong structure size", 20, s.size());
        Pointer p = s.getTypeInfo();
        assertNotNull("Type info should not be null", p);
    }
    
    public void testToString() {
        class TestStructure extends Structure {
            public int intField;
            public InnerStructure inner;
        }
        TestStructure s = new TestStructure();
        final String LS = System.getProperty("line.separator");
        final String EXPECTED = "(?m).*" + s.size() + " bytes.*" + LS 
            + "  int intField@0=0" + LS
            + "  .*InnerStructure inner@4=.*" + LS
            + "    int x@0=0" + LS
            + "    int y@4=0" + LS
            + "memory dump" + LS
            + "\\[00000000\\]" + LS
            + "\\[00000000\\]" + LS
            + "\\[00000000\\]";
        String actual = s.toString();
        assertTrue("Improperly formatted toString(): " + actual, 
                   actual.matches(EXPECTED));
    }
}