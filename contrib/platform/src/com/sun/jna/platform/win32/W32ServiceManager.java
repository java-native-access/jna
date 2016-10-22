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