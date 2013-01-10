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

import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.COM.COMException;

/**
 * @author dblock[at]dblock[dot]org
 */
public class OleAut32UtilTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(OleAut32UtilTest.class);
	}

	public void testCreateVarArray() {
		SAFEARRAY varArray = OleAut32Util.createVarArray(10);
		assertTrue(varArray != null);
	}

	public void testSafeArrayPutGetElement() {
		SAFEARRAY varArray = OleAut32Util.createVarArray(10);

		for (int i = 0; i < 10; i++) {
			try {
				VARIANT variant = new VARIANT(new BSTR("TEST_" + i));
				System.out.println(variant.toString(true));
				OleAut32Util.SafeArrayPutElement(varArray, i, variant);
			} catch (COMException e) {
				e.printStackTrace();
			}
		}

		assertTrue(varArray != null);

		for (int i = 0; i < 10; i++) {
			try {
				VARIANT element = OleAut32Util.SafeArrayGetElement(varArray, i);
				System.out.println(element.toString(true));
			} catch (COMException e) {
				e.printStackTrace();
			}
		}
	}
}
