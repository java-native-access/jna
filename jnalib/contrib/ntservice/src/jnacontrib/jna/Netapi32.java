/*
 * Netapi32.java
 *
 * Created on 2. August 2007, 13:12
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
public interface Netapi32  extends StdCallLibrary {
  Netapi32 INSTANCE = (Netapi32) Native.loadLibrary("Netapi32", Netapi32.class, Options.UNICODE_OPTIONS);
/*
NET_API_STATUS NetGetDCName(
  LPCWSTR servername,
  LPCWSTR domainname,
  LPBYTE* bufptr
);*/  
  public int NetGetDCName(String serverName, String domainName, PointerByReference bufptr);
  
/*  
DWORD DsGetDcName(
  LPCTSTR ComputerName,
  LPCTSTR DomainName,
  GUID* DomainGuid,
  LPCTSTR SiteName,
  ULONG Flags,
  PDOMAIN_CONTROLLER_INFO* DomainControllerInfo
);*/  
  public int DsGetDcName(String ComputerName, String DomainName, ByReference DomainGuid,
                   String SiteName, int Flags, PointerByReference DomainControllerInfo);
  
/*  
NET_API_STATUS NetUserGetGroups(
  LPCWSTR servername,
  LPCWSTR username,
  DWORD level,
  LPBYTE* bufptr,
  DWORD prefmaxlen,
  LPDWORD entriesread,
  LPDWORD totalentries
);*/ 
  public int NetUserGetGroups(String servername, String username, int level,
                       PointerByReference bufptr, int prefmaxlen,
                       IntByReference entriesread, IntByReference totalentries);
  
/* 
NET_API_STATUS NetUserGetLocalGroups(
  LPCWSTR servername,
  LPCWSTR username,
  DWORD level,
  DWORD flags,
  LPBYTE* bufptr,
  DWORD prefmaxlen,
  LPDWORD entriesread,
  LPDWORD totalentries
);*/
  public int NetUserGetLocalGroups(String servername, String username, int level,
                        int flags, PointerByReference bufptr, int prefmaxlen,
                       IntByReference entriesread, IntByReference totalentries);
  
/*  
NET_API_STATUS NetGroupEnum(
  LPCWSTR servername,
  DWORD level,
  LPBYTE* bufptr,
  DWORD prefmaxlen,
  LPDWORD entriesread,
  LPDWORD totalentries,
  PDWORD_PTR resume_handle
);*/  
  public int NetGroupEnum(String servername, int level, PointerByReference bufptr,
          int prefmaxlen, IntByReference entriesread, IntByReference totalentries,
          IntByReference resume_handle);
  
/*
NET_API_STATUS NetUserEnum(
  LPCWSTR servername,
  DWORD level,
  DWORD filter,
  LPBYTE* bufptr,
  DWORD prefmaxlen,
  LPDWORD entriesread,
  LPDWORD totalentries,
  LPDWORD resume_handle
);*/  
  public int NetUserEnum(String servername, int level, int filter, PointerByReference bufptr,
          int prefmaxlen, IntByReference entriesread, IntByReference totalentries,
          IntByReference resume_handle);
  
/*  
NET_API_STATUS NetApiBufferFree(
  LPVOID Buffer
);*/  
  public int NetApiBufferFree(Pointer Buffer);
  
/*  
typedef struct _GUID {
  DWORD Data1;
  WORD Data2;
  WORD Data3;
  BYTE Data4[8];
} GUID;
*/
  public static class GUID extends Structure {
    public GUID() {
      super();
    }
    
    public GUID(Pointer memory) {
      useMemory(memory);
      read();
    }
    
    public int Data1;
    public short Data2;
    public short Data3;
    public byte[] Data4 = new byte[8];
  }
  
/*  
typedef struct _DOMAIN_CONTROLLER_INFO {
  LPTSTR DomainControllerName;
  LPTSTR DomainControllerAddress;
  ULONG DomainControllerAddressType;
  GUID DomainGuid;
  LPTSTR DomainName;
  LPTSTR DnsForestName;
  ULONG Flags;
  LPTSTR DcSiteName;
  LPTSTR ClientSiteName;
} DOMAIN_CONTROLLER_INFO, 
*/
  public static class DOMAIN_CONTROLLER_INFO extends Structure {
    public DOMAIN_CONTROLLER_INFO(Pointer memory) {
      useMemory(memory);
      read();
    }
    
    public String DomainControllerName;
    public String DomainControllerAddress;
    public int DomainControllerAddressType;
    public GUID DomainGuid;
    public String DomainName;
    public String DnsForestName;
    public int Flags;
    public String DcSiteName;
    public String ClientSiteName;
  }
  
/*  
typedef struct _GROUP_USERS_INFO_0 {
  LPWSTR grui0_name;
} GROUP_USERS_INFO_0, 
 *PGROUP_USERS_INFO_0, 
 *LPGROUP_USERS_INFO_0;  
*/
  public static class GROUP_USERS_INFO_0 extends Structure {
    public GROUP_USERS_INFO_0() {
      super();
    }
    
    public GROUP_USERS_INFO_0(Pointer memory) {
      useMemory(memory);
      read();
    }
    
    public String grui0_name;
  }
  
/*  
typedef struct _LOCALGROUP_USERS_INFO_0 {
  LPWSTR lgrui0_name;
} LOCALGROUP_USERS_INFO_0, 
 *PLOCALGROUP_USERS_INFO_0, 
 *LPLOCALGROUP_USERS_INFO_0;  
 */
  public static class LOCALGROUP_USERS_INFO_0 extends Structure {
    public LOCALGROUP_USERS_INFO_0() {
      super();
    }
    
    public LOCALGROUP_USERS_INFO_0(Pointer memory) {
      useMemory(memory);
      read();
    }
    
    public String lgrui0_name;
  }
  
/*  
typedef struct _GROUP_INFO_0 {
  LPWSTR grpi0_name;
} GROUP_INFO_0, 
 *PGROUP_INFO_0, 
 *LPGROUP_INFO_0;
*/
  public static class GROUP_INFO_0 extends Structure {
    public GROUP_INFO_0() {
      super();
    }
    
    public GROUP_INFO_0(Pointer memory) {
      useMemory(memory);
      read();
    }
    
    public String grpi0_name;
  }
  
/*
typedef struct _USER_INFO_0 {
  LPWSTR usri0_name;
} USER_INFO_0, 
 *PUSER_INFO_0, 
 *LPUSER_INFO_0;*/  
  public static class USER_INFO_0 extends Structure {
    public USER_INFO_0() {
      super();
    }
    
    public USER_INFO_0(Pointer memory) {
      useMemory(memory);
      read();
    }
    
    public String usri0_name;
  }
}
  
