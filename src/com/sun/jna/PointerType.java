/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.lang.reflect.InvocationTargetException;

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

    /* All <code>PointerType</code> classes represent a native {@link Pointer}. */
    @Override
    public Class<?> nativeType() {
        return Pointer.class;
    }

    /** Convert this object to its native type (a {@link Pointer}). */
    @Override
    public Object toNative() {
        return getPointer();
    }

    /** Returns the associated native {@link Pointer}.
        @return Native pointer representation for this object.
     */
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
    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        // Always pass along null pointer values
        if (nativeValue == null) {
            return null;
        }
        PointerType pt = Klass.newInstance(getClass());
        pt.pointer = (Pointer)nativeValue;
        return pt;
    }

    /** The hash code for a <code>PointerType</code> is the same as that for
     * its pointer.
     */
    @Override
    public int hashCode() {
        return pointer != null ? pointer.hashCode() : 0;
    }

    /** Instances of <code>PointerType</code> with identical pointers compare
     * equal by default.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PointerType) {
            Pointer p = ((PointerType)o).getPointer();
            if (pointer == null) {
                return p == null;
            }
            return pointer.equals(p);
        }
        return false;
    }

    @Override
    public String toString() {
        return pointer == null ? "NULL" : pointer.toString() + " (" + super.toString() + ")";
    }
}
