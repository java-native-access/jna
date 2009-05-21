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
package com.sun.jna.examples.unix;

import com.sun.jna.*;
import com.sun.jna.ptr.*;

/** Definition (incomplete) of the X library. */
public interface X11 extends Library {

    class VisualID extends NativeLong {
        public VisualID() { }
        public VisualID(long value) { super(value); }
    }

    class XID extends NativeLong {
        public static final XID None = null;
        public XID() { this(0); }
        public XID(long id) { super(id); }
        protected boolean isNone(Object o) {
            return o == null
                || (o instanceof Number
                    && ((Number)o).longValue() == X11.None);
        }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new XID(((Number)nativeValue).longValue());
        }
        public String toString() {
            return "0x" + Long.toHexString(longValue());
        }
    }
    class Atom extends XID {
        public static final Atom None = null;
        public Atom() { }
        public Atom(long id) { super(id); }
        /** Return constants for predefined <code>Atom</code> values. */
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            long value = ((Number)nativeValue).longValue();
            if (value <= Integer.MAX_VALUE) {
                switch((int)value) {
                case 0: return None;
                case 1: return XA_PRIMARY;
                case 2: return XA_SECONDARY;
                case 3: return XA_ARC;
                case 4: return XA_ATOM;
                case 5: return XA_BITMAP;
                case 6: return XA_CARDINAL;
                case 7: return XA_COLORMAP;
                case 8: return XA_CURSOR;
                case 9: return XA_CUT_BUFFER0;
                case 10: return XA_CUT_BUFFER1;
                case 11: return XA_CUT_BUFFER2;
                case 12: return XA_CUT_BUFFER3;
                case 13: return XA_CUT_BUFFER4;
                case 14: return XA_CUT_BUFFER5;
                case 15: return XA_CUT_BUFFER6;
                case 16: return XA_CUT_BUFFER7;
                case 17: return XA_DRAWABLE;
                case 18: return XA_FONT;
                case 19: return XA_INTEGER;
                case 20: return XA_PIXMAP;
                case 21: return XA_POINT;
                case 22: return XA_RECTANGLE;
                case 23: return XA_RESOURCE_MANAGER;
                case 24: return XA_RGB_COLOR_MAP;
                case 25: return XA_RGB_BEST_MAP;
                case 26: return XA_RGB_BLUE_MAP;
                case 27: return XA_RGB_DEFAULT_MAP;
                case 28: return XA_RGB_GRAY_MAP;
                case 29: return XA_RGB_GREEN_MAP;
                case 30: return XA_RGB_RED_MAP;
                case 31: return XA_STRING;
                case 32: return XA_VISUALID;
                case 33: return XA_WINDOW;
                case 34: return XA_WM_COMMAND;
                case 35: return XA_WM_HINTS;
                case 36: return XA_WM_CLIENT_MACHINE;
                case 37: return XA_WM_ICON_NAME;
                case 38: return XA_WM_ICON_SIZE;
                case 39: return XA_WM_NAME;
                case 40: return XA_WM_NORMAL_HINTS;
                case 41: return XA_WM_SIZE_HINTS;
                case 42: return XA_WM_ZOOM_HINTS;
                case 43: return XA_MIN_SPACE;
                case 44: return XA_NORM_SPACE;
                case 45: return XA_MAX_SPACE;
                case 46: return XA_END_SPACE;
                case 47: return XA_SUPERSCRIPT_X;
                case 48: return XA_SUPERSCRIPT_Y;
                case 49: return XA_SUBSCRIPT_X;
                case 50: return XA_SUBSCRIPT_Y;
                case 51: return XA_UNDERLINE_POSITION;
                case 52: return XA_UNDERLINE_THICKNESS;
                case 53: return XA_STRIKEOUT_ASCENT;
                case 54: return XA_STRIKEOUT_DESCENT;
                case 55: return XA_ITALIC_ANGLE;
                case 56: return XA_X_HEIGHT;
                case 57: return XA_QUAD_WIDTH;
                case 58: return XA_WEIGHT;
                case 59: return XA_POINT_SIZE;
                case 60: return XA_RESOLUTION;
                case 61: return XA_COPYRIGHT;
                case 62: return XA_NOTICE;
                case 63: return XA_FONT_NAME;
                case 64: return XA_FAMILY_NAME;
                case 65: return XA_FULL_NAME;
                case 66: return XA_CAP_HEIGHT;
                case 67: return XA_WM_CLASS;
                case 68: return XA_WM_TRANSIENT_FOR;
                default:
                }
            }
            return new Atom(value);
        }
    }
    class AtomByReference extends ByReference {
        public AtomByReference() { super(XID.SIZE); }
        public Atom getValue() {
            NativeLong value = getPointer().getNativeLong(0);
            return (Atom)new Atom().fromNative(value, null);
        }
    }
    class Colormap extends XID {
        public static final Colormap None = null;
        public Colormap() { }
        public Colormap(long id) { super(id); }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new Colormap(((Number)nativeValue).longValue());
        }
    }
    class Font extends XID {
        public static final Font None = null;
        public Font() { }
        public Font(long id) { super(id); }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new Font(((Number)nativeValue).longValue());
        }
    }
    class Cursor extends XID {
        public static final Cursor None = null;
        public Cursor() { }
        public Cursor(long id) { super(id); }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new Cursor(((Number)nativeValue).longValue());
        }
    }
    class KeySym extends XID {
        public static final KeySym None = null;
        public KeySym() { }
        public KeySym(long id) { super(id); }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new KeySym(((Number)nativeValue).longValue());
        }
    }
    class Drawable extends XID {
        public static final Drawable None = null;
        public Drawable() { }
        public Drawable(long id) { super(id); }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new Drawable(((Number)nativeValue).longValue());
        }
    }
    class Window extends Drawable {
        public static final Window None = null;
        public Window() { }
        public Window(long id) { super(id); }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new Window(((Number)nativeValue).longValue());
        }
    }
    class WindowByReference extends ByReference {
        public WindowByReference() { super(XID.SIZE); }
        public Window getValue() {
            NativeLong value = getPointer().getNativeLong(0);
            return value.longValue() == X11.None
                ? Window.None : new Window(value.longValue());
        }
    }
    class Pixmap extends Drawable {
        public static final Pixmap None = null;
        public Pixmap() { }
        public Pixmap(long id) { super(id); }
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue))
                return None;
            return new Pixmap(((Number)nativeValue).longValue());
        }
    }
    // TODO: define structure
    class Display extends PointerType { }
    // TODO: define structure
    class Visual extends PointerType {
        public NativeLong getVisualID() {
            if (getPointer() != null)
                return getPointer().getNativeLong(Native.POINTER_SIZE);
            return new NativeLong(0);
        }
        public String toString() {
            return "Visual: VisualID=0x" + Long.toHexString(getVisualID().longValue());
        }
    }
    // TODO: define structure
    class Screen extends PointerType { }
    // TODO: define structure
    class GC extends PointerType { }
    // TODO: define structure
    class XImage extends PointerType { }

    /** Definition (incomplete) of the Xext library. */
    interface Xext extends Library {
        Xext INSTANCE = (Xext)Native.loadLibrary("Xext", Xext.class);
        // Shape Kinds
        int ShapeBounding = 0;
        int ShapeClip = 1;
        int ShapeInput = 2;
        // Operations
        int ShapeSet = 0;
        int ShapeUnion = 1;
        int ShapeIntersect = 2;
        int ShapeSubtract = 3;
        int ShapeInvert = 4;

        void XShapeCombineMask(Display display, Window window, int dest_kind,
                               int x_off, int y_off, Pixmap src, int op);
    }

    /** Definition (incomplete) of the Xrender library. */
    interface Xrender extends Library {
        Xrender INSTANCE = (Xrender)Native.loadLibrary("Xrender", Xrender.class);
        class XRenderDirectFormat extends Structure {
            public short red, redMask;
            public short green, greenMask;
            public short blue, blueMask;
            public short alpha, alphaMask;
        }
        class PictFormat extends NativeLong {
            public PictFormat(long value) { super(value); }
            public PictFormat() { }
        }
        class XRenderPictFormat extends Structure {
            public PictFormat id;
            public int type;
            public int depth;
            public XRenderDirectFormat direct;
            public Colormap colormap;
        }
        int PictTypeIndexed = 0x0;
        int PictTypeDirect = 0x1;
        XRenderPictFormat XRenderFindVisualFormat(Display display, Visual visual);
    }

    /** Definition of the Xevie library. */
    interface Xevie extends Library {
        /** Instance of Xevie. Note: This extension has been removed from xorg/xserver on Oct 22, 2008 because it is broken and maintainerless. */
        Xevie INSTANCE = (Xevie)Native.loadLibrary("Xevie", Xevie.class);
        int XEVIE_UNMODIFIED = 0;
        int XEVIE_MODIFIED   = 1;
        // Bool XevieQueryVersion (Display* display, int* major_version, int* minor_version);
        boolean XevieQueryVersion (Display display, IntByReference major_version, IntByReference minor_version);
        // Status XevieStart (Display* display);
        int XevieStart (Display display);
        // Status XevieEnd (Display* display);
        int XevieEnd (Display display);
        // Status XevieSendEvent (Display* display, XEvent* event, int data_type);
        int XevieSendEvent (Display display, XEvent event, int data_type);
        // Status XevieSelectInput (Display* display, NativeLong event_mask);
        int XevieSelectInput (Display display, NativeLong event_mask);
    }

    /** Definition of the XTest library. */
    interface XTest extends Library {
        XTest INSTANCE = (XTest)Native.loadLibrary("Xtst", XTest.class);///usr/lib/libxcb-xtest.so.0
        boolean XTestQueryExtension(Display display, IntByReference event_basep, IntByReference error_basep, IntByReference majorp, IntByReference minorp);
        boolean XTestCompareCursorWithWindow(Display display, Window window, Cursor cursor);
        boolean XTestCompareCurrentCursorWithWindow(Display display, Window window);
        // extern int XTestFakeKeyEvent(Display* display, unsigned int keycode, Bool is_press, unsigned long delay);
        int XTestFakeKeyEvent(Display display, int keycode, boolean is_press, NativeLong delay);
        int XTestFakeButtonEvent(Display display, int button, boolean is_press, NativeLong delay);
        int XTestFakeMotionEvent(Display display, int screen, int x, int y, NativeLong delay);
        int XTestFakeRelativeMotionEvent(Display display, int x, int y, NativeLong delay);
        int XTestFakeDeviceKeyEvent(Display display, XDeviceByReference dev, int keycode, boolean is_press, IntByReference axes, int n_axes, NativeLong delay);
        int XTestFakeDeviceButtonEvent(Display display, XDeviceByReference dev, int button, boolean is_press, IntByReference axes, int n_axes, NativeLong delay);
        int XTestFakeProximityEvent(Display display, XDeviceByReference dev, boolean in_prox, IntByReference axes, int n_axes, NativeLong delay);
        int XTestFakeDeviceMotionEvent(Display display, XDeviceByReference dev, boolean is_relative, int first_axis, IntByReference axes, int n_axes, NativeLong delay);
        int XTestGrabControl(Display display, boolean impervious);
        //void XTestSetGContextOfGC(GC gc, GContext gid);
        void XTestSetVisualIDOfVisual(Visual visual, VisualID visualid);
        int XTestDiscard(Display display);
    }

    class XInputClassInfoByReference extends Structure implements Structure.ByReference {
        public byte input_class;
        public byte event_type_base;
    }

    class XDeviceByReference extends Structure implements Structure.ByReference {
        public XID device_id;
        public int num_classes;
        public XInputClassInfoByReference classes;
    }

    X11 INSTANCE = (X11)Native.loadLibrary("X11", X11.class);

    /*
      typedef struct {
        long flags;     // marks which fields in this structure are defined
        Bool input;     // does this application rely on the window manager to
                        // get keyboard input?
        int initial_state;      // see below
        Pixmap icon_pixmap;     // pixmap to be used as icon
        Window icon_window;     // window to be used as icon
        int icon_x, icon_y;     // initial position of icon
        Pixmap icon_mask;       // icon mask bitmap
        XID window_group;       // id of related window group
        // this structure may be extended in the future
      } XWMHints;
    */
    class XWMHints extends Structure {
        public NativeLong flags;
        public boolean input;
        public int initial_state;
        public Pixmap icon_pixmap;
        public Window icon_window;
        public int icon_x, icon_y;
        public Pixmap icon_mask;
        public XID window_group;
    }

    /*
      typedef struct {
        unsigned char *value;   // same as Property routines
        Atom encoding;          // prop type
        int format;             // prop data format: 8, 16, or 32
        unsigned long nitems;   // number of data items in value
      } XTextProperty;
    */
    class XTextProperty extends Structure {
        public String value;
        public Atom encoding;
        public int format;
        public NativeLong nitems;
    }

    /*
      typedef struct {
        long flags;     // marks which fields in this structure are defined
        int x, y;       // obsolete for new window mgrs, but clients
        int width, height;      /// should set so old wm's don't mess up
        int min_width, min_height;
        int max_width, max_height;
        int width_inc, height_inc;
        struct {
          int x;        // numerator
          int y;        // denominator
        } min_aspect, max_aspect;
        int base_width, base_height;            // added by ICCCM version 1
        int win_gravity;                        // added by ICCCM version 1
      } XSizeHints;
     */
    class XSizeHints extends Structure {
        public NativeLong flags;
        public int x, y;
        public int width, height;
        public int min_width, min_height;
        public int max_width, max_height;
        public int width_inc, height_inc;
        public static class Aspect extends Structure {
            public int x; // numerator
            public int y; // denominator
        }
        public Aspect min_aspect, max_aspect;
        public int base_width, base_height;
        public int win_gravity;
    }

    /*
      typedef struct {
        int x, y;               // location of window
        int width, height;      // width and height of window
        int border_width;       // border width of window
        int depth;              // depth of window
        Visual *visual;         // the associated visual structure
        Window root;            // root of screen containing window
#if defined(__cplusplus) || defined(c_plusplus)
        int c_class;            // C++ InputOutput, InputOnly
#else
        int class;              // InputOutput, InputOnly
#endif
        int bit_gravity;        // one of bit gravity values
        int win_gravity;        // one of the window gravity values
        int backing_store;      // NotUseful, WhenMapped, Always
        unsigned long backing_planes;// planes to be preserved if possible
        unsigned long backing_pixel;// value to be used when restoring planes
        Bool save_under;        // boolean, should bits under be saved?
        Colormap colormap;      // color map to be associated with window
        Bool map_installed;     // boolean, is color map currently installed
        int map_state;          // IsUnmapped, IsUnviewable, IsViewable
        long all_event_masks;   // set of events all people have interest in
        long your_event_mask;   // my event mask
        long do_not_propagate_mask; // set of events that should not propagate
        Bool override_redirect; // boolean value for override-redirect
        Screen *screen;         // back pointer to correct screen
      } XWindowAttributes;
     */
    class XWindowAttributes extends Structure {
        public int x, y;
        public int width, height;
        public int border_width;
        public int depth;
        public Visual visual;
        public Window root;
        public int c_class;
        public int bit_gravity;
        public int win_gravity;
        public int backing_store;
        public NativeLong backing_planes;
        public NativeLong backing_pixel;
        public boolean save_under;
        public Colormap colormap;
        public boolean map_installed;
        public int map_state;
        public NativeLong all_event_masks;
        public NativeLong your_event_mask;
        public NativeLong do_not_propagate_mask;
        public boolean override_redirect;
        public Screen screen;
    }

    /*
      typedef struct {
        Pixmap background_pixmap;       // background or None or ParentRelative
        unsigned long background_pixel; // background pixel
        Pixmap border_pixmap;   // border of the window
        unsigned long border_pixel;     // border pixel value
        int bit_gravity;                // one of bit gravity values
        int win_gravity;                // one of the window gravity values
        int backing_store;              // NotUseful, WhenMapped, Always
        unsigned long backing_planes;// planes to be preseved if possible
        unsigned long backing_pixel;// value to use in restoring planes
        Bool save_under;                // should bits under be saved? (popups)
        long event_mask;                // set of events that should be saved
        long do_not_propagate_mask;     // set of events that should not propagate
        Bool override_redirect; // boolean value for override-redirect
        Colormap colormap;              // color map to be associated with window
        Cursor cursor;          // cursor to be displayed (or None)
      } XSetWindowAttributes;
     */
    class XSetWindowAttributes extends Structure {
        public Pixmap background_pixmap;
        public NativeLong background_pixel;
        public Pixmap border_pixmap;
        public NativeLong border_pixel;
        public int bit_gravity;
        public int win_gravity;
        public int backing_store;
        public NativeLong backing_planes;
        public NativeLong backing_pixel;
        public boolean save_under;
        public NativeLong event_mask;
        public NativeLong do_not_propagate_mask;
        public boolean override_redirect;
        public Colormap colormap;
        public Cursor cursor;
    }

    int XK_0 = 0x30;
    int XK_9 = 0x39;
    int XK_A = 0x41;
    int XK_Z = 0x5a;
    int XK_a = 0x61;
    int XK_z = 0x7a;
    int XK_Shift_L = 0xffe1;
    int XK_Shift_R = 0xffe1;
    int XK_Control_L = 0xffe3;
    int XK_Control_R = 0xffe4;
    int XK_CapsLock = 0xffe5;
    int XK_ShiftLock = 0xffe6;
    int XK_Meta_L = 0xffe7;
    int XK_Meta_R = 0xffe8;
    int XK_Alt_L = 0xffe9;
    int XK_Alt_R = 0xffea;

    int VisualNoMask = 0x0;
    int VisualIDMask = 0x1;
    int VisualScreenMask = 0x2;
    int VisualDepthMask = 0x4;
    int VisualClassMask = 0x8;
    int VisualRedMaskMask = 0x10;
    int VisualGreenMaskMask = 0x20;
    int VisualBlueMaskMask = 0x40;
    int VisualColormapSizeMask = 0x80;
    int VisualBitsPerRGBMask = 0x100;
    int VisualAllMask = 0x1FF;

    class XVisualInfo extends Structure {
        public Visual visual;
        public VisualID visualid;
        public int screen;
        public int depth;
        public int c_class;
        public NativeLong red_mask;
        public NativeLong green_mask;
        public NativeLong blue_mask;
        public int colormap_size;
        public int bits_per_rgb;
    }
    class XPoint extends Structure {
        public short x, y;
        public XPoint() { }
        public XPoint(short x, short y) {
            this.x = x;
            this.y = y;
        }
    }
    class XRectangle extends Structure {
        public short x, y;
        public short width, height;
        public XRectangle() { }
        public XRectangle(short x, short y, short width, short height) {
            this.x = x; this.y = y;
            this.width = width; this.height = height;
        }
    }

    Atom XA_PRIMARY = new Atom(1);
    Atom XA_SECONDARY = new Atom(2);
    Atom XA_ARC = new Atom(3);
    Atom XA_ATOM = new Atom(4);
    Atom XA_BITMAP = new Atom(5);
    Atom XA_CARDINAL = new Atom(6);
    Atom XA_COLORMAP = new Atom(7);
    Atom XA_CURSOR = new Atom(8);
    Atom XA_CUT_BUFFER0 = new Atom(9);
    Atom XA_CUT_BUFFER1 = new Atom(10);
    Atom XA_CUT_BUFFER2 = new Atom(11);
    Atom XA_CUT_BUFFER3 = new Atom(12);
    Atom XA_CUT_BUFFER4 = new Atom(13);
    Atom XA_CUT_BUFFER5 = new Atom(14);
    Atom XA_CUT_BUFFER6 = new Atom(15);
    Atom XA_CUT_BUFFER7 = new Atom(16);
    Atom XA_DRAWABLE = new Atom(17);
    Atom XA_FONT = new Atom(18);
    Atom XA_INTEGER = new Atom(19);
    Atom XA_PIXMAP = new Atom(20);
    Atom XA_POINT = new Atom(21);
    Atom XA_RECTANGLE = new Atom(22);
    Atom XA_RESOURCE_MANAGER = new Atom(23);
    Atom XA_RGB_COLOR_MAP = new Atom(24);
    Atom XA_RGB_BEST_MAP = new Atom(25);
    Atom XA_RGB_BLUE_MAP = new Atom(26);
    Atom XA_RGB_DEFAULT_MAP = new Atom(27);
    Atom XA_RGB_GRAY_MAP = new Atom(28);
    Atom XA_RGB_GREEN_MAP = new Atom(29);
    Atom XA_RGB_RED_MAP = new Atom(30);
    Atom XA_STRING = new Atom(31);
    Atom XA_VISUALID = new Atom(32);
    Atom XA_WINDOW = new Atom(33);
    Atom XA_WM_COMMAND = new Atom(34);
    Atom XA_WM_HINTS = new Atom(35);
    Atom XA_WM_CLIENT_MACHINE = new Atom(36);
    Atom XA_WM_ICON_NAME = new Atom(37);
    Atom XA_WM_ICON_SIZE = new Atom(38);
    Atom XA_WM_NAME = new Atom(39);
    Atom XA_WM_NORMAL_HINTS = new Atom(40);
    Atom XA_WM_SIZE_HINTS = new Atom(41);
    Atom XA_WM_ZOOM_HINTS = new Atom(42);
    Atom XA_MIN_SPACE = new Atom(43);
    Atom XA_NORM_SPACE = new Atom(44);
    Atom XA_MAX_SPACE = new Atom(45);
    Atom XA_END_SPACE = new Atom(46);
    Atom XA_SUPERSCRIPT_X = new Atom(47);
    Atom XA_SUPERSCRIPT_Y = new Atom(48);
    Atom XA_SUBSCRIPT_X = new Atom(49);
    Atom XA_SUBSCRIPT_Y = new Atom(50);
    Atom XA_UNDERLINE_POSITION = new Atom(51);
    Atom XA_UNDERLINE_THICKNESS = new Atom(52);
    Atom XA_STRIKEOUT_ASCENT = new Atom(53);
    Atom XA_STRIKEOUT_DESCENT = new Atom(54);
    Atom XA_ITALIC_ANGLE = new Atom(55);
    Atom XA_X_HEIGHT = new Atom(56);
    Atom XA_QUAD_WIDTH = new Atom(57);
    Atom XA_WEIGHT = new Atom(58);
    Atom XA_POINT_SIZE = new Atom(59);
    Atom XA_RESOLUTION = new Atom(60);
    Atom XA_COPYRIGHT = new Atom(61);
    Atom XA_NOTICE = new Atom(62);
    Atom XA_FONT_NAME = new Atom(63);
    Atom XA_FAMILY_NAME = new Atom(64);
    Atom XA_FULL_NAME = new Atom(65);
    Atom XA_CAP_HEIGHT = new Atom(66);
    Atom XA_WM_CLASS = new Atom(67);
    Atom XA_WM_TRANSIENT_FOR = new Atom(68);
    Atom XA_LAST_PREDEFINED = XA_WM_TRANSIENT_FOR;

    Display XOpenDisplay(String name);
    int XGetErrorText(Display display, int code, byte[] buffer, int len);
    int XDefaultScreen(Display display);
    Screen DefaultScreenOfDisplay(Display display);
    Visual XDefaultVisual(Display display, int screen);
    Colormap XDefaultColormap(Display display, int screen);
    int XDisplayWidth(Display display, int screen);
    int XDisplayHeight(Display display, int screen);
    Window XDefaultRootWindow(Display display);
    Window XRootWindow(Display display, int screen);
    int XAllocNamedColor(Display display, int colormap, String color_name,
                         Pointer screen_def_return, Pointer exact_def_return);
    XSizeHints XAllocSizeHints();
    void XSetWMProperties(Display display, Window window, String window_name,
                          String icon_name, String[] argv, int argc,
                          XSizeHints normal_hints, Pointer wm_hints,
                          Pointer class_hints);
    int XFree(Pointer data);
    Window XCreateSimpleWindow(Display display, Window parent, int x, int y,
                               int width, int height, int border_width,
                               int border, int background);
    Pixmap XCreateBitmapFromData(Display display, Window window, Pointer data,
                                 int width, int height);
    int XMapWindow(Display display, Window window);
    int XMapRaised(Display display, Window window);
    int XMapSubwindows(Display display, Window window);

    /** Flushes the output buffer. Most client applications need not use this function because the output buffer is automatically flushed as needed by calls to XPending, XNextEvent, and XWindowEvent. Events generated by the server may be enqueued into the library's event queue. */
    int XFlush(Display display);
    /** Flushes the output buffer and then waits until all requests have been received and processed by the X server. Any errors generated must be handled by the error handler. For each protocol error received by Xlib, XSync calls the client application's error handling routine (see section 11.8.2). Any events generated by the server are enqueued into the library's event queue.<br/>Finally, if you passed False, XSync does not discard the events in the queue. If you passed True, XSync discards all events in the queue, including those events that were on the queue before XSync was called. Client applications seldom need to call XSync. */
    int XSync(Display display, boolean discard);
    /** If mode is QueuedAlready, XEventsQueued returns the number of events already in the event queue (and never performs a system call). If mode is QueuedAfterFlush, XEventsQueued returns the number of events already in the queue if the number is nonzero. If there are no events in the queue, XEventsQueued flushes the output buffer, attempts to read more events out of the application's connection, and returns the number read. If mode is QueuedAfterReading, XEventsQueued returns the number of events already in the queue if the number is nonzero. If there are no events in the queue, XEventsQueued attempts to read more events out of the application's connection without flushing the output buffer and returns the number read.<br/>XEventsQueued always returns immediately without I/O if there are events already in the queue. XEventsQueued with mode QueuedAfterFlush is identical in behavior to XPending. XEventsQueued with mode QueuedAlready is identical to the XQLength function. */
    int XEventsQueued(Display display, int mode);
    /** Returns the number of events that have been received from the X server but have not been removed from the event queue. XPending  is identical to XEventsQueued with the mode QueuedAfterFlush specified. */
    int XPending(Display display);

    int XUnmapWindow(Display display, Window window);
    int XDestroyWindow(Display display, Window window);
    int XCloseDisplay(Display display);
    int XClearWindow(Display display, Window window);
    int XClearArea(Display display, Window window, int x, int y, int w, int h, int exposures);
    Pixmap XCreatePixmap(Display display, Drawable drawable, int width, int height, int depth);
    int XFreePixmap(Display display, Pixmap pixmap);
    class XGCValues extends Structure {
        public int function;            /* logical operation */
        public NativeLong plane_mask;/* plane mask */
        public NativeLong foreground;/* foreground pixel */
        public NativeLong background;/* background pixel */
        public int line_width;          /* line width (in pixels) */
        public int line_style;          /* LineSolid, LineOnOffDash, LineDoubleDash*/
        public int cap_style;           /* CapNotLast, CapButt, CapRound, CapProjecting */
        public int join_style;          /* JoinMiter, JoinRound, JoinBevel */
        public int fill_style;          /* FillSolid, FillTiled, FillStippled FillOpaqueStippled*/
        public int fill_rule;           /* EvenOddRule, WindingRule */
        public int arc_mode;            /* ArcChord, ArcPieSlice */
        public Pixmap tile;             /* tile pixmap for tiling operations */
        public Pixmap stipple;          /* stipple 1 plane pixmap for stippling */
        public int ts_x_origin;         /* offset for tile or stipple operations */
        public int ts_y_origin;
        public Font font;               /* default text font for text operations */
        public int subwindow_mode;      /* ClipByChildren, IncludeInferiors */
        public boolean graphics_exposures; /* boolean, should exposures be generated */
        public int clip_x_origin;       /* origin for clipping */
        public int clip_y_origin;
        public Pixmap clip_mask;        /* bitmap clipping; other calls for rects */
        public int dash_offset;         /* patterned/dashed line information */
        public byte dashes;
    }
    GC XCreateGC(Display display, Drawable drawable, NativeLong mask, XGCValues values);
    int XSetFillRule(Display display, GC gc, int fill_rule);
    int XFreeGC(Display display, GC gc);
    int XDrawPoint(Display display, Drawable drawable, GC gc, int x, int y);
    int XDrawPoints(Display display, Drawable drawable, GC gc,
                    XPoint[] points, int npoints, int mode);
    int XFillRectangle(Display display, Drawable drawable, GC gc,
                       int x, int y, int width, int height);
    int XFillRectangles(Display display, Drawable drawable, GC gc,
                        XRectangle[] rectangles, int nrectangles);
    int XSetForeground(Display display, GC gc, NativeLong color);
    int XSetBackground(Display display, GC gc, NativeLong color);
    int XFillArc(Display display, Drawable drawable, GC gc, int x, int y,
                 int width, int height, int angle1, int angle2);
    int XFillPolygon(Display dpy, Drawable drawable, GC gc, XPoint[] points,
                     int npoints, int shape, int mode);
    int XQueryTree(Display display, Window window, WindowByReference root,
                   WindowByReference parent, PointerByReference children,
                   IntByReference childCount);
    boolean XQueryPointer(Display display, Window window,
                          WindowByReference root_return,
                          WindowByReference child_return,
                          IntByReference root_x_return,
                          IntByReference root_y_return,
                          IntByReference win_x_return,
                          IntByReference win_y_return,
                          IntByReference mask_return);
    int XGetWindowAttributes(Display display, Window window, XWindowAttributes attributes);
    int XChangeWindowAttributes(Display display, Window window, NativeLong valuemask, XSetWindowAttributes attributes);
    // Status XGetGeometry(Display *display, Drawable d, Window *root_return, int *x_return, int *y_return, unsigned int *width_return,
    //                     unsigned int *height_return, unsigned int *border_width_return, unsigned int *depth_return); 
    int XGetGeometry(Display display, Drawable d, WindowByReference w, IntByReference x, IntByReference y, IntByReference width,
                     IntByReference heigth, IntByReference border_width, IntByReference depth);
    // Bool XTranslateCoordinates(Display *display, Window src_w, dest_w, int src_x, int src_y,
    //                            int *dest_x_return, int *dest_y_return, Window *child_return); 
    boolean XTranslateCoordinates(Display display, Window src_w, Window dest_w, int src_x, int src_y,
                                  IntByReference dest_x_return, IntByReference dest_y_return, WindowByReference child_return);

    /*****************************************************************
     * RESERVED RESOURCE AND CONSTANT DEFINITIONS
     *****************************************************************/

    int None = 0;        /* universal null resource or null atom */
    int ParentRelative = 1;        /* background pixmap in CreateWindow and ChangeWindowAttributes */
    int CopyFromParent = 0;            /* border pixmap in CreateWindow
                                          and ChangeWindowAttributes
                                          special VisualID and special window
                                          class passed to CreateWindow */
    int PointerWindow = 0;    /* destination window in SendEvent */
    int InputFocus = 1;    /* destination window in SendEvent */
    int PointerRoot = 1;    /* focus window in SetInputFocus */
    int AnyPropertyType = 0;    /* special Atom, passed to GetProperty */
    int AnyKey = 0;        /* special Key Code, passed to GrabKey */
    int AnyButton = 0;    /* special Button Code, passed to GrabButton */
    int AllTemporary = 0;    /* special Resource ID passed to KillClient */
    int CurrentTime = 0;    /* special Time */
    int NoSymbol = 0;        /* special KeySym */

    /*****************************************************************
     * EVENT DEFINITIONS
     *****************************************************************/

    /* Input Event Masks. Used as event-mask window attribute and as arguments
       to Grab requests.  Not to be confused with event names.  */
    int NoEventMask = 0;
    int KeyPressMask = (1<<0);
    int KeyReleaseMask = (1<<1);
    int ButtonPressMask = (1<<2);
    int ButtonReleaseMask = (1<<3);
    int EnterWindowMask = (1<<4);
    int LeaveWindowMask = (1<<5);
    int PointerMotionMask = (1<<6);
    int PointerMotionHintMask = (1<<7);
    int Button1MotionMask = (1<<8);
    int Button2MotionMask = (1<<9);
    int Button3MotionMask = (1<<10);
    int Button4MotionMask = (1<<11);
    int Button5MotionMask = (1<<12);
    int ButtonMotionMask = (1<<13);
    int KeymapStateMask = (1<<14);
    int ExposureMask = (1<<15);
    int VisibilityChangeMask = (1<<16);
    int StructureNotifyMask = (1<<17);
    int ResizeRedirectMask = (1<<18);
    int SubstructureNotifyMask = (1<<19);
    int SubstructureRedirectMask = (1<<20);
    int FocusChangeMask = (1<<21);
    int PropertyChangeMask = (1<<22);
    int ColormapChangeMask = (1<<23);
    int OwnerGrabButtonMask = (1<<24);

    /* Event names.  Used in "type" field in XEvent structures.  Not to be
       confused with event masks above.  They start from 2 because 0 and 1
       are reserved in the protocol for errors and replies. */
    int KeyPress = 2;
    int KeyRelease = 3;
    int ButtonPress = 4;
    int ButtonRelease = 5;
    int MotionNotify = 6;
    int EnterNotify = 7;
    int LeaveNotify = 8;
    int FocusIn = 9;
    int FocusOut = 10;
    int KeymapNotify = 11;
    int Expose = 12;
    int GraphicsExpose = 13;
    int NoExpose = 14;
    int VisibilityNotify = 15;
    int CreateNotify = 16;
    int DestroyNotify = 17;
    int UnmapNotify = 18;
    int MapNotify = 19;
    int MapRequest = 20;
    int ReparentNotify = 21;
    int ConfigureNotify = 22;
    int ConfigureRequest = 23;
    int GravityNotify = 24;
    int ResizeRequest = 25;
    int CirculateNotify = 26;
    int CirculateRequest = 27;
    int PropertyNotify = 28;
    int SelectionClear = 29;
    int SelectionRequest = 30;
    int SelectionNotify = 31;
    int ColormapNotify = 32;
    int ClientMessage = 33;
    int MappingNotify = 34;
    int LASTEvent = 35;   // must be bigger than any event #

    /* Key masks. Used as modifiers to GrabButton and GrabKey, results of QueryPointer,
       state in various key-, mouse-, and button-related events. */
    int ShiftMask = (1 << 0);
    int LockMask = (1 << 1);
    int ControlMask = (1 << 2);
    int Mod1Mask = (1 << 3);
    int Mod2Mask = (1 << 4);
    int Mod3Mask = (1 << 5);
    int Mod4Mask = (1 << 6);
    int Mod5Mask = (1 << 7);

    /* modifier names.  Used to build a SetModifierMapping request or
       to read a GetModifierMapping request.  These correspond to the
       masks defined above. */
    int ShiftMapIndex = 0;
    int LockMapIndex = 1;
    int ControlMapIndex = 2;
    int Mod1MapIndex = 3;
    int Mod2MapIndex = 4;
    int Mod3MapIndex = 5;
    int Mod4MapIndex = 6;
    int Mod5MapIndex = 7;

    /* button masks.  Used in same manner as Key masks above. Not to be confused
       with button names below. */
    int Button1Mask = (1 << 8);
    int Button2Mask = (1 << 9);
    int Button3Mask = (1 << 10);
    int Button4Mask = (1 << 11);
    int Button5Mask = (1 << 12);

    int AnyModifier = (1 << 15);  /* used in GrabButton, GrabKey */

    /* button names. Used as arguments to GrabButton and as detail in ButtonPress
       and ButtonRelease events.  Not to be confused with button masks above.
       Note that 0 is already defined above as "AnyButton".  */
    int Button1 = 1;
    int Button2 = 2;
    int Button3 = 3;
    int Button4 = 4;
    int Button5 = 5;

    /* Notify modes */
    int NotifyNormal = 0;
    int NotifyGrab = 1;
    int NotifyUngrab = 2;
    int NotifyWhileGrabbed = 3;

    int NotifyHint = 1;    /* for MotionNotify events */

    /* Notify detail */
    int NotifyAncestor = 0;
    int NotifyVirtual = 1;
    int NotifyInferior = 2;
    int NotifyNonlinear = 3;
    int NotifyNonlinearVirtual = 4;
    int NotifyPointer = 5;
    int NotifyPointerRoot = 6;
    int NotifyDetailNone = 7;

    /* Visibility notify */
    int VisibilityUnobscured = 0;
    int VisibilityPartiallyObscured = 1;
    int VisibilityFullyObscured = 2;

    /* Circulation request */
    int PlaceOnTop = 0;
    int PlaceOnBottom = 1;

    /* protocol families */
    int FamilyInternet = 0;    /* IPv4 */
    int FamilyDECnet = 1;
    int FamilyChaos = 2;
    int FamilyInternet6 = 6;    /* IPv6 */

    /* authentication families not tied to a specific protocol */
    int FamilyServerInterpreted = 5;

    /* Property notification */
    int PropertyNewValue = 0;
    int PropertyDelete = 1;

    /* Color Map notification */
    int ColormapUninstalled = 0;
    int ColormapInstalled = 1;

    /* GrabPointer, GrabButton, GrabKeyboard, GrabKey Modes */
    int GrabModeSync = 0;
    int GrabModeAsync = 1;

    /* GrabPointer, GrabKeyboard reply status */
    int GrabSuccess = 0;
    int AlreadyGrabbed = 1;
    int GrabInvalidTime = 2;
    int GrabNotViewable = 3;
    int GrabFrozen = 4;

    /* AllowEvents modes */
    int AsyncPointer = 0;
    int SyncPointer = 1;
    int ReplayPointer = 2;
    int AsyncKeyboard = 3;
    int SyncKeyboard = 4;
    int ReplayKeyboard = 5;
    int AsyncBoth = 6;
    int SyncBoth = 7;

    /* Used in SetInputFocus, GetInputFocus */
    int RevertToNone = (int)None;
    int RevertToPointerRoot = (int)PointerRoot;
    int RevertToParent = 2;

    /*****************************************************************
     * ERROR CODES
     *****************************************************************/

    int Success = 0;    /* everything's okay */
    int BadRequest = 1;    /* bad request code */
    int BadValue = 2;    /* int parameter out of range */
    int BadWindow = 3;    /* parameter not a Window */
    int BadPixmap = 4;    /* parameter not a Pixmap */
    int BadAtom = 5;    /* parameter not an Atom */
    int BadCursor = 6;    /* parameter not a Cursor */
    int BadFont = 7;    /* parameter not a Font */
    int BadMatch = 8;    /* parameter mismatch */
    int BadDrawable = 9;    /* parameter not a Pixmap or Window */
    int BadAccess = 10;    /* depending on context:
                     - key/button already grabbed
                     - attempt to free an illegal
                       cmap entry
                     - attempt to store into a read-only
                       color map entry.
                     - attempt to modify the access control
                       list from other than the local host.
                    */
    int BadAlloc = 11;    /* insufficient resources */
    int BadColor = 12;    /* no such colormap */
    int BadGC = 13;    /* parameter not a GC */
    int BadIDChoice = 14;    /* choice not in range or already used */
    int BadName = 15;    /* font or color name doesn't exist */
    int BadLength = 16;    /* Request length incorrect */
    int BadImplementation = 17;    /* server is defective */

    int FirstExtensionError = 128;
    int LastExtensionError = 255;

    /*****************************************************************
     * WINDOW DEFINITIONS
     *****************************************************************/

    /* Window classes used by CreateWindow */
    /* Note that CopyFromParent is already defined as 0 above */
    int InputOutput = 1;
    int InputOnly = 2;

    /* Window attributes for CreateWindow and ChangeWindowAttributes */
    int CWBackPixmap = (1<<0);
    int CWBackPixel = (1<<1);
    int CWBorderPixmap = (1<<2);
    int CWBorderPixel = (1<<3);
    int CWBitGravity = (1<<4);
    int CWWinGravity = (1<<5);
    int CWBackingStore = (1<<6);
    int CWBackingPlanes = (1<<7);
    int CWBackingPixel = (1<<8);
    int CWOverrideRedirect = (1<<9);
    int CWSaveUnder = (1<<10);
    int CWEventMask = (1<<11);
    int CWDontPropagate = (1<<12);
    int CWColormap = (1<<13);
    int CWCursor = (1<<14);

    /* ConfigureWindow structure */
    int CWX = (1<<0);
    int CWY = (1<<1);
    int CWWidth = (1<<2);
    int CWHeight = (1<<3);
    int CWBorderWidth = (1<<4);
    int CWSibling = (1<<5);
    int CWStackMode = (1<<6);


    /* Bit Gravity */
    int ForgetGravity = 0;
    int NorthWestGravity = 1;
    int NorthGravity = 2;
    int NorthEastGravity = 3;
    int WestGravity = 4;
    int CenterGravity = 5;
    int EastGravity = 6;
    int SouthWestGravity = 7;
    int SouthGravity = 8;
    int SouthEastGravity = 9;
    int StaticGravity = 10;

    /* Window gravity + bit gravity above */
    int UnmapGravity = 0;

    /* Used in CreateWindow for backing-store hint */
    int NotUseful = 0;
    int WhenMapped = 1;
    int Always = 2;

    /* Used in GetWindowAttributes reply */
    int IsUnmapped = 0;
    int IsUnviewable = 1;
    int IsViewable = 2;

    /* Used in ChangeSaveSet */
    int SetModeInsert = 0;
    int SetModeDelete = 1;

    /* Used in ChangeCloseDownMode */
    int DestroyAll = 0;
    int RetainPermanent = 1;
    int RetainTemporary = 2;

    /* Window stacking method (in configureWindow) */
    int Above = 0;
    int Below = 1;
    int TopIf = 2;
    int BottomIf = 3;
    int Opposite = 4;

    /* Circulation direction */
    int RaiseLowest = 0;
    int LowerHighest = 1;

    /* Property modes */
    int PropModeReplace = 0;
    int PropModePrepend = 1;
    int PropModeAppend = 2;

    /*****************************************************************
     * GRAPHICS DEFINITIONS
     *****************************************************************/

    /* graphics functions, as in GC.alu */
    int GXclear = 0x0;        /* 0 */
    int GXand = 0x1;        /* src AND dst */
    int GXandReverse = 0x2;        /* src AND NOT dst */
    int GXcopy = 0x3;        /* src */
    int GXandInverted = 0x4;        /* NOT src AND dst */
    int GXnoop = 0x5;        /* dst */
    int GXxor = 0x6;        /* src XOR dst */
    int GXor = 0x7;        /* src OR dst */
    int GXnor = 0x8;        /* NOT src AND NOT dst */
    int GXequiv = 0x9;        /* NOT src XOR dst */
    int GXinvert = 0xa;        /* NOT dst */
    int GXorReverse = 0xb;        /* src OR NOT dst */
    int GXcopyInverted = 0xc;        /* NOT src */
    int GXorInverted = 0xd;        /* NOT src OR dst */
    int GXnand = 0xe;        /* NOT src OR NOT dst */
    int GXset = 0xf;        /* 1 */

    /* LineStyle */
    int LineSolid = 0;
    int LineOnOffDash = 1;
    int LineDoubleDash = 2;

    /* capStyle */
    int CapNotLast = 0;
    int CapButt = 1;
    int CapRound = 2;
    int CapProjecting = 3;

    /* joinStyle */
    int JoinMiter = 0;
    int JoinRound = 1;
    int JoinBevel = 2;

    /* fillStyle */
    int FillSolid = 0;
    int FillTiled = 1;
    int FillStippled = 2;
    int FillOpaqueStippled = 3;

    /* fillRule */
    int EvenOddRule = 0;
    int WindingRule = 1;

    /* subwindow mode */
    int ClipByChildren = 0;
    int IncludeInferiors = 1;

    /* SetClipRectangles ordering */
    int Unsorted = 0;
    int YSorted = 1;
    int YXSorted = 2;
    int YXBanded = 3;

    /* CoordinateMode for drawing routines */
    int CoordModeOrigin = 0;    /* relative to the origin */
    int CoordModePrevious = 1;    /* relative to previous point */

    /* Polygon shapes */
    int Complex = 0;    /* paths may intersect */
    int Nonconvex = 1;    /* no paths intersect, but not convex */
    int Convex = 2;    /* wholly convex */

    /* Arc modes for PolyFillArc */
    int ArcChord = 0;    /* join endpoints of arc */
    int ArcPieSlice = 1;    /* join endpoints to center of arc */

    /* GC components: masks used in CreateGC, CopyGC, ChangeGC, OR'ed into
       GC.stateChanges */
    int GCFunction = (1<<0);
    int GCPlaneMask = (1<<1);
    int GCForeground = (1<<2);
    int GCBackground = (1<<3);
    int GCLineWidth = (1<<4);
    int GCLineStyle = (1<<5);
    int GCCapStyle = (1<<6);
    int GCJoinStyle = (1<<7);
    int GCFillStyle = (1<<8);
    int GCFillRule = (1<<9);
    int GCTile = (1<<10);
    int GCStipple = (1<<11);
    int GCTileStipXOrigin = (1<<12);
    int GCTileStipYOrigin = (1<<13);
    int GCFont = (1<<14);
    int GCSubwindowMode = (1<<15);
    int GCGraphicsExposures = (1<<16);
    int GCClipXOrigin = (1<<17);
    int GCClipYOrigin = (1<<18);
    int GCClipMask = (1<<19);
    int GCDashOffset = (1<<20);
    int GCDashList = (1<<21);
    int GCArcMode = (1<<22);

    int GCLastBit = 22;
    /*****************************************************************
     * FONTS
     *****************************************************************/

    /* used in QueryFont -- draw direction */
    int FontLeftToRight = 0;
    int FontRightToLeft = 1;

    int FontChange = 255;

    /*****************************************************************
     *  IMAGING
     *****************************************************************/

    /* ImageFormat -- PutImage, GetImage */
    int XYBitmap = 0;    /* depth 1, XYFormat */
    int XYPixmap = 1;    /* depth == drawable depth */
    int ZPixmap = 2;    /* depth == drawable depth */

    /*****************************************************************
     *  COLOR MAP STUFF
     *****************************************************************/

    /* For CreateColormap */
    int AllocNone = 0;    /* create map with no entries */
    int AllocAll = 1;    /* allocate entire map writeable */


    /* Flags used in StoreNamedColor, StoreColors */
    int DoRed = (1<<0);
    int DoGreen = (1<<1);
    int DoBlue = (1<<2);

    /*****************************************************************
     * CURSOR STUFF
     *****************************************************************/

    /* QueryBestSize Class */
    int CursorShape = 0;    /* largest size that can be displayed */
    int TileShape = 1;    /* size tiled fastest */
    int StippleShape = 2;    /* size stippled fastest */

    /*****************************************************************
     * KEYBOARD/POINTER STUFF
     *****************************************************************/

    int AutoRepeatModeOff = 0;
    int AutoRepeatModeOn = 1;
    int AutoRepeatModeDefault = 2;

    int LedModeOff = 0;
    int LedModeOn = 1;

    /* masks for ChangeKeyboardControl */
    int KBKeyClickPercent = (1<<0);
    int KBBellPercent = (1<<1);
    int KBBellPitch = (1<<2);
    int KBBellDuration = (1<<3);
    int KBLed = (1<<4);
    int KBLedMode = (1<<5);
    int KBKey = (1<<6);
    int KBAutoRepeatMode = (1<<7);

    int MappingSuccess = 0;
    int MappingBusy = 1;
    int MappingFailed = 2;

    int MappingModifier = 0;
    int MappingKeyboard = 1;
    int MappingPointer = 2;

    /*****************************************************************
     * SCREEN SAVER STUFF
     *****************************************************************/

    int DontPreferBlanking = 0;
    int PreferBlanking = 1;
    int DefaultBlanking = 2;

    int DisableScreenSaver = 0;
    int DisableScreenInterval = 0;

    int DontAllowExposures = 0;
    int AllowExposures = 1;
    int DefaultExposures = 2;

    /* for ForceScreenSaver */
    int ScreenSaverReset = 0;
    int ScreenSaverActive = 1;

    /*****************************************************************
     * HOSTS AND CONNECTIONS
     *****************************************************************/

    /* for ChangeHosts */
    int HostInsert = 0;
    int HostDelete = 1;

    /* for ChangeAccessControl */
    int EnableAccess = 1;
    int DisableAccess = 0;

    /* Display classes  used in opening the connection
     * Note that the statically allocated ones are even numbered and the
     * dynamically changeable ones are odd numbered */
    int StaticGray = 0;
    int GrayScale = 1;
    int StaticColor = 2;
    int PseudoColor = 3;
    int TrueColor = 4;
    int DirectColor = 5;

    /* Byte order  used in imageByteOrder and bitmapBitOrder */
    int LSBFirst = 0;
    int MSBFirst = 1;



    /*****************************************************************
     * DEFINITIONS OF SPECIFIC EVENTS
     *****************************************************************/

    public static class XEvent extends Union {
        public int type;
        public XAnyEvent xany;
        public XKeyEvent xkey;
        public XButtonEvent xbutton;
        public XMotionEvent xmotion;
        public XCrossingEvent xcrossing;
        public XFocusChangeEvent xfocus;
        public XExposeEvent xexpose;
        public XGraphicsExposeEvent xgraphicsexpose;
        public XNoExposeEvent xnoexpose;
        public XVisibilityEvent xvisibility;
        public XCreateWindowEvent xcreatewindow;
        public XDestroyWindowEvent xdestroywindow;
        public XUnmapEvent xunmap;
        public XMapEvent xmap;
        public XMapRequestEvent xmaprequest;
        public XReparentEvent xreparent;
        public XConfigureEvent xconfigure;
        public XGravityEvent xgravity;
        public XResizeRequestEvent xresizerequest;
        public XConfigureRequestEvent xconfigurerequest;
        public XCirculateEvent xcirculate;
        public XCirculateRequestEvent xcirculaterequest;
        public XPropertyEvent xproperty;
        public XSelectionClearEvent xselectionclear;
        public XSelectionRequestEvent xselectionrequest;
        public XSelectionEvent xselection;
        public XColormapEvent xcolormap;
        public XClientMessageEvent xclient;
        public XMappingEvent xmapping;
        public XErrorEvent xerror;
        public XKeymapEvent xkeymap;
        public NativeLong[] pad = new NativeLong[24];
    }

    public static class XAnyEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;       // window on which event was requested in event mask
    }

    class XKeyEvent extends Structure {
        public int type;            // of event
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // public Display the event was read from
        public Window window;       // "event" window it is reported relative to
        public Window root;         // root window that the event occurred on
        public Window subwindow;    // child window
        public NativeLong time;     // milliseconds
        public int x, y;            // pointer x, y coordinates in event window
        public int x_root, y_root;  // coordinates relative to root
        public int state;           // key or button mask
        public int keycode;         // detail
        public int same_screen;     // same screen flag
    }

    class XButtonEvent extends Structure {
        public int type;            // of event
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;       // "event" window it is reported relative to
        public Window root;         // root window that the event occurred on
        public Window subwindow;    // child window
        public NativeLong time;     // milliseconds
        public int x, y;            // pointer x, y coordinates in event window
        public int x_root, y_root;  // coordinates relative to root
        public int state;           // key or button mask
        public int button;          // detail
        public int same_screen;     // same screen flag
    }

    class XButtonPressedEvent extends XButtonEvent {
    }

    class XButtonReleasedEvent extends XButtonEvent {
    }

    public static class XClientMessageEvent extends Structure {
        public int type;                // ClientMessage
        public NativeLong serial;       // # of last request processed by server
        public int send_event;          // true if this came from a SendEvent request
        public Display display;         // Display the event was read from
        public Window window;
        public Atom message_type;
        public int format;
        public Data data;

        public static class Data extends Union {
            public byte b[] = new byte[20];
            public short s[] = new short[10];
            public NativeLong[] l = new NativeLong[5];
        }
    }

    class XMotionEvent extends Structure {
        public int type;            // of event
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;       // "event" window reported relative to
        public Window root;         // root window that the event occurred on
        public Window subwindow;    // child window
        public NativeLong time;     // milliseconds
        public int x, y;            // pointer x, y coordinates in event window
        public int x_root, y_root;  // coordinates relative to root
        public int state;           // key or button mask
        public byte is_hint;        // detail
        public int same_screen;     // same screen flag
    }

    class XPointerMovedEvent extends XMotionEvent {
    }

    class XCrossingEvent extends Structure {
        public int type;            // of event
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;       // "event" window reported relative to
        public Window root;         // root window that the event occurred on
        public Window subwindow;    // child window
        public NativeLong time;     // milliseconds
        public int x, y;            // pointer x, y coordinates in event window
        public int x_root, y_root;  // coordinates relative to root
        public int mode;            // NotifyNormal, NotifyGrab, NotifyUngrab
        public int detail;
        /*
        * NotifyAncestor, NotifyVirtual, NotifyInferior,
        * NotifyNonlinear,NotifyNonlinearVirtual
        */
        public int same_screen;     // same screen flag
        public int focus;           // intean focus
        public int state;           // key or button mask
    }

    class XEnterWindowEvent extends XCrossingEvent {
    }

    class XLeaveWindowEvent extends XCrossingEvent {
    }

    class XFocusChangeEvent extends Structure {
        public int type;            // FocusIn or FocusOut
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;       // window of event
        public int mode;            // NotifyNormal, NotifyWhileGrabbed, NotifyGrab, NotifyUngrab
        public int detail;
        /*
        * NotifyAncestor, NotifyVirtual, NotifyInferior,
        * NotifyNonlinear,NotifyNonlinearVirtual, NotifyPointer,
        * NotifyPointerRoot, NotifyDetailNone
        */
    }

    class XFocusInEvent extends XFocusChangeEvent {
    }

    class XFocusOutEvent extends XFocusChangeEvent {
    }

    class XExposeEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;
        public int x, y;
        public int width, height;
        public int count;           // if non-zero, at least this many more
    }

    class XGraphicsExposeEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Drawable drawable;
        public int x, y;
        public int width, height;
        public int count;           // if non-zero, at least this many more
        public int major_code;      // core is CopyArea or CopyPlane
        public int minor_code;      // not defined in the core
    }

    class XNoExposeEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Drawable drawable;
        public int major_code;      // core is CopyArea or CopyPlane
        public int minor_code;      // not defined in the core
    }

    class XVisibilityEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;
        public int state;           // Visibility state
    }

    class XCreateWindowEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window parent;       // parent of the window
        public Window window;       // window id of window created
        public int x, y;            // window location
        public int width, height;   // size of window
        public int border_width;    // border width
        public int override_redirect; // creation should be overridden
    }

    class XDestroyWindowEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window event;
        public Window window;
    }

    class XUnmapEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window event;
        public Window window;
        public int from_configure;
    }

    class XMapEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window event;
        public Window window;
        public int override_redirect; // intean, is override set...
    }

    class XMapRequestEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window parent;
        public Window window;
    }

    class XReparentEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window event;
        public Window window;
        public Window parent;
        public int x, y;
        public int override_redirect;
    }

    class XConfigureEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window event;
        public Window window;
        public int x, y;
        public int width, height;
        public int border_width;
        public Window above;
        public int override_redirect;
    }

    class XGravityEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window event;
        public Window window;
        public int x, y;
    }

    class XResizeRequestEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;
        public int width, height;
    }

    class XConfigureRequestEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window parent;
        public Window window;
        public int x, y;
        public int width, height;
        public int border_width;
        public Window above;
        public int detail;          // Above, Below, TopIf, BottomIf, Opposite
        public NativeLong value_mask;
    }

    class XCirculateEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window event;
        public Window window;
        public int place;           // PlaceOnTop, PlaceOnBottom
    }

    class XCirculateRequestEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window parent;
        public Window window;
        public int place;           // PlaceOnTop, PlaceOnBottom
    }

    class XPropertyEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;
        public Atom atom;
        public NativeLong time;
        public int state;           // NewValue, Deleted
    }

    class XSelectionClearEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;
        public Atom selection;
        public NativeLong time;
    }

    class XSelectionRequestEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window owner;
        public Window requestor;
        public Atom selection;
        public Atom target;
        Atom property;
        public NativeLong time;
    }

    class XSelectionEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window requestor;
        public Atom selection;
        public Atom target;
        public Atom property;       // ATOM or None
        public NativeLong time;
    }

    class XColormapEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;
        public Colormap colormap;   // COLORMAP or None
        public int c_new;           // C++
        public int state;           // ColormapInstalled, ColormapUninstalled
    }

    class XMappingEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;       // unused
        public int request;         // one of MappingModifier, MappingKeyboard, MappingPointer
        public int first_keycode;   // first keycode
        public int count;           // defines range of change w. first_keycode*/
    }

    class XErrorEvent extends Structure {
        public int type;
        public Display display;     // Display the event was read from
        public XID resourceid;      // resource id
        public NativeLong serial;   // serial number of failed request
        public byte error_code;     // error code of failed request
        public byte request_code;   // Major op-code of failed request
        public byte minor_code;     // Minor op-code of failed request
    }

    // generated on EnterWindow and FocusIn  when KeyMapState selected
    class XKeymapEvent extends Structure {
        public int type;
        public NativeLong serial;   // # of last request processed by server
        public int send_event;      // true if this came from a SendEvent request
        public Display display;     // Display the event was read from
        public Window window;
        public byte key_vector[] = new byte[32];
    }

    int XSelectInput(Display display, Window window, NativeLong eventMask);
    int XSendEvent(Display display, Window w, int propagate, NativeLong event_mask, XEvent event_send);

    int XNextEvent(Display display, XEvent event_return);

    int XPeekEvent(Display display, XEvent event_return);
    int XWindowEvent(Display display, Window w, NativeLong event_mask, XEvent event_return);
    boolean XCheckWindowEvent(Display display, Window w, NativeLong event_mask, XEvent event_return);
    int XMaskEvent(Display display, NativeLong event_mask, XEvent event_return);
    boolean XCheckMaskEvent(Display display, NativeLong event_mask, XEvent event_return);
    boolean XCheckTypedEvent(Display display, int event_type, XEvent event_return);
    boolean XCheckTypedWindowEvent(Display display, Window w, int event_type, XEvent event_return);

    /** Returns an {@link XWMHints} which must be freed by {@link #XFree}. */
    XWMHints XGetWMHints(Display display, Window window);
    int XGetWMName(Display display, Window window,
                   XTextProperty text_property_return);
    /** Returns an array of {@link XVisualInfo} which must be freed by {@link #XFree}.
     * Use {@link XVisualInfo#toArray(int)
     * toArray(nitems_return.getValue()} to obtain the array.
     */
    XVisualInfo XGetVisualInfo(Display display, NativeLong vinfo_mask,
                               XVisualInfo vinfo_template,
                               IntByReference nitems_return);
    Colormap XCreateColormap(Display display, Window w, Visual visual, int alloc);
    int XGetWindowProperty(Display display, Window w, Atom property,
                           NativeLong long_offset,
                           NativeLong long_length, boolean delete,
                           Atom reg_type,
                           AtomByReference actual_type_return,
                           IntByReference actual_format_return,
                           NativeLongByReference nitems_return,
                           NativeLongByReference bytes_after_return,
                           PointerByReference prop_return);
    int XChangeProperty(Display display, Window w, Atom property, Atom type,
                        int format, int mode, Pointer data, int nelements);
    int XDeleteProperty(Display display, Window w, Atom property);
    // Atom XInternAtom(Display *display, char *atom_name, Bool only_if_exists);
    Atom XInternAtom(Display display, String name, boolean only_if_exists);
    // char *XGetAtomName(Display *display, Atom atom);
    String XGetAtomName(Display display, Atom atom);
    int XCopyArea(Display dpy, Drawable src, Drawable dst, GC gc,
                  int src_x, int src_y, int w, int h, int dst_x, int dst_y);

    XImage XCreateImage(Display dpy, Visual visual, int depth, int format,
                        int offset, Pointer data, int width, int height,
                        int bitmap_pad, int bytes_per_line);
    int XPutImage(Display dpy, Drawable d, GC gc, XImage image,
                  int src_x, int src_y, int dest_x, int dest_y,
                  int width, int height);
    int XDestroyImage(XImage image);


    /*****************************************************************
     * KeySyms, Keycodes, Keymaps
     *****************************************************************/
    
    String XKeysymToString(KeySym keysym);
    KeySym XStringToKeysym(String string);
    byte XKeysymToKeycode(Display display, KeySym keysym);
    KeySym XKeycodeToKeysym(Display display, byte keycode, int index);

    //int XChangeKeyboardMapping(Display display, int first_keycode, int keysyms_per_keycode, KeySym *keysyms, int num_codes);
    /** Defines the symbols for the specified number of KeyCodes starting with first_keycode. The symbols for KeyCodes outside this range remain unchanged. The number of elements in keysyms must be: num_codes * keysyms_per_keycode. The specified first_keycode must be greater than or equal to min_keycode returned by XDisplayKeycodes, or a BadValue error results. In addition, the following expression must be less than or equal to max_keycode as returned by XDisplayKeycodes, or a BadValue error results: first_keycode + num_codes - 1. */
    int XChangeKeyboardMapping(Display display, int first_keycode, int keysyms_per_keycode, KeySym[] keysyms, int num_codes);
    /** Returns the symbols for the specified number of KeyCodes starting with first_keycode. The value specified in first_keycode must be greater than or equal to min_keycode as returned by XDisplayKeycodes, or a BadValue  error results. In addition, the following expression must be less than or equal to max_keycode as returned by XDisplayKeycodes: first_keycode + keycode_count - 1. If this is not the case, a BadValue error results. The number of elements in the KeySyms list is: keycode_count * keysyms_per_keycode_return. KeySym number N, counting from zero, for KeyCode K has the following index in the list, counting from zero: (K - first_code) * keysyms_per_code_return + N. The X server arbitrarily chooses the keysyms_per_keycode_return value to be large enough to report all requested symbols. A special KeySym value of NoSymbol is used to fill in unused elements for individual KeyCodes. To free the storage returned by XGetKeyboardMapping, use XFree. */
    KeySym XGetKeyboardMapping(Display display, byte first_keycode, int keycode_count, IntByReference keysyms_per_keycode_return);
    /** Returns the min-keycodes and max-keycodes supported by the specified display. The minimum number of KeyCodes returned is never less than 8, and the maximum number of KeyCodes returned is never greater than 255. Not all KeyCodes in this range are required to have corresponding keys. */
    int XDisplayKeycodes(Display display, IntByReference min_keycodes_return, IntByReference max_keycodes_return);
    /** Specifies the KeyCodes of the keys (if any) that are to be used as modifiers. If it succeeds, the X server generates a MappingNotify event, and XSetModifierMapping returns MappingSuccess. X permits at most 8 modifier keys. If more than 8 are specified in the XModifierKeymap  structure, a BadLength error results. */
    int XSetModifierMapping(Display display, XModifierKeymapRef modmap);
    /** The XGetModifierMapping  function returns a pointer to a newly created XModifierKeymap structure that contains the keys being used as modifiers. The structure should be freed after use by calling XFreeModifiermap. If only zero values appear in the set for any modifier, that modifier is disabled. */
    XModifierKeymapRef XGetModifierMapping(Display display);
    /** Returns a pointer to XModifierKeymap structure for later use. */
    XModifierKeymapRef XNewModifiermap(int max_keys_per_mod);
    /** Adds the specified KeyCode to the set that controls the specified modifier and returns the resulting XModifierKeymap  structure (expanded as needed). */
    XModifierKeymapRef XInsertModifiermapEntry(XModifierKeymapRef modmap, byte keycode_entry, int modifier);
    /** Deletes the specified KeyCode from the set that controls the specified modifier and returns a pointer to the resulting XModifierKeymap structure. */
    XModifierKeymapRef XDeleteModifiermapEntry(XModifierKeymapRef modmap, byte keycode_entry, int modifier);
    /** Frees the specified XModifierKeymap structure. */
    int XFreeModifiermap(XModifierKeymapRef modmap);


    /** Changes the keyboard control state.
     * @param display display
     * @param value_mask disjunction of KBKeyClickPercent, KBBellPercent, KBBellPitch, KBBellDuration, KBLed, KBLedMode, KBKey, KBAutoRepeatMode
     */
    int XChangeKeyboardControl(Display display, NativeLong value_mask, XKeyboardControlRef values);
    /** Returns the current control values for the keyboard to the XKeyboardState structure. */
    int XGetKeyboardControl(Display display, XKeyboardStateRef values_return);
    /** Turns on auto-repeat for the keyboard on the specified display. */
    int XAutoRepeatOn(Display display);
    /** Turns off auto-repeat for the keyboard on the specified display. */
    int XAutoRepeatOff(Display display);
    /** Rings the bell on the keyboard on the specified display, if possible. The specified volume is relative to the base volume for the keyboard. If the value for the percent argument is not in the range -100 to 100 inclusive, a BadValue error results. The volume at which the bell rings when the percent argument is nonnegative is: base - [(base * percent) / 100] + percent. The volume at which the bell rings when the percent argument is negative is: base + [(base * percent) / 100]. To change the base volume of the bell, use XChangeKeyboardControl. */
    int XBell(Display display, int percent);
    /** Returns a bit vector for the logical state of the keyboard, where each bit set to 1 indicates that the corresponding key is currently pressed down. The vector is represented as 32 bytes. Byte N (from 0) contains the bits for keys 8N to 8N + 7 with the least significant bit in the byte representing key 8N. Note that the logical state of a device (as seen by client applications) may lag the physical state if device event processing is frozen. */
    int XQueryKeymap(Display display, byte[] keys_return);

    /** The modifiermap member of the XModifierKeymap structure contains 8 sets of max_keypermod KeyCodes, one for each modifier in the order Shift, Lock, Control, Mod1, Mod2, Mod3, Mod4, and Mod5. Only nonzero KeyCodes have meaning in each set, and zero KeyCodes are ignored. In addition, all of the nonzero KeyCodes must be in the range specified by min_keycode and max_keycode in the Display structure, or a BadValue error results. */
    class XModifierKeymapRef extends Structure implements Structure.ByReference{
        public int max_keypermod;   /* The server's max # of keys per modifier */
        public Pointer modifiermap;   /* An 8 by max_keypermod array of modifiers */
    }
    
    class XKeyboardControlRef extends Structure implements Structure.ByReference {
        /** Volume for key clicks between 0 (off) and 100 (loud) inclusive, if possible. A setting of -1 restores the default. */
        public int key_click_percent;
        /** Base volume for the bell between 0 (off) and 100 (loud) inclusive, if possible. A setting of -1 restores the default. */
        public int bell_percent;
        /** Pitch (specified in Hz) of the bell, if possible. A setting of -1 restores the default. */
        public int bell_pitch;
        /** Duration of the bell specified in milliseconds, if possible. A setting of -1 restores the default. */
        public int bell_duration;
        /** State of the LEDs. At most 32 LEDs numbered from one are supported. */
        public int led;
        /** LED mode: LedModeOn or LedModeOff. */
        public int led_mode;
        /** <code>auto_repeat_mode</code> can change the auto repeat settings of this key. */
        public int key;
        /** AutoRepeatModeOff, AutoRepeatModeOn, AutoRepeatModeDefault. */
        public int auto_repeat_mode;

        public String toString() {
            return "XKeyboardControlByReference{" +
                    "key_click_percent=" + key_click_percent +
                    ", bell_percent=" + bell_percent +
                    ", bell_pitch=" + bell_pitch +
                    ", bell_duration=" + bell_duration +
                    ", led=" + led +
                    ", led_mode=" + led_mode +
                    ", key=" + key +
                    ", auto_repeat_mode=" + auto_repeat_mode +
                    '}';
        }
    }

    class XKeyboardStateRef extends Structure implements Structure.ByReference {
        /** Volume for key clicks between 0 (off) and 100 (loud) inclusive, if possible. */
        public int key_click_percent;
        /** Base volume for the bell between 0 (off) and 100 (loud) inclusive, if possible. */
        public int bell_percent;
        /** Pitch (specified in Hz) of the bell, if possible. A setting of -1 restores the default. */
        public int bell_pitch;
        /** Duration of the bell specified in milliseconds, if possible. A setting of -1 restores the default. */
        public int bell_duration;
        /** State of the LEDs. At most 32 LEDs numbered from one are supported. */
        public NativeLong led_mask;
        /** Global auto repeat mode: AutoRepeatModeOff or AutoRepeatModeOn. */
        public int global_auto_repeat;
        /** Bit vector. Each bit set to 1 indicates that auto-repeat is enabled for the corresponding key. The vector is represented as 32 bytes. Byte N (from 0) contains the bits for keys 8N to 8N + 7 with the least significant bit in the byte representing key 8N. */
        public byte auto_repeats[] = new byte[32];

        public String toString() {
            return "XKeyboardStateByReference{" +
                    "key_click_percent=" + key_click_percent +
                    ", bell_percent=" + bell_percent +
                    ", bell_pitch=" + bell_pitch +
                    ", bell_duration=" + bell_duration +
                    ", led_mask=" + led_mask +
                    ", global_auto_repeat=" + global_auto_repeat +
                    ", auto_repeats=" + auto_repeats +
                    '}';
        }
    }
}
