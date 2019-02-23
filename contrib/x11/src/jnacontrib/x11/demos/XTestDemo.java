/* Copyright (c) 2008 Stefan Endrullis, All Rights Reserved
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
package jnacontrib.x11.demos;

import jnacontrib.x11.api.X;

import com.sun.jna.platform.unix.X11;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.NativeLong;

/**
 * Demonstration of the X extension XTest.
 */
public class XTestDemo {
    private static final long DELAY = 20;

    public static void main(String[] args) throws InterruptedException {
        new XTestDemo();
    }

    private X.Display display = new X.Display();

    public XTestDemo() throws InterruptedException {
        IntByReference event_basep = new IntByReference();
        IntByReference error_basep = new IntByReference();
        IntByReference majorp = new IntByReference();
        IntByReference minorp = new IntByReference();

        if (X11.XTest.INSTANCE.XTestQueryExtension(
                display.getX11Display(),
                event_basep,
                error_basep,
                majorp,
                minorp)) {

            System.out.println("event_basep.getValue() = " + event_basep.getValue());
            System.out.println("error_basep.getValue() = " + error_basep.getValue());
            System.out.println("majorp.getValue() = " + majorp.getValue());
            System.out.println("minorp.getValue() = " + minorp.getValue());
        }

        Thread.sleep(1000);

        type("hello world");
//        typeKey("Return");
//        type("it works!");

        Thread.sleep(1000);
    }

    private void type(String text) {
        for (int i = 0; i < text.length(); i++) {
            char myChar =  text.charAt(i);

            // get keycode from character
            switch (myChar) {
                case ' ': typeKey(65); break;
                default: typeKey("" + myChar);
            }
        }
    }

    private void typeKey(String keyName) {
        X11.KeySym keysym = X11.INSTANCE.XStringToKeysym(keyName);
        typeKey(X11.INSTANCE.XKeysymToKeycode(display.getX11Display(), keysym));
    }

    private void typeKey(int keyCode) {
        if (keyCode == -1) return;

        // press key
        X11.XTest.INSTANCE.XTestFakeKeyEvent(display.getX11Display(), keyCode, true, new NativeLong(DELAY));
        X11.INSTANCE.XFlush(display.getX11Display());
        // release key
        X11.XTest.INSTANCE.XTestFakeKeyEvent(display.getX11Display(), keyCode, false, new NativeLong(DELAY));
        X11.INSTANCE.XFlush(display.getX11Display());
    }
}
