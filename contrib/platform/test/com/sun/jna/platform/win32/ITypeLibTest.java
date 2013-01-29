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
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OAIdl.TLIBATTR;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.ITypeComp;
import com.sun.jna.platform.win32.COM.ITypeInfo;
import com.sun.jna.platform.win32.COM.ITypeLib;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class ITypeLibTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ITypeLibTest.class);
	}

	public ITypeLibTest() {
		Native.setProtected(true);
	}

	public ITypeLib loadShellTypeLib() {
		// Microsoft Shell Controls And Automation
		CLSID.ByReference clsid = new CLSID.ByReference();
		// get CLSID from string
		HRESULT hr = Ole32.INSTANCE.CLSIDFromString(new WString(
				"{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}"), clsid);
		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());

		// get user default lcid
		LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
		PointerByReference pShellTypeLib = new PointerByReference();
		// load typelib
		hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, 1, 0, lcid, pShellTypeLib);
		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());

		return new ITypeLib(pShellTypeLib.getValue());
	}

	public void testGetTypeInfoCount() {
		ITypeLib shellTypeLib = loadShellTypeLib();
		UINT typeInfoCount = shellTypeLib.GetTypeInfoCount();
		System.out.println("getTypeInfoCount: " + typeInfoCount);
	}

	public void testGetTypeInfo() {
		ITypeLib shellTypeLib = loadShellTypeLib();

		ITypeInfo.ByReference ppTInfo = new ITypeInfo.ByReference();
		HRESULT hr = shellTypeLib.GetTypeInfo(new UINT(0), ppTInfo);

		// System.out.println(ppTInfo.);

		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());
	}

	public void testGetTypeInfoType() {
		ITypeLib shellTypeLib = loadShellTypeLib();

		IntByReference pTKind = new IntByReference();
		HRESULT hr = shellTypeLib.GetTypeInfoType(new UINT(0), pTKind);

		System.out.println("TYPEKIND: " + pTKind);

		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());
	}

	public void testGetTypeInfoOfGuid() {
		ITypeLib shellTypeLib = loadShellTypeLib();

		GUID shellGuid = new GUID("{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}");
		ITypeInfo.ByReference ppTInfo = new ITypeInfo.ByReference();
		HRESULT hr = shellTypeLib.GetTypeInfoOfGuid(shellGuid, ppTInfo);

		//System.out.println("TYPEKIND: " + ppTInfo.);

		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());
	}

	public void testGetLibAttr() {
		ITypeLib shellTypeLib = loadShellTypeLib();

		TLIBATTR.ByReference ppTLibAttr = new TLIBATTR.ByReference();
		HRESULT hr = shellTypeLib.GetLibAttr(ppTLibAttr);

		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());
		System.out.println("ppTLibAttr: " + ppTLibAttr.toString());
	}	
	
	public void testGetTypeComp() {
		ITypeLib shellTypeLib = loadShellTypeLib();

		ITypeComp.ByReference ppTComp = new ITypeComp.ByReference();
		HRESULT hr = shellTypeLib.GetTypeComp(ppTComp);

		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());
		System.out.println("ppTComp: " + ppTComp.toString());
	}		
}
