package com.sun.jna.platform.win32.structures;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;

/**
 * Contains information about a simulated mouse event.
 */
public class MOUSEINPUT extends Structure {

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