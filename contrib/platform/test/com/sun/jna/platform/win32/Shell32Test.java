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
		DWORD dwABM = new DWORD();
	    
		APPBARDATA ABData = new APPBARDATA.ByReference();
        ABData.uCallbackMessage.setValue( WM_USER + 1);
        ABData.cbSize.setValue( ABData.size() );
    	dwABM.setValue(ShellAPI.ABM_NEW);

    	UINT_PTR result = Shell32.INSTANCE.SHAppBarMessage( dwABM, ABData);
        assertNotNull(result );
    }

    private void removeAppBar()  {
    	
		DWORD dwABM = new DWORD();
    	APPBARDATA ABData = new APPBARDATA.ByReference();
    	ABData.cbSize.setValue( ABData.size() );
    	dwABM.setValue(ShellAPI.ABM_REMOVE);
    	UINT_PTR result = Shell32.INSTANCE.SHAppBarMessage( dwABM, ABData);
        assertNotNull(result );

    }

    private void queryPos( APPBARDATA ABData ) {
		DWORD dwABM = new DWORD();
		
		dwABM.setValue(ShellAPI.ABM_QUERYPOS);
		UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage( dwABM, ABData );

		assertNotNull(h);
		assertTrue(h.intValue()>0);
			
    }
    
	public void testResizeDesktopFromBottom() throws InterruptedException {

		newAppBar();
		
		DWORD dwABM = new DWORD();
		
		
		APPBARDATA data = new APPBARDATA.ByReference(); 

		data.uEdge.setValue(ShellAPI.ABE_BOTTOM);
		data.rc.top		= User32.INSTANCE.GetSystemMetrics(User32.SM_CYFULLSCREEN) - RESIZE_HEIGHT;
		data.rc.left		= 0;
		data.rc.bottom	= User32.INSTANCE.GetSystemMetrics(User32.SM_CYFULLSCREEN);
		data.rc.right		= User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN);

		queryPos(data);

		dwABM.setValue(ShellAPI.ABM_SETPOS);
		UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage( dwABM, data );

		assertNotNull(h);
		assertTrue(h.intValue()>=0);
	
		removeAppBar();		
	}
	
	public void testResizeDesktopFromTop() throws InterruptedException {
		newAppBar();

		DWORD dwABM = new DWORD();
		
		APPBARDATA data = new APPBARDATA.ByReference();
		data.uEdge.setValue(ShellAPI.ABE_TOP);
		data.rc.top	= 0;
		data.rc.left = 0;
		data.rc.bottom	= RESIZE_HEIGHT;
		data.rc.right		= User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN);

		queryPos(data);
		
		dwABM.setValue(ShellAPI.ABM_SETPOS);
		UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage( dwABM, data );

		assertNotNull(h);
		assertTrue(h.intValue()>=0);
		
		removeAppBar();		
		
	}

}
