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

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from Sspi.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface Sspi extends StdCallLibrary {

    /**
     * Maximum size in bytes of a security token.
     */
    public static final int MAX_TOKEN_SIZE = 12288;

    // Flags for the fCredentialUse parameter of AcquireCredentialsHandle

    /**
     * Validate an incoming server credential. Inbound credentials might be validated 
     * by using an authenticating authority when InitializeSecurityContext or
     * AcceptSecurityContext is called. If such an authority is not available, the function will 
     * fail and return SEC_E_NO_AUTHENTICATING_AUTHORITY. Validation is package specific.
     */
    public static final int SECPKG_CRED_INBOUND = 1;

    /**
     * Allow a local client credential to prepare an outgoing token.
     */
    public static final int SECPKG_CRED_OUTBOUND = 2;

    
    // Flags for the TargetDataRep parameter of AcceptSecurityContext and InitializeSecurityContext

    /**
     * Specifies Native data representation. 
     */
    public static final int SECURITY_NATIVE_DREP = 0x10;


    // Flags for the fContextReq parameter of InitializeSecurityContext or AcceptSecurityContext.

    /**
     * The security package allocates output buffers for you. 
     * When you have finished using the output buffers, free them by calling the FreeContextBuffer function.
     */
    public static final int ISC_REQ_ALLOCATE_MEMORY = 0x00000100;

    /**
     * Encrypt messages by using the EncryptMessage function.
     */
    public static final int ISC_REQ_CONFIDENTIALITY = 0x00000010;

    /**
     * The security context will not handle formatting messages. This value is the default.
     */
    public static final int ISC_REQ_CONNECTION = 0x00000800;

    /**
     * The server can use the context to authenticate to other servers as the client. 
     * The ISC_REQ_MUTUAL_AUTH flag must be set for this flag to work. Valid for Kerberos. 
     * Ignore this flag for constrained delegation.
     */
    public static final int ISC_REQ_DELEGATE = 0x00000001;

    /**
     * When errors occur, the remote party will be notified.
     */
    public static final int ISC_REQ_EXTENDED_ERROR = 0x00004000;

    /**
     * Sign messages and verify signatures by using the EncryptMessage and MakeSignature functions.
     */
    public static final int ISC_REQ_INTEGRITY = 0x00010000;

    /**
     * The mutual authentication policy of the service will be satisfied.
     */
    public static final int ISC_REQ_MUTUAL_AUTH = 0x00000002;

    /**
     * Detect replayed messages that have been encoded by using the 
     * EncryptMessage or MakeSignature functions.
     */
    public static final int ISC_REQ_REPLAY_DETECT = 0x00000004;

    /**
     * Detect messages received out of sequence.
     */
    public static final int ISC_REQ_SEQUENCE_DETECT = 0x00000008;

    /**
     * Support a stream-oriented connection.
     */
    public static final int ISC_REQ_STREAM = 0x00008000;

    /**
     * Version of the SecBuffer struct.
     */
    public static final int SECBUFFER_VERSION = 0;

    /**
     * This is a placeholder in the buffer array.
     */
    public static final int SECBUFFER_EMPTY = 0;
    /**
     * This buffer type is used for common data. The security package can read
     * and write this data.
     */
    public static final int SECBUFFER_DATA = 1;
    /**
     * This buffer type is used to indicate the security token portion of the message. 
     * This is read-only for input parameters or read/write for output parameters.
     */
    public static final int SECBUFFER_TOKEN = 2;
	
    /**
	 * Security handle.
	 */
	public static class SecHandle extends Structure {		
		public Pointer dwLower;
		public Pointer dwUpper;

		public static class ByReference extends SecHandle implements Structure.ByReference {

		}
		
		/**
		 * An empty SecHandle.
		 */
		public SecHandle() {
			dwLower = null;
			dwUpper = null;
		}
		
		/**
		 * Returns true if the handle is NULL.
		 * @return
		 *  True if NULL, False otherwise.
		 */
		public boolean isNull() {
			return dwLower == null && dwUpper == null;
		}
	}

	/**
	 * A pointer to a SecHandle
	 */
	public static class PSecHandle extends Structure {

		public static class ByReference extends PSecHandle implements Structure.ByReference {

		}
		
		/**
		 * The first entry in an array of SecPkgInfo structures.
		 */
		public SecHandle.ByReference secHandle;
		
		public PSecHandle() {
			
		}

		public PSecHandle(SecHandle h) {
			useMemory(h.getPointer());
			read();
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
			
		    /**
		     * Get buffer bytes.
		     * @return
		     *  Raw buffer bytes.
		     */
			public byte[] getBytes() {
				return super.getBytes();
			}
    	}
		
		/**
		 * Specifies the size, in bytes, of the buffer pointed to by the pvBuffer member.
		 */
	    public NativeLong cbBuffer;
	    /**
	     * Bit flags that indicate the type of buffer. Must be one of the values of 
	     * the SecBufferType enumeration.
	     */
	    public NativeLong BufferType;
	    /**
	     * A pointer to a buffer.
	     */
	    public Pointer pvBuffer;
	    
	    /**
	     * Create a new SECBUFFER_EMPTY buffer.
	     */
	    public SecBuffer() {
	    	cbBuffer = new NativeLong(0);
	    	pvBuffer = null;
	    	BufferType = new NativeLong(SECBUFFER_EMPTY);
	    }
	    
		/**
		 * Create a SecBuffer of a given type and size.
		 * @param type
		 *  Buffer type, one of SECBUFFER_EMTPY, etc.
		 * @param size
		 *  Buffer size, eg. MAX_TOKEN_SIZE.
		 */
	    public SecBuffer(int type, int size) {
	    	cbBuffer = new NativeLong(size);	    	
	    	pvBuffer = new Memory(size);
	    	BufferType = new NativeLong(type);
	    	allocateMemory();
	    }
	    
	    /**
	     * Create a SecBuffer of a given type with initial data.
		 * @param type
		 *  Buffer type, one of SECBUFFER_EMTPY, etc.
	     * @param token
	     *  Existing token.
	     */
	    public SecBuffer(int type, byte[] token) {
	    	cbBuffer = new NativeLong(token.length);	    	
	    	pvBuffer = new Memory(token.length);
	    	pvBuffer.write(0, token, 0, token.length);
	    	BufferType = new NativeLong(type);
	    	allocateMemory();
	    }
	    
	    /**
	     * Get buffer bytes.
	     * @return
	     *  Raw buffer bytes.
	     */
	    public byte[] getBytes() {
	    	return pvBuffer.getByteArray(0, cbBuffer.intValue());
	    }
	}

	public static class SecBufferDesc extends Structure {
				
		/**
		 * Version number.
		 */
	    public NativeLong ulVersion;
	    /**
	     * Number of buffers.
	     */
	    public NativeLong cBuffers;
	    /**
	     * Pointer to array of buffers.
	     */
	    public SecBuffer.ByReference[] pBuffers;

	    /**
	     * Create a new SecBufferDesc with one SECBUFFER_EMPTY buffer.
	     */
	    public SecBufferDesc() {
	    	ulVersion = new NativeLong(SECBUFFER_VERSION);
	    	cBuffers = new NativeLong(1);
	    	SecBuffer.ByReference secBuffer = new SecBuffer.ByReference();
	    	pBuffers = (SecBuffer.ByReference[]) secBuffer.toArray(1);
	    	allocateMemory();
	    }
	    
	    /**
	     * Create a new SecBufferDesc with initial data.
	     * @param type
	     *  Token type.
	     * @param token
	     *  Initial token data.
	     */
	    public SecBufferDesc(int type, byte[] token) {
	    	ulVersion = new NativeLong(SECBUFFER_VERSION);
	    	cBuffers = new NativeLong(1);
	    	SecBuffer.ByReference secBuffer = new SecBuffer.ByReference(type, token);
	    	pBuffers = (SecBuffer.ByReference[]) secBuffer.toArray(1);
	    	allocateMemory();	    	
	    }
	    
	    /**
	     * Create a new SecBufferDesc with one SecBuffer of a given type and size.
	     * @param type
	     * @param tokenSize
	     */
	    public SecBufferDesc(int type, int tokenSize) {
	    	ulVersion = new NativeLong(SECBUFFER_VERSION);
	    	cBuffers = new NativeLong(1);
	    	SecBuffer.ByReference secBuffer = new SecBuffer.ByReference(type, tokenSize);
	    	pBuffers = (SecBuffer.ByReference[]) secBuffer.toArray(1);
	    	allocateMemory();
	    }	    	
	    
	    public byte[] getBytes() {
	    	if (pBuffers == null || cBuffers == null) {
	    		throw new RuntimeException("pBuffers | cBuffers");
	    	}
	    	if (cBuffers.intValue() == 1) {
	    		return pBuffers[0].getBytes();
	    	}	    	
	    	throw new RuntimeException("cBuffers > 1");
	    }
	}
	
	/**
	 * A security integer.
	 */
	public static class SECURITY_INTEGER extends Structure {
		public NativeLong dwLower;
		public NativeLong dwUpper;

		/**
		 * An security integer of 0.
		 */
		public SECURITY_INTEGER() {
			dwLower = new NativeLong(0);
			dwUpper = new NativeLong(0);
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
		
		/**
		 * The first entry in an array of SecPkgInfo structures.
		 */
		public SecPkgInfo.ByReference pPkgInfo;
		
		public PSecPkgInfo() {
			
		}
		
		/**
		 * An array of SecPkgInfo structures.
		 */
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
    	
		/**
		 * Set of bit flags that describes the capabilities of the security package.
		 */
		public NativeLong fCapabilities;  
		/**
		 * Specifies the version of the package protocol. Must be 1. 
		 */
		public short wVersion;
		/**
		 * Specifies a DCE RPC identifier, if appropriate. If the package does not implement one of 
		 * the DCE registered security systems, the reserved value SECPKG_ID_NONE is used. 
		 */
		public short wRPCID;
		/**
		 * Specifies the maximum size, in bytes, of the token. 
		 */
		public NativeLong cbMaxToken;
		/**
		 * Pointer to a null-terminated string that contains the name of the security package.
		 */
		public WString Name;
		/**
		 * Pointer to a null-terminated string. This can be any additional string passed 
		 * back by the package. 
		 */
		public WString Comment;
		
		/**
		 * Create a new package info.
		 */
		public SecPkgInfo() {
			fCapabilities = new NativeLong(0);
			wVersion = 1;
			wRPCID = 0;
			cbMaxToken = new NativeLong(0);
		}
	}
}
