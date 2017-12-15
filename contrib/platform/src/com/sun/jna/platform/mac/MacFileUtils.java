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
package com.sun.jna.platform.mac;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.platform.FileUtils;

public class MacFileUtils extends FileUtils {

    @Override
    public boolean hasTrash() { return true; }

    public interface FileManager extends Library {

        FileManager INSTANCE = Native.loadLibrary("CoreServices", FileManager.class);

        int kFSFileOperationDefaultOptions = 0;
        int kFSFileOperationsOverwrite = 0x01;
        int kFSFileOperationsSkipSourcePermissionErrors = 0x02;
        int kFSFileOperationsDoNotMoveAcrossVolumes = 0x04;
        int kFSFileOperationsSkipPreflight = 0x08;

        int kFSPathDefaultOptions = 0x0;
        int kFSPathMakeRefDoNotFollowLeafSymlink = 0x01;

        class FSRef extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("hidden");
            public byte[] hidden = new byte[80];

            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        // Deprecated; use trashItemAtURL instead:
        // https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSFileManager_Class/Reference/Reference.html#//apple_ref/occ/instm/NSFileManager/trashItemAtURL:resultingItemURL:error:
        int FSRefMakePath(FSRef fsref, byte[] path, int maxPathSize);
        int FSPathMakeRef(String source, int options, ByteByReference isDirectory);
        int FSPathMakeRefWithOptions(String source, int options, FSRef fsref, ByteByReference isDirectory);
        int FSPathMoveObjectToTrashSync(String source, PointerByReference target, int options);
        int FSMoveObjectToTrashSync(FSRef source, FSRef target, int options);
    }

    @Override
    public void moveToTrash(File[] files) throws IOException {
        List<String> failed = new ArrayList<String>();
        for (File src: files) {
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
