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

import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.platform.win32.Winsvc.SC_STATUS_TYPE;
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
