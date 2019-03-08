/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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
        if (AbstractWin32TestSupport.isEnglishLocale) {
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
