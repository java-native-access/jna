/* Copyright (c) 2015 Andreas "PAX" L\u00FCck, All Rights Reserved
 *
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Psapi.MODULEINFO;
import com.sun.jna.platform.win32.Psapi.PERFORMANCE_INFORMATION;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.MEMORY_BASIC_INFORMATION;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.ptr.IntByReference;

/**
 * Applies API tests on {@link Psapi}.
 *
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public class PsapiTest {
    @Test
    public void testGetModuleFileNameEx() {
        final JFrame w = new JFrame();
        try {
            w.setVisible(true);
            final String searchSubStr = "\\bin\\java";
            final HWND hwnd = new HWND(Native.getComponentPointer(w));

            final IntByReference pid = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);

            final HANDLE process = Kernel32.INSTANCE.OpenProcess(
                    0x0400 | 0x0010, false, pid.getValue());
            if (process == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            // check ANSI function
            final byte[] filePathAnsi = new byte[1025];
            int length = Psapi.INSTANCE.GetModuleFileNameExA(process, null,
                    filePathAnsi, filePathAnsi.length - 1);
            if (length == 0) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            assertTrue(
                    "Path didn't contain '" + searchSubStr + "': "
                    + Native.toString(filePathAnsi),
                    Native.toString(filePathAnsi).toLowerCase()
                            .contains(searchSubStr));

            // check Unicode function
            final char[] filePathUnicode = new char[1025];
            length = Psapi.INSTANCE.GetModuleFileNameExW(process, null,
                    filePathUnicode, filePathUnicode.length - 1);
            if (length == 0) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            assertTrue(
                    "Path didn't contain '" + searchSubStr + "': "
                    + Native.toString(filePathUnicode),
                    Native.toString(filePathUnicode).toLowerCase()
                            .contains(searchSubStr));

            // check default function
            final int memAllocSize = 1025 * Native.WCHAR_SIZE;
            final Memory filePathDefault = new Memory(memAllocSize);
            length = Psapi.INSTANCE.GetModuleFileNameEx(process, null,
                    filePathDefault, (memAllocSize / Native.WCHAR_SIZE) - 1);
            if (length == 0) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            assertTrue(
                    "Path didn't contain '"
                    + searchSubStr
                    + "': "
                    + Native.toString(filePathDefault.getCharArray(0,
                            memAllocSize / Native.WCHAR_SIZE)),
                    Native.toString(
                            filePathDefault.getCharArray(0, memAllocSize
                                    / Native.WCHAR_SIZE)).toLowerCase()
                            .contains(searchSubStr));
        } finally {
            w.dispose();
        }
    }

    @Test
    public void testEnumProcessModules() {
        HANDLE me = null;
        Win32Exception we = null;

        try {
            me = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_ALL_ACCESS, false, Kernel32.INSTANCE.GetCurrentProcessId());
            assertTrue("Handle to my process should not be null", me != null);

            List<HMODULE> list = new LinkedList<HMODULE>();

            HMODULE[] lphModule = new HMODULE[100 * 4];
            IntByReference lpcbNeeded = new IntByReference();

            if (!Psapi.INSTANCE.EnumProcessModules(me, lphModule, lphModule.length, lpcbNeeded)) {
                throw new Win32Exception(Native.getLastError());
            }

            for (int i = 0; i < lpcbNeeded.getValue() / 4; i++) {
                list.add(lphModule[i]);
            }

            assertTrue("List should have at least 1 item in it.", list.size() > 0);
        } catch (Win32Exception e) {
            we = e;
            throw we;   // re-throw to invoke finally block
        } finally {
            try {
                Kernel32Util.closeHandle(me);
            } catch(Win32Exception e) {
                if (we == null) {
                    we = e;
                } else {
                    we.addSuppressedReflected(e);
                }
            }
            if (we != null) {
                throw we;
            }
        }
    }

    @Test
    public void testGetModuleInformation() {
        HANDLE me = null;
        Win32Exception we = null;

        try {
            me = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_ALL_ACCESS, false, Kernel32.INSTANCE.GetCurrentProcessId());
            assertTrue("Handle to my process should not be null", me != null);

            List<HMODULE> list = new LinkedList<HMODULE>();

            HMODULE[] lphModule = new HMODULE[100 * 4];
            IntByReference lpcbNeeded = new IntByReference();

            if (!Psapi.INSTANCE.EnumProcessModules(me, lphModule, lphModule.length, lpcbNeeded)) {
                throw new Win32Exception(Native.getLastError());
            }

            for (int i = 0; i < lpcbNeeded.getValue() / 4; i++) {
                list.add(lphModule[i]);
            }

            assertTrue("List should have at least 1 item in it.", list.size() > 0);

            MODULEINFO lpmodinfo = new MODULEINFO();

            if (!Psapi.INSTANCE.GetModuleInformation(me, list.get(0), lpmodinfo, lpmodinfo.size())) {
                throw new Win32Exception(Native.getLastError());
            }

            assertTrue("MODULEINFO.EntryPoint should not be null.", lpmodinfo.EntryPoint != null);

        } catch (Win32Exception e) {
            we = e;
            throw we;   // re-throw to invoke finally block
        } finally {
            try {
                Kernel32Util.closeHandle(me);
            } catch(Win32Exception e) {
                if (we == null) {
                    we = e;
                } else {
                    we.addSuppressedReflected(e);
                }
            }
            if (we != null) {
                throw we;
            }
        }
    }

    @Test
    public void testGetProcessImageFileName() {
        HANDLE me = null;
        Win32Exception we = null;

        try {
            me = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_ALL_ACCESS, false, Kernel32.INSTANCE.GetCurrentProcessId());
            assertTrue("Handle to my process should not be null", me != null);

            char[] buffer = new char[256];
            Psapi.INSTANCE.GetProcessImageFileName(me, buffer, 256);
            String path = new String(buffer);
            assertTrue("Image path should contain 'java' and '.exe'", path.contains("java") && path.contains(".exe"));
        } catch (Win32Exception e) {
            we = e;
            throw we;   // re-throw to invoke finally block
        } finally {
            try {
                Kernel32Util.closeHandle(me);
            } catch(Win32Exception e) {
                if (we == null) {
                    we = e;
                } else {
                    we.addSuppressedReflected(e);
                }
            }
            if (we != null) {
                throw we;
            }
        }
    }

    @Test
    public void testGetPerformanceInfo() {
        PERFORMANCE_INFORMATION perfInfo = new PERFORMANCE_INFORMATION();
        assertTrue(Psapi.INSTANCE.GetPerformanceInfo(perfInfo, perfInfo.size()));
        assertTrue(perfInfo.ProcessCount.intValue() > 0);
    }

    @Test
    public void testEnumProcesses() {
        int size = 0;
        int[] lpidProcess = null;
        IntByReference lpcbNeeded = new IntByReference();
        do {
            size += 1024;
            lpidProcess = new int[size];
            if (!Psapi.INSTANCE.EnumProcesses(lpidProcess, size * DWORD.SIZE, lpcbNeeded)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        } while (size == lpcbNeeded.getValue() / DWORD.SIZE);
        assertTrue("Size of pid list in bytes should be a multiple of " + DWORD.SIZE, lpcbNeeded.getValue() % DWORD.SIZE == 0);

        int myPid = Kernel32.INSTANCE.GetCurrentProcessId();
        boolean foundMyPid = false;
        for (int i = 0; i < lpcbNeeded.getValue() / DWORD.SIZE; i++) {
            if (lpidProcess[i] == myPid) {
                foundMyPid = true;
                break;
            }
        }
        assertTrue("List should contain my pid", foundMyPid);
    }

    @Test
    public void testQueryWorkingSetEx() {
        Win32Exception we = null;
        HANDLE selfHandle = Kernel32.INSTANCE.GetCurrentProcess();
        MEMORY_BASIC_INFORMATION mbi = new MEMORY_BASIC_INFORMATION();
        try {
            SIZE_T bytesRead = Kernel32.INSTANCE.VirtualQueryEx(selfHandle, Pointer.NULL, mbi, new SIZE_T(mbi.size()));
            assertNotEquals("Kernel should be able to read this Process' Bytes", bytesRead.intValue(), 0);
            Psapi.PSAPI_WORKING_SET_EX_INFORMATION pswsi = new Psapi.PSAPI_WORKING_SET_EX_INFORMATION();
            pswsi.VirtualAddress = mbi.baseAddress;
            if (!Psapi.INSTANCE.QueryWorkingSetEx(selfHandle, pswsi.VirtualAddress, pswsi.size())) {
                throw new Win32Exception(Native.getLastError());
            }
            assertTrue("Virual Attributes should not be null", pswsi.VirtualAttributes != null);
            if (Psapi.INSTANCE.QueryWorkingSetEx(new HANDLE(), pswsi.VirtualAddress, pswsi.size())) {
                throw new Win32Exception(Native.getLastError());
            }
            assertFalse("This line should never be called", true);
        } catch (Win32Exception e) {
            we = e;
            throw we;   // re-throw to invoke finally block
        } finally {
            try {
                Kernel32Util.closeHandle(selfHandle);
            } catch (Win32Exception e) {
                if (we == null) {
                    we = e;
                } else {
                    we.addSuppressedReflected(e);
                }
            }
            if (we != null) {
                throw we;
            }
        }
    }
}

