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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface Advapi32 extends W32API {
	Advapi32 INSTANCE = (Advapi32) Native.loadLibrary("Advapi32",
			Advapi32.class, DEFAULT_OPTIONS);

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
	 * The LookupAccountName function accepts the name of a system and an
	 * account as input. It retrieves a security identifier (SID) for the
	 * account and the name of the domain on which the account was found.
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
	public boolean LookupAccountNameW(char[] lpSystemName,
			char[] lpAccountName, byte[] Sid, IntByReference cbSid,
			char[] ReferencedDomainName,
			IntByReference cchReferencedDomainName, PointerByReference peUse);

	/**
	 * Convert a security identifier (SID) to a string format suitable for display, 
	 * storage, or transmission.
	 * 
	 * @param sid
	 *            The SID structure to be converted.
	 * @param stringSid
	 *            Pointer to a variable that receives a pointer to a
	 *            null-terminated SID string. To free the returned buffer, call
	 *            the LocalFree function.
	 * @return True if the function was successful, False otherwise.
	 */
	public boolean ConvertSidToStringSidW(byte[] Sid,
			PointerByReference StringSid);	

	/**
	 * Convert a string-format security identifier (SID) into a valid, functional SID.
	 * @param StringSid The string-format SID to convert.
	 * @param Sid Receives a pointer to the converted SID.
	 * @return True if the function was successful, False otherwise.
	 */
	public boolean ConvertStringSidToSidW(char[] StringSid,
			PointerByReference Sid);
	
	/**
	 * Returns the length, in bytes, of a valid security identifier (SID).
	 * @param sid A pointer to the SID structure whose length is returned.
	 * @return Length of the SID.
	 */
	public int GetLengthSid(PointerByReference sid);
}
