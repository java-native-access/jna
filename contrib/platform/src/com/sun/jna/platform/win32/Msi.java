/* This library is free software; you can redistribute it and/or
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
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Provides access to the w32 MSI installer library.
 */
public interface Msi extends StdCallLibrary {

    Msi INSTANCE = (Msi)
        Native.loadLibrary("msi", Msi.class, W32APIOptions.UNICODE_OPTIONS);

    /**
     * The component being requested is disabled on the computer.
     */
    static int INSTALLSTATE_NOTUSED = -7;

    /**
     * The configuration data is corrupt.
     */
    static int INSTALLSTATE_BADCONFIG = -6;

    /**
     * The installation is suspended or in progress.
     */
    static int INSTALLSTATE_INCOMPLETE = -5;

    /**
     * The feature must run from the source, and the source is unavailable.
     */
    static int INSTALLSTATE_SOURCEABSENT = -4;

    /**
     * The return buffer is full.
     */
    static int INSTALLSTATE_MOREDATA = -3;

    /**
     * An invalid parameter was passed to the function.
     */
    static int INSTALLSTATE_INVALIDARG = -2;

    /**
     * An unrecognized product or feature was specified.
     */
    static int INSTALLSTATE_UNKNOWN = -1;

    /**
     * The feature is broken.
     */
    static int INSTALLSTATE_BROKEN =  0;

    /**
     * The advertised feature.
     */
    static int INSTALLSTATE_ADVERTISED =  1;

    /**
     * The component is being removed.
     */
    static int INSTALLSTATE_REMOVED =  1;

    /**
     * The feature was uninstalled.
     */
    static int INSTALLSTATE_ABSENT =  2;

    /**
     * The feature was installed on the local drive.
     */
    static int INSTALLSTATE_LOCAL =  3;

    /**
     * The feature must run from the source, CD-ROM, or network.
     */
    static int INSTALLSTATE_SOURCE =  4;

    /**
     * The feature is installed in the default location: local or source.
     */
    static int INSTALLSTATE_DEFAULT =  5;

    /**
     * The MsiGetComponentPath function returns the full path to an installed component. If the key path for the
     * component is a registry key then the registry key is returned.
     *
     * @param szProduct
     *   Specifies the product code for the client product.
     *
     * @param szComponent
     *   Specifies the component ID of the component to be located.
     *
     * @param lpPathBuf
     *   Pointer to a variable that receives the path to the component. This parameter can be null. If the component is
     *   a registry key, the registry roots are represented numerically. If this is a registry subkey path, there is a
     *   backslash at the end of the Key Path. If this is a registry value key path, there is no backslash at the end.
     *   For example, a registry path on a 32-bit operating system of HKEY_CURRENT_USER\SOFTWARE\Microsoft is returned
     *   as "01:\SOFTWARE\Microsoft\".
     *
     * @param pcchBuf
     *   Pointer to a variable that specifies the size, in characters, of the buffer pointed to by the lpPathBuf
     *   parameter. On input, this is the full size of the buffer, including a space for a terminating null character.
     *   If the buffer passed in is too small, the count returned does not include the terminating null character.
     *
     *   If lpPathBuf is null, pcchBuf can be null.
     *
     * @return
     *   The MsiGetComponentPath function returns the following values.
     *   INSTALLSTATE_NOTUSED - The component being requested is disabled on the computer.
     *   INSTALLSTATE_ABSENT - The component is not installed.
     *   INSTALLSTATE_INVALIDARG - One of the function parameters is invalid.
     *   INSTALLSTATE_LOCAL - The component is installed locally.
     *   INSTALLSTATE_SOURCE - The component is installed to run from source.
     *   INSTALLSTATE_SOURCEABSENT - The component source is inaccessible.
     *   INSTALLSTATE_UNKNOWN - The product code or component ID is unknown.
     */
    int MsiGetComponentPath(String szProduct, String szComponent, char[] lpPathBuf, IntByReference pcchBuf);

    /**
     * The MsiLocateComponent function returns the full path to an installed component without a product code. This
     * function attempts to determine the product using MsiGetProductCode, but is not guaranteed to find the correct
     * product for the caller. MsiGetComponentPath should always be called when possible.
     *
     * @param szComponent
     *   Specifies the component ID of the component to be located.
     *
     * @param lpPathBuf
     *   Pointer to a variable that receives the path to the component. The variable includes the terminating null
     *   character.
     *
     * @param pcchBuf
     *   Pointer to a variable that specifies the size, in characters, of the buffer pointed to by the lpPathBuf
     *   parameter. On input, this is the full size of the buffer, including a space for a terminating null character.
     *   Upon success of the MsiLocateComponent function, the variable pointed to by pcchBuf contains the count of
     *   characters not including the terminating null character. If the size of the buffer passed in is too small, the
     *   function returns INSTALLSTATE_MOREDATA.
     *
     *   If lpPathBuf is null, pcchBuf can be null.
     *
     * @return
     *   The MsiGetComponentPath function returns the following values.
     *   INSTALLSTATE_NOTUSED - The component being requested is disabled on the computer.
     *   INSTALLSTATE_ABSENT - The component is not installed.
     *   INSTALLSTATE_INVALIDARG - One of the function parameters is invalid.
     *   INSTALLSTATE_LOCAL - The component is installed locally.
     *   INSTALLSTATE_MOREDATA - The buffer provided was too small.
     *   INSTALLSTATE_SOURCE - The component is installed to run from source.
     *   INSTALLSTATE_SOURCEABSENT - The component source is inaccessible.
     *   INSTALLSTATE_UNKNOWN - The product code or component ID is unknown.
     */
    int MsiLocateComponent(String szComponent, char[] lpPathBuf, IntByReference pcchBuf);

    /**
     * The MsiGetProductCode function returns the product code of an application by using the component code of an
     * installed or advertised component of the application. During initialization, an application must determine under
     * which product code it has been installed or advertised.
     *
     * @param szComponent
     *   This parameter specifies the component code of a component that has been installed by the application. This
     *   will be typically the component code of the component containing the executable file of the application.
     *
     * @param lpProductBuf
     *   Pointer to a buffer that receives the product code. This buffer must be 39 characters long. The first 38
     *   characters are for the GUID, and the last character is for the terminating null character.
     *
     * @return
     * ERROR_BAD_CONFIGURATION - The configuration data is corrupt.
     * ERROR_INSTALL_FAILURE - The product code could not be determined.
     * ERROR_INVALID_PARAMETER - An invalid parameter was passed to the function.
     * ERROR_SUCCESS - The function completed successfully.
     * ERROR_UNKNOWN_COMPONENT - The specified component is unknown.
     */
    int MsiGetProductCode(String szComponent, char[] lpProductBuf);

    /**
     * The MsiEnumComponents function enumerates the installed components for all products. This function retrieves one
     * component code each time it is called.
     *
     * @param iComponentIndex
     *   Specifies the index of the component to retrieve. This parameter should be zero for the first call to the
     *   MsiEnumComponents function and then incremented for subsequent calls. Because components are not ordered, any
     *   new component has an arbitrary index. This means that the function can return components in any order.
     *
     * @param lpComponentBuf
     *   Pointer to a buffer that receives the component code. This buffer must be 39 characters long. The first 38
     *   characters are for the GUID, and the last character is for the terminating null character.
     *
     * @return
     *  ERROR_BAD_CONFIGURATION - The configuration data is corrupt.
     *  ERROR_INVALID_PARAMETER - An invalid parameter was passed to the function.
     *  ERROR_NO_MORE_ITEMS - There are no components to return.
     *  ERROR_NOT_ENOUGH_MEMORY - The system does not have enough memory to complete the operation. Available with
     *    Windows Server 2003.
     *  ERROR_SUCCESS - A value was enumerated.
     */
    int MsiEnumComponents(WinDef.DWORD iComponentIndex, char[] lpComponentBuf);
}
