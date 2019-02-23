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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import junit.framework.TestCase;


public class PointerBufferTest extends TestCase {

    private static final String UNICODE = "[\u0444]";

    public void testByteBufferPutString() throws Exception {
        final String MAGIC = "magic" + UNICODE;
        final String ENCODING = "utf8";
        Memory m = new Memory(1024);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        buf.put(MAGIC.getBytes(ENCODING)).put((byte) 0).flip();
        assertEquals("String not written to memory", MAGIC,
                     m.getString(0, ENCODING));
    }
    public void testByteBufferPutByte() {
        final byte MAGIC = (byte)0xED;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        buf.put(MAGIC).flip();
        assertEquals("Byte not written to memory", MAGIC,
                m.getByte(0));
    }
    public void testByteBufferPutInt() {
        final int MAGIC = 0xABEDCF23;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        buf.putInt(MAGIC).flip();
        assertEquals("Int not written to memory", MAGIC,
                m.getInt(0));
    }
    public void testByteBufferPutLong() {
        final long MAGIC = 0x1234567887654321L;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        buf.putLong(MAGIC).flip();
        assertEquals("Long not written to memory", MAGIC,
                m.getLong(0));
    }
    public void testByteBufferGetByte() {
        final byte MAGIC = (byte)0xED;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        m.setByte(0, MAGIC);
        assertEquals("Byte not read from memory", MAGIC,
                buf.get(0));
    }
    public void testByteBufferGetInt() {
        final int MAGIC = 0xABEDCF23;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        m.setInt(0, MAGIC);
        assertEquals("Int not read from memory", MAGIC,
                buf.getInt(0));
    }
    public void testByteBufferGetLong() {
        final long MAGIC = 0x1234567887654321L;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        m.setLong(0, MAGIC);
        assertEquals("Long not read from memory", MAGIC,
                buf.getLong(0));
    }
    public void testIntBufferPut() {
        final int MAGIC = 0xABEDCF23;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        IntBuffer ib = buf.asIntBuffer();
        ib.put(MAGIC).flip();
        assertEquals("Int not written to memory", MAGIC,
                m.getInt(0));
    }
    public void testLongBufferPut() {
        final long MAGIC = 0x1234567887654321L;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        LongBuffer lb = buf.asLongBuffer();
        lb.put(MAGIC).flip();
        assertEquals("Long not written to memory", MAGIC,
                m.getLong(0));
    }
    public void testFloatBufferPut() {
        final float MAGIC = 1234.5678f;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        FloatBuffer fb = buf.asFloatBuffer();
        fb.put(MAGIC).flip();
        assertEquals("Int not written to memory", MAGIC, m.getFloat(0), 0f);
    }
    public void testDoubleBufferPut() {
        final double MAGIC = 1234.5678;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        DoubleBuffer db = buf.asDoubleBuffer();
        db.put(MAGIC).flip();
        assertEquals("Int not written to memory", MAGIC, m.getDouble(0), 0d);
    }
    public void testIntBufferGet() {
        final int MAGIC = 0xABEDCF23;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        IntBuffer ib = buf.asIntBuffer();
        m.setInt(0, MAGIC);
        assertEquals("Int not read from memory", MAGIC,
                ib.get(0));
    }
    public void testLongBufferGet() {
        final long MAGIC = 0x1234567887654321L;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        LongBuffer lb = buf.asLongBuffer();
        m.setLong(0, MAGIC);
        assertEquals("Long not read from memory", MAGIC,
                lb.get(0));
    }
    public void testFloatBufferGet() {
        final float MAGIC = 1234.5678f;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        FloatBuffer fb = buf.asFloatBuffer();
        m.setFloat(0, MAGIC);
        assertEquals("Float not read from memory", MAGIC, fb.get(0), 0f);
    }
    public void testDoubleBufferGet() {
        final double MAGIC = 1234.5678;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.size()).order(ByteOrder.nativeOrder());
        DoubleBuffer db = buf.asDoubleBuffer();
        m.setDouble(0, MAGIC);
        assertEquals("Double not read from memory", MAGIC, db.get(0), 0d);
    }
    public void testDirectBufferPointer() throws Exception {
        Pointer p = new Memory(1024);
        ByteBuffer b = p.getByteBuffer(0, 1024);
        assertEquals("ByteBuffer Pointer does not match",
                     p, Native.getDirectBufferPointer(b));
        assertEquals("ShortBuffer Pointer does not match",
                     p, Native.getDirectBufferPointer(b.asShortBuffer()));
        assertEquals("IntBuffer Pointer does not match",
                     p, Native.getDirectBufferPointer(b.asIntBuffer()));
        assertEquals("LongBuffer Pointer does not match",
                     p, Native.getDirectBufferPointer(b.asLongBuffer()));
        assertEquals("FloatBuffer Pointer does not match",
                     p, Native.getDirectBufferPointer(b.asFloatBuffer()));
        assertEquals("DoubleBuffer Pointer does not match",
                     p, Native.getDirectBufferPointer(b.asDoubleBuffer()));

        assertEquals("Wrong direct buffer address",
                     p, Native.getDirectBufferPointer(b));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PointerBufferTest.class);
    }
}
