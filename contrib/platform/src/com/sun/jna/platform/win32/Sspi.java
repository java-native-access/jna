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
import com.sun.jna.TypeMapper;
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
        public SecBuffer.ByReference[] pBuffers = new SecBuffer.ByReference[] {
            new SecBuffer.ByReference()
        };

        /**
         * Create a new SecBufferDesc with one SECBUFFER_EMPTY buffer.
         */
        public SecBufferDesc() {
            super();
        }

        /**
         * Create a new SecBufferDesc with initial data.
         * @param type Token type.
         * @param token Initial token data.
         */
        public SecBufferDesc(int type, byte[] token) {
            pBuffers[0] = new SecBuffer.ByReference(type, token);
        }

        /**
         * Create a new SecBufferDesc with one SecBuffer of a given type and size.
         * @param type type
         * @param tokenSize token size
         */
        public SecBufferDesc(int type, int tokenSize) {
            pBuffers[0] = new SecBuffer.ByReference(type, tokenSize);
        }

        public byte[] getBytes() {
            return pBuffers[0].getBytes();
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
}
