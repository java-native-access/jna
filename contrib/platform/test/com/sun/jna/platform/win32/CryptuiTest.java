/* Copyright (c) 2018 Roshan Muralidharan, All Rights Reserved
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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.platform.win32.WinCrypt.*;

import junit.framework.TestCase;

/**
 * @author roshan[dot]muralidharan[at]cerner[dot]com
 */
public class CryptuiTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(CryptuiTest.class);
    }

	public void testCryptUIDlgSelectCertificateFromStore() {
		CERT_CONTEXT.ByReference context = Cryptui.INSTANCE.CryptUIDlgSelectCertificateFromStore(null, null, "", "", 2, 0, null);

		assertNull("Context should be null as a valid certificate store handle was not provided.", context);
	}
}