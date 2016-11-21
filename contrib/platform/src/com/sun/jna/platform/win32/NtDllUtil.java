/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
