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

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.PSecPkgInfo;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LUID;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Secur32.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface Secur32 extends StdCallLibrary {
	Secur32 INSTANCE = (Secur32) Native.loadLibrary(
			"Secur32", Secur32.class, W32APIOptions.UNICODE_OPTIONS);
	
	/**
	 * Specifies a format for a directory service object name.
	 * http://msdn.microsoft.com/en-us/library/ms724268(VS.85).aspx
	 */
	public abstract class EXTENDED_NAME_FORMAT {
		public static final int NameUnknown = 0;
		public static final int NameFullyQualifiedDN = 1;
		public static final int NameSamCompatible = 2;
		public static final int NameDisplay = 3;
		public static final int NameUniqueId = 6;
		public static final int NameCanonical = 7;
		public static final int NameUserPrincipal = 8;
		public static final int NameCanonicalEx = 9;
		public static final int NameServicePrincipal = 10;
		public static final int NameDnsDomain = 12;
	};
	
	/**
	 * Retrieves the name of the user or other security principal associated with 
	 * the calling thread. You can specify the format of the returned name.
	 * @param nameFormat The format of the name. 
	 * @param lpNameBuffer A pointer to a buffer that receives the name in the specified format. 
	 * @param len On input, the size of the buffer, on output the number of characters copied into the buffer, not including the terminating null character.
	 * @return True if the function succeeds. False otherwise.
	 */
	public boolean GetUserNameEx(int nameFormat, char[] lpNameBuffer, IntByReference len);

	/**
	 * The AcquireCredentialsHandle function acquires a handle to preexisting credentials 
	 * of a security principal. This handle is required by the AcceptSecurityContext 
	 * and InitializeSecurityContext functions. These can be either preexisting credentials, 
	 * which are established through a system logon that is not described here, or the 
	 * caller can provide alternative credentials.
	 * @param pszPrincipal
	 *  A pointer to a null-terminated string that specifies the name of the principal whose 
	 *  credentials the handle will reference.
	 * @param pszPackage
	 *   A pointer to a null-terminated string that specifies the name of the security package 
	 *   with which these credentials will be used.
	 * @param fCredentialUse
	 *  A flag that indicates how these credentials will be used.
	 * @param pvLogonID
	 *  A pointer to a locally unique identifier (LUID) that identifies the user. 
	 * @param pAuthData
	 *  A pointer to package-specific data. This parameter can be NULL, which indicates 
	 *  that the default credentials for that package must be used. To use supplied 
	 *  credentials, pass a SEC_WINNT_AUTH_IDENTITY structure that includes those credentials 
	 *  in this parameter.
	 * @param pGetKeyFn
	 *  This parameter is not used and should be set to NULL. 
	 * @param pvGetKeyArgument
	 *  This parameter is not used and should be set to NULL.
	 * @param phCredential
	 *  A pointer to a CredHandle structure to receive the credential handle. 
	 * @param ptsExpiry
	 *  A pointer to a TimeStamp structure that receives the time at which the returned 
	 *  credentials expire. The value returned in this TimeStamp structure depends on 
	 *  the security package. The security package must return this value in local time.
	 * @return
	 *  If the function succeeds, the function returns one of the SEC_I_ success codes.
	 *  If the function fails, the function returns one of the SEC_E_ error codes.
	 */
	public int AcquireCredentialsHandle(String pszPrincipal, String pszPackage,
			NativeLong fCredentialUse, LUID pvLogonID,
			Pointer pAuthData, Pointer pGetKeyFn, // TODO: SEC_GET_KEY_FN
			Pointer pvGetKeyArgument, CredHandle phCredential, 
			TimeStamp ptsExpiry);
	
	/**
	 * The InitializeSecurityContext function initiates the client side, outbound security 
	 * context from a credential handle. The function is used to build a security context 
	 * between the client application and a remote peer. InitializeSecurityContext returns 
	 * a token that the client must pass to the remote peer, which the peer in turn submits 
	 * to the local security implementation through the AcceptSecurityContext call. The 
	 * token generated should be considered opaque by all callers.
	 * 
	 * Typically, the InitializeSecurityContext function is called in a loop until a 
	 * sufficient security context is established.
	 * 
	 * @param phCredential
	 *  A handle to the credentials returned by AcquireCredentialsHandle. This handle is 
	 *  used to build the security context. The InitializeSecurityContext function requires 
	 *  at least OUTBOUND credentials. 
	 * @param phContext
	 *  A pointer to a CtxtHandle structure. On the first call to InitializeSecurityContext,
	 *  this pointer is NULL. On the second call, this parameter is a pointer to the handle 
	 *  to the partially formed context returned in the phNewContext parameter by the first 
	 *  call.
	 * @param pszTargetName
	 *  A pointer to a null-terminated string that indicates the target of the context. 
	 *  The string contents are security-package specific.
	 * @param fContextReq
	 *  Bit flags that indicate requests for the context. Not all packages can support all 
	 *  requirements. Flags used for this parameter are prefixed with ISC_REQ_, for example,
	 *  ISC_REQ_DELEGATE. 
	 * @param Reserved1
	 *  This parameter is reserved and must be set to zero.
	 * @param TargetDataRep
	 *  The data representation, such as byte ordering, on the target. This parameter can be 
	 *  either SECURITY_NATIVE_DREP or SECURITY_NETWORK_DREP.
	 * @param pInput
	 *  A pointer to a SecBufferDesc structure that contains pointers to the buffers supplied 
	 *  as input to the package. The pointer must be NULL on the first call to the function. 
	 *  On subsequent calls to the function, it is a pointer to a buffer allocated with enough 
	 *  memory to hold the token returned by the remote peer.
	 * @param Reserved2
	 *  This parameter is reserved and must be set to zero. 
	 * @param phNewContext
	 *  A pointer to a CtxtHandle structure. On the first call to InitializeSecurityContext, 
	 *  this pointer receives the new context handle. On the second call, phNewContext can be 
	 *  the same as the handle specified in the phContext parameter.
	 * @param pOutput
	 *  A pointer to a SecBufferDesc structure that contains pointers to the SecBuffer structure 
	 *  that receives the output data. If a buffer was typed as SEC_READWRITE in the input, it 
	 *  will be there on output. The system will allocate a buffer for the security token if 
	 *  requested (through ISC_REQ_ALLOCATE_MEMORY) and fill in the address in the buffer 
	 *  descriptor for the security token.
	 * @param pfContextAttr
	 *  A pointer to a variable to receive a set of bit flags that indicate the attributes of 
	 *  the established context. Flags used for this parameter are prefixed with ISC_RET, 
	 *  such as ISC_RET_DELEGATE.
	 * @param ptsExpiry
	 *  A pointer to a TimeStamp structure that receives the expiration time of the context.
	 *  It is recommended that the security package always return this value in local time. 
	 *  This parameter is optional and NULL should be passed for short-lived clients.
	 * @return
	 *  If the function succeeds, the function returns one of the SEC_I_ success codes.
	 *  If the function fails, the function returns one of the SEC_E_ error codes.
	 */
	public int InitializeSecurityContext(CredHandle phCredential, CtxtHandle phContext,
			String pszTargetName, NativeLong fContextReq, NativeLong Reserved1,
			NativeLong TargetDataRep, SecBufferDesc pInput, NativeLong Reserved2,
			CtxtHandle phNewContext, SecBufferDesc pOutput, NativeLongByReference pfContextAttr,
			TimeStamp ptsExpiry);
	
	/**
	 * The DeleteSecurityContext function deletes the local data structures associated 
	 * with the specified security context.
	 * @param phContext
	 *  Handle of the security context to delete. 
	 * @return
	 *  If the function succeeds, the return value is SEC_E_OK.
	 *  If the function fails, the return value is SEC_E_INVALID_HANDLE;
	 */
	public int DeleteSecurityContext(CtxtHandle phContext);
	
	/**
	 * The FreeCredentialsHandle function notifies the security system that the 
	 * credentials are no longer needed. An application calls this function to free 
	 * the credential handle acquired in the call to the AcquireCredentialsHandle 
	 * function. When all references to this credential set have been removed, the 
	 * credentials themselves can be removed.
	 * @param phCredential
	 *  A pointer to the credential handle obtained by using the AcquireCredentialsHandle
	 *  function. 
	 * @return
	 *  If the function succeeds, the return value is SEC_E_OK.
	 *  If the function fails, the return value is SEC_E_INVALID_HANDLE;
	 */
	public int FreeCredentialsHandle(CredHandle phCredential);
	
	/**
	 * The AcceptSecurityContext function enables the server component of a transport 
	 * application to establish a security context between the server and a remote client.
	 * The remote client uses the InitializeSecurityContext function to start the process 
	 * of establishing a security context. The server can require one or more reply tokens
	 * from the remote client to complete establishing the security context.
	 * @param phCredential
	 *  A handle to the credentials of the server. The server calls the AcquireCredentialsHandle 
	 *  function with either the SECPKG_CRED_INBOUND or SECPKG_CRED_BOTH flag set to retrieve 
	 *  this handle. 
	 * @param phContext
	 *  A pointer to a CtxtHandle structure. On the first call to AcceptSecurityContext, 
	 *  this pointer is NULL. On subsequent calls, phContext is the handle to the partially 
	 *  formed context that was returned in the phNewContext parameter by the first call. 
	 * @param pInput
	 *  A pointer to a SecBufferDesc structure generated by a client call to 
	 *  InitializeSecurityContext that contains the input buffer descriptor. 
	 * @param fContextReq
	 *  Bit flags that specify the attributes required by the server to establish the 
	 *  context. Bit flags can be combined by using bitwise-OR operations.
	 * @param TargetDataRep
	 *  The data representation, such as byte ordering, on the target. This parameter can 
	 *  be either SECURITY_NATIVE_DREP or SECURITY_NETWORK_DREP.
	 * @param phNewContext
	 *  A pointer to a CtxtHandle structure. On the first call to AcceptSecurityContext, 
	 *  this pointer receives the new context handle. On subsequent calls, phNewContext 
	 *  can be the same as the handle specified in the phContext parameter. 
	 * @param pOutput
	 *  A pointer to a SecBufferDesc structure that contains the output buffer descriptor. 
	 *  This buffer is sent to the client for input into additional calls to 
	 *  InitializeSecurityContext. An output buffer may be generated even if the function 
	 *  returns SEC_E_OK. Any buffer generated must be sent back to the client application. 
	 * @param pfContextAttr
	 *  A pointer to a variable that receives a set of bit flags that indicate the 
	 *  attributes of the established context. For a description of the various attributes, 
	 *  see Context Requirements. Flags used for this parameter are prefixed with ASC_RET, 
	 *  for example, ASC_RET_DELEGATE.
	 * @param ptsTimeStamp
	 *  A pointer to a TimeStamp structure that receives the expiration time of the context. 
	 * @return
	 *  This function returns one of SEC_* values.
	 */
	public int AcceptSecurityContext(CredHandle phCredential, CtxtHandle phContext,
			SecBufferDesc pInput, NativeLong fContextReq, NativeLong TargetDataRep,
			CtxtHandle phNewContext, SecBufferDesc pOutput, NativeLongByReference pfContextAttr,
			TimeStamp ptsTimeStamp);

	/**
	 * The EnumerateSecurityPackages function returns an array of SecPkgInfo structures that 
	 * describe the security packages available to the client.
	 * @param pcPackages
	 *  A pointer to a ULONG variable that receives the number of packages returned.
	 * @param ppPackageInfo
	 *  A pointer to a variable that receives a pointer to an array of SecPkgInfo structures. 
	 *  Each structure contains information from the security support provider (SSP) that 
	 *  describes a security package that is available within that SSP. 
	 * @return
	 *  If the function succeeds, the function returns SEC_E_OK.
	 *  If the function fails, it returns a nonzero error code.
	 */
	public int EnumerateSecurityPackages(IntByReference pcPackages, 
			PSecPkgInfo.ByReference ppPackageInfo);
	
	/**
	 * The FreeContextBuffer function enables callers of security package functions to free a memory 
	 * buffer that was allocated by the security package as a result of calls to InitializeSecurityContext 
	 * and AcceptSecurityContext.
	 * @param buffer
	 *  A pointer to memory allocated by the security package.
	 * @return
	 *  If the function succeeds, the function returns SEC_E_OK.
	 *  If the function fails, it returns a nonzero error code.
	 */
	public int FreeContextBuffer(Pointer buffer);
	
	/**
	 * The QuerySecurityContextToken function obtains the access token for a client security context
	 * and uses it directly.
	 * @param phContext
	 *  Handle of the context to query. 
	 * @param phToken
	 *  Returned handle to the access token. 
	 * @return
	 *  If the function succeeds, the function returns SEC_E_OK.
	 *  If the function fails, it returns a nonzero error code. One possible error code return is 
	 *  SEC_E_INVALID_HANDLE.
	 */
	public int QuerySecurityContextToken(CtxtHandle phContext, 
			HANDLEByReference phToken);
	
	/**
	 * The ImpersonateSecurityContext function allows a server to impersonate a client by using 
	 * a token previously obtained by a call to AcceptSecurityContext or QuerySecurityContextToken. 
	 * This function allows the application server to act as the client, and thus all necessary 
	 * access controls are enforced.
	 * @param phContext
	 *  The handle of the context to impersonate. This handle must have been obtained by a call 
	 *  to the AcceptSecurityContext function.
	 * @return
	 *  If the function succeeds, the function returns SEC_E_OK.
	 *  If the function fails, it returns a SEC_E_INVALID_HANDLE, SEC_E_NO_IMPERSONATION or 
	 *  SEC_E_UNSUPPORTED_FUNCTION error code.
	 */
	public int ImpersonateSecurityContext(CtxtHandle phContext);
	
	/**
	 * Allows a security package to discontinue the impersonation of the caller and restore its 
	 * own security context.
	 * @param phContext
	 *  Handle of the security context being impersonated. This handle must have been obtained in 
	 *  the call to the AcceptSecurityContext function and used in the call to the 
	 *  ImpersonateSecurityContext function.
	 * @return
	 *  If the function succeeds, the return value is SEC_E_OK.
	 *  If the function fails, the return value can be either SEC_E_INVALID_HANDLE or SEC_E_UNSUPPORTED_FUNCTION.
	 */
	public int RevertSecurityContext(CtxtHandle phContext);
}
