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

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

public class Secur32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Secur32Test.class);
    }
    
    public void testGetUserNameEx() {
    	IntByReference len = new IntByReference();
    	Secur32.INSTANCE.GetUserNameEx(
    			Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, null, len);
    	assertTrue(len.getValue() > 0);
    	char[] buffer = new char[len.getValue() + 1];
    	assertTrue(Secur32.INSTANCE.GetUserNameEx(
    			Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, buffer, len));
    	String username = Native.toString(buffer);
    	assertTrue(username.length() > 0);
    }
    
    public void testAcquireCredentialsHandle() {
    	CredHandle phCredential = new CredHandle();
    	TimeStamp ptsExpiry = new TimeStamp();
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
    			null, "Negotiate", new NativeLong(Sspi.SECPKG_CRED_OUTBOUND), null, null, null, 
    			null, phCredential, ptsExpiry));
    	assertTrue(phCredential.dwLower != null);
    	assertTrue(phCredential.dwUpper != null);
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(
    			phCredential));    	
    }
    
    public void testAcquireCredentialsHandleInvalidPackage() {
    	CredHandle phCredential = new CredHandle();
    	TimeStamp ptsExpiry = new TimeStamp();
    	assertEquals(W32Errors.SEC_E_SECPKG_NOT_FOUND, Secur32.INSTANCE.AcquireCredentialsHandle(
    			null, "PackageDoesntExist", new NativeLong(Sspi.SECPKG_CRED_OUTBOUND), null, null, null, 
    			null, phCredential, ptsExpiry));
    }
    
    public void testInitializeSecurityContext() {
    	CredHandle phCredential = new CredHandle();
    	TimeStamp ptsExpiry = new TimeStamp();
    	// acquire a credentials handle
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
    			null, "Negotiate", new NativeLong(Sspi.SECPKG_CRED_OUTBOUND), null, null, null, 
    			null, phCredential, ptsExpiry));
    	// initialize security context
    	CtxtHandle phNewContext = new CtxtHandle();
    	SecBufferDesc pbToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
    	NativeLongByReference pfContextAttr = new NativeLongByReference();
    	int rc = Secur32.INSTANCE.InitializeSecurityContext(phCredential, null, 
    			Advapi32Util.getUserName(), new NativeLong(Sspi.ISC_REQ_CONNECTION), new NativeLong(0), 
    			new NativeLong(Sspi.SECURITY_NATIVE_DREP), null, new NativeLong(0), phNewContext, pbToken, 
    			pfContextAttr, null);    	
    	assertTrue(rc == W32Errors.SEC_I_CONTINUE_NEEDED || rc == W32Errors.SEC_E_OK);
    	assertTrue(phNewContext.dwLower != null);
    	assertTrue(phNewContext.dwUpper != null);
    	assertTrue(pbToken.pBuffers[0].getBytes().length > 0);
    	// release 
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DeleteSecurityContext(
    			phNewContext));
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(
    			phCredential));
    }
    
    public void testAcceptSecurityToken() {
    	// client ----------- acquire outbound credential handle
    	CredHandle phClientCredential = new CredHandle();
    	TimeStamp ptsClientExpiry = new TimeStamp();
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
    			null, "Negotiate", new NativeLong(Sspi.SECPKG_CRED_OUTBOUND), null, null, null, 
    			null, phClientCredential, ptsClientExpiry));
    	// client ----------- security context
    	CtxtHandle phClientContext = new CtxtHandle();
    	NativeLongByReference pfClientContextAttr = new NativeLongByReference();
		// server ----------- acquire inbound credential handle
    	CredHandle phServerCredential = new CredHandle();
    	TimeStamp ptsServerExpiry = new TimeStamp();
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
    			null, "Negotiate", new NativeLong(Sspi.SECPKG_CRED_INBOUND), null, null, null, 
    			null, phServerCredential, ptsServerExpiry));
    	// server ----------- security context
		CtxtHandle phServerContext = new CtxtHandle();
    	SecBufferDesc pbServerToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
    	NativeLongByReference pfServerContextAttr = new NativeLongByReference();
    	while(true) {    		
        	// client ----------- initialize security context, produce a client token
    		// client token returned is always new
        	SecBufferDesc pbClientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        	// server token is empty the first time
    		int clientRc = Secur32.INSTANCE.InitializeSecurityContext(
    				phClientCredential, 
    				phClientContext.isNull() ? null : phClientContext, 
        			Advapi32Util.getUserName(), 
        			new NativeLong(Sspi.ISC_REQ_CONNECTION), 
        			new NativeLong(0), 
        			new NativeLong(Sspi.SECURITY_NATIVE_DREP), 
        			pbServerToken, 
        			new NativeLong(0), 
        			phClientContext, 
        			pbClientToken, 
        			pfClientContextAttr, 
        			null);    		
    		if (clientRc == W32Errors.SEC_E_OK)
    			break;    		
    		assertTrue(clientRc == W32Errors.SEC_I_CONTINUE_NEEDED);    		
        	// server ----------- accept security context, produce a server token
    		int serverRc = Secur32.INSTANCE.AcceptSecurityContext(phServerCredential, 
    				phServerContext.isNull() ? null : phServerContext, 
    				pbClientToken, 
    				new NativeLong(Sspi.ISC_REQ_CONNECTION), 
    				new NativeLong(Sspi.SECURITY_NATIVE_DREP), 
    				phServerContext,
    				pbServerToken, 
    				pfServerContextAttr, 
    				ptsServerExpiry);    		
    		assertTrue(serverRc == W32Errors.SEC_I_CONTINUE_NEEDED 
    				|| serverRc == W32Errors.SEC_E_OK);    		
    	}
    	// release server context
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DeleteSecurityContext(
    			phServerContext));
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(
    			phServerCredential));
    	// release client context
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DeleteSecurityContext(
    			phClientContext));
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(
    			phClientCredential));
    }
}
