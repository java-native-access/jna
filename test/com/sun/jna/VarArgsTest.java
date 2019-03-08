/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.sun.jna.VarArgsTest.TestLibrary.TestStructure;

public class VarArgsTest extends TestCase {
    final int MAGIC32 = 0x12345678;
    public static interface TestLibrary extends Library {
        public static class TestStructure extends Structure {
            public int magic = 0;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("magic");
            }
        }
        public int addVarArgs(String fmt, Number... args);
        public String returnStringVarArgs(String fmt, Object... args);
        public void modifyStructureVarArgs(String fmt, Object arg1, Object... args);
        public String returnStringVarArgs2(String... args);
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
    public void testIntVarArgs() {
        int arg1 = 1;
        int arg2 = 2;
        assertEquals("32-bit integer varargs not added correctly", arg1 + arg2,
                     lib.addVarArgs("dd", Integer.valueOf(arg1), Integer.valueOf(arg2)));
    }
    public void testShortVarArgs() {
        short arg1 = 1;
        short arg2 = 2;
        assertEquals("16-bit integer varargs not added correctly", arg1 + arg2,
                     lib.addVarArgs("dd", Short.valueOf(arg1), Short.valueOf(arg2)));
    }
    public void testLongVarArgs() {
        short arg1 = 1;
        short arg2 = 2;
        assertEquals("64-bit integer varargs not added correctly", arg1 + arg2,
                     lib.addVarArgs("ll", Long.valueOf(arg1), Long.valueOf(arg2)));
    }
    public void testFloatVarArgs() {
        float arg1 = 1;
        float arg2 = 2;
        assertEquals("float varargs not added correctly", (int)arg1 + (int)arg2,
                     lib.addVarArgs("ff", Float.valueOf(arg1), Float.valueOf(arg2)));
    }
    public void testDoubleVarArgs() {
        double arg1 = 1;
        double arg2 = 2;
        assertEquals("double varargs not added correctly", (int)arg1 + (int)arg2,
                     lib.addVarArgs("gg", Double.valueOf(arg1), Double.valueOf(arg2)));
    }
    public void testStringVarArgs() {
        Object[] args = new Object[] { "Test" };
        assertEquals("Did not return correct string", args[0],
                     lib.returnStringVarArgs("", args));
    }

    public void testStringVarArgsFull() {
        Object[] args = new Object[] { "Test" };
        assertEquals("Did not return correct string", args[0],
                     lib.returnStringVarArgs2("", "Test"));
    }

    public void testAppendNullToVarargs() {
        Number[] args = new Number[] { Integer.valueOf(1) };
        assertEquals("No trailing NULL was appended to varargs list",
                     1, lib.addVarArgs("dd", args));
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
