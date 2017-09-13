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

import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Ported from Sspi.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface Sspi {

    /**
     * Maximum size in bytes of a security token.
     */
    int MAX_TOKEN_SIZE = 12288;

    // Flags for the fCredentialUse parameter of AcquireCredentialsHandle

    /**
     * Validate an incoming server credential. Inbound credentials might be validated
     * by using an authenticating authority when InitializeSecurityContext or
     * AcceptSecurityContext is called. If such an authority is not available, the function will
     * fail and return SEC_E_NO_AUTHENTICATING_AUTHORITY. Validation is package specific.
     */
    int SECPKG_CRED_INBOUND = 1;

    /**
     * Allow a local client credential to prepare an outgoing token.
     */
    int SECPKG_CRED_OUTBOUND = 2;


    // Flags for the TargetDataRep parameter of AcceptSecurityContext and InitializeSecurityContext

    /**
     * Specifies Native data representation.
     */
    int SECURITY_NATIVE_DREP = 0x10;

    
    /**
     * Specifies network data representation.
     */
    int SECURITY_NETWORK_DREP = 0x00;

    // Flags for the fContextReq parameter of InitializeSecurityContext or AcceptSecurityContext.

    /**
     * The security package allocates output buffers for you.
     * When you have finished using the output buffers, free them by calling the FreeContextBuffer function.
     */
    int ISC_REQ_ALLOCATE_MEMORY = 0x00000100;

    /**
     * Encrypt messages by using the EncryptMessage function.
     */
    int ISC_REQ_CONFIDENTIALITY = 0x00000010;

    /**
     * The security context will not handle formatting messages. This value is the default.
     */
    int ISC_REQ_CONNECTION = 0x00000800;

    /**
     * The server can use the context to authenticate to other servers as the client.
     * The ISC_REQ_MUTUAL_AUTH flag must be set for this flag to work. Valid for Kerberos.
     * Ignore this flag for constrained delegation.
     */
    int ISC_REQ_DELEGATE = 0x00000001;

    /**
     * When errors occur, the remote party will be notified.
     */
    int ISC_REQ_EXTENDED_ERROR = 0x00004000;

    /**
     * Sign messages and verify signatures by using the EncryptMessage and MakeSignature functions.
     */
    int ISC_REQ_INTEGRITY = 0x00010000;

    /**
     * The mutual authentication policy of the service will be satisfied.
     */
    int ISC_REQ_MUTUAL_AUTH = 0x00000002;

    /**
     * Detect replayed messages that have been encoded by using the
     * EncryptMessage or MakeSignature functions.
     */
    int ISC_REQ_REPLAY_DETECT = 0x00000004;

    /**
     * Detect messages received out of sequence.
     */
    int ISC_REQ_SEQUENCE_DETECT = 0x00000008;

    /**
     * Support a stream-oriented connection.
     */
    int ISC_REQ_STREAM = 0x00008000;

    /**
     * Version of the SecBuffer struct.
     */
    int SECBUFFER_VERSION = 0;

    /**
     * This is a placeholder in the buffer array.
     */
    int SECBUFFER_EMPTY = 0;
    /**
     * This buffer type is used for common data. The security package can read
     * and write this data.
     */
    int SECBUFFER_DATA = 1;
    /**
     * This buffer type is used to indicate the security token portion of the message.
     * This is read-only for input parameters or read/write for output parameters.
     */
    int SECBUFFER_TOKEN = 2;

    /**
     * The pBuffer parameter contains a pointer to a {@link SecPkgContext_Sizes}
     * structure.
     *
     * <p>Queries the sizes of the structures used in the per-message functions.</p>
     */
    int SECPKG_ATTR_SIZES = 0;
    /**
     * The pBuffer parameter contains a pointer to a {@link SecPkgCredentials_Names}
     * structure.
     *
     * <p>Queries the name associated with the context.</p>
     */
    int SECPKG_ATTR_NAMES = 1;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_Lifespan
     * structure.
     *
     * <p>Queries the life span of the context.</p>
     */
    int SECPKG_ATTR_LIFESPAN = 2;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_DceInfo
     * structure.
     *
     * <p>Queries for authorization data used by DCE services.</p>
     */
    int SECPKG_ATTR_DCE_INFO = 3;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_StreamSizes
     * structure.
     *
     * <p>Queries the sizes of the various parts of a stream used in the
     * per-message functions.</p>
     * <p>This attribute is supported only by the Schannel security package.</p>
     */
    int SECPKG_ATTR_STREAM_SIZES = 4;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_KeyInfo
     * structure.
     *
     * <p>Queries information about the keys used in a security context.</p>
     */
    int SECPKG_ATTR_KEY_INFO = 5;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_Authority
     * structure.
     *
     * <p>Queries the name of the authenticating authority.</p>
     */
    int SECPKG_ATTR_AUTHORITY = 6;
    int SECPKG_ATTR_PROTO_INFO = 7;
    /**
     * The pBuffer parameter contains a pointer to a
     * SecPkgContext_PasswordExpiry structure.
     *
     * <p>Returns password expiration information.</p>
     */
    int SECPKG_ATTR_PASSWORD_EXPIRY = 8;
    /**
     * The pBuffer parameter contains a pointer to a
     * {@link SecPkgContext_SessionKey} structure.
     *
     * Returns information about the session keys.
     */
    int SECPKG_ATTR_SESSION_KEY = 9;
    /**
     * The pBuffer parameter contains a pointer to a
     * {@link SecPkgContext_PackageInfo} structure.
     *
     * Returns information on the SSP in use.
     */
    int SECPKG_ATTR_PACKAGE_INFO = 10;
    int SECPKG_ATTR_USER_FLAGS = 11;
    /**
     * The pBuffer parameter contains a pointer to a
     * {@link SecPkgContext_NegotiationInfo} structure.
     *
     * <p>Returns information about the security package to be used with the
     * negotiation process and the current state of the negotiation for the use
     * of that package.</p>
     */
    int SECPKG_ATTR_NEGOTIATION_INFO = 12;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_NativeNames
     * structure.
     *
     * <p>Returns the principal name (CNAME) from the outbound ticket.</p>
     */
    int SECPKG_ATTR_NATIVE_NAMES = 13;
    /**
     * The pBuffer parameter contains a pointer to a {@link SecPkgContext_Flags}
     * structure.
     *
     * <p>Returns information about the negotiated context flags.</p>
     */
    int SECPKG_ATTR_FLAGS = 14;
    // These attributes exist only in Win XP and greater
    int SECPKG_ATTR_USE_VALIDATED = 15;
    int SECPKG_ATTR_CREDENTIAL_NAME = 16;
    /**
     * The pBuffer parameter contains a pointer to a
     * SecPkgContext_TargetInformation structure.
     *
     * <p>Returns information about the name of the remote server.</p>
     */
    int SECPKG_ATTR_TARGET_INFORMATION = 17;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_AccessToken
     * structure.
     *
     * <p>Returns a handle to the access token.</p>
     */
    int SECPKG_ATTR_ACCESS_TOKEN = 18;
    // These attributes exist only in Win2K3 and greater
    int SECPKG_ATTR_TARGET = 19;
    int SECPKG_ATTR_AUTHENTICATION_ID = 20;
    // These attributes exist only in Win2K3SP1 and greater
    int SECPKG_ATTR_LOGOFF_TIME = 21;
    //
    // win7 or greater
    //
    int SECPKG_ATTR_NEGO_KEYS = 22;
    int SECPKG_ATTR_PROMPTING_NEEDED = 24;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_Bindings
     * structure that specifies channel binding information.
     *
     * <p>This value is supported only by the Schannel security package.</p>
     *
     * <p><strong>Windows Server 2008, Windows Vista, Windows Server 2003 and
     * Windows XP:</strong>
     * This value is not supported.</p>
     */
    int SECPKG_ATTR_UNIQUE_BINDINGS = 25;
    /**
     * The pBuffer parameter contains a pointer to a SecPkgContext_Bindings
     * structure that specifies channel binding information.
     *
     * <p>This attribute is supported only by the Schannel security package.</p>
     *
     * <p><strong>Windows Server 2008, Windows Vista, Windows Server 2003 and
     * Windows XP:</strong>
     * This value is not supported.</p>
     */
    int SECPKG_ATTR_ENDPOINT_BINDINGS = 26;
    /**
     * The pBuffer parameter contains a pointer to a
     * SecPkgContext_ClientSpecifiedTarget structure that represents the service
     * principal name (SPN) of the initial target supplied by the client.
     * 
     * <p><strong>Windows Server 2008, Windows Vista, Windows Server 2003 and
     * Windows XP:</strong>
     * This value is not supported.</p>
     */
    int SECPKG_ATTR_CLIENT_SPECIFIED_TARGET = 27;

    /**
     * The pBuffer parameter contains a pointer to a
     * SecPkgContext_LastClientTokenStatus structure that specifies whether the
     * token from the most recent call to the InitializeSecurityContext function
     * is the last token from the client.
     *
     * <p>This value is supported only by the Negotiate, Kerberos, and NTLM
     * security packages.</p>
     * 
     * <p><strong>Windows Server 2008, Windows Vista, Windows Server 2003 and
     * Windows XP:</strong>
     * This value is not supported.</p>
     */
    int SECPKG_ATTR_LAST_CLIENT_TOKEN_STATUS = 30;
    int SECPKG_ATTR_NEGO_PKG_INFO = 31; // contains nego info of packages
    int SECPKG_ATTR_NEGO_STATUS = 32; // contains the last error
    int SECPKG_ATTR_CONTEXT_DELETED = 33; // a context has been deleted

    /**
     * The pBuffer parameter contains a pointer to a
     * SecPkgContext_SubjectAttributes structure.
     *
     * <p>This value returns information about the security attributes for the
     * connection.</p>
     *
     * <p>This value is supported only on the CredSSP server.</p>
     * 
     * <p><strong>Windows Server 2008, Windows Vista, Windows Server 2003 and
     * Windows XP:</strong>
     * This value is not supported.</p>
     */
    int SECPKG_ATTR_SUBJECT_SECURITY_ATTRIBUTES = 128;

    /**
     * Negotiation has been completed.
     */
    int SECPKG_NEGOTIATION_COMPLETE = 0;
    /**
     * Negotiations not yet completed.
     */
    int SECPKG_NEGOTIATION_OPTIMISTIC = 1;
    /**
     * Negotiations in progress.
     */
    int SECPKG_NEGOTIATION_IN_PROGRESS = 2;
    int SECPKG_NEGOTIATION_DIRECT = 3;
    int SECPKG_NEGOTIATION_TRY_MULTICRED = 4;
    
    
    // flags for SecPkgInfo fCapabilities
    // (https://msdn.microsoft.com/en-us/library/windows/desktop/aa380104(v=vs.85).aspx)
    /**
     * Supports integrity on messages
     */
    int SECPKG_FLAG_INTEGRITY = 0x00000001;
    /**
     * Supports privacy (confidentiality)
     */
    int SECPKG_FLAG_PRIVACY = 0x00000002;
    /**
     * Only security token needed
     */
    int SECPKG_FLAG_TOKEN_ONLY = 0x00000004;
    /**
     * Datagram RPC support
     */
    int SECPKG_FLAG_DATAGRAM = 0x00000008;
    /**
     * Connection oriented RPC support
     */
    int SECPKG_FLAG_CONNECTION = 0x00000010;
    /**
     * Full 3-leg required for re-auth.
     */
    int SECPKG_FLAG_MULTI_REQUIRED = 0x00000020;
    /**
     * Server side functionality not available
     */
    int SECPKG_FLAG_CLIENT_ONLY = 0x00000040;
    /**
     * Supports extended error msgs
     */
    int SECPKG_FLAG_EXTENDED_ERROR = 0x00000080;
    /**
     * Supports impersonation
     */
    int SECPKG_FLAG_IMPERSONATION = 0x00000100;
    /**
     * Accepts Win32 names
     */
    int SECPKG_FLAG_ACCEPT_WIN32_NAME = 0x00000200;
    /**
     * Supports stream semantics
     */
    int SECPKG_FLAG_STREAM = 0x00000400;
    /**
     * Can be used by the negotiate package
     */
    int SECPKG_FLAG_NEGOTIABLE = 0x00000800;
    /**
     * GSS Compatibility Available
     */
    int SECPKG_FLAG_GSS_COMPATIBLE = 0x00001000;
    /**
     * Supports common LsaLogonUser
     */
    int SECPKG_FLAG_LOGON = 0x00002000;
    /**
     * Token Buffers are in ASCII
     */
    int SECPKG_FLAG_ASCII_BUFFERS = 0x00004000;
    /**
     * Package can fragment to fit
     */
    int SECPKG_FLAG_FRAGMENT = 0x00008000;
    /**
     * Package can perform mutual authentication
     */
    int SECPKG_FLAG_MUTUAL_AUTH = 0x00010000;
    /**
     * Package can delegate
     */
    int SECPKG_FLAG_DELEGATION = 0x00020000;
    /**
     * Supports callers with restricted tokens.
     */
    int SECPKG_FLAG_RESTRICTED_TOKENS = 0x80000;
    /**
     * The security package extends the Microsoft Negotiate security package.
     */
    int SECPKG_FLAG_NEGO_EXTENDER = 0x00100000;
    /**
     * This package is negotiated by the package of type SECPKG_FLAG_NEGO_EXTENDER.
     */
    int SECPKG_FLAG_NEGOTIABLE2 = 0x00200000;
    /**
     * This package receives all calls from app container apps.
     */
    int SECPKG_FLAG_APPCONTAINER_PASSTHROUGH = 0x00400000;
    /**
     * This package receives calls from app container apps if one of the following checks succeeds.
     * <ul>
     * <li>Caller has default credentials capability.</li>
     * <li>The target is a proxy server.</li>
     * <li>The caller has supplied credentials.</li>
     * </ul>
     */
    int SECPKG_FLAG_APPCONTAINER_CHECKS = 0x00800000;

    /**
     * Returns the name of a credential in a pbuffer of type {@link SecPkgCredentials_Names}.
     */
    int SECPKG_CRED_ATTR_NAMES = 1;

    /**
     * Produce a header or trailer but do not encrypt the message.
     */
    int SECQOP_WRAP_NO_ENCRYPT = 0x80000001;
    /**
     * Send an Schannel alert message. In this case, the pMessage parameter must
     * contain a standard two-byte SSL/TLS event code. This value is supported
     * only by the Schannel SSP.
     */
    int SECQOP_WRAP_OOB_DATA = 0x40000000;
    
    /**
     * Security handle.
     */
    public static class SecHandle extends Structure {

        public static class ByReference extends SecHandle implements Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder("dwLower", "dwUpper");

        public Pointer dwLower;
        public Pointer dwUpper;

        /**
         * An empty SecHandle.
         */
        public SecHandle() {
            super();
        }

        /**
         * Returns true if the handle is NULL.
         * @return
         *  True if NULL, False otherwise.
         */
        public boolean isNull() {
            return dwLower == null && dwUpper == null;
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * A pointer to a SecHandle
     */
    public static class PSecHandle extends Structure {

        public static class ByReference extends PSecHandle implements Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder("secHandle");
        /**
         * The first entry in an array of SecPkgInfo structures.
         */
        public SecHandle.ByReference secHandle;

        public PSecHandle() {
            super();
        }

        public PSecHandle(SecHandle h) {
            super(h.getPointer());
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Credentials handle.
     */
    public static class CredHandle extends SecHandle {
    }

    /**
     * Security context handle.
     */
    public static class CtxtHandle extends SecHandle {
    }

    /**
     * The SecBuffer structure describes a buffer allocated by a transport application
     * to pass to a security package.
     */
    public static class SecBuffer extends Structure {

        /**
         * A ByReference SecBuffer.
         */
    	public static class ByReference extends SecBuffer implements Structure.ByReference {
            /**
             * Create a SECBUFFER_EMPTY SecBuffer.
             */
            public ByReference() {
            }

            /**
             * Create a SecBuffer of a given type and size.
             * @param type
             *  Buffer type, one of SECBUFFER_EMTPY, etc.
             * @param size
             *  Buffer size, eg. MAX_TOKEN_SIZE.
             */
            public ByReference(int type, int size) {
                super(type, size);
            }

            public ByReference(int type, byte[] token) {
                super(type, token);
            }
    	}

    	public static final List<String> FIELDS = createFieldsOrder("cbBuffer", "BufferType", "pvBuffer");
        /**
         * Specifies the size, in bytes, of the buffer pointed to by the pvBuffer member.
         */
        public int cbBuffer;
        /**
         * Bit flags that indicate the type of buffer. Must be one of the values of
         * the SecBufferType enumeration.
         */
        public int BufferType = SECBUFFER_EMPTY;
        /**
         * A pointer to a buffer.
         */
        public Pointer pvBuffer;

        /**
         * Create a new SECBUFFER_EMPTY buffer.
         */
        public SecBuffer() {
            super();
        }

        /**
         * Create a SecBuffer of a given type and size.
         * @param type
         *  Buffer type, one of SECBUFFER_EMTPY, etc.
         * @param size
         *  Buffer size, eg. MAX_TOKEN_SIZE.
         */
        public SecBuffer(int type, int size) {
            cbBuffer = size;
            pvBuffer = new Memory(size);
            BufferType = type;
        }

        /**
         * Create a SecBuffer of a given type with initial data.
         * @param type
         *  Buffer type, one of SECBUFFER_EMTPY, etc.
         * @param token
         *  Existing token.
         */
        public SecBuffer(int type, byte[] token) {
            cbBuffer = token.length;
            pvBuffer = new Memory(token.length);
            pvBuffer.write(0, token, 0, token.length);
            BufferType = type;
        }

        /**
         * Get buffer bytes.
         * @return
         *  Raw buffer bytes.
         */
        public byte[] getBytes() {
            return pvBuffer == null ? null : pvBuffer.getByteArray(0, cbBuffer);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * The SecBufferDesc structure describes an array of SecBuffer structures to
     * pass from a transport application to a security package.
     * 
     * <p>SecBufferDesc was introduced because {@link SecBufferDesc} does not
     * correctly cover the case there not exactly one {@link SecBuffer} is
     * passed to the security package.</p>
     * 
     * <p>If the SecBufferDesc is managed from the java side, <b>prefer to use 
     * {@link com.sun.jna.platform.win32.SspiUtil.ManagedSecBufferDesc ManagedSecBufferDesc}.</b></p>
     */
    public static class SecBufferDesc extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("ulVersion", "cBuffers", "pBuffers");

        /**
         * Version number.
         */
        public int ulVersion = SECBUFFER_VERSION;
        /**
         * Number of buffers.
         */
        public int cBuffers = 1;
        /**
         * Pointer to array of buffers.
         */
        public Pointer pBuffers;

        /**
         * Create a new SecBufferDesc with one SECBUFFER_EMPTY buffer.
         */
        public SecBufferDesc() {
            super();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * A security integer.
     */
    public static class SECURITY_INTEGER extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwLower", "dwUpper");
        public int dwLower;
        public int dwUpper;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * A timestamp.
     */
    public static class TimeStamp extends SECURITY_INTEGER {
    }

    /**
     * A pointer to an array of SecPkgInfo structures.
     */
    public static class PSecPkgInfo extends Structure {

        public static class ByReference extends PSecPkgInfo implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("pPkgInfo");

        /**
         * The first entry in an array of SecPkgInfo structures.
         */
        public SecPkgInfo.ByReference pPkgInfo;

        public PSecPkgInfo() {
            super();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        /**
         * An array of SecPkgInfo structures.
         */
        @Override
        public SecPkgInfo.ByReference[] toArray(int size) {
            return (SecPkgInfo.ByReference[]) pPkgInfo.toArray(size);
        }
    }

    /**
     * The SecPkgInfo structure provides general information about a security package,
     * such as its name and capabilities.
     */
    public static class SecPkgInfo extends Structure {

        /**
         * A reference pointer to a SecPkgInfo structure.
         */
        public static class ByReference extends SecPkgInfo implements Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder(
                "fCapabilities", "wVersion", "wRPCID", "cbMaxToken", "Name", "Comment");

        /**
         * Set of bit flags that describes the capabilities of the security package.
         */
        public int fCapabilities;
        /**
         * Specifies the version of the package protocol. Must be 1.
         */
        public short wVersion = 1;
        /**
         * Specifies a DCE RPC identifier, if appropriate. If the package does not implement one of
         * the DCE registered security systems, the reserved value SECPKG_ID_NONE is used.
         */
        public short wRPCID;
        /**
         * Specifies the maximum size, in bytes, of the token.
         */
        public int cbMaxToken;
        /**
         * Pointer to a null-terminated string that contains the name of the security package.
         */
        public String Name;
        /**
         * Pointer to a null-terminated string. This can be any additional string passed
         * back by the package.
         */
        public String Comment;

        public SecPkgInfo() {
            super(W32APITypeMapper.DEFAULT);
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * The SecPkgContext_PackageInfo structure.
     */
    public static class SecPkgContext_PackageInfo extends Structure {
        /**
         * A reference pointer to a SecPkgContext_PackageInfo structure.
         */
        public static class ByReference extends SecPkgContext_PackageInfo implements Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder("PackageInfo");

        /**
         * Pointer to a SecPkgInfo structure containing the name of the SSP in
         * use.
         */
        public SecPkgInfo.ByReference PackageInfo;

        public SecPkgContext_PackageInfo() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The SecPkgCredentials_Names structure holds the name of the user
     * associated with a context.
     *
     * <p>
     * The
     * {@link Secur32#QueryCredentialsAttributes(com.sun.jna.platform.win32.Sspi.CredHandle, int, com.sun.jna.Structure)}
     * function uses this structure.</p>
     */
    public static class SecPkgCredentials_Names extends Structure {

        public static class ByReference extends SecPkgCredentials_Names implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("sUserName");

        /**
         * Pointer to a null-terminated string containing the name of the user
         * represented by the credential. If the security package sets the
         * SECPKG_FLAG_ACCEPT_WIN32_NAME flag to indicate that it can process
         * Windows names, this name can be used in other Windows calls.
         */
        public Pointer sUserName;

        public SecPkgCredentials_Names() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        /**
         * @return value of userName attribute
         */
        public synchronized String getUserName() {
            if (sUserName == null) {
                return null;
            }
            return Boolean.getBoolean("w32.ascii") ? sUserName.getString(0) : sUserName.getWideString(0);
        }

        /**
         * Free native buffer
         * 
         * @return {@link WinError#SEC_E_OK} if ok
         */
        public synchronized int free() {
            if (sUserName != null) {
                int result = Secur32.INSTANCE.FreeContextBuffer(sUserName);
                sUserName = null;
                return result;
            }
            return WinError.SEC_E_OK;
        }
    }
    
    /**
     * The SecPkgContext_Sizes structure indicates the sizes of important
     * structures used in the message support functions.
     *
     * <p>
     * The {@link Secur32#QueryContextAttributes(com.sun.jna.platform.win32.Sspi.CtxtHandle, int, com.sun.jna.Structure)
     * } function uses this structure.</p>
     */
    public static class SecPkgContext_Sizes extends Structure {

        public static class ByReference extends SecPkgContext_Sizes implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("cbMaxToken", "cbMaxSignature", "cbBlockSize", "cbSecurityTrailer");

        /**
         * Specifies the maximum size of the security token used in the authentication exchanges.
         */
        public int cbMaxToken;
        
        /**
         * Specifies the maximum size of the signature created by the MakeSignature function. This member must be zero if integrity services are not requested or available.
         */
        public int cbMaxSignature;
        
        /**
         * Specifies the preferred integral size of the messages. For example, eight indicates that messages should be of size zero mod eight for optimal performance. Messages other than this block size can be padded.
         */
        public int cbBlockSize;
        
        /**
         * Size of the security trailer to be appended to messages. This member should be zero if the relevant services are not requested or available.
         */
        public int cbSecurityTrailer;

        public SecPkgContext_Sizes() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        @Override
        public String toString() {
            return "SecPkgContext_Sizes{" + "cbMaxToken=" + cbMaxToken +
                    ", cbMaxSignature=" + cbMaxSignature + ", cbBlockSize=" +
                    cbBlockSize + ", cbSecurityTrailer=" + cbSecurityTrailer +
                    '}';
        }
    }
    
    public static class SecPkgContext_SessionKey extends Structure {

        public static class ByReference extends SecPkgContext_SessionKey implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("SessionKeyLength", "SessionKey");

        /**
         * Size, in bytes, of the session key.
         */
        public int SessionKeyLength;
        
        /**
         * The session key for the security context.
         */
        public Pointer SessionKey;

        public SecPkgContext_SessionKey() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public byte[] getSessionKey() {
            if(SessionKey == null) {
                return null;
            }
            return SessionKey.getByteArray(0, SessionKeyLength);
        }
        
        public synchronized void free() {
            if(SessionKey != null) {
                Secur32.INSTANCE.FreeContextBuffer(SessionKey);
                SessionKey = null;
            }
        }
    }
    
    public static class SecPkgContext_KeyInfo extends Structure {

        public static class ByReference extends SecPkgContext_KeyInfo implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("sSignatureAlgorithmName", "sEncryptAlgorithmName","KeySize", "SignatureAlgorithm", "EncryptAlgorithm");

        /**
         * Name, if available, of the algorithm used for generating signatures, for example "MD5" or "SHA-2".
         */
        public Pointer sSignatureAlgorithmName;
        
        /**
         * Name, if available, of the algorithm used for encrypting messages. Reserved for future use.
         */
        public Pointer sEncryptAlgorithmName;
        
        /**
         * Specifies the effective key length, in bits, for the session key. This is typically 40, 56, or 128 bits.
         */
        public int KeySize;
        
        /**
         * Specifies the algorithm identifier (ALG_ID) used for generating signatures, if available.
         */
        public int SignatureAlgorithm;
        
        /**
         * Specifies the algorithm identifier (ALG_ID) used for encrypting messages. Reserved for future use.
         */
        public int EncryptAlgorithm;

        public SecPkgContext_KeyInfo() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public synchronized String getSignatureAlgorithmName() {
            if(sSignatureAlgorithmName == null) {
                return null;
            }
            return Boolean.getBoolean("w32.ascii") ? sSignatureAlgorithmName.getString(0) : sSignatureAlgorithmName.getWideString(0);
        }
        
        public synchronized String getEncryptAlgorithmName() {
            if(sEncryptAlgorithmName == null) {
                return null;
            }
            return Boolean.getBoolean("w32.ascii") ? sEncryptAlgorithmName.getString(0) : sEncryptAlgorithmName.getWideString(0);
        }
        
        public synchronized void free() {
            if(sSignatureAlgorithmName != null) {
                Secur32.INSTANCE.FreeContextBuffer(sSignatureAlgorithmName);
                sSignatureAlgorithmName = null;
            }
            if(sEncryptAlgorithmName != null) {
                Secur32.INSTANCE.FreeContextBuffer(sEncryptAlgorithmName);
                sEncryptAlgorithmName = null;
            }
        }
    }
    
    public static class SecPkgContext_Lifespan extends Structure {

        public static class ByReference extends SecPkgContext_Lifespan implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("tsStart", "tsExpiry");

        /**
         * Time at which the context was established.
         */
        public TimeStamp tsStart;
        
        /**
         * Time at which the context will expire.
         */
        public TimeStamp tsExpiry;

        public SecPkgContext_Lifespan() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    public static class SecPkgContext_NegotiationInfo extends Structure {

        public static class ByReference extends SecPkgContext_NegotiationInfo implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("PackageInfo", "NegotiationState");

        /**
         * Time at which the context was established.
         */
        public PSecPkgInfo PackageInfo;

        /**
         * Time at which the context will expire.
         */
        public int NegotiationState;

        public SecPkgContext_NegotiationInfo() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        
        public synchronized void free() {
            if(PackageInfo != null) {
                Secur32.INSTANCE.FreeContextBuffer(PackageInfo.pPkgInfo.getPointer());
                PackageInfo = null;
            }
        }
    }
    
    public static class SecPkgContext_Flags extends Structure {

        public static class ByReference extends SecPkgContext_Flags implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("Flags");

        /**
         * Flag values for the current security context. These values correspond
         * to the flags negotiated by the InitializeSecurityContext (General)
         * and AcceptSecurityContext (General) functions.
         */
        public int Flags;

        public SecPkgContext_Flags() {
            super(W32APITypeMapper.DEFAULT);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * Strings in structure {@link SEC_WINNT_AUTH_IDENTITY} are ANSI
     */
    public static final int SEC_WINNT_AUTH_IDENTITY_ANSI = 0x1;
    /**
     * String in structure {@link SEC_WINNT_AUTH_IDENTITY} are UNICODE
     */
    public static final int SEC_WINNT_AUTH_IDENTITY_UNICODE = 0x2;

    
    public static class SEC_WINNT_AUTH_IDENTITY extends Structure {

        public static final List<String> FIELDS = createFieldsOrder("User", "UserLength", "Domain", "DomainLength", "Password", "PasswordLength", "Flags");

        /**
         * A string that contains the user name.
         */
        public String User;

        /**
         * The length, in characters, of the user string, not including the
         * terminating null character.
         */
        public int UserLength;

        /**
         * A string that contains the domain name or the workgroup name.
         */
        public String Domain;

        /**
         * The length, in characters, of the domain string, not including the
         * terminating null character.
         */
        public int DomainLength;

        /**
         * A string that contains the password of the user in the domain or
         * workgroup. When you have finished using the password, remove the
         * sensitive information from memory by calling SecureZeroMemory. For
         * more information about protecting the password, see Handling
         * Passwords.
         */
        public String Password;

        /**
         * The length, in characters, of the password string, not including the
         * terminating null character.
         */
        public int PasswordLength;

        /**
         * This member can be one of the following values.
         *
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>SEC_WINNT_AUTH_IDENTITY_ANSI</td><td>The strings in this structure are in ANSI format.</td></tr>
         * <tr><td>SEC_WINNT_AUTH_IDENTITY_UNICODE</td><td>The strings in this structure are in Unicode format.</td></tr>
         * </table>
         *
         * <strong>As the string encoding is managed by JNA do not change this
         * value!</strong>
         */
        public int Flags = SEC_WINNT_AUTH_IDENTITY_UNICODE;
    

        /**
         * Create a new SecBufferDesc with one SECBUFFER_EMPTY buffer.
         */
        public SEC_WINNT_AUTH_IDENTITY() {
            super(W32APITypeMapper.UNICODE);
        }

        @Override
        public void write() {
            UserLength = User == null ? 0 : User.length();
            DomainLength = Domain == null ? 0 : Domain.length();
            PasswordLength = Password == null ? 0 : Password.length();
            super.write();
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
