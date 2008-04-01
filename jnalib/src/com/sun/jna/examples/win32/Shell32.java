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
package com.sun.jna.examples.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/** Mapping for w32 Shell API.  
 * Note that the C header "shellapi.h" includes "pshpack1.h", which disables 
 * automatic alignment of structure fields.  
 */
public interface Shell32 extends W32API {

    /** Custom alignment of structures. */
    int STRUCTURE_ALIGNMENT = Structure.ALIGN_NONE;
    Shell32 INSTANCE = (Shell32)
        Native.loadLibrary("shell32", Shell32.class, DEFAULT_OPTIONS);
    
    int FO_MOVE = 1;
    int FO_COPY = 2;
    int FO_DELETE = 3;
    int FO_RENAME = 4;
    
    int FOF_MULTIDESTFILES = 1;
    int FOF_CONFIRMMOUSE = 2;
    int FOF_SILENT = 4;
    int FOF_RENAMEONCOLLISION = 8;
    int FOF_NOCONFIRMATION = 16;
    int FOF_WANTMAPPINGHANDLE = 32;
    int FOF_ALLOWUNDO = 64;
    int FOF_FILESONLY = 128;
    int FOF_SIMPLEPROGRESS = 256;
    int FOF_NOCONFIRMMKDIR = 512;
    int FOF_NOERRORUI = 1024;
    int FOF_NOCOPYSECURITYATTRIBS = 2048;

    class SHFILEOPSTRUCT extends Structure {
        public HANDLE hwnd;
        public int wFunc;
        public String pFrom;
        public String pTo;
        public short fFlags;
        public boolean fAnyOperationsAborted;
        public Pointer pNameMappings;
        public String lpszProgressTitle;
        /** Use this to encode <code>pFrom/pTo</code> paths. */
        public String encodePaths(String[] paths) {
            String encoded = "";
            for (int i=0;i < paths.length;i++) {
                encoded += paths[i];
                encoded += "\0";
            }
            return encoded + "\0";
        }
    }
    int SHFileOperation(SHFILEOPSTRUCT fileop);
}
