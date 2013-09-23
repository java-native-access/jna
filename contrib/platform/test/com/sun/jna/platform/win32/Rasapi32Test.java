/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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

import junit.framework.TestCase;

import com.sun.jna.ptr.IntByReference;

/**
 * @author drrobison@openroadsconsulting.com
 */
public class Rasapi32Test extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(Rasapi32Test.class);
	}

	public void testRasEnumConnections() {
		IntByReference lpcb  = new IntByReference(0);
		IntByReference lpcConnections  = new IntByReference(0);
		int err = Rasapi32.INSTANCE.RasEnumConnections(null, lpcb, lpcConnections);
		if (err != WinError.ERROR_SUCCESS) assertEquals(WinRas.ERROR_BUFFER_TOO_SMALL, err);
		assertEquals(0, lpcConnections.getValue());
	}

	public void testRasGetErrorString() {
		char[] msg = new char[1024];
		assertEquals(W32Errors.ERROR_SUCCESS, Rasapi32.INSTANCE.RasGetErrorString(632, msg, msg.length));
		int len = 0;
		for (; len < msg.length; len++) if (msg[len] == 0) break;
		assertEquals("An incorrect structure size was detected.", new String (msg, 0, len));
	}
}
