/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import static com.sun.jna.platform.win32.WinNT.DACL_SECURITY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.GROUP_SECURITY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.OWNER_SECURITY_INFORMATION;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileWriter;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Wdm.KEY_BASIC_INFORMATION;
import com.sun.jna.platform.win32.Wdm.KEY_INFORMATION_CLASS;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.ptr.IntByReference;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NtDllTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NtDllTest.class);
    }

    public void testZwQueryKey() {
        // open a key
        HKEYByReference phKey = new HKEYByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
        // query key info
        IntByReference resultLength = new IntByReference();
        assertEquals(NTStatus.STATUS_BUFFER_TOO_SMALL, NtDll.INSTANCE.ZwQueryKey(
                phKey.getValue(), KEY_INFORMATION_CLASS.KeyBasicInformation,
                null, 0, resultLength));
        assertTrue(resultLength.getValue() > 0);
        KEY_BASIC_INFORMATION keyInformation = new KEY_BASIC_INFORMATION(resultLength.getValue());
        assertEquals(NTStatus.STATUS_SUCCESS, NtDll.INSTANCE.ZwQueryKey(
                phKey.getValue(), Wdm.KEY_INFORMATION_CLASS.KeyBasicInformation,
                keyInformation, resultLength.getValue(), resultLength));
        // show
        // Keys are case insensitive (https://msdn.microsoft.com/de-de/library/windows/desktop/ms724946(v=vs.85).aspx)
        assertEquals("software", keyInformation.getName().toLowerCase());
        // close key
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }

    public void testNtQuerySetSecurityObjectNoSACL() throws Exception {
        int infoType = OWNER_SECURITY_INFORMATION
                       | GROUP_SECURITY_INFORMATION
                       | DACL_SECURITY_INFORMATION;

        // create a temp file
        File file = createTempFile();
        String filePath = file.getAbsolutePath();
        HANDLE hFile = WinBase.INVALID_HANDLE_VALUE;

        try {
            hFile = Kernel32.INSTANCE.CreateFile(
                        filePath,
                        WinNT.GENERIC_WRITE | WinNT.WRITE_OWNER | WinNT.WRITE_DAC,
                        WinNT.FILE_SHARE_READ,
                        new WinBase.SECURITY_ATTRIBUTES(),
                        WinNT.OPEN_EXISTING,
                        WinNT.FILE_ATTRIBUTE_NORMAL,
                        null);
            assertNotEquals("Failed to create file handle: " + filePath, WinBase.INVALID_HANDLE_VALUE, hFile);

            int Length = 64 * 1024;
            Memory SecurityDescriptor = new Memory(Length);
            IntByReference LengthNeeded = new IntByReference();

            assertEquals("NtQuerySecurityObject(" + filePath + ")", 0,
                    NtDll.INSTANCE.NtQuerySecurityObject(
                            hFile,
                            infoType,
                            SecurityDescriptor,
                            Length,
                            LengthNeeded));
            assertTrue(LengthNeeded.getValue() > 0);
            assertTrue(LengthNeeded.getValue() < 64 * 1024);
            assertEquals("NtSetSecurityObject(" + filePath + ")", 0,
                    NtDll.INSTANCE.NtSetSecurityObject(
                            hFile,
                            infoType,
                            SecurityDescriptor));
        } finally {
            if (!WinBase.INVALID_HANDLE_VALUE.equals(hFile))
                Kernel32.INSTANCE.CloseHandle(hFile);
            file.delete();
        }
    }

    public void testRtlNtStatusToDosError() {
        int status = NTStatus.STATUS_INVALID_OWNER;
        int error = NtDll.INSTANCE.RtlNtStatusToDosError(status);
        assertEquals(W32Errors.ERROR_INVALID_OWNER, error);
    }

    private File createTempFile() throws Exception {
        String filePath = System.getProperty("java.io.tmpdir") + System.nanoTime()
                + ".text";
        File file = new File(filePath);
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        for (int i = 0; i < 1000; i++) {
            fileWriter.write("Sample text " + i + System.getProperty("line.separator"));
        }
        fileWriter.close();
        return file;
    }
}
