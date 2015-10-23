package com.sun.jna.platform.win32.COM;

/*
 * Copyright (c) 2015 L W Ahonen, All Rights Reserved
 *
 */


import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.PointerByReference;
import junit.framework.TestCase;

public class IShellFolderTest extends TestCase {

    private IShellFolder psfMyComputer;

    public static WinNT.HRESULT BindToCsidl(int csidl, Guid.REFIID riid, PointerByReference ppv) {
        WinNT.HRESULT hr;
        PointerByReference pidl = new PointerByReference();
        hr = Shell32.INSTANCE.SHGetSpecialFolderLocation(null, csidl, pidl);
        if (COMUtils.SUCCEEDED(hr)) {
            PointerByReference psfDesktopPTR = new PointerByReference();
            hr = Shell32.INSTANCE.SHGetDesktopFolder(psfDesktopPTR);
            if (COMUtils.SUCCEEDED(hr)) {
                IShellFolder psfDesktop = IShellFolder.Converter.PointerToIShellFolder(psfDesktopPTR);
                short cb = pidl.getValue().getShort(0);
                if (cb != 0) {
                    hr = psfDesktop.BindToObject(pidl.getValue(), null, riid, ppv);
                } else {
                    hr = psfDesktop.QueryInterface(riid, ppv);
                }
                psfDesktop.Release();
            }
            Ole32.INSTANCE.CoTaskMemFree(pidl.getValue());
        }
        return hr;
    }

    public void setUp() throws Exception {
        Ole32.INSTANCE.CoInitialize(null);
        int CSIDL_DRIVES = 0x0011;
        WinNT.HRESULT hr = Ole32.INSTANCE.CoInitialize(null);
        if (COMUtils.SUCCEEDED(hr)) {
            PointerByReference psfMyComputerPTR = new PointerByReference(Pointer.NULL);
            hr = BindToCsidl(CSIDL_DRIVES, new Guid.REFIID(IShellFolder.IID_ISHELLFOLDER), psfMyComputerPTR);
            if (COMUtils.SUCCEEDED(hr)) {
                psfMyComputer = IShellFolder.Converter.PointerToIShellFolder(psfMyComputerPTR);
            }
        }
        if(psfMyComputer == null)
            throw new RuntimeException("Unable to create my computer shell object");
    }

    public void tearDown() throws Exception {
        psfMyComputer.Release();
        Ole32.INSTANCE.CoUninitialize();
    }

    public void testEnumObjects() throws Exception {
        PointerByReference peidlPTR = new PointerByReference();
        int SHCONTF_FOLDERS = 0x20;
        int SHCONTF_NONFOLDERS = 0x40;
        boolean sawNames=false;

        WinNT.HRESULT hr = psfMyComputer.EnumObjects(null,
                SHCONTF_FOLDERS | SHCONTF_NONFOLDERS, peidlPTR);
        if (COMUtils.SUCCEEDED(hr)) {
            IEnumIDList peidl = IEnumIDList.Converter.PointerToIEnumIDList(peidlPTR);
            PointerByReference pidlItem = new PointerByReference();
            while (peidl.Next(1, pidlItem, null).intValue() == COMUtils.S_OK) {
                PointerByReference sr = new PointerByReference();
                hr = psfMyComputer.GetDisplayNameOf(pidlItem.getValue(), 0, sr);
                if (COMUtils.SUCCEEDED(hr)) {
                    PointerByReference pszName = new PointerByReference();
                    hr = Shlwapi.INSTANCE.StrRetToStr(sr, pidlItem.getValue(), pszName);
                    if (COMUtils.SUCCEEDED(hr)) {
                        String wideString = pszName.getValue().getWideString(0);
                        if (wideString != null && wideString.length() > 0)
                            sawNames = true;
                        Ole32.INSTANCE.CoTaskMemFree(pszName.getValue());
                    }
                    Ole32.INSTANCE.CoTaskMemFree(sr.getValue());
                }
                Ole32.INSTANCE.CoTaskMemFree(pidlItem.getValue());
            }
            peidl.Release();
        }
        assertTrue(sawNames);
    }
}