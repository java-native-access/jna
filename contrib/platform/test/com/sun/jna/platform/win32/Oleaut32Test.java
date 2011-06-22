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

import com.sun.jna.Pointer;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Oleaut32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Oleaut32Test.class);       
    }
    
    public void testSysAllocString() {
    	assertEquals(null, Oleaut32.INSTANCE.SysAllocString(null));
    	Pointer p = Oleaut32.INSTANCE.SysAllocString("hello world");
    	assertEquals("hello world", p.getString(0, true));
    	Oleaut32.INSTANCE.SysFreeString(p);
    }
    
    public void testSysFreeString() {
    	Oleaut32.INSTANCE.SysFreeString(null);
    }
}
