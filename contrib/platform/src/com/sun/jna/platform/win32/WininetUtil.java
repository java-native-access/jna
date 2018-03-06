/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Wininet.INTERNET_CACHE_ENTRY_INFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Reusable functions that use WinInet
 */
public class WininetUtil {
    /**
     * Helper function for traversing wininet's cache and returning all entries.
     * <br>
     * Some entries are cookies, some entries are history items, and some are
     * actual files.<br>
     * 
     * @return A map of cache URL =&gt; local file (or URL =&gt; empty string for
     *         cookie and history entries)
     */
    public static Map<String, String> getCache() {
        List<INTERNET_CACHE_ENTRY_INFO> items = new ArrayList<Wininet.INTERNET_CACHE_ENTRY_INFO>();

        HANDLE cacheHandle = null;
        Win32Exception we = null;
        int lastError = 0;

        // return
        Map<String, String> cacheItems = new LinkedHashMap<String, String>();

        try {
            IntByReference size = new IntByReference();

            // for every entry, we call the API twice:
            // once to get the size into the IntByReference
            // then again to get the actual item
            cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, null, size);
            lastError = Native.getLastError();
            
            // if there's nothing in the cache, we're done.
            if (lastError == WinError.ERROR_NO_MORE_ITEMS) {
                return cacheItems;
            } else if (lastError != WinError.ERROR_SUCCESS && lastError != WinError.ERROR_INSUFFICIENT_BUFFER) {
                throw new Win32Exception(lastError);
            }

            INTERNET_CACHE_ENTRY_INFO entry = new INTERNET_CACHE_ENTRY_INFO(size.getValue());
            cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, entry, size);

            if (cacheHandle == null) {
                throw new Win32Exception(Native.getLastError());
            }

            items.add(entry);

            while (true) {
                size = new IntByReference();

                // for every entry, we call the API twice:
                // once to get the size into the IntByReference
                // then again to get the actual item
                boolean result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, null, size);

                if (!result) {
                    lastError = Native.getLastError();
                    if (lastError == WinError.ERROR_NO_MORE_ITEMS) {
                        break;
                    } else if (lastError != WinError.ERROR_SUCCESS && lastError != WinError.ERROR_INSUFFICIENT_BUFFER) {
                        throw new Win32Exception(lastError);
                    }
                }

                entry = new INTERNET_CACHE_ENTRY_INFO(size.getValue());
                result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, entry, size);

                if (!result) {
                    lastError = Native.getLastError();
                    if (lastError == WinError.ERROR_NO_MORE_ITEMS) {
                        break;
                    } else if (lastError != WinError.ERROR_SUCCESS && lastError != WinError.ERROR_INSUFFICIENT_BUFFER) {
                        throw new Win32Exception(lastError);
                    }
                }
                items.add(entry);
            }

            for (INTERNET_CACHE_ENTRY_INFO item : items) {
                cacheItems.put(item.lpszSourceUrlName.getWideString(0), item.lpszLocalFileName == null ? "" : item.lpszLocalFileName.getWideString(0));
            }

        } catch (Win32Exception e) {
            we = e;
        } finally {
            if (cacheHandle != null) {
                if (!Wininet.INSTANCE.FindCloseUrlCache(cacheHandle)) {
                    if (we != null) {
                        Win32Exception e = new Win32Exception(Native.getLastError());
                        e.addSuppressedReflected(we);
                        we = e;
                    }
                }
            }
        }
        if (we != null) {
            throw we;
        }
        return cacheItems;
    }
}