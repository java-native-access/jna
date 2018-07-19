/* Copyright (c) 2018 Daniel Widdis, All Rights Reserved
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
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;

/**
 * Pdh utility API.
 * 
 * @author widdis[at]gmail[dot]com
 */
public abstract class PdhUtil {
    private static final int CHAR_TO_BYTES = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;

    // This REG_MULTI_SZ value in HKLM provides English counters regardless of
    // the current locale setting
    private static final String ENGLISH_COUNTER_KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
    private static final String ENGLISH_COUNTER_VALUE = "Counter";

    /**
     * Utility method to call Pdh's PdhLookupPerfNameByIndex that allocates the
     * required memory for the szNameBuffer parameter based on the type mapping
     * used, calls to PdhLookupPerfNameByIndex, and returns the received string.
     * 
     * @param szMachineName
     *            Null-terminated string that specifies the name of the computer
     *            where the specified performance object or counter is located.
     *            The computer name can be specified by the DNS name or the IP
     *            address. If NULL, the function uses the local computer.
     * @param dwNameIndex
     *            Index of the performance object or counter.
     * @return Returns the name of the performance object or counter.
     */
    public static String PdhLookupPerfNameByIndex(String szMachineName, int dwNameIndex) {
        // Call once to get required buffer size
        DWORDByReference pcchNameBufferSize = new DWORDByReference(new DWORD(0));
        Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, null, pcchNameBufferSize);

        // Can't allocate 0 memory
        if (pcchNameBufferSize.getValue().intValue() < 1) {
            return "";
        }
        // Allocate buffer and call again
        Memory mem = new Memory(pcchNameBufferSize.getValue().intValue() * CHAR_TO_BYTES);
        Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, mem, pcchNameBufferSize);

        // Convert buffer to Java String
        if (CHAR_TO_BYTES == 1) {
            return mem.getString(0);
        } else {
            return mem.getWideString(0);
        }
    }

    /**
     * Utility method similar to Pdh's PdhLookupPerfIndexByName that returns the
     * counter index corresponding to the specified counter name in English.
     * Uses the registry on the local machine to find the index in the English
     * locale, regardless of the current language setting on the machine.
     * 
     * @param szNameBuffer
     *            The English name of the performance counter
     * @return The counter's index if it exists, or 0 otherwise.
     */
    public static int PdhLookupPerfIndexByEnglishName(String szNameBuffer) {
        // Look up list of english names and ids
        String[] counters = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, ENGLISH_COUNTER_KEY,
                ENGLISH_COUNTER_VALUE);
        // Array contains alternating index/name pairs
        // {"1", "1847", "2", "System", "4", "Memory", ... }
        // Get position of name in the array (odd index), return parsed value of
        // previous even index
        for (int i = 1; i < counters.length; i += 2) {
            if (counters[i].equals(szNameBuffer)) {
                try {
                    return Integer.parseInt(counters[i - 1]);
                } catch (NumberFormatException e) {
                    // Unexpected but handle anyway
                    return 0;
                }
            }
        }
        // Didn't find the String
        return 0;
    }

    /**
     * Utility method to call Pdh's PdhEnumObjectItems that allocates the
     * required memory for the mszCounterList parameter based on the type
     * mapping used, calls to PdhEnumObjectItems, and returns the received lists
     * of strings.
     * 
     * @param szDataSource
     *            String that specifies the name of the log file used to
     *            enumerate the counter and instance names. If NULL, the
     *            function uses the computer specified in the szMachineName
     *            parameter to enumerate the names.
     * @param szMachineName
     *            String that specifies the name of the computer that contains
     *            the counter and instance names that you want to enumerate.
     *            Include the leading slashes in the computer name, for example,
     *            \\computername. If the szDataSource parameter is NULL, you can
     *            set szMachineName to NULL to specify the local computer.
     * @param szObjectName
     *            String that specifies the name of the object whose counter and
     *            instance names you want to enumerate.
     * @param dwDetailLevel
     *            Detail level of the performance items to return. All items
     *            that are of the specified detail level or less will be
     *            returned.
     * @return Returns a List of Strings of the counters for the object.
     */
    public static List<String> PdhEnumObjectItemCounters(String szDataSource, String szMachineName, String szObjectName,
            int dwDetailLevel) {
        List<String> counters = new ArrayList<String>();

        // Call once to get string lengths
        DWORDByReference pcchCounterListLength = new DWORDByReference(new DWORD(0));
        DWORDByReference pcchInstanceListLength = new DWORDByReference(new DWORD(0));
        Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, null, pcchCounterListLength, null,
                pcchInstanceListLength, dwDetailLevel, 0);

        // Can't allocate 0 memory if no counters
        if (pcchCounterListLength.getValue().intValue() < 1) {
            return counters;
        }
        // Allocate memory and call again to populate strings
        Memory mszCounterList = new Memory(pcchCounterListLength.getValue().intValue() * CHAR_TO_BYTES);
        // Don't need the instances
        pcchInstanceListLength.getValue().setValue(0);
        Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, mszCounterList,
                pcchCounterListLength, null, pcchInstanceListLength, dwDetailLevel, 0);

        // Fetch counters
        int offset = 0;
        while (offset < mszCounterList.size()) {
            String s = null;
            if (CHAR_TO_BYTES == 1) {
                s = mszCounterList.getString(offset);
            } else {
                s = mszCounterList.getWideString(offset);
            }
            // list ends with double null
            if (s.isEmpty()) {
                break;
            }
            counters.add(s);
            // Increment for string + null terminator
            offset += (s.length() + 1) * CHAR_TO_BYTES;
        }

        return counters;
    }

    /**
     * Utility method to call Pdh's PdhEnumObjectItems that allocates the
     * required memory for the mszInstanceList parameters based on the type
     * mapping used, calls to PdhEnumObjectItems, and returns the received lists
     * of strings.
     * 
     * @param szDataSource
     *            String that specifies the name of the log file used to
     *            enumerate the counter and instance names. If NULL, the
     *            function uses the computer specified in the szMachineName
     *            parameter to enumerate the names.
     * @param szMachineName
     *            String that specifies the name of the computer that contains
     *            the counter and instance names that you want to enumerate.
     *            Include the leading slashes in the computer name, for example,
     *            \\computername. If the szDataSource parameter is NULL, you can
     *            set szMachineName to NULL to specify the local computer.
     * @param szObjectName
     *            String that specifies the name of the object whose counter and
     *            instance names you want to enumerate.
     * @param dwDetailLevel
     *            Detail level of the performance items to return. All items
     *            that are of the specified detail level or less will be
     *            returned.
     * @return Returns a Lists of Strings of the instances of the object.
     */
    public static List<String> PdhEnumObjectItemInstances(String szDataSource, String szMachineName,
            String szObjectName, int dwDetailLevel) {
        List<String> instances = new ArrayList<String>();

        // Call once to get string lengths
        DWORDByReference pcchCounterListLength = new DWORDByReference(new DWORD(0));
        DWORDByReference pcchInstanceListLength = new DWORDByReference(new DWORD(0));
        Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, null, pcchCounterListLength, null,
                pcchInstanceListLength, dwDetailLevel, 0);

        // Can't allocate 0 memory if no instances
        if (pcchInstanceListLength.getValue().intValue() < 1) {
            return instances;
        }
        // Allocate memory and call again to populate strings
        Memory mszInstanceList = new Memory(pcchInstanceListLength.getValue().intValue() * CHAR_TO_BYTES);
        // Don't need the counters
        pcchCounterListLength.getValue().setValue(0);
        Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, null, pcchCounterListLength,
                mszInstanceList, pcchInstanceListLength, dwDetailLevel, 0);

        // Fetch instances
        int offset = 0;
        while (offset < mszInstanceList.size()) {
            String s = null;
            if (CHAR_TO_BYTES == 1) {
                s = mszInstanceList.getString(offset);
            } else {
                s = mszInstanceList.getWideString(offset);
            }
            // list ends with double null
            if (s.isEmpty()) {
                break;
            }
            instances.add(s);
            // Increment for string + null terminator
            offset += (s.length() + 1) * CHAR_TO_BYTES;
        }

        return instances;
    }
}
