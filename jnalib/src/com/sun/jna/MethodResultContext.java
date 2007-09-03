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

import java.lang.reflect.Method;

/** Provide result conversion context for a function call that is called via
 * a Library interface.
 */
public class MethodResultContext extends FunctionResultContext {
    private final Method method;
    MethodResultContext(Class resultClass, Function function, Object[] args, 
            Method method) {
        super(resultClass, function, args);
        this.method = method;        
    }
    /** Get the Method used to invoke this function call. */
    public Method getMethod() { return method; }
}
