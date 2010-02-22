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
package com.sun.jna.examples.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface Netapi32 extends W32API {
	Netapi32 INSTANCE = (Netapi32) Native.loadLibrary("Netapi32", Netapi32.class);

	public abstract class NETSETUP_JOIN_STATUS {
		public static final int NetSetupUnknownStatus = 0;
		public static final int NetSetupUnjoined = 1;
		public static final int NetSetupWorkgroupName = 2;
		public static final int NetSetupDomainName = 3;		
	};

    public static final int NERR_Success = 0;
    public static final int NERR_BufTooSmall = 2123;
    public static final int NERR_InvalidComputer = 2351;
	
	/**
	 * Retrieves join status information for the specified computer.
	 * @param lpServer Specifies the DNS or NetBIOS name of the computer on which to call the function.
	 * @param lpNameBuffer Receives the NetBIOS name of the domain or workgroup to which the computer is joined.
	 * @param BufferType Join status of the specified computer.
	 * @return
	 */
	public int NetGetJoinInformation(char[] lpServer, PointerByReference lpNameBuffer, 
			IntByReference BufferType);	
	
	/**
	 * Frees the memory that the NetApiBufferAllocate function allocates.
	 * @param buffer
	 * @return
	 */
	public int NetApiBufferFree(Pointer buffer);
}
