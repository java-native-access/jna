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
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import junit.framework.TestCase;

public class Kernel32Test extends TestCase {
    
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

    public static interface Advapi32 extends W32API {
        Advapi32 INSTANCE = (Advapi32)Native.loadLibrary("advapi32", Advapi32.class, DEFAULT_OPTIONS);
        Pointer OpenSCManager(String lpMachineName, String lpDatabaseName, int dwDesiredAccess);
    }
    
    public void testGetLastError() {
        Kernel32 kernel = Kernel32.INSTANCE;
        kernel.GetLastError();
        if (kernel.GetProcessId(null) == 0) {
            final int INVALID_HANDLE = 6;
            int code = kernel.GetLastError();
            assertEquals("GetLastError failed", INVALID_HANDLE, code);
            int ERRCODE  = 8;
            kernel.SetLastError(ERRCODE);
            code = kernel.GetLastError(); 
            assertEquals("Wrong GetLastError value", ERRCODE, code);
        }
        else {
            fail("GetProcessId(NULL) should fail");
        }
        
        /*
        final int GENERIC_EXECUTE = 0x20000000;
        Pointer h = Advapi32.INSTANCE.OpenSCManager("localhost", null, GENERIC_EXECUTE);
        int code = kernel.GetLastError();
        int EXPECTED = 1722;
        if (h == null) {
            assertEquals("Wrong error", EXPECTED, code);
        }
        else {
            fail("Unexpected non-null result");
        }*/
    }
}
