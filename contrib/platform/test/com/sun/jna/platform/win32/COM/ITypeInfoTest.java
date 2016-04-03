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

import com.sun.jna.platform.win32.OaIdl.HREFTYPEByReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author dblock[at]dblock[dot]org
 */
public class ITypeInfoTest {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private ITypeInfo getTypeInfo() {
        TypeLibUtil shellTypeLib = new TypeLibUtil("{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}", 1, 0);
        int typeInfoCount = shellTypeLib.getTypeInfoCount();
        if (typeInfoCount == 0)
            throw new RuntimeException("Shell lib contains zero type infos");
        ITypeInfo typeInfo = shellTypeLib.getTypeInfo(18);
        return typeInfo;
    }

    private ITypeInfo[] getTypeInfos() {
        TypeLibUtil shellTypeLib = new TypeLibUtil("{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}", 1, 0);
        int typeInfoCount = shellTypeLib.getTypeInfoCount();
        if (typeInfoCount == 0)
            throw new RuntimeException("Shell lib contains zero type infos");
        ITypeInfo[] typeInfos = new ITypeInfo[typeInfoCount];
        for (int i = 0; i < typeInfoCount; i++) {
            typeInfos[i] = shellTypeLib.getTypeInfo(i);
        }
        return typeInfos;
    }

    public void testGetTypeAttr() {
    }

    public void testGetTypeComp() {
    }

    public void testGetFuncDesc() {
    }

    public void testGetVarDesc() {
    }

    @Test
    public void testGetNames() {
        ITypeInfo[] typeInfos = getTypeInfos();
        MEMBERID memid = new MEMBERID(1);
        BSTR[] rgBstrNames = new BSTR[1];
        UINT cMaxNames = new UINT(1);
        UINTByReference pcNames = new UINTByReference();
        for (ITypeInfo typeInfo : typeInfos) {
            HRESULT hr = typeInfo.GetNames(memid, rgBstrNames, cMaxNames, pcNames);
            if (COMUtils.SUCCEEDED(hr)) {
                //System.out.println("rgBstrNames: " + rgBstrNames[0].getValue());
                //System.out.println("pcNames: " + pcNames.getValue().intValue());
                return;
            }
        }
        throw new RuntimeException("Didn't find name for member in any of the type infos");
    }

    @Test
    public void testGetRefTypeOfImplType() {
        ITypeInfo typeInfo = getTypeInfo();
        HREFTYPEByReference pRefType = new HREFTYPEByReference();
        HRESULT hr = typeInfo.GetRefTypeOfImplType(new UINT(0), pRefType);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        //System.out.println("GetRefTypeOfImplType: " + pRefType.toString());
    }

    @Test
    public void testGetImplTypeFlags() {
        ITypeInfo typeInfo = getTypeInfo();
        IntByReference pImplTypeFlags = new IntByReference();
        HRESULT hr = typeInfo.GetImplTypeFlags(new UINT(0), pImplTypeFlags);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        //System.out.println("GetImplTypeFlags: " + pImplTypeFlags.toString());
    }

    @Test
    public void testGetIDsOfNames() {
        ITypeInfo[] typeInfos = getTypeInfos();
        LPOLESTR[] rgszNames = {new LPOLESTR("Help")};
        UINT cNames = new UINT(1);
        MEMBERID[] pMemId = new MEMBERID[1];
        for (ITypeInfo typeInfo : typeInfos) {
            HRESULT hr = typeInfo.GetIDsOfNames(rgszNames, cNames, pMemId);
            if (COMUtils.SUCCEEDED(hr)) {
                //System.out.println("pMemId: " + pMemId.toString());
                return;
            }
        }
        throw new RuntimeException("Didn't find Help in any of the type infos");
    }

    public void testInvoke() {
    	
    }

    @Test
    public void testGetDocumentation() {
        ITypeInfo[] typeInfos = getTypeInfos();
        MEMBERID memid = new MEMBERID(0);
        BSTRByReference pBstrName = new BSTRByReference();
        BSTRByReference pBstrDocString = new BSTRByReference();
        DWORDByReference pdwHelpContext = new DWORDByReference();
        BSTRByReference pBstrHelpFile = new BSTRByReference();
        for (ITypeInfo typeInfo : typeInfos) {
        HRESULT hr = typeInfo.GetDocumentation(memid, pBstrName,
                pBstrDocString, pdwHelpContext, pBstrHelpFile);
            if (COMUtils.SUCCEEDED(hr)) {
                //System.out.println("memid: " + memid.intValue());
                //System.out.println("pBstrName: " + pBstrName.getValue());
                //System.out.println("pBstrDocString: " + pBstrDocString.getValue());
                //System.out.println("pdwHelpContext: " + pdwHelpContext.getValue());
                //System.out.println("pBstrHelpFile: " + pBstrHelpFile.getValue());
                return;
            }
        }
        throw new RuntimeException("Didn't find documentation in any of the type infos");
    }

    @Test
    @Ignore("Needs a DLL that contains code")
    public void testGetDllEntry() {
        ITypeInfo[] typeInfos = getTypeInfos();
        MEMBERID memid = new MEMBERID(0);
        BSTRByReference pBstrDllName = new BSTRByReference();
        BSTRByReference pBstrName = new BSTRByReference();
        WORDByReference pwOrdinal = new WORDByReference();
        for (ITypeInfo typeInfo : typeInfos) {
            HRESULT hr = typeInfo.GetDllEntry(memid, INVOKEKIND.INVOKE_FUNC,
                    pBstrDllName, pBstrName, pwOrdinal);
            if (COMUtils.SUCCEEDED(hr)) {
                //System.out.println("memid: " + memid.intValue());
                //System.out.println("pBstrDllName: " + pBstrDllName.getValue());
                //System.out.println("pBstrName: " + pBstrName.getValue());
                //System.out.println("pwOrdinal: " + pwOrdinal.getValue());
                return;
            }
        }
        throw new RuntimeException("Didn't find Dll entry for member in any of the type infos");
    }

    public void testGetRefTypeInfo() {
    }

    @Test
    @Ignore("Needs a DLL that contains code")
    public void testAddressOfMember() {
        ITypeInfo[] typeInfos = getTypeInfos();
        MEMBERID memid = new MEMBERID();
        PointerByReference ppv = new PointerByReference();
        for (ITypeInfo typeInfo : typeInfos) {
            HRESULT hr = typeInfo.AddressOfMember(memid, INVOKEKIND.INVOKE_FUNC,
                    ppv);
            if (COMUtils.SUCCEEDED(hr)) {
                //System.out.println("AddressOfMember: " + ppv.toString());
                return;
            }
        }
        throw new RuntimeException("Didn't find address for function in any of the type infos");
    }

    public void testCreateInstance() {

    }

    @Test
    public void testGetMops() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID(0);
        BSTRByReference pBstrMops = new BSTRByReference();
        HRESULT hr = typeInfo.GetMops(memid, pBstrMops);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        //System.out.println("pBstrMops: " + pBstrMops.toString());
    }

    public void testGetContainingTypeLib() {
    }

    public void testReleaseTypeAttr() {

    }

    public void testReleaseFuncDesc() {

    }

    public void testReleaseVarDesc() {

    }
}