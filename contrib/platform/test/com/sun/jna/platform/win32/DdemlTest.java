/* Copyright 2016, Matthias Bläsing, All Rights Reserved
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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ddeml.DdeCallback;
import com.sun.jna.platform.win32.Ddeml.HDDEDATA;
import com.sun.jna.platform.win32.Ddeml.HSZ;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import org.junit.Test;
import static org.junit.Assert.*;

public class DdemlTest {

    @Test
    public void testInitialization() {
        DdeCallback callback = new Ddeml.DdeCallback() {
            public WinDef.PVOID ddeCallback(int wType, int wFmt, Ddeml.HCONV hConv, Ddeml.HSZ hsz1, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hData, BaseTSD.ULONG_PTR lData1, BaseTSD.ULONG_PTR lData2) {
                return new PVOID();
            }
        };

        DWORDByReference pidInst = new DWORDByReference();
        int initResult = Ddeml.INSTANCE.DdeInitialize(pidInst, callback, Ddeml.APPCMD_CLIENTONLY, 0);
        assertEquals(Ddeml.DMLERR_NO_ERROR, initResult);
        boolean uninitResult = Ddeml.INSTANCE.DdeUninitialize(pidInst.getValue().intValue());
        assertTrue(uninitResult);
    }


    @Test
    public void testStringHandling() {
        DdeCallback callback = new Ddeml.DdeCallback() {
            public WinDef.PVOID ddeCallback(int wType, int wFmt, Ddeml.HCONV hConv, Ddeml.HSZ hsz1, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hData, BaseTSD.ULONG_PTR lData1, BaseTSD.ULONG_PTR lData2) {
                return new PVOID();
            }
        };

        DWORDByReference pidInst = new DWORDByReference();
        int initResult = Ddeml.INSTANCE.DdeInitialize(pidInst, callback, Ddeml.APPCMD_CLIENTONLY, 0);
        assertEquals(Ddeml.DMLERR_NO_ERROR, initResult);
        Memory mem = createMemoryForWideString("Test");
        HSZ handle = Ddeml.INSTANCE.DdeCreateStringHandle(pidInst.getValue().intValue(), mem, Ddeml.CP_WINUNICODE);
        assertNotNull(handle);

        mem = new Memory(256 * Native.WCHAR_SIZE); // String in DDE can not exceed 255 Chars
        mem.clear();
        Ddeml.INSTANCE.DdeQueryString(pidInst.getValue().intValue(), handle, mem, 256, Ddeml.CP_WINUNICODE);

        assertEquals("Test", mem.getWideString(0));

        synchronized(mem) {}

        assertTrue(Ddeml.INSTANCE.DdeFreeStringHandle(pidInst.getValue().intValue(), handle));

        // Test overlong creation -- according to documentation this must fail
        StringBuilder testString = new StringBuilder();
        for(int i = 0; i < 30; i++) {
            testString.append("0123456789");
        }
        mem = createMemoryForWideString(testString.toString());

        HSZ handle2 = Ddeml.INSTANCE.DdeCreateStringHandle(pidInst.getValue().intValue(), mem, Ddeml.CP_WINUNICODE);
        assertNull(handle2);

        boolean uninitResult = Ddeml.INSTANCE.DdeUninitialize(pidInst.getValue().intValue());
        assertTrue(uninitResult);
    }

    @Test
    public void testGetLastError() {
        int errorCode = Ddeml.INSTANCE.DdeGetLastError(0);
        assertEquals(Ddeml.DMLERR_INVALIDPARAMETER, errorCode);
    }

    @Test
    public void testMemoryHandling() {
        DdeCallback callback = new Ddeml.DdeCallback() {
            public WinDef.PVOID ddeCallback(int wType, int wFmt, Ddeml.HCONV hConv, Ddeml.HSZ hsz1, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hData, BaseTSD.ULONG_PTR lData1, BaseTSD.ULONG_PTR lData2) {
                return new PVOID();
            }
        };

        DWORDByReference pidInst = new DWORDByReference();
        int initResult = Ddeml.INSTANCE.DdeInitialize(pidInst, callback, Ddeml.APPCMD_CLIENTONLY, 0);
        assertEquals(Ddeml.DMLERR_NO_ERROR, initResult);

        // Acquire dummy handle
        HSZ hsz = Ddeml.INSTANCE.DdeCreateStringHandle(pidInst.getValue().intValue(), createMemoryForWideString("Dummy"), Ddeml.CP_WINUNICODE);

        String testStringPart1 = "Hallo ";
        String testStringPart2 = "Welt";

        // Create Handle
        Memory mem = new Memory(256 * Native.WCHAR_SIZE); // String in DDE can not exceed 255 Chars
        mem.setWideString(0, testStringPart1);
        HDDEDATA data = Ddeml.INSTANCE.DdeCreateDataHandle(pidInst.getValue().intValue(), mem, testStringPart1.length() * 2, 0, hsz, WinUser.CF_UNICODETEXT, Ddeml.HDATA_APPOWNED);

        mem.setWideString(0, testStringPart2);
        Ddeml.INSTANCE.DdeAddData(data, mem, (testStringPart2.length() + 1) * 2, testStringPart1.length() * 2);

        DWORDByReference dataSize = new DWORDByReference();
        Pointer resultPointer = Ddeml.INSTANCE.DdeAccessData(data, dataSize);

        assertEquals((testStringPart1.length() + testStringPart2.length() + 1) * 2, dataSize.getValue().intValue());
        assertEquals(testStringPart1 + testStringPart2, resultPointer.getWideString(0));

        boolean result = Ddeml.INSTANCE.DdeUnaccessData(data);

        int readSize = Ddeml.INSTANCE.DdeGetData(data, mem, (int) mem.size(), 0);
        assertEquals((testStringPart1.length() + testStringPart2.length() + 1) * 2, readSize);
        assertEquals(testStringPart1 + testStringPart2, mem.getWideString(0));

        assertTrue(result);

        result = Ddeml.INSTANCE.DdeFreeDataHandle(data);

        assertTrue(result);

        synchronized(mem) {}

        result = Ddeml.INSTANCE.DdeUninitialize(pidInst.getValue().intValue());
        assertTrue(result);
    }

    private Memory createMemoryForWideString(String str) {
        Memory mem = new Memory((str.length() + 1) * Native.WCHAR_SIZE);
        mem.clear();
        mem.setWideString(0, str);
        return mem;
    }
}
