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

import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS_PROCESS;

public class W32ServiceTest extends TestCase{
	W32ServiceManager _serviceManager = new W32ServiceManager();
	
	public void setUp() {
		_serviceManager.open(Winsvc.SC_MANAGER_CONNECT);
	}
	
	public void tearDown() {
		_serviceManager.close();
	}
	
	public void testStartService() {
		// TODO implement a test service and enable this test
//		W32Service service = _serviceManager.openService("eventlog", Winsvc.SERVICE_ALL_ACCESS);
//		service.startService();
//		assertEquals(service.queryStatus().dwCurrentState, Winsvc.SERVICE_RUNNING);
//		service.close();	
	}	
	
	public void testStopService() {
		// TODO implement a test service and enable this test
//		W32Service service = _serviceManager.openService("eventlog", Winsvc.SERVICE_ALL_ACCESS);
//		service.stopService();
//		assertEquals(service.queryStatus().dwCurrentState, Winsvc.SERVICE_STOPPED);
//		service.close();		
	}

	
	public void testPauseService() {
		// TODO implement a test service and enable this test
//		W32Service service = _serviceManager.openService("MSSQL$SQLEXPRESS", Winsvc.SERVICE_ALL_ACCESS);
//		service.pauseService();
//		assertEquals(service.queryStatus().dwCurrentState, Winsvc.SERVICE_PAUSED);
//		service.close();
	}
	
	public void testContinueService() {
		// TODO implement a test service and enable this test
//		W32Service service = _serviceManager.openService("MSSQL$SQLEXPRESS", Winsvc.SERVICE_ALL_ACCESS);
//		service.continueService();
//		assertEquals(service.queryStatus().dwCurrentState, Winsvc.SERVICE_RUNNING);
//		service.close();
	}
	
	public void testQueryStatus() {
		W32Service service = _serviceManager.openService("eventlog", Winsvc.SERVICE_QUERY_STATUS);
		SERVICE_STATUS_PROCESS status = service.queryStatus();
		assertTrue(status.dwCurrentState == Winsvc.SERVICE_RUNNING || 
				status.dwCurrentState == Winsvc.SERVICE_STOPPED);
		service.close();
	}
}
