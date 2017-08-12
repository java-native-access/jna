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

import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LUID;
import com.sun.jna.platform.win32.WinNT.LUID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.TOKEN_PRIVILEGES;
import com.sun.jna.platform.win32.Winsvc.SC_ACTION;
import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.platform.win32.Winsvc.SC_STATUS_TYPE;
import com.sun.jna.platform.win32.Winsvc.SERVICE_FAILURE_ACTIONS;
import com.sun.jna.platform.win32.Winsvc.SERVICE_FAILURE_ACTIONS_FLAG;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS_PROCESS;
import com.sun.jna.ptr.IntByReference;


/**
 * Win32 Service wrapper 
 * @author EugineLev
 */
public class W32Service {
	SC_HANDLE _handle = null;

	/**
	 * Win32 Service
	 * @param handle
	 *  A handle to the service. This handle is returned by the CreateService or OpenService 
	 *  function, and it must have the SERVICE_QUERY_STATUS access right.
	 */
	public W32Service(SC_HANDLE handle) {
		_handle = handle;
	}
	
	/**
	 * Close service.
	 */
	public void close() {
		if (_handle != null) {
			if (! Advapi32.INSTANCE.CloseServiceHandle(_handle)) {
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
			}
			_handle = null;
		}
	}

	private void addShutdownPrivilegeToProcess() {
		HANDLEByReference hToken = new HANDLEByReference();
		LUID luid = new LUID();
		Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(),
				WinNT.TOKEN_ADJUST_PRIVILEGES, hToken);
		Advapi32.INSTANCE.LookupPrivilegeValue("", WinNT.SE_SHUTDOWN_NAME, luid);
		TOKEN_PRIVILEGES tp = new TOKEN_PRIVILEGES(1);
		tp.Privileges[0] = new LUID_AND_ATTRIBUTES(luid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
		Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tp, tp.size(), null,
				new IntByReference());
	}

	/**
	 * Set the failure actions of the specified service. Corresponds to 
	 * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms681988.aspx">ChangeServiceConfig2</a>
	 * with parameter dwInfoLevel set to SERVICE_CONFIG_FAILURE_ACTIONS. 
	 */
	public void setFailureActions(List<SC_ACTION> actions, int resetPeriod, String rebootMsg, 
			String command) {
		SERVICE_FAILURE_ACTIONS.ByReference actionStruct = new SERVICE_FAILURE_ACTIONS.ByReference();
		actionStruct.dwResetPeriod = resetPeriod;
		actionStruct.lpRebootMsg = rebootMsg;
		actionStruct.lpCommand = command;
		actionStruct.cActions = actions.size();

		actionStruct.lpsaActions = new SC_ACTION.ByReference();
		SC_ACTION[] actionArray = (SC_ACTION[])actionStruct.lpsaActions.toArray(actions.size());
		boolean hasShutdownPrivilege = false;
		int i = 0;
		for (SC_ACTION action : actions) {
			if (!hasShutdownPrivilege && action.type == Winsvc.SC_ACTION_REBOOT) {
				addShutdownPrivilegeToProcess();
				hasShutdownPrivilege = true;
			}
			actionArray[i].type = action.type;
			actionArray[i].delay = action.delay;
			i++;
		}

		if (!Advapi32.INSTANCE.ChangeServiceConfig2(_handle, Winsvc.SERVICE_CONFIG_FAILURE_ACTIONS, 
				actionStruct)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
	}

	private Pointer queryServiceConfig2(int type) {
		IntByReference bufferSize = new IntByReference();
		Advapi32.INSTANCE.QueryServiceConfig2(_handle, type, Pointer.NULL, 0, bufferSize);

		Pointer buffer = new Memory(bufferSize.getValue());

		if (!Advapi32.INSTANCE.QueryServiceConfig2(_handle, type, buffer, bufferSize.getValue(),
				new IntByReference())) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}

		return buffer;
	}

	/**
	 * Get the failure actions of the specified service. Corresponds to 
	 * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms681988.aspx">QueryServiceConfig2</a>
	 * with parameter dwInfoLevel set to SERVICE_CONFIG_FAILURE_ACTIONS. 
	 */
	public SERVICE_FAILURE_ACTIONS getFailureActions() {
		Pointer buffer = queryServiceConfig2(Winsvc.SERVICE_CONFIG_FAILURE_ACTIONS);
		SERVICE_FAILURE_ACTIONS result = new SERVICE_FAILURE_ACTIONS(buffer);
		return result;
	}

	/**
	 * Set the failure action flag of the specified service. Corresponds to 
	 * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms681988.aspx">ChangeServiceConfig2</a>
	 * with parameter dwInfoLevel set to SERVICE_CONFIG_FAILURE_ACTIONS_FLAG. 
	 */
	public void setFailureActionsFlag(boolean flagValue) {
		SERVICE_FAILURE_ACTIONS_FLAG flag = new SERVICE_FAILURE_ACTIONS_FLAG();
		flag.fFailureActionsOnNonCrashFailures = flagValue ? 1 : 0;

		if (!Advapi32.INSTANCE.ChangeServiceConfig2(_handle, Winsvc.SERVICE_CONFIG_FAILURE_ACTIONS_FLAG, 
				flag)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
	}

	/**
	 * Get the failure actions flag of the specified service. Corresponds to 
	 * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms681988.aspx">QueryServiceConfig2</a>
	 * with parameter dwInfoLevel set to SERVICE_CONFIG_FAILURE_ACTIONS_FLAG. 
	 */
	public boolean getFailureActionsFlag() {
		Pointer buffer = queryServiceConfig2(Winsvc.SERVICE_CONFIG_FAILURE_ACTIONS_FLAG);
		SERVICE_FAILURE_ACTIONS_FLAG result = new SERVICE_FAILURE_ACTIONS_FLAG(buffer);
		return result.fFailureActionsOnNonCrashFailures != 0;
	}

	/**
	 * Retrieves the current status of the specified service based on the specified information level.
	 * @return 
	 *  Service status information
	 */
	public SERVICE_STATUS_PROCESS queryStatus() {
		IntByReference size = new IntByReference();
		
		Advapi32.INSTANCE.QueryServiceStatusEx(_handle, SC_STATUS_TYPE.SC_STATUS_PROCESS_INFO,
				null, 0, size);
		
		SERVICE_STATUS_PROCESS status = new SERVICE_STATUS_PROCESS(size.getValue());
		if(! Advapi32.INSTANCE.QueryServiceStatusEx(_handle, SC_STATUS_TYPE.SC_STATUS_PROCESS_INFO,
				status, status.size(), size)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		
		return status;
	}
	
	public void startService() {
		waitForNonPendingState();
		// If the service is already running - return
		if (queryStatus().dwCurrentState == Winsvc.SERVICE_RUNNING) {
			return;
		}
		if (! Advapi32.INSTANCE.StartService(_handle, 0, null)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		waitForNonPendingState();
		if (queryStatus().dwCurrentState != Winsvc.SERVICE_RUNNING) {
			throw new RuntimeException("Unable to start the service");
		}
	}
	
	/**
	 * Stop service.
	 */
	public void stopService() {
		waitForNonPendingState();
		// If the service is already stopped - return
		if (queryStatus().dwCurrentState == Winsvc.SERVICE_STOPPED) {
			return;
		}
		if (! Advapi32.INSTANCE.ControlService(_handle, Winsvc.SERVICE_CONTROL_STOP, 
				new Winsvc.SERVICE_STATUS())) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		waitForNonPendingState();
		if (queryStatus().dwCurrentState != Winsvc.SERVICE_STOPPED) {
			throw new RuntimeException("Unable to stop the service");
		}
	}

	/**
	 * Continue service.
	 */
	public void continueService() {
		waitForNonPendingState();
		// If the service is already stopped - return
		if (queryStatus().dwCurrentState == Winsvc.SERVICE_RUNNING) {
			return;
		}
		if (! Advapi32.INSTANCE.ControlService(_handle, Winsvc.SERVICE_CONTROL_CONTINUE, 
				new Winsvc.SERVICE_STATUS())) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		waitForNonPendingState();
		if (queryStatus().dwCurrentState != Winsvc.SERVICE_RUNNING) {
			throw new RuntimeException("Unable to continue the service");
		}
	}
	
	/**
	 * Pause service.
	 */
	public void pauseService() {
		waitForNonPendingState();
		// If the service is already paused - return
		if (queryStatus().dwCurrentState == Winsvc.SERVICE_PAUSED) {
			return;
		}
		if (! Advapi32.INSTANCE.ControlService(_handle, Winsvc.SERVICE_CONTROL_PAUSE, 
				new Winsvc.SERVICE_STATUS())) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		waitForNonPendingState();
		if (queryStatus().dwCurrentState != Winsvc.SERVICE_PAUSED) {
			throw new RuntimeException("Unable to pause the service");
		}
	}

    /**
     * Wait for the state to change to something other than a pending state.
     */
	public void waitForNonPendingState() {

		SERVICE_STATUS_PROCESS status = queryStatus(); 

		int previousCheckPoint = status.dwCheckPoint;
		int checkpointStartTickCount = Kernel32.INSTANCE.GetTickCount();;

		while (isPendingState(status.dwCurrentState)) { 

			// if the checkpoint advanced, start new tick count
			if (status.dwCheckPoint != previousCheckPoint) {
				previousCheckPoint = status.dwCheckPoint;
				checkpointStartTickCount = Kernel32.INSTANCE.GetTickCount();
			}

			// if the time that passed is greater than the wait hint - throw timeout exception
			if (Kernel32.INSTANCE.GetTickCount() - checkpointStartTickCount > status.dwWaitHint) {
				throw new RuntimeException("Timeout waiting for service to change to a non-pending state.");
			}

			// do not wait longer than the wait hint. A good interval is 
			// one-tenth the wait hint, but no less than 1 second and no 
			// more than 10 seconds. 

			int dwWaitTime = status.dwWaitHint / 10;

			if (dwWaitTime < 1000)
				dwWaitTime = 1000;
			else if (dwWaitTime > 10000)
				dwWaitTime = 10000;

			try {
				Thread.sleep( dwWaitTime );
			} catch (InterruptedException e){
				throw new RuntimeException(e);
			}

			status = queryStatus();
		}
	}

	private boolean isPendingState(int state) {
		switch (state) {
			case Winsvc.SERVICE_CONTINUE_PENDING:
			case Winsvc.SERVICE_STOP_PENDING:
			case Winsvc.SERVICE_PAUSE_PENDING:
			case Winsvc.SERVICE_START_PENDING:		
				return true;
			default:
				return false;
		}
	}
	
	
	/**
	 * Gets the service handle.
	 * @return 
	 *  Returns the service handle.
	 */
	public SC_HANDLE getHandle() {
		return _handle;
	}
}
