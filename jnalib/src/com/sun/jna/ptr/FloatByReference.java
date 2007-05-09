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

public class FloatByReference extends ByReference {
    public FloatByReference() {
        this(0f);
    }
    
    public FloatByReference(float value) {
        super(4);
        setValue(value);
    }
    
    public void setValue(float value) {
        getPointer().setFloat(0, value);
    }
    
    public float getValue() {
        return getPointer().getFloat(0);
    }

}
