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
import java.security.*;
import java.security.cert.X509Certificate;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32StringUtil;
import com.sun.jna.platform.win32.WinCrypt.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinCryptUtil.MANAGED_CRYPT_SIGN_MESSAGE_PARA;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Crypt32Test extends TestCase {

    private static final Logger LOG = Logger.getLogger(Crypt32Test.class.getName());

    private static final String TESTCERT_CN = "cryptsigntest";

    /**
     * Track if the test certificate was created during the test.
     */
    private boolean createdCertificate = false;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Crypt32Test.class);
    }

    @Override
    protected void setUp() {
        HCERTSTORE hCertStore = Crypt32.INSTANCE.CertOpenSystemStore(Pointer.NULL, "MY");

        CERT_CONTEXT.ByReference pc = Crypt32.INSTANCE.CertFindCertificateInStore(
                hCertStore,
                (WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING),
                0,
                WinCrypt.CERT_FIND_SUBJECT_STR,
                new WTypes.LPWSTR(TESTCERT_CN).getPointer(),
                null);

        if (pc == null) {
            createdCertificate = createTestCertificate();
        }
    }

    @Override
    protected void tearDown() {
        if(createdCertificate) {
            removeTestCertificate();
        }
    }

    public void testCryptProtectUnprotectData() {
        final byte[] payload = Native.toByteArray("hello world");
        final String description = "description";

        DATA_BLOB pDataIn = new DATA_BLOB(payload);
        DATA_BLOB pDataEncrypted = new DATA_BLOB();
        try {
            assertTrue("CryptProtectData(Initial)",
                    Crypt32.INSTANCE.CryptProtectData(pDataIn, description,
                            null, null, null, 0, pDataEncrypted));
            PointerByReference pDescription = new PointerByReference();
            try {
                DATA_BLOB pDataDecrypted = new DATA_BLOB();
                try {
                    assertTrue("CryptProtectData(Crypt)",
                            Crypt32.INSTANCE.CryptUnprotectData(pDataEncrypted, pDescription,
                                    null, null, null, 0, pDataDecrypted));
                    assertEquals(description, W32StringUtil.toString(pDescription.getValue()));
                    assertArrayEquals(payload, pDataDecrypted.getData());
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
        final byte[] payload = Native.toByteArray("hello world");
        final String description = "description";

        DATA_BLOB pDataIn = new DATA_BLOB(payload);
        DATA_BLOB pEntropy = new DATA_BLOB("entropy");
        DATA_BLOB pDataEncrypted = new DATA_BLOB();
        try {
            assertTrue("CryptProtectData(Initial)",
                    Crypt32.INSTANCE.CryptProtectData(pDataIn, description,
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
                    assertEquals(description, W32StringUtil.toString(pDescription.getValue()));
                    assertArrayEquals(payload, pDataDecrypted.getData());
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

    public void testCryptSignMessage() {
        // Open user keystore
        HCERTSTORE hCertStore = Crypt32.INSTANCE.CertOpenSystemStore(Pointer.NULL, "MY");

        assertNotNull(hCertStore);

        // Acquire test certificate (created in setup routine)
        CERT_CONTEXT.ByReference signCertContext = Crypt32.INSTANCE.CertFindCertificateInStore(
                hCertStore,
                (WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING),
                0,
                WinCrypt.CERT_FIND_SUBJECT_STR,
                new WTypes.LPWSTR(TESTCERT_CN).getPointer(),
                null);

        assertNotNull(signCertContext);

        if (signCertContext.pCertInfo.cExtension > 0) {
            assertTrue(signCertContext.pCertInfo.cExtension == signCertContext.pCertInfo.getRgExtension().length);
        }

        // Create sample message
        String message1String = "Message Part 1 öäü";
        Memory message1 = new Memory((message1String.length() + 1) * Native.WCHAR_SIZE);
        message1.setWideString(0, message1String);

        // Prepare signing
        MANAGED_CRYPT_SIGN_MESSAGE_PARA sigParams = new MANAGED_CRYPT_SIGN_MESSAGE_PARA();
        sigParams.dwMsgEncodingType = WinCrypt.X509_ASN_ENCODING | WinCrypt.PKCS_7_ASN_ENCODING;
        sigParams.pSigningCert = signCertContext;
        sigParams.HashAlgorithm.pszObjId = WinCrypt.szOID_RSA_SHA1RSA;
        sigParams.HashAlgorithm.Parameters.cbData = 0;
        sigParams.setRgpMsgCert(new CERT_CONTEXT[]{signCertContext});

        Pointer[] rgpbToBeSigned = {message1};
        int[] rgcbToBeSigned = {(int) message1.size()};

        IntByReference pcbSignedBlob1 = new IntByReference(0);

        // First determine required size of the required buffer
        boolean result = Crypt32.INSTANCE.CryptSignMessage(
                sigParams,
                false,
                rgpbToBeSigned.length,
                rgpbToBeSigned,
                rgcbToBeSigned,
                Pointer.NULL,
                pcbSignedBlob1);

        assertTrue("Failed to determine buffer size required for signing", result);

        Memory resultBuffer = new Memory(pcbSignedBlob1.getValue());

        // Create signed/encrypted buffer
        result = Crypt32.INSTANCE.CryptSignMessage(
                sigParams,
                false,
                rgpbToBeSigned.length,
                rgpbToBeSigned,
                rgcbToBeSigned,
                resultBuffer,
                pcbSignedBlob1);

        assertTrue("Failed to sign buffer", result);

        // Prepare verification
        CRYPT_VERIFY_MESSAGE_PARA verifyMessagePara = new CRYPT_VERIFY_MESSAGE_PARA();
        verifyMessagePara.dwMsgAndCertEncodingType = WinCrypt.X509_ASN_ENCODING | WinCrypt.PKCS_7_ASN_ENCODING;
        verifyMessagePara.hCryptProv = null;
        verifyMessagePara.pfnGetSignerCertificate = null;
        verifyMessagePara.pvGetArg = null;

        IntByReference decodedBlobSize = new IntByReference();

        // Determine required buffer size for decoded buffer
        result = Crypt32.INSTANCE.CryptVerifyMessageSignature(
                verifyMessagePara, 0, resultBuffer,
                (int) resultBuffer.size(), null,
                decodedBlobSize, null);

        assertTrue("Failed to determine buffer size required for verification", result);

        Memory resultBuffer2 = new Memory(decodedBlobSize.getValue());

        PointerByReference certContextPointer = new PointerByReference();

        result = Crypt32.INSTANCE.CryptVerifyMessageSignature(
                verifyMessagePara, 0, resultBuffer,
                (int) resultBuffer.size(), resultBuffer2,
                decodedBlobSize, certContextPointer);

        assertTrue("Verification failed", result);
        assertEquals(message1String, resultBuffer2.getWideString(0));

        assertNotNull(certContextPointer.getValue());
        CERT_CONTEXT resCertContext = Structure.newInstance(CERT_CONTEXT.class, certContextPointer.getValue());

        Crypt32.INSTANCE.CertFreeCertificateContext(signCertContext);
        Crypt32.INSTANCE.CertFreeCertificateContext(resCertContext);

        assertTrue("CERT_CONTEXT or CERT_CHAIN_CONTEXT were not correctly freed.",
                Crypt32.INSTANCE.CertCloseStore(hCertStore, WinCrypt.CERT_CLOSE_STORE_CHECK_FLAG));
    }

    public void testCertGetCertificateChain() throws IOException {
        byte[] testP12 = getBytes("/res/test.p12");
        DATA_BLOB testP12Blob = new DATA_BLOB(testP12);

        HCERTSTORE hCertStore = Crypt32.INSTANCE.PFXImportCertStore(testP12Blob, new WTypes.LPWSTR("test"), 0);

        assertNotNull(hCertStore);

        CERT_CONTEXT.ByReference pc = Crypt32.INSTANCE.CertFindCertificateInStore(hCertStore,
                (WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING), 0, WinCrypt.CERT_FIND_SUBJECT_STR,
                new WTypes.LPWSTR("www.doppel-helix.eu").getPointer(), null);

        assertNotNull(pc);

        CERT_CHAIN_PARA pChainPara = new CERT_CHAIN_PARA();

        pChainPara.cbSize = pChainPara.size();
        pChainPara.RequestedUsage.dwType = WinCrypt.USAGE_MATCH_TYPE_AND;
        pChainPara.RequestedUsage.Usage.cUsageIdentifier = 0;
        pChainPara.RequestedUsage.Usage.rgpszUsageIdentifier = null;

        PointerByReference pbr = new PointerByReference();
        boolean status = Crypt32.INSTANCE.CertGetCertificateChain(null, pc, null, null, pChainPara, 0, null,
                pbr);

        assertTrue("Assert that the operation succeeded when done with a valid certificate.", status);
        assertNotNull("Assert that a returned certificate chain context was returned.", pbr.getValue());
        CERT_CHAIN_CONTEXT pChainContext = Structure.newInstance(CERT_CHAIN_CONTEXT.class, pbr.getValue());
        pChainContext.read();

        Pointer[] chainPointers = new Pointer[pChainContext.cChain];
        pChainContext.rgpChain.read(0, chainPointers, 0, chainPointers.length);

        CERT_SIMPLE_CHAIN csc = Structure.newInstance(CERT_SIMPLE_CHAIN.class, chainPointers[0]);
        csc.read();
        CERT_CHAIN_ELEMENT[] element = csc.getRgpElement();
        assertEquals("Certificate chain does not contain 3 elements", 3, element.length);
        for (int i = 0; i < element.length; i++) {
            String subjectName = Crypt32Util.CertNameToStr(
                    WinCrypt.X509_ASN_ENCODING,
                    WinCrypt.CERT_SIMPLE_NAME_STR,
                    element[i].pCertContext.pCertInfo.Subject);
            switch(i) {
                case 0:
                    assertEquals("www.doppel-helix.eu", subjectName);
                    break;
                case 1:
                    assertEquals("US, Let's Encrypt, Let's Encrypt Authority X3", subjectName);
                    break;
                case 2:
                    assertEquals("Digital Signature Trust Co., DST Root CA X3", subjectName);
                    break;
            }
        }

        // Extract usage identifiers for root certificate
        String[] usagesArray = element[2].pApplicationUsage.getRgpszUsageIdentier();
        assertNotNull(usagesArray);
        assertEquals(6, usagesArray.length);
        List<String> usages = Arrays.asList(usagesArray);
        assertTrue(usages.contains("1.3.6.1.5.5.7.3.1")); // Indicates that a certificate can be used as an SSL server certificate.
        assertTrue(usages.contains("1.3.6.1.5.5.7.3.2")); // Indicates that a certificate can be used as an SSL client certificate.
        assertTrue(usages.contains("1.3.6.1.5.5.7.3.4")); // Indicates that a certificate can be used for protecting email (signing, encryption, key agreement).
        assertTrue(usages.contains("1.3.6.1.5.5.7.3.8")); // Indicates that a certificate can be used to bind the hash of an object to a time from a trusted time source.
        assertTrue(usages.contains("1.3.6.1.4.1.311.10.3.4")); // Can use encrypted file systems (EFS) - szOID_EFS_CRYPTO
        assertTrue(usages.contains("1.3.6.1.4.1.311.10.3.12")); // Signer of documents - szOID_KP_DOCUMENT_SIGNING

        Crypt32.INSTANCE.CertFreeCertificateChain(pChainContext);
        Crypt32.INSTANCE.CertFreeCertificateContext(pc);

        assertTrue("CERT_CONTEXT or CERT_CHAIN_CONTEXT were not correctly freed.",
                Crypt32.INSTANCE.CertCloseStore(hCertStore, WinCrypt.CERT_CLOSE_STORE_CHECK_FLAG));
    }

    public void testCertNameToStr() {
        // Open user keystore
        HCERTSTORE hCertStore = Crypt32.INSTANCE.CertOpenSystemStore(Pointer.NULL, "MY");

        // Acquire test certificate (created in setup routine)
        CERT_CONTEXT.ByReference pc = Crypt32.INSTANCE.CertFindCertificateInStore(
                hCertStore,
                (WinCrypt.PKCS_7_ASN_ENCODING | WinCrypt.X509_ASN_ENCODING),
                0,
                WinCrypt.CERT_FIND_SUBJECT_STR,
                new WTypes.LPWSTR(TESTCERT_CN).getPointer(),
                null);

        assertNotNull(pc);

        // Initialize the signature structure.
        int requiredSize = Crypt32.INSTANCE.CertNameToStr(
                WinCrypt.X509_ASN_ENCODING,
                pc.pCertInfo.Issuer,
                WinCrypt.CERT_SIMPLE_NAME_STR,
                Pointer.NULL,
                0);

        assertEquals("Issuer Name length repored incorrectly", TESTCERT_CN.length() + 1, requiredSize);

        Memory mem = W32StringUtil.allocateBuffer(requiredSize);

        Crypt32.INSTANCE.CertNameToStr(
                WinCrypt.X509_ASN_ENCODING,
                pc.pCertInfo.Issuer,
                WinCrypt.CERT_SIMPLE_NAME_STR,
                mem,
                requiredSize);

        assertEquals(TESTCERT_CN, W32StringUtil.toString(mem));

        String utilResult = Crypt32Util.CertNameToStr(WinCrypt.X509_ASN_ENCODING, WinCrypt.CERT_SIMPLE_NAME_STR, pc.pCertInfo.Issuer);

        assertEquals(TESTCERT_CN, utilResult);
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

            X500Name xx500Name = new X500Name("CN=cryptsigntest");

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair key = keyGen.generateKeyPair();
            PrivateKey privKey = key.getPrivate();
            PublicKey pubKey = key.getPublic();

            ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").build(privKey);
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                xx500Name,
                BigInteger.valueOf(1),
                new Date(System.currentTimeMillis() - 5 * 60 * 1000),
                new Date((long) (System.currentTimeMillis() + 24L * 60L * 60L * 1000L)),
                xx500Name, //Subject
                pubKey //Publickey to be associated with the certificate
            );

            Provider BC = new BouncyCastleProvider();
            X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certGen.build(sigGen));

            keyStore.setKeyEntry(TESTCERT_CN, privKey, null, new X509Certificate[]{certificate});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to complete test. Certificate creation failed.", e);
            return false;
        }

        return true;
    }

    private boolean removeTestCertificate() {
        try {
            KeyStore keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
            keyStore.load(null, null);
            keyStore.deleteEntry(TESTCERT_CN);
        } catch (Exception e) {
            System.out.println("Test certificate deletion failed.");
            return false;
        }

        return true;
    }

    private static byte[] getBytes(String path) throws IOException {
        InputStream is = Crypt32Test.class.getResourceAsStream(path);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read;
            byte[] buffer = new byte[10240];
            while ((read = is.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}