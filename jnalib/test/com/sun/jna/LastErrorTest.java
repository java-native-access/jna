/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
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

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public class LastErrorTest extends TestCase {
    
    public interface TestLibrary extends Library {
        void noThrowLastError(int code);
        void throwLastError(int code) throws LastErrorException;
    }

    public void testThrowLastError() {
        Map options = new HashMap();
        options.put(Library.OPTION_FUNCTION_MAPPER, new FunctionMapper() {
            public String getFunctionName(NativeLibrary library, Method m) {
                if (m.getName().equals("noThrowLastError")
                    || m.getName().equals("throwLastError")) {
                    return "setLastError";
                }
                return m.getName();
            }
        });
        TestLibrary lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class, options);

        final int ERROR = -1;
        lib.noThrowLastError(ERROR);
        assertEquals("Last error not preserved", ERROR, Native.getLastError());
        try {
            lib.throwLastError(ERROR);
            fail("Method should throw LastErrorException");
        }
        catch(LastErrorException e) {
            assertEquals("Exception should contain error code", ERROR, e.errorCode);
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LastErrorTest.class);
    }
}
