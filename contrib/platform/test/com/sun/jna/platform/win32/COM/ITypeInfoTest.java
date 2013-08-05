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
import com.sun.jna.platform.win32.OaIdl.HREFTYPEbyReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTbyReference;
import com.sun.jna.platform.win32.WinDef.WORDbyReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class ITypeInfoTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ITypeInfoTest.class);
    }

    public ITypeInfoTest() {
        Native.setProtected(true);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public int getTypeInfoCount() {
        return 0;
    }

    public ITypeInfo getTypeInfo() {
        return null;
    }

    public void testGetTypeAttr() {
    }

    public void testGetTypeComp() {
    }

    public void testGetFuncDesc() {
    }

    public void testGetVarDesc() {
    }

    public void testGetNames() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID(1);
        BSTR[] rgBstrNames = new BSTR[1];
        UINT cMaxNames = new UINT(1);
        UINTbyReference pcNames = new UINTbyReference();
        HRESULT hr = typeInfo.GetNames(memid, rgBstrNames, cMaxNames, pcNames);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("rgBstrNames: " + rgBstrNames[0].getValue());
        System.out.println("pcNames: " + pcNames.getValue().intValue());
    }

    public void testGetRefTypeOfImplType() {
        ITypeInfo typeInfo = getTypeInfo();
        HREFTYPEbyReference pRefType = new HREFTYPEbyReference();
        HRESULT hr = typeInfo.GetRefTypeOfImplType(new UINT(0), pRefType);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetRefTypeOfImplType: " + pRefType.toString());
    }

    public void testGetImplTypeFlags() {
        ITypeInfo typeInfo = getTypeInfo();
        IntByReference pImplTypeFlags = new IntByReference();
        HRESULT hr = typeInfo.GetImplTypeFlags(new UINT(0), pImplTypeFlags);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetImplTypeFlags: " + pImplTypeFlags.toString());
    }

    public void testGetIDsOfNames() {
        ITypeInfo typeInfo = getTypeInfo();
        LPOLESTR[] rgszNames = { new LPOLESTR("Help") };
        UINT cNames = new UINT(1);
        MEMBERID[] pMemId = new MEMBERID[1];
        HRESULT hr = typeInfo.GetIDsOfNames(rgszNames, cNames, pMemId);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("pMemId: " + pMemId.toString());
    }

    public void testInvoke() {
        fail("not implemented due complexity.");
    }

    public void testGetDocumentation() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID(0);
        BSTRByReference pBstrName = new BSTRByReference();
        BSTRByReference pBstrDocString = new BSTRByReference();
        DWORDbyReference pdwHelpContext = new DWORDbyReference();
        BSTRByReference pBstrHelpFile = new BSTRByReference();
        HRESULT hr = typeInfo.GetDocumentation(memid, pBstrName,
                pBstrDocString, pdwHelpContext, pBstrHelpFile);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("memid: " + memid.intValue());
        System.out.println("pBstrName: " + pBstrName.getValue());
        System.out.println("pBstrDocString: " + pBstrDocString.getValue());
        System.out.println("pdwHelpContext: " + pdwHelpContext.getValue());
        System.out.println("pBstrHelpFile: " + pBstrHelpFile.getValue());
    }

    public void testGetDllEntry() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID(0);
        BSTRByReference pBstrDllName = new BSTRByReference();
        BSTRByReference pBstrName = new BSTRByReference();
        WORDbyReference pwOrdinal = new WORDbyReference();
        HRESULT hr = typeInfo.GetDllEntry(memid, INVOKEKIND.INVOKE_FUNC,
                pBstrDllName, pBstrName, pwOrdinal);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("memid: " + memid.intValue());
        System.out.println("pBstrDllName: " + pBstrDllName.getValue());
        System.out.println("pBstrName: " + pBstrName.getValue());
        System.out.println("pwOrdinal: " + pwOrdinal.getValue());
    }

    public void testGetRefTypeInfo() {
    }

    public void testAddressOfMember() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID();
        PointerByReference ppv = new PointerByReference();
        HRESULT hr = typeInfo.AddressOfMember(memid, INVOKEKIND.INVOKE_FUNC,
                ppv);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("AddressOfMember: " + ppv.toString());
    }

    public void testCreateInstance() {
        fail("not implemented due complexity.");
    }

    public void testGetMops() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID(0);
        BSTRByReference pBstrMops = new BSTRByReference();
        HRESULT hr = typeInfo.GetMops(memid, pBstrMops);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("pBstrMops: " + pBstrMops.toString());
    }

    public void testGetContainingTypeLib() {
    }

    public void testReleaseTypeAttr() {
        fail("not implemented due complexity.");
    }

    public void testReleaseFuncDesc() {
        fail("not implemented due complexity.");
    }

    public void testReleaseVarDesc() {
        fail("not implemented due complexity.");
    }
}