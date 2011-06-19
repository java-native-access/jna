package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/** 
 * Provides access to the w32 MSI installer library.
 */
public interface Msi extends StdCallLibrary {

    Msi INSTANCE = (Msi)
        Native.loadLibrary("msi", Msi.class, W32APIOptions.DEFAULT_OPTIONS);

	static int INSTALLSTATE_NOTUSED = -7;
    static int INSTALLSTATE_BADCONFIG = -6;
    static int INSTALLSTATE_INCOMPLETE = -5;
    static int INSTALLSTATE_SOURCEABSENT = -4;
    static int INSTALLSTATE_MOREDATA = -3;
    static int INSTALLSTATE_INVALIDARG = -2;
    static int INSTALLSTATE_UNKNOWN = -1;
    static int INSTALLSTATE_BROKEN =  0;
    static int INSTALLSTATE_ADVERTISED =  1;
    static int INSTALLSTATE_REMOVED =  1;
    static int INSTALLSTATE_ABSENT =  2;
    static int INSTALLSTATE_LOCAL =  3;
    static int INSTALLSTATE_SOURCE =  4;
    static int INSTALLSTATE_DEFAULT =  5;
		
	int MsiGetComponentPathW(WString szProduct, WString szComponent, char[] lpPathBuf, IntByReference pcchBuf);
	
	int MsiLocateComponentW(WString szComponent, char[] lpPathBuf, IntByReference pcchBuf);
	
	int MsiGetProductCodeW(WString szComponent, char[] lpProductBuf);

    int MsiEnumComponents(WinDef.DWORD iComponentIndex, char[] lpComponentBuf);
}
