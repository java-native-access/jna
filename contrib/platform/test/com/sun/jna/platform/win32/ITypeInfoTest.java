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
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
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

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ITypeInfoTest.class);
	}

	public ITypeInfoTest() {
		Native.setProtected(true);
	}

	public ITypeInfo getShellTypeInfo() {
		// create a shell COM object
		COMObject shellObj = new COMObject("Word.Application", false);
		// get user default lcid
		LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
		// create a IUnknown pointer
		PointerByReference ppTInfo = new PointerByReference();

		shellObj.getIDispatch().GetTypeInfo(new UINT(0), lcid, ppTInfo);

		return new ITypeInfo(ppTInfo.getValue());
	}

	public void testGetTypeAttr() {
		ITypeInfo typeInfo = getShellTypeInfo();
		TYPEATTR.ByReference pTypeAttr = new TYPEATTR.ByReference();
		HRESULT hr = typeInfo.GetTypeAttr(pTypeAttr);

		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());
		System.out.println("GetTypeAttr: " + pTypeAttr.toString(true));
	}

}
