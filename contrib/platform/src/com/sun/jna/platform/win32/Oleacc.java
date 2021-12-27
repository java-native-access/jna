/* Copyright (c) 2021 Mo Beigi, All Rights Reserved
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
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.LPWSTR;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * oleacc.dll Interface.
 *
 * Provides Windows Accessibility Features
 * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/">oleacc.h header</a>
 *
 * @author Mo Beigi, me@mobeigi.org
 */
public interface Oleacc extends StdCallLibrary
{
    /** The instance. */
    Oleacc INSTANCE = Native.load("Oleacc", Oleacc.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Object Roles
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/object-roles">Object Roles</a>
     */
    int ROLE_SYSTEM_TITLEBAR = 0x1;
    int ROLE_SYSTEM_MENUBAR = 0x2;
    int ROLE_SYSTEM_SCROLLBAR = 0x3;
    int ROLE_SYSTEM_GRIP = 0x4;
    int ROLE_SYSTEM_SOUND = 0x5;
    int ROLE_SYSTEM_CURSOR = 0x6;
    int ROLE_SYSTEM_CARET = 0x7;
    int ROLE_SYSTEM_ALERT = 0x8;
    int ROLE_SYSTEM_WINDOW = 0x9;
    int ROLE_SYSTEM_CLIENT = 0xa;
    int ROLE_SYSTEM_MENUPOPUP = 0xb;
    int ROLE_SYSTEM_MENUITEM = 0xc;
    int ROLE_SYSTEM_TOOLTIP = 0xd;
    int ROLE_SYSTEM_APPLICATION = 0xe;
    int ROLE_SYSTEM_DOCUMENT = 0xf;
    int ROLE_SYSTEM_PANE = 0x10;
    int ROLE_SYSTEM_CHART = 0x11;
    int ROLE_SYSTEM_DIALOG = 0x12;
    int ROLE_SYSTEM_BORDER = 0x13;
    int ROLE_SYSTEM_GROUPING = 0x14;
    int ROLE_SYSTEM_SEPARATOR = 0x15;
    int ROLE_SYSTEM_TOOLBAR = 0x16;
    int ROLE_SYSTEM_STATUSBAR = 0x17;
    int ROLE_SYSTEM_TABLE = 0x18;
    int ROLE_SYSTEM_COLUMNHEADER = 0x19;
    int ROLE_SYSTEM_ROWHEADER = 0x1a;
    int ROLE_SYSTEM_COLUMN = 0x1b;
    int ROLE_SYSTEM_ROW = 0x1c;
    int ROLE_SYSTEM_CELL = 0x1d;
    int ROLE_SYSTEM_LINK = 0x1e;
    int ROLE_SYSTEM_HELPBALLOON = 0x1f;
    int ROLE_SYSTEM_CHARACTER = 0x20;
    int ROLE_SYSTEM_LIST = 0x21;
    int ROLE_SYSTEM_LISTITEM = 0x22;
    int ROLE_SYSTEM_OUTLINE = 0x23;
    int ROLE_SYSTEM_OUTLINEITEM = 0x24;
    int ROLE_SYSTEM_PAGETAB = 0x25;
    int ROLE_SYSTEM_PROPERTYPAGE = 0x26;
    int ROLE_SYSTEM_INDICATOR = 0x27;
    int ROLE_SYSTEM_GRAPHIC = 0x28;
    int ROLE_SYSTEM_STATICTEXT = 0x29;
    int ROLE_SYSTEM_TEXT = 0x2a;
    int ROLE_SYSTEM_PUSHBUTTON = 0x2b;
    int ROLE_SYSTEM_CHECKBUTTON = 0x2c;
    int ROLE_SYSTEM_RADIOBUTTON = 0x2d;
    int ROLE_SYSTEM_COMBOBOX = 0x2e;
    int ROLE_SYSTEM_DROPLIST = 0x2f;
    int ROLE_SYSTEM_PROGRESSBAR = 0x30;
    int ROLE_SYSTEM_DIAL = 0x31;
    int ROLE_SYSTEM_HOTKEYFIELD = 0x32;
    int ROLE_SYSTEM_SLIDER = 0x33;
    int ROLE_SYSTEM_SPINBUTTON = 0x34;
    int ROLE_SYSTEM_DIAGRAM = 0x35;
    int ROLE_SYSTEM_ANIMATION = 0x36;
    int ROLE_SYSTEM_EQUATION = 0x37;
    int ROLE_SYSTEM_BUTTONDROPDOWN = 0x38;
    int ROLE_SYSTEM_BUTTONMENU = 0x39;
    int ROLE_SYSTEM_BUTTONDROPDOWNGRID = 0x3a;
    int ROLE_SYSTEM_WHITESPACE = 0x3b;
    int ROLE_SYSTEM_PAGETABLIST = 0x3c;
    int ROLE_SYSTEM_CLOCK = 0x3d;
    int ROLE_SYSTEM_SPLITBUTTON = 0x3e;
    int ROLE_SYSTEM_IPADDRESS = 0x3f;
    int ROLE_SYSTEM_OUTLINEBUTTON = 0x40;

    /**
     * Retrieves the child ID or IDispatch of each child within an accessible container object.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-accessiblechildren">AccessibleChildren function (oleacc.h)</a>
     *
     * @param paccContainer [in] Pointer to the container object's IAccessible interface.
     * @param iChildStart [in] Specifies the zero-based index of the first child that is retrieved.
     *                    This parameter is an index, not a child ID, and it is usually set to zero (0).
     * @param cChildren [in] Specifies the number of children to retrieve. To retrieve the current number of children,
     *                  an application calls IAccessible::get_accChildCount.
     * @param rgvarChildren [out] Pointer to an array of VARIANT structures that receives information about the container's children.
     *                      If the vt member of an array element is VT_I4, then the lVal member for that element is the child ID.
     *                      If the vt member of an array element is VT_DISPATCH, then the pdispVal member for that element is
     *                      the address of the child object's IDispatch interface.
     * @param pcObtained [out] Address of a variable that receives the number of elements in the rgvarChildren array that is populated
     *                   by the AccessibleChildren function. This value is the same as that of the cChildren parameter;
     *                   however, if you request more children than exist, this value will be less than that of cChildren.
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT AccessibleChildren(Pointer paccContainer, int iChildStart, int cChildren, VARIANT[] rgvarChildren, IntByReference pcObtained);

    /**
     * Retrieves the address of the specified interface for the object associated with the specified window.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-accessibleobjectfromwindow">AccessibleObjectFromWindow function (oleacc.h)</a>
     *
     * @param hwnd [in] Specifies the handle of a window for which an object is to be retrieved.
     *             To retrieve an interface pointer to the cursor or caret object, specify NULL and use the appropriate object ID in dwObjectID.
     * @param dwId [in] Specifies the object ID. This value is one of the standard object identifier constants or a custom object ID such as OBJID_NATIVEOM,
     *             which is the object ID for the Office native object model.
     * @param riid [in] Specifies the reference identifier of the requested interface. This value is either IID_IAccessible or
     *             IID_IDispatch, but it can also be IID_IUnknown, or the IID of any interface that the object is expected to support.
     * @param ppvObject [out] Address of a pointer variable that receives the address of the specified interface.
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT AccessibleObjectFromWindow(HWND hwnd, int dwId, REFIID riid, PointerByReference ppvObject);

    /**
     * Retrieves the window handle that corresponds to a particular instance of an IAccessible interface.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-windowfromaccessibleobject">WindowFromAccessibleObject function (oleacc.h)</a>
     *
     * @param IAccessible [in] Pointer to the IAccessible interface whose corresponding window handle will be retrieved. This parameter must not be NULL.
     * @param phwnd [out] Address of a variable that receives a handle to the window containing the object specified in pacc.
     *              If this value is NULL after the call, the object is not contained within a window;
     *              for example, the mouse pointer is not contained within a window.
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT WindowFromAccessibleObject(Pointer IAccessible, PointerByReference phwnd);

    /**
     * Retrieves the localized string that describes the object's role for the specified role value.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-getroletexta">GetRoleTextA function (oleacc.h)</a>
     *
     * @param lRole [in] One of the object role constants.
     * @param lpszRole [out] Address of a buffer that receives the role text string. If this parameter is NULL, the function returns the role string's length, not including the null character.
     * @param cchRoleMax [in] The size of the buffer that is pointed to by the lpszRole parameter. For ANSI strings, this value is measured in bytes; for Unicode strings, it is measured in characters.
     * @return If successful, and if lpszRole is non-NULL, the return value is the number of bytes (ANSI strings)
     *         or characters (Unicode strings) copied into the buffer, not including the terminating null character.
     *         If lpszRole is NULL, the return value represents the string's length, not including the null character.
     *         If the string resource does not exist, or if the lpszRole parameter is not a valid pointer, the return value is zero (0).
     *         To get extended error information, call GetLastError.
     */
    int GetRoleTextA(int lRole, LPSTR lpszRole, int cchRoleMax);

    /**
     * Retrieves the localized string that describes the object's role for the specified role value.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-getroletextw">GetRoleTextW function (oleacc.h)</a>
     *
     * @param lRole [in] One of the object role constants.
     * @param lpszRole [out] Address of a buffer that receives the role text string. If this parameter is NULL, the function returns the role string's length, not including the null character.
     * @param cchRoleMax [in] The size of the buffer that is pointed to by the lpszRole parameter. For ANSI strings, this value is measured in bytes; for Unicode strings, it is measured in characters.
     * @return If successful, and if lpszRole is non-NULL, the return value is the number of bytes (ANSI strings)
     *         or characters (Unicode strings) copied into the buffer, not including the terminating null character.
     *         If lpszRole is NULL, the return value represents the string's length, not including the null character.
     *         If the string resource does not exist, or if the lpszRole parameter is not a valid pointer, the return value is zero (0).
     *         To get extended error information, call GetLastError.
     */
    int GetRoleTextW(int lRole, LPWSTR lpszRole, int cchRoleMax);
}
