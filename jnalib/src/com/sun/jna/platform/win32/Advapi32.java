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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Advapi32.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface Advapi32 extends W32API {
	Advapi32 INSTANCE = (Advapi32) Native.loadLibrary("Advapi32",
			Advapi32.class, UNICODE_OPTIONS);

	/**
	 * Retrieves the name of the user associated with the current thread.
	 * http://msdn.microsoft.com/en-us/library/ms724432(VS.85).aspx
	 * 
	 * @param buffer
	 *            Buffer to receive the user's logon name.
	 * @param len
	 *            On input, the size of the buffer, on output the number of
	 *            characters copied into the buffer, including the terminating
	 *            null character.
	 * @return True if succeeded.
	 */
	public boolean GetUserNameW(char[] buffer, IntByReference len);

	/**
	 * Accepts the name of a system and anaccount as input and retrieves a security 
	 * identifier (SID) for the account and the name of the domain on which the 
	 * account was found.
	 * http://msdn.microsoft.com/en-us/library/aa379159(VS.85).aspx
	 * 
	 * @param lpSystemName
	 *            Specifies the name of the system.
	 * @param lpAccountName
	 *            Specifies the account name.
	 * @param Sid
	 *            Receives the SID structure that corresponds to the account
	 *            name pointed to by the lpAccountName parameter.
	 * @param cbSid
	 *            On input, this value specifies the size, in bytes, of the Sid
	 *            buffer. If the function fails because the buffer is too small
	 *            or if cbSid is zero, this variable receives the required
	 *            buffer size.
	 * @param ReferencedDomainName
	 *            Receives the name of the domain where the account name is
	 *            found.
	 * @param cchReferencedDomainName
	 *            On input, this value specifies the size, in TCHARs, of the
	 *            ReferencedDomainName buffer. If the function fails because the
	 *            buffer is too small, this variable receives the required
	 *            buffer size, including the terminating null character.
	 * @param peUse
	 *            SID_NAME_USE enumerated type that indicates the type of the
	 *            account when the function returns.
	 * @return True if the function was successful, False otherwise.
	 */
	public boolean LookupAccountName(String lpSystemName,
			String lpAccountName, byte[] Sid, IntByReference cbSid,
			char[] ReferencedDomainName,
			IntByReference cchReferencedDomainName, PointerByReference peUse);

	/**
	 * Retrieves the name of the account for this SID and the name of the first domain 
	 * on which this SID is found.
	 * 
	 * @param lpSystemName Specifies the target computer.
	 * @param Sid The SID to look up. 
	 * @param lpName
	 *  Buffer that receives a null-terminated string that contains the 
	 * 	account name that corresponds to the lpSid parameter. 
	 * @param cchName 
	 * 	On input, specifies the size, in TCHARs, of the lpName buffer. If the function fails 
	 *  because the buffer is too small or if cchName is zero, cchName receives the required 
	 *  buffer size, including the terminating null character. 
	 * @param ReferencedDomainName
	 *  Pointer to a buffer that receives a null-terminated string that contains the name of 
	 *  the domain where the account name was found.
	 * @param cchReferencedDomainName
	 * 	On input, specifies the size, in TCHARs, of the lpReferencedDomainName buffer. If the 
	 * 	function fails because the buffer is too small or if cchReferencedDomainName is zero, 
	 * 	cchReferencedDomainName receives the required buffer size, including the terminating 
	 * 	null character. 
	 * @param peUse
	 *  Pointer to a variable that receives a SID_NAME_USE value that indicates the type of 
	 *  the account.
	 * @return 
	 *  If the function succeeds, the function returns nonzero.
	 *  If the function fails, it returns zero. To get extended error information, call 
	 *  GetLastError.
	 */
	public boolean LookupAccountSid(String lpSystemName, byte[] Sid,
			char[] lpName, IntByReference cchName,  char[] ReferencedDomainName,
	        IntByReference cchReferencedDomainName, PointerByReference peUse);
	
	/**
	 * Convert a security identifier (SID) to a string format suitable for display, 
	 * storage, or transmission.
	 * http://msdn.microsoft.com/en-us/library/aa376399(VS.85).aspx
	 * 
	 * @param Sid
	 *            The SID structure to be converted.
	 * @param StringSid
	 *            Pointer to a variable that receives a pointer to a
	 *            null-terminated SID string. To free the returned buffer, call
	 *            the LocalFree function.
	 * @return True if the function was successful, False otherwise.
	 */
	public boolean ConvertSidToStringSid(byte[] Sid,
			PointerByReference StringSid);	

	/**
	 * Convert a string-format security identifier (SID) into a valid, functional SID.
	 * http://msdn.microsoft.com/en-us/library/aa376402(VS.85).aspx
	 * 
	 * @param StringSid The string-format SID to convert.
	 * @param Sid Receives a pointer to the converted SID.
	 * @return True if the function was successful, False otherwise.
	 */
	public boolean ConvertStringSidToSid(String StringSid,
			PointerByReference Sid);
	
	/**
	 * Returns the length, in bytes, of a valid security identifier (SID).
	 * http://msdn.microsoft.com/en-us/library/aa446642(VS.85).aspx
	 * 
	 * @param sid A pointer to the SID structure whose length is returned.
	 * @return Length of the SID.
	 */
	public int GetLengthSid(PointerByReference sid);
}
