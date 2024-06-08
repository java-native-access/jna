/* Copyright (c) 2015 Adam Marcionek, All Rights Reserved
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

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.hosted.RuntimeJNIAccess;
import org.graalvm.nativeimage.hosted.RuntimeProxyCreation;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.graalvm.nativeimage.hosted.RuntimeResourceAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

// Provides common logic for JNA-related GraalVM feature classes. These classes should only be included
// at build time for a `native-image` target.
abstract class AbstractJNAFeature implements Feature {
    /**
     * Obtain a reference to a method on a class, in order to register it for reflective access
     *
     * @param clazz Class to obtain method reference from
     * @param methodName Name of the method to obtain a reference to
     * @param args Method arguments
     * @return Method reference
     */
    protected static Method method(Class<?> clazz, String methodName, Class<?>... args) {
        try {
            return clazz.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Obtain a reference to one or more fields on a class, in order to register them for reflective access
     *
     * @param clazz Class to obtain field references from
     * @param fieldNames Names of the fields to obtain references to
     * @return Field references
     */
    protected static Field[] fields(Class<?> clazz, String... fieldNames) {
        try {
            Field[] fields = new Field[fieldNames.length];
            for (int i = 0; i < fieldNames.length; i++) {
                fields[i] = clazz.getDeclaredField(fieldNames[i]);
            }
            return fields;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Register a class for reflective access at runtime
     *
     * @param clazz Class to register
     */
    protected static void reflectiveClass(Class<?>... clazz) {
        RuntimeReflection.register(clazz);
    }

    /**
     * Register a resource for use in the final image
     *
     * @param module Module which owns the resource
     * @param resource Path to the resource to register
     */
    protected static void registerResource(Module module, String resource) {
        RuntimeResourceAccess.addResource(module, resource);
    }

    /**
     * Register a resource for use in the final image
     *
     * @param resource Path to the resource to register
     */
    protected static void registerResource(String resource) {
        registerResource(AbstractJNAFeature.class.getModule(), resource);
    }

    /**
     * Register a class for JNI access at runtime
     *
     * @param clazz Class to register
     */
    protected static void registerJniClass(Class<?> clazz) {
        RuntimeJNIAccess.register(clazz);
        Arrays.stream(clazz.getConstructors()).forEach(RuntimeJNIAccess::register);
        Arrays.stream(clazz.getMethods()).forEach(RuntimeJNIAccess::register);
    }

    /**
     * Register a class for JNI access at runtime, potentially with reflective access as well
     *
     * @param clazz Class to register
     * @param reflective Whether to register the class and constructors for reflective access
     */
    protected static void registerJniClass(Class<?> clazz, Boolean reflective) {
        registerJniClass(clazz);
        if (reflective) {
            RuntimeReflection.register(clazz);
            RuntimeReflection.registerAllConstructors(clazz);
        }
    }

    /**
     * Register a suite of JNA methods for use at runtime
     *
     * @param reflective Whether to register the methods for reflective access
     * @param methods Methods to register
     */
    protected static void registerJniMethods(Boolean reflective, Method... methods) {
        RuntimeJNIAccess.register(methods);
        if (reflective) {
            RuntimeReflection.register(methods);
        }
    }

    /**
     * Register a suite of JNA methods for use at runtime
     *
     * @param methods Methods to register
     */
    protected static void registerJniMethods(Method... methods) {
        registerJniMethods(false, methods);
    }

    /**
     * Register a suite of JNA fields for use at runtime
     *
     * @param reflective Whether to register the fields for reflective access
     * @param fields Fields to register
     */
    protected static void registerJniFields(Boolean reflective, Field[] fields) {
        RuntimeJNIAccess.register(fields);
        if (reflective) {
            RuntimeReflection.register(fields);
        }
    }

    /**
     * Register a suite of JNA fields for use at runtime
     *
     * @param fields Fields to register
     */
    protected static void registerJniFields(Field[] fields) {
        registerJniFields(false, fields);
    }

    /**
     * Register a combination of interfaces used at runtime as a dynamic proxy object
     *
     * @param classes Combination of interface classes; order matters
     */
    protected static void registerProxyInterfaces(Class<?>... classes) {
        RuntimeProxyCreation.register(classes);
    }

    /**
     * Assign the specified class or classes to initialize at image build time
     *
     * @param clazz Classes to register for build-time initialization
     */
    protected static void initializeAtBuildTime(Class<?>... clazz) {
        for (Class<?> c : clazz) {
            RuntimeClassInitialization.initializeAtBuildTime(c);
        }
    }

    /**
     * Assign the specified class or classes to initialize at image run-time
     *
     * @param clazz Classes to register for run-time initialization
     */
    protected static void initializeAtRunTime(Class<?>... clazz) {
        for (Class<?> c : clazz) {
            RuntimeClassInitialization.initializeAtRunTime(c);
        }
    }
}
