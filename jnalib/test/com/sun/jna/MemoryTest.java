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

import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class MemoryTest extends TestCase {
    public void testAutoFreeMemory() throws Exception {
        final boolean[] flag = { false };
        Memory core = new Memory(10) {
            protected void finalize() {
                super.finalize();
                flag[0] = true;
            }
        };
        Pointer shared = core.share(0, 5);
        WeakReference ref = new WeakReference(core);
        
        core = null;
        System.gc();
        long start = System.currentTimeMillis();
        assertFalse("Memory prematurely GC'd", flag[0]);
        assertNotNull("Base memory GC'd while shared memory extant", ref.get());
        // Avoid having IBM J9 prematurely nullify "shared"
        shared.setInt(0, 0);

        shared = null;
        System.gc();
        while (ref.get() != null) {
            if (System.currentTimeMillis() - start > 5000)
                break;
            Thread.sleep(10);
        }
        assertNull("Memory not GC'd", ref.get());
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
        final int SIZE = 128;
        Memory base = new Memory(SIZE);
        for (int align=1;align < 8;align *= 2) {
            Memory unaligned = base;
            long mask = ~((long)align - 1);
            if ((base.peer & mask) == base.peer)
                unaligned = (Memory)base.share(1, SIZE-1);
            Pointer aligned = unaligned.align(align);
            assertEquals("Memory not aligned",
                         aligned.peer & mask, aligned.peer);
        }
        try {
            base.align(-1);
            fail("Negative alignments not allowed");
        }
        catch(IllegalArgumentException e) { }
    }

    public void testAvoidGCWithExtantBuffer() throws Exception {
        Memory m = new Memory(1024);
        ByteBuffer b = m.getByteBuffer(0, m.getSize());
        WeakReference ref = new WeakReference(m);
        WeakReference bref = new WeakReference(b);
        m = null;
        System.gc();
        Memory.purge();
        for (int i=0;i < 100 && ref.get() != null;i++) {
            Thread.sleep(10);
            System.gc();
            Memory.purge();
        }
        assertNotNull("Memory GC'd while NIO Buffer still extant", ref.get());

        // Avoid IBM J9 optimization resulting in premature GC of buffer
        b.put((byte)0);

        b = null;
        System.gc();
        Memory.purge();
        for (int i=0;i < 100 && (bref.get() != null || ref.get() != null);i++) {
            Thread.sleep(10);
            System.gc();
            Memory.purge();
        }
        assertNull("Buffer not GC'd\n", bref.get());
        assertNull("Memory not GC'd after buffer GC'd\n", ref.get());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MemoryTest.class);
    }
}
