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

    public int WH_KEYBOARD = 2;
    public int WH_MOUSE = 7;
    public int WH_KEYBOARD_LL = 13;
    public int WH_MOUSE_LL = 14;
    
    public class HHOOK extends HANDLE {
    	
    }
    
    public interface HOOKPROC extends StdCallCallback { 
    	
    }
    
    public int WM_KEYDOWN = 256;
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
}
