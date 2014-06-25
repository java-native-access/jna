/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.HBRUSH;
import com.sun.jna.platform.win32.WinDef.HCURSOR;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from WinUser.h Microsoft Windows SDK 6.0A.
 * 
 * @author dblock[at]dblock.org
 */
public interface WinUser extends StdCallLibrary, WinDef {
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
    
    int DWL_DLGPROC = Pointer.SIZE;
    int DWL_MSGRESULT = 0;
    int DWL_USER = 2*Pointer.SIZE;

    /* Window Styles */
    
    /** The window has a thin-line border. */
    int WS_BORDER	= 0x800000;
    
    /** The window has a title bar (includes the WS_BORDER style). */
    int WS_CAPTION	= 0xc00000;
    
    /** The window is a child window. A window with this style cannot have a
     * menu bar. This style cannot be used with the WS_POPUP style. */
    int WS_CHILD	= 0x40000000;
    
    /** Same as the WS_CHILD style. */
    int WS_CHILDWINDOW	= 0x40000000;
    
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
    int WS_DISABLED	= 0x8000000;
    
    /** The window has a border of a style typically used with dialog boxes. A
     * window with this style cannot have a title bar. */
    int WS_DLGFRAME	= 0x400000;
    
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
    int WS_GROUP	= 0x20000;
    
    /** The window has a horizontal scroll bar. */
    int WS_HSCROLL	= 0x100000;
    
    /** The window is initially minimized. Same as the WS_MINIMIZE style. */
    int WS_ICONIC	= 0x20000000;
    
    /** The window is initially maximized. */
    int WS_MAXIMIZE	= 0x1000000;
    
    /** The window has a maximize button. Cannot be combined with the
     * WS_EX_CONTEXTHELP style. The WS_SYSMENU style must also be specified.  */
    int WS_MAXIMIZEBOX	= 0x10000;
    
    /** The window is initially minimized. Same as the WS_ICONIC style. */
    int WS_MINIMIZE	= 0x20000000;
    
    /** The window has a minimize button. Cannot be combined with the
     * WS_EX_CONTEXTHELP style. The WS_SYSMENU style must also be specified. */
    int WS_MINIMIZEBOX	= 0x20000;
    
    /** The window style overlapped. The window is an overlapped window. An
     * overlapped window has a title bar and a border. Same as the WS_TILED style. */
    int WS_OVERLAPPED = 0x00000000;
    
    /** The windows is a pop-up window. This style cannot be used with the WS_CHILD style. */
    int WS_POPUP	= 0x80000000;
    
    /** The window has a window menu on its title bar. The WS_CAPTION style must also be specified. */
    int WS_SYSMENU	= 0x80000;
    
    /** The window has a sizing border. Same as the WS_SIZEBOX style. */
    int WS_THICKFRAME	= 0x40000;
    
    /** The window is a pop-up window. The WS_CAPTION and WS_POPUPWINDOW styles
     * must be combined to make the window menu visible. */
    int WS_POPUPWINDOW	= (WS_POPUP | WS_BORDER | WS_SYSMENU);
    
    /** The window is an overlapped window. Same as the WS_TILEDWINDOW style.  */
    int WS_OVERLAPPEDWINDOW	= (WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU |
    		WS_THICKFRAME | WS_MINIMIZEBOX | WS_MAXIMIZEBOX);
    
    /** The window has a sizing border. Same as the WS_THICKFRAME style. */
    int WS_SIZEBOX	= 0x40000;
    
    /** The window is a control that can receive the keyboard focus when the
     * user presses the TAB key. Pressing the TAB key changes the keyboard focus
     * to the next control with the WS_TABSTOP style.
     * 
     * You can turn this style on and off to change dialog box navigation.
     * To change this style after a window has been created, use the SetWindowLong
     * function. For user-created windows and modeless dialogs to work with tab
     * stops, alter the message loop to call the IsDialogMessage function.
     */
    int WS_TABSTOP	= 0x10000;
    
    /** The window is an overlapped window. An overlapped window has a
     * title bar and a border. Same as the WS_OVERLAPPED style. */
    int WS_TILED	= 0;
    
    /** The window is an overlapped window. Same as the WS_OVERLAPPEDWINDOW style. */
    int WS_TILEDWINDOW	= (WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU | 
    		WS_THICKFRAME | WS_MINIMIZEBOX | WS_MAXIMIZEBOX);
    
    /** The window is initially visible. This style can be turned on and off
     * by using the ShowWindow or SetWindowPos function. */
    int WS_VISIBLE	= 0x10000000;
    
    /** The window has a vertical scroll bar. */
    int WS_VSCROLL	= 0x200000;
    
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

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "hWnd", "message", "wParam",
                                                "lParam", "time", "pt" });
        }
    }

    public class FLASHWINFO extends Structure {
        public int cbSize = size();
        public HANDLE hWnd;
        public int dwFlags;
        public int uCount;
        public int dwTimeout;

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cbSize", "hWnd", "dwFlags",
                                                "uCount", "dwTimeout" });
        }
    }

    public interface WNDENUMPROC extends StdCallCallback {
        /** Return whether to continue enumeration. */
        boolean callback(HWND hWnd, Pointer data);
    }

    public interface LowLevelKeyboardProc extends HOOKPROC {
        LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT lParam);
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

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cx", "cy" });
        }
    }

    int AC_SRC_OVER = 0x00;
    int AC_SRC_ALPHA = 0x01;
    int AC_SRC_NO_PREMULT_ALPHA = 0x01;
    int AC_SRC_NO_ALPHA = 0x02;

    public class BLENDFUNCTION extends Structure {
        public byte BlendOp = AC_SRC_OVER; // only valid value
        public byte BlendFlags = 0; // only valid value
        public byte SourceConstantAlpha;
        public byte AlphaFormat;

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "BlendOp", "BlendFlags",
                                                "SourceConstantAlpha", "AlphaFormat" });
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
    int WH_MOUSE = 7;
    int WH_KEYBOARD_LL = 13;
    int WH_MOUSE_LL = 14;

    public class HHOOK extends HANDLE { }

    public interface HOOKPROC extends StdCallCallback { }

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

    int WM_KEYUP = 257;
    int WM_SYSKEYDOWN = 260;
    int WM_SYSKEYUP = 261;

    int WM_SESSION_CHANGE = 0x2b1;
    int WM_CREATE = 0x0001;
    int WM_SIZE = 0x0005;
    int WM_DESTROY = 0x0002;

    public static final int WM_DEVICECHANGE = 0x0219;

    public class KBDLLHOOKSTRUCT extends Structure {
        public int vkCode;
        public int scanCode;
        public int flags;
        public int time;
        public ULONG_PTR dwExtraInfo;

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
     * Retains the current Z order (ignores the hWndInsertAfter parameter).
     */
    int SWP_NOZORDER = 0x0004;

    /**
     * Minimizes the window.
     */
    int SC_MINIMIZE = 0xF020;

    /**
     * Maximizes the window.
     */
    int SC_MAXIMIZE = 0xF030;

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
        }

        /**
         * Instantiates a new wndclassex.
         * 
         * @param memory
         *            the memory
         */
        public WNDCLASSEX(Pointer memory) {
            super(memory);
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
        public WString lpszClassName;

        /** The h icon sm. */
        public HICON hIconSm;

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
    public interface WindowProc extends Callback {

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
     * The MONITORINFO structure contains information about a display monitor.<br/>
     * The {@link MyUser32#GetMonitorInfo(HMONITOR, MONITORINFO)} function stores 
     * information into a MONITORINFO structure<br/><br/>
     * The MONITORINFO structure is a subset of the MONITORINFOEX structure.      
     */
    public class MONITORINFO extends Structure
    {
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
         * <li>MONITORINFOF_PRIMARY</li>
         */
        public int     dwFlags;

        @Override
        protected List<String> getFieldOrder()
        {
            return Arrays.asList("cbSize", "rcMonitor", "rcWork", "dwFlags");
        }
    }

    /**
     * The MONITORINFOEX structure contains information about a display monitor.<br/>
     * The {@link MyUser32#GetMonitorInfo(HMONITOR, MONITORINFOEX)} function stores 
     * information into a MONITORINFOEX structure<br/><br/>
     * The MONITORINFOEX structure is a superset of the MONITORINFO structure. 
     * The MONITORINFOEX structure adds a string member to contain a name for the display monitor. 
     */
    public class MONITORINFOEX extends Structure
    {
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
         * <li>MONITORINFOF_PRIMARY</li>
         */
        public int     dwFlags;

        /**
         * A string that specifies the device name of the monitor being used. Most 
         * applications have no use for a display monitor name, and so can save some bytes 
         * by using a MONITORINFO structure.
         */
        public char[]  szDevice;

        public MONITORINFOEX()
        {
            szDevice = new char[CCHDEVICENAME];
            cbSize = size();
        }

        @Override
        protected List<String> getFieldOrder()
        {
            return Arrays.asList("cbSize", "rcMonitor", "rcWork", "dwFlags", "szDevice");
        }
    }

    /**
     * An application-defined callback function that is called by the {@link MyUser32#EnumDisplayMonitors} function.
     * <br/><br/>
     * You can use the EnumDisplayMonitors function to enumerate the set of display monitors that intersect 
     * the visible region of a specified device context and, optionally, a clipping rectangle. To do this, 
     * set the hdc parameter to a non-NULL value, and set the lprcClip parameter as needed.
     * <br/><br/>
     * You can also use the EnumDisplayMonitors function to enumerate one or more of the display monitors on 
     * the desktop, without supplying a device context. To do this, set the hdc parameter of 
     * EnumDisplayMonitors to NULL and set the lprcClip parameter as needed.
     * <br/><br/>
     * In all cases, EnumDisplayMonitors calls a specified MonitorEnumProc function once for each display 
     * monitor in the calculated enumeration set. The MonitorEnumProc function always receives a handle to 
     * the display monitor. If the hdc parameter of EnumDisplayMonitors is non-NULL, the MonitorEnumProc 
     * function also receives a handle to a device context whose color format is appropriate for the 
     * display monitor. You can then paint into the device context in a manner that is optimal for the 
     * display monitor.
     */
    public interface MONITORENUMPROC extends Callback
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
     * process must have the SE_SHUTDOWN_NAME privilege. For more information, see {@link com.sun.jna.platform.win32.User32.ExitWindowsEx}. */
    int EWX_POWEROFF = 0x00000008;
    
    /** Shuts down the system and then restarts the system. The calling process must have the SE_SHUTDOWN_NAME
     * privilege. For more information, see {@link com.sun.jna.platform.win32.User32.ExitWindowsEx}. */
    int EWX_REBOOT = 0x00000002; 

    /** Shuts down the system and then restarts it, as well as any applications that have been registered for
     * restart using the RegisterApplicationRestart function. These application receive the WM_QUERYENDSESSION
     * message with lParam set to the ENDSESSION_CLOSEAPP value. For more information, see Guidelines for Applications. */
    int EWX_RESTARTAPPS = 0x00000040; 

    /** Shuts down the system to a point at which it is safe to turn off the power. All file buffers
     * have been flushed to disk, and all running processes have stopped. The calling process must have
     * the SE_SHUTDOWN_NAME privilege. For more information, see {@link com.sun.jna.platform.win32.User32.ExitWindowsEx}. Specifying
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
     * message within the timeout interval. For more information, see {@link com.sun.jna.platform.win32.User32.ExitWindowsEx}. */
    int EWX_FORCEIFHUNG = 0x00000010;
}
