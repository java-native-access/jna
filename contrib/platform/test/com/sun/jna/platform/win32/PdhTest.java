/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Pdh.PDH_COUNTER_PATH_ELEMENTS;
import com.sun.jna.platform.win32.Pdh.PDH_RAW_COUNTER;
import com.sun.jna.platform.win32.PdhUtil.PdhEnumObjectItems;
import com.sun.jna.platform.win32.PdhUtil.PdhException;
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
        Collection<String> names = new LinkedList<>();
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
            Map<String, HANDLE> handlesMap = new HashMap<>(names.size());
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

    @Test
    public void testLookupPerfIndex() {
        int processorIndex = 238;
        String processorStr = "Processor"; // English locale

        // Test index-to-name
        String testStr = PdhUtil.PdhLookupPerfNameByIndex(null, processorIndex);
        if (AbstractWin32TestSupport.isEnglishLocale) {
            assertEquals(processorStr, testStr);
        } else {
            assertTrue(testStr.length() > 0);
        }

        // Test name-to-index
        DWORDByReference pdwIndex = new DWORDByReference();
        Pdh.INSTANCE.PdhLookupPerfIndexByName(null, testStr, pdwIndex);
        assertEquals(processorIndex, pdwIndex.getValue().intValue());

        // Test English name to index
        assertEquals(processorIndex, PdhUtil.PdhLookupPerfIndexByEnglishName(processorStr));
    }

    @Test
    public void testEnumObjectItems() {
        if (AbstractWin32TestSupport.isEnglishLocale) {
            String processorStr = "Processor";
            String processorTimeStr = "% Processor Time";

            // Fetch the counter and instance names
            PdhEnumObjectItems objects = PdhUtil.PdhEnumObjectItems(null, null, processorStr, 100);

            assertTrue(objects.getInstances().contains("0"));
            assertTrue(objects.getInstances().contains("_Total"));

            // Should have a "% Processor Time" counter
            assertTrue(objects.getCounters().contains(processorTimeStr));
        } else {
            System.err.println("testEnumObjectItems test can only be run with english locale.");
        }
    }

    @Test
    public void testEnumObjectItemsNonExisting() {
        Exception caughtException = null;
        try {
            PdhUtil.PdhEnumObjectItems(null, null, "Unknown counter", 100);
        } catch (Exception ex) {
            caughtException = ex;
        }
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof PdhException);
        assertEquals(Pdh.PDH_CSTATUS_NO_OBJECT, ((PdhException) caughtException).getErrorCode());
    }
}
