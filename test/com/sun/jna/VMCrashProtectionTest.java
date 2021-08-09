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

public class VMCrashProtectionTest extends TestCase {

    private boolean savedProtected = Boolean.getBoolean("jna.protected");
    protected void setUp() {
        Native.setProtected(true);
    }

    protected void tearDown() {
        Native.setProtected(savedProtected);
    }

    public void testAccessViolation() {
        if(Platform.ARCH.equals("s390x")) {
            System.out.println("Skipping VMCrashProtectionTest on s390x");
            return;
        }

        if (!Native.isProtected())
            return;

        Memory m = new Memory(Native.POINTER_SIZE);
        if (Native.POINTER_SIZE == 4)
            m.setInt(0, 1);
        else
            m.setLong(0, 1);
        Pointer p = m.getPointer(0);
        Throwable actual = null;
        try {
            p.setInt(0, 0);
        }
        catch(Throwable e) {
            actual = e;
        }
        assertNotNull("Exception should be thrown", actual);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(VMCrashProtectionTest.class);
    }
}
