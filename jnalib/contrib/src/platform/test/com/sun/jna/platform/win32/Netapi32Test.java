/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import com.sun.jna.WString;
import com.sun.jna.platform.win32.LMAccess.GROUP_INFO_2;
import com.sun.jna.platform.win32.LMAccess.GROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.LOCALGROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

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
    	String currentUser = Advapi32Util.getUserName();
    	PointerByReference bufptr = new PointerByReference();
    	IntByReference entriesread = new IntByReference();
    	IntByReference totalentries = new IntByReference();
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserGetGroups(
    			null, currentUser, 0, bufptr, LMCons.MAX_PREFERRED_LENGTH, 
    			entriesread, totalentries));
    	GROUP_USERS_INFO_0 lgroup = new GROUP_USERS_INFO_0(bufptr.getValue());    	
    	GROUP_USERS_INFO_0[] lgroups = (GROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
        for (GROUP_USERS_INFO_0 localGroupInfo : lgroups) {
        	assertTrue(localGroupInfo.grui0_name.length() > 0);
        }
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()));
    }
    
    public void testNetUserGetLocalGroups() {
    	String currentUser = Advapi32Util.getUserName();
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
    			null, 1, userInfo, null));
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
    			null, userInfo.usri1_name.toString()));
    }
    
    public void testNetUserChangePassword() {
    	USER_INFO_1 userInfo = new USER_INFO_1();
    	userInfo.usri1_name = new WString("JNANetapi32TestUser");
    	userInfo.usri1_password = new WString("!JNAP$$Wrd0");
    	userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserAdd(
    			null, 1, userInfo, null));
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserChangePassword(
    			null, userInfo.usri1_name.toString(), userInfo.usri1_password.toString(),
    			"!JNAP%%Wrd1"));
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
    			null, userInfo.usri1_name.toString()));
    }    
    
    public void testNetUserDel() {
    	assertEquals(LMErr.NERR_UserNotFound, Netapi32.INSTANCE.NetUserDel(
    			null, "JNANetapi32TestUserDoesntExist"));
    }
}
