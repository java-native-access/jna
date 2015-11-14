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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.PointerByReference;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Crypt32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Crypt32Test.class);
    }
    
    public void testCryptProtectUnprotectData() {
    	DATA_BLOB pDataIn = new DATA_BLOB("hello world");
    	DATA_BLOB pDataEncrypted = new DATA_BLOB();
    	assertTrue(Crypt32.INSTANCE.CryptProtectData(pDataIn, "description", 
    			null, null, null, 0, pDataEncrypted));
    	PointerByReference pDescription = new PointerByReference();
    	DATA_BLOB pDataDecrypted = new DATA_BLOB();
    	assertTrue(Crypt32.INSTANCE.CryptUnprotectData(pDataEncrypted, pDescription, 
    			null, null, null, 0, pDataDecrypted));
    	assertEquals("description", pDescription.getValue().getWideString(0));
    	assertEquals("hello world", pDataDecrypted.pbData.getString(0));
    	Kernel32.INSTANCE.LocalFree(pDataEncrypted.pbData);
    	Kernel32.INSTANCE.LocalFree(pDataDecrypted.pbData);
    	Kernel32.INSTANCE.LocalFree(pDescription.getValue());
    }
    
    public void testCryptProtectUnprotectDataWithEntropy() {
    	DATA_BLOB pDataIn = new DATA_BLOB("hello world");
    	DATA_BLOB pDataEncrypted = new DATA_BLOB();
    	DATA_BLOB pEntropy = new DATA_BLOB("entropy");
    	assertTrue(Crypt32.INSTANCE.CryptProtectData(pDataIn, "description", 
    			pEntropy, null, null, 0, pDataEncrypted));
    	PointerByReference pDescription = new PointerByReference();
    	DATA_BLOB pDataDecrypted = new DATA_BLOB();
    	// can't decrypt without entropy
    	assertFalse(Crypt32.INSTANCE.CryptUnprotectData(pDataEncrypted, pDescription, 
    			null, null, null, 0, pDataDecrypted));
    	// decrypt with entropy
    	assertTrue(Crypt32.INSTANCE.CryptUnprotectData(pDataEncrypted, pDescription, 
    			pEntropy, null, null, 0, pDataDecrypted));
    	assertEquals("description", pDescription.getValue().getWideString(0));
    	assertEquals("hello world", pDataDecrypted.pbData.getString(0));
    	Kernel32.INSTANCE.LocalFree(pDataEncrypted.pbData);
    	Kernel32.INSTANCE.LocalFree(pDataDecrypted.pbData);
    	Kernel32.INSTANCE.LocalFree(pDescription.getValue());
    }  

	public void testCertAddEncodedCertificateToSystemStore() {
		// try to install a non-existent certificate
		assertFalse(Crypt32.INSTANCE.CertAddEncodedCertificateToSystemStore("ROOT", null, new DWORD(0)));
		// should fail with "unexpected end of data"
		assertEquals(WinCrypt.CRYPT_E_ASN1_EOD, Native.getLastError());
	}
}