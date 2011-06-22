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

import com.sun.jna.platform.win32.Wdm.KEY_BASIC_INFORMATION;
import com.sun.jna.platform.win32.Wdm.KEY_INFORMATION_CLASS;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.ptr.IntByReference;

/**
 * NtDll Utility API.
 * @author dblock[at]dblock.org
 */
public abstract class NtDllUtil {
	
	/**
	 * Retrieve the name of an opened registry key.
	 * @param hkey Opened registry key.
	 * @return Basic key name, not including node information.
	 */
	public static String getKeyName(HKEY hkey) {
    	IntByReference resultLength = new IntByReference();    	
    	int rc = NtDll.INSTANCE.ZwQueryKey(hkey, KEY_INFORMATION_CLASS.KeyBasicInformation, 
    			null, 0, resultLength);
    	if (rc != NTStatus.STATUS_BUFFER_TOO_SMALL || resultLength.getValue() <= 0) {
    		throw new Win32Exception(rc);
    	}    	
    	KEY_BASIC_INFORMATION keyInformation = new KEY_BASIC_INFORMATION(
    			resultLength.getValue());
    	rc = NtDll.INSTANCE.ZwQueryKey(hkey, KEY_INFORMATION_CLASS.KeyBasicInformation,
    			keyInformation, resultLength.getValue(), resultLength);
    	if (rc != NTStatus.STATUS_SUCCESS) {
    		throw new Win32Exception(rc);
    	}    	
    	return keyInformation.getName();
	}
}
