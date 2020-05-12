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

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class NativeLongByReference extends ByReference {
    public NativeLongByReference() {
        this(new NativeLong(0));
    }

    public NativeLongByReference(NativeLong value) {
        super(NativeLong.SIZE);
        setValue(value);
    }

    public void setValue(NativeLong value) {
        getPointer().setNativeLong(0, value);
    }

    public NativeLong getValue() {
        return getPointer().getNativeLong(0);
    }

    @Override
    public String toString() {
        // Can't mix types with ternary operator
        if (NativeLong.SIZE > 4) {
            return String.format("NativeLong@0x1$%x=0x%2$x (%2$d)", Pointer.nativeValue(getPointer()),
                    getValue().longValue());
        } else {
            return String.format("NativeLong@0x1$%x=0x%2$x (%2$d)", Pointer.nativeValue(getPointer()),
                    getValue().intValue());
        }
    }
}
