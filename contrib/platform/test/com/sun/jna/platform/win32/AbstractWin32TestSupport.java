/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import com.sun.jna.Native;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.sun.jna.platform.AbstractPlatformTestSupport;
import static com.sun.jna.platform.win32.Tlhelp32.TH32CS_SNAPALL;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import java.util.Arrays;
import java.util.Collections;

public abstract class AbstractWin32TestSupport extends AbstractPlatformTestSupport {
    protected AbstractWin32TestSupport() {
        super();
    }

    /**
     * Makes sure that the method names (which represent APIs) do not repeat
     * themselves. This check is in order where APIs are WIN32 API <U>functions</U>
     * since these are C functions - which means no overloading is possible.
     *
     * @param ifc The interface (not checked) class to test
     * @see #detectDuplicateMethods(Class)
     */
    public static final void assertNoDuplicateMethodsNames(Class<?> ifc) {
        Collection<String> dupSet = detectDuplicateMethods(ifc);
        assertTrue("Duplicate names found in " + ifc.getSimpleName() + ": " + dupSet, dupSet.isEmpty());
    }

    /**
     * Checks if there are methods with the same name - regardless of the signature
     *
     * @param ifc The interface (not checked) class to test
     * @return The {@link Set} of duplicate names - empty if no duplicates
     */
    public static final Set<String> detectDuplicateMethods(Class<?> ifc) {
        Method[] methods = ifc.getMethods();
        Set<String> nameSet = new HashSet<String>(methods.length);
        Set<String> dupSet = new HashSet<String>();
        for (Method m : methods) {
            String name = m.getName();
            if (!nameSet.add(name)) {
                dupSet.add(name);
            }
        }

        return dupSet;
    }

    /**
     * Checks if the API call result is {@code true}. If not, then calls
     * {@link Kernel32#GetLastError()} and fails with the error code.
     * <B>Note:</B> if the error code is {@link WinError#ERROR_SUCCESS}
     * then an <I>&quot;unknown reason code&quot;</I> is reported
     * @param message Message to display if call failed
     * @param result The API call result
     */
    public static final void assertCallSucceeded(String message, boolean result) {
        if (result) {
            return;
        }

        int hr = Kernel32.INSTANCE.GetLastError();
        if (hr == WinError.ERROR_SUCCESS) {
            fail(message + " failed with unknown reason code");
        } else {
            fail(message + " failed: hr=" + hr + " - 0x" + Integer.toHexString(hr));
        }
    }

    /**
     * Checks if the status code is ERROR_SUCCESS
     * @param message Message to display if code is an error
     * @param statusCode Status code to check
     * @param showHex If status code is not error success then show it as HEX
     */
    public static final void assertErrorSuccess(String message, int statusCode, boolean showHex) {
        if (showHex) {
            if (statusCode != WinError.ERROR_SUCCESS) {
                fail(message + " - failed - hr=0x" + Integer.toHexString(statusCode));
            }
        } else {
            assertEquals(message, WinError.ERROR_SUCCESS, statusCode);
        }
    }

    /**
     * Makes sure that the handle argument is not {@code null} or {@link WinBase#INVALID_HANDLE_VALUE}.
     * If invalid handle detected, then it invokes {@link Kernel32#GetLastError()}
     * in order to display the error code
     * @param message Message to display if bad handle
     * @param handle The {@link HANDLE} to test
     * @return The same as the input handle if good handle - otherwise does
     * not return and throws an assertion error
     */
    public static final HANDLE assertValidHandle(String message, HANDLE handle) {
        if ((handle == null) || WinBase.INVALID_HANDLE_VALUE.equals(handle)) {
            int hr = Kernel32.INSTANCE.GetLastError();
            if (hr == WinError.ERROR_SUCCESS) {
                fail(message + " failed with unknown reason code");
            } else {
                fail(message + " failed: hr=" + hr + " - 0x" + Integer.toHexString(hr));
            }
        }

        return handle;
    }
    
    public static final LCID systemLCID = Kernel32.INSTANCE.GetSystemDefaultLCID();
    public static final boolean isEnglishLocale = 
            systemLCID.intValue() == 0x409 // en_US
            || systemLCID.intValue() == 0x809 // en_GB
            ;

    public static void killProcessByName(String filename) {
        HANDLE hSnapShot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(TH32CS_SNAPALL, null);
        Tlhelp32.PROCESSENTRY32 process = new Tlhelp32.PROCESSENTRY32();
        boolean hRes = Kernel32.INSTANCE.Process32First(hSnapShot, process);
        while (hRes) {
            String imageName = Native.toString(process.szExeFile);
            if (imageName.equalsIgnoreCase(filename)) {
                HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_TERMINATE, false, process.th32ProcessID.intValue());
                if (hProcess != null) {
                    Kernel32.INSTANCE.TerminateProcess(hProcess, 9);
                    Kernel32.INSTANCE.CloseHandle(hProcess);
                }
            }
            hRes = Kernel32.INSTANCE.Process32Next(hSnapShot, process);
        }
        Kernel32.INSTANCE.CloseHandle(hSnapShot);
    }
}
