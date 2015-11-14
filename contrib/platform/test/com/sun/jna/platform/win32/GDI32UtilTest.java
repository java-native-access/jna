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

import com.sun.jna.platform.win32.WinDef.HWND;

import junit.framework.TestCase;

public class GDI32UtilTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(GDI32Test.class);
	}

	public void testGetScreenshot() {
		HWND desktopWindow = User32.INSTANCE.GetDesktopWindow();
		assertNotNull(desktopWindow);
		BufferedImage image = GDI32Util.getScreenshot(desktopWindow);

		// We'll validate that the image is "good" by checking for 20 distinct
		// colors.
		// BufferedImages normally start life as one uniform color so if that's
		// not the case then something copied over.
		List<Integer> distinctPixels = new ArrayList<Integer>();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getRGB(x, y);
				if (!distinctPixels.contains(pixel)) {
					distinctPixels.add(pixel);
					System.out.println(x + " " + y);
				}
				if (distinctPixels.size() > 20) {
					break;
				}
			}
		}
		assertTrue(distinctPixels.size() > 20);
	}
}
