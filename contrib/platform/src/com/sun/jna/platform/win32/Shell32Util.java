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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HRESULT;

/**
 * Shell32 Utility API.
 * @author dblock[at]dblock.org
 * @author markus[at]headcrashing[dot]eu
 */
public abstract class Shell32Util {
	
	/**
	 * Get a special folder path.
	 * @param hwnd
	 *  Parent window.
	 * @param nFolder
	 *  Folder CSLID.
	 * @param dwFlags
	 *  Flags.
	 * @return
	 *  Special folder.
	 */
	public static String getFolderPath(HWND hwnd, int nFolder, DWORD dwFlags) {
    	char[] pszPath = new char[WinDef.MAX_PATH];
    	HRESULT hr = Shell32.INSTANCE.SHGetFolderPath(hwnd, 
    			nFolder, null, dwFlags, 
    			pszPath);
    	if (! hr.equals(W32Errors.S_OK)) {
    		throw new Win32Exception(hr);
    	}
    	return Native.toString(pszPath);
	}
	
	/**
	 * Get a special folder path.
	 * @param nFolder
	 *  Folder CSLID.
	 * @return
	 *  Special folder path.
	 */
	public static String getFolderPath(int nFolder) {
		return getFolderPath(null, nFolder, ShlObj.SHGFP_TYPE_CURRENT);
	}

	/**
     * Retrieves the path of a special folder, identified by its CSIDL.
     *
     * @param csidl
     *            A CSIDL that identifies the folder of interest. If a virtual folder is specified, this function will fail.
     * @param create
     *            Indicates whether the folder should be created if it does not already exist. If this value is nonzero, the folder is created. If this value is
     *            zero, the folder is not created.
     * @return The drive and path of the specified folder
     */
    public static final String getSpecialFolderPath(final int csidl, final boolean create) {
        final char[] pszPath = new char[WinDef.MAX_PATH];
        if (!Shell32.INSTANCE.SHGetSpecialFolderPath(null, pszPath, csidl, create))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        return Native.toString(pszPath);
    }
}