package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public interface Tlhelp32 extends StdCallLibrary {

    public static class PROCESSENTRY32W extends Structure {

        public static class ByReference extends PROCESSENTRY32W implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public PROCESSENTRY32W() {
            dwSize = new WinDef.DWORD(size());
        }

        public PROCESSENTRY32W(Pointer memory) {
            useMemory(memory);
            read();
        }

        public WinDef.DWORD dwSize;
        public WinDef.DWORD cntUsage;
        public WinDef.DWORD th32ProcessID;
        public BaseTSD.ULONG_PTR th32DefaultHeapID;
        public WinDef.DWORD th32ModuleID;
        public WinDef.DWORD cntThreads;
        public WinDef.DWORD th32ParentProcessID;
        public WinDef.LONG pcPriClassBase;
        public WinDef.DWORD dwFlags;
        public char[] szExeFile = new char[WinDef.MAX_PATH];
    }
}
