/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.ptr;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

/** Provides generic "pointer to type" functionality. */
public abstract class ByReference {
    
    private Pointer pointer;
    
    protected ByReference(int dataSize) {
        pointer = new Memory(dataSize);
    }
    
    public Pointer getPointer() {
        return pointer;
    }
}
