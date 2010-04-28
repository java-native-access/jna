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

import com.sun.jna.platform.FileUtils;

public class MacFileUtils extends FileUtils {

    public boolean hasTrash() { return true; }

    public void moveToTrash(File[] files) throws IOException {
        // TODO: use native API for moving to trash (if any)
        File home = new File(System.getProperty("user.home"));
        File trash = new File(home, ".Trash");
        if (!trash.exists()) {
            throw new IOException("The Trash was not found in its expected location (" + trash + ")");
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
