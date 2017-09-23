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

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Ported from Winbase.h (kernel32.dll/kernel services).
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface WinBase extends WinDef, BaseTSD {

    /** Constant value representing an invalid HANDLE. */
    HANDLE INVALID_HANDLE_VALUE =
        new HANDLE(Pointer.createConstant(Native.POINTER_SIZE == 8
                                          ? -1 : 0xFFFFFFFFL));

    int WAIT_FAILED = 0xFFFFFFFF;
    int WAIT_OBJECT_0 = ((NTStatus.STATUS_WAIT_0 ) + 0 );
    int WAIT_ABANDONED = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
    int WAIT_ABANDONED_0 = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );

    /**
     * Maximum computer name length.
     * The value is 15 on Mac, 31 on everything else.
     */
    int MAX_COMPUTERNAME_LENGTH = Platform.isMac() ? 15 : 31;

    /**
     * This logon type is intended for users who will be interactively using the computer, such
     * as a user being logged on by a terminal server, remote shell, or similar process. This
     * logon type has the additional expense of caching logon information for disconnected operations;
     * therefore, it is inappropriate for some client/server applications, such as a mail server.
     */
    int LOGON32_LOGON_INTERACTIVE = 2;
    /**
     * This logon type is intended for high performance servers to authenticate plaintext passwords.
     * The LogonUser function does not cache credentials for this logon type.
     */
    int LOGON32_LOGON_NETWORK = 3;
    /**
     * This logon type is intended for batch servers, where processes may be executing on behalf
     * of a user without their direct intervention. This type is also for higher performance servers
     * that process many plaintext authentication attempts at a time, such as mail or Web servers.
     * The LogonUser function does not cache credentials for this logon type.
     */
    int LOGON32_LOGON_BATCH = 4;
    /**
     * Indicates a service-type logon. The account provided must have the service privilege enabled.
     */
    int LOGON32_LOGON_SERVICE = 5;
    /**
     * This logon type is for GINA DLLs that log on users who will be interactively using the computer.
     * This logon type can generate a unique audit record that shows when the workstation was unlocked.
     */
    int LOGON32_LOGON_UNLOCK = 7;
    /**
     * This logon type preserves the name and password in the authentication package, which allows the
     * server to make connections to other network servers while impersonating the client. A server can
     * accept plaintext credentials from a client, call LogonUser, verify that the user can access the
     * system across the network, and still communicate with other servers.
     */
    int LOGON32_LOGON_NETWORK_CLEARTEXT = 8;
    /**
     * This logon type allows the caller to clone its current token and specify new credentials for
     * outbound connections. The new logon session has the same local identifier but uses different
     * credentials for other network connections. This logon type is supported only by the
     * LOGON32_PROVIDER_WINNT50 logon provider.
     */
    int LOGON32_LOGON_NEW_CREDENTIALS = 9;

    /**
     * Use the standard logon provider for the system. The default security provider is negotiate,
     * unless you pass NULL for the domain name and the user name is not in UPN format. In this case,
     * the default provider is NTLM.
     */
    int LOGON32_PROVIDER_DEFAULT = 0;

    /**
     * Use the Windows NT 3.5 logon provider.
     */
    int LOGON32_PROVIDER_WINNT35 = 1;
    /**
     * Use the NTLM logon provider.
     */
    int LOGON32_PROVIDER_WINNT40 = 2;
    /**
     * Use the negotiate logon provider.
     */
    int LOGON32_PROVIDER_WINNT50 = 3;

    /**
     * If this flag is set, a child process created with the bInheritHandles parameter of
     * CreateProcess set to TRUE will inherit the object handle.
     */
    int HANDLE_FLAG_INHERIT = 1;

    /**
     * If this flag is set, calling the {@link Kernel32#CloseHandle} function will not
     * close the object handle.
     */
    int HANDLE_FLAG_PROTECT_FROM_CLOSE = 2;

    // STARTUPINFO flags
    int STARTF_USESHOWWINDOW = 0x001;
    int STARTF_USESIZE = 0x002;
    int STARTF_USEPOSITION = 0x004;
    int STARTF_USECOUNTCHARS = 0x008;
    int STARTF_USEFILLATTRIBUTE = 0x010;
    int STARTF_RUNFULLSCREEN = 0x020;
    int STARTF_FORCEONFEEDBACK = 0x040;
    int STARTF_FORCEOFFFEEDBACK = 0x080;
    int STARTF_USESTDHANDLES = 0x100;

    // Process Creation flags
    int DEBUG_PROCESS = 0x00000001;
    int DEBUG_ONLY_THIS_PROCESS = 0x00000002;
    int CREATE_SUSPENDED = 0x00000004;
    int DETACHED_PROCESS = 0x00000008;
    int CREATE_NEW_CONSOLE = 0x00000010;
    int CREATE_NEW_PROCESS_GROUP = 0x00000200;
    int CREATE_UNICODE_ENVIRONMENT = 0x00000400;
    int CREATE_SEPARATE_WOW_VDM = 0x00000800;
    int CREATE_SHARED_WOW_VDM = 0x00001000;
    int CREATE_FORCEDOS = 0x00002000;
    int INHERIT_PARENT_AFFINITY = 0x00010000;
    int CREATE_PROTECTED_PROCESS = 0x00040000;
    int EXTENDED_STARTUPINFO_PRESENT = 0x00080000;
    int CREATE_BREAKAWAY_FROM_JOB = 0x01000000;
    int CREATE_PRESERVE_CODE_AUTHZ_LEVEL = 0x02000000;
    int CREATE_DEFAULT_ERROR_MODE = 0x04000000;
    int CREATE_NO_WINDOW = 0x08000000;

    /* File encryption status */
    int FILE_ENCRYPTABLE = 0;
    int FILE_IS_ENCRYPTED = 1;
    int FILE_SYSTEM_ATTR = 2;
    int FILE_ROOT_DIR = 3;
    int FILE_SYSTEM_DIR = 4;
    int FILE_UNKNOWN = 5;
    int FILE_SYSTEM_NOT_SUPPORT = 6;
    int FILE_USER_DISALLOWED = 7;
    int FILE_READ_ONLY = 8;
    int FILE_DIR_DISALOWED = 9;

    /* Open encrypted files raw flags */
    int CREATE_FOR_IMPORT = 1;
    int CREATE_FOR_DIR = 2;
    int OVERWRITE_HIDDEN = 4;

    /* Invalid return values */
    int INVALID_FILE_SIZE           = 0xFFFFFFFF;
    int INVALID_SET_FILE_POINTER    = 0xFFFFFFFF;
    int INVALID_FILE_ATTRIBUTES     = 0xFFFFFFFF;

    /**
     * Return code for a process still active.
     */
    int STILL_ACTIVE = WinNT.STATUS_PENDING;

    // Codes for FILE_INFO_BY_HANDLE_CLASS taken from Winbase.h
    int FileBasicInfo                   = 0;
    int FileStandardInfo                = 1;
    int FileNameInfo                    = 2;
    int FileRenameInfo                  = 3;
    int FileDispositionInfo             = 4;
    int FileAllocationInfo              = 5;
    int FileEndOfFileInfo               = 6;
    int FileStreamInfo                  = 7;
    int FileCompressionInfo             = 8;
    int FileAttributeTagInfo            = 9;
    int FileIdBothDirectoryInfo         = 10; // 0xA
    int FileIdBothDirectoryRestartInfo  = 11; // 0xB
    int FileIoPriorityHintInfo          = 12; // 0xC
    int FileRemoteProtocolInfo          = 13; // 0xD
    int FileFullDirectoryInfo           = 14; // 0xE
    int FileFullDirectoryRestartInfo    = 15; // 0xF
    int FileStorageInfo                 = 16; // 0x10
    int FileAlignmentInfo               = 17; // 0x11
    int FileIdInfo                      = 18; // 0x12
    int FileIdExtdDirectoryInfo         = 19; // 0x13
    int FileIdExtdDirectoryRestartInfo  = 20; // 0x14

    /**
     * Contains the basic information for a file. Used for file handles.
     */
    public static class FILE_BASIC_INFO extends Structure {

        public static class ByReference extends FILE_BASIC_INFO implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * The time the file was created in FILETIME format, which is a 64-bit value
         * representing the number of 100-nanosecond intervals since January 1, 1601 (UTC).
         */
        public LARGE_INTEGER CreationTime;

        /**
         * The time the file was last accessed in FILETIME format.
         */
        public LARGE_INTEGER LastAccessTime;

        /**
         * The time the file was last written to in FILETIME format.
         */
        public LARGE_INTEGER LastWriteTime;

        /**
         * The time the file was changed in FILETIME format.
         */
        public LARGE_INTEGER ChangeTime;

        /**
         * The file attributes. For a list of attributes, see File Attribute Constants.
         * If this is set to 0 in a FILE_BASIC_INFO structure passed to SetFileInformationByHandle
         * then none of the attributes are changed.
         */
        public int FileAttributes;

        public static int sizeOf()
        {
            return Native.getNativeSize(FILE_BASIC_INFO.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "CreationTime", "LastAccessTime", "LastWriteTime", "ChangeTime", "FileAttributes" });
        }

        public FILE_BASIC_INFO() {
            super();
        }

        public FILE_BASIC_INFO(Pointer memory) {
            super(memory);
            read();
            // This is admittedly odd, but the read() doesn't properly initialize the LARGE_INTEGERs via contructors, so do so here.
            this.CreationTime = new LARGE_INTEGER(this.CreationTime.getValue());
            this.LastAccessTime = new LARGE_INTEGER(this.LastAccessTime.getValue());
            this.LastWriteTime = new LARGE_INTEGER(this.LastWriteTime.getValue());
            this.ChangeTime = new LARGE_INTEGER(this.ChangeTime.getValue());
        }

        public FILE_BASIC_INFO(FILETIME CreationTime,
                FILETIME LastAccessTime,
                FILETIME LastWriteTime,
                FILETIME ChangeTime,
                int FileAttributes) {
            this.CreationTime = new LARGE_INTEGER(CreationTime.toTime());
            this.LastAccessTime = new LARGE_INTEGER(LastAccessTime.toTime());
            this.LastWriteTime = new LARGE_INTEGER(LastWriteTime.toTime());
            this.ChangeTime = new LARGE_INTEGER(ChangeTime.toTime());
            this.FileAttributes = FileAttributes;
            write();
        }

        public FILE_BASIC_INFO(LARGE_INTEGER CreationTime,
                LARGE_INTEGER LastAccessTime,
                LARGE_INTEGER LastWriteTime,
                LARGE_INTEGER ChangeTime,
                int FileAttributes) {
            this.CreationTime = CreationTime;
            this.LastAccessTime = LastAccessTime;
            this.LastWriteTime = LastWriteTime;
            this.ChangeTime = ChangeTime;
            this.FileAttributes = FileAttributes;
            write();
        }
    }

    /**
     * Receives extended information for the file. Used for file handles. Use only when calling GetFileInformationByHandleEx.
     */
    public static class FILE_STANDARD_INFO extends Structure {

        public static class ByReference extends FILE_STANDARD_INFO implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * The amount of space that is allocated for the file.
         */
        public LARGE_INTEGER AllocationSize;

        /**
         * The end of the file.
         */
        public LARGE_INTEGER EndOfFile;

        /**
         * The number of links to the file.
         */
        public int NumberOfLinks;

        /**
         * TRUE if the file in the delete queue; otherwise, false.
         */
        public boolean DeletePending;

        /**
         * TRUE if the file is a directory; otherwise, false.
         */
        public boolean Directory;

        public static int sizeOf()
        {
            return Native.getNativeSize(FILE_STANDARD_INFO.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "AllocationSize", "EndOfFile", "NumberOfLinks", "DeletePending", "Directory" });
        }

        public FILE_STANDARD_INFO() {
            super();
        }

        public FILE_STANDARD_INFO(Pointer memory) {
            super(memory);
            read();
        }

        public FILE_STANDARD_INFO(LARGE_INTEGER AllocationSize,
                LARGE_INTEGER EndOfFile,
                int NumberOfLinks,
                boolean DeletePending,
                boolean Directory) {
            this.AllocationSize = AllocationSize;
            this.EndOfFile = EndOfFile;
            this.NumberOfLinks = NumberOfLinks;
            this.DeletePending = DeletePending;
            this.Directory = Directory;
            write();
        }
    }

    /**
     * Indicates whether a file should be deleted. Used for any handles. Use only when calling SetFileInformationByHandle.
     */
    public static class FILE_DISPOSITION_INFO extends Structure {

        public static class ByReference extends FILE_DISPOSITION_INFO  implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * Indicates whether the file should be deleted. Set to TRUE to delete the file. This member
         * has no effect if the handle was opened with FILE_FLAG_DELETE_ON_CLOSE.
         */
        public boolean DeleteFile;

        public static int sizeOf()
        {
            return Native.getNativeSize(FILE_DISPOSITION_INFO.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "DeleteFile" });
        }

        public FILE_DISPOSITION_INFO () {
            super();
        }

        public FILE_DISPOSITION_INFO (Pointer memory) {
            super(memory);
            read();
        }

        public FILE_DISPOSITION_INFO (boolean DeleteFile) {
            this.DeleteFile = DeleteFile;
            write();
        }
    }

    /**
     * Receives extended information for the file. Used for file handles. Use only when calling GetFileInformationByHandleEx.
     */
    public static class FILE_COMPRESSION_INFO extends Structure {

        public static class ByReference extends FILE_COMPRESSION_INFO implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * The file size of the compressed file.
         */
        public LARGE_INTEGER CompressedFileSize;

        /**
         * The compression format that is used to compress the file.
         */
        public short CompressionFormat;

        /**
         * The factor that the compression uses.
         */
        public byte CompressionUnitShift;

        /**
         * The number of chunks that are shifted by compression.
         */
        public byte ChunkShift;

        /**
         * The number of clusters that are shifted by compression.
         */
        public byte ClusterShift;

        /**
         * Reserved
         */
        public byte[] Reserved = new byte[3];

        public static int sizeOf()
        {
            return Native.getNativeSize(FILE_COMPRESSION_INFO.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "CompressedFileSize", "CompressionFormat", "CompressionUnitShift", "ChunkShift", "ClusterShift", "Reserved" });
        }

        public FILE_COMPRESSION_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }

        public FILE_COMPRESSION_INFO(Pointer memory) {
            super(memory, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }

        public FILE_COMPRESSION_INFO(LARGE_INTEGER CompressedFileSize,
                short CompressionFormat,
                byte CompressionUnitShift,
                byte ChunkShift,
                byte ClusterShift) {
            this.CompressedFileSize = CompressedFileSize;
            this.CompressionFormat = CompressionFormat;
            this.CompressionUnitShift = CompressionUnitShift;
            this.ChunkShift = ChunkShift;
            this.ClusterShift = ClusterShift;
            this.Reserved = new byte[3];
            write();
        }
    }

    /**
     * Receives the requested file attribute information. Used for any handles. Use only when calling GetFileInformationByHandleEx.
     */
    public static class FILE_ATTRIBUTE_TAG_INFO extends Structure {

        public static class ByReference extends FILE_ATTRIBUTE_TAG_INFO implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * The file attribute information.
         */
        public int FileAttributes;

        /**
         * The reparse tag.
         */
        public int ReparseTag;

        public static int sizeOf()
        {
            return Native.getNativeSize(FILE_ATTRIBUTE_TAG_INFO.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "FileAttributes", "ReparseTag" });
        }

        public FILE_ATTRIBUTE_TAG_INFO() {
            super();
        }

        public FILE_ATTRIBUTE_TAG_INFO(Pointer memory) {
            super(memory);
            read();
        }

        public FILE_ATTRIBUTE_TAG_INFO(int FileAttributes,
                int ReparseTag) {
            this.FileAttributes = FileAttributes;
            this.ReparseTag = ReparseTag;
            write();
        }
    }

    /**
     * Contains identification information for a file. This structure is returned from the
     * GetFileInformationByHandleEx function when FileIdInfo is passed in the
     * FileInformationClass parameter.
     */
    public static class FILE_ID_INFO extends Structure {

        public static class ByReference extends FILE_ID_INFO implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static class FILE_ID_128 extends Structure {
            public BYTE[] Identifier = new BYTE[16];

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[] { "Identifier" });
            }

            public FILE_ID_128() {
                super();
            }

            public FILE_ID_128(Pointer memory) {
                super(memory);
                read();
            }

            public FILE_ID_128(BYTE[] Identifier) {
                this.Identifier = Identifier;
                write();
            }
        }

        /**
         * The serial number of the volume that contains a file.
         */
        public long VolumeSerialNumber;

        /**
         * The end of the file.
         */
        public FILE_ID_128 FileId;

        public static int sizeOf()
        {
            return Native.getNativeSize(FILE_ID_INFO.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "VolumeSerialNumber", "FileId" });
        }

        public FILE_ID_INFO() {
            super();
        }

        public FILE_ID_INFO(Pointer memory) {
            super(memory);
            read();
        }

        public FILE_ID_INFO(long VolumeSerialNumber,
                FILE_ID_128 FileId) {
            this.VolumeSerialNumber = VolumeSerialNumber;
            this.FileId = FileId;
            write();
        }
    }

    // FINDEX_INFO_LEVELS values defines values that are used with the FindFirstFileEx function to specify the information level of the returned data.

    /**
     * The FindFirstFileEx function retrieves a standard set of attribute information. The data is returned
     * in a WIN32_FIND_DATA structure.
     */
    int FindExInfoStandard = 0;

    /**
     * The FindFirstFileEx function does not query the short file name, improving overall enumeration speed. The data is
     * returned in a WIN32_FIND_DATA structure, and the cAlternateFileName member is always a NULL string.
     */
    int FindExInfoBasic = 1;
    /**
     * This value is used for validation. Supported values are less than this value.
     */
    int FindExInfoMaxInfoLevel = 2;

    // FINDEX_SEARCH_OPS values defines values that are used with the FindFirstFileEx function to specify the type of filtering to perform.
    /**
     * The search for a file that matches a specified file name. The lpSearchFilter parameter of FindFirstFileEx
     * must be NULL when this search operation is used.
     */
    int FindExSearchNameMatch = 0;

    /**
     * This is an advisory flag. If the file system supports directory filtering, the function searches for a file that
     * matches the specified name and is also a directory. If the file system does not support directory filtering,
     * this flag is silently ignored.
     * The lpSearchFilter parameter of the FindFirstFileEx function must be NULL when this search value is used.
     * If directory filtering is desired, this flag can be used on all file systems, but because it is an advisory
     * flag and only affects file systems that support it, the application must examine the file attribute data stored
     * in the lpFindFileData parameter of the FindFirstFileEx function to determine whether the function has returned
     * a handle to a directory.
     */
    int FindExSearchLimitToDirectories = 1;

    /**
     * This filtering type is not available. For more information, see Device Interface Classes.
     */
    int FindExSearchLimitToDevices = 2;

    /**
     * Contains information about the file that is found by the FindFirstFile, FindFirstFileEx, or FindNextFile function.
     */
    public static class WIN32_FIND_DATA extends Structure {

        public static class ByReference extends WIN32_FIND_DATA implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * The file attributes of a file. For possible values and their descriptions,
         * see File Attribute Constants. The FILE_ATTRIBUTE_SPARSE_FILE attribute on
         * the file is set if any of the streams of the file have ever been sparse.
         */
        public int dwFileAttributes;

        /**
         * A FILETIME structure that specifies when a file or directory was created. If
         * the underlying file system does not support creation time, this member is zero.
         */
        public FILETIME ftCreationTime;

        /**
         * A FILETIME structure.  For a file, the structure specifies when the file was last
         * read from, written to, or for executable files, run. For a directory, the structure
         * specifies when the directory is created. If the underlying file system does not
         * support last access time, this member is zero. On the FAT file system, the
         * specified date for both files and directories is correct, but the time of day is
         * always set to midnight.
         */
        public FILETIME ftLastAccessTime;

        /**
         * A FILETIME structure. For a file, the structure specifies when the file was last
         * written to, truncated, or overwritten, for example, when WriteFile or SetEndOfFile
         * are used. The date and time are not updated when file attributes or security descriptors
         * are changed. For a directory, the structure specifies when the directory is created.
         * If the underlying file system does not support last write time, this member is zero.
         */
        public FILETIME ftLastWriteTime;

        /**
         * The high-order DWORD value of the file size, in bytes. This value is zero unless the
         * file size is greater than MAXDWORD.
         * The size of the file is equal to (nFileSizeHigh * (MAXDWORD+1)) + nFileSizeLow.
         */
        public int nFileSizeHigh;

        /**
         * The low-order DWORD value of the file size, in bytes.
         */
        public int nFileSizeLow;

        /**
         * If the dwFileAttributes member includes the FILE_ATTRIBUTE_REPARSE_POINT attribute, this member
         * specifies the reparse point tag. Otherwise, this value is undefined and should not be used.
         * For more information see Reparse Point Tags.
         *
         * IO_REPARSE_TAG_CSV (0x80000009)
         * IO_REPARSE_TAG_DEDUP (0x80000013)
         * IO_REPARSE_TAG_DFS (0x8000000A)
         * IO_REPARSE_TAG_DFSR (0x80000012)
         * IO_REPARSE_TAG_HSM (0xC0000004)
         * IO_REPARSE_TAG_HSM2 (0x80000006)
         * IO_REPARSE_TAG_MOUNT_POINT (0xA0000003)
         * IO_REPARSE_TAG_NFS (0x80000014)
         * IO_REPARSE_TAG_SIS (0x80000007)
         * IO_REPARSE_TAG_SYMLINK (0xA000000C)
         * IO_REPARSE_TAG_WIM (0x80000008)
         */
        public int dwReserved0;

        /**
         * Reserved for future use.
         */
        public int dwReserved1;

        /**
         * The name of the file. <b>NOTE: When written from Native memory, this will be a null terminated string.
         * Any characters after the null terminator are random memory. Use function getFileName to
         * get a String with the name.</b>
         */
        public char[] cFileName = new char[MAX_PATH];

        /**
         * An alternative name for the file. This name is in the classic 8.3 file name format.
         * <b>NOTE: When written from Native memory, this will be a null terminated string.
         * Any characters after the null terminator are random memory. Use function getAlternateFileName to
         * get a String with the alternate name.</b>
         */
        public char[] cAlternateFileName = new char[14];

        public static int sizeOf() {
            return Native.getNativeSize(WIN32_FIND_DATA.class, null);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "dwFileAttributes", "ftCreationTime", "ftLastAccessTime", "ftLastWriteTime", "nFileSizeHigh", "nFileSizeLow", "dwReserved0", "dwReserved1", "cFileName", "cAlternateFileName" });
        }

        public WIN32_FIND_DATA() {
            super(W32APITypeMapper.DEFAULT);
        }

        public WIN32_FIND_DATA(Pointer memory) {
            super(memory, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }

        public WIN32_FIND_DATA(int dwFileAttributes,
                FILETIME ftCreationTime,
                FILETIME ftLastAccessTime,
                FILETIME ftLastWriteTime,
                int nFileSizeHigh,
                int nFileSizeLow,
                int dwReserved0,
                int dwReserved1,
                char[] cFileName,
                char[] cAlternateFileName) {
            this.dwFileAttributes = dwFileAttributes;
            this.ftCreationTime = ftCreationTime;
            this.ftLastAccessTime = ftLastAccessTime;
            this.ftLastWriteTime = ftLastWriteTime;
            this.nFileSizeHigh = nFileSizeHigh;
            this.nFileSizeLow = nFileSizeLow;
            this.dwReserved0 = dwReserved0;
            this.cFileName = cFileName;
            this.cAlternateFileName = cAlternateFileName;
            write();
        }

        /**
         * @return String containing the file name
         */
        public String getFileName() {
            return Native.toString(cFileName);
        }

        /**
         * @return String containing the alternate file name
         */
        public String getAlternateFileName() {
            return Native.toString(cAlternateFileName);
        }
    }

    /**
     * The FILETIME structure is a 64-bit value representing the number of
     * 100-nanosecond intervals since January 1, 1601 (UTC).
     * Conversion code in this class Copyright 2002-2004 Apache Software Foundation.
     * @author Rainer Klute (klute@rainer-klute.de) for the Apache Software Foundation (org.apache.poi.hpsf)
     */
    public static class FILETIME extends Structure {
        public int dwLowDateTime;
        public int dwHighDateTime;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "dwLowDateTime", "dwHighDateTime" });
        }

        public static class ByReference extends FILETIME implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public FILETIME(Date date) {
            long rawValue = dateToFileTime(date);
            dwHighDateTime = (int)(rawValue >> 32 & 0xffffffffL);
            dwLowDateTime = (int)(rawValue & 0xffffffffL);
        }

        /**
         * Construct FILETIME from LARGE_INTEGER
         * @param ft
         */
        public FILETIME(LARGE_INTEGER ft) {
            dwHighDateTime = ft.getHigh().intValue();
            dwLowDateTime = ft.getLow().intValue();
        }

        public FILETIME() {
        }

        public FILETIME(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * <p>The difference between the Windows epoch (1601-01-01
         * 00:00:00) and the Unix epoch (1970-01-01 00:00:00) in
         * milliseconds: 11644473600000L. (Use your favorite spreadsheet
         * program to verify the correctness of this value. By the way,
         * did you notice that you can tell from the epochs which
         * operating system is the modern one? :-))</p>
         */
        private static final long EPOCH_DIFF = 11644473600000L;

        /**
         * <p>Converts a Windows FILETIME into a {@link Date}. The Windows
         * FILETIME structure holds a date and time associated with a
         * file. The structure identifies a 64-bit integer specifying the
         * number of 100-nanosecond intervals which have passed since
         * January 1, 1601. This 64-bit value is split into the two double
         * words stored in the structure.</p>
         *
         * @param high The higher double word of the FILETIME structure.
         * @param low The lower double word of the FILETIME structure.
         * @return The Windows FILETIME as a {@link Date}.
         */
        public static Date filetimeToDate(final int high, final int low) {
            final long filetime = (long) high << 32 | low & 0xffffffffL;
            final long ms_since_16010101 = filetime / (1000 * 10);
            final long ms_since_19700101 = ms_since_16010101 - EPOCH_DIFF;
            return new Date(ms_since_19700101);
        }

        /**
         * <p>Converts a {@link Date} into a filetime.</p>
         *
         * @param date The date to be converted
         * @return The filetime
         *
         * @see #filetimeToDate
         */
        public static long dateToFileTime(final Date date) {
            final long ms_since_19700101 = date.getTime();
            final long ms_since_16010101 = ms_since_19700101 + EPOCH_DIFF;
            return ms_since_16010101 * 1000 * 10;
        }

        /**
         * <p>Converts this filetime into a {@link Date}</p>
         * @return The {@link Date} represented by this filetime.
         */
        public Date toDate() {
            return filetimeToDate(dwHighDateTime, dwLowDateTime);
        }

        /**
         * <p>Converts this filetime into a number of milliseconds which have
         * passed since January 1, 1970 (UTC).</p>
         * @return This filetime as a number of milliseconds which have passed
         * since January 1, 1970 (UTC)
         */
        public long toTime() {
            return toDate().getTime();
        }

        /**
         * <p>Converts the two 32-bit unsigned integer parts of this filetime
         * into a 64-bit unsigned integer representing the number of
         * 100-nanosecond intervals since January 1, 1601 (UTC).</p>
         * @return This filetime as a 64-bit unsigned integer number of
         * 100-nanosecond intervals since January 1, 1601 (UTC).
         */
        public DWORDLONG toDWordLong() {
            return new DWORDLONG((long) dwHighDateTime << 32 | dwLowDateTime & 0xffffffffL);
        }

        @Override
        public String toString() {
            return super.toString() + ": " + toDate().toString(); //$NON-NLS-1$
        }
    }

    /* Local Memory Flags */
    int  LMEM_FIXED = 0x0000;
    int  LMEM_MOVEABLE = 0x0002;
    int  LMEM_NOCOMPACT = 0x0010;
    int  LMEM_NODISCARD = 0x0020;
    int  LMEM_ZEROINIT = 0x0040;
    int  LMEM_MODIFY = 0x0080;
    int  LMEM_DISCARDABLE = 0x0F00;
    int  LMEM_VALID_FLAGS = 0x0F72;
    int  LMEM_INVALID_HANDLE = 0x8000;

    int  LHND = (LMEM_MOVEABLE | LMEM_ZEROINIT);
    int  LPTR = (LMEM_FIXED | LMEM_ZEROINIT);

    /* Flags returned by LocalFlags (in addition to LMEM_DISCARDABLE) */
    int  LMEM_DISCARDED = 0x4000;
    int  LMEM_LOCKCOUNT = 0x00FF;

    /**
     * Specifies a date and time, using individual members for the month,
     * day, year, weekday, hour, minute, second, and millisecond. The time
     * is either in coordinated universal time (UTC) or local time, depending
     * on the function that is being called.
     * @see <A HREF="http://msdn.microsoft.com/en-us/library/ms724950(VS.85).aspx">SYSTEMTIME structure</A>
     */
    public static class SYSTEMTIME extends Structure {
        // The year. The valid values for this member are 1601 through 30827.
        public short wYear;
        // The month. The valid values for this member are 1 through 12.
        public short wMonth;
        // The day of the week. The valid values for this member are 0 through 6.
        public short wDayOfWeek;
        // The day of the month. The valid values for this member are 1 through 31.
        public short wDay;
        // The hour. The valid values for this member are 0 through 23.
        public short wHour;
        // The minute. The valid values for this member are 0 through 59.
        public short wMinute;
        // The second. The valid values for this member are 0 through 59.
        public short wSecond;
        // The millisecond. The valid values for this member are 0 through 999.
        public short wMilliseconds;

        public SYSTEMTIME() {
            super();
        }

        public SYSTEMTIME(Date date) {
            this(date.getTime());
        }

        public SYSTEMTIME(long timestamp) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            fromCalendar(cal);
        }

        public SYSTEMTIME(Calendar cal) {
            fromCalendar(cal);
        }

        public void fromCalendar(Calendar cal) {
            wYear = (short) cal.get(Calendar.YEAR);
            wMonth = (short) (1 + cal.get(Calendar.MONTH) - Calendar.JANUARY);  // 1 = January
            wDay = (short) cal.get(Calendar.DAY_OF_MONTH);
            wHour = (short) cal.get(Calendar.HOUR_OF_DAY);
            wMinute = (short) cal.get(Calendar.MINUTE);
            wSecond = (short) cal.get(Calendar.SECOND);
            wMilliseconds = (short) cal.get(Calendar.MILLISECOND);
            wDayOfWeek = (short) (cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY); // 0 = Sunday
        }

        public Calendar toCalendar() {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, wYear);
            cal.set(Calendar.MONTH, Calendar.JANUARY + (wMonth - 1));
            cal.set(Calendar.DAY_OF_MONTH, wDay);
            cal.set(Calendar.HOUR_OF_DAY, wHour);
            cal.set(Calendar.MINUTE, wMinute);
            cal.set(Calendar.SECOND, wSecond);
            cal.set(Calendar.MILLISECOND, wMilliseconds);
            return cal;
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds" });
        }

        @Override
        public String toString() {
            // if not initialized, return the default representation
            if ((wYear == 0) && (wMonth == 0) && (wDay == 0)
             && (wHour == 0) && (wMinute == 0) && (wSecond == 0)
             && (wMilliseconds == 0)) {
                return super.toString();
            }

            DateFormat dtf = DateFormat.getDateTimeInstance();
            Calendar cal = toCalendar();
            return dtf.format(cal.getTime());
        }
    }

    /**
     * Specifies settings for a time zone.
     * http://msdn.microsoft.com/en-us/library/windows/desktop/ms725481(v=vs.85).aspx
     */
    public static class TIME_ZONE_INFORMATION extends Structure {
        public LONG       Bias;
        public String      StandardName;
        public SYSTEMTIME StandardDate;
        public LONG       StandardBias;
        public String      DaylightName;
        public SYSTEMTIME DaylightDate;
        public LONG       DaylightBias;

        public TIME_ZONE_INFORMATION() {
            super(W32APITypeMapper.DEFAULT);
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "Bias", "StandardName", "StandardDate", "StandardBias", "DaylightName", "DaylightDate", "DaylightBias" });
        }
    }

    /**
     * The lpBuffer parameter is a pointer to a PVOID pointer, and that the nSize
     * parameter specifies the minimum number of TCHARs to allocate for an output
     * message buffer. The function allocates a buffer large enough to hold the
     * formatted message, and places a pointer to the allocated buffer at the address
     * specified by lpBuffer. The caller should use the LocalFree function to free
     * the buffer when it is no longer needed.
     */
    int FORMAT_MESSAGE_ALLOCATE_BUFFER = 0x00000100;
    /**
     * Insert sequences in the message definition are to be ignored and passed through
     * to the output buffer unchanged. This flag is useful for fetching a message for
     * later formatting. If this flag is set, the Arguments parameter is ignored.
     */
    int FORMAT_MESSAGE_IGNORE_INSERTS  = 0x00000200;
    /**
     * The lpSource parameter is a pointer to a null-terminated message definition.
     * The message definition may contain insert sequences, just as the message text
     * in a message table resource may. Cannot be used with FORMAT_MESSAGE_FROM_HMODULE
     * or FORMAT_MESSAGE_FROM_SYSTEM.
     */
    int FORMAT_MESSAGE_FROM_STRING     = 0x00000400;
    /**
     * The lpSource parameter is a module handle containing the message-table
     * resource(s) to search. If this lpSource handle is NULL, the current process's
     * application image file will be searched. Cannot be used with
     * FORMAT_MESSAGE_FROM_STRING.
     */
    int FORMAT_MESSAGE_FROM_HMODULE    = 0x00000800;
    /**
     * The function should search the system message-table resource(s) for the
     * requested message. If this flag is specified with FORMAT_MESSAGE_FROM_HMODULE,
     * the function searches the system message table if the message is not found in
     * the module specified by lpSource. Cannot be used with FORMAT_MESSAGE_FROM_STRING.
     * If this flag is specified, an application can pass the result of the
     * GetLastError function to retrieve the message text for a system-defined error.
     */
    int FORMAT_MESSAGE_FROM_SYSTEM     = 0x00001000;
    /**
     * The Arguments parameter is not a va_list structure, but is a pointer to an array
     * of values that represent the arguments. This flag cannot be used with 64-bit
     * argument values. If you are using 64-bit values, you must use the va_list
     * structure.
     */
    int FORMAT_MESSAGE_ARGUMENT_ARRAY  = 0x00002000;

    /**
     * The drive type cannot be determined.
     */
    int DRIVE_UNKNOWN = 0;
    /**
     * The root path is invalid, for example, no volume is mounted at the path.
     */
    int DRIVE_NO_ROOT_DIR = 1;
    /**
     * The drive is a type that has removable media, for example, a floppy drive
     * or removable hard disk.
     */
    int DRIVE_REMOVABLE = 2;
    /**
     * The drive is a type that cannot be removed, for example, a fixed hard drive.
     */
    int DRIVE_FIXED = 3;
    /**
     * The drive is a remote (network) drive.
     */
    int DRIVE_REMOTE = 4;
    /**
     * The drive is a CD-ROM drive.
     */
    int DRIVE_CDROM = 5;
    /**
     * The drive is a RAM disk.
     */
    int DRIVE_RAMDISK = 6;

    /**
     * The OVERLAPPED structure contains information used in
     * asynchronous (or overlapped) input and output (I/O).
     */
    public static class OVERLAPPED extends Structure {
        public ULONG_PTR Internal;
        public ULONG_PTR InternalHigh;
        public int Offset;
        public int OffsetHigh;
        public HANDLE hEvent;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "Internal", "InternalHigh", "Offset", "OffsetHigh", "hEvent" });
        }
    }

   int INFINITE = 0xFFFFFFFF;

    /**
     * Contains information about the current computer system. This includes the architecture and
     * type of the processor, the number of processors in the system, the page size, and other such
     * information.
     */
    public static class SYSTEM_INFO extends Structure {

        /** Unnamed inner structure. */
        public static class PI extends Structure {

            public static class ByReference extends PI implements Structure.ByReference {

            }

            /**
             * System's processor architecture.
             * This value can be one of the following values:
             *
             *  PROCESSOR_ARCHITECTURE_UNKNOWN
             *  PROCESSOR_ARCHITECTURE_INTEL
             *  PROCESSOR_ARCHITECTURE_IA64
             *  PROCESSOR_ARCHITECTURE_AMD64
             */
            public WORD wProcessorArchitecture;
            /**
             * Reserved for future use.
             */
            public WORD wReserved;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[] { "wProcessorArchitecture", "wReserved" });
            }
        }

        /** Unnamed inner union. */
        public static class UNION extends Union {

            public static class ByReference extends UNION implements Structure.ByReference {

            }

            /**
             * An obsolete member that is retained for compatibility with Windows NT 3.5 and earlier.
             * New applications should use the wProcessorArchitecture branch of the union.
             * Windows Me/98/95: The system always sets this member to zero, the value defined
             * for PROCESSOR_ARCHITECTURE_INTEL.
             */
            public DWORD dwOemID;
            /**
             * Processor architecture (unnamed struct).
             */
            public PI pi;
        }

        /**
         * Processor architecture (unnamed union).
         */
        public UNION processorArchitecture;
        /**
         * Page size and the granularity of page protection and commitment.
         */
        public DWORD dwPageSize;
        /**
         * Pointer to the lowest memory address accessible to applications and dynamic-link libraries (DLLs).
         */
        public Pointer lpMinimumApplicationAddress;
        /**
         * Pointer to the highest memory address accessible to applications and DLLs.
         */
        public Pointer lpMaximumApplicationAddress;
        /**
         * Mask representing the set of processors configured into the system. Bit 0 is processor 0; bit 31 is processor 31.
         */
        public DWORD_PTR dwActiveProcessorMask;
        /**
         * Number of processors in the system.
         */
        public DWORD dwNumberOfProcessors;
        /**
         * An obsolete member that is retained for compatibility with Windows NT 3.5 and Windows Me/98/95.
         * Use the wProcessorArchitecture, wProcessorLevel, and wProcessorRevision members to determine
         * the type of processor.
         *  PROCESSOR_INTEL_386
         *  PROCESSOR_INTEL_486
         *  PROCESSOR_INTEL_PENTIUM
         */
        public DWORD dwProcessorType;
        /**
         * Granularity for the starting address at which virtual memory can be allocated.
         */
        public DWORD dwAllocationGranularity;
        /**
         * System's architecture-dependent processor level. It should be used only for display purposes.
         * To determine the feature set of a processor, use the IsProcessorFeaturePresent function.
         * If wProcessorArchitecture is PROCESSOR_ARCHITECTURE_INTEL, wProcessorLevel is defined by the CPU vendor.
         * If wProcessorArchitecture is PROCESSOR_ARCHITECTURE_IA64, wProcessorLevel is set to 1.
         */
        public WORD wProcessorLevel;
        /**
         * Architecture-dependent processor revision.
         */
        public WORD wProcessorRevision;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "processorArchitecture", "dwPageSize", "lpMinimumApplicationAddress", "lpMaximumApplicationAddress", "dwActiveProcessorMask", "dwNumberOfProcessors", "dwProcessorType", "dwAllocationGranularity", "wProcessorLevel", "wProcessorRevision"});
        }
    }

    /**
     * Contains information about the current state of both physical and virtual memory, including
     * extended memory. The GlobalMemoryStatusEx function stores information in this structure.
     */
    public static class MEMORYSTATUSEX extends Structure {
        /**
         * The size of the structure, in bytes.
         */
        public DWORD dwLength;
        /**
         * A number between 0 and 100 that specifies the approximate percentage of physical memory
         * that is in use (0 indicates no memory use and 100 indicates full memory use).
         */
        public DWORD dwMemoryLoad;
        /**
         * The amount of actual physical memory, in bytes.
         */
        public DWORDLONG ullTotalPhys;
        /**
         * The amount of physical memory currently available, in bytes. This is the amount of physical
         * memory that can be immediately reused without having to write its contents to disk first.
         * It is the sum of the size of the standby, free, and zero lists.
         */
        public DWORDLONG ullAvailPhys;
        /**
         * The current committed memory limit for the system or the current process, whichever is smaller, in bytes.
         */
        public DWORDLONG ullTotalPageFile;
        /**
         * The maximum amount of memory the current process can commit, in bytes. This value is equal to or smaller
         * than the system-wide available commit value.
         */
        public DWORDLONG ullAvailPageFile;
        /**
         * The size of the user-mode portion of the virtual address space of the calling process, in bytes.
         */
        public DWORDLONG ullTotalVirtual;
        /**
         * The amount of unreserved and uncommitted memory currently in the user-mode portion of the
         * virtual address space of the calling process, in bytes.
         */
        public DWORDLONG ullAvailVirtual;
        /**
         * Reserved. This value is always 0.
         */
        public DWORDLONG ullAvailExtendedVirtual;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "dwLength", "dwMemoryLoad", "ullTotalPhys", "ullAvailPhys", "ullTotalPageFile", "ullAvailPageFile", "ullTotalVirtual", "ullAvailVirtual", "ullAvailExtendedVirtual" });
        }

        public MEMORYSTATUSEX() {
            dwLength = new DWORD(size());
        }
    };

    /**
     * The SECURITY_ATTRIBUTES structure contains the security descriptor for an
     * object and specifies whether the handle retrieved by specifying this
     * structure is inheritable. This structure provides security settings for
     * objects created by various functions, such as {@link Kernel32#CreateFile},
     * {@link Kernel32#CreatePipe}, or {@link Advapi32#RegCreateKeyEx}.
     */
    public static class SECURITY_ATTRIBUTES extends Structure {
        /**
         * The size of the structure, in bytes.
         */
        public DWORD dwLength;

        /**
         * A pointer to a SECURITY_DESCRIPTOR structure that controls access to the object.
         */
        public Pointer lpSecurityDescriptor;

        /**
         * A Boolean value that specifies whether the returned handle is inherited when
         * a new process is created
         */
        public boolean bInheritHandle;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "dwLength", "lpSecurityDescriptor", "bInheritHandle" });
        }

        public SECURITY_ATTRIBUTES() {
            dwLength = new DWORD(size());
        }
    }

    /**
     * Specifies the window station, desktop, standard handles, and appearance of the main
     * window for a process at creation time.
     */
    public static class STARTUPINFO extends Structure {
        /**
         * The size of the structure, in bytes.
         */
        public DWORD cb;

        /**
         * Reserved; must be NULL.
         */
        public String lpReserved;

        /**
         * The name of the desktop, or the name of both the desktop and window station for this process.
         * A backslash in the string indicates that the string includes both the desktop and window
         * station names. For more information, see Thread Connection to a Desktop.
         */
        public String lpDesktop;

        /**
         * For console processes, this is the title displayed in the title bar
         * if a new console window is created. If NULL, the name of the
         * executable file is used as the window title instead. This parameter
         * must be NULL for GUI or console processes that do not create a new
         * console window.
         */
        public String lpTitle;

        /**
         * If dwFlags specifies STARTF_USEPOSITION, this member is the x offset
         * of the upper left corner of a window if a new window is created, in
         * pixels. Otherwise, this member is ignored.
         *
         * The offset is from the upper left corner of the screen. For GUI
         * processes, the specified position is used the first time the new
         * process calls CreateWindow to create an overlapped window if the x
         * parameter of CreateWindow is CW_USEDEFAULT.
         */
        public DWORD dwX;

        /**
         * If dwFlags specifies STARTF_USEPOSITION, this member is the y offset
         * of the upper left corner of a window if a new window is created, in
         * pixels. Otherwise, this member is ignored.
         *
         * The offset is from the upper left corner of the screen. For GUI
         * processes, the specified position is used the first time the new
         * process calls CreateWindow to create an overlapped window if the y
         * parameter of CreateWindow is CW_USEDEFAULT.
         */
        public DWORD dwY;

        /**
         * If dwFlags specifies STARTF_USESIZE, this member is the width of the
         * window if a new window is created, in pixels. Otherwise, this member
         * is ignored.
         *
         * For GUI processes, this is used only the first time the new process
         * calls CreateWindow to create an overlapped window if the nWidth
         * parameter of CreateWindow is CW_USEDEFAULT.
         */
        public DWORD dwXSize;

        /**
         * If dwFlags specifies STARTF_USESIZE, this member is the height of the
         * window if a new window is created, in pixels. Otherwise, this member
         * is ignored.
         *
         * For GUI processes, this is used only the first time the new process
         * calls CreateWindow to create an overlapped window if the nHeight
         * parameter of CreateWindow is CW_USEDEFAULT.
         */
        public DWORD dwYSize;

        /**
         * If dwFlags specifies STARTF_USECOUNTCHARS, if a new console window is
         * created in a console process, this member specifies the screen buffer
         * width, in character columns. Otherwise, this member is ignored.
         */
        public DWORD dwXCountChars;

        /**
         * If dwFlags specifies STARTF_USECOUNTCHARS, if a new console window is
         * created in a console process, this member specifies the screen buffer
         * height, in character rows. Otherwise, this member is ignored.
         */
        public DWORD dwYCountChars;

        /**
         * If dwFlags specifies STARTF_USEFILLATTRIBUTE, this member is the
         * initial text and background colors if a new console window is created
         * in a console application. Otherwise, this member is ignored.
         *
         * This value can be any combination of the following values:
         * FOREGROUND_BLUE, FOREGROUND_GREEN, FOREGROUND_RED,
         * FOREGROUND_INTENSITY, BACKGROUND_BLUE, BACKGROUND_GREEN,
         * BACKGROUND_RED, and BACKGROUND_INTENSITY. For example, the following
         * combination of values produces red text on a white background:
         *
         * FOREGROUND_RED| BACKGROUND_RED| BACKGROUND_GREEN| BACKGROUND_BLUE
         */
        public DWORD dwFillAttribute;

        /**
         * A bit field that determines whether certain STARTUPINFO members are
         * used when the process creates a window.
         */
        public int dwFlags;

        /**
         * If dwFlags specifies STARTF_USESHOWWINDOW, this member can be any of
         * the values that can be specified in the nCmdShow parameter for the
         * ShowWindow function, except for SW_SHOWDEFAULT. Otherwise, this
         * member is ignored.
         *
         * For GUI processes, the first time ShowWindow is called, its nCmdShow
         * parameter is ignored wShowWindow specifies the default value. In
         * subsequent calls to ShowWindow, the wShowWindow member is used if the
         * nCmdShow parameter of ShowWindow is set to SW_SHOWDEFAULT.
         */
        public WORD wShowWindow;

        /**
         * Reserved for use by the C Run-time; must be zero.
         */
        public WORD cbReserved2;

        /**
         * Reserved for use by the C Run-time; must be NULL.
         */
        public ByteByReference lpReserved2;

        /**
         * If dwFlags specifies STARTF_USESTDHANDLES, this member is the
         * standard input handle for the process. If STARTF_USESTDHANDLES is not
         * specified, the default for standard input is the keyboard buffer.
         *
         * If dwFlags specifies STARTF_USEHOTKEY, this member specifies a hotkey
         * value that is sent as the wParam parameter of a WM_SETHOTKEY message
         * to the first eligible top-level window created by the application
         * that owns the process. If the window is created with the WS_POPUP
         * window style, it is not eligible unless the WS_EX_APPWINDOW extended
         * window style is also set. For more information, see CreateWindowEx.
         *
         * Otherwise, this member is ignored.
         */
        public HANDLE hStdInput;

        /**
         * If dwFlags specifies STARTF_USESTDHANDLES, this member is the
         * standard output handle for the process. Otherwise, this member is
         * ignored and the default for standard output is the console window's
         * buffer.
         */
        public HANDLE hStdOutput;

        /**
         * If dwFlags specifies STARTF_USESTDHANDLES, this member is the
         * standard error handle for the process. Otherwise, this member is
         * ignored and the default for standard error is the console window's
         * buffer.
         */
        public HANDLE hStdError;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "cb", "lpReserved", "lpDesktop", "lpTitle", "dwX", "dwY", "dwXSize", "dwYSize", "dwXCountChars", "dwYCountChars", "dwFillAttribute", "dwFlags", "wShowWindow", "cbReserved2", "lpReserved2", "hStdInput", "hStdOutput", "hStdError" });
        }

        public STARTUPINFO() {
            super(W32APITypeMapper.DEFAULT);
            cb = new DWORD(size());
        }
    }

    /**
     * Contains information about a newly created process and its primary
     * thread. It is used with the CreateProcess, CreateProcessAsUser,
     * CreateProcessWithLogonW, or CreateProcessWithTokenW function.
     */
    public static class PROCESS_INFORMATION extends Structure {

        /**
         * A handle to the newly created process. The handle is used to specify
         * the process in all functions that perform operations on the process
         * object.
         */
        public HANDLE hProcess;

        /**
         * A handle to the primary thread of the newly created process. The
         * handle is used to specify the thread in all functions that perform
         * operations on the thread object.
         */
        public HANDLE hThread;

        /**
         * A value that can be used to identify a process. The value is valid
         * from the time the process is created until all handles to the process
         * are closed and the process object is freed; at this point, the
         * identifier may be reused.
         */
        public DWORD dwProcessId;

        /**
         * A value that can be used to identify a thread. The value is valid
         * from the time the thread is created until all handles to the thread
         * are closed and the thread object is freed; at this point, the
         * identifier may be reused.
         */
        public DWORD dwThreadId;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "hProcess", "hThread", "dwProcessId", "dwThreadId" });
        }

        public static class ByReference extends PROCESS_INFORMATION implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public PROCESS_INFORMATION() {
        }

        public PROCESS_INFORMATION(Pointer memory) {
            super(memory);
            read();
        }
    }

    /**
     * If the file is to be moved to a different volume, the function simulates the move by using the CopyFile and DeleteFile functions.
     *
     * This value cannot be used with MOVEFILE_DELAY_UNTIL_REBOOT.
     */
    int MOVEFILE_COPY_ALLOWED = 0x2;

    /**
     * Reserved for future use.
     */
    int MOVEFILE_CREATE_HARDLINK = 0x10;

    /**
     * The system does not move the file until the operating system is restarted. The system moves the file immediately
     * after AUTOCHK is executed, but before creating any paging files. Consequently, this parameter enables the
     * function to delete paging files from previous startups.
     *
     * This value can be used only if the process is in the context of a user who belongs to the administrators group or
     * the LocalSystem account.
     *
     * This value cannot be used with MOVEFILE_COPY_ALLOWED.
     *
     * Windows Server 2003 and Windows XP:  For information about special situations where this functionality can fail,
     * and a suggested workaround solution, see Files are not exchanged when Windows Server 2003 restarts if you use the
     * MoveFileEx function to schedule a replacement for some files in the Help and Support Knowledge Base.
     *
     * Windows 2000:  If you specify the MOVEFILE_DELAY_UNTIL_REBOOT flag for dwFlags, you cannot also prepend the file
     * name that is specified by lpExistingFileName with "\\?".
     */
    int MOVEFILE_DELAY_UNTIL_REBOOT = 0x4;

    /**
     * The function fails if the source file is a link source, but the file cannot be tracked after the move. This
     * situation can occur if the destination is a volume formatted with the FAT file system.
     */
    int MOVEFILE_FAIL_IF_NOT_TRACKABLE = 0x20;

    /**
     * If a file named lpNewFileName exists, the function replaces its contents with the contents of the
     * lpExistingFileName file, provided that security requirements regarding access control lists (ACLs) are met. For
     * more information, see the Remarks section of this topic.
     *
     * This value cannot be used if lpNewFileName or lpExistingFileName names a directory.
     */
    int MOVEFILE_REPLACE_EXISTING = 0x1;

    /**
     * The function does not return until the file is actually moved on the disk.
     *
     * Setting this value guarantees that a move performed as a copy and delete operation is flushed to disk before the
     * function returns. The flush occurs at the end of the copy operation.
     *
     * This value has no effect if MOVEFILE_DELAY_UNTIL_REBOOT is set.
     */
    int MOVEFILE_WRITE_THROUGH = 0x8;

    /**
     * Represents a thread entry point local to this process, as a Callback.
     */
    public interface THREAD_START_ROUTINE extends StdCallCallback{
        public DWORD apply( LPVOID lpParameter );
    }

    /**
     * Represents a thread entry point in another process. Can only be expressed as a pointer, as
     * the location has no meaning in the Java process.
     */
    public class FOREIGN_THREAD_START_ROUTINE extends Structure {
        LPVOID foreignLocation;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "foreignLocation" });
        }
    }

    /**
     * Specifies a type of computer name to be retrieved by the GetComputerNameEx function
     */
    public static interface COMPUTER_NAME_FORMAT {
        /**
          * The NetBIOS name of the local computer or the cluster associated with the local
          * computer. This name is limited to MAX_COMPUTERNAME_LENGTH + 1 characters and may
          * be a truncated version of the DNS host name. For example, if the DNS host name is
          * &quot;corporate-mail-server&quot;, the NetBIOS name would be &quot;corporate-mail-"&quot;.
          */
        int ComputerNameNetBIOS = 0;

        /**
         * The DNS name of the local computer or the cluster associated with the local computer.
         */
        int ComputerNameDnsHostname = 1;

        /**
         * The name of the DNS domain assigned to the local computer or the cluster associated
         * with the local computer.
         */
        int ComputerNameDnsDomain = 2;

        /**
         * The fully qualified DNS name that uniquely identifies the local computer or the cluster
         * associated with the local computer. This name is a combination of the DNS host name and
         * the DNS domain name, using the form HostName.DomainName. For example, if the DNS host
         * name is &quot;corporate-mail-server&quot; and the DNS domain name is &quot;microsoft.com&quot;,
         * the fully qualified DNS name is &quot;corporate-mail-server.microsoft.com&quot;.
         */
        int ComputerNameDnsFullyQualified = 3;

        /**
         * The NetBIOS name of the local computer. On a cluster, this is the NetBIOS name of the
         * local node on the cluster.
         */
        int ComputerNamePhysicalNetBIOS = 4;

        /**
         * The DNS host name of the local computer. On a cluster, this is the DNS host name of the
         * local node on the cluster.
         */
        int ComputerNamePhysicalDnsHostname = 5;

        /**
         * The name of the DNS domain assigned to the local computer. On a cluster, this is the DNS
         * domain of the local node on the cluster.
         */
        int ComputerNamePhysicalDnsDomain = 6;

        /**
         * The fully qualified DNS name that uniquely identifies the computer. On a cluster, this is
         * the fully qualified DNS name of the local node on the cluster. The fully qualified DNS name
         * is a combination of the DNS host name and the DNS domain name, using the form HostName.DomainName.
         */
        int ComputerNamePhysicalDnsFullyQualified = 7;

        /**
         * Note used - serves as an upper limit in case one wants to go through all the values
         */
        int ComputerNameMax = 8;
    }

    /**
     * An application-defined callback function used with ReadEncryptedFileRaw.
     * The system calls ExportCallback one or more times, each time with a block
     * of the encrypted file's data, until it has received all of the file data.
     * ExportCallback writes the encrypted file's data to another storage media,
     * usually for purposes of backing up the file.
     */
    public interface FE_EXPORT_FUNC extends StdCallCallback {
        public DWORD callback(Pointer pbData, Pointer pvCallbackContext,
                              ULONG ulLength);
    }

    /**
     * An application-defined callback function used with WriteEncryptedFileRaw.
     * The system calls ImportCallback one or more times, each time to retrieve a
     * portion of a backup file's data. ImportCallback reads the data from a
     * backup file sequentially and restores the data, and the system continues
     * calling it until it has read all of the backup file data.
     */
    public interface FE_IMPORT_FUNC extends StdCallCallback {
        public DWORD callback(Pointer pbData, Pointer pvCallbackContext,
                              ULONGByReference ulLength);
    }

    int PIPE_CLIENT_END=0x00000000;
    int PIPE_SERVER_END=0x00000001;

        /* Pipe open mode values */
    int PIPE_ACCESS_DUPLEX=0x00000003;
    int PIPE_ACCESS_INBOUND=0x00000001;
    int PIPE_ACCESS_OUTBOUND=0x00000002;

        /* Pipe type values */
    int PIPE_TYPE_BYTE=0x00000000;
    int PIPE_TYPE_MESSAGE=0x00000004;

        /* Pipe read modes */
    int PIPE_READMODE_BYTE=0x00000000;
    int PIPE_READMODE_MESSAGE=0x00000002;

        /* Pipe wait modes */
    int PIPE_WAIT=0x00000000;
    int PIPE_NOWAIT=0x00000001;

    int PIPE_ACCEPT_REMOTE_CLIENTS=0x00000000;
    int PIPE_REJECT_REMOTE_CLIENTS=0x00000008;

    int PIPE_UNLIMITED_INSTANCES=255;

    /* Named pipe pre-defined timeout values */
    int NMPWAIT_USE_DEFAULT_WAIT=0x00000000;
    int NMPWAIT_NOWAIT=0x00000001;
    int NMPWAIT_WAIT_FOREVER=0xffffffff;



    /**
     *
     * Contains the time-out parameters for a communications device. The
     * parameters determine the behavior of
     * {@link Kernel32#ReadFile}, {@link Kernel32#WriteFile}, ReadFileEx, and
     * WriteFileEx operations on the device.
     * <br>
     *
     * <b>Remarks</b><br>
     * If an application sets ReadIntervalTimeout and ReadTotalTimeoutMultiplier
     * to MAXDWORD and sets ReadTotalTimeoutConstant to a value greater than
     * zero and less than MAXDWORD, one of the following occurs when the
     * ReadFile function is called:
     * <li>If there are any bytes in the input buffer, ReadFile returns
     * immediately with the bytes in the buffer.</li>
     * <li>If there are no bytes in the input buffer, ReadFile waits until a
     * byte arrives and then returns immediately.</li>
     * <li>If no bytes arrive within the time specified by
     * ReadTotalTimeoutConstant, ReadFile times out.</li>
     *
     * @author Markus
     */
    public static class COMMTIMEOUTS extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("ReadIntervalTimeout", "ReadTotalTimeoutMultiplier",
                "ReadTotalTimeoutConstant", "WriteTotalTimeoutMultiplier", "WriteTotalTimeoutConstant");

        /**
         *
         * The maximum time allowed to elapse before the arrival of the next
         * byte on the communications line, in milliseconds. If the interval
         * between the arrival of any two bytes exceeds this amount, the
         * {@link Kernel32#ReadFile}
         * operation is completed and any buffered data is returned. A value of
         * zero indicates that interval time-outs are not used.
         *
         * A value of MAXDWORD, combined with zero values for both the
         * {@link COMMTIMEOUTS#ReadTotalTimeoutConstant} and
         * {@link COMMTIMEOUTS#ReadTotalTimeoutMultiplier} members, specifies
         * that the read operation is to return immediately with the bytes that
         * have already been received, even if no bytes have been received.
         *
         */
        public DWORD ReadIntervalTimeout;

        /**
         * The multiplier used to calculate the total time-out period for read
         * operations, in milliseconds. For each read operation, this value is
         * multiplied by the requested number of bytes to be read.
         */
        public DWORD ReadTotalTimeoutMultiplier;

        /**
         * A constant used to calculate the total time-out period for read
         * operations, in milliseconds. For each read operation, this value is
         * added to the product of the
         * {@link COMMTIMEOUTS#ReadTotalTimeoutMultiplier} member and the
         * requested number of bytes.
         *
         * A value of zero for both the
         * {@link COMMTIMEOUTS#ReadTotalTimeoutMultiplier} and
         * {@link COMMTIMEOUTS#ReadTotalTimeoutConstant} members indicates that
         * total time-outs are not used for read operations.
         */
        public DWORD ReadTotalTimeoutConstant;

        /**
         * The multiplier used to calculate the total time-out period for write
         * operations, in milliseconds. For each write operation, this value is
         * multiplied by the number of bytes to be written.
         */
        public DWORD WriteTotalTimeoutMultiplier;

        /**
         * A constant used to calculate the total time-out period for write
         * operations, in milliseconds. For each write operation, this value is
         * added to the product of the
         * {@link COMMTIMEOUTS#WriteTotalTimeoutMultiplier} member and the
         * number of bytes to be written.
         *
         * A value of zero for both the
         * {@link COMMTIMEOUTS#WriteTotalTimeoutMultiplier} and
         * {@link COMMTIMEOUTS#WriteTotalTimeoutConstant} members indicates that
         * total time-outs are not used for write operations.
         *
         */
        public DWORD WriteTotalTimeoutConstant;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Defines the control setting for a serial communications device.
     */
    public static class DCB extends Structure {

        /**
         * Type is used to handle the bitfield of the DBC structure.
         */
        public static class DCBControllBits extends DWORD {
            private static final long serialVersionUID = 8574966619718078579L;

            @Override
            public String toString() {
                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append('<');
                stringBuilder.append("fBinary:1=");
                stringBuilder.append(getfBinary() ? '1' : '0');
                stringBuilder.append(", fParity:1=");
                stringBuilder.append(getfParity() ? '1' : '0');
                stringBuilder.append(", fOutxCtsFlow:1=");
                stringBuilder.append(getfOutxCtsFlow() ? '1' : '0');
                stringBuilder.append(", fOutxDsrFlow:1=");
                stringBuilder.append(getfOutxDsrFlow() ? '1' : '0');
                stringBuilder.append(", fDtrControl:2=");
                stringBuilder.append(getfDtrControl());
                stringBuilder.append(", fDsrSensitivity:1=");
                stringBuilder.append(getfDsrSensitivity() ? '1' : '0');
                stringBuilder.append(", fTXContinueOnXoff:1=");
                stringBuilder.append(getfTXContinueOnXoff() ? '1' : '0');
                stringBuilder.append(", fOutX:1=");
                stringBuilder.append(getfOutX() ? '1' : '0');
                stringBuilder.append(", fInX:1=");
                stringBuilder.append(getfInX() ? '1' : '0');
                stringBuilder.append(", fErrorChar:1=");
                stringBuilder.append(getfErrorChar() ? '1' : '0');
                stringBuilder.append(", fNull:1=");
                stringBuilder.append(getfNull() ? '1' : '0');
                stringBuilder.append(", fRtsControl:2=");
                stringBuilder.append(getfRtsControl());
                stringBuilder.append(", fAbortOnError:1=");
                stringBuilder.append(getfAbortOnError() ? '1' : '0');
                stringBuilder.append(", fDummy2:17=");
                stringBuilder.append(getfDummy2());
                stringBuilder.append('>');
                return stringBuilder.toString();
            }

            public boolean getfAbortOnError() {
                return (this.intValue() & (0x01 << 14)) != 0x00;
            }

            public boolean getfBinary() {
                return (this.intValue() & 0x01) != 0x00;
            }

            public boolean getfDsrSensitivity() {
                return (this.intValue() & (0x01 << 6)) != 0x00;
            }

            public int getfDtrControl() {
                return (this.intValue() >>> 4) & 0x03;
            }

            public boolean getfErrorChar() {
                return (this.intValue() & (0x01 << 10)) != 0x00;
            }

            public boolean getfInX() {
                return (this.intValue() & (0x01 << 9)) != 0x00;
            }

            public boolean getfNull() {
                return (this.intValue() & (0x01 << 11)) != 0x00;
            }

            public boolean getfOutX() {
                return (this.intValue() & (0x01 << 8)) != 0x00;
            }

            public boolean getfOutxCtsFlow() {
                return (this.intValue() & (0x01 << 2)) != 0x00;
            }

            public boolean getfOutxDsrFlow() {
                return (this.intValue() & (0x01 << 3)) != 0x00;
            }

            public boolean getfParity() {
                return (this.intValue() & (0x01 << 1)) != 0x00;
            }

            public int getfRtsControl() {
                return (this.intValue() >>> 12) & 0x03;
            }

            public int getfDummy2() {
                return (this.intValue()>>>15) & 0x1FFFF;
            }

            public boolean getfTXContinueOnXoff() {
                return (this.intValue() & (0x01 << 7)) != 0x00;
            }

            /**
             * If this member is TRUE, the driver terminates all read and write
             * operations with an error status if an error occurs.<br>
             * The driver will not accept any further communications operations
             * until the application has acknowledged the error by calling the
             * ClearCommError function.
             *
             * @param fAbortOnError
             */
            public void setfAbortOnError(boolean fAbortOnError) {
                int tmp = leftShiftMask(fAbortOnError ? 1 : 0, (byte)14, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * If this member is TRUE, binary mode is enabled.<br>
             * Windows does not support nonbinary mode transfers, so this member
             * must be TRUE.
             *
             * @param fBinary
             */
            public void setfBinary(boolean fBinary) {
                int tmp = leftShiftMask(fBinary ? 1 : 0, (byte)0, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * If this member is TRUE, the communications driver is sensitive to the
             * state of the DSR signal.<br>
             * The driver ignores any bytes received, unless the DSR modem input
             * line is high.
             *
             * @param fDsrSensitivity
             */
            public void setfDsrSensitivity(boolean fDsrSensitivity) {
                int tmp = leftShiftMask(fDsrSensitivity ? 1 : 0, (byte)6, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * The DTR (data-terminal-ready) flow control. This member can be one of
             * the following values.
             * <li>{@link WinBase#DTR_CONTROL_DISABLE}</li>
             * <li>{@link WinBase#DTR_CONTROL_ENABLE}</li>
             * <li>{@link WinBase#DTR_CONTROL_HANDSHAKE}</li>
             *
             * @param fOutxDsrFlow
             *            value to set
             */
            public void setfDtrControl(int fOutxDsrFlow) {
                int tmp = leftShiftMask(fOutxDsrFlow, (byte)4, 0x03, this.intValue());
                this.setValue(tmp);
            }

            /**
             * Indicates whether bytes received with parity errors are replaced with
             * the character specified by the ErrorChar member.<br>
             * If this member is TRUE and the fParity member is TRUE, replacement
             * occurs.
             *
             * @param fErrorChar
             */
            public void setfErrorChar(boolean fErrorChar) {
                int tmp = leftShiftMask(fErrorChar ? 1 : 0, (byte)10, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * Indicates whether XON/XOFF flow control is used during reception.<br>
             * If this member is TRUE, the XoffChar character is sent when the input
             * buffer comes within XoffLim bytes of being full, and the XonChar
             * character is sent when the input buffer comes within XonLim bytes of
             * being empty.
             *
             * @param fInX
             */
            public void setfInX(boolean fInX) {
                int tmp = leftShiftMask(fInX ? 1 : 0, (byte)9, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * If this member is TRUE, null bytes are discarded when received.
             *
             * @param fNull
             */
            public void setfNull(boolean fNull) {
                int tmp = leftShiftMask(fNull ? 1 : 0, (byte)11, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * Indicates whether XON/XOFF flow control is used during transmission.
             * <br>
             * If this member is TRUE, transmission stops when the XoffChar
             * character is received and starts again when the XonChar character is
             * received.
             *
             * @param fOutX
             */
            public void setfOutX(boolean fOutX) {
                int tmp = leftShiftMask(fOutX ? 1 : 0, (byte)8, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * If this member is TRUE, the CTS (clear-to-send) signal is monitored
             * for output flow control.<br>
             * If this member is TRUE and CTS is turned off, output is suspended
             * until CTS is sent again.
             *
             * @param fOutxCtsFlow
             */
            public void setfOutxCtsFlow(boolean fOutxCtsFlow) {
                int tmp = leftShiftMask(fOutxCtsFlow ? 1 : 0, (byte)2, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * If this member is TRUE, the DSR (data-set-ready) signal is monitored
             * for output flow control.<br>
             * If this member is TRUE and DSR is turned off, output is suspended
             * until DSR is sent again.
             *
             * @param fOutxDsrFlow
             */
            public void setfOutxDsrFlow(boolean fOutxDsrFlow) {
                int tmp = leftShiftMask(fOutxDsrFlow ? 1 : 0, (byte)3, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             * If this member is TRUE, parity checking is performed and errors are
             * reported.
             *
             * @param fParity
             */
            public void setfParity(boolean fParity) {
                int tmp = leftShiftMask(fParity ? 1 : 0, (byte)1, 0x01, this.intValue());
                this.setValue(tmp);
            }

            /**
             *
             * The RTS (request-to-send) flow control. This member can be one of the
             * following values.
             * <li>{@link WinBase#RTS_CONTROL_DISABLE}</li>
             * <li>{@link WinBase#RTS_CONTROL_ENABLE}</li>
             * <li>{@link WinBase#RTS_CONTROL_HANDSHAKE}</li>
             * <li>{@link WinBase#RTS_CONTROL_TOGGLE}</li>
             *
             * @param fRtsControl
             */
            public void setfRtsControl(int fRtsControl) {
                int tmp = leftShiftMask(fRtsControl, (byte)12, 0x03, this.intValue());
                this.setValue(tmp);
            }

            /**
             * If this member is TRUE, transmission continues after the input buffer
             * has come within XoffLim bytes of being full and the driver has
             * transmitted the XoffChar character to stop receiving bytes.<br>
             * If this member is FALSE, transmission does not continue until the
             * input buffer is within XonLim bytes of being empty and the driver has
             * transmitted the XonChar character to resume reception.
             *
             * @param fTXContinueOnXoff
             */
            public void setfTXContinueOnXoff(boolean fTXContinueOnXoff) {
                int tmp = leftShiftMask(fTXContinueOnXoff ? 1 : 0, (byte)7, 0x01, this.intValue());
                this.setValue(tmp);
            }


            private static  int leftShiftMask(int valuetoset, byte shift, int mask, int storage) {
                int tmp = storage;
                tmp &= ~(mask << shift);
                tmp |= ((valuetoset & mask) << shift);
                return tmp;
            }
        }
        /**
         * The length of the structure, in bytes. The caller must set this
         * member to sizeof(DCB).
         */
        public DWORD DCBlength;

        /**
         *
         * The baud rate at which the communications device operates. This
         * member can be an actual baud rate value, or one of the following
         * indexes.
         * <li>{@link WinBase#CBR_110}</li>
         * <li>{@link WinBase#CBR_300}</li>
         * <li>{@link WinBase#CBR_600}</li>
         * <li>{@link WinBase#CBR_1200}</li>
         * <li>{@link WinBase#CBR_2400}</li>
         * <li>{@link WinBase#CBR_4800}</li>
         * <li>{@link WinBase#CBR_9600}</li>
         * <li>{@link WinBase#CBR_14400}</li>
         * <li>{@link WinBase#CBR_19200}</li>
         * <li>{@link WinBase#CBR_38400}</li>
         * <li>{@link WinBase#CBR_56000}</li>
         * <li>{@link WinBase#CBR_128000}</li>
         * <li>{@link WinBase#CBR_256000}</li>
         *
         */
        public DWORD BaudRate;

        /**
         * Contains all the bit wise setting entries.
         */
        public DCBControllBits controllBits;

        /**
         * Reserved; must be zero.
         */
        public WORD wReserved;

        /**
         * The minimum number of bytes in use allowed in the input buffer before
         * flow control is activated to allow transmission by the sender. This
         * assumes that either XON/XOFF, RTS, or DTR input flow control is
         * specified in the fInX, fRtsControl, or fDtrControl members.
         */
        public WORD XonLim;

        /**
         * The minimum number of free bytes allowed in the input buffer before
         * flow control is activated to inhibit the sender. Note that the sender
         * may transmit characters after the flow control signal has been
         * activated, so this value should never be zero. This assumes that
         * either XON/XOFF, RTS, or DTR input flow control is specified in the
         * fInX, fRtsControl, or fDtrControl members. The maximum number of
         * bytes in use allowed is calculated by subtracting this value from the
         * size, in bytes, of the input buffer.
         */
        public WORD XoffLim;

        /**
         * The number of bits in the bytes transmitted and received.
         */
        public BYTE ByteSize;

        /**
         *
         * The parity scheme to be used. This member can be one of the following
         * values.
         * <li>{@link WinBase#EVENPARITY}</li>
         * <li>{@link WinBase#ODDPARITY}</li>
         * <li>{@link WinBase#NOPARITY}</li>
         * <li>{@link WinBase#SPACEPARITY}</li>
         * <li>{@link WinBase#MARKPARITY}</li>
         */
        public BYTE Parity;

        /**
         * The number of stop bits to be used. This member can be one of the
         * following values.
         * <li>{@link WinBase#ONESTOPBIT}</li>
         * <li>{@link WinBase#ONE5STOPBITS}</li>
         * <li>{@link WinBase#TWOSTOPBITS}</li>
         */
        public BYTE StopBits;

        /**
         * The value of the XON character for both transmission and reception.
         */
        public char XonChar;

        /**
         * The value of the XOFF character for both transmission and reception.
         */
        public char XoffChar;

        /**
         * The value of the character used to replace bytes received with a
         * parity error.
         */
        public char ErrorChar;

        /**
         * The value of the character used to signal the end of data.
         */
        public char EofChar;

        /**
         * The value of the character used to signal an event.
         */
        public char EvtChar;

        /**
         * Reserved; do not use.
         */
        public WORD wReserved1;

        public DCB() {
            DCBlength = new DWORD(size());
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "DCBlength", "BaudRate", "controllBits", "wReserved", "XonLim",
                    "XoffLim", "ByteSize", "Parity", "StopBits", "XonChar", "XoffChar", "ErrorChar", "EofChar",
                    "EvtChar", "wReserved1" });
        }
    }

    /**
     * No parity.
     */
    int NOPARITY = 0;

    /**
     * Odd parity.
     */
    int ODDPARITY = 1;

    /**
     * Even parity.
     */
    int EVENPARITY = 2;

    /**
     * Mark parity.
     */
    int MARKPARITY = 3;

    /**
     * Space parity.
     */
    int SPACEPARITY = 4;

    /**
     * 1 stop bit.
     */
    int ONESTOPBIT = 0;

    /**
     * 1.5 stop bits.
     */
    int ONE5STOPBITS = 1;
    /**
     * 2 stop bits.
     */
    int TWOSTOPBITS = 2;
    /**
     * 110 bps.
     */
    int CBR_110 = 110;
    /**
     * 300 bps.
     */
    int CBR_300 = 300;
    /**
     * 600 bps.
     */
    int CBR_600 = 600;
    /**
     * 1200 bps.
     */
    int CBR_1200 = 1200;
    /**
     * 2400 bps.
     */
    int CBR_2400 = 2400;
    /**
     * 4800 bps.
     */
    int CBR_4800 = 4800;
    /**
     * 9600 bps.
     */
    int CBR_9600 = 9600;
    /**
     * 14400 bps.
     */
    int CBR_14400 = 14400;
    /**
     * 19200 bps.
     */
    int CBR_19200 = 19200;
    /**
     * 38400 bps.
     */
    int CBR_38400 = 38400;
    /**
     * 56000 bps.
     */
    int CBR_56000 = 56000;

    /**
     * 128000 bps.
     */
    int CBR_128000 = 128000;

    /**
     * 256000 bps.
     */
    int CBR_256000 = 256000;

    /**
     * Disables the DTR line when the device is opened and leaves it disabled.
     */
    int DTR_CONTROL_DISABLE = 0;

    /**
     * Enables the DTR line when the device is opened and leaves it on.
     */
    int DTR_CONTROL_ENABLE = 1;

    /**
     * Enables DTR handshaking.<br>
     * If handshaking is enabled, it is an error for the application to adjust
     * the line by using the EscapeCommFunction function.
     */
    int DTR_CONTROL_HANDSHAKE = 2;

    /**
     * Disables the RTS line when the device is opened and leaves it disabled.
     */
    int RTS_CONTROL_DISABLE = 0;

    /**
     * Enables the RTS line when the device is opened and leaves it on.
     */
    int RTS_CONTROL_ENABLE = 1;

    /**
     * Enables RTS handshaking.<br>
     * The driver raises the RTS line when the "type-ahead" (input) buffer is
     * less than one-half full and lowers the RTS line when the buffer is more
     * than three-quarters full.<br>
     * If handshaking is enabled, it is an error for the application to adjust
     * the line by using the EscapeCommFunction function.
     */
    int RTS_CONTROL_HANDSHAKE = 2;

    /**
     * Specifies that the RTS line will be high if bytes are available for
     * transmission.<br>
     * After all buffered bytes have been sent, the RTS line will be low.
     */
    int RTS_CONTROL_TOGGLE = 3;;

    /**
     * An application-defined callback function used with the EnumResourceTypes
     * and EnumResourceTypesEx functions. <br>
     * It receives resource types. <br>
     * The ENUMRESTYPEPROC type defines a pointer to this callback function.
     * <br>
     * EnumResTypeProc is a placeholder for the application-defined function
     * name.
     */
    interface EnumResTypeProc extends Callback {
        /**
         * @param module
         *            A handle to the module whose executable file contains the
         *            resources for which the types are to be enumerated. <br>
         *            If this parameter is NULL, the function enumerates the
         *            resource types in the module used to create the current
         *            process.
         * @param type
         *            The type of resource for which the type is being
         *            enumerated. <br>
         *            Alternately, rather than a pointer, this parameter can be
         *            MAKEINTRESOURCE(ID), where ID is the integer identifier of
         *            the given resource type. <br>
         *            For standard resource types, see Resource Types.<br>
         *            For more information, see the Remarks section below.
         * @param lParam
         *            An application-defined parameter passed to the
         *            EnumResourceTypes or EnumResourceTypesEx function.<br>
         *            This parameter can be used in error checking.
         * @return Returns TRUE to continue enumeration or FALSE to stop
         *         enumeration.
         */
        boolean invoke(HMODULE module, Pointer type, Pointer lParam);
    }

    /**
     * An application-defined callback function used with the EnumResourceNames
     * and EnumResourceNamesEx functions. <br>
     * It receives the type and name of a resource. <br>
     * The ENUMRESNAMEPROC type defines a pointer to this callback function.
     * <br>
     * EnumResNameProc is a placeholder for the application-defined function
     * name.
     */
    interface EnumResNameProc extends Callback {
        /**
         * @param module
         *            A handle to the module whose executable file contains the
         *            resources that are being enumerated. <br>
         *            If this parameter is NULL, the function enumerates the
         *            resource names in the module used to create the current
         *            process.
         * @param type
         *            The type of resource for which the name is being
         *            enumerated. <br>
         *            Alternately, rather than a pointer, this parameter can be
         *            <code>MAKEINTRESOURCE(ID)</code>, where ID is an integer
         *            value representing a predefined resource type. <br>
         *            For standard resource types, see <a href=
         *            "https://msdn.microsoft.com/en-us/library/windows/desktop/ms648009(v=vs.85).aspx">
         *            Resource Types</a>. <br>
         *            For more information, see the Remarks section below.
         * @param name
         *            The name of a resource of the type being enumerated.<br>
         *            Alternately, rather than a pointer, this parameter can be
         *            <code>MAKEINTRESOURCE(ID)</code>, where ID is the integer
         *            identifier of the resource.<br>
         *            For more information, see the Remarks section below.
         * @param lParam
         *            An application-defined parameter passed to the
         *            EnumResourceNames or EnumResourceNamesEx function. <br>
         *            This parameter can be used in error checking.
         * @return Returns TRUE to continue enumeration or FALSE to stop
         *         enumeration.
         */
        boolean invoke(HMODULE module, Pointer type, Pointer name, Pointer lParam);
    }
    
    /**
     * Enables away mode. This value must be specified with {@link #ES_CONTINUOUS}.
     *
     * Away mode should be used only by media-recording and media-distribution
     * applications that must perform critical background processing on desktop
     * computers while the computer appears to be sleeping. See Remarks.
     */
    int ES_AWAYMODE_REQUIRED = 0x00000040;
    /**
     * Informs the system that the state being set should remain in effect until
     * the next call that uses ES_CONTINUOUS and one of the other state flags is
     * cleared.
     */
    int ES_CONTINUOUS = 0x80000000;
    /**
     * Forces the display to be on by resetting the display idle timer.
     */
    int ES_DISPLAY_REQUIRED = 0x00000002;
    /**
     * Forces the system to be in the working state by resetting the system idle
     * timer.
     */
    int ES_SYSTEM_REQUIRED = 0x00000001;
    /**
     * This value is not supported. If ES_USER_PRESENT is combined with other
     * esFlags values, the call will fail and none of the specified states will
     * be set.
     */
    int ES_USER_PRESENT = 0x00000004;
}
