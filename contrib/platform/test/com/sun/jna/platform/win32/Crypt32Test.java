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
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
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
    	try {
        	assertTrue("CryptProtectData(Initial)",
        	        Crypt32.INSTANCE.CryptProtectData(pDataIn, "description",
        	                null, null, null, 0, pDataEncrypted));
        	PointerByReference pDescription = new PointerByReference();
        	try {
                DATA_BLOB pDataDecrypted = new DATA_BLOB();
                try {
                	assertTrue("CryptProtectData(Crypt)",
                	        Crypt32.INSTANCE.CryptUnprotectData(pDataEncrypted, pDescription,
                	                null, null, null, 0, pDataDecrypted));
                	assertEquals("description", pDescription.getValue().getWideString(0));
                	assertEquals("hello world", pDataDecrypted.pbData.getString(0));
                } finally {
                    Kernel32Util.freeLocalMemory(pDataDecrypted.pbData);
                }
        	} finally {
                Kernel32Util.freeLocalMemory(pDescription.getValue());
        	}
    	} finally {
    	    Kernel32Util.freeLocalMemory(pDataEncrypted.pbData);
    	}
    }

    public void testCryptProtectUnprotectDataWithEntropy() {
    	DATA_BLOB pDataIn = new DATA_BLOB("hello world");
        DATA_BLOB pEntropy = new DATA_BLOB("entropy");
    	DATA_BLOB pDataEncrypted = new DATA_BLOB();
    	try {
        	assertTrue("CryptProtectData(Initial)",
        	        Crypt32.INSTANCE.CryptProtectData(pDataIn, "description",
        	                pEntropy, null, null, 0, pDataEncrypted));
        	PointerByReference pDescription = new PointerByReference();
        	try {
            	DATA_BLOB pDataDecrypted = new DATA_BLOB();
            	try {
                	// can't decrypt without entropy
                	assertFalse("CryptUnprotectData(NoEntropy)",
                	        Crypt32.INSTANCE.CryptUnprotectData(pDataEncrypted, pDescription,
                	                null, null, null, 0, pDataDecrypted));
                	// decrypt with entropy
                	assertTrue("CryptUnprotectData(WithEntropy)",
                	        Crypt32.INSTANCE.CryptUnprotectData(pDataEncrypted, pDescription,
                	                pEntropy, null, null, 0, pDataDecrypted));
                	assertEquals("description", pDescription.getValue().getWideString(0));
                	assertEquals("hello world", pDataDecrypted.pbData.getString(0));
            	} finally {
                    Kernel32Util.freeLocalMemory(pDataDecrypted.pbData);
            	}
        	} finally {
                Kernel32Util.freeLocalMemory(pDescription.getValue());
        	}
    	} finally {
            Kernel32Util.freeLocalMemory(pDataEncrypted.pbData);
    	}
    }

	public void testCertAddEncodedCertificateToSystemStore() {
		// try to install a non-existent certificate
		assertFalse("Attempting to install a non-existent certificate should have returned false and set GetLastError()", Crypt32.INSTANCE.CertAddEncodedCertificateToSystemStore("ROOT", null, 0));
		// should fail with "unexpected end of data"
		assertEquals("GetLastError() should have been set to CRYPT_E_ASN1_EOD ('ASN.1 unexpected end of data' in WinCrypt.h)", WinCrypt.CRYPT_E_ASN1_EOD, Native.getLastError());
	}
}