/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.LastErrorException;

import junit.framework.TestCase;
import org.junit.Assume;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Win32ExceptionTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Win32ExceptionTest.class);
    }

    public void testFormatMessageFromLastErrorCode() {
        try {
            throw new Win32Exception(W32Errors.ERROR_SHARING_PAUSED);
        } catch (Win32Exception e) {
            if(AbstractWin32TestSupport.isEnglishLocale) {
                assertLastErrorValue(e, W32Errors.ERROR_SHARING_PAUSED,
                    "The remote server has been paused or is in the process of being started.");
            } else {
                System.err.println("testFormatMessageFromHR test can only be run with english locale.");
            }
        }
    }

    public void testFormatMessageFromHR() {
        try {
            throw new Win32Exception(W32Errors.S_OK);
        } catch (Win32Exception e) {
            if(AbstractWin32TestSupport.isEnglishLocale) {
                assertLastErrorValue(e, W32Errors.ERROR_SUCCESS, "The operation completed successfully.");
            } else {
                System.err.println("testFormatMessageFromHR test can only be run with english locale.");
            }
        }
    }

    public void testAddSuppressed() {
        Assume.assumeTrue(isMethodPresent(Win32Exception.class, "addSuppressed", Throwable.class));

        Win32Exception demoException = new Win32Exception(WinError.E_FAIL);
        demoException.addSuppressed(new RuntimeException("Demo"));

        assertEquals(1, demoException.getSuppressed().length);
        assertEquals("Demo", demoException.getSuppressed()[0].getMessage());
    }

    private void assertLastErrorValue(LastErrorException e, int code, String msg) {
        assertEquals("Mismatched error code", code, e.getErrorCode());
        assertEquals("Mismatched error message", msg, e.getMessage());
    }

    private boolean isMethodPresent(Class<?> baseClass, String methodName, Class... parameters) throws SecurityException {
        try {
            baseClass.getMethod(methodName, parameters);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }
}
