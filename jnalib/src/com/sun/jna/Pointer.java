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

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
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

    /** The size of a native pointer on the current platform */
    public static final int SIZE;
    public static final int LONG_SIZE;
    
    /** Convenience constant, same as <code>null</code>. */
    public static final Pointer NULL = null;
    /** Convenience constant, equivalent to <code>(void*)-1</code>. */
    public static final Pointer PM1 = new Pointer(-1);

    static {
        if (!loadNativeLibrary()) {
            // Let the system try
            System.loadLibrary("jnidispatch");
        }
        SIZE = initIDs();
        LONG_SIZE = longSize();
    }
    
    private static String getNativeLibraryResourcePath() {
        String arch = System.getProperty("os.arch");
        String osPrefix;
        if (Platform.isWindows()) {
            osPrefix = "win32-" + arch;
        }
        else if (Platform.isMac()) {
            osPrefix = "darwin-" + arch;
        }
        else if (Platform.isLinux()) {
            osPrefix = "linux-" + arch;
        }
        else if (Platform.isSolaris()) {
            osPrefix = "sunos-" + arch;
        }
        else {
            osPrefix = System.getProperty("os.name");
        }
        return "/com/sun/jna/" + osPrefix;
    }

    private static boolean loadNativeLibrary() {
        String libname = System.mapLibraryName("jnidispatch");
        String resourceName = getNativeLibraryResourcePath() + "/" + libname;
        URL url = Pointer.class.getResource(resourceName);
        if (url == null) {
            System.err.println("Warning: jnidispatch (" + resourceName 
                               + ") not found in resource path");
            return false;
        }

        File lib = null;
        if (url.getProtocol().toLowerCase().equals("file")) {
            try {
                lib = new File(URLDecoder.decode(url.getPath(), "UTF8"));
            }
            catch(UnsupportedEncodingException e) {
                throw new Error("JRE is unexpectedly missing UTF8 encoding");
            }
        }
        else {
            InputStream is = Pointer.class.getResourceAsStream(resourceName);
            if (is == null) {
                throw new Error("Can't obtain jnidispatch InputStream");
            }
            
            FileOutputStream fos = null;
            try {
                String ext = libname.substring(libname.lastIndexOf("."));
                lib = File.createTempFile("jna", ext);
                lib.deleteOnExit();
                fos = new FileOutputStream(lib);
                int count;
                byte[] buf = new byte[1024];
                while ((count = is.read(buf, 0, buf.length)) > 0) {
                    fos.write(buf, 0, count);
                }
            }
            catch(IOException e) {
                throw new Error("Failed to create temporary file for jnidispatch library", e);
            }
            finally {
                try { is.close(); } catch(IOException e) { }
                if (fos != null) {
                    try { fos.close(); } catch(IOException e) { }
                }
            }
        }
        // Avoid dependent library link errors on w32 (this is handled
        // internal to the jnidispatch library for X11-based platforms)
        if (Platform.isWindows()) {
            // Ensure AWT library ("awt") is loaded by the proper class loader, 
            // otherwise Toolkit class init will fail
            Toolkit.getDefaultToolkit();
            try { System.loadLibrary("jawt"); } 
            catch(UnsatisfiedLinkError e) { e.printStackTrace(); }
        }
        System.load(lib.getAbsolutePath());
        return true;
    }

    /**
     * Initialize field and method IDs for native methods of this class. 
     * Returns the size of a native pointer.
     **/
    private static native int initIDs();

    /** Return the size of a native <code>long</code>. */
    private static native int longSize();
    
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
    Pointer share(int offset, int sz) {
        return new Pointer(peer + offset);
    }

    /** Zero memory for the given number of bytes. */
    void clear(int size) {
        byte[] buffer = new byte[size];
        write(0, buffer, 0, size);
    }

    /**
     * Compares this <code>Pointer</code> to the specified object.
     *
     * @param	o 
     *			A <code>Pointer</code> instance
     * @return	True if the class of this <code>Pointer</code> object and 
     *			the class of <code>other</code> are exactly equal, and the C
     *			pointers being pointed to by these objects are also
     *			equal. Returns false otherwise.
     */
    public boolean equals(Object o) {
        if (o == null)
            return peer == 0;
        return o.getClass().equals(Pointer.class) && ((Pointer)o).peer == peer;
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


    public boolean isValid() {
        return peer != 0;
    }




    //////////////////////////////////////////////////////////////////////////
    // Raw read methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer into which data is copied
     * @param buf    <code>byte</code> array into which data is copied
     * @param index  array index from which to start copying
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(int bOff, byte[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>short</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(int bOff, short[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>char</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(int bOff, char[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>int</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(int bOff, int[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>long</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(int bOff, long[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>float</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(int bOff, float[] buf, int index, int length);


    /**
     * Indirect the native pointer, copying <em>from</em> memory pointed to by 
     * native pointer, into the specified array.
     *
     * @param bOff   byte offset from pointer from which data is copied
     * @param buf    <code>double</code> array into which data is copied
     * @param index  array index to which data is copied
     * @param length number of elements from native pointer that must be copied
     */
    public native void read(int bOff, double[] buf, int index, int length);




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
    public native void write(int bOff, byte[] buf, int index, int length);


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
    public native void write(int bOff, short[] buf, int index, int length);


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
    public native void write(int bOff, char[] buf, int index, int length);


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
    public native void write(int bOff, int[] buf, int index, int length);


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
    public native void write(int bOff, long[] buf, int index, int length);


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
    public native void write(int bOff, float[] buf, int index, int length);


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
    public native void write(int bOff, double[] buf, int index, int length);


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
    public native byte getByte(int offset);

    /**
     * Indirect the native pointer as a pointer to <code>wchar_t</code>.  This 
     * is equivalent to the expression 
     * <code>*((wchar_t*)((char *)Pointer + offset))</code>.
     *
     * @param offset offset from pointer to perform the indirection
     * @return the <code>wchar_t</code> value being pointed to
     */
    public native char getChar(int offset);

    /**
     * Indirect the native pointer as a pointer to <code>short</code>.  This is
     * equivalent to the expression
     * <code>*((jshort *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>short</code> value being pointed to
     */
    public native short getShort(int offset);


    /**
     * Indirect the native pointer as a pointer to <code>int</code>.  This is
     * equivalent to the expression
     * <code>*((jint *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>int</code> value being pointed to
     */
    public native int getInt(int offset);


    /**
     * Indirect the native pointer as a pointer to <code>long</code>.  This is
     * equivalent to the expression
     * <code>*((jlong *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>long</code> value being pointed to
     */
    public native long getLong(int offset);

    /**
     * Indirect the native pointer as a pointer to <code>long</code>.  This is
     * equivalent to the expression
     * <code>*((long *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>long</code> value being pointed to
     */
    public NativeLong getNativeLong(int offset) {
        return new NativeLong(SIZE == 8 ? getLong(offset) : getInt(offset));
    }
    
    /**
     * Indirect the native pointer as a pointer to <code>float</code>.  This is
     * equivalent to the expression
     * <code>*((jfloat *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>float</code> value being pointed to
     */
    public native float getFloat(int offset);


    /**
     * Indirect the native pointer as a pointer to <code>double</code>.  This 
     * is equivalent to the expression
     * <code>*((jdouble *)((char *)Pointer + offset))</code>.
     *
     * @param offset byte offset from pointer to perform the indirection
     * @return the <code>double</code> value being pointed to
     */
    public native double getDouble(int offset);

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
    public native Pointer getPointer(int offset);

    /**
     * Get a ByteBuffer mapped to the memory pointed to by the pointer,
     * ensuring the buffer uses native byte order.
     *
     * @param offset byte offset from pointer to start the buffer
     * @param length Length of ByteBuffer
     * @return a direct ByteBuffer that accesses the memory being pointed to, 
     */
    public ByteBuffer getByteBuffer(int offset, int length) {
        return getDirectByteBuffer(offset, length).order(ByteOrder.nativeOrder());
    }
    
    /**
     * Get a direct ByteBuffer mapped to the memory pointed to by the pointer.
     *
     * @param offset byte offset from pointer to start the buffer
     * @param length Length of ByteBuffer
     * @return a direct ByteBuffer that accesses the memory being pointed to, 
     */
    private native ByteBuffer getDirectByteBuffer(int offset, int length);

    /**
     * Copy native memory to a Java String.  If <code>wide</code> is true,
     * access the memory as an array of <code>wchar_t</code>, otherwise 
     * as an array of <code>char</code>, using the native encoding.
     *
     * @param offset byte offset from pointer to obtain the native string
     * @param wide whether to convert from a wide or standard C string
     * @return the <code>String</code> value being pointed to 
     */
    public native String getString(int offset, boolean wide);

    public byte[] getByteArray(int offset, int arraySize) {
        byte[] buf = new byte[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public char[] getCharArray(int offset, int arraySize) {
        char[] buf = new char[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public short[] getShortArray(int offset, int arraySize) {
        short[] buf = new short[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public int[] getIntArray(int offset, int arraySize) {
        int[] buf = new int[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public long[] getLongArray(int offset, int arraySize) {
        long[] buf = new long[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public float[] getFloatArray(int offset, int arraySize) {
        float[] buf = new float[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public double[] getDoubleArray(int offset, int arraySize) {
        double[] buf = new double[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public Pointer[] getPointerArray(int offset, int arraySize) {
        Pointer[] buf = new Pointer[arraySize];
        for (int i=0;i < buf.length;i++) {
            buf[i] = getPointer(offset + i*Pointer.SIZE);
        }
        return buf;
    }

    //////////////////////////////////////////////////////////////////////////
    // Java type write methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jbyte *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *		     must be set
     * @param value <code>byte</code> value to set
     */
    public native void setByte(int offset, byte value);


    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jshort *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *		     must be set
     * @param value <code>short</code> value to set
     */
    public native void setShort(int offset, short value);


    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jint *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *		     must be set
     * @param value <code>int</code> value to set
     */
    public native void setInt(int offset, int value);


    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jlong *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>long</code> value to set
     */
    public native void setLong(int offset, long value);

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((long *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>long</code> value to set
     */
    public void setNativeLong(int offset, NativeLong value) {
        if (SIZE == 8) {
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
    public native void setFloat(int offset, float value);
    

    /**
     * Set <code>value</code> at location being pointed to. This is equivalent
     * to the expression
     * <code>*((jdouble *)((char *)Pointer + offset)) = value</code>.
     *
     * @param offset byte offset from pointer at which <code>value</code>
     *               must be set
     * @param value <code>double</code> value to set
     */
    public native void setDouble(int offset, double value);


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
    public native void setPointer(int offset, Pointer value);


    /**
     * Copy string <code>value</code> to the location being pointed to.  Copy
     * each element in <code>value</code>, converted to native encoding, at an
     * <code>offset</code>from the location pointed to by this pointer.
     *
     * @param offset byte offset from pointer at which characters in
     * 		     <code>value</code> must be set
     * @param value  <code>java.lang.String</code> value to set 
     */
    public native void setString(int offset, String value, boolean wide);

    public String toString() {
        return "Native Pointer <0x" + Long.toHexString(peer) + ">";
    }
}
