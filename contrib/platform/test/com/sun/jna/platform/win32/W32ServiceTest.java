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

import com.sun.jna.platform.win32.Winsvc.ENUM_SERVICE_STATUS;
import java.util.List;
import java.util.LinkedList;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Winsvc.SC_ACTION;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS_PROCESS;
import com.sun.jna.platform.win32.Winsvc.SERVICE_FAILURE_ACTIONS;

public class W32ServiceTest extends TestCase {

    private final W32ServiceManager _serviceManager = new W32ServiceManager();

    @Override
    public void setUp() {
        _serviceManager.open(Winsvc.SC_MANAGER_CONNECT);
    }

    @Override
    public void tearDown() {
        _serviceManager.close();
    }

    public void testCreateServiceDeleteService() {
        // This tests:
        // - com.sun.jna.platform.win32.Advapi32.CreateService
        // - com.sun.jna.platform.win32.Advapi32.DeleteService
        // - com.sun.jna.platform.win32.Advapi32.SERVICE_DESCRIPTION
        Win32ServiceDemo.uninstall();
        assertTrue(Win32ServiceDemo.install());
        assertTrue(Win32ServiceDemo.uninstall());
    }

    public void testControlService() {
        // Cleanup in case of an unsuccessful previous run
        Win32ServiceDemo.uninstall();
        Win32ServiceDemo.install();
        // This test implicitly tests the "service side" functions/members:
        // - com.sun.jna.platform.win32.Advapi32.StartServiceCtrlDispatcher
        // - com.sun.jna.platform.win32.Advapi32.SERVICE_TABLE_ENTRY
        // - com.sun.jna.platform.win32.Advapi32.RegisterServiceCtrlHandlerEx
        // - com.sun.jna.platform.win32.Advapi32.SetServiceStatus
        // - com.sun.jna.platform.win32.Advapi32.SERVICE_MAIN_FUNCTION
        // - com.sun.jna.platform.win32.Advapi32.HandlerEx
        // - com.sun.jna.platform.win32.Advapi32.SERVICE_STATUS_HANDLE
        W32Service service = _serviceManager.openService(Win32ServiceDemo.serviceName, Winsvc.SERVICE_ALL_ACCESS);
        service.startService();
        assertEquals(service.queryStatus().dwCurrentState, Winsvc.SERVICE_RUNNING);
        service.pauseService();
        assertEquals(service.queryStatus().dwCurrentState, Winsvc.SERVICE_PAUSED);
        service.continueService();
        assertEquals(service.queryStatus().dwCurrentState, Winsvc.SERVICE_RUNNING);
        service.stopService();
        assertEquals(service.queryStatus().dwCurrentState, Winsvc.SERVICE_STOPPED);
        service.close();
        Win32ServiceDemo.uninstall();
    }

    public void testQueryStatus() {
        W32Service service = _serviceManager.openService("eventlog", Winsvc.SERVICE_QUERY_STATUS);
        SERVICE_STATUS_PROCESS status = service.queryStatus();
        assertTrue(status.dwCurrentState == Winsvc.SERVICE_RUNNING
                || status.dwCurrentState == Winsvc.SERVICE_STOPPED);
        service.close();
    }

    public void testSetAndGetFailureActions() {
        final String svcId = "w32time";
        final String rebootMsg = "Restarting " + svcId + " due to service failure";
        final String command = "echo " + svcId + " failure";
        final int resetPeriod = 5000;

        W32Service service = _serviceManager.openService(svcId, Winsvc.SC_MANAGER_ALL_ACCESS);
        SERVICE_FAILURE_ACTIONS prevActions = service.getFailureActions();

        List<SC_ACTION> actions = new LinkedList<>();

        SC_ACTION action = new SC_ACTION();
        action.type = Winsvc.SC_ACTION_RESTART;
        action.delay = 1000;
        actions.add(action);

        action = new SC_ACTION();
        action.type = Winsvc.SC_ACTION_REBOOT;
        action.delay = 2000;
        actions.add(action);

        action = new SC_ACTION();
        action.type = Winsvc.SC_ACTION_RUN_COMMAND;
        action.delay = 3000;
        actions.add(action);

        action = new SC_ACTION();
        action.type = Winsvc.SC_ACTION_NONE;
        action.delay = 4000;
        actions.add(action);

        service.setFailureActions(actions, resetPeriod, rebootMsg, command);

        SERVICE_FAILURE_ACTIONS changedActions = service.getFailureActions();
        assertEquals(changedActions.lpRebootMsg, rebootMsg);
        assertEquals(changedActions.lpCommand, command);
        assertEquals(changedActions.dwResetPeriod, resetPeriod);
        assertEquals(changedActions.cActions, 4);
        SC_ACTION[] actionArray = (SC_ACTION[]) changedActions.lpsaActions.toArray(changedActions.cActions);
        assertEquals(actionArray[0].type, Winsvc.SC_ACTION_RESTART);
        assertEquals(actionArray[0].delay, 1000);
        assertEquals(actionArray[1].type, Winsvc.SC_ACTION_REBOOT);
        assertEquals(actionArray[1].delay, 2000);
        assertEquals(actionArray[2].type, Winsvc.SC_ACTION_RUN_COMMAND);
        assertEquals(actionArray[2].delay, 3000);
        assertEquals(actionArray[3].type, Winsvc.SC_ACTION_NONE);
        assertEquals(actionArray[3].delay, 4000);

        // restore old settings
        Advapi32.INSTANCE.ChangeServiceConfig2(service._handle, Winsvc.SERVICE_CONFIG_FAILURE_ACTIONS,
                prevActions);

        service.close();
    }

    public void testSetFailureActionsFlag() {
        W32Service service = _serviceManager.openService("eventlog", Winsvc.SC_MANAGER_ALL_ACCESS);
        boolean prevFlag = service.getFailureActionsFlag();
        service.setFailureActionsFlag(!prevFlag);
        assertTrue(prevFlag != service.getFailureActionsFlag());
        service.setFailureActionsFlag(prevFlag);
        service.close();
    }

    public void testEnumDependendServices() {
        W32Service service = _serviceManager.openService("SystemEventsBroker", Winsvc.SERVICE_ENUMERATE_DEPENDENTS);
        ENUM_SERVICE_STATUS[] dependants = service.enumDependentServices(Winsvc.SERVICE_STATE_ALL);
        assertTrue(dependants.length > 0);
        for(ENUM_SERVICE_STATUS ess: dependants) {
//            System.out.printf("%-40s%-40s%n", ess.lpServiceName, ess.lpDisplayName);
            assertNotNull(ess.lpDisplayName);
            assertNotNull(ess.lpServiceName);
        }
    }
}
