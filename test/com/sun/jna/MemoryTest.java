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


import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import junit.framework.TestCase;
import org.junit.Assert;

import static com.sun.jna.Native.POINTER_SIZE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;

public class MemoryTest extends TestCase {

    public void testAutoFreeMemory() throws Exception {
        Memory core = new Memory(10);
        Pointer shared = core.share(0, 5);
        Reference<Memory> ref = new WeakReference<Memory>(core);

        core = null;
        System.gc();
        assertNotNull("Base memory GC'd while shared memory extant", ref.get());
        // Avoid having IBM J9 prematurely nullify "shared"
        shared.setInt(0, 0);

        shared = null;
        long start = System.currentTimeMillis();
        System.gc();
        Memory.purge();
        for (int i=0;i < GCWaits.GC_WAITS && ref.get() != null;i++) {
            GCWaits.gcRun();
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
        for (int i=0;i < GCWaits.GC_WAITS && ref.get() != null;i++) {
            GCWaits.gcRun();
        }
        assertNotNull("Memory GC'd while NIO Buffer still exists", ref.get());

        // Avoid IBM J9 optimization resulting in premature GC of buffer
        b.put((byte)0);

        b = null;
        System.gc();
        Memory.purge();
        for (int i=0;i < GCWaits.GC_WAITS && (bref.get() != null || ref.get() != null);i++) {
            GCWaits.gcRun();
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

    public void testGetSharedMemory() {
        Memory mem1 = new Memory(512);
        Memory mem2 = new Memory(512);

        // Get pointers into the two memory objects
        Pointer stringStart1 = mem1.share(128);
        Pointer stringStart2 = mem2.share(128);

        mem1.setPointer(10 * POINTER_SIZE, stringStart1);
        mem1.setPointer(11 * POINTER_SIZE, stringStart2);

        // The pointer in mem1 at offset 10 * POINTER_SIZE points into the
        // memory region of mem1, while the pointer at offset 11 * POINTER_SIZE
        // points to the second memory region

        // It is expected, that resolution of the first pointer results in
        // an instance of SharedMemory (a subclass of Memory, that retains a
        // reference on the originating Memory object)
        Assert.assertThat(mem1.getPointer(10 * POINTER_SIZE), instanceOf(Pointer.class));
        Assert.assertThat(mem1.getPointer(10 * POINTER_SIZE), instanceOf(Memory.class));
        // The second pointer lies outside of memory 1, so it must not be a
        // Memory object, but a raw pointer
        Assert.assertThat(mem1.getPointer(11 * POINTER_SIZE), instanceOf(Pointer.class));
        Assert.assertThat(mem1.getPointer(11 * POINTER_SIZE), not(instanceOf(Memory.class)));

        // It is expected, that Memory#read called for pointers shows the same
        // behaviour as direct calls to getPointer calls with the corresponding
        // offsets
        Pointer[] pointers = new Pointer[2];
        mem1.read(10 * POINTER_SIZE, pointers, 0, 2);

        Assert.assertThat(pointers[0], instanceOf(Pointer.class));
        Assert.assertThat(pointers[0], instanceOf(Memory.class));
        Assert.assertThat(pointers[1], instanceOf(Pointer.class));
        Assert.assertThat(pointers[1], not(instanceOf(Memory.class)));
    }

    public void testBoundsChecking() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Test the bounds checking of the Memory#read invocations
        testBoundsCheckArray(byte.class, 1);
        testBoundsCheckArray(char.class, Native.WCHAR_SIZE);
        testBoundsCheckArray(short.class, 2);
        testBoundsCheckArray(int.class, 4);
        testBoundsCheckArray(long.class, 8);
        testBoundsCheckArray(float.class, 4);
        testBoundsCheckArray(double.class, 8);
        testBoundsCheckArray(Pointer.class, Native.POINTER_SIZE);
        // Test the bounds checking of the Memory#get* / Memory#set* methods
        testBoundsCheckSingleValue(byte.class, "Byte", 1, (byte) 42);
        testBoundsCheckSingleValue(char.class, "Char", Native.WCHAR_SIZE, 'x');
        testBoundsCheckSingleValue(short.class, "Short", 2, (short) 42);
        testBoundsCheckSingleValue(int.class, "Int", 4, 42);
        testBoundsCheckSingleValue(long.class, "Long", 8, 42L);
        testBoundsCheckSingleValue(float.class, "Float", 4, 42f);
        testBoundsCheckSingleValue(double.class, "Double", 8, 42d);
        testBoundsCheckSingleValue(Pointer.class, "Pointer", Native.POINTER_SIZE, Pointer.createConstant(42L));
    }

    private void testBoundsCheckSingleValue(Class clazz, String name, int elementSize, Object value) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method readMethod = Memory.class.getMethod("get" + name, new Class[]{long.class});
        Method writeMethod = Memory.class.getMethod("set" + name, new Class[]{long.class, clazz});

        Memory mem = new Memory(elementSize * 10);
        readMethod.invoke(mem, elementSize * 0);
        readMethod.invoke(mem, elementSize * 8);
        try {
            readMethod.invoke(mem, elementSize * -1);
            fail("Negative offset was read");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
        try {
            readMethod.invoke(mem, elementSize * 10 - (elementSize / 2));
            fail("Value lies half outside the memory location");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
        try {
            readMethod.invoke(mem, elementSize * 20);
            fail("Read outsize allocated memory");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }

        writeMethod.invoke(mem, elementSize * 0, value);
        writeMethod.invoke(mem, elementSize * 8, value);
        try {
            writeMethod.invoke(mem, elementSize * -1, value);
            fail("Negative offset was read");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
        try {
            writeMethod.invoke(mem, elementSize * 10 - (elementSize / 2), value);
            fail("Value lies half outside the memory location");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
        try {
            writeMethod.invoke(mem, elementSize * 20, value);
            fail("Write outsize allocated memory");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
    }

    private void testBoundsCheckArray(Class componentClass, int elementSize) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object javaArray = Array.newInstance(componentClass, 2);
        Method readMethod = Memory.class.getMethod("read", new Class[]{long.class, javaArray.getClass(), int.class, int.class});
        Method writeMethod = Memory.class.getMethod("write", new Class[]{long.class, javaArray.getClass(), int.class, int.class});

        Memory mem = new Memory(elementSize * 10);

        readMethod.invoke(mem, elementSize * 0, javaArray, 0, 2);
        readMethod.invoke(mem, elementSize * 8, javaArray, 0, 2);
        try {
            readMethod.invoke(mem, elementSize * -1, javaArray, 0, 2);
            fail("Negative offset was read");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
        try {
            readMethod.invoke(mem, elementSize * 9, javaArray, 0, 2);
            fail("Half of the array contents layed outside the allocated area");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
        try {
            readMethod.invoke(mem, elementSize * 20, javaArray, 0, 2);
            fail("Array contents layed completely outside the allocated area");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }

        writeMethod.invoke(mem, 0, javaArray, 0, 2);
        writeMethod.invoke(mem, elementSize * 8, javaArray, 0, 2);
        try {
            writeMethod.invoke(mem, elementSize * -1, javaArray, 0, 2);
            fail("Negative offset was read");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
        try {
            writeMethod.invoke(mem, elementSize * 9, javaArray, 0, 2);
            fail("Half of the array contents layed outside the allocated area");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
        try {
            writeMethod.invoke(mem, elementSize * 20, javaArray, 0, 2);
            fail("Array contents layed completely outside the allocated area");
        } catch (InvocationTargetException ex) {
            checkCauseInstance(ex, IndexOutOfBoundsException.class);
        }
    }

    private void checkCauseInstance(Exception ex, Class<? extends Exception> expectedClazz) {
        Assert.assertThat(ex.getCause(), instanceOf(expectedClazz));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MemoryTest.class);
    }
}
