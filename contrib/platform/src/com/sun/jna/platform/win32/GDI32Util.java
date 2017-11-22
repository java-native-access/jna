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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * GDI32 utility API.
 * 
 * @author mlfreeman[at]gmail.com
 */
public class GDI32Util {
	private static final DirectColorModel SCREENSHOT_COLOR_MODEL = new DirectColorModel(24, 0x00FF0000, 0xFF00, 0xFF);
	private static final int[] SCREENSHOT_BAND_MASKS = {
	        SCREENSHOT_COLOR_MODEL.getRedMask(),
            SCREENSHOT_COLOR_MODEL.getGreenMask(),
            SCREENSHOT_COLOR_MODEL.getBlueMask()
	};

	/**
	 * Takes a screenshot of the given window
	 * 
	 * @param target
	 *            The window to target
	 * @return the window captured as a screenshot, or null if the BufferedImage doesn't construct properly
	 * @throws IllegalStateException
	 *             if the rectangle from GetWindowRect has a width and/or height
	 *             of 0. <br>
	 *             if the device context acquired from the original HWND doesn't
	 *             release properly
	 */
	public static BufferedImage getScreenshot(HWND target) {
		RECT rect = new RECT();
		if (!User32.INSTANCE.GetWindowRect(target, rect)) {
			throw new Win32Exception(Native.getLastError());
		}
		Rectangle jRectangle = rect.toRectangle();
		int windowWidth = jRectangle.width;
		int windowHeight = jRectangle.height;
		
		if (windowWidth == 0 || windowHeight == 0) {
			throw new IllegalStateException("Window width and/or height were 0 even though GetWindowRect did not appear to fail.");
		}
		
		HDC hdcTarget = User32.INSTANCE.GetDC(target);
		if (hdcTarget == null) {
			throw new Win32Exception(Native.getLastError());
		}

		Win32Exception we = null;

		// device context used for drawing
		HDC hdcTargetMem = null;

		// handle to the bitmap to be drawn to
		HBITMAP hBitmap = null;

		// original display surface associated with the device context
		HANDLE hOriginal = null;

		// final java image structure we're returning.
		BufferedImage image = null;
		
		try {
			hdcTargetMem = GDI32.INSTANCE.CreateCompatibleDC(hdcTarget);
			if (hdcTargetMem == null) {
				throw new Win32Exception(Native.getLastError());
			}

			hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcTarget, windowWidth, windowHeight);
			if (hBitmap == null) {
				throw new Win32Exception(Native.getLastError());
			}

			hOriginal = GDI32.INSTANCE.SelectObject(hdcTargetMem, hBitmap);
			if (hOriginal == null) {
				throw new Win32Exception(Native.getLastError());
			}

			// draw to the bitmap
			if (!GDI32.INSTANCE.BitBlt(hdcTargetMem, 0, 0, windowWidth, windowHeight, hdcTarget, 0, 0, GDI32.SRCCOPY)) {
				throw new Win32Exception(Native.getLastError());
			}

			BITMAPINFO bmi = new BITMAPINFO();
			bmi.bmiHeader.biWidth = windowWidth;
			bmi.bmiHeader.biHeight = -windowHeight;
			bmi.bmiHeader.biPlanes = 1;
			bmi.bmiHeader.biBitCount = 32;
			bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

			Memory buffer = new Memory(windowWidth * windowHeight * 4);
			int resultOfDrawing = GDI32.INSTANCE.GetDIBits(hdcTarget, hBitmap, 0, windowHeight, buffer, bmi,
					WinGDI.DIB_RGB_COLORS);
			if (resultOfDrawing == 0 || resultOfDrawing == WinError.ERROR_INVALID_PARAMETER) {
				throw new Win32Exception(Native.getLastError());
			}

			int bufferSize = windowWidth * windowHeight;
			DataBuffer dataBuffer = new DataBufferInt(buffer.getIntArray(0, bufferSize), bufferSize);
			WritableRaster raster = Raster.createPackedRaster(dataBuffer, windowWidth, windowHeight, windowWidth,
                                                              SCREENSHOT_BAND_MASKS, null);
			image = new BufferedImage(SCREENSHOT_COLOR_MODEL, raster, false, null);

		} catch (Win32Exception e) {
			we = e;
		} finally {
			if (hOriginal != null) {
				// per MSDN, set the display surface back when done drawing
				HANDLE result = GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal);
				// failure modes are null or equal to HGDI_ERROR
				if (result == null || WinGDI.HGDI_ERROR.equals(result)) {
					Win32Exception ex = new Win32Exception(Native.getLastError());
					if (we != null) {
						ex.addSuppressedReflected(we);
					}
					we = ex;
				}
			}

			if (hBitmap != null) {
				if (!GDI32.INSTANCE.DeleteObject(hBitmap)) {
					Win32Exception ex = new Win32Exception(Native.getLastError());
					if (we != null) {
						ex.addSuppressedReflected(we);
					}
					we = ex;
				}
			}

			if (hdcTargetMem != null) {
				// get rid of the device context when done
				if (!GDI32.INSTANCE.DeleteDC(hdcTargetMem)) {
					Win32Exception ex = new Win32Exception(Native.getLastError());
					if (we != null) {
						ex.addSuppressedReflected(we);
					}
					we = ex;
				}
			}

			if (hdcTarget != null) {
				if (0 == User32.INSTANCE.ReleaseDC(target, hdcTarget)) {
					throw new IllegalStateException("Device context did not release properly.");
				}
			}
		}

		if (we != null) {
			throw we;
		}
		return image;
	}
}