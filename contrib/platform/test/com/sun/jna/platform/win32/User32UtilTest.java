/* Copyright (c) 2013 Markus Karg, All Rights Reserved
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

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.EnumSet;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser.RAWINPUTDEVICELIST;

import junit.framework.TestCase;

/**
 * @author markus[at]headcrashing[dot]eu
 */
public final class User32UtilTest extends TestCase {
    public static final void main(final String[] args) {
        junit.textui.TestRunner.run(User32UtilTest.class);
    }

    public final void testRegisterWindowMessage() {
        final int msg = User32Util.registerWindowMessage("RM_UNITTEST");
        assertTrue(msg >= 0xC000 && msg <= 0xFFFF);
    }

    public final void testCreateWindow() {
        final HWND hWnd = User32Util.createWindow("Message", null, 0, 0, 0, 0, 0, null, null, null, null);
        try {
            assertTrue(Pointer.nativeValue(hWnd.getPointer()) > 0);
        } finally {
            User32Util.destroyWindow(hWnd);
        }
    }

    public final void testCreateWindowEx() {
        final HWND hWnd = User32Util.createWindowEx(0, "Message", null, 0, 0, 0, 0, 0, null, null, null, null);
        try {
            assertTrue(Pointer.nativeValue(hWnd.getPointer()) > 0);
        } finally {
            User32Util.destroyWindow(hWnd);
        }
    }

    public final void testDestroyWindow() {
        User32Util.destroyWindow(User32Util.createWindow("Message", null, 0, 0, 0, 0, 0, null, null, null, null));
    }

    public final void testGetRawInputDeviceList() {
        Collection<RAWINPUTDEVICELIST> deviceList = User32Util.GetRawInputDeviceList();
        /*
         * NOTE: we do do not check that deviceList.size() > 0 since theoretically
         * we could run on a host that has no input devices (keyboard, mouse, etc...).
         * We just want to make sure that the call succeeds
         */
        assertNotNull("No device list", deviceList);

//        for (RAWINPUTDEVICELIST device : deviceList) {
//            System.out.append('\t').append("Found device of type: ").println(device.dwType);
//        }
    }

    /**
     * Assert we can load a String from the string table of an executable.
     *
     * @throws UnsupportedEncodingException should never happen
     */
    public void testLoadString() throws UnsupportedEncodingException {
        String value = User32Util.loadString("%SystemRoot%\\system32\\input.dll,-5011");
        if(AbstractWin32TestSupport.isEnglishLocale) {
            assertEquals("German", value);
        } else {
            assertNotNull(value);
            assertFalse(value.isEmpty());
        }
    }

    /**
     * Assert some well known VK are members or not members of
     * {@link com.sun.jna.platform.win32.User32Util#WIN32VK_MAPPABLE}
     *
     */
    public void testVkMappable() {
        assertTrue(User32Util.WIN32VK_MAPPABLE.contains(Win32VK.VK_A));
        assertFalse(EnumSet.complementOf(User32Util.WIN32VK_MAPPABLE).contains(Win32VK.VK_A));
        assertTrue(EnumSet.complementOf(User32Util.WIN32VK_MAPPABLE).contains(Win32VK.VK_SHIFT));
    }
}
