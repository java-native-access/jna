/* Copyright (c) 2007-2014 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WTypes.BSTR;
import junit.framework.TestCase;

public class WTypesTest extends TestCase {

    private static final String TEST_STRING = "input";

    private static final Pointer TEST_POINTER_WCHAR = new Memory((TEST_STRING.length() + 1L) * Native.WCHAR_SIZE);

    private static final Pointer TEST_POINTER_CHAR = new Memory(TEST_STRING.length() + 1L);

    static {
        TEST_POINTER_WCHAR.setWideString(0, TEST_STRING);
        TEST_POINTER_CHAR.setString(0, TEST_STRING);
    }

    public void testLPOLESTRConstruction() {
        WTypes.LPOLESTR fromString = new WTypes.LPOLESTR(TEST_STRING);
        assertEquals(fromString.getValue(), TEST_STRING);
        WTypes.LPOLESTR empty = new WTypes.LPOLESTR();
        assertNull(empty.getValue());
        WTypes.LPOLESTR fromPointer = new WTypes.LPOLESTR(TEST_POINTER_WCHAR);
        assertEquals(fromPointer.getValue(), TEST_STRING);
    }

    public void testLPSTRConstruction() {
        WTypes.LPSTR instance = new WTypes.LPSTR(TEST_STRING);
        assertEquals(instance.getValue(), TEST_STRING);
        WTypes.LPSTR empty = new WTypes.LPSTR();
        assertNull(empty.getValue());
        WTypes.LPSTR fromPointer = new WTypes.LPSTR(TEST_POINTER_CHAR);
        assertEquals(fromPointer.getValue(), TEST_STRING);
    }

    public void testLPWSTRConstruction() {
        WTypes.LPWSTR instance = new WTypes.LPWSTR(TEST_STRING);
        assertEquals(instance.getValue(), TEST_STRING);
        WTypes.LPWSTR empty = new WTypes.LPWSTR();
        assertNull(empty.getValue());
        WTypes.LPWSTR fromPointer = new WTypes.LPWSTR(TEST_POINTER_WCHAR);
        assertEquals(fromPointer.getValue(), TEST_STRING);
    }

    @SuppressWarnings("deprecation")
    public void testBSTRBasic() {
        String demoString = "input\u00D6\u00E4\u00DC?!";
        // Allocation via system and the "correct" way
        BSTR sysAllocated = OleAuto.INSTANCE.SysAllocString(demoString);
        // Java based allocation - not suitable if passed via automation
        BSTR javaAllocated = new BSTR(demoString);

        // Ensure encoding roundtripping works
        assertEquals(demoString, sysAllocated.getValue());
        assertEquals(demoString, javaAllocated.getValue());

        // BSTR is encoded as UTF-16/UCS2, so byte length is 2 * char count
        assertEquals(demoString.length(), OleAuto.INSTANCE.SysStringLen(sysAllocated));
        assertEquals(demoString.length(), OleAuto.INSTANCE.SysStringLen(javaAllocated));
        assertEquals(2 * demoString.length(), OleAuto.INSTANCE.SysStringByteLen(sysAllocated));
        assertEquals(2 * demoString.length(), OleAuto.INSTANCE.SysStringByteLen(javaAllocated));

        // The BSTR Pointer points 4 bytes into the data itself (beginning of data
        // string, the 4 preceding bytes code the string length (in bytes)
        assertEquals(2 * demoString.length(), sysAllocated.getPointer().getInt(-4));
        assertEquals(2 * demoString.length(), javaAllocated.getPointer().getInt(-4));

        OleAuto.INSTANCE.SysFreeString(sysAllocated);
        // javaAllocated is allocated via Memory and will be freeed by the
        // garbadge collector automaticly
    }

    public void testBSTRNullPointerHandling() {
        // Allocation from NULL should return NULL
        BSTR sysAllocated = OleAuto.INSTANCE.SysAllocString(null);
        assertNull(sysAllocated);

        // MSDN states, that the BSTR from Nullpointer represents the string with
        // zero characters
        BSTR bstr = new BSTR(Pointer.NULL);
        assertEquals("", bstr.getValue());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(WTypesTest.class);
    }
}
