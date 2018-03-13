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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinCrypt.CRYPTPROTECT_PROMPTSTRUCT;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.platform.win32.WinCrypt.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WTypes.LPSTR;

/**
 * Crypt32.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface Crypt32 extends StdCallLibrary {

	Crypt32 INSTANCE = Native.loadLibrary("Crypt32", Crypt32.class, W32APIOptions.DEFAULT_OPTIONS);

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

	/**
	 * The CertAddEncodedCertificateToSystemStore function opens the specified
	 * system store and adds the encoded certificate to it.
	 *
	 * @param szCertStoreName
	 *            A null-terminated string that contains the name of the system
	 *            store for the encoded certificate.
	 * @param pbCertEncoded
	 *            A pointer to a buffer that contains the encoded certificate to
	 *            add.
	 * @param cbCertEncoded
	 *            The size, in bytes, of the pbCertEncoded buffer.
	 * @return If the function succeeds, the return value is TRUE.<br>
	 *         If the function fails, the return value is FALSE.<br>
	 *         CertAddEncodedCertificateToSystemStore depends on the functions
	 *         listed in the following remarks for error handling. <br>
	 *         Refer to those function topics for their respective error
	 *         handling behaviors.<br>
	 *         For extended error information, call GetLastError.
	 * @see <a href="http://msdn.microsoft.com/en-us/library/bb736347(v=vs.85).aspx">MSDN</a>
         */
       boolean CertAddEncodedCertificateToSystemStore(String szCertStoreName, Pointer pbCertEncoded, int cbCertEncoded);

       /**
        * The CertOpenSystemStore function is a simplified function that opens the
        * most common system certificate store. To open certificate stores with
        * more complex requirements, such as file-based or memory-based stores, use
        * CertOpenStore.
        *
        * @param hprov This parameter is not used and should be set to NULL.
        * @param szSubsystemProtocol A string that names a system store. If the
        * system store name provided in this parameter is not the name of an
        * existing system store, a new system store will be created and used.
        * CertEnumSystemStore can be used to list the names of existing system
        * stores. Some example system stores are listed in the following table.
        * @return If the function succeeds, the function returns a handle to the
        * certificate store. If the function fails, it returns NULL. For extended
        * error information, call GetLastError.
        */
       HCERTSTORE CertOpenSystemStore(Pointer hprov, String szSubsystemProtocol);

       /**
        * The CryptSignMessage function creates a hash of the specified content,
        * signs the hash, and then encodes both the original message content and
        * the signed hash.
        *
        * @param pSignPara A pointer to CRYPT_SIGN_MESSAGE_PARA structure
        * containing the signature parameters.
        * @param fDetachedSignature TRUE if this is to be a detached signature.
        * Otherwise, FALSE. If this parameter is set to TRUE, only the signed hash
        * is encoded in pbSignedBlob. Otherwise, both rgpbToBeSigned and the signed
        * hash are encoded.
        * @param cToBeSigned Count of the number of array elements in
        * rgpbToBeSigned and rgpbToBeSigned. This parameter must be set to one
        * unless fDetachedSignature is set to TRUE.
        * @param rgpbToBeSigned Array of pointers to buffers that contain the
        * contents to be signed.
        * @param rgcbToBeSigned Array of sizes, in bytes, of the content buffers
        * pointed to in rgpbToBeSigned.
        * @param pbSignedBlob A pointer to a buffer to receive the encoded signed
        * hash, if fDetachedSignature is TRUE, or to both the encoded content and
        * signed hash if fDetachedSignature is FALSE.
        * @param pcbSignedBlob A pointer to a DWORD specifying the size, in bytes,
        * of the pbSignedBlob buffer. When the function returns, this variable
        * contains the size, in bytes, of the signed and encoded message.
        * @return If the function succeeds, the return value is nonzero (TRUE). If
        * the function fails, the return value is zero (FALSE).
        */
       boolean CryptSignMessage(CRYPT_SIGN_MESSAGE_PARA pSignPara, boolean fDetachedSignature, int cToBeSigned,
               Pointer[] rgpbToBeSigned, int[] rgcbToBeSigned, Pointer pbSignedBlob, IntByReference pcbSignedBlob);

       /**
        * The CryptVerifyMessageSignature function verifies a signed message's
        * signature.
        *
        * This function should not be used to verify the signature of a detached
        * message. You should use the CryptVerifyDetachedMessageSignature function
        * to verify the signature of a detached message.
        *
        * @param pVerifyPara A pointer to a CRYPT_VERIFY_MESSAGE_PARA structure
        * that contains verification parameters.
        * @param signerIndex The index of the de sired signature. There can be more
        * than one signature. CryptVerifyMessageSignature can be called repeatedly,
        * incrementing dwSignerIndex each time.
        *
        * <p>
        * Set this pa rameter to zero for the first signer, or if there is only one
        * signer. If the function returns FALSE, and GetLastError returns
        * CRYPT_E_NO_SIGNER, the previous call processed the last signer of the
        * message.</p>
        * @param pbSignedBlob A pointer to a buffe r that contains the signed
        * message.
        * @param cbSignedBlob The size, in bytes, of the signed message buffer.
        * @param pbDecoded * A pointer to a buffer to receive the decoded message.
        *
        * <p>
        * This parameter can be NULL if the decoded message is not needed for
        * additional processing or to set the size of the message for memory
        * allocation purposes. For more information, see Retrieving Data of Unknown
        * Length.</p>
        *
        * @param pcbDecoded A pointer to a DWO RD value that specifies the size, in
        * bytes, of the pbDecoded buffer. When the function returns, this DWORD
        * contains the size, in bytes, of the decoded message. The decoded message
        * will not be returned if this parameter is NULL.
        * @param ppSignerCert The address of a CER T_CONTEXT structure pointer that
        * receives the certificate of the signer. When you have finished using this
        * structure, free it by passing this pointer to the
        * CertFreeCertificateContext function. This parameter can be NULL if the
        * signer's certificate is not needed.
        * @return If the function succeeds, the function returns nonzero. This does
        * not necessarily mean that the signature was verified. In the case of a
        * detached message, the variable pointe d to by pcbDecoded will contain
        * zero. In this case, this funct ion will return nonzero, but the signature
        * is not verified . To verify the signature of a detached message, use the
        * CryptVerifyDetachedMessageSignature function.
        *
        * <p>
        * If the function succeeds, the function returns nonzero. This does not
        * necessarily mean that the signature was verified. In the case of a
        * detached message, the variable pointed to by pcbDecoded will contain
        * zero. In this case, this function will return nonzero, but the signature
        * is not verified. To verify the signature of a detached message, use t he
        * CryptVerifyDetachedMessageSignature function.
        */
       boolean CryptVerifyMessageSignature(CRYPT_VERIFY_MESSAGE_PARA pVerifyPara,
               int signerIndex, Pointer pbSignedBlob, int cbSignedBlob,
               Pointer pbDecoded, IntByReference pcbDecoded, PointerByReference ppSignerCert);

       /**
        * The CertGetCertificateChain function builds a certificate chain context
        * starting from an end certificate and going back, if possible, to a
        * trusted root certificate.
        *
        * @param hChainEngine A handle of the chain engine (namespace and cache) to
        * be used. If hChainEngine is NULL, the default chain engine,
        * HCCE_CURRENT_USER, is used. This parameter can be set to
        * HCCE_LOCAL_MACHINE.
        * @param pCertContext A pointer to the CERT_CONTEXT of the end certificate,
        * the certificate for which a chain is being built. This certificate
        * context will be the zero-index element in the first simple chain.
        * @param pTime A pointer to a FILETIME variable that indicates the time for
        * which the chain is to be validated. Note that the time does not affect
        * trust list, revocation, or root store checking. The current system time
        * is used if NULL is passed to this parameter. Trust in a particular
        * certificate being a trusted root is based on the current state of the
        * root store and not the state of the root store at a time passed in by
        * this parameter. For revocation, a certificate revocation list (CRL),
        * itself, must be valid at the current time. The value of this parameter is
        * used to determine whether a certificate listed in a CRL has been revoked.
        * @param hAdditionalStore A handle to any additional store to search for
        * supporting certificates and certificate trust lists (CTLs). This
        * parameter can be NULL if no additional store is to be searched.
        * @param pChainPara A pointer to a CERT_CHAIN_PARA structure that includes
        * chain-building parameters.
        * @param dwFlags Flag values that indicate special processing. This
        * parameter can be a combination of one or more of the following flags.
        * @param pvReserved This parameter is reserved and must be NULL.
        * @param ppChainContext The address of a pointer to the chain context
        * created. When you have finished using the chain context, release the
        * chain by calling the CertFreeCertificateChain function.
        * @return If the function succeeds, the function returns nonzero (TRUE). If
        * the function fails, it returns zero (FALSE).
        */
       boolean CertGetCertificateChain(HCERTCHAINENGINE hChainEngine, CERT_CONTEXT pCertContext, FILETIME pTime,
               HCERTSTORE hAdditionalStore, CERT_CHAIN_PARA pChainPara, int dwFlags, Pointer pvReserved,
               PointerByReference ppChainContext);

       /**
        * The CertFreeCertificateContext function frees a certificate context by
        * decrementing its reference count. When the reference count goes to zero,
        * CertFreeCertificateContext frees the memory used by a certificate
        * context.
        *
        * @param pCertContext A pointer to the CERT_CONTEXT to be freed.
        * @return The function always returns nonzero.
        */
       boolean CertFreeCertificateContext(CERT_CONTEXT pCertContext);

       /**
        * The CertFreeCertificateChain function frees a certificate chain by
        * reducing its reference count. If the reference count becomes zero, memory
        * allocated for the chain is released.
        *
        * <p>To free a context obtained by a get, duplicate, or create function, call
        * the appropriate free function. To free a context obtained by a find or
        * enumerate function, either pass it in as the previous context parameter
        * to a subsequent invocation of the function, or call the appropriate free
        * function. For more information, see the reference topic for the function
        * that obtains the context.</p>
        * 
        * @param pChainContext A pointer to a CERT_CHAIN_CONTEXT certificate chain
        * context to be freed. If the reference count on the context reaches zero,
        * the storage allocated for the context is freed.
        */
       void CertFreeCertificateChain(CERT_CHAIN_CONTEXT pChainContext);

       /**
        * The CertCloseStore function closes a certificate store handle and reduces
        * the reference count on the store. There needs to be a corresponding call
        * to CertCloseStore for each successful call to the CertOpenStore or
        * CertDuplicateStore functions.
        *
        * @param hCertStore Handle of the certificate store to be closed.
        * @param dwFlags Typically, this parameter uses the default value zero. The
        * default is to close the store with memory remaining allocated for
        * contexts that have not been freed. In this case, no check is made to
        * determine whether memory for contexts remains allocated.
        * @return If the function succeeds, the return value is TRUE. If the
        * function fails, the return value is FALSE.
        */
       boolean CertCloseStore(HCERTSTORE hCertStore, int dwFlags);

       /**
        * The CertNameToStr function converts an encoded name in a CERT_NAME_BLOB
        * structure to a character string.
        *
        * @param dwCertEncodingType The certificate encoding type that was used to
        * encode the name. The message encoding type identifier, contained in the
        * high WORD of this value, is ignored by this function.
        * @param pName A pointer to the CERT_NAME_BLOB structure to be converted.
        * @param dwStrType This parameter specifies the format of the output
        * string. This parameter also specifies other options for the contents of
        * the string.
        * @param psz A pointer to a character buffer that receives the returned
        * string. The size of this buffer is specified in the csz parameter.
        * @param csz The size, in characters, of the psz buffer. The size must
        * include the terminating null character.
        * @return Returns the number of characters converted, including the
        * terminating null character. If psz is NULL or csz is zero, returns the
        * required size of the destination string.
        */
       int CertNameToStr(int dwCertEncodingType, DATA_BLOB pName, int dwStrType, Pointer psz, int csz);

       /**
        * The CertVerifyCertificateChainPolicy function checks a certificate chain
        * to verify its validity, including its compliance with any specified
        * validity policy criteria.
        *
        * @param pszPolicyOID Current predefined verify chain policy structures are
        * listed in the following table.
        * @param pChainContext A pointer to a CERT_CHAIN_CONTEXT structure that
        * contains a chain to be verified.
        * @param pPolicyPara A pointer to a CERT_CHAIN_POLICY_PARA structure that
        * provides the policy verification criteria for the chain. The dwFlags
        * member of that structure can be set to change the default policy checking
        * behavior.
        * @param pPolicyStatus A pointer to a CERT_CHAIN_POLICY_STATUS structure
        * where status information on the chain is returned. OID-specific extra
        * status can be returned in the pvExtraPolicyStatus member of this
        * structure.
        * @return The return value indicates whether the function was able to check
        * for the policy, it does not indicate whether the policy check failed or
        * passed.
        *
        * If the chain can be verified for the specified policy, TRUE is returned
        * and the dwError member of the pPolicyStatus is updated. A dwError of 0
        * (ERROR_SUCCESS or S_OK) indicates the chain satisfies the specified
        * policy.
        *
        * If the chain cannot be validated, the return value is TRUE and you need
        * to verify the pPolicyStatus parameter for the actual error.
        *
        * A value of FALSE indicates that the function wasn't able to check for the
        * policy.
        */
       boolean CertVerifyCertificateChainPolicy(LPSTR pszPolicyOID, CERT_CHAIN_CONTEXT pChainContext,
               CERT_CHAIN_POLICY_PARA pPolicyPara, CERT_CHAIN_POLICY_STATUS pPolicyStatus);

       /**
        * The CertFindCertificateInStore function finds the first or next
        * certificate context in a certificate store that matches a search criteria
        * established by the dwFindType and its associated pvFindPara. This
        * function can be used in a loop to find all of the certificates in a
        * certificate store that match the specified find criteria.
        *
        * @param hCertStore A handle of the certificate store to be searched.
        * @param dwCertEncodingType Specifies the type of encoding used. Both the
        * certificate and message encoding types must be specified by combining
        * them with a bitwise-OR.
        * @param dwFindFlags Used with some dwFindType values to modify the search
        * criteria. For most dwFindType values, dwFindFlags is not used and should
        * be set to zero.
        * @param dwFindType Specifies the type of search being made. The search
        * type determines the data type, contents, and the use of pvFindPara.
        * @param pvFindPara Points to a data item or structure used with
        * dwFindType.
        * @param pPrevCertContext A pointer to the last CERT_CONTEXT structure
        * returned by this function. This parameter must be NULL on the first call
        * of the function. To find successive certificates meeting the search
        * criteria, set pPrevCertContext to the pointer returned by the previous
        * call to the function. This function frees the CERT_CONTEXT referenced by
        * non-NULL values of this parameter.
        * @return If the function succeeds, the function returns a pointer to a
        * read-only CERT_CONTEXT structure.
        *
        * If the function fails and a certificate that matches the search criteria
        * is not found, the return value is NULL.
        *
        * A non-NULL CERT_CONTEXT that CertFindCertificateInStore returns must be
        * freed by CertFreeCertificateContext or by being passed as the
        * pPrevCertContext parameter on a subsequent call to
        * CertFindCertificateInStore.
        */
       CERT_CONTEXT.ByReference CertFindCertificateInStore(HCERTSTORE hCertStore, int dwCertEncodingType, int dwFindFlags,
               int dwFindType, Pointer pvFindPara, CERT_CONTEXT pPrevCertContext);

       /**
        * The PFXImportCertStore function imports a PFX BLOB and returns the handle
        * of a store that contains certificates and any associated private keys.
        *
        * @param pPFX A pointer to a CRYPT_DATA_BLOB structure that contains a PFX
        * packet with the exported and encrypted certificates and keys.
        * @param szPassword A string password used to decrypt and verify the PFX
        * packet. Whether set to a string of length greater than zero or set to an
        * empty string or to NULL, this value must be exactly the same as the value
        * that was used to encrypt the packet.
        *
        * <p>
        * Beginning with Windows 8 and Windows Server 2012, if the PFX packet was
        * created in the PFXExportCertStoreEx function by using the
        * PKCS12_PROTECT_TO_DOMAIN_SIDS flag, the PFXImportCertStore function
        * attempts to decrypt the password by using the Active Directory (AD)
        * principal that was used to encrypt it. The AD principal is specified in
        * the pvPara parameter. If the szPassword parameter in the
        * PFXExportCertStoreEx function was an empty string or NULL and the dwFlags
        * parameter was set to PKCS12_PROTECT_TO_DOMAIN_SIDS, that function
        * randomly generated a password and encrypted it to the AD principal
        * specified in the pvPara parameter. In that case you should set the
        * password to the value, empty string or NULL, that was used when the PFX
        * packet was created. The PFXImportCertStore function will use the AD
        * principal to decrypt the random password, and the randomly generated
        * password will be used to decrypt the PFX certificate.</p>
        *
        * <p>
        * When you have finished using the password, clear it from memory by
        * calling the SecureZeroMemory function. For more information about
        * protecting passwords, see Handling Passwords.</p>
        *
        * @param dwFlags This parameter can be one of the following values.
        * <ul>
        * <li>{@link WinCrypt#CRYPT_EXPORTABLE}</li>
        * <li>{@link WinCrypt#CRYPT_USER_PROTECTED}</li>
        * <li>{@link WinCrypt#CRYPT_MACHINE_KEYSET}</li>
        * <li>{@link WinCrypt#CRYPT_USER_KEYSET}</li>
        * <li>{@link WinCrypt#PKCS12_PREFER_CNG_KSP}</li>
        * <li>{@link WinCrypt#PKCS12_ALWAYS_CNG_KSP}</li>
        * <li>{@link WinCrypt#PKCS12_ALLOW_OVERWRITE_KEY}</li>
        * <li>{@link WinCrypt#PKCS12_NO_PERSIST_KEY}</li>
        * <li>{@link WinCrypt#PKCS12_INCLUDE_EXTENDED_PROPERTIES}</li>
        * </ul>
        *
        * @return If the function succeeds, the function returns a handle to a
        * certificate store that contains the imported certificates, including
        * available private keys.
        *
        * <p>
        * If the function fails, that is, if the password parameter does not
        * contain an exact match with the password used to encrypt the exported
        * packet or if there were any other problems decoding the PFX BLOB, the
        * function returns NULL, and an error code can be found by calling the
        * GetLastError function.</p>
        *
        * @see <a href=
        *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa387314(v=vs.85).aspx">MSDN</a>
        */
       HCERTSTORE PFXImportCertStore(DATA_BLOB pPFX, WTypes.LPWSTR szPassword, int dwFlags);
}
