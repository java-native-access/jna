package com.sun.jna;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
public interface NativeStructureOld
{
	/**
	 *
	 *
	 */
	public int size();


	/**
	 *
	 *
	 */
	public void free();


	/**
	 *
	 *
	 */
	public int getFieldOffset(String fieldName);


	/**
	 *
	 *
	 */
	public Memory getMemory();




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
		public Handler(Class interfaceClass)
		{
			super();

			if (interfaceClass==null || !interfaceClass.isInterface())
			{
				throw new IllegalArgumentException(
					"Invalid interface class \""+interfaceClass+"\"");
			}
		
			this.interfaceClass=interfaceClass;
			size=analyze(interfaceClass);

			memory=new Memory(size);
		}


		/**
		 *
		 *
		 */
		public Handler(Class interfaceClass, int size)
		{
			super();

			if (interfaceClass==null || !interfaceClass.isInterface())
			{
				throw new IllegalArgumentException(
					"Invalid interface class \""+interfaceClass+"\"");
			}

			this.interfaceClass=interfaceClass;
			this.size=size;
			memory=new Memory(size);

			analyze(interfaceClass);
		}


		/**
		 *
		 *
		 */	
		public void free()
		{
			if (!freed)
			{
				freed=true;
				getMemory().free();

				// Free all dependent strings that we allocated along the way
				for (Iterator i=allocatedStrings.values().iterator(); 
					i.hasNext(); )
				{
					((NativeString)i.next()).free();
				}
			}
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
		public Memory getMemory()
		{
			return memory;
		}


		/**
		 *
		 *
		 */
		private int analyze(Class interfaceClass)
		{
			Field field=null;
			try
			{
				field=interfaceClass.getField("FIELDS");
			}
			catch (NoSuchFieldException e)
			{
				throw new IllegalArgumentException("The struct declaration "+
					"field named \"FIELDS\" was not found");
			}

			Object fieldsArray=null;
			try
			{
				fieldsArray=field.get(null);
			}
			catch (IllegalAccessException e)
			{
				// Should never happen
				throw new IllegalArgumentException("The struct declaration "+
					"field named \"FIELDS\" could not be accessed");
			}

			if (!(fieldsArray.getClass().isArray() && 
				fieldsArray.getClass().getComponentType()==String.class))
			{
				throw new IllegalArgumentException("Every structure " +
					"interface must declare a static String array " +
					"named \"FIELDS\" containing the names of the struct "+
					"fields in the desired order");
			}

			// Partially initialize our list of struct fields
			String[] fields=(String[])fieldsArray;
			structFields=new StructField[fields.length];
			for (int i=0; i<structFields.length; i++)
			{
				structFields[i]=new StructField();
				structFields[i].name=fields[i];
			}

			int processedFields=0;
			int size=0;
			Method[] methods=interfaceClass.getMethods();
			for (int i=0; i<methods.length; i++)
			{
				Method method=methods[i];

				// Skip the free() and size() methods
				if (method.equals(METHOD_FREE) || method.equals(METHOD_SIZE) ||
					method.equals(METHOD_GET_FIELD_OFFSET) || 
					method.equals(METHOD_GET_MEMORY))
				{
					continue;
				}

				String fieldName=getFieldName(method);
				if (fieldName==null)
					continue;

				StructField structField=findStructField(fieldName);
				if (structField==null)
				{
					// We found a method that is not related to a field
					throw new IllegalArgumentException("The method \""+
						fieldName+"\" does not correspond to a declared " +
						"struct field; remove it, change the name, or " +
						"add the appropriate struct field declaration");
				}
				else
				if (structField.type==null)
				{
					structField.type=getFieldType(method);
					structField.width=getFieldWidth(structField.type);
					size+=structField.width;
					processedFields++;
				}
			}

			// Sanity check
			if (processedFields!=structFields.length)
			{
				throw new IllegalArgumentException("Not all fields "+
					"declared as struct fields had methods to get or set "+
					"the field value");
			}

			return size;
		}


		/**
		 *
		 *
		 */
		private StructField findStructField(String fieldName)
		{
			for (int i=0; i<structFields.length; i++)
			{
				if (structFields[i].name.equals(fieldName))
					return structFields[i];
			}

			return null;
		}


		/**
		 *
		 *
		 */
		protected int getFieldOffset(String fieldName)
		{
			int offset=0;
			for (int i=0; i<structFields.length; i++)
			{
				if (structFields[i].name.equals(fieldName))
					return offset;
				else
					offset+=structFields[i].width;
			}

			assert false:
				"StructField for \""+fieldName+"\" could not be found";
			return offset;
		}


		/**
		 *
		 *
		 */
		protected boolean isReadMethod(Method method)
		{
			String methodName=method.getName();
			return methodName.startsWith("get") || methodName.startsWith("is");
		}


		/**
		 *
		 *
		 */
		protected boolean isWriteMethod(Method method)
		{
			String methodName=method.getName();
			return methodName.startsWith("set");
		}


		/**
		 *
		 *
		 */
		protected String getFieldName(Method method)
		{
			String result=null;

			String methodName=method.getName();
			if (methodName.startsWith("get"))
				result=methodName.substring(3);
			else
			if (methodName.startsWith("set"))
				result=methodName.substring(3);
			else
			if (methodName.startsWith("is"))
				result=methodName.substring(2);

			if (result!=null)
				result=Introspector.decapitalize(result);

			return result;
		}

		
		/**
		 *
		 *
		 */
		protected int getFieldWidth(Class clazz)
		{
			if (Long.class.isAssignableFrom(clazz) || clazz==Long.TYPE)
			{
				return 8;
			}
			else
			if (Double.class.isAssignableFrom(clazz) || clazz==Double.TYPE)
			{
				return 8;
			}
			else
			if (Integer.class.isAssignableFrom(clazz) || clazz==Integer.TYPE)
			{
				return 4;
			}
			else
			if (Short.class.isAssignableFrom(clazz) || clazz==Short.TYPE)
			{
				return 2;
			}
			else
			if (Byte.class.isAssignableFrom(clazz) || clazz==Byte.TYPE)
			{
				return 1;
			}
			else
//			if (Boolean.class.isAssignableFrom(clazz) || clazz==Boolean.TYPE)
//			{
//				return NativePointer.BOOL_SIZE;
//			}
//			else
			if (Pointer.class.isAssignableFrom(clazz))
			{
				return Pointer.SIZE;
			}
			else
			if (NativeStructureOld.class.isAssignableFrom(clazz))
			{
				// Assume a pointer to a structure
				return Pointer.SIZE;
			}
			else
			if (NativeString.class.isAssignableFrom(clazz))
			{
				// A pointer to a string
				return Pointer.SIZE;
			}
			else
			if (String.class.isAssignableFrom(clazz))
			{
				// Assume a pointer to a string
				return Pointer.SIZE;
			}
			else
			{
				throw new IllegalArgumentException("Unsupported field type \""+
					clazz.getName()+"\"");
			}
		}


		/**
		 *
		 *
		 */
		protected Class getFieldType(Method method)
		{
			boolean readMethod=isReadMethod(method);
			boolean writeMethod=isWriteMethod(method);
			if (readMethod)
			{
				if (method.getParameterTypes().length!=0)
				{
					throw new IllegalArgumentException("Invalid " +
						"interface method \""+method.getName()+"\": "+
						"struct read methods must declare no parameters");
				}

				if (Void.class.isAssignableFrom(method.getReturnType()) || 
					method.getReturnType()==Void.TYPE)
				{
					throw new IllegalArgumentException("Invalid " +
						"interface method \""+method.getName()+"\": "+
						"struct write methods must declare a return " +
						"value");
				}

				return method.getReturnType();
			}
			else
			if (writeMethod)
			{
				if (method.getParameterTypes().length!=1)
				{
					throw new IllegalArgumentException("Invalid " +
						"interface method \""+method.getName()+"\": "+
						"struct write methods must declare only " +
						"one parameter");
				}

				if (!(Void.class.isAssignableFrom(method.getReturnType()) ||
					method.getReturnType()==Void.TYPE))
				{
					throw new IllegalArgumentException("Invalid " +
						"interface method \""+method.getName()+"\": "+
						"struct write methods must not declare a return " +
						"value");
				}

				return method.getParameterTypes()[0];
			}
			else
			{
				throw new IllegalArgumentException("Invalid interface "+
					"method \""+method.getName()+"\": struct methods " +
					"must begin with \"get\", \"set\", or \"is\"");
			}
		}




		////////////////////////////////////////////////////////////////////////
		// InvocationHandler methods
		////////////////////////////////////////////////////////////////////////

		/**
		 *
		 *
		 */
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable
		{
			if (freed)
			{
				throw new IllegalStateException(
					"Structure memory has already been freed");
			}

			// Special case our own free() method
			if (method.equals(METHOD_FREE))
			{
				free();
				return null;
			}

			// Special case our own size() method
			if (method.equals(METHOD_SIZE))
				return new Integer(size);

			if (method.equals(METHOD_GET_FIELD_OFFSET))
				return new Integer(getFieldOffset((String)args[0]));

			if (method.equals(METHOD_GET_MEMORY))
				return getMemory();

			// Derive the name of the field
			String fieldName=getFieldName(method);
			assert fieldName!=null:
				"Field for method \""+method.getName()+"\" not found";

			// Determine the type of the field
			Class fieldType=getFieldType(method);

			// Get the offset of the field
			int offset=getFieldOffset(fieldName);

			boolean readMethod=isReadMethod(method);

			Object result=null;
			if (readMethod)
			{
				// Get the value at the offset according to its type
//				if (fieldType==Boolean.TYPE || fieldType==Boolean.class)
//				{
//					result=new Boolean(getMemory().getBoolean(offset));
//				}
//				else
				if (fieldType==Byte.TYPE || fieldType==Byte.class)
				{
					result=new Byte(getMemory().getByte(offset));
				}
				else
				if (fieldType==Short.TYPE || fieldType==Short.class)
				{
					result=new Short(getMemory().getShort(offset));
				}
				else
				if (fieldType==Integer.TYPE || fieldType==Integer.class)
				{
					result=new Integer(getMemory().getInt(offset));
				}
				else
				if (fieldType==Long.TYPE || fieldType==Long.class)
				{
					result=new Long(getMemory().getLong(offset));
				}
				else
				if (fieldType==Float.TYPE || fieldType==Float.class)
				{
					result=new Float(getMemory().getFloat(offset));
				}
				else
				if (fieldType==Double.TYPE || fieldType==Double.class)
				{
					result=new Double(getMemory().getDouble(offset));
				}
				else
				if (Pointer.class.isAssignableFrom(fieldType))
				{
					result=getMemory().getPointer(offset);
				}
				else
				if (NativeString.class.isAssignableFrom(fieldType))
				{
					result=new NativeString(getMemory().getPointer(offset));
				}
				else
				if (fieldType==String.class)
				{
					return new NativeString(
						getMemory().getPointer(offset)).toString();
				}
				else
				{
					throw new IllegalArgumentException(
						"Unsupported field type \""+fieldType.getClass()+"\"");
				}
			}
			else
			{
				// Get the value to write to the field
				Object value=Pointer.NULL;
				if (String.class.isAssignableFrom(fieldType))
				{
					// Allocate a new string in memory
					NativeString nativeString=new NativeString((String)args[0]);
					value=nativeString;

					// If we previously allocated a string for this field,
					// we can free it now
					NativeString oldString=(NativeString)
						allocatedStrings.get(fieldName);
					if (oldString!=null)
						oldString.free();

					// Remember the string object so we can free it later.
					// Recall that the developer will never have access to
					// this pointer or memory, so he could never free it if
					// we didn't do this.
					allocatedStrings.put(fieldName,nativeString);
				}
				else
				if (NativeStructureOld.class.isAssignableFrom(fieldType))
				{
					// A structure is really a pointer; write the pointer 
					// instead
					Handler handler=(Handler)Proxy.getInvocationHandler(proxy);
					value=handler.getMemory();
				}
				else
				{
					// Otherwise, the value was provided directly
					if (!readMethod)
					{
						value=args[0];

						// The value must of the declared field type (should
						// always be the case)
						if (!fieldType.isPrimitive() && 
							!fieldType.isInstance(value))
						{
							throw new IllegalArgumentException(
								"Type mismatch: argument of type \""+
								value.getClass()+"\" (value = \""+value+
								"\") is not assignable to "+
								"\""+fieldType.getName()+"\"");
						}
					}
				}

				// Get the value at the offset according to its type
//				if (fieldType==Boolean.TYPE || fieldType==Boolean.class)
//				{
//					getMemory().setBoolean(offset,
//						((Boolean)value).booleanValue());
//				}
//				else
				if (fieldType==Byte.TYPE || fieldType==Byte.class)
				{
					getMemory().setByte(offset,((Byte)value).byteValue());
				}
				else
				if (fieldType==Short.TYPE || fieldType==Short.class)
				{
					getMemory().setShort(offset,((Short)value).shortValue());
				}
				else
				if (fieldType==Integer.TYPE || fieldType==Integer.class)
				{
					getMemory().setInt(offset,((Integer)value).intValue());
				}
				else
				if (fieldType==Long.TYPE || fieldType==Long.class)
				{
					getMemory().setLong(offset,((Long)value).longValue());
				}
				else
				if (fieldType==Float.TYPE || fieldType==Float.class)
				{
					getMemory().setFloat(offset,((Float)value).floatValue());
				}
				else
				if (fieldType==Double.TYPE || fieldType==Double.class)
				{
					getMemory().setDouble(offset,((Double)value).doubleValue());
				}
				else
				if (Pointer.class.isAssignableFrom(fieldType))
				{
					getMemory().setPointer(offset,(Pointer)value);
				}
				else
				if (NativeString.class.isAssignableFrom(fieldType))
				{
					getMemory().setPointer(offset,
						((NativeString)value).getPointer());
				}
				else
				if (fieldType==String.class)
				{
					getMemory().setPointer(offset,
						((NativeString)value).getPointer());
				}
				else
				{
					throw new IllegalArgumentException(
						"Unsupported field type \""+fieldType.getClass()+"\"");
				}
				
			}

			return result;
		}




		////////////////////////////////////////////////////////////////////////
		// Inner class
		////////////////////////////////////////////////////////////////////////

		/**
		 *
		 *
		 */
		private class StructField extends Object
		{
			public String name;
			public Class type;
			public int width;
		}




		////////////////////////////////////////////////////////////////////////
		// Class fields
		////////////////////////////////////////////////////////////////////////

		private static Method METHOD_FREE;
		private static Method METHOD_SIZE;
		private static Method METHOD_GET_FIELD_OFFSET;
		private static Method METHOD_GET_MEMORY;



		////////////////////////////////////////////////////////////////////////
		// Instance fields
		////////////////////////////////////////////////////////////////////////

		private Memory memory;
		private Class interfaceClass;
		private boolean freed;
		private int size;
		private StructField[] structFields;
		private Map allocatedStrings=new HashMap();




		////////////////////////////////////////////////////////////////////////
		// Initializers
		////////////////////////////////////////////////////////////////////////

		static
		{
			try
			{
				METHOD_FREE=NativeStructureOld.class.getMethod(
					"free",new Class[0]);
				METHOD_SIZE=NativeStructureOld.class.getMethod(
					"size",new Class[0]);
				METHOD_GET_FIELD_OFFSET=NativeStructureOld.class.getMethod(
					"getFieldOffset",new Class[] { String.class } );
				METHOD_GET_MEMORY=NativeStructureOld.class.getMethod(
					"getMemory",new Class[0]);
			}
			catch (NoSuchMethodException e)
			{
				// Cannot happen
				assert false:
					"free() method not found";
			}
		}
	}
}

