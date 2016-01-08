package com.sun.jna.platform.win32;

/*
 * @author L W Ahonen, lwahonen@iki.fi
 */

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.platform.win32.WinNT.*;

public interface Shlwapi extends StdCallLibrary {
    Shlwapi INSTANCE = Native.loadLibrary("Shlwapi", Shlwapi.class, W32APIOptions.UNICODE_OPTIONS);

    /**
     * Takes an STRRET structure returned by IShellFolder::GetDisplayNameOf and returns a pointer
     * to an allocated string containing the display name.
     *
     * @param pstr
     *            A pointer to the STRRET structure. When the function returns,
     *            this pointer will no longer be valid.
     * @param pidl
     *            A pointer to the item's ITEMIDLIST structure. This value can be NULL.
     *
     * @param ppszName
     *            A pointer to an allocated string containing the result. StrRetToStr allocates
     *            memory for this string with CoTaskMemAlloc. You should free the string
     *            with CoTaskMemFree when it is no longer needed.
     *
     * @return If this function succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     */

    HRESULT StrRetToStr(PointerByReference pstr, Pointer pidl, PointerByReference ppszName);
}
