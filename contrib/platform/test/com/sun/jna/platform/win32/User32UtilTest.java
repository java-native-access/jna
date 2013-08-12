/*
 * Copyright (c) 2013 Markus Karg, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;

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
}
