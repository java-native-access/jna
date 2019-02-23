/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

//@SuppressWarnings("unused")
public class LastErrorTest extends TestCase {

    private static final Map<String, ?> OPTIONS =
            Collections.singletonMap(Library.OPTION_FUNCTION_MAPPER, new FunctionMapper() {
                @Override
                public String getFunctionName(NativeLibrary library, Method m) {
                    if (m.getName().equals("noThrowLastError")
                        || m.getName().equals("throwLastError")) {
                        return "setLastError";
                    }
                    return m.getName();
                }
            });

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
        final TestLibrary lib = Native.load("testlib", TestLibrary.class);
        final int NTHREADS = 100;
        final int[] errors = new int[NTHREADS];
        List<Thread> threads = new ArrayList<Thread>(NTHREADS);
        for (int i=0;i < NTHREADS;i++) {
            final int idx = i;
            Thread t = new Thread("tLastErrorSetter-" + i) {
                @Override
                public void run() {
                    lib.setLastError(-idx-1);
                    errors[idx] = Native.getLastError();
                }
            };
            t.setDaemon(true);  // so we can stop the main thread if necessary
            threads.add(t);
        }
        int EXPECTED = 42;
        lib.setLastError(EXPECTED);
        assertEquals("Wrong error on main thread (immediate)", EXPECTED, Native.getLastError());
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join(TimeUnit.SECONDS.toMillis(7L));
            assertFalse("Thread " + t.getName() + " still alive", t.isAlive());
        }

        assertEquals("Wrong error on main thread", EXPECTED, Native.getLastError());
        for (int i=0;i < threads.size();i++) {
            assertEquals("Wrong error on thread " + i, -i-1, errors[i]);
        }
    }

    private final int ERROR = Platform.isWindows() ? 1 : -1;
    public void testThrowLastError() {
        TestLibrary lib = Native.load("testlib", TestLibrary.class, OPTIONS);

        lib.noThrowLastError(ERROR);
        assertEquals("Last error not preserved", ERROR, Native.getLastError());
        try {
            lib.throwLastError(ERROR);
            fail("Method should throw LastErrorException");
        } catch(LastErrorException e) {
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
        } catch(LastErrorException e) {
            assertEquals("Exception should contain error code", ERROR, e.getErrorCode());
            assertTrue("Exception should include error message: " + e.getMessage(), e.getMessage().length() > 0);
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LastErrorTest.class);
    }
}
