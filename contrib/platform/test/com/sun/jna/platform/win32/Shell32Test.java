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
import com.sun.jna.platform.win32.WinNT.HANDLE;
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

	public void setup() {
		
		APPBARDATA ABData = new APPBARDATA();
		
		
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
    
    private boolean AppBar_Register() {
		DWORD dwABM = new DWORD();
	    
		APPBARDATA ABData = new APPBARDATA.ByReference();
        ABData.uCallbackMessage.setValue( WM_USER + 1);
        ABData.cbSize.setValue( ABData.size() );
    	dwABM.setValue(ShellAPI.ABM_NEW);

        return (null!=Shell32.INSTANCE.SHAppBarMessage( dwABM, ABData));
    }

    private boolean AppBar_Unregister() {
		DWORD dwABM = new DWORD();
    	APPBARDATA ABData = new APPBARDATA.ByReference();
    	ABData.cbSize.setValue( ABData.size() );
    	dwABM.setValue(ShellAPI.ABM_REMOVE);
        return (null!=Shell32.INSTANCE.SHAppBarMessage(dwABM, ABData));
    }

    public void queryPos( APPBARDATA ABData ) {
		DWORD dwABM = new DWORD();
		
		dwABM.setValue(ShellAPI.ABM_QUERYPOS);
		HANDLE h = Shell32.INSTANCE.SHAppBarMessage( dwABM, ABData );

		assertNotNull(h);
			
		System.out.printf( "ABData.rc[%d,%d,%d,%d]\n", 
								ABData.rc.top, 
								ABData.rc.left, 
								ABData.rc.bottom, 
								ABData.rc.right);
    	
    }
    
	public void testResizeDesktopFromBottom() throws InterruptedException {

		DWORD dwABM = new DWORD();
		
		assertTrue( AppBar_Register() );
		
		APPBARDATA ABData = new APPBARDATA.ByReference(); 
		System.out.printf( "APPBARDATA sizeof [%d]\n", ABData.size());

		ABData.uEdge.setValue(ShellAPI.ABE_BOTTOM);
		ABData.rc.top		= User32.INSTANCE.GetSystemMetrics(User32.SM_CYFULLSCREEN) - RESIZE_HEIGHT;
		ABData.rc.left		= 0;
		ABData.rc.bottom	= User32.INSTANCE.GetSystemMetrics(User32.SM_CYFULLSCREEN);
		ABData.rc.right		= User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN);

		queryPos(ABData);

		dwABM.setValue(ShellAPI.ABM_SETPOS);
		HANDLE h = Shell32.INSTANCE.SHAppBarMessage( dwABM, ABData );

		assertNotNull(h);
		
		
		Thread.sleep( 5 * 1000 );
		assertTrue( AppBar_Unregister() );
		
	}
	
	public void testResizeDesktopFromTop() throws InterruptedException {

		DWORD dwABM = new DWORD();
		
		assertTrue( AppBar_Register() );
		
		APPBARDATA ABData = new APPBARDATA.ByReference();
		ABData.uEdge.setValue(ShellAPI.ABE_TOP);
		ABData.rc.top		= 0;
		ABData.rc.left		= 0;
		ABData.rc.bottom	= RESIZE_HEIGHT;
		ABData.rc.right		= User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN);

		queryPos(ABData);
		
		dwABM.setValue(ShellAPI.ABM_SETPOS);
		HANDLE h = Shell32.INSTANCE.SHAppBarMessage( dwABM, ABData );

		assertNotNull(h);
		
		Thread.sleep( 5 * 1000 );
		assertTrue( AppBar_Unregister() );
		
	}

}
