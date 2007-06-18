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

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/** Provides a reference to an association between a native callback closure
 * and a Java {@link Callback} closure. 
 */

class CallbackReference extends WeakReference {
    
    static final Map callbackMap = new WeakHashMap();
    static final Map altCallbackMap = new WeakHashMap();
    
    private static Pointer createCallback(Callback callback, int callingConvention) {
        Method[] mlist = callback.getClass().getMethods();
        for (int i=0;i < mlist.length;i++) {
            if (Callback.METHOD_NAME.equals(mlist[i].getName())) {
                Method m = mlist[i];
                Class[] paramTypes = m.getParameterTypes();
                Class rtype = m.getReturnType();
                if (paramTypes.length > Function.MAX_NARGS) {
                    String msg = "Method signature exceeds the maximum "
                        + "parameter count: " + m;
                    throw new IllegalArgumentException(msg);
                }
                return Function.createCallback(callback, m, paramTypes, rtype, callingConvention);
            }
        }
        String msg = "Callback must implement method named '"
            + Callback.METHOD_NAME + "'";
        throw new IllegalArgumentException(msg);
    }

    /** Return a CallbackReference associated with the given callback, using
     * the calling convention appropriate to the given callback. 
     */
    public static CallbackReference getInstance(Callback callback) {
        int callingConvention = callback instanceof AltCallingConvention
            ? Function.ALT_CONVENTION : Function.C_CONVENTION;
        return getInstance(callback, callingConvention);
    }
    
    /** Return a CallbackReference associated with the given callback, using
     * the requested calling convention. 
     */
    public static CallbackReference getInstance(Callback callback, int callingConvention) {
        Map map = callingConvention == Function.ALT_CONVENTION
            ? altCallbackMap : callbackMap;
        synchronized(map) {
            CallbackReference cbref = (CallbackReference)map.get(callback);
            if (cbref == null) {
                Pointer cbstruct = createCallback(callback, callingConvention);
                cbref = new CallbackReference(callback, cbstruct);
                map.put(callback, cbref);
            }
            return cbref;
        }
    }
    
    Pointer cbstruct;
    public CallbackReference(Callback callback, Pointer cbstruct) {
        super(callback);
        this.cbstruct = cbstruct;
    }
    public Pointer getTrampoline() {
        return cbstruct.getPointer(0);
    }
    protected void finalize() {
        Function.freeCallback(cbstruct.peer);
        cbstruct.peer = 0;
    }
}
