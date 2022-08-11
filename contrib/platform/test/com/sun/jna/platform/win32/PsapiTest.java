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
import static org.junit.Assert.assertEquals;

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
import com.sun.jna.platform.win32.Psapi.PSAPI_WORKING_SET_EX_INFORMATION;
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
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public void testQueryWorkingSetEx() {
        HANDLE selfHandle = Kernel32.INSTANCE.GetCurrentProcess();

        Memory[] mem = new Memory[4];
        mem[0] = new Memory(4096);
        new Memory(8192); // Try to ensure the memory pages are not adjacent
        mem[1] = new Memory(4096);
        new Memory(8192); // Try to ensure the memory pages are not adjacent
        mem[3] = new Memory(4096);

        try {
            PSAPI_WORKING_SET_EX_INFORMATION[] pswsi = (PSAPI_WORKING_SET_EX_INFORMATION[]) new PSAPI_WORKING_SET_EX_INFORMATION().toArray(4);

            pswsi[0].VirtualAddress = mem[0];
            pswsi[1].VirtualAddress = mem[1];
            pswsi[2].VirtualAddress = mem[2];
            pswsi[3].VirtualAddress = mem[3];

            for(int i = 0; i < pswsi.length; i++) {
                pswsi[i].write();
            }

            assertTrue("Failed to invoke QueryWorkingSetEx (1)", Psapi.INSTANCE.QueryWorkingSetEx(selfHandle, pswsi[0].getPointer(), pswsi[0].size() * pswsi.length));

            for (int i = 0; i < pswsi.length; i++) {
                pswsi[i].read();
                assertTrue("Virtual Attributes should not be null (1)", pswsi[i].VirtualAttributes != null);
                assertEquals("Virtual Address should not change before and after call (1)", pswsi[i].VirtualAddress, mem[i]);
                if (i != 2) {
                    assertTrue("Data was invalid (1)", pswsi[i].isValid());
                    assertFalse("Data was reported as bad (1)", pswsi[i].isBad());
                    assertEquals("Data indicates sharing (1)", pswsi[i].getShareCount(), 0);
                    assertEquals("Data indicated that protection does not match  PAGE_READWRITE (1)",
                            pswsi[i].getWin32Protection(), WinNT.PAGE_READWRITE);
                    assertFalse("Data was reported as shared (1)", pswsi[i].isShared());
                    assertFalse("Data was reported as locked (1)", pswsi[i].isLocked());
                    assertFalse("Data was reported as large pages (1)", pswsi[i].isLargePage());
                } else {
                    assertFalse("Data was reported valid, but expected to be invalid (1)", pswsi[i].isValid());
                }
            }

            // Lock the page we used into memory - this should be reflected in the reported flags in the next call
            assertTrue(Kernel32.INSTANCE.VirtualLock(mem[1], new SIZE_T(4096)));

            for (int i = 0; i < pswsi.length; i++) {
                pswsi[i].write();
            }

            assertTrue("Failed to invoke QueryWorkingSetEx (2)", Psapi.INSTANCE.QueryWorkingSetEx(selfHandle, pswsi[0].getPointer(), pswsi[0].size() * pswsi.length));

            for (int i = 0; i < pswsi.length; i++) {
                pswsi[i].read();
                assertTrue("Virtual Attributes should not be null (2)", pswsi[i].VirtualAttributes != null);
                assertEquals("Virtual Address should not change before and after call (2)", pswsi[i].VirtualAddress, mem[i]);
                if (i != 2) {
                    assertTrue("Virtual Attributes should not be null (2)", pswsi[i].VirtualAttributes != null);
                    assertEquals("Virtual Address should not change before and after call (2)", pswsi[i].VirtualAddress, mem[i]);
                    assertTrue("Data was invalid (2)", pswsi[i].isValid());
                    assertFalse("Data was reported as bad (2)", pswsi[i].isBad());
                    assertEquals("Data indicates sharing (2)", pswsi[i].getShareCount(), 0);
                    assertEquals("Data indicated that protection does not match  PAGE_READWRITE (2)",
                            pswsi[i].getWin32Protection(), WinNT.PAGE_READWRITE);
                    assertFalse("Data was reported as shared (2)", pswsi[i].isShared());
                    // Only the second page should be locked
                    if( i == 1 ) {
                        assertTrue("Data was reported as unlocked (2)", pswsi[i].isLocked());
                    } else {
                        assertFalse("Data was reported as locked (2)", pswsi[i].isLocked());
                    }
                    assertFalse("Data was reported as large pages (2)", pswsi[i].isLargePage());
                } else {
                    assertFalse("Data was reported valid, but expected to be invalid (2)", pswsi[i].isValid());
                }
            }

            // Check that a query against an invalid target succeeds, but report
            // invalid data
            PSAPI_WORKING_SET_EX_INFORMATION pswsi2 = new PSAPI_WORKING_SET_EX_INFORMATION();
            pswsi2.VirtualAddress = null;
            pswsi2.write();
            assertTrue("Failed to invoke QueryWorkingSetEx (3)", Psapi.INSTANCE.QueryWorkingSetEx(WinBase.INVALID_HANDLE_VALUE, pswsi2.getPointer(), pswsi2.size()));
            pswsi2.read();

            assertTrue("Virtual Attributes should not be null (3)", pswsi2.VirtualAttributes != null);
            assertTrue("Virtual Address should not change before and after call (3)", pswsi2.VirtualAddress == null);
            assertFalse("Data was reported valid, but expected to be invalid (3)", pswsi2.isValid());
        } finally {
            try {
                Kernel32Util.closeHandle(selfHandle);
            } catch (Win32Exception e) {
                // Ignore
            }
            try {
                Kernel32.INSTANCE.VirtualUnlock(mem[1], new SIZE_T(4096));
            } catch (Win32Exception e) {
                // Ignore
            }
        }
    }
}

