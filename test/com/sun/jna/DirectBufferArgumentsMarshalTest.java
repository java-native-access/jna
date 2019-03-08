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

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class DirectBufferArgumentsMarshalTest extends BufferArgumentsMarshalTest {

    public static class DirectTestLibrary implements TestLibrary {

        // ByteBuffer alternative definitions
        @Override
        public native int fillInt8Buffer(ByteBuffer buf, int len, byte value);
        @Override
        public native int fillInt16Buffer(ByteBuffer buf, int len, short value);
        @Override
        public native int fillInt32Buffer(ByteBuffer buf, int len, int value);
        @Override
        public native int fillInt64Buffer(ByteBuffer buf, int len, long value);
        @Override
        public native int fillFloatBuffer(ByteBuffer buf, int len, float value);
        @Override
        public native int fillDoubleBuffer(ByteBuffer buf, int len, double value);

        // {Short|Int|Long|Float|Double}Buffer alternative definitions
        @Override
        public native int fillInt16Buffer(ShortBuffer buf, int len, short value);
        @Override
        public native int fillInt32Buffer(IntBuffer buf, int len, int value);
        @Override
        public native int fillInt64Buffer(LongBuffer buf, int len, long value);
        @Override
        public native int fillFloatBuffer(FloatBuffer buf, int len, float value);
        @Override
        public native int fillDoubleBuffer(DoubleBuffer buf, int len, double value);

        static {
            Native.register("testlib");
        }
    }

    /* Override original. */
    @Override
    protected void setUp() {
        lib = new DirectTestLibrary();
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectBufferArgumentsMarshalTest.class);
    }

}
