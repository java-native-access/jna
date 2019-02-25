/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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

import junit.framework.TestCase;

/**
 * @author drrobison@openroadsconsulting.com
 */
public class OpenGL32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(OpenGL32UtilTest.class);
    }

    public void testCountGpusNV() {
        int cnt = OpenGL32Util.countGpusNV();
        assertTrue("Expecting >= 0", cnt >= 0);
    }
}
