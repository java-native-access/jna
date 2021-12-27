/* Copyright (c) 2021 Mo Beigi, All Rights Reserved
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
import com.sun.jna.platform.win32.COM.Accessible;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.IAccessible;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WTypes.LPWSTR;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.sun.jna.platform.win32.WinError.S_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OleaccTest
{
    @BeforeClass
    public static void setUp() throws Exception {
        Runtime.getRuntime().exec("calc");
        Thread.sleep(1000);

        // Initialize COM for this thread...
        WinNT.HRESULT hr = Ole32.INSTANCE.CoInitialize(null);

        if (W32Errors.FAILED(hr)) {
            tearDown();
            throw new COMException("CoInitialize() failed");
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Runtime.getRuntime().exec("taskkill.exe /f /im calculator.exe");
        Thread.sleep(1000);
        Ole32.INSTANCE.CoUninitialize();
    }

    private static HWND getCalculatorHwnd() {
        HWND hwnd = User32.INSTANCE.FindWindow(null, "Calculator");
        assertNotNull(hwnd);
        return hwnd;
    }

    private static Accessible getCalculatorAccessible() {
        HWND hwnd = getCalculatorHwnd();
        REFIID riid = new REFIID(IAccessible.IID_IACCESSIBLE);
        PointerByReference pointer = new PointerByReference();
        HRESULT hresult = Oleacc.INSTANCE.AccessibleObjectFromWindow(hwnd, 0, riid, pointer);
        assertEquals(S_OK, hresult);
        return new Accessible(pointer.getPointer().getPointer(0L));
    }

    @Test
    public void testAccessibleChildren() {
        // Get accessible from hwnd first
        Accessible accessible = getCalculatorAccessible();

        // Call AccessibleChildren
        IntByReference cChildren = new IntByReference();
        HRESULT hresult1 = accessible.get_accChildCount(cChildren);
        assertEquals(S_OK, hresult1);

        Variant.VARIANT[] rgvarChildren = new Variant.VARIANT[(int) cChildren.getValue()];
        IntByReference pcObtained = new IntByReference();

        HRESULT hresult2 = Oleacc.INSTANCE.AccessibleChildren(
                accessible.getPointer(),
                0,
                cChildren.getValue(),
                rgvarChildren,
                pcObtained
        );
        assertEquals(S_OK, hresult2);
        assertEquals(7L, pcObtained.getValue());

        for (int i = 0; i < pcObtained.getValue(); ++i) {
            assertEquals(Variant.VT_DISPATCH, rgvarChildren[i].getVarType().intValue());
        }
    }

    @Test
    public void testAccessibleObjectFromWindow() {
        HWND hwnd = getCalculatorHwnd();
        REFIID riid = new REFIID(IAccessible.IID_IACCESSIBLE);
        PointerByReference pointer = new PointerByReference();
        HRESULT hresult = Oleacc.INSTANCE.AccessibleObjectFromWindow(hwnd, 0, riid, pointer);
        assertEquals(S_OK, hresult);
    }

    @Test
    public void testWindowFromAccessibleObject() {
        // Get accessible from hwnd first
        HWND originalHwnd = getCalculatorHwnd();
        Accessible accessible = getCalculatorAccessible();

        // Then attempt to get same hwnd from accessible
        PointerByReference phwnd = new PointerByReference();
        HRESULT hresult2 = Oleacc.INSTANCE.WindowFromAccessibleObject(accessible.getPointer(), phwnd);
        assertEquals(S_OK, hresult2);
        assertEquals(originalHwnd, new HWND(phwnd.getPointer().getPointer(0L)));
    }

    @Test
    public void testGetRoleTextA() {
        int result = Oleacc.INSTANCE.GetRoleTextA(Oleacc.ROLE_SYSTEM_TITLEBAR, null, 0);
        assertEquals(9, result);
        LPSTR lptstr = new LPSTR(new Memory(result + 1)); // plus 1 for null terminator
        int result2 = Oleacc.INSTANCE.GetRoleTextA(Oleacc.ROLE_SYSTEM_TITLEBAR, lptstr, result + 1);
        assertEquals(result, result2);
        assertEquals("title bar", lptstr.toString());
    }

    @Test
    public void testGetRoleTextW() {
        int result = Oleacc.INSTANCE.GetRoleTextW(Oleacc.ROLE_SYSTEM_TITLEBAR, null, 0);
        assertEquals(9, result);
        LPWSTR lpwstr = new LPWSTR(new Memory(result + 1)); // plus 1 for null terminator
        int result2 = Oleacc.INSTANCE.GetRoleTextW(Oleacc.ROLE_SYSTEM_TITLEBAR, lpwstr, result + 1);
        assertEquals(result, result2);
        assertEquals("title bar", lpwstr.toString());
    }
}
