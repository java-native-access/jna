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

    DWORD SHGFP_TYPE_CURRENT  = new DWORD(0);   // current value for user, verify it exists
    DWORD SHGFP_TYPE_DEFAULT  = new DWORD(1);   // default value, may not exist
    int CSIDL_DESKTOP                 = 0x0000;        // <desktop>
    int CSIDL_INTERNET                = 0x0001;        // Internet Explorer (icon on desktop)
    int CSIDL_PROGRAMS                = 0x0002;        // Start Menu\Programs
    int CSIDL_CONTROLS                = 0x0003;        // My Computer\Control Panel
    int CSIDL_PRINTERS                = 0x0004;        // My Computer\Printers
    int CSIDL_PERSONAL                = 0x0005;        // My Documents
    int CSIDL_FAVORITES               = 0x0006;        // <user name>\Favorites
    int CSIDL_STARTUP                 = 0x0007;        // Start Menu\Programs\Startup
    int CSIDL_RECENT                  = 0x0008;        // <user name>\Recent
    int CSIDL_SENDTO                  = 0x0009;        // <user name>\SendTo
    int CSIDL_BITBUCKET               = 0x000a;        // <desktop>\Recycle Bin
    int CSIDL_STARTMENU               = 0x000b;        // <user name>\Start Menu
    int CSIDL_MYDOCUMENTS             = CSIDL_PERSONAL; //  Personal was just a silly name for My Documents
    int CSIDL_MYMUSIC                 = 0x000d;        // "My Music" folder
    int CSIDL_MYVIDEO                 = 0x000e;        // "My Videos" folder
    int CSIDL_DESKTOPDIRECTORY        = 0x0010;        // <user name>\Desktop
    int CSIDL_DRIVES                  = 0x0011;        // My Computer
    int CSIDL_NETWORK                 = 0x0012;        // Network Neighborhood (My Network Places)
    int CSIDL_NETHOOD                 = 0x0013;        // <user name>\nethood
    int CSIDL_FONTS                   = 0x0014;        // windows\fonts
    int CSIDL_TEMPLATES               = 0x0015;
    int CSIDL_COMMON_STARTMENU        = 0x0016;        // All Users\Start Menu
    int CSIDL_COMMON_PROGRAMS         = 0X0017;        // All Users\Start Menu\Programs
    int CSIDL_COMMON_STARTUP          = 0x0018;        // All Users\Startup
    int CSIDL_COMMON_DESKTOPDIRECTORY = 0x0019;        // All Users\Desktop
    int CSIDL_APPDATA                 = 0x001a;        // <user name>\Application Data
    int CSIDL_PRINTHOOD               = 0x001b;        // <user name>\PrintHood
    int CSIDL_LOCAL_APPDATA           = 0x001c;        // <user name>\Local Settings\Applicaiton Data (non roaming)
    int CSIDL_ALTSTARTUP              = 0x001d;        // non localized startup
    int CSIDL_COMMON_ALTSTARTUP       = 0x001e;        // non localized common startup
    int CSIDL_COMMON_FAVORITES        = 0x001f;
    int CSIDL_INTERNET_CACHE          = 0x0020;
    int CSIDL_COOKIES                 = 0x0021;
    int CSIDL_HISTORY                 = 0x0022;
    int CSIDL_COMMON_APPDATA          = 0x0023;        // All Users\Application Data
    int CSIDL_WINDOWS                 = 0x0024;        // GetWindowsDirectory()
    int CSIDL_SYSTEM                  = 0x0025;        // GetSystemDirectory()
    int CSIDL_PROGRAM_FILES           = 0x0026;        // C:\Program Files
    int CSIDL_MYPICTURES              = 0x0027;        // C:\Program Files\My Pictures
    int CSIDL_PROFILE                 = 0x0028;        // USERPROFILE
    int CSIDL_SYSTEMX86               = 0x0029;        // x86 system directory on RISC
    int CSIDL_PROGRAM_FILESX86        = 0x002a;        // x86 C:\Program Files on RISC
    int CSIDL_PROGRAM_FILES_COMMON    = 0x002b;        // C:\Program Files\Common
    int CSIDL_PROGRAM_FILES_COMMONX86 = 0x002c;        // x86 Program Files\Common on RISC
    int CSIDL_COMMON_TEMPLATES        = 0x002d;        // All Users\Templates
    int CSIDL_COMMON_DOCUMENTS        = 0x002e;        // All Users\Documents
    int CSIDL_COMMON_ADMINTOOLS       = 0x002f;        // All Users\Start Menu\Programs\Administrative Tools
    int CSIDL_ADMINTOOLS              = 0x0030;        // <user name>\Start Menu\Programs\Administrative Tools
    int CSIDL_CONNECTIONS             = 0x0031;        // Network and Dial-up Connections
    int CSIDL_COMMON_MUSIC            = 0x0035;        // All Users\My Music
    int CSIDL_COMMON_PICTURES         = 0x0036;        // All Users\My Pictures
    int CSIDL_COMMON_VIDEO            = 0x0037;        // All Users\My Video
    int CSIDL_RESOURCES               = 0x0038;        // Resource Direcotry
    int CSIDL_RESOURCES_LOCALIZED     = 0x0039;        // Localized Resource Direcotry
    int CSIDL_COMMON_OEM_LINKS        = 0x003a;        // Links to All Users OEM specific apps
    int CSIDL_CDBURN_AREA             = 0x003b;        // USERPROFILE\Local Settings\Application Data\Microsoft\CD Burning
    int CSIDL_COMPUTERSNEARME         = 0x003d;        // Computers Near Me (computered from Workgroup membership)
    HRESULT SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken, DWORD dwFlags, char[] pszPath);
}
