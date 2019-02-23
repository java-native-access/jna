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

//@SuppressWarnings("unused")
public class UnionTest extends TestCase {

    public static class TestStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("value");
        public String value;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class BigTestStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("field1", "field2");
        public long field1;
        public long field2;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class IntStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("value");
        public int value;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class SubIntStructure extends IntStructure {}

    public static interface Func1 extends Callback {
        public void callback();
    }

    public static class SizedUnion extends Union {
        public byte byteField;
        public short shortField;
        public int intField;
        public long longField;
        public TestStructure structField;
        public BigTestStructure structField2;
        public String string;
        public WString wstring;
        public Pointer pointer;
    }

    public static class StructUnion extends Union {
        public int intField;
        public TestStructure testStruct;
        public IntStructure intStruct;
        public Func1 func1;
    }

    public void testCalculateSize() {
        Union u = new SizedUnion();
        assertEquals("Wrong union size: " + u, 16, u.size());
        assertEquals("Union should be size of largest field",
                     new BigTestStructure().size(), u.size());
    }

    public void testFieldOffsets() {
        StructUnion u = new StructUnion();
        assertEquals("Wrong union size: " + u, Native.POINTER_SIZE, u.size());
        u.setType(u.testStruct.getClass());
        u.write();
        assertEquals("Wrong struct member base address",
                     u.getPointer(), u.testStruct.getPointer());
        u.setType(u.intStruct.getClass());
        u.write();
        assertEquals("Wrong struct member base address (2)",
                     u.getPointer(), u.intStruct.getPointer());
    }

    public void testWriteUnion() {
        SizedUnion u = new SizedUnion();
        final int VALUE = 0x12345678;
        u.intField = VALUE;
        u.setType(int.class);
        u.write();
        assertEquals("Wrong value written", VALUE, u.getPointer().getInt(0));
    }

    public void testReadUnion() {
        SizedUnion u = new SizedUnion();
        final int VALUE = 0x12345678;
        u.getPointer().setInt(0, VALUE);
        u.read();
        assertEquals("int field not read properly", VALUE, u.intField);
        assertTrue("byte field not read", u.byteField != 0);
        assertTrue("short field not read", u.shortField != 0);
        assertTrue("long field not read", u.longField != 0);
        assertNotNull("Unselected Pointer not read", u.pointer);
        assertNotNull("Union struct field should be initialized", u.structField);
        assertNull("Unselected structure should not be read", u.structField.value);
        assertNull("Unselected String should be null", u.string);
        assertNull("Unselected WString should be null", u.wstring);
    }

    public void testWriteTypedUnion() {
        final int VALUE = 0x12345678;
        // write an instance of a direct union class to memory
        StructUnion u = new StructUnion();
        assertEquals("Wrong union size: " + u, Native.POINTER_SIZE, u.size());
        IntStructure intStruct = new IntStructure();
        intStruct.value = VALUE;
        u.setTypedValue(intStruct);
        u.write();
        assertEquals("Wrong value written", VALUE, u.getPointer().getInt(0));
        // write an instance of a sub class of an union class to memory
        u = new StructUnion();
        SubIntStructure subIntStructure = new SubIntStructure();
        subIntStructure.value = VALUE;
        u.setTypedValue(subIntStructure);
        u.write();
        assertEquals("Wrong value written", VALUE, u.getPointer().getInt(0));
        // write an instance of an interface
        u = new StructUnion();
        Func1 func1 = new Func1() {
            @Override
            public void callback() {
                System.out.println("hi");
            }
        };
        u.setTypedValue(func1);
    }

    public void testReadTypedUnion() {
        StructUnion u = new StructUnion();
        assertEquals("Wrong union size: " + u, Native.POINTER_SIZE, u.size());
        final int VALUE = 0x12345678;
        u.getPointer().setInt(0, VALUE);
        assertEquals("int structure not read properly", VALUE, ((IntStructure) u.getTypedValue(IntStructure.class)).value);
    }

    public void testReadTypeInfo() {
        SizedUnion u = new SizedUnion();
        assertEquals("Wrong union size: " + u, 16, u.size());
        assertNotNull("Type information is missing for union field of type " + BigTestStructure.class, Structure.getTypeInfo(BigTestStructure.class));
        assertNotNull("Type information is missing for union instance", u.getTypeInfo());
        if (Native.POINTER_SIZE == 4) {
            assertEquals("Type size should be that of largest field if no field is active",
                         Structure.getTypeInfo(BigTestStructure.class).getInt(0),
                         u.getTypeInfo().getInt(0));
        }
        else {
            assertEquals("Type size should be that of largest field if no field is active",
                         Structure.getTypeInfo(BigTestStructure.class).getLong(0),
                         u.getTypeInfo().getLong(0));
        }
        u.setType(int.class);
        assertNotNull("Type information is missing for union field of type " + BigTestStructure.class, Structure.getTypeInfo(BigTestStructure.class));
        assertNotNull("Type information is missing for union instance after type set", u.getTypeInfo());
        if (Native.POINTER_SIZE == 4) {
            assertEquals("Type size should be that of largest field if any field is active",
                         Structure.getTypeInfo(BigTestStructure.class).getInt(0),
                         u.getTypeInfo().getInt(0));
        }
        else {
            assertEquals("Type size should be that of largest field if any field is active",
                         Structure.getTypeInfo(BigTestStructure.class).getLong(0),
                         u.getTypeInfo().getLong(0));
        }
    }

    public void testArraysInUnion() {
        class TestUnion extends Union {
            public byte[] bytes = new byte[16];
            public short[] shorts = new short[8];
            public int[] ints = new int[4];
        }
        Union u = new TestUnion();
        assertEquals("Wrong union size: " + u, 16, u.size());
        u.setType(byte[].class);
        u.setType(short[].class);
        u.setType(int[].class);
    }

    public void testDuplicateFieldTypes() {
        class TestUnion extends Union {
            public int field1;
            public int field2;
        }
        TestUnion u = new TestUnion();
        assertEquals("Wrong union size: " + u, 4, u.size());
        u.setType("field1");
        u.field1 = 42;
        u.write();
        u.setType("field2");
        u.read();
        assertEquals("Wrong field value after write/read", 42, u.field2);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UnionTest.class);
    }
}
