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

import com.sun.jna.Pointer;

/** Represents a reference to a pointer to native data. 
 * In C notation, <code>void**</code>.
 * @author twall
 */
public class PointerByReference extends ByReference {

    public PointerByReference() {
        this(null);
    }
    
    public PointerByReference(Pointer value) {
        super(Pointer.SIZE);
        setValue(value);
    }
    
    public void setValue(Pointer value) {
        getPointer().setPointer(0, value);
    }
    
    public Pointer getValue() {
        return getPointer().getPointer(0);
    }
}
