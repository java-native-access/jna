/*
 * Win32Service.java
 *
 * Created on 12. September 2007, 12:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jnacontrib.win32;

import jnacontrib.jna.*;
import com.sun.jna.Pointer;

/**
 * Baseclass for a Win32 service.
 */
public abstract class Win32Service {
  protected String serviceName;
  private ServiceMain serviceMain;
  private ServiceControl serviceControl;
  private Pointer serviceStatusHandle;
  private Object waitObject = new Object();
  
  /**
   * Creates a new instance of Win32Service.
   *
   * @param serviceName internal name of the service
   */
  public Win32Service(String serviceName) {
    this.serviceName = serviceName;
  }
  
  /**
   * Install the service.
   *
   * @param displayName visible name
   * @param description description
   * @param dependencies array of other services to depend on or null
   * @param account service account or null for LocalSystem
   * @param password password for service account or null
   * @throws java.lang.Exception 
   * @return true on success
   */
  public boolean install(String displayName, String description, String[] dependencies, String account, String password) {
    return(install(displayName, description, dependencies, account, password, "java.exe -cp \"" + 
           System.getProperty("java.class.path") + "\" -Xrs " + this.getClass().getName()));
  }
  
  /**
   * Install the service.
   * 
   * @return true on success
   * @param displayName visible name
   * @param description description
   * @param dependencies array of other services to depend on or null
   * @param account service account or null for LocalSystem
   * @param password password for service account or null
   * @param command command line to start the service
   * @throws java.lang.Exception 
   */
  public boolean install(String displayName, String description, String[] dependencies, String account, String password, String command) {
    Advapi32 advapi32;
    Advapi32.SERVICE_DESCRIPTION desc;
    Pointer serviceManager, service;
    boolean success = false;
    String dep = "";
    
    if(dependencies != null) {
      for(String s : dependencies) {
        dep += s + "\0";
      }
    }
    dep += "\0";
    
    desc = new Advapi32.SERVICE_DESCRIPTION();
    desc.lpDescription = description;
    
    advapi32 = Advapi32.INSTANCE;
    serviceManager = openServiceControlManager(null, WINSVC.SC_MANAGER_ALL_ACCESS);
    
    if(serviceManager != null) {
      service = advapi32.CreateService(serviceManager, serviceName, displayName,
              WINSVC.SERVICE_ALL_ACCESS, WINSVC.SERVICE_WIN32_OWN_PROCESS, WINSVC.SERVICE_DEMAND_START, 
              WINSVC.SERVICE_ERROR_NORMAL, 
              command, 
              null, null, dep, account, password);
      
      if(service != null) {
        success = advapi32.ChangeServiceConfig2(service, WINSVC.SERVICE_CONFIG_DESCRIPTION, desc);
        advapi32.CloseServiceHandle(service);
      }
      advapi32.CloseServiceHandle(serviceManager);
    }
    return(success);
  }
  
  /**
   * Uninstall the service.
   *
   * @throws java.lang.Exception 
   * @return true on success
   */
  public boolean uninstall() {
    Advapi32 advapi32;
    Pointer serviceManager, service;
    boolean success = false;
    
    advapi32 = Advapi32.INSTANCE;
    serviceManager = openServiceControlManager(null, WINSVC.SC_MANAGER_ALL_ACCESS);
    
    if(serviceManager != null) {
      service = advapi32.OpenService(serviceManager, serviceName, WINSVC.SERVICE_ALL_ACCESS);
      
      if(service != null) {
        success = advapi32.DeleteService(service);
        advapi32.CloseServiceHandle(service);
      }
      advapi32.CloseServiceHandle(serviceManager);
    }
    return(success);
  }
  
  /**
   * Ask the ServiceControlManager to start the service.
   * @return true on success
   */
  public boolean start() {
    Advapi32 advapi32;
    Pointer serviceManager, service;
    boolean success = false;
    
    advapi32 = Advapi32.INSTANCE;
    
    serviceManager = openServiceControlManager(null, WINNT.GENERIC_EXECUTE);
    
    if(serviceManager != null) {
      service = advapi32.OpenService(serviceManager, serviceName, WINNT.GENERIC_EXECUTE);
      
      if(service != null) {
        success = advapi32.StartService(service, 0, null);
        advapi32.CloseServiceHandle(service);
      }
      advapi32.CloseServiceHandle(serviceManager);
    }
    
    return(success);
  }
  
  /**
   * Ask the ServiceControlManager to stop the service.
   * @return true on success
   */
  public boolean stop() throws Exception {
    Advapi32 advapi32;
    Pointer serviceManager, service;
    Advapi32.SERVICE_STATUS serviceStatus;
    boolean success = false;
    
    advapi32 = Advapi32.INSTANCE;
    
    serviceManager = openServiceControlManager(null, WINNT.GENERIC_EXECUTE);
    
    if(serviceManager != null) {
      service = advapi32.OpenService(serviceManager, serviceName, WINNT.GENERIC_EXECUTE);
      
      if(service != null) {
        serviceStatus = new Advapi32.SERVICE_STATUS();
        success = advapi32.ControlService(service, WINSVC.SERVICE_CONTROL_STOP, serviceStatus);
        advapi32.CloseServiceHandle(service);
      }
      advapi32.CloseServiceHandle(serviceManager);
    }
    
    return(success);
  }
  
  /**
   * Initialize the service, connect to the ServiceControlManager.
   */
  public void init() {
    Advapi32 advapi32;
    Advapi32.SERVICE_TABLE_ENTRY entry;
    
    serviceMain = new ServiceMain();
    advapi32 = Advapi32.INSTANCE;
    entry = new Advapi32.SERVICE_TABLE_ENTRY();
    entry.lpServiceName = serviceName;
    entry.lpServiceProc = serviceMain;
    
    advapi32.StartServiceCtrlDispatcher(entry.toArray(2));
  }
  
  /**
   * Get a handle to the ServiceControlManager.
   *
   * @param machine name of the machine or null for localhost
   * @param access access flags
   * @return handle to ServiceControlManager or null when failed
   */
  private Pointer openServiceControlManager(String machine, int access) {
    Pointer handle = null;
    Advapi32 advapi32;
    
    advapi32 = Advapi32.INSTANCE;
    handle = advapi32.OpenSCManager(machine, null, access);
    return(handle);
  }
  
  /**
   * Report service status to the ServiceControlManager.
   *
   * @param status status
   * @param win32ExitCode exit code 
   * @param waitHint time to wait
   */
  private void reportStatus(int status, int win32ExitCode, int waitHint) {
    Advapi32 advapi32;
    Advapi32.SERVICE_STATUS serviceStatus;
    
    advapi32 = Advapi32.INSTANCE;
    serviceStatus = new Advapi32.SERVICE_STATUS();
    serviceStatus.dwServiceType = WINNT.SERVICE_WIN32_OWN_PROCESS;
    serviceStatus.dwControlsAccepted = WINSVC.SERVICE_ACCEPT_STOP | WINSVC.SERVICE_ACCEPT_SHUTDOWN;
    serviceStatus.dwWin32ExitCode = win32ExitCode;
    serviceStatus.dwWaitHint = waitHint;
    serviceStatus.dwCurrentState = status;
    
    advapi32.SetServiceStatus(serviceStatusHandle, serviceStatus);
  }
  
  /**
   * Called when service is starting.
   */
  public abstract void onStart();
  
  /*
   * Called when service should stop.
   */
  public abstract void onStop();
  
  
  /**
   * Implementation of the service main function.
   */
  private class ServiceMain implements Advapi32.SERVICE_MAIN_FUNCTION {
    
    /**
     * Called when the service is starting.
     *
     * @param dwArgc number of arguments
     * @param lpszArgv pointer to arguments
     */
    public void callback(int dwArgc, Pointer lpszArgv) {
      Advapi32 advapi32;
      
      advapi32 = Advapi32.INSTANCE;
      
      serviceControl = new ServiceControl();
      serviceStatusHandle = advapi32.RegisterServiceCtrlHandlerEx(serviceName, serviceControl, null);
      
      reportStatus(WINSVC.SERVICE_START_PENDING, WINERROR.NO_ERROR, 3000);
      reportStatus(WINSVC.SERVICE_RUNNING, WINERROR.NO_ERROR, 0);
      
      onStart();
      
      try {
        synchronized(waitObject) {
          waitObject.wait();
        }
      } catch (InterruptedException ex) {
      }
      reportStatus(WINSVC.SERVICE_STOPPED, WINERROR.NO_ERROR, 0);

      // Avoid returning from ServiceMain, which will cause a crash
      // See http://support.microsoft.com/kb/201349, which recommends
      // having init() wait for this thread.  
      // Waiting on this thread in init() won't fix the crash, though.
      //System.exit(0);
    }
  }
  
  
  /**
   * Implementation of the service control function.
   */
  private class ServiceControl implements Advapi32.HandlerEx {

    /**
     * Called when the service get a control code.
     *
     * @param dwControl 
     * @param dwEventType 
     * @param lpEventData 
     * @param lpContext 
     */
    public int callback(int dwControl, int dwEventType, Pointer lpEventData, Pointer lpContext) {
      switch(dwControl) {
        case WINSVC.SERVICE_CONTROL_STOP:
        case WINSVC.SERVICE_CONTROL_SHUTDOWN:
          reportStatus(WINSVC.SERVICE_STOP_PENDING, WINERROR.NO_ERROR, 5000);
          onStop();
          synchronized(waitObject) {
            waitObject.notifyAll();
          }
      }
      return WINERROR.NO_ERROR;
    }
  }
}
