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
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Provides access to the w32 version library.
 */
public interface Version extends StdCallLibrary {

    Version INSTANCE = (Version)
        Native.loadLibrary("version", Version.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Determines whether the operating system can retrieve version information for a specified file. If version
     * information is available, GetFileVersionInfoSize returns the size, in bytes, of that information.
     *
     * @param lptstrFilename
     *   The name of the file of interest. The function uses the search sequence specified by the LoadLibrary function.
     *
     * @param lpdwHandle
     *   A pointer to a variable that the function sets to zero.
     *
     * @return
     *   If the function succeeds, the return value is the size, in bytes, of the file's version information.
     *
     *   If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    int GetFileVersionInfoSize(String lptstrFilename, IntByReference lpdwHandle);

    /**
     * Retrieves version information for the specified file.
     *
     * @param lptstrFilename
     *   The name of the file. If a full path is not specified, the function uses the search sequence specified by the
     *   LoadLibrary function.
     *
     * @param dwHandle
     *   This parameter is ignored.
     *
     * @param dwLen
     *   The size, in bytes, of the buffer pointed to by the lpData parameter.
     *
     *   Call the GetFileVersionInfoSize function first to determine the size, in bytes, of a file's version
     *   information. The dwLen member should be equal to or greater than that value.
     *
     *   If the buffer pointed to by lpData is not large enough, the function truncates the file's version information
     *   to the size of the buffer.
     *
     * @param lpData
     *   Pointer to a buffer that receives the file-version information.
     *
     *   You can use this value in a subsequent call to the VerQueryValue function to retrieve data from the buffer.
     *
     * @return
     *   If the function succeeds, the return value is nonzero.
     *
     *   If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean GetFileVersionInfo(String lptstrFilename, int dwHandle, int dwLen, Pointer lpData);

    /**
     * Retrieves specified version information from the specified version-information resource. To retrieve the
     * appropriate resource, before you call VerQueryValue, you must first call the GetFileVersionInfoSize function, and
     * then the GetFileVersionInfo function.
     *
     * @param pBlock
     *   The version-information resource returned by the GetFileVersionInfo function.
     *
     * @param lpSubBlock
     *   The version-information value to be retrieved.
     *
     * @param lplpBuffer
     *   When this method returns, contains the address of a pointer to the requested version information in the buffer
     *   pointed to by pBlock. The memory pointed to by lplpBuffer is freed when the associated pBlock memory is freed.
     *
     * @param puLen
     *   When this method returns, contains a pointer to the size of the requested data pointed to by lplpBuffer: for
     *   version information values, the length in characters of the string stored at lplpBuffer; for translation array
     *   values, the size in bytes of the array stored at lplpBuffer; and for root block, the size in bytes of the
     *   structure.
     *
     * @return
     *   If the specified version-information structure exists, and version information is available, the return value
     *   is nonzero. If the address of the length buffer is zero, no value is available for the specified
     *   version-information name.
     *
     *   If the specified name does not exist or the specified resource is not valid, the return value is zero.
     */
    boolean VerQueryValue(Pointer pBlock, String lpSubBlock, PointerByReference lplpBuffer, IntByReference puLen);
}
