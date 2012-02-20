/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform.mac;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.platform.FileUtils;

public class MacFileUtils extends FileUtils {

    public boolean hasTrash() { return true; }

    public interface FileManager extends Library {
        int kFSFileOperationDefaultOptions = 0;
        int kFSFileOperationsOverwrite = 0x01;
        int kFSFileOperationsSkipSourcePermissionErrors = 0x02;
        int kFSFileOperationsDoNotMoveAcrossVolumes = 0x04;
        int kFSFileOperationsSkipPreflight = 0x08;

        public FileManager INSTANCE = (FileManager)Native.loadLibrary("CoreServices", FileManager.class);
        int FSPathMoveObjectToTrashSync(String src, PointerByReference target, int options);
    }

    public void moveToTrash(File[] files) throws IOException {
        File home = new File(System.getProperty("user.home"));
        File trash = new File(home, ".Trash");
        if (!trash.exists()) {
            throw new IOException("The Trash was not found in its expected location (" + trash + ")");
        }
        List<File> failed = new ArrayList<File>();
        for (int i=0;i < files.length;i++) {
            File src = files[i];
            if (FileManager.INSTANCE.FSPathMoveObjectToTrashSync(src.getAbsolutePath(), null, 0) != 0) {
                failed.add(src);
            }
        }
        if (failed.size() > 0) {
            throw new IOException("The following files could not be trashed: " + failed);
        }
    }
}
