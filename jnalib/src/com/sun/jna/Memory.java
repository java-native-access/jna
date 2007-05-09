package com.sun.jna;

import java.lang.reflect.*;

/**
 * A <code>NativePointer</code> to memory obtained from the native heap via a 
 * call to <code>malloc</code>.
 *
 * <p>In some cases it might be necessary to use memory obtained from
 * <code>malloc</code>.  For example, <code>NativeMemory</code> helps 
 * accomplish the following idiom:
 * <pre>
 * 		void *buf = malloc(BUF_LEN * sizeof(char));
 *		call_some_function(buf);
 *		free(buf);
 * </pre>
 *
 * <p><b>Remember to <code>free</code> any <code>malloc</code> space
 * explicitly</b>.  This class could perhaps contain a <code>finalize</code>
 * method that does the <code>free</code>, but note that in Java you should
 * not use finalizers to free resources.
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @see NativePointer
 */
public class Memory extends Pointer 
{
	/**
	 * Private constructor, to prevent creation of uninitialized malloc'd 
	 * space.
	 *
	 */
	private Memory()
	{
		// Do nothing
	}


	/**
	 * Allocate space in the native heap via a call to C's <code>malloc</code>.
	 *
	 * @param size number of <em>bytes</em> of space to allocate
	 */
	public Memory(int size) 
	{
		this.size = size;
		peer = malloc(size);
		if (peer == 0) 
			throw new OutOfMemoryError();
	}


	/**
	 *
	 *
	 */
	public void finalize()
	{
		free();
	}


	/**
	 * De-allocate space obtained via an earlier call to <code>malloc</code>.
	 *
	 */
	public void free() 
	{
		free(peer);
		peer = 0;
	}


	/**
	 *
	 *
	 */
	public void clear()
	{
		byte[] buffer=new byte[size];
		write(0,buffer,0,size);
	}


	/**
	 *
	 *
	 */
	public boolean isValid()
	{
		return peer!=0;
	}


	/**
	 *
	 *
	 */
	public int getSize()
	{
		return size;
	}




	////////////////////////////////////////////////////////////////////////////
	// Helper methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Check that indirection won't cause us to write outside the 
	 * malloc'ed space. 
	 *
	 */
	private void boundsCheck(int off, int sz) 
	{
		if (off < 0 || off + sz > size) 
			throw new IndexOutOfBoundsException();
	}




	////////////////////////////////////////////////////////////////////////////
	// Raw read methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.read</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#read(int,byte[],int,int) 
	 */
	public void read(int bOff, byte[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 1);
		super.read(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.read</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#read(int,short[],int,int)
	 */
	public void read(int bOff, short[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 2);
		super.read(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.read</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#read(int,char[],int,int) 
	 */
	public void read(int bOff, char[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 2);
		super.read(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.read</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#read(int,int[],int,int)
	 */
	public void read(int bOff, int[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 4);
		super.read(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.read</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#read(int,long[],int,int) 
	 */
	public void read(int bOff, long[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 8);
		super.read(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.read</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#read(int,float[],int,int) 
	 */
	public void read(int bOff, float[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 4);
		super.read(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.read</code>.  But this method performs a bounds checks to
	 * ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#read(int,double[],int,int) 
	 */
	public void read(int bOff, double[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 8);
		super.read(bOff, buf, index, length);
	}




	////////////////////////////////////////////////////////////////////////////
	// Raw write methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.write</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#write(int,byte[],int,int) 
	 */
	public void write(int bOff, byte[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 1);
		super.write(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.write</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#write(int,short[],int,int)
	 */
	public void write(int bOff, short[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 2);
		super.write(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.write</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#write(int,char[],int,int)
	 */
	public void write(int bOff, char[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 2);
		super.write(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.write</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#write(int,int[],int,int) 
	 */
	public void write(int bOff, int[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 4);
		super.write(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.write</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#write(int,long[],int,int) 
	 */
	public void write(int bOff, long[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 8);
		super.write(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.write</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#write(int,float[],int,int)
	 */
	public void write(int bOff, float[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 4);
		super.write(bOff, buf, index, length);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.write</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#write(int,double[],int,int) 
	 */
	public void write(int bOff, double[] buf, int index, int length) 
	{
	    boundsCheck(bOff, length * 8);
		super.write(bOff, buf, index, length);
	}




	////////////////////////////////////////////////////////////////////////////
	// Java type read methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.getByte</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#getByte(int)
	 */
	public byte getByte(int offset) 
	{
	    boundsCheck(offset, 1);
		return super.getByte(offset);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.getShort</code>.  But this method performs a bounds
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#getShort(int)
	 */
	public short getShort(int offset) 
	{
	    boundsCheck(offset, 2);
		return super.getShort(offset);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.getInt</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#getInt(int)
	 */
	public int getInt(int offset) 
	{
	    boundsCheck(offset, 4);
		return super.getInt(offset);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.getLong</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#getLong(int)
	 */
	public long getLong(int offset) 
	{
	    boundsCheck(offset, 8);
		return super.getLong(offset);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.getFloat</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#getFloat(int)
	 */
	public float getFloat(int offset) 
	{
	    boundsCheck(offset, 4);
		return super.getFloat(offset);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.getDouble</code>.  But this method performs a 
	 * bounds check to ensure that the indirection does not cause memory 
	 * outside the <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#getDouble(int)
	 */
	public double getDouble(int offset) 
	{
	    boundsCheck(offset, 8);
		return super.getDouble(offset);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.getNativePointer</code>.  But this method performs 
	 * a bounds checks to ensure that the indirection does not cause memory 
	 * outside the <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#getPointer(int)
	 */
	public Pointer getPointer(int offset) 
	{
	    boundsCheck(offset, SIZE);
		return super.getPointer(offset);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.getString</code>.  But this method performs a 
	 * bounds checks to ensure that the indirection does not cause memory 
	 * outside the <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#getString(int)
	 */
	public String getString(int offset) 
	{
	    boundsCheck(offset, 0);
		return super.getString(offset);
	}




	////////////////////////////////////////////////////////////////////////////
	// Java type write methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.setByte</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#setByte(int)
	 */
	public void setByte(int offset, byte value) 
	{
	    boundsCheck(offset, 1);
		super.setByte(offset, value);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.setShort</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#setShort(int)
	 */
	public void setShort(int offset, short value) 
	{
	    boundsCheck(offset, 2);
		super.setShort(offset, value);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.setInt</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#setInt(int)
	 */
	public void setInt(int offset, int value) 
	{
	    boundsCheck(offset, 4);
		super.setInt(offset, value);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.setLong</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#setLong(int)
	 */
	public void setLong(int offset, long value) 
	{
	    boundsCheck(offset, 8);
		super.setLong(offset, value);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.setFloat</code>.  But this method performs a bounds 
	 * checks to ensure that the indirection does not cause memory outside the
	 * <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#setFloat(int)
	 */
	public void setFloat(int offset, float value) 
	{
	    boundsCheck(offset, 4);
		super.setFloat(offset, value);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.setDouble</code>.  But this method performs a 
	 * bounds checks to ensure that the indirection does not cause memory 
	 * outside the <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#setDouble(int)
	 */
	public void setDouble(int offset, double value) 
	{
	    boundsCheck(offset, 8);
		super.setDouble(offset, value);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.setNativePointer</code>.  But this method performs 
	 * a bounds checks to ensure that the indirection does not cause memory 
	 * outside the <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#setPointer(int)
	 */
	public void setPointer(int offset, Pointer value) 
	{
	    boundsCheck(offset, SIZE);
		super.setPointer(offset, value);
	}


	/**
	 * Indirect the native pointer to <code>malloc</code> space, a la
	 * <code>NativePointer.setString</code>.  But this method performs a 
	 * bounds checks to ensure that the indirection does not cause memory 
	 * outside the <code>malloc</code>ed space to be accessed.
	 *
	 * @see NativePointer#setString(int)
	 */
	public void setString(int offset, String value) 
	{
	    byte[] bytes = value.getBytes();
		int length = bytes.length;
	    boundsCheck(offset, length + 1);
		super.write(offset, bytes, 0, length);
		super.setByte(offset + length, (byte)0);
	}




	////////////////////////////////////////////////////////////////////////////
	// Native methods
	////////////////////////////////////////////////////////////////////////////


	/**
	 * Call the real native malloc
	 *
	 */
	private static native long malloc(int size);

	/**
	 * Call the real native free
	 *
	 */
	private static native void free(long ptr);




	////////////////////////////////////////////////////////////////////////////
	// Member variables
	////////////////////////////////////////////////////////////////////////////

	private int size; // Size of the malloc'ed space
}



