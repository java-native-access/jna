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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Map;

import junit.framework.TestCase;

/** General structure by value functionality tests. */
public class DirectStructureByValueTest extends StructureByValueTest {

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectStructureByValueTest.class);
    }

    public static class DirectTestLibrary implements TestLibrary {
        public native byte testStructureByValueArgument8(ByValue8 arg);
        public native short testStructureByValueArgument16(ByValue16 arg);
        public native int testStructureByValueArgument32(ByValue32 arg);
        public native long testStructureByValueArgument64(ByValue64 arg);
        public native long testStructureByValueArgument128(ByValue128 arg);
        static {
            Native.register("testlib");
        }
    }

    protected void setUp() {
        lib = new DirectTestLibrary();
    }
}