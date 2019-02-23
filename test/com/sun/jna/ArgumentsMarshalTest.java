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
package com.sun.jna;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.sun.jna.ArgumentsMarshalTest.TestLibrary.CheckFieldAlignment;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
//@SuppressWarnings("unused")
public class ArgumentsMarshalTest extends TestCase {

    private static final String UNICODE = "[\0444]";

    public static interface TestLibrary extends Library {

        class CheckFieldAlignment extends Structure {
            public static class ByValue extends CheckFieldAlignment
                implements Structure.ByValue { }
            public static class ByReference extends CheckFieldAlignment
                implements Structure.ByReference { }

            public byte int8Field;
            public short int16Field;
            public int int32Field;
            public long int64Field;
            public float floatField;
            public double doubleField;

            @Override
            public List<String> getFieldOrder() {
                return Arrays.asList(new String[] { "int8Field", "int16Field", "int32Field", "int64Field", "floatField", "doubleField" });
            }
            public CheckFieldAlignment() {
                int8Field = (byte)fieldOffset("int8Field");
                int16Field = (short)fieldOffset("int16Field");
                int32Field = fieldOffset("int32Field");
                int64Field = fieldOffset("int64Field");
                floatField = fieldOffset("floatField");
                doubleField = fieldOffset("doubleField");
            }
        }

        String returnStringArgument(Object arg);
        boolean returnBooleanArgument(boolean arg);
        byte returnInt8Argument(byte arg);
        char returnWideCharArgument(char arg);
        short returnInt16Argument(short arg);
        int returnInt32Argument(int i);
        long returnInt64Argument(long l);
        NativeLong returnLongArgument(NativeLong l);
        float returnFloatArgument(float f);
        double returnDoubleArgument(double d);
        String returnStringArgument(String s);
        WString returnWStringArgument(WString s);
        Pointer returnPointerArgument(Pointer p);
        String returnStringArrayElement(String[] args, int which);
        WString returnWideStringArrayElement(WString[] args, int which);
        Pointer returnPointerArrayElement(Pointer[] args, int which);

        public static class TestPointerType extends PointerType {
            public TestPointerType() { }
            public TestPointerType(Pointer p) { super(p); }
        }
        TestPointerType returnPointerArrayElement(TestPointerType[] args, int which);
        CheckFieldAlignment returnPointerArrayElement(CheckFieldAlignment.ByReference[] args, int which);
        int returnRotatedArgumentCount(String[] args);

        long checkInt64ArgumentAlignment(int i, long j, int i2, long j2);
        double checkDoubleArgumentAlignment(float i, double j, float i2, double j2);
        Pointer testStructurePointerArgument(CheckFieldAlignment p);
        int testStructureByValueArgument(CheckFieldAlignment.ByValue p);
        int testStructureArrayInitialization(CheckFieldAlignment[] p, int len);
        int testStructureByReferenceArrayInitialization(CheckFieldAlignment.ByReference[] p, int len);
        void modifyStructureArray(CheckFieldAlignment[] p, int length);
        void modifyStructureByReferenceArray(CheckFieldAlignment.ByReference[] p, int length);

        int fillInt8Buffer(byte[] buf, int len, byte value);
        int fillInt16Buffer(short[] buf, int len, short value);
        int fillInt32Buffer(int[] buf, int len, int value);
        int fillInt64Buffer(long[] buf, int len, long value);
        int fillFloatBuffer(float[] buf, int len, float value);
        int fillDoubleBuffer(double[] buf, int len, double value);

        // boolean[] maps to jboolean* (always 8-bit), boolean mapping is 32-bit by default; use byte
        int fillInt8Buffer(boolean[] buf, int len, byte value);

        // char[] maps to jchar* (always 16-bit), char maps to wchar_t (can be 32-bit); use short
        int fillInt16Buffer(char[] buf, int len, short value);

        // Nonexistent functions
        boolean returnBooleanArgument(Object arg);

        // Structure
        class MinTestStructure extends Structure {
            public int field;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[] { "field" });
            }
        }
        Pointer testStructurePointerArgument(MinTestStructure s);

        class VariableSizedStructure extends Structure {
            public int length;
            public byte[] buffer;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[] { "length", "buffer" });
            }
            public VariableSizedStructure(String arg) {
                length = arg.length() + 1;
                buffer = new byte[length];
                System.arraycopy(arg.getBytes(), 0, buffer, 0, arg.length());
            }
        }
        String returnStringFromVariableSizedStructure(VariableSizedStructure s);
        class CbStruct extends Structure {
            public static interface TestCallback extends Callback {
                int callback(int arg1, int arg2);
            }
            public TestCallback cb;
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[] { "cb" });
            }
        }
        void setCallbackInStruct(CbStruct cbstruct);
    }

    TestLibrary lib;
    @Override
    protected void setUp() {
        lib = Native.load("testlib", TestLibrary.class);
    }

    @Override
    protected void tearDown() {
        lib = null;
    }

    public void testJavaObjectArgument() {
        Object o = this;
        try {
            lib.returnStringArgument(o);
            fail("Java Object arguments should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            assertTrue("Exception should include Object type description: " + e,
                       e.getMessage().indexOf(o.getClass().getName()) != -1);
        }
        catch(Throwable e) {
            fail("Java Object arguments should throw IllegalArgumentException, not " + e);
        }
    }

    public void testBooleanArgument() {
        assertTrue("True argument should be returned",
                   lib.returnBooleanArgument(true));
        assertFalse("False argument should be returned",
                    lib.returnBooleanArgument(false));
    }

    public void testInt8Argument() {
        byte b = 0;
        assertEquals("Wrong value returned",
                     b, lib.returnInt8Argument(b));
        b = 127;
        assertEquals("Wrong value returned",
                     b, lib.returnInt8Argument(b));
        b = -128;
        assertEquals("Wrong value returned",
                     b, lib.returnInt8Argument(b));
    }

    public void testWideCharArgument() {
        char c = 0;
        assertEquals("Wrong value returned",
                     c, lib.returnWideCharArgument(c));
        c = 0xFFFF;
        assertEquals("Wrong value returned",
                     c, lib.returnWideCharArgument(c));
        c = 0x7FFF;
        assertEquals("Wrong value returned",
                     c, lib.returnWideCharArgument(c));
    }

    public void testInt16Argument() {
        short v = 0;
        assertEquals("Wrong value returned",
                     v, lib.returnInt16Argument(v));
        v = 32767;
        assertEquals("Wrong value returned",
                     v, lib.returnInt16Argument(v));
        v = -32768;
        assertEquals("Wrong value returned",
                     v, lib.returnInt16Argument(v));
    }

    public void testIntArgument() {
        int value = 0;
        assertEquals("Should return 32-bit argument",
                     value, lib.returnInt32Argument(value));
        value = 1;
        assertEquals("Should return 32-bit argument",
                     value, lib.returnInt32Argument(value));
        value = 0x7FFFFFFF;
        assertEquals("Should return 32-bit argument",
                     value, lib.returnInt32Argument(value));
        value = 0x80000000;
        assertEquals("Should return 32-bit argument",
                     value, lib.returnInt32Argument(value));
    }

    public void testLongArgument() {
        long value = 0L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 1L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 0x7FFFFFFFL;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 0x80000000L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 0x7FFFFFFF00000000L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
        value = 0x8000000000000000L;
        assertEquals("Should return 64-bit argument",
                     value, lib.returnInt64Argument(value));
    }

    public void testNativeLongArgument() {
        NativeLong value = new NativeLong(0);
        assertEquals("Should return 0",
                     value, lib.returnLongArgument(value));
        value = new NativeLong(1);
        assertEquals("Should return 1",
                     value, lib.returnLongArgument(value));
        value = new NativeLong(0x7FFFFFFF);
        assertEquals("Should return 0x7FFFFFFF",
                     value, lib.returnLongArgument(value));
        value = new NativeLong(0x80000000);
        assertEquals("Should return 0x80000000",
                     value, lib.returnLongArgument(value));
    }

    public interface NativeMappedLibrary extends Library {
        int returnInt32Argument(Custom arg);
        int returnInt32Argument(size_t arg);
        long returnInt64Argument(size_t arg);
    }
    public static class size_t extends IntegerType {
        private static final long serialVersionUID = 1L;
        public size_t() {
            this(0);
        }
        public size_t(long value) {
            super(Native.SIZE_T_SIZE, true);
            setValue(value);
        }
    }
    public static class Custom implements NativeMapped {
        private int value;
        public Custom() { }
        public Custom(int value) {
            this.value = value;
        }
        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            return new Custom(((Integer)nativeValue).intValue());
        }
        @Override
        public Class<?> nativeType() {
            return Integer.class;
        }
        @Override
        public Object toNative() {
            return Integer.valueOf(value);
        }
    }
    protected NativeMappedLibrary loadNativeMappedLibrary() {
        return Native.load("testlib", NativeMappedLibrary.class);
    }
    public void testNativeMappedArgument() {
        NativeMappedLibrary lib = loadNativeMappedLibrary();
        final int MAGIC = 0x12345678;
        Custom arg = new Custom(MAGIC);
        assertEquals("Argument not mapped", MAGIC, lib.returnInt32Argument(arg));

        if (Native.SIZE_T_SIZE == 4) {
            size_t size = new size_t(MAGIC);
            assertEquals("Argument not mapped", MAGIC, lib.returnInt32Argument(size));
        }
        else {
            final long MAGIC64 = 0x123456789ABCDEFL;
            size_t size = new size_t(MAGIC64);
            assertEquals("Argument not mapped", MAGIC64, lib.returnInt64Argument(size));
        }
    }

    public void testPointerArgumentReturn() {
        assertEquals("Expect null pointer",
                     null, lib.returnPointerArgument(null));
        Structure s = new TestLibrary.CheckFieldAlignment();
        assertEquals("Expect structure pointer",
                     s.getPointer(),
                     lib.returnPointerArgument(s.getPointer()));
    }

    static final String MAGIC = "magic" + UNICODE;
    public void testStringArgumentReturn() {
        assertEquals("Expect null pointer", null, lib.returnStringArgument(null));
        assertEquals("Expect string magic", MAGIC, lib.returnStringArgument(MAGIC));
    }

    static final WString WMAGIC = new WString("magic" + UNICODE);
    public void testWStringArgumentReturn() {
        assertEquals("Expect null pointer", null, lib.returnWStringArgument(null));
        assertEquals("Expect string magic", WMAGIC.toString(), lib.returnWStringArgument(WMAGIC).toString());
    }

    public void testInt64ArgumentAlignment() {
        long value = lib.checkInt64ArgumentAlignment(0x10101010, 0x1111111111111111L,
                                                     0x01010101, 0x2222222222222222L);
        assertEquals("Improper handling of interspersed int32/int64",
                     0x3333333344444444L, value);
    }

    public void testDoubleArgumentAlignment() {
        double value = lib.checkDoubleArgumentAlignment(1f, 2d, 3f, 4d);
        assertEquals("Improper handling of interspersed float/double",
                     10d, value, 0);
    }

    public void testStructurePointerArgument() {
        TestLibrary.CheckFieldAlignment struct = new TestLibrary.CheckFieldAlignment();
        assertEquals("Native address of structure should be returned",
                     struct.getPointer(),
                     lib.testStructurePointerArgument(struct));
        // ensure that even if the argument is ByValue, it's passed as ptr
        struct = new TestLibrary.CheckFieldAlignment.ByValue();
        assertEquals("Structure argument should be passed according to method "
                     + "parameter type, not argument type",
                     struct.getPointer(),
                     lib.testStructurePointerArgument(struct));

        struct = null;
        assertNull("Null argument should be returned",
                   lib.testStructurePointerArgument(struct));
    }

    public void testStructureByValueArgument() {
        TestLibrary.CheckFieldAlignment.ByValue struct =
            new TestLibrary.CheckFieldAlignment.ByValue();
        assertEquals("Wrong alignment in " + struct.toString(true),
                     "0", Integer.toHexString(lib.testStructureByValueArgument(struct)));
    }

    public void testStructureByValueTypeInfo() {
        class TestStructure extends Structure implements Structure.ByValue {
            public byte b;
            public char c;
            public short s;
            public int i;
            public long j;
            public float f;
            public double d;
            public Pointer[] parray = new Pointer[2];
            public byte[] barray = new byte[2];
            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[] { "b", "c", "s", "i", "j", "f", "d", "parray", "barray" });
            }
        }
        Structure s = new TestStructure();
        // Force generation of type info
        s.size();
    }


    public void testWriteStructureArrayArgumentMemory() {
        final int LENGTH = 10;
        TestLibrary.CheckFieldAlignment block = new TestLibrary.CheckFieldAlignment();
        TestLibrary.CheckFieldAlignment[] array =
            (TestLibrary.CheckFieldAlignment[])block.toArray(LENGTH);
        for (int i=0;i < array.length;i++) {
            array[i].int32Field = i;
        }
        assertEquals("Structure array memory not properly initialized",
                     -1, lib.testStructureArrayInitialization(array, array.length));

    }

    public void testUninitializedStructureArrayArgument() {
        final int LENGTH = 10;
        TestLibrary.CheckFieldAlignment[] block =
            new TestLibrary.CheckFieldAlignment[LENGTH];
        lib.modifyStructureArray(block, block.length);
        for (int i=0;i < block.length;i++) {
            assertNotNull("Structure array not initialized at " + i, block[i]);
            assertEquals("Wrong value for int32 field of structure at " + i,
                         i, block[i].int32Field);
            assertEquals("Wrong value for int64 field of structure at " + i,
                         i + 1, block[i].int64Field);
            assertEquals("Wrong value for float field of structure at " + i,
                         i + 2, block[i].floatField, 0);
            assertEquals("Wrong value for double field of structure at " + i,
                         i + 3, block[i].doubleField, 0);
        }
    }

    public void testRejectNoncontiguousStructureArrayArgument() {
        TestLibrary.CheckFieldAlignment s1, s2, s3;
        s3 = new TestLibrary.CheckFieldAlignment();
        s1 = new TestLibrary.CheckFieldAlignment();
        s2 = new TestLibrary.CheckFieldAlignment();
        TestLibrary.CheckFieldAlignment[] block = { s1, s2, s3 };
        try {
            lib.modifyStructureArray(block, block.length);
            fail("Library invocation should fail");
        }
        catch(IllegalArgumentException e) {
        }
    }

    public void testRejectIncompatibleStructureArrayArgument() {
        TestLibrary.CheckFieldAlignment s1 = new TestLibrary.CheckFieldAlignment.ByReference();
        TestLibrary.CheckFieldAlignment[] autoArray = (TestLibrary.CheckFieldAlignment[])s1.toArray(3);
        try {
            lib.modifyStructureArray(autoArray, autoArray.length);
        }
        catch(IllegalArgumentException e) {
        }
        TestLibrary.CheckFieldAlignment.ByReference[] byRefArray =
            (TestLibrary.CheckFieldAlignment.ByReference[])s1.toArray(3);
        try {
            lib.modifyStructureArray(byRefArray, byRefArray.length);
        }
        catch(IllegalArgumentException e) {
        }
        TestLibrary.CheckFieldAlignment[] arrayWithRefElements = { autoArray[0], autoArray[1], autoArray[2] };
        try {
            lib.modifyStructureArray(arrayWithRefElements, arrayWithRefElements.length);
        }
        catch(IllegalArgumentException e) {
        }
    }

    /** When passing an array of <code>struct*</code> to native, be sure to
        invoke <code>Structure.write()</code> on each of the elements. */
    public void testWriteStructureByReferenceArrayArgumentMemory() {
        TestLibrary.CheckFieldAlignment.ByReference[] array = {
            new TestLibrary.CheckFieldAlignment.ByReference(),
            new TestLibrary.CheckFieldAlignment.ByReference(),
            new TestLibrary.CheckFieldAlignment.ByReference(),
            new TestLibrary.CheckFieldAlignment.ByReference(),
        };
        for (int i=0;i < array.length;i++) {
            array[i].int32Field = i;
        }
        assertEquals("Structure.ByReference array memory not properly initialized",
                     -1, lib.testStructureByReferenceArrayInitialization(array, array.length));
    }

    public void testReadStructureByReferenceArrayArgumentMemory() {
        TestLibrary.CheckFieldAlignment.ByReference[] array = {
            new TestLibrary.CheckFieldAlignment.ByReference(),
            new TestLibrary.CheckFieldAlignment.ByReference(),
            new TestLibrary.CheckFieldAlignment.ByReference(),
            new TestLibrary.CheckFieldAlignment.ByReference(),
        };
        lib.modifyStructureByReferenceArray(array, array.length);
        for (int i=0;i < array.length;i++) {
            assertEquals("Wrong value for int32 field of structure at " + i,
                         i, array[i].int32Field);
            assertEquals("Wrong value for int64 field of structure at " + i,
                         i + 1, array[i].int64Field);
            assertEquals("Wrong value for float field of structure at " + i,
                         i + 2, array[i].floatField, 0);
            assertEquals("Wrong value for double field of structure at " + i,
                         i + 3, array[i].doubleField, 0);
        }
    }

    public void testBooleanArrayArgument() {
        boolean[] buf = new boolean[1024];
        assertEquals("Wrong return value", buf.length,
                     lib.fillInt8Buffer(buf, buf.length, (byte)1));
        for (int i=0;i < buf.length;i++) {
            assertTrue("Bad value at index " + i, buf[i]);
        }
        assertEquals("Wrong return value", buf.length,
                     lib.fillInt8Buffer(buf, buf.length, (byte)0));
        for (int i=0;i < buf.length;i++) {
            assertFalse("Bad value at index " + i, buf[i]);
        }
    }

    public void testByteArrayArgument() {
        byte[] buf = new byte[1024];
        final byte MAGIC = (byte)0xED;
        assertEquals("Wrong return value", buf.length,
                     lib.fillInt8Buffer(buf, buf.length, MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }

    public void testCharArrayArgument() {
        char[] buf = new char[1024];
        final char MAGIC = '\uFEFF';
        assertEquals("Wrong return value", buf.length,
                     lib.fillInt16Buffer(buf, buf.length, (short)MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }

    public void testShortArrayArgument() {
        short[] buf = new short[1024];
        final short MAGIC = (short)0xABED;
        assertEquals("Wrong return value", buf.length,
                     lib.fillInt16Buffer(buf, buf.length, MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }

    public void testIntArrayArgument() {
        int[] buf = new int[1024];
        final int MAGIC = 0xABEDCF23;
        assertEquals("Wrong return value", buf.length,
                     lib.fillInt32Buffer(buf, buf.length, MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }

    public void testLongArrayArgument() {
        long[] buf = new long[1024];
        final long MAGIC = 0x1234567887654321L;
        assertEquals("Wrong return value", buf.length,
                     lib.fillInt64Buffer(buf, buf.length, MAGIC));
        for (int i=0;i < buf.length;i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf[i]);
        }
    }

    public void testUnsupportedJavaObjectArgument() {
        try {
            lib.returnBooleanArgument(this);
            fail("Unsupported Java objects should be rejected");
        }
        catch(IllegalArgumentException e) {
        }
    }

    public void testStringArrayArgument() {
        String[] args = { "one"+UNICODE, "two"+UNICODE, "three"+UNICODE };
        assertEquals("Wrong value returned", args[0], lib.returnStringArrayElement(args, 0));
        assertNull("Native String array should be null terminated",
                   lib.returnStringArrayElement(args, args.length));
    }

    public void testWideStringArrayArgument() {
        WString[] args = { new WString("one"+UNICODE), new WString("two"+UNICODE), new WString("three"+UNICODE) };
        assertEquals("Wrong value returned", args[0], lib.returnWideStringArrayElement(args, 0));
        assertNull("Native WString array should be null terminated",
                   lib.returnWideStringArrayElement(args, args.length));
    }

    public void testPointerArrayArgument() {
        Pointer[] args = {
            new NativeString(getName()).getPointer(),
            null,
            new NativeString(getName()+"2").getPointer(),
        };
        Pointer[] originals = new Pointer[args.length];
        System.arraycopy(args, 0, originals, 0, args.length);

        assertEquals("Wrong value returned", args[0], lib.returnPointerArrayElement(args, 0));
        assertNull("Wrong value returned", lib.returnPointerArrayElement(args, 1));
        assertEquals("Wrong value returned", args[2], lib.returnPointerArrayElement(args, 2));
        assertNull("Native array should be null terminated", lib.returnPointerArrayElement(args, 3));

        assertSame("Argument pointers should remain unmodified [0]",
                   originals[0], args[0]);
        assertSame("Argument pointers should remain unmodified [2]",
                   originals[2], args[2]);
    }

    public void testNativeMappedArrayArgument() {
        TestLibrary.TestPointerType[] args = {
            new TestLibrary.TestPointerType(new NativeString(getName()).getPointer()),
            null,
            new TestLibrary.TestPointerType(new NativeString(getName()+"2").getPointer()),
        };
        assertEquals("Wrong value returned", args[0], lib.returnPointerArrayElement(args, 0));
        assertNull("Wrong value returned", lib.returnPointerArrayElement(args, 1));
        assertEquals("Wrong value returned", args[2], lib.returnPointerArrayElement(args, 2));
    };

    public void testStructureByReferenceArrayArgument() {
        CheckFieldAlignment.ByReference[] args = {
            new CheckFieldAlignment.ByReference(),
            null,
            new CheckFieldAlignment.ByReference(),
        };
        assertTrue("Wrong value returned (0)", args[0].dataEquals(lib.returnPointerArrayElement(args, 0), true));
        assertNull("Wrong value returned (1)", lib.returnPointerArrayElement(args, 1));
        assertTrue("Wrong value returned (2)", args[2].dataEquals(lib.returnPointerArrayElement(args, 2), true));
        assertNull("Native array should be null terminated", lib.returnPointerArrayElement(args, 3));
    }

    public void testModifiedCharArrayArgument() {
        String[] args = { "one", "two", "three" };
        assertEquals("Wrong native array count", args.length, lib.returnRotatedArgumentCount(args));
        assertEquals("Modified array argument not re-read",
                     Arrays.asList(new String[] { "two", "three", "one" }),
                     Arrays.asList(args));
    }

    public void testReadFunctionPointerAsCallback() {
        TestLibrary.CbStruct s = new TestLibrary.CbStruct();
        assertNull("Function pointer field should be null", s.cb);
        lib.setCallbackInStruct(s);
        assertNotNull("Callback field not set", s.cb);
    }

    public void testCallProxiedFunctionPointer() {
        TestLibrary.CbStruct s = new TestLibrary.CbStruct();
        lib.setCallbackInStruct(s);
        assertEquals("Proxy to native function pointer failed: " + s.cb,
                     3, s.cb.callback(1, 2));
    }

    public void testVariableSizedStructureArgument() {
        String EXPECTED = getName();
        TestLibrary.VariableSizedStructure s =
            new TestLibrary.VariableSizedStructure(EXPECTED);
        assertEquals("Wrong string returned from variable sized struct",
                     EXPECTED, lib.returnStringFromVariableSizedStructure(s));
    }

    public void testDisableAutoSynch() {
        TestLibrary.MinTestStructure s = new TestLibrary.MinTestStructure();
        final int VALUE = 42;
        s.field = VALUE;
        s.setAutoWrite(false);
        lib.testStructurePointerArgument(s);
        assertEquals("Auto write should be disabled", 0, s.field);

        final int EXPECTED = s.field;
        s.getPointer().setInt(0, VALUE);
        s.setAutoRead(false);
        lib.testStructurePointerArgument(s);
        assertEquals("Auto read should be disabled", EXPECTED, s.field);
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ArgumentsMarshalTest.class);
    }

}
