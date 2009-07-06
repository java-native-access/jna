/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import com.sun.jna.ArgumentsMarshalTest.TestLibrary.CheckFieldAlignment;

import junit.framework.TestCase;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class ArgumentsMarshalTest extends TestCase {

    public static interface TestLibrary extends Library {
        
        class CheckFieldAlignment extends Structure {
            public static class ByValue extends CheckFieldAlignment 
                implements Structure.ByValue { }
            public static class ByReference extends CheckFieldAlignment
                implements Structure.ByReference { }
        
            public byte int8Field = 1;
            public short int16Field = 2;
            public int int32Field = 3;
            public long int64Field = 4;
            public float floatField = 5f;
            public double doubleField = 6d;
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
        double testStructureByValueArgument(CheckFieldAlignment.ByValue p);
        int testStructureArrayInitialization(CheckFieldAlignment[] p, int len);
        void modifyStructureArray(CheckFieldAlignment[] p, int length);
        
        int fillInt8Buffer(byte[] buf, int len, byte value);
        int fillInt16Buffer(short[] buf, int len, short value);
        int fillInt32Buffer(int[] buf, int len, int value);
        int fillInt64Buffer(long[] buf, int len, long value);

        // ByteBuffer alternative definitions
        int fillInt8Buffer(ByteBuffer buf, int len, byte value);
        int fillInt16Buffer(ByteBuffer buf, int len, short value);
        int fillInt32Buffer(ByteBuffer buf, int len, int value);
        int fillInt64Buffer(ByteBuffer buf, int len, long value);
        
        // {Short,Int,Long}Buffer alternative definitions        
        int fillInt16Buffer(ShortBuffer buf, int len, short value);
        int fillInt32Buffer(IntBuffer buf, int len, int value);
        int fillInt64Buffer(LongBuffer buf, int len, long value);

        // Nonexistent functions 
        boolean returnBooleanArgument(Object arg);

        // Structure
        class MinTestStructure extends Structure {
            public int field;
        }
        Pointer testStructurePointerArgument(MinTestStructure s);

        class VariableSizedStructure extends Structure {
            public int length;
            public byte[] buffer;
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
        }
        void setCallbackInStruct(CbStruct cbstruct);

        // Union (by value)
        class TestUnion extends Union implements Structure.ByValue {
            public String f1;
            public int f2;
        }
        interface UnionCallback extends Callback {
            TestUnion invoke(TestUnion arg);
        }
        TestUnion testUnionByValueCallbackArgument(UnionCallback cb, TestUnion arg);
    }

    TestLibrary lib;
    protected void setUp() {
        lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
    }
    
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
    }
    public static class Custom implements NativeMapped {
        private int value;
        public Custom() { }
        public Custom(int value) {
            this.value = value;
        }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            return new Custom(((Integer)nativeValue).intValue());
        }
        public Class nativeType() {
            return Integer.class;
        }
        public Object toNative() {
            return new Integer(value);
        }
    }
    protected NativeMappedLibrary loadNativeMappedLibrary() {
        return (NativeMappedLibrary)
            Native.loadLibrary("testlib", NativeMappedLibrary.class);
    }
    public void testNativeMappedArgument() {
        NativeMappedLibrary lib = loadNativeMappedLibrary();
        final int MAGIC = 0x12345678;
        Custom arg = new Custom(MAGIC);
        assertEquals("Argument not mapped", MAGIC, lib.returnInt32Argument(arg));
    }
    
    public void testPointerArgumentReturn() {
        assertEquals("Expect null pointer",
                     null, lib.returnPointerArgument(null));
        Structure s = new TestLibrary.CheckFieldAlignment();
        assertEquals("Expect structure pointer",
                     s.getPointer(), 
                     lib.returnPointerArgument(s.getPointer()));
    }

    static final String MAGIC = "magic";
    public void testStringArgumentReturn() {
        assertEquals("Expect null pointer", null, lib.returnStringArgument(null));
        assertEquals("Expect string magic", MAGIC, lib.returnStringArgument(MAGIC));
    }

    public void testWStringArgumentReturn() {
        assertEquals("Expect null pointer", null, lib.returnStringArgument(null));
        assertEquals("Expect string magic", MAGIC, lib.returnStringArgument(MAGIC).toString());
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
    }

    public void testStructureByValueArgument() {
        TestLibrary.CheckFieldAlignment.ByValue struct = 
            new TestLibrary.CheckFieldAlignment.ByValue();
        assertEquals("Wrong sum of fields", 
                     21d, lib.testStructureByValueArgument(struct));
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
        }
        Structure s = new TestStructure();
        // Force generation of type info
        s.size();
    }

    
    public void testWriteStructureArrayArgumentMemory() {
        final int LENGTH = 10;
        TestLibrary.CheckFieldAlignment block = new TestLibrary.CheckFieldAlignment();
        block.useMemory(new Memory(block.size() * LENGTH));
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
    
    public void testByteArrayArgument() {
        byte[] buf = new byte[1024];
        final byte MAGIC = (byte)0xED;
        assertEquals("Wrong return value", buf.length, 
                     lib.fillInt8Buffer(buf, buf.length, MAGIC));
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
    
    public void testByteBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocate(1024).order(ByteOrder.nativeOrder());
        final byte MAGIC = (byte)0xED;
        lib.fillInt8Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    public void testShortBufferArgument() {
        ShortBuffer buf  = ShortBuffer.allocate(1024);
        final short MAGIC = (short)0xABED;
        lib.fillInt16Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    public void testIntBufferArgument() {
        IntBuffer buf  = IntBuffer.allocate(1024);
        final int MAGIC = 0xABEDCF23;
        lib.fillInt32Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    public void testLongBufferArgument() {
        LongBuffer buf  = LongBuffer.allocate(1024);
        final long MAGIC = 0x1234567887654321L;
        lib.fillInt64Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    
    public void testDirectByteBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder());
        final byte MAGIC = (byte)0xED;
        lib.fillInt8Buffer(buf, 1024, MAGIC);
        for (int i=0;i < buf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, buf.get(i));
        }
    }
    
    public void testDirectShortBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*2).order(ByteOrder.nativeOrder());
        ShortBuffer shortBuf = buf.asShortBuffer();
        final short MAGIC = (short)0xABED;
        lib.fillInt16Buffer(shortBuf, 1024, MAGIC);
        for (int i=0;i < shortBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, shortBuf.get(i));
        }
    }
    
    public void testDirectIntBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*4).order(ByteOrder.nativeOrder());
        IntBuffer intBuf = buf.asIntBuffer();
        final int MAGIC = 0xABEDCF23;
        lib.fillInt32Buffer(intBuf, 1024, MAGIC);
        for (int i=0;i < intBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, intBuf.get(i));
        }
    }
    
    public void testDirectLongBufferArgument() {
        ByteBuffer buf  = ByteBuffer.allocateDirect(1024*8).order(ByteOrder.nativeOrder());
        LongBuffer longBuf = buf.asLongBuffer();
        final long MAGIC = 0x1234567887654321L;
        lib.fillInt64Buffer(longBuf, 1024, MAGIC);
        for (int i=0;i < longBuf.capacity();i++) {
            assertEquals("Bad value at index " + i, MAGIC, longBuf.get(i));
        }
    }
    
    public void testInvalidArgument() {
        try {
            lib.returnBooleanArgument(this);
            fail("Unsupported Java objects should be rejected");
        }
        catch(IllegalArgumentException e) {
        }
    }
    
    public void testStringArrayArgument() {
        String[] args = { "one", "two", "three" };
        assertEquals("Wrong value returned", args[0], lib.returnStringArrayElement(args, 0));
        assertNull("Native String array should be null terminated", 
                   lib.returnStringArrayElement(args, args.length));
    }
    
    public void testWideStringArrayArgument() {
        WString[] args = { new WString("one"), new WString("two"), new WString("three") };
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
        assertEquals("Wrong value returned", args[0], lib.returnPointerArrayElement(args, 0));
        assertNull("Wrong value returned", lib.returnPointerArrayElement(args, 1));
        assertEquals("Wrong value returned", args[2], lib.returnPointerArrayElement(args, 2));
        assertNull("Native array should be null terminated", lib.returnPointerArrayElement(args, 3));
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
        assertEquals("Wrong value returned", args[0], lib.returnPointerArrayElement(args, 0));
        assertNull("Wrong value returned", lib.returnPointerArrayElement(args, 1));
        assertEquals("Wrong value returned", args[2], lib.returnPointerArrayElement(args, 2));
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

    public void testUnionByValueCallbackArgument() throws Exception{
        TestLibrary.TestUnion arg = new TestLibrary.TestUnion();
        arg.setType(String.class);
        final String VALUE = getName();
        arg.f1 = VALUE;
        final boolean[] called = { false };
        final String[] cbvalue = { null };
        TestLibrary.TestUnion result = lib.testUnionByValueCallbackArgument(new TestLibrary.UnionCallback() {
                public TestLibrary.TestUnion invoke(TestLibrary.TestUnion v) {
                    called[0] = true;
                    v.setType(String.class);
                    v.read();
                    cbvalue[0] = v.f1;
                    return v;
                }
            }, arg);
        assertTrue("Callback not called", called[0]);
        assertEquals("Incorrect callback union argument", VALUE, cbvalue[0]);
        assertEquals("Union value not propagated", VALUE, result.getTypedValue(String.class));
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ArgumentsMarshalTest.class);
    }
    
}
