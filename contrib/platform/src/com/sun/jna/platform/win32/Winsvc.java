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
import com.sun.jna.win32.StdCallLibrary;
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
    /**
     * Required to call the QueryServiceConfig and
     * {@link com.sun.jna.platform.win32.Advapi32#QueryServiceConfig2}
     * functions to query the service configuration.
     */
    int SERVICE_QUERY_CONFIG			= 0x0001;
    /**
     * Required to call the ChangeServiceConfig or
     * {@link com.sun.jna.platform.win32.Advapi32#ChangeServiceConfig2} function
     * to change the service configuration. Because this grants the caller the
     * right to change the executable file that the system runs, it should be
     * granted only to administrators.
     */
    int SERVICE_CHANGE_CONFIG			= 0x0002;
    /**
     * Required to call the QueryServiceStatus or
     * {@link com.sun.jna.platform.win32.Advapi32#QueryServiceStatusEx} function
     * to ask the service control manager about the status of the service.
     *
     * <p>
     * Required to call the NotifyServiceStatusChange function to receive
     * notification when a service changes status.</p>
     */
    int SERVICE_QUERY_STATUS			= 0x0004;
    int SERVICE_ENUMERATE_DEPENDENTS	= 0x0008;
    /**
     * Required to call the
     * {@link com.sun.jna.platform.win32.Advapi32#StartService} function to
     * start the service.
     */
    int SERVICE_START					= 0x0010;
    /**
     * Required to call the
     * {@link com.sun.jna.platform.win32.Advapi32#ControlService} function to
     * stop the service.
     */
    int SERVICE_STOP = 0x0020;
    /**
     * Required to call the
     * {@link com.sun.jna.platform.win32.Advapi32#ControlService} function to
     * pause or continue the service.
     */
    int SERVICE_PAUSE_CONTINUE = 0x0040;
    /**
     * Required to call the
     * {@link com.sun.jna.platform.win32.Advapi32#ControlService} function to
     * pause or continue the service.
     */
    int SERVICE_INTERROGATE = 0x0080;
    /**
     * Required to call the
     * {@link com.sun.jna.platform.win32.Advapi32#ControlService} function to
     * ask the service to report its status immediately.
     */
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
    /**
     * Notifies a service that it should stop. The hService handle must have the
     * {@link #SERVICE_STOP} access right.
     *
     * <p>After sending the stop request to a service, you should not send other
     * controls to the service.</p>
     */
    int SERVICE_CONTROL_STOP                  = 0x00000001;
    /**
     * Notifies a service that it should pause. The hService handle must have
     * the {@link #SERVICE_PAUSE_CONTINUE} access right.
     */
    int SERVICE_CONTROL_PAUSE                 = 0x00000002;
    /**
     * Notifies a service that its startup parameters have changed. The hService
     * handle must have the {@link #SERVICE_PAUSE_CONTINUE} access right.
     */
    int SERVICE_CONTROL_CONTINUE              = 0x00000003;
    /**
     * Notifies a service that it should report its current status information
     * to the service control manager. The hService handle must have the
     * {@link #SERVICE_INTERROGATE} access right.
     *
     * <p>
     * Note that this control is not generally useful as the SCM is aware of the
     * current state of the service.</p>
     */
    int SERVICE_CONTROL_INTERROGATE           = 0x00000004;
    int SERVICE_CONTROL_SHUTDOWN              = 0x00000005;
    int SERVICE_CONTROL_PARAMCHANGE           = 0x00000006;
    /**
     * Notifies a network service that there is a new component for binding. The
     * hService handle must have the {@link #SERVICE_PAUSE_CONTINUE} access
     * right. However, this control code has been deprecated; use Plug and Play
     * functionality instead.
     */
    int SERVICE_CONTROL_NETBINDADD            = 0x00000007;
    /**
     * Notifies a network service that a component for binding has been removed.
     * The hService handle must have the {@link #SERVICE_PAUSE_CONTINUE} access
     * right. However, this control code has been deprecated; use Plug and Play
     * functionality instead.
     */
    int SERVICE_CONTROL_NETBINDREMOVE         = 0x00000008;
    /**
     * Notifies a network service that a disabled binding has been enabled. The
     * hService handle must have the {@link #SERVICE_PAUSE_CONTINUE} access
     * right. However, this control code has been deprecated; use Plug and Play
     * functionality instead.
     */
    int SERVICE_CONTROL_NETBINDENABLE         = 0x00000009;
    /**
     * Notifies a network service that one of its bindings has been disabled.
     * The hService handle must have the {@link #SERVICE_PAUSE_CONTINUE} access
     * right. However, this control code has been deprecated; use Plug and Play
     * functionality instead.
     */
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
    
    /**
     * The entry point for a service.
     */
    interface SERVICE_MAIN_FUNCTION extends StdCallLibrary.StdCallCallback {

        /**
         *
         * @param dwArgc   [in] The number of arguments in the lpszArgv array.
         * @param lpszArgv [in] The null-terminated argument strings passed to
         *                 the service by the call to the StartService function
         *                 that started the service. If there are no arguments,
         *                 this parameter can be NULL. Otherwise, the first
         *                 argument (lpszArgv[0]) is the name of the service,
         *                 followed by any additional arguments (lpszArgv[1]
         *                 through lpszArgv[dwArgc-1]).
         *
         *                 <p>If the user starts a manual service using the 
         *                 Services snap-in from the Control Panel, the strings
         *                 for the lpszArgv parameter come from the properties
         *                 dialog box for the service (from the Services snap-in,
         *                 right-click the service entry, click Properties, and
         *                 enter the parameters in Start parameters.)
         */
        public void callback(int dwArgc, Pointer lpszArgv);
    }

    /**
     * An application-defined callback function used with the
     * RegisterServiceCtrlHandler function. A service program can use it as the
     * control handler function of a particular service.
     *
     * <p>
     * This function has been superseded by the {@link HandlerEx} control
     * handler function used with the
     * {@link com.sun.jna.platform.win32.Advapi32#RegisterServiceCtrlHandlerEx}
     * function. A service can use either control handler, but the new control
     * handler supports user-defined context data and additional extended
     * control codes.</p>
     */
    interface Handler extends StdCallLibrary.StdCallCallback {

        /**
         * @param fdwControl [in] The control code. This parameter can be one of
         *                   the following values.
         * <table>
         * <tr><th>Control code</th><th>Meaning</th></tr>
         * <tr><td>SERVICE_CONTROL_CONTINUE<br>0x00000003</td><td>Notifies a paused service that it should resume.</td></tr>
         * <tr><td>SERVICE_CONTROL_INTERROGATE<br>0x00000004</td><td>Notifies a service that it should report its current status information to the service control manager.<br>The handler should simply return NO_ERROR; the SCM is aware of the current state of the service.</td></tr>
         * <tr><td>SERVICE_CONTROL_NETBINDADD<br>0x00000007</td><td>Notifies a network service that there is a new component for binding. The service should bind to the new component.<br>Applications should use Plug and Play functionality instead.</td></tr>
         * <tr><td>SERVICE_CONTROL_NETBINDDISABLE<br>0x0000000A</td><td>Notifies a network service that one of its bindings has been disabled. The service should reread its binding information and remove the binding.<br>Applications should use Plug and Play functionality instead.</td></tr>
         * <tr><td>SERVICE_CONTROL_NETBINDENABLE<br>0x00000009</td><td>Notifies a network service that a disabled binding has been enabled. The service should reread its binding information and add the new binding.<br>Applications should use Plug and Play functionality instead.</td></tr>
         * <tr><td>SERVICE_CONTROL_NETBINDREMOVE<br>0x00000008</td><td>Notifies a network service that a component for binding has been removed. The service should reread its binding information and unbind from the removed component.<br>Applications should use Plug and Play functionality instead.</td></tr>
         * <tr><td>SERVICE_CONTROL_PARAMCHANGE<br>0x00000006</td><td>Notifies a service that its startup parameters have changed. The service should reread its startup parameters.</td></tr>
         * <tr><td>SERVICE_CONTROL_PAUSE<br>0x00000002</td><td>Notifies a service that it should pause.</td></tr>
         * <tr><td>SERVICE_CONTROL_SHUTDOWN<br>0x00000005</td><td>Notifies a service that the system is shutting down so the service can perform cleanup tasks.<br>If a service accepts this control code, it must stop after it performs its cleanup tasks and return NO_ERROR. After the SCM sends this control code, it will not send other control codes to the service.</td></tr>
         * <tr><td>SERVICE_CONTROL_STOP<br>0x00000001</td><td>Notifies a service that it should stop.<br>If a service accepts this control code, it must stop upon receipt and return NO_ERROR. After the SCM sends this control code, it does not send other control codes.<br>Windows XP:  If the service returns NO_ERROR and continues to run, it continues to receive control codes. This behavior changed starting with Windows Server 2003 and Windows XP with SP2.</td></tr>
         * </table>
         * 
         * <p>This parameter can also be a user-defined control code, as described in the following table.</p>
         * 
         * <table>
         * <tr><th>Control code</th><th>Meaning</th></tr>
         * <tr><td>Range 128 to 255.</td><td>The service defines the action associated with the control code.</td></tr>
         * </table>
         */
        public void callback(int fdwControl);
    }

    /**
     * An application-defined callback function used with the
     * RegisterServiceCtrlHandlerEx function. A service program can use it as
     * the control handler function of a particular service.
     *
     * <p>
     * This function supersedes the Handler control handler function used with
     * the
     * {@link com.sun.jna.platform.win32.Advapi32#RegisterServiceCtrlHandlerEx}
     * function. A service can use either control handler, but the new control
     * handler supports user-defined context data and additional extended
     * control codes.</p>
     */
    interface HandlerEx extends StdCallLibrary.StdCallCallback {

        /**
         * @param dwControl [in] The control code. This parameter can be one of
         *                   the following values.
         * <table>
         * <tr><th>Control code</th><th>Meaning</th></tr>
         * <tr><td>SERVICE_CONTROL_CONTINUE<br>0x00000003</td><td>Notifies a paused service that it should resume.</td></tr>
         * <tr><td>SERVICE_CONTROL_INTERROGATE<br>0x00000004</td><td>Notifies a service that it should report its current status information to the service control manager.<br>The handler should simply return NO_ERROR; the SCM is aware of the current state of the service.</td></tr>
         * <tr><td>SERVICE_CONTROL_NETBINDADD<br>0x00000007</td><td>Notifies a network service that there is a new component for binding. The service should bind to the new component.<br>Applications should use Plug and Play functionality instead.</td></tr>
         * <tr><td>SERVICE_CONTROL_NETBINDDISABLE<br>0x0000000A</td><td>Notifies a network service that one of its bindings has been disabled. The service should reread its binding information and remove the binding.<br>Applications should use Plug and Play functionality instead.</td></tr>
         * <tr><td>SERVICE_CONTROL_NETBINDENABLE<br>0x00000009</td><td>Notifies a network service that a disabled binding has been enabled. The service should reread its binding information and add the new binding.<br>Applications should use Plug and Play functionality instead.</td></tr>
         * <tr><td>SERVICE_CONTROL_NETBINDREMOVE<br>0x00000008</td><td>Notifies a network service that a component for binding has been removed. The service should reread its binding information and unbind from the removed component.<br>Applications should use Plug and Play functionality instead.</td></tr>
         * <tr><td>SERVICE_CONTROL_PARAMCHANGE<br>0x00000006</td><td>Notifies a service that its startup parameters have changed. The service should reread its startup parameters.</td></tr>
         * <tr><td>SERVICE_CONTROL_PAUSE<br>0x00000002</td><td>Notifies a service that it should pause.</td></tr>
         * <tr><td>SERVICE_CONTROL_PRESHUTDOWN<br>0x0000000F</td><td>Notifies a service that the system will be shutting down. Services that need additional time to perform cleanup tasks beyond the tight time restriction at system shutdown can use this notification. The service control manager sends this notification to applications that have registered for it before sending a SERVICE_CONTROL_SHUTDOWN notification to applications that have registered for that notification.<br>A service that handles this notification blocks system shutdown until the service stops or the preshutdown time-out interval specified through SERVICE_PRESHUTDOWN_INFO expires. Because this affects the user experience, services should use this feature only if it is absolutely necessary to avoid data loss or significant recovery time at the next system start.<br>Windows Server 2003 and Windows XP:  This value is not supported.</td></tr>
         * <tr><td>SERVICE_CONTROL_SHUTDOWN<br>0x00000005</td><td>Notifies a service that the system is shutting down so the service can perform cleanup tasks.<br>If a service accepts this control code, it must stop after it performs its cleanup tasks and return NO_ERROR. After the SCM sends this control code, it will not send other control codes to the service.</td></tr>
         * <tr><td>SERVICE_CONTROL_STOP<br>0x00000001</td><td>Notifies a service that it should stop.<br>If a service accepts this control code, it must stop upon receipt and return NO_ERROR. After the SCM sends this control code, it does not send other control codes.<br>Windows XP:  If the service returns NO_ERROR and continues to run, it continues to receive control codes. This behavior changed starting with Windows Server 2003 and Windows XP with SP2.</td></tr>
         * </table>
         * 
         * <p>This parameter can also be one of the following extended control codes. Note that these control codes are not supported by the {@link Handler} function.</p>
         * 
         * <table>
         * <tr><th>Control code</th><th>Meaning</th></tr>
         * <tr><td>SERVICE_CONTROL_DEVICEEVENT<br>0x0000000B</td><td>Notifies a service of device events. (The service must have registered to receive these notifications using the RegisterDeviceNotification function.) The dwEventType and lpEventData parameters contain additional information.</td></tr>
         * <tr><td>SERVICE_CONTROL_HARDWAREPROFILECHANGE<br>0x0000000C</td><td>Notifies a service that the computer's hardware profile has changed. The dwEventType parameter contains additional information.</td></tr>
         * <tr><td>SERVICE_CONTROL_POWEREVENT<br>0x0000000D</td><td>Notifies a service of system power events. The dwEventType parameter contains additional information. If dwEventType is PBT_POWERSETTINGCHANGE, the lpEventData parameter also contains additional information.</td></tr>
         * <tr><td>SERVICE_CONTROL_SESSIONCHANGE<br>0x0000000E</td><td>Notifies a service of session change events. Note that a service will only be notified of a user logon if it is fully loaded before the logon attempt is made. The dwEventType and lpEventData parameters contain additional information. </td></tr>
         * <tr><td>SERVICE_CONTROL_TIMECHANGE<br>0x00000010</td><td>Notifies a service that the system time has changed. The lpEventData parameter contains additional information. The dwEventType parameter is not used.<br>Windows Server 2008, Windows Vista, Windows Server 2003 and Windows XP:  This control code is not supported.</td></tr>
         * <tr><td>SERVICE_CONTROL_TRIGGEREVENT<br>0x00000020</td><td>Notifies a service registered for a service trigger event that the event has occurred.<br>Windows Server 2008, Windows Vista, Windows Server 2003 and Windows XP:  This control code is not supported.</td></tr>
         * <tr><td>SERVICE_CONTROL_USERMODEREBOOT<br>0x00000040</td><td>Notifies a service that the user has initiated a reboot.<br>Windows Server 2008 R2, Windows 7, Windows Server 2008, Windows Vista, Windows Server 2003 and Windows XP:  This control code is not supported.</td></tr>
         * </table>
         * 
         * <p>This parameter can also be a user-defined control code, as described in the following table.</p>
         * 
         * <table>
         * <tr><th>Control code</th><th>Meaning</th></tr>
         * <tr><td>Range 128 to 255.</td><td>The service defines the action associated with the control code.</td></tr>
         * </table>
         * 
         * @param dwEventType The type of event that has occurred. This
         *                    parameter is used if dwControl is
         *                    SERVICE_CONTROL_DEVICEEVENT,
         *                    SERVICE_CONTROL_HARDWAREPROFILECHANGE,
         *                    SERVICE_CONTROL_POWEREVENT, or
         *                    SERVICE_CONTROL_SESSIONCHANGE. Otherwise, it is
         *                    zero.
         *
         * <p>If dwControl is SERVICE_CONTROL_DEVICEEVENT, this parameter can be
         * one of the following values:</p>
         *
         * <ul>
         * <li>DBT_DEVICEARRIVAL</li>
         * <li>DBT_DEVICEREMOVECOMPLETE</li>
         * <li>DBT_DEVICEQUERYREMOVE</li>
         * <li>DBT_DEVICEQUERYREMOVEFAILED</li>
         * <li>DBT_DEVICEREMOVEPENDING DBT_CUSTOMEVENT</li>
         * </ul>
         *
         * <p>If dwControl is SERVICE_CONTROL_HARDWAREPROFILECHANGE, this parameter
         * can be one of the following values:</p>
         *
         * <ul>
         * <li>DBT_CONFIGCHANGED</li>
         * <li>DBT_QUERYCHANGECONFIG</li>
         * <li>DBT_CONFIGCHANGECANCELED</li>
         * </ul>
         *
         * <p>If dwControl is SERVICE_CONTROL_POWEREVENT, this parameter can be one
         * of the values specified in the wParam parameter of the
         * WM_POWERBROADCAST message.</p>
         *
         * <p>If dwControl is SERVICE_CONTROL_SESSIONCHANGE, this parameter can be
         * one of the values specified in the wParam parameter of the
         * WM_WTSSESSION_CHANGE message.</p>
         *
         * @param lpEventData [in] Additional device information, if required.
         *                    The format of this data depends on the value of
         *                    the dwControl and dwEventType parameters.
         *
         * <p>If dwControl is SERVICE_CONTROL_DEVICEEVENT, this data corresponds to
         * the lParam parameter that applications receive as part of a
         * WM_DEVICECHANGE message.</p>
         *
         * <p>If dwControl is SERVICE_CONTROL_POWEREVENT and dwEventType is
         * PBT_POWERSETTINGCHANGE, this data is a pointer to a
         * POWERBROADCAST_SETTING structure.</p>
         *
         * <p>If dwControl is SERVICE_CONTROL_SESSIONCHANGE, this parameter is a
         * pointer to a WTSSESSION_NOTIFICATION structure.</p>
         *
         * <p>If dwControl is SERVICE_CONTROL_TIMECHANGE, this data is a pointer to
         * a SERVICE_TIMECHANGE_INFO structure.</p>
         * 
         * @param lpContext   [in] User-defined data passed from
         *                    {@link com.sun.jna.platform.win32.Advapi32#RegisterServiceCtrlHandlerEx}. When multiple
         *                    services share a process, the lpContext parameter
         *                    can help identify the service.
         * 
         * @return The return value for this function depends on the control
         *         code received.
         *
         * <p>The following list identifies the rules for this return value:</p>
         *
         * <ul>
         * <li>In general, if your service does not handle the control, return
         * ERROR_CALL_NOT_IMPLEMENTED. However, your service should return
         * NO_ERROR for SERVICE_CONTROL_INTERROGATE even if your service does
         * not handle it.</li>
         * <li>If your service handles SERVICE_CONTROL_STOP or
         * SERVICE_CONTROL_SHUTDOWN, return NO_ERROR.</li>
         * <li>If your service handles SERVICE_CONTROL_DEVICEEVENT, return
         * NO_ERROR to grant the request and an error code to deny the
         * request.</li>
         * <li>If your service handles SERVICE_CONTROL_HARDWAREPROFILECHANGE,
         * return NO_ERROR to grant the request and an error code to deny the
         * request.</li> <li>If your service handles
         * SERVICE_CONTROL_POWEREVENT, return NO_ERROR to grant the request and
         * an error code to deny the request.</li>
         * <li>For all other control codes your service handles, return
         * NO_ERROR.</li>
         * </ul>
         */
        public int callback(int dwControl, int dwEventType,
                Pointer lpEventData, Pointer lpContext);
    }
    
    /**
     * Specifies the ServiceMain function for a service that can run in the
     * calling process. It is used by the StartServiceCtrlDispatcher function.
     */
    public static class SERVICE_TABLE_ENTRY extends Structure {

        public static final List<String> FIELDS = createFieldsOrder("lpServiceName", "lpServiceProc");
        /**
         * The name of a service to be run in this service process.
         *
         * <p>
         * If the service is installed with the SERVICE_WIN32_OWN_PROCESS
         * service type, this member is ignored, but cannot be NULL. This member
         * can be an empty string ("").</p>
         * <p>
         * If the service is installed with the SERVICE_WIN32_SHARE_PROCESS
         * service type, this member specifies the name of the service that uses
         * the ServiceMain function pointed to by the lpServiceProc member.</p>
         */
        public String lpServiceName;
        public SERVICE_MAIN_FUNCTION lpServiceProc;

        public SERVICE_TABLE_ENTRY() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     *
     * Contains a service description.
     *
     * <p>
     * The description of the service. If this member is NULL, the description
     * remains unchanged. If this value is an empty string (""), the current
     * description is deleted.</p>
     * <p>
     * The service description must not exceed the size of a registry value of
     * type REG_SZ.</p>
     * <p>
     * This member can specify a localized string using the following
     * format:</p>
     * <p>
     * {@literal @}[path\]dllname,-strID</p>
     * <p>
     * The string with identifier strID is loaded from dllname; the path is
     * optional. For more information, see RegLoadMUIString.</p>
     * <p>
     * <strong>Windows Server 2003 and Windows XP:</strong> Localized strings
     * are not supported until Windows Vista.</p>
     *
     */
    public static class SERVICE_DESCRIPTION extends ChangeServiceConfig2Info {

        public static final List<String> FIELDS = createFieldsOrder("lpDescription");
        public String lpDescription;
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    public static class SERVICE_STATUS_HANDLE extends WinNT.HANDLE {

        public SERVICE_STATUS_HANDLE() {
        }

        public SERVICE_STATUS_HANDLE(Pointer p) {
            super(p);
        }
    }
}
