/* Copyright (c) 2015 Andreas "PAX" L\u00FCck, All Rights Reserved
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
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * The process status application programming interface (PSAPI) is a helper
 * library that makes it easier for you to obtain information about processes
 * and device drivers.
 * 
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public interface Psapi extends StdCallLibrary {
	Psapi INSTANCE = Native.loadLibrary("psapi", Psapi.class, W32APIOptions.DEFAULT_OPTIONS);
	
	/**
	 * Retrieves the fully qualified path for the file containing the specified
	 * module.
	 * 
	 * @param process
	 *            A handle to the process that contains the module.
	 * @param module
	 *            A handle to the module. If this parameter is NULL,
	 *            GetModuleFileNameEx returns the path of the executable file of
	 *            the process specified in hProcess.
	 * @param lpFilename
	 *            A pointer to a buffer that receives the fully qualified path
	 *            to the module. If the size of the file name is larger than the
	 *            value of the nSize parameter, the function succeeds but the
	 *            file name is truncated and null-terminated.
	 * @param nSize
	 *            The size of the lpFilename buffer, in characters.
	 * @return If the function succeeds, the return value specifies the length
	 *         of the string copied to the buffer. If the function fails, the
	 *         return value is zero. To get extended error information, call
	 *         {@link Kernel32Util#getLastErrorMessage()}.
	 */
	int GetModuleFileNameExA(HANDLE process, HANDLE module, byte[] lpFilename, int nSize);
	
	/**
	 * Retrieves the fully qualified path for the file containing the specified
	 * module.
	 * 
	 * @param process
	 *            A handle to the process that contains the module.
	 * @param module
	 *            A handle to the module. If this parameter is NULL,
	 *            GetModuleFileNameEx returns the path of the executable file of
	 *            the process specified in hProcess.
	 * @param lpFilename
	 *            A pointer to a buffer that receives the fully qualified path
	 *            to the module. If the size of the file name is larger than the
	 *            value of the nSize parameter, the function succeeds but the
	 *            file name is truncated and null-terminated.
	 * @param nSize
	 *            The size of the lpFilename buffer, in characters.
	 * @return If the function succeeds, the return value specifies the length
	 *         of the string copied to the buffer. If the function fails, the
	 *         return value is zero. To get extended error information, call
	 *         {@link Kernel32Util#getLastErrorMessage()}.
	 */
	int GetModuleFileNameExW(HANDLE process, HANDLE module, char[] lpFilename, int nSize);

	/**
	 * Retrieves the fully qualified path for the file containing the specified
	 * module.
	 * 
	 * @param process
	 *            A handle to the process that contains the module.
	 * @param module
	 *            A handle to the module. If this parameter is NULL,
	 *            GetModuleFileNameEx returns the path of the executable file of
	 *            the process specified in hProcess.
	 * @param lpFilename
	 *            A pointer to a buffer that receives the fully qualified path
	 *            to the module. If the size of the file name is larger than the
	 *            value of the nSize parameter, the function succeeds but the
	 *            file name is truncated and null-terminated.
	 * @param nSize
	 *            The size of the lpFilename buffer, in characters.
	 * @return If the function succeeds, the return value specifies the length
	 *         of the string copied to the buffer. If the function fails, the
	 *         return value is zero. To get extended error information, call
	 *         {@link Kernel32Util#getLastErrorMessage()}.
	 */
	int GetModuleFileNameEx(HANDLE process, HANDLE module, Pointer lpFilename, int nSize);
}
