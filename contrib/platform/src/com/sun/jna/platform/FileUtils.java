/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.mac.MacFileUtils;
import com.sun.jna.platform.win32.W32FileUtils;

/** Miscellaneous file utils not provided for by Java. */
public abstract class FileUtils {

    public boolean hasTrash() {
        return false;
    }

    /** Move the given file to the system trash, if one is available.
     * @param files files to move
     * @throws IOException on failure.
     */
    public abstract void moveToTrash(File[] files) throws IOException;

    /** Canonical lazy loading of a singleton. */
    private static class Holder {
        public static final FileUtils INSTANCE;
        static {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                INSTANCE = new W32FileUtils();
            }
            else if (os.startsWith("Mac")){
                INSTANCE = new MacFileUtils();
            }
            else {
                INSTANCE = new DefaultFileUtils();
            }
        }
    }
    
    public static FileUtils getInstance() {
        return Holder.INSTANCE;
    }    
    
    private static class DefaultFileUtils extends FileUtils {

        private File getTrashDirectory() {
            // very simple implementation.  should take care of renaming when
            // a file already exists, or any other platform-specific behavior
            File home = new File(System.getProperty("user.home"));
            File trash = new File(home, ".Trash");
            if (!trash.exists()) {
                trash = new File(home, "Trash");
                if (!trash.exists()) {
                    File desktop = new File(home, "Desktop");
                    if (desktop.exists()) {
                        trash = new File(desktop, ".Trash");
                        if (!trash.exists()) {
                            trash = new File(desktop, "Trash");
                            if (!trash.exists()) {
                                trash = new File(System.getProperty("fileutils.trash", "Trash"));
                            }
                        }
                    }
                }
            }
            return trash;
        }

        public boolean hasTrash() {
            return getTrashDirectory().exists();
        }

        /** The default implementation attempts to move the file to 
         * the desktop "Trash" folder.
         */
        public void moveToTrash(File[] files) throws IOException {
            File trash = getTrashDirectory();
            if (!trash.exists()) {
                throw new IOException("No trash location found (define fileutils.trash to be the path to the trash)");
            }
            List<File> failed = new ArrayList<File>();
            for (int i=0;i < files.length;i++) {
                File src = files[i];
                File target = new File(trash, src.getName());
                if (!src.renameTo(target)) {
                    failed.add(src);
                }
            }
            if (failed.size() > 0) {
                throw new IOException("The following files could not be trashed: " + failed);
            }
        }
    }
}
