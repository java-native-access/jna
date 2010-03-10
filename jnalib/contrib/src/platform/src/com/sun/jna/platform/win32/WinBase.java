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

import com.sun.jna.Platform;

/**
 * Ported from Winbase.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public abstract class WinBase {

	public static final int WAIT_FAILED = 0xFFFFFFFF;
	public static final int WAIT_OBJECT_0 = ((NTStatus.STATUS_WAIT_0 ) + 0 );
	public static final int WAIT_ABANDONED = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
	public static final int WAIT_ABANDONED_0 = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
	
	/**
	 * Maximum computer name length.
	 * @return 15 on MAC, 31 on everything else.
	 */
	public static int MAX_COMPUTERNAME_LENGTH() {
		if (Platform.isMac()) {
			return 15;
		} else {
			return 31;			
		}
	}
	
	/**
	 * This logon type is intended for users who will be interactively using the computer, such 
	 * as a user being logged on by a terminal server, remote shell, or similar process. This 
	 * logon type has the additional expense of caching logon information for disconnected operations; 
	 * therefore, it is inappropriate for some client/server applications, such as a mail server. 
	 */
	public static final int LOGON32_LOGON_INTERACTIVE = 2;
	/**
	 * This logon type is intended for high performance servers to authenticate plaintext passwords. 
	 * The LogonUser function does not cache credentials for this logon type.
	 */
	public static final int LOGON32_LOGON_NETWORK = 3;
	/**
	 * This logon type is intended for batch servers, where processes may be executing on behalf 
	 * of a user without their direct intervention. This type is also for higher performance servers 
	 * that process many plaintext authentication attempts at a time, such as mail or Web servers. 
	 * The LogonUser function does not cache credentials for this logon type.
	 */
	public static final int LOGON32_LOGON_BATCH = 4;
	/**
	 * Indicates a service-type logon. The account provided must have the service privilege enabled.
	 */
	public static final int LOGON32_LOGON_SERVICE = 5;
	/**
	 * This logon type is for GINA DLLs that log on users who will be interactively using the computer. 
	 * This logon type can generate a unique audit record that shows when the workstation was unlocked.
	 */
	public static final int LOGON32_LOGON_UNLOCK = 7;
	/**
	 * This logon type preserves the name and password in the authentication package, which allows the 
	 * server to make connections to other network servers while impersonating the client. A server can 
	 * accept plaintext credentials from a client, call LogonUser, verify that the user can access the 
	 * system across the network, and still communicate with other servers.
	 */
	public static final int LOGON32_LOGON_NETWORK_CLEARTEXT = 8;
	/**
	 * This logon type allows the caller to clone its current token and specify new credentials for 
	 * outbound connections. The new logon session has the same local identifier but uses different 
	 * credentials for other network connections. This logon type is supported only by the 
	 * LOGON32_PROVIDER_WINNT50 logon provider.
	 */
	public static final int LOGON32_LOGON_NEW_CREDENTIALS = 9;

	/**
	 * Use the standard logon provider for the system. The default security provider is negotiate, 
	 * unless you pass NULL for the domain name and the user name is not in UPN format. In this case, 
	 * the default provider is NTLM. 
	 */
	public static final int LOGON32_PROVIDER_DEFAULT = 0;
	
	/**
	 * Use the Windows NT 3.5 logon provider.
	 */
	public static final int LOGON32_PROVIDER_WINNT35 = 1;
	/**
	 * Use the NTLM logon provider.
	 */
	public static final int LOGON32_PROVIDER_WINNT40 = 2;
	/**
	 * Use the negotiate logon provider.
	 */
	public static final int LOGON32_PROVIDER_WINNT50 = 3;	
}
