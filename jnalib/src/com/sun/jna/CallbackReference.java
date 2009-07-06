/* Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved
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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/** Provides a reference to an association between a native callback closure
 * and a Java {@link Callback} closure. 
 */

class CallbackReference extends WeakReference {
    
    static final Map callbackMap = new WeakHashMap();
    static final Map directCallbackMap = new WeakHashMap();
    static final Map allocations = new WeakHashMap();
    private static final Method PROXY_CALLBACK_METHOD;
    
    static {
        try {
            PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod("callback", new Class[] { Object[].class });
        }
        catch(Exception e) {
            throw new Error("Error looking up CallbackProxy.callback() method");
        }
    }


    /** Return a Callback associated with the given function pointer.
     * If the pointer refers to a Java callback trampoline, return the original
     * Java Callback.  Otherwise, return a proxy to the native function pointer.
     */
    public static Callback getCallback(Class type, Pointer p) {
        return getCallback(type, p, false);
    }

    private static Callback getCallback(Class type, Pointer p, boolean direct) {
        if (p == null) {
            return null;
        }

        if (!type.isInterface())
            throw new IllegalArgumentException("Callback type must be an interface");
        Map map = direct ? directCallbackMap : callbackMap;
        synchronized(map) {
            for (Iterator i=map.keySet().iterator();i.hasNext();) {
                Callback cb = (Callback)i.next();
                if (type.isAssignableFrom(cb.getClass())) {
                    CallbackReference cbref = (CallbackReference)map.get(cb);
                    Pointer cbp = cbref != null
                        ? cbref.getTrampoline() : getNativeFunctionPointer(cb);
                    if (p.equals(cbp)) {
                        return cb;
                    }
                }
            }
            int ctype = AltCallingConvention.class.isAssignableFrom(type)
                ? Function.ALT_CONVENTION : Function.C_CONVENTION;
            Map foptions = new HashMap();
            Map options = Native.getLibraryOptions(type);
            if (options != null) {
                foptions.putAll(options);
            }
            foptions.put(Function.OPTION_INVOKING_METHOD, getCallbackMethod(type));
            NativeFunctionHandler h = new NativeFunctionHandler(p, ctype, foptions);
            Callback cb = (Callback)Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, h);
            map.put(cb, null);
            return cb;
        }
    }
    
    Pointer cbstruct;
    // Keep a reference to the proxy to avoid premature GC of it
    CallbackProxy proxy;
    Method method;
    private CallbackReference(Callback callback, int callingConvention, boolean direct) {
        super(callback);
        TypeMapper mapper = Native.getTypeMapper(callback.getClass());
        Class[] nativeParamTypes;
        Class returnType;

        // Check whether direct mapping may be used, or whether
        // we need to fall back to conventional mapping
        String arch = System.getProperty("os.arch").toLowerCase();
        boolean ppc = "ppc".equals(arch) || "powerpc".equals(arch);
        if (direct) {
            Method m = getCallbackMethod(callback);
            Class[] ptypes = m.getParameterTypes();
            for (int i=0;i < ptypes.length;i++) {
                // varargs w/FP args via ffi_call fails on ppc (darwin)
                if (ppc && (ptypes[i] == float.class 
                            || ptypes[i] == double.class)) {
                    direct = false;
                    break;
                }
                // No TypeMapper support in native callback code
                if (mapper != null
                    && mapper.getFromNativeConverter(ptypes[i]) != null) {
                    direct = false;
                    break;
                }
            }
            if (mapper != null
                && mapper.getToNativeConverter(m.getReturnType()) != null) {
                direct = false;
            }
        }

        if (direct) {
            method = getCallbackMethod(callback);
            nativeParamTypes = method.getParameterTypes();
            returnType = method.getReturnType();
            cbstruct = createNativeCallback(callback, method,
                                            nativeParamTypes, returnType,
                                            callingConvention, true);
        }
        else {
            if (callback instanceof CallbackProxy) {
                proxy = (CallbackProxy)callback;
            }
            else {
                proxy = new DefaultCallbackProxy(getCallbackMethod(callback), mapper);
            }
            nativeParamTypes = proxy.getParameterTypes();
            returnType = proxy.getReturnType();

            // Generate a list of parameter types that the native code can 
            // handle.  Let the CallbackProxy do any further conversion
            // to match the true Java callback method signature
            if (mapper != null) {
                for (int i=0;i < nativeParamTypes.length;i++) {
                    FromNativeConverter rc = mapper.getFromNativeConverter(nativeParamTypes[i]);
                    if (rc != null) {
                        nativeParamTypes[i] = rc.nativeType();
                    }
                }
                ToNativeConverter tn = mapper.getToNativeConverter(returnType);
                if (tn != null) {
                    returnType = tn.nativeType();
                }
            }
            for (int i=0;i < nativeParamTypes.length;i++) {
                nativeParamTypes[i] = getNativeType(nativeParamTypes[i]);
                if (!isAllowableNativeType(nativeParamTypes[i])) {
                    String msg = "Callback argument " + nativeParamTypes[i] 
                        + " requires custom type conversion";
                    throw new IllegalArgumentException(msg);
                }
            }
            returnType = getNativeType(returnType);
            if (!isAllowableNativeType(returnType)) {
                String msg = "Callback return type " + returnType
                    + " requires custom type conversion";
                throw new IllegalArgumentException(msg);
            }
            cbstruct = createNativeCallback(proxy, PROXY_CALLBACK_METHOD,  
                                            nativeParamTypes, returnType,
                                            callingConvention, false);
        }

    }
    
    private Class getNativeType(Class cls) {
        if (Structure.class.isAssignableFrom(cls)) {
            // Make sure we can instantiate an argument of this type
            Structure.newInstance(cls);
            if (!Structure.ByValue.class.isAssignableFrom(cls))
                return Pointer.class;
        }
        else if (NativeMapped.class.isAssignableFrom(cls)) {
            return NativeMappedConverter.getInstance(cls).nativeType();
        }
        else if (cls == String.class 
                 || cls == WString.class
                 || cls == String[].class
                 || cls == WString[].class
                 || Callback.class.isAssignableFrom(cls)) {
            return Pointer.class;
        }
        return cls;
    }
    
    private static Method checkMethod(Method m) {
        if (m.getParameterTypes().length > Function.MAX_NARGS) {
            String msg = "Method signature exceeds the maximum "
                + "parameter count: " + m;
            throw new UnsupportedOperationException(msg);
        }
        return m;
    }

    /** Find the first instance of an interface which implements the Callback
     * interface or an interface derived from Callback, which defines an
     * appropriate callback method.
     */
    static Class findCallbackClass(Class type) {
        if (!Callback.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not derived from com.sun.jna.Callback");
        }
        if (type.isInterface()) {
            return type;
        }
        Class[] ifaces = type.getInterfaces();
        for (int i=0;i < ifaces.length;i++) {
            if (Callback.class.isAssignableFrom(ifaces[i])) {
                try { 
                    // Make sure it's got a recognizable callback method
                    getCallbackMethod(ifaces[i]);
                    return ifaces[i];
                }
                catch(IllegalArgumentException e) {
                    break;
                }
            }
        }
        if (Callback.class.isAssignableFrom(type.getSuperclass())) {
            return findCallbackClass(type.getSuperclass());
        }
        return type;
    }
    
    private static Method getCallbackMethod(Callback callback) {
        return getCallbackMethod(findCallbackClass(callback.getClass()));
    }

    private static Method getCallbackMethod(Class cls) {
        // Look at only public methods defined by the Callback class
        Method[] pubMethods = cls.getDeclaredMethods();
        Method[] classMethods = cls.getMethods();
        Set pmethods = new HashSet(Arrays.asList(pubMethods));
        pmethods.retainAll(Arrays.asList(classMethods));

        // Remove Object methods disallowed as callback method names 
        for (Iterator i=pmethods.iterator();i.hasNext();) {
            Method m = (Method)i.next();
            if (Callback.FORBIDDEN_NAMES.contains(m.getName())) {
                i.remove();
            }
        }
        Method[] methods = (Method[])pmethods.toArray(new Method[pmethods.size()]);
        if (methods.length == 1) {
            return checkMethod(methods[0]);
        }
        for (int i=0;i < methods.length;i++) {
            Method m = methods[i];
            if (Callback.METHOD_NAME.equals(m.getName())) {
                return checkMethod(m);
            }
        }
        String msg = "Callback must implement a single public method, "
            + "or one public method named '" + Callback.METHOD_NAME + "'";
        throw new IllegalArgumentException(msg);
    }
    
    /** Obtain a pointer to the native glue code for this callback. */
    public Pointer getTrampoline() {
        return cbstruct.getPointer(0);
    }
    
    /** Free native resources associated with this callback. */
    protected void finalize() {
        freeNativeCallback(cbstruct.peer);
        cbstruct.peer = 0;
    }
    
    private Callback getCallback() {
        return (Callback)get();
    }

    private static Pointer getNativeFunctionPointer(Callback cb) {
        if (Proxy.isProxyClass(cb.getClass())) {
            Object handler = Proxy.getInvocationHandler(cb);
            if (handler instanceof NativeFunctionHandler) {
                return ((NativeFunctionHandler)handler).getPointer();
            }
        }
        return null;
    }
    
    /** Return a {@link Pointer} to the native function address for the
     * given callback. 
     */
    public static Pointer getFunctionPointer(Callback cb) {
        return getFunctionPointer(cb, false);
    }

    /** Native code calls this with direct=true. */
    private static Pointer getFunctionPointer(Callback cb, boolean direct) {
        Pointer fp = null;
        if (cb == null) {
            return null;
        }
        if ((fp = getNativeFunctionPointer(cb)) != null) {
            return fp;
        }
        int callingConvention = cb instanceof AltCallingConvention
            ? Function.ALT_CONVENTION : Function.C_CONVENTION;
        Map map = direct ? directCallbackMap : callbackMap;
        synchronized(map) {
            CallbackReference cbref = (CallbackReference)map.get(cb);
            if (cbref == null) {
                cbref = new CallbackReference(cb, callingConvention, direct);
                map.put(cb, cbref);
            }
            return cbref.getTrampoline();
        }
    }

    private class DefaultCallbackProxy implements CallbackProxy {
        private Method callbackMethod;
        private ToNativeConverter toNative;
        private FromNativeConverter[] fromNative;
        public DefaultCallbackProxy(Method callbackMethod, TypeMapper mapper) {
            this.callbackMethod = callbackMethod;
            Class[] argTypes = callbackMethod.getParameterTypes();
            Class returnType = callbackMethod.getReturnType();
            fromNative = new FromNativeConverter[argTypes.length];
            if (NativeMapped.class.isAssignableFrom(returnType)) {
                toNative = NativeMappedConverter.getInstance(returnType);
            }
            else if (mapper != null) {
                toNative = mapper.getToNativeConverter(returnType);
            }
            for (int i=0;i < fromNative.length;i++) {
                if (NativeMapped.class.isAssignableFrom(argTypes[i])) {
                    fromNative[i] = new NativeMappedConverter(argTypes[i]);
                }
                else if (mapper != null) {
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
        
        private Object invokeCallback(Object[] args) {
            Class[] paramTypes = callbackMethod.getParameterTypes();
            Object[] callbackArgs = new Object[args.length];
            
            // convert basic supported types to appropriate Java parameter types
            for (int i=0;i < args.length;i++) {
                Class type = paramTypes[i];
                Object arg = args[i];
                if (fromNative[i] != null) {
                    FromNativeContext context = 
                        new CallbackParameterContext(type, callbackMethod, args, i);
                    callbackArgs[i] = fromNative[i].fromNative(arg, context);
                }
                else {
                    callbackArgs[i] = convertArgument(arg, type);
                }
            }
            
            Object result = null;
            Callback cb = getCallback();
            if (cb != null) {
                try {
                    result = convertResult(callbackMethod.invoke(cb, callbackArgs));
                }
                catch (IllegalArgumentException e) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e);
                }
                catch (IllegalAccessException e) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e);
                }
                catch (InvocationTargetException e) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e.getTargetException());
                }
            }
            return result;
        }
        /** Called from native code.  All arguments are in an array of 
         * Object as the first argument.  Converts all arguments to types
         * required by the actual callback method signature, and converts
         * the result back into an appropriate native type.
         * This method <em>must not</em> throw exceptions. 
         */
        public Object callback(Object[] args) {
            try {
                return invokeCallback(args);
            }
            catch (Throwable t) {
                Native.getCallbackExceptionHandler().uncaughtException(getCallback(), t);
                return null;
            }
        }

        /** Convert argument from its basic native type to the given
         * Java parameter type.
         */
        private Object convertArgument(Object value, Class dstType) {
            if (value instanceof Pointer) {
                if (dstType == String.class) {
                    value = ((Pointer)value).getString(0);
                }
                else if (dstType == WString.class) {
                    value = new WString(((Pointer)value).getString(0, true));
                }
                else if (dstType == String[].class
                         || dstType == WString[].class) {
                    value = ((Pointer)value).getStringArray(0, dstType == WString[].class);
                }
                else if (Callback.class.isAssignableFrom(dstType)) {
                    value = getCallback(dstType, (Pointer)value);
                }
                else if (Structure.class.isAssignableFrom(dstType)) {
                    Structure s = Structure.newInstance(dstType);
                    // If passed by value, don't hold onto the pointer, which
                    // is only valid for the duration of the callback call
                    if (Structure.ByValue.class.isAssignableFrom(dstType)) {
                        byte[] buf = new byte[s.size()];
                        ((Pointer)value).read(0, buf, 0, buf.length);
                        s.getPointer().write(0, buf, 0, buf.length);
                    }
                    else {
                        s.useMemory((Pointer)value);
                    }
                    s.read();
                    value = s;
                }
            }
            else if ((boolean.class == dstType || Boolean.class == dstType)
                     && value instanceof Number) {
                value = Function.valueOf(((Number)value).intValue() != 0);
            }
            return value;
        }
        
        private Object convertResult(Object value) {
            if (toNative != null) {
                value = toNative.toNative(value, new CallbackResultContext(callbackMethod));
            }
            if (value == null)
                return null;
            Class cls = value.getClass();
            if (Structure.class.isAssignableFrom(cls)) {
                if (Structure.ByValue.class.isAssignableFrom(cls)) {
                    return value;
                }
                return ((Structure)value).getPointer();
            }
            else if (cls == boolean.class || cls == Boolean.class) {
                return Boolean.TRUE.equals(value) ? 
                    Function.INTEGER_TRUE : Function.INTEGER_FALSE;
            }
            else if (cls == String.class || cls == WString.class) {
                return getNativeString(value, cls == WString.class);
            }
            else if (cls == String[].class || cls == WString.class) {
                StringArray sa = cls == String[].class
                    ? new StringArray((String[])value)
                    : new StringArray((WString[])value);
                // Delay GC until array itself is GC'd.
                allocations.put(value, sa);
                return sa;
            }
            else if (Callback.class.isAssignableFrom(cls)) {
                return getFunctionPointer((Callback)value);
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

    /** Provide invocation handling for an auto-generated Java interface proxy 
     * for a native function pointer.
     * Cf. Library.Handler
     */
    private static class NativeFunctionHandler implements InvocationHandler {
        private Function function;
        private Map options;
        
        public NativeFunctionHandler(Pointer address, int callingConvention, Map options) {
            this.function = new Function(address, callingConvention);
            this.options = options;
        }
        
        /** Chain invocation to the native function. */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Library.Handler.OBJECT_TOSTRING.equals(method)) {
                String str = "Proxy interface to " + function;
                Method m = (Method)options.get(Function.OPTION_INVOKING_METHOD);
                Class cls = findCallbackClass(m.getDeclaringClass());
                str += " (" + cls.getName() + ")";

                return str;
            }
            else if (Library.Handler.OBJECT_HASHCODE.equals(method)) {
                return new Integer(hashCode());
            }
            else if (Library.Handler.OBJECT_EQUALS.equals(method)) {
                Object o = args[0];
                if (o != null && Proxy.isProxyClass(o.getClass())) {
                    return Function.valueOf(Proxy.getInvocationHandler(o) == this);
                }
                return Boolean.FALSE;
            }
            if (Function.isVarArgs(method)) {
                args = Function.concatenateVarArgs(args);
            }
            return function.invoke(method.getReturnType(), args, options);
        }
        
        public Pointer getPointer() {
            return function;
        }
    }
    /** Returns whether the given class is supported in native code.
     * Other types (String, WString, Structure, arrays, NativeMapped,
     * etc) are supported in the Java library.
     */
    private static boolean isAllowableNativeType(Class cls) {
        return cls == void.class || cls == Void.class
            || cls == boolean.class || cls == Boolean.class
            || cls == byte.class || cls == Byte.class
            || cls == short.class || cls == Short.class
            || cls == char.class || cls == Character.class
            || cls == int.class || cls == Integer.class
            || cls == long.class || cls == Long.class
            || cls == float.class || cls == Float.class
            || cls == double.class || cls == Double.class
            || (Structure.ByValue.class.isAssignableFrom(cls) 
                && Structure.class.isAssignableFrom(cls))
            || Pointer.class.isAssignableFrom(cls);
    }
    
    private static Pointer getNativeString(Object value, boolean wide) {
        if (value != null) {
            NativeString ns = new NativeString(value.toString(), wide);
            // Delay GC until string itself is GC'd.
            allocations.put(value, ns);
            return ns.getPointer();
        }
        return null;
    }

    /** Create a native trampoline to delegate execution to the Java callback. 
     */
    private static synchronized native Pointer createNativeCallback(Callback callback, 
                                                                    Method method, 
                                                                    Class[] parameterTypes,
                                                                    Class returnType,
                                                                    int callingConvention, boolean direct);
    /** Free the given callback trampoline. */
    private static synchronized native void freeNativeCallback(long ptr);
}
