package com.sun.jna;

import java.lang.reflect.*;

/**
 * An abstraction for a native function pointer.  An instance of 
 * <code>Function</code> repesents a pointer to some native function.  
 * <code>invokeXXX</code> methods provide means to call the function; select a 
 * <code>XXX</code> variant based on the return type of the native function.
 *
 * <p>Beware that the <code>copyIn</code>, <code>copyOut</code>, 
 * <code>setXXX</code>, and <code>getXXX</code> methods inherited from the 
 * parent will indirect machine code.
 *
 * @author Sheng Liang, originator
 * @author Todd Fast, suitability modifications
 * @see NativePointer
 */
public class Function extends Pointer 
{
    /**
	 * Private default constructor.  We don't allow creation of unitializaed 
	 * NativeFunction objects.
	 *
	 */
    private Function()
	{
		// Do nothing
	}


	/**
	 * Create a new <code>NativeFunction</code> that is linked with a native 
	 * function that follows the standard "C" calling convention.
	 * 
	 * <p>The allocated instance represents a pointer to the named native 
	 * function from the named library, called with the standard "C" calling
	 * convention.
	 *
	 * @param	libraryName
	 *			Library in which to find the native function
	 * @param	functionName
	 *			Name of the native function to be linked with
	 */
	public Function(String libraryName, String functionName) 
	{
		super();
		this.libraryName=libraryName;
		this.functionName=functionName;
	
		peer=find(libraryName,functionName);
	}


	/**
	 * Create a new <code>NativeFunction</code> that is linked with a native 
	 * function that follows a given calling convention.
	 * 
	 * <p>The allocated instance represents a pointer to the named native 
	 * function from the named library, called with the named calling 
	 * convention.
	 *
	 * @param	libraryName
	 *			Library in which to find the function
	 * @param	functionName
	 *			Name of the native function to be linked with
	 * @param	callingConvention
	 *			Calling convention used by the native function
	 */
	public Function(String libraryName, String functionName, 
		int callingConvention)
	{
		super();
		this.libraryName=libraryName;
		this.functionName=functionName;
	
		checkCallingConvention(callingConvention);
		this.callingConvention=callingConvention;

		peer=find(libraryName,functionName);
	}

	/**
	 *
	 *
	 */
	protected static void checkCallingConvention(int convention)
		throws IllegalArgumentException
	{
		if (convention!=C_CONVENTION && convention!=STDCALL_CONVENTION)
		{
			throw new IllegalArgumentException(
				"Unrecognized calling convention");
		}
	}




	////////////////////////////////////////////////////////////////////////////
	// Accessors and mutators
	////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 *
	 */
	public String getLibraryName()
	{
		return libraryName;
	}


	/**
	 *
	 *
	 */
	public String getName()
	{
		return functionName;
	}


	/**
	 *
	 *
	 */
	public int getCallingConvention()
	{
		return callingConvention;
	}




	////////////////////////////////////////////////////////////////////////////
	// Generic function invocation methods
	////////////////////////////////////////////////////////////////////////////

//	/**
//	 *
//	 *
//	 */
//	public Object invoke(Class returnType, Object[] args)
//	{
//	}


	/**
	 *
	 *
	 */
	protected Object[] convertArgs(Object[] args)
	{
		if (args==null)
			return NO_ARGS;

		for (int i=0; i<args.length; i++)
		{
			if (args[i]==null)
			{
				args[i]=Pointer.NULL;
			}
			else
			if (args[i] instanceof Boolean)
			{
				args[i]=new Integer(((Boolean)args[i]).booleanValue() ? 1 : 0);
			}
			else
			if (args[i] instanceof NativeStructureOld)
			{
				NativeStructureOld.Handler handler=(NativeStructureOld.Handler)
					Proxy.getInvocationHandler(args[i]);
				args[i]=handler.getMemory();
			}
		}

		return args;
	}




	////////////////////////////////////////////////////////////////////////////
	// Integer function invocation methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Call the native function being represented by this object
	 *
	 * @return	The value returned by the target native function
	 */
	public int invokeInt()
	{
		return invokeInt(NO_ARGS);
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg
	 *			The argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public int invokeInt(Object arg)
	{
		return invokeInt(callingConvention,convertArgs(new Object[] {arg}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public int invokeInt(Object arg1, Object arg2)
	{
		return invokeInt(callingConvention,convertArgs(
			new Object[] {arg1,arg2}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @param	arg3
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public int invokeInt(Object arg1, Object arg2, Object arg3)
	{
		return invokeInt(callingConvention,convertArgs(
			new Object[] {arg1,arg2,arg3}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The value returned by the target function
	 */
	public int invokeInt(Object[] args)
	{
		return invokeInt(callingConvention,convertArgs(args));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public native int invokeInt(int callingConvention, Object[] args);




	////////////////////////////////////////////////////////////////////////////
	// Boolean function invocation methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Call the native function being represented by this object
	 *
	 * @return	The value returned by the target native function
	 */
	public boolean invokeBoolean()
	{
		return invokeInt(NO_ARGS)==0 ? false : true;
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg
	 *			The argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public boolean invokeBoolean(Object arg)
	{
		return invokeInt(callingConvention,
			convertArgs(new Object[] {arg}))==0 ? false : true;
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public boolean invokeBoolean(Object arg1, Object arg2)
	{
		return invokeInt(callingConvention,
			convertArgs(new Object[] {arg1,arg2}))==0 ? false : true;
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @param	arg3
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public boolean invokeBoolean(Object arg1, Object arg2, Object arg3)
	{
		return invokeInt(callingConvention,convertArgs(
			new Object[] {arg1,arg2,arg3}))==0 ? false : true;
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The value returned by the target function
	 */
	public boolean invokeBoolean(Object[] args)
	{
		return invokeInt(callingConvention,convertArgs(args))==0 ? false : true;
	}




	////////////////////////////////////////////////////////////////////////////
	// Void function invocation methods
	////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Call the native function being represented by this object
	 *
	 * @return	The value returned by the target native function
	 */
	public void invoke()
	{
		invokeVoid(callingConvention,NO_ARGS);
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg
	 *			The argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public void invoke(Object arg)
	{
		invokeVoid(callingConvention,convertArgs(new Object[] {arg}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public void invoke(Object arg1, Object arg2)
	{
		invokeVoid(callingConvention,convertArgs(new Object[] {arg1,arg2}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @param	arg3
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public void invoke(Object arg1, Object arg2, Object arg3)
	{
		invokeVoid(callingConvention,
			convertArgs(new Object[] {arg1,arg2,arg3}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 */
	public void invoke(Object[] args)
	{
		invokeVoid(callingConvention,convertArgs(args));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 */
	public native void invokeVoid(int callingConvention, Object[] args);




	////////////////////////////////////////////////////////////////////////////
	// Float function invocation methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Call the native function being represented by this object
	 *
	 * @return	The value returned by the target native function
	 */
	public float invokeFloat()
	{
		return invokeFloat(callingConvention,NO_ARGS);
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg
	 *			The argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public float invokeFloat(Object arg)
	{
		return invokeFloat(callingConvention,convertArgs(new Object[] {arg}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public float invokeFloat(Object arg1, Object arg2)
	{
		return invokeFloat(callingConvention,
			convertArgs(new Object[] {arg1,arg2}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @param	arg3
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public float invokeFloat(Object arg1, Object arg2, Object arg3)
	{
		return invokeFloat(callingConvention,
			convertArgs(new Object[] {arg1,arg2,arg3}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public float invokeFloat(Object[] args)
	{
		return invokeFloat(callingConvention,convertArgs(args));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public native float invokeFloat(int callingConvention, Object[] args);




	////////////////////////////////////////////////////////////////////////////
	// Double function invocation methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Call the native function being represented by this object
	 *
	 * @return	The value returned by the target native function
	 */
	public double invokeDouble()
	{
		return invokeDouble(callingConvention,NO_ARGS);
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg
	 *			The argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public double invokeDouble(Object arg)
	{
		return invokeDouble(callingConvention,convertArgs(new Object[] {arg}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public double invokeDouble(Object arg1, Object arg2)
	{
		return invokeDouble(callingConvention,
			convertArgs(new Object[] {arg1, arg2}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @param	arg3
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public double invokeDouble(Object arg1, Object arg2, Object arg3)
	{
		return invokeDouble(callingConvention,
			convertArgs(new Object[] {arg1,arg2,arg3}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public double invokeDouble(Object[] args)
	{
		return invokeDouble(callingConvention,convertArgs(args));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public native double invokeDouble(int callingConvention, Object[] args);




	////////////////////////////////////////////////////////////////////////////
	// String function invocation methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Call the native function being represented by this object
	 *
	 * @return	The value returned by the target native function
	 */
	public String invokeString()
	{
		return invokeString(NO_ARGS);
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg
	 *			The argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public String invokeString(Object arg)
	{
		return invokeString(convertArgs(new Object[] {arg}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public String invokeString(Object arg1, Object arg2)
	{
		return invokeString(convertArgs(new Object[] {arg1,arg2}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @param	arg3
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public String invokeString(Object arg1, Object arg2, Object arg3)
	{
		return invokeString(convertArgs(new Object[] {arg1,arg2,arg3}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public String invokeString(Object[] args)
	{
		Pointer returnedString=
			invokePointer(callingConvention,convertArgs(args));
		return returnedString.getString(0);
	}




	////////////////////////////////////////////////////////////////////////////
	// Pointer function invocation methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Call the native function being represented by this object
	 *
	 * @return	The value returned by the target native function
	 */
	public Pointer invokePointer()
	{
		return invokePointer(callingConvention,NO_ARGS);
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg
	 *			The argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public Pointer invokePointer(Object arg)
	{
		return invokePointer(callingConvention,convertArgs(new Object[] {arg}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public Pointer invokePointer(Object arg1, Object arg2)
	{
		return invokePointer(callingConvention,
			convertArgs(new Object[] {arg1, arg2}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	arg1
	 *			An argument to pass to the native function
	 * @param	arg2
	 *			An argument to pass to the native function
	 * @param	arg3
	 *			An argument to pass to the native function
	 * @return	The value returned by the target native function
	 */
	public Pointer invokePointer(Object arg1, Object arg2, Object arg3)
	{
		return invokePointer(callingConvention,
			convertArgs(new Object[] {arg1,arg2,arg3}));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The native pointer returned by the target native function
	 */
	public Pointer invokePointer(Object[] args)
	{
		return invokePointer(callingConvention,convertArgs(args));
	}


	/**
	 * Call the native function being represented by this object
	 *
	 * @param	args
	 *			Arguments to pass to the native function
	 * @return	The native pointer returned by the target native function
	 */
	public native Pointer invokePointer(int callingConvention, 
		Object[] args);


	/**
	 * Find named function in the named library.  Note, this may also be useful
	 * to obtain the pointer to a function and pass it back into native code.
	 *
	 */
	public native long find(String lib, String fname);




	////////////////////////////////////////////////////////////////////////////
	// Class variables
	////////////////////////////////////////////////////////////////////////////

	public static final int C_CONVENTION=0;
	public static final int STDCALL_CONVENTION=1;
	public static final Object[] NO_ARGS=new Object[0];




	////////////////////////////////////////////////////////////////////////////
	// Instance variables
	////////////////////////////////////////////////////////////////////////////

	private int callingConvention=C_CONVENTION;
	private String libraryName;
	private String functionName;
}
