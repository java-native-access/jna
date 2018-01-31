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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.W32APITypeMapper;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.Union;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.PointerByReference;

/**
 * Ported from WinCrypt.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface WinCrypt {

    /**
     * The CryptoAPI CRYPTOAPI_BLOB structure is used for an arbitrary array of bytes.
     */
    public static class DATA_BLOB extends Structure {
		public static class ByReference extends DATA_BLOB implements Structure.ByReference {}
		
        public static final List<String> FIELDS = createFieldsOrder("cbData", "pbData");
		
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


        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
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
	 * certificate in a certificate chain, summary trust information about a simple
	 * chain of certificates, or summary information about an array of simple
	 * chains.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377590(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_TRUST_STATUS extends Structure {
		public static class ByReference extends CERT_TRUST_STATUS implements Structure.ByReference {}

		public int dwErrorStatus;
		public int dwInfoStatus;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwErrorStatus", "dwInfoStatus");
		}
	}

	/**
	 * The CTL_ENTRY structure is an element of a certificate trust list (CTL).
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381487(v=vs.85).aspx">MSDN</a>
	 */
	public static class CTL_ENTRY extends Structure {
		public static class ByReference extends CTL_ENTRY implements Structure.ByReference {}

		public DATA_BLOB SubjectIdentifier;
		public int cAttribute;
		public CRYPT_ATTRIBUTE.ByReference rgAttribute;

		public CRYPT_ATTRIBUTE[] getRgAttribute() {
			return (CRYPT_ATTRIBUTE[]) rgAttribute.toArray(cAttribute);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("SubjectIdentifier", "cAttribute", "rgAttribute");
		}
	}

	/**
	 * Contains information updated by a certificate revocation list (CRL)
	 * revocation type handler. The CERT_REVOCATION_CRL_INFO structure is used with
	 * both base and delta CRLs.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377509(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_REVOCATION_CRL_INFO extends Structure {
		public static class ByReference extends CERT_REVOCATION_CRL_INFO implements Structure.ByReference {}

		public int cbSize;
		public CRL_CONTEXT.ByReference pBaseCRLContext;
		public CRL_CONTEXT.ByReference pDeltaCRLContext;
		public CRL_ENTRY.ByReference pCrlEntry;
		public boolean fDeltaCrlEntry;
		
		public CERT_REVOCATION_CRL_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "pBaseCRLContext", "pDeltaCRLContext", "pCrlEntry", "fDeltaCrlEntry");
		}
	}

	/**
	 * The CERT_REVOCATION_INFO structure indicates the revocation status of a
	 * certificate in a CERT_CHAIN_ELEMENT.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377519(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_REVOCATION_INFO extends Structure {
		public static class ByReference extends CERT_REVOCATION_INFO implements Structure.ByReference {}

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

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "dwRevocationResult", "pszRevocationOid", "pvOidSpecificInfo",
					"fHasFreshnessTime", "dwFreshnessTime", "pCrlInfo");
		}
	}

	/**
	 * The CERT_CHAIN_ELEMENT structure is a single element in a simple certificate
	 * chain. Each element has a pointer to a certificate context, a pointer to a
	 * structure that indicates the error status and information status of the
	 * certificate, and a pointer to a structure that indicates the revocation
	 * status of the certificate.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377183(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_CHAIN_ELEMENT extends Structure {
		public static class ByReference extends CERT_CHAIN_ELEMENT implements Structure.ByReference {}

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

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "pCertContext", "TrustStatus", "pRevocationInfo", "pIssuanceUsage",
					"pApplicationUsage", "pwszExtendedErrorInfo");
		}
	}

	/**
	 * The CTL_INFO structure contains the information stored in a Certificate Trust
	 * List (CTL).
	 * 
	 * @see <a href="https://msdn.microsoft.com/es-xl/library/windows/desktop/aa381491(v=vs.85).aspx">MSDN</a>
	 */
	public static class CTL_INFO extends Structure {
		public static class ByReference extends CTL_INFO implements Structure.ByReference {}

		public int dwVersion;
		public CTL_USAGE SubjectUsage;
		public DATA_BLOB ListIdentifier;
		public DATA_BLOB SequenceNumber;
		public FILETIME ThisUpdate;
		public FILETIME NextUpdate;
		public CRYPT_ALGORITHM_IDENTIFIER SubjectAlgorithm;
		public int cCTLEntry;
		public CTL_ENTRY.ByReference rgCTLEntry;
		public int cExtension;
		public CERT_EXTENSION.ByReference rgExtension;

		public CERT_EXTENSION[] getRgExtension() {
			return (CERT_EXTENSION[]) rgExtension.toArray(cExtension);
		}

		public CTL_ENTRY[] getRgCTLEntry() {
			return (CTL_ENTRY[]) rgCTLEntry.toArray(cCTLEntry);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwVersion", "SubjectUsage", "ListIdentifier", "SequenceNumber", "ThisUpdate",
					"NextUpdate", "SubjectAlgorithm", "cCTLEntry", "rgCTLEntry", "cExtension", "rgExtension");
		}
	}

	/**
	 * The CTL_CONTEXT structure contains both the encoded and decoded
	 * representations of a CTL. It also contains an opened HCRYPTMSG handle to the
	 * decoded, cryptographically signed message containing the CTL_INFO as its
	 * inner content.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381486(v=vs.85).aspx">MSDN</a>
	 */
	public static class CTL_CONTEXT extends Structure {
		public static class ByReference extends CTL_CONTEXT implements Structure.ByReference {}

		public int dwMsgAndCertEncodingType;
		public Pointer pbCtlEncoded;
		public int cbCtlEncoded;
		public CTL_INFO.ByReference pCtlInfo;
		public HCERTSTORE hCertStore;
		public HCRYPTMSG hCryptMsg;
		public Pointer pbCtlContent;
		public int cbCtlContent;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwMsgAndCertEncodingType", "pbCtlEncoded", "cbCtlEncoded", "pCtlInfo", "hCertStore",
					"hCryptMsg", "pbCtlContent", "cbCtlContent");
		}
	}

	/**
	 * The CERT_TRUST_LIST_INFO structure that indicates valid usage of a CTL.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377585(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_TRUST_LIST_INFO extends Structure {
		public static class ByReference extends CERT_TRUST_LIST_INFO implements Structure.ByReference {}

		public int cbSize;
		public CTL_ENTRY.ByReference pCtlEntry;
		public CTL_CONTEXT.ByReference pCtlContext;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "pCtlEntry", "pCtlContext");
		}
	}

	/**
	 * The CTL_USAGE structure contains an array of object identifiers (OIDs) for
	 * Certificate Trust List (CTL) extensions. CTL_USAGE structures are used in
	 * functions that search for CTLs for specific uses.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381493(v=vs.85).aspx">MSDN</a>
	 */
	public static class CTL_USAGE extends Structure {
		public static class ByReference extends CTL_USAGE implements Structure.ByReference {}

		public int cUsageIdentifier;
		public Pointer rgpszUsageIdentifier;

		public CTL_USAGE() {
			super();
		}

		public CTL_USAGE(TypeMapper typeMapper) {
			super(typeMapper);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cUsageIdentifier", "rgpszUsageIdentifier");
		}
	}

	/**
	 * The CERT_USAGE_MATCH structure provides criteria for identifying issuer
	 * certificates to be used to build a certificate chain.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377593(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_USAGE_MATCH extends Structure {
		public static class ByReference extends CERT_USAGE_MATCH implements Structure.ByReference {}

		public int dwType;
		public CTL_USAGE Usage;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwType", "Usage");
		}
	}

	/**
	 * The CERT_CHAIN_PARA structure establishes the searching and matching criteria
	 * to be used in building a certificate chain.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377186(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_CHAIN_PARA extends Structure {
		public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {}

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

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "RequestedUsage", "RequestedIssuancePolicy", "dwUrlRetrievalTimeout",
					"fCheckRevocationFreshnessTime", "dwRevocationFreshnessTime", "pftCacheResync", "pStrongSignPara",
					"dwStrongSignFlags");
		}
	}
	
	/**
	 * Contains parameters used to check for strong signatures on certificates,
	 * certificate revocation lists (CRLs), online certificate status protocol
	 * (OCSP) responses, and PKCS #7 messages.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/hh870262(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_STRONG_SIGN_PARA extends Structure {
		public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {}

		public int cbSize;
		public int dwInfoChoice;
		public DUMMYUNION DUMMYUNIONNAME;

		public class DUMMYUNION extends Union {
			Pointer pvInfo;
			CERT_STRONG_SIGN_SERIALIZED_INFO.ByReference pSerializedInfo;
			LPSTR pszOID;
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "dwInfoChoice", "DUMMYUNIONNAME");
		}
	}

	/**
	 * Contains the signature algorithm/hash algorithm and public key algorithm/bit
	 * length pairs that can be used for strong signing. This structure is used by
	 * the CERT_STRONG_SIGN_PARA structure.
	 * 
	 * @see <a href= "https://msdn.microsoft.com/en-us/library/windows/desktop/hh870263(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_STRONG_SIGN_SERIALIZED_INFO extends Structure {
		public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {}

		public int dwFlags;
		public String pwszCNGSignHashAlgids;
		public String pwszCNGPubKeyMinBitLengths;

		public CERT_STRONG_SIGN_SERIALIZED_INFO() {
			super(W32APITypeMapper.UNICODE);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwFlags", "pwszCNGSignHashAlgids", "pwszCNGPubKeyMinBitLengths");
		}
	}

	/**
	 * The CERT_CHAIN_POLICY_STATUS structure holds certificate chain status
	 * information returned by the CertVerifyCertificateChainPolicy function when
	 * the certificate chains are validated.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377188(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_CHAIN_POLICY_STATUS extends Structure {
		public static class ByReference extends CERT_CHAIN_POLICY_STATUS implements Structure.ByReference {}

		public int cbSize;
		public int dwError;
		public int lChainIndex;
		public int lElementIndex;
		public Pointer pvExtraPolicyStatus;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "dwError", "lChainIndex", "lElementIndex", "pvExtraPolicyStatus");
		}
	}

	/**
	 * The CERT_SIMPLE_CHAIN structure contains an array of chain elements and a
	 * summary trust status for the chain that the array represents.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377544(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_SIMPLE_CHAIN extends Structure {
		public static class ByReference extends CERT_SIMPLE_CHAIN implements Structure.ByReference {}

		public int cbSize;
		public CERT_TRUST_STATUS TrustStatus;
		public int cElement;
		public CERT_CHAIN_ELEMENT.ByReference rgpElement;
		public CERT_TRUST_LIST_INFO.ByReference pTrustListInfo;

		public boolean fHasRevocationFreshnessTime;
		public int dwRevocationFreshnessTime;

		public CERT_SIMPLE_CHAIN() {
			super(W32APITypeMapper.DEFAULT);
		}

		public CERT_CHAIN_ELEMENT[] getRgpElement() {
			return (CERT_CHAIN_ELEMENT[]) rgpElement.toArray(cElement);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "TrustStatus", "cElement", "rgpElement", "pTrustListInfo",
					"fHasRevocationFreshnessTime", "dwRevocationFreshnessTime");
		}
	}

	/**
	 * The CERT_CHAIN_POLICY_PARA structure contains information used in
	 * CertVerifyCertificateChainPolicy to establish policy criteria for the
	 * verification of certificate chains.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377187(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_CHAIN_POLICY_PARA extends Structure {
		public static class ByReference extends CERT_CHAIN_POLICY_PARA implements Structure.ByReference {}

		public int cbSize;
		public int dwFlags;
		public Pointer pvExtraPolicyPara;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "dwFlags", "pvExtraPolicyPara");
		}
	}

	/**
	 * The CERT_CHAIN_CONTEXT structure contains an array of simple certificate
	 * chains and a trust status structure that indicates summary validity data on
	 * all of the connected simple chains.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377182(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_CHAIN_CONTEXT extends Structure {
		public static class ByReference extends CERT_CHAIN_CONTEXT implements Structure.ByReference {}

		public int cbSize;
		public CERT_TRUST_STATUS TrustStatus;
		public int cChain;
		public CERT_SIMPLE_CHAIN.ByReference rgpChain;
		public int cLowerQualityChainContext;
		public CERT_CHAIN_CONTEXT.ByReference rgpLowerQualityChainContext;
		public boolean fHasRevocationFreshnessTime;
		public int dwRevocationFreshnessTime;
		public int dwCreateFlags;
		public GUID ChainId;

		public CERT_SIMPLE_CHAIN[] getRgpChain() {
			return (CERT_SIMPLE_CHAIN[]) rgpChain.toArray(cChain);
		}

		public CERT_CHAIN_CONTEXT[] getRgpLowerQualityChainContext() {
			return (CERT_CHAIN_CONTEXT[]) rgpLowerQualityChainContext.toArray(cLowerQualityChainContext);
		}

		public CERT_CHAIN_CONTEXT() {
			super(W32APITypeMapper.DEFAULT);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "TrustStatus", "cChain", "rgpChain", "cLowerQualityChainContext",
					"rgpLowerQualityChainContext", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime",
					"dwCreateFlags", "ChainId");
		}
	}

	/**
	 * The CERT_CONTEXT structure contains both the encoded and decoded
	 * representations of a certificate. A certificate context returned by one of
	 * the functions defined in Wincrypt.h must be freed by calling the
	 * CertFreeCertificateContext function. The CertDuplicateCertificateContext
	 * function can be called to make a duplicate copy (which also must be freed by
	 * calling CertFreeCertificateContext).
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377189(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_CONTEXT extends Structure {
		public static class ByReference extends CERT_CONTEXT implements Structure.ByReference {}

		public int dwCertEncodingType;
		public Pointer pbCertEncoded;
		public int cbCertEncoded;
		public CERT_INFO.ByReference pCertInfo;
		public HCERTSTORE hCertStore;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwCertEncodingType", "pbCertEncoded", "cbCertEncoded", "pCertInfo", "hCertStore");
		}
	}

	/**
	 * The CERT_EXTENSION structure contains the extension information for a
	 * certificate, Certificate Revocation List (CRL) or Certificate Trust List
	 * (CTL).
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377195(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_EXTENSION extends Structure {
		public static class ByReference extends CERT_EXTENSION implements Structure.ByReference {}

		public String pszObjId;
		public boolean fCritical;
		public DATA_BLOB Value;
		
		public CERT_EXTENSION() {
            super(W32APITypeMapper.ASCII);
        }

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("pszObjId", "fCritical", "Value");
		}
	}

	/**
	 * The CERT_EXTENSIONS structure contains an array of extensions.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377196(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_EXTENSIONS extends Structure {
		public static class ByReference extends CERT_EXTENSIONS implements Structure.ByReference {}

		public int cExtension;
		public CERT_EXTENSION.ByReference rgExtension;

		public CERT_EXTENSION[] getRgExtension() {
			return (CERT_EXTENSION[]) rgExtension.toArray(cExtension);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cExtension", "rgExtension");
		}
	}

	/**
	 * The CERT_INFO structure contains the information of a certificate.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377200(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_INFO extends Structure {
		public static class ByReference extends CERT_INFO implements Structure.ByReference {}

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
		public CERT_EXTENSION.ByReference rgExtension;

		public CERT_EXTENSION[] getRgExtension() {
			return (CERT_EXTENSION[]) rgExtension.toArray(cExtension);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwVersion", "SerialNumber", "SignatureAlgorithm", "Issuer", "NotBefore", "NotAfter",
					"Subject", "SubjectPublicKeyInfo", "IssuerUniqueId", "SubjectUniqueId", "cExtension",
					"rgExtension");
		}
	}

	/**
	 * The CERT_PUBLIC_KEY_INFO structure contains a public key and its algorithm.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa377463(v=vs.85).aspx">MSDN</a>
	 */
	public static class CERT_PUBLIC_KEY_INFO extends Structure {
		public static class ByReference extends CERT_PUBLIC_KEY_INFO implements Structure.ByReference {}

		public CRYPT_ALGORITHM_IDENTIFIER Algorithm;
		public CRYPT_BIT_BLOB PublicKey;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("Algorithm", "PublicKey");
		}
	}

	/**
	 * The CRL_CONTEXT structure contains both the encoded and decoded
	 * representations of a certificate revocation list (CRL). CRL contexts returned
	 * by any CryptoAPI function must be freed by calling the CertFreeCRLContext
	 * function.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa379873(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRL_CONTEXT extends Structure {
		public static class ByReference extends CRL_CONTEXT implements Structure.ByReference {}

		public int dwCertEncodingType;
		public Pointer pbCrlEncoded;
		public int cbCrlEncoded;
		public CRL_INFO.ByReference pCrlInfo;
		public HCERTSTORE hCertStore;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwCertEncodingType", "pbCrlEncoded", "cbCrlEncoded", "pCrlInfo", "hCertStore");
		}
	}

	/**
	 * The CRL_ENTRY structure contains information about a single revoked
	 * certificate. It is a member of a CRL_INFO structure.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa379878(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRL_ENTRY extends Structure {
		public static class ByReference extends CRL_ENTRY implements Structure.ByReference {}

		public DATA_BLOB SerialNumber;
		public FILETIME RevocationDate;
		public int cExtension;
		public CERT_EXTENSION.ByReference rgExtension;

		public CERT_EXTENSION[] getRgExtension() {
			return (CERT_EXTENSION[]) rgExtension.toArray(cExtension);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("SerialNumber", "RevocationDate", "cExtension", "rgExtension");
		}
	}

	/**
	 * The CRL_INFO structure contains the information of a certificate revocation
	 * list (CRL).
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa379880(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRL_INFO extends Structure {
		public static class ByReference extends CRL_INFO implements Structure.ByReference {}

		public int dwVersion;
		public CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
		public DATA_BLOB Issuer;
		public FILETIME ThisUpdate;
		public FILETIME NextUpdate;
		public int cCRLEntry;
		public CRL_ENTRY.ByReference rgCRLEntry;
		public int cExtension;
		public CERT_EXTENSION.ByReference rgExtension;

		public CERT_EXTENSION[] getRgExtension() {
			return (CERT_EXTENSION[]) rgExtension.toArray(cExtension);
		}

		public CRL_ENTRY[] getRgCRLEntry() {
			return (CRL_ENTRY[]) rgCRLEntry.toArray(cCRLEntry);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwVersion", "SignatureAlgorithm", "Issuer", "ThisUpdate", "NextUpdate", "cCRLEntry",
					"rgCRLEntry", "cExtension", "rgExtension");
		}
	}

	/**
	 * The CRYPT_ALGORITHM_IDENTIFIER structure specifies an algorithm used to
	 * encrypt a private key. The structure includes the object identifier (OID) of
	 * the algorithm and any needed parameters for that algorithm. The parameters
	 * contained in its CRYPT_OBJID_BLOB are encoded.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381133(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRYPT_ALGORITHM_IDENTIFIER extends Structure {
		public static class ByReference extends CRYPT_ALGORITHM_IDENTIFIER implements Structure.ByReference {}

		public String pszObjId;
		public DATA_BLOB Parameters;
		
		public CRYPT_ALGORITHM_IDENTIFIER() {
            super(W32APITypeMapper.ASCII);
        }

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("pszObjId", "Parameters");
		}
	}

	/**
	 * The CRYPT_ATTRIBUTE structure specifies an attribute that has one or more
	 * values.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381139(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRYPT_ATTRIBUTE extends Structure {
		public static class ByReference extends CRYPT_ATTRIBUTE implements Structure.ByReference {}

		public String pszObjId;
		public int cValue;
		public DATA_BLOB.ByReference rgValue;

		public DATA_BLOB[] getRgValue() {
			return (DATA_BLOB[]) rgValue.toArray(cValue);
		}

		public CRYPT_ATTRIBUTE() {
			super(W32APITypeMapper.ASCII);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("pszObjId", "cValue", "rgValue");
		}
	}

	/**
	 * The CRYPT_BIT_BLOB structure contains a set of bits represented by an array
	 * of bytes.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381165(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRYPT_BIT_BLOB extends Structure {
		public static class ByReference extends CRYPT_BIT_BLOB implements Structure.ByReference {}

		public int cbData;
		public Pointer pbData;
		public int cUnusedBits;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbData", "pbData", "cUnusedBits");
		}
	}

	/**
	 * The CRYPT_KEY_PROV_INFO structure contains information about a key container
	 * within a cryptographic service provider (CSP).
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381420(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRYPT_KEY_PROV_INFO extends Structure {
		public static class ByReference extends CRYPT_KEY_PROV_INFO implements Structure.ByReference {}

		public String pwszContainerName;
		public String pwszProvName;
		public int dwProvType;
		public int dwFlags;
		public int cProvParam;
		public CRYPT_KEY_PROV_PARAM.ByReference rgProvParam;
		public int dwKeySpec;
		
		public CRYPT_KEY_PROV_INFO() {
            super(W32APITypeMapper.UNICODE);
        }

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("pwszContainerName", "pwszProvName", "dwProvType", "dwFlags", "cProvParam",
					"rgProvParam", "dwKeySpec");
		}
	}

	/**
	 * The CRYPT_KEY_PROV_PARAM structure contains information about a key container
	 * parameter. This structure is used with the CRYPT_KEY_PROV_INFO structure.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381423(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRYPT_KEY_PROV_PARAM extends Structure {
		public static class ByReference extends CRYPT_KEY_PROV_PARAM implements Structure.ByReference {}

		public int dwParam;
		public Pointer pbData;
		public int cbData;
		public int dwFlags;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("dwParam", "pbData", "cbData", "dwFlags");
		}
	}

	/**
	 * The CRYPT_SIGN_MESSAGE_PARA structure contains information for signing
	 * messages using a specified signing certificate context.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381468(v=vs.85).aspx">MSDN</a>
	 */
	public static class CRYPT_SIGN_MESSAGE_PARA extends Structure {
		public static class ByReference extends CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {}

		public int cbSize;
		public int dwMsgEncodingType;
		public CERT_CONTEXT.ByReference pSigningCert;
		public CRYPT_ALGORITHM_IDENTIFIER HashAlgorithm;
		public Pointer pvHashAuxInfo;
		public int cMsgCert;
		public PointerByReference rgpMsgCert;
		public int cMsgCrl;
		public PointerByReference rgpMsgCrl;
		public int cAuthAttr;
		public PointerByReference rgAuthAttr;
		public int cUnauthAttr;
		public PointerByReference rgUnauthAttr;
		public int dwFlags;
		public int dwInnerContentType;
		public CRYPT_ALGORITHM_IDENTIFIER HashEncryptionAlgorithm;
		public Pointer pvHashEncryptionAuxInfo;

		public void setRgpMsgCert(CERT_CONTEXT.ByReference[] contexts) {
			this.cMsgCert = contexts.length;
			this.rgpMsgCert = new PointerByReference(contexts[0].getPointer());
		}

		public void setRgpMsgCrl(CRL_CONTEXT.ByReference[] contexts) {
			this.cMsgCrl = contexts.length;
			this.rgpMsgCrl = new PointerByReference(contexts[0].getPointer());
		}

		public void setRgAuthAttr(CRYPT_ATTRIBUTE.ByReference[] contexts) {
			this.cAuthAttr = contexts.length;
			this.rgAuthAttr = new PointerByReference(contexts[0].getPointer());
		}

		public void setRgUnauthAttr(CRYPT_ATTRIBUTE.ByReference[] contexts) {
			this.cUnauthAttr = contexts.length;
			this.rgUnauthAttr = new PointerByReference(contexts[0].getPointer());
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("cbSize", "dwMsgEncodingType", "pSigningCert", "HashAlgorithm", "pvHashAuxInfo",
					"cMsgCert", "rgpMsgCert", "cMsgCrl", "rgpMsgCrl", "cAuthAttr", "rgAuthAttr", "cUnauthAttr",
					"rgUnauthAttr", "dwFlags", "dwInnerContentType", "HashEncryptionAlgorithm",
					"pvHashEncryptionAuxInfo");
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
         * @param p
         *            the p
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

    /**
     * The CRYPTPROTECT_PROMPTSTRUCT structure provides the text of a prompt and
     * information about when and where that prompt is to be displayed when using
     * the CryptProtectData and CryptUnprotectData functions.
     */
    public static class CRYPTPROTECT_PROMPTSTRUCT extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("cbSize", "dwPromptFlags", "hwndApp", "szPrompt");
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

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
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
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376078(v=vs.85).aspx">MSDN</a>
	 */
	HCERTCHAINENGINE HCCE_CURRENT_USER = new HCERTCHAINENGINE(Pointer.createConstant(0x0));
	
	/**
	 * Predefined certificate chain engine values.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376078(v=vs.85).aspx">MSDN</a>
	 */
    HCERTCHAINENGINE HCCE_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(0x1));
	
	/**
	 * Predefined certificate chain engine values.
	 * 
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa376078(v=vs.85).aspx">MSDN</a>
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
}
