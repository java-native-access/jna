/* Copyright (c) 2024 Matthias Bläsing, All Rights Reserved
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
import junit.framework.TestCase;

/**
 * Check getNativeLibrary functions in Native
 */
public class NativeGetNativeLibraryTest extends TestCase {

    private NativeLibrary libUTF8;
    private TestLib libUTF8Interface;

    @Override
    protected void setUp() {
        libUTF8 = NativeLibrary.getInstance("testlib",
                Collections.singletonMap(Library.OPTION_STRING_ENCODING, "UTF-8"));
        Native.register(TestLibUTF8.class, libUTF8);
        libUTF8Interface = Native.load("testlib", TestLib.class,
                Collections.singletonMap(Library.OPTION_STRING_ENCODING, "UTF-8"));
    }

    public void testGetNativeLibraryInterface() {
        NativeLibrary nl = Native.getNativeLibrary(libUTF8Interface);
        assertTrue(nl instanceof NativeLibrary);
    }

    public void testGetNativeLibraryDirect() {
        NativeLibrary nl = Native.getNativeLibrary(TestLibUTF8.class);
        assertTrue(nl instanceof NativeLibrary);
        // This only makes sense for the direct case, as that directly wraps
        // a supplied instance
        assertEquals(libUTF8, nl);
    }

    public void testGetNativeLibraryOnUnboundShouldFail() {
        try {
            Native.getNativeLibrary(new TestLib() {
                @Override
                public String returnStringArgument(Pointer input) {
                    return "";
                }
            });
            assertTrue("Exception not thrown", false);
        } catch (IllegalArgumentException ex) {
            // This should be reached
        }
    }

    public void testGetNativeLibraryOnNullShouldFail() {
        try {
            Native.getNativeLibrary((Class) null);
            assertTrue("Exception not thrown", false);
        } catch (IllegalArgumentException ex) {
            // This should be reached
        }
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(NativeGetNativeLibraryTest.class);
    }

    private static class TestLibUTF8 implements Library  {
        native String returnStringArgument(Pointer input);
    }

    private interface TestLib extends Library {
        public String returnStringArgument(Pointer input);
    }

}
