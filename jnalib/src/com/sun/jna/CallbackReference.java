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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/** Provides a reference to an association between a native callback closure
 * and a Java {@link Callback} closure. 
 */

class CallbackReference extends WeakReference {
    
    static final Map callbackMap = new WeakHashMap();
    static final Map altCallbackMap = new WeakHashMap();
    
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
                cbref = new CallbackReference(callback, callingConvention);
                map.put(callback, cbref);
                
            }
            return cbref;
        }
    }
    
    Pointer cbstruct;
    // Keep a reference to avoid premature GC
    CallbackProxy proxy;
    private CallbackReference(Callback callback, int callingConvention) {
        super(callback);
        Class type = callback.getClass();
        Class[] ifaces = type.getInterfaces();
        for (int i=0;i < ifaces.length;i++) {
            if (Callback.class.isAssignableFrom(ifaces[i])) {
                type = ifaces[i];
                break;
            }
        }
        TypeMapper mapper = null;
        Class declaring = type.getDeclaringClass();
        if (declaring != null) {
            mapper = Native.getTypeMapper(declaring);
        }
        Method m = getCallbackMethod(callback);
        proxy = new DefaultCallbackProxy(m, mapper);

        // Generate a list of parameter types that the native code can 
        // handle.  Let the CallbackProxy to do any further conversion
        // to match the true callback signature
        Class[] nativeParamTypes = proxy.getParameterTypes();
        if (mapper != null) {
            for (int i=0;i < nativeParamTypes.length;i++) {
                FromNativeConverter rc = mapper.getFromNativeConverter(nativeParamTypes[i]);
                if (rc != null) {
                    nativeParamTypes[i] = rc.nativeType();
                }
             }
        }
        for (int i=0;i < nativeParamTypes.length;i++) {
            Class cls = nativeParamTypes[i];
            if (Structure.class.isAssignableFrom(cls)) {
                // Make sure we can instantiate an argument of this type
                try {
                    cls.newInstance();
                }
                catch (InstantiationException e) {
                    throw new IllegalArgumentException("Can't instantiate " + cls + ": " + e);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Instantiation of " + cls 
                                                       + " not allowed (is it public?): " + e);
                } 
                nativeParamTypes[i] = Pointer.class;
            }
            else if (NativeLong.class.isAssignableFrom(cls)) {
                nativeParamTypes[i] = NativeLong.SIZE == 4 ? Integer.class : Long.class;
            }
            else if (cls == String.class || cls == WString.class) {
                nativeParamTypes[i] = Pointer.class;
            }
            else if (!isAllowableNativeType(cls)) {
                throw new IllegalArgumentException("Callback argument " + cls + " requires custom type conversion");
            }
        }
        try {
            Method proxyMethod = 
                CallbackProxy.class.getMethod("callback", new Class[] { Object[].class });
            cbstruct = createCallback(proxy, proxyMethod, 
                                      nativeParamTypes, callingConvention);
        }
        catch (NoSuchMethodException e) {
            throw new Error("Unexpectedly missing CallbackProxy.callback(Object[])");
        }
    }
    
    private Method getCallbackMethod(Callback callback) {
        Method[] mlist = callback.getClass().getMethods();
        for (int mi=0;mi < mlist.length;mi++) {
            Method m = mlist[mi];
            if (Callback.METHOD_NAME.equals(m.getName())) {
                if (m.getParameterTypes().length > Function.MAX_NARGS) {
                    String msg = "Method signature exceeds the maximum "
                        + "parameter count: " + m;
                    throw new IllegalArgumentException(msg);
                }
                return m;
            }
        }
        String msg = "Callback must implement method named '"
            + Callback.METHOD_NAME + "'";
        throw new IllegalArgumentException(msg);
    }
    
    public Pointer getTrampoline() {
        return cbstruct.getPointer(0);
    }
    protected void finalize() {
        freeCallback(cbstruct.peer);
        cbstruct.peer = 0;
    }
    
    private Callback getCallback() {
        return (Callback)get();
    }

    private class DefaultCallbackProxy implements CallbackProxy {
        private Method callbackMethod;
        private ToNativeConverter toNative;
        private FromNativeConverter[] fromNative;
        public DefaultCallbackProxy(Method callbackMethod, TypeMapper mapper) {
            this.callbackMethod = callbackMethod;
            Class[] argTypes = callbackMethod.getParameterTypes();
            fromNative = new FromNativeConverter[argTypes.length];
            if (mapper != null) {
                toNative = mapper.getToNativeConverter(callbackMethod.getReturnType());
                for (int i=0;i < fromNative.length;i++) {
                    fromNative[i] = mapper.getFromNativeConverter(argTypes[i]);
                }
            }
            if (!callbackMethod.isAccessible()) {
                try {
                    callbackMethod.setAccessible(true);
                }
                catch(SecurityException e) {
                    throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + callbackMethod);
                }
            }
        }
        /** Called from native code.  All arguments are in an array of 
         * Object as the first argument.  Converts all arguments to types
         * required by the actual callback method signature, and converts
         * the result back into an appropriate native type.
         * This method <em>must not</em> throw exceptions. 
         */
        public Object callback(Object[] args) {
            Class[] paramTypes = callbackMethod.getParameterTypes();
            Object[] callbackArgs = new Object[args.length];

            // convert basic supported types to appropriate Java parameter types
            for (int i=0;i < args.length;i++) {
                if (fromNative[i] != null) {
                    FromNativeContext context = 
                        new CallbackInvocationContext(paramTypes[i], callbackMethod, args);
                    args[i] = fromNative[i].fromNative(args[i], context);
                }
                callbackArgs[i] = convertArgument(args[i], paramTypes[i]);
            }

            Object result = null;
            Callback cb = getCallback();
            if (cb != null) {
                try {
                    result = convertResult(callbackMethod.invoke(cb, callbackArgs));
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        /** Convert argument from its basic native type to the given
         * Java parameter type.
         */
        private Object convertArgument(Object value, Class dstType) {
            if (value instanceof Pointer) {
                if (dstType == String.class) {
                    value = ((Pointer)value).getString(0, false);
                }
                else if (dstType == WString.class) {
                    value = new WString(((Pointer)value).getString(0, true));
                }
                else if (Structure.class.isAssignableFrom(dstType)) {
                    Pointer p = (Pointer)value;
                    try {
                        Structure s = (Structure)dstType.newInstance();
                        s.useMemory(p);
                        s.read();
                        value = s;
                    }
                    catch(InstantiationException e) {
                        // can't happen, already checked for
                    }
                    catch(IllegalAccessException e) {
                        // can't happen, already checked for
                    }
                }
            }
            else if (NativeLong.class.isAssignableFrom(dstType)
                     && (value instanceof Integer || value instanceof Long)) {
                value = new NativeLong(NativeLong.SIZE == 4
                                       ? ((Integer)value).intValue()
                                       : ((Long)value).longValue());
            }
            else if ((boolean.class == dstType || Boolean.class == dstType)
                     && value instanceof Number) {
                value = Boolean.valueOf(((Number)value).intValue() != 0);
            }
            return value;
        }
        
        private Object convertResult(Object value) {
            if (toNative != null) {
                value = toNative.toNative(value);
            }
            if (value == null)
                return null;
            Class cls = value.getClass();
            if (Structure.class.isAssignableFrom(cls)) {
                return ((Structure)value).getPointer();
            }
            else if (NativeLong.class.isAssignableFrom(cls)) {
                return ((NativeLong)value).asNativeValue();
            }
            else if (cls == boolean.class || cls == Boolean.class) {
                return new Integer(Boolean.TRUE.equals(value)?-1:0);
            }
            else if (cls == String.class) {
                // FIXME: need to prevent GC, but for how long?
                return new NativeString(value.toString()).getPointer();
            }
            else if (cls == WString.class) {
                // FIXME: need to prevent GC, but for how long?
                return new NativeString(value.toString(), true).getPointer();
            }
            else if (!isAllowableNativeType(cls)) {
                throw new IllegalArgumentException("Return type " + cls + " will be ignored");
            }
            return value;
        }
        public Class[] getParameterTypes() {
            return callbackMethod.getParameterTypes();
        }
        public Class getReturnType() {
            return callbackMethod.getReturnType();
        }
    }

    /** Returns whether the given class is supported in native code.
     * Other types (String, WString, Structure, arrays, NativeLong,
     * etc) are supported in the Java library.
     */
    static boolean isAllowableNativeType(Class cls) {
        return cls == boolean.class || cls == Boolean.class
            || cls == byte.class || cls == Byte.class
            || cls == short.class || cls == Short.class
            || cls == char.class || cls == Character.class
            || cls == int.class || cls == Integer.class
            || cls == long.class || cls == Long.class
            || cls == float.class || cls == Float.class
            || cls == double.class || cls == Double.class
            || Pointer.class.isAssignableFrom(cls);
    }
    
    /** Create a callback function pointer. */
    private static native Pointer createCallback(CallbackProxy callback, 
                                                 Method method, 
                                                 Class[] parameterTypes, 
                                                 int callingConvention);
    /** Free the given callback function pointer. */
    private static native void freeCallback(long ptr);
}
