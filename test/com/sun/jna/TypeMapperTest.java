/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;

//@SuppressWarnings("unused")
public class TypeMapperTest extends TestCase {

    private static final String UNICODE = "[\0444]";

    public static interface TestLibrary extends Library {
        int returnInt32Argument(boolean b);
        int returnInt32Argument(String s);
        int returnInt32Argument(Number n);
        WString returnWStringArgument(String s);
        String returnWStringArgument(WString s);
    }

    public void testBooleanToIntArgumentConversion() {
        final int MAGIC = 0xABEDCF23;
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
        TestLibrary lib = Native.load("testlib", TestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Failed to convert Boolean argument to Int", MAGIC, lib.returnInt32Argument(true));
    }
    public void testStringToIntArgumentConversion() {
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
        final int MAGIC = 0x7BEDCF23;
        TestLibrary lib = Native.load("testlib", TestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Failed to convert String argument to Int", MAGIC,
                     lib.returnInt32Argument(Integer.toHexString(MAGIC)));
    }
    public void testStringToWStringArgumentConversion() {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addToNativeConverter(String.class, new ToNativeConverter() {
            @Override
            public Object toNative(Object arg, ToNativeContext ctx) {
                return new WString(arg.toString());
            }
            @Override
            public Class<?> nativeType() {
                return WString.class;
            }
        });
        final String MAGIC = "magic" + UNICODE;
        TestLibrary lib = Native.load("testlib", TestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Failed to convert String argument to WString", new WString(MAGIC),
                     lib.returnWStringArgument(MAGIC));
    }
    public void testCharSequenceToIntArgumentConversion() {
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
        final int MAGIC = 0x7BEDCF23;
        TestLibrary lib = Native.load("testlib", TestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Failed to convert String argument to Int", MAGIC, lib.returnInt32Argument(Integer.toHexString(MAGIC)));
    }
    public void testNumberToIntArgumentConversion() {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addToNativeConverter(Double.class, new ToNativeConverter() {
            @Override
            public Object toNative(Object arg, ToNativeContext ctx) {
                return Integer.valueOf(((Double)arg).intValue());
            }
            @Override
            public Class<?> nativeType() {
                return Integer.class;
            }
        });

        final int MAGIC = 0x7BEDCF23;
        TestLibrary lib = Native.load("testlib", TestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Failed to convert Double argument to Int", MAGIC,
                     lib.returnInt32Argument(Double.valueOf(MAGIC)));
    }
    public void testWStringToStringResultConversion() throws Exception {
        final String MAGIC = "magic" + UNICODE;
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addFromNativeConverter(String.class, new FromNativeConverter() {
            @Override
            public Object fromNative(Object value, FromNativeContext ctx) {
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
        TestLibrary lib = Native.load("testlib", TestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Failed to convert WString result to String", MAGIC, lib.returnWStringArgument(new WString(MAGIC)));
    }

    public static interface BooleanTestLibrary extends Library {
        boolean returnInt32Argument(boolean b);
    }
    public void testIntegerToBooleanResultConversion() throws Exception {
        final int MAGIC = 0xABEDCF23;
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addToNativeConverter(Boolean.class, new ToNativeConverter() {
            @Override
            public Object toNative(Object value, ToNativeContext ctx) {
                return Integer.valueOf(Boolean.TRUE.equals(value) ? MAGIC : 0);
            }
            @Override
            public Class<?> nativeType() {
                return Integer.class;
            }
        });
        mapper.addFromNativeConverter(Boolean.class, new FromNativeConverter() {
            @Override
            public Object fromNative(Object value, FromNativeContext context) {
                return Boolean.valueOf(((Integer) value).intValue() == MAGIC);
            }
            @Override
            public Class<?> nativeType() {
                return Integer.class;
            }
        });
        BooleanTestLibrary lib = Native.load("testlib", BooleanTestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Failed to convert integer return to boolean TRUE", true, lib.returnInt32Argument(true));
        assertEquals("Failed to convert integer return to boolean FALSE", false, lib.returnInt32Argument(false));
    }

    public static interface StructureTestLibrary extends Library {
        public static class TestStructure extends Structure {
            public TestStructure(TypeMapper mapper) {
                super(mapper);
            }
            public boolean data;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("data");
            }
        }
    }
    public void testStructureConversion() throws Exception {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        TypeConverter converter = new TypeConverter() {
            @Override
            public Object toNative(Object value, ToNativeContext ctx) {
                return Integer.valueOf(Boolean.TRUE.equals(value) ? 1 : 0);
            }
            @Override
            public Object fromNative(Object value, FromNativeContext context) {
                return Boolean.valueOf(((Integer)value).intValue() == 1);
            }
            @Override
            public Class<?> nativeType() {
                return Integer.class;
            }
        };
        mapper.addTypeConverter(Boolean.class, converter);
        Native.load("testlib", StructureTestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        StructureTestLibrary.TestStructure s = new StructureTestLibrary.TestStructure(mapper);
        assertEquals("Wrong native size", 4, s.size());

        s.data = true;
        s.write();
        assertEquals("Wrong value written", 1, s.getPointer().getInt(0));

        s.getPointer().setInt(0, 0);
        s.read();
        assertFalse("Wrong value read", s.data);
    }

    public static enum Enumeration {
        STATUS_0(0), STATUS_1(1), STATUS_ERROR(-1);
        private final int code;
        Enumeration(int code) { this.code = code; }
        public int getCode() { return code; }
        public static Enumeration fromCode(int code) {
            switch(code) {
                case 0:
                    return STATUS_0;
                case 1:
                    return STATUS_1;
                default:
                    return STATUS_ERROR;
            }
        }
    }

    public static interface EnumerationTestLibrary extends Library {
        Enumeration returnInt32Argument(Enumeration arg);

        @Structure.FieldOrder({"field"})
        class MinTestStructure extends Structure {
            public Enumeration field;
        }
        MinTestStructure testStructurePointerArgument(MinTestStructure s);
    }

    public void testEnumConversion() throws Exception {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        TypeConverter converter = new TypeConverter() {
            @Override
            public Object toNative(Object value, ToNativeContext ctx) {
                if(value == null) {
                    // NULL needs to be explicityl handled for size calculation
                    // in structure use
                    return Enumeration.STATUS_ERROR.getCode();
                } else {
                    return ((Enumeration)value).getCode();
                }
            }
            @Override
            public Object fromNative(Object value, FromNativeContext context) {
                return Enumeration.fromCode(((Integer)value));
            }
            @Override
            public Class<?> nativeType() {
                return Integer.class;
            }
        };

        mapper.addTypeConverter(Enumeration.class, converter);
        EnumerationTestLibrary lib = Native.load("testlib", EnumerationTestLibrary.class, Collections.singletonMap(Library.OPTION_TYPE_MAPPER, mapper));
        assertEquals("Enumeration improperly converted", Enumeration.STATUS_0, lib.returnInt32Argument(Enumeration.STATUS_0));
        assertEquals("Enumeration improperly converted", Enumeration.STATUS_1, lib.returnInt32Argument(Enumeration.STATUS_1));
        EnumerationTestLibrary.MinTestStructure struct = new EnumerationTestLibrary.MinTestStructure();
        struct.field = Enumeration.STATUS_0;
        assertEquals("Enumeration in structure improperly converted", Enumeration.STATUS_0, lib.testStructurePointerArgument(struct).field);
        struct.field = Enumeration.STATUS_1;
        assertEquals("Enumeration in structure improperly converted", Enumeration.STATUS_1, lib.testStructurePointerArgument(struct).field);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TypeMapperTest.class);
    }
}
