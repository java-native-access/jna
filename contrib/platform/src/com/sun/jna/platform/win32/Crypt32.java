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
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinCrypt.CRYPTPROTECT_PROMPTSTRUCT;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Crypt32.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface Crypt32 extends StdCallLibrary {
	
	Crypt32 INSTANCE = (Crypt32) Native.loadLibrary("Crypt32",
			Crypt32.class, W32APIOptions.UNICODE_OPTIONS);
	
	/**
	 * The CryptProtectData function performs encryption on the data in a DATA_BLOB
	 * structure. Typically, only a user with the same logon credential as the encrypter 
	 * can decrypt the data. In addition, the encryption and decryption usually must be
	 * done on the same computer.
	 * @param pDataIn
	 *  Pointer to a DATA_BLOB structure that contains the plaintext to be encrypted. 
	 * @param szDataDescr
	 *  String with a readable description of the data to be encrypted. This description 
	 *  string is included with the encrypted data. This parameter is optional and can 
	 *  be set to NULL, except on Windows 2000.
	 * @param pOptionalEntropy
	 *  Pointer to a DATA_BLOB structure that contains a password or other additional 
	 *  entropy used to encrypt the data. The DATA_BLOB structure used in the encryption 
	 *  phase must also be used in the decryption phase. This parameter can be set to NULL
	 *  for no additional entropy.
	 * @param pvReserved
	 *  Reserved for future use and must be set to NULL. 
	 * @param pPromptStruct
	 *  Pointer to a CRYPTPROTECT_PROMPTSTRUCT structure that provides information about 
	 *  where and when prompts are to be displayed and what the content of those prompts 
	 *  should be. This parameter can be set to NULL in both the encryption and decryption
	 *  phases.
	 * @param dwFlags
	 *  One of CRYPTPROTECT_LOCAL_MACHINE, CRYPTPROTECT_UI_FORBIDDEN, CRYPTPROTECT_AUDIT, 
	 *  CRYPTPROTECT_VERIFY_PROTECTION.
	 * @param pDataOut
	 *  Pointer to a DATA_BLOB structure that receives the encrypted data. When you have 
	 *  finished using the DATA_BLOB structure, free its pbData member by calling the 
	 *  LocalFree function. 
	 * @return
	 *  If the function succeeds, the function returns TRUE. If the function fails, 
	 *  it returns FALSE. For extended error information, call GetLastError.
	 */
	public boolean CryptProtectData(DATA_BLOB pDataIn, String szDataDescr,
			DATA_BLOB pOptionalEntropy, Pointer pvReserved, 
			CRYPTPROTECT_PROMPTSTRUCT pPromptStruct,
			int dwFlags,
			DATA_BLOB pDataOut);
	
	/**
	 * The CryptUnprotectData function decrypts and does an integrity check of the data in
	 * a DATA_BLOB structure. Usually, only a user with the same logon credentials as the
	 * encrypter can decrypt the data. In addition, the encryption and decryption must be
	 * done on the same computer.  
	 * @param pDataIn
	 *  Pointer to a DATA_BLOB structure that holds the encrypted data. The DATA_BLOB 
	 *  structure's cbData member holds the length of the pbData member's byte string that 
	 *  contains the text to be encrypted.  
	 * @param szDataDescr
	 *  Pointer to a string-readable description of the encrypted data included with the 
	 *  encrypted data. This parameter can be set to NULL. When you have finished using 
	 *  ppszDataDescr, free it by calling the LocalFree function. 
	 * @param pOptionalEntropy
	 *  Pointer to a DATA_BLOB structure that contains a password or other additional 
	 *  entropy used when the data was encrypted. This parameter can be set to NULL; 
	 *  however, if an optional entropy DATA_BLOB structure was used in the encryption 
	 *  phase, that same DATA_BLOB structure must be used for the decryption phase.
	 * @param pvReserved
	 *  Reserved for future use; must be set to NULL. 
	 * @param pPromptStruct
	 *  Pointer to a CRYPTPROTECT_PROMPTSTRUCT structure that provides information about 
	 *  where and when prompts are to be displayed and what the content of those prompts 
	 *  should be. This parameter can be set to NULL. 
 	 * @param dwFlags
	 *  DWORD value that specifies options for this function. This parameter can be zero, 
	 *  in which case no option is set, or CRYPTPROTECT_UI_FORBIDDEN. 
	 * @param pDataOut
	 *  Pointer to a DATA_BLOB structure where the function stores the decrypted data. 
	 *  When you have finished using the DATA_BLOB structure, free its pbData member by
	 *  calling the LocalFree function. 
	 * @return
	 *  If the function succeeds, the return value is TRUE. If the function fails, the 
	 *  return value is FALSE.
	 */
	public boolean CryptUnprotectData(DATA_BLOB pDataIn, PointerByReference szDataDescr,
			DATA_BLOB pOptionalEntropy, Pointer pvReserved, 
			CRYPTPROTECT_PROMPTSTRUCT pPromptStruct,
			int dwFlags,
			DATA_BLOB pDataOut);
}
