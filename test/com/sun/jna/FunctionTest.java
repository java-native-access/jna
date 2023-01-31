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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;

/** Exercise the {@link Function} class.
 *
 * @author twall@users.sf.net
 */
//@SuppressWarnings("unused")
public class FunctionTest extends TestCase {

    private NativeLibrary libUTF8;
    private NativeLibrary libLatin1;
    private TestLibUTF8 libUTF8Direct;
    private TestLibLatin1 libLatin1Direct;
    private TestLib libUTF8Interface;
    private TestLib libLatin1Interface;

    @Override
    protected void setUp() {
        libUTF8 = NativeLibrary.getInstance("testlib",
                Collections.singletonMap(Library.OPTION_STRING_ENCODING, "UTF-8"));
        libLatin1 = NativeLibrary.getInstance("testlib",
                Collections.singletonMap(Library.OPTION_STRING_ENCODING, "ISO-8859-1"));
        Native.register(TestLibUTF8.class, libUTF8);
        Native.register(TestLibLatin1.class, libLatin1);
        libUTF8Direct = new TestLibUTF8();
        libLatin1Direct = new TestLibLatin1();
        libUTF8Interface = Native.load("testlib", TestLib.class,
                Collections.singletonMap(Library.OPTION_STRING_ENCODING, "UTF-8"));
        libLatin1Interface = Native.load("testlib", TestLib.class,
                Collections.singletonMap(Library.OPTION_STRING_ENCODING, "ISO-8859-1"));
    }

    public void testTooManyArgs() {
        NativeLibrary lib = NativeLibrary.getInstance(Platform.C_LIBRARY_NAME);
        Function f = lib.getFunction("printf");
        Object[] args = new Object[Function.MAX_NARGS+1];
        // Make sure we don't break 'printf'
        args[0] = getName();
        try {
            f.invokeInt(args);
            fail("Arguments should be limited to " + Function.MAX_NARGS);
        } catch(UnsupportedOperationException e) {
            // expected
        }
        assertEquals("Wrong result from 'printf'", getName().length(), f.invokeInt(new Object[] { getName() }));
    }

    public void testUnsupportedReturnType() {
        NativeLibrary lib = NativeLibrary.getInstance(Platform.C_LIBRARY_NAME);
        Function f = lib.getFunction("printf");
        try {
            f.invoke(getClass(), new Object[] { getName() });
            fail("Invalid return types should throw an exception");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testStringEncodingArgument() throws UnsupportedEncodingException {
        // String with german umlauts
        String input = "Hallo äöüß";
        byte[] result = new byte[32];
        Arrays.fill(result, (byte) 0);
        libUTF8Interface.copyString(input, result);
        Assert.assertArrayEquals(toByteArray(input, "UTF-8", 32), result);
        Arrays.fill(result, (byte) 0);
        libLatin1Interface.copyString(input, result);
        Assert.assertArrayEquals(toByteArray(input, "ISO-8859-1", 32), result);

        // String array with german umlauts
        String[] inputArray = new String[]{"1Hallo äöüß1", "2Hallo äöüß2"};
        result = new byte[64];
        Arrays.fill(result, (byte) 0);
        libUTF8Interface.copyStringArray(inputArray, result);
        Assert.assertArrayEquals(toByteArray(inputArray, "UTF-8", 64), result);
        Arrays.fill(result, (byte) 0);
        libLatin1Interface.copyStringArray(inputArray, result);
        Assert.assertArrayEquals(toByteArray(inputArray, "ISO-8859-1", 64), result);
    }

    public void testStringEncodingArgumentDirect() throws UnsupportedEncodingException {
        // String with german umlauts
        String input = "Hallo äöüß";
        byte[] result = new byte[32];
        Arrays.fill(result, (byte) 0);
        libUTF8Direct.copyString(input, result);
        Assert.assertArrayEquals(toByteArray(input, "UTF-8", 32), result);
        Arrays.fill(result, (byte) 0);
        libLatin1Direct.copyString(input, result);
        Assert.assertArrayEquals(toByteArray(input, "ISO-8859-1", 32), result);
    }

    public void testStringReturn() throws UnsupportedEncodingException {
        // String with german umlauts
        String input = "Hallo äöüß";

        String result;
        Memory mem = new Memory(32);
        mem.clear();
        mem.write(0, input.getBytes("UTF-8"), 0, input.getBytes("UTF-8").length);
        result = libUTF8Interface.returnStringArgument(mem);
        assertEquals(input, result);
        mem.clear();
        mem.write(0, input.getBytes("ISO-8859-1"), 0, input.getBytes("ISO-8859-1").length);
        result = libLatin1Interface.returnStringArgument(mem);
        assertEquals(input, result);
    }

    public void testStringReturnDirect() throws UnsupportedEncodingException {
        // String with german umlauts
        String input = "Hallo äöüß";

        String result;
        Memory mem = new Memory(32);
        mem.clear();
        mem.write(0, input.getBytes("UTF-8"), 0, input.getBytes("UTF-8").length);
        result = libUTF8Direct.returnStringArgument(mem);
        assertEquals(input, result);
        mem.clear();
        mem.write(0, input.getBytes("ISO-8859-1"), 0, input.getBytes("ISO-8859-1").length);
        result = libLatin1Direct.returnStringArgument(mem);
        assertEquals(input, result);
    }

    private byte[] toByteArray(String input, String encoding, int targetLength) throws UnsupportedEncodingException {
        byte[] result = new byte[targetLength];
        byte[] encoded = input.getBytes(encoding);
        System.arraycopy(encoded, 0, result, 0, encoded.length);
        return result;
    }

    private byte[] toByteArray(String[] input, String encoding, int targetLength) throws UnsupportedEncodingException {
        byte[] result = new byte[targetLength];
        int offset = 0;
        for(String currInput: input) {
            byte[] encoded = currInput.getBytes(encoding);
            System.arraycopy(encoded, 0, result, offset, encoded.length);
            offset += encoded.length;
            offset++;
        }
        return result;
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(FunctionTest.class);
    }

    private static class TestLibUTF8 implements Library  {
        native String returnStringArgument(Pointer input);
        native SizeT copyString(String input, byte[] output);
    }

    private static class TestLibLatin1 implements Library {
        native String returnStringArgument(Pointer input);
        native SizeT copyString(String input, byte[] output);
    }

    private interface TestLib extends Library {
        public String returnStringArgument(Pointer input);
        public SizeT copyString(String input, byte[] output);
        public SizeT copyStringArray(String[] input, byte[] output);
    }

    private static class SizeT extends IntegerType {
        public static final int SIZE = Native.SIZE_T_SIZE;

        public SizeT() {
            this(0);
        }

        public SizeT(long value) {
            super(SIZE, value, true);
        }

    }
}
