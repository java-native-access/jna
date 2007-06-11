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
package com.sun.jna.examples.win32;

import java.awt.Rectangle;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;

/** Definition (incomplete) of <code>gdi32.dll</code>. */
public interface GDI32 extends W32API {
    
    GDI32 INSTANCE = (GDI32)
        Native.loadLibrary("gdi32", GDI32.class, DEFAULT_OPTIONS);

    public static class RECT extends Structure {
        public int left;
        public int top;
        public int right;
        public int bottom;
        public Rectangle toRectangle() {
            return new Rectangle(left, top, right-left, bottom-top);
        }
        public String toString() {
            return "[(" + left + "," + top + ")(" + right + "," + bottom + ")]";
        }
    }

    int RDH_RECTANGLES = 1;
    public static class RGNDATAHEADER extends Structure {
        public int dwSize = size();
        public int iType = RDH_RECTANGLES; // required
        public int nCount;
        public int nRgnSize;
        public RECT rcBound; 
    }
    public static class RGNDATA extends Structure {
        public RGNDATAHEADER rdh;
        public byte[] Buffer;
        public RGNDATA(int bufferSize) {
            Buffer = new byte[bufferSize];
            allocateMemory();
        }
    }
    
    public Pointer ExtCreateRegion(Pointer lpXform, int nCount, RGNDATA lpRgnData);

    int RGN_AND = 1;
    int RGN_OR = 2;
    int RGN_XOR = 3;
    int RGN_DIFF = 4;
    int RGN_COPY = 5;
    
    int ERROR = 0;
    int NULLREGION = 1;
    int SIMPLEREGION = 2;
    int COMPLEXREGION = 3;
    int CombineRgn(Pointer hrgnDest, Pointer hrgnSrc1, Pointer hrgnSrc2, int fnCombineMode);
    
    Pointer CreateRectRgn(int nLeftRect, int nTopRect,
                          int nRightRect, int nBottomRect);
    
    Pointer CreateRoundRectRgn(int nLeftRect, int nTopRect,
                               int nRightRect, int nBottomRect,
                               int nWidthEllipse, 
                               int nHeightEllipse);
    
    int SetPixel(Pointer hDC, int x, int y, int crColor);
    
    Pointer CreateCompatibleDC(Pointer hDC);
    boolean DeleteDC(Pointer hDC);
    
    int BI_RGB = 0;
    int BI_RLE8 = 1;
    int BI_RLE4 = 2;
    int BI_BITFIELDS = 3;
    int BI_JPEG = 4;
    int BI_PNG = 5;
    public static class BITMAPINFOHEADER extends Structure {
        public int biSize = size();
        public int biWidth;
        public int biHeight;
        public short biPlanes;
        public short biBitCount;
        public int biCompression;
        public int biSizeImage;
        public int biXPelsPerMeter;
        public int biYPelsPerMeter;
        public int biClrUsed;
        public int biClrImportant;
    }
    public static class RGBQUAD extends Structure {
        public byte rgbBlue;
        public byte rgbGreen;
        public byte rgbRed;
        public byte rgbReserved = 0;
    }
    public static class BITMAPINFO extends Structure {
        public BITMAPINFOHEADER bmiHeader = new BITMAPINFOHEADER();
        //RGBQUAD:
        //byte rgbBlue;
        //byte rgbGreen;
        //byte rgbRed;
        //byte rgbReserved = 0;
        int[] bmiColors = new int[1];
        public BITMAPINFO() { this(1); }
        public BITMAPINFO(int size) {
            bmiColors = new int[size];
            allocateMemory();
        }
    }
    int DIB_RGB_COLORS = 0;
    int DIB_PAL_COLORS = 1;
    Pointer CreateDIBitmap(Pointer hDC, BITMAPINFOHEADER lpbmih, int fdwInit,
                           Pointer lpbInit, BITMAPINFO lpbmi, int fuUsage);
    Pointer CreateDIBSection(Pointer hDC, BITMAPINFO pbmi, int iUsage,
                             PointerByReference ppvBits, Pointer hSection,
                             int dwOffset);
    Pointer CreateCompatibleBitmap(Pointer hDC, int width, int height);
    
    Pointer SelectObject(Pointer hDC, Pointer hGDIObj);
    boolean DeleteObject(Pointer p);
}
