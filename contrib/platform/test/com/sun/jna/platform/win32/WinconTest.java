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
package com.sun.jna.platform.win32;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * @author lgoldstein
 */
public class WinconTest extends Assert {
	private static final Wincon	INSTANCE=Kernel32.INSTANCE;

	@Test
	public void testGetConsoleDisplayMode() {
		IntByReference	curMode=new IntByReference();
		assertCallSucceeded("Initial display mode value retrieval", INSTANCE.GetConsoleDisplayMode(curMode));
		System.out.println("GetConsoleDisplayMode: " + curMode.getValue());
	}
	
	@Test
	public void testConsoleCP() {
		int	curCP=INSTANCE.GetConsoleCP();
		System.out.println("Console CP=" + curCP);
		// NOTE: we use the same code page value just in case the "SetConsoleCP" call fails
		assertCallSucceeded("Restore CP=" + curCP, INSTANCE.SetConsoleCP(curCP));
	}

	@Test
	public void testConsoleOutputCP() {
		int	curCP=INSTANCE.GetConsoleOutputCP();
		System.out.println("Console output CP=" + curCP);
		// NOTE: we use the same code page value just in case the "SetConsoleOutputCP" call fails
		assertCallSucceeded("Restore CP=" + curCP, INSTANCE.SetConsoleOutputCP(curCP));
	}

	@Test
	public void testGetConsoleWindow() {
		HWND	hwnd=INSTANCE.GetConsoleWindow();
		// don't really care what the handle value is - just ensure that API can be called
		System.out.println("Console window=" + hwnd);
	}

	@Test
	public void testGetNumberOfConsoleMouseButtons() {
		IntByReference	numButtons=new IntByReference(0);
		assertCallSucceeded("Initial display mode value retrieval", INSTANCE.GetNumberOfConsoleMouseButtons(numButtons));
		System.out.println("GetNumberOfConsoleMouseButtons: " + numButtons.getValue());
	}

	@Test
	public void testGetStdHandle() {
		for (int nHandle : new int[] { Wincon.STD_INPUT_HANDLE, Wincon.STD_OUTPUT_HANDLE, Wincon.STD_ERROR_HANDLE }) {
			HANDLE	hndl=INSTANCE.GetStdHandle(nHandle);
			assertNotEquals("Bad handle value for std handle=" + nHandle, WinBase.INVALID_HANDLE_VALUE, hndl);
			// don't really care what the handle value is - just ensure that API can be called
			System.out.println("GetStdHandle(" + nHandle + "): " + hndl);

			/*
			 * According to the API documentation:
			 * 
			 * 		If an application does not have associated standard handles,
			 * 		such as a service running on an interactive desktop, and has
			 *  	not redirected them, the return value is NULL.
			 */
			Pointer	ptr=hndl.getPointer();
			if (ptr == Pointer.NULL) {
				System.out.println("NULL standard handle for type=" + nHandle);
			} else {
				assertCallSucceeded("SetStdHandle(" + nHandle + ")", INSTANCE.SetStdHandle(nHandle, hndl));
			}
		}
	}

	@Test
	@Ignore("For some reason we get hr=6 - ERROR_INVALID_HANDLE - even though the documentation stipulates that GetStdHandle can be used")
	public void testGetConsoleMode() {
		for (int nHandle : new int[] { Wincon.STD_INPUT_HANDLE, Wincon.STD_OUTPUT_HANDLE, Wincon.STD_ERROR_HANDLE }) {
			HANDLE	hndl=INSTANCE.GetStdHandle(nHandle);
			Pointer	ptr=hndl.getPointer();
			if (ptr == Pointer.NULL) {
				System.out.println("NULL standard handle for type=" + nHandle);
				continue;	// can happen for interactive desktop application
			}
			
			IntByReference	lpMode=new IntByReference(0);
			assertCallSucceeded("GetConsoleMode(" + nHandle + ")", INSTANCE.GetConsoleMode(hndl, lpMode));
			
			int	dwMode=lpMode.getValue();
			// don't really care what the mode is just want to make sure API can be called
			assertCallSucceeded("SetConsoleMode(" + nHandle + "," + dwMode + ")", INSTANCE.SetConsoleMode(hndl, dwMode));
		}
	}

	@Test
	public void testGetConsoleTitle() {
		char[]	lpConsoleTitle=new char[WinNT.MAX_PATH];
		int		len=INSTANCE.GetConsoleTitle(lpConsoleTitle, lpConsoleTitle.length);
		assertCallSucceeded("GetConsoleTitle", (len > 0));
		
		String	title=Native.toString(lpConsoleTitle);
		System.out.println("Console title(len=" + len + "): " + title);
		assertCallSucceeded("SetConsoleTitle", INSTANCE.SetConsoleTitle(title));
	}

	@Test
	public void testGetConsoleOriginalTitle() {
		char[]	lpConsoleTitle=new char[WinNT.MAX_PATH];
		int		len=INSTANCE.GetConsoleOriginalTitle(lpConsoleTitle, lpConsoleTitle.length);
		if (len <= 0) {
			int	hr=Kernel32.INSTANCE.GetLastError();
			if (hr == 0) {	// don't fail the test - we just want to see if the API can be called
				System.err.println("Buffer not large enough to hold the title");
			} else {
				fail("Call failed: hr=0x" + Integer.toHexString(hr));
			}
		} else {
			System.out.println("Console original title (len=" + len + "): " + Native.toString(lpConsoleTitle));
		}
	}

	private static void assertCallSucceeded(String message, boolean result) {
		if (result) {
			return;
		}
		
		int	hr=Kernel32.INSTANCE.GetLastError();
		if (hr == 0) {
			fail(message + " failed with unknown reason code");
		} else {
			fail(message + " failed: hr=0x" + Integer.toHexString(hr));
		}
	}
}
