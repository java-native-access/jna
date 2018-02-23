/*
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

/*
 * @author L W Ahonen, lwahonen@iki.fi
 */

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.ShTypes.STRRET;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.platform.win32.WinNT.*;

public interface Shlwapi extends StdCallLibrary {
    Shlwapi INSTANCE = Native.loadLibrary("Shlwapi", Shlwapi.class, W32APIOptions.DEFAULT_OPTIONS);

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

    HRESULT StrRetToStr(STRRET pstr, Pointer pidl, PointerByReference ppszName);

    /**
     * Determines if a path string is a valid Universal Naming Convention (UNC) path, as opposed to
     * a path based on a drive letter.
     *
     * @param path
     *            A string containing the path to validate.
     *
     * @return TRUE if the string is a valid UNC path; otherwise, FALSE.
     */
    boolean PathIsUNC(String path);
}
