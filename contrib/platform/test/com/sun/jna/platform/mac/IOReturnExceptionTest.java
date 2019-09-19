/*
 * Copyright (c) 2019 Daniel Widdis
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
package com.sun.jna.platform.mac;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IOReturnExceptionTest {

    @Test
    public void testException() {
        try {
            throw new IOReturnException(IOKit.kIOReturnNoDevice);
        } catch (IOReturnException e) {
            int code = e.getIOReturnCode();
            assertEquals(0xe00002c0, code);
            assertEquals(0x38, IOReturnException.getSystem(code)); // io_kit
            assertEquals(0x0, IOReturnException.getSubSystem(code)); // sub_iokit_common
            assertEquals(0x2c0, IOReturnException.getCode(code)); // kIOReturnNoDevice
            assertEquals("IOReturn error code: -536870208 (system=56, subSystem=0, code=704)", e.getMessage());
        }
    }
}
