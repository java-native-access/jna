/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna;

import java.util.Iterator;

/** Represents a native union.  When writing to native memory, the field
 * corresponding to the type passed to {@link #setType} will be written
 * to native memory.  Upon reading from native memory, Structure, String,
 * or WString fields will <em>not</em> be initialized unless they are 
 * the current field as identified by a call to {@link #setType}.  The current
 * field is always unset by default to avoid accidentally attempting to read
 * a field that is not valid.  In the case of a String, for instance, an 
 * invalid pointer may result in a memory fault when attempting to initialize
 * the String. 
 */
public abstract class Union extends Structure {
    private StructField activeField;
    StructField biggestField;
    /** Create a Union whose size and alignment will be calculated 
     * automatically.
     */
    protected Union() { }
    /** Create a Union of the given size, using default alignment. */
    protected Union(Pointer p) {
        super(p);
    }
    /** Create a Union of the given size and alignment type. */
    protected Union(Pointer p, int alignType) {
        super(p, alignType);
    }
    /** Create a Union of the given size and alignment type. */
    protected Union(TypeMapper mapper) {
        super(mapper);
    }
    /** Create a Union of the given size and alignment type. */
    protected Union(Pointer p, int alignType, TypeMapper mapper) {
        super(p, alignType, mapper);
    }
    /** Indicates which field will be used to write to native memory. 
     * @throws IllegalArgumentException if the type does not correspond to 
     * any declared union field.
     */
    public void setType(Class type) {
        ensureAllocated();
        for (Iterator i=fields().values().iterator();i.hasNext();) {
            StructField f = (StructField)i.next();
            if (f.type == type) {
                activeField = f;
                return;
            }
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }
    
    /** Force a read of the given field from native memory.
     * @return the new field value, after updating
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public Object readField(String name) {
        ensureAllocated();
        StructField f = (StructField)fields().get(name);
        if (f != null) {
            setType(f.type);
        }
        return super.readField(name);
    }

    /** Write the given field value to native memory.
     * The given field will become the active one.
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public void writeField(String name) {
        ensureAllocated();
        StructField f = (StructField)fields().get(name);
        if (f != null) {
            setType(f.type);
        }
        super.writeField(name);
    }

    /** Write the given field value to the field and native memory.
     * The given field will become the active one.
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public void writeField(String name, Object value) {
        ensureAllocated();
        StructField f = (StructField)fields().get(name);
        if (f != null) {
            setType(f.type);
        }
        super.writeField(name, value);
    }

    /** Reads the Structure field of the given type from memory, sets it as
     * the active type and returns it.  Convenience method for
     * <pre><code>
     * Union u;
     * Class type;
     * u.setType(type);
     * u.read();
     * value = u.<i>field</i>;
     * </code></pre>
     * @param type class type of the Structure field to read
     * @return the Structure field with the given type
     */
    public Object getTypedValue(Class type) {
        ensureAllocated();
        for (Iterator i=fields().values().iterator();i.hasNext();) {
            StructField f = (StructField)i.next();
            if (f.type == type) {
                activeField = f;
                read();
                return getField(activeField);
            }
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }

    /** Set the active type and its value.  Convenience method for
     * <pre><code>
     * Union u;
     * Class type;
     * u.setType(type);
     * u.<i>field</i> = value;
     * </code></pre>
     * @param object instance of a class which is part of the union
     * @return this Union object
     */
    public Object setTypedValue(Object object) {
        ensureAllocated();
        StructField f = findField(object.getClass());
        if (f != null) {
            activeField = f;
            setField(f, object);
            return this;
        }
        throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
    }

    /** Returns the field in this union with the same type as <code>type</code>,
     * if any, null otherwise.
     * @param type type to search for
     * @return StructField of matching type
     */
    private StructField findField(Class type) {
        for (Iterator i=fields().values().iterator();i.hasNext();) {
            StructField f = (StructField)i.next();
            if (f.type.isAssignableFrom(type)) {
                return f;
            }
        }
        return null;
    }

    /** Only the currently selected field will be written. */
    void writeField(StructField field) {
        if (field == activeField) {
            super.writeField(field);
        }
    }

    /** Avoid reading pointer-based fields and structures unless explicitly
     * selected.  Structures may contain pointer-based fields which can 
     * crash the VM if not properly initialized.
     */
    Object readField(StructField field) {
        if (field == activeField 
            || (!Structure.class.isAssignableFrom(field.type)
                && !String.class.isAssignableFrom(field.type)
                && !WString.class.isAssignableFrom(field.type))) {
            return super.readField(field);
        }
        // Field not accessible
        // TODO: read structure, to the extent possible; need a "recursive"
        // flag to "read"
        return null;
    }
    
    /** Adjust the size to be the size of the largest element, and ensure
     * all fields begin at offset zero. 
     */
    int calculateSize(boolean force) {
        int size = super.calculateSize(force);
        if (size != CALCULATE_SIZE) {
            int fsize = 0;
            for (Iterator i=fields().values().iterator();i.hasNext();) {
                StructField f = (StructField)i.next();
                f.offset = 0;
                if (f.size > fsize
                    // Prefer aggregate types to simple types, since they
                    // will have more complex packing rules (some platforms
                    // have specific methods for packing small structs into
                    // registers, which may not match the packing of bytes
                    // for a primitive type).
                    || (f.size == fsize
                        && Structure.class.isAssignableFrom(f.type))) {
                    fsize = f.size;
                    biggestField = f;
                }
            }
            size = calculateAlignedSize(fsize);
            if (size > 0) {
                // Update native FFI type information, if needed
                if (this instanceof ByValue) {
                    getTypeInfo();
                }
            }
        }
        return size;
    }
    /** All fields are considered the "first" element. */
    protected int getNativeAlignment(Class type, Object value, boolean isFirstElement) {
        return super.getNativeAlignment(type, value, true);
    }

    /** Avoid calculating type information until we know our biggest field.
     * Return type information for the largest field to ensure all available
     * bits are used.
     */
    Pointer getTypeInfo() {
        if (biggestField == null) {
            // Not calculated yet
            return null;
        }
        return super.getTypeInfo();
    }
}
