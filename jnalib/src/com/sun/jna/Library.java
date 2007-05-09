package com.sun.jna;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
public interface Library
{
	////////////////////////////////////////////////////////////////////////////
	// Inner class
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	/*pkg*/ static class Handler extends Object
		implements InvocationHandler
	{

		/**
		 *
		 *
		 */
		public Handler(String name, Class interfaceClass, 
			int callingConvention)
		{
			super();

			if (name==null || name.trim().length()==0)
			{
				throw new IllegalArgumentException(
					"Invalid library name \""+name+"\"");
			}

			if (interfaceClass==null || !interfaceClass.isInterface())
			{
				throw new IllegalArgumentException(
					"Invalid interface class \""+interfaceClass+"\"");
			}

			Function.checkCallingConvention(callingConvention);

			this.name=name;
			this.interfaceClass=interfaceClass;
			this.callingConvention=callingConvention;
		}


		/**
		 *
		 *
		 */
		public String getName()
		{
			return name;
		}


		/**
		 *
		 *
		 */
		public Class getInterfaceClass()
		{
			return interfaceClass;
		}


		/**
		 *
		 *
		 */
		public int getCallingConvention()
		{
			return callingConvention;
		}




		////////////////////////////////////////////////////////////////////////
		// InvocationHandler methods
		////////////////////////////////////////////////////////////////////////

		/**
		 *
		 *
		 */
		public Object invoke(Object proxy, Method method, Object[] inArgs)
			throws Throwable
		{
			Object result=null;

			// Clone the argument array
			Object[] args=Function.NO_ARGS;
			if (inArgs!=null)
			{
				args=new Object[inArgs.length];
				System.arraycopy(inArgs,0,args,0,args.length);
			}

			// Need to adjust args if they are structs to change them 
			// to pointers
			for (int i=0; i<args.length; i++)
			{
				Object arg=args[i];
				if (arg instanceof Structure)
				{
					Structure struct=((Structure)arg);
					
					if (struct.autoSync())
						struct.write();

					args[i]=((Structure)arg).getMemory();
				}
			}

			// Find the function to invoke
			String methodName=method.getName();
			Function function=(Function)functions.get(methodName);
			if (function==null)
			{
				function=new Function(getName(),
					methodName,getCallingConvention());
				functions.put(methodName,function);
			}

			Class returnType=method.getReturnType();
			if (returnType==Void.TYPE || returnType==Void.class)
			{
				function.invoke(args);
			}
			else
			if (returnType==Integer.TYPE || returnType==Integer.class)
			{
				result=new Integer(function.invokeInt(args));
			}
			else
			if (returnType==Boolean.TYPE || returnType==Boolean.class)
			{
				result=new Boolean(function.invokeBoolean(args));
			}
			else
			if (returnType==Float.TYPE || returnType==Float.class)
			{
				result=new Float(function.invokeFloat(args));
			}
			else
			if (returnType==Double.TYPE || returnType==Double.class)
			{
				result=new Double(function.invokeDouble(args));
			}
			else
			if (returnType==String.class)
			{
				result=function.invokeString(args);
			}
			else
			if (Pointer.class.isAssignableFrom(returnType))
			{
				result=function.invokePointer(args);
			}

			// Sync struct after invocation
			if (inArgs!=null)
			{
				for (int i=0; i<inArgs.length; i++)
				{
					Object arg=inArgs[i];
					if (arg instanceof Structure)
					{
						Structure struct=((Structure)arg);
						if (struct.autoSync())
							struct.read();
					}
				}
			}
			
			return result;
		}




		////////////////////////////////////////////////////////////////////////
		// Instance fields
		////////////////////////////////////////////////////////////////////////

		private String name;
		private Class interfaceClass;
		private int callingConvention;
		private Map functions=new HashMap();
	}
}
