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
import com.sun.jna.platform.win32.WinInet.INTERNET_CACHE_ENTRY_INFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Reusable functions that use WinInet.dll
 * 
 * @author mlfreeman[at]gmail.com
 */
public final class WinInetUtil {
	/**
	 * Helper function for traversing wininet's cache and returning all entries.
	 * <br>
	 * Some entries are cookies, some entries are history items, and some are
	 * actual files.<br>
	 * 
	 * @return A map of cache URL => local file (or URL => empty string for
	 *         cookie and history entries)
	 */
	public static Map<String, String> getBrowserCache() {
		List<INTERNET_CACHE_ENTRY_INFO> items = new ArrayList<WinInet.INTERNET_CACHE_ENTRY_INFO>();

		// WinInet APIs could return these two errors:

		// this one happens if the INTERNET_CACHE_ENTRY_INFO structure will
		// require more space to be filled properly
		// ERROR_INSUFFICIENT_BUFFER
		// 122 (0x7A)
		// The data area passed to a system call is too small.

		// this one happens when you've gone through the cache completely
		// ERROR_NO_MORE_ITEMS
		// 259 (0x103)
		// No more data is available.

		IntByReference size = new IntByReference();
		HANDLE entryHandle = WinInet.INSTANCE.FindFirstUrlCacheEntry(null, null, size);

		INTERNET_CACHE_ENTRY_INFO entry = new INTERNET_CACHE_ENTRY_INFO(size.getValue());
		entryHandle = WinInet.INSTANCE.FindFirstUrlCacheEntry(null, entry, size);

		if (entryHandle == null || Native.getLastError() != 0) {
			return null;
		}

		items.add(entry);

		while (true) {
			size = new IntByReference();
			WinInet.INSTANCE.FindNextUrlCacheEntry(entryHandle, null, size);

			if (Native.getLastError() == 259) {
				WinInet.INSTANCE.FindCloseUrlCache(entryHandle);
				break;
			}

			entry = new INTERNET_CACHE_ENTRY_INFO(size.getValue());
			boolean result = WinInet.INSTANCE.FindNextUrlCacheEntry(entryHandle, entry, size);

			if (!result || Native.getLastError() == 259) {
				WinInet.INSTANCE.FindCloseUrlCache(entryHandle);
				break;
			}
			items.add(entry);
		}

		Map<String, String> result = new LinkedHashMap<String, String>();
		for (INTERNET_CACHE_ENTRY_INFO item : items) {
			result.put(item.lpszSourceUrlName.getWideString(0),
					item.lpszLocalFileName == null ? "" : item.lpszLocalFileName.getWideString(0));
		}
		return result;
	}
}