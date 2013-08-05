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

import com.sun.jna.platform.win32.WinCrypt.CRYPTPROTECT_PROMPTSTRUCT;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.ptr.PointerByReference;

/**
 * Crypt32 utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Crypt32Util {

	/**
	 * Protect a blob of data.
	 * @param data
	 *  Data to protect.
	 * @return
	 *  Protected data.
	 */
	public static byte[] cryptProtectData(byte[] data) {
		return cryptProtectData(data, 0);
	}

	/**
	 * Protect a blob of data with optional flags.
	 * @param data
	 *  Data to protect.
	 * @param flags
	 *  Optional flags, eg. CRYPTPROTECT_LOCAL_MACHINE | CRYPTPROTECT_UI_FORBIDDEN.
	 * @return
	 *  Protected data.
	 */
	public static byte[] cryptProtectData(byte[] data, int flags) {
		return cryptProtectData(data, null, flags, "", null);
	}
	
	/**
	 * Protect a blob of data.
	 * @param data
	 *  Data to protect.
	 * @param entropy
	 *  Optional entropy.
	 * @param flags
	 *  Optional flags.
	 * @param description
	 *  Optional description.
	 * @param prompt
	 *  Prompt structure.
	 * @return
	 *  Protected bytes.
	 */
	public static byte[] cryptProtectData(byte[] data, byte[] entropy, int flags, 
			String description, CRYPTPROTECT_PROMPTSTRUCT prompt) {
    	DATA_BLOB pDataIn = new DATA_BLOB(data);
    	DATA_BLOB pDataProtected = new DATA_BLOB();
    	DATA_BLOB pEntropy = (entropy == null) ? null : new DATA_BLOB(entropy);
    	try {
	    	if (! Crypt32.INSTANCE.CryptProtectData(pDataIn, description, 
	    			pEntropy, null, prompt, flags, pDataProtected)) {
	    		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
	    	}
	    	return pDataProtected.getData();
    	} finally {
    		if (pDataProtected.pbData != null) {
    			Kernel32.INSTANCE.LocalFree(pDataProtected.pbData);
    		}
    	}
	}
	
	/**
	 * Unprotect a blob of data.
	 * @param data
	 *  Data to unprotect.
	 * @return
	 *  Unprotected blob of data.
	 */
	public static byte[] cryptUnprotectData(byte[] data) {
		return cryptUnprotectData(data, 0);
	}

	/**
	 * Unprotect a blob of data.
	 * @param data
	 *  Data to unprotect.
	 * @param flags
	 *  Optional flags, eg. CRYPTPROTECT_UI_FORBIDDEN.
	 * @return
	 *  Unprotected blob of data.
	 */
	public static byte[] cryptUnprotectData(byte[] data, int flags) {
		return cryptUnprotectData(data, null, flags, null);
	}
	
	/**
	 * Unprotect a blob of data.
	 * @param data
	 *  Data to unprotect.
	 * @param entropy
	 *  Optional entropy.
	 * @param flags
	 *  Optional flags.
	 * @param prompt
	 *  Optional prompt structure.
	 * @return
	 *  Unprotected blob of data.
	 */
	public static byte[] cryptUnprotectData(byte[] data, byte[] entropy, int flags, 
			CRYPTPROTECT_PROMPTSTRUCT prompt) {
    	DATA_BLOB pDataIn = new DATA_BLOB(data);
    	DATA_BLOB pDataUnprotected = new DATA_BLOB();
    	DATA_BLOB pEntropy = (entropy == null) ? null : new DATA_BLOB(entropy);
    	PointerByReference pDescription = new PointerByReference();
    	try {
	    	if (! Crypt32.INSTANCE.CryptUnprotectData(pDataIn, pDescription, 
	    			pEntropy, null, prompt, flags, pDataUnprotected)) {
	    		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
	    	}
	    	return pDataUnprotected.getData();
    	} finally {
    		if (pDataUnprotected.pbData != null) {
    			Kernel32.INSTANCE.LocalFree(pDataUnprotected.pbData);
    		}
    		if (pDescription.getValue() != null) {
    			Kernel32.INSTANCE.LocalFree(pDescription.getValue());
    		}
    	}
	}
}