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

import java.nio.ByteBuffer;


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
 * <p><b>Remember to <code>free</code> any <code>malloc</code> space
 * explicitly</b>.  While the <code>finalize</code> method will free
 * allocated memory, you should explicitly free resources when they are
 * no longer in use.
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @see Pointer
 */
public class Memory extends Pointer {

    protected int size; // Size of the malloc'ed space

    /** Provide a view into the original memory. */
    private class SharedMemory extends Memory {
        public SharedMemory(int offset) {
            this.size = Memory.this.size - offset;
            this.peer = Memory.this.peer + offset;
        }
        /** No need to free memory. */
        protected void finalize() { } 
    }
    
    /**
     * Allocate space in the native heap via a call to C's <code>malloc</code>.
     *
     * @param size number of <em>bytes</em> of space to allocate
     */
    public Memory(int size) {
        this.size = size;
        peer = malloc(size);
        if (peer == 0) 
            throw new OutOfMemoryError();
    }

    protected Memory() { }

    /** Provide a view onto this structure from the given offset. 
     * @throws IndexOutOfBoundsException if the requested memory is outside
     * the allocated bounds. 
     */
    Pointer share(int offset, int sz) {
        boundsCheck(offset, sz);
        return new SharedMemory(offset);
    }
    
    protected void finalize() {
        if (peer != 0) {
            free(peer);
            peer = 0;
        }
    }

    /** Zero the full extent of this memory region. */
    public void clear() {
        clear(size);
    }

    /** Returns false if the memory has been freed. */
    public boolean isValid() {
        return peer != 0;
    }

    public int getSize() {
        return size;
    }


    /**
     * Check that indirection won't cause us to write outside the 
     * malloc'ed space. 
     *
     */
    private void boundsCheck(int off, int sz) {
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
     * @see Pointer#read(int,byte[],int,int) 
     */
    public void read(int bOff, byte[] buf, int index, int length) {
        boundsCheck(bOff, length * 1);
        super.read(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(int,short[],int,int)
     */
    public void read(int bOff, short[] buf, int index, int length) {
        boundsCheck(bOff, length * 2);
        super.read(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(int,char[],int,int) 
     */
    public void read(int bOff, char[] buf, int index, int length) {
        boundsCheck(bOff, length * 2);
        super.read(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(int,int[],int,int)
     */
    public void read(int bOff, int[] buf, int index, int length) {
        boundsCheck(bOff, length * 4);
        super.read(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(int,long[],int,int) 
     */
    public void read(int bOff, long[] buf, int index, int length) {
        boundsCheck(bOff, length * 8);
        super.read(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(int,float[],int,int) 
     */
    public void read(int bOff, float[] buf, int index, int length) {
        boundsCheck(bOff, length * 4);
        super.read(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.read</code>.  But this method performs a bounds checks to
     * ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#read(int,double[],int,int) 
     */
    public void read(int bOff, double[] buf, int index, int length) 
    {
        boundsCheck(bOff, length * 8);
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
     * @see Pointer#write(int,byte[],int,int) 
     */
    public void write(int bOff, byte[] buf, int index, int length) {
        boundsCheck(bOff, length * 1);
        super.write(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(int,short[],int,int)
     */
    public void write(int bOff, short[] buf, int index, int length) {
        boundsCheck(bOff, length * 2);
        super.write(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(int,char[],int,int)
     */
    public void write(int bOff, char[] buf, int index, int length) {
        boundsCheck(bOff, length * 2);
        super.write(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(int,int[],int,int) 
     */
    public void write(int bOff, int[] buf, int index, int length) {
        boundsCheck(bOff, length * 4);
        super.write(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(int,long[],int,int) 
     */
    public void write(int bOff, long[] buf, int index, int length) {
        boundsCheck(bOff, length * 8);
        super.write(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(int,float[],int,int)
     */
    public void write(int bOff, float[] buf, int index, int length) {
        boundsCheck(bOff, length * 4);
        super.write(bOff, buf, index, length);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.write</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#write(int,double[],int,int) 
     */
    public void write(int bOff, double[] buf, int index, int length) {
        boundsCheck(bOff, length * 8);
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
     * @see Pointer#getByte(int)
     */
    public byte getByte(int offset) {
        boundsCheck(offset, 1);
        return super.getByte(offset);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getByte</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getByte(int)
     */
    public char getChar(int offset) {
        boundsCheck(offset, 1);
        return super.getChar(offset);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getShort</code>.  But this method performs a bounds
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getShort(int)
     */
    public short getShort(int offset) {
        boundsCheck(offset, 2);
        return super.getShort(offset);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getInt</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getInt(int)
     */
    public int getInt(int offset) {
        boundsCheck(offset, 4);
        return super.getInt(offset);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getLong</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getLong(int)
     */
    public long getLong(int offset) {
        boundsCheck(offset, 8);
        return super.getLong(offset);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getFloat</code>.  But this method performs a bounds 
     * checks to ensure that the indirection does not cause memory outside the
     * <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getFloat(int)
     */
    public float getFloat(int offset) {
        boundsCheck(offset, 4);
        return super.getFloat(offset);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getDouble</code>.  But this method performs a 
     * bounds check to ensure that the indirection does not cause memory 
     * outside the <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getDouble(int)
     */
    public double getDouble(int offset) {
        boundsCheck(offset, 8);
        return super.getDouble(offset);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getPointer</code>.  But this method performs 
     * a bounds checks to ensure that the indirection does not cause memory 
     * outside the <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getPointer(int)
     */
    public Pointer getPointer(int offset) {
        boundsCheck(offset, SIZE);
        return super.getPointer(offset);
    }

    /**
     * Get a ByteBuffer mapped to a portion of this memory.
     *
     * @param offset byte offset from pointer to start the buffer
     * @param length Length of ByteBuffer
     * @return a direct ByteBuffer that accesses the memory being pointed to, 
     */
    public ByteBuffer getByteBuffer(int offset, int length) {
        boundsCheck(offset, length);
        return super.getByteBuffer(offset, length);
    }

    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.getString</code>.  But this method performs a 
     * bounds checks to ensure that the indirection does not cause memory 
     * outside the <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#getString(int, boolean)
     */
    public String getString(int offset, boolean wide) {
        boundsCheck(offset, 0);
        return super.getString(offset, wide);
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
    public void setByte(int offset, byte value) {
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
    public void setChar(int offset, char value) {
        boundsCheck(offset, Pointer.WCHAR_SIZE);
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
    public void setShort(int offset, short value) {
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
    public void setInt(int offset, int value) {
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
    public void setLong(int offset, long value) {
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
    public void setFloat(int offset, float value) {
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
    public void setDouble(int offset, double value) {
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
    public void setPointer(int offset, Pointer value) {
        boundsCheck(offset, SIZE);
        super.setPointer(offset, value);
    }


    /**
     * Indirect the native pointer to <code>malloc</code> space, a la
     * <code>Pointer.setString</code>.  But this method performs a 
     * bounds checks to ensure that the indirection does not cause memory 
     * outside the <code>malloc</code>ed space to be accessed.
     *
     * @see Pointer#setString
     */
    public void setString(int offset, String value, boolean wide) {
        boundsCheck(offset, (value.length() + 1) * (wide ? Pointer.WCHAR_SIZE : 1));
        super.setString(offset, value, wide);
    }


    /**
     * Call the real native malloc
     */
    static native long malloc(int size);

    /**
     * Call the real native free
     */
    static native void free(long ptr);
    
    public String toString() {
        return "Native Allocated Memory <0x" + Long.toHexString(peer) + "> ("
            + size + " bytes)";
    }
}
