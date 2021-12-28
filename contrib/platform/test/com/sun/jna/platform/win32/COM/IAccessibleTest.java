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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Oleacc;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.sun.jna.platform.win32.Oleacc.ROLE_SYSTEM_WINDOW;
import static com.sun.jna.platform.win32.WinError.S_FALSE;
import static com.sun.jna.platform.win32.WinError.S_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IAccessibleTest
{

    @BeforeClass
    public static void setUp() throws Exception {
        Runtime.getRuntime().exec("calc");
        Thread.sleep(1000);

        // Initialize COM for this thread...
        HRESULT hr = Ole32.INSTANCE.CoInitialize(null);

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
    public void test_get_accName() {
        Accessible accessible = getCalculatorAccessible();

        Variant.VARIANT varChild = new Variant.VARIANT.ByValue();
        varChild.setValue(Variant.VT_I4, new LONG(0L));
        BSTRByReference bstr = new BSTRByReference();

        HRESULT hresult = accessible.get_accName(varChild, bstr);
        assertEquals(S_OK, hresult);
        assertEquals("Calculator", bstr.getValue().getValue());
    }

    @Test
    public void test_get_accValue() {
        Accessible accessible = getCalculatorAccessible();

        Variant.VARIANT varChild = new Variant.VARIANT.ByValue();
        varChild.setValue(Variant.VT_I4, new LONG(0L));
        BSTRByReference bstr = new BSTRByReference();

        HRESULT hresult = accessible.get_accValue(varChild, bstr);
        assertEquals(S_FALSE, hresult); // This object does not have a value
    }

    @Test
    public void test_get_accRole() {
        Accessible accessible = getCalculatorAccessible();

        Variant.VARIANT varChild = new Variant.VARIANT.ByValue();
        varChild.setValue(Variant.VT_I4, new LONG(0L));
        Variant.VARIANT.ByReference variantByReference = new Variant.VARIANT.ByReference();

        HRESULT hresult = accessible.get_accRole(varChild, variantByReference);
        assertEquals(S_OK, hresult);
        assertEquals(ROLE_SYSTEM_WINDOW, variantByReference.intValue());
    }

    @Test
    public void test_get_accChildCount() {
        Accessible accessible = getCalculatorAccessible();

        IntByReference intByReference = new IntByReference();
        HRESULT hresult = accessible.get_accChildCount(intByReference);
        assertEquals(S_OK, hresult);
        assertEquals(7, intByReference.getValue());
    }

    @Test
    public void test_get_accDefaultAction() {
        Accessible accessible = getCalculatorAccessible();

        Variant.VARIANT varChild = new Variant.VARIANT.ByValue();
        varChild.setValue(Variant.VT_I4, new LONG(0L));
        BSTRByReference bstr = new BSTRByReference();

        HRESULT hresult = accessible.get_accDefaultAction(varChild, bstr);
        assertEquals(S_FALSE, hresult); // No default action for root object
    }

    @Test
    public void test_accDoDefaultAction() {
        Accessible accessible = getCalculatorAccessible();

        Variant.VARIANT varChild = new Variant.VARIANT.ByValue();
        varChild.setValue(Variant.VT_I4, new LONG(0L));

        HRESULT hresult = accessible.accDoDefaultAction(varChild);
        assertEquals(S_FALSE, hresult); // No default action to do for root object
    }
}
