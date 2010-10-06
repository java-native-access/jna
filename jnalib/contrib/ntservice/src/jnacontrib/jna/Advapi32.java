/*
 * Advapi32.java
 *
 * Created on 6. August 2007, 11:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jnacontrib.jna;

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;

/**
 *
 * @author TB
 */
public interface Advapi32  extends StdCallLibrary {
  Advapi32 INSTANCE = (Advapi32) Native.loadLibrary("Advapi32", Advapi32.class, 
		  W32APIOptions.UNICODE_OPTIONS);  
  
/*
SC_HANDLE WINAPI OpenSCManager(
  LPCTSTR lpMachineName,
  LPCTSTR lpDatabaseName,
  DWORD dwDesiredAccess
);*/
  public Pointer OpenSCManager(String lpMachineName, WString lpDatabaseName, int dwDesiredAccess);
  
/*
BOOL WINAPI CloseServiceHandle(
  SC_HANDLE hSCObject
);*/
  public boolean CloseServiceHandle(Pointer hSCObject);
  
/*
SC_HANDLE WINAPI OpenService(
  SC_HANDLE hSCManager,
  LPCTSTR lpServiceName,
  DWORD dwDesiredAccess
);*/
  public Pointer OpenService(Pointer hSCManager, String lpServiceName, int dwDesiredAccess);
  
/*
BOOL WINAPI StartService(
  SC_HANDLE hService,
  DWORD dwNumServiceArgs,
  LPCTSTR* lpServiceArgVectors
);*/
  public boolean StartService(Pointer hService, int dwNumServiceArgs, char[] lpServiceArgVectors);
  
/*
BOOL WINAPI ControlService(
  SC_HANDLE hService,
  DWORD dwControl,
  LPSERVICE_STATUS lpServiceStatus
);*/
  public boolean ControlService(Pointer hService, int dwControl, SERVICE_STATUS lpServiceStatus);
 
/*  
BOOL WINAPI StartServiceCtrlDispatcher(
  const SERVICE_TABLE_ENTRY* lpServiceTable
);*/
  public boolean StartServiceCtrlDispatcher(Structure[] lpServiceTable);
  
/*
SERVICE_STATUS_HANDLE WINAPI RegisterServiceCtrlHandler(
  LPCTSTR lpServiceName,
  LPHANDLER_FUNCTION lpHandlerProc
);*/
  public Pointer RegisterServiceCtrlHandler(String lpServiceName, Handler lpHandlerProc);
  
/*
SERVICE_STATUS_HANDLE WINAPI RegisterServiceCtrlHandlerEx(
  LPCTSTR lpServiceName,
  LPHANDLER_FUNCTION_EX lpHandlerProc,
  LPVOID lpContext
);*/
  public Pointer RegisterServiceCtrlHandlerEx(String lpServiceName, HandlerEx lpHandlerProc, Pointer lpContext);
  
/*
BOOL WINAPI SetServiceStatus(
  SERVICE_STATUS_HANDLE hServiceStatus,
  LPSERVICE_STATUS lpServiceStatus
);*/
  public boolean SetServiceStatus(Pointer hServiceStatus, SERVICE_STATUS lpServiceStatus);
  
/*
SC_HANDLE WINAPI CreateService(
  SC_HANDLE hSCManager,
  LPCTSTR lpServiceName,
  LPCTSTR lpDisplayName,
  DWORD dwDesiredAccess,
  DWORD dwServiceType,
  DWORD dwStartType,
  DWORD dwErrorControl,
  LPCTSTR lpBinaryPathName,
  LPCTSTR lpLoadOrderGroup,
  LPDWORD lpdwTagId,
  LPCTSTR lpDependencies,
  LPCTSTR lpServiceStartName,
  LPCTSTR lpPassword
);*/
  public Pointer CreateService(Pointer hSCManager, String lpServiceName, String lpDisplayName,
          int dwDesiredAccess, int dwServiceType, int dwStartType, int dwErrorControl,
          String lpBinaryPathName, String lpLoadOrderGroup, IntByReference lpdwTagId,
          String lpDependencies, String lpServiceStartName, String lpPassword);
 
/*
BOOL WINAPI DeleteService(
  SC_HANDLE hService
);*/
  public boolean DeleteService(Pointer hService);
  
/*
BOOL WINAPI ChangeServiceConfig2(
  SC_HANDLE hService,
  DWORD dwInfoLevel,
  LPVOID lpInfo
);*/
  public boolean ChangeServiceConfig2(Pointer hService, int dwInfoLevel, ChangeServiceConfig2Info lpInfo);
  
/*
LONG WINAPI RegEnumValue(
  HKEY hKey,
  DWORD dwIndex,
  LPTSTR lpValueName,
  LPDWORD lpcchValueName,
  LPDWORD lpReserved,
  LPDWORD lpType,
  LPBYTE lpData,
  LPDWORD lpcbData
);*/
  public int RegEnumValue(int hKey, int dwIndex, char[] lpValueName, IntByReference lpcchValueName, IntByReference reserved,
          IntByReference lpType, byte[] lpData, IntByReference lpcbData);

  interface SERVICE_MAIN_FUNCTION extends StdCallCallback {
    /*
    VOID WINAPI ServiceMain(
    DWORD dwArgc,
    LPTSTR* lpszArgv
    );*/
    public void callback(int dwArgc, Pointer lpszArgv);
  }

  interface Handler extends StdCallCallback {
    /*
    VOID WINAPI Handler(
      DWORD fdwControl
    );*/  
    public void callback(int fdwControl);
  }
  
  interface HandlerEx extends StdCallCallback {
    /*
    DWORD WINAPI HandlerEx(
      DWORD dwControl,
      DWORD dwEventType,
      LPVOID lpEventData,
      LPVOID lpContext
    );*/
    public int callback(int dwControl, int dwEventType, Pointer lpEventData, Pointer lpContext);
  }
  
/*
typedef struct _SERVICE_STATUS {
  DWORD dwServiceType;
  DWORD dwCurrentState;
  DWORD dwControlsAccepted;
  DWORD dwWin32ExitCode;
  DWORD dwServiceSpecificExitCode;
  DWORD dwCheckPoint;
  DWORD dwWaitHint;
} SERVICE_STATUS, 
 *LPSERVICE_STATUS;*/
  public static class SERVICE_STATUS extends Structure {
    public int dwServiceType;
    public int dwCurrentState;
    public int dwControlsAccepted;
    public int dwWin32ExitCode;
    public int dwServiceSpecificExitCode;
    public int dwCheckPoint;
    public int dwWaitHint;
  }
  
/*  
typedef struct _SERVICE_TABLE_ENTRY {
  LPTSTR lpServiceName;
  LPSERVICE_MAIN_FUNCTION lpServiceProc;
} SERVICE_TABLE_ENTRY, 
 *LPSERVICE_TABLE_ENTRY;*/  
  public static class SERVICE_TABLE_ENTRY extends Structure {
    public String lpServiceName;
    public SERVICE_MAIN_FUNCTION lpServiceProc;
  }
 
  public static class ChangeServiceConfig2Info extends Structure {
  }
  
/*
 typedef struct _SERVICE_DESCRIPTION {
  LPTSTR lpDescription;
} SERVICE_DESCRIPTION, 
 *LPSERVICE_DESCRIPTION;*/
  public static class SERVICE_DESCRIPTION extends ChangeServiceConfig2Info {
    public String lpDescription;
  }  
}


