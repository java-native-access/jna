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

import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.W32APITypeMapper;

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
}
