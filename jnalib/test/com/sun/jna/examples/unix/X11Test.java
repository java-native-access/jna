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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JLabel;
import junit.framework.TestCase;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.unix.X11.Display;
import com.sun.jna.examples.unix.X11.GC;
import com.sun.jna.examples.unix.X11.Pixmap;
import com.sun.jna.examples.unix.X11.WindowByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class X11Test extends TestCase {

    X11 x11;
    X11.Xext ext;
    protected void setUp() {
        System.setProperty("jna.library.path", "/usr/X11R6/lib");
        x11 = X11.INSTANCE;
        ext = X11.Xext.INSTANCE;
    }
    
    protected void tearDown() {
        x11 = null;
        ext = null;
    }
    
    // This test has been superseded by WindowUtilsTest
    public void xtestShowShapedWindow() throws Exception {
        // Can't run this test headless
        if (GraphicsEnvironment.isHeadless())
            return;

        JFrame frame = new JFrame(getName());
        final boolean[] click = { false };
        final boolean[] ready = { false };
        frame.getContentPane().add(new JLabel(getName()));
        frame.pack();
        frame.setSize(new Dimension(300, 300));
        frame.setLocation(100, 100);
        frame.getContentPane().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                click[0] = true;
            }
        });
        frame.getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                ready[0] = true;
            }
        });
        
        Display dpy = x11.XOpenDisplay(null);

        int WIDTH = 300;
        int HEIGHT = 300;
        //int screen = x11.XDefaultScreen(dpy);
        //int root = x11.XRootWindow(dpy, screen);
        //Set windows = findWindow(dpy, root, frame);
        //assertEquals("Expect a single window", 1, windows.size());
        //int w = ((Integer)windows.iterator().next()).intValue();
        X11.Window w = new X11.Window((int)Native.getWindowID(frame));
        Pixmap pm = x11.XCreatePixmap(dpy, w, WIDTH, HEIGHT, 1);
        if (pm == null) {
            fail("Can't create pixmap");
        }
        GC gc = x11.XCreateGC(dpy, pm, new NativeLong(0), null);
        if (gc == null) {
            fail("Can't create GC");
        }
        Insets insets = frame.getInsets();
        x11.XSetForeground(dpy, gc, new NativeLong(0));
        x11.XFillRectangle(dpy, pm, gc, 0, 0, WIDTH, HEIGHT);
        x11.XSetForeground(dpy, gc, new NativeLong(0xFFFFFF));
        x11.XFillRectangle(dpy, pm, gc, 0, 0, WIDTH, (HEIGHT-insets.top)/2);
        x11.XFillArc(dpy, pm, gc, 0, 0, WIDTH, HEIGHT-insets.top, 0, 360*64);

        int ShapeBounding = 0;
        int ShapeSet = 0;
        ext.XShapeCombineMask(dpy, w, ShapeBounding, 0, 0, pm, ShapeSet);

        x11.XCloseDisplay(dpy);

        frame.setVisible(true);
        Thread.sleep(20000);

        frame.dispose();
    }

    // Example of XQueryTree
    Set findWindow(Display dpy, X11.Window parent, Window w) {
        Set list = new HashSet();

        // TODO: how to map a java window to native window?
        //   - check size, position, title, atom?
        X11.XWindowAttributes atts = new X11.XWindowAttributes();
        int status = x11.XGetWindowAttributes(dpy, parent, atts);
        if (status == 0) {
            System.err.println("skip " + parent);
            return list;
        }
        X11.XTextProperty tp = new X11.XTextProperty();
        x11.XGetWMName(dpy, parent, tp);
        String name = tp.value;
        if (w instanceof Frame && ((Frame)w).getTitle().equals(name)) {
            list.add(parent);
        }
        else {
            /*
            Point pt = w.getLocationOnScreen();
            if (atts.x == pt.x && atts.y == pt.y
                && atts.width == w.getWidth()
                && atts.height == w.getHeight()) {
                list.add(new Integer(parent));
            }
            */
        }
        WindowByReference rootRef = new WindowByReference();
        WindowByReference parentRef = new WindowByReference();
        PointerByReference kidsRef = new PointerByReference();
        IntByReference kidCount = new IntByReference();
        x11.XQueryTree(dpy, parent, rootRef, parentRef, kidsRef, kidCount);
        
        Pointer p = kidsRef.getValue();
        if (p != null) {
            int[] ids = p.getIntArray(0, kidCount.getValue());
            x11.XFree(kidsRef.getValue());
            for (int i=0;i < ids.length;i++) {
                X11.Window child = new X11.Window(ids[i]);
                list.addAll(findWindow(dpy, child, w));
            }
        }
        return list;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(X11Test.class);
    }
}
