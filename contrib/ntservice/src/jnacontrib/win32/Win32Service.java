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

package jnacontrib.win32;

import jnacontrib.jna.*;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Winsvc;
import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import jnacontrib.jna.Advapi32.SERVICE_STATUS_HANDLE;
import jnacontrib.jna.Advapi32.SERVICE_TABLE_ENTRY;

/**
 * Baseclass for a Win32 service.
 */
public abstract class Win32Service {
  protected String serviceName;
  private ServiceMain serviceMain;
  private ServiceControl serviceControl;
  private SERVICE_STATUS_HANDLE serviceStatusHandle;
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
   * @return true on success
   */
  public boolean install(String displayName, String description, String[] dependencies, String account, String password) {
    // This needs to be adjusted for a production implementation!
    //
    // Determine the JVM used to invoke this installer and use it as
    // runtime for the service
    String javaHome = System.getProperty("java.home");
    String javaBinary = javaHome + "\\bin\\java.exe";
    // Assumption: This is started as:
    //
    // java -jar <pathToJar>
    //
    // This is not portable, as it assumes, that this establishes a CL hierachy,
    // that starts with one ClassLoader that loads the main jar (this service
    // implementation) and its childs are resposible for loading referenced
    // jars.
    URLClassLoader cl = (URLClassLoader) Win32Service.class.getClassLoader();
    URL jarPath = cl.getURLs()[0];
    
    try {
        File jar = new File(jarPath.toURI());
        return(install(displayName, description, dependencies, account, password, 
                javaBinary + " -jar \"" + jar.getAbsolutePath() + "\""
        ));
    } catch (URISyntaxException ex) {
        return false;
    }
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
    SC_HANDLE service, serviceManager;
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
    serviceManager = openServiceControlManager(null, Winsvc.SC_MANAGER_ALL_ACCESS);
    
    if(serviceManager != null) {
      service = advapi32.CreateService(serviceManager, serviceName, displayName,
              Winsvc.SERVICE_ALL_ACCESS, WinNT.SERVICE_WIN32_OWN_PROCESS, WinNT.SERVICE_DEMAND_START, 
              WinNT.SERVICE_ERROR_NORMAL, 
              command, 
              null, null, dep, account, password);
      
      if(service != null) {
        success = advapi32.ChangeServiceConfig2(service, Winsvc.SERVICE_CONFIG_DESCRIPTION, desc);
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
    SC_HANDLE serviceManager, service;
    boolean success = false;
    
    advapi32 = Advapi32.INSTANCE;
    serviceManager = openServiceControlManager(null, Winsvc.SC_MANAGER_ALL_ACCESS);
    
    if(serviceManager != null) {
      service = advapi32.OpenService(serviceManager, serviceName, Winsvc.SERVICE_ALL_ACCESS);
      
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
    SC_HANDLE serviceManager, service;
    boolean success = false;
    
    advapi32 = Advapi32.INSTANCE;
    
    serviceManager = openServiceControlManager(null, WinNT.GENERIC_EXECUTE);
    
    if(serviceManager != null) {
      service = advapi32.OpenService(serviceManager, serviceName, WinNT.GENERIC_EXECUTE);
      
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
    SC_HANDLE serviceManager, service;
    SERVICE_STATUS serviceStatus;
    boolean success = false;
    
    advapi32 = Advapi32.INSTANCE;
    
    serviceManager = openServiceControlManager(null, WinNT.GENERIC_EXECUTE);
    
    if(serviceManager != null) {
      service = advapi32.OpenService(serviceManager, serviceName, WinNT.GENERIC_EXECUTE);
      
      if(service != null) {
        serviceStatus = new SERVICE_STATUS();
        success = advapi32.ControlService(service, Winsvc.SERVICE_CONTROL_STOP, serviceStatus);
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
    SERVICE_TABLE_ENTRY entry;
    
    serviceMain = new ServiceMain();
    advapi32 = Advapi32.INSTANCE;
    entry = new Advapi32.SERVICE_TABLE_ENTRY();
    entry.lpServiceName = serviceName;
    entry.lpServiceProc = serviceMain;
    
    advapi32.StartServiceCtrlDispatcher((SERVICE_TABLE_ENTRY[]) entry.toArray(2));
  }
  
  /**
   * Get a handle to the ServiceControlManager.
   *
   * @param machine name of the machine or null for localhost
   * @param access access flags
   * @return handle to ServiceControlManager or null when failed
   */
  private SC_HANDLE openServiceControlManager(String machine, int access) {
    SC_HANDLE handle = null;
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
    SERVICE_STATUS serviceStatus;
    
    advapi32 = Advapi32.INSTANCE;
    serviceStatus = new SERVICE_STATUS();
    serviceStatus.dwServiceType = WinNT.SERVICE_WIN32_OWN_PROCESS;
    serviceStatus.dwControlsAccepted = Winsvc.SERVICE_ACCEPT_STOP | Winsvc.SERVICE_ACCEPT_SHUTDOWN;
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
      
      reportStatus(Winsvc.SERVICE_START_PENDING, WinError.NO_ERROR, 3000);
      reportStatus(Winsvc.SERVICE_RUNNING, WinError.NO_ERROR, 0);
      
      onStart();
      
      try {
        synchronized(waitObject) {
          waitObject.wait();
        }
      } catch (InterruptedException ex) {
      }
      reportStatus(Winsvc.SERVICE_STOPPED, WinError.NO_ERROR, 0);

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
        case Winsvc.SERVICE_CONTROL_STOP:
        case Winsvc.SERVICE_CONTROL_SHUTDOWN:
          reportStatus(Winsvc.SERVICE_STOP_PENDING, WinError.NO_ERROR, 5000);
          onStop();
          synchronized(waitObject) {
            waitObject.notifyAll();
          }
      }
      return WinError.NO_ERROR;
    }
  }
}
