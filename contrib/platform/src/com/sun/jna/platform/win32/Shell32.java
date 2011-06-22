/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.ShellAPI.SHFILEOPSTRUCT;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/** 
 * Shell32.dll Interface.
 */
public interface Shell32 extends StdCallLibrary {
	
    Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32", Shell32.class, 
    		W32APIOptions.UNICODE_OPTIONS);
    
    /**
     * This function can be used to copy, move, rename, or delete a file system object.
     * @param fileop
     *  Address of an SHFILEOPSTRUCT structure that contains information this function 
     *  needs to carry out the specified operation. 
     * @return
     *  Returns zero if successful, or nonzero otherwise.
     */
    int SHFileOperation(SHFILEOPSTRUCT fileop);

    /**
     * Takes the CSIDL of a folder and returns the path.
     * @param hwndOwner
     *  Handle to an owner window. This parameter is typically set to NULL. If it is not NULL, 
     *  and a dial-up connection needs to be made to access the folder, a user interface (UI) 
     *  prompt will appear in this window. 
     * @param nFolder
     *  A CSIDL value that identifies the folder whose path is to be retrieved. Only real 
     *  folders are valid. If a virtual folder is specified, this function will fail. You can 
     *  force creation of a folder with SHGetFolderPath by combining the folder's CSIDL with 
     *  CSIDL_FLAG_CREATE. 
     * @param hToken
     *  An access token that can be used to represent a particular user. 
     * @param dwFlags
     *   Flags to specify which path is to be returned.
     * @param pszPath
     *  Pointer to a null-terminated string of length MAX_PATH which will receive the path. 
     *  If an error occurs or S_FALSE is returned, this string will be empty. 
     * @return
     *  Returns standard HRESULT codes.
     */
    HRESULT SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken, DWORD dwFlags, 
    		char[] pszPath);

    /**
     * Retrieves the IShellFolder interface for the desktop folder, which is the root of the Shell's namespace.
     * The retrieved COM interface pointer can be used via Com4JNA's ComObject.wrapNativeInterface call
     * given a suitable interface definition for IShellFolder
     * @param ppshf A place to put the IShellFolder interface pointer
     * @return If the function succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     */
    HRESULT SHGetDesktopFolder( PointerByReference ppshf );

}
