/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import static com.sun.jna.platform.win32.WinBase.FILE_DIR_DISALOWED;
import static com.sun.jna.platform.win32.WinBase.FILE_ENCRYPTABLE;
import static com.sun.jna.platform.win32.WinBase.FILE_IS_ENCRYPTED;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import com.sun.jna.platform.win32.Advapi32Util.EventLogRecord;
import com.sun.jna.platform.win32.Advapi32Util.Privilege;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.SECURITY_DESCRIPTOR_RELATIVE;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;
import com.sun.jna.platform.win32.WinNT.WELL_KNOWN_SID_TYPE;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Advapi32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Advapi32UtilTest.class);
        String currentUserName = Advapi32Util.getUserName();
        System.out.println("GetUserName: " + currentUserName);

        for(Account group : Advapi32Util.getCurrentUserGroups()) {
            System.out.println(" " + group.fqn + " [" + group.sidString + "]");
        }

        Account accountByName = Advapi32Util.getAccountByName(currentUserName);
        System.out.println("AccountByName: " + currentUserName);
        System.out.println(" Fqn: " + accountByName.fqn);
        System.out.println(" Domain: " + accountByName.domain);
        System.out.println(" Sid: " + accountByName.sidString);

        Account accountBySid = Advapi32Util.getAccountBySid(new PSID(accountByName.sid));
        System.out.println("AccountBySid: " + accountByName.sidString);
        System.out.println(" Fqn: " + accountBySid.fqn);
        System.out.println(" Name: " + accountBySid.name);
        System.out.println(" Domain: " + accountBySid.domain);
    }

    public void testReadAccess() {
        final boolean access = Advapi32Util.accessCheck(new File(System.getProperty("java.io.tmpdir")), Advapi32Util.AccessCheckPermission.READ);
        assertTrue(access);
    }

    public void testWriteAccess() {
        final boolean access = Advapi32Util.accessCheck(new File(System.getProperty("java.io.tmpdir")), Advapi32Util.AccessCheckPermission.WRITE);
        assertTrue(access);
    }


    public void testExecuteAccess() {
        final boolean access = Advapi32Util.accessCheck(new File(System.getProperty("java.io.tmpdir")), Advapi32Util.AccessCheckPermission.EXECUTE);
        assertTrue(access);
    }

    public void testGetUsername() {
        String username = Advapi32Util.getUserName();
        assertTrue(username.length() > 0);
    }

    public void testGetAccountBySid() {
        String accountName = Advapi32Util.getUserName();
        Account currentUser = Advapi32Util.getAccountByName(accountName);
        Account account = Advapi32Util.getAccountBySid(new PSID(currentUser.sid));
        assertEquals(SID_NAME_USE.SidTypeUser, account.accountType);
        assertEquals(currentUser.fqn.toLowerCase(), account.fqn.toLowerCase());
        assertEquals(currentUser.name.toLowerCase(), account.name.toLowerCase());
        assertEquals(currentUser.domain.toLowerCase(), account.domain.toLowerCase());
        assertEquals(currentUser.sidString, account.sidString);
    }

    public void testGetAccountByName() {
        String accountName = Advapi32Util.getUserName();
        Account account = Advapi32Util.getAccountByName(accountName);
        assertEquals(SID_NAME_USE.SidTypeUser, account.accountType);
    }

    public void testGetAccountNameFromSid() {
        if(AbstractWin32TestSupport.isEnglishLocale) {
            assertEquals("Everyone", Advapi32Util.getAccountBySid("S-1-1-0").name);
        } else {
            System.err.println("testGetAccountNameFromSid Test can only be run on english locale");
        }
    }

    public void testGetAccountSidFromName() {
        if(AbstractWin32TestSupport.isEnglishLocale) {
            assertEquals("S-1-1-0", Advapi32Util.getAccountByName("Everyone").sidString);
        } else {
            System.err.println("testGetAccountSidFromName Test can only be run on english locale");
        }
    }

    public void testGetAccountNameRoundtrip() {
        // This test ensures getAccountBySid and getAccountByName are at least
        // symmetrical. The names of the accounts are locale dependend so this is
        // a compromise. (german: "Jeder", english: "Everybody")
        String worldSID = "S-1-1-0"; // https://msdn.microsoft.com/en-us/library/windows/desktop/aa379649(v=vs.85).aspx
        String accountNameResolved = Advapi32Util.getAccountBySid(worldSID).name;
        String roundTripSid = Advapi32Util.getAccountByName(accountNameResolved).sidString;
        assertNotNull(accountNameResolved);
        assertTrue(! accountNameResolved.isEmpty());
        assertEquals(worldSID, roundTripSid);
    }

    public void testConvertSid() {
        String sidString = "S-1-1-0"; // Everyone
        byte[] sidBytes = Advapi32Util.convertStringSidToSid(sidString);
        assertTrue(sidBytes.length > 0);
        String convertedSidString = Advapi32Util.convertSidToStringSid(new PSID(sidBytes));
        assertEquals(convertedSidString, sidString);
    }

    public void testGetCurrentUserGroups() {
        Account[] groups = Advapi32Util.getCurrentUserGroups();
        assertTrue(groups.length > 0);
        for(Account group : groups) {
            assertTrue(group.name.length() > 0);
            assertTrue(group.sidString.length() > 0);
            assertTrue(group.sid.length > 0);
        }
    }

    public void testGetUserGroups() {
        USER_INFO_1 userInfo = new USER_INFO_1();
        userInfo.usri1_name = "JNANetapi32TestUser";
        userInfo.usri1_password = "!JNAP$$Wrd0";
        userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
        // ignore test if not able to add user (need to be administrator to do this).
        if (LMErr.NERR_Success != Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null)) {
            return;
        }
        try {
            HANDLEByReference phUser = new HANDLEByReference();
            try {
                assertTrue(Advapi32.INSTANCE.LogonUser(userInfo.usri1_name, null, userInfo.usri1_password,
                        WinBase.LOGON32_LOGON_NETWORK, WinBase.LOGON32_PROVIDER_DEFAULT, phUser));
                Account primaryGroup = Advapi32Util.getTokenPrimaryGroup(phUser.getValue());
                assertTrue(primaryGroup.name.length() > 0);
                assertTrue(primaryGroup.sidString.length() > 0);
                assertTrue(primaryGroup.sid.length > 0);
                Account[] groups = Advapi32Util.getTokenGroups(phUser.getValue());
                boolean primaryGroupFound = false;
                assertTrue(groups.length > 0);
                for (Account group : groups) {
                    assertTrue(group.name.length() > 0);
                    assertTrue(group.sidString.length() > 0);
                    assertTrue(group.sid.length > 0);
                    if (primaryGroup.name.equals(group.name)) {
                        primaryGroupFound = true;
                    }
                }
                assertTrue("PrimaryGroup must be in group list", primaryGroupFound);
            } finally {
                HANDLE hUser = phUser.getValue();
                if (!WinBase.INVALID_HANDLE_VALUE.equals(hUser)) {
                    Kernel32Util.closeHandle(hUser);
                }
            }
        } finally {
            assertEquals("Error in NetUserDel",
                         LMErr.NERR_Success,
                         Netapi32.INSTANCE.NetUserDel(null, userInfo.usri1_name));
        }
    }

    public void testGetUserAccount() {
        USER_INFO_1 userInfo = new USER_INFO_1();
        userInfo.usri1_name = "JNANetapi32TestUser";
        userInfo.usri1_password = "!JNAP$$Wrd0";
        userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
        // ignore test if not able to add user (need to be administrator to do this).
        if (LMErr.NERR_Success != Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null)) {
            return;
        }
        try {
            HANDLEByReference phUser = new HANDLEByReference();
            try {
                assertTrue(Advapi32.INSTANCE.LogonUser(userInfo.usri1_name,
                                                       null, userInfo.usri1_password, WinBase.LOGON32_LOGON_NETWORK,
                                                       WinBase.LOGON32_PROVIDER_DEFAULT, phUser));
                Advapi32Util.Account account = Advapi32Util.getTokenAccount(phUser.getValue());
                assertTrue(account.name.length() > 0);
                assertEquals(userInfo.usri1_name, account.name);
            } finally {
                HANDLE hUser = phUser.getValue();
                if (!WinBase.INVALID_HANDLE_VALUE.equals(hUser)) {
                    Kernel32Util.closeHandle(hUser);
                }
            }
        } finally {
            assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(null, userInfo.usri1_name));
        }
    }

    public void testRegistryKeyExists() {
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE,
                                                  ""));
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE,
                                                  "Software\\Microsoft"));
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE,
                                                   "KeyDoesNotExist\\SubKeyDoesNotExist"));
    }

    public void testRegistryKeyExistsSamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY));
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY));
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistryValueExists() {
        assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE,
                                                     "Software\\Microsoft", ""));
        assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE,
                                                     "Software\\Microsoft", "KeyDoesNotExist"));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE,
                                                    "SYSTEM\\CurrentControlSet\\Control", "SystemBootDevice"));
        assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "FAIL", "Path"));
    }

    public void testRegistryValueExistsSamExtra() {
        if (!is64bitWindows()) {
            return;
        }

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", 64, WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", 64, WinNT.KEY_WOW64_32KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_64KEY));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_32KEY);
        assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_64KEY));
        assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistryCreateDeleteKey() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA"));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA"));
    }

    public void testRegistryCreateDeleteKeySamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY));
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY));
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY));

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY));
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY));
        assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY));
    }

    public void testRegistryCreateKeyDisposition() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        assertTrue(Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA"));
        assertFalse(Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA"));
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA"));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistryCreateKeyDispositionSamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        assertTrue(Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY));
        assertTrue(Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY));
        assertFalse(Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY));
        assertFalse(Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY));
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY));
        assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistryDeleteValue() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue", 42);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue"));
        Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue");
        assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue"));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistryDeleteValueSamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", 64, WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", 32, WinNT.KEY_WOW64_32KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_64KEY));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_64KEY);
        assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_64KEY));
        Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_32KEY);
        assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistrySetGetIntValue() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue", 42);
        assertEquals(42, Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER,
                                                          "Software\\JNA", "IntValue"));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue"));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistrySetGetIntValueSamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);

        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", 64, WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", 32, WinNT.KEY_WOW64_32KEY);
        assertEquals(64, Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_64KEY));
        assertEquals(32, Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_32KEY));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_64KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "IntValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistrySetGetLongValue() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "LongValue", 1234L);
        assertEquals(1234L, Advapi32Util.registryGetLongValue(WinReg.HKEY_CURRENT_USER,
                                                              "Software\\JNA", "LongValue"));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "LongValue"));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistrySetGetLongValueSamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "LongValue", 64L, WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "LongValue", 32L, WinNT.KEY_WOW64_32KEY);
        assertEquals(64L, Advapi32Util.registryGetLongValue(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", "LongValue", WinNT.KEY_WOW64_64KEY));
        assertEquals(32L, Advapi32Util.registryGetLongValue(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", "LongValue", WinNT.KEY_WOW64_32KEY));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "LongValue", WinNT.KEY_WOW64_64KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "LongValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistrySetGetStringValue() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringValue", "Hello World");
        assertEquals("Hello World", Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                                                                        "Software\\JNA", "StringValue"));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringValue"));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistrySetGetStringValueSamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "StringValue", "Hello World64", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "StringValue", "Hello World32", WinNT.KEY_WOW64_32KEY);
        assertEquals("Hello World64", Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", "StringValue", WinNT.KEY_WOW64_64KEY));
        assertEquals("Hello World32", Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", "StringValue", WinNT.KEY_WOW64_32KEY));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "StringValue", WinNT.KEY_WOW64_64KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "StringValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistrySetGetExpandableStringValue() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registrySetExpandableStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringValue", "Temp is %TEMP%");
        assertEquals("Temp is %TEMP%", Advapi32Util.registryGetExpandableStringValue(WinReg.HKEY_CURRENT_USER,
                                                                                     "Software\\JNA", "StringValue"));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringValue"));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistrySetGetExpandableStringValueSamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registrySetExpandableStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "StringValue", "64 Temp is %TEMP%", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetExpandableStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "StringValue", "32 Temp is %TEMP%", WinNT.KEY_WOW64_32KEY);
        assertEquals("64 Temp is %TEMP%", Advapi32Util.registryGetExpandableStringValue(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", "StringValue", WinNT.KEY_WOW64_64KEY));
        assertEquals("32 Temp is %TEMP%", Advapi32Util.registryGetExpandableStringValue(WinReg.HKEY_CURRENT_USER,
                "Software\\Classes\\CLSID\\JNA", "StringValue", WinNT.KEY_WOW64_32KEY));
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "StringValue", WinNT.KEY_WOW64_64KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "StringValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistrySetGetStringArray() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        String[] dataWritten = { "Hello", "World" };
        Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "MultiStringValue", dataWritten);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "MultiStringValue"));
        String[] dataRead = Advapi32Util.registryGetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "MultiStringValue");
        this.assertStringArraysEqual(dataWritten, dataRead);
        dataWritten = new String[0];
        Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "EmptyMultiString", dataWritten);
        dataRead = Advapi32Util.registryGetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "EmptyMultiString");
        assertEquals(0, dataRead.length);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistrySetGetStringArraySamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        String[] dataWritten64 = { "Hello", "World", "64" };
        String[] dataWritten32 = { "Hello", "World", "32" };
        Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "MultiStringValue", dataWritten64, WinNT.KEY_WOW64_64KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "MultiStringValue", WinNT.KEY_WOW64_64KEY));
        Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "MultiStringValue", dataWritten32, WinNT.KEY_WOW64_32KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "MultiStringValue", WinNT.KEY_WOW64_32KEY));
        String[] dataRead64 = Advapi32Util.registryGetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "MultiStringValue", WinNT.KEY_WOW64_64KEY);
        String[] dataRead32 = Advapi32Util.registryGetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "MultiStringValue", WinNT.KEY_WOW64_32KEY);
        this.assertStringArraysEqual(dataWritten64, dataRead64);
        this.assertStringArraysEqual(dataWritten32, dataRead32);
        dataWritten64 = new String[0];
        dataWritten32 = new String[0];
        Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "EmptyMultiString", dataWritten64, WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "EmptyMultiString", dataWritten32, WinNT.KEY_WOW64_32KEY);
        dataRead64 = Advapi32Util.registryGetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "EmptyMultiString", WinNT.KEY_WOW64_64KEY);
        dataRead32 = Advapi32Util.registryGetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "EmptyMultiString", WinNT.KEY_WOW64_32KEY);
        assertEquals(0, dataRead32.length);
        assertEquals(0, dataRead64.length);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    private void assertStringArraysEqual(String[] dataWritten, String[] dataRead) {
        assertEquals(dataWritten.length, dataRead.length);
        for(int i = 0; i < dataRead.length; i++) {
            assertEquals(dataWritten[i], dataRead[i]);
        }
    }

    public void testRegistrySetGetBinaryValue() {
        byte[] data = { 0x00, 0x01, 0x02 };
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "BinaryValue", data);
        byte[] read = Advapi32Util.registryGetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "BinaryValue");
        assertBinaryArraysEqual(read, data);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "BinaryValue"));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistrySetGetBinaryValueSamExtra() {
        if (!is64bitWindows()) return;

        byte[] data32 = { 0x00, 0x01, 0x02, 0x32 };
        byte[] data64 = { 0x00, 0x01, 0x02, 0x64 };
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "BinaryValue", data64, WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "BinaryValue", data32, WinNT.KEY_WOW64_32KEY);
        byte[] read64 = Advapi32Util.registryGetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "BinaryValue", WinNT.KEY_WOW64_64KEY);
        byte[] read32 = Advapi32Util.registryGetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "BinaryValue", WinNT.KEY_WOW64_32KEY);
        assertBinaryArraysEqual(read64, data64);
        assertBinaryArraysEqual(read32, data32);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "BinaryValue", WinNT.KEY_WOW64_64KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "BinaryValue", WinNT.KEY_WOW64_32KEY));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    private void assertBinaryArraysEqual(byte[] dataWritten, byte[] dataRead) {
        assertEquals(dataWritten.length, dataRead.length);
        for(int i = 0; i < dataRead.length; i++) {
            assertEquals(dataWritten[i], dataRead[i]);
        }
    }

    public void testRegistryGetKeys() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key1");
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key2");
        String[] subKeys = Advapi32Util.registryGetKeys(WinReg.HKEY_CURRENT_USER, "Software\\JNA");
        assertEquals(2, subKeys.length);
        assertEquals(subKeys[0], "Key1");
        assertEquals(subKeys[1], "Key2");
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key1");
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key2");
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistryGetKeysSamExtra() {
        if (!is64bitWindows()) return;

        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Key1", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Key1", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Key2-64", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Key2-32", WinNT.KEY_WOW64_32KEY);
        String[] subKeys64 = Advapi32Util.registryGetKeys(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY);
        String[] subKeys32 = Advapi32Util.registryGetKeys(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY);
        assertEquals(2, subKeys64.length);
        assertEquals(subKeys64[0], "Key1");
        assertEquals(subKeys64[1], "Key2-64");
        assertEquals(2, subKeys32.length);
        assertEquals(subKeys32[0], "Key1");
        assertEquals(subKeys32[1], "Key2-32");
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Key1", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Key1", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Key2-64", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Key2-32", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistryGetCloseKey() {
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key1");
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key2");
        HKEYByReference phkKey = Advapi32Util.registryGetKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", WinNT.KEY_READ);
        String[] subKeys = Advapi32Util.registryGetKeys(phkKey.getValue());
        assertEquals(2, subKeys.length);
        assertEquals(subKeys[0], "Key1");
        assertEquals(subKeys[1], "Key2");
        Advapi32Util.registryCloseKey(phkKey.getValue());
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key1");
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key2");
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistryLoadAppKey() throws Exception {
        File tempDir = Files.createTempDirectory("testRegistryLoadAppKey").toFile();
        File registryFile = new File(tempDir, "privateregistry.bin");
        HKEYByReference phkKey = Advapi32Util.registryLoadAppKey(registryFile.getAbsolutePath(), WinNT.KEY_ALL_ACCESS, 0);
        Advapi32Util.registryCreateKey(phkKey.getValue(), "Test");
        Advapi32Util.registryDeleteKey(phkKey.getValue(), "Test");
        Advapi32Util.registryCloseKey(phkKey.getValue());
        registryFile.delete();
        tempDir.delete();
    }

    public void testRegistryGetValues() {
        String uu = "A\\u00ea\\u00f1\\u00fcC";
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "FourtyTwo" + uu, 42);
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "42" + uu, "FourtyTwo" + uu);
        Advapi32Util.registrySetExpandableStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "ExpandableString", "%TEMP%");
        byte[] dataWritten = { 0xD, 0xE, 0xA, 0xD, 0xB, 0xE, 0xE, 0xF };
        Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "DeadBeef", dataWritten);
        String[] stringsWritten = { "Hello", "World", "Hello World", uu };
        Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringArray", stringsWritten);
        String[] emptyArray = new String[0];
        Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "EmptyStringArray", emptyArray);
        Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "EmptyBinary", new byte[0]);
        TreeMap<String, Object> values = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, "Software\\JNA");
        assertEquals(7, values.keySet().size());
        assertEquals("FourtyTwo" + uu, values.get("42" + uu));
        assertEquals(42, values.get("FourtyTwo" + uu));
        assertEquals("%TEMP%", values.get("ExpandableString"));
        byte[] dataRead = (byte[]) values.get("DeadBeef");
        assertEquals(dataWritten.length, dataRead.length);
        for(int i = 0; i < dataWritten.length; i++) {
            assertEquals(dataWritten[i], dataRead[i]);
        }
        String[] stringsRead = (String[]) values.get("StringArray");
        assertEquals(stringsWritten.length, stringsRead.length);
        for(int i = 0; i < stringsWritten.length; i++) {
            assertEquals(stringsWritten[i], stringsRead[i]);
        }
        stringsRead = (String[]) values.get("EmptyStringArray");
        assertEquals(0, stringsRead.length);
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
    }

    public void testRegistryGetValuesSamExtra() {
        if (!is64bitWindows()) return;

        String uu = "A\\u00ea\\u00f1\\u00fcC";
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Number" + uu, 64, WinNT.KEY_WOW64_64KEY);
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", "Number" + uu, 32, WinNT.KEY_WOW64_32KEY);
        TreeMap<String, Object> values64 = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_64KEY);
        TreeMap<String, Object> values32 = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID\\JNA", WinNT.KEY_WOW64_32KEY);
        assertEquals(1, values64.keySet().size());
        assertEquals(64, values64.get("Number" + uu));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_64KEY);
        assertEquals(1, values32.keySet().size());
        assertEquals(32, values32.get("Number" + uu));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\Classes\\CLSID", "JNA", WinNT.KEY_WOW64_32KEY);
    }

    public void testRegistryGetEmptyValues() {
        HKEY root = WinReg.HKEY_CURRENT_USER;
        String keyPath = "Software\\JNA";
        Advapi32Util.registryCreateKey(root, "Software", "JNA");
        doTestRegistryGetEmptyValues(root, keyPath, WinNT.REG_BINARY);
        doTestRegistryGetEmptyValues(root, keyPath, WinNT.REG_EXPAND_SZ);
        doTestRegistryGetEmptyValues(root, keyPath, WinNT.REG_MULTI_SZ);
        doTestRegistryGetEmptyValues(root, keyPath, WinNT.REG_NONE);
        doTestRegistryGetEmptyValues(root, keyPath, WinNT.REG_SZ);
        Advapi32Util.registryDeleteKey(root, "Software", "JNA");
    }

    private void doTestRegistryGetEmptyValues(HKEY root, String keyPath, int valueType) {
        String valueName = "EmptyValue";
        registrySetEmptyValue(root, keyPath, valueName, valueType);
        Map<String, Object> values = Advapi32Util.registryGetValues(root, keyPath);
        assertEquals(1, values.size());
        assertTrue(values.containsKey(valueName));
    }

    private static void registrySetEmptyValue(HKEY root, String keyPath, String name, final int valueType) {
        HKEYByReference phkKey = new HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
        if (rc != W32Errors.ERROR_SUCCESS) {
            throw new Win32Exception(rc);
        }
        try {
            char[] data = new char[0];
            rc = Advapi32.INSTANCE.RegSetValueEx(phkKey.getValue(), name, 0, valueType, data, 0);
            if (rc != W32Errors.ERROR_SUCCESS) {
                throw new Win32Exception(rc);
            }
        } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != W32Errors.ERROR_SUCCESS) {
                throw new Win32Exception(rc);
            }
        }
    }

    public void testIsWellKnownSid() {
        String everyoneString = "S-1-1-0";
        assertTrue(Advapi32Util.isWellKnownSid(everyoneString, WELL_KNOWN_SID_TYPE.WinWorldSid));
        assertFalse(Advapi32Util.isWellKnownSid(everyoneString, WELL_KNOWN_SID_TYPE.WinAccountAdministratorSid));
        byte[] everyoneBytes = Advapi32Util.convertStringSidToSid(everyoneString);
        assertTrue(Advapi32Util.isWellKnownSid(everyoneBytes, WELL_KNOWN_SID_TYPE.WinWorldSid));
        assertFalse(Advapi32Util.isWellKnownSid(everyoneBytes, WELL_KNOWN_SID_TYPE.WinAccountAdministratorSid));
    }

    public void testEventLogIteratorForwards() {
        EventLogIterator iter = new EventLogIterator("Application");
        try {
            int max = 100;
            int lastId = 0;
            while(iter.hasNext()) {
                EventLogRecord record = iter.next();
                assertTrue(record.getRecordNumber() > lastId);
                lastId = record.getRecordNumber();
                assertNotNull(record.getType().name());
                assertNotNull(record.getSource());
                if (record.getRecord().DataLength.intValue() > 0) {
                    assertEquals(record.getData().length,
                                 record.getRecord().DataLength.intValue());
                } else {
                    assertNull(record.getData());
                }
                if (record.getRecord().NumStrings.intValue() > 0) {
                    assertEquals(record.getStrings().length,
                                 record.getRecord().NumStrings.intValue());
                } else {
                    assertNull(record.getStrings());
                }

                if (max-- <= 0) {
                    break; // shorten test
                }
                /*
                  System.out.println(record.getRecordNumber()
                  + ": Event ID: " + record.getEventId()
                  + ", Event Type: " + record.getType()
                  + ", Event Source: " + record.getSource());
                */
            }
        } finally {
            iter.close();
        }
    }

    public void testEventLogIteratorBackwards() {
        EventLogIterator iter = new EventLogIterator(null,
                                                     "Application", WinNT.EVENTLOG_BACKWARDS_READ);
        try {
            int max = 100;
            int lastId = -1;
            while(iter.hasNext()) {
                EventLogRecord record = iter.next();
                /*
                  System.out.println(record.getRecordNumber()
                  + ": Event ID: " + record.getEventId()
                  + ", Event Type: " + record.getType()
                  + ", Event Source: " + record.getSource());
                */
                assertTrue(record.getRecordNumber() < lastId || lastId == -1);
                lastId = record.getRecordNumber();
                if (max-- <= 0) {
                    break; // shorten test
                }
            }
        } finally {
            iter.close();
        }
    }

    public void testGetEnvironmentBlock() {
        String expected = "KEY=value\0"
            + "KEY_EMPTY=\0"
            + "KEY_NUMBER=2\0"
            + "\0";

        // Order is important to kept checking result simple
        Map<String, String> mockEnvironment = new TreeMap<>();
        mockEnvironment.put("KEY", "value");
        mockEnvironment.put("KEY_EMPTY", "");
        mockEnvironment.put("KEY_NUMBER", "2");
        mockEnvironment.put("KEY_NULL", null);

        String block = Advapi32Util.getEnvironmentBlock(mockEnvironment);
        assertEquals("Environment block must comprise key=value pairs separated by NUL characters", expected, block);
    }

    public void testGetFileSecurityDescriptor() throws Exception {
        File file = createTempFile();
        SECURITY_DESCRIPTOR_RELATIVE sdr = Advapi32Util.getFileSecurityDescriptor(file, false);
        assertTrue(Advapi32.INSTANCE.IsValidSecurityDescriptor(sdr.getPointer()));
        file.delete();
    }

    public void testSetFileSecurityDescriptor() throws Exception {
        File file = createTempFile();
        SECURITY_DESCRIPTOR_RELATIVE sdr = Advapi32Util.getFileSecurityDescriptor(file, false);
        Advapi32Util.setFileSecurityDescriptor(file, sdr, false, true, true, false, true, false);
        sdr = Advapi32Util.getFileSecurityDescriptor(file, false);
        assertTrue(Advapi32.INSTANCE.IsValidSecurityDescriptor(sdr.getPointer()));
        file.delete();
    }

    public void testEncryptFile() throws Exception {
        File file = createTempFile();
        assertEquals(FILE_ENCRYPTABLE, Advapi32Util.fileEncryptionStatus(file));
        Advapi32Util.encryptFile(file);
        assertEquals(FILE_IS_ENCRYPTED, Advapi32Util.fileEncryptionStatus(file));
        file.delete();
    }

    public void testDecryptFile() throws Exception {
        File file = createTempFile();
        Advapi32Util.encryptFile(file);
        assertEquals(FILE_IS_ENCRYPTED, Advapi32Util.fileEncryptionStatus(file));
        Advapi32Util.decryptFile(file);
        assertEquals(FILE_ENCRYPTABLE, Advapi32Util.fileEncryptionStatus(file));
        file.delete();
    }

    public void testDisableEncryption() throws Exception {
        File dir = new File(System.getProperty("java.io.tmpdir") + File.separator
                + System.nanoTime());
        dir.mkdir();
        assertEquals(FILE_ENCRYPTABLE, Advapi32Util.fileEncryptionStatus(dir));
        Advapi32Util.disableEncryption(dir, true);
        assertEquals(FILE_DIR_DISALOWED, Advapi32Util.fileEncryptionStatus(dir));
        Advapi32Util.disableEncryption(dir, false);
        assertEquals(FILE_ENCRYPTABLE, Advapi32Util.fileEncryptionStatus(dir));
        for (File file : dir.listFiles()) {
            file.delete();
        }
        dir.delete();
    }

    public void testBackupEncryptedFile() throws Exception {
        // backup an encrypted file
        File srcFile = createTempFile();
        Advapi32Util.encryptFile(srcFile);
        File dest = new File(System.getProperty("java.io.tmpdir") + File.separator
                + "backup" + System.nanoTime());
        dest.mkdir();

        Advapi32Util.backupEncryptedFile(srcFile, dest);

        // simple check to see if a backup file exist
        File backupFile = new File(dest.getAbsolutePath() + File.separator +
                srcFile.getName());
        assertTrue(backupFile.exists());
        assertEquals(srcFile.length(), backupFile.length());

        // backup an encrypted directory
        File srcDir = new File(System.getProperty("java.io.tmpdir") + File.separator
                + System.nanoTime());
        srcDir.mkdir();
        Advapi32Util.encryptFile(srcDir);

        Advapi32Util.backupEncryptedFile(srcDir, dest);

        // Check to see if a backup directory exist
        File backupDir = new File(dest.getAbsolutePath() + File.separator + srcDir.getName());
        assertTrue(backupDir.exists());

        // clean up
        srcFile.delete();
        for (File file : srcDir.listFiles()) {
            file.delete();
        }
        srcDir.delete();
        for (File file : dest.listFiles()) {
            file.delete();
        }
        dest.delete();
    }

    /**
     * Test Privilege class
     */
    public void testPrivilege() {
        // Test multiple known privileges
        Privilege privilege  = new Privilege(WinNT.SE_ASSIGNPRIMARYTOKEN_NAME, WinNT.SE_BACKUP_NAME);
        try {
            privilege.enable();
            // Will throw if it fails p.enable() fails
        }
        finally {
            privilege.close();
        }

        // Test unknown privilege
        try {
            privilege  = new Privilege("NOT_A_PRIVILEGE");
        }
        catch (IllegalArgumentException ex) {
            // Exception is expected
        }
        catch (Exception ex) {
            fail("Encountered unknown exception - " + ex.getMessage());
        }
        finally {
            privilege.close();
        }
    }

    /**
     * Test TOKEN_ELEVATION structure
     */
    public void testIsCurrentProcessElevated() {
        // This is either true if we're elevated or false otherwise. Just exercising the function.
        try {
            Advapi32Util.isCurrentProcessElevated();
        } catch (Exception ex) {
            fail("Encountered unknown exception - " + ex.getMessage());
        }
    }

    private File createTempFile() throws Exception{
        String filePath = System.getProperty("java.io.tmpdir") + System.nanoTime()
                + ".text";
        File file = new File(filePath);
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        for (int i = 0; i < 1000; i++) {
            fileWriter.write("Sample text " + i + System.getProperty("line.separator"));
        }
        fileWriter.close();
        return file;
    }

    private boolean is64bitWindows() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        return arch != null && arch.endsWith("64")
                || wow64Arch != null && wow64Arch.endsWith("64");
    }
}
