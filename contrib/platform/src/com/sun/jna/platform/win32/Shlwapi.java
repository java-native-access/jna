package com.sun.jna.platform.win32;


/*
 * Copyright (c) 2015 L W Ahonen, All Rights Reserved
 *
 */

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;

public interface Shlwapi   extends WinNT {
    Shlwapi INSTANCE = (Shlwapi) Native.loadLibrary("Shlwapi", Shlwapi.class, W32APIOptions.UNICODE_OPTIONS);

    HRESULT StrRetToStr(PointerByReference pstr, Pointer pidl, PointerByReference ppszName);
}
