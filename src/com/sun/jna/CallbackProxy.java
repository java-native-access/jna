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
    Class[] getParameterTypes();
    /** Returns the type of the callback method's return value. */
    Class getReturnType();
}