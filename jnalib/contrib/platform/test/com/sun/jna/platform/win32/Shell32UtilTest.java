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

/**
 * @author dblock[at]dblock[dot]org
 */
public class Shell32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Shell32UtilTest.class);
        System.out.println("Windows: " + Shell32Util.getFolderPath(ShlObj.CSIDL_WINDOWS));
        System.out.println(" System: " + Shell32Util.getFolderPath(ShlObj.CSIDL_SYSTEM));
        System.out.println("AppData: " + Shell32Util.getFolderPath(ShlObj.CSIDL_APPDATA));
    }
    
	public void testGetFolderPath() {
		assertTrue(Shell32Util.getFolderPath(ShlObj.CSIDL_WINDOWS).length() > 0);
	}
}
