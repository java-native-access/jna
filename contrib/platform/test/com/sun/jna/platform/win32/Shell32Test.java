/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.ShellAPI.APPBARDATA;
import com.sun.jna.platform.win32.ShellAPI.SHELLEXECUTEINFO;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 * @author markus[at]headcrashing[dot]eu
 */
public class Shell32Test extends TestCase {

    // avoid disrupting the screen _too_ much
    private static final int RESIZE_DELTA = 10;
    private static final int WM_USER = 0x0400;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Shell32Test.class);
    }

    public void testSHGetFolderPath() {
        char[] pszPath = new char[WinDef.MAX_PATH];
        assertEquals("Failed to retrieve path", W32Errors.S_OK,
                Shell32.INSTANCE.SHGetFolderPath(null, ShlObj.CSIDL_PROGRAM_FILES, null, ShlObj.SHGFP_TYPE_CURRENT, pszPath));
        assertTrue("Empty path", Native.toString(pszPath).length() > 0);
    }

    public void testSHGetDesktopFolder() {
        PointerByReference ppshf = new PointerByReference();
        WinNT.HRESULT hr = Shell32.INSTANCE.SHGetDesktopFolder(ppshf);
        assertTrue("Failed to get folder: " + hr.intValue(), W32Errors.SUCCEEDED(hr.intValue()));
        assertTrue("No folder value", ppshf.getValue() != null);
        // should release the interface, but we need Com4JNA to do that.
    }

    public final void testSHGetSpecialFolderPath() {
        final char[] pszPath = new char[WinDef.MAX_PATH];
        assertTrue("SHGetSpecialFolderPath", Shell32.INSTANCE.SHGetSpecialFolderPath(null, pszPath, ShlObj.CSIDL_APPDATA, false));
        assertFalse("No path", Native.toString(pszPath).isEmpty());
    }


    private void newAppBar() {
        APPBARDATA data = new APPBARDATA.ByReference();
        data.cbSize.setValue(data.size());
        data.uCallbackMessage.setValue(WM_USER + 1);

        UINT_PTR result = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_NEW), data);
        assertNotNull(result);
    }

    private void removeAppBar() {
        APPBARDATA data = new APPBARDATA.ByReference();
        data.cbSize.setValue(data.size());
        UINT_PTR result = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_REMOVE), data);
        assertNotNull(result);

    }

    private void queryPos(APPBARDATA data) {
        UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_QUERYPOS), data);

        assertNotNull(h);
        assertTrue(h.intValue() > 0);

    }

    public void testResizeDesktopFromBottom() throws InterruptedException {

        newAppBar();

        APPBARDATA data = new APPBARDATA.ByReference();

        data.uEdge.setValue(ShellAPI.ABE_BOTTOM);
        data.rc.top = User32.INSTANCE.GetSystemMetrics(User32.SM_CYFULLSCREEN) - RESIZE_DELTA;
        data.rc.left = 0;
        data.rc.bottom = User32.INSTANCE.GetSystemMetrics(User32.SM_CYFULLSCREEN);
        data.rc.right = User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN);

        queryPos(data);

        UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_SETPOS), data);

        assertNotNull(h);
        assertTrue(h.intValue() >= 0);

        removeAppBar();
    }

    public void testResizeDesktopFromTop() throws InterruptedException {

        newAppBar();

        APPBARDATA data = new APPBARDATA.ByReference();
        data.uEdge.setValue(ShellAPI.ABE_TOP);
        data.rc.top = 0;
        data.rc.left = 0;
        data.rc.bottom = User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN) - RESIZE_DELTA;
        data.rc.right = User32.INSTANCE.GetSystemMetrics(User32.SM_CXFULLSCREEN);

        queryPos(data);

        UINT_PTR h = Shell32.INSTANCE.SHAppBarMessage(new DWORD(ShellAPI.ABM_SETPOS), data);

        assertNotNull(h);
        assertTrue(h.intValue() >= 0);

        removeAppBar();

    }

    public void testSHGetKnownFolderPath() {
        int flags = ShlObj.KNOWN_FOLDER_FLAG.NONE.getFlag();
        PointerByReference outPath = new PointerByReference();
        HANDLE token = null;
        GUID guid = KnownFolders.FOLDERID_Fonts;
        HRESULT hr = Shell32.INSTANCE.SHGetKnownFolderPath(guid, flags, token, outPath);

        Ole32.INSTANCE.CoTaskMemFree(outPath.getValue());

        assertTrue(W32Errors.SUCCEEDED(hr.intValue()));
    }

    public void testSHEmptyRecycleBin() {
        File file = new File(System.getProperty("java.io.tmpdir"), System.nanoTime() + ".txt");
        try {
            // Create a file and immediately send it to the recycle bin.
            try {
                fillTempFile(file);
                W32FileUtils.getInstance().moveToTrash(new File[] { file });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int result = Shell32.INSTANCE.SHEmptyRecycleBin(null, null,
                                                            Shell32.SHERB_NOCONFIRMATION | Shell32.SHERB_NOPROGRESSUI | Shell32.SHERB_NOSOUND);
            // for reasons I can not find documented on MSDN,
            // the function returns the following:
            // 0 when the recycle bin has items in it
            // -2147418113 when the recycle bin has no items in it
            assertEquals("Result should have been ERROR_SUCCESS when emptying Recycle Bin - there should have been a file in it.",
                         W32Errors.ERROR_SUCCESS, result);
        } finally {
            // if the file wasn't sent to the recycle bin, delete it.
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void testShellExecuteEx() {
        File file = new File(System.getProperty("java.io.tmpdir"), System.nanoTime() + ".txt");
        try {
            try {
                fillTempFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            SHELLEXECUTEINFO lpExecInfo = new SHELLEXECUTEINFO();
            // to avoid opening something and having hProcess come up null
            // (meaning we opened something but can't close it)
            // we will do a negative test with a bogus action.
            lpExecInfo.lpVerb = "0p3n";
            lpExecInfo.nShow = User32.SW_SHOWDEFAULT;
            lpExecInfo.fMask = Shell32.SEE_MASK_NOCLOSEPROCESS | Shell32.SEE_MASK_FLAG_NO_UI;
            lpExecInfo.lpFile = file.getAbsolutePath();

            assertFalse("ShellExecuteEx should have returned false - action verb was bogus.",
                        Shell32.INSTANCE.ShellExecuteEx(lpExecInfo));
            assertEquals("GetLastError() should have been set to ERROR_NO_ASSOCIATION because of bogus action",
                         W32Errors.ERROR_NO_ASSOCIATION, Native.getLastError());
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }

    }

    /**
     * Creates (if needed) and fills the specified file with some content (10 lines of the same text)
     *
     * @param file
     *            The file to fill with content
     * @throws IOException
     *             If writing the content fails
     */
    private void fillTempFile(File file) throws IOException {
        file.createNewFile();
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (int i = 0; i < 10; i++) {
                fileWriter.write("Sample line of text");
                fileWriter.write(System.getProperty("line.separator"));
            }
        }
    }

    public void testExtractIconEx() {
        String winDir = Kernel32Util.getEnvironmentVariable("WINDIR");
        assertNotNull("No WINDIR value returned", winDir);
        assertTrue("Specified WINDIR does not exist: " + winDir, new File(winDir).exists());

        int iconCount = Shell32.INSTANCE.ExtractIconEx(new File(winDir, "explorer.exe").getAbsolutePath(), -1, null, null, 1);
        assertTrue("Should be at least two icons in explorer.exe", iconCount > 1);
    }

    public void testCurrentProcessExplicitAppUserModelID() {
        String appUserModelID = "com.sun.jna.platform.win32.Shell32Test";

        HRESULT r1 = Shell32.INSTANCE.SetCurrentProcessExplicitAppUserModelID(new WString(appUserModelID));
        assertEquals(WinError.S_OK, r1);

        PointerByReference ppszAppID = new PointerByReference();
        HRESULT r2 = Shell32.INSTANCE.GetCurrentProcessExplicitAppUserModelID(ppszAppID);
        assertEquals(WinError.S_OK, r2);

        assertEquals(appUserModelID, ppszAppID.getValue().getWideString(0));

        Ole32.INSTANCE.CoTaskMemFree(ppszAppID.getValue());
    }

    public void testCommandLineToArgvW() {
        WString cl = new WString("\"foo bar\" baz");
        String[] argv = { "foo bar", "baz" };
        IntByReference nargs = new IntByReference();
        Pointer strArr = Shell32.INSTANCE.CommandLineToArgvW(cl, nargs);
        assertNotNull(strArr);
        try {
            assertArrayEquals(argv, strArr.getWideStringArray(0, nargs.getValue()));
        } finally {
            Kernel32.INSTANCE.LocalFree(strArr);
        }
    }
}
