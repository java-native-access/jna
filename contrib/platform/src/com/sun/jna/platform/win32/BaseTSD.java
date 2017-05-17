/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

/**
* Based on basetsd.h (various types)
* @author dblock[at]dblock[dot]org
*/
@SuppressWarnings("serial")
public interface BaseTSD {

    /**
     * Signed long type for pointer precision.
     * Use when casting a pointer to a long to perform pointer arithmetic.
     */
    public static class LONG_PTR extends IntegerType {
        public LONG_PTR() {
            this(0);
        }

        public LONG_PTR(long value) {
            super(Native.POINTER_SIZE, value);
        }

        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /**
* Signed SIZE_T.
*/
    public static class SSIZE_T extends LONG_PTR {
        public SSIZE_T() {
            this(0);
        }

        public SSIZE_T(long value) {
            super(value);
        }
    }

    /**
* Unsigned LONG_PTR.
*/
    public static class ULONG_PTR extends IntegerType {
        public ULONG_PTR() {
            this(0);
        }

        public ULONG_PTR(long value) {
            super(Native.POINTER_SIZE, value, true);
        }

        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /**
* PULONG_PTR
*/
    public static class ULONG_PTRByReference extends ByReference {
        public ULONG_PTRByReference() {
            this(new ULONG_PTR(0));
        }
        public ULONG_PTRByReference(ULONG_PTR value) {
            super(Native.POINTER_SIZE);
            setValue(value);
        }
        public void setValue(ULONG_PTR value) {
            if (Native.POINTER_SIZE == 4) {
                getPointer().setInt(0, value.intValue());
            }
            else {
                getPointer().setLong(0, value.longValue());
            }
        }
        public ULONG_PTR getValue() {
            return new ULONG_PTR(Native.POINTER_SIZE == 4
                                 ? getPointer().getInt(0)
                                 : getPointer().getLong(0));
        }
    }


    /**
* Unsigned DWORD_PTR.
*/
    public static class DWORD_PTR extends IntegerType {
        public DWORD_PTR() {
            this(0);
        }

        public DWORD_PTR(long value) {
            super(Native.POINTER_SIZE, value);
        }
    }

    /**
* The maximum number of bytes to which a pointer can point.
* Use for a count that must span the full range of a pointer.
*/
    public static class SIZE_T extends ULONG_PTR {
        public SIZE_T() {
            this(0);
        }

        public SIZE_T(long value) {
            super(value);
        }
    }
}
