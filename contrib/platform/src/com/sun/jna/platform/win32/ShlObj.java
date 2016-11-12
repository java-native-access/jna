/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.platform.win32.WinDef.DWORD;

/**
 * Ported from ShlObj.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface ShlObj {
	
    DWORD SHGFP_TYPE_CURRENT = new DWORD(0); // current value for user, verify it exists
    DWORD SHGFP_TYPE_DEFAULT = new DWORD(1); // default value, may not exist
	
    int CSIDL_DESKTOP = 0x0000; // <desktop>
    int CSIDL_INTERNET = 0x0001; // Internet Explorer (icon on desktop)
    int CSIDL_PROGRAMS = 0x0002; // Start Menu\Programs
    int CSIDL_CONTROLS = 0x0003; // My Computer\Control Panel
    int CSIDL_PRINTERS = 0x0004; // My Computer\Printers
    int CSIDL_PERSONAL = 0x0005; // My Documents
    int CSIDL_FAVORITES = 0x0006; // <user name>\Favorites
    int CSIDL_STARTUP = 0x0007; // Start Menu\Programs\Startup
    int CSIDL_RECENT = 0x0008; // <user name>\Recent
    int CSIDL_SENDTO = 0x0009; // <user name>\SendTo
    int CSIDL_BITBUCKET = 0x000a; // <desktop>\Recycle Bin
    int CSIDL_STARTMENU = 0x000b; // <user name>\Start Menu
    int CSIDL_MYDOCUMENTS = CSIDL_PERSONAL; // Personal was just a silly name for My Documents
    int CSIDL_MYMUSIC = 0x000d; // "My Music" folder
    int CSIDL_MYVIDEO = 0x000e; // "My Videos" folder
    int CSIDL_DESKTOPDIRECTORY = 0x0010; // <user name>\Desktop
    int CSIDL_DRIVES = 0x0011; // My Computer
    int CSIDL_NETWORK = 0x0012; // Network Neighborhood (My Network Places)
    int CSIDL_NETHOOD = 0x0013; // <user name>\nethood
    int CSIDL_FONTS = 0x0014; // windows\fonts
    int CSIDL_TEMPLATES = 0x0015;
    int CSIDL_COMMON_STARTMENU = 0x0016; // All Users\Start Menu
    int CSIDL_COMMON_PROGRAMS = 0X0017; // All Users\Start Menu\Programs
    int CSIDL_COMMON_STARTUP = 0x0018; // All Users\Startup
    int CSIDL_COMMON_DESKTOPDIRECTORY = 0x0019; // All Users\Desktop
    int CSIDL_APPDATA = 0x001a; // <user name>\Application Data
    int CSIDL_PRINTHOOD = 0x001b; // <user name>\PrintHood
    int CSIDL_LOCAL_APPDATA = 0x001c; // <user name>\Local Settings\Applicaiton Data (non roaming)
    int CSIDL_ALTSTARTUP = 0x001d; // non localized startup
    int CSIDL_COMMON_ALTSTARTUP = 0x001e; // non localized common startup
    int CSIDL_COMMON_FAVORITES = 0x001f;
    int CSIDL_INTERNET_CACHE = 0x0020;
    int CSIDL_COOKIES = 0x0021;
    int CSIDL_HISTORY = 0x0022;
    int CSIDL_COMMON_APPDATA = 0x0023; // All Users\Application Data
    int CSIDL_WINDOWS = 0x0024; // GetWindowsDirectory()
    int CSIDL_SYSTEM = 0x0025; // GetSystemDirectory()
    int CSIDL_PROGRAM_FILES = 0x0026; // C:\Program Files
    int CSIDL_MYPICTURES = 0x0027; // C:\Program Files\My Pictures
    int CSIDL_PROFILE = 0x0028; // USERPROFILE
    int CSIDL_SYSTEMX86 = 0x0029; // x86 system directory on RISC
    int CSIDL_PROGRAM_FILESX86 = 0x002a; // x86 C:\Program Files on RISC
    int CSIDL_PROGRAM_FILES_COMMON = 0x002b; // C:\Program Files\Common
    int CSIDL_PROGRAM_FILES_COMMONX86 = 0x002c; // x86 Program Files\Common on RISC
    int CSIDL_COMMON_TEMPLATES = 0x002d; // All Users\Templates
    int CSIDL_COMMON_DOCUMENTS = 0x002e; // All Users\Documents
    int CSIDL_COMMON_ADMINTOOLS = 0x002f; // All Users\Start Menu\Programs\Administrative Tools
    int CSIDL_ADMINTOOLS = 0x0030; // <user name>\Start Menu\Programs\Administrative Tools
    int CSIDL_CONNECTIONS = 0x0031; // Network and Dial-up Connections
    int CSIDL_COMMON_MUSIC = 0x0035; // All Users\My Music
    int CSIDL_COMMON_PICTURES = 0x0036; // All Users\My Pictures
    int CSIDL_COMMON_VIDEO = 0x0037; // All Users\My Video
    int CSIDL_RESOURCES = 0x0038; // Resource Direcotry
    int CSIDL_RESOURCES_LOCALIZED = 0x0039; // Localized Resource Direcotry
    int CSIDL_COMMON_OEM_LINKS = 0x003a; // Links to All Users OEM specific apps
    int CSIDL_CDBURN_AREA = 0x003b; // USERPROFILE\Local Settings\Application Data\Microsoft\CD Burning
    int CSIDL_COMPUTERSNEARME = 0x003d; // Computers Near Me (computered from Workgroup membership)

    /**
     * KnownFolder flags as used by SHGetKnownFolderPath, SHGetKnownFolderIDList and others.
     * Microsoft Windows SDK 7.0A.
     */
    public enum KNOWN_FOLDER_FLAG
    {
        /**
         * None
         */
        NONE(0x00000000),

        /**
         * Build a simple IDList (PIDL) This value can be used when you want to retrieve the file system path but do not
         * specify this value if you are retrieving the localized display name of the folder because it might not
         * resolve correctly.
         */
        SIMPLE_IDLIST(0x00000100),

        /**
         * Gets the folder's default path independent of the current location of its parent. KF_FLAG_DEFAULT_PATH must
         * also be set.
         */
        NOT_PARENT_RELATIVE(0x00000200),

        /**
         * Gets the default path for a known folder. If this flag is not set, the function retrieves the current-and
         * possibly redirected-path of the folder. The execution of this flag includes a verification of the folder's
         * existence unless KF_FLAG_DONT_VERIFY is set.
         */
        DEFAULT_PATH(0x00000400),

        /**
         * Initializes the folder using its Desktop.ini settings. If the folder cannot be initialized, the function
         * returns a failure code and no path is returned. This flag should always be combined with KF_FLAG_CREATE.
         */
        INIT(0x00000800),

        /**
         * Gets the true system path for the folder, free of any aliased placeholders such as %USERPROFILE%, returned by
         * SHGetKnownFolderIDList and IKnownFolder::GetIDList. This flag has no effect on paths returned by
         * SHGetKnownFolderPath and IKnownFolder::GetPath. By default, known folder retrieval functions and methods
         * return the aliased path if an alias exists.
         */
        NO_ALIAS(0x00001000),

        /**
         * Stores the full path in the registry without using environment strings. If this flag is not set, portions of
         * the path may be represented by environment strings such as %USERPROFILE%. This flag can only be used with
         * SHSetKnownFolderPath and IKnownFolder::SetPath.
         */
        DONT_UNEXPAND(0x00002000),

        /**
         * Do not verify the folder's existence before attempting to retrieve the path or IDList. If this flag is not
         * set, an attempt is made to verify that the folder is truly present at the path. If that verification fails
         * due to the folder being absent or inaccessible, the function returns a failure code and no path is returned.
         * If the folder is located on a network, the function might take a longer time to execute. Setting this flag
         * can reduce that lag time.
         */
        DONT_VERIFY(0x00004000),

        /**
         * Forces the creation of the specified folder if that folder does not already exist. The security provisions
         * predefined for that folder are applied. If the folder does not exist and cannot be created, the function
         * returns a failure code and no path is returned. This value can be used only with the following functions and
         * methods: 
         * <ul>
         * <li>SHGetKnownFolderPath</li> 
         * <li>SHGetKnownFolderIDList</li> 
         * <li>IKnownFolder::GetIDList</li>
         * <li>IKnownFolder::GetPath</li> 
         * <li>IKnownFolder::GetShellItem</li>
         * </ul>
         */
        CREATE(0x00008000),
        /**
         * Introduced in Windows 7: When running inside an app container, or when providing an app container token, this
         * flag prevents redirection to app container folders. Instead, it retrieves the path that would be returned
         * where it not running inside an app container.
         */
        NO_APPCONTAINER_REDIRECTION(0x00010000),

        /**
         * Introduced in Windows 7. Return only aliased PIDLs. Do not use the file system path.
         */
        ALIAS_ONLY(0x80000000);

        private int flag;

        KNOWN_FOLDER_FLAG(int flag)
        {
            this.flag = flag;
        }

        public int getFlag()
        {
            return flag;
        }
    }
}
