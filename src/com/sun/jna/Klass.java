/* Copyright (c) 2018 Matthias Bläsing
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

import java.lang.reflect.InvocationTargetException;

abstract class Klass {

    private Klass() {
    }

    /**
     * Create a new instance for the given {@code klass}. Runtime exceptions
     * thrown from the constructor are rethrown, all other exceptions
     * generated from the reflective call are wrapped into a
     * {@link java.lang.IllegalArgumentException} and rethrown.
     *
     * @param klass desired class to instantiate
     * @return the new instance
     * @throws IllegalArgumentException if the instantiation fails
     * @throws RuntimeException if the constructor for {@code klass} throws
     *         a runtime exception
     */
    public static <T> T newInstance(Class<T> klass) {
        try {
            return klass.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException e) {
            String msg = "Can't create an instance of " + klass
                    + ", requires a public no-arg constructor: " + e;
            throw new IllegalArgumentException(msg, e);
        } catch (IllegalArgumentException e) {
            String msg = "Can't create an instance of " + klass
                    + ", requires a public no-arg constructor: " + e;
            throw new IllegalArgumentException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Can't create an instance of " + klass
                    + ", requires a public no-arg constructor: " + e;
            throw new IllegalArgumentException(msg, e);
        } catch (NoSuchMethodException e) {
            String msg = "Can't create an instance of " + klass
                    + ", requires a public no-arg constructor: " + e;
            throw new IllegalArgumentException(msg, e);
        } catch (SecurityException e) {
            String msg = "Can't create an instance of " + klass
                    + ", requires a public no-arg constructor: " + e;
            throw new IllegalArgumentException(msg, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                String msg = "Can't create an instance of " + klass
                        + ", requires a public no-arg constructor: " + e;
                throw new IllegalArgumentException(msg, e);
            }
        }
    }
}
