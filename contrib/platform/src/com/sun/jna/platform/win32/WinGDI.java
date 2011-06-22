/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from WinGDI.h. 
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface WinGDI extends StdCallLibrary {
    public int RDH_RECTANGLES = 1;

    public class RGNDATAHEADER extends Structure {
        public int dwSize = size();
        public int iType = RDH_RECTANGLES; // required
        public int nCount;
        public int nRgnSize;
        public RECT rcBound;
    }
    
    public class RGNDATA extends Structure {
        public RGNDATAHEADER rdh;
        public byte[] Buffer;
        public RGNDATA(int bufferSize) {
            Buffer = new byte[bufferSize];
            allocateMemory();
        }
    }

    public int RGN_AND = 1;
    public int RGN_OR = 2;
    public int RGN_XOR = 3;
    public int RGN_DIFF = 4;
    public int RGN_COPY = 5;
    
    public int ERROR = 0;
    public int NULLREGION = 1;
    public int SIMPLEREGION = 2;
    public int COMPLEXREGION = 3;

    public int ALTERNATE = 1;
    public int WINDING = 2;
    
    public int BI_RGB = 0;
    public int BI_RLE8 = 1;
    public int BI_RLE4 = 2;
    public int BI_BITFIELDS = 3;
    public int BI_JPEG = 4;
    public int BI_PNG = 5;
    
    public class BITMAPINFOHEADER extends Structure {
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
    
    public class RGBQUAD extends Structure {
        public byte rgbBlue;
        public byte rgbGreen;
        public byte rgbRed;
        public byte rgbReserved = 0;
    }
    
    public class BITMAPINFO extends Structure {
        public BITMAPINFOHEADER bmiHeader = new BITMAPINFOHEADER();
        public RGBQUAD[] bmiColors = new RGBQUAD[1];
        public BITMAPINFO() { this(1); }
        public BITMAPINFO(int size) {
            bmiColors = new RGBQUAD[size];
        }
    }
    
    public int DIB_RGB_COLORS = 0;
    public int DIB_PAL_COLORS = 1;
}