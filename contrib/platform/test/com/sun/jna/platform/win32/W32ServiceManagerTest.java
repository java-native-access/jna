/* Copyright (c) 2010 EugineLev, All Rights Reserved
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
        if (VersionHelpers.IsWindows8OrGreater()) {
            expectedServices.add("SystemEventsBroker");
        }
        expectedServices.add("Power");
        expectedServices.add("Netlogon");

        manager.open(SC_MANAGER_ENUMERATE_SERVICE | SC_MANAGER_CONNECT);
        try {
            for (ENUM_SERVICE_STATUS_PROCESS essp : manager.enumServicesStatusExProcess(SERVICE_WIN32, SERVICE_STATE_ALL, null)) {
//                                System.out.printf("%-40s%-40s%n", essp.lpServiceName, essp.lpDisplayName );
                assertNotNull(essp.lpDisplayName);
                assertNotNull(essp.lpServiceName);
                expectedServices.remove(essp.lpServiceName);
            }
        } finally {
            manager.close();
        }

        assertEquals("Not all expected services were found: " + expectedServices, 0, expectedServices.size());
    }
}
