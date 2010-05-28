/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.NativeLong;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.DsGetDC.DS_DOMAIN_TRUSTS;
import com.sun.jna.platform.win32.DsGetDC.PDOMAIN_CONTROLLER_INFO;
import com.sun.jna.platform.win32.DsGetDC.PDS_DOMAIN_TRUSTS;
import com.sun.jna.platform.win32.LMAccess.GROUP_INFO_2;
import com.sun.jna.platform.win32.LMAccess.GROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.LOCALGROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.platform.win32.NTSecApi.LSA_FOREST_TRUST_RECORD;
import com.sun.jna.platform.win32.NTSecApi.PLSA_FOREST_TRUST_INFORMATION;
import com.sun.jna.platform.win32.NTSecApi.PLSA_FOREST_TRUST_RECORD;
import com.sun.jna.platform.win32.Netapi32Util.User;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Netapi32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Netapi32Test.class);
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
    	for(int i = 0; i < 2; i++) {
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
    	userInfo.usri1_name = new WString("JNANetapi32TestUser");
    	userInfo.usri1_password = new WString("!JNAP$$Wrd0");
    	userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserAdd(
    			Kernel32Util.getComputerName(), 1, userInfo, null));
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
    			Kernel32Util.getComputerName(), userInfo.usri1_name.toString()));
    }
    
    public void testNetUserChangePassword() {
    	USER_INFO_1 userInfo = new USER_INFO_1();
    	userInfo.usri1_name = new WString("JNANetapi32TestUser");
    	userInfo.usri1_password = new WString("!JNAP$$Wrd0");
    	userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserAdd(
    			Kernel32Util.getComputerName(), 1, userInfo, null));
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
    	if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName)
    		return;
    	
        PDOMAIN_CONTROLLER_INFO.ByReference pdci = new PDOMAIN_CONTROLLER_INFO.ByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.DsGetDcName(
    			null, null, null, null, 0, pdci));
    	assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(
    			pdci.getPointer()));
    }
    
    public void testDsGetForestTrustInformation() {
    	if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName)
    		return;

    	String domainController = Netapi32Util.getDCName();    	
    	PLSA_FOREST_TRUST_INFORMATION.ByReference pfti = new PLSA_FOREST_TRUST_INFORMATION.ByReference();
    	assertEquals(W32Errors.NO_ERROR, Netapi32.INSTANCE.DsGetForestTrustInformation(
    			domainController, null, 0, pfti));
    	
    	assertTrue(pfti.fti.RecordCount.intValue() >= 0);
    	
    	for (PLSA_FOREST_TRUST_RECORD precord : pfti.fti.getEntries()) {
    		LSA_FOREST_TRUST_RECORD.UNION data = precord.tr.u;
			switch(precord.tr.ForestTrustType) {
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
    	if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName)
    		return;

    	NativeLongByReference domainCount = new NativeLongByReference();
    	PDS_DOMAIN_TRUSTS.ByReference domains = new PDS_DOMAIN_TRUSTS.ByReference();
    	assertEquals(W32Errors.NO_ERROR, Netapi32.INSTANCE.DsEnumerateDomainTrusts(
    			null, new NativeLong(DsGetDC.DS_DOMAIN_VALID_FLAGS), domains, domainCount));
    	
    	assertTrue(domainCount.getValue().intValue() >= 0);
    	
    	DS_DOMAIN_TRUSTS[] trusts = domains.getTrusts(domainCount.getValue().intValue());
    	for(DS_DOMAIN_TRUSTS trust : trusts) {
			assertTrue(trust.NetbiosDomainName.length() > 0);
			assertTrue(trust.DnsDomainName.length() > 0);
			assertTrue(Advapi32.INSTANCE.IsValidSid(trust.DomainSid));
			assertTrue(Advapi32Util.convertSidToStringSid(trust.DomainSid).startsWith("S-"));
			assertTrue(Ole32Util.getStringFromGUID(trust.DomainGuid).startsWith("{"));
    	}
    	
    	assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(
    			domains.getPointer()));   	    	
    }
}
