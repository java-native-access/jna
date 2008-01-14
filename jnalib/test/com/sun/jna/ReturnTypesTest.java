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
import com.sun.jna.ReturnTypesTest.TestLibrary.SimpleStructure;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class ReturnTypesTest extends TestCase {

    private static final double DOUBLE_MAGIC = -118.625d;
    private static final float FLOAT_MAGIC = -118.625f;

    public static interface TestLibrary extends Library {
        
        public static class SimpleStructure extends Structure {
            public double value;
        }
        
        public static class TestStructure extends Structure {
            public static class ByValue extends TestStructure implements Structure.ByValue { }
            
            public byte c;
            public short s;
            public int i;
            public long j;
            public SimpleStructure inner;
        }
        
        class CheckFieldAlignment extends Structure {
            public int int32Field = 1;
            public long int64Field = 2;
            public float floatField = 3f;
            public double doubleField = 4d;
        }

        Object returnStringArgument(String s);
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
        SimpleStructure returnStaticTestStructure();
        SimpleStructure returnNullTestStructure();
        TestStructure.ByValue returnStructureByValue();
        public interface Int32Callback extends Callback {
            public int callback(int arg);
        }
        Int32Callback returnCallback();
        Int32Callback returnCallbackArgument(Int32Callback cb);
    }

    TestLibrary lib;
    protected void setUp() {
        lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
    }
    
    protected void tearDown() {
        lib = null;
    }
    
    public void testReturnJavaObject() throws Exception {
        try {
            lib.returnStringArgument(getName());
            fail("Java Object return is not supported, should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            assertTrue("Exception should include return object type: " + e,
                       e.getMessage().indexOf("java.lang.Object") != -1);
        }
        catch(Throwable e) {
            fail("Method declared with Java Object return should throw IllegalArgumentException, not " + e);
        }
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

    static final String MAGIC = "magic";
    public void testInvokeString() {
        assertEquals("Expect string magic", MAGIC, lib.returnStringMagic());
    }
    
    public void testInvokeWString() {
        WString s = lib.returnWStringMagic();
        assertEquals("Expect wstring magic", new WString(MAGIC), s);
    }
    
    public void testInvokeStructure() {
        SimpleStructure s = lib.returnStaticTestStructure();
        assertEquals("Expect test structure magic", DOUBLE_MAGIC, s.value, 0d);
    }
    
    public void testInvokeNullStructure() {
        SimpleStructure s = lib.returnNullTestStructure();
        assertNull("Expect null structure return", s);
    }
    
    public void testInvokeCallback() {
        TestLibrary.Int32Callback cb = lib.returnCallback();
        assertNotNull("Callback should not be null", cb);
        assertEquals("Callback should be callable", 1, cb.callback(1));
        
        TestLibrary.Int32Callback cb2 = new TestLibrary.Int32Callback() {
            public int callback(int arg) {
                return 0;
            }
        };
        assertSame("Java callback should be looked up",
                   cb2, lib.returnCallbackArgument(cb2));
        assertSame("Existing native function wrapper should be reused",
                   cb, lib.returnCallbackArgument(cb));
    }
    
    public void testReturnStructureByValue() {
        TestStructure s = lib.returnStructureByValue();
        assertNotNull("Returned structure must not be null", s);
        assertEquals("Wrong char field value", 1, s.c);
        assertEquals("Wrong short field value", 2, s.s);
        assertEquals("Wrong int field value", 3, s.i);
        assertEquals("Wrong long field value", 4, s.j);

        assertNotNull("Structure not initialized", s.inner);
        assertEquals("Wrong inner structure value", 5, s.inner.value, 0);
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ReturnTypesTest.class);
    }
    
}
