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

/** Provides generic "pointer to type" functionality, often used in C
 * code to return values to the caller in addition to a function result.
 * <p>
 * Derived classes should define <code>setValue(&lt;T&gt;)</code>
 * and <code>&lt;T&gt; getValue()</code> methods which write to/read from
 * memory.
 */
public abstract class ByReference extends Memory {
    
    protected ByReference(int dataSize) {
        super(dataSize);
    }
    
    /** @deprecated This is equivalent to the object itself. */
    public Pointer getPointer() {
        return this;
    }

}
