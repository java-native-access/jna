package com.sun.jna;

import com.sun.jna.annotation.NativeLibrary;
import java.lang.reflect.*;

/**
 *
 * @author Todd Fast, todd.fast@sun.com
 */
public class Native extends Object
{
	/**
	 *
	 *
	 */
    private Native()
	{
		super();
    }




	////////////////////////////////////////////////////////////////////////
	// Library methods
	////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	private static <I> String getLibraryName(Class<I> interfaceClass)
	{
		NativeLibrary annotation=
			interfaceClass.getAnnotation(NativeLibrary.class);
		if (annotation==null)
		{
			throw new IllegalArgumentException("Library interface must be " +
				"annotated with the @"+NativeLibrary.class.getName()+
				" annotation");
		}
		
		String name=annotation.name();
		if ("".equals(name))
		{
			throw new IllegalArgumentException("No value for the name " +
				"attribute was specified on the @"+
				NativeLibrary.class.getName()+" annotation");
		}
		
		return name;
	}
	
	
	/**
	 *
	 */
	public static <I> I loadLibrary(Class<I> interfaceClass)
	{
		return loadLibrary(getLibraryName(interfaceClass),
			interfaceClass,Function.C_CONVENTION);
	}
	

	/**
	 *
	 */
	public static <I> I loadLibrary(String name, Class<I> interfaceClass)
	{
		return loadLibrary(name,interfaceClass,Function.C_CONVENTION);
	}


	/**
	 *
	 *
	 */
	public static <I> I loadLibrary(Class<I> interfaceClass, 
		int callingConvention)
	{
		return loadLibrary(getLibraryName(interfaceClass),interfaceClass,
			callingConvention,interfaceClass.getClassLoader());
	}


	/**
	 *
	 *
	 */
	public static <I> I loadLibrary(String name, 
		Class<I> interfaceClass, int callingConvention)
	{
		return loadLibrary(name,interfaceClass,callingConvention,
			interfaceClass.getClassLoader());
	}
	
	
	/**
	 *
	 *
	 */
	public static <I> I loadLibrary(Class<I> interfaceClass,
		int callingConvention, ClassLoader loader)
	{
		return loadLibrary(getLibraryName(interfaceClass),interfaceClass,
			callingConvention,interfaceClass.getClassLoader());
	}
	

	/**
	 *
	 *
	 */
	public static <I> I loadLibrary(String name, 
		Class<I> interfaceClass, int callingConvention, ClassLoader loader)
	{
// TAF: This doesn't really need to be true; instead, we need to check that
// the @NativeLibrary annotation is present
//		if (interfaceClass.isAssignableFrom(Library.class))
//		{
//			throw new IllegalArgumentException("Interface must be of " +
//				"type "+Library.class.getName());
//		}

		Library.Handler handler=
			new Library.Handler(name,interfaceClass,callingConvention);

		I proxy=(I)Proxy.newProxyInstance(loader,
			new Class[] {interfaceClass},handler);
		return proxy;
	}




	////////////////////////////////////////////////////////////////////////////
	// Structure allocation methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	private static void checkInterface(Class interfaceClass)
	{
		if (interfaceClass.isAssignableFrom(NativeStructureOld.class))
		{
			throw new IllegalArgumentException("Interface must be of " +
				"type "+NativeStructureOld.class.getName());
		}
	}


	/**
	 *
	 *
	 */
	public static NativeStructureOld allocStruct(Class interfaceClass)
	{
		checkInterface(interfaceClass);

		NativeStructureOld.Handler handler=
			new NativeStructureOld.Handler(interfaceClass);
		NativeStructureOld proxy=(NativeStructureOld)Proxy.newProxyInstance(
			interfaceClass.getClassLoader(),
			new Class[] {interfaceClass },handler);
		return proxy;
	}


	/**
	 *
	 *
	 */
	public static NativeStructureOld allocStruct(Class interfaceClass, int size,
		ClassLoader loader)
	{
		checkInterface(interfaceClass);

		if (loader==null)
			loader=interfaceClass.getClassLoader();

		NativeStructureOld.Handler handler=
			new NativeStructureOld.Handler(interfaceClass,size);
		NativeStructureOld proxy=(NativeStructureOld)Proxy.newProxyInstance(loader,
			new Class[] {interfaceClass },handler);
		return proxy;
	}
}
