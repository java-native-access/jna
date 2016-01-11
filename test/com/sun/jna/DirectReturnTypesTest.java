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

import java.util.Map;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class DirectReturnTypesTest extends ReturnTypesTest {

    public static class DirectTestLibrary implements TestLibrary {

        @Override
        public Object returnObjectArgument(Object s) {
            throw new IllegalArgumentException(s.getClass().getName());
        }
        @Override
        public TestObject returnObjectArgument(TestObject s) {
            throw new IllegalArgumentException(s.getClass().getName());
        }
        @Override
        public native boolean returnFalse();
        @Override
        public native boolean returnTrue();
        @Override
        public native int returnInt32Zero();
        @Override
        public native int returnInt32Magic();
        @Override
        public native long returnInt64Zero();
        @Override
        public native long returnInt64Magic();
        @Override
        public native NativeLong returnLongZero();
        @Override
        public native NativeLong returnLongMagic();
        @Override
        public native float returnFloatZero();
        @Override
        public native float returnFloatMagic();
        @Override
        public native double returnDoubleZero();
        @Override
        public native double returnDoubleMagic();
        @Override
        public native String returnStringMagic();
        @Override
        public native WString returnWStringMagic();
        @Override
        public native SimpleStructure returnStaticTestStructure();
        @Override
        public native SimpleStructure returnNullTestStructure();
        @Override
        public native TestSmallStructure.ByValue returnSmallStructureByValue();
        @Override
        public native TestStructure.ByValue returnStructureByValue();

        @Override
        public Pointer[] returnPointerArgument(Pointer[] arg) {throw new UnsupportedOperationException();}
        @Override
        public String[] returnPointerArgument(String[] arg) {throw new UnsupportedOperationException();}
        @Override
        public WString[] returnPointerArgument(WString[] arg) {throw new UnsupportedOperationException();}

        static {
            Native.register("testlib");
        }
    }

    @Override
    protected void setUp() {
        lib = new DirectTestLibrary();
    }

    public static class DirectObjectTestLibrary extends DirectTestLibrary {
        public DirectObjectTestLibrary(Map<String, ?> options) {
            Native.register(getClass(), NativeLibrary.getInstance("testlib", options));
        }
    }

    public static class DirectNativeMappedLibrary implements NativeMappedLibrary {
        @Override
        public native Custom returnInt32Argument(int arg);
        @Override
        public native size_t returnInt32Magic();
        @Override
        public native size_t returnInt64Magic();
        static {
            Native.register("testlib");
        }
    }
    @Override
    protected NativeMappedLibrary loadNativeMappedLibrary() {
        return new DirectNativeMappedLibrary();
    }

    // Override not-yet-supported tests
    @Override
    public void testReturnObject() { }
    @Override
    public void testReturnPointerArray() { }
    @Override
    public void testReturnStringArray() { }
    @Override
    public void testReturnWStringArray() { }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectReturnTypesTest.class);
    }
}
