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

import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinRas.RASCREDENTIALS;
import com.sun.jna.platform.win32.WinRas.RASDIALPARAMS;
import com.sun.jna.platform.win32.WinRas.RASENTRY;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32StringUtil;

/**
 * @author drrobison@openroadsconsulting.com
 */
public class Rasapi32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Rasapi32Test.class);
    }

    public void testRasEnumConnections() {
        IntByReference lpcb = new IntByReference(0);
        IntByReference lpcConnections = new IntByReference(0);
        int err = Rasapi32.INSTANCE.RasEnumConnections(null, lpcb, lpcConnections);
        if (err != WinError.ERROR_SUCCESS) {
            assertEquals(WinRas.ERROR_BUFFER_TOO_SMALL, err);
        }
        assertEquals(0, lpcConnections.getValue());
    }

    public void testRasGetErrorString() {
        char[] msg = new char[1024];
        assertEquals(W32Errors.ERROR_SUCCESS, Rasapi32.INSTANCE.RasGetErrorString(632, msg, msg.length));
        if (AbstractWin32TestSupport.isEnglishLocale) {
            assertEquals("An incorrect structure size was detected.", W32StringUtil.toString(msg));
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
        W32StringUtil.setString("TEST", rasDialParams.szEntryName, 0);
        BOOLByReference lpfPassword = new BOOLByReference();
        int err = Rasapi32.INSTANCE.RasGetEntryDialParams(null, rasDialParams, lpfPassword);
        assertEquals(623, err);
    }
}
