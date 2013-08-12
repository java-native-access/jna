/* Copyright (c) 2007-2012 Timothy Wall, All Rights Reserved
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    /** Unions do not need a field order, so automatically provide a value to
     * satisfy checking in the Structure superclass.
     */
    protected List getFieldOrder() {
        List flist = getFieldList();
        ArrayList list = new ArrayList();
        for (Iterator i=flist.iterator();i.hasNext();) {
            Field f = (Field)i.next();
            list.add(f.getName());
        }
        return list;
    }

    /** Indicates by type which field will be used to write to native memory.
     * If there are multiple fields of the same type, use {@link
     * #setType(String)} instead with the field name.
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

    /**
     * Indicates which field will be used to write to native memory.
     * @throws IllegalArgumentException if the name does not correspond to
     * any declared union field.
     */
    public void setType(String fieldName) {
        ensureAllocated();
        StructField f = (StructField) fields().get(fieldName);
        if (f != null) {
            activeField = f;
        }
        else {
            throw new IllegalArgumentException("No field named " + fieldName
                                               + " in " + this);
        }
    }

    /** Force a read of the given field from native memory.
     * @return the new field value, after updating
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public Object readField(String fieldName) {
        ensureAllocated();
        setType(fieldName);
        return super.readField(fieldName);
    }

    /** Write the given field value to native memory.
     * The given field will become the active one.
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public void writeField(String fieldName) {
        ensureAllocated();
        setType(fieldName);
        super.writeField(fieldName);
    }

    /** Write the given field value to the field and native memory.
     * The given field will become the active one.
     * @throws IllegalArgumentException if no field exists with the given name
     */
    public void writeField(String fieldName, Object value) {
        ensureAllocated();
        setType(fieldName);
        super.writeField(fieldName, value);
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
                return getFieldValue(activeField.field);
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
        StructField f = findField(object.getClass());
        if (f != null) {
            activeField = f;
            setFieldValue(f.field, object);
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
        ensureAllocated();
        for (Iterator i=fields().values().iterator();i.hasNext();) {
            StructField f = (StructField)i.next();
            if (f.type.isAssignableFrom(type)) {
                return f;
            }
        }
        return null;
    }

    /** Only the currently selected field will be written. */
    protected void writeField(StructField field) {
        if (field == activeField) {
            super.writeField(field);
        }
    }

    /** Avoid reading pointer-based fields and structures unless explicitly
     * selected.  Structures may contain pointer-based fields which can
     * crash the VM if not properly initialized.
     */
    protected Object readField(StructField field) {
        if (field == activeField
            || (!Structure.class.isAssignableFrom(field.type)
                && !String.class.isAssignableFrom(field.type)
                && !WString.class.isAssignableFrom(field.type))) {
            return super.readField(field);
        }
        // Field not accessible
        // TODO: read by-value structures, to the extent possible; need a
        // "read cautiously" method to "read" to indicate we want to avoid
        // pointer-based fields 
        return null;
    }

    /** All fields are considered the "first" element. */
    protected int getNativeAlignment(Class type, Object value, boolean isFirstElement) {
        return super.getNativeAlignment(type, value, true);
    }
}
