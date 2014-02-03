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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
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
        
        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "dwSize", "iType", "nCount", "nRgnSize", "rcBound" });
        }
    }
    
    public class RGNDATA extends Structure {
        public RGNDATAHEADER rdh;
        public byte[] Buffer;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "rdh", "Buffer" });
        }

        public RGNDATA() { 
            this(1); 
        }
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
    
    public final int PFD_TYPE_RGBA = 0;
    public final int PFD_TYPE_COLORINDEX = 1;

    public final int PFD_MAIN_PLANE = 0;
    public final int PFD_OVERLAY_PLANE = 1;
    public final int PFD_UNDERLAY_PLANE = (-1);

    public final int PFD_DOUBLEBUFFER = 0x00000001;
    public final int PFD_STEREO = 0x00000002;
    public final int PFD_DRAW_TO_WINDOW = 0x00000004;
    public final int PFD_DRAW_TO_BITMAP = 0x00000008;
    public final int PFD_SUPPORT_GDI = 0x00000010;
    public final int PFD_SUPPORT_OPENGL = 0x00000020;
    public final int PFD_GENERIC_FORMAT = 0x00000040;
    public final int PFD_NEED_PALETTE = 0x00000080;
    public final int PFD_NEED_SYSTEM_PALETTE = 0x00000100;
    public final int PFD_SWAP_EXCHANGE = 0x00000200;
    public final int PFD_SWAP_COPY = 0x00000400;
    public final int PFD_SWAP_LAYER_BUFFERS = 0x00000800;
    public final int PFD_GENERIC_ACCELERATED = 0x00001000;
    public final int PFD_SUPPORT_DIRECTDRAW = 0x00002000;

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
        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "biSize", "biWidth", "biHeight", "biPlanes", "biBitCount", "biCompression", "biSizeImage", "biXPelsPerMeter", "biYPelsPerMeter", "biClrUsed", "biClrImportant" });
        }
    }
    
    public class RGBQUAD extends Structure {
        public byte rgbBlue;
        public byte rgbGreen;
        public byte rgbRed;
        public byte rgbReserved = 0;
        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "rgbBlue", "rgbGreen", "rgbRed", "rgbReserved" });
        }
    }
    
    public class BITMAPINFO extends Structure {
        public BITMAPINFOHEADER bmiHeader = new BITMAPINFOHEADER();
        public RGBQUAD[] bmiColors = new RGBQUAD[1];
        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "bmiHeader", "bmiColors" });
        }
        public BITMAPINFO() { this(1); }
        public BITMAPINFO(int size) {
            bmiColors = new RGBQUAD[size];
        }
    }
    
    public int DIB_RGB_COLORS = 0;
    public int DIB_PAL_COLORS = 1;

    /**
     * The PIXELFORMATDESCRIPTOR structure describes the pixel format of a drawing surface.
     */
    public static class PIXELFORMATDESCRIPTOR extends Structure {
        public PIXELFORMATDESCRIPTOR() {
            super();
            nSize = (short) size();
        }

        public PIXELFORMATDESCRIPTOR(Pointer memory) {
            super(memory);
            read();
        }

        public static class ByReference extends PIXELFORMATDESCRIPTOR implements Structure.ByReference {
        }

        /**
         * Specifies the size of this data structure. This value should be set to sizeof(PIXELFORMATDESCRIPTOR).
         */
        public short  nSize;
        /**
         * Specifies the version of this data structure. This value should be set to 1.
         */
        public short  nVersion;
        /**
         * A set of bit flags that specify properties of the pixel buffer.
         */
        public int dwFlags;
        /**
         * Specifies the type of pixel data.
         */
        public byte  iPixelType;
        /**
         * Specifies the number of color bitplanes in each color buffer.
         */
        public byte  cColorBits;
        /**
         * Specifies the number of red bitplanes in each RGBA color buffer.
         */
        public byte  cRedBits;
        /**
         * Specifies the shift count for red bitplanes in each RGBA color buffer.
         */
        public byte  cRedShift;
        /**
         * Specifies the number of green bitplanes in each RGBA color buffer.
         */
        public byte  cGreenBits;
        /**
         * Specifies the shift count for green bitplanes in each RGBA color buffer.
         */
        public byte  cGreenShift;
        /**
         * Specifies the number of blue bitplanes in each RGBA color buffer.
         */
        public byte  cBlueBits;
        /**
         * Specifies the shift count for blue bitplanes in each RGBA color buffer.
         */
        public byte  cBlueShift;
        /**
         * Specifies the number of alpha bitplanes in each RGBA color buffer. Alpha bitplanes are not supported.
         */
        public byte  cAlphaBits;
        /**
         * Specifies the shift count for alpha bitplanes in each RGBA color buffer. Alpha bitplanes are not supported.
         */
        public byte  cAlphaShift;
        /**
         * Specifies the total number of bitplanes in the accumulation buffer.
         */
        public byte  cAccumBits;
        /**
         * Specifies the number of red bitplanes in the accumulation buffer.
         */
        public byte  cAccumRedBits;
        /**
         * Specifies the number of green bitplanes in the accumulation buffer.
         */
        public byte  cAccumGreenBits;
        /**
         * Specifies the number of blue bitplanes in the accumulation buffer.
         */
        public byte  cAccumBlueBits;
        /**
         * Specifies the number of alpha bitplanes in the accumulation buffer.
         */
        public byte  cAccumAlphaBits;
        /**
         * Specifies the depth of the depth (z-axis) buffer.
         */
        public byte  cDepthBits;
        /**
         * Specifies the depth of the stencil buffer.
         */
        public byte  cStencilBits;
        /**
         * Specifies the number of auxiliary buffers. Auxiliary buffers are not supported.
         */
        public byte  cAuxBuffers;
        /**
         * Ignored. Earlier implementations of OpenGL used this member, but it is no longer used.
         */
        public byte  iLayerType;
        /**
         * Specifies the number of overlay and underlay planes.
         */
        public byte  bReserved;
        /**
         * Ignored. Earlier implementations of OpenGL used this member, but it is no longer used.
         */
        public int dwLayerMask;
        /**
         * Specifies the transparent color or index of an underlay plane.
         */
        public int dwVisibleMask;
        /**
         * Ignored. Earlier implementations of OpenGL used this member, but it is no longer used.
         */
        public int dwDamageMask;

        @SuppressWarnings("rawtypes")
        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "nSize", "nVersion", "dwFlags", "iPixelType",
                    "cColorBits", "cRedBits", "cRedShift", "cGreenBits", "cGreenShift", "cBlueBits", "cBlueShift", "cAlphaBits", "cAlphaShift",
                    "cAccumBits", "cAccumRedBits", "cAccumGreenBits", "cAccumBlueBits", "cAccumAlphaBits",
                    "cDepthBits", "cStencilBits", "cAuxBuffers", "iLayerType", "bReserved", "dwLayerMask", "dwVisibleMask", "dwDamageMask", });
        }
    }
}
