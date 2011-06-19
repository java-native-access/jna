package com.sun.jna.platform.win32.structures;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

/**
 * Specifies the window station, desktop, standard handles, and appearance of the main window for a process at creation
 * time.
 */
public class STARTUPINFO extends Structure {

    public static final int STARTF_USESHOWWINDOW = 0x00000001;
    public static final int STARTF_USESIZE = 0x00000002;
    public static final int STARTF_USEPOSITION = 0x00000004;
    public static final int STARTF_USECOUNTCHARS = 0x00000008;
    public static final int STARTF_USEFILLATTRIBUTE = 0x00000010;
    public static final int STARTF_RUNFULLSCREEN = 0x00000020;
    public static final int STARTF_FORCEONFEEDBACK = 0x00000040;
    public static final int STARTF_FORCEOFFFEEDBACK = 0x00000080;
    public static final int STARTF_USESTDHANDLES = 0x00000100;

    public static class ByReference extends STARTUPINFO implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(Pointer memory) {
            super(memory);
        }
    }

    public STARTUPINFO() {
        cb = new WinDef.DWORD(size());
    }

    public STARTUPINFO(Pointer memory) {
        useMemory(memory);
        read();
    }

    public WinDef.DWORD cb;
    public String lpReserved;
    public String lpDesktop;
    public String lpTitle;
    public WinDef.DWORD dwX;
    public WinDef.DWORD dwY;
    public WinDef.DWORD dwXSize;
    public WinDef.DWORD dwYSize;
    public WinDef.DWORD dwXCountChars;
    public WinDef.DWORD dwYCountChars;
    public WinDef.DWORD dwFillAttribute;
    public WinDef.DWORD dwFlags;
    public WinDef.WORD wShowWindow;
    public WinDef.WORD cbReserved2;
    public Pointer lpReserved2;
    public WinNT.HANDLE hStdInput;
    public WinNT.HANDLE hStdOutput;
    public WinNT.HANDLE hStdError;
}
