/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.examples.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import junit.framework.TestCase;

public class Shell32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Shell32Test.class);
    }

    public void testStructurePacking() {
        Structure s = new Shell32.SHFILEOPSTRUCT();
        final int SIZE = Pointer.SIZE * 5 + 10; // 5 pointers, 2 ints, 1 short
        assertEquals("Wrong structure size", SIZE, s.size());
    }
}
