/* Copyright (c) 2007, 2013 Timothy Wall, Markus Karg, All Rights Reserved
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

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/** 
 * Shell32.dll Interface.
 */
public interface Shell32 extends ShellAPI, StdCallLibrary {
    /** The instance **/
    Shell32 INSTANCE = Native.loadLibrary("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);

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
     * Do not display an error message box if an error occurs.
     */
    int SEE_MASK_FLAG_NO_UI = 0x00000400;
    
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
    HRESULT SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken, DWORD dwFlags, char[] pszPath);

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
    HRESULT SHGetDesktopFolder(PointerByReference ppshf);

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
    INT_PTR ShellExecute(HWND hwnd, String lpOperation, String lpFile, String lpParameters, String lpDirectory, int nShowCmd);

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
     *    {@link ShellAPI#ABM_REMOVE} Unregisters an appbar, removing the bar from the system's internal list.
     *    {@link ShellAPI#ABM_QUERYPOS} Requests a size and screen position for an appbar.
     *    {@link ShellAPI#ABM_SETPOS} Sets the size and screen position of an appbar.
     *    {@link ShellAPI#ABM_GETSTATE} Retrieves the autohide and always-on-top states of the Windows taskbar.
     *    {@link ShellAPI#ABM_GETTASKBARPOS} Retrieves the bounding rectangle of the Windows taskbar. Note that this applies only to the system taskbar. Other objects, particularly toolbars supplied with third-party software, also can be present. As a result, some of the screen area not covered by the Windows taskbar might not be visible to the user. To retrieve the area of the screen not covered by both the taskbar and other app bars -- the working area available to your application --, use the GetMonitorInfo function.
     *    {@link ShellAPI#ABM_ACTIVATE} Notifies the system to activate or deactivate an appbar. The lParam member of the APPBARDATA pointed to by pData is set to TRUE to activate or FALSE to deactivate.
     *    {@link ShellAPI#ABM_GETAUTOHIDEBAR} Retrieves the handle to the autohide appbar associated with a particular edge of the screen.
     *    {@link ShellAPI#ABM_SETAUTOHIDEBAR} Registers or unregisters an autohide appbar for an edge of the screen.
     *    {@link ShellAPI#ABM_WINDOWPOSCHANGED} Notifies the system when an appbar's position has changed.
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
    UINT_PTR SHAppBarMessage(DWORD dwMessage, APPBARDATA pData);

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
     *      <p>
     *         Returns <strong>TRUE</strong> if successful; otherwise,
     *         <strong>FALSE</strong>. Call <a href=
     *         "https://msdn.microsoft.com/en-us/library/windows/desktop/ms679360(v=vs.85).aspx">
     *         <strong xmlns="http://www.w3.org/1999/xhtml">GetLastError
     *         </strong></a> for extended error information.
     *         </p>
     */
    boolean ShellExecuteEx(ShellAPI.SHELLEXECUTEINFO lpExecInfo);

    /**
     * SHGetSpecialFolderLocation function for getting PIDL reference to My Computer etc
     *
     * @param hwndOwner
     *            Reserved.
     * @param nFolder
     *            A CSIDL value that identifies the folder of interest.
     * @param        ppidl
     *            A PIDL specifying the folder's location relative to the root of the namespace (the desktop). It is the responsibility of the calling application to free the returned IDList by using CoTaskMemFree.
     *
     * @return If this function succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *
     */
    HRESULT SHGetSpecialFolderLocation(WinDef.HWND hwndOwner, int nFolder, PointerByReference ppidl);
    
    /**
     * @param lpszFile
     *            Type: LPCTSTR<br>
     *            The name of an executable file, DLL, or icon file from which
     *            icons will be extracted.
     * @param nIconIndex
     *            Type: int<br>
     *            The zero-based index of the first icon to extract. For
     *            example, if this value is zero, the function extracts the
     *            first icon in the specified file.<br>
     *            If this value is -1 and phiconLarge and phiconSmall are both
     *            NULL, the function returns the total number of icons in the
     *            specified file.<br>
     *            If the file is an executable file or DLL, the return value is
     *            the number of RT_GROUP_ICON resources.<br>
     *            If the file is an .ico file, the return value is 1. If this
     *            value is a negative number and either phiconLarge or
     *            phiconSmall is not NULL, the function begins by extracting the
     *            icon whose resource identifier is equal to the absolute value
     *            of nIconIndex. For example, use -3 to extract the icon whose
     *            resource identifier is 3.
     * @param phiconLarge
     *            Type: HICON*<br>
     *            An array of icon handles that receives handles to the large
     *            icons extracted from the file. If this parameter is NULL, no
     *            large icons are extracted from the file.
     * @param phiconSmall
     *            Type: HICON*<br>
     *            An array of icon handles that receives handles to the small
     *            icons extracted from the file. If this parameter is NULL, no
     *            small icons are extracted from the file.
     * @param nIcons
     *            Type: UINT<br>
     *            The number of icons to be extracted from the file.
     * @return Type: UINT <br>
     *         If the nIconIndex parameter is -1, the phiconLarge parameter is
     *         NULL, and the phiconSmall parameter is NULL, then the return
     *         value is the number of icons contained in the specified file.
     *         Otherwise, the return value is the number of icons successfully
     *         extracted from the file.
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms648069(VS.85).aspx">MSDN</a>
     */
    int ExtractIconEx(String lpszFile, int nIconIndex, HICON[] phiconLarge, HICON[] phiconSmall, int nIcons);

    /**
     * Retrieves the application-defined, explicit Application User Model ID (AppUserModelID) for the current process.
     * 
     * @param ppszAppID
     *            A pointer that receives the address of the AppUserModelID assigned to the process. The caller is responsible for freeing this string with {@link Ole32#CoTaskMemFree} when it is no longer needed.
     * @return If this function succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/dd378419(v=vs.85).aspx">MSDN</a>
     */
    HRESULT GetCurrentProcessExplicitAppUserModelID(PointerByReference ppszAppID);

    /**
     * Specifies a unique application-defined Application User Model ID (AppUserModelID) that identifies the current process to the taskbar. This identifier allows an application to group its associated processes and windows under a single taskbar button.
     * 
     * @param appID
     *            The AppUserModelID to assign to the current process.
     * @return If this function succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/dd378422(v=vs.85).aspx">MSDN</a>
     */
    HRESULT SetCurrentProcessExplicitAppUserModelID(WString appID);

}

