/* Copyright (c) 2008 Stefan Endrullis, All Rights Reserved
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
package jnacontrib.x11.demos;

import jnacontrib.x11.api.X;
import com.sun.jna.examples.unix.X11;
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
