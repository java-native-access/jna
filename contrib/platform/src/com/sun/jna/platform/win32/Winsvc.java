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
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * This module defines the 32-Bit Windows types and constants that are defined
 * by NT, but exposed through the Win32 API.
 * Ported from Winsvc.h.
 * Microsoft Windows SDK 7.0A.
 * @author EugineLev
 */
public interface Winsvc {

    /**
     *  Contains status information for a service. The ControlService, EnumDependentServices,
     *  EnumServicesStatus, and QueryServiceStatus functions use this structure. A service
     *  uses this structure in the SetServiceStatus function to report its current status
     *  to the service control manager.
     */
    public static class SERVICE_STATUS extends Structure {
        public static final List<String> FIELDS = createFieldsOrder(
                "dwServiceType", "dwCurrentState", "dwControlsAccepted", "dwWin32ExitCode", "dwServiceSpecificExitCode", "dwCheckPoint", "dwWaitHint");

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

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Contains process status information for a service. The ControlServiceEx,
     * EnumServicesStatusEx, NotifyServiceStatusChange, and QueryServiceStatusEx
     * functions use this structure.
     */
    public class SERVICE_STATUS_PROCESS extends Structure {
        public static final List<String> FIELDS = createFieldsOrder(
                "dwServiceType", "dwCurrentState", "dwControlsAccepted",
                "dwWin32ExitCode", "dwServiceSpecificExitCode",
                "dwCheckPoint", "dwWaitHint", "dwProcessId", "dwServiceFlags");

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
            super();
        }

        public SERVICE_STATUS_PROCESS(int size) {
            super(new Memory(size));
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
	
    public abstract class ChangeServiceConfig2Info extends Structure {
        public ChangeServiceConfig2Info() {
            super(Boolean.getBoolean("w32.ascii") ? W32APITypeMapper.ASCII : W32APITypeMapper.UNICODE);
        }
        
        public ChangeServiceConfig2Info(Pointer p) {
            super(p, ALIGN_DEFAULT, Boolean.getBoolean("w32.ascii") ? W32APITypeMapper.ASCII : W32APITypeMapper.UNICODE);
        }
    }

    /**
     * Represents the action the service controller should take on each failure of a service. A 
     * service is considered failed when it terminates without reporting a status of SERVICE_STOPPED 
     * to the service controller.
     * To configure additional circumstances under which the failure actions are to be executed, see 
     * SERVICE_FAILURE_ACTIONS_FLAG.
     */
    public class SERVICE_FAILURE_ACTIONS extends ChangeServiceConfig2Info {
        public static class ByReference extends SERVICE_FAILURE_ACTIONS implements Structure.ByReference {}

        /**
         * The time after which to reset the failure count to zero if there are no failures, in 
         * seconds. Specify INFINITE to indicate that this value should never be reset.
         */
        public int dwResetPeriod;
        /**
         * The message to be broadcast to server users before rebooting in response to the 
         * SC_ACTION_REBOOT service controller action.
         * If this value is NULL, the reboot message is unchanged. If the value is an empty string 
         * (""), the reboot message is deleted and no message is broadcast.
         * This member can specify a localized string using the following format:
         * "@[path]dllname,-strID"
         * The string with identifier strID is loaded from dllname; the path is optional. For more 
         * information, see RegLoadMUIString.
         * Windows Server 2003 and Windows XP:  Localized strings are not supported until Windows 
         * Vista.
         */
        public String lpRebootMsg;
        /**
         * The command line of the process for the CreateProcess function to execute in response to 
         * the SC_ACTION_RUN_COMMAND service controller action. This process runs under the same 
         * account as the service.
         * If this value is NULL, the command is unchanged. If the value is an empty string (""), 
         * the command is deleted and no program is run when the service fails.
         */
        public String lpCommand;
        /**
         * The number of elements in the lpsaActions array.
         * If this value is 0, but lpsaActions is not NULL, the reset period and array of failure 
         * actions are deleted.
         */
        public int cActions;
        /**
         * A pointer to an array of SC_ACTION structures.
         * If this value is NULL, the cActions and dwResetPeriod members are ignored.
         */
        public SC_ACTION.ByReference lpsaActions;
        
        public SERVICE_FAILURE_ACTIONS() {
            super();
        }
        
        public SERVICE_FAILURE_ACTIONS(Pointer p) {
            super(p);
            read();
        }

        public static final List<String> FIELDS = createFieldsOrder("dwResetPeriod", "lpRebootMsg", "lpCommand", "cActions", "lpsaActions");
                                                                    
        @Override
        protected List getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Represents an action that the service control manager can perform.
     */
    public class SC_ACTION extends Structure {
        public static class ByReference extends SC_ACTION implements Structure.ByReference {}
        /**
         * The action to be performed. This member can be one of the following values from the 
         * SC_ACTION_TYPE enumeration type.
         */
        public int type;
        /**
         * The time to wait before performing the specified action, in milliseconds.
         */
        public int delay;

        public static final List<String> FIELDS = createFieldsOrder("type", "delay");

        @Override
        protected List getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Contains the failure actions flag setting of a service. This setting determines when failure 
     * actions are to be executed.
     */
    public class SERVICE_FAILURE_ACTIONS_FLAG extends ChangeServiceConfig2Info {
        /**
         * If this member is TRUE and the service has configured failure actions, the failure 
         * actions are queued if the service process terminates without reporting a status of 
         * SERVICE_STOPPED or if it enters the SERVICE_STOPPED state but the dwWin32ExitCode member 
         * of the SERVICE_STATUS structure is not ERROR_SUCCESS (0).
         * If this member is FALSE and the service has configured failure actions, the failure 
         * actions are queued only if the service terminates without reporting a status of 
         * SERVICE_STOPPED.
         * This setting is ignored unless the service has configured failure actions. For 
         * information on configuring failure actions, see ChangeServiceConfig2.
         */
        public int fFailureActionsOnNonCrashFailures;
        
        public static final List<String> FIELDS = createFieldsOrder("fFailureActionsOnNonCrashFailures");

        @Override
        protected List getFieldOrder() {
            return FIELDS;
        }
        
        public SERVICE_FAILURE_ACTIONS_FLAG() {
            super();
        }
        
        public SERVICE_FAILURE_ACTIONS_FLAG(Pointer p) {
            super(p);
            read();
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
    int SERVICE_CONTROL_STOP                  = 0x00000001;
    int SERVICE_CONTROL_PAUSE                 = 0x00000002;
    int SERVICE_CONTROL_CONTINUE              = 0x00000003;
    int SERVICE_CONTROL_INTERROGATE           = 0x00000004;
    int SERVICE_CONTROL_SHUTDOWN              = 0x00000005;
    int SERVICE_CONTROL_PARAMCHANGE           = 0x00000006;
    int SERVICE_CONTROL_NETBINDADD            = 0x00000007;
    int SERVICE_CONTROL_NETBINDREMOVE         = 0x00000008;
    int SERVICE_CONTROL_NETBINDENABLE         = 0x00000009;
    int SERVICE_CONTROL_NETBINDDISABLE        = 0x0000000A;
    int SERVICE_CONTROL_DEVICEEVENT           = 0x0000000B;
    int SERVICE_CONTROL_HARDWAREPROFILECHANGE = 0x0000000C;
    int SERVICE_CONTROL_POWEREVENT            = 0x0000000D;
    int SERVICE_CONTROL_SESSIONCHANGE         = 0x0000000E;
    int SERVICE_CONTROL_PRESHUTDOWN           = 0x0000000F;
    int SERVICE_CONTROL_TIMECHANGE            = 0x00000010;
    int SERVICE_CONTROL_TRIGGEREVENT          = 0x00000020;
    int SERVICE_CONTROL_USERMODEREBOOT        = 0x00000040;

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

    //
    // ChangeServiceConfig2 dwInfoLevel values
    //
    int SERVICE_CONFIG_DESCRIPTION			= 0x00000001;
    int SERVICE_CONFIG_FAILURE_ACTIONS			= 0x00000002;
    int SERVICE_CONFIG_DELAYED_AUTO_START_INFO		= 0x00000003;
    int SERVICE_CONFIG_FAILURE_ACTIONS_FLAG		= 0x00000004;
    int SERVICE_CONFIG_SERVICE_SID_INFO		= 0x00000005;
    int SERVICE_CONFIG_REQUIRED_PRIVILEGES_INFO	= 0x00000006;
    int SERVICE_CONFIG_PRESHUTDOWN_INFO		= 0x00000007;
    int SERVICE_CONFIG_TRIGGER_INFO			= 0x00000008;
    int SERVICE_CONFIG_PREFERRED_NODE			= 0x00000009;
    int SERVICE_CONFIG_LAUNCH_PROTECTED		= 0x0000000c;

    //
    // Service failure actions
    //
    int SC_ACTION_NONE					= 0x00000000;
    int SC_ACTION_RESTART				= 0x00000001;
    int SC_ACTION_REBOOT				= 0x00000002;
    int SC_ACTION_RUN_COMMAND				= 0x00000003;
	
    /**
     * The SC_STATUS_TYPE enumeration type contains values
     */
    public abstract class SC_STATUS_TYPE {
        public static final int SC_STATUS_PROCESS_INFO = 0;
    }
}
