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

    int GetFileVersionInfoSize(String lptstrFilename, IntByReference lpdwHandle);

    boolean GetFileVersionInfo(String lptstrFilename, int dwHandle, int dwLen, Pointer lpData);

    boolean VerQueryValue(Pointer pBlock, String lpSubBlock, PointerByReference lplpBuffer, IntByReference puLen);
}
