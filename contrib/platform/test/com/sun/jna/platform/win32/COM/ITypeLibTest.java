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

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
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

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ITypeLibTest.class);
    }

    public ITypeLibTest() {
    }

    private ITypeLib loadShellTypeLib() {
        // Microsoft Shell Controls And Automation
        CLSID.ByReference clsid = new CLSID.ByReference();
        // get CLSID from string
        HRESULT hr = Ole32.INSTANCE.CLSIDFromString(new WString(
                "{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}"), clsid);
        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());

        // get user default lcid
        LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
        // create a IUnknown pointer
        PointerByReference pShellTypeLib = new PointerByReference();
        // load typelib
        hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, 1, 0, lcid, pShellTypeLib);
        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());

        return new TypeLib(pShellTypeLib.getValue());
    }

    public void testGetTypeInfoCount() {
        ITypeLib shellTypeLib = loadShellTypeLib();
        UINT typeInfoCount = shellTypeLib.GetTypeInfoCount();
        System.out.println("GetTypeInfoCount: " + typeInfoCount);
    }

    public void testGetTypeInfo() {
         ITypeLib shellTypeLib = loadShellTypeLib();
        
         PointerByReference ppTInfo = new PointerByReference();
         HRESULT hr = shellTypeLib.GetTypeInfo(new UINT(0), ppTInfo);
        
         COMUtils.checkRC(hr);
         assertEquals(0, hr.intValue());
         System.out.println("ITypeInfo: " + ppTInfo.toString());
    }

    public void testGetTypeInfoType() {
        ITypeLib shellTypeLib = loadShellTypeLib();

        TYPEKIND.ByReference pTKind = new TYPEKIND.ByReference();
        HRESULT hr = shellTypeLib.GetTypeInfoType(new UINT(0), pTKind);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("TYPEKIND: " + pTKind);
    }

    public void testGetTypeInfoOfGuid() {
        // ITypeLib shellTypeLib = loadShellTypeLib();
        //
        // GUID shellGuid = new GUID("{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}");
        // TypeInfo.ByReference pTInfo = new TypeInfo.ByReference();
        // HRESULT hr = shellTypeLib.GetTypeInfoOfGuid(shellGuid, pTInfo);
        //
        // COMUtils.checkRC(hr);
        // assertEquals(0, hr.intValue());
        // System.out.println("ITypeInfo: " + pTInfo.toString());
    }

    public void testGetLibAttr() {
        // ITypeLib shellTypeLib = loadShellTypeLib();
        //
        // TLIBATTR.ByReference ppTLibAttr = new TLIBATTR.ByReference();
        // HRESULT hr = shellTypeLib.GetLibAttr(ppTLibAttr);
        //
        // COMUtils.checkRC(hr);
        // assertEquals(0, hr.intValue());
        // System.out.println("ppTLibAttr: " + ppTLibAttr.toString());
    }

    public void testGetTypeComp() {
        // ITypeLib shellTypeLib = loadShellTypeLib();
        //
        // TypeComp.ByReference pTComp = new TypeComp.ByReference();
        // HRESULT hr = shellTypeLib.GetTypeComp(pTComp);
        //
        // COMUtils.checkRC(hr);
        // assertEquals(0, hr.intValue());
        // System.out.println("pTComp: " + pTComp.toString());
    }

    public void testFindName() {
		ITypeLib shellTypeLib = loadShellTypeLib();
		BSTRByReference szNameBuf = new BSTRByReference(OleAuto.INSTANCE.SysAllocString("Application"));
		ULONG lHashVal = new ULONG(0);
		USHORTByReference pcFound = new USHORTByReference((short)20);

		HRESULT hr = shellTypeLib.FindName(szNameBuf, lHashVal, null, null, pcFound);

		COMUtils.checkRC(hr);
		System.out.println("szNameBuf: " + szNameBuf);
	}
}
