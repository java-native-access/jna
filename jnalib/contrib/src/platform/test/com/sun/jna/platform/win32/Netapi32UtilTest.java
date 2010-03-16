/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

public class Netapi32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Netapi32UtilTest.class);
        System.out.println("Domain: " + Netapi32Util.getDomainName("localhost"));
		// server local groups
		Netapi32Util.LocalGroup[] localGroups = Netapi32Util.getLocalGroups();
        System.out.println("Local groups: " + localGroups.length);
		for(Netapi32Util.LocalGroup localGroup : localGroups) {
			System.out.println(" " + localGroup.name + " (" + localGroup.comment + ")");
		}
		// global groups
		Netapi32Util.Group[] groups = Netapi32Util.getGlobalGroups();
        System.out.println("Global groups: " + groups.length);
		for(Netapi32Util.Group group : groups) {
			System.out.println(" " + group.name);
		}
		// server users
		Netapi32Util.User[] users = Netapi32Util.getUsers();
        System.out.println("Users: " + users.length);
		for(Netapi32Util.User user : users) {
			System.out.println(" " + user.name);
		}
		// user local groups
		Netapi32Util.Group[] userLocalGroups = Netapi32Util.getCurrentUserLocalGroups();
        System.out.println("Local user groups: " + userLocalGroups.length);
		for(Netapi32Util.Group localGroup : userLocalGroups) {
			System.out.println(" " + localGroup.name);
		}
		// user domain groups
		Netapi32Util.Group[] userGroups = Netapi32Util.getCurrentUserGroups();
        System.out.println("Domain user groups: " + userGroups.length);
		for(Netapi32Util.Group group : userGroups) {
			System.out.println(" " + group.name);
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
		for(Netapi32Util.LocalGroup localGroup : localGroups) {
			assertTrue(localGroup.name.length() > 0);
		}
		assertTrue(localGroups.length > 0);
	}

	public void testGetUsers() {
		Netapi32Util.User[] users = Netapi32Util.getUsers();
		assertNotNull(users);
		for(Netapi32Util.User user : users) {
			assertTrue(user.name.length() > 0);
		}
		assertTrue(users.length > 0);
	}
	
	public void testGetGlobalGroups() {
		Netapi32Util.Group[] groups = Netapi32Util.getGlobalGroups();
		assertNotNull(groups);
		for(Netapi32Util.Group group : groups) {
			assertTrue(group.name.length() > 0);
		}
		assertTrue(groups.length > 0);
	}
	
	public void testGetCurrentUserLocalGroups() {
		Netapi32Util.Group[] localGroups = Netapi32Util.getCurrentUserLocalGroups();
		assertNotNull(localGroups);
		for(Netapi32Util.Group localGroup : localGroups) {
			assertTrue(localGroup.name.length() > 0);
		}
		assertTrue(localGroups.length > 0);
	}

	public void testGetCurrentUserGroups() {
		Netapi32Util.Group[] groups = Netapi32Util.getCurrentUserGroups();
		assertNotNull(groups);
		for(Netapi32Util.Group group : groups) {
			assertTrue(group.name.length() > 0);
		}
		assertTrue(groups.length > 0);
	}
	
	public void testGetJoinStatus() {
		int joinStatus = Netapi32Util.getJoinStatus();
		assertTrue(joinStatus == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName
				|| joinStatus == LMJoin.NETSETUP_JOIN_STATUS.NetSetupUnjoined
				|| joinStatus == LMJoin.NETSETUP_JOIN_STATUS.NetSetupWorkgroupName);
	}
	
	public void testGetDCName() {
		if (Netapi32Util.getJoinStatus() == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
			String domainController = Netapi32Util.getDCName();
			assertTrue(domainController.length() > 0);
		}
	}
}
