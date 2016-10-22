/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Wtsapi32 extends StdCallLibrary {

	Wtsapi32 INSTANCE = Native.loadLibrary("Wtsapi32", Wtsapi32.class, W32APIOptions.DEFAULT_OPTIONS);

	int NOTIFY_FOR_ALL_SESSIONS = 1;

	int NOTIFY_FOR_THIS_SESSION = 0;

	/**
	 * The session identified by lParam was connected to the console terminal or
	 * RemoteFX session.
	 */
	int WTS_CONSOLE_CONNECT = 0x1;

	/**
	 * The session identified by lParam was disconnected from the console
	 * terminal or RemoteFX session.
	 */
	int WTS_CONSOLE_DISCONNECT = 0x2;

	/**
	 * The session identified by lParam was connected to the remote terminal.
	 */
	int WTS_REMOTE_CONNECT = 0x3;

	/**
	 * The session identified by lParam was disconnected from the remote
	 * terminal.
	 */
	int WTS_REMOTE_DISCONNECT = 0x4;

	/**
	 * A user has logged on to the session identified by lParam.
	 */
	int WTS_SESSION_LOGON = 0x5;

	/**
	 * A user has logged off the session identified by lParam.
	 */
	int WTS_SESSION_LOGOFF = 0x6;

	/**
	 * The session identified by lParam has been locked.
	 */
	int WTS_SESSION_LOCK = 0x7;

	/**
	 * The session identified by lParam has been unlocked.
	 */
	int WTS_SESSION_UNLOCK = 0x8;

	/**
	 * The session identified by lParam has changed its remote controlled
	 * status. To determine the status, call GetSystemMetrics and check the
	 * SM_REMOTECONTROL metric.
	 */
	int WTS_SESSION_REMOTE_CONTROL = 0x9;

	/**
	 * Registers the specified window to receive session change notifications.
	 * 
	 * @param hWnd
	 *            [in] Handle of the window to receive session change
	 *            notifications.
	 * 
	 * @param dwFlags
	 *            [in] Specifies which session notifications are to be received.
	 *            This parameter can be one of the following values.
	 * 
	 * @return If the function succeeds, the return value is TRUE. Otherwise, it
	 *         is FALSE. To get extended error information, call GetLastError.
	 */
	boolean WTSRegisterSessionNotification(HWND hWnd, int dwFlags);

	/**
	 * Unregisters the specified window so that it receives no further session
	 * change notifications.
	 * 
	 * @param hWnd
	 *            [in] Handle of the window to be unregistered from receiving
	 *            session notifications.
	 * 
	 * @return If the function succeeds, the return value is TRUE. Otherwise, it
	 *         is FALSE. To get extended error information, call GetLastError.
	 */
	boolean WTSUnRegisterSessionNotification(HWND hWnd);
}
