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
import com.sun.jna.examples.win32.Shell32;
import com.sun.jna.examples.win32.Shell32.SHFILEOPSTRUCTW;

/** Miscellaneous file utils not provided for by Java. */
public abstract class FileUtils {

    /** Move the given file to the system trash, if one is available.
        Returns whether the operation was successful.
    */
    public abstract boolean moveToTrash(File[] files);

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
        public boolean moveToTrash(File[] files) {
            Shell32 shell = Shell32.INSTANCE;
            SHFILEOPSTRUCTW fileop = new SHFILEOPSTRUCTW();
            fileop.wFunc = Shell32.FO_DELETE;
            String[] paths = new String[files.length];
            for (int i=0;i < paths.length;i++) {
                paths[i] = files[i].getAbsolutePath();
            }
            fileop.pFrom = fileop.encodePaths(paths);
            fileop.fFlags = Shell32.FOF_ALLOWUNDO|Shell32.FOF_NOCONFIRMATION|Shell32.FOF_SILENT;
            return shell.SHFileOperationW(fileop) == 0;
        }
    }

    private static class MacFileUtils extends FileUtils {
        public boolean moveToTrash(File[] files) {
            // TODO: account for stuff not on the same volume
            // Also should suffix file by time moved to trash
            File home = new File(System.getProperty("user.home"));
            File trash = new File(home, ".Trash");
            if (trash.exists()) {
                boolean success = true;
                for (int i=0;i < files.length;i++) {
                    File src = files[i];
                    File target = new File(trash, src.getName());
                    if (!src.renameTo(target)) {
                        success = false;
                    }
                }
                return success;
            }
            return false;
        }
    }
    
    private static class DefaultFileUtils extends FileUtils {
        /** The default implementation attempts to move the file to 
         * the desktop "Trash" folder.
         */
        public boolean moveToTrash(File[] files) {
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
                        }
                    }
                }
            }
            if (trash.exists()) {
                boolean success = true;
                for (int i=0;i < files.length;i++) {
                    File src = files[i];
                    File target = new File(trash, src.getName());
                    if (!src.renameTo(target))
                        success = false;
                }
                return success;
            }
            return false;
        }
    }
}
