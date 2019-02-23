/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

package com.sun.jna;


public class FunctionParameterContext extends ToNativeContext {
    private Function function;
    private Object[] args;
    private int index;

    FunctionParameterContext(Function f, Object[] args, int index) {
        this.function = f;
        this.args = args;
        this.index = index;
    }
    /** Get the function that was invoked. */
    public Function getFunction() { return function; }
    /** Get the arguments used in this function call. */
    public Object[] getParameters() { return args; }
    public int getParameterIndex() { return index; }

}
