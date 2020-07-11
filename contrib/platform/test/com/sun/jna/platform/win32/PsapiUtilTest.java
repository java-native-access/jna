/* Copyright (c) 2020 Torbjörn Svensson, All Rights Reserved
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Applies API tests on {@link PsapiUtil}.
 *
 * @author Torbj&ouml;rn Svensson, azoff[at]svenskalinuxforeningen.se
 */
@SuppressWarnings("nls")
public class PsapiUtilTest {
    @Test
    public void enumProcesses() {
        int[] pids = PsapiUtil.enumProcesses();
        assertNotNull("List should not be null", pids);

        int myPid = Kernel32.INSTANCE.GetCurrentProcessId();
        boolean foundMyPid = false;
        for (int i = 0; i < pids.length; i++) {
            if (pids[i] == myPid) {
                foundMyPid = true;
                break;
            }
        }
        assertTrue("List should contain my pid", foundMyPid);
    }
}
