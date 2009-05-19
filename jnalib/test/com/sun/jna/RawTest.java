/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
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

import junit.framework.*;
import com.sun.jna.*;
import java.lang.ref.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class RawTest extends TestCase {

    private static final String BUILDDIR = 
        System.getProperty("jna.builddir",
                           "build" + (Platform.is64Bit() ? "-d64" : "")); 

    private static class JNI {
        static {
            String path = BUILDDIR + "/native/" + System.mapLibraryName("testlib");;
            if (!new File(path).isAbsolute()) {
                path = System.getProperty("user.dir") + "/" + path;
            }
            if (path.endsWith(".jnilib")) {
                path = path.replace(".jnilib", ".dylib");
            }
            System.load(path);
        }
        
        private static native double cos(double x);
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(RawTest.class);
        checkPerformance();
    }

    static class MathLibrary {

        public static native double cos(double x);
        
        static {
            Native.register(Platform.isWindows()?"msvcrt":"m");
        }
    }

    interface MathInterface extends Library {
        double cos(double x);
    }

    static class CLibrary {
        public static native Pointer memset(Pointer p, int v, int len);
        
        static {
            Native.register(Platform.isWindows()?"msvcrt":"c");
        }
    }

    static interface CInterface extends Library {
        Pointer memset(Pointer p, int v, int len);
    }

    private static class TestLoader extends URLClassLoader {
        public TestLoader() throws MalformedURLException {
            super(new URL[] {
                new File(BUILDDIR + "/classes").toURI().toURL(),
                new File(BUILDDIR + "/test-classes").toURI().toURL(),
            }, null);
        }
    }

    public void testRegisterMethods() throws Exception {
        // Use a dedicated class loader to ensure the class can be gc'd
        String name = "com.sun.jna.RawTest$MathLibrary";
        ClassLoader loader = new TestLoader();
        Class cls = Class.forName(name, true, loader);
        assertNotNull("Failed loading class", cls);
        WeakReference ref = new WeakReference(cls);
        loader = null;
        cls = null;
        System.gc();

        for (int i=0;i < 100 && ref.get() != null;i++) {
            try {
                Thread.sleep(10); // Give the GC a chance to run
		System.gc();
            } finally {}
        }
        // TODO: need a real check for freed native memory
        assertNull("Registered methods not GC'd: " + ref.get(), ref.get());
    }

    public void testInvokeMethod() {
        assertEquals("Wrong value", Math.cos(0), MathLibrary.cos(0d));
    }

    public void testInvokeMethodWithPointer() {
        assertNull("Returned pointer should be null",
                   CLibrary.memset(null, 0, 0));
    }

    // Requires java.library.path include testlib
    public static void checkPerformance() {
        System.out.println("Checking performance of different access methods");

        String mname = Platform.isWindows()?"msvcrt":"m";
        MathInterface mlib = (MathInterface)
            Native.loadLibrary(mname, MathInterface.class);
        Function f = NativeLibrary.getInstance(mname).getFunction("cos");
        final int COUNT = 1000000;
        Object[] args = { new Double(0) };
        long start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            mlib.cos(0d);
        }
        long delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNA interface): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            f.invokeDouble(args);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNA Function): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            MathLibrary.cos(0d);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNA raw): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            JNI.cos(0d);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNI): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            Math.cos(0d);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (pure java): " + delta + "ms");

        // memset
        String cname = Platform.isWindows()?"msvcrt":"c";
        CInterface clib = (CInterface)
            Native.loadLibrary(cname, CInterface.class);
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            clib.memset(null, 0, 0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA interface): " + delta + "ms");

        f = NativeLibrary.getInstance(cname).getFunction("memset");
        args = new Object[] { null, new Integer(0), new Integer(0)};
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            f.invokePointer(args);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA Function): " + delta + "ms");

        f = NativeLibrary.getInstance(cname).getFunction("memset");
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            CLibrary.memset(null, 0, 0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA raw): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            Pointer._setMemory(0L, 0L, (byte)0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNI): " + delta + "ms");
    }
}

