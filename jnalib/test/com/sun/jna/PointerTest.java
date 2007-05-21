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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import junit.framework.TestCase;
import com.sun.jna.Memory;


public class PointerTest extends TestCase {
    
    public void testByteBufferPutString() {
        final String MAGIC = "magic";
        Memory m = new Memory(1024);
        ByteBuffer buf = m.getByteBuffer(0, m.getSize()).order(ByteOrder.nativeOrder());
        buf.put(MAGIC.getBytes()).put((byte) 0).flip();
        assertEquals("String not written to memory", MAGIC, 
                m.getString(0, false));
    }
    public void testByteBufferPutByte() {
        final byte MAGIC = (byte)0xED;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.getSize()).order(ByteOrder.nativeOrder());
        buf.put(MAGIC).flip();
        assertEquals("Byte not written to memory", MAGIC, 
                m.getByte(0));
    }
    public void testByteBufferPutInt() {
        final int MAGIC = 0xABEDCF23;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.getSize()).order(ByteOrder.nativeOrder());
        buf.putInt(MAGIC).flip();
        assertEquals("Int not written to memory", MAGIC, 
                m.getInt(0));
    }
    public void testByteBufferPutLong() {
        final long MAGIC = 0x1234567887654321L;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.getSize()).order(ByteOrder.nativeOrder());
        buf.putLong(MAGIC).flip();
        assertEquals("Long not written to memory", MAGIC, 
                m.getLong(0));
    }
    public void testByteBufferGetByte() {
        final byte MAGIC = (byte)0xED;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.getSize()).order(ByteOrder.nativeOrder());
        m.setByte(0, MAGIC);
        assertEquals("Byte not read from memory", MAGIC, 
                buf.get(0));
    }
    public void testByteBufferGetInt() {
        final int MAGIC = 0xABEDCF23;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.getSize()).order(ByteOrder.nativeOrder());
        m.setInt(0, MAGIC);
        assertEquals("Int not read from memory", MAGIC, 
                buf.getInt(0));
    }
    public void testByteBufferGetLong() {
        final long MAGIC = 0x1234567887654321L;
        Memory m = new Memory(8);
        ByteBuffer buf = m.getByteBuffer(0, m.getSize()).order(ByteOrder.nativeOrder());
        m.setLong(0, MAGIC);
        assertEquals("Long not read from memory", MAGIC, 
                buf.getLong(0));
    }
    public void testGetNativeLong() {
        Memory m = new Memory(8);
        if (NativeLong.SIZE == 4) {
            final int MAGIC = 0xABEDCF23;
            m.setInt(0, MAGIC);
            NativeLong l = m.getNativeLong(0);
            assertEquals("Native long mismatch", MAGIC, l.intValue());
        } else {
            final long MAGIC = 0x1234567887654321L;
            m.setLong(0, MAGIC);
            NativeLong l = m.getNativeLong(0);
            assertEquals("Native long mismatch", MAGIC, l.longValue());
        }
    }
    public void testSetNativeLong() {
        Memory m = new Memory(8);
        if (NativeLong.SIZE == 4) {
            final int MAGIC = 0xABEDCF23;
            m.setNativeLong(0, new NativeLong(MAGIC));
            assertEquals("Native long mismatch", MAGIC, m.getInt(0));
        } else {
            final long MAGIC = 0x1234567887654321L;
            m.setNativeLong(0, new NativeLong(MAGIC));
            assertEquals("Native long mismatch", MAGIC, m.getLong(0));
        }
    }
}
