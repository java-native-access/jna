/*
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
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A <code>Pointer</code> to memory obtained from the native heap via a
 * call to <code>malloc</code>.
 *
 * <p>In some cases it might be necessary to use memory obtained from
 * <code>malloc</code>.  For example, <code>Memory</code> helps
 * accomplish the following idiom:
 * <pre>
 *        void *buf = malloc(BUF_LEN * sizeof(char));
 *        call_some_function(buf);
 *        free(buf);
 * </pre>
 *
 * <p>The {@link #finalize} method will free allocated memory when
 * this object is no longer referenced.
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @author Timothy Wall
 * @see Pointer
 */
public class Memory extends Pointer {
    /** Keep track of all allocated memory so we can dispose of it before unloading. */
    private static final Map<Memory, Reference<Memory>> allocatedMemory =
            Collections.synchronizedMap(new WeakHashMap<Memory, Reference<Memory>>());

    private static final WeakMemoryHolder buffers = new WeakMemoryHolder();

    /** Force cleanup of memory that has associated NIO Buffers which have
        been GC'd.
    */
    public static void purge() {
        buffers.clean();
    }

    /** Dispose of all allocated memory. */
    public static void disposeAll() {
        // use a copy since dispose() modifies the map
        Collection<Memory> refs = new LinkedList<Memory>(allocatedMemory.keySet());
        for (Memory r : refs) {
            r.dispose();
        }
    }

    protected long size; // Size of the malloc'ed space

    /** Provide a view into the original memory.  Keeps an implicit reference
     * to the original to prevent GC.
     */
    private class SharedMemory extends Memory {
        public SharedMemory(long offset, long size) {
            this.size = size;
            this.peer = Memory.this.peer + offset;
        }
        /** No need to free memory. */
        @Override
        protected synchronized void dispose() {
            this.peer = 0;
        }
        /** Pass bounds check to parent. */
        @Override
        protected void boundsCheck(long off, long sz) {
            Memory.this.boundsCheck(this.peer - Memory.this.peer + off, sz);
        }
        @Override
        public String toString() {
            return super.toString() + " (shared from " + Memory.this.toString() + ")";
        }
    }

    /**
     * Allocate space in the native heap via a call to C's <code>malloc</code>.
     *
     * @param size number of <em>bytes</em> of space to allocate
     */
    public Memory(long size) {
        this.size = size;
        if (size <= 0) {
            throw new IllegalArgumentException("Allocation size must be greater than zero");
        }
        peer = malloc(size);
        if (peer == 0)
            throw new OutOfMemoryError("Cannot allocate " + size + " bytes");

        allocatedMemory.put(this, new WeakReference<Memory>(this));
    }

    protected Memory() {
        super();
    }

    /** Provide a view of this memory using the given offset as the base address.  The
     * returned {@link Pointer} will have a size equal to that of the original
     * minus the offset.
     * @throws IndexOutOfBoundsException if the requested memory is outside
     * the allocated bounds.
     */
    @Override
    public Pointer share(long offset) {
        return share(offset, size() - offset);
    }

    /** Provide a view of this memory using the given offset as the base
     * address, bounds-limited with the given size.  Maintains a reference to
     * the original {@link Memory} object to avoid GC as long as the shared
     * memory is referenced.
     * @throws IndexOutOfBoundsException if the requested memory is outside
     * the allocated bounds.
     */
    @Override
    public Pointer share(long offset, long sz) {
        boundsCheck(offset, sz);
        return new SharedMemory(offset, sz);
    }

    /** Provide a view onto this structure with the given alignment.
     * @param byteBoundary Align memory to this number of bytes; should be a
     * power of two.
     * @throws IndexOutOfBoundsException if the requested alignment can
     * not be met.
     * @throws IllegalArgumentException if the requested alignment is not
     * a positive power of two.
     */
    public Memory align(int byteBoundary) {
        if (byteBoundary <= 0) {
            throw new IllegalArgumentException("Byte boundary must be positive: " + byteBoundary);
        }
        for (int i=0;i < 32;i++) {
            if (byteBoundary == (1<<i)) {
                long mask = ~((long)byteBoundary - 1);

                if ((peer & mask) != peer) {
                    long newPeer = (peer + byteBoundary - 1) & mask;
                    long newSize = peer + size - newPeer;
                    if (newSize <= 0) {
                        throw new IllegalArgumentException("Insufficient memory to align to the requested boundary");
                    }
                    return (Memory)share(newPeer - peer, newSize);
                }
                return this;
            }
        }
        throw new IllegalArgumentException("Byte boundary must be a power of two");
    }

    /** Properly dispose of native memory when this object is GC'd. */
    @Override
    protected void finalize() {
        dispose();
    }

    /** Free the native memory and set peer to zero */
    protected synchronized void dispose() {
        try {
            free(peer);
        } finally {
            allocatedMemory.remove(this);
            peer = 0;
        }
    }

    /** Zero the full extent of this memory region. */
    public void clear() {
        clear(size);
    }

    /** Returns false if the memory has been freed. */
    public boolean valid() {
        return peer != 0;
    }

    public long size() {
        return size;
    }

    /**
     * Check that indirection won't cause us to write outside the
     * malloc'ed space.
     *
     */
    protected void boundsCheck(long off, long sz) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("Invalid offset: " + off);
        }
        if (off + sz > size) {
            String msg = "Bounds exceeds available space : size="
                + size + ", offset=" + (off + sz);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Raw read methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(long,byte[],int,int)
     */
    @Override
    public void read(long bOff, byte[] buf, int index, int length) {
        boundsCheck(bOff, length * 1L);
        super.read(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(long,short[],int,int)
     */
    @Override
    public void read(long bOff, short[] buf, int index, int length) {
        boundsCheck(bOff, length * 2L);
        super.read(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(long,char[],int,int)
     */
    @Override
    public void read(long bOff, char[] buf, int index, int length) {
        boundsCheck(bOff, length * 2L);
        super.read(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(long,int[],int,int)
     */
    @Override
    public void read(long bOff, int[] buf, int index, int length) {
        boundsCheck(bOff, length * 4L);
        super.read(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(long,long[],int,int)
     */
    @Override
    public void read(long bOff, long[] buf, int index, int length) {
        boundsCheck(bOff, length * 8L);
        super.read(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(long,float[],int,int)
     */
    @Override
    public void read(long bOff, float[] buf, int index, int length) {
        boundsCheck(bOff, length * 4L);
        super.read(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds checks to
     * ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(long,double[],int,int)
     */
    @Override
    public void read(long bOff, double[] buf, int index, int length) {
        boundsCheck(bOff, length * 8L);
        super.read(bOff, buf, index, length);
    }

    //////////////////////////////////////////////////////////////////////////
    // Raw write methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(long,byte[],int,int)
     */
    @Override
    public void write(long bOff, byte[] buf, int index, int length) {
        boundsCheck(bOff, length * 1L);
        super.write(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(long,short[],int,int)
     */
    @Override
    public void write(long bOff, short[] buf, int index, int length) {
        boundsCheck(bOff, length * 2L);
        super.write(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(long,char[],int,int)
     */
    @Override
    public void write(long bOff, char[] buf, int index, int length) {
        boundsCheck(bOff, length * 2L);
        super.write(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(long,int[],int,int)
     */
    @Override
    public void write(long bOff, int[] buf, int index, int length) {
        boundsCheck(bOff, length * 4L);
        super.write(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(long,long[],int,int)
     */
    @Override
    public void write(long bOff, long[] buf, int index, int length) {
        boundsCheck(bOff, length * 8L);
        super.write(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(long,float[],int,int)
     */
    @Override
    public void write(long bOff, float[] buf, int index, int length) {
        boundsCheck(bOff, length * 4L);
        super.write(bOff, buf, index, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(long,double[],int,int)
     */
    @Override
    public void write(long bOff, double[] buf, int index, int length) {
        boundsCheck(bOff, length * 8L);
        super.write(bOff, buf, index, length);
    }

    //////////////////////////////////////////////////////////////////////////
    // Java type read methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getByte</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getByte(long)
     */
    @Override
    public byte getByte(long offset) {
        boundsCheck(offset, 1);
        return super.getByte(offset);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getByte</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getByte(long)
     */
    @Override
    public char getChar(long offset) {
        boundsCheck(offset, 1);
        return super.getChar(offset);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getShort</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getShort(long)
     */
    @Override
    public short getShort(long offset) {
        boundsCheck(offset, 2);
        return super.getShort(offset);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getInt</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getInt(long)
     */
    @Override
    public int getInt(long offset) {
        boundsCheck(offset, 4);
        return super.getInt(offset);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getLong</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getLong(long)
     */
    @Override
    public long getLong(long offset) {
        boundsCheck(offset, 8);
        return super.getLong(offset);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getFloat</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getFloat(long)
     */
    @Override
    public float getFloat(long offset) {
        boundsCheck(offset, 4);
        return super.getFloat(offset);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getDouble</code>.  But this method performs a
     * bounds check to ensure that the indirection does not cause memory
     * outside the <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getDouble(long)
     */
    @Override
    public double getDouble(long offset) {
        boundsCheck(offset, 8);
        return super.getDouble(offset);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getPointer</code>.  But this method performs
     * a bounds checks to ensure that the indirection does not cause memory
     * outside the <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getPointer(long)
     */
    @Override
    public Pointer getPointer(long offset) {
        boundsCheck(offset, Native.POINTER_SIZE);
        return super.getPointer(offset);
    }

    /**
     * Get a ByteBuffer mapped to a portion of this memory.
     * We keep a weak reference to all ByteBuffers provided so that this
     * memory object is not GC'd while there are still implicit outstanding
     * references to it (it'd be nice if we could attach our own reference to
     * the ByteBuffer, but the VM generates the object so we have no control
     * over it).
     *
     * @param offset byte offset from pointer to start the buffer
     * @param length Length of ByteBuffer
     * @return a direct ByteBuffer that accesses the memory being pointed to,
     */
    @Override
    public ByteBuffer getByteBuffer(long offset, long length) {
        boundsCheck(offset, length);
        ByteBuffer b = super.getByteBuffer(offset, length);
        // Ensure this Memory object will not be GC'd (and its memory freed)
        // if the Buffer is still extant.
        buffers.put(b, this);
        return b;
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getString</code>. But this method performs a bounds checks to
     * ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     * <p>
     * The encoding used is obtained from {@link Native#getDefaultStringEncoding()}.
     *
     * @param offset
     *            byte offset from pointer to start reading bytes
     * @return the <code>String</code> value being pointed to, up to either a null
     *         terminator or the end of allocated memory.
     */
    @Override
    public String getString(long offset) {
        return getString(offset, Native.getDefaultStringEncoding());
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getString</code>. But this method performs a bounds checks to
     * ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @return the <code>String</code> value being pointed to, up to either a null
     *         terminator or the end of allocated memory.
     * @see Pointer#getString(long, int, String)
     */
    @Override
    public String getString(long offset, String encoding) {
        // NOTE: we only make sure the start of the string is within bounds
        boundsCheck(offset, 0);
        // Call super limiting to remaining allocated memory
        return super.getString(offset, (int) (size() - offset), encoding);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getWideString</code>. But this method performs a bounds checks
     * to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @return the <code>String</code> value being pointed to, up to either a null
     *         terminator or the end of allocated memory.
     * @see Pointer#getWideString(long, int)
     */
    @Override
    public String getWideString(long offset) {
        // NOTE: we only make sure the start of the string is within bounds
        boundsCheck(offset, 0);
        // Call super limiting to remaining allocated memory
        return super.getWideString(offset, (int) (size() - offset));
    }

    //////////////////////////////////////////////////////////////////////////
    // Java type write methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setByte</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setByte
     */
    @Override
    public void setByte(long offset, byte value) {
        boundsCheck(offset, 1);
        super.setByte(offset, value);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setChar</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setChar
     */
    @Override
    public void setChar(long offset, char value) {
        boundsCheck(offset, Native.WCHAR_SIZE);
        super.setChar(offset, value);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setShort</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setShort
     */
    @Override
    public void setShort(long offset, short value) {
        boundsCheck(offset, 2);
        super.setShort(offset, value);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setInt</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setInt
     */
    @Override
    public void setInt(long offset, int value) {
        boundsCheck(offset, 4);
        super.setInt(offset, value);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setLong</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setLong
     */
    @Override
    public void setLong(long offset, long value) {
        boundsCheck(offset, 8);
        super.setLong(offset, value);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setFloat</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setFloat
     */
    @Override
    public void setFloat(long offset, float value) {
        boundsCheck(offset, 4);
        super.setFloat(offset, value);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setDouble</code>.  But this method performs a
     * bounds checks to ensure that the indirection does not cause memory
     * outside the <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setDouble
     */
    @Override
    public void setDouble(long offset, double value) {
        boundsCheck(offset, 8);
        super.setDouble(offset, value);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setPointer</code>.  But this method performs
     * a bounds checks to ensure that the indirection does not cause memory
     * outside the <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setPointer
     */
    @Override
    public void setPointer(long offset, Pointer value) {
        boundsCheck(offset, Native.POINTER_SIZE);
        super.setPointer(offset, value);
    }

    @Override
    public void setString(long offset, String value, String encoding) {
        boundsCheck(offset, Native.getBytes(value, encoding).length + 1L);
        super.setString(offset, value, encoding);
    }

    @Override
    public void setWideString(long offset, String value) {
        boundsCheck(offset, (value.length() + 1L) * Native.WCHAR_SIZE);
        super.setWideString(offset, value);
    }

    @Override
    public String toString() {
        return "allocated@0x" + Long.toHexString(peer) + " (" + size + " bytes)";
    }

    protected static void free(long p) {
        // free(0) is a no-op, so avoid the overhead of the call
        if (p != 0) {
            Native.free(p);
        }
    }

    protected static long malloc(long size) {
        return Native.malloc(size);
    }

    /** Dumps the contents of this memory object. */
    public String dump() {
        return dump(0, (int)size());
    }
}
