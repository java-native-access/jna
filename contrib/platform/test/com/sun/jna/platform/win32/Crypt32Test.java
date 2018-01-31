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
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import sun.security.tools.keytool.*;
import sun.security.x509.X500Name;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.platform.win32.WinCrypt.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinDef.DWORD;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Crypt32Test extends TestCase {
	
	/**
	* Track if the test certificate was created during the test.
	*/
	private boolean createdCertificate = false;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Crypt32Test.class);
    }
	
    protected void setUp() {
		HCERTSTORE hCertStore = Crypt32.INSTANCE.CertOpenSystemStore(Pointer.NULL, "MY");
		WString myString = new WString("cryptsigntest");
		Pointer pvFindPara = new Memory((myString.length() + 1) * 2);
		pvFindPara.setString(0, myString);

		CERT_CONTEXT.ByReference pc = Crypt32.INSTANCE.CertFindCertificateInStore(hCertStore,
				(WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING), 0, WinCrypt.CERT_FIND_SUBJECT_STR,
				pvFindPara, null);
		
		if (pc == null) {
			createdCertificate = createTestCertificate();
		}
    }

    protected void tearDown() {
    	if(createdCertificate) {
    		removeTestCertificate();
    	}
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
	
	public void testCertOpenSystemStore() {
		assertNotNull(
				"Should return a handle. If a store with the system store name does not exist, the method should create one.",
				Crypt32.INSTANCE.CertOpenSystemStore(Pointer.NULL, "test"));
	}

	public void testCryptSignMessage() {
		HCERTSTORE hCertStore = Crypt32.INSTANCE.CertOpenSystemStore(Pointer.NULL, "MY");
		WString myString = new WString("cryptsigntest");
		Pointer pvFindPara = new Memory((myString.length() + 1) * 2);
		pvFindPara.setString(0, myString);

		CERT_CONTEXT.ByReference pc = Crypt32.INSTANCE.CertFindCertificateInStore(hCertStore,
				(WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING), 0, WinCrypt.CERT_FIND_SUBJECT_STR,
				pvFindPara, null);

		if (pc == null && createTestCertificate() == false) {
			return;
		}

		pc = Crypt32.INSTANCE.CertFindCertificateInStore(hCertStore,
				(WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING), 0, WinCrypt.CERT_FIND_SUBJECT_STR,
				pvFindPara, null);
		
		if(pc.pCertInfo.cExtension > 0) {
			assertTrue(pc.pCertInfo.cExtension == pc.pCertInfo.getRgExtension().length);
		}
		
		CRYPT_SIGN_MESSAGE_PARA sigParams = new CRYPT_SIGN_MESSAGE_PARA();
		sigParams.cbSize = sigParams.size();
		sigParams.dwMsgEncodingType = WinCrypt.PKCS_7_ASN_ENCODING;
		sigParams.pSigningCert = pc;
		sigParams.HashAlgorithm.pszObjId = WinCrypt.szOID_RSA_SHA1RSA;
		sigParams.HashAlgorithm.Parameters.cbData = 0;
		sigParams.cMsgCert = 1;
		sigParams.setRgpMsgCert(new CERT_CONTEXT.ByReference[] { pc });
		sigParams.cAuthAttr = 0;
		sigParams.dwInnerContentType = 0;
		sigParams.cMsgCrl = 0;
		sigParams.cUnauthAttr = 0;
		sigParams.dwFlags = 0;
		sigParams.pvHashAuxInfo = Pointer.NULL;
		sigParams.rgAuthAttr = null;
		sigParams.rgpMsgCrl = null;
		sigParams.cUnauthAttr = 0;
		sigParams.rgUnauthAttr = null;

		String message = "test1";
		Pointer[] rgpbToBeSigned1 = { new Memory(message.length() + 1), new Memory(message.length() + 1) };
		rgpbToBeSigned1[0].setString(0, message);
		rgpbToBeSigned1[1].setString(0, message);
		DWORD[] rgcbToBeSigned1 = { new DWORD(message.length() + 1), new DWORD(message.length() + 1) };

		IntByReference pcbSignedBlob1 = new IntByReference(0);
		Crypt32.INSTANCE.CryptSignMessage(sigParams, false, rgpbToBeSigned1.length, rgpbToBeSigned1, rgcbToBeSigned1,
				Pointer.NULL, pcbSignedBlob1);
		int signedSize1 = pcbSignedBlob1.getValue();

		Pointer[] rgpbToBeSigned2 = { new Memory(message.length() + 1) };
		rgpbToBeSigned1[0].setString(0, message);
		DWORD[] rgcbToBeSigned2 = { new DWORD(message.length() + 1) };

		IntByReference pcbSignedBlob2 = new IntByReference(0);
		Crypt32.INSTANCE.CryptSignMessage(sigParams, false, rgpbToBeSigned2.length, rgpbToBeSigned2, rgcbToBeSigned2,
				Pointer.NULL, pcbSignedBlob2);
		int signedSize2 = pcbSignedBlob2.getValue();

		assertTrue("Buffer size 1 should be greater than buffer size 2", signedSize1 > signedSize2);
	}

	public void testCertGetCertificateChain() {
		HCERTSTORE hCertStore = Crypt32.INSTANCE.CertOpenSystemStore(Pointer.NULL, "MY");
		WString myString = new WString("cryptsigntest");
		Pointer pvFindPara = new Memory((myString.length() + 1) * 2);
		pvFindPara.setString(0, myString);

		CERT_CONTEXT.ByReference pc = Crypt32.INSTANCE.CertFindCertificateInStore(hCertStore,
				(WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING), 0, WinCrypt.CERT_FIND_SUBJECT_STR,
				pvFindPara, null);

		if (pc == null && createTestCertificate() == false) {
			return;
		}

		pc = Crypt32.INSTANCE.CertFindCertificateInStore(hCertStore,
				(WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING), 0, WinCrypt.CERT_FIND_SUBJECT_STR,
				pvFindPara, null);

		CERT_CHAIN_CONTEXT pChainContext = new CERT_CHAIN_CONTEXT();
		CERT_CHAIN_PARA pChainPara = new CERT_CHAIN_PARA();

		pChainPara.cbSize = pChainPara.size();
		pChainPara.RequestedUsage.dwType = WinCrypt.USAGE_MATCH_TYPE_AND;
		pChainPara.RequestedUsage.Usage.cUsageIdentifier = 0;
		pChainPara.RequestedUsage.Usage.rgpszUsageIdentifier = null;

		boolean status = Crypt32.INSTANCE.CertGetCertificateChain(null, pc, null, null, pChainPara, 0, null,
				pChainContext);
		assertTrue("Assert that the operation succeeded when done with a valid certificate.", status);
		assertNotNull("Assert that a returned certificate chain context was returned.", pChainContext);
		assertEquals("Error status of 0 says that the operation completed successfully", 0,
				pChainContext.TrustStatus.dwErrorStatus);
	}


	public void testCertFreeCertificateContext() {
		// Initialize the signature structure.
		CERT_CONTEXT certContext = new CERT_CONTEXT();
		certContext.pCertInfo = new CERT_INFO.ByReference();
		boolean status = Crypt32.INSTANCE.CertFreeCertificateContext(null);
		assertTrue("The status would be true since a valid certificate was not passed in.", status);
	}

	public void testCertCloseStore() {
		boolean status = Crypt32.INSTANCE.CertCloseStore(null, 0);

		assertTrue("The status would be true since a valid handle was not passed in.", status);
	}

	public void testCertNameToStr() {
		// Initialize the signature structure.
		DATA_BLOB blob = new DATA_BLOB("jna");
		int size = Crypt32.INSTANCE.CertNameToStr(WinCrypt.X509_ASN_ENCODING, blob, WinCrypt.CERT_SIMPLE_NAME_STR, null,
				0);

		assertEquals("The size would be one since a valid certificate context was not used.", 1, size);
	}

	public void testCertVerifyCertificateChainPolicy() {
		CERT_CHAIN_CONTEXT pChainContext = new CERT_CHAIN_CONTEXT();

		CERT_CHAIN_POLICY_PARA ChainPolicyPara = new CERT_CHAIN_POLICY_PARA();
		CERT_CHAIN_POLICY_STATUS PolicyStatus = new CERT_CHAIN_POLICY_STATUS();

		ChainPolicyPara.cbSize = ChainPolicyPara.size();
		ChainPolicyPara.dwFlags = 0;

		PolicyStatus.cbSize = PolicyStatus.size();
		boolean status = Crypt32.INSTANCE.CertVerifyCertificateChainPolicy(
				new LPSTR(Pointer.createConstant(WinCrypt.CERT_CHAIN_POLICY_BASE)), pChainContext, ChainPolicyPara,
				PolicyStatus);
		assertTrue("The status would be true since a valid certificate chain was not passed in.", status);
	}

	private boolean createTestCertificate() {
		try {
			KeyStore keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
			keyStore.load(null, null);

			CertAndKeyGen certAndKeyGen = new CertAndKeyGen("RSA", "SHA256WithRSA", null);
			certAndKeyGen.generate(1024);

			X509Certificate certificate = certAndKeyGen.getSelfCertificate(new X500Name("CN=cryptsigntest"), 24 * 60 * 60);

			keyStore.setKeyEntry("cryptsigntest", certAndKeyGen.getPrivateKey(), null, new X509Certificate[] { certificate });
		} catch (Exception e) {
			System.out.println("Unable to complete test. Certificate creation failed.");
			return false;
		}

		return true;
	}
	
	private boolean removeTestCertificate() {
		try {
			KeyStore keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
			keyStore.load(null, null);
			keyStore.deleteEntry("cryptsigntest");
		} catch (Exception e) {
			System.out.println("Test certificate deletion failed.");
			return false;
		}

		return true;
	}
}