/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * ntdll.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface NtDll extends StdCallLibrary {
	
	NtDll INSTANCE = (NtDll) Native.loadLibrary("NtDll",
			NtDll.class, W32APIOptions.UNICODE_OPTIONS);

	/**
	 * The ZwQueryKey routine provides information about the class of a registry key, 
	 * and the number and sizes of its subkeys.
	 * @param KeyHandle
	 *  Handle to the registry key to obtain information about. This handle is created by 
	 *  a successful call to ZwCreateKey or ZwOpenKey. 
	 * @param KeyInformationClass
	 *  Specifies a KEY_INFORMATION_CLASS value that determines the type of information 
	 *  returned in the KeyInformation buffer. 
	 * @param KeyInformation
	 *  Pointer to a caller-allocated buffer that receives the requested information.
	 * @param Length
	 *  Specifies the size, in bytes, of the KeyInformation buffer. 
	 * @param ResultLength
	 *  Pointer to a variable that receives the size, in bytes, of the requested key 
	 *  information. If ZwQueryKey returns STATUS_SUCCESS, the variable contains the amount 
	 *  of data returned. If ZwQueryKey returns STATUS_BUFFER_OVERFLOW or 
	 *  STATUS_BUFFER_TOO_SMALL, you can use the value of the variable to determine the 
	 *  required buffer size. 
	 * @return
	 *  ZwQueryKey returns STATUS_SUCCESS on success, or the appropriate error code on failure.
	 */
	public int ZwQueryKey(HANDLE KeyHandle, int KeyInformationClass,
			Structure KeyInformation, int Length, IntByReference ResultLength);
}