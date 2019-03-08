/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.platform.win32.WinReg.HKEYByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NtDllUtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NtDllUtilTest.class);
    }

    public void testGetKeyName() {
        HKEYByReference phKey = new HKEYByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
        // Keys are case insensitive (https://msdn.microsoft.com/de-de/library/windows/desktop/ms724946(v=vs.85).aspx)
        assertEquals("software", NtDllUtil.getKeyName(phKey.getValue()).toLowerCase());
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }
}
