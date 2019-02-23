/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
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

import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class DirectByReferenceArgumentsTest extends ByReferenceArgumentsTest {

    public static class DirectTestLibrary implements TestLibrary {

        public native void incrementInt8ByReference(ByteByReference b);
        public native void incrementInt16ByReference(ShortByReference s);
        public native void incrementInt32ByReference(IntByReference i);
        public native void incrementNativeLongByReference(NativeLongByReference i);
        public native void incrementInt64ByReference(LongByReference l);
        public native void complementFloatByReference(FloatByReference f);
        public native void complementDoubleByReference(DoubleByReference d);
        public native void setPointerByReferenceNull(PointerByReference p);

        static {
            Native.register("testlib");
        }
    }

    protected void setUp() {
        lib = new DirectTestLibrary();
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectByReferenceArgumentsTest.class);
    }

}
