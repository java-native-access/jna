/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import junit.framework.TestCase;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
//@SuppressWarnings("unused")
public class BufferArgumentsMarshalTest extends TestCase {

    public static interface TestLibrary extends Library {

        // ByteBuffer alternative definitions
        int fillInt8Buffer(ByteBuffer buf, int len, byte value);
        int fillInt16Buffer(ByteBuffer buf, int len, short value);
        int fillInt32Buffer(ByteBuffer buf, int len, int value);
        int fillInt64Buffer(ByteBuffer buf, int len, long value);
        int fillFloatBuffer(ByteBuffer buf, int len, float value);
        int fillDoubleBuffer(ByteBuffer buf, int len, double value);

        // {Short|Int|Long|,Float|Double}Buffer alternative definitions
        int fillInt16Buffer(ShortBuffer buf, int len, short value);
        int fillInt32Buffer(IntBuffer buf, int len, int value);
        int fillInt64Buffer(LongBuffer buf, int len, long value);
        int fillFloatBuffer(FloatBuffer buf, int len, float value);
        int fillDoubleBuffer(DoubleBuffer buf, int len, double value);
    }

    TestLibrary lib;
    @Override
    protected void setUp() {
        lib = Native.load("testlib", TestLibrary.class);
    }

    @Override
    protected void tearDown() {
        lib = null;
    }

    public void testByteBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocate(1024).order(ByteOrder.nativeOrder());
        final byte MAGIC = (byte)0xED;
        lib.fillInt8Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    public void testShortBufferArgument() {
        ShortBuffer buf  = ShortBuffer.allocate(1024);
        final short MAGIC = (short)0xABED;
        lib.fillInt16Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    public void testIntBufferArgument() {
        IntBuffer buf  = IntBuffer.allocate(1024);
        final int MAGIC = 0xABEDCF23;
        lib.fillInt32Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    public void testLongBufferArgument() {
        LongBuffer buf  = LongBuffer.allocate(1024);
        final long MAGIC = 0x1234567887654321L;
        lib.fillInt64Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }

    public void testDirectByteBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder());
        final byte MAGIC = (byte)0xED;
        lib.fillInt8Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
        buf.position(512);
        lib.fillInt8Buffer(buf, 512, (byte)0);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i,
                         i < 512 ? MAGIC : 0, buf.get(i));
        }
    }

    public void testDirectShortBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*2).order(ByteOrder.nativeOrder());
        ShortBuffer shortBuf = buf.asShortBuffer();
        final short MAGIC = (short)0xABED;
        lib.fillInt16Buffer(shortBuf, 1024, MAGIC);
        for (int i=0;i < shortBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, shortBuf.get(i));
        }
    }

    public void testDirectIntBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*4).order(ByteOrder.nativeOrder());
        IntBuffer intBuf = buf.asIntBuffer();
        final int MAGIC = 0xABEDCF23;
        lib.fillInt32Buffer(intBuf, 1024, MAGIC);
        for (int i=0;i < intBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, intBuf.get(i));
        }
    }

    public void testDirectLongBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*8).order(ByteOrder.nativeOrder());
        LongBuffer longBuf = buf.asLongBuffer();
        final long MAGIC = 0x1234567887654321L;
        lib.fillInt64Buffer(longBuf, 1024, MAGIC);
        for (int i=0;i < longBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, longBuf.get(i));
        }
    }

    public void testWrappedByteArrayArgument() {
        byte[] array = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(array, 512, 512);
        final byte MAGIC = (byte)0xAB;
        lib.fillInt8Buffer(buf, 512, MAGIC);
        for (int i=0;i < array.length;i++) {
            assertEquals("Bad value at index " + i,
                         i < 512 ? 0 : MAGIC, array[i]);
        }
    }
    public void testWrappedShortArrayArgument() {
        short[] array = new short[1024];
        ShortBuffer buf = ShortBuffer.wrap(array, 512, 512);
        final short MAGIC = (short)0xABED;
        lib.fillInt16Buffer(buf, 512, MAGIC);
        for (int i=0;i < array.length;i++) {
            assertEquals("Bad value at index " + i,
                         i < 512 ? 0 : MAGIC, array[i]);
        }
    }
    public void testWrappedIntArrayArgument() {
        int[] array = new int[1024];
        IntBuffer buf  = IntBuffer.wrap(array, 512, 512);
        final int MAGIC = 0xABEDCF23;
        lib.fillInt32Buffer(buf, 512, MAGIC);
        for (int i=0;i < array.length;i++) {
            assertEquals("Bad value at index " + i,
                         i < 512 ? 0 : MAGIC, array[i]);
        }
    }
    public void testWrappedLongArrayArguent() {
        long[] array = new long[1024];
        LongBuffer buf  = LongBuffer.wrap(array, 512, 512);
        final long MAGIC = 0x1234567887654321L;
        lib.fillInt64Buffer(buf, 512, MAGIC);
        for (int i=0;i < array.length;i++) {
            assertEquals("Bad value at index " + i,
                         i < 512 ? 0 : MAGIC, array[i]);
        }
    }
    public void testWrappedFloatArrayArguent() {
        float[] array = new float[1024];
        FloatBuffer buf  = FloatBuffer.wrap(array, 512, 512);
        final float MAGIC = -118.625f;
        lib.fillFloatBuffer(buf, 512, MAGIC);
        for (int i=0;i < array.length;i++) {
            assertEquals("Bad value at index " + i,
                         i < 512 ? 0 : MAGIC, array[i]);
        }
    }
    public void testWrappedDoubleArrayArguent() {
        double[] array = new double[1024];
        DoubleBuffer buf  = DoubleBuffer.wrap(array, 512, 512);
        final double MAGIC = -118.625;
        lib.fillDoubleBuffer(buf, 512, MAGIC);
        for (int i=0;i < array.length;i++) {
            assertEquals("Bad value at index " + i,
                         i < 512 ? 0 : MAGIC, array[i]);
        }
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(BufferArgumentsMarshalTest.class);
    }

}
