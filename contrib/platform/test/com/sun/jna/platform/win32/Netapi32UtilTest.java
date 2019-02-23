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

import com.sun.jna.platform.win32.Netapi32Util.DomainController;
import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;
import com.sun.jna.platform.win32.Netapi32Util.UserInfo;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Netapi32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Netapi32UtilTest.class);
        System.out.println("Domain: " + Netapi32Util.getDomainName("localhost"));
        // server local groups
        Netapi32Util.LocalGroup[] localGroups = Netapi32Util.getLocalGroups();
        System.out.println("Local groups: " + localGroups.length);
        for (Netapi32Util.LocalGroup localGroup : localGroups) {
            System.out.println(" " + localGroup.name + " (" + localGroup.comment + ")");
        }
        // global groups
        Netapi32Util.Group[] groups = Netapi32Util.getGlobalGroups();
        System.out.println("Global groups: " + groups.length);
        for (Netapi32Util.Group group : groups) {
            System.out.println(" " + group.name);
        }
        // server users
        Netapi32Util.User[] users = Netapi32Util.getUsers();
        System.out.println("Users: " + users.length);
        for (Netapi32Util.User user : users) {
            System.out.println(" " + user.name);
        }
        // user local groups
        Netapi32Util.Group[] userLocalGroups = Netapi32Util.getCurrentUserLocalGroups();
        System.out.println("Local user groups: " + userLocalGroups.length);
        for (Netapi32Util.Group localGroup : userLocalGroups) {
            System.out.println(" " + localGroup.name);
        }
        // domain controller
        if (Netapi32Util.getJoinStatus() == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            System.out.println("Pdc: " + Netapi32Util.getDCName());
            DomainController dc = Netapi32Util.getDC();
            System.out.println("Domain controller:");
            System.out.println("    name: " + dc.name);
            System.out.println(" address: " + dc.address);
            System.out.println("  domain: " + dc.domainName);
            System.out.println("    site: " + dc.clientSiteName);
            System.out.println("  forest: " + dc.dnsForestName);
            System.out.println("    guid: " + Ole32Util.getStringFromGUID(dc.domainGuid));
        }
        // domain trusts
        if (Netapi32Util.getJoinStatus() == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            DomainTrust[] trusts = Netapi32Util.getDomainTrusts();
            System.out.println("Domain trusts: (" + trusts.length + ")");
            for (DomainTrust trust : trusts) {
                System.out.println(" " + trust.NetbiosDomainName + ": " + trust.DnsDomainName
                        + " (" + trust.DomainSidString + ")");
            }
        }
    }

    public void testGetDomain() {
        String computerName = System.getenv("COMPUTERNAME");
        String domain = Netapi32Util.getDomainName(computerName);
        assertTrue(domain.length() > 0);
    }

    public void testGetLocalGroups() {
        Netapi32Util.LocalGroup[] localGroups = Netapi32Util.getLocalGroups();
        assertNotNull(localGroups);
        for (Netapi32Util.LocalGroup localGroup : localGroups) {
            assertTrue(localGroup.name.length() > 0);
        }
        assertTrue(localGroups.length > 0);
    }

    public void testGetUsers() {
        Netapi32Util.User[] users = Netapi32Util.getUsers();
        assertNotNull(users);
        for (Netapi32Util.User user : users) {
            assertTrue(user.name.length() > 0);
        }
        assertTrue(users.length > 0);
    }

    public void testGetUserInfo() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }
        assertNotNull(Netapi32Util.getUserInfo(Advapi32Util.getUserName()));
    }

    public void testGetUserInfoWithDomainSpecified() {
        UserInfo userInfo = Netapi32Util.getUserInfo(Advapi32Util.getUserName(), System.getenv("USERDOMAIN"));
        assertNotNull("No user info retrieved", userInfo);
        assertTrue("Invalid user SID", Advapi32.INSTANCE.IsValidSid(userInfo.sid));
    }

    public void testGetGlobalGroups() {
        Netapi32Util.Group[] groups = Netapi32Util.getGlobalGroups();
        assertNotNull(groups);
        for (Netapi32Util.Group group : groups) {
            assertTrue(group.name.length() > 0);
        }
        assertTrue(groups.length > 0);
    }

    public void testGetCurrentUserLocalGroups() {
        Netapi32Util.Group[] localGroups = Netapi32Util.getCurrentUserLocalGroups();
        assertNotNull(localGroups);
        for (Netapi32Util.Group localGroup : localGroups) {
            assertTrue(localGroup.name.length() > 0);
        }
        assertTrue(localGroups.length > 0);
    }

    public void testGetJoinStatus() {
        int joinStatus = Netapi32Util.getJoinStatus();
        assertTrue(joinStatus == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName
                || joinStatus == LMJoin.NETSETUP_JOIN_STATUS.NetSetupUnjoined
                || joinStatus == LMJoin.NETSETUP_JOIN_STATUS.NetSetupWorkgroupName);
    }

    public void testGetDCName() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }

        String domainController = Netapi32Util.getDCName();
        assertTrue(domainController.length() > 0);
        assertTrue(domainController.startsWith("\\\\"));
    }

    public void testGetDC() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }

        DomainController dc = Netapi32Util.getDC();
        assertTrue("Invalid address prefix: " + dc.address, dc.address.startsWith("\\\\"));
        assertTrue("Empty domain name", dc.domainName.length() > 0);
    }

    public void testGetDomainTrusts() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }

        DomainTrust[] trusts = Netapi32Util.getDomainTrusts();
        assertTrue(trusts.length >= 0);
        for (DomainTrust trust : trusts) {
            assertTrue(trust.NetbiosDomainName.length() > 0);
            assertTrue(trust.DnsDomainName.length() > 0);
            assertTrue(Advapi32.INSTANCE.IsValidSid(trust.DomainSid));
            assertTrue(trust.isInbound() || trust.isOutbound() || trust.isPrimary());
        }
    }
}
