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

import static com.sun.jna.platform.win32.WinBase.WAIT_OBJECT_0;
import static com.sun.jna.platform.win32.WinNT.MEM_COMMIT;
import static com.sun.jna.platform.win32.WinNT.MEM_RESERVE;
import static com.sun.jna.platform.win32.WinNT.PAGE_EXECUTE_READWRITE;
import static com.sun.jna.platform.win32.WinioctlUtil.FSCTL_GET_COMPRESSION;
import static com.sun.jna.platform.win32.WinioctlUtil.FSCTL_GET_REPARSE_POINT;
import static com.sun.jna.platform.win32.WinioctlUtil.FSCTL_SET_COMPRESSION;
import static com.sun.jna.platform.win32.WinioctlUtil.FSCTL_SET_REPARSE_POINT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTRByReference;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.Ntifs.REPARSE_DATA_BUFFER;
import com.sun.jna.platform.win32.Ntifs.SymbolicLinkReparseBuffer;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.FILE_ATTRIBUTE_TAG_INFO;
import com.sun.jna.platform.win32.WinBase.FILE_BASIC_INFO;
import com.sun.jna.platform.win32.WinBase.FILE_COMPRESSION_INFO;
import com.sun.jna.platform.win32.WinBase.FILE_DISPOSITION_INFO;
import com.sun.jna.platform.win32.WinBase.FILE_ID_INFO;
import com.sun.jna.platform.win32.WinBase.FILE_STANDARD_INFO;
import com.sun.jna.platform.win32.WinBase.MEMORYSTATUSEX;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinBase.WIN32_FIND_DATA;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinNT.MEMORY_BASIC_INFORMATION;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFO;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFOEX;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;


import junit.framework.TestCase;
import org.junit.Assume;

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

    // see https://github.com/java-native-access/jna/issues/604
    public void testGetLastErrorNativeLibraryOverride() {
        assertFalse("Unexpected success", Kernel32.INSTANCE.CloseHandle(null));
        assertEquals("Mismatched error code", WinError.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
    }

    // see https://github.com/twall/jna/issues/482
    public void testNoDuplicateMethodsNames() {
        Collection<String> dupSet = AbstractWin32TestSupport.detectDuplicateMethods(Kernel32.class);
        if (dupSet.size() > 0) {
            for (String name : new String[]{
                // has 2 overloads by design since the API accepts both OSVERSIONINFO and OSVERSIONINFOEX
                "GetVersionEx",
                // one version is kind-of broken and retained for compatiblity (deprecated)
                "CreateRemoteThread"
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
        assertNotNull("Failed to create event: " + Kernel32.INSTANCE.GetLastError(), handle);

        try {
            // handle runs into timeout since it is not triggered
            // WAIT_TIMEOUT = 0x00000102
            assertEquals(WinError.WAIT_TIMEOUT, Kernel32.INSTANCE.WaitForSingleObject(handle, 1000));
        } finally {
            Kernel32Util.closeHandle(handle);
        }
    }

    public void testOpenEvent() {
        HANDLE handle = null, handle2 = null;

        try {
            handle = Kernel32.INSTANCE.CreateEvent(null, false, false, "jna-kernel32test");
            assertNotNull("Failed to create event: " + Kernel32.INSTANCE.GetLastError(), handle);

            handle2 = Kernel32.INSTANCE.OpenEvent(WinNT.EVENT_MODIFY_STATE, false, "jna-kernel32test");
            assertNotNull("Failed to open event: " + Kernel32.INSTANCE.GetLastError(), handle2);

            Kernel32.INSTANCE.SetEvent(handle2);

            assertEquals(WinBase.WAIT_OBJECT_0, Kernel32.INSTANCE.WaitForSingleObject(handle, 1000));
        } finally {
            Kernel32Util.closeHandle(handle);
            Kernel32Util.closeHandle(handle2);
        }
    }

    public void testResetEvent() {
        HANDLE handle = Kernel32.INSTANCE.CreateEvent(null, true, false, null);
        assertNotNull("Failed to create event: " + Kernel32.INSTANCE.GetLastError(), handle);

        try {
            // set the event to the signaled state
            Kernel32.INSTANCE.SetEvent(handle);

            // This should return successfully
            assertEquals(WinBase.WAIT_OBJECT_0, Kernel32.INSTANCE.WaitForSingleObject(
                     handle, 1000));

            // now reset it to not signaled
            Kernel32.INSTANCE.ResetEvent(handle);

            // handle runs into timeout since it is not triggered
            // WAIT_TIMEOUT = 0x00000102
            assertEquals(WinError.WAIT_TIMEOUT, Kernel32.INSTANCE.WaitForSingleObject(handle, 1000));
        } finally {
            Kernel32Util.closeHandle(handle);
        }
    }

    public void testWaitForMultipleObjects(){
        HANDLE[] handles = new HANDLE[2];
        try {
            for (int index = 0; index < handles.length; index++) {
                HANDLE h = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
                assertNotNull("Failed to create event #" + index + ": " + Kernel32.INSTANCE.GetLastError(), h);
                handles[index] = h;
            }
            // handle runs into timeout since it is not triggered
            // WAIT_TIMEOUT = 0x00000102
            assertEquals(WinError.WAIT_TIMEOUT, Kernel32.INSTANCE.WaitForMultipleObjects(
                    handles.length, handles, false, 1000));
        } finally {
            Kernel32Util.closeHandles(handles);
        }

        // invalid Handle
        handles[0] = WinBase.INVALID_HANDLE_VALUE;
        handles[1] = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
        assertNotNull("Failed to create valid event: " + Kernel32.INSTANCE.GetLastError(), handles[1]);
        try {
            // returns WAIT_FAILED since handle is invalid
            assertEquals(WinBase.WAIT_FAILED, Kernel32.INSTANCE.WaitForMultipleObjects(
                    handles.length, handles, false, 5000));

        } finally {
            Kernel32Util.closeHandle(handles[1]);
        }
    }

    public void testGetCurrentThreadId() {
        assertTrue(Kernel32.INSTANCE.GetCurrentThreadId() > 0);
    }

    public void testGetCurrentThread() {
        HANDLE h = Kernel32.INSTANCE.GetCurrentThread();
        assertNotNull("No current thread handle", h);
        assertNotNull("Null current thread handle", h.getPointer());
    }

    public void testOpenThread() {
        HANDLE h = Kernel32.INSTANCE.OpenThread(WinNT.THREAD_ALL_ACCESS, false,
                Kernel32.INSTANCE.GetCurrentThreadId());
        assertNotNull(h);
        assertNotNull(h.getPointer());
        Kernel32Util.closeHandle(h);
    }

    public void testGetCurrentProcessId() {
        assertTrue(Kernel32.INSTANCE.GetCurrentProcessId() > 0);
    }

    public void testGetCurrentProcess() {
        HANDLE h = Kernel32.INSTANCE.GetCurrentProcess();
        assertNotNull("No current process handle", h);
        assertNotNull("Null current process handle", h.getPointer());
    }

    public void testOpenProcess() {
        HANDLE h = Kernel32.INSTANCE.OpenProcess(0, false,
                Kernel32.INSTANCE.GetCurrentProcessId());
        assertNull(h);
        // opening your own process fails with access denied
        assertEquals(WinError.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());
    }

    public void testQueryFullProcessImageName() {
        int pid = Kernel32.INSTANCE.GetCurrentProcessId();
        HANDLE h = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION, false, pid);
        assertNotNull("Failed (" + Kernel32.INSTANCE.GetLastError() + ") to get process ID=" + pid + " handle", h);

        try {
            char[] path = new char[WinDef.MAX_PATH];
            IntByReference lpdwSize = new IntByReference(path.length);
            boolean b = Kernel32.INSTANCE.QueryFullProcessImageName(h, 0, path, lpdwSize);
            assertTrue("Failed (" + Kernel32.INSTANCE.GetLastError() + ") to query process image name", b);
            assertTrue("Failed to query process image name, empty path returned", lpdwSize.getValue() > 0);
        } finally {
            Kernel32Util.closeHandle(h);
        }
    }

    public void testGetProcessTimes() {
        int pid = Kernel32.INSTANCE.GetCurrentProcessId();
        HANDLE h = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION, false, pid);
        assertNotNull("Failed (" + Kernel32.INSTANCE.GetLastError() + ") to get process ID=" + pid + " handle", h);

        try {
            FILETIME lpCreationTime = new FILETIME();
            FILETIME lpExitTime = new FILETIME();
            FILETIME lpKernelTime = new FILETIME();
            FILETIME lpUserTime = new FILETIME();
            boolean b = Kernel32.INSTANCE.GetProcessTimes(h, lpCreationTime, lpExitTime, lpKernelTime, lpUserTime);
            assertTrue("Failed (" + Kernel32.INSTANCE.GetLastError() + ") to get process times", b);
            // Process must have started before now.
            long upTimeMillis = System.currentTimeMillis() - lpCreationTime.toTime();
            assertTrue(upTimeMillis >= 0);
            // lpExitTime is undefined for a running process, do not test
            // Kernel and User time must be < up time (in 100ns ticks)
            assertTrue(lpKernelTime.toDWordLong().longValue()
                    + lpUserTime.toDWordLong().longValue() < (upTimeMillis + 1L) * 10000L);
        } finally {
            Kernel32Util.closeHandle(h);
        }
    }

    public void testGetProcessIoCounters() {
        int pid = Kernel32.INSTANCE.GetCurrentProcessId();
        HANDLE h = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION, false, pid);
        assertNotNull("Failed (" + Kernel32.INSTANCE.GetLastError() + ") to get process ID=" + pid + " handle", h);

        try {
            WinNT.IO_COUNTERS lpIoCounters = new WinNT.IO_COUNTERS();
            boolean b = Kernel32.INSTANCE.GetProcessIoCounters(h, lpIoCounters);
            assertTrue("Failed (" + Kernel32.INSTANCE.GetLastError() + ") to get process IO counters", b);
            // IO must be nonzero
            assertTrue(lpIoCounters.ReadOperationCount >= 0);
            assertTrue(lpIoCounters.WriteOperationCount >= 0);
            assertTrue(lpIoCounters.OtherOperationCount >= 0);
            assertTrue(lpIoCounters.ReadTransferCount >= 0);
            assertTrue(lpIoCounters.WriteTransferCount >= 0);
            assertTrue(lpIoCounters.OtherTransferCount >= 0);
        } finally {
            Kernel32Util.closeHandle(h);
        }
    }

    public void testGetAndSetProcessAffinityMask() {
        // Pseudo handle, no need to close. Has PROCESS_ALL_ACCESS right.
        HANDLE pHandle = Kernel32.INSTANCE.GetCurrentProcess();
        assertNotNull(pHandle);

        ULONG_PTRByReference pProcessAffinity = new ULONG_PTRByReference();
        ULONG_PTRByReference pSystemAffinity = new ULONG_PTRByReference();
        assertTrue("Failed to get affinity masks.",
                Kernel32.INSTANCE.GetProcessAffinityMask(pHandle, pProcessAffinity, pSystemAffinity));

        long processAffinity = pProcessAffinity.getValue().longValue();
        long systemAffinity = pSystemAffinity.getValue().longValue();

        if (systemAffinity == 0) {
            // Rare case for process to be running in multiple processor groups, where both
            // systemAffinity and processAffinity are 0 and we can't do anything else.
            assertEquals(
                    "Both process and system affinity must be zero if this process is running in multiple processor groups",
                    processAffinity, systemAffinity);
        } else {
            // Test current affinity
            assertEquals("Process affinity must be a subset of system affinity", processAffinity,
                    processAffinity & systemAffinity);
            assertEquals("System affinity must be a superset of process affinity", systemAffinity,
                    processAffinity | systemAffinity);

            // Set affinity to a single processor in the current system
            long lowestOneBit = Long.lowestOneBit(systemAffinity);
            ULONG_PTR dwProcessAffinityMask = new ULONG_PTR(lowestOneBit);
            assertTrue("Failed to set affinity",
                    Kernel32.INSTANCE.SetProcessAffinityMask(pHandle, dwProcessAffinityMask));
            assertTrue("Failed to get affinity masks.",
                    Kernel32.INSTANCE.GetProcessAffinityMask(pHandle, pProcessAffinity, pSystemAffinity));
            assertEquals("Process affinity doesn't match what was just set", lowestOneBit,
                    pProcessAffinity.getValue().longValue());

            // Now try to set affinity to an invalid processor
            lowestOneBit = Long.lowestOneBit(~systemAffinity);
            // In case we have exactly 64 processors we can't do this, otherwise...
            if (lowestOneBit != 0) {
                dwProcessAffinityMask = new ULONG_PTR(lowestOneBit);
                assertFalse("Successfully set affinity when it should have failed",
                        Kernel32.INSTANCE.SetProcessAffinityMask(pHandle, dwProcessAffinityMask));
                assertEquals("Last error should be ERROR_INVALID_PARAMETER", WinError.ERROR_INVALID_PARAMETER,
                        Kernel32.INSTANCE.GetLastError());
            }

            // Cleanup. Be nice and put affinity back where it started!
            dwProcessAffinityMask = new ULONG_PTR(processAffinity);
            assertTrue("Failed to restore affinity to original setting",
                    Kernel32.INSTANCE.SetProcessAffinityMask(pHandle, dwProcessAffinityMask));
        }
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

    public void testGetTickCount64() throws InterruptedException {
        long tick1 = Kernel32.INSTANCE.GetTickCount64();
        Thread.sleep(100);
        long tick2 = Kernel32.INSTANCE.GetTickCount64();

        assertTrue(tick2 > tick1);
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
        assertTrue(lpVersionInfo.getMajor() > 0);
        assertTrue(lpVersionInfo.getMinor() >= 0);
        assertEquals(lpVersionInfo.size(), lpVersionInfo.dwOSVersionInfoSize.longValue());
        assertTrue(lpVersionInfo.getPlatformId() > 0);
        assertTrue(lpVersionInfo.getBuildNumber() > 0);
        assertTrue(lpVersionInfo.getServicePack().length() >= 0);
        assertTrue(lpVersionInfo.getProductType() >= 0);
    }

    public void testGetSystemInfo() {
        SYSTEM_INFO lpSystemInfo = new SYSTEM_INFO();
        Kernel32.INSTANCE.GetSystemInfo(lpSystemInfo);
        assertTrue(lpSystemInfo.dwNumberOfProcessors.intValue() > 0);
        // the dwOemID member is obsolete, but gets a value.
        // the pi member is a structure and isn't read by default
        assertEquals(lpSystemInfo.processorArchitecture.dwOemID.getLow(),
                lpSystemInfo.processorArchitecture.pi.wProcessorArchitecture);
    }

    public void testGetSystemTimes() {
        Kernel32 kernel = Kernel32.INSTANCE;
        FILETIME lpIdleTime = new FILETIME();
        FILETIME lpKernelTime = new FILETIME();
        FILETIME lpUserTime = new FILETIME();
        boolean succ = kernel.GetSystemTimes(lpIdleTime, lpKernelTime, lpUserTime);
        assertTrue(succ);
        long idleTime = lpIdleTime.toDWordLong().longValue();
        long kernelTime = lpKernelTime.toDWordLong().longValue();
        long userTime = lpUserTime.toDWordLong().longValue();
        // All should be >= 0.  kernel includes idle.
        assertTrue(idleTime >= 0);
        assertTrue(kernelTime >= idleTime);
        assertTrue(userTime >= 0);
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
            Kernel32Util.closeHandle(hFile);
        }
    }

    public void testSetHandleInformation() throws IOException {
        File tmp = File.createTempFile("testSetHandleInformation", "jna");
        tmp.deleteOnExit();

        HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_READ, WinNT.FILE_SHARE_READ,
                new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));
        try {
            assertTrue(Kernel32.INSTANCE.SetHandleInformation(hFile, WinBase.HANDLE_FLAG_PROTECT_FROM_CLOSE, 0));
        } finally {
            Kernel32Util.closeHandle(hFile);
        }
    }

    public void testCreatePipe() {
        HANDLEByReference hReadPipe = new HANDLEByReference();
        HANDLEByReference hWritePipe = new HANDLEByReference();
        try {
            assertTrue(Kernel32.INSTANCE.CreatePipe(hReadPipe, hWritePipe, null, 0));
        } finally {
            Kernel32Util.closeHandleRefs(hReadPipe, hWritePipe);
        }
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
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));
        try {
            assertFalse(Kernel32.INSTANCE.TerminateProcess(hFile, 1));
            assertEquals(WinError.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
        } finally {
            Kernel32Util.closeHandle(hFile);
        }
    }

    public void testGetFileAttributes() {
        assertTrue(WinBase.INVALID_FILE_ATTRIBUTES != Kernel32.INSTANCE.GetFileAttributes("."));
    }

    public void testDeviceIoControlFsctlCompression() throws IOException {
        File tmp = File.createTempFile("testDeviceIoControlFsctlCompression", "jna");
        tmp.deleteOnExit();

        HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_ALL, WinNT.FILE_SHARE_READ,
                new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {
            ShortByReference lpBuffer = new ShortByReference();
            IntByReference lpBytes = new IntByReference();

            AbstractWin32TestSupport.assertCallSucceeded("DeviceIoControl",
                    Kernel32.INSTANCE.DeviceIoControl(hFile,
                    FSCTL_GET_COMPRESSION,
                    null,
                    0,
                    lpBuffer.getPointer(),
                    USHORT.SIZE,
                    lpBytes,
                    null));
            assertEquals(WinNT.COMPRESSION_FORMAT_NONE, lpBuffer.getValue());
            assertEquals(USHORT.SIZE, lpBytes.getValue());

            lpBuffer = new ShortByReference((short)WinNT.COMPRESSION_FORMAT_LZNT1);

            AbstractWin32TestSupport.assertCallSucceeded("DeviceIoControl",
                    Kernel32.INSTANCE.DeviceIoControl(hFile,
                    FSCTL_SET_COMPRESSION,
                    lpBuffer.getPointer(),
                    USHORT.SIZE,
                    null,
                    0,
                    lpBytes,
                    null));

            AbstractWin32TestSupport.assertCallSucceeded("DeviceIoControl",
                    Kernel32.INSTANCE.DeviceIoControl(hFile,
                    FSCTL_GET_COMPRESSION,
                    null,
                    0,
                    lpBuffer.getPointer(),
                    USHORT.SIZE,
                    lpBytes,
                    null));
            assertEquals(WinNT.COMPRESSION_FORMAT_LZNT1, lpBuffer.getValue());
            assertEquals(USHORT.SIZE, lpBytes.getValue());

        } finally {
            Kernel32Util.closeHandle(hFile);
        }
    }

    /**
     * NOTE: Due to process elevation, this test must be run as administrator
     * @throws IOException
     */
    public void testDeviceIoControlFsctlReparse() throws IOException {
        Path folder = Files.createTempDirectory("testDeviceIoControlFsctlReparse_FOLDER");
        Path link = Files.createTempDirectory("testDeviceIoControlFsctlReparse_LINK");
        File delFolder = folder.toFile();
        delFolder.deleteOnExit();
        File delLink = link.toFile();
        delLink.deleteOnExit();

        // Required for FSCTL_SET_REPARSE_POINT
        Advapi32Util.Privilege restore = new Advapi32Util.Privilege(WinNT.SE_RESTORE_NAME);
        try {
            restore.enable();
            HANDLE hFile = Kernel32.INSTANCE.CreateFile(link.toAbsolutePath().toString(),
                    WinNT.GENERIC_READ | WinNT.FILE_WRITE_ATTRIBUTES | WinNT.FILE_WRITE_EA,
                    WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE | WinNT.FILE_SHARE_DELETE,
                    new WinBase.SECURITY_ATTRIBUTES(),
                    WinNT.OPEN_EXISTING,
                    WinNT.FILE_ATTRIBUTE_DIRECTORY | WinNT.FILE_FLAG_BACKUP_SEMANTICS | WinNT.FILE_FLAG_OPEN_REPARSE_POINT,
                    null);

            AbstractWin32TestSupport.assertCallSucceeded("CreateFile",
                    !WinBase.INVALID_HANDLE_VALUE.equals(hFile));

            try {
                SymbolicLinkReparseBuffer symLinkReparseBuffer = new SymbolicLinkReparseBuffer(folder.getFileName().toString(),
                        folder.getFileName().toString(),
                        Ntifs.SYMLINK_FLAG_RELATIVE);

                REPARSE_DATA_BUFFER lpBuffer = new REPARSE_DATA_BUFFER(WinNT.IO_REPARSE_TAG_SYMLINK, (short) 0, symLinkReparseBuffer);

                AbstractWin32TestSupport.assertCallSucceeded("DeviceIoControl",
                        Kernel32.INSTANCE.DeviceIoControl(hFile,
                        FSCTL_SET_REPARSE_POINT,
                        lpBuffer.getPointer(),
                        lpBuffer.getSize(),
                        null,
                        0,
                        null,
                        null));

                Memory p = new Memory(REPARSE_DATA_BUFFER.sizeOf());
                IntByReference lpBytes = new IntByReference();
                AbstractWin32TestSupport.assertCallSucceeded("DeviceIoControl",
                        Kernel32.INSTANCE.DeviceIoControl(hFile,
                        FSCTL_GET_REPARSE_POINT,
                        null,
                        0,
                        p,
                        (int) p.size(),
                        lpBytes,
                        null));
                // Is a reparse point
                lpBuffer = new REPARSE_DATA_BUFFER(p);
                assertTrue(lpBytes.getValue() > 0);
                assertEquals(WinNT.IO_REPARSE_TAG_SYMLINK, lpBuffer.ReparseTag);
                assertEquals(folder.getFileName().toString(), lpBuffer.u.symLinkReparseBuffer.getPrintName());
                assertEquals(folder.getFileName().toString(), lpBuffer.u.symLinkReparseBuffer.getSubstituteName());
            } finally {
                Kernel32Util.closeHandle(hFile);
            }
        }
        finally {
            restore.close();
        }
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

    public void testFindFirstFile() throws IOException {
        Path tmpDir = Files.createTempDirectory("testFindFirstFile");
        File tmpFile = new File(Files.createTempFile(tmpDir, "testFindFirstFile", ".jna").toString());

        Memory p = new Memory(WIN32_FIND_DATA.sizeOf());
        HANDLE hFile = Kernel32.INSTANCE.FindFirstFile(tmpDir.toAbsolutePath().toString() + "\\*", p);
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {

            // Get data and confirm the 1st name is . for the directory itself.
            WIN32_FIND_DATA fd = new WIN32_FIND_DATA(p);
            String actualFileName = new String(fd.getFileName());
            assertTrue(actualFileName.contentEquals("."));

            // Get data and confirm the 2nd name is .. for the directory's parent
            assertTrue(Kernel32.INSTANCE.FindNextFile(hFile, p));
            fd = new WIN32_FIND_DATA(p);
            actualFileName = new String(fd.getFileName());
            assertTrue(actualFileName.contentEquals(".."));

            // Get data and confirm the 3rd name is the tmp file name
            assertTrue(Kernel32.INSTANCE.FindNextFile(hFile, p));
            fd = new WIN32_FIND_DATA(p);
            actualFileName = new String(fd.getFileName());
            assertTrue(actualFileName.contentEquals(tmpFile.getName()));

            // No more files in directory
            assertFalse(Kernel32.INSTANCE.FindNextFile(hFile, p));
            assertEquals(WinNT.ERROR_NO_MORE_FILES, Kernel32.INSTANCE.GetLastError());
        }
        finally {
            Kernel32.INSTANCE.FindClose(hFile);
            tmpFile.delete();
            Files.delete(tmpDir);
        }
    }

    public void testFindFirstFileExFindExInfoStandard() throws IOException {
        Path tmpDir = Files.createTempDirectory("testFindFirstFileExFindExInfoStandard");
        File tmpFile = new File(Files.createTempFile(tmpDir, "testFindFirstFileExFindExInfoStandard", ".jna").toString());

        Memory p = new Memory(WIN32_FIND_DATA.sizeOf());
        HANDLE hFile = Kernel32.INSTANCE.FindFirstFileEx(tmpDir.toAbsolutePath().toString() + "\\*",
                WinBase.FindExInfoStandard,
                p,
                WinBase.FindExSearchNameMatch,
                null,
                new DWORD(0));
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {

            // Get data and confirm the 1st name is . for the directory itself.
            WIN32_FIND_DATA fd = new WIN32_FIND_DATA(p);
            String actualFileName = new String(fd.getFileName());
            assertTrue(actualFileName.contentEquals("."));

            // Get data and confirm the 2nd name is .. for the directory's parent
            assertTrue(Kernel32.INSTANCE.FindNextFile(hFile, p));
            fd = new WIN32_FIND_DATA(p);
            actualFileName = new String(fd.getFileName());
            assertTrue(actualFileName.contentEquals(".."));

            // Get data and confirm the 3rd name is the tmp file name
            assertTrue(Kernel32.INSTANCE.FindNextFile(hFile, p));
            fd = new WIN32_FIND_DATA(p);
            actualFileName = new String(fd.getFileName());
            assertTrue(actualFileName.contentEquals(tmpFile.getName()));

            // No more files in directory
            assertFalse(Kernel32.INSTANCE.FindNextFile(hFile, p));
            assertEquals(WinNT.ERROR_NO_MORE_FILES, Kernel32.INSTANCE.GetLastError());
        }
        finally {
            Kernel32.INSTANCE.FindClose(hFile);
            tmpFile.delete();
            Files.delete(tmpDir);
        }
    }

    public void testFindFirstFileExFindExInfoBasic() throws IOException {
        Path tmpDir = Files.createTempDirectory("testFindFirstFileExFindExInfoBasic");
        File tmpFile = new File(Files.createTempFile(tmpDir, "testFindFirstFileExFindExInfoBasic", ".jna").toString());

        Memory p = new Memory(WIN32_FIND_DATA.sizeOf());
        // Add the file name to the search to get just that one entry
        HANDLE hFile = Kernel32.INSTANCE.FindFirstFileEx(tmpDir.toAbsolutePath().toString() + "\\" + tmpFile.getName(),
                WinBase.FindExInfoBasic,
                p,
                WinBase.FindExSearchNameMatch,
                null,
                new DWORD(0));
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {
            // Get data and confirm the 1st name is for the file itself
            WIN32_FIND_DATA fd = new WIN32_FIND_DATA(p);
            String actualFileName = new String(fd.getFileName());
            actualFileName = new String(fd.getFileName());
            assertTrue(actualFileName.contentEquals(tmpFile.getName()));

            // FindExInfoBasic does not return the short name, so confirm that its empty
            String alternateFileName = fd.getAlternateFileName();
            assertTrue(alternateFileName.isEmpty());

            // No more files in directory
            assertFalse(Kernel32.INSTANCE.FindNextFile(hFile, p));
            assertEquals(WinNT.ERROR_NO_MORE_FILES, Kernel32.INSTANCE.GetLastError());
        }
        finally {
            Kernel32.INSTANCE.FindClose(hFile);
            tmpFile.delete();
            Files.delete(tmpDir);
        }
    }

    public void testGetFileInformationByHandleEx() throws IOException {
        File tmp = File.createTempFile("testGetFileInformationByHandleEx", "jna");
        tmp.deleteOnExit();

        HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_WRITE, WinNT.FILE_SHARE_WRITE,
                new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {

            Memory p = new Memory(FILE_BASIC_INFO.sizeOf());
            AbstractWin32TestSupport.assertCallSucceeded("GetFileInformationByHandleEx for FileBasicInfo (" + p.size() + " bytes)",
                    Kernel32.INSTANCE.GetFileInformationByHandleEx(hFile, WinBase.FileBasicInfo, p, new DWORD(p.size())));
            FILE_BASIC_INFO fbi = new FILE_BASIC_INFO(p);
            // New file has non-zero creation time
            assertTrue(0 != fbi.CreationTime.getValue());

            p = new Memory(FILE_STANDARD_INFO.sizeOf());
            AbstractWin32TestSupport.assertCallSucceeded("GetFileInformationByHandleEx for FileStandardInfo (" + p.size() + " bytes)",
                    Kernel32.INSTANCE.GetFileInformationByHandleEx(hFile, WinBase.FileStandardInfo, p, new DWORD(p.size())));
            FILE_STANDARD_INFO fsi = new FILE_STANDARD_INFO(p);
            // New file has 1 link
            assertEquals(1, fsi.NumberOfLinks);

            p = new Memory(FILE_COMPRESSION_INFO.sizeOf());
            AbstractWin32TestSupport.assertCallSucceeded("GetFileInformationByHandleEx for FileCompressionInfo (" + p.size() + " bytes)",
                    Kernel32.INSTANCE.GetFileInformationByHandleEx(hFile, WinBase.FileCompressionInfo, p, new DWORD(p.size())));
            FILE_COMPRESSION_INFO fci = new FILE_COMPRESSION_INFO(p);
            // Uncompressed file should be zero
            assertEquals(0, fci.CompressionFormat);

            p = new Memory(FILE_ATTRIBUTE_TAG_INFO.sizeOf());
            AbstractWin32TestSupport.assertCallSucceeded("GetFileInformationByHandleEx for FileAttributeTagInfo (" + p.size() + " bytes)",
                    Kernel32.INSTANCE.GetFileInformationByHandleEx(hFile, WinBase.FileAttributeTagInfo, p, new DWORD(p.size())));
            FILE_ATTRIBUTE_TAG_INFO fati = new FILE_ATTRIBUTE_TAG_INFO(p);
            // New files have the archive bit
            assertEquals(WinNT.FILE_ATTRIBUTE_ARCHIVE, fati.FileAttributes & WinNT.FILE_ATTRIBUTE_ARCHIVE);

            if (VersionHelpers.IsWindowsServer() && VersionHelpers.IsWindows8OrGreater()) {
                p = new Memory(FILE_ID_INFO.sizeOf());
                AbstractWin32TestSupport.assertCallSucceeded("GetFileInformationByHandleEx for FileIdInfo (" + p.size() + " bytes)",
                        Kernel32.INSTANCE.GetFileInformationByHandleEx(hFile, WinBase.FileIdInfo, p, new DWORD(p.size())));
                FILE_ID_INFO fii = new FILE_ID_INFO(p);
                // Volume serial number should be non-zero
                assertFalse(fii.VolumeSerialNumber == 0);
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(hFile);
        }
    }

    public void testSetFileInformationByHandleFileBasicInfo() throws IOException, InterruptedException {
        File tmp = File.createTempFile("testSetFileInformationByHandleFileBasicInfo", "jna");
        tmp.deleteOnExit();

        HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(),
                WinNT.GENERIC_READ | WinNT.GENERIC_WRITE,
                WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE,
                new WinBase.SECURITY_ATTRIBUTES(),
                WinNT.OPEN_EXISTING,
                WinNT.FILE_ATTRIBUTE_NORMAL,
                null);

        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {
            Memory p = new Memory(FILE_BASIC_INFO.sizeOf());
            AbstractWin32TestSupport.assertCallSucceeded("GetFileInformationByHandleEx for FileBasicInfo (" + p.size() + " bytes)",
                    Kernel32.INSTANCE.GetFileInformationByHandleEx(hFile, WinBase.FileBasicInfo, p, new DWORD(p.size())));

            FILE_BASIC_INFO fbi = new FILE_BASIC_INFO(p);
            // Add TEMP attribute
            fbi.FileAttributes = fbi.FileAttributes | WinNT.FILE_ATTRIBUTE_TEMPORARY;
            fbi.ChangeTime = new WinNT.LARGE_INTEGER(0);
            fbi.CreationTime = new WinNT.LARGE_INTEGER(0);
            fbi.LastAccessTime = new WinNT.LARGE_INTEGER(0);
            fbi.LastWriteTime = new WinNT.LARGE_INTEGER(0);
            fbi.write();

            AbstractWin32TestSupport.assertCallSucceeded("SetFileInformationByHandle for FileBasicInfo (" + FILE_BASIC_INFO.sizeOf() + " bytes)",
                    Kernel32.INSTANCE.SetFileInformationByHandle(hFile, WinBase.FileBasicInfo, fbi.getPointer(), new DWORD(FILE_BASIC_INFO.sizeOf())));

            AbstractWin32TestSupport.assertCallSucceeded("GetFileInformationByHandleEx for FileBasicInfo (" + p.size() + " bytes)",
                    Kernel32.INSTANCE.GetFileInformationByHandleEx(hFile, WinBase.FileBasicInfo, p, new DWORD(p.size())));

            fbi = new FILE_BASIC_INFO(p);
            assertTrue((fbi.FileAttributes & WinNT.FILE_ATTRIBUTE_TEMPORARY) != 0);
        }
        finally {
            Kernel32.INSTANCE.CloseHandle(hFile);
        }
    }

    public void testSetFileInformationByHandleFileDispositionInfo() throws IOException, InterruptedException {
        File tmp = File.createTempFile("testSetFileInformationByHandleFileDispositionInfo", "jna");

        HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_WRITE | WinNT.DELETE, WinNT.FILE_SHARE_WRITE,
                new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);

        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {
            FILE_DISPOSITION_INFO fdi = new FILE_DISPOSITION_INFO(true);
            AbstractWin32TestSupport.assertCallSucceeded("SetFileInformationByHandle for FileDispositionInfo (" + FILE_DISPOSITION_INFO.sizeOf() + " bytes)",
                    Kernel32.INSTANCE.SetFileInformationByHandle(hFile, WinBase.FileDispositionInfo, fdi.getPointer(), new DWORD(FILE_DISPOSITION_INFO.sizeOf())));

        } finally {
            Kernel32.INSTANCE.CloseHandle(hFile);
        }

        assertFalse(Files.exists(Paths.get(tmp.getAbsolutePath())));
    }

    public void testGetSetFileTime() throws IOException {
        File tmp = File.createTempFile("testGetSetFileTime", "jna");
        tmp.deleteOnExit();

        HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_WRITE, WinNT.FILE_SHARE_WRITE,
                new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));
        try {
            WinBase.FILETIME.ByReference creationTime = new WinBase.FILETIME.ByReference();
            WinBase.FILETIME.ByReference accessTime = new WinBase.FILETIME.ByReference();
            WinBase.FILETIME.ByReference modifiedTime = new WinBase.FILETIME.ByReference();
            Kernel32.INSTANCE.GetFileTime(hFile, creationTime, accessTime, modifiedTime);

            assertEquals(creationTime.toDate().getYear(), new Date().getYear());
            assertEquals(accessTime.toDate().getYear(), new Date().getYear());
            assertEquals(modifiedTime.toDate().getYear(), new Date().getYear());

            Kernel32.INSTANCE.SetFileTime(hFile, null, null, new WinBase.FILETIME(new Date(2010, 1, 1)));
            assertEquals(2010, new Date(tmp.lastModified()).getYear());
        } finally {
            Kernel32Util.closeHandle(hFile);
        }
    }

    public void testSetFileAttributes() throws IOException {
        File tmp = File.createTempFile("testSetFileAttributes", "jna");
        tmp.deleteOnExit();

        Kernel32.INSTANCE.SetFileAttributes(tmp.getCanonicalPath(), new DWORD(WinNT.FILE_ATTRIBUTE_HIDDEN));
        int attributes = Kernel32.INSTANCE.GetFileAttributes(tmp.getCanonicalPath());

        assertTrue((attributes & WinNT.FILE_ATTRIBUTE_HIDDEN) != 0);
    }

    public void testGetProcessList() throws IOException {
        HANDLE processEnumHandle = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPALL, new WinDef.DWORD(0));
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(processEnumHandle));
        try {
            Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();

            assertTrue(Kernel32.INSTANCE.Process32First(processEnumHandle, processEntry));

            List<Long> processIdList = new ArrayList<Long>();
            processIdList.add(processEntry.th32ProcessID.longValue());

            while (Kernel32.INSTANCE.Process32Next(processEnumHandle, processEntry)) {
                processIdList.add(processEntry.th32ProcessID.longValue());
            }

            assertTrue(processIdList.size() > 4);
        } finally {
            Kernel32Util.closeHandle(processEnumHandle);
        }
    }

    public void testGetThreadList() throws IOException {
        HANDLE threadEnumHandle = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPTHREAD,
                new WinDef.DWORD(0));
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(threadEnumHandle));
        try {
            Tlhelp32.THREADENTRY32.ByReference threadEntry = new Tlhelp32.THREADENTRY32.ByReference();

            assertTrue(Kernel32.INSTANCE.Thread32First(threadEnumHandle, threadEntry));

            List<Integer> threadIdList = new ArrayList<Integer>();
            threadIdList.add(threadEntry.th32ThreadID);

            while (Kernel32.INSTANCE.Thread32Next(threadEnumHandle, threadEntry)) {
                threadIdList.add(threadEntry.th32ThreadID);
            }

            assertTrue(threadIdList.size() > 4);
        } finally {
            Kernel32Util.closeHandle(threadEnumHandle);
        }
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

    /**
     * Test both SystemTimeToFileTime and FileTimeToSystemTime
     * @throws IOException
     */
    public final void testSystemTimeToFileTimeAndFileTimeToSystemTime() throws IOException {

        WinBase.SYSTEMTIME systemTime = new WinBase.SYSTEMTIME();
        Kernel32.INSTANCE.GetSystemTime(systemTime);
        WinBase.FILETIME fileTime = new WinBase.FILETIME();

        AbstractWin32TestSupport.assertCallSucceeded("SystemTimeToFileTime",
                Kernel32.INSTANCE.SystemTimeToFileTime(systemTime, fileTime));

        WinBase.SYSTEMTIME newSystemTime = new WinBase.SYSTEMTIME();
        AbstractWin32TestSupport.assertCallSucceeded("FileTimeToSystemTime",
                Kernel32.INSTANCE.FileTimeToSystemTime(fileTime, newSystemTime));

        assertEquals(systemTime.wYear, newSystemTime.wYear);
        assertEquals(systemTime.wDay, newSystemTime.wDay);
        assertEquals(systemTime.wMonth, newSystemTime.wMonth);
        assertEquals(systemTime.wHour, newSystemTime.wHour);
        assertEquals(systemTime.wMinute, newSystemTime.wMinute);
        assertEquals(systemTime.wSecond, newSystemTime.wSecond);
        assertEquals(systemTime.wMilliseconds, newSystemTime.wMilliseconds);
    }

    /**
     * Test FILETIME's LARGE_INTEGER constructor
     * @throws IOException
     */
    public final void testFileTimeFromLargeInteger() throws IOException {

        File tmp = File.createTempFile("testGetFileInformationByHandleEx", "jna");
        tmp.deleteOnExit();

        HANDLE hFile = Kernel32.INSTANCE.CreateFile(tmp.getAbsolutePath(), WinNT.GENERIC_WRITE, WinNT.FILE_SHARE_WRITE,
                new WinBase.SECURITY_ATTRIBUTES(), WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
        assertFalse(WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {

            Memory p = new Memory(FILE_BASIC_INFO.sizeOf());
            AbstractWin32TestSupport.assertCallSucceeded("GetFileInformationByHandleEx for FileBasicInfo (" + p.size() + " bytes)",
                    Kernel32.INSTANCE.GetFileInformationByHandleEx(hFile, WinBase.FileBasicInfo, p, new DWORD(p.size())));
            FILE_BASIC_INFO fbi = new FILE_BASIC_INFO(p);
            FILETIME ft = new FILETIME(fbi.LastWriteTime);
            SYSTEMTIME stUTC = new SYSTEMTIME();
            SYSTEMTIME stLocal = new SYSTEMTIME();
            Kernel32.INSTANCE.FileTimeToSystemTime(ft, stUTC);
            // Covert to local
            Kernel32.INSTANCE.SystemTimeToTzSpecificLocalTime(null, stUTC, stLocal);
            FileTime calculatedCreateTime = FileTime.fromMillis(stLocal.toCalendar().getTimeInMillis());

            // Actual file's createTime
            FileTime createTime = Files.getLastModifiedTime(Paths.get(tmp.getAbsolutePath()));

            assertEquals(createTime.toMillis(), calculatedCreateTime.toMillis());
        }
        finally {
            Kernel32.INSTANCE.CloseHandle(hFile);
        }
    }

    public final void testCreateRemoteThreadInvalid() throws IOException {
        HANDLE hThrd = Kernel32.INSTANCE.CreateRemoteThread(null, null, 0, (Pointer) null, null, 0, null);
        assertNull(hThrd);
        assertEquals(Kernel32.INSTANCE.GetLastError(), WinError.ERROR_INVALID_HANDLE);
    }

    @Test
    public void testCreateRemoteThread() {
        Assume.assumeTrue("testCreateRemoteThread is only implemented for x86 + x86-64", Platform.isIntel());

        Pointer addr = Kernel32.INSTANCE.VirtualAllocEx(
            Kernel32.INSTANCE.GetCurrentProcess(), null, new SIZE_T(4096),
            MEM_COMMIT | MEM_RESERVE, PAGE_EXECUTE_READWRITE);

        Memory localBuffer = new Memory(4096);
        localBuffer.clear();

        if (Platform.is64Bit()) {
            // mov eax, ecx; ret; int3
            localBuffer.setByte(0, (byte) 0x8b); // MOV
            localBuffer.setByte(1, (byte) 0xc1); // ECX -> EAX
            localBuffer.setByte(2, (byte) 0xc3); // RET
            localBuffer.setByte(3, (byte) 0xcc); // INT3
        } else {
            // mov eax, esp + 4; ret; int3
            localBuffer.setByte(0, (byte) 0x8b); // MOV
            localBuffer.setByte(1, (byte) 0x44); // ESP + 4bytes-> EAX
            localBuffer.setByte(2, (byte) 0x24); //
            localBuffer.setByte(3, (byte) 0x04); //
            localBuffer.setByte(4, (byte) 0xc3); // RET
            localBuffer.setByte(5, (byte) 0xcc); // INT3
        }

        IntByReference bytesWritten = new IntByReference();
        Kernel32.INSTANCE.WriteProcessMemory(Kernel32.INSTANCE.GetCurrentProcess(),
            addr, localBuffer, 4096, bytesWritten);
        assertEquals(4096, bytesWritten.getValue());

        DWORDByReference threadId = new DWORDByReference();
        HANDLE hThread = Kernel32.INSTANCE.CreateRemoteThread(
            Kernel32.INSTANCE.GetCurrentProcess(),
            null, 0, addr, new Pointer(12345), 0, threadId);
        assertNotNull(hThread);
        assertTrue(threadId.getValue().longValue() > 0);

        int waitResult = Kernel32.INSTANCE.WaitForSingleObject(hThread, 10000);
        assertEquals(WAIT_OBJECT_0, waitResult);

        IntByReference exitCode = new IntByReference();
        boolean exitResult = Kernel32.INSTANCE.GetExitCodeThread(hThread, exitCode);
        assertTrue(exitResult);
        assertEquals(12345, exitCode.getValue());

        assertTrue(Kernel32.INSTANCE.VirtualFreeEx(Kernel32.INSTANCE.GetCurrentProcess(),
            addr, new SIZE_T(0), WinNT.MEM_RELEASE));
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
                Kernel32Util.closeHandle(handleSerialPort);
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
                Kernel32Util.closeHandle(handleSerialPort);
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
                Kernel32Util.closeHandle(handleSerialPort);
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
                Kernel32Util.closeHandle(handleSerialPort);
            }
        }
    }

    public void testProcessIdToSessionId() {
        int myProcessID = Kernel32.INSTANCE.GetCurrentProcessId();

        IntByReference pSessionId = new IntByReference();
        boolean result = Kernel32.INSTANCE.ProcessIdToSessionId(myProcessID, pSessionId);

        // should give us our session ID
        assertTrue("ProcessIdToSessionId should return true.", result);

        // on Win Vista and later we'll never be session 0
        // due to service isolation
        // anything negative would be a definite error.
        assertTrue("Session should be 1 or higher because of service isolation", pSessionId.getValue() > 0);
    }

    public void testLoadLibraryEx() {
        String winDir = Kernel32Util.getEnvironmentVariable("WINDIR");
        assertNotNull("No WINDIR value returned", winDir);
        assertTrue("Specified WINDIR does not exist: " + winDir, new File(winDir).exists());

        HMODULE hModule = null;
        try {
            hModule = Kernel32.INSTANCE.LoadLibraryEx(new File(winDir, "explorer.exe").getAbsolutePath(), null,
                    Kernel32.LOAD_LIBRARY_AS_DATAFILE);
            if (hModule == null) {
                throw new Win32Exception(Native.getLastError());
            }
            assertNotNull("hModule should not be null.", hModule);
        } finally {
            if (hModule != null) {
                if (!Kernel32.INSTANCE.FreeLibrary(hModule)) {
                    throw new Win32Exception(Native.getLastError());
                }
            }
        }
    }

    public void testEnumResourceNames() {
        // "14" is the type name of the My Computer icon in explorer.exe
        Pointer pointer = new Memory(Native.WCHAR_SIZE * 3);
        pointer.setWideString(0, "14");
        WinBase.EnumResNameProc ernp = new WinBase.EnumResNameProc() {

            @Override
            public boolean invoke(HMODULE module, Pointer type, Pointer name, Pointer lParam) {

                return true;
            }
        };
        // null HMODULE means use this process / its EXE
        // there are no type "14" resources in it.
        boolean result = Kernel32.INSTANCE.EnumResourceNames(null, pointer, ernp, null);
        assertFalse("EnumResourceNames should have failed.", result);
        assertEquals("GetLastError should be set to 1813", WinError.ERROR_RESOURCE_TYPE_NOT_FOUND, Kernel32.INSTANCE.GetLastError());
    }


    public void testEnumResourceTypes() {
        final List<String> types = new ArrayList<String>();
        WinBase.EnumResTypeProc ertp = new WinBase.EnumResTypeProc() {

            @Override
            public boolean invoke(HMODULE module, Pointer type, Pointer lParam) {
                // simulate IS_INTRESOURCE macro defined in WinUser.h
                // basically that means that if "type" is less than or equal to 65,535
                // it assumes it's an ID.
                // otherwise it assumes it's a pointer to a string
                if (Pointer.nativeValue(type) <= 65535) {
                    types.add(Pointer.nativeValue(type) + "");
                } else {
                    types.add(type.getWideString(0));
                }
                return true;
            }
        };
        // null HMODULE means use this process / its EXE
        // there are no type "14" resources in it.
        boolean result = Kernel32.INSTANCE.EnumResourceTypes(null, ertp, null);
        assertTrue("EnumResourceTypes should not have failed.", result);
        assertEquals("GetLastError should be set to 0", WinError.ERROR_SUCCESS, Kernel32.INSTANCE.GetLastError());
        assertTrue("EnumResourceTypes should return some resource type names", types.size() > 0);
    }

    public void testModule32FirstW() {
        HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new DWORD(Kernel32.INSTANCE.GetCurrentProcessId()));
        if (snapshot == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        Win32Exception we = null;
        Tlhelp32.MODULEENTRY32W first = new Tlhelp32.MODULEENTRY32W();
        try {
            if (!Kernel32.INSTANCE.Module32FirstW(snapshot, first)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }


            // not sure if this will be run against java.exe or javaw.exe but this
            // check tests both
            assertTrue("The first module in the current process should be java.exe or javaw.exe", first.szModule().startsWith("java"));
            assertEquals("The process ID of the module ID should be our process ID", Kernel32.INSTANCE.GetCurrentProcessId(), first.th32ProcessID.intValue());
        } catch (Win32Exception e) {
            we = e;
            throw we;   // re-throw so finally block is executed
        } finally {
            try {
                Kernel32Util.closeHandle(snapshot);
            } catch(Win32Exception e) {
                if (we == null) {
                    we = e;
                } else {
                    we.addSuppressedReflected(e);
                }
            }

            if (we != null) {
                throw we;
            }
        }
    }

    public void testModule32NextW() {
        HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new DWORD(Kernel32.INSTANCE.GetCurrentProcessId()));
        if (snapshot == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        Win32Exception we = null;
        Tlhelp32.MODULEENTRY32W first = new Tlhelp32.MODULEENTRY32W();
        try {
            if (!Kernel32.INSTANCE.Module32NextW(snapshot, first)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            // not sure if this will be run against java.exe or javaw.exe but this
            // check tests both
            assertTrue("The first module in the current process should be java.exe or javaw.exe", first.szModule().startsWith("java"));
            assertEquals("The process ID of the module ID should be our process ID", Kernel32.INSTANCE.GetCurrentProcessId(), first.th32ProcessID.intValue());
        } catch (Win32Exception e) {
            we = e;
            throw we;   // re-throw so finally block is executed
        } finally {
            try {
                Kernel32Util.closeHandle(snapshot);
            } catch(Win32Exception e) {
                if (we == null) {
                    we = e;
                } else {
                    we.addSuppressedReflected(e);
                }
            }

            if (we != null) {
                throw we;
            }
        }
    }

    public void testSetErrorMode() {
        // Set bit flags to 0x0001
        int previousMode = Kernel32.INSTANCE.SetErrorMode(0x0001);
        // Restore to previous state; 0x0001 is now "previous"
        // Since 0x0004 bit is sticky previous may be 0x0005
        assertEquals(0x0001, Kernel32.INSTANCE.SetErrorMode(previousMode) & ~0x0004);
    }

// Testcase is disabled, as kernel32 ordinal values are not stable.
// a library with a stable function <-> ordinal value is needed.
//
//    /**
//     * Test that a named function on win32 can be equally resolved by its ordinal
//     * value.
//     *
//     * From link.exe /dump /exports c:\\Windows\\System32\\kernel32.dll
//     *
//     *  746  2E9 0004FA20 GetTapeStatus
//     *  747  2EA 0002DB20 GetTempFileNameA
//     *  748  2EB 0002DB30 GetTempFileNameW
//     *  749  2EC 0002DB40 GetTempPathA
//     *  750  2ED 0002DB50 GetTempPathW
//     *  751  2EE 00026780 GetThreadContext
//     *
//     * The tested function is GetTempPathW which is mapped to the ordinal 750.
//     */
//    public void testGetProcAddress() {
//        NativeLibrary kernel32Library = NativeLibrary.getInstance("kernel32");
//        // get module handle needed to resolve function pointer via GetProcAddress
//        HMODULE kernel32Module = Kernel32.INSTANCE.GetModuleHandle("kernel32");
//
//        Function namedFunction = kernel32Library.getFunction("GetTempPathW");
//        long namedFunctionPointerValue = Pointer.nativeValue(namedFunction);
//
//        Pointer ordinalFunction = Kernel32.INSTANCE.GetProcAddress(kernel32Module, 750);
//        long ordinalFunctionPointerValue = Pointer.nativeValue(ordinalFunction);
//
//        assertEquals(namedFunctionPointerValue, ordinalFunctionPointerValue);
//    }

    public void testSetThreadExecutionState() {
        int originalExecutionState = Kernel32.INSTANCE.SetThreadExecutionState(
                WinBase.ES_CONTINUOUS | WinBase.ES_SYSTEM_REQUIRED | WinBase.ES_AWAYMODE_REQUIRED
        );

        assert originalExecutionState > 0;

        int intermediateExecutionState = Kernel32.INSTANCE.SetThreadExecutionState(
                WinBase.ES_CONTINUOUS
        );

        assertEquals(WinBase.ES_CONTINUOUS | WinBase.ES_SYSTEM_REQUIRED | WinBase.ES_AWAYMODE_REQUIRED, intermediateExecutionState);

        Kernel32.INSTANCE.SetThreadExecutionState(originalExecutionState);
    }

    public void testMutex() throws InterruptedException {
        HANDLE mutexHandle = Kernel32.INSTANCE.CreateMutex(null, true, "JNA-Test-Mutex");

        assertNotNull(mutexHandle);

        final CountDownLatch preWait = new CountDownLatch(1);
        final CountDownLatch postWait = new CountDownLatch(1);
        final CountDownLatch postRelease = new CountDownLatch(1);

        final Exception[] exceptions = new Exception[1];
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    HANDLE mutexHandle2 = Kernel32.INSTANCE.OpenMutex(WinNT.SYNCHRONIZE, false, "JNA-Test-Mutex");
                    try {
                        assertNotNull(mutexHandle2);
                        preWait.countDown();
                        int result = Kernel32.INSTANCE.WaitForSingleObject(mutexHandle2, WinBase.INFINITE);
                        assertEquals(result, WinBase.WAIT_OBJECT_0);
                        postWait.countDown();
                    } finally {
                        Kernel32.INSTANCE.ReleaseMutex(mutexHandle2);
                        Kernel32.INSTANCE.CloseHandle(mutexHandle2);
                        postRelease.countDown();
                    }
                } catch (Exception ex) {
                    exceptions[0] = ex;
                }
            }
        };

        t.start();

        assertTrue(preWait.await(2, TimeUnit.SECONDS));

        Kernel32.INSTANCE.ReleaseMutex(mutexHandle);

        assertTrue(postWait.await(2, TimeUnit.SECONDS));

        Kernel32.INSTANCE.CloseHandle(mutexHandle);

        assertTrue(postRelease.await(2, TimeUnit.SECONDS));

        assertNull(exceptions[0]);

        mutexHandle = Kernel32.INSTANCE.OpenMutex(WinNT.SYNCHRONIZE, false, "JNA-Test-Mutex");

        assertNull(mutexHandle);
    }

    public void testApplicationRestart() {
        try {
            HRESULT insufficientBuffer = W32Errors.HRESULT_FROM_WIN32(WinError.ERROR_INSUFFICIENT_BUFFER);
            HRESULT result;
            String dummyCommandline = "/restart -f .\\filename.ext";
            char[] dummyCommandlineArray = Native.toCharArray(dummyCommandline);
            int dummyFlags = 2;
            result = Kernel32.INSTANCE.RegisterApplicationRestart(dummyCommandlineArray, dummyFlags);
            assertTrue(COMUtils.SUCCEEDED(result));

            char[] queriedCommandlineArray = null;
            IntByReference queriedSize = new IntByReference();
            IntByReference queriedFlags = new IntByReference();

            // Query without target buffer to determine required buffer size
            result = Kernel32.INSTANCE.GetApplicationRestartSettings(Kernel32.INSTANCE.GetCurrentProcess(), queriedCommandlineArray, queriedSize, queriedFlags);

            assertTrue(COMUtils.SUCCEEDED(result));
            assertEquals(dummyCommandline.length() + 1, queriedSize.getValue());

            // Check error reporting, use insufficient buffer size
            queriedCommandlineArray = new char[1];
            queriedSize.setValue(queriedCommandlineArray.length);
            queriedFlags.setValue(-1);
            result = Kernel32.INSTANCE.GetApplicationRestartSettings(Kernel32.INSTANCE.GetCurrentProcess(), queriedCommandlineArray, queriedSize, queriedFlags);

            assertTrue(COMUtils.FAILED(result));
            assertEquals(insufficientBuffer, result);
            assertEquals(dummyCommandline.length() + 1, queriedSize.getValue());

            // Now query with the right buffer size
            queriedCommandlineArray = new char[queriedSize.getValue()];
            queriedSize.setValue(queriedCommandlineArray.length);
            queriedFlags.setValue(-1);
            result = Kernel32.INSTANCE.GetApplicationRestartSettings(Kernel32.INSTANCE.GetCurrentProcess(), queriedCommandlineArray, queriedSize, queriedFlags);

            assertTrue(COMUtils.SUCCEEDED(result));
            assertEquals(dummyCommandline.length() + 1, queriedSize.getValue());
            assertEquals(dummyFlags, queriedFlags.getValue());
            assertEquals(dummyCommandline, Native.toString(queriedCommandlineArray));

            result = Kernel32.INSTANCE.UnregisterApplicationRestart();
            assertTrue(COMUtils.SUCCEEDED(result));
        } finally {
            // Last resort if test succeeds partially
            Kernel32.INSTANCE.UnregisterApplicationRestart();
        }
    }

    public void testCreateFileMapping() throws IOException {
        // Test creating an unnamed file mapping. The test creates a file, that
        // containes "Hello World" followed by a NULL byte. The file is mapped
        // into memory and read with the normal pointer getString funtion to
        // read it as a NULL terminated string.

        String testString = "Hello World";
        File testFile = File.createTempFile("jna-test", ".txt");

        try {
            OutputStream os = new FileOutputStream(testFile);
            try {
                os.write(testString.getBytes("UTF-8"));
                os.write(0);
            } finally {
                os.close();
            }

            SYSTEM_INFO lpSystemInfo = new SYSTEM_INFO();
            Kernel32.INSTANCE.GetSystemInfo(lpSystemInfo);

            HANDLE fileHandle = Kernel32.INSTANCE.CreateFile(
                    testFile.getAbsolutePath(),
                    WinNT.GENERIC_READ | WinNT.GENERIC_WRITE,
                    0,
                    null,
                    WinNT.OPEN_EXISTING,
                    WinNT.FILE_ATTRIBUTE_NORMAL,
                    null);

            assertNotNull(fileHandle);

            HANDLE fileMappingHandle = Kernel32.INSTANCE.CreateFileMapping(
                    fileHandle,
                    null,
                    WinNT.PAGE_READWRITE,
                    0,
                    lpSystemInfo.dwAllocationGranularity.intValue(),
                    null);

            assertNotNull(fileMappingHandle);

            Pointer mappingAddress = Kernel32.INSTANCE.MapViewOfFile(
                    fileMappingHandle,
                    WinNT.FILE_MAP_ALL_ACCESS,
                    0,
                    0,
                    lpSystemInfo.dwAllocationGranularity.intValue());

            assertNotNull(mappingAddress);

            assertEquals(testString, mappingAddress.getString(0, "UTF-8"));

            assertTrue(Kernel32.INSTANCE.UnmapViewOfFile(mappingAddress));

            assertTrue(Kernel32.INSTANCE.CloseHandle(fileMappingHandle));

            assertTrue(Kernel32.INSTANCE.CloseHandle(fileHandle));
        } finally {
            testFile.delete();
        }
    }

    public void testOpenFileMapping() throws IOException {
        // Test creating an named file mapping. The test creates a file mapping
        // backed by the paging paging file. A second Mapping is opened from
        // that. The first mapping is used to write a test string and the
        // second is used to read it.

        int mappingSize = 256;
        String nameOfMapping = "JNATestMapping";
        String testString = "Hello World";

        HANDLE fileMappingHandle1 = Kernel32.INSTANCE.CreateFileMapping(
                WinNT.INVALID_HANDLE_VALUE,
                null,
                WinNT.PAGE_READWRITE,
                0,
                mappingSize,
                nameOfMapping);

        assertNotNull("Error: " + Kernel32.INSTANCE.GetLastError(), fileMappingHandle1);

        HANDLE fileMappingHandle2 = Kernel32.INSTANCE.OpenFileMapping(
                WinNT.FILE_MAP_ALL_ACCESS,
                false,
                nameOfMapping);

        assertNotNull(fileMappingHandle1);

        Pointer mappingAddress1 = Kernel32.INSTANCE.MapViewOfFile(
                fileMappingHandle1,
                WinNT.FILE_MAP_ALL_ACCESS,
                0,
                0,
                mappingSize);

        assertNotNull(mappingAddress1);

        Pointer mappingAddress2 = Kernel32.INSTANCE.MapViewOfFile(
                fileMappingHandle2,
                WinNT.FILE_MAP_ALL_ACCESS,
                0,
                0,
                mappingSize);

        assertNotNull(mappingAddress2);

        mappingAddress1.setString(0, testString, "UTF-8");

        assertEquals(testString, mappingAddress2.getString(0, "UTF-8"));

        assertTrue(Kernel32.INSTANCE.UnmapViewOfFile(mappingAddress1));

        assertTrue(Kernel32.INSTANCE.UnmapViewOfFile(mappingAddress2));

        assertTrue(Kernel32.INSTANCE.CloseHandle(fileMappingHandle1));

        assertTrue(Kernel32.INSTANCE.CloseHandle(fileMappingHandle2));

    }

    public void testVirtualLockUnlock() {
        Memory mem = new Memory(4096);
        // Test that locking works
        assertTrue(Kernel32.INSTANCE.VirtualLock(mem, new SIZE_T(4096)));
        // Test that unlocked region can be unlocked
        assertTrue(Kernel32.INSTANCE.VirtualUnlock(mem, new SIZE_T(4096)));
        // Locking a region we don't have access to should fail
        assertFalse(Kernel32.INSTANCE.VirtualLock(null, new SIZE_T(4096)));
        // Unlocking an unlocked region should fail
        assertFalse(Kernel32.INSTANCE.VirtualUnlock(mem, new SIZE_T(4096)));
    }

    public void testGetPriorityClass() {
        final HANDLE selfHandle = Kernel32.INSTANCE.GetCurrentProcess();
        assertTrue(Kernel32Util.isValidPriorityClass(Kernel32.INSTANCE.GetPriorityClass(selfHandle)));
    }

    public void testSetPriorityClass() {
        final HANDLE selfHandle = Kernel32.INSTANCE.GetCurrentProcess();
        assertTrue(Kernel32.INSTANCE.SetPriorityClass(selfHandle, Kernel32.HIGH_PRIORITY_CLASS));
    }

    public void testGetThreadPriority() {
        final HANDLE selfHandle = Kernel32.INSTANCE.GetCurrentThread();
        assertTrue(Kernel32Util.isValidThreadPriority(Kernel32.INSTANCE.GetThreadPriority(selfHandle)));
    }

    public void testSetThreadPriority() {
        final HANDLE selfHandle = Kernel32.INSTANCE.GetCurrentThread();
        assertTrue(Kernel32.INSTANCE.SetThreadPriority(selfHandle, Kernel32.THREAD_PRIORITY_ABOVE_NORMAL));
    }
}
