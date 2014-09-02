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
        
    }
    
    /**
     * Implementation always returning false when {@link NoVarArgsChecker#isVarArgs(Method)}
     * is called. This implementation will be chosen, if {@link Method#isVarArgs()} is not available
     */
    private static final class NoVarArgsChecker extends VarArgsChecker {
        
        boolean isVarArgs(Method m) {
            return false;
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
    
}
