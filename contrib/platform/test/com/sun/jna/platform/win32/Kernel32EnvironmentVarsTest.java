/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.sun.jna.Native;

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
            
            String  actual=Native.toString(data);
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
            assertEquals("Mismatched retrieved variable data length", size - 1, Kernel32.INSTANCE.GetEnvironmentVariable(name, data, size));
            
            String  actual=Native.toString(data);
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
