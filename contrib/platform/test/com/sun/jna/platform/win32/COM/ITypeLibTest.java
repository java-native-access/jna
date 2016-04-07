/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import junit.framework.TestCase;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORTByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class ITypeLibTest extends TestCase {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }
    
    // Microsoft Shell Controls And Automation
    private static final String SHELL_CLSID = "{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}";
    // Version 1.0
    private static final int SHELL_MAJOR = 1;
    private static final int SHELL_MINOR = 0;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ITypeLibTest.class);
    }

    public ITypeLibTest() {
    }

    private ITypeLib loadShellTypeLib() {
        CLSID.ByReference clsid = new CLSID.ByReference();
        // get CLSID from string
        HRESULT hr = Ole32.INSTANCE.CLSIDFromString(new WString(SHELL_CLSID), clsid);
        assertTrue(COMUtils.SUCCEEDED(hr));

        // get user default lcid
        LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();

        PointerByReference pShellTypeLib = new PointerByReference();
        // load typelib
        hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, SHELL_MAJOR, SHELL_MINOR, lcid, pShellTypeLib);
        
        assertTrue(COMUtils.SUCCEEDED(hr));

        return new TypeLib(pShellTypeLib.getValue());
    }

    public void testGetTypeInfoCount() {
        ITypeLib shellTypeLib = loadShellTypeLib();
        UINT typeInfoCount = shellTypeLib.GetTypeInfoCount();
        assertEquals(38, typeInfoCount.intValue());
    }

    public void testGetTypeInfo() {
        ITypeLib shellTypeLib = loadShellTypeLib();
        
        PointerByReference ppTInfo = new PointerByReference();
        HRESULT hr = shellTypeLib.GetTypeInfo(new UINT(0), ppTInfo);
        
        assertTrue(COMUtils.SUCCEEDED(hr));
        
        //System.out.println("ITypeInfo: " + ppTInfo.toString());
    }

    public void testGetTypeInfoType() {
        ITypeLib shellTypeLib = loadShellTypeLib();

        TYPEKIND.ByReference pTKind = new TYPEKIND.ByReference();
        HRESULT hr = shellTypeLib.GetTypeInfoType(new UINT(0), pTKind);

        assertTrue(COMUtils.SUCCEEDED(hr));
        
        //System.out.println("TYPEKIND: " + pTKind);
    }

    public void testGetTypeInfoOfGuid() {
         ITypeLib shellTypeLib = loadShellTypeLib();
        
         // GUID for dispinterface IFolderViewOC
         GUID iFolderViewOC = new GUID("{9BA05970-F6A8-11CF-A442-00A0C90A8F39}");
         PointerByReference pbr = new PointerByReference();
         HRESULT hr = shellTypeLib.GetTypeInfoOfGuid(iFolderViewOC, pbr);
        
         assertTrue(COMUtils.SUCCEEDED(hr));
    }

    public void testLibAttr() {
         ITypeLib shellTypeLib = loadShellTypeLib();
        
         PointerByReference pbr = new PointerByReference();
         HRESULT hr = shellTypeLib.GetLibAttr(pbr);
         
         assertTrue(COMUtils.SUCCEEDED(hr));
         
         OaIdl.TLIBATTR tlibAttr = new OaIdl.TLIBATTR(pbr.getValue());
        
         assertEquals(SHELL_CLSID, tlibAttr.guid.toGuidString());
         assertEquals(SHELL_MAJOR, tlibAttr.wMajorVerNum.intValue());
         assertEquals(SHELL_MINOR, tlibAttr.wMinorVerNum.intValue());
         
         shellTypeLib.ReleaseTLibAttr(tlibAttr);
    }

    public void testGetTypeComp() {
        ITypeLib shellTypeLib = loadShellTypeLib();

        PointerByReference pbr = new PointerByReference();
        HRESULT hr = shellTypeLib.GetTypeComp(pbr);

        // Only check that call works
        assertTrue(COMUtils.SUCCEEDED(hr));
    }

    public void testIsName() {
        ITypeLib shellTypeLib = loadShellTypeLib();

        String memberValue = "Folder";
        Pointer p = Ole32.INSTANCE.CoTaskMemAlloc((memberValue.length() + 1L) * Native.WCHAR_SIZE);
        WTypes.LPOLESTR olestr = new WTypes.LPOLESTR(p);
        olestr.setValue(memberValue);
        
        WinDef.BOOLByReference boolByRef = new WinDef.BOOLByReference();
        
        HRESULT hr = shellTypeLib.IsName(olestr, new ULONG(0), boolByRef);
        assertTrue(COMUtils.SUCCEEDED(hr));
        
        // Folder is a member
        assertTrue(boolByRef.getValue().booleanValue());
        
        Ole32.INSTANCE.CoTaskMemFree(p);
    }
    
    public void testFindName() {
        ITypeLib shellTypeLib = loadShellTypeLib();
        
        // The found member is Count, search done with lowercase value to test
        // correct behaviour (search is case insensitive)
        String memberValue = "count";
        String memberValueOk = "Count";
        Pointer p = Ole32.INSTANCE.CoTaskMemAlloc((memberValue.length() + 1L) * Native.WCHAR_SIZE);
        WTypes.LPOLESTR olestr = new WTypes.LPOLESTR(p);
        olestr.setValue(memberValue);
        
        short maxResults = 100;
        
        ULONG lHashVal = new ULONG(0);
        USHORTByReference pcFound = new USHORTByReference(maxResults);
        Pointer[] pointers = new Pointer[maxResults];
        MEMBERID[] rgMemId = new MEMBERID[maxResults];
        
        HRESULT hr = shellTypeLib.FindName(olestr, lHashVal, pointers, rgMemId, pcFound);
        assertTrue(COMUtils.SUCCEEDED(hr));
                
        // If a reader can come up with more tests it would be appretiated,
        // the documentation is unclear what more can be expected
        
        // 2 matches come from manual tests
        assertTrue(pcFound.getValue().intValue() == 2);
        // Check that function return corrected member name (Count) - see uppercase C
        assertEquals(memberValueOk, olestr.getValue());
        
        // There have to be as many pointers as reported by pcFound
        assertNotNull(pointers[0]);
        assertNotNull(pointers[1]);
        assertNull(pointers[2]); // Might be flaky, contract only defined positions 0 -> (pcFound - 1)
        
        // Test access to second value
        TypeInfo secondTypeInfo = new TypeInfo(pointers[1]);
        
        PointerByReference pbr = new PointerByReference();
        hr = secondTypeInfo.GetTypeAttr(pbr);
        assertTrue(COMUtils.SUCCEEDED(hr));
        OaIdl.TYPEATTR pTypeAttr = new OaIdl.TYPEATTR(pbr.getValue());
        
        // Either interface FolderItemVerbs ({1F8352C0-50B0-11CF-960C-0080C7F4EE85})
        // or FolderItems ({744129E0-CBE5-11CE-8350-444553540000})
        String typeGUID = pTypeAttr.guid.toGuidString();
        
        assertTrue(typeGUID.equals("{1F8352C0-50B0-11CF-960C-0080C7F4EE85}") ||
                typeGUID.equals("{744129E0-CBE5-11CE-8350-444553540000}"));
                
        secondTypeInfo.ReleaseTypeAttr(pTypeAttr);
        
        Ole32.INSTANCE.CoTaskMemFree(olestr.getPointer());
    }
}
