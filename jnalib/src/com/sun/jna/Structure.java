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
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Represents a native structure with a Java peer class.  When used as a
 * function parameter or return value, this class corresponds to
 * <code>struct*</code>.  When used as a field within another
 * <code>Structure</code>, it corresponds to <code>struct</code>.  The
 * tagging interfaces {@link ByReference} and {@link ByValue} may be used
 * to alter the default behavior.
 * <p>
 * See the <a href={@docRoot}/overview-summary.html>overview</a> for supported
 * type mappings.
 * <p>
 * Structure alignment and type mappings are derived by default from the
 * enclosing interface definition (if any) by using
 * {@link Native#getStructureAlignment} and {@link Native#getTypeMapper}.
 * <p>
 * Structure fields corresponding to native fields <em>must</em> be public.
 * The may additionally have the following modifiers:<br>
 * <ul>
 * <li><code>volatile</code> JNA will not write the field unless specifically
 * instructed to do so via {@link #writeField(String)}.
 * <li><code>final</code> JNA will overwrite the field via {@link #read()},
 * but otherwise the field is not modifiable from Java.
 * </ul>
 * NOTE: Strings are used to represent native C strings because usage of
 * <code>char *</code> is generally more common than <code>wchar_t *</code>.
 * <p>
 * NOTE: This class assumes that fields are returned in {@link Class#getFields}
 * in the same or reverse order as declared.  If your VM returns them in
 * no particular order, you're out of luck.
 *
 * @author  Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
public abstract class Structure {
    
    /** Tagging interface to indicate the value of an instance of the
     * <code>Structure</code> type is to be used in function invocations rather
     * than its address.  The default behavior is to treat
     * <code>Structure</code> function parameters and return values as by
     * reference, meaning the address of the structure is used.
     */
    public interface ByValue { }
    /** Tagging interface to indicate the address of an instance of the
     * Structure type is to be used within a <code>Structure</code> definition
     * rather than nesting the full Structure contents.  The default behavior
     * is to inline <code>Structure</code> fields.
     */
    public interface ByReference { }

    private static class MemberOrder {
        public int first;
        public int middle;
        public int last;
    }

    private static final boolean REVERSE_FIELDS;
    static boolean REQUIRES_FIELD_ORDER;

    static final boolean isPPC;
    static final boolean isSPARC;

    static {
        // IBM and JRockit store fields in reverse order; check for it
        Field[] fields = MemberOrder.class.getFields();
        REVERSE_FIELDS = "last".equals(fields[0].getName());
        REQUIRES_FIELD_ORDER = !"middle".equals(fields[1].getName());
        String arch = System.getProperty("os.arch").toLowerCase();
        isPPC = "ppc".equals(arch) || "powerpc".equals(arch);
        isSPARC = "sparc".equals(arch);
    }

    /** Use the platform default alignment. */
    public static final int ALIGN_DEFAULT = 0;
    /** No alignment, place all fields on nearest 1-byte boundary */
    public static final int ALIGN_NONE = 1;
    /** validated for 32-bit x86 linux/gcc; align field size, max 4 bytes */
    public static final int ALIGN_GNUC = 2;
    /** validated for w32/msvc; align on field size */
    public static final int ALIGN_MSVC = 3;

    private static final int MAX_GNUC_ALIGNMENT = isSPARC ? 8 : Native.LONG_SIZE;
    protected static final int CALCULATE_SIZE = -1;

    // This field is accessed by native code
    private Pointer memory;
    private int size = CALCULATE_SIZE;
    private int alignType;
    private int structAlignment;
    private final Map structFields = new LinkedHashMap();
    // Keep track of java strings which have been converted to C strings
    private final Map nativeStrings = new HashMap();
    private TypeMapper typeMapper;
    // This field is accessed by native code
    private long typeInfo;
    private List fieldOrder;
    private boolean autoRead = true;
    private boolean autoWrite = true;
    private Structure[] array;

    protected Structure() {
        this((Pointer)null);
    }

    protected Structure(TypeMapper mapper) {
        this((Pointer)null, ALIGN_DEFAULT, mapper);
    }

    /** Create a structure cast onto pre-allocated memory. */
    protected Structure(Pointer p) {
        this(p, ALIGN_DEFAULT);
    }

    protected Structure(Pointer p, int alignment) {
        this(p, alignment, null);
    }

    protected Structure(Pointer p, int alignment, TypeMapper mapper) {
        setAlignType(alignment);
        setTypeMapper(mapper);
        if (p != null) {
            useMemory(p);
        }
        else {
            allocateMemory(CALCULATE_SIZE);
        }
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
        if (this.memory instanceof AutoAllocated) {
            this.memory = null;
        }
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
        if (this.memory instanceof AutoAllocated) {
            this.memory = null;
        }
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
        // Invoking size() here is important when this method is invoked
        // from the ctor, to ensure fields are properly scanned and allocated
        try {
            this.memory = m.share(offset, size());
        }
        catch(IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Structure exceeds provided memory bounds");
        }
    }

    protected void ensureAllocated() {
        if (size == CALCULATE_SIZE) {
            allocateMemory();
        }
    }

    /** Attempt to allocate memory if sufficient information is available.
     * Returns whether the operation was successful.
     */
    protected void allocateMemory() {
        allocateMemory(calculateSize(true));
    }

    /** Provided for derived classes to indicate a different
     * size than the default.  Returns whether the operation was successful.
     * Will leave memory untouched if it is non-null and not allocated
     * by this class.
     */
    protected void allocateMemory(int size) {
        if (size == CALCULATE_SIZE) {
            // Analyze the struct, but don't worry if we can't yet do it
            size = calculateSize(false);
        }
        else if (size <= 0) {
            throw new IllegalArgumentException("Structure size must be greater than zero: " + size);
        }
        // May need to defer size calculation if derived class not fully
        // initialized
        if (size != CALCULATE_SIZE) {
            if (this.memory == null 
                || this.memory instanceof AutoAllocated) {
                this.memory = new AutoAllocated(size);
                // Always clear new structure memory
                this.memory.clear(size);
            }
            this.size = size;
        }
    }

    public int size() {
        ensureAllocated();
        return size;
    }

    public void clear() {
        memory.clear(size());
    }

    /** Return a {@link Pointer} object to this structure.  Note that if you
     * use the structure's pointer as a function argument, you are responsible
     * for calling {@link #write()} prior to the call and {@link #read()}
     * after the call.  These calls are normally handled automatically by the
     * {@link Function} object when it encounters a {@link Structure} argument
     * or return value.
     */
    public Pointer getPointer() {
        ensureAllocated();
        return memory;
    }

    //////////////////////////////////////////////////////////////////////////
    // Data synchronization methods
    //////////////////////////////////////////////////////////////////////////

    // Keep track of what is currently being read/written to avoid redundant
    // reads (avoids problems with circular references).
    private static final ThreadLocal busy = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new HashSet();
        }
    };
    private Set busy() {
        return (Set)busy.get();
    }

    /**
     * Reads the fields of the struct from native memory
     */
    public void read() {
        // convenience: allocate memory if it hasn't been already; this
        // allows structures to do field-based initialization of arrays and not
        // have to explicitly call allocateMemory in a ctor
        ensureAllocated();
        // Avoid recursive reads
        if (busy().contains(this)) {
            return;
        }
        busy().add(this);
        try {
            for (Iterator i=structFields.values().iterator();i.hasNext();) {
                readField((StructField)i.next());
            }
        }
        finally {
            busy().remove(this);
        }
    }

    /** Force a read of the given field from native memory.  The Java field
     * will be updated from the current contents of native memory.
     * @return the new field value, after updating
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public Object readField(String name) {
        ensureAllocated();
        StructField f = (StructField)structFields.get(name);
        if (f == null)
            throw new IllegalArgumentException("No such field: " + name);
        return readField(f);
    }

    /** Obtain the value currently in the Java field.  Does not read from
     * memory.
     */
    Object getField(StructField structField) {
        try {
            return structField.field.get(this);
        }
        catch (Exception e) {
            throw new Error("Exception reading field '"
                            + structField.name + "' in " + getClass()
                            + ": " + e);
        }
    }

    void setField(StructField structField, Object value) {
        try {
            structField.field.set(this, value);
        }
        catch(IllegalAccessException e) {
            throw new Error("Unexpectedly unable to write to field '"
                            + structField.name + "' within " + getClass()
                            + ": " + e);
        }
    }

    /** Only keep the original structure if its native address is unchanged.
     * Otherwise replace it with a new object.
     * @param type Structure subclass
     * @param s Original Structure object
     * @param address the native <code>struct *</code>
     * @return Updated <code>Structure.ByReference</code> object
     */
    static Structure updateStructureByReference(Class type, Structure s, Pointer address) {
        if (address == null) {
            s = null;
        }
        else {
            if (s == null || !address.equals(s.getPointer())) {
                s = newInstance(type);
                s.useMemory(address);
            }
            s.autoRead();
        }
        return s;
    }

    /** Read the given field and return its value.  The Java field will be
     * updated from the contents of native memory.
     */
    // TODO: make overridable method with calculated native type, offset, etc
    Object readField(StructField structField) {

        // Get the offset of the field
        int offset = structField.offset;

        // Determine the type of the field
        Class fieldType = structField.type;
        FromNativeConverter readConverter = structField.readConverter;
        if (readConverter != null) {
            fieldType = readConverter.nativeType();
        }
        // Get the current value only for types which might need to be preserved
        Object currentValue = (Structure.class.isAssignableFrom(fieldType)
                               || Callback.class.isAssignableFrom(fieldType)
                               || Buffer.class.isAssignableFrom(fieldType)
                               || Pointer.class.isAssignableFrom(fieldType)
                               || fieldType.isArray())
            ? getField(structField) : null;
        Object result = memory.getValue(offset, fieldType, currentValue);
        // TODO: process against current value here
        
        if (readConverter != null) {
            result = readConverter.fromNative(result, structField.context);
        }

        // Update the value on the field
        setField(structField, result);
        return result;
    }

    /**
     * Writes the fields of the struct to native memory
     */
    public void write() {
        // convenience: allocate memory if it hasn't been already; this
        // allows structures to do field-based initialization of arrays and not
        // have to explicitly call allocateMemory in a ctor
        ensureAllocated();

        // Update native FFI type information, if needed
        if (this instanceof ByValue) {
            getTypeInfo();
        }

        if (busy().contains(this)) {
            return;
        }
        busy().add(this);
        try {
            // Write all fields, except those marked 'volatile'
            for (Iterator i=structFields.values().iterator();i.hasNext();) {
                StructField sf = (StructField)i.next();
                if (!sf.isVolatile) {
                    writeField(sf);
                }
            }
        }
        finally {
            busy().remove(this);
        }
    }

    /** Write the given field to native memory.  The current value in the Java
     * field will be translated into native memory.
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public void writeField(String name) {
        ensureAllocated();
        StructField f = (StructField)structFields.get(name);
        if (f == null)
            throw new IllegalArgumentException("No such field: " + name);
        writeField(f);
    }

    /** Write the given field value to the field and native memory.   The
     * given value will be written both to the Java field and the
     * corresponding native memory.
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public void writeField(String name, Object value) {
        ensureAllocated();
        StructField f = (StructField)structFields.get(name);
        if (f == null)
            throw new IllegalArgumentException("No such field: " + name);
        setField(f, value);
        writeField(f);
    }

    void writeField(StructField structField) {
        // Get the offset of the field
        int offset = structField.offset;

        // Get the value from the field
        Object value = getField(structField);

        // Determine the type of the field
        Class fieldType = structField.type;
        ToNativeConverter converter = structField.writeConverter;
        if (converter != null) {
            value = converter.toNative(value, new StructureWriteContext(this, structField.field));
            fieldType = converter.nativeType();
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
                nativeStrings.remove(structField.name);
            }
        }

        try {
            memory.setValue(offset, value, fieldType);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
            String msg = "Structure field \"" + structField.name
                + "\" was declared as " + structField.type
                + (structField.type == fieldType
                   ? "" : " (native type " + fieldType + ")")
                + ", which is not supported within a Structure";
            throw new IllegalArgumentException(msg);
        }
    }

    protected List getFieldOrder() {
        synchronized(this) {
            if (fieldOrder == null) {
                fieldOrder = new ArrayList();
            }
            return fieldOrder;
        }
    }

    /** Provided for VMs where the field order as returned by {@link
     * Class#getFields()} is not predictable.
     */
    protected void setFieldOrder(String[] fields) {
        getFieldOrder().addAll(Arrays.asList(fields));
    }

    /** Sort the structure fields according to the given array of names. */
    protected void sortFields(Field[] fields, String[] names) {
        for (int i=0;i < names.length;i++) {
            for (int f=i;f < fields.length;f++) {
                if (names[i].equals(fields[f].getName())) {
                    Field tmp = fields[f];
                    fields[f] = fields[i];
                    fields[i] = tmp;
                    break;
                }
            }
        }
    }

    /** Calculate the amount of native memory required for this structure.
     * May return {@link #CALCULATE_SIZE} if the size can not yet be
     * determined (usually due to fields in the derived class not yet
     * being initialized).
     * <p>
     * If the <code>force</code> parameter is <code>true</code> will throw
     * an {@link IllegalStateException} if the size can not be determined.
     * @throws IllegalStateException an array field is not initialized
     * @throws IllegalArgumentException when an unsupported field type is
     * encountered
     */
    int calculateSize(boolean force) {
        // TODO: maybe cache this information on a per-class basis
        // so that we don't have to re-analyze this static information each
        // time a struct is allocated.

        structAlignment = 1;
        int calculatedSize = 0;
        Field[] fields = getClass().getFields();
        // Restrict to valid fields
        List flist = new ArrayList();
        for (int i=0;i < fields.length;i++) {
            int modifiers = fields[i].getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers))
                continue;
            flist.add(fields[i]);
        }
        fields = (Field[])flist.toArray(new Field[flist.size()]);

        if (REVERSE_FIELDS) {
            for (int i=0;i < fields.length/2;i++) {
                int idx = fields.length-1-i;
                Field tmp = fields[i];
                fields[i] = fields[idx];
                fields[idx] = tmp;
            }
        }
        else if (REQUIRES_FIELD_ORDER) {
            List fieldOrder = getFieldOrder();
            if (fieldOrder.size() < fields.length) {
                if (force) {
                    throw new Error("This VM does not store fields in a predictable order; you must use setFieldOrder: " + System.getProperty("java.vendor") + ", " + System.getProperty("java.version"));
                }
                return CALCULATE_SIZE;
            }
            sortFields(fields, (String[])fieldOrder.toArray(new String[fieldOrder.size()]));
        }

        for (int i=0; i<fields.length; i++) {
            Field field = fields[i];
            int modifiers = field.getModifiers();

            Class type = field.getType();
            StructField structField = new StructField();
            structField.isVolatile = Modifier.isVolatile(modifiers);
            structField.field = field;
            if (Modifier.isFinal(modifiers)) {
                field.setAccessible(true);
            }
            structField.name = field.getName();
            structField.type = type;

            // Check for illegal field types
            if (Callback.class.isAssignableFrom(type) && !type.isInterface()) {
                throw new IllegalArgumentException("Structure Callback field '"
                                                   + field.getName()
                                                   + "' must be an interface");
            }
            if (type.isArray()
                && Structure.class.equals(type.getComponentType())) {
                String msg = "Nested Structure arrays must use a "
                    + "derived Structure type so that the size of "
                    + "the elements can be determined";
                throw new IllegalArgumentException(msg);
            }

            int fieldAlignment = 1;
            if (!Modifier.isPublic(field.getModifiers()))
                continue;

            Object value = getField(structField);
            if (value == null) {
                if (Structure.class.isAssignableFrom(type)
                    && !(ByReference.class.isAssignableFrom(type))) {
                    try {
                        value = newInstance(type);
                        setField(structField, value);
                    }
                    catch(IllegalArgumentException e) {
                        String msg = "Can't determine size of nested structure: "
                            + e.getMessage();
                        throw new IllegalArgumentException(msg);
                    }
                }
                else if (type.isArray()) {
                    // can't calculate size yet, defer until later
                    if (force) {
                        throw new IllegalStateException("Array fields must be initialized");
                    }
                    return CALCULATE_SIZE;
                }
            }
            Class nativeType = type;
            if (NativeMapped.class.isAssignableFrom(type)) {
                NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
                if (value == null) {
                    value = tc.defaultValue();
                    setField(structField, value);
                }
                nativeType = tc.nativeType();
                structField.writeConverter = tc;
                structField.readConverter = tc;
                structField.context = new StructureReadContext(this, field);
            }
            else if (typeMapper != null) {
                ToNativeConverter writeConverter = typeMapper.getToNativeConverter(type);
                FromNativeConverter readConverter = typeMapper.getFromNativeConverter(type);
                if (writeConverter != null && readConverter != null) {
                    value = writeConverter.toNative(value,
                                                    new StructureWriteContext(this, structField.field));
                    nativeType = value != null ? value.getClass() : Pointer.class;
                    structField.writeConverter = writeConverter;
                    structField.readConverter = readConverter;
                    structField.context = new StructureReadContext(this, field);
                }
                else if (writeConverter != null || readConverter != null) {
                    String msg = "Structures require bidirectional type conversion for " + type;
                    throw new IllegalArgumentException(msg);
                }
            }
            try {
                structField.size = Native.getNativeSize(nativeType, value);
                fieldAlignment = getNativeAlignment(nativeType, value, i==0);
            }
            catch(IllegalArgumentException e) {
                // Might simply not yet have a type mapper set
                if (!force && typeMapper == null) {
                    return CALCULATE_SIZE;
                }
                String msg = "Invalid Structure field in " + getClass() + ", field name '" + structField.name + "', " + structField.type + ": " + e.getMessage();
                throw new IllegalArgumentException(msg);
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
            int size = calculateAlignedSize(calculatedSize);
            // Update native FFI type information, if needed
            if (this instanceof ByValue) {
                getTypeInfo();
            }
            return size;
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

    protected int getStructAlignment() {
        if (size == CALCULATE_SIZE) {
            // calculate size, but don't allocate memory
            calculateSize(true);
        }
        return structAlignment;
    }

    /** Overridable in subclasses. */
    // TODO: write getNaturalAlignment(stack/alloc) + getEmbeddedAlignment(structs)
    // TODO: move this into a native call which detects default alignment
    // automatically
    protected int getNativeAlignment(Class type, Object value, boolean isFirstElement) {
        int alignment = 1;
        if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc = NativeMappedConverter.getInstance(type); 
            type = tc.nativeType();
            value = tc.toNative(value, new ToNativeContext());
        }
        int size = Native.getNativeSize(type, value);
        if (type.isPrimitive() || Long.class == type || Integer.class == type
            || Short.class == type || Character.class == type
            || Byte.class == type || Boolean.class == type
            || Float.class == type || Double.class == type) {
            alignment = size;
        }
        else if (Pointer.class == type
                 || Buffer.class.isAssignableFrom(type)
                 || Callback.class.isAssignableFrom(type)
                 || WString.class == type
                 || String.class == type) {
            alignment = Pointer.SIZE;
        }
        else if (Structure.class.isAssignableFrom(type)) {
            if (ByReference.class.isAssignableFrom(type)) {
                alignment = Pointer.SIZE;
            }
            else {
                if (value == null)
                    value = newInstance(type);
                alignment = ((Structure)value).getStructAlignment();
            }
        }
        else if (type.isArray()) {
            alignment = getNativeAlignment(type.getComponentType(), null, isFirstElement);
        }
        else {
            throw new IllegalArgumentException("Type " + type + " has unknown "
                                               + "native alignment");
        }
        if (alignType == ALIGN_NONE) {
            alignment = 1;
        }
        else if (alignType == ALIGN_MSVC) {
            alignment = Math.min(8, alignment);
        }
        else if (alignType == ALIGN_GNUC) {
            // NOTE this is published ABI for 32-bit gcc/linux/x86, osx/x86,
            // and osx/ppc.  osx/ppc special-cases the first element
            if (!isFirstElement || !(Platform.isMac() && isPPC)) {
                alignment = Math.min(MAX_GNUC_ALIGNMENT, alignment);
            }
        }
        return alignment;
    }

    public String toString() {
        return toString(0);
    }

    private String format(Class type) {
        String s = type.getName();
        int dot = s.lastIndexOf(".");
        return s.substring(dot + 1);
    }

    private String toString(int indent) {
        String LS = System.getProperty("line.separator");
        String name = format(getClass()) + "(" + getPointer() + ")";
        if (!(getPointer() instanceof Memory)) {
            name += " (" + size() + " bytes)";
        }
        String prefix = "";
        for (int idx=0;idx < indent;idx++) {
            prefix += "  ";
        }
        String contents = "";
        // Write all fields
        for (Iterator i=structFields.values().iterator();i.hasNext();) {
            StructField sf = (StructField)i.next();
            Object value = getField(sf);
            String type = format(sf.type);
            String index = "";
            contents += prefix;
            if (sf.type.isArray() && value != null) {
                type = format(sf.type.getComponentType());
                index = "[" + Array.getLength(value) + "]";
            }
            contents += "  " + type + " "
                + sf.name + index + "@" + Integer.toHexString(sf.offset);
            if (value instanceof Structure) {
                if (value instanceof Structure.ByReference) {
                    String v = value.toString();
                    if (v.indexOf(LS) != -1) {
                        v = v.substring(0, v.indexOf(LS));
                    }
                    value = v + "...}";
                }
                else {
                    value = ((Structure)value).toString(indent + 1);
                }
            }
            contents += "=";
            if (value instanceof Long) {
                contents += Long.toHexString(((Long)value).longValue());
            }
            else if (value instanceof Integer) {
                contents += Integer.toHexString(((Integer)value).intValue());
            }
            else if (value instanceof Short) {
                contents += Integer.toHexString(((Short)value).shortValue());
            }
            else if (value instanceof Byte) {
                contents += Integer.toHexString(((Byte)value).byteValue());
            }
            else {
                contents += String.valueOf(value).trim();
            }
            contents += LS;
            if (!i.hasNext())
                contents += prefix + "}";
        }
        if (indent == 0 && Boolean.getBoolean("jna.dump_memory")) {
            byte[] buf = getPointer().getByteArray(0, size());
            final int BYTES_PER_ROW = 4;
            contents += LS + "memory dump" + LS;
            for (int i=0;i < buf.length;i++) {
                if ((i % BYTES_PER_ROW) == 0) contents += "[";
                if (buf[i] >=0 && buf[i] < 16)
                    contents += "0";
                contents += Integer.toHexString(buf[i] & 0xFF);
                if ((i % BYTES_PER_ROW) == BYTES_PER_ROW-1 && i < buf.length-1)
                    contents += "]" + LS;
            }
            contents += "]";
        }
        return name + " {" + LS + contents;
    }

    /** Returns a view of this structure's memory as an array of structures.
     * Note that this <code>Structure</code> must have a public, no-arg
     * constructor.  If the structure is currently using a {@link Memory}
     * backing, the memory will be resized to fit the entire array.
     */
    public Structure[] toArray(Structure[] array) {
        ensureAllocated();
        if (memory instanceof AutoAllocated) {
            // reallocate if necessary
            Memory m = (Memory)memory;
            int requiredSize = array.length * size();
            if (m.getSize() < requiredSize) {
                m = new AutoAllocated(requiredSize);
                m.clear();
                useMemory(m);
            }
        }
        array[0] = this;
        int size = size();
        for (int i=1;i < array.length;i++) {
            array[i] = Structure.newInstance(getClass());
            array[i].useMemory(memory.share(i*size, size));
            array[i].read();
        }

        if (this instanceof ByReference) {
            this.array = array;
        }

        return array;
    }

    /** Returns a view of this structure's memory as an array of structures.
     * Note that this <code>Structure</code> must have a public, no-arg
     * constructor.  If the structure is currently using a {@link Memory}
     * backing, the memory will be resized to fit the entire array.
     */
    public Structure[] toArray(int size) {
        return toArray((Structure[])Array.newInstance(getClass(), size));
    }

    /** This structure is only equal to another based on the same native
     * memory address and data type.
     */
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Structure && ((Structure)o).size() == size()) {
            if (o.getClass().isAssignableFrom(getClass())
                || getClass().isAssignableFrom(o.getClass())) {
                Structure s = (Structure)o;
                for (Iterator i=fields().keySet().iterator();i.hasNext();) {
                    String name = (String)i.next();
                    Object f1 = readField(name);
                    Object f2 = s.readField(name);
                    if (f1 != null) {
                        if (!f1.equals(f2))
                            return false;
                    }
                    else if (f2 != null) {
                        if (!f2.equals(f1))
                            return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /** Since {@link #equals} depends on the native address, use that
     * as the hash code.
     */
    public int hashCode() {
        Pointer p = getPointer();
        return p != null ? p.hashCode() : 0;
    }

    protected void cacheTypeInfo(Pointer p) {
        typeInfo = p.peer;
    }

    /** Obtain native type information for this structure. */
    Pointer getTypeInfo() {
        Pointer p = getTypeInfo(this);
        cacheTypeInfo(p);
        return p;
    }

    /** Set whether the structure is automatically synched to native memory
        before and after a native function call.  Convenience method for
        <pre><code>
        boolean auto = ...;
        setAutoRead(auto);
        setAutoWrite(auto);
        </code></pre>
    */
    public void setAutoSynch(boolean auto) {
        setAutoRead(auto);
        setAutoWrite(auto);
    }

    /** Set whether the struture is written to native memory prior to
        a native function call.
    */
    public void setAutoRead(boolean auto) {
        this.autoRead = auto;
    }

    /** Returns whether the struture is written to native memory prior to
        a native function call.
    */
    public boolean getAutoRead() {
        return this.autoRead;
    }

    /** Set whether the structure is read from native memory after a native
        function call. 
    */
    public void setAutoWrite(boolean auto) {
        this.autoWrite = auto;
    }

    /** Returns whether the structure is read from native memory after a native
        function call. 
    */
    public boolean getAutoWrite() {
        return this.autoWrite;
    }

    /** Exposed for testing purposes only. */
    static Pointer getTypeInfo(Object obj) {
        return FFIType.get(obj);
    }

    /** Create a new Structure instance of the given type
     * @param type
     * @return the new instance
     * @throws IllegalArgumentException if the instantiation fails
     */
    public static Structure newInstance(Class type) throws IllegalArgumentException {
        try {
            Structure s = (Structure)type.newInstance();
            if (s instanceof ByValue) {
                s.allocateMemory();
            }
            return s;
        }
        catch(InstantiationException e) {
            String msg = "Can't instantiate " + type + " (" + e + ")";
            throw new IllegalArgumentException(msg);
        }
        catch(IllegalAccessException e) {
            String msg = "Instantiation of " + type
                + " not allowed, is it public? (" + e + ")";
            throw new IllegalArgumentException(msg);
        }
    }

    class StructField extends Object {
        public String name;
        public Class type;
        public Field field;
        public int size = -1;
        public int offset = -1;
        public boolean isVolatile;
        public FromNativeConverter readConverter;
        public ToNativeConverter writeConverter;
        public FromNativeContext context;
    }
    /** This class auto-generates an ffi_type structure appropriate for a given
     * structure for use by libffi.  The lifecycle of this structure is easier
     * to manage on the Java side than in native code.
     */
    static class FFIType extends Structure {
        public static class size_t extends IntegerType {
            public size_t() { this(0); }
            public size_t(long value) { super(Native.POINTER_SIZE, value); }
        }
        private static Map typeInfoMap = new WeakHashMap();
        // Native.initIDs initializes these fields to their appropriate
        // pointer values.  These are in a separate class so that they may
        // be initialized prior to loading the FFIType class
        private static class FFITypes {
            private static Pointer ffi_type_void;
            private static Pointer ffi_type_float;
            private static Pointer ffi_type_double;
            private static Pointer ffi_type_longdouble;
            private static Pointer ffi_type_uint8;
            private static Pointer ffi_type_sint8;
            private static Pointer ffi_type_uint16;
            private static Pointer ffi_type_sint16;
            private static Pointer ffi_type_uint32;
            private static Pointer ffi_type_sint32;
            private static Pointer ffi_type_uint64;
            private static Pointer ffi_type_sint64;
            private static Pointer ffi_type_pointer;
        }
        static {
            if (Native.POINTER_SIZE == 0)
                throw new Error("Native library not initialized");
            if (FFITypes.ffi_type_void == null)
                throw new Error("FFI types not initialized");
            typeInfoMap.put(void.class, FFITypes.ffi_type_void);
            typeInfoMap.put(Void.class, FFITypes.ffi_type_void);
            typeInfoMap.put(float.class, FFITypes.ffi_type_float);
            typeInfoMap.put(Float.class, FFITypes.ffi_type_float);
            typeInfoMap.put(double.class, FFITypes.ffi_type_double);
            typeInfoMap.put(Double.class, FFITypes.ffi_type_double);
            typeInfoMap.put(long.class, FFITypes.ffi_type_sint64);
            typeInfoMap.put(Long.class, FFITypes.ffi_type_sint64);
            typeInfoMap.put(int.class, FFITypes.ffi_type_sint32);
            typeInfoMap.put(Integer.class, FFITypes.ffi_type_sint32);
            typeInfoMap.put(short.class, FFITypes.ffi_type_sint16);
            typeInfoMap.put(Short.class, FFITypes.ffi_type_sint16);
            Pointer ctype = Native.WCHAR_SIZE == 2
                ? FFITypes.ffi_type_uint16 : FFITypes.ffi_type_uint32;
            typeInfoMap.put(char.class, ctype);
            typeInfoMap.put(Character.class, ctype);
            typeInfoMap.put(byte.class, FFITypes.ffi_type_sint8);
            typeInfoMap.put(Byte.class, FFITypes.ffi_type_sint8);
            typeInfoMap.put(boolean.class, FFITypes.ffi_type_uint32);
            typeInfoMap.put(Boolean.class, FFITypes.ffi_type_uint32);
            typeInfoMap.put(Pointer.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(String.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(WString.class, FFITypes.ffi_type_pointer);
        }
        // From ffi.h
        private static final int FFI_TYPE_STRUCT = 13;
        // Structure fields
        public size_t size;
        public short alignment;
        public short type = FFI_TYPE_STRUCT;
        public Pointer elements;

        private FFIType(Structure ref) {
            Pointer[] els;
            if (ref instanceof Union) {
                StructField sf = ((Union)ref).biggestField;
                els = new Pointer[] {
                    get(ref.getField(sf), sf.type), null,
                };
            }
            else {
                els = new Pointer[ref.fields().size() + 1];
                int idx = 0;
                for (Iterator i=ref.fields().values().iterator();i.hasNext();) {
                    StructField sf = (StructField)i.next();
                    els[idx++] = get(ref.getField(sf), sf.type);
                }
            }
            init(els);
        }
        // Represent fixed-size arrays as structures of N identical elements
        private FFIType(Object array, Class type) {
            int length = Array.getLength(array);
            Pointer[] els = new Pointer[length+1];
            Pointer p = get(null, type.getComponentType());
            for (int i=0;i < length;i++) {
                els[i] = p;
            }
            init(els);
        }
        private void init(Pointer[] els) {
            elements = new Memory(Pointer.SIZE * els.length);
            elements.write(0, els, 0, els.length);
            write();
        }

        static Pointer get(Object obj) {
            if (obj == null)
                return FFITypes.ffi_type_pointer;
            if (obj instanceof Class)
                return get(null, (Class)obj);
            return get(obj, obj.getClass());
        }

        private static Pointer get(Object obj, Class cls) {
            synchronized(typeInfoMap) {
                Object o = typeInfoMap.get(cls);
                if (o instanceof Pointer) {
                    return (Pointer)o;
                }
                if (o instanceof FFIType) {
                    return ((FFIType)o).getPointer();
                }
                if (Buffer.class.isAssignableFrom(cls)
                    || Callback.class.isAssignableFrom(cls)) {
                    typeInfoMap.put(cls, FFITypes.ffi_type_pointer);
                    return FFITypes.ffi_type_pointer;
                }
                if (Structure.class.isAssignableFrom(cls)) {
                    if (obj == null) obj = newInstance(cls);
                    if (ByReference.class.isAssignableFrom(cls)) {
                        typeInfoMap.put(cls, FFITypes.ffi_type_pointer);
                        return FFITypes.ffi_type_pointer;
                    }
                    FFIType type = new FFIType((Structure)obj);
                    typeInfoMap.put(cls, type);
                    return type.getPointer();
                }
                if (NativeMapped.class.isAssignableFrom(cls)) {
                    NativeMappedConverter c = NativeMappedConverter.getInstance(cls);
                    return get(c.toNative(obj, new ToNativeContext()), c.nativeType());
                }
                if (cls.isArray()) {
                    FFIType type = new FFIType(obj, cls);
                    // Store it in the map to prevent premature GC of type info
                    typeInfoMap.put(obj, type);
                    return type.getPointer();
                }
                throw new IllegalArgumentException("Unsupported type " + cls);
            }
        }
    }
    
    private class AutoAllocated extends Memory {
        public AutoAllocated(int size) {
            super(size);
        }
    }

    public void autoRead() {
        if (getAutoRead()) {
            read();
            if (array != null) {
                for (int i=1;i < array.length;i++) {
                    array[i].autoRead();
                }
            }
        }
    }

    public void autoWrite() {
        if (getAutoWrite()) {
            write();
            if (array != null) {
                for (int i=1;i < array.length;i++) {
                    array[i].autoWrite();
                }
            }
        }
    }
}
