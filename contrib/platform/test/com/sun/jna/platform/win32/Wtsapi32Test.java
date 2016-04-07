package com.sun.jna.platform.win32;

import java.awt.Frame;

import com.sun.jna.Native;
import junit.framework.TestCase;

import com.sun.jna.platform.win32.WinDef.HWND;

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
}
