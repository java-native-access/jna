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
    private StructField writeField;
    /** Create a Union whose size and alignment will be calculated 
     * automatically.
     */
    protected Union() { }
    /** Create a Union of the given size, using default alignment. */
    protected Union(int size) {
        super(size);
    }
    /** Create a Union of the given size and alignment type. */
    protected Union(int size, int alignType) {
        super(size, alignType);
    }
    /** Indicates which field will be used to write to native memory. 
     * @throws IllegalArgumentException if the type does not correspond to 
     * any declared union field.
     */
    public void setType(Class type) {
        for (Iterator i=fields().values().iterator();i.hasNext();) {
            StructField f = (StructField)i.next();
            if (f.type == type) {
                writeField = f;
                return;
            }
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }
    /** Only the currently selected field will be written. */
    void writeField(StructField field) {
        if (field == writeField) {
            super.writeField(field);
        }
    }

    /** Avoid reading pointer-based fields and structures unless explicitly
     * selected.  Structures may contain pointer-based fields which can 
     * crash the VM if not properly initialized.
     */
    void readField(StructField field) {
        if (field == writeField 
            || (!Structure.class.isAssignableFrom(field.type)
                && !String.class.isAssignableFrom(field.type)
                && !WString.class.isAssignableFrom(field.type))) {
            super.readField(field);
        }
    }
    
    /** Adjust the size to be the size of the largest element, and ensure
     * all fields begin at offset zero. 
     */
    int calculateSize() {
        int size = super.calculateSize();
        if (size != CALCULATE_SIZE) {
            int fsize = 0;
            for (Iterator i=fields().values().iterator();i.hasNext();) {
                StructField f = (StructField)i.next();
                f.offset = 0;
                fsize = Math.max(fsize, f.size);
            }
            size = calculateAlignedSize(fsize);
        }
        return size;
    }
    /** All fields are considered the "first" element. */
    protected int getNativeAlignment(Class type, Object value, boolean firstElement) {
        return super.getNativeAlignment(type, value, true);
    }
}
