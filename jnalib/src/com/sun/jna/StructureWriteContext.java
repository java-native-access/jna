/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

import java.lang.reflect.Field;

/** Provide Java to native type conversion context for a {@link Structure} 
 * field write. 
 */
public class StructureWriteContext extends ToNativeContext {
    private Structure struct;
    private Field field;
    
    StructureWriteContext(Structure struct, Field field) {
        this.struct = struct;
        this.field = field;                
    }
    /** Get the {@link Structure} the field is a member of. */
    public Structure getStructure() { return struct; }
    
    /** Get the {@link Field} being written to native memory. */
    public Field getField() { return field; }
}

