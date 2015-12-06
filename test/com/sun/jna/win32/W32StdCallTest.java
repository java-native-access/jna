/* Copyright (c) 2007-2014 Timothy Wall, All Rights Reserved
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
package com.sun.jna.win32;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author twall@users.sf.net
 */
public class W32StdCallTest extends TestCase {

    public static interface TestLibrary extends StdCallLibrary {
        public static class Inner extends Structure {
            public double value;
            protected List getFieldOrder() {
                return Arrays.asList(new String[] { "value" }); 
            }
        }
        public static class TestStructure extends Structure {
            public static class ByValue extends TestStructure implements Structure.ByValue { }
            public byte c;
            public short s;
            public int i;
            public long j;
            public Inner inner;
            protected List getFieldOrder() {
                return Arrays.asList(new String[] { "c", "s", "i", "j", "inner" }); 
            }
        }
        int returnInt32ArgumentStdCall(int arg);
        short returnInt16ArgumentStdCall(short arg1, short arg2, Pointer buf, short len);
        TestStructure.ByValue returnStructureByValueArgumentStdCall(TestStructure.ByValue arg);
        interface Int32Callback extends StdCallCallback {
            int callback(int arg, int arg2);
        }
        int callInt32StdCallCallback(Int32Callback c, int arg, int arg2);
        interface ManyArgsStdCallCallback extends StdCallCallback {
            void callback(NativeLong arg1, int arg2, double arg3,
                          String arg4, String arg5,
                          double arg6, NativeLong arg7,
                          double arg8, NativeLong arg9,
                          NativeLong arg10, NativeLong arg11);
        }
        int callManyArgsStdCallCallback(ManyArgsStdCallCallback c, NativeLong arg1, int arg2,
                                        double arg3, String arg4, String arg5,
                                        double arg6, NativeLong arg7,
                                        double arg8, NativeLong arg9,
                                        NativeLong arg10, NativeLong arg11);
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(W32StdCallTest.class);
    }

    private TestLibrary testlib;
    
    protected void setUp() {
        testlib = (TestLibrary)
            Native.loadLibrary("testlib", TestLibrary.class, new HashMap() {
                { put(Library.OPTION_FUNCTION_MAPPER, StdCallLibrary.FUNCTION_MAPPER); }
            });
    }
    
    protected void tearDown() {
        testlib = null;
    }

    public void testFunctionMapper() throws Exception {
        FunctionMapper mapper = StdCallLibrary.FUNCTION_MAPPER;
        NativeLibrary lib = NativeLibrary.getInstance("testlib");

        Method[] methods = {
            TestLibrary.class.getMethod("returnInt32ArgumentStdCall",
                                        new Class[] { int.class }),
            TestLibrary.class.getMethod("returnStructureByValueArgumentStdCall",
                                        new Class[] {
                                            TestLibrary.TestStructure.ByValue.class
                                        }),
            TestLibrary.class.getMethod("callInt32StdCallCallback",
                                        new Class[] {
                                            TestLibrary.Int32Callback.class,
                                            int.class, int.class,
                                        }),
        };

        for (int i=0;i < methods.length;i++) {
            String name = mapper.getFunctionName(lib, methods[i]);
            assertTrue("Function name not decorated for method "
                       + methods[i].getName()
                       + ": " + name, name.indexOf("@") != -1);
            assertEquals("Wrong name in mapped function",
                         name, lib.getFunction(name, StdCallLibrary.STDCALL_CONVENTION).getName());
        }
    }
    
    public void testStdCallReturnInt32Argument() {
        final int MAGIC = 0x12345678;
        assertEquals("Expect zero return", 0, testlib.returnInt32ArgumentStdCall(0));
        assertEquals("Expect magic return", MAGIC, testlib.returnInt32ArgumentStdCall(MAGIC));
    }
    
    public void testStdCallReturnStructureByValueArgument() {
        TestLibrary.TestStructure.ByValue s = new TestLibrary.TestStructure.ByValue();
        assertTrue("Wrong struct value", s.dataEquals(testlib.returnStructureByValueArgumentStdCall(s)));
    }
    
    public void testStdCallStackAlignment() {
        final short MAGIC = 0x1234;
        assertEquals("Incorrect stdcall stack alignment", MAGIC,
                     testlib.returnInt16ArgumentStdCall((short)1, (short)2,
                                                        null, MAGIC));
    }

    public void testStdCallCallback() {
        final int MAGIC = 0x11111111;
        final boolean[] called = { false };
        TestLibrary.Int32Callback cb = new TestLibrary.Int32Callback() {
            public int callback(int arg, int arg2) {
                called[0] = true;
                return arg + arg2;
            }
        };
        final int EXPECTED = MAGIC*3;
        int value = testlib.callInt32StdCallCallback(cb, MAGIC, MAGIC*2);
        assertTrue("stdcall callback not called", called[0]);
        if (value == -1) {
            fail("stdcall callback did not restore the stack pointer");
        }
        assertEquals("Wrong stdcall callback value", Integer.toHexString(EXPECTED), 
                     Integer.toHexString(value));
        
        value = testlib.callInt32StdCallCallback(cb, -1, -2);
        if (value == -1) {
            fail("stdcall callback did not restore the stack pointer");
        }
        assertEquals("Wrong stdcall callback return", -3, value);
    }

    public void testStdCallCallbackStackAlignment() {
        final boolean[] called = { false };
        TestLibrary.ManyArgsStdCallCallback cb = new TestLibrary.ManyArgsStdCallCallback() {
            public void callback(NativeLong arg1, int arg2, double arg3,
                                 String arg4, String arg5,
                                 double arg6, NativeLong arg7,
                                 double arg8, NativeLong arg9,
                                 NativeLong arg10, NativeLong arg11) {
                called[0] = true;
            }
        };
        int value = testlib.callManyArgsStdCallCallback(cb, new NativeLong(1),
                                                        2, 3, "four", "five",
                                                        6, new NativeLong(7),
                                                        8, new NativeLong(9),
                                                        new NativeLong(10),
                                                        new NativeLong(11));
        assertTrue("stdcall callback not called", called[0]);
        if (value == -1) {
            fail("stdcall callback did not restore the stack pointer");
        }
    }
}
