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
import com.sun.jna.platform.win32.DsGetDC.PDOMAIN_CONTROLLER_INFO;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.NTSecApi.PLSA_FOREST_TRUST_INFORMATION;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Netapi32.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface Netapi32 extends StdCallLibrary {
	
	Netapi32 INSTANCE = Native.loadLibrary("Netapi32", Netapi32.class, W32APIOptions.DEFAULT_OPTIONS);

	/**
	 * Retrieves join status information for the specified computer.
	 * 
	 * @param lpServer
	 *  Specifies the DNS or NetBIOS name of the computer on which to
	 *  call the function.
	 * @param lpNameBuffer
	 *  Receives the NetBIOS name of the domain or workgroup to which
	 *  the computer is joined.
	 * @param BufferType
	 *  Join status of the specified computer.
	 * @return If the function succeeds, the return value is NERR_Success. If
	 *         the function fails, the return value is a system error code.
	 */
	public int NetGetJoinInformation(String lpServer,
			PointerByReference lpNameBuffer, IntByReference BufferType);

	/**
	 * Frees the memory that the NetApiBufferAllocate function allocates.
	 * 
	 * @param buffer buffer
	 * @return If the function succeeds, the return value is NERR_Success. If
	 *         the function fails, the return value is a system error code.
	 */
	public int NetApiBufferFree(Pointer buffer);

	/**
	 * Returns information about each local group account on the specified
	 * server.
	 * 
	 * @param serverName
	 *  Specifies the DNS or NetBIOS name of the remote server on
	 *  which the function is to execute. If this parameter is NULL,
	 *  the local computer is used.
	 * @param level
	 *  Specifies the information level of the data.
	 * @param bufptr
	 *  Pointer to the address of the buffer that receives the
	 *  information structure.
	 * @param prefmaxlen
	 *  Specifies the preferred maximum length of returned data, in
	 *  bytes.
	 * @param entriesread
	 *  Pointer to a value that receives the count of elements
	 *  actually enumerated.
	 * @param totalentries
	 *  Pointer to a value that receives the approximate total number
	 *  of entries that could have been enumerated from the current
	 *  resume position.
	 * @param resume_handle
	 *  Pointer to a value that contains a resume handle that is used
	 *  to continue an existing local group search.
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

	/**
	 * The NetGroupEnum function retrieves information about each global group 
	 * in the security database, which is the security accounts manager (SAM) database or,
	 * in the case of domain controllers, the Active Directory.
	 * @param servername
	 *  Pointer to a constant string that specifies the DNS or NetBIOS name of the 
	 *  remote server on which the function is to execute. If this parameter is NULL, 
	 *  the local computer is used. 
	 * @param level
	 *  Specifies the information level of the data. 
	 * @param bufptr
	 *  Pointer to the buffer to receive the global group information structure. 
	 *  The format of this data depends on the value of the level parameter. 
	 * @param prefmaxlen
	 *  Specifies the preferred maximum length of the returned data, in bytes. 
	 *  If you specify MAX_PREFERRED_LENGTH, the function allocates the amount of 
	 *  memory required to hold the data. If you specify another value in this 
	 *  parameter, it can restrict the number of bytes that the function returns. 
	 *  If the buffer size is insufficient to hold all entries, the function 
	 *  returns ERROR_MORE_DATA.
	 * @param entriesread
	 *  Pointer to a value that receives the count of elements actually enumerated. 
	 * @param totalentries
	 *  Pointer to a value that receives the total number of entries that could have 
	 *  been enumerated from the current resume position. The total number of entries 
	 *  is only a hint.
	 * @param resume_handle
	 *  Pointer to a variable that contains a resume handle that is used to continue 
	 *  the global group enumeration. The handle should be zero on the first call and 
	 *  left unchanged for subsequent calls. If resume_handle is NULL, no resume handle 
	 *  is stored. 
	 * @return
	 *  If the function succeeds, the return value is NERR_Success.
	 */
	public int NetGroupEnum(String servername, int level, PointerByReference bufptr,
			int prefmaxlen, IntByReference entriesread, IntByReference totalentries,
			IntByReference resume_handle);

	/**
	 * The NetUserEnum function provides information about all user accounts on a server.	 
	 * @param servername
	 *  Pointer to a constant string that specifies the DNS or NetBIOS name of the 
	 *  remote server on which the function is to execute. If this parameter is NULL, 
	 *  the local computer is used. 
	 * @param level
	 *  Specifies the information level of the data.
	 * @param filter
	 *  Specifies a value that filters the account types for enumeration.
	 * @param bufptr
	 *  Pointer to the buffer that receives the data. The format of this data depends 
	 *  on the value of the level parameter. This buffer is allocated by the system and
	 *  must be freed using the NetApiBufferFree function. Note that you must free the
	 *  buffer even if the function fails with ERROR_MORE_DATA. 
	 * @param prefmaxlen
	 *  Specifies the preferred maximum length, in 8-bit bytes of returned data. If you 
	 *  specify MAX_PREFERRED_LENGTH, the function allocates the amount of memory 
	 *  required for the data. If you specify another value in this parameter, it can
	 *  restrict the number of bytes that the function returns. If the buffer size is
	 *  insufficient to hold all entries, the function returns ERROR_MORE_DATA. 
	 * @param entriesread
	 *  Pointer to a value that receives the count of elements actually enumerated. 
	 * @param totalentries
	 *  Pointer to a value that receives the total number of entries that could have 
	 *  been enumerated from the current resume position. Note that applications should 
	 *  consider this value only as a hint.
	 * @param resume_handle
	 *  Pointer to a value that contains a resume handle which is used to continue an 
	 *  existing user search. The handle should be zero on the first call and left 
	 *  unchanged for subsequent calls. If resume_handle is NULL, then no resume 
	 *  handle is stored. 
	 * @return
	 *  If the function succeeds, the return value is NERR_Success.
	 */
	public int NetUserEnum(String servername, int level, int filter, PointerByReference bufptr,
			int prefmaxlen, IntByReference entriesread, IntByReference totalentries,
			IntByReference resume_handle);

	/**
	 * The NetUserGetGroups function retrieves a list of global groups to which a 
	 * specified user belongs.
	 * @param servername
	 *  Pointer to a constant string that specifies the DNS or NetBIOS name of the 
	 *  remote server on which the function is to execute. If this parameter is NULL, 
	 *  the local computer is used. 
	 * @param username
	 *  Pointer to a constant string that specifies the name of the user to search for 
	 *  in each group account. For more information, see the following Remarks section. 
	 * @param level
	 *  Specifies the information level of the data.
	 * @param bufptr
	 *  Pointer to the buffer that receives the data. This buffer is allocated by the 
	 *  system and must be freed using the NetApiBufferFree function. Note that you must 
	 *  free the buffer even if the function fails with ERROR_MORE_DATA. 
	 * @param prefmaxlen
	 *  Specifies the preferred maximum length of returned data, in bytes. If you specify 
	 *  MAX_PREFERRED_LENGTH, the function allocates the amount of memory required for the 
	 *  data. If you specify another value in this parameter, it can restrict the number 
	 *  of bytes that the function returns. If the buffer size is insufficient to hold 
	 *  all entries, the function returns ERROR_MORE_DATA.
	 * @param entriesread
	 *  Pointer to a value that receives the count of elements actually retrieved. 
	 * @param totalentries
	 *  Pointer to a value that receives the total number of entries that could have been retrieved. 
	 * @return
	 *  If the function succeeds, the return value is NERR_Success.
	 */
	public int NetUserGetGroups(String servername, String username, int level,
			PointerByReference bufptr, int prefmaxlen,
			IntByReference entriesread, IntByReference totalentries);
 
	/**
	 * The NetUserGetLocalGroups function retrieves a list of local groups to which a
	 * specified user belongs.
	 * @param servername
	 *  Pointer to a constant string that specifies the DNS or NetBIOS name of the remote 
	 *  server on which the function is to execute. If this parameter is NULL, the local 
	 *  computer is used. 
	 * @param username
	 *  Pointer to a constant string that specifies the name of the user for which to return
	 *  local group membership information. If the string is of the form DomainName\UserName 
	 *  the user name is expected to be found on that domain. If the string is of the form 
	 *  UserName, the user name is expected to be found on the server specified by the 
	 *  servername parameter.
	 * @param level
	 *  Specifies the information level of the data.
	 * @param flags
	 *  Specifies a bitmask of flags. Currently, only the value LG_INCLUDE_INDIRECT is 
	 *  defined. If this bit is set, the function also returns the names of the local 
	 *  groups in which the user is indirectly a member (that is, the user has membership 
	 *  in a global group that is itself a member of one or more local groups). 
	 * @param bufptr
	 *  Pointer to the buffer that receives the data. The format of this data depends on 
	 *  the value of the level parameter. This buffer is allocated by the system and must 
	 *  be freed using the NetApiBufferFree function. Note that you must free the buffer 
	 *  even if the function fails with ERROR_MORE_DATA. 
	 * @param prefmaxlen
	 *  Specifies the preferred maximum length of returned data, in bytes. If you specify 
	 *  MAX_PREFERRED_LENGTH, the function allocates the amount of memory required for the 
	 *  data. If you specify another value in this parameter, it can restrict the number of 
	 *  bytes that the function returns. If the buffer size is insufficient to hold all 
	 *  entries, the function returns ERROR_MORE_DATA. For more information, see Network 
	 *  Management Function Buffers and Network Management Function Buffer Lengths. 
	 * @param entriesread
	 *  Pointer to a value that receives the count of elements actually enumerated.
	 * @param totalentries
	 *  Pointer to a value that receives the total number of entries that could have been enumerated. 
	 * @return
	 *  If the function succeeds, the return value is NERR_Success.
	 */
	public int NetUserGetLocalGroups(String servername, String username, int level,
			int flags, PointerByReference bufptr, int prefmaxlen,
			IntByReference entriesread, IntByReference totalentries);
	
	/**
	 * The NetUserAdd function adds a user account and assigns a password and privilege level.
	 * @param servername
	 *  Pointer to a constant string that specifies the DNS or NetBIOS name of the remote server 
	 *  on which the function is to execute.
	 * @param level
	 *  Specifies the information level of the data.
	 * @param buf
	 *  Pointer to the buffer that specifies the data. The format of this data depends on the 
	 *  value of the level parameter.
	 * @param parm_err
	 *  Pointer to a value that receives the index of the first member of the user information 
	 *  structure that causes ERROR_INVALID_PARAMETER. If this parameter is NULL, the index is 
	 *  not returned on error. 
	 * @return
	 *  If the function succeeds, the return value is NERR_Success.
	 */
	public int NetUserAdd(String servername, int level, 
			Structure buf, IntByReference parm_err);
	
	
	/**
	 * The NetUserDel function deletes a user account from a server.
	 * @param servername
	 *  Pointer to a constant string that specifies the DNS or NetBIOS name of the remote 
	 *  server on which the function is to execute. If this parameter is NULL, the local 
	 *  computer is used. 
	 * @param username
	 *  Pointer to a constant string that specifies the name of the user account to delete. 
	 * @return
	 *  If the function succeeds, the return value is NERR_Success.
	 */
	public int NetUserDel(String servername, String username);		
	
	/**
	 * The NetUserChangePassword function changes a user's password for a specified
	 * network server or domain.
	 * @param domainname
	 *  Pointer to a constant string that specifies the DNS or NetBIOS name of a remote 
	 *  server or domain on which the function is to execute. If this parameter is NULL, 
	 *  the logon domain of the caller is used. 
	 * @param username
	 *  Pointer to a constant string that specifies a user name. The NetUserChangePassword 
	 *  function changes the password for the specified user. If this parameter is NULL, 
	 *  the logon name of the caller is used.
	 * @param oldpassword
	 *  Pointer to a constant string that specifies the user's old password. 
	 * @param newpassword
	 *  Pointer to a constant string that specifies the user's new password. 
	 * @return
	 *  If the function succeeds, the return value is NERR_Success.
	 */
	public int NetUserChangePassword(String domainname, String username, 
			String oldpassword, String newpassword);
	
	/**
	 * The DsGetDcName function returns the name of a domain controller in a specified domain. 
	 * This function accepts additional domain controller selection criteria to indicate 
	 * preference for a domain controller with particular characteristics.
	 * @param ComputerName
	 *  Pointer to a null-terminated string that specifies the name of the server to process 
	 *  this function. Typically, this parameter is NULL, which indicates that the local 
	 *  computer is used. 
	 * @param DomainName
	 *  Pointer to a null-terminated string that specifies the name of the domain or application
	 *  partition to query. This name can either be a DNS style name, for example, fabrikam.com,
	 *  or a flat-style name, for example, Fabrikam. If a DNS style name is specified, the name 
	 *  may be specified with or without a trailing period.
	 * @param DomainGuid
	 *  Pointer to a GUID structure that specifies the GUID of the domain queried. If DomainGuid 
	 *  is not NULL and the domain specified by DomainName or ComputerName cannot be found, 
	 *  DsGetDcName attempts to locate a domain controller in the domain having the GUID specified 
	 *  by DomainGuid.
	 * @param SiteName
	 *  Pointer to a null-terminated string that specifies the name of the site where the returned 
	 *  domain controller should physically exist. If this parameter is NULL, DsGetDcName attempts 
	 *  to return a domain controller in the site closest to the site of the computer specified by 
	 *  ComputerName. This parameter should be NULL, by default.
	 * @param Flags
	 *  Contains a set of flags that provide additional data used to process the request.
	 * @param DomainControllerInfo
	 *  Pointer to a PDOMAIN_CONTROLLER_INFO value that receives a pointer to a 
	 *  DOMAIN_CONTROLLER_INFO structure that contains data about the domain controller selected. 
	 *  This structure is allocated by DsGetDcName. The caller must free the structure using 
	 *  the NetApiBufferFree function when it is no longer required.
	 * @return
	 *  If the function returns domain controller data, the return value is ERROR_SUCCESS.
	 *  If the function fails, the return code is one of ERROR_* values.
	 */
	public int DsGetDcName(String ComputerName, String DomainName, GUID DomainGuid,
            String SiteName, int Flags, PDOMAIN_CONTROLLER_INFO DomainControllerInfo);
	
	/**
	 * The DsGetForestTrustInformationW function obtains forest trust data for a specified domain.
	 * @param serverName
	 *  Contains the name of the domain controller that DsGetForestTrustInformationW 
	 *  is connected to remotely. The caller must be an authenticated user on this server. 
	 *  If this parameter is NULL, the local server is used.
	 * @param trustedDomainName
	 *  Contains the NETBIOS or DNS name of the trusted domain that the forest trust data 
	 *  is to be retrieved for. This domain must have the TRUST_ATTRIBUTE_FOREST_TRANSITIVE 
	 *  trust attribute. If this parameter is NULL, the forest trust data for the domain 
	 *  hosted by ServerName is retrieved.
	 * @param Flags
	 *  Contains a set of flags that modify the behavior of this function.
	 *  DS_GFTI_UPDATE_TDO: If this flag is set, DsGetForestTrustInformationW will update the 
	 *  forest trust data of the trusted domain identified by the TrustedDomainName parameter. 
	 * @param ForestTrustInfo
	 *  Pointer to an LSA_FOREST_TRUST_INFORMATION structure pointer that receives the forest 
	 *  trust data that describes the namespaces claimed by the domain specified by 
	 *  TrustedDomainName. The Time member of all returned records will be zero. 
	 * @return
	 *  Returns NO_ERROR if successful or a Win32 error code otherwise. 
	 */
	public int DsGetForestTrustInformation(String serverName, String trustedDomainName, int Flags, 
			PLSA_FOREST_TRUST_INFORMATION ForestTrustInfo);
	
	/**
	 * The DsEnumerateDomainTrusts function obtains domain trust data for a specified domain.
	 * @param serverName
	 *  Pointer to a null-terminated string that specifies the name of a computer in the domain to 
	 *  obtain the trust information for. This computer must be running the Windows 2000 or later 
	 *  operating system. If this parameter is NULL, the name of the local computer is used. 
	 *  The caller must be an authenticated user in this domain.
	 * @param Flags
	 *  Contains a set of flags that determines which domain trusts to enumerate.
	 * @param Domains
	 *  Receives a pointer which points to an array of DS_DOMAIN_TRUSTS structures. 
	 *  Each structure in this array contains trust data about a domain. The caller must free this 
	 *  memory when it is no longer required by calling NetApiBufferFree.
	 * @param DomainCount
	 *  Pointer to a ULONG value that receives the number of elements returned in the Domains array.
	 * @return
	 *  Returns ERROR_SUCCESS if successful or a Win32 error code otherwise.
	 */
	public int DsEnumerateDomainTrusts(String serverName, int Flags, 
			PointerByReference Domains, IntByReference DomainCount);
	
	/**
	 * The NetUserGetInfo function retrieves information about a particular user account on a server.
	 * @param servername
	 * A pointer to a constant string that specifies the DNS or NetBIOS name of the remote server on 
	 * which the function is to execute. If this parameter is NULL, the local computer is used.
	 * @param username
	 * A pointer to a constant string that specifies the name of the user account for which to return information. 
	 * For more information, see the following Remarks section.
	 * @param level
	 * The information level of the data. This parameter can be one of the following values.
	 * Value Meaning
	 * 0     Return the user account name. The bufptr parameter points to a USER_INFO_0 structure.
	 * 1     Return detailed information about the user account. The bufptr parameter points to a USER_INFO_1 structure.
	 * 2     Return detailed information and additional attributes about the user account. The bufptr parameter points to a USER_INFO_2 structure.
	 * 3     Return detailed information and additional attributes about the user account. This level is valid only on servers. The bufptr parameter points to a USER_INFO_3 structure. Note that it is recommended that you use USER_INFO_4 instead.
	 * 4     Return detailed information and additional attributes about the user account. This level is valid only on servers. The bufptr parameter points to a USER_INFO_4 structure. Windows 2000:  This level is not supported.
	 * 10    Return user and account names and comments. The bufptr parameter points to a USER_INFO_10 structure.
	 * 11    Return detailed information about the user account. The bufptr parameter points to a USER_INFO_11 structure.
	 * 20    Return the user's name and identifier and various account attributes. The bufptr parameter points to a USER_INFO_20 structure. Note that on Windows XP and later, it is recommended that you use USER_INFO_23 instead.
	 * 23    Return the user's name and identifier and various account attributes. The bufptr parameter points to a USER_INFO_23 structure.  Windows 2000:  This level is not supported.
	 * @param bufptr
	 * A pointer to the buffer that receives the data. 
	 * The format of this data depends on the value of the level parameter. 
	 * This buffer is allocated by the system and must be freed using the NetApiBufferFree function. 
	 * For more information, see Network Management Function Buffers and Network Management Function Buffer Lengths.
	 * @return
	 *  If the function succeeds, the return value is NERR_Success.
	 */
	public int NetUserGetInfo(String servername, String username, int level, PointerByReference bufptr);

    /**
     * Shares a server resource.
     * 
     * @param servername [in]
     *  Pointer to a string that specifies the DNS or NetBIOS name of the remote server on which the function is to execute.
     *  If this parameter is NULL, the local computer is used.
     * @param level [in]
     *  Specifies the information level of the data. This parameter can be one of the following values:
     *  2 - Specifies information about the shared resource, including the name of the resource, type and permissions, and number of connections.
     *  The buf parameter points to a SHARE_INFO_2 structure.
     *  502 - Specifies information about the shared resource, including the name of the resource, type and permissions, number of connections, and other pertinent information.
     *  The buf parameter points to a SHARE_INFO_502 structure.
     *  503 - Specifies information about the shared resource, including the name of the resource, type and permissions, number of connections, and other pertinent information.
     *  The buf parameter points to a SHARE_INFO_503 structure.
     * @param buf [in]
     *  Pointer to the buffer that specifies the data. The format of this data depends on the value of the <code>level</code> parameter.
     *  For more information, see Network Management Function Buffers (https://msdn.microsoft.com/en-us/library/windows/desktop/aa370676(v=vs.85).aspx)
     * @param parm_err [out]
     *  Pointer to a value that receives the index of the first member of the share information structure that causes the ERROR_INVALID_PARAMETER error. If this parameter is NULL, the
     *  index is not returned on error. For more information, see the NetShareSetInfo function.
     * @return If the function succeeds, the return value is NERR_Success. If the function fails, the return value can be an error code as seen on MSDN.
     */
    public int NetShareAdd(String servername, int level, Pointer buf, IntByReference parm_err);

    /**
     * Deletes a share name from a server's list of shared resources, disconnecting all connections to the shared resource.
     * 
     * @param servername [in]
     *  Pointer to a string that specifies the DNS or NetBIOS name of the remote server on which the function is to execute.
     *  If this parameter is NULL, the local computer is used.
     * @param netname [in]
     *  Pointer to a string that specifies the name of the share to delete.
     * @param reserved
     *  Reserved, must be zero.
     * @return If the function succeeds, the return value is LMErr.NERR_Success.
     *  If the function fails, the return value can be an error code as seen on MSDN.
     */
    public int NetShareDel(String servername, String netname, int reserved);
}
