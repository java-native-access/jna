/* Copyright (c) 2019 Daniel Widdis, All Rights Reserved
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

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFOEX;

/**
 * The following functions can be used to determine the current operating system
 * version or identify whether it is a Windows or Windows Server release. These
 * functions provide simple tests that use the VerifyVersionInfo function and
 * the recommended greater than or equal to comparisons that are proven as a
 * robust means to determine the operating system version.
 */
public class VersionHelpers {
    /*
     * Code in this class is an attempt to faithfully port the inline macros in
     * the versionhelpers.h header file of the Windows 10 SDK.
     */

    /**
     * This function is useful in confirming a version of Windows Server that
     * doesn't share a version number with a client release. You should only use
     * this function if the other provided version helper functions do not fit
     * your scenario.
     *
     * @param wMajorVersion
     *            The major version to test
     * @param wMinorVersion
     *            The minor version to test
     * @param wServicePackMajor
     *            The service pack to test
     * @return True if the current OS version matches, or is greater than, the
     *         provided version information.
     */
    public static boolean IsWindowsVersionOrGreater(int wMajorVersion, int wMinorVersion, int wServicePackMajor) {
        OSVERSIONINFOEX osvi = new OSVERSIONINFOEX();
        osvi.dwOSVersionInfoSize = new DWORD(osvi.size());
        osvi.dwMajorVersion = new DWORD(wMajorVersion);
        osvi.dwMinorVersion = new DWORD(wMinorVersion);
        osvi.wServicePackMajor = new WORD(wServicePackMajor);

        long dwlConditionMask = 0;
        dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(dwlConditionMask, WinNT.VER_MAJORVERSION,
                (byte) WinNT.VER_GREATER_EQUAL);
        dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(dwlConditionMask, WinNT.VER_MINORVERSION,
                (byte) WinNT.VER_GREATER_EQUAL);
        dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(dwlConditionMask, WinNT.VER_SERVICEPACKMAJOR,
                (byte) WinNT.VER_GREATER_EQUAL);

        return Kernel32.INSTANCE.VerifyVersionInfo(osvi,
                WinNT.VER_MAJORVERSION | WinNT.VER_MINORVERSION | WinNT.VER_SERVICEPACKMAJOR, dwlConditionMask);
    }

    /*
     * The constants Kernel32.WIN32_WINNT_* are 2-byte encodings of windows
     * version numbers, for example Windows XP is version 5.1 and is encoded as
     * 0x0501. To pass to IsWindowsVersionOrGreater, we pass the HIBYTE (e.g.,
     * 0x05) as the first argument and LOBYTE (e.g., 0x01) as the second. To get
     * the high byte of a short, we shift right 8 bits and cast to byte, e.g.,
     * (byte) (word>>>8); to get the low byte wse simply cast to byte.
     */

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows XP version.
     */
    public static boolean IsWindowsXPOrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WINXP >>> 8), (byte) Kernel32.WIN32_WINNT_WINXP,
                0);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows XP with Service Pack 1 (SP1) version.
     */
    public static boolean IsWindowsXPSP1OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WINXP >>> 8), (byte) Kernel32.WIN32_WINNT_WINXP,
                1);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows XP with Service Pack 2 (SP2) version.
     */
    public static boolean IsWindowsXPSP2OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WINXP >>> 8), (byte) Kernel32.WIN32_WINNT_WINXP,
                2);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows XP with Service Pack 3 (SP3) version.
     */
    public static boolean IsWindowsXPSP3OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WINXP >>> 8), (byte) Kernel32.WIN32_WINNT_WINXP,
                3);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows Vista version.
     */
    public static boolean IsWindowsVistaOrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_VISTA >>> 8), (byte) Kernel32.WIN32_WINNT_VISTA,
                0);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows Vista with Service Pack 1 (SP1) version.
     */
    public static boolean IsWindowsVistaSP1OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_VISTA >>> 8), (byte) Kernel32.WIN32_WINNT_VISTA,
                1);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows Vista with Service Pack 2 (SP2) version.
     */
    public static boolean IsWindowsVistaSP2OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_VISTA >>> 8), (byte) Kernel32.WIN32_WINNT_VISTA,
                2);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows 7 version.
     */
    public static boolean IsWindows7OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WIN7 >>> 8), (byte) Kernel32.WIN32_WINNT_WIN7, 0);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows 7 with Service Pack 1 (SP1) version.
     */
    public static boolean IsWindows7SP1OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WIN7 >>> 8), (byte) Kernel32.WIN32_WINNT_WIN7, 1);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows 8 version.
     */
    public static boolean IsWindows8OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WIN8 >>> 8), (byte) Kernel32.WIN32_WINNT_WIN8, 0);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows 8.1 version. For Windows 8.1 and/or Windows 10,
     *         {@link #IsWindows8Point1OrGreater} returns false unless the
     *         application contains a manifest that includes a compatibility
     *         section that contains the GUIDs that designate Windows 8.1 and/or
     *         Windows 10.
     */
    public static boolean IsWindows8Point1OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WINBLUE >>> 8),
                (byte) Kernel32.WIN32_WINNT_WINBLUE, 0);
    }

    /**
     * @return true if the current OS version matches, or is greater than, the
     *         Windows 10 version. For Windows 10,
     *         {@link #IsWindows8Point1OrGreater} returns false unless the
     *         application contains a manifest that includes a compatibility
     *         section that contains the GUID that designates Windows 10.
     */
    public static boolean IsWindows10OrGreater() {
        return IsWindowsVersionOrGreater((byte) (Kernel32.WIN32_WINNT_WIN10 >>> 8), (byte) Kernel32.WIN32_WINNT_WIN10,
                0);
    }

    /**
     * Applications that need to distinguish between server and client versions
     * of Windows should call this function.
     *
     * @return true if the current OS is a Windows Server release.
     */
    public static boolean IsWindowsServer() {
        OSVERSIONINFOEX osvi = new OSVERSIONINFOEX();
        osvi.dwOSVersionInfoSize = new DWORD(osvi.size());
        osvi.wProductType = WinNT.VER_NT_WORKSTATION;

        long dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(0, WinNT.VER_PRODUCT_TYPE,
                (byte) WinNT.VER_EQUAL);

        return !Kernel32.INSTANCE.VerifyVersionInfo(osvi, WinNT.VER_PRODUCT_TYPE, dwlConditionMask);
    }
}

