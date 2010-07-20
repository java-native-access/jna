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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from ShellAPI.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface ShellAPI extends StdCallLibrary {

	int STRUCTURE_ALIGNMENT = Structure.ALIGN_NONE;
	
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
