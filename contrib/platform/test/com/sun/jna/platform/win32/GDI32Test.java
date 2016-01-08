/* Copyright (c) 2010 Timothy Wall, All Rights Reserved
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

import java.io.File;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinGDI.BITMAP;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import junit.framework.TestCase;

/**
 * @author twalljava[at]dev[dot]java[dot]net
 */
public class GDI32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(GDI32Test.class);
    }

    public void testBITMAPINFO() {
        BITMAPINFO info = new BITMAPINFO();
        assertEquals("Wrong size for BITMAPINFO()", 44, info.size());

        info = new BITMAPINFO(2);
        assertEquals("Wrong size for BITMAPINFO(2)", 48, info.size());

        info = new BITMAPINFO(16);
        assertEquals("Wrong size for BITMAPINFO(16)", 104, info.size());
    }

	public void testGetObject() throws Exception {
		final ICONINFO iconInfo = new ICONINFO();
		final HANDLE hImage = User32.INSTANCE.LoadImage(null, new File(
				getClass().getResource("/res/test_icon.ico").toURI())
				.getAbsolutePath(), WinUser.IMAGE_ICON, 0, 0,
				WinUser.LR_LOADFROMFILE);

		try {
			// obtain test icon from classpath
			if (!User32.INSTANCE.GetIconInfo(new HICON(hImage), iconInfo))
				throw new Exception(
						"Invocation of User32.GetIconInfo() failed: "
								+ Kernel32Util.getLastErrorMessage());
			iconInfo.read();

			// test GetObject method
			BITMAP bmp = new BITMAP();
			int nWrittenBytes = GDI32.INSTANCE.GetObject(iconInfo.hbmColor,
					bmp.size(), bmp.getPointer());
			bmp.read();
			if (nWrittenBytes <= 0)
				throw new Exception("Detection of bitmap information failed: "
						+ Kernel32Util.getLastErrorMessage());

			// verify that bitmap information was successfully detected
			assertEquals(32, bmp.bmHeight.intValue());
			assertEquals(32, bmp.bmWidth.intValue());
		} finally {
			if (iconInfo.hbmColor != null
					&& iconInfo.hbmColor.getPointer() != Pointer.NULL)
				GDI32.INSTANCE.DeleteObject(iconInfo.hbmColor);
			if (iconInfo.hbmMask != null
					&& iconInfo.hbmMask.getPointer() != Pointer.NULL)
				GDI32.INSTANCE.DeleteObject(iconInfo.hbmMask);
		}
	}
}
