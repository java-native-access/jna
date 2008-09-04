/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.examples;

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {
    
    public void testMoveToTrash() throws Exception {
        FileUtils utils = FileUtils.getInstance();
        if (!utils.hasTrash()) 
            return;

        File home = new File(System.getProperty("user.home"));
        File file = File.createTempFile(getName(), ".tmp", home);
        try {
            assertTrue("File should exist", file.exists());
            try {
                utils.moveToTrash(new File[] { file });
            }
            catch(IOException e) {
                fail(e.toString());
            }
            assertFalse("Failed to move " + file + " to trash", file.exists());
        }
        finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FileUtilsTest.class);
    }
}
