package com.sun.jna;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class VarArgsCheckerTest extends TestCase {

    public void testCreation() throws Exception {
        final VarArgsChecker sut = VarArgsChecker.create();
        assertNotNull(sut);
    }
    
    public void testNoVarArgs() throws Exception {
        final VarArgsChecker sut = VarArgsChecker.create();
        final Method toCheckForVarArgs = VarArgsCheckerTest.class.getMethod("testNoVarArgs", new Class[0]);
        final boolean res = sut.isVarArgs(toCheckForVarArgs);
        // no matter if JVM 1.4 or 1.5+, the result should always be false
        assertFalse(res);
    }
    
    public void testVarArgsExist() throws Exception {
        final VarArgsChecker sut = VarArgsChecker.create();
        final Method toCheckForVarArgs = VarArgsCheckerTest.class.getMethod("methodWithVarArgs", new Class[]{String[].class});
        final boolean res = sut.isVarArgs(toCheckForVarArgs);
        // this test has to run with Java 1.5+, because this has a method with
        // varargs. So the result has to be true.
        assertTrue(res);
    }
    
    public void methodWithVarArgs(String... args) {
        System.out.println();
    }
}
