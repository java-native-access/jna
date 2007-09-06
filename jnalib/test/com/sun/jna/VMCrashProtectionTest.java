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
package com.sun.jna;

import junit.framework.TestCase;

// TODO: add more platforms
public class VMCrashProtectionTest extends TestCase {
    
    protected void setUp() {
        Native.setProtected(true);
    }
    
    protected void tearDown() {
        Native.setProtected(false);
    }
    
    public void testAccessViolation() {
        if (!Native.isProtected())
            return;
        
        Memory m = new Memory(Pointer.SIZE);
        if (Pointer.SIZE == 4)
            m.setInt(0, 1);
        else
            m.setLong(0, 1);
        Pointer p = m.getPointer(0);
        try {
            p.setInt(0, 0);
            fail("Exception should be thrown");
        }
        catch(Throwable e) {
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(VMCrashProtectionTest.class);
    }
}
