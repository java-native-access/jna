/* Copyright (c) 2019 Matthias Bläsing, All Rights Reserved
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

import org.junit.Test;
import static org.junit.Assert.*;

public class DefaultMethodInvocationTest {

    private final static MixedDirectTestLibrary lib = new DefaultMethodInvocationTest.MixedDirectTestLibrary();
    private final static MixedDirectTestLibrary2 lib2 = new DefaultMethodInvocationTest.MixedDirectTestLibrary2();

    public static interface MixedTestLibrary extends Library {
        public MixedTestLibrary INSTANCE = Native.load("testlib", MixedTestLibrary.class);

        String returnStringArgument(String s);

        default String returnHello(String name) {
            return returnStringArgument("Hello " + name);
        }
    }

    public static class MixedDirectTestLibrary implements MixedTestLibrary {
        @Override
        public native String returnStringArgument(String s);

        static {
            Native.register("testlib");
        }
    }

    public static class MixedDirectTestLibrary2 implements MixedTestLibrary {
        @Override
        public native String returnStringArgument(String s);

        @Override
        public String returnHello(String name) {
            return "Greetings " + name;
        }

        static {
            Native.register("testlib");
        }
    }

    @Test
    public void testBoundMethodInvoke() {
        assertEquals("world", MixedTestLibrary.INSTANCE.returnStringArgument("world"));
    }

    @Test
    public void testDefaultMethodInvoke() {
        assertEquals("Hello world", MixedTestLibrary.INSTANCE.returnHello("world"));
    }

    @Test
    public void testDirectBoundMethodInvoke() {
        assertEquals("world", lib.returnStringArgument("world"));
    }

    @Test
    public void testDirectDefaultMethodInvoke() {
        assertEquals("Hello world", lib.returnHello("world"));
    }

    @Test
    public void testDefaultMethodCanBeOverriden() {
        assertEquals("Greetings world", lib2.returnHello("world"));
    }
}
