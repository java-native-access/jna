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

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class DirectArgumentsMarshalTest extends ArgumentsMarshalTest {

    public static class DirectTestLibrary implements TestLibrary {
        /** Dummy.  Automatically fail when passed an object. */
        @Override
        public String returnStringArgument(Object arg) {throw new IllegalArgumentException(arg.getClass().getName()); }
        @Override
        public native boolean returnBooleanArgument(boolean arg);
        @Override
        public native byte returnInt8Argument(byte arg);
        @Override
        public native char returnWideCharArgument(char arg);
        @Override
        public native short returnInt16Argument(short arg);
        @Override
        public native int returnInt32Argument(int i);
        @Override
        public native long returnInt64Argument(long l);
        @Override
        public native NativeLong returnLongArgument(NativeLong l);
        @Override
        public native float returnFloatArgument(float f);
        @Override
        public native double returnDoubleArgument(double d);
        @Override
        public native String returnStringArgument(String s);
        @Override
        public native WString returnWStringArgument(WString s);
        @Override
        public native Pointer returnPointerArgument(Pointer p);
        @Override
        public String returnStringArrayElement(String[] args, int which) {throw new UnsupportedOperationException();}
        @Override
        public WString returnWideStringArrayElement(WString[] args, int which) {throw new UnsupportedOperationException();}
        @Override
        public Pointer returnPointerArrayElement(Pointer[] args, int which) {throw new UnsupportedOperationException();}
        @Override
        public TestPointerType returnPointerArrayElement(TestPointerType[] args, int which) {throw new UnsupportedOperationException();}
        @Override
        public CheckFieldAlignment returnPointerArrayElement(CheckFieldAlignment.ByReference[] args, int which) {throw new UnsupportedOperationException();}
        @Override
        public int returnRotatedArgumentCount(String[] args) {throw new UnsupportedOperationException();}

        @Override
        public native long checkInt64ArgumentAlignment(int i, long j, int i2, long j2);
        @Override
        public native double checkDoubleArgumentAlignment(float i, double j, float i2, double j2);
        @Override
        public native Pointer testStructurePointerArgument(CheckFieldAlignment p);
        @Override
        public native int testStructureByValueArgument(CheckFieldAlignment.ByValue p);
        @Override
        public int testStructureArrayInitialization(CheckFieldAlignment[] p, int len) {
            throw new UnsupportedOperationException();
        }
        @Override
        public int testStructureByReferenceArrayInitialization(CheckFieldAlignment.ByReference[] p, int len) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void modifyStructureArray(CheckFieldAlignment[] p, int length) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void modifyStructureByReferenceArray(CheckFieldAlignment.ByReference[] p, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public native int fillInt8Buffer(byte[] buf, int len, byte value);
        @Override
        public native int fillInt16Buffer(short[] buf, int len, short value);
        @Override
        public native int fillInt32Buffer(int[] buf, int len, int value);
        @Override
        public native int fillInt64Buffer(long[] buf, int len, long value);
        @Override
        public native int fillFloatBuffer(float[] buf, int len, float value);
        @Override
        public native int fillDoubleBuffer(double[] buf, int len, double value);
        @Override
        public native int fillInt8Buffer(boolean[] buf, int len, byte value);
        @Override
        public native int fillInt16Buffer(char[] buf, int len, short value);

        // dummy to avoid causing Native.register to fail
        @Override
        public boolean returnBooleanArgument(Object arg) {throw new IllegalArgumentException();}

        @Override
        public native Pointer testStructurePointerArgument(MinTestStructure s);
        @Override
        public native String returnStringFromVariableSizedStructure(VariableSizedStructure s);
        @Override
        public native void setCallbackInStruct(CbStruct s);

        static {
            Native.register("testlib");
        }
    }

    /* Override original. */
    @Override
    protected void setUp() {
        lib = new DirectTestLibrary();
    }

    public static class DirectNativeMappedLibrary implements NativeMappedLibrary {
        @Override
        public native int returnInt32Argument(Custom arg);
        @Override
        public native int returnInt32Argument(size_t arg);
        @Override
        public native long returnInt64Argument(size_t arg);
        static {
            Native.register("testlib");
        }
    }
    @Override
    protected NativeMappedLibrary loadNativeMappedLibrary() {
        return new DirectNativeMappedLibrary();
    }

    // This test crashes on w32 IBM J9 unless -Xint is used
    // (jvmwi3260-20080415_18762)
    @Override
    public void testWideCharArgument() {
        if (Platform.isWindows()
            && "IBM".equals(System.getProperty("java.vm.vendor"))) {
            fail("XFAIL, crash avoided");
        }
        super.testWideCharArgument();
    }
    // This test crashes on w32 IBM J9 unless -Xint is used
    // (jvmwi3260-20080415_18762)
    @Override
    public void testWStringArgumentReturn() {
        if (Platform.isWindows()
            && "IBM".equals(System.getProperty("java.vm.vendor"))) {
            fail("XFAIL, crash avoided");
        }
        super.testWStringArgumentReturn();
    }

    // Override tests not yet supported in direct mode
    @Override
    public void testStringArrayArgument() { }
    @Override
    public void testWriteStructureArrayArgumentMemory() { }
    @Override
    public void testUninitializedStructureArrayArgument() { }
    @Override
    public void testRejectNoncontiguousStructureArrayArgument() { }
    @Override
    public void testRejectIncompatibleStructureArrayArgument() { }
    @Override
    public void testWideStringArrayArgument() { }
    @Override
    public void testPointerArrayArgument() { }
    @Override
    public void testNativeMappedArrayArgument() { }
    @Override
    public void testStructureByReferenceArrayArgument() { }
    @Override
    public void testWriteStructureByReferenceArrayArgumentMemory() { }
    @Override
    public void testReadStructureByReferenceArrayArgumentMemory() { }
    @Override
    public void testModifiedCharArrayArgument() { }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectArgumentsMarshalTest.class);
    }

}
