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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Ported from WinUser.h Microsoft Windows SDK 6.0A.
 *
 * @author dblock[at]dblock.org
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public interface WinUser extends WinDef {
    HWND HWND_BROADCAST = new HWND(Pointer.createConstant(0xFFFF));
    HWND HWND_MESSAGE = new HWND(Pointer.createConstant(-3));

    /* RegisterDeviceNotification stuff */
    public static class HDEVNOTIFY extends PVOID {
        public HDEVNOTIFY() {

        }

        public HDEVNOTIFY(Pointer p) {
            super(p);
        }
    }

    int FLASHW_STOP = 0;
    int FLASHW_CAPTION = 1;
    int FLASHW_TRAY = 2;
    int FLASHW_ALL = (FLASHW_CAPTION | FLASHW_TRAY);
    int FLASHW_TIMER = 4;
    int FLASHW_TIMERNOFG = 12;

    int IMAGE_BITMAP = 0;
    int IMAGE_ICON = 1;
    int IMAGE_CURSOR = 2;
    int IMAGE_ENHMETAFILE = 3;

    int LR_DEFAULTCOLOR = 0x0000;
    int LR_MONOCHROME = 0x0001;
    int LR_COLOR = 0x0002;
    int LR_COPYRETURNORG = 0x0004;
    int LR_COPYDELETEORG = 0x0008;
    int LR_LOADFROMFILE = 0x0010;
    int LR_LOADTRANSPARENT = 0x0020;
    int LR_DEFAULTSIZE = 0x0040;
    int LR_VGACOLOR = 0x0080;
    int LR_LOADMAP3DCOLORS = 0x1000;
    int LR_CREATEDIBSECTION = 0x2000;
    int LR_COPYFROMRESOURCE = 0x4000;
    int LR_SHARED = 0x8000;

    public class GUITHREADINFO extends Structure {
        public int cbSize = size();
        public int flags;
        public HWND hwndActive;
        public HWND hwndFocus;
        public HWND hwndCapture;
        public HWND hwndMenuOwner;
        public HWND hwndMoveSize;
        public HWND hwndCaret;
        public RECT rcCaret;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cbSize", "flags",
                                                "hwndActive", "hwndFocus", "hwndCapture", "hwndMenuOwner",
                                                "hwndMoveSize", "hwndCaret", "rcCaret" });
        }
    }

    public class WINDOWINFO extends Structure {
        public int cbSize = size();
        public RECT rcWindow;
        public RECT rcClient;
        public int dwStyle;
        public int dwExStyle;
        public int dwWindowStatus;
        public int cxWindowBorders;
        public int cyWindowBorders;
        public short atomWindowType;
        public short wCreatorVersion;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cbSize", "rcWindow",
                                                "rcClient", "dwStyle", "dwExStyle", "dwWindowStatus",
                                                "cxWindowBorders", "cyWindowBorders", "atomWindowType",
                                                "wCreatorVersion" });
        }
    }

    /**
     * Contains information about the placement of a window on the screen.
     */
    public class WINDOWPLACEMENT extends Structure {
        /**
         * The coordinates of the minimized window may be specified.
         */
        public static final int WPF_SETMINPOSITION = 0x1;

        /**The restored window will be maximized, regardless of whether it was maximized before it
         * was minimized. This setting is only valid the next time the window is restored. It does not
         * change the default restoration behavior.
         *
         * This flag is only valid when the SW_SHOWMINIMIZED value is specified for the showCmd member.
         */
        public static final int WPF_RESTORETOMAXIMIZED = 0x2;

        /**
         * If the calling thread and the thread that owns the window are attached to different input
         * queues, the system posts the request to the thread that owns the window. This prevents
         * the calling thread from blocking its execution while other threads process the request.
         */
        public static final int WPF_ASYNCWINDOWPLACEMENT = 0x4;



        /**
         * The length of the structure, in bytes.
         */
        public int length = size();
        /**
         * The flags that control the position of the minimized window and the method by which the
         * window is restored. This member can be one or more of WPF_SETMINPOSITION,
         * WPF_RESTORETOMAXIMIZED, or WPF_ASYNCWINDOWPLACEMENT.
         */
        public int flags;
        /**
         * The current show state of the window. This member can be one of SW_HIDE, SW_MAXIMIZE,
         * SW_MINIMIZE, SW_RESTORE, SW_SHOW, SW_SHOWMAXIMIZED, SW_SHOWMINIMIZED, SW_SHOWMINNOACTIVE,
         * SW_SHOWNA, SW_SHOWNOACTIVATE, SW_SHOWNORMAL.
         *
         * Note that here SW_MAXIMIZE and SW_SHOWMAXIMIZED are the same value.
         */
        public int showCmd;
        /**
         * Virtual position of the window's upper-left corner when minimized. Usually largely negative.
         * May be in workspace coordinates.
         */
        public POINT ptMinPosition;
        /**
         * Coordinates of the window's upper-right corner when maximized. Usually small and negative.
         * May be in workspace coordinates.
         */
        public POINT ptMaxPosition;
        /**
         * The window's coordinates when the window is in the restored position. May be in workspace
         * coordinates.
         */
        public RECT rcNormalPosition;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"length","flags","showCmd","ptMinPosition","ptMaxPosition",
                    "rcNormalPosition"});
        }
    }

    /* Get/SetWindowLong properties */
    int GWL_EXSTYLE = -20;
    int GWL_STYLE = -16;
    int GWL_WNDPROC = -4;
    int GWL_HINSTANCE = -6;
    int GWL_ID = -12;
    int GWL_USERDATA = -21;
    int GWL_HWNDPARENT = -8;

    int DWL_DLGPROC = Native.POINTER_SIZE;
    int DWL_MSGRESULT = 0;
    int DWL_USER = 2*Native.POINTER_SIZE;

    /* Window Styles */

    /** The window has a thin-line border. */
    int WS_BORDER    = 0x800000;

    /** The window has a title bar (includes the WS_BORDER style). */
    int WS_CAPTION    = 0xc00000;

    /** The window is a child window. A window with this style cannot have a
     * menu bar. This style cannot be used with the WS_POPUP style. */
    int WS_CHILD    = 0x40000000;

    /** Same as the WS_CHILD style. */
    int WS_CHILDWINDOW    = 0x40000000;

    /** Excludes the area occupied by child windows when drawing occurs within
     * the parent window. This style is used when creating the parent window. */
    int WS_CLIPCHILDREN = 0x2000000;

    /** Clips child windows relative to each other; that is, when a particular
     * child window receives a WM_PAINT message, the WS_CLIPSIBLINGS style clips
     * all other overlapping child windows out of the region of the child window
     * to be updated. If WS_CLIPSIBLINGS is not specified and child windows
     * overlap, it is possible, when drawing within the client area of a child
     * window, to draw within the client area of a neighboring child window. */
    int WS_CLIPSIBLINGS = 0x4000000;

    /** The window is initially disabled. A disabled window cannot receive input
     * from the user. To change this after a window has been created, use the
     * EnableWindow function. */
    int WS_DISABLED    = 0x8000000;

    /** The window has a border of a style typically used with dialog boxes. A
     * window with this style cannot have a title bar. */
    int WS_DLGFRAME    = 0x400000;

    /** The window is the first control of a group of controls. The group
     * consists of this first control and all controls defined after it, up to
     * the next control with the WS_GROUP style. The first control in each group
     * usually has the WS_TABSTOP style so that the user can move from group to
     * group. The user can subsequently change the keyboard focus from one control
     * in the group to the next control in the group by using the direction keys
     * .
     * You can turn this style on and off to change dialog box navigation. To
     * change this style after a window has been created, use the SetWindowLong
     * function.
     */
    int WS_GROUP    = 0x20000;

    /** The window has a horizontal scroll bar. */
    int WS_HSCROLL    = 0x100000;

    /** The window is initially minimized. Same as the WS_MINIMIZE style. */
    int WS_ICONIC    = 0x20000000;

    /** The window is initially maximized. */
    int WS_MAXIMIZE    = 0x1000000;

    /** The window has a maximize button. Cannot be combined with the
     * WS_EX_CONTEXTHELP style. The WS_SYSMENU style must also be specified.  */
    int WS_MAXIMIZEBOX    = 0x10000;

    /** The window is initially minimized. Same as the WS_ICONIC style. */
    int WS_MINIMIZE    = 0x20000000;

    /** The window has a minimize button. Cannot be combined with the
     * WS_EX_CONTEXTHELP style. The WS_SYSMENU style must also be specified. */
    int WS_MINIMIZEBOX    = 0x20000;

    /** The window style overlapped. The window is an overlapped window. An
     * overlapped window has a title bar and a border. Same as the WS_TILED style. */
    int WS_OVERLAPPED = 0x00000000;

    /** The windows is a pop-up window. This style cannot be used with the WS_CHILD style. */
    int WS_POPUP    = 0x80000000;

    /** The window has a window menu on its title bar. The WS_CAPTION style must also be specified. */
    int WS_SYSMENU    = 0x80000;

    /** The window has a sizing border. Same as the WS_SIZEBOX style. */
    int WS_THICKFRAME    = 0x40000;

    /** The window is a pop-up window. The WS_CAPTION and WS_POPUPWINDOW styles
     * must be combined to make the window menu visible. */
    int WS_POPUPWINDOW    = (WS_POPUP | WS_BORDER | WS_SYSMENU);

    /** The window is an overlapped window. Same as the WS_TILEDWINDOW style.  */
    int WS_OVERLAPPEDWINDOW    = (WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU |
            WS_THICKFRAME | WS_MINIMIZEBOX | WS_MAXIMIZEBOX);

    /** The window has a sizing border. Same as the WS_THICKFRAME style. */
    int WS_SIZEBOX    = 0x40000;

    /** The window is a control that can receive the keyboard focus when the
     * user presses the TAB key. Pressing the TAB key changes the keyboard focus
     * to the next control with the WS_TABSTOP style.
     *
     * You can turn this style on and off to change dialog box navigation.
     * To change this style after a window has been created, use the SetWindowLong
     * function. For user-created windows and modeless dialogs to work with tab
     * stops, alter the message loop to call the IsDialogMessage function.
     */
    int WS_TABSTOP    = 0x10000;

    /** The window is an overlapped window. An overlapped window has a
     * title bar and a border. Same as the WS_OVERLAPPED style. */
    int WS_TILED    = 0;

    /** The window is an overlapped window. Same as the WS_OVERLAPPEDWINDOW style. */
    int WS_TILEDWINDOW    = (WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU |
            WS_THICKFRAME | WS_MINIMIZEBOX | WS_MAXIMIZEBOX);

    /** The window is initially visible. This style can be turned on and off
     * by using the ShowWindow or SetWindowPos function. */
    int WS_VISIBLE    = 0x10000000;

    /** The window has a vertical scroll bar. */
    int WS_VSCROLL    = 0x200000;

    /* Extended Window Styles */
    int WS_EX_COMPOSITED = 0x20000000;
    int WS_EX_LAYERED = 0x80000;
    int WS_EX_TRANSPARENT = 32;

    /* Layered Window Attributes flags */
    int LWA_COLORKEY = 1;
    int LWA_ALPHA = 2;

    /* Update Layered Window flags */
    int ULW_COLORKEY = 1;
    int ULW_ALPHA = 2;
    int ULW_OPAQUE = 4;

    public class MSG extends Structure {
        public HWND hWnd;
        public int message;
        public WPARAM wParam;
        public LPARAM lParam;
        public int time;
        public POINT pt;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "hWnd", "message", "wParam",
                                                "lParam", "time", "pt" });
        }
    }

    
    /**
     * Contains data to be passed to another application by the WM_COPYDATA message.
     */
    public class COPYDATASTRUCT extends Structure {

		public COPYDATASTRUCT() {
			super();
		}
		
		public COPYDATASTRUCT(Pointer p) {
			super(p);
			//Receiving data and read it from native memory to fill the structure.
			read();
		}
		
		public ULONG_PTR dwData;
		public int cbData;
		public Pointer lpData;

		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwData", "cbData", "lpData" });
		}
	}
    
    public class FLASHWINFO extends Structure {
        public int cbSize = size();
        public HANDLE hWnd;
        public int dwFlags;
        public int uCount;
        public int dwTimeout;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cbSize", "hWnd", "dwFlags",
                                                "uCount", "dwTimeout" });
        }
    }

    public interface WNDENUMPROC extends StdCallCallback {
        /** Return whether to continue enumeration.
         * @param hWnd window handle
         * @param data callback data
         * @return FIXME
         */
        boolean callback(HWND hWnd, Pointer data);
    }

    public interface LowLevelKeyboardProc extends HOOKPROC {
        LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT lParam);
    }

    /**
     * An application-defined callback (or hook) function that the system calls
     * in response to events generated by an accessible object.<br>
     * The hook function processes the event notifications as required.<br>
     * Clients install the hook function and request specific types of event
     * notifications by calling SetWinEventHook.<br>
     * The WINEVENTPROC type defines a pointer to this callback function.
     * WinEventProc is a placeholder for the application-defined function name.
     */
    public static interface WinEventProc extends Callback {
        /**
         * @param hWinEventHook
         *            Type: HWINEVENTHOOK<br>
         *            Handle to an event hook function.<br>
         *            This value is returned by SetWinEventHook when the hook
         *            function is installed and is specific to each instance of
         *            the hook function.
         * @param event
         *            Type: DWORD<br>
         *            Specifies the event that occurred.<br>
         *            This value is one of the event constants.
         * @param hwnd
         *            Type: HWND<br>
         *            Handle to the window that generates the event, or NULL if
         *            no window is associated with the event.<br>
         *            For example, the mouse pointer is not associated with a
         *            window.
         * @param idObject
         *            Type: LONG<br>
         *            Identifies the object associated with the event.<br>
         *            This is one of the object identifiers or a custom object
         *            ID.
         * @param idChild
         *            Type: LONG<br>
         *            Identifies whether the event was triggered by an object or
         *            a child element of the object.<br>
         *            If this value is CHILDID_SELF, the event was triggered by
         *            the object; otherwise, this value is the child ID of the
         *            element that triggered the event.
         * @param dwEventThread
         *            Type: DWORD<br>
         *            Identifies the thread that generated the event, or the
         *            thread that owns the current window.
         * @param dwmsEventTime
         *            Type: DWORD<br>
         *            Specifies the time, in milliseconds, that the event was
         *            generated.
         */
        void callback(HANDLE hWinEventHook, DWORD event, HWND hwnd, LONG idObject, LONG idChild, DWORD dwEventThread,
                DWORD dwmsEventTime);
    }

    /** Specifies the width and height of a rectangle. */
    public class SIZE extends Structure {
        public int cx, cy;

        public SIZE() {
        }

        public SIZE(int w, int h) {
            this.cx = w;
            this.cy = h;
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cx", "cy" });
        }
    }

    int AC_SRC_OVER = 0x00;
    int AC_SRC_ALPHA = 0x01;
    int AC_SRC_NO_PREMULT_ALPHA = 0x01;
    int AC_SRC_NO_ALPHA = 0x02;

    public class BLENDFUNCTION extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("BlendOp", "BlendFlags", "SourceConstantAlpha", "AlphaFormat");

        public byte BlendOp = AC_SRC_OVER; // only valid value
        public byte BlendFlags = 0; // only valid value
        public byte SourceConstantAlpha;
        public byte AlphaFormat;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    int VK_SHIFT = 16;
    int VK_LSHIFT = 0xA0;
    int VK_RSHIFT = 0xA1;
    int VK_CONTROL = 17;
    int VK_LCONTROL = 0xA2;
    int VK_RCONTROL = 0xA3;
    int VK_MENU = 18;
    int VK_LMENU = 0xA4;
    int VK_RMENU = 0xA5;

    int MOD_ALT = 0x0001;
    int MOD_CONTROL = 0x0002;
    int MOD_NOREPEAT = 0x4000;
    int MOD_SHIFT = 0x0004;
    int MOD_WIN = 0x0008;

    int WH_KEYBOARD = 2;
    int WH_CALLWNDPROC = 4;
    int WH_MOUSE = 7;
    int WH_KEYBOARD_LL = 13;
    int WH_MOUSE_LL = 14;

    public class HHOOK extends HANDLE { }

    public interface HOOKPROC extends StdCallCallback { }

    /**
     * Defines the message parameters passed to a WH_CALLWNDPROC hook procedure, CallWndProc.
     */
    public class CWPSTRUCT extends Structure {

    	public CWPSTRUCT() {
    		super();
		}
    	
		public CWPSTRUCT(Pointer p) {
			super(p);
			//Receiving data and read it from native memory to fill the structure.
			read();
		}

		public LPARAM lParam;
		public WPARAM wParam;
		public int message;
		public HWND hwnd;

		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "lParam", "wParam", "message", "hwnd"});
		}
    }
    
    /**
     * The WM_PAINT message is sent when the system or another application makes
     * a request to paint a portion of an \ application's window.
     */
    int WM_PAINT = 0x000F;

    /**
     * Sent as a signal that a window or an application should terminate.
     */
    int WM_CLOSE = 0x0010;

    /**
     * Indicates a request to terminate an application, and is generated when
     * the application calls the PostQuitMessage function.
     */
    int WM_QUIT = 0x0012;

    /**
     * Sent to a window when the window is about to be hidden or shown.
     */
    int WM_SHOWWINDOW = 0x0018;

    /**
     * Sent to the parent window of an owner-drawn button, combo box, list box,
     * or menu when a visual aspect of the button, combo box, list box, or menu
     * has changed.
     */
    int WM_DRAWITEM = 0x002B;

    /**
     * Posted to the window with the keyboard focus when a nonsystem key is
     * pressed. A nonsystem key is a key that is pressed when the ALT key is not
     * pressed.
     */
    int WM_KEYDOWN = 0x0100;

    /**
     * Posted to the window with the keyboard focus when a WM_KEYDOWN message is
     * translated by the TranslateMessage function. The WM_CHAR message contains
     * the character code of the key that was pressed.
     */
    int WM_CHAR = 0x0102;

    /**
     * A window receives this message when the user chooses a command from the
     * Window menu (formerly known as the system or control menu) or when the
     * user chooses the maximize button, minimize button, restore button, or
     * close button.
     */
    int WM_SYSCOMMAND = 0x0112;

    /**
     * An application sends the WM_MDIMAXIMIZE message to a multiple-document
     * interface (MDI) client window to maximize an MDI child window.
     */
    int WM_MDIMAXIMIZE = 0x0225;

    /**
     * Posted when the user presses a hot key registered by the RegisterHotKey
     * function. The message is placed at the top of the message queue
     * associated with the thread that registered the hot key.
     */
    int WM_HOTKEY = 0x0312;
    
    /**
     * Used to define private messages for use by private window classes,
     * usually of the form WM_USER+x, where x is an integer value.
     */
    int WM_USER = 0x0400;
    
    /**
     * An application sends the WM_COPYDATA message to pass data to another application.
     */
    int WM_COPYDATA = 0x004A;

    int WM_KEYUP = 257;
    int WM_SYSKEYDOWN = 260;
    int WM_SYSKEYUP = 261;

    int WM_SESSION_CHANGE = 0x2b1;
    int WM_CREATE = 0x0001;
    int WM_SIZE = 0x0005;
    int WM_DESTROY = 0x0002;

    public static final int WM_DEVICECHANGE = 0x0219;

    /**
     * Sent to a window to retrieve a handle to the large or small icon
     * associated with a window. The system displays the large icon in the
     * ALT+TAB dialog, and the small icon in the window caption.
     */
    int WM_GETICON = 0x007F;

    /**
     * Retrieve the large icon for the window.
     */
    int ICON_BIG = 1;

    /**
     * Retrieve the small icon for the window.
     */
    int ICON_SMALL = 0;

    /**
     * Retrieves the small icon provided by the application. If the application
     * does not provide one, the system uses the system-generated icon for that
     * window.
     */
    int ICON_SMALL2 = 2;

    public class KBDLLHOOKSTRUCT extends Structure {
        public int vkCode;
        public int scanCode;
        public int flags;
        public int time;
        public ULONG_PTR dwExtraInfo;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "vkCode", "scanCode", "flags",
                                                "time", "dwExtraInfo" });
        }
    }

    /* System Metrics */
    int SM_CXSCREEN = 0;
    int SM_CYSCREEN = 1;
    int SM_CXVSCROLL = 2;
    int SM_CYHSCROLL = 3;
    int SM_CYCAPTION = 4;
    int SM_CXBORDER = 5;
    int SM_CYBORDER = 6;
    int SM_CXDLGFRAME = 7;
    int SM_CYDLGFRAME = 8;
    int SM_CYVTHUMB = 9;
    int SM_CXHTHUMB = 10;
    int SM_CXICON = 11;
    int SM_CYICON = 12;
    int SM_CXCURSOR = 13;
    int SM_CYCURSOR = 14;
    int SM_CYMENU = 15;
    int SM_CXFULLSCREEN = 16;
    int SM_CYFULLSCREEN = 17;
    int SM_CYKANJIWINDOW = 18;
    int SM_MOUSEPRESENT = 19;
    int SM_CYVSCROLL = 20;
    int SM_CXHSCROLL = 21;
    int SM_DEBUG = 22;
    int SM_SWAPBUTTON = 23;
    int SM_RESERVED1 = 24;
    int SM_RESERVED2 = 25;
    int SM_RESERVED3 = 26;
    int SM_RESERVED4 = 27;
    int SM_CXMIN = 28;
    int SM_CYMIN = 29;
    int SM_CXSIZE = 30;
    int SM_CYSIZE = 31;
    int SM_CXFRAME = 32;
    int SM_CYFRAME = 33;
    int SM_CXMINTRACK = 34;
    int SM_CYMINTRACK = 35;
    int SM_CXDOUBLECLK = 36;
    int SM_CYDOUBLECLK = 37;
    int SM_CXICONSPACING = 38;
    int SM_CYICONSPACING = 39;
    int SM_MENUDROPALIGNMENT = 40;
    int SM_PENWINDOWS = 41;
    int SM_DBCSENABLED = 42;
    int SM_CMOUSEBUTTONS = 43;

    int SM_CXFIXEDFRAME = SM_CXDLGFRAME; /* ;win40 name change */
    int SM_CYFIXEDFRAME = SM_CYDLGFRAME; /* ;win40 name change */
    int SM_CXSIZEFRAME = SM_CXFRAME; /* ;win40 name change */
    int SM_CYSIZEFRAME = SM_CYFRAME; /* ;win40 name change */

    int SM_SECURE = 44;
    int SM_CXEDGE = 45;
    int SM_CYEDGE = 46;
    int SM_CXMINSPACING = 47;
    int SM_CYMINSPACING = 48;
    int SM_CXSMICON = 49;
    int SM_CYSMICON = 50;
    int SM_CYSMCAPTION = 51;
    int SM_CXSMSIZE = 52;
    int SM_CYSMSIZE = 53;
    int SM_CXMENUSIZE = 54;
    int SM_CYMENUSIZE = 55;
    int SM_ARRANGE = 56;
    int SM_CXMINIMIZED = 57;
    int SM_CYMINIMIZED = 58;
    int SM_CXMAXTRACK = 59;
    int SM_CYMAXTRACK = 60;
    int SM_CXMAXIMIZED = 61;
    int SM_CYMAXIMIZED = 62;
    int SM_NETWORK = 63;
    int SM_CLEANBOOT = 67;
    int SM_CXDRAG = 68;
    int SM_CYDRAG = 69;
    int SM_SHOWSOUNDS = 70;
    int SM_CXMENUCHECK = 71;
    int SM_CYMENUCHECK = 72;
    int SM_SLOWMACHINE = 73;
    int SM_MIDEASTENABLED = 74;
    int SM_MOUSEWHEELPRESENT = 75;
    int SM_XVIRTUALSCREEN = 76;
    int SM_YVIRTUALSCREEN = 77;
    int SM_CXVIRTUALSCREEN = 78;
    int SM_CYVIRTUALSCREEN = 79;
    int SM_CMONITORS = 80;
    int SM_SAMEDISPLAYFORMAT = 81;
    int SM_IMMENABLED = 82;
    int SM_CXFOCUSBORDER = 83;
    int SM_CYFOCUSBORDER = 84;
    int SM_TABLETPC = 86;
    int SM_MEDIACENTER = 87;
    int SM_STARTER = 88;
    int SM_SERVERR2 = 89;
    int SM_MOUSEHORIZONTALWHEELPRESENT = 91;
    int SM_CXPADDEDBORDER = 92;
    int SM_REMOTESESSION = 0x1000;
    int SM_SHUTTINGDOWN = 0x2000;
    int SM_REMOTECONTROL = 0x2001;
    int SM_CARETBLINKINGENABLED = 0x2002;

    int SW_HIDE = 0;
    int SW_SHOWNORMAL = 1;
    int SW_NORMAL = 1;
    int SW_SHOWMINIMIZED = 2;
    int SW_SHOWMAXIMIZED = 3;
    int SW_MAXIMIZE = 3;
    int SW_SHOWNOACTIVATE = 4;
    int SW_SHOW = 5;
    int SW_MINIMIZE = 6;
    int SW_SHOWMINNOACTIVE = 7;
    int SW_SHOWNA = 8;
    int SW_RESTORE = 9;
    int SW_SHOWDEFAULT = 10;
    int SW_FORCEMINIMIZE = 11;
    int SW_MAX = 11;

    int RDW_INVALIDATE = 0x0001;
    int RDW_INTERNALPAINT = 0x0002;
    int RDW_ERASE = 0x0004;
    int RDW_VALIDATE = 0x0008;
    int RDW_NOINTERNALPAINT = 0x0010;
    int RDW_NOERASE = 0x0020;
    int RDW_NOCHILDREN = 0x0040;
    int RDW_ALLCHILDREN = 0x0080;
    int RDW_UPDATENOW = 0x0100;
    int RDW_ERASENOW = 0x0200;
    int RDW_FRAME = 0x0400;
    int RDW_NOFRAME = 0x0800;

    /**
     * The retrieved handle identifies the window of the same type that is
     * highest in the Z order.
     *
     * If the specified window is a topmost window, the handle identifies a
     * topmost window. If the specified window is a top-level window, the handle
     * identifies a top-level window. If the specified window is a child window,
     * the handle identifies a sibling window.
     */
    int GW_HWNDFIRST = 0;

    /**
     * The retrieved handle identifies the window of the same type that is
     * lowest in the Z order.
     *
     * If the specified window is a topmost window, the handle identifies a
     * topmost window. If the specified window is a top-level window, the handle
     * identifies a top-level window. If the specified window is a child window,
     * the handle identifies a sibling window.
     */
    int GW_HWNDLAST = 1;

    /**
     * The retrieved handle identifies the window below the specified window in
     * the Z order.
     *
     * If the specified window is a topmost window, the handle identifies a
     * topmost window. If the specified window is a top-level window, the handle
     * identifies a top-level window. If the specified window is a child window,
     * the handle identifies a sibling window.
     */
    int GW_HWNDNEXT = 2;

    /**
     * The retrieved handle identifies the window above the specified window in
     * the Z order.
     *
     * If the specified window is a topmost window, the handle identifies a
     * topmost window. If the specified window is a top-level window, the handle
     * identifies a top-level window. If the specified window is a child window,
     * the handle identifies a sibling window.
     */
    int GW_HWNDPREV = 3;

    /**
     * The retrieved handle identifies the specified window's owner window, if
     * any. For more information, see Owned Windows.
     */
    int GW_OWNER = 4;

    /**
     * The retrieved handle identifies the child window at the top of the Z
     * order, if the specified window is a parent window; otherwise, the
     * retrieved handle is NULL. The function examines only child windows of the
     * specified window. It does not examine descendant windows.
     */
    int GW_CHILD = 5;

    /**
     * The retrieved handle identifies the enabled popup window owned by the
     * specified window (the search uses the first such window found using
     * GW_HWNDNEXT); otherwise, if there are no enabled popup windows, the
     * retrieved handle is that of the specified window.
     */
    int GW_ENABLEDPOPUP = 6;

    /**
     * If the calling thread and the thread that owns the window are attached 
     * to different input queues, the system posts the request to the thread 
     * that owns the window. This prevents the calling thread from blocking 
     * its execution while other threads process the request.
     */
    int SWP_ASYNCWINDOWPOS = 0x4000; 
    		
    /**
     * Prevents generation of the WM_SYNCPAINT message.
     */
    int SWP_DEFERERASE = 0x2000;
    
    /**
     * Draws a frame (defined in the window's class description) around the window.
     */
    int SWP_DRAWFRAME = 0x0020;
    
    /**
     * Applies new frame styles set using the SetWindowLong function. Sends 
     * a WM_NCCALCSIZE message to the window, even if the window's size is 
     * not being changed. If this flag is not specified, WM_NCCALCSIZE is 
     * sent only when the window's size is being changed.
     */
    int SWP_FRAMECHANGED = 0x0020;
    
    /**
     * Hides the window.
     */
    int SWP_HIDEWINDOW = 0x0080;
    
    /**
     * Does not activate the window. If this flag is not set, the window is 
     * activated and moved to the top of either the topmost or non-topmost 
     * group (depending on the setting of the hWndInsertAfter parameter).
     */
    int SWP_NOACTIVATE = 0x0010;
    
    /**
     * Discards the entire contents of the client area. If this flag is not 
     * specified, the valid contents of the client area are saved and copied 
     * back into the client area after the window is sized or repositioned.
     */
    int SWP_NOCOPYBITS = 0x0100;
    
    /**
     * Retains the current position (ignores X and Y parameters).
     */
    int SWP_NOMOVE = 0x0002;
    
    /**
     * Does not change the owner window's position in the Z order.
     */
    int SWP_NOOWNERZORDER = 0x0200;
    
    /**
     * Does not redraw changes. If this flag is set, no repainting of any kind
     *  occurs. This applies to the client area, the nonclient area (including
     *   the title bar and scroll bars), and any part of the parent window 
     *   uncovered as a result of the window being moved. When this flag is 
     *   set, the application must explicitly invalidate or redraw any parts
     *   of the window and parent window that need redrawing.
     */
    int SWP_NOREDRAW = 0x0008;
    
    /**
     * Same as the SWP_NOOWNERZORDER flag.
     */
    int SWP_NOREPOSITION = 0x0200;
    
    /**
     * Used by User32.SetWindowPos. <br>
     * Prevents the window from receiving the WM_WINDOWPOSCHANGING message.
     */
    int SWP_NOSENDCHANGING = 0x0400;
    
    /**
     * Retains the current size (ignores the cx and cy parameters).
     */
    int SWP_NOSIZE = 0x0001;
    		
    /**
     * Retains the current Z order (ignores the hWndInsertAfter parameter).
     */
    int SWP_NOZORDER = 0x0004;

    /**
     * Displays the window.
     */
    int SWP_SHOWWINDOW = 0x0040;
    
    /**
     * Minimizes the window.
     */
    int SC_MINIMIZE = 0xF020;

    /**
     * Maximizes the window.
     */
    int SC_MAXIMIZE = 0xF030;

    /**
     * Creates a push button that posts a WM_COMMAND message to the owner window
     * when the user selects the button.
     */
    int BS_PUSHBUTTON                  = 0x00000000;

    /**
     * Creates a push button that behaves like a BS_PUSHBUTTON style button, but
     * has a distinct appearance.<br>
     * If the button is in a dialog box, the user can select the button by
     * pressing the ENTER key, even when the button does not have the input
     * focus.<br>
     * This style is useful for enabling the user to quickly select the most
     * likely (default) option.
     */
    int BS_DEFPUSHBUTTON               = 0x00000001;

    /**
     * Creates a small, empty check box with text. By default, the text is
     * displayed to the right of the check box.<br>
     * To display the text to the left of the check box, combine this flag with
     * the BS_LEFTTEXT style (or with the equivalent BS_RIGHTBUTTON style).
     */
    int BS_CHECKBOX                    = 0x00000002;

    /**
     * Creates a button that is the same as a check box, except that the check
     * state automatically toggles between checked and cleared each time the
     * user selects the check box.
     */
    int BS_AUTOCHECKBOX                = 0x00000003;

    /**
     * Creates a small circle with text. By default, the text is displayed to
     * the right of the circle.<br>
     * To display the text to the left of the circle, combine this flag with the
     * BS_LEFTTEXT style (or with the equivalent BS_RIGHTBUTTON style).<br>
     * Use radio buttons for groups of related, but mutually exclusive choices.
     */
    int BS_RADIOBUTTON                 = 0x00000004;

    /**
     * Creates a button that is the same as a check box, except that the box can
     * be grayed as well as checked or cleared.<br>
     * Use the grayed state to show that the state of the check box is not
     * determined.
     */
    int BS_3STATE                      = 0x00000005;

    /**
     * Creates a button that is the same as a three-state check box, except that
     * the box changes its state when the user selects it.<br>
     * The state cycles through checked, indeterminate, and cleared.
     */
    int BS_AUTO3STATE                  = 0x00000006;

    /**
     * Creates a rectangle in which other controls can be grouped. Any text
     * associated with this style is displayed in the rectangle's upper left
     * corner.
     */
    int BS_GROUPBOX                    = 0x00000007;

    /**
     * Obsolete, but provided for compatibility with 16-bit versions of Windows.
     * Applications should use BS_OWNERDRAW instead.
     */
    int BS_USERBUTTON                  = 0x00000008;

    /**
     * Creates a button that is the same as a radio button, except that when the
     * user selects it,<br>
     * the system automatically sets the button's check state to checked and
     * automatically sets the check state for all other buttons in the same
     * group to cleared.
     */
    int BS_AUTORADIOBUTTON             = 0x00000009;

    /**
     * A button that only shows the text
     */
    int BS_PUSHBOX                     = 0x0000000A;

    /**
     * Creates an owner-drawn button.<br>
     * The owner window receives a WM_DRAWITEM message when a visual aspect of
     * the button has changed.<br>
     * Do not combine the BS_OWNERDRAW style with any other button styles.
     */
    int BS_OWNERDRAW                   = 0x0000000B;

    /**
     * Do not use this style.<br>
     * A composite style bit that results from using the OR operator on BS_*
     * style bits.<br>
     * It can be used to mask out valid BS_* bits from a given bitmask.<br>
     * Note that this is out of date and does not correctly include all valid
     * styles.<br>
     * Thus, you should not use this style. <br>
     * <br>
     * However, it makes basic GetWindowLong work when trying to test for a
     * button style for basic button controls.
     */
    int BS_TYPEMASK                    = 0x0000000F;

    /**
     * Places text on the left side of the radio button or check box when
     * combined with a radio button or check box style. Same as the
     * BS_RIGHTBUTTON style.
     */
    int BS_LEFTTEXT                    = 0x00000020;


    /**
     * Contains information about a simulated message generated by an input
     * device other than a keyboard or mouse.
     */
    public static class HARDWAREINPUT extends Structure {

        public static class ByReference extends HARDWAREINPUT implements
                                                                  Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public HARDWAREINPUT() {
        }

        public HARDWAREINPUT(Pointer memory) {
            super(memory);
            read();
        }

        public WinDef.DWORD uMsg;
        public WinDef.WORD wParamL;
        public WinDef.WORD wParamH;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "uMsg", "wParamL", "wParamH" });
        }
    }

    /**
     * Used by SendInput to store information for synthesizing input events such
     * as keystrokes, mouse movement, and mouse clicks.
     */
    public static class INPUT extends Structure {

        public static final int INPUT_MOUSE = 0;
        public static final int INPUT_KEYBOARD = 1;
        public static final int INPUT_HARDWARE = 2;

        public static class ByReference extends INPUT implements
                                                          Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public INPUT() {
        }

        public INPUT(Pointer memory) {
            super(memory);
            read();
        }

        public WinDef.DWORD type;
        public INPUT_UNION input = new INPUT_UNION();

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "type", "input" });
        }

        public static class INPUT_UNION extends Union {

            public INPUT_UNION() {
            }

            public INPUT_UNION(Pointer memory) {
                super(memory);
                read();
            }

            public MOUSEINPUT mi;
            public KEYBDINPUT ki;
            public HARDWAREINPUT hi;
        }
    }

    /**
     * Contains information about a simulated keyboard event.
     */
    public static class KEYBDINPUT extends Structure {

        /**
         * If specified, the scan code was preceded by a prefix byte that has
         * the value 0xE0 (224).
         */
        public static final int KEYEVENTF_EXTENDEDKEY = 0x0001;

        /**
         * If specified, the key is being released. If not specified, the key is
         * being pressed.
         */
        public static final int KEYEVENTF_KEYUP = 0x0002;

        /**
         * If specified, the system synthesizes a VK_PACKET keystroke. The wVk
         * parameter must be zero. This flag can only be combined with the
         * KEYEVENTF_KEYUP flag. For more information, see the Remarks section.
         */
        public static final int KEYEVENTF_UNICODE = 0x0004;

        /**
         * If specified, wScan identifies the key and wVk is ignored.
         */
        public static final int KEYEVENTF_SCANCODE = 0x0008;

        public static class ByReference extends KEYBDINPUT implements
                                                               Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public KEYBDINPUT() {
        }

        public KEYBDINPUT(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * A virtual-key code. The code must be a value in the range 1 to 254.
         * If the dwFlags member specifies KEYEVENTF_UNICODE, wVk must be 0.
         */
        public WinDef.WORD wVk;

        /**
         * A hardware scan code for the key. If dwFlags specifies
         * KEYEVENTF_UNICODE, wScan specifies a Unicode character which is to be
         * sent to the foreground application.
         */
        public WinDef.WORD wScan;

        /**
         * Specifies various aspects of a keystroke. This member can be certain
         * combinations of the following values.
         */
        public WinDef.DWORD dwFlags;

        /**
         * The time stamp for the event, in milliseconds. If this parameter is
         * zero, the system will provide its own time stamp.
         */
        public WinDef.DWORD time;

        /**
         * An additional value associated with the keystroke. Use the
         * GetMessageExtraInfo function to obtain this information.
         */
        public BaseTSD.ULONG_PTR dwExtraInfo;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "wVk", "wScan", "dwFlags",
                                                "time", "dwExtraInfo" });
        }
    }

    /**
     * Contains information about a simulated mouse event.
     */
    public static class MOUSEINPUT extends Structure {

        public static class ByReference extends MOUSEINPUT implements
                                                               Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public MOUSEINPUT() {
        }

        public MOUSEINPUT(Pointer memory) {
            super(memory);
            read();
        }

        public WinDef.LONG dx;
        public WinDef.LONG dy;
        public WinDef.DWORD mouseData;
        public WinDef.DWORD dwFlags;
        public WinDef.DWORD time;
        public BaseTSD.ULONG_PTR dwExtraInfo;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "dx", "dy", "mouseData",
                                                "dwFlags", "time", "dwExtraInfo" });
        }
    }

    /**
     * Contains the time of the last input.
     */
    public static class LASTINPUTINFO extends Structure {
        public int cbSize = size();

        // Tick count of when the last input event was received.
        public int dwTime;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cbSize", "dwTime" });
        }
    }

    /**
     * Contains window class information. It is used with the RegisterClassEx
     * and GetClassInfoEx functions.
     *
     * The WNDCLASSEX structure is similar to the WNDCLASS structure. There are
     * two differences. WNDCLASSEX includes the cbSize member, which specifies
     * the size of the structure, and the hIconSm member, which contains a
     * handle to a small icon associated with the window class.
     */
    public class WNDCLASSEX extends Structure {

        /**
         * The Class ByReference.
         */
        public static class ByReference extends WNDCLASSEX implements
                                                               Structure.ByReference {
        }

        /**
         * Instantiates a new wndclassex.
         */
        public WNDCLASSEX() {
            super(W32APITypeMapper.DEFAULT);
        }

        /**
         * Instantiates a new wndclassex.
         *
         * @param memory
         *            the memory
         */
        public WNDCLASSEX(Pointer memory) {
            super(memory, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }

        /** The cb size. */
        public int cbSize = this.size();

        /** The style. */
        public int style;

        /** The lpfn wnd proc. */
        public Callback lpfnWndProc;

        /** The cb cls extra. */
        public int cbClsExtra;

        /** The cb wnd extra. */
        public int cbWndExtra;

        /** The h instance. */
        public HINSTANCE hInstance;

        /** The h icon. */
        public HICON hIcon;

        /** The h cursor. */
        public HCURSOR hCursor;

        /** The hbr background. */
        public HBRUSH hbrBackground;

        /** The lpsz menu name. */
        public String lpszMenuName;

        /** The lpsz class name. */
        public String lpszClassName;

        /** The h icon sm. */
        public HICON hIconSm;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cbSize", "style",
                                                "lpfnWndProc", "cbClsExtra", "cbWndExtra", "hInstance",
                                                "hIcon", "hCursor", "hbrBackground", "lpszMenuName",
                                                "lpszClassName", "hIconSm" });
        }
    }

    /**
     * An application-defined function that processes messages sent to a window.
     * The WNDPROC type defines a pointer to this callback function.
     *
     * WindowProc is a placeholder for the application-defined function name.
     */
    public interface WindowProc extends StdCallCallback {

        /**
         * @param hwnd
         *            [in] Type: HWND
         *
         *            A handle to the window.
         *
         * @param uMsg
         *            [in] Type: UINT
         *
         *            The message.
         *
         *            For lists of the system-provided messages, see
         *            System-Defined Messages.
         *
         * @param wParam
         *            [in] Type: WPARAM
         *
         *            Additional message information. The contents of this
         *            parameter depend on the value of the uMsg parameter.
         *
         * @param lParam
         *            [in] Type: LPARAM
         *
         *            Additional message information. The contents of this
         *            parameter depend on the value of the uMsg parameter.
         *
         * @return the lresult
         */
        LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam);
    }

    /**
     * Each physical display is represented by a monitor handle of type HMONITOR. A valid HMONITOR
     * is guaranteed to be non-NULL. A physical display has the same HMONITOR as long as it is part
     * of the desktop.
     */
    public class HMONITOR extends HANDLE {

        /**
         * Instantiates a new HMONITOR.
         */
        public HMONITOR()
        {
        }

        /**
         * Instantiates a new HMONITOR.
         * @param p the pointer
         */
        public HMONITOR(Pointer p)
        {
            super(p);
        }
    }


    /**
     * Returns NULL.
     */
    final int MONITOR_DEFAULTTONULL =        0x00000000;

    /**
     * Returns a handle to the primary display monitor.
     */
    final int MONITOR_DEFAULTTOPRIMARY =     0x00000001;

    /**
     * Returns a handle to the display monitor that is nearest to the window.
     */
    final int MONITOR_DEFAULTTONEAREST =     0x00000002;

    /**
     * This is the primary display monitor.
     */
    final int MONITORINFOF_PRIMARY =         0x00000001;

    /**
     * Length of the device name in MONITORINFOEX
     */
    final int CCHDEVICENAME =  32;

    /**
     * <p>The MONITORINFO structure contains information about a display monitor.</p><p>
     * The {@link User32#GetMonitorInfo} function stores
     * information into a MONITORINFO structure</p>
     * The MONITORINFO structure is a subset of the MONITORINFOEX structure.
     */
    public class MONITORINFO extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("cbSize", "rcMonitor", "rcWork", "dwFlags");
        /**
         * The size, in bytes, of the structure.
         */
        public int     cbSize = size();

        /**
         * Specifies the display monitor rectangle, expressed in virtual-screen coordinates.
         * Note that if the monitor is not the primary display monitor, some of the
         * rectangle's coordinates may be negative values.
         */
        public RECT    rcMonitor;

        /**
         * Specifies the work area rectangle of the display monitor that can be used by
         * applications, expressed in virtual-screen coordinates. Windows uses this rectangle
         * to maximize an application on the monitor. The rest of the area in rcMonitor
         * contains system windows such as the task bar and side bars. Note that if the
         * monitor is not the primary display monitor, some of the rectangle's coordinates
         * may be negative values.
         */
        public RECT    rcWork;

        /**
         * The attributes of the display monitor. This member can be the following value.
         * <ul><li>MONITORINFOF_PRIMARY</li></ul>
         */
        public int     dwFlags;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * <p>The MONITORINFOEX structure contains information about a display monitor.</p><p>
     * The {@link User32#GetMonitorInfo} function stores
     * information into a MONITORINFOEX structure</p>
     * The MONITORINFOEX structure is a superset of the MONITORINFO structure.
     * The MONITORINFOEX structure adds a string member to contain a name for the display monitor.
     */
    public class MONITORINFOEX extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("cbSize", "rcMonitor", "rcWork", "dwFlags", "szDevice");
        /**
         * The size, in bytes, of the structure.
         */
        public int     cbSize;

        /**
         * Specifies the display monitor rectangle, expressed in virtual-screen coordinates.
         * Note that if the monitor is not the primary display monitor, some of the
         * rectangle's coordinates may be negative values.
         */
        public RECT    rcMonitor;

        /**
         * Specifies the work area rectangle of the display monitor that can be used by
         * applications, expressed in virtual-screen coordinates. Windows uses this rectangle
         * to maximize an application on the monitor. The rest of the area in rcMonitor
         * contains system windows such as the task bar and side bars. Note that if the
         * monitor is not the primary display monitor, some of the rectangle's coordinates
         * may be negative values.
         */
        public RECT    rcWork;

        /**
         * The attributes of the display monitor. This member can be the following value.
         * <ul><li>MONITORINFOF_PRIMARY</li></ul>
         */
        public int     dwFlags;

        /**
         * A string that specifies the device name of the monitor being used. Most
         * applications have no use for a display monitor name, and so can save some bytes
         * by using a MONITORINFO structure.
         */
        public char[]  szDevice;

        public MONITORINFOEX() {
            szDevice = new char[CCHDEVICENAME];
            cbSize = size();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * An application-defined callback function that is called by the {@link User32#EnumDisplayMonitors} function.
     * <p>
     * You can use the EnumDisplayMonitors function to enumerate the set of display monitors that intersect
     * the visible region of a specified device context and, optionally, a clipping rectangle. To do this,
     * set the hdc parameter to a non-NULL value, and set the lprcClip parameter as needed.
     * </p><p>
     * You can also use the EnumDisplayMonitors function to enumerate one or more of the display monitors on
     * the desktop, without supplying a device context. To do this, set the hdc parameter of
     * EnumDisplayMonitors to NULL and set the lprcClip parameter as needed.
     * </p>
     * In all cases, EnumDisplayMonitors calls a specified MonitorEnumProc function once for each display
     * monitor in the calculated enumeration set. The MonitorEnumProc function always receives a handle to
     * the display monitor. If the hdc parameter of EnumDisplayMonitors is non-NULL, the MonitorEnumProc
     * function also receives a handle to a device context whose color format is appropriate for the
     * display monitor. You can then paint into the device context in a manner that is optimal for the
     * display monitor.
     */
    public interface MONITORENUMPROC extends StdCallCallback
    {
        /**
         * @param hMonitor A handle to the display monitor. This value will always be non-NULL.
         * @param hdcMonitor A handle to a device context. The device context has color attributes that are
         *        appropriate for the display monitor identified by hMonitor. The clipping area of the device
         *        context is set to the intersection of the visible region of the device context identified
         *        by the hdc parameter of EnumDisplayMonitors, the rectangle pointed to by the lprcClip
         *        parameter of EnumDisplayMonitors, and the display monitor rectangle.
         * @param lprcMonitor A pointer to a RECT structure. If hdcMonitor is non-NULL, this rectangle is the
         *        intersection of the clipping area of the device context identified by hdcMonitor and the
         *        display monitor rectangle. The rectangle coordinates are device-context coordinates.
         *        If hdcMonitor is NULL, this rectangle is the display monitor rectangle. The rectangle
         *        coordinates are virtual-screen coordinates.
         * @param dwData Application-defined data that EnumDisplayMonitors passes directly to the enumeration
         *        function.
         * @return To continue the enumeration, return TRUE. To stop the enumeration, return FALSE.
         */
        public int apply(HMONITOR hMonitor, HDC hdcMonitor, RECT lprcMonitor, LPARAM dwData);
    }

    /* Extendend Exit Windows flags */

    /** Beginning with Windows 8:  You can prepare the system for a faster startup by
     * combining the EWX_HYBRID_SHUTDOWN flag with the EWX_SHUTDOWN flag. */
    int EWX_HYBRID_SHUTDOWN = 0x00400000;

    /** Shuts down all processes running in the logon session of the process that called the ExitWindowsEx function.
     * Then it logs the user off. This flag can be used only by processes running in an interactive user's logon session. */
    int EWX_LOGOFF = 0;

    /** Shuts down the system and turns off the power. The system must support the power-off feature. The calling
     * process must have the SE_SHUTDOWN_NAME privilege. For more information, see {@link com.sun.jna.platform.win32.User32#ExitWindowsEx}. */
    int EWX_POWEROFF = 0x00000008;

    /** Shuts down the system and then restarts the system. The calling process must have the SE_SHUTDOWN_NAME
     * privilege. For more information, see {@link com.sun.jna.platform.win32.User32#ExitWindowsEx}. */
    int EWX_REBOOT = 0x00000002;

    /** Shuts down the system and then restarts it, as well as any applications that have been registered for
     * restart using the RegisterApplicationRestart function. These application receive the WM_QUERYENDSESSION
     * message with lParam set to the ENDSESSION_CLOSEAPP value. For more information, see Guidelines for Applications. */
    int EWX_RESTARTAPPS = 0x00000040;

    /** Shuts down the system to a point at which it is safe to turn off the power. All file buffers
     * have been flushed to disk, and all running processes have stopped. The calling process must have
     * the SE_SHUTDOWN_NAME privilege. For more information, see {@link com.sun.jna.platform.win32.User32#ExitWindowsEx}. Specifying
     * this flag will not turn off the power even if the system supports the power-off feature. You must
     * specify EWX_POWEROFF to do this.
     *
     * Windows XP with SP1:  If the system supports the power-off feature, specifying this flag turns off the power.
     */
    int EWX_SHUTDOWN = 0x00000001;

    /** This flag has no effect if terminal services is enabled. Otherwise, the system does not send the
     * WM_QUERYENDSESSION message. This can cause applications to lose data. Therefore, you should only
     * use this flag in an emergency. */
    int EWX_FORCE = 0x00000004;

    /** Forces processes to terminate if they do not respond to the WM_QUERYENDSESSION or WM_ENDSESSION
     * message within the timeout interval. For more information, see {@link com.sun.jna.platform.win32.User32#ExitWindowsEx}. */
    int EWX_FORCEIFHUNG = 0x00000010;

    /* GetAncestor properties */
    /**
     * Retrieves the parent window. This does not include the owner, as it does with the GetParent function.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms633502(v=vs.85).aspx">MSDN</a>
     */
    int GA_PARENT = 1;

    /**
     * Retrieves the root window by walking the chain of parent windows.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms633502(v=vs.85).aspx">MSDN</a>
     */
    int GA_ROOT = 2;

    /**
     * Retrieves the owned root window by walking the chain of parent and owner windows returned by GetParent.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms633502(v=vs.85).aspx">MSDN</a>
     */
    int GA_ROOTOWNER = 3;

    /* GetClassLong properties */
    /**
     * Retrieves an ATOM value that uniquely identifies the window class. This
     * is the same atom that the RegisterClassEx function returns.
     */
    int GCW_ATOM = -32;

    /**
     * Retrieves a handle to the icon associated with the class.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms633588(v=vs.85).aspx">MSDN</a>
     */
    int GCL_HICON = -14;

    /**
     * Retrieves a handle to the small icon associated with the class.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms633588(v=vs.85).aspx">MSDN</a>
     */
    int GCL_HICONSM = -34;

    /**
     * Retrieves the size, in bytes, of the extra memory associated with the
     * class.
     */
    int GCL_CBCLSEXTRA = -20;

    /**
     * Retrieves the size, in bytes, of the extra window memory associated with
     * each window in the class. For information on how to access this memory,
     * see GetWindowLongPtr.
     */
    int GCL_CBWNDEXTRA = -18;

    /**
     * Retrieves a handle to the background brush associated with the class.
     */
    int GCLP_HBRBACKGROUND = -10;

    /**
     * Retrieves a handle to the cursor associated with the class.
     */
    int GCLP_HCURSOR = -12;

    /**
     * Retrieves a handle to the icon associated with the class.
     */
    int GCLP_HICON = -14;

    /**
     * Retrieves a handle to the small icon associated with the class.
     */
    int GCLP_HICONSM = -34;

    /**
     * Retrieves a handle to the module that registered the class.
     */
    int GCLP_HMODULE = -16;

    /**
     * Retrieves the pointer to the menu name string. The string identifies the
     * menu resource associated with the class.
     */
    int GCLP_MENUNAME = -8;

    /**
     * Retrieves the window-class style bits.
     */
    int GCL_STYLE = -26;

    /**
     * Retrieves the address of the window procedure, or a handle representing
     * the address of the window procedure. You must use the CallWindowProc
     * function to call the window procedure.
     */
    int GCLP_WNDPROC = -24;

    /* SendMessageTimeout properties */
    /**
     * The function returns without waiting for the time-out period to elapse if
     * the receiving thread appears to not respond or "hangs."
     */
    int SMTO_ABORTIFHUNG = 0x0002;

    /**
     * Prevents the calling thread from processing any other requests until the
     * function returns.
     */
    int SMTO_BLOCK = 0x0001;

    /**
     * The calling thread is not prevented from processing other requests while
     * waiting for the function to return.
     */
    int SMTO_NORMAL = 0x0000;

    /**
     * The function does not enforce the time-out period as long as the
     * receiving thread is processing messages.
     */
    int SMTO_NOTIMEOUTIFNOTHUNG = 0x0008;

    /**
     * The function should return 0 if the receiving window is destroyed or its
     * owning thread dies while the message is being processed.
     */
    int SMTO_ERRORONEXIT=0x0020;

    /* GetIconInfo properties */

    /**
     * Standard arrow and small hourglass cursor.
     */
    int IDC_APPSTARTING = 32650;

    /**
     * Standard arrow cursor.
     */
    int IDC_ARROW = 32512;

    /**
     * Crosshair cursor.
     */
    int IDC_CROSS = 32515;

    /**
     * Hand cursor.
     */
    int IDC_HAND = 32649;

    /**
     * Arrow and question mark cursor.
     */
    int IDC_HELP = 32651;

    /**
     * I-beam cursor.
     */
    int IDC_IBEAM = 32513;

    /**
     * Slashed circle cursor.
     */
    int IDC_NO = 32648;

    /**
     * Four-pointed arrow cursor pointing north, south, east, and west.
     */
    int IDC_SIZEALL = 32646;

    /**
     * Double-pointed arrow cursor pointing northeast and southwest.
     */
    int IDC_SIZENESW = 32643;

    /**
     * Double-pointed arrow cursor pointing north and south.
     */
    int IDC_SIZENS = 32645;

    /**
     * Double-pointed arrow cursor pointing northwest and southeast.
     */
    int IDC_SIZENWSE = 32642;

    /**
     * Double-pointed arrow cursor pointing west and east.
     */
    int IDC_SIZEWE = 32644;

    /**
     * Vertical arrow cursor.
     */
    int IDC_UPARROW = 32516;

    /**
     * Hourglass cursor.
     */
    int IDC_WAIT = 32514;

    /**
     * Application icon.
     */
    int IDI_APPLICATION = 32512;

    /**
     * Asterisk icon.
     */
    int IDI_ASTERISK = 32516;

    /**
     * Exclamation point icon.
     */
    int IDI_EXCLAMATION = 32515;

    /**
     * Stop sign icon.
     */
    int IDI_HAND = 32513;

    /**
     * Question-mark icon.
     */
    int IDI_QUESTION = 32514;

    /**
     * Application icon. Windows 2000: Windows logo icon.
     */
    int IDI_WINLOGO = 32517;

        /* Types of devices in RAWINPUTDEVICELIST */
    /** The device is a mouse. */
    int RIM_TYPEMOUSE =  0;

    /** The device is a keyboard. */
    int RIM_TYPEKEYBOARD = 1;

    /** The device is an HID that is not a keyboard and not a mouse. **/
    int RIM_TYPEHID = 2;

    /**
     * Contains information about a raw input device.
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/ms645568(v=vs.85).aspx"></A>
     */
    public class RAWINPUTDEVICELIST extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("hDevice", "dwType");
        public HANDLE hDevice;
        public int dwType;

        public RAWINPUTDEVICELIST() {
            super();
        }

        public RAWINPUTDEVICELIST(Pointer p) {
            super(p);
        }

        public int sizeof() {
            return calculateSize(false);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        @Override
        public String toString() {
            return "hDevice=" + hDevice + ", dwType=" + dwType;
        }
    }

    /**
     * A handle to a bitmap (HBITMAP).
     */
    public int CF_BITMAT = 2;
    /**
     * A memory object containing a BITMAPINFO structure followed by the bitmap
     * bits.
     */
    public int CF_DIB = 8;
    /**
     * A memory object containing a BITMAPV5HEADER structure followed by the
     * bitmap color space information and the bitmap bits.
     */
    public int CF_DIBV5 = 17;
    /**
     * Software Arts' Data Interchange Format.
     */
    public int CF_DIF = 5;
    /**
     * Bitmap display format associated with a private format. The hMem
     * parameter must be a handle to data that can be displayed in bitmap format
     * in lieu of the privately formatted data.
     */
    public int CF_DSPBITMAP = 0x0082;
    /**
     * Enhanced metafile display format associated with a private format. The
     * hMem parameter must be a handle to data that can be displayed in enhanced
     * metafile format in lieu of the privately formatted data.
     */
    public int CF_DSPENHMETAFILE = 0x008E;
    /**
     * Metafile-picture display format associated with a private format. The
     * hMem parameter must be a handle to data that can be displayed in
     * metafile-picture format in lieu of the privately formatted data.
     */
    public int CF_DSPMETAFILEPICT = 0x0083;
    /**
     * Text display format associated with a private format. The hMem parameter
     * must be a handle to data that can be displayed in text format in lieu of
     * the privately formatted data.
     */
    public int CF_DSPTEXT = 0x0081;
    /**
     * A handle to an enhanced metafile (HENHMETAFILE).
     */
    public int CF_ENHMETAFILE = 14;
    /**
     * Start of a range of integer values for application-defined GDI object
     * clipboard formats. The end of the range is CF_GDIOBJLAST.
     *
     * <p>
     * Handles associated with clipboard formats in this range are not
     * automatically deleted using the GlobalFree function when the clipboard is
     * emptied. Also, when using values in this range, the hMem parameter is not
     * a handle to a GDI object, but is a handle allocated by the GlobalAlloc
     * function with the GMEM_MOVEABLE flag.</p>
     */
    public int CF_GDIOBJFIRST = 0x0300;
    /**
     * @see WinUser#CF_GDIOBJFIRST
     */
    public int CF_GDIOBJLAST = 0x03FF;
    /**
     * A handle to type HDROP that identifies a list of files. An application
     * can retrieve information about the files by passing the handle to the
     * DragQueryFile function.
     */
    public int CF_HDROP = 15;
    /**
     * The data is a handle to the locale identifier associated with text in the
     * clipboard. When you close the clipboard, if it contains CF_TEXT data but
     * no CF_LOCALE data, the system automatically sets the CF_LOCALE format to
     * the current input language. You can use the CF_LOCALE format to associate
     * a different locale with the clipboard text.
     *
     * <p>
     * An application that pastes text from the clipboard can retrieve this
     * format to determine which character set was used to generate the
     * text.</p>
     * <p>
     * Note that the clipboard does not support plain text in multiple character
     * sets. To achieve this, use a formatted text data type such as RTF
     * instead.</p>
     * <p>
     * The system uses the code page associated with CF_LOCALE to implicitly
     * convert from CF_TEXT to CF_UNICODETEXT. Therefore, the correct code page
     * table is used for the conversion.</p>
     */
    public int CF_LOCALE = 16;
    /**
     * Handle to a metafile picture format as defined by the METAFILEPICT
     * structure. When passing a CF_METAFILEPICT handle by means of DDE, the
     * application responsible for deleting hMem should also free the metafile
     * referred to by the CF_METAFILEPICT handle.
     */
    public int CF_METAFILEPICT = 3;
    /**
     * Text format containing characters in the OEM character set. Each line
     * ends with a carriage return/linefeed (CR-LF) combination. A null
     * character signals the end of the data.
     */
    public int CF_OEMTEXT = 7;
    /**
     * Owner-display format. The clipboard owner must display and update the
     * clipboard viewer window, and receive the WM_ASKCBFORMATNAME,
     * WM_HSCROLLCLIPBOARD, WM_PAINTCLIPBOARD, WM_SIZECLIPBOARD, and
     * WM_VSCROLLCLIPBOARD messages. The hMem parameter must be NULL.
     */
    public int CF_OWNERDISPLAY = 0x0080;
    /**
     * Handle to a color palette. Whenever an application places data in the
     * clipboard that depends on or assumes a color palette, it should place the
     * palette on the clipboard as well.
     *
     * <p>
     * If the clipboard contains data in the CF_PALETTE (logical color palette)
     * format, the application should use the SelectPalette and RealizePalette
     * functions to realize (compare) any other data in the clipboard against
     * that logical palette.</p>
     *<p>
     * When displaying clipboard data, the clipboard always uses as its current
     * palette any object on the clipboard that is in the CF_PALETTE format.</p>
     */
    public int CF_PALETTE = 9;
    /**
     * Data for the pen extensions to the Microsoft Windows for Pen Computing.
     */
    public int CF_PENDATA = 10;
    /**
     * Start of a range of integer values for private clipboard formats. The
     * range ends with CF_PRIVATELAST. Handles associated with private clipboard
     * formats are not freed automatically; the clipboard owner must free such
     * handles, typically in response to the WM_DESTROYCLIPBOARD message.
     */
    public int CF_PRIVATEFIRST = 0x0200;
    /**
     * @see WinUser#CF_PRIVATEFIRST
     */
    public int CF_PRIVATELAST = 0x02FF;
    /**
     * Represents audio data more complex than can be represented in a CF_WAVE
     * standard wave format.
     */
    public int CF_RIFF = 11;
    /**
     * Microsoft Symbolic Link (SYLK) format.
     */
    public int CF_SYLK = 4;
    /**
     * Text format. Each line ends with a carriage return/linefeed (CR-LF)
     * combination. A null character signals the end of the data. Use this
     * format for ANSI text.
     */
    public int CF_TEXT = 1;
    /**
     * Tagged-image file format.
     */
    public int CF_TIFF = 6;
    /**
     * Unicode text format. Each line ends with a carriage return/linefeed
     * (CR-LF) combination. A null character signals the end of the data.
     */
    public int CF_UNICODETEXT = 13;
    /**
     * Represents audio data in one of the standard wave formats, such as 11 kHz
     * or 22 kHz PCM.
     */
    public int CF_WAVE = 12;
}
