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
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from WinCrypt.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface WinCrypt extends StdCallLibrary {
	
	/**
	 * The CryptoAPI CRYPTOAPI_BLOB structure is used for an arbitrary array of bytes.
	 */
	public static class DATA_BLOB extends Structure {
		public DATA_BLOB() {
			super();
		}
		
		public DATA_BLOB(Pointer memory) {
			useMemory(memory);
			read();
		}

		public DATA_BLOB(byte [] data) {
			pbData = new Memory(data.length);
			pbData.write(0, data, 0, data.length);
			cbData = data.length;
			allocateMemory();
		}
		
		public DATA_BLOB(String s) {
			this(Native.toByteArray(s));
		}
		
		/**
		 * The count of bytes in the buffer pointed to by pbData. 
		 */
        public int cbData;
        /**
         * A pointer to a block of data bytes. 
         */
        public Pointer pbData;
        
        /**
         * Get byte data.
         * @return
         *  Byte data or null.
         */
        public byte[] getData() {
        	return pbData == null ? null : pbData.getByteArray(0, cbData);
        }
	} 
	
	/**
	 * The CRYPTPROTECT_PROMPTSTRUCT structure provides the text of a prompt and 
	 * information about when and where that prompt is to be displayed when using
	 * the CryptProtectData and CryptUnprotectData functions. 
	 */
	public static class CRYPTPROTECT_PROMPTSTRUCT extends Structure {
		public CRYPTPROTECT_PROMPTSTRUCT() {
			super();
		}

		public CRYPTPROTECT_PROMPTSTRUCT(Pointer memory) {
			useMemory(memory);
			read();
		}
		
		/**
		 * Size of this structure in bytes.
		 */
	    public int cbSize;
	    /**
	     * DWORD flags that indicate when prompts to the user are to be displayed.
	     */
	    public int dwPromptFlags;
	    /**
	     * Window handle to the parent window. 
	     */
	    public HWND hwndApp;
	    /**
	     * A string containing the text of a prompt to be displayed. 
	     */
	    public String szPrompt;
	}
	
	//
	// CryptProtect PromptStruct dwPromtFlags
	//

	/**
	 * Prompt on unprotect.
	 */
	public static final int CRYPTPROTECT_PROMPT_ON_UNPROTECT = 0x1; // 1<<0
	/**
	 * Prompt on protect.
	 */
	public static final int CRYPTPROTECT_PROMPT_ON_PROTECT = 0x2; // 1<<1
	/**
	 * Reserved, don't use.
	 */
	public static final int CRYPTPROTECT_PROMPT_RESERVED = 0x04; 
	/**
	 * Default to strong variant UI protection (user supplied password currently).
	 */
	public static final int CRYPTPROTECT_PROMPT_STRONG = 0x08; // 1<<3
	/**
	 * Require strong variant UI protection (user supplied password currently).
	 */
	public static final int CRYPTPROTECT_PROMPT_REQUIRE_STRONG = 0x10; // 1<<4

	//
	// CryptProtectData and CryptUnprotectData dwFlags
	//
	/**
	 * For remote-access situations where ui is not an option, if UI was specified 
	 * on protect or unprotect operation, the call will fail and GetLastError() will 
	 * indicate ERROR_PASSWORD_RESTRICTION.
	 */
	public static final int CRYPTPROTECT_UI_FORBIDDEN = 0x1;
	/**
	 * Per machine protected data -- any user on machine where CryptProtectData 
	 * took place may CryptUnprotectData.
	 */
	public static final int CRYPTPROTECT_LOCAL_MACHINE = 0x4;
	/**
	 * Force credential synchronize during CryptProtectData() 
	 * Synchronize is only operation that occurs during this operation.
	 */
	public static final int CRYPTPROTECT_CRED_SYNC = 0x8;
	/**
	 * Generate an Audit on protect and unprotect operations.
	 */
	public static final int CRYPTPROTECT_AUDIT = 0x10;
	/**
	 * Protect data with a non-recoverable key.
	 */
	public static final int CRYPTPROTECT_NO_RECOVERY = 0x20;
	/**
	 * Verify the protection of a protected blob.
	 */
	public static final int CRYPTPROTECT_VERIFY_PROTECTION = 0x40;
	/**
	 * Regenerate the local machine protection.
	 */
	public static final int CRYPTPROTECT_CRED_REGENERATE = 0x80;
}
