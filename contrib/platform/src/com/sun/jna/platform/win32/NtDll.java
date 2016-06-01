/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * ntdll.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface NtDll extends StdCallLibrary {
	
	NtDll INSTANCE = Native.loadLibrary("NtDll", NtDll.class, W32APIOptions.DEFAULT_OPTIONS);

	/**
	 * The ZwQueryKey routine provides information about the class of a registry key, 
	 * and the number and sizes of its subkeys.
	 * @param KeyHandle
	 *  Handle to the registry key to obtain information about. This handle is created by 
	 *  a successful call to ZwCreateKey or ZwOpenKey. 
	 * @param KeyInformationClass
	 *  Specifies a KEY_INFORMATION_CLASS value that determines the type of information 
	 *  returned in the KeyInformation buffer. 
	 * @param KeyInformation
	 *  Pointer to a caller-allocated buffer that receives the requested information.
	 * @param Length
	 *  Specifies the size, in bytes, of the KeyInformation buffer. 
	 * @param ResultLength
	 *  Pointer to a variable that receives the size, in bytes, of the requested key 
	 *  information. If ZwQueryKey returns STATUS_SUCCESS, the variable contains the amount 
	 *  of data returned. If ZwQueryKey returns STATUS_BUFFER_OVERFLOW or 
	 *  STATUS_BUFFER_TOO_SMALL, you can use the value of the variable to determine the 
	 *  required buffer size. 
	 * @return
	 *  ZwQueryKey returns STATUS_SUCCESS on success, or the appropriate error code on failure.
	 */
	public int ZwQueryKey(HANDLE KeyHandle, int KeyInformationClass,
			Structure KeyInformation, int Length, IntByReference ResultLength);

    /**
     * The NtSetSecurityObject routine sets an object's security state.
     * @param handle [in]
     *  Handle for the object whose security state is to be set. This handle must have the access
     *  specified in the Meaning column of the table shown in the description of the
     *  SecurityInformation parameter.
     * @param SecurityInformation [in]
     * SECURITY_INFORMATION value specifying the information to be set. Can be a combination of
     *  one or more of the following:
     *   DACL_SECURITY_INFORMATION
     *    Indicates the discretionary access control list (DACL) of the object is to be set. Requires WRITE_DAC access.
     *   GROUP_SECURITY_INFORMATION
     *    Indicates the primary group identifier of the object is to be set. Requires WRITE_OWNER access.
     *   OWNER_SECURITY_INFORMATION
     *    Indicates the owner identifier of the object is to be set. Requires WRITE_OWNER access.
     *   SACL_SECURITY_INFORMATION
     *    Indicates the system ACL (SACL) of the object is to be set. Requires ACCESS_SYSTEM_SECURITY access.
     * @param pSecurityDescriptor [in]
     *  Pointer to the security descriptor to be set for the object.
     * @return
     *  NtSetSecurityObject returns STATUS_SUCCESS or an appropriate error status.
     */
    public int NtSetSecurityObject(HANDLE handle, int SecurityInformation, Pointer pSecurityDescriptor);

    /**
     * The NtQuerySecurityObject routine retrieves a copy of an object's security descriptor.
     *
     * @param handle [in]
     *  Handle for the object whose security descriptor is to be queried. This handle must have the access specified
     *  in the Meaning column of the table shown in the description of the SecurityInformation parameter.
     * @param SecurityInformation [in]
     *  Pointer to a SECURITY_INFORMATION value specifying the information to be queried. Can be a combination of
     *  one or more of the following:
     *   DACL_SECURITY_INFORMATION
     *    Indicates the discretionary access control list (DACL) of the object is to be set. Requires WRITE_DAC access.
     *   GROUP_SECURITY_INFORMATION
     *    Indicates the primary group identifier of the object is to be set. Requires WRITE_OWNER access.
     *   OWNER_SECURITY_INFORMATION
     *    Indicates the owner identifier of the object is to be set. Requires WRITE_OWNER access.
     *   SACL_SECURITY_INFORMATION
     *    Indicates the system ACL (SACL) of the object is to be set. Requires ACCESS_SYSTEM_SECURITY access.
     * @param SecurityDescriptor [out]
     *  Pointer to the security descriptor to be set for the object.
     * @param Length [in]
     *  Size, in bytes, of the buffer pointed to by SecurityDescriptor.
     * @param LengthNeeded [in]
     *  Pointer to a caller-allocated variable that receives the number of bytes required to store the copied security descriptor.
     * @return
     *  NtQuerySecurityObject returns STATUS_SUCCESS or an appropriate error status.
     */
    public int NtQuerySecurityObject(HANDLE handle, int SecurityInformation, Pointer SecurityDescriptor, long Length, LongByReference LengthNeeded);
}
