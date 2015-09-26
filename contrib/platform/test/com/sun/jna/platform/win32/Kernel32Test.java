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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.sun.jna.Native;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.platform.win32.WinBase.MEMORYSTATUSEX;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.MEMORY_BASIC_INFORMATION;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFO;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFOEX;
import com.sun.jna.ptr.IntByReference;

import junit.framework.TestCase;

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

    // see https://github.com/twall/jna/issues/482
    public void testNoDuplicateMethodsNames() {
        Collection<String> dupSet = AbstractWin32TestSupport.detectDuplicateMethods(Kernel32.class);
        if (dupSet.size() > 0) {
            for (String name : new String[] {
                    // has 2 overloads by design since the API accepts both OSVERSIONINFO and OSVERSIONINFOEX
                    "GetVersionEx"
                }) {
                dupSet.remove(name);
            }
        }

        assertTrue("Duplicate methods found: " + dupSet, dupSet.isEmpty());
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

    public void testSetSystemTime() {
        Kernel32 kernel = Kernel32.INSTANCE;
        WinBase.SYSTEMTIME time = new WinBase.SYSTEMTIME();
        kernel.GetSystemTime(time);
        try {
            WinBase.SYSTEMTIME expected = new WinBase.SYSTEMTIME();
            expected.wYear = time.wYear;
            expected.wMonth = time.wMonth;
            expected.wDay = time.wDay;
            expected.wHour = time.wHour;
            expected.wMinute = time.wMinute;
            expected.wSecond = time.wSecond;
            expected.wMilliseconds = time.wMilliseconds;

            if (expected.wHour > 0) {
                expected.wHour--;
            } else {
                expected.wHour++;
            }

            if (assertTimeSettingOperationSucceeded("SetSystemTime", kernel.SetSystemTime(expected))) {
                WinBase.SYSTEMTIME actual = new WinBase.SYSTEMTIME();
                kernel.GetSystemTime(actual);
                assertEquals("Mismatched hour value", expected.wHour, actual.wHour);
            }
        } finally {
            assertTimeSettingOperationSucceeded("Restore original system time", kernel.SetSystemTime(time));
        }
    }

    public void testSetLocaltime() {
        Kernel32 kernel = Kernel32.INSTANCE;
        WinBase.SYSTEMTIME time = new WinBase.SYSTEMTIME();
        kernel.GetLocalTime(time);
        try {
            WinBase.SYSTEMTIME expected = new WinBase.SYSTEMTIME();
            expected.wYear = time.wYear;
            expected.wMonth = time.wMonth;
            expected.wDay = time.wDay;
            expected.wHour = time.wHour;
            expected.wMinute = time.wMinute;
            expected.wSecond = time.wSecond;
            expected.wMilliseconds = time.wMilliseconds;

            if (expected.wHour > 0) {
                expected.wHour--;
            } else {
                expected.wHour++;
            }

            if (assertTimeSettingOperationSucceeded("SetLocalTime", kernel.SetLocalTime(expected))) {
                WinBase.SYSTEMTIME actual = new WinBase.SYSTEMTIME();
                kernel.GetLocalTime(actual);
                assertEquals("Mismatched hour value", expected.wHour, actual.wHour);
            }
        } finally {
            assertTimeSettingOperationSucceeded("Restore local time", kernel.SetLocalTime(time));
        }
    }

    private static boolean assertTimeSettingOperationSucceeded(String message, boolean result) {
        if (result) {
            return result;
        }

        int hr=Kernel32.INSTANCE.GetLastError();
        /*
         * Check special error in case the user running the test isn't allowed
         * to change the time. This can happen for hosts that are managed
         * by some central administrator using an automated time setting mechanism.
         * In such cases, the user running the tests might not have admin
         * privileges and it may be too much to ask to have them just for running
         * this JNA API test...
         */
        if (hr == WinError.ERROR_PRIVILEGE_NOT_HELD) {
            return false; // don't fail the test, but signal the failure
        }

        if (hr != WinError.ERROR_SUCCESS) {
            fail(message + " failed: hr=" + hr);
        } else {
            fail(message + " unknown failure reason code");
        }

        return false;
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

    public void testGetComputerNameExSameAsGetComputerName() {
    	IntByReference lpnSize = new IntByReference(0);
    	char buffer[] = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
    	lpnSize.setValue(buffer.length);
    	assertTrue("Failed to retrieve expected computer name", Kernel32.INSTANCE.GetComputerName(buffer, lpnSize));
        String expected = Native.toString(buffer);

        // reset
    	lpnSize.setValue(buffer.length);
        Arrays.fill(buffer, '\0');
    	assertTrue("Failed to retrieve extended computer name", Kernel32.INSTANCE.GetComputerNameEx(WinBase.COMPUTER_NAME_FORMAT.ComputerNameNetBIOS, buffer, lpnSize));
        String  actual = Native.toString(buffer);

        assertEquals("Mismatched names", expected, actual);
    }

    public void testWaitForSingleObject() {
		HANDLE handle = Kernel32.INSTANCE.CreateEvent(null, false, false, null);

		// handle runs into timeout since it is not triggered
		// WAIT_TIMEOUT = 0x00000102
		assertEquals(WinError.WAIT_TIMEOUT, Kernel32.INSTANCE.WaitForSingleObject(
				handle, 1000));

		Kernel32.INSTANCE.CloseHandle(handle);
	}

    public void testResetEvent() {
		HANDLE handle = Kernel32.INSTANCE.CreateEvent(null, true, false, null);

		// set the event to the signaled state
		Kernel32.INSTANCE.SetEvent(handle);

		// This should return successfully
		assertEquals(WinBase.WAIT_OBJECT_0, Kernel32.INSTANCE.WaitForSingleObject(
				 handle, 1000));

		// now reset it to not signaled
		Kernel32.INSTANCE.ResetEvent(handle);

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

    public void testDeleteFile() {
    	String filename = Kernel32Util.getTempPath() + "\\FileDoesNotExist.jna";
    	assertFalse(Kernel32.INSTANCE.DeleteFile(filename));
    	assertEquals(WinError.ERROR_FILE_NOT_FOUND, Kernel32.INSTANCE.GetLastError());
    }

    public void testReadFile() throws IOException {
    	String expected = "jna - testReadFile";
    	File tmp = File.createTempFile("testReadFile", "jna");
    	tmp.deleteOnExit();

    	FileWriter fw = new FileWriter(tmp);
    	try {
    	    fw.append(expected);
    	} finally {
    	    fw.close();
    	}

    	HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_READ, WinNT.FILE_SHARE_READ,
    			new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
    	assertFalse("Failed to create file handle: " + tmp, WinBase.INVALID_HANDLE_VALUE.equals(hFile));

    	try {
            byte[] readBuffer=new byte[expected.length() + Byte.MAX_VALUE];
        	IntByReference lpNumberOfBytesRead = new IntByReference(0);
        	assertTrue("Failed to read from file", Kernel32.INSTANCE.ReadFile(hFile, readBuffer, readBuffer.length, lpNumberOfBytesRead, null));

        	int read = lpNumberOfBytesRead.getValue();
        	assertEquals("Mismatched read size", expected.length(), read);

        	assertEquals("Mismatched read content", expected, new String(readBuffer, 0, read));
    	} finally {
    	    assertTrue("Failed to close file", Kernel32.INSTANCE.CloseHandle(hFile));
    	}
    }

    public void testSetHandleInformation() throws IOException {
    	File tmp = File.createTempFile("testSetHandleInformation", "jna");
    	tmp.deleteOnExit();

    	HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_READ, WinNT.FILE_SHARE_READ,
    			new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
    	assertFalse(hFile == WinBase.INVALID_HANDLE_VALUE);

    	assertTrue(Kernel32.INSTANCE.SetHandleInformation(hFile, WinBase.HANDLE_FLAG_PROTECT_FROM_CLOSE, 0));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(hFile));
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
    	File tmp = File.createTempFile("testTerminateProcess", "jna");
    	tmp.deleteOnExit();
    	HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_READ, WinNT.FILE_SHARE_READ,
    			new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);

    	assertFalse(Kernel32.INSTANCE.TerminateProcess(hFile, 1));
    	assertEquals(WinError.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
    	assertTrue(Kernel32.INSTANCE.CloseHandle(hFile));
    }

    public void testGetFileAttributes() {
    	assertTrue(WinBase.INVALID_FILE_ATTRIBUTES != Kernel32.INSTANCE.GetFileAttributes("."));
    }

    public void testCopyFile() throws IOException {
        File source = File.createTempFile("testCopyFile", "jna");
        source.deleteOnExit();
        File destination = new File(source.getParent(), source.getName() + "-destination");
        destination.deleteOnExit();

        Kernel32.INSTANCE.CopyFile(source.getCanonicalPath(), destination.getCanonicalPath(), true);
        assertTrue(destination.exists());
    }

    public void testMoveFile() throws IOException {
        File source = File.createTempFile("testMoveFile", "jna");
        source.deleteOnExit();
        File destination = new File(source.getParent(), source.getName() + "-destination");
        destination.deleteOnExit();

        Kernel32.INSTANCE.MoveFile(source.getCanonicalPath(), destination.getCanonicalPath());
        assertTrue(destination.exists());
        assertFalse(source.exists());
    }

    public void testMoveFileEx() throws IOException {
        File source = File.createTempFile("testMoveFileEx", "jna");
        source.deleteOnExit();
        File destination = File.createTempFile("testCopyFile", "jna");
        destination.deleteOnExit();

        Kernel32.INSTANCE.MoveFileEx(source.getCanonicalPath(), destination.getCanonicalPath(), new DWORD(WinBase.MOVEFILE_REPLACE_EXISTING));
        assertTrue(destination.exists());
        assertFalse(source.exists());
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

    public void testGetSetFileTime() throws IOException {
        File tmp = File.createTempFile("testGetSetFileTime", "jna");
        tmp.deleteOnExit();

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

    public void testSetFileAttributes() throws IOException {
        File tmp = File.createTempFile("testSetFileAttributes", "jna");
        tmp.deleteOnExit();

        Kernel32.INSTANCE.SetFileAttributes(tmp.getCanonicalPath(), new DWORD(WinNT.FILE_ATTRIBUTE_HIDDEN));
        int attributes = Kernel32.INSTANCE.GetFileAttributes(tmp.getCanonicalPath());

        assertTrue((attributes & WinNT.FILE_ATTRIBUTE_HIDDEN) != 0);
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
        final File tmp = File.createTempFile("testGetPrivateProfileInt", "ini");
        tmp.deleteOnExit();
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        writer.println("[Section]");
        writer.println("existingKey = 123");
        writer.close();

        assertEquals(123, Kernel32.INSTANCE.GetPrivateProfileInt("Section", "existingKey", 456, tmp.getCanonicalPath()));
        assertEquals(456, Kernel32.INSTANCE.GetPrivateProfileInt("Section", "missingKey", 456, tmp.getCanonicalPath()));
    }

    public final void testGetPrivateProfileString() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileString", ".ini");
        tmp.deleteOnExit();
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        writer.println("[Section]");
        writer.println("existingKey = ABC");
        writer.close();

        final char[] buffer = new char[8];

        DWORD len = Kernel32.INSTANCE.GetPrivateProfileString("Section", "existingKey", "DEF", buffer, new DWORD(buffer.length), tmp.getCanonicalPath());
        assertEquals(3, len.intValue());
        assertEquals("ABC", Native.toString(buffer));

        len = Kernel32.INSTANCE.GetPrivateProfileString("Section", "missingKey", "DEF", buffer, new DWORD(buffer.length), tmp.getCanonicalPath());
        assertEquals(3, len.intValue());
        assertEquals("DEF", Native.toString(buffer));
    }

    public final void testWritePrivateProfileString() throws IOException {
        final File tmp = File.createTempFile("testWritePrivateProfileString", ".ini");
        tmp.deleteOnExit();
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

    public final void testGetPrivateProfileSection() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileSection", ".ini");
        tmp.deleteOnExit();

        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        try {
            writer.println("[X]");
            writer.println("A=1");
            writer.println("B=X");
        } finally {
            writer.close();
        }

        final char[] buffer = new char[9];
        final DWORD len = Kernel32.INSTANCE.GetPrivateProfileSection("X", buffer, new DWORD(buffer.length), tmp.getCanonicalPath());

        assertEquals(len.intValue(), 7);
        assertEquals(new String(buffer), "A=1\0B=X\0\0");
    }

    public final void testGetPrivateProfileSectionNames() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileSectionNames", ".ini");
        tmp.deleteOnExit();

        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        try {
            writer.println("[S1]");
            writer.println("[S2]");
        } finally {
            writer.close();
        }

        final char[] buffer = new char[7];
        final DWORD len = Kernel32.INSTANCE.GetPrivateProfileSectionNames(buffer, new DWORD(buffer.length), tmp.getCanonicalPath());
        assertEquals(len.intValue(), 5);
        assertEquals(new String(buffer), "S1\0S2\0\0");
    }

    public final void testWritePrivateProfileSection() throws IOException {
        final File tmp = File.createTempFile("testWritePrivateProfileSection", ".ini");
        tmp.deleteOnExit();

        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        try {
            writer.println("[S1]");
            writer.println("A=1");
            writer.println("B=X");
        } finally {
            writer.close();
        }

        final boolean result = Kernel32.INSTANCE.WritePrivateProfileSection("S1", "A=3\0E=Z\0\0", tmp.getCanonicalPath());
        assertTrue(result);

        final BufferedReader reader = new BufferedReader(new FileReader(tmp));
        assertEquals(reader.readLine(), "[S1]");
        assertTrue(reader.readLine().matches("A\\s*=\\s*3"));
        assertTrue(reader.readLine().matches("E\\s*=\\s*Z"));
        reader.close();
    }

    public final void testCreateRemoteThread() throws IOException {
    	HANDLE hThrd = Kernel32.INSTANCE.CreateRemoteThread(null, null, 0, null, null, null, null);
    	assertNull(hThrd);
    	assertEquals(Kernel32.INSTANCE.GetLastError(), WinError.ERROR_INVALID_HANDLE);
    }

    public void testWriteProcessMemory() {
    	Kernel32 kernel = Kernel32.INSTANCE;

    	boolean successWrite = kernel.WriteProcessMemory(null, Pointer.NULL, Pointer.NULL, 1, null);
    	assertFalse(successWrite);
    	assertEquals(kernel.GetLastError(), WinError.ERROR_INVALID_HANDLE);

    	ByteBuffer bufDest = ByteBuffer.allocateDirect(4);
    	bufDest.put(new byte[]{0,1,2,3});
    	ByteBuffer bufSrc = ByteBuffer.allocateDirect(4);
    	bufSrc.put(new byte[]{5,10,15,20});
    	Pointer ptrSrc = Native.getDirectBufferPointer(bufSrc);
    	Pointer ptrDest = Native.getDirectBufferPointer(bufDest);

    	HANDLE selfHandle = kernel.GetCurrentProcess();
    	kernel.WriteProcessMemory(selfHandle, ptrDest, ptrSrc, 3, null);//Write only the first three

		assertEquals(bufDest.get(0),5);
    	assertEquals(bufDest.get(1),10);
    	assertEquals(bufDest.get(2),15);
    	assertEquals(bufDest.get(3),3);
	}

    public void testReadProcessMemory() {
    	Kernel32 kernel = Kernel32.INSTANCE;

    	boolean successRead = kernel.ReadProcessMemory(null, Pointer.NULL, Pointer.NULL, 1, null);
    	assertFalse(successRead);
    	assertEquals(kernel.GetLastError(), WinError.ERROR_INVALID_HANDLE);

    	ByteBuffer bufSrc = ByteBuffer.allocateDirect(4);
    	bufSrc.put(new byte[]{5,10,15,20});
    	ByteBuffer bufDest = ByteBuffer.allocateDirect(4);
    	bufDest.put(new byte[]{0,1,2,3});
    	Pointer ptrSrc = Native.getDirectBufferPointer(bufSrc);
    	Pointer ptrDest = Native.getDirectBufferPointer(bufDest);

    	HANDLE selfHandle = kernel.GetCurrentProcess();
    	kernel.ReadProcessMemory(selfHandle, ptrSrc, ptrDest, 3, null);//Read only the first three

		assertEquals(bufDest.get(0),5);
    	assertEquals(bufDest.get(1),10);
    	assertEquals(bufDest.get(2),15);
    	assertEquals(bufDest.get(3),3);
    }

    public void testVirtualQueryEx() {
        HANDLE selfHandle = Kernel32.INSTANCE.GetCurrentProcess();
        MEMORY_BASIC_INFORMATION mbi = new MEMORY_BASIC_INFORMATION();
        SIZE_T bytesRead = Kernel32.INSTANCE.VirtualQueryEx(selfHandle, Pointer.NULL, mbi, new SIZE_T(mbi.size()));
        assertTrue(bytesRead.intValue() > 0);
    }
    
	public void testGetCommState() {
		WinBase.DCB lpDCB = new WinBase.DCB();
		// Here we test a com port that definitely does not exist!
		HANDLE handleSerialPort = Kernel32.INSTANCE.CreateFile("\\\\.\\comDummy",
				WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0, null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL,
				null);

		int lastError = Kernel32.INSTANCE.GetLastError();
		assertEquals(lastError, WinNT.ERROR_FILE_NOT_FOUND);
		//try to read the com port state using the invalid handle
		assertFalse(Kernel32.INSTANCE.GetCommState(handleSerialPort, lpDCB));
		// Check if we can open a connection to com port1
		// If yes, we try to read the com state
		// If no com port exists we have to skip this test
		handleSerialPort = Kernel32.INSTANCE.CreateFile("\\\\.\\com1", WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0,
				null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
		lastError = Kernel32.INSTANCE.GetLastError();
		if (WinNT.NO_ERROR == lastError) {
			assertFalse(WinNT.INVALID_HANDLE_VALUE.equals(handleSerialPort));
			try {				
				lpDCB = new WinBase.DCB();
				assertTrue(Kernel32.INSTANCE.GetCommState(handleSerialPort, lpDCB));				
				switch (lpDCB.BaudRate.intValue()) {
					case WinBase.CBR_110:
					case WinBase.CBR_1200:
					case WinBase.CBR_128000:
					case WinBase.CBR_14400:
					case WinBase.CBR_19200:
					case WinBase.CBR_2400:
					case WinBase.CBR_256000:
					case WinBase.CBR_300:
					case WinBase.CBR_38400:
					case WinBase.CBR_4800:
					case WinBase.CBR_56000:
					case WinBase.CBR_600:
					case WinBase.CBR_9600:
					break;
					default:
						fail("Received value of WinBase.DCB.BaudRate is not valid");
				}
			} finally {
				Kernel32.INSTANCE.CloseHandle(handleSerialPort);
			}
		}
	}
	
	public void testSetCommState() {
		WinBase.DCB lpDCB = new WinBase.DCB();
		// Here we test a com port that definitely does not exist!
		HANDLE handleSerialPort = Kernel32.INSTANCE.CreateFile("\\\\.\\comDummy",
				WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0, null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL,
				null);

		int lastError = Kernel32.INSTANCE.GetLastError();
		assertEquals(lastError, WinNT.ERROR_FILE_NOT_FOUND);
		// try to read the com port state using the invalid handle
		assertFalse(Kernel32.INSTANCE.SetCommState(handleSerialPort, lpDCB));
		// Check if we can open a connection to com port1
		// If yes, we try to read the com state
		// If no com port exists we have to skip this test
		handleSerialPort = Kernel32.INSTANCE.CreateFile("\\\\.\\com1", WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0,
				null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
		lastError = Kernel32.INSTANCE.GetLastError();
		if (WinNT.NO_ERROR == lastError) {
			assertFalse(WinNT.INVALID_HANDLE_VALUE.equals(handleSerialPort));
			try {
				lpDCB = new WinBase.DCB();
				assertTrue(Kernel32.INSTANCE.GetCommState(handleSerialPort, lpDCB));
				DWORD oldBaudRate = new DWORD(lpDCB.BaudRate.longValue());

				lpDCB.BaudRate = new DWORD(WinBase.CBR_110);

				assertTrue(Kernel32.INSTANCE.SetCommState(handleSerialPort, lpDCB));
				WinBase.DCB lpNewDCB = new WinBase.DCB();
				assertTrue(Kernel32.INSTANCE.GetCommState(handleSerialPort, lpNewDCB));

				assertEquals(WinBase.CBR_110, lpNewDCB.BaudRate.intValue()); 

				lpDCB.BaudRate = oldBaudRate;
				assertTrue(Kernel32.INSTANCE.SetCommState(handleSerialPort, lpDCB));

			} finally {
				Kernel32.INSTANCE.CloseHandle(handleSerialPort);
			}
		}
	}
		
	public void testGetCommTimeouts() {
		WinBase.COMMTIMEOUTS lpCommTimeouts = new WinBase.COMMTIMEOUTS();

		// Here we test a com port that definitely does not exist!
		HANDLE handleSerialPort = Kernel32.INSTANCE.CreateFile("\\\\.\\comDummy",
				WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0, null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL,
				null);

		int lastError = Kernel32.INSTANCE.GetLastError();
		assertEquals(lastError, WinNT.ERROR_FILE_NOT_FOUND);
		// try to read the com port timeouts using the invalid handle
		assertFalse(Kernel32.INSTANCE.GetCommTimeouts(handleSerialPort, lpCommTimeouts));

		// Check if we can open a connection to com port1
		// If yes, we try to read the com state
		// If no com port exists we have to skip this test
		handleSerialPort = Kernel32.INSTANCE.CreateFile("\\\\.\\com1", WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0,
				null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
		lastError = Kernel32.INSTANCE.GetLastError();
		if (WinNT.NO_ERROR == lastError) {
			assertFalse(WinNT.INVALID_HANDLE_VALUE.equals(handleSerialPort));
			try {
				lpCommTimeouts = new WinBase.COMMTIMEOUTS();
				assertTrue(Kernel32.INSTANCE.GetCommTimeouts(handleSerialPort, lpCommTimeouts));
			} finally {
				Kernel32.INSTANCE.CloseHandle(handleSerialPort);
			}
		}
	}
	
	public void testSetCommTimeouts() {
		WinBase.COMMTIMEOUTS lpCommTimeouts = new WinBase.COMMTIMEOUTS();

		// Here we test a com port that definitely does not exist!
		HANDLE handleSerialPort = Kernel32.INSTANCE.CreateFile("\\\\.\\comDummy",
				WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0, null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL,
				null);

		int lastError = Kernel32.INSTANCE.GetLastError();
		assertEquals(lastError, WinNT.ERROR_FILE_NOT_FOUND);
		// try to store the com port timeouts using the invalid handle
		assertFalse(Kernel32.INSTANCE.SetCommTimeouts(handleSerialPort, lpCommTimeouts));

		// Check if we can open a connection to com port1
		// If yes, we try to store the com timeouts
		// If no com port exists we have to skip this test
		handleSerialPort = Kernel32.INSTANCE.CreateFile("\\\\.\\com1", WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0,
				null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
		lastError = Kernel32.INSTANCE.GetLastError();
		if (WinNT.NO_ERROR == lastError) {
			assertFalse(WinNT.INVALID_HANDLE_VALUE.equals(handleSerialPort));
			try {
				lpCommTimeouts = new WinBase.COMMTIMEOUTS();
				assertTrue(Kernel32.INSTANCE.GetCommTimeouts(handleSerialPort, lpCommTimeouts));

				DWORD oldReadIntervalTimeout = new DWORD(lpCommTimeouts.ReadIntervalTimeout.longValue());

				lpCommTimeouts.ReadIntervalTimeout = new DWORD(20);

				assertTrue(Kernel32.INSTANCE.SetCommTimeouts(handleSerialPort, lpCommTimeouts));

				WinBase.COMMTIMEOUTS lpNewCommTimeouts = new WinBase.COMMTIMEOUTS();
				assertTrue(Kernel32.INSTANCE.GetCommTimeouts(handleSerialPort, lpNewCommTimeouts));

				assertEquals(20, lpNewCommTimeouts.ReadIntervalTimeout.intValue());

				lpCommTimeouts.ReadIntervalTimeout = oldReadIntervalTimeout;

				assertTrue(Kernel32.INSTANCE.SetCommTimeouts(handleSerialPort, lpCommTimeouts));

			} finally {
				Kernel32.INSTANCE.CloseHandle(handleSerialPort);
			}
		}
	}
}
