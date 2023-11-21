/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import com.sun.jna.platform.win32.WinDef.HWND;

public class GDI32UtilTest extends AbstractWin32TestSupport {

    public static void main(String[] args) {
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.run(GDI32UtilTest.class);
    }

    @Test
    public void testGetScreenshot() {
        HWND desktopWindow = User32.INSTANCE.GetDesktopWindow();
        assertNotNull("Failed to obtain desktop window handle", desktopWindow);
        BufferedImage image = GDI32Util.getScreenshot(desktopWindow);
        // Since this test involves taking a whole-desktop screenshot
        // we can't be sure what the image will be exactly.
        // We'll validate that the image is "good"
        // by checking for 20 distinct colors.
        // BufferedImages normally start life as one uniform color
        // so if that's not the case then some data was indeed copied over as a result of the getScreenshot() function.
        List<Integer> distinctPixels = new ArrayList<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getRGB(x, y);
                if (!distinctPixels.contains(pixel)) {
                    distinctPixels.add(pixel);
                }
                if (distinctPixels.size() > 20) {
                    break;
                }
            }
        }
        assertTrue("Number of distinct pixels was not above 20.", distinctPixels.size() > 20);
    }
}
