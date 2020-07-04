/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32StringUtil;

public class Kernel32VolumeManagementFunctionsTest extends AbstractWin32TestSupport {
    public Kernel32VolumeManagementFunctionsTest() {
        super();
    }

    @Test
    public void testQueryDosDevice() {
        Collection<String> logicalDrives = Kernel32Util.getLogicalDriveStrings();
        for (String lpszDeviceName : logicalDrives) {
            // the documentation states that the device name cannot have a trailing backslash
            if (lpszDeviceName.charAt(lpszDeviceName.length() - 1) == File.separatorChar) {
                lpszDeviceName = lpszDeviceName.substring(0, lpszDeviceName.length() - 1);
            }

            Collection<String> devices = Kernel32Util.queryDosDevice(lpszDeviceName, WinBase.MAX_PATH);
            assertTrue("No devices for " + lpszDeviceName, devices.size() > 0);
            for (String name : devices) {
                assertTrue("Empty device name for " + lpszDeviceName, name.length() > 0);
//                System.out.append(getCurrentTestName()).append('[').append(lpszDeviceName).append(']').append(" - ").println(name);
            }
        }
    }

    @Test
    public void testGetVolumePathName() {
        char[] lpszVolumePathName = new char[WinDef.MAX_PATH + 1];
        for (String propName : new String[] { "java.home", "java.io.tmpdir", "user.dir", "user.home" }) {
            String lpszFileName = System.getProperty(propName);
            assertCallSucceeded("GetVolumePathName(" + lpszFileName + ")",
                    Kernel32.INSTANCE.GetVolumePathName(lpszFileName, lpszVolumePathName, lpszVolumePathName.length));
            String path = Native.toString(lpszVolumePathName);
//            System.out.append(getCurrentTestName()).append('[').append(lpszFileName).append(']').append(" - ").println(path);
            assertTrue("No volume path for " + lpszFileName, path.length() > 0);
        }
    }

    @Test
    public void testGetVolumeNameForVolumeMountPoint() {
        Collection<String> logicalDrives = Kernel32Util.getLogicalDriveStrings();
        char[] lpVolumeNameBuffer = new char[WinDef.MAX_PATH + 1];
        for (String lpszVolumeMountPoint : logicalDrives) {
            // according to documentation path MUST end in backslash
            if (lpszVolumeMountPoint.charAt(lpszVolumeMountPoint.length() - 1) != File.separatorChar) {
                lpszVolumeMountPoint += File.separator;
            }

            int driveType = Kernel32.INSTANCE.GetDriveType(lpszVolumeMountPoint);
            // network mapped drives fail GetVolumeNameForVolumeMountPoint call
            if (driveType != WinBase.DRIVE_FIXED) {
//                System.out.append('\t').append('[').append(lpszVolumeMountPoint).append(']').println(" - skipped: non-fixed drive");
                continue;
            }

            if (Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint(lpszVolumeMountPoint, lpVolumeNameBuffer, lpVolumeNameBuffer.length)) {
                String volumeGUID = Native.toString(lpVolumeNameBuffer);
//                System.out.append(getCurrentTestName()).append('[').append(lpszVolumeMountPoint).append(']').append(" - ").println(volumeGUID);
                assertTrue("Empty GUID for " + lpszVolumeMountPoint, volumeGUID.length() > 0);
            } else {
                int hr = Kernel32.INSTANCE.GetLastError();
                if ((hr == WinError.ERROR_ACCESS_DENIED)    // e.g., hidden volumes
                    || (hr == WinError.ERROR_NOT_READY)) {     // e.g., DVD drive
//                    System.out.append('\t').append('[').append(lpszVolumeMountPoint).append(']').append(" - skipped: reason=").println(hr);
                    continue;
                }

                fail("Cannot (error=" + hr + ") get volume information mount point " + lpszVolumeMountPoint);
            }
        }
    }

    @Test
    public void testGetVolumeInformation() {
        List<String> logicalDrives = Kernel32Util.getLogicalDriveStrings();
        char[] lpVolumeNameBuffer = new char[WinDef.MAX_PATH + 1];
        char[] lpFileSystemNameBuffer = new char[WinDef.MAX_PATH + 1];
        IntByReference lpVolumeSerialNumber = new IntByReference();
        IntByReference lpMaximumComponentLength = new IntByReference();
        IntByReference lpFileSystemFlags = new IntByReference();

        for (int index=(-1); index < logicalDrives.size(); index++) {
            String lpRootPathName = (index < 0) ? null /* curdir */ : logicalDrives.get(index);
            // according to documentation path MUST end in backslash
            if ((lpRootPathName != null) && (lpRootPathName.charAt(lpRootPathName.length() - 1) != File.separatorChar)) {
                lpRootPathName += File.separator;
            }

            if (!Kernel32.INSTANCE.GetVolumeInformation(lpRootPathName,
                        lpVolumeNameBuffer, lpVolumeNameBuffer.length,
                        lpVolumeSerialNumber, lpMaximumComponentLength, lpFileSystemFlags,
                        lpFileSystemNameBuffer, lpFileSystemNameBuffer.length)) {
                int hr = Kernel32.INSTANCE.GetLastError();
                if ((hr == WinError.ERROR_ACCESS_DENIED)    // e.g., network or hidden volumes
                    || (hr == WinError.ERROR_NOT_READY)) {     // e.g., DVD drive
//                    System.out.append('\t').append('[').append(lpRootPathName).append(']').append(" - skipped: reason=").println(hr);
                    continue;
                }

                fail("Cannot (error=" + hr + ") get volume information for " + lpRootPathName);
            }

//            System.out.append(getCurrentTestName()).append('[').append(lpRootPathName).println(']');
//            System.out.append('\t').append("Volume name: ").println(Native.toString(lpVolumeNameBuffer));
//            System.out.append('\t').append("File system name: ").println(Native.toString(lpFileSystemNameBuffer));
//            System.out.append('\t').append("Serial number: ").println(lpVolumeSerialNumber.getValue());
//            System.out.append('\t').append("Max. component: ").println(lpMaximumComponentLength.getValue());
//            System.out.append('\t').append("File system flags: 0x").println(Integer.toHexString(lpFileSystemFlags.getValue()));
        }
    }

    @Test
    public void testEnumVolumes() {
        char[] lpszVolumeName = new char[WinDef.MAX_PATH + 1];
        HANDLE hFindVolume = assertValidHandle("FindFirstVolume", Kernel32.INSTANCE.FindFirstVolume(lpszVolumeName, lpszVolumeName.length));
        try {
            int foundPaths = 0;
            do {
                String volumeGUID = W32StringUtil.toString(lpszVolumeName);
                testEnumVolumeMountMoints(volumeGUID);
                foundPaths += testGetVolumePathNamesForVolumeName(volumeGUID);
            } while(Kernel32.INSTANCE.FindNextVolume(hFindVolume, lpszVolumeName, lpszVolumeName.length));

            assertTrue("No paths were found", foundPaths > 0);

            int hr = Kernel32.INSTANCE.GetLastError();
            assertEquals("Bad volumes enum termination reason", WinError.ERROR_NO_MORE_FILES, hr);
        } finally {
            assertCallSucceeded("FindVolumeClose", Kernel32.INSTANCE.FindVolumeClose(hFindVolume));
        }
    }

    private int testGetVolumePathNamesForVolumeName(String lpszVolumeName) {
        Collection<String> paths = Kernel32Util.getVolumePathNamesForVolumeName(lpszVolumeName);
        for (String p : paths) {
//            System.out.append('\t').append("testGetVolumePathNamesForVolumeName").append('[').append(lpszVolumeName).append(']').append(" - ").println(p);
            assertTrue("Empty path for volume " + lpszVolumeName, p.length() > 0);
        }
        return paths.size();
    }

    private void testEnumVolumeMountMoints(String volumeGUID) {
        char[] lpszVolumeMountPoint = new char[WinDef.MAX_PATH + 1];
        HANDLE hFindVolumeMountPoint = Kernel32.INSTANCE.FindFirstVolumeMountPoint(volumeGUID, lpszVolumeMountPoint, lpszVolumeMountPoint.length);
        if (WinNT.INVALID_HANDLE_VALUE.equals(hFindVolumeMountPoint)) {
            int hr = Kernel32.INSTANCE.GetLastError();
            if ((hr == WinError.ERROR_ACCESS_DENIED)    // e.g., network or hidden volumes
                || (hr == WinError.ERROR_NOT_READY)        // e.g., DVD drive
                || (hr == WinError.ERROR_NO_MORE_FILES)    // No folders found
                || (hr == WinError.ERROR_PATH_NOT_FOUND)) {
//                System.out.append('\t').append('[').append(volumeGUID).append(']').append(" - skipped: reason=").println(hr);
                return;
            }

            fail("Cannot (error=" + hr + ") open mount point search handle for " + volumeGUID);
        }

        try {
            do {
                String name = W32StringUtil.toString(lpszVolumeMountPoint);
                assertTrue("Empty mount point for " + volumeGUID, name.length() > 0);
//                System.out.append('\t').append("testEnumVolumeMountMoints").append('[').append(volumeGUID).append(']').append(" - ").println(name);
            } while(Kernel32.INSTANCE.FindNextVolumeMountPoint(hFindVolumeMountPoint, lpszVolumeMountPoint, lpszVolumeMountPoint.length));

            int hr = Kernel32.INSTANCE.GetLastError();
            assertEquals("Mount points enum termination reason for " + volumeGUID, WinError.ERROR_NO_MORE_FILES, hr);
        } finally {
            assertCallSucceeded("FindVolumeMountPointClose(" + volumeGUID + ")", Kernel32.INSTANCE.FindVolumeMountPointClose(hFindVolumeMountPoint));
        }
    }
}
