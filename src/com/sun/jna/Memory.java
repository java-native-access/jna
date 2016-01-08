/* This library is free software; you can redistribute it and/or
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

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A <code>Pointer</code> to memory obtained from the native heap via a 
 * call to <code>malloc</code>.
 *
 * <p>In some cases it might be necessary to use memory obtained from
 * <code>malloc</code>.  For example, <code>Memory</code> helps 
 * accomplish the following idiom:
 * <pre>
 * 		void *buf = malloc(BUF_LEN * sizeof(char));
 *		call_some_function(buf);
 *		free(buf);
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

    private static final Map buffers;
    /** Keep track of all allocated memory so we can dispose of it before unloading. */
    private static final Map allocatedMemory;

    static {
        buffers = Collections.synchronizedMap(Platform.HAS_BUFFERS
                                              ? (Map)new WeakIdentityHashMap()
                                              : (Map)new HashMap());
        allocatedMemory = Collections.synchronizedMap(new WeakHashMap());
    }

    /** Force cleanup of memory that has associated NIO Buffers which have
        been GC'd.
    */
    public static void purge() {
        buffers.size();
    }

    /** Dispose of all allocated memory. */
    public static void disposeAll() {
        for (Iterator i=allocatedMemory.keySet().iterator();i.hasNext();) {
            ((Memory)i.next()).dispose();
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
        protected void dispose() {
            this.peer = 0;
        } 
        /** Pass bounds check to parent. */
        protected void boundsCheck(long off, long sz) {
            Memory.this.boundsCheck(this.peer - Memory.this.peer + off, sz);
        }
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

        allocatedMemory.put(this, new WeakReference(this));
    }

    protected Memory() { }

    /** Provide a view of this memory using the given offset as the base address.  The
     * returned {@link Pointer} will have a size equal to that of the original
     * minus the offset.
     * @throws IndexOutOfBoundsException if the requested memory is outside
     * the allocated bounds. 
     */
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
    protected void finalize() {
        dispose();
    }

    /** Free the native memory and set peer to zero */
    protected synchronized void dispose() {
        free(peer);
        peer = 0;
        allocatedMemory.remove(this);
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
    public void read(long bOff, double[] buf, int index, int length) 
    {
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
    public Pointer getPointer(long offset) {
        boundsCheck(offset, Pointer.SIZE);
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
    public ByteBuffer getByteBuffer(long offset, long length) {
        boundsCheck(offset, length);
        ByteBuffer b = super.getByteBuffer(offset, length);
        // Ensure this Memory object will not be GC'd (and its memory freed)
        // if the Buffer is still extant.
        buffers.put(b, this);
        return b;
    }

    public String getString(long offset, String encoding) {
        // NOTE: we only make sure the start of the string is within bounds
        boundsCheck(offset, 0);
        return super.getString(offset, encoding);
    }

    public String getWideString(long offset) {
        // NOTE: we only make sure the start of the string is within bounds
        boundsCheck(offset, 0);
        return super.getWideString(offset);
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
    public void setPointer(long offset, Pointer value) {
        boundsCheck(offset, Pointer.SIZE);
        super.setPointer(offset, value);
    }

    public void setString(long offset, String value, String encoding) {
        boundsCheck(offset, Native.getBytes(value, encoding).length + 1L);
        super.setString(offset, value, encoding);
    }

    public void setWideString(long offset, String value) {
        boundsCheck(offset, (value.length() + 1L) * Native.WCHAR_SIZE);
        super.setWideString(offset, value);
    }

    public String toString() {
        return "allocated@0x" + Long.toHexString(peer) + " ("
            + size + " bytes)";
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
