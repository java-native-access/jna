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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinDef.LONGByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * WinInet.dll interface
 * 
 * @author mlfreeman[at]gmail.com
 */
public interface WinInet extends StdCallLibrary {

	/**
	 * Normal cache entry; can be deleted to recover space for new entries.
	 */
	public static final int NORMAL_CACHE_ENTRY = 1;

	/**
	 * Sticky cache entry that is exempt from scavenging for the amount of time
	 * specified by dwExemptDelta.<br>
	 * The default value set by CommitUrlCacheEntryA and CommitUrlCacheEntryW is
	 * one day.
	 */
	public static final int STICKY_CACHE_ENTRY = 4;

	/**
	 * Cache entry file that has been edited externally. This cache entry type
	 * is exempt from scavenging.
	 */
	public static final int EDITED_CACHE_ENTRY = 8;

	/**
	 * Not currently implemented.
	 */
	public static final int TRACK_OFFLINE_CACHE_ENTRY = 16;

	/**
	 * Not currently implemented.
	 */
	public static final int TRACK_ONLINE_CACHE_ENTRY = 32;

	/**
	 * Partial response cache entry.
	 */
	public static final int SPARSE_CACHE_ENTRY = 65536;

	/**
	 * Cookie cache entry.
	 */
	public static final int COOKIE_CACHE_ENTRY = 1048576;

	/**
	 * Visited link cache entry.
	 */
	public static final int URLHISTORY_CACHE_ENTRY = 2097152;

	/**
	 * Indicates that all of the cache groups in the user's system should be
	 * enumerated.
	 */
	int CACHEGROUP_SEARCH_ALL = 0;

	/**
	 * Not currently implemented.
	 */
	int CACHEGROUP_SEARCH_BYURL = 1;

	/**
	 * Indicates that all the cache entries associated with the cache group
	 * should be deleted, unless the entry belongs to another cache group.
	 */
	int CACHEGROUP_FLAG_FLUSHURL_ONDELETE = 2;

	/**
	 * A usable instance of this interface
	 */
	WinInet INSTANCE = (WinInet) Native.loadLibrary("wininet", WinInet.class, W32APIOptions.DEFAULT_OPTIONS);

	/**
	 * Initiates the enumeration of the cache groups in the Internet cache.<br>
	 * The handle returned from FindFirstUrlCacheGroup is used in subsequent
	 * calls to FindNextUrlCacheGroup.<br>
	 * At the end of the enumeration, the application should call
	 * FindCloseUrlCache.
	 * 
	 * @param dwFlags
	 *            This parameter is reserved and must be 0.
	 * @param dwFilter
	 *            Filters to be used. This parameter can be zero or one of the
	 *            following values:<br>
	 *            CACHEGROUP_SEARCH_ALL (0)<br>
	 *            CACHEGROUP_SEARCH_BYURL (1)<br>
	 * @param lpSearchCondition
	 *            This parameter is reserved and must be NULL.
	 * @param dwSearchCondition
	 *            This parameter is reserved and must be 0.
	 * @param lpGroupID
	 *            Pointer to the ID of the first cache group that matches the
	 *            search criteria.
	 * @param lpReserved
	 *            This parameter is reserved and must be NULL.
	 * @return Returns a valid handle to the first item in the enumeration if
	 *         successful, or NULL otherwise. <br>
	 *         To get specific error information, call GetLastError. If the
	 *         function finds no matching files, GetLastError returns
	 *         ERROR_NO_MORE_FILES.
	 */
	public HANDLE FindFirstUrlCacheGroup(int dwFlags, int dwFilter, Pointer lpSearchCondition, int dwSearchCondition,
			LONGByReference lpGroupID, Pointer lpReserved);

	/**
	 * Retrieves the next cache group in a cache group enumeration started by
	 * FindFirstUrlCacheGroup.<br>
	 * 
	 * @param hFind
	 *            The cache group enumeration handle, which is returned by
	 *            FindFirstUrlCacheGroup.
	 * @param lpGroupID
	 *            Pointer to a variable that receives the cache group
	 *            identifier.
	 * @param lpReserved
	 *            This parameter is reserved and must be NULL.
	 * @return Returns TRUE if successful, or FALSE otherwise. To get specific
	 *         error information, call GetLastError.<br>
	 *         Continue to call FindNextUrlCacheGroup until the last item in the
	 *         cache is returned.
	 */
	public boolean FindNextUrlCacheGroup(HANDLE hFind, LONGByReference lpGroupID, Pointer lpReserved);

	/**
	 * Releases the specified GROUPID and any associated state in the cache
	 * index file.
	 * 
	 * @param GroupID
	 *            ID of the cache group to be released.
	 * @param dwFlags
	 *            Controls the cache group deletion.<br>
	 *            This can be set to any member of the cache group constants.
	 *            <br>
	 *            When this parameter is set to
	 *            CACHEGROUP_FLAG_FLUSHURL_ONDELETE, it causes
	 *            DeleteUrlCacheGroup to delete all of the cache entries
	 *            associated with this group, unless the entry belongs to
	 *            another group. CACHEGROUP_FLAG_FLUSHURL_ONDELETE is 2.
	 * @param lpReserved
	 *            This parameter is reserved and must be NULL.
	 * @return Returns TRUE if successful, or FALSE otherwise. To get specific
	 *         error information, call GetLastError.
	 */
	public boolean DeleteUrlCacheGroup(LONGByReference GroupID, int dwFlags, Pointer lpReserved);

	/**
	 * Closes the specified cache enumeration handle.
	 * 
	 * @param hFind
	 *            Handle returned by a previous call to the
	 *            FindFirstUrlCacheEntry function.
	 * @return Returns TRUE if successful, or FALSE otherwise. To get extended
	 *         error information, call GetLastError.
	 */
	public boolean FindCloseUrlCache(HANDLE hFind);

	/**
	 * @param lpszUrlName
	 *            String that contains the name of the source that corresponds
	 *            to the cache entry.
	 * @return Returns TRUE if successful, or FALSE otherwise.<br>
	 *         To get extended error information, call GetLastError.<br>
	 *         Possible error values include the following.<br>
	 *         <ul>
	 *         <li><b>ERROR_ACCESS_DENIED:</b>The file is locked or in use. The
	 *         entry is marked and deleted when the file is unlocked.</li>
	 *         <li><b>ERROR_FILE_NOT_FOUND:</b>The file is not in the cache.
	 *         </li>
	 *         </ul>
	 */
	public boolean DeleteUrlCacheEntry(String lpszUrlName);

	/**
	 * Begins the enumeration of the Internet cache.
	 * 
	 * @param lpszUrlSearchPattern
	 *            A pointer to a string that contains the source name pattern to
	 *            search for.<br>
	 *            This parameter can only be set to "cookie:", "visited:", or
	 *            NULL.<br>
	 *            Set this parameter to "cookie:" to enumerate the cookies or
	 *            "visited:" to enumerate the URL History entries in the cache.
	 *            <br>
	 *            If this parameter is NULL, FindFirstUrlCacheEntry returns all
	 *            content entries in the cache.
	 * @param lpFirstCacheEntryInfo
	 *            Pointer to an INTERNET_CACHE_ENTRY_INFO structure.
	 * @param lpcbCacheEntryInfo
	 *            Pointer to a variable that specifies the size of the
	 *            lpFirstCacheEntryInfo buffer, in bytes.<br>
	 *            When the function returns, the variable contains the number of
	 *            bytes copied to the buffer, or the required size needed to
	 *            retrieve the cache entry, in bytes.
	 * @return Returns a handle that the application can use in the
	 *         FindNextUrlCacheEntry function to retrieve subsequent entries in
	 *         the cache.<br>
	 *         If the function fails, the return value is NULL. To get extended
	 *         error information, call GetLastError.<br>
	 *         ERROR_INSUFFICIENT_BUFFER indicates that the size of
	 *         lpFirstCacheEntryInfo as specified by
	 *         lpdwFirstCacheEntryInfoBufferSize is not sufficient to contain
	 *         all the information.<br>
	 *         The value returned in lpdwFirstCacheEntryInfoBufferSize indicates
	 *         the buffer size necessary to contain all the information.
	 */
	public HANDLE FindFirstUrlCacheEntry(String lpszUrlSearchPattern, INTERNET_CACHE_ENTRY_INFO lpFirstCacheEntryInfo,
			IntByReference lpcbCacheEntryInfo);

	/**
	 * @param hEnumHandle
	 *            Handle to the enumeration obtained from a previous call to
	 *            FindFirstUrlCacheEntry.
	 * @param lpNextCacheEntryInfo
	 *            Pointer to an INTERNET_CACHE_ENTRY_INFO structure that
	 *            receives information about the cache entry.
	 * @param lpcbCacheEntryInfo
	 *            Pointer to a variable that specifies the size of the
	 *            lpNextCacheEntryInfo buffer, in bytes.<br>
	 *            When the function returns, the variable contains the number of
	 *            bytes copied to the buffer, or the size of the buffer required
	 *            to retrieve the cache entry, in bytes.
	 * @return Returns TRUE if successful, or FALSE otherwise.<br>
	 *         To get extended error information, call GetLastError. <br>
	 *         Possible error values include the following.<br>
	 *         <ul>
	 *         <li><b>ERROR_INSUFFICIENT_BUFFER:</b>The size of
	 *         lpNextCacheEntryInfo as specified by
	 *         lpdwNextCacheEntryInfoBufferSize is not sufficient to contain all
	 *         the information.<br>
	 *         The value returned in lpdwNextCacheEntryInfoBufferSize indicates
	 *         the buffer size necessary to contain all the information.</li>
	 *         <li><b>ERROR_NO_MORE_ITEMS:</b>The enumeration completed.</li>
	 *         </ul>
	 */
	public boolean FindNextUrlCacheEntry(HANDLE hEnumHandle, INTERNET_CACHE_ENTRY_INFO lpNextCacheEntryInfo,
			IntByReference lpcbCacheEntryInfo);

	/**
	 * Contains information about an entry in the Internet cache.
	 * 
	 * <pre>
	 * <code>
	 * typedef struct _INTERNET_CACHE_ENTRY_INFO {
	 *   DWORD    dwStructSize;
	 *   LPTSTR   lpszSourceUrlName;
	 *   LPTSTR   lpszLocalFileName;
	 *   DWORD    CacheEntryType;
	 *   DWORD    dwUseCount;
	 *   DWORD    dwHitRate;
	 *   DWORD    dwSizeLow;
	 *   DWORD    dwSizeHigh;
	 *   FILETIME LastModifiedTime;
	 *   FILETIME ExpireTime;
	 *   FILETIME LastAccessTime;
	 *   FILETIME LastSyncTime;
	 *   LPTSTR   lpHeaderInfo;
	 *   DWORD    dwHeaderInfoSize;
	 *   LPTSTR   lpszFileExtension;
	 *   union {
	 *     DWORD dwReserved;
	 *     DWORD dwExemptDelta;
	 *   };
	 * } INTERNET_CACHE_ENTRY_INFO, *LPINTERNET_CACHE_ENTRY_INFO;
	 * 
	 *     </code>
	 * </pre>
	 * 
	 * @see https://msdn.microsoft.com/en-us/library/windows/desktop/aa385134(v=
	 *      vs.85).aspx
	 */
	public static class INTERNET_CACHE_ENTRY_INFO extends Structure {
		/**
		 * Size of this structure, in bytes. This value can be used to help
		 * determine the version of the cache system.
		 */
		public int dwStructSize;

		/**
		 * Pointer to a null-terminated string that contains the URL name. The
		 * string occupies the memory area at the end of this structure.
		 */
		public Pointer lpszSourceUrlName;

		/**
		 * Pointer to a null-terminated string that contains the local file
		 * name. The string occupies the memory area at the end of this
		 * structure.
		 */
		public Pointer lpszLocalFileName;

		/**
		 * A bitmask indicating the type of cache entry and its properties.<br>
		 * The cache entry types include: history entries
		 * (URLHISTORY_CACHE_ENTRY), cookie entries (COOKIE_CACHE_ENTRY), and
		 * normal cached content (NORMAL_CACHE_ENTRY). <br>
		 * <br>
		 * This member can be zero or more of the following property flags, and
		 * cache type flags listed below.
		 * <ul>
		 * <li><b>EDITED_CACHE_ENTRY:</b> Cache entry file that has been edited
		 * externally. This cache entry type is exempt from scavenging.</li>
		 * <li><b>SPARSE_CACHE_ENTRY:</b> Partial response cache entry.</li>
		 * <li><b>STICKY_CACHE_ENTRY:</b> Sticky cache entry that is exempt from
		 * scavenging for the amount of time specified by dwExemptDelta.<br>
		 * The default value set by CommitUrlCacheEntryA and
		 * CommitUrlCacheEntryW is one day.</li>
		 * <li><b>TRACK_OFFLINE_CACHE_ENTRY:</b> Not currently implemented.</li>
		 * <li><b>TRACK_ONLINE_CACHE_ENTRY:</b> Not currently implemented.</li>
		 * </ul>
		 * <br>
		 * The following list contains the cache type flags.
		 * <ul>
		 * <li><b>COOKIE_CACHE_ENTRY:</b> Cookie cache entry.</li>
		 * <li><b>NORMAL_CACHE_ENTRY:</b> Normal cache entry; can be deleted to
		 * recover space for new entries.</li>
		 * <li><b>URLHISTORY_CACHE_ENTRY:</b> Visited link cache entry.</li>
		 * </ul>
		 */
		public int CacheEntryType;

		/**
		 * Current number of WinInet callers using the cache entry.
		 */
		public int dwUseCount;

		/**
		 * Number of times the cache entry was retrieved.
		 */
		public int dwHitRate;

		/**
		 * Low-order portion of the file size, in bytes.
		 */
		public int dwSizeLow;

		/**
		 * High-order portion of the file size, in bytes.
		 */
		public int dwSizeHigh;

		/**
		 * FILETIME structure that contains the last modified time of this URL,
		 * in Greenwich mean time format.
		 */
		public Kernel32.FILETIME LastModifiedTime;

		/**
		 * FILETIME structure that contains the expiration time of this file, in
		 * Greenwich mean time format.
		 */
		public Kernel32.FILETIME ExpireTime;

		/**
		 * FILETIME structure that contains the last accessed time, in Greenwich
		 * mean time format.
		 */
		public Kernel32.FILETIME LastAccessTime;

		/**
		 * FILETIME structure that contains the last time the cache was
		 * synchronized.
		 */
		public Kernel32.FILETIME LastSyncTime;

		/**
		 * Pointer to a buffer that contains the header information. The buffer
		 * occupies the memory at the end of this structure.
		 */
		public Pointer lpHeaderInfo;

		/**
		 * Size of the lpHeaderInfo buffer, in TCHARs.
		 */
		public int dwHeaderInfoSize;

		/**
		 * Pointer to a string that contains the file name extension used to
		 * retrieve the data as a file. The string occupies the memory area at
		 * the end of this structure.
		 */
		public Pointer lpszFileExtension;

		/**
		 * A union of the last two distinct fields in INTERNET_CACHE_ENTRY_INFO
		 */
		public UNION u;

		/**
		 * Additional data (the path and URLs mentioned previously, and more)
		 */
		public byte[] additional;

		public INTERNET_CACHE_ENTRY_INFO(int size) {
			additional = new byte[size];
		}

		/**
		 * A union of the last two distinct fields in INTERNET_CACHE_ENTRY_INFO
		 * 
		 * <pre>
		 * <code>
		 *             union {
		 *                 DWORD dwReserved;
		 *                 DWORD dwExemptDelta;
		 *             };</code>
		 * </pre>
		 */
		public static class UNION extends Union {
			/**
			 * Reserved. Must be zero.
			 */
			public int dwReserved;

			/**
			 * Exemption time from the last accessed time, in seconds.
			 */
			public int dwExemptDelta;
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "dwStructSize", "lpszSourceUrlName", "lpszLocalFileName",
					"CacheEntryType", "dwUseCount", "dwHitRate", "dwSizeLow", "dwSizeHigh", "LastModifiedTime",
					"ExpireTime", "LastAccessTime", "LastSyncTime", "lpHeaderInfo", "dwHeaderInfoSize",
					"lpszFileExtension", "u", "additional" });
		}

		@Override
		public String toString() {
			return (lpszLocalFileName == null ? "" : lpszLocalFileName.getWideString(0) + " => ")
					+ (lpszSourceUrlName == null ? "null" : lpszSourceUrlName.getWideString(0));
		}

	}

}
