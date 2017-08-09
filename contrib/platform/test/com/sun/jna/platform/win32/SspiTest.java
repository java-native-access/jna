
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
