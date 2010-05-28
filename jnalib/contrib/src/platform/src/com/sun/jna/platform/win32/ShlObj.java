/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from ShlObj.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface ShlObj extends StdCallLibrary {
	
	public static final DWORD SHGFP_TYPE_CURRENT = new DWORD(0); // current value for user, verify it exists
	public static final DWORD SHGFP_TYPE_DEFAULT = new DWORD(1); // default value, may not exist
	
	public static final int CSIDL_DESKTOP = 0x0000; // <desktop>
	public static final int CSIDL_INTERNET = 0x0001; // Internet Explorer (icon on desktop)
	public static final int CSIDL_PROGRAMS = 0x0002; // Start Menu\Programs
	public static final int CSIDL_CONTROLS = 0x0003; // My Computer\Control Panel
	public static final int CSIDL_PRINTERS = 0x0004; // My Computer\Printers
	public static final int CSIDL_PERSONAL = 0x0005; // My Documents
	public static final int CSIDL_FAVORITES = 0x0006; // <user name>\Favorites
	public static final int CSIDL_STARTUP = 0x0007; // Start Menu\Programs\Startup
	public static final int CSIDL_RECENT = 0x0008; // <user name>\Recent
	public static final int CSIDL_SENDTO = 0x0009; // <user name>\SendTo
	public static final int CSIDL_BITBUCKET = 0x000a; // <desktop>\Recycle Bin
	public static final int CSIDL_STARTMENU = 0x000b; // <user name>\Start Menu
	public static final int CSIDL_MYDOCUMENTS = CSIDL_PERSONAL; // Personal was just a silly name for My Documents
	public static final int CSIDL_MYMUSIC = 0x000d; // "My Music" folder
	public static final int CSIDL_MYVIDEO = 0x000e; // "My Videos" folder
	public static final int CSIDL_DESKTOPDIRECTORY = 0x0010; // <user name>\Desktop
	public static final int CSIDL_DRIVES = 0x0011; // My Computer
	public static final int CSIDL_NETWORK = 0x0012; // Network Neighborhood (My Network Places)
	public static final int CSIDL_NETHOOD = 0x0013; // <user name>\nethood
	public static final int CSIDL_FONTS = 0x0014; // windows\fonts
	public static final int CSIDL_TEMPLATES = 0x0015;
	public static final int CSIDL_COMMON_STARTMENU = 0x0016; // All Users\Start Menu
	public static final int CSIDL_COMMON_PROGRAMS = 0X0017; // All Users\Start Menu\Programs
	public static final int CSIDL_COMMON_STARTUP = 0x0018; // All Users\Startup
	public static final int CSIDL_COMMON_DESKTOPDIRECTORY = 0x0019; // All Users\Desktop
	public static final int CSIDL_APPDATA = 0x001a; // <user name>\Application Data
	public static final int CSIDL_PRINTHOOD = 0x001b; // <user name>\PrintHood
	public static final int CSIDL_LOCAL_APPDATA = 0x001c; // <user name>\Local Settings\Applicaiton Data (non roaming)
	public static final int CSIDL_ALTSTARTUP = 0x001d; // non localized startup
	public static final int CSIDL_COMMON_ALTSTARTUP = 0x001e; // non localized common startup
	public static final int CSIDL_COMMON_FAVORITES = 0x001f;
	public static final int CSIDL_INTERNET_CACHE = 0x0020;
	public static final int CSIDL_COOKIES = 0x0021;
	public static final int CSIDL_HISTORY = 0x0022;
	public static final int CSIDL_COMMON_APPDATA = 0x0023; // All Users\Application Data
	public static final int CSIDL_WINDOWS = 0x0024; // GetWindowsDirectory()
	public static final int CSIDL_SYSTEM = 0x0025; // GetSystemDirectory()
	public static final int CSIDL_PROGRAM_FILES = 0x0026; // C:\Program Files
	public static final int CSIDL_MYPICTURES = 0x0027; // C:\Program Files\My Pictures
	public static final int CSIDL_PROFILE = 0x0028; // USERPROFILE
	public static final int CSIDL_SYSTEMX86 = 0x0029; // x86 system directory on RISC
	public static final int CSIDL_PROGRAM_FILESX86 = 0x002a; // x86 C:\Program Files on RISC
	public static final int CSIDL_PROGRAM_FILES_COMMON = 0x002b; // C:\Program Files\Common
	public static final int CSIDL_PROGRAM_FILES_COMMONX86 = 0x002c; // x86 Program Files\Common on RISC
	public static final int CSIDL_COMMON_TEMPLATES = 0x002d; // All Users\Templates
	public static final int CSIDL_COMMON_DOCUMENTS = 0x002e; // All Users\Documents
	public static final int CSIDL_COMMON_ADMINTOOLS = 0x002f; // All Users\Start Menu\Programs\Administrative Tools
	public static final int CSIDL_ADMINTOOLS = 0x0030; // <user name>\Start Menu\Programs\Administrative Tools
	public static final int CSIDL_CONNECTIONS = 0x0031; // Network and Dial-up Connections
	public static final int CSIDL_COMMON_MUSIC = 0x0035; // All Users\My Music
	public static final int CSIDL_COMMON_PICTURES = 0x0036; // All Users\My Pictures
	public static final int CSIDL_COMMON_VIDEO = 0x0037; // All Users\My Video
	public static final int CSIDL_RESOURCES = 0x0038; // Resource Direcotry
	public static final int CSIDL_RESOURCES_LOCALIZED = 0x0039; // Localized Resource Direcotry
	public static final int CSIDL_COMMON_OEM_LINKS = 0x003a; // Links to All Users OEM specific apps
	public static final int CSIDL_CDBURN_AREA = 0x003b; // USERPROFILE\Local Settings\Application Data\Microsoft\CD Burning
	public static final int CSIDL_COMPUTERSNEARME = 0x003d; // Computers Near Me (computered from Workgroup membership)
}
