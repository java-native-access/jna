package com.sun.jna;
/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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
/** This class provides for customization of the mapping of native threads
 * onto attached Java threads.  Use
 * {@link Native#setCallbackThreadInitializer} to customize the thread context
 * in which a given callback is invoked.<p/>
 *  When a JNA callback is invoked on a native thread that is not currently
 * tracked by the VM and a <code>CallbackThreadInitiailizer</code> is
 * registered for that callback, the initializer object will be used to
 * determine how the thread should be attached to the VM.<p/>.
 *  Once attached, the method {@link Native#detach} may be used from within
 * the callback invocation to alter whether the thread will be detached or not
 * when the callback finishes execution.  Typically this functionality is used
 * in situations where you expect a callback to be called repeatedly from the
 * same thread and you want to avoid potential extra thread allocation
 * overhead on each callback invocation, since the VM may or may not re-use
 * the same allocated thread object each time the thread is attached.<p/>
 * A single initializer may be used for multiple callbacks, one initializer
 * per callback, or you may subclass the initializer to provide different
 * initializer settings depending on the callback.<p/>
 */
public class CallbackThreadInitializer {
    private boolean daemon;
    private boolean detach;
    private String name;
    private ThreadGroup group;
    /** The default initializer causes the callback thread to remain attached
        as a daemon thread, using the default thread name and group.
    */
    public CallbackThreadInitializer() {
        this(true);
    }
    /** Keep the callback thread attached, with the given daemon state, 
        using the default thread name and group.
    */
    public CallbackThreadInitializer(boolean daemon) {
        this(daemon, false);
    }
    /** Uses the default thread name and group. */
    public CallbackThreadInitializer(boolean daemon, boolean detach) {
        this(daemon, detach, null);
    }
    /** Uses the default thread group. */
    public CallbackThreadInitializer(boolean daemon, boolean detach, String name) {
        this(daemon, detach, name, null);
    }
    /** Specify all aspects of how the callback thread should be initialized. */
    public CallbackThreadInitializer(boolean daemon, boolean detach, String name, ThreadGroup group) {
        this.daemon = daemon;
        this.detach = detach;
        this.name = name;
        this.group = group;
    }

    /** Returns the desired name for this thread, or null for the default. */
    public String getName(Callback cb) { return name; }
    /** Returns the desired ThreadGroup for thread, or null for the default. */
    public ThreadGroup getThreadGroup(Callback cb) { return group; }
    /** Returns whether the callback thread should be a daemon thread. */
    public boolean isDaemon(Callback cb) { return daemon; }
    /** Returns whether the Thread should be detached from the VM after the
        callback exits, if the thread was not already attached to begin with.
    */
    public boolean detach(Callback cb) { return detach; }
}
