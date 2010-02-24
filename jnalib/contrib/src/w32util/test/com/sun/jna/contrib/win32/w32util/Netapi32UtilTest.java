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
package com.sun.jna.contrib.win32.w32util;

import junit.framework.TestCase;

public class Netapi32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Netapi32UtilTest.class);
    }
    
	public void testGetDomain() {
		String computerName = System.getenv("COMPUTERNAME");
		String domain = Netapi32Util.getDomainName(computerName);
		assertTrue(domain.length() > 0);
	}
	
	public void testGetLocalGroups() {
		String[] localGroups = Netapi32Util.getLocalGroups();
		int totalLength = 0;
		for(String localGroup : localGroups) {
			totalLength += localGroup.length();
		}
		assertTrue(totalLength / localGroups.length > 1);
		assertNotNull(localGroups);
		assertTrue(localGroups.length > 0);
	}
}
