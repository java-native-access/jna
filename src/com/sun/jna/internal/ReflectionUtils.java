/* Copyright (c) 2019 Matthias Bläsing, All Rights Reserved
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
package com.sun.jna.internal;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to invoke default method reflectively.
 *
 * <p><strong>This class is intented to be used only be JNA itself.</strong></p>
 *
 * <p>This implementation is inspired by:
 * <a href="https://blog.jooq.org/2018/03/28/correct-reflective-access-to-interface-default-methods-in-java-8-9-10/">
 * Correct Reflective Access to Interface Default Methods in Java 8, 9, 10
 * </a>
 */
public class ReflectionUtils {

    private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());

    private static final Method METHOD_IS_DEFAULT;
    private static final Method METHOD_HANDLES_LOOKUP;
    private static final Method METHOD_HANDLES_LOOKUP_IN;
    private static final Method METHOD_HANDLES_PRIVATE_LOOKUP_IN;
    private static final Method METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL;
    private static final Method METHOD_HANDLES_LOOKUP_FIND_SPECIAL;
    private static final Method METHOD_HANDLES_BIND_TO;
    private static final Method METHOD_HANDLES_INVOKE_WITH_ARGUMENTS;
    private static final Method METHOD_TYPE;
    private static Constructor CONSTRUCTOR_LOOKUP_CLASS;

    private static Constructor getConstructorLookupClass() {
        if (CONSTRUCTOR_LOOKUP_CLASS == null) {
            Class lookup = lookupClass("java.lang.invoke.MethodHandles$Lookup");
            CONSTRUCTOR_LOOKUP_CLASS = lookupDeclaredConstructor(lookup, Class.class);
        }
        return CONSTRUCTOR_LOOKUP_CLASS;
    }

    static {
        Class methodHandles = lookupClass("java.lang.invoke.MethodHandles");
        Class methodHandle = lookupClass("java.lang.invoke.MethodHandle");
        Class lookup = lookupClass("java.lang.invoke.MethodHandles$Lookup");
        Class methodType = lookupClass("java.lang.invoke.MethodType");

        METHOD_IS_DEFAULT = lookupMethod(Method.class, "isDefault");
        METHOD_HANDLES_LOOKUP = lookupMethod(methodHandles, "lookup");
        METHOD_HANDLES_LOOKUP_IN = lookupMethod(lookup, "in", Class.class);
        METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL = lookupMethod(lookup, "unreflectSpecial", Method.class, Class.class);
        METHOD_HANDLES_LOOKUP_FIND_SPECIAL = lookupMethod(lookup, "findSpecial", Class.class, String.class, MethodType.class, Class.class);
        METHOD_HANDLES_BIND_TO = lookupMethod(methodHandle, "bindTo", Object.class);
        METHOD_HANDLES_INVOKE_WITH_ARGUMENTS = lookupMethod(methodHandle, "invokeWithArguments", Object[].class);
        METHOD_HANDLES_PRIVATE_LOOKUP_IN = lookupMethod(methodHandles, "privateLookupIn", Class.class, lookup);
        METHOD_TYPE = lookupMethod(methodType, "methodType", Class.class, Class[].class);
    }

    private static Constructor lookupDeclaredConstructor(Class clazz, Class... arguments) {
        if(clazz == null) {
            LOG.log(Level.FINE, "Failed to lookup method: <init>#{1}({2})",
                new Object[]{clazz, Arrays.toString(arguments)});
            return null;
        }
        try {
            Constructor init = clazz.getDeclaredConstructor(arguments);
            init.setAccessible(true);
            return init;
        } catch (Exception ex) {
            LOG.log(Level.FINE, "Failed to lookup method: <init>#{1}({2})",
                new Object[]{clazz, Arrays.toString(arguments)});
            return null;
        }
    }

    private static Method lookupMethod(Class clazz, String methodName, Class... arguments) {
        if(clazz == null) {
            LOG.log(Level.FINE, "Failed to lookup method: {0}#{1}({2})",
                new Object[]{clazz, methodName, Arrays.toString(arguments)});
            return null;
        }
        try {
            return clazz.getMethod(methodName, arguments);
        } catch (Exception ex) {
            LOG.log(Level.FINE, "Failed to lookup method: {0}#{1}({2})",
                new Object[]{clazz, methodName, Arrays.toString(arguments)});
            return null;
        }
    }

    private static Class lookupClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.FINE, "Failed to lookup class: " + name, ex);
            return null;
        }
    }

    /**
     * Check if the supplied method object represents a default method.
     *
     * <p>This is the reflective equivalent of {@code method.isDefault()}.</p>
     *
     * @param method
     * @return true if JVM supports default methods and {@code method} is a
     * default method
     */
    public static boolean isDefault(Method method) {
        if (METHOD_IS_DEFAULT == null) {
            return false;
        }
        try {
            return (boolean) (Boolean) METHOD_IS_DEFAULT.invoke(method);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }
    }

    /**
     * Retrieve the method handle, that can be used to invoke the provided
     * method. It is only intended to be used to call default methods on
     * interfaces.
     *
     * @param method
     * @return method handle that can be used to invoke the supplied method
     * @throws Exception
     */
    public static Object getMethodHandle(Method method) throws Exception {
        assert isDefault(method);
        Object baseLookup = createLookup();
        try {
            Object lookup = createPrivateLookupIn(method.getDeclaringClass(), baseLookup);
            Object mh = mhViaFindSpecial(lookup, method);
            return mh;
        } catch (Exception ex) {
            Object lookup = getConstructorLookupClass().newInstance(method.getDeclaringClass());
            Object mh = mhViaUnreflectSpecial(lookup, method);
            return mh;
        }
    }

    private static Object mhViaFindSpecial(Object lookup, Method method) throws Exception {
        return METHOD_HANDLES_LOOKUP_FIND_SPECIAL.invoke(
            lookup,
            method.getDeclaringClass(),
            method.getName(),
            METHOD_TYPE.invoke(null, method.getReturnType(), method.getParameterTypes()),
            method.getDeclaringClass());
    }

    private static Object mhViaUnreflectSpecial(Object lookup, Method method) throws Exception {
        Object l2 = METHOD_HANDLES_LOOKUP_IN.invoke(lookup, method.getDeclaringClass());
        return METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL.invoke(l2, method, method.getDeclaringClass());
    }

    private static Object createPrivateLookupIn(Class type, Object lookup) throws Exception {
        return METHOD_HANDLES_PRIVATE_LOOKUP_IN.invoke(null, type, lookup);
    }

    private static Object createLookup() throws Exception {
        return METHOD_HANDLES_LOOKUP.invoke(null);
    }

    /**
     * Invokes a default method reflectively. The method must be called with
     * the method handle for a default method on an interfaces.
     *
     * @param target object to invoke the supplied method handle on
     * @param methodHandle retrieved via {@link #getMethodHandle(java.lang.reflect.Method)}
     * @param args
     * @return result of the invokation
     * @throws Throwable
     */
    public static Object invokeDefaultMethod(Object target, Object methodHandle, Object... args) throws Throwable {
        Object boundMethodHandle = METHOD_HANDLES_BIND_TO.invoke(methodHandle, target);
        return METHOD_HANDLES_INVOKE_WITH_ARGUMENTS.invoke(boundMethodHandle, new Object[]{args});
    }

}