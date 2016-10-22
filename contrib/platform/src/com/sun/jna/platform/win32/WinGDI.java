/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.RECT;

/**
 * Ported from WinGDI.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public interface WinGDI {
    int RDH_RECTANGLES = 1;

    class RGNDATAHEADER extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwSize", "iType", "nCount", "nRgnSize", "rcBound");
        public int dwSize = size();
        public int iType = RDH_RECTANGLES; // required
        public int nCount;
        public int nRgnSize;
        public RECT rcBound;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class RGNDATA extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("rdh", "Buffer" );
        public RGNDATAHEADER rdh;
        public byte[] Buffer;

        public RGNDATA() {
            this(1);
        }
        public RGNDATA(int bufferSize) {
            Buffer = new byte[bufferSize];
            allocateMemory();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    HANDLE HGDI_ERROR = new HANDLE(Pointer.createConstant(0xFFFFFFFF));

    int RGN_AND = 1;
    int RGN_OR = 2;
    int RGN_XOR = 3;
    int RGN_DIFF = 4;
    int RGN_COPY = 5;

    int ERROR = 0;
    int NULLREGION = 1;
    int SIMPLEREGION = 2;
    int COMPLEXREGION = 3;

    int ALTERNATE = 1;
    int WINDING = 2;

    int BI_RGB = 0;
    int BI_RLE8 = 1;
    int BI_RLE4 = 2;
    int BI_BITFIELDS = 3;
    int BI_JPEG = 4;
    int BI_PNG = 5;

    int PFD_TYPE_RGBA = 0;
    int PFD_TYPE_COLORINDEX = 1;

    int PFD_MAIN_PLANE = 0;
    int PFD_OVERLAY_PLANE = 1;
    int PFD_UNDERLAY_PLANE = (-1);

    int PFD_DOUBLEBUFFER = 0x00000001;
    int PFD_STEREO = 0x00000002;
    int PFD_DRAW_TO_WINDOW = 0x00000004;
    int PFD_DRAW_TO_BITMAP = 0x00000008;
    int PFD_SUPPORT_GDI = 0x00000010;
    int PFD_SUPPORT_OPENGL = 0x00000020;
    int PFD_GENERIC_FORMAT = 0x00000040;
    int PFD_NEED_PALETTE = 0x00000080;
    int PFD_NEED_SYSTEM_PALETTE = 0x00000100;
    int PFD_SWAP_EXCHANGE = 0x00000200;
    int PFD_SWAP_COPY = 0x00000400;
    int PFD_SWAP_LAYER_BUFFERS = 0x00000800;
    int PFD_GENERIC_ACCELERATED = 0x00001000;
    int PFD_SUPPORT_DIRECTDRAW = 0x00002000;

    class BITMAPINFOHEADER extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("biSize",
                "biWidth", "biHeight", "biPlanes", "biBitCount", "biCompression",
                "biSizeImage", "biXPelsPerMeter", "biYPelsPerMeter", "biClrUsed", "biClrImportant");

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

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class RGBQUAD extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("rgbBlue", "rgbGreen", "rgbRed", "rgbReserved");

        public byte rgbBlue;
        public byte rgbGreen;
        public byte rgbRed;
        public byte rgbReserved = 0;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class BITMAPINFO extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("bmiHeader", "bmiColors");

        public BITMAPINFOHEADER bmiHeader = new BITMAPINFOHEADER();
        public RGBQUAD[] bmiColors = new RGBQUAD[1];
        public BITMAPINFO() {
            this(1);
        }
        public BITMAPINFO(int size) {
            bmiColors = new RGBQUAD[size];
        }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class ICONINFO extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("fIcon", "xHotspot", "yHotspot", "hbmMask", "hbmColor");

        public boolean fIcon;
        public int xHotspot;
        public int yHotspot;
        public HBITMAP hbmMask;
        public HBITMAP hbmColor;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class BITMAP extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("bmType", "bmWidth", "bmHeight",
                "bmWidthBytes", "bmPlanes", "bmBitsPixel", "bmBits");
        public NativeLong bmType;
        public NativeLong bmWidth;
        public NativeLong bmHeight;
        public NativeLong bmWidthBytes;
        public short bmPlanes;
        public short bmBitsPixel;
        public Pointer bmBits;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class DIBSECTION extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dsBm", "dsBmih", "dsBitfields", "dshSection", "dsOffset");

        public BITMAP           dsBm;
        public BITMAPINFOHEADER dsBmih;
        public int[]            dsBitfields = new int[3];
        public HANDLE           dshSection;
        public int              dsOffset;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    int DIB_RGB_COLORS = 0;
    int DIB_PAL_COLORS = 1;

    /**
     * The PIXELFORMATDESCRIPTOR structure describes the pixel format of a drawing surface.
     */
    class PIXELFORMATDESCRIPTOR extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("nSize", "nVersion", "dwFlags", "iPixelType",
                "cColorBits", "cRedBits", "cRedShift", "cGreenBits", "cGreenShift", "cBlueBits", "cBlueShift", "cAlphaBits", "cAlphaShift",
                "cAccumBits", "cAccumRedBits", "cAccumGreenBits", "cAccumBlueBits", "cAccumAlphaBits",
                "cDepthBits", "cStencilBits", "cAuxBuffers", "iLayerType", "bReserved", "dwLayerMask", "dwVisibleMask", "dwDamageMask");

        public PIXELFORMATDESCRIPTOR() {
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

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
