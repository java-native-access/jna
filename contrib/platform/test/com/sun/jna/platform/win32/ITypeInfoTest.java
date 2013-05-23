/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.HREFTYPE;
import com.sun.jna.platform.win32.OaIdl.HREFTYPEByReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.MEMBERIDByReference;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMObject;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.ITypeComp;
import com.sun.jna.platform.win32.COM.ITypeInfo;
import com.sun.jna.platform.win32.COM.ITypeLib;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class ITypeInfoTest extends TestCase {

    private COMObject comObj = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ITypeInfoTest.class);
    }

    @Override
	protected void setUp() throws Exception {
        if (this.comObj == null) {
            // create a shell COM object
            this.comObj = new COMObject("Shell.Application", false);
        }
    }

    @Override
	protected void tearDown() throws Exception {
        if (this.comObj != null) {
            this.comObj.release();
        }
    }

    public ITypeInfo getTypeInfo() {
        // get user default lcid
        LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
        // create a IUnknown pointer
        PointerByReference ppTInfo = new PointerByReference();

        comObj.getIDispatch().GetTypeInfo(new UINT(0), lcid, ppTInfo);

        return new ITypeInfo(ppTInfo.getValue());
    }

    public void testGetTypeAttr() {
        ITypeInfo typeInfo = getTypeInfo();
        TYPEATTR.ByReference pTypeAttr = new TYPEATTR.ByReference();
        HRESULT hr = typeInfo.GetTypeAttr(pTypeAttr);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetTypeAttr: " + pTypeAttr.toString(true));
    }

    public void testGetTypeComp() {
        ITypeInfo typeInfo = getTypeInfo();
        ITypeComp.ByReference pTComp = new ITypeComp.ByReference();
        HRESULT hr = typeInfo.GetTypeComp(pTComp);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetTypeComp: " + pTComp.toString());
    }

    public void testGetFuncDesc() {
        ITypeInfo typeInfo = getTypeInfo();
        FUNCDESC.ByReference pFuncDesc = new FUNCDESC.ByReference();
        HRESULT hr = typeInfo.GetFuncDesc(new UINT(1), pFuncDesc);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetFuncDesc: " + pFuncDesc.toString(true));
    }

    public void testGetVarDesc() {
        ITypeInfo typeInfo = getTypeInfo();
        VARDESC.ByReference pVarDesc = new VARDESC.ByReference();
        HRESULT hr = typeInfo.GetVarDesc(new UINT(0), pVarDesc);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetVarDesc: " + pVarDesc.toString());
    }

    public void testGetNames() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID(1);
        BSTR[] rgBstrNames = new BSTR[1];
        UINT cMaxNames = new UINT(1);
        UINTByReference pcNames = new UINTByReference();
        HRESULT hr = typeInfo.GetNames(memid, rgBstrNames, cMaxNames, pcNames);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("rgBstrNames: " + rgBstrNames[0].getValue());
        System.out.println("pcNames: " + pcNames.getValue().intValue());
    }

    public void testGetRefTypeOfImplType() {
        ITypeInfo typeInfo = getTypeInfo();
        HREFTYPEByReference pRefType = new HREFTYPEByReference();
        HRESULT hr = typeInfo.GetRefTypeOfImplType(new UINT(0), pRefType);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetRefTypeOfImplType: " + pRefType.toString());
    }

    public void testGetImplTypeFlags() {
        ITypeInfo typeInfo = getTypeInfo();
        IntByReference pImplTypeFlags = new IntByReference();
        HRESULT hr = typeInfo.GetImplTypeFlags(new UINT(0), pImplTypeFlags);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetImplTypeFlags: " + pImplTypeFlags.toString());
    }

    public void testGetIDsOfNames() {
        ITypeInfo typeInfo = getTypeInfo();
        WString[] rgszNames = { new WString("Visible") };
        UINT cNames = new UINT(1);
        MEMBERID[] pMemId = new MEMBERID[1];
        HRESULT hr = typeInfo.GetIDsOfNames(rgszNames, cNames, pMemId);

        COMUtils.checkAutoRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("pMemId: " + pMemId.toString());
    }

    public void testInvoke() {
        fail("Test not implemented");
    }

    public void testGetDocumentation() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID(0);
        BSTR pBstrName = new BSTR();
        BSTR pBstrDocString = new BSTR();
        DWORDByReference pdwHelpContext = new DWORDByReference();
        BSTR pBstrHelpFile = new BSTR();
        HRESULT hr = typeInfo.GetDocumentation(memid, pBstrName,
                                               pBstrDocString, pdwHelpContext, pBstrHelpFile);

        COMUtils.checkAutoRC(hr);
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
        BSTR pBstrDllName = new BSTR();
        BSTR pBstrName = new BSTR();
        WORDByReference pwOrdinal = new WORDByReference();
        HRESULT hr = typeInfo.GetDllEntry(memid, INVOKEKIND.INVOKE_FUNC,
                                          pBstrDllName, pBstrName, pwOrdinal);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("memid: " + memid.intValue());
        System.out.println("pBstrDllName: " + pBstrDllName.getValue());
        System.out.println("pBstrName: " + pBstrName.getValue());
        System.out.println("pwOrdinal: " + pwOrdinal.getValue());
    }

    public void testGetRefTypeInfo() {
        ITypeInfo typeInfo = getTypeInfo();
        HREFTYPE hRefType = new HREFTYPE();
        ITypeInfo.ByReference ppTInfo = new ITypeInfo.ByReference();
        HRESULT hr = typeInfo.GetRefTypeInfo(hRefType, ppTInfo);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("GetRefTypeInfo: " + ppTInfo.toString());
    }

    public void testAddressOfMember() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID();
        PointerByReference ppv = new PointerByReference();
        HRESULT hr = typeInfo.AddressOfMember(memid, INVOKEKIND.INVOKE_FUNC,
                                              ppv);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("AddressOfMember: " + ppv.toString());
    }

    public void testCreateInstance() {
        fail("Test not implemented");
    }

    public void testGetMops() {
        ITypeInfo typeInfo = getTypeInfo();
        MEMBERID memid = new MEMBERID(0);
        BSTR pBstrMops = new BSTR();
        HRESULT hr = typeInfo.GetMops(memid, pBstrMops);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("pBstrMops: " + pBstrMops.toString());
    }

    public void testGetContainingTypeLib() {
        ITypeInfo typeInfo = getTypeInfo();
        ITypeLib.ByReference pTLib = new ITypeLib.ByReference();
        UINTByReference pIndex = new UINTByReference();
        HRESULT hr = typeInfo.GetContainingTypeLib(pTLib, pIndex);

        COMUtils.checkTypeLibRC(hr);
        assertEquals(0, hr.intValue());
        System.out.println("pTLib: " + pTLib.toString());
        System.out.println("pTLib: " + pIndex.toString());
    }

    public void testReleaseTypeAttr() {
        fail("Test not implemented");
    }

    public void testReleaseFuncDesc() {
        fail("Test not implemented");
    }

    public void testReleaseVarDesc() {
        fail("Test not implemented");
    }
}
