/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import static com.sun.jna.platform.win32.WinNT.DACL_SECURITY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.GROUP_SECURITY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.OWNER_SECURITY_INFORMATION;

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
            assertFalse("Failed to create file handle: " + filePath, WinBase.INVALID_HANDLE_VALUE.equals(hFile));

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
            if (hFile != WinBase.INVALID_HANDLE_VALUE)
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
