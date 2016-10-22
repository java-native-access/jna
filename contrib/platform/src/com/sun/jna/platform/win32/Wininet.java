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

import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Functions in WinInet.dll
 */
public interface Wininet extends StdCallLibrary {
    /**
     * A usable instance of this interface
     */
    Wininet INSTANCE = Native.loadLibrary("wininet", Wininet.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Normal cache entry; can be deleted to recover space for new entries.
     */
    int NORMAL_CACHE_ENTRY        = 1;

    /**
     * Sticky cache entry that is exempt from scavenging for the amount of time
     * specified by dwExemptDelta.<br>
     * The default value set by CommitUrlCacheEntryA and CommitUrlCacheEntryW is
     * one day.
     */
    int STICKY_CACHE_ENTRY        = 4;

    /**
     * Cache entry file that has been edited externally. This cache entry type
     * is exempt from scavenging.
     */
    int EDITED_CACHE_ENTRY        = 8;

    /**
     * Not currently implemented.
     */
    int TRACK_OFFLINE_CACHE_ENTRY = 16;

    /**
     * Not currently implemented.
     */
    int TRACK_ONLINE_CACHE_ENTRY  = 32;

    /**
     * Partial response cache entry.
     */
    int SPARSE_CACHE_ENTRY        = 65536;

    /**
     * Cookie cache entry.
     */
    int COOKIE_CACHE_ENTRY        = 1048576;

    /**
     * Visited link cache entry.
     */
    int URLHISTORY_CACHE_ENTRY    = 2097152;

    /**
     * Closes the specified cache enumeration handle.
     *
     * @param hFind
     *            Handle returned by a previous call to the
     *            FindFirstUrlCacheEntry function.
     * @return Returns TRUE if successful, or FALSE otherwise. To get extended
     *         error information, call GetLastError.
     */
    boolean FindCloseUrlCache(HANDLE hFind);

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
    boolean DeleteUrlCacheEntry(String lpszUrlName);

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
    HANDLE FindFirstUrlCacheEntry(String lpszUrlSearchPattern, INTERNET_CACHE_ENTRY_INFO lpFirstCacheEntryInfo,
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
    boolean FindNextUrlCacheEntry(HANDLE hEnumHandle, INTERNET_CACHE_ENTRY_INFO lpNextCacheEntryInfo,
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
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa385134(v=vs.85).aspx">MSDN</a>
     */
    static class INTERNET_CACHE_ENTRY_INFO extends Structure {
        public static final List<String> FIELDS = createFieldsOrder(
                "dwStructSize", "lpszSourceUrlName", "lpszLocalFileName",
                "CacheEntryType", "dwUseCount", "dwHitRate", "dwSizeLow", "dwSizeHigh", "LastModifiedTime",
                "ExpireTime", "LastAccessTime", "LastSyncTime", "lpHeaderInfo", "dwHeaderInfoSize",
                "lpszFileExtension", "u", "additional");

        /**
         * Size of this structure, in bytes. This value can be used to help
         * determine the version of the cache system.
         */
        public int               dwStructSize;

        /**
         * Pointer to a null-terminated string that contains the URL name. The
         * string occupies the memory area at the end of this structure.
         */
        public Pointer           lpszSourceUrlName;

        /**
         * Pointer to a null-terminated string that contains the local file
         * name. The string occupies the memory area at the end of this
         * structure.
         */
        public Pointer           lpszLocalFileName;

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
        public int               CacheEntryType;

        /**
         * Current number of WinInet callers using the cache entry.
         */
        public int               dwUseCount;

        /**
         * Number of times the cache entry was retrieved.
         */
        public int               dwHitRate;

        /**
         * Low-order portion of the file size, in bytes.
         */
        public int               dwSizeLow;

        /**
         * High-order portion of the file size, in bytes.
         */
        public int               dwSizeHigh;

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
        public Pointer           lpHeaderInfo;

        /**
         * Size of the lpHeaderInfo buffer, in TCHARs.
         */
        public int               dwHeaderInfoSize;

        /**
         * Pointer to a string that contains the file name extension used to
         * retrieve the data as a file. The string occupies the memory area at
         * the end of this structure.
         */
        public Pointer           lpszFileExtension;

        /**
         * A union of the last two distinct fields in INTERNET_CACHE_ENTRY_INFO
         */
        public UNION             u;

        /**
         * Additional data (the path and URLs mentioned previously, and more)
         */
        public byte[]            additional;

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

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        @Override
        public String toString() {
            return (lpszLocalFileName == null ? "" : lpszLocalFileName.getWideString(0) + " => ")
                    + (lpszSourceUrlName == null ? "null" : lpszSourceUrlName.getWideString(0));
        }

    }

}