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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * GDI32 utility API.
 * 
 * @author mlfreeman[at]gmail.com
 */
public class GDI32Util {
	/**
	 * Takes a screenshot of the given window
	 * 
	 * @param target
	 *            The window to target
	 * @param windowWidth
	 *            the width of the window
	 * @param windowHeight
	 *            the height of the window
	 * @return the window captured as a screenshot
	 */
	public static BufferedImage getScreenshot(HWND target) {
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(target, rect);
		Rectangle rectangle = rect.toRectangle();
		HDC hdcTarget = User32.INSTANCE.GetDC(target);
		HDC hdcTargetMem = GDI32.INSTANCE.CreateCompatibleDC(hdcTarget);
		HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcTarget, rectangle.width, rectangle.height);
		HANDLE hdcTargetOld = GDI32.INSTANCE.SelectObject(hdcTargetMem, hBitmap);

		GDI32.INSTANCE.BitBlt(hdcTargetMem, 0, 0, rectangle.width, rectangle.height, hdcTarget, 0, 0, GDI32.SRCCOPY);
		GDI32.INSTANCE.SelectObject(hdcTargetMem, hdcTargetOld);
		GDI32.INSTANCE.DeleteDC(hdcTargetMem);

		BITMAPINFO bmi = new BITMAPINFO();
		bmi.bmiHeader.biWidth = rectangle.width;
		bmi.bmiHeader.biHeight = -rectangle.height;
		bmi.bmiHeader.biPlanes = 1;
		bmi.bmiHeader.biBitCount = 32;
		bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

		Memory buffer = new Memory(rectangle.width * rectangle.height * 4);
		GDI32.INSTANCE.GetDIBits(hdcTarget, hBitmap, 0, rectangle.height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

		BufferedImage image = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, rectangle.width, rectangle.height,
				buffer.getIntArray(0, rectangle.width * rectangle.height), 0, rectangle.width);

		GDI32.INSTANCE.DeleteObject(hBitmap);
		User32.INSTANCE.ReleaseDC(target, hdcTarget);

		return image;
	}
}
