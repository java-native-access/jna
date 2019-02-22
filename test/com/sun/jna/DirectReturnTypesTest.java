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

import java.util.Collections;

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
        public Class returnClass(JNIEnv env, Object arg) {
            throw new IllegalArgumentException(arg.getClass().getName());
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

    public static class DirectObjectTestLibrary extends DirectTestLibrary {
        @Override
        public native Object returnObjectArgument(Object s);
        @Override
        public native TestObject returnObjectArgument(TestObject s);
        @Override
        public native Class returnClass(JNIEnv env, Object arg);

        static {
            Native.register(NativeLibrary.getInstance("testlib",
                    Collections.singletonMap(Library.OPTION_ALLOW_OBJECTS, Boolean.TRUE)));
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
    protected void setUp() {
        lib = new DirectTestLibrary();
        libSupportingObject = new DirectObjectTestLibrary();
        libNativeMapped = new DirectNativeMappedLibrary();
    }

    // Override not-yet-supported tests
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
