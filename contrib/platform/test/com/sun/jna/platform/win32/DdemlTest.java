
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
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
        
        HSZ handle = Ddeml.INSTANCE.DdeCreateStringHandle(pidInst.getValue().intValue(), "Test", Ddeml.CP_WINUNICODE);
        assertNotNull(handle);
        
        
        Memory mem = new Memory(256 * 2); // String in DDE can not exceed 255 Chars
        Ddeml.INSTANCE.DdeQueryString(pidInst.getValue().intValue(), handle, mem, 256, Ddeml.CP_WINUNICODE);
        
        assertEquals("Test", mem.getWideString(0));
        
        synchronized(mem) {}
        
        assertTrue(Ddeml.INSTANCE.DdeFreeStringHandle(pidInst.getValue().intValue(), handle));
        
        // Test overlong creation -- according to documentation this must fail
        StringBuilder testString = new StringBuilder();
        for(int i = 0; i < 30; i++) {
            testString.append("0123456789");
        }
        
        HSZ handle2 = Ddeml.INSTANCE.DdeCreateStringHandle(pidInst.getValue().intValue(), testString.toString(), Ddeml.CP_WINUNICODE);
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
        HSZ hsz = Ddeml.INSTANCE.DdeCreateStringHandle(pidInst.getValue().intValue(), "Dummy", Ddeml.CP_WINUNICODE);
        
        String testStringPart1 = "Hallo ";
        String testStringPart2 = "Welt";
        
        // Create Handle
        Memory mem = new Memory(256 * 2); // String in DDE can not exceed 255 Chars
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
}
