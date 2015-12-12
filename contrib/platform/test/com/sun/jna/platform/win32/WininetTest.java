/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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

import org.junit.Test;
import org.junit.runner.JUnitCore;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.Wininet.INTERNET_CACHE_ENTRY_INFO;
import com.sun.jna.ptr.IntByReference;

public class WininetTest extends AbstractWin32TestSupport {

    public static void main(String[] args) {
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.run(WininetTest.class);
    }

    @Test
    public void testFindCloseUrlCache() {
        boolean result = Wininet.INSTANCE.FindCloseUrlCache(null);
        int lastError = Native.getLastError();
        assertFalse("FindCloseUrlCache should return false with an invalid handle.", result);
        assertEquals("GetLastError should return ERROR_INVALID_HANDLE", WinError.ERROR_INVALID_HANDLE, lastError);
    }

    @Test
    public void testDeleteUrlCacheEntry() {
        boolean result = Wininet.INSTANCE.DeleteUrlCacheEntry("c:\\tempWinInetTest");
        int lastError = Native.getLastError();
        assertFalse("DeleteUrlCacheEntry should return false with a bogus path.", result);
        assertEquals("GetLastError should return ERROR_FILE_NOT_FOUND", WinError.ERROR_FILE_NOT_FOUND, lastError);
    }

    @Test
    public void testFindFirstUrlCacheEntry() {
        IntByReference size = new IntByReference();
        HANDLE cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, null, size);
        int lastError = Native.getLastError();
        // ERROR_INSUFFICIENT_BUFFER is returned when there are items in the cache
        // ERROR_NO_MORE_ITEMS is returned when the cache is empty.
        // Both are acceptable for a mapping test where the state of the cache would be unknown.
        assertTrue("GetLastError should have returned ERROR_INSUFFICIENT_BUFFER or ERROR_NO_MORE_ITEMS.", lastError ==  WinError.ERROR_INSUFFICIENT_BUFFER || lastError == WinError.ERROR_NO_MORE_ITEMS);
        assertNull("FindFirstUrlCacheEntry should have returned null.", cacheHandle);
    }

    @Test
    public void testFindNextUrlCacheEntry() {
        HANDLE cacheHandle = null;
        try {
            IntByReference size = new IntByReference();
            int lastError = 0;

            // for every entry, we call the API twice:
            // once to get the size into the IntByReference
            // then again to get the actual item
            cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, null, size);
            lastError = Native.getLastError();
            assertNull("FindFirstUrlCacheEntry should have returned null.", cacheHandle);
            
            // if the Wininet cache is empty, exercise FindNextUrlCacheEntry with an invalid handle 
            // just to ensure the mapping gets tested
            if (lastError == WinError.ERROR_NO_MORE_ITEMS) {
                boolean result = Wininet.INSTANCE.FindNextUrlCacheEntry(null, null, size);
                lastError = Native.getLastError();
                assertFalse("FindNextUrlCacheEntry should have returned false.", result);
                assertEquals("GetLastError should have returned ERROR_INVALID_PARAMETER.",
                        WinError.ERROR_INVALID_PARAMETER, lastError);              
                return;
            }
            
            assertEquals("GetLastError should have returned ERROR_INSUFFICIENT_BUFFER.",
                    WinError.ERROR_INSUFFICIENT_BUFFER, lastError);

            INTERNET_CACHE_ENTRY_INFO entry = new INTERNET_CACHE_ENTRY_INFO(size.getValue());
            cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, entry, size);
            lastError = Native.getLastError();

            assertNotNull("FindFirstUrlCacheEntry should not have returned null.", cacheHandle);
            assertEquals("GetLastError should have returned ERROR_SUCCESS.", WinError.ERROR_SUCCESS, lastError);

            size = new IntByReference();

            // for every entry, we call the API twice:
            // once to get the size into the IntByReference
            // then again to get the actual item
            boolean result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, null, size);
            lastError = Native.getLastError();
            assertFalse("FindNextUrlCacheEntry should have returned false.", result);
            assertEquals("GetLastError should have returned ERROR_INSUFFICIENT_BUFFER.",
                    WinError.ERROR_INSUFFICIENT_BUFFER, lastError);

            entry = new INTERNET_CACHE_ENTRY_INFO(size.getValue());
            result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, entry, size);
            lastError = Native.getLastError();
            assertTrue("FindNextUrlCacheEntry should have returned true.", result);
            assertEquals("GetLastError should have returned ERROR_SUCCESS.", WinError.ERROR_SUCCESS, lastError);

        } finally {
            if (cacheHandle != null) {
                if (!Wininet.INSTANCE.FindCloseUrlCache(cacheHandle)) {
                    throw new Win32Exception(Native.getLastError());
                }
            }
        }
    }
}
