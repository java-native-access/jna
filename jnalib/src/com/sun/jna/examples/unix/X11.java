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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/** Definition (incomplete) of the X library. */
public interface X11 extends Library {
    
    /** Definition (incomplete) of the Xext library. */
    public interface Xext extends Library {
        Xext INSTANCE = (Xext)Native.loadLibrary("Xext", Xext.class);

        void XShapeCombineMask(Pointer display, int window, int dest_kind,
                               int x_off, int y_off, int src, int op);
    }
    
    /** Definition (incomplete) of the Xrender library. */
    public interface Xrender extends Library {
        Xrender INSTANCE = (Xrender)Native.loadLibrary("Xrender", Xrender.class);
        public static class XRenderDirectFormat extends Structure {
            public short green, greenMask;
            public short blue, blueMask;
            public short alpha, alphaMask;
        }
        public static class XRenderPictFormat extends Structure {
            public int id;
            public int type;
            public int depth;
            public XRenderDirectFormat direct;
            public int colormap;
        }
        int PictTypeIndexed = 0x0;
        int PictTypeDirect = 0x1;
        XRenderPictFormat XRenderFindVisualFormat(Pointer display, Pointer visual);
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
    
    public static class XWMHints extends Structure {
        public int flags;
        public int input;
        public int initial_state;
        public int icon_pixmap;
        public int icon_window;
        public int icon_x, icon_y;
        public int icon_mask;
        public int window_group;
    }

    public static class XTextProperty extends Structure {
        public String value;
        public int encoding;
        public int format;
        public int nitems;
    }

    public static class XSizeHints extends Structure {
        public int flags;
        public int x, y;
        public int width, height;
        public int min_width, min_height;
        public int max_width, max_height;
        public int width_inc, height_inc;
        // TODO: nested struct
        public int min_aspect_x;
        public int min_aspect_y;
        public int max_aspect_x;
        public int max_aspect_y;
        public int base_width_y;
        public int base_width, base_height;
        public int win_gravity;
    }
    
    public static class XWindowAttributes extends Structure {
        public int x, y;
        public int width, height;
        public int border_width;
        public int depth;
        public Pointer visual;
        public int root;
        public int c_class;
        public int bit_gravity;
        public int win_gravity;
        public int backing_store;
        public int backing_planes;
        public int backing_pixel;
        public int save_under;
        public int colormap;
        public int map_installed;
        public int map_state;
        public int all_event_masks;
        public int your_event_mask;
        public int do_not_propagate_mask;
        public int override_redirect;
        public Pointer screen;
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
    public static class XSetWindowAttributes extends Structure {
        public int background_pixmap;
        public int background_pixel;
        public int border_pixmap;
        public int border_pixel;
        public int bit_gravity;
        public int win_gravity;
        public int backing_store;
        public int backing_planes;
        public int backing_pixel;
        public int save_under; // boolean
        public int event_mask;
        public int do_not_propagate_mask;
        public int override_redirect; // boolean
        public int colormap;
        public int cursor;
        
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
    public static class XVisualInfo extends Structure {
        public Pointer visual;
        public int visualID;
        public int screen;
        public int depth;
        public int clazz;
        public int red_mask;
        public int green_mask;
        public int blue_mask;
        public int colormap_size;
        public int bits_per_rgb;
    }
    int AllocNone = 0;
    int AllocAll = 1;
    
    int XA_PRIMARY = 0;
    int XA_SECONDARY = 1;
    int XA_ARC = 2;
    int XA_ATOM = 4;
    int XA_BITMAP = 5;
    int XA_CARDINAL = 6;
    
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
    
    Pointer XOpenDisplay(String name);
    int XGetErrorText(Pointer display, int code, byte[] buffer, int len);
    int XDefaultScreen(Pointer display);
    Pointer XDefaultVisual(Pointer display, int screen);
    int XDefaultColormap(Pointer display, int screen);
    int XDisplayWidth(Pointer display, int screen);
    int XDisplayHeight(Pointer display, int screen);
    int XDefaultRootWindow(Pointer display);
    int XRootWindow(Pointer display, int screen);
    int XAllocNamedColor(Pointer display, int colormap, String color_name, 
                         Pointer screen_def_return, Pointer exact_def_return);
    XSizeHints XAllocSizeHints();
    void XSetWMProperties(Pointer display, int window, String window_name,
                          String icon_name, Pointer argv, int argc,
                          XSizeHints normal_hints, Pointer wm_hints,
                          Pointer class_hints);
    int XFree(Pointer data);
    int XCreateSimpleWindow(Pointer display, int parent, int x, int y, 
                            int width, int height, int border_width,
                            int border, int background);
    int XCreateBitmapFromData(Pointer display, int window, Pointer data, 
                              int width, int height);
    int XMapWindow(Pointer display, int window);
    int XFlush(Pointer display);
    int XUnmapWindow(Pointer display, int window);
    int XDestroyWindow(Pointer display, int window);
    int XCloseDisplay(Pointer display);
    int XClearWindow(Pointer display, int window);
    int XClearArea(Pointer display, int window, int x, int y, int w, int h, int exposures);
    int XCreatePixmap(Pointer display, int drawable, int width, int height, int depth);
    int XFreePixmap(Pointer display, int pixmap);
    Pointer XCreateGC(Pointer display, int drawable, int mask, Pointer values);
    int XFreeGC(Pointer display, Pointer gc);
    int XFillRectangle(Pointer display, int drawable, Pointer gc, 
                       int x, int y, int width, int height);
    int XSetForeground(Pointer display, Pointer gc, int color);
    int XSetBackground(Pointer display, Pointer gc, int color);
    int XFillArc(Pointer display, int drawable, Pointer gc, int x, int y, 
                 int width, int height, int angle1, int angle2);
    int XQueryTree(Pointer display, int window, IntByReference root, 
                   IntByReference parent, PointerByReference children,
                   IntByReference childCount);
    boolean XQueryPointer(Pointer display, int window, 
                          IntByReference root_return,
                          IntByReference child_return, 
                          IntByReference root_x_return,
                          IntByReference root_y_return, 
                          IntByReference win_x_return,
                          IntByReference win_y_return,
                          IntByReference mask_return);
    int XGetWindowAttributes(Pointer display, int window, XWindowAttributes attributes);
    int XChangeWindowAttributes(Pointer display, int window, int valuemask, XSetWindowAttributes attributes);
    
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
    
    int XSelectInput(Pointer display, int window, int eventMask);
    /** Returns an {@link XWMHints} which must be freed by {@link #XFree}. */
    XWMHints XGetWMHints(Pointer display, int window);
    int XGetWMName(Pointer display, int window,
                   XTextProperty text_property_return);
    int XQueryKeymap(Pointer display, byte[] keys_return);
    int XKeycodeToKeysym(Pointer display, int keycode, int index);
    /** Returns an array of {@link XVisualInfo} which must be freed by {@link #XFree}.
     * Use {@link XVisualInfo#toArray(Structure[]) 
     * toArray(new XVisualInfo[nitems_return.getValue()]} to obtain the array. 
     */
    XVisualInfo XGetVisualInfo(Pointer display, int vinfo_mask, XVisualInfo vinfo_template,
                               IntByReference nitems_return);
    int XCreateColormap(Pointer display, int w, Pointer visual, int alloc);
    int XGetWindowProperty(Pointer display, int w, int property, int long_offset,
                           int long_length, boolean delete, int reg_type,
                           IntByReference actual_type_return,
                           IntByReference actual_format_return,
                           IntByReference nitems_return,
                           IntByReference bytes_after_return,
                           PointerByReference prop_return);
    int XChangeProperty(Pointer display, int w, int property, int type, 
                        int format, int mode, Pointer data, int nelements);
    int XDeleteProperty(Pointer display, int w, int property);
    int XInternAtom(Pointer display, String name, boolean only_if_exists);
    int XCopyArea(Pointer dpy, int src, int dst, Pointer gc, 
                  int src_x, int src_y, int w, int h, int dst_x, int dst_y);
}
