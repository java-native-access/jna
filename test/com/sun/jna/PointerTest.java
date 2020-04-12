/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
 * Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import junit.framework.TestCase;


public class PointerTest extends TestCase {

    private static final String UNICODE = "[\u0444]";

    public void testGetNativeLong() {
        Memory m = new Memory(8);
        if (NativeLong.SIZE == 4) {
            final int MAGIC = 0xABEDCF23;
            m.setInt(0, MAGIC);
            NativeLong l = m.getNativeLong(0);
            assertEquals("Native long mismatch", MAGIC, l.intValue());
        } else {
            final long MAGIC = 0x1234567887654321L;
            m.setLong(0, MAGIC);
            NativeLong l = m.getNativeLong(0);
            assertEquals("Native long mismatch", MAGIC, l.longValue());
        }
    }

    public void testSetNativeLong() {
        Memory m = new Memory(8);
        if (NativeLong.SIZE == 4) {
            final int MAGIC = 0xABEDCF23;
            m.setNativeLong(0, new NativeLong(MAGIC));
            assertEquals("Native long mismatch", MAGIC, m.getInt(0));
        } else {
            final long MAGIC = 0x1234567887654321L;
            m.setNativeLong(0, new NativeLong(MAGIC));
            assertEquals("Native long mismatch", MAGIC, m.getLong(0));
        }
    }

    public void testGetSetStringWithDefaultEncoding() throws Exception {
        final String ENCODING = Native.DEFAULT_ENCODING;
        String VALUE = getName();
        int size = VALUE.getBytes(ENCODING).length+1;
        Memory m = new Memory(size);
        m.setString(0, VALUE);
        assertEquals("Wrong decoded value", VALUE, m.getString(0));
    }

    public void testGetSetStringWithCustomEncoding() throws Exception {
        final String ENCODING = "utf8";
        String VALUE = getName() + UNICODE;
        int size = VALUE.getBytes(ENCODING).length+1;
        Memory m = new Memory(size);
        m.setString(0, VALUE, ENCODING);
        assertEquals("Wrong decoded value", VALUE, m.getString(0, ENCODING));
    }

    public void testGetStringWithMaxBytesWithDefaultEncoding() throws Exception {
        final String ENCODING = Native.DEFAULT_ENCODING;
        String VALUE = "Hello World of " + '\0' + "Null";
        int size = VALUE.getBytes(ENCODING).length + 1;
        Memory m = new Memory(size);
        m.setString(0, VALUE);
        assertEquals("Wrong decoded value", "Hello World of ", m.getString(0, size));
        assertEquals("Wrong decoded value", "Hello", m.getString(0, 5));
    }

    public void testGetStringWithMaxBytesWithCustomEncoding() throws Exception {
        final String ENCODING = "utf8";
        String VALUE = "Hello World of " + '\0' + UNICODE;
        int size = VALUE.getBytes(ENCODING).length + 1;
        Memory m = new Memory(size);
        m.setString(0, VALUE);
        assertEquals("Wrong decoded value", "Hello World of ", m.getString(0, size, ENCODING));
        assertEquals("Wrong decoded value", "Hello", m.getString(0, 5, ENCODING));
    }

    public void testGetWideStringWithMaxBytes() throws Exception {
        String VALUE = "Hello Wide World of " + '\0' + "Null";
        int size = (VALUE.length() + 1) * Native.WCHAR_SIZE;
        Memory m = new Memory(size);
        m.setWideString(0, VALUE);
        assertEquals("Wrong decoded value", "Hello Wide World of ", m.getWideString(0, size));
        int wideSize = 5 * Native.WCHAR_SIZE;
        assertEquals("Wrong decoded value", "Hello", m.getWideString(0, wideSize));
    }

    public static class TestPointerType extends PointerType {
        public TestPointerType() { }
        public TestPointerType(Pointer p) { super(p); }
    }

    public void testSetNativeMapped() {
        Pointer p = new Memory(Native.POINTER_SIZE);
        TestPointerType tp = new TestPointerType(p);

        p.setValue(0, tp, tp.getClass());

        assertEquals("Wrong value written", p, p.getPointer(0));
    }

    public void testGetNativeMapped() {
        Pointer p = new Memory(Native.POINTER_SIZE);
        p.setPointer(0, null);
        Object o = p.getValue(0, TestPointerType.class, null);
        assertNull("Wrong empty value: " + o, o);
        p.setPointer(0, p);
        TestPointerType tp = new TestPointerType(p);
        assertEquals("Wrong value", tp, p.getValue(0, TestPointerType.class, null));
    }

    public void testGetStringArray() {
        Pointer p = new Memory(Native.POINTER_SIZE*3);
        final String VALUE1 = getName() + UNICODE;
        final String VALUE2 = getName() + "2" + UNICODE;
        final String ENCODING = "utf8";

        p.setPointer(0, new NativeString(VALUE1, ENCODING).getPointer());
        p.setPointer(Native.POINTER_SIZE, new NativeString(VALUE2, ENCODING).getPointer());
        p.setPointer(Native.POINTER_SIZE*2, null);

        assertEquals("Wrong null-terminated String array",
                     Arrays.asList(new String[] { VALUE1, VALUE2 }),
                     Arrays.asList(p.getStringArray(0, ENCODING)));

        assertEquals("Wrong length-specified String array (1)",
                     Arrays.asList(new String[] { VALUE1 }),
                     Arrays.asList(p.getStringArray(0, 1, ENCODING)));
        assertEquals("Wrong length-specified String array (2)",
                     Arrays.asList(new String[] { VALUE1, VALUE2 }),
                     Arrays.asList(p.getStringArray(0, 2, ENCODING)));
    }

    public void testGetWideStringArray() {
        Pointer p = new Memory(Native.POINTER_SIZE*3);
        final String VALUE1 = getName() + UNICODE;
        final String VALUE2 = getName() + "2" + UNICODE;

        p.setPointer(0, new NativeString(VALUE1, true).getPointer());
        p.setPointer(Native.POINTER_SIZE, new NativeString(VALUE2, true).getPointer());
        p.setPointer(Native.POINTER_SIZE*2, null);

        assertEquals("Wrong null-terminated String array",
                     Arrays.asList(new String[] { VALUE1, VALUE2 }),
                     Arrays.asList(p.getWideStringArray(0)));

        assertEquals("Wrong length-specified String array (1)",
                     Arrays.asList(new String[] { VALUE1 }),
                     Arrays.asList(p.getWideStringArray(0, 1)));
        assertEquals("Wrong length-specified String array (2)",
                     Arrays.asList(new String[] { VALUE1, VALUE2 }),
                     Arrays.asList(p.getWideStringArray(0, 2)));
    }

    public void testReadPointerArray() {
        Pointer mem = new Memory(Native.POINTER_SIZE * 2);
        Pointer[] p = new Pointer[2];
        String VALUE1 = getName();

        p[0] = new NativeString(VALUE1).getPointer();
        p[1] = new Memory(1024);
        Pointer[] orig = new Pointer[p.length];
        System.arraycopy(p, 0, orig, 0, p.length);

        mem.write(0, p, 0, p.length);
        mem.read(0, p, 0, p.length);

        assertSame("Pointer object not preserved[0]", orig[0], p[0]);
        assertSame("Pointer object not preserved[1]", orig[1], p[1]);

        mem.setPointer(0, null);
        mem.setPointer(Native.POINTER_SIZE, new Memory(1024));
        mem.read(0, p, 0, p.length);
        assertNull("Pointer element not updated[0]", p[0]);
        assertNotSame("Pointer element not updated[1]", orig[1], p[1]);
    }

    public void testCreateConstantPointer() {
        Pointer p = Pointer.createConstant(0xFFFFFFFF);
        assertEquals("Wrong peer value", p.peer, 0xFFFFFFFF);

        p = Pointer.createConstant(-1);
        assertEquals("Wrong peer value", p.peer, -1);
    }

    public void testReadStringArrayNULLElement() {
        Memory m = new Memory(Native.POINTER_SIZE);
        m.clear();
        String[] arr = m.getStringArray(0, 1);
        assertEquals("Wrong array size", 1, arr.length);
        assertNull("Array element should be null", arr[0]);
    }

    private Object defaultArg(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) return Boolean.FALSE;
        if (type == byte.class || type == Byte.class) return Byte.valueOf((byte)0);
        if (type == char.class || type == Character.class) return Character.valueOf((char)0);
        if (type == short.class || type == Short.class) return Short.valueOf((short)0);
        if (type == int.class || type == Integer.class) return Integer.valueOf(0);
        if (type == long.class || type == Long.class) return Long.valueOf(0L);
        if (type == float.class || type == Float.class) return Float.valueOf(0);
        if (type == double.class || type == Double.class) return Double.valueOf(0);
        if (type == NativeLong.class) return new NativeLong(0);
        return null;
    }

    public void testOpaquePointer() throws Exception {
        Pointer p = Pointer.createConstant(0);
        Class<?> cls = p.getClass();
        Method[] methods = cls.getMethods();
        for (int i=0;i < methods.length;i++) {
            Method m = methods[i];
            Class<?>[] argTypes = m.getParameterTypes();
            try {
                Object[] args = new Object[argTypes.length];
                for (int arg=0;arg < args.length;arg++) {
                    args[arg] = defaultArg(argTypes[arg]);
                }
                if ("hashCode".equals(m.getName())
                    || "equals".equals(m.getName())
                    || m.getDeclaringClass() == Object.class
                    || (m.getModifiers() & Modifier.STATIC) != 0) {
                    continue;
                }
                Object result = m.invoke(p, args);
                if ("toString".equals(m.getName())) {
                    assertTrue("toString() should indicate const-ness", ((String)result).indexOf("const") != -1);
                    continue;
                }
                fail("Method '" + m.getName() + "(" + Arrays.asList(argTypes) + ")' should throw UnsupportedOperationException");
            }
            catch(InvocationTargetException e) {
                assertEquals("Wrong exception type thrown by '" + m.getName() + "(" + Arrays.asList(argTypes) + ")", UnsupportedOperationException.class, e.getTargetException().getClass());
            }
            catch(IllegalArgumentException e) {
                fail("Need to fix test of method '" + m.getName() + "(" + Arrays.asList(argTypes) + ")'");
            }
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PointerTest.class);
    }
}
