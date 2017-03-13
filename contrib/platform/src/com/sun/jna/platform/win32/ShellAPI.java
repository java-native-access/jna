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

import java.util.List;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.TypeMapper;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Ported from ShellAPI.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface ShellAPI extends StdCallLibrary {

    int STRUCTURE_ALIGNMENT = Platform.is64Bit() ? Structure.ALIGN_DEFAULT : Structure.ALIGN_NONE;
    TypeMapper TYPE_MAPPER = Boolean.getBoolean("w32.ascii") ? W32APITypeMapper.ASCII : W32APITypeMapper.UNICODE;

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
        public static final List<String> FIELDS = createFieldsOrder(
                "hwnd", "wFunc", "pFrom", "pTo", "fFlags", "fAnyOperationsAborted", "pNameMappings", "lpszProgressTitle");

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
        public String pFrom;
        /**
         * A pointer to the destination file or directory name.
         */
        public String pTo;
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
        public String lpszProgressTitle;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        /** Use this to encode <code>pFrom/pTo</code> paths.
         * @param paths Paths to encode
         * @return Encoded paths
         */
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

        public static final List<String> FIELDS = createFieldsOrder("cbSize", "hWnd", "uCallbackMessage", "uEdge",  "rc", "lParam");

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
        protected List<String> getFieldOrder() {
        	return FIELDS;
        }
    }

	/**
	 * <p>
	 * Contains information used by <a href=
	 * "https://msdn.microsoft.com/en-us/library/windows/desktop/bb762154(v=vs.85).aspx">
	 * <strong xmlns="http://www.w3.org/1999/xhtml">ShellExecuteEx</strong></a>.
	 * </p>
	 *
	 * <pre>
	 * <span style="color:Blue;">typedef</span> <span style="color:Blue;">struct</span> _SHELLEXECUTEINFO {
	 *   DWORD &nbsp;&nbsp;&nbsp;&nbsp;cbSize;
	 *   ULONG &nbsp;&nbsp;&nbsp;&nbsp;fMask;
	 *   HWND &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hwnd;
	 *   LPCTSTR &nbsp;&nbsp;lpVerb;
	 *   LPCTSTR &nbsp;&nbsp;lpFile;
	 *   LPCTSTR &nbsp;&nbsp;lpParameters;
	 *   LPCTSTR &nbsp;&nbsp;lpDirectory;
	 *   <span style="color:Blue;">int</span> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nShow;
	 *   HINSTANCE hInstApp;
	 *   LPVOID &nbsp;&nbsp;&nbsp;lpIDList;
	 *   LPCTSTR &nbsp;&nbsp;lpClass;
	 *   HKEY &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hkeyClass;
	 *   DWORD &nbsp;&nbsp;&nbsp;&nbsp;dwHotKey;
	 *   <span style="color:Blue;">union</span> {
	 *     HANDLE hIcon;
	 *     HANDLE hMonitor;
	 *   }&nbsp;DUMMYUNIONNAME;
	 *   HANDLE &nbsp;&nbsp;&nbsp;hProcess;
	 * } SHELLEXECUTEINFO, *LPSHELLEXECUTEINFO;
	 * </pre>
	 *
	 * <h2>Remarks</h2>
	 * <p>
	 * The <strong>SEE_MASK_NOASYNC</strong> flag must be specified if the
	 * thread calling <a href=
	 * "https://msdn.microsoft.com/en-us/library/windows/desktop/bb762154(v=vs.85).aspx">
	 * <strong xmlns="http://www.w3.org/1999/xhtml">ShellExecuteEx</strong></a>
	 * does not have a message loop or if the thread or process will terminate
	 * soon after <strong>ShellExecuteEx</strong> returns. Under such
	 * conditions, the calling thread will not be available to complete the DDE
	 * conversation, so it is important that <strong>ShellExecuteEx</strong>
	 * complete the conversation before returning control to the calling
	 * application. Failure to complete the conversation can result in an
	 * unsuccessful launch of the document.
	 * </p>
	 * <p>
	 * If the calling thread has a message loop and will exist for some time
	 * after the call to <a href=
	 * "https://msdn.microsoft.com/en-us/library/windows/desktop/bb762154(v=vs.85).aspx">
	 * <strong xmlns="http://www.w3.org/1999/xhtml">ShellExecuteEx</strong></a>
	 * returns, the <strong>SEE_MASK_NOASYNC</strong> flag is optional. If the
	 * flag is omitted, the calling thread's message pump will be used to
	 * complete the DDE conversation. The calling application regains control
	 * sooner, since the DDE conversation can be completed in the background.
	 * </p>
	 * <p>
	 * When populating the most frequently used program list using the
	 * <strong>SEE_MASK_FLAG_LOG_USAGE</strong> flag in <strong>fMask</strong>,
	 * counts are made differently for the classic and Windows&nbsp;XP-style
	 * Start menus. The classic style menu only counts hits to the shortcuts in
	 * the Program menu. The Windows&nbsp;XP-style menu counts both hits to the
	 * shortcuts in the Program menu and hits to those shortcuts' targets
	 * outside of the Program menu. Therefore, setting <strong>lpFile</strong>
	 * to myfile.exe would affect the count for the Windows&nbsp;XP-style menu
	 * regardless of whether that file was launched directly or through a
	 * shortcut. The classic style-which would require <strong>lpFile</strong>
	 * to contain a .lnk file name-would not be affected.
	 * </p>
	 * <p>
	 * To include double quotation marks in <strong>lpParameters</strong>,
	 * enclose each mark in a pair of quotation marks, as in the following
	 * example.
	 * </p>
         * <p>
	 * <pre>
	 * sei.lpParameters = &quot;An example: \&quot;\&quot;\&quot;quoted text\&quot;\&quot;\&quot;&quot;;
	 * </pre>
         * </p>
	 * <p>
	 * In this case, the application receives three parameters: <em>An</em>,
	 * <em>example:</em>, and <em>"quoted text"</em>.
	 * </p>
	 */
	public class SHELLEXECUTEINFO extends Structure {
	    public static final List<String> FIELDS = createFieldsOrder("cbSize", "fMask", "hwnd", "lpVerb", "lpFile", "lpParameters",
                "lpDirectory", "nShow", "hInstApp", "lpIDList", "lpClass", "hKeyClass", "dwHotKey", "hMonitor",
                "hProcess");
		/**
		 * <p>
		 * Type: <strong>DWORD</strong>
		 * </p>
		 * <p>
		 * Required. The size of this structure, in bytes.
		 * </p>
		 */
		public int cbSize = size();

		/**
		 * <p>
		 * Type: <strong>ULONG</strong>
		 * </p>
		 * <p>
		 * Flags that indicate the content and validity of the other structure
		 * members; a combination of the following values:
		 * </p>
		 * <dl class="indent">
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_DEFAULT</strong> (0x00000000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use default values.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_CLASSNAME</strong> (0x00000001)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use the class name given by the <strong>lpClass</strong> member. If
		 * both SEE_MASK_CLASSKEY and SEE_MASK_CLASSNAME are set, the class key
		 * is used.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_CLASSKEY</strong> (0x00000003)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use the class key given by the <strong>hkeyClass</strong> member. If
		 * both SEE_MASK_CLASSKEY and SEE_MASK_CLASSNAME are set, the class key
		 * is used.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_IDLIST</strong> (0x00000004)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use the item identifier list given by the <strong>lpIDList</strong>
		 * member. The <strong>lpIDList</strong> member must point to an
		 * structure.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_INVOKEIDLIST</strong> (0x0000000C)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use the interface of the selected item's . Use either
		 * <strong>lpFile</strong> to identify the item by its file system path
		 * or <strong>lpIDList</strong> to identify the item by its PIDL. This
		 * flag allows applications to use to invoke verbs from shortcut menu
		 * extensions instead of the static verbs listed in the registry.
		 * </p>
		 * <div class="note"><strong>Note</strong>
		 * &nbsp;&nbsp;SEE_MASK_INVOKEIDLIST overrides and implies
		 * SEE_MASK_IDLIST.</div></dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_ICON</strong> (0x00000010)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use the icon given by the <strong>hIcon</strong> member. This flag
		 * cannot be combined with SEE_MASK_HMONITOR.
		 * </p>
		 * <div class="note"><strong>Note</strong>&nbsp;&nbsp;This flag is used
		 * only in Windows&nbsp;XP and earlier. It is ignored as of
		 * Windows&nbsp;Vista.</div></dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_HOTKEY</strong> (0x00000020)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use the keyboard shortcut given by the <strong>dwHotKey</strong>
		 * member.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_NOCLOSEPROCESS</strong> (0x00000040)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use to indicate that the <strong>hProcess</strong> member receives
		 * the process handle. This handle is typically used to allow an
		 * application to find out when a process created with terminates. In
		 * some cases, such as when execution is satisfied through a DDE
		 * conversation, no handle will be returned. The calling application is
		 * responsible for closing the handle when it is no longer needed.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_CONNECTNETDRV</strong> (0x00000080)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Validate the share and connect to a drive letter. This enables
		 * reconnection of disconnected network drives. The
		 * <strong>lpFile</strong> member is a UNC path of a file on a network.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_NOASYNC</strong> (0x00000100)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Wait for the execute operation to complete before returning. This
		 * flag should be used by callers that are using ShellExecute forms that
		 * might result in an async activation, for example DDE, and create a
		 * process that might be run on a background thread. (Note: runs on a
		 * background thread by default if the caller's threading model is not
		 * Apartment.) Calls to <strong>ShellExecuteEx</strong> from processes
		 * already running on background threads should always pass this flag.
		 * Also, applications that exit immediately after calling
		 * <strong>ShellExecuteEx</strong> should specify this flag.
		 * </p>
		 * <p>
		 * If the execute operation is performed on a background thread and the
		 * caller did not specify the SEE_MASK_ASYNCOK flag, then the calling
		 * thread waits until the new process has started before returning. This
		 * typically means that either has been called, the DDE communication
		 * has completed, or that the custom execution delegate has notified
		 * that it is done. If the SEE_MASK_WAITFORINPUTIDLE flag is specified,
		 * then <strong>ShellExecuteEx</strong> calls and waits for the new
		 * process to idle before returning, with a maximum timeout of 1 minute.
		 * </p>
		 * <p>
		 * For further discussion on when this flag is necessary, see the
		 * Remarks section.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_FLAG_DDEWAIT</strong> (0x00000100)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Do not use; use SEE_MASK_NOASYNC instead.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_DOENVSUBST</strong> (0x00000200)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Expand any environment variables specified in the string given by the
		 * <strong>lpDirectory</strong> or <strong>lpFile</strong> member.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_FLAG_NO_UI</strong> (0x00000400)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Do not display an error message box if an error occurs.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_UNICODE</strong> (0x00004000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use this flag to indicate a Unicode application.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_NO_CONSOLE</strong> (0x00008000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use to inherit the parent's console for the new process instead of
		 * having it create a new console. It is the opposite of using a
		 * CREATE_NEW_CONSOLE flag with .
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_ASYNCOK</strong> (0x00100000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * The execution can be performed on a background thread and the call
		 * should return immediately without waiting for the background thread
		 * to finish. Note that in certain cases ignores this flag and waits for
		 * the process to finish before returning.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_NOQUERYCLASSSTORE</strong> (0x01000000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Not used.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_HMONITOR</strong> (0x00200000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Use this flag when specifying a monitor on multi-monitor systems. The
		 * monitor is specified in the <strong>hMonitor</strong> member. This
		 * flag cannot be combined with SEE_MASK_ICON.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_NOZONECHECKS</strong> (0x00800000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * <strong>Introduced in Windows&nbsp;XP</strong>. Do not perform a zone
		 * check. This flag allows to bypass zone checking put into place by .
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_WAITFORINPUTIDLE</strong> (0x02000000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * After the new process is created, wait for the process to become idle
		 * before returning, with a one minute timeout. See for more details.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_FLAG_LOG_USAGE</strong> (0x04000000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * <strong>Introduced in Windows&nbsp;XP</strong>. Keep track of the
		 * number of times this application has been launched. Applications with
		 * sufficiently high counts appear in the Start Menu's list of most
		 * frequently used programs.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SEE_MASK_FLAG_HINST_IS_SITE</strong> (0x08000000)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * <strong>Introduced in Windows&nbsp;8</strong>. The
		 * <strong>hInstApp</strong> member is used to specify the of an object
		 * that implements . This object will be used as a site pointer. The
		 * site pointer is used to provide services to the function, the handler
		 * binding process, and invoked verb handlers.
		 * </p>
		 * </dd>
		 * </dl>
		 */
		public int fMask;

		/**
		 * <p>
		 * Type: <strong>HWND</strong>
		 * </p>
		 * <p>
		 * Optional. A handle to the parent window, used to display any message
		 * boxes that the system might produce while executing this function.
		 * This value can be <strong>NULL</strong>.
		 * </p>
		 */
		public HWND hwnd;

		/**
		 * <p>
		 * Type: <strong>LPCTSTR</strong>
		 * </p>
		 * </dd>
		 * <dd>
		 * <p>
		 * A string, referred to as a <em>verb</em>, that specifies the action
		 * to be performed. The set of available verbs depends on the particular
		 * file or folder. Generally, the actions available from an object's
		 * shortcut menu are available verbs. This parameter can be
		 * <strong>NULL</strong>, in which case the default verb is used if
		 * available. If not, the "open" verb is used. If neither verb is
		 * available, the system uses the first verb listed in the registry. The
		 * following verbs are commonly used:
		 * </p>
		 * <dl class="indent">
		 * <dt>
		 * <p>
		 * <strong>edit</strong>
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Launches an editor and opens the document for editing. If
		 * <strong>lpFile</strong> is not a document file, the function will
		 * fail.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>explore</strong>
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Explores the folder specified by <strong>lpFile</strong>.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>find</strong>
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Initiates a search starting from the specified directory.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>open</strong>
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Opens the file specified by the <strong>lpFile</strong> parameter.
		 * The file can be an executable file, a document file, or a folder.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>print</strong>
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Prints the document file specified by <strong>lpFile</strong>. If
		 * <strong>lpFile</strong> is not a document file, the function will
		 * fail.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>properties</strong>
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Displays the file or folder's properties.
		 * </p>
		 * </dd>
		 * </dl>
		 */
		public String lpVerb;

		/**
		 * <p>
		 * Type: <strong>LPCTSTR</strong>
		 * </p>
		 * <p>
		 * The address of a null-terminated string that specifies the name of
		 * the file or object on which will perform the action specified by the
		 * <strong>lpVerb</strong> parameter. The system registry verbs that are
		 * supported by the <strong>ShellExecuteEx</strong> function include
		 * "open" for executable files and document files and "print" for
		 * document files for which a print handler has been registered. Other
		 * applications might have added Shell verbs through the system
		 * registry, such as "play" for .avi and .wav files. To specify a Shell
		 * namespace object, pass the fully qualified parse name and set the
		 * <strong>SEE_MASK_INVOKEIDLIST</strong> flag in the
		 * <strong>fMask</strong> parameter.
		 * </p>
		 * <div class="note"><strong>Note</strong>&nbsp;&nbsp;If the
		 * <strong>SEE_MASK_INVOKEIDLIST</strong> flag is set, you can use
		 * either <strong>lpFile</strong> or <strong>lpIDList</strong> to
		 * identify the item by its file system path or its PIDL respectively.
		 * One of the two values-<strong>lpFile</strong> or
		 * <strong>lpIDList</strong>-must be set.</div>
		 * <div class="note"><strong>Note</strong>&nbsp;&nbsp;If the path is not
		 * included with the name, the current directory is assumed.</div>
		 */
		public String lpFile;

		/**
		 * <p>
		 * Type: <strong>LPCTSTR</strong>
		 * </p>
		 * <p>
		 * Optional. The address of a null-terminated string that contains the
		 * application parameters. The parameters must be separated by spaces.
		 * If the <strong>lpFile</strong> member specifies a document file,
		 * <strong>lpParameters</strong> should be <strong>NULL</strong>.
		 * </p>
		 */
		public String lpParameters;

		/**
		 * <p>
		 * Type: <strong>LPCTSTR</strong>
		 * </p>
		 * <p>
		 * Optional. The address of a null-terminated string that specifies the
		 * name of the working directory. If this member is
		 * <strong>NULL</strong>, the current directory is used as the working
		 * directory.
		 * </p>
		 */
		public String lpDirectory;

		/**
		 * <p>
		 * Type: <strong>int</strong>
		 * </p>
		 * <p>
		 * Required. Flags that specify how an application is to be shown when
		 * it is opened; one of the SW_ values listed for the <a href=
		 * "https://msdn.microsoft.com/en-us/library/windows/desktop/bb762153(v=vs.85).aspx">
		 * <strong xmlns="http://www.w3.org/1999/xhtml">ShellExecute</strong>
		 * </a> function. If <strong>lpFile</strong> specifies a document file,
		 * the flag is simply passed to the associated application. It is up to
		 * the application to decide how to handle it.
		 * </p>
		 */
		public int nShow;

		/**
		 * <p>
		 * Type: <strong>HINSTANCE</strong>
		 * </p>
		 * <p>
		 * [out] If SEE_MASK_NOCLOSEPROCESS is set and the call succeeds, it
		 * sets this member to a value greater than 32. If the function fails,
		 * it is set to an SE_ERR_XXX error value that indicates the cause of
		 * the failure. Although <strong>hInstApp</strong> is declared as an
		 * HINSTANCE for compatibility with 16-bit Windows applications, it is
		 * not a true HINSTANCE. It can be cast only to an <strong>int</strong>
		 * and compared to either 32 or the following SE_ERR_XXX error codes.
		 * </p>
		 * <dl class="indent">
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_FNF</strong> (2)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * File not found.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_PNF</strong> (3)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Path not found.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_ACCESSDENIED</strong> (5)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Access denied.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_OOM</strong> (8)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Out of memory.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_DLLNOTFOUND</strong> (32)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Dynamic-link library not found.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_SHARE</strong> (26)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * Cannot share an open file.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_ASSOCINCOMPLETE</strong> (27)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * File association information not complete.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_DDETIMEOUT</strong> (28)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * DDE operation timed out.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_DDEFAIL</strong> (29)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * DDE operation failed.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_DDEBUSY</strong> (30)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * DDE operation is busy.
		 * </p>
		 * </dd>
		 * <dt>
		 * <p>
		 * <strong>SE_ERR_NOASSOC</strong> (31)
		 * </p>
		 * </dt>
		 * <dd>
		 * <p>
		 * File association not available.
		 * </p>
		 * </dd>
		 * </dl>
		 */
		public HINSTANCE hInstApp;

		/**
		 * <p>
		 * Type: <strong>LPVOID</strong>
		 * </p>
		 * <p>
		 * The address of an absolute <a href=
		 * "https://msdn.microsoft.com/en-us/library/windows/desktop/bb773321(v=vs.85).aspx">
		 * <strong xmlns="http://www.w3.org/1999/xhtml">ITEMIDLIST</strong></a>
		 * structure (PCIDLIST_ABSOLUTE) to contain an item identifier list that
		 * uniquely identifies the file to execute. This member is ignored if
		 * the <strong>fMask</strong> member does not include
		 * <strong>SEE_MASK_IDLIST</strong> or
		 * <strong>SEE_MASK_INVOKEIDLIST</strong>.
		 * </p>
		 */
		public Pointer lpIDList;

		/**
		 * <p>
		 * Type: <strong>LPCTSTR</strong>
		 * </p>
		 *
		 * <p>
		 * The address of a null-terminated string that specifies one of the
		 * following:
		 * </p>
		 * <ul>
		 * <li>A ProgId. For example, "Paint.Picture".</li>
		 * <li>A URI protocol scheme. For example, "http".</li>
		 * <li>A file extension. For example, ".txt".</li>
		 * <li>A registry path under HKEY_CLASSES_ROOT that names a subkey that
		 * contains one or more Shell verbs. This key will have a subkey that
		 * conforms to the Shell verb registry schema, such as
		 * <p>
		 * <strong>shell</strong>\<em>verb name</em>
		 * </p>
		 * .</li>
		 * </ul>
		 * <p>
		 * This member is ignored if <strong>fMask</strong> does not include
		 * <strong>SEE_MASK_CLASSNAME</strong>.
		 * </p>
		 */
		public String lpClass;

		/**
		 * <p>
		 * Type: <strong>HKEY</strong>
		 * </p>
		 * <p>
		 * A handle to the registry key for the file type. The access rights for
		 * this registry key should be set to KEY_READ. This member is ignored
		 * if <strong>fMask</strong> does not include
		 * <strong>SEE_MASK_CLASSKEY</strong>.
		 * </p>
		 */
		public HKEY hKeyClass;

		/**
		 * <p>
		 * Type: <strong>DWORD</strong>
		 * </p>
		 * <p>
		 * A keyboard shortcut to associate with the application. The low-order
		 * word is the virtual key code, and the high-order word is a modifier
		 * flag (HOTKEYF_). For a list of modifier flags, see the description of
		 * the <a href=
		 * "https://msdn.microsoft.com/en-us/library/windows/desktop/ms646284(v=vs.85).aspx">
		 * <strong xmlns="http://www.w3.org/1999/xhtml">WM_SETHOTKEY</strong>
		 * </a> message. This member is ignored if <strong>fMask</strong> does
		 * not include <strong>SEE_MASK_HOTKEY</strong>.
		 * </p>
		 */
		public int dwHotKey;

		/**
		 * This is actually a union:
		 *
		 * <pre>
		 * <code>union { HANDLE hIcon; HANDLE hMonitor; } DUMMYUNIONNAME;</code>
		 * </pre>
		 *
		 * <strong>DUMMYUNIONNAME</strong>
		 * <dl>
		 * <dt><strong>hIcon</strong></dt>
		 * <dd>
		 * <p>
		 * <strong>Type: <strong>HANDLE</strong></strong>
		 * </p>
		 * </dd>
		 * <dd>
		 * <p>
		 * A handle to the icon for the file type. This member is ignored if
		 * <strong>fMask</strong> does not include
		 * <strong>SEE_MASK_ICON</strong>. This value is used only in
		 * Windows&nbsp;XP and earlier. It is ignored as of Windows&nbsp;Vista.
		 * </p>
		 * </dd>
		 * <dt><strong>hMonitor</strong></dt>
		 * <dd>
		 * <p>
		 * <strong>Type: <strong>HANDLE</strong></strong>
		 * </p>
		 * </dd>
		 * <dd>
		 * <p>
		 * A handle to the monitor upon which the document is to be displayed.
		 * This member is ignored if <strong>fMask</strong> does not include
		 * <strong>SEE_MASK_HMONITOR</strong>.
		 * </p>
		 * </dd>
		 * </dl>
		 */
		public HANDLE hMonitor;

		/**
		 * <p>
		 * Type: <strong>HANDLE</strong>
		 * </p>
		 * <p>
		 * A handle to the newly started application. This member is set on
		 * return and is always <strong>NULL</strong> unless
		 * <strong>fMask</strong> is set to
		 * <strong>SEE_MASK_NOCLOSEPROCESS</strong>. Even if
		 * <strong>fMask</strong> is set to
		 * <strong>SEE_MASK_NOCLOSEPROCESS</strong>, <strong>hProcess</strong>
		 * will be <strong>NULL</strong> if no process was launched. For
		 * example, if a document to be launched is a URL and an instance of
		 * Internet Explorer is already running, it will display the document.
		 * No new process is launched, and <strong>hProcess</strong> will be
		 * <strong>NULL</strong>.
		 * </p>
		 * <div class="note"><strong>Note</strong>&nbsp;&nbsp;<a href=
		 * "https://msdn.microsoft.com/en-us/library/windows/desktop/bb762154(v=vs.85).aspx">
		 * <strong xmlns="http://www.w3.org/1999/xhtml">ShellExecuteEx</strong>
		 * </a> does not always return an <strong>hProcess</strong>, even if a
		 * process is launched as the result of the call. For example, an
		 * <strong>hProcess</strong> does not return when you use
		 * <strong>SEE_MASK_INVOKEIDLIST</strong> to invoke <a href=
		 * "https://msdn.microsoft.com/en-us/library/windows/desktop/bb776095(v=vs.85).aspx">
		 * <strong xmlns="http://www.w3.org/1999/xhtml">IContextMenu</strong>
		 * </a>.</div>
		 */
		public HANDLE hProcess;

		@Override
        protected List<String> getFieldOrder() {
			return FIELDS;
		}
	}

}
