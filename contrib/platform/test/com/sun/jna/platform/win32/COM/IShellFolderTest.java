package com.sun.jna.platform.win32.COM;

/*
 * @author L W Ahonen, lwahonen@iki.fi
 */

import junit.framework.TestCase;


import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.PointerByReference;

public class IShellFolderTest extends TestCase {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }
        
    private IShellFolder psfMyComputer;

    public static WinNT.HRESULT BindToCsidl(int csidl, Guid.REFIID riid, PointerByReference ppv) {
        WinNT.HRESULT hr;
        PointerByReference pidl = new PointerByReference();
        hr = Shell32.INSTANCE.SHGetSpecialFolderLocation(null, csidl, pidl);
        assertTrue(COMUtils.SUCCEEDED(hr));
        PointerByReference psfDesktopPTR = new PointerByReference();
        hr = Shell32.INSTANCE.SHGetDesktopFolder(psfDesktopPTR);
        assertTrue(COMUtils.SUCCEEDED(hr));
        IShellFolder psfDesktop = IShellFolder.Converter.PointerToIShellFolder(psfDesktopPTR);
        short cb = pidl.getValue().getShort(0); // See http://blogs.msdn.com/b/oldnewthing/archive/2011/08/30/10202076.aspx for explanation about this bit
        if (cb != 0) {
            hr = psfDesktop.BindToObject(pidl.getValue(), null, riid, ppv);
        } else {
            hr = psfDesktop.QueryInterface(riid, ppv);
        }
        psfDesktop.Release();
        Ole32.INSTANCE.CoTaskMemFree(pidl.getValue());
        return hr;
    }

    public void setUp() throws Exception {
        int CSIDL_DRIVES = 0x0011;
        WinNT.HRESULT hr = Ole32.INSTANCE.CoInitialize(null);
        assertTrue(COMUtils.SUCCEEDED(hr));
        PointerByReference psfMyComputerPTR = new PointerByReference(Pointer.NULL);
        hr = BindToCsidl(CSIDL_DRIVES, new Guid.REFIID(IShellFolder.IID_ISHELLFOLDER), psfMyComputerPTR);
        assertTrue(COMUtils.SUCCEEDED(hr));
        psfMyComputer = IShellFolder.Converter.PointerToIShellFolder(psfMyComputerPTR);
    }

    public void tearDown() throws Exception {
        psfMyComputer.Release();
        Ole32.INSTANCE.CoUninitialize();
    }

    public void testEnumObjects() throws Exception {
        PointerByReference peidlPTR = new PointerByReference();
        int SHCONTF_FOLDERS = 0x20;
        int SHCONTF_NONFOLDERS = 0x40;
        boolean sawNames = false;

        WinNT.HRESULT hr = psfMyComputer.EnumObjects(null,
                SHCONTF_FOLDERS | SHCONTF_NONFOLDERS, peidlPTR);
        assertTrue(COMUtils.SUCCEEDED(hr));
        IEnumIDList peidl = IEnumIDList.Converter.PointerToIEnumIDList(peidlPTR);
        PointerByReference pidlItem = new PointerByReference();
        while (peidl.Next(1, pidlItem, null).intValue() == COMUtils.S_OK) {
            PointerByReference sr = new PointerByReference();
            hr = psfMyComputer.GetDisplayNameOf(pidlItem.getValue(), 0, sr);
            assertTrue(COMUtils.SUCCEEDED(hr));
            PointerByReference pszName = new PointerByReference();
            hr = Shlwapi.INSTANCE.StrRetToStr(sr, pidlItem.getValue(), pszName);
            assertTrue(COMUtils.SUCCEEDED(hr));
            String wideString = pszName.getValue().getWideString(0);
            if (wideString != null && wideString.length() > 0)
                sawNames = true;
            Ole32.INSTANCE.CoTaskMemFree(pszName.getValue());
            Ole32.INSTANCE.CoTaskMemFree(pidlItem.getValue());
        }
        peidl.Release();
        assertTrue(sawNames); // We should see at least one item with a name
    }
}