package com.sun.jna.platform.win32;

import java.awt.Frame;

import junit.framework.TestCase;
import sun.awt.windows.WComponentPeer;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;

public class Wtsapi32Test extends TestCase {

	private long peer = 0;

	private HWND hwnd;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(Wtsapi32Test.class);
	}

	public Wtsapi32Test() {
		Frame frame = new Frame();
		frame.setVisible(true);
		this.peer = ((WComponentPeer) frame.getPeer()).getHWnd();
		this.hwnd = new HWND(new Pointer(peer));
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
