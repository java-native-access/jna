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

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Oleacc;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.sun.jna.platform.win32.WinError.S_OK;
import static com.sun.jna.platform.win32.WinUser.CHILDID_SELF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccessibleUtilTest
{
    private static boolean initialized = false;

    @BeforeClass
    public static void setUp() throws Exception {
        Runtime.getRuntime().exec("calc");
        Thread.sleep(1000);

        // Initialize COM for this thread...
        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized = true;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Runtime.getRuntime().exec("taskkill.exe /f /im calculator.exe");
        Thread.sleep(1000);

        if (initialized) {
            Ole32.INSTANCE.CoUninitialize();
            initialized = false;
        }
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
        String accName = new AccessibleUtil(accessible).get_accName(CHILDID_SELF);
        assertEquals("Calculator", accName);
    }
}
