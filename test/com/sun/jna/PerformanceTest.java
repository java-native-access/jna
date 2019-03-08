/* Copyright (c) 2009-2015 Timothy Wall, All Rights Reserved
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

import junit.framework.*;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.Collections;
import java.lang.reflect.Method;

import com.sun.jna.DirectTest.TestInterface;
import com.sun.jna.DirectTest.TestLibrary;

//@SuppressWarnings("unused")
public class PerformanceTest extends TestCase implements Paths {

    public void testEmpty() { }

    private static class JNILibrary {
        static {
            String path = TESTPATH + NativeLibrary.mapSharedLibraryName("testlib");;
            if (!new File(path).isAbsolute()) {
                path = new File(path).getAbsolutePath();
            }
            System.load(path);
        }

        private static native double cos(double x);
        private static native int getpid();
    }

    public static void main(java.lang.String[] argList) {
        checkPerformance();
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
            private static final long serialVersionUID = 1L;

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
        public static native int strlen(Buffer b);

        static {
            Native.register(Platform.C_LIBRARY_NAME);
        }
    }

    static class PIDLibrary {
        public static native int getpid();
        static {
            Native.register(Platform.C_LIBRARY_NAME);
        }
    }

    static class W32PIDLibrary {
        public static native int _getpid();
        static {
            Native.register(Platform.C_LIBRARY_NAME);
        }
    }

    static interface CInterface extends Library {
        int getpid();
        Pointer memset(Pointer p, int v, int len);
        int strlen(String s);
    }

    // Requires java.library.path include testlib
    public static void checkPerformance() {
        if (!Platform.HAS_BUFFERS) return;

        final int COUNT = 100000;
        System.out.println("Checking performance of different access methods (" + COUNT + " iterations)");
        final int SIZE = 8*1024;
        ByteBuffer b = ByteBuffer.allocateDirect(SIZE);
        // Native order is faster
        b.order(ByteOrder.nativeOrder());
        Pointer pb = Native.getDirectBufferPointer(b);

        String mname = Platform.MATH_LIBRARY_NAME;
        MathInterface mlib = Native.load(mname, MathInterface.class);
        Function f = NativeLibrary.getInstance(mname).getFunction("cos");

        ///////////////////////////////////////////
        // cos
        Object[] args = { Double.valueOf(0) };
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
        System.out.println("cos (JNA direct): " + delta + "ms");

        long types = pb.peer;
        long cif;
        long resp;
        long argv;
        if (Native.POINTER_SIZE == 4) {
            b.putInt(0, (int)Structure.FFIType.get(double.class).peer);
            cif = Native.ffi_prep_cif(0, 1, Structure.FFIType.get(double.class).peer, types);
            resp = pb.peer + 4;
            argv = pb.peer + 12;
            double INPUT = 42;
            start = System.currentTimeMillis();
            for (int i=0;i < COUNT;i++) {
                b.putInt(12, (int)pb.peer + 16);
                b.putDouble(16, INPUT);
                Native.ffi_call(cif, f.peer, resp, argv);
                dresult = b.getDouble(4);
            }
            delta = System.currentTimeMillis() - start;
        }
        else {
            b.putLong(0, Structure.FFIType.get(double.class).peer);
            cif = Native.ffi_prep_cif(0, 1, Structure.FFIType.get(double.class).peer, types);
            resp = pb.peer + 8;
            argv = pb.peer + 16;
            double INPUT = 42;
            start = System.currentTimeMillis();
            for (int i=0;i < COUNT;i++) {
                b.putLong(16, pb.peer + 24);
                b.putDouble(24, INPUT);
                Native.ffi_call(cif, f.peer, resp, argv);
                dresult = b.getDouble(8);
            }
            delta = System.currentTimeMillis() - start;
        }
        System.out.println("cos (JNI ffi): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            dresult = JNILibrary.cos(0d);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (JNI): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            dresult = Math.cos(0d);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("cos (pure java): " + delta + "ms");

        String cname = Platform.C_LIBRARY_NAME;
        Map<String, ?> options = Collections.<String, Object>emptyMap();
        if (Platform.isWindows()) {
            options = Collections.singletonMap(Library.OPTION_FUNCTION_MAPPER, new FunctionMapper() {
                @Override
                public String getFunctionName(NativeLibrary library, Method method) {
                    String name = method.getName();
                    if ("getpid".equals(name)) {
                        name = "_getpid";
                    }
                    return name;
                }
            });
        }
        CInterface clib = Native.load(cname, CInterface.class, options);

        ///////////////////////////////////////////
        // getpid
        int pid;
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            pid = clib.getpid();
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("getpid (JNA interface): " + delta + "ms");

        start = System.currentTimeMillis();
        if (Platform.isWindows()) {
            for (int i=0;i < COUNT;i++) {
                pid = W32PIDLibrary._getpid();
            }
        }
        else {
            for (int i=0;i < COUNT;i++) {
                pid = PIDLibrary.getpid();
            }
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("getpid (JNA direct): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            pid = JNILibrary.getpid();
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("getpid (JNI): " + delta + "ms");

        ///////////////////////////////////////////
        // memset
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            Pointer presult = clib.memset(null, 0, 0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA interface): " + delta + "ms");

        f = NativeLibrary.getInstance(cname).getFunction("memset");
        args = new Object[] { null, Integer.valueOf(0), Integer.valueOf(0)};
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            Pointer presult = f.invokePointer(args);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA function): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            Pointer presult = CLibrary.memset((Pointer)null, 0, new CLibrary.size_t(0));
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA direct Pointer/size_t): " + delta + "ms");
        start = System.currentTimeMillis();
        if (Native.POINTER_SIZE == 4) {
            for (int i=0;i < COUNT;i++) {
                Pointer presult = CLibrary.memset((Pointer)null, 0, 0);
            }
        } else {
            for (int i=0;i < COUNT;i++) {
                Pointer presult = CLibrary.memset((Pointer)null, 0, 0L);
            }
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA direct Pointer/primitive): " + delta + "ms");
        start = System.currentTimeMillis();
        if (Native.POINTER_SIZE == 4) {
            for (int i=0;i < COUNT;i++) {
                int iresult = CLibrary.memset(0, 0, 0);
            }
        } else {
            for (int i=0;i < COUNT;i++) {
                long jresult = CLibrary.memset(0L, 0, 0L);
            }
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNA direct primitives): " + delta + "ms");

        if (Native.POINTER_SIZE == 4) {
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
                b.getInt(12);
            }
            delta = System.currentTimeMillis() - start;
        }
        else {
            b.putLong(0, Structure.FFIType.get(Pointer.class).peer);
            b.putLong(8, Structure.FFIType.get(int.class).peer);
            b.putLong(16, Structure.FFIType.get(long.class).peer);
            cif = Native.ffi_prep_cif(0, 3, Structure.FFIType.get(Pointer.class).peer, types);
            resp = pb.peer + 24;
            argv = pb.peer + 32;
            start = System.currentTimeMillis();
            for (int i=0;i < COUNT;i++) {
                b.putLong(32, pb.peer + 56);
                b.putLong(40, pb.peer + 64);
                b.putLong(48, pb.peer + 72);
                b.putLong(56, 0);
                b.putInt(64, 0);
                b.putLong(72, 0);
                Native.ffi_call(cif, f.peer, resp, argv);
                b.getLong(24);
            }
            delta = System.currentTimeMillis() - start;
        }
        System.out.println("memset (JNI ffi): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            Native.setMemory(Pointer.NULL, 0L, 0L, 0L, (byte)0);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("memset (JNI): " + delta + "ms");

        ///////////////////////////////////////////
        // strlen
        String str = "performance test";
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            int iresult = clib.strlen(str);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA interface): " + delta + "ms");

        f = NativeLibrary.getInstance(cname).getFunction("strlen");
        args = new Object[] { str };
        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            int iresult = f.invokeInt(args);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA function): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            int iresult = CLibrary.strlen(str);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA direct - String): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            int iresult = CLibrary.strlen(new NativeString(str).getPointer());
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA direct - Pointer): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            int iresult = CLibrary.strlen(Native.toByteArray(str));
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA direct - byte[]): " + delta + "ms");

        start = System.currentTimeMillis();
        for (int i=0;i < COUNT;i++) {
            byte[] bytes = str.getBytes();
            b.position(0);
            b.put(bytes);
            b.put((byte)0);
            int iresult = CLibrary.strlen(b);
        }
        delta = System.currentTimeMillis() - start;
        System.out.println("strlen (JNA direct - Buffer): " + delta + "ms");

        if (Native.POINTER_SIZE == 4) {
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
                int iresult = b.getInt(4);
            }
            delta = System.currentTimeMillis() - start;
        }
        else {
            b.putLong(0, Structure.FFIType.get(Pointer.class).peer);
            cif = Native.ffi_prep_cif(0, 1, Structure.FFIType.get(long.class).peer, types);
            resp = pb.peer + 8;
            argv = pb.peer + 16;
            start = System.currentTimeMillis();
            for (int i=0;i < COUNT;i++) {
                b.putLong(16, pb.peer + 24);
                b.putLong(24, pb.peer + 32);
                b.position(32);
                // This operation is very expensive!
                b.put(str.getBytes());
                b.put((byte)0);
                Native.ffi_call(cif, f.peer, resp, argv);
                long jresult = b.getLong(8);
            }
            delta = System.currentTimeMillis() - start;
        }
        System.out.println("strlen (JNI ffi): " + delta + "ms");

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

        ///////////////////////////////////////////
        // Callbacks
        TestInterface tlib = Native.load("testlib", TestInterface.class);
        start = System.currentTimeMillis();
        TestInterface.Int32Callback cb = new TestInterface.Int32Callback() {
            @Override
            public int invoke(int arg1, int arg2) {
                return arg1 + arg2;
            }
        };
        tlib.callInt32CallbackRepeatedly(cb, 1, 2, COUNT);
        delta = System.currentTimeMillis() - start;
        System.out.println("callback (JNA interface): " + delta + "ms");

        tlib = new TestLibrary();
        start = System.currentTimeMillis();
        tlib.callInt32CallbackRepeatedly(cb, 1, 2, COUNT);
        delta = System.currentTimeMillis() - start;
        System.out.println("callback (JNA direct): " + delta + "ms");

        start = System.currentTimeMillis();
        TestInterface.NativeLongCallback nlcb = new TestInterface.NativeLongCallback() {
            @Override
            public NativeLong invoke(NativeLong arg1, NativeLong arg2) {
                return new NativeLong(arg1.longValue() + arg2.longValue());
            }
        };
        tlib.callLongCallbackRepeatedly(nlcb, new NativeLong(1), new NativeLong(2), COUNT);
        delta = System.currentTimeMillis() - start;
        System.out.println("callback w/NativeMapped (JNA interface): " + delta + "ms");

        tlib = new TestLibrary();
        start = System.currentTimeMillis();
        tlib.callLongCallbackRepeatedly(nlcb, new NativeLong(1), new NativeLong(2), COUNT);
        delta = System.currentTimeMillis() - start;
        System.out.println("callback w/NativeMapped (JNA direct): " + delta + "ms");
    }
}

