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
public class CallbackThreadInitializer {
    private boolean daemon;
    private boolean detach;
    private String name;
    private ThreadGroup group;
    public CallbackThreadInitializer() {
        this(false);
    }
    public CallbackThreadInitializer(boolean daemon) {
        this(daemon, true);
    }
    public CallbackThreadInitializer(boolean daemon, boolean detach) {
        this(daemon, detach, null);
    }
    public CallbackThreadInitializer(boolean daemon, boolean detach, String name) {
        this(daemon, detach, name, null);
    }
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