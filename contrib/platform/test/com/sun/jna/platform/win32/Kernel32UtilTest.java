/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Tlhelp32.MODULEENTRY32W;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.CACHE_RELATIONSHIP;
import com.sun.jna.platform.win32.WinNT.GROUP_RELATIONSHIP;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinNT.LOGICAL_PROCESSOR_RELATIONSHIP;
import com.sun.jna.platform.win32.WinNT.NUMA_NODE_RELATIONSHIP;
import com.sun.jna.platform.win32.WinNT.PROCESSOR_CACHE_TYPE;
import com.sun.jna.platform.win32.WinNT.PROCESSOR_RELATIONSHIP;
import com.sun.jna.platform.win32.WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION;
import com.sun.jna.platform.win32.WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX;

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

    public void testFormatMessageFromErrorCodeWithNonEnglishLocale() {
        int errorCode = W32Errors.S_OK.intValue();
        String formattedMsgInDefaultLocale = Kernel32Util.formatMessage(errorCode);
        // primary and sub languages id's of the english locale, because it is present on most machines
        String formattedMsgInEnglishLocale = Kernel32Util.formatMessage(errorCode, 9, 1);
        if(AbstractWin32TestSupport.isEnglishLocale) {
            assertEquals(formattedMsgInDefaultLocale, formattedMsgInEnglishLocale);
        } else {
            assertNotSame(formattedMsgInDefaultLocale, formattedMsgInEnglishLocale);
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

        final PrintWriter writer0 = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        try {
            writer0.println("[X]");
        } finally {
            writer0.close();
        }

        final String[] lines0 = Kernel32Util.getPrivateProfileSection("X", tmp.getCanonicalPath());
        assertEquals(lines0.length, 0);

        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp, true)));
        try {
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
        int pid = Kernel32.INSTANCE.GetCurrentProcessId();

        HANDLE h = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION, false, pid);
        assertNotNull("Failed (" + Kernel32.INSTANCE.GetLastError() + ") to get process handle", h);
        try {
            String name = Kernel32Util.QueryFullProcessImageName(h, 0);
            assertNotNull("Failed to query process image name, null path returned", name);
            assertTrue("Failed to query process image name, empty path returned", name.length() > 0);
        } finally {
            Kernel32Util.closeHandle(h);
        }

        String name = Kernel32Util.QueryFullProcessImageName(pid, 0);
        assertNotNull("Failed to query process image name, null path returned", name);
        assertTrue("Failed to query process image name, empty path returned", name.length() > 0);

        try {
            Kernel32Util.QueryFullProcessImageName(0, 0); // the system process
            fail("Should never reach here");
        } catch (Win32Exception expected) {
            assertEquals("Should get Invalid Parameter error", Kernel32.ERROR_INVALID_PARAMETER, expected.getErrorCode());
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

    public void testExpandEnvironmentStrings() {
        Kernel32.INSTANCE.SetEnvironmentVariable("DemoVariable", "DemoValue");
        assertEquals("DemoValue", Kernel32Util.expandEnvironmentStrings("%DemoVariable%"));
    }

    public void testGetLogicalProcessorInformation() {
        SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] procInfo = Kernel32Util
                .getLogicalProcessorInformation();
        assertTrue(procInfo.length > 0);
    }

    public void testGetLogicalProcessorInformationEx() {
        SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] procInfo = Kernel32Util
                .getLogicalProcessorInformationEx(WinNT.LOGICAL_PROCESSOR_RELATIONSHIP.RelationAll);
        List<GROUP_RELATIONSHIP> groups = new ArrayList<GROUP_RELATIONSHIP>();
        List<PROCESSOR_RELATIONSHIP> packages = new ArrayList<PROCESSOR_RELATIONSHIP>();
        List<NUMA_NODE_RELATIONSHIP> numaNodes = new ArrayList<NUMA_NODE_RELATIONSHIP>();
        List<CACHE_RELATIONSHIP> caches = new ArrayList<CACHE_RELATIONSHIP>();
        List<PROCESSOR_RELATIONSHIP> cores = new ArrayList<PROCESSOR_RELATIONSHIP>();

        for (int i = 0; i < procInfo.length; i++) {
            // Build list from relationship
            switch (procInfo[i].relationship) {
                case LOGICAL_PROCESSOR_RELATIONSHIP.RelationGroup:
                    groups.add((GROUP_RELATIONSHIP) procInfo[i]);
                    break;
                case LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorPackage:
                    packages.add((PROCESSOR_RELATIONSHIP) procInfo[i]);
                    break;
                case LOGICAL_PROCESSOR_RELATIONSHIP.RelationNumaNode:
                    numaNodes.add((NUMA_NODE_RELATIONSHIP) procInfo[i]);
                    break;
                case LOGICAL_PROCESSOR_RELATIONSHIP.RelationCache:
                    caches.add((CACHE_RELATIONSHIP) procInfo[i]);
                    break;
                case LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorCore:
                    cores.add((PROCESSOR_RELATIONSHIP) procInfo[i]);
                    break;
                default:
                    throw new IllegalStateException("Unmapped relationship.");
            }
            // Test that native provided size matches JNA structure size
            assertEquals(procInfo[i].size, procInfo[i].size());
        }

        // Test that getting all relations matches the same totals as
        // individuals.
        assertEquals(groups.size(), Kernel32Util
                .getLogicalProcessorInformationEx(WinNT.LOGICAL_PROCESSOR_RELATIONSHIP.RelationGroup).length);
        assertEquals(packages.size(), Kernel32Util.getLogicalProcessorInformationEx(
                WinNT.LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorPackage).length);
        assertEquals(numaNodes.size(), Kernel32Util
                .getLogicalProcessorInformationEx(WinNT.LOGICAL_PROCESSOR_RELATIONSHIP.RelationNumaNode).length);
        assertEquals(caches.size(), Kernel32Util
                .getLogicalProcessorInformationEx(WinNT.LOGICAL_PROCESSOR_RELATIONSHIP.RelationCache).length);
        assertEquals(cores.size(), Kernel32Util
                .getLogicalProcessorInformationEx(WinNT.LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorCore).length);

        // Test GROUP_RELATIONSHIP
        assertEquals(1, groups.size()); // Should only be one group structure
        for (GROUP_RELATIONSHIP group : groups) {
            assertEquals(LOGICAL_PROCESSOR_RELATIONSHIP.RelationGroup, group.relationship);
            assertTrue(group.activeGroupCount <= group.maximumGroupCount);
            assertEquals(group.activeGroupCount, group.groupInfo.length);
            for (int j = 0; j < group.activeGroupCount; j++) {
                assertTrue(group.groupInfo[j].activeProcessorCount <= group.groupInfo[j].maximumProcessorCount);
                assertEquals(group.groupInfo[j].activeProcessorCount,
                        Long.bitCount(group.groupInfo[j].activeProcessorMask.longValue()));
                assertTrue(group.groupInfo[j].maximumProcessorCount <= 64);
            }
        }

        // Test PROCESSOR_RELATIONSHIP packages
        assertTrue(cores.size() >= packages.size());
        for (PROCESSOR_RELATIONSHIP pkg : packages) {
            assertEquals(LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorPackage, pkg.relationship);
            assertEquals(0, pkg.flags); // packages have 0 flags
            assertEquals(0, pkg.efficiencyClass); // packages have 0 efficiency
            assertEquals(pkg.groupCount, pkg.groupMask.length);
        }

        // Test PROCESSOR_RELATIONSHIP cores
        for (PROCESSOR_RELATIONSHIP core : cores) {
            assertEquals(LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorCore, core.relationship);
            // Hyperthreading flag set if at least 2 logical processors
            assertTrue(Long.bitCount(core.groupMask[0].mask.longValue()) > 0);
            if (Long.bitCount(core.groupMask[0].mask.longValue()) > 1) {
                assertEquals(WinNT.LTP_PC_SMT, core.flags);
            } else {
                assertEquals(0, core.flags);
            }
            // Cores are always in one group
            assertEquals(1, core.groupCount);
            assertEquals(1, core.groupMask.length);
        }

        // Test NUMA_NODE_RELATIONSHIP
        for (NUMA_NODE_RELATIONSHIP numaNode : numaNodes) {
            assertEquals(LOGICAL_PROCESSOR_RELATIONSHIP.RelationNumaNode, numaNode.relationship);
            assertTrue(numaNode.nodeNumber >= 0);
        }

        // Test CACHE_RELATIONSHIP
        for (CACHE_RELATIONSHIP cache : caches) {
            assertEquals(LOGICAL_PROCESSOR_RELATIONSHIP.RelationCache, cache.relationship);
            assertTrue(cache.level >= 1);
            assertTrue(cache.level <= 4);
            assertTrue(cache.cacheSize > 0);
            assertTrue(cache.lineSize > 0);
            assertTrue(cache.type == PROCESSOR_CACHE_TYPE.CacheUnified
                    || cache.type == PROCESSOR_CACHE_TYPE.CacheInstruction
                    || cache.type == PROCESSOR_CACHE_TYPE.CacheData || cache.type == PROCESSOR_CACHE_TYPE.CacheTrace);
            assertTrue(cache.associativity == WinNT.CACHE_FULLY_ASSOCIATIVE || cache.associativity > 0);
        }
    }

    public void testGetCurrentProcessPriority() {
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32Util.getCurrentProcessPriority()));
    }

    public void testSetCurrentProcessPriority() {
        Kernel32Util.setCurrentProcessPriority(Kernel32.HIGH_PRIORITY_CLASS);
    }

    public void testSetCurrentProcessBackgroundMode() {
        try {
            Kernel32Util.setCurrentProcessBackgroundMode(true);
        } finally {
            try {
                Kernel32Util.setCurrentProcessBackgroundMode(false); // Reset the "background" mode!
            } catch (Exception e) { }
        }
    }

    public void testGetCurrentThreadPriority() {
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32Util.getCurrentThreadPriority()));
    }

    public void testSetCurrentThreadPriority() {
        Kernel32Util.setCurrentThreadPriority(Kernel32.THREAD_PRIORITY_ABOVE_NORMAL);
    }

    public void testSetCurrentThreadBackgroundMode() {
        try {
            Kernel32Util.setCurrentThreadBackgroundMode(true);
        } finally {
            try {
                Kernel32Util.setCurrentThreadBackgroundMode(false); // Reset the "background" mode!
            } catch (Exception e) { }
        }
    }

    public void testGetProcessPriority() {
        final int pid = Kernel32.INSTANCE.GetCurrentProcessId();
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32Util.getProcessPriority(pid)));
    }

    public void testSetProcessPriority() {
        final int pid = Kernel32.INSTANCE.GetCurrentProcessId();
        Kernel32Util.setProcessPriority(pid, Kernel32.HIGH_PRIORITY_CLASS);
    }

    public void testGetThreadPriority() {
        final int tid = Kernel32.INSTANCE.GetCurrentThreadId();
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32Util.getThreadPriority(tid)));
    }

    public void testSetThreadPriority() {
        final int tid = Kernel32.INSTANCE.GetCurrentThreadId();
        Kernel32Util.setThreadPriority(tid, Kernel32.THREAD_PRIORITY_ABOVE_NORMAL);
    }

    public void testIsValidPriorityClass() {
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32.NORMAL_PRIORITY_CLASS));
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32.IDLE_PRIORITY_CLASS));
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32.HIGH_PRIORITY_CLASS));
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32.REALTIME_PRIORITY_CLASS));
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32.BELOW_NORMAL_PRIORITY_CLASS));
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32.ABOVE_NORMAL_PRIORITY_CLASS));
        assertFalse(Kernel32Util.isValidPriorityClass(new DWORD(0L)));
        assertFalse(Kernel32Util.isValidPriorityClass(new DWORD(1L)));
        assertFalse(Kernel32Util.isValidPriorityClass(new DWORD(0xFFFFFFFF)));
        assertFalse(Kernel32Util.isValidPriorityClass(Kernel32.PROCESS_MODE_BACKGROUND_BEGIN));
        assertFalse(Kernel32Util.isValidPriorityClass(Kernel32.PROCESS_MODE_BACKGROUND_END));
    }

    public void testIsValidThreadPriority() {
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_PRIORITY_IDLE));
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_PRIORITY_LOWEST));
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_PRIORITY_BELOW_NORMAL));
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_PRIORITY_NORMAL));
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_PRIORITY_ABOVE_NORMAL));
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_PRIORITY_HIGHEST));
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_PRIORITY_TIME_CRITICAL));
        assertFalse(Kernel32Util.isValidThreadPriority(  3));
        assertFalse(Kernel32Util.isValidThreadPriority( -3));
        assertFalse(Kernel32Util.isValidThreadPriority( 16));
        assertFalse(Kernel32Util.isValidThreadPriority(-16));
        assertFalse(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_MODE_BACKGROUND_BEGIN));
        assertFalse(Kernel32Util.isValidThreadPriority(Kernel32.THREAD_MODE_BACKGROUND_END));
    }
}
