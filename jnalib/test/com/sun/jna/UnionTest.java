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
    
    public static class SizedUnion extends Union {
        public byte byteField;
        public short shortField;
        public int intField;
        public long longField;
        public TestStructure structField;
        public String string;
        public WString wstring;
        public Pointer pointer;
    }
    
    public void testCalculateSize() {
        Union u = new SizedUnion();
        assertEquals("Union should be size of largest field", 8, u.size());
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
    
    public void testReadTypeInfo() {
        SizedUnion u = new SizedUnion();
        assertEquals("Type should be that of longest field if no field active",
                     Structure.getTypeInfo(new Long(0)),
                     u.getTypeInfo());
        u.setType(int.class);
        assertEquals("Type should be that of longest field if field active",
                     Structure.getTypeInfo(new Long(0)),
                     u.getTypeInfo());
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(UnionTest.class);
    }
}
