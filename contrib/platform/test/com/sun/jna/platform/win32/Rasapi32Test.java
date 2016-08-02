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

import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinRas.RASCREDENTIALS;
import com.sun.jna.platform.win32.WinRas.RASDIALPARAMS;
import com.sun.jna.platform.win32.WinRas.RASENTRY;
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
                if(AbstractWin32TestSupport.isEnglishLocale) {
                    assertEquals("An incorrect structure size was detected.", new String (msg, 0, len));
                } else {
                    System.err.println("testRasGetErrorString test can only be run with english locale.");
                }
	}

	public void testRasGetCredentials() {
		RASCREDENTIALS.ByReference credentials = new RASCREDENTIALS.ByReference();
		credentials.dwMask = WinRas.RASCM_UserName | WinRas.RASCM_Password | WinRas.RASCM_Domain;
		int err = Rasapi32.INSTANCE.RasGetCredentials(null, "TEST", credentials);
		assertEquals(623, err);
	}

	public void testRasGetEntryProperties() {
		RASENTRY.ByReference rasEntry = new RASENTRY.ByReference();
		IntByReference lpdwEntryInfoSize = new IntByReference(rasEntry.size());
		int err = Rasapi32.INSTANCE.RasGetEntryProperties(null, "TEST", rasEntry, lpdwEntryInfoSize, null, null);
		assertEquals(623, err);
	}

	public void testRasGetEntryDialParams() {
		RASDIALPARAMS.ByReference rasDialParams = new RASDIALPARAMS.ByReference();
		System.arraycopy(rasDialParams.szEntryName, 0, "TEST".toCharArray(), 0, "TEST".length());
		BOOLByReference lpfPassword = new BOOLByReference();
		int err = Rasapi32.INSTANCE.RasGetEntryDialParams(null, rasDialParams, lpfPassword);
		assertEquals(623, err);
	}
}
