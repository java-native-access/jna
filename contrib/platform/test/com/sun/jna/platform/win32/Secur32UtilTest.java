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

import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.Secur32Util.SecurityPackage;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Secur32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Secur32UtilTest.class);
        System.out.println("Current user: " + Secur32Util.getUserNameEx(
                EXTENDED_NAME_FORMAT.NameSamCompatible));
        System.out.println("Security packages:");
        for (SecurityPackage sp : Secur32Util.getSecurityPackages()) {
            System.out.println(" " + sp.name + ": " + sp.comment);
        }
    }

    public void testGetUsernameEx() {
        String usernameSamCompatible = Secur32Util.getUserNameEx(
                EXTENDED_NAME_FORMAT.NameSamCompatible);
        assertTrue(usernameSamCompatible.length() > 1);
        assertTrue(usernameSamCompatible.indexOf('\\') > 0);
    }

    public void testGetSecurityPackages() {
        SecurityPackage[] sps = Secur32Util.getSecurityPackages();
        for (SecurityPackage sp : sps) {
            assertTrue(sp.name.length() > 0);
            assertTrue(sp.comment.length() >= 0);
        }
    }
}
