/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Tlhelp32.MODULEENTRY32W;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 * @author markus[at]headcrashing[dot]eu
 */
public class Kernel32UtilTest extends TestCase {

    public static void main(String[] args) throws Exception {
        System.out.println("Computer name: " + Kernel32Util.getComputerName());
        System.out.println("Temp path: " + Kernel32Util.getTempPath());
        // logical drives
        System.out.println("Logical drives: ");
        Collection<String> logicalDrives = Kernel32Util.getLogicalDriveStrings();
        for(String logicalDrive : logicalDrives) {
            // drive type
            System.out.println(" " + logicalDrive + " ("
                    + Kernel32.INSTANCE.GetDriveType(logicalDrive) + ")");
            // free space
            LARGE_INTEGER.ByReference lpFreeBytesAvailable = new LARGE_INTEGER.ByReference();
            LARGE_INTEGER.ByReference lpTotalNumberOfBytes = new LARGE_INTEGER.ByReference();
            LARGE_INTEGER.ByReference lpTotalNumberOfFreeBytes = new LARGE_INTEGER.ByReference();
            if (Kernel32.INSTANCE.GetDiskFreeSpaceEx(logicalDrive, lpFreeBytesAvailable, lpTotalNumberOfBytes, lpTotalNumberOfFreeBytes)) {
                System.out.println("  Total: " + formatBytes(lpTotalNumberOfBytes.getValue()));
                System.out.println("   Free: " + formatBytes(lpTotalNumberOfFreeBytes.getValue()));
            }
        }

        junit.textui.TestRunner.run(Kernel32UtilTest.class);
    }

    /**
     * Format bytes.
     * @param bytes
     *  Bytes.
     * @return
     *  Rounded string representation of the byte size.
     */
    private static String formatBytes(long bytes) {
        if (bytes == 1) { // bytes
            return String.format("%d byte", bytes);
        } else if (bytes < 1024) { // bytes
            return String.format("%d bytes", bytes);
        } else if (bytes < 1048576 && bytes % 1024 == 0) { // Kb
            return String.format("%.0f KB", (double) bytes / 1024);
        } else if (bytes < 1048576) { // Kb
            return String.format("%.1f KB", (double) bytes / 1024);
        } else if (bytes % 1048576 == 0 && bytes < 1073741824) { // Mb
            return String.format("%.0f MB", (double) bytes / 1048576);
        } else if (bytes < 1073741824) { // Mb
            return String.format("%.1f MB", (double) bytes / 1048576);
        } else if (bytes % 1073741824 == 0 && bytes < 1099511627776L) { // GB
            return String.format("%.0f GB", (double) bytes / 1073741824);
        } else if (bytes < 1099511627776L ) {
            return String.format("%.1f GB", (double) bytes / 1073741824);
        } else if (bytes % 1099511627776L == 0 && bytes < 1125899906842624L) { // TB
            return String.format("%.0f TB", (double) bytes / 1099511627776L);
        } else if (bytes < 1125899906842624L ) {
            return String.format("%.1f TB", (double) bytes / 1099511627776L);
        } else {
            return String.format("%d bytes", bytes);
        }
    }

    public void testFreeLocalMemory() {
        try {
            Pointer ptr = new Pointer(0xFFFFFFFFFFFFFFFFL);
            Kernel32Util.freeLocalMemory(ptr);
            fail("Unexpected success to free bad local memory");
        } catch(Win32Exception e) {
            HRESULT hr = e.getHR();
            int code = W32Errors.HRESULT_CODE(hr.intValue());
            assertEquals("Mismatched failure reason code", WinError.ERROR_INVALID_HANDLE, code);
        }
    }

    public void testFreeGlobalMemory() {
        try {
            Pointer ptr = new Pointer(0xFFFFFFFFFFFFFFFFL);
            Kernel32Util.freeGlobalMemory(ptr);
            fail("Unexpected success to free bad global memory");
        } catch(Win32Exception e) {
            HRESULT hr = e.getHR();
            int code = W32Errors.HRESULT_CODE(hr.intValue());
            assertEquals("Mismatched failure reason code", WinError.ERROR_INVALID_HANDLE, code);
        }
    }

    public void testGetComputerName() {
        assertTrue(Kernel32Util.getComputerName().length() > 0);
    }

    public void testFormatMessageFromLastErrorCode() {
        if (AbstractWin32TestSupport.isEnglishLocale) {
            assertEquals("The remote server has been paused or is in the process of being started.",
                    Kernel32Util.formatMessageFromLastErrorCode(W32Errors.ERROR_SHARING_PAUSED));
        } else {
            System.out.println("testFormatMessageFromLastErrorCode Test can only be run on english locale");
        }
    }

    public void testFormatMessageFromHR() {
        if(AbstractWin32TestSupport.isEnglishLocale) {
            assertEquals("The operation completed successfully.",
                    Kernel32Util.formatMessage(W32Errors.S_OK));
        } else {
            System.out.println("testFormatMessageFromHR Test can only be run on english locale");
        }
    }

    public void testGetTempPath() {
        assertTrue(Kernel32Util.getTempPath().length() > 0);
    }

    public void testGetLogicalDriveStrings() {
        Collection<String> logicalDrives = Kernel32Util.getLogicalDriveStrings();
        assertTrue("No logical drives found", logicalDrives.size() > 0);
        for(String logicalDrive : logicalDrives) {
            assertTrue("Empty logical drive name in list", logicalDrive.length() > 0);
        }
    }

    public void testDeleteFile() throws IOException {
        String filename = Kernel32Util.getTempPath() + "\\FileDoesNotExist.jna";
        File f = new File(filename);
        f.createNewFile();
        Kernel32Util.deleteFile(filename);
    }

    public void testGetFileAttributes() throws IOException {
        String filename = Kernel32Util.getTempPath();
        int fileAttributes = Kernel32Util.getFileAttributes(filename);
        assertEquals(WinNT.FILE_ATTRIBUTE_DIRECTORY, fileAttributes & WinNT.FILE_ATTRIBUTE_DIRECTORY);
        File tempFile = File.createTempFile("jna", "tmp");
        tempFile.deleteOnExit();
        int fileAttributes2 = Kernel32Util.getFileAttributes(tempFile.getAbsolutePath());
        tempFile.delete();
        assertEquals(0, fileAttributes2 & WinNT.FILE_ATTRIBUTE_DIRECTORY);
    }

    public void testGetEnvironmentVariable() {
        assertEquals(null, Kernel32Util.getEnvironmentVariable("jna-getenvironment-test"));
        Kernel32.INSTANCE.SetEnvironmentVariable("jna-getenvironment-test", "42");
        assertEquals("42", Kernel32Util.getEnvironmentVariable("jna-getenvironment-test"));
    }

    public final void testGetPrivateProfileInt() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileInt", "ini");
        tmp.deleteOnExit();
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        writer.println("[Section]");
        writer.println("existingKey = 123");
        writer.close();

        assertEquals(123, Kernel32Util.getPrivateProfileInt("Section", "existingKey", 456, tmp.getCanonicalPath()));
        assertEquals(456, Kernel32Util.getPrivateProfileInt("Section", "missingKey", 456, tmp.getCanonicalPath()));
    }

    public final void testGetPrivateProfileString() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileString", "ini");
        tmp.deleteOnExit();
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        writer.println("[Section]");
        writer.println("existingKey = ABC");
        writer.close();

        assertEquals("ABC", Kernel32Util.getPrivateProfileString("Section", "existingKey", "DEF", tmp.getCanonicalPath()));
        assertEquals("DEF", Kernel32Util.getPrivateProfileString("Section", "missingKey", "DEF", tmp.getCanonicalPath()));
    }

    public final void testWritePrivateProfileString() throws IOException {
        final File tmp = File.createTempFile("testWritePrivateProfileString", "ini");
        tmp.deleteOnExit();
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        writer.println("[Section]");
        writer.println("existingKey = ABC");
        writer.println("removedKey = JKL");
        writer.close();

        Kernel32Util.writePrivateProfileString("Section", "existingKey", "DEF", tmp.getCanonicalPath());
        Kernel32Util.writePrivateProfileString("Section", "addedKey", "GHI", tmp.getCanonicalPath());
        Kernel32Util.writePrivateProfileString("Section", "removedKey", null, tmp.getCanonicalPath());

        final BufferedReader reader = new BufferedReader(new FileReader(tmp));
        assertEquals(reader.readLine(), "[Section]");
        assertTrue(reader.readLine().matches("existingKey\\s*=\\s*DEF"));
        assertTrue(reader.readLine().matches("addedKey\\s*=\\s*GHI"));
        assertEquals(reader.readLine(), null);
        reader.close();
    }

    public final void testGetPrivateProfileSection() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileSection", ".ini");
        tmp.deleteOnExit();

        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        try {
            writer.println("[X]");
            writer.println("A=1");
            writer.println("foo=bar");
        } finally {
            writer.close();
        }

        final String[] lines = Kernel32Util.getPrivateProfileSection("X", tmp.getCanonicalPath());
        assertEquals(lines.length, 2);
        assertEquals(lines[0], "A=1");
        assertEquals(lines[1], "foo=bar");
    }

    public final void testGetPrivateProfileSectionNames() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileSectionNames", "ini");
        tmp.deleteOnExit();

        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        try {
            writer.println("[S1]");
            writer.println("A=1");
            writer.println("B=X");
            writer.println("[S2]");
            writer.println("C=2");
            writer.println("D=Y");
        } finally {
            writer.close();
        }

        String[] sectionNames = Kernel32Util.getPrivateProfileSectionNames(tmp.getCanonicalPath());
        assertEquals(sectionNames.length, 2);
        assertEquals(sectionNames[0], "S1");
        assertEquals(sectionNames[1], "S2");
    }

    public final void testWritePrivateProfileSection() throws IOException {
        final File tmp = File.createTempFile("testWritePrivateProfileSecion", "ini");
        tmp.deleteOnExit();

        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        try {
            writer.println("[S1]");
            writer.println("A=1");
            writer.println("B=X");
            writer.println("[S2]");
            writer.println("C=2");
            writer.println("foo=bar");
        } finally {
            writer.close();
        }

        Kernel32Util.writePrivateProfileSection("S1", new String[] { "A=3", "E=Z" }, tmp.getCanonicalPath());

        final BufferedReader reader = new BufferedReader(new FileReader(tmp));
        try {
            assertEquals(reader.readLine(), "[S1]");
            assertEquals(reader.readLine(), "A=3");
            assertEquals(reader.readLine(), "E=Z");
            assertEquals(reader.readLine(), "[S2]");
            assertEquals(reader.readLine(), "C=2");
            assertEquals(reader.readLine(), "foo=bar");
        } finally {
            reader.close();
        }
    }

    public final void testQueryFullProcessImageName() {
        HANDLE h = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION, false, Kernel32.INSTANCE.GetCurrentProcessId());
        assertNotNull("Failed (" + Kernel32.INSTANCE.GetLastError() + ") to get process handle", h);
        try {
            String name = Kernel32Util.QueryFullProcessImageName(h, 0);
            assertTrue("Failed to query process image name, empty path returned", name.length() > 0);
        } finally {
            Kernel32Util.closeHandle(h);
        }
    }

    public void testGetResource() {
        String winDir = Kernel32Util.getEnvironmentVariable("WINDIR");
        assertNotNull("No WINDIR value returned", winDir);
        assertTrue("Specified WINDIR does not exist: " + winDir, new File(winDir).exists());

        // On Windows 7, "14" is the type assigned to the "My Computer" icon
        // (which is named "ICO_MYCOMPUTER")
        byte[] results = Kernel32Util.getResource(new File(winDir, "explorer.exe").getAbsolutePath(), "14",
                "ICO_MYCOMPUTER");
        assertNotNull("The 'ICO_MYCOMPUTER' resource in explorer.exe should have some content.", results);
        assertTrue("The 'ICO_MYCOMPUTER' resource in explorer.exe should have some content.", results.length > 0);
    }

    public void testGetResourceNames() {
        String winDir = Kernel32Util.getEnvironmentVariable("WINDIR");
        assertNotNull("No WINDIR value returned", winDir);
        assertTrue("Specified WINDIR does not exist: " + winDir, new File(winDir).exists());

        // On Windows 7, "14" is the type assigned to the "My Computer" icon
        // (which is named "ICO_MYCOMPUTER")
        Map<String, List<String>> names = Kernel32Util.getResourceNames(new File(winDir, "explorer.exe").getAbsolutePath());

        assertNotNull("explorer.exe should contain some resources in it.", names);
        assertTrue("explorer.exe should contain some resource types in it.", names.size() > 0);
        assertTrue("explorer.exe should contain a resource of type '14' in it.", names.containsKey("14"));
        assertTrue("resource type 14 should have a name named ICO_MYCOMPUTER associated with it.", names.get("14").contains("ICO_MYCOMPUTER"));
    }

    public void testGetModules() {
        List<MODULEENTRY32W> results = Kernel32Util.getModules(Kernel32.INSTANCE.GetCurrentProcessId());

        // not sure if this will be run against java.exe or javaw.exe but these checks should work with both
        assertNotNull("There should be some modules returned from this helper", results);
        assertTrue("The first module in this process should be java.exe or javaw.exe", results.get(0).szModule().startsWith("java"));

        // since this is supposed to return all the modules in a process, there should be an EXE and at least 1 Windows DLL
        // so assert total count is at least two
        assertTrue("This is supposed to return all the modules in a process, so there should be an EXE and at least 1 Windows API DLL.", results.size() > 2);
    }
}
