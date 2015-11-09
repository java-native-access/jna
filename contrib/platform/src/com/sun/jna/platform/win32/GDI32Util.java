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
		GDI32 gdi32 = GDI32.INSTANCE;
		User32 user32 = User32.INSTANCE;

		RECT rect = new RECT();

		user32.GetWindowRect(target, rect);

		Rectangle rectangle = rect.toRectangle();

		HDC hdcTarget = user32.GetDC(target);

		HDC hdcTargetMem = gdi32.CreateCompatibleDC(hdcTarget);

		HBITMAP hBitmap = gdi32.CreateCompatibleBitmap(hdcTarget, rectangle.width, rect.toRectangle().height);

		HANDLE hdcTargetOld = gdi32.SelectObject(hdcTargetMem, hBitmap);

		gdi32.BitBlt(hdcTargetMem, 0, 0, rectangle.width, rect.toRectangle().height, hdcTarget, 0, 0, GDI32.SRCCOPY);

		gdi32.SelectObject(hdcTargetMem, hdcTargetOld);
		gdi32.DeleteDC(hdcTargetMem);

		BITMAPINFO bmi = new BITMAPINFO();
		bmi.bmiHeader.biWidth = rectangle.width;
		bmi.bmiHeader.biHeight = -rect.toRectangle().height;
		bmi.bmiHeader.biPlanes = 1;
		bmi.bmiHeader.biBitCount = 32;
		bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

		Memory buffer = new Memory(rectangle.width * rect.toRectangle().height * 4);
		gdi32.GetDIBits(hdcTarget, hBitmap, 0, rect.toRectangle().height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

		BufferedImage image = new BufferedImage(rectangle.width, rect.toRectangle().height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, rectangle.width, rect.toRectangle().height,
				buffer.getIntArray(0, rectangle.width * rect.toRectangle().height), 0, rectangle.width);

		gdi32.DeleteObject(hBitmap);
		user32.ReleaseDC(target, hdcTarget);

		return image;
	}
}
