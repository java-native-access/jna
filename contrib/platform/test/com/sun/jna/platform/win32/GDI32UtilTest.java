/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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
		List<Integer> distinctPixels = new ArrayList<Integer>();
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
