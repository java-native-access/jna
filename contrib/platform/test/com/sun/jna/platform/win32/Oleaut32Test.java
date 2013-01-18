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
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.OleAut32.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Oleaut32Test extends TestCase {

	public static void main(String[] args) {
		Native.setProtected(true);
		junit.textui.TestRunner.run(Oleaut32Test.class);
	}

	public Oleaut32Test() {
		Native.setProtected(true);
	}

	public void testSysAllocString() {
		assertEquals(null, OleAut32.INSTANCE.SysAllocString(null));
		BSTR p = OleAut32.INSTANCE.SysAllocString("hello world");
		assertEquals("hello world", p.getValue());
		OleAut32.INSTANCE.SysFreeString(p);
	}

	public void testSysFreeString() {
		OleAut32.INSTANCE.SysFreeString(null);
	}

	public void testDISPPARAMS() {
		// Build DISPPARAMS
		SAFEARRAY.ByReference safeArg = OleAut32Util.createVarArray(1);
		OleAut32Util.SafeArrayPutElement(safeArg, 0, new VARIANT(
				Variant.VARIANT_TRUE));
		DISPPARAMS dp = new DISPPARAMS(safeArg, new DISPID(
				OleAut32.DISPATCH_PROPERTYPUT), 1, 1);

		System.out.println(safeArg.toString(true));
		System.out
				.println("\n\n\n----------------------------------------------\n\n\n");
		System.out.println(dp.toString(true));
	}
}
