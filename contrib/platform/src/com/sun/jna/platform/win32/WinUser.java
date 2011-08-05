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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
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
public interface WinUser extends StdCallLibrary {
	public HWND HWND_BROADCAST = new HWND(Pointer.createConstant(0xFFFF));

	public int FLASHW_STOP = 0;
	public int FLASHW_CAPTION = 1;
	public int FLASHW_TRAY = 2;
	public int FLASHW_ALL = (FLASHW_CAPTION | FLASHW_TRAY);
	public int FLASHW_TIMER = 4;
	public int FLASHW_TIMERNOFG = 12;

	public int IMAGE_BITMAP = 0;
	public int IMAGE_ICON = 1;
	public int IMAGE_CURSOR = 2;
	public int IMAGE_ENHMETAFILE = 3;

	public int LR_DEFAULTCOLOR = 0x0000;
	public int LR_MONOCHROME = 0x0001;
	public int LR_COLOR = 0x0002;
	public int LR_COPYRETURNORG = 0x0004;
	public int LR_COPYDELETEORG = 0x0008;
	public int LR_LOADFROMFILE = 0x0010;
	public int LR_LOADTRANSPARENT = 0x0020;
	public int LR_DEFAULTSIZE = 0x0040;
	public int LR_VGACOLOR = 0x0080;
	public int LR_LOADMAP3DCOLORS = 0x1000;
	public int LR_CREATEDIBSECTION = 0x2000;
	public int LR_COPYFROMRESOURCE = 0x4000;
	public int LR_SHARED = 0x8000;

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
    }

    public int GWL_EXSTYLE = -20;
    public int GWL_STYLE = -16;
    public int GWL_WNDPROC = -4;
    public int GWL_HINSTANCE = -6;
    public int GWL_ID = -12;
    public int GWL_USERDATA = -21;
    public int DWL_DLGPROC = 4;
    
    public int DWL_MSGRESULT = 0;
    public int DWL_USER = 8;
    
    public int WS_MAXIMIZE = 0x01000000;
    public int WS_VISIBLE = 0x10000000;
    public int WS_MINIMIZE = 0x20000000;
    public int WS_CHILD = 0x40000000;
    public int WS_POPUP = 0x80000000;
    public int WS_EX_COMPOSITED = 0x20000000;
    public int WS_EX_LAYERED = 0x80000;
    public int WS_EX_TRANSPARENT = 32;

    public int LWA_COLORKEY = 1;
    public int LWA_ALPHA = 2;
    public int ULW_COLORKEY = 1;
    public int ULW_ALPHA = 2;
    public int ULW_OPAQUE = 4;
    
    /** Defines the x- and y-coordinates of a point. */
    public class POINT extends Structure {
        public int x, y;
        public POINT() { }
        public POINT(int x, int y) { 
        	this.x = x; 
        	this.y = y; 
        }
    }
    
    public class MSG extends Structure {
        public HWND hWnd;
        public int message;
        public WPARAM wParam;
        public LPARAM lParam;
        public int time;
        public POINT pt;
    }

	public class FLASHWINFO extends Structure {
		public int cbSize;
		public HANDLE hWnd;
		public int dwFlags;
		public int uCount;
		public int dwTimeout;
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
        public SIZE() { }
        public SIZE(int w, int h) { 
        	this.cx = w; 
        	this.cy = h; 
        }
    }
    
    public int AC_SRC_OVER = 0x00;
    public int AC_SRC_ALPHA = 0x01;
    public int AC_SRC_NO_PREMULT_ALPHA = 0x01;
    public int AC_SRC_NO_ALPHA = 0x02;

    public class BLENDFUNCTION extends Structure {
        public byte BlendOp = AC_SRC_OVER; // only valid value
        public byte BlendFlags = 0; // only valid value
        public byte SourceConstantAlpha;
        public byte AlphaFormat;
    }

    public int VK_SHIFT = 16;
    public int VK_LSHIFT = 0xA0;
    public int VK_RSHIFT = 0xA1;
    public int VK_CONTROL = 17;
    public int VK_LCONTROL = 0xA2;
    public int VK_RCONTROL = 0xA3;
    public int VK_MENU = 18;
    public int VK_LMENU = 0xA4;
    public int VK_RMENU = 0xA5;

    public int MOD_ALT = 0x0001;
    public int MOD_CONTROL = 0x0002;
    public int MOD_NOREPEAT = 0x4000;
    public int MOD_SHIFT = 0x0004;
    public int MOD_WIN = 0x0008;

    public int WH_KEYBOARD = 2;
    public int WH_MOUSE = 7;
    public int WH_KEYBOARD_LL = 13;
    public int WH_MOUSE_LL = 14;
    
    public class HHOOK extends HANDLE {
    	
    }
    
    public interface HOOKPROC extends StdCallCallback { 
    	
    }

    /**
     * The WM_PAINT message is sent when the system or another application makes a request to paint a portion of an \
     * application's window.
     */
    public int WM_PAINT = 0x000F;

    /**
     * Sent as a signal that a window or an application should terminate.
     */
    public int WM_CLOSE = 0x0010;

    /**
     * Indicates a request to terminate an application, and is generated when the application calls the PostQuitMessage
     * function.
     */
    public int WM_QUIT = 0x0012;

    /**
     * Sent to a window when the window is about to be hidden or shown.
     */
    public int WM_SHOWWINDOW = 0x0018;

    /**
     * Sent to the parent window of an owner-drawn button, combo box, list box, or menu when a visual aspect of the
     * button, combo box, list box, or menu has changed.
     */
    public int WM_DRAWITEM = 0x002B;

    /**
     * Posted to the window with the keyboard focus when a nonsystem key is pressed. A nonsystem key is a key that is
     * pressed when the ALT key is not pressed.
     */
    public int WM_KEYDOWN = 0x0100;

    /**
     * Posted to the window with the keyboard focus when a WM_KEYDOWN message is translated by the TranslateMessage
     * function. The WM_CHAR message contains the character code of the key that was pressed.
     */
    public int WM_CHAR = 0x0102;

    /**
     * A window receives this message when the user chooses a command from the Window menu (formerly known as the system
     * or control menu) or when the user chooses the maximize button, minimize button, restore button, or close button.
     */
    public int WM_SYSCOMMAND = 0x0112;

    /**
     * An application sends the WM_MDIMAXIMIZE message to a multiple-document interface (MDI) client window to maximize
     * an MDI child window.
     */
    int WM_MDIMAXIMIZE = 0x0225;

    /**
     * Posted when the user presses a hot key registered by the RegisterHotKey function.
     * The message is placed at the top of the message queue associated with the thread that registered the hot key.
     */
    public int WM_HOTKEY = 0x0312;

    public int WM_KEYUP = 257;
    public int WM_SYSKEYDOWN = 260;
    public int WM_SYSKEYUP = 261;
    
    public class KBDLLHOOKSTRUCT extends Structure {
        public int vkCode;
        public int scanCode;
        public int flags;
        public int time;
        public ULONG_PTR dwExtraInfo;
    }

    public int SM_CXSCREEN = 0;
	public int SM_CYSCREEN = 1;
	public int SM_CXVSCROLL = 2;
	public int SM_CYHSCROLL = 3;
	public int SM_CYCAPTION = 4;
	public int SM_CXBORDER = 5;
	public int SM_CYBORDER = 6;
	public int SM_CXDLGFRAME = 7;
	public int SM_CYDLGFRAME = 8;
	public int SM_CYVTHUMB = 9;
	public int SM_CXHTHUMB = 10;
	public int SM_CXICON = 11;
	public int SM_CYICON = 12;
	public int SM_CXCURSOR = 13;
	public int SM_CYCURSOR = 14;
	public int SM_CYMENU = 15;
	public int SM_CXFULLSCREEN = 16;
	public int SM_CYFULLSCREEN = 17;
	public int SM_CYKANJIWINDOW = 18;
	public int SM_MOUSEPRESENT = 19;
	public int SM_CYVSCROLL = 20;
	public int SM_CXHSCROLL = 21;
	public int SM_DEBUG = 22;
	public int SM_SWAPBUTTON = 23;
	public int SM_RESERVED1 = 24;
	public int SM_RESERVED2 = 25;
	public int SM_RESERVED3 = 26;
	public int SM_RESERVED4 = 27;
	public int SM_CXMIN = 28;
	public int SM_CYMIN = 29;
	public int SM_CXSIZE = 30;
	public int SM_CYSIZE = 31;
	public int SM_CXFRAME = 32;
	public int SM_CYFRAME = 33;
	public int SM_CXMINTRACK = 34;
	public int SM_CYMINTRACK = 35;
	public int SM_CXDOUBLECLK = 36;
	public int SM_CYDOUBLECLK = 37;
	public int SM_CXICONSPACING = 38;
	public int SM_CYICONSPACING = 39;
	public int SM_MENUDROPALIGNMENT = 40;
	public int SM_PENWINDOWS = 41;
	public int SM_DBCSENABLED = 42;
	public int SM_CMOUSEBUTTONS = 43;

	public int SM_CXFIXEDFRAME = SM_CXDLGFRAME; /* ;win40 name change */
	public int SM_CYFIXEDFRAME = SM_CYDLGFRAME; /* ;win40 name change */
	public int SM_CXSIZEFRAME = SM_CXFRAME; /* ;win40 name change */
	public int SM_CYSIZEFRAME = SM_CYFRAME; /* ;win40 name change */

	public int SM_SECURE = 44;
	public int SM_CXEDGE = 45;
	public int SM_CYEDGE = 46;
	public int SM_CXMINSPACING = 47;
	public int SM_CYMINSPACING = 48;
	public int SM_CXSMICON = 49;
	public int SM_CYSMICON = 50;
	public int SM_CYSMCAPTION = 51;
	public int SM_CXSMSIZE = 52;
	public int SM_CYSMSIZE = 53;
	public int SM_CXMENUSIZE = 54;
	public int SM_CYMENUSIZE = 55;
	public int SM_ARRANGE = 56;
	public int SM_CXMINIMIZED = 57;
	public int SM_CYMINIMIZED = 58;
	public int SM_CXMAXTRACK = 59;
	public int SM_CYMAXTRACK = 60;
	public int SM_CXMAXIMIZED = 61;
	public int SM_CYMAXIMIZED = 62;
	public int SM_NETWORK = 63;
	public int SM_CLEANBOOT = 67;
	public int SM_CXDRAG = 68;
	public int SM_CYDRAG = 69;
	public int SM_SHOWSOUNDS = 70;
	public int SM_CXMENUCHECK = 71;
	public int SM_CYMENUCHECK = 72;
	public int SM_SLOWMACHINE = 73;
	public int SM_MIDEASTENABLED = 74;
	public int SM_MOUSEWHEELPRESENT = 75;
	public int SM_XVIRTUALSCREEN = 76;
	public int SM_YVIRTUALSCREEN = 77;
	public int SM_CXVIRTUALSCREEN = 78;
	public int SM_CYVIRTUALSCREEN = 79;
	public int SM_CMONITORS = 80;
	public int SM_SAMEDISPLAYFORMAT = 81;
	public int SM_IMMENABLED = 82;
	public int SM_CXFOCUSBORDER = 83;
	public int SM_CYFOCUSBORDER = 84;
	public int SM_TABLETPC = 86;
	public int SM_MEDIACENTER = 87;
	public int SM_STARTER = 88;
	public int SM_SERVERR2 = 89;
	public int SM_MOUSEHORIZONTALWHEELPRESENT = 91;
	public int SM_CXPADDEDBORDER = 92;
	public int SM_REMOTESESSION = 0x1000;
	public int SM_SHUTTINGDOWN = 0x2000;
	public int SM_REMOTECONTROL = 0x2001;
	public int SM_CARETBLINKINGENABLED = 0x2002;

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
     * The retrieved handle identifies the window of the same type that is highest in the Z order.
     *
     * If the specified window is a topmost window, the handle identifies a topmost window. If the specified window is a
     * top-level window, the handle identifies a top-level window. If the specified window is a child window, the handle
     * identifies a sibling window.
     */
    int GW_HWNDFIRST = 0;

    /**
     * The retrieved handle identifies the window of the same type that is lowest in the Z order.
     *
     * If the specified window is a topmost window, the handle identifies a topmost window. If the specified window is a
     * top-level window, the handle identifies a top-level window. If the specified window is a child window, the handle
     * identifies a sibling window.
     */
    int GW_HWNDLAST = 1;

    /**
     * The retrieved handle identifies the window below the specified window in the Z order.
     *
     * If the specified window is a topmost window, the handle identifies a topmost window. If the specified window is a
     * top-level window, the handle identifies a top-level window. If the specified window is a child window, the handle
     * identifies a sibling window.
     */
    int GW_HWNDNEXT = 2;

    /**
     * The retrieved handle identifies the window above the specified window in the Z order.
     *
     * If the specified window is a topmost window, the handle identifies a topmost window. If the specified window is a
     * top-level window, the handle identifies a top-level window. If the specified window is a child window, the
     * handle identifies a sibling window.
     */
    int GW_HWNDPREV = 3;

    /**
     * The retrieved handle identifies the specified window's owner window, if any. For more information, see Owned
     * Windows.
     */
    int GW_OWNER = 4;

    /**
     * The retrieved handle identifies the child window at the top of the Z order, if the specified window is a parent
     * window; otherwise, the retrieved handle is NULL. The function examines only child windows of the specified
     * window. It does not examine descendant windows.
     */
    int GW_CHILD = 5;

    /**
     * The retrieved handle identifies the enabled popup window owned by the specified window (the search uses the first
     * such window found using GW_HWNDNEXT); otherwise, if there are no enabled popup windows, the retrieved handle is
     * that of the specified window.
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
     * Contains information about a simulated message generated by an input device other than a keyboard or mouse.
     */
    public static class HARDWAREINPUT extends Structure {

        public static class ByReference extends HARDWAREINPUT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public HARDWAREINPUT() {
            setAlignType(Structure.ALIGN_MSVC);
        }

        public HARDWAREINPUT(Pointer memory) {
            setAlignType(Structure.ALIGN_MSVC);
            useMemory(memory);
            read();
        }

        public WinDef.DWORD uMsg;
        public WinDef.WORD wParamL;
        public WinDef.WORD wParamH;
    }

    /**
     * Used by SendInput to store information for synthesizing input events such as keystrokes, mouse movement, and mouse
     * clicks.
     */
    public static class INPUT extends Structure {

        public static final int INPUT_MOUSE = 0;
        public static final int INPUT_KEYBOARD = 1;
        public static final int INPUT_HARDWARE = 2;

        public static class ByReference extends INPUT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public INPUT() {
            setAlignType(Structure.ALIGN_MSVC);
        }

        public INPUT(Pointer memory) {
            setAlignType(Structure.ALIGN_MSVC);
            useMemory(memory);
            read();
        }

        public WinDef.DWORD type;
        public INPUT_UNION input = new INPUT_UNION();

        public static class INPUT_UNION extends Union {

            public INPUT_UNION() {
            }

            public INPUT_UNION(Pointer memory) {
                useMemory(memory);
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
         * If specified, the scan code was preceded by a prefix byte that has the value 0xE0 (224).
         */
        public static final int KEYEVENTF_EXTENDEDKEY = 0x0001;

        /**
         * If specified, the key is being released. If not specified, the key is being pressed.
         */
        public static final int KEYEVENTF_KEYUP = 0x0002;

        /**
         * If specified, the system synthesizes a VK_PACKET keystroke. The wVk parameter must be zero. This flag can only be
         * combined with the KEYEVENTF_KEYUP flag. For more information, see the Remarks section.
         */
        public static final int KEYEVENTF_UNICODE = 0x0004;

        /**
         * If specified, wScan identifies the key and wVk is ignored.
         */
        public static final int KEYEVENTF_SCANCODE = 0x0008;

        public static class ByReference extends KEYBDINPUT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public KEYBDINPUT() {
            setAlignType(Structure.ALIGN_MSVC);
        }

        public KEYBDINPUT(Pointer memory) {
            setAlignType(Structure.ALIGN_MSVC);
            useMemory(memory);
            read();
        }

        /**
         * A virtual-key code. The code must be a value in the range 1 to 254. If the dwFlags member specifies
         * KEYEVENTF_UNICODE, wVk must be 0.
         */
        public WinDef.WORD wVk;

        /**
         * A hardware scan code for the key. If dwFlags specifies KEYEVENTF_UNICODE, wScan specifies a Unicode character
         * which is to be sent to the foreground application.
         */
        public WinDef.WORD wScan;

        /**
         * Specifies various aspects of a keystroke. This member can be certain combinations of the following values.
         */
        public WinDef.DWORD dwFlags;

        /**
         * The time stamp for the event, in milliseconds. If this parameter is zero, the system will provide its own time
         * stamp.
         */
        public WinDef.DWORD time;

        /**
         * An additional value associated with the keystroke. Use the GetMessageExtraInfo function to obtain this
         * information.
         */
        public BaseTSD.ULONG_PTR dwExtraInfo;
    }

    /**
     * Contains information about a simulated mouse event.
     */
    public static class MOUSEINPUT extends Structure {

        public static class ByReference extends MOUSEINPUT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public MOUSEINPUT() {
            setAlignType(Structure.ALIGN_MSVC);
        }

        public MOUSEINPUT(Pointer memory) {
            setAlignType(Structure.ALIGN_MSVC);
            useMemory(memory);
            read();
        }

        public WinDef.LONG dx;
        public WinDef.LONG dy;
        public WinDef.DWORD mouseData;
        public WinDef.DWORD dwFlags;
        public WinDef.DWORD time;
        public BaseTSD.ULONG_PTR dwExtraInfo;
    }
}
