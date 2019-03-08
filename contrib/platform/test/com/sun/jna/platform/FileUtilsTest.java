/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
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
