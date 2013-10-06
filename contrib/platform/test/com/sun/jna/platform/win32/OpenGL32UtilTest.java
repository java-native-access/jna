/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
