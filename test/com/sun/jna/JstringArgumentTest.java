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

import java.util.HashMap;

import com.sun.jna.DirectArgumentsMarshalTest.DirectTestLibrary;

import junit.framework.TestCase;

/** Exercise a range of native methods.
 *
 * @author twall@users.sf.net
 */
public class JstringArgumentTest extends TestCase {

    public static interface JStringTestLibrary extends Library {
        // test passing jstring without converting to const char *
        boolean testJStringArgument(JNIEnv env, JString jstring);
    }

    JStringTestLibrary lib;

    @Override
    protected void setUp() {
        // we have to support objects (for JString/JNIEnv tests)
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put(Library.OPTION_ALLOW_OBJECTS, Boolean.TRUE);
        options.put(Library.OPTION_CLASSLOADER, DirectTestLibrary.class.getClassLoader());

        lib = Native.load("testlib", JStringTestLibrary.class, options);
    }

    @Override
    protected void tearDown() {
        lib = null;
    }

    public void testJStringArgument() {
        assertEquals("Error passing jstring as argument to native method", true,
                     lib.testJStringArgument(JNIEnv.CURRENT, new JString("test string")));
    }


    public static void main(String[] argList) {
        junit.textui.TestRunner.run(JstringArgumentTest.class);
    }
}
