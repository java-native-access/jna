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
package com.sun.jna.platform.win32;

import java.io.File;
import java.io.IOException;

import com.sun.jna.platform.FileUtils;

public class W32FileUtils extends FileUtils {

    public boolean hasTrash() { 
    	return true; 
    }

    public void moveToTrash(File[] files) throws IOException {
        Shell32 shell = Shell32.INSTANCE;
        ShellAPI.SHFILEOPSTRUCT fileop = new ShellAPI.SHFILEOPSTRUCT();
        fileop.wFunc = ShellAPI.FO_DELETE;
        String[] paths = new String[files.length];
        for (int i=0;i < paths.length;i++) {
            paths[i] = files[i].getAbsolutePath();
        }
        fileop.pFrom = fileop.encodePaths(paths);
        fileop.fFlags = ShellAPI.FOF_ALLOWUNDO|ShellAPI.FOF_NO_UI;
        int ret = shell.SHFileOperation(fileop);
        if (ret != 0) {
            throw new IOException("Move to trash failed: " + fileop.pFrom + ": " + 
                                  Kernel32Util.formatMessageFromLastErrorCode(ret));
        }
        if (fileop.fAnyOperationsAborted) {
            throw new IOException("Move to trash aborted");
        }
    }
}
