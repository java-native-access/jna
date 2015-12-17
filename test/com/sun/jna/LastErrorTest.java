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
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Method;

import junit.framework.TestCase;

//@SuppressWarnings("unused")
public class LastErrorTest extends TestCase {

    private static final Map OPTIONS = new HashMap() {{
        put(Library.OPTION_FUNCTION_MAPPER, new FunctionMapper() {
            @Override
            public String getFunctionName(NativeLibrary library, Method m) {
                if (m.getName().equals("noThrowLastError")
                    || m.getName().equals("throwLastError")) {
                    return "setLastError";
                }
                return m.getName();
            }
        });
    }};

    public interface TestLibrary extends Library {
        void setLastError(int code);
        void noThrowLastError(int code);
        void throwLastError(int code) throws LastErrorException;
    }

    public static class DirectTestLibrary implements TestLibrary {
        @Override
        public native void setLastError(int code);
        @Override
        public native void noThrowLastError(int code);
        @Override
        public native void throwLastError(int code) throws LastErrorException;
        static {
            Native.register(NativeLibrary.getInstance("testlib", OPTIONS));
        }
    }

    public void testLastErrorPerThreadStorage() throws Exception {
        final TestLibrary lib = Native.loadLibrary("testlib", TestLibrary.class);
        final int NTHREADS = 100;
        final int[] errors = new int[NTHREADS];
        Set<Thread> threads = new HashSet<Thread>();
        for (int i=0;i < NTHREADS;i++) {
            final int idx = i;
            Thread t = new Thread() { @Override
            public void run() {
                lib.setLastError(-idx-1);
                errors[idx] = Native.getLastError();
            }};
            threads.add(t);
        }
        int EXPECTED = 42;
        lib.setLastError(EXPECTED);
        assertEquals("Wrong error on main thread (immediate)", EXPECTED, Native.getLastError());
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        assertEquals("Wrong error on main thread", EXPECTED, Native.getLastError());
        for (int i=0;i < threads.size();i++) {
            assertEquals("Wrong error on thread " + i, -i-1, errors[i]);
        }
    }

    private final int ERROR = Platform.isWindows() ? 1 : -1;
    public void testThrowLastError() {
        TestLibrary lib = Native.loadLibrary("testlib", TestLibrary.class, OPTIONS);

        lib.noThrowLastError(ERROR);
        assertEquals("Last error not preserved", ERROR, Native.getLastError());
        try {
            lib.throwLastError(ERROR);
            fail("Method should throw LastErrorException");
        }
        catch(LastErrorException e) {
            assertEquals("Exception should contain error code", ERROR, e.getErrorCode());
            assertTrue("Exception should include error message: '" + e.getMessage() + "'", e.getMessage().length() > 0);
        }
    }

    public void testThrowLastErrorDirect() {
        TestLibrary lib = new DirectTestLibrary();

        lib.noThrowLastError(ERROR);
        assertEquals("Last error not preserved", ERROR, Native.getLastError());
        try {
            lib.throwLastError(ERROR);
            fail("Method should throw LastErrorException");
        }
        catch(LastErrorException e) {
            assertEquals("Exception should contain error code", ERROR, e.getErrorCode());
            assertTrue("Exception should include error message: " + e.getMessage(), e.getMessage().length() > 0);
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LastErrorTest.class);
    }
}
