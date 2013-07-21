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

import static com.sun.jna.platform.win32.RegexMatcher.matches;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinBase.MEMORYSTATUSEX;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFO;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFOEX;
import com.sun.jna.ptr.IntByReference;

public class Kernel32Test extends TestCase {

    public static void main(String[] args) {
    	OSVERSIONINFO lpVersionInfo = new OSVERSIONINFO();
    	assertTrue(Kernel32.INSTANCE.GetVersionEx(lpVersionInfo));
    	System.out.println("Operating system: "
    			+ lpVersionInfo.dwMajorVersion.longValue() + "." + lpVersionInfo.dwMinorVersion.longValue()
    			+ " (" + lpVersionInfo.dwBuildNumber + ")"
    			+ " [" + Native.toString(lpVersionInfo.szCSDVersion) + "]");
        junit.textui.TestRunner.run(Kernel32Test.class);
    }

    public void testGetDriveType() {
        if (!Platform.isWindows()) return;

        Kernel32 kernel = Kernel32.INSTANCE;
        assertEquals("Wrong drive type.", WinBase.DRIVE_FIXED, kernel.GetDriveType("c:"));
    }

    public void testStructureOutArgument() {
        Kernel32 kernel = Kernel32.INSTANCE;
        WinBase.SYSTEMTIME time = new WinBase.SYSTEMTIME();
        kernel.GetSystemTime(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        assertEquals("Hour not properly set",
                     cal.get(Calendar.HOUR_OF_DAY), time.wHour);
        assertEquals("Day not properly set",
                     cal.get(Calendar.DAY_OF_WEEK)-1,
                     time.wDayOfWeek);
        assertEquals("Year not properly set",
                     cal.get(Calendar.YEAR), time.wYear);
    }

    public void testGetLastError() {
        Kernel32 kernel = Kernel32.INSTANCE;
        int ERRCODE  = 8;

        kernel.SetLastError(ERRCODE);
        int code = kernel.GetLastError();
        assertEquals("Wrong error value after SetLastError", ERRCODE, code);

        if (kernel.GetProcessVersion(-1) == 0) {
            final int INVALID_PARAMETER = 87;
            code = kernel.GetLastError();
            assertEquals("Wrong error value after failed syscall", INVALID_PARAMETER, code);
        }
        else {
            fail("GetProcessId(NULL) should fail");
        }
    }

    public void testConvertHWND_BROADCAST() {
        HWND hwnd = WinUser.HWND_BROADCAST;
        NativeMappedConverter.getInstance(hwnd.getClass()).toNative(hwnd, null);
    }

    public void testGetComputerName() {
    	IntByReference lpnSize = new IntByReference(0);
    	assertFalse(Kernel32.INSTANCE.GetComputerName(null, lpnSize));
    	assertEquals(WinError.ERROR_BUFFER_OVERFLOW, Kernel32.INSTANCE.GetLastError());
    	char buffer[] = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
    	lpnSize.setValue(buffer.length);
    	assertTrue(Kernel32.INSTANCE.GetComputerName(buffer, lpnSize));
    }

    public void testWaitForSingleObject() {
		HANDLE handle = Kernel32.INSTANCE.CreateEvent(null, false, false, null);

		// handle runs into timeout since it is not triggered
		// WAIT_TIMEOUT = 0x00000102
		assertEquals(WinError.WAIT_TIMEOUT, Kernel32.INSTANCE.WaitForSingleObject(
				handle, 1000));

		Kernel32.INSTANCE.CloseHandle(handle);
	}

    public void testWaitForMultipleObjects(){
    	HANDLE[] handles = new HANDLE[2];

		handles[0] = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
		handles[1] = Kernel32.INSTANCE.CreateEvent(null, false, false, null);

		// handle runs into timeout since it is not triggered
		// WAIT_TIMEOUT = 0x00000102
		assertEquals(WinError.WAIT_TIMEOUT, Kernel32.INSTANCE.WaitForMultipleObjects(
				handles.length, handles, false, 1000));

		Kernel32.INSTANCE.CloseHandle(handles[0]);
		Kernel32.INSTANCE.CloseHandle(handles[1]);

		// invalid Handle
		handles[0] = WinBase.INVALID_HANDLE_VALUE;
		handles[1] = Kernel32.INSTANCE.CreateEvent(null, false, false, null);

		// returns WAIT_FAILED since handle is invalid
		assertEquals(WinBase.WAIT_FAILED, Kernel32.INSTANCE.WaitForMultipleObjects(
				handles.length, handles, false, 5000));

		Kernel32.INSTANCE.CloseHandle(handles[1]);
    }

    public void testGetCurrentThreadId() {
    	assertTrue(Kernel32.INSTANCE.GetCurrentThreadId() > 0);
    }

    public void testGetCurrentThread() {
    	HANDLE h = Kernel32.INSTANCE.GetCurrentThread();
    	assertNotNull(h);
    	assertFalse(h.equals(0));
    	// CloseHandle does not need to be called for a thread handle
    	assertFalse(Kernel32.INSTANCE.CloseHandle(h));
    	assertEquals(WinError.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
    }

    public void testOpenThread() {
    	HANDLE h = Kernel32.INSTANCE.OpenThread(WinNT.THREAD_ALL_ACCESS, false,
    			Kernel32.INSTANCE.GetCurrentThreadId());
    	assertNotNull(h);
    	assertFalse(h.equals(0));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(h));
    }

    public void testGetCurrentProcessId() {
    	assertTrue(Kernel32.INSTANCE.GetCurrentProcessId() > 0);
    }

    public void testGetCurrentProcess() {
    	HANDLE h = Kernel32.INSTANCE.GetCurrentProcess();
    	assertNotNull(h);
    	assertFalse(h.equals(0));
    	// CloseHandle does not need to be called for a process handle
    	assertFalse(Kernel32.INSTANCE.CloseHandle(h));
    	assertEquals(WinError.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
    }

    public void testOpenProcess() {
    	HANDLE h = Kernel32.INSTANCE.OpenProcess(0, false,
    			Kernel32.INSTANCE.GetCurrentProcessId());
    	assertNull(h);
    	// opening your own process fails with access denied
    	assertEquals(WinError.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());
    }

    public void testGetTempPath() {
    	char[] buffer = new char[WinDef.MAX_PATH];
    	assertTrue(Kernel32.INSTANCE.GetTempPath(new DWORD(WinDef.MAX_PATH), buffer).intValue() > 0);
    }

	public void testGetTickCount() throws InterruptedException {
		// Tick count rolls over every 49.7 days, so to safeguard from
		// roll-over, we will get two time spans. At least one should
		// yield a positive.
		int tick1 = Kernel32.INSTANCE.GetTickCount();
		Thread.sleep(10);
		int tick2 = Kernel32.INSTANCE.GetTickCount();
		Thread.sleep(10);
		int tick3 = Kernel32.INSTANCE.GetTickCount();

		assertTrue(tick2 > tick1 || tick3 > tick2);
	}

    public void testGetVersion() {
    	DWORD version = Kernel32.INSTANCE.GetVersion();
    	assertTrue("Version high should be non-zero: 0x" + Integer.toHexString(version.getHigh().intValue()), version.getHigh().intValue() != 0);
    	assertTrue("Version low should be >= 0: 0x" + Integer.toHexString(version.getLow().intValue()), version.getLow().intValue() >= 0);
    }

    public void testGetVersionEx_OSVERSIONINFO() {
    	OSVERSIONINFO lpVersionInfo = new OSVERSIONINFO();
    	assertEquals(lpVersionInfo.size(), lpVersionInfo.dwOSVersionInfoSize.longValue());
    	assertTrue(Kernel32.INSTANCE.GetVersionEx(lpVersionInfo));
    	assertTrue(lpVersionInfo.dwMajorVersion.longValue() > 0);
    	assertTrue(lpVersionInfo.dwMinorVersion.longValue() >= 0);
    	assertEquals(lpVersionInfo.size(), lpVersionInfo.dwOSVersionInfoSize.longValue());
    	assertTrue(lpVersionInfo.dwPlatformId.longValue() > 0);
    	assertTrue(lpVersionInfo.dwBuildNumber.longValue() > 0);
    	assertTrue(Native.toString(lpVersionInfo.szCSDVersion).length() >= 0);
    }

    public void testGetVersionEx_OSVERSIONINFOEX() {
    	OSVERSIONINFOEX lpVersionInfo = new OSVERSIONINFOEX();
    	assertEquals(lpVersionInfo.size(), lpVersionInfo.dwOSVersionInfoSize.longValue());
    	assertTrue(Kernel32.INSTANCE.GetVersionEx(lpVersionInfo));
    	assertTrue(lpVersionInfo.dwMajorVersion.longValue() > 0);
    	assertTrue(lpVersionInfo.dwMinorVersion.longValue() >= 0);
    	assertEquals(lpVersionInfo.size(), lpVersionInfo.dwOSVersionInfoSize.longValue());
    	assertTrue(lpVersionInfo.dwPlatformId.longValue() > 0);
    	assertTrue(lpVersionInfo.dwBuildNumber.longValue() > 0);
    	assertTrue(Native.toString(lpVersionInfo.szCSDVersion).length() >= 0);
    	assertTrue(lpVersionInfo.wProductType >= 0);
    }

    public void testGetSystemInfo() {
    	SYSTEM_INFO lpSystemInfo = new SYSTEM_INFO();
    	Kernel32.INSTANCE.GetSystemInfo(lpSystemInfo);
    	assertTrue(lpSystemInfo.dwNumberOfProcessors.intValue() > 0);
    }

    public void testIsWow64Process() {
    	try {
	    	IntByReference isWow64 = new IntByReference(42);
	    	HANDLE hProcess = Kernel32.INSTANCE.GetCurrentProcess();
	    	assertTrue(Kernel32.INSTANCE.IsWow64Process(hProcess, isWow64));
	    	assertTrue(0 == isWow64.getValue() || 1 == isWow64.getValue());
    	} catch (UnsatisfiedLinkError e) {
    		// IsWow64Process is not available on this OS
    	}
    }

    public void testGetNativeSystemInfo() {
    	try {
        	SYSTEM_INFO lpSystemInfo = new SYSTEM_INFO();
        	Kernel32.INSTANCE.GetNativeSystemInfo(lpSystemInfo);
        	assertTrue(lpSystemInfo.dwNumberOfProcessors.intValue() > 0);
    	} catch (UnsatisfiedLinkError e) {
    		// only available under WOW64
    	}
    }

    public void testGlobalMemoryStatusEx() {
    	MEMORYSTATUSEX lpBuffer = new MEMORYSTATUSEX();
    	assertTrue(Kernel32.INSTANCE.GlobalMemoryStatusEx(lpBuffer));
    	assertTrue(lpBuffer.ullTotalPhys.longValue() > 0);
    	assertTrue(lpBuffer.dwMemoryLoad.intValue() >= 0 && lpBuffer.dwMemoryLoad.intValue() <= 100);
    	assertEquals(0, lpBuffer.ullAvailExtendedVirtual.intValue());
    }

    public void testGetLogicalDriveStrings() {
    	DWORD dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(new DWORD(0), null);
    	assertTrue(dwSize.intValue() > 0);
    	char buf[] = new char[dwSize.intValue()];
    	assertTrue(Kernel32.INSTANCE.GetLogicalDriveStrings(dwSize, buf).intValue() > 0);
    }

    public void testGetDiskFreeSpaceEx() {
    	LARGE_INTEGER.ByReference lpFreeBytesAvailable = new LARGE_INTEGER.ByReference();
    	LARGE_INTEGER.ByReference lpTotalNumberOfBytes = new LARGE_INTEGER.ByReference();
    	LARGE_INTEGER.ByReference lpTotalNumberOfFreeBytes = new LARGE_INTEGER.ByReference();
    	assertTrue(Kernel32.INSTANCE.GetDiskFreeSpaceEx(null,
    			lpFreeBytesAvailable, lpTotalNumberOfBytes, lpTotalNumberOfFreeBytes));
    	assertTrue(lpTotalNumberOfFreeBytes.getValue() > 0);
    	assertTrue(lpTotalNumberOfFreeBytes.getValue() < lpTotalNumberOfBytes.getValue());
    }

    public void testDeleteFile() {
    	String filename = Kernel32Util.getTempPath() + "\\FileDoesNotExist.jna";
    	assertFalse(Kernel32.INSTANCE.DeleteFile(filename));
    	assertEquals(WinError.ERROR_FILE_NOT_FOUND, Kernel32.INSTANCE.GetLastError());
    }

    public void testReadFile() throws IOException {
    	String expected = "jna - testReadFile";
    	File tmp = File.createTempFile(getName(), "jna");
    	tmp.deleteOnExit();
        try {
            FileWriter fw = new FileWriter(tmp);
            fw.append(expected);
            fw.close();
            
            HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_READ, WinNT.FILE_SHARE_READ,
                                                        new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
            assertFalse(hFile == WinBase.INVALID_HANDLE_VALUE);
            
            Memory m = new Memory(2048);
            IntByReference lpNumberOfBytesRead = new IntByReference(0);
            assertTrue(Kernel32.INSTANCE.ReadFile(hFile, m, (int) m.size(), lpNumberOfBytesRead, null));
            int read = lpNumberOfBytesRead.getValue();
            assertEquals(expected.length(), read);
            assertEquals(expected, new String(m.getByteArray(0, read)));
            
            assertTrue(Kernel32.INSTANCE.CloseHandle(hFile));
        }
        finally {
            tmp.delete();
        }
    }

    public void testSetHandleInformation() throws IOException {
    	File tmp = File.createTempFile(getName(), "jna");
    	tmp.deleteOnExit();
        try {
            HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_READ, WinNT.FILE_SHARE_READ,
                                                        new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
            assertFalse(hFile == WinBase.INVALID_HANDLE_VALUE);
            
            assertTrue(Kernel32.INSTANCE.SetHandleInformation(hFile, WinBase.HANDLE_FLAG_PROTECT_FROM_CLOSE, 0));
            assertTrue(Kernel32.INSTANCE.CloseHandle(hFile));
        }
        finally {
            tmp.delete();
        }
    }

    public void testCreatePipe() {
    	HANDLEByReference hReadPipe = new HANDLEByReference();
    	HANDLEByReference hWritePipe = new HANDLEByReference();

    	assertTrue(Kernel32.INSTANCE.CreatePipe(hReadPipe, hWritePipe, null, 0));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(hReadPipe.getValue()));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(hWritePipe.getValue()));
    }

    public void testGetExitCodeProcess() {
    	IntByReference lpExitCode = new IntByReference(0);
    	assertTrue(Kernel32.INSTANCE.GetExitCodeProcess(Kernel32.INSTANCE.GetCurrentProcess(), lpExitCode));
    	assertEquals(WinBase.STILL_ACTIVE, lpExitCode.getValue());
    }

    public void testTerminateProcess() throws IOException {
    	File tmp = File.createTempFile(getName(), "jna");
    	tmp.deleteOnExit();
        try {
            HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_READ, WinNT.FILE_SHARE_READ,
                                                        new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
            
            assertFalse(Kernel32.INSTANCE.TerminateProcess(hFile, 1));
            assertEquals(WinError.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
            assertTrue(Kernel32.INSTANCE.CloseHandle(hFile));
        }
        finally {
            tmp.delete();
        }
    }

    public void testGetFileAttributes() {
    	assertTrue(WinBase.INVALID_FILE_ATTRIBUTES != Kernel32.INSTANCE.GetFileAttributes("."));
    }

    public void testCopyFile() throws IOException {
        File source = File.createTempFile(getName(), "jna");
        source.deleteOnExit();
        File destination = new File(source.getParent(), source.getName() + "-destination");
        destination.deleteOnExit();
        try {
            Kernel32.INSTANCE.CopyFile(source.getCanonicalPath(), destination.getCanonicalPath(), true);
            assertTrue(destination.exists());
        }
        finally {
            source.delete();
            destination.delete();
        }
    }

    public void testMoveFile() throws IOException {
        File source = File.createTempFile(getName(), "jna");
        source.deleteOnExit();
        File destination = new File(source.getParent(), source.getName() + "-destination");
        destination.deleteOnExit();

        try {
            Kernel32.INSTANCE.MoveFile(source.getCanonicalPath(), destination.getCanonicalPath());
            assertTrue(destination.exists());
            assertFalse(source.exists());
        }
        finally {
            source.delete();
            destination.delete();
        }
    }

    public void testMoveFileEx() throws IOException {
        File source = File.createTempFile(getName(), "jna");
        source.deleteOnExit();
        File destination = File.createTempFile(getName()+"Copy", "jna");
        destination.deleteOnExit();

        try {
            Kernel32.INSTANCE.MoveFileEx(source.getCanonicalPath(), destination.getCanonicalPath(), new DWORD(WinBase.MOVEFILE_REPLACE_EXISTING));
            assertTrue(destination.exists());
            assertFalse(source.exists());
        }
        finally {
            source.delete();
            destination.delete();
        }
    }

    public void testCreateProcess() {
        WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
        WinBase.PROCESS_INFORMATION.ByReference processInformation = new WinBase.PROCESS_INFORMATION.ByReference();

        boolean status = Kernel32.INSTANCE.CreateProcess(
            null,
            "cmd.exe /c echo hi",
            null,
            null,
            true,
            new WinDef.DWORD(0),
            Pointer.NULL,
            System.getProperty("java.io.tmpdir"),
            startupInfo,
            processInformation);

        assertTrue(status);
        assertTrue(processInformation.dwProcessId.longValue() > 0);
    }

    public void testCreateProcessW() {
        WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
        WinBase.PROCESS_INFORMATION.ByReference processInformation = new WinBase.PROCESS_INFORMATION.ByReference();

        boolean status = Kernel32.INSTANCE.CreateProcessW(
            null,
            Native.toCharArray("cmd.exe /c echo hi"),
            null,
            null,
            true,
            new WinDef.DWORD(0),
            Pointer.NULL,
            System.getProperty("java.io.tmpdir"),
            startupInfo,
            processInformation);

        assertTrue(status);
        assertTrue(processInformation.dwProcessId.longValue() > 0);
    }

    public void testGetEnvironmentVariable() {
    	assertTrue(Kernel32.INSTANCE.SetEnvironmentVariable("jna-getenvironment-test", "42"));
    	int size = Kernel32.INSTANCE.GetEnvironmentVariable("jna-getenvironment-test", null, 0);
    	assertTrue(size == 3);
    	char[] data = new char[size];
    	assertEquals(size - 1, Kernel32.INSTANCE.GetEnvironmentVariable("jna-getenvironment-test", data, size));
    	assertEquals(size - 1, Native.toString(data).length());
    }

    public void testSetEnvironmentVariable() {
        int value = new Random().nextInt();
        Kernel32.INSTANCE.SetEnvironmentVariable("jna-setenvironment-test", Integer.toString(value));
        assertEquals(Integer.toString(value), Kernel32Util.getEnvironmentVariable("jna-setenvironment-test"));
    }

    public void testGetSetFileTime() throws IOException {
        File tmp = File.createTempFile(getName(), "jna");
        tmp.deleteOnExit();
        try {
            HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_WRITE, WinNT.FILE_SHARE_WRITE,
                                                        new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
            assertFalse(hFile == WinBase.INVALID_HANDLE_VALUE);
            
            WinBase.FILETIME.ByReference creationTime = new WinBase.FILETIME.ByReference();
            WinBase.FILETIME.ByReference accessTime = new WinBase.FILETIME.ByReference();
            WinBase.FILETIME.ByReference modifiedTime = new WinBase.FILETIME.ByReference();
            Kernel32.INSTANCE.GetFileTime(hFile, creationTime, accessTime, modifiedTime);
            
            assertEquals(creationTime.toDate().getYear(), new Date().getYear());
            assertEquals(accessTime.toDate().getYear(), new Date().getYear());
            assertEquals(modifiedTime.toDate().getYear(), new Date().getYear());
            
            Kernel32.INSTANCE.SetFileTime(hFile, null, null, new WinBase.FILETIME(new Date(2010, 1, 1)));
            
            assertTrue(Kernel32.INSTANCE.CloseHandle(hFile));
            
            assertEquals(2010, new Date(tmp.lastModified()).getYear());
        }
        finally {
            tmp.delete();
        }
    }

    public void testSetFileAttributes() throws IOException {
        File tmp = File.createTempFile(getName(), "jna");
        tmp.deleteOnExit();
        try {
            Kernel32.INSTANCE.SetFileAttributes(tmp.getCanonicalPath(), new DWORD(WinNT.FILE_ATTRIBUTE_HIDDEN));
            int attributes = Kernel32.INSTANCE.GetFileAttributes(tmp.getCanonicalPath());
            
            assertTrue((attributes & WinNT.FILE_ATTRIBUTE_HIDDEN) != 0);
        }
        finally {
            tmp.delete();
        }
    }
        
    public void testGetProcessList() throws IOException {
        WinNT.HANDLE processEnumHandle = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPALL, new WinDef.DWORD(0));
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(processEnumHandle));

        Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();

        assertTrue(Kernel32.INSTANCE.Process32First(processEnumHandle, processEntry));

        List<Long> processIdList = new ArrayList<Long>();
        processIdList.add(processEntry.th32ProcessID.longValue());

        while (Kernel32.INSTANCE.Process32Next(processEnumHandle, processEntry))
        {
            processIdList.add(processEntry.th32ProcessID.longValue());
        }

        assertTrue(Kernel32.INSTANCE.CloseHandle(processEnumHandle));
        assertTrue(processIdList.size() > 4);
    }

    public final void testGetPrivateProfileInt() throws IOException {
        final File tmp = File.createTempFile(getName(), "ini");
        tmp.deleteOnExit();
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            writer.println("[Section]");
            writer.println("existingKey = 123");
            writer.close();
            
            assertEquals(123, Kernel32.INSTANCE.GetPrivateProfileInt("Section", "existingKey", 456, tmp.getCanonicalPath()));
            assertEquals(456, Kernel32.INSTANCE.GetPrivateProfileInt("Section", "missingKey", 456, tmp.getCanonicalPath()));
        }
        finally {
            tmp.delete();
        }
    }

    public final void testGetPrivateProfileString() throws IOException {
        final File tmp = File.createTempFile(getName(), "ini");
        tmp.deleteOnExit();
        final String SECTION = "Section";
        final String VALUE = "ABC";
        final String DEFAULT = "DEF";
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            writer.println("[" + SECTION + "]");
            writer.println("existingKey = " + VALUE);
            writer.close();
            
            final char[] buffer = new char[8];
            DWORD len = Kernel32.INSTANCE.GetPrivateProfileString(SECTION, "existingKey", DEFAULT, buffer, new DWORD(buffer.length), tmp.getCanonicalPath());
            assertEquals("Wrong value length", new DWORD(VALUE.length()), len);
            assertEquals("Wrong existing value", VALUE, Native.toString(buffer));

            len = Kernel32.INSTANCE.GetPrivateProfileString("Section", "missingKey", DEFAULT, buffer, new DWORD(buffer.length), tmp.getCanonicalPath());
            assertEquals("Wrong value length", new DWORD(DEFAULT.length()), len);
            assertEquals("Wrong default value", DEFAULT, Native.toString(buffer));
        }
        finally {
            tmp.delete();
        }
    }

    public final void testWritePrivateProfileString() throws IOException {
        final File tmp = File.createTempFile(getName(), "ini");
        tmp.deleteOnExit();
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            writer.println("[Section]");
            writer.println("existingKey = ABC");
            writer.println("removedKey = JKL");
            writer.close();
            
            assertTrue(Kernel32.INSTANCE.WritePrivateProfileString("Section", "existingKey", "DEF", tmp.getCanonicalPath()));
            assertTrue(Kernel32.INSTANCE.WritePrivateProfileString("Section", "addedKey", "GHI", tmp.getCanonicalPath()));
            assertTrue(Kernel32.INSTANCE.WritePrivateProfileString("Section", "removedKey", null, tmp.getCanonicalPath()));
            
            final BufferedReader reader = new BufferedReader(new FileReader(tmp));
            assertEquals(reader.readLine(), "[Section]");
            assertTrue(reader.readLine().matches("existingKey\\s*=\\s*DEF"));
            assertTrue(reader.readLine().matches("addedKey\\s*=\\s*GHI"));
            assertEquals(reader.readLine(), null);
            reader.close();
        }
        finally {
            tmp.delete();
        }
    }

    @Test
    public final void testGetPrivateProfileSection() throws IOException {
        // given
        final File tmp = File.createTempFile(getName(), "ini");
        tmp.deleteOnExit();
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            try {
                writer.println("[X]");
                writer.println("A=1");
                writer.println("B=X");
            } finally {
                writer.close();
            }

            // when
            final char[] buffer = new char[9];
            final DWORD len = Kernel32.INSTANCE.GetPrivateProfileSection("X", buffer, new DWORD(buffer.length), tmp.getCanonicalPath());

            // then
            assertThat("Wrong length", len, is(new DWORD(7)));
            assertThat("Wrong content", buffer, is(anyOf(equalTo("A=1\0B=X\0\0".toCharArray()), equalTo("B=X\0A=1\0\0".toCharArray()))));
        } finally {
            tmp.delete();
        }
    }

    @Test
    public final void testGetPrivateProfileSectionNames() throws IOException {
        // given
        final File tmp = File.createTempFile(getName(), "ini");
        tmp.deleteOnExit();
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            try {
                writer.println("[S1]");
                writer.println("[S2]");
            } finally {
                writer.close();
            }

            // when
            final char[] buffer = new char[7];
            final DWORD len = Kernel32.INSTANCE.GetPrivateProfileSectionNames(buffer, new DWORD(buffer.length), tmp.getCanonicalPath());

            // then
            assertThat("Wrong length", len, is(new DWORD(5)));
            assertThat("Wrong content", buffer, is(anyOf(equalTo("S1\0S2\0\0".toCharArray()), equalTo("S2\0S1\0\0".toCharArray()))));
        } finally {
            tmp.delete();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testWritePrivateProfileSection() throws IOException {
        // given
        final File tmp = File.createTempFile(getName(), "ini");
        tmp.deleteOnExit();
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            try {
                writer.println("[S1]");
                writer.println("A=1");
                writer.println("B=X");
            } finally {
                writer.close();
            }

            // when
            final boolean result = Kernel32.INSTANCE.WritePrivateProfileSection("S1", "A=3\0E=Z\0\0", tmp.getCanonicalPath());

            // then
            assertThat("Wrong result", result, is(true));
            assertThat(readAllLines(tmp), hasItems(is("[S1]"), matches("A\\s*=\\s*3"), matches("E\\s*=\\s*Z")));
        } finally {
            tmp.delete();
        }
    }

    private static final List<String> readAllLines(final File file) throws IOException {
        final List<String> lines = new LinkedList<String>();
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine())
                lines.add(line);
        } finally {
            reader.close();
        }
        return lines;
    }
}
