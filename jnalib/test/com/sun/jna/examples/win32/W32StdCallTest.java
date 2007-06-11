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
package com.sun.jna.examples.win32;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import javax.swing.JFrame;
import javax.swing.JLabel;
import junit.framework.TestCase;
import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author twall@users.sf.net
 */
public class W32StdCallTest extends TestCase {

    public static interface TestLibrary extends StdCallLibrary {
        TestLibrary INSTANCE = (TestLibrary)
            Native.loadLibrary("testlib", TestLibrary.class, new HashMap() {
                { put(OPTION_FUNCTION_MAPPER, FUNCTION_MAPPER); }
            });
        int returnInt32ArgumentStdCall(int arg);
        interface Int32Callback extends StdCallCallback {
            int callback(int arg, int arg2);
        }
        int callInt32StdCallCallback(Int32Callback c, int arg, int arg2);
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(W32StdCallTest.class);
    }

    private TestLibrary testlib;
    
    protected void setUp() {
        testlib = TestLibrary.INSTANCE;
    }
    
    protected void tearDown() {
        testlib = null;
    }

    public void testStructureOutArgument() {
        Kernel32 kernel = Kernel32.INSTANCE;
        Kernel32.SYSTEMTIME time = new Kernel32.SYSTEMTIME();
        kernel.GetSystemTime(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        assertEquals("Hour not properly set", 
                     cal.get(Calendar.HOUR_OF_DAY), time.wHour); 
        assertEquals("Day not properly set", 
                     cal.get(Calendar.DAY_OF_WEEK)-1, 
                     time.wDayOfWeek); 
        assertEquals("Year not properly set", 
                     cal.get(Calendar.YEAR), time.wYear); 
    }
    
    public void testFunctionMapper() throws Exception {
        NativeLibrary lib = NativeLibrary.getInstance("testlib");
        Method m = TestLibrary.class.getMethod("returnInt32ArgumentStdCall", new Class[] { int.class });
        assertEquals("Function mapper should provide decorated name",
                     "returnInt32ArgumentStdCall@4",
                     StdCallLibrary.FUNCTION_MAPPER.getFunctionName(lib, m));
    }
    
    public void testStdCallReturnInt32Argument() {
        final int MAGIC = 0x12345678;
        assertEquals("Expect zero return", 0, testlib.returnInt32ArgumentStdCall(0));
        assertEquals("Expect magic return", MAGIC, testlib.returnInt32ArgumentStdCall(MAGIC));
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
        assertTrue("__stdcall callback not called", called[0]);
        assertEquals("Wrong __stdcall callback value", Integer.toHexString(EXPECTED), 
                     Integer.toHexString(value));
        
        value = testlib.callInt32StdCallCallback(cb, -1, -2);
        assertEquals("Wrong __stdcall callback return", -3, value);
    }
}
