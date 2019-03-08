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

import java.util.Collections;
import junit.framework.TestCase;

public class DirectTypeMapperTest extends TestCase {

    private static final String UNICODE = "[\0444]";

    /** Converts boolean to int when going to native. */
    public static class DirectTestLibraryBoolean {
        final static int MAGIC = 0xABEDCF23;
        public native int returnInt32Argument(boolean b);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addToNativeConverter(Boolean.class, new ToNativeConverter() {
                @Override
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return Integer.valueOf(Boolean.TRUE.equals(arg) ? MAGIC : 0);
                }
                @Override
                public Class<?> nativeType() {
                    return Integer.class;
                }
            });

            Native.register(NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper)));
        }
    }
    /** Converts String to int when going to native. */
    public static class DirectTestLibraryString {
        public native int returnInt32Argument(String s);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addToNativeConverter(String.class, new ToNativeConverter() {
                @Override
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return Integer.valueOf((String) arg, 16);
                }
                @Override
                public Class<?> nativeType() {
                    return Integer.class;
                }
            });
            Native.register(NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper)));
        }
    }
    /** Converts CharSequence to int when going to native. */
    public static class DirectTestLibraryCharSequence {
        public native int returnInt32Argument(String n);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addToNativeConverter(CharSequence.class, new ToNativeConverter() {
                @Override
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return Integer.valueOf(((CharSequence)arg).toString(), 16);
                }
                @Override
                public Class<?> nativeType() {
                    return Integer.class;
                }
            });
            Native.register(NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper)));
        }
    }
    /** Converts Number to int when going to native. */
    public static class DirectTestLibraryNumber {
        public native int returnInt32Argument(Number n);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addToNativeConverter(Number.class, new ToNativeConverter() {
                @Override
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return Integer.valueOf(((Number)arg).intValue());
                }
                @Override
                public Class<?> nativeType() {
                    return Integer.class;
                }
            });
            Native.register(NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper)));
        }
    }
    /** Converts String to WString and back. */
    public static class DirectTestLibraryWString {
        public native String returnWStringArgument(String s);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addTypeConverter(String.class, new TypeConverter() {
                @Override
                public Object toNative(Object value, ToNativeContext ctx) {
                    if (value == null) {
                        return null;
                    }
                    return new WString(value.toString());
                }
                @Override
                public Object fromNative(Object value, FromNativeContext context) {
                    if (value == null) {
                        return null;
                    }
                    return value.toString();
                }
                @Override
                public Class<?> nativeType() {
                    return WString.class;
                }
            });
            Native.register(NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper)));
        }
    }
    public void testBooleanToIntArgumentConversion() {
        DirectTestLibraryBoolean lib = new DirectTestLibraryBoolean();
        assertEquals("Failed to convert Boolean argument to Int",
                     DirectTestLibraryBoolean.MAGIC,
                     lib.returnInt32Argument(true));
    }
    public void testStringToIntArgumentConversion() {
        final int MAGIC = 0x7BEDCF23;
        DirectTestLibraryString lib = new DirectTestLibraryString();
        assertEquals("Failed to convert String argument to Int", MAGIC,
                     lib.returnInt32Argument(Integer.toHexString(MAGIC)));
    }
    public void testCharSequenceToIntArgumentConversion() {
        final int MAGIC = 0x7BEDCF23;
        DirectTestLibraryCharSequence lib = new DirectTestLibraryCharSequence();
        assertEquals("Failed to convert String argument to Int", MAGIC,
                     lib.returnInt32Argument(Integer.toHexString(MAGIC)));
    }
    public void testNumberToIntArgumentConversion() {

        final int MAGIC = 0x7BEDCF23;
        DirectTestLibraryNumber lib = new DirectTestLibraryNumber();
        assertEquals("Failed to convert Double argument to Int", MAGIC,
                     lib.returnInt32Argument(Double.valueOf(MAGIC)));
    }
    public void testStringToWStringArgumentConversion() {
        final String MAGIC = "magic" + UNICODE;
        DirectTestLibraryWString lib = new DirectTestLibraryWString();
        assertEquals("Failed to convert String argument to WString", MAGIC,
                     lib.returnWStringArgument(MAGIC));
    }

    /** Uses a type mapper to convert boolean->int and int->boolean */
    public static class DirectTestLibraryBidirectionalBoolean {
        public native boolean returnInt32Argument(boolean b);
        static {
            final int MAGIC = 0xABEDCF23;
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            // Use opposite sense of default int<-->boolean conversions
            mapper.addToNativeConverter(Boolean.class, new ToNativeConverter() {
                @Override
                public Object toNative(Object value, ToNativeContext ctx) {
                    return Integer.valueOf(Boolean.TRUE.equals(value) ? 0 : MAGIC);
                }
                @Override
                public Class<?> nativeType() {
                    return Integer.class;
                }
            });
            mapper.addFromNativeConverter(Boolean.class, new FromNativeConverter() {
                @Override
                public Object fromNative(Object value, FromNativeContext context) {
                    return Boolean.valueOf(((Integer) value).intValue() != MAGIC);
                }
                @Override
                public Class<?> nativeType() {
                    return Integer.class;
                }
            });
            Native.register(NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper)));
        }
    }
    public void testIntegerToBooleanResultConversion() throws Exception {
        DirectTestLibraryBidirectionalBoolean lib = new DirectTestLibraryBidirectionalBoolean();
        // argument "true" converts to zero; result zero converts to "true"
        assertTrue("Failed to convert integer return to boolean TRUE",
                   lib.returnInt32Argument(true));
        // argument "true" converts to MAGIC; result MAGIC converts to "false"
        assertFalse("Failed to convert integer return to boolean FALSE",
                    lib.returnInt32Argument(false));
    }
    public static class PointTestClass {
        public static TypeMapper TYPE_MAPPER;
        int x, y;
    }
    public static class DirectTypeMappedResultTypeTestLibrary {
        public native PointTestClass returnPoint(int x, int y);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addTypeConverter(PointTestClass.class, new TypeConverter() {
                @Override
                public Object fromNative(Object value, FromNativeContext context) {
                    Pointer p = (Pointer) value;
                    PointTestClass pc = new PointTestClass();
                    pc.x = p.getInt(0);
                    pc.y = p.getInt(4);
                    Native.free(Pointer.nativeValue(p));
                    return pc;
                }
                @Override
                public Object toNative(Object value, ToNativeContext context) {
                    return Pointer.NULL; // dummy implementation (not called)
                }
                @Override
                public Class<?> nativeType() {
                    return Pointer.class;
                }
            });

            PointTestClass.TYPE_MAPPER = mapper;
            Native.register(NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper)));
        }
    }
    public void testTypeMapperResultTypeConversion() throws Exception {
        DirectTypeMappedResultTypeTestLibrary lib = new DirectTypeMappedResultTypeTestLibrary();
        PointTestClass p = lib.returnPoint(1234, 5678);
        assertEquals("Failed to convert int* return to java.awt.Point", 1234, p.x);
        assertEquals("Failed to convert int* return to java.awt.Point", 5678, p.y);
    }
    public static class DirectTypeMappedEnumerationTestLibrary {
        public static enum Enumeration {
            STATUS_0(0), STATUS_1(1), STATUS_ERROR(-1);
            private final int code;
            Enumeration(int code) { this.code = code; }
            public int getCode() { return code; }
            public static Enumeration fromCode(int code) {
                switch (code) {
                    case 0:
                        return STATUS_0;
                    case 1:
                        return STATUS_1;
                    default:
                        return STATUS_ERROR;
                }
            }
        }
        public native Enumeration returnInt32Argument(Enumeration e);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addTypeConverter(Enumeration.class, new TypeConverter() {
                @Override
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return Integer.valueOf(((Enumeration)arg).getCode());
                }
                @Override
                public Object fromNative(Object value, FromNativeContext context) {
                    return Enumeration.fromCode(((Integer)value).intValue());
                }
                @Override
                public Class<?> nativeType() {
                    return Integer.class;
                }
            });
            Native.register(NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper)));
        }
    }
    public void testEnumerationConversion() {
        DirectTypeMappedEnumerationTestLibrary lib = new DirectTypeMappedEnumerationTestLibrary();
        DirectTypeMappedEnumerationTestLibrary.Enumeration e = lib.returnInt32Argument(DirectTypeMappedEnumerationTestLibrary.Enumeration.STATUS_1);
        assertEquals("Failed to convert enumeration", DirectTypeMappedEnumerationTestLibrary.Enumeration.STATUS_1, e);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DirectTypeMapperTest.class);
    }
}
