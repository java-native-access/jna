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

import com.sun.jna.Pointer;

public class FloatByReference extends ByReference {
    public FloatByReference() {
        this(0f);
    }

    public FloatByReference(float value) {
        super(4);
        setValue(value);
    }

    public void setValue(float value) {
        getPointer().setFloat(0, value);
    }

    public float getValue() {
        return getPointer().getFloat(0);
    }

    @Override
    public String toString() {
        return String.format("float@0x%x=%s", Pointer.nativeValue(getPointer()), getValue());
    }
}
