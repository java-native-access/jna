/* Copyright (c) 2015 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform.unix;

import com.sun.jna.StructureFieldOrderInspector;
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

    public void testXQueryExtension() {
        final IntByReference opcode = new IntByReference(0);
        final IntByReference first_event = new IntByReference(0);
        final IntByReference first_error = new IntByReference(0);

        // check if the XTEST extension is available
        if (X11.INSTANCE.XQueryExtension(display, "XTEST", opcode, first_event, first_error)) {
            // Opcode for extension should be assigned in range 128-255
            Assert.assertTrue("Value for opcode should be between 128-255.", (opcode.getValue() & 0x80) > 0);
            // No first_event defined for XTEST
            Assert.assertEquals("Wrong value for first_event returned", 0, first_event.getValue());
            // No first_error defined for XTEST
            Assert.assertEquals("Wrong value for first_error returned", 0, first_error.getValue());
        } else {
            // XTEST extension is not supported by the X server
        }
    }

    public void testStructureFieldOrder() {
        StructureFieldOrderInspector.batchCheckStructureGetFieldOrder(X11.class, null, true);
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(X11Test.class);
    }
}


