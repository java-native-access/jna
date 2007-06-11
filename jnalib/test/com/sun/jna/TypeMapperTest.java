/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

public class TypeMapperTest extends TestCase {
    public static interface TestLibrary extends Library {
        int returnInt32Argument(boolean b);
        int returnInt32Argument(String s);
        int returnInt32Argument(Number n);
    }
    public TypeMapperTest(String testName) {
        super(testName);
    }
    public void testBooleanToIntArgumentConversion() {
        final int MAGIC = 0xABEDCF23;
        Map options = new HashMap();
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addArgumentConverter(Boolean.class, new ArgumentConverter() {
            public Object toNative(Object arg) {
                return new Integer(Boolean.TRUE.equals(arg) ? MAGIC : 0);
            }
        });
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        TestLibrary lib = (TestLibrary) 
            Native.loadLibrary("testlib", TestLibrary.class, options);
        assertEquals("Failed to convert Boolean argument to Int", MAGIC,
                     lib.returnInt32Argument(true));
    }
    public void testStringToIntArgumentConversion() {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addArgumentConverter(String.class, new ArgumentConverter() {
            public Object toNative(Object arg) {
                return Integer.valueOf((String) arg, 16);
            }
        });
        Map options = new HashMap();
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        final int MAGIC = 0x7BEDCF23;
        TestLibrary lib = (TestLibrary) 
            Native.loadLibrary("testlib", TestLibrary.class, options);
        assertEquals("Failed to convert String argument to Int", MAGIC,
                     lib.returnInt32Argument(Integer.toHexString(MAGIC)));
    }
    public void testCharSequenceToIntArgumentConversion() {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addArgumentConverter(CharSequence.class, new ArgumentConverter() {
            public Object toNative(Object arg) {
                return Integer.valueOf(((CharSequence)arg).toString(), 16);
            }
        });
        Map options = new HashMap();
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        
        final int MAGIC = 0x7BEDCF23;
        TestLibrary lib = (TestLibrary) 
            Native.loadLibrary("testlib", TestLibrary.class, options);
        assertEquals("Failed to convert String argument to Int", MAGIC,
                     lib.returnInt32Argument(Integer.toHexString(MAGIC)));
    }
    public void testNumberToIntArgumentConversion() {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addArgumentConverter(Double.class, new ArgumentConverter() {
            public Object toNative(Object arg) {
                return new Integer(((Double)arg).intValue());
            }
        });
        Map options = new HashMap();
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        
        final int MAGIC = 0x7BEDCF23;
        TestLibrary lib = (TestLibrary) 
            Native.loadLibrary("testlib", TestLibrary.class, options);
        assertEquals("Failed to convert Double argument to Int", MAGIC,
                     lib.returnInt32Argument(new Double(MAGIC)));
    }
    public static interface BooleanTestLibrary extends Library {
        boolean returnInt32Argument(boolean b);
    }
    public void testIntegerToBooleanResultConversion() throws Exception {
        final int MAGIC = 0xABEDCF23;
        Map options = new HashMap();
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        mapper.addArgumentConverter(Boolean.class, new ArgumentConverter() {
            public Object toNative(Object value) {
                return new Integer(Boolean.TRUE.equals(value) ? MAGIC : 0);
            }
        });
        mapper.addResultConverter(Boolean.class, new ResultConverter() {
            public Object fromNative(Object value, ResultContext context) {
                return Boolean.valueOf(((Integer) value).intValue() == MAGIC);
            }
            public Class nativeType() { 
                return Integer.class;
            }
        });
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        BooleanTestLibrary lib = (BooleanTestLibrary) 
            Native.loadLibrary("testlib", BooleanTestLibrary.class, options);
        assertEquals("Failed to convert integer return to boolean TRUE", true,
                     lib.returnInt32Argument(true));
        assertEquals("Failed to convert integer return to boolean FALSE", false,
                     lib.returnInt32Argument(false));
    }
    public static interface StructureTestLibrary extends Library {
        public static class TestStructure extends Structure {
            public TestStructure(TypeMapper mapper) {
                setTypeMapper(mapper);
            }
            public boolean data;
        }
    }
    public void testStructureConversion() throws Exception {
        DefaultTypeMapper mapper = new DefaultTypeMapper();
        TypeConverter converter = new TypeConverter() {
            public Object toNative(Object value) {
                return new Integer(Boolean.TRUE.equals(value) ? 1 : 0);
            }
            public Object fromNative(Object value, ResultContext context) {
                return new Boolean(((Integer)value).intValue() == 1);
            }
            public Class nativeType() {
                return Integer.class;
            }
        };
        mapper.addArgumentConverter(Boolean.class, converter);
        mapper.addResultConverter(Boolean.class, converter);
        Map options = new HashMap();
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        StructureTestLibrary lib = (StructureTestLibrary)
            Native.loadLibrary("testlib", StructureTestLibrary.class, options);
        StructureTestLibrary.TestStructure s = new StructureTestLibrary.TestStructure(mapper);
        assertEquals("Wrong native size", 4, s.size());
        
        s.data = true;
        s.write();
        assertEquals("Wrong value written", 1, s.getPointer().getInt(0));
        
        s.getPointer().setInt(0, 0);
        s.read();
        assertFalse("Wrong value read", s.data);
    }
}
