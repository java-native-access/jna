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
package com.sun.jna.ptr;

import java.lang.reflect.Method;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/**
 * Provides generic "pointer to type" functionality, often used in C code to
 * return values to the caller in addition to a function result.
 * <p>
 * Derived classes must define <code>setValue(&lt;T&gt;)</code> and
 * <code>&lt;T&gt; getValue()</code> methods which write to/read from the
 * allocated memory.
 * <p>
 * This class derives from PointerType instead of Memory in order to restrict
 * the API to only <code>getValue/setValue</code>.
 * <p>
 * NOTE: this class would ideally be replaced by a generic.
 */
public abstract class ByReference extends PointerType {

    /**
     * Allocates memory at this pointer, to contain the pointed-to value.
     *
     * @param dataSize
     *            The number of bytes to allocate. Must match the byte size of
     *            <code>T</code> in the derived class
     *            <code>setValue(&lt;T&gt;)</code> and
     *            <code>&lt;T&gt; getValue()</code> methods.
     */
    protected ByReference(int dataSize) {
        setPointer(new Memory(dataSize));
    }

    @Override
    public String toString() {
        try {
            Method getValue = getClass().getMethod("getValue");
            Object value = getValue.invoke(this);
            if (value == null) {
                return String.format("null@0x%x", Pointer.nativeValue(getPointer()));
            }
            return String.format("%s@0x%x=%s", value.getClass().getSimpleName(), Pointer.nativeValue(getPointer()),
                    value);
        } catch (Exception ex) {
            return String.format("ByReference Contract violated - %s#getValue raised exception: %s",
                    getClass().getName(), ex.getMessage());
        }
    }
}
