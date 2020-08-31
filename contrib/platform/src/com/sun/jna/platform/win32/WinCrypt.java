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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.W32APITypeMapper;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from WinCrypt.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface WinCrypt {

    /**
     * The CryptoAPI CRYPTOAPI_BLOB structure is used for an arbitrary array of bytes.
     */
    @FieldOrder({"cbData", "pbData"})
    public static class DATA_BLOB extends Structure {
        public static class ByReference extends DATA_BLOB implements Structure.ByReference {}

        /**
         * The count of bytes in the buffer pointed to by pbData.
         */
        public int cbData;
        /**
         * A pointer to a block of data bytes.
         */
        public Pointer pbData;

        public DATA_BLOB() {
            super();
        }

        public DATA_BLOB(Pointer memory) {
            super(memory);
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
         * Get byte data.
         * @return
         *  Byte data or null.
         */
        public byte[] getData() {
            return pbData == null ? null : pbData.getByteArray(0, cbData);
        }
    }

    /**
     * The CERT_TRUST_STATUS structure contains trust information about a
     * certificate in a certificate chain, summary trust information about a
     * simple chain of certificates, or summary information about an array of
     * simple chains.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377590(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwErrorStatus", "dwInfoStatus"})
    public static class CERT_TRUST_STATUS extends Structure {
        public static class ByReference extends CERT_TRUST_STATUS implements Structure.ByReference {
        }

        public int dwErrorStatus;
        public int dwInfoStatus;
    }

    /**
     * The CTL_ENTRY structure is an element of a certificate trust list (CTL).
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381487(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"SubjectIdentifier", "cAttribute", "rgAttribute"})
    public static class CTL_ENTRY extends Structure {
        public static class ByReference extends CTL_ENTRY implements Structure.ByReference {
        }

        public DATA_BLOB SubjectIdentifier;
        public int cAttribute;
        public Pointer rgAttribute;

        public CRYPT_ATTRIBUTE[] getRgAttribute() {
            if (cAttribute == 0) {
                return new CRYPT_ATTRIBUTE[0];
            } else {
                CRYPT_ATTRIBUTE[] result = (CRYPT_ATTRIBUTE[]) Structure.newInstance(
                        CRYPT_ATTRIBUTE.class,
                        rgAttribute)
                        .toArray(cAttribute);
                result[0].read();
                return result;
            }
        }
    }

    /**
     * Contains information updated by a certificate revocation list (CRL)
     * revocation type handler. The CERT_REVOCATION_CRL_INFO structure is used
     * with both base and delta CRLs.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377509(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "pBaseCRLContext", "pDeltaCRLContext", "pCrlEntry",
                "fDeltaCrlEntry"})
    public static class CERT_REVOCATION_CRL_INFO extends Structure {
        public static class ByReference extends CERT_REVOCATION_CRL_INFO implements Structure.ByReference {
        }

        public int cbSize;
        public CRL_CONTEXT.ByReference pBaseCRLContext;
        public CRL_CONTEXT.ByReference pDeltaCRLContext;
        public CRL_ENTRY.ByReference pCrlEntry;
        public boolean fDeltaCrlEntry;

        public CERT_REVOCATION_CRL_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /**
     * The CERT_REVOCATION_INFO structure indicates the revocation status of a
     * certificate in a CERT_CHAIN_ELEMENT.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377519(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "dwRevocationResult", "pszRevocationOid",
        "pvOidSpecificInfo", "fHasFreshnessTime", "dwFreshnessTime", "pCrlInfo"})
    public static class CERT_REVOCATION_INFO extends Structure {
        public static class ByReference extends CERT_REVOCATION_INFO implements Structure.ByReference {
        }

        public int cbSize;
        public int dwRevocationResult;
        public String pszRevocationOid;
        public Pointer pvOidSpecificInfo;
        public boolean fHasFreshnessTime;
        public int dwFreshnessTime;
        public CERT_REVOCATION_CRL_INFO.ByReference pCrlInfo;

        public CERT_REVOCATION_INFO() {
            super(W32APITypeMapper.ASCII);
        }
    }

    /**
     * The CERT_CHAIN_ELEMENT structure is a single element in a simple
     * certificate chain. Each element has a pointer to a certificate context, a
     * pointer to a structure that indicates the error status and information
     * status of the certificate, and a pointer to a structure that indicates
     * the revocation status of the certificate.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377183(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "pCertContext", "TrustStatus", "pRevocationInfo",
        "pIssuanceUsage", "pApplicationUsage", "pwszExtendedErrorInfo"})
    public static class CERT_CHAIN_ELEMENT extends Structure {
        public static class ByReference extends CERT_CHAIN_ELEMENT implements Structure.ByReference {
        }

        public int cbSize;
        public CERT_CONTEXT.ByReference pCertContext;
        public CERT_TRUST_STATUS TrustStatus;
        public CERT_REVOCATION_INFO.ByReference pRevocationInfo;
        public CTL_USAGE.ByReference pIssuanceUsage;
        public CTL_USAGE.ByReference pApplicationUsage;

        public String pwszExtendedErrorInfo;

        public CERT_CHAIN_ELEMENT() {
            super(W32APITypeMapper.UNICODE);
        }

        public CERT_CHAIN_ELEMENT(Pointer p) {
            super(p, Structure.ALIGN_DEFAULT, W32APITypeMapper.UNICODE);
        }
    }

    /**
     * The CTL_INFO structure contains the information stored in a Certificate
     * Trust List (CTL).
     *
     * @see
     * <a href="https://msdn.microsoft.com/es-xl/library/windows/desktop/aa381491(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwVersion", "SubjectUsage", "ListIdentifier", "SequenceNumber",
                "ThisUpdate", "NextUpdate", "SubjectAlgorithm", "cCTLEntry",
                "rgCTLEntry", "cExtension", "rgExtension"})
    public static class CTL_INFO extends Structure {
        public static class ByReference extends CTL_INFO implements Structure.ByReference {
        }

        public int dwVersion;
        public CTL_USAGE SubjectUsage;
        public DATA_BLOB ListIdentifier;
        public DATA_BLOB SequenceNumber;
        public FILETIME ThisUpdate;
        public FILETIME NextUpdate;
        public CRYPT_ALGORITHM_IDENTIFIER SubjectAlgorithm;
        public int cCTLEntry;
        public Pointer rgCTLEntry;
        public int cExtension;
        public Pointer rgExtension;

        public CTL_ENTRY[] getRgCTLEntry() {
            if (cCTLEntry == 0) {
                return new CTL_ENTRY[0];
            } else {
                CTL_ENTRY[] result = (CTL_ENTRY[]) Structure.newInstance(
                        CTL_ENTRY.class,
                        rgCTLEntry)
                        .toArray(cCTLEntry);
                result[0].read();
                return result;
            }
        }

        public CERT_EXTENSION[] getRgExtension() {
            if (cExtension == 0) {
                return new CERT_EXTENSION[0];
            } else {
                CERT_EXTENSION[] result = (CERT_EXTENSION[]) Structure.newInstance(
                        CERT_EXTENSION.class,
                        rgExtension)
                        .toArray(cExtension);
                result[0].read();
                return result;
            }
        }
    }

    /**
     * The CTL_CONTEXT structure contains both the encoded and decoded
     * representations of a CTL. It also contains an opened HCRYPTMSG handle to
     * the decoded, cryptographically signed message containing the CTL_INFO as
     * its inner content.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381486(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwMsgAndCertEncodingType", "pbCtlEncoded", "cbCtlEncoded",
                "pCtlInfo", "hCertStore", "hCryptMsg", "pbCtlContent",
                "cbCtlContent"})
    public static class CTL_CONTEXT extends Structure {
        public static class ByReference extends CTL_CONTEXT implements Structure.ByReference {
        }

        public int dwMsgAndCertEncodingType;
        public Pointer pbCtlEncoded;
        public int cbCtlEncoded;
        public CTL_INFO.ByReference pCtlInfo;
        public HCERTSTORE hCertStore;
        public HCRYPTMSG hCryptMsg;
        public Pointer pbCtlContent;
        public int cbCtlContent;
    }

    /**
     * The CERT_TRUST_LIST_INFO structure that indicates valid usage of a CTL.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377585(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "pCtlEntry", "pCtlContext"})
    public static class CERT_TRUST_LIST_INFO extends Structure {
        public static class ByReference extends CERT_TRUST_LIST_INFO implements Structure.ByReference {
        }

        public int cbSize;
        public CTL_ENTRY.ByReference pCtlEntry;
        public CTL_CONTEXT.ByReference pCtlContext;
    }

    /**
     * The CTL_USAGE structure contains an array of object identifiers (OIDs)
     * for Certificate Trust List (CTL) extensions. CTL_USAGE structures are
     * used in functions that search for CTLs for specific uses.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381493(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cUsageIdentifier", "rgpszUsageIdentifier"})
    public static class CTL_USAGE extends Structure {

        public static class ByReference extends CTL_USAGE implements Structure.ByReference {
        }

        public int cUsageIdentifier;
        public Pointer rgpszUsageIdentifier;

        public CTL_USAGE() {
            super();
        }

        public String[] getRgpszUsageIdentier() {
            if (cUsageIdentifier == 0) {
                return new String[0];
            } else {
                return rgpszUsageIdentifier.getStringArray(0, cUsageIdentifier);
            }
        }

        public void setRgpszUsageIdentier(String[] array) {
            if (array == null || array.length == 0) {
                cUsageIdentifier = 0;
                rgpszUsageIdentifier = null;
            } else {
                cUsageIdentifier = array.length;
                rgpszUsageIdentifier = new StringArray(array);
            }
        }
    }

    /**
     * The CERT_USAGE_MATCH structure provides criteria for identifying issuer
     * certificates to be used to build a certificate chain.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377593(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwType", "Usage"})
    public static class CERT_USAGE_MATCH extends Structure {
        public static class ByReference extends CERT_USAGE_MATCH implements Structure.ByReference {
        }

        public int dwType;
        public CTL_USAGE Usage;
    }

    /**
     * The CERT_CHAIN_PARA structure establishes the searching and matching
     * criteria to be used in building a certificate chain.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377186(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "RequestedUsage", "RequestedIssuancePolicy",
                "dwUrlRetrievalTimeout", "fCheckRevocationFreshnessTime",
                "dwRevocationFreshnessTime", "pftCacheResync", "pStrongSignPara",
                "dwStrongSignFlags"})
    public static class CERT_CHAIN_PARA extends Structure {
        public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
        }

        public int cbSize;
        public CERT_USAGE_MATCH RequestedUsage;
        public CERT_USAGE_MATCH RequestedIssuancePolicy;
        public int dwUrlRetrievalTimeout;
        public boolean fCheckRevocationFreshnessTime;
        public int dwRevocationFreshnessTime;
        public FILETIME.ByReference pftCacheResync;
        public CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;
        public int dwStrongSignFlags;

        public CERT_CHAIN_PARA() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /**
     * Contains parameters used to check for strong signatures on certificates,
     * certificate revocation lists (CRLs), online certificate status protocol
     * (OCSP) responses, and PKCS #7 messages.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/hh870262(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "dwInfoChoice", "DUMMYUNIONNAME"})
    public static class CERT_STRONG_SIGN_PARA extends Structure {
        public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
        }

        public int cbSize;
        public int dwInfoChoice;
        public DUMMYUNION DUMMYUNIONNAME;

        public class DUMMYUNION extends Union {

            Pointer pvInfo;
            CERT_STRONG_SIGN_SERIALIZED_INFO.ByReference pSerializedInfo;
            LPSTR pszOID;
        }
    }

    /**
     * Contains the signature algorithm/hash algorithm and public key
     * algorithm/bit length pairs that can be used for strong signing. This
     * structure is used by the CERT_STRONG_SIGN_PARA structure.
     *
     * @see
     * <a href= "https://msdn.microsoft.com/en-us/library/windows/desktop/hh870263(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwFlags", "pwszCNGSignHashAlgids", "pwszCNGPubKeyMinBitLengths"})
    public static class CERT_STRONG_SIGN_SERIALIZED_INFO extends Structure {
        public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
        }

        public int dwFlags;
        public String pwszCNGSignHashAlgids;
        public String pwszCNGPubKeyMinBitLengths;

        public CERT_STRONG_SIGN_SERIALIZED_INFO() {
            super(W32APITypeMapper.UNICODE);
        }
    }

    /**
     * The CERT_CHAIN_POLICY_STATUS structure holds certificate chain status
     * information returned by the CertVerifyCertificateChainPolicy function
     * when the certificate chains are validated.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377188(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "dwError", "lChainIndex", "lElementIndex",
                "pvExtraPolicyStatus"})
    public static class CERT_CHAIN_POLICY_STATUS extends Structure {
        public static class ByReference extends CERT_CHAIN_POLICY_STATUS implements Structure.ByReference {
        }

        public int cbSize;
        public int dwError;
        public int lChainIndex;
        public int lElementIndex;
        public Pointer pvExtraPolicyStatus;
    }

    /**
     * The CERT_SIMPLE_CHAIN structure contains an array of chain elements and a
     * summary trust status for the chain that the array represents.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377544(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "TrustStatus", "cElement", "rgpElement", "pTrustListInfo",
                "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime"})
    public static class CERT_SIMPLE_CHAIN extends Structure {
        public static class ByReference extends CERT_SIMPLE_CHAIN implements Structure.ByReference {
        }

        public int cbSize;
        public CERT_TRUST_STATUS TrustStatus;
        public int cElement;
        public Pointer rgpElement;
        public CERT_TRUST_LIST_INFO.ByReference pTrustListInfo;

        public boolean fHasRevocationFreshnessTime;
        public int dwRevocationFreshnessTime;

        public CERT_SIMPLE_CHAIN() {
            super(W32APITypeMapper.DEFAULT);
        }

        public CERT_CHAIN_ELEMENT[] getRgpElement() {
            CERT_CHAIN_ELEMENT[] elements = new CERT_CHAIN_ELEMENT[cElement];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = Structure.newInstance(
                        CERT_CHAIN_ELEMENT.class,
                        rgpElement.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }
    }

    /**
     * The CERT_CHAIN_POLICY_PARA structure contains information used in
     * CertVerifyCertificateChainPolicy to establish policy criteria for the
     * verification of certificate chains.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377187(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "dwFlags", "pvExtraPolicyPara"})
    public static class CERT_CHAIN_POLICY_PARA extends Structure {
        public static class ByReference extends CERT_CHAIN_POLICY_PARA implements Structure.ByReference {
        }

        public int cbSize;
        public int dwFlags;
        public Pointer pvExtraPolicyPara;
    }

    /**
     * The CERT_CHAIN_CONTEXT structure contains an array of simple certificate
     * chains and a trust status structure that indicates summary validity data
     * on all of the connected simple chains.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377182(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "TrustStatus", "cChain", "rgpChain",
        "cLowerQualityChainContext", "rgpLowerQualityChainContext",
        "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime",
        "dwCreateFlags", "ChainId"})
    public static class CERT_CHAIN_CONTEXT extends Structure {
        public static class ByReference extends CERT_CHAIN_CONTEXT implements Structure.ByReference {
        }

        public int cbSize;
        public CERT_TRUST_STATUS TrustStatus;
        public int cChain;
        public Pointer rgpChain;
        public int cLowerQualityChainContext;
        public Pointer rgpLowerQualityChainContext;
        public boolean fHasRevocationFreshnessTime;
        public int dwRevocationFreshnessTime;
        public int dwCreateFlags;
        public GUID ChainId;

        public CERT_SIMPLE_CHAIN[] getRgpChain() {
            CERT_SIMPLE_CHAIN[] elements = new CERT_SIMPLE_CHAIN[cChain];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = Structure.newInstance(
                        CERT_SIMPLE_CHAIN.class,
                        rgpChain.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }

        public CERT_CHAIN_CONTEXT[] getRgpLowerQualityChainContext() {
            CERT_CHAIN_CONTEXT[] elements = new CERT_CHAIN_CONTEXT[cLowerQualityChainContext];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = Structure.newInstance(
                        CERT_CHAIN_CONTEXT.class,
                        rgpLowerQualityChainContext.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }

        public CERT_CHAIN_CONTEXT() {
            super(W32APITypeMapper.DEFAULT);
        }
    }

    /**
     * The CERT_CONTEXT structure contains both the encoded and decoded
     * representations of a certificate. A certificate context returned by one
     * of the functions defined in Wincrypt.h must be freed by calling the
     * CertFreeCertificateContext function. The CertDuplicateCertificateContext
     * function can be called to make a duplicate copy (which also must be freed
     * by calling CertFreeCertificateContext).
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377189(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwCertEncodingType", "pbCertEncoded", "cbCertEncoded",
        "pCertInfo", "hCertStore"})
    public static class CERT_CONTEXT extends Structure {
        public static class ByReference extends CERT_CONTEXT implements Structure.ByReference {
        }

        public int dwCertEncodingType;
        public Pointer pbCertEncoded;
        public int cbCertEncoded;
        public CERT_INFO.ByReference pCertInfo;
        public HCERTSTORE hCertStore;
    }

    /**
     * The CERT_EXTENSION structure contains the extension information for a
     * certificate, Certificate Revocation List (CRL) or Certificate Trust List
     * (CTL).
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377195(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"pszObjId", "fCritical", "Value"})
    public static class CERT_EXTENSION extends Structure {
        public static class ByReference extends CERT_EXTENSION implements Structure.ByReference {
        }

        public String pszObjId;
        public boolean fCritical;
        public DATA_BLOB Value;

        public CERT_EXTENSION() {
            super(W32APITypeMapper.ASCII);
        }
    }

    /**
     * The CERT_EXTENSIONS structure contains an array of extensions.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377196(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cExtension", "rgExtension"})
    public static class CERT_EXTENSIONS extends Structure {
        public static class ByReference extends CERT_EXTENSIONS implements Structure.ByReference {
        }

        public int cExtension;
        public Pointer rgExtension;

        public CERT_EXTENSION[] getRgExtension() {
            if(cExtension == 0) {
                return new CERT_EXTENSION[0];
            }
            CERT_EXTENSION[] ces = (CERT_EXTENSION[]) Structure
                .newInstance(CERT_EXTENSION.class, rgExtension)
                .toArray(cExtension);
            ces[0].read();
            return ces;
        }
    }

    /**
     * The CERT_INFO structure contains the information of a certificate.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377200(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwVersion", "SerialNumber", "SignatureAlgorithm", "Issuer",
        "NotBefore", "NotAfter", "Subject", "SubjectPublicKeyInfo",
        "IssuerUniqueId", "SubjectUniqueId", "cExtension", "rgExtension"})
    public static class CERT_INFO extends Structure {
        public static class ByReference extends CERT_INFO implements Structure.ByReference {
        }

        public int dwVersion;
        public DATA_BLOB SerialNumber;
        public CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
        public DATA_BLOB Issuer;
        public FILETIME NotBefore;
        public FILETIME NotAfter;
        public DATA_BLOB Subject;
        public CERT_PUBLIC_KEY_INFO SubjectPublicKeyInfo;
        public CRYPT_BIT_BLOB IssuerUniqueId;
        public CRYPT_BIT_BLOB SubjectUniqueId;
        public int cExtension;
        public Pointer rgExtension;

        public CERT_EXTENSION[] getRgExtension() {
            if(cExtension == 0) {
                return new CERT_EXTENSION[0];
            }
            CERT_EXTENSION[] ces = (CERT_EXTENSION[]) Structure
                .newInstance(CERT_EXTENSION.class, rgExtension)
                .toArray(cExtension);
            ces[0].read();
            return ces;
        }
    }

    /**
     * The CERT_PUBLIC_KEY_INFO structure contains a public key and its
     * algorithm.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377463(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"Algorithm", "PublicKey"})
    public static class CERT_PUBLIC_KEY_INFO extends Structure {
        public static class ByReference extends CERT_PUBLIC_KEY_INFO implements Structure.ByReference {
        }

        public CRYPT_ALGORITHM_IDENTIFIER Algorithm;
        public CRYPT_BIT_BLOB PublicKey;
    }

    /**
     * The CRL_CONTEXT structure contains both the encoded and decoded
     * representations of a certificate revocation list (CRL). CRL contexts
     * returned by any CryptoAPI function must be freed by calling the
     * CertFreeCRLContext function.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa379873(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwCertEncodingType", "pbCrlEncoded", "cbCrlEncoded",
                "pCrlInfo", "hCertStore"})
    public static class CRL_CONTEXT extends Structure {
        public static class ByReference extends CRL_CONTEXT implements Structure.ByReference {
        }

        public int dwCertEncodingType;
        public Pointer pbCrlEncoded;
        public int cbCrlEncoded;
        public CRL_INFO.ByReference pCrlInfo;
        public HCERTSTORE hCertStore;
    }

    /**
     * The CRL_ENTRY structure contains information about a single revoked
     * certificate. It is a member of a CRL_INFO structure.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa379878(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"SerialNumber", "RevocationDate", "cExtension", "rgExtension"})
    public static class CRL_ENTRY extends Structure {
        public static class ByReference extends CRL_ENTRY implements Structure.ByReference {
        }

        public DATA_BLOB SerialNumber;
        public FILETIME RevocationDate;
        public int cExtension;
        public Pointer rgExtension;

        public CERT_EXTENSION[] getRgExtension() {
            if(cExtension == 0) {
                return new CERT_EXTENSION[0];
            } else {
                CERT_EXTENSION[] result = (CERT_EXTENSION[]) Structure
                        .newInstance(CERT_EXTENSION.class, rgExtension)
                        .toArray(cExtension);
                result[0].read();
                return result;
            }
        }
    }

    /**
     * The CRL_INFO structure contains the information of a certificate
     * revocation list (CRL).
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa379880(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwVersion", "SignatureAlgorithm", "Issuer", "ThisUpdate",
        "NextUpdate", "cCRLEntry", "rgCRLEntry", "cExtension", "rgExtension"})
    public static class CRL_INFO extends Structure {
        public static class ByReference extends CRL_INFO implements Structure.ByReference {
        }

        public int dwVersion;
        public CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
        public DATA_BLOB Issuer;
        public FILETIME ThisUpdate;
        public FILETIME NextUpdate;
        public int cCRLEntry;
        public Pointer rgCRLEntry;
        public int cExtension;
        public Pointer rgExtension;

        public CRL_ENTRY[] getRgCRLEntry() {
            if (cCRLEntry == 0) {
                return new CRL_ENTRY[0];
            } else {
                CRL_ENTRY[] result = (CRL_ENTRY[]) Structure
                        .newInstance(CRL_ENTRY.class, rgCRLEntry)
                        .toArray(cCRLEntry);
                result[0].read();
                return result;
            }
        }

        public CERT_EXTENSION[] getRgExtension() {
            if (cExtension == 0) {
                return new CERT_EXTENSION[0];
            } else {
                CERT_EXTENSION[] result = (CERT_EXTENSION[]) Structure
                        .newInstance(CERT_EXTENSION.class, rgExtension)
                        .toArray(cExtension);
                result[0].read();
                return result;
            }
        }
    }

    /**
     * The CRYPT_ALGORITHM_IDENTIFIER structure specifies an algorithm used to
     * encrypt a private key. The structure includes the object identifier (OID)
     * of the algorithm and any needed parameters for that algorithm. The
     * parameters contained in its CRYPT_OBJID_BLOB are encoded.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381133(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"pszObjId", "Parameters"})
    public static class CRYPT_ALGORITHM_IDENTIFIER extends Structure {
        public static class ByReference extends CRYPT_ALGORITHM_IDENTIFIER implements Structure.ByReference {
        }

        public String pszObjId;
        public DATA_BLOB Parameters;

        public CRYPT_ALGORITHM_IDENTIFIER() {
            super(W32APITypeMapper.ASCII);
        }
    }

    /**
     * The CRYPT_ATTRIBUTE structure specifies an attribute that has one or more
     * values.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381139(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"pszObjId", "cValue", "rgValue"})
    public static class CRYPT_ATTRIBUTE extends Structure {
        public static class ByReference extends CRYPT_ATTRIBUTE implements Structure.ByReference {
        }

        public String pszObjId;
        public int cValue;
        public DATA_BLOB.ByReference rgValue;

        public DATA_BLOB[] getRgValue() {
            return (DATA_BLOB[]) rgValue.toArray(cValue);
        }

        public CRYPT_ATTRIBUTE() {
            super(W32APITypeMapper.ASCII);
        }
    }

    /**
     * The CRYPT_BIT_BLOB structure contains a set of bits represented by an
     * array of bytes.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381165(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbData", "pbData", "cUnusedBits"})
    public static class CRYPT_BIT_BLOB extends Structure {
        public static class ByReference extends CRYPT_BIT_BLOB implements Structure.ByReference {
        }

        public int cbData;
        public Pointer pbData;
        public int cUnusedBits;
    }

    /**
     * The CRYPT_KEY_PROV_INFO structure contains information about a key
     * container within a cryptographic service provider (CSP).
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381420(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"pwszContainerName", "pwszProvName", "dwProvType", "dwFlags",
                "cProvParam", "rgProvParam", "dwKeySpec"})
    public static class CRYPT_KEY_PROV_INFO extends Structure {
        public static class ByReference extends CRYPT_KEY_PROV_INFO implements Structure.ByReference {
        }

        public String pwszContainerName;
        public String pwszProvName;
        public int dwProvType;
        public int dwFlags;
        public int cProvParam;
        public Pointer rgProvParam;
        public int dwKeySpec;

        public CRYPT_KEY_PROV_INFO() {
            super(W32APITypeMapper.UNICODE);
        }

        public CRYPT_KEY_PROV_PARAM[] getRgProvParam() {
            CRYPT_KEY_PROV_PARAM[] elements = new CRYPT_KEY_PROV_PARAM[cProvParam];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = Structure.newInstance(
                        CRYPT_KEY_PROV_PARAM.class,
                        rgProvParam.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }
    }

    /**
     * The CRYPT_KEY_PROV_PARAM structure contains information about a key
     * container parameter. This structure is used with the CRYPT_KEY_PROV_INFO
     * structure.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381423(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"dwParam", "pbData", "cbData", "dwFlags"})
    public static class CRYPT_KEY_PROV_PARAM extends Structure {
        public static class ByReference extends CRYPT_KEY_PROV_PARAM implements Structure.ByReference {
        }

        public int dwParam;
        public Pointer pbData;
        public int cbData;
        public int dwFlags;
    }

    /**
     * The CRYPT_SIGN_MESSAGE_PARA structure contains information for signing
     * messages using a specified signing certificate context.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381468(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "dwMsgEncodingType", "pSigningCert", "HashAlgorithm",
                "pvHashAuxInfo", "cMsgCert", "rgpMsgCert", "cMsgCrl",
                "rgpMsgCrl", "cAuthAttr", "rgAuthAttr", "cUnauthAttr",
                "rgUnauthAttr", "dwFlags", "dwInnerContentType",
                "HashEncryptionAlgorithm", "pvHashEncryptionAuxInfo"})
    public static class CRYPT_SIGN_MESSAGE_PARA extends Structure {
        public static class ByReference extends CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {
        }

        public int cbSize;
        public int dwMsgEncodingType;
        public CERT_CONTEXT.ByReference pSigningCert;
        public CRYPT_ALGORITHM_IDENTIFIER HashAlgorithm;
        public Pointer pvHashAuxInfo;
        public int cMsgCert;
        public Pointer rgpMsgCert = null;
        public int cMsgCrl;
        public Pointer rgpMsgCrl = null;
        public int cAuthAttr;
        public Pointer rgAuthAttr = null;
        public int cUnauthAttr;
        public Pointer rgUnauthAttr = null;
        public int dwFlags;
        public int dwInnerContentType;
        public CRYPT_ALGORITHM_IDENTIFIER HashEncryptionAlgorithm;
        public Pointer pvHashEncryptionAuxInfo;

        public CERT_CONTEXT[] getRgpMsgCert() {
            CERT_CONTEXT[] elements = new CERT_CONTEXT[cMsgCrl];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = Structure.newInstance(
                        CERT_CONTEXT.class,
                        rgpMsgCert.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }

        public CRL_CONTEXT[] getRgpMsgCrl() {
            CRL_CONTEXT[] elements = new CRL_CONTEXT[cMsgCrl];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = Structure.newInstance(
                        CRL_CONTEXT.class,
                        rgpMsgCrl.getPointer(i * Native.POINTER_SIZE));
                elements[i].read();
            }
            return elements;
        }

        public CRYPT_ATTRIBUTE[] getRgAuthAttr() {
            if (cAuthAttr == 0) {
                return new CRYPT_ATTRIBUTE[0];
            } else {
                return (CRYPT_ATTRIBUTE[]) Structure.newInstance(
                        CRYPT_ATTRIBUTE.class,
                        rgAuthAttr)
                        .toArray(cAuthAttr);
            }
        }

        public CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
            if (cUnauthAttr == 0) {
                return new CRYPT_ATTRIBUTE[0];
            } else {
                return (CRYPT_ATTRIBUTE[]) Structure.newInstance(
                        CRYPT_ATTRIBUTE.class,
                        rgUnauthAttr)
                        .toArray(cUnauthAttr);
            }
        }
    }

    /**
     * The CryptGetSignerCertificateCallback user supplied callback function is
     * used with the CRYPT_VERIFY_MESSAGE_PARA structure to get and verify a
     * message signer's certificate.
     */
    public interface CryptGetSignerCertificateCallback extends StdCallLibrary.StdCallCallback {
        /**
         *
         * @param pvGetArg
         * A pointer to user-defined data passed on to the
         * verification function as specified in the CRYPT_VERIFY_MESSAGE_PARA
         * structure.
         * @param dwCertEncodingType
         * Specifies the type of encoding used. It is always acceptable to
         * specify both the certificate and message encoding types by combining
         * them with a bitwise-OR operation as shown in the following example:
         *
         * <p><code>X509_ASN_ENCODING | PKCS_7_ASN_ENCODING</code></p>
         *
         * <p>Currently defined encoding types are:</p>
         *
         * <ul>
         * <li>X509_ASN_ENCODING</li>
         * <li>PKCS_7_ASN_ENCODING</li>
         * </ul>
         * @param pSignerId A pointer to a CERT_INFO structure containing the
         * issuer and serial number. Can be NULL if there is no content or
         * signer.
         * @param hMsgCertStore A handle to the certificate store containing all
         * the certificates and CRLs in the signed message.
         * @return Pointer to a read-only {@link com.sun.jna.platform.win32.WinCrypt.CERT_CONTEXT}
         * if a signer certificate is found, {@code null} if the function fails.
         */
        public CERT_CONTEXT.ByReference callback(Pointer pvGetArg, int dwCertEncodingType,
                              CERT_INFO pSignerId,
                              HCERTSTORE hMsgCertStore);
    }

    /**
     * The CRYPT_VERIFY_MESSAGE_PARA structure contains information needed to
     * verify signed messages.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381477(v=vs.85).aspx">MSDN</a>
     */
    @FieldOrder({"cbSize", "dwMsgAndCertEncodingType", "hCryptProv",
                "pfnGetSignerCertificate", "pvGetArg", "pStrongSignPara"})
    public static class CRYPT_VERIFY_MESSAGE_PARA extends Structure {
        public static class ByReference extends CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {
        }

        public int cbSize;
        public int dwMsgAndCertEncodingType;
        public HCRYPTPROV_LEGACY hCryptProv;
        public CryptGetSignerCertificateCallback pfnGetSignerCertificate;
        public Pointer pvGetArg;
        public CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;

        @Override
        public void write() {
            cbSize = size();
            super.write();
        }
    }

    /**
     * Handle to a certificate chain engine.
     */
    public static class HCERTCHAINENGINE extends HANDLE {

        /**
         * Instantiates a new hcertchainengine.
         */
        public HCERTCHAINENGINE() {

        }

        /**
         * Instantiates a new hcertchainengine.
         *
         * @param p the p
         */
        public HCERTCHAINENGINE(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a certificate store.
     */
    public static class HCERTSTORE extends HANDLE {

        /**
         * Instantiates a new hcertstore.
         */
        public HCERTSTORE() {

        }

        /**
         * Instantiates a new hcertstore.
         *
         * @param p
         *            the p
         */
        public HCERTSTORE(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a cryptographic message.
     */
    public static class HCRYPTMSG extends HANDLE {

        /**
         * Instantiates a new hcryptmgr.
         */
        public HCRYPTMSG() {

        }

        /**
         * Instantiates a new hcryptmsg.
         *
         * @param p
         *            the p
         */
        public HCRYPTMSG(Pointer p) {
            super(p);
        }
    }

    public static class HCRYPTPROV_LEGACY extends BaseTSD.ULONG_PTR {

        public HCRYPTPROV_LEGACY() {
        }

        public HCRYPTPROV_LEGACY(long value) {
            super(value);
        }
    }

    /**
     * The CRYPTPROTECT_PROMPTSTRUCT structure provides the text of a prompt and
     * information about when and where that prompt is to be displayed when using
     * the CryptProtectData and CryptUnprotectData functions.
     */
    @FieldOrder({"cbSize", "dwPromptFlags", "hwndApp", "szPrompt"})
    public static class CRYPTPROTECT_PROMPTSTRUCT extends Structure {
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

        public CRYPTPROTECT_PROMPTSTRUCT() {
            super(W32APITypeMapper.DEFAULT);
        }

        public CRYPTPROTECT_PROMPTSTRUCT(Pointer memory) {
            super(memory, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }
    }

    //
    // CryptProtect PromptStruct dwPromtFlags
    //

    /**
     * Prompt on unprotect.
     */
    int CRYPTPROTECT_PROMPT_ON_UNPROTECT = 0x1; // 1<<0
    /**
     * Prompt on protect.
     */
    int CRYPTPROTECT_PROMPT_ON_PROTECT = 0x2; // 1<<1
    /**
     * Reserved, don't use.
     */
    int CRYPTPROTECT_PROMPT_RESERVED = 0x04;
    /**
     * Default to strong variant UI protection (user supplied password currently).
     */
    int CRYPTPROTECT_PROMPT_STRONG = 0x08; // 1<<3
    /**
     * Require strong variant UI protection (user supplied password currently).
     */
    int CRYPTPROTECT_PROMPT_REQUIRE_STRONG = 0x10; // 1<<4

    //
    // CryptProtectData and CryptUnprotectData dwFlags
    //
    /**
     * For remote-access situations where ui is not an option, if UI was specified
     * on protect or unprotect operation, the call will fail and GetLastError() will
     * indicate ERROR_PASSWORD_RESTRICTION.
     */
    int CRYPTPROTECT_UI_FORBIDDEN = 0x1;
    /**
     * Per machine protected data -- any user on machine where CryptProtectData
     * took place may CryptUnprotectData.
     */
    int CRYPTPROTECT_LOCAL_MACHINE = 0x4;
    /**
     * Force credential synchronize during CryptProtectData()
     * Synchronize is only operation that occurs during this operation.
     */
    int CRYPTPROTECT_CRED_SYNC = 0x8;
    /**
     * Generate an Audit on protect and unprotect operations.
     */
    int CRYPTPROTECT_AUDIT = 0x10;
    /**
     * Protect data with a non-recoverable key.
     */
    int CRYPTPROTECT_NO_RECOVERY = 0x20;
    /**
     * Verify the protection of a protected blob.
     */
    int CRYPTPROTECT_VERIFY_PROTECTION = 0x40;
    /**
     * Regenerate the local machine protection.
     */
    int CRYPTPROTECT_CRED_REGENERATE = 0x80;

    /**
     * ASN.1 Certificate encode/decode return value base
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_ERROR = 0x80093100;

    /**
     * ASN.1 internal encode or decode error
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_INTERNAL = 0x80093101;

    /**
     * ASN.1 unexpected end of data
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_EOD = 0x80093102;

    /**
     * ASN.1 corrupted data
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_CORRUPT = 0x80093103;

    /**
     * ASN.1 value too large
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_LARGE = 0x80093104;

    /**
     * ASN.1 constraint violated
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_CONSTRAINT = 0x80093105;

    /**
     * ASN.1 out of memory
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_MEMORY = 0x80093106;

    /**
     * ASN.1 buffer overflow
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_OVERFLOW = 0x80093107;

    /**
     * ASN.1 function not supported for this PDU
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_BADPDU = 0x80093108;

    /**
     * ASN.1 bad arguments to function call
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_BADARGS = 0x80093109;

    /**
     * ASN.1 bad real value
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_BADREAL = 0x8009310A;

    /**
     * ASN.1 bad tag value met
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_BADTAG = 0x8009310B;

    /**
     * ASN.1 bad choice value
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_CHOICE = 0x8009310C;

    /**
     * ASN.1 bad encoding rule
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_RULE = 0x8009310D;

    /**
     * ASN.1 bad Unicode (UTF8)
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_UTF8 = 0x8009310E;

    /**
     * ASN.1 bad PDU type
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_PDU_TYPE = 0x80093133;

    /**
     * ASN.1 not yet implemented
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_NYI = 0x80093134;

    /**
     * ASN.1 skipped unknown extensions
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_EXTENDED = 0x80093201;

    /**
     * ASN.1 end of data expected
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa375564(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_E_ASN1_NOEOD = 0x80093202;

    /**
     * Message Encoding Type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376511(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_ASN_ENCODING = 0x00000001;

    /**
     * Message Encoding Type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376511(v=vs.85).aspx">MSDN</a>
     */
    int CRYPT_NDR_ENCODING = 0x00000002;

    /**
     * Message Encoding Type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376511(v=vs.85).aspx">MSDN</a>
     */
    int X509_ASN_ENCODING = 0x00000001;

    /**
     * Message Encoding Type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376511(v=vs.85).aspx">MSDN</a>
     */
    int X509_NDR_ENCODING = 0x00000002;

    /**
     * Message Encoding Type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376511(v=vs.85).aspx">MSDN</a>
     */
    int PKCS_7_ASN_ENCODING = 0x00010000;

    /**
     * Message Encoding Type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376511(v=vs.85).aspx">MSDN</a>
     */
    int PKCS_7_NDR_ENCODING = 0x00020000;

    /**
     * Determines the kind of issuer matching to be done.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377593(v=vs.85).aspx">MSDN</a>
     */
    int USAGE_MATCH_TYPE_AND = 0x00000000;

    /**
     * Determines the kind of issuer matching to be done.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377593(v=vs.85).aspx">MSDN</a>
     */
    int USAGE_MATCH_TYPE_OR = 0x00000001;

    /**
     * Set the window handle that the provider uses as the parent of any dialog
     * boxes it creates.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa380276(v=vs.85).aspx">MSDN</a>
     */
    int PP_CLIENT_HWND = 1;

    /**
     * Certificate name string type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376556(v=vs.85).aspx">MSDN</a>
     */
    int CERT_SIMPLE_NAME_STR = 1;

    /**
     * Certificate name string type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376556(v=vs.85).aspx">MSDN</a>
     */
    int CERT_OID_NAME_STR = 2;

    /**
     * Certificate name string type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376556(v=vs.85).aspx">MSDN</a>
     */
    int CERT_X500_NAME_STR = 3;

    /**
     * Certificate name string type.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376556(v=vs.85).aspx">MSDN</a>
     */
    int CERT_XML_NAME_STR = 4;

    /**
     * Predefined verify chain policies.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377163(v=vs.85).aspx">MSDN</a>
     */
    int CERT_CHAIN_POLICY_BASE = 1;

    /**
     * Algorithm object identifiers RSA.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381133(v=vs.85).aspx">MSDN</a>
     */
    String szOID_RSA_SHA1RSA = "1.2.840.113549.1.1.5";

    /**
     * Predefined certificate chain engine values.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376078(v=vs.85).aspx">MSDN</a>
     */
    HCERTCHAINENGINE HCCE_CURRENT_USER = new HCERTCHAINENGINE(Pointer.createConstant(0x0));

    /**
     * Predefined certificate chain engine values.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376078(v=vs.85).aspx">MSDN</a>
     */
    HCERTCHAINENGINE HCCE_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(0x1));

    /**
     * Predefined certificate chain engine values.
     *
     * @see
     * <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376078(v=vs.85).aspx">MSDN</a>
     */
    HCERTCHAINENGINE HCCE_SERIAL_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(0x2));

    /**
     * Certificate comparison functions.
     *
     * @see <a href=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa376064(v=vs.85).aspx">MSDN</a>
     */
    int CERT_COMPARE_SHIFT = 16;

    /**
     * Certificate comparison functions.
     *
     * @see <a href=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa376064(v=vs.85).aspx">MSDN</a>
     */
    int CERT_COMPARE_NAME_STR_W = 8;

    /**
     * Certificate comparison functions.
     *
     * @see <a href=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa376064(v=vs.85).aspx">MSDN</a>
     */
    int CERT_INFO_SUBJECT_FLAG = 7;

    /**
     * Certificate comparison functions.
     *
     * @see <a href=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa376064(v=vs.85).aspx">MSDN</a>
     */
    int CERT_FIND_SUBJECT_STR_W = (CERT_COMPARE_NAME_STR_W << CERT_COMPARE_SHIFT | CERT_INFO_SUBJECT_FLAG);

    /**
     * Certificate comparison functions.
     *
     * @see <a href=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa376064(v=vs.85).aspx">MSDN</a>
     */
    int CERT_FIND_SUBJECT_STR = CERT_FIND_SUBJECT_STR_W;

    /**
     * Imported keys are marked as exportable. If this flag is not used, calls
     * to the CryptExportKey function with the key handle fail.
     */
    int CRYPT_EXPORTABLE = 0x00000001;

    /**
     * The user is to be notified through a dialog box or other method when
     * certain attempts to use this key are made. The precise behavior is
     * specified by the cryptographic service provider (CSP) being used.
     *
     * <p>
     * Prior to Internet Explorer 4.0, Microsoft cryptographic service providers
     * ignored this flag. Starting with Internet Explorer 4.0, Microsoft
     * providers support this flag.</p>
     *
     * <p>
     * If the provider context was opened with the CRYPT_SILENT flag set, using
     * this flag causes a failure and the last error is set to
     * NTE_SILENT_CONTEXT.</p>
     */
    int CRYPT_USER_PROTECTED = 0x00000002;

    /**
     * The private keys are stored under the local computer and not under the
     * current user.
     */
    int CRYPT_MACHINE_KEYSET = 0x00000020;

    /**
     * The private keys are stored under the current user and not under the
     * local computer even if the PFX BLOB specifies that they should go into
     * the local computer.
     */
    int CRYPT_USER_KEYSET = 0x00001000;

    /**
     * Indicates that the CNG key storage provider (KSP) is preferred. If the
     * CSP is specified in the PFX file, then the CSP is used, otherwise the KSP
     * is preferred. If the CNG KSP is unavailable, the PFXImportCertStore
     * function will fail.
     */
    int PKCS12_PREFER_CNG_KSP = 0x00000100;

    /**
     * Indicates that the CNG KSP is always used. When specified,
     * PFXImportCertStore attempts to use the CNG KSP irrespective of provider
     * information in the PFX file. If the CNG KSP is unavailable, the import
     * will not fail.
     */
    int PKCS12_ALWAYS_CNG_KSP = 0x00000200;

    /**
     * Allow overwrite of the existing key. Specify this flag when you encounter
     * a scenario in which you must import a PFX file that contains a key name
     * that already exists. For example, when you import a PFX file, it is
     * possible that a container of the same name is already present because
     * there is no unique namespace for key containers. If you have created a
     * "TestKey" on your computer, and then you import a PFX file that also has
     * "TestKey" as the key container, the PKCS12_ALLOW_OVERWRITE_KEY setting
     * allows the key to be overwritten.
     */
    int PKCS12_ALLOW_OVERWRITE_KEY = 0x00004000;

    /**
     * Do not persist the key. Specify this flag when you do not want to persist
     * the key. For example, if it is not necessary to store the key after
     * verification, then instead of creating a container and then deleting it,
     * you can specify this flag to dispose of the key immediately.
     */
    int PKCS12_NO_PERSIST_KEY = 0x00008000;

    /**
     * Import all extended properties on the certificate that were saved on the
     * certificate when it was exported.
     */
    int PKCS12_INCLUDE_EXTENDED_PROPERTIES = 0x0010;

    /**
     * Checks for nonfreed certificate, CRL, and CTL contexts. A returned error
     * code indicates that one or more store elements is still in use. This flag
     * should only be used as a diagnostic tool in the development of
     * applications.
     */
    int CERT_CLOSE_STORE_FORCE_FLAG = 0x00000001;
    /**
     * Forces the freeing of memory for all contexts associated with the store.
     * This flag can be safely used only when the store is opened in a function
     * and neither the store handle nor any of its contexts are passed to any
     * called functions. For details, see Remarks.
     */
    int CERT_CLOSE_STORE_CHECK_FLAG = 0x00000002;
    /** encoded single certificate */
    int CERT_QUERY_CONTENT_CERT = 1;
    /** encoded single CTL */
    int CERT_QUERY_CONTENT_CTL = 2;
    /** encoded single CRL */
    int CERT_QUERY_CONTENT_CRL = 3;
    /** serialized store */
    int CERT_QUERY_CONTENT_SERIALIZED_STORE = 4;
    /** serialized single certificate */
    int CERT_QUERY_CONTENT_SERIALIZED_CERT = 5;
    /** serialized single CTL */
    int CERT_QUERY_CONTENT_SERIALIZED_CTL = 6;
    /** serialized single CRL */
    int CERT_QUERY_CONTENT_SERIALIZED_CRL = 7;
    /** a PKCS#7 signed message */
    int CERT_QUERY_CONTENT_PKCS7_SIGNED = 8;
    /** a PKCS#7 message, such as enveloped message. But it is not a signed message,  */
    int CERT_QUERY_CONTENT_PKCS7_UNSIGNED = 9;
    /** a PKCS7 signed message embedded in a file */
    int CERT_QUERY_CONTENT_PKCS7_SIGNED_EMBED = 10;
    /** an encoded PKCS#10 */
    int CERT_QUERY_CONTENT_PKCS10 = 11;
    /** an encoded PFX BLOB */
    int CERT_QUERY_CONTENT_PFX = 12;
    /** an encoded CertificatePair (contains forward and/or reverse cross certs) */
    int CERT_QUERY_CONTENT_CERT_PAIR = 13;
    /** an encoded PFX BLOB, which was loaded to phCertStore */
    int CERT_QUERY_CONTENT_PFX_AND_LOAD = 14;

    /** encoded single certificate */
    int CERT_QUERY_CONTENT_FLAG_CERT = (1 << CERT_QUERY_CONTENT_CERT);

    /** encoded single CTL */
    int CERT_QUERY_CONTENT_FLAG_CTL = (1 << CERT_QUERY_CONTENT_CTL);

    /** encoded single CRL */
    int CERT_QUERY_CONTENT_FLAG_CRL = (1 << CERT_QUERY_CONTENT_CRL);

    /** serialized store */
    int CERT_QUERY_CONTENT_FLAG_SERIALIZED_STORE = (1 << CERT_QUERY_CONTENT_SERIALIZED_STORE);

    /** serialized single certificate */
    int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CERT = (1 << CERT_QUERY_CONTENT_SERIALIZED_CERT);

    /** serialized single CTL */
    int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CTL = (1 << CERT_QUERY_CONTENT_SERIALIZED_CTL);

    /** serialized single CRL */
    int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CRL = (1 << CERT_QUERY_CONTENT_SERIALIZED_CRL);

    /** an encoded PKCS#7 signed message */
    int CERT_QUERY_CONTENT_FLAG_PKCS7_SIGNED = (1 << CERT_QUERY_CONTENT_PKCS7_SIGNED);

    /** an encoded PKCS#7 message.  But it is not a signed message */
    int CERT_QUERY_CONTENT_FLAG_PKCS7_UNSIGNED = (1 << CERT_QUERY_CONTENT_PKCS7_UNSIGNED);

    /** the content includes an embedded PKCS7 signed message */
    int CERT_QUERY_CONTENT_FLAG_PKCS7_SIGNED_EMBED = (1 << CERT_QUERY_CONTENT_PKCS7_SIGNED_EMBED);

    /** an encoded PKCS#10 */
    int CERT_QUERY_CONTENT_FLAG_PKCS10 = (1 << CERT_QUERY_CONTENT_PKCS10);

    /** an encoded PFX BLOB */
    int CERT_QUERY_CONTENT_FLAG_PFX = (1 << CERT_QUERY_CONTENT_PFX);

    /** an encoded CertificatePair (contains forward and/or reverse cross certs) */
    int CERT_QUERY_CONTENT_FLAG_CERT_PAIR = (1 << CERT_QUERY_CONTENT_CERT_PAIR);

    /** an encoded PFX BLOB, and we do want to load it (not included in {@link #CERT_QUERY_CONTENT_FLAG_ALL} */
    int CERT_QUERY_CONTENT_FLAG_PFX_AND_LOAD = (1 << CERT_QUERY_CONTENT_PFX_AND_LOAD);

    /** content can be any type */
    int CERT_QUERY_CONTENT_FLAG_ALL = CERT_QUERY_CONTENT_FLAG_CERT
        | CERT_QUERY_CONTENT_FLAG_CTL
        | CERT_QUERY_CONTENT_FLAG_CRL
        | CERT_QUERY_CONTENT_FLAG_SERIALIZED_STORE
        | CERT_QUERY_CONTENT_FLAG_SERIALIZED_CERT
        | CERT_QUERY_CONTENT_FLAG_SERIALIZED_CTL
        | CERT_QUERY_CONTENT_FLAG_SERIALIZED_CRL
        | CERT_QUERY_CONTENT_FLAG_PKCS7_SIGNED
        | CERT_QUERY_CONTENT_FLAG_PKCS7_UNSIGNED
        | CERT_QUERY_CONTENT_FLAG_PKCS7_SIGNED_EMBED
        | CERT_QUERY_CONTENT_FLAG_PKCS10
        | CERT_QUERY_CONTENT_FLAG_PFX
        | CERT_QUERY_CONTENT_FLAG_CERT_PAIR;

    /** the content is in binary format */
    int CERT_QUERY_FORMAT_BINARY = 1;

    /** the content is base64 encoded */
    int CERT_QUERY_FORMAT_BASE64_ENCODED = 2;

    /** the content is ascii hex encoded with "{ASN}" prefix */
    int CERT_QUERY_FORMAT_ASN_ASCII_HEX_ENCODED = 3;

    /** the content is in binary format */
    int CERT_QUERY_FORMAT_FLAG_BINARY = ( 1 << CERT_QUERY_FORMAT_BINARY);

    /** the content is base64 encoded */
    int CERT_QUERY_FORMAT_FLAG_BASE64_ENCODED = ( 1 << CERT_QUERY_FORMAT_BASE64_ENCODED);

    /** the content is ascii hex encoded with "{ASN}" prefix */
    int CERT_QUERY_FORMAT_FLAG_ASN_ASCII_HEX_ENCODED  = ( 1 << CERT_QUERY_FORMAT_ASN_ASCII_HEX_ENCODED);

    /** the content can be of any format */
    int CERT_QUERY_FORMAT_FLAG_ALL = CERT_QUERY_FORMAT_FLAG_BINARY
        | CERT_QUERY_FORMAT_FLAG_BASE64_ENCODED
        | CERT_QUERY_FORMAT_FLAG_ASN_ASCII_HEX_ENCODED;

    /**
     * The object is stored in a file.
     */
    int CERT_QUERY_OBJECT_FILE = 0x00000001;
    /**
     * The object is stored in a structure in memory.
     */
    int CERT_QUERY_OBJECT_BLOB = 0x00000002;
}
