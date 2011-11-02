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
import com.sun.jna.ptr.PointerByReference;
import java.lang.ref.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

//@SuppressWarnings("unused")
public class DirectTest extends TestCase {

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
        junit.textui.TestRunner.run(DirectTest.class);
    }

    static class MathLibrary {

        public static native double cos(double x);
        
        static {
            Native.register(Platform.MATH_LIBRARY_NAME);
        }
    }

    interface MathInterface extends Library {
        double cos(double x);
    }

    static class CLibrary {
        public static class size_t extends IntegerType {
            public size_t() {
                super(Native.POINTER_SIZE);
            }
            public size_t(long value) {
                super(Native.POINTER_SIZE, value);
            }
        }

        public static native Pointer memset(Pointer p, int v, size_t len);
        public static native Pointer memset(Pointer p, int v, int len);
        public static native Pointer memset(Pointer p, int v, long len);
        public static native long memset(long p, int v, long len);
        public static native int memset(int p, int v, int len);
        public static native int strlen(String s1);
        public static native int strlen(Pointer p);
        public static native int strlen(byte[] b);
        
        static {
            Native.register(Platform.C_LIBRARY_NAME);
        }
    }

    static interface CInterface extends Library {
        Pointer memset(Pointer p, int v, int len);
        int strlen(String s);
    }

    static interface TestInterface extends Library {
        interface Int32Callback extends Callback {
            int invoke(int arg1, int arg2);
        }
        interface NativeLongCallback extends Callback {
            NativeLong invoke(NativeLong arg1, NativeLong arg2);
        }
        int callInt32CallbackRepeatedly(Int32Callback cb, int arg1, int arg2, int count);
        NativeLong callLongCallbackRepeatedly(NativeLongCallback cb, NativeLong arg1, NativeLong arg2, int count);
    }

    static class TestLibrary implements TestInterface {
        public native int callInt32CallbackRepeatedly(Int32Callback cb, int arg1, int arg2, int count);
        public native NativeLong callLongCallbackRepeatedly(NativeLongCallback cb, NativeLong arg1, NativeLong arg2, int count);
        static {
            Native.register("testlib");
        }
    }

    private static class TestLoader extends URLClassLoader {
        public TestLoader() throws MalformedURLException {
            super(Platform.isWindowsCE()
                  ? new URL[] {
                      new File("/Storage Card/test.jar").toURI().toURL()
                  }
                  : new URL[] {
                      new File(BUILDDIR + "/classes").toURI().toURL(),
                      new File(BUILDDIR + "/test-classes").toURI().toURL(),
                  }, null);
        }
        protected Class findClass(String name) throws ClassNotFoundException {
            String boot = System.getProperty("jna.boot.library.path");
            if (boot != null) {
                System.setProperty("jna.boot.library.path", "");
            }
            Class cls = super.findClass(name);
            if (boot != null) {
                System.setProperty("jna.boot.library.path", boot);
            }
            return cls;
        }
    }

    public void testRegisterMethods() throws Exception {
        // Use a dedicated class loader to ensure the class can be gc'd
        String name = "com.sun.jna.DirectTest$MathLibrary";
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
        // TODO: need a real check to ensure native memory is freed
        assertNull("Registered methods not GC'd: " + ref.get(), ref.get());
    }

    private Class returnCallingClass() {
        return Native.getCallingClass();
    }

    public void testFindCallingClass() {
        assertEquals("Wrong calling class detected",
                     getClass(), returnCallingClass());
    }

    public void testFindNativeClass() {
        class UnregisterLibrary {
            class Inner {
                public Class getNativeClass() {
                    return getNativeClassInner();
                }
                public Class getNativeClassInner() {
                    return Native.getNativeClass(Native.getCallingClass());
                };
            }
            public native double cos(double x);
            public Class getNativeClass() {
                return new Inner().getNativeClass();
            };
        }
        assertEquals("Wrong native class found",
                     UnregisterLibrary.class, new UnregisterLibrary().getNativeClass());
    }
}

