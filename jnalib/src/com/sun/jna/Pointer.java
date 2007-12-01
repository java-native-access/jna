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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An abstraction for a native pointer data type.  A Pointer instance 
 * represents, on the Java side, a native pointer.  The native pointer could 
 * be any <em>type</em> of native pointer.  Methods such as <code>write</code>, 
 * <code>read</code>, <code>getXXX</code>, and <code>setXXX</code>, provide 
 * means to access memory underlying the native pointer.<p>
 * The constructors are intentionally package-private, since it's not generally
 * a good idea to be creating C pointers de novo. 
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @author Timothy Wall, robust library loading
 * @see    Function
 */
public class Pointer {

    /** Size of a native pointer, in bytes. */
    public static final int SIZE;
    
    static {
        // Force load of native library
        if ((SIZE = Native.POINTER_SIZE) == 0) {
            throw new Error("Native library not initialized");
        }
    }
    
    /** Convenience constant, same as <code>null</code>. */
    public static final Pointer NULL = null;
    
    /** Convenience constant, equivalent to <code>(void*)-1</code>. */
    public static final Pointer createConstant(long peer) {
        return new Opaque(peer);
    }
    
    /** Pointer value of the real native pointer. Use long to be 64-bit safe. 
     */
    long peer;

    /** Derived class must assign peer pointer value. */
    Pointer() { }
    
    /** Create from native pointer. */
    Pointer(long peer) {
        this.peer = peer;
    }

    /** Provide a view of this pointer with a different peer base. */
    public Pointer share(long offset, long sz) {
        return new Pointer(peer + offset);
    }

    /** Zero memory for the given number of bytes. */
    void clear(long size) {
        setMemory(0, size, (byte)0);
    }

    /**
     * Compares this <code>Pointer</code> to the specified object.
     *
     * @param	o 
     *			A <code>Pointer</code> instance
     * @return	True if the other object is a <code>Pointer</code>, 
     *          and the C pointers being pointed to by these objects are also
     *			equal. Returns false otherwise.
     */
    public boolean equals(Object o) {
        if (o == null)
            return peer == 0;
        return (o instanceof Pointer) && ((Pointer)o).peer == peer;
    }

    /**
     * Returns a hashcode for the native pointer represented by this
     * <code>Pointer</code> object
     *
     * @return	A hash code value for the represented native pointer
     */
    public int hashCode() {
        return (int)((peer >>> 32) + (peer & 0xFFFFFFFF));
    }


    //////////////////////////////////////////////////////////////////////////
    // Raw read methods
    //////////////////////////////////////////////////////////////////////////

    /** Returns the offset of the given value in memory from the given offset,
     * or -1 if the value is not found.
     */
    public native long indexOf(long bOff, byte value);
    
    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>byte</code> array into which data is copied
     * @param index  array index from which to start copying
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(long bOff, byte[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>short</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(long bOff, short[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>char</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(long bOff, char[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>int</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(long bOff, int[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>long</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(long bOff, long[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>float</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(long bOff, float[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>double</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(long bOff, double[] buf, int index, int length);

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    {@link Pointer} array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long bOff, Pointer[] buf, int index, int length) {
        for (int i=0;i < length;i++) {
            buf[i + index] = getPointer(bOff + i*Pointer.SIZE);
        }
    }


    //////////////////////////////////////////////////////////////////////////
    // Raw write methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by 
     * native pointer, from the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>byte</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public native void write(long bOff, byte[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by 
     * native pointer, from the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>short</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public native void write(long bOff, short[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by 
     * native pointer, from the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>char</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public native void write(long bOff, char[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by 
     * native pointer, from the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>int</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public native void write(long bOff, int[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by 
     * native pointer, from the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>long</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public native void write(long bOff, long[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by 
     * native pointer, from the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>float</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public native void write(long bOff, float[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by 
     * native pointer, from the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>double</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public native void write(long bOff, double[] buf, int index, int length);


    /** Write the given array of Pointer to native memory. 
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>Pointer</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
    */
    public void write(long bOff, Pointer[] buf, int index, int length) {
        for (int i=0;i < length;i++) {
            setPointer(bOff + i * Pointer.SIZE, buf[index + i]);
        }
    }
    
    //////////////////////////////////////////////////////////////////////////
    // Java type read methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Indirect the native pointer as a pointer to <code>byte</code>.  This is
     * equivalent to the expression 
     * <code>*((jbyte *)((char *)Pointer + offset))</code>.
     *
     * @param offset offset from pointer to perform the indirection
     * @return the <code>byte</code> value being pointed to
     */
    public native byte getByte(long offset);

    /**
     * Indirect the native pointer as a pointer to <code>wchar_t</code>.  This 
     * is equivalent to the expression 
     * <code>*((wchar_t*)((char *)Pointer + offset))</code>.
     *
     * @param offset offset from pointer to perform the indirection
     * @return the <code>wchar_t</code> value being pointed to
     */
    public native char getChar(long offset);

    /**
     * Indirect the native pointer as a pointer to <code>short</code>.  This is
     * equivalent to the expression
     * <code>*((jshort *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>short</code> value being pointed to
     */
    public native short getShort(long offset);


    /**
     * Indirect the native pointer as a pointer to <code>int</code>.  This is
     * equivalent to the expression
     * <code>*((jint *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>int</code> value being pointed to
     */
    public native int getInt(long offset);


    /**
     * Indirect the native pointer as a pointer to <code>long</code>.  This is
     * equivalent to the expression
     * <code>*((jlong *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>long</code> value being pointed to
     */
    public native long getLong(long offset);

    /**
     * Indirect the native pointer as a pointer to <code>long</code>.  This is
     * equivalent to the expression
     * <code>*((long *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>long</code> value being pointed to
     */
    public NativeLong getNativeLong(long offset) {
        return new NativeLong(NativeLong.SIZE == 8 ? getLong(offset) : getInt(offset));
    }
    
    /**
     * Indirect the native pointer as a pointer to <code>float</code>.  This is
     * equivalent to the expression
     * <code>*((jfloat *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>float</code> value being pointed to
     */
    public native float getFloat(long offset);


    /**
     * Indirect the native pointer as a pointer to <code>double</code>.  This 
     * is equivalent to the expression
     * <code>*((jdouble *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>double</code> value being pointed to
     */
    public native double getDouble(long offset);

    /**
     * Indirect the native pointer as a pointer to pointer.  This is equivalent 
     * to the expression 
     * <code>*((void **)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return a {@link Pointer} equivalent of the pointer value 
     * being pointed to, or <code>null</code> if the pointer value is 
     * <code>NULL</code>; 
     */
    public native Pointer getPointer(long offset);

    /**
     * Get a ByteBuffer mapped to the memory pointed to by the pointer,
     * ensuring the buffer uses native byte order.
     *
     * @param offset byte offset from pointer to start the buffer
     * @param length Length of ByteBuffer
     * @return a direct ByteBuffer that accesses the memory being pointed to, 
     */
    public ByteBuffer getByteBuffer(long offset, long length) {
        return getDirectByteBuffer(offset, length).order(ByteOrder.nativeOrder());
    }
    
    /**
     * Get a direct ByteBuffer mapped to the memory pointed to by the pointer.
     *
     * @param offset byte offset from pointer to start the buffer
     * @param length Length of ByteBuffer
     * @return a direct ByteBuffer that accesses the memory being pointed to, 
     */
    private native ByteBuffer getDirectByteBuffer(long offset, long length);

    /**
     * Copy native memory to a Java String.  If <code>wide</code> is true,
     * access the memory as an array of <code>wchar_t</code>, otherwise 
     * as an array of <code>char</code>, using the default platform encoding.
     *
     * @param offset byte offset from pointer to obtain the native string
     * @param wide whether to convert from a wide or standard C string
     * @return the <code>String</code> value being pointed to 
     */
    public native String getString(long offset, boolean wide);

    /**
     * Copy native memory to a Java String.  If the system property 
     * <code>jna.encoding</code> is set, uses it as the native charset
     * when decoding the value, otherwise falls back to the default platform
     * encoding.
     *
     * @param offset byte offset from pointer to obtain the native string
     * @return the <code>String</code> value being pointed to 
     */
    public String getString(long offset) {
        String encoding = System.getProperty("jna.encoding");
        if (encoding != null) {
            long len = indexOf(offset, (byte)0);
            if (len != -1) {
                if (len > Integer.MAX_VALUE) {
                    throw new OutOfMemoryError("String exceeds maximum length: " + len);
                }
                byte[] data = getByteArray(offset, (int)len);
                try {
                    return new String(data, encoding);
                }
                catch(UnsupportedEncodingException e) { 
                }
            }
        }
        return getString(offset, false);
    }

    public byte[] getByteArray(long offset, int arraySize) {
        byte[] buf = new byte[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }
    
    public char[] getCharArray(long offset, int arraySize) {
        char[] buf = new char[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public short[] getShortArray(long offset, int arraySize) {
        short[] buf = new short[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public int[] getIntArray(long offset, int arraySize) {
        int[] buf = new int[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public long[] getLongArray(long offset, int arraySize) {
        long[] buf = new long[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public float[] getFloatArray(long offset, int arraySize) {
        float[] buf = new float[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public double[] getDoubleArray(long offset, int arraySize) {
        double[] buf = new double[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public Pointer[] getPointerArray(long offset, int arraySize) {
        Pointer[] buf = new Pointer[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    //////////////////////////////////////////////////////////////////////////
    // Java type write methods
    //////////////////////////////////////////////////////////////////////////

    /** Write <code>value</code> to the requested bank of memory. 
     * @param offset byte offset from pointer to start
     * @param length number of bytes to write
     * @param value value to be written
     */
    public native void setMemory(long offset, long length, byte value);
    
    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jbyte *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *		     must be set
     * @param value <code>byte</code> value to set
     */
    public native void setByte(long offset, byte value);


    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jshort *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *		     must be set
     * @param value <code>short</code> value to set
     */
    public native void setShort(long offset, short value);


    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((wchar_t *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>char</code> value to set
     */
    public native void setChar(long offset, char value);


    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jint *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *		     must be set
     * @param value <code>int</code> value to set
     */
    public native void setInt(long offset, int value);


    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jlong *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>long</code> value to set
     */
    public native void setLong(long offset, long value);

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((long *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>long</code> value to set
     */
    public void setNativeLong(long offset, NativeLong value) {
        if (NativeLong.SIZE == 8) {
            setLong(offset, value.longValue());
        } else {
            setInt(offset, value.intValue());
        }
    }

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jfloat *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>float</code> value to set
     */
    public native void setFloat(long offset, float value);
    

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jdouble *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>double</code> value to set
     */
    public native void setDouble(long offset, double value);


    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression 
     * <code>*((void **)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code> 
     *               must be set
     * @param value <code>Pointer</code> holding the actual pointer value to 
     * set, which may be <code>null</code> to indicate a <code>NULL</code>
     * pointer.
     */
    public native void setPointer(long offset, Pointer value);


    /**
     * Copy string <code>value</code> to the location being pointed to.  Copy
     * each element in <code>value</code>, converted to native encoding, at an
     * <code>offset</code>from the location pointed to by this pointer.
     *
     * @param offset byte offset from pointer at which characters in
     * 		     <code>value</code> must be set
     * @param value  <code>java.lang.String</code> value to set
     * @param wide whether to write the native string as an array of 
     * <code>wchar_t</code>.  If false, writes as a NUL-terminated array of 
     * <code>char</code> using the default platform encoding. 
     */
    public native void setString(long offset, String value, boolean wide);

    /**
     * Copy string <code>value</code> to the location being pointed to.  Copy
     * each element in <code>value</code>, converted to native encoding, at an
     * <code>offset</code>from the location pointed to by this pointer.
     * Uses the value of the system property <code>jna.encoding</code>, if set, 
     * to determine the appropriate native charset in which to encode the value.  
     * If the property is not set, uses the default platform encoding.
     *
     * @param offset byte offset from pointer at which characters in
     *               <code>value</code> must be set
     * @param value  <code>java.lang.String</code> value to set
     */
    public void setString(long offset, String value) {
        byte[] data = Native.getBytes(value);
        write(offset, data, 0, data.length);
        setByte(offset + data.length, (byte)0);
    }
    
    public String toString() {
        return "native@0x" + Long.toHexString(peer);
    }
    
    /** Pointer which disallows all read/write access. */
    private static class Opaque extends Pointer {
        private Opaque(long peer) { super(peer); }
        private String MSG = "This pointer is opaque: " + this;
        public long indexOf(long offset, byte value) {
            throw new UnsupportedOperationException(MSG);
        }
        public void read(long bOff, byte[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void read(long bOff, char[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void read(long bOff, short[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void read(long bOff, int[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void read(long bOff, long[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void read(long bOff, float[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void read(long bOff, double[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void write(long bOff, byte[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void write(long bOff, char[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void write(long bOff, short[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void write(long bOff, int[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void write(long bOff, long[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void write(long bOff, float[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public void write(long bOff, double[] buf, int index, int length) { 
            throw new UnsupportedOperationException(MSG); 
        }
        public byte getByte(long bOff) {
            throw new UnsupportedOperationException(MSG); 
        }
        public char getChar(long bOff) {
            throw new UnsupportedOperationException(MSG); 
        }
        public short getShort(long bOff) {
            throw new UnsupportedOperationException(MSG); 
        }
        public int getInt(long bOff) {
            throw new UnsupportedOperationException(MSG); 
        }
        public long getLong(long bOff) {
            throw new UnsupportedOperationException(MSG); 
        }
        public float getFloat(long bOff) {
            throw new UnsupportedOperationException(MSG); 
        }
        public double getDouble(long bOff) {
            throw new UnsupportedOperationException(MSG); 
        }
        public Pointer getPointer(long bOff) {
            throw new UnsupportedOperationException(MSG); 
        }
        public String getString(long bOff, boolean wide) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setByte(long bOff, byte value) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setChar(long bOff, char value) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setShort(long bOff, short value) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setInt(long bOff, int value) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setLong(long bOff, long value) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setFloat(long bOff, float value) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setDouble(long bOff, double value) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setPointer(long offset, Pointer value) {
            throw new UnsupportedOperationException(MSG); 
        }
        public void setString(long offset, String value, boolean wide) {
            throw new UnsupportedOperationException(MSG); 
        }
        public String toString() {
            return "opaque@0x" + Long.toHexString(peer);
        }
    }
}
