/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
 * @author dblock[at]dblock[dot]org
 */
public class User32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(User32Test.class);
    }

    public void testGetSystemMetrics() {
    	int cursorWidth = User32.INSTANCE.GetSystemMetrics(WinUser.SM_CXCURSOR);
    	assertTrue(cursorWidth > 0);
    }
}
