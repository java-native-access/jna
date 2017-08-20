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
import com.sun.jna.platform.win32.WinBase.FE_EXPORT_FUNC;
import com.sun.jna.platform.win32.WinBase.FE_IMPORT_FUNC;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.ACL;
import com.sun.jna.platform.win32.WinNT.GENERIC_MAPPING;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PACLByReference;
import com.sun.jna.platform.win32.WinNT.PRIVILEGE_SET;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.PSIDByReference;
import com.sun.jna.platform.win32.WinNT.SECURITY_DESCRIPTOR;
import com.sun.jna.platform.win32.WinNT.SECURITY_DESCRIPTOR_RELATIVE;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.platform.win32.Winsvc.ChangeServiceConfig2Info;
import com.sun.jna.platform.win32.Winsvc.HandlerEx;
import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS_HANDLE;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS_PROCESS;
import com.sun.jna.platform.win32.Winsvc.SERVICE_TABLE_ENTRY;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Advapi32.dll Interface.
 *
 * @author dblock[at]dblock.org
 */
public interface Advapi32 extends StdCallLibrary {
    Advapi32 INSTANCE = Native.loadLibrary("Advapi32", Advapi32.class, W32APIOptions.DEFAULT_OPTIONS);

    int MAX_KEY_LENGTH = 255;
    int MAX_VALUE_NAME = 16383;

    int RRF_RT_ANY = 0x0000ffff;
    int RRF_RT_DWORD = 0x00000018;
    int RRF_RT_QWORD = 0x00000048;
    int RRF_RT_REG_BINARY = 0x00000008;
    int RRF_RT_REG_DWORD = 0x00000010;
    int RRF_RT_REG_EXPAND_SZ = 0x00000004;
    int RRF_RT_REG_MULTI_SZ = 0x00000020;
    int RRF_RT_REG_NONE = 0x00000001;
    int RRF_RT_REG_QWORD = 0x00000040;
    int RRF_RT_REG_SZ = 0x00000002;

    /**
     * LOGON_WITH_PROFILE: 0x00000001<br>
     * Log on, then load the user profile in the HKEY_USERS registry key.<br>
     * The function returns after the profile is loaded. <br>
     * Loading the profile can be time-consuming, so it is best to use this
     * value only if you must access the information in the HKEY_CURRENT_USER
     * registry key.<br>
     * Windows Server 2003: The profile is unloaded after the new process is
     * terminated, whether or not it has created child processes.<br>
     * Windows XP: The profile is unloaded after the new process and all child
     * processes it has created are terminated.<br>
     */
    int LOGON_WITH_PROFILE = 0x00000001;

    /**
     * LOGON_NETCREDENTIALS_ONLY: 0x00000002<br>
     * Log on, but use the specified credentials on the network only.<br>
     * The new process uses the same token as the caller, but the system creates
     * a new logon session within LSA, and the process uses the specified
     * credentials as the default credentials. <br>
     * This value can be used to create a process that uses a different set of
     * credentials locally than it does remotely.<br>
     * This is useful in inter-domain scenarios where there is no trust
     * relationship.<br>
     * The system does not validate the specified credentials.<br>
     * Therefore, the process can start, but it may not have access to network
     * resources.
     */
    int LOGON_NETCREDENTIALS_ONLY = 0x00000002;

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
    boolean GetUserNameW(char[] buffer, IntByReference len);

    /**
     * Accepts the name of a system and anaccount as input and retrieves a
     * security identifier (SID) for the account and the name of the domain on
     * which the account was found.
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
    boolean LookupAccountName(String lpSystemName, String lpAccountName,
                              PSID Sid, IntByReference cbSid, char[] ReferencedDomainName,
                              IntByReference cchReferencedDomainName, PointerByReference peUse);

    /**
     * Retrieves the name of the account for this SID and the name of the first
     * domain on which this SID is found.
     *
     * @param lpSystemName
     *            Specifies the target computer.
     * @param Sid
     *            The SID to look up.
     * @param lpName
     *            Buffer that receives a null-terminated string that contains
     *            the account name that corresponds to the lpSid parameter.
     * @param cchName
     *            On input, specifies the size, in TCHARs, of the lpName buffer.
     *            If the function fails because the buffer is too small or if
     *            cchName is zero, cchName receives the required buffer size,
     *            including the terminating null character.
     * @param ReferencedDomainName
     *            Pointer to a buffer that receives a null-terminated string
     *            that contains the name of the domain where the account name
     *            was found.
     * @param cchReferencedDomainName
     *            On input, specifies the size, in TCHARs, of the
     *            lpReferencedDomainName buffer. If the function fails because
     *            the buffer is too small or if cchReferencedDomainName is zero,
     *            cchReferencedDomainName receives the required buffer size,
     *            including the terminating null character.
     * @param peUse
     *            Pointer to a variable that receives a SID_NAME_USE value that
     *            indicates the type of the account.
     * @return If the function succeeds, the function returns nonzero. If the
     *         function fails, it returns zero. To get extended error
     *         information, call GetLastError.
     */
    boolean LookupAccountSid(String lpSystemName, PSID Sid,
                             char[] lpName, IntByReference cchName, char[] ReferencedDomainName,
                             IntByReference cchReferencedDomainName, PointerByReference peUse);

    /**
     * Convert a security identifier (SID) to a string format suitable for
     * display, storage, or transmission.
     *
     * @param Sid
     *            The SID structure to be converted.
     * @param StringSid
     *            Pointer to a variable that receives a pointer to a
     *            null-terminated SID string. To free the returned buffer, call
     *            the LocalFree function.
     * @return {@code true} if the function was successful - call {@code GetLastError()}
     * to check failure reason
     * @see <A HREF="http://msdn.microsoft.com/en-us/library/aa376399(VS.85).aspx">ConvertSidToStringSid</A>
     */
    boolean ConvertSidToStringSid(PSID Sid, PointerByReference StringSid);

    /**
     * Convert a string-format security identifier (SID) into a valid,
     * functional SID.
     *
     *
     * @param StringSid
     *            The string-format SID to convert.
     * @param Sid
     *            Receives a pointer to the converted SID. To free the returned buffer, call
     *            the LocalFree function.
     * @return {@code true} if the function was successful - call {@code GetLastError()}
     * to check failure reason
     * @see <A HREF="http://msdn.microsoft.com/en-us/library/aa376402(VS.85).aspx">ConvertStringSidToSid</A>
     */
    boolean ConvertStringSidToSid(String StringSid, PSIDByReference Sid);

    /**
     * Returns the length, in bytes, of a valid security identifier (SID).
     * http://msdn.microsoft.com/en-us/library/aa446642(VS.85).aspx
     *
     * @param pSid
     *            A pointer to the SID structure whose length is returned.
     * @return Length of the SID.
     */
    int GetLengthSid(PSID pSid);

    /**
     * The IsValidSid function validates a security identifier (SID) by
     * verifying that the revision number is within a known range, and that the
     * number of subauthorities is less than the maximum.
     *
     * @param pSid
     *            Pointer to the SID structure to validate. This parameter
     *            cannot be NULL.
     * @return If the SID structure is valid, the return value is nonzero. If
     *         the SID structure is not valid, the return value is zero. There
     *         is no extended error information for this function; do not call
     *         GetLastError.
     */
    boolean IsValidSid(PSID pSid);

    /**
     * he EqualSid function tests two security identifier (SID) values for equality.
     * Two SIDs must match exactly to be considered equal.
     * @param pSid1
     *             A pointer to the first SID structure to compare. This structure is assumed to be valid.
     * @param pSid2
     *             A pointer to the second SID structure to compare. This structure is assumed to be valid.
     * @return If the SID structures are equal, the return value is nonzero.
     *         If the SID structures are not equal, the return value is zero. To get extended error
     *         information, call GetLastError.
     *         If either SID structure is not valid, the return value is undefined.
     */
    boolean EqualSid(PSID pSid1, PSID pSid2);

    /**
     * Compares a SID to a well known SID and returns TRUE if they match.
     *
     * @param pSid
     *            SID to test.
     * @param wellKnownSidType
     *            Member of the WELL_KNOWN_SID_TYPE enumeration to compare with
     *            the SID at pSid.
     * @return True if the SID is of a given well known type, false otherwise.
     */
    boolean IsWellKnownSid(PSID pSid, int wellKnownSidType);

    /**
     * The CreateWellKnownSid function creates a SID for predefined aliases.
     *
     * @param wellKnownSidType
     *            Member of the WELL_KNOWN_SID_TYPE enumeration that specifies
     *            what the SID will identify.
     * @param domainSid
     *            Pointer to a SID that identifies the domain control to use
     *            when creating the SID. Pass NULL to use the local computer.
     * @param pSid
     *            Pointer to memory where CreateWellKnownSid will store the new
     *            SID.
     * @param cbSid
     *            Pointer to a DWORD that contains the number of bytes available
     *            at pSid. The CreateWellKnownSid function stores the number of
     *            bytes actually used at this location.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean CreateWellKnownSid(int wellKnownSidType, PSID domainSid,
                               PSID pSid, IntByReference cbSid);

    /**
     * The InitializeSecurityDescriptor function initializes a new security descriptor.
     * @param pSecurityDescriptor
     *              A pointer to a SECURITY_DESCRIPTOR structure that the function initializes.
     * @param dwRevision
     *              The revision level to assign to the security descriptor. This parameter
     *              must be SECURITY_DESCRIPTOR_REVISION.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean InitializeSecurityDescriptor(SECURITY_DESCRIPTOR pSecurityDescriptor, int dwRevision);

    /**
     * The GetSecurityDescriptorControl function retrieves a security descriptor control and revision information.
     * @param pSecurityDescriptor
     *              A pointer to a SECURITY_DESCRIPTOR structure whose control and revision
     *              information the function retrieves.
     * @param pControl
     *              A pointer to a SECURITY_DESCRIPTOR_CONTROL structure that receives the security descriptor's
     *              control information.
     * @param lpdwRevision
     *              A pointer to a variable that receives the security descriptor's revision value.
     *              This value is always set, even when GetSecurityDescriptorControl returns an error.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean GetSecurityDescriptorControl(SECURITY_DESCRIPTOR pSecurityDescriptor, ShortByReference pControl, IntByReference lpdwRevision);

    /**
     * The SetSecurityDescriptorControl function sets the control bits of a security descriptor. The function can set only the control
     * bits that relate to automatic inheritance of ACEs. To set the other control bits of a security descriptor, use the functions,
     * such as SetSecurityDescriptorDacl, for modifying the components of a security descriptor.
     * @param pSecurityDescriptor
     *              A pointer to a SECURITY_DESCRIPTOR structure whose control and revision information are set.
     * @param ControlBitsOfInterest
     *              A SECURITY_DESCRIPTOR_CONTROL mask that indicates the control bits to set.
     * @param ControlBitsToSet
     *               SECURITY_DESCRIPTOR_CONTROL mask that indicates the new values for the control bits specified by the ControlBitsOfInterest mask.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean SetSecurityDescriptorControl(SECURITY_DESCRIPTOR pSecurityDescriptor, short ControlBitsOfInterest, short ControlBitsToSet);

    /**
     * The GetSecurityDescriptorOwner function retrieves the owner information from a security descriptor.
     * @param pSecurityDescriptor
     *          A pointer to a SECURITY_DESCRIPTOR structure whose owner information the function retrieves.
     * @param pOwner
     *          A pointer to a pointer to a security identifier (SID) that identifies the owner when the function returns.
     *          If the security descriptor does not contain an owner, the function sets the pointer pointed to by pOwner
     *          to NULL and ignores the remaining output parameter, lpbOwnerDefaulted. If the security descriptor contains an owner,
     *          the function sets the pointer pointed to by pOwner to the address of the security descriptor's owner SID
     *          and provides a valid value for the variable pointed to by lpbOwnerDefaulted.
     * @param lpbOwnerDefaulted
     *          A pointer to a flag that is set to the value of the SE_OWNER_DEFAULTED flag in the SECURITY_DESCRIPTOR_CONTROL
     *          structure when the function returns. If the value stored in the variable pointed to by the pOwner parameter is
     *          NULL, no value is set.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean GetSecurityDescriptorOwner(SECURITY_DESCRIPTOR pSecurityDescriptor, PSIDByReference pOwner, BOOLByReference lpbOwnerDefaulted);

    /**
     * The SetSecurityDescriptorOwner function sets the owner information of an absolute-format security descriptor. It replaces
     * any owner information already present in the security descriptor.
     * @param pSecurityDescriptor
     *          A pointer to the SECURITY_DESCRIPTOR structure whose owner is set by this function. The function replaces any existing
     *          owner with the new owner.
     * @param pOwner
     *          A pointer to a SID structure for the security descriptor's new primary owner. The SID structure is referenced by, not
     *          copied into, the security descriptor. If this parameter is NULL, the function clears the security descriptor's owner
     *          information. This marks the security descriptor as having no owner.
     * @param bOwnerDefaulted
     *          Indicates whether the owner information is derived from a default mechanism. If this value is TRUE, it is default information.
     *          The function stores this value as the SE_OWNER_DEFAULTED flag in the SECURITY_DESCRIPTOR_CONTROL structure. If this parameter
     *          is zero, the SE_OWNER_DEFAULTED flag is cleared.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean SetSecurityDescriptorOwner(SECURITY_DESCRIPTOR pSecurityDescriptor, PSID pOwner, boolean bOwnerDefaulted);

    /**
     * The GetSecurityDescriptorGroup function retrieves the primary group information from a security descriptor.
     * @param pSecurityDescriptor
     *          A pointer to a SECURITY_DESCRIPTOR structure whose primary group information the function retrieves.
     * @param pGroup
     *          A pointer to a pointer to a security identifier (SID) that identifies the primary group when the function
     *          returns. If the security descriptor does not contain a primary group, the function sets the pointer
     *          pointed to by pGroup to NULL and ignores the remaining output parameter, lpbGroupDefaulted. If the
     *          security descriptor contains a primary group, the function sets the pointer pointed to by pGroup to the
     *          address of the security descriptor's group SID and provides a valid value for the variable pointed to
     *          by lpbGroupDefaulted.
     * @param lpbGroupDefaulted
     *          A pointer to a flag that is set to the value of the SE_GROUP_DEFAULTED flag in the
     *          SECURITY_DESCRIPTOR_CONTROL structure when the function returns. If the value stored in the variable
     *          pointed to by the pGroup parameter is NULL, no value is set.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean GetSecurityDescriptorGroup(SECURITY_DESCRIPTOR pSecurityDescriptor, PSIDByReference pGroup, BOOLByReference lpbGroupDefaulted);

    /**
     * The SetSecurityDescriptorGroup function sets the primary group information of an absolute-format security descriptor, replacing
     * any primary group information already present in the security descriptor.
     * @param pSecurityDescriptor
     *          A pointer to the SECURITY_DESCRIPTOR structure whose primary group is set by this function. The function replaces
     *          any existing primary group with the new primary group.
     * @param pGroup
     *          A pointer to a SID structure for the security descriptor's new primary group. The SID structure is referenced by, not copied
     *          into, the security descriptor. If this parameter is NULL, the function clears the security descriptor's primary group
     *          information. This marks the security descriptor as having no primary group.
     * @param bGroupDefaulted
     *          Indicates whether the primary group information was derived from a default mechanism. If this value is TRUE, it is default
     *          information, and the function stores this value as the SE_GROUP_DEFAULTED flag in the SECURITY_DESCRIPTOR_CONTROL structure.
     *          If this parameter is zero, the SE_GROUP_DEFAULTED flag is cleared.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean SetSecurityDescriptorGroup(SECURITY_DESCRIPTOR pSecurityDescriptor, PSID pGroup, boolean bGroupDefaulted);

    /**
     * The GetSecurityDescriptorDacl function retrieves a pointer to the discretionary access control list (DACL) in
     * a specified security descriptor.
     * @param pSecurityDescriptor
     *              A pointer to the SECURITY_DESCRIPTOR structure that contains the DACL. The function retrieves a pointer to it.
     * @param bDaclPresent
     *              A pointer to a value that indicates the presence of a DACL in the specified security descriptor. If
     *              lpbDaclPresent is TRUE, the security descriptor contains a DACL, and the remaining output parameters in this
     *              function receive valid values. If lpbDaclPresent is FALSE, the security descriptor does not contain a DACL,
     *              and the remaining output parameters do not receive valid values. A value of TRUE for lpbDaclPresent does not
     *              mean that pDacl is not NULL. That is, lpbDaclPresent can be TRUE while pDacl is NULL, meaning that a NULL
     *              DACL is in effect. A NULL DACL implicitly allows all access to an object and is not the same as an empty DACL.
     *              An empty DACL permits no access to an object. For information about creating a proper DACL, see Creating a DACL.
     * @param pDacl
     *              A pointer to a pointer to an access control list (ACL). If a DACL exists, the function sets the pointer pointed
     *              to by pDacl to the address of the security descriptor's DACL. If a DACL does not exist, no value is stored.
     *              If the function stores a NULL value in the pointer pointed to by pDacl, the security descriptor has a NULL DACL.
     *              A NULL DACL implicitly allows all access to an object.
     *              If an application expects a non-NULL DACL but encounters a NULL DACL, the application should fail securely and
     *              not allow access.
     * @param bDaclDefaulted
     *              A pointer to a flag set to the value of the SE_DACL_DEFAULTED flag in the SECURITY_DESCRIPTOR_CONTROL structure
     *              if a DACL exists for the security descriptor. If this flag is TRUE, the DACL was retrieved by a default mechanism;
     *              if FALSE, the DACL was explicitly specified by a user.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean GetSecurityDescriptorDacl(SECURITY_DESCRIPTOR pSecurityDescriptor, BOOLByReference bDaclPresent, PACLByReference pDacl, BOOLByReference bDaclDefaulted);

    /**
     * The SetSecurityDescriptorDacl function sets information in a discretionary access control list (DACL).
     * If a DACL is already present in the security descriptor, the DACL is replaced.
     * @param pSecurityDescriptor
     *              A pointer to the SECURITY_DESCRIPTOR structure to which the function adds the DACL. This
     *              security descriptor must be in absolute format, meaning that its members must be pointers
     *              to other structures, rather than offsets to contiguous data.
     * @param bDaclPresent
     *              A flag that indicates the presence of a DACL in the security descriptor. If this parameter
     *              is TRUE, the function sets the SE_DACL_PRESENT flag in the SECURITY_DESCRIPTOR_CONTROL
     *              structure and uses the values in the pDacl and bDaclDefaulted parameters. If this parameter
     *              is FALSE, the function clears the SE_DACL_PRESENT flag, and pDacl and bDaclDefaulted are ignored.
     * @param pDacl
     *              A pointer to an ACL structure that specifies the DACL for the security descriptor. If this
     *              parameter is NULL, a NULL DACL is assigned to the security descriptor, which allows all access
     *              to the object. The DACL is referenced by, not copied into, the security descriptor.
     * @param bDaclDefaulted
     *              A flag that indicates the source of the DACL. If this flag is TRUE, the DACL has been retrieved
     *              by some default mechanism. If FALSE, the DACL has been explicitly specified by a user. The function
     *              stores this value in the SE_DACL_DEFAULTED flag of the SECURITY_DESCRIPTOR_CONTROL structure. If
     *              this parameter is not specified, the SE_DACL_DEFAULTED flag is cleared.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean SetSecurityDescriptorDacl(SECURITY_DESCRIPTOR pSecurityDescriptor, boolean bDaclPresent, ACL pDacl, boolean bDaclDefaulted);

    /**
     * The InitializeAcl function initializes a new ACL structure.
     * @param pAcl
     *              A pointer to an ACL structure to be initialized by this function.
     *              Allocate memory for pAcl before calling this function.
     * @param nAclLength
     *              The length, in bytes, of the buffer pointed to by the pAcl parameter. This value
     *              must be large enough to contain the ACL header and all of the access control
     *              entries (ACEs) to be stored in the ACL. In addition, this value must be
     *              DWORD-aligned. For more information about calculating the size of an ACL,
     *              see Remarks.
     * @param dwAclRevision
     *              The revision level of the ACL structure being created. This value can be ACL_REVISION
     *              or ACL_REVISION_DS. Use ACL_REVISION_DS if the access control list (ACL) supports
     *              object-specific ACEs.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean InitializeAcl(ACL pAcl, int nAclLength, int dwAclRevision);

    /**
     * The AddAce function adds one or more access control entries (ACEs) to a specified access control list (ACL).
     * @param pAcl
     *          A pointer to an ACL. This function adds an ACE to this ACL.
     * @param dwAceRevision
     *          Specifies the revision level of the ACL being modified. This value can be ACL_REVISION or
     *          ACL_REVISION_DS. Use ACL_REVISION_DS if the ACL contains object-specific ACEs. This value
     *          must be compatible with the AceType field of all ACEs in pAceList. Otherwise, the function
     *          will fail and set the last error to ERROR_INVALID_PARAMETER.
     * @param dwStartingAceIndex
     *          Specifies the position in the ACL's list of ACEs at which to add new ACEs. A value of zero
     *          inserts the ACEs at the beginning of the list. A value of MAXDWORD appends the ACEs to the end
     *          of the list.
     * @param pAceList
     *          A pointer to a list of one or more ACEs to be added to the specified ACL. The ACEs in the list
     *          must be stored contiguously.
     * @param nAceListLength
     *          Specifies the size, in bytes, of the input buffer pointed to by the pAceList parameter.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean AddAce(ACL pAcl, int dwAceRevision, int dwStartingAceIndex, Pointer pAceList, int nAceListLength);

    /**
     * The AddAce function adds one or more access control entries (ACEs) to a specified access control list (ACL).
     * @param pAcl
     *          A pointer to an ACL. This function adds an access-allowed ACE to the end of this ACL.
     *          The ACE is in the form of an ACCESS_ALLOWED_ACE structure.
     * @param dwAceRevision
     *          Specifies the revision level of the ACL being modified. This value can be ACL_REVISION or
     *          ACL_REVISION_DS. Use ACL_REVISION_DS if the ACL contains object-specific ACEs.
     * @param AccessMask
     *          Specifies the mask of access rights to be granted to the specified SID.
     * @param pSid
     *          A pointer to the SID representing a user, group, or logon account being granted access.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean AddAccessAllowedAce(ACL pAcl, int dwAceRevision, int AccessMask, PSID pSid);

    /**
     * The AddAce function adds one or more access control entries (ACEs) to a specified access control list (ACL).
     * @param pAcl
     *          A pointer to an ACL. This function adds an access-allowed ACE to the end of this ACL.
     *          The ACE is in the form of an ACCESS_ALLOWED_ACE structure.
     * @param dwAceRevision
     *          Specifies the revision level of the ACL being modified. This value can be ACL_REVISION or
     *          ACL_REVISION_DS. Use ACL_REVISION_DS if the ACL contains object-specific ACEs.
     * @param AceFlags
     *          A set of bit flags that control ACE inheritance. The function sets these flags in the AceFlags
     *          member of the ACE_HEADER structure of the new ACE. This parameter can be a combination
     *          of the following values: CONTAINER_INHERIT_ACE, INHERIT_ONLY_ACE, INHERITED_ACE,
     *          NO_PROPAGATE_INHERIT_ACE, and OBJECT_INHERIT_ACE
     * @param AccessMask
     *          Specifies the mask of access rights to be granted to the specified SID.
     * @param pSid
     *          A pointer to the SID representing a user, group, or logon account being granted access.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean AddAccessAllowedAceEx(ACL pAcl, int dwAceRevision, int AceFlags, int AccessMask, PSID pSid);

    /**
     * The GetAce function obtains a pointer to an access control entry (ACE) in an access
     * control list (ACL).
     * @param pAcl
     *          A pointer to an ACL that contains the ACE to be retrieved.
     * @param dwAceIndex
     *          The index of the ACE to be retrieved. A value of zero corresponds to the first ACE in
     *          the ACL, a value of one to the second ACE, and so on.
     * @param pAce
     *          A pointer to a pointer that the function sets to the address of the ACE.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. For extended error
     *         information, call GetLastError.
     */
    boolean GetAce(ACL pAcl, int dwAceIndex, PointerByReference pAce);

    /**
     * The LogonUser function attempts to log a user on to the local computer.
     * The local computer is the computer from which LogonUser was called. You
     * cannot use LogonUser to log on to a remote computer. You specify the user
     * with a user name and domain, and authenticate the user with a plaintext
     * password. If the function succeeds, you receive a handle to a token that
     * represents the logged-on user. You can then use this token handle to
     * impersonate the specified user or, in most cases, to create a process
     * that runs in the context of the specified user.
     *
     * @param lpszUsername
     *            A pointer to a null-terminated string that specifies the name
     *            of the user. This is the name of the user account to log on
     *            to. If you use the user principal name (UPN) format,
     *            user@DNS_domain_name, the lpszDomain parameter must be NULL.
     * @param lpszDomain
     *            A pointer to a null-terminated string that specifies the name
     *            of the domain or server whose account database contains the
     *            lpszUsername account. If this parameter is NULL, the user name
     *            must be specified in UPN format. If this parameter is ".", the
     *            function validates the account using only the local account
     *            database.
     * @param lpszPassword
     *            A pointer to a null-terminated string that specifies the
     *            plaintext password for the user account specified by
     *            lpszUsername.
     * @param logonType
     *            The type of logon operation to perform.
     * @param logonProvider
     *            Specifies the logon provider.
     * @param phToken
     *            A pointer to a handle variable that receives a handle to a
     *            token that represents the specified user.
     * @return If the function succeeds, the function returns nonzero. If the
     *         function fails, it returns zero. To get extended error
     *         information, call GetLastError.
     */
    boolean LogonUser(String lpszUsername, String lpszDomain,
                      String lpszPassword, int logonType, int logonProvider,
                      HANDLEByReference phToken);

    /**
     * The OpenThreadToken function opens the access token associated with a
     * thread.
     *
     * @param ThreadHandle
     *            Handle to the thread whose access token is opened.
     * @param DesiredAccess
     *            Specifies an access mask that specifies the requested types of
     *            access to the access token. These requested access types are
     *            reconciled against the token's discretionary access control
     *            list (DACL) to determine which accesses are granted or denied.
     * @param OpenAsSelf
     *            Indicates whether the access check is to be made against the
     *            security context of the thread calling the OpenThreadToken
     *            function or against the security context of the process for
     *            the calling thread.
     * @param TokenHandle
     *            Pointer to a variable that receives the handle to the newly
     *            opened access token.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean OpenThreadToken(HANDLE ThreadHandle, int DesiredAccess,
                            boolean OpenAsSelf, HANDLEByReference TokenHandle);

    /**
     * The SetThreadToken function assigns an impersonation token to a thread.
     * The function can also cause a thread to stop using an impersonation token.
     * @param ThreadHandle [in, optional]
     *                     A pointer to a handle to the thread to which the function
     *                     assigns the impersonation token. If ThreadHandle is NULL, the
     *                     function assigns the impersonation token to the calling thread.
     * @param TokenHandle [in, optional]
     *                     A handle to the impersonation token to assign to the thread.
     *                     This handle must have been opened with TOKEN_IMPERSONATE access
     *                     rights. For more information, see Access Rights for Access-Token
     *                     Objects. If Token is NULL, the function causes the
     *                     thread to stop using an impersonation token.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean SetThreadToken(HANDLEByReference ThreadHandle, HANDLE TokenHandle);

    /**
     * The OpenProcessToken function opens the access token associated with a
     * process.
     *
     * @param ProcessHandle
     *            Handle to the process whose access token is opened. The
     *            process must have the PROCESS_QUERY_INFORMATION access
     *            permission.
     * @param DesiredAccess
     *            Specifies an access mask that specifies the requested types of
     *            access to the access token. These requested access types are
     *            compared with the discretionary access control list (DACL) of
     *            the token to determine which accesses are granted or denied.
     * @param TokenHandle
     *            Pointer to a handle that identifies the newly opened access
     *            token when the function returns.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean OpenProcessToken(HANDLE ProcessHandle, int DesiredAccess,
                             HANDLEByReference TokenHandle);

    /**
     * The DuplicateToken function creates a new access token that duplicates
     * one already in existence.
     *
     * @param ExistingTokenHandle
     *            Handle to an access token opened with TOKEN_DUPLICATE access.
     * @param ImpersonationLevel
     *            Specifies a SECURITY_IMPERSONATION_LEVEL enumerated type that
     *            supplies the impersonation level of the new token.
     * @param DuplicateTokenHandle
     *            Pointer to a variable that receives a handle to the duplicate
     *            token. This handle has TOKEN_IMPERSONATE and TOKEN_QUERY
     *            access to the new token.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean DuplicateToken(HANDLE ExistingTokenHandle,
                           int ImpersonationLevel, HANDLEByReference DuplicateTokenHandle);

    /**
     * The DuplicateTokenEx function creates a new access token that duplicates
     * an existing token. This function can create either a primary token or an
     * impersonation token.
     *
     * @param hExistingToken
     *            A handle to an access token opened with TOKEN_DUPLICATE
     *            access.
     * @param dwDesiredAccess
     *            Specifies the requested access rights for the new token.
     * @param lpTokenAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that specifies a
     *            security descriptor for the new token and determines whether
     *            child processes can inherit the token.
     * @param ImpersonationLevel
     *            Specifies a value from the SECURITY_IMPERSONATION_LEVEL
     *            enumeration that indicates the impersonation level of the new
     *            token.
     * @param TokenType
     *            Specifies one of the following values from the TOKEN_TYPE
     *            enumeration.
     * @param phNewToken
     *            A pointer to a HANDLE variable that receives the new token.
     * @return If the function succeeds, the function returns a nonzero value.
     *         If the function fails, it returns zero. To get extended error
     *         information, call GetLastError.
     */
    boolean DuplicateTokenEx(HANDLE hExistingToken, int dwDesiredAccess,
                             WinBase.SECURITY_ATTRIBUTES lpTokenAttributes,
                             int ImpersonationLevel, int TokenType, HANDLEByReference phNewToken);

    /**
     * Retrieves a specified type of information about an access token. The
     * calling process must have appropriate access rights to obtain the
     * information.
     *
     * @param tokenHandle
     *            Handle to an access token from which information is retrieved.
     *            If TokenInformationClass specifies TokenSource, the handle
     *            must have TOKEN_QUERY_SOURCE access. For all other
     *            TokenInformationClass values, the handle must have TOKEN_QUERY
     *            access.
     * @param tokenInformationClass
     *            Specifies a value from the TOKEN_INFORMATION_CLASS enumerated
     *            type to identify the type of information the function
     *            retrieves.
     * @param tokenInformation
     *            Pointer to a buffer the function fills with the requested
     *            information. The structure put into this buffer depends upon
     *            the type of information specified by the TokenInformationClass
     *            parameter.
     * @param tokenInformationLength
     *            Specifies the size, in bytes, of the buffer pointed to by the
     *            TokenInformation parameter. If TokenInformation is NULL, this
     *            parameter must be zero.
     * @param returnLength
     *            Pointer to a variable that receives the number of bytes needed
     *            for the buffer pointed to by the TokenInformation parameter.
     *            If this value is larger than the value specified in the
     *            TokenInformationLength parameter, the function fails and
     *            stores no data in the buffer.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GetTokenInformation(HANDLE tokenHandle,
                                int tokenInformationClass, Structure tokenInformation,
                                int tokenInformationLength, IntByReference returnLength);

    /**
     * The ImpersonateLoggedOnUser function lets the calling thread impersonate
     * the security context of a logged-on user. The user is represented by a
     * token handle.
     *
     * @param hToken
     *            Handle to a primary or impersonation access token that
     *            represents a logged-on user. This can be a token handle
     *            returned by a call to LogonUser, CreateRestrictedToken,
     *            DuplicateToken, DuplicateTokenEx, OpenProcessToken, or
     *            OpenThreadToken functions. If hToken is a primary token, it
     *            must have TOKEN_QUERY and TOKEN_DUPLICATE access. If hToken is
     *            an impersonation token, it must have TOKEN_QUERY and
     *            TOKEN_IMPERSONATE access.
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean ImpersonateLoggedOnUser(HANDLE hToken);

    /**
     * The ImpersonateSelf function obtains an access token that impersonates
     * the security context of the calling process. The token is assigned to the
     * calling thread.
     *
     * @param ImpersonationLevel
     *            Specifies a SECURITY_IMPERSONATION_LEVEL enumerated type that
     *            supplies the impersonation level of the new token.
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean ImpersonateSelf(int ImpersonationLevel);

    /**
     * The RevertToSelf function terminates the impersonation of a client
     * application.
     *
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean RevertToSelf();

    /**
     * The RegOpenKeyEx function opens the specified registry key. Note that key
     * names are not case sensitive.
     *
     * @param hKey
     *            Handle to an open key.
     * @param lpSubKey
     *            Pointer to a null-terminated string containing the name of the
     *            subkey to open.
     * @param ulOptions
     *            Reserved; must be zero.
     * @param samDesired
     *            Access mask that specifies the desired access rights to the
     *            key. The function fails if the security descriptor of the key
     *            does not permit the requested access for the calling process.
     * @param phkResult
     *            Pointer to a variable that receives a handle to the opened
     *            key. If the key is not one of the predefined registry keys,
     *            call the RegCloseKey function after you have finished using
     *            the handle.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegOpenKeyEx(HKEY hKey, String lpSubKey, int ulOptions,
                     int samDesired, HKEYByReference phkResult);

    /**
     * The RegQueryValueEx function retrieves the type and data for a specified
     * value name associated with an open registry key.
     *
     * @param hKey
     *            Handle to an open key. The key must have been opened with the
     *            KEY_QUERY_VALUE access right.
     * @param lpValueName
     *            Pointer to a null-terminated string containing the name of the
     *            value to query. If lpValueName is NULL or an empty string, "",
     *            the function retrieves the type and data for the key's unnamed
     *            or default value, if any.
     * @param lpReserved
     *            Reserved; must be NULL.
     * @param lpType
     *            Pointer to a variable that receives a code indicating the type
     *            of data stored in the specified value.
     * @param lpData
     *            Pointer to a buffer that receives the value's data. This
     *            parameter can be NULL if the data is not required. If the data
     *            is a string, the function checks for a terminating null
     *            character. If one is not found, the string is stored with a
     *            null terminator if the buffer is large enough to accommodate
     *            the extra character. Otherwise, the string is stored as is.
     * @param lpcbData
     *            Pointer to a variable that specifies the size of the buffer
     *            pointed to by the lpData parameter, in bytes. When the
     *            function returns, this variable contains the size of the data
     *            copied to lpData. The lpcbData parameter can be NULL only if
     *            lpData is NULL. If the data has the REG_SZ, REG_MULTI_SZ or
     *            REG_EXPAND_SZ type, this size includes any terminating null
     *            character or characters. If the buffer specified by lpData
     *            parameter is not large enough to hold the data, the function
     *            returns ERROR_MORE_DATA and stores the required buffer size in
     *            the variable pointed to by lpcbData. In this case, the
     *            contents of the lpData buffer are undefined. If lpData is
     *            NULL, and lpcbData is non-NULL, the function returns
     *            ERROR_SUCCESS and stores the size of the data, in bytes, in
     *            the variable pointed to by lpcbData. This enables an
     *            application to determine the best way to allocate a buffer for
     *            the value's data.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegQueryValueEx(HKEY hKey, String lpValueName, int lpReserved,
			IntByReference lpType, char[] lpData, IntByReference lpcbData);

    int RegQueryValueEx(HKEY hKey, String lpValueName, int lpReserved,
			IntByReference lpType, byte[] lpData, IntByReference lpcbData);

    int RegQueryValueEx(HKEY hKey, String lpValueName, int lpReserved,
			IntByReference lpType, IntByReference lpData,
			IntByReference lpcbData);

    int RegQueryValueEx(HKEY hKey, String lpValueName, int lpReserved,
			IntByReference lpType, LongByReference lpData,
			IntByReference lpcbData);

    int RegQueryValueEx(HKEY hKey, String lpValueName, int lpReserved,
			IntByReference lpType, Pointer lpData, IntByReference lpcbData);

    /**
     * The RegCloseKey function releases a handle to the specified registry key.
     *
     * @param hKey
     *            Handle to the open key to be closed. The handle must have been
     *            opened by the RegCreateKeyEx, RegOpenKeyEx, or
     *            RegConnectRegistry function.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegCloseKey(HKEY hKey);

    /**
     * The RegDeleteValue function removes a named value from the specified
     * registry key. Note that value names are not case sensitive.
     *
     * @param hKey
     *            Handle to an open key. The key must have been opened with the
     *            KEY_SET_VALUE access right.
     * @param lpValueName
     *            Pointer to a null-terminated string that names the value to
     *            remove. If this parameter is NULL or an empty string, the
     *            value set by the RegSetValue function is removed.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegDeleteValue(HKEY hKey, String lpValueName);

    /**
     * The RegSetValueEx function sets the data and type of a specified value
     * under a registry key.
     *
     * @param hKey
     *            Handle to an open key. The key must have been opened with the
     *            KEY_SET_VALUE access right.
     * @param lpValueName
     *            Pointer to a string containing the name of the value to set.
     *            If a value with this name is not already present in the key,
     *            the function adds it to the key. If lpValueName is NULL or an
     *            empty string, "", the function sets the type and data for the
     *            key's unnamed or default value.
     * @param Reserved
     *            Reserved; must be zero.
     * @param dwType
     *            Type of data pointed to by the lpData parameter.
     * @param lpData
     *            Pointer to a buffer containing the data to be stored with the
     *            specified value name.
     * @param cbData
     *            Size of the information pointed to by the lpData parameter, in
     *            bytes. If the data is of type REG_SZ, REG_EXPAND_SZ, or
     *            REG_MULTI_SZ, cbData must include the size of the terminating
     *            null character or characters.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegSetValueEx(HKEY hKey, String lpValueName, int Reserved,
                      int dwType, char[] lpData, int cbData);

    int RegSetValueEx(HKEY hKey, String lpValueName, int Reserved,
                      int dwType, byte[] lpData, int cbData);

    /**
     *
     * @param hKey registry key
     * @param lpSubKey subkey name
     * @param Reserved unused
     * @param lpClass class
     * @param dwOptions options
     * @param samDesired ?
     * @param lpSecurityAttributes security attributes
     * @param phkResult resulting key
     * @param lpdwDisposition ?
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegCreateKeyEx(HKEY hKey, String lpSubKey, int Reserved,
                       String lpClass, int dwOptions, int samDesired,
                       SECURITY_ATTRIBUTES lpSecurityAttributes,
                       HKEYByReference phkResult, IntByReference lpdwDisposition);

    /**
     *
     * @param hKey registry key
     * @param name key name
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegDeleteKey(HKEY hKey, String name);

    /**
     * The RegEnumKeyEx function enumerates subkeys of the specified open
     * registry key. The function retrieves information about one subkey each
     * time it is called.
     *
     * @param hKey
     *            Handle to an open key. The key must have been opened with the
     *            KEY_ENUMERATE_SUB_KEYS access right.
     * @param dwIndex
     *            Index of the subkey to retrieve. This parameter should be zero
     *            for the first call to the RegEnumKeyEx function and then
     *            incremented for subsequent calls. Because subkeys are not
     *            ordered, any new subkey will have an arbitrary index. This
     *            means that the function may return subkeys in any order.
     * @param lpName
     *            Pointer to a buffer that receives the name of the subkey,
     *            including the terminating null character. The function copies
     *            only the name of the subkey, not the full key hierarchy, to
     *            the buffer.
     * @param lpcName
     *            Pointer to a variable that specifies the size of the buffer
     *            specified by the lpName parameter, in TCHARs. This size should
     *            include the terminating null character. When the function
     *            returns, the variable pointed to by lpcName contains the
     *            number of characters stored in the buffer. The count returned
     *            does not include the terminating null character.
     * @param reserved
     *            Reserved; must be NULL.
     * @param lpClass
     *            Pointer to a buffer that receives the null-terminated class
     *            string of the enumerated subkey. This parameter can be NULL.
     * @param lpcClass
     *            Pointer to a variable that specifies the size of the buffer
     *            specified by the lpClass parameter, in TCHARs. The size should
     *            include the terminating null character. When the function
     *            returns, lpcClass contains the number of characters stored in
     *            the buffer. The count returned does not include the
     *            terminating null character. This parameter can be NULL only if
     *            lpClass is NULL.
     * @param lpftLastWriteTime
     *            Pointer to a variable that receives the time at which the
     *            enumerated subkey was last written.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegEnumKeyEx(HKEY hKey, int dwIndex, char[] lpName,
                     IntByReference lpcName, IntByReference reserved, char[] lpClass,
                     IntByReference lpcClass, WinBase.FILETIME lpftLastWriteTime);

    /**
     * The RegEnumValue function enumerates the values for the specified open
     * registry key. The function copies one indexed value name and data block
     * for the key each time it is called.
     *
     * @param hKey
     *            Handle to an open key. The key must have been opened with the
     *            KEY_QUERY_VALUE access right.
     * @param dwIndex
     *            Index of the value to be retrieved. This parameter should be
     *            zero for the first call to the RegEnumValue function and then
     *            be incremented for subsequent calls. Because values are not
     *            ordered, any new value will have an arbitrary index. This
     *            means that the function may return values in any order.
     * @param lpValueName
     *            Pointer to a buffer that receives the name of the value,
     *            including the terminating null character.
     * @param lpcchValueName
     *            Pointer to a variable that specifies the size of the buffer
     *            pointed to by the lpValueName parameter, in TCHARs. This size
     *            should include the terminating null character. When the
     *            function returns, the variable pointed to by lpcValueName
     *            contains the number of characters stored in the buffer. The
     *            count returned does not include the terminating null
     *            character.
     * @param reserved
     *            Reserved; must be NULL.
     * @param lpType
     *            Pointer to a variable that receives a code indicating the type
     *            of data stored in the specified value.
     * @param lpData
     *            Pointer to a buffer that receives the data for the value
     *            entry. This parameter can be NULL if the data is not required.
     * @param lpcbData
     *            Pointer to a variable that specifies the size of the buffer
     *            pointed to by the lpData parameter, in bytes.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegEnumValue(HKEY hKey, int dwIndex, char[] lpValueName,
                     IntByReference lpcchValueName, IntByReference reserved,
                     IntByReference lpType, byte[] lpData, IntByReference lpcbData);

    /**
     * The RegQueryInfoKey function retrieves information about the specified
     * registry key.
     *
     * @param hKey
     *            A handle to an open key. The key must have been opened with
     *            the KEY_QUERY_VALUE access right.
     * @param lpClass
     *            A pointer to a buffer that receives the null-terminated class
     *            string of the key. This parameter can be ignored. This
     *            parameter can be NULL.
     * @param lpcClass
     *            A pointer to a variable that specifies the size of the buffer
     *            pointed to by the lpClass parameter, in characters.
     * @param lpReserved
     *            Reserved; must be NULL.
     * @param lpcSubKeys
     *            A pointer to a variable that receives the number of subkeys
     *            that are contained by the specified key. This parameter can be
     *            NULL.
     * @param lpcMaxSubKeyLen
     *            A pointer to a variable that receives the size of the key's
     *            subkey with the longest name, in characters, not including the
     *            terminating null character. This parameter can be NULL.
     * @param lpcMaxClassLen
     *            A pointer to a variable that receives the size of the longest
     *            string that specifies a subkey class, in characters. The count
     *            returned does not include the terminating null character. This
     *            parameter can be NULL.
     * @param lpcValues
     *            A pointer to a variable that receives the number of values
     *            that are associated with the key. This parameter can be NULL.
     * @param lpcMaxValueNameLen
     *            A pointer to a variable that receives the size of the key's
     *            longest value name, in characters. The size does not include
     *            the terminating null character. This parameter can be NULL.
     * @param lpcMaxValueLen
     *            A pointer to a variable that receives the size of the longest
     *            data component among the key's values, in bytes. This
     *            parameter can be NULL.
     * @param lpcbSecurityDescriptor
     *            A pointer to a variable that receives the size of the key's
     *            security descriptor, in bytes. This parameter can be NULL.
     * @param lpftLastWriteTime
     *            A pointer to a FILETIME structure that receives the last write
     *            time. This parameter can be NULL.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If
     *         the function fails, the return value is a nonzero error code
     *         defined in Winerror.h.
     */
    int RegQueryInfoKey(HKEY hKey, char[] lpClass,
			IntByReference lpcClass, IntByReference lpReserved,
			IntByReference lpcSubKeys, IntByReference lpcMaxSubKeyLen,
			IntByReference lpcMaxClassLen, IntByReference lpcValues,
			IntByReference lpcMaxValueNameLen, IntByReference lpcMaxValueLen,
			IntByReference lpcbSecurityDescriptor,
			WinBase.FILETIME lpftLastWriteTime);

    /**
     * Retrieves the type and data for the specified registry value.
     *
     * @param hkey
     *            [in] A handle to an open registry key. The key must have been
     *            opened with the KEY_QUERY_VALUE access right. For more
     *            information, see Registry Key Security and Access Rights.
     *
     *            This handle is returned by the RegCreateKeyEx,
     *            RegCreateKeyTransacted, RegOpenKeyEx, or RegOpenKeyTransacted
     *            function. It can also be one of the following predefined keys:
     *
     *            HKEY_CLASSES_ROOT HKEY_CURRENT_CONFIG HKEY_CURRENT_USER
     *            HKEY_LOCAL_MACHINE HKEY_PERFORMANCE_DATA
     *            HKEY_PERFORMANCE_NLSTEXT HKEY_PERFORMANCE_TEXT HKEY_USERS
     *
     * @param lpSubKey
     *            [in, optional] The name of the registry key. This key must be
     *            a subkey of the key specified by the hkey parameter.
     *
     *            Key names are not case sensitive.
     *
     * @param lpValue
     *            [in, optional]
     *
     *            The name of the registry value.
     *
     *            If this parameter is NULL or an empty string, "", the function
     *            retrieves the type and data for the key's unnamed or default
     *            value, if any.
     *
     *            For more information, see Registry Element Size Limits.
     *
     *            Keys do not automatically have an unnamed or default value.
     *            Unnamed values can be of any type.
     *
     * @param dwFlags
     *            [in, optional]
     *
     *            The flags that restrict the data type of value to be queried.
     *            If the data type of the value does not meet this criteria, the
     *            function fails. This parameter can be one or more of the
     *            following values.
     *
     *            RRF_RT_ANY 0x0000ffff No type restriction. RRF_RT_DWORD
     *            0x00000018 Restrict type to 32-bit
     *            RRF_RT_REG_BINARY|RRF_RT_REG_DWORD. RRF_RT_QWORD 0x00000048
     *            Restrict type to 64-bit RRF_RT_REG_BINARY | RRF_RT_REG_DWORD.
     *            RRF_RT_REG_BINARY 0x00000008 Restrict type to REG_BINARY.
     *            RRF_RT_REG_DWORD 0x00000010 Restrict type to REG_DWORD.
     *            RRF_RT_REG_EXPAND_SZ 0x00000004 Restrict type to
     *            REG_EXPAND_SZ. RRF_RT_REG_MULTI_SZ 0x00000020 Restrict type to
     *            REG_MULTI_SZ. RRF_RT_REG_NONE 0x00000001 Restrict type to
     *            REG_NONE. RRF_RT_REG_QWORD 0x00000040 Restrict type to
     *            REG_QWORD. RRF_RT_REG_SZ 0x00000002 Restrict type to REG_SZ.
     *
     *            This parameter can also include one or more of the following
     *            values. RRF_NOEXPAND 0x10000000
     *
     *            Do not automatically expand environment strings if the value
     *            is of type REG_EXPAND_SZ.
     *
     *            RRF_ZEROONFAILURE 0x20000000
     *
     *            If pvData is not NULL, set the contents of the buffer to
     *            zeroes on failure.
     *
     * @param pdwType
     *            [out, optional]
     *
     *            A pointer to a variable that receives a code indicating the
     *            type of data stored in the specified value. For a list of the
     *            possible type codes, see Registry Value Types. This parameter
     *            can be NULL if the type is not required.
     *
     * @param pvData
     *            [out, optional]
     *
     *            A pointer to a buffer that receives the value's data. This
     *            parameter can be NULL if the data is not required.
     *
     *            If the data is a string, the function checks for a terminating
     *            null character. If one is not found, the string is stored with
     *            a null terminator if the buffer is large enough to accommodate
     *            the extra character. Otherwise, the function fails and returns
     *            ERROR_MORE_DATA.
     *
     * @param pcbData
     *            [in, out, optional]
     *
     *            A pointer to a variable that specifies the size of the buffer
     *            pointed to by the pvData parameter, in bytes. When the
     *            function returns, this variable contains the size of the data
     *            copied to pvData.
     *
     *            The pcbData parameter can be NULL only if pvData is NULL.
     *
     *            If the data has the REG_SZ, REG_MULTI_SZ or REG_EXPAND_SZ
     *            type, this size includes any terminating null character or
     *            characters. For more information, see Remarks.
     *
     *            If the buffer specified by pvData parameter is not large
     *            enough to hold the data, the function returns ERROR_MORE_DATA
     *            and stores the required buffer size in the variable pointed to
     *            by pcbData. In this case, the contents of the pvData buffer
     *            are undefined.
     *
     *            If pvData is NULL, and pcbData is non-NULL, the function
     *            returns ERROR_SUCCESS and stores the size of the data, in
     *            bytes, in the variable pointed to by pcbData. This enables an
     *            application to determine the best way to allocate a buffer for
     *            the value's data.
     *
     *            If hKey specifies HKEY_PERFORMANCE_DATA and the pvData buffer
     *            is not large enough to contain all of the returned data, the
     *            function returns ERROR_MORE_DATA and the value returned
     *            through the pcbData parameter is undefined. This is because
     *            the size of the performance data can change from one call to
     *            the next. In this case, you must increase the buffer size and
     *            call RegGetValue again passing the updated buffer size in the
     *            pcbData parameter. Repeat this until the function succeeds.
     *            You need to maintain a separate variable to keep track of the
     *            buffer size, because the value returned by pcbData is
     *            unpredictable.
     *
     *            Return value If the function succeeds, the return value is
     *            ERROR_SUCCESS. If the function fails, the return value is a
     *            system error code. If the pvData buffer is too small to
     *            receive the value, the function returns ERROR_MORE_DATA.
     * @return status
     */
    int RegGetValue(HKEY hkey, String lpSubKey, String lpValue,
                    int dwFlags, IntByReference pdwType, byte[] pvData,
                    IntByReference pcbData);

    /**
     * Retrieves a registered handle to the specified event log.
     *
     * @param lpUNCServerName
     *            The Universal Naming Convention (UNC) name of the remote
     *            server on which this operation is to be performed. If this
     *            parameter is NULL, the local computer is used.
     * @param lpSourceName
     *            The name of the event source whose handle is to be retrieved.
     *            The source name must be a subkey of a log under the Eventlog
     *            registry key. However, the Security log is for system use
     *            only.
     * @return If the function succeeds, the return value is a handle to the
     *         event log. If the function fails, the return value is NULL. To
     *         get extended error information, call GetLastError. The function
     *         returns ERROR_ACCESS_DENIED if lpSourceName specifies the
     *         Security event log.
     */
    HANDLE RegisterEventSource(String lpUNCServerName, String lpSourceName);

    /**
     * Closes the specified event log.
     *
     * @param hEventLog
     *            A handle to the event log. The RegisterEventSource function
     *            returns this handle.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean DeregisterEventSource(HANDLE hEventLog);

    /**
     * Opens a handle to the specified event log.
     *
     * @param lpUNCServerName
     *            The Universal Naming Convention (UNC) name of the remote
     *            server on which the event log is to be opened. If this
     *            parameter is NULL, the local computer is used.
     * @param lpSourceName
     *            The name of the log. If you specify a custom log and it cannot
     *            be found, the event logging service opens the Application log;
     *            however, there will be no associated message or category
     *            string file.
     * @return If the function succeeds, the return value is the handle to an
     *         event log. If the function fails, the return value is NULL. To
     *         get extended error information, call GetLastError.
     */
    HANDLE OpenEventLog(String lpUNCServerName, String lpSourceName);

    /**
     * Closes the specified event log.
     *
     * @param hEventLog
     *            A handle to the event log to be closed. The OpenEventLog or
     *            OpenBackupEventLog function returns this handle.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean CloseEventLog(HANDLE hEventLog);

    /**
     * Retrieves the number of records in the specified event log.
     *
     * @param hEventLog
     *            A handle to the open event log. The OpenEventLog or
     *            OpenBackupEventLog function returns this handle.
     * @param NumberOfRecords
     *            A pointer to a variable that receives the number of records in
     *            the specified event log.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GetNumberOfEventLogRecords(HANDLE hEventLog, IntByReference NumberOfRecords);

    /**
     * Writes an entry at the end of the specified event log.
     *
     * @param hEventLog
     *            A handle to the event log. The RegisterEventSource function
     *            returns this handle. As of Windows XP with SP2, this parameter
     *            cannot be a handle to the Security log. To write an event to
     *            the Security log, use the AuthzReportSecurityEvent function.
     * @param wType
     *            The type of event to be logged.
     * @param wCategory
     *            The event category. This is source-specific information; the
     *            category can have any value.
     * @param dwEventID
     *            The event identifier. The event identifier specifies the entry
     *            in the message file associated with the event source.
     * @param lpUserSid
     *            A pointer to the current user's security identifier. This
     *            parameter can be NULL if the security identifier is not
     *            required.
     * @param wNumStrings
     *            The number of insert strings in the array pointed to by the
     *            lpStrings parameter. A value of zero indicates that no strings
     *            are present.
     * @param dwDataSize
     *            The number of bytes of event-specific raw (binary) data to
     *            write to the log. If this parameter is zero, no event-specific
     *            data is present.
     * @param lpStrings
     *            A pointer to a buffer containing an array of null-terminated
     *            strings that are merged into the message before Event Viewer
     *            displays the string to the user. This parameter must be a
     *            valid pointer (or NULL), even if wNumStrings is zero. Each
     *            string is limited to 31,839 characters.
     * @param lpRawData
     *            A pointer to the buffer containing the binary data. This
     *            parameter must be a valid pointer (or NULL), even if the
     *            dwDataSize parameter is zero.
     * @return If the function succeeds, the return value is nonzero, indicating
     *         that the entry was written to the log. If the function fails, the
     *         return value is zero. To get extended error information, call
     *         GetLastError.
     */
    boolean ReportEvent(HANDLE hEventLog, int wType, int wCategory,
			int dwEventID, PSID lpUserSid, int wNumStrings, int dwDataSize,
			String[] lpStrings, Pointer lpRawData);

    /**
     * Clears the specified event log, and optionally saves the current copy of
     * the log to a backup file.
     *
     * @param hEventLog
     *            A handle to the event log to be cleared. The OpenEventLog
     *            function returns this handle.
     * @param lpBackupFileName
     *            The absolute or relative path of the backup file. If this file
     *            already exists, the function fails. If the lpBackupFileName
     *            parameter is NULL, the event log is not backed up.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError. The ClearEventLog function can
     *         fail if the event log is empty or the backup file already exists.
     */
    boolean ClearEventLog(HANDLE hEventLog, String lpBackupFileName);

    /**
     * Saves the specified event log to a backup file. The function does not
     * clear the event log.
     *
     * @param hEventLog
     *            A handle to the open event log. The OpenEventLog function
     *            returns this handle.
     * @param lpBackupFileName
     *            The absolute or relative path of the backup file.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean BackupEventLog(HANDLE hEventLog, String lpBackupFileName);

    /**
     * Opens a handle to a backup event log created by the BackupEventLog
     * function.
     *
     * @param lpUNCServerName
     *            The Universal Naming Convention (UNC) name of the remote
     *            server on which this operation is to be performed. If this
     *            parameter is NULL, the local computer is used.
     * @param lpFileName
     *            The full path of the backup file.
     * @return If the function succeeds, the return value is a handle to the
     *         backup event log. If the function fails, the return value is
     *         NULL. To get extended error information, call GetLastError.
     */
    HANDLE OpenBackupEventLog(String lpUNCServerName, String lpFileName);

    /**
     * Reads the specified number of entries from the specified event log. The
     * function can be used to read log entries in chronological or reverse
     * chronological order.
     *
     * @param hEventLog
     *            A handle to the event log to be read. The OpenEventLog
     *            function returns this handle.
     * @param dwReadFlags
     *            Use the following flag values to indicate how to read the log
     *            file.
     * @param dwRecordOffset
     *            The record number of the log-entry at which the read operation
     *            should start. This parameter is ignored unless dwReadFlags
     *            includes the EVENTLOG_SEEK_READ flag.
     * @param lpBuffer
     *            An application-allocated buffer that will receive one or more
     *            EVENTLOGRECORD structures. This parameter cannot be NULL, even
     *            if the nNumberOfBytesToRead parameter is zero. The maximum
     *            size of this buffer is 0x7ffff bytes.
     * @param nNumberOfBytesToRead
     *            The size of the lpBuffer buffer, in bytes. This function will
     *            read as many log entries as will fit in the buffer; the
     *            function will not return partial entries.
     * @param pnBytesRead
     *            A pointer to a variable that receives the number of bytes read
     *            by the function.
     * @param pnMinNumberOfBytesNeeded
     *            A pointer to a variable that receives the required size of the
     *            lpBuffer buffer. This value is valid only this function
     *            returns zero and GetLastError returns
     *            ERROR_INSUFFICIENT_BUFFER.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean ReadEventLog(HANDLE hEventLog, int dwReadFlags,
                         int dwRecordOffset, Pointer lpBuffer, int nNumberOfBytesToRead,
                         IntByReference pnBytesRead, IntByReference pnMinNumberOfBytesNeeded);

    /**
     * The GetOldestEventLogRecord function retrieves the absolute record number
     * of the oldest record in the specified event log.
     *
     * @param hEventLog
     *            Handle to the open event log. This handle is returned by the
     *            OpenEventLog or OpenBackupEventLog function.
     * @param OldestRecord
     *            Pointer to a variable that receives the absolute record number
     *            of the oldest record in the specified event log.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GetOldestEventLogRecord(HANDLE hEventLog, IntByReference OldestRecord);

    /**
     * Changes the optional configuration parameters of a service.
     *
     * @param hService
     *            A handle to the service. This handle is returned by the
     *            OpenService or CreateService function and must have the
     *            SERVICE_CHANGE_CONFIG access right. For more information,
     *            see <a
     *            href="http://msdn.microsoft.com/en-us/library/ms685981.aspx"
     *            >Service Security and Access Rights</a>.
     *            If the service controller handles the SC_ACTION_RESTART
     *            action, hService must have the SERVICE_START access right.
     * @param dwInfoLevel
     *            The configuration information to be changed.
     * @param lpInfo
     *            A pointer to the new value to be set for the configuration
     *            information. The format of this data depends on the value
     *            of the dwInfoLevel parameter. If this value is NULL, the
     *            information remains unchanged.
     * @return If the function succeeds, the return value is nonzero.
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    public boolean ChangeServiceConfig2(SC_HANDLE hService, int dwInfoLevel,
                                        ChangeServiceConfig2Info lpInfo);

    /**
     * Retrieves the optional configuration parameters of the specified service.
     *
     * @param hService
     *            A handle to the service. This handle is returned by the OpenService or
     *            CreateService function and must have the SERVICE_QUERY_CONFIG access right. For
     *            more information, see Service Security and Access Rights.
     * @param dwInfoLevel
     *            The configuration information to be queried.
     * @param lpBuffer
     *            A pointer to the buffer that receives the service configuration information. The
     *            format of this data depends on the value of the dwInfoLevel parameter.
     *            The maximum size of this array is 8K bytes. To determine the required size,
     *            specify NULL for this parameter and 0 for the cbBufSize parameter. The function
     *            fails and GetLastError returns ERROR_INSUFFICIENT_BUFFER. The pcbBytesNeeded
     *            parameter receives the needed size.
     * @param cbBufSize
     *            The size of the structure pointed to by the lpBuffer parameter, in bytes.
     * @param pcbBytesNeeded
     *            A pointer to a variable that receives the number of bytes required to store the
     *            configuration information, if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @return If the function succeeds, the return value is nonzero.
     *         If the function fails, the return value is zero. To get extended error information,
     *         call GetLastError.
     */
    public boolean QueryServiceConfig2(SC_HANDLE hService, int dwInfoLevel,
                                       Pointer lpBuffer, int cbBufSize, IntByReference pcbBytesNeeded);


    /**
     * Retrieves the current status of the specified service based on the
     * specified information level.
     *
     * @param hService
     *            A handle to the service. This handle is returned by the
     *            OpenService(SC_HANDLE, String, int) or CreateService()
     *            function, and it must have the SERVICE_QUERY_STATUS access
     *            right. For more information, see <a
     *            href="http://msdn.microsoft.com/en-us/library/ms685981.aspx"
     *            >Service Security and Access Rights</a>.
     * @param InfoLevel
     *            The service attributes to be returned (a value from
     *            SC_STATUS_TYPE enumeration). Use SC_STATUS_PROCESS_INFO to
     *            retrieve the service status information. The lpBuffer
     *            parameter is a pointer to a SERVICE_STATUS_PROCESS structure.
     *            Currently, no other information levels are defined.
     * @param lpBuffer
     *            (optional) A pointer to the buffer that receives the status
     *            information. The format of this data depends on the value of
     *            the InfoLevel parameter. The maximum size of this array is 8K
     *            bytes. To determine the required size, specify NULL for this
     *            parameter and 0 for the cbBufSize parameter. The function will
     *            fail and GetLastError will return ERROR_INSUFFICIENT_BUFFER.
     *            The pcbBytesNeeded parameter will receive the required size.
     * @param cbBufSize
     *            The size of the buffer pointed to by the lpBuffer parameter,
     *            in bytes.
     * @param pcbBytesNeeded
     *            A pointer to a variable that receives the number of bytes
     *            needed to store all status information, if the function fails
     *            with ERROR_INSUFFICIENT_BUFFER.
     * @return If the function succeeds, the return value is true. If the
     *         function fails, the return value is false. To get extended error
     *         information, call GetLastError. This value is a nonzero error
     *         code defined in Winerror.h.
     */
    boolean QueryServiceStatusEx(SC_HANDLE hService, int InfoLevel,
                                 SERVICE_STATUS_PROCESS lpBuffer, int cbBufSize,
                                 IntByReference pcbBytesNeeded);

    /**
     * Sends a control code to a service. To specify additional information when
     * stopping a service, use the ControlServiceEx function.
     *
     * @param hService
     *            A handle to the service. This handle is returned by the
     *            OpenService(SC_HANDLE, String, int) or CreateService()
     *            function. The access rights required for this handle depend on
     *            the dwControl code requested.
     * @param dwControl
     *            This parameter can be one of the following control codes
     *            (found in Winsvc.h): SERVICE_CONTROL_STOP,
     *            SERVICE_CONTROL_PAUSE, SERVICE_CONTROL_CONTINUE
     *            SERVICE_CONTROL_INTERROGATE, SERVICE_CONTROL_PARAMCHANGE,
     *            SERVICE_CONTROL_NETBINDADD, SERVICE_CONTROL_NETBINDREMOVE,
     *            SERVICE_CONTROL_NETBINDENABLE, SERVICE_CONTROL_NETBINDDISABLE
     *            This value can also be a user-defined control code, as
     *            described below: Range 128 to 255 - The service defines the
     *            action associated with the control code. The hService handle
     *            must have the SERVICE_USER_DEFINED_CONTROL access right.
     * @param lpServiceStatus
     *            A pointer to a SERVICE_STATUS structure that receives the
     *            latest service status information. The information returned
     *            reflects the most recent status that the service reported to
     *            the service control manager. The service control manager fills
     *            in the structure only when ControlService returns one of the
     *            following error codes: NO_ERROR,
     *            ERROR_INVALID_SERVICE_CONTROL,
     *            ERROR_SERVICE_CANNOT_ACCEPT_CTRL, or ERROR_SERVICE_NOT_ACTIVE.
     *            Otherwise, the structure is not filled in.
     * @return If the function succeeds, the return value is true. If the
     *         function fails, the return value is false. To get extended error
     *         information, call GetLastError. This value is a nonzero error
     *         code defined in Winerror.h.
     */
    boolean ControlService(SC_HANDLE hService, int dwControl, SERVICE_STATUS lpServiceStatus);

    /**
     * Starts a service.
     *
     * @param hService
     *            A handle to the service. This handle is returned by the
     *            OpenService(SC_HANDLE, String, int) or CreateService()
     *            function, and it must have the SERVICE_START access right. For
     *            more information, see <a
     *            href="http://msdn.microsoft.com/en-us/library/ms685981.aspx">
     *            Service Security and Access Rights</a>.
     * @param dwNumServiceArgs
     *            The number of strings in the lpServiceArgVectors array. If
     *            lpServiceArgVectors is NULL, this parameter can be zero.
     * @param lpServiceArgVectors
     *            The null-terminated strings to be passed to the ServiceMain
     *            function for the service as arguments. If there are no
     *            arguments, this parameter can be null. Otherwise, the first
     *            argument (lpServiceArgVectors[0]) is the name of the service,
     *            followed by any additional arguments (lpServiceArgVectors[1]
     *            through lpServiceArgVectors[dwNumServiceArgs-1]). Driver
     *            services do not receive these arguments.
     * @return If the function succeeds, the return value is true. If the
     *         function fails, the return value is false. To get extended error
     *         information, call GetLastError. This value is a nonzero error
     *         code defined in Winerror.h.
     */
    boolean StartService(SC_HANDLE hService, int dwNumServiceArgs, String[] lpServiceArgVectors);

    /**
     * Closes a handle to a service control manager or service object.
     *
     * @param hSCObject
     *            A handle to the service control manager object or the service
     *            object to close. Handles to service control manager objects
     *            are returned by the OpenSCManager(String, String, int)
     *            function, and handles to service objects are returned by
     *            either the OpenService(SC_HANDLE, String, int) or
     *            CreateService() function.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError. This value is a nonzero error
     *         code defined in Winerror.h.
     */
    boolean CloseServiceHandle(SC_HANDLE hSCObject);

    /**
     * Opens an existing service.
     *
     * @param hSCManager
     *            A handle to the service control manager database. The
     *            OpenSCManager(String, String, int) function returns this
     *            handle.
     * @param lpServiceName
     *            The name of the service to be opened. This is the name
     *            specified by the lpServiceName parameter of the CreateService
     *            function when the service object was created, not the service
     *            display name that is shown by user interface applications to
     *            identify the service. The maximum string length is 256
     *            characters. The service control manager database preserves the
     *            case of the characters, but service name comparisons are
     *            always case insensitive. Forward-slash (/) and backslash (\)
     *            are invalid service name characters.
     * @param dwDesiredAccess
     *            The access to the service. For a list of access rights, see <a
     *            href="http://msdn.microsoft.com/en-us/library/ms685981.aspx">
     *            Service Security and Access Rights</a>. Before granting the
     *            requested access, the system checks the access token of the
     *            calling process against the discretionary access-control list
     *            of the security descriptor associated with the service object.
     * @return If the function succeeds, the return value is a handle to the
     *         service. If the function fails, the return value is NULL. To get
     *         extended error information, call GetLastError. This value is a
     *         nonzero error code defined in Winerror.h.
     */
    SC_HANDLE OpenService(SC_HANDLE hSCManager, String lpServiceName, int dwDesiredAccess);

    /**
     * Establishes a connection to the service control manager on the specified
     * computer and opens the specified service control manager database.
     *
     * @param lpMachineName
     *            The name of the target computer. If the pointer is NULL or
     *            points to an empty string, the function connects to the
     *            service control manager on the local computer.
     * @param lpDatabaseName
     *            The name of the service control manager database. This
     *            parameter should be set to SERVICES_ACTIVE_DATABASE. If it is
     *            NULL, the SERVICES_ACTIVE_DATABASE database is opened by
     *            default.
     * @param dwDesiredAccess
     *            The access to the service control manager. For a list of
     *            access rights, see <a
     *            href="http://msdn.microsoft.com/en-us/library/ms685981.aspx">
     *            Service Security and Access Rights</a>. Before granting the
     *            requested access rights, the system checks the access token of
     *            the calling process against the discretionary access-control
     *            list of the security descriptor associated with the service
     *            control manager. The SC_MANAGER_CONNECT access right is
     *            implicitly specified by calling this function.
     * @return If the function succeeds, the return value is a handle to the
     *         specified service control manager database. If the function
     *         fails, the return value is NULL. To get extended error
     *         information, call GetLastError. This value is a nonzero error
     *         code defined in Winerror.h.
     */
    SC_HANDLE OpenSCManager(String lpMachineName, String lpDatabaseName, int dwDesiredAccess);

    /**
     * Creates a new process and its primary thread. The new process runs in the
     * security context of the user represented by the specified token.
     *
     * Typically, the process that calls the CreateProcessAsUser function must
     * have the SE_INCREASE_QUOTA_NAME privilege and may require the
     * SE_ASSIGNPRIMARYTOKEN_NAME privilege if the token is not assignable. If
     * this function fails with ERROR_PRIVILEGE_NOT_HELD (1314), use the
     * CreateProcessWithLogonW function instead. CreateProcessWithLogonW
     * requires no special privileges, but the specified user account must be
     * allowed to log on interactively. Generally, it is best to use
     * CreateProcessWithLogonW to create a process with alternate credentials.
     *
     * @param hToken
     *            A handle to the primary token that represents a user.
     * @param lpApplicationName
     *            The name of the module to be executed.
     * @param lpCommandLine
     *            The command line to be executed.
     * @param lpProcessAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that specifies a
     *            security descriptor for the new process object and determines
     *            whether child processes can inherit the returned handle to the
     *            process.
     * @param lpThreadAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that specifies a
     *            security descriptor for the new thread object and determines
     *            whether child processes can inherit the returned handle to the
     *            thread.
     * @param bInheritHandles
     *            If this parameter is TRUE, each inheritable handle in the
     *            calling process is inherited by the new process. If the
     *            parameter is FALSE, the handles are not inherited. Note that
     *            inherited handles have the same value and access rights as the
     *            original handles.
     * @param dwCreationFlags
     *            The flags that control the priority class and the creation of
     *            the process. For a list of values, see Process Creation Flags.
     * @param lpEnvironment
     *            A pointer to an environment block for the new process. If this
     *            parameter is NULL, the new process uses the environment of the
     *            calling process.
     *
     *            An environment block consists of a null-terminated block of
     *            null-terminated strings. Each string is in the following form:
     *            name=value\0
     * @param lpCurrentDirectory
     *            The full path to the current directory for the process. The
     *            string can also specify a UNC path.
     * @param lpStartupInfo
     *            A pointer to a STARTUPINFO or STARTUPINFOEX structure.
     * @param lpProcessInformation
     *            A pointer to a PROCESS_INFORMATION structure that receives
     *            identification information about the new process.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean CreateProcessAsUser(HANDLE hToken, String lpApplicationName,
                                String lpCommandLine, SECURITY_ATTRIBUTES lpProcessAttributes,
                                SECURITY_ATTRIBUTES lpThreadAttributes, boolean bInheritHandles,
                                int dwCreationFlags, String lpEnvironment,
                                String lpCurrentDirectory, WinBase.STARTUPINFO lpStartupInfo,
                                WinBase.PROCESS_INFORMATION lpProcessInformation);

    /**
     * The AdjustTokenPrivileges function enables or disables privileges in the
     * specified access token. Enabling or disabling privileges in an access
     * token requires TOKEN_ADJUST_PRIVILEGES access.
     *
     * @param TokenHandle
     *            A handle to the access token that contains the privileges to
     *            be modified.
     * @param DisableAllPrivileges
     *            Specifies whether the function disables all of the token's
     *            privileges.
     * @param NewState
     *            A pointer to a TOKEN_PRIVILEGES structure that specifies an
     *            array of privileges and their attributes.
     * @param BufferLength
     *            Specifies the size, in bytes, of the buffer pointed to by the
     *            PreviousState parameter. This parameter can be zero if the
     *            PreviousState parameter is NULL.
     * @param PreviousState
     *            A pointer to a buffer that the function fills with a
     *            TOKEN_PRIVILEGES structure that contains the previous state of
     *            any privileges that the function modifies.
     * @param ReturnLength
     *            A pointer to a variable that receives the required size, in
     *            bytes, of the buffer pointed to by the PreviousState
     *            parameter.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean AdjustTokenPrivileges(HANDLE TokenHandle,
                                  boolean DisableAllPrivileges, WinNT.TOKEN_PRIVILEGES NewState,
                                  int BufferLength, WinNT.TOKEN_PRIVILEGES PreviousState,
                                  IntByReference ReturnLength);

    /**
     * The LookupPrivilegeName function retrieves the name that corresponds to
     * the privilege represented on a specific system by a specified locally
     * unique identifier (LUID).
     *
     * @param lpSystemName
     *            A pointer to a null-terminated string that specifies the name
     *            of the system on which the privilege name is retrieved. If a
     *            null string is specified, the function attempts to find the
     *            privilege name on the local system.
     * @param lpLuid
     *            A pointer to the LUID by which the privilege is known on the
     *            target system.
     * @param lpName
     *            A pointer to a buffer that receives a null-terminated string
     *            that represents the privilege name. For example, this string
     *            could be "SeSecurityPrivilege".
     * @param cchName
     *            A pointer to a variable that specifies the size, in a TCHAR
     *            value, of the lpName buffer.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean LookupPrivilegeName(String lpSystemName, WinNT.LUID lpLuid,
                                char[] lpName, IntByReference cchName);

    /**
     * The LookupPrivilegeValue function retrieves the locally unique identifier
     * (LUID) used on a specified system to locally represent the specified
     * privilege name.
     *
     * @param lpSystemName
     *            A pointer to a null-terminated string that specifies the name
     *            of the system on which the privilege name is retrieved. If a
     *            null string is specified, the function attempts to find the
     *            privilege name on the local system.
     * @param lpName
     *            A pointer to a null-terminated string that specifies the name
     *            of the privilege, as defined in the Winnt.h header file. For
     *            example, this parameter could specify the constant,
     *            SE_SECURITY_NAME, or its corresponding string,
     *            "SeSecurityPrivilege".
     * @param lpLuid
     *            A pointer to a variable that receives the LUID by which the
     *            privilege is known on the system specified by the lpSystemName
     *            parameter.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean LookupPrivilegeValue(String lpSystemName, String lpName, WinNT.LUID lpLuid);

    /**
     * The function obtains specified information about the security of a file
     * or directory. The information obtained is constrained by the caller's
     * access rights and privileges.
     *
     * @param lpFileName
     *            A pointer to a null-terminated string that specifies the file
     *            or directory for which security information is retrieved.
     * @param RequestedInformation
     *            A SECURITY_INFORMATION value that identifies the security
     *            information being requested. See WinNT *_SECURITY_INFORMATION
     * @param pointer
     *            A pointer to a buffer that receives a copy of the security
     *            descriptor of the object specified by the lpFileName
     *            parameter. The calling process must have permission to view
     *            the specified aspects of the object's security status. The
     *            SECURITY_DESCRIPTOR structure is returned in self-relative
     *            format.
     * @param nLength
     *            Specifies the size, in bytes, of the buffer pointed to by the
     *            pSecurityDescriptor parameter.
     * @param lpnLengthNeeded
     *            A pointer to the variable that receives the number of bytes
     *            necessary to store the complete security descriptor. If the
     *            returned number of bytes is less than or equal to nLength, the
     *            entire security descriptor is returned in the output buffer;
     *            otherwise, none of the descriptor is returned.
     * @return whether the call succeeded
     */
    boolean GetFileSecurity(String lpFileName,
                            int RequestedInformation, Pointer pointer, int nLength,
                            IntByReference lpnLengthNeeded);

    /**
     * The SetFileSecurity function sets the security of a file or directory object.
     * This function is obsolete. Use the SetNamedSecurityInfo function instead.
     *
     * @param lpFileName
     *          A pointer to a null-terminated string that specifies the file or directory for which security is set.
     *          Note that security applied to a directory is not inherited by its children.
     * @param SecurityInformation
     *          Specifies a SECURITY_INFORMATION structure that identifies the contents of the security descriptor
     *          pointed to by the pSecurityDescriptor parameter.
     * @param pSecurityDescriptor
     *          A pointer to a SECURITY_DESCRIPTOR structure.
     * @return
     *          If the function succeeds, the function returns nonzero. If the function fails, it returns zero. To get
     *          extended error information, call GetLastError.
     */
    boolean SetFileSecurity(String lpFileName, int SecurityInformation, Pointer pSecurityDescriptor);

    /**
     * The GetSecurityInfo function retrieves a copy of the security descriptor for an object specified by a handle.
     *
     * @param handle [in]
     *          A handle to the object from which to retrieve security information.
     * @param ObjectType [in]
     *          SE_OBJECT_TYPE enumeration value that indicates the type of object.
     * @param SecurityInfo [in]
     *          A set of bit flags that indicate the type of security information to retrieve. See WinNT *_SECURITY_INFORMATION
     * @param ppsidOwner [out, optional]
     *          A pointer to a variable that receives a pointer to the owner SID in the security descriptor returned
     *          in ppSecurityDescriptor. The returned pointer is valid only if you set the OWNER_SECURITY_INFORMATION flag.
     *          This parameter can be NULL if you do not need the owner SID.
     * @param ppsidGroup [in, optional]
     *          A pointer to a variable that receives a pointer to the primary group SID in the returned security descriptor.
     *          The returned pointer is valid only if you set the GROUP_SECURITY_INFORMATION flag. This parameter can be NULL
     *          if you do not need the group SID.
     * @param ppDacl [in, optional]
     *          A pointer to a variable that receives a pointer to the DACL in the returned security descriptor. The returned
     *          pointer is valid only if you set the DACL_SECURITY_INFORMATION flag. This parameter can be NULL if you do not
     *          need the DACL.
     * @param ppSacl [in, optional]
     *          A pointer to a variable that receives a pointer to the SACL in the returned security descriptor. The returned
     *          pointer is valid only if you set the SACL_SECURITY_INFORMATION flag. This parameter can be NULL if you do not
     *          need the SACL.
     * @param ppSecurityDescriptor
     *          A pointer to a variable that receives a pointer to the security descriptor of the object. When you have finished
     *          using the pointer, free the returned buffer by calling the LocalFree function.
     *          This parameter is required if any one of the ppsidOwner, ppsidGroup, ppDacl, or ppSacl parameters is not NULL.
     * @return whether the call succeeded. A nonzero return is a failure.
     *
     * <p><b>NOTES:</b></p>
     * <p>1. If the ppsidOwner, ppsidGroup, ppDacl, and ppSacl parameters are non-NULL, and the SecurityInfo parameter specifies
     * that they be retrieved from the object, those parameters will point to the corresponding parameters in the security descriptor
     * returned in ppSecurityDescriptor.</p>
     * <p>2. To read the owner, group, or DACL from the object's security descriptor, the calling process must have been granted
     * READ_CONTROL access when the handle was opened. To get READ_CONTROL access, the caller must be the owner of the object or
     * the object's DACL must grant the access.</p>
     * <p>3. To read the SACL from the security descriptor, the calling process must have been granted ACCESS_SYSTEM_SECURITY access
     * when the handle was opened. The proper way to get this access is to enable the SE_SECURITY_NAME privilege in the caller's
     * current token, open the handle for ACCESS_SYSTEM_SECURITY access, and then disable the privilege.</p>
     * <p>4. If the supplied handle was opened with an ACCESS_MASK value of MAXIMUM_ALLOWED,
     * then the SetSecurityInfo function will not propagate ACEs to children.</p>
     */
    int GetSecurityInfo(HANDLE handle,
                        int ObjectType,
                        int SecurityInfo,
                        PointerByReference ppsidOwner,
                        PointerByReference ppsidGroup,
                        PointerByReference ppDacl,
                        PointerByReference ppSacl,
                        PointerByReference ppSecurityDescriptor);

    /**
     * The SetSecurityInfo function sets specified security information in
     * the security descriptor of a specified object. The caller identifies the
     * object by a handle.
     *
     * @param handle [in]
     *            A handle to the object for which to set security information.
     * @param ObjectType [in]
     *            A value of the SE_OBJECT_TYPE enumeration that indicates the type
     *            of object named by the pObjectName parameter.
     * @param SecurityInfo [in]
     *            A set of bit flags that indicate the type of security
     *            information to set. See WinNT *_SECURITY_INFORMATION
     * @param ppsidOwner [in, optional]
     *            A pointer to a SID structure that identifies the owner of the object.
     *            If the caller does not have the SeRestorePrivilege constant
     *            (see Privilege Constants), this SID must be contained in the
     *            caller's token, and must have the SE_GROUP_OWNER permission enabled.
     *            The SecurityInfo parameter must include the OWNER_SECURITY_INFORMATION
     *            flag. To set the owner, the caller must have WRITE_OWNER access to
     *            the object or have the SE_TAKE_OWNERSHIP_NAME privilege enabled.
     *            If you are not setting the owner SID, this parameter can be NULL.
     * @param ppsidGroup [in, optional]
     *            A pointer to a SID that identifies the primary group of the object.
     *            The SecurityInfo parameter must include the GROUP_SECURITY_INFORMATION
     *            flag. If you are not setting the primary group SID, this parameter
     *            can be NULL.
     * @param ppDacl [in, optional]
     *            A pointer to the new DACL for the object. The SecurityInfo parameter
     *            must include the DACL_SECURITY_INFORMATION flag. The caller must have
     *            WRITE_DAC access to the object or be the owner of the object. If you
     *            are not setting the DACL, this parameter can be NULL.
     * @param ppSacl [in, optional]
     *             A pointer to the new SACL for the object. The SecurityInfo parameter
     *             must include any of the following flags: SACL_SECURITY_INFORMATION,
     *             LABEL_SECURITY_INFORMATION, ATTRIBUTE_SECURITY_INFORMATION,
     *             SCOPE_SECURITY_INFORMATION, or BACKUP_SECURITY_INFORMATION.
     *             If setting SACL_SECURITY_INFORMATION or SCOPE_SECURITY_INFORMATION,
     *             the caller must have the SE_SECURITY_NAME privilege enabled. If
     *             you are not setting the SACL, this parameter can be NULL.
     * @return whether the call succeeded. A nonzero return is a failure.
     *
     * <p><b>NOTES:</b></p>
     * <p>1. If you are setting the discretionary access control list (DACL) or any elements
     * in the system access control list (SACL) of an object, the system automatically
     * propagates any inheritable access control entries (ACEs) to existing child objects,
     * according to the ACE inheritance rules.</p>
     * <p>2. The SetSecurityInfo function does not reorder access-allowed or access-denied
     * ACEs based on the preferred order. When propagating inheritable ACEs to existing
     * child objects, SetSecurityInfo puts inherited ACEs in order after all of the
     * noninherited ACEs in the DACLs of the child objects.</p>
     * <p>3. If share access to the children of the object is not available, this function
     * will not propagate unprotected ACEs to the children. For example, if a directory
     * is opened with exclusive access, the operating system will not propagate unprotected
     * ACEs to the subdirectories or files of that directory when the security on the
     * directory is changed.</p>
     * <p>4. If the supplied handle was opened with an ACCESS_MASK value of MAXIMUM_ALLOWED,
     * then the SetSecurityInfo function will not propagate ACEs to children.</p>
     */
    int SetSecurityInfo(HANDLE handle,
                        int ObjectType,
                        int SecurityInfo,
                        Pointer ppsidOwner,
                        Pointer ppsidGroup,
                        Pointer ppDacl,
                        Pointer ppSacl);

    /**
     * The GetNamedSecurityInfo function retrieves a copy of the security
     * descriptor for an object specified by name
     *
     * @param pObjectName
     *            A pointer to a that specifies the name of the object from
     *            which to retrieve security information.
     *            For descriptions of the string formats for the different
     *            object types, see SE_OBJECT_TYPE.
     * @param ObjectType
     *            Specifies a value from the SE_OBJECT_TYPE enumeration that
     *            indicates the type of object named by the pObjectName parameter.
     * @param SecurityInfo
     *            A set of bit flags that indicate the type of security
     *            information to retrieve. See WinNT *_SECURITY_INFORMATION
     * @param ppsidOwner [out, optional]
     *            A pointer to a variable that receives a pointer to the owner SID
     *            in the security descriptor returned in ppSecurityDescriptor
     *            or NULL if the security descriptor has no owner SID.
     *            The returned pointer is valid only if you set the
     *            OWNER_SECURITY_INFORMATION flag. Also, this parameter can be
     *            NULL if you do not need the owner SID.
     * @param ppsidGroup [out, optional]
     *            A pointer to a variable that receives a pointer to the primary
     *            group SID in the returned security descriptor or NULL if the
     *            security descriptor has no group SID. The returned pointer is
     *            valid only if you set the GROUP_SECURITY_INFORMATION flag.
     *            Also, this parameter can be NULL if you do not need the group SID.
     * @param ppDacl [out, optional]
     *            A pointer to a variable that receives a pointer to the DACL in
     *            the returned security descriptor or NULL if the security
     *            descriptor has no DACL. The returned pointer is valid only if
     *            you set the DACL_SECURITY_INFORMATION flag. Also, this parameter
     *            can be NULL if you do not need the DACL.
     * @param ppSacl [out, optional]
     *             A pointer to a variable that receives a pointer to the SACL in
     *             the returned security descriptor or NULL if the security
     *             descriptor has no SACL. The returned pointer is valid only if
     *             you set the SACL_SECURITY_INFORMATION flag. Also, this parameter
     *             can be NULL if you do not need the SACL.
     * @param ppSecurityDescriptor
     *            A pointer to a variable that receives a pointer to the security
     *            descriptor of the object. When you have finished using the
     *            pointer, free the returned buffer by calling the LocalFree
     *            function.
     *
     *            This parameter is required if any one of the ppsidOwner,
     *            ppsidGroup, ppDacl, or ppSacl parameters is not NULL.
     * @return whether the call succeeded. A nonzero return is a failure.
     *
     * NOTES:
     * 1. To read the owner, group, or DACL from the object's security descriptor,
     * the object's DACL must grant READ_CONTROL access to the caller, or the caller
     * must be the owner of the object.
     * 2. To read the system access control list of the object, the SE_SECURITY_NAME
     * privilege must be enabled for the calling process. For information about the
     * security implications of enabling privileges, see Running with Special Privileges.
     */
    int GetNamedSecurityInfo(
                             String pObjectName,
                             int ObjectType,
                             int SecurityInfo,
                             PointerByReference ppsidOwner,
                             PointerByReference ppsidGroup,
                             PointerByReference ppDacl,
                             PointerByReference ppSacl,
                             PointerByReference ppSecurityDescriptor);

    /**
     * The SetNamedSecurityInfo function sets specified security information in
     * the security descriptor of a specified object. The caller identifies the
     * object by name.
     *
     * @param pObjectName [in]
     *            A pointer to a string that specifies the name of the object for
     *            which to set security information. This can be
     *            the name of a local or remote file or directory on an NTFS file
     *            system, network share, registry key, semaphore, event, mutex,
     *            file mapping, or waitable timer.	 *
     *            For descriptions of the string formats for the different
     *            object types, see SE_OBJECT_TYPE.
     * @param ObjectType [in]
     *            A value of the SE_OBJECT_TYPE enumeration that indicates the type
     *            of object named by the pObjectName parameter.
     * @param SecurityInfo [in]
     *            A set of bit flags that indicate the type of security
     *            information to set. See WinNT *_SECURITY_INFORMATION
     * @param ppsidOwner [in, optional]
     *            A pointer to a SID structure that identifies the owner of the object.
     *            If the caller does not have the SeRestorePrivilege constant
     *            (see Privilege Constants), this SID must be contained in the
     *            caller's token, and must have the SE_GROUP_OWNER permission enabled.
     *            The SecurityInfo parameter must include the OWNER_SECURITY_INFORMATION
     *            flag. To set the owner, the caller must have WRITE_OWNER access to
     *            the object or have the SE_TAKE_OWNERSHIP_NAME privilege enabled.
     *            If you are not setting the owner SID, this parameter can be NULL.
     * @param ppsidGroup [in, optional]
     *            A pointer to a SID that identifies the primary group of the object.
     *            The SecurityInfo parameter must include the GROUP_SECURITY_INFORMATION
     *            flag. If you are not setting the primary group SID, this parameter
     *            can be NULL.
     * @param ppDacl [in, optional]
     *            A pointer to the new DACL for the object. The SecurityInfo parameter
     *            must include the DACL_SECURITY_INFORMATION flag. The caller must have
     *            WRITE_DAC access to the object or be the owner of the object. If you
     *            are not setting the DACL, this parameter can be NULL.
     * @param ppSacl [in, optional]
     *             A pointer to the new SACL for the object. The SecurityInfo parameter
     *             must include any of the following flags: SACL_SECURITY_INFORMATION,
     *             LABEL_SECURITY_INFORMATION, ATTRIBUTE_SECURITY_INFORMATION,
     *             SCOPE_SECURITY_INFORMATION, or BACKUP_SECURITY_INFORMATION.
     *             If setting SACL_SECURITY_INFORMATION or SCOPE_SECURITY_INFORMATION,
     *             the caller must have the SE_SECURITY_NAME privilege enabled. If
     *             you are not setting the SACL, this parameter can be NULL.
     * @return whether the call succeeded. A nonzero return is a failure.
     *
     * NOTES:
     * 1. The SetNamedSecurityInfo function does not reorder access-allowed or access-denied
     * ACEs based on the preferred order. When propagating inheritable ACEs to existing
     * child objects, SetNamedSecurityInfo puts inherited ACEs in order after all of the
     * noninherited ACEs in the DACLs of the child objects.
     * 2. This function transfers information in plaintext. The information transferred by
     * this function is signed unless signing has been turned off for the system, but no
     * encryption is performed.
     * 3. When you update access rights of a folder indicated by an UNC path, for example
     * \\Test\TestFolder, the original inherited ACE is removed and the full volume path
     * is not included.
     */
    int SetNamedSecurityInfo(
                             String pObjectName,
                             int ObjectType,
                             int SecurityInfo,
                             Pointer ppsidOwner,
                             Pointer ppsidGroup,
                             Pointer ppDacl,
                             Pointer ppSacl);

    /**
     * The GetSecurityDescriptorLength function returns the length, in bytes, of a structurally
     * valid security descriptor. The length includes the length of all associated structures.
     *
     * @param ppSecurityDescriptor
     *            A pointer to the SECURITY_DESCRIPTOR structure whose length the function returns.
     *            The pointer is assumed to be valid.
     * @return If the function succeeds, the function returns the length, in bytes, of the SECURITY_DESCRIPTOR structure.
     *         If the SECURITY_DESCRIPTOR structure is not valid, the return value is undefined.
     */
    int GetSecurityDescriptorLength(Pointer ppSecurityDescriptor);

    /**
     * The IsValidSecurityDescriptor function determines whether the components of a security descriptor are valid.
     *
     * @param ppSecurityDescriptor [in]
     *            A pointer to a SECURITY_DESCRIPTOR structure that the function validates.
     * @return If the components of the security descriptor are valid, the return value is nonzero.
     */
    boolean IsValidSecurityDescriptor(Pointer ppSecurityDescriptor);

    /**
     * A pointer to a SECURITY_DESCRIPTOR structure in absolute format. The function creates a version of this security
     * descriptor in self-relative format without modifying the original.
     * @param pAbsoluteSD
     *          A pointer to a SECURITY_DESCRIPTOR structure in absolute format. The function creates a version of this
     *          security descriptor in self-relative format without modifying the original.
     * @param pSelfRelativeSD
     *          A pointer to a buffer the function fills with a security descriptor in self-relative format.
     * @param lpdwBufferLength
     *          A pointer to a variable specifying the size of the buffer pointed to by the pSelfRelativeSD parameter.
     *          If the buffer is not large enough for the security descriptor, the function fails and sets this variable
     *          to the minimum required size.
     * @return If the function succeeds, the function returns nonzero. If the function fails, it returns zero. To get
     *         extended error information, call GetLastError. Possible return codes include, but are not limited to, the following:
     *         ERROR_INSUFFICIENT_BUFFER - One or more of the buffers is too small.
     */
    boolean MakeSelfRelativeSD(SECURITY_DESCRIPTOR pAbsoluteSD,
                               SECURITY_DESCRIPTOR_RELATIVE pSelfRelativeSD,
                               IntByReference lpdwBufferLength);

    /**
     * The MakeAbsoluteSD function creates a security descriptor in absolute format by using a
     * security descriptor in self-relative format as a template.
     * @param pSelfRelativeSD
     *              A pointer to a SECURITY_DESCRIPTOR structure in self-relative format. The function creates an
     *              absolute-format version of this security descriptor without modifying the original security descriptor.
     * @param pAbsoluteSD
     *              A pointer to a buffer that the function fills with the main body of an absolute-format security
     *              descriptor. This information is formatted as a SECURITY_DESCRIPTOR structure.
     * @param lpdwAbsoluteSDSize
     *              A pointer to a variable that specifies the size of the buffer pointed to by the pAbsoluteSD parameter.
     *              If the buffer is not large enough for the security descriptor, the function fails and sets this variable
     *              to the minimum required size.
     * @param pDacl
     *              A pointer to a buffer the function fills with the discretionary access control list (DACL) of the
     *              absolute-format security descriptor. The main body of the absolute-format security descriptor references
     *              this pointer.
     * @param lpdwDaclSize
     *              A pointer to a variable that specifies the size of the buffer pointed to by the pDacl parameter. If
     *              the buffer is not large enough for the access control list (ACL), the function fails and sets this
     *              variable to the minimum required size.
     * @param pSacl
     *              A pointer to a buffer the function fills with the system access control list (SACL) of the absolute-format
     *              security descriptor. The main body of the absolute-format security descriptor references this pointer.
     * @param lpdwSaclSize
     *              A pointer to a variable that specifies the size of the buffer pointed to by the pSacl parameter. If the
     *              buffer is not large enough for the ACL, the function fails and sets this variable to the minimum required
     *              size.
     * @param pOwner
     *              A pointer to a buffer the function fills with the security identifier (SID) of the owner of the
     *              absolute-format security descriptor. The main body of the absolute-format security descriptor references
     *              this pointer.
     * @param lpdwOwnerSize
     *              A pointer to a variable that specifies the size of the buffer pointed to by the pOwner parameter.
     *              If the buffer is not large enough for the SID, the function fails and sets this variable to the minimum
     *              required size.
     * @param pPrimaryGroup
     *              A pointer to a buffer the function fills with the SID of the absolute-format security descriptor's
     *              primary group. The main body of the absolute-format security descriptor references this pointer.
     * @param lpdwPrimaryGroupSize
     *              A pointer to a variable that specifies the size of the buffer pointed to by the pPrimaryGroup parameter.
     *              If the buffer is not large enough for the SID, the function fails and sets this variable to the minimum
     *              required size.
     * @return If the function succeeds, the function returns nonzero. If the function fails, it returns zero. To get
     *         extended error information, call GetLastError. Possible return codes include, but are not limited to, the following:
     *         ERROR_INSUFFICIENT_BUFFER - One or more of the buffers is too small.
     */
    boolean MakeAbsoluteSD(SECURITY_DESCRIPTOR_RELATIVE pSelfRelativeSD,
                           SECURITY_DESCRIPTOR pAbsoluteSD,
                           IntByReference lpdwAbsoluteSDSize,
                           ACL pDacl,
                           IntByReference lpdwDaclSize,
                           ACL pSacl,
                           IntByReference lpdwSaclSize,
                           PSID pOwner,
                           IntByReference lpdwOwnerSize,
                           PSID pPrimaryGroup,
                           IntByReference lpdwPrimaryGroupSize);

    /**
     * The IsValidAcl function validates an access control list (ACL).
     *
     * @param pAcl [in]
     *            A pointer to an ACL structure validated by this function. This value must not be NULL.
     * @return If the ACL is valid, the function returns nonzero. If the ACL is not valid, the function returns zero.
     * There is no extended error information for this function; do not call GetLastError.
     *
     * This function checks the revision level of the ACL and verifies that the number of access control entries
     * (ACEs) specified in the AceCount member of the ACL structure fits the space specified by the AclSize member
     * of the ACL structure.If pAcl is NULL, the application will fail with an access violation.
     */
    boolean IsValidAcl(Pointer pAcl);

    /**
     * Applies the given mapping of generic access rights to the given access mask.
     * @param AccessMask [in, out] A pointer to an access mask.
     * @param GenericMapping [in] A pointer to a GENERIC_MAPPING structure specifying a mapping of generic access types to specific and standard access types.
     */
    void MapGenericMask(DWORDByReference AccessMask, GENERIC_MAPPING GenericMapping);


    /**
     * Check if the if the security descriptor grants access to the given client token.
     *
     * @param pSecurityDescriptor [in] A pointer to a SECURITY_DESCRIPTOR structure against which access is checked.
     * @param ClientToken [in] A handle to an impersonation token that represents the client that is attempting to gain access. The handle must have TOKEN_QUERY access to the token; otherwise, the function fails with ERROR_ACCESS_DENIED.
     * @param DesiredAccess [in] Access mask that specifies the access rights to check. This mask must have been mapped by the MapGenericMask function to contain no generic access rights.<br>
     *                      If this parameter is MAXIMUM_ALLOWED, the function sets the GrantedAccess access mask to indicate the maximum access rights the security descriptor allows the client.
     * @param GenericMapping [in] A pointer to the GENERIC_MAPPING structure associated with the object for which access is being checked.
     * @param PrivilegeSet [out, optional] A pointer to a PRIVILEGE_SET structure that receives the privileges used to perform the access validation. If no privileges were used, the function sets the PrivilegeCount member to zero.
     * @param PrivilegeSetLength [in, out] Specifies the size, in bytes, of the buffer pointed to by the PrivilegeSet parameter.
     * @param GrantedAccess [out] A pointer to an access mask that receives the granted access rights. If AccessStatus is set to FALSE, the function sets the access mask to zero. If the function fails, it does not set the access mask.
     * @param AccessStatus [out] A pointer to a variable that receives the results of the access check. If the security descriptor allows the requested access rights to the client identified by the access token, AccessStatus is set to TRUE. Otherwise, AccessStatus is set to FALSE, and you can call GetLastError to get extended error information.
     * @return true on success; false on failure (use GetLastError to get extended error information)
     */
    boolean AccessCheck(Pointer pSecurityDescriptor,
                        HANDLE ClientToken, DWORD DesiredAccess,
                        GENERIC_MAPPING GenericMapping,
                        PRIVILEGE_SET PrivilegeSet,
                        DWORDByReference PrivilegeSetLength,
                        DWORDByReference GrantedAccess, BOOLByReference AccessStatus);

    /**
     * Encrypts a file or directory. All data streams in a file are encrypted. All
     * new files created in an encrypted directory are encrypted.
     *
     * @param lpFileName
     *         The name of the file or directory to be encrypted.
     * @return If the function succeeds, the return value is nonzero. If the
     * function fails, the return value is zero. To get extended error
     * information, call GetLastError.
     */
    boolean EncryptFile(String lpFileName);

    /**
     * Decrypts an encrypted file or directory.
     *
     * @param lpFileName
     *         The name of the file or directory to be decrypted.
     * @param dwReserved
     *         Reserved; must be zero.
     * @return If the function succeeds, the return value is nonzero. If the
     * function fails, the return value is zero. To get extended error
     * information, call GetLastError.
     */
    boolean DecryptFile(String lpFileName, DWORD dwReserved);

    /**
     * Retrieves the encryption status of the specified file.
     *
     * @param lpFileName
     *         The name of the file.
     * @param lpStatus
     *         A pointer to a variable that receives the encryption status of the
     *         file.
     * @return If the function succeeds, the return value is nonzero. If the
     * function fails, the return value is zero. To get extended error
     * information, call GetLastError.
     */
    boolean FileEncryptionStatus(String lpFileName, DWORDByReference lpStatus);

    /**
     * Disables or enables encryption of the specified directory and the files in
     * it. It does not affect encryption of subdirectories below the indicated
     * directory.
     *
     * @param DirPath
     *         The name of the directory for which to enable or disable
     *         encryption.
     * @param Disable
     *         Indicates whether to disable encryption (TRUE) or enable it
     *         (FALSE).
     * @return If the function succeeds, the return value is nonzero. If the
     * function fails, the return value is zero. To get extended error
     * information, call GetLastError.
     */
    boolean EncryptionDisable(String DirPath, boolean Disable);

    /**
     * Opens an encrypted file in order to backup (export) or restore (import) the
     * file. This is one of a group of Encrypted File System (EFS) functions that
     * is intended to implement backup and restore functionality, while
     * maintaining files in their encrypted state.
     *
     * @param lpFileName
     *         The name of the file to be opened. The string must consist of
     *         characters from the Windows character set.
     * @param ulFlags
     *         The operation to be performed.
     * @param pvContext
     *         The address of a context block that must be presented in subsequent
     *         calls to ReadEncryptedFileRaw, WriteEncryptedFileRaw, or
     *         CloseEncryptedFileRaw. Do not modify it.
     * @return If the function succeeds, it returns ERROR_SUCCESS. If the function
     * fails, it returns a nonzero error code defined in WinError.h. You can use
     * FormatMessage with the FORMAT_MESSAGE_FROM_SYSTEM flag to get a generic
     * text description of the error.
     */
    int OpenEncryptedFileRaw(String lpFileName, ULONG ulFlags, PointerByReference pvContext);

    /**
     * Backs up (export) encrypted files. This is one of a group of Encrypted File
     * System (EFS) functions that is intended to implement backup and restore
     * functionality, while maintaining files in their encrypted state.
     *
     * @param pfExportCallback
     *         A pointer to the export callback function. The system calls the
     *         callback function multiple times, each time passing a block of the
     *         file's data to the callback function until the entire file has been
     *         read. For more information, see ExportCallback.
     * @param pvCallbackContext
     *         A pointer to an application-defined and allocated context block.
     *         The system passes this pointer to the callback function as a
     *         parameter so that the callback function can have access to
     *         application-specific data. This can be a structure and can contain
     *         any data the application needs, such as the handle to the file that
     *         will contain the backup copy of the encrypted file.
     * @param pvContext
     *         A pointer to a system-defined context block. The context block is
     *         returned by the OpenEncryptedFileRaw function. Do not modify it.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If the
     * function fails, it returns a nonzero error code defined in WinError.h. You
     * can use FormatMessage with the FORMAT_MESSAGE_FROM_SYSTEM flag to get a
     * generic text description of the error.
     */
    int ReadEncryptedFileRaw(FE_EXPORT_FUNC pfExportCallback,
                             Pointer pvCallbackContext, Pointer pvContext);

    /**
     * Restores (import) encrypted files. This is one of a group of Encrypted File
     * System (EFS) functions that is intended to implement backup and restore
     * functionality, while maintaining files in.
     *
     * @param pfImportCallback
     *         A pointer to the import callback function. The system calls the
     *         callback function multiple times, each time passing a buffer that
     *         will be filled by the callback function with a portion of backed-up
     *         file's data. When the callback function signals that the entire
     *         file has been processed, it tells the system that the restore
     *         operation is finished. For more information, see ImportCallback.
     * @param pvCallbackContext
     *         A pointer to an application-defined and allocated context block.
     *         The system passes this pointer to the callback function as a
     *         parameter so that the callback function can have access to
     *         application-specific data. This can be a structure and can contain
     *         any data the application needs, such as the handle to the file that
     *         will contain the backup copy of the encrypted file.
     * @param pvContext
     *         A pointer to a system-defined context block. The context block is
     *         returned by the OpenEncryptedFileRaw function. Do not modify it.
     * @return If the function succeeds, the return value is ERROR_SUCCESS. If the
     * function fails, it returns a nonzero error code defined in WinError.h. You
     * can use FormatMessage with the FORMAT_MESSAGE_FROM_SYSTEM flag to get a
     * generic text description of the error.
     */
    int WriteEncryptedFileRaw(FE_IMPORT_FUNC pfImportCallback,
                              Pointer pvCallbackContext, Pointer pvContext);

    /**
     * Closes an encrypted file after a backup or restore operation, and frees
     * associated system resources. This is one of a group of Encrypted File
     * System (EFS) functions that is intended to implement backup and restore
     * functionality, while maintaining files in their encrypted state.
     *
     * @param pvContext
     *         A pointer to a system-defined context block. The
     *         OpenEncryptedFileRaw function returns the context block.
     */
    void CloseEncryptedFileRaw(Pointer pvContext);

    /**
     * <code>
     BOOL WINAPI CreateProcessWithLogonW(
     _In_         LPCWSTR lpUsername,
     _In_opt_     LPCWSTR lpDomain,
     _In_         LPCWSTR lpPassword,
     _In_         DWORD dwLogonFlags,
     _In_opt_     LPCWSTR lpApplicationName,
     _Inout_opt_  LPWSTR lpCommandLine,
     _In_         DWORD dwCreationFlags,
     _In_opt_     LPVOID lpEnvironment,
     _In_opt_     LPCWSTR lpCurrentDirectory,
     _In_         LPSTARTUPINFOW lpStartupInfo,
     _Out_        LPPROCESS_INFORMATION lpProcessInfo
     );
     * </code>
     *
     * @param lpUsername
     *            [in]<br>
     *            The name of the user. This is the name of the user account to
     *            log on to. <br>
     *            If you use the UPN format, user@DNS_domain_name, the lpDomain
     *            parameter must be NULL.<br>
     *            The user account must have the Log On Locally permission on
     *            the local computer. <br>
     *            This permission is granted to all users on workstations and
     *            servers, but only to administrators on domain controllers.
     * @param lpDomain
     *            [in, optional]<br>
     *            The name of the domain or server whose account database
     *            contains the lpUsername account. <br>
     *            If this parameter is NULL, the user name must be specified in
     *            UPN format.
     * @param lpPassword
     *            [in]<br>
     *            The clear-text password for the lpUsername account.
     * @param dwLogonFlags
     *            [in]<br>
     *            The logon option. This parameter can be 0 (zero) or one of the
     *            following values. <br>
     *            LOGON_WITH_PROFILE: 0x00000001<br>
     *            Log on, then load the user profile in the HKEY_USERS registry
     *            key.<br>
     *            The function returns after the profile is loaded. <br>
     *            Loading the profile can be time-consuming, so it is best to
     *            use this value only if you must access the information in the
     *            HKEY_CURRENT_USER registry key.<br>
     *            Windows Server 2003: The profile is unloaded after the new
     *            process is terminated, whether or not it has created child
     *            processes.<br>
     *            Windows XP: The profile is unloaded after the new process and
     *            all child processes it has created are terminated.<br>
     *            <br>
     *            LOGON_NETCREDENTIALS_ONLY: 0x00000002<br>
     *            Log on, but use the specified credentials on the network only.
     *            <br>
     *            The new process uses the same token as the caller, but the
     *            system creates a new logon session within LSA, and the process
     *            uses the specified credentials as the default credentials.
     *            <br>
     *            This value can be used to create a process that uses a
     *            different set of credentials locally than it does remotely.
     *            <br>
     *            This is useful in inter-domain scenarios where there is no
     *            trust relationship.<br>
     *            The system does not validate the specified credentials.<br>
     *            Therefore, the process can start, but it may not have access
     *            to network resources.
     * @param lpApplicationName
     *            [in, optional]<br>
     *            The name of the module to be executed. This module can be a
     *            Windows-based application.<br>
     *            It can be some other type of module (for example, MS-DOS or
     *            OS/2) if the appropriate subsystem is available on the local
     *            computer. The string can specify the full path and file name
     *            of the module to execute or it can specify a partial name.
     *            <br>
     *            If it is a partial name, the function uses the current drive
     *            and current directory to complete the specification. <br>
     *            The function does not use the search path. This parameter must
     *            include the file name extension; no default extension is
     *            assumed. The lpApplicationName parameter can be NULL, and the
     *            module name must be the first white space-delimited token in
     *            the lpCommandLine string.<br>
     *            If you are using a long file name that contains a space, use
     *            quoted strings to indicate where the file name ends and the
     *            arguments begin; otherwise, the file name is ambiguous. For
     *            example, the following string can be interpreted in different
     *            ways:<br>
     *            "c:\program files\sub dir\program name"<br>
     *            The system tries to interpret the possibilities in the
     *            following order:<br>
     *            <br>
     *            c:\program.exe files\sub dir\program name<br>
     *            c:\program files\sub.exe dir\program name<br>
     *            c:\program files\sub dir\program.exe name<br>
     *            c:\program files\sub dir\program name.exe<br>
     *            <br>
     *            If the executable module is a 16-bit application,
     *            lpApplicationName should be NULL, and the string pointed to by
     *            lpCommandLine should specify the executable module and its
     *            arguments.
     * @param lpCommandLine
     *            [in, out, optional]<br>
     *            The command line to be executed. The maximum length of this
     *            string is 1024 characters. <br>
     *            If lpApplicationName is NULL, the module name portion of
     *            lpCommandLine is limited to MAX_PATH characters.<br>
     *            The function can modify the contents of this string. <br>
     *            Therefore, this parameter cannot be a pointer to read-only
     *            memory (such as a const variable or a literal string).<br>
     *            If this parameter is a constant string, the function may cause
     *            an access violation.<br>
     *            The lpCommandLine parameter can be NULL, and the function uses
     *            the string pointed to by lpApplicationNameas the command line.
     *            <br>
     *            <br>
     *            If both lpApplicationName and lpCommandLine are non-NULL,
     *            *lpApplicationName specifies the module to execute, and
     *            *lpCommandLine specifies the command line.<br>
     *            The new process can use GetCommandLine to retrieve the entire
     *            command line.<br>
     *            Console processes written in C can use the argc and argv
     *            arguments to parse the command line. <br>
     *            Because argv[0] is the module name, C programmers typically
     *            repeat the module name as the first token in the command line.
     *            <br>
     *            If lpApplicationName is NULL, the first white space-delimited
     *            token of the command line specifies the module name.<br>
     *            If you are using a long file name that contains a space, use
     *            quoted strings to indicate where the file name ends and the
     *            arguments begin (see the explanation for the lpApplicationName
     *            parameter). If the file name does not contain an extension,
     *            .exe is appended. Therefore, if the file name extension is
     *            .com, this parameter must include the .com extension. If the
     *            file name ends in a period with no extension, or if the file
     *            name contains a path, .exe is not appended. If the file name
     *            does not contain a directory path, the system searches for the
     *            executable file in the following sequence:<br>
     *            <br>
     *            1. The directory from which the application loaded.<br>
     *            2. The current directory for the parent process.<br>
     *            3. The 32-bit Windows system directory. Use the
     *            GetSystemDirectory function to get the path of this directory.
     *            <br>
     *            4. The 16-bit Windows system directory. There is no function
     *            that obtains the path of this directory, but it is searched.
     *            <br>
     *            5. The Windows directory. Use the GetWindowsDirectory function
     *            to get the path of this directory.<br>
     *            6. The directories that are listed in the PATH environment
     *            variable. Note that this function does not search the
     *            per-application path specified by the App Paths registry key.
     *            To include this per-application path in the search sequence,
     *            use the ShellExecute function.<br>
     *            <br>
     *            The system adds a null character to the command line string to
     *            separate the file name from the arguments. This divides the
     *            original string into two strings for internal processing.<br>
     * @param dwCreationFlags
     *            The flags that control how the process is created. <br>
     *            The CREATE_DEFAULT_ERROR_MODE, CREATE_NEW_CONSOLE, and
     *            CREATE_NEW_PROCESS_GROUP flags are enabled by default. <br>
     *            Even if you do not set the flag, the system functions as if it
     *            were set. You can specify additional flags as noted.<br>
     *            <br>
     *            CREATE_DEFAULT_ERROR_MODE: 0x04000000<br>
     *            The new process does not inherit the error mode of the calling
     *            process. <br>
     *            Instead, CreateProcessWithLogonW gives the new process the
     *            current default error mode. <br>
     *            An application sets the current default error mode by calling
     *            SetErrorMode. This flag is enabled by default.<br>
     *            <br>
     *            CREATE_NEW_CONSOLE: 0x00000010<br>
     *            The new process has a new console, instead of inheriting the
     *            parent's console. This flag cannot be used with the
     *            DETACHED_PROCESS flag.<br>
     *            This flag is enabled by default.<br>
     *            <br>
     *            CREATE_NEW_PROCESS_GROUP: 0x00000200<br>
     *            The new process is the root process of a new process group.
     *            <br>
     *            The process group includes all processes that are descendants
     *            of this root process.<br>
     *            The process identifier of the new process group is the same as
     *            the process identifier, which is returned in the lpProcessInfo
     *            parameter.<br>
     *            Process groups are used by the GenerateConsoleCtrlEvent
     *            function to enable sending a CTRL+C or CTRL+BREAK signal to a
     *            group of console processes.<br>
     *            This flag is enabled by default.<br>
     *            <br>
     *            CREATE_SEPARATE_WOW_VDM: 0x00000800<br>
     *            This flag is only valid starting a 16-bit Windows-based
     *            application.<br>
     *            If set, the new process runs in a private Virtual DOS Machine
     *            (VDM).<br>
     *            By default, all 16-bit Windows-based applications run in a
     *            single, shared VDM.<br>
     *            The advantage of running separately is that a crash only
     *            terminates the single VDM; any other programs running in
     *            distinct VDMs continue to function normally.<br>
     *            Also, 16-bit Windows-based applications that run in separate
     *            VDMs have separate input queues, which means that if one
     *            application stops responding momentarily, applications in
     *            separate VDMs continue to receive input. <br>
     *            CREATE_SUSPENDED: 0x00000004<br>
     *            The primary thread of the new process is created in a
     *            suspended state, and does not run until the ResumeThread
     *            function is called.<br>
     *            <br>
     *            CREATE_UNICODE_ENVIRONMENT: 0x00000400<br>
     *            Indicates the format of the lpEnvironment parameter.<br>
     *            If this flag is set, the environment block pointed to by
     *            lpEnvironment uses Unicode characters. <br>
     *            Otherwise, the environment block uses ANSI characters. <br>
     *            EXTENDED_STARTUPINFO_PRESENT: 0x00080000<br>
     *            The process is created with extended startup information; the
     *            lpStartupInfo parameter specifies a STARTUPINFOEX structure.
     *            <br>
     *            Windows Server 2003 and Windows XP: This value is not
     *            supported.<br>
     * @param lpEnvironment
     *            [in, optional]<br>
     *            A pointer to an environment block for the new process.<br>
     *            If this parameter is NULL, the new process uses an environment
     *            created from the profile of the user specified by lpUsername.
     *            An environment block consists of a null-terminated block of
     *            null-terminated strings.<br>
     *            Each string is in the following form:<br>
     *            name=value<br>
     *            Because the equal sign (=) is used as a separator, it must not
     *            be used in the name of an environment variable.<br>
     *            An environment block can contain Unicode or ANSI characters.
     *            <br>
     *            If the environment block pointed to by lpEnvironment contains
     *            Unicode characters, ensure that dwCreationFlags includes
     *            CREATE_UNICODE_ENVIRONMENT.<br>
     *            If this parameter is NULL and the environment block of the
     *            parent process contains Unicode characters, you must also
     *            ensure that dwCreationFlags includes
     *            CREATE_UNICODE_ENVIRONMENT.<br>
     *            An ANSI environment block is terminated by two 0 (zero) bytes:
     *            one for the last string and one more to terminate the block.
     *            <br>
     *            A Unicode environment block is terminated by four zero bytes:
     *            two for the last string and two more to terminate the block.
     *            <br>
     *            To retrieve a copy of the environment block for a specific
     *            user, use the CreateEnvironmentBlock function.<br>
     * @param lpCurrentDirectory
     *            [in, optional]<br>
     *            The full path to the current directory for the process.<br>
     *            The string can also specify a UNC path.<br>
     *            If this parameter is NULL, the new process has the same
     *            current drive and directory as the calling process.<br>
     *            This feature is provided primarily for shells that need to
     *            start an application, and specify its initial drive and
     *            working directory.<br>
     * @param lpStartupInfo
     *            [in]<br>
     *            A pointer to a STARTUPINFO or STARTUPINFOEX structure. <br>
     *            The application must add permission for the specified user
     *            account to the specified window station and desktop, even for
     *            WinSta0\Default.<br>
     *            If the lpDesktop member is NULL or an empty string, the new
     *            process inherits the desktop and window station of its parent
     *            process.<br>
     *            The application must add permission for the specified user
     *            account to the inherited window station and desktop.<br>
     *            Windows XP: CreateProcessWithLogonW adds permission for the
     *            specified user account to the inherited window station and
     *            desktop.<br>
     *            Handles in STARTUPINFO or STARTUPINFOEX must be closed with
     *            CloseHandle when they are no longer needed.<br>
     *            Important If the dwFlags member of the STARTUPINFO structure
     *            specifies STARTF_USESTDHANDLES, the standard handle fields are
     *            copied unchanged to the child process without validation.<br>
     *            The caller is responsible for ensuring that these fields
     *            contain valid handle values. Incorrect values can cause the
     *            child process to misbehave or crash.<br>
     *            Use the Application Verifier runtime verification tool to
     *            detect invalid handles.
     * @param lpProcessInfo
     *            [out]<br>
     *            A pointer to a PROCESS_INFORMATION structure that receives
     *            identification information for the new process, including a
     *            handle to the process.<br>
     *            Handles in PROCESS_INFORMATION must be closed with the
     *            CloseHandle function when they are not needed.<br>
     * @return If the function succeeds, the return value is nonzero.<br>
     *         If the function fails, the return value is 0 (zero).<br>
     *         To get extended error information, call GetLastError.<br>
     *         Note that the function returns before the process has finished
     *         initialization.<br>
     *         If a required DLL cannot be located or fails to initialize, the
     *         process is terminated.<br>
     *         To get the termination status of a process, call
     *         GetExitCodeProcess.
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms682431%28v=vs.85%29.aspx">MSDN</a>
     */
    boolean CreateProcessWithLogonW(String lpUsername, String lpDomain, String lpPassword, int dwLogonFlags,
                                    String lpApplicationName, String lpCommandLine, int dwCreationFlags, Pointer lpEnvironment,
                                    String lpCurrentDirectory, STARTUPINFO lpStartupInfo, PROCESS_INFORMATION lpProcessInfo);

    /**
     * Connects the main thread of a service process to the service control
     * manager, which causes the thread to be the service control dispatcher
     * thread for the calling process.
     *
     * @param lpServiceTable A pointer to an array of SERVICE_TABLE_ENTRY
     *                       structures containing one entry for each service
     *                       that can execute in the calling process. The
     *                       members of the last entry in the table must have
     *                       NULL values to designate the end of the table.
     * 
     * @return true if function succeeds. To get extended error information, call
     * GetLastError. Possible error codes:
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_FAILED_SERVICE_CONTROLLER_CONNECT</td><td>This error is returned if the program is being run as a console application rather than as a service. If the program will be run as a console application for debugging purposes, structure it such that service-specific code is not called when this error is returned.</td></tr>
     * <tr><td>ERROR_INVALID_DATA</td><td>The specified dispatch table contains entries that are not in the proper format.</td></tr>
     * <tr><td>ERROR_SERVICE_ALREADY_RUNNING</td><td>The process has already called StartServiceCtrlDispatcher. Each process can call StartServiceCtrlDispatcher only one time.</td></tr>
     * </table>
     */
    public boolean StartServiceCtrlDispatcher(SERVICE_TABLE_ENTRY[] lpServiceTable);

    /**
     * Registers a function to handle service control requests.
     *
     * <p>This function has been superseded by the RegisterServiceCtrlHandlerEx
     * function. A service can use either function, but the new function
     * supports user-defined context data, and the new handler function supports
     * additional extended control codes.</p>
     *
     * @param lpServiceName The name of the service run by the calling thread.
     *                      This is the service name that the service control
     *                      program specified in the CreateService function when
     *                      creating the service.
     *
     *                      <p>If the service type is SERVICE_WIN32_OWN_PROCESS,
     *                      the function does not verify that the specified name
     *                      is valid, because there is only one registered
     *                      service in the process.</p>
     *
     * @param lpHandlerProc A pointer to the handler function to be registered.
     *                      For more information, see
     *                      {@link com.sun.jna.platform.win32.Winsvc.Handler WinSvc.Handler}.
     *
     * @return A service status handle, NULL on error. Call GetLastError to
     * get extended error condition. Possible error codes:
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_NOT_ENOUGH_MEMORY</td><td>Not enough memory is available to convert an ANSI string parameter to Unicode. This error does not occur for Unicode string parameters.</td></tr>
     * <tr><td>ERROR_SERVICE_NOT_IN_EXE</td><td>The service entry was specified incorrectly when the process called the {@link #StartServiceCtrlDispatcher} function.</td></tr>
     * </table>
     */
    public SERVICE_STATUS_HANDLE RegisterServiceCtrlHandler(String lpServiceName,
            Handler lpHandlerProc);

    /**
     * Registers a function to handle extended service control requests.
     *
     * @param lpServiceName The name of the service run by the calling thread.
     *                      This is the service name that the service control
     *                      program specified in the CreateService function when
     *                      creating the service.
     * @param lpHandlerProc The handler function to be registered.
     *                      For more information, see HandlerEx.
     * @param lpContext     Any user-defined data. This parameter, which is
     *                      passed to the handler function, can help identify
     *                      the service when multiple services share a process.
     *
     * @return A service status handle on success, NULL on error. Call GetLastError
     * to get extended information. Possible error codes:
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_NOT_ENOUGH_MEMORY</td><td>Not enough memory is available to convert an ANSI string parameter to Unicode. This error does not occur for Unicode string parameters.</td></tr>
     * <tr><td>ERROR_SERVICE_NOT_IN_EXE</td><td>The service entry was specified incorrectly when the process called the {@link #StartServiceCtrlDispatcher} function.</td></tr>
     * </table>
     */
    public SERVICE_STATUS_HANDLE RegisterServiceCtrlHandlerEx(String lpServiceName,
            HandlerEx lpHandlerProc, Pointer lpContext);

    /**
     * Updates the service control manager's status information for the calling
     * service.
     *
     *
     * @param hServiceStatus  A handle to the status information structure for
     *                        the current service. This handle is returned by
     *                        the RegisterServiceCtrlHandlerEx function.
     * @param lpServiceStatus A pointer to the SERVICE_STATUS structure the
     *                        contains the latest status information for the
     *                        calling service.
     *  
     * @return true if function succeeds. To get extended error information, call
     * GetLastError. Possible error codes:
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_INVALID_DATA</td><td>The specified service status structure is invalid.</td></tr>
     * <tr><td>ERROR_INVALID_HANDLE</td><td>The specified handle is invalid.</td></tr>
     * </table>
     */
    public boolean SetServiceStatus(SERVICE_STATUS_HANDLE hServiceStatus,
            SERVICE_STATUS lpServiceStatus);

    /**
     * Creates a service object and adds it to the specified service control
     * manager database.
     *
     * @param hSCManager         [in] A handle to the service control manager
     *                           database. This handle is returned by the
     *                           OpenSCManager function and must have the
     *                           SC_MANAGER_CREATE_SERVICE access right. For
     *                           more information, see Service Security and
     *                           Access Rights.
     * @param lpServiceName      [in] The name of the service to install. The
     *                           maximum string length is 256 characters. The
     *                           service control manager database preserves the
     *                           case of the characters, but service name
     *                           comparisons are always case insensitive.
     *                           Forward-slash (/) and backslash (\) are not
     *                           valid service name characters.
     * @param lpDisplayName      [in, optional] The display name to be used by
     *                           user interface programs to identify the
     *                           service. This string has a maximum length of
     *                           256 characters. The name is case-preserved in
     *                           the service control manager. Display name
     *                           comparisons are always case-insensitive.
     * @param dwDesiredAccess    [in] The access to the service. Before granting
     *                           the requested access, the system checks the
     *                           access token of the calling process. For a list
     *                           of values, see Service Security and Access
     *                           Rights.
     * @param dwServiceType      [in] The service type. This parameter can be
     *                           one of the following values.
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>SERVICE_ADAPTER<br>0x00000004</td><td>Reserved.</td></tr>
     * <tr><td>SERVICE_FILE_SYSTEM_DRIVER<br>0x00000002</td><td>File system driver service.</td></tr>
     * <tr><td>SERVICE_KERNEL_DRIVER<br>0x00000001</td><td>Driver service.</td></tr>
     * <tr><td>SERVICE_RECOGNIZER_DRIVER<br>0x00000008</td><td>Reserved.</td></tr>
     * <tr><td>SERVICE_WIN32_OWN_PROCESS<br>0x00000010</td><td>Service that runs in its own process.</td></tr>
     * <tr><td>SERVICE_WIN32_SHARE_PROCESS<br>0x00000020</td><td>Service that shares a process with one or more other services. For more information, see Service Programs.</td></tr>
     * </table>
     * 
     * <p>If you specify either SERVICE_WIN32_OWN_PROCESS or SERVICE_WIN32_SHARE_PROCESS, and the service is running in the context of the LocalSystem account, you can also specify the following value.</p>
     * 
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>SERVICE_INTERACTIVE_PROCESS<br>0x00000100</td><td>The service can interact with the desktop.</td></tr>
     * </table>
     * 
     * @param dwStartType        [in] The service start options. This parameter
     *                           can be one of the following values.
     * 
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>SERVICE_AUTO_START<br>0x00000002</td><td>A service started automatically by the service control manager during system startup.</td></tr>
     * <tr><td>SERVICE_BOOT_START<br>0x00000000</td><td>A device driver started by the system loader. This value is valid only for driver services.</td></tr>
     * <tr><td>SERVICE_DEMAND_START<br>0x00000003</td><td>A service started by the service control manager when a process calls the StartService function.</td></tr>
     * <tr><td>SERVICE_DISABLED<br>0x00000004</td><td>A service that cannot be started. Attempts to start the service result in the error code ERROR_SERVICE_DISABLED.</td></tr>
     * <tr><td>SERVICE_SYSTEM_START<br>0x00000001</td><td>A device driver started by the IoInitSystem function. This value is valid only for driver services.</td></tr>
     * </table>
     *
     * @param dwErrorControl     [in] The severity of the error, and action
     *                           taken, if this service fails to start. This
     *                           parameter can be one of the following values.
     * 
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>SERVICE_ERROR_CRITICAL<br>0x00000003</td><td>The startup program logs the error in the event log, if possible. If the last-known-good configuration is being started, the startup operation fails. Otherwise, the system is restarted with the last-known good configuration.</td></tr>
     * <tr><td>SERVICE_ERROR_IGNORE<br>0x00000000</td><td>The startup program ignores the error and continues the startup operation.</td></tr>
     * <tr><td>SERVICE_ERROR_NORMAL<br>0x00000001</td><td>The startup program logs the error in the event log but continues the startup operation.</td></tr>
     * <tr><td>SERVICE_ERROR_SEVERE<br>0x00000002</td><td>The startup program logs the error in the event log. If the last-known-good configuration is being started, the startup operation continues. Otherwise, the system is restarted with the last-known-good configuration.</td></tr>
     * </table>
     *
     * @param lpBinaryPathName   [in, optional] The fully qualified path to the
     *                           service binary file. If the path contains a
     *                           space, it must be quoted so that it is
     *                           correctly interpreted. For example, "d:\\my
     *                           share\\myservice.exe" should be specified as
     *                           "\"d:\\my share\\myservice.exe\"".
     *
     *                           <p>The path can also include arguments for an 
     *                           auto-start service. For example, 
     *                           "d:\\myshare\\myservice.exe arg1 arg2". These
     *                           passed to the service entry point (typically 
     *                           the main function).</p>
     *
     *                           <p>If you specify a path on another computer, 
     *                           the share must be accessible by the computer 
     *                           account of the local computer because this is 
     *                           the security context used in the remote call. 
     *                           However, this requirement allows any potential
     *                           vulnerabilities in the remote computer to 
     *                           affect the local computer. Therefore, it is
     *                           best to use a local file.</p>
     * 
     * @param lpLoadOrderGroup   [in, optional] The names of the load ordering
     *                           group of which this service is a member.
     *                           Specify NULL or an empty string if the service
     *                           does not belong to a group.
     *
     *                           <p>The startup program uses load ordering 
     *                           groups to load groups of services in a 
     *                           specified order with respect to the other 
     *                           groups. The list of load ordering groups is
     *                           contained in the following registry value:</p>
     *
     * <p>HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\ServiceGroupOrder</p>
     * @param lpdwTagId          [out, optional] A pointer to a variable that
     *                           receives a tag value that is unique in the
     *                           group specified in the lpLoadOrderGroup
     *                           parameter. Specify NULL if you are not changing
     *                           the existing tag.
     *
     *                           <p>You can use a tag for ordering service 
     *                           startup within a load ordering group by 
     *                           specifying a tag order vector in the following
     *                           registry value:</p>
     *
     * <p>HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\GroupOrderList</p>
     *
     *                           <p>Tags are only evaluated for driver services
     *                           that have SERVICE_BOOT_START or 
     *                           SERVICE_SYSTEM_START start types.</p>
     * @param lpDependencies     [in, optional] A pointer to a double
     *                           null-terminated array of null-separated names
     *                           of services or load ordering groups that the
     *                           system must start before this service. Specify
     *                           NULL or an empty string if the service has no
     *                           dependencies. Dependency on a group means that
     *                           this service can run if at least one member of
     *                           the group is running after an attempt to start
     *                           all members of the group.
     *
     *                           <p>You must prefix group names with 
     *                           SC_GROUP_IDENTIFIER so that they can be
     *                           distinguished from a service name, because 
     *                           services and service groups share the same name
     *                           space.</p>
     * @param lpServiceStartName [in, optional] The name of the account under
     *                           which the service should run. If the service
     *                           type is SERVICE_WIN32_OWN_PROCESS, use an
     *                           account name in the form DomainName\UserName.
     *                           The service process will be logged on as this
     *                           user. If the account belongs to the built-in
     *                           domain, you can specify .\UserName.
     *
     *                           <p>If this parameter is NULL, CreateService 
     *                           uses the LocalSystem account. If the service 
     *                           type specifies SERVICE_INTERACTIVE_PROCESS, the
     *                           service must run in the LocalSystem account.</p>
     *
     *                           <p>If this parameter is NT AUTHORITY\LocalService,
     *                           CreateService uses the LocalService account. If
     *                           the parameter is NT AUTHORITY\NetworkService,
     *                           CreateService uses the NetworkService account.</p>
     *
     *                           <p>A shared process can run as any user.</p>
     *
     *                           <p>If the service type is SERVICE_KERNEL_DRIVER
     *                           or SERVICE_FILE_SYSTEM_DRIVER, the name is the
     *                           driver object name that the system uses to load
     *                           the device driver. Specify NULL if the driver
     *                           is to use a default object name created by the
     *                           I/O system.</p>
     *
     *                           <p>A service can be configured to use a managed
     *                           account or a virtual account. If the service is
     *                           configured to use a managed service account,
     *                           the name is the managed service account name.
     *                           If the service is configured to use a virtual
     *                           account, specify the name as NT
     *                           SERVICE\ServiceName. For more information about
     *                           managed service accounts and virtual accounts, 
     *                           see the Service Accounts Step-by-Step Guide.
     *
     * <p><strong>Windows Server 2008, Windows Vista, Windows Server 2003 and Windows XP:</strong>
     * Managed service accounts and virtual accounts are not supported until
     * Windows 7 and Windows Server 2008 R2.</p>
     * @param lpPassword         [in, optional] The password to the account name
     *                           specified by the lpServiceStartName parameter.
     *                           Specify an empty string if the account has no
     *                           password or if the service runs in the
     *                           LocalService, NetworkService, or LocalSystem
     *                           account. For more information, see Service
     *                           Record List.
     *
     *                           <p>If the account name specified by the 
     *                           lpServiceStartName parameter is the name of a 
     *                           managed service account or virtual account
     *                           name, the lpPassword parameter must be NULL.</p>
     *
     *                           <p>Passwords are ignored for driver services.</p>
     *
     * @return SC_HANDLE on success, NULL on error. Call GetLastError to
     * get extended error condition. Possible error codes:
     * 
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_ACCESS_DENIED</td><td>The handle to the SCM database does not have the SC_MANAGER_CREATE_SERVICE access right.</td></tr>
     * <tr><td>ERROR_CIRCULAR_DEPENDENCY</td><td>A circular service dependency was specified.</td></tr>
     * <tr><td>ERROR_DUPLICATE_SERVICE_NAME</td><td>The display name already exists in the service control manager database either as a service name or as another display name.</td></tr>
     * <tr><td>ERROR_INVALID_HANDLE</td><td>The handle to the specified service control manager database is invalid.</td></tr>
     * <tr><td>ERROR_INVALID_NAME</td><td>The specified service name is invalid.</td></tr>
     * <tr><td>ERROR_INVALID_PARAMETER</td><td>A parameter that was specified is invalid.</td></tr>
     * <tr><td>ERROR_INVALID_SERVICE_ACCOUNT</td><td>The user account name specified in the lpServiceStartName parameter does not exist.</td></tr>
     * <tr><td>ERROR_SERVICE_EXISTS</td><td>The specified service already exists in this database.</td></tr>
     * <tr><td>ERROR_SERVICE_MARKED_FOR_DELETE</td><td>The specified service already exists in this database and has been marked for deletion.</td></tr>
     * </table>
     */
    public SC_HANDLE CreateService(SC_HANDLE hSCManager, String lpServiceName,
            String lpDisplayName, int dwDesiredAccess, int dwServiceType,
            int dwStartType, int dwErrorControl, String lpBinaryPathName,
            String lpLoadOrderGroup, IntByReference lpdwTagId,
            String lpDependencies, String lpServiceStartName, String lpPassword);

    /**
     * Marks the specified service for deletion from the service control manager database.
     * 
     * @param hService [in] A handle to the service. This handle is returned by
     *                 the OpenService or CreateService function, and it must
     *                 have the DELETE access right.
     * 
     * @return true if function succeeds. To get extended error information, call
     * GetLastError. Possible error codes:
     * 
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_ACCESS_DENIED</td><td>The handle does not have the DELETE access right.</td></tr>
     * <tr><td>ERROR_INVALID_HANDLE</td><td>The specified handle is invalid.</td></tr>
     * <tr><td>ERROR_SERVICE_MARKED_FOR_DELETE</td><td>The specified service has already been marked for deletion.</td></tr>
     * </table>
     */
    public boolean DeleteService(SC_HANDLE hService);
}
