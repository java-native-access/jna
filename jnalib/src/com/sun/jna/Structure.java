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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a native structure with a Java peer class. 
 * <p>
 * See the <a href=overview.html>overview</a> for supported type mappings.
 * <p>
 * Note: this class
 * and its methods are not threadsafe! You must ensure that this class is
 * used in a safe manner.<p>
 * Note: Strings are used to represent native C strings because usage of 
 * <code>char *</code> is generally more common than <code>wchar_t *</code>.
 *
 * @author  Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
public abstract class Structure {

    // validated for 32-bit linux/gcc; align on minimum of 32-bits or field size
    protected static final int ALIGN_GNUC = 0;
    // validated for w32/msvc; align on field size boundary
    protected static final int ALIGN_MSVC = 1;
    
    protected static final int CALCULATE_SIZE = -1;

    private Pointer memory;
    private boolean freed;
    private int size = -1;
    private int alignType;
    private int structAlignment;
    private Map structFields = new LinkedHashMap();
    // Keep track of java strings which have been converted to C strings
    private Map nativeStrings = new HashMap();

    public static int defaultAlignment() {
        if (System.getProperty("os.name").startsWith("Windows"))
            return ALIGN_MSVC;
        return ALIGN_GNUC;
    }

    protected Structure() {
        this(CALCULATE_SIZE);
    }

    protected Structure(int size) {
        this(size, defaultAlignment());
    }

    protected Structure(int size, int alignment) {
        this.alignType = alignment;
        allocateMemory(size);
    }

    protected void allocateMemory() {
        allocateMemory(calculateSize());
    }
    
    /** Set the memory used by this structure.  This method is used to 
     * indicate the given structure is nested within another or otherwise
     * overlaid on some other memory block and thus does not own its own 
     * memory.
     */
    protected void useMemory(Pointer m) {
        useMemory(m, 0);
    }

    /** Set the memory used by this structure.  This method is used to 
     * indicate the given structure is nested within another or otherwise
     * overlaid on some other memory block and thus does not own its own 
     * memory.
     */
    protected void useMemory(Pointer m, int offset) {
        if (memory instanceof Memory) {
            ((Memory)memory).free();
        }
        this.memory = m.share(offset, size());
        this.freed = false;
    }
    
    protected Pointer getMemory() {
        return memory;
    }
    
    /** Provided for derived classes to indicate a different
     * size than the default.
     */
    protected void allocateMemory(int size) {
        if (size == CALCULATE_SIZE) {
            // Analyze the struct
            size = calculateSize();
        }
        else if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }
        
        if (size > 0) {
            if (memory instanceof Memory) {
                ((Memory)memory).free();
            }
            memory = new Memory(size);
            this.size = size;
            this.freed = false;
        }
    }

    protected void finalize() {
        free();
    }

    public int size() {
        if (size == -1) {
            allocateMemory();
        }
        return size;
    }

    public void clear() {
        memory.clear(size);
    }

    /**
     * Free the memory for the struct. Note, this method is not threadsafe! 
     */
    public void free() {

        if (memory instanceof Memory) {
            ((Memory)memory).free();
        }
        // The memory used by the native strings will be reclaimed as
        // the objects are GC'd
        nativeStrings.clear();
        freed = true;
    }

    /** Return a {@link Pointer} object to this structure. */
    public Pointer getPointer() {
        write();
        return memory;
    }

    //////////////////////////////////////////////////////////////////////////
    // Synchronization methods
    //////////////////////////////////////////////////////////////////////////


    /**
     * Reads the fields of the struct from native memory
     */
    public void read() {
        if (freed) {
            throw new IllegalStateException("No structure memory is available");
        }

        // Read all fields
        for (Iterator i=structFields.values().iterator();i.hasNext();) {
            StructField f = (StructField)i.next();
            if (Structure.class.isAssignableFrom(f.type)) {
                try {
                    Structure s = (Structure)f.field.get(this);
                    s.useMemory(memory, f.offset);
                    s.read();
                }
                catch (IllegalAccessException e) {
                }
            }
            else {
                readField(f);
            }
        }
    }


    private void readField(StructField structField) {

        // Get the offset of the field
        int offset = structField.offset;

        // Determine the type of the field
        Class fieldType = structField.type;

        // Get the value at the offset according to its type
        Object result = null;
        if (fieldType == Byte.TYPE || fieldType == Byte.class) {
            result = new Byte(memory.getByte(offset));
        }
        else if (fieldType == Short.TYPE || fieldType == Short.class) {
            result = new Short(memory.getShort(offset));
        }
        else if (fieldType == Character.TYPE || fieldType == Character.class) {
            result = new Character(memory.getChar(offset));
        }
        else if (fieldType == Integer.TYPE || fieldType == Integer.class) {
            result = new Integer(memory.getInt(offset));
        }
        else if (fieldType == Long.TYPE || fieldType == Long.class) {
            result = new Long(memory.getLong(offset));
        }
        else if (fieldType == Float.TYPE || fieldType == Float.class) {
            result=new Float(memory.getFloat(offset));
        }
        else if (fieldType == Double.TYPE || fieldType == Double.class) {
            result = new Double(memory.getDouble(offset));
        }
        else if (Pointer.class.isAssignableFrom(fieldType)) {
            result = memory.getPointer(offset);
        }
        else if (fieldType == String.class) {
            Pointer p = memory.getPointer(offset);
            result = p != null ? new NativeString(p, false).toString() : null;
        }
        else if (fieldType == WString.class) {
            Pointer p = memory.getPointer(offset);
            result = p != null ? new NativeString(p, true).toString() : null;
        }
        else if (fieldType.isArray()) {
            Class cls = fieldType.getComponentType();
            int length = 0;
            try {
                Object o = structField.field.get(this);
                if (o == null) {
                    throw new IllegalStateException("Structure array field not initialized");
                }
                length = Array.getLength(o);
            }
            catch (IllegalArgumentException e) {
            }
            catch (IllegalAccessException e) {
            }

            if (cls == byte.class) {
                result = memory.getByteArray(offset, length);
            }
            else if (cls == char.class) {
                result = memory.getCharArray(offset, length);
            }
            else if (cls == short.class) {
                result = memory.getShortArray(offset, length);
            }
            else if (cls == int.class) {
                result = memory.getIntArray(offset, length);
            }
            else if (cls == long.class) {
                result = memory.getLongArray(offset, length);
            }
            else if (cls == float.class) {
                result = memory.getFloatArray(offset, length);
            }
            else if (cls == double.class) {
                result = memory.getDoubleArray(offset, length);
            }
            else {
                throw new IllegalArgumentException("Array of "
                                                   + cls + " not supported");
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported field type \""
                                               + fieldType.getClass() + "\"");
        }

        // Set the value on the field
        try {
            structField.field.set(this,result);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception setting field \""
                                       + structField.name+"\"", e);
        }
    }


    /**
     * Writes the fields of the struct to native memory
     */
    public void write() {
        if (freed) {
            throw new IllegalStateException("Memory has been freed");
        }
        // convenience: allocate memory if it hasn't been already; this
        // allows structures to inline arrays of primitive types and not have
        // to explicitly call allocateMemory in the ctor
        if (size == -1) {
            allocateMemory();
        }
        // Write all fields
        for (Iterator i=structFields.values().iterator();i.hasNext();) {
            writeField((StructField)i.next());
        }
    }

    private void writeField(Structure.StructField structField) {
        // Get the offset of the field
        int offset = structField.offset;

        // Determine the type of the field
        Class fieldType = structField.type;

        // Get the value from the field
        Object value = null;
        try {
            value = structField.field.get(this);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception reading field \""
                                       + structField.name + "\"", e);
        }

        // Java strings get converted to C strings, where a Pointer is used
        if (String.class == fieldType
            || WString.class == fieldType) {

            // Allocate a new string in memory
            boolean wide = fieldType == WString.class;
            if (value != null) {
                NativeString nativeString = new NativeString(value.toString(), wide);
                // Keep track of allocated C strings to avoid 
                // premature garbage collection of the memory.
                nativeStrings.put(structField.name, nativeString);
                value = nativeString.getPointer();
            }
            else {
                value = null;
            }
        }

        // Get the value at the offset according to its type
        if (fieldType == Byte.TYPE || fieldType == Byte.class) {
            memory.setByte(offset, ((Byte)value).byteValue());
        }
        else if (fieldType == Short.TYPE || fieldType == Short.class) {
            memory.setShort(offset, ((Short)value).shortValue());
        }
        else if (fieldType == Integer.TYPE || fieldType == Integer.class) {
            memory.setInt(offset, ((Integer)value).intValue());
        }
        else if (fieldType == Long.TYPE || fieldType == Long.class) {
            memory.setLong(offset, ((Long)value).longValue());
        }
        else if (fieldType == Float.TYPE || fieldType == Float.class) {
            memory.setFloat(offset, ((Float)value).floatValue());
        }
        else if (fieldType == Double.TYPE || fieldType == Double.class) {
            memory.setDouble(offset, ((Double)value).doubleValue());
        }
        else if (Pointer.class.isAssignableFrom(fieldType)) {
            memory.setPointer(offset, (Pointer)value);
        }
        else if (fieldType == String.class) {
            memory.setPointer(offset, (Pointer)value);
        }
        else if (fieldType == WString.class) {
            memory.setPointer(offset, (Pointer)value);
        }
        else if (fieldType.isArray()) {
            Class cls = fieldType.getComponentType();
            if (cls == byte.class) {
                byte[] buf = (byte[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else if (cls == char.class) {
                char[] buf = (char[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else if (cls == short.class) {
                short[] buf = (short[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else if (cls == int.class) {
                int[] buf = (int[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else if (cls == long.class) {
                long[] buf = (long[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else if (cls == float.class) {
                float[] buf = (float[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else if (cls == double.class) {
                double[] buf = (double[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else {
                throw new IllegalArgumentException("Inline array of "
                                                   + cls + " not supported");
            }
        }
        else if (Structure.class.isAssignableFrom(fieldType)) {
            Structure s = (Structure)value;
            s.useMemory(memory, offset);
            s.write();
        }
        else {
            throw new IllegalArgumentException("Field \"" + structField.name
                                               + "\" was declared as an "
                                               + "unsupported type \""
                                               + fieldType.getClass() + "\"");
        }
    }


    private int calculateSize() {
        // TODO: maybe cache this information on a per-class basis
        // so that we don't have to re-analyze this static information each 
        // time a struct is allocated.
		
        // TODO: Handle derived structures
        // Currently, we're not accounting for superclasses with declared
        // fields.  We need to walk the inheritance tree and build up a list
        // of all struct fields.  Note, there cannot be any conflicts because
        // the Java compiler will enforce this for public fields.
        structAlignment = 1;
        int calculatedSize = 0;
        Field[] fields = getClass().getFields();
        for (int i=0; i<fields.length; i++) {
            Field field = fields[i];
            if ((field.getModifiers() & Modifier.STATIC) != 0)
                continue;
            
            StructField structField = new StructField();
            structField.field = field;
            structField.name = field.getName();
            
            // Currently only simple scalar types supported
            structField.type = field.getType();
            
            int fieldAlignment = 1;
            if (structField.size < 1) {
                try {
                    Object value = field.get(this);
                    if (value == null) {
                        Class type = field.getType();
                        if (Structure.class.isAssignableFrom(type)) {
                            try {
                                value = type.newInstance();
                                field.set(this, value);
                            }
                            catch(InstantiationException e) {
                                String msg = "Can't determine size of  nested structure: " 
                                    + e.getMessage();
                                throw new IllegalArgumentException(msg);
                            }
                        }
                        else if (type.isArray()) {
                            // can't calculate yet, defer until later
                            return -1;
                        }
                    }
                    structField.size = getNativeSize(field.getType(), value);
                    fieldAlignment = getNativeAlignment(field.getType(), value);
                }
                catch (IllegalAccessException e) {
                }
            }
            
            // Align fields as appropriate
            structAlignment = Math.max(structAlignment, fieldAlignment);
            if ((calculatedSize % fieldAlignment) != 0) {
                calculatedSize += fieldAlignment - (calculatedSize % fieldAlignment);
            }
            structField.offset = calculatedSize;
            calculatedSize += structField.size;
            
            // Save the field in our list
            structFields.put(structField.name, structField);
        }

        // Structure size must be an integral multiple of its alignment,
        // add padding if necessary.
        if ((calculatedSize % structAlignment) != 0) {
            calculatedSize += structAlignment - (calculatedSize % structAlignment);
        }
        
        if (calculatedSize > 0) {
            return calculatedSize;
        }

        throw new IllegalArgumentException("Structure " + getClass()
                                           + " has invalid size (ensure "
                                           + "all fields are public)");
    }

    private int getNativeAlignment(Class type, Object value) {
        int alignment = 1;
        int size = getNativeSize(type, value);
        if (type.isPrimitive() || Long.class == type || Integer.class == type
            || Short.class == type || Character.class == type 
            || Byte.class == type 
            || Float.class == type || Double.class == type) {
            alignment = size;
        }
        else if (Pointer.class.isAssignableFrom(type)
                 || WString.class.isAssignableFrom(type)
                 || String.class.isAssignableFrom(type)) {
            alignment = Pointer.SIZE;
        }
        else if (Structure.class.isAssignableFrom(type)) {
            alignment = ((Structure)value).structAlignment;
        }
        else if (type.isArray()) {
            alignment = getNativeAlignment(type.getComponentType(), null);
        }
        else {
            throw new IllegalArgumentException("Type " + type + " has unknown "
                                               + "native alignment");
        }
        if (alignType == ALIGN_MSVC)
            return Math.min(8, alignment);
        return Math.min(4, alignment);
    }

    private int getNativeSize(Class type, Object value) {
        if (long.class == type || Long.class == type) {
            structAlignment = Math.max(8, structAlignment);
            return 8;
        }
        else if (double.class == type || Double.class == type) {
            structAlignment = Math.max(8, structAlignment);
            return 8;
        }
        else if (float.class == type || Float.class == type) {
            structAlignment = Math.max(4, structAlignment);
            return 4;
        }
        else if (int.class == type || Integer.class == type) {
            structAlignment = Math.max(4, structAlignment);
            return 4;
        }
        else if (short.class == type || Short.class == type
                 || char.class == type || Character.class == type) {
            structAlignment = Math.max(2, structAlignment);
            return 2;
        }
        else if (byte.class == type || Byte.class == type) {
            return 1;
        }
        else if (Pointer.class.isAssignableFrom(type)) {
            return Pointer.SIZE;
        }
        else if (value instanceof Structure) {
            Structure s = (Structure)value;
            // inline structure
            int size = s.size();
            structAlignment = Math.max(s.structAlignment, structAlignment);
            return size;
        }
        else if (String.class == type
                 || WString.class == type) { 
            // C string (pointer to char/wchar_t)
            return Pointer.SIZE;
        }
        else if (type.isArray()) {
            int len = Array.getLength(value);
            if (len > 0) {
                Object o = Array.get(value, 0);
                if (o != null)
                    return len * getNativeSize(o.getClass(), o);
                // zero-length arrays are not ordinary...
            }
            return 0;
        }
        else {
            throw new IllegalArgumentException("Unsupported field type \""
                                               + type.getName() + "\"");
        }
    }
    
    public Structure[] toArray(Structure[] array) {
        array[0] = this;
        int size = size();
        for (int i=1;i < array.length;i++) {
            try {
                array[i] = (Structure)getClass().newInstance();
                array[i].useMemory(getPointer().share(i*size, size));
            }
            catch (InstantiationException e) {
                throw new IllegalArgumentException("Error instantiating "
                                                   + getClass() + ": " + e);
            }
            catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Not allowed to instantiate "
                                                   + getClass() + ": " + e);
            }
        }
        return array;
    }
    
    public Structure[] toArray(int size) {
        return toArray(new Structure[size]);
    }

    static class StructField extends Object {
        public String name;
        public Class type;
        public Field field;
        public int size = -1;
        public int offset = -1;
    }
}
