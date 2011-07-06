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

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.PSecPkgInfo;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Sspi.SecPkgInfo;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
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
    
    public void testAcceptSecurityContext() {
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
    	SecBufferDesc pbServerToken = null;
    	NativeLongByReference pfServerContextAttr = new NativeLongByReference();
    	int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
    	int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
    	do {
        	// client ----------- initialize security context, produce a client token
    		// client token returned is always new
        	SecBufferDesc pbClientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        	if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
	        	// server token is empty the first time
	        	SecBufferDesc pbServerTokenCopy = pbServerToken == null 
	        		? null : new SecBufferDesc(Sspi.SECBUFFER_TOKEN, pbServerToken.getBytes());
	        	clientRc = Secur32.INSTANCE.InitializeSecurityContext(
	    				phClientCredential, 
	    				phClientContext.isNull() ? null : phClientContext, 
	        			Advapi32Util.getUserName(), 
	        			new NativeLong(Sspi.ISC_REQ_CONNECTION), 
	        			new NativeLong(0), 
	        			new NativeLong(Sspi.SECURITY_NATIVE_DREP), 
	        			pbServerTokenCopy, 
	        			new NativeLong(0), 
	        			phClientContext, 
	        			pbClientToken, 
	        			pfClientContextAttr, 
	        			null);    		
	    		assertTrue(clientRc == W32Errors.SEC_I_CONTINUE_NEEDED || clientRc == W32Errors.SEC_E_OK);
        	}
        	// server ----------- accept security context, produce a server token
    		if (serverRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
	    		pbServerToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
	    		SecBufferDesc pbClientTokenByValue = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, pbClientToken.getBytes());
	    		serverRc = Secur32.INSTANCE.AcceptSecurityContext(phServerCredential, 
	    				phServerContext.isNull() ? null : phServerContext, 
	    				pbClientTokenByValue,
	    				new NativeLong(Sspi.ISC_REQ_CONNECTION), 
	    				new NativeLong(Sspi.SECURITY_NATIVE_DREP), 
	    				phServerContext,
	    				pbServerToken, 
	    				pfServerContextAttr, 
	    				ptsServerExpiry);    		
	    		assertTrue(serverRc == W32Errors.SEC_I_CONTINUE_NEEDED || serverRc == W32Errors.SEC_E_OK);    		
    		}
    	} while(serverRc != W32Errors.SEC_E_OK || clientRc != W32Errors.SEC_E_OK);
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
    
    public void testImpersonateRevertSecurityContext() {
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
    	SecBufferDesc pbServerToken = null;
    	NativeLongByReference pfServerContextAttr = new NativeLongByReference();
    	int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
    	int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
    	do {
        	// client ----------- initialize security context, produce a client token
    		// client token returned is always new
        	SecBufferDesc pbClientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        	if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
	        	// server token is empty the first time
	        	SecBufferDesc pbServerTokenCopy = pbServerToken == null 
	        		? null : new SecBufferDesc(Sspi.SECBUFFER_TOKEN, pbServerToken.getBytes());
	        	clientRc = Secur32.INSTANCE.InitializeSecurityContext(
	    				phClientCredential, 
	    				phClientContext.isNull() ? null : phClientContext, 
	        			Advapi32Util.getUserName(), 
	        			new NativeLong(Sspi.ISC_REQ_CONNECTION), 
	        			new NativeLong(0), 
	        			new NativeLong(Sspi.SECURITY_NATIVE_DREP), 
	        			pbServerTokenCopy, 
	        			new NativeLong(0), 
	        			phClientContext, 
	        			pbClientToken, 
	        			pfClientContextAttr, 
	        			null);    		
	    		assertTrue(clientRc == W32Errors.SEC_I_CONTINUE_NEEDED || clientRc == W32Errors.SEC_E_OK);
        	}
        	// server ----------- accept security context, produce a server token
    		if (serverRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
	    		pbServerToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
	    		SecBufferDesc pbClientTokenByValue = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, pbClientToken.getBytes());
	    		serverRc = Secur32.INSTANCE.AcceptSecurityContext(phServerCredential, 
	    				phServerContext.isNull() ? null : phServerContext, 
	    				pbClientTokenByValue,
	    				new NativeLong(Sspi.ISC_REQ_CONNECTION), 
	    				new NativeLong(Sspi.SECURITY_NATIVE_DREP), 
	    				phServerContext,
	    				pbServerToken, 
	    				pfServerContextAttr, 
	    				ptsServerExpiry);    		
	    		assertTrue(serverRc == W32Errors.SEC_I_CONTINUE_NEEDED || serverRc == W32Errors.SEC_E_OK);    		
    		}
    	} while(serverRc != W32Errors.SEC_E_OK || clientRc != W32Errors.SEC_E_OK);
    	// impersonate
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.ImpersonateSecurityContext(
    			phServerContext));
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.RevertSecurityContext(
    			phServerContext));
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
    
    public void testEnumerateSecurityPackages() {
    	IntByReference pcPackages = new IntByReference();
    	PSecPkgInfo.ByReference pPackageInfo = new PSecPkgInfo.ByReference();
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.EnumerateSecurityPackages(
    			pcPackages, pPackageInfo));
    	SecPkgInfo.ByReference[] packagesInfo = pPackageInfo.toArray(
    			pcPackages.getValue());    	
    	for(SecPkgInfo.ByReference packageInfo : packagesInfo) {
    		assertTrue(packageInfo.Name.length() > 0);
    		assertTrue(packageInfo.Comment.length() >= 0);
    	}
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeContextBuffer(
    			pPackageInfo.getPointer()));
    }
    
    public void testQuerySecurityContextToken() {
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
    	int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
    	int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
    	do {
    		// client token returned is always new
        	SecBufferDesc pbClientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        	// client ----------- initialize security context, produce a client token
    		if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
	        	// server token is empty the first time
	    		clientRc = Secur32.INSTANCE.InitializeSecurityContext(
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
	    		assertTrue(clientRc == W32Errors.SEC_I_CONTINUE_NEEDED || clientRc == W32Errors.SEC_E_OK);    		
    		}    		
        	// server ----------- accept security context, produce a server token
    		if (serverRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
	    		serverRc = Secur32.INSTANCE.AcceptSecurityContext(phServerCredential, 
	    				phServerContext.isNull() ? null : phServerContext, 
	    				pbClientToken, 
	    				new NativeLong(Sspi.ISC_REQ_CONNECTION), 
	    				new NativeLong(Sspi.SECURITY_NATIVE_DREP), 
	    				phServerContext,
	    				pbServerToken, 
	    				pfServerContextAttr, 
	    				ptsServerExpiry);
	    		assertTrue(serverRc == W32Errors.SEC_I_CONTINUE_NEEDED || serverRc == W32Errors.SEC_E_OK);
    		}    		
    	} while(serverRc != W32Errors.SEC_E_OK || clientRc != W32Errors.SEC_E_OK);    	
    	// query security context token
    	HANDLEByReference phContextToken = new HANDLEByReference();
    	assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.QuerySecurityContextToken(
    			phServerContext, phContextToken));
    	// release security context token
    	assertTrue(Kernel32.INSTANCE.CloseHandle(phContextToken.getValue()));
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
