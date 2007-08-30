/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

import com.sun.jna.VarArgsTest.TestLibrary.TestStructure;
import junit.framework.TestCase;

public class VarArgsTest extends TestCase {
    final int MAGIC32 = 0x12345678;
    public static interface TestLibrary extends Library {
        public static class TestStructure extends Structure {
            public int magic = 0;
        }
        public int addInt32VarArgs(String fmt, Number... args);
        public String returnStringVarArgs(String fmt, Object... args);
        public void modifyStructureVarArgs(String fmt, Object arg1, Object... args);
    }
    TestLibrary lib;
    protected void setUp() {
        lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
    }
    protected void tearDown() {
        lib = null;
    }   
    public void testIntVarArgs() {
        int arg1 = 1;
        int arg2 = 2;
        assertEquals("VarArgs not added correctly", arg1 + arg2,
                     lib.addInt32VarArgs("dd", new Integer(arg1), new Integer(arg2)));
    }
    public void testShortVarArgs() {
        short arg1 = 1;
        short arg2 = 2;
        assertEquals("VarArgs not added correctly", arg1 + arg2,
                     lib.addInt32VarArgs("dd", new Short(arg1), new Short(arg2)));
    }
    public void testLongVarArgs() {
        short arg1 = 1;
        short arg2 = 2;
        assertEquals("VarArgs not added correctly", arg1 + arg2,
                     lib.addInt32VarArgs("ll", new Long(arg1), new Long(arg2)));
    }
    public void testStringVarArgs() {
        Object[] args = new Object[] { "Test" };
        assertEquals("Did not return correct string", args[0],
                     lib.returnStringVarArgs("", args));
    }
    
    public void testAppendNullToVarargs() {
        Number[] args = new Number[] { new Integer(1) };
        assertEquals("No trailing NULL was appended to varargs list",
                     1, lib.addInt32VarArgs("dd", args));
    }
    
    public void testModifyStructureInVarargs() {
        TestStructure arg1 = new TestStructure();
        TestStructure[] varargs = new TestStructure[] { new TestStructure() };
        lib.modifyStructureVarArgs("ss", arg1, varargs[0]);
        assertEquals("Structure memory not read in fixed arg w/varargs",
                     MAGIC32, arg1.magic); 
        assertEquals("Structure memory not read in varargs",
                     MAGIC32, varargs[0].magic); 
                     
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(VarArgsTest.class);
    }
}
