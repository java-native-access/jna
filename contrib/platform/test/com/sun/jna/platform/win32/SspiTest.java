/*
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

import com.sun.jna.platform.win32.Sspi.SEC_WINNT_AUTH_IDENTITY;
import junit.framework.TestCase;

public class SspiTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SspiTest.class);
    }

    public void testSecWinntAuthIdentity() {
        String username = "sample Username";
        String password = ""; // empty string
        String domain = "German Umlauts: \u00c4, \u00f6, \u00dc";
        SEC_WINNT_AUTH_IDENTITY identity = new SEC_WINNT_AUTH_IDENTITY();
        identity.User = username;
        identity.Password = password;
        identity.Domain = domain;
        identity.write();
        assertEquals(username.length(), identity.UserLength);
        assertEquals(password.length(), identity.PasswordLength);
        assertEquals(domain.length(), identity.DomainLength);
        assertEquals(Sspi.SEC_WINNT_AUTH_IDENTITY_UNICODE, identity.Flags);
    }
}
