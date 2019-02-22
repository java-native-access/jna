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

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;

import com.sun.jna.ReturnTypesTest.TestLibrary.SimpleStructure;
import com.sun.jna.ReturnTypesTest.TestLibrary.TestSmallStructure;
import com.sun.jna.ReturnTypesTest.TestLibrary.TestStructure;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class ReturnTypesTest extends TestCase {

    private static final String UNICODE = "[\u0444]";
    private static final int INT_MAGIC = 0x12345678;
    private static final long LONG_MAGIC = 0x123456789ABCDEF0L;
    private static final double DOUBLE_MAGIC = -118.625d;
    private static final float FLOAT_MAGIC = -118.625f;
    private static final String STRING_MAGIC = "magic";

    public static interface TestLibrary extends Library {

        public static class SimpleStructure extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("value");
            public double value;
            public static int allocations = 0;
            public SimpleStructure() { }
            public SimpleStructure(Pointer p) { super(p); read(); }
            @Override
            protected void allocateMemory(int size) {
                super.allocateMemory(size);
                ++allocations;
            }
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        public static class TestSmallStructure extends Structure {
            public static class ByValue extends TestSmallStructure implements Structure.ByValue { }

            public static final List<String> FIELDS = createFieldsOrder("c1", "c2", "s");
            public byte c1;
            public byte c2;
            public short s;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        public static class TestStructure extends Structure {
            public static class ByValue extends TestStructure implements Structure.ByValue { }

            public static final List<String> FIELDS = createFieldsOrder("c", "s", "i", "j", "inner");
            public byte c;
            public short s;
            public int i;
            public long j;
            public SimpleStructure inner;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        public static class CheckFieldAlignment extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("int32Field", "int64Field", "floatField", "doubleField");
            public int int32Field = 1;
            public long int64Field = 2;
            public float floatField = 3f;
            public double doubleField = 4d;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        class TestObject { }
        Object returnObjectArgument(Object s);
        TestObject returnObjectArgument(TestObject s);
        Class returnClass(JNIEnv env, Object arg);
        boolean returnFalse();
        boolean returnTrue();
        int returnInt32Zero();
        int returnInt32Magic();
        long returnInt64Zero();
        long returnInt64Magic();
        NativeLong returnLongZero();
        NativeLong returnLongMagic();
        float returnFloatZero();
        float returnFloatMagic();
        double returnDoubleZero();
        double returnDoubleMagic();
        String returnStringMagic();
        WString returnWStringMagic();
        SimpleStructure returnStaticTestStructure();
        SimpleStructure returnNullTestStructure();
        TestSmallStructure.ByValue returnSmallStructureByValue();
        TestStructure.ByValue returnStructureByValue();

        Pointer[] returnPointerArgument(Pointer[] arg);
        String[] returnPointerArgument(String[] arg);
        WString[] returnPointerArgument(WString[] arg);
    }

    TestLibrary lib;
    TestLibrary libSupportingObject;
    NativeMappedLibrary libNativeMapped;

    @Override
    protected void setUp() {
        lib = Native.load("testlib", TestLibrary.class);
        libSupportingObject = Native.load("testlib", TestLibrary.class,
                Collections.singletonMap(Library.OPTION_ALLOW_OBJECTS, Boolean.TRUE));
        libNativeMapped = Native.load("testlib", NativeMappedLibrary.class);
    }

    @Override
    protected void tearDown() {
        lib = null;
        libSupportingObject = null;
        libNativeMapped = null;
    }

    public void testReturnObject() throws Exception {
        assertNull("null value not returned", libSupportingObject.returnObjectArgument(null));
        final Object VALUE = new Object() {
            @Override
            public String toString() {
                return getName();
            }
        };
        assertEquals("Wrong object returned", VALUE, libSupportingObject.returnObjectArgument(VALUE));
    }

    public void testReturnObjectUnsupported() throws Exception {
        try {
            lib.returnObjectArgument(new TestLibrary.TestObject());
            fail("Java Object return is not supported, should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            assertTrue("Exception should include return object type: " + e,
                       e.getMessage().indexOf(TestLibrary.TestObject.class.getName()) != -1);
        }
        catch(Throwable e) {
            fail("Method declared with Java Object return should throw IllegalArgumentException, not " + e);
        }
    }

    public void testReturnClass() throws Exception {
        assertEquals("Wrong class returned", Class.class,
                libSupportingObject.returnClass(JNIEnv.CURRENT, TestLibrary.class));
        assertEquals("Wrong class returned", StringBuilder.class,
                libSupportingObject.returnClass(JNIEnv.CURRENT, new StringBuilder()));
    }

    public void testInvokeBoolean() {
        assertFalse("Expect false", lib.returnFalse());
        assertTrue("Expect true", lib.returnTrue());
    }

    public void testInvokeInt() {
        assertEquals("Expect 32-bit zero", 0, lib.returnInt32Zero());
        assertEquals("Expect 32-bit magic", INT_MAGIC, lib.returnInt32Magic());
    }

    public void testInvokeLong() {
        assertEquals("Expect 64-bit zero", 0L, lib.returnInt64Zero());
        assertEquals("Expect 64-bit magic", LONG_MAGIC, lib.returnInt64Magic());
    }

    public void testInvokeNativeLong() {
        if (NativeLong.SIZE == 4) {
            assertEquals("Expect 32-bit zero", new NativeLong(0), lib.returnLongZero());
            assertEquals("Expect 32-bit magic",
                         new NativeLong(INT_MAGIC), lib.returnLongMagic());
        } else {
            assertEquals("Expect 64-bit zero", new NativeLong(0L), lib.returnLongZero());
            assertEquals("Expect 64-bit magic",
                         new NativeLong(LONG_MAGIC), lib.returnLongMagic());
        }
    }

    public interface NativeMappedLibrary extends Library {
        Custom returnInt32Argument(int arg);
        size_t returnInt32Magic();
        size_t returnInt64Magic();
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
        @Override
        public boolean equals(Object o) {
            return o instanceof Custom && ((Custom)o).value == value;
        }
    }

    public void testInvokeNativeMapped() {
        final Custom EXPECTED = new Custom(INT_MAGIC);
        assertEquals("NativeMapped 'Custom' result not mapped",
                     EXPECTED, libNativeMapped.returnInt32Argument(INT_MAGIC));

        assertEquals("NativeMapped IntegerType result not mapped (32)",
                     new size_t(INT_MAGIC), libNativeMapped.returnInt32Magic());
        if (Native.SIZE_T_SIZE == 8) {
            assertEquals("NativeMapped IntegerType result not mapped (64)",
                         new size_t(LONG_MAGIC), libNativeMapped.returnInt64Magic());
        }
    }

    public void testInvokeFloat() {
        assertEquals("Expect float zero", 0f, lib.returnFloatZero(), 0d);
        assertEquals("Expect float magic",
                     FLOAT_MAGIC, lib.returnFloatMagic(), 0d);
    }

    public void testInvokeDouble() {
        assertEquals("Expect double zero", 0d, lib.returnDoubleZero(), 0d);
        assertEquals("Expect double magic",
                     DOUBLE_MAGIC, lib.returnDoubleMagic(), 0d);
    }

    public void testInvokeString() {
        assertEquals("Expect String magic", STRING_MAGIC, lib.returnStringMagic());
    }

    public void testInvokeWString() {
        WString s = lib.returnWStringMagic();
        assertEquals("Wrong length", STRING_MAGIC.length(), s.length());
        assertEquals("Expect WString magic", new WString(STRING_MAGIC), s);
    }

    public void testInvokeStructure() {
        SimpleStructure.allocations = 0;
        SimpleStructure s = lib.returnStaticTestStructure();
        assertEquals("Expect test structure magic", DOUBLE_MAGIC, s.value, 0d);
        // Optimized structure allocation
        assertEquals("Returned Structure should allocate no memory", 0, SimpleStructure.allocations);
    }

    public void testInvokeNullStructure() {
        SimpleStructure s = lib.returnNullTestStructure();
        assertNull("Expect null structure return", s);
    }

    public void testReturnSmallStructureByValue() {
        TestSmallStructure s = lib.returnSmallStructureByValue();
        assertNotNull("Returned structure must not be null", s);
        assertEquals("Wrong char field value (1)", 1, s.c1);
        assertEquals("Wrong char field value (2)", 2, s.c2);
        assertEquals("Wrong short field value", 3, s.s);
    }

    public void testReturnStructureByValue() {
        TestStructure s = lib.returnStructureByValue();
        assertNotNull("Returned structure must not be null", s);
        assertEquals("Wrong char field value", 1, s.c);
        assertEquals("Wrong short field value", 2, s.s);
        assertEquals("Wrong int field value", 3, s.i);
        assertEquals("Wrong long field value", 4, s.j);

        assertNotNull("Structure not initialized", s.inner);
        assertEquals("Wrong inner structure value", 5, s.inner.value, 0);
    }

    public void testReturnPointerArray() {
        Pointer value = new Memory(10);
        Pointer[] input = {
            value, null,
        };
        Pointer[] result = lib.returnPointerArgument(input);
        assertEquals("Wrong array length", input.length-1, result.length);
        assertEquals("Wrong array element value", value, result[0]);

        assertNull("NULL should result in null return value", lib.returnPointerArgument((Pointer[])null));
    }

    public void testReturnStringArray() {
        Charset charset = Charset.forName(Native.getDefaultStringEncoding());
        final String VALUE = getName() + charset.decode(charset.encode(UNICODE));
        String[] input = {
            VALUE, null,
        };
        String[] result = lib.returnPointerArgument(input);
        assertEquals("Wrong array length", input.length-1, result.length);
        assertEquals("Wrong array element value", VALUE, result[0]);

        assertNull("NULL should result in null return value", lib.returnPointerArgument((String[])null));
    }

    public void testReturnWStringArray() {
        final WString VALUE = new WString(getName() + UNICODE);
        WString[] input = {
            VALUE, null,
        };
        WString[] result = lib.returnPointerArgument(input);
        assertEquals("Wrong array length", input.length-1, result.length);
        assertEquals("Wrong array element value", VALUE, result[0]);

        assertNull("NULL should result in null return value", lib.returnPointerArgument((WString[])null));
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(ReturnTypesTest.class);
    }

}