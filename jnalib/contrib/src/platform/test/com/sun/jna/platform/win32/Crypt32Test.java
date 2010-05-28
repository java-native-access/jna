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

import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.ptr.PointerByReference;

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
    	assertEquals("description", pDescription.getValue().getString(0, true));
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
    	assertEquals("description", pDescription.getValue().getString(0, true));
    	assertEquals("hello world", pDataDecrypted.pbData.getString(0));
    	Kernel32.INSTANCE.LocalFree(pDataEncrypted.pbData);
    	Kernel32.INSTANCE.LocalFree(pDataDecrypted.pbData);
    	Kernel32.INSTANCE.LocalFree(pDescription.getValue());
    }    
}