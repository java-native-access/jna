/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform;

import com.sun.jna.Platform;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {
    
    public void testMoveToTrash() throws Exception {
        FileUtils utils = FileUtils.getInstance();
        if (!utils.hasTrash()) 
            return;

        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        try {
            assertTrue("Original source file missing: " + file, file.exists());
            try {
                utils.moveToTrash(new File[] { file });
            }
            catch(IOException e) {
                fail(e.toString());
            }
            assertFalse("File still exists after move to trash: " + file, file.exists());
        }
        finally {
            file.delete();
        }
    }
    
    public void testMoveSymlinkToTrash() throws Exception {
        if (Platform.isWindows()) {
            return;
        }
        FileUtils utils = FileUtils.getInstance();
        if (!utils.hasTrash()) 
            return;

        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        File symlink = new File(tmpdir, file.getName() + ".link");
        try {
            Runtime.getRuntime().exec(new String[] { "ln", "-s", file.getAbsolutePath(), symlink.getAbsolutePath() });
            // OSX 10.8 needs a little time for the symlink to register
            long start = System.currentTimeMillis();
            while (!file.exists() || !symlink.exists()) {
                Thread.sleep(100);
                if (System.currentTimeMillis() - start > 5000) {
                    break;
                }
            }
            assertTrue("Original source file missing: " + file, file.exists());
            assertTrue("Symlink creation failed (missing): " + symlink, symlink.exists());
            try {
                utils.moveToTrash(new File[] { symlink });
            }
            catch(IOException e) {
                fail(e.toString());
            }
            assertFalse("Symlink still exists after move to trash: " + symlink, symlink.exists());
            assertTrue("Original file should still exist after move to trash: " + file, file.exists());
        }
        finally {
            symlink.delete();
            file.delete();
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FileUtilsTest.class);
    }
}
