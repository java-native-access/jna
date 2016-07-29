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

import com.sun.jna.platform.win32.Rasapi32Util.Ras32Exception;

/**
 * @author drrobison@openroadsconsulting.com
 */
public class Rasapi32UtilTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(Rasapi32UtilTest.class);
	}

	public void testGetRasConnection() {
		assertNull(Rasapi32Util.getRasConnection("TEST"));
	}

	public void testGetPhonebookEntry() {
		try {
			Rasapi32Util.getPhoneBookEntry("TEST");
			fail("Test expected to fail");
		} catch (Ras32Exception e) {
			assertEquals(623, e.getCode());
		}
	}

	public void testGetPhonebookDialingParams() {
		try {
			Rasapi32Util.getPhoneBookDialingParams("TEST");
		} catch (Ras32Exception e) {
			assertEquals(623, e.getCode());
		}
	}

	public void testDialEntry() {
		try {
			Rasapi32Util.dialEntry("TEST");
		} catch (Ras32Exception e) {
			assertEquals(623, e.getCode());
		}
	}

	public void testGetRasErrorString() {
                if(AbstractWin32TestSupport.isEnglishLocale) {
                    assertEquals("An incorrect structure size was detected.", Rasapi32Util.getRasErrorString(632));
                } else {
                    System.err.println("testGetRasErrorString test can only be run with english locale.");
                }
	}

	public void testGetRasConnectionStatusText() {
		assertEquals("Disconnected", Rasapi32Util.getRasConnectionStatusText(8193));
	}

	public void testhangupRasConnection() {
		Rasapi32Util.hangupRasConnection("TEST");
	}
}
