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
public class Win32ExceptionTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Win32ExceptionTest.class);
    }

	public void testFormatMessageFromLastErrorCode() {
		try {
			throw new Win32Exception(W32Errors.ERROR_SHARING_PAUSED);
		} catch (Win32Exception e) {
		    assertEquals("The remote server has been paused or is in the process of being started.",
		    		e.getMessage());			
		}
	}

	public void testFormatMessageFromHR() {
		try {
			throw new Win32Exception(W32Errors.S_OK);
		} catch (Win32Exception e) {
			assertEquals("The operation completed successfully.", 
					e.getMessage());
		}
	}
}
