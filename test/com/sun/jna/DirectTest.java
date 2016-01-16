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
import java.lang.reflect.Method;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@SuppressWarnings("unused")
public class DirectTest extends TestCase implements Paths, GCWaits {

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
        @Override
        public native int callInt32CallbackRepeatedly(Int32Callback cb, int arg1, int arg2, int count);
        @Override
        public native NativeLong callLongCallbackRepeatedly(NativeLongCallback cb, NativeLong arg1, NativeLong arg2, int count);
        static {
            Native.register("testlib");
        }
    }

    private static class TestLoader extends URLClassLoader {
        public TestLoader() throws MalformedURLException {
            this(null);
        }
        public TestLoader(ClassLoader parent) throws MalformedURLException {
            super(Platform.isWindowsCE()
                  ? new URL[] {
                      new File("/Storage Card/test.jar").toURI().toURL()
                  }
                  : new URL[] {
                      new File(BUILDDIR + "/classes").toURI().toURL(),
                      new File(BUILDDIR + "/test-classes").toURI().toURL(),
                  }, new CloverLoader(parent));
        }
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            String boot = System.getProperty("jna.boot.library.path");
            if (boot != null) {
                System.setProperty("jna.boot.library.path", "");
            }
            Class<?> cls = super.findClass(name);
            if (boot != null) {
                System.setProperty("jna.boot.library.path", boot);
            }
            return cls;
        }
    }

    public void testRegisterMethods() throws Exception {
        assertEquals("Math library call failed", 1., MathLibrary.cos(0), .01);
        assertTrue("Library not registered",
                   Native.registered(MathLibrary.class));
        Native.unregister(MathLibrary.class);
        assertFalse("Registered methods not unregistered",
                    Native.registered(MathLibrary.class));
    }

    private Class<?> returnCallingClass() {
        return Native.getCallingClass();
    }

    public void testFindCallingClass() {
        assertEquals("Wrong calling class detected",
                     getClass(), returnCallingClass());
    }

    public void testFindNativeClass() {
        class UnregisterLibrary {
            class Inner {
                public Class<?> findDirectMappedClass() {
                    return findDirectMappedClassInner();
                }
                public Class<?> findDirectMappedClassInner() {
                    return Native.findDirectMappedClass(Native.getCallingClass());
                };
            }
            public native double cos(double x);
            public Class<?> findDirectMappedClass() {
                return new Inner().findDirectMappedClass();
            };
        }
        assertEquals("Wrong native class found",
                     UnregisterLibrary.class, new UnregisterLibrary().findDirectMappedClass());
    }

    public static class DirectMapping {
        public static class DirectStructure extends Structure {
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        public static interface DirectCallback extends Callback {
            void invoke();
        }
        public DirectMapping(Map options) {
            Native.register(getClass(), NativeLibrary.getInstance("testlib", options));
        }
    }

    public void testGetOptionsForDirectMappingWithMemberInitializer() {
        Class[] classes = {
            DirectMapping.class,
            DirectMapping.DirectStructure.class,
            DirectMapping.DirectCallback.class,
        };
        final TypeMapper mapper = new DefaultTypeMapper();
        final int alignment = Structure.ALIGN_NONE;
        final String encoding = System.getProperty("file.encoding");
        Map options = new HashMap();
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        options.put(Library.OPTION_STRUCTURE_ALIGNMENT, alignment);
        options.put(Library.OPTION_STRING_ENCODING, encoding);
        DirectMapping lib = new DirectMapping(options);
        for (int i=0;i < classes.length;i++) {
            assertEquals("Wrong type mapper for direct mapping " + classes[i],
                         mapper, Native.getTypeMapper(classes[i]));
            assertEquals("Wrong alignment for direct mapping " + classes[i],
                         alignment, Native.getStructureAlignment(classes[i]));
            assertEquals("Wrong encoding for direct mapping " + classes[i],
                         encoding, Native.getStringEncoding(classes[i]));
            Object last = Native.getLibraryOptions(classes[i]);;
            assertSame("Options not cached", last, Native.getLibraryOptions(classes[i]));
        }
    }

    public static class DirectMappingStatic {
        final static TypeMapper TEST_MAPPER = new DefaultTypeMapper();
        final static int TEST_ALIGNMENT = Structure.ALIGN_DEFAULT;
        final static String TEST_ENCODING = System.getProperty("file.encoding");
        final static Map TEST_OPTIONS = new HashMap() {
            {
                put(Library.OPTION_TYPE_MAPPER, TEST_MAPPER);
                put(Library.OPTION_STRUCTURE_ALIGNMENT, TEST_ALIGNMENT);
                put(Library.OPTION_STRING_ENCODING, TEST_ENCODING);
            }
        };
        static {
            Native.register(DirectMappingStatic.class, NativeLibrary.getInstance("testlib", TEST_OPTIONS));
        }
        public static class DirectStructure extends Structure {
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("field");
            }
        }
        public static interface DirectCallback extends Callback {
            void invoke();
        }
    }

    public void testGetOptionsForDirectMappingWithStaticInitializer() {
        Class[] classes = {
            DirectMappingStatic.class,
            DirectMappingStatic.DirectStructure.class,
            DirectMappingStatic.DirectCallback.class,
        };
        for (int i=0;i < classes.length;i++) {
            assertEquals("Wrong type mapper for direct mapping " + classes[i],
                         DirectMappingStatic.TEST_MAPPER, Native.getTypeMapper(classes[i]));
            assertEquals("Wrong alignment for direct mapping " + classes[i],
                         DirectMappingStatic.TEST_ALIGNMENT, Native.getStructureAlignment(classes[i]));
            assertEquals("Wrong encoding for direct mapping " + classes[i],
                         DirectMappingStatic.TEST_ENCODING, Native.getStringEncoding(classes[i]));
            Object last = Native.getLibraryOptions(classes[i]);;
            assertSame("Options not cached", last, Native.getLibraryOptions(classes[i]));
        }
    }

    static class RemappedCLibrary {
        public static native int $$YJP$$strlen(String s);
        public static native int _prefixed_strlen(String s);
    }

    public void testDirectMappingFunctionMapper() {
        FunctionMapper MAPPER = new FunctionMapper() {
            @Override
            public String getFunctionName(NativeLibrary lib, Method method) {
                String name = method.getName();
                if (name.startsWith("_prefixed_")) {
                    return name.substring(10);
                }
                return name;
            }
        };
        Map options = new HashMap();
        options.put(Library.OPTION_FUNCTION_MAPPER, MAPPER);
        try {
            Native.register(RemappedCLibrary.class,
                            NativeLibrary.getInstance(Platform.C_LIBRARY_NAME, options));
            final String VALUE = getName();
            int len;

            len = RemappedCLibrary.$$YJP$$strlen(VALUE);
            assertEquals(VALUE.length(), len);

            len = RemappedCLibrary._prefixed_strlen(VALUE);
            assertEquals(VALUE.length(), len);
        }
        catch(Exception e) {
            fail("Native method was not properly mapped: " + e);
        }
    }
}

