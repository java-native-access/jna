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

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;

public class Kernel32DiskManagementFunctionsTest extends AbstractWin32TestSupport {
    public Kernel32DiskManagementFunctionsTest() {
        super();
    }

    @Test
    public void testGetDiskFreeSpaceEx() {
        List<String> driveList = Kernel32Util.getLogicalDriveStrings();
        for (int index = (-1); index < driveList.size(); index++) {
            String driveName = (index < 0) ? null : driveList.get(index);
            if (driveName != null) {
                // according to the documentation must end with a backslash
                if (driveName.charAt(driveName.length() - 1) != File.separatorChar) {
                    driveName += File.separator;
                }

                int driveType = Kernel32.INSTANCE.GetDriveType(driveName);
                /*
                 * Don't try DVD or network drives since they may yield errors
                 * for the test - e.g., DEVICE_NOT_READY
                 */
                if (driveType != WinNT.DRIVE_FIXED) {
                    continue;
                }
            }

            testGetDiskFreeSpaceEx(driveName);
        }
    }

    private void testGetDiskFreeSpaceEx(String lpDirectoryName) {
        LARGE_INTEGER.ByReference lpFreeBytesAvailable = new LARGE_INTEGER.ByReference();
        LARGE_INTEGER.ByReference lpTotalNumberOfBytes = new LARGE_INTEGER.ByReference();
        LARGE_INTEGER.ByReference lpTotalNumberOfFreeBytes = new LARGE_INTEGER.ByReference();
        assertCallSucceeded("GetDiskFreeSpaceEx(" + lpDirectoryName + ")",
                Kernel32.INSTANCE.GetDiskFreeSpaceEx(lpDirectoryName,
                        lpFreeBytesAvailable, lpTotalNumberOfBytes, lpTotalNumberOfFreeBytes));

//        System.out.append(getCurrentTestName()).append('[').append(lpDirectoryName).println(']');
//        System.out.append('\t').append("FreeBytesAvailable: ").println(lpFreeBytesAvailable);
//        System.out.append('\t').append("TotalNumberOfBytes: ").println(lpTotalNumberOfBytes);
//        System.out.append('\t').append("TotalNumberOfFreeBytes: ").println(lpTotalNumberOfFreeBytes);

        assertTrue("No free size for " + lpDirectoryName, LARGE_INTEGER.compare(lpTotalNumberOfFreeBytes, 0L) > 0);
        assertTrue("Free size (" + lpTotalNumberOfFreeBytes + ")"
                 + " not below total size (" + lpTotalNumberOfBytes + ")"
                 + " for " + lpDirectoryName,
                 LARGE_INTEGER.compare(lpTotalNumberOfFreeBytes, lpTotalNumberOfBytes) < 0);
    }

    @Test
    public void testGetDiskFreeSpace() {
        List<String> driveList = Kernel32Util.getLogicalDriveStrings();
        for (int index = (-1); index < driveList.size(); index++) {
            String driveName = (index < 0) ? null : driveList.get(index);
            if (driveName != null) {
                // according to the documentation must end with a backslash
                if (driveName.charAt(driveName.length() - 1) != File.separatorChar) {
                    driveName += File.separator;
                }

                int driveType = Kernel32.INSTANCE.GetDriveType(driveName);
                /*
                 * Don't try DVD or network drives since they may yield errors
                 * for the test - e.g., DEVICE_NOT_READY
                 */
                if (driveType != WinNT.DRIVE_FIXED) {
                    continue;
                }
            }

            testGetDiskFreeSpace(driveName);
        }
    }

    private void testGetDiskFreeSpace(String lpRootPathName) {
        DWORDByReference lpSectorsPerCluster = new DWORDByReference();
        DWORDByReference lpBytesPerSector = new DWORDByReference();
        DWORDByReference lpNumberOfFreeClusters = new DWORDByReference();
        DWORDByReference lpTotalNumberOfClusters = new DWORDByReference();
        assertCallSucceeded("GetDiskFreeSpace(" + lpRootPathName + ")",
                Kernel32.INSTANCE.GetDiskFreeSpace(lpRootPathName,
                        lpSectorsPerCluster, lpBytesPerSector, lpNumberOfFreeClusters, lpTotalNumberOfClusters));

//        System.out.append(getCurrentTestName()).append('[').append(lpRootPathName).println(']');
//        System.out.append('\t').append("SectorsPerCluster: ").println(lpSectorsPerCluster.getValue());
//        System.out.append('\t').append("BytesPerSector: ").println(lpBytesPerSector.getValue());
//        System.out.append('\t').append("NumberOfFreeClusters: ").println(lpNumberOfFreeClusters.getValue());
//        System.out.append('\t').append("TotalNumberOfClusters: ").println(lpTotalNumberOfClusters.getValue());

        DWORD freeSize = lpNumberOfFreeClusters.getValue();
        assertTrue("No free clusters for " + lpRootPathName, freeSize.longValue() > 0L);

        DWORD totalSize = lpTotalNumberOfClusters.getValue();
        assertTrue("Free clusters (" + freeSize + ") not below total (" + totalSize + ") for " + lpRootPathName, freeSize.longValue() < totalSize.longValue());
    }
}
