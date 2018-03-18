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

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Winsvc.ENUM_SERVICE_STATUS_PROCESS;
import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.ptr.IntByReference;
import java.io.Closeable;

/**
 * Win32 Service Manager wrapper 
 * @author EugineLev
 */
public class W32ServiceManager implements Closeable {
	SC_HANDLE _handle = null; 
	String _machineName = null;
	String _databaseName = null;
	
        /**
         * Instantiate a W32ServiceManager for the local computer and the 
         * SERVICES_ACTIVE_DATABASE ("ServicesActive") database.
         * 
         * <p>The connection is not established until {@link #open(int)} is 
         * called.</p>
         */
	public W32ServiceManager() {
	}
	
        /**
         * Instantiate a W32ServiceManager for the local computer and the 
         * SERVICES_ACTIVE_DATABASE ("ServicesActive") database.
         * 
         * <p>A connection is opened directly with the requested permissions.</p>
         * 
         * @param permissions requested permissions for access
         */
	public W32ServiceManager(int permissions) {
                open(permissions);
	}
        
        /**
         * Instantiate a W32ServiceManager.
         *
         * @param machineName  The name of the target computer. If the pointer
         *                     is NULL or points to an empty string, the
         *                     function connects to the service control manager
         *                     on the local computer.
         * @param databaseName The name of the service control manager database.
         *                     This parameter should be set to "ServicesActive".
         *                     If it is NULL, the "ServicesActive"
         *                     (SERVICES_ACTIVE_DATABASE) database is opened by
         *                     default.
         * <p>The connection is not established until {@link #open(int)} is 
         * called.</p>
         */
	public W32ServiceManager(String machineName, String databaseName) {
		_machineName = machineName;
		_databaseName = databaseName;
	}
	
       /**
         * Instantiate a W32ServiceManager.
         * 
         * <p>
         * A connection is opened directly with the requested permissions.</p>
         *
         * @param machineName  The name of the target computer. If the pointer
         *                     is NULL or points to an empty string, the
         *                     function connects to the service control manager
         *                     on the local computer.
         * @param databaseName The name of the service control manager database.
         *                     This parameter should be set to "ServicesActive".
         *                     If it is NULL, the "ServicesActive"
         *                     (SERVICES_ACTIVE_DATABASE) database is opened by
         *                     default.
         * @param permissions  requested permissions for access
         */
	public W32ServiceManager(String machineName, String databaseName, int permissions) {
		_machineName = machineName;
		_databaseName = databaseName;
                open(permissions);
	}
        
	/**
	 * (Re-)Opens the Service Manager with the supplied permissions.
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
        @Override
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

        /**
         * Enumerates services in the specified service control manager database.
         * The name and status of each service are provided, along with additional
         * data based on the specified information level.
         *
         * @param dwServiceType      The type of services to be enumerated. This
         *                           parameter can be one or more of the following
         *                           values.
         * 
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>{@link WinNT#SERVICE_DRIVER}</td><td>Services of type {@link WinNT#SERVICE_KERNEL_DRIVER} and {@link WinNT#SERVICE_FILE_SYSTEM_DRIVER}.</td></tr>
         * <tr><td>{@link WinNT#SERVICE_FILE_SYSTEM_DRIVER}</td><td>File system driver services.</td></tr>
         * <tr><td>{@link WinNT#SERVICE_KERNEL_DRIVER}</td><td>Driver services.</td></tr>
         * <tr><td>{@link WinNT#SERVICE_WIN32}</td><td>Services of type {@link WinNT#SERVICE_WIN32_OWN_PROCESS} and {@link WinNT#SERVICE_WIN32_SHARE_PROCESS}.</td></tr>
         * <tr><td>{@link WinNT#SERVICE_WIN32_OWN_PROCESS}</td><td>Services that run in their own processes.</td></tr>
         * <tr><td>{@link WinNT#SERVICE_WIN32_SHARE_PROCESS}</td><td>Services that share a process with one or more other services. For more information, see Service Programs.</td></tr>
         * </table>
         * 
         * @param dwServiceState     The state of the services to be enumerated.
         *                           This parameter can be one of the following
         *                           values.
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>{@link Winsvc#SERVICE_ACTIVE}</td><td>Enumerates services that
         * are in the following states:
         * {@link Winsvc#SERVICE_START_PENDING}, {@link Winsvc#SERVICE_STOP_PENDING}, {@link Winsvc#SERVICE_RUNNING}, {@link Winsvc#SERVICE_CONTINUE_PENDING}, {@link Winsvc#SERVICE_PAUSE_PENDING},
         * and {@link Winsvc#SERVICE_PAUSED}.</td></tr>
         * <tr><td>{@link Winsvc#SERVICE_INACTIVE}</td><td>Enumerates services that
         * are in the {@link Winsvc#SERVICE_STOPPED} state.</td></tr>
         * <tr><td>{@link Winsvc#SERVICE_STATE_ALL}</td><td>Combines the following
         * states: {@link Winsvc#SERVICE_ACTIVE} and
         * {@link Winsvc#SERVICE_INACTIVE}.</td></tr>
         * </table>
         * @param groupName          The load-order group name. If this parameter is
         *                           a string, the only services enumerated are
         *                           those that belong to the group that has the
         *                           name specified by the string. If this parameter
         *                           is an empty string, only services that do not
         *                           belong to any group are enumerated. If this
         *                           parameter is NULL, group membership is ignored
         *                           and all services are enumerated.
         * 
         * @return array of ENUM_SERVICE_STATUS_PROCESS structures.
         */
        public ENUM_SERVICE_STATUS_PROCESS[] enumServicesStatusExProcess(int dwServiceType, int dwServiceState, String groupName) {
                IntByReference pcbBytesNeeded = new IntByReference(0);
                IntByReference lpServicesReturned = new IntByReference(0);
                IntByReference lpResumeHandle = new IntByReference(0);
                Advapi32.INSTANCE.EnumServicesStatusEx(_handle, Winsvc.SC_ENUM_PROCESS_INFO, dwServiceType, dwServiceState, Pointer.NULL, 0, pcbBytesNeeded, lpServicesReturned,lpResumeHandle,groupName);
                int lastError = Kernel32.INSTANCE.GetLastError();
                if (lastError != WinError.ERROR_MORE_DATA) {
                    throw new Win32Exception(lastError);
                }
                Memory buffer = new Memory(pcbBytesNeeded.getValue());
                boolean result = Advapi32.INSTANCE.EnumServicesStatusEx(_handle, Winsvc.SC_ENUM_PROCESS_INFO, dwServiceType, dwServiceState, buffer, (int) buffer.size(), pcbBytesNeeded, lpServicesReturned,lpResumeHandle,groupName);
                if (!result) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                if (lpServicesReturned.getValue() == 0) {
                    return new Winsvc.ENUM_SERVICE_STATUS_PROCESS[0];
                }
                ENUM_SERVICE_STATUS_PROCESS status = Structure.newInstance(ENUM_SERVICE_STATUS_PROCESS.class, buffer);
                status.read();
                return (ENUM_SERVICE_STATUS_PROCESS[]) status.toArray(lpServicesReturned.getValue());
        }
}