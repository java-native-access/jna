/* Copyright (c) 2007, 2013 Timothy Wall, Markus Karg, All Rights Reserved
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
package com.sun.jna.platform.win32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/** 
 * Shell32.dll Interface.
 */
public interface Shell32 extends ShellAPI, StdCallLibrary {
	
    Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32", Shell32.class, 
    		W32APIOptions.UNICODE_OPTIONS);

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
	 * shortcut. The classic style—which would require <strong>lpFile</strong>
	 * to contain a .lnk file name—would not be affected.
	 * </p>
	 * <p>
	 * To include double quotation marks in <strong>lpParameters</strong>,
	 * enclose each mark in a pair of quotation marks, as in the following
	 * example.
	 * </p>
	 * <div id="code-snippet-2" class="codeSnippetContainer" xmlns=""> <div
	 * class="codeSnippetContainerTabs"> </div>
	 * <div class="codeSnippetContainerCodeContainer"> <div class=
	 * "codeSnippetToolBar"> <div class="codeSnippetToolBarText"> <a name=
	 * "CodeSnippetCopyLink" style="display: none;" title=
	 * "Copy to clipboard." href=
	 * "javascript:if (window.epx.codeSnippet)window.epx.codeSnippet.copyCode('CodeSnippetContainerCode_3de148bb-edf3-4344-8ecf-c211304bfa9e');"
	 * >Copy</a> </div> </div>
	 * <div id="CodeSnippetContainerCode_3de148bb-edf3-4344-8ecf-c211304bfa9e"
	 * class="codeSnippetContainerCode" dir="ltr"> <div style="color:Black;">
	 * 
	 * <pre>
	 * sei.lpParameters = &quot;An example: \&quot;\&quot;\&quot;quoted text\&quot;\&quot;\&quot;&quot;;
	 * </pre>
	 * 
	 * </div> </div> </div> </div>
	 * <p>
	 * In this case, the application receives three parameters: <em>An</em>,
	 * <em>example:</em>, and <em>"quoted text"</em>.
	 * </p>
	 */
	public static class SHELLEXECUTEINFO extends Structure {

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
		public WString lpVerb;

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
		 * One of the two values—<strong>lpFile</strong> or
		 * <strong>lpIDList</strong>—must be set.</div>
		 * <div class="note"><strong>Note</strong>&nbsp;&nbsp;If the path is not
		 * included with the name, the current directory is assumed.</div>
		 */
		public WString lpFile;

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
		public WString lpParameters;

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
		public WString lpDirectory;

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
		public WString lpClass;

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

		@SuppressWarnings("rawtypes")
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "cbSize", "fMask", "hwnd", "lpVerb", "lpFile", "lpParameters",
					"lpDirectory", "nShow", "hInstApp", "lpIDList", "lpClass", "hKeyClass", "dwHotKey", "hMonitor",
					"hProcess", });
		}
	}
    
	/**
	 * No dialog box confirming the deletion of the objects will be displayed.
	 */
	int SHERB_NOCONFIRMATION = 0x00000001;

	/**
	 * No dialog box indicating the progress will be displayed.
	 */
	int SHERB_NOPROGRESSUI = 0x00000002;

	/**
	 * No sound will be played when the operation is complete.
	 */
	int SHERB_NOSOUND = 0x00000004;

	/**
	 * <p>
	 * Sets the show state based on the SW_ value specified in the <a href=
	 * "https://msdn.microsoft.com/en-us/library/windows/desktop/ms686331(v=vs.85).aspx">
	 * <strong xmlns="http://www.w3.org/1999/xhtml">STARTUPINFO</strong></a>
	 * structure passed to the <a href=
	 * "https://msdn.microsoft.com/en-us/library/windows/desktop/ms682425(v=vs.85).aspx">
	 * <strong xmlns="http://www.w3.org/1999/xhtml">CreateProcess</strong></a>
	 * function by the program that started the application.
	 * </p>
	 */
	int SW_SHOWDEFAULT = 10;

	/**
	 * <p>
	 * <strong>SEE_MASK_NOCLOSEPROCESS</strong> (0x00000040)
	 * </p>
	 * <p>
	 * Use to indicate that the <strong>hProcess</strong> member receives the
	 * process handle. This handle is typically used to allow an application to
	 * find out when a process created with terminates. In some cases, such as
	 * when execution is satisfied through a DDE conversation, no handle will be
	 * returned. The calling application is responsible for closing the handle
	 * when it is no longer needed.
	 * </p>
	 */
	int SEE_MASK_NOCLOSEPROCESS = 0x00000040;
	
    /**
     * This function can be used to copy, move, rename, or delete a file system object.
     * @param fileop
     *  Address of an SHFILEOPSTRUCT structure that contains information this function 
     *  needs to carry out the specified operation. 
     * @return
     *  Returns zero if successful, or nonzero otherwise.
     */
    int SHFileOperation(SHFILEOPSTRUCT fileop);

    /**
     * Takes the CSIDL of a folder and returns the path.
     * @param hwndOwner
     *  Handle to an owner window. This parameter is typically set to NULL. If it is not NULL, 
     *  and a dial-up connection needs to be made to access the folder, a user interface (UI) 
     *  prompt will appear in this window. 
     * @param nFolder
     *  A CSIDL value that identifies the folder whose path is to be retrieved. Only real 
     *  folders are valid. If a virtual folder is specified, this function will fail. You can 
     *  force creation of a folder with SHGetFolderPath by combining the folder's CSIDL with 
     *  CSIDL_FLAG_CREATE. 
     * @param hToken
     *  An access token that can be used to represent a particular user. 
     * @param dwFlags
     *   Flags to specify which path is to be returned.
     * @param pszPath
     *  Pointer to a null-terminated string of length MAX_PATH which will receive the path. 
     *  If an error occurs or S_FALSE is returned, this string will be empty. 
     * @return
     *  Returns standard HRESULT codes.
     */
    HRESULT SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken, DWORD dwFlags, 
    		char[] pszPath);

    /**
     * Retrieves the full path of a known folder identified by the folder's KNOWNFOLDERID. This function replaces
     * {@link #SHGetFolderPath}. That older function is now simply a wrapper for SHGetKnownFolderPath.
     * @param rfid A reference to the KNOWNFOLDERID (in {@link KnownFolders}) that identifies the folder.
     * @param dwFlags Flags that specify special retrieval options. This value can be 0; otherwise, one or more of the
     *        {@link ShlObj.KNOWN_FOLDER_FLAG} values.
     * @param hToken Type: HANDLE An access token that represents a particular user. If this parameter is NULL, which is
     *        the most common usage, the function requests the known folder for the current user. Request a specific user's
     *        folder by passing the hToken of that user. This is typically done in the context of a service that has sufficient
     *        privileges to retrieve the token of a given user. That token must be opened with TOKEN_QUERY and
     *        TOKEN_IMPERSONATE rights. In some cases, you also need to include TOKEN_DUPLICATE. In addition to passing the
     *        user's hToken, the registry hive of that specific user must be mounted. See Access Control for further discussion
     *        of access control issues. Assigning the hToken parameter a value of -1 indicates the Default User. This allows
     *        clients of SHGetKnownFolderPath to find folder locations (such as the Desktop folder) for the Default User. The
     *        Default User user profile is duplicated when any new user account is created, and includes special folders such
     *        as Documents and Desktop. Any items added to the Default User folder also appear in any new user account. Note
     *        that access to the Default User folders requires administrator privileges.
     * @param ppszPath When this method returns, contains the address of a pointer to a null-terminated
     *        Unicode string that specifies the path of the known folder. The calling process is responsible for freeing this
     *        resource once it is no longer needed by calling {@link Ole32#CoTaskMemFree}. The returned path does not include a trailing
     *        backslash. For example, "C:\Users" is returned rather than "C:\Users\".
     * @return Returns S_OK if successful, or an error value otherwise, including the following: 
     *        <ul><li>E_FAIL Among other things, this value can indicate that the rfid parameter references a KNOWNFOLDERID which 
     *        does not have a path (such as a folder marked as KF_CATEGORY_VIRTUAL).</li> 
     *        <li>E_INVALIDARG Among other things, this value can indicate that the rfid parameter references a KNOWNFOLDERID 
     *        that is not present on the system. Not all KNOWNFOLDERID values are present on all systems. Use 
     *        IKnownFolderManager::GetFolderIds to retrieve the set of KNOWNFOLDERID values for the current system.</li></ul>
     */
    HRESULT SHGetKnownFolderPath(GUID rfid, int dwFlags, HANDLE hToken, PointerByReference ppszPath);

    /**
     * Retrieves the IShellFolder interface for the desktop folder, which is the root of the Shell's namespace.
     * The retrieved COM interface pointer can be used via Com4JNA's ComObject.wrapNativeInterface call
     * given a suitable interface definition for IShellFolder
     * @param ppshf A place to put the IShellFolder interface pointer
     * @return If the function succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     */
    HRESULT SHGetDesktopFolder( PointerByReference ppshf );

    /**
     * Performs an operation on a specified file.
     * 
     * @param hwnd
     *   A handle to the owner window used for displaying a UI or error messages. This value can be NULL if the
     *   operation is not associated with a window.
     *
     * @param lpOperation
     *   A pointer to a null-terminated string, referred to in this case as a verb, that specifies the action to be
     *   performed. The set of available verbs depends on the particular file or folder. Generally, the actions
     *   available from an object's shortcut menu are available verbs. The following verbs are commonly used:
     *
     *   edit
     *     Launches an editor and opens the document for editing. If lpFile is not a document file, the function will
     *     fail.
     *   explore
     *     Explores a folder specified by lpFile.
     *   find
     *     Initiates a search beginning in the directory specified by lpDirectory.
     *   open
     *     Opens the item specified by the lpFile parameter. The item can be a file or folder.
     *   print
     *     Prints the file specified by lpFile. If lpFile is not a document file, the function fails.
     *   NULL
     *     In systems prior to Windows 2000, the default verb is used if it is valid and available in the registry. If
     *     not, the "open" verb is used.
     *     In Windows 2000 and later, the default verb is used if available. If not, the "open" verb is used. If neither
     *     verb is available, the system uses the first verb listed in the registry.
     * 
     * @param lpFile
     *   A pointer to a null-terminated string that specifies the file or object on which to execute the specified verb.
     *   To specify a Shell namespace object, pass the fully qualified parse name. Note that not all verbs are supported
     *   on all objects. For example, not all document types support the "print" verb. If a relative path is used for
     *   the lpDirectory parameter do not use a relative path for lpFile.
     *
     * @param lpParameters
     *   If lpFile specifies an executable file, this parameter is a pointer to a null-terminated string that specifies
     *   the parameters to be passed to the application. The format of this string is determined by the verb that is to
     *   be invoked. If lpFile specifies a document file, lpParameters should be NULL.
     *
     * @param lpDirectory
     *   A pointer to a null-terminated string that specifies the default (working) directory for the action. If this
     *   value is NULL, the current working directory is used. If a relative path is provided at lpFile, do not use a
     *   relative path for lpDirectory.
     *
     * @param nShowCmd
     *   The flags that specify how an application is to be displayed when it is opened. If lpFile specifies a document
     *   file, the flag is simply passed to the associated application. It is up to the application to decide how to
     *   handle it.
     *
     * @return
     *   If the function succeeds, it returns a value greater than 32. If the function fails, it returns an error value
     *   that indicates the cause of the failure. The return value is cast as an HINSTANCE for backward compatibility
     *   with 16-bit Windows applications. It is not a true HINSTANCE, however. It can be cast only to an int and
     *   compared to either 32 or the following error codes below.
     * 
     * NOTE: {@link WinDef.INT_PTR} is used instead of HINSTANCE here, since
     *   the former fits the reutrn type's actual usage more closely.
     *
     *   0 The operating system is out of memory or resources.
     *   ERROR_FILE_NOT_FOUND The specified file was not found.
     *   ERROR_PATH_NOT_FOUND The specified path was not found.
     *   ERROR_BAD_FORMAT The .exe file is invalid (non-Win32 .exe or error in .exe image).
     *   SE_ERR_ACCESSDENIED The operating system denied access to the specified file.
     *   SE_ERR_ASSOCINCOMPLETE The file name association is incomplete or invalid.
     *   SE_ERR_DDEBUSY The DDE transaction could not be completed because other DDE transactions were being processed.
     *   SE_ERR_DDEFAIL The DDE transaction failed.
     *   SE_ERR_DDETIMEOUT The DDE transaction could not be completed because the request timed out.
     *   SE_ERR_DLLNOTFOUND The specified DLL was not found.
     *   SE_ERR_FNF The specified file was not found.
     *   SE_ERR_NOASSOC There is no application associated with the given file name extension. This error will also be
     *     returned if you attempt to print a file that is not printable.
     *   SE_ERR_OOM There was not enough memory to complete the operation.
     *   SE_ERR_PNF The specified path was not found.
     *   SE_ERR_SHARE A sharing violation occurred.
     */
    INT_PTR ShellExecute(HWND hwnd, String lpOperation, String lpFile, String lpParameters, String lpDirectory,
                                  int nShowCmd);

    /**
     * Retrieves the path of a special folder, identified by its CSIDL.
     *
     * @param owner
     *            Reserved.
     * @param path
     *            A pointer to a null-terminated string that receives the drive and path of the specified folder. This buffer must be at least MAX_PATH
     *            characters in size.
     * @param csidl
     *            A CSIDL that identifies the folder of interest. If a virtual folder is specified, this function will fail.
     * @param create
     *            Indicates whether the folder should be created if it does not already exist. If this value is nonzero, the folder is created. If this value is
     *            zero, the folder is not created.
     * @return {@code true} if successful; otherwise, {@code false}.
     */
    boolean SHGetSpecialFolderPath(HWND owner, char[] path, int csidl, boolean create);
    
    
    /**
     * SHAppBarMessage function
     * 
     * @param dwMessage 
     *   Appbar message value to send. This parameter can be one of the following values.
     *    {@link ShellAPI#ABM_NEW} Registers a new appbar and specifies the message identifier that the system should use to send notification messages to the appbar.
     * 	  {@link ShellAPI#ABM_REMOVE} Unregisters an appbar, removing the bar from the system's internal list.
     * 	  {@link ShellAPI#ABM_QUERYPOS} Requests a size and screen position for an appbar.
     *    {@link ShellAPI#ABM_SETPOS} Sets the size and screen position of an appbar.
     * 	  {@link ShellAPI#ABM_GETSTATE} Retrieves the autohide and always-on-top states of the Windows taskbar.
     * 	  {@link ShellAPI#ABM_GETTASKBARPOS} Retrieves the bounding rectangle of the Windows taskbar. Note that this applies only to the system taskbar. Other objects, particularly toolbars supplied with third-party software, also can be present. As a result, some of the screen area not covered by the Windows taskbar might not be visible to the user. To retrieve the area of the screen not covered by both the taskbar and other app bars -- the working area available to your application --, use the GetMonitorInfo function.
     * 	  {@link ShellAPI#ABM_ACTIVATE} Notifies the system to activate or deactivate an appbar. The lParam member of the APPBARDATA pointed to by pData is set to TRUE to activate or FALSE to deactivate.
     * 	  {@link ShellAPI#ABM_GETAUTOHIDEBAR} Retrieves the handle to the autohide appbar associated with a particular edge of the screen.
     * 	  {@link ShellAPI#ABM_SETAUTOHIDEBAR} Registers or unregisters an autohide appbar for an edge of the screen.
     * 	  {@link ShellAPI#ABM_WINDOWPOSCHANGED} Notifies the system when an appbar's position has changed.
     *    {@link ShellAPI#ABM_SETSTATE} Windows XP and later: Sets the state of the appbar's autohide and always-on-top attributes.
     * 
     * @param pData
     *   A pointer to an APPBARDATA structure. The content of the structure on entry and on exit depends on the value set in the dwMessage parameter. See the individual message pages for specifics.
     *
     * @return This function returns a message-dependent value. For more information, see the Windows SDK documentation for the specific appbar message sent.
     *
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787951(v=vs.85).aspx">ABM_NEW</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787955(v=vs.85).aspx">ABM_REMOVE</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787953(v=vs.85).aspx">ABM_QUERYPOS</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787959(v=vs.85).aspx">ABM_SETPOS</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787947(v=vs.85).aspx">ABM_GETSTATE</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787949(v=vs.85).aspx">ABM_GETTASKBARPOS</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787943(v=vs.85).aspx">ABM_ACTIVATE</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787945(v=vs.85).aspx">ABM_GETAUTOHIDEBAR</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787957(v=vs.85).aspx">ABM_SETAUTOHIDEBAR</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787963(v=vs.85).aspx">ABM_WINDOWPOSCHANGED</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/bb787961(v=vs.85).aspx">ABM_SETSTATE</a>
     * 
     */
    UINT_PTR SHAppBarMessage( DWORD dwMessage, APPBARDATA pData );

	/**
	 * Empties the Recycle Bin on the specified drive.
	 * 
	 * @param hwnd
	 *            A handle to the parent window of any dialog boxes that might
	 *            be displayed during the operation.<br>
	 *            This parameter can be NULL.
	 * @param pszRootPath
	 *            a null-terminated string of maximum length MAX_PATH that
	 *            contains the path of the root<br>
	 *            drive on which the Recycle Bin is located. This parameter can
	 *            contain a string formatted with the drive,<br>
	 *            folder, and subfolder names, for example c:\windows\system\,
	 *            etc. It can also contain an empty string or<br>
	 *            NULL. If this value is an empty string or NULL, all Recycle
	 *            Bins on all drives will be emptied.
	 * @param dwFlags
	 *            a bitwise combination of SHERB_NOCONFIRMATION,
	 *            SHERB_NOPROGRESSUI and SHERB_NOSOUND.<br>
	 * @return Returns S_OK (0) if successful, or a COM-defined error value
	 *         otherwise.<br>
	 */
	int SHEmptyRecycleBin(HANDLE hwnd, String pszRootPath, int dwFlags);

	/**
	 * @param lpExecInfo
	 *            <p>
	 *            Type: <strong>SHELLEXECUTEINFO*</strong>
	 *            </p>
	 *            <p>
	 *            A pointer to a <a href=
	 *            "https://msdn.microsoft.com/en-us/library/windows/desktop/bb759784(v=vs.85).aspx">
	 *            <strong xmlns="http://www.w3.org/1999/xhtml">SHELLEXECUTEINFO
	 *            </strong></a> structure that contains and receives information
	 *            about the application being executed.
	 *            </p>
	 * @return
	 * 		<p>
	 *         Returns <strong>TRUE</strong> if successful; otherwise,
	 *         <strong>FALSE</strong>. Call <a href=
	 *         "https://msdn.microsoft.com/en-us/library/windows/desktop/ms679360(v=vs.85).aspx">
	 *         <strong xmlns="http://www.w3.org/1999/xhtml">GetLastError
	 *         </strong></a> for extended error information.
	 *         </p>
	 */
	boolean ShellExecuteEx(SHELLEXECUTEINFO lpExecInfo);

}
