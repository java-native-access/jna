/*
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

import java.awt.Frame;
import java.util.HashSet;
import java.util.Set;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.Wtsapi32.WTSINFO;
import com.sun.jna.platform.win32.Wtsapi32.WTS_CLIENT_ADDRESS;
import com.sun.jna.platform.win32.Wtsapi32.WTS_CONNECTSTATE_CLASS;
import com.sun.jna.platform.win32.Wtsapi32.WTS_INFO_CLASS;
import com.sun.jna.platform.win32.Wtsapi32.WTS_PROCESS_INFO_EX;
import com.sun.jna.platform.win32.Wtsapi32.WTS_SESSION_INFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import junit.framework.TestCase;

public class Wtsapi32Test extends TestCase {

    private final HWND hwnd;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Wtsapi32Test.class);
    }

    public Wtsapi32Test() {
        Frame frame = new Frame();
        frame.setVisible(true);
        this.hwnd = new HWND(Native.getWindowPointer(frame));
    }

    public void testWTSRegisterSessionNotification() {
        boolean result = Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hwnd,
                Wtsapi32.NOTIFY_FOR_ALL_SESSIONS);
        assertEquals(true, result);
    }

    public void testWTSUnRegisterSessionNotification() {
        // needed to register before you can unregister!
        testWTSRegisterSessionNotification();
        boolean result = Wtsapi32.INSTANCE
                .WTSUnRegisterSessionNotification(hwnd);

        assertEquals(true, result);
    }

    public void testWTSEnumerateProcessesEx() {
        // Get processes from WTS
        PointerByReference ppProcessInfo = new PointerByReference();
        IntByReference pCount = new IntByReference(0);
        assertTrue(Wtsapi32.INSTANCE.WTSEnumerateProcessesEx(Wtsapi32.WTS_CURRENT_SERVER_HANDLE,
                new IntByReference(Wtsapi32.WTS_PROCESS_INFO_LEVEL_1), Wtsapi32.WTS_ANY_SESSION, ppProcessInfo,
                pCount));
        // extract the pointed-to pointer and create array
        Pointer pProcessInfo = ppProcessInfo.getValue();
        WTS_PROCESS_INFO_EX processInfoRef = new WTS_PROCESS_INFO_EX(pProcessInfo);
        WTS_PROCESS_INFO_EX[] processInfo = (WTS_PROCESS_INFO_EX[]) processInfoRef.toArray(pCount.getValue());

        Set<Integer> pidSet = new HashSet<>();
        for (WTS_PROCESS_INFO_EX procInfo : processInfo) {
            // PIDs should be unique
            if (procInfo.ProcessId != 0) {
                assertFalse(pidSet.contains(procInfo.ProcessId));
                pidSet.add(procInfo.ProcessId);
            }

            // A process cannot time travel and use negative CPU ticks
            assertTrue(procInfo.KernelTime.getValue() >= 0);
            assertTrue(procInfo.UserTime.getValue() >= 0);

            // Process name should be nonempty except for the Idle process
            String name = procInfo.pProcessName;
            if (procInfo.ProcessId != 0) {
                assertFalse(name.isEmpty());
            }

        }
        // Clean up memory allocated in C
        assertTrue(
                Wtsapi32.INSTANCE.WTSFreeMemoryEx(Wtsapi32.WTS_PROCESS_INFO_LEVEL_1, pProcessInfo, pCount.getValue()));

    }

    public void testWTSEnumerateSessions() {
        PointerByReference ppSessionInfo = new PointerByReference();
        IntByReference pCount = new IntByReference();
        assertTrue("Enumerate Sessions failed.", Wtsapi32.INSTANCE
                .WTSEnumerateSessions(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, 0, 1, ppSessionInfo, pCount));
        Pointer pSessionInfo = ppSessionInfo.getValue();
        if (pCount.getValue() > 0) {
            WTS_SESSION_INFO sessionInfoRef = new WTS_SESSION_INFO(pSessionInfo);
            WTS_SESSION_INFO[] sessionInfo = (WTS_SESSION_INFO[]) sessionInfoRef.toArray(pCount.getValue());
            for (WTS_SESSION_INFO session : sessionInfo) {
                if (session.State == WTS_CONNECTSTATE_CLASS.WTSActive) {
                    // Use session id to fetch additional session information
                    PointerByReference ppBuffer = new PointerByReference();
                    IntByReference pBytes = new IntByReference();
                    Wtsapi32.INSTANCE.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, session.SessionId,
                            WTS_INFO_CLASS.WTSClientProtocolType, ppBuffer, pBytes);
                    Pointer pBuffer = ppBuffer.getValue(); // pointer to USHORT
                    short protocolType = pBuffer.getShort(0); // 0 = console, 2 = RDP
                    assertTrue("Protocol Type must be between 0 and 2", protocolType >= 0 && protocolType <= 2);
                    Wtsapi32.INSTANCE.WTSFreeMemory(pBuffer);

                    Wtsapi32.INSTANCE.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, session.SessionId,
                            WTS_INFO_CLASS.WTSSessionInfo, ppBuffer, pBytes);
                    pBuffer = ppBuffer.getValue(); // returns WTSINFO
                    WTSINFO wtsInfo = new WTSINFO(pBuffer);
                    assertEquals("State from WTSINFO must match WTS_SESSION_INFO", session.State, wtsInfo.State);
                    assertEquals("SessionId from WTSINFO must match WTS_SESSION_INFO", session.SessionId,
                            wtsInfo.SessionId);
                    assertEquals("WinStationName from WTSINFO must match WTS_SESSION_INFO", session.pWinStationName,
                            wtsInfo.getWinStationName());
                    long logonTimeMillis = new WinBase.FILETIME(wtsInfo.LogonTime).toTime();
                    assertTrue("Logon Time can't be in the future", logonTimeMillis <= System.currentTimeMillis());
                    long currentTimeMillis = new WinBase.FILETIME(wtsInfo.CurrentTime).toTime();
                    assertTrue("Current Time must be after Logon Time", logonTimeMillis <= currentTimeMillis);
                    Wtsapi32.INSTANCE.WTSFreeMemory(pBuffer);

                    Wtsapi32.INSTANCE.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, session.SessionId,
                            WTS_INFO_CLASS.WTSClientAddress, ppBuffer, pBytes);
                    pBuffer = ppBuffer.getValue(); // returns WTS_CLIENT_ADDRESS
                    WTS_CLIENT_ADDRESS addr = new WTS_CLIENT_ADDRESS(pBuffer);
                    assertTrue("Address family must be AF_INET, AF_INET6, AF_IPX, AF_NETBIOS, or AF_UNSPEC.",
                            addr.AddressFamily == IPHlpAPI.AF_UNSPEC
                                    || addr.AddressFamily == IPHlpAPI.AF_INET
                                    || addr.AddressFamily == IPHlpAPI.AF_INET6
                                    || addr.AddressFamily == IPHlpAPI.AF_IPX
                                    || addr.AddressFamily == IPHlpAPI.AF_NETBIOS);
                    Wtsapi32.INSTANCE.WTSFreeMemory(pBuffer);
                }
            }
        }
        Wtsapi32.INSTANCE.WTSFreeMemory(pSessionInfo);
    }
}
