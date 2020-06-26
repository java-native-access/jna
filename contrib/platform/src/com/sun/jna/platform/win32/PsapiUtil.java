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

import java.util.Arrays;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.IntByReference;

/**
 * Psapi utility API.
 *
 * @author Torbj&ouml;rn Svensson, azoff[at]svenskalinuxforeninen.se
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
            size = size + 1024;
            lpidProcess = new int[size];
            if (!Psapi.INSTANCE.EnumProcesses(lpidProcess, size * DWORD.SIZE, lpcbNeeded)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        } while (size == lpcbNeeded.getValue() / DWORD.SIZE);

        return Arrays.copyOf(lpidProcess, lpcbNeeded.getValue() / DWORD.SIZE);
    }
}
