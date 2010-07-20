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

import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Kernel32UtilTest extends TestCase {
	
    public static void main(String[] args) throws Exception {
        System.out.println("Computer name: " + Kernel32Util.getComputerName());
        System.out.println("Temp path: " + Kernel32Util.getTempPath());
        // logical drives
        System.out.println("Logical drives: ");
		String[] logicalDrives = Kernel32Util.getLogicalDriveStrings();
		for(String logicalDrive : logicalDrives) {
			// drive type
			System.out.println(" " + logicalDrive + " (" 
					+ Kernel32.INSTANCE.GetDriveType(logicalDrive) + ")");
			// free space
	    	LARGE_INTEGER.ByReference lpFreeBytesAvailable = new LARGE_INTEGER.ByReference(); 
	    	LARGE_INTEGER.ByReference lpTotalNumberOfBytes = new LARGE_INTEGER.ByReference(); 
	    	LARGE_INTEGER.ByReference lpTotalNumberOfFreeBytes = new LARGE_INTEGER.ByReference(); 
	    	if (Kernel32.INSTANCE.GetDiskFreeSpaceEx(logicalDrive, lpFreeBytesAvailable, lpTotalNumberOfBytes, lpTotalNumberOfFreeBytes)) {
		    	System.out.println("  Total: " + formatBytes(lpTotalNumberOfBytes.getValue()));
		    	System.out.println("   Free: " + formatBytes(lpTotalNumberOfFreeBytes.getValue()));
	    	}
		}

		junit.textui.TestRunner.run(Kernel32UtilTest.class);
    }

	/**
	 * Format bytes.
	 * @param bytes
	 *  Bytes.
	 * @return
	 *  Rounded string representation of the byte size.
	 */
    private static String formatBytes(long bytes) {
		if (bytes == 1) { // bytes
			return String.format("%d byte", bytes);
		} else if (bytes < 1024) { // bytes
			return String.format("%d bytes", bytes);
		} else if (bytes < 1048576 && bytes % 1024 == 0) { // Kb
			return String.format("%.0f KB", (double) bytes / 1024);
		} else if (bytes < 1048576) { // Kb
			return String.format("%.1f KB", (double) bytes / 1024);
		} else if (bytes % 1048576 == 0 && bytes < 1073741824) { // Mb
			return String.format("%.0f MB", (double) bytes / 1048576);
		} else if (bytes < 1073741824) { // Mb
			return String.format("%.1f MB", (double) bytes / 1048576);
		} else if (bytes % 1073741824 == 0 && bytes < 1099511627776L) { // GB
			return String.format("%.0f GB", (double) bytes / 1073741824);
		} else if (bytes < 1099511627776L ) {
			return String.format("%.1f GB", (double) bytes / 1073741824);
		} else if (bytes % 1099511627776L == 0 && bytes < 1125899906842624L) { // TB
			return String.format("%.0f TB", (double) bytes / 1099511627776L);
		} else if (bytes < 1125899906842624L ) {
			return String.format("%.1f TB", (double) bytes / 1099511627776L);
		} else {
			return String.format("%d bytes", bytes);
		}
    }
    
	public void testGetComputerName() {
		assertTrue(Kernel32Util.getComputerName().length() > 0);
	}
	
	public void testFormatMessageFromLastErrorCode() {
	    assertEquals("The remote server has been paused or is in the process of being started.",
	    		Kernel32Util.formatMessageFromLastErrorCode(W32Errors.ERROR_SHARING_PAUSED));	
	}

	public void testFormatMessageFromHR() {
		assertEquals("The operation completed successfully.",
				Kernel32Util.formatMessageFromHR(W32Errors.S_OK));
	}
	
	public void testGetTempPath() {
		assertTrue(Kernel32Util.getTempPath().length() > 0);
	}
	
	public void testGetLogicalDriveStrings() {
		String[] logicalDrives = Kernel32Util.getLogicalDriveStrings();
		assertTrue(logicalDrives.length > 0);
		for(String logicalDrive : logicalDrives) {
			assertTrue(logicalDrive.length() > 0);
		}
	}
}
