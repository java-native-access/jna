/* Copyright (c) 2007-2014 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import junit.framework.TestCase;

public class WTypesTest extends TestCase {

    private static final String TEST_STRING = "input";

    private static final Pointer TEST_POINTER = new Memory((TEST_STRING.length() + 1L) * Native.WCHAR_SIZE);

    static {
        TEST_POINTER.setWideString(0, TEST_STRING);
    }

    public void testLPOLESTRConstruction() {
        WTypes.LPOLESTR fromString = new WTypes.LPOLESTR(TEST_STRING);
        assertEquals(fromString.getValue(), TEST_STRING);
        WTypes.LPOLESTR empty = new WTypes.LPOLESTR();
        assertNull(empty.getValue());
        WTypes.LPOLESTR fromPointer = new WTypes.LPOLESTR(TEST_POINTER);
        assertEquals(fromPointer.getValue(), TEST_STRING);
    }

    public void testLPSTRConstruction() {
        WTypes.LPSTR instance = new WTypes.LPSTR(TEST_STRING);
        assertEquals(instance.getValue(), TEST_STRING);
        WTypes.LPSTR empty = new WTypes.LPSTR();
        assertNull(empty.getValue());
        WTypes.LPSTR fromPointer = new WTypes.LPSTR(TEST_POINTER);
        assertEquals(fromPointer.getValue(), TEST_STRING);
    }

    public void testLPWSTRConstruction() {
        WTypes.LPWSTR instance = new WTypes.LPWSTR(TEST_STRING);
        assertEquals(instance.getValue(), TEST_STRING);
        WTypes.LPWSTR empty = new WTypes.LPWSTR();
        assertNull(empty.getValue());
        WTypes.LPWSTR fromPointer = new WTypes.LPWSTR(TEST_POINTER);
        assertEquals(fromPointer.getValue(), TEST_STRING);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(WTypesTest.class);
    }
}
