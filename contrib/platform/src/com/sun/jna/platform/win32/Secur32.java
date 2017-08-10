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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.PSecPkgInfo;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LUID;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Secur32.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface Secur32 extends StdCallLibrary {
    Secur32 INSTANCE = Native.loadLibrary("Secur32", Secur32.class, W32APIOptions.DEFAULT_OPTIONS);
	
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
    boolean GetUserNameEx(int nameFormat, char[] lpNameBuffer, IntByReference len);

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
     *  credentials, pass a {@link com.sun.jna.platform.win32.Sspi.SEC_WINNT_AUTH_IDENTITY} 
     *  structure that includes those credentials in this parameter.
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
    int AcquireCredentialsHandle(String pszPrincipal, String pszPackage,
                                        int fCredentialUse, LUID pvLogonID,
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
    int InitializeSecurityContext(CredHandle phCredential, CtxtHandle phContext,
                                         String pszTargetName, int fContextReq, int Reserved1,
                                         int TargetDataRep, SecBufferDesc pInput, int Reserved2,
                                         CtxtHandle phNewContext, SecBufferDesc pOutput, IntByReference pfContextAttr,
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
    int DeleteSecurityContext(CtxtHandle phContext);
	
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
    int FreeCredentialsHandle(CredHandle phCredential);

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
    int AcceptSecurityContext(CredHandle phCredential, CtxtHandle phContext,
                                     SecBufferDesc pInput, int fContextReq, int TargetDataRep,
                                     CtxtHandle phNewContext, SecBufferDesc pOutput, IntByReference pfContextAttr,
                                     TimeStamp ptsTimeStamp);
    
    /**
     * The EnumerateSecurityPackages function returns an array of SecPkgInfo structures that 
     * describe the security packages available to the client.
     * @param pcPackages
     *  A pointer to a int variable that receives the number of packages returned.
     * @param ppPackageInfo
     *  A pointer to a variable that receives a pointer to an array of SecPkgInfo structures. 
     *  Each structure contains information from the security support provider (SSP) that 
     *  describes a security package that is available within that SSP. 
     * @return
     *  If the function succeeds, the function returns SEC_E_OK.
     *  If the function fails, it returns a nonzero error code.
     */
    int EnumerateSecurityPackages(IntByReference pcPackages,  PSecPkgInfo ppPackageInfo);
	
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
    int FreeContextBuffer(Pointer buffer);
	
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
    int QuerySecurityContextToken(CtxtHandle phContext, HANDLEByReference phToken);
	
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
    int ImpersonateSecurityContext(CtxtHandle phContext);
	
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
    int RevertSecurityContext(CtxtHandle phContext);
	
    /**
     * Enables a transport application to query a security package for certain
     * attributes of a security context.
     * 
     * @param phContext
     *  A handle to the security context to be queried.
     * @param ulAttribute
     *  Specifies the attribute of the context to be returned. This
     *  parameter can be one of the SECPKG_ATTR_* values defined in
     *  {@link Sspi}.
     * @param pBuffer
     *  A pointer to a structure that receives the attributes. The
     *  type of structure pointed to depends on the value specified in
     *  the ulAttribute parameter.
     * @return
     *  If the function succeeds, the return value is SEC_E_OK.
     *  If the function fails, the return value is a nonzero error code.
     */
    int QueryContextAttributes(CtxtHandle phContext, int ulAttribute, Structure pBuffer);
    
    /**
     * Retrieves the attributes of a credential, such as the name associated
     * with the credential. The information is valid for any security context
     * created with the specified credential.
     *
     * @param phCredential A handle of the credentials to be queried.
     * @param ulAttribute Specifies the attribute of the context to be returned.
     *                    This parameter can be one of the SECPKG_ATTR_* values
     *                    defined in {@link Sspi}.
     * @param pBuffer     A pointer to a structure that receives the attributes.
     *                    The type of structure pointed to depends on the value
     *                    specified in the ulAttribute parameter.
     * @return If the function succeeds, the return value is SEC_E_OK. If the
     *         function fails, the return value is a nonzero error code.
     */
    int QueryCredentialsAttributes(Sspi.CredHandle phCredential, int ulAttribute, Structure pBuffer);
    
    /**
     * Retrieves information about a specified security package. This
     * information includes the bounds on sizes of authentication information,
     * credentials, and contexts.
     *
     * @param pszPackageName Name of the security package.
     * @param ppPackageInfo  Variable that receives a pointer to a SecPkgInfo
     *                       structure containing information about the
     *                       specified security package.
     * @return  If the function succeeds, the return value is SEC_E_OK.
     * If the function fails, the return value is a nonzero error code.
     */
    int QuerySecurityPackageInfo(String pszPackageName, Sspi.PSecPkgInfo ppPackageInfo);
    
    /**
     * EncryptMessage (Kerberos) function
     * 
     * <p>
     * The EncryptMessage (Kerberos) function encrypts a message to provide
     * privacy. EncryptMessage (Kerberos) allows an application to choose among
     * cryptographic algorithms supported by the chosen mechanism. The
     * EncryptMessage (Kerberos) function uses the security context referenced
     * by the context handle. Some packages do not have messages to be encrypted
     * or decrypted but rather provide an integrity hash that can be
     * checked.</p>
     *
     * @param phContext A handle to the security context to be used to encrypt
     *                  the message.
     * @param fQOP      Package-specific flags that indicate the quality of
     *                  protection. A security package can use this parameter to
     *                  enable the selection of cryptographic algorithms. This
     *                  parameter can be the following flag:
     *                  {@link Sspi#SECQOP_WRAP_NO_ENCRYPT}.
     * @param pMessage  A pointer to a SecBufferDesc structure. On input, the
     *                  structure references one or more SecBuffer structures
     *                  that can be of type SECBUFFER_DATA. That buffer contains
     *                  the message to be encrypted. The message is encrypted in
     *                  place, overwriting the original contents of the
     *                  structure.
     *
     * <p>
     * The function does not process buffers with the SECBUFFER_READONLY
     * attribute.</p>
     *
     * <p>
     * The length of the SecBuffer structure that contains the message must be
     * no greater than cbMaximumMessage, which is obtained from the
     * QueryContextAttributes (Kerberos) (SECPKG_ATTR_STREAM_SIZES)
     * function.</p>
     *
     * <p>
     * Applications that do not use SSL must supply a SecBuffer of type
     * SECBUFFER_PADDING.</p>
     * @param MessageSeqNo The sequence number that the transport application
     *                     assigned to the message. If the transport application
     *                     does not maintain sequence numbers, this parameter
     *                     must be zero.
     * @return If the function succeeds, the function returns SEC_E_OK.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375385(v=vs.85).aspx">MSDN Entry</a>
     */
    int EncryptMessage(CtxtHandle phContext, int fQOP, SecBufferDesc pMessage, int MessageSeqNo);
    
    /**
     * VerifySignature function.
     *
     * <p>
     * Verifies that a message signed by using the MakeSignature function was
     * received in the correct sequence and has not been modified.</p>
     *
     * <p>
     * <strong>Warning</strong></p>
     *
     * <p>
     * The VerifySignature function will fail if the message was signed using
     * the RsaSignPssSha512 algorithm on a different version of Windows. For
     * example, a message that was signed by calling the MakeSignature function
     * on Windows 8 will cause the VerifySignature function on Windows 8.1 to
     * fail.</p>
     *
     * @param phContext    A handle to the security context to use for the
     *                     message.
     * @param pMessage     Pointer to a SecBufferDesc structure that references
     *                     a set of SecBuffer structures that contain the
     *                     message and signature to verify. The signature is in
     *                     a SecBuffer structure of type SECBUFFER_TOKEN.
     * @param MessageSeqNo Specifies the sequence number expected by the
     *                     transport application, if any. If the transport
     *                     application does not maintain sequence numbers, this
     *                     parameter is zero.
     * @param pfQOP        Pointer to a ULONG variable that receives
     *                     package-specific flags that indicate the quality of
     *                     protection.
     *
     *                      <p>Some security packages ignore this parameter.</p>
     *
     * @return If the function verifies that the message was received in the
     *         correct sequence and has not been modified, the return value is
     *         SEC_E_OK.
     *
     * <p>
     * If the function determines that the message is not correct according to
     * the information in the signature, the return value can be one of the
     * following error codes.</p>
     * 
     * <table>
     * <tr><th>Return code</th><th>Description</th></tr>
     * <tr><td>SEC_E_OUT_OF_SEQUENCE</td><td>The message was not received in the
     * correct sequence.</td></tr>
     * <tr><td>SEC_E_MESSAGE_ALTERED</td><td>The message has been
     * altered.</td></tr>
     * <tr><td>SEC_E_INVALID_HANDLE</td><td>The context handle specified by
     * phContext is not valid.</td></tr>
     * <tr><td>SEC_E_INVALID_TOKEN</td><td>pMessage did not contain a valid
     * SECBUFFER_TOKEN buffer, or contained too few buffers.</td></tr>
     * <tr><td>SEC_E_QOP_NOT_SUPPORTED</td><td>The quality of protection
     * negotiated between the client and server did not include integrity
     * checking.</td></tr>
     * </table>
     */
    int VerifySignature(CtxtHandle phContext, SecBufferDesc pMessage, int MessageSeqNo, IntByReference pfQOP);
    
    /**
     * MakeSignature function.
     * 
     * <p>
     * The MakeSignature function generates a cryptographic checksum of the
     * message, and also includes sequencing information to prevent message loss
     * or insertion. MakeSignature allows the application to choose between
     * several cryptographic algorithms, if supported by the chosen mechanism.
     * The MakeSignature function uses the security context referenced by the
     * context handle.</p>
     *
     * <p>
     * <strong>Remarks</strong></p>
     *
     * <p>
     * Remarks</p>
     *<p>
     * The MakeSignature function generates a signature that is based on the
     * message and the session key for the context.</p>
     *<p>
     * The VerifySignature function verifies the messages signed by the
     * MakeSignature function.</p>
     *<p>
     * If the transport application created the security context to support
     * sequence detection and the caller provides a sequence number, the
     * function includes this information in the signature. This protects
     * against reply, insertion, and suppression of messages. The security
     * package incorporates the sequence number passed down from the transport
     * application.</p>
     *
     * @param phContext    A handle to the security context to use to sign the
     *                     message.
     * @param fQOP         Package-specific flags that indicate the quality of
     *                     protection. A security package can use this parameter
     *                     to enable the selection of cryptographic algorithms.
     * <p>
     * When using the Digest SSP, this parameter must be set to zero.</p>
     *
     * @param pMessage     A pointer to a SecBufferDesc structure. On input, the
     *                     structure references one or more SecBuffer structures
     *                     that contain the message to be signed. The function
     *                     does not process buffers with the
     *                     SECBUFFER_READONLY_WITH_CHECKSUM attribute.
     *
     * <p>
     * The SecBufferDesc structure also references a SecBuffer structure of type
     * SECBUFFER_TOKEN that receives the signature.</p>
     * <p>
     * When the Digest SSP is used as an HTTP authentication protocol, the
     * buffers should be configured as follows.</p>
     * <table>
     * <tr><th>Buffer #/buffer type</th><th>Meaning</th></tr>
     * <tr><td>0 / SECBUFFER_TOKEN</td><td>Empty.</td></tr>
     * <tr><td>1 / SECBUFFER_PKG_PARAMS</td><td>Method.</td></tr>
     * <tr><td>2 / SECBUFFER_PKG_PARAMS</td><td>URL.</td></tr>
     * <tr><td>3 / SECBUFFER_PKG_PARAMS</td><td>HEntity. For more information,
     * see Input Buffers for the Digest Challenge Response.</td></tr>
     * <tr><td>4 / SECBUFFER_PADDING</td><td>Empty. Receives the
     * signature.</td></tr>
     * </table>
     *<p>
     * When the Digest SSP is used as an SASL mechanism, the buffers should be
     * configured as follows.</p>
     *<table>
     * <tr><th>Buffer #/buffer type</th><th>Meaning</th></tr>
     * <tr><td>0 / SECBUFFER_TOKEN</td><td>Empty. Receives the signature. This
     * buffer must be large enough to hold the largest possible signature.
     * Determine the size required by calling the QueryContextAttributes
     * (General) function and specifying SECPKG_ATTR_SIZES. Check the returned
     * SecPkgContext_Sizes structure member cbMaxSignature.</td></tr>
     * <tr><td>1 / SECBUFFER_DATA</td><td>Message to be signed.</td></tr>
     * <tr><td>2 / SECBUFFER_PADDING</td><td>Empty.</td></tr>
     * </table>
     * @param MessageSeqNo      *
     *                     The sequence number that the transport application
     *                     assigned to the message. If the transport application
     *                     does not maintain sequence numbers, this parameter is
     *                     zero.
     *
     * <p>
     * When using the Digest SSP, this parameter must be set to zero. The Digest
     * SSP manages sequence numbering internally.</p>
     *
     * @return If the function succeeds, the function returns SEC_E_OK.
     *
     * <p>
     * If the function fails, it returns one of the following error codes.</p>
     *
     * <table>
     * <tr><th>Return code</th><th>Description</th>
     * <tr><td>SEC_I_RENEGOTIATE</td><td>The remote party requires a new
     * handshake sequence or the application has just initiated a shutdown.
     * Return to the negotiation loop and call AcceptSecurityContext (General)
     * or InitializeSecurityContext (General) again. An empty input buffer is
     * passed in the first call.</td></tr>
     * <tr><td>SEC_E_INVALID_HANDLE</td><td>The context handle specified by
     * phContext is not valid.</td></tr>
     * <tr><td>SEC_E_INVALID_TOKEN</td><td>pMessage did not contain a valid
     * SECBUFFER_TOKEN buffer or contained too few buffers.</td></tr>
     * <tr><td>SEC_E_OUT_OF_SEQUENCE</td><td>The nonce count is out of
     * sequence.</td></tr>
     * <tr><td>SEC_E_NO_AUTHENTICATING_AUTHORITY</td><td>The security context
     * (phContext) must be revalidated.</td></tr>
     * <tr><td>STATUS_INVALID_PARAMETER</td><td>The nonce count is not
     * numeric.</td></tr>
     * <tr><td>SEC_E_QOP_NOT_SUPPORTED</td><td>The quality of protection
     * negotiated between the client and server did not include integrity
     * checking.</td></tr>
     * </table>
     */
    int MakeSignature(CtxtHandle phContext, int fQOP, SecBufferDesc pMessage, int MessageSeqNo);
    
    /**
     * DecryptMessage (Kerberos) function
     *
     * <p>
     * The DecryptMessage (Kerberos) function decrypts a message. Some packages
     * do not encrypt and decrypt messages but rather perform and check an
     * integrity hash.</p>
     *
     * @param phContext    A handle to the security context to be used to
     *                     encrypt the message.
     * @param pMessage     A pointer to a SecBufferDesc structure. On input, the
     *                     structure references one or more SecBuffer structures
     *                     that may be of type SECBUFFER_DATA. The buffer
     *                     contains the encrypted message. The encrypted message
     *                     is decrypted in place, overwriting the original
     *                     contents of its buffer.
     * @param MessageSeqNo The sequence number expected by the transport
     *                     application, if any. If the transport application
     *                     does not maintain sequence numbers, this parameter
     *                     must be set to zero.
     * @param pfQOP        A pointer to a variable of type ULONG that receives
     *                     package-specific flags that indicate the quality of
     *                     protection. This parameter can be the following flag:
     *                     {@link Sspi#SECQOP_WRAP_NO_ENCRYPT}.
     * @return If the function verifies that the message was received in the correct sequence, the function returns SEC_E_OK.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375385(v=vs.85).aspx">MSDN Entry</a>
     */
    int DecryptMessage(CtxtHandle phContext, SecBufferDesc pMessage, int MessageSeqNo, IntByReference pfQOP);
}
