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

/**
 * Win32 Service Manager wrapper 
 * @author EugineLev
 */
public class W32ServiceManager {
	SC_HANDLE _handle = null; 
	String _machineName = null;
	String _databaseName = null;
	
	public W32ServiceManager() {
	}
	
	public W32ServiceManager(String machineName, String databaseName) {
		_machineName = machineName;
		_databaseName = databaseName;
	}
	
	/**
	 * Opens the Service Manager with the supplied permissions.
	 * @param permissions
	 * 	Permissions.
	 */
	public void open(int permissions) {
		close();
		
		_handle = Advapi32.INSTANCE.OpenSCManager(
				_machineName, _databaseName, permissions);

		if (_handle == null) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
	}
	
	/**
	 * Closes the previously opened Service Manager.
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
	 * Opens a Service.  
	 * @param serviceName
	 *  Service name.
	 * @param permissions
	 *  Permissions.
	 * @return
	 *  Returns an opened service.
	 */
	public W32Service openService(String serviceName, int permissions) {
		SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService( 
				_handle, serviceName, permissions);
		
		if (serviceHandle == null) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		
		return new W32Service(serviceHandle);
	}
	
	/**
	 * Gets the service manager handle.
	 * @return 
	 *  Returns the service manager handle.
	 */
	public SC_HANDLE getHandle() {
		return _handle;
	}	
}