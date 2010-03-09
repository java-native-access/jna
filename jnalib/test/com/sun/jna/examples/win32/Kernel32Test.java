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
package com.sun.jna.examples.win32;

import java.util.Calendar;
import java.util.TimeZone;
import junit.framework.TestCase;
import com.sun.jna.*;
import com.sun.jna.examples.win32.*;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class Kernel32Test extends TestCase {
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(Kernel32Test.class);
    }
    
    public void testGetDriveType() {
        if (!Platform.isWindows()) return;
        
        Kernel32 kernel = Kernel32.INSTANCE;
        assertEquals("Wrong drive type.", Kernel32.DRIVE_FIXED, kernel.GetDriveType("c:"));
    }
    
    public void testStructureOutArgument() {
        Kernel32 kernel = Kernel32.INSTANCE;
        Kernel32.SYSTEMTIME time = new Kernel32.SYSTEMTIME();
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
        W32API.HWND hwnd = W32API.HWND_BROADCAST;
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
		handles[0] = W32API.INVALID_HANDLE_VALUE;
		handles[1] = Kernel32.INSTANCE.CreateEvent(null, false, false, null);
		
		// returns WAIT_FAILED since handle is invalid
		assertEquals(WinBase.WAIT_FAILED, Kernel32.INSTANCE.WaitForMultipleObjects(
				handles.length, handles, false, 5000));

		Kernel32.INSTANCE.CloseHandle(handles[1]);
    }   
}
