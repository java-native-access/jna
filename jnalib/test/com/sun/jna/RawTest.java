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
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        public static native void memset(byte[] b, int v, int len);
        public static native int strlen(String s1);
        public static native int strlen(Pointer p);
        public static native int strlen(byte[] b);
        public static native int strlen(Buffer b);
        
        static {
            Native.register(Platform.isWindows()?"msvcrt":"c");
        }
    }

    static interface CInterface extends Library {
        Pointer memset(Pointer p, int v, int len);
        int strlen(String s);
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

    // Requires java.library.path include testlib
    public static void checkPerformance() {
        System.out.println("Checking performance of different access methods");
        final int SIZE = 8*1024;
        ByteBuffer b = ByteBuffer.allocateDirect(SIZE);
        // Native order is faster
        b.order(ByteOrder.nativeOrder());
        Pointer pb = Native.getDirectBufferPointer(b);

        String mname = Platform.isWindows()?"msvcrt":"m";
        MathInterface mlib = (MathInterface)
            Native.loadLibrary(mname, MathInterface.class);
        Function f = NativeLibrary.getInstance(mname).getFunction("cos");
        final int COUNT = 100000;

        ///////////////////////////////////////////
        // cos
        Object[] args = { new Double(0) };
        double dresult;
        long start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            dresult = mlib.cos(0d);
        }
        long delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNA interface): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            dresult = f.invokeDouble(args);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNA function): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            dresult = MathLibrary.cos(0d);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNA raw): " + delta + "ms");

        long types = pb.peer;
        b.putInt(0, (int)Structure.FFIType.get(double.class).peer);
        long cif = Native.ffi_prep_cif(0, 1, Structure.FFIType.get(double.class).peer, types);
        long resp = pb.peer + 4;
        long argv = pb.peer + 12;
        if (Native.POINTER_SIZE == 4) {
            start = System.currentTimeMillis();
            for (int i=0;i < COUNT;i++) {
                b.putInt(8, (int)pb.peer + 16);
                b.putDouble(16, 0);
                Native.ffi_call(cif, f.peer, resp, argv);
                dresult = b.getDouble(4);
            }
            delta = System.currentTimeMillis() - start;
            System.out.println("cos (JNI ffi): " + delta + "ms");
        }

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            dresult = JNI.cos(0d);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNI): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            dresult = Math.cos(0d);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (pure java): " + delta + "ms");

        ///////////////////////////////////////////
        // memset
        Pointer presult;
        String cname = Platform.isWindows()?"msvcrt":"c";
        CInterface clib = (CInterface)
            Native.loadLibrary(cname, CInterface.class);
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            presult = clib.memset(null, 0, 0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA interface): " + delta + "ms");

        f = NativeLibrary.getInstance(cname).getFunction("memset");
        args = new Object[] { null, new Integer(0), new Integer(0)};
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            presult = f.invokePointer(args);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA function): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            presult = CLibrary.memset((Pointer)null, 0, 0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA raw): " + delta + "ms");

        if (Native.POINTER_SIZE == 4) {
            types = pb.peer;
            b.putInt(0, (int)Structure.FFIType.get(Pointer.class).peer);
            b.putInt(4, (int)Structure.FFIType.get(int.class).peer);
            b.putInt(8, (int)Structure.FFIType.get(int.class).peer);
            cif = Native.ffi_prep_cif(0, 3, Structure.FFIType.get(Pointer.class).peer, types);
            resp = pb.peer + 12;
            argv = pb.peer + 16;
            start = System.currentTimeMillis();
            for (int i=0;i < COUNT;i++) {
                b.putInt(16, (int)pb.peer + 28);
                b.putInt(20, (int)pb.peer + 32);
                b.putInt(24, (int)pb.peer + 36);
                b.putInt(28, 0);
                b.putInt(32, 0);
                b.putInt(36, 0);
                Native.ffi_call(cif, f.peer, resp, argv);
                b.getInt(4);
            }
            delta = System.currentTimeMillis() - start;
            System.out.println("memset (JNI ffi): " + delta + "ms");
        }

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            Pointer._setMemory(0L, 0L, (byte)0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNI): " + delta + "ms");

        ///////////////////////////////////////////
        // strlen
        int iresult;
        String str = "performance test";
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            iresult = clib.strlen(str);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA interface): " + delta + "ms");

        f = NativeLibrary.getInstance(cname).getFunction("strlen");
        args = new Object[] { str };
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            iresult = f.invokeInt(args);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA function): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            iresult = CLibrary.strlen(str);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA raw - String): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            iresult = CLibrary.strlen(new NativeString(str).getPointer());
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA raw - Pointer): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            iresult = CLibrary.strlen(Native.toByteArray(str));
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA raw - byte[]): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            byte[] bytes = str.getBytes();
            b.position(0);
            b.put(bytes);
            b.put((byte)0);
            iresult = CLibrary.strlen(b);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA raw - Buffer): " + delta + "ms");

        if (Native.POINTER_SIZE == 4) {
            types = pb.peer;
            b.putInt(0, (int)Structure.FFIType.get(Pointer.class).peer);
            cif = Native.ffi_prep_cif(0, 1, Structure.FFIType.get(int.class).peer, types);
            resp = pb.peer + 4;
            argv = pb.peer + 8;
            start = System.currentTimeMillis();
            for (int i=0;i < COUNT;i++) {
                b.putInt(8, (int)pb.peer + 12);
                b.putInt(12, (int)pb.peer + 16);
                b.position(16);
                // This operation is very expensive!
                b.put(str.getBytes());
                b.put((byte)0);
                Native.ffi_call(cif, f.peer, resp, argv);
                iresult = b.getInt(4);
            }
            delta = System.currentTimeMillis() - start;
            System.out.println("strlen (JNI ffi): " + delta + "ms");
        }
        ///////////////////////////////////////////
        // Direct buffer vs. Pointer methods
        byte[] bulk = new byte[SIZE];
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            b.putInt(0, 0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("direct Buffer write: " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            b.position(0);
            b.put(bulk);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("direct Buffer write (bulk): " + delta + "ms");

        Pointer p = new Memory(SIZE);
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            p.setInt(0, 0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("Memory write: " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            p.write(0, bulk, 0, bulk.length);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("Memory write (bulk): " + delta + "ms");
    }
}

