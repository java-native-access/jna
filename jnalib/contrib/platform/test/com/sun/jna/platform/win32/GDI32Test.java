/* Copyright (c) 2010 Timothy Wall, All Rights Reserved
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

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.RGBQUAD;

/**
 * @author twalljava[at]dev[dot]java[dot]net
 */
public class GDI32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(GDI32Test.class);
    }
    
    public void testBITMAPINFO() {    	
        BITMAPINFO info = new BITMAPINFO();
        assertEquals("Wrong size for BITMAPINFO()", 44, info.size());

        info = new BITMAPINFO(2);
        assertEquals("Wrong size for BITMAPINFO(2)", 48, info.size());

        info = new BITMAPINFO(16);
        assertEquals("Wrong size for BITMAPINFO(16)", 104, info.size());
    }
}
