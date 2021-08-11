/* Copyright (c) 2020 Torbjörn Svensson, All Rights Reserved
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
import java.util.Arrays;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Psapi utility API.
 *
 * @author Torbj&ouml;rn Svensson, azoff[at]svenskalinuxforeningen.se
 */
public abstract class PsapiUtil {

    /**
     * Retrieves the process identifier for each process object in the system.
     *
     * @return Array of pids
     */
    public static int[] enumProcesses() {
        int size = 0;
        int[] lpidProcess = null;
        IntByReference lpcbNeeded = new IntByReference();
        do {
            size += 1024;
            lpidProcess = new int[size];
            if (!Psapi.INSTANCE.EnumProcesses(lpidProcess, size * DWORD.SIZE, lpcbNeeded)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        } while (size == lpcbNeeded.getValue() / DWORD.SIZE);

        return Arrays.copyOf(lpidProcess, lpcbNeeded.getValue() / DWORD.SIZE);
    }

    /**
     * Retrieves the name of the executable file for the specified process.
     *
     * @param hProcess
     *            A handle to the process. The handle must have the
     *            PROCESS_QUERY_INFORMATION or PROCESS_QUERY_LIMITED_INFORMATION
     *            access right. For more information, see Process Security and
     *            Access Rights. <br>
     *            Windows Server 2003 and Windows XP: The handle must have the
     *            PROCESS_QUERY_INFORMATION access right.
     * @return ame of the executable file for the specified process.
     * @throws Win32Exception in case an error occurs
     * @see <a href="http://msdn.microsoft.com/en-us/library/ms683217(VS.85).aspx">MSDN</a>
     */
    public static String GetProcessImageFileName(HANDLE hProcess) {
        int size = 2048;
        while (true) {
            final char[] filePath = new char[size];
            int length = Psapi.INSTANCE.GetProcessImageFileName(hProcess,
                filePath, filePath.length);
            if(length == 0) {
                if(Native.getLastError() != WinError.ERROR_INSUFFICIENT_BUFFER) {
                    throw new Win32Exception(Native.getLastError());
                }
                size += 2048;
            } else {
                return Native.toString(filePath);
            }
        }
    }
}
