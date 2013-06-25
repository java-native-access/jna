/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.ShellAPI.APPBARDATA;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.ptr.PointerByReference;


/**
 * @author dblock[at]dblock[dot]org
 * @author markus[at]headcrashing[dot]eu
 */
public class Shell32Test extends TestCase {

    private static final int RESIZE_HEIGHT = 500;
    private static final int WM_USER = 0x0400;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Shell32Test.class);
    }

    public void testSHGetFolderPath() {
    	char[] pszPath = new char[WinDef.MAX_PATH];
    	assertEquals(W32Errors.S_OK, Shell32.INSTANCE.SHGetFolderPath(null, 
    			ShlObj.CSIDL_PROGRAM_FILES, null, ShlObj.SHGFP_TYPE_CURRENT, 
    			pszPath));
    	assertTrue(Native.toString(pszPath).length() > 0);
    }

    public void testSHGetDesktopFolder() {
        PointerByReference ppshf = new PointerByReference();
        WinNT.HRESULT hr = Shell32.INSTANCE.SHGetDesktopFolder(ppshf);
        assertTrue(W32Errors.SUCCEEDED(hr.intValue()));
        assertTrue(ppshf.getValue() != null);
        // should release the interface, but we need Com4JNA to do that.
    }

    public final void testSHGetSpecialFolderPath() {
        final char[] pszPath = new char[WinDef.MAX_PATH];
        assertTrue(Shell32.INSTANCE.SHGetSpecialFolderPath(null, pszPath, ShlObj.CSIDL_APPDATA, false));
        assertFalse(Native.toString(pszPath).isEmpty());
    }

    
    private void newAppBar() {
        APPBARDATA data = new APPBARDATA.ByReference();
        data.cbSize.setValue(data.size());
        data.uCallbackMessage.setValue(WM_USER + 1);

        UINT_PTR result = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_NEW), data);
        assertNotNull(result);
    }

    private void removeAppBar() {
        APPBARDATA data = new APPBARDATA.ByReference();
        data.cbSize.setValue(data.size());
        UINT_PTR result = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_REMOVE), data);
        assertNotNull(result);

    }

    private void queryPos(APPBARDATA data) {
        UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_QUERYPOS), data);

        assertNotNull(h);
        assertTrue(h.intValue() > 0);

    }

    public void testResizeDesktopFromBottom() throws InterruptedException {

        newAppBar();

        APPBARDATA data = new APPBARDATA.ByReference();

        data.uEdge.setValue(ShellAPI.ABE_BOTTOM);
        data.rc.top = User32.INSTANCE.GetSystemMetrics(User32.SM_CYFULLSCREEN) - RESIZE_HEIGHT;
        data.rc.left = 0;
        data.rc.bottom = User32.INSTANCE.GetSystemMetrics(User32.SM_CYFULLSCREEN);
        data.rc.right = User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN);

        queryPos(data);

        UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_SETPOS), data);

        assertNotNull(h);
        assertTrue(h.intValue() >= 0);

        removeAppBar();
    }

    public void testResizeDesktopFromTop() throws InterruptedException {
        
        newAppBar();

        APPBARDATA data = new APPBARDATA.ByReference();
        data.uEdge.setValue(ShellAPI.ABE_TOP);
        data.rc.top = 0;
        data.rc.left = 0;
        data.rc.bottom = RESIZE_HEIGHT;
        data.rc.right = User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN);

        queryPos(data);

        UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_SETPOS), data);

        assertNotNull(h);
        assertTrue(h.intValue() >= 0);

        removeAppBar();

    }

}
