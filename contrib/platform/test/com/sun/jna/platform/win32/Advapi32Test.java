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

import static com.sun.jna.platform.win32.WinBase.CREATE_FOR_IMPORT;
import static com.sun.jna.platform.win32.WinBase.FILE_DIR_DISALOWED;
import static com.sun.jna.platform.win32.WinBase.FILE_ENCRYPTABLE;
import static com.sun.jna.platform.win32.WinBase.FILE_IS_ENCRYPTED;
import static com.sun.jna.platform.win32.WinBase.FILE_READ_ONLY;
import static com.sun.jna.platform.win32.WinNT.DACL_SECURITY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.FILE_ALL_ACCESS;
import static com.sun.jna.platform.win32.WinNT.FILE_GENERIC_EXECUTE;
import static com.sun.jna.platform.win32.WinNT.FILE_GENERIC_READ;
import static com.sun.jna.platform.win32.WinNT.FILE_GENERIC_WRITE;
import static com.sun.jna.platform.win32.WinNT.GENERIC_ALL;
import static com.sun.jna.platform.win32.WinNT.GENERIC_EXECUTE;
import static com.sun.jna.platform.win32.WinNT.GENERIC_READ;
import static com.sun.jna.platform.win32.WinNT.GENERIC_WRITE;
import static com.sun.jna.platform.win32.WinNT.GROUP_SECURITY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.OWNER_SECURITY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.SACL_SECURITY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.SE_RESTORE_NAME;
import static com.sun.jna.platform.win32.WinNT.SE_SECURITY_NAME;
import static com.sun.jna.platform.win32.WinNT.TOKEN_ADJUST_PRIVILEGES;
import static com.sun.jna.platform.win32.WinNT.TOKEN_DUPLICATE;
import static com.sun.jna.platform.win32.WinNT.TOKEN_IMPERSONATE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import com.sun.jna.platform.win32.Advapi32Util.EventLogRecord;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.platform.win32.WinBase.FE_EXPORT_FUNC;
import com.sun.jna.platform.win32.WinBase.FE_IMPORT_FUNC;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.ACCESS_ALLOWED_ACE;
import com.sun.jna.platform.win32.WinNT.ACL;
import com.sun.jna.platform.win32.WinNT.EVENTLOGRECORD;
import com.sun.jna.platform.win32.WinNT.GENERIC_MAPPING;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PACLByReference;
import com.sun.jna.platform.win32.WinNT.PRIVILEGE_SET;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.PSIDByReference;
import com.sun.jna.platform.win32.WinNT.SECURITY_DESCRIPTOR;
import com.sun.jna.platform.win32.WinNT.SECURITY_DESCRIPTOR_RELATIVE;
import com.sun.jna.platform.win32.WinNT.SECURITY_IMPERSONATION_LEVEL;
import com.sun.jna.platform.win32.WinNT.SID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;
import com.sun.jna.platform.win32.WinNT.TOKEN_PRIVILEGES;
import com.sun.jna.platform.win32.WinNT.TOKEN_TYPE;
import com.sun.jna.platform.win32.WinNT.WELL_KNOWN_SID_TYPE;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.platform.win32.Winsvc.SC_STATUS_TYPE;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS_PROCESS;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Advapi32Test extends TestCase {

    private static final String EVERYONE = "S-1-1-0";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Advapi32Test.class);
    }

    // see https://github.com/twall/jna/issues/482
    public void testNoDuplicateMethodsNames() {
        Collection<String> dupSet = AbstractWin32TestSupport.detectDuplicateMethods(Advapi32.class);
        if (dupSet.size() > 0) {
            for (String name : new String[]{
                // these have several overloads by design since the input/output values can be several types of data
                "RegQueryValueEx",
                "RegSetValueEx",
                "RegGetValue",
                "RegEnumValue"
            }) {
                dupSet.remove(name);
            }
        }

        assertTrue("Duplicate methods found: " + dupSet, dupSet.isEmpty());
    }

    public void testGetUserName() {
        IntByReference len = new IntByReference();
        assertFalse(Advapi32.INSTANCE.GetUserNameW(null, len));
        assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
        char[] buffer = new char[len.getValue()];
        assertTrue(Advapi32.INSTANCE.GetUserNameW(buffer, len));
        String username = Native.toString(buffer);
        assertTrue(username.length() > 0);
    }

    public void testLookupAccountName() {
        IntByReference pSid = new IntByReference(0);
        IntByReference pDomain = new IntByReference(0);
        PointerByReference peUse = new PointerByReference();
        String accountName = "Administrator";
        assertFalse(Advapi32.INSTANCE.LookupAccountName(
                null, accountName, null, pSid, null, pDomain, peUse));
        int lastError = Kernel32.INSTANCE.GetLastError();
        // The Administrator account may not exist
        if (lastError != W32Errors.ERROR_NONE_MAPPED) {
            assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, lastError);
            assertTrue(pSid.getValue() > 0);
            Memory sidMemory = new Memory(pSid.getValue());
            PSID pSidMemory = new PSID(sidMemory);
            char[] referencedDomainName = new char[pDomain.getValue() + 1];
            assertTrue(Advapi32.INSTANCE.LookupAccountName(null, accountName, pSidMemory, pSid, referencedDomainName,
                    pDomain, peUse));
            assertEquals(SID_NAME_USE.SidTypeUser, peUse.getPointer().getInt(0));
            assertTrue(Native.toString(referencedDomainName).length() > 0);
        }
    }

    public void testIsValidSid() {
        String sidString = EVERYONE;
        PSIDByReference sid = new PSIDByReference();
        assertTrue("SID conversion failed", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));

        PSID value = sid.getValue();
        try {
            assertTrue("Converted SID not valid: " + value, Advapi32.INSTANCE.IsValidSid(value));
            int sidLength = Advapi32.INSTANCE.GetLengthSid(value);
            assertTrue("Non positive sid length", sidLength > 0);
            assertTrue("Invalid sid", Advapi32.INSTANCE.IsValidSid(value));
        } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
        }
    }

    public void testEqualSid() {
        String sidString = EVERYONE;
        PSIDByReference sid1 = new PSIDByReference();
        PSIDByReference sid2 = new PSIDByReference();
        assertTrue("SID1 conversion failed", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid1));
        assertTrue("SID2 conversion failed", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid2));

        try {
            assertTrue("Converted SID1 not valid", Advapi32.INSTANCE.IsValidSid(sid1.getValue()));
            assertTrue("Converted SID2 not valid", Advapi32.INSTANCE.IsValidSid(sid2.getValue()));
            assertTrue("Invalid sid", Advapi32.INSTANCE.EqualSid(sid1.getValue(), sid2.getValue()));
        } finally {
            Kernel32Util.freeLocalMemory(sid1.getValue().getPointer());
            Kernel32Util.freeLocalMemory(sid2.getValue().getPointer());
        }
    }

    public void testGetSidLength() {
        String sidString = EVERYONE;
        PSIDByReference sid = new PSIDByReference();
        assertTrue("SID conversion failed", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));

        PSID value = sid.getValue();
        try {
            assertEquals("Wrong SID length", 12, Advapi32.INSTANCE.GetLengthSid(value));
        } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
        }
    }

    public void testLookupAccountSid() {
        // get SID bytes
        String sidString = EVERYONE;
        PSIDByReference sid = new PSIDByReference();
        assertTrue("Failed to create sid", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));

        PSID value = sid.getValue();
        try {
            int sidLength = Advapi32.INSTANCE.GetLengthSid(value);
            assertTrue("Non-positive sid length", sidLength > 0);
            // lookup account
            IntByReference cchName = new IntByReference();
            IntByReference cchReferencedDomainName = new IntByReference();
            PointerByReference peUse = new PointerByReference();
            assertFalse(Advapi32.INSTANCE.LookupAccountSid(null, value,
                    null, cchName, null, cchReferencedDomainName, peUse));
            assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
            assertTrue(cchName.getValue() > 0);
            assertTrue(cchReferencedDomainName.getValue() > 0);
            char[] referencedDomainName = new char[cchReferencedDomainName.getValue()];
            char[] name = new char[cchName.getValue()];
            assertTrue(Advapi32.INSTANCE.LookupAccountSid(null, value,
                    name, cchName, referencedDomainName, cchReferencedDomainName, peUse));
            assertEquals(5, peUse.getPointer().getInt(0)); // SidTypeWellKnownGroup
            String nameString = Native.toString(name);
            String referencedDomainNameString = Native.toString(referencedDomainName);
            assertTrue(nameString.length() > 0);
            if (AbstractWin32TestSupport.isEnglishLocale) {
                assertEquals("Everyone", nameString);
            }
            assertTrue(referencedDomainNameString.length() == 0);
        } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
        }
    }

    public void testConvertSid() {
        String sidString = EVERYONE;
        PSIDByReference sid = new PSIDByReference();
        assertTrue("Failed to convert SID string", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));

        PSID value = sid.getValue();
        try {
            PointerByReference convertedSidStringPtr = new PointerByReference();
            assertTrue("Failed to convert SID string", Advapi32.INSTANCE.ConvertSidToStringSid(value, convertedSidStringPtr));

            Pointer conv = convertedSidStringPtr.getValue();
            try {
                String convertedSidString = conv.getWideString(0);
                assertEquals("Mismatched SID string", convertedSidString, sidString);
            } finally {
                Kernel32Util.freeLocalMemory(conv);
            }
        } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
        }
    }

    public void testLogonUser() {
        HANDLEByReference phToken = new HANDLEByReference();
        assertFalse(Advapi32.INSTANCE.LogonUser("AccountDoesntExist", ".", "passwordIsInvalid",
                WinBase.LOGON32_LOGON_NETWORK, WinBase.LOGON32_PROVIDER_DEFAULT, phToken));
        assertTrue(W32Errors.ERROR_SUCCESS != Kernel32.INSTANCE.GetLastError());
    }

    public void testOpenThreadTokenNoToken() {
        HANDLEByReference phToken = new HANDLEByReference();
        HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
        assertNotNull(threadHandle);
        assertFalse(Advapi32.INSTANCE.OpenThreadToken(threadHandle,
                WinNT.TOKEN_READ, false, phToken));
        assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
    }

    public void testOpenProcessToken() {
        HANDLEByReference phToken = new HANDLEByReference();
        try {
            HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
            assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                    WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
        } finally {
            Kernel32Util.closeHandleRef(phToken);
        }
    }

    public void testOpenThreadOrProcessToken() {
        HANDLEByReference phToken = new HANDLEByReference();
        try {
            HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
            if (!Advapi32.INSTANCE.OpenThreadToken(threadHandle,
                    WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, true, phToken)) {
                assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
                HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
                assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                        WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
            }
        } finally {
            Kernel32Util.closeHandleRef(phToken);
        }
    }

    public void testSetThreadTokenCurrentThread() {
        HANDLEByReference phToken = new HANDLEByReference();
        HANDLEByReference phTokenDup = new HANDLEByReference();
        try {
            HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
            // See if thread has a token. If not, must duplicate process token and set thread token using that.
            if (!Advapi32.INSTANCE.OpenThreadToken(threadHandle,
                    WinNT.TOKEN_IMPERSONATE | WinNT.TOKEN_QUERY, false, phToken)) {
                assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
                HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
                assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle, WinNT.TOKEN_DUPLICATE, phToken));
                assertTrue(Advapi32.INSTANCE.DuplicateTokenEx(phToken.getValue(),
                        WinNT.TOKEN_IMPERSONATE,
                        null,
                        WinNT.SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation,
                        WinNT.TOKEN_TYPE.TokenImpersonation,
                        phTokenDup));
                // Null sets on current thread
                assertTrue(Advapi32.INSTANCE.SetThreadToken(null, phTokenDup.getValue()));
            } else {
                //Null sets on current thread
                assertTrue(Advapi32.INSTANCE.SetThreadToken(null, phToken.getValue()));
            }
            // Revert and cleanup
            assertTrue(Advapi32.INSTANCE.SetThreadToken(null, null));
        } finally {
            Kernel32Util.closeHandleRefs(phToken, phTokenDup);
        }
    }

    public void testSetThreadTokenThisThread() {
        HANDLEByReference phToken = new HANDLEByReference();
        HANDLEByReference phTokenDup = new HANDLEByReference();
        try {
            HANDLEByReference pthreadHandle = new HANDLEByReference();
            pthreadHandle.setValue(Kernel32.INSTANCE.GetCurrentThread());
            // See if thread has a token. If not, must duplicate process token and set thread token using that.
            if (!Advapi32.INSTANCE.OpenThreadToken(pthreadHandle.getValue(),
                    WinNT.TOKEN_IMPERSONATE | WinNT.TOKEN_QUERY, false, phToken)) {
                assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
                HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
                assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                        WinNT.TOKEN_DUPLICATE, phToken));
                assertTrue(Advapi32.INSTANCE.DuplicateTokenEx(phToken.getValue(),
                        WinNT.TOKEN_IMPERSONATE,
                        null,
                        WinNT.SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation,
                        WinNT.TOKEN_TYPE.TokenImpersonation,
                        phTokenDup));
                // Use HANDLEByReference on this thread to test, should be good enough for API compatibility.
                assertTrue(Advapi32.INSTANCE.SetThreadToken(pthreadHandle, phTokenDup.getValue()));
            } else {
                // Use HANDLEByReference on this thread to test, should be good enough for API compatibility.
                assertTrue(Advapi32.INSTANCE.SetThreadToken(pthreadHandle, phToken.getValue()));
            }
            // Revert and cleanup
            assertTrue(Advapi32.INSTANCE.SetThreadToken(null, null));
        } finally {
            Kernel32Util.closeHandleRefs(phToken, phTokenDup);
        }
    }

    public void testDuplicateToken() {
        HANDLEByReference phToken = new HANDLEByReference();
        HANDLEByReference phTokenDup = new HANDLEByReference();
        HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
        try {
            assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                    WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
            assertTrue(Advapi32.INSTANCE.DuplicateToken(phToken.getValue(),
                    WinNT.SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation, phTokenDup));
        } finally {
            Kernel32Util.closeHandleRefs(phTokenDup, phToken);
        }
    }

    public void testDuplicateTokenEx() {
        HANDLEByReference hExistingToken = new HANDLEByReference();
        HANDLEByReference phNewToken = new HANDLEByReference();
        try {
            HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
            assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                    WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, hExistingToken));
            assertTrue(Advapi32.INSTANCE.DuplicateTokenEx(hExistingToken.getValue(),
                    WinNT.GENERIC_READ, null, SECURITY_IMPERSONATION_LEVEL.SecurityAnonymous,
                    TOKEN_TYPE.TokenPrimary, phNewToken));
        } finally {
            Kernel32Util.closeHandleRefs(phNewToken, hExistingToken);
        }
    }

    public void testGetTokenOwnerInformation() {
        HANDLEByReference phToken = new HANDLEByReference();
        try {
            HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
            assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                    WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
            IntByReference tokenInformationLength = new IntByReference();
            assertFalse(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(),
                    WinNT.TOKEN_INFORMATION_CLASS.TokenOwner, null, 0, tokenInformationLength));
            assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
            WinNT.TOKEN_OWNER owner = new WinNT.TOKEN_OWNER(tokenInformationLength.getValue());
            assertTrue(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(),
                    WinNT.TOKEN_INFORMATION_CLASS.TokenOwner, owner,
                    tokenInformationLength.getValue(), tokenInformationLength));
            assertTrue(tokenInformationLength.getValue() > 0);
            assertTrue(Advapi32.INSTANCE.IsValidSid(owner.Owner));
            int sidLength = Advapi32.INSTANCE.GetLengthSid(owner.Owner);
            assertTrue(sidLength < tokenInformationLength.getValue());
            assertTrue(sidLength > 0);
            // System.out.println(Advapi32Util.convertSidToStringSid(owner.Owner));
        } finally {
            Kernel32Util.closeHandleRef(phToken);
        }
    }

    public void testGetTokenUserInformation() {
        HANDLEByReference phToken = new HANDLEByReference();
        try {
            HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
            assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                    WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
            IntByReference tokenInformationLength = new IntByReference();
            assertFalse(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(),
                    WinNT.TOKEN_INFORMATION_CLASS.TokenUser, null, 0, tokenInformationLength));
            assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
            WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(tokenInformationLength.getValue());
            assertTrue(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(),
                    WinNT.TOKEN_INFORMATION_CLASS.TokenUser, user,
                    tokenInformationLength.getValue(), tokenInformationLength));
            assertTrue(tokenInformationLength.getValue() > 0);
            assertTrue(Advapi32.INSTANCE.IsValidSid(user.User.Sid));
            int sidLength = Advapi32.INSTANCE.GetLengthSid(user.User.Sid);
            assertTrue(sidLength > 0);
            assertTrue(sidLength < tokenInformationLength.getValue());
            // System.out.println(Advapi32Util.convertSidToStringSid(user.User.Sid));
        } finally {
            Kernel32Util.closeHandleRef(phToken);
        }
    }

    public void testGetTokenGroupsInformation() {
        HANDLEByReference phToken = new HANDLEByReference();
        try {
            HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
            assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                    WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
            IntByReference tokenInformationLength = new IntByReference();
            assertFalse(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(),
                    WinNT.TOKEN_INFORMATION_CLASS.TokenGroups, null, 0, tokenInformationLength));
            assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
            WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(tokenInformationLength.getValue());
            assertTrue(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(),
                    WinNT.TOKEN_INFORMATION_CLASS.TokenGroups, groups,
                    tokenInformationLength.getValue(), tokenInformationLength));
            assertTrue(tokenInformationLength.getValue() > 0);
            assertTrue(groups.GroupCount > 0);
            for (SID_AND_ATTRIBUTES sidAndAttribute : groups.getGroups()) {
                assertTrue(Advapi32.INSTANCE.IsValidSid(sidAndAttribute.Sid));
                // System.out.println(Advapi32Util.convertSidToStringSid(sidAndAttribute.Sid));
            }
        } finally {
            Kernel32Util.closeHandleRef(phToken);
        }
    }

    public void testImpersonateLoggedOnUser() {
        USER_INFO_1 userInfo = new USER_INFO_1();
        userInfo.usri1_name = "JNAAdvapi32TestImp";
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
                assertTrue(Advapi32.INSTANCE.ImpersonateLoggedOnUser(phUser.getValue()));
                assertTrue(Advapi32.INSTANCE.RevertToSelf());
            } finally {
                HANDLE hUser = phUser.getValue();
                if (!WinBase.INVALID_HANDLE_VALUE.equals(hUser)) {
                    Kernel32Util.closeHandle(hUser);
                }
            }
        } finally {
            assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
                    null, userInfo.usri1_name));
        }
    }

    public void testRegOpenKeyEx() {
        HKEYByReference phKey = new HKEYByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft", 0, WinNT.KEY_READ, phKey));
        assertTrue(WinBase.INVALID_HANDLE_VALUE != phKey.getValue());
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }

    public void testRegConnectRegistry() {
        HKEYByReference phkResult = new HKEYByReference();
        int connectResult = Advapi32.INSTANCE.RegConnectRegistry(
                "\\\\localhost", WinReg.HKEY_LOCAL_MACHINE, phkResult);

        if (connectResult == W32Errors.ERROR_BAD_NETPATH) {
            System.err.println("Failed to connect to registry with RegConnectRegistry, remote registry service is probably disabled");
            return;
        }

        if (connectResult == W32Errors.ERROR_ACCESS_DENIED) {
            System.err.println("Access was denied when connecting to registry with RegConnectRegistry");
            return;
        }

        assertEquals(W32Errors.ERROR_SUCCESS, connectResult);
        assertTrue(WinBase.INVALID_HANDLE_VALUE != phkResult.getValue());
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phkResult.getValue()));
    }

    public void testRegQueryValueEx() {
        HKEYByReference phKey = new HKEYByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", 0, WinNT.KEY_READ, phKey));
        IntByReference lpcbData = new IntByReference();
        IntByReference lpType = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
                phKey.getValue(), "User Agent", 0, lpType, (char[]) null, lpcbData));
        assertEquals(WinNT.REG_SZ, lpType.getValue());
        assertTrue(lpcbData.getValue() > 0);
        char[] buffer = new char[lpcbData.getValue()];
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
                phKey.getValue(), "User Agent", 0, lpType, buffer, lpcbData));
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }

    public void testRegDeleteValue() {
        assertEquals(W32Errors.ERROR_FILE_NOT_FOUND, Advapi32.INSTANCE.RegDeleteValue(
                WinReg.HKEY_CURRENT_USER, "JNAAdvapi32TestDoesntExist"));
    }

    public void testRegSetValueEx_REG_SZ() {
        HKEYByReference phKey = new HKEYByReference();
        // create parent key
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
        HKEYByReference phkTest = new HKEYByReference();
        IntByReference lpdwDisposition = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCreateKeyEx(
                phKey.getValue(), "JNAAdvapi32Test", 0, null, 0, WinNT.KEY_ALL_ACCESS,
                null, phkTest, lpdwDisposition));
        // write a REG_SZ value
        char[] lpData = Native.toCharArray("Test");
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegSetValueEx(
                phkTest.getValue(), "REG_SZ", 0, WinNT.REG_SZ, lpData, lpData.length * 2));
        // re-read the REG_SZ value
        IntByReference lpType = new IntByReference();
        IntByReference lpcbData = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
                phkTest.getValue(), "REG_SZ", 0, lpType, (char[]) null, lpcbData));
        assertEquals(WinNT.REG_SZ, lpType.getValue());
        assertTrue(lpcbData.getValue() > 0);
        char[] buffer = new char[lpcbData.getValue()];
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
                phkTest.getValue(), "REG_SZ", 0, lpType, buffer, lpcbData));
        assertEquals("Test", Native.toString(buffer));
        // delete the test key
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(
                phkTest.getValue()));
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegDeleteKey(
                phKey.getValue(), "JNAAdvapi32Test"));
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }

    public void testRegSetValueEx_DWORD() {
        HKEYByReference phKey = new HKEYByReference();
        // create parent key
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
        HKEYByReference phkTest = new HKEYByReference();
        IntByReference lpdwDisposition = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCreateKeyEx(
                phKey.getValue(), "JNAAdvapi32Test", 0, null, 0, WinNT.KEY_ALL_ACCESS,
                null, phkTest, lpdwDisposition));
        // write a REG_DWORD value
        int value = 42145;
        byte[] data = new byte[4];
        data[0] = (byte) (value & 0xff);
        data[1] = (byte) ((value >> 8) & 0xff);
        data[2] = (byte) ((value >> 16) & 0xff);
        data[3] = (byte) ((value >> 24) & 0xff);
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegSetValueEx(
                phkTest.getValue(), "DWORD", 0, WinNT.REG_DWORD, data, 4));
        // re-read the REG_DWORD value
        IntByReference lpType = new IntByReference();
        IntByReference lpcbData = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
                phkTest.getValue(), "DWORD", 0, lpType, (char[]) null, lpcbData));
        assertEquals(WinNT.REG_DWORD, lpType.getValue());
        assertEquals(4, lpcbData.getValue());
        IntByReference valueRead = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
                phkTest.getValue(), "DWORD", 0, lpType, valueRead, lpcbData));
        assertEquals(value, valueRead.getValue());
        // delete the test key
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(
                phkTest.getValue()));
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegDeleteKey(
                phKey.getValue(), "JNAAdvapi32Test"));
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }

    public void testRegCreateKeyEx() {
        HKEYByReference phKey = new HKEYByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
        HKEYByReference phkResult = new HKEYByReference();
        IntByReference lpdwDisposition = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCreateKeyEx(
                phKey.getValue(), "JNAAdvapi32Test", 0, null, 0, WinNT.KEY_ALL_ACCESS,
                null, phkResult, lpdwDisposition));
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phkResult.getValue()));
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegDeleteKey(
                phKey.getValue(), "JNAAdvapi32Test"));
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }

    public void testRegDeleteKey() {
        assertEquals(W32Errors.ERROR_FILE_NOT_FOUND, Advapi32.INSTANCE.RegDeleteKey(
                WinReg.HKEY_CURRENT_USER, "JNAAdvapi32TestDoesntExist"));
    }

    public void testRegEnumKeyEx() {
        HKEYByReference phKey = new HKEYByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings",
                0, WinNT.KEY_READ, phKey));
        IntByReference lpcSubKeys = new IntByReference();
        IntByReference lpcMaxSubKeyLen = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryInfoKey(
                phKey.getValue(), null, null, null, lpcSubKeys, lpcMaxSubKeyLen, null, null,
                null, null, null, null));
        char[] name = new char[lpcMaxSubKeyLen.getValue() + 1];
        for (int i = 0; i < lpcSubKeys.getValue(); i++) {
            IntByReference lpcchValueName = new IntByReference(lpcMaxSubKeyLen.getValue() + 1);
            assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegEnumKeyEx(
                    phKey.getValue(), i, name, lpcchValueName, null, null, null, null));
            assertEquals(Native.toString(name).length(), lpcchValueName.getValue());
        }
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }

    public void testRegEnumValue() {
        HKEYByReference phKey = new HKEYByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
                WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings",
                0, WinNT.KEY_READ, phKey));
        IntByReference lpcValues = new IntByReference();
        IntByReference lpcMaxValueNameLen = new IntByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryInfoKey(
                phKey.getValue(), null, null, null, null, null, null, lpcValues,
                lpcMaxValueNameLen, null, null, null));
        char[] name = new char[lpcMaxValueNameLen.getValue() + 1];
        for (int i = 0; i < lpcValues.getValue(); i++) {
            IntByReference lpcchValueName = new IntByReference(lpcMaxValueNameLen.getValue() + 1);
            IntByReference lpType = new IntByReference();
            assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegEnumValue(
                    phKey.getValue(), i, name, lpcchValueName, null,
                    lpType, (Pointer) null, null));
            assertEquals(Native.toString(name).length(), lpcchValueName.getValue());
        }
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }

    public void testRegQueryInfoKey() {
        IntByReference lpcClass = new IntByReference();
        IntByReference lpcSubKeys = new IntByReference();
        IntByReference lpcMaxSubKeyLen = new IntByReference();
        IntByReference lpcValues = new IntByReference();
        IntByReference lpcMaxClassLen = new IntByReference();
        IntByReference lpcMaxValueNameLen = new IntByReference();
        IntByReference lpcMaxValueLen = new IntByReference();
        IntByReference lpcbSecurityDescriptor = new IntByReference();
        FILETIME lpftLastWriteTime = new FILETIME();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryInfoKey(
                WinReg.HKEY_LOCAL_MACHINE, null, lpcClass, null,
                lpcSubKeys, lpcMaxSubKeyLen, lpcMaxClassLen, lpcValues,
                lpcMaxValueNameLen, lpcMaxValueLen, lpcbSecurityDescriptor,
                lpftLastWriteTime));
        assertTrue(lpcSubKeys.getValue() > 0);
    }

    public void testIsWellKnownSid() {
        String sidString = EVERYONE;
        PSIDByReference sid = new PSIDByReference();
        assertTrue("sid conversion failed", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));

        PSID value = sid.getValue();
        try {
            assertTrue("Not a world sid", Advapi32.INSTANCE.IsWellKnownSid(value, WELL_KNOWN_SID_TYPE.WinWorldSid));
            assertFalse("Unexpected admin sid", Advapi32.INSTANCE.IsWellKnownSid(value, WELL_KNOWN_SID_TYPE.WinAccountAdministratorSid));
        } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
        }
    }

    public void testCreateWellKnownSid() {
        PSID pSid = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinWorldSid, null, pSid, cbSid));
        assertTrue("Not recognized as well-known SID",
                Advapi32.INSTANCE.IsWellKnownSid(pSid, WELL_KNOWN_SID_TYPE.WinWorldSid));
        assertTrue("Invalid SID size", cbSid.getValue() <= WinNT.SECURITY_MAX_SID_SIZE);
        PointerByReference convertedSidStringPtr = new PointerByReference();
        assertTrue("Failed to convert SID", Advapi32.INSTANCE.ConvertSidToStringSid(pSid, convertedSidStringPtr));

        Pointer conv = convertedSidStringPtr.getValue();
        try {
            String convertedSidString = conv.getWideString(0);
            assertEquals("Mismatched SID string", EVERYONE, convertedSidString);
        } finally {
            Kernel32Util.freeLocalMemory(conv);
        }
    }

    public void testInitializeSecurityDescriptor() {
        SECURITY_DESCRIPTOR sd = new SECURITY_DESCRIPTOR(64 * 1024);
        assertTrue(Advapi32.INSTANCE.InitializeSecurityDescriptor(sd, WinNT.SECURITY_DESCRIPTOR_REVISION));
    }

    public void testSetGetSecurityDescriptorControl() {
        SECURITY_DESCRIPTOR sd = new SECURITY_DESCRIPTOR(64 * 1024);
        assertTrue(Advapi32.INSTANCE.InitializeSecurityDescriptor(sd, WinNT.SECURITY_DESCRIPTOR_REVISION));
        assertTrue(Advapi32.INSTANCE.SetSecurityDescriptorControl(sd, (short)WinNT.SE_DACL_PROTECTED, (short)WinNT.SE_DACL_PROTECTED));
        ShortByReference pControl = new ShortByReference();
        IntByReference lpdwRevision = new IntByReference();
        assertTrue(Advapi32.INSTANCE.GetSecurityDescriptorControl(sd, pControl, lpdwRevision));
        assertTrue(pControl.getValue() == WinNT.SE_DACL_PROTECTED);
        assertTrue(lpdwRevision.getValue() == WinNT.SECURITY_DESCRIPTOR_REVISION);
    }

    public void testSetGetSecurityDescriptorOwner() {
        SECURITY_DESCRIPTOR sd = new SECURITY_DESCRIPTOR(64 * 1024);
        assertTrue(Advapi32.INSTANCE.InitializeSecurityDescriptor(sd, WinNT.SECURITY_DESCRIPTOR_REVISION));

        PSID pSidPut = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid, null, pSidPut, cbSid));

        assertTrue(Advapi32.INSTANCE.SetSecurityDescriptorOwner(sd, pSidPut, true));

        BOOLByReference lpbOwnerDefaulted = new BOOLByReference();
        PSIDByReference prSd = new PSIDByReference();
        assertTrue(Advapi32.INSTANCE.GetSecurityDescriptorOwner(sd, prSd, lpbOwnerDefaulted));

        PSID pSidGet = prSd.getValue();
        assertTrue(Advapi32.INSTANCE.EqualSid(pSidPut, pSidGet));
    }

    public void testSetGetSecurityDescriptorGroup() {
        SECURITY_DESCRIPTOR sd = new SECURITY_DESCRIPTOR(64 * 1024);
        assertTrue(Advapi32.INSTANCE.InitializeSecurityDescriptor(sd, WinNT.SECURITY_DESCRIPTOR_REVISION));

        PSID pSidPut = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid, null, pSidPut, cbSid));

        assertTrue(Advapi32.INSTANCE.SetSecurityDescriptorGroup(sd, pSidPut, true));

        BOOLByReference lpbOwnerDefaulted = new BOOLByReference();
        PSIDByReference prSd = new PSIDByReference();
        assertTrue(Advapi32.INSTANCE.GetSecurityDescriptorGroup(sd, prSd, lpbOwnerDefaulted));

        PSID pSidGet = prSd.getValue();
        assertTrue(Advapi32.INSTANCE.EqualSid(pSidPut, pSidGet));
    }

    public void testSetGetSecurityDescriptorDacl() throws IOException {
        SECURITY_DESCRIPTOR sd = new SECURITY_DESCRIPTOR(64 * 1024);
        assertTrue(Advapi32.INSTANCE.InitializeSecurityDescriptor(sd, WinNT.SECURITY_DESCRIPTOR_REVISION));

        PSID pSid = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid, null, pSid, cbSid));

        int sidLength = Advapi32.INSTANCE.GetLengthSid(pSid);
        int cbAcl = Native.getNativeSize(ACL.class, null);
        cbAcl += Native.getNativeSize(ACCESS_ALLOWED_ACE.class, null);
        cbAcl += (sidLength - DWORD.SIZE);
        cbAcl = Advapi32Util.alignOnDWORD(cbAcl);
        ACL pAcl = new ACL(cbAcl);
        assertTrue(Advapi32.INSTANCE.InitializeAcl(pAcl, cbAcl, WinNT.ACL_REVISION));
        assertTrue(Advapi32.INSTANCE.AddAccessAllowedAce(pAcl, WinNT.ACL_REVISION, WinNT.STANDARD_RIGHTS_ALL, pSid));
        assertTrue(Advapi32.INSTANCE.SetSecurityDescriptorDacl(sd, true, pAcl, false));
        BOOLByReference lpbDaclPresent  = new BOOLByReference();
        BOOLByReference lpbDaclDefaulted  = new BOOLByReference();
        PACLByReference pDacl = new PACLByReference();
        assertTrue(Advapi32.INSTANCE.GetSecurityDescriptorDacl(sd, lpbDaclPresent, pDacl, lpbDaclDefaulted));
        ACL pAclGet = pDacl.getValue();
        assertEquals(new BOOL(true), lpbDaclPresent.getValue());
        assertEquals(new BOOL(false), lpbDaclDefaulted.getValue());
        assertEquals(1, pAclGet.AceCount);
        assertEquals(WinNT.ACL_REVISION, pAclGet.AclRevision);
    }

    public void testInitializeAcl() throws IOException {
        PSID pSid = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid, null, pSid, cbSid));

        int sidLength = Advapi32.INSTANCE.GetLengthSid(pSid);
        int cbAcl = Native.getNativeSize(ACL.class, null);
        cbAcl += Native.getNativeSize(ACCESS_ALLOWED_ACE.class, null);
        cbAcl += (sidLength - DWORD.SIZE);
        cbAcl = Advapi32Util.alignOnDWORD(cbAcl);
        ACL pAcl = new ACL(cbAcl);
        assertTrue(Advapi32.INSTANCE.InitializeAcl(pAcl, cbAcl, WinNT.ACL_REVISION));
    }

    public void testGetAce() throws IOException {
        PSID pSid = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid, null, pSid, cbSid));

        int sidLength = Advapi32.INSTANCE.GetLengthSid(pSid);
        int cbAcl = Native.getNativeSize(ACL.class, null);
        cbAcl += Native.getNativeSize(ACCESS_ALLOWED_ACE.class, null);
        cbAcl += (sidLength - DWORD.SIZE);
        cbAcl = Advapi32Util.alignOnDWORD(cbAcl);
        ACL pAcl = new ACL(cbAcl);
        assertTrue(Advapi32.INSTANCE.InitializeAcl(pAcl, cbAcl, WinNT.ACL_REVISION));
        assertTrue(Advapi32.INSTANCE.AddAccessAllowedAce(pAcl, WinNT.ACL_REVISION, WinNT.STANDARD_RIGHTS_ALL, pSid));

        PointerByReference pAce = new PointerByReference(new Memory(16));
        assertTrue(Advapi32.INSTANCE.GetAce(pAcl, 0, pAce));
        ACCESS_ALLOWED_ACE pAceGet = new ACCESS_ALLOWED_ACE(pAce.getValue());
        assertTrue(pAceGet.Mask == WinNT.STANDARD_RIGHTS_ALL);
        assertTrue(Advapi32.INSTANCE.EqualSid(pAceGet.psid, pSid));
    }

    public void testAddAce() throws IOException {
        PSID pSid = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid, null, pSid, cbSid));

        int sidLength = Advapi32.INSTANCE.GetLengthSid(pSid);
        int cbAcl = Native.getNativeSize(ACL.class, null);
        cbAcl += Advapi32Util.getAceSize(sidLength);
        cbAcl = Advapi32Util.alignOnDWORD(cbAcl);
        ACL pAcl = new ACL(cbAcl);
        ACCESS_ALLOWED_ACE pace = new ACCESS_ALLOWED_ACE(WinNT.STANDARD_RIGHTS_ALL,
                WinNT.INHERITED_ACE,
                pSid);

        assertTrue(Advapi32.INSTANCE.InitializeAcl(pAcl, cbAcl, WinNT.ACL_REVISION));
        assertTrue(Advapi32.INSTANCE.AddAce(pAcl, WinNT.ACL_REVISION, WinNT.MAXDWORD, pace.getPointer(), pace.size()));

        PointerByReference pAce = new PointerByReference(new Memory(16));
        assertTrue(Advapi32.INSTANCE.GetAce(pAcl, 0, pAce));
        ACCESS_ALLOWED_ACE pAceGet = new ACCESS_ALLOWED_ACE(pAce.getValue());
        assertTrue(pAceGet.Mask == WinNT.STANDARD_RIGHTS_ALL);
        assertTrue(Advapi32.INSTANCE.EqualSid(pAceGet.psid, pSid));
    }

    public void testAddAccessAllowedAce() throws IOException {
        PSID pSid = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid, null, pSid, cbSid));

        int sidLength = Advapi32.INSTANCE.GetLengthSid(pSid);
        int cbAcl = Native.getNativeSize(ACL.class, null);
        cbAcl += Advapi32Util.getAceSize(sidLength);
        cbAcl = Advapi32Util.alignOnDWORD(cbAcl);
        ACL pAcl = new ACL(cbAcl);
        assertTrue(Advapi32.INSTANCE.InitializeAcl(pAcl, cbAcl, WinNT.ACL_REVISION));
        assertTrue(Advapi32.INSTANCE.AddAccessAllowedAce(pAcl, WinNT.ACL_REVISION, WinNT.STANDARD_RIGHTS_ALL, pSid));

        PointerByReference pAce = new PointerByReference(new Memory(16));
        assertTrue(Advapi32.INSTANCE.GetAce(pAcl, 0, pAce));
        ACCESS_ALLOWED_ACE pAceGet = new ACCESS_ALLOWED_ACE(pAce.getValue());
        assertTrue(pAceGet.Mask == WinNT.STANDARD_RIGHTS_ALL);
        assertTrue(Advapi32.INSTANCE.EqualSid(pAceGet.psid, pSid));
    }

    public void testAddAccessAllowedAceEx() throws IOException {
        PSID pSid = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
        IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
        assertTrue("Failed to create well-known SID",
                Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinBuiltinAdministratorsSid, null, pSid, cbSid));

        int sidLength = Advapi32.INSTANCE.GetLengthSid(pSid);
        int cbAcl = Native.getNativeSize(ACL.class, null);
        cbAcl += Advapi32Util.getAceSize(sidLength);
        cbAcl = Advapi32Util.alignOnDWORD(cbAcl);
        ACL pAcl = new ACL(cbAcl);
        assertTrue(Advapi32.INSTANCE.InitializeAcl(pAcl, cbAcl, WinNT.ACL_REVISION));
        assertTrue(Advapi32.INSTANCE.AddAccessAllowedAceEx(pAcl, WinNT.ACL_REVISION, WinNT.INHERIT_ONLY_ACE, WinNT.STANDARD_RIGHTS_ALL, pSid));

        PointerByReference pAce = new PointerByReference(new Memory(16));
        assertTrue(Advapi32.INSTANCE.GetAce(pAcl, 0, pAce));
        ACCESS_ALLOWED_ACE pAceGet = new ACCESS_ALLOWED_ACE(pAce.getValue());
        assertTrue(pAceGet.Mask == WinNT.STANDARD_RIGHTS_ALL);
        assertTrue(Advapi32.INSTANCE.EqualSid(pAceGet.psid, pSid));
    }

    public void testOpenEventLog() {
        HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
        assertNotNull(h);
        assertFalse(h.equals(WinBase.INVALID_HANDLE_VALUE));
        assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }

    public void testRegisterEventSource() {
        // the Security event log is reserved
        HANDLE h = Advapi32.INSTANCE.RegisterEventSource(null, "Security");
        assertNull(h);
        assertEquals(W32Errors.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());
    }

    public void testReportEvent() {
        String applicationEventLog = "SYSTEM\\CurrentControlSet\\Services\\EventLog\\Application";
        String jnaEventSource = "JNADevEventSource";
        String jnaEventSourceRegistryPath = applicationEventLog + "\\" + jnaEventSource;
        // ignore test if not able to create key (need to be administrator to do this).
        try {
            final boolean keyCreated = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, jnaEventSourceRegistryPath);
            if (!keyCreated) {
                return;
            }
        } catch (Win32Exception e) {
            return;
        }

        HANDLE h = Advapi32.INSTANCE.RegisterEventSource(null, jnaEventSource);
        IntByReference before = new IntByReference();
        assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, before));
        assertNotNull(h);
        String s[] = {"JNA", "Event"};
        Memory m = new Memory(4);
        m.setByte(0, (byte) 1);
        m.setByte(1, (byte) 2);
        m.setByte(2, (byte) 3);
        m.setByte(3, (byte) 4);
        int eventId = 123 + 0x40000000;
        assertTrue(Advapi32.INSTANCE.ReportEvent(h, WinNT.EVENTLOG_ERROR_TYPE, 0, eventId, null, 2, 4, s, m));
        IntByReference after = new IntByReference();
        assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, after));
        assertTrue(before.getValue() < after.getValue());
        assertFalse(h.equals(WinBase.INVALID_HANDLE_VALUE));
        assertTrue(Advapi32.INSTANCE.DeregisterEventSource(h));
        Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, jnaEventSourceRegistryPath);

        // Test the event record
        EventLogIterator iter = new EventLogIterator(null, "Application", WinNT.EVENTLOG_BACKWARDS_READ);
        while (iter.hasNext()) {
            EventLogRecord record = iter.next();
            if (record.getRecord().EventID.getLow().intValue() == 123 && record.getStrings().length > 1
                    && "JNA".equals(record.getStrings()[0]) && "Event".equals(record.getStrings()[1])) {
                // Test Status Code stripping top 16 bits
                assertEquals(123, record.getStatusCode());
                // Test InstanceId
                assertEquals(123 | 0x40000000, record.getInstanceId());
                // Test Event ID stripping top 2 bits from InstanceId
                assertEquals(123, record.getInstanceId() & 0x3FFFFFFF);
            }
        }
    }

    public void testGetNumberOfEventLogRecords() {
        HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
        assertFalse(h.equals(WinBase.INVALID_HANDLE_VALUE));
        IntByReference n = new IntByReference();
        assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, n));
        assertTrue(n.getValue() >= 0);
        assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }

    /*
    public void testClearEventLog() {
        HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
        assertFalse(h.equals(WinBase.INVALID_HANDLE_VALUE));
        IntByReference before = new IntByReference();
        assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, before));
        assertTrue(before.getValue() >= 0);
        assertTrue(Advapi32.INSTANCE.ClearEventLog(h, null));
        IntByReference after = new IntByReference();
        assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, after));
        assertTrue(after.getValue() < before.getValue() || before.getValue() == 0);
        assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }
    */

    public void testBackupEventLog() {
        HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
        assertNotNull(h);
        String backupFileName = Kernel32Util.getTempPath() + "\\JNADevEventLog.bak";
        File f = new File(backupFileName);
        if (f.exists()) {
            f.delete();
        }

        assertTrue(Advapi32.INSTANCE.BackupEventLog(h, backupFileName));
        HANDLE hBackup = Advapi32.INSTANCE.OpenBackupEventLog(null, backupFileName);
        assertNotNull(hBackup);

        IntByReference n = new IntByReference();
        assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(hBackup, n));
        assertTrue(n.getValue() >= 0);

        assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
        assertTrue(Advapi32.INSTANCE.CloseEventLog(hBackup));
    }

    public void testReadEventLog() {
        HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
        IntByReference pnBytesRead = new IntByReference();
        IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
        Memory buffer = new Memory(1);
        assertFalse(Advapi32.INSTANCE.ReadEventLog(h,
                WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_BACKWARDS_READ,
                0, buffer, (int) buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded));
        assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
        assertTrue(pnMinNumberOfBytesNeeded.getValue() > 0);
        assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }

    public void testReadEventLogEntries() {
        HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
        IntByReference pnBytesRead = new IntByReference();
        IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
        Memory buffer = new Memory(1024 * 64);
        // shorten test, avoid iterating through all events
        int maxReads = 3;
        int rc = 0;
        while(true) {
            if (maxReads-- <= 0)
                break;
            if (! Advapi32.INSTANCE.ReadEventLog(h,
                                                 WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_FORWARDS_READ,
                                                 0, buffer, (int) buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
                rc = Kernel32.INSTANCE.GetLastError();
                if (rc == W32Errors.ERROR_INSUFFICIENT_BUFFER) {
                    buffer = new Memory(pnMinNumberOfBytesNeeded.getValue());
                    rc = 0;
                    continue;
                }
                break;
            }
            int dwRead = pnBytesRead.getValue();
            Pointer pevlr = buffer;
            int maxRecords = 3;
            while (dwRead > 0 && maxRecords-- > 0) {
                EVENTLOGRECORD record = new EVENTLOGRECORD(pevlr);
                /*
                  System.out.println(record.RecordNumber.intValue()
                  + " Event ID: " + record.EventID.intValue()
                  + " Event Type: " + record.EventType.intValue()
                  + " Event Source: " + pevlr.getString(record.size(), true));
                */
                dwRead -= record.Length.intValue();
                pevlr = pevlr.share(record.Length.intValue());
            }
        }
        assertTrue(String.format("Unexpected error after reading event log: 0x%08X (%s)"
                   , rc, Kernel32Util.formatMessage(rc)),
                   rc == W32Errors.ERROR_HANDLE_EOF || rc == 0);
        assertTrue("Error closing event log",
                   Advapi32.INSTANCE.CloseEventLog(h));
    }

    public void testGetOldestEventLogRecord() {
        HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
        IntByReference oldestRecord = new IntByReference();
        assertTrue(Advapi32.INSTANCE.GetOldestEventLogRecord(h, oldestRecord));
        assertTrue(oldestRecord.getValue() >= 0);
        assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }

    public void testQueryServiceStatusEx() {

        SC_HANDLE scmHandle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
        assertNotNull(scmHandle);

        SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(scmHandle, "eventlog", Winsvc.SERVICE_QUERY_STATUS);
        assertNotNull(serviceHandle);

        IntByReference pcbBytesNeeded = new IntByReference();

        assertFalse(Advapi32.INSTANCE.QueryServiceStatusEx(serviceHandle, SC_STATUS_TYPE.SC_STATUS_PROCESS_INFO,
                null, 0, pcbBytesNeeded));
        assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());

        assertTrue(pcbBytesNeeded.getValue() > 0);

        SERVICE_STATUS_PROCESS status = new SERVICE_STATUS_PROCESS(pcbBytesNeeded.getValue());

        assertTrue(Advapi32.INSTANCE.QueryServiceStatusEx(serviceHandle, SC_STATUS_TYPE.SC_STATUS_PROCESS_INFO,
                status, status.size(), pcbBytesNeeded));

        assertTrue(status.dwCurrentState == Winsvc.SERVICE_STOPPED ||
                status.dwCurrentState == Winsvc.SERVICE_RUNNING);

        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(serviceHandle));
        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(scmHandle));
    }


    public void testControlService() {
        SC_HANDLE scmHandle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
        assertNotNull(scmHandle);

        SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(scmHandle, "eventlog", Winsvc.SERVICE_QUERY_CONFIG);
        assertNotNull(serviceHandle);

        Winsvc.SERVICE_STATUS serverStatus = new Winsvc.SERVICE_STATUS();

        assertNotNull(serviceHandle);
        assertFalse(Advapi32.INSTANCE.ControlService(serviceHandle, Winsvc.SERVICE_CONTROL_STOP, serverStatus));
        assertEquals(W32Errors.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());

        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(serviceHandle));
        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(scmHandle));
    }

    public void testStartService() {
        SC_HANDLE scmHandle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
        assertNotNull(scmHandle);

        SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(scmHandle, "eventlog", Winsvc.SERVICE_QUERY_CONFIG);
        assertNotNull(serviceHandle);

        assertFalse(Advapi32.INSTANCE.StartService(serviceHandle, 0, null));
        assertEquals(W32Errors.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());

        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(serviceHandle));
        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(scmHandle));
    }

    public void testOpenService() {
        assertNull(Advapi32.INSTANCE.OpenService(null, "eventlog", Winsvc.SERVICE_QUERY_CONFIG ));
        assertEquals(W32Errors.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());

        SC_HANDLE scmHandle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
        assertNotNull(scmHandle);

        SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(scmHandle, "eventlog", Winsvc.SERVICE_QUERY_CONFIG );
        assertNotNull(serviceHandle);
        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(serviceHandle));

        assertNull(Advapi32.INSTANCE.OpenService(scmHandle, "slashesArentValidChars/", Winsvc.SERVICE_QUERY_CONFIG ));
        assertEquals(W32Errors.ERROR_INVALID_NAME, Kernel32.INSTANCE.GetLastError());

        assertNull(Advapi32.INSTANCE.OpenService(scmHandle, "serviceDoesNotExist", Winsvc.SERVICE_QUERY_CONFIG ));
        assertEquals(W32Errors.ERROR_SERVICE_DOES_NOT_EXIST, Kernel32.INSTANCE.GetLastError());

        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(scmHandle));
    }

    public void testOpenSCManager() {
        SC_HANDLE handle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
        assertNotNull(handle);
        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(handle));

        assertNull(Advapi32.INSTANCE.OpenSCManager("invalidMachineName", null, Winsvc.SC_MANAGER_CONNECT));
        int err = Kernel32.INSTANCE.GetLastError();
        assertTrue("Unexpected error in OpenSCManager: " + err,
                   err == W32Errors.RPC_S_SERVER_UNAVAILABLE
                   || err == W32Errors.RPC_S_INVALID_NET_ADDR);

        assertNull(Advapi32.INSTANCE.OpenSCManager(null, "invalidDatabase", Winsvc.SC_MANAGER_CONNECT));
        assertEquals(W32Errors.ERROR_INVALID_NAME, Kernel32.INSTANCE.GetLastError());
    }

    public void testCloseServiceHandle() throws Exception {
        SC_HANDLE handle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
        assertNotNull(handle);
        assertTrue(Advapi32.INSTANCE.CloseServiceHandle(handle));

        assertFalse(Advapi32.INSTANCE.CloseServiceHandle(null));
        assertEquals(W32Errors.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
    }

    public void testCreateProcessAsUser() {
        HANDLEByReference hToken = new HANDLEByReference();
        HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
        assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
                WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, hToken));
        try {
            assertFalse(Advapi32.INSTANCE.CreateProcessAsUser(hToken.getValue(), null, "InvalidCmdLine.jna",
                    null, null, false, 0, null, null, new WinBase.STARTUPINFO(),
                    new WinBase.PROCESS_INFORMATION()));
            assertEquals(W32Errors.ERROR_FILE_NOT_FOUND, Kernel32.INSTANCE.GetLastError());
        } finally {
            Kernel32Util.closeHandleRef(hToken);
        }
    }

    /**
     * Tests both {@link Advapi32#LookupPrivilegeValue} and {@link Advapi32#LookupPrivilegeName}
     */
    public void testLookupPrivilegeValueAndLookupPrivilegeName() {
        WinNT.LUID luid = new WinNT.LUID();

        assertFalse(Advapi32.INSTANCE.LookupPrivilegeValue(null, "InvalidName", luid));
        assertEquals(Kernel32.INSTANCE.GetLastError(), W32Errors.ERROR_NO_SUCH_PRIVILEGE);

        assertTrue(Advapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_BACKUP_NAME, luid));
        assertTrue(luid.LowPart > 0 || luid.HighPart > 0);

        char[] lpName = new char[256];
        IntByReference cchName = new IntByReference(lpName.length);
        assertTrue(Advapi32.INSTANCE.LookupPrivilegeName(null, luid, lpName, cchName));
        assertEquals(WinNT.SE_BACKUP_NAME.length(), cchName.getValue());
        assertEquals(WinNT.SE_BACKUP_NAME, Native.toString(lpName));
    }

    public void testAdjustTokenPrivileges() {
        HANDLEByReference hToken = new HANDLEByReference();
        assertTrue(Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(),
                WinNT.TOKEN_ADJUST_PRIVILEGES | WinNT.TOKEN_QUERY, hToken));

        try {
            // Find an already enabled privilege
            TOKEN_PRIVILEGES tp = new TOKEN_PRIVILEGES(1024);
            IntByReference returnLength = new IntByReference();
            assertTrue(Advapi32.INSTANCE.GetTokenInformation(hToken.getValue(),    WinNT.TOKEN_INFORMATION_CLASS.TokenPrivileges,
                    tp, tp.size(), returnLength));
            assertTrue(tp.PrivilegeCount.intValue() > 0);

            WinNT.LUID luid = null;
            for (int i=0; i<tp.PrivilegeCount.intValue(); i++) {
                if ((tp.Privileges[i].Attributes.intValue() & WinNT.SE_PRIVILEGE_ENABLED) > 0) {
                    luid = tp.Privileges[i].Luid;
                }
            }
            assertTrue(luid != null);

            // Re-enable it. That should succeed.
            tp = new WinNT.TOKEN_PRIVILEGES(1);
            tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));

            assertTrue(Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tp, 0, null, null));
        } finally {
            Kernel32Util.closeHandleRef(hToken);
        }
    }

    public void testImpersonateSelf() {
        assertTrue(Advapi32.INSTANCE.ImpersonateSelf(WinNT.SECURITY_IMPERSONATION_LEVEL.SecurityAnonymous));
        assertTrue(Advapi32.INSTANCE.RevertToSelf());
    }

    public void testGetSetFileSecurityNoSACL() throws Exception {
        int infoType = OWNER_SECURITY_INFORMATION
                       | GROUP_SECURITY_INFORMATION
                       | DACL_SECURITY_INFORMATION;

        int memSize = 64 * 1024;
        Memory memorySecurity = new Memory(memSize);
        IntByReference sizeNeeded = new IntByReference(0);
        // create a temp file
        File file = createTempFile();
        String filePath = file.getAbsolutePath();

        try {

            assertEquals("GetFileSecurity(" + filePath + ")", true,
                         Advapi32.INSTANCE.GetFileSecurity(
                                 filePath,
                                 infoType,
                                 memorySecurity,
                                 memSize,
                                 sizeNeeded));
            assertEquals("SetFileSecurity(" + filePath + ")", true,
                         Advapi32.INSTANCE.SetFileSecurity(
                                 filePath,
                                 infoType,
                                 memorySecurity));
        } finally {
            file.delete();
        }
    }

    public void testGetSetSecurityInfoNoSACL() throws Exception {
        int infoType = OWNER_SECURITY_INFORMATION
                       | GROUP_SECURITY_INFORMATION
                       | DACL_SECURITY_INFORMATION;

        PointerByReference ppsidOwner = new PointerByReference();
        PointerByReference ppsidGroup = new PointerByReference();
        PointerByReference ppDacl = new PointerByReference();
        PointerByReference ppSecurityDescriptor = new PointerByReference();
        // create a temp file
        File file = createTempFile();
        String filePath = file.getAbsolutePath();

        HANDLE hFile = Kernel32.INSTANCE.CreateFile(
                filePath,
                WinNT.GENERIC_ALL | WinNT.WRITE_OWNER | WinNT.WRITE_DAC,
                WinNT.FILE_SHARE_READ,
                new WinBase.SECURITY_ATTRIBUTES(),
                WinNT.OPEN_EXISTING,
                WinNT.FILE_ATTRIBUTE_NORMAL,
                null);
        assertFalse("Failed to create file handle: " + filePath, WinBase.INVALID_HANDLE_VALUE.equals(hFile));

        try {
            try {

                assertEquals("GetSecurityInfo(" + filePath + ")", 0,
                        Advapi32.INSTANCE.GetSecurityInfo(
                                hFile,
                                AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                infoType,
                                ppsidOwner,
                                ppsidGroup,
                                ppDacl,
                                null,
                                ppSecurityDescriptor));

                assertEquals("SetSecurityInfo(" + filePath + ")", 0,
                        Advapi32.INSTANCE.SetSecurityInfo(
                                hFile,
                                AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                infoType,
                                ppsidOwner.getValue(),
                                ppsidGroup.getValue(),
                                ppDacl.getValue(),
                                null));
            } finally {
                Kernel32.INSTANCE.CloseHandle(hFile);
                file.delete();
            }
        } finally {
            Kernel32Util.freeLocalMemory(ppSecurityDescriptor.getValue());
        }
    }

    public void testGetSetSecurityInfoForFileWithSACL() throws Exception {
        boolean impersontating = false;

        HANDLEByReference phToken = new HANDLEByReference();
        HANDLEByReference phTokenDuplicate = new HANDLEByReference();
        try {
            // open thread or process token, elevate
            if (!Advapi32.INSTANCE.OpenThreadToken(
                 Kernel32.INSTANCE.GetCurrentThread(),
                 TOKEN_ADJUST_PRIVILEGES,
                 false,
                 phToken))
            {
                assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
                // OpenThreadToken may fail with W32Errors.ERROR_NO_TOKEN if current thread is anonymous.  When this happens,
                // we need to open the process token to duplicate it, then set our thread token.
                assertTrue(Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), TOKEN_DUPLICATE, phToken));
                // Process token opened, now duplicate
                assertTrue(Advapi32.INSTANCE.DuplicateTokenEx(
                            phToken.getValue(),
                            TOKEN_ADJUST_PRIVILEGES | TOKEN_IMPERSONATE,
                            null,
                            SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation,
                            TOKEN_TYPE.TokenImpersonation,
                            phTokenDuplicate));
                // And set thread token.
                assertTrue(Advapi32.INSTANCE.SetThreadToken(null, phTokenDuplicate.getValue()));
                impersontating = true;
            }

            // Which token to adjust depends on whether we had to impersonate or not.
            HANDLE tokenAdjust = impersontating ? phTokenDuplicate.getValue() : phToken.getValue();

            WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(1);
            WinNT.LUID pLuid = new WinNT.LUID();

            assertTrue(Advapi32.INSTANCE.LookupPrivilegeValue(null, SE_SECURITY_NAME, pLuid));
            tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(pLuid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
            assertTrue(Advapi32.INSTANCE.AdjustTokenPrivileges(tokenAdjust, false, tp, 0, null, null));

            assertTrue(Advapi32.INSTANCE.LookupPrivilegeValue(null, SE_RESTORE_NAME, pLuid));
            tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(pLuid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
            assertTrue(Advapi32.INSTANCE.AdjustTokenPrivileges(tokenAdjust, false, tp, 0, null, null));

            // create a temp file
            File file = createTempFile();
            int infoType = OWNER_SECURITY_INFORMATION
                           | GROUP_SECURITY_INFORMATION
                           | DACL_SECURITY_INFORMATION
                           | SACL_SECURITY_INFORMATION;

            PointerByReference ppsidOwner = new PointerByReference();
            PointerByReference ppsidGroup = new PointerByReference();
            PointerByReference ppDacl = new PointerByReference();
            PointerByReference ppSacl = new PointerByReference();
            PointerByReference ppSecurityDescriptor = new PointerByReference();
            String filePath = file.getAbsolutePath();
            HANDLE hFile = WinBase.INVALID_HANDLE_VALUE;

            try {
                try {
                    hFile = Kernel32.INSTANCE.CreateFile(
                                filePath,
                                WinNT.ACCESS_SYSTEM_SECURITY | WinNT.GENERIC_WRITE | WinNT.WRITE_OWNER | WinNT.WRITE_DAC,
                                WinNT.FILE_SHARE_READ,
                                new WinBase.SECURITY_ATTRIBUTES(),
                                WinNT.OPEN_EXISTING,
                                WinNT.FILE_ATTRIBUTE_NORMAL,
                                null);
                    assertEquals("GetSecurityInfo(" + filePath + ")", 0,
                            Advapi32.INSTANCE.GetSecurityInfo(
                                    hFile,
                                    AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                    infoType,
                                    ppsidOwner,
                                    ppsidGroup,
                                    ppDacl,
                                    ppSacl,
                                    ppSecurityDescriptor));
                    assertEquals("SetSecurityInfo(" + filePath + ")", 0,
                            Advapi32.INSTANCE.SetSecurityInfo(
                                    hFile,
                                    AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                    infoType,
                                    ppsidOwner.getValue(),
                                    ppsidGroup.getValue(),
                                    ppDacl.getValue(),
                                    ppSacl.getValue()));
                } finally {
                    if (hFile != WinBase.INVALID_HANDLE_VALUE)
                        Kernel32.INSTANCE.CloseHandle(hFile);
                    file.delete();
                }
            } finally {
                Kernel32Util.freeLocalMemory(ppSecurityDescriptor.getValue());
            }

            if (impersontating) {
                assertTrue("SetThreadToken", Advapi32.INSTANCE.SetThreadToken(null, null));
            }
            else {
                tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(pLuid, new DWORD(0));
                assertTrue("AdjustTokenPrivileges", Advapi32.INSTANCE.AdjustTokenPrivileges(tokenAdjust, false, tp, 0, null, null));
            }
        } finally {
            Kernel32Util.closeHandleRefs(phToken, phTokenDuplicate);
        }
    }

    public void testGetNamedSecurityInfoForFileNoSACL() throws Exception {
        int infoType = OWNER_SECURITY_INFORMATION
                       | GROUP_SECURITY_INFORMATION
                       | DACL_SECURITY_INFORMATION;

        PointerByReference ppsidOwner = new PointerByReference();
        PointerByReference ppsidGroup = new PointerByReference();
        PointerByReference ppDacl = new PointerByReference();
        PointerByReference ppSecurityDescriptor = new PointerByReference();

        File file = createTempFile();
        try {
            try {
                assertEquals("GetNamedSecurityInfo(" + file + ")", 0,
                        Advapi32.INSTANCE.GetNamedSecurityInfo(
                              file.getAbsolutePath(),
                              AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                              infoType,
                              ppsidOwner,
                              ppsidGroup,
                              ppDacl,
                              null,
                              ppSecurityDescriptor));
            } finally {
                file.delete();
            }
        } finally {
            Kernel32Util.freeLocalMemory(ppSecurityDescriptor.getValue());
        }
    }

    public void testGetNamedSecurityInfoForFileWithSACL() throws Exception {

        boolean impersontating = false;
        WinNT.LUID pLuid = new WinNT.LUID();

        assertTrue(Advapi32.INSTANCE.LookupPrivilegeValue(null, SE_SECURITY_NAME, pLuid));

        HANDLEByReference phToken = new HANDLEByReference();
        HANDLEByReference phTokenDuplicate = new HANDLEByReference();
        try {
            // open thread or process token, elevate
            if (!Advapi32.INSTANCE.OpenThreadToken(
                    Kernel32.INSTANCE.GetCurrentThread(), TOKEN_ADJUST_PRIVILEGES, false, phToken))
            {
                assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
                // OpenThreadToken may fail with W32Errors.ERROR_NO_TOKEN if current thread is anonymous.  When this happens,
                // we need to open the process token to duplicate it, then set our thread token.
                assertTrue(Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), TOKEN_DUPLICATE, phToken));
                // Process token opened, now duplicate
                assertTrue(Advapi32.INSTANCE.DuplicateTokenEx(phToken.getValue(),
                            TOKEN_ADJUST_PRIVILEGES | TOKEN_IMPERSONATE,
                            null,
                            SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation,
                            TOKEN_TYPE.TokenImpersonation,
                            phTokenDuplicate));
                // And set thread token.
                assertTrue(Advapi32.INSTANCE.SetThreadToken(null, phTokenDuplicate.getValue()));
                impersontating = true;
            }

            // Which token to adjust depends on whether we had to impersonate or not.
            HANDLE tokenAdjust = impersontating ? phTokenDuplicate.getValue() : phToken.getValue();

            WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(1);
            tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(pLuid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
            assertTrue(Advapi32.INSTANCE.AdjustTokenPrivileges(tokenAdjust, false, tp, 0, null, null));

            int infoType = OWNER_SECURITY_INFORMATION
                           | GROUP_SECURITY_INFORMATION
                           | DACL_SECURITY_INFORMATION
                           | SACL_SECURITY_INFORMATION;

            PointerByReference ppsidOwner = new PointerByReference();
            PointerByReference ppsidGroup = new PointerByReference();
            PointerByReference ppDacl = new PointerByReference();
            PointerByReference ppSacl = new PointerByReference();
            PointerByReference ppSecurityDescriptor = new PointerByReference();

            File file = createTempFile();
            String filePath = file.getAbsolutePath();
            try {
                try {
                    assertEquals("GetNamedSecurityInfo(" + filePath + ")", 0,
                                 Advapi32.INSTANCE.GetNamedSecurityInfo(
                                         filePath,
                                         AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                         infoType,
                                         ppsidOwner,
                                         ppsidGroup,
                                         ppDacl,
                                         ppSacl,
                                         ppSecurityDescriptor));
                } finally {
                    file.delete();
                }
            } finally {
                Kernel32Util.freeLocalMemory(ppSecurityDescriptor.getValue());
            }
            if (impersontating) {
                Advapi32.INSTANCE.SetThreadToken(null, null);
            }
            else {
                tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(pLuid, new DWORD(0));
                Advapi32.INSTANCE.AdjustTokenPrivileges(tokenAdjust, false, tp, 0, null, null);
            }
        } finally {
            Kernel32Util.closeHandleRefs(phToken, phTokenDuplicate);
        }
    }

    public void testSetNamedSecurityInfoForFileNoSACL() throws Exception {
        int infoType = OWNER_SECURITY_INFORMATION
                       | GROUP_SECURITY_INFORMATION
                       | DACL_SECURITY_INFORMATION;

        PointerByReference ppsidOwner = new PointerByReference();
        PointerByReference ppsidGroup = new PointerByReference();
        PointerByReference ppDacl = new PointerByReference();
        PointerByReference ppSecurityDescriptor = new PointerByReference();
        // create a temp file
        File file = createTempFile();
        String filePath = file.getAbsolutePath();
        try {
            try {
                assertEquals("GetNamedSecurityInfo(" + filePath + ")", 0,
                             Advapi32.INSTANCE.GetNamedSecurityInfo(
                                     filePath,
                                     AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                     infoType,
                                     ppsidOwner,
                                     ppsidGroup,
                                     ppDacl,
                                     null,
                                     ppSecurityDescriptor));
                assertEquals("SetNamedSecurityInfo(" + filePath + ")", 0,
                             Advapi32.INSTANCE.SetNamedSecurityInfo(
                                     filePath,
                                     AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                     infoType,
                                     ppsidOwner.getValue(),
                                     ppsidGroup.getValue(),
                                     ppDacl.getValue(),
                                     null));
            } finally {
                file.delete();
            }
        } finally {
            Kernel32Util.freeLocalMemory(ppSecurityDescriptor.getValue());
        }
    }

    public void testSetNamedSecurityInfoForFileWithSACL() throws Exception {
        boolean impersontating = false;

        HANDLEByReference phToken = new HANDLEByReference();
        HANDLEByReference phTokenDuplicate = new HANDLEByReference();
        try {
            // open thread or process token, elevate
            if (!Advapi32.INSTANCE.OpenThreadToken(
                 Kernel32.INSTANCE.GetCurrentThread(),
                 TOKEN_ADJUST_PRIVILEGES,
                 false,
                 phToken))
            {
                assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
                // OpenThreadToken may fail with W32Errors.ERROR_NO_TOKEN if current thread is anonymous.  When this happens,
                // we need to open the process token to duplicate it, then set our thread token.
                assertTrue(Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), TOKEN_DUPLICATE, phToken));
                // Process token opened, now duplicate
                assertTrue(Advapi32.INSTANCE.DuplicateTokenEx(
                            phToken.getValue(),
                            TOKEN_ADJUST_PRIVILEGES | TOKEN_IMPERSONATE,
                            null,
                            SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation,
                            TOKEN_TYPE.TokenImpersonation,
                            phTokenDuplicate));
                // And set thread token.
                assertTrue(Advapi32.INSTANCE.SetThreadToken(null, phTokenDuplicate.getValue()));
                impersontating = true;
            }

            // Which token to adjust depends on whether we had to impersonate or not.
            HANDLE tokenAdjust = impersontating ? phTokenDuplicate.getValue() : phToken.getValue();

            WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(1);
            WinNT.LUID pLuid = new WinNT.LUID();

            assertTrue(Advapi32.INSTANCE.LookupPrivilegeValue(null, SE_SECURITY_NAME, pLuid));
            tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(pLuid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
            assertTrue(Advapi32.INSTANCE.AdjustTokenPrivileges(tokenAdjust, false, tp, 0, null, null));

            assertTrue(Advapi32.INSTANCE.LookupPrivilegeValue(null, SE_RESTORE_NAME, pLuid));
            tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(pLuid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
            assertTrue(Advapi32.INSTANCE.AdjustTokenPrivileges(tokenAdjust, false, tp, 0, null, null));

            // create a temp file
            File file = createTempFile();
            int infoType = OWNER_SECURITY_INFORMATION
                           | GROUP_SECURITY_INFORMATION
                           | DACL_SECURITY_INFORMATION
                           | SACL_SECURITY_INFORMATION;

            PointerByReference ppsidOwner = new PointerByReference();
            PointerByReference ppsidGroup = new PointerByReference();
            PointerByReference ppDacl = new PointerByReference();
            PointerByReference ppSacl = new PointerByReference();
            PointerByReference ppSecurityDescriptor = new PointerByReference();
            String filePath = file.getAbsolutePath();
            try {
                try {
                    assertEquals("GetNamedSecurityInfo(" + filePath + ")", 0,
                            Advapi32.INSTANCE.GetNamedSecurityInfo(
                                  filePath,
                                  AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                  infoType,
                                  ppsidOwner,
                                  ppsidGroup,
                                  ppDacl,
                                  ppSacl,
                                  ppSecurityDescriptor));

                    // Send the DACL as a SACL
                    assertEquals("SetNamedSecurityInfo(" + filePath + ")", 0,
                            Advapi32.INSTANCE.SetNamedSecurityInfo(
                                  filePath,
                                  AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                                  infoType,
                                  ppsidOwner.getValue(),
                                  ppsidGroup.getValue(),
                                  ppDacl.getValue(),
                                  ppDacl.getValue()));
                } finally {
                    file.delete();
                }
            } finally {
                Kernel32Util.freeLocalMemory(ppSecurityDescriptor.getValue());
            }

            if (impersontating) {
                assertTrue("SetThreadToken", Advapi32.INSTANCE.SetThreadToken(null, null));
            }
            else {
                tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(pLuid, new DWORD(0));
                assertTrue("AdjustTokenPrivileges", Advapi32.INSTANCE.AdjustTokenPrivileges(tokenAdjust, false, tp, 0, null, null));
            }
        } finally {
            Kernel32Util.closeHandleRefs(phToken, phTokenDuplicate);
        }
    }

    public void testGetSecurityDescriptorLength() throws Exception {
        int infoType = OWNER_SECURITY_INFORMATION
                       | GROUP_SECURITY_INFORMATION
                       | DACL_SECURITY_INFORMATION;

        PointerByReference ppSecurityDescriptor = new PointerByReference();

        File file = createTempFile();
        try {
            try {
                assertEquals("GetNamedSecurityInfo(" + file + ")", 0,
                        Advapi32.INSTANCE.GetNamedSecurityInfo(
                              file.getAbsolutePath(),
                              AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                              infoType,
                              null,
                              null,
                              null,
                              null,
                              ppSecurityDescriptor));

                assertTrue("GetSecurityDescriptorLength(" + file + ")",
                        Advapi32.INSTANCE.GetSecurityDescriptorLength(ppSecurityDescriptor.getValue()) > 0);
            } finally {
                file.delete();
            }
        } finally {
            Kernel32Util.freeLocalMemory(ppSecurityDescriptor.getValue());
        }
    }

    public void testIsValidSecurityDescriptor() throws Exception {
        int infoType = OWNER_SECURITY_INFORMATION
                       | GROUP_SECURITY_INFORMATION
                       | DACL_SECURITY_INFORMATION;

        PointerByReference ppSecurityDescriptor = new PointerByReference();

        File file = createTempFile();
        try {
            try {
                assertEquals("GetNamedSecurityInfo(" + file + ")",
                        Advapi32.INSTANCE.GetNamedSecurityInfo(
                              file.getAbsolutePath(),
                              AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                              infoType,
                              null,
                              null,
                              null,
                              null,
                              ppSecurityDescriptor), 0);

                assertTrue("IsValidSecurityDescriptor(" + file + ")",
                        Advapi32.INSTANCE.IsValidSecurityDescriptor(ppSecurityDescriptor.getValue()));
            } finally {
                file.delete();
            }
        } finally {
            Kernel32Util.freeLocalMemory(ppSecurityDescriptor.getValue());
        }
    }

    public void testMakeSelfRelativeSD() {
        SECURITY_DESCRIPTOR absolute = new SECURITY_DESCRIPTOR(64 * 1024);
        assertTrue(Advapi32.INSTANCE.InitializeSecurityDescriptor(absolute, WinNT.SECURITY_DESCRIPTOR_REVISION));
        SECURITY_DESCRIPTOR_RELATIVE relative = new SECURITY_DESCRIPTOR_RELATIVE(64 * 1024);
        IntByReference lpdwBufferLength = new IntByReference(64 * 1024);
        assertTrue(Advapi32.INSTANCE.MakeSelfRelativeSD(absolute, relative, lpdwBufferLength));
        assertEquals(WinNT.SECURITY_DESCRIPTOR_REVISION, relative.Revision);
    }

    public void testMakeAbsoluteSD() throws Exception {
        SECURITY_DESCRIPTOR absolute = new SECURITY_DESCRIPTOR(64 * 1024);

        // Get a SD in self relative form
        int infoType = OWNER_SECURITY_INFORMATION
            | GROUP_SECURITY_INFORMATION
            | DACL_SECURITY_INFORMATION;

        PointerByReference relativeByReference = new PointerByReference();
        File file = createTempFile();
        try {
            try {
                assertEquals("GetNamedSecurityInfo(" + file + ")",
                    Advapi32.INSTANCE.GetNamedSecurityInfo(
                        file.getAbsolutePath(),
                        AccCtrl.SE_OBJECT_TYPE.SE_FILE_OBJECT,
                        infoType,
                        null,
                        null,
                        null,
                        null,
                        relativeByReference), 0);

                SECURITY_DESCRIPTOR_RELATIVE relative = new SECURITY_DESCRIPTOR_RELATIVE(relativeByReference.getValue());

                PSID pOwner = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
                PSID pGroup = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
                ACL pDacl = new ACL(ACL.MAX_ACL_SIZE);
                ACL pSacl = new ACL(ACL.MAX_ACL_SIZE);

                IntByReference lpdwBufferLength = new IntByReference(absolute.size());
                IntByReference lpdwDaclSize = new IntByReference(ACL.MAX_ACL_SIZE);
                IntByReference lpdwSaclSize = new IntByReference(ACL.MAX_ACL_SIZE);
                IntByReference lpdwOwnerSize = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
                IntByReference lpdwPrimaryGroupSize = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);

                assertTrue(Advapi32.INSTANCE.MakeAbsoluteSD(relative, absolute, lpdwBufferLength, pDacl, lpdwDaclSize, pSacl, lpdwSaclSize, pOwner, lpdwOwnerSize, pGroup, lpdwPrimaryGroupSize));
            } finally {
                file.delete();
            }
        } finally {
            Kernel32Util.freeLocalMemory(relativeByReference.getValue());
        }
    }

    public void testMapGenericReadMask() {
        final GENERIC_MAPPING mapping = new GENERIC_MAPPING();
        mapping.genericRead = new DWORD(FILE_GENERIC_READ);
        mapping.genericWrite = new DWORD(FILE_GENERIC_WRITE);
        mapping.genericExecute = new DWORD(FILE_GENERIC_EXECUTE);
        mapping.genericAll = new DWORD(FILE_ALL_ACCESS);

        final DWORDByReference rights = new DWORDByReference(new DWORD(GENERIC_READ));
        Advapi32.INSTANCE.MapGenericMask(rights, mapping);

        assertEquals(FILE_GENERIC_READ, rights.getValue().intValue());
        assertTrue(GENERIC_READ != (rights.getValue().intValue() & GENERIC_READ));
    }

    public void testMapGenericWriteMask() {
        final GENERIC_MAPPING mapping = new GENERIC_MAPPING();
        mapping.genericRead = new DWORD(FILE_GENERIC_READ);
        mapping.genericWrite = new DWORD(FILE_GENERIC_WRITE);
        mapping.genericExecute = new DWORD(FILE_GENERIC_EXECUTE);
        mapping.genericAll = new DWORD(FILE_ALL_ACCESS);

        final DWORDByReference rights = new DWORDByReference(new DWORD(GENERIC_WRITE));
        Advapi32.INSTANCE.MapGenericMask(rights, mapping);

        assertEquals(FILE_GENERIC_WRITE, rights.getValue().intValue());
        assertTrue(GENERIC_WRITE != (rights.getValue().intValue() & GENERIC_WRITE));
    }

    public void testMapGenericExecuteMask() {
        final GENERIC_MAPPING mapping = new GENERIC_MAPPING();
        mapping.genericRead = new DWORD(FILE_GENERIC_READ);
        mapping.genericWrite = new DWORD(FILE_GENERIC_WRITE);
        mapping.genericExecute = new DWORD(FILE_GENERIC_EXECUTE);
        mapping.genericAll = new DWORD(FILE_ALL_ACCESS);

        final DWORDByReference rights = new DWORDByReference(new DWORD(GENERIC_EXECUTE));
        Advapi32.INSTANCE.MapGenericMask(rights, mapping);

        assertEquals(FILE_GENERIC_EXECUTE, rights.getValue().intValue());
        assertTrue(GENERIC_EXECUTE != (rights.getValue().intValue() & GENERIC_EXECUTE));
    }

    public void testMapGenericAllMask() {
        final GENERIC_MAPPING mapping = new GENERIC_MAPPING();
        mapping.genericRead = new DWORD(FILE_GENERIC_READ);
        mapping.genericWrite = new DWORD(FILE_GENERIC_WRITE);
        mapping.genericExecute = new DWORD(FILE_GENERIC_EXECUTE);
        mapping.genericAll = new DWORD(FILE_ALL_ACCESS);

        final DWORDByReference rights = new DWORDByReference(new DWORD(GENERIC_ALL));
        Advapi32.INSTANCE.MapGenericMask(rights, mapping);

        assertEquals(FILE_ALL_ACCESS, rights.getValue().intValue());
        assertTrue(GENERIC_ALL != (rights.getValue().intValue() & GENERIC_ALL));
    }

    public void testAccessCheck() {
        final GENERIC_MAPPING mapping = new GENERIC_MAPPING();
        mapping.genericRead = new DWORD(FILE_GENERIC_READ);
        mapping.genericWrite = new DWORD(FILE_GENERIC_WRITE);
        mapping.genericExecute = new DWORD(FILE_GENERIC_EXECUTE);
        mapping.genericAll = new DWORD(FILE_ALL_ACCESS);
        final Memory securityDescriptorMemoryPointer = new Memory(1);

        final PRIVILEGE_SET privileges = new PRIVILEGE_SET(1);
        privileges.PrivilegeCount = new DWORD(0);
        final DWORDByReference privilegeLength = new DWORDByReference(new DWORD(privileges.size()));
        final DWORDByReference grantedAccess = new DWORDByReference();
        final BOOLByReference result = new BOOLByReference();

        final boolean status = Advapi32.INSTANCE.AccessCheck(securityDescriptorMemoryPointer, null, new DWORD(FILE_GENERIC_READ), mapping, privileges, privilegeLength, grantedAccess, result);
        assertFalse(status);
        assertFalse(result.getValue().booleanValue());

        assertEquals(WinError.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
    }

    public void testEncryptFile() throws Exception {
        // create a temp file
        File file = createTempFile();
        String lpFileName = file.getAbsolutePath();

        // encrypt a read only file
        file.setWritable(false);
        assertFalse(Advapi32.INSTANCE.EncryptFile(lpFileName));
        assertEquals(WinError.ERROR_FILE_READ_ONLY, Kernel32.INSTANCE.GetLastError());

        // encrypt a writable file
        file.setWritable(true);
        assertTrue(Advapi32.INSTANCE.EncryptFile(lpFileName));

        file.delete();
    }

    public void testDecryptFile() throws Exception {
        // create an encrypted file
        File file = createTempFile();
        String lpFileName = file.getAbsolutePath();
        assertTrue(Advapi32.INSTANCE.EncryptFile(lpFileName));

        // decrypt a read only file
        file.setWritable(false);
        assertFalse(Advapi32.INSTANCE.DecryptFile(lpFileName, new DWORD(0)));
        assertEquals(WinError.ERROR_FILE_READ_ONLY, Kernel32.INSTANCE.GetLastError());

        // decrypt
        file.setWritable(true);
        assertTrue(Advapi32.INSTANCE.DecryptFile(lpFileName, new DWORD(0)));

        file.delete();
    }

    public void testFileEncryptionStatus() throws Exception {
        DWORDByReference lpStatus = new DWORDByReference();

        // create a temp file
        File file = createTempFile();
        String lpFileName = file.getAbsolutePath();

        // unencrypted file
        assertTrue(Advapi32.INSTANCE.FileEncryptionStatus(lpFileName, lpStatus));
        assertEquals(FILE_ENCRYPTABLE, lpStatus.getValue().intValue());

        // read only file
        file.setWritable(false);
        assertTrue(Advapi32.INSTANCE.FileEncryptionStatus(lpFileName, lpStatus));
        assertEquals(FILE_READ_ONLY, lpStatus.getValue().intValue());

        // encrypted file
        file.setWritable(true);
        assertTrue(Advapi32.INSTANCE.EncryptFile(lpFileName));
        assertTrue(Advapi32.INSTANCE.FileEncryptionStatus(lpFileName, lpStatus));
        assertEquals(FILE_IS_ENCRYPTED, lpStatus.getValue().intValue());

        file.delete();
    }

    public void testEncryptionDisable() throws Exception {
        DWORDByReference lpStatus = new DWORDByReference();

        // create a temp dir
        String filePath = System.getProperty("java.io.tmpdir") + File.separator +
                System.nanoTime();
        String DirPath = filePath;
        File dir = new File(filePath);
        dir.mkdir();

        // check status
        assertTrue(Advapi32.INSTANCE.FileEncryptionStatus(DirPath, lpStatus));
        assertEquals(FILE_ENCRYPTABLE, lpStatus.getValue().intValue());

        // disable encryption
        assertTrue(Advapi32.INSTANCE.EncryptionDisable(DirPath, true));
        assertTrue(Advapi32.INSTANCE.FileEncryptionStatus(DirPath, lpStatus));
        assertEquals(FILE_DIR_DISALOWED, lpStatus.getValue().intValue());

        // enable encryption
        assertTrue(Advapi32.INSTANCE.EncryptionDisable(DirPath, false));
        assertTrue(Advapi32.INSTANCE.FileEncryptionStatus(DirPath, lpStatus));
        assertEquals(FILE_ENCRYPTABLE, lpStatus.getValue().intValue());

        // clean up
        for (File file : dir.listFiles()) {
            file.delete();
        }
        dir.delete();
    }

    public void testOpenEncryptedFileRaw() throws Exception {
        // create an encrypted file
        File file = createTempFile();
        String lpFileName = file.getAbsolutePath();
        assertTrue(Advapi32.INSTANCE.EncryptFile(lpFileName));

        // open file for export
        ULONG ulFlags = new ULONG(0);
        PointerByReference pvContext = new PointerByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.OpenEncryptedFileRaw(
                lpFileName, ulFlags, pvContext));

        Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());
        file.delete();
    }

    public void testReadEncryptedFileRaw() throws Exception {
        // create an encrypted file
        File file = createTempFile();
        String lpFileName = file.getAbsolutePath();
        assertTrue("EncryptFile(" + lpFileName + ")", Advapi32.INSTANCE.EncryptFile(lpFileName));

        // open file for export
        ULONG ulFlags = new ULONG(0);
        PointerByReference pvContext = new PointerByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.OpenEncryptedFileRaw(
                lpFileName, ulFlags, pvContext));

        // read encrypted file
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FE_EXPORT_FUNC pfExportCallback = new FE_EXPORT_FUNC() {
            @Override
            public DWORD callback(Pointer pbData, Pointer
                    pvCallbackContext, ULONG ulLength) {
                if (pbData == null) {
                    throw new NullPointerException("Callback data unexpectedly missing");
                }
                byte[] arr = pbData.getByteArray(0, ulLength.intValue());
                try {
                    outputStream.write(arr);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new DWORD(W32Errors.ERROR_SUCCESS);
            }
        };

        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.ReadEncryptedFileRaw(
                pfExportCallback, null, pvContext.getValue()));
        outputStream.close();

        Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());
        file.delete();
    }

    public void testWriteEncryptedFileRaw() throws Exception {
        // create an encrypted file
        File file = createTempFile();
        String lpFileName = file.getAbsolutePath();
        assertTrue(Advapi32.INSTANCE.EncryptFile(lpFileName));

        // open file for export
        ULONG ulFlags = new ULONG(0);
        PointerByReference pvContext = new PointerByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.OpenEncryptedFileRaw(
                lpFileName, ulFlags, pvContext));

        // read encrypted file
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FE_EXPORT_FUNC pfExportCallback = new FE_EXPORT_FUNC() {
            @Override
            public DWORD callback(Pointer pbData, Pointer
                    pvCallbackContext, ULONG ulLength) {
                if (pbData == null) {
                    throw new NullPointerException("Callback data unexpectedly null");
                }
                byte[] arr = pbData.getByteArray(0, ulLength.intValue());
                try {
                    outputStream.write(arr);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new DWORD(W32Errors.ERROR_SUCCESS);
            }
        };

        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.ReadEncryptedFileRaw(
                pfExportCallback, null, pvContext.getValue()));
        outputStream.close();
        Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());

        // open file for import
        String lbFileName2 = System.getProperty("java.io.tmpdir") +
                File.separator + "backup-" + file.getName();
        ULONG ulFlags2 = new ULONG(CREATE_FOR_IMPORT);
        PointerByReference pvContext2 = new PointerByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.OpenEncryptedFileRaw(
                lbFileName2, ulFlags2, pvContext2));

        // write encrypted file
        final IntByReference elementsReadWrapper = new IntByReference(0);
        FE_IMPORT_FUNC pfImportCallback = new FE_IMPORT_FUNC() {
            @Override
            public DWORD callback(Pointer pbData, Pointer pvCallbackContext,
                                  ULONGByReference ulLength) {
                int elementsRead = elementsReadWrapper.getValue();
                int remainingElements = outputStream.size() - elementsRead;
                int length = Math.min(remainingElements, ulLength.getValue().intValue());
                pbData.write(0, outputStream.toByteArray(), elementsRead, length);
                elementsReadWrapper.setValue(elementsRead + length);
                ulLength.setValue(new ULONG(length));
                return new DWORD(W32Errors.ERROR_SUCCESS);
            }
        };

        assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.WriteEncryptedFileRaw(
                pfImportCallback, null, pvContext2.getValue()));
        Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext2.getValue());

        file.delete();
        new File(lbFileName2).delete();
    }

    private File createTempFile() throws Exception {
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

    public void testCreateProcessWithLogonW() {
        String winDir = Kernel32Util.getEnvironmentVariable("WINDIR");
        assertNotNull("No WINDIR value returned", winDir);
        assertTrue("Specified WINDIR does not exist: " + winDir, new File(winDir).exists());

        STARTUPINFO si = new STARTUPINFO();
        si.lpDesktop = null;
        PROCESS_INFORMATION results = new PROCESS_INFORMATION();

        // i have the same combination on my luggage
        boolean result = Advapi32.INSTANCE.CreateProcessWithLogonW("A" + System.currentTimeMillis(), "localhost", "12345", Advapi32.LOGON_WITH_PROFILE, new File(winDir, "notepad.exe").getAbsolutePath(), "", 0, null, "", si, results);

        // we tried to run notepad as a bogus user, so it should fail.
        assertFalse("CreateProcessWithLogonW should have returned false because the username was bogus.", result);

        // should fail with "the user name or password is incorrect" (error 1326)
        assertEquals("GetLastError() should have returned ERROR_LOGON_FAILURE because the username was bogus.", W32Errors.ERROR_LOGON_FAILURE, Native.getLastError());
    }
}
