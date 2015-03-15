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

import static org.junit.Assert.assertTrue;

import javax.swing.JFrame;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Applies API tests on {@link Psapi}.
 * 
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public class PsapiTest {
	@Test
	public void testGetModuleFileNameEx() {
		final JFrame w = new JFrame();
		try {
			w.setVisible(true);
			final String searchSubStr = "\\bin\\javaw.exe";
			final HWND hwnd = new HWND();
			hwnd.setPointer(Native.getComponentPointer(w));

			final IntByReference pid = new IntByReference();
			User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);

			final HANDLE process = Kernel32.INSTANCE.OpenProcess(
					0x0400 | 0x0010, false, pid.getValue());
			if (process == null)
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

			// check ANSI function
			final byte[] filePathAnsi = new byte[1025];
			int length = Psapi.INSTANCE.GetModuleFileNameExA(process, null,
					filePathAnsi, filePathAnsi.length - 1);
			if (length == 0)
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

			assertTrue(
					"Path didn't contain '" + searchSubStr + "': "
							+ Native.toString(filePathAnsi),
					Native.toString(filePathAnsi).toLowerCase()
							.contains(searchSubStr));

			// check Unicode function
			final char[] filePathUnicode = new char[1025];
			length = Psapi.INSTANCE.GetModuleFileNameExW(process, null,
					filePathUnicode, filePathUnicode.length - 1);
			if (length == 0)
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

			assertTrue(
					"Path didn't contain '" + searchSubStr + "': "
							+ Native.toString(filePathUnicode),
					Native.toString(filePathUnicode).toLowerCase()
							.contains(searchSubStr));

			// check default function
			final int memAllocSize = 1025 * Native.WCHAR_SIZE;
			final Memory filePathDefault = new Memory(memAllocSize);
			length = Psapi.INSTANCE.GetModuleFileNameEx(process, null,
					filePathDefault, (memAllocSize / Native.WCHAR_SIZE) - 1);
			if (length == 0)
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

			assertTrue(
					"Path didn't contain '"
							+ searchSubStr
							+ "': "
							+ Native.toString(filePathDefault.getCharArray(0,
									memAllocSize / Native.WCHAR_SIZE)),
					Native.toString(
							filePathDefault.getCharArray(0, memAllocSize
									/ Native.WCHAR_SIZE)).toLowerCase()
							.contains(searchSubStr));
		} finally {
			w.dispose();
		}
	}
}
