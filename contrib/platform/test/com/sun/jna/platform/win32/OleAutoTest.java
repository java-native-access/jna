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
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class OleAutoTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(OleAutoTest.class);
	}

	public void testSysAllocString() {
		assertEquals(null, OleAuto.INSTANCE.SysAllocString(null));
		BSTR p = OleAuto.INSTANCE.SysAllocString("hello world");
		assertEquals("hello world", p.getValue());
		OleAuto.INSTANCE.SysFreeString(p);
	}

	public void testSysFreeString() {
		OleAuto.INSTANCE.SysFreeString(null);
	}

	public void testDISPPARAMS() {
		// Build DISPPARAMS
		SAFEARRAY safeArg = OleAutoUtil.createVarArray(1);
		OleAutoUtil.SafeArrayPutElement(safeArg, 0, new VARIANT(
				Variant.VARIANT_TRUE));
		System.out.println(safeArg.toString(true));
	}

	public void testLoadRegTypeLib() {
		// MS Word typelib guid
		CLSID clsid = new CLSID();
		// get CLSID from string
		HRESULT hr = Ole32.INSTANCE.CLSIDFromString(new WString(
				"{00020905-0000-0000-C000-000000000046}"), clsid);
		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());

		// get user default lcid
		LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
		PointerByReference pWordTypeLib = new PointerByReference();
		// get typelib based on Word 8.3 (v11)
		hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, 8, 3, lcid, pWordTypeLib);
		COMUtils.checkTypeLibRC(hr);
		assertEquals(0, hr.intValue());
	}

}
