/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.jna;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a native structure with a Java peer class.  When used as a
 * function parameter or return value, this class corresponds to
 * <code>struct*</code>.  When used as a field within another
 * <code>Structure</code>, it corresponds to <code>struct</code>.  The
 * tagging interfaces {@link ByReference} and {@link ByValue} may be used
 * to alter the default behavior.  Structures may have variable size, but only
 * by providing an array field (e.g. byte[]).
 * <p>
 * See the <a href={@docRoot}/overview-summary.html>overview</a> for supported
 * type mappings for struct fields.
 * <p>
 * Structure alignment and type mappings are derived by default from the
 * enclosing interface definition (if any) by using
 * {@link Native#getStructureAlignment} and {@link Native#getTypeMapper}.
 * Alternatively you can explicitly provide alignment, field order, or type
 * mapping by calling the respective Structure functions in your subclass's
 * constructor.
 * </p>
 * <p>Structure fields corresponding to native struct fields <em>must</em> be
 * public.  If your structure is to have no fields of its own, it must be
 * declared abstract.
 * </p>
 * <p>You <em>must</em> annotate the class with {@link FieldOrder} or implement
 * {@link #getFieldOrder}, whichever you choose it must contain the field names
 * (Strings) indicating the proper order of the fields. If you chose to implement
 * {@link #getFieldOrder} notice that when dealing with multiple levels of
 * subclasses of Structure, you must add to the list provided by the superclass
 * {@link #getFieldOrder} the fields defined in the current class.
 * </p>
 * <p>In the past, most VMs would return them in a predictable order, but the JVM
 * spec does not require it, so {@link #getFieldOrder} is now required to
 * ensure JNA knows the proper order).
 * </p>
 * <p>Structure fields may additionally have the following modifiers:</p>
 * <ul>
 * <li><code>volatile</code> JNA will not write the field unless specifically
 * instructed to do so via {@link #writeField(String)}.  This allows you to
 * prevent inadvertently overwriting memory that may be updated in real time
 * on another (possibly native) thread.
 * <li><code>final</code> JNA will overwrite the field via {@link #read()},
 * but otherwise the field is not modifiable from Java.  Take care when using
 * this option, since the compiler will usually assume <em>all</em> accesses
 * to the field (for a given Structure instance) have the same value.  This
 * modifier is invalid to use on J2ME.
 * </ul>
 * <p>NOTE: Strings are used to represent native C strings because usage of
 * <code>char *</code> is generally more common than <code>wchar_t *</code>.
 * You may provide a type mapper ({@link com.sun.jna.win32.W32APITypeMapper
 * example here)} if you prefer to use String in place of {@link WString} if
 * your native code predominantly uses <code>wchar_t *</code>.
 * </p>
 * <p>NOTE: In general, instances of this class are <em>not</em> synchronized.
 * </p>
 *
 * @author  Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
public abstract class Structure {

    private static final Logger LOG = Logger.getLogger(Structure.class.getName());

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

    /** Use the platform default alignment. */
    public static final int ALIGN_DEFAULT = 0;
    /** No alignment, place all fields on nearest 1-byte boundary */
    public static final int ALIGN_NONE = 1;
    /** validated for 32-bit x86 linux/gcc; align field size, max 4 bytes */
    public static final int ALIGN_GNUC = 2;
    /** validated for w32/msvc; align on field size */
    public static final int ALIGN_MSVC = 3;

    /** Align to a 2-byte boundary. */
    //public static final int ALIGN_2 = 4;
    /** Align to a 4-byte boundary. */
    //public static final int ALIGN_4 = 5;
    /** Align to an 8-byte boundary. */
    //public static final int ALIGN_8 = 6;

    protected static final int CALCULATE_SIZE = -1;
    static final Map<Class<?>, LayoutInfo> layoutInfo = new WeakHashMap<Class<?>, LayoutInfo>();
    static final Map<Class<?>, List<String>> fieldOrder = new WeakHashMap<Class<?>, List<String>>();

    // This field is accessed by native code
    private Pointer memory;
    private int size = CALCULATE_SIZE;
    private int alignType;
    private String encoding;
    private int actualAlignType;
    private int structAlignment;
    private Map<String, StructField> structFields;
    // Keep track of native C strings which have been allocated,
    // corresponding to String fields of this Structure
    private final Map<String, Object> nativeStrings = new HashMap<String, Object>();
    private TypeMapper typeMapper;
    // This field is accessed by native code
    private long typeInfo;

    private boolean autoRead = true;
    private boolean autoWrite = true;
    // Keep a reference when this structure is mapped to an array
    private Structure[] array;
    private boolean readCalled;

    protected Structure() {
        this(ALIGN_DEFAULT);
    }

    protected Structure(TypeMapper mapper) {
        this(null, ALIGN_DEFAULT, mapper);
    }

    protected Structure(int alignType) {
        this(null, alignType);
    }

    protected Structure(int alignType, TypeMapper mapper) {
        this(null, alignType, mapper);
    }

    /** Create a structure cast onto pre-allocated memory. */
    protected Structure(Pointer p) {
        this(p, ALIGN_DEFAULT);
    }

    protected Structure(Pointer p, int alignType) {
        this(p, alignType, null);
    }

    protected Structure(Pointer p, int alignType, TypeMapper mapper) {
        setAlignType(alignType);
        setStringEncoding(Native.getStringEncoding(getClass()));
        initializeTypeMapper(mapper);
        validateFields();
        if (p != null) {
            useMemory(p, 0, true);
        }
        else {
            allocateMemory(CALCULATE_SIZE);
        }
        initializeFields();
    }

    /** Return all fields in this structure (ordered).  This represents the
     * layout of the structure, and will be shared among Structures of the
     * same class except when the Structure can have a variable size.
     * NOTE: {@link #ensureAllocated()} <em>must</em> be called prior to
     * calling this method.
     * @return {@link Map} of field names to field representations.
     */
    Map<String, StructField> fields() {
        return structFields;
    }

    /**
     * @return the type mapper in effect for this Structure.
     */
    TypeMapper getTypeMapper() {
        return typeMapper;
    }

    /** Initialize the type mapper for this structure.
     * If <code>null</code>, the default mapper for the
     * defining class will be used.
     * @param mapper Find the type mapper appropriate for this structure's
     * context if none was explicitly set.
     */
    private void initializeTypeMapper(TypeMapper mapper) {
        if (mapper == null) {
            mapper = Native.getTypeMapper(getClass());
        }
        this.typeMapper = mapper;
        layoutChanged();
    }

    /** Call whenever a Structure setting is changed which might affect its
     * memory layout.
     */
    private void layoutChanged() {
        if (this.size != CALCULATE_SIZE) {
            this.size = CALCULATE_SIZE;
            if (this.memory instanceof AutoAllocated) {
                this.memory = null;
            }
            // recalculate layout, since it was done once already
            ensureAllocated();
        }
    }

    /** Set the desired encoding to use when writing String fields to native
     * memory.
     * @param encoding desired encoding
     */
    protected void setStringEncoding(String encoding) {
        this.encoding = encoding;
    }

    /** Encoding to use to convert {@link String} to native <code>const
     * char*</code>.  Defaults to {@link Native#getDefaultStringEncoding()}.
     * @return Current encoding
     */
    protected String getStringEncoding() {
        return this.encoding;
    }

    /** Change the alignment of this structure.  Re-allocates memory if
     * necessary.  If alignment is {@link #ALIGN_DEFAULT}, the default
     * alignment for the defining class will be used.
     * @param alignType desired alignment type
     */
    protected void setAlignType(int alignType) {
        this.alignType = alignType;
        if (alignType == ALIGN_DEFAULT) {
            alignType = Native.getStructureAlignment(getClass());
            if (alignType == ALIGN_DEFAULT) {
                if (Platform.isWindows())
                    alignType = ALIGN_MSVC;
                else
                    alignType = ALIGN_GNUC;
            }
        }
        this.actualAlignType = alignType;
        layoutChanged();
    }

    /**
     * Obtain auto-allocated memory for use with struct represenations.
     * @param size desired size
     * @return newly-allocated memory
     */
    protected Memory autoAllocate(int size) {
        return new AutoAllocated(size);
    }

    /** Set the memory used by this structure.  This method is used to
     * indicate the given structure is nested within another or otherwise
     * overlaid on some other memory block and thus does not own its own
     * memory.
     * @param m Memory to with which to back this {@link Structure}.
     */
    protected void useMemory(Pointer m) {
        useMemory(m, 0);
    }

    /** Set the memory used by this structure.  This method is used to
     * indicate the given structure is based on natively-allocated data,
     * nested within another, or otherwise overlaid on existing memory and
     * thus does not own its own memory allocation.
     * @param m Base memory to use to back this structure.
     * @param offset offset into provided memory where structure mapping
     * should start.
     */
    protected void useMemory(Pointer m, int offset) {
        useMemory(m, offset, false);
    }

    /** Set the memory used by this structure.  This method is used to
     * indicate the given structure is based on natively-allocated data,
     * nested within another, or otherwise overlaid on existing memory and
     * thus does not own its own memory allocation.
     * @param m Native pointer
     * @param offset offset from pointer to use
     * @param force ByValue structures normally ignore requests to use a
     * different memory offset; this input is set <code>true</code> when
     * setting a ByValue struct that is nested within another struct.
     */
    void useMemory(Pointer m, int offset, boolean force) {
        try {
            // Clear any local cache
            nativeStrings.clear();

            if (this instanceof ByValue && !force) {
                // ByValue parameters always use dedicated memory, so only
                // copy the contents of the original
                byte[] buf = new byte[size()];
                m.read(0, buf, 0, buf.length);
                this.memory.write(0, buf, 0, buf.length);
            }
            else {
                // Ensure our memory pointer is initialized, even if we can't
                // yet figure out a proper size/layout
                this.memory = m.share(offset);
                if (size == CALCULATE_SIZE) {
                    size = calculateSize(false);
                }
                if (size != CALCULATE_SIZE) {
                    this.memory = m.share(offset, size);
                }
            }
            this.array = null;
            this.readCalled = false;
        }
        catch(IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Structure exceeds provided memory bounds", e);
        }
    }

    /** Ensure this memory has its size and layout calculated and its
        memory allocated. */
    protected void ensureAllocated() {
        ensureAllocated(false);
    }

    /** Ensure this memory has its size and layout calculated and its
        memory allocated.
        @param avoidFFIType used when computing FFI type information
        to avoid recursion
    */
    private void ensureAllocated(boolean avoidFFIType) {
        if (memory == null) {
            allocateMemory(avoidFFIType);
        }
        else if (size == CALCULATE_SIZE) {
            this.size = calculateSize(true, avoidFFIType);
            if (!(this.memory instanceof AutoAllocated)) {
                // Ensure we've set bounds on the shared memory used
                try {
                    this.memory = this.memory.share(0, this.size);
                }
                catch(IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Structure exceeds provided memory bounds", e);
                }
            }
        }
    }

    /** Attempt to allocate memory if sufficient information is available.
     * Returns whether the operation was successful.
     */
    protected void allocateMemory() {
        allocateMemory(false);
    }

    private void allocateMemory(boolean avoidFFIType) {
        allocateMemory(calculateSize(true, avoidFFIType));
    }


    /** Provided for derived classes to indicate a different
     * size than the default.  Returns whether the operation was successful.
     * Will leave memory untouched if it is non-null and not allocated
     * by this class.
     * @param size how much memory to allocate
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
                this.memory = autoAllocate(size);
            }
            this.size = size;
        }
    }

    /** Returns the size in memory occupied by this Structure.
     * @return Native size of this structure, in bytes.
     */
    public int size() {
        ensureAllocated();
        return this.size;
    }

    /** Clears the native memory associated with this Structure. */
    public void clear() {
        ensureAllocated();
        memory.clear(size());
    }

    /** Return a {@link Pointer} object to this structure.  Note that if you
     * use the structure's pointer as a function argument, you are responsible
     * for calling {@link #write()} prior to the call and {@link #read()}
     * after the call.  These calls are normally handled automatically by the
     * {@link Function} object when it encounters a {@link Structure} argument
     * or return value.
     * The returned pointer may not have meaning for {@link Structure.ByValue}
     * structure representations.
     * @return Native pointer representation of this structure.
     */
    public Pointer getPointer() {
        ensureAllocated();
        return memory;
    }

    //////////////////////////////////////////////////////////////////////////
    // Data synchronization methods
    //////////////////////////////////////////////////////////////////////////

    // Keep track of ByReference reads to avoid redundant reads of the same
    // address
    private static final ThreadLocal<Map<Pointer, Structure>> reads = new ThreadLocal<Map<Pointer, Structure>>() {
        @Override
        protected synchronized Map<Pointer, Structure> initialValue() {
            return new HashMap<Pointer, Structure>();
        }
    };

    // Keep track of what is currently being read/written to avoid redundant
    // reads (avoids problems with circular references).
    private static final ThreadLocal<Set<Structure>> busy = new ThreadLocal<Set<Structure>>() {
        @Override
        protected synchronized Set<Structure> initialValue() {
            return new StructureSet();
        }
    };

    /** Avoid using a hash-based implementation since the hash code
            for a Structure is not immutable.
     */
    static class StructureSet extends AbstractCollection<Structure> implements Set<Structure> {
        Structure[] elements;
        private int count;
        private void ensureCapacity(int size) {
            if (elements == null) {
                elements = new Structure[size*3/2];
            }
            else if (elements.length < size) {
                Structure[] e = new Structure[size*3/2];
                System.arraycopy(elements, 0, e, 0, elements.length);
                elements = e;
            }
        }
        public Structure[] getElements() {
            return elements;
        }
        @Override
        public int size() { return count; }
        @Override
        public boolean contains(Object o) {
            return indexOf((Structure) o) != -1;
        }
        @Override
        public boolean add(Structure o) {
            if (!contains(o)) {
                ensureCapacity(count+1);
                elements[count++] = o;
            }
            return true;
        }
        private int indexOf(Structure s1) {
            for (int i=0;i < count;i++) {
                Structure s2 = elements[i];
                if (s1 == s2
                    || (s1.getClass() == s2.getClass()
                        && s1.size() == s2.size()
                        && s1.getPointer().equals(s2.getPointer()))) {
                    return i;
                }
            }
            return -1;
        }
        @Override
        public boolean remove(Object o) {
            int idx = indexOf((Structure) o);
            if (idx != -1) {
                if (--count >= 0) {
                    elements[idx] = elements[count];
                    elements[count] = null;
                }
                return true;
            }
            return false;
        }
        /** Simple implementation so that toString() doesn't break.
            Provides an iterator over a snapshot of this Set.
        */
        @Override
        public Iterator<Structure> iterator() {
            Structure[] e = new Structure[count];
            if (count > 0) {
                System.arraycopy(elements, 0, e, 0, count);
            }
            return Arrays.asList(e).iterator();
        }
    }

    static Set<Structure> busy() {
        return busy.get();
    }
    static Map<Pointer, Structure> reading() {
        return reads.get();
    }

    /** Performs auto-read only if uninitialized. */
    void conditionalAutoRead() {
        if (!readCalled) {
            autoRead();
        }
    }

    /**
     * Reads the fields of the struct from native memory
     */
    public void read() {
        // Avoid reading from a null pointer
        if (memory == PLACEHOLDER_MEMORY) {
            return;
        }
        readCalled = true;

        // convenience: allocate memory and/or calculate size if it hasn't
        // been already; this allows structures to do field-based
        // initialization of arrays and not have to explicitly call
        // allocateMemory in a ctor
        ensureAllocated();

        // Avoid redundant reads
        if (busy().contains(this)) {
            return;
        }
        busy().add(this);
        if (this instanceof Structure.ByReference) {
            reading().put(getPointer(), this);
        }
        try {
            for (StructField structField : fields().values()) {
                readField(structField);
            }
        }
        finally {
            busy().remove(this);
            if (reading().get(getPointer()) == this) {
                reading().remove(getPointer());
            }
        }
    }

    /** Returns the calculated offset of the given field.
     * @param name field to examine
     * @return return offset of the given field
     */
    protected int fieldOffset(String name) {
        ensureAllocated();
        StructField f = fields().get(name);
        if (f == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        return f.offset;
    }

    /** Force a read of the given field from native memory.  The Java field
     * will be updated from the current contents of native memory.
     * @param name field to be read
     * @return the new field value, after updating
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public Object readField(String name) {
        ensureAllocated();
        StructField f = fields().get(name);
        if (f == null)
            throw new IllegalArgumentException("No such field: " + name);
        return readField(f);
    }

    /** Obtain the value currently in the Java field.  Does not read from
     * native memory.
     * @param field field to look up
     * @return current field value (Java-side only)
     */
    Object getFieldValue(Field field) {
        try {
            return field.get(this);
        }
        catch (Exception e) {
            throw new Error("Exception reading field '" + field.getName() + "' in " + getClass(), e);
        }
    }

    /**
     * @param field field to set
     * @param value value to set
     */
    void setFieldValue(Field field, Object value) {
        setFieldValue(field, value, false);
    }

    private void setFieldValue(Field field, Object value, boolean overrideFinal) {

        try {
            field.set(this, value);
        }
        catch(IllegalAccessException e) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers)) {
                if (overrideFinal) {
                    // WARNING: setAccessible(true) on J2ME does *not* allow
                    // overwriting of a final field.
                    throw new UnsupportedOperationException("This VM does not support Structures with final fields (field '" + field.getName() + "' within " + getClass() + ")", e);
                }
                throw new UnsupportedOperationException("Attempt to write to read-only field '" + field.getName() + "' within " + getClass(), e);
            }
            throw new Error("Unexpectedly unable to write to field '" + field.getName() + "' within " + getClass(), e);
        }
    }

    /** Only keep the original structure if its native address is unchanged.
     * Otherwise replace it with a new object.
     * @param type Structure subclass
     * @param s Original Structure object
     * @param address the native <code>struct *</code>
     * @return Updated <code>Structure.ByReference</code> object
     */
    static <T extends Structure> T updateStructureByReference(Class<T> type, T s, Pointer address) {
        if (address == null) {
            s = null;
        }
        else {
            if (s == null || !address.equals(s.getPointer())) {
                Structure s1 = reading().get(address);
                if (s1 != null && type.equals(s1.getClass())) {
                    s = (T) s1;
                    s.autoRead();
                }
                else {
                    s = newInstance(type, address);
                    s.conditionalAutoRead();
                }
            }
            else {
                s.autoRead();
            }
        }
        return s;
    }

    /** Read the given field and return its value.  The Java field will be
     * updated from the contents of native memory.
     * @param structField field to be read
     * @return value of the requested field
     */
    // TODO: make overridable method with calculated native type, offset, etc
    protected Object readField(StructField structField) {

        // Get the offset of the field
        int offset = structField.offset;

        // Determine the type of the field
        Class<?> fieldType = structField.type;
        FromNativeConverter readConverter = structField.readConverter;
        if (readConverter != null) {
            fieldType = readConverter.nativeType();
        }
        // Get the current value only for types which might need to be preserved
        Object currentValue = (Structure.class.isAssignableFrom(fieldType)
                               || Callback.class.isAssignableFrom(fieldType)
                               || (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(fieldType))
                               || Pointer.class.isAssignableFrom(fieldType)
                               || NativeMapped.class.isAssignableFrom(fieldType)
                               || fieldType.isArray())
            ? getFieldValue(structField.field) : null;

        Object result;
        if (fieldType == String.class) {
            Pointer p = memory.getPointer(offset);
            result = p == null ? null : p.getString(0, encoding);
        }
        else {
            result = memory.getValue(offset, fieldType, currentValue);
        }
        if (readConverter != null) {
            result = readConverter.fromNative(result, structField.context);
            if (currentValue != null && currentValue.equals(result)) {
                result = currentValue;
            }
        }

        if (fieldType.equals(String.class)
            || fieldType.equals(WString.class)) {
            nativeStrings.put(structField.name + ".ptr", memory.getPointer(offset));
            nativeStrings.put(structField.name + ".val", result);
        }

        // Update the value on the Java field
        setFieldValue(structField.field, result, true);
        return result;
    }

    /**
     * Writes the fields of the struct to native memory
     */
    public void write() {
        // Avoid writing to a null pointer
        if (memory == PLACEHOLDER_MEMORY) {
            return;
        }

        // convenience: allocate memory if it hasn't been already; this
        // allows structures to do field-based initialization of arrays and not
        // have to explicitly call allocateMemory in a ctor
        ensureAllocated();

        // Update native FFI type information, if needed
        if (this instanceof ByValue) {
            getTypeInfo();
        }

        // Avoid redundant writes
        if (busy().contains(this)) {
            return;
        }
        busy().add(this);
        try {
            // Write all fields, except those marked 'volatile'
            for (StructField sf : fields().values()) {
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
     * @param name which field to synch
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public void writeField(String name) {
        ensureAllocated();
        StructField f = fields().get(name);
        if (f == null)
            throw new IllegalArgumentException("No such field: " + name);
        writeField(f);
    }

    /** Write the given field value to the field and native memory.   The
     * given value will be written both to the Java field and the
     * corresponding native memory.
     * @param name field to write
     * @param value value to write
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public void writeField(String name, Object value) {
        ensureAllocated();
        StructField structField = fields().get(name);
        if (structField == null)
            throw new IllegalArgumentException("No such field: " + name);
        setFieldValue(structField.field, value);
        writeField(structField);
    }

    /**
     * @param structField internal field representation to synch to native memory
     */
    protected void writeField(StructField structField) {

        if (structField.isReadOnly)
            return;

        // Get the offset of the field
        int offset = structField.offset;

        // Get the value from the field
        Object value = getFieldValue(structField.field);

        // Determine the type of the field
        Class<?> fieldType = structField.type;
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
                // If we've already allocated a native string here, and the
                // string value is unchanged, leave it alone
                if (nativeStrings.containsKey(structField.name + ".ptr")
                    && value.equals(nativeStrings.get(structField.name + ".val"))) {
                    return;
                }
                NativeString nativeString = wide
                    ? new NativeString(value.toString(), true)
                    : new NativeString(value.toString(), encoding);
                // Keep track of allocated C strings to avoid
                // premature garbage collection of the memory.
                nativeStrings.put(structField.name, nativeString);
                value = nativeString.getPointer();
            }
            else {
                nativeStrings.remove(structField.name);
            }
            nativeStrings.remove(structField.name + ".ptr");
            nativeStrings.remove(structField.name + ".val");
        }

        try {
            memory.setValue(offset, value, fieldType);
        }
        catch(IllegalArgumentException e) {
            String msg = "Structure field \"" + structField.name
                + "\" was declared as " + structField.type
                + (structField.type == fieldType
                   ? "" : " (native type " + fieldType + ")")
                + ", which is not supported within a Structure";
            throw new IllegalArgumentException(msg, e);
        }
    }

    /** Used to declare fields order as metadata instead of method.
     * example:
     * <pre><code>
     * // New
     * {@literal @}FieldOrder({ "n", "s" })
     * class Parent extends Structure {
     *     public int n;
     *     public String s;
     * }
     * {@literal @}FieldOrder({ "d", "c" })
     * class Son extends Parent {
     *     public double d;
     *     public char c;
     * }
     * // Old
     * class Parent extends Structure {
     *     public int n;
     *     public String s;
     *     protected List<String> getFieldOrder() {
     *         return Arrays.asList("n", "s");
     *     }
     * }
     * class Son extends Parent {
     *     public double d;
     *     public char c;
     *     protected List<String> getFieldOrder() {
     *         List<String> fields = new LinkedList<String>(super.getFieldOrder());
     *         fields.addAll(Arrays.asList("d", "c"));
     *         return fields;
     *     }
     * }
     * </code></pre>
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface FieldOrder {
        String[] value();
    }

    /** Returns this Structure's field names in their proper order.<br>
     *
     * When defining a new {@link Structure} you shouldn't override this
     * method, but use {@link FieldOrder} annotation to define your field
     * order(this also works with inheritance)<br>
     *
     * If you want to do something non-standard you can override the method
     * and define it as followed
     * <pre><code>
     * protected List<String> getFieldOrder() {
     *     return Arrays.asList(...);
     * }
     * </code></pre>
     * <strong>IMPORTANT</strong>
     * When deriving from an existing Structure subclass, ensure that
     * you augment the list provided by the superclass, e.g.
     * <pre><code>
     * protected List<String> getFieldOrder() {
     *     List<String> fields = new LinkedList<String>(super.getFieldOrder());
     *     fields.addAll(Arrays.asList(...));
     *     return fields;
     * }
     * </code></pre>
     *
     * Field order must be explicitly indicated, since the
     * field order as returned by {@link Class#getFields()} is not
     * guaranteed to be predictable.
     * @return ordered list of field names
     */
    // TODO(idosu 28 Apr 2018): Maybe deprecate this method to let users know they should use @FieldOrder
    protected List<String> getFieldOrder() {
        List<String> fields = new LinkedList<String>();
        for (Class<?> clazz = getClass(); clazz != Structure.class; clazz = clazz.getSuperclass()) {
            FieldOrder order = clazz.getAnnotation(FieldOrder.class);
            if (order != null) {
                fields.addAll(0, Arrays.asList(order.value()));
            }
        }

        // fields.isEmpty() can be true because it is check somewhere else
        return Collections.unmodifiableList(fields);
    }

    /** Sort the structure fields according to the given array of names.
     * @param fields list of fields to be sorted
     * @param names list of names representing the desired sort order
     */
    protected void sortFields(List<Field> fields, List<String> names) {
        for (int i=0;i < names.size();i++) {
            String name = names.get(i);
            for (int f=0;f < fields.size();f++) {
                Field field = fields.get(f);
                if (name.equals(field.getName())) {
                    Collections.swap(fields, i, f);
                    break;
                }
            }
        }
    }

    /** Look up all fields in this class and superclasses.
     * @return ordered list of public {@link java.lang.reflect.Field} available on
     * this {@link Structure} class.
     */
    protected List<Field> getFieldList() {
        List<Field> flist = new ArrayList<Field>();
        for (Class<?> cls = getClass();
             !cls.equals(Structure.class);
             cls = cls.getSuperclass()) {
            List<Field> classFields = new ArrayList<Field>();
            Field[] fields = cls.getDeclaredFields();
            for (int i=0;i < fields.length;i++) {
                int modifiers = fields[i].getModifiers();
                if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                    continue;
                }
                classFields.add(fields[i]);
            }
            flist.addAll(0, classFields);
        }
        return flist;
    }

    /** Cache field order per-class.
     * @return (cached) ordered list of fields
     */
    private List<String> fieldOrder() {
        Class<?> clazz = getClass();
        synchronized(fieldOrder) {
            List<String> list = fieldOrder.get(clazz);
            if (list == null) {
                list = getFieldOrder();
                fieldOrder.put(clazz, list);
            }
            return list;
        }
    }

    public static List<String> createFieldsOrder(List<String> baseFields, String ... extraFields) {
        return createFieldsOrder(baseFields, Arrays.asList(extraFields));
    }

    public static List<String> createFieldsOrder(List<String> baseFields, List<String> extraFields) {
        List<String> fields = new ArrayList<String>(baseFields.size() + extraFields.size());
        fields.addAll(baseFields);
        fields.addAll(extraFields);
        return Collections.unmodifiableList(fields);
    }

    /**
     * @param field The (single) field name
     * @return @return An <U>un-modifiable</U> list containing the field name
     */
    public static List<String> createFieldsOrder(String field) {
        return Collections.unmodifiableList(Collections.singletonList(field));
    }

    /**
     * @param fields The structure field names in correct order
     * @return An <U>un-modifiable</U> list of the fields
     */
    public static List<String> createFieldsOrder(String ... fields) {
        return Collections.unmodifiableList(Arrays.asList(fields));
    }

    private static <T extends Comparable<T>> List<T> sort(Collection<? extends T> c) {
        List<T> list = new ArrayList<T>(c);
        Collections.sort(list);
        return list;
    }

    /** Returns all field names (sorted) provided so far by
        {@link #getFieldOrder}
        @param force set if results are required immediately
        @return null if not yet able to provide fields, and force is false.
        @throws Error if force is true and field order data not yet specified
        and can't be generated automatically.
    **/
    protected List<Field> getFields(boolean force) {
        List<Field> flist = getFieldList();
        Set<String> names = new HashSet<String>();
        for (Field f : flist) {
            names.add(f.getName());
        }

        List<String> fieldOrder = fieldOrder();
        if (fieldOrder.size() != flist.size() && flist.size() > 1) {
            if (force) {
                throw new Error("Structure.getFieldOrder() on " + getClass()
                                + (fieldOrder.size() < flist.size()
                                    ? " does not provide enough"
                                    : " provides too many")
                                + " names [" + fieldOrder.size()
                                + "] ("
                                + sort(fieldOrder)
                                + ") to match declared fields [" + flist.size()
                                + "] ("
                                + sort(names)
                                + ")");
            }
            return null;
        }

        Set<String> orderedNames = new HashSet<String>(fieldOrder);
        if (!orderedNames.equals(names)) {
            throw new Error("Structure.getFieldOrder() on " + getClass()
                            + " returns names ("
                            + sort(fieldOrder)
                            + ") which do not match declared field names ("
                            + sort(names) + ")");
        }

        sortFields(flist, fieldOrder);
        return flist;
    }

    /** Calculate the amount of native memory required for this structure.
     * May return {@link #CALCULATE_SIZE} if the size can not yet be
     * determined (usually due to fields in the derived class not yet
     * being initialized).
     * If the <code>force</code> parameter is <code>true</code> will throw
     * an {@link IllegalStateException} if the size can not be determined.
     * @param force whether to force size calculation
     * @return calculated size, or {@link #CALCULATE_SIZE} if the size can not
     * yet be determined.
     * @throws IllegalStateException an array field is not initialized or the
     * size can not be determined while <code>force</code> is <code>true</code>.
     * @throws IllegalArgumentException when an unsupported field type is
     * encountered
     */
    protected int calculateSize(boolean force) {
        return calculateSize(force, false);
    }

    /** Efficiently calculate the size of the given Structure subclass.
     * @param type Structure subclass to check
     * @return native size of the given Structure subclass
     */
    static int size(Class<? extends Structure> type) {
        return size(type, null);
    }

    /** Efficiently calculate the size of the given Structure subclass.
     * @param type Structure subclass to check
     * @param value optional instance of the given class
     * @return native size of the Structure subclass
     */
    static <T extends Structure> int size(Class<T> type, T value) {
        LayoutInfo info;
        synchronized(layoutInfo) {
            info = layoutInfo.get(type);
        }
        int sz = (info != null && !info.variable) ? info.size : CALCULATE_SIZE;
        if (sz == CALCULATE_SIZE) {
            if (value == null) {
                value = newInstance(type, PLACEHOLDER_MEMORY);
            }
            sz = value.size();
        }
        return sz;
    }

    /**
     * @param force whether to force size calculation.
     * @param avoidFFIType set false in certain situations to avoid recursive
     * type lookup.
     * @return calculated size, or {@link #CALCULATE_SIZE} if there is not yet
     * enough information to perform the size calculation.
     */
    int calculateSize(boolean force, boolean avoidFFIType) {
        int size = CALCULATE_SIZE;
        Class<?> clazz = getClass();
        LayoutInfo info;
        synchronized(layoutInfo) {
            info = layoutInfo.get(clazz);
        }
        if (info == null
            || this.alignType != info.alignType
            || this.typeMapper != info.typeMapper) {
            info = deriveLayout(force, avoidFFIType);
        }
        if (info != null) {
            this.structAlignment = info.alignment;
            this.structFields = info.fields;

            if (!info.variable) {
                synchronized(layoutInfo) {
                    // If we've already cached it, only override layout if
                    // we're using non-default values for alignment and/or
                    // type mapper; this way we don't override the cache
                    // prematurely when processing subclasses that call
                    // setAlignType() or setTypeMapper() in the constructor
                    if (!layoutInfo.containsKey(clazz)
                        || this.alignType != ALIGN_DEFAULT
                        || this.typeMapper != null) {
                        layoutInfo.put(clazz, info);
                    }
                }
            }
            size = info.size;
        }
        return size;
    }

    /** Keep track of structure layout information.  Alignment type, type
        mapper, and explicit field order will affect this information.
    */
    private static class LayoutInfo {
        private int size = CALCULATE_SIZE;
        private int alignment = 1;
        private final Map<String, StructField> fields = Collections.synchronizedMap(new LinkedHashMap<String, StructField>());
        private int alignType = ALIGN_DEFAULT;
        private TypeMapper typeMapper;
        private boolean variable;
        // For unions only, field on which the union FFI type info is based
        private StructField typeInfoField;
    }

    private void validateField(String name, Class<?> type) {
        if (typeMapper != null) {
            ToNativeConverter toNative = typeMapper.getToNativeConverter(type);
            if (toNative != null) {
                validateField(name, toNative.nativeType());
                return;
            }
        }
        if (type.isArray()) {
            validateField(name, type.getComponentType());
        }
        else {
            try {
                getNativeSize(type);
            }
            catch(IllegalArgumentException e) {
                String msg = "Invalid Structure field in " + getClass() + ", field name '" + name + "' (" + type + "): " + e.getMessage();
                throw new IllegalArgumentException(msg, e);
            }
        }
    }

    /** ensure all fields are of valid type. */
    private void validateFields() {
        List<Field> fields = getFieldList();
        for (Field f : fields) {
            validateField(f.getName(), f.getType());
        }
    }

    /** Calculates the size, alignment, and field layout of this structure.
        Also initializes any null-valued Structure or NativeMapped
        members.
     */
    private LayoutInfo deriveLayout(boolean force, boolean avoidFFIType) {
        int calculatedSize = 0;
        List<Field> fields = getFields(force);
        if (fields == null) {
            return null;
        }

        LayoutInfo info = new LayoutInfo();
        info.alignType = this.alignType;
        info.typeMapper = this.typeMapper;

        boolean firstField = true;
        for (Iterator<Field> i=fields.iterator();i.hasNext();firstField=false) {
            Field field = i.next();
            int modifiers = field.getModifiers();

            Class<?> type = field.getType();
            if (type.isArray()) {
                info.variable = true;
            }
            StructField structField = new StructField();
            structField.isVolatile = Modifier.isVolatile(modifiers);
            structField.isReadOnly = Modifier.isFinal(modifiers);
            if (structField.isReadOnly) {
                if (!Platform.RO_FIELDS) {
                    throw new IllegalArgumentException("This VM does not support read-only fields (field '"
                                                       + field.getName() + "' within " + getClass() + ")");
                }
                // In J2SE VMs, this allows overriding the value of final
                // fields
                field.setAccessible(true);
            }
            structField.field = field;
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
            if (!Modifier.isPublic(field.getModifiers())) {
                continue;
            }

            Object value = getFieldValue(structField.field);
            if (value == null && type.isArray()) {
                if (force) {
                    throw new IllegalStateException("Array fields must be initialized");
                }
                // can't calculate size yet, defer until later
                return null;
            }
            Class<?> nativeType = type;
            if (NativeMapped.class.isAssignableFrom(type)) {
                NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
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

            if (value == null) {
                value = initializeField(structField.field, type);
            }

            try {
                structField.size = getNativeSize(nativeType, value);
                fieldAlignment = getNativeAlignment(nativeType, value, firstField);
            }
            catch(IllegalArgumentException e) {
                // Might simply not yet have a type mapper set yet
                if (!force && typeMapper == null) {
                    return null;
                }
                String msg = "Invalid Structure field in " + getClass() + ", field name '" + structField.name + "' (" + structField.type + "): " + e.getMessage();
                throw new IllegalArgumentException(msg, e);
            }

            // Align fields as appropriate
            if (fieldAlignment == 0) {
                throw new Error("Field alignment is zero for field '" + structField.name + "' within " + getClass());
            }
            info.alignment = Math.max(info.alignment, fieldAlignment);
            if ((calculatedSize % fieldAlignment) != 0) {
                calculatedSize += fieldAlignment - (calculatedSize % fieldAlignment);
            }
            if (this instanceof Union) {
                structField.offset = 0;
                calculatedSize = Math.max(calculatedSize, structField.size);
            }
            else {
                structField.offset = calculatedSize;
                calculatedSize += structField.size;
            }

            // Save the field in our list
            info.fields.put(structField.name, structField);

            if (info.typeInfoField == null
                || info.typeInfoField.size < structField.size
                || (info.typeInfoField.size == structField.size
                    && Structure.class.isAssignableFrom(structField.type))) {
                info.typeInfoField = structField;
            }
        }

        if (calculatedSize > 0) {
            int size = addPadding(calculatedSize, info.alignment);
            // Update native FFI type information, if needed
            if (this instanceof ByValue && !avoidFFIType) {
                getTypeInfo();
            }
            info.size = size;
            return info;
        }

        throw new IllegalArgumentException("Structure " + getClass()
                                           + " has unknown or zero size (ensure "
                                           + "all fields are public)");
    }

    /**
     * Initialize any null-valued fields that should have a non-null default
     * value.
     */
    @SuppressWarnings("UseSpecificCatch")
    private void initializeFields() {
        // Get the full field list, don't care about sorting
        List<Field> flist = getFieldList();
        for (Field f : flist) {
            try {
                Object o = f.get(this);
                if (o == null) {
                    initializeField(f, f.getType());
                }
            }
            catch (Exception e) {
                throw new Error("Exception reading field '" + f.getName() + "' in " + getClass(), e);
            }
        }
    }

    private Object initializeField(Field field, Class<?> type) {
        Object value = null;
        if (Structure.class.isAssignableFrom(type)
            && !(ByReference.class.isAssignableFrom(type))) {
            try {
                value = newInstance((Class<? extends Structure>) type, PLACEHOLDER_MEMORY);
                setFieldValue(field, value);
            }
            catch(IllegalArgumentException e) {
                String msg = "Can't determine size of nested structure";
                throw new IllegalArgumentException(msg, e);
            }
        }
        else if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
            value = tc.defaultValue();
            setFieldValue(field, value);
        }
        return value;
    }

    private int addPadding(int calculatedSize) {
        return addPadding(calculatedSize, structAlignment);
    }

    private int addPadding(int calculatedSize, int alignment) {
        // Structure size must be an integral multiple of its alignment,
        // add padding if necessary.
        if (actualAlignType != ALIGN_NONE) {
            if ((calculatedSize % alignment) != 0) {
                calculatedSize += alignment - (calculatedSize % alignment);
            }
        }
        return calculatedSize;
    }

    /**
     * @return current alignment setting for this structure
     */
    protected int getStructAlignment() {
        if (size == CALCULATE_SIZE) {
            // calculate size, but don't allocate memory
            calculateSize(true);
        }
        return structAlignment;
    }

    /** Overridable in subclasses.
     * Calculate the appropriate alignment for a field of a given type within this struct.
     * @param type field type
     * @param value field value, if available
     * @param isFirstElement is this field the first element in the struct?
     * @return the native byte alignment
     */
    // TODO: write getNaturalAlignment(stack/alloc) + getEmbeddedAlignment(structs)
    // TODO: move this into a native call which detects default alignment
    // automatically
    protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
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
        else if ((Pointer.class.isAssignableFrom(type) && !Function.class.isAssignableFrom(type))
                 || (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type))
                 || Callback.class.isAssignableFrom(type)
                 || WString.class == type
                 || String.class == type) {
            alignment = Native.POINTER_SIZE;
        }
        else if (Structure.class.isAssignableFrom(type)) {
            if (ByReference.class.isAssignableFrom(type)) {
                alignment = Native.POINTER_SIZE;
            }
            else {
                if (value == null)
                    value = newInstance((Class<? extends Structure>) type, PLACEHOLDER_MEMORY);
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
        if (actualAlignType == ALIGN_NONE) {
            alignment = 1;
        }
        else if (actualAlignType == ALIGN_MSVC) {
            alignment = Math.min(8, alignment);
        }
        else if (actualAlignType == ALIGN_GNUC) {
            // NOTE this is published ABI for 32-bit gcc/linux/x86, osx/x86,
            // and osx/ppc.  osx/ppc special-cases the first element
            if (!isFirstElement || !(Platform.isMac() && Platform.isPPC())) {
                alignment = Math.min(Native.MAX_ALIGNMENT, alignment);
            }
            if (!isFirstElement && Platform.isAIX() && (type == double.class || type == Double.class)) {
                alignment = 4;
            }
        }
        return alignment;
    }

    /**
     * If <code>jna.dump_memory</code> is true, will include a native memory dump
     * of the Structure's backing memory.
     * @return String representation of this object.
     */
    @Override
    public String toString() {
        return toString(Boolean.getBoolean("jna.dump_memory"));
    }

    /**
     * @param debug If true, will include a native memory dump of the
     * Structure's backing memory.
     * @return String representation of this object.
     */
    public String toString(boolean debug) {
        return toString(0, true, debug);
    }

    private String format(Class<?> type) {
        String s = type.getName();
        int dot = s.lastIndexOf(".");
        return s.substring(dot + 1);
    }

    private String toString(int indent, boolean showContents, boolean dumpMemory) {
        ensureAllocated();
        String LS = System.getProperty("line.separator");
        String name = format(getClass()) + "(" + getPointer() + ")";
        if (!(getPointer() instanceof Memory)) {
            name += " (" + size() + " bytes)";
        }
        String prefix = "";
        for (int idx=0;idx < indent;idx++) {
            prefix += "  ";
        }
        String contents = LS;
        if (!showContents) {
            contents = "...}";
        } else {
            for (Iterator<StructField> i = fields().values().iterator(); i.hasNext();) {
                StructField sf = i.next();
                Object value = getFieldValue(sf.field);
                String type = format(sf.type);
                String index = "";
                contents += prefix;
                if (sf.type.isArray() && value != null) {
                    type = format(sf.type.getComponentType());
                    index = "[" + Array.getLength(value) + "]";
                }
                contents += String.format("  %s %s%s@0x%X", type, sf.name, index, sf.offset);
                if (value instanceof Structure) {
                    value = ((Structure)value).toString(indent + 1, !(value instanceof Structure.ByReference), dumpMemory);
                }
                contents += "=";
                if (value instanceof Long) {
                    contents += String.format("0x%08X", (Long) value);
                }
                else if (value instanceof Integer) {
                    contents += String.format("0x%04X", (Integer) value);
                }
                else if (value instanceof Short) {
                    contents += String.format("0x%02X", (Short) value);
                }
                else if (value instanceof Byte) {
                    contents += String.format("0x%01X", (Byte) value);
                }
                else {
                    contents += String.valueOf(value).trim();
                }
                contents += LS;
                if (!i.hasNext())
                    contents += prefix + "}";
            }
        }
        if (indent == 0 && dumpMemory) {
            final int BYTES_PER_ROW = 4;
            contents += LS + "memory dump" + LS;
            byte[] buf = getPointer().getByteArray(0, size());
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
        return name + " {" + contents;
    }

    /** Returns a view of this structure's memory as an array of structures.
     * Note that this <code>Structure</code> must have a public, no-arg
     * constructor.  If the structure is currently using auto-allocated
     * {@link Memory} backing, the memory will be resized to fit the entire
     * array.
     * @param array Structure[] object to populate
     * @return array of Structure mapped onto the available memory
     */
    public Structure[] toArray(Structure[] array) {
        ensureAllocated();
        if (this.memory instanceof AutoAllocated) {
            // reallocate if necessary
            Memory m = (Memory)this.memory;
            int requiredSize = array.length * size();
            if (m.size() < requiredSize) {
                useMemory(autoAllocate(requiredSize));
            }
        }
        // TODO: optimize - check whether array already exists
        array[0] = this;
        int size = size();
        for (int i=1;i < array.length;i++) {
            array[i] = newInstance(getClass(), memory.share(i*size, size));
            array[i].conditionalAutoRead();
        }

        if (!(this instanceof ByValue)) {
            // keep track for later auto-read/writes
            this.array = array;
        }

        return array;
    }

    /** Returns a view of this structure's memory as an array of structures.
     * Note that this <code>Structure</code> must have a public, no-arg
     * constructor.  If the structure is currently using auto-allocated
     * {@link Memory} backing, the memory will be resized to fit the entire
     * array.
     * @param size desired number of elements
     * @return array of Structure (individual elements will be of the
     * appropriate type, as will the Structure[]).
     */
    public Structure[] toArray(int size) {
        return toArray((Structure[])Array.newInstance(getClass(), size));
    }

    private Class<?> baseClass() {
        if ((this instanceof Structure.ByReference
             || this instanceof Structure.ByValue)
            && Structure.class.isAssignableFrom(getClass().getSuperclass())) {
            return getClass().getSuperclass();
        }
        return getClass();
    }

    /** Return whether the given Structure's native backing data is identical to
     * this one.
     * @param s Structure to compare
     * @return equality result
     */
    public boolean dataEquals(Structure s) {
        return dataEquals(s, false);
    }

    /** Return whether the given Structure's backing data is identical to
     * this one, optionally clearing and re-writing native memory before checking.
     * @param s Structure to compare
     * @param clear whether to clear native memory
     * @return equality result
     */
    public boolean dataEquals(Structure s, boolean clear) {
        if (clear) {
            s.getPointer().clear(s.size());
            s.write();
            getPointer().clear(size());
            write();
        }
        byte[] data = s.getPointer().getByteArray(0, s.size());
        byte[] ref = getPointer().getByteArray(0, size());
        if (data.length == ref.length) {
            for (int i=0;i < data.length;i++) {
                if (data[i] != ref[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @return whether the given structure's type and pointer match.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Structure
            && o.getClass() == getClass()
            && ((Structure)o).getPointer().equals(getPointer());
    }

    /**
     * @return hash code for this structure's pointer.
     */
    @Override
    public int hashCode() {
        Pointer p = getPointer();
        if (p != null) {
            return getPointer().hashCode();
        }
        return getClass().hashCode();
    }

    /** Cache native type information for use in native code.
     * @param p Native pointer to the type information
     */
    protected void cacheTypeInfo(Pointer p) {
        this.typeInfo = p.peer;
    }

    /** Override to supply native type information for the given field.
     * @param f internal field representation
     * @return Native pointer to the corresponding type information
     */
    Pointer getFieldTypeInfo(StructField f) {
        Class<?> type = f.type;
        Object value = getFieldValue(f.field);
        if (typeMapper != null) {
            ToNativeConverter nc = typeMapper.getToNativeConverter(type);
            if (nc != null) {
                type = nc.nativeType();
                value = nc.toNative(value, new ToNativeContext());
            }
        }
        return FFIType.get(value, type);
    }

    /**
     * @return native type information for this structure.
     */
    Pointer getTypeInfo() {
        Pointer p = getTypeInfo(this);
        cacheTypeInfo(p);
        return p;
    }

    /** Set whether the structure is automatically synchronized to native memory
        before and after a native function call.  Convenience method for
        <pre><code>
        boolean auto = ...;
        setAutoRead(auto);
        setAutoWrite(auto);
        </code></pre>
        For extremely large or complex structures where you only need to
        access a small number of fields, you may see a significant performance
        benefit by avoiding automatic structure reads and writes.  If
        auto-read and -write are disabled, it is up to you to ensure that the
        Java fields of interest are synched before and after native function
        calls via {@link #readField(String)} and {@link
        #writeField(String,Object)}.
        This is typically most effective when a native call populates a large
        structure and you only need a few fields out of it.  After the native
        call you can call {@link #readField(String)} on only the fields of
        interest.
        @param auto whether to automatically synch with native memory.
    */
    public void setAutoSynch(boolean auto) {
        setAutoRead(auto);
        setAutoWrite(auto);
    }

    /** Set whether the structure is read from native memory after
     * a native function call.
     * @param auto whether to automatically synch from native memory.
     */
    public void setAutoRead(boolean auto) {
        this.autoRead = auto;
    }

    /** Returns whether the structure is read from native memory after
     * a native function call.
     * @return whether automatic synch from native memory is enabled.
     */
    public boolean getAutoRead() {
        return this.autoRead;
    }

    /** Set whether the structure is written to native memory prior to a native
     * function call.
     * @param auto whether to automatically synch to native memory.
     */
    public void setAutoWrite(boolean auto) {
        this.autoWrite = auto;
    }

    /** Returns whether the structure is written to native memory prior to a native
     * function call.
     * @return whether automatic synch to native memory is enabled.
     */
    public boolean getAutoWrite() {
        return this.autoWrite;
    }

    /** Exposed for testing purposes only.
     * @param obj object to query
     * @return native pointer to type information
     */
    static Pointer getTypeInfo(Object obj) {
        return FFIType.get(obj);
    }

    /** Called from native code only; same as {@link
     * #newInstance(Class,Pointer)}, except that it additionally calls
     * {@link #conditionalAutoRead()}.
     */
    private static <T extends Structure> T newInstance(Class<T> type, long init) {
        try {
            T s = newInstance(type, init == 0 ? PLACEHOLDER_MEMORY : new Pointer(init));
            if (init != 0) {
                s.conditionalAutoRead();
            }
            return s;
        }
        catch(Throwable e) {
            LOG.log(Level.WARNING, "JNA: Error creating structure", e);
            return null;
        }
    }

    /** Create a new Structure instance of the given type, initialized with
     * the given memory.
     * @param type desired Structure type
     * @param init initial memory
     * @return the new instance
     * @throws IllegalArgumentException if the instantiation fails
     */
    public static <T extends Structure> T newInstance(Class<T> type, Pointer init) throws IllegalArgumentException {
        try {
            Constructor<T> ctor = type.getConstructor(Pointer.class);
            return ctor.newInstance(init);
        }
        catch(NoSuchMethodException e) {
            // Not defined, fall back to the default
        }
        catch(SecurityException e) {
            // Might as well try the fallback
        }
        catch(InstantiationException e) {
            String msg = "Can't instantiate " + type;
            throw new IllegalArgumentException(msg, e);
        }
        catch(IllegalAccessException e) {
            String msg = "Instantiation of " + type + " (Pointer) not allowed, is it public?";
            throw new IllegalArgumentException(msg, e);
        }
        catch(InvocationTargetException e) {
            String msg = "Exception thrown while instantiating an instance of " + type;
            throw new IllegalArgumentException(msg, e);
        }
        T s = newInstance(type);
        if (init != PLACEHOLDER_MEMORY) {
            s.useMemory(init);
        }
        return s;
    }

    /**
     * Create a new Structure instance of the given type
     * @param type desired Structure type
     * @return the new instance
     * @throws IllegalArgumentException if the instantiation fails
     */
    public static <T extends Structure> T newInstance(Class<T> type) throws IllegalArgumentException {
        T s = Klass.newInstance(type);
        if (s instanceof ByValue) {
            s.allocateMemory();
        }
        return s;
    }

    /** Keep track of the largest aggregate field of the union to use for
     * FFI type information.
     * @return which field to use to obtain FFI type information
     */
    StructField typeInfoField() {
        LayoutInfo info;
        synchronized(layoutInfo) {
            info = layoutInfo.get(getClass());
        }
        if (info != null) {
            return info.typeInfoField;
        }
        return null;
    }

    protected static class StructField extends Object {
        public String name;
        public Class<?> type;
        public Field field;
        public int size = -1;
        public int offset = -1;
        public boolean isVolatile;
        public boolean isReadOnly;
        public FromNativeConverter readConverter;
        public ToNativeConverter writeConverter;
        public FromNativeContext context;
        @Override
        public String toString() {
            return name + "@" + offset + "[" + size + "] (" + type + ")";
        }
    }
    /** This class auto-generates an ffi_type structure appropriate for a given
     * structure for use by libffi.  The lifecycle of this structure is easier
     * to manage on the Java side than in native code.
     */
    @FieldOrder({ "size", "alignment", "type", "elements" })
    static class FFIType extends Structure {
        public static class size_t extends IntegerType {
            private static final long serialVersionUID = 1L;

            public size_t() { this(0); }
            public size_t(long value) { super(Native.SIZE_T_SIZE, value); }
        }

        private static final Map<Object, Object> typeInfoMap = new WeakHashMap<Object, Object>();

        // Native.initIDs initializes these fields to their appropriate
        // pointer values.  These are in a separate class from FFIType so that
        // they may be initialized prior to loading the FFIType class
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
            typeInfoMap.put(Pointer.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(String.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(WString.class, FFITypes.ffi_type_pointer);
            typeInfoMap.put(boolean.class, FFITypes.ffi_type_uint32);
            typeInfoMap.put(Boolean.class, FFITypes.ffi_type_uint32);
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
            ref.ensureAllocated(true);

            if (ref instanceof Union) {
                StructField sf = ((Union)ref).typeInfoField();
                els = new Pointer[] {
                    get(ref.getFieldValue(sf.field), sf.type),
                    null,
                };
            }
            else {
                els = new Pointer[ref.fields().size() + 1];
                int idx = 0;
                for (StructField sf : ref.fields().values()) {
                    els[idx++] = ref.getFieldTypeInfo(sf);
                }
            }
            init(els);
        }
        // Represent fixed-size arrays as structures of N identical elements
        private FFIType(Object array, Class<?> type) {
            int length = Array.getLength(array);
            Pointer[] els = new Pointer[length+1];
            Pointer p = get(null, type.getComponentType());
            for (int i=0;i < length;i++) {
                els[i] = p;
            }
            init(els);
        }

        private void init(Pointer[] els) {
            elements = new Memory(Native.POINTER_SIZE * els.length);
            elements.write(0, els, 0, els.length);
            write();
        }

        /** Obtain a pointer to the native FFI type descriptor for the given object. */
        static Pointer get(Object obj) {
            if (obj == null)
                return FFITypes.ffi_type_pointer;
            if (obj instanceof Class)
                return get(null, (Class<?>)obj);
            return get(obj, obj.getClass());
        }

        private static Pointer get(Object obj, Class<?> cls) {
            TypeMapper mapper = Native.getTypeMapper(cls);
            if (mapper != null) {
                ToNativeConverter nc = mapper.getToNativeConverter(cls);
                if (nc != null) {
                    cls = nc.nativeType();
                }
            }
            synchronized(typeInfoMap) {
                Object o = typeInfoMap.get(cls);
                if (o instanceof Pointer) {
                    return (Pointer)o;
                }
                if (o instanceof FFIType) {
                    return ((FFIType)o).getPointer();
                }
                if ((Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(cls))
                    || Callback.class.isAssignableFrom(cls)) {
                    typeInfoMap.put(cls, FFITypes.ffi_type_pointer);
                    return FFITypes.ffi_type_pointer;
                }
                if (Structure.class.isAssignableFrom(cls)) {
                    if (obj == null) obj = newInstance((Class<? extends Structure>) cls, PLACEHOLDER_MEMORY);
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

    private static class AutoAllocated extends Memory {
        public AutoAllocated(int size) {
            super(size);
            // Always clear new structure memory
            super.clear();
        }
        @Override
        public String toString() {
            return "auto-" + super.toString();
        }
    }

    private static void structureArrayCheck(Structure[] ss) {
        if (Structure.ByReference[].class.isAssignableFrom(ss.getClass())) {
            return;
        }
        Pointer base = ss[0].getPointer();
        int size = ss[0].size();
        for (int si=1;si < ss.length;si++) {
            if (ss[si].getPointer().peer != base.peer + size*si) {
                String msg = "Structure array elements must use"
                    + " contiguous memory (bad backing address at Structure array index " + si + ")";
                throw new IllegalArgumentException(msg);
            }
        }
    }

    public static void autoRead(Structure[] ss) {
        structureArrayCheck(ss);
        if (ss[0].array == ss) {
            ss[0].autoRead();
        }
        else {
            for (int si=0;si < ss.length;si++) {
                if (ss[si] != null) {
                    ss[si].autoRead();
                }
            }
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

    public static void autoWrite(Structure[] ss) {
        structureArrayCheck(ss);
        if (ss[0].array == ss) {
            ss[0].autoWrite();
        }
        else {
            for (int si=0;si < ss.length;si++) {
                if (ss[si] != null) {
                    ss[si].autoWrite();
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

    /** Return the native size of the given Java type, from the perspective of
     * this Structure.
     * @param nativeType field type to examine
     * @return native size (in bytes) of the requested field type
     */
    protected int getNativeSize(Class<?> nativeType) {
        return getNativeSize(nativeType, null);
    }

    /** Return the native size of the given Java type, from the perspective of
     * this Structure.
     * @param nativeType field type to examine
     * @param value instance of the field type
     * @return native size (in bytes) of the requested field type
     */
    protected int getNativeSize(Class<?> nativeType, Object value) {
        return Native.getNativeSize(nativeType, value);
    }

    /** Placeholder pointer to help avoid auto-allocation of memory where a
     * Structure needs a valid pointer but want to avoid actually reading from it.
     */
    private static final Pointer PLACEHOLDER_MEMORY = new Pointer(0) {
        @Override
        public Pointer share(long offset, long sz) { return this; }
    };

    /** Indicate whether the given Structure class can be created by JNA.
     * @param cls Structure subclass to check
     */
    static void validate(Class<? extends Structure> cls) {
        try {
            cls.getConstructor();
            return;
        }catch(NoSuchMethodException e) {
        }
        catch(SecurityException e) {
        }
        throw new IllegalArgumentException("No suitable constructor found for class: " + cls.getName());
    }
}
