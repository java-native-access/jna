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
package com.sun.jna;

import java.util.Arrays;
import java.util.Collection;

/** All callback definitions must derive from this interface.  Any 
 * derived interfaces must define a single public method (which may not be named
 * "hashCode", "equals", or "toString"), or one public method named "callback".
 * You are responsible for deregistering your callback (if necessary)
 * in its {@link Object#finalize} method.  If native code attempts to call
 * a callback which has been GC'd, you will likely crash the VM.  If 
 * there is no method to deregister the callback (e.g. <code>atexit</code>
 * in the C library), you must ensure that you always keep a live reference
 * to the callback object.<p>
 * A callback should generally never throw an exception, since it doesn't
 * necessarily have an encompassing Java environment to catch it.  Any
 * exceptions thrown will be passed to the default callback exception
 * handler. 
 */
public interface Callback { 
    interface UncaughtExceptionHandler {
        /** Method invoked when the given callback throws an uncaught
         * exception.<p>
         * Any exception thrown by this method will be ignored.
         */
        void uncaughtException(Callback c, Throwable e);
    }
    /** You must this method name if your callback interface has multiple
        public methods.  Typically a callback will have only one such
        method, in which case any method name may be used, with the exception
        of those in {@link #FORBIDDEN_NAMES}.
    */
    String METHOD_NAME = "callback";
    /** These method names may not be used for a callback method. */
    Collection FORBIDDEN_NAMES = Arrays.asList(new String[] {
            "hashCode", "equals", "toString",
        });
} 
