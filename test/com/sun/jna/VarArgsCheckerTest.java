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

import junit.framework.TestCase;

public class VarArgsCheckerTest extends TestCase {

    public void testCreation() throws Exception {
        final VarArgsChecker checker = VarArgsChecker.create();
        assertNotNull(checker);
    }

    public void testNoVarArgs() throws Exception {
        final VarArgsChecker checker = VarArgsChecker.create();
        final Method method = VarArgsCheckerTest.class.getMethod("testNoVarArgs", new Class[0]);
        final boolean res = checker.isVarArgs(method);
        // no matter if JVM 1.4 or 1.5+, the result should always be false
        assertFalse("Method should not be detected as varargs", res);
        assertEquals("Non-varargs should return fixed args of zero", 0, checker.fixedArgs(method));
    }

    public void testVarArgsExist() throws Exception {
        final VarArgsChecker checker = VarArgsChecker.create();
        final Method method = VarArgsCheckerTest.class.getMethod("methodWithVarArgs", new Class[]{String[].class});
        final boolean res = checker.isVarArgs(method);
        // this test has to run with Java 1.5+, because this has a method with
        // varargs. So the result has to be true.
        assertTrue("Method should be detected as varargs", res);
    }

    public void testFixedArgsOne() throws Exception {
        final VarArgsChecker checker = VarArgsChecker.create();
        final Method method = VarArgsCheckerTest.class.getMethod("methodWithOneFixedArg", new Class[]{String.class, Object[].class});
        final int res = checker.fixedArgs(method);
        // this test has to run with Java 1.5+, because this has a method with
        // varargs. So the result has to be true.
        assertEquals("Wrong number of fixed args", 1, res);
    }

    public void testFixedArgsTwo() throws Exception {
        final VarArgsChecker checker = VarArgsChecker.create();
        final Method method = VarArgsCheckerTest.class.getMethod("methodWithTwoFixedArgs", new Class[]{String.class, String.class, Object[].class});
        final int res = checker.fixedArgs(method);
        // this test has to run with Java 1.5+, because this has a method with
        // varargs. So the result has to be true.
        assertEquals("Wrong number of fixed args", 2, res);
    }

    public void methodWithVarArgs(String... args) {
        // nothing to do
    }

    public void methodWithOneFixedArg(String fmt, Object... args) {
        // nothing to do
    }

    public void methodWithTwoFixedArgs(String fmt, String fmt2, Object... args) {
        // nothing to do
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(VarArgsCheckerTest.class);
    }
}
