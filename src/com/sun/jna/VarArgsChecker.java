/*
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

import java.lang.reflect.Method;

/**
 * Class for checking if a method has vararg parameters.
 * Use method {@link VarArgsChecker#create() create()} to create an instance
 * of this class. How the returned instance work depends on the capabilities
 * of the underlying JVM implementation. On older versions of the VM not supporting
 * varargs, the returned VarArgsChecker will always return <code>false</code>
 * on calls to {@link VarArgsChecker#isVarArgs(Method) isVarArgs(Method)}.
 * @author Max Bureck
 */
abstract class VarArgsChecker {

    private VarArgsChecker() {
    }

    /**
     * Implementation actually using Method.isVarArgs()
     */
    private static final class RealVarArgsChecker extends VarArgsChecker {

        boolean isVarArgs(Method m) {
            return m.isVarArgs();
        }

        int fixedArgs(Method m) {
            // In Java, final argument contains all "varargs"
            return m.isVarArgs() ? m.getParameterTypes().length - 1 : 0;
        }
    }

    /**
     * Implementation always returning false when {@link NoVarArgsChecker#isVarArgs(Method)}
     * is called. This implementation will be chosen, if {@link Method#isVarArgs()} is not available
     */
    private static final class NoVarArgsChecker extends VarArgsChecker {

        boolean isVarArgs(Method m) {
            return false;
        }

        int fixedArgs(Method m) {
            return 0;
        }
    }

    /**
     * Creates a new instance of a concrete subclass of VarArgsChecker, depending
     * if {@link Method#isVarArgs()} exists.
     * @return new instance of concrete VarArgsChecker subclass
     */
    static VarArgsChecker create() {
        try {
            // check if Method#isVarArgs() exists
            final Method isVarArgsMethod = Method.class.getMethod("isVarArgs", new Class[0]);
            if(isVarArgsMethod != null) {
                // if it exitsts, return new instance of RealVarArgsChecker
                return new RealVarArgsChecker();
            } else {
                return new NoVarArgsChecker();
            }
        } catch (NoSuchMethodException e) {
            return new NoVarArgsChecker();
        } catch (SecurityException e) {
            return new NoVarArgsChecker();
        }
    }

    /**
     * Checks if the given method was declared to take a variable number of arguments.
     * @param m Method to be checked
     * @return <code>true</code> if the given method takes a variable number of arguments, <code>false</code> otherwise.
     */
    abstract boolean isVarArgs(Method m);

    /**
     * If variadic, returns the number of fixed arguments to the method.
     * @param m Method to be checked
     * @return Number of fixed arguments if the given method takes a variable number of arguments, zero otherwise.
     */
    abstract int fixedArgs(Method m);
}
