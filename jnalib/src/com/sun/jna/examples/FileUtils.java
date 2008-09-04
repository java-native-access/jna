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
import java.util.List;
import java.util.ArrayList;
import com.sun.jna.examples.win32.Shell32;
import com.sun.jna.examples.win32.Shell32.SHFILEOPSTRUCT;

/** Miscellaneous file utils not provided for by Java. */
public abstract class FileUtils {

    public boolean hasTrash() {
        return false;
    }

    /** Move the given file to the system trash, if one is available.
        Throws an exception on failure.
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
    
    private static class W32FileUtils extends FileUtils {

        public boolean hasTrash() { return true; }

        public void moveToTrash(File[] files) throws IOException {
            Shell32 shell = Shell32.INSTANCE;
            SHFILEOPSTRUCT fileop = new SHFILEOPSTRUCT();
            fileop.wFunc = Shell32.FO_DELETE;
            String[] paths = new String[files.length];
            for (int i=0;i < paths.length;i++) {
                paths[i] = files[i].getAbsolutePath();
            }
            fileop.pFrom = fileop.encodePaths(paths);
            fileop.fFlags = Shell32.FOF_ALLOWUNDO|Shell32.FOF_NOCONFIRMATION|Shell32.FOF_SILENT;
            int ret = shell.SHFileOperation(fileop);
            if (ret != 0) {
                throw new IOException("Move to trash failed: " + ret);
            }
            if (fileop.fAnyOperationsAborted) {
                throw new IOException("Move to trash aborted");
            }
        }
    }

    private static class MacFileUtils extends FileUtils {

        public boolean hasTrash() { return true; }

        public void moveToTrash(File[] files) throws IOException {
            // TODO: use native API for moving to trash (if any)
            File home = new File(System.getProperty("user.home"));
            File trash = new File(home, ".Trash");
            if (!trash.exists()) {
                throw new IOException("The Trash was not found in its expected location (" + trash + ")");
            }
            List failed = new ArrayList();
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
            List failed = new ArrayList();
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
