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

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class DirectTypeMapperTest extends TestCase {

    private static final String UNICODE = "[\0444]";

    /** Converts boolean to int when going to native. */
    public static class DirectTestLibraryBoolean {
        final static int MAGIC = 0xABEDCF23;
        public native int returnInt32Argument(boolean b);
        static {
            Map options = new HashMap();
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addToNativeConverter(Boolean.class, new ToNativeConverter() {
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return new Integer(Boolean.TRUE.equals(arg) ? MAGIC : 0);
                }
                public Class nativeType() {
                    return Integer.class;
                }
            });
            options.put(Library.OPTION_TYPE_MAPPER, mapper);
            Native.register(NativeLibrary.getInstance("testlib", options));
        }
    }
    /** Converts String to int when going to native. */
    public static class DirectTestLibraryString {
        public native int returnInt32Argument(String s);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addToNativeConverter(String.class, new ToNativeConverter() {
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return Integer.valueOf((String) arg, 16);
                }
                public Class nativeType() {
                    return Integer.class;
                }
            });
            Map options = new HashMap();
            options.put(Library.OPTION_TYPE_MAPPER, mapper);
            Native.register(NativeLibrary.getInstance("testlib", options));
        }
    }
    /** Converts CharSequence to int when going to native. */
    public static class DirectTestLibraryCharSequence {
        public native int returnInt32Argument(String n);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addToNativeConverter(CharSequence.class, new ToNativeConverter() {
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return Integer.valueOf(((CharSequence)arg).toString(), 16);
                }
                public Class nativeType() {
                    return Integer.class;
                }
            });
            Map options = new HashMap();
            options.put(Library.OPTION_TYPE_MAPPER, mapper);
            
            Native.register(NativeLibrary.getInstance("testlib", options));
        }
    }
    /** Converts Number to int when going to native. */
    public static class DirectTestLibraryNumber {
        public native int returnInt32Argument(Number n);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addToNativeConverter(Number.class, new ToNativeConverter() {
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return new Integer(((Number)arg).intValue());
                }
                public Class nativeType() {
                    return Integer.class;
                }
            });
            Map options = new HashMap();
            options.put(Library.OPTION_TYPE_MAPPER, mapper);
            
            Native.register(NativeLibrary.getInstance("testlib", options));
        }
    }
    /** Converts String to WString and back. */
    public static class DirectTestLibraryWString {
        public native String returnWStringArgument(String s);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addTypeConverter(String.class, new TypeConverter() {
                public Object toNative(Object value, ToNativeContext ctx) {
                    if (value == null) {
                        return null;
                    }
                    return new WString(value.toString());
                }
                public Object fromNative(Object value, FromNativeContext context) {
                    if (value == null) {
                        return null;
                    }
                    return value.toString();
                }
                public Class nativeType() {
                    return WString.class;
                }
            });
            Map options = new HashMap();
            options.put(Library.OPTION_TYPE_MAPPER, mapper);
            
            Native.register(NativeLibrary.getInstance("testlib", options));
        }
    }
    public void testBooleanToIntArgumentConversion() {
        DirectTestLibraryBoolean lib = new DirectTestLibraryBoolean();
        assertEquals("Failed to convert Boolean argument to Int",
                     lib.MAGIC,
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
                     lib.returnInt32Argument(new Double(MAGIC)));
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
            Map options = new HashMap();
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            // Use opposite sense of default int<-->boolean conversions
            mapper.addToNativeConverter(Boolean.class, new ToNativeConverter() {
                public Object toNative(Object value, ToNativeContext ctx) {
                    return new Integer(Boolean.TRUE.equals(value) ? 0 : MAGIC);
                }
                public Class nativeType() {
                    return Integer.class;
                }
            });
            mapper.addFromNativeConverter(Boolean.class, new FromNativeConverter() {
                public Object fromNative(Object value, FromNativeContext context) {
                    return Boolean.valueOf(((Integer) value).intValue() != MAGIC);
                }
                public Class nativeType() { 
                    return Integer.class;
                }
            });
            options.put(Library.OPTION_TYPE_MAPPER, mapper);
            Native.register(NativeLibrary.getInstance("testlib", options));
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
            Map options = new HashMap();
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addTypeConverter(PointTestClass.class, new TypeConverter() {
                public Object fromNative(Object value, FromNativeContext context) {
                    Pointer p = (Pointer) value;
                    PointTestClass pc = new PointTestClass();
                    pc.x = p.getInt(0);
                    pc.y = p.getInt(4);
                    Native.free(Pointer.nativeValue(p));
                    return pc;
                }
                public Object toNative(Object value, ToNativeContext context) {
                    return Pointer.NULL; // dummy implementation (not called)
                }
                public Class nativeType() { 
                    return Pointer.class;
                }
            });
            options.put(Library.OPTION_TYPE_MAPPER, mapper);
            PointTestClass.TYPE_MAPPER = mapper;
            Native.register(NativeLibrary.getInstance("testlib", options));
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
                switch(code) {
                case 0: return STATUS_0;
                case 1: return STATUS_1;
                default: return STATUS_ERROR;
                }
            }
        }
        public native Enumeration returnInt32Argument(Enumeration e);
        static {
            DefaultTypeMapper mapper = new DefaultTypeMapper();
            mapper.addTypeConverter(Enumeration.class, new TypeConverter() {
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return new Integer(((Enumeration)arg).getCode());
                }
                public Object fromNative(Object value, FromNativeContext context) {
                    return Enumeration.fromCode(((Integer)value).intValue());
                }
                public Class nativeType() {
                    return Integer.class;
                }
            });
            Map options = new HashMap();
            options.put(Library.OPTION_TYPE_MAPPER, mapper);
            
            Native.register(NativeLibrary.getInstance("testlib", options));
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
