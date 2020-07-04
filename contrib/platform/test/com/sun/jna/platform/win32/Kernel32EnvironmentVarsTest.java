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
package com.sun.jna.platform.win32;

import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.sun.jna.win32.W32StringUtil;

/**
 * @author lgoldstein
 */
public class Kernel32EnvironmentVarsTest extends AbstractWin32TestSupport {
    public Kernel32EnvironmentVarsTest() {
        super();
    }

    @Test
    public void testKernelUtilGetEnvironmentStrings() {
        Map<String,String>  vars=Kernel32Util.getEnvironmentVariables();
        for (Map.Entry<String,String> entry : vars.entrySet()) {
            String  name=entry.getKey(), expected=entry.getValue();
            if("".equals(name)) {
                // Empty names are created by and env-entry with name "=C:..."
                // as "=" is the split character, this fails here
                continue;
            }
            char[]  data=new char[expected.length() + 1];
            int     size=Kernel32.INSTANCE.GetEnvironmentVariable(name, data, data.length);
            assertEquals("Mismatched retrieved length for " + name, data.length - 1 /* w/o the '\0' */, size);

            String actual = W32StringUtil.toString(data);
            assertEquals("Mismatched retrieved value for " + name, expected, actual);
        }
    }

    @Test
    public void testKernelSetAndGetEnvironmentVariable() {
        String  name=getCurrentTestName(), expected="42";
        assertCallSucceeded("SetEnvironmentVariable", Kernel32.INSTANCE.SetEnvironmentVariable(name, expected));

        try {
            int size = Kernel32.INSTANCE.GetEnvironmentVariable(name, null, 0);
            assertEquals("Mismatched required buffer size", expected.length() + 1, size);

            char[] data = new char[size];
            assertEquals("Mismatched retrieved variable data length", size - 1, Kernel32.INSTANCE.GetEnvironmentVariable(name, data, data.length));

            String actual = W32StringUtil.toString(data);
            assertEquals("Mismatched retrieved variable value", expected, actual);
        } finally {
            assertCallSucceeded("Clean up variable", Kernel32.INSTANCE.SetEnvironmentVariable(name, null));
        }
    }

    @Test
    public void testKernelUtilGetEnvironmentVariable() {
        String  name=getCurrentTestName(), expected=Integer.toString(new Random().nextInt());
        assertCallSucceeded("SetEnvironmentVariable", Kernel32.INSTANCE.SetEnvironmentVariable(name, expected));
        try {
            assertEquals("Mismatched retrieved value", expected, Kernel32Util.getEnvironmentVariable(name));
        } finally {
            assertCallSucceeded("Clean up variable", Kernel32.INSTANCE.SetEnvironmentVariable(name, null));
        }
    }
}
