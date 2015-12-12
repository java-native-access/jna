/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Reads Windows Version info from files (the version details you can see by
 * right-clicking and choosing properties)
 * 
 * @author mlfreeman[at]gmail.com
 */
public class VersionUtil {

    /**
     * Gets the file's version number info
     * 
     * @param filePath
     *            The path to the file
     * @return The VS_FIXEDFILEINFO structure read from the file.<br>
     *         Use the getFileVersionMajor(), getFileVersionMinor(),
     *         getFileVersionRevision(), and getFileVersionBuild()
     * @throws UnsupportedOperationException
     *             if VerQueryValue fails to get version info from the file.
     */
    public static VS_FIXEDFILEINFO getFileVersionInfo(String filePath) {
        IntByReference dwDummy = new IntByReference();

        int versionLength = Version.INSTANCE.GetFileVersionInfoSize(filePath, dwDummy);

        // Reading version info failed.
        // throw a Win32Exception with GetLastError()
        if (versionLength == 0) {
            throw new Win32Exception(Native.getLastError());
        }

        // buffer to hold version info
        Pointer lpData = new Memory(versionLength);

        // pointer to pointer to location in aforementioned buffer
        PointerByReference lplpBuffer = new PointerByReference();

        if (!Version.INSTANCE.GetFileVersionInfo(filePath, 0, versionLength, lpData)) {
            throw new Win32Exception(Native.getLastError());
        }

        // here to make VerQueryValue happy.
        IntByReference puLen = new IntByReference();

        // this does not set GetLastError, so no need to throw a Win32Exception
        if (!Version.INSTANCE.VerQueryValue(lpData, "\\", lplpBuffer, puLen)) {
            throw new UnsupportedOperationException("Unable to extract version info from the file: \"" + filePath + "\"");
        }

        VS_FIXEDFILEINFO fileInfo = new VS_FIXEDFILEINFO(lplpBuffer.getValue());
        fileInfo.read();
        return fileInfo;
    }

}
