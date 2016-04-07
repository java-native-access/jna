/* Copyright (c) 2014 Reinhard Pointner, All Rights Reserved
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
package com.sun.jna.platform.mac;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class XAttrUtilTest extends TestCase {

	String testPath;

	protected void setUp() throws Exception {
		testPath = File.createTempFile("xattr-test", ".txt").getAbsolutePath();
		assertTrue(new File(testPath).exists());
	}

	protected void tearDown() throws Exception {
		new File(testPath).delete();
		assertFalse(new File(testPath).exists());
	}

	public void testListXAttr() {
		// no xattr initially
		List<String> keys = XAttrUtil.listXAttr(testPath);
		assertEquals(0, keys.size());

		// set multiple xattr
		String[] names = new String[] { "Java", "Native", "Access" };
		for (int i = 0; i < names.length; i++) {
			// set xattr
			XAttrUtil.setXAttr(testPath, names[i], names[i]);

			// check if new xattr is listed
			keys = XAttrUtil.listXAttr(testPath);
			assertEquals(i + 1, keys.size());
			assertTrue(keys.contains(names[i]));
		}
	}

	public void testGetXAttr() {
		String value = XAttrUtil.getXAttr(testPath, "JNA");
		assertNull(value);

		XAttrUtil.setXAttr(testPath, "JNA", "Java Native Access");
		value = XAttrUtil.getXAttr(testPath, "JNA");

		assertEquals(Arrays.toString("Java Native Access".getBytes()), Arrays.toString(value.getBytes()));
	}

	public void testSetXAttr() {
		String value = XAttrUtil.getXAttr(testPath, "JNA");
		assertNull(value);

		XAttrUtil.setXAttr(testPath, "JNA", "Java Native Access");
		value = XAttrUtil.getXAttr(testPath, "JNA");
		assertEquals("Java Native Access", value);

		XAttrUtil.setXAttr(testPath, "JNA", "is nice");
		value = XAttrUtil.getXAttr(testPath, "JNA");
		assertEquals("is nice", value);
	}

	public void testRemoveXAttr() {
		XAttrUtil.setXAttr(testPath, "JNA", "Java Native Access");
		assertEquals("[JNA]", XAttrUtil.listXAttr(testPath).toString());

		// remove xattr
		XAttrUtil.removeXAttr(testPath, "JNA");

		assertEquals("[]", XAttrUtil.listXAttr(testPath).toString());
	}

	public void testUnicode() {
		String[] names = new String[] {
                    "\u4E2D\u6587",
                    "\u306B\u307B\u3093\u3054",
                    "\u00D6sterreichisch",
                    "Fran\u00E7aise",
                    "Portugu\u00EAs",
                };
		for (int i = 0; i < names.length; i++) {
			// set xattr
			XAttrUtil.setXAttr(testPath, names[i], names[i]);

			// check if new xattr is listed
			List<String> keys = XAttrUtil.listXAttr(testPath);
			assertEquals(i + 1, keys.size());
			assertTrue(keys.contains(names[i]));

			String value = XAttrUtil.getXAttr(testPath, names[i]);
			assertEquals(names[i], value);
		}
	}

	public void testLargeData() {
		StringBuilder name = new StringBuilder();
		while (name.length() < XAttr.XATTR_MAXNAMELEN) {
			name.append('X');
		}

		StringBuilder data = new StringBuilder();
		while (data.length() < 4 * 1024 * 1024) {
			data.append('X');
		}

		XAttrUtil.setXAttr(testPath, name.toString(), data.toString());
		String value = XAttrUtil.getXAttr(testPath, name.toString());
		assertEquals(data.toString(), value.toString());
	}
}
