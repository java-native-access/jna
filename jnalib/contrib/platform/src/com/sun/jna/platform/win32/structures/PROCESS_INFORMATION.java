package com.sun.jna.platform.win32.structures;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

/**
 * Contains information about a newly created process and its primary thread.
 */
public class PROCESS_INFORMATION extends Structure {

    public static class ByReference extends PROCESS_INFORMATION implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(Pointer memory) {
            super(memory);
        }
    }

    public PROCESS_INFORMATION() {
    }

    public PROCESS_INFORMATION(Pointer memory) {
        useMemory(memory);
        read();
    }

    public WinNT.HANDLE hProcess;
    public WinNT.HANDLE hThread;
    public WinDef.DWORD dwProcessId;
    public WinDef.DWORD dwThreadId;
}
