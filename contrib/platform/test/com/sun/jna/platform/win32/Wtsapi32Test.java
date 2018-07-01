package com.sun.jna.platform.win32;

import java.awt.Frame;
import java.util.HashSet;
import java.util.Set;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.Wtsapi32.WTS_PROCESS_INFO_EX;
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
        int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;

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

        Set<Integer> pidSet = new HashSet<Integer>();
        for (WTS_PROCESS_INFO_EX procInfo : processInfo) {
            // PIDs should be unique
            int pid = procInfo.ProcessId.intValue();
            assertTrue(pid >= 0);
            if (pid > 0) {
                assertFalse(pidSet.contains(pid));
                pidSet.add(pid);
            }

            // A process cannot time travel and use negative CPU ticks
            assertTrue(procInfo.KernelTime.getValue() >= 0);
            assertTrue(procInfo.UserTime.getValue() >= 0);

            // Process name should be nonempty except for the Idle process
            String name;
            if (charToBytes > 1) {
                name = procInfo.pProcessName.getWideString(0);
            } else {
                name = procInfo.pProcessName.getString(0);
            }
            if (pid > 0) {
                assertFalse(name.isEmpty());
            }

        }
        // Clean up memory allocated in C
        assertTrue(
                Wtsapi32.INSTANCE.WTSFreeMemoryEx(Wtsapi32.WTS_PROCESS_INFO_LEVEL_1, pProcessInfo, pCount.getValue()));

    }
}
