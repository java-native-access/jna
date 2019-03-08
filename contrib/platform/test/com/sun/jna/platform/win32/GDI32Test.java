/* Copyright (c) 2010 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
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
            if (!User32.INSTANCE.GetIconInfo(new HICON(hImage), iconInfo)) {
                throw new Exception(
                        "Invocation of User32.GetIconInfo() failed: "
                        + Kernel32Util.getLastErrorMessage());
            }
            iconInfo.read();

            // test GetObject method
            BITMAP bmp = new BITMAP();
            int nWrittenBytes = GDI32.INSTANCE.GetObject(iconInfo.hbmColor,
                    bmp.size(), bmp.getPointer());
            bmp.read();
            if (nWrittenBytes <= 0) {
                throw new Exception("Detection of bitmap information failed: "
                        + Kernel32Util.getLastErrorMessage());
            }

            // verify that bitmap information was successfully detected
            assertEquals(32, bmp.bmHeight.intValue());
            assertEquals(32, bmp.bmWidth.intValue());
        } finally {
            if (iconInfo.hbmColor != null
                    && iconInfo.hbmColor.getPointer() != Pointer.NULL) {
                GDI32.INSTANCE.DeleteObject(iconInfo.hbmColor);
            }
            if (iconInfo.hbmMask != null
                    && iconInfo.hbmMask.getPointer() != Pointer.NULL) {
                GDI32.INSTANCE.DeleteObject(iconInfo.hbmMask);
            }
        }
    }
}
