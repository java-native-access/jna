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

/** Provide result conversion context for a function call. */
public class FunctionResultContext extends FromNativeContext {
    
    private Function function;
    private Object[] args;
    FunctionResultContext(Class resultClass, Function function, Object[] args) {
        super(resultClass);
        this.function = function;
        this.args = args;
    }
    /** Get the function that was invoked. */
    public Function getFunction() { return function; }
    /** Get the arguments used in this function call. */
    public Object[] getArguments() { return args; }
}
