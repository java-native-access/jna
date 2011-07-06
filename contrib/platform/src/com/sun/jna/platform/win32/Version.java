package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Provides access to the w32 version library.
 */
public interface Version extends StdCallLibrary {

    Version INSTANCE = (Version)
        Native.loadLibrary("version", Version.class, W32APIOptions.DEFAULT_OPTIONS);

    int GetFileVersionInfoSize(String lptstrFilename, IntByReference lpdwHandle);

    boolean GetFileVersionInfo(String lptstrFilename, int dwHandle, int dwLen, Pointer lpData);

    boolean VerQueryValue(Pointer pBlock, String lpSubBlock, PointerByReference lplpBuffer, IntByReference puLen);
}
