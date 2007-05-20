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

import java.lang.ref.WeakReference;
import junit.framework.TestCase;

public class MemoryTest extends TestCase {
    public void testAutoFreeMemory() throws Exception {
        Memory core = new Memory(10);
        Pointer shared = core.share(0, 5);
        WeakReference ref = new WeakReference(core);
        
        core = null;
        System.gc();
        long start = System.currentTimeMillis();
        assertNotNull("Memory prematurely GC'd", ref.get());
        shared = null;
        System.gc();
        while (ref.get() != null) {
            if (System.currentTimeMillis() - start > 5000)
                break;
            Thread.sleep(10);
        }
        assertNull("Memory not GC'd", ref.get());
    }
}
