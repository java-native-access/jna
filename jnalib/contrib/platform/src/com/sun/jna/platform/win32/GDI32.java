/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HRGN;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFOHEADER;
import com.sun.jna.platform.win32.WinGDI.RGNDATA;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/** Definition (incomplete) of <code>gdi32.dll</code>. */
public interface GDI32 extends StdCallLibrary {

    GDI32 INSTANCE = (GDI32) Native.loadLibrary("gdi32", GDI32.class,
                                                W32APIOptions.DEFAULT_OPTIONS);

    /**
     * The ExtCreateRegion function creates a region from the specified region and transformation data.
     * @param lpXform
     *  Pointer to an XFORM structure that defines the transformation to be performed on the region. If this pointer is NULL, 
     *  the identity transformation is used. 
     * @param nCount
     *  Specifies the number of bytes pointed to by lpRgnData.
     * @param lpRgnData
     *  Pointer to a RGNDATA structure that contains the region data in logical units.
     * @return
     *  If the function succeeds, the return value is the value of the region.
     *  If the function fails, the return value is NULL. 
     *  To get extended error information, call GetLastError.
     */
    public HRGN ExtCreateRegion(Pointer lpXform, int nCount, RGNDATA lpRgnData);

    /**
     * The CombineRgn function combines two regions and stores the result in a third region. 
     * The two regions are combined according to the specified mode.
     * @param hrgnDest
     *  Handle to a new region with dimensions defined by combining two other regions.
     * @param hrgnSrc1
     *  Handle to the first of two regions to be combined. 
     * @param hrgnSrc2
     *  Handle to the second of two regions to be combined. 
     * @param fnCombineMode
     *  Specifies a mode indicating how the two regions will be combined.
     * @return
     *  The return value specifies the type of the resulting region.
     */
    int CombineRgn(HRGN hrgnDest, HRGN hrgnSrc1, HRGN hrgnSrc2,
                   int fnCombineMode);

    /**
     * The CreateRectRgn function creates a rectangular region. 
     * @param nLeftRect
     *  Specifies the x-coordinate of the upper-left corner of the region in logical units.
     * @param nTopRect
     *  Specifies the y-coordinate of the upper-left corner of the region in logical units.
     * @param nRightRect
     *  Specifies the x-coordinate of the lower-right corner of the region in logical units. 
     * @param nBottomRect
     *  Specifies the y-coordinate of the lower-right corner of the region in logical units.
     * @return
     *  If the function succeeds, the return value is the handle to the region.
     *  If the function fails, the return value is NULL. 
     *  To get extended error information, call GetLastError.
     */
    HRGN CreateRectRgn(int nLeftRect, int nTopRect, int nRightRect,
                       int nBottomRect);

    /**
     * The CreateRoundRectRgn function creates a rectangular region with rounded corners. 
     * @param nLeftRect
     *  Specifies the x-coordinate of the upper-left corner of the region in logical units. 
     * @param nTopRect
     *  Specifies the y-coordinate of the upper-left corner of the region in logical units.
     * @param nRightRect
     *  Specifies the x-coordinate of the lower-right corner of the region in logical units.
     * @param nBottomRect
     *  Specifies the y-coordinate of the lower-right corner of the region in logical units.
     * @param nWidthEllipse
     *  Specifies the width of the ellipse used to create the rounded corners in logical units.
     * @param nHeightEllipse
     *  Specifies the height of the ellipse used to create the rounded corners in logical units.
     * @return
     *  If the function succeeds, the return value is the handle to the region.
     *  If the function fails, the return value is NULL. 
     *  To get extended error information, call GetLastError.
     */
    HRGN CreateRoundRectRgn(int nLeftRect, int nTopRect, int nRightRect,
                            int nBottomRect, int nWidthEllipse, int nHeightEllipse);

    /**
     * The CreatePolyPolygonRgn function creates a region consisting of a series 
     * of polygons. The polygons can overlap. 
     * @param lppt
     *  Pointer to an array of POINT structures that define the vertices of the polygons in logical units. 
     *  The polygons are specified consecutively. Each polygon is presumed closed and each vertex is 
     *  specified only once.
     * @param lpPolyCounts
     *  Pointer to an array of integers, each of which specifies the number of points in one of the polygons 
     *  in the array pointed to by lppt. 
     * @param nCount
     *  Specifies the total number of integers in the array pointed to by lpPolyCounts. 
     * @param fnPolyFillMode
     *  Specifies the fill mode used to determine which pixels are in the region.
     * @return
     *  If the function succeeds, the return value is the handle to the region.
     *  If the function fails, the return value is zero. 
     *  To get extended error information, call GetLastError.
     */
    HRGN CreatePolyPolygonRgn(WinUser.POINT[] lppt, int[] lpPolyCounts,
                              int nCount, int fnPolyFillMode);

    /**
     * The SetRectRgn function converts a region into a rectangular region with the specified coordinates. 
     * @param hrgn
     *  Handle to the region.
     * @param nLeftRect
     *  Specifies the x-coordinate of the upper-left corner of the rectangular region in logical units.
     * @param nTopRect
     *  Specifies the y-coordinate of the upper-left corner of the rectangular region in logical units.
     * @param nRightRect
     *  Specifies the x-coordinate of the lower-right corner of the rectangular region in logical units. 
     * @param nBottomRect
     *  Specifies the y-coordinate of the lower-right corner of the rectangular region in logical units. 
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. 
     *  To get extended error information, call GetLastError.
     */
    boolean SetRectRgn(HRGN hrgn, int nLeftRect, int nTopRect, int nRightRect,
                       int nBottomRect);

    /**
     * The SetPixel function sets the pixel at the specified coordinates to the specified color. 
     * @param hDC
     *  Handle to the device context.
     * @param x
     *  Specifies the x-coordinate, in logical units, of the point to be set. 
     * @param y
     *  Specifies the y-coordinate, in logical units, of the point to be set. 
     * @param crColor
     *  Specifies the color to be used to paint the point. To create a COLORREF color value, use the RGB macro.
     * @return
     *  If the function succeeds, the return value is the RGB value that the function sets the pixel to. 
     *  This value may differ from the color specified by crColor; that occurs when an exact match for the 
     *  specified color cannot be found. If the function fails, the return value is 1. To get extended error 
     *  information, call GetLastError. This can be the following value.
     */
    int SetPixel(HDC hDC, int x, int y, int crColor);

    /**
     * The CreateCompatibleDC function creates a memory device context (DC) compatible with the specified device. 
     * @param hDC
     *  Handle to an existing DC. If this handle is NULL, the function creates a memory DC compatible with the 
     *  application's current screen.
     * @return
     *  If the function succeeds, the return value is the handle to a memory DC.
     *  If the function fails, the return value is NULL. 
     *  To get extended error information, call GetLastError.
     */
    HDC CreateCompatibleDC(HDC hDC);

    /**
     * The DeleteDC function deletes the specified device context (DC).
     * @param hDC
     *  Handle to the device context. 
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. 
     *  To get extended error information, call GetLastError.
     */
    boolean DeleteDC(HDC hDC);

    /**
     * The CreateDIBitmap function creates a compatible bitmap (DDB) from a DIB and, optionally,
     * sets the bitmap bits. 
     * @param hDC
     *  Handle to a device context. 
     * @param lpbmih
     *  Pointer to a bitmap information header structure, which may be one of those shown in the following table.
     * @param fdwInit
     *  Specifies how the system initializes the bitmap bits.
     * @param lpbInit
     *  Pointer to an array of bytes containing the initial bitmap data.
     * @param lpbmi
     *  Pointer to a BITMAPINFO structure that describes the dimensions and color format of 
     *  the array pointed to by the lpbInit parameter.
     * @param fuUsage
     *  Specifies whether the bmiColors member of the BITMAPINFO structure was initialized and, if so,
     *  whether bmiColors contains explicit red, green, blue (RGB) values or palette indexes. The 
     *  fuUsage parameter must be one of the following values. 
     * @return
     *  If the function succeeds, the return value is a handle to the compatible bitmap. 
     *  If the function fails, the return value is NULL. 
     *  To get extended error information, call GetLastError.
     */
    HBITMAP CreateDIBitmap(HDC hDC, BITMAPINFOHEADER lpbmih, int fdwInit,
                           Pointer lpbInit, BITMAPINFO lpbmi, int fuUsage);

    /**
     * The CreateDIBSection function creates a DIB that applications can write to directly. 
     * The function gives you a pointer to the location of the bitmap bit values. You can supply
     * a handle to a file-mapping object that the function will use to create the bitmap, or you
     * can let the system allocate the memory for the bitmap.
     * @param hDC
     *  Handle to a device context. If the value of iUsage is DIB_PAL_COLORS, the function uses this
     *  device context's logical palette to initialize the DIB colors.
     * @param pbmi
     *  Pointer to a BITMAPINFO structure that specifies various attributes of the DIB, including 
     *  the bitmap dimensions and colors. 
     * @param iUsage
     *  Specifies the type of data contained in the bmiColors array member of the BITMAPINFO structure 
     *  pointed to by pbmi (either logical palette indexes or literal RGB values). 
     * @param ppvBits
     *  Pointer to a variable that receives a pointer to the location of the DIB bit values.
     * @param hSection
     *  Handle to a file-mapping object that the function will use to create the DIB. This parameter can be NULL.
     * @param dwOffset
     *  Specifies the offset from the beginning of the file-mapping object referenced by hSection where storage 
     *  for the bitmap bit values is to begin.
     * @return
     *  Specifies the offset from the beginning of the file-mapping object referenced by hSection where storage 
     *  for the bitmap bit values is to begin.
     */
    HBITMAP CreateDIBSection(HDC hDC, BITMAPINFO pbmi, int iUsage,
                             PointerByReference ppvBits, Pointer hSection, int dwOffset);

    /**
     * The CreateCompatibleBitmap function creates a bitmap compatible with the device that is
     * associated with the specified device context. 
     * @param hDC
     *  Handle to a device context. 
     * @param width
     *  Specifies the bitmap width, in pixels. 
     * @param height
     *  Specifies the bitmap height, in pixels. 
     * @return
     *  If the function succeeds, the return value is a handle to the compatible bitmap (DDB).
     *  If the function fails, the return value is NULL. 
     *  To get extended error information, call GetLastError.
     */
    HBITMAP CreateCompatibleBitmap(HDC hDC, int width, int height);

    /**
     * The SelectObject function selects an object into the specified device context (DC). 
     * The new object replaces the previous object of the same type. 
     * @param hDC
     *  Handle to the DC. 
     * @param hGDIObj
     *  Handle to the object to be selected.
     * @return
     *  If the selected object is not a region and the function succeeds, the return value 
     *  is a handle to the object being replaced. If the selected object is a region and the 
     *  function succeeds, the return value is one of the REGION values. 
     */
    HANDLE SelectObject(HDC hDC, HANDLE hGDIObj);

    /**
     * The DeleteObject function deletes a logical pen, brush, font, bitmap, region, or palette, 
     * freeing all system resources associated with the object. After the object is deleted, the 
     * specified handle is no longer valid.
     * @param hObject
     *  Handle to a logical pen, brush, font, bitmap, region, or palette. 
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the specified handle is not valid or is currently selected into a DC, the return value is zero. 
     *  To get extended error information, call GetLastError.
     */
    boolean DeleteObject(HANDLE hObject);

    /** The GetDeviceCaps function retrieves device-specific information for
     * the specified device.
     * @param hdc A handle to the DC.
     * @param nIndex The item to be returned.  
     * @return
     *  The return value specifies the value of the desired item.  When
     *  <i>nIndex</i> is <code>BITSPIXEL</code> and the device has 15bpp or
     *  16bpp, the return value is 16.
     */
    int GetDeviceCaps(HDC hdc, int nIndex);

    /** The GetDIBits function retrieves the bits fo the specified compatible
     * bitmap and copies them into a buffer as a DIB using the specified
     * format. 
     * @param hdc A handle to the device context.
     * @param hbmp A handle to the bitmap.  This must be a compatible bitmap
     * (DDB). 
     * @param uStartScan The first scan line to retrieve
     * @param cScanLines The number of scan lines to retrieve.
     * @param lpvBits A pointer to a buffer to receive the bitmap data.  If
     * this parameter is <code>null</code>, the function passes the dimensions
     * and format of the bitmap to the {@link BITMAPINFO} structure pointed to
     * by the <i>lpbi</i> parameter.
     * @param lpbi A pointer to a {@link BITMAPINFO} structure that specifies
     * the desired format for the DIB data.
     * @param uUsage The format of the bmiColors member of the {@link
     * BITMAPINFO} structure.  
     */
    int GetDIBits(HDC hdc, HBITMAP hbmp, int uStartScan, int cScanLines, Pointer lpvBits, BITMAPINFO lpbi, int uUsage);
}
