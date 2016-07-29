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

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Pdh.PDH_COUNTER_PATH_ELEMENTS;
import com.sun.jna.platform.win32.Pdh.PDH_RAW_COUNTER;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;

/**
 * @author lgoldstein
 */
public class PdhTest extends AbstractWin32TestSupport {
    private static final Pdh pdh = Pdh.INSTANCE;

    @Test
    public void testQueryOneCounter() {
        PDH_COUNTER_PATH_ELEMENTS elems = new PDH_COUNTER_PATH_ELEMENTS();
        elems.szObjectName = "Processor";
        elems.szInstanceName = "_Total";
        elems.szCounterName = "% Processor Time";
        String counterName = makeCounterPath(pdh, elems);

        HANDLEByReference ref = new HANDLEByReference();
        assertErrorSuccess("PdhOpenQuery", pdh.PdhOpenQuery(null, null, ref), true);
        
        HANDLE hQuery = ref.getValue();
        try {
            ref.setValue(null);
            assertErrorSuccess("PdhAddEnglishCounter", pdh.PdhAddEnglishCounter(hQuery, counterName, null, ref), true);
            
            HANDLE hCounter = ref.getValue();
            try {
                assertErrorSuccess("PdhCollectQueryData", pdh.PdhCollectQueryData(hQuery), true);
                
                DWORDByReference lpdwType = new DWORDByReference();
                PDH_RAW_COUNTER rawCounter = new PDH_RAW_COUNTER();
                assertErrorSuccess("PdhGetRawCounterValue", pdh.PdhGetRawCounterValue(hCounter, lpdwType, rawCounter), true);
                assertEquals("Bad counter data status", PdhMsg.PDH_CSTATUS_VALID_DATA, rawCounter.CStatus);
                
                DWORD dwType = lpdwType.getValue();
                int typeValue = dwType.intValue();
                // see https://technet.microsoft.com/en-us/library/cc786359(v=ws.10).aspx
                assertEquals("Mismatched counter type", WinPerf.PERF_100NSEC_TIMER_INV, typeValue);
                showRawCounterData(System.out, counterName, rawCounter);
            } finally {
                assertErrorSuccess("PdhRemoveCounter", pdh.PdhRemoveCounter(hCounter), true);
            }
        } finally {
            assertErrorSuccess("PdhCloseQuery", pdh.PdhCloseQuery(hQuery), true);
        }
    }
    
    @Test
    public void testQueryMultipleCounters() {
        Collection<String> names = new LinkedList<String>();
        PDH_COUNTER_PATH_ELEMENTS elems = new PDH_COUNTER_PATH_ELEMENTS();
        elems.szObjectName = "Processor";
        elems.szInstanceName = "_Total";
        for (String n : new String[] { "% Processor Time", "% Idle Time", "% User Time"}) {
            elems.szCounterName = n;
            String counterName = makeCounterPath(pdh, elems);
            names.add(counterName);
        }

        HANDLEByReference ref = new HANDLEByReference();
        assertErrorSuccess("PdhOpenQuery", pdh.PdhOpenQuery(null, null, ref), true);
        
        HANDLE hQuery = ref.getValue();
        try {
            Map<String, HANDLE> handlesMap = new HashMap<String, HANDLE>(names.size());
            try {
                for (String counterName : names) {
                    ref.setValue(null);
                    assertErrorSuccess("PdhAddCounter[" + counterName + "]", pdh.PdhAddEnglishCounter(hQuery, counterName, null, ref), true);

                    HANDLE hCounter = ref.getValue();
                    handlesMap.put(counterName, hCounter);
                }

                assertErrorSuccess("PdhCollectQueryData", pdh.PdhCollectQueryData(hQuery), true);
                
                for (Map.Entry<String, HANDLE> ch : handlesMap.entrySet()) {
                    String counterName = ch.getKey();
                    HANDLE hCounter = ch.getValue();
                    PDH_RAW_COUNTER rawCounter = new PDH_RAW_COUNTER();
                    DWORDByReference lpdwType = new DWORDByReference();
                    assertErrorSuccess("PdhGetRawCounterValue[" + counterName + "]", pdh.PdhGetRawCounterValue(hCounter, lpdwType, rawCounter), true);
                    assertEquals("Bad counter data status for " + counterName, PdhMsg.PDH_CSTATUS_VALID_DATA, rawCounter.CStatus);
                    showRawCounterData(System.out, counterName, rawCounter);
                }
            } finally {
                names.clear();

                for (Map.Entry<String, HANDLE> ch : handlesMap.entrySet()) {
                    String name = ch.getKey();
                    HANDLE hCounter = ch.getValue();
                    int status = pdh.PdhRemoveCounter(hCounter);
                    if (status != WinError.ERROR_SUCCESS) {
                        names.add(name);
                    }
                }
                
                if (names.size() > 0) {
                    fail("Failed to remove counters: " + names);
                }
            }
        } finally {
            assertErrorSuccess("PdhCloseQuery", pdh.PdhCloseQuery(hQuery), true);
        }
    }

    private static void showRawCounterData(PrintStream out, String counterName, PDH_RAW_COUNTER rawCounter) {
        out.append('\t').append(counterName)
           .append(" ").append(String.valueOf(rawCounter.TimeStamp.toDate()))
           .append(" 1st=").append(String.valueOf(rawCounter.FirstValue))
           .append(" 2nd=").append(String.valueOf(rawCounter.SecondValue))
           .append(" multi=").append(String.valueOf(rawCounter.MultiCount))
           .println();
    }

    private static String makeCounterPath(Pdh pdh, PDH_COUNTER_PATH_ELEMENTS pathElements) {
        DWORDByReference pcchBufferSize = new DWORDByReference(); 
        int status = pdh.PdhMakeCounterPath(pathElements, null, pcchBufferSize, 0);
        assertEquals("Unexpected status code: 0x" + Integer.toHexString(status), PdhMsg.PDH_MORE_DATA, status);
        
        DWORD bufSize = pcchBufferSize.getValue();
        int numChars = bufSize.intValue();
        assertTrue("Bad required buffer size: " + numChars, numChars > 0);
        
        char[] szFullPathBuffer = new char[numChars + 1 /* the \0 */];
        pcchBufferSize.setValue(new DWORD(szFullPathBuffer.length));
        assertErrorSuccess("PdhMakeCounterPath", pdh.PdhMakeCounterPath(pathElements, szFullPathBuffer,  pcchBufferSize, 0), true);
        
        return Native.toString(szFullPathBuffer);
    }
}
