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
import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.WinBase.MEMORYSTATUSEX;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
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
    	assertEquals(W32Errors.ERROR_BUFFER_OVERFLOW, Kernel32.INSTANCE.GetLastError());
    	char buffer[] = new char[WinBase.MAX_COMPUTERNAME_LENGTH() + 1];
    	lpnSize.setValue(buffer.length);
    	assertTrue(Kernel32.INSTANCE.GetComputerName(buffer, lpnSize));
    }

    public void testWaitForSingleObject() {
		HANDLE handle = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
		
		// handle runs into timeout since it is not triggered
		// WAIT_TIMEOUT = 0x00000102 
		assertEquals(W32Errors.WAIT_TIMEOUT, Kernel32.INSTANCE.WaitForSingleObject(
				handle, 1000));
		
		Kernel32.INSTANCE.CloseHandle(handle);
	}
    
    public void testWaitForMultipleObjects(){    	
    	HANDLE[] handles = new HANDLE[2];
    	
		handles[0] = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
		handles[1] = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
		
		// handle runs into timeout since it is not triggered
		// WAIT_TIMEOUT = 0x00000102
		assertEquals(W32Errors.WAIT_TIMEOUT, Kernel32.INSTANCE.WaitForMultipleObjects(
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
    	assertEquals(W32Errors.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
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
    	assertEquals(W32Errors.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
    }    
    
    public void testOpenProcess() {
    	HANDLE h = Kernel32.INSTANCE.OpenProcess(0, false, 
    			Kernel32.INSTANCE.GetCurrentProcessId());
    	assertNull(h);
    	// opening your own process fails with access denied
    	assertEquals(W32Errors.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());
    }
    
    public void testGetTempPath() {
    	char[] buffer = new char[WinDef.MAX_PATH]; 
    	assertTrue(Kernel32.INSTANCE.GetTempPath(new DWORD(WinDef.MAX_PATH), buffer).intValue() > 0);    	
    }
    
    public void testGetVersion() {
    	DWORD version = Kernel32.INSTANCE.GetVersion();
    	assertTrue(version.getHigh().intValue() != 0);
    	assertTrue(version.getLow().intValue() >= 0);
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
}
