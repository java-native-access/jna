/* Copyright (c) 2015 Andreas "PAX" L\u00FCck, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
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

import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * The process status application programming interface (PSAPI) is a helper
 * library that makes it easier for you to obtain information about processes
 * and device drivers.
 *
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public interface Psapi extends StdCallLibrary {
    Psapi INSTANCE = Native.loadLibrary("psapi", Psapi.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Retrieves the fully qualified path for the file containing the specified
     * module.
     *
     * @param process
     *            A handle to the process that contains the module.
     * @param module
     *            A handle to the module. If this parameter is NULL,
     *            GetModuleFileNameEx returns the path of the executable file of
     *            the process specified in hProcess.
     * @param lpFilename
     *            A pointer to a buffer that receives the fully qualified path
     *            to the module. If the size of the file name is larger than the
     *            value of the nSize parameter, the function succeeds but the
     *            file name is truncated and null-terminated.
     * @param nSize
     *            The size of the lpFilename buffer, in characters.
     * @return If the function succeeds, the return value specifies the length
     *         of the string copied to the buffer. If the function fails, the
     *         return value is zero. To get extended error information, call
     *         {@link Kernel32Util#getLastErrorMessage()}.
     */
    int GetModuleFileNameExA(HANDLE process, HANDLE module, byte[] lpFilename, int nSize);

    /**
     * Retrieves the fully qualified path for the file containing the specified
     * module.
     *
     * @param process
     *            A handle to the process that contains the module.
     * @param module
     *            A handle to the module. If this parameter is NULL,
     *            GetModuleFileNameEx returns the path of the executable file of
     *            the process specified in hProcess.
     * @param lpFilename
     *            A pointer to a buffer that receives the fully qualified path
     *            to the module. If the size of the file name is larger than the
     *            value of the nSize parameter, the function succeeds but the
     *            file name is truncated and null-terminated.
     * @param nSize
     *            The size of the lpFilename buffer, in characters.
     * @return If the function succeeds, the return value specifies the length
     *         of the string copied to the buffer. If the function fails, the
     *         return value is zero. To get extended error information, call
     *         {@link Kernel32Util#getLastErrorMessage()}.
     */
    int GetModuleFileNameExW(HANDLE process, HANDLE module, char[] lpFilename, int nSize);

    /**
     * Retrieves the fully qualified path for the file containing the specified
     * module.
     *
     * @param process
     *            A handle to the process that contains the module.
     * @param module
     *            A handle to the module. If this parameter is NULL,
     *            GetModuleFileNameEx returns the path of the executable file of
     *            the process specified in hProcess.
     * @param lpFilename
     *            A pointer to a buffer that receives the fully qualified path
     *            to the module. If the size of the file name is larger than the
     *            value of the nSize parameter, the function succeeds but the
     *            file name is truncated and null-terminated.
     * @param nSize
     *            The size of the lpFilename buffer, in characters.
     * @return If the function succeeds, the return value specifies the length
     *         of the string copied to the buffer. If the function fails, the
     *         return value is zero. To get extended error information, call
     *         {@link Kernel32Util#getLastErrorMessage()}.
     */
    int GetModuleFileNameEx(HANDLE process, HANDLE module, Pointer lpFilename, int nSize);

    /**
     *
     * The EnumProcessModules function is primarily designed for use by
     * debuggers and similar applications that must extract module information
     * from another process.<br>
     * If the module list in the target process is corrupted or not yet
     * initialized, or if the module list changes during the function call as a
     * result of DLLs being loaded or unloaded, EnumProcessModules may fail or
     * return incorrect information.<br>
     * It is a good idea to specify a large array of HMODULE values, because it
     * is hard to predict how many modules there will be in the process at the
     * time you call EnumProcessModules.<br>
     * To determine if the lphModule array is too small to hold all module
     * handles for the process, compare the value returned in lpcbNeeded with
     * the value specified in cb.<br>
     * If lpcbNeeded is greater than cb, increase the size of the array and call
     * EnumProcessModules again. To determine how many modules were enumerated
     * by the call to EnumProcessModules, divide the resulting value in the
     * lpcbNeeded parameter by sizeof(HMODULE).<br>
     * The EnumProcessModules function does not retrieve handles for modules
     * that were loaded with the LOAD_LIBRARY_AS_DATAFILE or similar flags. For
     * more information, see LoadLibraryEx.<br>
     * Do not call CloseHandle on any of the handles returned by this function.
     * The information comes from a snapshot, so there are no resources to be
     * freed.<br>
     * If this function is called from a 32-bit application running on WOW64, it
     * can only enumerate the modules of a 32-bit process.<br>
     * If the process is a 64-bit process, this function fails and the last
     * error code is ERROR_PARTIAL_COPY (299).<br>
     * To take a snapshot of specified processes and the heaps, modules, and
     * threads used by these processes, use the CreateToolhelp32Snapshot
     * function.<br>
     * Starting with Windows 7 and Windows Server 2008 R2, Psapi.h establishes
     * version numbers for the PSAPI functions.<br>
     * The PSAPI version number affects the name used to call the function and
     * the library that a program must load. <br>
     * If PSAPI_VERSION is 2 or greater, this function is defined as
     * K32EnumProcessModules in Psapi.h and exported in Kernel32.lib and
     * Kernel32.dll.<br>
     * If PSAPI_VERSION is 1, this function is defined as EnumProcessModules in
     * Psapi.h and exported in Psapi.lib and Psapi.dll as a wrapper that calls
     * K32EnumProcessModules.<br>
     * Programs that must run on earlier versions of Windows as well as Windows
     * 7 and later versions should always call this function as
     * EnumProcessModules. <br>
     * To ensure correct resolution of symbols, add Psapi.lib to the TARGETLIBS
     * macro and compile the program with -DPSAPI_VERSION=1.<br>
     * To use run-time dynamic linking, load Psapi.dll.
     *
     * @param hProcess
     *            A handle to the process.
     * @param lphModule
     *            An array that receives the list of module handles.
     * @param cb
     *            The size of the lphModule array, in bytes.
     * @param lpcbNeeded
     *            The number of bytes required to store all module handles in
     *            the lphModule array.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     * @see <a href="http://msdn.microsoft.com/en-us/library/ms682631(VS.85).aspx">MSDN/a>
     */
    boolean EnumProcessModules(HANDLE hProcess, HMODULE[] lphModule, int cb, IntByReference lpcbNeeded);

    /**
     * To get information for the calling process, pass the handle returned by
     * GetCurrentProcess.<br>
     * The GetModuleInformation function does not retrieve information for
     * modules that were loaded with the LOAD_LIBRARY_AS_DATAFILE flag.<br>
     * For more information, see LoadLibraryEx. <br>
     * Starting with Windows 7 and Windows Server 2008 R2, Psapi.h establishes
     * version numbers for the PSAPI functions.<br>
     * The PSAPI version number affects the name used to call the function and
     * the library that a program must load.<br>
     * If PSAPI_VERSION is 2 or greater, this function is defined as
     * K32GetModuleInformation in Psapi.h and exported in Kernel32.lib and
     * Kernel32.dll. <br>
     * If PSAPI_VERSION is 1, this function is defined as
     * K32GetModuleInformation in Psapi.h and exported in Psapi.lib and
     * Psapi.dll as a wrapper that calls K32GetModuleInformation.<br>
     * Programs that must run on earlier versions of Windows as well as Windows
     * 7 and later versions should always call this function as
     * K32GetModuleInformation. <br>
     * To ensure correct resolution of symbols, add Psapi.lib to the TARGETLIBS
     * macro and compile the program with -DPSAPI_VERSION=1. <br>
     * To use run-time dynamic linking, load Psapi.dll.
     *
     * @param hProcess
     *            A handle to the process that contains the module. The handle
     *            must have the PROCESS_QUERY_INFORMATION and PROCESS_VM_READ
     *            access rights. For more information, see Process Security and
     *            Access Rights.
     * @param hModule
     *            A handle to the module.
     *
     * @param lpmodinfo
     *            A pointer to the MODULEINFO structure that receives
     *            information about the module.
     * @param cb
     *            The size of the MODULEINFO structure, in bytes.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     * @see <a href="http://msdn.microsoft.com/en-us/library/ms683201(VS.85).aspx">MSDN</a>
     */
    boolean GetModuleInformation(HANDLE hProcess, HMODULE hModule, MODULEINFO lpmodinfo, int cb);

    /**
     * @param hProcess
     *            A handle to the process. The handle must have the
     *            PROCESS_QUERY_INFORMATION or PROCESS_QUERY_LIMITED_INFORMATION
     *            access right. For more information, see Process Security and
     *            Access Rights. <br>
     *            Windows Server 2003 and Windows XP: The handle must have the
     *            PROCESS_QUERY_INFORMATION access right.
     * @param lpImageFileName
     *            A pointer to a buffer that receives the full path to the
     *            executable file.
     * @param nSize
     *            The size of the lpImageFileName buffer, in characters.
     * @return If the function succeeds, the return value specifies the length
     *         of the string copied to the buffer. If the function fails, the
     *         return value is zero. To get extended error information, call
     *         GetLastError.
     * @see <a href="http://msdn.microsoft.com/en-us/library/ms683217(VS.85).aspx">MSDN</a>
     */
    int GetProcessImageFileName(HANDLE hProcess, char[] lpImageFileName, int nSize);

    class MODULEINFO extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("lpBaseOfDll", "SizeOfImage", "EntryPoint");

        public Pointer EntryPoint;
        public Pointer lpBaseOfDll;
        public int     SizeOfImage;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
