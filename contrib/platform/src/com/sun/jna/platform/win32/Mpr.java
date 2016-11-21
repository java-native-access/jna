/* Copyright (c) 2015 Adam Marcionek, All Rights Reserved
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
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.Winnetwk.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Ported from Winnetwk.h. Microsoft Windows SDK 8.1
 * 
 * @author amarcionek[at]gmail.com
 */

public interface Mpr extends StdCallLibrary {

    Mpr INSTANCE = Native.loadLibrary("Mpr", Mpr.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * The WNetOpenEnum function starts an enumeration of network resources or
     * existing connections. You can continue the enumeration by calling the
     * WNetEnumResource function.
     * 
     * @param dwScope
     *            Scope of the enumeration. This parameter can be one of the
     *            following values from NETRESOURCEScope: RESOURCE_CONNECTED,
     *            RESOURCE_CONTEXT, RESOURCE_GLOBALNET, RESOURCE_REMEMBERED
     * @param dwType
     *            Resource types to be enumerated. This parameter can be a
     *            combination of the following values from NETRESOURCEType:
     *            RESOURCETYPE_ANY, RESOURCETYPE_DISK, RESOURCETYPE_PRINT
     * @param dwUsage
     *            Resource usage type to be enumerated. This parameter can be a
     *            combination of the following values from NETRESOURCEUsage: 0,
     *            RESOURCEUSAGE_CONNECTABLE, RESOURCEUSAGE_CONTAINER,
     *            RESOURCEUSAGE_ATTACHED, RESOURCEUSAGE_ALL
     * @param lpNETRESOURCE
     *            Pointer to a NETRESOURCE structure that specifies the
     *            container to enumerate. If the dwScope parameter is not
     *            RESOURCE_GLOBALNET, this parameter must be NULL. If this
     *            parameter is NULL, the root of the network is assumed. (The
     *            system organizes a network as a hierarchy; the root is the
     *            topmost container in the network.) If this parameter is not
     *            NULL, it must point to a NETRESOURCE structure. This structure
     *            can be filled in by the application or it can be returned by a
     *            call to the WNetEnumResource function. The NETRESOURCE
     *            structure must specify a container resource; that is, the
     *            RESOURCEUSAGE_CONTAINER value must be specified in the dwUsage
     *            parameter. To enumerate all network resources, an application
     *            can begin the enumeration by calling WNetOpenEnum with the
     *            lpNETRESOURCE parameter set to NULL, and then use the returned
     *            handle to call WNetEnumResource to enumerate resources. If one
     *            of the resources in the NETRESOURCE array returned by the
     *            WNetEnumResource function is a container resource, you can
     *            call WNetOpenEnum to open the resource for further
     *            enumeration.
     * @param lphEnum
     *            Pointer to an enumeration handle that can be used in a
     *            subsequent call to WNetEnumResource.
     * @return <code>NO_ERROR</code> if the function succeeds, otherwise a
     *         system error code. See MSDN documentation for common error
     *         values:
     *         https://msdn.microsoft.com/en-us/library/windows/desktop/aa385478
     *         (v=vs.85).aspx
     */
    public int WNetOpenEnum(int dwScope, int dwType, int dwUsage, NETRESOURCE.ByReference lpNETRESOURCE, HANDLEByReference lphEnum);

    /**
     * The WNetEnumResource function continues an enumeration of network
     * resources that was started by a call to the WNetOpenEnum function.
     * 
     * @param hEnum
     *            [in] Handle that identifies an enumeration instance. This
     *            handle must be returned by the WNetOpenEnum function.
     * @param lpcCount
     *            [in, out] Pointer to a variable specifying the number of
     *            entries requested. If the number requested is -1, the function
     *            returns as many entries as possible. If the function succeeds,
     *            on return the variable pointed to by this parameter contains
     *            the number of entries actually read.
     * @param lpBuffer
     *            [out] Pointer to the buffer that receives the enumeration
     *            results. The results are returned as an array of NETRESOURCE
     *            structures. Note that the buffer you allocate must be large
     *            enough to hold the structures, plus the strings to which their
     *            members point. For more information, see the Remarks section
     *            on MSDN:
     *            https://msdn.microsoft.com/en-us/library/windows/desktop/
     *            aa385449(v=vs.85).aspx The buffer is valid until the next call
     *            using the handle specified by the hEnum parameter. The order
     *            of NETRESOURCE structures in the array is not predictable.
     * @param lpBufferSize
     *            [in, out] Pointer to a variable that specifies the size of the
     *            lpBuffer parameter, in bytes. If the buffer is too small to
     *            receive even one entry, this parameter receives the required
     *            size of the buffer.
     * @return If the function succeeds, the return value is one of the
     *         following values: NO_ERROR - The enumeration succeeded, and the
     *         buffer contains the requested data. The calling application can
     *         continue to call WNetEnumResource to complete the enumeration.
     *         ERROR_NO_MORE_ITEMS - There are no more entries. The buffer
     *         contents are undefined. If the function fails, see MSDN
     *         documentation for common error values:
     *         https://msdn.microsoft.com/en-us/library/windows/desktop/aa385478
     *         (v=vs.85).aspx
     */
    public int WNetEnumResource(HANDLE hEnum, IntByReference lpcCount, Pointer lpBuffer, IntByReference lpBufferSize);

    /**
     * The WNetCloseEnum function ends a network resource enumeration started by
     * a call to the WNetOpenEnum function.
     * 
     * @param hEnum
     *            [in] Handle that identifies an enumeration instance. This
     *            handle must be returned by the WNetOpenEnum function.
     * @return <code>NO_ERROR</code> if the function succeeds, otherwise a
     *         system error code. See MSDN documentation for common error
     *         values:
     *         https://msdn.microsoft.com/en-us/library/windows/desktop/aa385431
     *         (v=vs.85).aspx
     */
    int WNetCloseEnum(HANDLE hEnum);

    /**
     * The WNetGetUniversalName function takes a drive-based path for a network
     * resource and returns an information structure that contains a more
     * universal form of the name.
     * 
     * @param lpLocalPath
     *            [in] A pointer to a constant null-terminated string that is a
     *            drive-based path for a network resource. For example, if drive
     *            H has been mapped to a network drive share, and the network
     *            resource of interest is a file named Sample.doc in the
     *            directory \Win32\Examples on that share, the drive-based path
     *            is H:\Win32\Examples\Sample.doc.
     * @param dwInfoLevel
     *            [in] The type of structure that the function stores in the
     *            buffer pointed to by the lpBuffer parameter. This parameter
     *            can be one of the following values defined in the
     *            Winnetwk.java. UNIVERSAL_NAME_INFO_LEVEL - The function stores
     *            a UNIVERSAL_NAME_INFO structure in the buffer.
     *            REMOTE_NAME_INFO_LEVEL - The function stores a
     *            REMOTE_NAME_INFO structure in the buffer. The
     *            UNIVERSAL_NAME_INFO structure points to a Universal Naming
     *            Convention (UNC) name string. The REMOTE_NAME_INFO structure
     *            points to a UNC name string and two additional connection
     *            information strings. For more information, see the following
     *            Remarks section.
     * @param lpBuffer
     *            [out] A pointer to a buffer that receives the structure
     *            specified by the dwInfoLevel parameter.
     * @param lpBufferSize
     *            [in,out] A pointer to a variable that specifies the size, in
     *            bytes, of the buffer pointed to by the lpBuffer parameter. If
     *            the function succeeds, it sets the variable pointed to by
     *            lpBufferSize to the number of bytes stored in the buffer. If
     *            the function fails because the buffer is too small, this
     *            location receives the required buffer size, and the function
     *            returns ERROR_MORE_DATA.
     * @return If the function succeeds, the return value is NO_ERROR, otherwise
     *         see MSDN for common error codes:
     *         https://msdn.microsoft.com/en-us/library/windows/desktop/aa385474
     *         (v=vs.85).aspx
     */
    int WNetGetUniversalName(String lpLocalPath, int dwInfoLevel, Pointer lpBuffer, IntByReference lpBufferSize);

    /**
     * The WNetUseConnection function makes a connection to a network resource.
     * The function can redirect a local device to a network resource.
     * 
     * The WNetUseConnection function is similar to the WNetAddConnection3
     * function. The main difference is that WNetUseConnection can automatically
     * select an unused local device to redirect to the network resource.
     * 
     * @param hwndOwner
     *            [in] Handle to a window that the provider of network resources
     *            can use as an owner window for dialog boxes. Use this
     *            parameter if you set the CONNECT_INTERACTIVE value in the
     *            dwFlags parameter.
     * @param lpNETRESOURCE
     *            [in] Pointer to a NETRESOURCE structure that specifies details
     *            of the proposed connection. The structure contains information
     *            about the network resource, the local device, and the network
     *            resource provider.
     * 
     *            You must specify the following members of the NETRESOURCE
     *            structure. The WNetUseConnection function ignores the other
     *            members of the NETRESOURCE structure. For more information,
     *            see the descriptions following for the dwFlags parameter.
     * 
     *            dwType Specifies the type of resource to connect to. It is
     *            most efficient to specify a resource type in this member, such
     *            as RESOURCETYPE_DISK or RESOURCETYPE_PRINT. However, if the
     *            lpLocalName member is NULL, or if it points to an empty string
     *            and CONNECT_REDIRECT is not set, dwType can be
     *            RESOURCETYPE_ANY.
     * 
     *            This method works only if the function does not automatically
     *            choose a device to redirect to the network resource. Although
     *            this member is required, its information may be ignored by the
     *            network service provider lpLocalName Pointer to a
     *            null-terminated string that specifies the name of a local
     *            device to be redirected, such as "F:" or "LPT1". The string is
     *            treated in a case-insensitive manner. If the string is empty,
     *            or if lpLocalName is NULL, a connection to the network occurs
     *            without redirection. If the CONNECT_REDIRECT value is set in
     *            the dwFlags parameter, or if the network requires a redirected
     *            local device, the function chooses a local device to redirect
     *            and returns the name of the device in the lpAccessName
     *            parameter. lpRemoveName Pointer to a null-terminated string
     *            that specifies the network resource to connect to. The string
     *            can be up to MAX_PATH characters in length, and it must follow
     *            the network provider's naming conventions. lpProvider Pointer
     *            to a null-terminated string that specifies the network
     *            provider to connect to. If lpProvider is NULL, or if it points
     *            to an empty string, the operating system attempts to determine
     *            the correct provider by parsing the string pointed to by the
     *            lpRemoteName member. If this member is not NULL, the operating
     *            system attempts to make a connection only to the named network
     *            provider. You should set this member only if you know the
     *            network provider you want to use. Otherwise, let the operating
     *            system determine which provider the network name maps to.
     * @param lpPassword
     *            [in] Pointer to a constant null-terminated string that
     *            specifies a password to be used in making the network
     *            connection. If lpPassword is NULL, the function uses the
     *            current default password associated with the user specified by
     *            lpUserID. If lpPassword points to an empty string, the
     *            function does not use a password. If the connection fails
     *            because of an invalid password and the CONNECT_INTERACTIVE
     *            value is set in the dwFlags parameter, the function displays a
     *            dialog box asking the user to type the password.
     * @param lpUserID
     *            [in] Pointer to a constant null-terminated string that
     *            specifies a user name for making the connection. If lpUserID
     *            is NULL, the function uses the default user name. (The user
     *            context for the process provides the default user name.) The
     *            lpUserID parameter is specified when users want to connect to
     *            a network resource for which they have been assigned a user
     *            name or account other than the default user name or account.
     *            The user-name string represents a security context. It may be
     *            specific to a network provider. For security context, see
     *            https://msdn.microsoft.com/en-us/library/windows/desktop/
     *            ms721625(v=vs.85).aspx
     * @param dwFlags
     *            [in] Set of bit flags describing the connection. This
     *            parameter can be any combination of the values in ConnectFlag.
     * @param lpAccessName
     *            [out] Pointer to a buffer that receives system requests on the
     *            connection. This parameter can be NULL. If this parameter is
     *            specified, and the lpLocalName member of the NETRESOURCE
     *            structure specifies a local device, this buffer receives the
     *            local device name. If lpLocalName does not specify a device
     *            and the network requires a local device redirection, or if the
     *            CONNECT_REDIRECT value is set, this buffer receives the name
     *            of the redirected local device. Otherwise, the name copied
     *            into the buffer is that of a remote resource. If specified,
     *            this buffer must be at least as large as the string pointed to
     *            by the lpRemoteName member.
     * @param lpBufferSize
     *            [in, out] Pointer to a variable that specifies the size of the
     *            lpAccessName buffer, in characters. If the call fails because
     *            the buffer is not large enough, the function returns the
     *            required buffer size in this location. For more information,
     *            see the descriptions of the lpAccessName parameter and the
     *            ERROR_MORE_DATA error code in the Return Values section.
     * @param lpResult
     *            [out] Pointer to a variable that receives additional
     *            information about the connection. This parameter can be the
     *            following value:
     * 
     *            ConnectFlag.CONNECT_LOCALDRIVE - If this flag is set, the
     *            connection was made using a local device redirection. If the
     *            lpAccessName parameter points to a buffer, the local device
     *            name is copied to the buffer.
     * @return <code>NO_ERROR</code> if the function succeeds, otherwise a
     *         system error code. See MSDN documentation for common error
     *         values:
     *         https://msdn.microsoft.com/en-us/library/windows/desktop/aa385482
     *         (v=vs.85).aspx
     */
    public int WNetUseConnection(HWND hwndOwner, NETRESOURCE lpNETRESOURCE, String lpPassword, String lpUserID, int dwFlags,
            PointerByReference lpAccessName, IntByReference lpBufferSize, IntByReference lpResult);

    /**
     * The WNetAddConnection3 function makes a connection to a network resource.
     * The function can redirect a local device to the network resource.
     * 
     * @param hwndOwner
     *            [in] Handle to a window that the provider of network resources
     *            can use as an owner window for dialog boxes. Use this
     *            parameter if you set the CONNECT_INTERACTIVE value in the
     *            dwFlags parameter.
     * @param lpNETRESOURCE
     *            [in] Pointer to a NETRESOURCE structure that specifies details
     *            of the proposed connection. The structure contains information
     *            about the network resource, the local device, and the network
     *            resource provider.
     * 
     *            You must specify the following members of the NETRESOURCE
     *            structure. The WNetUseConnection function ignores the other
     *            members of the NETRESOURCE structure. For more information,
     *            see the descriptions following for the dwFlags parameter.
     * 
     *            dwType Specifies the type of resource to connect to. It is
     *            most efficient to specify a resource type in this member, such
     *            as RESOURCETYPE_DISK or RESOURCETYPE_PRINT. However, if the
     *            lpLocalName member is NULL, or if it points to an empty string
     *            and CONNECT_REDIRECT is not set, dwType can be
     *            RESOURCETYPE_ANY.
     * 
     *            This method works only if the function does not automatically
     *            choose a device to redirect to the network resource. Although
     *            this member is required, its information may be ignored by the
     *            network service provider lpLocalName Pointer to a
     *            null-terminated string that specifies the name of a local
     *            device to be redirected, such as "F:" or "LPT1". The string is
     *            treated in a case-insensitive manner. If the string is empty,
     *            or if lpLocalName is NULL, a connection to the network occurs
     *            without redirection. If the CONNECT_REDIRECT value is set in
     *            the dwFlags parameter, or if the network requires a redirected
     *            local device, the function chooses a local device to redirect
     *            and returns the name of the device in the lpAccessName
     *            parameter. lpRemoveName Pointer to a null-terminated string
     *            that specifies the network resource to connect to. The string
     *            can be up to MAX_PATH characters in length, and it must follow
     *            the network provider's naming conventions. lpProvider Pointer
     *            to a null-terminated string that specifies the network
     *            provider to connect to. If lpProvider is NULL, or if it points
     *            to an empty string, the operating system attempts to determine
     *            the correct provider by parsing the string pointed to by the
     *            lpRemoteName member. If this member is not NULL, the operating
     *            system attempts to make a connection only to the named network
     *            provider. You should set this member only if you know the
     *            network provider you want to use. Otherwise, let the operating
     *            system determine which provider the network name maps to.
     * @param lpPassword
     *            [in] Pointer to a constant null-terminated string that
     *            specifies a password to be used in making the network
     *            connection. If lpPassword is NULL, the function uses the
     *            current default password associated with the user specified by
     *            lpUserID. If lpPassword points to an empty string, the
     *            function does not use a password. If the connection fails
     *            because of an invalid password and the CONNECT_INTERACTIVE
     *            value is set in the dwFlags parameter, the function displays a
     *            dialog box asking the user to type the password.
     * @param lpUserID
     *            [in] Pointer to a constant null-terminated string that
     *            specifies a user name for making the connection. If lpUserID
     *            is NULL, the function uses the default user name. (The user
     *            context for the process provides the default user name.) The
     *            lpUserID parameter is specified when users want to connect to
     *            a network resource for which they have been assigned a user
     *            name or account other than the default user name or account.
     *            The user-name string represents a security context. It may be
     *            specific to a network provider. For security context, see
     *            https://msdn.microsoft.com/en-us/library/windows/desktop/
     *            ms721625(v=vs.85).aspx
     * @param dwFlags
     *            [in] Set of bit flags describing the connection. This
     *            parameter can be any combination of the values in ConnectFlag.
     */
    public int WNetAddConnection3(HWND hwndOwner, NETRESOURCE lpNETRESOURCE, String lpPassword, String lpUserID, int dwFlags);

    /**
     * The WNetCancelConnection2 function cancels an existing network
     * connection. You can also call the function to remove remembered network
     * connections that are not currently connected.
     * 
     * @param lpName
     *            [in] Pointer to a constant null-terminated string that
     *            specifies the name of either the redirected local device or
     *            the remote network resource to disconnect from. If this
     *            parameter specifies a redirected local device, the function
     *            cancels only the specified device redirection. If the
     *            parameter specifies a remote network resource, all connections
     *            without devices are canceled.
     * @param dwFlags
     *            [in] Connection type. The following values are defined. 0 -
     *            The system does not update information about the connection.
     *            If the connection was marked as persistent in the registry,
     *            the system continues to restore the connection at the next
     *            logon. If the connection was not marked as persistent, the
     *            function ignores the setting of the CONNECT_UPDATE_PROFILE
     *            flag. CONNECT_UPDATE_PROFILE - The system updates the user
     *            profile with the information that the connection is no longer
     *            a persistent one. The system will not restore this connection
     *            during subsequent logon operations. (Disconnecting resources
     *            using remote names has no effect on persistent connections.)
     * @param fForce
     *            [in] Specifies whether the disconnection should occur if there
     *            are open files or jobs on the connection. If this parameter is
     *            FALSE, the function fails if there are open files or jobs.
     * @return <code>NO_ERROR</code> if the function succeeds, otherwise a
     *         system error code. See MSDN documentation for common error
     *         values:
     *         https://msdn.microsoft.com/en-us/library/windows/desktop/aa385482
     *         (v=vs.85).aspx
     */
    public int WNetCancelConnection2(String lpName, int dwFlags, boolean fForce);
}
