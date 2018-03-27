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

import static com.sun.jna.platform.win32.WinNT.SERVICE_WIN32;
import com.sun.jna.platform.win32.Winsvc.ENUM_SERVICE_STATUS_PROCESS;
import static com.sun.jna.platform.win32.Winsvc.SC_MANAGER_CONNECT;
import static com.sun.jna.platform.win32.Winsvc.SC_MANAGER_ENUMERATE_SERVICE;
import static com.sun.jna.platform.win32.Winsvc.SERVICE_STATE_ALL;
import java.util.ArrayList;
import java.util.List;
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
        
        public void testEnumServices() {
                W32ServiceManager manager = new W32ServiceManager();
                
                // It is expected, that these services are present
                List<String> expectedServices = new ArrayList<String>(4);
                expectedServices.add("Schedule");
                expectedServices.add("SystemEventsBroker");
                expectedServices.add("Power");
                expectedServices.add("Netlogon");
                
                manager.open(SC_MANAGER_ENUMERATE_SERVICE | SC_MANAGER_CONNECT);
                try {
                        for(ENUM_SERVICE_STATUS_PROCESS essp: manager.enumServicesStatusExProcess(SERVICE_WIN32, SERVICE_STATE_ALL, null)) {
//                                System.out.printf("%-40s%-40s%n", essp.lpServiceName, essp.lpDisplayName );
                                assertNotNull(essp.lpDisplayName);
                                assertNotNull(essp.lpServiceName);
                                expectedServices.remove(essp.lpServiceName);
                        }
                } finally {
                        manager.close();
                }
                
                assertEquals( "Not all expected services were found: " + expectedServices,0, expectedServices.size());
        }
}
