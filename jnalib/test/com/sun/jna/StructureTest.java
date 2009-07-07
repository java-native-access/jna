/* Copyright (c) 2007-2009 Timothy Wall, All Rights Reserved
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
import java.nio.DoubleBuffer;
import java.util.Map;

import junit.framework.TestCase;

import com.sun.jna.ptr.ByteByReference;
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


    public void testFieldsAllocated() {
        class TestStructure extends Structure {
            public TestStructure() { }
            public TestStructure(Pointer p) { super(p); }
            public int field;
            public int fieldCount() { return fields().size(); }
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong number of fields (default)", 1, s.fieldCount());

        s = new TestStructure(new Memory(4));
        assertEquals("Wrong number of fields (preallocated)", 1, s.fieldCount());
    }

    public void testProvidedMemoryTooSmall() {
        class TestStructure extends Structure {
            public TestStructure() { }
            public TestStructure(Pointer p) { super(p); }
            public int field;
            public int fieldCount() { return fields().size(); }
        }
        try {
            TestStructure s = new TestStructure(new Memory(2));
            fail("Expect exception if provided memory is insufficient");
        }
        catch(IllegalArgumentException e) {
        }
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

    // must be publicly accessible in order to create array elements
    public static class PublicTestStructure extends Structure {
        public static class ByReference extends PublicTestStructure implements Structure.ByReference { }
        public int x, y;
    }
    public void testStructureField() {
        class TestStructure extends Structure {
            public PublicTestStructure s1, s2;
            public int after;
        }
        TestStructure s = new TestStructure();
        assertNotNull("Inner structure should be initialized", s.s1);
        assertEquals("Wrong aggregate size",
                     s.s1.size() + s.s2.size() + 4, s.size());
        s.write();
        assertEquals("Wrong memory for structure field 1 after write",
                     s.getPointer(), s.s1.getPointer());
        assertEquals("Wrong memory for structure field 2 after write",
                     s.getPointer().share(s.s1.size()),
                     s.s2.getPointer());

        s.read();
        assertEquals("Wrong memory for structure field 1 after read",
                     s.getPointer(), s.s1.getPointer());
        assertEquals("Wrong memory for structure field 2 after read",
                     s.getPointer().share(s.s1.size()),
                     s.s2.getPointer());
    }

    public void testPrimitiveArrayField() {
        class TestStructure extends Structure {
            public byte[] buffer = new byte[1024];
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong size for structure with nested array", 1024, s.size());
        assertNotNull("Array should be initialized", s.buffer);
        s.write();
        s.read();
    }

    public void testStructureArrayField() {
        class TestStructure extends Structure {
            public PublicTestStructure[] inner = new PublicTestStructure[2];
            public PublicTestStructure[] inner2 = (PublicTestStructure[])
                new PublicTestStructure().toArray(2);
        }
        TestStructure s = new TestStructure();
        int innerSize = new PublicTestStructure().size();
        assertEquals("Wrong size for structure with nested array of struct",
                     s.inner.length * innerSize + s.inner2.length * innerSize,
                     s.size());
        s.write();
        assertNotNull("Inner array elements should auto-initialize", s.inner[0]);
        s.inner[0].x = s.inner[0].y = -1;
        s.inner[1].x = s.inner[1].y = -1;
        s.read();
        assertEquals("Inner structure array element 0 not properly read",
                     0, s.inner[0].x);
        assertEquals("Inner structure array element 1 not properly read",
                     0, s.inner[1].x);

        assertEquals("Wrong memory for uninitialized nested array",
                     s.getPointer(), s.inner[0].getPointer());
        assertEquals("Wrong memory for initialized nested array",
                     s.getPointer().share(innerSize * s.inner.length),
                     s.inner2[0].getPointer());
    }

    public static class ToArrayTestStructure extends Structure {
        public PublicTestStructure[] inner =
            (PublicTestStructure[])new PublicTestStructure().toArray(2);
    }
    public void testToArrayWithStructureArrayField() {
        ToArrayTestStructure[] array =
            (ToArrayTestStructure[])new ToArrayTestStructure().toArray(2);
        assertEquals("Wrong address for top-level array element",
                     array[0].getPointer().share(array[0].size()),
                     array[1].getPointer());
        assertEquals("Wrong address for nested array element",
                     array[1].inner[0].getPointer().share(array[1].inner[0].size()),
                     array[1].inner[1].getPointer());
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
            public boolean z;       // native int
            public byte b;          // native char
            public char c;          // native wchar_t
            public short s;         // native short
            public int i;           // native int
            public long l;          // native long long
            public float f;         // native float
            public double d;        // native double
            public byte[] ba = new byte[3];
            public char[] ca = new char[3];
            public short[] sa = new short[3];
            public int[] ia = new int[3];
            public long[] la = new long[3];
            public float[] fa = new float[3];
            public double[] da = new double[3];
            public PublicTestStructure nested;
        }
        TestStructure s = new TestStructure();
        // set content of the structure
        s.z = true;
        s.b = 1;
        s.c = 2;
        s.s = 3;
        s.i = 4;
        s.l = 5;
        s.f = 6.0f;
        s.d = 7.0;
        s.nested.x = 1;
        s.nested.y = 2;
        for (int i = 0; i < 3; i++) {
            s.ba[i] = (byte) (8 + i);
            s.ca[i] = (char) (11 + i);
            s.sa[i] = (short) (14 + i);
            s.ia[i] = 17 + i;
            s.la[i] = 23 + i;
            s.fa[i] = (float) 26 + i;
            s.da[i] = (double) 29 + i;
        }
        // write content to memory
        s.write();
        Pointer p = s.getPointer();
        s = new TestStructure();
        s.useMemory(p);
        // read content from memory and compare field values
        s.read();
        assertEquals("Wrong boolean field value after write/read", s.z, true);
        assertEquals("Wrong byte field value after write/read", s.b, 1);
        assertEquals("Wrong char field value after write/read", s.c, 2);
        assertEquals("Wrong short field value after write/read", s.s, 3);
        assertEquals("Wrong int field value after write/read", s.i, 4);
        assertEquals("Wrong long field value after write/read", s.l, 5);
        assertEquals("Wrong float field value after write/read", s.f, 6.0f);
        assertEquals("Wrong double field value after write/read", s.d, 7.0);
        assertEquals("Wrong nested struct field value after write/read (x)", s.nested.x, 1);
        assertEquals("Wrong nested struct field value after write/read (y)", s.nested.y, 2);
        for (int i = 0; i < 3; i++) {
            assertEquals("Wrong byte array field value after write/read", s.ba[i], (byte) (8 + i));
            assertEquals("Wrong char array field value after write/read", s.ca[i], (char) (11 + i));
            assertEquals("Wrong short array field value after write/read", s.sa[i], (short) (14 + i));
            assertEquals("Wrong int array field value after write/read", s.ia[i], 17 + i);
            assertEquals("Wrong long array field value after write/read", s.la[i], 23 + i);
            assertEquals("Wrong float array field value after write/read", s.fa[i], (float) 26 + i);
            assertEquals("Wrong double array field value after write/read", s.da[i], (double) 29 + i);
        }
        // test constancy of references after read
        int[] ia = s.ia;
        s.read();
        assertTrue("Array field reference should be unchanged", ia == s.ia);
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
            new BadFieldStructure().size();
            fail("Function fields should not be allowed");
        }
        catch(IllegalArgumentException e) {
        }
    }

    public static class BadFieldStructure extends Structure {
        public Object badField;
    }
    public void testUnsupportedField() {
        class BadNestedStructure extends Structure {
            public BadFieldStructure badStruct = new BadFieldStructure();
        }
        try {
            new BadFieldStructure().size();
            fail("Should throw IllegalArgumentException on bad field");
        }
        catch(IllegalArgumentException e) {
            assertTrue("Exception should include field name",
                       e.getMessage().indexOf("badField") != -1);
        }
        try {
            new BadNestedStructure().size();
            fail("Should throw IllegalArgumentException on bad field");
        }
        catch(IllegalArgumentException e) {
            assertTrue("Exception should include enclosing type: " + e,
                       e.getMessage().indexOf(BadNestedStructure.class.getName()) != -1);
            assertTrue("Exception should include nested field name: " + e,
                       e.getMessage().indexOf("badStruct") != -1);
            assertTrue("Exception should include field name: " + e,
                       e.getMessage().indexOf("badField") != -1);
        }
    }

    public void testToArray() {
        PublicTestStructure s = new PublicTestStructure();
        PublicTestStructure[] array = (PublicTestStructure[])s.toArray(1);
        assertEquals("Array should consist of a single element",
                     1, array.length);
        assertEquals("First element should be original", s, array[0]);
        assertEquals("Structure memory should be expanded", 2, s.toArray(2).length);
    }

    public void testByReferenceArraySync() {
        PublicTestStructure.ByReference s = new PublicTestStructure.ByReference();
        PublicTestStructure.ByReference[] array =
            (PublicTestStructure.ByReference[])s.toArray(2);
        class TestStructure extends Structure {
            public PublicTestStructure.ByReference ref;
        }
        TestStructure ts = new TestStructure();
        ts.ref = s;
        final int VALUE = 42;
        array[0].x = VALUE;
        array[1].x = VALUE;
        ts.write();

        assertEquals("Array element not written: " + array[0],
                     VALUE, array[0].getPointer().getInt(0));
        assertEquals("Array element not written: " + array[1],
                     VALUE, array[1].getPointer().getInt(0));

        array[0].getPointer().setInt(4, VALUE);
        array[1].getPointer().setInt(4, VALUE);
        ts.read();

        assertEquals("Array element not read: " + array[0], VALUE, array[0].y);
        assertEquals("Array element not read: " + array[1], VALUE, array[1].y);
    }

    static class CbStruct extends Structure {
        public Callback cb;
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
    public void testPlainStructureArrayField() {
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

    class BufferStructure extends Structure {
        public Buffer buffer;
        public DoubleBuffer dbuffer;
    }
    public void testBufferFieldWriteNULL() {
        BufferStructure bs = new BufferStructure();
        bs.write();
    }
    public void testBufferFieldWriteNonNULL() {
        BufferStructure bs = new BufferStructure();
        bs.buffer = ByteBuffer.allocateDirect(16);
        bs.dbuffer = ((ByteBuffer)bs.buffer).asDoubleBuffer();
        bs.write();
    }
    public void testBufferFieldReadUnchanged() {
        BufferStructure bs = new BufferStructure();
        bs.buffer = ByteBuffer.allocateDirect(16);
        bs.dbuffer = ((ByteBuffer)bs.buffer).asDoubleBuffer();
        bs.write();
        bs.read();
    }
    public void testBufferFieldReadChanged() {
        BufferStructure bs = new BufferStructure();
        if (Pointer.SIZE == 4) {
            bs.getPointer().setInt(0, 0x1);
        }
        else {
            bs.getPointer().setLong(0, 0x1);
        }
        try {
            bs.read();
            fail("Structure read should fail if Buffer pointer was set");
        }
        catch(IllegalStateException e) {
        }
        bs.buffer = ByteBuffer.allocateDirect(16);
        try {
            bs.read();
            fail("Structure read should fail if Buffer pointer has changed");
        }
        catch(IllegalStateException e) {
        }
    }
    public void testBufferFieldReadChangedToNULL() {
        BufferStructure bs = new BufferStructure();
        bs.buffer = ByteBuffer.allocateDirect(16);
        bs.dbuffer = ((ByteBuffer)bs.buffer).asDoubleBuffer();
        bs.read();
        assertNull("Structure Buffer field should be set null", bs.buffer);
        assertNull("Structure DoubleBuffer field should be set null", bs.dbuffer);
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
        public PublicTestStructure.ByReference s1;
        public PublicTestStructure.ByReference s2;
    }
    public void testStructureByReferenceField() {
        StructureWithPointers s = new StructureWithPointers();
        assertEquals("Wrong size for structure with structure references",
                     Pointer.SIZE * 2, s.size());

        assertNull("Initial refs should be null", s.s1);
    }

    public void testRegenerateStructureByReferenceField() {
        StructureWithPointers s = new StructureWithPointers();
        PublicTestStructure.ByReference inner =
            new PublicTestStructure.ByReference();
        s.s1 = inner;
        s.write();
        s.s1 = null;
        s.read();
        assertEquals("Inner structure not regenerated on read", inner, s.s1);
    }

    public void testPreserveStructureByReferenceWithUnchangedPointerOnRead() {
        StructureWithPointers s = new StructureWithPointers();
        PublicTestStructure.ByReference inner =
            new PublicTestStructure.ByReference();

        s.s1 = inner;
        s.write();
        s.read();
        assertSame("Read should preserve structure object", inner, s.s1);
        assertTrue("Read should preserve structure memory",
                   inner.getPointer() instanceof Memory);
    }
    
    public static class TestPointer extends PointerType { }
    public void testPreservePointerFields() {
        class TestStructure extends Structure {
            public Pointer p = new Memory(256);
            public TestPointer p2 = new TestPointer() {
                    { setPointer(new Memory(256)); }
                };
        }
        TestStructure s = new TestStructure();
        final Pointer p = s.p;
        s.write();
        s.read();
        assertSame("Should preserve Pointer references if peer unchanged", p, s.p);
    }

    public void testOverwriteStructureByReferenceFieldOnRead() {
        StructureWithPointers s = new StructureWithPointers();
        PublicTestStructure.ByReference inner =
            new PublicTestStructure.ByReference();
        PublicTestStructure.ByReference inner2 =
            new PublicTestStructure.ByReference();
        s.s1 = inner2;
        s.write();
        s.s1 = inner;
        s.read();
        assertNotSame("Read should overwrite structure reference", inner, s.s1);
    }

    public void testAutoWriteStructureByReferenceField() {
        StructureWithPointers s = new StructureWithPointers();
        s.s1 = new StructureTest.PublicTestStructure.ByReference();
        s.s1.x = -1;
        s.write();
        assertEquals("Structure.ByReference not written automatically",
                     -1, s.s1.getPointer().getInt(0));
    }

    public void testStructureByReferenceArrayField() {
        class TestStructure extends Structure {
            public PublicTestStructure.ByReference[] array = new PublicTestStructure.ByReference[2];
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong structure size", 2*Pointer.SIZE, s.size());

        PublicTestStructure.ByReference ref = new PublicTestStructure.ByReference();
        ref.x = 42;
        Object aref = s.array;
        s.array[0] = ref;
        s.array[1] = new PublicTestStructure.ByReference();

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

    public void testAutoReadWriteStructureByReferenceArrayField() {
        class TestStructure extends Structure {
            public PublicTestStructure.ByReference field;
        }
        TestStructure s = new TestStructure();
        s.field = new PublicTestStructure.ByReference();
        PublicTestStructure.ByReference[] array = 
            (PublicTestStructure.ByReference[])s.field.toArray(2);
        final int VALUE = -1;
        array[1].x = VALUE;
        s.write();
        assertEquals("ByReference array member not auto-written",
                     VALUE, array[1].getPointer().getInt(0));

        array[1].getPointer().setInt(0, VALUE*2);
        s.read();
        assertEquals("ByReference array member not auto-read",
                     VALUE*2, array[1].x);
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
        public size_t(long value) { super(Native.POINTER_SIZE, value); }
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

    public void testTypeInfoForNull() {
        assertEquals("Wrong type information for 'null'",
                     Structure.getTypeInfo(new Pointer(0)),
                     Structure.getTypeInfo(null));
    }

    public void testToString() {
        class TestStructure extends Structure {
            public int intField;
            public PublicTestStructure inner;
        }
        TestStructure s = new TestStructure();
        final String LS = System.getProperty("line.separator");
        System.setProperty("jna.dump_memory", "true");
        final String EXPECTED = "(?m).*" + s.size() + " bytes.*\\{" + LS
            + "  int intField@0=0" + LS
            + "  .* inner@4=.*\\{" + LS
            + "    int x@0=0" + LS
            + "    int y@4=0" + LS
            + "  \\}" + LS
            + "\\}" + LS
            + "memory dump" + LS
            + "\\[00000000\\]" + LS
            + "\\[00000000\\]" + LS
            + "\\[00000000\\]";
        String actual = s.toString();
        assertTrue("Improperly formatted toString(): expected "
                   + EXPECTED + "\n" + actual,
                   actual.matches(EXPECTED));
    }

    public void testNativeMappedWrite() {
    	class TestStructure extends Structure {
    		public ByteByReference ref;
    	}
    	TestStructure s = new TestStructure();
    	s.ref = null;
    	s.write();
    }

    public static class ROStructure extends Structure {
        public final int field;
        {
            field = 0;
            getPointer().setInt(0, 42);
            read();
        }
    }
    public void testReadOnlyField() {
        ROStructure s = new ROStructure();
        assertEquals("Field value should be writable from native", 42, s.field);
    }
    public void testNativeMappedArrayField() {
        final int SIZE = 24;
        class TestStructure extends Structure {
            public NativeLong[] longs = new NativeLong[SIZE];
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong structure size", Native.LONG_SIZE * SIZE, s.size());

        NativeLong[] aref = s.longs;
        for (int i=0;i < s.longs.length;i++) {
            s.longs[i] = new NativeLong(i);
        }
        s.write();
        for (int i=0;i < s.longs.length;i++) {
            assertEquals("Value not written to memory at index " + i,
                         i, s.getPointer().getNativeLong(i * NativeLong.SIZE).intValue());
        }
        s.read();
        assertEquals("Array reference should remain unchanged on read",
                     aref, s.longs);

        for (int i=0;i < s.longs.length;i++) {
            assertEquals("Wrong value after read at index " + i,
                         i, s.longs[i].intValue());
        }
    }


    public void testInitializeNativeMappedField() {
        final long VALUE = 20;
        final NativeLong INITIAL = new NativeLong(VALUE);
        class TestStructure extends Structure {
            // field overwritten, wrong value before write
            // NL bug, wrong value written
            { setAlignType(ALIGN_NONE); }
            public NativeLong nl = INITIAL;
            public NativeLong uninitialized;
        }
        TestStructure ts = new TestStructure();
        assertEquals("Wrong value in field", VALUE, ts.nl.longValue());
        assertSame("Initial value overwritten", INITIAL, ts.nl);
        assertEquals("Wrong field value before write", VALUE, ts.nl.longValue());
        assertNotNull("Uninitialized field should be initialized", ts.uninitialized);
        assertEquals("Wrong initialized value", 0, ts.uninitialized.longValue());
        ts.write();
        assertEquals("Wrong field value written", VALUE, ts.getPointer().getNativeLong(0).longValue());
        assertEquals("Wrong field value written (2)", 0, ts.getPointer().getNativeLong(NativeLong.SIZE).longValue());
        ts.read();
        assertEquals("Wrong field value", VALUE, ts.nl.longValue());
        assertEquals("Wrong field value (2)", 0, ts.uninitialized.longValue());
    }

    public void testStructureFieldOrder() {
        Structure.REQUIRES_FIELD_ORDER = true;
        try {
            class TestStructure extends Structure {
                public int one = 1;
                public int three = 3;
                public int two = 2;
                {
                    setFieldOrder(new String[] { "one", "two", "three" });
                }
            }
            class DerivedTestStructure extends TestStructure {
                public int four = 4;
                {
                    setFieldOrder(new String[] { "four" });
                }
            }

            DerivedTestStructure s = new DerivedTestStructure();
            DerivedTestStructure s2 = new DerivedTestStructure();
            s.write();
            s2.write();
            assertEquals("Wrong first field", 1, s.getPointer().getInt(0));
            assertEquals("Wrong second field", 2, s.getPointer().getInt(4));
            assertEquals("Wrong third field", 3, s.getPointer().getInt(8));
            assertEquals("Wrong derived field", 4, s.getPointer().getInt(12));
        }
        finally {
            Structure.REQUIRES_FIELD_ORDER = false;
        }
    }

    public void testCustomTypeMapper() {
        class TestField { }
        class TestStructure extends Structure {
            public TestField field;
            public TestStructure() {
                DefaultTypeMapper m = new DefaultTypeMapper();
                m.addTypeConverter(TestField.class, new TypeConverter() {
                    public Object fromNative(Object value, FromNativeContext context) {
                        return new TestField();
                    }
                    public Class nativeType() {
                        return String.class;
                    }
                    public Object toNative(Object value, ToNativeContext ctx) {
                        return value == null ? null : value.toString();
                    }
                });
                setTypeMapper(new DefaultTypeMapper());
            }
        }
        new TestStructure();
    }
    
    public void testWriteWithNullBoxedPrimitives() {
        class TestStructure extends Structure {
            public Boolean zfield;
            public Integer field;
        }
        TestStructure s = new TestStructure();
        s.write();
        s.read();
        assertNotNull("Field should not be null after read", s.field);
    }

    public void testStructureEquals() {
        class TestStructure extends Structure {
            public byte first;
            public int second;
        }
        TestStructure s1 = new TestStructure();
        TestStructure s2 = new TestStructure();
        s2.getPointer().setInt(0, -1);
        s2.write();
        assertEquals("Structure equals should ignore padding", s1, s2);
    }

    public void testRecursiveReadWrite() {
        class TestStructureByRef extends Structure implements Structure.ByReference{
            public TestStructureByRef s;
        }
        TestStructureByRef s = new TestStructureByRef();
        s.s = s;
        s.write();
        s.read();
    }
}
