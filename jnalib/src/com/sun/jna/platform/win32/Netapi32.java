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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Netapi32.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface Netapi32 extends W32API {
	Netapi32 INSTANCE = (Netapi32) Native.loadLibrary("Netapi32",
			Netapi32.class, UNICODE_OPTIONS);

	/**
	 * Retrieves join status information for the specified computer.
	 * 
	 * @param lpServer
	 *            Specifies the DNS or NetBIOS name of the computer on which to
	 *            call the function.
	 * @param lpNameBuffer
	 *            Receives the NetBIOS name of the domain or workgroup to which
	 *            the computer is joined.
	 * @param BufferType
	 *            Join status of the specified computer.
	 * @return If the function succeeds, the return value is NERR_Success. If
	 *         the function fails, the return value is a system error code.
	 */
	public int NetGetJoinInformation(String lpServer,
			PointerByReference lpNameBuffer, IntByReference BufferType);

	/**
	 * Frees the memory that the NetApiBufferAllocate function allocates.
	 * 
	 * @param buffer
	 * @return If the function succeeds, the return value is NERR_Success. If
	 *         the function fails, the return value is a system error code.
	 */
	public int NetApiBufferFree(Pointer buffer);

	/**
	 * Returns information about each local group account on the specified
	 * server.
	 * 
	 * @param serverName
	 *            Specifies the DNS or NetBIOS name of the remote server on
	 *            which the function is to execute. If this parameter is NULL,
	 *            the local computer is used.
	 * @param level
	 *            Specifies the information level of the data.
	 * @param bufptr
	 *            Pointer to the address of the buffer that receives the
	 *            information structure.
	 * @param prefmaxlen
	 *            Specifies the preferred maximum length of returned data, in
	 *            bytes.
	 * @param entriesread
	 *            Pointer to a value that receives the count of elements
	 *            actually enumerated.
	 * @param totalentries
	 *            Pointer to a value that receives the approximate total number
	 *            of entries that could have been enumerated from the current
	 *            resume position.
	 * @param resume_handle
	 *            Pointer to a value that contains a resume handle that is used
	 *            to continue an existing local group search.
	 * @return If the function succeeds, the return value is NERR_Success.
	 */
	public int NetLocalGroupEnum(String serverName, int level,
			PointerByReference bufptr, int prefmaxlen,
			IntByReference entriesread, IntByReference totalentries,
			IntByReference resume_handle);
	
	/**
	 * Returns the name of the primary domain controller (PDC).
	 * 
	 * @param serverName 
	 * 	Specifies the DNS or NetBIOS name of the remote server on which the function is 
	 * 	to execute. If this parameter is NULL, the local computer is used. 
	 * @param domainName
	 * 	Specifies the name of the domain. 
	 * @param bufptr
	 * 	Receives a string that specifies the server name of the PDC of the domain.
	 * @return 
	 * 	If the function succeeds, the return value is NERR_Success.
	 */
	public int NetGetDCName(String serverName, String domainName, 
			PointerByReference bufptr);
}
