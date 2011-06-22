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
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HRGN;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.BLENDFUNCTION;
import com.sun.jna.platform.win32.WinUser.FLASHWINFO;
import com.sun.jna.platform.win32.WinUser.GUITHREADINFO;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.POINT;
import com.sun.jna.platform.win32.WinUser.SIZE;
import com.sun.jna.platform.win32.WinUser.WINDOWINFO;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
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
 */
public interface User32 extends StdCallLibrary {

	User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
			W32APIOptions.DEFAULT_OPTIONS);

	/**
	 * This function retrieves a handle to a display device context (DC) for the
	 * client area of the specified window. The display device context can be
	 * used in subsequent graphics display interface (GDI) functions to draw in
	 * the client area of the window.
	 * @param hWnd
	 *  Handle to the window whose device context is to be retrieved.
	 *  If this value is NULL, GetDC retrieves the device context for
	 *  the entire screen.
	 * @return 
	 *  The handle the device context for the specified window's client
	 *  area indicates success. NULL indicates failure. To get extended
	 *  error information, call GetLastError.
	 */
	HDC GetDC(HWND hWnd);

	/**
	 * This function releases a device context (DC), freeing it for use by other
	 * applications. The effect of ReleaseDC depends on the type of device
	 * context.
	 * 
	 * @param hWnd
	 *  Handle to the window whose device context is to be released.
	 * @param hDC
	 *  Handle to the device context to be released.
	 * @return 
	 *  The return value specifies whether the device context is
	 *  released. 1 indicates that the device context is released. Zero
	 *  indicates that the device context is not released.
	 */
	int ReleaseDC(HWND hWnd, HDC hDC);

	/**
	 * This function retrieves the handle to the top-level window whose class name and 
	 * window name match the specified strings. This function does not search child windows.
	 * @param lpClassName 
	 *  Long pointer to a null-terminated string that specifies the class name or is an atom 
	 *  that identifies the class-name string. If this parameter is an atom, it must be a 
	 *  global atom created by a previous call to the GlobalAddAtom function. The atom, a 
	 *  16-bit value, must be placed in the low-order word of lpClassName; the high-order 
	 *  word must be zero. 
	 * @param lpWindowName
	 *  Long pointer to a null-terminated string that specifies the window name (the window's 
	 *  title). If this parameter is NULL, all window names match. 
	 * @return
	 *  A handle to the window that has the specified class name and window name indicates 
	 *  success. NULL indicates failure. To get extended error information, call GetLastError.
	 */
	HWND FindWindow(String lpClassName, String lpWindowName);

	/**
	 * This function retrieves the name of the class to which the specified window belongs. 
	 * @param hWnd
	 *  Handle to the window and, indirectly, the class to which the window belongs. 
	 * @param lpClassName
	 *  Long pointer to the buffer that is to receive the class name string.
	 * @param nMaxCount
	 *  Specifies the length, in characters, of the buffer pointed to by the lpClassName 
	 *  parameter. The class name string is truncated if it is longer than the buffer.
	 * @return
	 *  The number of characters copied to the specified buffer indicates success. Zero 
	 *  indicates failure. To get extended error information, call GetLastError. 
	 */
	int GetClassName(HWND hWnd, char[] lpClassName, int nMaxCount);

	/**
	 * Retrieves information about the active window or a specified graphical user 
	 * interface (GUI) thread. 
	 * @param idThread
	 *  Identifies the thread for which information is to be retrieved. To retrieve 
	 *  this value, use the GetWindowThreadProcessId function. If this parameter is NULL,
	 *  the function returns information for the foreground thread. 
	 * @param lpgui
	 *  Pointer to a GUITHREADINFO structure that receives information describing the thread. 
	 *  Note that you must set GUITHREADINFO.cbSize to sizeof(GUITHREADINFO) before calling this function. 
	 * @return
	 *  If the function succeeds, the return value is nonzero.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError. 
	 */
	boolean GetGUIThreadInfo(int idThread, GUITHREADINFO lpgui);

	/**
	 * The GetWindowInfo function retrieves information about the specified window.
	 * @param hWnd
	 *  Handle to the window whose information is to be retrieved.
	 * @param pwi
	 *  Pointer to a WINDOWINFO structure to receive the information. Note that you must set WINDOWINFO.cbSize 
	 *  to sizeof(WINDOWINFO) before calling this function.
	 * @return
	 *  If the function succeeds, the return value is nonzero.
	 *  If the function fails, the return value is zero. 
	 */
	boolean GetWindowInfo(HWND hWnd, WINDOWINFO pwi);

	/**
	 * This function retrieves the dimensions of the bounding rectangle of the specified window. The 
	 * dimensions are given in screen coordinates that are relative to the upper-left corner of the screen.
	 * @param hWnd
	 *  Handle to the window. 
	 * @param rect
	 *  Long pointer to a RECT structure that receives the screen coordinates of the upper-left and lower-right 
	 *  corners of the window. 
	 * @return
	 *  Nonzero indicates success. Zero indicates failure. To get extended error information, call GetLastError.
	 */
	boolean GetWindowRect(HWND hWnd, RECT rect);

	/**
	 * This function copies the text of the specified window's title bar - if it has one - into a buffer. If 
	 * the specified window is a control, the text of the control is copied.
	 * @param hWnd
	 *  Handle to the window or control containing the text. 
	 * @param lpString
	 *  Long pointer to the buffer that will receive the text. 
	 * @param nMaxCount
	 *  Specifies the maximum number of characters to copy to the buffer, including the NULL character. 
	 *  If the text exceeds this limit, it is truncated. 
	 * @return
	 *  The length, in characters, of the copied string, not including the terminating null character, 
	 *  indicates success. Zero indicates that the window has no title bar or text, if the title bar is
	 *  empty, or if the window or control handle is invalid. To get extended error information, call 
	 *  GetLastError. This function cannot retrieve the text of an edit control in another application.
	 */
	int GetWindowText(HWND hWnd, char[] lpString, int nMaxCount);

	/**
	 * This function retrieves the length, in characters, of the specified window's title bar text -
	 * if the window has a title bar. If the specified window is a control, the function retrieves the
	 * length of the text within the control.
	 * @param hWnd
	 *  Handle to the window or control. 
	 * @return
	 *  The length, in characters, of the text indicates success. Under certain conditions, this value 
	 *  may actually be greater than the length of the text. Zero indicates that the window has no text. 
	 *  To get extended error information, call GetLastError.
	 */
	int GetWindowTextLength(HWND hWnd);

	/**
	 * The GetWindowModuleFileName function retrieves the full path and file name of the module associated
	 * with the specified window handle.
	 * @param hWnd
	 *  Handle to the window whose module file name will be retrieved.
	 * @param lpszFileName
	 *  Pointer to a buffer that receives the path and file name.
	 * @param cchFileNameMax
	 *  Specifies the maximum number of TCHARs that can be copied into the lpszFileName buffer.
	 * @return
	 *  The return value is the total number of TCHARs copied into the buffer.
	 */
	int GetWindowModuleFileName(HWND hWnd, char[] lpszFileName,
			int cchFileNameMax);

	/**
	 * This function retrieves the identifier of the thread that created the specified window and, optionally,
	 * the identifier of the process that created the window.
	 * @param hWnd
	 *  Handle to the window.
	 * @param lpdwProcessId
	 *  Pointer to a 32-bit value that receives the process identifier. If this parameter is not NULL, 
	 *  GetWindowThreadProcessId copies the identifier of the process to the 32-bit value; otherwise, 
	 *  it does not. 
	 * @return
	 *  The return value is the identifier of the thread that created the window.
	 */
	int GetWindowThreadProcessId(HWND hWnd, IntByReference lpdwProcessId);

	/**
	 * This function enumerates all top-level windows on the screen by passing the handle to each window,
	 * in turn, to an application-defined callback function. EnumWindows continues until the last top-level
	 * window is enumerated or the callback function returns FALSE.
	 * @param lpEnumFunc
	 *  Long pointer to an application-defined callback function.
	 * @param data
	 *  Specifies an application-defined value to be passed to the callback function.
	 * @return
	 *  Nonzero indicates success. Zero indicates failure. To get extended error information, call GetLastError.
	 */
	boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer data);

	/**
	 * The EnumChildWindows function enumerates the child windows that belong to the specified parent window
	 * by passing the handle to each child window, in turn, to an application-defined callback function. 
	 * EnumChildWindows continues until the last child window is enumerated or the callback function returns FALSE.
	 * @param hWnd
	 *  Handle to the parent window whose child windows are to be enumerated. If this parameter is NULL, this 
	 *  function is equivalent to EnumWindows.
	 * @param lpEnumFunc
	 *  Pointer to an application-defined callback function.
	 * @param data
	 *  Specifies an application-defined value to be passed to the callback function.
	 * @return
	 *  If the function succeeds, the return value is nonzero.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError. 
	 *  If EnumChildProc returns zero, the return value is also zero. In this case, the callback function 
	 *  should call SetLastError to obtain a meaningful error code to be returned to the caller of 
	 *  EnumChildWindows.
	 */
	boolean EnumChildWindows(HWND hWnd, WNDENUMPROC lpEnumFunc, Pointer data);

	/**
	 * The EnumThreadWindows function enumerates all nonchild windows associated with a thread by passing 
	 * the handle to each window, in turn, to an application-defined callback function. EnumThreadWindows
	 * continues until the last window is enumerated or the callback function returns FALSE. To enumerate
	 * child windows of a particular window, use the EnumChildWindows function.
	 * @param dwThreadId
	 *  Identifies the thread whose windows are to be enumerated.
	 * @param lpEnumFunc
	 *  Pointer to an application-defined callback function. 
	 * @param data
	 *  Specifies an application-defined value to be passed to the callback function.
	 * @return
	 *  If the callback function returns TRUE for all windows in the thread specified by dwThreadId, the 
	 *  return value is TRUE. If the callback function returns FALSE on any enumerated window, or if there
	 *  are no windows found in the thread specified by dwThreadId, the return value is FALSE.
	 */
	boolean EnumThreadWindows(int dwThreadId, WNDENUMPROC lpEnumFunc,
			Pointer data);

	/**
	 * The FlashWindowEx function flashes the specified window. It does not change the active state of the window.
	 * @param pfwi
	 *  Pointer to the FLASHWINFO structure. 
	 * @return
	 *  The return value specifies the window's state before the call to the FlashWindowEx function. If the window
	 *  caption was drawn as active before the call, the return value is nonzero. Otherwise, the return value is zero.
	 */
	boolean FlashWindowEx(FLASHWINFO pfwi);

	/**
	 * This function loads the specified icon resource from the executable (.exe) file associated with an 
	 * application instance.
	 * @param hInstance
	 *  Handle to an instance of the module whose executable file contains the icon to be loaded. 
	 *  This parameter must be NULL when a standard icon is being loaded.
	 * @param iconName
	 *  Long pointer to a null-terminated string that contains the name of the icon resource to be loaded. 
	 *  Alternatively, this parameter can contain the resource identifier in the low-order word and zero 
	 *  in the high-order word. Use the MAKEINTRESOURCE macro to create this value.
	 * @return
	 *  A handle to the newly loaded icon indicates success. NULL indicates failure. To get extended 
	 *  error information, call GetLastError. 
	 */
	HICON LoadIcon(HINSTANCE hInstance, String iconName);

	/**
	 * This function loads an icon, cursor, or bitmap.
	 * @param hinst
	 *  Handle to an instance of the module that contains the image to be loaded.
	 * @param name
	 *  Pointer to a null-terminated string that contains the name of the image resource 
	 *  in the hinst module that identifies the image to load.
	 * @param type
	 *  Specifies the type of image to be loaded.
	 * @param xDesired
	 *  Specifies the width, in pixels, of the icon or cursor. If this parameter is zero, the function uses 
	 *  the SM_CXICON or SM_CXCURSOR system metric value to set the width. If uType is IMAGE_BITMAP, this 
	 *  parameter must be zero.
	 * @param yDesired
	 *  Specifies the height, in pixels, of the icon or cursor. If this parameter is zero, the function uses 
	 *  the SM_CYICON or SM_CYCURSOR system metric value to set the height. If uType is IMAGE_BITMAP, this 
	 *  parameter must be zero.
	 * @param load
	 *  Set to zero.
	 * @return
	 *  The handle of the newly loaded image indicates success. NULL indicates failure. To get extended error information, call GetLastError.
	 */
	HANDLE LoadImage(HINSTANCE hinst, String name, int type, int xDesired,
			int yDesired, int load);

	/**
	 * This function destroys an icon and frees any memory the icon occupied.
	 * @param hicon
	 *  Handle to the icon to be destroyed. The icon must not be in use. 
	 * @return
	 *  Nonzero indicates success. Zero indicates failure. To get extended error information, call GetLastError.
	 */
	boolean DestroyIcon(HICON hicon);

	/**
	 * This function retrieves information about the specified window. GetWindowLong also retrieves 
	 * the 32-bit (long) value at the specified offset into the extra window memory of a window. 
	 * @param hWnd
	 *  Handle to the window and, indirectly, the class to which the window belongs. 
	 * @param nIndex
	 *  Specifies the zero-based offset to the value to be retrieved.
	 * @return
	 *  The requested 32-bit value indicates success. Zero indicates failure. To get extended error 
	 *  information, call GetLastError.
	 */
	int GetWindowLong(HWND hWnd, int nIndex);

	/**
	 * This function changes an attribute of the specified window. SetWindowLong also sets a 32-bit (LONG) 
	 * value at the specified offset into the extra window memory of a window.
	 * @param hWnd
	 *  Handle to the window and, indirectly, the class to which the window belongs. 
	 * @param nIndex
	 *  Specifies the zero-based offset to the value to be set.
	 * @param dwNewLong
	 *  Specifies the replacement value.
	 * @return
	 *  The previous value of the specified 32-bit integer indicates success. Zero indicates failure. 
	 *  To get extended error information, call GetLastError.
	 */
	int SetWindowLong(HWND hWnd, int nIndex, int dwNewLong);

	/**
	 * This function changes an attribute of the specified window. SetWindowLong also sets a 
	 * 32-bit (LONG) value at the specified offset into the extra window memory of a window.
	 * Do not use this version on Windows-64.
	 * @param hWnd
	 *  Handle to the window and, indirectly, the class to which the window belongs.
	 * @param nIndex
	 *  Specifies the zero-based offset to the value to be set.
	 * @param dwNewLong
	 *  Specifies the replacement value. 
	 * @return
	 *  The previous value of the specified 32-bit integer indicates success. Zero indicates failure.
	 *  To get extended error information, call GetLastError. 
	 */
	Pointer SetWindowLong(HWND hWnd, int nIndex, Pointer dwNewLong);

	/**
	 * The GetWindowLongPtr function retrieves information about the specified window. 
	 * The function also retrieves the value at a specified offset into the extra window memory.
	 * @param hWnd
	 *  Handle to the window and, indirectly, the class to which the window belongs.
	 * @param nIndex
	 *  Specifies the zero-based offset to the value to be retrieved.
	 * @return
	 *  If the function succeeds, the return value is the requested value.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError. 
	 *  If SetWindowLong or SetWindowLongPtr has not been called previously, GetWindowLongPtr returns zero for
	 *  values in the extra window or class memory.
	 */
	LONG_PTR GetWindowLongPtr(HWND hWnd, int nIndex);

	/**
	 * The SetWindowLongPtr function changes an attribute of the specified window. The function also
	 * sets a value at the specified offset in the extra window memory.
	 * @param hWnd
	 *  Handle to the window and, indirectly, the class to which the window belongs.
	 * @param nIndex
	 *  Specifies the zero-based offset to the value to be set.
	 * @param dwNewLongPtr
	 *  Specifies the replacement value.
	 * @return
	 *  If the function succeeds, the return value is the previous value of the specified offset.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError. 
	 *  If the previous value is zero and the function succeeds, the return value is zero, but the function 
	 *  does not clear the last error information. To determine success or failure, clear the last error 
	 *  information by calling SetLastError(0), then call SetWindowLongPtr. Function failure will be indicated
	 *  by a return value of zero and a GetLastError result that is nonzero.
	 */
	LONG_PTR SetWindowLongPtr(HWND hWnd, int nIndex, LONG_PTR dwNewLongPtr);

	/**
	 * The SetWindowLongPtr function changes an attribute of the specified window. The function also
	 * sets a value at the specified offset in the extra window memory.
	 * @param hWnd
	 *  Handle to the window and, indirectly, the class to which the window belongs.
	 * @param nIndex
	 *  Specifies the zero-based offset to the value to be set.
	 * @param dwNewLongPtr
	 *  Specifies the replacement value.
	 * @return
	 *  If the function succeeds, the return value is the previous value of the specified offset.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError. 
	 *  If the previous value is zero and the function succeeds, the return value is zero, but the function 
	 *  does not clear the last error information. To determine success or failure, clear the last error 
	 *  information by calling SetLastError(0), then call SetWindowLongPtr. Function failure will be indicated
	 *  by a return value of zero and a GetLastError result that is nonzero.
	 */
	Pointer SetWindowLongPtr(HWND hWnd, int nIndex, Pointer dwNewLongPtr);

	/**
	 * The SetLayeredWindowAttributes function sets the opacity and transparency color key of a layered window.
	 * @param hwnd
	 *  Handle to the layered window.
	 * @param crKey
	 *  COLORREF structure that specifies the transparency color key to be used when composing the layered window.
	 * @param bAlpha
	 *  Alpha value used to describe the opacity of the layered window. 
	 * @param dwFlags
	 *  Specifies an action to take.
	 * @return
	 *  If the function succeeds, the return value is nonzero. 
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
	 */
	boolean SetLayeredWindowAttributes(HWND hwnd, int crKey, byte bAlpha,
			int dwFlags);

	/**
	 * The GetLayeredWindowAttributes function retrieves the opacity and transparency color 
	 * key of a layered window.
	 * @param hwnd
	 *  Handle to the layered window. A layered window is created by specifying WS_EX_LAYERED 
	 *  when creating the window with the CreateWindowEx function or by setting WS_EX_LAYERED
	 *  via SetWindowLong after the window has been created.
	 * @param pcrKey
	 *  Pointer to a COLORREF value that receives the transparency color key to be used when 
	 *  composing the layered window. All pixels painted by the window in this color will be 
	 *  transparent. This can be NULL if the argument is not needed.
	 * @param pbAlpha
	 *  Pointer to a BYTE that receives the Alpha value used to describe the opacity of the 
	 *  layered window. Similar to the SourceConstantAlpha member of the BLENDFUNCTION structure.
	 *  When the variable referred to by pbAlpha is 0, the window is completely transparent. 
	 *  When the variable referred to by pbAlpha is 255, the window is opaque. This can be NULL 
	 *  if the argument is not needed.
	 * @param pdwFlags
	 *  Pointer to a DWORD that receives a layering flag. This can be NULL if the argument is not needed.
	 * @return
	 *  If the function succeeds, the return value is nonzero.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError. 
	 */
	boolean GetLayeredWindowAttributes(HWND hwnd, IntByReference pcrKey,
			ByteByReference pbAlpha, IntByReference pdwFlags);

	/**
	 * The UpdateLayeredWindow function updates the position, size, shape, content, and 
	 * translucency of a layered window. 
	 * @param hwnd
	 *  Handle to a layered window. A layered window is created by specifying WS_EX_LAYERED 
	 *  when creating the window with the CreateWindowEx function. 
	 * @param hdcDst
	 *  Handle to a device context (DC) for the screen. This handle is obtained by specifying NULL 
	 *  when calling the function. It is used for palette color matching when the window contents 
	 *  are updated. If hdcDst isNULL, the default palette will be used. If hdcSrc is NULL, hdcDst must be NULL.
	 * @param pptDst
	 *  Pointer to a POINT structure that specifies the new screen position of the layered window. 
	 *  If the current position is not changing, pptDst can be NULL. 
	 * @param psize
	 *  Pointer to a SIZE structure that specifies the new size of the layered window. If the size of the window
	 *  is not changing, psize can be NULL. If hdcSrc is NULL, psize must be NULL. 
	 * @param hdcSrc
	 *  Handle to a DC for the surface that defines the layered window. This handle can be obtained by calling 
	 *  the CreateCompatibleDC function. If the shape and visual context of the window are not changing, hdcSrc 
	 *  can be NULL. 
	 * @param pptSrc
	 *  Pointer to a POINT structure that specifies the location of the layer in the device context. 
	 *  If hdcSrc is NULL, pptSrc should be NULL.
	 * @param crKey
	 *  Pointer to a COLORREF value that specifies the color key to be used when composing the layered window.
	 *  To generate a COLORREF, use the RGB macro.
	 * @param pblend
	 *  Pointer to a BLENDFUNCTION structure that specifies the transparency value to be used when composing 
	 *  the layered window. 
	 * @param dwFlags
	 *  ULW_* flags.
	 * @return
	 *  If the function succeeds, the return value is nonzero.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError. 
	 */
	boolean UpdateLayeredWindow(HWND hwnd, HDC hdcDst, POINT pptDst,
			SIZE psize, HDC hdcSrc, POINT pptSrc, int crKey,
			BLENDFUNCTION pblend, int dwFlags);

	/**
	 * This function sets the window region of a window. The window region determines the area within the 
	 * window where the system permits drawing. The system does not display any portion of a window that lies
	 * outside of the window region. 
	 * @param hWnd
	 *  Handle to the window whose window region is to be set.
	 * @param hRgn
	 *  Handle to a region.  The function sets the window region of the window to this region. 
	 *  If hRgn is NULL, the function sets the window region to NULL. 
	 * @param bRedraw
	 *  Specifies whether the system redraws the window after setting the window region.  
	 *  If bRedraw is TRUE, the system does so; otherwise, it does not. 
	 *  Typically, you set bRedraw to TRUE if the window is visible. 
	 * @return
	 *  Nonzero indicates success. 
	 *  Zero indicates failure. 
	 *  To get extended error information, call GetLastError.
	 */
	int SetWindowRgn(HWND hWnd, HRGN hRgn, boolean bRedraw);

	/**
	 * The GetKeyboardState function copies the status of the 256 virtual keys to the specified buffer. 
	 * @param lpKeyState
	 *  Pointer to the 256-byte array that receives the status data for each virtual key.
	 * @return
	 *  If the function succeeds, the return value is nonzero.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError. 
	 */
	boolean GetKeyboardState(byte[] lpKeyState);

	/**
	 * This function determines whether a key is up or down at the time the function is called, 
	 * and whether the key was pressed after a previous call to GetAsyncKeyState.
	 * @param vKey
	 *  Specifies one of 256 possible virtual-key codes.
	 * @return
	 *  If the function succeeds, the return value specifies whether the key was pressed since the last 
	 *  call to GetAsyncKeyState, and whether the key is currently up or down. If the most significant 
	 *  bit is set, the key is down.
	 */
	short GetAsyncKeyState(int vKey);

	/**
	 * The SetWindowsHookEx function installs an application-defined hook procedure into a hook chain. 
	 * You would install a hook procedure to monitor the system for certain types of events. These 
	 * events are associated either with a specific thread or with all threads in the same desktop 
	 * as the calling thread.
	 * @param idHook
	 *  Specifies the type of hook procedure to be installed.
	 * @param lpfn
	 *  Pointer to the hook procedure.
	 * @param hMod
	 *  Handle to the DLL containing the hook procedure pointed to by the lpfn parameter.
	 * @param dwThreadId
	 *   Specifies the identifier of the thread with which the hook procedure is to be associated.
	 * @return
	 *  If the function succeeds, the return value is the handle to the hook procedure. 
	 *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
	 */
	HHOOK SetWindowsHookEx(int idHook, HOOKPROC lpfn, HINSTANCE hMod,
			int dwThreadId);

	/**
	 * The CallNextHookEx function passes the hook information to the next hook procedure
	 * in the current hook chain. A hook procedure can call this function either before or
	 * after processing the hook information.
	 * @param hhk
	 *  Ignored.
	 * @param nCode
	 *  Specifies the hook code passed to the current hook procedure. The next hook procedure 
	 *  uses this code to determine how to process the hook information.
	 * @param wParam
	 *  Specifies the wParam value passed to the current hook procedure. The meaning of this
	 *  parameter depends on the type of hook associated with the current hook chain.
	 * @param lParam
	 *  Specifies the lParam value passed to the current hook procedure. The meaning of this 
	 *  parameter depends on the type of hook associated with the current hook chain.
	 * @return
	 *  This value is returned by the next hook procedure in the chain. The current hook procedure
	 *  must also return this value. The meaning of the return value depends on the hook type. 
	 */
	LRESULT CallNextHookEx(HHOOK hhk, int nCode, WPARAM wParam, LPARAM lParam);

	/**
	 * The CallNextHookEx function passes the hook information to the next hook procedure
	 * in the current hook chain. A hook procedure can call this function either before or
	 * after processing the hook information.
	 * @param hhk
	 *  Ignored.
	 * @param nCode
	 *  Specifies the hook code passed to the current hook procedure. The next hook procedure 
	 *  uses this code to determine how to process the hook information.
	 * @param wParam
	 *  Specifies the wParam value passed to the current hook procedure. The meaning of this
	 *  parameter depends on the type of hook associated with the current hook chain.
	 * @param lParam
	 *  Specifies the lParam value passed to the current hook procedure. The meaning of this 
	 *  parameter depends on the type of hook associated with the current hook chain.
	 * @return
	 *  This value is returned by the next hook procedure in the chain. The current hook procedure
	 *  must also return this value. The meaning of the return value depends on the hook type. 
	 */
	LRESULT CallNextHookEx(HHOOK hhk, int nCode, WPARAM wParam, Pointer lParam);

	/**
	 * The UnhookWindowsHookEx function removes a hook procedure installed in 
	 * a hook chain by the SetWindowsHookEx function. 
	 * @param hhk
	 *  Handle to the hook to be removed. This parameter is a hook handle obtained 
	 *  by a previous call to SetWindowsHookEx.
	 * @return
	 *  If the function succeeds, the return value is nonzero.
	 *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
	 */
	boolean UnhookWindowsHookEx(HHOOK hhk);

	/**
	 * This function retrieves a message from the calling thread's message queue 
	 * and places it in the specified structure. 
	 * @param lpMsg
	 *  Pointer to an MSG structure that receives message information from the thread's message queue.
	 * @param hWnd
	 *  Handle to the window whose messages are to be retrieved. One value has a special meaning.
	 * @param wMsgFilterMin
	 *  Specifies the integer value of the lowest message value to be retrieved.
	 * @param wMsgFilterMax
	 *  Specifies the integer value of the highest message value to be retrieved.
	 * @return
	 *  Nonzero indicates that the function retrieves a message other than WM_QUIT. Zero indicates 
	 *  that the function retrieves the WM_QUIT message, or that lpMsg is an invalid pointer. To 
	 *  get extended error information, call GetLastError.
	 */
	int GetMessage(MSG lpMsg, HWND hWnd, int wMsgFilterMin, int wMsgFilterMax);

	/**
	 * This function checks a thread message queue for a message and places the 
	 * message (if any) in the specified structure.
	 * @param lpMsg
	 *  Pointer to an MSG structure that receives message information.
	 * @param hWnd
	 *  Handle to the window whose messages are to be examined. 
	 * @param wMsgFilterMin
	 *  Specifies the value of the first message in the range of messages to be examined.
	 * @param wMsgFilterMax
	 *  Specifies the value of the last message in the range of messages to be examined. 
	 * @param wRemoveMsg
	 *  Specifies how messages are handled. This parameter can be one of the following values. 
	 * @return
	 *  Nonzero indicates success. Zero indicates failure.
	 */
	boolean PeekMessage(MSG lpMsg, HWND hWnd, int wMsgFilterMin,
			int wMsgFilterMax, int wRemoveMsg);

	/**
	 * This function translates virtual-key messages into character messages. The character messages 
	 * are posted to the calling thread's message queue, to be read the next time the thread calls the 
	 * GetMessage or PeekMessage function. 
	 * @param lpMsg
	 *  Pointer to an MSG structure that contains message information retrieved from the calling thread's 
	 *  message queue by using the GetMessage or PeekMessage function. 
	 * @return
	 *  Nonzero indicates that the message is translated, that is, a character message is posted to the 
	 *  thread's message queue. If the message is WM_KEYDOWN or WM_SYSKEYDOWN, the return value is nonzero, 
	 *  regardless of the translation. Zero indicates that the message is not translated, that is, a 
	 *  character message is not posted to the thread's message queue.
	 */
	boolean TranslateMessage(MSG lpMsg);

	/**
	 * This function dispatches a message to a window procedure. It is typically used
	 * to dispatch a message retrieved by the GetMessage function.
	 * @param lpMsg
	 *  Pointer to an MSG structure that contains the message. 
	 * @return
	 *  The return value specifies the value returned by the window procedure. Although its meaning 
	 *  depends on the message being dispatched, the return value generally is ignored. 
	 */
	LRESULT DispatchMessage(MSG lpMsg);

	/**
	 * This function places a message in the message queue associated with the thread that
	 * created the specified window and then returns without waiting for the thread to process
	 * the message. Messages in a message queue are retrieved by calls to the GetMessage
	 * or PeekMessage function.
	 * @param hWnd
	 *  Handle to the window whose window procedure is to receive the message.
	 * @param msg
	 *  Specifies the message to be posted. 
	 * @param wParam
	 *  Specifies additional message-specific information.
	 * @param lParam
	 *  Specifies additional message-specific information.
	 */
	void PostMessage(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam);

	/**
	 * This function indicates to Windows that a thread has made a request to terminate (quit). 
	 * It is typically used in response to a WM_DESTROY message.
	 * @param nExitCode
	 *  Specifies an application exit code. This value is used as the wParam parameter of 
	 *  the WM_QUIT message.
	 */
	void PostQuitMessage(int nExitCode);

	/**
	 * The GetSystemMetrics function retrieves various system metrics (widths
	 * and heights of display elements) and system configuration settings. All
	 * dimensions retrieved by GetSystemMetrics are in pixels.
	 * @param nIndex
	 *  System metric or configuration setting to retrieve. This
	 *  parameter can be one of the following values. Note that all
	 *  SM_CX* values are widths and all SM_CY* values are heights.
	 *  Also note that all settings designed to return Boolean data
	 *  represent TRUE as any nonzero value, and FALSE as a zero
	 *  value.
	 * @return 
	 *  If the function succeeds, the return value is the requested
	 *  system metric or configuration setting. If the function fails,
	 *  the return value is zero. GetLastError does not provide extended
	 *  error information.
	 */
	public int GetSystemMetrics(int nIndex);
}
