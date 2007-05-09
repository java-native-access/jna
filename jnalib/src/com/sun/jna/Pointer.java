package com.sun.jna;

/**
 * An abstraction for a native pointer data type.  A NativePointer instance 
 * represents, on the Java side, a native pointer.  The native pointer could 
 * be any <em>type</em> of native pointer.  Methods such as <code>write</code>, 
 * <code>read</code>, <code>getXXX</code>, and <code>setXXX</code>, provide 
 * means to indirect the underlying native pointer. 
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @see    NativeFunction
 */
public class Pointer extends Object
{
    /* 
	NOTE:
	The member 'peer' and the no-arg constructor are deliberately package 
	private, so all classes that are abstractions of a native pointer can only 
	be in one isolated package.  
	*/

    /**
	 * Default constructor
	 *
	 */
    /*pkg*/ Pointer()
	{
		// Do nothing
	}


	/**
	 * Compares this <code>NativePointer</code> to the specified object.
	 *
	 * @param	other 
	 *			A <code>NativePointer</code> instance
	 * @return	True if the class of this <code>NativePointer</code> object and 
	 *			the class of <code>other</code> are exactly equal, and the C
	 *			pointers being pointed to by these objects are also
	 *			equal. Returns false otherwise.
	 */
	public boolean equals(Object other) 
	{
		if (other == null)
			return false;

		if (other == this)
			return true;

		if (Pointer.class!=other.getClass())
			return false;

		return peer==((Pointer)other).peer;
    }


	/**
	 * Returns a hashcode for the native pointer represented by this
	 * <code>NativePointer</code> object
	 *
	 * @return	A hash code value for the represented native pointer
	 */
	public int hashCode() 
	{
		return (int)((peer >>> 32) + (peer & 0xFFFFFFFF));
    }


	/**
	 *
	 *
	 */
	public boolean isValid()
	{
		return peer!=0;
	}




	////////////////////////////////////////////////////////////////////////////
	// Raw read methods
	////////////////////////////////////////////////////////////////////////////

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




	////////////////////////////////////////////////////////////////////////////
	// Raw write methods
	////////////////////////////////////////////////////////////////////////////

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




	////////////////////////////////////////////////////////////////////////////
	// Java type read methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Indirect the native pointer as a pointer to <code>byte</code>.  This is
	 * equivalent to the expression 
	 * <code>*((jbyte *)((char *)NativePointer + * offset))</code>.
	 *
	 * @param offset offset from pointer to perform the indirection
	 * @return the <code>byte</code> value being pointed to
	 */
	public native byte getByte(int offset);


	/**
	 * Indirect the native pointer as a pointer to <code>short</code>.  This is
	 * equivalent to the expression
	 * <code>*((jshort *)((char *)NativePointer + offset))</code>.
	 *
	 * @param offset byte offset from pointer to perform the indirection
	 * @return the <code>short</code> value being pointed to
	 */
	public native short getShort(int offset);


	/**
	 * Indirect the native pointer as a pointer to <code>int</code>.  This is
	 * equivalent to the expression
	 * <code>*((jint *)((char *)NativePointer + offset))</code>.
	 *
	 * @param offset byte offset from pointer to perform the indirection
	 * @return the <code>int</code> value being pointed to
	 */
	public native int getInt(int offset);


	/**
	 * Indirect the native pointer as a pointer to <code>long</code>.  This is
	 * equivalent to the expression
	 * <code>*((jlong *)((char *)NativePointer + offset))</code>.
	 *
	 * @param offset byte offset from pointer to perform the indirection
	 * @return the <code>long</code> value being pointed to
	 */
	public native long getLong(int offset);


	/**
	 * Indirect the native pointer as a pointer to <code>float</code>.  This is
	 * equivalent to the expression
	 * <code>*((jfloat *)((char *)NativePointer + offset))</code>.
	 *
	 * @param offset byte offset from pointer to perform the indirection
	 * @return the <code>float</code> value being pointed to
	 */
	public native float getFloat(int offset);


	/**
	 * Indirect the native pointer as a pointer to <code>double</code>.  This 
	 * is equivalent to the expression
	 * <code>*((jdouble *)((char *)NativePointer + offset))</code>.
	 *
	 * @param offset byte offset from pointer to perform the indirection
	 * @return the <code>double</code> value being pointed to
	 */
	public native double getDouble(int offset);


	/**
	 * Indirect the native pointer as a pointer to pointer.  This is equivalent 
	 * to the expression 
	 * <code>*((void **)((char *)NativePointer + offset))</code>.
	 *
	 * @param offset byte offset from pointer to perform the indirection
	 * @return the <code>pointer</code> value being pointed to
	 */
	public native Pointer getPointer(int offset);


	/**
	 * Indirect the native pointer as a pointer to <code>char *</code>, a
	 * <code>NULL</code>-terminated native string. Convert the native string 
	 * to a <code>java.lang.String</code>.
	 *
	 * @param offset byte offset from pointer to obtain the native string
	 * @return the <code>String</code> value being pointed to 
	 */
	public native String getString(int offset);




	////////////////////////////////////////////////////////////////////////////
	// Java type write methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Set <code>value</code> at location being pointed to. This is equivalent
	 * to the expression
	 * <code>*((jbyte *)((char *)NativePointer + offset)) = value</code>.
	 *
	 * @param offset byte offset from pointer at which <code>value</code>
	 *		     must be set
	 * @param value <code>byte</code> value to set
	 */
	public native void setByte(int offset, byte value);


	/**
	 * Set <code>value</code> at location being pointed to. This is equivalent
	 * to the expression
	 * <code>*((jshort *)((char *)NativePointer + offset)) = value</code>.
	 *
	 * @param offset byte offset from pointer at which <code>value</code>
	 *		     must be set
	 * @param value <code>short</code> value to set
	 */
	public native void setShort(int offset, short value);


	/**
	 * Set <code>value</code> at location being pointed to. This is equivalent
	 * to the expression
	 * <code>*((jint *)((char *)NativePointer + offset)) = value</code>.
	 *
	 * @param offset byte offset from pointer at which <code>value</code>
	 *		     must be set
	 * @param value <code>int</code> value to set
	 */
	public native void setInt(int offset, int value);


	/**
	 * Set <code>value</code> at location being pointed to. This is equivalent
	 * to the expression
	 * <code>*((jlong *)((char *)NativePointer + offset)) = value</code>.
	 *
	 * @param offset byte offset from pointer at which <code>value</code>
	 *               must be set
	 * @param value <code>long</code> value to set
	 */
	public native void setLong(int offset, long value);


	/**
	 * Set <code>value</code> at location being pointed to. This is equivalent
	 * to the expression
	 * <code>*((jfloat *)((char *)NativePointer + offset)) = value</code>.
	 *
	 * @param offset byte offset from pointer at which <code>value</code>
	 *               must be set
	 * @param value <code>float</code> value to set
	 */
	public native void setFloat(int offset, float value);


	/**
	 * Set <code>value</code> at location being pointed to. This is equivalent
	 * to the expression
	 * <code>*((jdouble *)((char *)NativePointer + offset)) = value</code>.
	 *
	 * @param offset byte offset from pointer at which <code>value</code>
	 *               must be set
	 * @param value <code>double</code> value to set
	 */
	public native void setDouble(int offset, double value);


	/**
	 * Set <code>value</code> at location being pointed to. This is equivalent
	 * to the expression 
	 * <code>*((void **)((char *)NativePointer + offset)) = value</code>.
	 *
	 * @param offset byte offset from pointer at which <code>value</code> 
	 *               must be set
	 * @param value <code>NativePointer</code> value to set
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
	public native void setString(int offset, String value);




	////////////////////////////////////////////////////////////////////////////
	// Initialization methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialize field and method IDs for native methods of this class. 
	 *
	 **/
	private static native int initIDs(Pointer p);


	/**
	 * Get the size of a boolean field
	 *
	 **/
	private static native int getBoolSize();




	////////////////////////////////////////////////////////////////////////////
	// Class variables
	////////////////////////////////////////////////////////////////////////////

	/** The size of a native pointer on the current platform */
	public static final int SIZE;

	/** The size of a boolean value on the current platform */
	public static final int BOOL_SIZE;

	/** A canonical representation of C's NULL pointer. */
	public static final Pointer NULL;




	////////////////////////////////////////////////////////////////////////////
	// Member variables
	////////////////////////////////////////////////////////////////////////////

	/** Pointer value of the real native pointer. Use long to be 64-bit safe. */
	long peer;



	
	////////////////////////////////////////////////////////////////////////////
	// Static initializer
	////////////////////////////////////////////////////////////////////////////

	static 
	{
		System.loadLibrary("jnidispatch");
		NULL=new Pointer();
		SIZE=initIDs(NULL);
		BOOL_SIZE=getBoolSize();
	}
}
