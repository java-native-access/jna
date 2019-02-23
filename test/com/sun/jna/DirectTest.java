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

import junit.framework.*;
import java.lang.reflect.Method;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@SuppressWarnings("unused")
public class DirectTest extends TestCase implements Paths {

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
        public DirectMapping(Map<String, ?> options) {
            Native.register(getClass(), NativeLibrary.getInstance("testlib", options));
        }
    }

    public void testGetOptionsForDirectMappingWithMemberInitializer() {
        Class<?>[] classes = {
            DirectMapping.class,
            DirectMapping.DirectStructure.class,
            DirectMapping.DirectCallback.class,
        };
        final TypeMapper mapper = new DefaultTypeMapper();
        final int alignment = Structure.ALIGN_NONE;
        final String encoding = System.getProperty("file.encoding");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        options.put(Library.OPTION_STRUCTURE_ALIGNMENT, alignment);
        options.put(Library.OPTION_STRING_ENCODING, encoding);
        DirectMapping lib = new DirectMapping(options);
        for (Class<?> cls : classes) {
            assertEquals("Wrong type mapper for direct mapping " + cls,
                         mapper, Native.getTypeMapper(cls));
            assertEquals("Wrong alignment for direct mapping " + cls,
                         alignment, Native.getStructureAlignment(cls));
            assertEquals("Wrong encoding for direct mapping " + cls,
                         encoding, Native.getStringEncoding(cls));
            Object last = Native.getLibraryOptions(cls);
            assertSame("Options not cached", last, Native.getLibraryOptions(cls));
        }
    }

    public static class DirectMappingStatic {
        final static TypeMapper TEST_MAPPER = new DefaultTypeMapper();
        final static int TEST_ALIGNMENT = Structure.ALIGN_DEFAULT;
        final static String TEST_ENCODING = System.getProperty("file.encoding");
        final static Map<String, Object> TEST_OPTIONS = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;    // we're not serializing it

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
        Class<?>[] classes = {
            DirectMappingStatic.class,
            DirectMappingStatic.DirectStructure.class,
            DirectMappingStatic.DirectCallback.class,
        };
        for (Class<?> cls : classes) {
            assertEquals("Wrong type mapper for direct mapping " + cls,
                         DirectMappingStatic.TEST_MAPPER, Native.getTypeMapper(cls));
            assertEquals("Wrong alignment for direct mapping " + cls,
                         DirectMappingStatic.TEST_ALIGNMENT, Native.getStructureAlignment(cls));
            assertEquals("Wrong encoding for direct mapping " + cls,
                         DirectMappingStatic.TEST_ENCODING, Native.getStringEncoding(cls));
            Object last = Native.getLibraryOptions(cls);
            assertSame("Options not cached", last, Native.getLibraryOptions(cls));
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

        try {
            Native.register(RemappedCLibrary.class,
                    NativeLibrary.getInstance(Platform.C_LIBRARY_NAME, Collections.singletonMap(Library.OPTION_FUNCTION_MAPPER, MAPPER)));
            final String VALUE = getName();
            int len;

            len = RemappedCLibrary.$$YJP$$strlen(VALUE);
            assertEquals("Mismatched YJP strlen value", VALUE.length(), len);

            len = RemappedCLibrary._prefixed_strlen(VALUE);
            assertEquals("Mismatched prefixed strlen value", VALUE.length(), len);
        } catch(Exception e) {
            fail("Native method was not properly mapped: " + e);
        }
    }

    public static class PointerNativeMapped implements NativeMapped {
        String nativeMethodName;
        @Override
        public PointerNativeMapped fromNative(Object nativeValue, FromNativeContext context) {
            nativeMethodName = ((MethodResultContext)context).getMethod().getName();
            return this;
        }
        @Override
        public Object toNative() {
            return null;
        }
        @Override
        public Class<?> nativeType() {
            return Pointer.class;
        }
    }
    public static class PointerTypeMapped {
        String nativeMethodName;
    }
    public static class FromNativeTests {
        static native PointerNativeMapped returnPointerArgument(PointerNativeMapped arg);
        static native PointerTypeMapped returnPointerArgument(PointerTypeMapped arg);
    }
    public void testDirectMappingFromNative() {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addTypeConverter(PointerTypeMapped.class, new TypeConverter() {
            @Override
            public PointerTypeMapped fromNative(Object nativeValue, FromNativeContext context) {
                PointerTypeMapped ret = new PointerTypeMapped();
                ret.nativeMethodName = ((MethodResultContext)context).getMethod().getName();
                return ret;
            }
            @Override
            public Object toNative(Object value, ToNativeContext context) {
                return null;
            }
            @Override
            public Class<?> nativeType() {
                return Pointer.class;
            }
        });
        NativeLibrary lib = NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        Native.register(FromNativeTests.class, lib);
        assertEquals("Failed to access MethodResultContext", "returnPointerArgument", FromNativeTests.returnPointerArgument(new PointerNativeMapped()).nativeMethodName);
        assertEquals("Failed to access MethodResultContext", "returnPointerArgument", FromNativeTests.returnPointerArgument(new PointerTypeMapped()).nativeMethodName);
    }
}

