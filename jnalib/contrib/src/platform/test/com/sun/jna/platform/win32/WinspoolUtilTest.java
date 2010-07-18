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

import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WinspoolUtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Shell32UtilTest.class);
        for(PRINTER_INFO_1 printerInfo : WinspoolUtil.getPrinterInfo1()) {
            System.out.println(printerInfo.pName + ": " + printerInfo.pDescription);        	
        }
    }
    
	public void testGetFolderPath() {
		assertTrue(WinspoolUtil.getPrinterInfo1().length > 0);
	}
}
