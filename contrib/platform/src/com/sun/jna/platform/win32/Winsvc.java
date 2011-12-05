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


import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

/**
 * This module defines the 32-Bit Windows types and constants that are defined
 * by NT, but exposed through the Win32 API.
 * Ported from Winsvc.h.
 * Microsoft Windows SDK 7.0A.
 * @author EugineLev
 */
public interface Winsvc extends StdCallLibrary {	

    /**
     *  Contains status information for a service. The ControlService, EnumDependentServices,
     *  EnumServicesStatus, and QueryServiceStatus functions use this structure. A service
     *  uses this structure in the SetServiceStatus function to report its current status 
     *  to the service control manager.
     */
    public static class SERVICE_STATUS extends Structure {
		
        /**
         * dwServiceType - the type of service. This member can be one 
         * of the following values:
         * SERVICE_KERNEL_DRIVER, SERVICE_FILE_SYSTEM_DRIVER, 
         * SERVICE_WIN32_OWN_PROCESS, SERVICE_WIN32_SHARE_PROCESS, 

         * If the service type is either SERVICE_WIN32_OWN_PROCESS or 
         * SERVICE_WIN32_SHARE_PROCESS, and the service is running in the 
         * context of the LocalSystem account, the following type may also 
         * be specified:
         * SERVICE_INTERACTIVE_PROCESS
         * 
         * These values can be found in WinNT.h
         */
        public int dwServiceType;
		
        /**
         * dwCurrentState - The current state of the service. 
         * This member can be one of the following values:
         * SERVICE_STOPPED, SERVICE_START_PENDING, SERVICE_STOP_PENDING, SERVICE_RUNNING,
         * SERVICE_CONTINUE_PENDING, SERVICE_PAUSE_PENDING, SERVICE_PAUSED	
         */
        public int dwCurrentState;
		
        /**
         * dwControlsAccepted - The control codes the service accepts and processes 
         * in its handler function:
         * SERVICE_ACCEPT_STOP, SERVICE_ACCEPT_PAUSE_CONTINUE, SERVICE_ACCEPT_SHUTDOWN,
         * SERVICE_ACCEPT_PARAMCHANGE,  SERVICE_ACCEPT_NETBINDCHANGE, SERVICE_ACCEPT_HARDWAREPROFILECHANGE,
         * SERVICE_ACCEPT_POWEREVENT, SERVICE_ACCEPT_SESSIONCHANGE, SERVICE_ACCEPT_PRESHUTDOWN,
         * SERVICE_ACCEPT_TIMECHANGE, SERVICE_ACCEPT_TRIGGEREVENT
         */
        public int dwControlsAccepted;
		
        /**
         * dwWin32ExitCode - The error code the service uses to report an error that occurs 
         * when it is starting or stopping. To return an error code specific to the service,
         * the service must set this value to ERROR_SERVICE_SPECIFIC_ERROR to indicate that
         * the dwServiceSpecificExitCode member contains the error code. The service should
         * set this value to NO_ERROR when it is running and on normal termination.
         */
        public int dwWin32ExitCode;
		
        /**
         * dwServiceSpecificExitCode - A service-specific error code that the service returns
         * when an error occurs while the service is starting or stopping. This value is
         * ignored unless the dwWin32ExitCode member is set to ERROR_SERVICE_SPECIFIC_ERROR.
         */
        public int dwServiceSpecificExitCode;
		
        /**
         * dwCheckPoint - The check-point value the service increments periodically to report
         * its progress during a lengthy start, stop, pause, or continue operation.
         */
        public int dwCheckPoint;
		
        /**
         * dwWaitHint - The estimated time required for a pending start, stop, pause, or continue 
         * operation, in milliseconds.
         */
        public int dwWaitHint;

        public SERVICE_STATUS() {
            super();
        }

    }

    /**
     * Contains process status information for a service. The ControlServiceEx, 
     * EnumServicesStatusEx, NotifyServiceStatusChange, and QueryServiceStatusEx
     * functions use this structure.
     */
    public class SERVICE_STATUS_PROCESS extends Structure {
        /**
         * dwServiceType - the type of service. This member can be one 
         * of the following values:
         * SERVICE_KERNEL_DRIVER, SERVICE_FILE_SYSTEM_DRIVER, 
         * SERVICE_WIN32_OWN_PROCESS, SERVICE_WIN32_SHARE_PROCESS, 

         * If the service type is either SERVICE_WIN32_OWN_PROCESS or 
         * SERVICE_WIN32_SHARE_PROCESS, and the service is running in the 
         * context of the LocalSystem account, the following type may also 
         * be specified:
         * SERVICE_INTERACTIVE_PROCESS
         * 
         * These values can be found in WinNT.h
         */
        public int   dwServiceType;
		
        /**
         * dwCurrentState - The current state of the service. 
         * This member can be one of the following values:
         * SERVICE_STOPPED, SERVICE_START_PENDING, SERVICE_STOP_PENDING, SERVICE_RUNNING,
         * SERVICE_CONTINUE_PENDING, SERVICE_PAUSE_PENDING, SERVICE_PAUSED	
         */
        public int   dwCurrentState;
		
        /**
         * dwControlsAccepted - The control codes the service accepts and processes 
         * in its handler function:
         * SERVICE_ACCEPT_STOP, SERVICE_ACCEPT_PAUSE_CONTINUE, SERVICE_ACCEPT_SHUTDOWN,
         * SERVICE_ACCEPT_PARAMCHANGE,  SERVICE_ACCEPT_NETBINDCHANGE, SERVICE_ACCEPT_HARDWAREPROFILECHANGE,
         * SERVICE_ACCEPT_POWEREVENT, SERVICE_ACCEPT_SESSIONCHANGE, SERVICE_ACCEPT_PRESHUTDOWN,
         * SERVICE_ACCEPT_TIMECHANGE, SERVICE_ACCEPT_TRIGGEREVENT
         */
        public int   dwControlsAccepted;
		
        /**
         * dwWin32ExitCode - The error code the service uses to report an error that occurs 
         * when it is starting or stopping. To return an error code specific to the service,
         * the service must set this value to ERROR_SERVICE_SPECIFIC_ERROR to indicate that
         * the dwServiceSpecificExitCode member contains the error code. The service should
         * set this value to NO_ERROR when it is running and on normal termination.
         */
        public int   dwWin32ExitCode;
		
        /**
         * dwServiceSpecificExitCode - A service-specific error code that the service returns
         * when an error occurs while the service is starting or stopping. This value is
         * ignored unless the dwWin32ExitCode member is set to ERROR_SERVICE_SPECIFIC_ERROR.
         */
        public int   dwServiceSpecificExitCode;
		
        /**
         * dwCheckPoint - The check-point value the service increments periodically to report
         * its progress during a lengthy start, stop, pause, or continue operation.
         */
        public int   dwCheckPoint;
		
        /**
         * dwWaitHint - The estimated time required for a pending start, stop, pause, or continue 
         * operation, in milliseconds.
         */
        public int   dwWaitHint;
		
        /**
         * dwProcessId - The process identifier of the service.
         */
        public int   dwProcessId;
		
        /**
         * This member can be one of the following values: 0, or SERVICE_RUNS_IN_SYSTEM_PROCESS
         */
        public int   dwServiceFlags;
		
        public SERVICE_STATUS_PROCESS() {
        }
		
        public SERVICE_STATUS_PROCESS(int size) {
            super(new Memory(size));
        }
    }
	
    //
    // Service flags for QueryServiceStatusEx
    //
    int SERVICE_RUNS_IN_SYSTEM_PROCESS = 0x00000001;
	
    public static class SC_HANDLE extends HANDLE { }
	
    //
    // Service Control Manager object specific access types
    //
    int SC_MANAGER_CONNECT				= 0x0001;
    int SC_MANAGER_CREATE_SERVICE		= 0x0002;
    int SC_MANAGER_ENUMERATE_SERVICE	= 0x0004;
    int SC_MANAGER_LOCK					= 0x0008;
    int SC_MANAGER_QUERY_LOCK_STATUS	= 0x0010;
    int SC_MANAGER_MODIFY_BOOT_CONFIG	= 0x0020;

    int SC_MANAGER_ALL_ACCESS = 
        WinNT.STANDARD_RIGHTS_REQUIRED | SC_MANAGER_CONNECT 
        | SC_MANAGER_CREATE_SERVICE | SC_MANAGER_ENUMERATE_SERVICE 
        | SC_MANAGER_LOCK | SC_MANAGER_QUERY_LOCK_STATUS  
        | SC_MANAGER_MODIFY_BOOT_CONFIG;

    //
    // Service object specific access type
    //
    int SERVICE_QUERY_CONFIG			= 0x0001;
    int SERVICE_CHANGE_CONFIG			= 0x0002;
    int SERVICE_QUERY_STATUS			= 0x0004;
    int SERVICE_ENUMERATE_DEPENDENTS	= 0x0008;
    int SERVICE_START					= 0x0010;
    int SERVICE_STOP					= 0x0020;
    int SERVICE_PAUSE_CONTINUE			= 0x0040;
    int SERVICE_INTERROGATE				= 0x0080;
    int SERVICE_USER_DEFINED_CONTROL	= 0x0100;

    int SERVICE_ALL_ACCESS =
        WinNT.STANDARD_RIGHTS_REQUIRED | SERVICE_QUERY_CONFIG 
        | SERVICE_CHANGE_CONFIG | SERVICE_QUERY_STATUS 
        | SERVICE_ENUMERATE_DEPENDENTS | SERVICE_START | SERVICE_STOP 
        | SERVICE_PAUSE_CONTINUE | SERVICE_INTERROGATE 
        | SERVICE_USER_DEFINED_CONTROL;

    //
    // Controls
    //
    int  SERVICE_CONTROL_STOP			= 0x00000001;
    int  SERVICE_CONTROL_PAUSE			= 0x00000002;
    int  SERVICE_CONTROL_CONTINUE		= 0x00000003;
    int  SERVICE_CONTROL_INTERROGATE	= 0x00000004;
    //	int  SERVICE_CONTROL_SHUTDOWN		= 0x00000005;
    int  SERVICE_CONTROL_PARAMCHANGE	= 0x00000006;
    int  SERVICE_CONTROL_NETBINDADD		= 0x00000007;
    int  SERVICE_CONTROL_NETBINDREMOVE	= 0x00000008;
    int  SERVICE_CONTROL_NETBINDENABLE	= 0x00000009;
    int  SERVICE_CONTROL_NETBINDDISABLE	= 0x0000000A;
    //	int SERVICE_CONTROL_DEVICEEVENT		= 0x0000000B;
    //	int SERVICE_CONTROL_HARDWAREPROFILECHANGE = 0x0000000C;
    //	int SERVICE_CONTROL_POWEREVENT		= 0x0000000D;
    //	int SERVICE_CONTROL_SESSIONCHANGE	= 0x0000000E;
    //	int SERVICE_CONTROL_PRESHUTDOWN		= 0x0000000F;
    //	int SERVICE_CONTROL_TIMECHANGE		= 0x00000010;
    //	int SERVICE_CONTROL_TRIGGEREVENT	= 0x00000020;
	
    //
    // Service State -- for CurrentState
    //
    int SERVICE_STOPPED				= 0x00000001;
    int SERVICE_START_PENDING		= 0x00000002;
    int SERVICE_STOP_PENDING		= 0x00000003;
    int SERVICE_RUNNING				= 0x00000004;
    int SERVICE_CONTINUE_PENDING	= 0x00000005;
    int SERVICE_PAUSE_PENDING		= 0x00000006;
    int SERVICE_PAUSED				= 0x00000007;

    //
    // Controls Accepted  (Bit Mask)
    //
    int SERVICE_ACCEPT_STOP						= 0x00000001;
    int SERVICE_ACCEPT_PAUSE_CONTINUE			= 0x00000002;
    int SERVICE_ACCEPT_SHUTDOWN					= 0x00000004;
    int SERVICE_ACCEPT_PARAMCHANGE				= 0x00000008;
    int SERVICE_ACCEPT_NETBINDCHANGE			= 0x00000010;
    int SERVICE_ACCEPT_HARDWAREPROFILECHANGE	= 0x00000020;
    int SERVICE_ACCEPT_POWEREVENT				= 0x00000040;
    int SERVICE_ACCEPT_SESSIONCHANGE			= 0x00000080;
    int SERVICE_ACCEPT_PRESHUTDOWN				= 0x00000100;
    int SERVICE_ACCEPT_TIMECHANGE				= 0x00000200;
    int SERVICE_ACCEPT_TRIGGEREVENT				= 0x00000400;
	
    /**
     * The SC_STATUS_TYPE enumeration type contains values 
     */
    public abstract class SC_STATUS_TYPE { 
        public static final int SC_STATUS_PROCESS_INFO = 0;
    }

}
