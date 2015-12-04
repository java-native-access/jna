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

import junit.framework.TestCase;

/**
 * Exercise the {@link X11} class.
 *
 * @author twalljava@java.net
 */
// @SuppressWarnings("unused")
public class X11Test extends TestCase {

    public void testXrender() {
        X11.Xrender.XRenderPictFormat s = new X11.Xrender.XRenderPictFormat();
        s.getPointer().setInt(0, 25);
        s.read();
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(X11Test.class);
    }
}


