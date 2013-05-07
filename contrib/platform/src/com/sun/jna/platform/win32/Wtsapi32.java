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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Wtsapi32 extends StdCallLibrary {

	Wtsapi32 INSTANCE = (Wtsapi32) Native.loadLibrary("Wtsapi32",
			Wtsapi32.class, W32APIOptions.DEFAULT_OPTIONS);

	int NOTIFY_FOR_ALL_SESSIONS = 1;

	int NOTIFY_FOR_THIS_SESSION = 0;

	/**
	 * The session identified by lParam was connected to the console terminal or
	 * RemoteFX session.
	 */
	public static final int WTS_CONSOLE_CONNECT = 0x1;

	/**
	 * The session identified by lParam was disconnected from the console
	 * terminal or RemoteFX session.
	 */
	public static final int WTS_CONSOLE_DISCONNECT = 0x2;

	/**
	 * The session identified by lParam was connected to the remote terminal.
	 */
	public static final int WTS_REMOTE_CONNECT = 0x3;

	/**
	 * The session identified by lParam was disconnected from the remote
	 * terminal.
	 */
	public static final int WTS_REMOTE_DISCONNECT = 0x4;

	/**
	 * A user has logged on to the session identified by lParam.
	 */
	public static final int WTS_SESSION_LOGON = 0x5;

	/**
	 * A user has logged off the session identified by lParam.
	 */
	public static final int WTS_SESSION_LOGOFF = 0x6;

	/**
	 * The session identified by lParam has been locked.
	 */
	public static final int WTS_SESSION_LOCK = 0x7;

	/**
	 * The session identified by lParam has been unlocked.
	 */
	public static final int WTS_SESSION_UNLOCK = 0x8;

	/**
	 * The session identified by lParam has changed its remote controlled
	 * status. To determine the status, call GetSystemMetrics and check the
	 * SM_REMOTECONTROL metric.
	 */
	public static final int WTS_SESSION_REMOTE_CONTROL = 0x9;

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
	public boolean WTSRegisterSessionNotification(HWND hWnd, int dwFlags);

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
	public boolean WTSUnRegisterSessionNotification(HWND hWnd);
}
