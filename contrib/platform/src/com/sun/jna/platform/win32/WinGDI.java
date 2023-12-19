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

import com.sun.jna.*;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import static com.sun.jna.platform.win32.WinDef.*;
import java.nio.charset.StandardCharsets;

/**
 * Ported from WinGDI.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 * @author SSHTOOLS Limited, support@sshtools.com
 */
public interface WinGDI {
    int RDH_RECTANGLES = 1;

    int DM_ORIENTATION = 0x00000001;
    int DM_PAPERSIZE = 0x00000002;
    int DM_PAPERLENGTH = 0x00000004;
    int DM_PAPERWIDTH = 0x00000008;
    int DM_SCALE = 0x00000010;
    int DM_POSITION = 0x00000020;
    int DM_NUP = 0x00000040;
    int DM_DISPLAYORIENTATION = 0x00000080;
    int DM_COPIES = 0x00000100;
    int DM_DEFAULTSOURCE = 0x00000200;
    int DM_PRINTQUALITY = 0x00000400;
    int DM_COLOR = 0x00000800;
    int DM_DUPLEX = 0x00001000;
    int DM_YRESOLUTION = 0x00002000;
    int DM_TTOPTION = 0x00004000;
    int DM_COLLATE = 0x00008000;
    int DM_FORMNAME = 0x00010000;
    int DM_LOGPIXELS = 0x00020000;
    int DM_BITSPERPEL = 0x00040000;
    int DM_PELSWIDTH = 0x00080000;
    int DM_PELSHEIGHT = 0x00100000;
    int DM_DISPLAYFLAGS = 0x00200000;
    int DM_DISPLAYFREQUENCY = 0x00400000;
    int DM_ICMMETHOD = 0x00800000;
    int DM_ICMINTENT = 0x01000000;
    int DM_MEDIATYPE = 0x02000000;
    int DM_DITHERTYPE = 0x04000000;
    int DM_PANNINGWIDTH = 0x08000000;
    int DM_PANNINGHEIGHT = 0x10000000;
    int DM_DISPLAYFIXEDOUTPUT = 0x20000000;

    @FieldOrder({ "dmDeviceName", "dmSpecVersion", "dmDriverVersion", "dmSize", "dmDriverExtra", "dmFields", "dmUnion1", "dmColor",
            "dmDuplex", "dmYResolution", "dmTTOption", "dmCollate", "dmFormName", "dmLogPixels", "dmBitsPerPel", "dmPelsWidth",
            "dmPelsHeight", "dummyunionname2", "dmDisplayFrequency", "dmICMMethod", "dmICMIntent", "dmMediaType", "dmDitherType",
            "dmReserved1", "dmReserved2", "dmPanningWidth", "dmPanningHeight" })
    /**
     * The size of the "public" <code>DEVMODE</code> data can vary for different versions of the structure.
     * <p>
     * - The <code>dmSize</code> member specifies the number of bytes of "public" data
     * - The <code>dmDriverExtra</code> member specifies the number of bytes of "private" data.
     *
     * A device driver's "private" data follows the public portion of the <code>DEVMODE</code> structure.
     *
     * @see <a href=
     *      "https://docs.microsoft.com/ewindows/win32/api/wingdi/ns-wingdi-devmodew">
     * DEVMODEW structure</a>
     */
    public static class DEVMODE extends Structure {
        public static class ByReference extends DEVMODE implements Structure.ByReference {}
        private static final int CHAR_WIDTH = Boolean.getBoolean("w32.ascii") ? 1 : 2;
        private static final int DUMMYSTRUCTNAME_MASK = DM_ORIENTATION | DM_PAPERSIZE | DM_PAPERLENGTH | DM_PAPERWIDTH
                | DM_SCALE | DM_COPIES | DM_DEFAULTSOURCE | DM_PRINTQUALITY;
        private static final int DUMMYSTRUCTNAME2_MASK = DM_POSITION | DM_DISPLAYORIENTATION | DM_DISPLAYFIXEDOUTPUT;

        @Override
        public void read() {
            super.read();
            if ((dmFields & DUMMYSTRUCTNAME_MASK) > 0) {
                dmUnion1.setType(DUMMYUNIONNAME.DUMMYSTRUCTNAME.class);
                dmUnion1.read();
            } else if ((dmFields & DUMMYSTRUCTNAME2_MASK) > 0) {
                dmUnion1.setType(DUMMYUNIONNAME.DUMMYSTRUCTNAME2.class);
                dmUnion1.read();
            } else if ((dmFields & DM_POSITION) > 0) {
                dmUnion1.setType(POINT.class);
                dmUnion1.read();
            }
        }

        /**
         * A zero-terminated character array that specifies the "friendly" name of the printer or display
         * <p>
         * For a printer: "PCL/HP LaserJet" in the case of PCL/HP LaserJet.
         *
         * For a display: "perm3dd" in the case of the 3Dlabs Permedia3 display driver.
         *
         * This string is unique among device driverers.  Note that this name may be truncated to fit in the
         * <code>dmDeviceName</code> array.
         */
        public byte[] dmDeviceName = new byte[Winspool.CCHDEVICENAME * CHAR_WIDTH];

        /**
         * The version number of the initialization data specification on which the structure is based. To ensure the
         * correct version is used for any operating system, use <code>DM_SPECVERSION</code> constant in <code>wingdi.h</code>
         */
        public short dmSpecVersion;

        /**
         * For a printer: Specifies the printer driver version number assigned by the printer driver developer.
         *
         * For a display: Drivers can set this member to <code>DM_SPECVERSION</code> constant in <code>wingdi.h</code>.
         */
        public short dmDriverVersion;

        /**
         * Specifies the size in bytes of the public <code>DEVMODE</code> structure, not including any private
         * driver-specified members identified by the <code>dmDriverExtra</code> member.
         */
        public short dmSize;

        /**
         * Contains the number of bytes of private driver-data that follow this structure. If a device driver
         * does not use device-specific information, set this member to zero.
         *
         * Since the size and format of this data is driver-specific, applications needing it
         * must read the data into a separate one-off struct to match.
         */
        public short dmDriverExtra;

        /**
         * Specifies bit flags identifying which of the following <code>DEVMODE</code> members are in use.
         *
         * For example, the <code>DM_ORIENTATION</<code> flag is set when the <code>dmOrientation</code> member
         * contains valid data. The <code>DM_XXX</code> flags are defined in <code>wingdi.h.</code>
         */
        public int dmFields;

        public DUMMYUNIONNAME dmUnion1;

        /**
         * For printers: Specifies whether a color printer should print color or monochrome. This member can be one of
         * <code>DMCOLOR_COLOR</code> or <code>DMCOLOR_MONOCHROME</code>.
         *
         * For displays: This member is not used for displays.
         */
        public short dmColor;

        /**
         * For printers: Specifies duplex (double-sided) printing for duplex-capable printers. This member can be
         * <code>DMCOLOR_COLOR</code> or <code>DMCOLOR_MONOCHROME</code>.
         *
         * For displays: This member is not used for displays.
         */
        public short dmDuplex;

        /**
         * For printers: Specifies the y resolution of the printer, in DPI. If this member is used, the
         * <code>dmPrintQuality</code> member specifies the x resolution.
         *
         * For displays: This member is not used for displays.
         */
        public short dmYResolution;

        /**
         * For printers: Specifies how TrueType fonts should be printed. This member must be one of the
         * <code>DMTT_XXX</code> constants defined in <code>wingdi.h</code>.
         *
         * For displays: This member is not used for displays.
         */
        public short dmTTOption;

        /**
         * For printers: Specifies whether multiple copies should be collated. This member can be one of
         * <code>DMCOLLATE_TRUE</code>, <code>DMCOLLATE_FALSE</code>.
         *
         * For displays: This member is not used for displays.
         */
        public short dmCollate;

        /**
         * For printers: Specifies the name of the form to use; such as "Letter" or "Legal". This must be a name that
         * can be obtained by calling the Win32 <code>EnumForms</code> function (described in the Microsoft Window SDK
         * documentation).
         *
         * For displays: This member is not used for displays.
         */
        public byte[] dmFormName = new byte[Winspool.CCHFORMNAME * CHAR_WIDTH];

        /**
         * For displays: Specifies the number of logical pixels per inch of a display device and should be equal to the
         * <code>ulLogPixels</code> member of the <code>GDIINFO</code> structure.
         *
         * For printers: This member is not used for printers.
         */
        public short dmLogPixels;

        /**
         * For displays: Specifies the color resolution, in bits per pixel, of a display device.
         *
         * For printers: This member is not used for printers.
         */
        public int dmBitsPerPel;

        /**
         * For displays: Specifies the width, in pixels, of the visible device surface.
         *
         * For printers: This member is not used for printers.
         */
        public int dmPelsWidth;

        /**
         * For displays: Specifies the height, in pixels, of the visible device surface.
         *
         * For printers: This member is not used for printers.
         */
        public int dmPelsHeight;

        public DUMMYUNIONNAME2 dummyunionname2;

        /**
         * For displays: Specifies the frequency, in hertz (cycles per second), of the display device in a particular
         * mode. This value is also known as the display device's vertical refresh rate. Display drivers use this
         * member. It is used, for example, in the <code>ChangeDisplaySettings</code> function.
         *
         * When you call the <code>EnumDisplaySettings</code> function, the <code>dmDisplayFrequency</code> member may
         * return with the value 0 or 1. These values represent the display hardware's default refresh rate. This
         * default rate is typically set by switches on a display card or computer motherboard, or by a configuration
         * program that does not use display functions such as <code>ChangeDisplaySettings</code>.
         *
         * For printers: This member is not used for printers.
         */
        public int dmDisplayFrequency;

        /**
         * For printers: Specifies how ICM (image color management) is handled. For a non-ICM application, this member determines
         * if ICM is enabled or disabled. For ICM applications, the system examines this member to determine how to
         * handle ICM support. This member can be one of the predefined <code>DMICMMETHOD_XXX</code>values, or a driver-defined value
         * greater than or equal to the value of <code>DMICMMETHOD_USER</code>.
         *
         * The printer driver must provide a user interface for setting this member. Most printer drivers support only
         * the <code>DMICMMETHOD_SYSTEM</code> or <code>DMICMMETHOD_NONE</code> value. Drivers for PostScript printers
         * support all <code>DCMICMMETHOD_XXX</code>values.
         *
         * For displays: This member is not used for displays.
         */
        public int dmICMMethod;

        /**
         * For printers: Specifies which color matching method, or intent, should be used by default. This member is primarily for
         * non-ICM applications. ICM applications can establish intents by using the ICM functions. This member can be
         * one of the following predefined values, or a driver defined value greater than or equal to the value of
         * <code>DMICM_USER</code>.
         *
         * For displays: This member is not used for displays.
         */
        public int dmICMIntent;

        /**
         * For printers: Specifies the type of media being printed on. The member can be one of the predefined
         * <code>DMMEDIA_XXX</code> values, or a driver-defined value greater than or equal to the value of <code>DMMEDIA_USER</code>.
         *
         * To retrieve a list of the available media types for a printer, use the <code>DeviceCapabilities</code>
         * function with the <code>DC_MEDIATYPES</code> flag.
         *
         * For displays: This member is not used for displays.
         */
        public int dmMediaType;

        /**
         * For printers: Specifies how dithering is to be done. The member can be one of the predefined
         * <code>DMDITHER_XXX</code> values, or a driver-defined value greater than or equal to the value of
         * <code>DMDITHER_USER</code>.
         *
         * For displays: This member is not used for displays.
         */
        public int dmDitherType;

        /**
         * Not used; must be zero.
         */
        public int dmReserved1;

        /**
         * Not used; must be zero.
         */
        public int dmReserved2;

        /**
         * This member must be zero.
         */
        public int dmPanningWidth;

        /**
         * This member must be zero.
         */
        public int dmPanningHeight;

        /**
         * Converts dmDeviceName from raw byte[] to String
         */
        public String getDmDeviceName() {
            long offset = fieldOffset("dmDeviceName");
            if(CHAR_WIDTH == 1) {
                //todo: this can overrun if there is no null, perhaps we should add overrun protection to getString
                return this.getPointer().getString(offset);
            } else {
                return this.getPointer().getWideString(offset);
            }
        }

        /**
         * Converts dmFormName from raw byte[] to String
         */
        public String getDmFormName() {
            long offset = fieldOffset("dmFormName");
            if(CHAR_WIDTH == 1) {
                return this.getPointer().getString(offset);
            } else {
                return this.getPointer().getWideString(offset);
            }
        }

        public static class DUMMYUNIONNAME extends Union {
            public DUMMYSTRUCTNAME dummystructname;
            public POINT dmPosition;
            public DUMMYSTRUCTNAME2 dummystructname2;

            @FieldOrder({ "dmOrientation", "dmPaperSize", "dmPaperLength", "dmPaperWidth", "dmScale", "dmCopies", "dmDefaultSource",
                    "dmPrintQuality" })
            public static class DUMMYSTRUCTNAME extends Structure {
                /**
                 * For printers: Specifies the paper orientation. This member can be either DMORIENT_PORTRAIT or DMORIENT_LANDSCAPE.
                 *
                 * For displays: This member is not used for displays.
                 */
                public short dmOrientation;

                /**
                 * For printers, specifies the size of the paper to be printed on. This member must be zero if the
                 * length and width of the paper are specified by the dmPaperLength and
                 * <code>dmPaperWidth</code> members. Otherwise, the <code>dmPaperSize</code> member must be one of the
                 * <code>DMPAPER_XXX</code> constants defined in <code>wingdi.h</code>.
                 *
                 * For displays: This member is not used for displays.
                 */
                public short dmPaperSize;

                /**
                 * For printers: Specifies the length of the paper, in units of 1/10 of a millimeter. This value
                 * overrides the length of the paper specified by the <code>dmPaperSize</code> member, and is used if
                 * the paper is of a custom size, or if the device is a dot matrix printer, which can print a page of
                 * arbitrary length.
                 *
                 * For displays: This member is not used for displays.
                 */
                public short dmPaperLength;

                /**
                 * For printers: Specifies the width of the paper, in units of 1/10 of a millimeter. This value
                 * overrides the width of the paper specified by the <code>dmPaperSize</code> member. This member must
                 * be used if <code>dmPaperLength</code> is used.
                 *
                 * For displays: This member is not used for displays.
                 */
                public short dmPaperWidth;

                /**
                 * For printers: Specifies the percentage by which the image is to be scaled for printing.
                 * The image's page size is scaled to the physical page by a factor of <code>dmScale</code>/100.
                 *
                 * For example, a 17-inch by 22-inch image with a scale value of 100 requires 17x22-inch paper, while
                 * the same image with a scale value of 50 should print as half-sized and fit on letter-sized paper.
                 *
                 * For displays: This member is not used for displays.
                 */
                public short dmScale;

                /**
                 * For printers: Specifies the number of copies to be printed, if the device supports multiple copies.
                 *
                 * For displays: This member is not used for displays.
                 */
                public short dmCopies;

                /**
                 * For printers: Specifies the printer's default input bin (paper source). This must be one of the
                 * <code>DMBIN_XXX</code> constants defined in <code>wingdi.h</code>. If the specified constant is
                 * <code>DMBIN_FORMSOURCE</code>, the input bin should be selected automatically.
                 *
                 * To retrieve a list of the available paper sources for a printer, use the
                 * <code>DeviceCapabilities</code> function with the <code>DC_BINS</code> flag.
                 *
                 * This member can be one of the <code>DMBIN_XXX</code>> values, or it can be a device-specific value greater than or
                 * equal to <code>DMBIN_USER</code>.
                 *
                 * For displays: This member is not used for displays.
                 */
                public short dmDefaultSource;

                /**
                 * For printers: Specifies the printer resolution. The following negative constant values are defined in
                 * <code>wingdi.h</code>:
                 *
                 * <code>DMRES_HIGH</code>
                 * <code>DMRES_MEDIUM</code>
                 * <code>DMRES_LOW</code>
                 * <code>DMRES_DRAFT</code>
                 *
                 * For displays: This member is not used for displays.
                 */
                public short dmPrintQuality;
            }

            @FieldOrder({ "dmPosition", "dmDisplayOrientation", "dmDisplayFixedOutput" })
            public static class DUMMYSTRUCTNAME2 extends Structure {

                /**
                 * For displays: Specifies a <code>POINTL</code> structure containing the x- and y-coordinates of
                 * upper-left corner of the display, in desktop coordinates. This member is used to determine the
                 * relative position of monitors in a multiple monitor environment.
                 *
                 * For printers: This member is not used for printers.
                 *
                 * Note: This member is defined only for Windows XP and later.
                 */
                public POINT dmPosition;

                /**
                 * For displays: Specifies the orientation at which images should be presented. When the
                 * <code>DM_DISPLAYORIENTATION</code> bit is not set in the <code>dmFields</code> member, this member
                 * must be set to zero. When the <code>DM_DISPLAYORIENTATION</code> bit is set in the
                 * <code>dmFields</code> member, this member must be set to one of the <code>DMDO_XXX</code> values
                 *
                 * For printers: This member is not used for printers.
                 *
                 * Note: This member is defined only for Windows XP and later.
                 */
                public int dmDisplayOrientation;

                /**
                 * For displays: For fixed-resolution displays, specifies how the device can present a lower-resolution
                 * mode on a higher-resolution display. For example, if a display device's resolution is fixed at
                 * <code>1024 X 768</code>, and its mode is set to <code>640 x 480</code>, the device can either
                 * display a <code>640 X 480</code> image within the <code>1024 X 768</code> screen space, or stretch
                 * the <code>640 X 480</code> image to fill the larger screen space.
                 *
                 * When the <code>DM_DISPLAYFIXEDOUTPUT</code> bit is not set in the <code>dmFields</code> member,
                 * this member must be set to zero. When the <code>DM_DISPLAYFIXEDOUTPUT</code> bit is set in the
                 * <code>dmFields</code> member, this member must be set to one of the <code>DMDFO_XXX</code> values.
                 *
                 * For printers: This member is not used for printers.
                 *
                 * Note: This member is defined only for Windows XP and later.
                 */
                public int dmDisplayFixedOutput;
            }
        }

        public static class DUMMYUNIONNAME2 extends Union {
            /**
             * For displays: Specifies a display device's display mode. This member can be
             * <code>DM_GRAYSCALE</code>, <code>DM_INTERLACED</code> or <code>DMDISPLAYFLAGS_TEXTMODE</code>,
             * however values other than <code>DM_INTERLACED</code> are invalid for newer systems.
             *
             * For printers: This member is not used for printers.
             */
            public int dmDisplayFlags;

            /**
             * For printers: Specifies where the N-UP (pages per sheet) is done. It can be <code>DMNUP_SYSTEM</code> or
             * <code>DMNUP_ONEUP</code> being controlled by the spooler or the application, respectively.
             *
             * For displays: This member is not used for displays.
             */
            public int dmNup;
        }
    }

    @FieldOrder({"dwSize", "iType", "nCount", "nRgnSize", "rcBound"})
    class RGNDATAHEADER extends Structure {
        public int dwSize = size();
        public int iType = RDH_RECTANGLES; // required
        public int nCount;
        public int nRgnSize;
        public RECT rcBound;
    }

    @FieldOrder({"rdh", "Buffer"})
    class RGNDATA extends Structure {
        public RGNDATAHEADER rdh;
        public byte[] Buffer;

        public RGNDATA() {
            this(1);
        }
        public RGNDATA(int bufferSize) {
            Buffer = new byte[bufferSize];
            allocateMemory();
        }
    }

    HANDLE HGDI_ERROR = new HANDLE(Pointer.createConstant(0xFFFFFFFF));

    int DMCOLOR_MONOCHROME = 1;
    int DMCOLOR_COLOR = 2;

    /* TrueType options */
    /** print TT fonts as graphics **/
    int DMTT_BITMAP = 1;
    /** download TT fonts as soft fonts **/
    int DMTT_DOWNLOAD = 2;
    /** substitute device fonts for TT fonts **/
    int DMTT_SUBDEV = 3;
    /** download TT fonts as outline soft fonts **/
    int DMTT_DOWNLOAD_OUTLINE = 4;


    int DMORIENT_PORTRAIT = 1;
    int DMORIENT_LANDSCAPE = 2;

    /* device capabilities indices */
    int DC_FIELDS =1;
    int DC_PAPERS = 2;
    int DC_PAPERSIZE = 3;
    int DC_MINEXTENT = 4;
    int DC_MAXEXTENT = 5;
    int DC_BINS = 6;
    int DC_DUPLEX = 7;
    int DC_SIZE = 8;
    int DC_EXTRA = 9;
    int DC_VERSION = 10;
    int DC_DRIVER = 11;
    int DC_BINNAMES = 12;
    int DC_ENUMRESOLUTIONS = 13;
    int DC_FILEDEPENDENCIES = 14;
    int DC_TRUETYPE = 15;
    int DC_PAPERNAMES = 16;
    int DC_ORIENTATION = 17;
    int DC_COPIES = 18;
    int DC_BINADJUST = 19;
    int DC_EMF_COMPLIANT = 20;
    int DC_DATATYPE_PRODUCED = 21;
    int DC_COLLATE = 22;
    int DC_MANUFACTURER = 23;
    int DC_MODEL = 24;
    int DC_PERSONALITY = 25;
    int DC_PRINTRATE = 26;
    int DC_PRINTRATEUNIT = 27;
    int PRINTRATEUNIT_PPM = 1;
    int PRINTRATEUNIT_CPS = 2;
    int PRINTRATEUNIT_LPM = 3;
    int PRINTRATEUNIT_IPM = 4;
    int DC_PRINTERMEM = 28;
    int DC_MEDIAREADY = 29;
    int DC_STAPLE = 30;
    int DC_PRINTRATEPPM = 31;
    int DC_COLORDEVICE = 32;
    int DC_NUP = 33;
    int DC_MEDIATYPENAMES = 34;
    int DC_MEDIATYPES = 35;

    /* print qualities */
    int DMRES_DRAFT = -1;
    int DMRES_LOW = -2;
    int DMRES_MEDIUM = -3;
    int DMRES_HIGH = -4;

    /* bin selections */
    int DMBIN_UPPER = 0x0001;
    int DMBIN_LOWER = 0x0002;
    int DMBIN_MIDDLE = 0x0003;
    int DMBIN_MANUAL = 0x0004;
    int DMBIN_ENVELOPE = 0x0005;
    int DMBIN_ENVMANUAL = 0x0006;
    int DMBIN_AUTO = 0x0007;
    int DMBIN_TRACTOR = 0x0008;
    int DMBIN_SMALLFMT = 0x0009;
    int DMBIN_LARGEFMT = 0x000A;
    int DMBIN_LARGECAPACITY = 0x000B;
    int DMBIN_CASSETTE = 0x000E;
    int DMBIN_FORMSOURCE = 0x000F;

    /* DEVMODE dmDisplayOrientation specifiations */
    int DMDO_DEFAULT = 0;
    int DMDO_90 = 1;
    int DMDO_180 = 2;
    int DMDO_270 = 3;

    /* DEVMODE dmDisplayFixedOutput specifiations */
    int DMDFO_DEFAULT = 0;
    int DMDFO_STRETCH = 1;
    int DMDFO_CENTER = 2;

    /* DEVMODE dmDisplayFlags flags */
    int DM_GRAYSCALE = 0x00000001;
    int DM_INTERLACED = 0x00000002;
    int DMDISPLAYFLAGS_TEXTMODE = 0x00000004;

    /* DEVMODE dmNup: multiple logical page per physical page options */
    /** The print spooler does the NUP (pages per sheet) **/
    int DMNUP_SYSTEM = 1;
    /** The application does the NUP (pages per sheet) **/
    int DMNUP_ONEUP = 2;

    /* ICM methods */
    /** ICM disabled **/
    int DMICMMETHOD_NONE = 1;
    /** ICM handled by system **/
    int DMICMMETHOD_SYSTEM = 2;
    /** ICM handled by driver **/
    int DMICMMETHOD_DRIVER = 3;
    /** ICM handled by device **/
    int DMICMMETHOD_DEVICE = 4;

    /* ICM Intents */
    /** Maximize color saturation **/
    int DMICM_SATURATE = 1;
    /** Maximize color contrast **/
    int DMICM_CONTRAST = 2;
    /** Use specific color metric **/
    int DMICM_COLORIMETRIC  = 3;
    /** Use specific color metric **/
    int DMICM_ABS_COLORIMETRIC =  4;
    /** Device-specific intents start here **/
    int DMICM_USER = 256;

    /* Media types */
    /** Standard paper **/
    int DMMEDIA_STANDARD = 1;
    /** Transparency **/
    int DMMEDIA_TRANSPARENCY = 2;
    /** Glossy paper **/
    int DMMEDIA_GLOSSY = 3;
    /** Device-specific media start here */
    int DMMEDIA_USER = 256;

    /* Dither types */
    /** No dithering **/
    int DMDITHER_NONE= 1;
    /** Dither with a coarse brush **/
    int DMDITHER_COARSE     = 2;
    /** Dither with a fine brush **/
    int DMDITHER_FINE= 3;
    /** LineArt dithering **/
    int DMDITHER_LINEART   = 4;
    /** LineArt dithering **/
    int DMDITHER_ERRORDIFFUSION = 5;
    /** LineArt dithering **/
    int DMDITHER_RESERVED6 = 6;
    /** LineArt dithering **/
    int DMDITHER_RESERVED7 = 7;
    /** LineArt dithering **/
    int DMDITHER_RESERVED8 = 8;
    /** LineArt dithering **/
    int DMDITHER_RESERVED9 = 9;
    /** Device does grayscaling **/
    int DMDITHER_GRAYSCALE  = 10;
    /** Device-specific dithers start here **/
    int DMDITHER_USER = 256;

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

    @FieldOrder({"biSize", "biWidth", "biHeight", "biPlanes", "biBitCount",
        "biCompression", "biSizeImage", "biXPelsPerMeter", "biYPelsPerMeter",
        "biClrUsed", "biClrImportant"})
    class BITMAPINFOHEADER extends Structure {
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

    @FieldOrder({"rgbBlue", "rgbGreen", "rgbRed", "rgbReserved"})
    class RGBQUAD extends Structure {
        public byte rgbBlue;
        public byte rgbGreen;
        public byte rgbRed;
        public byte rgbReserved = 0;
    }

    @FieldOrder({"bmiHeader", "bmiColors"})
    class BITMAPINFO extends Structure {

        public BITMAPINFOHEADER bmiHeader = new BITMAPINFOHEADER();
        public RGBQUAD[] bmiColors = new RGBQUAD[1];
        public BITMAPINFO() {
            this(1);
        }
        public BITMAPINFO(int size) {
            bmiColors = new RGBQUAD[size];
        }
    }

    @FieldOrder({"fIcon", "xHotspot", "yHotspot", "hbmMask", "hbmColor"})
    class ICONINFO extends Structure {
        public boolean fIcon;
        public int xHotspot;
        public int yHotspot;
        public HBITMAP hbmMask;
        public HBITMAP hbmColor;
    }

    @FieldOrder({"bmType", "bmWidth", "bmHeight", "bmWidthBytes", "bmPlanes", "bmBitsPixel", "bmBits"})
    class BITMAP extends Structure {
        public NativeLong bmType;
        public NativeLong bmWidth;
        public NativeLong bmHeight;
        public NativeLong bmWidthBytes;
        public short bmPlanes;
        public short bmBitsPixel;
        public Pointer bmBits;
    }

    @FieldOrder({"dsBm", "dsBmih", "dsBitfields", "dshSection", "dsOffset"})
    class DIBSECTION extends Structure {
        public BITMAP           dsBm;
        public BITMAPINFOHEADER dsBmih;
        public int[]            dsBitfields = new int[3];
        public HANDLE           dshSection;
        public int              dsOffset;
    }

    int DIB_RGB_COLORS = 0;
    int DIB_PAL_COLORS = 1;

    /**
     * The PIXELFORMATDESCRIPTOR structure describes the pixel format of a drawing surface.
     */
    @FieldOrder({"nSize", "nVersion", "dwFlags", "iPixelType",
        "cColorBits", "cRedBits", "cRedShift", "cGreenBits", "cGreenShift", "cBlueBits", "cBlueShift", "cAlphaBits", "cAlphaShift",
        "cAccumBits", "cAccumRedBits", "cAccumGreenBits", "cAccumBlueBits", "cAccumAlphaBits",
        "cDepthBits", "cStencilBits", "cAuxBuffers", "iLayerType", "bReserved", "dwLayerMask", "dwVisibleMask", "dwDamageMask"})
    class PIXELFORMATDESCRIPTOR extends Structure {
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
    }
}
