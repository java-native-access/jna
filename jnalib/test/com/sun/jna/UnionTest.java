/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna;

import junit.framework.TestCase;

public class UnionTest extends TestCase {

    public static class TestStructure extends Structure {
        public String value;
    }
    
    public static class BigTestStructure extends Structure {
        public long field1;
        public long field2;
    }
    
    public static class IntStructure extends Structure {
        public int value;
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
        assertEquals("Union should be size of largest field",
                     new BigTestStructure().size(), u.size());
    }

    public void testFieldOffsets() {
        StructUnion u = new StructUnion();
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
        assertNull("Unselected structure should not be read", u.structField.value);
        assertNull("Unselected String should be null", u.string);
        assertNull("Unselected WString should be null", u.wstring);
    }
    
    public void testWriteTypedUnion() {
        final int VALUE = 0x12345678;
        // write an instance of a direct union class to memory
        StructUnion u = new StructUnion();
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
            public void callback() {
                System.out.println("hi");
            }
        };
        u.setTypedValue(func1);
    }

    public void testReadTypedUnion() {
        StructUnion u = new StructUnion();
        final int VALUE = 0x12345678;
        u.getPointer().setInt(0, VALUE);
        assertEquals("int structure not read properly", VALUE, ((IntStructure) u.getTypedValue(IntStructure.class)).value);
    }

    public void testReadTypeInfo() {
        SizedUnion u = new SizedUnion();
        if (Native.POINTER_SIZE == 4) {
            assertEquals("Type size should be that of longest field if no field active",
                         Structure.getTypeInfo(BigTestStructure.class).getInt(0),
                         u.getTypeInfo().getInt(0));
        }
        else {
            assertEquals("Type size should be that of longest field if no field active",
                         Structure.getTypeInfo(BigTestStructure.class).getLong(0),
                         u.getTypeInfo().getLong(0));
        }
        u.setType(int.class);
        if (Native.POINTER_SIZE == 4) {
            assertEquals("Type size should be that of longest field if field active",
                         Structure.getTypeInfo(BigTestStructure.class).getInt(0),
                         u.getTypeInfo().getInt(0));
        }
        else {
            assertEquals("Type size should be that of longest field if field active",
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
        u.setType(byte[].class);
        u.setType(short[].class);
        u.setType(int[].class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(UnionTest.class);
    }
}
