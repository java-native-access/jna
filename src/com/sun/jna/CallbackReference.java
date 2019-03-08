/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.sun.jna.win32.DLLCallback;

/** Provides a reference to an association between a native callback closure
 * and a Java {@link Callback} closure.
 */

public class CallbackReference extends WeakReference<Callback> {

    static final Map<Callback, CallbackReference> callbackMap = new WeakHashMap<Callback, CallbackReference>();
    static final Map<Callback, CallbackReference> directCallbackMap = new WeakHashMap<Callback, CallbackReference>();
    static final Map<Pointer, Reference<Callback>> pointerCallbackMap = new WeakHashMap<Pointer, Reference<Callback>>();
    // Track memory allocations associated with this closure (usually String args)
    static final Map<Object, Object> allocations = new WeakHashMap<Object, Object>();
    // Global map of allocated closures to facilitate centralized cleanup
    private static final Map<CallbackReference, Reference<CallbackReference>> allocatedMemory =
            Collections.synchronizedMap(new WeakHashMap<CallbackReference, Reference<CallbackReference>>());
    private static final Method PROXY_CALLBACK_METHOD;

    static {
        try {
            PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod("callback", new Class[] { Object[].class });
        } catch(Exception e) {
            throw new Error("Error looking up CallbackProxy.callback() method");
        }
    }

    private static final Map<Callback, CallbackThreadInitializer> initializers = new WeakHashMap<Callback, CallbackThreadInitializer>();
    /**
     * @param cb The {@link Callback} instance
     * @param initializer The {@link CallbackThreadInitializer} - if {@code null} then the
     * associated initializer instance is removed
     * @return The previous initializer instance (may be {@code null})
     */
    static CallbackThreadInitializer setCallbackThreadInitializer(Callback cb, CallbackThreadInitializer initializer) {
        synchronized(initializers) {
            if (initializer != null) {
                return initializers.put(cb, initializer);
            } else {
                return initializers.remove(cb);
            }
        }
    }

    static class AttachOptions extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("daemon", "detach", "name");
        public boolean daemon;
        public boolean detach;
        public String name;
        // Thread name must be UTF8-encoded
        {
            setStringEncoding("utf8");
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /* Called from native code to initialize a callback thread. */
    private static ThreadGroup initializeThread(Callback cb, AttachOptions args) {
        CallbackThreadInitializer init = null;
        if (cb instanceof DefaultCallbackProxy) {
            cb = ((DefaultCallbackProxy)cb).getCallback();
        }
        synchronized(initializers) {
            init = initializers.get(cb);
        }
        ThreadGroup group = null;
        if (init != null) {
            group = init.getThreadGroup(cb);
            args.name = init.getName(cb);
            args.daemon = init.isDaemon(cb);
            args.detach = init.detach(cb);
            args.write();
        }
        return group;
    }

    /** Return a Callback associated with the given function pointer.
     * If the pointer refers to a Java callback trampoline, return the original
     * Java Callback.  Otherwise, return a proxy to the native function
     * pointer.
     * @throws IllegalStateException if the given pointer has already been
     * mapped to a callback of a different type.
     */
    public static Callback getCallback(Class<?> type, Pointer p) {
        return getCallback(type, p, false);
    }

    private static Callback getCallback(Class<?> type, Pointer p, boolean direct) {
        if (p == null) {
            return null;
        }

        if (!type.isInterface())
            throw new IllegalArgumentException("Callback type must be an interface");
        Map<Callback, CallbackReference> map = direct ? directCallbackMap : callbackMap;
        synchronized(pointerCallbackMap) {
            Callback cb = null;
            Reference<Callback> ref = pointerCallbackMap.get(p);
            if (ref != null) {
                cb = ref.get();
                if (cb != null && !type.isAssignableFrom(cb.getClass())) {
                    throw new IllegalStateException("Pointer " + p + " already mapped to " + cb
                                                    + ".\nNative code may be re-using a default function pointer"
                                                    + ", in which case you may need to use a common Callback class"
                                                    + " wherever the function pointer is reused.");
                }
                return cb;
            }
            int ctype = AltCallingConvention.class.isAssignableFrom(type)
                ? Function.ALT_CONVENTION : Function.C_CONVENTION;
            Map<String, Object> foptions = new HashMap<String, Object>(Native.getLibraryOptions(type));
            foptions.put(Function.OPTION_INVOKING_METHOD, getCallbackMethod(type));
            NativeFunctionHandler h = new NativeFunctionHandler(p, ctype, foptions);
            cb = (Callback)Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, h);
            // No CallbackReference for this callback
            map.remove(cb);
            pointerCallbackMap.put(p, new WeakReference<Callback>(cb));
            return cb;
        }
    }

    Pointer cbstruct;
    Pointer trampoline;
    // Keep a reference to the proxy to avoid premature GC of it
    CallbackProxy proxy;
    Method method;
    int callingConvention;
    private CallbackReference(Callback callback, int callingConvention, boolean direct) {
        super(callback);
        TypeMapper mapper = Native.getTypeMapper(callback.getClass());
        this.callingConvention = callingConvention;
        Class<?>[] nativeParamTypes;
        Class<?> returnType;

        // Check whether direct mapping may be used, or whether
        // we need to fall back to conventional mapping
        boolean ppc = Platform.isPPC();
        if (direct) {
            Method m = getCallbackMethod(callback);
            Class<?>[] ptypes = m.getParameterTypes();
            for (int i=0;i < ptypes.length;i++) {
                // varargs w/FP args via ffi_call fails on ppc (darwin)
                if (ppc && (ptypes[i] == float.class
                            || ptypes[i] == double.class)) {
                    direct = false;
                    break;
                }
                // Direct mode callbacks do not support TypeMapper
                if (mapper != null
                    && mapper.getFromNativeConverter(ptypes[i]) != null) {
                    direct = false;
                    break;
                }
            }
            // Direct mode callbacks do not support TypeMapper
            if (mapper != null
                && mapper.getToNativeConverter(m.getReturnType()) != null) {
                direct = false;
            }
        }

        String encoding = Native.getStringEncoding(callback.getClass());
        long peer = 0;
        if (direct) {
            method = getCallbackMethod(callback);
            nativeParamTypes = method.getParameterTypes();
            returnType = method.getReturnType();
            int flags = Native.CB_OPTION_DIRECT;
            if (callback instanceof DLLCallback) {
                flags |= Native.CB_OPTION_IN_DLL;
            }
            peer = Native.createNativeCallback(callback, method,
                                               nativeParamTypes, returnType,
                                               callingConvention, flags,
                                               encoding);
        } else {
            if (callback instanceof CallbackProxy) {
                proxy = (CallbackProxy)callback;
            }
            else {
                proxy = new DefaultCallbackProxy(getCallbackMethod(callback), mapper, encoding);
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
            int flags = callback instanceof DLLCallback
                ? Native.CB_OPTION_IN_DLL : 0;
            peer = Native.createNativeCallback(proxy, PROXY_CALLBACK_METHOD,
                                               nativeParamTypes, returnType,
                                               callingConvention, flags,
                                               encoding);
        }
        cbstruct = peer != 0 ? new Pointer(peer) : null;
        allocatedMemory.put(this, new WeakReference<CallbackReference>(this));
    }

    private Class<?> getNativeType(Class<?> cls) {
        if (Structure.class.isAssignableFrom(cls)) {
            // Make sure we can instantiate an argument of this type
            Structure.validate((Class<? extends Structure>)cls);
            if (!Structure.ByValue.class.isAssignableFrom(cls))
                return Pointer.class;
        } else if (NativeMapped.class.isAssignableFrom(cls)) {
            return NativeMappedConverter.getInstance(cls).nativeType();
        } else if (cls == String.class
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

    /*
     * Find the first instance of an interface which implements the Callback
     * interface or an interface derived from Callback, which defines an
     * appropriate callback method.
     */
    static Class<?> findCallbackClass(Class<?> type) {
        if (!Callback.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not derived from com.sun.jna.Callback");
        }
        if (type.isInterface()) {
            return type;
        }
        Class<?>[] ifaces = type.getInterfaces();
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

    private static Method getCallbackMethod(Class<?> cls) {
        // Look at only public methods defined by the Callback class
        Method[] pubMethods = cls.getDeclaredMethods();
        Method[] classMethods = cls.getMethods();
        Set<Method> pmethods = new HashSet<Method>(Arrays.asList(pubMethods));
        pmethods.retainAll(Arrays.asList(classMethods));

        // Remove Object methods disallowed as callback method names
        for (Iterator<Method> i=pmethods.iterator();i.hasNext();) {
            Method m = i.next();
            if (Callback.FORBIDDEN_NAMES.contains(m.getName())) {
                i.remove();
            }
        }

        Method[] methods = pmethods.toArray(new Method[0]);
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

    /** Set the behavioral options for this callback. */
    private void setCallbackOptions(int options) {
        cbstruct.setInt(Native.POINTER_SIZE, options);
    }

    /** Obtain a pointer to the native glue code for this callback. */
    public Pointer getTrampoline() {
        if (trampoline == null) {
            trampoline = cbstruct.getPointer(0);
        }
        return trampoline;
    }

    /** Free native resources associated with this callback when GC'd. */
    @Override
    protected void finalize() {
        dispose();
    }

    /** Free native resources associated with this callback. */
    protected synchronized void dispose() {
        if (cbstruct != null) {
            try {
                Native.freeNativeCallback(cbstruct.peer);
            } finally {
                cbstruct.peer = 0;
                cbstruct = null;
                allocatedMemory.remove(this);
            }
        }
    }

    /** Dispose of all memory allocated for callbacks. */
    static void disposeAll() {
        // use a copy since dispose() modifes the map
        Collection<CallbackReference> refs = new LinkedList<CallbackReference>(allocatedMemory.keySet());
        for (CallbackReference r : refs) {
            r.dispose();
        }
    }

    private Callback getCallback() {
        return get();
    }

    /** If the callback is one we generated to wrap a native function pointer,
        return that.  Otherwise return null.
    */
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

    /** Native code may call this method with direct=true. */
    private static Pointer getFunctionPointer(Callback cb, boolean direct) {
        Pointer fp = null;
        if (cb == null) {
            return null;
        }
        if ((fp = getNativeFunctionPointer(cb)) != null) {
            return fp;
        }
        Map<String, ?> options = Native.getLibraryOptions(cb.getClass());
        int callingConvention = cb instanceof AltCallingConvention
            ? Function.ALT_CONVENTION
            : (options != null && options.containsKey(Library.OPTION_CALLING_CONVENTION)
               ? ((Integer)options.get(Library.OPTION_CALLING_CONVENTION)).intValue()
               : Function.C_CONVENTION);

        Map<Callback, CallbackReference> map = direct ? directCallbackMap : callbackMap;
        synchronized(pointerCallbackMap) {
            CallbackReference cbref = map.get(cb);
            if (cbref == null) {
                cbref = new CallbackReference(cb, callingConvention, direct);
                map.put(cb, cbref);
                pointerCallbackMap.put(cbref.getTrampoline(), new WeakReference<Callback>(cb));
                if (initializers.containsKey(cb)) {
                    cbref.setCallbackOptions(Native.CB_HAS_INITIALIZER);
                }
            }
            return cbref.getTrampoline();
        }
    }

    private class DefaultCallbackProxy implements CallbackProxy {
        private final Method callbackMethod;
        private ToNativeConverter toNative;
        private final FromNativeConverter[] fromNative;
        private final String encoding;
        public DefaultCallbackProxy(Method callbackMethod, TypeMapper mapper, String encoding) {
            this.callbackMethod = callbackMethod;
            this.encoding = encoding;
            Class<?>[] argTypes = callbackMethod.getParameterTypes();
            Class<?> returnType = callbackMethod.getReturnType();
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

        public Callback getCallback() {
            return CallbackReference.this.getCallback();
        }

        private Object invokeCallback(Object[] args) {
            Class<?>[] paramTypes = callbackMethod.getParameterTypes();
            Object[] callbackArgs = new Object[args.length];

            // convert basic supported types to appropriate Java parameter types
            for (int i=0;i < args.length;i++) {
                Class<?> type = paramTypes[i];
                Object arg = args[i];
                if (fromNative[i] != null) {
                    FromNativeContext context =
                        new CallbackParameterContext(type, callbackMethod, args, i);
                    callbackArgs[i] = fromNative[i].fromNative(arg, context);
                } else {
                    callbackArgs[i] = convertArgument(arg, type);
                }
            }

            Object result = null;
            Callback cb = DefaultCallbackProxy.this.getCallback();
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
            // Synch any structure arguments back to native memory
            for (int i=0;i < callbackArgs.length;i++) {
                if (callbackArgs[i] instanceof Structure
                    && !(callbackArgs[i] instanceof Structure.ByValue)) {
                    ((Structure)callbackArgs[i]).autoWrite();
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
        @Override
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
        private Object convertArgument(Object value, Class<?> dstType) {
            if (value instanceof Pointer) {
                if (dstType == String.class) {
                    value = ((Pointer)value).getString(0, encoding);
                }
                else if (dstType == WString.class) {
                    value = new WString(((Pointer)value).getWideString(0));
                }
                else if (dstType == String[].class) {
                    value = ((Pointer)value).getStringArray(0, encoding);
                }
                else if (dstType == WString[].class) {
                    value = ((Pointer)value).getWideStringArray(0);
                }
                else if (Callback.class.isAssignableFrom(dstType)) {
                    value = CallbackReference.getCallback(dstType, (Pointer)value);
                }
                else if (Structure.class.isAssignableFrom(dstType)) {
                    // If passed by value, don't hold onto the pointer, which
                    // is only valid for the duration of the callback call
                    if (Structure.ByValue.class.isAssignableFrom(dstType)) {
                        Structure s = Structure.newInstance((Class<? extends Structure>) dstType);
                        byte[] buf = new byte[s.size()];
                        ((Pointer)value).read(0, buf, 0, buf.length);
                        s.getPointer().write(0, buf, 0, buf.length);
                        s.read();
                        value = s;
                    } else {
                        Structure s = Structure.newInstance((Class<? extends Structure>) dstType, (Pointer)value);
                        s.conditionalAutoRead();
                        value = s;
                    }
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
            if (value == null) {
                return null;
            }

            Class<?> cls = value.getClass();
            if (Structure.class.isAssignableFrom(cls)) {
                if (Structure.ByValue.class.isAssignableFrom(cls)) {
                    return value;
                }
                return ((Structure)value).getPointer();
            } else if (cls == boolean.class || cls == Boolean.class) {
                return Boolean.TRUE.equals(value) ?
                    Function.INTEGER_TRUE : Function.INTEGER_FALSE;
            } else if (cls == String.class || cls == WString.class) {
                return getNativeString(value, cls == WString.class);
            } else if (cls == String[].class || cls == WString.class) {
                StringArray sa = cls == String[].class
                    ? new StringArray((String[])value, encoding)
                    : new StringArray((WString[])value);
                // Delay GC until array itself is GC'd.
                allocations.put(value, sa);
                return sa;
            } else if (Callback.class.isAssignableFrom(cls)) {
                return getFunctionPointer((Callback)value);
            }
            return value;
        }
        @Override
        public Class<?>[] getParameterTypes() {
            return callbackMethod.getParameterTypes();
        }
        @Override
        public Class<?> getReturnType() {
            return callbackMethod.getReturnType();
        }
    }

    /** Provide invocation handling for an auto-generated Java interface proxy
     * for a native function pointer.
     * Cf. Library.Handler
     */
    private static class NativeFunctionHandler implements InvocationHandler {
        private final Function function;
        private final Map<String, ?> options;

        public NativeFunctionHandler(Pointer address, int callingConvention, Map<String, ?> options) {
            this.options = options;
            this.function = new Function(address, callingConvention, (String) options.get(Library.OPTION_STRING_ENCODING));
        }

        /** Chain invocation to the native function. */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Library.Handler.OBJECT_TOSTRING.equals(method)) {
                String str = "Proxy interface to " + function;
                Method m = (Method)options.get(Function.OPTION_INVOKING_METHOD);
                Class<?> cls = findCallbackClass(m.getDeclaringClass());
                str += " (" + cls.getName() + ")";

                return str;
            } else if (Library.Handler.OBJECT_HASHCODE.equals(method)) {
                return Integer.valueOf(hashCode());
            } else if (Library.Handler.OBJECT_EQUALS.equals(method)) {
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
    private static boolean isAllowableNativeType(Class<?> cls) {
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
}
