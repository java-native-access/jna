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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from ShellAPI.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface ShellAPI extends StdCallLibrary {

    int STRUCTURE_ALIGNMENT = Platform.is64Bit() ? Structure.ALIGN_DEFAULT : Structure.ALIGN_NONE;
	
    int FO_MOVE = 0x0001;
    int FO_COPY = 0x0002;
    int FO_DELETE = 0x0003;
    int FO_RENAME = 0x0004;
    
    int FOF_MULTIDESTFILES = 0x0001;
    int FOF_CONFIRMMOUSE = 0x0002;
    int FOF_SILENT = 0x0004; // don't display progress UI (confirm prompts may be displayed still)
    int FOF_RENAMEONCOLLISION = 0x0008; // automatically rename the source files to avoid the collisions
    int FOF_NOCONFIRMATION = 0x0010; // don't display confirmation UI, assume "yes" for cases that can be bypassed, "no" for those that can not
    int FOF_WANTMAPPINGHANDLE = 0x0020; // Fill in SHFILEOPSTRUCT.hNameMappings
    int FOF_ALLOWUNDO = 0x0040; // enable undo including Recycle behavior for IFileOperation::Delete()
    int FOF_FILESONLY = 0x0080; // only operate on the files (non folders), both files and folders are assumed without this
    int FOF_SIMPLEPROGRESS = 0x0100; // means don't show names of files
    int FOF_NOCONFIRMMKDIR = 0x0200; // don't dispplay confirmatino UI before making any needed directories, assume "Yes" in these cases
    int FOF_NOERRORUI = 0x0400; // don't put up error UI, other UI may be displayed, progress, confirmations
    int FOF_NOCOPYSECURITYATTRIBS = 0x0800; // dont copy file security attributes (ACLs)
    int FOF_NORECURSION = 0x1000; // don't recurse into directories for operations that would recurse
    int FOF_NO_CONNECTED_ELEMENTS = 0x2000; // don't operate on connected elements ("xxx_files" folders that go with .htm files)
    int FOF_WANTNUKEWARNING = 0x4000; // during delete operation, warn if nuking instead of recycling (partially overrides FOF_NOCONFIRMATION)
    int FOF_NORECURSEREPARSE = 0x8000; // deprecated; the operations engine always does the right thing on FolderLink objects (symlinks, reparse points, folder shortcuts)
    int FOF_NO_UI = (FOF_SILENT | FOF_NOCONFIRMATION | FOF_NOERRORUI | FOF_NOCONFIRMMKDIR); // don't display any UI at all
	  
    int PO_DELETE = 0x0013; // printer is being deleted
    int PO_RENAME = 0x0014; // printer is being renamed
    int PO_PORTCHANGE = 0x0020; // port this printer connected to is being changed
    int PO_REN_PORT = 0x0034; // PO_RENAME and PO_PORTCHANGE at same time.

    /**
     * Contains information that the SHFileOperation function uses to perform file operations. 
     */
    public static class SHFILEOPSTRUCT extends Structure {
        /**
         * A window handle to the dialog box to display information about 
         * the status of the file operation.
         */
        public HANDLE hwnd;
        /**
         * An FO_* value that indicates which operation to perform.
         */
        public int wFunc;
        /**
         * A pointer to one or more source file names, double null-terminated. 
         */
        public WString pFrom;
        /**
         * A pointer to the destination file or directory name.
         */
        public WString pTo;
        /**
         * Flags that control the file operation.
         */
        public short fFlags;
        /**
         * When the function returns, this member contains TRUE if any file operations 
         * were aborted before they were completed; otherwise, FALSE. An operation can 
         * be manually aborted by the user through UI or it can be silently aborted by 
         * the system if the FOF_NOERRORUI or FOF_NOCONFIRMATION flags were set.
         */
        public boolean fAnyOperationsAborted;
        /**
         * When the function returns, this member contains a handle to a name mapping 
         * object that contains the old and new names of the renamed files. This member 
         * is used only if the fFlags member includes the FOF_WANTMAPPINGHANDLE flag. 
         */
        public Pointer pNameMappings;
        /**
         * A pointer to the title of a progress dialog box. This is a null-terminated string. 
         */
        public WString lpszProgressTitle;
        
        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "hwnd", "wFunc", "pFrom", "pTo", "fFlags", "fAnyOperationsAborted", "pNameMappings", "lpszProgressTitle" });
        }

        /** Use this to encode <code>pFrom/pTo</code> paths. */
        public String encodePaths(String[] paths) {
            String encoded = "";
            for (int i=0; i < paths.length;i++) {
                encoded += paths[i];
                encoded += "\0";
            }
            return encoded + "\0";
        }
        
        
    }
    
    /** 
     * Appbar message value to send. This parameter can be one of the following
     * values.
     */
    int ABM_NEW = 0x00000000;
    /**
     * Registers a new appbar and specifies the message identifier that the
     * system should use to send notification messages to the appbar.
     */
    int ABM_REMOVE = 0x00000001;
    /** Unregisters an appbar, removing the bar from the system's internal list.*/
    int ABM_QUERYPOS = 0x00000002;
    /** Requests a size and screen position for an appbar. */
    int ABM_SETPOS = 0x00000003;
    /** Sets the size and screen position of an appbar. */
    int ABM_GETSTATE = 0x00000004;
    /** Retrieves the autohide and always-on-top states of the Windows taskbar. */
    int ABM_GETTASKBARPOS = 0x00000005;
    /**
     * Retrieves the bounding rectangle of the Windows taskbar. Note that this
     * applies only to the system taskbar. Other objects, particularly toolbars
     * supplied with third-party software, also can be present. As a result,
     * some of the screen area not covered by the Windows taskbar might not be
     * visible to the user. To retrieve the area of the screen not covered by
     * both the taskbar and other app bars -- the working area available to your
     * application --, use the GetMonitorInfo function.
     */
    int ABM_ACTIVATE = 0x00000006;
    /**
     * Notifies the system to activate or deactivate an appbar. The lParam
     * member of the APPBARDATA pointed to by pData is set to TRUE to activate
     * or FALSE to deactivate.
     */
    int ABM_GETAUTOHIDEBAR = 0x00000007;
    /**
     *  Retrieves the handle to the autohide appbar associated with a particular
     * edge of the screen.
     */
    int ABM_SETAUTOHIDEBAR = 0x00000008;
    /** Registers or unregisters an autohide appbar for an edge of the screen. */
    int ABM_WINDOWPOSCHANGED = 0x00000009;
    /** Notifies the system when an appbar's position has changed. */
    int ABM_SETSTATE = 0x0000000A;

    /** Left edge. */
    int ABE_LEFT = 0;
    /** Top edge. */
    int ABE_TOP = 1; 
    /** Right edge. */
    int ABE_RIGHT = 2; 
    /** Bottom edge. */
    int ABE_BOTTOM = 3; 

    /**
     * Contains information about a system appbar message.
     */
    public static class APPBARDATA extends Structure {
        public static class ByReference extends APPBARDATA implements Structure.ByReference {
        }

        public DWORD cbSize;
        public HWND hWnd;
        public UINT uCallbackMessage;
        public UINT uEdge;
        public RECT rc;
        public LPARAM lParam;

        public APPBARDATA() {
        	super();
		}

        public APPBARDATA(Pointer p) {
        	super(p);
        }

        @Override
        protected List getFieldOrder() {
        	return Arrays.asList("cbSize", "hWnd", "uCallbackMessage", "uEdge",	"rc", "lParam");
        }
    }

}
