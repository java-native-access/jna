/* This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public interface Tlhelp32 extends StdCallLibrary {

    WinDef.DWORD TH32CS_SNAPHEAPLIST = new WinDef.DWORD(0x00000001);
    WinDef.DWORD TH32CS_SNAPPROCESS  = new WinDef.DWORD(0x00000002);
    WinDef.DWORD TH32CS_SNAPTHREAD   = new WinDef.DWORD(0x00000004);
    WinDef.DWORD TH32CS_SNAPMODULE   = new WinDef.DWORD(0x00000008);
    WinDef.DWORD TH32CS_SNAPMODULE32 = new WinDef.DWORD(0x00000010);
    WinDef.DWORD TH32CS_SNAPALL      = new WinDef.DWORD((TH32CS_SNAPHEAPLIST.intValue() | TH32CS_SNAPPROCESS.intValue() | TH32CS_SNAPTHREAD.intValue() | TH32CS_SNAPMODULE.intValue()));
    WinDef.DWORD TH32CS_INHERIT      = new WinDef.DWORD(0x80000000);

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
