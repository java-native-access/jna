package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

public interface WinUser extends StdCallLibrary {
    public HWND HWND_BROADCAST = new HWND(Pointer.createConstant(0xFFFF));
}
