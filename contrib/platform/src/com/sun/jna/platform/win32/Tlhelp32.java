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

import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HMODULE;

/**
 * Interface for the Tlhelp32.h header file.
 */
public interface Tlhelp32 {

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
     *
     * Used with Kernel32.CreateToolhelp32Snapshot<br>
     * Includes all modules of the process specified in th32ProcessID in the
     * snapshot. <br>
     * To enumerate the modules, see Module32First.<br>
     * If the function fails with ERROR_BAD_LENGTH, retry the function until it
     * succeeds. <br>
     * 64-bit Windows: Using this flag in a 32-bit process includes the 32-bit
     * modules of the process specified in th32ProcessID, while using it in a
     * 64-bit process includes the 64-bit modules.<br>
     * To include the 32-bit modules of the process specified in th32ProcessID
     * from a 64-bit process, use the TH32CS_SNAPMODULE32 flag.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms682489(v=vs.85).aspx">MSDN</a>
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

    int MAX_MODULE_NAME32 = 255;

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

        public static final List<String> FIELDS = createFieldsOrder(
                "dwSize", "cntUsage", "th32ProcessID", "th32DefaultHeapID", "th32ModuleID",
                "cntThreads", "th32ParentProcessID", "pcPriClassBase", "dwFlags", "szExeFile");

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

        public PROCESSENTRY32() {
            dwSize = new WinDef.DWORD(size());
        }

        public PROCESSENTRY32(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Describes an entry from a list of the modules belonging to the specified
     * process.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms684225(v=vs.85).aspx">MSDN</a>
     */
    public class MODULEENTRY32W extends Structure {

        /**
         * A representation of a MODULEENTRY32 structure as a reference
         */
        public static class ByReference extends MODULEENTRY32W implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static final List<String> FIELDS = createFieldsOrder(
                "dwSize", "th32ModuleID", "th32ProcessID", "GlblcntUsage",
                "ProccntUsage", "modBaseAddr", "modBaseSize", "hModule", "szModule", "szExePath");

        /**
         * The size of the structure, in bytes. Before calling the Module32First
         * function, set this member to sizeof(MODULEENTRY32). If you do not
         * initialize dwSize, Module32First fails.
         */
        public DWORD dwSize;

        /**
         * This member is no longer used, and is always set to one.
         */
        public DWORD th32ModuleID;

        /**
         * The identifier of the process whose modules are to be examined.
         */
        public DWORD th32ProcessID;

        /**
         * The load count of the module, which is not generally meaningful, and
         * usually equal to 0xFFFF.
         */
        public DWORD GlblcntUsage;

        /**
         * The load count of the module (same as GlblcntUsage), which is not
         * generally meaningful, and usually equal to 0xFFFF.
         */
        public DWORD ProccntUsage;

        /**
         * The base address of the module in the context of the owning process.
         */
        public Pointer modBaseAddr;

        /**
         * The size of the module, in bytes.
         */
        public DWORD modBaseSize;

        /**
         * A handle to the module in the context of the owning process.
         */
        public HMODULE hModule;

        /**
         * The module name.
         */
        public char[] szModule = new char[MAX_MODULE_NAME32 + 1];

        /**
         * The module path.
         */
        public char[] szExePath = new char[Kernel32.MAX_PATH];

        public MODULEENTRY32W() {
            dwSize = new WinDef.DWORD(size());
        }

        public MODULEENTRY32W(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * @return The module name.
         */
        public String szModule() {
            return Native.toString(this.szModule);
        }

        /**
         * @return The module path.
         */
        public String szExePath() {
            return Native.toString(this.szExePath);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
