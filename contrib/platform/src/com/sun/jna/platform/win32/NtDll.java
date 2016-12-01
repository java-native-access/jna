/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
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
    public int NtQuerySecurityObject(HANDLE handle, int SecurityInformation, Pointer SecurityDescriptor, int Length, IntByReference LengthNeeded);

    /**
     * Converts the specified NTSTATUS code to its equivalent system error code.
     * @param Status [in]
     *  The NTSTATUS code to be converted.
     * @return The function returns the corresponding system error code. ERROR_MR_MID_NOT_FOUND is returned when the specified NTSTATUS code
     *  does not have a corresponding system error code.
     */
    public int RtlNtStatusToDosError(int Status);
}
