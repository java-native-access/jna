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

import com.sun.jna.FromNativeContext;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.NativeMapped;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

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
                case 0:
                    return None;
                case 1:
                    return XA_PRIMARY;
                case 2:
                    return XA_SECONDARY;
                case 3:
                    return XA_ARC;
                case 4:
                    return XA_ATOM;
                case 5:
                    return XA_BITMAP;
                case 6:
                    return XA_CARDINAL;
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

    X11 INSTANCE = (X11)Native.loadLibrary("X11", X11.class);

    int Success = 0;
    int BadRequest = 1;
    int BadValue = 2;
    int BadWindow = 3;
    int BadPixmap = 4;
    int BadAtom = 5;
    int BadCursor = 6;
    int BadFont = 7;
    int BadMatch = 8;
    int BadDrawable = 9;
    int BadAccess = 10;
    int BadAlloc = 11;
    int BadColor = 12;
    int BadGC = 13;
    int BadIDChoice = 14;
    int BadName = 15;
    int BadLength = 16;
    int BadImplementation = 17;

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

    int StaticGray = 0x0;
    int GrayScale = 0x1;
    int StaticColor = 0x2;
    int PseudoColor = 0x3;
    int TrueColor = 0x4;
    int DirectColor = 0x5;
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

    int AllocNone = 0;
    int AllocAll = 1;

    Atom XA_PRIMARY = new Atom(1);
    Atom XA_SECONDARY = new Atom(2);
    Atom XA_ARC = new Atom(3);
    Atom XA_ATOM = new Atom(4);
    Atom XA_BITMAP = new Atom(5);
    Atom XA_CARDINAL = new Atom(6);
    Atom XA_COLORMAP = new Atom(7);
    Atom XA_CURSOR = new Atom(8);

    int PropModeReplace = 0;
    int PropModePrepend = 1;
    int PropModeAppend = 2;

    int None = 0;
    int ParentRelative = 1;
    int CopyFromParent = 0;
    int PointerWindow = 0;
    int InputFocus = 1;
    int PointerRoot = 1;
    int AnyPropertyType = 0;
    int AnyKey = 0;
    int AnyButton = 0;
    int AllTemporary = 0;
    int CurrentTime = 0;
    int NoSymbol = 0;

    Display XOpenDisplay(String name);
    int XGetErrorText(Display display, int code, byte[] buffer, int len);
    int XDefaultScreen(Display display);
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
    int XFlush(Display display);
    int XUnmapWindow(Display display, Window window);
    int XDestroyWindow(Display display, Window window);
    int XCloseDisplay(Display display);
    int XClearWindow(Display display, Window window);
    int XClearArea(Display display, Window window, int x, int y, int w, int h, int exposures);
    Pixmap XCreatePixmap(Display display, Drawable drawable, int width, int height, int depth);
    int XFreePixmap(Display display, Pixmap pixmap);
    int GCFunction                  = (1<<0);
    int GCPlaneMask                 = (1<<1);
    int GCForeground                = (1<<2);
    int GCBackground                = (1<<3);
    int GCLineWidth                 = (1<<4);
    int GCLineStyle                 = (1<<5);
    int GCCapStyle                  = (1<<6);
    int GCJoinStyle                 = (1<<7);
    int GCFillStyle                 = (1<<8);
    int GCFillRule                  = (1<<9);
    int GCTile                      = (1<<10);
    int GCStipple                   = (1<<11);
    int GCTileStipXOrigin           = (1<<12);
    int GCTileStipYOrigin           = (1<<13);
    int GCFont                      = (1<<14);
    int GCSubwindowMode             = (1<<15);
    int GCGraphicsExposures         = (1<<16);
    int GCClipXOrigin               = (1<<17);
    int GCClipYOrigin               = (1<<18);
    int GCClipMask                  = (1<<19);
    int GCDashOffset                = (1<<20);
    int GCDashList                  = (1<<21);
    int GCArcMode                   = (1<<22);
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
    int EvenOddRule = 0;
    int WindingRule = 1;
    int XSetFillRule(Display display, GC gc, int fill_rule);
    int XFreeGC(Display display, GC gc);
    int XDrawPoint(Display display, Drawable drawable, GC gc, int x, int y);
    int CoordModeOrigin = 0;
    int CoordModePrevious = 1;
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
    int Complex = 0;
    int Nonconvex = 1;
    int Convex = 2;
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

    int XSelectInput(Display display, Window window, NativeLong eventMask);
    /** Returns an {@link XWMHints} which must be freed by {@link #XFree}. */
    XWMHints XGetWMHints(Display display, Window window);
    int XGetWMName(Display display, Window window,
                   XTextProperty text_property_return);
    int XQueryKeymap(Display display, byte[] keys_return);
    int XKeycodeToKeysym(Display display, byte keycode, int index);
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
    Atom XInternAtom(Display display, String name, boolean only_if_exists);
    int XCopyArea(Display dpy, Drawable src, Drawable dst, GC gc,
                  int src_x, int src_y, int w, int h, int dst_x, int dst_y);

    int XYBitmap = 0;
    int XYPixmap = 1;
    int ZPixmap = 2;
    XImage XCreateImage(Display dpy, Visual visual, int depth, int format,
                        int offset, Pointer data, int width, int height,
                        int bitmap_pad, int bytes_per_line);
    int XPutImage(Display dpy, Drawable d, GC gc, XImage image,
                  int src_x, int src_y, int dest_x, int dest_y,
                  int width, int height);
    int XDestroyImage(XImage image);
}
