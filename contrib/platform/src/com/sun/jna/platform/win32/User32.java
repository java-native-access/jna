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
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Provides access to the w32 user32 library. Incomplete implementation to
 * support demos.
 *
 * @author Todd Fast, todd.fast@sun.com
 * @author twalljava@dev.java.net
 * @author Tobias Wolf, wolf.tobias@gmx.net
 * @author Markus KARG (markus[at]headcrashing[dot]eu)
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public interface User32 extends StdCallLibrary, WinUser, WinNT {

    /** The instance. */
    User32 INSTANCE = Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Handle for message-only window.
     */
    HWND HWND_MESSAGE = new HWND(Pointer.createConstant(-3));

    /** The cs globalclass. */
    int CS_GLOBALCLASS = 0x4000;

    /** The ws ex topmost. */
    int WS_EX_TOPMOST = 0x00000008;

    /** The hRecipient parameter is a window handle. */
    int DEVICE_NOTIFY_WINDOW_HANDLE = 0x00000000;

    /** The hRecipient parameter is a service status handle. */
    int DEVICE_NOTIFY_SERVICE_HANDLE = 0x00000001;

    /** The device notify all interface classes. */
    int DEVICE_NOTIFY_ALL_INTERFACE_CLASSES = 0x00000004;

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
     * This function retrieves a handle to a display device context (DC) for the
     * client area of the specified window. The display device context can be
     * used in subsequent graphics display interface (GDI) functions to draw in
     * the client area of the window.
     *
     * @param hWnd
     *            Handle to the window whose device context is to be retrieved.
     *            If this value is NULL, GetDC retrieves the device context for
     *            the entire screen.
     * @return The handle the device context for the specified window's client
     *         area indicates success. NULL indicates failure. To get extended
     *         error information, call GetLastError.
     */
    HDC GetDC(HWND hWnd);

    /**
     * This function releases a device context (DC), freeing it for use by other
     * applications. The effect of ReleaseDC depends on the type of device
     * context.
     *
     * @param hWnd
     *            Handle to the window whose device context is to be released.
     * @param hDC
     *            Handle to the device context to be released.
     * @return The return value specifies whether the device context is
     *         released. 1 indicates that the device context is released. Zero
     *         indicates that the device context is not released.
     */
    int ReleaseDC(HWND hWnd, HDC hDC);

    /**
     * This function retrieves the handle to the top-level window whose class
     * name and window name match the specified strings. This function does not
     * search child windows.
     *
     * @param lpClassName
     *            Long pointer to a null-terminated string that specifies the
     *            class name or is an atom that identifies the class-name
     *            string. If this parameter is an atom, it must be a global atom
     *            created by a previous call to the GlobalAddAtom function. The
     *            atom, a 16-bit value, must be placed in the low-order word of
     *            lpClassName; the high-order word must be zero.
     * @param lpWindowName
     *            Long pointer to a null-terminated string that specifies the
     *            window name (the window's title). If this parameter is NULL,
     *            all window names match.
     * @return A handle to the window that has the specified class name and
     *         window name indicates success. NULL indicates failure. To get
     *         extended error information, call GetLastError.
     */
    HWND FindWindow(String lpClassName, String lpWindowName);

    /**
     * This function retrieves the name of the class to which the specified
     * window belongs.
     *
     * @param hWnd
     *            Handle to the window and, indirectly, the class to which the
     *            window belongs.
     * @param lpClassName
     *            Long pointer to the buffer that is to receive the class name
     *            string.
     * @param nMaxCount
     *            Specifies the length, in characters, of the buffer pointed to
     *            by the lpClassName parameter. The class name string is
     *            truncated if it is longer than the buffer.
     * @return The number of characters copied to the specified buffer indicates
     *         success. Zero indicates failure. To get extended error
     *         information, call GetLastError.
     */
    int GetClassName(HWND hWnd, char[] lpClassName, int nMaxCount);

    /**
     * Retrieves information about the active window or a specified graphical
     * user interface (GUI) thread.
     *
     * @param idThread
     *            Identifies the thread for which information is to be
     *            retrieved. To retrieve this value, use the
     *            GetWindowThreadProcessId function. If this parameter is NULL,
     *            the function returns information for the foreground thread.
     * @param lpgui
     *            Pointer to a GUITHREADINFO structure that receives information
     *            describing the thread. Note that you must set
     *            GUITHREADINFO.cbSize to sizeof(GUITHREADINFO) before calling
     *            this function.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GetGUIThreadInfo(int idThread, GUITHREADINFO lpgui);

    /**
     * The GetWindowInfo function retrieves information about the specified
     * window.
     *
     * @param hWnd
     *            Handle to the window whose information is to be retrieved.
     * @param pwi
     *            Pointer to a WINDOWINFO structure to receive the information.
     *            Note that you must set WINDOWINFO.cbSize to sizeof(WINDOWINFO)
     *            before calling this function.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero.
     */
    boolean GetWindowInfo(HWND hWnd, WINDOWINFO pwi);

    /**
     * This function retrieves the dimensions of the bounding rectangle of the
     * specified window. The dimensions are given in screen coordinates that are
     * relative to the upper-left corner of the screen.
     *
     * @param hWnd
     *            Handle to the window.
     * @param rect
     *            Long pointer to a RECT structure that receives the screen
     *            coordinates of the upper-left and lower-right corners of the
     *            window.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero.
     */
    boolean GetWindowRect(HWND hWnd, RECT rect);

    /**
     * This function retrieves the coordinates of a window's client area. 
     * The client coordinates specify the upper-left and lower-right corners of the 
     * client area. Because client coordinates are relative to the upper-left 
     * corner of a window's client area, the coordinates of the upper-left corner 
     * are (0,0).
     *
     * @param hWnd
     *            Handle to the window.
     * @param rect
     *            Long pointer to a RECT structure that structure that receives 
     *            the client coordinates. The left and top members are zero. The 
     *            right and bottom members contain the width and height of the 
     *            window.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero.
     */
    boolean GetClientRect(HWND hWnd, RECT rect);
    
    /**
     * This function copies the text of the specified window's title bar - if it
     * has one - into a buffer. If the specified window is a control, the text
     * of the control is copied.
     *
     * @param hWnd
     *            Handle to the window or control containing the text.
     * @param lpString
     *            Long pointer to the buffer that will receive the text.
     * @param nMaxCount
     *            Specifies the maximum number of characters to copy to the
     *            buffer, including the NULL character. If the text exceeds this
     *            limit, it is truncated.
     * @return The length, in characters, of the copied string, not including
     *         the terminating null character, indicates success. Zero indicates
     *         that the window has no title bar or text, if the title bar is
     *         empty, or if the window or control handle is invalid. To get
     *         extended error information, call GetLastError. This function
     *         cannot retrieve the text of an edit control in another
     *         application.
     */
    int GetWindowText(HWND hWnd, char[] lpString, int nMaxCount);

    /**
     * This function retrieves the length, in characters, of the specified
     * window's title bar text - if the window has a title bar. If the specified
     * window is a control, the function retrieves the length of the text within
     * the control.
     *
     * @param hWnd
     *            Handle to the window or control.
     * @return The length, in characters, of the text indicates success. Under
     *         certain conditions, this value may actually be greater than the
     *         length of the text. Zero indicates that the window has no text.
     *         To get extended error information, call GetLastError.
     */
    int GetWindowTextLength(HWND hWnd);

    /**
     * The GetWindowModuleFileName function retrieves the full path and file
     * name of the module associated with the specified window handle.
     *
     * @param hWnd
     *            Handle to the window whose module file name will be retrieved.
     * @param lpszFileName
     *            Pointer to a buffer that receives the path and file name.
     * @param cchFileNameMax
     *            Specifies the maximum number of TCHARs that can be copied into
     *            the lpszFileName buffer.
     * @return The return value is the total number of TCHARs copied into the
     *         buffer.
     */
    int GetWindowModuleFileName(HWND hWnd, char[] lpszFileName, int cchFileNameMax);

    /**
     * This function retrieves the identifier of the thread that created the
     * specified window and, optionally, the identifier of the process that
     * created the window.
     *
     * @param hWnd
     *            Handle to the window.
     * @param lpdwProcessId
     *            Pointer to a 32-bit value that receives the process
     *            identifier. If this parameter is not NULL,
     *            GetWindowThreadProcessId copies the identifier of the process
     *            to the 32-bit value; otherwise, it does not.
     * @return The return value is the identifier of the thread that created the
     *         window.
     */
    int GetWindowThreadProcessId(HWND hWnd, IntByReference lpdwProcessId);

    /**
     * This function enumerates all top-level windows on the screen by passing
     * the handle to each window, in turn, to an application-defined callback
     * function. EnumWindows continues until the last top-level window is
     * enumerated or the callback function returns FALSE.
     *
     * @param lpEnumFunc
     *            Long pointer to an application-defined callback function.
     * @param data
     *            Specifies an application-defined value to be passed to the
     *            callback function.
     * @return Nonzero indicates success. Zero indicates failure. To get
     *         extended error information, call GetLastError.
     */
    boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer data);

    /**
     * The EnumChildWindows function enumerates the child windows that belong to
     * the specified parent window by passing the handle to each child window,
     * in turn, to an application-defined callback function. EnumChildWindows
     * continues until the last child window is enumerated or the callback
     * function returns FALSE.
     *
     * @param hWnd
     *            Handle to the parent window whose child windows are to be
     *            enumerated. If this parameter is NULL, this function is
     *            equivalent to EnumWindows.
     * @param lpEnumFunc
     *            Pointer to an application-defined callback function.
     * @param data
     *            Specifies an application-defined value to be passed to the
     *            callback function.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError. If EnumChildProc returns zero,
     *         the return value is also zero. In this case, the callback
     *         function should call SetLastError to obtain a meaningful error
     *         code to be returned to the caller of EnumChildWindows.
     */
    boolean EnumChildWindows(HWND hWnd, WNDENUMPROC lpEnumFunc, Pointer data);

    /**
     * The EnumThreadWindows function enumerates all nonchild windows associated
     * with a thread by passing the handle to each window, in turn, to an
     * application-defined callback function. EnumThreadWindows continues until
     * the last window is enumerated or the callback function returns FALSE. To
     * enumerate child windows of a particular window, use the EnumChildWindows
     * function.
     *
     * @param dwThreadId
     *            Identifies the thread whose windows are to be enumerated.
     * @param lpEnumFunc
     *            Pointer to an application-defined callback function.
     * @param data
     *            Specifies an application-defined value to be passed to the
     *            callback function.
     * @return If the callback function returns TRUE for all windows in the
     *         thread specified by dwThreadId, the return value is TRUE. If the
     *         callback function returns FALSE on any enumerated window, or if
     *         there are no windows found in the thread specified by dwThreadId,
     *         the return value is FALSE.
     */
    boolean EnumThreadWindows(int dwThreadId, WNDENUMPROC lpEnumFunc, Pointer data);

    /**
     * The FlashWindowEx function flashes the specified window. It does not
     * change the active state of the window.
     *
     * @param pfwi
     *            Pointer to the FLASHWINFO structure.
     * @return The return value specifies the window's state before the call to
     *         the FlashWindowEx function. If the window caption was drawn as
     *         active before the call, the return value is nonzero. Otherwise,
     *         the return value is zero.
     */
    boolean FlashWindowEx(FLASHWINFO pfwi);

    /**
     * This function loads the specified icon resource from the executable
     * (.exe) file associated with an application instance.
     *
     * @param hInstance
     *            Handle to an instance of the module whose executable file
     *            contains the icon to be loaded. This parameter must be NULL
     *            when a standard icon is being loaded.
     * @param iconName
     *            Long pointer to a null-terminated string that contains the
     *            name of the icon resource to be loaded. Alternatively, this
     *            parameter can contain the resource identifier in the low-order
     *            word and zero in the high-order word. Use the MAKEINTRESOURCE
     *            macro to create this value.
     *            <p>
     *            To use one of the predefined icons, set the hInstance
     *            parameter to NULL and the lpIconName parameter to one of the
     *            following values: {@link WinUser#IDC_APPSTARTING} etc.</p>
     * @return A handle to the newly loaded icon indicates success. NULL
     *         indicates failure. To get extended error information, call
     *         GetLastError.
     */
    HICON LoadIcon(HINSTANCE hInstance, String iconName);

    /**
     * This function loads an icon, cursor, or bitmap.
     *
     * @param hinst
     *            Handle to an instance of the module that contains the image to
     *            be loaded.
     * @param name
     *            Pointer to a null-terminated string that contains the name of
     *            the image resource in the hinst module that identifies the
     *            image to load.
     * @param type
     *            Specifies the type of image to be loaded.
     * @param xDesired
     *            Specifies the width, in pixels, of the icon or cursor. If this
     *            parameter is zero, the function uses the SM_CXICON or
     *            SM_CXCURSOR system metric value to set the width. If uType is
     *            IMAGE_BITMAP, this parameter must be zero.
     * @param yDesired
     *            Specifies the height, in pixels, of the icon or cursor. If
     *            this parameter is zero, the function uses the SM_CYICON or
     *            SM_CYCURSOR system metric value to set the height. If uType is
     *            IMAGE_BITMAP, this parameter must be zero.
     * @param load
     *            Set to zero.
     * @return The handle of the newly loaded image indicates success. NULL
     *         indicates failure. To get extended error information, call
     *         GetLastError.
     */
    HANDLE LoadImage(HINSTANCE hinst, String name, int type, int xDesired,
                     int yDesired, int load);

    /**
     * This function destroys an icon and frees any memory the icon occupied.
     *
     * @param hicon
     *            Handle to the icon to be destroyed. The icon must not be in
     *            use.
     * @return Nonzero indicates success. Zero indicates failure. To get
     *         extended error information, call GetLastError.
     */
    boolean DestroyIcon(HICON hicon);

    /**
     * This function retrieves information about the specified window.
     * GetWindowLong also retrieves the 32-bit (long) value at the specified
     * offset into the extra window memory of a window.
     *
     * @param hWnd
     *            Handle to the window and, indirectly, the class to which the
     *            window belongs.
     * @param nIndex
     *            Specifies the zero-based offset to the value to be retrieved.
     * @return The requested 32-bit value indicates success. Zero indicates
     *         failure. To get extended error information, call GetLastError.
     */
    int GetWindowLong(HWND hWnd, int nIndex);

    /**
     * This function changes an attribute of the specified window. SetWindowLong
     * also sets a 32-bit (LONG) value at the specified offset into the extra
     * window memory of a window.
     *
     * @param hWnd
     *            Handle to the window and, indirectly, the class to which the
     *            window belongs.
     * @param nIndex
     *            Specifies the zero-based offset to the value to be set.
     * @param dwNewLong
     *            Specifies the replacement value.
     * @return The previous value of the specified 32-bit integer indicates
     *         success. Zero indicates failure. To get extended error
     *         information, call GetLastError.
     */
    int SetWindowLong(HWND hWnd, int nIndex, int dwNewLong);

    /**
     * The GetWindowLongPtr function retrieves information about the specified
     * window. The function also retrieves the value at a specified offset into
     * the extra window memory.
     *
     * @param hWnd
     *            Handle to the window and, indirectly, the class to which the
     *            window belongs.
     * @param nIndex
     *            Specifies the zero-based offset to the value to be retrieved.
     * @return If the function succeeds, the return value is the requested
     *         value. If the function fails, the return value is zero. To get
     *         extended error information, call GetLastError. If SetWindowLong
     *         or SetWindowLongPtr has not been called previously,
     *         GetWindowLongPtr returns zero for values in the extra window or
     *         class memory.
     */
    LONG_PTR GetWindowLongPtr(HWND hWnd, int nIndex);

    /**
     * The SetWindowLongPtr function changes an attribute of the specified
     * window. The function also sets a value at the specified offset in the
     * extra window memory.
     *
     * @param hWnd
     *            Handle to the window and, indirectly, the class to which the
     *            window belongs.
     * @param nIndex
     *            Specifies the zero-based offset to the value to be set.
     * @param dwNewLongPtr
     *            Specifies the replacement value.
     * @return If the function succeeds, the return value is the previous value
     *         of the specified offset. If the function fails, the return value
     *         is zero. To get extended error information, call GetLastError. If
     *         the previous value is zero and the function succeeds, the return
     *         value is zero, but the function does not clear the last error
     *         information. To determine success or failure, clear the last
     *         error information by calling SetLastError(0), then call
     *         SetWindowLongPtr. Function failure will be indicated by a return
     *         value of zero and a GetLastError result that is nonzero.
     */
    Pointer SetWindowLongPtr(HWND hWnd, int nIndex, Pointer dwNewLongPtr);

    /**
     * The SetLayeredWindowAttributes function sets the opacity and transparency
     * color key of a layered window.
     *
     * @param hwnd
     *            Handle to the layered window.
     * @param crKey
     *            COLORREF structure that specifies the transparency color key
     *            to be used when composing the layered window.
     * @param bAlpha
     *            Alpha value used to describe the opacity of the layered
     *            window.
     * @param dwFlags
     *            Specifies an action to take.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean SetLayeredWindowAttributes(HWND hwnd, int crKey, byte bAlpha, int dwFlags);

    /**
     * The GetLayeredWindowAttributes function retrieves the opacity and
     * transparency color key of a layered window.
     *
     * @param hwnd
     *            Handle to the layered window. A layered window is created by
     *            specifying WS_EX_LAYERED when creating the window with the
     *            CreateWindowEx function or by setting WS_EX_LAYERED via
     *            SetWindowLong after the window has been created.
     * @param pcrKey
     *            Pointer to a COLORREF value that receives the transparency
     *            color key to be used when composing the layered window. All
     *            pixels painted by the window in this color will be
     *            transparent. This can be NULL if the argument is not needed.
     * @param pbAlpha
     *            Pointer to a BYTE that receives the Alpha value used to
     *            describe the opacity of the layered window. Similar to the
     *            SourceConstantAlpha member of the BLENDFUNCTION structure.
     *            When the variable referred to by pbAlpha is 0, the window is
     *            completely transparent. When the variable referred to by
     *            pbAlpha is 255, the window is opaque. This can be NULL if the
     *            argument is not needed.
     * @param pdwFlags
     *            Pointer to a DWORD that receives a layering flag. This can be
     *            NULL if the argument is not needed.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GetLayeredWindowAttributes(HWND hwnd, IntByReference pcrKey,
                        ByteByReference pbAlpha, IntByReference pdwFlags);

    /**
     * The UpdateLayeredWindow function updates the position, size, shape,
     * content, and translucency of a layered window.
     *
     * @param hwnd
     *            Handle to a layered window. A layered window is created by
     *            specifying WS_EX_LAYERED when creating the window with the
     *            CreateWindowEx function.
     * @param hdcDst
     *            Handle to a device context (DC) for the screen. This handle is
     *            obtained by specifying NULL when calling the function. It is
     *            used for palette color matching when the window contents are
     *            updated. If hdcDst isNULL, the default palette will be used.
     *            If hdcSrc is NULL, hdcDst must be NULL.
     * @param pptDst
     *            Pointer to a POINT structure that specifies the new screen
     *            position of the layered window. If the current position is not
     *            changing, pptDst can be NULL.
     * @param psize
     *            Pointer to a SIZE structure that specifies the new size of the
     *            layered window. If the size of the window is not changing,
     *            psize can be NULL. If hdcSrc is NULL, psize must be NULL.
     * @param hdcSrc
     *            Handle to a DC for the surface that defines the layered
     *            window. This handle can be obtained by calling the
     *            CreateCompatibleDC function. If the shape and visual context
     *            of the window are not changing, hdcSrc can be NULL.
     * @param pptSrc
     *            Pointer to a POINT structure that specifies the location of
     *            the layer in the device context. If hdcSrc is NULL, pptSrc
     *            should be NULL.
     * @param crKey
     *            Pointer to a COLORREF value that specifies the color key to be
     *            used when composing the layered window. To generate a
     *            COLORREF, use the RGB macro.
     * @param pblend
     *            Pointer to a BLENDFUNCTION structure that specifies the
     *            transparency value to be used when composing the layered
     *            window.
     * @param dwFlags
     *            ULW_* flags.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean UpdateLayeredWindow(HWND hwnd, HDC hdcDst, POINT pptDst,
                                SIZE psize, HDC hdcSrc, POINT pptSrc, int crKey,
                                BLENDFUNCTION pblend, int dwFlags);

    /**
     * This function sets the window region of a window. The window region
     * determines the area within the window where the system permits drawing.
     * The system does not display any portion of a window that lies outside of
     * the window region.
     *
     * @param hWnd
     *            Handle to the window whose window region is to be set.
     * @param hRgn
     *            Handle to a region. The function sets the window region of the
     *            window to this region. If hRgn is NULL, the function sets the
     *            window region to NULL.
     * @param bRedraw
     *            Specifies whether the system redraws the window after setting
     *            the window region. If bRedraw is TRUE, the system does so;
     *            otherwise, it does not. Typically, you set bRedraw to TRUE if
     *            the window is visible.
     * @return Nonzero indicates success. Zero indicates failure. To get
     *         extended error information, call GetLastError.
     */
    int SetWindowRgn(HWND hWnd, HRGN hRgn, boolean bRedraw);

    /**
     * The GetKeyboardState function copies the status of the 256 virtual keys
     * to the specified buffer.
     *
     * @param lpKeyState
     *            Pointer to the 256-byte array that receives the status data
     *            for each virtual key.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GetKeyboardState(byte[] lpKeyState);

    /**
     * This function determines whether a key is up or down at the time the
     * function is called, and whether the key was pressed after a previous call
     * to GetAsyncKeyState.
     *
     * @param vKey
     *            Specifies one of 256 possible virtual-key codes.
     * @return If the function succeeds, the return value specifies whether the
     *         key was pressed since the last call to GetAsyncKeyState, and
     *         whether the key is currently up or down. If the most significant
     *         bit is set, the key is down.
     */
    short GetAsyncKeyState(int vKey);

    /**
     * The SetWindowsHookEx function installs an application-defined hook
     * procedure into a hook chain. You would install a hook procedure to
     * monitor the system for certain types of events. These events are
     * associated either with a specific thread or with all threads in the same
     * desktop as the calling thread.
     *
     * @param idHook
     *            Specifies the type of hook procedure to be installed.
     * @param lpfn
     *            Pointer to the hook procedure.
     * @param hMod
     *            Handle to the DLL containing the hook procedure pointed to by
     *            the lpfn parameter.
     * @param dwThreadId
     *            Specifies the identifier of the thread with which the hook
     *            procedure is to be associated.
     * @return If the function succeeds, the return value is the handle to the
     *         hook procedure. If the function fails, the return value is NULL.
     *         To get extended error information, call GetLastError.
     */
    HHOOK SetWindowsHookEx(int idHook, HOOKPROC lpfn, HINSTANCE hMod, int dwThreadId);

    /**
     * The CallNextHookEx function passes the hook information to the next hook
     * procedure in the current hook chain. A hook procedure can call this
     * function either before or after processing the hook information.
     *
     * @param hhk
     *            Ignored.
     * @param nCode
     *            Specifies the hook code passed to the current hook procedure.
     *            The next hook procedure uses this code to determine how to
     *            process the hook information.
     * @param wParam
     *            Specifies the wParam value passed to the current hook
     *            procedure. The meaning of this parameter depends on the type
     *            of hook associated with the current hook chain.
     * @param lParam
     *            Specifies the lParam value passed to the current hook
     *            procedure. The meaning of this parameter depends on the type
     *            of hook associated with the current hook chain.
     * @return This value is returned by the next hook procedure in the chain.
     *         The current hook procedure must also return this value. The
     *         meaning of the return value depends on the hook type.
     */
    LRESULT CallNextHookEx(HHOOK hhk, int nCode, WPARAM wParam, LPARAM lParam);

    /**
     * The UnhookWindowsHookEx function removes a hook procedure installed in a
     * hook chain by the SetWindowsHookEx function.
     *
     * @param hhk
     *            Handle to the hook to be removed. This parameter is a hook
     *            handle obtained by a previous call to SetWindowsHookEx.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean UnhookWindowsHookEx(HHOOK hhk);

    /**
     * This function retrieves a message from the calling thread's message queue
     * and places it in the specified structure.
     *
     * @param lpMsg
     *            Pointer to an MSG structure that receives message information
     *            from the thread's message queue.
     * @param hWnd
     *            Handle to the window whose messages are to be retrieved. One
     *            value has a special meaning.
     * @param wMsgFilterMin
     *            Specifies the integer value of the lowest message value to be
     *            retrieved.
     * @param wMsgFilterMax
     *            Specifies the integer value of the highest message value to be
     *            retrieved.
     * @return If the function retrieves a message other than WM_QUIT, the
     *         return value is nonzero.
     *
     * <p>If the function retrieves the WM_QUIT message, the return value is zero.</p>
     *
     * <p>If there is an error, the return value is -1. For example, the function
     * fails if hWnd is an invalid window handle or lpMsg is an invalid pointer.
     * To get extended error information, call GetLastError.</p>
     */
    int GetMessage(MSG lpMsg, HWND hWnd, int wMsgFilterMin, int wMsgFilterMax);

    /**
     * This function checks a thread message queue for a message and places the
     * message (if any) in the specified structure.
     *
     * @param lpMsg
     *            Pointer to an MSG structure that receives message information.
     * @param hWnd
     *            Handle to the window whose messages are to be examined.
     * @param wMsgFilterMin
     *            Specifies the value of the first message in the range of
     *            messages to be examined.
     * @param wMsgFilterMax
     *            Specifies the value of the last message in the range of
     *            messages to be examined.
     * @param wRemoveMsg
     *            Specifies how messages are handled. This parameter can be one
     *            of the following values.
     * @return Nonzero indicates success. Zero indicates failure.
     */
    boolean PeekMessage(MSG lpMsg, HWND hWnd, int wMsgFilterMin,
            int wMsgFilterMax, int wRemoveMsg);

    /**
     * This function translates virtual-key messages into character messages.
     * The character messages are posted to the calling thread's message queue,
     * to be read the next time the thread calls the GetMessage or PeekMessage
     * function.
     *
     * @param lpMsg
     *            Pointer to an MSG structure that contains message information
     *            retrieved from the calling thread's message queue by using the
     *            GetMessage or PeekMessage function.
     * @return Nonzero indicates that the message is translated, that is, a
     *         character message is posted to the thread's message queue. If the
     *         message is WM_KEYDOWN or WM_SYSKEYDOWN, the return value is
     *         nonzero, regardless of the translation. Zero indicates that the
     *         message is not translated, that is, a character message is not
     *         posted to the thread's message queue.
     */
    boolean TranslateMessage(MSG lpMsg);

    /**
     * This function dispatches a message to a window procedure. It is typically
     * used to dispatch a message retrieved by the GetMessage function.
     *
     * @param lpMsg
     *            Pointer to an MSG structure that contains the message.
     * @return The return value specifies the value returned by the window
     *         procedure. Although its meaning depends on the message being
     *         dispatched, the return value generally is ignored.
     */
    LRESULT DispatchMessage(MSG lpMsg);

    /**
     * This function places a message in the message queue associated with the
     * thread that created the specified window and then returns without waiting
     * for the thread to process the message. Messages in a message queue are
     * retrieved by calls to the GetMessage or PeekMessage function.
     *
     * @param hWnd
     *            Handle to the window whose window procedure is to receive the
     *            message.
     * @param msg
     *            Specifies the message to be posted.
     * @param wParam
     *            Specifies additional message-specific information.
     * @param lParam
     *            Specifies additional message-specific information.
     */
    void PostMessage(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam);

    /**
     * Posts a message to the message queue of the specified thread. It returns
     * without waiting for the thread to process the message.
     * 
     * @param idThread The identifier of the thread to which the message is to 
     * be posted.
     * 
     * <p>The function fails if the specified thread does not have a 
     * message queue. The system creates a thread's message queue when the
     * thread makes its first call to one of the User or GDI functions.</p>
     * 
     * <p>Message posting is subject to UIPI. The thread of a process can post 
     * messages only to posted-message queues of threads in processes of lesser
     * or equal integrity level.</p>
     * 
     * <p>This thread must have the SE_TCB_NAME privilege to post a message to a
     * thread that belongs to a process with the same locally unique identifier
     * (LUID) but is in a different desktop. Otherwise, the function fails
     * and returns ERROR_INVALID_THREAD_ID.</p>
     * 
     * <p>This thread must either belong to the same desktop as the calling 
     * thread or to a process with the same LUID. Otherwise, the function
     * fails and returns ERROR_INVALID_THREAD_ID.</p>
     * 
     * @param Msg The type of message to be posted.
     *
     * @param wParam Additional message-specific information.
     * 
     * @param lParam Additional message-specific information.
     * 
     * @return If the function succeeds, the return value is nonzero.
     * 
     * <p>If the function fails, the return value is zero. To get extended error
     * information, call GetLastError.</p><p>GetLastError returns 
     * ERROR_INVALID_THREAD_ID if idThread is not a valid thread identifier, or
     * if the thread specified by idThread does not have a message queue.</p>
     * <p>GetLastError returns ERROR_NOT_ENOUGH_QUOTA when the message limit is hit.</p>
     */
    int PostThreadMessage(int  idThread, int  Msg, WPARAM wParam,  LPARAM lParam);
    
    /**
     * This function indicates to Windows that a thread has made a request to
     * terminate (quit). It is typically used in response to a WM_DESTROY
     * message.
     *
     * @param nExitCode
     *            Specifies an application exit code. This value is used as the
     *            wParam parameter of the WM_QUIT message.
     */
    void PostQuitMessage(int nExitCode);

    /**
     * The GetSystemMetrics function retrieves various system metrics (widths
     * and heights of display elements) and system configuration settings. All
     * dimensions retrieved by GetSystemMetrics are in pixels.
     *
     * @param nIndex
     *            System metric or configuration setting to retrieve. This
     *            parameter can be one of the following values. Note that all
     *            SM_CX* values are widths and all SM_CY* values are heights.
     *            Also note that all settings designed to return Boolean data
     *            represent TRUE as any nonzero value, and FALSE as a zero
     *            value.
     * @return If the function succeeds, the return value is the requested
     *         system metric or configuration setting. If the function fails,
     *         the return value is zero. GetLastError does not provide extended
     *         error information.
     */
    int GetSystemMetrics(int nIndex);

    /**
     * Changes the parent window of the specified child window.
     *
     * @param hWndChild
     *            A handle to the child window.
     *
     * @param hWndNewParent
     *            A handle to the new parent window. If this parameter is NULL,
     *            the desktop window becomes the new parent window. If this
     *            parameter is HWND_MESSAGE, the child window becomes a
     *            message-only window.
     *
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    HWND SetParent(HWND hWndChild, HWND hWndNewParent);

    /**
     * Determines the visibility state of the specified window.
     *
     * @param hWnd
     *            A handle to the window to be tested.
     *
     * @return If the specified window, its parent window, its parent's parent
     *         window, and so forth, have the WS_VISIBLE style, the return value
     *         is nonzero. Otherwise, the return value is zero.
     *
     *         Because the return value specifies whether the window has the
     *         WS_VISIBLE style, it may be nonzero even if the window is totally
     *         obscured by other windows.
     */
    boolean IsWindowVisible(HWND hWnd);

    /**
     * Changes the position and dimensions of the specified window. For a
     * top-level window, the position and dimensions are relative to the
     * upper-left corner of the screen. For a child window, they are relative to
     * the upper-left corner of the parent window's client area.
     *
     * @param hWnd
     *            A handle to the window.
     *
     * @param X
     *            The new position of the left side of the window.
     *
     * @param Y
     *            The new position of the top of the window.
     *
     * @param nWidth
     *            The new width of the window.
     *
     * @param nHeight
     *            The new height of the window.
     *
     * @param bRepaint
     *            Indicates whether the window is to be repainted. If this
     *            parameter is TRUE, the window receives a message. If the
     *            parameter is FALSE, no repainting of any kind occurs. This
     *            applies to the client area, the nonclient area (including the
     *            title bar and scroll bars), and any part of the parent window
     *            uncovered as a result of moving a child window.
     *
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean MoveWindow(HWND hWnd, int X, int Y, int nWidth, int nHeight, boolean bRepaint);

    /**
     * Changes the size, position, and Z order of a child, pop-up, or top-level
     * window. These windows are ordered according to their appearance on the
     * screen. The topmost window receives the highest rank and is the first
     * window in the Z order.
     *
     * @param hWnd
     *            A handle to the window.
     *
     * @param hWndInsertAfter
     *            A handle to the window to precede the positioned window in the
     *            Z order.
     *
     * @param X
     *            The new position of the left side of the window, in client
     *            coordinates.
     *
     * @param Y
     *            The new position of the top of the window, in client
     *            coordinates.
     *
     * @param cx
     *            The new width of the window, in pixels.
     *
     * @param cy
     *            The new height of the window, in pixels.
     *
     * @param uFlags
     *            The window sizing and positioning flags.
     *
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean SetWindowPos(HWND hWnd, HWND hWndInsertAfter, int X, int Y, int cx,
                         int cy, int uFlags);

    /**
     * Attaches or detaches the input processing mechanism of one thread to that
     * of another thread.
     *
     * @param idAttach
     *            The identifier of the thread to be attached to another thread.
     *            The thread to be attached cannot be a system thread.
     *
     * @param idAttachTo
     *            The identifier of the thread to which idAttach will be
     *            attached. This thread cannot be a system thread. A thread
     *            cannot attach to itself. Therefore, idAttachTo cannot equal
     *            idAttach.
     *
     * @param fAttach
     *            If this parameter is TRUE, the two threads are attached. If
     *            the parameter is FALSE, the threads are detached.
     *
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean AttachThreadInput(DWORD idAttach, DWORD idAttachTo, boolean fAttach);

    /**
     * Brings the thread that created the specified window into the foreground
     * and activates the window. Keyboard input is directed to the window, and
     * various visual cues are changed for the user. The system assigns a
     * slightly higher priority to the thread that created the foreground window
     * than it does to other threads.
     *
     * @param hWnd
     *            A handle to the window that should be activated and brought to
     *            the foreground.
     *
     * @return If the window was brought to the foreground, the return value is
     *         nonzero.
     */
    boolean SetForegroundWindow(HWND hWnd);

    /**
     * Retrieves a handle to the foreground window (the window with which the
     * user is currently working). The system assigns a slightly higher priority
     * to the thread that creates the foreground window than it does to other
     * threads.
     *
     * @return The return value is a handle to the foreground window. The
     *         foreground window can be NULL in certain circumstances, such as
     *         when a window is losing activation.
     */
    HWND GetForegroundWindow();

    /**
     * Sets the keyboard focus to the specified window. The window must be
     * attached to the calling thread's message queue.
     *
     * @param hWnd
     *            A handle to the window that will receive the keyboard input.
     *            If this parameter is NULL, keystrokes are ignored.
     *
     * @return If the function succeeds, the return value is the handle to the
     *         window that previously had the keyboard focus. If the hWnd
     *         parameter is invalid or the window is not attached to the calling
     *         thread's message queue, the return value is NULL. To get extended
     *         error information, call GetLastError.
     */
    HWND SetFocus(HWND hWnd);

    /**
     * Synthesizes keystrokes, mouse motions, and button clicks.
     *
     * @param nInputs
     *            The number of structures in the pInputs array.
     *
     * @param pInputs
     *            An array of INPUT structures. Each structure represents an
     *            event to be inserted into the keyboard or mouse input stream.
     *
     * @param cbSize
     *            The size, in bytes, of an INPUT structure. If cbSize is not
     *            the size of an INPUT structure, the function fails.
     *
     * @return The function returns the number of events that it successfully
     *         inserted into the keyboard or mouse input stream. If the function
     *         returns zero, the input was already blocked by another thread. To
     *         get extended error information, call GetLastError.
     *
     *         This function fails when it is blocked by UIPI. Note that neither
     *         GetLastError nor the return value will indicate the failure was
     *         caused by UIPI blocking.
     */
    DWORD SendInput(DWORD nInputs, WinUser.INPUT[] pInputs, int cbSize);

    /**
     * Waits until the specified process has finished processing its initial
     * input and is waiting for user input with no input pending, or until the
     * time-out interval has elapsed.
     *
     * @param hProcess
     *            A handle to the process. If this process is a console
     *            application or does not have a message queue, WaitForInputIdle
     *            returns immediately.
     *
     * @param dwMilliseconds
     *            The time-out interval, in milliseconds. If dwMilliseconds is
     *            INFINITE, the function does not return until the process is
     *            idle.
     *
     * @return The following table shows the possible return values for this
     *         function.
     *         <table>
     *         <caption>Possible return values</caption>
     *         <tr>
     *         <th>Return code/value</th>
     *         <th>Description</th>
     *         </tr>
     *         <tr>
     *         <td>0</td>
     *         <td>The wait was satisfied successfully.</td>
     *         </tr>
     *         <tr>
     *         <td>WAIT_TIMEOUT</td>
     *         <td>The wait was terminated because the time-out interval
     *         elapsed.</td>
     *         </tr>
     *         <tr>
     *         <td>WAIT_FAILED</td>
     *         <td>An error occurred.</td>
     *         </tr>
     *         </table>
     */
    DWORD WaitForInputIdle(HANDLE hProcess, DWORD dwMilliseconds);

    /**
     * The InvalidateRect function adds a rectangle to the specified window's
     * update region. The update region represents the portion of the window's
     * client area that must be redrawn.
     *
     * @param hWnd
     *            A handle to the window whose update region has changed. If
     *            this parameter is NULL, the system invalidates and redraws all
     *            windows, not just the windows for this application, and sends
     *            the WM_ERASEBKGND and WM_NCPAINT messages before the function
     *            returns. Setting this parameter to NULL is not recommended.
     *
     * @param lpRect
     *            A pointer to a RECT structure that contains the client
     *            coordinates of the rectangle to be added to the update region.
     *            If this parameter is NULL, the entire client area is added to
     *            the update region.
     *
     * @param bErase
     *            Specifies whether the background within the update region is
     *            to be erased when the update region is processed. If this
     *            parameter is TRUE, the background is erased when the
     *            BeginPaint function is called. If this parameter is FALSE, the
     *            background remains unchanged.
     *
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero.
     */
    boolean InvalidateRect(HWND hWnd, RECT lpRect, boolean bErase);

    /**
     * The RedrawWindow function updates the specified rectangle or region in a
     * window's client area.
     *
     * @param hWnd
     *            A handle to the window to be redrawn. If this parameter is
     *            NULL, the desktop window is updated.
     *
     * @param lprcUpdate
     *            A pointer to a RECT structure containing the coordinates, in
     *            device units, of the update rectangle. This parameter is
     *            ignored if the hrgnUpdate parameter identifies a region.
     *
     * @param hrgnUpdate
     *            A handle to the update region. If both the hrgnUpdate and
     *            lprcUpdate parameters are NULL, the entire client area is
     *            added to the update region.
     *
     * @param flags
     *            One or more redraw flags. This parameter can be used to
     *            invalidate or validate a window, control repainting, and
     *            control which windows are affected by RedrawWindow.
     *
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero.
     */
    boolean RedrawWindow(HWND hWnd, RECT lprcUpdate, HRGN hrgnUpdate, DWORD flags);

    /**
     * Retrieves a handle to a window that has the specified relationship
     * (Z-Order or owner) to the specified window.
     *
     * @param hWnd
     *            A handle to a window. The window handle retrieved is relative
     *            to this window, based on the value of the uCmd parameter.
     *
     * @param uCmd
     *            The relationship between the specified window and the window
     *            whose handle is to be retrieved.
     *
     * @return If the function succeeds, the return value is a window handle. If
     *         no window exists with the specified relationship to the specified
     *         window, the return value is NULL. To get extended error
     *         information, call GetLastError.
     */
    HWND GetWindow(HWND hWnd, DWORD uCmd);

    /**
     * The UpdateWindow function updates the client area of the specified window
     * by sending a WM_PAINT message to the window if the window's update region
     * is not empty. The function sends a WM_PAINT message directly to the
     * window procedure of the specified window, bypassing the application
     * queue. If the update region is empty, no message is sent.
     *
     * @param hWnd
     *            Handle to the window to be updated.
     *
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero.
     */
    boolean UpdateWindow(HWND hWnd);

    /**
     * Sets the specified window's show state.
     *
     * @param hWnd
     *            A handle to the window.
     *
     * @param nCmdShow
     *            Controls how the window is to be shown. This parameter is
     *            ignored the first time an application calls ShowWindow, if the
     *            program that launched the application provides a STARTUPINFO
     *            structure. Otherwise, the first time ShowWindow is called, the
     *            value should be the value obtained by the WinMain function in
     *            its nCmdShow parameter.
     *
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean ShowWindow(HWND hWnd, int nCmdShow);

    /**
     * Minimizes (but does not destroy) the specified window.
     *
     * @param hWnd
     *            A handle to the window to be minimized.
     *
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean CloseWindow(HWND hWnd);

    /**
     * Defines a system-wide hot key.
     *
     * @param hWnd
     *            A handle to the window that will receive
     * @param id
     *            The identifier of the hot key
     * @param fsModifiers
     *            The keys that must be pressed in combination with the key
     *            specified by the uVirtKey parameter in order to generate the
     * @param vk
     *            The virtual-key code of the hot key
     * @return If the function succeeds, the return value is nonzero.
     *
     *         <p>If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError}.
     *         {@link WinUser#WM_HOTKEY} messages generated by the hot key
     *         {@link WinUser#WM_HOTKEY} message.</p>
     *         A combination of the following values
     *         <ul>
     *         <li>{@link WinUser#MOD_ALT} Either ALT key must be held down.</li>
     *         <li>{@link WinUser#MOD_CONTROL} Either CTRL key must be held
     *         down.</li>
     *         <li>{@link WinUser#MOD_NOREPEAT} Changes the hotkey behavior so
     *         that the keyboard auto-repeat does not yield multiple hotkey
     *         notifications. <p><b>
     *         Windows Vista and Windows XP/2000: This flag is not
     *         supported.</b></p></li>
     *         <li>{@link WinUser#MOD_SHIFT} Either SHIFT key must be held down.
     *         </li>
     *         <li>{@link WinUser#MOD_WIN} Either WINDOWS key was held down.
     *         These keys are labeled with the Windows logo.</li>
     *         </ul>
     */
    boolean RegisterHotKey(HWND hWnd, int id, int fsModifiers, int vk);

    /**
     * Frees a hot key previously registered by the calling thread.
     *
     * @param hWnd
     *            A handle to the window associated with the hot key to be
     *            freed. This parameter should be NULL if the hot key is not
     *            associated with a window.
     *
     * @param id
     *            The identifier of the hot key to be freed.
     *
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError}.
     */
    boolean UnregisterHotKey(Pointer hWnd, int id);

    /**
     * Retrieves the time of the last input event.
     *
     * @param plii
     *            structure that receives the time of the last input event
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError}.
     */
    boolean GetLastInputInfo(LASTINPUTINFO plii);

    /**
     * Registers a window class for subsequent use in calls to the CreateWindow
     * or CreateWindowEx function.
     *
     * @param lpwcx
     *            Type: const WNDCLASSEX* A pointer to a WNDCLASSEX structure.
     *            You must fill the structure with the appropriate class
     *            attributes before passing it to the function.
     *
     * @return If the function succeeds, the return value is a class atom that
     *         uniquely identifies the class being registered. This atom can
     *         only be used by the CreateWindow, CreateWindowEx, GetClassInfo,
     *         GetClassInfoEx, FindWindow, FindWindowEx, and UnregisterClass
     *         functions and the IActiveIMMap::FilterClientWindows method.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError}.
     */
    ATOM RegisterClassEx(WNDCLASSEX lpwcx);

    /**
     * Unregisters a window class, freeing the memory required for the class.
     *
     * @param lpClassName
     *            [in] Type: LPCTSTR
     *
     *            A null-terminated string or a class atom. If lpClassName is a
     *            string, it specifies the window class name. This class name
     *            must have been registered by a previous call to the
     *            RegisterClass or RegisterClassEx function. System classes,
     *            such as dialog box controls, cannot be unregistered. If this
     *            parameter is an atom, it must be a class atom created by a
     *            previous call to the RegisterClass or RegisterClassEx
     *            function. The atom must be in the low-order word of
     *            lpClassName; the high-order word must be zero.
     *
     * @param hInstance
     *            [in,optional] Type: HINSTANCE A handle to the instance of the
     *            module that created the class. *
     *
     * @return Type: BOOL If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError}.
     */
    boolean UnregisterClass(String lpClassName, HINSTANCE hInstance);

    /**
     * Creates an overlapped, pop-up, or child window with an extended window
     * style; otherwise, this function is identical to the CreateWindow
     * function. For more information about creating a window and for full
     * descriptions of the other parameters of CreateWindowEx, see CreateWindow.
     *
     * @param dwExStyle
     *            [in] Type: DWORD
     *
     *            The extended window style of the window being created. For a
     *            list of possible values,see Extended Window Styles.
     *
     * @param lpClassName
     *            [in, optional] Type: LPCTSTR
     *
     *            A null-terminated string or a class atom created by a previous
     *            call to the RegisterClass or RegisterClassEx function. The
     *            atom must be in the low-order word of lpClassName; the
     *            high-order word must be zero. If lpClassName is a string, it
     *            specifies the window class name. The class name can be any
     *            name registered with RegisterClass or RegisterClassEx,
     *            provided that the module that registers the class is also the
     *            module that creates the window. The class name can also be any
     *            of the predefined system class names.
     *
     * @param lpWindowName
     *            [in, optional] Type: LPCTSTR
     *
     *            The window name. If the window style specifies a title bar,
     *            the window title pointed to by lpWindowName is displayed in
     *            the title bar. When using CreateWindow to create controls,
     *            such as buttons, check boxes, and static controls, use
     *            lpWindowName to specify the text of the control. When creating
     *            a static control with the SS_ICON style, use lpWindowName to
     *            specify the icon name or identifier. To specify an identifier,
     *            use the syntax "#num".
     *
     * @param dwStyle
     *            [in] Type: DWORD
     *
     *            The style of the window being created. This parameter can be a
     *            combination of the window style values, plus the control
     *            styles indicated in the Remarks section.
     *
     * @param x
     *            [in] Type: int
     *
     *            The initial horizontal position of the window. For an
     *            overlapped or pop-up window, the x parameter is the initial
     *            x-coordinate of the window's upper-left corner, in screen
     *            coordinates. For a child window, x is the x-coordinate of the
     *            upper-left corner of the window relative to the upper-left
     *            corner of the parent window's client area. If x is set to
     *            CW_USEDEFAULT, the system selects the default position for the
     *            window's upper-left corner and ignores the y parameter.
     *            CW_USEDEFAULT is valid only for overlapped windows; if it is
     *            specified for a pop-up or child window, the x and y parameters
     *            are set to zero.
     *
     * @param y
     *            [in] Type: int
     *
     *            The initial vertical position of the window. For an overlapped
     *            or pop-up window, the y parameter is the initial y-coordinate
     *            of the window's upper-left corner, in screen coordinates. For
     *            a child window, y is the initial y-coordinate of the
     *            upper-left corner of the child window relative to the
     *            upper-left corner of the parent window's client area. For a
     *            list box y is the initial y-coordinate of the upper-left
     *            corner of the list box's client area relative to the
     *            upper-left corner of the parent window's client area.
     *
     *            If an overlapped window is created with the WS_VISIBLE style
     *            bit set and the x parameter is set to CW_USEDEFAULT, then the
     *            y parameter determines how the window is shown. If the y
     *            parameter is CW_USEDEFAULT, then the window manager calls
     *            ShowWindow with the SW_SHOW flag after the window has been
     *            created. If the y parameter is some other value, then the
     *            window manager calls ShowWindow with that value as the
     *            nCmdShow parameter.
     *
     * @param nWidth
     *            [in] Type: int
     *
     *            The width, in device units, of the window. For overlapped
     *            windows, nWidth is the window's width, in screen coordinates,
     *            or CW_USEDEFAULT. If nWidth is CW_USEDEFAULT, the system
     *            selects a default width and height for the window; the default
     *            width extends from the initial x-coordinates to the right edge
     *            of the screen; the default height extends from the initial
     *            y-coordinate to the top of the icon area. CW_USEDEFAULT is
     *            valid only for overlapped windows; if CW_USEDEFAULT is
     *            specified for a pop-up or child window, the nWidth and nHeight
     *            parameter are set to zero.
     *
     * @param nHeight
     *            [in] Type: int
     *
     *            The height, in device units, of the window. For overlapped
     *            windows, nHeight is the window's height, in screen
     *            coordinates. If the nWidth parameter is set to CW_USEDEFAULT,
     *            the system ignores nHeight.
     *
     * @param hWndParent
     *            [in, optional] Type: HWND
     *
     *            A handle to the parent or owner window of the window being
     *            created. To create a child window or an owned window, supply a
     *            valid window handle. This parameter is optional for pop-up
     *            windows.
     *
     *            To create a message-only window, supply HWND_MESSAGE or a
     *            handle to an existing message-only window.
     *
     * @param hMenu
     *            [in, optional] Type: HMENU
     *
     *            A handle to a menu, or specifies a child-window identifier,
     *            depending on the window style. For an overlapped or pop-up
     *            window, hMenu identifies the menu to be used with the window;
     *            it can be NULL if the class menu is to be used. For a child
     *            window, hMenu specifies the child-window identifier, an
     *            integer value used by a dialog box control to notify its
     *            parent about events. The application determines the
     *            child-window identifier; it must be unique for all child
     *            windows with the same parent window.
     *
     * @param hInstance
     *            [in, optional] Type: HINSTANCE
     *
     *            A handle to the instance of the module to be associated with
     *            the window.
     *
     * @param lpParam
     *            [in, optional] Type: LPVOID
     *
     *            Pointer to a value to be passed to the window through the
     *            CREATESTRUCT structure (lpCreateParams member) pointed to by
     *            the lParam param of the WM_CREATE message. This message is
     *            sent to the created window by this function before it returns.
     *
     *            If an application calls CreateWindow to create a MDI client
     *            window, lpParam should point to a CLIENTCREATESTRUCT
     *            structure. If an MDI client window calls CreateWindow to
     *            create an MDI child window, lpParam should point to a
     *            MDICREATESTRUCT structure. lpParam may be NULL if no
     *            additional data is needed.
     *
     * @return Type: HWND
     *
     *         If the function succeeds, the return value is a handle to the new
     *         window.
     *
     *         If the function fails, the return value is NULL. To get extended
     *         error information, call GetLastError.
     *
     *         <p>This function typically fails for one of the following reasons:</p>
     *         <ul>
     *         <li>an invalid parameter value</li>
     *         <li>the system class was registered by a different module</li>
     *         <li>The WH_CBT hook is installed and returns a failure code</li>
     *         <li>if one of the controls in the dialog template is not registered,
     *          or its window window procedure fails WM_CREATE or
     *         WM_NCCREATE</li>
     *         </ul>
     */
    HWND CreateWindowEx(int dwExStyle, String lpClassName,
                               String lpWindowName, int dwStyle, int x, int y, int nWidth,
                               int nHeight, HWND hWndParent, HMENU hMenu, HINSTANCE hInstance,
                               LPVOID lpParam);

    /**
     * Destroys the specified window. The function sends WM_DESTROY and
     * WM_NCDESTROY messages to the window to deactivate it and remove the
     * keyboard focus from it. The function also destroys the window's menu,
     * flushes the thread message queue, destroys timers, removes clipboard
     * ownership, and breaks the clipboard viewer chain (if the window is at the
     * top of the viewer chain).
     *
     * If the specified window is a parent or owner window, DestroyWindow
     * automatically destroys the associated child or owned windows when it
     * destroys the parent or owner window. The function first destroys child or
     * owned windows, and then it destroys the parent or owner window.
     *
     * DestroyWindow also destroys modeless dialog boxes created by the
     * CreateDialog function.
     *
     * @param hWnd
     *            [in] Type: HWND A handle to the window to be destroyed.
     *
     * @return Type: BOOL If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError}.
     */
    boolean DestroyWindow(HWND hWnd);

    /**
     * Retrieves information about a window class, including a handle to the
     * small icon associated with the window class. The GetClassInfo function
     * does not retrieve a handle to the small icon.
     *
     * @param hinst
     *            [in, optional] Type: HINSTANCE
     *
     *            A handle to the instance of the application that created the
     *            class. To retrieve information about classes defined by the
     *            system (such as buttons or list boxes), set this parameter to
     *            NULL.
     *
     * @param lpszClass
     *            [in] Type: LPCTSTR
     *
     *            The class name. The name must be that of a preregistered class
     *            or a class registered by a previous call to the RegisterClass
     *            or RegisterClassEx function. Alternatively, this parameter can
     *            be a class atom created by a previous call to RegisterClass or
     *            RegisterClassEx. The atom must be in the low-order word of
     *            lpszClass; the high-order word must be zero.
     *
     * @param lpwcx
     *            [out] Type: LPWNDCLASSEX
     *
     *            A pointer to a WNDCLASSEX structure that receives the
     *            information about the class.
     *
     * @return Type: BOOL If the function finds a matching class and
     *         successfully copies the data, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError} .
     */
    boolean GetClassInfoEx(HINSTANCE hinst, String lpszClass, WNDCLASSEX lpwcx);

    /**
     * Calls the default window procedure to provide default processing for any
     * window messages that an application does not process. This function
     * ensures that every message is processed. DefWindowProc is called with the
     * same parameters received by the window procedure.
     *
     * @param hWnd
     *            [in] Type: HWND
     *
     *            A handle to the window procedure that received the message.
     *
     * @param Msg
     *            [in] Type: UINT
     *
     *            The message.
     *
     * @param wParam
     *            [in] Type: WPARAM
     *
     *            Additional message information. The content of this parameter
     *            depends on the value of the Msg parameter.
     *
     * @param lParam
     *            [in] Type: LPARAM
     *
     *            Additional message information. The content of this parameter
     *            depends on the value of the Msg parameter.
     *
     * @return Type: LRESULT The return value is the result of the message
     *         processing and depends on the message.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError}.
     */
    LRESULT DefWindowProc(HWND hWnd, int Msg, WPARAM wParam, LPARAM lParam);

    /**
     * Registers the device or type of device for which a window will receive
     * notifications.
     *
     * @param hRecipient [in] A handle to the window or service that will receive
     *             device events for the devices specified in the
     *             NotificationFilter parameter. The same window handle can be
     *             used in multiple calls to RegisterDeviceNotification.
     *
     *             Services can specify either a window handle or service status
     *             handle.
     *
     * @param notificationFilter
     *            [in] A pointer to a block of data that specifies the type of
     *            device for which notifications should be sent. This block
     *            always begins with the DEV_BROADCAST_HDR structure. The data
     *            following this header is dependent on the value of the
     *            dbch_devicetype member, which can be
     *            DBT_DEVTYP_DEVICEINTERFACE or DBT_DEVTYP_HANDLE. For more
     *            information, see Remarks.
     *
     * @param Flags
     *            [in] This parameter can be one of the following values.
     *            DEVICE_NOTIFY_WINDOW_HANDLE0x00000000 The hRecipient parameter
     *            is a window handle.
     *
     *            DEVICE_NOTIFY_SERVICE_HANDLE0x00000001 The hRecipient
     *            parameter is a service status handle.
     *
     *            In addition, you can specify the following value.
     *
     *            DEVICE_NOTIFY_ALL_INTERFACE_CLASSES0x00000004 Notifies the
     *            recipient of device interface events for all device interface
     *            classes. (The dbcc_classguid member is ignored.)
     *
     *            This value can be used only if the dbch_devicetype member is
     *            DBT_DEVTYP_DEVICEINTERFACE.
     *
     * @return value
     *
     *         If the function succeeds, the return value is a device
     *         notification handle.
     *
     *         If the function fails, the return value is NULL. To get extended
     *         error information, call GetLastError.
     */
    HDEVNOTIFY RegisterDeviceNotification(HANDLE hRecipient, Structure notificationFilter, int Flags);

    /**
     * Closes the specified device notification handle.
     *
     * @param Handle [in] Device notification handle returned by the
     *         RegisterDeviceNotification function.
     *
     * @return Return value
     *
     *         If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean UnregisterDeviceNotification(HDEVNOTIFY Handle);

    /**
     * Defines a new window message that is guaranteed to be unique throughout the system.
     * The message value can be used when sending or posting messages.
     *
     * @param string
     *            The message to be registered.
     *
     * @return If the message is successfully registered, the return value is a message identifier in the range 0xC000 through 0xFFFF.
     *         <p>
     *         If the function fails, the return value is zero. To get extended error information, call GetLastError.
     *         </p>
     */
    int RegisterWindowMessage(String string);
    
    /**
     * Retrieves a handle to the display monitor that contains a specified point.
     * @param pt A POINT structure that specifies the point of interest in virtual-screen
     *        coordinates.
     * @param dwFlags Determines the function's return value if the window does not intersect
     *        any display monitor. This parameter can be one of the following values.
     *        <ul><li>MONITOR_DEFAULTTONEAREST</li>
     *        <li>MONITOR_DEFAULTTONULL</li>
     *        <li>MONITOR_DEFAULTTOPRIMARY</li></ul>
     * @return If the point is contained by a display monitor, the return value is an HMONITOR
     *        handle to that display monitor. If the point is not contained by a display monitor,
     *        the return value depends on the value of dwFlags.
     */
    HMONITOR MonitorFromPoint(POINT.ByValue pt, int dwFlags);

    /**
     * Retrieves a handle to the display monitor that has the largest area of intersection with
     * a specified rectangle.
     * @param lprc A pointer to a RECT structure that specifies the rectangle of interest in
     *        virtual-screen coordinates.
     * @param dwFlags Determines the function's return value if the window does not intersect
     *        any display monitor. This parameter can be one of the following values.
     *        <ul><li>MONITOR_DEFAULTTONEAREST</li>
     *        <li>MONITOR_DEFAULTTONULL</li>
     *        <li>MONITOR_DEFAULTTOPRIMARY</li></ul>
     * @return If the rectangle intersects one or more display monitor rectangles, the return
     *        value is an HMONITOR handle to the display monitor that has the largest area of
     *        intersection with the rectangle. If the rectangle does not intersect a display
     *        monitor, the return value depends on the value of dwFlags.
     */
    HMONITOR MonitorFromRect(RECT lprc, int dwFlags);

    /**
     * Retrieves a handle to the display monitor that has the largest area of intersection with
     * the bounding rectangle of a specified window.
     * <p>
     * If the window is currently minimized, MonitorFromWindow uses the rectangle of the window
     * before it was minimized.</p>
     * @param hwnd A handle to the window of interest.
     * @param dwFlags Determines the function's return value if the window does not intersect
     *        any display monitor. This parameter can be one of the following values.
     *        <ul><li>MONITOR_DEFAULTTONEAREST</li>
     *        <li>MONITOR_DEFAULTTONULL</li>
     *        <li>MONITOR_DEFAULTTOPRIMARY</li></ul>
     * @return If the window intersects one or more display monitor rectangles, the return value
     *        is an HMONITOR handle to the display monitor that has the largest area of
     *        intersection with the window. If the window does not intersect a display monitor,
     *        the return value depends on the value of dwFlags.
     */
    HMONITOR MonitorFromWindow(HWND hwnd, int dwFlags);

    /**
     * Retrieves information about a display monitor.
     * @param hMonitor A handle to the display monitor of interest.
     * @param lpmi A pointer to a {@link WinUser.MONITORINFO} structure that receives information about
     *        the specified display monitor.
     * @return If the function succeeds, the return value is nonzero. If the function
     *        fails, the return value is zero.
     */
    BOOL GetMonitorInfo(HMONITOR hMonitor, MONITORINFO lpmi);

    /**
     * Retrieves information about a display monitor.
     * @param hMonitor A handle to the display monitor of interest.
     * @param lpmi A pointer to a {@link WinUser.MONITORINFOEX} structure that receives information about
     *        the specified display monitor.
     * @return If the function succeeds, the return value is nonzero. If the function
     *        fails, the return value is zero.
     */
    BOOL GetMonitorInfo(HMONITOR hMonitor, MONITORINFOEX lpmi);

    /**
     * Enumerates display monitors (including invisible pseudo-monitors associated with the mirroring drivers)
     * that intersect a region formed by the intersection of a specified clipping rectangle and the visible
     * region of a device context. EnumDisplayMonitors calls an application-defined MonitorEnumProc callback
     * function once for each monitor that is enumerated. Note that GetSystemMetrics (SM_CMONITORS) counts
     * only the display monitors.
     *
     * @param hdc A handle to a display device context that defines the visible region of interest. If this
     *        parameter is NULL, the hdcMonitor parameter passed to the callback function will be NULL, and
     *        the visible region of interest is the virtual screen that encompasses all the displays on the
     *        desktop.
     * @param lprcClip A pointer to a RECT structure that specifies a clipping rectangle. The region of
     *        interest is the intersection of the clipping rectangle with the visible region specified by hdc.
     *        If hdc is non-NULL, the coordinates of the clipping rectangle are relative to the origin of the
     *        hdc. If hdc is NULL, the coordinates are virtual-screen coordinates. This parameter can be NULL
     *        if you don't want to clip the region specified by hdc.
     * @param lpfnEnum A pointer to an application-defined callback function.
     * @param dwData Application-defined data that EnumDisplayMonitors passes directly to the lpfnEnum function.
     * @return If the function succeeds, the return value is nonzero. If the function fails, the return value
     *        is zero.
     */
    BOOL EnumDisplayMonitors(HDC hdc, RECT lprcClip, MONITORENUMPROC lpfnEnum, LPARAM dwData);

    /**
     * Retrieves the show state and the restored, minimized, and maximized
     * positions of the specified window.
     *
     * @param hwnd A handle to the window.
     * @param lpwndpl A pointer to the WINDOWPLACEMENT structure that receives the
     *        show state and position information.
     * @return The number of characters copied to the specified buffer indicates
     *        success. Zero indicates failure. To get extended error
     *        information, call GetLastError.
     */
    BOOL GetWindowPlacement(HWND hwnd, WINDOWPLACEMENT lpwndpl);

    /**
     * Sets the show state and the restored, minimized, and maximized positions
     * of the specified window.
     *
     * @param hwnd A handle to the window.
     * @param lpwndpl A pointer to a WINDOWPLACEMENT structure that specifies the
     *        new show state and window positions.
     * @return The number of characters copied to the specified buffer indicates
     *        success. Zero indicates failure. To get extended error
     *        information, call GetLastError.
     */
    BOOL SetWindowPlacement(HWND hwnd, WINDOWPLACEMENT lpwndpl);

    /**
     * Calculates the required size of the window rectangle, based on the desired
     * client-rectangle size. The window rectangle can then be passed to the CreateWindow
     * function to create a window whose client area is the desired size.
     *
     * To specify an extended window style, use the AdjustWindowRectEx function.
     *
     * A client rectangle is the smallest rectangle that completely encloses a
     * client area. A window rectangle is the smallest rectangle that completely
     * encloses the window, which includes the client area and the nonclient area.
     *
     * The AdjustWindowRect function does not add extra space when a menu bar wraps
     * to two or more rows.
     *
     * The AdjustWindowRect function does not take the WS_VSCROLL or WS_HSCROLL
     * styles into account. To account for the scroll bars, call the GetSystemMetrics
     * function with SM_CXVSCROLL or SM_CYHSCROLL.
     *
     * @param lpRect A pointer to a RECT structure that contains the coordinates
     *        of the top-left and bottom-right corners  of the desired client area.
     *        When the function returns, the structure contains the coordinates
     *        of the top-left and bottom-right corners of the window to accommodate
     *        the desired client area.
     * @param dwStyle The window style of the window whose required size is to be
     *        calculated. Note that you cannot specify the WS_OVERLAPPED style.
     * @param bMenu Indicates whether the window has a menu.
     * @return The number of characters copied to the specified buffer indicates
     *        success. Zero indicates failure. To get extended error
     *        information, call GetLastError.
     */
    BOOL AdjustWindowRect(RECT lpRect, DWORD dwStyle, BOOL bMenu);

    /**
     * Calculates the required size of the window rectangle, based on the desired
     * client-rectangle size. The window rectangle can then be passed to the CreateWindowEx
     * function to create a window whose client area is the desired size.
     *
     * A client rectangle is the smallest rectangle that completely encloses a
     * client area. A window rectangle is the smallest rectangle that completely
     * encloses the window, which includes the client area and the nonclient area.
     *
     * The AdjustWindowRectEx function does not add extra space when a menu bar wraps
     * to two or more rows.
     *
     * The AdjustWindowRectEx function does not take the WS_VSCROLL or WS_HSCROLL
     * styles into account. To account for the scroll bars, call the GetSystemMetrics
     * function with SM_CXVSCROLL or SM_CYHSCROLL.
     *
     * @param lpRect A pointer to a RECT structure that contains the coordinates
     *        of the top-left and bottom-right corners  of the desired client area.
     *        When the function returns, the structure contains the coordinates
     *        of the top-left and bottom-right corners of the window to accommodate
     *        the desired client area.
     * @param dwStyle The window style of the window whose required size is to be
     *        calculated. Note that you cannot specify the WS_OVERLAPPED style.
     * @param bMenu Indicates whether the window has a menu.
     * @param dwExStyle The extended window style of the window whose required size
     *        is to be calculated.
     * @return The number of characters copied to the specified buffer indicates
     *        success. Zero indicates failure. To get extended error
     *        information, call GetLastError.
     */
    BOOL AdjustWindowRectEx(RECT lpRect, DWORD dwStyle, BOOL bMenu, DWORD dwExStyle);

    /**
     * Logs off the interactive user, shuts down the system, or shuts down and restarts
     * the system. It sends the WM_QUERYENDSESSION message to all applications to
     * determine if they can be terminated.
     *
     * When this function is called, the caller must specify whether or not applications
     * with unsaved changes should be forcibly closed. If the caller chooses not to force
     * these applications to close and an application with unsaved changes is running on
     * the console session, the shutdown will remain in progress until the user logged
     * into the console session aborts the shutdown, saves changes, closes the application,
     * or forces the application to close. During this period, the shutdown may not be
     * aborted except by the console user, and another shutdown may not be initiated.
     *
     * Calling this function with the value of the uFlags parameter set to EWX_FORCE avoids
     * this situation. Remember that doing this may result in loss of data.
     *
     * To set a shutdown priority for an application relative to other applications in the
     * system, use the SetProcessShutdownParameters function.
     *
     * During a shutdown or log-off operation, running applications are allowed a specific
     * amount of time to respond to the shutdown request. If this time expires before all
     * applications have stopped, the system displays a user interface that allows the user
     * to forcibly shut down the system or to cancel the shutdown request. If the EWX_FORCE
     * value is specified, the system forces running applications to stop when the time expires.
     *
     * If the EWX_FORCEIFHUNG value is specified, the system forces hung applications to close
     * and does not display the dialog box.
     *
     * Console processes receive a separate notification message, CTRL_SHUTDOWN_EVENT or
     * CTRL_LOGOFF_EVENT, as the situation warrants. A console process routes these messages
     * to its HandlerRoutine function. ExitWindowsEx sends these notification messages
     * asynchronously; thus, an application cannot assume that the console notification messages
     * have been handled when a call to ExitWindowsEx returns.
     *
     * To shut down or restart the system, the calling process must use the {@link com.sun.jna.platform.win32.Advapi32#AdjustTokenPrivileges}
     * function to enable the SE_SHUTDOWN_NAME privilege. For more information, see Running with Special Privileges.
     *
     * @param uFlags The shutdown type. This parameter must include one of EWX_HYBRID_SHUTDOWN,
     *        EWX_LOGOFF, EWX_POWEROFF, EWX_REBOOT, EWX_RESTARTAPPS, or EWX_SHUTDOWN. This
     *        parameter can optionally include one of EWX_FORCE or EWX_FORCEIFHUNG.
     * @param dReason The reason for initiating the shutdown. This parameter must be one
     *        of the system shutdown reason codes. If this parameter is zero, the
     *        SHTDN_REASON_FLAG_PLANNED reason code will not be set and therefore the
     *        default action is an undefined shutdown that is logged as "No title for
     *        this reason could be found". By default, it is also an unplanned shutdown.
     *        Depending on how the system is configured, an unplanned shutdown triggers
     *        the creation of a file that contains the system state information, which
     *        can delay shutdown. Therefore, do not use zero for this parameter.
     * @return If the function succeeds, the return value is nonzero. Because the function
     *        executes asynchronously, a nonzero return value indicates that the shutdown has been
     *        initiated. It does not indicate whether the shutdown will succeed. It is possible
     *        that the system, the user, or another application will abort the shutdown. If the
     *        function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    BOOL ExitWindowsEx(UINT uFlags, DWORD dReason);

    /**
     * Locks the workstation's display. Locking a workstation protects it from unauthorized use. The
     * LockWorkStation function is callable only by processes running on the interactive desktop.
     * In addition, the user must be logged on, and the workstation cannot already be locked.
     *
     * @return If the function succeeds, the return value is nonzero. Because the function executes
     *        asynchronously, a nonzero return value indicates that the operation has been initiated.
     *        It does not indicate whether the workstation has been successfully locked. If the
     *        function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    BOOL LockWorkStation();

    /**
     * Retrieves information about the specified icon or cursor.
     *
     * @param hIcon
     *            A handle to the icon or cursor.
     *            <p>
     *            To retrieve information about a standard icon or cursor,
     *            specify one of the following values and use the
     *            MAKEINTRESOURCE macro to create this value:
     *            {@link WinUser#IDC_APPSTARTING} etc.</p>
     * @param piconinfo
     *            A pointer to an ICONINFO structure. The function fills in the
     *            structure's members.
     * @return If the function succeeds, the return value is {@code true} and
     *         the function fills in the members of the specified ICONINFO
     *         structure.
     *         <p>
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError()}.</p>
     */
    boolean GetIconInfo(HICON hIcon, ICONINFO piconinfo);

    /**
     * Sends the specified message to one or more windows.
     *
     * @param hWnd
     *            A handle to the window whose window procedure will receive the
     *            message.
     *            <p>
     *            If this parameter is HWND_BROADCAST ((HWND)0xffff), the
     *            message is sent to all top-level windows in the system,
     *            including disabled or invisible unowned windows. The function
     *            does not return until each window has timed out. Therefore,
     *            the total wait time can be up to the value of uTimeout
     *            multiplied by the number of top-level windows.</p>
     * @param msg
     *            The message to be sent.
     * @param wParam
     *            Any additional message-specific information.
     * @param lParam
     *            Any additional message-specific information.
     * @param fuFlags
     *            The behavior of this function. This parameter can be one or
     *            more of the following values: {@link WinUser#SMTO_ABORTIFHUNG}
     *            etc.
     * @param uTimeout
     *            The duration of the time-out period, in milliseconds. If the
     *            message is a broadcast message, each window can use the full
     *            time-out period. For example, if you specify a five second
     *            time-out period and there are three top-level windows that
     *            fail to process the message, you could have up to a 15 second
     *            delay.
     * @param lpdwResult
     *            The result of the message processing. The value of this
     *            parameter depends on the message that is specified.
     * @return If the function succeeds, the return value is nonzero.
     *         SendMessageTimeout does not provide information about individual
     *         windows timing out if HWND_BROADCAST is used.
     *         <p>
     *         If the function fails or times out, the return value is 0. To get
     *         extended error information, call GetLastError. If GetLastError
     *         returns ERROR_TIMEOUT, then the function timed out.
     *         </p>
     *         Windows 2000: If {@link Kernel32#GetLastError()} returns 0, then
     *         the function timed out.
     */
        LRESULT SendMessageTimeout(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam,
            int fuFlags, int uTimeout, DWORDByReference lpdwResult);

    /**
     * Retrieves the specified value from the WNDCLASSEX structure associated
     * with the specified window.
     *
     * @param hWnd
     *            A handle to the window and, indirectly, the class to which the
     *            window belongs.
     * @param nIndex
     *            The value to be retrieved. To retrieve a value from the extra
     *            class memory, specify the positive, zero-based byte offset of
     *            the value to be retrieved. Valid values are in the range zero
     *            through the number of bytes of extra class memory, minus
     *            eight; for example, if you specified 24 or more bytes of extra
     *            class memory, a value of 16 would be an index to the third
     *            integer.To retrieve any other value from the WNDCLASSEX
     *            structure, specify one of the following values:
     *            {@link WinUser#GCW_ATOM} etc.
     * @return If the function succeeds, the return value is the requested
     *         value.
     *         <p>
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError()}.</p>
     */
    ULONG_PTR GetClassLongPtr(HWND hWnd, int nIndex);

    /**
     * @param pRawInputDeviceList
     *            An array of {@link WinUser.RAWINPUTDEVICELIST} structures for the devices
     *            attached to the system. If (@code null}, the number of devices is
     *            returned in <tt>puiNumDevices</tt>
     * @param puiNumDevices
     *            If <tt>pRawInputDeviceList</tt> is {@code null}, the function populates
     *            this variable with the number of devices attached to the system;
     *            otherwise, this variable specifies the number of {@link WinUser.RAWINPUTDEVICELIST}
     *            structures that can be contained in the buffer to which <tt>pRawInputDeviceList</tt>
     *            points. If this value is less than the number of devices attached to
     *            the system, the function returns the actual number of devices in this
     *            variable and fails with ERROR_INSUFFICIENT_BUFFER.
     * @param cbSize
     *            The size of a {@link WinUser.RAWINPUTDEVICELIST} structure, in bytes.
     * @return If the function is successful, the return value is the number of devices
     *             stored in the buffer pointed to by <tt>pRawInputDeviceList</tt>. On
     *             any other error, the function returns -1 and {@code GetLastError}
     *             returns the error indication.
     * @see WinUser.RAWINPUTDEVICELIST#sizeof()
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/ms645598(v=vs.85).aspx">GetRawInputDeviceList</A>
     */
    int GetRawInputDeviceList(RAWINPUTDEVICELIST[] pRawInputDeviceList, IntByReference puiNumDevices, int cbSize);


    /**
     * Retrieves a handle to the desktop window. The desktop window covers the
     * entire screen. The desktop window is the area on top of which other
     * windows are painted.
     *
     * @return Type: HWND The return value is a handle to the desktop window.
     */
    HWND GetDesktopWindow();

    /**
     * The PrintWindow function copies a visual window into the specified device
     * context (DC), typically a printer DC.
     *
     * @param hWnd
     *            A handle to the window that will be copied.
     * @param dest
     *            A handle to the destination device context.
     * @param flags
     *            The drawing options. It can be one of the following values:
     *            <br>
     *            PW_CLIENTONLY:<br>
     *            Only the client area of the window is copied to hdcBlt. By
     *            default, the entire window is copied.
     * @return If the function succeeds, it returns true (MSDN: a nonzero
     *         value). If the function fails, it returns false (MSDN: zero).
     */
    boolean PrintWindow(HWND hWnd, HDC dest, int flags);

    /**
     * Determines whether the specified window is enabled for mouse and keyboard
     * input.
     *
     * @param hWnd
     *            A handle to the window to be tested.
     * @return If the window is enabled, the return value is true (nonzero).<br>
     *         If the window is not enabled, the return value is false (zero).
     */
    boolean IsWindowEnabled(HWND hWnd);

    /**
     * Determines whether the specified window handle identifies an existing
     * window. <br>
     * A thread should not use IsWindow for a window that it did not create
     * because the window could be destroyed after this function was called.<br>
     * Further, because window handles are recycled the handle could even point
     * to a different window.
     *
     * @param hWnd
     *            A handle to the window to be tested.
     * @return If the window handle identifies an existing window, the return
     *         value is true (nonzero).<br>
     *         If the window handle does not identify an existing window, the
     *         return value is false (zero).
     */
    boolean IsWindow(HWND hWnd);

    /**
     * @param parent
     *            Type: <strong>HWND</strong><br>
     *            A handle to the parent window whose child windows are to be
     *            searched.<br>
     *            If <em>hwndParent</em> is <strong>NULL</strong>, the function
     *            uses the desktop window as the parent window. The function
     *            searches among windows that are child windows of the desktop.
     *            <br>
     *            If <em>hwndParent</em> is <strong>HWND_MESSAGE</strong>, the
     *            function searches all <a href=
     *            "https://msdn.microsoft.com/en-us/library/windows/desktop/ms632599(v=vs.85).aspx#message_only">
     *            message-only windows</a>.<br>
     * @param child
     *            Type: <strong>HWND</strong><br>
     *            A handle to a child window. The search begins with the next
     *            child window in the Z order. The child window must be a direct
     *            child window of <em>hwndParent</em>, not just a descendant
     *            window.<br>
     *            If <em>hwndChildAfter</em> is <strong>NULL</strong>, the
     *            search begins with the first child window of
     *            <em>hwndParent</em>.<br>
     *            Note that if both <em>hwndParent</em> and
     *            <em>hwndChildAfter</em> are <strong>NULL</strong>, the
     *            function searches all top-level and message-only windows.<br>
     * @param className
     *            Type: <strong>LPCTSTR</strong><br>
     *            The class name or a class atom created by a previous call to
     *            the <a href=
     *            "https://msdn.microsoft.com/en-us/library/windows/desktop/ms633586(v=vs.85).aspx">
     *            <strong xmlns="http://www.w3.org/1999/xhtml">RegisterClass
     *            </strong></a> or <a href=
     *            "https://msdn.microsoft.com/en-us/library/windows/desktop/ms633587(v=vs.85).aspx">
     *            <strong xmlns="http://www.w3.org/1999/xhtml">RegisterClassEx
     *            </strong></a> function. The atom must be placed in the
     *            low-order word of <em>lpszClass</em>; the high-order word must
     *            be zero.<br>
     *            If <em>lpszClass</em> is a string, it specifies the window
     *            class name. The class name can be any name registered with
     *            <a href=
     *            "https://msdn.microsoft.com/en-us/library/windows/desktop/ms633586(v=vs.85).aspx">
     *            <strong xmlns="http://www.w3.org/1999/xhtml">RegisterClass
     *            </strong></a> or <a href=
     *            "https://msdn.microsoft.com/en-us/library/windows/desktop/ms633587(v=vs.85).aspx">
     *            <strong xmlns="http://www.w3.org/1999/xhtml">RegisterClassEx
     *            </strong></a>, or any of the predefined control-class names,
     *            or it can be <code>MAKEINTATOM(0x8000)</code>. In this latter
     *            case, 0x8000 is the atom for a menu class. For more
     *            information, see the Remarks section of this topic.<br>
     * @param window
     *            Type: <strong>LPCTSTR</strong><br>
     *            The window name (the window's title). If this parameter is
     *            <strong>NULL</strong>, all window names match.
     * @return If the function succeeds, the return value is a handle to the
     *         window that has the specified class and window names. <br>
     *         If the function fails, the return value is NULL. To get extended
     *         error information, call GetLastError.
     */
    HWND FindWindowEx(HWND parent, HWND child, String className, String window);

    /**
     * Retrieves the handle to the ancestor of the specified window.
     *
     * @param hwnd
     *            A handle to the window whose ancestor is to be retrieved. If
     *            this parameter is the desktop window, the function returns
     *            NULL.
     * @param gaFlags
     *            The ancestor to be retrieved. This parameter can be one of the
     *            following values:<br>
     *            <ul>
     *            <li>GA_PARENT (1) -- Retrieves the parent window. This does
     *            not include the owner, as it does with the GetParent function.
     *            </li>
     *            <li>GA_ROOT (2) -- Retrieves the root window by walking the
     *            chain of parent windows..</li>
     *            <li>GA_ROOTOWNER (3) -- Retrieves the owned root window by
     *            walking the chain of parent and owner windows returned by
     *            GetParent..</li>
     *            </ul>
     * @return The return value is the handle to the ancestor window.
     */
    HWND GetAncestor(HWND hwnd, int gaFlags);

    /**
     * Retrieves the position of the mouse cursor, in screen coordinates.
     *
     * @param p
     *            lpPoint [out]<br>
     *            Type: LPPOINT<br>
     *            A pointer to a POINT structure that receives the screen
     *            coordinates of the cursor.
     * @return Type: BOOL.<br>
     *         Returns nonzero if successful or zero otherwise. To get extended
     *         error information, call GetLastError.
     */
    boolean GetCursorPos(POINT p);

    /**
     * Moves the cursor to the specified screen coordinates.<br>
     * If the new coordinates are not within the screen rectangle set by the
     * most recent ClipCursor function call, the system automatically adjusts
     * the coordinates so that the cursor stays within the rectangle.
     *
     * @param x
     *            The new x-coordinate of the cursor, in screen coordinates.
     * @param y
     *            The new y-coordinate of the cursor, in screen coordinates.
     * @return Type: BOOL.<br>
     *         Returns nonzero if successful or zero otherwise. To get extended
     *         error information, call GetLastError.
     */
    boolean SetCursorPos(long x, long y);

    /**
     * @param eventMin
     *            Type: UINT<br>
     *            Specifies the event constant for the lowest event value in the
     *            range of events that are handled by the hook function.<br>
     *            This parameter can be set to EVENT_MIN to indicate the lowest
     *            possible event value.
     * @param eventMax
     *            Type: UINT<br>
     *            Specifies the event constant for the highest event value in
     *            the range of events that are handled by the hook function.<br>
     *            This parameter can be set to EVENT_MAX to indicate the highest
     *            possible event value.
     * @param hmodWinEventProc
     *            Type: HMODULE<br>
     *            Handle to the DLL that contains the hook function at
     *            lpfnWinEventProc, if the WINEVENT_INCONTEXT flag is specified
     *            in the dwFlags parameter.<br>
     *            If the hook function is not located in a DLL, or if the
     *            WINEVENT_OUTOFCONTEXT flag is specified, this parameter is
     *            NULL.
     * @param winEventProc
     *            Type: WINEVENTPROC<br>
     *            Pointer to the event hook function. For more information about
     *            this function, see WinEventProc.
     * @param processID
     *            Type: DWORD<br>
     *            Specifies the ID of the process from which the hook function
     *            receives events.<br>
     *            Specify zero (0) to receive events from all processes on the
     *            current desktop.
     * @param threadID
     *            Type: DWORD<br>
     *            Specifies the ID of the thread from which the hook function
     *            receives events.<br>
     *            If this parameter is zero, the hook function is associated
     *            with all existing threads on the current desktop.
     * @param flags
     *            Type: UINT<br>
     *            Flag values that specify the location of the hook function and
     *            of the events to be skipped.<br>
     *            The following flags are valid:<br>
     *            <ul>
     *            <li><b> WINEVENT_INCONTEXT:</b> The DLL that contains the
     *            callback function is mapped into the address space of the
     *            process that generates the event.<br>
     *            With this flag, the system sends event notifications to the
     *            callback function as they occur.<br>
     *            The hook function must be in a DLL when this flag is
     *            specified. <br>
     *            This flag has no effect when both the calling process and the
     *            generating process are not 32-bit or 64-bit processes, or when
     *            the generating process is a console application.<br>
     *            For more information, see In-Context Hook Functions.</li>
     *            <li><b>WINEVENT_OUTOFCONTEXT:</b> The callback function is not
     *            mapped into the address space of the process that generates
     *            the event.<br>
     *            Because the hook function is called across process boundaries,
     *            the system must queue events.<br>
     *            Although this method is asynchronous, events are guaranteed to
     *            be in sequential order. <br>
     *            For more information, see Out-of-Context Hook Functions.</li>
     *            <li><b>WINEVENT_SKIPOWNPROCESS:</b><br>
     *            Prevents this instance of the hook from receiving the events
     *            that are generated by threads in this process. <br>
     *            This flag does not prevent threads from generating events.
     *            </li>
     *            <li><b>WINEVENT_SKIPOWNTHREAD:</b><br>
     *            Prevents this instance of the hook from receiving the events
     *            that are generated by the thread that is registering this
     *            hook.</li>
     *            </ul>
     * @return Type: HWINEVENTHOOK<br>
     *         If successful, returns an HWINEVENTHOOK value that identifies
     *         this event hook instance. Applications save this return value to
     *         use it with the UnhookWinEvent function.<br>
     *         If unsuccessful, returns zero.<br>
     */
    HANDLE SetWinEventHook(int eventMin, int eventMax, HMODULE hmodWinEventProc, WinEventProc winEventProc,
            int processID, int threadID, int flags);

    /**
     * Removes an event hook function created by a previous call to
     * SetWinEventHook.
     *
     * @param hook
     *            Type: HWINEVENTHOOK<br>
     *            Handle to the event hook returned in the previous call to
     *            SetWinEventHook.
     * @return Type: BOOL If successful, returns TRUE; otherwise, returns FALSE.
     *         <br>
     *         Three common errors cause this function to fail:<br>
     *         The hWinEventHook parameter is NULL or not valid.<br>
     *         The event hook specified by hWinEventHook was already removed.
     *         <br>
     *         UnhookWinEvent is called from a thread that is different from the
     *         original call to SetWinEventHook.<br>
     */
    boolean UnhookWinEvent(HANDLE hook);

    /**
     * Copies the specified icon from another module to the current module.
     *
     * @param hIcon
     *            A handle to the icon to be copied.
     * @return If the function succeeds, the return value is a handle to the
     *         duplicate icon. <br>
     *         If the function fails, the return value is NULL. To get extended
     *         error information, call GetLastError.
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms648058(S.85).aspx">MSDN</a>
     */
    HICON CopyIcon(HICON hIcon);

    /**
     * Retrieves the specified 32-bit (DWORD) value from the WNDCLASSEX
     * structure associated with the specified window. Note If you are
     * retrieving a pointer or a handle, this function has been superseded by
     * the GetClassLongPtr function. <br>
     * (Pointers and handles are 32 bits on 32-bit Windows and 64 bits on 64-bit
     * Windows.)
     *
     * @param hWnd
     *            A handle to the window and, indirectly, the class to which the
     *            window belongs.
     * @param nIndex
     *            The value to be retrieved. To retrieve a value from the extra
     *            class memory, specify the positive, zero-based byte offset of
     *            the value to be retrieved. Valid values are in the range zero
     *            through the number of bytes of extra class memory, minus four;
     *            for example, if you specified 12 or more bytes of extra class
     *            memory, a value of 8 would be an index to the third integer.
     *            To retrieve any other value from the WNDCLASSEX structure,
     *            specify one of the following values.<br> GCW_ATOM: -32<br>
     *            Retrieves an ATOM value that uniquely identifies the window
     *            class. This is the same atom that the RegisterClassEx function
     *            returns.<br>
     *            GCL_CBCLSEXTRA: -20<br>
     *            Retrieves the size, in bytes, of the extra memory associated
     *            with the class.<br>
     *            GCL_CBWNDEXTRA: -18<br>
     *            Retrieves the size, in bytes, of the extra window memory
     *            associated with each window in the class. For information on
     *            how to access this memory, see GetWindowLong.<br>
     *            GCL_HBRBACKGROUND: -10<br>
     *            Retrieves a handle to the background brush associated with the
     *            class.<br>
     *            GCL_HCURSOR: -12<br>
     *            Retrieves a handle to the cursor associated with the class.
     *            <br>
     *            GCL_HICON: -14<br>
     *            Retrieves a handle to the icon associated with the class.<br>
     *            GCL_HICONSM: -34<br>
     *            Retrieves a handle to the small icon associated with the
     *            class.<br>
     *            GCL_HMODULE: -16<br>
     *            Retrieves a handle to the module that registered the class.
     *            <br>
     *            GCL_MENUNAME: -8<br>
     *            Retrieves the address of the menu name string. The string
     *            identifies the menu resource associated with the class.<br>
     *            GCL_STYLE: -26<br>
     *            Retrieves the window-class style bits.<br>
     *            GCL_WNDPROC: -24<br>
     *            Retrieves the address of the window procedure, or a handle
     *            representing the address of the window procedure. You must use
     *            the CallWindowProc function to call the window procedure.
     * @return Type: DWORD If the function succeeds, the return value is the
     *         requested value.<br>
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.<br>
     */
    int GetClassLong(HWND hWnd, int nIndex);
    
    /**
     * Registers a new clipboard format. This format can then be used as a 
     * valid clipboard format. 
     * 
     * @param formatName The name of the new format. 
     * 
     * @return If the function succeeds, the return value identifies the 
     * registered clipboard format.
     * 
     * <p> If the function fails, the return value is zero. To get extended
     * error information, call GetLastError.</p>
     */
    public int RegisterClipboardFormat(String formatName);
    
    /**
     * Retrieves the window handle to the active window attached to the 
     * calling thread's message queue.
     * 
     * @return Type: HWND The return value is the handle to the active 
     * window attached to the calling thread's message queue. Otherwise, 
     * the return value is NULL.
     */
    public HWND GetActiveWindow();
  
	/**
	 * Sends the specified message to a window or windows. 
	 * The SendMessage function calls the window procedure for the specified window and 
	 * does not return until the window procedure has processed the message.
	 * 
 	 * To send a message and return immediately, use the SendMessageCallback or SendNotifyMessage function. 
 	 * To post a message to a thread's message queue and return immediately, 
 	 * use the PostMessage or PostThreadMessage function.
	 * 
	 * @param hWnd A handle to the window whose window procedure will receive the message. 
	 * 		If this parameter is HWND_BROADCAST ((HWND)0xffff), the message is sent to all 
	 * 		top-level windows in the system, including disabled or invisible unowned windows, 
	 * 		overlapped windows, and pop-up windows; but the message is not sent to child windows.
	 * 		Message sending is subject to UIPI. The thread of a process can send messages 
	 * 		only to message queues of threads in processes of lesser or equal integrity level.
	 * 
	 * @param msg The message to be sent.
	 * 		For lists of the system-provided messages, see System-Defined Messages.
	 * @param wParam Additional message-specific information.
	 * @param lParam Additional message-specific information.
	 * 
	 * @return The return value specifies the result of the message processing; it depends on the message sent.
	 * 
	 * Two classic usage : 
	 *  - with a WM_USER+x msg value : wParam and lParam are numeric values
	 *  - with a WM_COPYDATA msg value : wParam is the length of the structure and lParam a pointer to a COPYDATASTRUCT 
	 */
    LRESULT SendMessage(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam);
	
}
