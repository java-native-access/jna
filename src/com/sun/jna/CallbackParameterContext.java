/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.lang.reflect.Method;

/** Provide argument conversion context for a callback invocation. */
public class CallbackParameterContext extends FromNativeContext {
    private Method method;
    private Object[] args;
    private int index;
    CallbackParameterContext(Class<?> javaType, Method m, Object[] args, int index) {
        super(javaType);
        this.method = m;
        this.args = args;
        this.index = index;
    }
    public Method getMethod() { return method; }
    public Object[] getArguments() { return args; }
    public int getIndex() { return index; }
}
