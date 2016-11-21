/*
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

package jnacontrib.jna;

import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.platform.win32.Winsvc.ChangeServiceConfig2Info;
import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS;
import com.sun.jna.win32.W32APITypeMapper;

/**
 *
 * @author TB
 */
public interface Advapi32 extends com.sun.jna.platform.win32.Advapi32 {

    Advapi32 INSTANCE = Native.loadLibrary("Advapi32", Advapi32.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Connects the main thread of a service process to the service control
     * manager, which causes the thread to be the service control dispatcher
     * thread for the calling process.
     *
     * @param lpServiceTable A pointer to an array of SERVICE_TABLE_ENTRY
     *                       structures containing one entry for each service
     *                       that can execute in the calling process. The
     *                       members of the last entry in the table must have
     *                       NULL values to designate the end of the table.
     * 
     * @return true if function succeeds. To get extended error information, call
     * GetLastError. Possible error codes:
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_FAILED_SERVICE_CONTROLLER_CONNECT</td><td>This error is returned if the program is being run as a console application rather than as a service. If the program will be run as a console application for debugging purposes, structure it such that service-specific code is not called when this error is returned.</td></tr>
     * <tr><td>ERROR_INVALID_DATA</td><td>The specified dispatch table contains entries that are not in the proper format.</td></tr>
     * <tr><td>ERROR_SERVICE_ALREADY_RUNNING</td><td>The process has already called StartServiceCtrlDispatcher. Each process can call StartServiceCtrlDispatcher only one time.</td></tr>
     * </table>
     */
    public boolean StartServiceCtrlDispatcher(SERVICE_TABLE_ENTRY[] lpServiceTable);

    /**
     * Registers a function to handle service control requests.
     *
     * <p>This function has been superseded by the RegisterServiceCtrlHandlerEx
     * function. A service can use either function, but the new function
     * supports user-defined context data, and the new handler function supports
     * additional extended control codes.</p>
     *
     * @param lpServiceName The name of the service run by the calling thread.
     *                      This is the service name that the service control
     *                      program specified in the CreateService function when
     *                      creating the service.
     *
     *                      <p>If the service type is SERVICE_WIN32_OWN_PROCESS,
     *                      the function does not verify that the specified name
     *                      is valid, because there is only one registered
     *                      service in the process.</p>
     *
     * @param lpHandlerProc A pointer to the handler function to be registered.
     *                      For more information, see {@link Handler}.
     *
     * @return A service status handle, NULL on error. Call GetLastError to
     * get extended error condition. Possible error codes:
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_NOT_ENOUGH_MEMORY</td><td>Not enough memory is available to convert an ANSI string parameter to Unicode. This error does not occur for Unicode string parameters.</td></tr>
     * <tr><td>ERROR_SERVICE_NOT_IN_EXE</td><td>The service entry was specified incorrectly when the process called the {@link #StartServiceCtrlDispatcher} function.</td></tr>
     * </table>
     */
    public SERVICE_STATUS_HANDLE RegisterServiceCtrlHandler(String lpServiceName,
            Handler lpHandlerProc);

    /**
     * Registers a function to handle extended service control requests.
     *
     * @param lpServiceName The name of the service run by the calling thread.
     *                      This is the service name that the service control
     *                      program specified in the CreateService function when
     *                      creating the service.
     * @param lpHandlerProc The handler function to be registered.
     *                      For more information, see HandlerEx.
     * @param lpContext     Any user-defined data. This parameter, which is
     *                      passed to the handler function, can help identify
     *                      the service when multiple services share a process.
     *
     * @return A service status handle on success, NULL on error. Call GetLastError
     * to get extended information. Possible error codes:
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_NOT_ENOUGH_MEMORY</td><td>Not enough memory is available to convert an ANSI string parameter to Unicode. This error does not occur for Unicode string parameters.</td></tr>
     * <tr><td>ERROR_SERVICE_NOT_IN_EXE</td><td>The service entry was specified incorrectly when the process called the {@link #StartServiceCtrlDispatcher} function.</td></tr>
     * </table>
     */
    public SERVICE_STATUS_HANDLE RegisterServiceCtrlHandlerEx(String lpServiceName,
            HandlerEx lpHandlerProc, Pointer lpContext);

    /**
     * Updates the service control manager's status information for the calling
     * service.
     *
     *
     * @param hServiceStatus  A handle to the status information structure for
     *                        the current service. This handle is returned by
     *                        the RegisterServiceCtrlHandlerEx function.
     * @param lpServiceStatus A pointer to the SERVICE_STATUS structure the
     *                        contains the latest status information for the
     *                        calling service.
     *  
     * @return true if function succeeds. To get extended error information, call
     * GetLastError. Possible error codes:
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_INVALID_DATA</td><td>The specified service status structure is invalid.</td></tr>
     * <tr><td>ERROR_INVALID_HANDLE</td><td>The specified handle is invalid.</td></tr>
     * </table>
     */
    public boolean SetServiceStatus(SERVICE_STATUS_HANDLE hServiceStatus,
            SERVICE_STATUS lpServiceStatus);

    /**
     * Creates a service object and adds it to the specified service control
     * manager database.
     *
     * @param hSCManager         [in] A handle to the service control manager
     *                           database. This handle is returned by the
     *                           OpenSCManager function and must have the
     *                           SC_MANAGER_CREATE_SERVICE access right. For
     *                           more information, see Service Security and
     *                           Access Rights.
     * @param lpServiceName      [in] The name of the service to install. The
     *                           maximum string length is 256 characters. The
     *                           service control manager database preserves the
     *                           case of the characters, but service name
     *                           comparisons are always case insensitive.
     *                           Forward-slash (/) and backslash (\) are not
     *                           valid service name characters.
     * @param lpDisplayName      [in, optional] The display name to be used by
     *                           user interface programs to identify the
     *                           service. This string has a maximum length of
     *                           256 characters. The name is case-preserved in
     *                           the service control manager. Display name
     *                           comparisons are always case-insensitive.
     * @param dwDesiredAccess    [in] The access to the service. Before granting
     *                           the requested access, the system checks the
     *                           access token of the calling process. For a list
     *                           of values, see Service Security and Access
     *                           Rights.
     * @param dwServiceType      [in] The service type. This parameter can be
     *                           one of the following values.
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>SERVICE_ADAPTER<br>0x00000004</td><td>Reserved.</td></tr>
     * <tr><td>SERVICE_FILE_SYSTEM_DRIVER<br>0x00000002</td><td>File system driver service.</td></tr>
     * <tr><td>SERVICE_KERNEL_DRIVER<br>0x00000001</td><td>Driver service.</td></tr>
     * <tr><td>SERVICE_RECOGNIZER_DRIVER<br>0x00000008</td><td>Reserved.</td></tr>
     * <tr><td>SERVICE_WIN32_OWN_PROCESS<br>0x00000010</td><td>Service that runs in its own process.</td></tr>
     * <tr><td>SERVICE_WIN32_SHARE_PROCESS<br>0x00000020</td><td>Service that shares a process with one or more other services. For more information, see Service Programs.</td></tr>
     * </table>
     * 
     * <p>If you specify either SERVICE_WIN32_OWN_PROCESS or SERVICE_WIN32_SHARE_PROCESS, and the service is running in the context of the LocalSystem account, you can also specify the following value.</p>
     * 
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>SERVICE_INTERACTIVE_PROCESS<br>0x00000100</td><td>The service can interact with the desktop.</td></tr>
     * </table>
     * 
     * @param dwStartType        [in] The service start options. This parameter
     *                           can be one of the following values.
     * 
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>SERVICE_AUTO_START<br>0x00000002</td><td>A service started automatically by the service control manager during system startup.</td></tr>
     * <tr><td>SERVICE_BOOT_START<br>0x00000000</td><td>A device driver started by the system loader. This value is valid only for driver services.</td></tr>
     * <tr><td>SERVICE_DEMAND_START<br>0x00000003</td><td>A service started by the service control manager when a process calls the StartService function.</td></tr>
     * <tr><td>SERVICE_DISABLED<br>0x00000004</td><td>A service that cannot be started. Attempts to start the service result in the error code ERROR_SERVICE_DISABLED.</td></tr>
     * <tr><td>SERVICE_SYSTEM_START<br>0x00000001</td><td>A device driver started by the IoInitSystem function. This value is valid only for driver services.</td></tr>
     * </table>
     *
     * @param dwErrorControl     [in] The severity of the error, and action
     *                           taken, if this service fails to start. This
     *                           parameter can be one of the following values.
     * 
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>SERVICE_ERROR_CRITICAL<br>0x00000003</td><td>The startup program logs the error in the event log, if possible. If the last-known-good configuration is being started, the startup operation fails. Otherwise, the system is restarted with the last-known good configuration.</td></tr>
     * <tr><td>SERVICE_ERROR_IGNORE<br>0x00000000</td><td>The startup program ignores the error and continues the startup operation.</td></tr>
     * <tr><td>SERVICE_ERROR_NORMAL<br>0x00000001</td><td>The startup program logs the error in the event log but continues the startup operation.</td></tr>
     * <tr><td>SERVICE_ERROR_SEVERE<br>0x00000002</td><td>The startup program logs the error in the event log. If the last-known-good configuration is being started, the startup operation continues. Otherwise, the system is restarted with the last-known-good configuration.</td></tr>
     * </table>
     *
     * @param lpBinaryPathName   [in, optional] The fully qualified path to the
     *                           service binary file. If the path contains a
     *                           space, it must be quoted so that it is
     *                           correctly interpreted. For example, "d:\\my
     *                           share\\myservice.exe" should be specified as
     *                           "\"d:\\my share\\myservice.exe\"".
     *
     *                           <p>The path can also include arguments for an 
     *                           auto-start service. For example, 
     *                           "d:\\myshare\\myservice.exe arg1 arg2". These
     *                           passed to the service entry point (typically 
     *                           the main function).</p>
     *
     *                           <p>If you specify a path on another computer, 
     *                           the share must be accessible by the computer 
     *                           account of the local computer because this is 
     *                           the security context used in the remote call. 
     *                           However, this requirement allows any potential
     *                           vulnerabilities in the remote computer to 
     *                           affect the local computer. Therefore, it is
     *                           best to use a local file.</p>
     * 
     * @param lpLoadOrderGroup   [in, optional] The names of the load ordering
     *                           group of which this service is a member.
     *                           Specify NULL or an empty string if the service
     *                           does not belong to a group.
     *
     *                           <p>The startup program uses load ordering 
     *                           groups to load groups of services in a 
     *                           specified order with respect to the other 
     *                           groups. The list of load ordering groups is
     *                           contained in the following registry value:</p>
     *
     * <p>HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\ServiceGroupOrder</p>
     * @param lpdwTagId          [out, optional] A pointer to a variable that
     *                           receives a tag value that is unique in the
     *                           group specified in the lpLoadOrderGroup
     *                           parameter. Specify NULL if you are not changing
     *                           the existing tag.
     *
     *                           <p>You can use a tag for ordering service 
     *                           startup within a load ordering group by 
     *                           specifying a tag order vector in the following
     *                           registry value:</p>
     *
     * <p>HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\GroupOrderList</p>
     *
     *                           <p>Tags are only evaluated for driver services
     *                           that have SERVICE_BOOT_START or 
     *                           SERVICE_SYSTEM_START start types.</p>
     * @param lpDependencies     [in, optional] A pointer to a double
     *                           null-terminated array of null-separated names
     *                           of services or load ordering groups that the
     *                           system must start before this service. Specify
     *                           NULL or an empty string if the service has no
     *                           dependencies. Dependency on a group means that
     *                           this service can run if at least one member of
     *                           the group is running after an attempt to start
     *                           all members of the group.
     *
     *                           <p>You must prefix group names with 
     *                           SC_GROUP_IDENTIFIER so that they can be
     *                           distinguished from a service name, because 
     *                           services and service groups share the same name
     *                           space.</p>
     * @param lpServiceStartName [in, optional] The name of the account under
     *                           which the service should run. If the service
     *                           type is SERVICE_WIN32_OWN_PROCESS, use an
     *                           account name in the form DomainName\UserName.
     *                           The service process will be logged on as this
     *                           user. If the account belongs to the built-in
     *                           domain, you can specify .\UserName.
     *
     *                           <p>If this parameter is NULL, CreateService 
     *                           uses the LocalSystem account. If the service 
     *                           type specifies SERVICE_INTERACTIVE_PROCESS, the
     *                           service must run in the LocalSystem account.</p>
     *
     *                           <p>If this parameter is NT AUTHORITY\LocalService,
     *                           CreateService uses the LocalService account. If
     *                           the parameter is NT AUTHORITY\NetworkService,
     *                           CreateService uses the NetworkService account.</p>
     *
     *                           <p>A shared process can run as any user.</p>
     *
     *                           <p>If the service type is SERVICE_KERNEL_DRIVER
     *                           or SERVICE_FILE_SYSTEM_DRIVER, the name is the
     *                           driver object name that the system uses to load
     *                           the device driver. Specify NULL if the driver
     *                           is to use a default object name created by the
     *                           I/O system.</p>
     *
     *                           <p>A service can be configured to use a managed
     *                           account or a virtual account. If the service is
     *                           configured to use a managed service account,
     *                           the name is the managed service account name.
     *                           If the service is configured to use a virtual
     *                           account, specify the name as NT
     *                           SERVICE\ServiceName. For more information about
     *                           managed service accounts and virtual accounts, 
     *                           see the Service Accounts Step-by-Step Guide.
     *
     * <p><strong>Windows Server 2008, Windows Vista, Windows Server 2003 and Windows XP:</strong>
     * Managed service accounts and virtual accounts are not supported until
     * Windows 7 and Windows Server 2008 R2.</p>
     * @param lpPassword         [in, optional] The password to the account name
     *                           specified by the lpServiceStartName parameter.
     *                           Specify an empty string if the account has no
     *                           password or if the service runs in the
     *                           LocalService, NetworkService, or LocalSystem
     *                           account. For more information, see Service
     *                           Record List.
     *
     *                           <p>If the account name specified by the 
     *                           lpServiceStartName parameter is the name of a 
     *                           managed service account or virtual account
     *                           name, the lpPassword parameter must be NULL.</p>
     *
     *                           <p>Passwords are ignored for driver services.</p>
     *
     * @return SC_HANDLE on success, NULL on error. Call GetLastError to
     * get extended error condition. Possible error codes:
     * 
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_ACCESS_DENIED</td><td>The handle to the SCM database does not have the SC_MANAGER_CREATE_SERVICE access right.</td></tr>
     * <tr><td>ERROR_CIRCULAR_DEPENDENCY</td><td>A circular service dependency was specified.</td></tr>
     * <tr><td>ERROR_DUPLICATE_SERVICE_NAME</td><td>The display name already exists in the service control manager database either as a service name or as another display name.</td></tr>
     * <tr><td>ERROR_INVALID_HANDLE</td><td>The handle to the specified service control manager database is invalid.</td></tr>
     * <tr><td>ERROR_INVALID_NAME</td><td>The specified service name is invalid.</td></tr>
     * <tr><td>ERROR_INVALID_PARAMETER</td><td>A parameter that was specified is invalid.</td></tr>
     * <tr><td>ERROR_INVALID_SERVICE_ACCOUNT</td><td>The user account name specified in the lpServiceStartName parameter does not exist.</td></tr>
     * <tr><td>ERROR_SERVICE_EXISTS</td><td>The specified service already exists in this database.</td></tr>
     * <tr><td>ERROR_SERVICE_MARKED_FOR_DELETE</td><td>The specified service already exists in this database and has been marked for deletion.</td></tr>
     * </table>
     */
    public SC_HANDLE CreateService(SC_HANDLE hSCManager, String lpServiceName,
            String lpDisplayName, int dwDesiredAccess, int dwServiceType,
            int dwStartType, int dwErrorControl, String lpBinaryPathName,
            String lpLoadOrderGroup, IntByReference lpdwTagId,
            String lpDependencies, String lpServiceStartName, String lpPassword);

    /**
     * Marks the specified service for deletion from the service control manager database.
     * 
     * @param hService [in] A handle to the service. This handle is returned by
     *                 the OpenService or CreateService function, and it must
     *                 have the DELETE access right.
     * 
     * @return true if function succeeds. To get extended error information, call
     * GetLastError. Possible error codes:
     * 
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>ERROR_ACCESS_DENIED</td><td>The handle does not have the DELETE access right.</td></tr>
     * <tr><td>ERROR_INVALID_HANDLE</td><td>The specified handle is invalid.</td></tr>
     * <tr><td>ERROR_SERVICE_MARKED_FOR_DELETE</td><td>The specified service has already been marked for deletion.</td></tr>
     * </table>
     */
    public boolean DeleteService(SC_HANDLE hService);

    /**
     * The entry point for a service.
     */
    interface SERVICE_MAIN_FUNCTION extends StdCallCallback {

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
     * This function has been superseded by the {@link HandlerEx} control handler
     * function used with the {@link #RegisterServiceCtrlHandlerEx} function. A service
     * can use either control handler, but the new control handler supports
     * user-defined context data and additional extended control codes.</p>
     */
    interface Handler extends StdCallCallback {

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
     * <p>This function supersedes the Handler control handler function used
     * with the {@link RegisterServiceCtrlHandler} function. A service can use either
     * control handler, but the new control handler supports user-defined
     * context data and additional extended control codes.</p>
     */
    interface HandlerEx extends StdCallCallback {

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
         *                    {@link RegisterServiceCtrlHandlerEx}. When multiple
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
         * request.< </li> <li>If your service handles
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
     * @[path\]dllname,-strID</p>
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
