/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
import com.sun.jna.platform.win32.WinDef.SHORT;
import com.sun.jna.platform.win32.COM.COMException;

/**
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class OleAutoUtilTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(OleAutoUtilTest.class);
	}

	public void testCreateVarArray() {
		SAFEARRAY varArray = OleAutoUtil.createVarArray(1);
		assertTrue(varArray != null);
	}

	public void testSafeArrayPutGetElement() {
		SAFEARRAY varArray = OleAutoUtil.createVarArray(10);

		for (int i = 0; i < 10; i++) {
			try {
				VARIANT variant = new VARIANT(new SHORT(i + i*100));
				System.out.println(variant.toString(true));
				OleAutoUtil.SafeArrayPutElement(varArray, i, variant);
			} catch (COMException e) {
				e.printStackTrace();
			}
		}

		assertTrue(varArray != null);

		System.out
				.println("\n\n\n-------------------------------------------------------------");
		System.out
				.println("-------------------------------------------------------------\n\n\n");

		for (int i = 0; i < 10; i++) {
			try {
				VARIANT element = OleAutoUtil.SafeArrayGetElement(varArray, i);
				System.out.println(element.toString(true));
				System.out.println("variant type: " + element.getVarType());
				System.out.println("value: " + element.getValue());
			} catch (COMException e) {
				e.printStackTrace();
			}
		}
	}
}
