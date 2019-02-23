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

/** Placeholder proxy interface to allow an InvocationHandler to convert
 * arguments/return values on callback methods.  This is a special sub-interface
 * of {@link Callback} which expects its arguments in a single Object array
 * passed to its {@link #callback} method.
 */
public interface CallbackProxy extends Callback {

    /** This is the callback method invoked from native code.
     * It must <em>not</em> throw any exceptions whatsoever.
     */
    Object callback(Object[] args);
    /** Returns the types of the parameters to the callback method. */
    Class<?>[] getParameterTypes();
    /** Returns the type of the callback method's return value. */
    Class<?> getReturnType();
}