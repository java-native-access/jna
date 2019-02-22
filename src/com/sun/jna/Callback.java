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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    List<String> FORBIDDEN_NAMES = Collections.unmodifiableList(
            Arrays.asList("hashCode", "equals", "toString"));
}
