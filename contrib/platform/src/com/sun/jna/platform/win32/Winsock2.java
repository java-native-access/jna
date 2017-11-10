/* Copyright (c) 2017 Matthias Bläsing, All Rights Reserved
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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public interface Winsock2 extends Library {

    Winsock2 INSTANCE = (Winsock2) Native.loadLibrary("ws2_32", Winsock2.class, W32APIOptions.ASCII_OPTIONS);

    /**
     * The gethostname function retrieves the standard host name for the local
     * computer.
     *
     * <p>
     * <strong>Remarks</strong></p>
     *
     * <p>
     * The gethostname function returns the name of the local host into the
     * buffer specified by the name parameter. The host name is returned as a
     * null-terminated string. The form of the host name is dependent on the
     * Windows Sockets provider—it can be a simple host name, or it can be a
     * fully qualified domain name. However, it is guaranteed that the name
     * returned will be successfully parsed by gethostbyname and
     * WSAAsyncGetHostByName.</p>
     *
     * <p>
     * The maximum length of the name returned in the buffer pointed to by the
     * name parameter is dependent on the namespace provider.</p>
     *
     * <p>
     * If the gethostname function is used on a cluster resource on Windows
     * Server 2008, Windows Server 2003, or Windows 2000 Server and the
     * _CLUSTER_NETWORK_NAME_ environment variable is defined, then the value in
     * this environment variable overrides the actual hostname and is returned.
     * On a cluster resource, the _CLUSTER_NETWORK_NAME_ environment variable
     * contains the name of the cluster.</p>
     *
     * <p>
     * The gethostname function queries namespace providers to determine the
     * local host name using the SVCID_HOSTNAME GUID defined in the Svgguid.h
     * header file. If no namespace provider responds, then the gethostname
     * function returns the NetBIOS name of the local computer.</p>
     *
     * <p>
     * The maximum length, in bytes, of the string returned in the buffer
     * pointed to by the name parameter is dependent on the namespace provider,
     * but this string must be 256 bytes or less. So if a buffer of 256 bytes is
     * passed in the name parameter and the namelen parameter is set to 256, the
     * buffer size will always be adequate.</p>
     *
     * @param name A bytearray that receives the local host name.
     * @param namelen The length, in bytes, of the buffer pointed to by the name parameter.
     * @return If no error occurs, gethostname returns zero. Otherwise, it returns SOCKET_ERROR and a specific error code can be retrieved by calling WSAGetLastError.
     */
    public int gethostname(byte[] name, int namelen);

}
