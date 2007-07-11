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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a native structure with a Java peer class. 
 * <p>
 * See the <a href=overview.html>overview</a> for supported type mappings.
 * <p>
 * NOTE: Strings are used to represent native C strings because usage of 
 * <code>char *</code> is generally more common than <code>wchar_t *</code>.<p>
 * NOTE: This class assumes that fields are returned in {@link Class#getFields}
 * in the same or reverse order as declared.  If your VM returns them in
 * no particular order, you're out of luck.
 *
 * @author  Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
public abstract class Structure {
    
    private static class MemberOrder {
        public int first;
        public int middle;
        public int last;
    }
    
    private static final boolean REVERSE_FIELDS;
    
    static {
        // IBM and JRockit store fields in reverse order; check for it
        Field[] fields = MemberOrder.class.getFields();
        REVERSE_FIELDS = "last".equals(fields[0].getName());
        if (!"middle".equals(fields[1].getName())) {
            throw new Error("This VM does not store fields in a predictable order");
        }
    }

    public static final int ALIGN_DEFAULT = 0;
    /** No alignment, place all fields on nearest 1-byte boundary */
    public static final int ALIGN_NONE = 1;
    /** validated for 32-bit x86 linux/gcc; align field size, max 4 bytes */
    public static final int ALIGN_GNUC = 2;
    /** validated for w32/msvc; align on field size */
    public static final int ALIGN_MSVC = 3;
    
    protected static final int CALCULATE_SIZE = -1;

    private Pointer memory;
    private int size = CALCULATE_SIZE;
    private int alignType;
    private int structAlignment;
    private Map structFields = new LinkedHashMap();
    // Keep track of java strings which have been converted to C strings
    private Map nativeStrings = new HashMap();
    private TypeMapper typeMapper;

    protected Structure() {
        this(CALCULATE_SIZE);
    }

    protected Structure(int size) {
        this(size, ALIGN_DEFAULT);
    }

    protected Structure(int size, int alignment) {
        setAlignType(alignment);
        setTypeMapper(null);
        allocateMemory(size);
    }
    
    /** Return all fields in this structure (ordered). */
    Map fields() {
        return structFields;
    }

    /** Change the type mapping for this structure.  May cause the structure
     * to be resized and any existing memory to be reallocated.  
     * If <code>null</code>, the default mapper for the
     * defining class will be used.
     */
    protected void setTypeMapper(TypeMapper mapper) {
        if (mapper == null) {
            Class declaring = getClass().getDeclaringClass();
            if (declaring != null) {
                mapper = Native.getTypeMapper(declaring);
            }
        }
        this.typeMapper = mapper;
        this.size = CALCULATE_SIZE;
        this.memory = null;
    }
    
    /** Change the alignment of this structure.  Re-allocates memory if 
     * necessary.  If alignment is {@link #ALIGN_DEFAULT}, the default 
     * alignment for the defining class will be used. 
     */
    protected void setAlignType(int alignType) {
        if (alignType == ALIGN_DEFAULT) {
            Class declaring = getClass().getDeclaringClass();
            if (declaring != null) 
                alignType = Native.getStructureAlignment(declaring);
            if (alignType == ALIGN_DEFAULT) {
                if (Platform.isWindows())
                    alignType = ALIGN_MSVC;
                else
                    alignType = ALIGN_GNUC;
            }
        }
        this.alignType = alignType;
        this.size = CALCULATE_SIZE;
        this.memory = null;
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
        this.memory = m.share(offset, size());
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
            throw new IllegalArgumentException("Size must be greater than zero: " + size);
        }
        // May need to defer size calculation if derived class not fully
        // initialized
        if (size != CALCULATE_SIZE) {
            memory = new Memory(size);
            // Always clear new structure memory
            memory.clear(size);
            this.size = size;
        }
    }

    public int size() {
        if (size == CALCULATE_SIZE) {
            allocateMemory();
        }
        return size;
    }

    public void clear() {
        memory.clear(size);
    }

    /** Return a {@link Pointer} object to this structure. */
    public Pointer getPointer() {
        return memory;
    }

    //////////////////////////////////////////////////////////////////////////
    // Data synchronization methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Reads the fields of the struct from native memory
     */
    public void read() {
        // Read all fields
        for (Iterator i=structFields.values().iterator();i.hasNext();) {
            readField((StructField)i.next());
        }
    }

    void readField(StructField structField) {
        
        // Get the offset of the field
        int offset = structField.offset;

        // Determine the type of the field
        Class nativeType = structField.type;
        FromNativeConverter readConverter = structField.readConverter;
        if (readConverter != null) {
            nativeType = readConverter.nativeType();
        }

        // Get the value at the offset according to its type
        Object result = null;
        if (Structure.class.isAssignableFrom(nativeType)) {
            Structure s = null;
            try {
                s = (Structure)structField.field.get(this);
                s.useMemory(memory, offset);
                s.read();
            }
            catch (IllegalAccessException e) {
            }
            result = s;
        }
        else if (nativeType == byte.class || nativeType == Byte.class) {
            result = new Byte(memory.getByte(offset));
        }
        else if (nativeType == short.class || nativeType == Short.class) {
            result = new Short(memory.getShort(offset));
        }
        else if (nativeType == char.class || nativeType == Character.class) {
            result = new Character(memory.getChar(offset));
        }
        else if (nativeType == int.class || nativeType == Integer.class) {
            result = new Integer(memory.getInt(offset));
        }
        else if (nativeType == long.class || nativeType == Long.class) {
            result = new Long(memory.getLong(offset));
        }
        else if (nativeType == NativeLong.class) {
            result = memory.getNativeLong(offset);
        }
        else if (nativeType == float.class || nativeType == Float.class) {
            result=new Float(memory.getFloat(offset));
        }
        else if (nativeType == double.class || nativeType == Double.class) {
            result = new Double(memory.getDouble(offset));
        }
        else if (Pointer.class.isAssignableFrom(nativeType)) {
            result = memory.getPointer(offset);
        }
        else if (nativeType == String.class) {
            Pointer p = memory.getPointer(offset);
            result = p != null ? new NativeString(p, false).toString() : null;
        }
        else if (nativeType == WString.class) {
            Pointer p = memory.getPointer(offset);
            result = p != null ? new NativeString(p, true).toString() : null;
        }
        else if (Callback.class.isAssignableFrom(nativeType)) {
            // ignore; Callback members are write-only (don't try to convert
            // a native function pointer to a Java Callback)
            // TODO: may want to warn if the value has been changed by
            // native code
            return;
        }
        else if (nativeType.isArray()) {
            Class cls = nativeType.getComponentType();
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
            else if (cls == short.class) {
                result = memory.getShortArray(offset, length);
            }
            else if (cls == char.class) {
                result = memory.getCharArray(offset, length);
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
                                               + nativeType + "\"");
        }

        if (readConverter != null) {
            result = readConverter.fromNative(result, structField.context);
        }

        // Set the value on the field
        try {
            structField.field.set(this, result);
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
        // convenience: allocate memory if it hasn't been already; this
        // allows structures to inline arrays of primitive types and not have
        // to explicitly call allocateMemory in the ctor
        if (size == CALCULATE_SIZE) {
            allocateMemory();
        }
        // Write all fields
        for (Iterator i=structFields.values().iterator();i.hasNext();) {
            writeField((StructField)i.next());
        }
    }

    void writeField(StructField structField) {
        // Get the offset of the field
        int offset = structField.offset;

        // Get the value from the field
        Object value = null;
        try {
            value = structField.field.get(this);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception reading field \""
                                       + structField.name + "\"", e);
        }
        // Determine the type of the field
        Class nativeType = structField.type;
        ToNativeConverter converter = structField.writeConverter;
        if (converter != null) {
            value = converter.toNative(value);
            // Assume any null values are pointers
            nativeType = value != null ? value.getClass() : Pointer.class;
        }

        // Java strings get converted to C strings, where a Pointer is used
        if (String.class == nativeType
            || WString.class == nativeType) {

            // Allocate a new string in memory
            boolean wide = nativeType == WString.class;
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
        if (nativeType == byte.class || nativeType == Byte.class) {
            memory.setByte(offset, ((Byte)value).byteValue());
        }
        else if (nativeType == short.class || nativeType == Short.class) {
            memory.setShort(offset, ((Short)value).shortValue());
        }
        else if (nativeType == char.class || nativeType == Character.class) {
            memory.setChar(offset, ((Character)value).charValue());
        }
        else if (nativeType == int.class || nativeType == Integer.class) {
            memory.setInt(offset, ((Integer)value).intValue());
        }
        else if (nativeType == long.class || nativeType == Long.class) {
            memory.setLong(offset, ((Long)value).longValue());
        }
        else if (nativeType == NativeLong.class) {
            memory.setNativeLong(offset, ((NativeLong)value));
        }
        else if (nativeType == float.class || nativeType == Float.class) {
            memory.setFloat(offset, ((Float)value).floatValue());
        }
        else if (nativeType == double.class || nativeType == Double.class) {
            memory.setDouble(offset, ((Double)value).doubleValue());
        }
        else if (Pointer.class.isAssignableFrom(nativeType)) {
            memory.setPointer(offset, (Pointer)value);
        }
        else if (nativeType == String.class) {
            memory.setPointer(offset, (Pointer)value);
        }
        else if (nativeType == WString.class) {
            memory.setPointer(offset, (Pointer)value);
        }
        else if (nativeType.isArray()) {
            Class cls = nativeType.getComponentType();
            if (cls == byte.class) {
                byte[] buf = (byte[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else if (cls == short.class) {
                short[] buf = (short[])value;
                memory.write(offset, buf, 0, buf.length);
            }
            else if (cls == char.class) {
                char[] buf = (char[])value;
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
        else if (Structure.class.isAssignableFrom(nativeType)) {
            Structure s = (Structure)value;
            s.useMemory(memory, offset);
            s.write();
        }
        else if (Callback.class.isAssignableFrom(nativeType)) {
            Pointer p = null;
            if (value != null) {
                CallbackReference cbref = CallbackReference.getInstance((Callback)value);
                p = cbref.getTrampoline();
            }
            memory.setPointer(offset, p);
        }
        else {
            throw new IllegalArgumentException("Field \"" + structField.name
                                               + "\" was declared as an "
                                               + "unsupported type \""
                                               + nativeType + "\"");
        }
    }


    /** Calculate the amount of native memory required for this structure.
     * May return {@link #CALCULATE_SIZE} if the size can not yet be 
     * determined (usually due to fields in the derived class not yet
     * being initialized).
     */
    int calculateSize() {
        // TODO: maybe cache this information on a per-class basis
        // so that we don't have to re-analyze this static information each 
        // time a struct is allocated.
		
        // Currently, we're not accounting for superclasses with declared
        // fields.  Since C structs have no inheritance, this shouldn't be
        // an issue.
        structAlignment = 1;
        int calculatedSize = 0;
        Field[] fields = getClass().getFields();
        if (REVERSE_FIELDS) {
            for (int i=0;i < fields.length/2;i++) {
                int idx = fields.length-1-i;
                Field tmp = fields[i];
                fields[i] = fields[idx];
                fields[idx] = tmp;
            }
        }
        for (int i=0; i<fields.length; i++) {
            Field field = fields[i];
            if ((field.getModifiers() & Modifier.STATIC) != 0)
                continue;
            
            Class type = field.getType();
            StructField structField = new StructField();
            structField.field = field;
            structField.name = field.getName();
            structField.type = type;
            
            int fieldAlignment = 1;
            try {
                Object value = field.get(this);
                if (value == null) {
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
                    else if (NativeLong.class == type) {
                        field.set(this, value = new NativeLong(0));
                    }
                    else if (type.isArray()) {
                        // can't calculate size yet, defer until later
                        return CALCULATE_SIZE;
                    }
                }
                Class nativeType = type;
                if (typeMapper != null) {
                    ToNativeConverter writeConverter = typeMapper.getToNativeConverter(type);
                    FromNativeConverter readConverter = typeMapper.getFromNativeConverter(type);
                    if (writeConverter != null && readConverter != null) {
                        value = writeConverter.toNative(value);
                        nativeType = value != null ? value.getClass() : Pointer.class;
                        structField.writeConverter = writeConverter;
                        structField.readConverter = readConverter;
                        structField.context = new StructureReadContext(type, this);
                    }
                    else if (writeConverter != null || readConverter != null) {
                        String msg = "Structures require bidirectional type conversion for " + type;
                        throw new IllegalArgumentException(msg);
                    }
                }
                structField.size = getNativeSize(nativeType, value);
                fieldAlignment = getNativeAlignment(nativeType, value, i==0);
            }
            catch (IllegalAccessException e) {
                // ignore non-public fields
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

        if (calculatedSize > 0) {
            return calculateAlignedSize(calculatedSize);
        }

        throw new IllegalArgumentException("Structure " + getClass()
                                           + " has unknown size (ensure "
                                           + "all fields are public)");
    }
    
    int calculateAlignedSize(int calculatedSize) {
        // Structure size must be an integral multiple of its alignment,
        // add padding if necessary.
        if (alignType != ALIGN_NONE) {
            if ((calculatedSize % structAlignment) != 0) {
                calculatedSize += structAlignment - (calculatedSize % structAlignment);
            }
        }
        return calculatedSize;
    }

    /** Overridable in subclasses. */
    // TODO: write getNaturalAlignment(stack/alloc) + getEmbeddedAlignment(structs)
    // TODO: move this into a native call which detects default alignment
    // automatically
    protected int getNativeAlignment(Class type, Object value, boolean firstElement) {
        int alignment = 1;
        int size = getNativeSize(type, value);
        if (type.isPrimitive() || Long.class == type || Integer.class == type
            || NativeLong.class == type
            || Short.class == type || Character.class == type 
            || Byte.class == type 
            || Float.class == type || Double.class == type) {
            alignment = size;
        }
        else if (Pointer.class.isAssignableFrom(type)
                 || Callback.class.isAssignableFrom(type)
                 || WString.class.isAssignableFrom(type)
                 || String.class.isAssignableFrom(type)) {
            alignment = Pointer.SIZE;
        }
        else if (Structure.class.isAssignableFrom(type)) {
            alignment = ((Structure)value).structAlignment;
        }
        else if (type.isArray()) {
            alignment = getNativeAlignment(type.getComponentType(), null, firstElement);
        }
        else {
            throw new IllegalArgumentException("Type " + type + " has unknown "
                                               + "native alignment");
        }
        if (alignType == ALIGN_NONE)
            return 1;
        if (alignType == ALIGN_MSVC)
            return Math.min(8, alignment);
        if (alignType == ALIGN_GNUC) {
            // NOTE this is published ABI for 32-bit gcc/linux/x86, osx/x86,
            // and osx/ppc.  osx/ppc special-cases the first element
            if (!firstElement || !"ppc".equals(System.getProperty("os.arch")))
                return Math.min(4, alignment);
        }
        return alignment;
    }

    /** Returns the native size for classes which don't need an object instance
     * to determine size.
     */
    protected int getNativeSize(Class cls) {
        if (cls == byte.class || cls == Byte.class) return 1;
        if (cls == short.class || cls == Short.class) return 2; 
        if (cls == char.class || cls == Character.class) return Pointer.WCHAR_SIZE;
        if (cls == int.class || cls == Integer.class) return 4;
        if (cls == long.class || cls == Long.class) return 8;
        if (cls == float.class || cls == Float.class) return 4;
        if (cls == double.class || cls == Double.class) return 8;
        if (NativeLong.class.isAssignableFrom(cls)) return Pointer.LONG_SIZE;
        if (Pointer.class.isAssignableFrom(cls)
            || Callback.class.isAssignableFrom(cls)
            || String.class == cls
            || WString.class == cls) {
            return Pointer.SIZE;
        }
        throw new IllegalArgumentException("Native type undefined for " + cls);
    }
    /** Returns the native size of the given class, in bytes. */
    protected int getNativeSize(Class type, Object value) {
        if (Structure.class.isAssignableFrom(type)) {
            Structure s = (Structure)value;
            // inline structure
            return s.size();
        }
        if (type.isArray()) {
            int len = Array.getLength(value);
            if (len > 0) {
                Object o = Array.get(value, 0);
                return len * getNativeSize(type.getComponentType(), o);
            }
            // Don't process zero-length arrays
            throw new IllegalArgumentException("Arrays of length zero not allowed in structure: " + this);
        }
        return getNativeSize(type);
    }
    
    public String toString() {
        String LS = System.getProperty("line.separator");
        String name = getClass().getName() + "(" + getPointer() + ")";
        String contents = "";
        // Write all fields
        for (Iterator i=structFields.values().iterator();i.hasNext();) {
            contents += "  " + i.next();
            contents += LS;
        }
        byte[] buf = getPointer().getByteArray(0, size());
        final int BYTES_PER_ROW = 4;
        contents += "memory dump" + LS;
        for (int i=0;i < buf.length;i++) {
            if ((i % BYTES_PER_ROW) == 0) contents += "[";
            if (buf[i] >=0 && buf[i] < 16)
                contents += "0";
            contents += Integer.toHexString(buf[i] & 0xFF);
            if ((i % BYTES_PER_ROW) == BYTES_PER_ROW-1 && i < buf.length-1)
                contents += "]" + LS;
        }
        contents += "]";
        return name + LS + contents;
    }
    
    /** Returns a view of this structure's memory as an array of structures.
     * Note that this <code>Structure</code> must have a public, no-arg
     * constructor.  If the structure is currently using a {@link Memory}
     * backing, the memory will be resized to fit the entire array.
     */
    public Structure[] toArray(Structure[] array) {
        if (memory instanceof Memory) {
            // reallocate if necessary
            Memory m = (Memory)memory;
            int requiredSize = array.length * size();
            if (m.getSize() < requiredSize) {
                useMemory(new Memory(requiredSize));
            }
        }
        array[0] = this;
        int size = size();
        for (int i=1;i < array.length;i++) {
            try {
                array[i] = (Structure)getClass().newInstance();
                array[i].useMemory(memory.share(i*size, size));
                array[i].read();
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
    
    /** Returns a view of this structure's memory as an array of structures.
     * Note that this <code>Structure</code> must have a public, no-arg
     * constructor.  If the structure is currently using a {@link Memory}
     * backing, the memory will be resized to fit the entire array.
     */
    public Structure[] toArray(int size) {
        return toArray(new Structure[size]);
    }

    /** This structure is only equal to another based on the same native 
     * memory address and data type.
     */
    public boolean equals(Object o) {
        return o == this
            || (o != null
                && o.getClass() == getClass()
                && ((Structure)o).getPointer().equals(getPointer()));
    }
    
    /** Since {@link #equals} depends on the native address, use that
     * as the hashcode.
     */
    public int hashCode() {
        return getPointer().hashCode();
    }

    class StructField extends Object {
        public String name;
        public Class type;
        public Field field;
        public int size = -1;
        public int offset = -1;
        public FromNativeConverter readConverter;
        public ToNativeConverter writeConverter;
        public FromNativeContext context;
        public String toString() {
            Object value = "<unavailable>";
            try {
                value = field.get(Structure.this);
            }
            catch(Exception e) { }
            return type + " " + name + "@" + Integer.toHexString(offset) 
                + "=" + value;
        }
    }    
}
