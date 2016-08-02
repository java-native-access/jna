/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.LastErrorException;

import junit.framework.TestCase;

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

    private void assertLastErrorValue(LastErrorException e, int code, String msg) {
        assertEquals("Mismatched error code", code, e.getErrorCode());
        assertEquals("Mismatched error message", msg, e.getMessage());
    }
}
