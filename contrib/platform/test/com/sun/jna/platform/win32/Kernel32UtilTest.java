/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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

import static com.sun.jna.platform.win32.RegexMatcher.matches;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinNT.LOGICAL_PROCESSOR_RELATIONSHIP;

/**
 * @author dblock[at]dblock[dot]org
 * @author markus[at]headcrashing[dot]eu
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
	
	public void testDeleteFile() throws IOException {
		String filename = Kernel32Util.getTempPath() + "\\FileDoesNotExist.jna";
		File f = new File(filename);
		f.createNewFile();
		Kernel32Util.deleteFile(filename);
	}
	
	public void testGetFileAttributes() throws IOException {
		String filename = Kernel32Util.getTempPath();
		int fileAttributes = Kernel32Util.getFileAttributes(filename);
		assertEquals(WinNT.FILE_ATTRIBUTE_DIRECTORY, fileAttributes & WinNT.FILE_ATTRIBUTE_DIRECTORY);
		File tempFile = File.createTempFile("jna", "tmp");
		tempFile.deleteOnExit();
		int fileAttributes2 = Kernel32Util.getFileAttributes(tempFile.getAbsolutePath());
		tempFile.delete();
		assertEquals(0, fileAttributes2 & WinNT.FILE_ATTRIBUTE_DIRECTORY);
	}
	
    public void testGetEnvironmentVariable() {
    	assertEquals(null, Kernel32Util.getEnvironmentVariable("jna-getenvironment-test"));
    	Kernel32.INSTANCE.SetEnvironmentVariable("jna-getenvironment-test", "42");
    	assertEquals("42", Kernel32Util.getEnvironmentVariable("jna-getenvironment-test"));
    }

    public final void testGetPrivateProfileInt() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileInt", "ini");
        tmp.deleteOnExit();
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        writer.println("[Section]");
        writer.println("existingKey = 123");
        writer.close();

        assertEquals(123, Kernel32Util.getPrivateProfileInt("Section", "existingKey", 456, tmp.getCanonicalPath()));
        assertEquals(456, Kernel32Util.getPrivateProfileInt("Section", "missingKey", 456, tmp.getCanonicalPath()));
    }

    public final void testGetPrivateProfileString() throws IOException {
        final File tmp = File.createTempFile("testGetPrivateProfileString", "ini");
        tmp.deleteOnExit();
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        writer.println("[Section]");
        writer.println("existingKey = ABC");
        writer.close();

        assertEquals("ABC", Kernel32Util.getPrivateProfileString("Section", "existingKey", "DEF", tmp.getCanonicalPath()));
        assertEquals("DEF", Kernel32Util.getPrivateProfileString("Section", "missingKey", "DEF", tmp.getCanonicalPath()));
    }

    public final void testWritePrivateProfileString() throws IOException {
        final File tmp = File.createTempFile("testWritePrivateProfileString", "ini");
        tmp.deleteOnExit();
        final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
        writer.println("[Section]");
        writer.println("existingKey = ABC");
        writer.println("removedKey = JKL");
        writer.close();

        Kernel32Util.writePrivateProfileString("Section", "existingKey", "DEF", tmp.getCanonicalPath());
        Kernel32Util.writePrivateProfileString("Section", "addedKey", "GHI", tmp.getCanonicalPath());
        Kernel32Util.writePrivateProfileString("Section", "removedKey", null, tmp.getCanonicalPath());
        
        final BufferedReader reader = new BufferedReader(new FileReader(tmp));
        assertEquals(reader.readLine(), "[Section]");
        assertTrue(reader.readLine().matches("existingKey\\s*=\\s*DEF"));
        assertTrue(reader.readLine().matches("addedKey\\s*=\\s*GHI"));
        assertEquals(reader.readLine(), null);
        reader.close();
    }

    @Test
    public final void testGetPrivateProfileSection() throws IOException {
        // given
        final File tmp = File.createTempFile("testGetPrivateProfileSection"(), "ini");
        tmp.deleteOnExit();
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            try {
                writer.println("[X]");
                writer.println("A=1");
                writer.println("B=X");
            } finally {
                writer.close();
            }

            // when
            final List<String> section = Kernel32Util.getPrivateProfileSection("X", tmp.getCanonicalPath());

            // then
            assertThat(section, hasItems("A=1", "B=X"));
        } finally {
            tmp.delete();
        }
    }

    @Test
    public final void testGetPrivateProfileSectionNames() throws IOException {
        // given
        final File tmp = File.createTempFile("testGetPrivateProfileSectionNames", "ini");
        tmp.deleteOnExit();
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            try {
                writer.println("[S1]");
                writer.println("A=1");
                writer.println("B=X");
                writer.println("[S2]");
                writer.println("C=2");
                writer.println("D=Y");
            } finally {
                writer.close();
            }

            // when
            final List<String> section = Kernel32Util.getPrivateProfileSectionNames(tmp.getCanonicalPath());

            // then
            assertThat(section, hasItems("S1", "S2"));
        } finally {
            tmp.delete();
        }
    }

    @Test
    public final void testWritePrivateProfileSection() throws IOException {
        // given
        final File tmp = File.createTempFile("testWritePrivateProfileSecion", "ini");
        tmp.deleteOnExit();
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
            try {
                writer.println("[S1]");
                writer.println("A=1");
                writer.println("B=X");
                writer.println("[S2]");
                writer.println("C=2");
                writer.println("D=Y");
            } finally {
                writer.close();
            }

            // when
            Kernel32Util.writePrivateProfileSection("S1", asList("A=3", "E=Z"), tmp.getCanonicalPath());

            // then
            final BufferedReader reader = new BufferedReader(new FileReader(tmp));
            try {
                assertThat(reader.readLine(), is("[S1]"));
                assertThat(reader.readLine(), matches("A\\s*=\\s*3"));
                assertThat(reader.readLine(), matches("E\\s*=\\s*Z"));
                assertThat(reader.readLine(), is("[S2]"));
                assertThat(reader.readLine(), matches("C\\s*=\\s*2"));
                assertThat(reader.readLine(), matches("D\\s*=\\s*Y"));
                assertThat(reader.readLine(), is(nullValue()));
            } finally {
                reader.close();
            }
        } finally {
            tmp.delete();
        }
    }

    public final void testGetLogicalProcessorInformation() {
        WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] informationArray = Kernel32Util.getLogicalProcessorInformation();
        assertTrue(informationArray.length >= 1); // docs say so
        for (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION info : informationArray) {
            assertTrue(info.processorMask.intValue() >= 0);
            assertTrue(info.relationship >= LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorCore && info.relationship <= LOGICAL_PROCESSOR_RELATIONSHIP.RelationAll);
        }
    }
}
