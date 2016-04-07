/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
