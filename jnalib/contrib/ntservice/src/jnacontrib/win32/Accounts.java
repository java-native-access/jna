/*
 * Accounts.java
 *
 * Created on 7. August 2007, 07:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jnacontrib.win32;

import jnacontrib.jna.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import java.util.*;

/**
 *
 * @author TB
 */
public class Accounts {
  
  /** Creates a new instance of Accounts */
  private Accounts() {
  }
  
  /**
   * Testing.
   * @param args arguments
   * @throws java.lang.Exception on error
   */
  public static void main(String[] args) throws Exception {
    System.out.println("FullDomainName: " + getFullDomainName());
    System.out.println("DomainControllerName: " + getDomainControllerName());
    System.out.println("UserDomainGroups: " + getUserDomainGroups("administrator"));
    System.out.println("UserLocalGroups: " + getUserLocalGroups(null, "administrator"));
    System.out.println("AllDomainGroups: " + getAllDomainGroups());
    System.out.println("AllDomainUsers: " + getAllDomainUsers());
    System.out.println("AccountSidString: " + getAccountSidString("administrator"));
    System.out.println("AccountNameBySid: " + getAccountName(getAccountSid("administrator")));
    System.out.println("AccountNameBySidString: " + getAccountName(getAccountSidString("administrator")));
  }
  
  /**
   * Get name of the domain.
   *
   * @return name
   */
  public static String getFullDomainName() {
    Netapi32.DOMAIN_CONTROLLER_INFO dci;
    String domain = null;
    
    dci = getDomainControllerInfo();
    
    if(dci != null) {
      domain = dci.DomainName;
    }
    return(domain);
  }
  
  /**
   * Get the name of the domain controller.
   *
   * @return name
   */
  public static String getDomainControllerName() {
    Netapi32.DOMAIN_CONTROLLER_INFO dci;
    String domainController = null;

    dci = getDomainControllerInfo();
    
    if(dci != null) {
      domainController = dci.DomainControllerName;
    }
    return(domainController);
  }
  
  /**
   * Get info about the domain controller.
   *
   * @return info
   */
  private static Netapi32.DOMAIN_CONTROLLER_INFO getDomainControllerInfo() {
    Netapi32 netapi32;
    PointerByReference pDci;
    Netapi32.DOMAIN_CONTROLLER_INFO dci = null;
    
    netapi32 = Netapi32.INSTANCE;
    pDci = new PointerByReference();
    
    if(netapi32.DsGetDcName(null, null, null, null, 0, pDci) == WINERROR.ERROR_SUCCESS) {
      dci = new Netapi32.DOMAIN_CONTROLLER_INFO(pDci.getValue());
      netapi32.NetApiBufferFree(pDci.getValue());
    }
    return(dci);
  }
  
  /**
   * Get all the domain groups where a user belongs to.
   *
   * @param userName user
   * @return TreeSet of group names
   */
  public static TreeSet<String> getUserDomainGroups(String userName) {
    Netapi32 netapi32;
    PointerByReference buf;
    IntByReference entriesread;
    IntByReference totalentries;
    Netapi32.GROUP_USERS_INFO_0 group;
    Structure[] groups;
    TreeSet<String> domainGroups;
    int i;
    
    domainGroups = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    netapi32 = Netapi32.INSTANCE;
    
    buf = new PointerByReference();
    entriesread = new IntByReference();
    totalentries = new IntByReference();
    
    if(netapi32.NetUserGetGroups(getDomainControllerName(), // servername
                                        userName, // username
                                        0, // level of user info in buffer
                                        buf, // user info buffer
                                        LMCONS.MAX_PREFERRED_LENGTH,     
                                        entriesread, // read ( out )
                                        totalentries // total ( out )
                                        ) == LMERR.NERR_Success) {
      group = new Netapi32.GROUP_USERS_INFO_0(buf.getValue());
      groups = group.toArray(entriesread.getValue());
      
      for(i = 0; i < entriesread.getValue(); i++) {
        group = (Netapi32.GROUP_USERS_INFO_0)groups[i];
        domainGroups.add(group.grui0_name);
      }
      
      if(entriesread.getValue() > 0) {
        netapi32.NetApiBufferFree(buf.getValue());
      }
    }
    return(domainGroups);
  }
  
  /**
   * Get all the local groups a user belongs to.
   *
   * @param serverName server name
   * @param userName user
   * @return TreeSetof group names
   */
  public static TreeSet<String> getUserLocalGroups(String serverName, String userName) {
    Netapi32 netapi32;
    PointerByReference lbuf;
    IntByReference lentriesread;
    IntByReference ltotalentries;
    Netapi32.LOCALGROUP_USERS_INFO_0 lgroup;
    Structure[] lgroups;
    TreeSet<String> localGroups;
    int i;
    
    localGroups = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    netapi32 = Netapi32.INSTANCE;
    
    lbuf = new PointerByReference();
    lentriesread = new IntByReference();
    ltotalentries = new IntByReference();
    if(netapi32.NetUserGetLocalGroups(null, // servername
                                        userName, // username
                                        0, // level of user info in buffer
                                        0, // flags
                                        lbuf, // user info buffer
                                        LMCONS.MAX_PREFERRED_LENGTH,     
                                        lentriesread, // read ( out )
                                        ltotalentries // total ( out )
                                        ) == LMERR.NERR_Success) {
      lgroup = new Netapi32.LOCALGROUP_USERS_INFO_0(lbuf.getValue());
      lgroups = lgroup.toArray(lentriesread.getValue());
      
      for(i = 0; i < lentriesread.getValue(); i++) {
        lgroup = (Netapi32.LOCALGROUP_USERS_INFO_0)lgroups[i];
        localGroups.add(lgroup.lgrui0_name);
      }
      
      if(lentriesread.getValue() > 0) {
        netapi32.NetApiBufferFree(lbuf.getValue());
      }
    }
    return(localGroups);
  }
  
  /**
   * Get all domain groups.
   *
   * @return  TreeSet of group names
   */
  public static TreeSet<String> getAllDomainGroups() {
    Netapi32 netapi32;
    PointerByReference gbuf;
    IntByReference gentriesread;
    IntByReference gtotalentries;
    Netapi32.GROUP_INFO_0 ggroup;
    Structure[] ggroups;
    TreeSet<String> domainGroups;
    int i;
    
    netapi32 = Netapi32.INSTANCE;
    domainGroups = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    gbuf = new PointerByReference();
    gentriesread = new IntByReference();
    gtotalentries = new IntByReference();
    
    if(netapi32.NetGroupEnum(getDomainControllerName(), // servername
                                        0, // level of user info in buffer
                                        gbuf, // user info buffer
                                        LMCONS.MAX_PREFERRED_LENGTH,     
                                        gentriesread, // read ( out )
                                        gtotalentries, // total ( out )
                                        null // resume_handle
                                        ) == LMERR.NERR_Success) {
      ggroup = new Netapi32.GROUP_INFO_0(gbuf.getValue());
      ggroups = ggroup.toArray(gentriesread.getValue());
      
      for(i = 0; i < gentriesread.getValue(); i++) {
        ggroup = (Netapi32.GROUP_INFO_0)ggroups[i];
        domainGroups.add(ggroup.grpi0_name);
      }

      if(gentriesread.getValue() > 0) {
        netapi32.NetApiBufferFree(gbuf.getValue());
      }
    }
    return(domainGroups);
  }
  
  /**
   * Get all domain users.
   *
   * @return TreeSet of user names
   */
  public static TreeSet<String> getAllDomainUsers() {
    Netapi32 netapi32;
    PointerByReference ubuf;
    IntByReference uentriesread;
    IntByReference utotalentries;
    Netapi32.USER_INFO_0 user;
    Structure[] users;
    TreeSet<String> domainUsers;
    int i;
    
    netapi32 = Netapi32.INSTANCE;
    domainUsers = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    ubuf = new PointerByReference();
    uentriesread = new IntByReference();
    utotalentries = new IntByReference();
    
    if(netapi32.NetUserEnum(getDomainControllerName(), // servername
                                        0, // level of user info in buffer
                                        LMACCESS.FILTER_NORMAL_ACCOUNT, // filter
                                        ubuf, // user info buffer
                                        LMCONS.MAX_PREFERRED_LENGTH,     
                                        uentriesread, // read ( out )
                                        utotalentries, // total ( out )
                                        null // resume_handle
                                        ) == LMERR.NERR_Success) {
      user = new Netapi32.USER_INFO_0(ubuf.getValue());
      users = user.toArray(uentriesread.getValue());
      
      for(i = 0; i < uentriesread.getValue(); i++) {
        user = (Netapi32.USER_INFO_0)users[i];
        domainUsers.add(user.usri0_name);
      }

      if(uentriesread.getValue() > 0) {
        netapi32.NetApiBufferFree(ubuf.getValue());
      }
    }
    return(domainUsers);
  }

  /**
   * Get SID of account.
   *
   * @param account account name
   * @return SID
   */
  public static byte[] getAccountSid(String account) {
    Advapi32 advapi32;
    IntByReference cbSid;
    IntByReference cchReferencedDomainName;
    PointerByReference peUse;
    byte[] sid;
    char[] referencedDomainName;
    
    advapi32 = Advapi32.INSTANCE;
    
    cbSid = new IntByReference(0);
    cchReferencedDomainName = new IntByReference(0);
    peUse = new PointerByReference();
    advapi32.LookupAccountName(null, account, null, cbSid,
            null, cchReferencedDomainName, peUse);
    sid = new byte[cbSid.getValue()];
    referencedDomainName = new char[cchReferencedDomainName.getValue()];
    
    if(! advapi32.LookupAccountName(null, account, sid, cbSid,
            referencedDomainName, cchReferencedDomainName, peUse)) {
      sid = null;
    }
    return(sid);
  }
  
  /**
   * Get account name of SID.
   *
   * @param sid SID
   * @return account name
   */
  public static String getAccountName(byte[] sid) {
    Advapi32 advapi32;
    IntByReference cchName;
    IntByReference cchReferencedDomainName;
    PointerByReference peUse;
    String sidString = null;
    char[] lpName;
    char[] referencedDomainName;
    
    advapi32 = Advapi32.INSTANCE;
    
    cchName = new IntByReference(0);
    cchReferencedDomainName = new IntByReference(0);
    peUse = new PointerByReference();
    advapi32.LookupAccountSid(null, sid, null, cchName,
            null, cchReferencedDomainName, peUse);
    lpName = new char[cchName.getValue()];
    referencedDomainName = new char[cchReferencedDomainName.getValue()];
    
    if(! advapi32.LookupAccountSid(null, sid, lpName, cchName,
            referencedDomainName, cchReferencedDomainName, peUse)) {
      lpName = null;
    }
    
    if(lpName != null) {
      return(Native.toString(lpName));
      
    } else {
      return(null);
    }
  }
  
  /**
   * Get SID of account as String.
   *
   * @param account account name
   * @return SID
   */
  public static String getAccountSidString(String account) {
    String sidString = null;
    byte[] sid;
    
    sid = getAccountSid(account);
    
    if(sid != null) {
      sidString = convertSidToString(sid);
    }
    return(sidString);
  }
  
  /**
   * Get account of SID String.
   *
   * @param sidString SID
   * @return account name
   */
  public static String getAccountName(String sidString) {
    String name = null;
    byte[] sid;
    
    sid = convertStringToSid(sidString);
    
    if(sid != null) {
      name = getAccountName(sid);
    }
    return(name);
  }  

  /**
   * Convert a SID to String.
   *
   * @param sid SID
   * @return SID String
   */
  public static String convertSidToString(byte[] sid) {
    Advapi32 advapi32;
    PointerByReference stringSid;
    String sidString = null;

    advapi32 = Advapi32.INSTANCE;
    
    stringSid = new PointerByReference();
    
    if(advapi32.ConvertSidToStringSid(sid, stringSid)) {
      sidString = stringSid.getValue().getString(0, true);
      Kernel32b.INSTANCE.LocalFree(stringSid.getValue());
    }
    return(sidString);
  }
  
  /**
   * Convert a SID String to SID.
   *
   * @param sidString SID String
   * @return SID
   */
  public static byte[] convertStringToSid(String sidString) {
    Advapi32 advapi32;
    PointerByReference pSid;
    byte[] sid = null;

    advapi32 = Advapi32.INSTANCE;
    
    pSid = new PointerByReference();
    
    if(advapi32.ConvertStringSidToSid(sidString, pSid)) {
      sid = pSid.getValue().getByteArray(0, 100);
      Kernel32b.INSTANCE.LocalFree(pSid.getValue());
    }
    return(sid);
  }
}
