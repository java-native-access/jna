/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import junit.framework.TestCase;

/** Exercise the {@link Function} class.
 *
 * @author twall@users.sf.net
 */
//@SuppressWarnings("unused")
public class FunctionTest extends TestCase {

    public void testTooManyArgs() {
        NativeLibrary lib = NativeLibrary.getInstance(Platform.C_LIBRARY_NAME);
        Function f = lib.getFunction("printf");
        Object[] args = new Object[Function.MAX_NARGS+1];
        // Make sure we don't break 'printf'
        args[0] = getName();
        try {
            f.invokeInt(args);
            fail("Arguments should be limited to " + Function.MAX_NARGS);
        } catch(UnsupportedOperationException e) {
            // expected
        }
        assertEquals("Wrong result from 'printf'", getName().length(), f.invokeInt(new Object[] { getName() }));
    }

    public void testUnsupportedReturnType() {
        NativeLibrary lib = NativeLibrary.getInstance(Platform.C_LIBRARY_NAME);
        Function f = lib.getFunction("printf");
        try {
            f.invoke(getClass(), new Object[] { getName() });
            fail("Invalid return types should throw an exception");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(FunctionTest.class);
    }

}
