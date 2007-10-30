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

/** Type representing a type-safe native pointer.
 * Derived classes may override the {@link NativeMapped#fromNative} method,
 * which should instantiate a new object (or look up an existing one)
 * of the appropriate type. 
 */
public abstract class PointerType implements NativeMapped {
    private Pointer pointer;
   
    /** The default constructor wraps a NULL pointer. */
    protected PointerType() {
        this.pointer = Pointer.NULL;
    }
    
    /** This constructor is typically used by {@link #fromNative} if generating
     * a new object instance. 
     */
    protected PointerType(Pointer p) {
        this.pointer = p;
    }

    /** All <code>PointerType</code> classes represent a native {@link Pointer}. 
     */
    public Class nativeType() {
        return Pointer.class;
    }

    /** Convert this object to its native type (a {@link Pointer}). */
    public Object toNative() {
        return getPointer();
    }

    /** Returns the associated native {@link Pointer}. */
    public Pointer getPointer() {
        return pointer;
    }
    
    public void setPointer(Pointer p) {
        this.pointer = p;
    }
    
    /** The default implementation simply creates a new instance of the class
     * and assigns its pointer field.  Override if you need different behavior,
     * such as ensuring a single {@link PointerType} instance for each unique
     * {@link Pointer} value, or instantiating a different {@link PointerType}
     * subclass.
     */
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        // Always pass along null pointer values
        if (nativeValue == null) {
            return null;
        }
        try {
            PointerType pt = (PointerType)getClass().newInstance();
            pt.pointer = (Pointer)nativeValue;
            return pt;
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Can't instantiate " + getClass());
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Not allowed to instantiate " + getClass());
        }
    }

    /** The hash code for a <code>PointerType</code> is the same as that for
     * its pointer.
     */
    public int hashCode() {
        return pointer != null ? pointer.hashCode() : 0;
    }
    
    /** Instances of <code>PointerType</code> with identical pointers compare
     * equal by default.
     */
    public boolean equals(Object o) {
        if (o instanceof PointerType) {
            Pointer p = ((PointerType)o).getPointer();
            if (pointer == null)
                return p == null;
            return pointer.equals(p);
        }
        return false;
    }
}
