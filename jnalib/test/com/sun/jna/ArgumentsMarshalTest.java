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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import junit.framework.TestCase;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class ArgumentsMarshalTest extends TestCase {

    public static interface TestLibrary extends Library {
        TestLibrary INSTANCE = (TestLibrary)
            Native.loadLibrary("testlib", TestLibrary.class);

        class CheckFieldAlignment extends Structure {
            public int int32Field = 1;
            public long int64Field = 2;
            public float floatField = 3f;
            public double doubleField = 4d;
        }

        boolean returnBooleanArgument(boolean arg);
        byte returnInt8Argument(byte arg);
        short returnInt16Argument(short arg);
        int returnInt32Argument(int i);
        long returnInt64Argument(long l);
        NativeLong returnLongArgument(NativeLong l);
        float returnFloatArgument(float f);
        double returnDoubleArgument(double d);
        String returnStringArgument(String s);
        WString returnWStringArgument(WString s);
        Pointer returnPointerArgument(Pointer p);
        long checkInt64ArgumentAlignment(int i, long j, int i2, long j2);
        double checkDoubleArgumentAlignment(float i, double j, float i2, double j2);
        int testSimpleStructurePointerArgument(CheckFieldAlignment p);
        void modifyStructureArray(CheckFieldAlignment[] p, int length);
        int fillInt8Buffer(byte[] buf, int len, byte value);
        int fillInt16Buffer(short[] buf, int len, short value);
        int fillInt32Buffer(int[] buf, int len, int value);
        int fillInt64Buffer(long[] buf, int len, long value);

        // ByteBuffer alternative definitions
        int fillInt8Buffer(ByteBuffer buf, int len, byte value);
        int fillInt16Buffer(ByteBuffer buf, int len, short value);
        int fillInt32Buffer(ByteBuffer buf, int len, int value);
        int fillInt64Buffer(ByteBuffer buf, int len, long value);
    }

    TestLibrary lib;
    protected void setUp() {
        lib = TestLibrary.INSTANCE;
    }
    
    protected void tearDown() {
        lib = null;
    }

    public void testBooleanArgument() {
        assertTrue("True argument should be returned", 
                   lib.returnBooleanArgument(true));
        assertFalse("False argument should be returned", 
                    lib.returnBooleanArgument(false));
    }

    public void testInt8Argument() {
        byte b = 0;
        assertEquals("Wrong value returned", 
                     b, lib.returnInt8Argument(b));
        b = 127;
        assertEquals("Wrong value returned", 
                     b, lib.returnInt8Argument(b));
        b = -128;
        assertEquals("Wrong value returned", 
                     b, lib.returnInt8Argument(b));
    }

    public void testInt16Argument() {
        short v = 0;
        assertEquals("Wrong value returned", 
                     v, lib.returnInt16Argument(v));
        v = 32767;
        assertEquals("Wrong value returned", 
                     v, lib.returnInt16Argument(v));
        v = -32768;
        assertEquals("Wrong value returned", 
                     v, lib.returnInt16Argument(v));
    }

    public void testIntArgument() {
        int value = 0;
        assertEquals("Should return 32-bit argument", 
                     value, lib.returnInt32Argument(value));
        value = 1;
        assertEquals("Should return 32-bit argument", 
                     value, lib.returnInt32Argument(value));
        value = 0x7FFFFFFF;
        assertEquals("Should return 32-bit argument", 
                     value, lib.returnInt32Argument(value));
        value = 0x80000000;
        assertEquals("Should return 32-bit argument", 
                     value, lib.returnInt32Argument(value));
    }

    public void testLongArgument() {
        long value = 0L;
        assertEquals("Should return 64-bit argument", 
                     value, lib.returnInt64Argument(value));
        value = 1L;
        assertEquals("Should return 64-bit argument", 
                     value, lib.returnInt64Argument(value));
        value = 0x7FFFFFFFL;
        assertEquals("Should return 64-bit argument", 
                     value, lib.returnInt64Argument(value));
        value = 0x80000000L;
        assertEquals("Should return 64-bit argument", 
                     value, lib.returnInt64Argument(value));
        value = 0x7FFFFFFF00000000L;
        assertEquals("Should return 64-bit argument", 
                     value, lib.returnInt64Argument(value));
        value = 0x8000000000000000L;
        assertEquals("Should return 64-bit argument", 
                     value, lib.returnInt64Argument(value));
    }

    public void testNativeLongArgument() {
        NativeLong value = new NativeLong(0);
        assertEquals("Should return 0", 
                     value, lib.returnLongArgument(value));
        value = new NativeLong(1);
        assertEquals("Should return 1", 
                     value, lib.returnLongArgument(value));
        value = new NativeLong(0x7FFFFFFF);
        assertEquals("Should return 0x7FFFFFFF", 
                     value, lib.returnLongArgument(value));
        value = new NativeLong(0x80000000);
        assertEquals("Should return 0x80000000", 
                     value, lib.returnLongArgument(value));
    }
    
    public void testPointerArgumentReturn() {
        assertEquals("Expect null pointer",
                     null, lib.returnPointerArgument(null));
        Structure s = new TestLibrary.CheckFieldAlignment();
        assertEquals("Expect structure pointer",
                     s.getPointer(), 
                     lib.returnPointerArgument(s.getPointer()));
    }

    final String MAGIC = "magic";
    public void testStringArgumentReturn() {
        assertEquals("Expect null pointer", null, lib.returnStringArgument(null));
        assertEquals("Expect string magic", MAGIC, lib.returnStringArgument(MAGIC));
    }

    public void testWStringArgumentReturn() {
        assertEquals("Expect null pointer", null, lib.returnStringArgument(null));
        assertEquals("Expect string magic", MAGIC, lib.returnStringArgument(MAGIC).toString());
    }
    
    public void testInt64ArgumentAlignment() {
        long value = lib.checkInt64ArgumentAlignment(0x10101010, 0x1111111111111111L, 
                                                     0x01010101, 0x2222222222222222L);
        assertEquals("Improper handling of interspersed int32/int64",
                     0x3333333344444444L, value);
    }

    public void testDoubleArgumentAlignment() {
        double value = lib.checkDoubleArgumentAlignment(1f, 2d, 3f, 4d);
        assertEquals("Improper handling of interspersed float/double",
                     10d, value, 0);
    }

    public void testSimpleStructurePointerArgument() {
        TestLibrary.CheckFieldAlignment aligned = new TestLibrary.CheckFieldAlignment();
        assertEquals("Structure data not properly initialized",
                     aligned.size(), 
                     lib.testSimpleStructurePointerArgument(aligned));
    }
    
    public void testUninitializedStructureArrayArgument() {
        final int LENGTH = 10;
        TestLibrary.CheckFieldAlignment[] block = 
            new TestLibrary.CheckFieldAlignment[LENGTH];
        lib.modifyStructureArray(block, block.length);
        for (int i=0;i < block.length;i++) {
            assertNotNull("Structure array not initialized at " + i, block[i]);
            assertEquals("Wrong value for int32 field of structure at " + i,
                         i, block[i].int32Field);
            assertEquals("Wrong value for int64 field of structure at " + i,
                         i + 1, block[i].int64Field);
            assertEquals("Wrong value for float field of structure at " + i,
                         i + 2, block[i].floatField, 0);
            assertEquals("Wrong value for double field of structure at " + i,
                         i + 3, block[i].doubleField, 0);
        }
    }

    public void testRejectNoncontiguousStructureArrayArgument() {
        TestLibrary.CheckFieldAlignment[] block = 
            new TestLibrary.CheckFieldAlignment[] {
            new TestLibrary.CheckFieldAlignment(),
            new TestLibrary.CheckFieldAlignment(),
        };
        try {
            lib.modifyStructureArray(block, block.length);
            fail("Library invocation should fail");
        }
        catch(IllegalArgumentException e) {
        }
    }
    
    public void testByteArrayArgument() {
        byte[] buf = new byte[1024];
        final byte MAGIC = (byte)0xED;
        assertEquals("Wrong return value", buf.length, +
                     lib.fillInt8Buffer(buf, buf.length, MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }
    
    public void testShortArrayArgument() {
        short[] buf = new short[1024];
        final short MAGIC = (short)0xABED;
        assertEquals("Wrong return value", buf.length, +
                     lib.fillInt16Buffer(buf, buf.length, MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }
    
    public void testIntArrayArgument() {
        int[] buf = new int[1024];
        final int MAGIC = 0xABEDCF23;
        assertEquals("Wrong return value", buf.length, +
                     lib.fillInt32Buffer(buf, buf.length, MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }
    
    public void testLongArrayArgument() {
        long[] buf = new long[1024];
        final long MAGIC = 0x1234567887654321L;
        assertEquals("Wrong return value", buf.length, +
                     lib.fillInt64Buffer(buf, buf.length, MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }
    
    public void testByteBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder());
        final byte MAGIC = (byte)0xED;
        lib.fillInt8Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    public void testShortBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*2).order(ByteOrder.nativeOrder());
        ShortBuffer shortBuf = buf.asShortBuffer();
        final short MAGIC = (short)0xABED;
        lib.fillInt16Buffer(buf, 1024, MAGIC);
        for (int i=0;i < shortBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, shortBuf.get(i));
        }
    }
    public void testIntBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*4).order(ByteOrder.nativeOrder());
        IntBuffer intBuf = buf.asIntBuffer();
        final int MAGIC = 0xABEDCF23;
        lib.fillInt32Buffer(buf, 1024, MAGIC);
        for (int i=0;i < intBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, intBuf.get(i));
        }
    }
    public void testLongBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*8).order(ByteOrder.nativeOrder());
        LongBuffer longBuf = buf.asLongBuffer();
        final long MAGIC = 0x1234567887654321L;
        lib.fillInt64Buffer(buf, 1024, MAGIC);
        for (int i=0;i < longBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, longBuf.get(i));
        }
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ArgumentsMarshalTest.class);
    }
    
}
