/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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
