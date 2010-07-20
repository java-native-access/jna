/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.platform.win32.Guid.GUID;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Ole32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Ole32UtilTest.class);
    }
    
    public void testGenerateGUID() {
    	GUID guid1 = Ole32Util.generateGUID();
    	GUID guid2 = Ole32Util.generateGUID();
    	assertTrue(guid1 != guid2);
    	assertTrue(Ole32Util.getStringFromGUID(guid1) != Ole32Util.getStringFromGUID(guid2));
    }
    
    public void testGetStringFromGUID() {
    	assertEquals("{00000000-0000-0000-0000-000000000000}", Ole32Util.getStringFromGUID(
    			new GUID()));
    	assertFalse("{00000000-0000-0000-0000-000000000000}" == Ole32Util.getStringFromGUID(
    			Ole32Util.generateGUID()));
    }
    
    public void testGetGUIDFromString() {
    	GUID lpiid = Ole32Util.getGUIDFromString("{13709620-C279-11CE-A49E-444553540000}");
    	assertEquals(0x13709620, lpiid.Data1);
    	assertEquals(0xFFFFC279, lpiid.Data2);
    	assertEquals(0x11CE, lpiid.Data3);
    	assertEquals(0xFFFFFFA4, lpiid.Data4[0]);
    	assertEquals(0xFFFFFF9E, lpiid.Data4[1]);
    	assertEquals(0x44, lpiid.Data4[2]);
    	assertEquals(0x45, lpiid.Data4[3]);
    	assertEquals(0x53, lpiid.Data4[4]);
    	assertEquals(0x54, lpiid.Data4[5]);
    	assertEquals(0, lpiid.Data4[6]);
    	assertEquals(0, lpiid.Data4[7]);
	}
    
    public void testGetGUIDToFromString() {
    	GUID guid = Ole32Util.generateGUID();
    	assertEquals(guid, Ole32Util.getGUIDFromString(
    			Ole32Util.getStringFromGUID(guid)));
    }
}
