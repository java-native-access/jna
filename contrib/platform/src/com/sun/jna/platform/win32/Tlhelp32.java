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

/**
 * Interface for the Tlhelp32.h header file.
 */
public interface Tlhelp32 extends StdCallLibrary {

    /**
     * Includes all heaps of the process specified in th32ProcessID in the snapshot. To enumerate the heaps, see
     * Heap32ListFirst.
     */
    WinDef.DWORD TH32CS_SNAPHEAPLIST = new WinDef.DWORD(0x00000001);

    /**
     * Includes all processes in the system in the snapshot. To enumerate the processes, see Process32First.
     */
    WinDef.DWORD TH32CS_SNAPPROCESS  = new WinDef.DWORD(0x00000002);

    /**
     * Includes all threads in the system in the snapshot. To enumerate the threads, see Thread32First.
     */
    WinDef.DWORD TH32CS_SNAPTHREAD   = new WinDef.DWORD(0x00000004);

    /**
     * Includes all modules of the process specified in th32ProcessID in the snapshot. To enumerate the modules, see
     * Module32First. If the function fails with ERROR_BAD_LENGTH, retry the function until it succeeds.
     */
    WinDef.DWORD TH32CS_SNAPMODULE   = new WinDef.DWORD(0x00000008);

    /**
     * Includes all 32-bit modules of the process specified in th32ProcessID in the snapshot when called from a 64-bit
     * process. This flag can be combined with TH32CS_SNAPMODULE or TH32CS_SNAPALL. If the function fails with
     * ERROR_BAD_LENGTH, retry the function until it succeeds.
     */
    WinDef.DWORD TH32CS_SNAPMODULE32 = new WinDef.DWORD(0x00000010);

    /**
     * Includes all processes and threads in the system, plus the heaps and modules of the process specified in th32ProcessID.
     */
    WinDef.DWORD TH32CS_SNAPALL      = new WinDef.DWORD((TH32CS_SNAPHEAPLIST.intValue() |
            TH32CS_SNAPPROCESS.intValue() | TH32CS_SNAPTHREAD.intValue() | TH32CS_SNAPMODULE.intValue()));

    /**
     * Indicates that the snapshot handle is to be inheritable.
     */
    WinDef.DWORD TH32CS_INHERIT      = new WinDef.DWORD(0x80000000);

    /**
     * Describes an entry from a list of the processes residing in the system address space when a snapshot was taken.
     */
    public static class PROCESSENTRY32 extends Structure {

        public static class ByReference extends PROCESSENTRY32 implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public PROCESSENTRY32() {
            dwSize = new WinDef.DWORD(size());
        }

        public PROCESSENTRY32(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * The size of the structure, in bytes. Before calling the Process32First function, set this member to
         * sizeof(PROCESSENTRY32). If you do not initialize dwSize, Process32First fails.
         */
        public WinDef.DWORD dwSize;

        /**
         * This member is no longer used and is always set to zero.
         */
        public WinDef.DWORD cntUsage;

        /**
         * The process identifier.
         */
        public WinDef.DWORD th32ProcessID;

        /**
         * This member is no longer used and is always set to zero.
         */
        public BaseTSD.ULONG_PTR th32DefaultHeapID;

        /**
         * This member is no longer used and is always set to zero.
         */
        public WinDef.DWORD th32ModuleID;

        /**
         * The number of execution threads started by the process.
         */
        public WinDef.DWORD cntThreads;

        /**
         * The identifier of the process that created this process (its parent process).
         */
        public WinDef.DWORD th32ParentProcessID;

        /**
         * The base priority of any threads created by this process.
         */
        public WinDef.LONG pcPriClassBase;

        /**
         * This member is no longer used, and is always set to zero.
         */
        public WinDef.DWORD dwFlags;

        /**
         * The name of the executable file for the process. To retrieve the full path to the executable file, call the
         * Module32First function and check the szExePath member of the MODULEENTRY32 structure that is returned.
         * However, if the calling process is a 32-bit process, you must call the QueryFullProcessImageName function to
         * retrieve the full path of the executable file for a 64-bit process.
         */
        public char[] szExeFile = new char[WinDef.MAX_PATH];
    }
}
