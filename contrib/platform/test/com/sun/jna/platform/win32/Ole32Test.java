/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 * @author markus[at]headcrashing[dot]eu
 */
public class Ole32Test extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(Ole32Test.class);
	}

	public void testCoCreateGUID() {
		GUID pguid = new GUID();
		assertEquals(W32Errors.S_OK, Ole32.INSTANCE.CoCreateGuid(pguid));
		assertTrue(pguid.Data1 != 0 || pguid.Data2 != 0 || pguid.Data3 != 0
				&& pguid.Data4 != null);
	}

	public void testIIDFromString() {
		GUID.ByReference lpiid = new GUID.ByReference();
		assertEquals(W32Errors.S_OK, Ole32.INSTANCE.IIDFromString(
				"{13709620-C279-11CE-A49E-444553540000}", lpiid)); // Shell.Application.1
		assertEquals(0x13709620, lpiid.Data1);
		assertEquals(0xFFFFC279, lpiid.Data2);
		assertEquals(0x11CE, lpiid.Data3);
		assertEquals(0xFFFFFFA4, lpiid.Data4[0]);
		assertEquals(0xFFFFFF9E, lpiid.Data4[1]);
		assertEquals(0x44, lpiid.Data4[2]);
		assertEquals(0x45, lpiid.Data4[3]);
		assertEquals(0x53, lpiid.Data4[4]);
		assertEquals(0x54, lpiid.Data4[5]);
		assertEquals(0, lpiid.Data4[6]);
		assertEquals(0, lpiid.Data4[7]);
	}

	public void testStringFromGUID2() {
		GUID.ByReference pguid = new GUID.ByReference();
		pguid.Data1 = 0;
		pguid.Data2 = 0;
		pguid.Data3 = 0;
		for (int i = 0; i < pguid.Data4.length; i++) {
			pguid.Data4[i] = 0;
		}
		int max = 39;
		char[] lpsz = new char[max];
		int len = Ole32.INSTANCE.StringFromGUID2(pguid, lpsz, max);
		assertTrue(len > 1);
		lpsz[len - 1] = 0;
		assertEquals("{00000000-0000-0000-0000-000000000000}",
				Native.toString(lpsz));
	}

	public void testCoInitializeEx() {
		HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, 0);
		assertTrue(W32Errors.SUCCEEDED(hr.intValue())
				|| hr.intValue() == W32Errors.RPC_E_CHANGED_MODE);
		if (W32Errors.SUCCEEDED(hr.intValue()))
			Ole32.INSTANCE.CoUninitialize();
	}

	public void testCoCreateInstance() {
		HRESULT hrCI = Ole32.INSTANCE.CoInitializeEx(null, 0);

		GUID guid = Ole32Util
				.getGUIDFromString("{00021401-0000-0000-C000-000000000046}"); // Shell object
		GUID riid = Ole32Util
				.getGUIDFromString("{000214EE-0000-0000-C000-000000000046}"); // IShellLinkA

		PointerByReference pDispatch = new PointerByReference();

		HRESULT hr = Ole32.INSTANCE.CoCreateInstance(guid, null, // pOuter =
																	// null, no
																	// aggregation
				WTypes.CLSCTX_LOCAL_SERVER, riid, pDispatch);
		assertTrue(W32Errors.SUCCEEDED(hr.intValue()));
		assertTrue(!pDispatch.equals(Pointer.NULL));
		// We leak this iUnknown reference because we don't have the JNACOM lib
		// here to wrap the native iUnknown pointer and call iUnknown.release()
		if (W32Errors.SUCCEEDED(hrCI.intValue()))
			Ole32.INSTANCE.CoUninitialize();
	}

	public final void testCLSIDFromProgID() {
	    final Guid.CLSID.ByReference clsid = new Guid.CLSID.ByReference();  
	    assertEquals(WinError.S_OK, Ole32.INSTANCE.CLSIDFromProgID("jpegfile", clsid));
	    assertEquals("{25336920-03F9-11CF-8FD0-00AA00686F13}", clsid.toGuidString());
	}

    public void testCoTaskMemAlloc() {
        Pointer ptr = Ole32.INSTANCE.CoTaskMemAlloc(256);

        assertTrue(!ptr.equals(Pointer.NULL));

        Ole32.INSTANCE.CoTaskMemFree(ptr);
    }

    public void testCoTaskMemRealloc() {
        Pointer ptr = Ole32.INSTANCE.CoTaskMemAlloc(256);

        assertTrue(!ptr.equals(Pointer.NULL));

        ptr = Ole32.INSTANCE.CoTaskMemRealloc(ptr, 128);

        assertTrue(!ptr.equals(Pointer.NULL));

        Ole32.INSTANCE.CoTaskMemFree(ptr);
    }
}
