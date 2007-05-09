package com.sun.jna;

/**
 *
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
public class NativeString extends Object
	implements CharSequence, Comparable
{
	/**
	 *
	 *
	 */
    protected NativeString(Pointer pointer)
	{
		super();
		this.pointer=pointer;
    }


	/**
	 *
	 *
	 */
    public NativeString(String string)
	{
		super();

		if (string==null)
		{
			pointer=Pointer.NULL;
		}
		else
		{
			// Allocate the memory to hold the string.  Note, we have to
			// make this 1 byte longer in order to accomodate the terminating 
			// null (which is accounted for in NativeMemory.setString()).
			pointer=new Memory(string.getBytes().length+1);
			pointer.setString(0,string);

			allocatedMemory=true;
		}
    }


	/**
	 *
	 *
	 */
	public boolean isValid()
	{
		return getPointer()!=null && getPointer().peer!=0;
	}


	/**
	 *
	 *
	 */
	public int hashCode()
	{
		if (isValid())
			return toString().hashCode();
		else
			return super.hashCode();
	}


	/**
	 *
	 *
	 */
	public boolean equals(Object other)
	{
		if (!isValid())
			return false;

		String s1=null;
		if (other instanceof NativeString)
		{
			if (!((NativeString)other).isValid())
				return false;
			else
				s1=((NativeString)other).toString();
		}
		else
		if (other instanceof String)
		{
			s1=(String)other;
		}
		else
		{
			return false;
		}

		return s1.equals(toString());
	}


	/**
	 *
	 *
	 */
	public String toString()
	{
		if (!isValid())
		{
			throw new IllegalStateException(
				"String memory has already been freed");
		}

		return getPointer().getString(0);
	}


	/**
	 *
	 *
	 */
	public Pointer getPointer()
	{
		return pointer;
	}


	/**
	 *
	 *
	 */
	public void free()
	{
		if (allocatedMemory)
			((Memory)pointer).free();

		pointer=null;
	}


	/**
	 *
	 *
	 */
	protected void finalize()
	{
		free();
	}


	/**
	 *
	 *
	 */
	public char charAt(int index)
	{
		return toString().charAt(index);
	}


	/**
	 *
	 *
	 */
	public int length()
	{
		return toString().length();
	}


	/**
	 *
	 *
	 */
	public CharSequence subSequence(int start, int end)
	{
		return toString().subSequence(start,end);
	}


	/**
	 *
	 *
	 */
	public int compareTo(Object other)
	{
		if (!isValid())
		{
			if (other instanceof NativeString)
			{
				if (!((NativeString)other).isValid())
					return 0;
			}
			else
			{
				return -1;
			}
		}

		if (other==null)
			return 1;

		String s1=null;
		if (other instanceof NativeString)
		{
			if (!((NativeString)other).isValid())
				return 1;
			else
				s1=((NativeString)other).toString();
		}
		else
		if (other instanceof String)
		{
			s1=(String)other;
		}
		else
		{
			s1=other.toString();
		}

		return toString().compareTo(s1);
	}




	////////////////////////////////////////////////////////////////////////////
	// Instance fields
	////////////////////////////////////////////////////////////////////////////

	private Pointer pointer;
	private boolean allocatedMemory;
}
