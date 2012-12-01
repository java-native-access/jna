/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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

import com.sun.jna.WString;
import com.sun.jna.platform.win32.User32.WNDCLASSEX;
import com.sun.jna.platform.win32.User32.WindowProc;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.MSG;

// TODO: Auto-generated Javadoc
/**
 * The Class Win32WindowTest.
 */
public class Win32WindowTest implements WindowProc {

	/**
	 * Instantiates a new win32 window test.
	 */
	public Win32WindowTest() {
		// define new window class
		WString windowClass = new WString("MyWindowClass");
		HMODULE hInst = Kernel32.INSTANCE.GetModuleHandle("");

		WNDCLASSEX wClass = new WNDCLASSEX();
		wClass.hInstance = hInst;
		wClass.lpfnWndProc = Win32WindowTest.this;
		wClass.lpszClassName = windowClass;
		
		// register window class
		User32.INSTANCE.RegisterClassEx(wClass);
		getLastError();
		
		// create new window
		HWND hWnd = User32.INSTANCE
				.CreateWindowEx(
						User32.WS_EX_TOPMOST,
						windowClass,
						"My hidden helper window, used only to catch the windows events",
						0, 0, 0, 0, 0, WinUser.HWND_MESSAGE, null, hInst, null);

		getLastError();
		System.out.println("window sucessfully created! window hwnd: " + hWnd.getPointer().toString());

		Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd,
				Wtsapi32.NOTIFY_FOR_THIS_SESSION);

		MSG msg = new MSG();
		while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0) {
			User32.INSTANCE.TranslateMessage(msg);
			User32.INSTANCE.DispatchMessage(msg);
		}

		Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
		User32.INSTANCE.UnregisterClass(windowClass, hInst);
		User32.INSTANCE.DestroyWindow(hWnd);
		
		System.out.println("program exit!");
	}

	/* (non-Javadoc)
	 * @see com.sun.jna.platform.win32.User32.WindowProc#callback(com.sun.jna.platform.win32.WinDef.HWND, int, com.sun.jna.platform.win32.WinDef.WPARAM, com.sun.jna.platform.win32.WinDef.LPARAM)
	 */
	public LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam) {
		switch (uMsg) {
		case WinUser.WM_DESTROY: {
			User32.INSTANCE.PostQuitMessage(0);
			return new LRESULT(0);
		}
		case WinUser.WM_SESSION_CHANGE: {
			this.onSessionChange(wParam, lParam);
			return new LRESULT(0);
		}
		case WinUser.WM_DEVICECHANGE: {
			this.onDeviceChange(wParam, lParam);
			return new LRESULT(0);
		}
		default:
			return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
		}
	}

	/**
	 * Gets the last error.
	 *
	 * @return the last error
	 */
	public int getLastError() {
		int rc = Kernel32.INSTANCE.GetLastError();

		if (rc != 0)
			System.out.println("error: " + rc);

		return rc;
	}

	/**
	 * On session change.
	 *
	 * @param wParam the w param
	 * @param lParam the l param
	 */
	protected void onSessionChange(WPARAM wParam, LPARAM lParam) {
		switch (wParam.intValue()) {
		case Wtsapi32.WTS_CONSOLE_CONNECT: {
			this.onConsoleConnect(lParam.intValue());
			break;
		}
		case Wtsapi32.WTS_CONSOLE_DISCONNECT: {
			this.onConsoleDisconnect(lParam.intValue());
			break;
		}
		case Wtsapi32.WTS_SESSION_LOGON: {
			this.onMachineLogon(lParam.intValue());
			break;
		}
		case Wtsapi32.WTS_SESSION_LOGOFF: {
			this.onMachineLogoff(lParam.intValue());
			break;
		}
		case Wtsapi32.WTS_SESSION_LOCK: {
			this.onMachineLocked(lParam.intValue());
			break;
		}
		case Wtsapi32.WTS_SESSION_UNLOCK: {
			this.onMachineUnlocked(lParam.intValue());
			break;
		}
		}
	}

	/**
	 * On console connect.
	 *
	 * @param sessionId the session id
	 */
	protected void onConsoleConnect(int sessionId) {
		System.out.println("onConsoleConnect: " + sessionId);
	}

	/**
	 * On console disconnect.
	 *
	 * @param sessionId the session id
	 */
	protected void onConsoleDisconnect(int sessionId) {
		System.out.println("onConsoleDisconnect: " + sessionId);
	}

	/**
	 * On machine locked.
	 *
	 * @param sessionId the session id
	 */
	protected void onMachineLocked(int sessionId) {
		System.out.println("onMachineLocked: " + sessionId);
	}

	/**
	 * On machine unlocked.
	 *
	 * @param sessionId the session id
	 */
	protected void onMachineUnlocked(int sessionId) {
		System.out.println("onMachineUnlocked: " + sessionId);
	}

	/**
	 * On machine logon.
	 *
	 * @param sessionId the session id
	 */
	protected void onMachineLogon(int sessionId) {
		System.out.println("onMachineLogon: " + sessionId);
	}

	/**
	 * On machine logoff.
	 *
	 * @param sessionId the session id
	 */
	protected void onMachineLogoff(int sessionId) {
		System.out.println("onMachineLogoff: " + sessionId);
	}

	/**
	 * On device change.
	 *
	 * @param wParam the w param
	 * @param lParam the l param
	 */
	protected void onDeviceChange(WPARAM wParam, LPARAM lParam) {
		System.out.println("WM_DEVICECHANGE");
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new Win32WindowTest();
	}
}
