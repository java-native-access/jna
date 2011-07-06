/*
 * WINSVC.java
 *
 * Created on 8. August 2007, 15:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jnacontrib.jna;


/**
 *
 * @author TB
 */
public interface WINSVC {
  public final static int SERVICE_CONTROL_STOP         = 0x00000001;
  public final static int SERVICE_CONTROL_SHUTDOWN     = 0x00000005;
  
  public final static int SERVICE_STOPPED              = 0x00000001;
  public final static int SERVICE_START_PENDING        = 0x00000002;
  public final static int SERVICE_STOP_PENDING         = 0x00000003;
  public final static int SERVICE_RUNNING              = 0x00000004;
  public final static int SERVICE_CONTINUE_PENDING     = 0x00000005;
  public final static int SERVICE_PAUSE_PENDING        = 0x00000006;
  public final static int SERVICE_PAUSED               = 0x00000007;
  
  public final static int SERVICE_ACCEPT_STOP           = 0x00000001;
  public final static int SERVICE_ACCEPT_PAUSE_CONTINUE = 0x00000002;
  public final static int SERVICE_ACCEPT_SHUTDOWN       = 0x00000004;
  public final static int SERVICE_ACCEPT_PARAMCHANGE    = 0x00000008;
  public final static int SERVICE_ACCEPT_NETBINDCHANGE  = 0x00000010;
  
  public final static int SC_MANAGER_CONNECT = 0x0001;
  public final static int SC_MANAGER_CREATE_SERVICE = 0x0002;
  public final static int SC_MANAGER_ENUMERATE_SERVICE = 0x0004;
  public final static int SC_MANAGER_LOCK = 0x0008;
  public final static int SC_MANAGER_QUERY_LOCK_STATUS = 0x0010;
  public final static int SC_MANAGER_MODIFY_BOOT_CONFIG = 0x0020;

  public final static int SC_MANAGER_ALL_ACCESS = WINNT.STANDARD_RIGHTS_REQUIRED |
                                                  SC_MANAGER_CONNECT             |
                                                  SC_MANAGER_CREATE_SERVICE      |
                                                  SC_MANAGER_ENUMERATE_SERVICE   |
                                                  SC_MANAGER_LOCK                |
                                                  SC_MANAGER_QUERY_LOCK_STATUS   |
                                                  SC_MANAGER_MODIFY_BOOT_CONFIG;
                                        
  public final static int SERVICE_QUERY_CONFIG         = 0x0001;
  public final static int SERVICE_CHANGE_CONFIG        = 0x0002;
  public final static int SERVICE_QUERY_STATUS         = 0x0004;
  public final static int SERVICE_ENUMERATE_DEPENDENTS = 0x0008;
  public final static int SERVICE_START                = 0x0010;
  public final static int SERVICE_STOP                 = 0x0020;
  public final static int SERVICE_PAUSE_CONTINUE       = 0x0040;
  public final static int SERVICE_INTERROGATE          = 0x0080;
  public final static int SERVICE_USER_DEFINED_CONTROL = 0x0100;

  public final static int SERVICE_ALL_ACCESS = WINNT.STANDARD_RIGHTS_REQUIRED |
                                               SERVICE_QUERY_CONFIG           |
                                               SERVICE_CHANGE_CONFIG          |
                                               SERVICE_QUERY_STATUS           |
                                               SERVICE_ENUMERATE_DEPENDENTS   |
                                               SERVICE_START                  |
                                               SERVICE_STOP                   |
                                               SERVICE_PAUSE_CONTINUE         |
                                               SERVICE_INTERROGATE            |
                                               SERVICE_USER_DEFINED_CONTROL;

  public final static int SERVICE_CONFIG_DESCRIPTION     = 1;
  public final static int SERVICE_CONFIG_FAILURE_ACTIONS = 2;
  
  public final static int SERVICE_KERNEL_DRIVER      = 0x00000001;
  public final static int SERVICE_FILE_SYSTEM_DRIVER = 0x00000002;
  public final static int SERVICE_ADAPTER            = 0x00000004;
  public final static int SERVICE_RECOGNIZER_DRIVER  = 0x00000008;

  public final static int SERVICE_DRIVER = SERVICE_KERNEL_DRIVER      |
                                           SERVICE_FILE_SYSTEM_DRIVER |
                                           SERVICE_RECOGNIZER_DRIVER;
                                        
  public final static int SERVICE_WIN32_OWN_PROCESS   = 0x00000010;
  public final static int SERVICE_WIN32_SHARE_PROCESS = 0x00000020;
  public final static int SERVICE_WIN32 = SERVICE_WIN32_OWN_PROCESS | SERVICE_WIN32_SHARE_PROCESS;

  public final static int SERVICE_INTERACTIVE_PROCESS = 0x00000100;

  public final static int SERVICE_TYPE_ALL = SERVICE_WIN32   |
                                             SERVICE_ADAPTER | 
                                             SERVICE_DRIVER  |
                                             SERVICE_INTERACTIVE_PROCESS;
  
  public final static int SERVICE_BOOT_START   = 0x00000000;
  public final static int SERVICE_SYSTEM_START = 0x00000001;
  public final static int SERVICE_AUTO_START   = 0x00000002;
  public final static int SERVICE_DEMAND_START = 0x00000003;
  public final static int SERVICE_DISABLED     = 0x00000004;
  
  public final static int SERVICE_ERROR_IGNORE   = 0x00000000;
  public final static int SERVICE_ERROR_NORMAL   = 0x00000001;
  public final static int SERVICE_ERROR_SEVERE   = 0x00000002;
  public final static int SERVICE_ERROR_CRITICAL = 0x00000003;
}
