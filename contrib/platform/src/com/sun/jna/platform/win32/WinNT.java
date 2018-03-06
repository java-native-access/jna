/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.util.Collections;
import java.util.List;

import com.sun.jna.FromNativeContext;
import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

/**
 * This module defines the 32-Bit Windows types and constants that are defined
 * by NT, but exposed through the Win32 API. Ported from WinNT.h Microsoft
 * Windows SDK 6.0A. Avoid including any NIO Buffer mappings here; put them in a
 * DLL-derived interface (e.g. kernel32, user32, etc) instead.
 *
 * @author dblock[at]dblock.org
 */
@SuppressWarnings("serial")
public interface WinNT extends WinError, WinDef, WinBase, BaseTSD {

    int MINCHAR     = 0x80;
    int MAXCHAR     = 0x7f;
    int MINSHORT    = 0x8000;
    int MAXSHORT    = 0x7fff;
    int MINLONG     = 0x80000000;
    int MAXLONG     = 0x7fffffff;
    int MAXBYTE     = 0xff;
    int MAXWORD     = 0xffff;
    int MAXDWORD    = 0xffffffff;
    //
    // The following are masks for the predefined standard access types
    //

    int DELETE = 0x00010000;
    int READ_CONTROL = 0x00020000;
    int WRITE_DAC = 0x00040000;
    int WRITE_OWNER = 0x00080000;
    int SYNCHRONIZE = 0x00100000;

    int STANDARD_RIGHTS_REQUIRED = 0x000F0000;
    int STANDARD_RIGHTS_READ = READ_CONTROL;
    int STANDARD_RIGHTS_WRITE = READ_CONTROL;
    int STANDARD_RIGHTS_EXECUTE = READ_CONTROL;
    int STANDARD_RIGHTS_ALL = 0x001F0000;

    int SPECIFIC_RIGHTS_ALL = 0x0000FFFF;

    //
    // Token Specific Access Rights.
    //

    /**
     * Required to attach a primary token to a process. The
     * SE_ASSIGNPRIMARYTOKEN_NAME privilege is also required to accomplish this
     * task.
     */
    int TOKEN_ASSIGN_PRIMARY = 0x0001;

    /**
     * Required to duplicate an access token.
     */
    int TOKEN_DUPLICATE = 0x0002;

    /**
     * Required to attach an impersonation access token to a process.
     */
    int TOKEN_IMPERSONATE = 0x0004;

    /**
     * Required to query an access token.
     */
    int TOKEN_QUERY = 0x0008;

    /**
     * Required to query the source of an access token.
     */
    int TOKEN_QUERY_SOURCE = 0x0010;

    /**
     * Required to enable or disable the privileges in an access token.
     */
    int TOKEN_ADJUST_PRIVILEGES = 0x0020;

    /**
     * Required to adjust the attributes of the groups in an access token.
     */
    int TOKEN_ADJUST_GROUPS = 0x0040;

    /**
     * Required to change the default owner, primary group, or DACL of an access
     * token.
     */
    int TOKEN_ADJUST_DEFAULT = 0x0080;

    /**
     * Required to adjust the session ID of an access token. The SE_TCB_NAME
     * privilege is required.
     */
    int TOKEN_ADJUST_SESSIONID = 0x0100;

    int TOKEN_ALL_ACCESS_P = STANDARD_RIGHTS_REQUIRED | TOKEN_ASSIGN_PRIMARY
            | TOKEN_DUPLICATE | TOKEN_IMPERSONATE | TOKEN_QUERY
            | TOKEN_QUERY_SOURCE | TOKEN_ADJUST_PRIVILEGES
            | TOKEN_ADJUST_GROUPS | TOKEN_ADJUST_DEFAULT;

    /**
     * Combines all possible access rights for a token.
     */
    int TOKEN_ALL_ACCESS = TOKEN_ALL_ACCESS_P | TOKEN_ADJUST_SESSIONID;

    /**
     * Combines STANDARD_RIGHTS_READ and TOKEN_QUERY.
     */
    int TOKEN_READ = STANDARD_RIGHTS_READ | TOKEN_QUERY;

    /**
     * Combines STANDARD_RIGHTS_WRITE, TOKEN_ADJUST_PRIVILEGES,
     * TOKEN_ADJUST_GROUPS, and TOKEN_ADJUST_DEFAULT.
     */
    int TOKEN_WRITE = STANDARD_RIGHTS_WRITE | TOKEN_ADJUST_PRIVILEGES
            | TOKEN_ADJUST_GROUPS | TOKEN_ADJUST_DEFAULT;

    /**
     * Combines STANDARD_RIGHTS_EXECUTE and TOKEN_IMPERSONATE.
     */
    int TOKEN_EXECUTE = STANDARD_RIGHTS_EXECUTE;

    int THREAD_TERMINATE = 0x0001;
    int THREAD_SUSPEND_RESUME = 0x0002;
    int THREAD_GET_CONTEXT = 0x0008;
    int THREAD_SET_CONTEXT = 0x0010;
    int THREAD_QUERY_INFORMATION = 0x0040;
    int THREAD_SET_INFORMATION = 0x0020;
    int THREAD_SET_THREAD_TOKEN = 0x0080;
    int THREAD_IMPERSONATE = 0x0100;
    int THREAD_DIRECT_IMPERSONATION = 0x0200;
    int THREAD_SET_LIMITED_INFORMATION = 0x0400;
    int THREAD_QUERY_LIMITED_INFORMATION = 0x0800;
    int THREAD_ALL_ACCESS = STANDARD_RIGHTS_REQUIRED | SYNCHRONIZE | 0x3FF;

    /**
     * The SECURITY_IMPERSONATION_LEVEL enumeration type contains values that
     * specify security impersonation levels. Security impersonation levels
     * govern the degree to which a server process can act on behalf of a client
     * process.
     */
    public abstract class SECURITY_IMPERSONATION_LEVEL {
        /**
         * The server process cannot obtain identification information about the
         * client, and it cannot impersonate the client. It is defined with no
         * value given, and thus, by ANSI C rules, defaults to a value of zero.
         */
        public static final int SecurityAnonymous = 0;

        /**
         * The server process can obtain information about the client, such as
         * security identifiers and privileges, but it cannot impersonate the
         * client. This is useful for servers that export their own objects, for
         * example, database products that export tables and views. Using the
         * retrieved client-security information, the server can make
         * access-validation decisions without being able to use other services
         * that are using the client's security context.
         */
        public static final int SecurityIdentification = 1;

        /**
         * The server process can impersonate the client's security context on
         * its local system. The server cannot impersonate the client on remote
         * systems.
         */
        public static final int SecurityImpersonation = 2;

        /**
         * The server process can impersonate the client's security context on
         * remote systems.
         */
        public static final int SecurityDelegation = 3;
    }

    /**
     * The TOKEN_INFORMATION_CLASS enumeration type contains values that specify
     * the type of information being assigned to or retrieved from an access
     * token.
     */
    public abstract class TOKEN_INFORMATION_CLASS {
        public static final int TokenUser = 1;
        public static final int TokenGroups = 2;
        public static final int TokenPrivileges = 3;
        public static final int TokenOwner = 4;
        public static final int TokenPrimaryGroup = 5;
        public static final int TokenDefaultDacl = 6;
        public static final int TokenSource = 7;
        public static final int TokenType = 8;
        public static final int TokenImpersonationLevel = 9;
        public static final int TokenStatistics = 10;
        public static final int TokenRestrictedSids = 11;
        public static final int TokenSessionId = 12;
        public static final int TokenGroupsAndPrivileges = 13;
        public static final int TokenSessionReference = 14;
        public static final int TokenSandBoxInert = 15;
        public static final int TokenAuditPolicy = 16;
        public static final int TokenOrigin = 17;
        public static final int TokenElevationType = 18;
        public static final int TokenLinkedToken = 19;
        public static final int TokenElevation = 20;
        public static final int TokenHasRestrictions = 21;
        public static final int TokenAccessInformation = 22;
        public static final int TokenVirtualizationAllowed = 23;
        public static final int TokenVirtualizationEnabled = 24;
        public static final int TokenIntegrityLevel = 25;
        public static final int TokenUIAccess = 26;
        public static final int TokenMandatoryPolicy = 27;
        public static final int TokenLogonSid = 28;
    }

    /**
     * The TOKEN_TYPE enumeration type contains values that differentiate
     * between a primary token and an impersonation token.
     */
    public abstract class TOKEN_TYPE {
        public static final int TokenPrimary = 1;
        public static final int TokenImpersonation = 2;
    }

    /**
     * The LUID_AND_ATTRIBUTES structure represents a locally unique identifier
     * (LUID) and its attributes.
     */
    public static class LUID_AND_ATTRIBUTES extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("Luid", "Attributes");
        /**
         * Specifies an LUID value.
         */
        public LUID Luid;

        /**
         * Specifies attributes of the LUID. This value contains up to 32
         * one-bit flags. Its meaning is dependent on the definition and use of
         * the LUID.
         */
        public DWORD Attributes;

        public LUID_AND_ATTRIBUTES() {
            super();
        }

        public LUID_AND_ATTRIBUTES(LUID luid, DWORD attributes) {
            this.Luid = luid;
            this.Attributes = attributes;
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The SID_AND_ATTRIBUTES structure represents a security identifier (SID)
     * and its attributes. SIDs are used to uniquely identify users or groups.
     */
    public static class SID_AND_ATTRIBUTES extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("Sid", "Attributes");
        /**
         * Pointer to a SID structure.
         */
        public PSID.ByReference Sid;

        /**
         * Specifies attributes of the SID. This value contains up to 32 one-bit
         * flags. Its meaning depends on the definition and use of the SID.
         */
        public int Attributes;

        public SID_AND_ATTRIBUTES() {
            super();
        }

        public SID_AND_ATTRIBUTES(Pointer memory) {
            super(memory);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The TOKEN_OWNER structure contains the default owner security identifier
     * (SID) that will be applied to newly created objects.
     */
    public static class TOKEN_OWNER extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("Owner");
        /**
         * Pointer to a SID structure representing a user who will become the
         * owner of any objects created by a process using this access token.
         * The SID must be one of the user or group SIDs already in the token.
         */
        public PSID.ByReference Owner; // PSID

        public TOKEN_OWNER() {
            super();
        }

        public TOKEN_OWNER(int size) {
            super(new Memory(size));
        }

        public TOKEN_OWNER(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class PSID extends Structure {
        public static class ByReference extends PSID implements Structure.ByReference { }
        public static final List<String> FIELDS = createFieldsOrder("sid");
        public Pointer sid;

        public PSID() {
            super();
        }

        public PSID(byte[] data) {
            super(new Memory(data.length));
            getPointer().write(0, data, 0, data.length);
            read();
        }

        public PSID(int size) {
            super(new Memory(size));
        }

        public PSID(Pointer memory) {
            super(memory);
            read();
        }

        public byte[] getBytes() {
            int len = Advapi32.INSTANCE.GetLengthSid(this);
            return getPointer().getByteArray(0, len);
        }

        public String getSidString() {
            return Advapi32Util.convertSidToStringSid(this);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class PSIDByReference extends ByReference {
        public PSIDByReference() {
            this(null);
        }

        public PSIDByReference(PSID h) {
            super(Native.POINTER_SIZE);
            setValue(h);
        }

        public void setValue(PSID h) {
            getPointer().setPointer(0, h != null ? h.getPointer() : null);
        }

        public PSID getValue() {
            Pointer p = getPointer().getPointer(0);
            if (p == null) {
                return null;
            }
            else {
                return new PSID(p);
            }
        }
    }

    /**
     * The TOKEN_USER structure identifies the user associated with an access
     * token.
     */
    public static class TOKEN_USER extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("User");
        /**
         * Specifies a SID_AND_ATTRIBUTES structure representing the user
         * associated with the access token. There are currently no attributes
         * defined for user security identifiers (SIDs).
         */
        public SID_AND_ATTRIBUTES User;

        public TOKEN_USER() {
            super();
        }

        public TOKEN_USER(Pointer memory) {
            super(memory);
            read();
        }

        public TOKEN_USER(int size) {
            super(new Memory(size));
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The TOKEN_GROUPS structure contains information about the group security
     * identifiers (SIDs) in an access token.
     */
    public static class TOKEN_GROUPS extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("GroupCount", "Group0");
        /**
         * Specifies the number of groups in the access token.
         */
        public int GroupCount;
        public SID_AND_ATTRIBUTES Group0;

        public TOKEN_GROUPS() {
            super();
        }

        public TOKEN_GROUPS(Pointer memory) {
            super(memory);
            read();
        }

        public TOKEN_GROUPS(int size) {
            super(new Memory(size));
        }

        /**
         * Specifies an array of SID_AND_ATTRIBUTES structures that contain a
         * set of SIDs and corresponding attributes.
         * @return attributes
         */
        public SID_AND_ATTRIBUTES[] getGroups() {
            return (SID_AND_ATTRIBUTES[]) Group0.toArray(GroupCount);
        }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Specifies a set of privileges. <br>
     * It is also used to indicate which, if any, privileges are held by a user or group requesting access to an object.
     */
    public static class PRIVILEGE_SET extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("PrivilegeCount", "Control", "Privileges");
        public DWORD PrivilegeCount;
        public DWORD Control;
        public LUID_AND_ATTRIBUTES Privileges[];

        public PRIVILEGE_SET() {
            this(0);
        }
        /**
         * @param nbOfPrivileges
         *            Desired size of the Privileges array
         */
        public PRIVILEGE_SET(int nbOfPrivileges) {
            PrivilegeCount = new DWORD(nbOfPrivileges);
            if(nbOfPrivileges > 0) {
                Privileges = new LUID_AND_ATTRIBUTES[nbOfPrivileges];
            }
        }

        /** Initialize a TOKEN_PRIVILEGES instance from initialized memory.
         * @param p base address
         */
        public PRIVILEGE_SET(Pointer p) {
            super(p);
            final int count = p.getInt(0);
            PrivilegeCount = new DWORD(count);
            if(count > 0) {
                Privileges = new LUID_AND_ATTRIBUTES[count];
            }
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The TOKEN_PRIVILEGES structure contains information about a set of
     * privileges for an access token.
     */
    public static class TOKEN_PRIVILEGES extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("PrivilegeCount", "Privileges");
        /**
         * This must be set to the number of entries in the Privileges array.
         */
        public DWORD PrivilegeCount;

        /**
         * Specifies an array of LUID_AND_ATTRIBUTES structures. Each structure
         * contains the LUID and attributes of a privilege.
         */
        public LUID_AND_ATTRIBUTES Privileges[];

        /** Creates an empty instance with no privileges. */
        public TOKEN_PRIVILEGES() {
            this(0);
        }
        /**
         * @param nbOfPrivileges
         *            Desired size of the Privileges array
         */
        public TOKEN_PRIVILEGES(int nbOfPrivileges) {
            PrivilegeCount = new DWORD(nbOfPrivileges);
            Privileges = new LUID_AND_ATTRIBUTES[nbOfPrivileges];
        }

        /** Initialize a TOKEN_PRIVILEGES instance from initialized memory.
         * @param p base address
         */
        public TOKEN_PRIVILEGES(Pointer p) {
            super(p);
            int count = p.getInt(0);
            PrivilegeCount = new DWORD(count);
            Privileges = new LUID_AND_ATTRIBUTES[count];
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The SID_NAME_USE enumeration type contains values that specify the type
     * of a security identifier (SID).
     */
    public abstract class SID_NAME_USE {
        /**
         * Indicates a user SID.
         */
        public static final int SidTypeUser = 1;

        /**
         * Indicates a group SID.
         */
        public static final int SidTypeGroup = 2;

        /**
         * Indicates a domain SID.
         */
        public static final int SidTypeDomain = 3;

        /**
         * Indicates an alias SID.
         */
        public static final int SidTypeAlias = 4;

        /**
         * Indicates a SID for a well-known group.
         */
        public static final int SidTypeWellKnownGroup = 5;

        /**
         * Indicates a SID for a deleted account.
         */
        public static final int SidTypeDeletedAccount = 6;

        /**
         * Indicates an invalid SID.
         */
        public static final int SidTypeInvalid = 7;

        /**
         * Indicates an unknown SID type.
         */
        public static final int SidTypeUnknown = 8;

        /**
         * Indicates a SID for a computer.
         */
        public static final int SidTypeComputer = 9;

        /**
         * ?
         */
        public static final int SidTypeLabel = 10;
    }

    /* File access rights */
    int FILE_READ_DATA = 0x00000001;
    int FILE_LIST_DIRECTORY = 0x00000001;
    int FILE_WRITE_DATA = 0x00000002;
    int FILE_ADD_FILE = 0x00000002;
    int FILE_APPEND_DATA = 0x00000004;
    int FILE_ADD_SUBDIRECTORY = 0x00000004;
    int FILE_CREATE_PIPE_INSTANCE = 0x00000004;
    int FILE_READ_EA = 0x00000008;
    int FILE_WRITE_EA = 0x00000010;
    int FILE_EXECUTE = 0x00000020;
    int FILE_TRAVERSE = 0x00000020;
    int FILE_DELETE_CHILD = 0x00000040;
    int FILE_READ_ATTRIBUTES = 0x00000080;
    int FILE_WRITE_ATTRIBUTES = 0x00000100;

    int FILE_ALL_ACCESS = STANDARD_RIGHTS_REQUIRED | SYNCHRONIZE | 0x000001FF;

    int FILE_GENERIC_READ = STANDARD_RIGHTS_READ | SYNCHRONIZE | FILE_READ_DATA
            | FILE_READ_ATTRIBUTES | FILE_READ_EA;

    int FILE_GENERIC_WRITE = STANDARD_RIGHTS_WRITE | SYNCHRONIZE
            | FILE_WRITE_DATA | FILE_WRITE_ATTRIBUTES | FILE_WRITE_EA
            | FILE_APPEND_DATA;

    int FILE_GENERIC_EXECUTE = STANDARD_RIGHTS_EXECUTE | SYNCHRONIZE
            | FILE_READ_ATTRIBUTES | FILE_EXECUTE;

    int CREATE_NEW = 1;
    int CREATE_ALWAYS = 2;
    int OPEN_EXISTING = 3;
    int OPEN_ALWAYS = 4;
    int TRUNCATE_EXISTING = 5;

    int FILE_FLAG_WRITE_THROUGH = 0x80000000;
    int FILE_FLAG_OVERLAPPED = 0x40000000;
    int FILE_FLAG_NO_BUFFERING = 0x20000000;
    int FILE_FLAG_RANDOM_ACCESS = 0x10000000;
    int FILE_FLAG_SEQUENTIAL_SCAN = 0x08000000;
    int FILE_FLAG_DELETE_ON_CLOSE = 0x04000000;
    int FILE_FLAG_BACKUP_SEMANTICS = 0x02000000;
    int FILE_FLAG_POSIX_SEMANTICS = 0x01000000;
    int FILE_FLAG_OPEN_REPARSE_POINT = 0x00200000;
    int FILE_FLAG_OPEN_NO_RECALL = 0x00100000;

    //
    // These are the generic rights.
    //

    int GENERIC_READ = 0x80000000;
    int GENERIC_WRITE = 0x40000000;
    int GENERIC_EXECUTE = 0x20000000;
    int GENERIC_ALL = 0x10000000;

    //
    // AccessSystemAcl access type
    //

    int ACCESS_SYSTEM_SECURITY             = 0x01000000;

    /**
     * Pages in the region become guard pages. <br>
     * Any attempt to access a guard page causes the system to raise a
     * STATUS_GUARD_PAGE_VIOLATION exception and turn off the guard page status.
     * <br>
     * Guard pages thus act as a one-time access alarm. <br>
     * For more information, see Creating Guard Pages. <br>
     * When an access attempt leads the system to turn off guard page status,
     * the underlying page protection takes over.<br>
     * If a guard page exception occurs during a system service, the service
     * typically returns a failure status indicator. <br>
     * This value cannot be used with PAGE_NOACCESS. This flag is not supported
     * by the CreateFileMapping function.
     *
     * @see <a href=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx">
     *      MSDN</a>
     */
    int PAGE_GUARD                         = 0x100;

    /**
     * Disables all access to the committed region of pages.<br>
     * An attempt to read from, write to, or execute the committed region
     * results in an access violation.<br>
     * This flag is not supported by the CreateFileMapping function.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx">MSDN</a>
     */
    int PAGE_NOACCESS                      = 0x01;

    /**
     * Enables read-only access to the committed region of pages.<br>
     * An attempt to write to the committed region results in an access
     * violation. <br>
     * If Data Execution Prevention is enabled, an attempt to execute code in
     * the committed region results in an access violation.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx">MSDN</a>
     */
    int PAGE_READONLY                      = 0x02;

    /**
     * Enables read-only or read/write access to the committed region of pages. <br>
     * If Data Execution Prevention is enabled, attempting to execute code in
     * the committed region results in an access violation.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx">MSDN</a>
     */
    int PAGE_READWRITE                     = 0x04;

    /**
     * Enables read-only or copy-on-write access to a mapped view of a file
     * mapping object. An attempt to write to a committed copy-on-write page
     * results in a private copy of the page being made for the process. The
     * private page is marked as PAGE_READWRITE, and the change is written to
     * the new page. If Data Execution Prevention is enabled, attempting to
     * execute code in the committed region results in an access violation.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx"> MSDN</a>
     */
    int PAGE_WRITECOPY = 0x08;

    /**
     * Enables execute access to the committed region of pages. An attempt to
     * write to the committed region results in an access violation. This flag
     * is not supported by the CreateFileMapping function.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx">MSDN</a>
     */
    int PAGE_EXECUTE                       = 0x10;

    /**
     * Enables execute or read-only access to the committed region of pages. An
     * attempt to write to the committed region results in an access violation.
     * Windows Server 2003 and Windows XP: This attribute is not supported by
     * the CreateFileMapping function until Windows XP with SP2 and Windows
     * Server 2003 with SP1.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx">MSDN</a>
     */
    int PAGE_EXECUTE_READ                  = 0x20;

    /**
     * Enables execute, read-only, or read/write access to the committed region
     * of pages. Windows Server 2003 and Windows XP: This attribute is not
     * supported by the CreateFileMapping function until Windows XP with SP2 and
     * Windows Server 2003 with SP1.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366786(v=vs.85).aspx">MSDN</a>
     */
    int PAGE_EXECUTE_READWRITE             = 0x40;

    int SECTION_QUERY = 0x0001;
    int SECTION_MAP_WRITE = 0x0002;
    int SECTION_MAP_READ = 0x0004;
    int SECTION_MAP_EXECUTE = 0x0008;
    int SECTION_EXTEND_SIZE = 0x0010;

    int FILE_SHARE_READ = 0x00000001;
    int FILE_SHARE_WRITE = 0x00000002;
    int FILE_SHARE_DELETE = 0x00000004;
    int FILE_TYPE_CHAR = 0x0002;
    int FILE_TYPE_DISK = 0x0001;
    int FILE_TYPE_PIPE = 0x0003;
    int FILE_TYPE_REMOTE = 0x8000;
    int FILE_TYPE_UNKNOWN = 0x0000;
    int FILE_ATTRIBUTE_READONLY = 0x00000001;
    int FILE_ATTRIBUTE_HIDDEN = 0x00000002;
    int FILE_ATTRIBUTE_SYSTEM = 0x00000004;
    int FILE_ATTRIBUTE_DIRECTORY = 0x00000010;
    int FILE_ATTRIBUTE_ARCHIVE = 0x00000020;
    int FILE_ATTRIBUTE_DEVICE = 0x00000040;
    int FILE_ATTRIBUTE_NORMAL = 0x00000080;
    int FILE_ATTRIBUTE_TEMPORARY = 0x00000100;
    int FILE_ATTRIBUTE_SPARSE_FILE = 0x00000200;
    int FILE_ATTRIBUTE_REPARSE_POINT = 0x00000400;
    int FILE_ATTRIBUTE_COMPRESSED = 0x00000800;
    int FILE_ATTRIBUTE_OFFLINE = 0x00001000;
    int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 0x00002000;
    int FILE_ATTRIBUTE_ENCRYPTED = 0x00004000;
    int FILE_ATTRIBUTE_VIRTUAL = 0x00010000;
    int FILE_NOTIFY_CHANGE_FILE_NAME = 0x00000001;
    int FILE_NOTIFY_CHANGE_DIR_NAME = 0x00000002;
    int FILE_NOTIFY_CHANGE_NAME = 0x00000003;
    int FILE_NOTIFY_CHANGE_ATTRIBUTES = 0x00000004;
    int FILE_NOTIFY_CHANGE_SIZE = 0x00000008;
    int FILE_NOTIFY_CHANGE_LAST_WRITE = 0x00000010;
    int FILE_NOTIFY_CHANGE_LAST_ACCESS = 0x00000020;
    int FILE_NOTIFY_CHANGE_CREATION = 0x00000040;
    int FILE_NOTIFY_CHANGE_SECURITY = 0x00000100;
    int FILE_ACTION_ADDED = 0x00000001;
    int FILE_ACTION_REMOVED = 0x00000002;
    int FILE_ACTION_MODIFIED = 0x00000003;
    int FILE_ACTION_RENAMED_OLD_NAME = 0x00000004;
    int FILE_ACTION_RENAMED_NEW_NAME = 0x00000005;
    int FILE_CASE_SENSITIVE_SEARCH = 0x00000001;
    int FILE_CASE_PRESERVED_NAMES = 0x00000002;
    int FILE_UNICODE_ON_DISK = 0x00000004;
    int FILE_PERSISTENT_ACLS = 0x00000008;
    int FILE_FILE_COMPRESSION = 0x00000010;
    int FILE_VOLUME_QUOTAS = 0x00000020;
    int FILE_SUPPORTS_SPARSE_FILES = 0x00000040;
    int FILE_SUPPORTS_REPARSE_POINTS = 0x00000080;
    int FILE_SUPPORTS_REMOTE_STORAGE = 0x00000100;
    int FILE_VOLUME_IS_COMPRESSED = 0x00008000;
    int FILE_SUPPORTS_OBJECT_IDS = 0x00010000;
    int FILE_SUPPORTS_ENCRYPTION = 0x00020000;
    int FILE_NAMED_STREAMS = 0x00040000;
    int FILE_READ_ONLY_VOLUME = 0x00080000;
    int FILE_SEQUENTIAL_WRITE_ONCE = 0x00100000;
    int FILE_SUPPORTS_TRANSACTIONS = 0x00200000;
    // NOTE: These values are not supported until Windows Server 2008 R2 and Windows 7
    int FILE_SUPPORTS_HARD_LINKS = 0x00400000;
    int FILE_SUPPORTS_EXTENDED_ATTRIBUTES = 0x00800000;
    int FILE_SUPPORTS_OPEN_BY_FILE_ID = 0x01000000;
    int FILE_SUPPORTS_USN_JOURNAL = 0x02000000;

    // Reparse point tags
    int IO_REPARSE_TAG_MOUNT_POINT              = 0xA0000003;
    int IO_REPARSE_TAG_HSM                      = 0xC0000004;
    int IO_REPARSE_TAG_HSM2                     = 0x80000006;
    int IO_REPARSE_TAG_SIS                      = 0x80000007;
    int IO_REPARSE_TAG_WIM                      = 0x80000008;
    int IO_REPARSE_TAG_CSV                      = 0x80000009;
    int IO_REPARSE_TAG_DFS                      = 0x8000000A;
    int IO_REPARSE_TAG_SYMLINK                  = 0xA000000C;
    int IO_REPARSE_TAG_DFSR                     = 0x80000012;

    // The controllable aspects of the DefineDosDevice function.
    // see https://msdn.microsoft.com/en-us/library/windows/desktop/aa363904(v=vs.85).aspx
    int DDD_RAW_TARGET_PATH = 0x00000001;
    int DDD_REMOVE_DEFINITION = 0x00000002;
    int DDD_EXACT_MATCH_ON_REMOVE = 0x00000004;
    int DDD_NO_BROADCAST_SYSTEM = 0x00000008;

    int COMPRESSION_FORMAT_NONE          = 0x0000;
    int COMPRESSION_FORMAT_DEFAULT       = 0x0001;
    int COMPRESSION_FORMAT_LZNT1         = 0x0002;
    int COMPRESSION_FORMAT_XPRESS        = 0x0003;
    int COMPRESSION_FORMAT_XPRESS_HUFF   = 0x0004;
    int COMPRESSION_ENGINE_STANDARD      = 0x0000;
    int COMPRESSION_ENGINE_MAXIMUM       = 0x0100;
    int COMPRESSION_ENGINE_HIBER         = 0x0200;

    /**
     * The FILE_NOTIFY_INFORMATION structure describes the changes found by the
     * ReadDirectoryChangesW function.
     *
     * This structure is non-trivial since it is a pattern stamped into a large
     * block of result memory rather than something that stands alone or is used
     * for input.
     */
    public static class FILE_NOTIFY_INFORMATION extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("NextEntryOffset", "Action", "FileNameLength", "FileName");
        public int NextEntryOffset;
        public int Action;
        public int FileNameLength;
        // filename is not nul-terminated, so we can't use a String/WString
        public char[] FileName = new char[1];

        private FILE_NOTIFY_INFORMATION() {
            super();
        }

        public FILE_NOTIFY_INFORMATION(int size) {
            if (size < size()) {
                throw new IllegalArgumentException("Size must greater than "
                        + size() + ", requested " + size);
            }
            allocateMemory(size);
        }

        /**
         * WARNING: this filename may be either the short or long form of the
         * filename.
         * @return filename
         */
        public String getFilename() {
            return new String(FileName, 0, FileNameLength / 2);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        @Override
        public void read() {
            // avoid reading filename until we know how long it is
            FileName = new char[0];
            super.read();
            FileName = getPointer().getCharArray(12, FileNameLength / 2);
        }

        public FILE_NOTIFY_INFORMATION next() {
            if (NextEntryOffset == 0) {
                return null;
            }
            FILE_NOTIFY_INFORMATION next = new FILE_NOTIFY_INFORMATION();
            next.useMemory(getPointer(), NextEntryOffset);
            next.read();
            return next;
        }
    }

    /**
     * Registry options.
     */
    int KEY_QUERY_VALUE = 0x0001;
    int KEY_SET_VALUE = 0x0002;
    int KEY_CREATE_SUB_KEY = 0x0004;
    int KEY_ENUMERATE_SUB_KEYS = 0x0008;
    int KEY_NOTIFY = 0x0010;
    int KEY_CREATE_LINK = 0x0020;
    int KEY_WOW64_32KEY = 0x0200;
    int KEY_WOW64_64KEY = 0x0100;
    int KEY_WOW64_RES = 0x0300;

    int KEY_READ = STANDARD_RIGHTS_READ | KEY_QUERY_VALUE
            | KEY_ENUMERATE_SUB_KEYS | KEY_NOTIFY & (~SYNCHRONIZE);

    int KEY_WRITE = STANDARD_RIGHTS_WRITE | KEY_SET_VALUE | KEY_CREATE_SUB_KEY
            & (~SYNCHRONIZE);

    int KEY_EXECUTE = KEY_READ & (~SYNCHRONIZE);

    int KEY_ALL_ACCESS = STANDARD_RIGHTS_ALL | KEY_QUERY_VALUE | KEY_SET_VALUE
            | KEY_CREATE_SUB_KEY | KEY_ENUMERATE_SUB_KEYS | KEY_NOTIFY
            | KEY_CREATE_LINK & (~SYNCHRONIZE);

    //
    // Open/Create Options
    //

    /**
     * Parameter is reserved.
     */
    int REG_OPTION_RESERVED = 0x00000000;

    /**
     * Key is preserved when system is rebooted.
     */
    int REG_OPTION_NON_VOLATILE = 0x00000000;

    /**
     * Key is not preserved when system is rebooted.
     */
    int REG_OPTION_VOLATILE = 0x00000001;

    /**
     * Created key is a symbolic link.
     */
    int REG_OPTION_CREATE_LINK = 0x00000002;

    /**
     * Open for backup or restore special access rules privilege required.
     */
    int REG_OPTION_BACKUP_RESTORE = 0x00000004;

    /**
     * Open symbolic link.
     */
    int REG_OPTION_OPEN_LINK = 0x00000008;

    int REG_LEGAL_OPTION = REG_OPTION_RESERVED | REG_OPTION_NON_VOLATILE
            | REG_OPTION_VOLATILE | REG_OPTION_CREATE_LINK
            | REG_OPTION_BACKUP_RESTORE | REG_OPTION_OPEN_LINK;

    //
    // Key creation/open disposition
    //

    /**
     * New Registry Key created.
     */
    int REG_CREATED_NEW_KEY = 0x00000001;

    /**
     * Existing Key opened.
     */
    int REG_OPENED_EXISTING_KEY = 0x00000002;

    int REG_STANDARD_FORMAT = 1;
    int REG_LATEST_FORMAT = 2;
    int REG_NO_COMPRESSION = 4;

    //
    // Key restore & hive load flags
    //

    /**
     * Restore whole hive volatile.
     */
    int REG_WHOLE_HIVE_VOLATILE = 0x00000001;

    /**
     * Unwind changes to last flush.
     */
    int REG_REFRESH_HIVE = 0x00000002;

    /**
     * Never lazy flush this hive.
     */
    int REG_NO_LAZY_FLUSH = 0x00000004;

    /**
     * Force the restore process even when we have open handles on subkeys.
     */
    int REG_FORCE_RESTORE = 0x00000008;

    /**
     * Loads the hive visible to the calling process.
     */
    int REG_APP_HIVE = 0x00000010;

    /**
     * Hive cannot be mounted by any other process while in use.
     */
    int REG_PROCESS_PRIVATE = 0x00000020;

    /**
     * Starts Hive Journal.
     */
    int REG_START_JOURNAL = 0x00000040;

    /**
     * Grow hive file in exact 4k increments.
     */
    int REG_HIVE_EXACT_FILE_GROWTH = 0x00000080;

    /**
     * No RM is started for this hive = no transactions.
     */
    int REG_HIVE_NO_RM = 0x00000100;

    /**
     * Legacy single logging is used for this hive.
     */
    int REG_HIVE_SINGLE_LOG = 0x00000200;

    //
    // Unload Flags
    //

    int REG_FORCE_UNLOAD = 1;

    //
    // Notify filter values
    //

    int REG_NOTIFY_CHANGE_NAME = 0x00000001;
    int REG_NOTIFY_CHANGE_ATTRIBUTES = 0x00000002;
    int REG_NOTIFY_CHANGE_LAST_SET = 0x00000004;
    int REG_NOTIFY_CHANGE_SECURITY = 0x00000008;

    int REG_LEGAL_CHANGE_FILTER = REG_NOTIFY_CHANGE_NAME
            | REG_NOTIFY_CHANGE_ATTRIBUTES | REG_NOTIFY_CHANGE_LAST_SET
            | REG_NOTIFY_CHANGE_SECURITY;

    //
    // Predefined Value Types.
    //

    /**
     * No value type.
     */
    int REG_NONE = 0;

    /**
     * Unicode null-terminated string.
     */
    int REG_SZ = 1;

    /**
     * Unicode null-terminated string with environment variable references.
     */
    int REG_EXPAND_SZ = 2;

    /**
     * Free-formed binary.
     */
    int REG_BINARY = 3;

    /**
     * 32-bit number.
     */
    int REG_DWORD = 4;

    /**
     * 32-bit number, same as REG_DWORD.
     */
    int REG_DWORD_LITTLE_ENDIAN = 4;

    /**
     * 32-bit number.
     */
    int REG_DWORD_BIG_ENDIAN = 5;

    /**
     * Symbolic link (unicode).
     */
    int REG_LINK = 6;

    /**
     * Multiple unicode strings.
     */
    int REG_MULTI_SZ = 7;

    /**
     * Resource list in the resource map.
     */
    int REG_RESOURCE_LIST = 8;

    /**
     * Resource list in the hardware description.
     */
    int REG_FULL_RESOURCE_DESCRIPTOR = 9;

    /**
     *
     */
    int REG_RESOURCE_REQUIREMENTS_LIST = 10;

    /**
     * 64-bit number.
     */
    int REG_QWORD = 11;

    /**
     * 64-bit number, same as REG_QWORD.
     */
    int REG_QWORD_LITTLE_ENDIAN = 11;

    /**
     * A 64-bit value that is guaranteed to be unique on the operating system
     * that generated it until the system is restarted.
     */
    public static class LUID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("LowPart", "HighPart");
        public int LowPart;
        public int HighPart;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * A 64-bit integer;
     */
    public static class LARGE_INTEGER extends Structure implements Comparable<LARGE_INTEGER> {
        public static class ByReference extends LARGE_INTEGER implements
                Structure.ByReference {
        }

        public static class LowHigh extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("LowPart", "HighPart");
            public DWORD LowPart;
            public DWORD HighPart;

            public LowHigh() {
                super();
            }

            public LowHigh(long value) {
                this(new DWORD(value & 0xFFFFFFFFL),  new DWORD((value >> 32) & 0xFFFFFFFFL));
            }

            public LowHigh(DWORD low, DWORD high) {
                LowPart = low;
                HighPart = high;
            }

            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }

            public long longValue() {
                long loValue = LowPart.longValue();
                long hiValue = HighPart.longValue();
                return ((hiValue << 32) & 0xFFFFFFFF00000000L) | (loValue & 0xFFFFFFFFL);
            }

            @Override
            public String toString() {
                if ((LowPart == null) || (HighPart == null)) {
                    return "null";
                } else {
                    return Long.toString(longValue());
                }
            }
        }

        public static class UNION extends Union {
            public LowHigh lh;
            public long value;

            public UNION() {
                super();
            }

            public UNION(long value) {
                this.value = value;
                this.lh = new LowHigh(value);
            }

            public long longValue() {
                return value;
            }

            @Override
            public String toString() {
                return Long.toString(longValue());
            }
        }

        public UNION u;

        @Override
        protected List<String> getFieldOrder() {
            return Collections.singletonList("u");
        }

        public LARGE_INTEGER() {
            super();
        }

        public LARGE_INTEGER(long value) {
            this.u = new UNION(value);
        }

        /**
         * Low DWORD.
         *
         * @return Low DWORD value
         */
        public DWORD getLow() {
            return u.lh.LowPart;
        }

        /**
         * High DWORD.
         *
         * @return High DWORD value
         */
        public DWORD getHigh() {
            return u.lh.HighPart;
        }

        /**
         * 64-bit value.
         *
         * @return The 64-bit value.
         */
        public long getValue() {
            return u.value;
        }

        @Override
        public int compareTo(LARGE_INTEGER other) {
            return compare(this, other);
        }

        @Override
        public String toString() {
            return (u == null) ? "null" : Long.toString(getValue());
        }

        /**
         * Compares 2 LARGE_INTEGER values -  - <B>Note:</B> a {@code null}
         * value is considered <U>greater</U> than any non-{@code null} one
         * (i.e., {@code null} values are &quot;pushed&quot; to the end
         * of a sorted array / list of values)
         *
         * @param v1 The 1st value
         * @param v2 The 2nd value
         * @return 0 if values are equal (including if <U>both</U> are {@code null},
         * negative if 1st value less than 2nd one, positive otherwise. <B>Note:</B>
         * the comparison uses the {@link #getValue()}.
         * @see IntegerType#compare(long, long)
         */
        public static int compare(LARGE_INTEGER v1, LARGE_INTEGER v2) {
            if (v1 == v2) {
                return 0;
            } else if (v1 == null) {
                return 1;   // v2 cannot be null or v1 == v2 would hold
            } else if (v2 == null) {
                return (-1);
            } else {
                return IntegerType.compare(v1.getValue(), v2.getValue());
            }
        }

        /**
         * Compares a LARGE_INTEGER value with a {@code long} one. <B>Note:</B> if
         * the LARGE_INTEGER value is {@code null} then it is consider <U>greater</U>
         * than any {@code long} value.
         *
         * @param v1 The {@link LARGE_INTEGER} value
         * @param v2 The {@code long} value
         * @return 0 if values are equal, negative if 1st value less than 2nd one,
         * positive otherwise. <B>Note:</B> the comparison uses the {@link #getValue()}.
         * @see IntegerType#compare(long, long)
         */
        public static int compare(LARGE_INTEGER v1, long v2) {
            if (v1 == null) {
                return 1;
            } else {
                return IntegerType.compare(v1.getValue(), v2);
            }
        }
    }

    /**
     * Handle to an object.
     */
    public static class HANDLE extends PointerType {
        private boolean immutable;

        public HANDLE() {
        }

        public HANDLE(Pointer p) {
            setPointer(p);
            immutable = true;
        }

        /** Override to the appropriate object for INVALID_HANDLE_VALUE. */
        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            Object o = super.fromNative(nativeValue, context);
            if (WinBase.INVALID_HANDLE_VALUE.equals(o)) {
                return WinBase.INVALID_HANDLE_VALUE;
            }
            return o;
        }

        @Override
        public void setPointer(Pointer p) {
            if (immutable) {
                throw new UnsupportedOperationException("immutable reference");
            }

            super.setPointer(p);
        }

        @Override
        public String toString() {
            return String.valueOf(getPointer());
        }
    }

    /**
     * LPHANDLE
     */
    public static class HANDLEByReference extends ByReference {
        public HANDLEByReference() {
            this(null);
        }

        public HANDLEByReference(HANDLE h) {
            super(Native.POINTER_SIZE);
            setValue(h);
        }

        public void setValue(HANDLE h) {
            getPointer().setPointer(0, h != null ? h.getPointer() : null);
        }

        public HANDLE getValue() {
            Pointer p = getPointer().getPointer(0);
            if (p == null) {
                return null;
            }
            if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) {
                return WinBase.INVALID_HANDLE_VALUE;
            }
            HANDLE h = new HANDLE();
            h.setPointer(p);
            return h;
        }
    }

    /**
     * Return code used by interfaces. It is zero upon success and nonzero to
     * represent an error code or status information.
     */
    class HRESULT extends NativeLong {
        public HRESULT() {
        }

        public HRESULT(int value) {
            super(value);
        }
    }

    /**
     * The WELL_KNOWN_SID_TYPE enumeration type is a list of commonly used
     * security identifiers (SIDs). Programs can pass these values to the
     * CreateWellKnownSid function to create a SID from this list.
     */
    public abstract class WELL_KNOWN_SID_TYPE {
        /**
         * Indicates a null SID.
         */
        public static final int WinNullSid = 0;

        /**
         * Indicates a SID that matches everyone.
         */
        public static final int WinWorldSid = 1;

        /**
         * Indicates a local SID.
         */
        public static final int WinLocalSid = 2;

        /**
         * Indicates a SID that matches the owner or creator of an object.
         */
        public static final int WinCreatorOwnerSid = 3;

        /**
         * Indicates a SID that matches the creator group of an object.
         */
        public static final int WinCreatorGroupSid = 4;

        /**
         * Indicates a creator owner server SID.
         */
        public static final int WinCreatorOwnerServerSid = 5;

        /**
         * Indicates a creator group server SID.
         */
        public static final int WinCreatorGroupServerSid = 6;

        /**
         * Indicates a SID for the Windows NT authority.
         */
        public static final int WinNtAuthoritySid = 7;

        /**
         * Indicates a SID for a dial-up account.
         */
        public static final int WinDialupSid = 8;

        /**
         * Indicates a SID for a network account. This SID is added to the
         * process of a token when it logs on across a network. The
         * corresponding logon type is LOGON32_LOGON_NETWORK.
         */
        public static final int WinNetworkSid = 9;

        /**
         * Indicates a SID for a batch process. This SID is added to the process
         * of a token when it logs on as a batch job. The corresponding logon
         * type is LOGON32_LOGON_BATCH.
         */
        public static final int WinBatchSid = 10;

        /**
         * Indicates a SID for an interactive account. This SID is added to the
         * process of a token when it logs on interactively. The corresponding
         * logon type is LOGON32_LOGON_INTERACTIVE.
         */
        public static final int WinInteractiveSid = 11;

        /**
         * Indicates a SID for a service. This SID is added to the process of a
         * token when it logs on as a service. The corresponding logon type is
         * LOGON32_LOGON_bSERVICE.
         */
        public static final int WinServiceSid = 12;

        /**
         * Indicates a SID for the anonymous account.
         */
        public static final int WinAnonymousSid = 13;

        /**
         * Indicates a proxy SID.
         */
        public static final int WinProxySid = 14;

        /**
         * Indicates a SID for an enterprise controller.
         */
        public static final int WinEnterpriseControllersSid = 15;

        /**
         * Indicates a SID for self.
         */
        public static final int WinSelfSid = 16;

        /**
         * Indicates a SID that matches any authenticated user.
         */
        public static final int WinAuthenticatedUserSid = 17;

        /**
         * Indicates a SID for restricted code.
         */
        public static final int WinRestrictedCodeSid = 18;

        /**
         * Indicates a SID that matches a terminal server account.
         */
        public static final int WinTerminalServerSid = 19;

        /**
         * Indicates a SID that matches remote logons.
         */
        public static final int WinRemoteLogonIdSid = 20;

        /**
         * Indicates a SID that matches logon IDs.
         */
        public static final int WinLogonIdsSid = 21;

        /**
         * Indicates a SID that matches the local system.
         */
        public static final int WinLocalSystemSid = 22;

        /**
         * Indicates a SID that matches a local service.
         */
        public static final int WinLocalServiceSid = 23;

        /**
         * Indicates a SID that matches a network service.
         */
        public static final int WinNetworkServiceSid = 24;

        /**
         * Indicates a SID that matches the domain account.
         */
        public static final int WinBuiltinDomainSid = 25;

        /**
         * Indicates a SID that matches the administrator account.
         */
        public static final int WinBuiltinAdministratorsSid = 26;

        /**
         * Indicates a SID that matches built-in user accounts.
         */
        public static final int WinBuiltinUsersSid = 27;

        /**
         * Indicates a SID that matches the guest account.
         */
        public static final int WinBuiltinGuestsSid = 28;

        /**
         * Indicates a SID that matches the power users group.
         */
        public static final int WinBuiltinPowerUsersSid = 29;

        /**
         * Indicates a SID that matches the account operators account.
         */
        public static final int WinBuiltinAccountOperatorsSid = 30;

        /**
         * Indicates a SID that matches the system operators group.
         */
        public static final int WinBuiltinSystemOperatorsSid = 31;

        /**
         * Indicates a SID that matches the print operators group.
         */
        public static final int WinBuiltinPrintOperatorsSid = 32;

        /**
         * Indicates a SID that matches the backup operators group.
         */
        public static final int WinBuiltinBackupOperatorsSid = 33;

        /**
         * Indicates a SID that matches the replicator account.
         */
        public static final int WinBuiltinReplicatorSid = 34;

        /**
         * Indicates a SID that matches pre-Windows 2000 compatible accounts.
         */
        public static final int WinBuiltinPreWindows2000CompatibleAccessSid = 35;

        /**
         * Indicates a SID that matches remote desktop users.
         */
        public static final int WinBuiltinRemoteDesktopUsersSid = 36;

        /**
         * Indicates a SID that matches the network operators group.
         */
        public static final int WinBuiltinNetworkConfigurationOperatorsSid = 37;

        /**
         * Indicates a SID that matches the account administrators group.
         */
        public static final int WinAccountAdministratorSid = 38;

        /**
         * Indicates a SID that matches the account guest group.
         */
        public static final int WinAccountGuestSid = 39;

        /**
         * Indicates a SID that matches account Kerberos target group.
         */
        public static final int WinAccountKrbtgtSid = 40;

        /**
         * Indicates a SID that matches the account domain administrator group.
         */
        public static final int WinAccountDomainAdminsSid = 41;

        /**
         * Indicates a SID that matches the account domain users group.
         */
        public static final int WinAccountDomainUsersSid = 42;

        /**
         * Indicates a SID that matches the account domain guests group.
         */
        public static final int WinAccountDomainGuestsSid = 43;

        /**
         * Indicates a SID that matches the account computer group.
         */
        public static final int WinAccountComputersSid = 44;

        /**
         * Indicates a SID that matches the account controller group.
         */
        public static final int WinAccountControllersSid = 45;

        /**
         * Indicates a SID that matches the certificate administrators group.
         */
        public static final int WinAccountCertAdminsSid = 46;

        /**
         * Indicates a SID that matches the schema administrators group.
         */
        public static final int WinAccountSchemaAdminsSid = 47;

        /**
         * Indicates a SID that matches the enterprise administrators group.
         */
        public static final int WinAccountEnterpriseAdminsSid = 48;

        /**
         * Indicates a SID that matches the policy administrators group.
         */
        public static final int WinAccountPolicyAdminsSid = 49;

        /**
         * Indicates a SID that matches the RAS and IAS server account.
         */
        public static final int WinAccountRasAndIasServersSid = 50;

        /**
         * Indicates a SID present when the Microsoft NTLM authentication
         * package authenticated the client.
         */
        public static final int WinNTLMAuthenticationSid = 51;

        /**
         * Indicates a SID present when the Microsoft Digest authentication
         * package authenticated the client.
         */
        public static final int WinDigestAuthenticationSid = 52;

        /**
         * Indicates a SID present when the Secure Channel (SSL/TLS)
         * authentication package authenticated the client.
         */
        public static final int WinSChannelAuthenticationSid = 53;

        /**
         * Indicates a SID present when the user authenticated from within the
         * forest or across a trust that does not have the selective
         * authentication option enabled. If this SID is present, then
         * WinOtherOrganizationSid cannot be present.
         */
        public static final int WinThisOrganizationSid = 54;

        /**
         * Indicates a SID present when the user authenticated across a forest
         * with the selective authentication option enabled. If this SID is
         * present, then WinThisOrganizationSid cannot be present.
         */
        public static final int WinOtherOrganizationSid = 55;

        /**
         * Indicates a SID that allows a user to create incoming forest trusts.
         * It is added to the token of users who are a member of the Incoming
         * Forest Trust Builders built-in group in the root domain of the
         * forest.
         */
        public static final int WinBuiltinIncomingForestTrustBuildersSid = 56;

        /**
         * Indicates a SID that matches the performance monitor user group.
         */
        public static final int WinBuiltinPerfMonitoringUsersSid = 57;

        /**
         * Indicates a SID that matches the performance log user group.
         */
        public static final int WinBuiltinPerfLoggingUsersSid = 58;

        /**
         * Indicates a SID that matches the Windows Authorization Access group.
         */
        public static final int WinBuiltinAuthorizationAccessSid = 59;

        /**
         * Indicates a SID is present in a server that can issue Terminal Server
         * licenses.
         */
        public static final int WinBuiltinTerminalServerLicenseServersSid = 60;

        /**
	 *
	 */
        public static final int WinBuiltinDCOMUsersSid = 61;

        /**
	 *
	 */
        public static final int WinBuiltinIUsersSid = 62;

        /**
	 *
	 */
        public static final int WinIUserSid = 63;

        /**
	 *
	 */
        public static final int WinBuiltinCryptoOperatorsSid = 64;

        /**
	 *
	 */
        public static final int WinUntrustedLabelSid = 65;

        /**
	 *
	 */
        public static final int WinLowLabelSid = 66;

        /**
	 *
	 */
        public static final int WinMediumLabelSid = 67;

        /**
	 *
	 */
        public static final int WinHighLabelSid = 68;

        /**
	 *
	 */
        public static final int WinSystemLabelSid = 69;

        /**
	 *
	 */
        public static final int WinWriteRestrictedCodeSid = 70;

        /**
	 *
	 */
        public static final int WinCreatorOwnerRightsSid = 71;

        /**
	 *
	 */
        public static final int WinCacheablePrincipalsGroupSid = 72;

        /**
	 *
	 */
        public static final int WinNonCacheablePrincipalsGroupSid = 73;

        /**
	 *
	 */
        public static final int WinEnterpriseReadonlyControllersSid = 74;

        /**
         * Indicates a SID that matches a read-only enterprise domain
         * controller.
         */
        public static final int WinAccountReadonlyControllersSid = 75;

        /**
         * Indicates a SID that matches the built-in DCOM certification services
         * access group.
         */
        public static final int WinBuiltinEventLogReadersGroup = 76;
    }

    /**
     * Current SID revision level.
     */
    int SID_REVISION = 1;
    int SID_MAX_SUB_AUTHORITIES = 15;
    int SID_RECOMMENDED_SUB_AUTHORITIES = 1;

    /**
     * Maximum bytes used by a SID. (sizeof(SID) - sizeof(DWORD) +
     * (SID_MAX_SUB_AUTHORITIES * sizeof(DWORD)))
     */
    int SECURITY_MAX_SID_SIZE = 68;

    /**
     * The OSVERSIONINFO data structure contains operating system version
     * information. The information includes major and minor version numbers, a
     * build number, a platform identifier, and descriptive text about the
     * operating system. This structure is used with the GetVersionEx function.
     */
    public static class OSVERSIONINFO extends Structure {
        public static final List<String> FIELDS = createFieldsOrder(
                "dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion");

        /**
         * Size of this data structure, in bytes. Set this member to
         * sizeof(OSVERSIONINFO) before calling the GetVersionEx function.
         */
        public DWORD dwOSVersionInfoSize;

        /**
         * Major version number of the operating system.
         */
        public DWORD dwMajorVersion;

        /**
         * Minor version number of the operating system.
         */
        public DWORD dwMinorVersion;

        /**
         * Build number of the operating system.
         */
        public DWORD dwBuildNumber;

        /**
         * Operating system platform.
         */
        public DWORD dwPlatformId;

        /**
         * Pointer to a null-terminated string, such as "Service Pack 3", that
         * indicates the latest Service Pack installed on the system.
         */
        public char szCSDVersion[];

        public OSVERSIONINFO() {
            szCSDVersion = new char[128];
            dwOSVersionInfoSize = new DWORD(size()); // sizeof(OSVERSIONINFO)
        }

        public OSVERSIONINFO(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Contains operating system version information. The information includes
     * major and minor version numbers, a build number, a platform identifier,
     * and information about product suites and the latest Service Pack
     * installed on the system.
     */
    public static class OSVERSIONINFOEX extends Structure {
        public static final List<String> FIELDS = createFieldsOrder(
                "dwOSVersionInfoSize",
                "dwMajorVersion", "dwMinorVersion", "dwBuildNumber",
                "dwPlatformId",
                "szCSDVersion",
                "wServicePackMajor", "wServicePackMinor",
                "wSuiteMask", "wProductType", "wReserved");

        /**
         * The size of this data structure, in bytes.
         */
        public DWORD dwOSVersionInfoSize;

        /**
         * The major version number of the operating system.
         */
        public DWORD dwMajorVersion;

        /**
         * The minor version number of the operating system.
         */
        public DWORD dwMinorVersion;

        /**
         * The build number of the operating system.
         */
        public DWORD dwBuildNumber;

        /**
         * The operating system platform. This member can be
         * VER_PLATFORM_WIN32_NT.
         */
        public DWORD dwPlatformId;

        /**
         * A null-terminated string, such as "Service Pack 3", that indicates
         * the latest Service Pack installed on the system. If no Service Pack
         * has been installed, the string is empty.
         */
        public char szCSDVersion[];

        /**
         * The major version number of the latest Service Pack installed on the
         * system. For example, for Service Pack 3, the major version number is
         * 3. If no Service Pack has been installed, the value is zero.
         */
        public WORD wServicePackMajor;

        /**
         * The minor version number of the latest Service Pack installed on the
         * system. For example, for Service Pack 3, the minor version number is
         * 0.
         */
        public WORD wServicePackMinor;

        /**
         * A bit mask that identifies the product suites available on the
         * system.
         */
        public WORD wSuiteMask;

        /**
         * Any additional information about the system.
         */
        public byte wProductType;

        /**
         * Reserved for future use.
         */
        public byte wReserved;

        public OSVERSIONINFOEX() {
            szCSDVersion = new char[128];
            dwOSVersionInfoSize = new DWORD(size()); // sizeof(OSVERSIONINFOEX)
        }

        public OSVERSIONINFOEX(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        /**
         * @return The major version number of the operating system.
         */
        public int getMajor() {
            return dwMajorVersion.intValue();
        }

        /**
         * @return The minor version number of the operating system.
         */
        public int getMinor() {
            return dwMinorVersion.intValue();
        }

        /**
         * @return The build number of the operating system.
         */
        public int getBuildNumber() {
            return dwBuildNumber.intValue();
        }

        /**
         * @return  The operating system platform. This member can be VER_PLATFORM_WIN32_NT.
         */
        public int getPlatformId() {
            return dwPlatformId.intValue();
        }

        /**
         * @return String, such as "Service Pack 3", that indicates the latest
         *         Service Pack installed on the system.<br>
         *         If no Service Pack has been installed, the string is empty.
         */
        public String getServicePack() {
            return Native.toString(szCSDVersion);
        }

        /**
         * @return A bit mask that identifies the product suites available on the system.
         */
        public int getSuiteMask() {
            return wSuiteMask.intValue();
        }

        /**
         * @return Any additional information about the system.
         */
        public byte getProductType() {
            return wProductType;
        }
    }

    int VER_EQUAL = 1;
    int VER_GREATER = 2;
    int VER_GREATER_EQUAL = 3;
    int VER_LESS = 4;
    int VER_LESS_EQUAL = 5;
    int VER_AND = 6;
    int VER_OR = 7;

    int VER_CONDITION_MASK = 7;
    int VER_NUM_BITS_PER_CONDITION_MASK = 3;

    int VER_MINORVERSION = 0x0000001;
    int VER_MAJORVERSION = 0x0000002;
    int VER_BUILDNUMBER = 0x0000004;
    int VER_PLATFORMID = 0x0000008;
    int VER_SERVICEPACKMINOR = 0x0000010;
    int VER_SERVICEPACKMAJOR = 0x0000020;
    int VER_SUITENAME = 0x0000040;
    int VER_PRODUCT_TYPE = 0x0000080;

    int VER_NT_WORKSTATION = 0x0000001;
    int VER_NT_DOMAIN_CONTROLLER = 0x0000002;
    int VER_NT_SERVER = 0x0000003;

    int VER_PLATFORM_WIN32s = 0;
    int VER_PLATFORM_WIN32_WINDOWS = 1;
    int VER_PLATFORM_WIN32_NT = 2;

    /**
     * Read the records sequentially. If this is the first read operation, the
     * EVENTLOG_FORWARDS_READ EVENTLOG_BACKWARDS_READ flags determines which
     * record is read first.
     */
    int EVENTLOG_SEQUENTIAL_READ = 0x0001;

    /**
     * Begin reading from the record specified in the dwRecordOffset parameter.
     * This option may not work with large log files if the function cannot
     * determine the log file's size. For details, see Knowledge Base article,
     * 177199.
     */
    int EVENTLOG_SEEK_READ = 0x0002;

    /**
     * The log is read in chronological order (oldest to newest). The default.
     */
    int EVENTLOG_FORWARDS_READ = 0x0004;

    /**
     * The log is read in reverse chronological order (newest to oldest).
     */
    int EVENTLOG_BACKWARDS_READ = 0x0008;

    /**
     * Information event
     */
    int EVENTLOG_SUCCESS = 0x0000;

    /**
     * Error event
     */
    int EVENTLOG_ERROR_TYPE = 0x0001;

    /**
     * Warning event
     */
    int EVENTLOG_WARNING_TYPE = 0x0002;

    /**
     * Information event
     */
    int EVENTLOG_INFORMATION_TYPE = 0x0004;

    /**
     * Success Audit event
     */
    int EVENTLOG_AUDIT_SUCCESS = 0x0008;

    /**
     * Failure Audit event
     */
    int EVENTLOG_AUDIT_FAILURE = 0x0010;

    /**
     * The EVENTLOGRECORD structure contains information about an event record
     * returned by the ReadEventLog function.
     */
    public static class EVENTLOGRECORD extends Structure {
        public static final List<String> FIELDS = createFieldsOrder(
                "Length", "Reserved", "RecordNumber", "TimeGenerated", "TimeWritten",
                "EventID", "EventType", "NumStrings", "EventCategory", "ReservedFlags",
                "ClosingRecordNumber", "StringOffset", "UserSidLength", "UserSidOffset",
                "DataLength", "DataOffset");

        /**
         * Size of this event record, in bytes. Note that this value is stored
         * at both ends of the entry to ease moving forward or backward through
         * the log. The length includes any pad bytes inserted at the end of the
         * record for DWORD alignment.
         */
        public DWORD Length;

        /**
         * Reserved.
         */
        public DWORD Reserved;

        /**
         * Record number of the record. This value can be used with the
         * EVENTLOG_SEEK_READ flag in the ReadEventLog function to begin reading
         * at a specified record.
         */
        public DWORD RecordNumber;

        /**
         * Time at which this entry was submitted. This time is measured in the
         * number of seconds elapsed since 00:00:00 January 1, 1970, Universal
         * Coordinated Time.
         */
        public DWORD TimeGenerated;

        /**
         * Time at which this entry was received by the service to be written to
         * the log. This time is measured in the number of seconds elapsed since
         * 00:00:00 January 1, 1970, Universal Coordinated Time.
         */
        public DWORD TimeWritten;

        /**
         * Event identifier. The value is specific to the event source for the
         * event, and is used with source name to locate a description string in
         * the message file for the event source.
         */
        public DWORD EventID;

        /**
         * Type of event.
         */
        public WORD EventType;

        /**
         * Number of strings present in the log (at the position indicated by
         * StringOffset). These strings are merged into the message before it is
         * displayed to the user.
         */
        public WORD NumStrings;

        /**
         * Category for this event. The meaning of this value depends on the
         * event source.
         */
        public WORD EventCategory;

        /**
         * Reserved.
         */
        public WORD ReservedFlags;

        /**
         * Reserved.
         */
        public DWORD ClosingRecordNumber;

        /**
         * Offset of the description strings within this event log record.
         */
        public DWORD StringOffset;

        /**
         * Size of the UserSid member, in bytes. This value can be zero if no
         * security identifier was provided.
         */
        public DWORD UserSidLength;

        /**
         * Offset of the security identifier (SID) within this event log record.
         * To obtain the user name for this SID, use the LookupAccountSid
         * function.
         */
        public DWORD UserSidOffset;

        /**
         * Size of the event-specific data (at the position indicated by
         * DataOffset), in bytes.
         */
        public DWORD DataLength;

        /**
         * Offset of the event-specific information within this event log
         * record, in bytes. This information could be something specific (a
         * disk driver might log the number of retries, for example), followed
         * by binary information specific to the event being logged and to the
         * source that generated the entry.
         */
        public DWORD DataOffset;

        public EVENTLOGRECORD() {
            super();
        }

        public EVENTLOGRECORD(Pointer p) {
            super(p);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    //
    // Service Types (Bit Mask)
    //
    int SERVICE_KERNEL_DRIVER = 0x00000001;
    int SERVICE_FILE_SYSTEM_DRIVER = 0x00000002;
    int SERVICE_ADAPTER = 0x00000004;
    int SERVICE_RECOGNIZER_DRIVER = 0x00000008;
    int SERVICE_DRIVER = SERVICE_KERNEL_DRIVER | SERVICE_FILE_SYSTEM_DRIVER
            | SERVICE_RECOGNIZER_DRIVER;
    int SERVICE_WIN32_OWN_PROCESS = 0x00000010;
    int SERVICE_WIN32_SHARE_PROCESS = 0x00000020;
    int SERVICE_WIN32 = SERVICE_WIN32_OWN_PROCESS | SERVICE_WIN32_SHARE_PROCESS;
    int SERVICE_INTERACTIVE_PROCESS = 0x00000100;
    int SERVICE_TYPE_ALL = SERVICE_WIN32 | SERVICE_ADAPTER | SERVICE_DRIVER
            | SERVICE_INTERACTIVE_PROCESS;
    
    //
    // Start Type
    //

    int SERVICE_BOOT_START   = 0x00000000;
    int SERVICE_SYSTEM_START = 0x00000001;
    int SERVICE_AUTO_START   = 0x00000002;
    int SERVICE_DEMAND_START = 0x00000003;
    int SERVICE_DISABLED     = 0x00000004;

    //
    // Error control type
    //
    int SERVICE_ERROR_IGNORE   = 0x00000000;
    int SERVICE_ERROR_NORMAL   = 0x00000001;
    int SERVICE_ERROR_SEVERE   = 0x00000002;
    int SERVICE_ERROR_CRITICAL = 0x00000003;
    
    int STATUS_PENDING = 0x00000103;

    // Privilege Constants
    String SE_CREATE_TOKEN_NAME = "SeCreateTokenPrivilege";
    String SE_ASSIGNPRIMARYTOKEN_NAME = "SeAssignPrimaryTokenPrivilege";
    String SE_LOCK_MEMORY_NAME = "SeLockMemoryPrivilege";
    String SE_INCREASE_QUOTA_NAME = "SeIncreaseQuotaPrivilege";
    String SE_UNSOLICITED_INPUT_NAME = "SeUnsolicitedInputPrivilege";
    String SE_MACHINE_ACCOUNT_NAME = "SeMachineAccountPrivilege";
    String SE_TCB_NAME = "SeTcbPrivilege";
    String SE_SECURITY_NAME = "SeSecurityPrivilege";
    String SE_TAKE_OWNERSHIP_NAME = "SeTakeOwnershipPrivilege";
    String SE_LOAD_DRIVER_NAME = "SeLoadDriverPrivilege";
    String SE_SYSTEM_PROFILE_NAME = "SeSystemProfilePrivilege";
    String SE_SYSTEMTIME_NAME = "SeSystemtimePrivilege";
    String SE_PROF_SINGLE_PROCESS_NAME = "SeProfileSingleProcessPrivilege";
    String SE_INC_BASE_PRIORITY_NAME = "SeIncreaseBasePriorityPrivilege";
    String SE_CREATE_PAGEFILE_NAME = "SeCreatePagefilePrivilege";
    String SE_CREATE_PERMANENT_NAME = "SeCreatePermanentPrivilege";
    String SE_BACKUP_NAME = "SeBackupPrivilege";
    String SE_RESTORE_NAME = "SeRestorePrivilege";
    String SE_SHUTDOWN_NAME = "SeShutdownPrivilege";
    String SE_DEBUG_NAME = "SeDebugPrivilege";
    String SE_AUDIT_NAME = "SeAuditPrivilege";
    String SE_SYSTEM_ENVIRONMENT_NAME = "SeSystemEnvironmentPrivilege";
    String SE_CHANGE_NOTIFY_NAME = "SeChangeNotifyPrivilege";
    String SE_REMOTE_SHUTDOWN_NAME = "SeRemoteShutdownPrivilege";
    String SE_UNDOCK_NAME = "SeUndockPrivilege";
    String SE_SYNC_AGENT_NAME = "SeSyncAgentPrivilege";
    String SE_ENABLE_DELEGATION_NAME = "SeEnableDelegationPrivilege";
    String SE_MANAGE_VOLUME_NAME = "SeManageVolumePrivilege";
    String SE_IMPERSONATE_NAME = "SeImpersonatePrivilege";
    String SE_CREATE_GLOBAL_NAME = "SeCreateGlobalPrivilege";

    int SE_PRIVILEGE_ENABLED_BY_DEFAULT = 0x00000001;
    int SE_PRIVILEGE_ENABLED = 0x00000002;
    int SE_PRIVILEGE_REMOVED = 0X00000004;
    int SE_PRIVILEGE_USED_FOR_ACCESS = 0x80000000;

	/** Required to create a process. */
	int PROCESS_CREATE_PROCESS = 0x0080;

	/** Required to create a thread. */
	int PROCESS_CREATE_THREAD = 0x0002;

	/**
	 * Required to duplicate a handle using
	 * {@link Kernel32#DuplicateHandle}
	 * .
	 */
	int PROCESS_DUP_HANDLE = 0x0040;

    /**
     * All possible access rights for a process object. Windows Server 2003 and
     * Windows XP: The size of the PROCESS_ALL_ACCESS flag increased on Windows
     * Server 2008 and Windows Vista. <br>
     * If an application compiled for Windows Server 2008 and Windows Vista is
     * run on Windows Server 2003 or Windows XP, the PROCESS_ALL_ACCESS flag is
     * too large and the function specifying this flag fails with
     * ERROR_ACCESS_DENIED.<br>
     * To avoid this problem, specify the minimum set of access rights required
     * for the operation.<br>
     * If PROCESS_ALL_ACCESS must be used, set _WIN32_WINNT to the minimum
     * operating system targeted by your application (for example, #define
     * _WIN32_WINNT _WIN32_WINNT_WINXP).<br>
     * For more information, see Using the Windows Headers.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms684880(v=VS.85).aspx">MSDN</a>
     */
    int PROCESS_ALL_ACCESS = WinNT.PROCESS_CREATE_PROCESS
            | WinNT.PROCESS_CREATE_THREAD
            | WinNT.PROCESS_DUP_HANDLE
            | WinNT.PROCESS_QUERY_INFORMATION
            | WinNT.PROCESS_QUERY_LIMITED_INFORMATION
            | WinNT.PROCESS_SET_INFORMATION
            | WinNT.PROCESS_SET_QUOTA
            | WinNT.PROCESS_SUSPEND_RESUME
            | WinNT.PROCESS_SYNCHRONIZE
            | WinNT.PROCESS_TERMINATE
            | WinNT.PROCESS_VM_OPERATION
            | WinNT.PROCESS_VM_READ
            | WinNT.PROCESS_VM_WRITE
            | WinNT.DELETE
            | WinNT.READ_CONTROL
            | WinNT.WRITE_DAC
            | WinNT.WRITE_OWNER
            | WinNT.SYNCHRONIZE;

	/**
	 * Required to retrieve certain information about a process, such as its
	 * token, exit code, and priority class (see
	 * {@link Advapi32#OpenProcessToken}).
	 */
	int PROCESS_QUERY_INFORMATION = 0x0400;

	/**
	 * Required to retrieve certain information about a process (see
	 * {@link Kernel32#GetExitCodeProcess}
	 * , {@code Kernel32#GetPriorityClass}, {@code Kernel32#IsProcessInJob},
	 * {@code Kernel32.QueryFullProcessImageName}). A handle that has the
	 * {@link #PROCESS_QUERY_INFORMATION} access right is automatically granted
	 * {@link #PROCESS_QUERY_LIMITED_INFORMATION}.
	 *
	 * Windows Server 2003 and Windows XP: This access right is not supported.
	 */
	int PROCESS_QUERY_LIMITED_INFORMATION = 0x1000;

	/**
	 * Required to set certain information about a process, such as its priority
	 * class (see {@code Kernel32#SetPriorityClass}).
	 */
	int PROCESS_SET_INFORMATION = 0x0200;

	/**
	 * Required to set memory limits using
	 * {@code Kernel32.SetProcessWorkingSetSize()}.
	 */
	int PROCESS_SET_QUOTA = 0x0100;

	/** Required to suspend or resume a process. */
	int PROCESS_SUSPEND_RESUME = 0x0800;

	/**
	 * Required to terminate a process using
	 * {@link Kernel32#TerminateProcess}.
	 */
	int PROCESS_TERMINATE = 0x00000001;

    /**
	 * Required for getting process exe path in native system path format
	 * {@code Kernel32.QueryFullProcessImageName()}.
	 */
	int PROCESS_NAME_NATIVE = 0x00000001;

	/**
	 * Required to perform an operation on the address space of a process (see
	 * {@code Kernel32.VirtualProtectEx()} and
	 * {@link Kernel32#WriteProcessMemory}
	 * ).
	 */
	int PROCESS_VM_OPERATION = 0x0008;

	/**
	 * Required to read memory in a process using
	 * {@link Kernel32#ReadProcessMemory}
	 * .
	 */
	int PROCESS_VM_READ = 0x0010;

	/**
	 * Required to write to memory in a process using
	 * {@link Kernel32#WriteProcessMemory}
	 * .
	 */
	int PROCESS_VM_WRITE = 0x0020;

	/** Required to wait for the process to terminate using the wait functions. */
	int PROCESS_SYNCHRONIZE = 0x00100000;

    /* Security information types */
    int OWNER_SECURITY_INFORMATION = 0x00000001;
    int GROUP_SECURITY_INFORMATION = 0x00000002;
    int DACL_SECURITY_INFORMATION = 0x00000004;
    int SACL_SECURITY_INFORMATION = 0x00000008;
    int LABEL_SECURITY_INFORMATION = 0x00000010;
    int PROTECTED_DACL_SECURITY_INFORMATION = 0x80000000;
    int PROTECTED_SACL_SECURITY_INFORMATION = 0x40000000;
    int UNPROTECTED_DACL_SECURITY_INFORMATION = 0x20000000;
    int UNPROTECTED_SACL_SECURITY_INFORMATION = 0x10000000;

    /* Security control bits */
    int SE_OWNER_DEFAULTED          = 0x00000001;
    int SE_GROUP_DEFAULTED          = 0x00000002;
    int SE_DACL_PRESENT             = 0x00000004;
    int SE_DACL_DEFAULTED           = 0x00000008;
    int SE_SACL_PRESENT             = 0x00000010;
    int SE_SACL_DEFAULTED           = 0x00000020;
    int SE_DACL_AUTO_INHERIT_REQ    = 0x00000100;
    int SE_SACL_AUTO_INHERIT_REQ    = 0x00000200;
    int SE_DACL_AUTO_INHERITED      = 0x00000400;
    int SE_SACL_AUTO_INHERITED      = 0x00000800;
    int SE_DACL_PROTECTED           = 0x00001000;
    int SE_SACL_PROTECTED           = 0x00002000;
    int SE_RM_CONTROL_VALID         = 0x00004000;
    int SE_SELF_RELATIVE            = 0x00008000;

    int SECURITY_DESCRIPTOR_REVISION = 0x00000001;

    public static class SECURITY_DESCRIPTOR extends Structure {
        public static class ByReference extends SECURITY_DESCRIPTOR implements
                Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder("data");
        public byte[] data;

        public SECURITY_DESCRIPTOR() {
            super();
        }

        public SECURITY_DESCRIPTOR(byte[] data) {
            super();
            this.data = data;
            useMemory(new Memory(data.length));
        }

        public SECURITY_DESCRIPTOR(int size) {
            super();
            useMemory(new Memory(size));
            data = new byte[size];
        }

        public SECURITY_DESCRIPTOR(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    int ACL_REVISION        = 2;
    int ACL_REVISION_DS     = 4;

    // This is the history of ACL revisions.  Add a new one whenever
    // ACL_REVISION is updated
    int ACL_REVISION1       = 1;
    int ACL_REVISION2       = 2;
    int ACL_REVISION3       = 3;
    int ACL_REVISION4       = 4;
    int MIN_ACL_REVISION    = ACL_REVISION2;
    int MAX_ACL_REVISION    = ACL_REVISION4;

    public static class ACL extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("AclRevision", "Sbz1", "AclSize", "AceCount", "Sbz2");

        /*
         * Maximum size chosen based on technet article:
         * https://technet.microsoft.com/en-us/library/cc781716.aspx
         */
        public static int MAX_ACL_SIZE = 64 * 1024;

        public byte AclRevision;
        public byte Sbz1;
        public short AclSize;
        public short AceCount;
        public short Sbz2;

        private ACCESS_ACEStructure[] ACEs;

        public ACL() {
            super();
        }

        public ACL(int size) {
            super();
            useMemory(new Memory(size));
        }

        public ACL(Pointer pointer) {
            super(pointer);
            read();
            ACEs = new ACCESS_ACEStructure[AceCount];
            int offset = size();
            for (int i = 0; i < AceCount; i++) {
                Pointer share = pointer.share(offset);
                // ACE_HEADER.AceType
                final byte aceType = share.getByte(0);
                ACCESS_ACEStructure ace;
                switch (aceType) {
                    case ACCESS_ALLOWED_ACE_TYPE:
                        ace = new ACCESS_ALLOWED_ACE(share);
                        break;
                    case ACCESS_DENIED_ACE_TYPE:
                        ace = new ACCESS_DENIED_ACE(share);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown ACE type " + aceType);
                }
                ACEs[i] = ace;
                offset += ace.AceSize;
            }
        }

        public ACCESS_ACEStructure[] getACEStructures() {
            return ACEs;
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class PACLByReference extends ByReference {
        public PACLByReference() {
            this(null);
        }

        public PACLByReference(ACL h) {
            super(Native.POINTER_SIZE);
            setValue(h);
        }

        public void setValue(ACL h) {
            getPointer().setPointer(0, h != null ? h.getPointer() : null);
        }

        public ACL getValue() {
            Pointer p = getPointer().getPointer(0);
            if (p == null) {
                return null;
            }
            else {
                return new ACL(p);
            }
        }
    }

    public static class SECURITY_DESCRIPTOR_RELATIVE extends Structure {
        public static class ByReference extends SECURITY_DESCRIPTOR_RELATIVE
                implements Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder("Revision", "Sbz1", "Control", "Owner", "Group", "Sacl", "Dacl");

        public byte Revision;
        public byte Sbz1;
        public short Control;
        public int Owner;
        public int Group;
        public int Sacl;
        public int Dacl;

        private PSID OWNER;
        private PSID GROUP;
        private ACL SACL;
        private ACL DACL;

        public SECURITY_DESCRIPTOR_RELATIVE() {
            super();
        }

        public SECURITY_DESCRIPTOR_RELATIVE(byte[] data) {
            super(new Memory(data.length));
            getPointer().write(0, data, 0, data.length);
            setMembers();
        }

        public SECURITY_DESCRIPTOR_RELATIVE(int length) {
            super(new Memory(length));
        }

        public SECURITY_DESCRIPTOR_RELATIVE(Pointer p) {
            super(p);
            setMembers();
        }

        public PSID getOwner() {
        	return OWNER;
        }

        public PSID getGroup() {
        	return GROUP;
        }

        public ACL getDiscretionaryACL() {
            return DACL;
        }

        public ACL getSystemACL() {
        	return SACL;
        }

        private final void setMembers() {
            read();
            if (Dacl != 0) {
                DACL = new ACL(getPointer().share(Dacl));
            }
            if (Sacl != 0) {
                SACL = new ACL(getPointer().share(Sacl));
            }
        	if (Group != 0) {
        		GROUP =  new PSID(getPointer().share(Group));
        	}
        	if (Owner != 0) {
        		OWNER =  new PSID(getPointer().share(Owner));
        	}
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static abstract class ACEStructure extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("AceType", "AceFlags", "AceSize");

        public byte AceType;
        public byte AceFlags;
        public short AceSize;

        PSID psid;

        public ACEStructure() {
            super();
        }

        public ACEStructure(Pointer p) {
            super(p);
        }

        public ACEStructure(byte AceType, byte AceFlags, short AceSize, PSID psid) {
            super();
            this.AceType = AceType;
            this.AceFlags = AceFlags;
            this.AceSize = AceSize;
            this.psid = psid;
            write();
        }

        public String getSidString() {
            return Advapi32Util.convertSidToStringSid(psid);
        }

        public PSID getSID() {
            return psid;
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /* ACE header */
    public static class ACE_HEADER extends ACEStructure {
        public ACE_HEADER() {
            super();
        }

        public ACE_HEADER(Pointer p) {
            super(p);
            read();
        }
    }

    /**
     * ACCESS_ALLOWED_ACE and ACCESS_DENIED_ACE have the same structure layout
     */
    public static abstract class ACCESS_ACEStructure extends ACEStructure {
        public static final List<String> FIELDS = createFieldsOrder(ACEStructure.FIELDS, "Mask", "SidStart");

        public int Mask;
        /**
         * First 4 bytes of the SID
         * Only used to have a valid field defined - use sid!
         */
        public byte[] SidStart = new byte[4];

        public ACCESS_ACEStructure() {
            super();
        }

        public ACCESS_ACEStructure(int Mask, byte AceType, byte AceFlags, PSID psid) {
            super();
            this.calculateSize(true);
            this.AceType = AceType;
            this.AceFlags = AceFlags;
            this.AceSize = (short) (super.fieldOffset("SidStart") + psid.getBytes().length);
            this.psid = psid;
            this.Mask = Mask;
            this.SidStart = psid.getPointer().getByteArray(0, SidStart.length);
            this.allocateMemory(AceSize);
            write();
        }

        public ACCESS_ACEStructure(Pointer p) {
            super(p);
            read();
        }

        /**
         * Write override due to psid not being a managed field
         */
        @Override
        public void write() {
            super.write();
            int offsetOfSID = super.fieldOffset("SidStart");
            int sizeOfSID = super.AceSize - super.fieldOffset("SidStart");
            if(psid != null) {
                // Get bytes from the PSID
                byte[] psidWrite = psid.getBytes();
                assert psidWrite.length <= sizeOfSID;
                // Write those bytes to native memory
                getPointer().write(offsetOfSID, psidWrite, 0, sizeOfSID);
            }
        }

        @Override
        public void read() {
            super.read();
            int offsetOfSID = super.fieldOffset("SidStart");
            int sizeOfSID = super.AceSize - super.fieldOffset("SidStart");
            if(sizeOfSID > 0) {
                psid = new PSID(getPointer().getByteArray(offsetOfSID, sizeOfSID));
            } else {
                psid = new PSID();
            }
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /* Access allowed ACE */
    public static class ACCESS_ALLOWED_ACE extends ACCESS_ACEStructure {
        public ACCESS_ALLOWED_ACE() {
            super();
        }

        public ACCESS_ALLOWED_ACE(Pointer p) {
            super(p);
        }

        public ACCESS_ALLOWED_ACE(int Mask, byte AceFlags, PSID psid) {
            super(Mask, ACCESS_ALLOWED_ACE_TYPE, AceFlags, psid);
        }
    }

    /* Access denied ACE */
    public static class ACCESS_DENIED_ACE extends ACCESS_ACEStructure {
        public ACCESS_DENIED_ACE() {
            super();
        }

        public ACCESS_DENIED_ACE(Pointer p) {
            super(p);
        }

        public ACCESS_DENIED_ACE(int Mask, byte AceFlags, PSID psid) {
            super(Mask, ACCESS_DENIED_ACE_TYPE, AceFlags, psid);
        }
    }

    /* ACE types */
    byte ACCESS_ALLOWED_ACE_TYPE = 0x00;
    byte ACCESS_DENIED_ACE_TYPE = 0x01;
    byte SYSTEM_AUDIT_ACE_TYPE = 0x02;
    byte SYSTEM_ALARM_ACE_TYPE = 0x03;
    byte ACCESS_ALLOWED_COMPOUND_ACE_TYPE = 0x04;
    byte ACCESS_ALLOWED_OBJECT_ACE_TYPE = 0x05;
    byte ACCESS_DENIED_OBJECT_ACE_TYPE = 0x06;
    byte SYSTEM_AUDIT_OBJECT_ACE_TYPE = 0x07;
    byte SYSTEM_ALARM_OBJECT_ACE_TYPE = 0x08;
    byte ACCESS_ALLOWED_CALLBACK_ACE_TYPE = 0x09;
    byte ACCESS_DENIED_CALLBACK_ACE_TYPE = 0x0A;
    byte ACCESS_ALLOWED_CALLBACK_OBJECT_ACE_TYPE = 0x0B;
    byte ACCESS_DENIED_CALLBACK_OBJECT_ACE_TYPE = 0x0C;
    byte SYSTEM_AUDIT_CALLBACK_ACE_TYPE = 0x0D;
    byte SYSTEM_ALARM_CALLBACK_ACE_TYPE = 0x0E;
    byte SYSTEM_AUDIT_CALLBACK_OBJECT_ACE_TYPE = 0x0F;
    byte SYSTEM_ALARM_CALLBACK_OBJECT_ACE_TYPE = 0x10;
    byte SYSTEM_MANDATORY_LABEL_ACE_TYPE = 0x11;

    /* ACE inherit flags */
    byte OBJECT_INHERIT_ACE = 0x01;
    byte CONTAINER_INHERIT_ACE = 0x02;
    byte NO_PROPAGATE_INHERIT_ACE = 0x04;
    byte INHERIT_ONLY_ACE = 0x08;
    byte INHERITED_ACE = 0x10;
    byte VALID_INHERIT_FLAGS = 0x1F;

    // TODO: figure out how OVERLAPPED is used and apply an appropriate mapping
    interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallCallback {
        void callback(int errorCode, int nBytesTransferred,
                WinBase.OVERLAPPED overlapped);
    }


    /**
     * Defines the mapping of generic access rights to specific and standard access rights for an object
     */
    public static class GENERIC_MAPPING extends Structure {
        public static class ByReference extends GENERIC_MAPPING implements Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder(
                "genericRead", "genericWrite", "genericExecute", "genericAll");

        public DWORD genericRead;
        public DWORD genericWrite;
        public DWORD genericExecute;
        public DWORD genericAll;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Describes the relationship between the specified processor set. This structure is used with the
     * {@link Kernel32#GetLogicalProcessorInformation} function.
     */
    public static class SYSTEM_LOGICAL_PROCESSOR_INFORMATION extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("processorMask", "relationship", "payload");

        /**
         * The processor mask identifying the processors described by this structure. A processor mask is a bit
         * vector in which each set bit represents an active processor in the relationship.
         */
        public ULONG_PTR processorMask;

        /**
         * The relationship between the processors identified by the value of the {@link #processorMask} member.
         * This member can be one of
         * {@link LOGICAL_PROCESSOR_RELATIONSHIP#RelationCache},
         * {@link LOGICAL_PROCESSOR_RELATIONSHIP#RelationNumaNode},
         * {@link LOGICAL_PROCESSOR_RELATIONSHIP#RelationProcessorCore} or
         * {@link LOGICAL_PROCESSOR_RELATIONSHIP#RelationProcessorPackage}.
         *
         * @see LOGICAL_PROCESSOR_RELATIONSHIP
         */
        public int /* LOGICAL_PROCESSOR_RELATIONSHIP */ relationship;

        /**
         * A union of fields which differs depending on {@link #relationship}.
         */
        public AnonymousUnionPayload payload;

        public SYSTEM_LOGICAL_PROCESSOR_INFORMATION() {
            super();
        }

        public SYSTEM_LOGICAL_PROCESSOR_INFORMATION(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public static class AnonymousUnionPayload extends Union {
            /**
             * Contains valid data only if {@link #relationship} is {@link LOGICAL_PROCESSOR_RELATIONSHIP#RelationProcessorCore}.
             */
            public AnonymousStructProcessorCore processorCore;

            /**
             * Contains valid data only if {@link #relationship} is {@link LOGICAL_PROCESSOR_RELATIONSHIP#RelationNumaNode}.
             */
            public AnonymousStructNumaNode numaNode;

            /**
             * <p>Identifies the characteristics of a particular cache. There is one record returned for each cache
             *    reported. Some or all caches may not be reported, depending on how caches are identified. Therefore,
             *    do not assume the absence of any particular caches. Caches are not necessarily shared among
             *    logical processors.</p>
             *
             * <p>Contains valid data only if {@link #relationship} is
             *    {@link LOGICAL_PROCESSOR_RELATIONSHIP#RelationCache}.</p>
             *
             * <p>This member was not supported until Windows Server 2003 SP1 / Windows XP Professional x64.</p>
             */
            public CACHE_DESCRIPTOR cache;

            /**
             * Reserved. Do not use.
             */
            public ULONGLONG[] reserved = new ULONGLONG[2];
        }

        public static class AnonymousStructProcessorCore extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("flags");
            /**
             * <p>If the value of this mmeber is {@code 1}, the logical processors identified by the value of the
             *    {@link #processorMask} member share functional units, as in Hyperthreading or SMT. Otherwise, the
             *    identified logical processors do not share functional units.</p>
             *
             * <p>Note: Prior to Windows Vista, this member is also {@code 1} for cores that share a physical
             *    package.</p>
             */
            public BYTE flags;

            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        public static class AnonymousStructNumaNode extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("nodeNumber");
            /**
             * Identifies the NUMA node. Valid values are {@code 0} to the highest NUMA node number inclusive.
             * A non-NUMA multiprocessor system will report that all processors belong to one NUMA node.
             */
            public DWORD nodeNumber;

            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }
    }

    /**
     * Represents the relationship between the processor set identified in the corresponding
     * {@link SYSTEM_LOGICAL_PROCESSOR_INFORMATION} or <code>SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX</code> structure.
     */
    public interface LOGICAL_PROCESSOR_RELATIONSHIP {
        /**
         * The specified logical processors share a single processor core.
         */
        int RelationProcessorCore = 0;

        /**
         * The specified logical processors are part of the same NUMA node.
         */
        int RelationNumaNode = 1;

        /**
         * <p>The specified logical processors share a cache.</p>
         *
         * <p>Not supported until Windows Server 2003 SP1 / Windows XP Professional x64.</p>
         */
        int RelationCache = 2;

        /**
         * <p>The specified logical processors share a physical package (a single package socketed or soldered onto a
         *    motherboard may contain multiple processor cores or threads, each of which is treated as a separate
         *    processor by the operating system.)</p>
         *
         * <p>Not supported until Windows Server 2003 SP1 / Windows XP Professional x64.</p>
         */
        int RelationProcessorPackage = 3;

        /**
         * <p>The specified logical processors share a single processor group.</p>
         *
         * <p>Not supported until Windows Server 2008 R2.</p>
         */
        int RelationGroup = 4;

        /**
         * <p>On input, retrieves information about all possible relation types. This value is not used on output.</p>
         *
         * <p>Not supported until Windows Server 2008 R2.</p>
         */
        int RelationAll = 0xFFFF;
    }

    byte CACHE_FULLY_ASSOCIATIVE = (byte)0xFF;

    /**
     * Describes the cache attributes.
     */
    public static class CACHE_DESCRIPTOR extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("level", "associativity", "lineSize", "size", "type");
        /**
         * The cache level. This member can be 1, 2 or 3, corresponding to L1, L2 or L3 cache, respectively (other
         * values may be supported in the future.)
         */
        public BYTE level;

        /**
         * The cache associativity. If this member is {@link #CACHE_FULLY_ASSOCIATIVE}, the cache is fully
         * associative.
         */
        public BYTE associativity;

        /**
         * The cache line size, in bytes.
         */
        public WORD lineSize;

        /**
         * The cache size, in bytes.
         */
        public DWORD size;

        /**
         * The cache type.
         *
         * @see PROCESSOR_CACHE_TYPE
         */
        public int /* PROCESSOR_CACHE_TYPE */ type;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Represents the type of processor cache identifier in the corresponding {@link CACHE_DESCRIPTOR} structure.
     */
    public static abstract class PROCESSOR_CACHE_TYPE {
        /**
         * The cache is unified.
         */
        public static int CacheUnified = 0;

        /**
         * The cache is for processor instructions.
         */
        public static int CacheInstruction = 1;

        /**
         * The cache is for data.
         */
        public static int CacheData = 2;

        /**
         * The cache is for traces.
         */
        public static int CacheTrace = 3;
    }

    /**
     * Indicates committed pages for which physical storage has been allocated, either in memory or in the paging file on disk.
     */
    int MEM_COMMIT = 0x1000;

    /**
     * Indicates free pages not accessible to the calling process and available to be allocated.
     * For free pages, the information in the AllocationBase, AllocationProtect, Protect, and Type members is undefined.
     */
    int MEM_FREE = 0x10000;

    /**
     * Indicates reserved pages where a range of the process's virtual address space is reserved without any physical storage being allocated.
     * For reserved pages, the information in the Protect member is undefined.
     */
    int MEM_RESERVE = 0x2000;

    /**
     * Indicates that the memory pages within the region are mapped into the view of an image section.
     */
    int MEM_IMAGE = 0x1000000;

    /**
     * Indicates that the memory pages within the region are mapped into the view of a section.
     */
    int MEM_MAPPED = 0x40000;

    /**
     * Indicates that the memory pages within the region are private (that is, not shared by other processes).
     */
    int MEM_PRIVATE = 0x20000;

    public static class MEMORY_BASIC_INFORMATION extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("baseAddress", "allocationBase", "allocationProtect",
                "regionSize", "state", "protect", "type");

        /**
         * A pointer to the base address of the region of pages.
         */
        public Pointer baseAddress;

        /**
         * A pointer to the base address of a range of pages allocated by the VirtualAlloc function.
         * The page pointed to by the BaseAddress member is contained within this allocation range.
         */
        public Pointer allocationBase;

        /**
         * The memory protection option when the region was initially allocated.
         * This member can be one of the memory protection constants or 0 if the caller does not have access.
         */
        public DWORD allocationProtect;

        /**
         * The size of the region beginning at the base address in which all pages have identical attributes, in bytes.
         */
        public SIZE_T regionSize;

        /**
         * The state of the pages in the region.
         * This member can be one of the following values:
         *
         * MEM_COMMIT,
         * MEM_FREE,
         * MEM_RESERVE.
         */
        public DWORD state;

        /**
         * The access protection of the pages in the region.
         * This member is one of the values listed for the AllocationProtect member.
         */
        public DWORD protect;

        /**
         * The type of pages in the region.
         * The following types are defined:
         *
         * MEM_IMAGE
         * MEM_MAPPED
         * MEM_PRIVATE
         */
        public DWORD type;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    public class SECURITY_QUALITY_OF_SERVICE extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "Length", "ImpersonationLevel", "ContextTrackingMode", "EffectiveOnly"
        );

        /** Specifies the size, in bytes, of this structure.
         */
        public int Length;
        /**
         * Specifies the information given to the server about the client, and
         * how the server may represent, or impersonate, the client. Security
         * impersonation levels govern the degree to which a server process can
         * act on behalf of a client process. This member is a
         * {@link WinNT.SECURITY_IMPERSONATION_LEVEL} enumeration type value.
         */
        public int ImpersonationLevel;
        /**
         * Specifies whether the server is to be given a snapshot of the
         * client's security context (called static tracking), or is to be
         * continually updated to track changes to the client's security context
         * (called dynamic tracking). The {@link WinNT#SECURITY_STATIC_TRACKING}
         * value specifies static tracking, and the
         * {@link WinNT#SECURITY_DYNAMIC_TRACKING} value specifies dynamic
         * tracking. Not all communications mechanisms support dynamic tracking;
         * those that do not will default to static tracking.
         */
        public byte ContextTrackingMode;
        /**
         * Specifies whether the server may enable or disable privileges and
         * groups that the client's security context may include.
         * 
         * <p>This is a boolean value. See {@link WinNT#BOOLEAN_TRUE} and 
         * {@link WinNT#BOOLEAN_FALSE}.</p>
         */
        public byte EffectiveOnly;

        @Override
        public void write() {
            this.Length = size();
            super.write();
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    byte SECURITY_DYNAMIC_TRACKING = (byte) 1;
    byte SECURITY_STATIC_TRACKING = (byte) 0;
    byte BOOLEAN_TRUE = (byte) 1;
    byte BOOLEAN_FALSE = (byte) 0;

    /*
     * Primary language IDs.
     */
    public static final int LANG_NEUTRAL                    = 0x00;
    public static final int LANG_INVARIANT                  = 0x7f;

    public static final int LANG_AFRIKAANS                  = 0x36;
    public static final int LANG_ALBANIAN                   = 0x1c;
    public static final int LANG_ARABIC                     = 0x01;
    public static final int LANG_ARMENIAN                   = 0x2b;
    public static final int LANG_ASSAMESE                   = 0x4d;
    public static final int LANG_AZERI                      = 0x2c;
    public static final int LANG_BASQUE                     = 0x2d;
    public static final int LANG_BELARUSIAN                 = 0x23;
    public static final int LANG_BENGALI                    = 0x45;
    public static final int LANG_BULGARIAN                  = 0x02;
    public static final int LANG_CATALAN                    = 0x03;
    public static final int LANG_CHINESE                    = 0x04;
    public static final int LANG_CROATIAN                   = 0x1a;
    public static final int LANG_CZECH                      = 0x05;
    public static final int LANG_DANISH                     = 0x06;
    public static final int LANG_DIVEHI                     = 0x65;
    public static final int LANG_DUTCH                      = 0x13;
    public static final int LANG_ENGLISH                    = 0x09;
    public static final int LANG_ESTONIAN                   = 0x25;
    public static final int LANG_FAEROESE                   = 0x38;
    public static final int LANG_FARSI                      = 0x29;
    public static final int LANG_FINNISH                    = 0x0b;
    public static final int LANG_FRENCH                     = 0x0c;
    public static final int LANG_GALICIAN                   = 0x56;
    public static final int LANG_GEORGIAN                   = 0x37;
    public static final int LANG_GERMAN                     = 0x07;
    public static final int LANG_GREEK                      = 0x08;
    public static final int LANG_GUJARATI                   = 0x47;
    public static final int LANG_HEBREW                     = 0x0d;
    public static final int LANG_HINDI                      = 0x39;
    public static final int LANG_HUNGARIAN                  = 0x0e;
    public static final int LANG_ICELANDIC                  = 0x0f;
    public static final int LANG_INDONESIAN                 = 0x21;
    public static final int LANG_ITALIAN                    = 0x10;
    public static final int LANG_JAPANESE                   = 0x11;
    public static final int LANG_KANNADA                    = 0x4b;
    public static final int LANG_KASHMIRI                   = 0x60;
    public static final int LANG_KAZAK                      = 0x3f;
    public static final int LANG_KONKANI                    = 0x57;
    public static final int LANG_KOREAN                     = 0x12;
    public static final int LANG_KYRGYZ                     = 0x40;
    public static final int LANG_LATVIAN                    = 0x26;
    public static final int LANG_LITHUANIAN                 = 0x27;
    public static final int LANG_MACEDONIAN                 = 0x2f;   // the Former Yugoslav Republic of Macedonia
    public static final int LANG_MALAY                      = 0x3e;
    public static final int LANG_MALAYALAM                  = 0x4c;
    public static final int LANG_MANIPURI                   = 0x58;
    public static final int LANG_MARATHI                    = 0x4e;
    public static final int LANG_MONGOLIAN                  = 0x50;
    public static final int LANG_NEPALI                     = 0x61;
    public static final int LANG_NORWEGIAN                  = 0x14;
    public static final int LANG_ORIYA                      = 0x48;
    public static final int LANG_POLISH                     = 0x15;
    public static final int LANG_PORTUGUESE                 = 0x16;
    public static final int LANG_PUNJABI                    = 0x46;
    public static final int LANG_ROMANIAN                   = 0x18;
    public static final int LANG_RUSSIAN                    = 0x19;
    public static final int LANG_SANSKRIT                   = 0x4f;
    public static final int LANG_SERBIAN                    = 0x1a;
    public static final int LANG_SINDHI                     = 0x59;
    public static final int LANG_SLOVAK                     = 0x1b;
    public static final int LANG_SLOVENIAN                  = 0x24;
    public static final int LANG_SPANISH                    = 0x0a;
    public static final int LANG_SWAHILI                    = 0x41;
    public static final int LANG_SWEDISH                    = 0x1d;
    public static final int LANG_SYRIAC                     = 0x5a;
    public static final int LANG_TAMIL                      = 0x49;
    public static final int LANG_TATAR                      = 0x44;
    public static final int LANG_TELUGU                     = 0x4a;
    public static final int LANG_THAI                       = 0x1e;
    public static final int LANG_TURKISH                    = 0x1f;
    public static final int LANG_UKRAINIAN                  = 0x22;
    public static final int LANG_URDU                       = 0x20;
    public static final int LANG_UZBEK                      = 0x43;
    public static final int LANG_VIETNAMESE                 = 0x2a;

    /*
     * Sublanguage IDs.
     *
     * The name immediately following SUBLANG_ dictates which primary
     * language ID that sublanguage ID can be combined with to form a
     * valid language ID.
     */
    public static final int SUBLANG_NEUTRAL                 = 0x00;    // language neutral
    public static final int SUBLANG_DEFAULT                 = 0x01;    // user default
    public static final int SUBLANG_SYS_DEFAULT             = 0x02;    // system default

    public static final int SUBLANG_ARABIC_SAUDI_ARABIA     = 0x01;    // Arabic (Saudi Arabia)
    public static final int SUBLANG_ARABIC_IRAQ             = 0x02;    // Arabic (Iraq)
    public static final int SUBLANG_ARABIC_EGYPT            = 0x03;    // Arabic (Egypt)
    public static final int SUBLANG_ARABIC_LIBYA            = 0x04;    // Arabic (Libya)
    public static final int SUBLANG_ARABIC_ALGERIA          = 0x05;    // Arabic (Algeria)
    public static final int SUBLANG_ARABIC_MOROCCO          = 0x06;    // Arabic (Morocco)
    public static final int SUBLANG_ARABIC_TUNISIA          = 0x07;    // Arabic (Tunisia)
    public static final int SUBLANG_ARABIC_OMAN             = 0x08;    // Arabic (Oman)
    public static final int SUBLANG_ARABIC_YEMEN            = 0x09;    // Arabic (Yemen)
    public static final int SUBLANG_ARABIC_SYRIA            = 0x0a;    // Arabic (Syria)
    public static final int SUBLANG_ARABIC_JORDAN           = 0x0b;    // Arabic (Jordan)
    public static final int SUBLANG_ARABIC_LEBANON          = 0x0c;    // Arabic (Lebanon)
    public static final int SUBLANG_ARABIC_KUWAIT           = 0x0d;    // Arabic (Kuwait)
    public static final int SUBLANG_ARABIC_UAE              = 0x0e;    // Arabic (U.A.E)
    public static final int SUBLANG_ARABIC_BAHRAIN          = 0x0f;    // Arabic (Bahrain)
    public static final int SUBLANG_ARABIC_QATAR            = 0x10;    // Arabic (Qatar)
    public static final int SUBLANG_AZERI_LATIN             = 0x01;    // Azeri (Latin)
    public static final int SUBLANG_AZERI_CYRILLIC          = 0x02;    // Azeri (Cyrillic)
    public static final int SUBLANG_CHINESE_TRADITIONAL     = 0x01;    // Chinese (Taiwan)
    public static final int SUBLANG_CHINESE_SIMPLIFIED      = 0x02;    // Chinese (PR China)
    public static final int SUBLANG_CHINESE_HONGKONG        = 0x03;    // Chinese (Hong Kong S.A.R., P.R.C.)
    public static final int SUBLANG_CHINESE_SINGAPORE       = 0x04;    // Chinese (Singapore)
    public static final int SUBLANG_CHINESE_MACAU           = 0x05;    // Chinese (Macau S.A.R.)
    public static final int SUBLANG_DUTCH                   = 0x01;    // Dutch
    public static final int SUBLANG_DUTCH_BELGIAN           = 0x02;    // Dutch (Belgian)
    public static final int SUBLANG_ENGLISH_US              = 0x01;    // English (USA)
    public static final int SUBLANG_ENGLISH_UK              = 0x02;    // English (UK)
    public static final int SUBLANG_ENGLISH_AUS             = 0x03;    // English (Australian)
    public static final int SUBLANG_ENGLISH_CAN             = 0x04;    // English (Canadian)
    public static final int SUBLANG_ENGLISH_NZ              = 0x05;    // English (New Zealand)
    public static final int SUBLANG_ENGLISH_EIRE            = 0x06;    // English (Irish)
    public static final int SUBLANG_ENGLISH_SOUTH_AFRICA    = 0x07;    // English (South Africa)
    public static final int SUBLANG_ENGLISH_JAMAICA         = 0x08;    // English (Jamaica)
    public static final int SUBLANG_ENGLISH_CARIBBEAN       = 0x09;    // English (Caribbean)
    public static final int SUBLANG_ENGLISH_BELIZE          = 0x0a;    // English (Belize)
    public static final int SUBLANG_ENGLISH_TRINIDAD        = 0x0b;    // English (Trinidad)
    public static final int SUBLANG_ENGLISH_ZIMBABWE        = 0x0c;    // English (Zimbabwe)
    public static final int SUBLANG_ENGLISH_PHILIPPINES     = 0x0d;    // English (Philippines)
    public static final int SUBLANG_FRENCH                  = 0x01;    // French
    public static final int SUBLANG_FRENCH_BELGIAN          = 0x02;    // French (Belgian)
    public static final int SUBLANG_FRENCH_CANADIAN         = 0x03;    // French (Canadian)
    public static final int SUBLANG_FRENCH_SWISS            = 0x04;    // French (Swiss)
    public static final int SUBLANG_FRENCH_LUXEMBOURG       = 0x05;    // French (Luxembourg)
    public static final int SUBLANG_FRENCH_MONACO           = 0x06;    // French (Monaco)
    public static final int SUBLANG_GERMAN                  = 0x01;    // German
    public static final int SUBLANG_GERMAN_SWISS            = 0x02;    // German (Swiss)
    public static final int SUBLANG_GERMAN_AUSTRIAN         = 0x03;    // German (Austrian)
    public static final int SUBLANG_GERMAN_LUXEMBOURG       = 0x04;    // German (Luxembourg)
    public static final int SUBLANG_GERMAN_LIECHTENSTEIN    = 0x05;    // German (Liechtenstein)
    public static final int SUBLANG_ITALIAN                 = 0x01;    // Italian
    public static final int SUBLANG_ITALIAN_SWISS           = 0x02;    // Italian (Swiss)
    public static final int SUBLANG_KASHMIRI_SASIA          = 0x02;    // Kashmiri (South Asia)
    public static final int SUBLANG_KASHMIRI_INDIA          = 0x02;    // For app compatibility only
    public static final int SUBLANG_KOREAN                  = 0x01;    // Korean (Extended Wansung)
    public static final int SUBLANG_LITHUANIAN              = 0x01;    // Lithuanian
    public static final int SUBLANG_MALAY_MALAYSIA          = 0x01;    // Malay (Malaysia)
    public static final int SUBLANG_MALAY_BRUNEI_DARUSSALAM = 0x02;    // Malay (Brunei Darussalam)
    public static final int SUBLANG_NEPALI_INDIA            = 0x02;    // Nepali (India)
    public static final int SUBLANG_NORWEGIAN_BOKMAL        = 0x01;    // Norwegian (Bokmal)
    public static final int SUBLANG_NORWEGIAN_NYNORSK       = 0x02;    // Norwegian (Nynorsk)
    public static final int SUBLANG_PORTUGUESE              = 0x02;    // Portuguese
    public static final int SUBLANG_PORTUGUESE_BRAZILIAN    = 0x01;    // Portuguese (Brazilian)
    public static final int SUBLANG_SERBIAN_LATIN           = 0x02;    // Serbian (Latin)
    public static final int SUBLANG_SERBIAN_CYRILLIC        = 0x03;    // Serbian (Cyrillic)
    public static final int SUBLANG_SPANISH                 = 0x01;    // Spanish (Castilian)
    public static final int SUBLANG_SPANISH_MEXICAN         = 0x02;    // Spanish (Mexican)
    public static final int SUBLANG_SPANISH_MODERN          = 0x03;    // Spanish (Spain)
    public static final int SUBLANG_SPANISH_GUATEMALA       = 0x04;    // Spanish (Guatemala)
    public static final int SUBLANG_SPANISH_COSTA_RICA      = 0x05;    // Spanish (Costa Rica)
    public static final int SUBLANG_SPANISH_PANAMA          = 0x06;    // Spanish (Panama)
    public static final int SUBLANG_SPANISH_DOMINICAN_REPUBLIC = 0x07; // Spanish (Dominican Republic)
    public static final int SUBLANG_SPANISH_VENEZUELA       = 0x08;    // Spanish (Venezuela)
    public static final int SUBLANG_SPANISH_COLOMBIA        = 0x09;    // Spanish (Colombia)
    public static final int SUBLANG_SPANISH_PERU            = 0x0a;    // Spanish (Peru)
    public static final int SUBLANG_SPANISH_ARGENTINA       = 0x0b;    // Spanish (Argentina)
    public static final int SUBLANG_SPANISH_ECUADOR         = 0x0c;    // Spanish (Ecuador)
    public static final int SUBLANG_SPANISH_CHILE           = 0x0d;    // Spanish (Chile)
    public static final int SUBLANG_SPANISH_URUGUAY         = 0x0e;    // Spanish (Uruguay)
    public static final int SUBLANG_SPANISH_PARAGUAY        = 0x0f;    // Spanish (Paraguay)
    public static final int SUBLANG_SPANISH_BOLIVIA         = 0x10;    // Spanish (Bolivia)
    public static final int SUBLANG_SPANISH_EL_SALVADOR     = 0x11;    // Spanish (El Salvador)
    public static final int SUBLANG_SPANISH_HONDURAS        = 0x12;    // Spanish (Honduras)
    public static final int SUBLANG_SPANISH_NICARAGUA       = 0x13;    // Spanish (Nicaragua)
    public static final int SUBLANG_SPANISH_PUERTO_RICO     = 0x14;    // Spanish (Puerto Rico)
    public static final int SUBLANG_SWEDISH                 = 0x01;    // Swedish
    public static final int SUBLANG_SWEDISH_FINLAND         = 0x02;    // Swedish (Finland)
    public static final int SUBLANG_URDU_PAKISTAN           = 0x01;    // Urdu (Pakistan)
    public static final int SUBLANG_URDU_INDIA              = 0x02;    // Urdu (India)
    public static final int SUBLANG_UZBEK_LATIN             = 0x01;    // Uzbek (Latin)
    public static final int SUBLANG_UZBEK_CYRILLIC          = 0x02;    // Uzbek (Cyrillic)

    /*
     * Sorting IDs.
     */
    public static final int SORT_DEFAULT                    = 0x0;     // sorting default

    public static final int SORT_JAPANESE_XJIS              = 0x0;     // Japanese XJIS order
    public static final int SORT_JAPANESE_UNICODE           = 0x1;     // Japanese Unicode order

    public static final int SORT_CHINESE_BIG5               = 0x0;     // Chinese BIG5 order
    public static final int SORT_CHINESE_PRCP               = 0x0;     // PRC Chinese Phonetic order
    public static final int SORT_CHINESE_UNICODE            = 0x1;     // Chinese Unicode order
    public static final int SORT_CHINESE_PRC                = 0x2;     // PRC Chinese Stroke Count order
    public static final int SORT_CHINESE_BOPOMOFO           = 0x3;     // Traditional Chinese Bopomofo order

    public static final int SORT_KOREAN_KSC                 = 0x0;     // Korean KSC order
    public static final int SORT_KOREAN_UNICODE             = 0x1;     // Korean Unicode order

    public static final int SORT_GERMAN_PHONE_BOOK          = 0x1;     // German Phone Book order

    public static final int SORT_HUNGARIAN_DEFAULT          = 0x0;     // Hungarian Default order
    public static final int SORT_HUNGARIAN_TECHNICAL        = 0x1;     // Hungarian Technical order

    public static final int SORT_GEORGIAN_TRADITIONAL       = 0x0;     // Georgian Traditional order
    public static final int SORT_GEORGIAN_MODERN            = 0x1;     // Georgian Modern order

    public static final int NLS_VALID_LOCALE_MASK = 0x000fffff;

    /**
     *  <p>A language ID is a 16 bit value which is the combination of a
     *  primary language ID and a secondary language ID.  The bits are
     *  allocated as follows:</p>
     *
     *  <pre>
     *       +-----------------------+-------------------------+
     *       |     Sublanguage ID    |   Primary Language ID   |
     *       +-----------------------+-------------------------+
     *        15                   10 9                       0   bit
     *  </pre>
     * 
     *  <p>WARNING:  This pattern isn't always follows, Serbina, Bosnian &amp; Croation for example.</p>
     *
     *  <p>It is recommended that applications test for locale names or actual LCIDs.</p>
     *
     *  <p>Note that the LANG, SUBLANG construction is not always consistent.
     *  The named locale APIs (eg GetLocaleInfoEx) are recommended.</p>
     *
     *  <p>Language IDs do not exist for all locales</p>
     *
     *  <p>A locale ID is a 32 bit value which is the combination of a
     *  language ID, a sort ID, and a reserved area.  The bits are
     * allocated as follows:</p>
     *
     * <pre>
     *   +-------------+---------+-------------------------+
     *   |   Reserved  | Sort ID |      Language ID        |
     *   +-------------+---------+-------------------------+
     *    31         20 19     16 15                      0   bit
     * </pre>
     * 
     * <p>WARNING: This pattern isn't always followed (es-ES_tradnl vs es-ES for example)</p>
     * 
     * <p>It is recommended that applications test for locale names or actual LCIDs.</p>
     */
    public static final class LocaleMacros {
        private static final int _MAKELCID(int lgid, int srtid) {
            return (srtid << 16) | lgid;
        }

        /**
         * construct the locale id from a language id and a sort id.
         * 
         * @param lgid language id
         * @param srtid sort id
         * @return locale id derived from ldig and srtid
         */
        public static final LCID MAKELCID(int lgid, int srtid) {
            return new LCID(_MAKELCID(lgid, srtid));
        }

        /**
         * construct the locale id from a language id, sort id, and sort version.
         * 
         * @param lgid locale id
         * @param srtid sort id
         * @param ver sort version
         * @return locale id derviced from a language id, sort id, and sort version.
         */
        public static final LCID MAKESORTLCID(int lgid, int srtid, int ver) {
            return new LCID(_MAKELCID(lgid, srtid) | (ver << 20));
        }

        /**
         * extract the language id from a locale id.
         * 
         * @param lcid locale id
         * @return extracted language id
         */
        public static final int LANGIDFROMLCID(LCID lcid) {
            return lcid.intValue() & 0xFFFF;
        }

        /**
         * extract the sort id from a locale id.
         * 
         * @param lcid locale id
         * @return extracted sort id
         */
        public static final int SORTIDFROMLCID(LCID lcid) {
            return (lcid.intValue() >>> 16) & 0xf;
        }

        /**
         * extract the sort version from a locale id.
         * 
         * @param lcid locale id
         * @return extracted sort version
         */
        public static final int SORTVERSIONFROMLCID(LCID lcid) {
            return (lcid.intValue() >>> 20) & 0xf;
        }

        /**
         * Construct language id from a primary language id and a sublanguage id.
         * 
         * @param p primary language ID
         * @param s sublanguage ID
         * @return constructed language id
         */
        public static final int MAKELANGID(int p, int s) {
            return (s << 10) | (p & 0xFFFF);
        }

        /**
         * Extract primary language id from a language id.
         * 
         * @param lgid language ID
         * @return extracted primary language id
         */
        public static final int PRIMARYLANGID(int lgid) {
            return lgid & 0x3ff;
        }

        /**
         * Extract sublanguage id from a language id.
         * 
         * @param lgid language ID
         * @return extracted sublanguage id
         */
        public static final int SUBLANGID(int lgid) {
            return (lgid  & 0xFFFF) >>> 10;
        }
    }

    public static final int  LANG_SYSTEM_DEFAULT   = LocaleMacros.MAKELANGID(LANG_NEUTRAL, SUBLANG_SYS_DEFAULT);
    public static final int  LANG_USER_DEFAULT     = LocaleMacros.MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT);

    public static final LCID LOCALE_SYSTEM_DEFAULT = LocaleMacros.MAKELCID(LANG_SYSTEM_DEFAULT, SORT_DEFAULT);
    public static final LCID LOCALE_USER_DEFAULT   = LocaleMacros.MAKELCID(LANG_USER_DEFAULT, SORT_DEFAULT);

    public static final LCID LOCALE_NEUTRAL        = LocaleMacros.MAKELCID(LocaleMacros.MAKELANGID(LANG_NEUTRAL, SUBLANG_NEUTRAL), SORT_DEFAULT);

    public static final LCID LOCALE_INVARIANT      = LocaleMacros.MAKELCID(LocaleMacros.MAKELANGID(LANG_INVARIANT, SUBLANG_NEUTRAL), SORT_DEFAULT);
}
