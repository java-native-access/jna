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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;

public interface Shell32 extends StdCallLibrary {

    Shell32 INSTANCE = (Shell32)Native.loadLibrary("shell32", Shell32.class);
    
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

    public static class SHFILEOPSTRUCTW extends Structure {
        public int hwnd;
        public int wFunc;
        public Pointer pFrom;
        public Pointer pTo;
        public short fFlags;
        public int fAnyOperationsAborted;
        public Pointer pNameMappings;
        public WString lpszProgressTitle;
        public Pointer encodePaths(String[] paths) {
            int size = 0;
            for (int i=0;i < paths.length;i++) {
                size += paths[i].length() + 1;
            }
            ++size;
            Memory m = new Memory(size*2);
            char[] buf = new char[size];
            int offset = 0;
            for (int i=0;i < paths.length;i++) {
                char[] from = paths[i].toCharArray();
                System.arraycopy(from, 0, buf, offset, from.length);
                offset += from.length;
                buf[offset++] = 0;
            }
            buf[offset++] = 0;
            m.write(0, buf, 0, size);
            return m;
        }
    }
    int SHFileOperationW(SHFILEOPSTRUCTW fileop);
}
