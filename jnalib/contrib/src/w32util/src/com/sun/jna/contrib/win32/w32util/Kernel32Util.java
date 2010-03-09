/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.contrib.win32.w32util;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;

/**
 * Kernel32 utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Kernel32Util {
	
	/**
	 * Get current computer NetBIOS name.
	 * @return Netbios name.
	 */
	public static String getComputerName() {
    	char buffer[] = new char[WinBase.MAX_COMPUTERNAME_LENGTH() + 1];
    	IntByReference lpnSize = new IntByReference(buffer.length);
    	if (! Kernel32.INSTANCE.GetComputerName(buffer, lpnSize)) {
    		throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
    	}
    	return Native.toString(buffer);
	}
}
