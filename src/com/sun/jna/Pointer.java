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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstraction for a native pointer data type.  A Pointer instance
 * represents, on the Java side, a native pointer.  The native pointer could
 * be any <em>type</em> of native pointer.  Methods such as <code>write</code>,
 * <code>read</code>, <code>getXXX</code>, and <code>setXXX</code>, provide
 * means to access memory underlying the native pointer.<p>
 * While a constructor exists to create a Pointer from an integer value, it's
 * not generally a good idea to be creating pointers that way.
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @author Timothy Wall, robust library loading
 * @see    Function
 */
public class Pointer {

    /** Convenience constant, same as <code>null</code>. */
    public static final Pointer NULL = null;

    /** Convenience constant, equivalent to <code>(void*)CONSTANT</code>. */
    public static final Pointer createConstant(long peer) {
        return new Opaque(peer);
    }

    /** Convenience constant, equivalent to <code>(void*)CONSTANT</code>.
        This version will avoid setting any of the high bits on 64-bit
        systems.
     */
    public static final Pointer createConstant(int peer) {
        return new Opaque((long)peer & 0xFFFFFFFF);
    }

    /** Pointer value of the real native pointer. Use long to be 64-bit safe.
     */
    protected long peer;

    /** Derived class must assign peer pointer value. */
    Pointer() {
        super();
    }

    /** Create from native pointer.  Don't use this unless you know what
     * you're doing.
     */
    public Pointer(long peer) {
        this.peer = peer;
    }

    /** Provide a view of this memory using the given offset to calculate a new base address. */
    public Pointer share(long offset) {
        return share(offset, 0);
    }

    /** Provide a view of this memory using the given offset to calculate a
     * new base address, bounds-limiting the memory with the given size.
     */
    public Pointer share(long offset, long sz) {
        if (offset == 0L) {
            return this;
        }
        return new Pointer(peer + offset);
    }

    /** Zero memory for the given number of bytes. */
    public void clear(long size) {
        setMemory(0, size, (byte)0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        return (o instanceof Pointer) && (((Pointer)o).peer == peer);
    }

    @Override
    public int hashCode() {
        return (int)((peer >>> 32) + (peer & 0xFFFFFFFF));
    }


    //////////////////////////////////////////////////////////////////////////
    // Raw read methods
    //////////////////////////////////////////////////////////////////////////

    /** Returns the offset of the given value in memory from the given offset,
     * or -1 if the value is not found.
     */
    public long indexOf(long offset, byte value) {
        return Native.indexOf(this, this.peer, offset, value);
    }

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by
     * native pointer, into the specified array.
     *
     * @param offset byte offset from pointer from which data is copied
     * @param buf    <code>byte</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long offset, byte[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by
     * native pointer, into the specified array.
     *
     * @param offset byte offset from pointer from which data is copied
     * @param buf    <code>short</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long offset, short[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by
     * native pointer, into the specified array.
     *
     * @param offset byte offset from pointer from which data is copied
     * @param buf    <code>char</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long offset, char[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by
     * native pointer, into the specified array.
     *
     * @param offset byte offset from pointer from which data is copied
     * @param buf    <code>int</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long offset, int[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by
     * native pointer, into the specified array.
     *
     * @param offset byte offset from pointer from which data is copied
     * @param buf    <code>long</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long offset, long[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by
     * native pointer, into the specified array.
     *
     * @param offset byte offset from pointer from which data is copied
     * @param buf    <code>float</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long offset, float[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by
     * native pointer, into the specified array.
     *
     * @param offset byte offset from pointer from which data is copied
     * @param buf    <code>double</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long offset, double[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by
     * native pointer, into the specified array.
     *
     * @param offset byte offset from pointer from which data is copied
     * @param buf    {@link Pointer} array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public void read(long offset, Pointer[] buf, int index, int length) {
        for (int i=0;i < length;i++) {
            Pointer p = getPointer(offset + i*Native.POINTER_SIZE);
            Pointer oldp = buf[i+index];
            // Avoid replacing the original pointer if it hasn't changed
            if (oldp == null || p == null || p.peer != oldp.peer) {
                buf[i+index] = p;
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////
    // Raw write methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by
     * native pointer, from the specified array.
     *
     * @param offset byte offset from pointer into which data is copied
     * @param buf    <code>byte</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public void write(long offset, byte[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by
     * native pointer, from the specified array.
     *
     * @param offset byte offset from pointer into which data is copied
     * @param buf    <code>short</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public void write(long offset, short[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by
     * native pointer, from the specified array.
     *
     * @param offset byte offset from pointer into which data is copied
     * @param buf    <code>char</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public void write(long offset, char[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by
     * native pointer, from the specified array.
     *
     * @param offset byte offset from pointer into which data is copied
     * @param buf    <code>int</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public void write(long offset, int[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by
     * native pointer, from the specified array.
     *
     * @param offset byte offset from pointer into which data is copied
     * @param buf    <code>long</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public void write(long offset, long[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by
     * native pointer, from the specified array.
     *
     * @param offset byte offset from pointer into which data is copied
     * @param buf    <code>float</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public void write(long offset, float[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    /**
     * Indirect the native pointer, copying <em>into</em> memory pointed to by
     * native pointer, from the specified array.
     *
     * @param offset byte offset from pointer into which data is copied
     * @param buf    <code>double</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
     */
    public void write(long offset, double[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    /** Write the given array of Pointer to native memory.
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>Pointer</code> array from which to copy
     * @param index  array index from which to start copying
     * @param length number of elements from <code>buf</code> that must be
     *               copied
    */
    public void write(long bOff, Pointer[] buf, int index, int length) {
        for (int i=0;i < length;i++) {
            setPointer(bOff + i * Native.POINTER_SIZE, buf[index + i]);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Java type read methods
    //////////////////////////////////////////////////////////////////////////

    Object getValue(long offset, Class<?> type, Object currentValue) {

        Object result = null;
        if (Structure.class.isAssignableFrom(type)) {
            Structure s = (Structure)currentValue;
            if (Structure.ByReference.class.isAssignableFrom(type)) {
                s = Structure.updateStructureByReference((Class<Structure>) type, s, getPointer(offset));
            } else {
                s.useMemory(this, (int)offset, true);
                s.read();
            }
            result = s;
        } else if (type == boolean.class || type == Boolean.class) {
            result = Function.valueOf(getInt(offset) != 0);
        } else if (type == byte.class || type == Byte.class) {
            result =  Byte.valueOf(getByte(offset));
        } else if (type == short.class || type == Short.class) {
            result = Short.valueOf(getShort(offset));
        } else if (type == char.class || type == Character.class) {
            result = Character.valueOf(getChar(offset));
        } else if (type == int.class || type == Integer.class) {
            result = Integer.valueOf(getInt(offset));
        } else if (type == long.class || type == Long.class) {
            result = Long.valueOf(getLong(offset));
        } else if (type == float.class || type == Float.class) {
            result = Float.valueOf(getFloat(offset));
        } else if (type == double.class || type == Double.class) {
            result = Double.valueOf(getDouble(offset));
        } else if (Pointer.class.isAssignableFrom(type)) {
            Pointer p = getPointer(offset);
            if (p != null) {
                Pointer oldp = currentValue instanceof Pointer
                    ? (Pointer)currentValue : null;
                if (oldp == null || p.peer != oldp.peer) {
                    result = p;
                } else {
                    result = oldp;
                }
            }
        } else if (type == String.class) {
            Pointer p = getPointer(offset);
            result = p != null ? p.getString(0) : null;
        } else if (type == WString.class) {
            Pointer p = getPointer(offset);
            result = p != null ? new WString(p.getWideString(0)) : null;
        } else if (Callback.class.isAssignableFrom(type)) {
            // Overwrite the Java memory if the native pointer is a different
            // function pointer.
            Pointer fp = getPointer(offset);
            if (fp == null) {
                result = null;
            } else {
                Callback cb = (Callback)currentValue;
                Pointer oldfp = CallbackReference.getFunctionPointer(cb);
                if (!fp.equals(oldfp)) {
                    cb = CallbackReference.getCallback(type, fp);
                }
                result = cb;
            }
        } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
            Pointer bp = getPointer(offset);
            if (bp == null) {
                result = null;
            } else {
                Pointer oldbp = currentValue == null ? null
                    : Native.getDirectBufferPointer((Buffer)currentValue);
                if (oldbp == null || !oldbp.equals(bp)) {
                    throw new IllegalStateException("Can't autogenerate a direct buffer on memory read");
                }
                result = currentValue;
            }
        } else if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMapped nm = (NativeMapped)currentValue;
            if (nm != null) {
                Object value = getValue(offset, nm.nativeType(), null);
                result = nm.fromNative(value, new FromNativeContext(type));
                if (nm.equals(result)) {
                    result = nm;
                }
            } else {
                NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
                Object value = getValue(offset, tc.nativeType(), null);
                result = tc.fromNative(value, new FromNativeContext(type));
            }
        } else if (type.isArray()) {
            result = currentValue;
            if (result == null) {
                throw new IllegalStateException("Need an initialized array");
            }
            readArray(offset, result, type.getComponentType());
        } else {
            throw new IllegalArgumentException("Reading \"" + type + "\" from memory is not supported");
        }
        return result;
    }

    /** Read memory starting at offset into the array with element type cls. */
    private void readArray(long offset, Object o, Class<?> cls) {
        int length = 0;
        length = Array.getLength(o);
        Object result = o;

        if (cls == byte.class) {
            read(offset, (byte[])result, 0, length);
        }
        else if (cls == short.class) {
            read(offset, (short[])result, 0, length);
        }
        else if (cls == char.class) {
            read(offset, (char[])result, 0, length);
        }
        else if (cls == int.class) {
            read(offset, (int[])result, 0, length);
        }
        else if (cls == long.class) {
            read(offset, (long[])result, 0, length);
        }
        else if (cls == float.class) {
            read(offset, (float[])result, 0, length);
        }
        else if (cls == double.class) {
            read(offset, (double[])result, 0, length);
        }
        else if (Pointer.class.isAssignableFrom(cls)) {
            read(offset, (Pointer[])result, 0, length);
        }
        else if (Structure.class.isAssignableFrom(cls)) {
            Structure[] sarray = (Structure[])result;
            if (Structure.ByReference.class.isAssignableFrom(cls)) {
                Pointer[] parray = getPointerArray(offset, sarray.length);
                for (int i=0;i < sarray.length;i++) {
                    sarray[i] = Structure.updateStructureByReference((Class<Structure>) cls, sarray[i], parray[i]);
                }
            }
            else {
                Structure first = sarray[0];
                if (first == null) {
                    first = Structure.newInstance((Class<Structure>) cls, share(offset));
                    first.conditionalAutoRead();
                    sarray[0] = first;
                }
                else {
                    first.useMemory(this, (int)offset, true);
                    first.read();
                }
                Structure[] tmp = first.toArray(sarray.length);
                for (int i=1;i < sarray.length;i++) {
                    if (sarray[i] == null) {
                        // Structure.toArray() takes care of read
                        sarray[i] = tmp[i];
                    }
                    else {
                        sarray[i].useMemory(this, (int)(offset + i * sarray[i].size()), true);
                        sarray[i].read();
                    }
                }
            }
        }
        else if (NativeMapped.class.isAssignableFrom(cls)) {
            NativeMapped[] array = (NativeMapped[])result;
            NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
            int size = Native.getNativeSize(result.getClass(), result) / array.length;
            for (int i=0;i < array.length;i++) {
                Object value = getValue(offset + size*i, tc.nativeType(), array[i]);
                array[i] = (NativeMapped)tc.fromNative(value, new FromNativeContext(cls));
            }
        }
        else {
            throw new IllegalArgumentException("Reading array of "
                                               + cls
                                               + " from memory not supported");
        }
    }

    /**
     * Indirect the native pointer as a pointer to <code>byte</code>.  This is
     * equivalent to the expression
     * <code>*((jbyte *)((char *)Pointer + offset))</code>.
     *
     * @param offset offset from pointer to perform the indirection
     * @return the <code>byte</code> value being pointed to
     */
    public byte getByte(long offset) {
        return Native.getByte(this, this.peer, offset);
    }

    /**
     * Indirect the native pointer as a pointer to <code>wchar_t</code>.  This
     * is equivalent to the expression
     * <code>*((wchar_t*)((char *)Pointer + offset))</code>.
     *
     * @param offset offset from pointer to perform the indirection
     * @return the <code>wchar_t</code> value being pointed to
     */
    public char getChar(long offset) {
        return Native.getChar(this, this.peer, offset);
    }

    /**
     * Indirect the native pointer as a pointer to <code>short</code>.  This is
     * equivalent to the expression
     * <code>*((jshort *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>short</code> value being pointed to
     */
    public short getShort(long offset) {
        return Native.getShort(this, this.peer, offset);
    }

    /**
     * Indirect the native pointer as a pointer to <code>int</code>.  This is
     * equivalent to the expression
     * <code>*((jint *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>int</code> value being pointed to
     */
    public int getInt(long offset) {
        return Native.getInt(this, this.peer, offset);
    }

    /**
     * Indirect the native pointer as a pointer to <code>long</code>.  This is
     * equivalent to the expression
     * <code>*((jlong *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>long</code> value being pointed to
     */
    public long getLong(long offset) {
        return Native.getLong(this, this.peer, offset);
    }

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
    public float getFloat(long offset) {
        return Native.getFloat(this, this.peer, offset);
    }

    /**
     * Indirect the native pointer as a pointer to <code>double</code>.  This
     * is equivalent to the expression
     * <code>*((jdouble *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>double</code> value being pointed to
     */
    public double getDouble(long offset) {
        return Native.getDouble(this, this.peer, offset);
    }

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
    public Pointer getPointer(long offset) {
        return Native.getPointer(peer + offset);
    }

    /**
     * Get a ByteBuffer mapped to the memory pointed to by the pointer,
     * ensuring the buffer uses native byte order.
     *
     * @param offset byte offset from pointer to start the buffer
     * @param length Length of ByteBuffer
     * @return a direct ByteBuffer that accesses the memory being pointed to,
     */
    public ByteBuffer getByteBuffer(long offset, long length) {
        return Native.getDirectByteBuffer(this, this.peer, offset, length).order(ByteOrder.nativeOrder());
    }

    /**
     * Read a wide (<code>const wchar_t *</code>) string from memory.
     *
     * @param offset
     *            byte offset from pointer to start reading bytes
     * @return the <code>String</code> value being pointed to
     */
    public String getWideString(long offset) {
        return Native.getWideString(this, this.peer, offset);
    }

    /**
     * Read a wide (<code>const wchar_t *</code>) string from memory.
     *
     * @param offset
     *            byte offset from pointer to start reading bytes
     * @param maxBytes
     *            the maximum number of bytes to read. This value must not exceed
     *            allocated memory bounds.
     * @return the <code>String</code> value being pointed to, up to either a null
     *         terminator or <code>maxBytes</code>
     */
    public String getWideString(long offset, int maxBytes) {
        // Fetch the maxBytes
        char[] data = this.getCharArray(offset, maxBytes / Native.WCHAR_SIZE);
        // Convert to String using Wide String encoding
        return Native.toString(data);
    }

    /**
     * Copy native memory to a Java String. The encoding used is obtained form
     * {@link Native#getDefaultStringEncoding()}.
     *
     * @param offset
     *            byte offset from pointer to start reading bytes
     * @return the <code>String</code> value being pointed to
     */
    public String getString(long offset) {
        return getString(offset, Native.getDefaultStringEncoding());
    }

    /**
     * Copy native memory to a Java String. The encoding used is obtained form
     * {@link Native#getDefaultStringEncoding()}.
     *
     * @param offset
     *            byte offset from pointer to start reading bytes
     * @param maxBytes
     *            the maximum number of bytes to read. This value must not exceed
     *            allocated memory bounds.
     * @return the <code>String</code> value being pointed to, up to either a null
     *         terminator or <code>maxBytes</code>
     */
    public String getString(long offset, int maxBytes) {
        return getString(offset, maxBytes, Native.getDefaultStringEncoding());
    }

    /**
     * Copy native memory to a Java String using the requested encoding.
     *
     * @param offset
     *            byte offset from pointer to obtain the native string
     * @param encoding
     *            the desired encoding
     * @return the <code>String</code> value being pointed to
     */
    public String getString(long offset, String encoding) {
        return Native.getString(this, offset, encoding);
    }

    /**
     * Copy native memory to a Java String using the requested encoding.
     *
     * @param offset
     *            byte offset from pointer to obtain the native string
     * @param maxBytes
     *            the maximum number of bytes to read. This value must not exceed
     *            allocated memory bounds.
     * @param encoding
     *            the desired encoding
     * @return the <code>String</code> value being pointed to, up to either a null
     *         terminator or <code>maxBytes</code>
     */
    public String getString(long offset, int maxBytes, String encoding) {
        // Fetch the maxBytes
        byte[] data = this.getByteArray(offset, maxBytes);
        // Convert to String
        return Native.toString(data, encoding);
    }

    /**
     * Read a native array of bytes of size <code>arraySize</code> from the given
     * <code>offset</code> from this {@link Pointer}.
     */
    public byte[] getByteArray(long offset, int arraySize) {
        byte[] buf = new byte[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    /** Read a native array of wchar_t of size <code>arraySize</code> from the
        given <code>offset</code> from this {@link Pointer}.
    */
    public char[] getCharArray(long offset, int arraySize) {
        char[] buf = new char[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    /** Read a native array of int16 of size <code>arraySize</code> from the
        given <code>offset</code> from this {@link Pointer}.
    */
    public short[] getShortArray(long offset, int arraySize) {
        short[] buf = new short[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    /** Read a native array of int32 of size <code>arraySize</code> from the
        given <code>offset</code> from this {@link Pointer}.
    */
    public int[] getIntArray(long offset, int arraySize) {
        int[] buf = new int[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    /** Read a native array of int64 of size <code>arraySize</code> from the
        given <code>offset</code> from this {@link Pointer}.
    */
    public long[] getLongArray(long offset, int arraySize) {
        long[] buf = new long[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    /** Read a native array of float of size <code>arraySize</code> from the
        given <code>offset</code> from this {@link Pointer}.
    */
    public float[] getFloatArray(long offset, int arraySize) {
        float[] buf = new float[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    /** Read a native array of double of size <code>arraySize</code> from the
        given <code>offset</code> from this {@link Pointer}.
    */
    public double[] getDoubleArray(long offset, int arraySize) {
        double[] buf = new double[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    /** Returns an array of {@link Pointer}.  The array length is
     * determined by a NULL-valued terminating element.
     */
    public Pointer[] getPointerArray(long offset) {
        List<Pointer> array = new ArrayList<Pointer>();
        int addOffset = 0;
        Pointer p = getPointer(offset);
        while (p != null) {
            array.add(p);
            addOffset += Native.POINTER_SIZE;
            p = getPointer(offset + addOffset);
        }
        return array.toArray(new Pointer[0]);
    }

    /** Returns an array of {@link Pointer} of the requested size. */
    public Pointer[] getPointerArray(long offset, int arraySize) {
        Pointer[] buf = new Pointer[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    /** <p>Returns an array of <code>String</code> based on a native array
     * of <code>char *</code>.  The array length is determined by a
     * NULL-valued terminating element.
     * </p>
     * The strings are decoded using the encoding returned by {@link
     * Native#getDefaultStringEncoding()}.
     */
    public String[] getStringArray(long offset) {
        return getStringArray(offset, -1, Native.getDefaultStringEncoding());
    }

    /** Returns an array of <code>String</code> based on a native array
     * of <code>char *</code>, using the requested encoding.  The array length
     * is determined by a NULL-valued terminating element.
     */
    public String[] getStringArray(long offset, String encoding) {
        return getStringArray(offset, -1, encoding);
    }

    /** <p>Returns an array of <code>String</code> based on a native array
     * of <code>char *</code>, using the given array length.
     * </p>
     * The strings are decoded using the encoding returned by {@link
     * Native#getDefaultStringEncoding()}.
     */
    public String[] getStringArray(long offset, int length) {
        return getStringArray(offset, length, Native.getDefaultStringEncoding());
    }

    public String[] getWideStringArray(long offset) {
        return getWideStringArray(offset, -1);
    }

    public String[] getWideStringArray(long offset, int length) {
        return getStringArray(offset, length, NativeString.WIDE_STRING);
    }

    /** Returns an array of <code>String</code> based on a native array
     * of <code>char*</code> or <code>wchar_t*</code> based on the
     * <code>wide</code> parameter, using the given array length.
     * @param offset
     * @param length
     * @param encoding
     */
    public String[] getStringArray(long offset, int length, String encoding) {
        List<String> strings = new ArrayList<String>();
        Pointer p;
        int addOffset = 0;
        if (length != -1) {
            p = getPointer(offset + addOffset);
            int count = 0;
            while (count++ < length) {
                String s = p == null
                    ? null
                    : (NativeString.WIDE_STRING.equals(encoding)
                       ? p.getWideString(0) : p.getString(0, encoding));
                strings.add(s);
                if (count < length) {
                    addOffset += Native.POINTER_SIZE;
                    p = getPointer(offset + addOffset);
                }
            }
        } else {
            while ((p = getPointer(offset + addOffset)) != null) {
                String s = NativeString.WIDE_STRING.equals(encoding)
                        ? p.getWideString(0)
                        : p.getString(0, encoding);
                strings.add(s);
                addOffset += Native.POINTER_SIZE;
            }
        }
        return strings.toArray(new String[0]);
    }

    //////////////////////////////////////////////////////////////////////////
    // Java type write methods
    //////////////////////////////////////////////////////////////////////////

    void setValue(long offset, Object value, Class<?> type) {

        // Set the value at the offset according to its type
        if (type == boolean.class || type == Boolean.class) {
            setInt(offset, Boolean.TRUE.equals(value) ? -1 : 0);
        } else if (type == byte.class || type == Byte.class) {
            setByte(offset, value == null ? 0 : ((Byte)value).byteValue());
        } else if (type == short.class || type == Short.class) {
            setShort(offset, value == null ? 0 : ((Short)value).shortValue());
        } else if (type == char.class || type == Character.class) {
            setChar(offset, value == null ? 0 : ((Character)value).charValue());
        } else if (type == int.class || type == Integer.class) {
            setInt(offset, value == null ? 0 : ((Integer)value).intValue());
        } else if (type == long.class || type == Long.class) {
            setLong(offset, value == null ? 0 : ((Long)value).longValue());
        } else if (type == float.class || type == Float.class) {
            setFloat(offset, value == null ? 0f : ((Float)value).floatValue());
        } else if (type == double.class || type == Double.class) {
            setDouble(offset, value == null ? 0.0 : ((Double)value).doubleValue());
        } else if (type == Pointer.class) {
            setPointer(offset, (Pointer)value);
        } else if (type == String.class) {
            setPointer(offset, (Pointer)value);
        } else if (type == WString.class) {
            setPointer(offset, (Pointer)value);
        } else if (Structure.class.isAssignableFrom(type)) {
            Structure s = (Structure)value;
            if (Structure.ByReference.class.isAssignableFrom(type)) {
                setPointer(offset, s == null ? null : s.getPointer());
                if (s != null) {
                    s.autoWrite();
                }
            }
            else {
                s.useMemory(this, (int)offset, true);
                s.write();
            }
        } else if (Callback.class.isAssignableFrom(type)) {
            setPointer(offset, CallbackReference.getFunctionPointer((Callback)value));
        } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
            Pointer p = value == null ? null
                : Native.getDirectBufferPointer((Buffer)value);
            setPointer(offset, p);
        } else if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
            Class<?> nativeType = tc.nativeType();
            setValue(offset, tc.toNative(value, new ToNativeContext()), nativeType);
        } else if (type.isArray()) {
            writeArray(offset, value, type.getComponentType());
        } else {
            throw new IllegalArgumentException("Writing " + type + " to memory is not supported");
        }
    }

    /** Write memory starting at offset from the array with element type cls. */
    private void writeArray(long offset, Object value, Class<?> cls) {
        if (cls == byte.class) {
            byte[] buf = (byte[])value;
            write(offset, buf, 0, buf.length);
        } else if (cls == short.class) {
            short[] buf = (short[])value;
            write(offset, buf, 0, buf.length);
        } else if (cls == char.class) {
            char[] buf = (char[])value;
            write(offset, buf, 0, buf.length);
        } else if (cls == int.class) {
            int[] buf = (int[])value;
            write(offset, buf, 0, buf.length);
        } else if (cls == long.class) {
            long[] buf = (long[])value;
            write(offset, buf, 0, buf.length);
        } else if (cls == float.class) {
            float[] buf = (float[])value;
            write(offset, buf, 0, buf.length);
        } else if (cls == double.class) {
            double[] buf = (double[])value;
            write(offset, buf, 0, buf.length);
        } else if (Pointer.class.isAssignableFrom(cls)) {
            Pointer[] buf = (Pointer[])value;
            write(offset, buf, 0, buf.length);
        } else if (Structure.class.isAssignableFrom(cls)) {
            Structure[] sbuf = (Structure[])value;
            if (Structure.ByReference.class.isAssignableFrom(cls)) {
                Pointer[] buf = new Pointer[sbuf.length];
                for (int i=0;i < sbuf.length;i++) {
                    if (sbuf[i] == null) {
                        buf[i] = null;
                    } else {
                        buf[i] = sbuf[i].getPointer();
                        sbuf[i].write();
                    }
                }
                write(offset, buf, 0, buf.length);
            } else {
                Structure first = sbuf[0];
                if (first == null) {
                    first = Structure.newInstance((Class<Structure>) cls, share(offset));
                    sbuf[0] = first;
                } else {
                    first.useMemory(this, (int)offset, true);
                }
                first.write();
                Structure[] tmp = first.toArray(sbuf.length);
                for (int i=1;i < sbuf.length;i++) {
                    if (sbuf[i] == null) {
                        sbuf[i] = tmp[i];
                    } else {
                        sbuf[i].useMemory(this, (int)(offset + i * sbuf[i].size()), true);
                    }
                    sbuf[i].write();
                }
            }
        } else if (NativeMapped.class.isAssignableFrom(cls)) {
            NativeMapped[] buf = (NativeMapped[])value;
            NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
            Class<?> nativeType = tc.nativeType();
            int size = Native.getNativeSize(value.getClass(), value) / buf.length;
            for (int i=0;i < buf.length;i++) {
                Object element = tc.toNative(buf[i], new ToNativeContext());
                setValue(offset + i*size, element, nativeType);
            }
        } else {
            throw new IllegalArgumentException("Writing array of "
                                               + cls + " to memory not supported");
        }
    }

    /** Write <code>value</code> to the requested bank of memory.
     * @param offset byte offset from pointer to start
     * @param length number of bytes to write
     * @param value value to be written
     */
    public void setMemory(long offset, long length, byte value) {
        Native.setMemory(this, this.peer, offset, length, value);
    }

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jbyte *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code> must
     *               be set
     * @param value <code>byte</code> value to set
     */
    public void setByte(long offset, byte value) {
        Native.setByte(this, this.peer, offset, value);
    }

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jshort *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code> must
     *               be set
     * @param value <code>short</code> value to set
     */
    public void setShort(long offset, short value) {
        Native.setShort(this, this.peer, offset, value);
    }

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((wchar_t *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>char</code> value to set
     */
    public void setChar(long offset, char value) {
        Native.setChar(this, this.peer, offset, value);
    }

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jint *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code> must
     *               be set
     * @param value <code>int</code> value to set
     */
    public void setInt(long offset, int value) {
        Native.setInt(this, this.peer, offset, value);
    }

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jlong *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>long</code> value to set
     */
    public void setLong(long offset, long value) {
        Native.setLong(this, this.peer, offset, value);
    }

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
    public void setFloat(long offset, float value) {
        Native.setFloat(this, this.peer, offset, value);
    }

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jdouble *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>double</code> value to set
     */
    public void setDouble(long offset, double value) {
        Native.setDouble(this, this.peer, offset, value);
    }

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
    public void setPointer(long offset, Pointer value) {
        Native.setPointer(this, this.peer, offset, value != null ? value.peer : 0);
    }

    /**
     * Copy string <code>value</code> to the location being pointed to as a
     * wide string (<code>wchar_t*</code>).
     *
     * @param offset byte offset from pointer at which characters in
     *               <code>value</code> must be set
     * @param value  <code>java.lang.String</code> value to set
     */
    public void setWideString(long offset, String value) {
        Native.setWideString(this, this.peer, offset, value);
    }

    /**
     * Copy string <code>value</code> to the location being pointed to as a
     * wide string (<code>wchar_t*</code>).
     *
     * @param offset byte offset from pointer at which characters in
     *               <code>value</code> must be set
     * @param value  <code>WString</code> value to set
     */
    public void setString(long offset, WString value) {
        setWideString(offset, value == null ? null : value.toString());
    }

    /**
     * Copy bytes out of string <code>value</code> to the location being
     * pointed to, using the encoding indicated by {@link
     * Native#getDefaultStringEncoding()}.
     *
     * @param offset byte offset from pointer at which characters in
     *               <code>value</code> must be set
     * @param value  <code>java.lang.String</code> value to set
     */
    public void setString(long offset, String value) {
        setString(offset, value, Native.getDefaultStringEncoding());
    }

    /**
     * Copy string <code>value</code> to the location being pointed to, using
     * the requested encoding.
     *
     * @param offset byte offset from pointer at which characters in
     *               <code>value</code> must be set
     * @param value  <code>java.lang.String</code> value to set
     * @param encoding desired encoding
     */
    public void setString(long offset, String value, String encoding) {
        byte[] data = Native.getBytes(value, encoding);
        write(offset, data, 0, data.length);
        setByte(offset + data.length, (byte)0);
    }

    /** Dump memory for debugging purposes. */
    public String dump(long offset, int size) {
        final int BYTES_PER_ROW = 4;
        final String TITLE = "memory dump";
        // estimate initial size assuming a 2 char line separator
        StringWriter sw = new StringWriter(TITLE.length() + 2 + size * 2 + (size / BYTES_PER_ROW * 4));
        PrintWriter out = new PrintWriter(sw);
        out.println(TITLE);
//        byte[] buf = getByteArray(offset, size);
        for (int i=0;i < size;i++) {
//            byte b = buf[i];
            byte b = getByte(offset + i);
            if ((i % BYTES_PER_ROW) == 0) out.print("[");
            if (b >=0 && b < 16)
                out.print("0");
            out.print(Integer.toHexString(b & 0xFF));
            if ((i % BYTES_PER_ROW) == BYTES_PER_ROW-1 && i < size-1)
                out.println("]");
        }
        if (sw.getBuffer().charAt(sw.getBuffer().length() - 2) != ']') {
            out.println("]");
        }
        return sw.toString();
    }

    @Override
    public String toString() {
        return "native@0x" + Long.toHexString(peer);
    }

    /** Read the native peer value.  Use with caution. */
    public static long nativeValue(Pointer p) {
        return p == null ? 0 : p.peer;
    }

    /** Set the native peer value.  Use with caution. */
    public static void nativeValue(Pointer p, long value) {
        p.peer = value;
    }

    /** Pointer which disallows all read/write access. */
    private static class Opaque extends Pointer {
        private Opaque(long peer) { super(peer); }
        private final String MSG = "This pointer is opaque: " + this;
        @Override
        public Pointer share(long offset, long size) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void clear(long size) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public long indexOf(long offset, byte value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void read(long bOff, byte[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void read(long bOff, char[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void read(long bOff, short[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void read(long bOff, int[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void read(long bOff, long[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void read(long bOff, float[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void read(long bOff, double[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void read(long bOff, Pointer[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void write(long bOff, byte[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void write(long bOff, char[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void write(long bOff, short[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void write(long bOff, int[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void write(long bOff, long[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void write(long bOff, float[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void write(long bOff, double[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void write(long bOff, Pointer[] buf, int index, int length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public ByteBuffer getByteBuffer(long offset, long length) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public byte getByte(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public char getChar(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public short getShort(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public int getInt(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public long getLong(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public float getFloat(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public double getDouble(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public Pointer getPointer(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public String getString(long bOff, String encoding) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public String getWideString(long bOff) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setByte(long bOff, byte value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setChar(long bOff, char value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setShort(long bOff, short value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setInt(long bOff, int value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setLong(long bOff, long value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setFloat(long bOff, float value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setDouble(long bOff, double value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setPointer(long offset, Pointer value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setString(long offset, String value, String encoding) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setWideString(long offset, String value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public void setMemory(long offset, long size, byte value) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public String dump(long offset, int size) {
            throw new UnsupportedOperationException(MSG);
        }
        @Override
        public String toString() {
            return "const@0x" + Long.toHexString(peer);
        }
    }
}
