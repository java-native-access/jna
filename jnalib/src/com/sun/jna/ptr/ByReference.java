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
import com.sun.jna.NativeMapped;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/** Provides generic "pointer to type" functionality, often used in C
 * code to return values to the caller in addition to a function result.
 * <p>
 * Derived classes should define <code>setValue(&lt;T&gt;)</code>
 * and <code>&lt;T&gt; getValue()</code> methods which write to/read from
 * memory.
 * <p>This class derives from PointerType instead of Memory in order to
 * restrict the API to only <code>getValue/setValue</code>.
 * <p>NOTE: this class would ideally be replaced by a generic.
 */
public abstract class ByReference extends PointerType {
    
    protected ByReference(int dataSize) {
        setPointer(new Memory(dataSize));
    }
}
