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

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Kernel32 utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Kernel32Util implements WinDef {
	
	/**
	 * Get current computer NetBIOS name.
	 * @return 
	 *  Netbios name.
	 */
	public static String getComputerName() {
    	char buffer[] = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
    	IntByReference lpnSize = new IntByReference(buffer.length);
    	if (! Kernel32.INSTANCE.GetComputerName(buffer, lpnSize)) {
    		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}
    	return Native.toString(buffer);
	}
	
	/**
	 * Format a message from an HRESULT.
	 * @param code
	 *  HRESULT
	 * @return
	 *  Formatted message.
	 */
	public static String formatMessageFromHR(HRESULT code) {
		PointerByReference buffer = new PointerByReference();        
        if (0 == Kernel32.INSTANCE.FormatMessage(
        		WinBase.FORMAT_MESSAGE_ALLOCATE_BUFFER
        		| WinBase.FORMAT_MESSAGE_FROM_SYSTEM 
        		| WinBase.FORMAT_MESSAGE_IGNORE_INSERTS, 
                null,
                code.intValue(), 
                0, // TODO: MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT)
                buffer, 
                0, 
                null)) {
        	throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
        }	       
    	String s = buffer.getValue().getString(0, ! Boolean.getBoolean("w32.ascii"));
    	Kernel32.INSTANCE.LocalFree(buffer.getValue());
    	return s.trim();		
	}
	
	/**
	 * Format a system message from an error code.
	 * @param code
	 *  Error code, typically a result of GetLastError.
	 * @return
	 *  Formatted message.
	 */
	public static String formatMessageFromLastErrorCode(int code) {
		return formatMessageFromHR(W32Errors.HRESULT_FROM_WIN32(code));
	}
	
	/**
	 * Return the path designated for temporary files.
	 * @return
	 *  Path.
	 */
	public static String getTempPath() {
		DWORD nBufferLength = new DWORD(WinDef.MAX_PATH);
    	char[] buffer = new char[nBufferLength.intValue()]; 
    	if (Kernel32.INSTANCE.GetTempPath(nBufferLength, buffer).intValue() == 0) {
    		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}
    	return Native.toString(buffer);
	}
	
	public static void deleteFile(String filename) {
    	if (! Kernel32.INSTANCE.DeleteFile(filename)) {
    		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}
	}
	
	/**
	 * Returns valid drives in the system.
	 * @return
	 *  An array of valid drives.
	 */
	public static String[] getLogicalDriveStrings() {
    	DWORD dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(new DWORD(0), null);
    	if (dwSize.intValue() <= 0) {
    		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}
    	
    	char buf[] = new char[dwSize.intValue()];
    	dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(dwSize, buf);
    	if (dwSize.intValue() <= 0) {
    		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}

    	List<String> drives = new ArrayList<String>();    	
    	String drive = "";
    	// the buffer is double-null-terminated
    	for(int i = 0; i < buf.length - 1; i++) {
    		if (buf[i] == 0) {
    			drives.add(drive);
    			drive = "";
    		} else {
    			drive += buf[i];
    		}
    	}
    	return drives.toArray(new String[0]);
	}	
	
	/**
	 * Retrieves file system attributes for a specified file or directory.
	 * @param fileName
	 * 	The name of the file or directory.
	 * @return
	 *  The attributes of the specified file or directory.
	 */
	public static int getFileAttributes(String fileName) {
		int fileAttributes = Kernel32.INSTANCE.GetFileAttributes(fileName);
		if (fileAttributes == WinBase.INVALID_FILE_ATTRIBUTES) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		return fileAttributes;
	}
}
