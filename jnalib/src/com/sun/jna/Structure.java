package com.sun.jna;

import java.util.*;
import com.sun.jna.annotation.*;
import java.lang.annotation.Annotation;

/**
 * Represents a native structure with a Java peer class. Note, this class
 * and its methods are not threadsafe! You must ensure that this class is
 * used in a safe manner.
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
public abstract /*final*/ class Structure extends Object
{
	/**
	 *
	 *
	 */
	protected Structure()
		/*throws InstantiationException, IllegalAccessException*/
	{
		super();

		// Analyze the struct
		analyze(this.getClass());

		// Allocate the memory
		memory=new Memory(size);
		
		// Create an instance for the Java side of the struct
//		struct=structClass.newInstance();
		struct=this;
	}

//	TODO: It would be a lot simpler to just make this class extensible so
//	that library method signatures could directly use specific types instead of 
//	Structure<Foo>. However, this would loosen control over the struct
//	lifecycle (wouldn't it?) and possibly feel a bit less elegant in the 
//	struct declaration (would be no need for the @NativeStructure annotation,
//	which is nice because it makes everything crystal clear). It would 
//	eliminate the ugly use of the struct() method, though. That alone might
//	be reason enough...
	
//	/**
//	 *
//	 *
//	 */
//	public static <C> Structure<C> allocate(Class<C> structClass)
//		throws InstantiationException, IllegalAccessException
//	{
//		return new Structure<C>(structClass);
//	}
	

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
	public void clear()
	{
		getMemory().clear();
	}


	/**
	 *
	 *
	 */
	public int size()
	{
		return size;
	}


	/**
	 * Free the memory for the struct. Note, this method is not threadsafe! 
	 *
	 */
	public void free()
	{
		struct=null;

		if (!freed)
		{
			freed=true;
			getMemory().free();

			// Free all dependent strings that we allocated along the way
			for (NativeString string : allocatedStrings.values())
				string.free();
			
			allocatedStrings.clear();
		}
	}


	/**
	 *
	 *
	 */
	public int getFieldOffset(String fieldName)
	{
		Structure.Field field=structFields.get(fieldName);
		if (field==null)
		{
			throw new IllegalArgumentException("Field \""+fieldName+
				"\" not found");
		}

		assert field.offset!=-1:
			"Offset for field \""+fieldName+"\" was not properly initialized";

		return field.offset;
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
	 * Return the Java struct object. Note, this method is not threadsafe!
	 *
	 */
//	public S struct()
	private Structure struct()
	{
		if (freed)
		{
			throw new IllegalStateException("The structure has "+
				"already been freed and cannot be accessed");
		}
		
		return struct;
	}

	
	/**
	 *
	 *
	 */
	public boolean autoSync()
	{
		return autoSync;
	}

	


	////////////////////////////////////////////////////////////////////////////
	// Synchronization methods
	////////////////////////////////////////////////////////////////////////////


	/**
	 * Reads the fields of the struct from native memory
	 *
	 */
	public void read()
	{
		if (freed)
		{
			throw new IllegalStateException(
				"Structure memory has already been freed");
		}

		// Read all fields
		for (Structure.Field field : structFields.values())
			readField(field);
	}


	/**
	 *
	 *
	 */
	private void readField(Structure.Field structField)
	{
		// Get the offset of the field
		int offset=structField.offset;

		// Determine the type of the field
		Class fieldType=structField.type;

		// Get the value at the offset according to its type
		Object result=null;
//		if (fieldType==Boolean.TYPE || fieldType==Boolean.class)
//		{
//			result=new Boolean(getMemory().getBoolean(offset));
//		}
//		else
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
			result=new NativeString(getMemory().getPointer(offset)).toString();
		}
		else
		{
			throw new IllegalArgumentException(
				"Unsupported field type \""+fieldType.getClass()+"\"");
		}

		// Set the value on the field
		try
		{
			structField.field.set(struct(),result);
		}
		catch (Exception e)
		{
			throw new RuntimeException(
				"Exception setting field \""+structField.name+"\"",e);
		}
	}


	/**
	 * Writes the fields of the struct to native memory
	 *
	 */
	public void write()
	{
		if (freed)
		{
			throw new IllegalStateException(
				"Structure memory has already been freed");
		}

		// Write all fields
		for (Structure.Field field : structFields.values())
			writeField(field);
	}


	/**
	 *
	 *
	 */
	private void writeField(Structure.Field structField)
	{
		// Get the offset of the field
		int offset=structField.offset;

		// Determine the type of the field
		Class fieldType=structField.type;

		// Get the value from the field
		Object value=Pointer.NULL;
		try
		{
			value=structField.field.get(struct());
		}
		catch (Exception e)
		{
			throw new RuntimeException(
				"Exception reading field \""+structField.name+"\"",e);
		}

		// Special case String and Structure types
		if (String.class.isAssignableFrom(fieldType))
		{
			// Allocate a new string in memory
			NativeString nativeString=new NativeString((String)value);
			value=nativeString;

			// If we previously allocated a string for this field,
			// we can free it now
			NativeString oldString=(NativeString)
				allocatedStrings.get(structField.name);
			if (oldString!=null)
				oldString.free();

			// Remember the string object so we can free it later.
			// Recall that the developer will never have access to
			// this pointer or memory, so he could never free it if
			// we didn't do this.
			allocatedStrings.put(structField.name,nativeString);
		}
		else
		if (Structure.class.isAssignableFrom(fieldType))
		{
			// A structure is really a pointer; write the pointer 
			// instead
			value=((Structure)value).getMemory();
		}

		// Get the value at the offset according to its type
//		if (fieldType==Boolean.TYPE || fieldType==Boolean.class)
//		{
//			getMemory().setBoolean(offset,
//				((Boolean)value).booleanValue());
//		}
//		else
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
			getMemory().setPointer(offset,((NativeString)value).getPointer());
		}
		else
		{
			throw new IllegalArgumentException(
				"Field \""+structField.name+"\" was declared as an "+
				"unsupported type \""+fieldType.getClass()+"\"");
		}
	}




	////////////////////////////////////////////////////////////////////////////
	// Analysis methods
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
//	private void analyze(Class<S> structClass)
	private void analyze(Class structClass)
	{
		// TODO: We should cache this information in WeakMap so that we
		// don't have to reanalyze this static information each time a struct
		// is allocated.
		
		// Ensure the class is annotated, even if there is no specific 
		// info available.  This ensures that the semantics are clear.
		Annotation annotation=
			structClass.getAnnotation(NativeStructure.class);
		if (annotation==null)
		{
			throw new IllegalArgumentException("Structure class must be " +
				"annotated with the @"+NativeStructure.class.getName()+
				" annotation");
		}

		autoSync=((NativeStructure)annotation).autoSync();
			
		// TODO: Currently, we're not accounting for superclasses with declared
		// fields.  We need to walk the inheritance tree and build up a list
		// of all struct fields.  Note, there cannot be any conflicts because
		// the Java compiler will enforce this for public fields.

		int calculatedSize=0;
		java.lang.reflect.Field[] fields=structClass.getFields();
		for (int i=0; i<fields.length; i++)
		{
			java.lang.reflect.Field field=fields[i];

			// Skip any non-annotated fields
			NativeField fieldAnnotation=field.getAnnotation(NativeField.class);
			if (fieldAnnotation==null)
				continue;

			Structure.Field structField=new Structure.Field();
			structField.field=field;
			structField.name=field.getName();

			// Simple scalar types only please
			structField.type=field.getType();
			if (structField.type.isArray())
			{
				throw new IllegalArgumentException("The field \""+
					structField.name+"\" cannot be an array type");
			}

			// TODO: Account for fields that overlap!  For now assume that
			// none overlap.
			structField.size=fieldAnnotation.size();
			if (structField.size<1)
				structField.size=getFieldSize(structField.type);

// TAF: Note, this code was never completed--treat as suspect
//			// Calculate the offset; assume that if one is not specified, the
//			// field comes immediately after the prior field
//			// TODO: Account for fields that overlap!  For now assume that
//			// none overlap.
//			structField.offset=fieldAnnotation.offset();
//			if (structField.offset<=0)
//			{
//				structField.offset=calculatedSize;
//			}
//			else
//			if (structField.offset<baseOffset)
//			{
//				throw new IllegalArgumentException("Offset for field \""+
//					structField.name+"\" overlaps a previously specified "+
//					"field offset. If specified, field offsets must be "+
//					"specified in ascending order.");
//			}

			// TODO: For now, do not allow specification of offset and simply
			// assume that all fields of the struct are present
			structField.offset=calculatedSize;
			calculatedSize+=structField.size;

			// Save the field in our list
			structFields.put(structField.name,structField);
		}

		// Remember the size of the struct
		int declaredSize=((NativeStructure)annotation).size();
		size=declaredSize > 0 ? declaredSize : calculatedSize;
	}


	/**
	 *
	 *
	 */
	protected int getFieldSize(Class clazz)
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
		if (Structure.class.isAssignableFrom(clazz))
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




	////////////////////////////////////////////////////////////////////////
	// Inner class
	////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	/*pkg*/ static class Field extends Object
	{
		public String name;
		public Class type;
		public java.lang.reflect.Field field;
		public int size=-1;
		public int offset=-1;
	}




	////////////////////////////////////////////////////////////////////////////
	// Instance members
	////////////////////////////////////////////////////////////////////////////

//	private S struct;
	private Structure struct;
	private Memory memory;
	private boolean freed;
	private boolean sizeSpecified;
	private int size;
	private boolean autoSync;
	private Map<String,Structure.Field> structFields=
		new LinkedHashMap<String,Structure.Field>();
	private Map<String,NativeString> allocatedStrings=
		new HashMap<String,NativeString>();
}
