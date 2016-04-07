/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class MemoryTest extends TestCase implements GCWaits {

    public void testAutoFreeMemory() throws Exception {
        final boolean[] flag = { false };
        Memory core = new Memory(10) {
            @Override
            protected void finalize() {
                super.finalize();
                flag[0] = true;
            }
        };
        Pointer shared = core.share(0, 5);
        Reference<Memory> ref = new WeakReference<Memory>(core);

        core = null;
        System.gc();
        assertFalse("Memory prematurely GC'd", flag[0]);
        assertNotNull("Base memory GC'd while shared memory extant", ref.get());
        // Avoid having IBM J9 prematurely nullify "shared"
        shared.setInt(0, 0);

        shared = null;
        long start = System.currentTimeMillis();
        System.gc();
        Memory.purge();
        for (int i=0;i < GC_WAITS && ref.get() != null;i++) {
            Thread.sleep(GC_WAIT_INTERVAL);
            System.gc();
            Memory.purge();
        }
        long end = System.currentTimeMillis();
        assertNull("Memory not GC'd after " + (end - start) + " millis", ref.get());
    }

    public void testShareMemory() {
        Memory base = new Memory(8);
        Pointer shared = base.share(0);
        assertNotSame("Memory share should return a different object", base, shared);
    }

    public void testSharedMemoryBounds() {
        Memory base = new Memory(16);
        Pointer shared = base.share(4, 4);
        shared.getInt(-4);
        try {
            shared.getInt(-8);
            fail("Bounds check should fail");
        }
        catch(IndexOutOfBoundsException e) {
        }
        shared.getInt(8);
        try {
            shared.getInt(12);
            fail("Bounds check should fail");
        }
        catch(IndexOutOfBoundsException e) {
        }
    }

    public void testAlignment() {
        final int SIZE = 1<<16;
        Memory base = new Memory(SIZE);
        for (int align=1;align < SIZE;align *= 2) {
            Memory unaligned = base;
            long mask = ~((long)align - 1);
            if ((base.peer & mask) == base.peer)
                unaligned = (Memory)base.share(1, SIZE-1);
            Pointer aligned = unaligned.align(align);
            assertEquals("Memory not aligned (" + align + ")",
                         aligned.peer & mask, aligned.peer);

            assertSame("Alignment request on aligned memory should no-op",
                       aligned, ((Memory)aligned).align(align));
        }
    }

    public void testNegativeAlignment() {
        final int SIZE = 128;
        Memory base = new Memory(SIZE);
        try {
            base.align(-1);
            fail("Negative alignments not allowed");
        }
        catch(IllegalArgumentException e) { }
    }

    public void testInvalidAlignment() {
        final int SIZE = 128;
        Memory base = new Memory(SIZE);
        int[] alignments = { 0, 3, 5, 9, 13 };
        for (int i=0;i < alignments.length;i++) {
            try {
                base.align(alignments[i]);
                fail("Power-of-two alignments required");
            }
            catch(IllegalArgumentException e) { }
        }
    }

    public void testAvoidGCWithExtantBuffer() throws Exception {
        if (!Platform.HAS_BUFFERS) return;

        Memory m = new Memory(1024);
        m.clear();

        ByteBuffer b = m.getByteBuffer(0, m.size());
        Reference<Memory> ref = new WeakReference<Memory>(m);
        Reference<ByteBuffer> bref = new WeakReference<ByteBuffer>(b);

        // Create a second byte buffer "equal" to the first
        m = new Memory(1024);
        m.clear();
        m.getByteBuffer(0, m.size());

        m = null;
        System.gc();
        Memory.purge();
        for (int i=0;i < GC_WAITS && ref.get() != null;i++) {
            Thread.sleep(GC_WAIT_INTERVAL);
            System.gc();
            Memory.purge();
        }
        assertNotNull("Memory GC'd while NIO Buffer still exists", ref.get());

        // Avoid IBM J9 optimization resulting in premature GC of buffer
        b.put((byte)0);

        b = null;
        System.gc();
        Memory.purge();
        for (int i=0;i < GC_WAITS && (bref.get() != null || ref.get() != null);i++) {
            Thread.sleep(GC_WAIT_INTERVAL);
            System.gc();
            Memory.purge();
        }
        assertNull("Buffer not GC'd\n", bref.get());
        assertNull("Memory not GC'd after buffer GC'd\n", ref.get());
    }

    public void testDump() {
        // test with 15 bytes so last line has less than 4 bytes
        int n = 15;

        Memory m = new Memory(n);

        for (int i = 0; i < n; i++) {
            m.setByte(i, (byte) i);
        }

        String ls = System.getProperty("line.separator");

        assertEquals("memory dump" + ls +
            "[00010203]" + ls +
            "[04050607]" + ls +
            "[08090a0b]" + ls +
            "[0c0d0e]" + ls, m.dump());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MemoryTest.class);
    }
}
