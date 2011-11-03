/* Copyright (c) 2007-2009 Timothy Wall, All Rights Reserved
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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/** TODO: need more alignment tests, especially platform-specific behavior
 * @author twall@users.sf.net
 */
//@SuppressWarnings("unused")
public class StructureBufferFieldTest extends TestCase {

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(StructureBufferFieldTest.class);
    }

    static class BufferStructure extends Structure {
        public Buffer buffer;
        public DoubleBuffer dbuffer;
    }
    public void testBufferFieldWriteNULL() {
        if (!Platform.HAS_BUFFERS) return;
        BufferStructure bs = new BufferStructure();
        bs.write();
    }
    public void testBufferFieldWriteNonNULL() {
        if (!Platform.HAS_BUFFERS) return;
        BufferStructure bs = new BufferStructure();
        bs.buffer = ByteBuffer.allocateDirect(16);
        bs.dbuffer = ((ByteBuffer)bs.buffer).asDoubleBuffer();
        bs.write();
    }
    public void testBufferFieldReadUnchanged() {
        if (!Platform.HAS_BUFFERS) return;
        BufferStructure bs = new BufferStructure();
        Buffer b = ByteBuffer.allocateDirect(16);
        bs.buffer = b;
        bs.dbuffer = ((ByteBuffer)bs.buffer).asDoubleBuffer();
        bs.write();
        bs.read();
        assertEquals("Buffer field should be unchanged", b, bs.buffer);
    }
    public void testBufferFieldReadChanged() {
        if (!Platform.HAS_BUFFERS) return;
        BufferStructure bs = new BufferStructure();
        if (Pointer.SIZE == 4) {
            bs.getPointer().setInt(0, 0x1);
        }
        else {
            bs.getPointer().setLong(0, 0x1);
        }
        try {
            bs.read();
            fail("Structure read should fail if Buffer pointer was set");
        }
        catch(IllegalStateException e) {
        }
        bs.buffer = ByteBuffer.allocateDirect(16);
        try {
            bs.read();
            fail("Structure read should fail if Buffer pointer has changed");
        }
        catch(IllegalStateException e) {
        }
    }
    public void testBufferFieldReadChangedToNULL() {
        if (!Platform.HAS_BUFFERS) return;
        BufferStructure bs = new BufferStructure();
        bs.buffer = ByteBuffer.allocateDirect(16);
        bs.dbuffer = ((ByteBuffer)bs.buffer).asDoubleBuffer();
        bs.read();
        assertNull("Structure Buffer field should be set null", bs.buffer);
        assertNull("Structure DoubleBuffer field should be set null", bs.dbuffer);
    }
}
