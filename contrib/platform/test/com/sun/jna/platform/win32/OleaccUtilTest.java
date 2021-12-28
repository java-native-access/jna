/* Copyright (c) 2021 Mo Beigi, All Rights Reserved
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

import org.junit.Test;


import static com.sun.jna.platform.win32.Oleacc.ROLE_SYSTEM_TITLEBAR;
import static org.junit.Assert.assertEquals;

public class OleaccUtilTest
{
    @Test
    public void testGetRoleText() {
        String roleText = OleaccUtil.GetRoleText(ROLE_SYSTEM_TITLEBAR);
        assertEquals("title bar", roleText);
    }

    @Test(expected = Win32Exception.class)
    public void testGetRoleTextInvalidRoleThrowsException() {
        OleaccUtil.GetRoleText(-1);
    }
}
