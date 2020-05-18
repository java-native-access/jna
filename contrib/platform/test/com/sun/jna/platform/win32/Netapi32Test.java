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

import java.io.File;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.DsGetDC.DS_DOMAIN_TRUSTS;
import com.sun.jna.platform.win32.DsGetDC.PDOMAIN_CONTROLLER_INFO;
import com.sun.jna.platform.win32.LMAccess.GROUP_INFO_2;
import com.sun.jna.platform.win32.LMAccess.GROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.LOCALGROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.platform.win32.LMShare.SHARE_INFO_2;
import com.sun.jna.platform.win32.LMShare.SHARE_INFO_502;
import com.sun.jna.platform.win32.NTSecApi.LSA_FOREST_TRUST_RECORD;
import com.sun.jna.platform.win32.NTSecApi.PLSA_FOREST_TRUST_INFORMATION;
import com.sun.jna.platform.win32.NTSecApi.PLSA_FOREST_TRUST_RECORD;
import com.sun.jna.platform.win32.Netapi32.SESSION_INFO_10;
import com.sun.jna.platform.win32.Netapi32Util.User;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Netapi32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Netapi32Test.class);
    }

    public void testNetSessionEnum() {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        assertEquals("NetSessionEnum call failed", 0, Netapi32.INSTANCE.NetSessionEnum(null, null, null, 10, bufptr,
                Netapi32.MAX_PREFERRED_LENGTH, entriesread, totalentries, null));
        Pointer buf = bufptr.getValue();
        SESSION_INFO_10 si10 = new SESSION_INFO_10(buf);
        if (entriesread.getValue() > 0) {
            SESSION_INFO_10[] sessionInfo = (SESSION_INFO_10[]) si10.toArray(entriesread.getValue());
            for (SESSION_INFO_10 si : sessionInfo) {
                assertNotNull("Computer name was null", si.sesi10_cname);
                assertNotNull("User name was null", si.sesi10_username);
                // time field is connected seconds
                assertTrue("Idle time must be at least 0", si.sesi10_idle_time >= 0);
                assertTrue("Idle time must be less than Connected time", si.sesi10_time >= si.sesi10_idle_time);
            }
            assertEquals(0, Netapi32.INSTANCE.NetApiBufferFree(buf));
        }
    }

    public void testNetGetJoinInformation() {
        IntByReference bufferType = new IntByReference();
        assertEquals(W32Errors.ERROR_INVALID_PARAMETER, Netapi32.INSTANCE.NetGetJoinInformation(
                null, null, bufferType));
        PointerByReference lpNameBuffer = new PointerByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetGetJoinInformation(
                null, lpNameBuffer, bufferType));
        assertTrue(lpNameBuffer.getValue().getString(0).length() > 0);
        assertTrue(bufferType.getValue() > 0);
        assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(
                lpNameBuffer.getValue()));
    }

    public void testNetGetLocalGroups() {
        for (int i = 0; i < 2; i++) {
            PointerByReference bufptr = new PointerByReference();
            IntByReference entriesRead = new IntByReference();
            IntByReference totalEntries = new IntByReference();
            assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetLocalGroupEnum(null, i, bufptr,
                    LMCons.MAX_PREFERRED_LENGTH,
                    entriesRead,
                    totalEntries,
                    null));
            assertTrue(entriesRead.getValue() > 0);
            assertEquals(totalEntries.getValue(), entriesRead.getValue());
            assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(
                    bufptr.getValue()));
        }
    }

    public void testNetGetDCName() {
        PointerByReference lpNameBuffer = new PointerByReference();
        IntByReference BufferType = new IntByReference();
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetGetJoinInformation(null, lpNameBuffer, BufferType));
        if (BufferType.getValue() == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            PointerByReference bufptr = new PointerByReference();
            assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetGetDCName(null, null, bufptr));
            String dc = bufptr.getValue().getString(0);
            assertTrue(dc.length() > 0);
            assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()));
        }
        assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue()));
    }

    public void testNetUserGetGroups() {
        User[] users = Netapi32Util.getUsers();
        assertTrue(users.length >= 1);
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserGetGroups(
                null, users[0].name, 0, bufptr, LMCons.MAX_PREFERRED_LENGTH,
                entriesread, totalentries));
        GROUP_USERS_INFO_0 lgroup = new GROUP_USERS_INFO_0(bufptr.getValue());
        GROUP_USERS_INFO_0[] lgroups = (GROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
        for (GROUP_USERS_INFO_0 localGroupInfo : lgroups) {
            assertTrue(localGroupInfo.grui0_name.length() > 0);
        }
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()));
    }

    public void testNetUserGetLocalGroups() {
        String currentUser = Secur32Util.getUserNameEx(
                EXTENDED_NAME_FORMAT.NameSamCompatible);
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserGetLocalGroups(
                null, currentUser, 0, 0, bufptr, LMCons.MAX_PREFERRED_LENGTH,
                entriesread, totalentries));
        LOCALGROUP_USERS_INFO_0 lgroup = new LOCALGROUP_USERS_INFO_0(bufptr.getValue());
        LOCALGROUP_USERS_INFO_0[] lgroups = (LOCALGROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
        for (LOCALGROUP_USERS_INFO_0 localGroupInfo : lgroups) {
            assertTrue(localGroupInfo.lgrui0_name.length() > 0);
        }
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()));
    }

    public void testNetGroupEnum() {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetGroupEnum(
                null, 2, bufptr, LMCons.MAX_PREFERRED_LENGTH, entriesread, totalentries, null));
        GROUP_INFO_2 group = new GROUP_INFO_2(bufptr.getValue());
        GROUP_INFO_2[] groups = (GROUP_INFO_2[]) group.toArray(entriesread.getValue());
        for (GROUP_INFO_2 grpi : groups) {
            assertTrue(grpi.grpi2_name.length() > 0);
        }
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()));
    }

    public void testNetUserEnum() {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserEnum(
                null, 1, 0, bufptr, LMCons.MAX_PREFERRED_LENGTH, entriesread, totalentries, null));
        USER_INFO_1 userinfo = new USER_INFO_1(bufptr.getValue());
        USER_INFO_1[] userinfos = (USER_INFO_1[]) userinfo.toArray(entriesread.getValue());
        for (USER_INFO_1 ui : userinfos) {
            assertTrue(ui.usri1_name.length() > 0);
        }
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()));
    }

    public void testNetUserAdd() {
        USER_INFO_1 userInfo = new USER_INFO_1();
        userInfo.usri1_name = "JNANetapi32TestUser";
        userInfo.usri1_password = "!JNAP$$Wrd0";
        userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
        // ignore test if not able to add user (need to be administrator to do this).
        if (LMErr.NERR_Success != Netapi32.INSTANCE.NetUserAdd(Kernel32Util.getComputerName(), 1, userInfo, null)) {
            return;
        }
        assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
                Kernel32Util.getComputerName(), userInfo.usri1_name.toString()));
    }

    public void testNetUserChangePassword() {
        USER_INFO_1 userInfo = new USER_INFO_1();
        userInfo.usri1_name = "JNANetapi32TestUser";
        userInfo.usri1_password = "!JNAP$$Wrd0";
        userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
        // ignore test if not able to add user (need to be administrator to do this).
        if (LMErr.NERR_Success != Netapi32.INSTANCE.NetUserAdd(Kernel32Util.getComputerName(), 1, userInfo, null)) {
            return;
        }
        try {
            assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserChangePassword(
                    Kernel32Util.getComputerName(), userInfo.usri1_name.toString(), userInfo.usri1_password.toString(),
                    "!JNAP%%Wrd1"));
        } finally {
            assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
                    Kernel32Util.getComputerName(), userInfo.usri1_name.toString()));
        }
    }

    public void testNetUserDel() {
        assertEquals(LMErr.NERR_UserNotFound, Netapi32.INSTANCE.NetUserDel(
                Kernel32Util.getComputerName(), "JNANetapi32TestUserDoesntExist"));
    }

    public void testDsGetDcName() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }

        PDOMAIN_CONTROLLER_INFO.ByReference pdci = new PDOMAIN_CONTROLLER_INFO.ByReference();
        assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.DsGetDcName(
                null, null, null, null, 0, pdci));
        assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(
                pdci.getPointer()));
    }

    public void testDsGetForestTrustInformation() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }

        String domainController = Netapi32Util.getDCName();
        PLSA_FOREST_TRUST_INFORMATION.ByReference pfti = new PLSA_FOREST_TRUST_INFORMATION.ByReference();
        assertEquals(W32Errors.NO_ERROR, Netapi32.INSTANCE.DsGetForestTrustInformation(
                domainController, null, 0, pfti));

        assertTrue(pfti.fti.RecordCount >= 0);

        for (PLSA_FOREST_TRUST_RECORD precord : pfti.fti.getEntries()) {
            LSA_FOREST_TRUST_RECORD.UNION data = precord.tr.u;
            switch (precord.tr.ForestTrustType) {
                case NTSecApi.ForestTrustTopLevelName:
                case NTSecApi.ForestTrustTopLevelNameEx:
                    assertTrue(data.TopLevelName.Length > 0);
                    assertTrue(data.TopLevelName.MaximumLength > 0);
                    assertTrue(data.TopLevelName.MaximumLength >= data.TopLevelName.Length);
                    assertTrue(data.TopLevelName.getString().length() > 0);
                    break;
                case NTSecApi.ForestTrustDomainInfo:
                    assertTrue(data.DomainInfo.DnsName.Length > 0);
                    assertTrue(data.DomainInfo.DnsName.MaximumLength > 0);
                    assertTrue(data.DomainInfo.DnsName.MaximumLength >= data.DomainInfo.DnsName.Length);
                    assertTrue(data.DomainInfo.DnsName.getString().length() > 0);
                    assertTrue(data.DomainInfo.NetbiosName.Length > 0);
                    assertTrue(data.DomainInfo.NetbiosName.MaximumLength > 0);
                    assertTrue(data.DomainInfo.NetbiosName.MaximumLength >= data.DomainInfo.NetbiosName.Length);
                    assertTrue(data.DomainInfo.NetbiosName.getString().length() > 0);
                    assertTrue(Advapi32.INSTANCE.IsValidSid(data.DomainInfo.Sid));
                    assertTrue(Advapi32Util.convertSidToStringSid(data.DomainInfo.Sid).startsWith("S-"));
                    break;
            }
        }

        assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(
                pfti.getPointer()));
    }

    public void testDsEnumerateDomainTrusts() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }

        IntByReference domainTrustCount = new IntByReference();
        PointerByReference domainsPointerRef = new PointerByReference();
        assertEquals(W32Errors.NO_ERROR, Netapi32.INSTANCE.DsEnumerateDomainTrusts(null,
                DsGetDC.DS_DOMAIN_VALID_FLAGS, domainsPointerRef, domainTrustCount));
        assertTrue(domainTrustCount.getValue() >= 0);

        DS_DOMAIN_TRUSTS domainTrustRefs = new DS_DOMAIN_TRUSTS(domainsPointerRef.getValue());
        DS_DOMAIN_TRUSTS[] domainTrusts = (DS_DOMAIN_TRUSTS[]) domainTrustRefs.toArray(new DS_DOMAIN_TRUSTS[domainTrustCount.getValue()]);

        for (DS_DOMAIN_TRUSTS trust : domainTrusts) {
            assertTrue(trust.DnsDomainName.length() > 0);
            assertTrue(Advapi32.INSTANCE.IsValidSid(trust.DomainSid));
            assertTrue(Advapi32Util.convertSidToStringSid(trust.DomainSid).startsWith("S-"));
            assertTrue(Ole32Util.getStringFromGUID(trust.DomainGuid).startsWith("{"));
        }

        assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(domainTrustRefs.getPointer()));
    }

    public void testNetShareAddShareInfo2() throws Exception {

        File fileShareFolder = createTempFolder();

        SHARE_INFO_2 shi = new SHARE_INFO_2();
        shi.shi2_netname = fileShareFolder.getName();
        shi.shi2_type = LMShare.STYPE_DISKTREE;
        shi.shi2_remark = "";
        shi.shi2_permissions = LMAccess.ACCESS_ALL;
        shi.shi2_max_uses = -1;
        shi.shi2_current_uses = 0;
        shi.shi2_path = fileShareFolder.getAbsolutePath();
        shi.shi2_passwd = "";

        // Write from struct to native memory.
        shi.write();

        IntByReference parm_err = new IntByReference(0);
        int winError = Netapi32.INSTANCE.NetShareAdd(null, // Use local computer
                2, shi.getPointer(), parm_err);

        if (winError == W32Errors.ERROR_INVALID_PARAMETER) {
            // fail with offset.
            throw new Exception("testNetShareAddShareInfo2 failed with invalid parameter on structure offset: " + parm_err.getValue());
        }

        assertEquals("Failed to add share", LMErr.NERR_Success, winError);

        Netapi32.INSTANCE.NetShareDel(null, shi.shi2_netname, 0);
    }

    public void testNetShareAddShareInfo502() throws Exception {

        File fileShareFolder = createTempFolder();

        SHARE_INFO_502 shi = new SHARE_INFO_502();
        shi.shi502_netname = fileShareFolder.getName();
        shi.shi502_type = LMShare.STYPE_DISKTREE;
        shi.shi502_remark = "";
        shi.shi502_permissions = LMAccess.ACCESS_ALL;
        shi.shi502_max_uses = -1;
        shi.shi502_current_uses = 0;
        shi.shi502_path = fileShareFolder.getAbsolutePath();
        shi.shi502_passwd = null;
        shi.shi502_reserved = 0;
        shi.shi502_security_descriptor = null;

        // Write from struct to native memory.
        shi.write();

        IntByReference parm_err = new IntByReference(0);
        int winError = Netapi32.INSTANCE.NetShareAdd(null, // Use local computer
                502, shi.getPointer(), parm_err);

        if (winError == W32Errors.ERROR_INVALID_PARAMETER) {
            // fail with offset.
            throw new Exception("testNetShareAddShareInfo502 failed with invalid parameter on structure offset: " + parm_err.getValue());
        }

        assertEquals(LMErr.NERR_Success, winError);

        Netapi32.INSTANCE.NetShareDel(null, shi.shi502_netname, 0);
    }

    public void testNetShareDel() throws Exception {

        File fileShareFolder = createTempFolder();

        SHARE_INFO_2 shi = new SHARE_INFO_2();
        shi.shi2_netname = fileShareFolder.getName();
        shi.shi2_type = LMShare.STYPE_DISKTREE;
        shi.shi2_remark = "";
        shi.shi2_permissions = LMAccess.ACCESS_ALL;
        shi.shi2_max_uses = -1;
        shi.shi2_current_uses = 0;
        shi.shi2_path = fileShareFolder.getAbsolutePath();
        shi.shi2_passwd = "";

        // Write from struct to native memory.
        shi.write();

        IntByReference parm_err = new IntByReference(0);
        assertEquals("Failed to add share", LMErr.NERR_Success, Netapi32.INSTANCE.NetShareAdd(null, // Use local computer
                2, shi.getPointer(), parm_err));

        assertEquals("Failed to delete share", LMErr.NERR_Success, Netapi32.INSTANCE.NetShareDel(null, shi.shi2_netname, 0));
    }

    private File createTempFolder() throws Exception {
        String folderPath = System.getProperty("java.io.tmpdir") + File.separatorChar + System.nanoTime();
        File file = new File(folderPath);
        file.mkdir();
        return file;
    }
}
