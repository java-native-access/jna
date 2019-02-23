/* Copyright (c) 2007-2009 Timothy Wall, All Rights Reserved
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Structure.StructureSet;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/** TODO: need more alignment tests, especially platform-specific behavior
 * @author twall@users.sf.net
 */
//@SuppressWarnings("unused")
public class StructureTest extends TestCase {

    private static final String UNICODE = "[\u0444]";

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(StructureTest.class);
    }

    public void testSimpleSize() throws Exception {
        class TestStructure extends Structure {
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        Structure s = new TestStructure();
        assertEquals("Wrong size", 4, s.size());
    }

    public void testInitializeFromPointer() {
        class TestStructureX extends Structure {
            public int field1, field2;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field1", "field2");
            }
            public TestStructureX() {
            }
            public TestStructureX(Pointer p) {
                super(p);
            }
        }
        Structure s = new TestStructureX();
        Pointer p = s.getPointer();
        Structure s1 = new TestStructureX(p);
        Pointer p1 = s1.getPointer();

        assertEquals("Constructor address not used", p, p1);
        assertFalse("Pointer should not be auto-allocated", p.getClass().equals(p1.getClass()));
        assertNotSame("Initial pointer should not be used directly: " + p, p, p1);
    }

    public void testInitializeWithTypeMapper() {
        class TestStructure extends Structure {
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
            public TestStructure(TypeMapper m) {
                super(ALIGN_DEFAULT, m);
            }
        }
        TypeMapper m = new DefaultTypeMapper();
        TestStructure s = new TestStructure(m);
        assertEquals("Type mapper not installed", m, s.getTypeMapper());
    }

    // must be public to populate array
    public static class TestAllocStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("f0", "f1", "f2", "f3");
        public int f0;
        public int f1;
        public int f2;
        public int f3;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public void testFieldsAllocated() {
        class TestStructure extends Structure {
            public TestStructure() { }
            public TestStructure(Pointer p) { super(p); }
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
            public int fieldCount() { ensureAllocated(); return fields().size(); }
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong number of fields (default)", 1, s.fieldCount());

        s = new TestStructure(new Memory(4));
        assertEquals("Wrong number of fields (preallocated)", 1, s.fieldCount());
    }

    public void testProvidedMemoryTooSmall() {
        class TestStructure extends Structure {
            public TestStructure(Pointer p) { super(p); }
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        try {
            new TestStructure(new Memory(2));
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
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("b", "s", "i", "l", "f", "d");
            }
        }
        Structure s = new TestStructure();
        s.setAlignType(Structure.ALIGN_GNUC);
        final int SIZE = Native.MAX_ALIGNMENT == 8 ? 32 : 28;
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
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("b", "s", "i", "l", "f", "d");
            }
        }
        Structure s = new TestStructure();
        s.setAlignType(Structure.ALIGN_MSVC);
        assertEquals("Wrong structure size", 32, s.size());
    }

    public static abstract class FilledStructure extends Structure {
        private boolean initialized;
        @Override
        protected void ensureAllocated() {
            super.ensureAllocated();
            if (!initialized) {
                initialized = true;
                for (int i=0;i < size();i++) {
                    getPointer().setByte(i, (byte)0xFF);
                }
            }
        }
    }
    // Do NOT change the order of naming w/o changing testlib.c as well
    public static class TestStructure0 extends FilledStructure {
        public static final List<String> FIELDS = createFieldsOrder("field0", "field1");
        public byte field0 = 0x01;
        public short field1 = 0x0202;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    @FieldOrder({ "field0", "field1" })
    public static class AnnotatedTestStructure0 extends FilledStructure {
        public byte field0 = 0x01;
        public short field1 = 0x0202;
    }
    public static class TestStructure1 extends FilledStructure {
        public static final List<String> FIELDS = createFieldsOrder("field0", "field1");
        public byte field0 = 0x01;
        public int field1 = 0x02020202;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    @FieldOrder({ "field0", "field1" })
    public static class AnnotatedTestStructure1 extends FilledStructure {
        public byte field0 = 0x01;
        public int field1 = 0x02020202;
    }
    public static class TestStructure2 extends FilledStructure {
        public static final List<String> FIELDS = createFieldsOrder("field0", "field1");
        public short field0 = 0x0101;
        public int field1 = 0x02020202;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    @FieldOrder({ "field0", "field1" })
    public static class AnnotatedTestStructure2 extends FilledStructure {
        public short field0 = 0x0101;
        public int field1 = 0x02020202;
    }
    public static class TestStructure3 extends FilledStructure {
        public static final List<String> FIELDS = createFieldsOrder("field0", "field1", "field2");
        public int field0 = 0x01010101;
        public short field1 = 0x0202;
        public int field2 = 0x03030303;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    @FieldOrder({ "field0", "field1", "field2" })
    public static class AnnotatedTestStructure3 extends FilledStructure {
        public int field0 = 0x01010101;
        public short field1 = 0x0202;
        public int field2 = 0x03030303;
    }
    public static class TestStructure4 extends FilledStructure {
        public static final List<String> FIELDS = createFieldsOrder("field0", "field1", "field2", "field3");
        public int field0 = 0x01010101;
        public long field1 = 0x0202020202020202L;
        public int field2 = 0x03030303;
        public long field3 = 0x0404040404040404L;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    @FieldOrder({ "field0", "field1", "field2", "field3" })
    public static class AnnotatedTestStructure4 extends FilledStructure {
        public int field0 = 0x01010101;
        public long field1 = 0x0202020202020202L;
        public int field2 = 0x03030303;
        public long field3 = 0x0404040404040404L;
    }
    @FieldOrder({ "field0", "field1" })
    public static class _InheritanceTestStructure4Parent extends FilledStructure {
        public int field0 = 0x01010101;
        public long field1 = 0x0202020202020202L;
    }
    @FieldOrder({ "field2", "field3" })
    public static class InheritanceTestStructure4 extends _InheritanceTestStructure4Parent {
        public int field2 = 0x03030303;
        public long field3 = 0x0404040404040404L;
    }
    public static class TestStructure5 extends FilledStructure {
        public static final List<String> FIELDS = createFieldsOrder("field0", "field1");
        public long field0 = 0x0101010101010101L;
        public byte field1 = 0x02;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    @FieldOrder({ "field0", "field1" })
    public static class AnnotatedTestStructure5 extends FilledStructure {
        public long field0 = 0x0101010101010101L;
        public byte field1 = 0x02;
    }
    public interface SizeTest extends Library {
        int getStructureSize(int type);
    }
    private void testStructureSize(int index) {
        testStructureSize("", index);
    }
    private void testStructureSize(String prefix, int index) {
        try {
            SizeTest lib = Native.load("testlib", SizeTest.class);
            Class<? extends Structure> cls = (Class<? extends Structure>) Class.forName(getClass().getName() + "$" + prefix + "TestStructure" + index);
            Structure s = Structure.newInstance(cls);
            assertEquals("Incorrect size for structure " + index + "=>" + s.toString(true), lib.getStructureSize(index), s.size());
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }
    public void testStructureSize0() {
        testStructureSize(0);
    }
    public void annotatedTestStructureSize0() {
        testStructureSize("Eazy", 0);
    }
    public void testStructureSize1() {
        testStructureSize(1);
    }
    public void annotatedTestStructureSize1() {
        testStructureSize("Eazy", 1);
    }
    public void testStructureSize2() {
        testStructureSize(2);
    }
    public void annotatedTestStructureSize2() {
        testStructureSize("Eazy", 2);
    }
    public void testStructureSize3() {
        testStructureSize(3);
    }
    public void annotatedTestStructureSize3() {
        testStructureSize("Eazy", 3);
    }
    public void testStructureSize4() {
        testStructureSize(4);
    }
    public void annotatedTestStructureSize4() {
        testStructureSize("Eazy", 4);
    }
    public void inheritanceTestStructureSize4() {
        testStructureSize("Inheritance", 4);
    }
    public void testStructureSize5() {
        testStructureSize(5);
    }
    public void annotatedTestStructureSize5() {
        testStructureSize("Eazy", 5);
    }

    public interface AlignmentTest extends Library {
        int testStructureAlignment(Structure s, int type,
                                   IntByReference offsetp, LongByReference valuep);
    }

    private void testAlignStruct(int index) {
        testAlignStruct("", index);
    }
    private void testAlignStruct(String prefix,int index) {
        AlignmentTest lib = Native.load("testlib", AlignmentTest.class);
        try {
            IntByReference offset = new IntByReference();
            LongByReference value = new LongByReference();
            Class<?> cls = Class.forName(getClass().getName() + "$" + prefix + "TestStructure" + index);
            Structure s = Structure.newInstance((Class<? extends Structure>) cls);
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
    public void annotatedTestAlignStruct0() {
        testAlignStruct("Annotated", 0);
    }
    public void testAlignStruct1() {
        testAlignStruct(1);
    }
    public void annotatedTestAlignStruct1() {
        testAlignStruct("Annotated", 1);
    }
    public void testAlignStruct2() {
        testAlignStruct(2);
    }
    public void annotatedTestAlignStruct2() {
        testAlignStruct("Annotated", 2);
    }
    public void testAlignStruct3() {
        testAlignStruct(3);
    }
    public void annotatedTestAlignStruct3() {
        testAlignStruct("Annotated", 3);
    }
    public void testAlignStruct4() {
        testAlignStruct(4);
    }
    public void annotatedTestAlignStruct4() {
        testAlignStruct("Annotated", 4);
    }
    public void inheritanceTestAlignStruct4() {
        testAlignStruct("Inheritance", 4);
    }
    public void testAlignStruct5() {
        testAlignStruct(5);
    }
    public void annotatedTestAlignStruct5() {
        testAlignStruct("Annotated", 5);
    }

    public void testInheritanceFieldOrder() {
        @FieldOrder({ "a", "b" })
        class Parent extends Structure {
            public int a;
            public int b;
        }
        @FieldOrder({ "c", "d" })
        class Son extends Parent {
            public int c;
            public int d;
        }

        Son test = new Son();
        assertEquals("Wrong field order", Arrays.asList("a", "b", "c", "d"), test.getFieldOrder());
    }

    public void testStructureWithNoFields() {
        class TestStructure extends Structure {
            @Override
            protected List<String> getFieldOrder() {
                return Collections.emptyList();
            }
        }
        try {
            new TestStructure();
            fail("Structure should not be instantiable if it has no public member fields");
        }
        catch(IllegalArgumentException e) {
            // expected - ignored
        }
    }

    public void testAnnotatedStructureWithNoFields() {
        class TestStructure extends Structure {
        }
        try {
            new TestStructure();
            fail("Structure should not be instantiable if it has no public member fields");
        }
        catch(IllegalArgumentException e) {
            // expected - ignored
        }
    }

    public void testAnnotatedStructureWithNoFieldsWithAnnot() {
        @FieldOrder({ })
        class TestStructure extends Structure {
        }
        try {
            new TestStructure();
            fail("Structure should not be instantiable if it has no public member fields");
        }
        catch(IllegalArgumentException e) {
            // expected - ignored
        }
    }

    public void testStructureWithOnlyNonPublicMemberFields() {
        class TestStructure extends Structure {
            int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        try {
            new TestStructure();
            fail("Structure should not be instantiable if it has no public member fields");
        }
        catch(Error e) {
            // expected - ignored
        }
    }

    // must be publicly accessible in order to create array elements
    public static class PublicTestStructure extends Structure {
        public static class ByValue extends PublicTestStructure implements Structure.ByValue {
            public ByValue() { }
            public ByValue(Pointer p) { super(p); }
        }
        public static class ByReference extends PublicTestStructure implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer p) { super(p); }
        }

        public static final List<String> FIELDS = createFieldsOrder("x", "y");
        public int x = 1, y = 2;
        public PublicTestStructure() { }
        public PublicTestStructure(Pointer p) { super(p); read(); }
        public static int allocations = 0;
        @Override
        protected void allocateMemory(int size) {
            super.allocateMemory(size);
            ++allocations;
        }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public void testStructureField() {
        class TestStructure extends Structure {
            public PublicTestStructure s1, s2;
            public int after;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("s1", "s2", "after");
            }
        }
        TestStructure s = new TestStructure();
        TestStructure s2 = new TestStructure();
        assertNotNull("Inner structure should be initialized", s.s1);
        assertNotNull("Inner structure should be initialized (cached)", s2.s1);
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

    public void testStructureByValueField() {
        class TestStructure extends Structure {
            public PublicTestStructure.ByValue s1, s2;
            public int after;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("s1", "s2", "after");
            }
        }
        TestStructure s = new TestStructure();
        TestStructure s2 = new TestStructure();
        assertNotNull("Inner structure should be initialized", s.s1);
        assertNotNull("Inner structure should be initialized (cached)", s2.s1);
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

    public static class TestUnion extends Union {
        public int u_int;
        public float u_float;
        public double u_double;
    }
    public void testUnionField() {
        class TestStructure extends Structure {
            public long s_long;
            public TestUnion s_union;
            public int s_int;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("s_long", "s_union", "s_int");
            }
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong structure size", Native.MAX_PADDING == 4 ? 20 : 24, s.size());
        assertEquals("Wrong union size", 8, s.s_union.size());
    }

    public static class NonAllocatingTestStructure extends PublicTestStructure {
        public NonAllocatingTestStructure() { }
        public NonAllocatingTestStructure(Pointer p) { super(p); read(); }
        @Override
        protected void allocateMemory(int size) {
            throw new Error("Memory unexpectedly allocated");
        }
    }

    // TODO: add'l newInstance(Pointer) tests:
    // NOTE: ensure structure-by-value respected (no more flag on newjavastructure)
    // native call (direct mode)
    // getNativeAlignment
    public void testStructureFieldAvoidsSeparateMemoryAllocation() {
        class TestStructure extends Structure {
            public NonAllocatingTestStructure s1;
            public TestStructure() { }
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("s1");
            }
        }
        TestStructure ts = new TestStructure();
        assertNotNull("Inner structure should be initialized", ts.s1);
    }

    public void testPrimitiveArrayField() {
        class TestStructure extends Structure {
            public byte[] buffer = new byte[1024];
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("buffer");
            }
        }
        TestStructure s = new TestStructure();
        TestStructure s2 = new TestStructure();
        assertEquals("Wrong size for structure with nested array", 1024, s.size());
        assertEquals("Wrong size for structure with nested array (cached)", 1024, s2.size());
        assertNotNull("Array should be initialized", s.buffer);
        assertNotNull("Array should be initialized (cached)", s2.buffer);
        s.write();
        s.read();
    }

    public void testStructureArrayField() {
        class TestStructure extends Structure {
            // uninitialized array elements
            public PublicTestStructure[] inner = new PublicTestStructure[2];
            // initialized array elements
            public PublicTestStructure[] inner2 = (PublicTestStructure[])
                new PublicTestStructure().toArray(2);
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("inner", "inner2");
            }
        }
        TestStructure s = new TestStructure();
        int innerSize = new PublicTestStructure().size();
        assertEquals("Wrong size for structure with nested array of struct",
                     s.inner.length * innerSize + s.inner2.length * innerSize,
                     s.size());
        Structure s0 = s.inner2[0];
        Structure s1 = s.inner2[1];

        s.write();
        assertNotNull("Inner array elements should auto-initialize after write", s.inner[0]);
        assertSame("Inner array (2) element 0 reference should not be changed after write", s0, s.inner2[0]);
        assertSame("Inner array (2) element 1 reference should not be changed after write", s1, s.inner2[1]);

        s.inner[0].x = s.inner[1].x = -1;
        s.inner2[0].x = s.inner2[1].x = -1;
        s.read();
        assertEquals("Inner structure array element 0 not properly read",
                     0, s.inner[0].x);
        assertEquals("Inner structure array element 1 not properly read",
                     0, s.inner[1].x);
        // First element (after toArray()) should preserve values from field initializers
        assertEquals("Inner structure array (2) element 0 not properly read",
                     1, s.inner2[0].x);
        // Subsequent elements from toArray() are initialized from first's
        // memory, which is zeroed
        assertEquals("Inner structure array (2) element 1 not properly read",
                     0, s.inner2[1].x);

        assertEquals("Wrong memory for uninitialized nested array",
                     s.getPointer(), s.inner[0].getPointer());
        assertEquals("Wrong memory for initialized nested array",
                     s.getPointer().share(innerSize * s.inner.length),
                     s.inner2[0].getPointer());
    }

    public static class ToArrayTestStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("inner");
        public PublicTestStructure[] inner =
            (PublicTestStructure[])new PublicTestStructure().toArray(2);

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
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
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("buffer");
            }
        }
        Structure s = new TestStructure();
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
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("z", "b", "c", "s", "i", "l", "f", "d", "ba", "ca", "sa", "ia", "la", "fa", "da", "nested");
            }
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
        assertEquals("Wrong float field value after write/read", s.f, 6.0f, 0f);
        assertEquals("Wrong double field value after write/read", s.d, 7.0, 0d);
        assertEquals("Wrong nested struct field value after write/read (x)", s.nested.x, 1);
        assertEquals("Wrong nested struct field value after write/read (y)", s.nested.y, 2);
        for (int i = 0; i < 3; i++) {
            assertEquals("Wrong byte array field value after write/read", s.ba[i], (byte) (8 + i));
            assertEquals("Wrong char array field value after write/read", s.ca[i], (char) (11 + i));
            assertEquals("Wrong short array field value after write/read", s.sa[i], (short) (14 + i));
            assertEquals("Wrong int array field value after write/read", s.ia[i], 17 + i);
            assertEquals("Wrong long array field value after write/read", s.la[i], 23 + i);
            assertEquals("Wrong float array field value after write/read", s.fa[i], (float) 26 + i, 0f);
            assertEquals("Wrong double array field value after write/read", s.da[i], (double) 29 + i, 0d);
        }
        // test constancy of references after read
        int[] ia = s.ia;
        s.read();
        assertTrue("Array field reference should be unchanged", ia == s.ia);
    }

    public void testNativeLongSize() throws Exception {
        class TestStructure extends Structure {
            public NativeLong l;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("l");
            }
        }
        Structure s = new TestStructure();
        assertEquals("Wrong size", NativeLong.SIZE, s.size());
    }

    public void testNativeLongRead() throws Exception {
        class TestStructure extends Structure {
            public int i;
            public NativeLong l;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("i", "l");
            }
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
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("i", "l");
            }
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

    public void testMemoryField() {
        class MemoryFieldStructure extends Structure {
            public Memory m;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("m");
            }
        }
        new MemoryFieldStructure().size();
    }

    public void testDisallowFunctionPointerAsField() {
        class DisallowFunctionPointer extends Structure {
            public Function cb;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("cb");
            }
        }
        try {
            new DisallowFunctionPointer().size();
            fail("Function fields should not be allowed");
        }
        catch(IllegalArgumentException e) {
            // expected - ignored
        }
    }

    public static class BadFieldStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("badField");
        public Object badField;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public void testUnsupportedField() {
        class BadNestedStructure extends Structure {
            public BadFieldStructure badStruct = new BadFieldStructure();
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("badStruct");
            }
        }
        try {
            new BadFieldStructure();
            fail("Should throw IllegalArgumentException on bad field");
        }
        catch(IllegalArgumentException e) {
            assertTrue("Exception should include field name: " + e.getMessage(), e.getMessage().indexOf("badField") != -1);
        }
        try {
            new BadNestedStructure();
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
        final int allocated[] = { 0 };
        PublicTestStructure.allocations = 0;
        PublicTestStructure s = new PublicTestStructure();
        PublicTestStructure[] array = (PublicTestStructure[])s.toArray(1);
        assertEquals("Array should consist of a single element",
                     1, array.length);
        assertEquals("First element should be original", s, array[0]);

        array = (PublicTestStructure[])s.toArray(2);
        assertEquals("Structure memory should be expanded", 2, array.length);
        assertEquals("No memory should be allocated for new element", 1, PublicTestStructure.allocations);
        assertEquals("Structure.read called on New element", 0, array[1].x);
    }

    public void testByReferenceArraySync() {
        PublicTestStructure.ByReference s = new PublicTestStructure.ByReference();
        PublicTestStructure.ByReference[] array =
            (PublicTestStructure.ByReference[])s.toArray(2);
        class TestStructure extends Structure {
            public PublicTestStructure.ByReference ref;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("ref");
            }
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

    public static class CbStruct extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("cb");
        public Callback cb;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
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
        Map<Callback, CallbackReference> refs = CallbackReference.callbackMap;
        assertTrue("Callback not cached", refs.containsKey(s.cb));
        CallbackReference ref = refs.get(s.cb);
        assertEquals("Wrong trampoline", ref.getTrampoline(), func);
    }

    public void testUninitializedArrayField() {
        class UninitializedArrayFieldStructure extends Structure {
            public byte[] array;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("array");
            }
        }
        try {
            Structure s = new UninitializedArrayFieldStructure();
            assertTrue("Invalid size: " + s.size(), s.size() > 0);
            fail("Uninitialized array field should cause write failure");
        } catch(IllegalStateException e) {
            // expected
        }
    }

    public static class StructureWithArrayOfStructureField extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("array");
        public Structure[] array;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public void testPlainStructureArrayField() {
        try {
            new StructureWithArrayOfStructureField();
            fail("Structure[] should not be allowed as a field of Structure");
        }
        catch(IllegalArgumentException e) {
            // expected - ignored
        }
        catch(Exception e) {
            fail("Wrong exception thrown when Structure[] field encountered in a Structure: " + e);
        }
    }

    public static class ArrayOfPointerStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("array");
        final static int SIZE = 10;
        public Pointer[] array = new Pointer[SIZE];

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public void testPointerArrayField() {
        ArrayOfPointerStructure s = new ArrayOfPointerStructure();
        int size = s.size();
        assertEquals("Wrong size", ArrayOfPointerStructure.SIZE * Native.POINTER_SIZE, size);
        s.array[0] = s.getPointer();
        s.write();
        s.array[0] = null;
        s.read();
        assertEquals("Wrong first element", s.getPointer(), s.array[0]);
    }

    public static class VolatileStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("counter", "value");
        public volatile int counter;
        public int value;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
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

        s.equals(s);
        assertEquals("Structure equals should leave volatile field unchanged", 1, s.getPointer().getInt(0));

        s.hashCode();
        assertEquals("Structure equals should leave volatile field unchanged", 1, s.getPointer().getInt(0));
    }

    public static class StructureWithPointers extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("s1", "s2");
        public PublicTestStructure.ByReference s1;
        public PublicTestStructure.ByReference s2;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public void testStructureByReferenceField() {
        StructureWithPointers s = new StructureWithPointers();
        assertEquals("Wrong size for structure with structure references", Native.POINTER_SIZE * 2, s.size());
        assertNull("Initial refs should be null", s.s1);
    }

    public void testRegenerateStructureByReferenceField() {
        StructureWithPointers s = new StructureWithPointers();
        PublicTestStructure.ByReference inner = new PublicTestStructure.ByReference();
        PublicTestStructure.allocations = 0;
        s.s1 = inner;
        s.write();
        s.s1 = null;
        s.read();
        assertEquals("Inner structure not regenerated on read", inner, s.s1);
        assertEquals("Inner structure should not allocate memory", 0, PublicTestStructure.allocations);
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
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("p", "p2");
            }
        }
        TestStructure s = new TestStructure();
        final Pointer p = s.p;
        final TestPointer p2 = s.p2;
        s.write();
        s.read();
        assertSame("Should preserve Pointer references if peer unchanged", p, s.p);
        assertSame("Should preserve PointerType references if peer unchanged", p2, s.p2);
    }

    public void testPreserveStringFields() {
        final String VALUE = getName();
        final WString WVALUE = new WString(getName() + UNICODE);
        class TestStructure extends Structure {
            public String s;
            public WString ws;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("s", "ws");
            }
        }
        TestStructure s = new TestStructure();
        Memory m = new Memory(VALUE.length()+1);
        m.setString(0, VALUE);
        Memory m2 = new Memory((WVALUE.length()+1)*Native.WCHAR_SIZE);
        m2.setString(0, WVALUE);

        s.getPointer().setPointer(0, m);
        s.getPointer().setPointer(Native.POINTER_SIZE, m2);
        s.read();
        assertEquals("Wrong String field value", VALUE, s.s);
        assertEquals("Wrong WString field value", WVALUE, s.ws);

        s.write();
        assertEquals("String field should not be overwritten: " + s, m, s.getPointer().getPointer(0));
        assertEquals("WString field should not be overwritten: " + s, m2, s.getPointer().getPointer(Native.POINTER_SIZE));
    }

    // Ensure string cacheing doesn't interfere with wrapped structure writes.
    public static class StructureFromPointer extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("s", "ws");
        public String s;
        public WString ws;
        public StructureFromPointer(Pointer p) {
            super(p);
            read();
        }
        public StructureFromPointer() {
            super();
        }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public void testInitializeStructureFieldWithStrings() {
        class ContainingStructure extends Structure {
            public StructureFromPointer inner;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("inner");
            }
        }
        StructureFromPointer o = new StructureFromPointer();
        Charset charset = Charset.forName(o.getStringEncoding());
        final String VALUE = getName() + charset.decode(charset.encode(UNICODE));
        final WString WVALUE = new WString(VALUE);
        o.s = VALUE;
        o.ws = WVALUE;
        o.write();
        StructureFromPointer t = new StructureFromPointer(o.getPointer());
        assertEquals("String field not initialized", VALUE, t.s);
        assertEquals("WString field not initialized", WVALUE, t.ws);

        ContainingStructure outer = new ContainingStructure();
        outer.inner = t;
        outer.write();
        assertEquals("Inner String field corrupted", VALUE, outer.inner.s);
        assertEquals("Inner WString field corrupted", WVALUE, outer.inner.ws);
        outer.inner.read();
        assertEquals("Native memory behind Inner String field not updated", VALUE, outer.inner.s);
        assertEquals("Native memory behind Inner WString field not updated", WVALUE, outer.inner.ws);
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
        assertEquals("Structure.ByReference field not written automatically",
                     -1, s.s1.getPointer().getInt(0));
    }

    public void testStructureByReferenceArrayField() {
        class TestStructure extends Structure {
            public PublicTestStructure.ByReference[] array = new PublicTestStructure.ByReference[2];
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("array");
            }
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong structure size", 2*Native.POINTER_SIZE, s.size());

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
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
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

    public static class NestedTypeInfoStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("inner", "dummy");
        public static class Inner extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("dummy");
            public int dummy;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }
        public Inner inner;
        public int dummy;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public static class size_t extends IntegerType {
        private static final long serialVersionUID = 1L;
        public size_t() { this(0); }
        public size_t(long value) { super(Native.POINTER_SIZE, value); }
    }
    /** Same structure as the internal representation, to be initialized from
     * the internal FFIType native data.
     */
    public static class TestFFIType extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("size", "alignment", "type", "elements");
        // NOTE: this field is not normally initialized by libffi
        // We force initialize by calling initialize_ffi_type
        public size_t size;
        public short alignment;
        public short type;
        public Pointer elements;

        public TestFFIType(Pointer p) {
            super(p);
            read();
            assertTrue("Test FFIType type not initialized: " + this, this.type != 0);

            // Force libffi to explicitly calculate the size field of
            // this FFIType object
            int size = Native.initialize_ffi_type(p.peer);
            read();
            assertEquals("Test FFIType size improperly initialized: " + TestFFIType.this, size, TestFFIType.this.size.intValue());
        }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public void testNestedStructureTypeInfo() {
        NestedTypeInfoStructure s = new NestedTypeInfoStructure();
        Pointer p = s.getTypeInfo();
        assertNotNull("Type info should not be null", p);
        TestFFIType ffi_type = new TestFFIType(p);
        assertEquals("FFIType size mismatch: " + ffi_type, s.size(), ffi_type.size.intValue());
        Pointer els = ffi_type.elements;
        Pointer inner = s.inner.getTypeInfo();
        assertEquals("Wrong type information for 'inner' field",
                     inner, els.getPointer(0));
        assertEquals("Wrong type information for integer field",
                     Structure.getTypeInfo(Integer.valueOf(0)),
                     els.getPointer(Native.POINTER_SIZE));
        assertNull("Type element list should be null-terminated",
                   els.getPointer(Native.POINTER_SIZE*2));
    }

    public void testInnerArrayTypeInfo() {
        class TestStructure extends Structure {
            public int[] inner = new int[5];
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("inner");
            }
        }
        Structure s = new TestStructure();
        Pointer p = s.getTypeInfo();
        assertNotNull("Type info should not be null", p);
        TestFFIType ffi_type = new TestFFIType(p);
        assertEquals("Wrong structure size", 20, s.size());
        assertEquals("FFIType info size mismatch", s.size(), ffi_type.size.intValue());
    }

    public void testTypeInfoForNull() {
        assertEquals("Wrong type information for 'null'",
                     Structure.getTypeInfo(new Pointer(0)),
                     Structure.getTypeInfo(null));
    }

    public void testToString() {
        // wce missing String.matches() and regex support
        if (Platform.isWindowsCE()) return;

        class TestStructure extends Structure {
            public int intField;
            public PublicTestStructure inner;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("intField", "inner");
            }
        }
        TestStructure s = new TestStructure();
        final String LS = System.getProperty("line.separator");
        System.setProperty("jna.dump_memory", "true");
        final String EXPECTED = "(?m).*" + s.size() + " bytes.*\\{" + LS
            + "  int intField@0x0=0x0000" + LS
            + "  .* inner@0x4=.*\\{" + LS
            + "    int x@0x0=.*" + LS
            + "    int y@0x4=.*" + LS
            + "  \\}" + LS
            + "\\}" + LS
            + "memory dump" + LS
            + "\\[[0-9a-f]+\\]" + LS
            + "\\[[0-9a-f]+\\]" + LS
            + "\\[[0-9a-f]+\\]";
        String actual = s.toString();
        assertTrue("Improperly formatted toString(): expected "
                   + EXPECTED + "\n" + actual,
                   actual.matches(EXPECTED));

        System.setProperty("jna.dump_memory", "false");
        assertFalse("Doesn't dump memory when jna.dump_memory is false",
                   s.toString().contains("memory dump"));
    }

    public void testNativeMappedWrite() {
        class TestStructure extends Structure {
            public ByteByReference ref;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("ref");
            }
        }
        TestStructure s = new TestStructure();
        ByteByReference ref = s.ref = new ByteByReference();
        s.write();
        assertEquals("Value not properly written", ref.getPointer(), s.getPointer().getPointer(0));

        s.ref = null;
        s.write();
        assertNull("Non-null value was written: " + s.getPointer().getPointer(0), s.getPointer().getPointer(0));
    }

    public void testNativeMappedRead() {
        class TestStructure extends Structure {
            public ByteByReference ref;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("ref");
            }
        }
        TestStructure s = new TestStructure();
        s.read();
        assertNull("Should read null for initial field value", s.ref);

        ByteByReference ref = new ByteByReference();
        s.getPointer().setPointer(0, ref.getPointer());
        s.read();
        assertEquals("Field incorrectly read", ref, s.ref);

        s.getPointer().setPointer(0, null);
        s.read();
        assertNull("Null field incorrectly read", s.ref);
    }

    public static class ROStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("field");
        public final int field;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        {
            // Initialize in ctor to avoid compiler replacing
            // field references with a constant everywhere
            field = 0;
        }
    }
    private ROStructure avoidConstantFieldOptimization(ROStructure s) {
        return s;
    }

    public void testReadOnlyField() {
        if (!Platform.RO_FIELDS) {
            try {
                new ROStructure();
                fail("Creation of a Structure with final fields should fail");
            }
            catch(Exception e) {
            }
            return;
        }

        ROStructure s = new ROStructure();
        s.getPointer().setInt(0, 42);
        s.read();
        s = avoidConstantFieldOptimization(s);
        assertEquals("Field value should be set from native", 42, s.field);

        s.getPointer().setInt(0, 0);
        s.read();
        s = avoidConstantFieldOptimization(s);
        assertEquals("Field value not synched after native change", 0, s.field);

        // Read-only fields  should not be copied to native memory
        s.getPointer().setInt(0, 42);
        try { s.write(); } catch(UnsupportedOperationException e) { }
        assertEquals("Field should not be written", 42, s.getPointer().getInt(0));

        // Native changes should propagate to read-only fields
        s.read();
        s = avoidConstantFieldOptimization(s);
        assertEquals("Field value not synched after native change (2)", 42, s.field);

    }
    public void testNativeMappedArrayField() {
        final int SIZE = 24;
        class TestStructure extends Structure {
            public NativeLong[] longs = new NativeLong[SIZE];
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("longs");
            }
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
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("nl", "uninitialized");
            }
        }
        TestStructure ts = new TestStructure();
        TestStructure ts2 = new TestStructure();
        assertEquals("Wrong value in field", VALUE, ts.nl.longValue());
        assertSame("Initial value overwritten", INITIAL, ts.nl);
        assertEquals("Wrong field value before write", VALUE, ts.nl.longValue());
        assertNotNull("Uninitialized field should be initialized", ts.uninitialized);
        assertNotNull("Uninitialized field should be initialized (cached)", ts2.uninitialized);
        assertEquals("Wrong initialized value", 0, ts.uninitialized.longValue());
        ts.write();
        assertEquals("Wrong field value written", VALUE, ts.getPointer().getNativeLong(0).longValue());
        assertEquals("Wrong field value written (2)", 0, ts.getPointer().getNativeLong(NativeLong.SIZE).longValue());
        ts.read();
        assertEquals("Wrong field value", VALUE, ts.nl.longValue());
        assertEquals("Wrong field value (2)", 0, ts.uninitialized.longValue());
    }

    public void testThrowErrorOnMissingFieldOrderOnDerivedStructure() {
        class TestStructure extends Structure {
            public int f1, f2;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("f1", "f2");
            }
        }
        class NoFieldOrder extends TestStructure {
            public int f3;
        }
        try {
            new NoFieldOrder();
            fail("Expected an error when structure fails to provide field order");
        }
        catch(Error e) {
        }
    }

    public void testThrowErrorOnIncorrectFieldOrderNameMismatch() {
        class TestStructure extends Structure {
            public int f1, f2;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("F1", "F2");
            }
        }
        try {
            new TestStructure();
            fail("Expected an error when creating a structure without mismatched field names");
        }
        catch(Error e) {
        }
    }

    public void testThrowErrorOnIncorrectFieldOrderCount() {
        class TestStructure extends Structure {
            public int f1, f2;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("f1", "f2", "f3");
            }
        }
        try {
            new TestStructure();
            fail("Expected an error when creating a structure with wrong number of fiels in getFieldOrder()");
        }
        catch(Error e) {
            // expected - ignored
        }
    }

    public static class XTestStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("first");
        public int first = 1;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class XTestStructureSub extends XTestStructure {
        public static final List<String> EXTRA_FIELDS = createFieldsOrder("second");
        private static final AtomicReference<List<String>> fieldsHolder = new AtomicReference<List<String>>(null);
        private static List<String> resolveEffectiveFields(List<String> baseFields) {
            List<String> fields;
            synchronized (fieldsHolder) {
                fields = fieldsHolder.get();
                if (fields == null) {
                    fields = createFieldsOrder(baseFields, EXTRA_FIELDS);
                    fieldsHolder.set(fields);
                }
            }

            return fields;
        }
        public int second = 2;

        @Override
        protected List<String> getFieldOrder() {
            return resolveEffectiveFields(super.getFieldOrder());
        }
    }

    public void testInheritedStructureFieldOrder() {
        XTestStructureSub s = new XTestStructureSub();
        assertEquals("Wrong size", 8, s.size());
        s.write();
        assertEquals("Wrong first field: " + s, s.first, s.getPointer().getInt(0));
        assertEquals("Wrong second field: " + s, s.second, s.getPointer().getInt(4));
    }

    public void testVariedStructureFieldOrder() {
        final String[] ORDER = new String[] { "one", "two", "three" };
        final String[] ORDER2 = new String[] { "one", "two", "three", "four", "five" };
        class TestStructure extends Structure {
            public int one = 1;
            public int three = 3;
            public int two = 2;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(ORDER);
            }
        }
        class DerivedTestStructure extends TestStructure {
            public int five = 5;
            public int four = 4;
            @Override
            protected List<String> getFieldOrder() {
                List<String> list = new ArrayList<String>(super.getFieldOrder());
                list.addAll(Arrays.asList("four", "five"));
                return list;
            }
        }

        TestStructure s = new TestStructure();
        assertEquals("Wrong field order", Arrays.asList(ORDER), s.getFieldOrder());
        s.write();
        assertEquals("Wrong first field: " + s, s.one, s.getPointer().getInt(0));
        assertEquals("Wrong second field: " + s, s.two, s.getPointer().getInt(4));
        assertEquals("Wrong third field: " + s, s.three, s.getPointer().getInt(8));

        DerivedTestStructure s2 = new DerivedTestStructure();
        assertEquals("Wrong field order", Arrays.asList(ORDER2), s2.getFieldOrder());
        s2.write();
        assertEquals("Wrong first field: " + s2, s2.one, s2.getPointer().getInt(0));
        assertEquals("Wrong second field: " + s2, s2.two, s2.getPointer().getInt(4));
        assertEquals("Wrong third field: " + s2, s2.three, s2.getPointer().getInt(8));
        assertEquals("Wrong derived field (1): " + s2, s2.four, s2.getPointer().getInt(12));
        assertEquals("Wrong derived field (2): " + s2, s2.five, s2.getPointer().getInt(16));
    }

    public void testCustomTypeMapper() {
        class TestField { }
        final DefaultTypeMapper mapper = new DefaultTypeMapper() {
            {
                addTypeConverter(TestField.class, new TypeConverter() {
                    @Override
                    public Object fromNative(Object value, FromNativeContext context) {
                        return new TestField();
                    }
                    @Override
                    public Class<?> nativeType() {
                        return String.class;
                    }
                    @Override
                    public Object toNative(Object value, ToNativeContext ctx) {
                        return value == null ? null : value.toString();
                    }
                });
            }
        };
        class TestStructure extends Structure {
            public TestField field;
            public TestStructure() {
                super(mapper);
            }
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        Structure s = new TestStructure();
        assertEquals("Wrong type mapper: " + s, mapper, s.getTypeMapper());
    }

    public void testWriteWithNullBoxedPrimitives() {
        class TestStructure extends Structure {
            public Boolean zfield;
            public Integer field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("zfield", "field");
            }
        }
        TestStructure s = new TestStructure();
        s.write();
        s.read();
        assertNotNull("Field should not be null after read", s.field);
    }

    public void testStructureEquals() {
        class OtherStructure extends Structure {
            public int first;
            public int[] second = new int[4];
            public Pointer[] third = new Pointer[4];
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("first", "second", "third");
            }
        }
        class TestStructure extends Structure {
            public int first;
            public int[] second = new int[4];
            public Pointer[] third = new Pointer[4];
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("first", "second", "third");
            }
            public TestStructure() { }
            public TestStructure(Pointer p) { super(p); }
        }
        OtherStructure s0 = new OtherStructure();
        TestStructure s1 = new TestStructure();
        TestStructure s2 = new TestStructure(s1.getPointer());
        TestStructure s3 = new TestStructure(s2.getPointer());
        TestStructure s4 = new TestStructure();

        assertFalse("Structures of different classes with same fields are not equal", s1.equals(s0));
        assertFalse("Structures of different classes with same fields are not equal (reflexive)", s0.equals(s1));

        assertFalse("Compare to null failed", s1.equals(null));
        assertTrue("Equals is not reflexive", s1.equals(s1));
        assertTrue("Equals failed on structures with same pointer", s1.equals(s2));
        assertTrue("Equals is not symmetric", s2.equals(s1));
        assertTrue("Equals is not transitive", s1.equals(s2) && s2.equals(s3) && s1.equals(s3));
        assertFalse("Compare to different structure failed", s1.equals(s4));
    }

    public void testStructureHashCodeMatchesWhenEqual() {
        class TestStructure extends Structure {
            public int first;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("first");
            }
        }
        TestStructure s1 = new TestStructure();
        TestStructure s2 = new TestStructure();
        assertFalse("hashCode should be different for two different structures", s1.hashCode() == s2.hashCode());

        s2.useMemory(s1.getPointer());
        assertEquals("hashCode should match when structures have same pointer", s1.hashCode(), s2.hashCode());

        s2.useMemory(s1.getPointer());
        assertEquals("hashCode should match when structures have same pointer", s1.hashCode(), s2.hashCode());
    }

    public void testRecursiveWrite() {
        class TestStructureByRef extends Structure implements Structure.ByReference{
            public TestStructureByRef(Pointer p) { super(p); }
            public TestStructureByRef() { }
            public int unique;
            public TestStructureByRef s;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("unique", "s");
            }
        }
        TestStructureByRef s = new TestStructureByRef();
        s.s = new TestStructureByRef();
        s.unique = 1;
        s.s.s = s;
        s.s.unique = 2;

        s.write();
        assertEquals("Structure field contents not written", 1, s.getPointer().getInt(0));
        assertEquals("ByReference structure field contents not written", 2, s.s.getPointer().getInt(0));

        s.s.unique = 0;
        Structure value = s.s;
        s.read();
        assertEquals("ByReference structure field not preserved", value, s.s);
        assertEquals("ByReference structure field contents not read", 2, s.s.unique);
        assertTrue("Temporary storage should be cleared", Structure.busy().isEmpty());
    }

    public static class CyclicTestStructure extends Structure {
        public static class ByReference extends CyclicTestStructure implements Structure.ByReference {}
        public static final List<String> FIELDS = createFieldsOrder("next");
        public CyclicTestStructure(Pointer p) { super(p); }
        public CyclicTestStructure() { }
        public CyclicTestStructure.ByReference next;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    public void testCyclicRead() {
        CyclicTestStructure s = new CyclicTestStructure();
        s.next = new CyclicTestStructure.ByReference();

        Structure value = s.next;
        s.next.next = s.next;
        s.write();
        s.read();
        assertEquals("ByReference structure field not preserved", value, s.next);

        value = s.next;
        s.next.next = null;
        s.read();
        assertSame("ByReference structure field should reuse existing value", value, s.next);
        assertSame("Nested ByReference structure field should reuse existing value", value, s.next.next);
    }

    public void testAvoidMemoryAllocationInPointerCTOR() {
        class TestStructure extends Structure {
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
            public TestStructure(Pointer p) {
                super(p);
            }
            @Override
            protected Memory autoAllocate(int size) {
                fail("Memory should not be auto-allocated");
                return null;
            }
        }
        Memory p = new Memory(4);
        Structure s = new TestStructure(p);
    }

    public void testPointerCTORWithInitializedFields() {
        class TestStructure extends Structure {
            public int intField;
            public byte[] arrayField = new byte[256];
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("intField", "arrayField");
            }
            public TestStructure(Pointer p) {
                super(p);
                read(); // Important!
            }
        }
        Memory p = new Memory(260);
        p.setInt(0, 1);
        p.setByte(4, (byte)2);
        p.setByte(5, (byte)3);
        p.setByte(6, (byte)4);
        p.setByte(7, (byte)5);
        TestStructure s = new TestStructure(p);

        assertEquals("Structure primitive field not initialized",
                     (byte)1, s.intField);
        assertEquals("Structure primitive array field not initialized",
                     (byte)2, s.arrayField[0]);
        assertEquals("Structure primitive array field not initialized",
                     (byte)3, s.arrayField[1]);
        assertEquals("Structure primitive array field not initialized",
                     (byte)4, s.arrayField[2]);
        assertEquals("Structure primitive array field not initialized",
                     (byte)5, s.arrayField[3]);
        assertEquals("Wrong structure size", p.size(), s.size());
    }


    public static class TestByReferenceArrayField extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("value1", "array", "value2");
        public TestByReferenceArrayField() { }
        public TestByReferenceArrayField(Pointer m) {
            super(m);
            read(); // Important!
        }
        public int value1;
        public ByReference[] array = new ByReference[13];
        public int value2;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public static class ByReference extends TestByReferenceArrayField implements Structure.ByReference { }
    }

    public static void testByReferenceArrayField() {
        TestByReferenceArrayField.ByReference s = new TestByReferenceArrayField.ByReference();
        s.value1 = 22;
        s.array[0] = s;
        s.value2 = 42;
        s.write();

        TestByReferenceArrayField s2 =
            new TestByReferenceArrayField(s.getPointer());
        assertEquals("value1 not properly read from Pointer", s.value1, s2.value1);
        assertNotNull("Structure.ByReference array field was not initialized", s2.array);
        assertEquals("Structure.ByReference array field initialized to incorrect length", 13, s2.array.length);
        assertNotNull("Structure.ByReference array field element was not initialized", s2.array[0]);
        assertEquals("Incorrect value for Structure.ByReference array field element", s.array[0].getPointer(), s2.array[0].getPointer());
        assertEquals("Field 'value2' not properly read from Pointer", s.value2, s2.value2);
    }

    public void testEquals() {
        class TestStructure extends Structure {
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
            public TestStructure() { }
            public TestStructure(Pointer p) { super(p); read(); }
        }
        Structure s = new TestStructure();
        assertTrue("Should match self", s.equals(s));
        assertFalse("Not equal null", s.equals(null));
        assertFalse("Not equal some other object", s.equals(new Object()));
        Structure s1 = new TestStructure(s.getPointer());
        assertEquals("Same base address/type should be equal", s, s1);
    }

    public void testStructureLayoutCacheing() {
        class TestStructure extends Structure {
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        Structure ts = new TestStructure(); ts.ensureAllocated();
        Structure ts2 = new TestStructure(); ts2.ensureAllocated();

        assertSame("Structure layout not cached", ts.fields(), ts2.fields());
    }

    public void testStructureLayoutVariableNoCache() {
        class TestStructure extends Structure {
            public byte[] variable;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("variable");
            }
            public TestStructure(int size) {
                this.variable = new byte[size];
            }
        }
        Structure ts = new TestStructure(8);
        Structure ts2 = new TestStructure(16);

        // Ensure allocated; primitive array prevents initial layout calculation
        ts.ensureAllocated(); ts2.ensureAllocated();
        assertNotSame("Structure layout should not be cached", ts.fields(), ts2.fields());
    }

    public void testStructureLayoutCacheingWithTypeMapper() {
        class TestTypeMapper extends DefaultTypeMapper {
            {
                TypeConverter tc = new TypeConverter() {
                    @Override
                    public Class<?> nativeType() { return int.class; }
                    @Override
                    public Object fromNative(Object nativeValue, FromNativeContext c) {
                        return Boolean.valueOf(nativeValue.equals(Integer.valueOf(0)));
                    }
                    @Override
                    public Object toNative(Object value, ToNativeContext c) {
                        return Integer.valueOf(Boolean.TRUE.equals(value) ? -1 : 0);
                    }
                };
                addTypeConverter(boolean.class, tc);
                addTypeConverter(Boolean.class, tc);
            }
        }
        final TestTypeMapper m = new TestTypeMapper();
        class TestStructure extends Structure {
            public boolean field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
            public TestStructure() {
                super(m);
            }
            public TestStructure(TypeMapper m) {
                super(m);
            }
        }
        Structure ts = new TestStructure();
        Structure ts2 = new TestStructure();
        Structure ts3 = new TestStructure(m);
        assertSame("Structure layout should be cached with custom type mapper", ts.fields(), ts2.fields());
        assertSame("Structure layout should be cached with custom type mapper, regardless of constructor type", ts.fields(), ts3.fields());
    }

    public void testStructureLayoutCacheingWithAlignment() {
        class TestStructure extends Structure {
            public byte first;
            public int second;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("first", "second");
            }
            public TestStructure() {
                setAlignType(ALIGN_NONE);
            }
            public TestStructure(int alignType) {
                super(alignType);
            }
        }
        Structure ts = new TestStructure();
        Structure ts2 = new TestStructure();
        Structure ts3 = new TestStructure(Structure.ALIGN_NONE);

        assertSame("Structure layout should be cached with custom alignment", ts.fields(), ts2.fields());
        assertSame("Structure layout should be cached with custom alignment, regardless of how set", ts.fields(), ts3.fields());
    }

    public void testStructureSetIterator() {
        assertNotNull("Indirect test of StructureSet.Iterator",
                      Structure.busy().toString());
    }

    public void testFFITypeCalculationWithTypeMappedFields() {
        final TypeMapper mapper = new TypeMapper() {
            @Override
            public FromNativeConverter getFromNativeConverter(Class<?> cls) {
                if (Boolean.class.equals(cls)
                    || boolean.class.equals(cls)) {
                    return new FromNativeConverter() {
                        @Override
                        public Class<?> nativeType() {
                            return byte.class;
                        }
                        @Override
                        public Object fromNative(Object nativeValue, FromNativeContext context) {
                            return nativeValue.equals(Byte.valueOf((byte)0))
                                ? Boolean.FALSE : Boolean.TRUE;
                        }
                    };
                }
                return null;
            }
            @Override
            public ToNativeConverter getToNativeConverter(Class<?> javaType) {
                if (Boolean.class.equals(javaType)
                    || boolean.class.equals(javaType)) {
                    return new ToNativeConverter() {
                        @Override
                        public Object toNative(Object value, ToNativeContext context) {
                            return Byte.valueOf(Boolean.TRUE.equals(value) ? (byte)1 : (byte)0);
                        }
                        @Override
                        public Class<?> nativeType() {
                            return byte.class;
                        }
                    };
                }
                return null;
            }
        };
        class TestStructure extends Structure {
            public boolean b;
            public short s;
            // Ensure we're not stuffed into a register
            public int p0,p1,p2,p3,p4,p5,p6,p7;
            public TestStructure() {
                super(mapper);
            }
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("b", "s", "p0", "p1", "p2", "p3", "p4", "p5", "p6", "p7");
            }
        }
        Structure s = new TestStructure();
        assertEquals("Wrong type mapper for structure", mapper, s.getTypeMapper());

        TestFFIType ffi_type = new TestFFIType(Structure.getTypeInfo(s));
        assertEquals("Java Structure size does not match FFIType size",
                     s.size(), ffi_type.size.intValue());
    }

    public void testDefaultStringEncoding() {
        class TestStructure extends Structure {
            public String field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong default structure encoding",
                     Native.getDefaultStringEncoding(),
                     s.getStringEncoding());
    }

    public void testStringFieldEncoding() throws Exception {
        class TestStructure extends Structure {
            public String field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        TestStructure s = new TestStructure();
        final String ENCODING = "utf8";
        s.setStringEncoding(ENCODING);
        assertEquals("Manual customization of string encoding failed", ENCODING, s.getStringEncoding());

        final String VALUE = "\u0444\u043b\u0441\u0432\u0443";
        s.field = VALUE;
        s.write();
        byte[] expected = VALUE.getBytes("utf8");
        byte[] actual = s.getPointer().getPointer(0).getByteArray(0, expected.length);
        for (int i=0;i < Math.min(expected.length, actual.length);i++) {
            assertEquals("Improperly encoded (" + ENCODING
                         + ") on structure write at " + i,
                         expected[i], actual[i]);
        }
        assertEquals("Encoding length mismatch", expected.length, actual.length);

        s.field = null;
        s.read();
        assertEquals("String not decoded properly on read", VALUE, s.field);
    }

    public void testThreadLocalSetReleasesReferences() {
        class TestStructure extends Structure {
            public String field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }

        TestStructure ts1 = new TestStructure();
        TestStructure ts2 = new TestStructure();

        StructureSet structureSet = (StructureSet) Structure.busy();
        structureSet.add(ts1);
        structureSet.add(ts2);
        structureSet.remove(ts1);
        assertNotNull(structureSet.elements[0]);
        structureSet.remove(ts2);
        assertNull(structureSet.elements[0]);
    }
}
