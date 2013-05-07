/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
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
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.platform.FileUtils;

public class MacFileUtils extends FileUtils {

    public boolean hasTrash() { return true; }

    public interface FileManager extends Library {

        public FileManager INSTANCE = (FileManager)Native.loadLibrary("CoreServices", FileManager.class);

        int kFSFileOperationDefaultOptions = 0;
        int kFSFileOperationsOverwrite = 0x01;
        int kFSFileOperationsSkipSourcePermissionErrors = 0x02;
        int kFSFileOperationsDoNotMoveAcrossVolumes = 0x04;
        int kFSFileOperationsSkipPreflight = 0x08;

        int kFSPathDefaultOptions = 0x0;
        int kFSPathMakeRefDoNotFollowLeafSymlink = 0x01;

        class FSRef extends Structure {
            public byte[] hidden = new byte[80];
            protected List getFieldOrder() { return Arrays.asList(new String[] { "hidden" }); }
        }

        // Deprecated; use trashItemAtURL instead:
        // https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSFileManager_Class/Reference/Reference.html#//apple_ref/occ/instm/NSFileManager/trashItemAtURL:resultingItemURL:error: 
        int FSRefMakePath(FSRef fsref, byte[] path, int maxPathSize);
        int FSPathMakeRef(String source, int options, ByteByReference isDirectory);
        int FSPathMakeRefWithOptions(String source, int options, FSRef fsref, ByteByReference isDirectory);
        int FSPathMoveObjectToTrashSync(String source, PointerByReference target, int options);
        int FSMoveObjectToTrashSync(FSRef source, FSRef target, int options);
    }

    public void moveToTrash(File[] files) throws IOException {
        File home = new File(System.getProperty("user.home"));
        File trash = new File(home, ".Trash");
        if (!trash.exists()) {
            throw new IOException("The Trash was not found in its expected location (" + trash + ")");
        }
        List<String> failed = new ArrayList<String>();
        for (int i=0;i < files.length;i++) {
            File src = files[i];
            FileManager.FSRef fsref = new FileManager.FSRef();
            int status = FileManager.INSTANCE.FSPathMakeRefWithOptions(src.getAbsolutePath(), 
                                                                       FileManager.kFSPathMakeRefDoNotFollowLeafSymlink,
                                                                       fsref, null);
            if (status != 0) {
                failed.add(src + " (FSRef: " + status + ")");
                continue;
            }
            status = FileManager.INSTANCE.FSMoveObjectToTrashSync(fsref, null, 0);
            if (status != 0) {
                failed.add(src + " (" + status + ")");
            }
        }
        if (failed.size() > 0) {
            throw new IOException("The following files could not be trashed: " + failed);
        }
    }
}
