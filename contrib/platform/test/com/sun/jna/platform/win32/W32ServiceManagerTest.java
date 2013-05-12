/* Copyright (c) 2010 EugineLev, All Rights Reserved
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

public class W32ServiceManagerTest extends TestCase {
	
	public void testOpenClose() {
		W32ServiceManager manager = new W32ServiceManager();
		
		manager.close();
		
		manager.open(Winsvc.SC_MANAGER_CONNECT);
		manager.open(Winsvc.SC_MANAGER_CONNECT);
		
		manager.close();
		manager.close();
	}
	
	public void testOpenService() {
		W32ServiceManager manager = new W32ServiceManager();
		
		manager.open(Winsvc.SC_MANAGER_CONNECT);
		try {
			manager.openService("invalidServiceName", Winsvc.SERVICE_QUERY_CONFIG);
			fail("Open service should have failed due to invalid service name.");
		} catch (Win32Exception e) {
			assertEquals(W32Errors.HRESULT_FROM_WIN32(W32Errors.ERROR_SERVICE_DOES_NOT_EXIST), e.getHR());
		} finally {
			manager.close();
		}
	}
}
