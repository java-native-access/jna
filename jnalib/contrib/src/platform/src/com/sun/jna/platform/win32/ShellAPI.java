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
package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.W32API.HANDLE;

/**
 * Ported from ShellAPI.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public abstract class ShellAPI {
	
    public static final int FO_MOVE = 0x0001;
    public static final int FO_COPY = 0x0002;
    public static final int FO_DELETE = 0x0003;
    public static final int FO_RENAME = 0x0004;
    
	public static final int FOF_MULTIDESTFILES = 0x0001;
	public static final int FOF_CONFIRMMOUSE = 0x0002;
	public static final int FOF_SILENT = 0x0004; // don't display progress UI (confirm prompts may be displayed still)
	public static final int FOF_RENAMEONCOLLISION = 0x0008; // automatically rename the source files to avoid the collisions
	public static final int FOF_NOCONFIRMATION = 0x0010; // don't display confirmation UI, assume "yes" for cases that can be bypassed, "no" for those that can not
	public static final int FOF_WANTMAPPINGHANDLE = 0x0020; // Fill in SHFILEOPSTRUCT.hNameMappings
	public static final int FOF_ALLOWUNDO = 0x0040; // enable undo including Recycle behavior for IFileOperation::Delete()
	public static final int FOF_FILESONLY = 0x0080; // only operate on the files (non folders), both files and folders are assumed without this
	public static final int FOF_SIMPLEPROGRESS = 0x0100; // means don't show names of files
	public static final int FOF_NOCONFIRMMKDIR = 0x0200; // don't dispplay confirmatino UI before making any needed directories, assume "Yes" in these cases
	public static final int FOF_NOERRORUI = 0x0400; // don't put up error UI, other UI may be displayed, progress, confirmations
	public static final int FOF_NOCOPYSECURITYATTRIBS = 0x0800; // dont copy file security attributes (ACLs)
	public static final int FOF_NORECURSION = 0x1000; // don't recurse into directories for operations that would recurse
	public static final int FOF_NO_CONNECTED_ELEMENTS = 0x2000; // don't operate on connected elements ("xxx_files" folders that go with .htm files)
	public static final int FOF_WANTNUKEWARNING = 0x4000; // during delete operation, warn if nuking instead of recycling (partially overrides FOF_NOCONFIRMATION)
	public static final int FOF_NORECURSEREPARSE = 0x8000; // deprecated; the operations engine always does the right thing on FolderLink objects (symlinks, reparse points, folder shortcuts)
	public static final int FOF_NO_UI = (FOF_SILENT | FOF_NOCONFIRMATION | FOF_NOERRORUI | FOF_NOCONFIRMMKDIR); // don't display any UI at all
	  
	public static final int PO_DELETE = 0x0013; // printer is being deleted
	public static final int PO_RENAME = 0x0014; // printer is being renamed
	public static final int PO_PORTCHANGE = 0x0020; // port this printer connected to is being changed
	public static final int PO_REN_PORT = 0x0034; // PO_RENAME and PO_PORTCHANGE at same time.

	public static class SHFILEOPSTRUCT extends Structure {
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
			for (int i=0; i < paths.length;i++) {
				encoded += paths[i];
				encoded += "\0";
			}
			return encoded + "\0";
		}
	}
}
