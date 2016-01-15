/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
