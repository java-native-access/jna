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
package w32util;

public class Netapi32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Netapi32UtilTest.class);
    }
    
	public void testGetDomain() {
		String computerName = System.getenv("COMPUTERNAME");
		System.out.println(computerName);
		String domain = Netapi32Util.GetDomainName(computerName);
		System.out.println(domain);
		assertTrue(domain.length() > 0);
	}
}
