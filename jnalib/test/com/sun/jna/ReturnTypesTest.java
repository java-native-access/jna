/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import junit.framework.TestCase;
import com.sun.jna.ReturnTypesTest.TestLibrary.TestStructure;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class ReturnTypesTest extends TestCase {

    private static final double DOUBLE_MAGIC = -118.625d;
    private static final float FLOAT_MAGIC = -118.625f;

    public static interface TestLibrary extends Library {
        
        public static class TestStructure extends Structure {
            public double value;
        }

        class CheckFieldAlignment extends Structure {
            public int int32Field = 1;
            public long int64Field = 2;
            public float floatField = 3f;
            public double doubleField = 4d;
        }

        boolean returnFalse();
        boolean returnTrue();
        int returnInt32Zero();
        int returnInt32Magic();
        long returnInt64Zero();
        long returnInt64Magic();
        NativeLong returnLongZero();
        NativeLong returnLongMagic();
        float returnFloatZero();
        float returnFloatMagic();
        double returnDoubleZero();
        double returnDoubleMagic();
        String returnStringMagic();
        WString returnWStringMagic();
        TestStructure returnStaticTestStructure();
        TestStructure returnNullTestStructure();
    }

    TestLibrary lib;
    protected void setUp() {
        lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
    }
    
    protected void tearDown() {
        lib = null;
    }

    public void testInvokeBoolean() {
        assertFalse("Expect false", lib.returnFalse());
        assertTrue("Expect true", lib.returnTrue());
    }

    public void testInvokeInt() {
        assertEquals("Expect 32-bit zero", 0, lib.returnInt32Zero());
        assertEquals("Expect 32-bit magic", 
                     "12345678", 
                     Integer.toHexString(lib.returnInt32Magic()));
    }

    public void testInvokeLong() {
        assertEquals("Expect 64-bit zero", 0L, lib.returnInt64Zero());
        assertEquals("Expect 64-bit magic", 
                     "123456789abcdef0", 
                     Long.toHexString(lib.returnInt64Magic()));
    }
    
    public void testInvokeNativeLong() {
        if (NativeLong.SIZE == 4) {
            assertEquals("Expect 32-bit zero", new NativeLong(0), lib.returnLongZero());
            assertEquals("Expect 32-bit magic", 
                         "12345678", 
                         Integer.toHexString(lib.returnLongMagic().intValue()));
                         
        } else {
            assertEquals("Expect 64-bit zero", new NativeLong(0L), 
                         lib.returnLongZero());
            assertEquals("Expect 64-bit magic", 
                         "123456789abcdef0", 
                         Long.toHexString(lib.returnLongMagic().longValue()));
        }
    }

    public interface NativeMappedLibrary extends Library {
        Custom returnInt32Argument(int arg);
    }
    public static class Custom implements NativeMapped {
        private int value;
        public Custom() { }
        public Custom(int value) {
            this.value = value;
        }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            return new Custom(((Integer)nativeValue).intValue());
        }
        public Class nativeType() {
            return Integer.class;
        }
        public Object toNative() {
            return new Integer(value);
        }
        public boolean equals(Object o) {
            return o instanceof Custom && ((Custom)o).value == value;
        }
    }
    public void testInvokeNativeMapped() {
        NativeMappedLibrary lib = (NativeMappedLibrary)
            Native.loadLibrary("testlib", NativeMappedLibrary.class);
        final int MAGIC = 0x12345678;
        final Custom EXPECTED = new Custom(MAGIC);
        assertEquals("Argument not mapped", EXPECTED, lib.returnInt32Argument(MAGIC));
    }

    public void testInvokeFloat() {
        assertEquals("Expect float zero", 0f, lib.returnFloatZero(), 0d);
        assertEquals("Expect float magic", 
                     FLOAT_MAGIC, lib.returnFloatMagic(), 0d);
    }

    public void testInvokeDouble() {
        assertEquals("Expect double zero", 0d, lib.returnDoubleZero(), 0d);
        assertEquals("Expect double magic", 
                     DOUBLE_MAGIC, lib.returnDoubleMagic(), 0d);
    }

    final String MAGIC = "magic";
    public void testInvokeString() {
        assertEquals("Expect string magic", MAGIC, lib.returnStringMagic());
    }
    
    public void testInvokeWString() {
        WString s = lib.returnWStringMagic();
        assertEquals("Expect wstring magic", new WString(MAGIC), s);
    }
    
    public void testInvokeStructure() {
        TestStructure s = lib.returnStaticTestStructure();
        assertEquals("Expect test structure magic", DOUBLE_MAGIC, s.value, 0d);
    }
    
    public void testInvokeNullStructure() {
        TestStructure s = lib.returnNullTestStructure();
        assertNull("Expect null structure return", s);
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ReturnTypesTest.class);
    }
    
}
