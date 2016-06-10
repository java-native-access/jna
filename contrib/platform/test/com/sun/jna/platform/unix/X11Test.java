/* Copyright (c) 2015 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform.unix;

import java.awt.GraphicsEnvironment;

import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Exercise the {@link X11} class.
 *
 * @author twalljava@java.net
 */
// @SuppressWarnings("unused")
public class X11Test extends TestCase {

    private X11.Display display = null;
    private X11.Window root = null;

    @Override
    protected void setUp() {
        if (!GraphicsEnvironment.isHeadless()) {
            display = X11.INSTANCE.XOpenDisplay(null);
            if (display == null) {
                throw new IllegalStateException("Can't open default display");
            }
            root = X11.INSTANCE.XRootWindow(display, X11.INSTANCE.XDefaultScreen(display));
            if (root == null) {
                throw new IllegalStateException("Can't find root window");
            }
        }
    }

    @Override
    protected void tearDown() {
        if (display != null) {
            X11.INSTANCE.XCloseDisplay(display);
        }
    }

    @Override
    protected void runTest() throws Throwable {
        if (!GraphicsEnvironment.isHeadless()) {
            super.runTest();
        }
    }

    public void testXrender() {
        X11.Xrender.XRenderPictFormat s = new X11.Xrender.XRenderPictFormat();
        s.getPointer().setInt(0, 25);
        s.read();
    }

    public void testXFetchName() {
        PointerByReference pref = new PointerByReference();
        int status = X11.INSTANCE.XFetchName(display, root, pref);
        try {
            assertEquals("Bad status for XFetchName", 0, status);
        }
        finally {
            if (pref.getValue() != null) {
                X11.INSTANCE.XFree(pref.getValue());
            }
        }
    }

    public void testXSetWMProtocols() {
        X11.Atom[] atoms = new X11.Atom[]{ X11.INSTANCE.XInternAtom(display, "WM_DELETE_WINDOW", false), X11.INSTANCE.XInternAtom(display, "WM_TAKE_FOCUS", false) };
        int status = X11.INSTANCE.XSetWMProtocols(display, root, atoms, atoms.length);
        Assert.assertNotEquals("Bad status for XSetWMProtocols", 0, status);
    }

    public void testXGetWMProtocols() {
        X11.Atom[] sentAtoms = new X11.Atom[]{ X11.INSTANCE.XInternAtom(display, "WM_DELETE_WINDOW", false), X11.INSTANCE.XInternAtom(display, "WM_TAKE_FOCUS", false) };
        X11.INSTANCE.XSetWMProtocols(display, root, sentAtoms, sentAtoms.length);
        
        PointerByReference protocols = new PointerByReference();
        IntByReference count = new IntByReference();
        
        int status = X11.INSTANCE.XGetWMProtocols(display, root, protocols, count);
        
        X11.Atom[] receivedAtoms = new X11.Atom[count.getValue()];
        for(int i = count.getValue() - 1; i >= 0; i--) {
            receivedAtoms[i] = new X11.Atom(protocols.getValue().getLong(X11.Atom.SIZE * i));
        }
        X11.INSTANCE.XFree(protocols.getValue());

        Assert.assertNotEquals("Bad status for XGetWMProtocols", 0, status);
        Assert.assertEquals("Wrong number of protocols returned for XGetWMProtocols", sentAtoms.length, receivedAtoms.length);
        Assert.assertArrayEquals("Sent protocols were not equal to returned procols for XGetWMProtocols", sentAtoms, receivedAtoms);
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(X11Test.class);
    }
}


