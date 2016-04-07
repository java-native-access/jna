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
     * @return A map of cache URL => local file (or URL => empty string for
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
                        e.addSuppressed(we);
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