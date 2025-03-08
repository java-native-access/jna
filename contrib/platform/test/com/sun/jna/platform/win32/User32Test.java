/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import static com.sun.jna.platform.win32.User32.INSTANCE;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HKL;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.LASTINPUTINFO;
import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;
import com.sun.jna.platform.win32.WinUser.MONITORINFO;
import com.sun.jna.platform.win32.WinUser.MONITORINFOEX;

import javax.swing.JFrame;
import javax.swing.JWindow;

/**
 * @author dblock[at]dblock[dot]org
 */
public class User32Test extends AbstractWin32TestSupport {

    public static void main(String[] args) {
        JUnitCore.runClasses(User32Test.class);
    }

    @Before
    public void setUp() {
        Native.setLastError(0);
    }

    /**
     * Iterates over all currently available Desktop windows and searches for
     * the window with the associated process whose full PE file path ends with
     * the specified string (case insensitive).
     *
     * @param filePathEnd
     *            The requested end of the process' full file path.
     * @return Either the found window or {@code null} if nothing was found.
     */
    private static DesktopWindow getWindowByProcessPath(final String filePathEnd) {
        final List<DesktopWindow> allWindows = WindowUtils.getAllWindows(false);
        for (final DesktopWindow wnd : allWindows) {
            if (wnd.getFilePath().toLowerCase()
                    .endsWith(filePathEnd.toLowerCase())) {
                return wnd;
            }
        }

        return null;
    }

    @Test
    public void testNoDuplicateMethodsNames() {
        // see https://github.com/twall/jna/issues/482
        Collection<String> dupSet = AbstractWin32TestSupport.detectDuplicateMethods(User32.class);
        if (dupSet.size() > 0) {
            for (String name : new String[]{
                // has 2 overloads since the original API accepts both MONITORINFO and MONITORINFOEX
                "GetMonitorInfo" // has 2 overloads since there was a broken binding for MonitorFromPoint
                ,
                "MonitorFromPoint"
            }) {
                dupSet.remove(name);
            }
        }

        assertTrue("Duplicate methods found: " + dupSet, dupSet.isEmpty());
    }

    @Test
    public void testGetSystemMetrics() {
        int cursorWidth = INSTANCE.GetSystemMetrics(WinUser.SM_CXCURSOR);
        assertTrue(cursorWidth > 0);
    }

    @Test
    public void testRegisterHotKey() {
        int vk = KeyEvent.VK_D;
        int id = 1;

        assertTrue("RegisterHotKey failed", INSTANCE.RegisterHotKey(null, id, WinUser.MOD_CONTROL | WinUser.MOD_ALT, vk));

        Robot robot = null;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(vk);
            Thread.sleep(50);
            robot.keyRelease(vk);
            WinUser.MSG msg = waitForMessage(500);
            assertNotNull(msg);
            assertEquals(msg.wParam.intValue(), id);

            assertTrue(INSTANCE.UnregisterHotKey(null, id));
            robot.keyPress(vk);
            Thread.sleep(10);
            robot.keyRelease(vk);
            msg = waitForMessage(500);
            assertNull(msg);
        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
            fail();
        } finally {
            if (robot != null) {
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_ALT);
            }
        }
    }

    private WinUser.MSG waitForMessage(int timeout) {
        WinUser.MSG msg = new WinUser.MSG();

        try {
            long time = System.currentTimeMillis();
            while (true) {
                while (INSTANCE.PeekMessage(msg, null, 0, 0, 1)) {
                    if (msg.message == WinUser.WM_HOTKEY) {
                        return msg;
                    }
                }
                if (System.currentTimeMillis() - time > timeout)
                    break;

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void testGetLastInputInfo() throws Exception {
        LASTINPUTINFO plii = new LASTINPUTINFO();
        assertEquals(plii.size(), plii.cbSize);

        assertTrue(User32.INSTANCE.GetLastInputInfo(plii));
        assertTrue(Kernel32.INSTANCE.GetTickCount() >= plii.dwTime);
        assertTrue(plii.dwTime > 0);
    }

    @Test
    public final void testRegisterWindowMessage() {
        final int msg = User32.INSTANCE.RegisterWindowMessage("RM_UNITTEST");
        assertTrue(msg >= 0xC000 && msg <= 0xFFFF);
    }

    @Test
    public final void testMonitorFromPoint() {
        int dwFlags = WinUser.MONITOR_DEFAULTTOPRIMARY;

        POINT.ByValue pt = new POINT.ByValue(0, 0);
        assertNotNull(User32.INSTANCE.MonitorFromPoint(pt, dwFlags));
    }

    @Test
    public final void testMonitorFromRect() {
        int dwFlags = WinUser.MONITOR_DEFAULTTOPRIMARY;
        RECT lprc = new RECT();
        assertNotNull(User32.INSTANCE.MonitorFromRect(lprc, dwFlags));
    }

    @Test
    public final void testMonitorFromWindow() {
        int dwFlags = WinUser.MONITOR_DEFAULTTOPRIMARY;

        HWND hwnd = new HWND();
        assertNotNull(User32.INSTANCE.MonitorFromWindow(hwnd, dwFlags));
    }

    @Test
    public final void testGetMonitorInfo() {
        HMONITOR hMon = User32.INSTANCE.MonitorFromWindow(User32.INSTANCE.GetDesktopWindow(), WinUser.MONITOR_DEFAULTTOPRIMARY);

        assertTrue(User32.INSTANCE.GetMonitorInfo(hMon, new MONITORINFO()).booleanValue());

        assertTrue(User32.INSTANCE.GetMonitorInfo(hMon, new MONITORINFOEX()).booleanValue());
    }

    @Test
    public final void testEnumDisplayMonitors() {

        assertTrue(User32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

            @Override
            public int apply(HMONITOR hMonitor, HDC hdc, RECT rect, LPARAM lparam)
            {
                return 1;
            }
        }, new LPARAM(0)).booleanValue());
    }

    @Test
    public final void testAdjustWindowRect() {
        RECT lpRect = new RECT();
        lpRect.left = 100;
        lpRect.top = 200;
        lpRect.bottom = 300;
        lpRect.right = 500;

        assertTrue(User32.INSTANCE.AdjustWindowRect(lpRect, new DWORD(WinUser.WS_THICKFRAME), new BOOL(1)).booleanValue());

        assertTrue(lpRect.left < 100);
        assertTrue(lpRect.top < 200);
        assertTrue(lpRect.bottom > 300);
        assertTrue(lpRect.right > 500);
    }

    @Ignore("Locks the workstation")
    @Test
    public final void testLockWorkStation() {
        assertTrue(User32.INSTANCE.LockWorkStation().booleanValue());
    }

    @Ignore("Shuts down the workstation")
    @Test
    public final void testExitWindows() {
        assertTrue(User32.INSTANCE.ExitWindowsEx(new UINT(WinUser.EWX_LOGOFF), new DWORD(0x00030000)).booleanValue()); //This only tries to log off.
    }

    @Test
    public void testGetIconInfo() throws Exception {
        final ICONINFO iconInfo = new ICONINFO();
        final HANDLE hImage = User32.INSTANCE.LoadImage(null, new File(
                getClass().getResource("/res/test_icon.ico").toURI())
                .getAbsolutePath(), WinUser.IMAGE_ICON, 0, 0,
                WinUser.LR_LOADFROMFILE);

        try {
            // obtain test icon from classpath
            if (!User32.INSTANCE.GetIconInfo(new HICON(hImage), iconInfo))
                throw new Exception(
                        "Invocation of User32.GetIconInfo() failed: "
                                + Kernel32Util.getLastErrorMessage());
            iconInfo.read();
        } finally {
            if (iconInfo.hbmColor != null
                    && iconInfo.hbmColor.getPointer() != Pointer.NULL)
                GDI32.INSTANCE.DeleteObject(iconInfo.hbmColor);
            if (iconInfo.hbmMask != null
                    && iconInfo.hbmMask.getPointer() != Pointer.NULL)
                GDI32.INSTANCE.DeleteObject(iconInfo.hbmMask);
        }
    }

    @Test
    public void testSendMessageTimeout() {
        DesktopWindow explorerProc = getWindowByProcessPath("explorer.exe");

        assertNotNull(explorerProc);

        final DWORDByReference hIconNumber = new DWORDByReference();
        LRESULT result = User32.INSTANCE
                    .SendMessageTimeout(explorerProc.getHWND(),
                                        WinUser.WM_GETICON,
                                        new WPARAM(WinUser.ICON_BIG),
                                        new LPARAM(0),
                                        WinUser.SMTO_ABORTIFHUNG, 500, hIconNumber);

        assertNotEquals(0, result);
    }

    @Test
    public void testGetClassLongPtr() {
        if (System.getProperty("os.arch", "unknown").equalsIgnoreCase("amd64")) {
            DesktopWindow explorerProc = getWindowByProcessPath("explorer.exe");

            assertNotNull("Could not find explorer.exe process",
                    explorerProc);

            ULONG_PTR result = User32.INSTANCE
                    .GetClassLongPtr(explorerProc.getHWND(),
                            WinUser.GCLP_HMODULE);

            assertNotEquals(0, result.intValue());
        } else {
            System.err.println("GetClassLongPtr only supported on x64");
        }
    }

    @Test
    public void testGetDesktopWindow() {
        HWND desktopWindow = User32.INSTANCE.GetDesktopWindow();
        assertNotNull("Failed to get desktop window HWND", desktopWindow);
    }

    @Test
    public void testPrintWindow() {
        boolean pwResult = User32.INSTANCE.PrintWindow(null, null, 0);
        assertFalse("PrintWindow result should be false", pwResult);
        assertEquals("GetLastError should be ERROR_INVALID_WINDOW_HANDLE.",  WinError.ERROR_INVALID_WINDOW_HANDLE, Native.getLastError());
    }

    @Test
    public void testIsWindowEnabled() {
        boolean iweResult = User32.INSTANCE.IsWindowEnabled(null);
        assertFalse("IsWindowEnabled result should be false", iweResult);
        assertEquals("GetLastError should be ERROR_INVALID_WINDOW_HANDLE.", WinError.ERROR_INVALID_WINDOW_HANDLE, Native.getLastError());
    }

    @Test
    public void testIsWindow() {
        boolean iwResult = User32.INSTANCE.IsWindow(null);
        assertFalse("IsWindow result should be false", iwResult);
    }

    @Test
    public void testFindWindowEx() {
        HWND result = User32.INSTANCE.FindWindowEx(null, null, null, null);
        assertNotNull("FindWindowEx result should not be null", result);
        assertEquals("GetLastError should be ERROR_SUCCESS.", WinError.ERROR_SUCCESS, Native.getLastError());
    }

    @Test
    public void testGetAncestor() {
        HWND desktopWindow = User32.INSTANCE.GetDesktopWindow();
        assertNotNull("Failed to get desktop window HWND", desktopWindow);

        HWND result = User32.INSTANCE.GetAncestor(desktopWindow, WinUser.GA_PARENT);
        assertNull("GetAncestor result should be null", result);
    }

    @Test
    public void testGetParent() {
        HWND desktopWindow = User32.INSTANCE.GetDesktopWindow();
        assertNotNull("Failed to get desktop window HWND", desktopWindow);

        HWND result = User32.INSTANCE.GetParent(desktopWindow);
        assertNull("GetParent result should be null", result);

        final JFrame parent = new JFrame("Parent");
        final JWindow child = new JWindow(parent);

        try {
            parent.setVisible(true);
            child.setVisible(true);

            HWND parentHwnd = new HWND();
            parentHwnd.setPointer(Native.getComponentPointer(parent));

            HWND childHwnd = new HWND();
            childHwnd.setPointer(Native.getComponentPointer(child));

            result = User32.INSTANCE.GetParent(childHwnd);
            assertEquals("GetParent of child should be parent", parentHwnd, result);
        } finally {
            child.dispose();
            parent.dispose();
        }
    }

    @Test
    public void testGetCursorPos() {
        POINT cursorPos = new POINT();
        boolean result = User32.INSTANCE.GetCursorPos(cursorPos);
        assertTrue("GetCursorPos should return true", result);
        assertTrue("X coordinate in POINT should be >= 0", cursorPos.x >= 0);
        assertTrue("Y coordinate in POINT should be >= 0", cursorPos.y >= 0);
    }

    @Test
    public void testSetCursorPos() {
        POINT cursorPos = new POINT();
        boolean result = User32.INSTANCE.GetCursorPos(cursorPos);
        assertTrue("GetCursorPos should return true", result);
        assertTrue("X coordinate in POINT should be >= 0", cursorPos.x >= 0);

        boolean scpResult = User32.INSTANCE.SetCursorPos(cursorPos.x + 20, cursorPos.y);
        assertTrue("SetCursorPos should return true", scpResult);

        POINT cursorPos2 = new POINT();
        boolean result2 = User32.INSTANCE.GetCursorPos(cursorPos2);
        assertTrue("GetCursorPos should return true", result2);
        assertTrue(String.format(
                "X coordinate in POINT should be original cursor position - 20 (Old: %dx%d, New: %dx%d)",
                cursorPos.x, cursorPos.y, cursorPos2.x, cursorPos2.y
                ),
                cursorPos2.x == cursorPos.x + 20
        );
    }

    @Test
    public void testSetWinEventHook() {
        HANDLE result = User32.INSTANCE.SetWinEventHook(0, 0, null, null, 0, 0, 0);
        assertNull("SetWinEventHook result should be null", result);
        assertEquals("GetLastError should be ERROR_INVALID_FILTER_PROC.", WinError.ERROR_INVALID_FILTER_PROC, Native.getLastError());
    }

    @Test
    public void testUnhookWinEvent() {
        boolean iwResult = User32.INSTANCE.UnhookWinEvent(null);
        assertFalse("UnhookWinEvent result should be false", iwResult);
        assertEquals("GetLastError should be ERROR_INVALID_HANDLE.", WinError.ERROR_INVALID_HANDLE, Native.getLastError());
    }

    @Test
    public void testCopyIcon() {
        HICON result = User32.INSTANCE.CopyIcon(null);
        assertNull("CopyIcon result should be false", result);
        assertEquals("GetLastError should be ERROR_INVALID_CURSOR_HANDLE.", WinError.ERROR_INVALID_CURSOR_HANDLE, Native.getLastError());
    }

    @Test
    public void testGetClassLong() {
        int result = User32.INSTANCE.GetClassLong(null, 0);
        assertEquals("GetClassLong result should be 0", 0, result);
        assertEquals("GetLastError should be ERROR_INVALID_WINDOW_HANDLE.", WinError.ERROR_INVALID_WINDOW_HANDLE, Native.getLastError());
    }

    @Test
    public void testGetActiveWindow() {
        HWND result = User32.INSTANCE.GetActiveWindow();
        assertNull("GetActiveWindow result should be null (there is no active window)", result);
    }

    @Test
    public void testBringWindowToTop() {
        boolean result = User32.INSTANCE.BringWindowToTop(null);
        assertFalse("BringWindowToTop(null) result should be false", result);

        final JFrame w = new JFrame("Frame to bring to top");
        try {
            w.setVisible(true);

            HWND hwnd = new HWND();
            hwnd.setPointer(Native.getComponentPointer(w));

            result = User32.INSTANCE.BringWindowToTop(hwnd);
            assertTrue("Couldn't bring frame to top", result);
        } finally {
            w.dispose();
        }
    }

    @Test
    public void testSendMessage() {
        DesktopWindow explorerProc = getWindowByProcessPath("explorer.exe");

        assertNotNull(explorerProc);

        LRESULT result = User32.INSTANCE
                .SendMessage(explorerProc.getHWND(),
                        WinUser.WM_USER,
                        new WPARAM(124),
                        new LPARAM(12345));

        assertNotEquals(0, result);

    }

    /**
     * Test the retrieval of the keyboard layouts.
     */
    @Test
    public void testGetKeyboardLayoutList() {
        int n = User32.INSTANCE.GetKeyboardLayoutList(0, null);
        assertNotEquals(0, n);

        HKL[] lpList = new HKL[n];
        int n2 = User32.INSTANCE.GetKeyboardLayoutList(lpList.length, lpList);
        assertEquals(n, n2);
    }

    /**
     * Test the retrieval of the active keyboard layouts. Check that is is in the
     * list of all keyboard layouts.
     */
    @Test
    public void testGetKeyboardLayout() {
        HKL[] lpList = new HKL[32];
        int n = User32.INSTANCE.GetKeyboardLayoutList(lpList.length, lpList);
        assertNotEquals(0, n);
        HKL hkl = User32.INSTANCE.GetKeyboardLayout(0);
        assertNotNull(hkl);
        for (int i = 0; i < n; i++) {
            if (lpList[i].equals(hkl)) {
                return;
            }
        }
        fail("Active keyboard layout not in list of keyboard layouts.");
    }

    /**
     * Test the retrieval of the active keyboard layout's name.
     */
    @Test
    public void testGetKeyboardLayoutName() {
        char[] name = new char[User32.KL_NAMELENGTH];
        boolean success = User32.INSTANCE.GetKeyboardLayoutName(name);
        assertTrue(success);
        assertNotEquals(0, name[0]);
    }

    /**
     * Test the mapping of a single byte character to Modifier state + VK. Assumes A
     * is mapped to VK_A, which is likely but not necessary.
     */
    @Test
    public void testVkKeyScanExA() {
        HKL dwhkl = User32.INSTANCE.GetKeyboardLayout(0);
        short code = User32.INSTANCE.VkKeyScanExA((byte) 'A', dwhkl);

        int shiftState = code >>> 8;
        assertEquals(User32.MODIFIER_SHIFT_MASK, shiftState);

        Win32VK vk = Win32VK.fromValue(code & 0xFF);
        assertEquals(Win32VK.VK_A, vk);
    }

    /**
     * Test the mapping of a multi byte character to Modifier state + VK. Assumes A
     * is mapped to VK_A, which is likely but not necessary.
     */
    @Test
    public void testVkKeyScanExW() {
        HKL dwhkl = User32.INSTANCE.GetKeyboardLayout(0);
        short code = User32.INSTANCE.VkKeyScanExW('A', dwhkl);

        int shiftState = code >>> 8;
        assertEquals(User32.MODIFIER_SHIFT_MASK, shiftState);

        Win32VK vk = Win32VK.fromValue(code & 0xFF);
        assertEquals(Win32VK.VK_A, vk);
    }

    /**
     * Test the mapping of a VK to a character value. Assumes A is mapped to VK_A,
     * which is likely but not necessary.
     */
    @Test
    public void testMapVirtualKeyEx() {
        HKL dwhkl = User32.INSTANCE.GetKeyboardLayout(0);
        int charValue = User32.INSTANCE.MapVirtualKeyEx(Win32VK.VK_A.code, WinUser.MAPVK_VK_TO_CHAR, dwhkl);
        assertEquals('A', charValue);
    }

    @Test
    public void testToUnicodeEx() {
        // this function cannot be tested reliably as it interacts with the system
        // keyboard stack
        // which is in an undefined state at test execution and may change arbitrarily
        // during the test.
    }

    @Test
    public void testRegisterPowerSettingNotification() {
        WinUser.HPOWERNOTIFY hpowernotify = INSTANCE.RegisterPowerSettingNotification(
                new HWND(), WinNT.GUID_CONSOLE_DISPLAY_STATE, User32.DEVICE_NOTIFY_WINDOW_HANDLE);
        assertNotNull(hpowernotify);
        assertNotEquals(0, INSTANCE.UnregisterPowerSettingNotification(hpowernotify));
    }
}
