/* Copyright (c) 2015 Adam Marcionek, All Rights Reserved
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

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.LMShare.SHARE_INFO_2;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.Winnetwk.ConnectFlag;
import com.sun.jna.platform.win32.Winnetwk.NETRESOURCE;
import com.sun.jna.platform.win32.Winnetwk.REMOTE_NAME_INFO;
import com.sun.jna.platform.win32.Winnetwk.RESOURCESCOPE;
import com.sun.jna.platform.win32.Winnetwk.RESOURCETYPE;
import com.sun.jna.platform.win32.Winnetwk.RESOURCEUSAGE;
import com.sun.jna.platform.win32.Winnetwk.UNIVERSAL_NAME_INFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32StringUtil;

import junit.framework.TestCase;

/**
 * @author amarcionek[at]gmail[dot]com
 */
public class MprTest extends TestCase {

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(MprTest.class);
    }

    public void testCreateLocalShare() throws Exception {
        File fileShareFolder = createTempFolder();
        String share = createLocalShare(fileShareFolder);
        Netapi32.INSTANCE.NetShareDel(null, share, 0);
    }

    public void testWNetUseConnection() throws Exception {
        // First create a share on the local machine
        File fileShareFolder = createTempFolder();
        String share = createLocalShare(fileShareFolder);

        NETRESOURCE resource = new NETRESOURCE();

        resource.dwDisplayType = 0;
        resource.dwScope = 0;
        resource.dwType = RESOURCETYPE.RESOURCETYPE_DISK;
        resource.lpRemoteName = "\\\\" + getLocalComputerName() + "\\" + share;

        try {
            // Cancel any existing connections of the same name
            Mpr.INSTANCE.WNetCancelConnection2(resource.lpRemoteName, 0, true);
            // Establish a new one
            Memory lpAccessName;
            if(DEFAULT_OPTIONS==UNICODE_OPTIONS) {
                lpAccessName = new Memory((WinDef.MAX_PATH + 1) * Native.WCHAR_SIZE);
            } else {
                lpAccessName = new Memory(WinDef.MAX_PATH + 1);
            }
            IntByReference lpAccessNameSize = new IntByReference(WinDef.MAX_PATH);
            assertEquals(WinError.ERROR_SUCCESS, Mpr.INSTANCE.WNetUseConnection(null, resource, null, null, 0, lpAccessName, lpAccessNameSize, null));
            String accessName;
            if(DEFAULT_OPTIONS==UNICODE_OPTIONS) {
                accessName = lpAccessName.getWideString(0);
            } else {
                accessName = lpAccessName.getString(0);
            }
            // System.out.println("Size: " + lpAccessNameSize.getValue());
            // System.out.println("lpAccessName: " + accessName);
            assertNotNull(accessName);
            assertFalse(accessName.isEmpty());
        } finally {
            // Clean up resources
            Mpr.INSTANCE.WNetCancelConnection2(resource.lpRemoteName, 0, true);
            Netapi32.INSTANCE.NetShareDel(null, share, 0);
            fileShareFolder.delete();
        }
    }

    public void testWNetAddConnection3() throws Exception {
        // First create a share on the local machine
        File fileShareFolder = createTempFolder();
        String share = createLocalShare(fileShareFolder);

        NETRESOURCE resource = new NETRESOURCE();

        resource.dwDisplayType = 0;
        resource.dwScope = 0;
        resource.dwType = RESOURCETYPE.RESOURCETYPE_DISK;
        resource.lpRemoteName = "\\\\" + getLocalComputerName() + "\\" + share;

        try {
            // Cancel any existing connections of the same name
            Mpr.INSTANCE.WNetCancelConnection2(resource.lpRemoteName, 0, true);
            // Establish a new one
            assertEquals(WinError.ERROR_SUCCESS, Mpr.INSTANCE.WNetAddConnection3(null, resource, null, null, 0));
        } finally {
            // Clean up resources
            Mpr.INSTANCE.WNetCancelConnection2(resource.lpRemoteName, 0, true);
            Netapi32.INSTANCE.NetShareDel(null, share, 0);
            fileShareFolder.delete();
        }
    }

    public void testWNetOpenCloseConnection() throws Exception {
        HANDLEByReference lphEnum = new HANDLEByReference();
        assertEquals(WinError.ERROR_SUCCESS, Mpr.INSTANCE.WNetOpenEnum(RESOURCESCOPE.RESOURCE_CONNECTED, RESOURCETYPE.RESOURCETYPE_DISK,
                RESOURCEUSAGE.RESOURCEUSAGE_ALL, null, lphEnum));
        assertEquals(WinError.ERROR_SUCCESS, Mpr.INSTANCE.WNetCloseEnum(lphEnum.getValue()));
    }

    public void testWNetEnumConnection() throws Exception {
        int bufferSize = 16 * 1024; // MSDN recommends this as a reasonable size
        HANDLEByReference lphEnum = new HANDLEByReference();

        // Create a local share and connect to it. This ensures the enum will
        // find at least one entry.
        File fileShareFolder = createTempFolder();
        String share = createLocalShare(fileShareFolder);
        // Connect to local share
        connectToLocalShare(share, null);

        try {

            // Open an enumeration
            assertEquals(WinError.ERROR_SUCCESS, Mpr.INSTANCE.WNetOpenEnum(RESOURCESCOPE.RESOURCE_CONNECTED, RESOURCETYPE.RESOURCETYPE_DISK,
                    RESOURCEUSAGE.RESOURCEUSAGE_ALL, null, lphEnum));

            int winError = WinError.ERROR_SUCCESS;

            while (true) {

                Memory memory = new Memory(bufferSize);

                IntByReference lpBufferSize = new IntByReference(bufferSize);
                IntByReference lpcCount = new IntByReference(1);

                // Get next value
                winError = Mpr.INSTANCE.WNetEnumResource(lphEnum.getValue(), lpcCount, memory, lpBufferSize);

                // Reached end of enumeration
                if (winError == WinError.ERROR_NO_MORE_ITEMS)
                    break;

                // Unlikely, but means our buffer size isn't large enough.
                if (winError == WinError.ERROR_MORE_DATA) {
                    bufferSize = bufferSize * 2;
                    continue;
                }

                // If we get here, it means it has to be a success or our
                // programming logic was wrong.
                assertEquals(winError, WinError.ERROR_SUCCESS);

                // Asked for one, should only get one.
                assertEquals(1, lpcCount.getValue());

                // Create a NETRESOURCE based on the memory
                NETRESOURCE resource = new NETRESOURCE(memory);

                // Assert things we know for sure.
                assertNotNull(resource.lpRemoteName);
            }

            // Expect ERROR_NO_MORE_ITEMS here.
            assertEquals(winError, WinError.ERROR_NO_MORE_ITEMS);
        } finally {
            // Clean up resources
            Mpr.INSTANCE.WNetCloseEnum(lphEnum.getValue());
            disconnectFromLocalShare("\\\\" + getLocalComputerName() + "\\" + share);
            deleteLocalShare(share);
            fileShareFolder.delete();
        }
    }

    public void testWNetGetUniversalName() throws Exception {
        int bufferSize = 1024; // MSDN recommends this as a reasonable size
        Memory memory = new Memory(bufferSize);
        IntByReference lpBufferSize = new IntByReference(bufferSize);
        File file = null;
        String share = null;
        String driveLetter = new String("x:");
        File fileShareFolder = createTempFolder();

        try {
            // Create a local share and connect to it.
            share = createLocalShare(fileShareFolder);
            // Connect to share using a drive letter.
            connectToLocalShare(share, driveLetter);

            // Create a path on local device redirected to the share.
            String filePath = new String(driveLetter + "\\testfile.txt");
            file = new File(filePath);
            file.createNewFile();

            // Test WNetGetUniversalName using UNIVERSAL_NAME_INFO_LEVEL
            assertEquals(WinError.ERROR_SUCCESS,
                    Mpr.INSTANCE.WNetGetUniversalName(filePath, Winnetwk.UNIVERSAL_NAME_INFO_LEVEL, memory, lpBufferSize));

            UNIVERSAL_NAME_INFO uinfo = new UNIVERSAL_NAME_INFO(memory);
            assertNotNull(uinfo.lpUniversalName);

            // Test WNetGetUniversalName using REMOTE_NAME_INFO_LEVEL
            assertEquals(WinError.ERROR_SUCCESS,
                    Mpr.INSTANCE.WNetGetUniversalName(filePath, Winnetwk.REMOTE_NAME_INFO_LEVEL, memory, lpBufferSize));

            REMOTE_NAME_INFO rinfo = new REMOTE_NAME_INFO(memory);
            assertNotNull(rinfo.lpUniversalName);
            assertNotNull(rinfo.lpConnectionName);
            assertNotNull(rinfo.lpRemainingPath);
        } finally {
            // Clean up resources
            if (file != null)
                file.delete();
            if (share != null) {
                disconnectFromLocalShare(driveLetter);
                deleteLocalShare(share);
                fileShareFolder.delete();
            }
        }
    }

    private static File createTempFolder() throws Exception {
        String folderPath = System.getProperty("java.io.tmpdir") + File.separatorChar + System.nanoTime();
        File file = new File(folderPath);
        file.mkdir();
        return file;
    }

    /**
     * Get local NETBIOS machine name
     *
     * @return String with machine name
     * @throws Exception
     */
    private String getLocalComputerName() throws Exception {
        IntByReference lpnSize = new IntByReference(0);
        // Get size of char array
        Kernel32.INSTANCE.GetComputerName(null, lpnSize);
        assertEquals(WinError.ERROR_BUFFER_OVERFLOW, Kernel32.INSTANCE.GetLastError());
        // Allocate character array
        char buffer[] = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
        lpnSize.setValue(buffer.length);
        assertTrue(Kernel32.INSTANCE.GetComputerName(buffer, lpnSize));
        // Return string with computer name
        return W32StringUtil.toString(buffer);
    }

    /**
     * Create a share on the local machine. Uses a temporary directory and
     * shares it out with ACCESS_ALL
     *
     * @param shareFolder
     *            the full path local folder to share
     * @return String with the share name, essentially the top level folder
     *         name.
     * @throws Exception
     *             the exception
     */
    private String createLocalShare(File shareFolder) throws Exception {

        SHARE_INFO_2 shi = new SHARE_INFO_2();
        shi.shi2_netname = shareFolder.getName();
        shi.shi2_type = LMShare.STYPE_DISKTREE;
        shi.shi2_remark = "";
        shi.shi2_permissions = LMAccess.ACCESS_ALL;
        shi.shi2_max_uses = -1;
        shi.shi2_current_uses = 0;
        shi.shi2_path = shareFolder.getAbsolutePath();
        shi.shi2_passwd = "";

        // Write from struct to native memory.
        shi.write();

        IntByReference parm_err = new IntByReference(0);
        int errorCode = Netapi32.INSTANCE.NetShareAdd(null, // Use local computer
                2, shi.getPointer(), parm_err);
        assertEquals(
                String.format("Failed to create share - errorCode: %d (Param: %d)", errorCode, parm_err.getValue()),
                LMErr.NERR_Success, errorCode);

        return shareFolder.getName();
    }

    /**
     * Delete a local share
     *
     * @param share
     */
    private void deleteLocalShare(String share) {
        Netapi32.INSTANCE.NetShareDel(null, share, 0);
    }

    /**
     * Connect to a local share on the local machine. Assumes the share is
     * already present
     *
     * @param share
     *            name of share on local computer.
     * @param lpLocalName
     *            name of local device to redirect to. E.g. F:. If null, makes a
     *            connection without redirecting.
     * @throws Exception
     *             the exception
     */
    private void connectToLocalShare(String share, String lpLocalName) throws Exception {
        NETRESOURCE resource = new NETRESOURCE();

        resource.dwDisplayType = 0;
        resource.dwScope = 0;
        resource.dwType = RESOURCETYPE.RESOURCETYPE_DISK;
        resource.lpLocalName = lpLocalName;
        resource.lpRemoteName = "\\\\" + getLocalComputerName() + "\\" + share;

        // Establish connection
        assertEquals(WinError.ERROR_SUCCESS, Mpr.INSTANCE.WNetAddConnection3(null, resource, null, null, 0));
    }

    /**
     * Disconnect from a share.
     *
     * @param lpName
     *            [in] Pointer to a constant null-terminated string that
     *            specifies the name of either the redirected local device or
     *            the remote network resource to disconnect from. If this
     *            parameter specifies a redirected local device, the function
     *            cancels only the specified device redirection. If the
     *            parameter specifies a remote network resource, all connections
     *            without devices are canceled.
     */
    private void disconnectFromLocalShare(String lpName) {
        // Remove connection
        Mpr.INSTANCE.WNetCancelConnection2(lpName, ConnectFlag.CONNECT_UPDATE_PROFILE, true);
    }
}
