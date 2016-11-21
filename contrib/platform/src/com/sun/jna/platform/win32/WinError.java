/* Copyright (c) 2011 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.platform.win32.WinNT.HRESULT;
/**
 * Error code definitions for the Win32 API functions.
 * Ported from Windows SDK 6.0A
 * @author dblock[at]dblock.org
 */
public interface WinError {

    //
    //  Values are 32 bit values laid out as follows:
    //
    //   3 3 2 2 2 2 2 2 2 2 2 2 1 1 1 1 1 1 1 1 1 1
    //   1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0
    //  +---+-+-+-----------------------+-------------------------------+
    //  |Sev|C|R|     Facility          |               Code            |
    //  +---+-+-+-----------------------+-------------------------------+
    //
    //  where
    //
    //      Sev - is the severity code
    //
    //          00 - Success
    //          01 - Informational
    //          10 - Warning
    //          11 - Error
    //
    //      C - is the Customer code flag
    //
    //      R - is a reserved bit
    //
    //      Facility - is the facility code
    //
    //      Code - is the facility's status code

    //
    // Define the facility codes
    //
    short FACILITY_WINRM = 51;
    short FACILITY_WINDOWSUPDATE = 36;
    short FACILITY_WINDOWS_DEFENDER = 80;
    short FACILITY_WINDOWS_CE = 24;
    short FACILITY_WINDOWS = 8;
    short FACILITY_URT = 19;
    short FACILITY_UMI = 22;
    short FACILITY_TPM_SOFTWARE = 41;
    short FACILITY_TPM_SERVICES = 40;
    short FACILITY_SXS = 23;
    short FACILITY_STORAGE = 3;
    short FACILITY_STATE_MANAGEMENT = 34;
    short FACILITY_SSPI = 9;
    short FACILITY_SCARD = 16;
    short FACILITY_SHELL = 39;
    short FACILITY_SETUPAPI = 15;
    short FACILITY_SECURITY = 9;
    short FACILITY_RPC = 1;
    short FACILITY_PLA = 48;
    short FACILITY_WIN32 = 7;
    short FACILITY_CONTROL = 10;
    short FACILITY_NULL = 0;
    short FACILITY_NDIS = 52;
    short FACILITY_METADIRECTORY = 35;
    short FACILITY_MSMQ = 14;
    short FACILITY_MEDIASERVER = 13;
    short FACILITY_INTERNET = 12;
    short FACILITY_ITF = 4;
    short FACILITY_USERMODE_HYPERVISOR = 53;
    short FACILITY_HTTP = 25;
    short FACILITY_GRAPHICS = 38;
    short FACILITY_FWP = 50;
    short FACILITY_FVE = 49;
    short FACILITY_USERMODE_FILTER_MANAGER = 31;
    short FACILITY_DPLAY = 21;
    short FACILITY_DISPATCH = 2;
    short FACILITY_DIRECTORYSERVICE = 37;
    short FACILITY_CONFIGURATION = 33;
    short FACILITY_COMPLUS = 17;
    short FACILITY_USERMODE_COMMONLOG = 26;
    short FACILITY_CMI = 54;
    short FACILITY_CERT = 11;
    short FACILITY_BACKGROUNDCOPY = 32;
    short FACILITY_ACS = 20;
    short FACILITY_AAF = 18;

    //
    // Define the severity codes
    //

    //
    // MessageId: ERROR_SUCCESS
    //
    // MessageText:
    //
    // The operation completed successfully.
    //
    int ERROR_SUCCESS = 0;

    int NO_ERROR = 0; // dderror
    int SEC_E_OK = 0;
    //
    // MessageId: ERROR_INVALID_FUNCTION
    //
    // MessageText:
    //
    // Incorrect function.
    //
    int ERROR_INVALID_FUNCTION = 1; // dderror

    //
    // MessageId: ERROR_FILE_NOT_FOUND
    //
    // MessageText:
    //
    // The system cannot find the file specified.
    //
    int ERROR_FILE_NOT_FOUND = 2;

    //
    // MessageId: ERROR_PATH_NOT_FOUND
    //
    // MessageText:
    //
    // The system cannot find the path specified.
    //
    int ERROR_PATH_NOT_FOUND = 3;

    //
    // MessageId: ERROR_TOO_MANY_OPEN_FILES
    //
    // MessageText:
    //
    // The system cannot open the file.
    //
    int ERROR_TOO_MANY_OPEN_FILES = 4;

    //
    // MessageId: ERROR_ACCESS_DENIED
    //
    // MessageText:
    //
    // Access is denied.
    //
    int ERROR_ACCESS_DENIED = 5;

    //
    // MessageId: ERROR_INVALID_HANDLE
    //
    // MessageText:
    //
    // The handle is invalid.
    //
    int ERROR_INVALID_HANDLE = 6;

    //
    // MessageId: ERROR_ARENA_TRASHED
    //
    // MessageText:
    //
    // The storage control blocks were destroyed.
    //
    int ERROR_ARENA_TRASHED = 7;

    //
    // MessageId: ERROR_NOT_ENOUGH_MEMORY
    //
    // MessageText:
    //
    // Not enough storage is available to process this command.
    //
    int ERROR_NOT_ENOUGH_MEMORY = 8; // dderror

    //
    // MessageId: ERROR_INVALID_BLOCK
    //
    // MessageText:
    //
    // The storage control block address is invalid.
    //
    int ERROR_INVALID_BLOCK = 9;

    //
    // MessageId: ERROR_BAD_ENVIRONMENT
    //
    // MessageText:
    //
    // The environment is incorrect.
    //
    int ERROR_BAD_ENVIRONMENT = 10;

    //
    // MessageId: ERROR_BAD_FORMAT
    //
    // MessageText:
    //
    // An attempt was made to load a program with an incorrect format.
    //
    int ERROR_BAD_FORMAT = 11;

    //
    // MessageId: ERROR_INVALID_ACCESS
    //
    // MessageText:
    //
    // The access code is invalid.
    //
    int ERROR_INVALID_ACCESS = 12;

    //
    // MessageId: ERROR_INVALID_DATA
    //
    // MessageText:
    //
    // The data is invalid.
    //
    int ERROR_INVALID_DATA = 13;

    //
    // MessageId: ERROR_OUTOFMEMORY
    //
    // MessageText:
    //
    // Not enough storage is available to complete this operation.
    //
    int ERROR_OUTOFMEMORY = 14;

    //
    // MessageId: ERROR_INVALID_DRIVE
    //
    // MessageText:
    //
    // The system cannot find the drive specified.
    //
    int ERROR_INVALID_DRIVE = 15;

    //
    // MessageId: ERROR_CURRENT_DIRECTORY
    //
    // MessageText:
    //
    // The directory cannot be removed.
    //
    int ERROR_CURRENT_DIRECTORY = 16;

    //
    // MessageId: ERROR_NOT_SAME_DEVICE
    //
    // MessageText:
    //
    // The system cannot move the file to a different disk drive.
    //
    int ERROR_NOT_SAME_DEVICE = 17;

    //
    // MessageId: ERROR_NO_MORE_FILES
    //
    // MessageText:
    //
    // There are no more files.
    //
    int ERROR_NO_MORE_FILES = 18;

    //
    // MessageId: ERROR_WRITE_PROTECT
    //
    // MessageText:
    //
    // The media is write protected.
    //
    int ERROR_WRITE_PROTECT = 19;

    //
    // MessageId: ERROR_BAD_UNIT
    //
    // MessageText:
    //
    // The system cannot find the device specified.
    //
    int ERROR_BAD_UNIT = 20;

    //
    // MessageId: ERROR_NOT_READY
    //
    // MessageText:
    //
    // The device is not ready.
    //
    int ERROR_NOT_READY = 21;

    //
    // MessageId: ERROR_BAD_COMMAND
    //
    // MessageText:
    //
    // The device does not recognize the command.
    //
    int ERROR_BAD_COMMAND = 22;

    //
    // MessageId: ERROR_CRC
    //
    // MessageText:
    //
    // Data error (cyclic redundancy check).
    //
    int ERROR_CRC = 23;

    //
    // MessageId: ERROR_BAD_LENGTH
    //
    // MessageText:
    //
    // The program issued a command but the command length is incorrect.
    //
    int ERROR_BAD_LENGTH = 24;

    //
    // MessageId: ERROR_SEEK
    //
    // MessageText:
    //
    // The drive cannot locate a specific area or track on the disk.
    //
    int ERROR_SEEK = 25;

    //
    // MessageId: ERROR_NOT_DOS_DISK
    //
    // MessageText:
    //
    // The specified disk or diskette cannot be accessed.
    //
    int ERROR_NOT_DOS_DISK = 26;

    //
    // MessageId: ERROR_SECTOR_NOT_FOUND
    //
    // MessageText:
    //
    // The drive cannot find the sector requested.
    //
    int ERROR_SECTOR_NOT_FOUND = 27;

    //
    // MessageId: ERROR_OUT_OF_PAPER
    //
    // MessageText:
    //
    // The printer is out of paper.
    //
    int ERROR_OUT_OF_PAPER = 28;

    //
    // MessageId: ERROR_WRITE_FAULT
    //
    // MessageText:
    //
    // The system cannot write to the specified device.
    //
    int ERROR_WRITE_FAULT = 29;

    //
    // MessageId: ERROR_READ_FAULT
    //
    // MessageText:
    //
    // The system cannot read from the specified device.
    //
    int ERROR_READ_FAULT = 30;

    //
    // MessageId: ERROR_GEN_FAILURE
    //
    // MessageText:
    //
    // A device attached to the system is not functioning.
    //
    int ERROR_GEN_FAILURE = 31;

    //
    // MessageId: ERROR_SHARING_VIOLATION
    //
    // MessageText:
    //
    // The process cannot access the file because it is being used by another
    // process.
    //
    int ERROR_SHARING_VIOLATION = 32;

    //
    // MessageId: ERROR_LOCK_VIOLATION
    //
    // MessageText:
    //
    // The process cannot access the file because another process has locked a
    // portion of the file.
    //
    int ERROR_LOCK_VIOLATION = 33;

    //
    // MessageId: ERROR_WRONG_DISK
    //
    // MessageText:
    //
    // The wrong diskette is in the drive.
    // Insert %2 (Volume Serial Number: %3) into drive %1.
    //
    int ERROR_WRONG_DISK = 34;

    //
    // MessageId: ERROR_SHARING_BUFFER_EXCEEDED
    //
    // MessageText:
    //
    // Too many files opened for sharing.
    //
    int ERROR_SHARING_BUFFER_EXCEEDED = 36;

    //
    // MessageId: ERROR_HANDLE_EOF
    //
    // MessageText:
    //
    // Reached the end of the file.
    //
    int ERROR_HANDLE_EOF = 38;

    //
    // MessageId: ERROR_HANDLE_DISK_FULL
    //
    // MessageText:
    //
    // The disk is full.
    //
    int ERROR_HANDLE_DISK_FULL = 39;

    //
    // MessageId: ERROR_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The request is not supported.
    //
    int ERROR_NOT_SUPPORTED = 50;

    //
    // MessageId: ERROR_REM_NOT_LIST
    //
    // MessageText:
    //
    // Windows cannot find the network path. Verify that the network path is
    // correct and the destination computer is not busy or turned off. If
    // Windows still cannot find the network path, contact your network
    // administrator.
    //
    int ERROR_REM_NOT_LIST = 51;

    //
    // MessageId: ERROR_DUP_NAME
    //
    // MessageText:
    //
    // You were not connected because a duplicate name exists on the network. If
    // joining a domain, go to System in Control Panel to change the computer
    // name and try again. If joining a workgroup, choose another workgroup
    // name.
    //
    int ERROR_DUP_NAME = 52;

    //
    // MessageId: ERROR_BAD_NETPATH
    //
    // MessageText:
    //
    // The network path was not found.
    //
    int ERROR_BAD_NETPATH = 53;

    //
    // MessageId: ERROR_NETWORK_BUSY
    //
    // MessageText:
    //
    // The network is busy.
    //
    int ERROR_NETWORK_BUSY = 54;

    //
    // MessageId: ERROR_DEV_NOT_EXIST
    //
    // MessageText:
    //
    // The specified network resource or device is no longer available.
    //
    int ERROR_DEV_NOT_EXIST = 55; // dderror

    //
    // MessageId: ERROR_TOO_MANY_CMDS
    //
    // MessageText:
    //
    // The network BIOS command limit has been reached.
    //
    int ERROR_TOO_MANY_CMDS = 56;

    //
    // MessageId: ERROR_ADAP_HDW_ERR
    //
    // MessageText:
    //
    // A network adapter hardware error occurred.
    //
    int ERROR_ADAP_HDW_ERR = 57;

    //
    // MessageId: ERROR_BAD_NET_RESP
    //
    // MessageText:
    //
    // The specified server cannot perform the requested operation.
    //
    int ERROR_BAD_NET_RESP = 58;

    //
    // MessageId: ERROR_UNEXP_NET_ERR
    //
    // MessageText:
    //
    // An unexpected network error occurred.
    //
    int ERROR_UNEXP_NET_ERR = 59;

    //
    // MessageId: ERROR_BAD_REM_ADAP
    //
    // MessageText:
    //
    // The remote adapter is not compatible.
    //
    int ERROR_BAD_REM_ADAP = 60;

    //
    // MessageId: ERROR_PRINTQ_FULL
    //
    // MessageText:
    //
    // The printer queue is full.
    //
    int ERROR_PRINTQ_FULL = 61;

    //
    // MessageId: ERROR_NO_SPOOL_SPACE
    //
    // MessageText:
    //
    // Space to store the file waiting to be printed is not available on the
    // server.
    //
    int ERROR_NO_SPOOL_SPACE = 62;

    //
    // MessageId: ERROR_PRINT_CANCELLED
    //
    // MessageText:
    //
    // Your file waiting to be printed was deleted.
    //
    int ERROR_PRINT_CANCELLED = 63;

    //
    // MessageId: ERROR_NETNAME_DELETED
    //
    // MessageText:
    //
    // The specified network name is no longer available.
    //
    int ERROR_NETNAME_DELETED = 64;

    //
    // MessageId: ERROR_NETWORK_ACCESS_DENIED
    //
    // MessageText:
    //
    // Network access is denied.
    //
    int ERROR_NETWORK_ACCESS_DENIED = 65;

    //
    // MessageId: ERROR_BAD_DEV_TYPE
    //
    // MessageText:
    //
    // The network resource type is not correct.
    //
    int ERROR_BAD_DEV_TYPE = 66;

    //
    // MessageId: ERROR_BAD_NET_NAME
    //
    // MessageText:
    //
    // The network name cannot be found.
    //
    int ERROR_BAD_NET_NAME = 67;

    //
    // MessageId: ERROR_TOO_MANY_NAMES
    //
    // MessageText:
    //
    // The name limit for the local computer network adapter card was exceeded.
    //
    int ERROR_TOO_MANY_NAMES = 68;

    //
    // MessageId: ERROR_TOO_MANY_SESS
    //
    // MessageText:
    //
    // The network BIOS session limit was exceeded.
    //
    int ERROR_TOO_MANY_SESS = 69;

    //
    // MessageId: ERROR_SHARING_PAUSED
    //
    // MessageText:
    //
    // The remote server has been paused or is in the process of being started.
    //
    int ERROR_SHARING_PAUSED = 70;

    //
    // MessageId: ERROR_REQ_NOT_ACCEP
    //
    // MessageText:
    //
    // No more connections can be made to this remote computer at this time
    // because there are already as many connections as the computer can accept.
    //
    int ERROR_REQ_NOT_ACCEP = 71;

    //
    // MessageId: ERROR_REDIR_PAUSED
    //
    // MessageText:
    //
    // The specified printer or disk device has been paused.
    //
    int ERROR_REDIR_PAUSED = 72;

    //
    // MessageId: ERROR_FILE_EXISTS
    //
    // MessageText:
    //
    // The file exists.
    //
    int ERROR_FILE_EXISTS = 80;

    //
    // MessageId: ERROR_CANNOT_MAKE
    //
    // MessageText:
    //
    // The directory or file cannot be created.
    //
    int ERROR_CANNOT_MAKE = 82;

    //
    // MessageId: ERROR_FAIL_I24
    //
    // MessageText:
    //
    // Fail on INT=24.
    //
    int ERROR_FAIL_I24 = 83;

    //
    // MessageId: ERROR_OUT_OF_STRUCTURES
    //
    // MessageText:
    //
    // Storage to process this request is not available.
    //
    int ERROR_OUT_OF_STRUCTURES = 84;

    //
    // MessageId: ERROR_ALREADY_ASSIGNED
    //
    // MessageText:
    //
    // The local device name is already in use.
    //
    int ERROR_ALREADY_ASSIGNED = 85;

    //
    // MessageId: ERROR_INVALID_PASSWORD
    //
    // MessageText:
    //
    // The specified network password is not correct.
    //
    int ERROR_INVALID_PASSWORD = 86;

    //
    // MessageId: ERROR_INVALID_PARAMETER
    //
    // MessageText:
    //
    // The parameter is incorrect.
    //
    int ERROR_INVALID_PARAMETER = 87; // dderror

    //
    // MessageId: ERROR_NET_WRITE_FAULT
    //
    // MessageText:
    //
    // A write fault occurred on the network.
    //
    int ERROR_NET_WRITE_FAULT = 88;

    //
    // MessageId: ERROR_NO_PROC_SLOTS
    //
    // MessageText:
    //
    // The system cannot start another process at this time.
    //
    int ERROR_NO_PROC_SLOTS = 89;

    //
    // MessageId: ERROR_TOO_MANY_SEMAPHORES
    //
    // MessageText:
    //
    // Cannot create another system semaphore.
    //
    int ERROR_TOO_MANY_SEMAPHORES = 100;

    //
    // MessageId: ERROR_EXCL_SEM_ALREADY_OWNED
    //
    // MessageText:
    //
    // The exclusive semaphore is owned by another process.
    //
    int ERROR_EXCL_SEM_ALREADY_OWNED = 101;

    //
    // MessageId: ERROR_SEM_IS_SET
    //
    // MessageText:
    //
    // The semaphore is set and cannot be closed.
    //
    int ERROR_SEM_IS_SET = 102;

    //
    // MessageId: ERROR_TOO_MANY_SEM_REQUESTS
    //
    // MessageText:
    //
    // The semaphore cannot be set again.
    //
    int ERROR_TOO_MANY_SEM_REQUESTS = 103;

    //
    // MessageId: ERROR_INVALID_AT_INTERRUPT_TIME
    //
    // MessageText:
    //
    // Cannot request exclusive semaphores at interrupt time.
    //
    int ERROR_INVALID_AT_INTERRUPT_TIME = 104;

    //
    // MessageId: ERROR_SEM_OWNER_DIED
    //
    // MessageText:
    //
    // The previous ownership of this semaphore has ended.
    //
    int ERROR_SEM_OWNER_DIED = 105;

    //
    // MessageId: ERROR_SEM_USER_LIMIT
    //
    // MessageText:
    //
    // Insert the diskette for drive %1.
    //
    int ERROR_SEM_USER_LIMIT = 106;

    //
    // MessageId: ERROR_DISK_CHANGE
    //
    // MessageText:
    //
    // The program stopped because an alternate diskette was not inserted.
    //
    int ERROR_DISK_CHANGE = 107;

    //
    // MessageId: ERROR_DRIVE_LOCKED
    //
    // MessageText:
    //
    // The disk is in use or locked by another process.
    //
    int ERROR_DRIVE_LOCKED = 108;

    //
    // MessageId: ERROR_BROKEN_PIPE
    //
    // MessageText:
    //
    // The pipe has been ended.
    //
    int ERROR_BROKEN_PIPE = 109;

    //
    // MessageId: ERROR_OPEN_FAILED
    //
    // MessageText:
    //
    // The system cannot open the device or file specified.
    //
    int ERROR_OPEN_FAILED = 110;

    //
    // MessageId: ERROR_BUFFER_OVERFLOW
    //
    // MessageText:
    //
    // The file name is too long.
    //
    int ERROR_BUFFER_OVERFLOW = 111;

    //
    // MessageId: ERROR_DISK_FULL
    //
    // MessageText:
    //
    // There is not enough space on the disk.
    //
    int ERROR_DISK_FULL = 112;

    //
    // MessageId: ERROR_NO_MORE_SEARCH_HANDLES
    //
    // MessageText:
    //
    // No more internal file identifiers available.
    //
    int ERROR_NO_MORE_SEARCH_HANDLES = 113;

    //
    // MessageId: ERROR_INVALID_TARGET_HANDLE
    //
    // MessageText:
    //
    // The target internal file identifier is incorrect.
    //
    int ERROR_INVALID_TARGET_HANDLE = 114;

    //
    // MessageId: ERROR_INVALID_CATEGORY
    //
    // MessageText:
    //
    // The IOCTL call made by the application program is not correct.
    //
    int ERROR_INVALID_CATEGORY = 117;

    //
    // MessageId: ERROR_INVALID_VERIFY_SWITCH
    //
    // MessageText:
    //
    // The verify-on-write switch parameter value is not correct.
    //
    int ERROR_INVALID_VERIFY_SWITCH = 118;

    //
    // MessageId: ERROR_BAD_DRIVER_LEVEL
    //
    // MessageText:
    //
    // The system does not support the command requested.
    //
    int ERROR_BAD_DRIVER_LEVEL = 119;

    //
    // MessageId: ERROR_CALL_NOT_IMPLEMENTED
    //
    // MessageText:
    //
    // This function is not supported on this system.
    //
    int ERROR_CALL_NOT_IMPLEMENTED = 120;

    //
    // MessageId: ERROR_SEM_TIMEOUT
    //
    // MessageText:
    //
    // The semaphore timeout period has expired.
    //
    int ERROR_SEM_TIMEOUT = 121;

    //
    // MessageId: ERROR_INSUFFICIENT_BUFFER
    //
    // MessageText:
    //
    // The data area passed to a system call is too small.
    //
    int ERROR_INSUFFICIENT_BUFFER = 122; // dderror

    //
    // MessageId: ERROR_INVALID_NAME
    //
    // MessageText:
    //
    // The filename, directory name, or volume label syntax is incorrect.
    //
    int ERROR_INVALID_NAME = 123; // dderror

    //
    // MessageId: ERROR_INVALID_LEVEL
    //
    // MessageText:
    //
    // The system call level is not correct.
    //
    int ERROR_INVALID_LEVEL = 124;

    //
    // MessageId: ERROR_NO_VOLUME_LABEL
    //
    // MessageText:
    //
    // The disk has no volume label.
    //
    int ERROR_NO_VOLUME_LABEL = 125;

    //
    // MessageId: ERROR_MOD_NOT_FOUND
    //
    // MessageText:
    //
    // The specified module could not be found.
    //
    int ERROR_MOD_NOT_FOUND = 126;

    //
    // MessageId: ERROR_PROC_NOT_FOUND
    //
    // MessageText:
    //
    // The specified procedure could not be found.
    //
    int ERROR_PROC_NOT_FOUND = 127;

    //
    // MessageId: ERROR_WAIT_NO_CHILDREN
    //
    // MessageText:
    //
    // There are no child processes to wait for.
    //
    int ERROR_WAIT_NO_CHILDREN = 128;

    //
    // MessageId: ERROR_CHILD_NOT_COMPLETE
    //
    // MessageText:
    //
    // The %1 application cannot be run in Win32 mode.
    //
    int ERROR_CHILD_NOT_COMPLETE = 129;

    //
    // MessageId: ERROR_DIRECT_ACCESS_HANDLE
    //
    // MessageText:
    //
    // Attempt to use a file handle to an open disk partition for an operation
    // other than raw disk I/O.
    //
    int ERROR_DIRECT_ACCESS_HANDLE = 130;

    //
    // MessageId: ERROR_NEGATIVE_SEEK
    //
    // MessageText:
    //
    // An attempt was made to move the file pointer before the beginning of the
    // file.
    //
    int ERROR_NEGATIVE_SEEK = 131;

    //
    // MessageId: ERROR_SEEK_ON_DEVICE
    //
    // MessageText:
    //
    // The file pointer cannot be set on the specified device or file.
    //
    int ERROR_SEEK_ON_DEVICE = 132;

    //
    // MessageId: ERROR_IS_JOIN_TARGET
    //
    // MessageText:
    //
    // A JOIN or SUBST command cannot be used for a drive that contains
    // previously joined drives.
    //
    int ERROR_IS_JOIN_TARGET = 133;

    //
    // MessageId: ERROR_IS_JOINED
    //
    // MessageText:
    //
    // An attempt was made to use a JOIN or SUBST command on a drive that has
    // already been joined.
    //
    int ERROR_IS_JOINED = 134;

    //
    // MessageId: ERROR_IS_SUBSTED
    //
    // MessageText:
    //
    // An attempt was made to use a JOIN or SUBST command on a drive that has
    // already been substituted.
    //
    int ERROR_IS_SUBSTED = 135;

    //
    // MessageId: ERROR_NOT_JOINED
    //
    // MessageText:
    //
    // The system tried to delete the JOIN of a drive that is not joined.
    //
    int ERROR_NOT_JOINED = 136;

    //
    // MessageId: ERROR_NOT_SUBSTED
    //
    // MessageText:
    //
    // The system tried to delete the substitution of a drive that is not
    // substituted.
    //
    int ERROR_NOT_SUBSTED = 137;

    //
    // MessageId: ERROR_JOIN_TO_JOIN
    //
    // MessageText:
    //
    // The system tried to join a drive to a directory on a joined drive.
    //
    int ERROR_JOIN_TO_JOIN = 138;

    //
    // MessageId: ERROR_SUBST_TO_SUBST
    //
    // MessageText:
    //
    // The system tried to substitute a drive to a directory on a substituted
    // drive.
    //
    int ERROR_SUBST_TO_SUBST = 139;

    //
    // MessageId: ERROR_JOIN_TO_SUBST
    //
    // MessageText:
    //
    // The system tried to join a drive to a directory on a substituted drive.
    //
    int ERROR_JOIN_TO_SUBST = 140;

    //
    // MessageId: ERROR_SUBST_TO_JOIN
    //
    // MessageText:
    //
    // The system tried to SUBST a drive to a directory on a joined drive.
    //
    int ERROR_SUBST_TO_JOIN = 141;

    //
    // MessageId: ERROR_BUSY_DRIVE
    //
    // MessageText:
    //
    // The system cannot perform a JOIN or SUBST at this time.
    //
    int ERROR_BUSY_DRIVE = 142;

    //
    // MessageId: ERROR_SAME_DRIVE
    //
    // MessageText:
    //
    // The system cannot join or substitute a drive to or for a directory on the
    // same drive.
    //
    int ERROR_SAME_DRIVE = 143;

    //
    // MessageId: ERROR_DIR_NOT_ROOT
    //
    // MessageText:
    //
    // The directory is not a subdirectory of the root directory.
    //
    int ERROR_DIR_NOT_ROOT = 144;

    //
    // MessageId: ERROR_DIR_NOT_EMPTY
    //
    // MessageText:
    //
    // The directory is not empty.
    //
    int ERROR_DIR_NOT_EMPTY = 145;

    //
    // MessageId: ERROR_IS_SUBST_PATH
    //
    // MessageText:
    //
    // The path specified is being used in a substitute.
    //
    int ERROR_IS_SUBST_PATH = 146;

    //
    // MessageId: ERROR_IS_JOIN_PATH
    //
    // MessageText:
    //
    // Not enough resources are available to process this command.
    //
    int ERROR_IS_JOIN_PATH = 147;

    //
    // MessageId: ERROR_PATH_BUSY
    //
    // MessageText:
    //
    // The path specified cannot be used at this time.
    //
    int ERROR_PATH_BUSY = 148;

    //
    // MessageId: ERROR_IS_SUBST_TARGET
    //
    // MessageText:
    //
    // An attempt was made to join or substitute a drive for which a directory
    // on the drive is the target of a previous substitute.
    //
    int ERROR_IS_SUBST_TARGET = 149;

    //
    // MessageId: ERROR_SYSTEM_TRACE
    //
    // MessageText:
    //
    // System trace information was not specified in your CONFIG.SYS file, or
    // tracing is disallowed.
    //
    int ERROR_SYSTEM_TRACE = 150;

    //
    // MessageId: ERROR_INVALID_EVENT_COUNT
    //
    // MessageText:
    //
    // The number of specified semaphore events for DosMuxSemWait is not
    // correct.
    //
    int ERROR_INVALID_EVENT_COUNT = 151;

    //
    // MessageId: ERROR_TOO_MANY_MUXWAITERS
    //
    // MessageText:
    //
    // DosMuxSemWait did not execute; too many semaphores are already set.
    //
    int ERROR_TOO_MANY_MUXWAITERS = 152;

    //
    // MessageId: ERROR_INVALID_LIST_FORMAT
    //
    // MessageText:
    //
    // The DosMuxSemWait list is not correct.
    //
    int ERROR_INVALID_LIST_FORMAT = 153;

    //
    // MessageId: ERROR_LABEL_TOO_LONG
    //
    // MessageText:
    //
    // The volume label you entered exceeds the label character limit of the
    // target file system.
    //
    int ERROR_LABEL_TOO_LONG = 154;

    //
    // MessageId: ERROR_TOO_MANY_TCBS
    //
    // MessageText:
    //
    // Cannot create another thread.
    //
    int ERROR_TOO_MANY_TCBS = 155;

    //
    // MessageId: ERROR_SIGNAL_REFUSED
    //
    // MessageText:
    //
    // The recipient process has refused the signal.
    //
    int ERROR_SIGNAL_REFUSED = 156;

    //
    // MessageId: ERROR_DISCARDED
    //
    // MessageText:
    //
    // The segment is already discarded and cannot be locked.
    //
    int ERROR_DISCARDED = 157;

    //
    // MessageId: ERROR_NOT_LOCKED
    //
    // MessageText:
    //
    // The segment is already unlocked.
    //
    int ERROR_NOT_LOCKED = 158;

    //
    // MessageId: ERROR_BAD_THREADID_ADDR
    //
    // MessageText:
    //
    // The address for the thread ID is not correct.
    //
    int ERROR_BAD_THREADID_ADDR = 159;

    //
    // MessageId: ERROR_BAD_ARGUMENTS
    //
    // MessageText:
    //
    // One or more arguments are not correct.
    //
    int ERROR_BAD_ARGUMENTS = 160;

    //
    // MessageId: ERROR_BAD_PATHNAME
    //
    // MessageText:
    //
    // The specified path is invalid.
    //
    int ERROR_BAD_PATHNAME = 161;

    //
    // MessageId: ERROR_SIGNAL_PENDING
    //
    // MessageText:
    //
    // A signal is already pending.
    //
    int ERROR_SIGNAL_PENDING = 162;

    //
    // MessageId: ERROR_MAX_THRDS_REACHED
    //
    // MessageText:
    //
    // No more threads can be created in the system.
    //
    int ERROR_MAX_THRDS_REACHED = 164;

    //
    // MessageId: ERROR_LOCK_FAILED
    //
    // MessageText:
    //
    // Unable to lock a region of a file.
    //
    int ERROR_LOCK_FAILED = 167;

    //
    // MessageId: ERROR_BUSY
    //
    // MessageText:
    //
    // The requested resource is in use.
    //
    int ERROR_BUSY = 170; // dderror

    //
    // MessageId: ERROR_CANCEL_VIOLATION
    //
    // MessageText:
    //
    // A lock request was not outstanding for the supplied cancel region.
    //
    int ERROR_CANCEL_VIOLATION = 173;

    //
    // MessageId: ERROR_ATOMIC_LOCKS_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The file system does not support atomic changes to the lock type.
    //
    int ERROR_ATOMIC_LOCKS_NOT_SUPPORTED = 174;

    //
    // MessageId: ERROR_INVALID_SEGMENT_NUMBER
    //
    // MessageText:
    //
    // The system detected a segment number that was not correct.
    //
    int ERROR_INVALID_SEGMENT_NUMBER = 180;

    //
    // MessageId: ERROR_INVALID_ORDINAL
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_INVALID_ORDINAL = 182;

    //
    // MessageId: ERROR_ALREADY_EXISTS
    //
    // MessageText:
    //
    // Cannot create a file when that file already exists.
    //
    int ERROR_ALREADY_EXISTS = 183;

    //
    // MessageId: ERROR_INVALID_FLAG_NUMBER
    //
    // MessageText:
    //
    // The flag passed is not correct.
    //
    int ERROR_INVALID_FLAG_NUMBER = 186;

    //
    // MessageId: ERROR_SEM_NOT_FOUND
    //
    // MessageText:
    //
    // The specified system semaphore name was not found.
    //
    int ERROR_SEM_NOT_FOUND = 187;

    //
    // MessageId: ERROR_INVALID_STARTING_CODESEG
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_INVALID_STARTING_CODESEG = 188;

    //
    // MessageId: ERROR_INVALID_STACKSEG
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_INVALID_STACKSEG = 189;

    //
    // MessageId: ERROR_INVALID_MODULETYPE
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_INVALID_MODULETYPE = 190;

    //
    // MessageId: ERROR_INVALID_EXE_SIGNATURE
    //
    // MessageText:
    //
    // Cannot run %1 in Win32 mode.
    //
    int ERROR_INVALID_EXE_SIGNATURE = 191;

    //
    // MessageId: ERROR_EXE_MARKED_INVALID
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_EXE_MARKED_INVALID = 192;

    //
    // MessageId: ERROR_BAD_EXE_FORMAT
    //
    // MessageText:
    //
    // %1 is not a valid Win32 application.
    //
    int ERROR_BAD_EXE_FORMAT = 193;

    //
    // MessageId: ERROR_ITERATED_DATA_EXCEEDS_64k
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_ITERATED_DATA_EXCEEDS_64k = 194;

    //
    // MessageId: ERROR_INVALID_MINALLOCSIZE
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_INVALID_MINALLOCSIZE = 195;

    //
    // MessageId: ERROR_DYNLINK_FROM_INVALID_RING
    //
    // MessageText:
    //
    // The operating system cannot run this application program.
    //
    int ERROR_DYNLINK_FROM_INVALID_RING = 196;

    //
    // MessageId: ERROR_IOPL_NOT_ENABLED
    //
    // MessageText:
    //
    // The operating system is not presently configured to run this application.
    //
    int ERROR_IOPL_NOT_ENABLED = 197;

    //
    // MessageId: ERROR_INVALID_SEGDPL
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_INVALID_SEGDPL = 198;

    //
    // MessageId: ERROR_AUTODATASEG_EXCEEDS_64k
    //
    // MessageText:
    //
    // The operating system cannot run this application program.
    //
    int ERROR_AUTODATASEG_EXCEEDS_64k = 199;

    //
    // MessageId: ERROR_RING2SEG_MUST_BE_MOVABLE
    //
    // MessageText:
    //
    // The code segment cannot be greater than or equal to=64K.
    //
    int ERROR_RING2SEG_MUST_BE_MOVABLE = 200;

    //
    // MessageId: ERROR_RELOC_CHAIN_XEEDS_SEGLIM
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_RELOC_CHAIN_XEEDS_SEGLIM = 201;

    //
    // MessageId: ERROR_INFLOOP_IN_RELOC_CHAIN
    //
    // MessageText:
    //
    // The operating system cannot run %1.
    //
    int ERROR_INFLOOP_IN_RELOC_CHAIN = 202;

    //
    // MessageId: ERROR_ENVVAR_NOT_FOUND
    //
    // MessageText:
    //
    // The system could not find the environment option that was entered.
    //
    int ERROR_ENVVAR_NOT_FOUND = 203;

    //
    // MessageId: ERROR_NO_SIGNAL_SENT
    //
    // MessageText:
    //
    // No process in the command subtree has a signal handler.
    //
    int ERROR_NO_SIGNAL_SENT = 205;

    //
    // MessageId: ERROR_FILENAME_EXCED_RANGE
    //
    // MessageText:
    //
    // The filename or extension is too long.
    //
    int ERROR_FILENAME_EXCED_RANGE = 206;

    //
    // MessageId: ERROR_RING2_STACK_IN_USE
    //
    // MessageText:
    //
    // The ring=2 stack is in use.
    //
    int ERROR_RING2_STACK_IN_USE = 207;

    //
    // MessageId: ERROR_META_EXPANSION_TOO_LONG
    //
    // MessageText:
    //
    // The global filename characters, * or ?, are entered incorrectly or too
    // many global filename characters are specified.
    //
    int ERROR_META_EXPANSION_TOO_LONG = 208;

    //
    // MessageId: ERROR_INVALID_SIGNAL_NUMBER
    //
    // MessageText:
    //
    // The signal being posted is not correct.
    //
    int ERROR_INVALID_SIGNAL_NUMBER = 209;

    //
    // MessageId: ERROR_THREAD_1_INACTIVE
    //
    // MessageText:
    //
    // The signal handler cannot be set.
    //
    int ERROR_THREAD_1_INACTIVE = 210;

    //
    // MessageId: ERROR_LOCKED
    //
    // MessageText:
    //
    // The segment is locked and cannot be reallocated.
    //
    int ERROR_LOCKED = 212;

    //
    // MessageId: ERROR_TOO_MANY_MODULES
    //
    // MessageText:
    //
    // Too many dynamic-link modules are attached to this program or
    // dynamic-link module.
    //
    int ERROR_TOO_MANY_MODULES = 214;

    //
    // MessageId: ERROR_NESTING_NOT_ALLOWED
    //
    // MessageText:
    //
    // Cannot nest calls to LoadModule.
    //
    int ERROR_NESTING_NOT_ALLOWED = 215;

    //
    // MessageId: ERROR_EXE_MACHINE_TYPE_MISMATCH
    //
    // MessageText:
    //
    // This version of %1 is not compatible with the version of Windows you're
    // running. Check your computer's system information to see whether you need
    // a x86 (32-bit) or x64 (64-bit) version of the program, and then contact
    // the software publisher.
    //
    int ERROR_EXE_MACHINE_TYPE_MISMATCH = 216;

    //
    // MessageId: ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY
    //
    // MessageText:
    //
    // The image file %1 is signed, unable to modify.
    //
    int ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY = 217;

    //
    // MessageId: ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY
    //
    // MessageText:
    //
    // The image file %1 is strong signed, unable to modify.
    //
    int ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY = 218;

    //
    // MessageId: ERROR_FILE_CHECKED_OUT
    //
    // MessageText:
    //
    // This file is checked out or locked for editing by another user.
    //
    int ERROR_FILE_CHECKED_OUT = 220;

    //
    // MessageId: ERROR_CHECKOUT_REQUIRED
    //
    // MessageText:
    //
    // The file must be checked out before saving changes.
    //
    int ERROR_CHECKOUT_REQUIRED = 221;

    //
    // MessageId: ERROR_BAD_FILE_TYPE
    //
    // MessageText:
    //
    // The file type being saved or retrieved has been blocked.
    //
    int ERROR_BAD_FILE_TYPE = 222;

    //
    // MessageId: ERROR_FILE_TOO_LARGE
    //
    // MessageText:
    //
    // The file size exceeds the limit allowed and cannot be saved.
    //
    int ERROR_FILE_TOO_LARGE = 223;

    //
    // MessageId: ERROR_FORMS_AUTH_REQUIRED
    //
    // MessageText:
    //
    // Access Denied. Before opening files in this location, you must first
    // browse to the web site and select the option to login automatically.
    //
    int ERROR_FORMS_AUTH_REQUIRED = 224;

    //
    // MessageId: ERROR_VIRUS_INFECTED
    //
    // MessageText:
    //
    // Operation did not complete successfully because the file contains a
    // virus.
    //
    int ERROR_VIRUS_INFECTED = 225;

    //
    // MessageId: ERROR_VIRUS_DELETED
    //
    // MessageText:
    //
    // This file contains a virus and cannot be opened. Due to the nature of
    // this virus, the file has been removed from this location.
    //
    int ERROR_VIRUS_DELETED = 226;

    //
    // MessageId: ERROR_PIPE_LOCAL
    //
    // MessageText:
    //
    // The pipe is local.
    //
    int ERROR_PIPE_LOCAL = 229;

    //
    // MessageId: ERROR_BAD_PIPE
    //
    // MessageText:
    //
    // The pipe state is invalid.
    //
    int ERROR_BAD_PIPE = 230;

    //
    // MessageId: ERROR_PIPE_BUSY
    //
    // MessageText:
    //
    // All pipe instances are busy.
    //
    int ERROR_PIPE_BUSY = 231;

    //
    // MessageId: ERROR_NO_DATA
    //
    // MessageText:
    //
    // The pipe is being closed.
    //
    int ERROR_NO_DATA = 232;

    //
    // MessageId: ERROR_PIPE_NOT_CONNECTED
    //
    // MessageText:
    //
    // No process is on the other end of the pipe.
    //
    int ERROR_PIPE_NOT_CONNECTED = 233;

    //
    // MessageId: ERROR_MORE_DATA
    //
    // MessageText:
    //
    // More data is available.
    //
    int ERROR_MORE_DATA = 234; // dderror

    //
    // MessageId: ERROR_VC_DISCONNECTED
    //
    // MessageText:
    //
    // The session was canceled.
    //
    int ERROR_VC_DISCONNECTED = 240;

    //
    // MessageId: ERROR_INVALID_EA_NAME
    //
    // MessageText:
    //
    // The specified extended attribute name was invalid.
    //
    int ERROR_INVALID_EA_NAME = 254;

    //
    // MessageId: ERROR_EA_LIST_INCONSISTENT
    //
    // MessageText:
    //
    // The extended attributes are inconsistent.
    //
    int ERROR_EA_LIST_INCONSISTENT = 255;

    //
    // MessageId: WAIT_TIMEOUT
    //
    // MessageText:
    //
    // The wait operation timed out.
    //
    int WAIT_TIMEOUT = 258; // dderror

    //
    // MessageId: ERROR_NO_MORE_ITEMS
    //
    // MessageText:
    //
    // No more data is available.
    //
    int ERROR_NO_MORE_ITEMS = 259;

    //
    // MessageId: ERROR_CANNOT_COPY
    //
    // MessageText:
    //
    // The copy functions cannot be used.
    //
    int ERROR_CANNOT_COPY = 266;

    //
    // MessageId: ERROR_DIRECTORY
    //
    // MessageText:
    //
    // The directory name is invalid.
    //
    int ERROR_DIRECTORY = 267;

    //
    // MessageId: ERROR_EAS_DIDNT_FIT
    //
    // MessageText:
    //
    // The extended attributes did not fit in the buffer.
    //
    int ERROR_EAS_DIDNT_FIT = 275;

    //
    // MessageId: ERROR_EA_FILE_CORRUPT
    //
    // MessageText:
    //
    // The extended attribute file on the mounted file system is corrupt.
    //
    int ERROR_EA_FILE_CORRUPT = 276;

    //
    // MessageId: ERROR_EA_TABLE_FULL
    //
    // MessageText:
    //
    // The extended attribute table file is full.
    //
    int ERROR_EA_TABLE_FULL = 277;

    //
    // MessageId: ERROR_INVALID_EA_HANDLE
    //
    // MessageText:
    //
    // The specified extended attribute handle is invalid.
    //
    int ERROR_INVALID_EA_HANDLE = 278;

    //
    // MessageId: ERROR_EAS_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The mounted file system does not support extended attributes.
    //
    int ERROR_EAS_NOT_SUPPORTED = 282;

    //
    // MessageId: ERROR_NOT_OWNER
    //
    // MessageText:
    //
    // Attempt to release mutex not owned by caller.
    //
    int ERROR_NOT_OWNER = 288;

    //
    // MessageId: ERROR_TOO_MANY_POSTS
    //
    // MessageText:
    //
    // Too many posts were made to a semaphore.
    //
    int ERROR_TOO_MANY_POSTS = 298;

    //
    // MessageId: ERROR_PARTIAL_COPY
    //
    // MessageText:
    //
    // Only part of a ReadProcessMemory or WriteProcessMemory request was
    // completed.
    //
    int ERROR_PARTIAL_COPY = 299;

    //
    // MessageId: ERROR_OPLOCK_NOT_GRANTED
    //
    // MessageText:
    //
    // The oplock request is denied.
    //
    int ERROR_OPLOCK_NOT_GRANTED = 300;

    //
    // MessageId: ERROR_INVALID_OPLOCK_PROTOCOL
    //
    // MessageText:
    //
    // An invalid oplock acknowledgment was received by the system.
    //
    int ERROR_INVALID_OPLOCK_PROTOCOL = 301;

    //
    // MessageId: ERROR_DISK_TOO_FRAGMENTED
    //
    // MessageText:
    //
    // The volume is too fragmented to complete this operation.
    //
    int ERROR_DISK_TOO_FRAGMENTED = 302;

    //
    // MessageId: ERROR_DELETE_PENDING
    //
    // MessageText:
    //
    // The file cannot be opened because it is in the process of being deleted.
    //
    int ERROR_DELETE_PENDING = 303;

    //
    // MessageId: ERROR_MR_MID_NOT_FOUND
    //
    // MessageText:
    //
    // The system cannot find message text for message number 0x%1 in the
    // message file for %2.
    //
    int ERROR_MR_MID_NOT_FOUND = 317;

    //
    // MessageId: ERROR_SCOPE_NOT_FOUND
    //
    // MessageText:
    //
    // The scope specified was not found.
    //
    int ERROR_SCOPE_NOT_FOUND = 318;

    //
    // MessageId: ERROR_FAIL_NOACTION_REBOOT
    //
    // MessageText:
    //
    // No action was taken as a system reboot is required.
    //
    int ERROR_FAIL_NOACTION_REBOOT = 350;

    //
    // MessageId: ERROR_FAIL_SHUTDOWN
    //
    // MessageText:
    //
    // The shutdown operation failed.
    //
    int ERROR_FAIL_SHUTDOWN = 351;

    //
    // MessageId: ERROR_FAIL_RESTART
    //
    // MessageText:
    //
    // The restart operation failed.
    //
    int ERROR_FAIL_RESTART = 352;

    //
    // MessageId: ERROR_MAX_SESSIONS_REACHED
    //
    // MessageText:
    //
    // The maximum number of sessions has been reached.
    //
    int ERROR_MAX_SESSIONS_REACHED = 353;

    //
    // MessageId: ERROR_THREAD_MODE_ALREADY_BACKGROUND
    //
    // MessageText:
    //
    // The thread is already in background processing mode.
    //
    int ERROR_THREAD_MODE_ALREADY_BACKGROUND = 400;

    //
    // MessageId: ERROR_THREAD_MODE_NOT_BACKGROUND
    //
    // MessageText:
    //
    // The thread is not in background processing mode.
    //
    int ERROR_THREAD_MODE_NOT_BACKGROUND = 401;

    //
    // MessageId: ERROR_PROCESS_MODE_ALREADY_BACKGROUND
    //
    // MessageText:
    //
    // The process is already in background processing mode.
    //
    int ERROR_PROCESS_MODE_ALREADY_BACKGROUND = 402;

    //
    // MessageId: ERROR_PROCESS_MODE_NOT_BACKGROUND
    //
    // MessageText:
    //
    // The process is not in background processing mode.
    //
    int ERROR_PROCESS_MODE_NOT_BACKGROUND = 403;

    //
    // MessageId: ERROR_INVALID_ADDRESS
    //
    // MessageText:
    //
    // Attempt to access invalid address.
    //
    int ERROR_INVALID_ADDRESS = 487;

    //
    // MessageId: ERROR_USER_PROFILE_LOAD
    //
    // MessageText:
    //
    // User profile cannot be loaded.
    //
    int ERROR_USER_PROFILE_LOAD = 500;

    //
    // MessageId: ERROR_ARITHMETIC_OVERFLOW
    //
    // MessageText:
    //
    // Arithmetic result exceeded=32 bits.
    //
    int ERROR_ARITHMETIC_OVERFLOW = 534;

    //
    // MessageId: ERROR_PIPE_CONNECTED
    //
    // MessageText:
    //
    // There is a process on other end of the pipe.
    //
    int ERROR_PIPE_CONNECTED = 535;

    //
    // MessageId: ERROR_PIPE_LISTENING
    //
    // MessageText:
    //
    // Waiting for a process to open the other end of the pipe.
    //
    int ERROR_PIPE_LISTENING = 536;

    //
    // MessageId: ERROR_VERIFIER_STOP
    //
    // MessageText:
    //
    // Application verifier has found an error in the current process.
    //
    int ERROR_VERIFIER_STOP = 537;

    //
    // MessageId: ERROR_ABIOS_ERROR
    //
    // MessageText:
    //
    // An error occurred in the ABIOS subsystem.
    //
    int ERROR_ABIOS_ERROR = 538;

    //
    // MessageId: ERROR_WX86_WARNING
    //
    // MessageText:
    //
    // A warning occurred in the WX86 subsystem.
    //
    int ERROR_WX86_WARNING = 539;

    //
    // MessageId: ERROR_WX86_ERROR
    //
    // MessageText:
    //
    // An error occurred in the WX86 subsystem.
    //
    int ERROR_WX86_ERROR = 540;

    //
    // MessageId: ERROR_TIMER_NOT_CANCELED
    //
    // MessageText:
    //
    // An attempt was made to cancel or set a timer that has an associated APC
    // and the subject thread is not the thread that originally set the timer
    // with an associated APC routine.
    //
    int ERROR_TIMER_NOT_CANCELED = 541;

    //
    // MessageId: ERROR_UNWIND
    //
    // MessageText:
    //
    // Unwind exception code.
    //
    int ERROR_UNWIND = 542;

    //
    // MessageId: ERROR_BAD_STACK
    //
    // MessageText:
    //
    // An invalid or unaligned stack was encountered during an unwind operation.
    //
    int ERROR_BAD_STACK = 543;

    //
    // MessageId: ERROR_INVALID_UNWIND_TARGET
    //
    // MessageText:
    //
    // An invalid unwind target was encountered during an unwind operation.
    //
    int ERROR_INVALID_UNWIND_TARGET = 544;

    //
    // MessageId: ERROR_INVALID_PORT_ATTRIBUTES
    //
    // MessageText:
    //
    // Invalid Object Attributes specified to NtCreatePort or invalid Port
    // Attributes specified to NtConnectPort
    //
    int ERROR_INVALID_PORT_ATTRIBUTES = 545;

    //
    // MessageId: ERROR_PORT_MESSAGE_TOO_LONG
    //
    // MessageText:
    //
    // Length of message passed to NtRequestPort or NtRequestWaitReplyPort was
    // longer than the maximum message allowed by the port.
    //
    int ERROR_PORT_MESSAGE_TOO_LONG = 546;

    //
    // MessageId: ERROR_INVALID_QUOTA_LOWER
    //
    // MessageText:
    //
    // An attempt was made to lower a quota limit below the current usage.
    //
    int ERROR_INVALID_QUOTA_LOWER = 547;

    //
    // MessageId: ERROR_DEVICE_ALREADY_ATTACHED
    //
    // MessageText:
    //
    // An attempt was made to attach to a device that was already attached to
    // another device.
    //
    int ERROR_DEVICE_ALREADY_ATTACHED = 548;

    //
    // MessageId: ERROR_INSTRUCTION_MISALIGNMENT
    //
    // MessageText:
    //
    // An attempt was made to execute an instruction at an unaligned address and
    // the host system does not support unaligned instruction references.
    //
    int ERROR_INSTRUCTION_MISALIGNMENT = 549;

    //
    // MessageId: ERROR_PROFILING_NOT_STARTED
    //
    // MessageText:
    //
    // Profiling not started.
    //
    int ERROR_PROFILING_NOT_STARTED = 550;

    //
    // MessageId: ERROR_PROFILING_NOT_STOPPED
    //
    // MessageText:
    //
    // Profiling not stopped.
    //
    int ERROR_PROFILING_NOT_STOPPED = 551;

    //
    // MessageId: ERROR_COULD_NOT_INTERPRET
    //
    // MessageText:
    //
    // The passed ACL did not contain the minimum required information.
    //
    int ERROR_COULD_NOT_INTERPRET = 552;

    //
    // MessageId: ERROR_PROFILING_AT_LIMIT
    //
    // MessageText:
    //
    // The number of active profiling objects is at the maximum and no more may
    // be started.
    //
    int ERROR_PROFILING_AT_LIMIT = 553;

    //
    // MessageId: ERROR_CANT_WAIT
    //
    // MessageText:
    //
    // Used to indicate that an operation cannot continue without blocking for
    // I/O.
    //
    int ERROR_CANT_WAIT = 554;

    //
    // MessageId: ERROR_CANT_TERMINATE_SELF
    //
    // MessageText:
    //
    // Indicates that a thread attempted to terminate itself by default (called
    // NtTerminateThread with NUL; and it was the last thread in the current
    // process.
    //
    int ERROR_CANT_TERMINATE_SELF = 555;

    //
    // MessageId: ERROR_UNEXPECTED_MM_CREATE_ERR
    //
    // MessageText:
    //
    // If an MM error is returned which is not defined in the standard FsRtl
    // filter, it is converted to one of the following errors which is
    // guaranteed to be in the filter.
    // In this case information is lost, however, the filter correctly handles
    // the exception.
    //
    int ERROR_UNEXPECTED_MM_CREATE_ERR = 556;

    //
    // MessageId: ERROR_UNEXPECTED_MM_MAP_ERROR
    //
    // MessageText:
    //
    // If an MM error is returned which is not defined in the standard FsRtl
    // filter, it is converted to one of the following errors which is
    // guaranteed to be in the filter.
    // In this case information is lost, however, the filter correctly handles
    // the exception.
    //
    int ERROR_UNEXPECTED_MM_MAP_ERROR = 557;

    //
    // MessageId: ERROR_UNEXPECTED_MM_EXTEND_ERR
    //
    // MessageText:
    //
    // If an MM error is returned which is not defined in the standard FsRtl
    // filter, it is converted to one of the following errors which is
    // guaranteed to be in the filter.
    // In this case information is lost, however, the filter correctly handles
    // the exception.
    //
    int ERROR_UNEXPECTED_MM_EXTEND_ERR = 558;

    //
    // MessageId: ERROR_BAD_FUNCTION_TABLE
    //
    // MessageText:
    //
    // A malformed function table was encountered during an unwind operation.
    //
    int ERROR_BAD_FUNCTION_TABLE = 559;

    //
    // MessageId: ERROR_NO_GUID_TRANSLATION
    //
    // MessageText:
    //
    // Indicates that an attempt was made to assign protection to a file system
    // file or directory and one of the SIDs in the security descriptor could
    // not be translated into a GUID that could be stored by the file system.
    // This causes the protection attempt to fail, which may cause a file
    // creation attempt to fail.
    //
    int ERROR_NO_GUID_TRANSLATION = 560;

    //
    // MessageId: ERROR_INVALID_LDT_SIZE
    //
    // MessageText:
    //
    // Indicates that an attempt was made to grow an LDT by setting its size, or
    // that the size was not an even number of selectors.
    //
    int ERROR_INVALID_LDT_SIZE = 561;

    //
    // MessageId: ERROR_INVALID_LDT_OFFSET
    //
    // MessageText:
    //
    // Indicates that the starting value for the LDT information was not an
    // integral multiple of the selector size.
    //
    int ERROR_INVALID_LDT_OFFSET = 563;

    //
    // MessageId: ERROR_INVALID_LDT_DESCRIPTOR
    //
    // MessageText:
    //
    // Indicates that the user supplied an invalid descriptor when trying to set
    // up Ldt descriptors.
    //
    int ERROR_INVALID_LDT_DESCRIPTOR = 564;

    //
    // MessageId: ERROR_TOO_MANY_THREADS
    //
    // MessageText:
    //
    // Indicates a process has too many threads to perform the requested action.
    // For example, assignment of a primary token may only be performed when a
    // process has zero or one threads.
    //
    int ERROR_TOO_MANY_THREADS = 565;

    //
    // MessageId: ERROR_THREAD_NOT_IN_PROCESS
    //
    // MessageText:
    //
    // An attempt was made to operate on a thread within a specific process, but
    // the thread specified is not in the process specified.
    //
    int ERROR_THREAD_NOT_IN_PROCESS = 566;

    //
    // MessageId: ERROR_PAGEFILE_QUOTA_EXCEEDED
    //
    // MessageText:
    //
    // Page file quota was exceeded.
    //
    int ERROR_PAGEFILE_QUOTA_EXCEEDED = 567;

    //
    // MessageId: ERROR_LOGON_SERVER_CONFLICT
    //
    // MessageText:
    //
    // The Netlogon service cannot start because another Netlogon service
    // running in the domain conflicts with the specified role.
    //
    int ERROR_LOGON_SERVER_CONFLICT = 568;

    //
    // MessageId: ERROR_SYNCHRONIZATION_REQUIRED
    //
    // MessageText:
    //
    // The SAM database on a Windows Server is significantly out of
    // synchronization with the copy on the Domain Controller. A complete
    // synchronization is required.
    //
    int ERROR_SYNCHRONIZATION_REQUIRED = 569;

    //
    // MessageId: ERROR_NET_OPEN_FAILED
    //
    // MessageText:
    //
    // The NtCreateFile API failed. This error should never be returned to an
    // application, it is a place holder for the Windows Lan Manager Redirector
    // to use in its internal error mapping routines.
    //
    int ERROR_NET_OPEN_FAILED = 570;

    //
    // MessageId: ERROR_IO_PRIVILEGE_FAILED
    //
    // MessageText:
    //
    // {Privilege Failed}
    // The I/O permissions for the process could not be changed.
    //
    int ERROR_IO_PRIVILEGE_FAILED = 571;

    //
    // MessageId: ERROR_CONTROL_C_EXIT
    //
    // MessageText:
    //
    // {Application Exit by CTRL+C}
    // The application terminated as a result of a CTRL+C.
    //
    int ERROR_CONTROL_C_EXIT = 572; // winnt

    //
    // MessageId: ERROR_MISSING_SYSTEMFILE
    //
    // MessageText:
    //
    // {Missing System File}
    // The required system file %hs is bad or missing.
    //
    int ERROR_MISSING_SYSTEMFILE = 573;

    //
    // MessageId: ERROR_UNHANDLED_EXCEPTION
    //
    // MessageText:
    //
    // {Application Error}
    // The exception %s (0x%08;x) occurred in the application at location
    // 0x%08;x.
    //
    int ERROR_UNHANDLED_EXCEPTION = 574;

    //
    // MessageId: ERROR_APP_INIT_FAILURE
    //
    // MessageText:
    //
    // {Application Error}
    // The application failed to initialize properly (0x%lx). Click OK to
    // terminate the application.
    //
    int ERROR_APP_INIT_FAILURE = 575;

    //
    // MessageId: ERROR_PAGEFILE_CREATE_FAILED
    //
    // MessageText:
    //
    // {Unable to Create Paging File}
    // The creation of the paging file %hs failed (%lx). The requested size was
    // %ld.
    //
    int ERROR_PAGEFILE_CREATE_FAILED = 576;

    //
    // MessageId: ERROR_INVALID_IMAGE_HASH
    //
    // MessageText:
    //
    // Windows cannot verify the digital signature for this file. A recent
    // hardware or software change might have installed a file that is signed
    // incorrectly or damaged, or that might be malicious software from an
    // unknown source.
    //
    int ERROR_INVALID_IMAGE_HASH = 577;

    //
    // MessageId: ERROR_NO_PAGEFILE
    //
    // MessageText:
    //
    // {No Paging File Specified}
    // No paging file was specified in the system configuration.
    //
    int ERROR_NO_PAGEFILE = 578;

    //
    // MessageId: ERROR_ILLEGAL_FLOAT_CONTEXT
    //
    // MessageText:
    //
    // {EXCEPTION}
    // A real-mode application issued a floating-point instruction and
    // floating-point hardware is not present.
    //
    int ERROR_ILLEGAL_FLOAT_CONTEXT = 579;

    //
    // MessageId: ERROR_NO_EVENT_PAIR
    //
    // MessageText:
    //
    // An event pair synchronization operation was performed using the thread
    // specific client/server event pair object, but no event pair object was
    // associated with the thread.
    //
    int ERROR_NO_EVENT_PAIR = 580;

    //
    // MessageId: ERROR_DOMAIN_CTRLR_CONFIG_ERROR
    //
    // MessageText:
    //
    // A Windows Server has an incorrect configuration.
    //
    int ERROR_DOMAIN_CTRLR_CONFIG_ERROR = 581;

    //
    // MessageId: ERROR_ILLEGAL_CHARACTER
    //
    // MessageText:
    //
    // An illegal character was encountered. For a multi-byte character set this
    // includes a lead byte without a succeeding trail byte. For the Unicode
    // character set this includes the characters 0xFFFF and 0xFFFE.
    //
    int ERROR_ILLEGAL_CHARACTER = 582;

    //
    // MessageId: ERROR_UNDEFINED_CHARACTER
    //
    // MessageText:
    //
    // The Unicode character is not defined in the Unicode character set
    // installed on the system.
    //
    int ERROR_UNDEFINED_CHARACTER = 583;

    //
    // MessageId: ERROR_FLOPPY_VOLUME
    //
    // MessageText:
    //
    // The paging file cannot be created on a floppy diskette.
    //
    int ERROR_FLOPPY_VOLUME = 584;

    //
    // MessageId: ERROR_BIOS_FAILED_TO_CONNECT_INTERRUPT
    //
    // MessageText:
    //
    // The system BIOS failed to connect a system interrupt to the device or bus
    // for which the device is connected.
    //
    int ERROR_BIOS_FAILED_TO_CONNECT_INTERRUPT = 585;

    //
    // MessageId: ERROR_BACKUP_CONTROLLER
    //
    // MessageText:
    //
    // This operation is only allowed for the Primary Domain Controller of the
    // domain.
    //
    int ERROR_BACKUP_CONTROLLER = 586;

    //
    // MessageId: ERROR_MUTANT_LIMIT_EXCEEDED
    //
    // MessageText:
    //
    // An attempt was made to acquire a mutant such that its maximum count would
    // have been exceeded.
    //
    int ERROR_MUTANT_LIMIT_EXCEEDED = 587;

    //
    // MessageId: ERROR_FS_DRIVER_REQUIRED
    //
    // MessageText:
    //
    // A volume has been accessed for which a file system driver is required
    // that has not yet been loaded.
    //
    int ERROR_FS_DRIVER_REQUIRED = 588;

    //
    // MessageId: ERROR_CANNOT_LOAD_REGISTRY_FILE
    //
    // MessageText:
    //
    // {Registry File Failure}
    // The registry cannot load the hive (file):
    // %hs
    // or its log or alternate.
    // It is corrupt, absent, or not writable.
    //
    int ERROR_CANNOT_LOAD_REGISTRY_FILE = 589;

    //
    // MessageId: ERROR_DEBUG_ATTACH_FAILED
    //
    // MessageText:
    //
    // {Unexpected Failure in DebugActiveProcess}
    // An unexpected failure occurred while processing a DebugActiveProcess API
    // request. You may choose OK to terminate the process, or Cancel to ignore
    // the error.
    //
    int ERROR_DEBUG_ATTACH_FAILED = 590;

    //
    // MessageId: ERROR_SYSTEM_PROCESS_TERMINATED
    //
    // MessageText:
    //
    // {Fatal System Error}
    // The %hs system process terminated unexpectedly with a status of 0x%08x
    // (0x%08x 0x%08x).
    // The system has been shut down.
    //
    int ERROR_SYSTEM_PROCESS_TERMINATED = 591;

    //
    // MessageId: ERROR_DATA_NOT_ACCEPTED
    //
    // MessageText:
    //
    // {Data Not Accepted}
    // The TDI client could not handle the data received during an indication.
    //
    int ERROR_DATA_NOT_ACCEPTED = 592;

    //
    // MessageId: ERROR_VDM_HARD_ERROR
    //
    // MessageText:
    //
    // NTVDM encountered a hard error.
    //
    int ERROR_VDM_HARD_ERROR = 593;

    //
    // MessageId: ERROR_DRIVER_CANCEL_TIMEOUT
    //
    // MessageText:
    //
    // {Cancel Timeout}
    // The driver %hs failed to complete a cancelled I/O request in the allotted
    // time.
    //
    int ERROR_DRIVER_CANCEL_TIMEOUT = 594;

    //
    // MessageId: ERROR_REPLY_MESSAGE_MISMATCH
    //
    // MessageText:
    //
    // {Reply Message Mismatch}
    // An attempt was made to reply to an LPC message, but the thread specified
    // by the client ID in the message was not waiting on that message.
    //
    int ERROR_REPLY_MESSAGE_MISMATCH = 595;

    //
    // MessageId: ERROR_LOST_WRITEBEHIND_DATA
    //
    // MessageText:
    //
    // {Delayed Write Failed}
    // Windows was unable to save all the data for the file %hs. The data has
    // been lost.
    // This error may be caused by a failure of your computer hardware or
    // network connection. Please try to save this file elsewhere.
    //
    int ERROR_LOST_WRITEBEHIND_DATA = 596;

    //
    // MessageId: ERROR_CLIENT_SERVER_PARAMETERS_INVALID
    //
    // MessageText:
    //
    // The parameter(s) passed to the server in the client/server shared memory
    // window were invalid. Too much data may have been put in the shared memory
    // window.
    //
    int ERROR_CLIENT_SERVER_PARAMETERS_INVALID = 597;

    //
    // MessageId: ERROR_NOT_TINY_STREAM
    //
    // MessageText:
    //
    // The stream is not a tiny stream.
    //
    int ERROR_NOT_TINY_STREAM = 598;

    //
    // MessageId: ERROR_STACK_OVERFLOW_READ
    //
    // MessageText:
    //
    // The request must be handled by the stack overflow code.
    //
    int ERROR_STACK_OVERFLOW_READ = 599;

    //
    // MessageId: ERROR_CONVERT_TO_LARGE
    //
    // MessageText:
    //
    // Internal OFS status codes indicating how an allocation operation is
    // handled. Either it is retried after the containing onode is moved or the
    // extent stream is converted to a large stream.
    //
    int ERROR_CONVERT_TO_LARGE = 600;

    //
    // MessageId: ERROR_FOUND_OUT_OF_SCOPE
    //
    // MessageText:
    //
    // The attempt to find the object found an object matching by ID on the
    // volume but it is out of the scope of the handle used for the operation.
    //
    int ERROR_FOUND_OUT_OF_SCOPE = 601;

    //
    // MessageId: ERROR_ALLOCATE_BUCKET
    //
    // MessageText:
    //
    // The bucket array must be grown. Retry transaction after doing so.
    //
    int ERROR_ALLOCATE_BUCKET = 602;

    //
    // MessageId: ERROR_MARSHALL_OVERFLOW
    //
    // MessageText:
    //
    // The user/kernel marshalling buffer has overflowed.
    //
    int ERROR_MARSHALL_OVERFLOW = 603;

    //
    // MessageId: ERROR_INVALID_VARIANT
    //
    // MessageText:
    //
    // The supplied variant structure contains invalid data.
    //
    int ERROR_INVALID_VARIANT = 604;

    //
    // MessageId: ERROR_BAD_COMPRESSION_BUFFER
    //
    // MessageText:
    //
    // The specified buffer contains ill-formed data.
    //
    int ERROR_BAD_COMPRESSION_BUFFER = 605;

    //
    // MessageId: ERROR_AUDIT_FAILED
    //
    // MessageText:
    //
    // {Audit Failed}
    // An attempt to generate a security audit failed.
    //
    int ERROR_AUDIT_FAILED = 606;

    //
    // MessageId: ERROR_TIMER_RESOLUTION_NOT_SET
    //
    // MessageText:
    //
    // The timer resolution was not previously set by the current process.
    //
    int ERROR_TIMER_RESOLUTION_NOT_SET = 607;

    //
    // MessageId: ERROR_INSUFFICIENT_LOGON_INFO
    //
    // MessageText:
    //
    // There is insufficient account information to log you on.
    //
    int ERROR_INSUFFICIENT_LOGON_INFO = 608;

    //
    // MessageId: ERROR_BAD_DLL_ENTRYPOINT
    //
    // MessageText:
    //
    // {Invalid DLL Entrypoint}
    // The dynamic link library %hs is not written correctly. The stack pointer
    // has been left in an inconsistent state.
    // The entrypoint should be declared as WINAPI or STDCALL. Select YES to
    // fail the DLL load. Select NO to continue execution. Selecting NO may
    // cause the application to operate incorrectly.
    //
    int ERROR_BAD_DLL_ENTRYPOINT = 609;

    //
    // MessageId: ERROR_BAD_SERVICE_ENTRYPOINT
    //
    // MessageText:
    //
    // {Invalid Service Callback Entrypoint}
    // The %hs service is not written correctly. The stack pointer has been left
    // in an inconsistent state.
    // The callback entrypoint should be declared as WINAPI or STDCALL.
    // Selecting OK will cause the service to continue operation. However, the
    // service process may operate incorrectly.
    //
    int ERROR_BAD_SERVICE_ENTRYPOINT = 610;

    //
    // MessageId: ERROR_IP_ADDRESS_CONFLICT1
    //
    // MessageText:
    //
    // There is an IP address conflict with another system on the network
    //
    int ERROR_IP_ADDRESS_CONFLICT1 = 611;

    //
    // MessageId: ERROR_IP_ADDRESS_CONFLICT2
    //
    // MessageText:
    //
    // There is an IP address conflict with another system on the network
    //
    int ERROR_IP_ADDRESS_CONFLICT2 = 612;

    //
    // MessageId: ERROR_REGISTRY_QUOTA_LIMIT
    //
    // MessageText:
    //
    // {Low On Registry Space}
    // The system has reached the maximum size allowed for the system part of
    // the registry. Additional storage requests will be ignored.
    //
    int ERROR_REGISTRY_QUOTA_LIMIT = 613;

    //
    // MessageId: ERROR_NO_CALLBACK_ACTIVE
    //
    // MessageText:
    //
    // A callback return system service cannot be executed when no callback is
    // active.
    //
    int ERROR_NO_CALLBACK_ACTIVE = 614;

    //
    // MessageId: ERROR_PWD_TOO_SHORT
    //
    // MessageText:
    //
    // The password provided is too short to meet the policy of your user
    // account.
    // Please choose a longer password.
    //
    int ERROR_PWD_TOO_SHORT = 615;

    //
    // MessageId: ERROR_PWD_TOO_RECENT
    //
    // MessageText:
    //
    // The policy of your user account does not allow you to change passwords
    // too frequently.
    // This is done to prevent users from changing back to a familiar, but
    // potentially discovered, password.
    // If you feel your password has been compromised then please contact your
    // administrator immediately to have a new one assigned.
    //
    int ERROR_PWD_TOO_RECENT = 616;

    //
    // MessageId: ERROR_PWD_HISTORY_CONFLICT
    //
    // MessageText:
    //
    // You have attempted to change your password to one that you have used in
    // the past.
    // The policy of your user account does not allow this. Please select a
    // password that you have not previously used.
    //
    int ERROR_PWD_HISTORY_CONFLICT = 617;

    //
    // MessageId: ERROR_UNSUPPORTED_COMPRESSION
    //
    // MessageText:
    //
    // The specified compression format is unsupported.
    //
    int ERROR_UNSUPPORTED_COMPRESSION = 618;

    //
    // MessageId: ERROR_INVALID_HW_PROFILE
    //
    // MessageText:
    //
    // The specified hardware profile configuration is invalid.
    //
    int ERROR_INVALID_HW_PROFILE = 619;

    //
    // MessageId: ERROR_INVALID_PLUGPLAY_DEVICE_PATH
    //
    // MessageText:
    //
    // The specified Plug and Play registry device path is invalid.
    //
    int ERROR_INVALID_PLUGPLAY_DEVICE_PATH = 620;

    //
    // MessageId: ERROR_QUOTA_LIST_INCONSISTENT
    //
    // MessageText:
    //
    // The specified quota list is internally inconsistent with its descriptor.
    //
    int ERROR_QUOTA_LIST_INCONSISTENT = 621;

    //
    // MessageId: ERROR_EVALUATION_EXPIRATION
    //
    // MessageText:
    //
    // {Windows Evaluation Notification}
    // The evaluation period for this installation of Windows has expired. This
    // system will shutdown in=1 hour. To restore access to this installation of
    // Windows, please upgrade this installation using a licensed distribution
    // of this product.
    //
    int ERROR_EVALUATION_EXPIRATION = 622;

    //
    // MessageId: ERROR_ILLEGAL_DLL_RELOCATION
    //
    // MessageText:
    //
    // {Illegal System DLL Relocation}
    // The system DLL %hs was relocated in memory. The application will not run
    // properly.
    // The relocation occurred because the DLL %hs occupied an address range
    // reserved for Windows system DLLs. The vendor supplying the DLL should be
    // contacted for a new DLL.
    //
    int ERROR_ILLEGAL_DLL_RELOCATION = 623;

    //
    // MessageId: ERROR_DLL_INIT_FAILED_LOGOFF
    //
    // MessageText:
    //
    // {DLL Initialization Failed}
    // The application failed to initialize because the window station is
    // shutting down.
    //
    int ERROR_DLL_INIT_FAILED_LOGOFF = 624;

    //
    // MessageId: ERROR_VALIDATE_CONTINUE
    //
    // MessageText:
    //
    // The validation process needs to continue on to the next step.
    //
    int ERROR_VALIDATE_CONTINUE = 625;

    //
    // MessageId: ERROR_NO_MORE_MATCHES
    //
    // MessageText:
    //
    // There are no more matches for the current index enumeration.
    //
    int ERROR_NO_MORE_MATCHES = 626;

    //
    // MessageId: ERROR_RANGE_LIST_CONFLICT
    //
    // MessageText:
    //
    // The range could not be added to the range list because of a conflict.
    //
    int ERROR_RANGE_LIST_CONFLICT = 627;

    //
    // MessageId: ERROR_SERVER_SID_MISMATCH
    //
    // MessageText:
    //
    // The server process is running under a SID different than that required by
    // client.
    //
    int ERROR_SERVER_SID_MISMATCH = 628;

    //
    // MessageId: ERROR_CANT_ENABLE_DENY_ONLY
    //
    // MessageText:
    //
    // A group marked use for deny only cannot be enabled.
    //
    int ERROR_CANT_ENABLE_DENY_ONLY = 629;

    //
    // MessageId: ERROR_FLOAT_MULTIPLE_FAULTS
    //
    // MessageText:
    //
    // {EXCEPTION}
    // Multiple floating point faults.
    //
    int ERROR_FLOAT_MULTIPLE_FAULTS = 630; // winnt

    //
    // MessageId: ERROR_FLOAT_MULTIPLE_TRAPS
    //
    // MessageText:
    //
    // {EXCEPTION}
    // Multiple floating point traps.
    //
    int ERROR_FLOAT_MULTIPLE_TRAPS = 631; // winnt

    //
    // MessageId: ERROR_NOINTERFACE
    //
    // MessageText:
    //
    // The requested interface is not supported.
    //
    int ERROR_NOINTERFACE = 632;

    //
    // MessageId: ERROR_DRIVER_FAILED_SLEEP
    //
    // MessageText:
    //
    // {System Standby Failed}
    // The driver %hs does not support standby mode. Updating this driver may
    // allow the system to go to standby mode.
    //
    int ERROR_DRIVER_FAILED_SLEEP = 633;

    //
    // MessageId: ERROR_CORRUPT_SYSTEM_FILE
    //
    // MessageText:
    //
    // The system file %1 has become corrupt and has been replaced.
    //
    int ERROR_CORRUPT_SYSTEM_FILE = 634;

    //
    // MessageId: ERROR_COMMITMENT_MINIMUM
    //
    // MessageText:
    //
    // {Virtual Memory Minimum Too Low}
    // Your system is low on virtual memory. Windows is increasing the size of
    // your virtual memory paging file.
    // During this process, memory requests for some applications may be denied.
    // For more information, see Help.
    //
    int ERROR_COMMITMENT_MINIMUM = 635;

    //
    // MessageId: ERROR_PNP_RESTART_ENUMERATION
    //
    // MessageText:
    //
    // A device was removed so enumeration must be restarted.
    //
    int ERROR_PNP_RESTART_ENUMERATION = 636;

    //
    // MessageId: ERROR_SYSTEM_IMAGE_BAD_SIGNATURE
    //
    // MessageText:
    //
    // {Fatal System Error}
    // The system image %s is not properly signed.
    // The file has been replaced with the signed file.
    // The system has been shut down.
    //
    int ERROR_SYSTEM_IMAGE_BAD_SIGNATURE = 637;

    //
    // MessageId: ERROR_PNP_REBOOT_REQUIRED
    //
    // MessageText:
    //
    // Device will not start without a reboot.
    //
    int ERROR_PNP_REBOOT_REQUIRED = 638;

    //
    // MessageId: ERROR_INSUFFICIENT_POWER
    //
    // MessageText:
    //
    // There is not enough power to complete the requested operation.
    //
    int ERROR_INSUFFICIENT_POWER = 639;

    //
    // MessageId: ERROR_MULTIPLE_FAULT_VIOLATION
    //
    // MessageText:
    //
    // ERROR_MULTIPLE_FAULT_VIOLATION
    //
    int ERROR_MULTIPLE_FAULT_VIOLATION = 640;

    //
    // MessageId: ERROR_SYSTEM_SHUTDOWN
    //
    // MessageText:
    //
    // The system is in the process of shutting down.
    //
    int ERROR_SYSTEM_SHUTDOWN = 641;

    //
    // MessageId: ERROR_PORT_NOT_SET
    //
    // MessageText:
    //
    // An attempt to remove a processes DebugPort was made, but a port was not
    // already associated with the process.
    //
    int ERROR_PORT_NOT_SET = 642;

    //
    // MessageId: ERROR_DS_VERSION_CHECK_FAILURE
    //
    // MessageText:
    //
    // This version of Windows is not compatible with the behavior version of
    // directory forest, domain or domain controller.
    //
    int ERROR_DS_VERSION_CHECK_FAILURE = 643;

    //
    // MessageId: ERROR_RANGE_NOT_FOUND
    //
    // MessageText:
    //
    // The specified range could not be found in the range list.
    //
    int ERROR_RANGE_NOT_FOUND = 644;

    //
    // MessageId: ERROR_NOT_SAFE_MODE_DRIVER
    //
    // MessageText:
    //
    // The driver was not loaded because the system is booting into safe mode.
    //
    int ERROR_NOT_SAFE_MODE_DRIVER = 646;

    //
    // MessageId: ERROR_FAILED_DRIVER_ENTRY
    //
    // MessageText:
    //
    // The driver was not loaded because it failed it's initialization call.
    //
    int ERROR_FAILED_DRIVER_ENTRY = 647;

    //
    // MessageId: ERROR_DEVICE_ENUMERATION_ERROR
    //
    // MessageText:
    //
    // The "%hs" encountered an error while applying power or reading the device
    // configuration.
    // This may be caused by a failure of your hardware or by a poor connection.
    //
    int ERROR_DEVICE_ENUMERATION_ERROR = 648;

    //
    // MessageId: ERROR_MOUNT_POINT_NOT_RESOLVED
    //
    // MessageText:
    //
    // The create operation failed because the name contained at least one mount
    // point which resolves to a volume to which the specified device object is
    // not attached.
    //
    int ERROR_MOUNT_POINT_NOT_RESOLVED = 649;

    //
    // MessageId: ERROR_INVALID_DEVICE_OBJECT_PARAMETER
    //
    // MessageText:
    //
    // The device object parameter is either not a valid device object or is not
    // attached to the volume specified by the file name.
    //
    int ERROR_INVALID_DEVICE_OBJECT_PARAMETER = 650;

    //
    // MessageId: ERROR_MCA_OCCURED
    //
    // MessageText:
    //
    // A Machine Check Error has occurred. Please check the system eventlog for
    // additional information.
    //
    int ERROR_MCA_OCCURED = 651;

    //
    // MessageId: ERROR_DRIVER_DATABASE_ERROR
    //
    // MessageText:
    //
    // There was error [%2] processing the driver database.
    //
    int ERROR_DRIVER_DATABASE_ERROR = 652;

    //
    // MessageId: ERROR_SYSTEM_HIVE_TOO_LARGE
    //
    // MessageText:
    //
    // System hive size has exceeded its limit.
    //
    int ERROR_SYSTEM_HIVE_TOO_LARGE = 653;

    //
    // MessageId: ERROR_DRIVER_FAILED_PRIOR_UNLOAD
    //
    // MessageText:
    //
    // The driver could not be loaded because a previous version of the driver
    // is still in memory.
    //
    int ERROR_DRIVER_FAILED_PRIOR_UNLOAD = 654;

    //
    // MessageId: ERROR_VOLSNAP_PREPARE_HIBERNATE
    //
    // MessageText:
    //
    // {Volume Shadow Copy Service}
    // Please wait while the Volume Shadow Copy Service prepares volume %hs for
    // hibernation.
    //
    int ERROR_VOLSNAP_PREPARE_HIBERNATE = 655;

    //
    // MessageId: ERROR_HIBERNATION_FAILURE
    //
    // MessageText:
    //
    // The system has failed to hibernate (The error code is %hs). Hibernation
    // will be disabled until the system is restarted.
    //
    int ERROR_HIBERNATION_FAILURE = 656;

    //
    // MessageId: ERROR_FILE_SYSTEM_LIMITATION
    //
    // MessageText:
    //
    // The requested operation could not be completed due to a file system
    // limitation
    //
    int ERROR_FILE_SYSTEM_LIMITATION = 665;

    //
    // MessageId: ERROR_ASSERTION_FAILURE
    //
    // MessageText:
    //
    // An assertion failure has occurred.
    //
    int ERROR_ASSERTION_FAILURE = 668;

    //
    // MessageId: ERROR_ACPI_ERROR
    //
    // MessageText:
    //
    // An error occurred in the ACPI subsystem.
    //
    int ERROR_ACPI_ERROR = 669;

    //
    // MessageId: ERROR_WOW_ASSERTION
    //
    // MessageText:
    //
    // WOW Assertion Error.
    //
    int ERROR_WOW_ASSERTION = 670;

    //
    // MessageId: ERROR_PNP_BAD_MPS_TABLE
    //
    // MessageText:
    //
    // A device is missing in the system BIOS MPS table. This device will not be
    // used.
    // Please contact your system vendor for system BIOS update.
    //
    int ERROR_PNP_BAD_MPS_TABLE = 671;

    //
    // MessageId: ERROR_PNP_TRANSLATION_FAILED
    //
    // MessageText:
    //
    // A translator failed to translate resources.
    //
    int ERROR_PNP_TRANSLATION_FAILED = 672;

    //
    // MessageId: ERROR_PNP_IRQ_TRANSLATION_FAILED
    //
    // MessageText:
    //
    // A IRQ translator failed to translate resources.
    //
    int ERROR_PNP_IRQ_TRANSLATION_FAILED = 673;

    //
    // MessageId: ERROR_PNP_INVALID_ID
    //
    // MessageText:
    //
    // Driver %2 returned invalid ID for a child device (%3).
    //
    int ERROR_PNP_INVALID_ID = 674;

    //
    // MessageId: ERROR_WAKE_SYSTEM_DEBUGGER
    //
    // MessageText:
    //
    // {Kernel Debugger Awakened}
    // the system debugger was awakened by an interrupt.
    //
    int ERROR_WAKE_SYSTEM_DEBUGGER = 675;

    //
    // MessageId: ERROR_HANDLES_CLOSED
    //
    // MessageText:
    //
    // {Handles Closed}
    // Handles to objects have been automatically closed as a result of the
    // requested operation.
    //
    int ERROR_HANDLES_CLOSED = 676;

    //
    // MessageId: ERROR_EXTRANEOUS_INFORMATION
    //
    // MessageText:
    //
    // {Too Much Information}
    // The specified access control list (AC; contained more information than
    // was expected.
    //
    int ERROR_EXTRANEOUS_INFORMATION = 677;

    //
    // MessageId: ERROR_RXACT_COMMIT_NECESSARY
    //
    // MessageText:
    //
    // This warning level status indicates that the transaction state already
    // exists for the registry sub-tree, but that a transaction commit was
    // previously aborted.
    // The commit has NOT been completed, but has not been rolled back either
    // (so it may still be committed if desired).
    //
    int ERROR_RXACT_COMMIT_NECESSARY = 678;

    //
    // MessageId: ERROR_MEDIA_CHECK
    //
    // MessageText:
    //
    // {Media Changed}
    // The media may have changed.
    //
    int ERROR_MEDIA_CHECK = 679;

    //
    // MessageId: ERROR_GUID_SUBSTITUTION_MADE
    //
    // MessageText:
    //
    // {GUID Substitution}
    // During the translation of a global identifier (GUID) to a Windows
    // security ID (SID), no administratively-defined GUID prefix was found.
    // A substitute prefix was used, which will not compromise system security.
    // However, this may provide a more restrictive access than intended.
    //
    int ERROR_GUID_SUBSTITUTION_MADE = 680;

    //
    // MessageId: ERROR_STOPPED_ON_SYMLINK
    //
    // MessageText:
    //
    // The create operation stopped after reaching a symbolic link
    //
    int ERROR_STOPPED_ON_SYMLINK = 681;

    //
    // MessageId: ERROR_LONGJUMP
    //
    // MessageText:
    //
    // A long jump has been executed.
    //
    int ERROR_LONGJUMP = 682;

    //
    // MessageId: ERROR_PLUGPLAY_QUERY_VETOED
    //
    // MessageText:
    //
    // The Plug and Play query operation was not successful.
    //
    int ERROR_PLUGPLAY_QUERY_VETOED = 683;

    //
    // MessageId: ERROR_UNWIND_CONSOLIDATE
    //
    // MessageText:
    //
    // A frame consolidation has been executed.
    //
    int ERROR_UNWIND_CONSOLIDATE = 684;

    //
    // MessageId: ERROR_REGISTRY_HIVE_RECOVERED
    //
    // MessageText:
    //
    // {Registry Hive Recovered}
    // Registry hive (file):
    // %hs
    // was corrupted and it has been recovered. Some data might have been lost.
    //
    int ERROR_REGISTRY_HIVE_RECOVERED = 685;

    //
    // MessageId: ERROR_DLL_MIGHT_BE_INSECURE
    //
    // MessageText:
    //
    // The application is attempting to run executable code from the module %hs.
    // This may be insecure. An alternative, %hs, is available. Should the
    // application use the secure module %hs?
    //
    int ERROR_DLL_MIGHT_BE_INSECURE = 686;

    //
    // MessageId: ERROR_DLL_MIGHT_BE_INCOMPATIBLE
    //
    // MessageText:
    //
    // The application is loading executable code from the module %hs. This is
    // secure, but may be incompatible with previous releases of the operating
    // system. An alternative, %hs, is available. Should the application use the
    // secure module %hs?
    //
    int ERROR_DLL_MIGHT_BE_INCOMPATIBLE = 687;

    //
    // MessageId: ERROR_DBG_EXCEPTION_NOT_HANDLED
    //
    // MessageText:
    //
    // Debugger did not handle the exception.
    //
    int ERROR_DBG_EXCEPTION_NOT_HANDLED = 688; // winnt

    //
    // MessageId: ERROR_DBG_REPLY_LATER
    //
    // MessageText:
    //
    // Debugger will reply later.
    //
    int ERROR_DBG_REPLY_LATER = 689;

    //
    // MessageId: ERROR_DBG_UNABLE_TO_PROVIDE_HANDLE
    //
    // MessageText:
    //
    // Debugger cannot provide handle.
    //
    int ERROR_DBG_UNABLE_TO_PROVIDE_HANDLE = 690;

    //
    // MessageId: ERROR_DBG_TERMINATE_THREAD
    //
    // MessageText:
    //
    // Debugger terminated thread.
    //
    int ERROR_DBG_TERMINATE_THREAD = 691; // winnt

    //
    // MessageId: ERROR_DBG_TERMINATE_PROCESS
    //
    // MessageText:
    //
    // Debugger terminated process.
    //
    int ERROR_DBG_TERMINATE_PROCESS = 692; // winnt

    //
    // MessageId: ERROR_DBG_CONTROL_C
    //
    // MessageText:
    //
    // Debugger got control C.
    //
    int ERROR_DBG_CONTROL_C = 693; // winnt

    //
    // MessageId: ERROR_DBG_PRINTEXCEPTION_C
    //
    // MessageText:
    //
    // Debugger printed exception on control C.
    //
    int ERROR_DBG_PRINTEXCEPTION_C = 694;

    //
    // MessageId: ERROR_DBG_RIPEXCEPTION
    //
    // MessageText:
    //
    // Debugger received RIP exception.
    //
    int ERROR_DBG_RIPEXCEPTION = 695;

    //
    // MessageId: ERROR_DBG_CONTROL_BREAK
    //
    // MessageText:
    //
    // Debugger received control break.
    //
    int ERROR_DBG_CONTROL_BREAK = 696; // winnt

    //
    // MessageId: ERROR_DBG_COMMAND_EXCEPTION
    //
    // MessageText:
    //
    // Debugger command communication exception.
    //
    int ERROR_DBG_COMMAND_EXCEPTION = 697; // winnt

    //
    // MessageId: ERROR_OBJECT_NAME_EXISTS
    //
    // MessageText:
    //
    // {Object Exists}
    // An attempt was made to create an object and the object name already
    // existed.
    //
    int ERROR_OBJECT_NAME_EXISTS = 698;

    //
    // MessageId: ERROR_THREAD_WAS_SUSPENDED
    //
    // MessageText:
    //
    // {Thread Suspended}
    // A thread termination occurred while the thread was suspended. The thread
    // was resumed, and termination proceeded.
    //
    int ERROR_THREAD_WAS_SUSPENDED = 699;

    //
    // MessageId: ERROR_IMAGE_NOT_AT_BASE
    //
    // MessageText:
    //
    // {Image Relocated}
    // An image file could not be mapped at the address specified in the image
    // file. Local fixups must be performed on this image.
    //
    int ERROR_IMAGE_NOT_AT_BASE = 700;

    //
    // MessageId: ERROR_RXACT_STATE_CREATED
    //
    // MessageText:
    //
    // This informational level status indicates that a specified registry
    // sub-tree transaction state did not yet exist and had to be created.
    //
    int ERROR_RXACT_STATE_CREATED = 701;

    //
    // MessageId: ERROR_SEGMENT_NOTIFICATION
    //
    // MessageText:
    //
    // {Segment Load}
    // A virtual DOS machine (VDM) is loading, unloading, or moving an MS-DOS or
    // Win16 program segment image.
    // An exception is raised so a debugger can load, unload or track symbols
    // and breakpoints within these=16-bit segments.
    //
    int ERROR_SEGMENT_NOTIFICATION = 702; // winnt

    //
    // MessageId: ERROR_BAD_CURRENT_DIRECTORY
    //
    // MessageText:
    //
    // {Invalid Current Directory}
    // The process cannot switch to the startup current directory %hs.
    // Select OK to set current directory to %hs, or select CANCEL to exit.
    //
    int ERROR_BAD_CURRENT_DIRECTORY = 703;

    //
    // MessageId: ERROR_FT_READ_RECOVERY_FROM_BACKUP
    //
    // MessageText:
    //
    // {Redundant Read}
    // To satisfy a read request, the NT fault-tolerant file system successfully
    // read the requested data from a redundant copy.
    // This was done because the file system encountered a failure on a member
    // of the fault-tolerant volume, but was unable to reassign the failing area
    // of the device.
    //
    int ERROR_FT_READ_RECOVERY_FROM_BACKUP = 704;

    //
    // MessageId: ERROR_FT_WRITE_RECOVERY
    //
    // MessageText:
    //
    // {Redundant Write}
    // To satisfy a write request, the NT fault-tolerant file system
    // successfully wrote a redundant copy of the information.
    // This was done because the file system encountered a failure on a member
    // of the fault-tolerant volume, but was not able to reassign the failing
    // area of the device.
    //
    int ERROR_FT_WRITE_RECOVERY = 705;

    //
    // MessageId: ERROR_IMAGE_MACHINE_TYPE_MISMATCH
    //
    // MessageText:
    //
    // {Machine Type Mismatch}
    // The image file %hs is valid, but is for a machine type other than the
    // current machine. Select OK to continue, or CANCEL to fail the DLL load.
    //
    int ERROR_IMAGE_MACHINE_TYPE_MISMATCH = 706;

    //
    // MessageId: ERROR_RECEIVE_PARTIAL
    //
    // MessageText:
    //
    // {Partial Data Received}
    // The network transport returned partial data to its client. The remaining
    // data will be sent later.
    //
    int ERROR_RECEIVE_PARTIAL = 707;

    //
    // MessageId: ERROR_RECEIVE_EXPEDITED
    //
    // MessageText:
    //
    // {Expedited Data Received}
    // The network transport returned data to its client that was marked as
    // expedited by the remote system.
    //
    int ERROR_RECEIVE_EXPEDITED = 708;

    //
    // MessageId: ERROR_RECEIVE_PARTIAL_EXPEDITED
    //
    // MessageText:
    //
    // {Partial Expedited Data Received}
    // The network transport returned partial data to its client and this data
    // was marked as expedited by the remote system. The remaining data will be
    // sent later.
    //
    int ERROR_RECEIVE_PARTIAL_EXPEDITED = 709;

    //
    // MessageId: ERROR_EVENT_DONE
    //
    // MessageText:
    //
    // {TDI Event Done}
    // The TDI indication has completed successfully.
    //
    int ERROR_EVENT_DONE = 710;

    //
    // MessageId: ERROR_EVENT_PENDING
    //
    // MessageText:
    //
    // {TDI Event Pending}
    // The TDI indication has entered the pending state.
    //
    int ERROR_EVENT_PENDING = 711;

    //
    // MessageId: ERROR_CHECKING_FILE_SYSTEM
    //
    // MessageText:
    //
    // Checking file system on %wZ
    //
    int ERROR_CHECKING_FILE_SYSTEM = 712;

    //
    // MessageId: ERROR_FATAL_APP_EXIT
    //
    // MessageText:
    //
    // {Fatal Application Exit}
    // %hs
    //
    int ERROR_FATAL_APP_EXIT = 713;

    //
    // MessageId: ERROR_PREDEFINED_HANDLE
    //
    // MessageText:
    //
    // The specified registry key is referenced by a predefined handle.
    //
    int ERROR_PREDEFINED_HANDLE = 714;

    //
    // MessageId: ERROR_WAS_UNLOCKED
    //
    // MessageText:
    //
    // {Page Unlocked}
    // The page protection of a locked page was changed to 'No Access' and the
    // page was unlocked from memory and from the process.
    //
    int ERROR_WAS_UNLOCKED = 715;

    //
    // MessageId: ERROR_SERVICE_NOTIFICATION
    //
    // MessageText:
    //
    // %hs
    //
    int ERROR_SERVICE_NOTIFICATION = 716;

    //
    // MessageId: ERROR_WAS_LOCKED
    //
    // MessageText:
    //
    // {Page Locked}
    // One of the pages to lock was already locked.
    //
    int ERROR_WAS_LOCKED = 717;

    //
    // MessageId: ERROR_LOG_HARD_ERROR
    //
    // MessageText:
    //
    // Application popup: %1 : %2
    //
    int ERROR_LOG_HARD_ERROR = 718;

    //
    // MessageId: ERROR_ALREADY_WIN32
    //
    // MessageText:
    //
    // ERROR_ALREADY_WIN32
    //
    int ERROR_ALREADY_WIN32 = 719;

    //
    // MessageId: ERROR_IMAGE_MACHINE_TYPE_MISMATCH_EXE
    //
    // MessageText:
    //
    // {Machine Type Mismatch}
    // The image file %hs is valid, but is for a machine type other than the
    // current machine.
    //
    int ERROR_IMAGE_MACHINE_TYPE_MISMATCH_EXE = 720;

    //
    // MessageId: ERROR_NO_YIELD_PERFORMED
    //
    // MessageText:
    //
    // A yield execution was performed and no thread was available to run.
    //
    int ERROR_NO_YIELD_PERFORMED = 721;

    //
    // MessageId: ERROR_TIMER_RESUME_IGNORED
    //
    // MessageText:
    //
    // The resumable flag to a timer API was ignored.
    //
    int ERROR_TIMER_RESUME_IGNORED = 722;

    //
    // MessageId: ERROR_ARBITRATION_UNHANDLED
    //
    // MessageText:
    //
    // The arbiter has deferred arbitration of these resources to its parent
    //
    int ERROR_ARBITRATION_UNHANDLED = 723;

    //
    // MessageId: ERROR_CARDBUS_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The inserted CardBus device cannot be started because of a configuration
    // error on "%hs".
    //
    int ERROR_CARDBUS_NOT_SUPPORTED = 724;

    //
    // MessageId: ERROR_MP_PROCESSOR_MISMATCH
    //
    // MessageText:
    //
    // The CPUs in this multiprocessor system are not all the same revision
    // level. To use all processors the operating system restricts itself to the
    // features of the least capable processor in the system. Should problems
    // occur with this system, contact the CPU manufacturer to see if this mix
    // of processors is supported.
    //
    int ERROR_MP_PROCESSOR_MISMATCH = 725;

    //
    // MessageId: ERROR_HIBERNATED
    //
    // MessageText:
    //
    // The system was put into hibernation.
    //
    int ERROR_HIBERNATED = 726;

    //
    // MessageId: ERROR_RESUME_HIBERNATION
    //
    // MessageText:
    //
    // The system was resumed from hibernation.
    //
    int ERROR_RESUME_HIBERNATION = 727;

    //
    // MessageId: ERROR_FIRMWARE_UPDATED
    //
    // MessageText:
    //
    // Windows has detected that the system firmware (BIOS) was updated
    // [previous firmware date =%2, current firmware date %3].
    //
    int ERROR_FIRMWARE_UPDATED = 728;

    //
    // MessageId: ERROR_DRIVERS_LEAKING_LOCKED_PAGES
    //
    // MessageText:
    //
    // A device driver is leaking locked I/O pages causing system degradation.
    // The system has automatically enabled tracking code in order to try and
    // catch the culprit.
    //
    int ERROR_DRIVERS_LEAKING_LOCKED_PAGES = 729;

    //
    // MessageId: ERROR_WAKE_SYSTEM
    //
    // MessageText:
    //
    // The system has awoken
    //
    int ERROR_WAKE_SYSTEM = 730;

    //
    // MessageId: ERROR_WAIT_1
    //
    // MessageText:
    //
    // ERROR_WAIT_1
    //
    int ERROR_WAIT_1 = 731;

    //
    // MessageId: ERROR_WAIT_2
    //
    // MessageText:
    //
    // ERROR_WAIT_2
    //
    int ERROR_WAIT_2 = 732;

    //
    // MessageId: ERROR_WAIT_3
    //
    // MessageText:
    //
    // ERROR_WAIT_3
    //
    int ERROR_WAIT_3 = 733;

    //
    // MessageId: ERROR_WAIT_63
    //
    // MessageText:
    //
    // ERROR_WAIT_63
    //
    int ERROR_WAIT_63 = 734;

    //
    // MessageId: ERROR_ABANDONED_WAIT_0
    //
    // MessageText:
    //
    // ERROR_ABANDONED_WAIT_0
    //
    int ERROR_ABANDONED_WAIT_0 = 735; // winnt

    //
    // MessageId: ERROR_ABANDONED_WAIT_63
    //
    // MessageText:
    //
    // ERROR_ABANDONED_WAIT_63
    //
    int ERROR_ABANDONED_WAIT_63 = 736;

    //
    // MessageId: ERROR_USER_APC
    //
    // MessageText:
    //
    // ERROR_USER_APC
    //
    int ERROR_USER_APC = 737; // winnt

    //
    // MessageId: ERROR_KERNEL_APC
    //
    // MessageText:
    //
    // ERROR_KERNEL_APC
    //
    int ERROR_KERNEL_APC = 738;

    //
    // MessageId: ERROR_ALERTED
    //
    // MessageText:
    //
    // ERROR_ALERTED
    //
    int ERROR_ALERTED = 739;

    //
    // MessageId: ERROR_ELEVATION_REQUIRED
    //
    // MessageText:
    //
    // The requested operation requires elevation.
    //
    int ERROR_ELEVATION_REQUIRED = 740;

    //
    // MessageId: ERROR_REPARSE
    //
    // MessageText:
    //
    // A reparse should be performed by the Object Manager since the name of the
    // file resulted in a symbolic link.
    //
    int ERROR_REPARSE = 741;

    //
    // MessageId: ERROR_OPLOCK_BREAK_IN_PROGRESS
    //
    // MessageText:
    //
    // An open/create operation completed while an oplock break is underway.
    //
    int ERROR_OPLOCK_BREAK_IN_PROGRESS = 742;

    //
    // MessageId: ERROR_VOLUME_MOUNTED
    //
    // MessageText:
    //
    // A new volume has been mounted by a file system.
    //
    int ERROR_VOLUME_MOUNTED = 743;

    //
    // MessageId: ERROR_RXACT_COMMITTED
    //
    // MessageText:
    //
    // This success level status indicates that the transaction state already
    // exists for the registry sub-tree, but that a transaction commit was
    // previously aborted.
    // The commit has now been completed.
    //
    int ERROR_RXACT_COMMITTED = 744;

    //
    // MessageId: ERROR_NOTIFY_CLEANUP
    //
    // MessageText:
    //
    // This indicates that a notify change request has been completed due to
    // closing the handle which made the notify change request.
    //
    int ERROR_NOTIFY_CLEANUP = 745;

    //
    // MessageId: ERROR_PRIMARY_TRANSPORT_CONNECT_FAILED
    //
    // MessageText:
    //
    // {Connect Failure on Primary Transport}
    // An attempt was made to connect to the remote server %hs on the primary
    // transport, but the connection failed.
    // The computer WAS able to connect on a secondary transport.
    //
    int ERROR_PRIMARY_TRANSPORT_CONNECT_FAILED = 746;

    //
    // MessageId: ERROR_PAGE_FAULT_TRANSITION
    //
    // MessageText:
    //
    // Page fault was a transition fault.
    //
    int ERROR_PAGE_FAULT_TRANSITION = 747;

    //
    // MessageId: ERROR_PAGE_FAULT_DEMAND_ZERO
    //
    // MessageText:
    //
    // Page fault was a demand zero fault.
    //
    int ERROR_PAGE_FAULT_DEMAND_ZERO = 748;

    //
    // MessageId: ERROR_PAGE_FAULT_COPY_ON_WRITE
    //
    // MessageText:
    //
    // Page fault was a demand zero fault.
    //
    int ERROR_PAGE_FAULT_COPY_ON_WRITE = 749;

    //
    // MessageId: ERROR_PAGE_FAULT_GUARD_PAGE
    //
    // MessageText:
    //
    // Page fault was a demand zero fault.
    //
    int ERROR_PAGE_FAULT_GUARD_PAGE = 750;

    //
    // MessageId: ERROR_PAGE_FAULT_PAGING_FILE
    //
    // MessageText:
    //
    // Page fault was satisfied by reading from a secondary storage device.
    //
    int ERROR_PAGE_FAULT_PAGING_FILE = 751;

    //
    // MessageId: ERROR_CACHE_PAGE_LOCKED
    //
    // MessageText:
    //
    // Cached page was locked during operation.
    //
    int ERROR_CACHE_PAGE_LOCKED = 752;

    //
    // MessageId: ERROR_CRASH_DUMP
    //
    // MessageText:
    //
    // Crash dump exists in paging file.
    //
    int ERROR_CRASH_DUMP = 753;

    //
    // MessageId: ERROR_BUFFER_ALL_ZEROS
    //
    // MessageText:
    //
    // Specified buffer contains all zeros.
    //
    int ERROR_BUFFER_ALL_ZEROS = 754;

    //
    // MessageId: ERROR_REPARSE_OBJECT
    //
    // MessageText:
    //
    // A reparse should be performed by the Object Manager since the name of the
    // file resulted in a symbolic link.
    //
    int ERROR_REPARSE_OBJECT = 755;

    //
    // MessageId: ERROR_RESOURCE_REQUIREMENTS_CHANGED
    //
    // MessageText:
    //
    // The device has succeeded a query-stop and its resource requirements have
    // changed.
    //
    int ERROR_RESOURCE_REQUIREMENTS_CHANGED = 756;

    //
    // MessageId: ERROR_TRANSLATION_COMPLETE
    //
    // MessageText:
    //
    // The translator has translated these resources into the global space and
    // no further translations should be performed.
    //
    int ERROR_TRANSLATION_COMPLETE = 757;

    //
    // MessageId: ERROR_NOTHING_TO_TERMINATE
    //
    // MessageText:
    //
    // A process being terminated has no threads to terminate.
    //
    int ERROR_NOTHING_TO_TERMINATE = 758;

    //
    // MessageId: ERROR_PROCESS_NOT_IN_JOB
    //
    // MessageText:
    //
    // The specified process is not part of a job.
    //
    int ERROR_PROCESS_NOT_IN_JOB = 759;

    //
    // MessageId: ERROR_PROCESS_IN_JOB
    //
    // MessageText:
    //
    // The specified process is part of a job.
    //
    int ERROR_PROCESS_IN_JOB = 760;

    //
    // MessageId: ERROR_VOLSNAP_HIBERNATE_READY
    //
    // MessageText:
    //
    // {Volume Shadow Copy Service}
    // The system is now ready for hibernation.
    //
    int ERROR_VOLSNAP_HIBERNATE_READY = 761;

    //
    // MessageId: ERROR_FSFILTER_OP_COMPLETED_SUCCESSFULLY
    //
    // MessageText:
    //
    // A file system or file system filter driver has successfully completed an
    // FsFilter operation.
    //
    int ERROR_FSFILTER_OP_COMPLETED_SUCCESSFULLY = 762;

    //
    // MessageId: ERROR_INTERRUPT_VECTOR_ALREADY_CONNECTED
    //
    // MessageText:
    //
    // The specified interrupt vector was already connected.
    //
    int ERROR_INTERRUPT_VECTOR_ALREADY_CONNECTED = 763;

    //
    // MessageId: ERROR_INTERRUPT_STILL_CONNECTED
    //
    // MessageText:
    //
    // The specified interrupt vector is still connected.
    //
    int ERROR_INTERRUPT_STILL_CONNECTED = 764;

    //
    // MessageId: ERROR_WAIT_FOR_OPLOCK
    //
    // MessageText:
    //
    // An operation is blocked waiting for an oplock.
    //
    int ERROR_WAIT_FOR_OPLOCK = 765;

    //
    // MessageId: ERROR_DBG_EXCEPTION_HANDLED
    //
    // MessageText:
    //
    // Debugger handled exception
    //
    int ERROR_DBG_EXCEPTION_HANDLED = 766; // winnt

    //
    // MessageId: ERROR_DBG_CONTINUE
    //
    // MessageText:
    //
    // Debugger continued
    //
    int ERROR_DBG_CONTINUE = 767; // winnt

    //
    // MessageId: ERROR_CALLBACK_POP_STACK
    //
    // MessageText:
    //
    // An exception occurred in a user mode callback and the kernel callback
    // frame should be removed.
    //
    int ERROR_CALLBACK_POP_STACK = 768;

    //
    // MessageId: ERROR_COMPRESSION_DISABLED
    //
    // MessageText:
    //
    // Compression is disabled for this volume.
    //
    int ERROR_COMPRESSION_DISABLED = 769;

    //
    // MessageId: ERROR_CANTFETCHBACKWARDS
    //
    // MessageText:
    //
    // The data provider cannot fetch backwards through a result set.
    //
    int ERROR_CANTFETCHBACKWARDS = 770;

    //
    // MessageId: ERROR_CANTSCROLLBACKWARDS
    //
    // MessageText:
    //
    // The data provider cannot scroll backwards through a result set.
    //
    int ERROR_CANTSCROLLBACKWARDS = 771;

    //
    // MessageId: ERROR_ROWSNOTRELEASED
    //
    // MessageText:
    //
    // The data provider requires that previously fetched data is released
    // before asking for more data.
    //
    int ERROR_ROWSNOTRELEASED = 772;

    //
    // MessageId: ERROR_BAD_ACCESSOR_FLAGS
    //
    // MessageText:
    //
    // The data provider was not able to intrepret the flags set for a column
    // binding in an accessor.
    //
    int ERROR_BAD_ACCESSOR_FLAGS = 773;

    //
    // MessageId: ERROR_ERRORS_ENCOUNTERED
    //
    // MessageText:
    //
    // One or more errors occurred while processing the request.
    //
    int ERROR_ERRORS_ENCOUNTERED = 774;

    //
    // MessageId: ERROR_NOT_CAPABLE
    //
    // MessageText:
    //
    // The implementation is not capable of performing the request.
    //
    int ERROR_NOT_CAPABLE = 775;

    //
    // MessageId: ERROR_REQUEST_OUT_OF_SEQUENCE
    //
    // MessageText:
    //
    // The client of a component requested an operation which is not valid given
    // the state of the component instance.
    //
    int ERROR_REQUEST_OUT_OF_SEQUENCE = 776;

    //
    // MessageId: ERROR_VERSION_PARSE_ERROR
    //
    // MessageText:
    //
    // A version number could not be parsed.
    //
    int ERROR_VERSION_PARSE_ERROR = 777;

    //
    // MessageId: ERROR_BADSTARTPOSITION
    //
    // MessageText:
    //
    // The iterator's start position is invalid.
    //
    int ERROR_BADSTARTPOSITION = 778;

    //
    // MessageId: ERROR_MEMORY_HARDWARE
    //
    // MessageText:
    //
    // The hardware has reported an uncorrectable memory error.
    //
    int ERROR_MEMORY_HARDWARE = 779;

    //
    // MessageId: ERROR_DISK_REPAIR_DISABLED
    //
    // MessageText:
    //
    // The attempted operation required self healing to be enabled.
    //
    int ERROR_DISK_REPAIR_DISABLED = 780;

    //
    // MessageId: ERROR_INSUFFICIENT_RESOURCE_FOR_SPECIFIED_SHARED_SECTION_SIZE
    //
    // MessageText:
    //
    // The Desktop heap encountered an error while allocating session memory.
    // There is more information in the system event log.
    //
    int ERROR_INSUFFICIENT_RESOURCE_FOR_SPECIFIED_SHARED_SECTION_SIZE = 781;

    //
    // MessageId: ERROR_SYSTEM_POWERSTATE_TRANSITION
    //
    // MessageText:
    //
    // The system powerstate is transitioning from %2 to %3.
    //
    int ERROR_SYSTEM_POWERSTATE_TRANSITION = 782;

    //
    // MessageId: ERROR_SYSTEM_POWERSTATE_COMPLEX_TRANSITION
    //
    // MessageText:
    //
    // The system powerstate is transitioning from %2 to %3 but could enter %4.
    //
    int ERROR_SYSTEM_POWERSTATE_COMPLEX_TRANSITION = 783;

    //
    // MessageId: ERROR_MCA_EXCEPTION
    //
    // MessageText:
    //
    // A thread is getting dispatched with MCA EXCEPTION because of MCA.
    //
    int ERROR_MCA_EXCEPTION = 784;

    //
    // MessageId: ERROR_ACCESS_AUDIT_BY_POLICY
    //
    // MessageText:
    //
    // Access to %1 is monitored by policy rule %2.
    //
    int ERROR_ACCESS_AUDIT_BY_POLICY = 785;

    //
    // MessageId: ERROR_ACCESS_DISABLED_NO_SAFER_UI_BY_POLICY
    //
    // MessageText:
    //
    // Access to %1 has been restricted by your Administrator by policy rule %2.
    //
    int ERROR_ACCESS_DISABLED_NO_SAFER_UI_BY_POLICY = 786;

    //
    // MessageId: ERROR_ABANDON_HIBERFILE
    //
    // MessageText:
    //
    // A valid hibernation file has been invalidated and should be abandoned.
    //
    int ERROR_ABANDON_HIBERFILE = 787;

    //
    // MessageId: ERROR_LOST_WRITEBEHIND_DATA_NETWORK_DISCONNECTED
    //
    // MessageText:
    //
    // {Delayed Write Failed}
    // Windows was unable to save all the data for the file %hs; the data has
    // been lost.
    // This error may be caused by network connectivity issues. Please try to
    // save this file elsewhere.
    //
    int ERROR_LOST_WRITEBEHIND_DATA_NETWORK_DISCONNECTED = 788;

    //
    // MessageId: ERROR_LOST_WRITEBEHIND_DATA_NETWORK_SERVER_ERROR
    //
    // MessageText:
    //
    // {Delayed Write Failed}
    // Windows was unable to save all the data for the file %hs; the data has
    // been lost.
    // This error was returned by the server on which the file exists. Please
    // try to save this file elsewhere.
    //
    int ERROR_LOST_WRITEBEHIND_DATA_NETWORK_SERVER_ERROR = 789;

    //
    // MessageId: ERROR_LOST_WRITEBEHIND_DATA_LOCAL_DISK_ERROR
    //
    // MessageText:
    //
    // {Delayed Write Failed}
    // Windows was unable to save all the data for the file %hs; the data has
    // been lost.
    // This error may be caused if the device has been removed or the media is
    // write-protected.
    //
    int ERROR_LOST_WRITEBEHIND_DATA_LOCAL_DISK_ERROR = 790;

    //
    // MessageId: ERROR_BAD_MCFG_TABLE
    //
    // MessageText:
    //
    // The resources required for this device conflict with the MCFG table.
    //
    int ERROR_BAD_MCFG_TABLE = 791;

    //
    // MessageId: ERROR_EA_ACCESS_DENIED
    //
    // MessageText:
    //
    // Access to the extended attribute was denied.
    //
    int ERROR_EA_ACCESS_DENIED = 994;

    //
    // MessageId: ERROR_OPERATION_ABORTED
    //
    // MessageText:
    //
    // The I/O operation has been aborted because of either a thread exit or an
    // application request.
    //
    int ERROR_OPERATION_ABORTED = 995;

    //
    // MessageId: ERROR_IO_INCOMPLETE
    //
    // MessageText:
    //
    // Overlapped I/O event is not in a signaled state.
    //
    int ERROR_IO_INCOMPLETE = 996;

    //
    // MessageId: ERROR_IO_PENDING
    //
    // MessageText:
    //
    // Overlapped I/O operation is in progress.
    //
    int ERROR_IO_PENDING = 997; // dderror

    //
    // MessageId: ERROR_NOACCESS
    //
    // MessageText:
    //
    // Invalid access to memory location.
    //
    int ERROR_NOACCESS = 998;

    //
    // MessageId: ERROR_SWAPERROR
    //
    // MessageText:
    //
    // Error performing inpage operation.
    //
    int ERROR_SWAPERROR = 999;

    //
    // MessageId: ERROR_STACK_OVERFLOW
    //
    // MessageText:
    //
    // Recursion too deep; the stack overflowed.
    //
    int ERROR_STACK_OVERFLOW = 1001;

    //
    // MessageId: ERROR_INVALID_MESSAGE
    //
    // MessageText:
    //
    // The window cannot act on the sent message.
    //
    int ERROR_INVALID_MESSAGE = 1002;

    //
    // MessageId: ERROR_CAN_NOT_COMPLETE
    //
    // MessageText:
    //
    // Cannot complete this function.
    //
    int ERROR_CAN_NOT_COMPLETE = 1003;

    //
    // MessageId: ERROR_INVALID_FLAGS
    //
    // MessageText:
    //
    // Invalid flags.
    //
    int ERROR_INVALID_FLAGS = 1004;

    //
    // MessageId: ERROR_UNRECOGNIZED_VOLUME
    //
    // MessageText:
    //
    // The volume does not contain a recognized file system.
    // Please make sure that all required file system drivers are loaded and
    // that the volume is not corrupted.
    //
    int ERROR_UNRECOGNIZED_VOLUME = 1005;

    //
    // MessageId: ERROR_FILE_INVALID
    //
    // MessageText:
    //
    // The volume for a file has been externally altered so that the opened file
    // is no longer valid.
    //
    int ERROR_FILE_INVALID = 1006;

    //
    // MessageId: ERROR_FULLSCREEN_MODE
    //
    // MessageText:
    //
    // The requested operation cannot be performed in full-screen mode.
    //
    int ERROR_FULLSCREEN_MODE = 1007;

    //
    // MessageId: ERROR_NO_TOKEN
    //
    // MessageText:
    //
    // An attempt was made to reference a token that does not exist.
    //
    int ERROR_NO_TOKEN = 1008;

    //
    // MessageId: ERROR_BADDB
    //
    // MessageText:
    //
    // The configuration registry database is corrupt.
    //
    int ERROR_BADDB = 1009;

    //
    // MessageId: ERROR_BADKEY
    //
    // MessageText:
    //
    // The configuration registry key is invalid.
    //
    int ERROR_BADKEY = 1010;

    //
    // MessageId: ERROR_CANTOPEN
    //
    // MessageText:
    //
    // The configuration registry key could not be opened.
    //
    int ERROR_CANTOPEN = 1011;

    //
    // MessageId: ERROR_CANTREAD
    //
    // MessageText:
    //
    // The configuration registry key could not be read.
    //
    int ERROR_CANTREAD = 1012;

    //
    // MessageId: ERROR_CANTWRITE
    //
    // MessageText:
    //
    // The configuration registry key could not be written.
    //
    int ERROR_CANTWRITE = 1013;

    //
    // MessageId: ERROR_REGISTRY_RECOVERED
    //
    // MessageText:
    //
    // One of the files in the registry database had to be recovered by use of a
    // log or alternate copy. The recovery was successful.
    //
    int ERROR_REGISTRY_RECOVERED = 1014;

    //
    // MessageId: ERROR_REGISTRY_CORRUPT
    //
    // MessageText:
    //
    // The registry is corrupted. The structure of one of the files containing
    // registry data is corrupted, or the system's memory image of the file is
    // corrupted, or the file could not be recovered because the alternate copy
    // or log was absent or corrupted.
    //
    int ERROR_REGISTRY_CORRUPT = 1015;

    //
    // MessageId: ERROR_REGISTRY_IO_FAILED
    //
    // MessageText:
    //
    // An I/O operation initiated by the registry failed unrecoverably. The
    // registry could not read in, or write out, or flush, one of the files that
    // contain the system's image of the registry.
    //
    int ERROR_REGISTRY_IO_FAILED = 1016;

    //
    // MessageId: ERROR_NOT_REGISTRY_FILE
    //
    // MessageText:
    //
    // The system has attempted to load or restore a file into the registry, but
    // the specified file is not in a registry file format.
    //
    int ERROR_NOT_REGISTRY_FILE = 1017;

    //
    // MessageId: ERROR_KEY_DELETED
    //
    // MessageText:
    //
    // Illegal operation attempted on a registry key that has been marked for
    // deletion.
    //
    int ERROR_KEY_DELETED = 1018;

    //
    // MessageId: ERROR_NO_LOG_SPACE
    //
    // MessageText:
    //
    // System could not allocate the required space in a registry log.
    //
    int ERROR_NO_LOG_SPACE = 1019;

    //
    // MessageId: ERROR_KEY_HAS_CHILDREN
    //
    // MessageText:
    //
    // Cannot create a symbolic link in a registry key that already has subkeys
    // or values.
    //
    int ERROR_KEY_HAS_CHILDREN = 1020;

    //
    // MessageId: ERROR_CHILD_MUST_BE_VOLATILE
    //
    // MessageText:
    //
    // Cannot create a stable subkey under a volatile parent key.
    //
    int ERROR_CHILD_MUST_BE_VOLATILE = 1021;

    //
    // MessageId: ERROR_NOTIFY_ENUM_DIR
    //
    // MessageText:
    //
    // A notify change request is being completed and the information is not
    // being returned in the caller's buffer. The caller now needs to enumerate
    // the files to find the changes.
    //
    int ERROR_NOTIFY_ENUM_DIR = 1022;

    //
    // MessageId: ERROR_DEPENDENT_SERVICES_RUNNING
    //
    // MessageText:
    //
    // A stop control has been sent to a service that other running services are
    // dependent on.
    //
    int ERROR_DEPENDENT_SERVICES_RUNNING = 1051;

    //
    // MessageId: ERROR_INVALID_SERVICE_CONTROL
    //
    // MessageText:
    //
    // The requested control is not valid for this service.
    //
    int ERROR_INVALID_SERVICE_CONTROL = 1052;

    //
    // MessageId: ERROR_SERVICE_REQUEST_TIMEOUT
    //
    // MessageText:
    //
    // The service did not respond to the start or control request in a timely
    // fashion.
    //
    int ERROR_SERVICE_REQUEST_TIMEOUT = 1053;

    //
    // MessageId: ERROR_SERVICE_NO_THREAD
    //
    // MessageText:
    //
    // A thread could not be created for the service.
    //
    int ERROR_SERVICE_NO_THREAD = 1054;

    //
    // MessageId: ERROR_SERVICE_DATABASE_LOCKED
    //
    // MessageText:
    //
    // The service database is locked.
    //
    int ERROR_SERVICE_DATABASE_LOCKED = 1055;

    //
    // MessageId: ERROR_SERVICE_ALREADY_RUNNING
    //
    // MessageText:
    //
    // An instance of the service is already running.
    //
    int ERROR_SERVICE_ALREADY_RUNNING = 1056;

    //
    // MessageId: ERROR_INVALID_SERVICE_ACCOUNT
    //
    // MessageText:
    //
    // The account name is invalid or does not exist, or the password is invalid
    // for the account name specified.
    //
    int ERROR_INVALID_SERVICE_ACCOUNT = 1057;

    //
    // MessageId: ERROR_SERVICE_DISABLED
    //
    // MessageText:
    //
    // The service cannot be started, either because it is disabled or because
    // it has no enabled devices associated with it.
    //
    int ERROR_SERVICE_DISABLED = 1058;

    //
    // MessageId: ERROR_CIRCULAR_DEPENDENCY
    //
    // MessageText:
    //
    // Circular service dependency was specified.
    //
    int ERROR_CIRCULAR_DEPENDENCY = 1059;

    //
    // MessageId: ERROR_SERVICE_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // The specified service does not exist as an installed service.
    //
    int ERROR_SERVICE_DOES_NOT_EXIST = 1060;

    //
    // MessageId: ERROR_SERVICE_CANNOT_ACCEPT_CTRL
    //
    // MessageText:
    //
    // The service cannot accept control messages at this time.
    //
    int ERROR_SERVICE_CANNOT_ACCEPT_CTRL = 1061;

    //
    // MessageId: ERROR_SERVICE_NOT_ACTIVE
    //
    // MessageText:
    //
    // The service has not been started.
    //
    int ERROR_SERVICE_NOT_ACTIVE = 1062;

    //
    // MessageId: ERROR_FAILED_SERVICE_CONTROLLER_CONNECT
    //
    // MessageText:
    //
    // The service process could not connect to the service controller.
    //
    int ERROR_FAILED_SERVICE_CONTROLLER_CONNECT = 1063;

    //
    // MessageId: ERROR_EXCEPTION_IN_SERVICE
    //
    // MessageText:
    //
    // An exception occurred in the service when handling the control request.
    //
    int ERROR_EXCEPTION_IN_SERVICE = 1064;

    //
    // MessageId: ERROR_DATABASE_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // The database specified does not exist.
    //
    int ERROR_DATABASE_DOES_NOT_EXIST = 1065;

    //
    // MessageId: ERROR_SERVICE_SPECIFIC_ERROR
    //
    // MessageText:
    //
    // The service has returned a service-specific error code.
    //
    int ERROR_SERVICE_SPECIFIC_ERROR = 1066;

    //
    // MessageId: ERROR_PROCESS_ABORTED
    //
    // MessageText:
    //
    // The process terminated unexpectedly.
    //
    int ERROR_PROCESS_ABORTED = 1067;

    //
    // MessageId: ERROR_SERVICE_DEPENDENCY_FAIL
    //
    // MessageText:
    //
    // The dependency service or group failed to start.
    //
    int ERROR_SERVICE_DEPENDENCY_FAIL = 1068;

    //
    // MessageId: ERROR_SERVICE_LOGON_FAILED
    //
    // MessageText:
    //
    // The service did not start due to a logon failure.
    //
    int ERROR_SERVICE_LOGON_FAILED = 1069;

    //
    // MessageId: ERROR_SERVICE_START_HANG
    //
    // MessageText:
    //
    // After starting, the service hung in a start-pending state.
    //
    int ERROR_SERVICE_START_HANG = 1070;

    //
    // MessageId: ERROR_INVALID_SERVICE_LOCK
    //
    // MessageText:
    //
    // The specified service database lock is invalid.
    //
    int ERROR_INVALID_SERVICE_LOCK = 1071;

    //
    // MessageId: ERROR_SERVICE_MARKED_FOR_DELETE
    //
    // MessageText:
    //
    // The specified service has been marked for deletion.
    //
    int ERROR_SERVICE_MARKED_FOR_DELETE = 1072;

    //
    // MessageId: ERROR_SERVICE_EXISTS
    //
    // MessageText:
    //
    // The specified service already exists.
    //
    int ERROR_SERVICE_EXISTS = 1073;

    //
    // MessageId: ERROR_ALREADY_RUNNING_LKG
    //
    // MessageText:
    //
    // The system is currently running with the last-known-good configuration.
    //
    int ERROR_ALREADY_RUNNING_LKG = 1074;

    //
    // MessageId: ERROR_SERVICE_DEPENDENCY_DELETED
    //
    // MessageText:
    //
    // The dependency service does not exist or has been marked for deletion.
    //
    int ERROR_SERVICE_DEPENDENCY_DELETED = 1075;

    //
    // MessageId: ERROR_BOOT_ALREADY_ACCEPTED
    //
    // MessageText:
    //
    // The current boot has already been accepted for use as the last-known-good
    // control set.
    //
    int ERROR_BOOT_ALREADY_ACCEPTED = 1076;

    //
    // MessageId: ERROR_SERVICE_NEVER_STARTED
    //
    // MessageText:
    //
    // No attempts to start the service have been made since the last boot.
    //
    int ERROR_SERVICE_NEVER_STARTED = 1077;

    //
    // MessageId: ERROR_DUPLICATE_SERVICE_NAME
    //
    // MessageText:
    //
    // The name is already in use as either a service name or a service display
    // name.
    //
    int ERROR_DUPLICATE_SERVICE_NAME = 1078;

    //
    // MessageId: ERROR_DIFFERENT_SERVICE_ACCOUNT
    //
    // MessageText:
    //
    // The account specified for this service is different from the account
    // specified for other services running in the same process.
    //
    int ERROR_DIFFERENT_SERVICE_ACCOUNT = 1079;

    //
    // MessageId: ERROR_CANNOT_DETECT_DRIVER_FAILURE
    //
    // MessageText:
    //
    // Failure actions can only be set for Win32 services, not for drivers.
    //
    int ERROR_CANNOT_DETECT_DRIVER_FAILURE = 1080;

    //
    // MessageId: ERROR_CANNOT_DETECT_PROCESS_ABORT
    //
    // MessageText:
    //
    // This service runs in the same process as the service control manager.
    // Therefore, the service control manager cannot take action if this
    // service's process terminates unexpectedly.
    //
    int ERROR_CANNOT_DETECT_PROCESS_ABORT = 1081;

    //
    // MessageId: ERROR_NO_RECOVERY_PROGRAM
    //
    // MessageText:
    //
    // No recovery program has been configured for this service.
    //
    int ERROR_NO_RECOVERY_PROGRAM = 1082;

    //
    // MessageId: ERROR_SERVICE_NOT_IN_EXE
    //
    // MessageText:
    //
    // The executable program that this service is configured to run in does not
    // implement the service.
    //
    int ERROR_SERVICE_NOT_IN_EXE = 1083;

    //
    // MessageId: ERROR_NOT_SAFEBOOT_SERVICE
    //
    // MessageText:
    //
    // This service cannot be started in Safe Mode
    //
    int ERROR_NOT_SAFEBOOT_SERVICE = 1084;

    //
    // MessageId: ERROR_END_OF_MEDIA
    //
    // MessageText:
    //
    // The physical end of the tape has been reached.
    //
    int ERROR_END_OF_MEDIA = 1100;

    //
    // MessageId: ERROR_FILEMARK_DETECTED
    //
    // MessageText:
    //
    // A tape access reached a filemark.
    //
    int ERROR_FILEMARK_DETECTED = 1101;

    //
    // MessageId: ERROR_BEGINNING_OF_MEDIA
    //
    // MessageText:
    //
    // The beginning of the tape or a partition was encountered.
    //
    int ERROR_BEGINNING_OF_MEDIA = 1102;

    //
    // MessageId: ERROR_SETMARK_DETECTED
    //
    // MessageText:
    //
    // A tape access reached the end of a set of files.
    //
    int ERROR_SETMARK_DETECTED = 1103;

    //
    // MessageId: ERROR_NO_DATA_DETECTED
    //
    // MessageText:
    //
    // No more data is on the tape.
    //
    int ERROR_NO_DATA_DETECTED = 1104;

    //
    // MessageId: ERROR_PARTITION_FAILURE
    //
    // MessageText:
    //
    // Tape could not be partitioned.
    //
    int ERROR_PARTITION_FAILURE = 1105;

    //
    // MessageId: ERROR_INVALID_BLOCK_LENGTH
    //
    // MessageText:
    //
    // When accessing a new tape of a multivolume partition, the current block
    // size is incorrect.
    //
    int ERROR_INVALID_BLOCK_LENGTH = 1106;

    //
    // MessageId: ERROR_DEVICE_NOT_PARTITIONED
    //
    // MessageText:
    //
    // Tape partition information could not be found when loading a tape.
    //
    int ERROR_DEVICE_NOT_PARTITIONED = 1107;

    //
    // MessageId: ERROR_UNABLE_TO_LOCK_MEDIA
    //
    // MessageText:
    //
    // Unable to lock the media eject mechanism.
    //
    int ERROR_UNABLE_TO_LOCK_MEDIA = 1108;

    //
    // MessageId: ERROR_UNABLE_TO_UNLOAD_MEDIA
    //
    // MessageText:
    //
    // Unable to unload the media.
    //
    int ERROR_UNABLE_TO_UNLOAD_MEDIA = 1109;

    //
    // MessageId: ERROR_MEDIA_CHANGED
    //
    // MessageText:
    //
    // The media in the drive may have changed.
    //
    int ERROR_MEDIA_CHANGED = 1110;

    //
    // MessageId: ERROR_BUS_RESET
    //
    // MessageText:
    //
    // The I/O bus was reset.
    //
    int ERROR_BUS_RESET = 1111;

    //
    // MessageId: ERROR_NO_MEDIA_IN_DRIVE
    //
    // MessageText:
    //
    // No media in drive.
    //
    int ERROR_NO_MEDIA_IN_DRIVE = 1112;

    //
    // MessageId: ERROR_NO_UNICODE_TRANSLATION
    //
    // MessageText:
    //
    // No mapping for the Unicode character exists in the target multi-byte code
    // page.
    //
    int ERROR_NO_UNICODE_TRANSLATION = 1113;

    //
    // MessageId: ERROR_DLL_INIT_FAILED
    //
    // MessageText:
    //
    // A dynamic link library (DL; initialization routine failed.
    //
    int ERROR_DLL_INIT_FAILED = 1114;

    //
    // MessageId: ERROR_SHUTDOWN_IN_PROGRESS
    //
    // MessageText:
    //
    // A system shutdown is in progress.
    //
    int ERROR_SHUTDOWN_IN_PROGRESS = 1115;

    //
    // MessageId: ERROR_NO_SHUTDOWN_IN_PROGRESS
    //
    // MessageText:
    //
    // Unable to abort the system shutdown because no shutdown was in progress.
    //
    int ERROR_NO_SHUTDOWN_IN_PROGRESS = 1116;

    //
    // MessageId: ERROR_IO_DEVICE
    //
    // MessageText:
    //
    // The request could not be performed because of an I/O device error.
    //
    int ERROR_IO_DEVICE = 1117;

    //
    // MessageId: ERROR_SERIAL_NO_DEVICE
    //
    // MessageText:
    //
    // No serial device was successfully initialized. The serial driver will
    // unload.
    //
    int ERROR_SERIAL_NO_DEVICE = 1118;

    //
    // MessageId: ERROR_IRQ_BUSY
    //
    // MessageText:
    //
    // Unable to open a device that was sharing an interrupt request (IRQ) with
    // other devices. At least one other device that uses that IRQ was already
    // opened.
    //
    int ERROR_IRQ_BUSY = 1119;

    //
    // MessageId: ERROR_MORE_WRITES
    //
    // MessageText:
    //
    // A serial I/O operation was completed by another write to the serial port.
    // (The IOCTL_SERIAL_XOFF_COUNTER reached zero.)
    //
    int ERROR_MORE_WRITES = 1120;

    //
    // MessageId: ERROR_COUNTER_TIMEOUT
    //
    // MessageText:
    //
    // A serial I/O operation completed because the timeout period expired.
    // (The IOCTL_SERIAL_XOFF_COUNTER did not reach zero.)
    //
    int ERROR_COUNTER_TIMEOUT = 1121;

    //
    // MessageId: ERROR_FLOPPY_ID_MARK_NOT_FOUND
    //
    // MessageText:
    //
    // No ID address mark was found on the floppy disk.
    //
    int ERROR_FLOPPY_ID_MARK_NOT_FOUND = 1122;

    //
    // MessageId: ERROR_FLOPPY_WRONG_CYLINDER
    //
    // MessageText:
    //
    // Mismatch between the floppy disk sector ID field and the floppy disk
    // controller track address.
    //
    int ERROR_FLOPPY_WRONG_CYLINDER = 1123;

    //
    // MessageId: ERROR_FLOPPY_UNKNOWN_ERROR
    //
    // MessageText:
    //
    // The floppy disk controller reported an error that is not recognized by
    // the floppy disk driver.
    //
    int ERROR_FLOPPY_UNKNOWN_ERROR = 1124;

    //
    // MessageId: ERROR_FLOPPY_BAD_REGISTERS
    //
    // MessageText:
    //
    // The floppy disk controller returned inconsistent results in its
    // registers.
    //
    int ERROR_FLOPPY_BAD_REGISTERS = 1125;

    //
    // MessageId: ERROR_DISK_RECALIBRATE_FAILED
    //
    // MessageText:
    //
    // While accessing the hard disk, a recalibrate operation failed, even after
    // retries.
    //
    int ERROR_DISK_RECALIBRATE_FAILED = 1126;

    //
    // MessageId: ERROR_DISK_OPERATION_FAILED
    //
    // MessageText:
    //
    // While accessing the hard disk, a disk operation failed even after
    // retries.
    //
    int ERROR_DISK_OPERATION_FAILED = 1127;

    //
    // MessageId: ERROR_DISK_RESET_FAILED
    //
    // MessageText:
    //
    // While accessing the hard disk, a disk controller reset was needed, but
    // even that failed.
    //
    int ERROR_DISK_RESET_FAILED = 1128;

    //
    // MessageId: ERROR_EOM_OVERFLOW
    //
    // MessageText:
    //
    // Physical end of tape encountered.
    //
    int ERROR_EOM_OVERFLOW = 1129;

    //
    // MessageId: ERROR_NOT_ENOUGH_SERVER_MEMORY
    //
    // MessageText:
    //
    // Not enough server storage is available to process this command.
    //
    int ERROR_NOT_ENOUGH_SERVER_MEMORY = 1130;

    //
    // MessageId: ERROR_POSSIBLE_DEADLOCK
    //
    // MessageText:
    //
    // A potential deadlock condition has been detected.
    //
    int ERROR_POSSIBLE_DEADLOCK = 1131;

    //
    // MessageId: ERROR_MAPPED_ALIGNMENT
    //
    // MessageText:
    //
    // The base address or the file offset specified does not have the proper
    // alignment.
    //
    int ERROR_MAPPED_ALIGNMENT = 1132;

    //
    // MessageId: ERROR_SET_POWER_STATE_VETOED
    //
    // MessageText:
    //
    // An attempt to change the system power state was vetoed by another
    // application or driver.
    //
    int ERROR_SET_POWER_STATE_VETOED = 1140;

    //
    // MessageId: ERROR_SET_POWER_STATE_FAILED
    //
    // MessageText:
    //
    // The system BIOS failed an attempt to change the system power state.
    //
    int ERROR_SET_POWER_STATE_FAILED = 1141;

    //
    // MessageId: ERROR_TOO_MANY_LINKS
    //
    // MessageText:
    //
    // An attempt was made to create more links on a file than the file system
    // supports.
    //
    int ERROR_TOO_MANY_LINKS = 1142;

    //
    // MessageId: ERROR_OLD_WIN_VERSION
    //
    // MessageText:
    //
    // The specified program requires a newer version of Windows.
    //
    int ERROR_OLD_WIN_VERSION = 1150;

    //
    // MessageId: ERROR_APP_WRONG_OS
    //
    // MessageText:
    //
    // The specified program is not a Windows or MS-DOS program.
    //
    int ERROR_APP_WRONG_OS = 1151;

    //
    // MessageId: ERROR_SINGLE_INSTANCE_APP
    //
    // MessageText:
    //
    // Cannot start more than one instance of the specified program.
    //
    int ERROR_SINGLE_INSTANCE_APP = 1152;

    //
    // MessageId: ERROR_RMODE_APP
    //
    // MessageText:
    //
    // The specified program was written for an earlier version of Windows.
    //
    int ERROR_RMODE_APP = 1153;

    //
    // MessageId: ERROR_INVALID_DLL
    //
    // MessageText:
    //
    // One of the library files needed to run this application is damaged.
    //
    int ERROR_INVALID_DLL = 1154;

    //
    // MessageId: ERROR_NO_ASSOCIATION
    //
    // MessageText:
    //
    // No application is associated with the specified file for this operation.
    //
    int ERROR_NO_ASSOCIATION = 1155;

    //
    // MessageId: ERROR_DDE_FAIL
    //
    // MessageText:
    //
    // An error occurred in sending the command to the application.
    //
    int ERROR_DDE_FAIL = 1156;

    //
    // MessageId: ERROR_DLL_NOT_FOUND
    //
    // MessageText:
    //
    // One of the library files needed to run this application cannot be found.
    //
    int ERROR_DLL_NOT_FOUND = 1157;

    //
    // MessageId: ERROR_NO_MORE_USER_HANDLES
    //
    // MessageText:
    //
    // The current process has used all of its system allowance of handles for
    // Window Manager objects.
    //
    int ERROR_NO_MORE_USER_HANDLES = 1158;

    //
    // MessageId: ERROR_MESSAGE_SYNC_ONLY
    //
    // MessageText:
    //
    // The message can be used only with synchronous operations.
    //
    int ERROR_MESSAGE_SYNC_ONLY = 1159;

    //
    // MessageId: ERROR_SOURCE_ELEMENT_EMPTY
    //
    // MessageText:
    //
    // The indicated source element has no media.
    //
    int ERROR_SOURCE_ELEMENT_EMPTY = 1160;

    //
    // MessageId: ERROR_DESTINATION_ELEMENT_FULL
    //
    // MessageText:
    //
    // The indicated destination element already contains media.
    //
    int ERROR_DESTINATION_ELEMENT_FULL = 1161;

    //
    // MessageId: ERROR_ILLEGAL_ELEMENT_ADDRESS
    //
    // MessageText:
    //
    // The indicated element does not exist.
    //
    int ERROR_ILLEGAL_ELEMENT_ADDRESS = 1162;

    //
    // MessageId: ERROR_MAGAZINE_NOT_PRESENT
    //
    // MessageText:
    //
    // The indicated element is part of a magazine that is not present.
    //
    int ERROR_MAGAZINE_NOT_PRESENT = 1163;

    //
    // MessageId: ERROR_DEVICE_REINITIALIZATION_NEEDED
    //
    // MessageText:
    //
    // The indicated device requires reinitialization due to hardware errors.
    //
    int ERROR_DEVICE_REINITIALIZATION_NEEDED = 1164; // dderror

    //
    // MessageId: ERROR_DEVICE_REQUIRES_CLEANING
    //
    // MessageText:
    //
    // The device has indicated that cleaning is required before further
    // operations are attempted.
    //
    int ERROR_DEVICE_REQUIRES_CLEANING = 1165;

    //
    // MessageId: ERROR_DEVICE_DOOR_OPEN
    //
    // MessageText:
    //
    // The device has indicated that its door is open.
    //
    int ERROR_DEVICE_DOOR_OPEN = 1166;

    //
    // MessageId: ERROR_DEVICE_NOT_CONNECTED
    //
    // MessageText:
    //
    // The device is not connected.
    //
    int ERROR_DEVICE_NOT_CONNECTED = 1167;

    //
    // MessageId: ERROR_NOT_FOUND
    //
    // MessageText:
    //
    // Element not found.
    //
    int ERROR_NOT_FOUND = 1168;

    //
    // MessageId: ERROR_NO_MATCH
    //
    // MessageText:
    //
    // There was no match for the specified key in the index.
    //
    int ERROR_NO_MATCH = 1169;

    //
    // MessageId: ERROR_SET_NOT_FOUND
    //
    // MessageText:
    //
    // The property set specified does not exist on the object.
    //
    int ERROR_SET_NOT_FOUND = 1170;

    //
    // MessageId: ERROR_POINT_NOT_FOUND
    //
    // MessageText:
    //
    // The point passed to GetMouseMovePoints is not in the buffer.
    //
    int ERROR_POINT_NOT_FOUND = 1171;

    //
    // MessageId: ERROR_NO_TRACKING_SERVICE
    //
    // MessageText:
    //
    // The tracking (workstation) service is not running.
    //
    int ERROR_NO_TRACKING_SERVICE = 1172;

    //
    // MessageId: ERROR_NO_VOLUME_ID
    //
    // MessageText:
    //
    // The Volume ID could not be found.
    //
    int ERROR_NO_VOLUME_ID = 1173;

    //
    // MessageId: ERROR_UNABLE_TO_REMOVE_REPLACED
    //
    // MessageText:
    //
    // Unable to remove the file to be replaced.
    //
    int ERROR_UNABLE_TO_REMOVE_REPLACED = 1175;

    //
    // MessageId: ERROR_UNABLE_TO_MOVE_REPLACEMENT
    //
    // MessageText:
    //
    // Unable to move the replacement file to the file to be replaced. The file
    // to be replaced has retained its original name.
    //
    int ERROR_UNABLE_TO_MOVE_REPLACEMENT = 1176;

    //
    // MessageId: ERROR_UNABLE_TO_MOVE_REPLACEMENT_2
    //
    // MessageText:
    //
    // Unable to move the replacement file to the file to be replaced. The file
    // to be replaced has been renamed using the backup name.
    //
    int ERROR_UNABLE_TO_MOVE_REPLACEMENT_2 = 1177;

    //
    // MessageId: ERROR_JOURNAL_DELETE_IN_PROGRESS
    //
    // MessageText:
    //
    // The volume change journal is being deleted.
    //
    int ERROR_JOURNAL_DELETE_IN_PROGRESS = 1178;

    //
    // MessageId: ERROR_JOURNAL_NOT_ACTIVE
    //
    // MessageText:
    //
    // The volume change journal is not active.
    //
    int ERROR_JOURNAL_NOT_ACTIVE = 1179;

    //
    // MessageId: ERROR_POTENTIAL_FILE_FOUND
    //
    // MessageText:
    //
    // A file was found, but it may not be the correct file.
    //
    int ERROR_POTENTIAL_FILE_FOUND = 1180;

    //
    // MessageId: ERROR_JOURNAL_ENTRY_DELETED
    //
    // MessageText:
    //
    // The journal entry has been deleted from the journal.
    //
    int ERROR_JOURNAL_ENTRY_DELETED = 1181;

    //
    // MessageId: ERROR_SHUTDOWN_IS_SCHEDULED
    //
    // MessageText:
    //
    // A system shutdown has already been scheduled.
    //
    int ERROR_SHUTDOWN_IS_SCHEDULED = 1190;

    //
    // MessageId: ERROR_SHUTDOWN_USERS_LOGGED_ON
    //
    // MessageText:
    //
    // The system shutdown cannot be initiated because there are other users
    // logged on to the computer.
    //
    int ERROR_SHUTDOWN_USERS_LOGGED_ON = 1191;

    //
    // MessageId: ERROR_BAD_DEVICE
    //
    // MessageText:
    //
    // The specified device name is invalid.
    //
    int ERROR_BAD_DEVICE = 1200;

    //
    // MessageId: ERROR_CONNECTION_UNAVAIL
    //
    // MessageText:
    //
    // The device is not currently connected but it is a remembered connection.
    //
    int ERROR_CONNECTION_UNAVAIL = 1201;

    //
    // MessageId: ERROR_DEVICE_ALREADY_REMEMBERED
    //
    // MessageText:
    //
    // The local device name has a remembered connection to another network
    // resource.
    //
    int ERROR_DEVICE_ALREADY_REMEMBERED = 1202;

    //
    // MessageId: ERROR_NO_NET_OR_BAD_PATH
    //
    // MessageText:
    //
    // The network path was either typed incorrectly, does not exist, or the
    // network provider is not currently available. Please try retyping the path
    // or contact your network administrator.
    //
    int ERROR_NO_NET_OR_BAD_PATH = 1203;

    //
    // MessageId: ERROR_BAD_PROVIDER
    //
    // MessageText:
    //
    // The specified network provider name is invalid.
    //
    int ERROR_BAD_PROVIDER = 1204;

    //
    // MessageId: ERROR_CANNOT_OPEN_PROFILE
    //
    // MessageText:
    //
    // Unable to open the network connection profile.
    //
    int ERROR_CANNOT_OPEN_PROFILE = 1205;

    //
    // MessageId: ERROR_BAD_PROFILE
    //
    // MessageText:
    //
    // The network connection profile is corrupted.
    //
    int ERROR_BAD_PROFILE = 1206;

    //
    // MessageId: ERROR_NOT_CONTAINER
    //
    // MessageText:
    //
    // Cannot enumerate a noncontainer.
    //
    int ERROR_NOT_CONTAINER = 1207;

    //
    // MessageId: ERROR_EXTENDED_ERROR
    //
    // MessageText:
    //
    // An extended error has occurred.
    //
    int ERROR_EXTENDED_ERROR = 1208;

    //
    // MessageId: ERROR_INVALID_GROUPNAME
    //
    // MessageText:
    //
    // The format of the specified group name is invalid.
    //
    int ERROR_INVALID_GROUPNAME = 1209;

    //
    // MessageId: ERROR_INVALID_COMPUTERNAME
    //
    // MessageText:
    //
    // The format of the specified computer name is invalid.
    //
    int ERROR_INVALID_COMPUTERNAME = 1210;

    //
    // MessageId: ERROR_INVALID_EVENTNAME
    //
    // MessageText:
    //
    // The format of the specified event name is invalid.
    //
    int ERROR_INVALID_EVENTNAME = 1211;

    //
    // MessageId: ERROR_INVALID_DOMAINNAME
    //
    // MessageText:
    //
    // The format of the specified domain name is invalid.
    //
    int ERROR_INVALID_DOMAINNAME = 1212;

    //
    // MessageId: ERROR_INVALID_SERVICENAME
    //
    // MessageText:
    //
    // The format of the specified service name is invalid.
    //
    int ERROR_INVALID_SERVICENAME = 1213;

    //
    // MessageId: ERROR_INVALID_NETNAME
    //
    // MessageText:
    //
    // The format of the specified network name is invalid.
    //
    int ERROR_INVALID_NETNAME = 1214;

    //
    // MessageId: ERROR_INVALID_SHARENAME
    //
    // MessageText:
    //
    // The format of the specified share name is invalid.
    //
    int ERROR_INVALID_SHARENAME = 1215;

    //
    // MessageId: ERROR_INVALID_PASSWORDNAME
    //
    // MessageText:
    //
    // The format of the specified password is invalid.
    //
    int ERROR_INVALID_PASSWORDNAME = 1216;

    //
    // MessageId: ERROR_INVALID_MESSAGENAME
    //
    // MessageText:
    //
    // The format of the specified message name is invalid.
    //
    int ERROR_INVALID_MESSAGENAME = 1217;

    //
    // MessageId: ERROR_INVALID_MESSAGEDEST
    //
    // MessageText:
    //
    // The format of the specified message destination is invalid.
    //
    int ERROR_INVALID_MESSAGEDEST = 1218;

    //
    // MessageId: ERROR_SESSION_CREDENTIAL_CONFLICT
    //
    // MessageText:
    //
    // Multiple connections to a server or shared resource by the same user,
    // using more than one user name, are not allowed. Disconnect all previous
    // connections to the server or shared resource and try again.
    //
    int ERROR_SESSION_CREDENTIAL_CONFLICT = 1219;

    //
    // MessageId: ERROR_REMOTE_SESSION_LIMIT_EXCEEDED
    //
    // MessageText:
    //
    // An attempt was made to establish a session to a network server, but there
    // are already too many sessions established to that server.
    //
    int ERROR_REMOTE_SESSION_LIMIT_EXCEEDED = 1220;

    //
    // MessageId: ERROR_DUP_DOMAINNAME
    //
    // MessageText:
    //
    // The workgroup or domain name is already in use by another computer on the
    // network.
    //
    int ERROR_DUP_DOMAINNAME = 1221;

    //
    // MessageId: ERROR_NO_NETWORK
    //
    // MessageText:
    //
    // The network is not present or not started.
    //
    int ERROR_NO_NETWORK = 1222;

    //
    // MessageId: ERROR_CANCELLED
    //
    // MessageText:
    //
    // The operation was canceled by the user.
    //
    int ERROR_CANCELLED = 1223;

    //
    // MessageId: ERROR_USER_MAPPED_FILE
    //
    // MessageText:
    //
    // The requested operation cannot be performed on a file with a user-mapped
    // section open.
    //
    int ERROR_USER_MAPPED_FILE = 1224;

    //
    // MessageId: ERROR_CONNECTION_REFUSED
    //
    // MessageText:
    //
    // The remote computer refused the network connection.
    //
    int ERROR_CONNECTION_REFUSED = 1225;

    //
    // MessageId: ERROR_GRACEFUL_DISCONNECT
    //
    // MessageText:
    //
    // The network connection was gracefully closed.
    //
    int ERROR_GRACEFUL_DISCONNECT = 1226;

    //
    // MessageId: ERROR_ADDRESS_ALREADY_ASSOCIATED
    //
    // MessageText:
    //
    // The network transport endpoint already has an address associated with it.
    //
    int ERROR_ADDRESS_ALREADY_ASSOCIATED = 1227;

    //
    // MessageId: ERROR_ADDRESS_NOT_ASSOCIATED
    //
    // MessageText:
    //
    // An address has not yet been associated with the network endpoint.
    //
    int ERROR_ADDRESS_NOT_ASSOCIATED = 1228;

    //
    // MessageId: ERROR_CONNECTION_INVALID
    //
    // MessageText:
    //
    // An operation was attempted on a nonexistent network connection.
    //
    int ERROR_CONNECTION_INVALID = 1229;

    //
    // MessageId: ERROR_CONNECTION_ACTIVE
    //
    // MessageText:
    //
    // An invalid operation was attempted on an active network connection.
    //
    int ERROR_CONNECTION_ACTIVE = 1230;

    //
    // MessageId: ERROR_NETWORK_UNREACHABLE
    //
    // MessageText:
    //
    // The network location cannot be reached. For information about network
    // troubleshooting, see Windows Help.
    //
    int ERROR_NETWORK_UNREACHABLE = 1231;

    //
    // MessageId: ERROR_HOST_UNREACHABLE
    //
    // MessageText:
    //
    // The network location cannot be reached. For information about network
    // troubleshooting, see Windows Help.
    //
    int ERROR_HOST_UNREACHABLE = 1232;

    //
    // MessageId: ERROR_PROTOCOL_UNREACHABLE
    //
    // MessageText:
    //
    // The network location cannot be reached. For information about network
    // troubleshooting, see Windows Help.
    //
    int ERROR_PROTOCOL_UNREACHABLE = 1233;

    //
    // MessageId: ERROR_PORT_UNREACHABLE
    //
    // MessageText:
    //
    // No service is operating at the destination network endpoint on the remote
    // system.
    //
    int ERROR_PORT_UNREACHABLE = 1234;

    //
    // MessageId: ERROR_REQUEST_ABORTED
    //
    // MessageText:
    //
    // The request was aborted.
    //
    int ERROR_REQUEST_ABORTED = 1235;

    //
    // MessageId: ERROR_CONNECTION_ABORTED
    //
    // MessageText:
    //
    // The network connection was aborted by the local system.
    //
    int ERROR_CONNECTION_ABORTED = 1236;

    //
    // MessageId: ERROR_RETRY
    //
    // MessageText:
    //
    // The operation could not be completed. A retry should be performed.
    //
    int ERROR_RETRY = 1237;

    //
    // MessageId: ERROR_CONNECTION_COUNT_LIMIT
    //
    // MessageText:
    //
    // A connection to the server could not be made because the limit on the
    // number of concurrent connections for this account has been reached.
    //
    int ERROR_CONNECTION_COUNT_LIMIT = 1238;

    //
    // MessageId: ERROR_LOGIN_TIME_RESTRICTION
    //
    // MessageText:
    //
    // Attempting to log in during an unauthorized time of day for this account.
    //
    int ERROR_LOGIN_TIME_RESTRICTION = 1239;

    //
    // MessageId: ERROR_LOGIN_WKSTA_RESTRICTION
    //
    // MessageText:
    //
    // The account is not authorized to log in from this station.
    //
    int ERROR_LOGIN_WKSTA_RESTRICTION = 1240;

    //
    // MessageId: ERROR_INCORRECT_ADDRESS
    //
    // MessageText:
    //
    // The network address could not be used for the operation requested.
    //
    int ERROR_INCORRECT_ADDRESS = 1241;

    //
    // MessageId: ERROR_ALREADY_REGISTERED
    //
    // MessageText:
    //
    // The service is already registered.
    //
    int ERROR_ALREADY_REGISTERED = 1242;

    //
    // MessageId: ERROR_SERVICE_NOT_FOUND
    //
    // MessageText:
    //
    // The specified service does not exist.
    //
    int ERROR_SERVICE_NOT_FOUND = 1243;

    //
    // MessageId: ERROR_NOT_AUTHENTICATED
    //
    // MessageText:
    //
    // The operation being requested was not performed because the user has not
    // been authenticated.
    //
    int ERROR_NOT_AUTHENTICATED = 1244;

    //
    // MessageId: ERROR_NOT_LOGGED_ON
    //
    // MessageText:
    //
    // The operation being requested was not performed because the user has not
    // logged on to the network.
    // The specified service does not exist.
    //
    int ERROR_NOT_LOGGED_ON = 1245;

    //
    // MessageId: ERROR_CONTINUE
    //
    // MessageText:
    //
    // Continue with work in progress.
    //
    int ERROR_CONTINUE = 1246; // dderror

    //
    // MessageId: ERROR_ALREADY_INITIALIZED
    //
    // MessageText:
    //
    // An attempt was made to perform an initialization operation when
    // initialization has already been completed.
    //
    int ERROR_ALREADY_INITIALIZED = 1247;

    //
    // MessageId: ERROR_NO_MORE_DEVICES
    //
    // MessageText:
    //
    // No more local devices.
    //
    int ERROR_NO_MORE_DEVICES = 1248; // dderror

    //
    // MessageId: ERROR_NO_SUCH_SITE
    //
    // MessageText:
    //
    // The specified site does not exist.
    //
    int ERROR_NO_SUCH_SITE = 1249;

    //
    // MessageId: ERROR_DOMAIN_CONTROLLER_EXISTS
    //
    // MessageText:
    //
    // A domain controller with the specified name already exists.
    //
    int ERROR_DOMAIN_CONTROLLER_EXISTS = 1250;

    //
    // MessageId: ERROR_ONLY_IF_CONNECTED
    //
    // MessageText:
    //
    // This operation is supported only when you are connected to the server.
    //
    int ERROR_ONLY_IF_CONNECTED = 1251;

    //
    // MessageId: ERROR_OVERRIDE_NOCHANGES
    //
    // MessageText:
    //
    // The group policy framework should call the extension even if there are no
    // changes.
    //
    int ERROR_OVERRIDE_NOCHANGES = 1252;

    //
    // MessageId: ERROR_BAD_USER_PROFILE
    //
    // MessageText:
    //
    // The specified user does not have a valid profile.
    //
    int ERROR_BAD_USER_PROFILE = 1253;

    //
    // MessageId: ERROR_NOT_SUPPORTED_ON_SBS
    //
    // MessageText:
    //
    // This operation is not supported on a computer running Windows Server=2003
    // for Small Business Server
    //
    int ERROR_NOT_SUPPORTED_ON_SBS = 1254;

    //
    // MessageId: ERROR_SERVER_SHUTDOWN_IN_PROGRESS
    //
    // MessageText:
    //
    // The server machine is shutting down.
    //
    int ERROR_SERVER_SHUTDOWN_IN_PROGRESS = 1255;

    //
    // MessageId: ERROR_HOST_DOWN
    //
    // MessageText:
    //
    // The remote system is not available. For information about network
    // troubleshooting, see Windows Help.
    //
    int ERROR_HOST_DOWN = 1256;

    //
    // MessageId: ERROR_NON_ACCOUNT_SID
    //
    // MessageText:
    //
    // The security identifier provided is not from an account domain.
    //
    int ERROR_NON_ACCOUNT_SID = 1257;

    //
    // MessageId: ERROR_NON_DOMAIN_SID
    //
    // MessageText:
    //
    // The security identifier provided does not have a domain component.
    //
    int ERROR_NON_DOMAIN_SID = 1258;

    //
    // MessageId: ERROR_APPHELP_BLOCK
    //
    // MessageText:
    //
    // AppHelp dialog canceled thus preventing the application from starting.
    //
    int ERROR_APPHELP_BLOCK = 1259;

    //
    // MessageId: ERROR_ACCESS_DISABLED_BY_POLICY
    //
    // MessageText:
    //
    // This program is blocked by group policy. For more information, contact
    // your system administrator.
    //
    int ERROR_ACCESS_DISABLED_BY_POLICY = 1260;

    //
    // MessageId: ERROR_REG_NAT_CONSUMPTION
    //
    // MessageText:
    //
    // A program attempt to use an invalid register value. Normally caused by an
    // uninitialized register. This error is Itanium specific.
    //
    int ERROR_REG_NAT_CONSUMPTION = 1261;

    //
    // MessageId: ERROR_CSCSHARE_OFFLINE
    //
    // MessageText:
    //
    // The share is currently offline or does not exist.
    //
    int ERROR_CSCSHARE_OFFLINE = 1262;

    //
    // MessageId: ERROR_PKINIT_FAILURE
    //
    // MessageText:
    //
    // The kerberos protocol encountered an error while validating the KDC
    // certificate during smartcard logon. There is more information in the
    // system event log.
    //
    int ERROR_PKINIT_FAILURE = 1263;

    //
    // MessageId: ERROR_SMARTCARD_SUBSYSTEM_FAILURE
    //
    // MessageText:
    //
    // The kerberos protocol encountered an error while attempting to utilize
    // the smartcard subsystem.
    //
    int ERROR_SMARTCARD_SUBSYSTEM_FAILURE = 1264;

    //
    // MessageId: ERROR_DOWNGRADE_DETECTED
    //
    // MessageText:
    //
    // The system detected a possible attempt to compromise security. Please
    // ensure that you can contact the server that authenticated you.
    //
    int ERROR_DOWNGRADE_DETECTED = 1265;

    //
    // Do not use ID's=1266 -=1270 as the symbolicNames have been moved to
    // SEC_E_*
    //
    //
    // MessageId: ERROR_MACHINE_LOCKED
    //
    // MessageText:
    //
    // The machine is locked and cannot be shut down without the force option.
    //
    int ERROR_MACHINE_LOCKED = 1271;

    //
    // MessageId: ERROR_CALLBACK_SUPPLIED_INVALID_DATA
    //
    // MessageText:
    //
    // An application-defined callback gave invalid data when called.
    //
    int ERROR_CALLBACK_SUPPLIED_INVALID_DATA = 1273;

    //
    // MessageId: ERROR_SYNC_FOREGROUND_REFRESH_REQUIRED
    //
    // MessageText:
    //
    // The group policy framework should call the extension in the synchronous
    // foreground policy refresh.
    //
    int ERROR_SYNC_FOREGROUND_REFRESH_REQUIRED = 1274;

    //
    // MessageId: ERROR_DRIVER_BLOCKED
    //
    // MessageText:
    //
    // This driver has been blocked from loading
    //
    int ERROR_DRIVER_BLOCKED = 1275;

    //
    // MessageId: ERROR_INVALID_IMPORT_OF_NON_DLL
    //
    // MessageText:
    //
    // A dynamic link library (DL; referenced a module that was neither a DLL
    // nor the process's executable image.
    //
    int ERROR_INVALID_IMPORT_OF_NON_DLL = 1276;

    //
    // MessageId: ERROR_ACCESS_DISABLED_WEBBLADE
    //
    // MessageText:
    //
    // Windows cannot open this program since it has been disabled.
    //
    int ERROR_ACCESS_DISABLED_WEBBLADE = 1277;

    //
    // MessageId: ERROR_ACCESS_DISABLED_WEBBLADE_TAMPER
    //
    // MessageText:
    //
    // Windows cannot open this program because the license enforcement system
    // has been tampered with or become corrupted.
    //
    int ERROR_ACCESS_DISABLED_WEBBLADE_TAMPER = 1278;

    //
    // MessageId: ERROR_RECOVERY_FAILURE
    //
    // MessageText:
    //
    // A transaction recover failed.
    //
    int ERROR_RECOVERY_FAILURE = 1279;

    //
    // MessageId: ERROR_ALREADY_FIBER
    //
    // MessageText:
    //
    // The current thread has already been converted to a fiber.
    //
    int ERROR_ALREADY_FIBER = 1280;

    //
    // MessageId: ERROR_ALREADY_THREAD
    //
    // MessageText:
    //
    // The current thread has already been converted from a fiber.
    //
    int ERROR_ALREADY_THREAD = 1281;

    //
    // MessageId: ERROR_STACK_BUFFER_OVERRUN
    //
    // MessageText:
    //
    // The system detected an overrun of a stack-based buffer in this
    // application. This overrun could potentially allow a malicious user to
    // gain control of this application.
    //
    int ERROR_STACK_BUFFER_OVERRUN = 1282;

    //
    // MessageId: ERROR_PARAMETER_QUOTA_EXCEEDED
    //
    // MessageText:
    //
    // Data present in one of the parameters is more than the function can
    // operate on.
    //
    int ERROR_PARAMETER_QUOTA_EXCEEDED = 1283;

    //
    // MessageId: ERROR_DEBUGGER_INACTIVE
    //
    // MessageText:
    //
    // An attempt to do an operation on a debug object failed because the object
    // is in the process of being deleted.
    //
    int ERROR_DEBUGGER_INACTIVE = 1284;

    //
    // MessageId: ERROR_DELAY_LOAD_FAILED
    //
    // MessageText:
    //
    // An attempt to delay-load a .dll or get a function address in a
    // delay-loaded .dll failed.
    //
    int ERROR_DELAY_LOAD_FAILED = 1285;

    //
    // MessageId: ERROR_VDM_DISALLOWED
    //
    // MessageText:
    //
    // %1 is a=16-bit application. You do not have permissions to execute=16-bit
    // applications. Check your permissions with your system administrator.
    //
    int ERROR_VDM_DISALLOWED = 1286;

    //
    // MessageId: ERROR_UNIDENTIFIED_ERROR
    //
    // MessageText:
    //
    // Insufficient information exists to identify the cause of failure.
    //
    int ERROR_UNIDENTIFIED_ERROR = 1287;

    //
    // MessageId: ERROR_INVALID_CRUNTIME_PARAMETER
    //
    // MessageText:
    //
    // The parameter passed to a C runtime function is incorrect.
    //
    int ERROR_INVALID_CRUNTIME_PARAMETER = 1288;

    //
    // MessageId: ERROR_BEYOND_VDL
    //
    // MessageText:
    //
    // The operation occurred beyond the valid data length of the file.
    //
    int ERROR_BEYOND_VDL = 1289;

    //
    // MessageId: ERROR_INCOMPATIBLE_SERVICE_SID_TYPE
    //
    // MessageText:
    //
    // The service start failed since one or more services in the same process
    // have an incompatible service SID type setting. A service with restricted
    // service SID type can only coexist in the same process with other services
    // with a restricted SID type. If the service SID type for this service was
    // just configured, the hosting process must be restarted in order to start
    // this service.
    //
    int ERROR_INCOMPATIBLE_SERVICE_SID_TYPE = 1290;

    //
    // MessageId: ERROR_DRIVER_PROCESS_TERMINATED
    //
    // MessageText:
    //
    // The process hosting the driver for this device has been terminated.
    //
    int ERROR_DRIVER_PROCESS_TERMINATED = 1291;

    //
    // MessageId: ERROR_IMPLEMENTATION_LIMIT
    //
    // MessageText:
    //
    // An operation attempted to exceed an implementation-defined limit.
    //
    int ERROR_IMPLEMENTATION_LIMIT = 1292;

    //
    // MessageId: ERROR_PROCESS_IS_PROTECTED
    //
    // MessageText:
    //
    // Either the target process, or the target thread's containing process, is
    // a protected process.
    //
    int ERROR_PROCESS_IS_PROTECTED = 1293;

    //
    // MessageId: ERROR_SERVICE_NOTIFY_CLIENT_LAGGING
    //
    // MessageText:
    //
    // The service notification client is lagging too far behind the current
    // state of services in the machine.
    //
    int ERROR_SERVICE_NOTIFY_CLIENT_LAGGING = 1294;

    //
    // MessageId: ERROR_DISK_QUOTA_EXCEEDED
    //
    // MessageText:
    //
    // The requested file operation failed because the storage quota was
    // exceeded.
    // To free up disk space, move files to a different location or delete
    // unnecessary files. For more information, contact your system
    // administrator.
    //
    int ERROR_DISK_QUOTA_EXCEEDED = 1295;

    //
    // MessageId: ERROR_CONTENT_BLOCKED
    //
    // MessageText:
    //
    // The requested file operation failed because the storage policy blocks
    // that type of file. For more information, contact your system
    // administrator.
    //
    int ERROR_CONTENT_BLOCKED = 1296;

    //
    // MessageId: ERROR_INCOMPATIBLE_SERVICE_PRIVILEGE
    //
    // MessageText:
    //
    // A privilege that the service requires to function properly does not exist
    // in the service account configuration.
    // You may use the Services Microsoft Management Console (MMC) snap-in
    // (services.msc) and the Local Security Settings MMC snap-in (secpol.msc)
    // to view the service configuration and the account configuration.
    //
    int ERROR_INCOMPATIBLE_SERVICE_PRIVILEGE = 1297;

    // /////////////////////////////////////////////////
    // =//
    // =SECURITY Error codes=//
    // =//
    // =1299 to=1399=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_INVALID_LABEL
    //
    // MessageText:
    //
    // Indicates a particular Security ID may not be assigned as the label of an
    // object.
    //
    int ERROR_INVALID_LABEL = 1299;

    //
    // MessageId: ERROR_NOT_ALL_ASSIGNED
    //
    // MessageText:
    //
    // Not all privileges or groups referenced are assigned to the caller.
    //
    int ERROR_NOT_ALL_ASSIGNED = 1300;

    //
    // MessageId: ERROR_SOME_NOT_MAPPED
    //
    // MessageText:
    //
    // Some mapping between account names and security IDs was not done.
    //
    int ERROR_SOME_NOT_MAPPED = 1301;

    //
    // MessageId: ERROR_NO_QUOTAS_FOR_ACCOUNT
    //
    // MessageText:
    //
    // No system quota limits are specifically set for this account.
    //
    int ERROR_NO_QUOTAS_FOR_ACCOUNT = 1302;

    //
    // MessageId: ERROR_LOCAL_USER_SESSION_KEY
    //
    // MessageText:
    //
    // No encryption key is available. A well-known encryption key was returned.
    //
    int ERROR_LOCAL_USER_SESSION_KEY = 1303;

    //
    // MessageId: ERROR_NULL_LM_PASSWORD
    //
    // MessageText:
    //
    // The password is too complex to be converted to a LAN Manager password.
    // The LAN Manager password returned is a NULL string.
    //
    int ERROR_NULL_LM_PASSWORD = 1304;

    //
    // MessageId: ERROR_UNKNOWN_REVISION
    //
    // MessageText:
    //
    // The revision level is unknown.
    //
    int ERROR_UNKNOWN_REVISION = 1305;

    //
    // MessageId: ERROR_REVISION_MISMATCH
    //
    // MessageText:
    //
    // Indicates two revision levels are incompatible.
    //
    int ERROR_REVISION_MISMATCH = 1306;

    //
    // MessageId: ERROR_INVALID_OWNER
    //
    // MessageText:
    //
    // This security ID may not be assigned as the owner of this object.
    //
    int ERROR_INVALID_OWNER = 1307;

    //
    // MessageId: ERROR_INVALID_PRIMARY_GROUP
    //
    // MessageText:
    //
    // This security ID may not be assigned as the primary group of an object.
    //
    int ERROR_INVALID_PRIMARY_GROUP = 1308;

    //
    // MessageId: ERROR_NO_IMPERSONATION_TOKEN
    //
    // MessageText:
    //
    // An attempt has been made to operate on an impersonation token by a thread
    // that is not currently impersonating a client.
    //
    int ERROR_NO_IMPERSONATION_TOKEN = 1309;

    //
    // MessageId: ERROR_CANT_DISABLE_MANDATORY
    //
    // MessageText:
    //
    // The group may not be disabled.
    //
    int ERROR_CANT_DISABLE_MANDATORY = 1310;

    //
    // MessageId: ERROR_NO_LOGON_SERVERS
    //
    // MessageText:
    //
    // There are currently no logon servers available to service the logon
    // request.
    //
    int ERROR_NO_LOGON_SERVERS = 1311;

    //
    // MessageId: ERROR_NO_SUCH_LOGON_SESSION
    //
    // MessageText:
    //
    // A specified logon session does not exist. It may already have been
    // terminated.
    //
    int ERROR_NO_SUCH_LOGON_SESSION = 1312;

    //
    // MessageId: ERROR_NO_SUCH_PRIVILEGE
    //
    // MessageText:
    //
    // A specified privilege does not exist.
    //
    int ERROR_NO_SUCH_PRIVILEGE = 1313;

    //
    // MessageId: ERROR_PRIVILEGE_NOT_HELD
    //
    // MessageText:
    //
    // A required privilege is not held by the client.
    //
    int ERROR_PRIVILEGE_NOT_HELD = 1314;

    //
    // MessageId: ERROR_INVALID_ACCOUNT_NAME
    //
    // MessageText:
    //
    // The name provided is not a properly formed account name.
    //
    int ERROR_INVALID_ACCOUNT_NAME = 1315;

    //
    // MessageId: ERROR_USER_EXISTS
    //
    // MessageText:
    //
    // The specified account already exists.
    //
    int ERROR_USER_EXISTS = 1316;

    //
    // MessageId: ERROR_NO_SUCH_USER
    //
    // MessageText:
    //
    // The specified account does not exist.
    //
    int ERROR_NO_SUCH_USER = 1317;

    //
    // MessageId: ERROR_GROUP_EXISTS
    //
    // MessageText:
    //
    // The specified group already exists.
    //
    int ERROR_GROUP_EXISTS = 1318;

    //
    // MessageId: ERROR_NO_SUCH_GROUP
    //
    // MessageText:
    //
    // The specified group does not exist.
    //
    int ERROR_NO_SUCH_GROUP = 1319;

    //
    // MessageId: ERROR_MEMBER_IN_GROUP
    //
    // MessageText:
    //
    // Either the specified user account is already a member of the specified
    // group, or the specified group cannot be deleted because it contains a
    // member.
    //
    int ERROR_MEMBER_IN_GROUP = 1320;

    //
    // MessageId: ERROR_MEMBER_NOT_IN_GROUP
    //
    // MessageText:
    //
    // The specified user account is not a member of the specified group
    // account.
    //
    int ERROR_MEMBER_NOT_IN_GROUP = 1321;

    //
    // MessageId: ERROR_LAST_ADMIN
    //
    // MessageText:
    //
    // The last remaining administration account cannot be disabled or deleted.
    //
    int ERROR_LAST_ADMIN = 1322;

    //
    // MessageId: ERROR_WRONG_PASSWORD
    //
    // MessageText:
    //
    // Unable to update the password. The value provided as the current password
    // is incorrect.
    //
    int ERROR_WRONG_PASSWORD = 1323;

    //
    // MessageId: ERROR_ILL_FORMED_PASSWORD
    //
    // MessageText:
    //
    // Unable to update the password. The value provided for the new password
    // contains values that are not allowed in passwords.
    //
    int ERROR_ILL_FORMED_PASSWORD = 1324;

    //
    // MessageId: ERROR_PASSWORD_RESTRICTION
    //
    // MessageText:
    //
    // Unable to update the password. The value provided for the new password
    // does not meet the length, complexity, or history requirements of the
    // domain.
    //
    int ERROR_PASSWORD_RESTRICTION = 1325;

    //
    // MessageId: ERROR_LOGON_FAILURE
    //
    // MessageText:
    //
    // Logon failure: unknown user name or bad password.
    //
    int ERROR_LOGON_FAILURE = 1326;

    //
    // MessageId: ERROR_ACCOUNT_RESTRICTION
    //
    // MessageText:
    //
    // Logon failure: user account restriction. Possible reasons are blank
    // passwords not allowed, logon hour restrictions, or a policy restriction
    // has been enforced.
    //
    int ERROR_ACCOUNT_RESTRICTION = 1327;

    //
    // MessageId: ERROR_INVALID_LOGON_HOURS
    //
    // MessageText:
    //
    // Logon failure: account logon time restriction violation.
    //
    int ERROR_INVALID_LOGON_HOURS = 1328;

    //
    // MessageId: ERROR_INVALID_WORKSTATION
    //
    // MessageText:
    //
    // Logon failure: user not allowed to log on to this computer.
    //
    int ERROR_INVALID_WORKSTATION = 1329;

    //
    // MessageId: ERROR_PASSWORD_EXPIRED
    //
    // MessageText:
    //
    // Logon failure: the specified account password has expired.
    //
    int ERROR_PASSWORD_EXPIRED = 1330;

    //
    // MessageId: ERROR_ACCOUNT_DISABLED
    //
    // MessageText:
    //
    // Logon failure: account currently disabled.
    //
    int ERROR_ACCOUNT_DISABLED = 1331;

    //
    // MessageId: ERROR_NONE_MAPPED
    //
    // MessageText:
    //
    // No mapping between account names and security IDs was done.
    //
    int ERROR_NONE_MAPPED = 1332;

    //
    // MessageId: ERROR_TOO_MANY_LUIDS_REQUESTED
    //
    // MessageText:
    //
    // Too many local user identifiers (LUIDs) were requested at one time.
    //
    int ERROR_TOO_MANY_LUIDS_REQUESTED = 1333;

    //
    // MessageId: ERROR_LUIDS_EXHAUSTED
    //
    // MessageText:
    //
    // No more local user identifiers (LUIDs) are available.
    //
    int ERROR_LUIDS_EXHAUSTED = 1334;

    //
    // MessageId: ERROR_INVALID_SUB_AUTHORITY
    //
    // MessageText:
    //
    // The subauthority part of a security ID is invalid for this particular
    // use.
    //
    int ERROR_INVALID_SUB_AUTHORITY = 1335;

    //
    // MessageId: ERROR_INVALID_ACL
    //
    // MessageText:
    //
    // The access control list (AC; structure is invalid.
    //
    int ERROR_INVALID_ACL = 1336;

    //
    // MessageId: ERROR_INVALID_SID
    //
    // MessageText:
    //
    // The security ID structure is invalid.
    //
    int ERROR_INVALID_SID = 1337;

    //
    // MessageId: ERROR_INVALID_SECURITY_DESCR
    //
    // MessageText:
    //
    // The security descriptor structure is invalid.
    //
    int ERROR_INVALID_SECURITY_DESCR = 1338;

    //
    // MessageId: ERROR_BAD_INHERITANCE_ACL
    //
    // MessageText:
    //
    // The inherited access control list (AC; or access control entry (ACE)
    // could not be built.
    //
    int ERROR_BAD_INHERITANCE_ACL = 1340;

    //
    // MessageId: ERROR_SERVER_DISABLED
    //
    // MessageText:
    //
    // The server is currently disabled.
    //
    int ERROR_SERVER_DISABLED = 1341;

    //
    // MessageId: ERROR_SERVER_NOT_DISABLED
    //
    // MessageText:
    //
    // The server is currently enabled.
    //
    int ERROR_SERVER_NOT_DISABLED = 1342;

    //
    // MessageId: ERROR_INVALID_ID_AUTHORITY
    //
    // MessageText:
    //
    // The value provided was an invalid value for an identifier authority.
    //
    int ERROR_INVALID_ID_AUTHORITY = 1343;

    //
    // MessageId: ERROR_ALLOTTED_SPACE_EXCEEDED
    //
    // MessageText:
    //
    // No more memory is available for security information updates.
    //
    int ERROR_ALLOTTED_SPACE_EXCEEDED = 1344;

    //
    // MessageId: ERROR_INVALID_GROUP_ATTRIBUTES
    //
    // MessageText:
    //
    // The specified attributes are invalid, or incompatible with the attributes
    // for the group as a whole.
    //
    int ERROR_INVALID_GROUP_ATTRIBUTES = 1345;

    //
    // MessageId: ERROR_BAD_IMPERSONATION_LEVEL
    //
    // MessageText:
    //
    // Either a required impersonation level was not provided, or the provided
    // impersonation level is invalid.
    //
    int ERROR_BAD_IMPERSONATION_LEVEL = 1346;

    //
    // MessageId: ERROR_CANT_OPEN_ANONYMOUS
    //
    // MessageText:
    //
    // Cannot open an anonymous level security token.
    //
    int ERROR_CANT_OPEN_ANONYMOUS = 1347;

    //
    // MessageId: ERROR_BAD_VALIDATION_CLASS
    //
    // MessageText:
    //
    // The validation information class requested was invalid.
    //
    int ERROR_BAD_VALIDATION_CLASS = 1348;

    //
    // MessageId: ERROR_BAD_TOKEN_TYPE
    //
    // MessageText:
    //
    // The type of the token is inappropriate for its attempted use.
    //
    int ERROR_BAD_TOKEN_TYPE = 1349;

    //
    // MessageId: ERROR_NO_SECURITY_ON_OBJECT
    //
    // MessageText:
    //
    // Unable to perform a security operation on an object that has no
    // associated security.
    //
    int ERROR_NO_SECURITY_ON_OBJECT = 1350;

    //
    // MessageId: ERROR_CANT_ACCESS_DOMAIN_INFO
    //
    // MessageText:
    //
    // Configuration information could not be read from the domain controller,
    // either because the machine is unavailable, or access has been denied.
    //
    int ERROR_CANT_ACCESS_DOMAIN_INFO = 1351;

    //
    // MessageId: ERROR_INVALID_SERVER_STATE
    //
    // MessageText:
    //
    // The security account manager (SAM) or local security authority (LSA)
    // server was in the wrong state to perform the security operation.
    //
    int ERROR_INVALID_SERVER_STATE = 1352;

    //
    // MessageId: ERROR_INVALID_DOMAIN_STATE
    //
    // MessageText:
    //
    // The domain was in the wrong state to perform the security operation.
    //
    int ERROR_INVALID_DOMAIN_STATE = 1353;

    //
    // MessageId: ERROR_INVALID_DOMAIN_ROLE
    //
    // MessageText:
    //
    // This operation is only allowed for the Primary Domain Controller of the
    // domain.
    //
    int ERROR_INVALID_DOMAIN_ROLE = 1354;

    //
    // MessageId: ERROR_NO_SUCH_DOMAIN
    //
    // MessageText:
    //
    // The specified domain either does not exist or could not be contacted.
    //
    int ERROR_NO_SUCH_DOMAIN = 1355;

    //
    // MessageId: ERROR_DOMAIN_EXISTS
    //
    // MessageText:
    //
    // The specified domain already exists.
    //
    int ERROR_DOMAIN_EXISTS = 1356;

    //
    // MessageId: ERROR_DOMAIN_LIMIT_EXCEEDED
    //
    // MessageText:
    //
    // An attempt was made to exceed the limit on the number of domains per
    // server.
    //
    int ERROR_DOMAIN_LIMIT_EXCEEDED = 1357;

    //
    // MessageId: ERROR_INTERNAL_DB_CORRUPTION
    //
    // MessageText:
    //
    // Unable to complete the requested operation because of either a
    // catastrophic media failure or a data structure corruption on the disk.
    //
    int ERROR_INTERNAL_DB_CORRUPTION = 1358;

    //
    // MessageId: ERROR_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal error occurred.
    //
    int ERROR_INTERNAL_ERROR = 1359;

    //
    // MessageId: ERROR_GENERIC_NOT_MAPPED
    //
    // MessageText:
    //
    // Generic access types were contained in an access mask which should
    // already be mapped to nongeneric types.
    //
    int ERROR_GENERIC_NOT_MAPPED = 1360;

    //
    // MessageId: ERROR_BAD_DESCRIPTOR_FORMAT
    //
    // MessageText:
    //
    // A security descriptor is not in the right format (absolute or
    // self-relative).
    //
    int ERROR_BAD_DESCRIPTOR_FORMAT = 1361;

    //
    // MessageId: ERROR_NOT_LOGON_PROCESS
    //
    // MessageText:
    //
    // The requested action is restricted for use by logon processes only. The
    // calling process has not registered as a logon process.
    //
    int ERROR_NOT_LOGON_PROCESS = 1362;

    //
    // MessageId: ERROR_LOGON_SESSION_EXISTS
    //
    // MessageText:
    //
    // Cannot start a new logon session with an ID that is already in use.
    //
    int ERROR_LOGON_SESSION_EXISTS = 1363;

    //
    // MessageId: ERROR_NO_SUCH_PACKAGE
    //
    // MessageText:
    //
    // A specified authentication package is unknown.
    //
    int ERROR_NO_SUCH_PACKAGE = 1364;

    //
    // MessageId: ERROR_BAD_LOGON_SESSION_STATE
    //
    // MessageText:
    //
    // The logon session is not in a state that is consistent with the requested
    // operation.
    //
    int ERROR_BAD_LOGON_SESSION_STATE = 1365;

    //
    // MessageId: ERROR_LOGON_SESSION_COLLISION
    //
    // MessageText:
    //
    // The logon session ID is already in use.
    //
    int ERROR_LOGON_SESSION_COLLISION = 1366;

    //
    // MessageId: ERROR_INVALID_LOGON_TYPE
    //
    // MessageText:
    //
    // A logon request contained an invalid logon type value.
    //
    int ERROR_INVALID_LOGON_TYPE = 1367;

    //
    // MessageId: ERROR_CANNOT_IMPERSONATE
    //
    // MessageText:
    //
    // Unable to impersonate using a named pipe until data has been read from
    // that pipe.
    //
    int ERROR_CANNOT_IMPERSONATE = 1368;

    //
    // MessageId: ERROR_RXACT_INVALID_STATE
    //
    // MessageText:
    //
    // The transaction state of a registry subtree is incompatible with the
    // requested operation.
    //
    int ERROR_RXACT_INVALID_STATE = 1369;

    //
    // MessageId: ERROR_RXACT_COMMIT_FAILURE
    //
    // MessageText:
    //
    // An internal security database corruption has been encountered.
    //
    int ERROR_RXACT_COMMIT_FAILURE = 1370;

    //
    // MessageId: ERROR_SPECIAL_ACCOUNT
    //
    // MessageText:
    //
    // Cannot perform this operation on built-in accounts.
    //
    int ERROR_SPECIAL_ACCOUNT = 1371;

    //
    // MessageId: ERROR_SPECIAL_GROUP
    //
    // MessageText:
    //
    // Cannot perform this operation on this built-in special group.
    //
    int ERROR_SPECIAL_GROUP = 1372;

    //
    // MessageId: ERROR_SPECIAL_USER
    //
    // MessageText:
    //
    // Cannot perform this operation on this built-in special user.
    //
    int ERROR_SPECIAL_USER = 1373;

    //
    // MessageId: ERROR_MEMBERS_PRIMARY_GROUP
    //
    // MessageText:
    //
    // The user cannot be removed from a group because the group is currently
    // the user's primary group.
    //
    int ERROR_MEMBERS_PRIMARY_GROUP = 1374;

    //
    // MessageId: ERROR_TOKEN_ALREADY_IN_USE
    //
    // MessageText:
    //
    // The token is already in use as a primary token.
    //
    int ERROR_TOKEN_ALREADY_IN_USE = 1375;

    //
    // MessageId: ERROR_NO_SUCH_ALIAS
    //
    // MessageText:
    //
    // The specified local group does not exist.
    //
    int ERROR_NO_SUCH_ALIAS = 1376;

    //
    // MessageId: ERROR_MEMBER_NOT_IN_ALIAS
    //
    // MessageText:
    //
    // The specified account name is not a member of the group.
    //
    int ERROR_MEMBER_NOT_IN_ALIAS = 1377;

    //
    // MessageId: ERROR_MEMBER_IN_ALIAS
    //
    // MessageText:
    //
    // The specified account name is already a member of the group.
    //
    int ERROR_MEMBER_IN_ALIAS = 1378;

    //
    // MessageId: ERROR_ALIAS_EXISTS
    //
    // MessageText:
    //
    // The specified local group already exists.
    //
    int ERROR_ALIAS_EXISTS = 1379;

    //
    // MessageId: ERROR_LOGON_NOT_GRANTED
    //
    // MessageText:
    //
    // Logon failure: the user has not been granted the requested logon type at
    // this computer.
    //
    int ERROR_LOGON_NOT_GRANTED = 1380;

    //
    // MessageId: ERROR_TOO_MANY_SECRETS
    //
    // MessageText:
    //
    // The maximum number of secrets that may be stored in a single system has
    // been exceeded.
    //
    int ERROR_TOO_MANY_SECRETS = 1381;

    //
    // MessageId: ERROR_SECRET_TOO_LONG
    //
    // MessageText:
    //
    // The length of a secret exceeds the maximum length allowed.
    //
    int ERROR_SECRET_TOO_LONG = 1382;

    //
    // MessageId: ERROR_INTERNAL_DB_ERROR
    //
    // MessageText:
    //
    // The local security authority database contains an internal inconsistency.
    //
    int ERROR_INTERNAL_DB_ERROR = 1383;

    //
    // MessageId: ERROR_TOO_MANY_CONTEXT_IDS
    //
    // MessageText:
    //
    // During a logon attempt, the user's security context accumulated too many
    // security IDs.
    //
    int ERROR_TOO_MANY_CONTEXT_IDS = 1384;

    //
    // MessageId: ERROR_LOGON_TYPE_NOT_GRANTED
    //
    // MessageText:
    //
    // Logon failure: the user has not been granted the requested logon type at
    // this computer.
    //
    int ERROR_LOGON_TYPE_NOT_GRANTED = 1385;

    //
    // MessageId: ERROR_NT_CROSS_ENCRYPTION_REQUIRED
    //
    // MessageText:
    //
    // A cross-encrypted password is necessary to change a user password.
    //
    int ERROR_NT_CROSS_ENCRYPTION_REQUIRED = 1386;

    //
    // MessageId: ERROR_NO_SUCH_MEMBER
    //
    // MessageText:
    //
    // A member could not be added to or removed from the local group because
    // the member does not exist.
    //
    int ERROR_NO_SUCH_MEMBER = 1387;

    //
    // MessageId: ERROR_INVALID_MEMBER
    //
    // MessageText:
    //
    // A new member could not be added to a local group because the member has
    // the wrong account type.
    //
    int ERROR_INVALID_MEMBER = 1388;

    //
    // MessageId: ERROR_TOO_MANY_SIDS
    //
    // MessageText:
    //
    // Too many security IDs have been specified.
    //
    int ERROR_TOO_MANY_SIDS = 1389;

    //
    // MessageId: ERROR_LM_CROSS_ENCRYPTION_REQUIRED
    //
    // MessageText:
    //
    // A cross-encrypted password is necessary to change this user password.
    //
    int ERROR_LM_CROSS_ENCRYPTION_REQUIRED = 1390;

    //
    // MessageId: ERROR_NO_INHERITANCE
    //
    // MessageText:
    //
    // Indicates an ACL contains no inheritable components.
    //
    int ERROR_NO_INHERITANCE = 1391;

    //
    // MessageId: ERROR_FILE_CORRUPT
    //
    // MessageText:
    //
    // The file or directory is corrupted and unreadable.
    //
    int ERROR_FILE_CORRUPT = 1392;

    //
    // MessageId: ERROR_DISK_CORRUPT
    //
    // MessageText:
    //
    // The disk structure is corrupted and unreadable.
    //
    int ERROR_DISK_CORRUPT = 1393;

    //
    // MessageId: ERROR_NO_USER_SESSION_KEY
    //
    // MessageText:
    //
    // There is no user session key for the specified logon session.
    //
    int ERROR_NO_USER_SESSION_KEY = 1394;

    //
    // MessageId: ERROR_LICENSE_QUOTA_EXCEEDED
    //
    // MessageText:
    //
    // The service being accessed is licensed for a particular number of
    // connections.
    // No more connections can be made to the service at this time because there
    // are already as many connections as the service can accept.
    //
    int ERROR_LICENSE_QUOTA_EXCEEDED = 1395;

    //
    // MessageId: ERROR_WRONG_TARGET_NAME
    //
    // MessageText:
    //
    // Logon Failure: The target account name is incorrect.
    //
    int ERROR_WRONG_TARGET_NAME = 1396;

    //
    // MessageId: ERROR_MUTUAL_AUTH_FAILED
    //
    // MessageText:
    //
    // Mutual Authentication failed. The server's password is out of date at the
    // domain controller.
    //
    int ERROR_MUTUAL_AUTH_FAILED = 1397;

    //
    // MessageId: ERROR_TIME_SKEW
    //
    // MessageText:
    //
    // There is a time and/or date difference between the client and server.
    //
    int ERROR_TIME_SKEW = 1398;

    //
    // MessageId: ERROR_CURRENT_DOMAIN_NOT_ALLOWED
    //
    // MessageText:
    //
    // This operation cannot be performed on the current domain.
    //
    int ERROR_CURRENT_DOMAIN_NOT_ALLOWED = 1399;

    // /////////////////////////////////////////////////
    // =//
    // =WinUser Error codes=//
    // =//
    // =1400 to=1499=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_INVALID_WINDOW_HANDLE
    //
    // MessageText:
    //
    // Invalid window handle.
    //
    int ERROR_INVALID_WINDOW_HANDLE = 1400;

    //
    // MessageId: ERROR_INVALID_MENU_HANDLE
    //
    // MessageText:
    //
    // Invalid menu handle.
    //
    int ERROR_INVALID_MENU_HANDLE = 1401;

    //
    // MessageId: ERROR_INVALID_CURSOR_HANDLE
    //
    // MessageText:
    //
    // Invalid cursor handle.
    //
    int ERROR_INVALID_CURSOR_HANDLE = 1402;

    //
    // MessageId: ERROR_INVALID_ACCEL_HANDLE
    //
    // MessageText:
    //
    // Invalid accelerator table handle.
    //
    int ERROR_INVALID_ACCEL_HANDLE = 1403;

    //
    // MessageId: ERROR_INVALID_HOOK_HANDLE
    //
    // MessageText:
    //
    // Invalid hook handle.
    //
    int ERROR_INVALID_HOOK_HANDLE = 1404;

    //
    // MessageId: ERROR_INVALID_DWP_HANDLE
    //
    // MessageText:
    //
    // Invalid handle to a multiple-window position structure.
    //
    int ERROR_INVALID_DWP_HANDLE = 1405;

    //
    // MessageId: ERROR_TLW_WITH_WSCHILD
    //
    // MessageText:
    //
    // Cannot create a top-level child window.
    //
    int ERROR_TLW_WITH_WSCHILD = 1406;

    //
    // MessageId: ERROR_CANNOT_FIND_WND_CLASS
    //
    // MessageText:
    //
    // Cannot find window class.
    //
    int ERROR_CANNOT_FIND_WND_CLASS = 1407;

    //
    // MessageId: ERROR_WINDOW_OF_OTHER_THREAD
    //
    // MessageText:
    //
    // Invalid window; it belongs to other thread.
    //
    int ERROR_WINDOW_OF_OTHER_THREAD = 1408;

    //
    // MessageId: ERROR_HOTKEY_ALREADY_REGISTERED
    //
    // MessageText:
    //
    // Hot key is already registered.
    //
    int ERROR_HOTKEY_ALREADY_REGISTERED = 1409;

    //
    // MessageId: ERROR_CLASS_ALREADY_EXISTS
    //
    // MessageText:
    //
    // Class already exists.
    //
    int ERROR_CLASS_ALREADY_EXISTS = 1410;

    //
    // MessageId: ERROR_CLASS_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // Class does not exist.
    //
    int ERROR_CLASS_DOES_NOT_EXIST = 1411;

    //
    // MessageId: ERROR_CLASS_HAS_WINDOWS
    //
    // MessageText:
    //
    // Class still has open windows.
    //
    int ERROR_CLASS_HAS_WINDOWS = 1412;

    //
    // MessageId: ERROR_INVALID_INDEX
    //
    // MessageText:
    //
    // Invalid index.
    //
    int ERROR_INVALID_INDEX = 1413;

    //
    // MessageId: ERROR_INVALID_ICON_HANDLE
    //
    // MessageText:
    //
    // Invalid icon handle.
    //
    int ERROR_INVALID_ICON_HANDLE = 1414;

    //
    // MessageId: ERROR_PRIVATE_DIALOG_INDEX
    //
    // MessageText:
    //
    // Using private DIALOG window words.
    //
    int ERROR_PRIVATE_DIALOG_INDEX = 1415;

    //
    // MessageId: ERROR_LISTBOX_ID_NOT_FOUND
    //
    // MessageText:
    //
    // The list box identifier was not found.
    //
    int ERROR_LISTBOX_ID_NOT_FOUND = 1416;

    //
    // MessageId: ERROR_NO_WILDCARD_CHARACTERS
    //
    // MessageText:
    //
    // No wildcards were found.
    //
    int ERROR_NO_WILDCARD_CHARACTERS = 1417;

    //
    // MessageId: ERROR_CLIPBOARD_NOT_OPEN
    //
    // MessageText:
    //
    // Thread does not have a clipboard open.
    //
    int ERROR_CLIPBOARD_NOT_OPEN = 1418;

    //
    // MessageId: ERROR_HOTKEY_NOT_REGISTERED
    //
    // MessageText:
    //
    // Hot key is not registered.
    //
    int ERROR_HOTKEY_NOT_REGISTERED = 1419;

    //
    // MessageId: ERROR_WINDOW_NOT_DIALOG
    //
    // MessageText:
    //
    // The window is not a valid dialog window.
    //
    int ERROR_WINDOW_NOT_DIALOG = 1420;

    //
    // MessageId: ERROR_CONTROL_ID_NOT_FOUND
    //
    // MessageText:
    //
    // Control ID not found.
    //
    int ERROR_CONTROL_ID_NOT_FOUND = 1421;

    //
    // MessageId: ERROR_INVALID_COMBOBOX_MESSAGE
    //
    // MessageText:
    //
    // Invalid message for a combo box because it does not have an edit control.
    //
    int ERROR_INVALID_COMBOBOX_MESSAGE = 1422;

    //
    // MessageId: ERROR_WINDOW_NOT_COMBOBOX
    //
    // MessageText:
    //
    // The window is not a combo box.
    //
    int ERROR_WINDOW_NOT_COMBOBOX = 1423;

    //
    // MessageId: ERROR_INVALID_EDIT_HEIGHT
    //
    // MessageText:
    //
    // Height must be less than=256.
    //
    int ERROR_INVALID_EDIT_HEIGHT = 1424;

    //
    // MessageId: ERROR_DC_NOT_FOUND
    //
    // MessageText:
    //
    // Invalid device context (DC) handle.
    //
    int ERROR_DC_NOT_FOUND = 1425;

    //
    // MessageId: ERROR_INVALID_HOOK_FILTER
    //
    // MessageText:
    //
    // Invalid hook procedure type.
    //
    int ERROR_INVALID_HOOK_FILTER = 1426;

    //
    // MessageId: ERROR_INVALID_FILTER_PROC
    //
    // MessageText:
    //
    // Invalid hook procedure.
    //
    int ERROR_INVALID_FILTER_PROC = 1427;

    //
    // MessageId: ERROR_HOOK_NEEDS_HMOD
    //
    // MessageText:
    //
    // Cannot set nonlocal hook without a module handle.
    //
    int ERROR_HOOK_NEEDS_HMOD = 1428;

    //
    // MessageId: ERROR_GLOBAL_ONLY_HOOK
    //
    // MessageText:
    //
    // This hook procedure can only be set globally.
    //
    int ERROR_GLOBAL_ONLY_HOOK = 1429;

    //
    // MessageId: ERROR_JOURNAL_HOOK_SET
    //
    // MessageText:
    //
    // The journal hook procedure is already installed.
    //
    int ERROR_JOURNAL_HOOK_SET = 1430;

    //
    // MessageId: ERROR_HOOK_NOT_INSTALLED
    //
    // MessageText:
    //
    // The hook procedure is not installed.
    //
    int ERROR_HOOK_NOT_INSTALLED = 1431;

    //
    // MessageId: ERROR_INVALID_LB_MESSAGE
    //
    // MessageText:
    //
    // Invalid message for single-selection list box.
    //
    int ERROR_INVALID_LB_MESSAGE = 1432;

    //
    // MessageId: ERROR_SETCOUNT_ON_BAD_LB
    //
    // MessageText:
    //
    // LB_SETCOUNT sent to non-lazy list box.
    //
    int ERROR_SETCOUNT_ON_BAD_LB = 1433;

    //
    // MessageId: ERROR_LB_WITHOUT_TABSTOPS
    //
    // MessageText:
    //
    // This list box does not support tab stops.
    //
    int ERROR_LB_WITHOUT_TABSTOPS = 1434;

    //
    // MessageId: ERROR_DESTROY_OBJECT_OF_OTHER_THREAD
    //
    // MessageText:
    //
    // Cannot destroy object created by another thread.
    //
    int ERROR_DESTROY_OBJECT_OF_OTHER_THREAD = 1435;

    //
    // MessageId: ERROR_CHILD_WINDOW_MENU
    //
    // MessageText:
    //
    // Child windows cannot have menus.
    //
    int ERROR_CHILD_WINDOW_MENU = 1436;

    //
    // MessageId: ERROR_NO_SYSTEM_MENU
    //
    // MessageText:
    //
    // The window does not have a system menu.
    //
    int ERROR_NO_SYSTEM_MENU = 1437;

    //
    // MessageId: ERROR_INVALID_MSGBOX_STYLE
    //
    // MessageText:
    //
    // Invalid message box style.
    //
    int ERROR_INVALID_MSGBOX_STYLE = 1438;

    //
    // MessageId: ERROR_INVALID_SPI_VALUE
    //
    // MessageText:
    //
    // Invalid system-wide (SPI_*) parameter.
    //
    int ERROR_INVALID_SPI_VALUE = 1439;

    //
    // MessageId: ERROR_SCREEN_ALREADY_LOCKED
    //
    // MessageText:
    //
    // Screen already locked.
    //
    int ERROR_SCREEN_ALREADY_LOCKED = 1440;

    //
    // MessageId: ERROR_HWNDS_HAVE_DIFF_PARENT
    //
    // MessageText:
    //
    // All handles to windows in a multiple-window position structure must have
    // the same parent.
    //
    int ERROR_HWNDS_HAVE_DIFF_PARENT = 1441;

    //
    // MessageId: ERROR_NOT_CHILD_WINDOW
    //
    // MessageText:
    //
    // The window is not a child window.
    //
    int ERROR_NOT_CHILD_WINDOW = 1442;

    //
    // MessageId: ERROR_INVALID_GW_COMMAND
    //
    // MessageText:
    //
    // Invalid GW_* command.
    //
    int ERROR_INVALID_GW_COMMAND = 1443;

    //
    // MessageId: ERROR_INVALID_THREAD_ID
    //
    // MessageText:
    //
    // Invalid thread identifier.
    //
    int ERROR_INVALID_THREAD_ID = 1444;

    //
    // MessageId: ERROR_NON_MDICHILD_WINDOW
    //
    // MessageText:
    //
    // Cannot process a message from a window that is not a multiple document
    // interface (MDI) window.
    //
    int ERROR_NON_MDICHILD_WINDOW = 1445;

    //
    // MessageId: ERROR_POPUP_ALREADY_ACTIVE
    //
    // MessageText:
    //
    // Popup menu already active.
    //
    int ERROR_POPUP_ALREADY_ACTIVE = 1446;

    //
    // MessageId: ERROR_NO_SCROLLBARS
    //
    // MessageText:
    //
    // The window does not have scroll bars.
    //
    int ERROR_NO_SCROLLBARS = 1447;

    //
    // MessageId: ERROR_INVALID_SCROLLBAR_RANGE
    //
    // MessageText:
    //
    // Scroll bar range cannot be greater than MAXLONG.
    //
    int ERROR_INVALID_SCROLLBAR_RANGE = 1448;

    //
    // MessageId: ERROR_INVALID_SHOWWIN_COMMAND
    //
    // MessageText:
    //
    // Cannot show or remove the window in the way specified.
    //
    int ERROR_INVALID_SHOWWIN_COMMAND = 1449;

    //
    // MessageId: ERROR_NO_SYSTEM_RESOURCES
    //
    // MessageText:
    //
    // Insufficient system resources exist to complete the requested service.
    //
    int ERROR_NO_SYSTEM_RESOURCES = 1450;

    //
    // MessageId: ERROR_NONPAGED_SYSTEM_RESOURCES
    //
    // MessageText:
    //
    // Insufficient system resources exist to complete the requested service.
    //
    int ERROR_NONPAGED_SYSTEM_RESOURCES = 1451;

    //
    // MessageId: ERROR_PAGED_SYSTEM_RESOURCES
    //
    // MessageText:
    //
    // Insufficient system resources exist to complete the requested service.
    //
    int ERROR_PAGED_SYSTEM_RESOURCES = 1452;

    //
    // MessageId: ERROR_WORKING_SET_QUOTA
    //
    // MessageText:
    //
    // Insufficient quota to complete the requested service.
    //
    int ERROR_WORKING_SET_QUOTA = 1453;

    //
    // MessageId: ERROR_PAGEFILE_QUOTA
    //
    // MessageText:
    //
    // Insufficient quota to complete the requested service.
    //
    int ERROR_PAGEFILE_QUOTA = 1454;

    //
    // MessageId: ERROR_COMMITMENT_LIMIT
    //
    // MessageText:
    //
    // The paging file is too small for this operation to complete.
    //
    int ERROR_COMMITMENT_LIMIT = 1455;

    //
    // MessageId: ERROR_MENU_ITEM_NOT_FOUND
    //
    // MessageText:
    //
    // A menu item was not found.
    //
    int ERROR_MENU_ITEM_NOT_FOUND = 1456;

    //
    // MessageId: ERROR_INVALID_KEYBOARD_HANDLE
    //
    // MessageText:
    //
    // Invalid keyboard layout handle.
    //
    int ERROR_INVALID_KEYBOARD_HANDLE = 1457;

    //
    // MessageId: ERROR_HOOK_TYPE_NOT_ALLOWED
    //
    // MessageText:
    //
    // Hook type not allowed.
    //
    int ERROR_HOOK_TYPE_NOT_ALLOWED = 1458;

    //
    // MessageId: ERROR_REQUIRES_INTERACTIVE_WINDOWSTATION
    //
    // MessageText:
    //
    // This operation requires an interactive window station.
    //
    int ERROR_REQUIRES_INTERACTIVE_WINDOWSTATION = 1459;

    //
    // MessageId: ERROR_TIMEOUT
    //
    // MessageText:
    //
    // This operation returned because the timeout period expired.
    //
    int ERROR_TIMEOUT = 1460;

    //
    // MessageId: ERROR_INVALID_MONITOR_HANDLE
    //
    // MessageText:
    //
    // Invalid monitor handle.
    //
    int ERROR_INVALID_MONITOR_HANDLE = 1461;

    //
    // MessageId: ERROR_INCORRECT_SIZE
    //
    // MessageText:
    //
    // Incorrect size argument.
    //
    int ERROR_INCORRECT_SIZE = 1462;

    //
    // MessageId: ERROR_SYMLINK_CLASS_DISABLED
    //
    // MessageText:
    //
    // The symbolic link cannot be followed because its type is disabled.
    //
    int ERROR_SYMLINK_CLASS_DISABLED = 1463;

    //
    // MessageId: ERROR_SYMLINK_NOT_SUPPORTED
    //
    // MessageText:
    //
    // This application does not support the current operation on symbolic
    // links.
    //
    int ERROR_SYMLINK_NOT_SUPPORTED = 1464;

    //
    // MessageId: ERROR_XML_PARSE_ERROR
    //
    // MessageText:
    //
    // Windows was unable to parse the requested XML data.
    //
    int ERROR_XML_PARSE_ERROR = 1465;

    //
    // MessageId: ERROR_XMLDSIG_ERROR
    //
    // MessageText:
    //
    // An error was encountered while processing an XML digital signature.
    //
    int ERROR_XMLDSIG_ERROR = 1466;

    //
    // MessageId: ERROR_RESTART_APPLICATION
    //
    // MessageText:
    //
    // This application must be restarted.
    //
    int ERROR_RESTART_APPLICATION = 1467;

    //
    // MessageId: ERROR_WRONG_COMPARTMENT
    //
    // MessageText:
    //
    // The caller made the connection request in the wrong routing compartment.
    //
    int ERROR_WRONG_COMPARTMENT = 1468;

    //
    // MessageId: ERROR_AUTHIP_FAILURE
    //
    // MessageText:
    //
    // There was an AuthIP failure when attempting to connect to the remote
    // host.
    //
    int ERROR_AUTHIP_FAILURE = 1469;

    // /////////////////////////////////////////////////
    // =//
    // =EventLog Error codes=//
    // =//
    // =1500 to=1549=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_EVENTLOG_FILE_CORRUPT
    //
    // MessageText:
    //
    // The event log file is corrupted.
    //
    int ERROR_EVENTLOG_FILE_CORRUPT = 1500;

    //
    // MessageId: ERROR_EVENTLOG_CANT_START
    //
    // MessageText:
    //
    // No event log file could be opened, so the event logging service did not
    // start.
    //
    int ERROR_EVENTLOG_CANT_START = 1501;

    //
    // MessageId: ERROR_LOG_FILE_FULL
    //
    // MessageText:
    //
    // The event log file is full.
    //
    int ERROR_LOG_FILE_FULL = 1502;

    //
    // MessageId: ERROR_EVENTLOG_FILE_CHANGED
    //
    // MessageText:
    //
    // The event log file has changed between read operations.
    //
    int ERROR_EVENTLOG_FILE_CHANGED = 1503;

    // /////////////////////////////////////////////////
    // =//
    // =Class Scheduler Error codes=//
    // =//
    // =1550 to=1599=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_INVALID_TASK_NAME
    //
    // MessageText:
    //
    // The specified task name is invalid.
    //
    int ERROR_INVALID_TASK_NAME = 1550;

    //
    // MessageId: ERROR_INVALID_TASK_INDEX
    //
    // MessageText:
    //
    // The specified task index is invalid.
    //
    int ERROR_INVALID_TASK_INDEX = 1551;

    //
    // MessageId: ERROR_THREAD_ALREADY_IN_TASK
    //
    // MessageText:
    //
    // The specified thread is already joining a task.
    //
    int ERROR_THREAD_ALREADY_IN_TASK = 1552;

    // /////////////////////////////////////////////////
    // =//
    // =MSI Error codes=//
    // =//
    // =1600 to=1699=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_INSTALL_SERVICE_FAILURE
    //
    // MessageText:
    //
    // The Windows Installer Service could not be accessed. This can occur if
    // the Windows Installer is not correctly installed. Contact your support
    // personnel for assistance.
    //
    int ERROR_INSTALL_SERVICE_FAILURE = 1601;

    //
    // MessageId: ERROR_INSTALL_USEREXIT
    //
    // MessageText:
    //
    // User cancelled installation.
    //
    int ERROR_INSTALL_USEREXIT = 1602;

    //
    // MessageId: ERROR_INSTALL_FAILURE
    //
    // MessageText:
    //
    // Fatal error during installation.
    //
    int ERROR_INSTALL_FAILURE = 1603;

    //
    // MessageId: ERROR_INSTALL_SUSPEND
    //
    // MessageText:
    //
    // Installation suspended, incomplete.
    //
    int ERROR_INSTALL_SUSPEND = 1604;

    //
    // MessageId: ERROR_UNKNOWN_PRODUCT
    //
    // MessageText:
    //
    // This action is only valid for products that are currently installed.
    //
    int ERROR_UNKNOWN_PRODUCT = 1605;

    //
    // MessageId: ERROR_UNKNOWN_FEATURE
    //
    // MessageText:
    //
    // Feature ID not registered.
    //
    int ERROR_UNKNOWN_FEATURE = 1606;

    //
    // MessageId: ERROR_UNKNOWN_COMPONENT
    //
    // MessageText:
    //
    // Component ID not registered.
    //
    int ERROR_UNKNOWN_COMPONENT = 1607;

    //
    // MessageId: ERROR_UNKNOWN_PROPERTY
    //
    // MessageText:
    //
    // Unknown property.
    //
    int ERROR_UNKNOWN_PROPERTY = 1608;

    //
    // MessageId: ERROR_INVALID_HANDLE_STATE
    //
    // MessageText:
    //
    // Handle is in an invalid state.
    //
    int ERROR_INVALID_HANDLE_STATE = 1609;

    //
    // MessageId: ERROR_BAD_CONFIGURATION
    //
    // MessageText:
    //
    // The configuration data for this product is corrupt. Contact your support
    // personnel.
    //
    int ERROR_BAD_CONFIGURATION = 1610;

    //
    // MessageId: ERROR_INDEX_ABSENT
    //
    // MessageText:
    //
    // Component qualifier not present.
    //
    int ERROR_INDEX_ABSENT = 1611;

    //
    // MessageId: ERROR_INSTALL_SOURCE_ABSENT
    //
    // MessageText:
    //
    // The installation source for this product is not available. Verify that
    // the source exists and that you can access it.
    //
    int ERROR_INSTALL_SOURCE_ABSENT = 1612;

    //
    // MessageId: ERROR_INSTALL_PACKAGE_VERSION
    //
    // MessageText:
    //
    // This installation package cannot be installed by the Windows Installer
    // service. You must install a Windows service pack that contains a newer
    // version of the Windows Installer service.
    //
    int ERROR_INSTALL_PACKAGE_VERSION = 1613;

    //
    // MessageId: ERROR_PRODUCT_UNINSTALLED
    //
    // MessageText:
    //
    // Product is uninstalled.
    //
    int ERROR_PRODUCT_UNINSTALLED = 1614;

    //
    // MessageId: ERROR_BAD_QUERY_SYNTAX
    //
    // MessageText:
    //
    // SQL query syntax invalid or unsupported.
    //
    int ERROR_BAD_QUERY_SYNTAX = 1615;

    //
    // MessageId: ERROR_INVALID_FIELD
    //
    // MessageText:
    //
    // Record field does not exist.
    //
    int ERROR_INVALID_FIELD = 1616;

    //
    // MessageId: ERROR_DEVICE_REMOVED
    //
    // MessageText:
    //
    // The device has been removed.
    //
    int ERROR_DEVICE_REMOVED = 1617;

    //
    // MessageId: ERROR_INSTALL_ALREADY_RUNNING
    //
    // MessageText:
    //
    // Another installation is already in progress. Complete that installation
    // before proceeding with this install.
    //
    int ERROR_INSTALL_ALREADY_RUNNING = 1618;

    //
    // MessageId: ERROR_INSTALL_PACKAGE_OPEN_FAILED
    //
    // MessageText:
    //
    // This installation package could not be opened. Verify that the package
    // exists and that you can access it, or contact the application vendor to
    // verify that this is a valid Windows Installer package.
    //
    int ERROR_INSTALL_PACKAGE_OPEN_FAILED = 1619;

    //
    // MessageId: ERROR_INSTALL_PACKAGE_INVALID
    //
    // MessageText:
    //
    // This installation package could not be opened. Contact the application
    // vendor to verify that this is a valid Windows Installer package.
    //
    int ERROR_INSTALL_PACKAGE_INVALID = 1620;

    //
    // MessageId: ERROR_INSTALL_UI_FAILURE
    //
    // MessageText:
    //
    // There was an error starting the Windows Installer service user interface.
    // Contact your support personnel.
    //
    int ERROR_INSTALL_UI_FAILURE = 1621;

    //
    // MessageId: ERROR_INSTALL_LOG_FAILURE
    //
    // MessageText:
    //
    // Error opening installation log file. Verify that the specified log file
    // location exists and that you can write to it.
    //
    int ERROR_INSTALL_LOG_FAILURE = 1622;

    //
    // MessageId: ERROR_INSTALL_LANGUAGE_UNSUPPORTED
    //
    // MessageText:
    //
    // The language of this installation package is not supported by your
    // system.
    //
    int ERROR_INSTALL_LANGUAGE_UNSUPPORTED = 1623;

    //
    // MessageId: ERROR_INSTALL_TRANSFORM_FAILURE
    //
    // MessageText:
    //
    // Error applying transforms. Verify that the specified transform paths are
    // valid.
    //
    int ERROR_INSTALL_TRANSFORM_FAILURE = 1624;

    //
    // MessageId: ERROR_INSTALL_PACKAGE_REJECTED
    //
    // MessageText:
    //
    // This installation is forbidden by system policy. Contact your system
    // administrator.
    //
    int ERROR_INSTALL_PACKAGE_REJECTED = 1625;

    //
    // MessageId: ERROR_FUNCTION_NOT_CALLED
    //
    // MessageText:
    //
    // Function could not be executed.
    //
    int ERROR_FUNCTION_NOT_CALLED = 1626;

    //
    // MessageId: ERROR_FUNCTION_FAILED
    //
    // MessageText:
    //
    // Function failed during execution.
    //
    int ERROR_FUNCTION_FAILED = 1627;

    //
    // MessageId: ERROR_INVALID_TABLE
    //
    // MessageText:
    //
    // Invalid or unknown table specified.
    //
    int ERROR_INVALID_TABLE = 1628;

    //
    // MessageId: ERROR_DATATYPE_MISMATCH
    //
    // MessageText:
    //
    // Data supplied is of wrong type.
    //
    int ERROR_DATATYPE_MISMATCH = 1629;

    //
    // MessageId: ERROR_UNSUPPORTED_TYPE
    //
    // MessageText:
    //
    // Data of this type is not supported.
    //
    int ERROR_UNSUPPORTED_TYPE = 1630;

    //
    // MessageId: ERROR_CREATE_FAILED
    //
    // MessageText:
    //
    // The Windows Installer service failed to start. Contact your support
    // personnel.
    //
    int ERROR_CREATE_FAILED = 1631;

    //
    // MessageId: ERROR_INSTALL_TEMP_UNWRITABLE
    //
    // MessageText:
    //
    // The Temp folder is on a drive that is full or is inaccessible. Free up
    // space on the drive or verify that you have write permission on the Temp
    // folder.
    //
    int ERROR_INSTALL_TEMP_UNWRITABLE = 1632;

    //
    // MessageId: ERROR_INSTALL_PLATFORM_UNSUPPORTED
    //
    // MessageText:
    //
    // This installation package is not supported by this processor type.
    // Contact your product vendor.
    //
    int ERROR_INSTALL_PLATFORM_UNSUPPORTED = 1633;

    //
    // MessageId: ERROR_INSTALL_NOTUSED
    //
    // MessageText:
    //
    // Component not used on this computer.
    //
    int ERROR_INSTALL_NOTUSED = 1634;

    //
    // MessageId: ERROR_PATCH_PACKAGE_OPEN_FAILED
    //
    // MessageText:
    //
    // This update package could not be opened. Verify that the update package
    // exists and that you can access it, or contact the application vendor to
    // verify that this is a valid Windows Installer update package.
    //
    int ERROR_PATCH_PACKAGE_OPEN_FAILED = 1635;

    //
    // MessageId: ERROR_PATCH_PACKAGE_INVALID
    //
    // MessageText:
    //
    // This update package could not be opened. Contact the application vendor
    // to verify that this is a valid Windows Installer update package.
    //
    int ERROR_PATCH_PACKAGE_INVALID = 1636;

    //
    // MessageId: ERROR_PATCH_PACKAGE_UNSUPPORTED
    //
    // MessageText:
    //
    // This update package cannot be processed by the Windows Installer service.
    // You must install a Windows service pack that contains a newer version of
    // the Windows Installer service.
    //
    int ERROR_PATCH_PACKAGE_UNSUPPORTED = 1637;

    //
    // MessageId: ERROR_PRODUCT_VERSION
    //
    // MessageText:
    //
    // Another version of this product is already installed. Installation of
    // this version cannot continue. To configure or remove the existing version
    // of this product, use Add/Remove Programs on the Control Panel.
    //
    int ERROR_PRODUCT_VERSION = 1638;

    //
    // MessageId: ERROR_INVALID_COMMAND_LINE
    //
    // MessageText:
    //
    // Invalid command line argument. Consult the Windows Installer SDK for
    // detailed command line help.
    //
    int ERROR_INVALID_COMMAND_LINE = 1639;

    //
    // MessageId: ERROR_INSTALL_REMOTE_DISALLOWED
    //
    // MessageText:
    //
    // Only administrators have permission to add, remove, or configure server
    // software during a Terminal services remote session. If you want to
    // install or configure software on the server, contact your network
    // administrator.
    //
    int ERROR_INSTALL_REMOTE_DISALLOWED = 1640;

    //
    // MessageId: ERROR_SUCCESS_REBOOT_INITIATED
    //
    // MessageText:
    //
    // The requested operation completed successfully. The system will be
    // restarted so the changes can take effect.
    //
    int ERROR_SUCCESS_REBOOT_INITIATED = 1641;

    //
    // MessageId: ERROR_PATCH_TARGET_NOT_FOUND
    //
    // MessageText:
    //
    // The upgrade cannot be installed by the Windows Installer service because
    // the program to be upgraded may be missing, or the upgrade may update a
    // different version of the program. Verify that the program to be upgraded
    // exists on your computer and that you have the correct upgrade.
    //
    int ERROR_PATCH_TARGET_NOT_FOUND = 1642;

    //
    // MessageId: ERROR_PATCH_PACKAGE_REJECTED
    //
    // MessageText:
    //
    // The update package is not permitted by software restriction policy.
    //
    int ERROR_PATCH_PACKAGE_REJECTED = 1643;

    //
    // MessageId: ERROR_INSTALL_TRANSFORM_REJECTED
    //
    // MessageText:
    //
    // One or more customizations are not permitted by software restriction
    // policy.
    //
    int ERROR_INSTALL_TRANSFORM_REJECTED = 1644;

    //
    // MessageId: ERROR_INSTALL_REMOTE_PROHIBITED
    //
    // MessageText:
    //
    // The Windows Installer does not permit installation from a Remote Desktop
    // Connection.
    //
    int ERROR_INSTALL_REMOTE_PROHIBITED = 1645;

    //
    // MessageId: ERROR_PATCH_REMOVAL_UNSUPPORTED
    //
    // MessageText:
    //
    // Uninstallation of the update package is not supported.
    //
    int ERROR_PATCH_REMOVAL_UNSUPPORTED = 1646;

    //
    // MessageId: ERROR_UNKNOWN_PATCH
    //
    // MessageText:
    //
    // The update is not applied to this product.
    //
    int ERROR_UNKNOWN_PATCH = 1647;

    //
    // MessageId: ERROR_PATCH_NO_SEQUENCE
    //
    // MessageText:
    //
    // No valid sequence could be found for the set of updates.
    //
    int ERROR_PATCH_NO_SEQUENCE = 1648;

    //
    // MessageId: ERROR_PATCH_REMOVAL_DISALLOWED
    //
    // MessageText:
    //
    // Update removal was disallowed by policy.
    //
    int ERROR_PATCH_REMOVAL_DISALLOWED = 1649;

    //
    // MessageId: ERROR_INVALID_PATCH_XML
    //
    // MessageText:
    //
    // The XML update data is invalid.
    //
    int ERROR_INVALID_PATCH_XML = 1650;

    //
    // MessageId: ERROR_PATCH_MANAGED_ADVERTISED_PRODUCT
    //
    // MessageText:
    //
    // Windows Installer does not permit updating of managed advertised
    // products. At least one feature of the product must be installed before
    // applying the update.
    //
    int ERROR_PATCH_MANAGED_ADVERTISED_PRODUCT = 1651;

    //
    // MessageId: ERROR_INSTALL_SERVICE_SAFEBOOT
    //
    // MessageText:
    //
    // The Windows Installer service is not accessible in Safe Mode. Please try
    // again when your computer is not in Safe Mode or you can use System
    // Restore to return your machine to a previous good state.
    //
    int ERROR_INSTALL_SERVICE_SAFEBOOT = 1652;

    // /////////////////////////////////////////////////
    // =//
    // =RPC Error codes=//
    // =//
    // =1700 to=1999=//
    // /////////////////////////////////////////////////

    //
    // MessageId: RPC_S_INVALID_STRING_BINDING
    //
    // MessageText:
    //
    // The string binding is invalid.
    //
    int RPC_S_INVALID_STRING_BINDING = 1700;

    //
    // MessageId: RPC_S_WRONG_KIND_OF_BINDING
    //
    // MessageText:
    //
    // The binding handle is not the correct type.
    //
    int RPC_S_WRONG_KIND_OF_BINDING = 1701;

    //
    // MessageId: RPC_S_INVALID_BINDING
    //
    // MessageText:
    //
    // The binding handle is invalid.
    //
    int RPC_S_INVALID_BINDING = 1702;

    //
    // MessageId: RPC_S_PROTSEQ_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The RPC protocol sequence is not supported.
    //
    int RPC_S_PROTSEQ_NOT_SUPPORTED = 1703;

    //
    // MessageId: RPC_S_INVALID_RPC_PROTSEQ
    //
    // MessageText:
    //
    // The RPC protocol sequence is invalid.
    //
    int RPC_S_INVALID_RPC_PROTSEQ = 1704;

    //
    // MessageId: RPC_S_INVALID_STRING_UUID
    //
    // MessageText:
    //
    // The string universal unique identifier (UUID) is invalid.
    //
    int RPC_S_INVALID_STRING_UUID = 1705;

    //
    // MessageId: RPC_S_INVALID_ENDPOINT_FORMAT
    //
    // MessageText:
    //
    // The endpoint format is invalid.
    //
    int RPC_S_INVALID_ENDPOINT_FORMAT = 1706;

    //
    // MessageId: RPC_S_INVALID_NET_ADDR
    //
    // MessageText:
    //
    // The network address is invalid.
    //
    int RPC_S_INVALID_NET_ADDR = 1707;

    //
    // MessageId: RPC_S_NO_ENDPOINT_FOUND
    //
    // MessageText:
    //
    // No endpoint was found.
    //
    int RPC_S_NO_ENDPOINT_FOUND = 1708;

    //
    // MessageId: RPC_S_INVALID_TIMEOUT
    //
    // MessageText:
    //
    // The timeout value is invalid.
    //
    int RPC_S_INVALID_TIMEOUT = 1709;

    //
    // MessageId: RPC_S_OBJECT_NOT_FOUND
    //
    // MessageText:
    //
    // The object universal unique identifier (UUID) was not found.
    //
    int RPC_S_OBJECT_NOT_FOUND = 1710;

    //
    // MessageId: RPC_S_ALREADY_REGISTERED
    //
    // MessageText:
    //
    // The object universal unique identifier (UUID) has already been
    // registered.
    //
    int RPC_S_ALREADY_REGISTERED = 1711;

    //
    // MessageId: RPC_S_TYPE_ALREADY_REGISTERED
    //
    // MessageText:
    //
    // The type universal unique identifier (UUID) has already been registered.
    //
    int RPC_S_TYPE_ALREADY_REGISTERED = 1712;

    //
    // MessageId: RPC_S_ALREADY_LISTENING
    //
    // MessageText:
    //
    // The RPC server is already listening.
    //
    int RPC_S_ALREADY_LISTENING = 1713;

    //
    // MessageId: RPC_S_NO_PROTSEQS_REGISTERED
    //
    // MessageText:
    //
    // No protocol sequences have been registered.
    //
    int RPC_S_NO_PROTSEQS_REGISTERED = 1714;

    //
    // MessageId: RPC_S_NOT_LISTENING
    //
    // MessageText:
    //
    // The RPC server is not listening.
    //
    int RPC_S_NOT_LISTENING = 1715;

    //
    // MessageId: RPC_S_UNKNOWN_MGR_TYPE
    //
    // MessageText:
    //
    // The manager type is unknown.
    //
    int RPC_S_UNKNOWN_MGR_TYPE = 1716;

    //
    // MessageId: RPC_S_UNKNOWN_IF
    //
    // MessageText:
    //
    // The interface is unknown.
    //
    int RPC_S_UNKNOWN_IF = 1717;

    //
    // MessageId: RPC_S_NO_BINDINGS
    //
    // MessageText:
    //
    // There are no bindings.
    //
    int RPC_S_NO_BINDINGS = 1718;

    //
    // MessageId: RPC_S_NO_PROTSEQS
    //
    // MessageText:
    //
    // There are no protocol sequences.
    //
    int RPC_S_NO_PROTSEQS = 1719;

    //
    // MessageId: RPC_S_CANT_CREATE_ENDPOINT
    //
    // MessageText:
    //
    // The endpoint cannot be created.
    //
    int RPC_S_CANT_CREATE_ENDPOINT = 1720;

    //
    // MessageId: RPC_S_OUT_OF_RESOURCES
    //
    // MessageText:
    //
    // Not enough resources are available to complete this operation.
    //
    int RPC_S_OUT_OF_RESOURCES = 1721;

    //
    // MessageId: RPC_S_SERVER_UNAVAILABLE
    //
    // MessageText:
    //
    // The RPC server is unavailable.
    //
    int RPC_S_SERVER_UNAVAILABLE = 1722;

    //
    // MessageId: RPC_S_SERVER_TOO_BUSY
    //
    // MessageText:
    //
    // The RPC server is too busy to complete this operation.
    //
    int RPC_S_SERVER_TOO_BUSY = 1723;

    //
    // MessageId: RPC_S_INVALID_NETWORK_OPTIONS
    //
    // MessageText:
    //
    // The network options are invalid.
    //
    int RPC_S_INVALID_NETWORK_OPTIONS = 1724;

    //
    // MessageId: RPC_S_NO_CALL_ACTIVE
    //
    // MessageText:
    //
    // There are no remote procedure calls active on this thread.
    //
    int RPC_S_NO_CALL_ACTIVE = 1725;

    //
    // MessageId: RPC_S_CALL_FAILED
    //
    // MessageText:
    //
    // The remote procedure call failed.
    //
    int RPC_S_CALL_FAILED = 1726;

    //
    // MessageId: RPC_S_CALL_FAILED_DNE
    //
    // MessageText:
    //
    // The remote procedure call failed and did not execute.
    //
    int RPC_S_CALL_FAILED_DNE = 1727;

    //
    // MessageId: RPC_S_PROTOCOL_ERROR
    //
    // MessageText:
    //
    // A remote procedure call (RPC) protocol error occurred.
    //
    int RPC_S_PROTOCOL_ERROR = 1728;

    //
    // MessageId: RPC_S_PROXY_ACCESS_DENIED
    //
    // MessageText:
    //
    // Access to the HTTP proxy is denied.
    //
    int RPC_S_PROXY_ACCESS_DENIED = 1729;

    //
    // MessageId: RPC_S_UNSUPPORTED_TRANS_SYN
    //
    // MessageText:
    //
    // The transfer syntax is not supported by the RPC server.
    //
    int RPC_S_UNSUPPORTED_TRANS_SYN = 1730;

    //
    // MessageId: RPC_S_UNSUPPORTED_TYPE
    //
    // MessageText:
    //
    // The universal unique identifier (UUID) type is not supported.
    //
    int RPC_S_UNSUPPORTED_TYPE = 1732;

    //
    // MessageId: RPC_S_INVALID_TAG
    //
    // MessageText:
    //
    // The tag is invalid.
    //
    int RPC_S_INVALID_TAG = 1733;

    //
    // MessageId: RPC_S_INVALID_BOUND
    //
    // MessageText:
    //
    // The array bounds are invalid.
    //
    int RPC_S_INVALID_BOUND = 1734;

    //
    // MessageId: RPC_S_NO_ENTRY_NAME
    //
    // MessageText:
    //
    // The binding does not contain an entry name.
    //
    int RPC_S_NO_ENTRY_NAME = 1735;

    //
    // MessageId: RPC_S_INVALID_NAME_SYNTAX
    //
    // MessageText:
    //
    // The name syntax is invalid.
    //
    int RPC_S_INVALID_NAME_SYNTAX = 1736;

    //
    // MessageId: RPC_S_UNSUPPORTED_NAME_SYNTAX
    //
    // MessageText:
    //
    // The name syntax is not supported.
    //
    int RPC_S_UNSUPPORTED_NAME_SYNTAX = 1737;

    //
    // MessageId: RPC_S_UUID_NO_ADDRESS
    //
    // MessageText:
    //
    // No network address is available to use to construct a universal unique
    // identifier (UUID).
    //
    int RPC_S_UUID_NO_ADDRESS = 1739;

    //
    // MessageId: RPC_S_DUPLICATE_ENDPOINT
    //
    // MessageText:
    //
    // The endpoint is a duplicate.
    //
    int RPC_S_DUPLICATE_ENDPOINT = 1740;

    //
    // MessageId: RPC_S_UNKNOWN_AUTHN_TYPE
    //
    // MessageText:
    //
    // The authentication type is unknown.
    //
    int RPC_S_UNKNOWN_AUTHN_TYPE = 1741;

    //
    // MessageId: RPC_S_MAX_CALLS_TOO_SMALL
    //
    // MessageText:
    //
    // The maximum number of calls is too small.
    //
    int RPC_S_MAX_CALLS_TOO_SMALL = 1742;

    //
    // MessageId: RPC_S_STRING_TOO_LONG
    //
    // MessageText:
    //
    // The string is too long.
    //
    int RPC_S_STRING_TOO_LONG = 1743;

    //
    // MessageId: RPC_S_PROTSEQ_NOT_FOUND
    //
    // MessageText:
    //
    // The RPC protocol sequence was not found.
    //
    int RPC_S_PROTSEQ_NOT_FOUND = 1744;

    //
    // MessageId: RPC_S_PROCNUM_OUT_OF_RANGE
    //
    // MessageText:
    //
    // The procedure number is out of range.
    //
    int RPC_S_PROCNUM_OUT_OF_RANGE = 1745;

    //
    // MessageId: RPC_S_BINDING_HAS_NO_AUTH
    //
    // MessageText:
    //
    // The binding does not contain any authentication information.
    //
    int RPC_S_BINDING_HAS_NO_AUTH = 1746;

    //
    // MessageId: RPC_S_UNKNOWN_AUTHN_SERVICE
    //
    // MessageText:
    //
    // The authentication service is unknown.
    //
    int RPC_S_UNKNOWN_AUTHN_SERVICE = 1747;

    //
    // MessageId: RPC_S_UNKNOWN_AUTHN_LEVEL
    //
    // MessageText:
    //
    // The authentication level is unknown.
    //
    int RPC_S_UNKNOWN_AUTHN_LEVEL = 1748;

    //
    // MessageId: RPC_S_INVALID_AUTH_IDENTITY
    //
    // MessageText:
    //
    // The security context is invalid.
    //
    int RPC_S_INVALID_AUTH_IDENTITY = 1749;

    //
    // MessageId: RPC_S_UNKNOWN_AUTHZ_SERVICE
    //
    // MessageText:
    //
    // The authorization service is unknown.
    //
    int RPC_S_UNKNOWN_AUTHZ_SERVICE = 1750;

    //
    // MessageId: EPT_S_INVALID_ENTRY
    //
    // MessageText:
    //
    // The entry is invalid.
    //
    int EPT_S_INVALID_ENTRY = 1751;

    //
    // MessageId: EPT_S_CANT_PERFORM_OP
    //
    // MessageText:
    //
    // The server endpoint cannot perform the operation.
    //
    int EPT_S_CANT_PERFORM_OP = 1752;

    //
    // MessageId: EPT_S_NOT_REGISTERED
    //
    // MessageText:
    //
    // There are no more endpoints available from the endpoint mapper.
    //
    int EPT_S_NOT_REGISTERED = 1753;

    //
    // MessageId: RPC_S_NOTHING_TO_EXPORT
    //
    // MessageText:
    //
    // No interfaces have been exported.
    //
    int RPC_S_NOTHING_TO_EXPORT = 1754;

    //
    // MessageId: RPC_S_INCOMPLETE_NAME
    //
    // MessageText:
    //
    // The entry name is incomplete.
    //
    int RPC_S_INCOMPLETE_NAME = 1755;

    //
    // MessageId: RPC_S_INVALID_VERS_OPTION
    //
    // MessageText:
    //
    // The version option is invalid.
    //
    int RPC_S_INVALID_VERS_OPTION = 1756;

    //
    // MessageId: RPC_S_NO_MORE_MEMBERS
    //
    // MessageText:
    //
    // There are no more members.
    //
    int RPC_S_NO_MORE_MEMBERS = 1757;

    //
    // MessageId: RPC_S_NOT_ALL_OBJS_UNEXPORTED
    //
    // MessageText:
    //
    // There is nothing to unexport.
    //
    int RPC_S_NOT_ALL_OBJS_UNEXPORTED = 1758;

    //
    // MessageId: RPC_S_INTERFACE_NOT_FOUND
    //
    // MessageText:
    //
    // The interface was not found.
    //
    int RPC_S_INTERFACE_NOT_FOUND = 1759;

    //
    // MessageId: RPC_S_ENTRY_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The entry already exists.
    //
    int RPC_S_ENTRY_ALREADY_EXISTS = 1760;

    //
    // MessageId: RPC_S_ENTRY_NOT_FOUND
    //
    // MessageText:
    //
    // The entry is not found.
    //
    int RPC_S_ENTRY_NOT_FOUND = 1761;

    //
    // MessageId: RPC_S_NAME_SERVICE_UNAVAILABLE
    //
    // MessageText:
    //
    // The name service is unavailable.
    //
    int RPC_S_NAME_SERVICE_UNAVAILABLE = 1762;

    //
    // MessageId: RPC_S_INVALID_NAF_ID
    //
    // MessageText:
    //
    // The network address family is invalid.
    //
    int RPC_S_INVALID_NAF_ID = 1763;

    //
    // MessageId: RPC_S_CANNOT_SUPPORT
    //
    // MessageText:
    //
    // The requested operation is not supported.
    //
    int RPC_S_CANNOT_SUPPORT = 1764;

    //
    // MessageId: RPC_S_NO_CONTEXT_AVAILABLE
    //
    // MessageText:
    //
    // No security context is available to allow impersonation.
    //
    int RPC_S_NO_CONTEXT_AVAILABLE = 1765;

    //
    // MessageId: RPC_S_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal error occurred in a remote procedure call (RPC).
    //
    int RPC_S_INTERNAL_ERROR = 1766;

    //
    // MessageId: RPC_S_ZERO_DIVIDE
    //
    // MessageText:
    //
    // The RPC server attempted an integer division by zero.
    //
    int RPC_S_ZERO_DIVIDE = 1767;

    //
    // MessageId: RPC_S_ADDRESS_ERROR
    //
    // MessageText:
    //
    // An addressing error occurred in the RPC server.
    //
    int RPC_S_ADDRESS_ERROR = 1768;

    //
    // MessageId: RPC_S_FP_DIV_ZERO
    //
    // MessageText:
    //
    // A floating-point operation at the RPC server caused a division by zero.
    //
    int RPC_S_FP_DIV_ZERO = 1769;

    //
    // MessageId: RPC_S_FP_UNDERFLOW
    //
    // MessageText:
    //
    // A floating-point underflow occurred at the RPC server.
    //
    int RPC_S_FP_UNDERFLOW = 1770;

    //
    // MessageId: RPC_S_FP_OVERFLOW
    //
    // MessageText:
    //
    // A floating-point overflow occurred at the RPC server.
    //
    int RPC_S_FP_OVERFLOW = 1771;

    //
    // MessageId: RPC_X_NO_MORE_ENTRIES
    //
    // MessageText:
    //
    // The list of RPC servers available for the binding of auto handles has
    // been exhausted.
    //
    int RPC_X_NO_MORE_ENTRIES = 1772;

    //
    // MessageId: RPC_X_SS_CHAR_TRANS_OPEN_FAIL
    //
    // MessageText:
    //
    // Unable to open the character translation table file.
    //
    int RPC_X_SS_CHAR_TRANS_OPEN_FAIL = 1773;

    //
    // MessageId: RPC_X_SS_CHAR_TRANS_SHORT_FILE
    //
    // MessageText:
    //
    // The file containing the character translation table has fewer than=512
    // bytes.
    //
    int RPC_X_SS_CHAR_TRANS_SHORT_FILE = 1774;

    //
    // MessageId: RPC_X_SS_IN_NULL_CONTEXT
    //
    // MessageText:
    //
    // A null context handle was passed from the client to the host during a
    // remote procedure call.
    //
    int RPC_X_SS_IN_NULL_CONTEXT = 1775;

    //
    // MessageId: RPC_X_SS_CONTEXT_DAMAGED
    //
    // MessageText:
    //
    // The context handle changed during a remote procedure call.
    //
    int RPC_X_SS_CONTEXT_DAMAGED = 1777;

    //
    // MessageId: RPC_X_SS_HANDLES_MISMATCH
    //
    // MessageText:
    //
    // The binding handles passed to a remote procedure call do not match.
    //
    int RPC_X_SS_HANDLES_MISMATCH = 1778;

    //
    // MessageId: RPC_X_SS_CANNOT_GET_CALL_HANDLE
    //
    // MessageText:
    //
    // The stub is unable to get the remote procedure call handle.
    //
    int RPC_X_SS_CANNOT_GET_CALL_HANDLE = 1779;

    //
    // MessageId: RPC_X_NULL_REF_POINTER
    //
    // MessageText:
    //
    // A null reference pointer was passed to the stub.
    //
    int RPC_X_NULL_REF_POINTER = 1780;

    //
    // MessageId: RPC_X_ENUM_VALUE_OUT_OF_RANGE
    //
    // MessageText:
    //
    // The enumeration value is out of range.
    //
    int RPC_X_ENUM_VALUE_OUT_OF_RANGE = 1781;

    //
    // MessageId: RPC_X_BYTE_COUNT_TOO_SMALL
    //
    // MessageText:
    //
    // The byte count is too small.
    //
    int RPC_X_BYTE_COUNT_TOO_SMALL = 1782;

    //
    // MessageId: RPC_X_BAD_STUB_DATA
    //
    // MessageText:
    //
    // The stub received bad data.
    //
    int RPC_X_BAD_STUB_DATA = 1783;

    //
    // MessageId: ERROR_INVALID_USER_BUFFER
    //
    // MessageText:
    //
    // The supplied user buffer is not valid for the requested operation.
    //
    int ERROR_INVALID_USER_BUFFER = 1784;

    //
    // MessageId: ERROR_UNRECOGNIZED_MEDIA
    //
    // MessageText:
    //
    // The disk media is not recognized. It may not be formatted.
    //
    int ERROR_UNRECOGNIZED_MEDIA = 1785;

    //
    // MessageId: ERROR_NO_TRUST_LSA_SECRET
    //
    // MessageText:
    //
    // The workstation does not have a trust secret.
    //
    int ERROR_NO_TRUST_LSA_SECRET = 1786;

    //
    // MessageId: ERROR_NO_TRUST_SAM_ACCOUNT
    //
    // MessageText:
    //
    // The security database on the server does not have a computer account for
    // this workstation trust relationship.
    //
    int ERROR_NO_TRUST_SAM_ACCOUNT = 1787;

    //
    // MessageId: ERROR_TRUSTED_DOMAIN_FAILURE
    //
    // MessageText:
    //
    // The trust relationship between the primary domain and the trusted domain
    // failed.
    //
    int ERROR_TRUSTED_DOMAIN_FAILURE = 1788;

    //
    // MessageId: ERROR_TRUSTED_RELATIONSHIP_FAILURE
    //
    // MessageText:
    //
    // The trust relationship between this workstation and the primary domain
    // failed.
    //
    int ERROR_TRUSTED_RELATIONSHIP_FAILURE = 1789;

    //
    // MessageId: ERROR_TRUST_FAILURE
    //
    // MessageText:
    //
    // The network logon failed.
    //
    int ERROR_TRUST_FAILURE = 1790;

    //
    // MessageId: RPC_S_CALL_IN_PROGRESS
    //
    // MessageText:
    //
    // A remote procedure call is already in progress for this thread.
    //
    int RPC_S_CALL_IN_PROGRESS = 1791;

    //
    // MessageId: ERROR_NETLOGON_NOT_STARTED
    //
    // MessageText:
    //
    // An attempt was made to logon, but the network logon service was not
    // started.
    //
    int ERROR_NETLOGON_NOT_STARTED = 1792;

    //
    // MessageId: ERROR_ACCOUNT_EXPIRED
    //
    // MessageText:
    //
    // The user's account has expired.
    //
    int ERROR_ACCOUNT_EXPIRED = 1793;

    //
    // MessageId: ERROR_REDIRECTOR_HAS_OPEN_HANDLES
    //
    // MessageText:
    //
    // The redirector is in use and cannot be unloaded.
    //
    int ERROR_REDIRECTOR_HAS_OPEN_HANDLES = 1794;

    //
    // MessageId: ERROR_PRINTER_DRIVER_ALREADY_INSTALLED
    //
    // MessageText:
    //
    // The specified printer driver is already installed.
    //
    int ERROR_PRINTER_DRIVER_ALREADY_INSTALLED = 1795;

    //
    // MessageId: ERROR_UNKNOWN_PORT
    //
    // MessageText:
    //
    // The specified port is unknown.
    //
    int ERROR_UNKNOWN_PORT = 1796;

    //
    // MessageId: ERROR_UNKNOWN_PRINTER_DRIVER
    //
    // MessageText:
    //
    // The printer driver is unknown.
    //
    int ERROR_UNKNOWN_PRINTER_DRIVER = 1797;

    //
    // MessageId: ERROR_UNKNOWN_PRINTPROCESSOR
    //
    // MessageText:
    //
    // The print processor is unknown.
    //
    int ERROR_UNKNOWN_PRINTPROCESSOR = 1798;

    //
    // MessageId: ERROR_INVALID_SEPARATOR_FILE
    //
    // MessageText:
    //
    // The specified separator file is invalid.
    //
    int ERROR_INVALID_SEPARATOR_FILE = 1799;

    //
    // MessageId: ERROR_INVALID_PRIORITY
    //
    // MessageText:
    //
    // The specified priority is invalid.
    //
    int ERROR_INVALID_PRIORITY = 1800;

    //
    // MessageId: ERROR_INVALID_PRINTER_NAME
    //
    // MessageText:
    //
    // The printer name is invalid.
    //
    int ERROR_INVALID_PRINTER_NAME = 1801;

    //
    // MessageId: ERROR_PRINTER_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The printer already exists.
    //
    int ERROR_PRINTER_ALREADY_EXISTS = 1802;

    //
    // MessageId: ERROR_INVALID_PRINTER_COMMAND
    //
    // MessageText:
    //
    // The printer command is invalid.
    //
    int ERROR_INVALID_PRINTER_COMMAND = 1803;

    //
    // MessageId: ERROR_INVALID_DATATYPE
    //
    // MessageText:
    //
    // The specified datatype is invalid.
    //
    int ERROR_INVALID_DATATYPE = 1804;

    //
    // MessageId: ERROR_INVALID_ENVIRONMENT
    //
    // MessageText:
    //
    // The environment specified is invalid.
    //
    int ERROR_INVALID_ENVIRONMENT = 1805;

    //
    // MessageId: RPC_S_NO_MORE_BINDINGS
    //
    // MessageText:
    //
    // There are no more bindings.
    //
    int RPC_S_NO_MORE_BINDINGS = 1806;

    //
    // MessageId: ERROR_NOLOGON_INTERDOMAIN_TRUST_ACCOUNT
    //
    // MessageText:
    //
    // The account used is an interdomain trust account. Use your global user
    // account or local user account to access this server.
    //
    int ERROR_NOLOGON_INTERDOMAIN_TRUST_ACCOUNT = 1807;

    //
    // MessageId: ERROR_NOLOGON_WORKSTATION_TRUST_ACCOUNT
    //
    // MessageText:
    //
    // The account used is a computer account. Use your global user account or
    // local user account to access this server.
    //
    int ERROR_NOLOGON_WORKSTATION_TRUST_ACCOUNT = 1808;

    //
    // MessageId: ERROR_NOLOGON_SERVER_TRUST_ACCOUNT
    //
    // MessageText:
    //
    // The account used is a server trust account. Use your global user account
    // or local user account to access this server.
    //
    int ERROR_NOLOGON_SERVER_TRUST_ACCOUNT = 1809;

    //
    // MessageId: ERROR_DOMAIN_TRUST_INCONSISTENT
    //
    // MessageText:
    //
    // The name or security ID (SID) of the domain specified is inconsistent
    // with the trust information for that domain.
    //
    int ERROR_DOMAIN_TRUST_INCONSISTENT = 1810;

    //
    // MessageId: ERROR_SERVER_HAS_OPEN_HANDLES
    //
    // MessageText:
    //
    // The server is in use and cannot be unloaded.
    //
    int ERROR_SERVER_HAS_OPEN_HANDLES = 1811;

    //
    // MessageId: ERROR_RESOURCE_DATA_NOT_FOUND
    //
    // MessageText:
    //
    // The specified image file did not contain a resource section.
    //
    int ERROR_RESOURCE_DATA_NOT_FOUND = 1812;

    //
    // MessageId: ERROR_RESOURCE_TYPE_NOT_FOUND
    //
    // MessageText:
    //
    // The specified resource type cannot be found in the image file.
    //
    int ERROR_RESOURCE_TYPE_NOT_FOUND = 1813;

    //
    // MessageId: ERROR_RESOURCE_NAME_NOT_FOUND
    //
    // MessageText:
    //
    // The specified resource name cannot be found in the image file.
    //
    int ERROR_RESOURCE_NAME_NOT_FOUND = 1814;

    //
    // MessageId: ERROR_RESOURCE_LANG_NOT_FOUND
    //
    // MessageText:
    //
    // The specified resource language ID cannot be found in the image file.
    //
    int ERROR_RESOURCE_LANG_NOT_FOUND = 1815;

    //
    // MessageId: ERROR_NOT_ENOUGH_QUOTA
    //
    // MessageText:
    //
    // Not enough quota is available to process this command.
    //
    int ERROR_NOT_ENOUGH_QUOTA = 1816;

    //
    // MessageId: RPC_S_NO_INTERFACES
    //
    // MessageText:
    //
    // No interfaces have been registered.
    //
    int RPC_S_NO_INTERFACES = 1817;

    //
    // MessageId: RPC_S_CALL_CANCELLED
    //
    // MessageText:
    //
    // The remote procedure call was cancelled.
    //
    int RPC_S_CALL_CANCELLED = 1818;

    //
    // MessageId: RPC_S_BINDING_INCOMPLETE
    //
    // MessageText:
    //
    // The binding handle does not contain all required information.
    //
    int RPC_S_BINDING_INCOMPLETE = 1819;

    //
    // MessageId: RPC_S_COMM_FAILURE
    //
    // MessageText:
    //
    // A communications failure occurred during a remote procedure call.
    //
    int RPC_S_COMM_FAILURE = 1820;

    //
    // MessageId: RPC_S_UNSUPPORTED_AUTHN_LEVEL
    //
    // MessageText:
    //
    // The requested authentication level is not supported.
    //
    int RPC_S_UNSUPPORTED_AUTHN_LEVEL = 1821;

    //
    // MessageId: RPC_S_NO_PRINC_NAME
    //
    // MessageText:
    //
    // No principal name registered.
    //
    int RPC_S_NO_PRINC_NAME = 1822;

    //
    // MessageId: RPC_S_NOT_RPC_ERROR
    //
    // MessageText:
    //
    // The error specified is not a valid Windows RPC error code.
    //
    int RPC_S_NOT_RPC_ERROR = 1823;

    //
    // MessageId: RPC_S_UUID_LOCAL_ONLY
    //
    // MessageText:
    //
    // A UUID that is valid only on this computer has been allocated.
    //
    int RPC_S_UUID_LOCAL_ONLY = 1824;

    //
    // MessageId: RPC_S_SEC_PKG_ERROR
    //
    // MessageText:
    //
    // A security package specific error occurred.
    //
    int RPC_S_SEC_PKG_ERROR = 1825;

    //
    // MessageId: RPC_S_NOT_CANCELLED
    //
    // MessageText:
    //
    // Thread is not canceled.
    //
    int RPC_S_NOT_CANCELLED = 1826;

    //
    // MessageId: RPC_X_INVALID_ES_ACTION
    //
    // MessageText:
    //
    // Invalid operation on the encoding/decoding handle.
    //
    int RPC_X_INVALID_ES_ACTION = 1827;

    //
    // MessageId: RPC_X_WRONG_ES_VERSION
    //
    // MessageText:
    //
    // Incompatible version of the serializing package.
    //
    int RPC_X_WRONG_ES_VERSION = 1828;

    //
    // MessageId: RPC_X_WRONG_STUB_VERSION
    //
    // MessageText:
    //
    // Incompatible version of the RPC stub.
    //
    int RPC_X_WRONG_STUB_VERSION = 1829;

    //
    // MessageId: RPC_X_INVALID_PIPE_OBJECT
    //
    // MessageText:
    //
    // The RPC pipe object is invalid or corrupted.
    //
    int RPC_X_INVALID_PIPE_OBJECT = 1830;

    //
    // MessageId: RPC_X_WRONG_PIPE_ORDER
    //
    // MessageText:
    //
    // An invalid operation was attempted on an RPC pipe object.
    //
    int RPC_X_WRONG_PIPE_ORDER = 1831;

    //
    // MessageId: RPC_X_WRONG_PIPE_VERSION
    //
    // MessageText:
    //
    // Unsupported RPC pipe version.
    //
    int RPC_X_WRONG_PIPE_VERSION = 1832;

    //
    // MessageId: RPC_S_GROUP_MEMBER_NOT_FOUND
    //
    // MessageText:
    //
    // The group member was not found.
    //
    int RPC_S_GROUP_MEMBER_NOT_FOUND = 1898;

    //
    // MessageId: EPT_S_CANT_CREATE
    //
    // MessageText:
    //
    // The endpoint mapper database entry could not be created.
    //
    int EPT_S_CANT_CREATE = 1899;

    //
    // MessageId: RPC_S_INVALID_OBJECT
    //
    // MessageText:
    //
    // The object universal unique identifier (UUID) is the nil UUID.
    //
    int RPC_S_INVALID_OBJECT = 1900;

    //
    // MessageId: ERROR_INVALID_TIME
    //
    // MessageText:
    //
    // The specified time is invalid.
    //
    int ERROR_INVALID_TIME = 1901;

    //
    // MessageId: ERROR_INVALID_FORM_NAME
    //
    // MessageText:
    //
    // The specified form name is invalid.
    //
    int ERROR_INVALID_FORM_NAME = 1902;

    //
    // MessageId: ERROR_INVALID_FORM_SIZE
    //
    // MessageText:
    //
    // The specified form size is invalid.
    //
    int ERROR_INVALID_FORM_SIZE = 1903;

    //
    // MessageId: ERROR_ALREADY_WAITING
    //
    // MessageText:
    //
    // The specified printer handle is already being waited on
    //
    int ERROR_ALREADY_WAITING = 1904;

    //
    // MessageId: ERROR_PRINTER_DELETED
    //
    // MessageText:
    //
    // The specified printer has been deleted.
    //
    int ERROR_PRINTER_DELETED = 1905;

    //
    // MessageId: ERROR_INVALID_PRINTER_STATE
    //
    // MessageText:
    //
    // The state of the printer is invalid.
    //
    int ERROR_INVALID_PRINTER_STATE = 1906;

    //
    // MessageId: ERROR_PASSWORD_MUST_CHANGE
    //
    // MessageText:
    //
    // The user's password must be changed before logging on the first time.
    //
    int ERROR_PASSWORD_MUST_CHANGE = 1907;

    //
    // MessageId: ERROR_DOMAIN_CONTROLLER_NOT_FOUND
    //
    // MessageText:
    //
    // Could not find the domain controller for this domain.
    //
    int ERROR_DOMAIN_CONTROLLER_NOT_FOUND = 1908;

    //
    // MessageId: ERROR_ACCOUNT_LOCKED_OUT
    //
    // MessageText:
    //
    // The referenced account is currently locked out and may not be logged on
    // to.
    //
    int ERROR_ACCOUNT_LOCKED_OUT = 1909;

    //
    // MessageId: OR_INVALID_OXID
    //
    // MessageText:
    //
    // The object exporter specified was not found.
    //
    int OR_INVALID_OXID = 1910;

    //
    // MessageId: OR_INVALID_OID
    //
    // MessageText:
    //
    // The object specified was not found.
    //
    int OR_INVALID_OID = 1911;

    //
    // MessageId: OR_INVALID_SET
    //
    // MessageText:
    //
    // The object resolver set specified was not found.
    //
    int OR_INVALID_SET = 1912;

    //
    // MessageId: RPC_S_SEND_INCOMPLETE
    //
    // MessageText:
    //
    // Some data remains to be sent in the request buffer.
    //
    int RPC_S_SEND_INCOMPLETE = 1913;

    //
    // MessageId: RPC_S_INVALID_ASYNC_HANDLE
    //
    // MessageText:
    //
    // Invalid asynchronous remote procedure call handle.
    //
    int RPC_S_INVALID_ASYNC_HANDLE = 1914;

    //
    // MessageId: RPC_S_INVALID_ASYNC_CALL
    //
    // MessageText:
    //
    // Invalid asynchronous RPC call handle for this operation.
    //
    int RPC_S_INVALID_ASYNC_CALL = 1915;

    //
    // MessageId: RPC_X_PIPE_CLOSED
    //
    // MessageText:
    //
    // The RPC pipe object has already been closed.
    //
    int RPC_X_PIPE_CLOSED = 1916;

    //
    // MessageId: RPC_X_PIPE_DISCIPLINE_ERROR
    //
    // MessageText:
    //
    // The RPC call completed before all pipes were processed.
    //
    int RPC_X_PIPE_DISCIPLINE_ERROR = 1917;

    //
    // MessageId: RPC_X_PIPE_EMPTY
    //
    // MessageText:
    //
    // No more data is available from the RPC pipe.
    //
    int RPC_X_PIPE_EMPTY = 1918;

    //
    // MessageId: ERROR_NO_SITENAME
    //
    // MessageText:
    //
    // No site name is available for this machine.
    //
    int ERROR_NO_SITENAME = 1919;

    //
    // MessageId: ERROR_CANT_ACCESS_FILE
    //
    // MessageText:
    //
    // The file cannot be accessed by the system.
    //
    int ERROR_CANT_ACCESS_FILE = 1920;

    //
    // MessageId: ERROR_CANT_RESOLVE_FILENAME
    //
    // MessageText:
    //
    // The name of the file cannot be resolved by the system.
    //
    int ERROR_CANT_RESOLVE_FILENAME = 1921;

    //
    // MessageId: RPC_S_ENTRY_TYPE_MISMATCH
    //
    // MessageText:
    //
    // The entry is not of the expected type.
    //
    int RPC_S_ENTRY_TYPE_MISMATCH = 1922;

    //
    // MessageId: RPC_S_NOT_ALL_OBJS_EXPORTED
    //
    // MessageText:
    //
    // Not all object UUIDs could be exported to the specified entry.
    //
    int RPC_S_NOT_ALL_OBJS_EXPORTED = 1923;

    //
    // MessageId: RPC_S_INTERFACE_NOT_EXPORTED
    //
    // MessageText:
    //
    // Interface could not be exported to the specified entry.
    //
    int RPC_S_INTERFACE_NOT_EXPORTED = 1924;

    //
    // MessageId: RPC_S_PROFILE_NOT_ADDED
    //
    // MessageText:
    //
    // The specified profile entry could not be added.
    //
    int RPC_S_PROFILE_NOT_ADDED = 1925;

    //
    // MessageId: RPC_S_PRF_ELT_NOT_ADDED
    //
    // MessageText:
    //
    // The specified profile element could not be added.
    //
    int RPC_S_PRF_ELT_NOT_ADDED = 1926;

    //
    // MessageId: RPC_S_PRF_ELT_NOT_REMOVED
    //
    // MessageText:
    //
    // The specified profile element could not be removed.
    //
    int RPC_S_PRF_ELT_NOT_REMOVED = 1927;

    //
    // MessageId: RPC_S_GRP_ELT_NOT_ADDED
    //
    // MessageText:
    //
    // The group element could not be added.
    //
    int RPC_S_GRP_ELT_NOT_ADDED = 1928;

    //
    // MessageId: RPC_S_GRP_ELT_NOT_REMOVED
    //
    // MessageText:
    //
    // The group element could not be removed.
    //
    int RPC_S_GRP_ELT_NOT_REMOVED = 1929;

    //
    // MessageId: ERROR_KM_DRIVER_BLOCKED
    //
    // MessageText:
    //
    // The printer driver is not compatible with a policy enabled on your
    // computer that blocks NT=4.0 drivers.
    //
    int ERROR_KM_DRIVER_BLOCKED = 1930;

    //
    // MessageId: ERROR_CONTEXT_EXPIRED
    //
    // MessageText:
    //
    // The context has expired and can no longer be used.
    //
    int ERROR_CONTEXT_EXPIRED = 1931;

    //
    // MessageId: ERROR_PER_USER_TRUST_QUOTA_EXCEEDED
    //
    // MessageText:
    //
    // The current user's delegated trust creation quota has been exceeded.
    //
    int ERROR_PER_USER_TRUST_QUOTA_EXCEEDED = 1932;

    //
    // MessageId: ERROR_ALL_USER_TRUST_QUOTA_EXCEEDED
    //
    // MessageText:
    //
    // The total delegated trust creation quota has been exceeded.
    //
    int ERROR_ALL_USER_TRUST_QUOTA_EXCEEDED = 1933;

    //
    // MessageId: ERROR_USER_DELETE_TRUST_QUOTA_EXCEEDED
    //
    // MessageText:
    //
    // The current user's delegated trust deletion quota has been exceeded.
    //
    int ERROR_USER_DELETE_TRUST_QUOTA_EXCEEDED = 1934;

    //
    // MessageId: ERROR_AUTHENTICATION_FIREWALL_FAILED
    //
    // MessageText:
    //
    // Logon Failure: The machine you are logging onto is protected by an
    // authentication firewall. The specified account is not allowed to
    // authenticate to the machine.
    //
    int ERROR_AUTHENTICATION_FIREWALL_FAILED = 1935;

    //
    // MessageId: ERROR_REMOTE_PRINT_CONNECTIONS_BLOCKED
    //
    // MessageText:
    //
    // Remote connections to the Print Spooler are blocked by a policy set on
    // your machine.
    //
    int ERROR_REMOTE_PRINT_CONNECTIONS_BLOCKED = 1936;

    //
    // MessageId: ERROR_NTLM_BLOCKED
    //
    // MessageText:
    //
    // Logon Failure: Authentication failed because NTLM authentication has been
    // disabled.
    //
    int ERROR_NTLM_BLOCKED = 1937;

    // /////////////////////////////////////////////////
    // =//
    // =OpenGL Error codes=//
    // =//
    // =2000 to=2009=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_INVALID_PIXEL_FORMAT
    //
    // MessageText:
    //
    // The pixel format is invalid.
    //
    int ERROR_INVALID_PIXEL_FORMAT = 2000;

    //
    // MessageId: ERROR_BAD_DRIVER
    //
    // MessageText:
    //
    // The specified driver is invalid.
    //
    int ERROR_BAD_DRIVER = 2001;

    //
    // MessageId: ERROR_INVALID_WINDOW_STYLE
    //
    // MessageText:
    //
    // The window style or class attribute is invalid for this operation.
    //
    int ERROR_INVALID_WINDOW_STYLE = 2002;

    //
    // MessageId: ERROR_METAFILE_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The requested metafile operation is not supported.
    //
    int ERROR_METAFILE_NOT_SUPPORTED = 2003;

    //
    // MessageId: ERROR_TRANSFORM_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The requested transformation operation is not supported.
    //
    int ERROR_TRANSFORM_NOT_SUPPORTED = 2004;

    //
    // MessageId: ERROR_CLIPPING_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The requested clipping operation is not supported.
    //
    int ERROR_CLIPPING_NOT_SUPPORTED = 2005;

    // /////////////////////////////////////////////////
    // =//
    // Image Color Management Error codes //
    // =//
    // =2010 to=2049=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_INVALID_CMM
    //
    // MessageText:
    //
    // The specified color management module is invalid.
    //
    int ERROR_INVALID_CMM = 2010;

    //
    // MessageId: ERROR_INVALID_PROFILE
    //
    // MessageText:
    //
    // The specified color profile is invalid.
    //
    int ERROR_INVALID_PROFILE = 2011;

    //
    // MessageId: ERROR_TAG_NOT_FOUND
    //
    // MessageText:
    //
    // The specified tag was not found.
    //
    int ERROR_TAG_NOT_FOUND = 2012;

    //
    // MessageId: ERROR_TAG_NOT_PRESENT
    //
    // MessageText:
    //
    // A required tag is not present.
    //
    int ERROR_TAG_NOT_PRESENT = 2013;

    //
    // MessageId: ERROR_DUPLICATE_TAG
    //
    // MessageText:
    //
    // The specified tag is already present.
    //
    int ERROR_DUPLICATE_TAG = 2014;

    //
    // MessageId: ERROR_PROFILE_NOT_ASSOCIATED_WITH_DEVICE
    //
    // MessageText:
    //
    // The specified color profile is not associated with the specified device.
    //
    int ERROR_PROFILE_NOT_ASSOCIATED_WITH_DEVICE = 2015;

    //
    // MessageId: ERROR_PROFILE_NOT_FOUND
    //
    // MessageText:
    //
    // The specified color profile was not found.
    //
    int ERROR_PROFILE_NOT_FOUND = 2016;

    //
    // MessageId: ERROR_INVALID_COLORSPACE
    //
    // MessageText:
    //
    // The specified color space is invalid.
    //
    int ERROR_INVALID_COLORSPACE = 2017;

    //
    // MessageId: ERROR_ICM_NOT_ENABLED
    //
    // MessageText:
    //
    // Image Color Management is not enabled.
    //
    int ERROR_ICM_NOT_ENABLED = 2018;

    //
    // MessageId: ERROR_DELETING_ICM_XFORM
    //
    // MessageText:
    //
    // There was an error while deleting the color transform.
    //
    int ERROR_DELETING_ICM_XFORM = 2019;

    //
    // MessageId: ERROR_INVALID_TRANSFORM
    //
    // MessageText:
    //
    // The specified color transform is invalid.
    //
    int ERROR_INVALID_TRANSFORM = 2020;

    //
    // MessageId: ERROR_COLORSPACE_MISMATCH
    //
    // MessageText:
    //
    // The specified transform does not match the bitmap's color space.
    //
    int ERROR_COLORSPACE_MISMATCH = 2021;

    //
    // MessageId: ERROR_INVALID_COLORINDEX
    //
    // MessageText:
    //
    // The specified named color index is not present in the profile.
    //
    int ERROR_INVALID_COLORINDEX = 2022;

    //
    // MessageId: ERROR_PROFILE_DOES_NOT_MATCH_DEVICE
    //
    // MessageText:
    //
    // The specified profile is intended for a device of a different type than
    // the specified device.
    //
    int ERROR_PROFILE_DOES_NOT_MATCH_DEVICE = 2023;

    // /////////////////////////////////////////////////
    // =//
    // =Winnet32 Error codes=//
    // =//
    // =2100 to=2999=//
    // =//
    // The range=2100 through=2999 is reserved for //
    // network status codes. See lmerr.h for a //
    // complete listing=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_CONNECTED_OTHER_PASSWORD
    //
    // MessageText:
    //
    // The network connection was made successfully, but the user had to be
    // prompted for a password other than the one originally specified.
    //
    int ERROR_CONNECTED_OTHER_PASSWORD = 2108;

    //
    // MessageId: ERROR_CONNECTED_OTHER_PASSWORD_DEFAULT
    //
    // MessageText:
    //
    // The network connection was made successfully using default credentials.
    //
    int ERROR_CONNECTED_OTHER_PASSWORD_DEFAULT = 2109;

    //
    // MessageId: ERROR_BAD_USERNAME
    //
    // MessageText:
    //
    // The specified username is invalid.
    //
    int ERROR_BAD_USERNAME = 2202;

    //
    // MessageId: ERROR_NOT_CONNECTED
    //
    // MessageText:
    //
    // This network connection does not exist.
    //
    int ERROR_NOT_CONNECTED = 2250;

    //
    // MessageId: ERROR_OPEN_FILES
    //
    // MessageText:
    //
    // This network connection has files open or requests pending.
    //
    int ERROR_OPEN_FILES = 2401;

    //
    // MessageId: ERROR_ACTIVE_CONNECTIONS
    //
    // MessageText:
    //
    // Active connections still exist.
    //
    int ERROR_ACTIVE_CONNECTIONS = 2402;

    //
    // MessageId: ERROR_DEVICE_IN_USE
    //
    // MessageText:
    //
    // The device is in use by an active process and cannot be disconnected.
    //
    int ERROR_DEVICE_IN_USE = 2404;

    // /////////////////////////////////////////////////
    // =//
    // =Win32 Spooler Error codes=//
    // =//
    // =3000 to=3049=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_UNKNOWN_PRINT_MONITOR
    //
    // MessageText:
    //
    // The specified print monitor is unknown.
    //
    int ERROR_UNKNOWN_PRINT_MONITOR = 3000;

    //
    // MessageId: ERROR_PRINTER_DRIVER_IN_USE
    //
    // MessageText:
    //
    // The specified printer driver is currently in use.
    //
    int ERROR_PRINTER_DRIVER_IN_USE = 3001;

    //
    // MessageId: ERROR_SPOOL_FILE_NOT_FOUND
    //
    // MessageText:
    //
    // The spool file was not found.
    //
    int ERROR_SPOOL_FILE_NOT_FOUND = 3002;

    //
    // MessageId: ERROR_SPL_NO_STARTDOC
    //
    // MessageText:
    //
    // A StartDocPrinter call was not issued.
    //
    int ERROR_SPL_NO_STARTDOC = 3003;

    //
    // MessageId: ERROR_SPL_NO_ADDJOB
    //
    // MessageText:
    //
    // An AddJob call was not issued.
    //
    int ERROR_SPL_NO_ADDJOB = 3004;

    //
    // MessageId: ERROR_PRINT_PROCESSOR_ALREADY_INSTALLED
    //
    // MessageText:
    //
    // The specified print processor has already been installed.
    //
    int ERROR_PRINT_PROCESSOR_ALREADY_INSTALLED = 3005;

    //
    // MessageId: ERROR_PRINT_MONITOR_ALREADY_INSTALLED
    //
    // MessageText:
    //
    // The specified print monitor has already been installed.
    //
    int ERROR_PRINT_MONITOR_ALREADY_INSTALLED = 3006;

    //
    // MessageId: ERROR_INVALID_PRINT_MONITOR
    //
    // MessageText:
    //
    // The specified print monitor does not have the required functions.
    //
    int ERROR_INVALID_PRINT_MONITOR = 3007;

    //
    // MessageId: ERROR_PRINT_MONITOR_IN_USE
    //
    // MessageText:
    //
    // The specified print monitor is currently in use.
    //
    int ERROR_PRINT_MONITOR_IN_USE = 3008;

    //
    // MessageId: ERROR_PRINTER_HAS_JOBS_QUEUED
    //
    // MessageText:
    //
    // The requested operation is not allowed when there are jobs queued to the
    // printer.
    //
    int ERROR_PRINTER_HAS_JOBS_QUEUED = 3009;

    //
    // MessageId: ERROR_SUCCESS_REBOOT_REQUIRED
    //
    // MessageText:
    //
    // The requested operation is successful. Changes will not be effective
    // until the system is rebooted.
    //
    int ERROR_SUCCESS_REBOOT_REQUIRED = 3010;

    //
    // MessageId: ERROR_SUCCESS_RESTART_REQUIRED
    //
    // MessageText:
    //
    // The requested operation is successful. Changes will not be effective
    // until the service is restarted.
    //
    int ERROR_SUCCESS_RESTART_REQUIRED = 3011;

    //
    // MessageId: ERROR_PRINTER_NOT_FOUND
    //
    // MessageText:
    //
    // No printers were found.
    //
    int ERROR_PRINTER_NOT_FOUND = 3012;

    //
    // MessageId: ERROR_PRINTER_DRIVER_WARNED
    //
    // MessageText:
    //
    // The printer driver is known to be unreliable.
    //
    int ERROR_PRINTER_DRIVER_WARNED = 3013;

    //
    // MessageId: ERROR_PRINTER_DRIVER_BLOCKED
    //
    // MessageText:
    //
    // The printer driver is known to harm the system.
    //
    int ERROR_PRINTER_DRIVER_BLOCKED = 3014;

    //
    // MessageId: ERROR_PRINTER_DRIVER_PACKAGE_IN_USE
    //
    // MessageText:
    //
    // The specified printer driver package is currently in use.
    //
    int ERROR_PRINTER_DRIVER_PACKAGE_IN_USE = 3015;

    //
    // MessageId: ERROR_CORE_DRIVER_PACKAGE_NOT_FOUND
    //
    // MessageText:
    //
    // Unable to find a core driver package that is required by the printer
    // driver package.
    //
    int ERROR_CORE_DRIVER_PACKAGE_NOT_FOUND = 3016;

    //
    // MessageId: ERROR_FAIL_REBOOT_REQUIRED
    //
    // MessageText:
    //
    // The requested operation failed. A system reboot is required to roll back
    // changes made.
    //
    int ERROR_FAIL_REBOOT_REQUIRED = 3017;

    //
    // MessageId: ERROR_FAIL_REBOOT_INITIATED
    //
    // MessageText:
    //
    // The requested operation failed. A system reboot has been initiated to
    // roll back changes made.
    //
    int ERROR_FAIL_REBOOT_INITIATED = 3018;

    //
    // MessageId: ERROR_PRINTER_DRIVER_DOWNLOAD_NEEDED
    //
    // MessageText:
    //
    // The specified printer driver was not found on the system and needs to be
    // downloaded.
    //
    int ERROR_PRINTER_DRIVER_DOWNLOAD_NEEDED = 3019;

    //
    // MessageId: ERROR_PRINT_JOB_RESTART_REQUIRED
    //
    // MessageText:
    //
    // The requested print job has failed to print. A print system update
    // requires the job to be resubmitted.
    //
    int ERROR_PRINT_JOB_RESTART_REQUIRED = 3020;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =3050 to=3899=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =IO Error Codes=//
    // =//
    // =3900 to=3999=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_IO_REISSUE_AS_CACHED
    //
    // MessageText:
    //
    // Reissue the given operation as a cached IO operation
    //
    int ERROR_IO_REISSUE_AS_CACHED = 3950;

    // /////////////////////////////////////////////////
    // =//
    // =Wins Error codes=//
    // =//
    // =4000 to=4049=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_WINS_INTERNAL
    //
    // MessageText:
    //
    // WINS encountered an error while processing the command.
    //
    int ERROR_WINS_INTERNAL = 4000;

    //
    // MessageId: ERROR_CAN_NOT_DEL_LOCAL_WINS
    //
    // MessageText:
    //
    // The local WINS cannot be deleted.
    //
    int ERROR_CAN_NOT_DEL_LOCAL_WINS = 4001;

    //
    // MessageId: ERROR_STATIC_INIT
    //
    // MessageText:
    //
    // The importation from the file failed.
    //
    int ERROR_STATIC_INIT = 4002;

    //
    // MessageId: ERROR_INC_BACKUP
    //
    // MessageText:
    //
    // The backup failed. Was a full backup done before?
    //
    int ERROR_INC_BACKUP = 4003;

    //
    // MessageId: ERROR_FULL_BACKUP
    //
    // MessageText:
    //
    // The backup failed. Check the directory to which you are backing the
    // database.
    //
    int ERROR_FULL_BACKUP = 4004;

    //
    // MessageId: ERROR_REC_NON_EXISTENT
    //
    // MessageText:
    //
    // The name does not exist in the WINS database.
    //
    int ERROR_REC_NON_EXISTENT = 4005;

    //
    // MessageId: ERROR_RPL_NOT_ALLOWED
    //
    // MessageText:
    //
    // Replication with a nonconfigured partner is not allowed.
    //
    int ERROR_RPL_NOT_ALLOWED = 4006;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =4050 to=4099=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =DHCP Error codes=//
    // =//
    // =4100 to=4149=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_DHCP_ADDRESS_CONFLICT
    //
    // MessageText:
    //
    // The DHCP client has obtained an IP address that is already in use on the
    // network. The local interface will be disabled until the DHCP client can
    // obtain a new address.
    //
    int ERROR_DHCP_ADDRESS_CONFLICT = 4100;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =4150 to=4199=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =WMI Error codes=//
    // =//
    // =4200 to=4249=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_WMI_GUID_NOT_FOUND
    //
    // MessageText:
    //
    // The GUID passed was not recognized as valid by a WMI data provider.
    //
    int ERROR_WMI_GUID_NOT_FOUND = 4200;

    //
    // MessageId: ERROR_WMI_INSTANCE_NOT_FOUND
    //
    // MessageText:
    //
    // The instance name passed was not recognized as valid by a WMI data
    // provider.
    //
    int ERROR_WMI_INSTANCE_NOT_FOUND = 4201;

    //
    // MessageId: ERROR_WMI_ITEMID_NOT_FOUND
    //
    // MessageText:
    //
    // The data item ID passed was not recognized as valid by a WMI data
    // provider.
    //
    int ERROR_WMI_ITEMID_NOT_FOUND = 4202;

    //
    // MessageId: ERROR_WMI_TRY_AGAIN
    //
    // MessageText:
    //
    // The WMI request could not be completed and should be retried.
    //
    int ERROR_WMI_TRY_AGAIN = 4203;

    //
    // MessageId: ERROR_WMI_DP_NOT_FOUND
    //
    // MessageText:
    //
    // The WMI data provider could not be located.
    //
    int ERROR_WMI_DP_NOT_FOUND = 4204;

    //
    // MessageId: ERROR_WMI_UNRESOLVED_INSTANCE_REF
    //
    // MessageText:
    //
    // The WMI data provider references an instance set that has not been
    // registered.
    //
    int ERROR_WMI_UNRESOLVED_INSTANCE_REF = 4205;

    //
    // MessageId: ERROR_WMI_ALREADY_ENABLED
    //
    // MessageText:
    //
    // The WMI data block or event notification has already been enabled.
    //
    int ERROR_WMI_ALREADY_ENABLED = 4206;

    //
    // MessageId: ERROR_WMI_GUID_DISCONNECTED
    //
    // MessageText:
    //
    // The WMI data block is no longer available.
    //
    int ERROR_WMI_GUID_DISCONNECTED = 4207;

    //
    // MessageId: ERROR_WMI_SERVER_UNAVAILABLE
    //
    // MessageText:
    //
    // The WMI data service is not available.
    //
    int ERROR_WMI_SERVER_UNAVAILABLE = 4208;

    //
    // MessageId: ERROR_WMI_DP_FAILED
    //
    // MessageText:
    //
    // The WMI data provider failed to carry out the request.
    //
    int ERROR_WMI_DP_FAILED = 4209;

    //
    // MessageId: ERROR_WMI_INVALID_MOF
    //
    // MessageText:
    //
    // The WMI MOF information is not valid.
    //
    int ERROR_WMI_INVALID_MOF = 4210;

    //
    // MessageId: ERROR_WMI_INVALID_REGINFO
    //
    // MessageText:
    //
    // The WMI registration information is not valid.
    //
    int ERROR_WMI_INVALID_REGINFO = 4211;

    //
    // MessageId: ERROR_WMI_ALREADY_DISABLED
    //
    // MessageText:
    //
    // The WMI data block or event notification has already been disabled.
    //
    int ERROR_WMI_ALREADY_DISABLED = 4212;

    //
    // MessageId: ERROR_WMI_READ_ONLY
    //
    // MessageText:
    //
    // The WMI data item or data block is read only.
    //
    int ERROR_WMI_READ_ONLY = 4213;

    //
    // MessageId: ERROR_WMI_SET_FAILURE
    //
    // MessageText:
    //
    // The WMI data item or data block could not be changed.
    //
    int ERROR_WMI_SET_FAILURE = 4214;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =4250 to=4299=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =RSM (Media Services) Error codes //
    // =//
    // =4300 to=4349=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_INVALID_MEDIA
    //
    // MessageText:
    //
    // The media identifier does not represent a valid medium.
    //
    int ERROR_INVALID_MEDIA = 4300;

    //
    // MessageId: ERROR_INVALID_LIBRARY
    //
    // MessageText:
    //
    // The library identifier does not represent a valid library.
    //
    int ERROR_INVALID_LIBRARY = 4301;

    //
    // MessageId: ERROR_INVALID_MEDIA_POOL
    //
    // MessageText:
    //
    // The media pool identifier does not represent a valid media pool.
    //
    int ERROR_INVALID_MEDIA_POOL = 4302;

    //
    // MessageId: ERROR_DRIVE_MEDIA_MISMATCH
    //
    // MessageText:
    //
    // The drive and medium are not compatible or exist in different libraries.
    //
    int ERROR_DRIVE_MEDIA_MISMATCH = 4303;

    //
    // MessageId: ERROR_MEDIA_OFFLINE
    //
    // MessageText:
    //
    // The medium currently exists in an offline library and must be online to
    // perform this operation.
    //
    int ERROR_MEDIA_OFFLINE = 4304;

    //
    // MessageId: ERROR_LIBRARY_OFFLINE
    //
    // MessageText:
    //
    // The operation cannot be performed on an offline library.
    //
    int ERROR_LIBRARY_OFFLINE = 4305;

    //
    // MessageId: ERROR_EMPTY
    //
    // MessageText:
    //
    // The library, drive, or media pool is empty.
    //
    int ERROR_EMPTY = 4306;

    //
    // MessageId: ERROR_NOT_EMPTY
    //
    // MessageText:
    //
    // The library, drive, or media pool must be empty to perform this
    // operation.
    //
    int ERROR_NOT_EMPTY = 4307;

    //
    // MessageId: ERROR_MEDIA_UNAVAILABLE
    //
    // MessageText:
    //
    // No media is currently available in this media pool or library.
    //
    int ERROR_MEDIA_UNAVAILABLE = 4308;

    //
    // MessageId: ERROR_RESOURCE_DISABLED
    //
    // MessageText:
    //
    // A resource required for this operation is disabled.
    //
    int ERROR_RESOURCE_DISABLED = 4309;

    //
    // MessageId: ERROR_INVALID_CLEANER
    //
    // MessageText:
    //
    // The media identifier does not represent a valid cleaner.
    //
    int ERROR_INVALID_CLEANER = 4310;

    //
    // MessageId: ERROR_UNABLE_TO_CLEAN
    //
    // MessageText:
    //
    // The drive cannot be cleaned or does not support cleaning.
    //
    int ERROR_UNABLE_TO_CLEAN = 4311;

    //
    // MessageId: ERROR_OBJECT_NOT_FOUND
    //
    // MessageText:
    //
    // The object identifier does not represent a valid object.
    //
    int ERROR_OBJECT_NOT_FOUND = 4312;

    //
    // MessageId: ERROR_DATABASE_FAILURE
    //
    // MessageText:
    //
    // Unable to read from or write to the database.
    //
    int ERROR_DATABASE_FAILURE = 4313;

    //
    // MessageId: ERROR_DATABASE_FULL
    //
    // MessageText:
    //
    // The database is full.
    //
    int ERROR_DATABASE_FULL = 4314;

    //
    // MessageId: ERROR_MEDIA_INCOMPATIBLE
    //
    // MessageText:
    //
    // The medium is not compatible with the device or media pool.
    //
    int ERROR_MEDIA_INCOMPATIBLE = 4315;

    //
    // MessageId: ERROR_RESOURCE_NOT_PRESENT
    //
    // MessageText:
    //
    // The resource required for this operation does not exist.
    //
    int ERROR_RESOURCE_NOT_PRESENT = 4316;

    //
    // MessageId: ERROR_INVALID_OPERATION
    //
    // MessageText:
    //
    // The operation identifier is not valid.
    //
    int ERROR_INVALID_OPERATION = 4317;

    //
    // MessageId: ERROR_MEDIA_NOT_AVAILABLE
    //
    // MessageText:
    //
    // The media is not mounted or ready for use.
    //
    int ERROR_MEDIA_NOT_AVAILABLE = 4318;

    //
    // MessageId: ERROR_DEVICE_NOT_AVAILABLE
    //
    // MessageText:
    //
    // The device is not ready for use.
    //
    int ERROR_DEVICE_NOT_AVAILABLE = 4319;

    //
    // MessageId: ERROR_REQUEST_REFUSED
    //
    // MessageText:
    //
    // The operator or administrator has refused the request.
    //
    int ERROR_REQUEST_REFUSED = 4320;

    //
    // MessageId: ERROR_INVALID_DRIVE_OBJECT
    //
    // MessageText:
    //
    // The drive identifier does not represent a valid drive.
    //
    int ERROR_INVALID_DRIVE_OBJECT = 4321;

    //
    // MessageId: ERROR_LIBRARY_FULL
    //
    // MessageText:
    //
    // Library is full. No slot is available for use.
    //
    int ERROR_LIBRARY_FULL = 4322;

    //
    // MessageId: ERROR_MEDIUM_NOT_ACCESSIBLE
    //
    // MessageText:
    //
    // The transport cannot access the medium.
    //
    int ERROR_MEDIUM_NOT_ACCESSIBLE = 4323;

    //
    // MessageId: ERROR_UNABLE_TO_LOAD_MEDIUM
    //
    // MessageText:
    //
    // Unable to load the medium into the drive.
    //
    int ERROR_UNABLE_TO_LOAD_MEDIUM = 4324;

    //
    // MessageId: ERROR_UNABLE_TO_INVENTORY_DRIVE
    //
    // MessageText:
    //
    // Unable to retrieve the drive status.
    //
    int ERROR_UNABLE_TO_INVENTORY_DRIVE = 4325;

    //
    // MessageId: ERROR_UNABLE_TO_INVENTORY_SLOT
    //
    // MessageText:
    //
    // Unable to retrieve the slot status.
    //
    int ERROR_UNABLE_TO_INVENTORY_SLOT = 4326;

    //
    // MessageId: ERROR_UNABLE_TO_INVENTORY_TRANSPORT
    //
    // MessageText:
    //
    // Unable to retrieve status about the transport.
    //
    int ERROR_UNABLE_TO_INVENTORY_TRANSPORT = 4327;

    //
    // MessageId: ERROR_TRANSPORT_FULL
    //
    // MessageText:
    //
    // Cannot use the transport because it is already in use.
    //
    int ERROR_TRANSPORT_FULL = 4328;

    //
    // MessageId: ERROR_CONTROLLING_IEPORT
    //
    // MessageText:
    //
    // Unable to open or close the inject/eject port.
    //
    int ERROR_CONTROLLING_IEPORT = 4329;

    //
    // MessageId: ERROR_UNABLE_TO_EJECT_MOUNTED_MEDIA
    //
    // MessageText:
    //
    // Unable to eject the medium because it is in a drive.
    //
    int ERROR_UNABLE_TO_EJECT_MOUNTED_MEDIA = 4330;

    //
    // MessageId: ERROR_CLEANER_SLOT_SET
    //
    // MessageText:
    //
    // A cleaner slot is already reserved.
    //
    int ERROR_CLEANER_SLOT_SET = 4331;

    //
    // MessageId: ERROR_CLEANER_SLOT_NOT_SET
    //
    // MessageText:
    //
    // A cleaner slot is not reserved.
    //
    int ERROR_CLEANER_SLOT_NOT_SET = 4332;

    //
    // MessageId: ERROR_CLEANER_CARTRIDGE_SPENT
    //
    // MessageText:
    //
    // The cleaner cartridge has performed the maximum number of drive
    // cleanings.
    //
    int ERROR_CLEANER_CARTRIDGE_SPENT = 4333;

    //
    // MessageId: ERROR_UNEXPECTED_OMID
    //
    // MessageText:
    //
    // Unexpected on-medium identifier.
    //
    int ERROR_UNEXPECTED_OMID = 4334;

    //
    // MessageId: ERROR_CANT_DELETE_LAST_ITEM
    //
    // MessageText:
    //
    // The last remaining item in this group or resource cannot be deleted.
    //
    int ERROR_CANT_DELETE_LAST_ITEM = 4335;

    //
    // MessageId: ERROR_MESSAGE_EXCEEDS_MAX_SIZE
    //
    // MessageText:
    //
    // The message provided exceeds the maximum size allowed for this parameter.
    //
    int ERROR_MESSAGE_EXCEEDS_MAX_SIZE = 4336;

    //
    // MessageId: ERROR_VOLUME_CONTAINS_SYS_FILES
    //
    // MessageText:
    //
    // The volume contains system or paging files.
    //
    int ERROR_VOLUME_CONTAINS_SYS_FILES = 4337;

    //
    // MessageId: ERROR_INDIGENOUS_TYPE
    //
    // MessageText:
    //
    // The media type cannot be removed from this library since at least one
    // drive in the library reports it can support this media type.
    //
    int ERROR_INDIGENOUS_TYPE = 4338;

    //
    // MessageId: ERROR_NO_SUPPORTING_DRIVES
    //
    // MessageText:
    //
    // This offline media cannot be mounted on this system since no enabled
    // drives are present which can be used.
    //
    int ERROR_NO_SUPPORTING_DRIVES = 4339;

    //
    // MessageId: ERROR_CLEANER_CARTRIDGE_INSTALLED
    //
    // MessageText:
    //
    // A cleaner cartridge is present in the tape library.
    //
    int ERROR_CLEANER_CARTRIDGE_INSTALLED = 4340;

    //
    // MessageId: ERROR_IEPORT_FULL
    //
    // MessageText:
    //
    // Cannot use the ieport because it is not empty.
    //
    int ERROR_IEPORT_FULL = 4341;

    // /////////////////////////////////////////////////
    // =//
    // Remote Storage Service Error codes //
    // =//
    // =4350 to=4389=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_FILE_OFFLINE
    //
    // MessageText:
    //
    // This file is currently not available for use on this computer.
    //
    int ERROR_FILE_OFFLINE = 4350;

    //
    // MessageId: ERROR_REMOTE_STORAGE_NOT_ACTIVE
    //
    // MessageText:
    //
    // The remote storage service is not operational at this time.
    //
    int ERROR_REMOTE_STORAGE_NOT_ACTIVE = 4351;

    //
    // MessageId: ERROR_REMOTE_STORAGE_MEDIA_ERROR
    //
    // MessageText:
    //
    // The remote storage service encountered a media error.
    //
    int ERROR_REMOTE_STORAGE_MEDIA_ERROR = 4352;

    // /////////////////////////////////////////////////
    // =//
    // =Reparse Point Error codes=//
    // =//
    // =4390 to=4399=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_NOT_A_REPARSE_POINT
    //
    // MessageText:
    //
    // The file or directory is not a reparse point.
    //
    int ERROR_NOT_A_REPARSE_POINT = 4390;

    //
    // MessageId: ERROR_REPARSE_ATTRIBUTE_CONFLICT
    //
    // MessageText:
    //
    // The reparse point attribute cannot be set because it conflicts with an
    // existing attribute.
    //
    int ERROR_REPARSE_ATTRIBUTE_CONFLICT = 4391;

    //
    // MessageId: ERROR_INVALID_REPARSE_DATA
    //
    // MessageText:
    //
    // The data present in the reparse point buffer is invalid.
    //
    int ERROR_INVALID_REPARSE_DATA = 4392;

    //
    // MessageId: ERROR_REPARSE_TAG_INVALID
    //
    // MessageText:
    //
    // The tag present in the reparse point buffer is invalid.
    //
    int ERROR_REPARSE_TAG_INVALID = 4393;

    //
    // MessageId: ERROR_REPARSE_TAG_MISMATCH
    //
    // MessageText:
    //
    // There is a mismatch between the tag specified in the request and the tag
    // present in the reparse point.
    // 
    //
    int ERROR_REPARSE_TAG_MISMATCH = 4394;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =4400 to=4499=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // Single Instance Store (SIS) Error codes //
    // =//
    // =4500 to=4549=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_VOLUME_NOT_SIS_ENABLED
    //
    // MessageText:
    //
    // Single Instance Storage is not available on this volume.
    //
    int ERROR_VOLUME_NOT_SIS_ENABLED = 4500;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =4550 to=4599=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =Cluster Error codes=//
    // =//
    // =5000 to=5999=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_DEPENDENT_RESOURCE_EXISTS
    //
    // MessageText:
    //
    // The operation cannot be completed because other resources are dependent
    // on this resource.
    //
    int ERROR_DEPENDENT_RESOURCE_EXISTS = 5001;

    //
    // MessageId: ERROR_DEPENDENCY_NOT_FOUND
    //
    // MessageText:
    //
    // The cluster resource dependency cannot be found.
    //
    int ERROR_DEPENDENCY_NOT_FOUND = 5002;

    //
    // MessageId: ERROR_DEPENDENCY_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The cluster resource cannot be made dependent on the specified resource
    // because it is already dependent.
    //
    int ERROR_DEPENDENCY_ALREADY_EXISTS = 5003;

    //
    // MessageId: ERROR_RESOURCE_NOT_ONLINE
    //
    // MessageText:
    //
    // The cluster resource is not online.
    //
    int ERROR_RESOURCE_NOT_ONLINE = 5004;

    //
    // MessageId: ERROR_HOST_NODE_NOT_AVAILABLE
    //
    // MessageText:
    //
    // A cluster node is not available for this operation.
    //
    int ERROR_HOST_NODE_NOT_AVAILABLE = 5005;

    //
    // MessageId: ERROR_RESOURCE_NOT_AVAILABLE
    //
    // MessageText:
    //
    // The cluster resource is not available.
    //
    int ERROR_RESOURCE_NOT_AVAILABLE = 5006;

    //
    // MessageId: ERROR_RESOURCE_NOT_FOUND
    //
    // MessageText:
    //
    // The cluster resource could not be found.
    //
    int ERROR_RESOURCE_NOT_FOUND = 5007;

    //
    // MessageId: ERROR_SHUTDOWN_CLUSTER
    //
    // MessageText:
    //
    // The cluster is being shut down.
    //
    int ERROR_SHUTDOWN_CLUSTER = 5008;

    //
    // MessageId: ERROR_CANT_EVICT_ACTIVE_NODE
    //
    // MessageText:
    //
    // A cluster node cannot be evicted from the cluster unless the node is down
    // or it is the last node.
    //
    int ERROR_CANT_EVICT_ACTIVE_NODE = 5009;

    //
    // MessageId: ERROR_OBJECT_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The object already exists.
    //
    int ERROR_OBJECT_ALREADY_EXISTS = 5010;

    //
    // MessageId: ERROR_OBJECT_IN_LIST
    //
    // MessageText:
    //
    // The object is already in the list.
    //
    int ERROR_OBJECT_IN_LIST = 5011;

    //
    // MessageId: ERROR_GROUP_NOT_AVAILABLE
    //
    // MessageText:
    //
    // The cluster group is not available for any new requests.
    //
    int ERROR_GROUP_NOT_AVAILABLE = 5012;

    //
    // MessageId: ERROR_GROUP_NOT_FOUND
    //
    // MessageText:
    //
    // The cluster group could not be found.
    //
    int ERROR_GROUP_NOT_FOUND = 5013;

    //
    // MessageId: ERROR_GROUP_NOT_ONLINE
    //
    // MessageText:
    //
    // The operation could not be completed because the cluster group is not
    // online.
    //
    int ERROR_GROUP_NOT_ONLINE = 5014;

    //
    // MessageId: ERROR_HOST_NODE_NOT_RESOURCE_OWNER
    //
    // MessageText:
    //
    // The operation failed because either the specified cluster node is not the
    // owner of the resource, or the node
    // is not a possible owner of the resource.
    //
    int ERROR_HOST_NODE_NOT_RESOURCE_OWNER = 5015;

    //
    // MessageId: ERROR_HOST_NODE_NOT_GROUP_OWNER
    //
    // MessageText:
    //
    // The operation failed because either the specified cluster node is not the
    // owner of the group, or the node
    // is not a possible owner of the group.
    //
    int ERROR_HOST_NODE_NOT_GROUP_OWNER = 5016;

    //
    // MessageId: ERROR_RESMON_CREATE_FAILED
    //
    // MessageText:
    //
    // The cluster resource could not be created in the specified resource
    // monitor.
    //
    int ERROR_RESMON_CREATE_FAILED = 5017;

    //
    // MessageId: ERROR_RESMON_ONLINE_FAILED
    //
    // MessageText:
    //
    // The cluster resource could not be brought online by the resource monitor.
    //
    int ERROR_RESMON_ONLINE_FAILED = 5018;

    //
    // MessageId: ERROR_RESOURCE_ONLINE
    //
    // MessageText:
    //
    // The operation could not be completed because the cluster resource is
    // online.
    //
    int ERROR_RESOURCE_ONLINE = 5019;

    //
    // MessageId: ERROR_QUORUM_RESOURCE
    //
    // MessageText:
    //
    // The cluster resource could not be deleted or brought offline because it
    // is the quorum resource.
    //
    int ERROR_QUORUM_RESOURCE = 5020;

    //
    // MessageId: ERROR_NOT_QUORUM_CAPABLE
    //
    // MessageText:
    //
    // The cluster could not make the specified resource a quorum resource
    // because it is not capable of being a quorum resource.
    //
    int ERROR_NOT_QUORUM_CAPABLE = 5021;

    //
    // MessageId: ERROR_CLUSTER_SHUTTING_DOWN
    //
    // MessageText:
    //
    // The cluster software is shutting down.
    //
    int ERROR_CLUSTER_SHUTTING_DOWN = 5022;

    //
    // MessageId: ERROR_INVALID_STATE
    //
    // MessageText:
    //
    // The group or resource is not in the correct state to perform the
    // requested operation.
    //
    int ERROR_INVALID_STATE = 5023;

    //
    // MessageId: ERROR_RESOURCE_PROPERTIES_STORED
    //
    // MessageText:
    //
    // The properties were stored but not all changes will take effect until the
    // next time the resource is brought online.
    //
    int ERROR_RESOURCE_PROPERTIES_STORED = 5024;

    //
    // MessageId: ERROR_NOT_QUORUM_CLASS
    //
    // MessageText:
    //
    // The cluster could not make the specified resource a quorum resource
    // because it does not belong to a shared storage class.
    //
    int ERROR_NOT_QUORUM_CLASS = 5025;

    //
    // MessageId: ERROR_CORE_RESOURCE
    //
    // MessageText:
    //
    // The cluster resource could not be deleted since it is a core resource.
    //
    int ERROR_CORE_RESOURCE = 5026;

    //
    // MessageId: ERROR_QUORUM_RESOURCE_ONLINE_FAILED
    //
    // MessageText:
    //
    // The quorum resource failed to come online.
    //
    int ERROR_QUORUM_RESOURCE_ONLINE_FAILED = 5027;

    //
    // MessageId: ERROR_QUORUMLOG_OPEN_FAILED
    //
    // MessageText:
    //
    // The quorum log could not be created or mounted successfully.
    //
    int ERROR_QUORUMLOG_OPEN_FAILED = 5028;

    //
    // MessageId: ERROR_CLUSTERLOG_CORRUPT
    //
    // MessageText:
    //
    // The cluster log is corrupt.
    //
    int ERROR_CLUSTERLOG_CORRUPT = 5029;

    //
    // MessageId: ERROR_CLUSTERLOG_RECORD_EXCEEDS_MAXSIZE
    //
    // MessageText:
    //
    // The record could not be written to the cluster log since it exceeds the
    // maximum size.
    //
    int ERROR_CLUSTERLOG_RECORD_EXCEEDS_MAXSIZE = 5030;

    //
    // MessageId: ERROR_CLUSTERLOG_EXCEEDS_MAXSIZE
    //
    // MessageText:
    //
    // The cluster log exceeds its maximum size.
    //
    int ERROR_CLUSTERLOG_EXCEEDS_MAXSIZE = 5031;

    //
    // MessageId: ERROR_CLUSTERLOG_CHKPOINT_NOT_FOUND
    //
    // MessageText:
    //
    // No checkpoint record was found in the cluster log.
    //
    int ERROR_CLUSTERLOG_CHKPOINT_NOT_FOUND = 5032;

    //
    // MessageId: ERROR_CLUSTERLOG_NOT_ENOUGH_SPACE
    //
    // MessageText:
    //
    // The minimum required disk space needed for logging is not available.
    //
    int ERROR_CLUSTERLOG_NOT_ENOUGH_SPACE = 5033;

    //
    // MessageId: ERROR_QUORUM_OWNER_ALIVE
    //
    // MessageText:
    //
    // The cluster node failed to take control of the quorum resource because
    // the resource is owned by another active node.
    //
    int ERROR_QUORUM_OWNER_ALIVE = 5034;

    //
    // MessageId: ERROR_NETWORK_NOT_AVAILABLE
    //
    // MessageText:
    //
    // A cluster network is not available for this operation.
    //
    int ERROR_NETWORK_NOT_AVAILABLE = 5035;

    //
    // MessageId: ERROR_NODE_NOT_AVAILABLE
    //
    // MessageText:
    //
    // A cluster node is not available for this operation.
    //
    int ERROR_NODE_NOT_AVAILABLE = 5036;

    //
    // MessageId: ERROR_ALL_NODES_NOT_AVAILABLE
    //
    // MessageText:
    //
    // All cluster nodes must be running to perform this operation.
    //
    int ERROR_ALL_NODES_NOT_AVAILABLE = 5037;

    //
    // MessageId: ERROR_RESOURCE_FAILED
    //
    // MessageText:
    //
    // A cluster resource failed.
    //
    int ERROR_RESOURCE_FAILED = 5038;

    //
    // MessageId: ERROR_CLUSTER_INVALID_NODE
    //
    // MessageText:
    //
    // The cluster node is not valid.
    //
    int ERROR_CLUSTER_INVALID_NODE = 5039;

    //
    // MessageId: ERROR_CLUSTER_NODE_EXISTS
    //
    // MessageText:
    //
    // The cluster node already exists.
    //
    int ERROR_CLUSTER_NODE_EXISTS = 5040;

    //
    // MessageId: ERROR_CLUSTER_JOIN_IN_PROGRESS
    //
    // MessageText:
    //
    // A node is in the process of joining the cluster.
    //
    int ERROR_CLUSTER_JOIN_IN_PROGRESS = 5041;

    //
    // MessageId: ERROR_CLUSTER_NODE_NOT_FOUND
    //
    // MessageText:
    //
    // The cluster node was not found.
    //
    int ERROR_CLUSTER_NODE_NOT_FOUND = 5042;

    //
    // MessageId: ERROR_CLUSTER_LOCAL_NODE_NOT_FOUND
    //
    // MessageText:
    //
    // The cluster local node information was not found.
    //
    int ERROR_CLUSTER_LOCAL_NODE_NOT_FOUND = 5043;

    //
    // MessageId: ERROR_CLUSTER_NETWORK_EXISTS
    //
    // MessageText:
    //
    // The cluster network already exists.
    //
    int ERROR_CLUSTER_NETWORK_EXISTS = 5044;

    //
    // MessageId: ERROR_CLUSTER_NETWORK_NOT_FOUND
    //
    // MessageText:
    //
    // The cluster network was not found.
    //
    int ERROR_CLUSTER_NETWORK_NOT_FOUND = 5045;

    //
    // MessageId: ERROR_CLUSTER_NETINTERFACE_EXISTS
    //
    // MessageText:
    //
    // The cluster network interface already exists.
    //
    int ERROR_CLUSTER_NETINTERFACE_EXISTS = 5046;

    //
    // MessageId: ERROR_CLUSTER_NETINTERFACE_NOT_FOUND
    //
    // MessageText:
    //
    // The cluster network interface was not found.
    //
    int ERROR_CLUSTER_NETINTERFACE_NOT_FOUND = 5047;

    //
    // MessageId: ERROR_CLUSTER_INVALID_REQUEST
    //
    // MessageText:
    //
    // The cluster request is not valid for this object.
    //
    int ERROR_CLUSTER_INVALID_REQUEST = 5048;

    //
    // MessageId: ERROR_CLUSTER_INVALID_NETWORK_PROVIDER
    //
    // MessageText:
    //
    // The cluster network provider is not valid.
    //
    int ERROR_CLUSTER_INVALID_NETWORK_PROVIDER = 5049;

    //
    // MessageId: ERROR_CLUSTER_NODE_DOWN
    //
    // MessageText:
    //
    // The cluster node is down.
    //
    int ERROR_CLUSTER_NODE_DOWN = 5050;

    //
    // MessageId: ERROR_CLUSTER_NODE_UNREACHABLE
    //
    // MessageText:
    //
    // The cluster node is not reachable.
    //
    int ERROR_CLUSTER_NODE_UNREACHABLE = 5051;

    //
    // MessageId: ERROR_CLUSTER_NODE_NOT_MEMBER
    //
    // MessageText:
    //
    // The cluster node is not a member of the cluster.
    //
    int ERROR_CLUSTER_NODE_NOT_MEMBER = 5052;

    //
    // MessageId: ERROR_CLUSTER_JOIN_NOT_IN_PROGRESS
    //
    // MessageText:
    //
    // A cluster join operation is not in progress.
    //
    int ERROR_CLUSTER_JOIN_NOT_IN_PROGRESS = 5053;

    //
    // MessageId: ERROR_CLUSTER_INVALID_NETWORK
    //
    // MessageText:
    //
    // The cluster network is not valid.
    //
    int ERROR_CLUSTER_INVALID_NETWORK = 5054;

    //
    // MessageId: ERROR_CLUSTER_NODE_UP
    //
    // MessageText:
    //
    // The cluster node is up.
    //
    int ERROR_CLUSTER_NODE_UP = 5056;

    //
    // MessageId: ERROR_CLUSTER_IPADDR_IN_USE
    //
    // MessageText:
    //
    // The cluster IP address is already in use.
    //
    int ERROR_CLUSTER_IPADDR_IN_USE = 5057;

    //
    // MessageId: ERROR_CLUSTER_NODE_NOT_PAUSED
    //
    // MessageText:
    //
    // The cluster node is not paused.
    //
    int ERROR_CLUSTER_NODE_NOT_PAUSED = 5058;

    //
    // MessageId: ERROR_CLUSTER_NO_SECURITY_CONTEXT
    //
    // MessageText:
    //
    // No cluster security context is available.
    //
    int ERROR_CLUSTER_NO_SECURITY_CONTEXT = 5059;

    //
    // MessageId: ERROR_CLUSTER_NETWORK_NOT_INTERNAL
    //
    // MessageText:
    //
    // The cluster network is not configured for internal cluster communication.
    //
    int ERROR_CLUSTER_NETWORK_NOT_INTERNAL = 5060;

    //
    // MessageId: ERROR_CLUSTER_NODE_ALREADY_UP
    //
    // MessageText:
    //
    // The cluster node is already up.
    //
    int ERROR_CLUSTER_NODE_ALREADY_UP = 5061;

    //
    // MessageId: ERROR_CLUSTER_NODE_ALREADY_DOWN
    //
    // MessageText:
    //
    // The cluster node is already down.
    //
    int ERROR_CLUSTER_NODE_ALREADY_DOWN = 5062;

    //
    // MessageId: ERROR_CLUSTER_NETWORK_ALREADY_ONLINE
    //
    // MessageText:
    //
    // The cluster network is already online.
    //
    int ERROR_CLUSTER_NETWORK_ALREADY_ONLINE = 5063;

    //
    // MessageId: ERROR_CLUSTER_NETWORK_ALREADY_OFFLINE
    //
    // MessageText:
    //
    // The cluster network is already offline.
    //
    int ERROR_CLUSTER_NETWORK_ALREADY_OFFLINE = 5064;

    //
    // MessageId: ERROR_CLUSTER_NODE_ALREADY_MEMBER
    //
    // MessageText:
    //
    // The cluster node is already a member of the cluster.
    //
    int ERROR_CLUSTER_NODE_ALREADY_MEMBER = 5065;

    //
    // MessageId: ERROR_CLUSTER_LAST_INTERNAL_NETWORK
    //
    // MessageText:
    //
    // The cluster network is the only one configured for internal cluster
    // communication between two or more active cluster nodes. The internal
    // communication capability cannot be removed from the network.
    //
    int ERROR_CLUSTER_LAST_INTERNAL_NETWORK = 5066;

    //
    // MessageId: ERROR_CLUSTER_NETWORK_HAS_DEPENDENTS
    //
    // MessageText:
    //
    // One or more cluster resources depend on the network to provide service to
    // clients. The client access capability cannot be removed from the network.
    //
    int ERROR_CLUSTER_NETWORK_HAS_DEPENDENTS = 5067;

    //
    // MessageId: ERROR_INVALID_OPERATION_ON_QUORUM
    //
    // MessageText:
    //
    // This operation cannot be performed on the cluster resource as it the
    // quorum resource. You may not bring the quorum resource offline or modify
    // its possible owners list.
    //
    int ERROR_INVALID_OPERATION_ON_QUORUM = 5068;

    //
    // MessageId: ERROR_DEPENDENCY_NOT_ALLOWED
    //
    // MessageText:
    //
    // The cluster quorum resource is not allowed to have any dependencies.
    //
    int ERROR_DEPENDENCY_NOT_ALLOWED = 5069;

    //
    // MessageId: ERROR_CLUSTER_NODE_PAUSED
    //
    // MessageText:
    //
    // The cluster node is paused.
    //
    int ERROR_CLUSTER_NODE_PAUSED = 5070;

    //
    // MessageId: ERROR_NODE_CANT_HOST_RESOURCE
    //
    // MessageText:
    //
    // The cluster resource cannot be brought online. The owner node cannot run
    // this resource.
    //
    int ERROR_NODE_CANT_HOST_RESOURCE = 5071;

    //
    // MessageId: ERROR_CLUSTER_NODE_NOT_READY
    //
    // MessageText:
    //
    // The cluster node is not ready to perform the requested operation.
    //
    int ERROR_CLUSTER_NODE_NOT_READY = 5072;

    //
    // MessageId: ERROR_CLUSTER_NODE_SHUTTING_DOWN
    //
    // MessageText:
    //
    // The cluster node is shutting down.
    //
    int ERROR_CLUSTER_NODE_SHUTTING_DOWN = 5073;

    //
    // MessageId: ERROR_CLUSTER_JOIN_ABORTED
    //
    // MessageText:
    //
    // The cluster join operation was aborted.
    //
    int ERROR_CLUSTER_JOIN_ABORTED = 5074;

    //
    // MessageId: ERROR_CLUSTER_INCOMPATIBLE_VERSIONS
    //
    // MessageText:
    //
    // The cluster join operation failed due to incompatible software versions
    // between the joining node and its sponsor.
    //
    int ERROR_CLUSTER_INCOMPATIBLE_VERSIONS = 5075;

    //
    // MessageId: ERROR_CLUSTER_MAXNUM_OF_RESOURCES_EXCEEDED
    //
    // MessageText:
    //
    // This resource cannot be created because the cluster has reached the limit
    // on the number of resources it can monitor.
    //
    int ERROR_CLUSTER_MAXNUM_OF_RESOURCES_EXCEEDED = 5076;

    //
    // MessageId: ERROR_CLUSTER_SYSTEM_CONFIG_CHANGED
    //
    // MessageText:
    //
    // The system configuration changed during the cluster join or form
    // operation. The join or form operation was aborted.
    //
    int ERROR_CLUSTER_SYSTEM_CONFIG_CHANGED = 5077;

    //
    // MessageId: ERROR_CLUSTER_RESOURCE_TYPE_NOT_FOUND
    //
    // MessageText:
    //
    // The specified resource type was not found.
    //
    int ERROR_CLUSTER_RESOURCE_TYPE_NOT_FOUND = 5078;

    //
    // MessageId: ERROR_CLUSTER_RESTYPE_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The specified node does not support a resource of this type. This may be
    // due to version inconsistencies or due to the absence of the resource DLL
    // on this node.
    //
    int ERROR_CLUSTER_RESTYPE_NOT_SUPPORTED = 5079;

    //
    // MessageId: ERROR_CLUSTER_RESNAME_NOT_FOUND
    //
    // MessageText:
    //
    // The specified resource name is not supported by this resource DLL. This
    // may be due to a bad (or changed) name supplied to the resource DLL.
    //
    int ERROR_CLUSTER_RESNAME_NOT_FOUND = 5080;

    //
    // MessageId: ERROR_CLUSTER_NO_RPC_PACKAGES_REGISTERED
    //
    // MessageText:
    //
    // No authentication package could be registered with the RPC server.
    //
    int ERROR_CLUSTER_NO_RPC_PACKAGES_REGISTERED = 5081;

    //
    // MessageId: ERROR_CLUSTER_OWNER_NOT_IN_PREFLIST
    //
    // MessageText:
    //
    // You cannot bring the group online because the owner of the group is not
    // in the preferred list for the group. To change the owner node for the
    // group, move the group.
    //
    int ERROR_CLUSTER_OWNER_NOT_IN_PREFLIST = 5082;

    //
    // MessageId: ERROR_CLUSTER_DATABASE_SEQMISMATCH
    //
    // MessageText:
    //
    // The join operation failed because the cluster database sequence number
    // has changed or is incompatible with the locker node. This may happen
    // during a join operation if the cluster database was changing during the
    // join.
    //
    int ERROR_CLUSTER_DATABASE_SEQMISMATCH = 5083;

    //
    // MessageId: ERROR_RESMON_INVALID_STATE
    //
    // MessageText:
    //
    // The resource monitor will not allow the fail operation to be performed
    // while the resource is in its current state. This may happen if the
    // resource is in a pending state.
    //
    int ERROR_RESMON_INVALID_STATE = 5084;

    //
    // MessageId: ERROR_CLUSTER_GUM_NOT_LOCKER
    //
    // MessageText:
    //
    // A non locker code got a request to reserve the lock for making global
    // updates.
    //
    int ERROR_CLUSTER_GUM_NOT_LOCKER = 5085;

    //
    // MessageId: ERROR_QUORUM_DISK_NOT_FOUND
    //
    // MessageText:
    //
    // The quorum disk could not be located by the cluster service.
    //
    int ERROR_QUORUM_DISK_NOT_FOUND = 5086;

    //
    // MessageId: ERROR_DATABASE_BACKUP_CORRUPT
    //
    // MessageText:
    //
    // The backed up cluster database is possibly corrupt.
    //
    int ERROR_DATABASE_BACKUP_CORRUPT = 5087;

    //
    // MessageId: ERROR_CLUSTER_NODE_ALREADY_HAS_DFS_ROOT
    //
    // MessageText:
    //
    // A DFS root already exists in this cluster node.
    //
    int ERROR_CLUSTER_NODE_ALREADY_HAS_DFS_ROOT = 5088;

    //
    // MessageId: ERROR_RESOURCE_PROPERTY_UNCHANGEABLE
    //
    // MessageText:
    //
    // An attempt to modify a resource property failed because it conflicts with
    // another existing property.
    //
    int ERROR_RESOURCE_PROPERTY_UNCHANGEABLE = 5089;

    /*
     * Codes from=4300 through=5889 overlap with codes in
     * ds\published\inc\apperr2.w. Do not add any more error codes in that
     * range.
     */
    //
    // MessageId: ERROR_CLUSTER_MEMBERSHIP_INVALID_STATE
    //
    // MessageText:
    //
    // An operation was attempted that is incompatible with the current
    // membership state of the node.
    //
    int ERROR_CLUSTER_MEMBERSHIP_INVALID_STATE = 5890;

    //
    // MessageId: ERROR_CLUSTER_QUORUMLOG_NOT_FOUND
    //
    // MessageText:
    //
    // The quorum resource does not contain the quorum log.
    //
    int ERROR_CLUSTER_QUORUMLOG_NOT_FOUND = 5891;

    //
    // MessageId: ERROR_CLUSTER_MEMBERSHIP_HALT
    //
    // MessageText:
    //
    // The membership engine requested shutdown of the cluster service on this
    // node.
    //
    int ERROR_CLUSTER_MEMBERSHIP_HALT = 5892;

    //
    // MessageId: ERROR_CLUSTER_INSTANCE_ID_MISMATCH
    //
    // MessageText:
    //
    // The join operation failed because the cluster instance ID of the joining
    // node does not match the cluster instance ID of the sponsor node.
    //
    int ERROR_CLUSTER_INSTANCE_ID_MISMATCH = 5893;

    //
    // MessageId: ERROR_CLUSTER_NETWORK_NOT_FOUND_FOR_IP
    //
    // MessageText:
    //
    // A matching cluster network for the specified IP address could not be
    // found.
    //
    int ERROR_CLUSTER_NETWORK_NOT_FOUND_FOR_IP = 5894;

    //
    // MessageId: ERROR_CLUSTER_PROPERTY_DATA_TYPE_MISMATCH
    //
    // MessageText:
    //
    // The actual data type of the property did not match the expected data type
    // of the property.
    //
    int ERROR_CLUSTER_PROPERTY_DATA_TYPE_MISMATCH = 5895;

    //
    // MessageId: ERROR_CLUSTER_EVICT_WITHOUT_CLEANUP
    //
    // MessageText:
    //
    // The cluster node was evicted from the cluster successfully, but the node
    // was not cleaned up. To determine what cleanup steps failed and how to
    // recover, see the Failover Clustering application event log using Event
    // Viewer.
    //
    int ERROR_CLUSTER_EVICT_WITHOUT_CLEANUP = 5896;

    //
    // MessageId: ERROR_CLUSTER_PARAMETER_MISMATCH
    //
    // MessageText:
    //
    // Two or more parameter values specified for a resource's properties are in
    // conflict.
    //
    int ERROR_CLUSTER_PARAMETER_MISMATCH = 5897;

    //
    // MessageId: ERROR_NODE_CANNOT_BE_CLUSTERED
    //
    // MessageText:
    //
    // This computer cannot be made a member of a cluster.
    //
    int ERROR_NODE_CANNOT_BE_CLUSTERED = 5898;

    //
    // MessageId: ERROR_CLUSTER_WRONG_OS_VERSION
    //
    // MessageText:
    //
    // This computer cannot be made a member of a cluster because it does not
    // have the correct version of Windows installed.
    //
    int ERROR_CLUSTER_WRONG_OS_VERSION = 5899;

    //
    // MessageId: ERROR_CLUSTER_CANT_CREATE_DUP_CLUSTER_NAME
    //
    // MessageText:
    //
    // A cluster cannot be created with the specified cluster name because that
    // cluster name is already in use. Specify a different name for the cluster.
    //
    int ERROR_CLUSTER_CANT_CREATE_DUP_CLUSTER_NAME = 5900;

    //
    // MessageId: ERROR_CLUSCFG_ALREADY_COMMITTED
    //
    // MessageText:
    //
    // The cluster configuration action has already been committed.
    //
    int ERROR_CLUSCFG_ALREADY_COMMITTED = 5901;

    //
    // MessageId: ERROR_CLUSCFG_ROLLBACK_FAILED
    //
    // MessageText:
    //
    // The cluster configuration action could not be rolled back.
    //
    int ERROR_CLUSCFG_ROLLBACK_FAILED = 5902;

    //
    // MessageId: ERROR_CLUSCFG_SYSTEM_DISK_DRIVE_LETTER_CONFLICT
    //
    // MessageText:
    //
    // The drive letter assigned to a system disk on one node conflicted with
    // the drive letter assigned to a disk on another node.
    //
    int ERROR_CLUSCFG_SYSTEM_DISK_DRIVE_LETTER_CONFLICT = 5903;

    //
    // MessageId: ERROR_CLUSTER_OLD_VERSION
    //
    // MessageText:
    //
    // One or more nodes in the cluster are running a version of Windows that
    // does not support this operation.
    //
    int ERROR_CLUSTER_OLD_VERSION = 5904;

    //
    // MessageId: ERROR_CLUSTER_MISMATCHED_COMPUTER_ACCT_NAME
    //
    // MessageText:
    //
    // The name of the corresponding computer account doesn't match the Network
    // Name for this resource.
    //
    int ERROR_CLUSTER_MISMATCHED_COMPUTER_ACCT_NAME = 5905;

    //
    // MessageId: ERROR_CLUSTER_NO_NET_ADAPTERS
    //
    // MessageText:
    //
    // No network adapters are available.
    //
    int ERROR_CLUSTER_NO_NET_ADAPTERS = 5906;

    //
    // MessageId: ERROR_CLUSTER_POISONED
    //
    // MessageText:
    //
    // The cluster node has been poisoned.
    //
    int ERROR_CLUSTER_POISONED = 5907;

    //
    // MessageId: ERROR_CLUSTER_GROUP_MOVING
    //
    // MessageText:
    //
    // The group is unable to accept the request since it is moving to another
    // node.
    //
    int ERROR_CLUSTER_GROUP_MOVING = 5908;

    //
    // MessageId: ERROR_CLUSTER_RESOURCE_TYPE_BUSY
    //
    // MessageText:
    //
    // The resource type cannot accept the request since is too busy performing
    // another operation.
    //
    int ERROR_CLUSTER_RESOURCE_TYPE_BUSY = 5909;

    //
    // MessageId: ERROR_RESOURCE_CALL_TIMED_OUT
    //
    // MessageText:
    //
    // The call to the cluster resource DLL timed out.
    //
    int ERROR_RESOURCE_CALL_TIMED_OUT = 5910;

    //
    // MessageId: ERROR_INVALID_CLUSTER_IPV6_ADDRESS
    //
    // MessageText:
    //
    // The address is not valid for an IPv6 Address resource. A global IPv6
    // address is required, and it must match a cluster network. Compatibility
    // addresses are not permitted.
    //
    int ERROR_INVALID_CLUSTER_IPV6_ADDRESS = 5911;

    //
    // MessageId: ERROR_CLUSTER_INTERNAL_INVALID_FUNCTION
    //
    // MessageText:
    //
    // An internal cluster error occurred. A call to an invalid function was
    // attempted.
    //
    int ERROR_CLUSTER_INTERNAL_INVALID_FUNCTION = 5912;

    //
    // MessageId: ERROR_CLUSTER_PARAMETER_OUT_OF_BOUNDS
    //
    // MessageText:
    //
    // A parameter value is out of acceptable range.
    //
    int ERROR_CLUSTER_PARAMETER_OUT_OF_BOUNDS = 5913;

    //
    // MessageId: ERROR_CLUSTER_PARTIAL_SEND
    //
    // MessageText:
    //
    // A network error occurred while sending data to another node in the
    // cluster. The number of bytes transmitted was less than required.
    //
    int ERROR_CLUSTER_PARTIAL_SEND = 5914;

    //
    // MessageId: ERROR_CLUSTER_REGISTRY_INVALID_FUNCTION
    //
    // MessageText:
    //
    // An invalid cluster registry operation was attempted.
    //
    int ERROR_CLUSTER_REGISTRY_INVALID_FUNCTION = 5915;

    //
    // MessageId: ERROR_CLUSTER_INVALID_STRING_TERMINATION
    //
    // MessageText:
    //
    // An input string of characters is not properly terminated.
    //
    int ERROR_CLUSTER_INVALID_STRING_TERMINATION = 5916;

    //
    // MessageId: ERROR_CLUSTER_INVALID_STRING_FORMAT
    //
    // MessageText:
    //
    // An input string of characters is not in a valid format for the data it
    // represents.
    //
    int ERROR_CLUSTER_INVALID_STRING_FORMAT = 5917;

    //
    // MessageId: ERROR_CLUSTER_DATABASE_TRANSACTION_IN_PROGRESS
    //
    // MessageText:
    //
    // An internal cluster error occurred. A cluster database transaction was
    // attempted while a transaction was already in progress.
    //
    int ERROR_CLUSTER_DATABASE_TRANSACTION_IN_PROGRESS = 5918;

    //
    // MessageId: ERROR_CLUSTER_DATABASE_TRANSACTION_NOT_IN_PROGRESS
    //
    // MessageText:
    //
    // An internal cluster error occurred. There was an attempt to commit a
    // cluster database transaction while no transaction was in progress.
    //
    int ERROR_CLUSTER_DATABASE_TRANSACTION_NOT_IN_PROGRESS = 5919;

    //
    // MessageId: ERROR_CLUSTER_NULL_DATA
    //
    // MessageText:
    //
    // An internal cluster error occurred. Data was not properly initialized.
    //
    int ERROR_CLUSTER_NULL_DATA = 5920;

    //
    // MessageId: ERROR_CLUSTER_PARTIAL_READ
    //
    // MessageText:
    //
    // An error occurred while reading from a stream of data. An unexpected
    // number of bytes was returned.
    //
    int ERROR_CLUSTER_PARTIAL_READ = 5921;

    //
    // MessageId: ERROR_CLUSTER_PARTIAL_WRITE
    //
    // MessageText:
    //
    // An error occurred while writing to a stream of data. The required number
    // of bytes could not be written.
    //
    int ERROR_CLUSTER_PARTIAL_WRITE = 5922;

    //
    // MessageId: ERROR_CLUSTER_CANT_DESERIALIZE_DATA
    //
    // MessageText:
    //
    // An error occurred while deserializing a stream of cluster data.
    //
    int ERROR_CLUSTER_CANT_DESERIALIZE_DATA = 5923;

    //
    // MessageId: ERROR_DEPENDENT_RESOURCE_PROPERTY_CONFLICT
    //
    // MessageText:
    //
    // One or more property values for this resource are in conflict with one or
    // more property values associated with its dependent resource(s).
    //
    int ERROR_DEPENDENT_RESOURCE_PROPERTY_CONFLICT = 5924;

    //
    // MessageId: ERROR_CLUSTER_NO_QUORUM
    //
    // MessageText:
    //
    // A quorum of cluster nodes was not present to form a cluster.
    //
    int ERROR_CLUSTER_NO_QUORUM = 5925;

    //
    // MessageId: ERROR_CLUSTER_INVALID_IPV6_NETWORK
    //
    // MessageText:
    //
    // The cluster network is not valid for an IPv6 Address resource, or it does
    // not match the configured address.
    //
    int ERROR_CLUSTER_INVALID_IPV6_NETWORK = 5926;

    //
    // MessageId: ERROR_CLUSTER_INVALID_IPV6_TUNNEL_NETWORK
    //
    // MessageText:
    //
    // The cluster network is not valid for an IPv6 Tunnel resource. Check the
    // configuration of the IP Address resource on which the IPv6 Tunnel
    // resource depends.
    //
    int ERROR_CLUSTER_INVALID_IPV6_TUNNEL_NETWORK = 5927;

    //
    // MessageId: ERROR_QUORUM_NOT_ALLOWED_IN_THIS_GROUP
    //
    // MessageText:
    //
    // Quorum resource cannot reside in the Available Storage group.
    //
    int ERROR_QUORUM_NOT_ALLOWED_IN_THIS_GROUP = 5928;

    //
    // MessageId: ERROR_DEPENDENCY_TREE_TOO_COMPLEX
    //
    // MessageText:
    //
    // The dependencies for this resource are nested too deeply.
    //
    int ERROR_DEPENDENCY_TREE_TOO_COMPLEX = 5929;

    //
    // MessageId: ERROR_EXCEPTION_IN_RESOURCE_CALL
    //
    // MessageText:
    //
    // The call into the resource DLL raised an unhandled exception.
    //
    int ERROR_EXCEPTION_IN_RESOURCE_CALL = 5930;

    //
    // MessageId: ERROR_CLUSTER_RHS_FAILED_INITIALIZATION
    //
    // MessageText:
    //
    // The RHS process failed to initialize.
    //
    int ERROR_CLUSTER_RHS_FAILED_INITIALIZATION = 5931;

    //
    // MessageId: ERROR_CLUSTER_NOT_INSTALLED
    //
    // MessageText:
    //
    // The Failover Clustering feature is not installed on this node.
    //
    int ERROR_CLUSTER_NOT_INSTALLED = 5932;

    //
    // MessageId: ERROR_CLUSTER_RESOURCES_MUST_BE_ONLINE_ON_THE_SAME_NODE
    //
    // MessageText:
    //
    // The resources must be online on the same node for this operation
    //
    int ERROR_CLUSTER_RESOURCES_MUST_BE_ONLINE_ON_THE_SAME_NODE = 5933;

    // /////////////////////////////////////////////////
    // =//
    // =EFS Error codes=//
    // =//
    // =6000 to=6099=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_ENCRYPTION_FAILED
    //
    // MessageText:
    //
    // The specified file could not be encrypted.
    //
    int ERROR_ENCRYPTION_FAILED = 6000;

    //
    // MessageId: ERROR_DECRYPTION_FAILED
    //
    // MessageText:
    //
    // The specified file could not be decrypted.
    //
    int ERROR_DECRYPTION_FAILED = 6001;

    //
    // MessageId: ERROR_FILE_ENCRYPTED
    //
    // MessageText:
    //
    // The specified file is encrypted and the user does not have the ability to
    // decrypt it.
    //
    int ERROR_FILE_ENCRYPTED = 6002;

    //
    // MessageId: ERROR_NO_RECOVERY_POLICY
    //
    // MessageText:
    //
    // There is no valid encryption recovery policy configured for this system.
    //
    int ERROR_NO_RECOVERY_POLICY = 6003;

    //
    // MessageId: ERROR_NO_EFS
    //
    // MessageText:
    //
    // The required encryption driver is not loaded for this system.
    //
    int ERROR_NO_EFS = 6004;

    //
    // MessageId: ERROR_WRONG_EFS
    //
    // MessageText:
    //
    // The file was encrypted with a different encryption driver than is
    // currently loaded.
    //
    int ERROR_WRONG_EFS = 6005;

    //
    // MessageId: ERROR_NO_USER_KEYS
    //
    // MessageText:
    //
    // There are no EFS keys defined for the user.
    //
    int ERROR_NO_USER_KEYS = 6006;

    //
    // MessageId: ERROR_FILE_NOT_ENCRYPTED
    //
    // MessageText:
    //
    // The specified file is not encrypted.
    //
    int ERROR_FILE_NOT_ENCRYPTED = 6007;

    //
    // MessageId: ERROR_NOT_EXPORT_FORMAT
    //
    // MessageText:
    //
    // The specified file is not in the defined EFS export format.
    //
    int ERROR_NOT_EXPORT_FORMAT = 6008;

    //
    // MessageId: ERROR_FILE_READ_ONLY
    //
    // MessageText:
    //
    // The specified file is read only.
    //
    int ERROR_FILE_READ_ONLY = 6009;

    //
    // MessageId: ERROR_DIR_EFS_DISALLOWED
    //
    // MessageText:
    //
    // The directory has been disabled for encryption.
    //
    int ERROR_DIR_EFS_DISALLOWED = 6010;

    //
    // MessageId: ERROR_EFS_SERVER_NOT_TRUSTED
    //
    // MessageText:
    //
    // The server is not trusted for remote encryption operation.
    //
    int ERROR_EFS_SERVER_NOT_TRUSTED = 6011;

    //
    // MessageId: ERROR_BAD_RECOVERY_POLICY
    //
    // MessageText:
    //
    // Recovery policy configured for this system contains invalid recovery
    // certificate.
    //
    int ERROR_BAD_RECOVERY_POLICY = 6012;

    //
    // MessageId: ERROR_EFS_ALG_BLOB_TOO_BIG
    //
    // MessageText:
    //
    // The encryption algorithm used on the source file needs a bigger key
    // buffer than the one on the destination file.
    //
    int ERROR_EFS_ALG_BLOB_TOO_BIG = 6013;

    //
    // MessageId: ERROR_VOLUME_NOT_SUPPORT_EFS
    //
    // MessageText:
    //
    // The disk partition does not support file encryption.
    //
    int ERROR_VOLUME_NOT_SUPPORT_EFS = 6014;

    //
    // MessageId: ERROR_EFS_DISABLED
    //
    // MessageText:
    //
    // This machine is disabled for file encryption.
    //
    int ERROR_EFS_DISABLED = 6015;

    //
    // MessageId: ERROR_EFS_VERSION_NOT_SUPPORT
    //
    // MessageText:
    //
    // A newer system is required to decrypt this encrypted file.
    //
    int ERROR_EFS_VERSION_NOT_SUPPORT = 6016;

    //
    // MessageId: ERROR_CS_ENCRYPTION_INVALID_SERVER_RESPONSE
    //
    // MessageText:
    //
    // The remote server sent an invalid response for a file being opened with
    // Client Side Encryption.
    //
    int ERROR_CS_ENCRYPTION_INVALID_SERVER_RESPONSE = 6017;

    //
    // MessageId: ERROR_CS_ENCRYPTION_UNSUPPORTED_SERVER
    //
    // MessageText:
    //
    // Client Side Encryption is not supported by the remote server even though
    // it claims to support it.
    //
    int ERROR_CS_ENCRYPTION_UNSUPPORTED_SERVER = 6018;

    //
    // MessageId: ERROR_CS_ENCRYPTION_EXISTING_ENCRYPTED_FILE
    //
    // MessageText:
    //
    // File is encrypted and should be opened in Client Side Encryption mode.
    //
    int ERROR_CS_ENCRYPTION_EXISTING_ENCRYPTED_FILE = 6019;

    //
    // MessageId: ERROR_CS_ENCRYPTION_NEW_ENCRYPTED_FILE
    //
    // MessageText:
    //
    // A new encrypted file is being created and a $EFS needs to be provided.
    //
    int ERROR_CS_ENCRYPTION_NEW_ENCRYPTED_FILE = 6020;

    //
    // MessageId: ERROR_CS_ENCRYPTION_FILE_NOT_CSE
    //
    // MessageText:
    //
    // The SMB client requested a CSE FSCTL on a non-CSE file.
    //
    int ERROR_CS_ENCRYPTION_FILE_NOT_CSE = 6021;

    // /////////////////////////////////////////////////
    // =//
    // =BROWSER Error codes=//
    // =//
    // =6100 to=6199=//
    // /////////////////////////////////////////////////

    // This message number is for historical purposes and cannot be changed or
    // re-used.
    //
    // MessageId: ERROR_NO_BROWSER_SERVERS_FOUND
    //
    // MessageText:
    //
    // The list of servers for this workgroup is not currently available
    //
    int ERROR_NO_BROWSER_SERVERS_FOUND = 6118;

    // /////////////////////////////////////////////////
    // =//
    // =Task Scheduler Error codes=//
    // =NET START must understand=//
    // =//
    // =6200 to=6249=//
    // /////////////////////////////////////////////////

    //
    // MessageId: SCHED_E_SERVICE_NOT_LOCALSYSTEM
    //
    // MessageText:
    //
    // The Task Scheduler service must be configured to run in the System
    // account to function properly. Individual tasks may be configured to run
    // in other accounts.
    //
    int SCHED_E_SERVICE_NOT_LOCALSYSTEM = 6200;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =6250 to=6599=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =Common Log (CLFS) Error codes=//
    // =//
    // =6600 to=6699=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_LOG_SECTOR_INVALID
    //
    // MessageText:
    //
    // Log service encountered an invalid log sector.
    //
    int ERROR_LOG_SECTOR_INVALID = 6600;

    //
    // MessageId: ERROR_LOG_SECTOR_PARITY_INVALID
    //
    // MessageText:
    //
    // Log service encountered a log sector with invalid block parity.
    //
    int ERROR_LOG_SECTOR_PARITY_INVALID = 6601;

    //
    // MessageId: ERROR_LOG_SECTOR_REMAPPED
    //
    // MessageText:
    //
    // Log service encountered a remapped log sector.
    //
    int ERROR_LOG_SECTOR_REMAPPED = 6602;

    //
    // MessageId: ERROR_LOG_BLOCK_INCOMPLETE
    //
    // MessageText:
    //
    // Log service encountered a partial or incomplete log block.
    //
    int ERROR_LOG_BLOCK_INCOMPLETE = 6603;

    //
    // MessageId: ERROR_LOG_INVALID_RANGE
    //
    // MessageText:
    //
    // Log service encountered an attempt access data outside the active log
    // range.
    //
    int ERROR_LOG_INVALID_RANGE = 6604;

    //
    // MessageId: ERROR_LOG_BLOCKS_EXHAUSTED
    //
    // MessageText:
    //
    // Log service user marshalling buffers are exhausted.
    //
    int ERROR_LOG_BLOCKS_EXHAUSTED = 6605;

    //
    // MessageId: ERROR_LOG_READ_CONTEXT_INVALID
    //
    // MessageText:
    //
    // Log service encountered an attempt read from a marshalling area with an
    // invalid read context.
    //
    int ERROR_LOG_READ_CONTEXT_INVALID = 6606;

    //
    // MessageId: ERROR_LOG_RESTART_INVALID
    //
    // MessageText:
    //
    // Log service encountered an invalid log restart area.
    //
    int ERROR_LOG_RESTART_INVALID = 6607;

    //
    // MessageId: ERROR_LOG_BLOCK_VERSION
    //
    // MessageText:
    //
    // Log service encountered an invalid log block version.
    //
    int ERROR_LOG_BLOCK_VERSION = 6608;

    //
    // MessageId: ERROR_LOG_BLOCK_INVALID
    //
    // MessageText:
    //
    // Log service encountered an invalid log block.
    //
    int ERROR_LOG_BLOCK_INVALID = 6609;

    //
    // MessageId: ERROR_LOG_READ_MODE_INVALID
    //
    // MessageText:
    //
    // Log service encountered an attempt to read the log with an invalid read
    // mode.
    //
    int ERROR_LOG_READ_MODE_INVALID = 6610;

    //
    // MessageId: ERROR_LOG_NO_RESTART
    //
    // MessageText:
    //
    // Log service encountered a log stream with no restart area.
    //
    int ERROR_LOG_NO_RESTART = 6611;

    //
    // MessageId: ERROR_LOG_METADATA_CORRUPT
    //
    // MessageText:
    //
    // Log service encountered a corrupted metadata file.
    //
    int ERROR_LOG_METADATA_CORRUPT = 6612;

    //
    // MessageId: ERROR_LOG_METADATA_INVALID
    //
    // MessageText:
    //
    // Log service encountered a metadata file that could not be created by the
    // log file system.
    //
    int ERROR_LOG_METADATA_INVALID = 6613;

    //
    // MessageId: ERROR_LOG_METADATA_INCONSISTENT
    //
    // MessageText:
    //
    // Log service encountered a metadata file with inconsistent data.
    //
    int ERROR_LOG_METADATA_INCONSISTENT = 6614;

    //
    // MessageId: ERROR_LOG_RESERVATION_INVALID
    //
    // MessageText:
    //
    // Log service encountered an attempt to erroneous allocate or dispose
    // reservation space.
    //
    int ERROR_LOG_RESERVATION_INVALID = 6615;

    //
    // MessageId: ERROR_LOG_CANT_DELETE
    //
    // MessageText:
    //
    // Log service cannot delete log file or file system container.
    //
    int ERROR_LOG_CANT_DELETE = 6616;

    //
    // MessageId: ERROR_LOG_CONTAINER_LIMIT_EXCEEDED
    //
    // MessageText:
    //
    // Log service has reached the maximum allowable containers allocated to a
    // log file.
    //
    int ERROR_LOG_CONTAINER_LIMIT_EXCEEDED = 6617;

    //
    // MessageId: ERROR_LOG_START_OF_LOG
    //
    // MessageText:
    //
    // Log service has attempted to read or write backward past the start of the
    // log.
    //
    int ERROR_LOG_START_OF_LOG = 6618;

    //
    // MessageId: ERROR_LOG_POLICY_ALREADY_INSTALLED
    //
    // MessageText:
    //
    // Log policy could not be installed because a policy of the same type is
    // already present.
    //
    int ERROR_LOG_POLICY_ALREADY_INSTALLED = 6619;

    //
    // MessageId: ERROR_LOG_POLICY_NOT_INSTALLED
    //
    // MessageText:
    //
    // Log policy in question was not installed at the time of the request.
    //
    int ERROR_LOG_POLICY_NOT_INSTALLED = 6620;

    //
    // MessageId: ERROR_LOG_POLICY_INVALID
    //
    // MessageText:
    //
    // The installed set of policies on the log is invalid.
    //
    int ERROR_LOG_POLICY_INVALID = 6621;

    //
    // MessageId: ERROR_LOG_POLICY_CONFLICT
    //
    // MessageText:
    //
    // A policy on the log in question prevented the operation from completing.
    //
    int ERROR_LOG_POLICY_CONFLICT = 6622;

    //
    // MessageId: ERROR_LOG_PINNED_ARCHIVE_TAIL
    //
    // MessageText:
    //
    // Log space cannot be reclaimed because the log is pinned by the archive
    // tail.
    //
    int ERROR_LOG_PINNED_ARCHIVE_TAIL = 6623;

    //
    // MessageId: ERROR_LOG_RECORD_NONEXISTENT
    //
    // MessageText:
    //
    // Log record is not a record in the log file.
    //
    int ERROR_LOG_RECORD_NONEXISTENT = 6624;

    //
    // MessageId: ERROR_LOG_RECORDS_RESERVED_INVALID
    //
    // MessageText:
    //
    // Number of reserved log records or the adjustment of the number of
    // reserved log records is invalid.
    //
    int ERROR_LOG_RECORDS_RESERVED_INVALID = 6625;

    //
    // MessageId: ERROR_LOG_SPACE_RESERVED_INVALID
    //
    // MessageText:
    //
    // Reserved log space or the adjustment of the log space is invalid.
    //
    int ERROR_LOG_SPACE_RESERVED_INVALID = 6626;

    //
    // MessageId: ERROR_LOG_TAIL_INVALID
    //
    // MessageText:
    //
    // An new or existing archive tail or base of the active log is invalid.
    //
    int ERROR_LOG_TAIL_INVALID = 6627;

    //
    // MessageId: ERROR_LOG_FULL
    //
    // MessageText:
    //
    // Log space is exhausted.
    //
    int ERROR_LOG_FULL = 6628;

    //
    // MessageId: ERROR_COULD_NOT_RESIZE_LOG
    //
    // MessageText:
    //
    // The log could not be set to the requested size.
    //
    int ERROR_COULD_NOT_RESIZE_LOG = 6629;

    //
    // MessageId: ERROR_LOG_MULTIPLEXED
    //
    // MessageText:
    //
    // Log is multiplexed, no direct writes to the physical log is allowed.
    //
    int ERROR_LOG_MULTIPLEXED = 6630;

    //
    // MessageId: ERROR_LOG_DEDICATED
    //
    // MessageText:
    //
    // The operation failed because the log is a dedicated log.
    //
    int ERROR_LOG_DEDICATED = 6631;

    //
    // MessageId: ERROR_LOG_ARCHIVE_NOT_IN_PROGRESS
    //
    // MessageText:
    //
    // The operation requires an archive context.
    //
    int ERROR_LOG_ARCHIVE_NOT_IN_PROGRESS = 6632;

    //
    // MessageId: ERROR_LOG_ARCHIVE_IN_PROGRESS
    //
    // MessageText:
    //
    // Log archival is in progress.
    //
    int ERROR_LOG_ARCHIVE_IN_PROGRESS = 6633;

    //
    // MessageId: ERROR_LOG_EPHEMERAL
    //
    // MessageText:
    //
    // The operation requires a non-ephemeral log, but the log is ephemeral.
    //
    int ERROR_LOG_EPHEMERAL = 6634;

    //
    // MessageId: ERROR_LOG_NOT_ENOUGH_CONTAINERS
    //
    // MessageText:
    //
    // The log must have at least two containers before it can be read from or
    // written to.
    //
    int ERROR_LOG_NOT_ENOUGH_CONTAINERS = 6635;

    //
    // MessageId: ERROR_LOG_CLIENT_ALREADY_REGISTERED
    //
    // MessageText:
    //
    // A log client has already registered on the stream.
    //
    int ERROR_LOG_CLIENT_ALREADY_REGISTERED = 6636;

    //
    // MessageId: ERROR_LOG_CLIENT_NOT_REGISTERED
    //
    // MessageText:
    //
    // A log client has not been registered on the stream.
    //
    int ERROR_LOG_CLIENT_NOT_REGISTERED = 6637;

    //
    // MessageId: ERROR_LOG_FULL_HANDLER_IN_PROGRESS
    //
    // MessageText:
    //
    // A request has already been made to handle the log full condition.
    //
    int ERROR_LOG_FULL_HANDLER_IN_PROGRESS = 6638;

    //
    // MessageId: ERROR_LOG_CONTAINER_READ_FAILED
    //
    // MessageText:
    //
    // Log service enountered an error when attempting to read from a log
    // container.
    //
    int ERROR_LOG_CONTAINER_READ_FAILED = 6639;

    //
    // MessageId: ERROR_LOG_CONTAINER_WRITE_FAILED
    //
    // MessageText:
    //
    // Log service enountered an error when attempting to write to a log
    // container.
    //
    int ERROR_LOG_CONTAINER_WRITE_FAILED = 6640;

    //
    // MessageId: ERROR_LOG_CONTAINER_OPEN_FAILED
    //
    // MessageText:
    //
    // Log service enountered an error when attempting open a log container.
    //
    int ERROR_LOG_CONTAINER_OPEN_FAILED = 6641;

    //
    // MessageId: ERROR_LOG_CONTAINER_STATE_INVALID
    //
    // MessageText:
    //
    // Log service enountered an invalid container state when attempting a
    // requested action.
    //
    int ERROR_LOG_CONTAINER_STATE_INVALID = 6642;

    //
    // MessageId: ERROR_LOG_STATE_INVALID
    //
    // MessageText:
    //
    // Log service is not in the correct state to perform a requested action.
    //
    int ERROR_LOG_STATE_INVALID = 6643;

    //
    // MessageId: ERROR_LOG_PINNED
    //
    // MessageText:
    //
    // Log space cannot be reclaimed because the log is pinned.
    //
    int ERROR_LOG_PINNED = 6644;

    //
    // MessageId: ERROR_LOG_METADATA_FLUSH_FAILED
    //
    // MessageText:
    //
    // Log metadata flush failed.
    //
    int ERROR_LOG_METADATA_FLUSH_FAILED = 6645;

    //
    // MessageId: ERROR_LOG_INCONSISTENT_SECURITY
    //
    // MessageText:
    //
    // Security on the log and its containers is inconsistent.
    //
    int ERROR_LOG_INCONSISTENT_SECURITY = 6646;

    //
    // MessageId: ERROR_LOG_APPENDED_FLUSH_FAILED
    //
    // MessageText:
    //
    // Records were appended to the log or reservation changes were made, but
    // the log could not be flushed.
    //
    int ERROR_LOG_APPENDED_FLUSH_FAILED = 6647;

    //
    // MessageId: ERROR_LOG_PINNED_RESERVATION
    //
    // MessageText:
    //
    // The log is pinned due to reservation consuming most of the log space.
    // Free some reserved records to make space available.
    //
    int ERROR_LOG_PINNED_RESERVATION = 6648;

    // /////////////////////////////////////////////////
    // =//
    // =Transaction (KTM) Error codes //
    // =//
    // =6700 to=6799=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_INVALID_TRANSACTION
    //
    // MessageText:
    //
    // The transaction handle associated with this operation is not valid.
    //
    int ERROR_INVALID_TRANSACTION = 6700;

    //
    // MessageId: ERROR_TRANSACTION_NOT_ACTIVE
    //
    // MessageText:
    //
    // The requested operation was made in the context of a transaction that is
    // no longer active.
    //
    int ERROR_TRANSACTION_NOT_ACTIVE = 6701;

    //
    // MessageId: ERROR_TRANSACTION_REQUEST_NOT_VALID
    //
    // MessageText:
    //
    // The requested operation is not valid on the Transaction object in its
    // current state.
    //
    int ERROR_TRANSACTION_REQUEST_NOT_VALID = 6702;

    //
    // MessageId: ERROR_TRANSACTION_NOT_REQUESTED
    //
    // MessageText:
    //
    // The caller has called a response API, but the response is not expected
    // because the TM did not issue the corresponding request to the caller.
    //
    int ERROR_TRANSACTION_NOT_REQUESTED = 6703;

    //
    // MessageId: ERROR_TRANSACTION_ALREADY_ABORTED
    //
    // MessageText:
    //
    // It is too late to perform the requested operation, since the Transaction
    // has already been aborted.
    //
    int ERROR_TRANSACTION_ALREADY_ABORTED = 6704;

    //
    // MessageId: ERROR_TRANSACTION_ALREADY_COMMITTED
    //
    // MessageText:
    //
    // It is too late to perform the requested operation, since the Transaction
    // has already been committed.
    //
    int ERROR_TRANSACTION_ALREADY_COMMITTED = 6705;

    //
    // MessageId: ERROR_TM_INITIALIZATION_FAILED
    //
    // MessageText:
    //
    // The Transaction Manager was unable to be successfully initialized.
    // Transacted operations are not supported.
    //
    int ERROR_TM_INITIALIZATION_FAILED = 6706;

    //
    // MessageId: ERROR_RESOURCEMANAGER_READ_ONLY
    //
    // MessageText:
    //
    // The specified ResourceManager made no changes or updates to the resource
    // under this transaction.
    //
    int ERROR_RESOURCEMANAGER_READ_ONLY = 6707;

    //
    // MessageId: ERROR_TRANSACTION_NOT_JOINED
    //
    // MessageText:
    //
    // The resource manager has attempted to prepare a transaction that it has
    // not successfully joined.
    //
    int ERROR_TRANSACTION_NOT_JOINED = 6708;

    //
    // MessageId: ERROR_TRANSACTION_SUPERIOR_EXISTS
    //
    // MessageText:
    //
    // The Transaction object already has a superior enlistment, and the caller
    // attempted an operation that would have created a new superior. Only a
    // single superior enlistment is allow.
    //
    int ERROR_TRANSACTION_SUPERIOR_EXISTS = 6709;

    //
    // MessageId: ERROR_CRM_PROTOCOL_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The RM tried to register a protocol that already exists.
    //
    int ERROR_CRM_PROTOCOL_ALREADY_EXISTS = 6710;

    //
    // MessageId: ERROR_TRANSACTION_PROPAGATION_FAILED
    //
    // MessageText:
    //
    // The attempt to propagate the Transaction failed.
    //
    int ERROR_TRANSACTION_PROPAGATION_FAILED = 6711;

    //
    // MessageId: ERROR_CRM_PROTOCOL_NOT_FOUND
    //
    // MessageText:
    //
    // The requested propagation protocol was not registered as a CRM.
    //
    int ERROR_CRM_PROTOCOL_NOT_FOUND = 6712;

    //
    // MessageId: ERROR_TRANSACTION_INVALID_MARSHALL_BUFFER
    //
    // MessageText:
    //
    // The buffer passed in to PushTransaction or PullTransaction is not in a
    // valid format.
    //
    int ERROR_TRANSACTION_INVALID_MARSHALL_BUFFER = 6713;

    //
    // MessageId: ERROR_CURRENT_TRANSACTION_NOT_VALID
    //
    // MessageText:
    //
    // The current transaction context associated with the thread is not a valid
    // handle to a transaction object.
    //
    int ERROR_CURRENT_TRANSACTION_NOT_VALID = 6714;

    //
    // MessageId: ERROR_TRANSACTION_NOT_FOUND
    //
    // MessageText:
    //
    // The specified Transaction object could not be opened, because it was not
    // found.
    //
    int ERROR_TRANSACTION_NOT_FOUND = 6715;

    //
    // MessageId: ERROR_RESOURCEMANAGER_NOT_FOUND
    //
    // MessageText:
    //
    // The specified ResourceManager object could not be opened, because it was
    // not found.
    //
    int ERROR_RESOURCEMANAGER_NOT_FOUND = 6716;

    //
    // MessageId: ERROR_ENLISTMENT_NOT_FOUND
    //
    // MessageText:
    //
    // The specified Enlistment object could not be opened, because it was not
    // found.
    //
    int ERROR_ENLISTMENT_NOT_FOUND = 6717;

    //
    // MessageId: ERROR_TRANSACTIONMANAGER_NOT_FOUND
    //
    // MessageText:
    //
    // The specified TransactionManager object could not be opened, because it
    // was not found.
    //
    int ERROR_TRANSACTIONMANAGER_NOT_FOUND = 6718;

    //
    // MessageId: ERROR_TRANSACTIONMANAGER_NOT_ONLINE
    //
    // MessageText:
    //
    // The specified ResourceManager was unable to create an enlistment, because
    // its associated TransactionManager is not online.
    //
    int ERROR_TRANSACTIONMANAGER_NOT_ONLINE = 6719;

    //
    // MessageId: ERROR_TRANSACTIONMANAGER_RECOVERY_NAME_COLLISION
    //
    // MessageText:
    //
    // The specified TransactionManager was unable to create the objects
    // contained in its logfile in the Ob namespace. Therefore, the
    // TransactionManager was unable to recover.
    //
    int ERROR_TRANSACTIONMANAGER_RECOVERY_NAME_COLLISION = 6720;

    //
    // MessageId: ERROR_TRANSACTION_NOT_ROOT
    //
    // MessageText:
    //
    // The call to create a superior Enlistment on this Transaction object could
    // not be completed, because the Transaction object specified for the
    // enlistment is a subordinate branch of the Transaction. Only the root of
    // the Transactoin can be enlisted on as a superior.
    //
    int ERROR_TRANSACTION_NOT_ROOT = 6721;

    //
    // MessageId: ERROR_TRANSACTION_OBJECT_EXPIRED
    //
    // MessageText:
    //
    // Because the associated transaction manager or resource manager has been
    // closed, the handle is no longer valid.
    //
    int ERROR_TRANSACTION_OBJECT_EXPIRED = 6722;

    //
    // MessageId: ERROR_TRANSACTION_RESPONSE_NOT_ENLISTED
    //
    // MessageText:
    //
    // The specified operation could not be performed on this Superior
    // enlistment, because the enlistment was not created with the corresponding
    // completion response in the NotificationMask.
    //
    int ERROR_TRANSACTION_RESPONSE_NOT_ENLISTED = 6723;

    //
    // MessageId: ERROR_TRANSACTION_RECORD_TOO_LONG
    //
    // MessageText:
    //
    // The specified operation could not be performed, because the record that
    // would be logged was too long. This can occur because of two conditions:
    // either there are too many Enlistments on this Transaction, or the
    // combined RecoveryInformation being logged on behalf of those Enlistments
    // is too long.
    //
    int ERROR_TRANSACTION_RECORD_TOO_LONG = 6724;

    //
    // MessageId: ERROR_IMPLICIT_TRANSACTION_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Implicit transaction are not supported.
    //
    int ERROR_IMPLICIT_TRANSACTION_NOT_SUPPORTED = 6725;

    //
    // MessageId: ERROR_TRANSACTION_INTEGRITY_VIOLATED
    //
    // MessageText:
    //
    // The kernel transaction manager had to abort or forget the transaction
    // because it blocked forward progress.
    //
    int ERROR_TRANSACTION_INTEGRITY_VIOLATED = 6726;

    // /////////////////////////////////////////////////
    // =//
    // =Transactional File Services (TxF) //
    // =Error codes=//
    // =//
    // =6800 to=6899=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_TRANSACTIONAL_CONFLICT
    //
    // MessageText:
    //
    // The function attempted to use a name that is reserved for use by another
    // transaction.
    //
    int ERROR_TRANSACTIONAL_CONFLICT = 6800;

    //
    // MessageId: ERROR_RM_NOT_ACTIVE
    //
    // MessageText:
    //
    // Transaction support within the specified file system resource manager is
    // not started or was shutdown due to an error.
    //
    int ERROR_RM_NOT_ACTIVE = 6801;

    //
    // MessageId: ERROR_RM_METADATA_CORRUPT
    //
    // MessageText:
    //
    // The metadata of the RM has been corrupted. The RM will not function.
    //
    int ERROR_RM_METADATA_CORRUPT = 6802;

    //
    // MessageId: ERROR_DIRECTORY_NOT_RM
    //
    // MessageText:
    //
    // The specified directory does not contain a resource manager.
    //
    int ERROR_DIRECTORY_NOT_RM = 6803;

    //
    // MessageId: ERROR_TRANSACTIONS_UNSUPPORTED_REMOTE
    //
    // MessageText:
    //
    // The remote server or share does not support transacted file operations.
    //
    int ERROR_TRANSACTIONS_UNSUPPORTED_REMOTE = 6805;

    //
    // MessageId: ERROR_LOG_RESIZE_INVALID_SIZE
    //
    // MessageText:
    //
    // The requested log size is invalid.
    //
    int ERROR_LOG_RESIZE_INVALID_SIZE = 6806;

    //
    // MessageId: ERROR_OBJECT_NO_LONGER_EXISTS
    //
    // MessageText:
    //
    // The object (file, stream, link) corresponding to the handle has been
    // deleted by a Transaction Savepoint Rollback.
    //
    int ERROR_OBJECT_NO_LONGER_EXISTS = 6807;

    //
    // MessageId: ERROR_STREAM_MINIVERSION_NOT_FOUND
    //
    // MessageText:
    //
    // The specified file miniversion was not found for this transacted file
    // open.
    //
    int ERROR_STREAM_MINIVERSION_NOT_FOUND = 6808;

    //
    // MessageId: ERROR_STREAM_MINIVERSION_NOT_VALID
    //
    // MessageText:
    //
    // The specified file miniversion was found but has been invalidated. Most
    // likely cause is a transaction savepoint rollback.
    //
    int ERROR_STREAM_MINIVERSION_NOT_VALID = 6809;

    //
    // MessageId: ERROR_MINIVERSION_INACCESSIBLE_FROM_SPECIFIED_TRANSACTION
    //
    // MessageText:
    //
    // A miniversion may only be opened in the context of the transaction that
    // created it.
    //
    int ERROR_MINIVERSION_INACCESSIBLE_FROM_SPECIFIED_TRANSACTION = 6810;

    //
    // MessageId: ERROR_CANT_OPEN_MINIVERSION_WITH_MODIFY_INTENT
    //
    // MessageText:
    //
    // It is not possible to open a miniversion with modify access.
    //
    int ERROR_CANT_OPEN_MINIVERSION_WITH_MODIFY_INTENT = 6811;

    //
    // MessageId: ERROR_CANT_CREATE_MORE_STREAM_MINIVERSIONS
    //
    // MessageText:
    //
    // It is not possible to create any more miniversions for this stream.
    //
    int ERROR_CANT_CREATE_MORE_STREAM_MINIVERSIONS = 6812;

    //
    // MessageId: ERROR_REMOTE_FILE_VERSION_MISMATCH
    //
    // MessageText:
    //
    // The remote server sent mismatching version number or Fid for a file
    // opened with transactions.
    //
    int ERROR_REMOTE_FILE_VERSION_MISMATCH = 6814;

    //
    // MessageId: ERROR_HANDLE_NO_LONGER_VALID
    //
    // MessageText:
    //
    // The handle has been invalidated by a transaction. The most likely cause
    // is the presence of memory mapping on a file or an open handle when the
    // transaction ended or rolled back to savepoint.
    //
    int ERROR_HANDLE_NO_LONGER_VALID = 6815;

    //
    // MessageId: ERROR_NO_TXF_METADATA
    //
    // MessageText:
    //
    // There is no transaction metadata on the file.
    //
    int ERROR_NO_TXF_METADATA = 6816;

    //
    // MessageId: ERROR_LOG_CORRUPTION_DETECTED
    //
    // MessageText:
    //
    // The log data is corrupt.
    //
    int ERROR_LOG_CORRUPTION_DETECTED = 6817;

    //
    // MessageId: ERROR_CANT_RECOVER_WITH_HANDLE_OPEN
    //
    // MessageText:
    //
    // The file can't be recovered because there is a handle still open on it.
    //
    int ERROR_CANT_RECOVER_WITH_HANDLE_OPEN = 6818;

    //
    // MessageId: ERROR_RM_DISCONNECTED
    //
    // MessageText:
    //
    // The transaction outcome is unavailable because the resource manager
    // responsible for it has disconnected.
    //
    int ERROR_RM_DISCONNECTED = 6819;

    //
    // MessageId: ERROR_ENLISTMENT_NOT_SUPERIOR
    //
    // MessageText:
    //
    // The request was rejected because the enlistment in question is not a
    // superior enlistment.
    //
    int ERROR_ENLISTMENT_NOT_SUPERIOR = 6820;

    //
    // MessageId: ERROR_RECOVERY_NOT_NEEDED
    //
    // MessageText:
    //
    // The transactional resource manager is already consistent. Recovery is not
    // needed.
    //
    int ERROR_RECOVERY_NOT_NEEDED = 6821;

    //
    // MessageId: ERROR_RM_ALREADY_STARTED
    //
    // MessageText:
    //
    // The transactional resource manager has already been started.
    //
    int ERROR_RM_ALREADY_STARTED = 6822;

    //
    // MessageId: ERROR_FILE_IDENTITY_NOT_PERSISTENT
    //
    // MessageText:
    //
    // The file cannot be opened transactionally, because its identity depends
    // on the outcome of an unresolved transaction.
    //
    int ERROR_FILE_IDENTITY_NOT_PERSISTENT = 6823;

    //
    // MessageId: ERROR_CANT_BREAK_TRANSACTIONAL_DEPENDENCY
    //
    // MessageText:
    //
    // The operation cannot be performed because another transaction is
    // depending on the fact that this property will not change.
    //
    int ERROR_CANT_BREAK_TRANSACTIONAL_DEPENDENCY = 6824;

    //
    // MessageId: ERROR_CANT_CROSS_RM_BOUNDARY
    //
    // MessageText:
    //
    // The operation would involve a single file with two transactional resource
    // managers and is therefore not allowed.
    //
    int ERROR_CANT_CROSS_RM_BOUNDARY = 6825;

    //
    // MessageId: ERROR_TXF_DIR_NOT_EMPTY
    //
    // MessageText:
    //
    // The $Txf directory must be empty for this operation to succeed.
    //
    int ERROR_TXF_DIR_NOT_EMPTY = 6826;

    //
    // MessageId: ERROR_INDOUBT_TRANSACTIONS_EXIST
    //
    // MessageText:
    //
    // The operation would leave a transactional resource manager in an
    // inconsistent state and is therefore not allowed.
    //
    int ERROR_INDOUBT_TRANSACTIONS_EXIST = 6827;

    //
    // MessageId: ERROR_TM_VOLATILE
    //
    // MessageText:
    //
    // The operation could not be completed because the transaction manager does
    // not have a log.
    //
    int ERROR_TM_VOLATILE = 6828;

    //
    // MessageId: ERROR_ROLLBACK_TIMER_EXPIRED
    //
    // MessageText:
    //
    // A rollback could not be scheduled because a previously scheduled rollback
    // has already executed or been queued for execution.
    //
    int ERROR_ROLLBACK_TIMER_EXPIRED = 6829;

    //
    // MessageId: ERROR_TXF_ATTRIBUTE_CORRUPT
    //
    // MessageText:
    //
    // The transactional metadata attribute on the file or directory is corrupt
    // and unreadable.
    //
    int ERROR_TXF_ATTRIBUTE_CORRUPT = 6830;

    //
    // MessageId: ERROR_EFS_NOT_ALLOWED_IN_TRANSACTION
    //
    // MessageText:
    //
    // The encryption operation could not be completed because a transaction is
    // active.
    //
    int ERROR_EFS_NOT_ALLOWED_IN_TRANSACTION = 6831;

    //
    // MessageId: ERROR_TRANSACTIONAL_OPEN_NOT_ALLOWED
    //
    // MessageText:
    //
    // This object is not allowed to be opened in a transaction.
    //
    int ERROR_TRANSACTIONAL_OPEN_NOT_ALLOWED = 6832;

    //
    // MessageId: ERROR_LOG_GROWTH_FAILED
    //
    // MessageText:
    //
    // An attempt to create space in the transactional resource manager's log
    // failed. The failure status has been recorded in the event log.
    //
    int ERROR_LOG_GROWTH_FAILED = 6833;

    //
    // MessageId: ERROR_TRANSACTED_MAPPING_UNSUPPORTED_REMOTE
    //
    // MessageText:
    //
    // Memory mapping (creating a mapped section) a remote file under a
    // transaction is not supported.
    //
    int ERROR_TRANSACTED_MAPPING_UNSUPPORTED_REMOTE = 6834;

    //
    // MessageId: ERROR_TXF_METADATA_ALREADY_PRESENT
    //
    // MessageText:
    //
    // Transaction metadata is already present on this file and cannot be
    // superseded.
    //
    int ERROR_TXF_METADATA_ALREADY_PRESENT = 6835;

    //
    // MessageId: ERROR_TRANSACTION_SCOPE_CALLBACKS_NOT_SET
    //
    // MessageText:
    //
    // A transaction scope could not be entered because the scope handler has
    // not been initialized.
    //
    int ERROR_TRANSACTION_SCOPE_CALLBACKS_NOT_SET = 6836;

    //
    // MessageId: ERROR_TRANSACTION_REQUIRED_PROMOTION
    //
    // MessageText:
    //
    // Promotion was required in order to allow the resource manager to enlist,
    // but the transaction was set to disallow it.
    //
    int ERROR_TRANSACTION_REQUIRED_PROMOTION = 6837;

    //
    // MessageId: ERROR_CANNOT_EXECUTE_FILE_IN_TRANSACTION
    //
    // MessageText:
    //
    // This file is open for modification in an unresolved transaction and may
    // be opened for execute only by a transacted reader.
    //
    int ERROR_CANNOT_EXECUTE_FILE_IN_TRANSACTION = 6838;

    //
    // MessageId: ERROR_TRANSACTIONS_NOT_FROZEN
    //
    // MessageText:
    //
    // The request to thaw frozen transactions was ignored because transactions
    // had not previously been frozen.
    //
    int ERROR_TRANSACTIONS_NOT_FROZEN = 6839;

    //
    // MessageId: ERROR_TRANSACTION_FREEZE_IN_PROGRESS
    //
    // MessageText:
    //
    // Transactions cannot be frozen because a freeze is already in progress.
    //
    int ERROR_TRANSACTION_FREEZE_IN_PROGRESS = 6840;

    //
    // MessageId: ERROR_NOT_SNAPSHOT_VOLUME
    //
    // MessageText:
    //
    // The target volume is not a snapshot volume. This operation is only valid
    // on a volume mounted as a snapshot.
    //
    int ERROR_NOT_SNAPSHOT_VOLUME = 6841;

    //
    // MessageId: ERROR_NO_SAVEPOINT_WITH_OPEN_FILES
    //
    // MessageText:
    //
    // The savepoint operation failed because files are open on the transaction.
    // This is not permitted.
    //
    int ERROR_NO_SAVEPOINT_WITH_OPEN_FILES = 6842;

    //
    // MessageId: ERROR_DATA_LOST_REPAIR
    //
    // MessageText:
    //
    // Windows has discovered corruption in a file, and that file has since been
    // repaired. Data loss may have occurred.
    //
    int ERROR_DATA_LOST_REPAIR = 6843;

    //
    // MessageId: ERROR_SPARSE_NOT_ALLOWED_IN_TRANSACTION
    //
    // MessageText:
    //
    // The sparse operation could not be completed because a transaction is
    // active on the file.
    //
    int ERROR_SPARSE_NOT_ALLOWED_IN_TRANSACTION = 6844;

    //
    // MessageId: ERROR_TM_IDENTITY_MISMATCH
    //
    // MessageText:
    //
    // The call to create a TransactionManager object failed because the Tm
    // Identity stored in the logfile does not match the Tm Identity that was
    // passed in as an argument.
    //
    int ERROR_TM_IDENTITY_MISMATCH = 6845;

    //
    // MessageId: ERROR_FLOATED_SECTION
    //
    // MessageText:
    //
    // I/O was attempted on a section object that has been floated as a result
    // of a transaction ending. There is no valid data.
    //
    int ERROR_FLOATED_SECTION = 6846;

    //
    // MessageId: ERROR_CANNOT_ACCEPT_TRANSACTED_WORK
    //
    // MessageText:
    //
    // The transactional resource manager cannot currently accept transacted
    // work due to a transient condition such as low resources.
    //
    int ERROR_CANNOT_ACCEPT_TRANSACTED_WORK = 6847;

    //
    // MessageId: ERROR_CANNOT_ABORT_TRANSACTIONS
    //
    // MessageText:
    //
    // The transactional resource manager had too many tranactions outstanding
    // that could not be aborted. The transactional resource manger has been
    // shut down.
    //
    int ERROR_CANNOT_ABORT_TRANSACTIONS = 6848;

    //
    // MessageId: ERROR_BAD_CLUSTERS
    //
    // MessageText:
    //
    // The operation could not be completed due to bad clusters on disk.
    //
    int ERROR_BAD_CLUSTERS = 6849;

    //
    // MessageId: ERROR_COMPRESSION_NOT_ALLOWED_IN_TRANSACTION
    //
    // MessageText:
    //
    // The compression operation could not be completed because a transaction is
    // active on the file.
    //
    int ERROR_COMPRESSION_NOT_ALLOWED_IN_TRANSACTION = 6850;

    //
    // MessageId: ERROR_VOLUME_DIRTY
    //
    // MessageText:
    //
    // The operation could not be completed because the volume is dirty. Please
    // run chkdsk and try again.
    //
    int ERROR_VOLUME_DIRTY = 6851;

    //
    // MessageId: ERROR_NO_LINK_TRACKING_IN_TRANSACTION
    //
    // MessageText:
    //
    // The link tracking operation could not be completed because a transaction
    // is active.
    //
    int ERROR_NO_LINK_TRACKING_IN_TRANSACTION = 6852;

    //
    // MessageId: ERROR_OPERATION_NOT_SUPPORTED_IN_TRANSACTION
    //
    // MessageText:
    //
    // This operation cannot be performed in a transaction.
    //
    int ERROR_OPERATION_NOT_SUPPORTED_IN_TRANSACTION = 6853;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =6900 to=6999=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =Terminal Server Error codes=//
    // =//
    // =7000 to=7099=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_CTX_WINSTATION_NAME_INVALID
    //
    // MessageText:
    //
    // The specified session name is invalid.
    //
    int ERROR_CTX_WINSTATION_NAME_INVALID = 7001;

    //
    // MessageId: ERROR_CTX_INVALID_PD
    //
    // MessageText:
    //
    // The specified protocol driver is invalid.
    //
    int ERROR_CTX_INVALID_PD = 7002;

    //
    // MessageId: ERROR_CTX_PD_NOT_FOUND
    //
    // MessageText:
    //
    // The specified protocol driver was not found in the system path.
    //
    int ERROR_CTX_PD_NOT_FOUND = 7003;

    //
    // MessageId: ERROR_CTX_WD_NOT_FOUND
    //
    // MessageText:
    //
    // The specified terminal connection driver was not found in the system
    // path.
    //
    int ERROR_CTX_WD_NOT_FOUND = 7004;

    //
    // MessageId: ERROR_CTX_CANNOT_MAKE_EVENTLOG_ENTRY
    //
    // MessageText:
    //
    // A registry key for event logging could not be created for this session.
    //
    int ERROR_CTX_CANNOT_MAKE_EVENTLOG_ENTRY = 7005;

    //
    // MessageId: ERROR_CTX_SERVICE_NAME_COLLISION
    //
    // MessageText:
    //
    // A service with the same name already exists on the system.
    //
    int ERROR_CTX_SERVICE_NAME_COLLISION = 7006;

    //
    // MessageId: ERROR_CTX_CLOSE_PENDING
    //
    // MessageText:
    //
    // A close operation is pending on the session.
    //
    int ERROR_CTX_CLOSE_PENDING = 7007;

    //
    // MessageId: ERROR_CTX_NO_OUTBUF
    //
    // MessageText:
    //
    // There are no free output buffers available.
    //
    int ERROR_CTX_NO_OUTBUF = 7008;

    //
    // MessageId: ERROR_CTX_MODEM_INF_NOT_FOUND
    //
    // MessageText:
    //
    // The MODEM.INF file was not found.
    //
    int ERROR_CTX_MODEM_INF_NOT_FOUND = 7009;

    //
    // MessageId: ERROR_CTX_INVALID_MODEMNAME
    //
    // MessageText:
    //
    // The modem name was not found in MODEM.INF.
    //
    int ERROR_CTX_INVALID_MODEMNAME = 7010;

    //
    // MessageId: ERROR_CTX_MODEM_RESPONSE_ERROR
    //
    // MessageText:
    //
    // The modem did not accept the command sent to it. Verify that the
    // configured modem name matches the attached modem.
    //
    int ERROR_CTX_MODEM_RESPONSE_ERROR = 7011;

    //
    // MessageId: ERROR_CTX_MODEM_RESPONSE_TIMEOUT
    //
    // MessageText:
    //
    // The modem did not respond to the command sent to it. Verify that the
    // modem is properly cabled and powered on.
    //
    int ERROR_CTX_MODEM_RESPONSE_TIMEOUT = 7012;

    //
    // MessageId: ERROR_CTX_MODEM_RESPONSE_NO_CARRIER
    //
    // MessageText:
    //
    // Carrier detect has failed or carrier has been dropped due to disconnect.
    //
    int ERROR_CTX_MODEM_RESPONSE_NO_CARRIER = 7013;

    //
    // MessageId: ERROR_CTX_MODEM_RESPONSE_NO_DIALTONE
    //
    // MessageText:
    //
    // Dial tone not detected within the required time. Verify that the phone
    // cable is properly attached and functional.
    //
    int ERROR_CTX_MODEM_RESPONSE_NO_DIALTONE = 7014;

    //
    // MessageId: ERROR_CTX_MODEM_RESPONSE_BUSY
    //
    // MessageText:
    //
    // Busy signal detected at remote site on callback.
    //
    int ERROR_CTX_MODEM_RESPONSE_BUSY = 7015;

    //
    // MessageId: ERROR_CTX_MODEM_RESPONSE_VOICE
    //
    // MessageText:
    //
    // Voice detected at remote site on callback.
    //
    int ERROR_CTX_MODEM_RESPONSE_VOICE = 7016;

    //
    // MessageId: ERROR_CTX_TD_ERROR
    //
    // MessageText:
    //
    // Transport driver error
    //
    int ERROR_CTX_TD_ERROR = 7017;

    //
    // MessageId: ERROR_CTX_WINSTATION_NOT_FOUND
    //
    // MessageText:
    //
    // The specified session cannot be found.
    //
    int ERROR_CTX_WINSTATION_NOT_FOUND = 7022;

    //
    // MessageId: ERROR_CTX_WINSTATION_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The specified session name is already in use.
    //
    int ERROR_CTX_WINSTATION_ALREADY_EXISTS = 7023;

    //
    // MessageId: ERROR_CTX_WINSTATION_BUSY
    //
    // MessageText:
    //
    // The requested operation cannot be completed because the terminal
    // connection is currently busy processing a connect, disconnect, reset, or
    // delete operation.
    //
    int ERROR_CTX_WINSTATION_BUSY = 7024;

    //
    // MessageId: ERROR_CTX_BAD_VIDEO_MODE
    //
    // MessageText:
    //
    // An attempt has been made to connect to a session whose video mode is not
    // supported by the current client.
    //
    int ERROR_CTX_BAD_VIDEO_MODE = 7025;

    //
    // MessageId: ERROR_CTX_GRAPHICS_INVALID
    //
    // MessageText:
    //
    // The application attempted to enable DOS graphics mode.
    // DOS graphics mode is not supported.
    //
    int ERROR_CTX_GRAPHICS_INVALID = 7035;

    //
    // MessageId: ERROR_CTX_LOGON_DISABLED
    //
    // MessageText:
    //
    // Your interactive logon privilege has been disabled.
    // Please contact your administrator.
    //
    int ERROR_CTX_LOGON_DISABLED = 7037;

    //
    // MessageId: ERROR_CTX_NOT_CONSOLE
    //
    // MessageText:
    //
    // The requested operation can be performed only on the system console.
    // This is most often the result of a driver or system DLL requiring direct
    // console access.
    //
    int ERROR_CTX_NOT_CONSOLE = 7038;

    //
    // MessageId: ERROR_CTX_CLIENT_QUERY_TIMEOUT
    //
    // MessageText:
    //
    // The client failed to respond to the server connect message.
    //
    int ERROR_CTX_CLIENT_QUERY_TIMEOUT = 7040;

    //
    // MessageId: ERROR_CTX_CONSOLE_DISCONNECT
    //
    // MessageText:
    //
    // Disconnecting the console session is not supported.
    //
    int ERROR_CTX_CONSOLE_DISCONNECT = 7041;

    //
    // MessageId: ERROR_CTX_CONSOLE_CONNECT
    //
    // MessageText:
    //
    // Reconnecting a disconnected session to the console is not supported.
    //
    int ERROR_CTX_CONSOLE_CONNECT = 7042;

    //
    // MessageId: ERROR_CTX_SHADOW_DENIED
    //
    // MessageText:
    //
    // The request to control another session remotely was denied.
    //
    int ERROR_CTX_SHADOW_DENIED = 7044;

    //
    // MessageId: ERROR_CTX_WINSTATION_ACCESS_DENIED
    //
    // MessageText:
    //
    // The requested session access is denied.
    //
    int ERROR_CTX_WINSTATION_ACCESS_DENIED = 7045;

    //
    // MessageId: ERROR_CTX_INVALID_WD
    //
    // MessageText:
    //
    // The specified terminal connection driver is invalid.
    //
    int ERROR_CTX_INVALID_WD = 7049;

    //
    // MessageId: ERROR_CTX_SHADOW_INVALID
    //
    // MessageText:
    //
    // The requested session cannot be controlled remotely.
    // This may be because the session is disconnected or does not currently
    // have a user logged on.
    //
    int ERROR_CTX_SHADOW_INVALID = 7050;

    //
    // MessageId: ERROR_CTX_SHADOW_DISABLED
    //
    // MessageText:
    //
    // The requested session is not configured to allow remote control.
    //
    int ERROR_CTX_SHADOW_DISABLED = 7051;

    //
    // MessageId: ERROR_CTX_CLIENT_LICENSE_IN_USE
    //
    // MessageText:
    //
    // Your request to connect to this Terminal Server has been rejected. Your
    // Terminal Server client license number is currently being used by another
    // user.
    // Please call your system administrator to obtain a unique license number.
    //
    int ERROR_CTX_CLIENT_LICENSE_IN_USE = 7052;

    //
    // MessageId: ERROR_CTX_CLIENT_LICENSE_NOT_SET
    //
    // MessageText:
    //
    // Your request to connect to this Terminal Server has been rejected. Your
    // Terminal Server client license number has not been entered for this copy
    // of the Terminal Server client.
    // Please contact your system administrator.
    //
    int ERROR_CTX_CLIENT_LICENSE_NOT_SET = 7053;

    //
    // MessageId: ERROR_CTX_LICENSE_NOT_AVAILABLE
    //
    // MessageText:
    //
    // The number of connections to this computer is limited and all connections
    // are in use right now.
    // Try connecting later or contact your system administrator.
    //
    int ERROR_CTX_LICENSE_NOT_AVAILABLE = 7054;

    //
    // MessageId: ERROR_CTX_LICENSE_CLIENT_INVALID
    //
    // MessageText:
    //
    // The client you are using is not licensed to use this system. Your logon
    // request is denied.
    //
    int ERROR_CTX_LICENSE_CLIENT_INVALID = 7055;

    //
    // MessageId: ERROR_CTX_LICENSE_EXPIRED
    //
    // MessageText:
    //
    // The system license has expired. Your logon request is denied.
    //
    int ERROR_CTX_LICENSE_EXPIRED = 7056;

    //
    // MessageId: ERROR_CTX_SHADOW_NOT_RUNNING
    //
    // MessageText:
    //
    // Remote control could not be terminated because the specified session is
    // not currently being remotely controlled.
    //
    int ERROR_CTX_SHADOW_NOT_RUNNING = 7057;

    //
    // MessageId: ERROR_CTX_SHADOW_ENDED_BY_MODE_CHANGE
    //
    // MessageText:
    //
    // The remote control of the console was terminated because the display mode
    // was changed. Changing the display mode in a remote control session is not
    // supported.
    //
    int ERROR_CTX_SHADOW_ENDED_BY_MODE_CHANGE = 7058;

    //
    // MessageId: ERROR_ACTIVATION_COUNT_EXCEEDED
    //
    // MessageText:
    //
    // Activation has already been reset the maximum number of times for this
    // installation. Your activation timer will not be cleared.
    //
    int ERROR_ACTIVATION_COUNT_EXCEEDED = 7059;

    //
    // MessageId: ERROR_CTX_WINSTATIONS_DISABLED
    //
    // MessageText:
    //
    // Remote logins are currently disabled.
    //
    int ERROR_CTX_WINSTATIONS_DISABLED = 7060;

    //
    // MessageId: ERROR_CTX_ENCRYPTION_LEVEL_REQUIRED
    //
    // MessageText:
    //
    // You do not have the proper encryption level to access this Session.
    //
    int ERROR_CTX_ENCRYPTION_LEVEL_REQUIRED = 7061;

    //
    // MessageId: ERROR_CTX_SESSION_IN_USE
    //
    // MessageText:
    //
    // The user %s\\%s is currently logged on to this computer. Only the current
    // user or an administrator can log on to this computer.
    //
    int ERROR_CTX_SESSION_IN_USE = 7062;

    //
    // MessageId: ERROR_CTX_NO_FORCE_LOGOFF
    //
    // MessageText:
    //
    // The user %s\\%s is already logged on to the console of this computer. You
    // do not have permission to log in at this time. To resolve this issue,
    // contact %s\\%s and have them log off.
    //
    int ERROR_CTX_NO_FORCE_LOGOFF = 7063;

    //
    // MessageId: ERROR_CTX_ACCOUNT_RESTRICTION
    //
    // MessageText:
    //
    // Unable to log you on because of an account restriction.
    //
    int ERROR_CTX_ACCOUNT_RESTRICTION = 7064;

    //
    // MessageId: ERROR_RDP_PROTOCOL_ERROR
    //
    // MessageText:
    //
    // The RDP protocol component %2 detected an error in the protocol stream
    // and has disconnected the client.
    //
    int ERROR_RDP_PROTOCOL_ERROR = 7065;

    //
    // MessageId: ERROR_CTX_CDM_CONNECT
    //
    // MessageText:
    //
    // The Client Drive Mapping Service Has Connected on Terminal Connection.
    //
    int ERROR_CTX_CDM_CONNECT = 7066;

    //
    // MessageId: ERROR_CTX_CDM_DISCONNECT
    //
    // MessageText:
    //
    // The Client Drive Mapping Service Has Disconnected on Terminal Connection.
    //
    int ERROR_CTX_CDM_DISCONNECT = 7067;

    //
    // MessageId: ERROR_CTX_SECURITY_LAYER_ERROR
    //
    // MessageText:
    //
    // The Terminal Server security layer detected an error in the protocol
    // stream and has disconnected the client.
    //
    int ERROR_CTX_SECURITY_LAYER_ERROR = 7068;

    //
    // MessageId: ERROR_TS_INCOMPATIBLE_SESSIONS
    //
    // MessageText:
    //
    // The target session is incompatible with the current session.
    //
    int ERROR_TS_INCOMPATIBLE_SESSIONS = 7069;

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =7100 to=7499=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =/
    // =Traffic Control Error Codes=/
    // =/
    // =7500 to=7999=/
    // =/
    // =defined in: tcerror.h=/
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =Active Directory Error codes=//
    // =//
    // =8000 to=8999=//
    // /////////////////////////////////////////////////

    // *****************
    // FACILITY_FILE_REPLICATION_SERVICE
    // *****************
    //
    // MessageId: FRS_ERR_INVALID_API_SEQUENCE
    //
    // MessageText:
    //
    // The file replication service API was called incorrectly.
    //
    int FRS_ERR_INVALID_API_SEQUENCE = 8001;

    //
    // MessageId: FRS_ERR_STARTING_SERVICE
    //
    // MessageText:
    //
    // The file replication service cannot be started.
    //
    int FRS_ERR_STARTING_SERVICE = 8002;

    //
    // MessageId: FRS_ERR_STOPPING_SERVICE
    //
    // MessageText:
    //
    // The file replication service cannot be stopped.
    //
    int FRS_ERR_STOPPING_SERVICE = 8003;

    //
    // MessageId: FRS_ERR_INTERNAL_API
    //
    // MessageText:
    //
    // The file replication service API terminated the request.
    // The event log may have more information.
    //
    int FRS_ERR_INTERNAL_API = 8004;

    //
    // MessageId: FRS_ERR_INTERNAL
    //
    // MessageText:
    //
    // The file replication service terminated the request.
    // The event log may have more information.
    //
    int FRS_ERR_INTERNAL = 8005;

    //
    // MessageId: FRS_ERR_SERVICE_COMM
    //
    // MessageText:
    //
    // The file replication service cannot be contacted.
    // The event log may have more information.
    //
    int FRS_ERR_SERVICE_COMM = 8006;

    //
    // MessageId: FRS_ERR_INSUFFICIENT_PRIV
    //
    // MessageText:
    //
    // The file replication service cannot satisfy the request because the user
    // has insufficient privileges.
    // The event log may have more information.
    //
    int FRS_ERR_INSUFFICIENT_PRIV = 8007;

    //
    // MessageId: FRS_ERR_AUTHENTICATION
    //
    // MessageText:
    //
    // The file replication service cannot satisfy the request because
    // authenticated RPC is not available.
    // The event log may have more information.
    //
    int FRS_ERR_AUTHENTICATION = 8008;

    //
    // MessageId: FRS_ERR_PARENT_INSUFFICIENT_PRIV
    //
    // MessageText:
    //
    // The file replication service cannot satisfy the request because the user
    // has insufficient privileges on the domain controller.
    // The event log may have more information.
    //
    int FRS_ERR_PARENT_INSUFFICIENT_PRIV = 8009;

    //
    // MessageId: FRS_ERR_PARENT_AUTHENTICATION
    //
    // MessageText:
    //
    // The file replication service cannot satisfy the request because
    // authenticated RPC is not available on the domain controller.
    // The event log may have more information.
    //
    int FRS_ERR_PARENT_AUTHENTICATION = 8010;

    //
    // MessageId: FRS_ERR_CHILD_TO_PARENT_COMM
    //
    // MessageText:
    //
    // The file replication service cannot communicate with the file replication
    // service on the domain controller.
    // The event log may have more information.
    //
    int FRS_ERR_CHILD_TO_PARENT_COMM = 8011;

    //
    // MessageId: FRS_ERR_PARENT_TO_CHILD_COMM
    //
    // MessageText:
    //
    // The file replication service on the domain controller cannot communicate
    // with the file replication service on this computer.
    // The event log may have more information.
    //
    int FRS_ERR_PARENT_TO_CHILD_COMM = 8012;

    //
    // MessageId: FRS_ERR_SYSVOL_POPULATE
    //
    // MessageText:
    //
    // The file replication service cannot populate the system volume because of
    // an internal error.
    // The event log may have more information.
    //
    int FRS_ERR_SYSVOL_POPULATE = 8013;

    //
    // MessageId: FRS_ERR_SYSVOL_POPULATE_TIMEOUT
    //
    // MessageText:
    //
    // The file replication service cannot populate the system volume because of
    // an internal timeout.
    // The event log may have more information.
    //
    int FRS_ERR_SYSVOL_POPULATE_TIMEOUT = 8014;

    //
    // MessageId: FRS_ERR_SYSVOL_IS_BUSY
    //
    // MessageText:
    //
    // The file replication service cannot process the request. The system
    // volume is busy with a previous request.
    //
    int FRS_ERR_SYSVOL_IS_BUSY = 8015;

    //
    // MessageId: FRS_ERR_SYSVOL_DEMOTE
    //
    // MessageText:
    //
    // The file replication service cannot stop replicating the system volume
    // because of an internal error.
    // The event log may have more information.
    //
    int FRS_ERR_SYSVOL_DEMOTE = 8016;

    //
    // MessageId: FRS_ERR_INVALID_SERVICE_PARAMETER
    //
    // MessageText:
    //
    // The file replication service detected an invalid parameter.
    //
    int FRS_ERR_INVALID_SERVICE_PARAMETER = 8017;

    // *****************
    // FACILITY DIRECTORY SERVICE
    // *****************
    int DS_S_SUCCESS = NO_ERROR;
    //
    // MessageId: ERROR_DS_NOT_INSTALLED
    //
    // MessageText:
    //
    // An error occurred while installing the directory service. For more
    // information, see the event log.
    //
    int ERROR_DS_NOT_INSTALLED = 8200;

    //
    // MessageId: ERROR_DS_MEMBERSHIP_EVALUATED_LOCALLY
    //
    // MessageText:
    //
    // The directory service evaluated group memberships locally.
    //
    int ERROR_DS_MEMBERSHIP_EVALUATED_LOCALLY = 8201;

    //
    // MessageId: ERROR_DS_NO_ATTRIBUTE_OR_VALUE
    //
    // MessageText:
    //
    // The specified directory service attribute or value does not exist.
    //
    int ERROR_DS_NO_ATTRIBUTE_OR_VALUE = 8202;

    //
    // MessageId: ERROR_DS_INVALID_ATTRIBUTE_SYNTAX
    //
    // MessageText:
    //
    // The attribute syntax specified to the directory service is invalid.
    //
    int ERROR_DS_INVALID_ATTRIBUTE_SYNTAX = 8203;

    //
    // MessageId: ERROR_DS_ATTRIBUTE_TYPE_UNDEFINED
    //
    // MessageText:
    //
    // The attribute type specified to the directory service is not defined.
    //
    int ERROR_DS_ATTRIBUTE_TYPE_UNDEFINED = 8204;

    //
    // MessageId: ERROR_DS_ATTRIBUTE_OR_VALUE_EXISTS
    //
    // MessageText:
    //
    // The specified directory service attribute or value already exists.
    //
    int ERROR_DS_ATTRIBUTE_OR_VALUE_EXISTS = 8205;

    //
    // MessageId: ERROR_DS_BUSY
    //
    // MessageText:
    //
    // The directory service is busy.
    //
    int ERROR_DS_BUSY = 8206;

    //
    // MessageId: ERROR_DS_UNAVAILABLE
    //
    // MessageText:
    //
    // The directory service is unavailable.
    //
    int ERROR_DS_UNAVAILABLE = 8207;

    //
    // MessageId: ERROR_DS_NO_RIDS_ALLOCATED
    //
    // MessageText:
    //
    // The directory service was unable to allocate a relative identifier.
    //
    int ERROR_DS_NO_RIDS_ALLOCATED = 8208;

    //
    // MessageId: ERROR_DS_NO_MORE_RIDS
    //
    // MessageText:
    //
    // The directory service has exhausted the pool of relative identifiers.
    //
    int ERROR_DS_NO_MORE_RIDS = 8209;

    //
    // MessageId: ERROR_DS_INCORRECT_ROLE_OWNER
    //
    // MessageText:
    //
    // The requested operation could not be performed because the directory
    // service is not the master for that type of operation.
    //
    int ERROR_DS_INCORRECT_ROLE_OWNER = 8210;

    //
    // MessageId: ERROR_DS_RIDMGR_INIT_ERROR
    //
    // MessageText:
    //
    // The directory service was unable to initialize the subsystem that
    // allocates relative identifiers.
    //
    int ERROR_DS_RIDMGR_INIT_ERROR = 8211;

    //
    // MessageId: ERROR_DS_OBJ_CLASS_VIOLATION
    //
    // MessageText:
    //
    // The requested operation did not satisfy one or more constraints
    // associated with the class of the object.
    //
    int ERROR_DS_OBJ_CLASS_VIOLATION = 8212;

    //
    // MessageId: ERROR_DS_CANT_ON_NON_LEAF
    //
    // MessageText:
    //
    // The directory service can perform the requested operation only on a leaf
    // object.
    //
    int ERROR_DS_CANT_ON_NON_LEAF = 8213;

    //
    // MessageId: ERROR_DS_CANT_ON_RDN
    //
    // MessageText:
    //
    // The directory service cannot perform the requested operation on the RDN
    // attribute of an object.
    //
    int ERROR_DS_CANT_ON_RDN = 8214;

    //
    // MessageId: ERROR_DS_CANT_MOD_OBJ_CLASS
    //
    // MessageText:
    //
    // The directory service detected an attempt to modify the object class of
    // an object.
    //
    int ERROR_DS_CANT_MOD_OBJ_CLASS = 8215;

    //
    // MessageId: ERROR_DS_CROSS_DOM_MOVE_ERROR
    //
    // MessageText:
    //
    // The requested cross-domain move operation could not be performed.
    //
    int ERROR_DS_CROSS_DOM_MOVE_ERROR = 8216;

    //
    // MessageId: ERROR_DS_GC_NOT_AVAILABLE
    //
    // MessageText:
    //
    // Unable to contact the global catalog server.
    //
    int ERROR_DS_GC_NOT_AVAILABLE = 8217;

    //
    // MessageId: ERROR_SHARED_POLICY
    //
    // MessageText:
    //
    // The policy object is shared and can only be modified at the root.
    //
    int ERROR_SHARED_POLICY = 8218;

    //
    // MessageId: ERROR_POLICY_OBJECT_NOT_FOUND
    //
    // MessageText:
    //
    // The policy object does not exist.
    //
    int ERROR_POLICY_OBJECT_NOT_FOUND = 8219;

    //
    // MessageId: ERROR_POLICY_ONLY_IN_DS
    //
    // MessageText:
    //
    // The requested policy information is only in the directory service.
    //
    int ERROR_POLICY_ONLY_IN_DS = 8220;

    //
    // MessageId: ERROR_PROMOTION_ACTIVE
    //
    // MessageText:
    //
    // A domain controller promotion is currently active.
    //
    int ERROR_PROMOTION_ACTIVE = 8221;

    //
    // MessageId: ERROR_NO_PROMOTION_ACTIVE
    //
    // MessageText:
    //
    // A domain controller promotion is not currently active
    //
    int ERROR_NO_PROMOTION_ACTIVE = 8222;

    // =8223 unused
    //
    // MessageId: ERROR_DS_OPERATIONS_ERROR
    //
    // MessageText:
    //
    // An operations error occurred.
    //
    int ERROR_DS_OPERATIONS_ERROR = 8224;

    //
    // MessageId: ERROR_DS_PROTOCOL_ERROR
    //
    // MessageText:
    //
    // A protocol error occurred.
    //
    int ERROR_DS_PROTOCOL_ERROR = 8225;

    //
    // MessageId: ERROR_DS_TIMELIMIT_EXCEEDED
    //
    // MessageText:
    //
    // The time limit for this request was exceeded.
    //
    int ERROR_DS_TIMELIMIT_EXCEEDED = 8226;

    //
    // MessageId: ERROR_DS_SIZELIMIT_EXCEEDED
    //
    // MessageText:
    //
    // The size limit for this request was exceeded.
    //
    int ERROR_DS_SIZELIMIT_EXCEEDED = 8227;

    //
    // MessageId: ERROR_DS_ADMIN_LIMIT_EXCEEDED
    //
    // MessageText:
    //
    // The administrative limit for this request was exceeded.
    //
    int ERROR_DS_ADMIN_LIMIT_EXCEEDED = 8228;

    //
    // MessageId: ERROR_DS_COMPARE_FALSE
    //
    // MessageText:
    //
    // The compare response was false.
    //
    int ERROR_DS_COMPARE_FALSE = 8229;

    //
    // MessageId: ERROR_DS_COMPARE_TRUE
    //
    // MessageText:
    //
    // The compare response was true.
    //
    int ERROR_DS_COMPARE_TRUE = 8230;

    //
    // MessageId: ERROR_DS_AUTH_METHOD_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The requested authentication method is not supported by the server.
    //
    int ERROR_DS_AUTH_METHOD_NOT_SUPPORTED = 8231;

    //
    // MessageId: ERROR_DS_STRONG_AUTH_REQUIRED
    //
    // MessageText:
    //
    // A more secure authentication method is required for this server.
    //
    int ERROR_DS_STRONG_AUTH_REQUIRED = 8232;

    //
    // MessageId: ERROR_DS_INAPPROPRIATE_AUTH
    //
    // MessageText:
    //
    // Inappropriate authentication.
    //
    int ERROR_DS_INAPPROPRIATE_AUTH = 8233;

    //
    // MessageId: ERROR_DS_AUTH_UNKNOWN
    //
    // MessageText:
    //
    // The authentication mechanism is unknown.
    //
    int ERROR_DS_AUTH_UNKNOWN = 8234;

    //
    // MessageId: ERROR_DS_REFERRAL
    //
    // MessageText:
    //
    // A referral was returned from the server.
    //
    int ERROR_DS_REFERRAL = 8235;

    //
    // MessageId: ERROR_DS_UNAVAILABLE_CRIT_EXTENSION
    //
    // MessageText:
    //
    // The server does not support the requested critical extension.
    //
    int ERROR_DS_UNAVAILABLE_CRIT_EXTENSION = 8236;

    //
    // MessageId: ERROR_DS_CONFIDENTIALITY_REQUIRED
    //
    // MessageText:
    //
    // This request requires a secure connection.
    //
    int ERROR_DS_CONFIDENTIALITY_REQUIRED = 8237;

    //
    // MessageId: ERROR_DS_INAPPROPRIATE_MATCHING
    //
    // MessageText:
    //
    // Inappropriate matching.
    //
    int ERROR_DS_INAPPROPRIATE_MATCHING = 8238;

    //
    // MessageId: ERROR_DS_CONSTRAINT_VIOLATION
    //
    // MessageText:
    //
    // A constraint violation occurred.
    //
    int ERROR_DS_CONSTRAINT_VIOLATION = 8239;

    //
    // MessageId: ERROR_DS_NO_SUCH_OBJECT
    //
    // MessageText:
    //
    // There is no such object on the server.
    //
    int ERROR_DS_NO_SUCH_OBJECT = 8240;

    //
    // MessageId: ERROR_DS_ALIAS_PROBLEM
    //
    // MessageText:
    //
    // There is an alias problem.
    //
    int ERROR_DS_ALIAS_PROBLEM = 8241;

    //
    // MessageId: ERROR_DS_INVALID_DN_SYNTAX
    //
    // MessageText:
    //
    // An invalid dn syntax has been specified.
    //
    int ERROR_DS_INVALID_DN_SYNTAX = 8242;

    //
    // MessageId: ERROR_DS_IS_LEAF
    //
    // MessageText:
    //
    // The object is a leaf object.
    //
    int ERROR_DS_IS_LEAF = 8243;

    //
    // MessageId: ERROR_DS_ALIAS_DEREF_PROBLEM
    //
    // MessageText:
    //
    // There is an alias dereferencing problem.
    //
    int ERROR_DS_ALIAS_DEREF_PROBLEM = 8244;

    //
    // MessageId: ERROR_DS_UNWILLING_TO_PERFORM
    //
    // MessageText:
    //
    // The server is unwilling to process the request.
    //
    int ERROR_DS_UNWILLING_TO_PERFORM = 8245;

    //
    // MessageId: ERROR_DS_LOOP_DETECT
    //
    // MessageText:
    //
    // A loop has been detected.
    //
    int ERROR_DS_LOOP_DETECT = 8246;

    //
    // MessageId: ERROR_DS_NAMING_VIOLATION
    //
    // MessageText:
    //
    // There is a naming violation.
    //
    int ERROR_DS_NAMING_VIOLATION = 8247;

    //
    // MessageId: ERROR_DS_OBJECT_RESULTS_TOO_LARGE
    //
    // MessageText:
    //
    // The result set is too large.
    //
    int ERROR_DS_OBJECT_RESULTS_TOO_LARGE = 8248;

    //
    // MessageId: ERROR_DS_AFFECTS_MULTIPLE_DSAS
    //
    // MessageText:
    //
    // The operation affects multiple DSAs
    //
    int ERROR_DS_AFFECTS_MULTIPLE_DSAS = 8249;

    //
    // MessageId: ERROR_DS_SERVER_DOWN
    //
    // MessageText:
    //
    // The server is not operational.
    //
    int ERROR_DS_SERVER_DOWN = 8250;

    //
    // MessageId: ERROR_DS_LOCAL_ERROR
    //
    // MessageText:
    //
    // A local error has occurred.
    //
    int ERROR_DS_LOCAL_ERROR = 8251;

    //
    // MessageId: ERROR_DS_ENCODING_ERROR
    //
    // MessageText:
    //
    // An encoding error has occurred.
    //
    int ERROR_DS_ENCODING_ERROR = 8252;

    //
    // MessageId: ERROR_DS_DECODING_ERROR
    //
    // MessageText:
    //
    // A decoding error has occurred.
    //
    int ERROR_DS_DECODING_ERROR = 8253;

    //
    // MessageId: ERROR_DS_FILTER_UNKNOWN
    //
    // MessageText:
    //
    // The search filter cannot be recognized.
    //
    int ERROR_DS_FILTER_UNKNOWN = 8254;

    //
    // MessageId: ERROR_DS_PARAM_ERROR
    //
    // MessageText:
    //
    // One or more parameters are illegal.
    //
    int ERROR_DS_PARAM_ERROR = 8255;

    //
    // MessageId: ERROR_DS_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The specified method is not supported.
    //
    int ERROR_DS_NOT_SUPPORTED = 8256;

    //
    // MessageId: ERROR_DS_NO_RESULTS_RETURNED
    //
    // MessageText:
    //
    // No results were returned.
    //
    int ERROR_DS_NO_RESULTS_RETURNED = 8257;

    //
    // MessageId: ERROR_DS_CONTROL_NOT_FOUND
    //
    // MessageText:
    //
    // The specified control is not supported by the server.
    //
    int ERROR_DS_CONTROL_NOT_FOUND = 8258;

    //
    // MessageId: ERROR_DS_CLIENT_LOOP
    //
    // MessageText:
    //
    // A referral loop was detected by the client.
    //
    int ERROR_DS_CLIENT_LOOP = 8259;

    //
    // MessageId: ERROR_DS_REFERRAL_LIMIT_EXCEEDED
    //
    // MessageText:
    //
    // The preset referral limit was exceeded.
    //
    int ERROR_DS_REFERRAL_LIMIT_EXCEEDED = 8260;

    //
    // MessageId: ERROR_DS_SORT_CONTROL_MISSING
    //
    // MessageText:
    //
    // The search requires a SORT control.
    //
    int ERROR_DS_SORT_CONTROL_MISSING = 8261;

    //
    // MessageId: ERROR_DS_OFFSET_RANGE_ERROR
    //
    // MessageText:
    //
    // The search results exceed the offset range specified.
    //
    int ERROR_DS_OFFSET_RANGE_ERROR = 8262;

    //
    // MessageId: ERROR_DS_ROOT_MUST_BE_NC
    //
    // MessageText:
    //
    // The root object must be the head of a naming context. The root object
    // cannot have an instantiated parent.
    //
    int ERROR_DS_ROOT_MUST_BE_NC = 8301;

    //
    // MessageId: ERROR_DS_ADD_REPLICA_INHIBITED
    //
    // MessageText:
    //
    // The add replica operation cannot be performed. The naming context must be
    // writeable in order to create the replica.
    //
    int ERROR_DS_ADD_REPLICA_INHIBITED = 8302;

    //
    // MessageId: ERROR_DS_ATT_NOT_DEF_IN_SCHEMA
    //
    // MessageText:
    //
    // A reference to an attribute that is not defined in the schema occurred.
    //
    int ERROR_DS_ATT_NOT_DEF_IN_SCHEMA = 8303;

    //
    // MessageId: ERROR_DS_MAX_OBJ_SIZE_EXCEEDED
    //
    // MessageText:
    //
    // The maximum size of an object has been exceeded.
    //
    int ERROR_DS_MAX_OBJ_SIZE_EXCEEDED = 8304;

    //
    // MessageId: ERROR_DS_OBJ_STRING_NAME_EXISTS
    //
    // MessageText:
    //
    // An attempt was made to add an object to the directory with a name that is
    // already in use.
    //
    int ERROR_DS_OBJ_STRING_NAME_EXISTS = 8305;

    //
    // MessageId: ERROR_DS_NO_RDN_DEFINED_IN_SCHEMA
    //
    // MessageText:
    //
    // An attempt was made to add an object of a class that does not have an RDN
    // defined in the schema.
    //
    int ERROR_DS_NO_RDN_DEFINED_IN_SCHEMA = 8306;

    //
    // MessageId: ERROR_DS_RDN_DOESNT_MATCH_SCHEMA
    //
    // MessageText:
    //
    // An attempt was made to add an object using an RDN that is not the RDN
    // defined in the schema.
    //
    int ERROR_DS_RDN_DOESNT_MATCH_SCHEMA = 8307;

    //
    // MessageId: ERROR_DS_NO_REQUESTED_ATTS_FOUND
    //
    // MessageText:
    //
    // None of the requested attributes were found on the objects.
    //
    int ERROR_DS_NO_REQUESTED_ATTS_FOUND = 8308;

    //
    // MessageId: ERROR_DS_USER_BUFFER_TO_SMALL
    //
    // MessageText:
    //
    // The user buffer is too small.
    //
    int ERROR_DS_USER_BUFFER_TO_SMALL = 8309;

    //
    // MessageId: ERROR_DS_ATT_IS_NOT_ON_OBJ
    //
    // MessageText:
    //
    // The attribute specified in the operation is not present on the object.
    //
    int ERROR_DS_ATT_IS_NOT_ON_OBJ = 8310;

    //
    // MessageId: ERROR_DS_ILLEGAL_MOD_OPERATION
    //
    // MessageText:
    //
    // Illegal modify operation. Some aspect of the modification is not
    // permitted.
    //
    int ERROR_DS_ILLEGAL_MOD_OPERATION = 8311;

    //
    // MessageId: ERROR_DS_OBJ_TOO_LARGE
    //
    // MessageText:
    //
    // The specified object is too large.
    //
    int ERROR_DS_OBJ_TOO_LARGE = 8312;

    //
    // MessageId: ERROR_DS_BAD_INSTANCE_TYPE
    //
    // MessageText:
    //
    // The specified instance type is not valid.
    //
    int ERROR_DS_BAD_INSTANCE_TYPE = 8313;

    //
    // MessageId: ERROR_DS_MASTERDSA_REQUIRED
    //
    // MessageText:
    //
    // The operation must be performed at a master DSA.
    //
    int ERROR_DS_MASTERDSA_REQUIRED = 8314;

    //
    // MessageId: ERROR_DS_OBJECT_CLASS_REQUIRED
    //
    // MessageText:
    //
    // The object class attribute must be specified.
    //
    int ERROR_DS_OBJECT_CLASS_REQUIRED = 8315;

    //
    // MessageId: ERROR_DS_MISSING_REQUIRED_ATT
    //
    // MessageText:
    //
    // A required attribute is missing.
    //
    int ERROR_DS_MISSING_REQUIRED_ATT = 8316;

    //
    // MessageId: ERROR_DS_ATT_NOT_DEF_FOR_CLASS
    //
    // MessageText:
    //
    // An attempt was made to modify an object to include an attribute that is
    // not legal for its class.
    //
    int ERROR_DS_ATT_NOT_DEF_FOR_CLASS = 8317;

    //
    // MessageId: ERROR_DS_ATT_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The specified attribute is already present on the object.
    //
    int ERROR_DS_ATT_ALREADY_EXISTS = 8318;

    // =8319 unused
    //
    // MessageId: ERROR_DS_CANT_ADD_ATT_VALUES
    //
    // MessageText:
    //
    // The specified attribute is not present, or has no values.
    //
    int ERROR_DS_CANT_ADD_ATT_VALUES = 8320;

    //
    // MessageId: ERROR_DS_SINGLE_VALUE_CONSTRAINT
    //
    // MessageText:
    //
    // Multiple values were specified for an attribute that can have only one
    // value.
    //
    int ERROR_DS_SINGLE_VALUE_CONSTRAINT = 8321;

    //
    // MessageId: ERROR_DS_RANGE_CONSTRAINT
    //
    // MessageText:
    //
    // A value for the attribute was not in the acceptable range of values.
    //
    int ERROR_DS_RANGE_CONSTRAINT = 8322;

    //
    // MessageId: ERROR_DS_ATT_VAL_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The specified value already exists.
    //
    int ERROR_DS_ATT_VAL_ALREADY_EXISTS = 8323;

    //
    // MessageId: ERROR_DS_CANT_REM_MISSING_ATT
    //
    // MessageText:
    //
    // The attribute cannot be removed because it is not present on the object.
    //
    int ERROR_DS_CANT_REM_MISSING_ATT = 8324;

    //
    // MessageId: ERROR_DS_CANT_REM_MISSING_ATT_VAL
    //
    // MessageText:
    //
    // The attribute value cannot be removed because it is not present on the
    // object.
    //
    int ERROR_DS_CANT_REM_MISSING_ATT_VAL = 8325;

    //
    // MessageId: ERROR_DS_ROOT_CANT_BE_SUBREF
    //
    // MessageText:
    //
    // The specified root object cannot be a subref.
    //
    int ERROR_DS_ROOT_CANT_BE_SUBREF = 8326;

    //
    // MessageId: ERROR_DS_NO_CHAINING
    //
    // MessageText:
    //
    // Chaining is not permitted.
    //
    int ERROR_DS_NO_CHAINING = 8327;

    //
    // MessageId: ERROR_DS_NO_CHAINED_EVAL
    //
    // MessageText:
    //
    // Chained evaluation is not permitted.
    //
    int ERROR_DS_NO_CHAINED_EVAL = 8328;

    //
    // MessageId: ERROR_DS_NO_PARENT_OBJECT
    //
    // MessageText:
    //
    // The operation could not be performed because the object's parent is
    // either uninstantiated or deleted.
    //
    int ERROR_DS_NO_PARENT_OBJECT = 8329;

    //
    // MessageId: ERROR_DS_PARENT_IS_AN_ALIAS
    //
    // MessageText:
    //
    // Having a parent that is an alias is not permitted. Aliases are leaf
    // objects.
    //
    int ERROR_DS_PARENT_IS_AN_ALIAS = 8330;

    //
    // MessageId: ERROR_DS_CANT_MIX_MASTER_AND_REPS
    //
    // MessageText:
    //
    // The object and parent must be of the same type, either both masters or
    // both replicas.
    //
    int ERROR_DS_CANT_MIX_MASTER_AND_REPS = 8331;

    //
    // MessageId: ERROR_DS_CHILDREN_EXIST
    //
    // MessageText:
    //
    // The operation cannot be performed because child objects exist. This
    // operation can only be performed on a leaf object.
    //
    int ERROR_DS_CHILDREN_EXIST = 8332;

    //
    // MessageId: ERROR_DS_OBJ_NOT_FOUND
    //
    // MessageText:
    //
    // Directory object not found.
    //
    int ERROR_DS_OBJ_NOT_FOUND = 8333;

    //
    // MessageId: ERROR_DS_ALIASED_OBJ_MISSING
    //
    // MessageText:
    //
    // The aliased object is missing.
    //
    int ERROR_DS_ALIASED_OBJ_MISSING = 8334;

    //
    // MessageId: ERROR_DS_BAD_NAME_SYNTAX
    //
    // MessageText:
    //
    // The object name has bad syntax.
    //
    int ERROR_DS_BAD_NAME_SYNTAX = 8335;

    //
    // MessageId: ERROR_DS_ALIAS_POINTS_TO_ALIAS
    //
    // MessageText:
    //
    // It is not permitted for an alias to refer to another alias.
    //
    int ERROR_DS_ALIAS_POINTS_TO_ALIAS = 8336;

    //
    // MessageId: ERROR_DS_CANT_DEREF_ALIAS
    //
    // MessageText:
    //
    // The alias cannot be dereferenced.
    //
    int ERROR_DS_CANT_DEREF_ALIAS = 8337;

    //
    // MessageId: ERROR_DS_OUT_OF_SCOPE
    //
    // MessageText:
    //
    // The operation is out of scope.
    //
    int ERROR_DS_OUT_OF_SCOPE = 8338;

    //
    // MessageId: ERROR_DS_OBJECT_BEING_REMOVED
    //
    // MessageText:
    //
    // The operation cannot continue because the object is in the process of
    // being removed.
    //
    int ERROR_DS_OBJECT_BEING_REMOVED = 8339;

    //
    // MessageId: ERROR_DS_CANT_DELETE_DSA_OBJ
    //
    // MessageText:
    //
    // The DSA object cannot be deleted.
    //
    int ERROR_DS_CANT_DELETE_DSA_OBJ = 8340;

    //
    // MessageId: ERROR_DS_GENERIC_ERROR
    //
    // MessageText:
    //
    // A directory service error has occurred.
    //
    int ERROR_DS_GENERIC_ERROR = 8341;

    //
    // MessageId: ERROR_DS_DSA_MUST_BE_INT_MASTER
    //
    // MessageText:
    //
    // The operation can only be performed on an internal master DSA object.
    //
    int ERROR_DS_DSA_MUST_BE_INT_MASTER = 8342;

    //
    // MessageId: ERROR_DS_CLASS_NOT_DSA
    //
    // MessageText:
    //
    // The object must be of class DSA.
    //
    int ERROR_DS_CLASS_NOT_DSA = 8343;

    //
    // MessageId: ERROR_DS_INSUFF_ACCESS_RIGHTS
    //
    // MessageText:
    //
    // Insufficient access rights to perform the operation.
    //
    int ERROR_DS_INSUFF_ACCESS_RIGHTS = 8344;

    //
    // MessageId: ERROR_DS_ILLEGAL_SUPERIOR
    //
    // MessageText:
    //
    // The object cannot be added because the parent is not on the list of
    // possible superiors.
    //
    int ERROR_DS_ILLEGAL_SUPERIOR = 8345;

    //
    // MessageId: ERROR_DS_ATTRIBUTE_OWNED_BY_SAM
    //
    // MessageText:
    //
    // Access to the attribute is not permitted because the attribute is owned
    // by the Security Accounts Manager (SAM).
    //
    int ERROR_DS_ATTRIBUTE_OWNED_BY_SAM = 8346;

    //
    // MessageId: ERROR_DS_NAME_TOO_MANY_PARTS
    //
    // MessageText:
    //
    // The name has too many parts.
    //
    int ERROR_DS_NAME_TOO_MANY_PARTS = 8347;

    //
    // MessageId: ERROR_DS_NAME_TOO_LONG
    //
    // MessageText:
    //
    // The name is too long.
    //
    int ERROR_DS_NAME_TOO_LONG = 8348;

    //
    // MessageId: ERROR_DS_NAME_VALUE_TOO_LONG
    //
    // MessageText:
    //
    // The name value is too long.
    //
    int ERROR_DS_NAME_VALUE_TOO_LONG = 8349;

    //
    // MessageId: ERROR_DS_NAME_UNPARSEABLE
    //
    // MessageText:
    //
    // The directory service encountered an error parsing a name.
    //
    int ERROR_DS_NAME_UNPARSEABLE = 8350;

    //
    // MessageId: ERROR_DS_NAME_TYPE_UNKNOWN
    //
    // MessageText:
    //
    // The directory service cannot get the attribute type for a name.
    //
    int ERROR_DS_NAME_TYPE_UNKNOWN = 8351;

    //
    // MessageId: ERROR_DS_NOT_AN_OBJECT
    //
    // MessageText:
    //
    // The name does not identify an object; the name identifies a phantom.
    //
    int ERROR_DS_NOT_AN_OBJECT = 8352;

    //
    // MessageId: ERROR_DS_SEC_DESC_TOO_SHORT
    //
    // MessageText:
    //
    // The security descriptor is too short.
    //
    int ERROR_DS_SEC_DESC_TOO_SHORT = 8353;

    //
    // MessageId: ERROR_DS_SEC_DESC_INVALID
    //
    // MessageText:
    //
    // The security descriptor is invalid.
    //
    int ERROR_DS_SEC_DESC_INVALID = 8354;

    //
    // MessageId: ERROR_DS_NO_DELETED_NAME
    //
    // MessageText:
    //
    // Failed to create name for deleted object.
    //
    int ERROR_DS_NO_DELETED_NAME = 8355;

    //
    // MessageId: ERROR_DS_SUBREF_MUST_HAVE_PARENT
    //
    // MessageText:
    //
    // The parent of a new subref must exist.
    //
    int ERROR_DS_SUBREF_MUST_HAVE_PARENT = 8356;

    //
    // MessageId: ERROR_DS_NCNAME_MUST_BE_NC
    //
    // MessageText:
    //
    // The object must be a naming context.
    //
    int ERROR_DS_NCNAME_MUST_BE_NC = 8357;

    //
    // MessageId: ERROR_DS_CANT_ADD_SYSTEM_ONLY
    //
    // MessageText:
    //
    // It is not permitted to add an attribute which is owned by the system.
    //
    int ERROR_DS_CANT_ADD_SYSTEM_ONLY = 8358;

    //
    // MessageId: ERROR_DS_CLASS_MUST_BE_CONCRETE
    //
    // MessageText:
    //
    // The class of the object must be structural; you cannot instantiate an
    // abstract class.
    //
    int ERROR_DS_CLASS_MUST_BE_CONCRETE = 8359;

    //
    // MessageId: ERROR_DS_INVALID_DMD
    //
    // MessageText:
    //
    // The schema object could not be found.
    //
    int ERROR_DS_INVALID_DMD = 8360;

    //
    // MessageId: ERROR_DS_OBJ_GUID_EXISTS
    //
    // MessageText:
    //
    // A local object with this GUID (dead or alive) already exists.
    //
    int ERROR_DS_OBJ_GUID_EXISTS = 8361;

    //
    // MessageId: ERROR_DS_NOT_ON_BACKLINK
    //
    // MessageText:
    //
    // The operation cannot be performed on a back link.
    //
    int ERROR_DS_NOT_ON_BACKLINK = 8362;

    //
    // MessageId: ERROR_DS_NO_CROSSREF_FOR_NC
    //
    // MessageText:
    //
    // The cross reference for the specified naming context could not be found.
    //
    int ERROR_DS_NO_CROSSREF_FOR_NC = 8363;

    //
    // MessageId: ERROR_DS_SHUTTING_DOWN
    //
    // MessageText:
    //
    // The operation could not be performed because the directory service is
    // shutting down.
    //
    int ERROR_DS_SHUTTING_DOWN = 8364;

    //
    // MessageId: ERROR_DS_UNKNOWN_OPERATION
    //
    // MessageText:
    //
    // The directory service request is invalid.
    //
    int ERROR_DS_UNKNOWN_OPERATION = 8365;

    //
    // MessageId: ERROR_DS_INVALID_ROLE_OWNER
    //
    // MessageText:
    //
    // The role owner attribute could not be read.
    //
    int ERROR_DS_INVALID_ROLE_OWNER = 8366;

    //
    // MessageId: ERROR_DS_COULDNT_CONTACT_FSMO
    //
    // MessageText:
    //
    // The requested FSMO operation failed. The current FSMO holder could not be
    // contacted.
    //
    int ERROR_DS_COULDNT_CONTACT_FSMO = 8367;

    //
    // MessageId: ERROR_DS_CROSS_NC_DN_RENAME
    //
    // MessageText:
    //
    // Modification of a DN across a naming context is not permitted.
    //
    int ERROR_DS_CROSS_NC_DN_RENAME = 8368;

    //
    // MessageId: ERROR_DS_CANT_MOD_SYSTEM_ONLY
    //
    // MessageText:
    //
    // The attribute cannot be modified because it is owned by the system.
    //
    int ERROR_DS_CANT_MOD_SYSTEM_ONLY = 8369;

    //
    // MessageId: ERROR_DS_REPLICATOR_ONLY
    //
    // MessageText:
    //
    // Only the replicator can perform this function.
    //
    int ERROR_DS_REPLICATOR_ONLY = 8370;

    //
    // MessageId: ERROR_DS_OBJ_CLASS_NOT_DEFINED
    //
    // MessageText:
    //
    // The specified class is not defined.
    //
    int ERROR_DS_OBJ_CLASS_NOT_DEFINED = 8371;

    //
    // MessageId: ERROR_DS_OBJ_CLASS_NOT_SUBCLASS
    //
    // MessageText:
    //
    // The specified class is not a subclass.
    //
    int ERROR_DS_OBJ_CLASS_NOT_SUBCLASS = 8372;

    //
    // MessageId: ERROR_DS_NAME_REFERENCE_INVALID
    //
    // MessageText:
    //
    // The name reference is invalid.
    //
    int ERROR_DS_NAME_REFERENCE_INVALID = 8373;

    //
    // MessageId: ERROR_DS_CROSS_REF_EXISTS
    //
    // MessageText:
    //
    // A cross reference already exists.
    //
    int ERROR_DS_CROSS_REF_EXISTS = 8374;

    //
    // MessageId: ERROR_DS_CANT_DEL_MASTER_CROSSREF
    //
    // MessageText:
    //
    // It is not permitted to delete a master cross reference.
    //
    int ERROR_DS_CANT_DEL_MASTER_CROSSREF = 8375;

    //
    // MessageId: ERROR_DS_SUBTREE_NOTIFY_NOT_NC_HEAD
    //
    // MessageText:
    //
    // Subtree notifications are only supported on NC heads.
    //
    int ERROR_DS_SUBTREE_NOTIFY_NOT_NC_HEAD = 8376;

    //
    // MessageId: ERROR_DS_NOTIFY_FILTER_TOO_COMPLEX
    //
    // MessageText:
    //
    // Notification filter is too complex.
    //
    int ERROR_DS_NOTIFY_FILTER_TOO_COMPLEX = 8377;

    //
    // MessageId: ERROR_DS_DUP_RDN
    //
    // MessageText:
    //
    // Schema update failed: duplicate RDN.
    //
    int ERROR_DS_DUP_RDN = 8378;

    //
    // MessageId: ERROR_DS_DUP_OID
    //
    // MessageText:
    //
    // Schema update failed: duplicate OID.
    //
    int ERROR_DS_DUP_OID = 8379;

    //
    // MessageId: ERROR_DS_DUP_MAPI_ID
    //
    // MessageText:
    //
    // Schema update failed: duplicate MAPI identifier.
    //
    int ERROR_DS_DUP_MAPI_ID = 8380;

    //
    // MessageId: ERROR_DS_DUP_SCHEMA_ID_GUID
    //
    // MessageText:
    //
    // Schema update failed: duplicate schema-id GUID.
    //
    int ERROR_DS_DUP_SCHEMA_ID_GUID = 8381;

    //
    // MessageId: ERROR_DS_DUP_LDAP_DISPLAY_NAME
    //
    // MessageText:
    //
    // Schema update failed: duplicate LDAP display name.
    //
    int ERROR_DS_DUP_LDAP_DISPLAY_NAME = 8382;

    //
    // MessageId: ERROR_DS_SEMANTIC_ATT_TEST
    //
    // MessageText:
    //
    // Schema update failed: range-lower less than range upper.
    //
    int ERROR_DS_SEMANTIC_ATT_TEST = 8383;

    //
    // MessageId: ERROR_DS_SYNTAX_MISMATCH
    //
    // MessageText:
    //
    // Schema update failed: syntax mismatch.
    //
    int ERROR_DS_SYNTAX_MISMATCH = 8384;

    //
    // MessageId: ERROR_DS_EXISTS_IN_MUST_HAVE
    //
    // MessageText:
    //
    // Schema deletion failed: attribute is used in must-contain.
    //
    int ERROR_DS_EXISTS_IN_MUST_HAVE = 8385;

    //
    // MessageId: ERROR_DS_EXISTS_IN_MAY_HAVE
    //
    // MessageText:
    //
    // Schema deletion failed: attribute is used in may-contain.
    //
    int ERROR_DS_EXISTS_IN_MAY_HAVE = 8386;

    //
    // MessageId: ERROR_DS_NONEXISTENT_MAY_HAVE
    //
    // MessageText:
    //
    // Schema update failed: attribute in may-contain does not exist.
    //
    int ERROR_DS_NONEXISTENT_MAY_HAVE = 8387;

    //
    // MessageId: ERROR_DS_NONEXISTENT_MUST_HAVE
    //
    // MessageText:
    //
    // Schema update failed: attribute in must-contain does not exist.
    //
    int ERROR_DS_NONEXISTENT_MUST_HAVE = 8388;

    //
    // MessageId: ERROR_DS_AUX_CLS_TEST_FAIL
    //
    // MessageText:
    //
    // Schema update failed: class in aux-class list does not exist or is not an
    // auxiliary class.
    //
    int ERROR_DS_AUX_CLS_TEST_FAIL = 8389;

    //
    // MessageId: ERROR_DS_NONEXISTENT_POSS_SUP
    //
    // MessageText:
    //
    // Schema update failed: class in poss-superiors does not exist.
    //
    int ERROR_DS_NONEXISTENT_POSS_SUP = 8390;

    //
    // MessageId: ERROR_DS_SUB_CLS_TEST_FAIL
    //
    // MessageText:
    //
    // Schema update failed: class in subclassof list does not exist or does not
    // satisfy hierarchy rules.
    //
    int ERROR_DS_SUB_CLS_TEST_FAIL = 8391;

    //
    // MessageId: ERROR_DS_BAD_RDN_ATT_ID_SYNTAX
    //
    // MessageText:
    //
    // Schema update failed: Rdn-Att-Id has wrong syntax.
    //
    int ERROR_DS_BAD_RDN_ATT_ID_SYNTAX = 8392;

    //
    // MessageId: ERROR_DS_EXISTS_IN_AUX_CLS
    //
    // MessageText:
    //
    // Schema deletion failed: class is used as auxiliary class.
    //
    int ERROR_DS_EXISTS_IN_AUX_CLS = 8393;

    //
    // MessageId: ERROR_DS_EXISTS_IN_SUB_CLS
    //
    // MessageText:
    //
    // Schema deletion failed: class is used as sub class.
    //
    int ERROR_DS_EXISTS_IN_SUB_CLS = 8394;

    //
    // MessageId: ERROR_DS_EXISTS_IN_POSS_SUP
    //
    // MessageText:
    //
    // Schema deletion failed: class is used as poss superior.
    //
    int ERROR_DS_EXISTS_IN_POSS_SUP = 8395;

    //
    // MessageId: ERROR_DS_RECALCSCHEMA_FAILED
    //
    // MessageText:
    //
    // Schema update failed in recalculating validation cache.
    //
    int ERROR_DS_RECALCSCHEMA_FAILED = 8396;

    //
    // MessageId: ERROR_DS_TREE_DELETE_NOT_FINISHED
    //
    // MessageText:
    //
    // The tree deletion is not finished. The request must be made again to
    // continue deleting the tree.
    //
    int ERROR_DS_TREE_DELETE_NOT_FINISHED = 8397;

    //
    // MessageId: ERROR_DS_CANT_DELETE
    //
    // MessageText:
    //
    // The requested delete operation could not be performed.
    //
    int ERROR_DS_CANT_DELETE = 8398;

    //
    // MessageId: ERROR_DS_ATT_SCHEMA_REQ_ID
    //
    // MessageText:
    //
    // Cannot read the governs class identifier for the schema record.
    //
    int ERROR_DS_ATT_SCHEMA_REQ_ID = 8399;

    //
    // MessageId: ERROR_DS_BAD_ATT_SCHEMA_SYNTAX
    //
    // MessageText:
    //
    // The attribute schema has bad syntax.
    //
    int ERROR_DS_BAD_ATT_SCHEMA_SYNTAX = 8400;

    //
    // MessageId: ERROR_DS_CANT_CACHE_ATT
    //
    // MessageText:
    //
    // The attribute could not be cached.
    //
    int ERROR_DS_CANT_CACHE_ATT = 8401;

    //
    // MessageId: ERROR_DS_CANT_CACHE_CLASS
    //
    // MessageText:
    //
    // The class could not be cached.
    //
    int ERROR_DS_CANT_CACHE_CLASS = 8402;

    //
    // MessageId: ERROR_DS_CANT_REMOVE_ATT_CACHE
    //
    // MessageText:
    //
    // The attribute could not be removed from the cache.
    //
    int ERROR_DS_CANT_REMOVE_ATT_CACHE = 8403;

    //
    // MessageId: ERROR_DS_CANT_REMOVE_CLASS_CACHE
    //
    // MessageText:
    //
    // The class could not be removed from the cache.
    //
    int ERROR_DS_CANT_REMOVE_CLASS_CACHE = 8404;

    //
    // MessageId: ERROR_DS_CANT_RETRIEVE_DN
    //
    // MessageText:
    //
    // The distinguished name attribute could not be read.
    //
    int ERROR_DS_CANT_RETRIEVE_DN = 8405;

    //
    // MessageId: ERROR_DS_MISSING_SUPREF
    //
    // MessageText:
    //
    // No superior reference has been configured for the directory service. The
    // directory service is therefore unable to issue referrals to objects
    // outside this forest.
    //
    int ERROR_DS_MISSING_SUPREF = 8406;

    //
    // MessageId: ERROR_DS_CANT_RETRIEVE_INSTANCE
    //
    // MessageText:
    //
    // The instance type attribute could not be retrieved.
    //
    int ERROR_DS_CANT_RETRIEVE_INSTANCE = 8407;

    //
    // MessageId: ERROR_DS_CODE_INCONSISTENCY
    //
    // MessageText:
    //
    // An internal error has occurred.
    //
    int ERROR_DS_CODE_INCONSISTENCY = 8408;

    //
    // MessageId: ERROR_DS_DATABASE_ERROR
    //
    // MessageText:
    //
    // A database error has occurred.
    //
    int ERROR_DS_DATABASE_ERROR = 8409;

    //
    // MessageId: ERROR_DS_GOVERNSID_MISSING
    //
    // MessageText:
    //
    // The attribute GOVERNSID is missing.
    //
    int ERROR_DS_GOVERNSID_MISSING = 8410;

    //
    // MessageId: ERROR_DS_MISSING_EXPECTED_ATT
    //
    // MessageText:
    //
    // An expected attribute is missing.
    //
    int ERROR_DS_MISSING_EXPECTED_ATT = 8411;

    //
    // MessageId: ERROR_DS_NCNAME_MISSING_CR_REF
    //
    // MessageText:
    //
    // The specified naming context is missing a cross reference.
    //
    int ERROR_DS_NCNAME_MISSING_CR_REF = 8412;

    //
    // MessageId: ERROR_DS_SECURITY_CHECKING_ERROR
    //
    // MessageText:
    //
    // A security checking error has occurred.
    //
    int ERROR_DS_SECURITY_CHECKING_ERROR = 8413;

    //
    // MessageId: ERROR_DS_SCHEMA_NOT_LOADED
    //
    // MessageText:
    //
    // The schema is not loaded.
    //
    int ERROR_DS_SCHEMA_NOT_LOADED = 8414;

    //
    // MessageId: ERROR_DS_SCHEMA_ALLOC_FAILED
    //
    // MessageText:
    //
    // Schema allocation failed. Please check if the machine is running low on
    // memory.
    //
    int ERROR_DS_SCHEMA_ALLOC_FAILED = 8415;

    //
    // MessageId: ERROR_DS_ATT_SCHEMA_REQ_SYNTAX
    //
    // MessageText:
    //
    // Failed to obtain the required syntax for the attribute schema.
    //
    int ERROR_DS_ATT_SCHEMA_REQ_SYNTAX = 8416;

    //
    // MessageId: ERROR_DS_GCVERIFY_ERROR
    //
    // MessageText:
    //
    // The global catalog verification failed. The global catalog is not
    // available or does not support the operation. Some part of the directory
    // is currently not available.
    //
    int ERROR_DS_GCVERIFY_ERROR = 8417;

    //
    // MessageId: ERROR_DS_DRA_SCHEMA_MISMATCH
    //
    // MessageText:
    //
    // The replication operation failed because of a schema mismatch between the
    // servers involved.
    //
    int ERROR_DS_DRA_SCHEMA_MISMATCH = 8418;

    //
    // MessageId: ERROR_DS_CANT_FIND_DSA_OBJ
    //
    // MessageText:
    //
    // The DSA object could not be found.
    //
    int ERROR_DS_CANT_FIND_DSA_OBJ = 8419;

    //
    // MessageId: ERROR_DS_CANT_FIND_EXPECTED_NC
    //
    // MessageText:
    //
    // The naming context could not be found.
    //
    int ERROR_DS_CANT_FIND_EXPECTED_NC = 8420;

    //
    // MessageId: ERROR_DS_CANT_FIND_NC_IN_CACHE
    //
    // MessageText:
    //
    // The naming context could not be found in the cache.
    //
    int ERROR_DS_CANT_FIND_NC_IN_CACHE = 8421;

    //
    // MessageId: ERROR_DS_CANT_RETRIEVE_CHILD
    //
    // MessageText:
    //
    // The child object could not be retrieved.
    //
    int ERROR_DS_CANT_RETRIEVE_CHILD = 8422;

    //
    // MessageId: ERROR_DS_SECURITY_ILLEGAL_MODIFY
    //
    // MessageText:
    //
    // The modification was not permitted for security reasons.
    //
    int ERROR_DS_SECURITY_ILLEGAL_MODIFY = 8423;

    //
    // MessageId: ERROR_DS_CANT_REPLACE_HIDDEN_REC
    //
    // MessageText:
    //
    // The operation cannot replace the hidden record.
    //
    int ERROR_DS_CANT_REPLACE_HIDDEN_REC = 8424;

    //
    // MessageId: ERROR_DS_BAD_HIERARCHY_FILE
    //
    // MessageText:
    //
    // The hierarchy file is invalid.
    //
    int ERROR_DS_BAD_HIERARCHY_FILE = 8425;

    //
    // MessageId: ERROR_DS_BUILD_HIERARCHY_TABLE_FAILED
    //
    // MessageText:
    //
    // The attempt to build the hierarchy table failed.
    //
    int ERROR_DS_BUILD_HIERARCHY_TABLE_FAILED = 8426;

    //
    // MessageId: ERROR_DS_CONFIG_PARAM_MISSING
    //
    // MessageText:
    //
    // The directory configuration parameter is missing from the registry.
    //
    int ERROR_DS_CONFIG_PARAM_MISSING = 8427;

    //
    // MessageId: ERROR_DS_COUNTING_AB_INDICES_FAILED
    //
    // MessageText:
    //
    // The attempt to count the address book indices failed.
    //
    int ERROR_DS_COUNTING_AB_INDICES_FAILED = 8428;

    //
    // MessageId: ERROR_DS_HIERARCHY_TABLE_MALLOC_FAILED
    //
    // MessageText:
    //
    // The allocation of the hierarchy table failed.
    //
    int ERROR_DS_HIERARCHY_TABLE_MALLOC_FAILED = 8429;

    //
    // MessageId: ERROR_DS_INTERNAL_FAILURE
    //
    // MessageText:
    //
    // The directory service encountered an internal failure.
    //
    int ERROR_DS_INTERNAL_FAILURE = 8430;

    //
    // MessageId: ERROR_DS_UNKNOWN_ERROR
    //
    // MessageText:
    //
    // The directory service encountered an unknown failure.
    //
    int ERROR_DS_UNKNOWN_ERROR = 8431;

    //
    // MessageId: ERROR_DS_ROOT_REQUIRES_CLASS_TOP
    //
    // MessageText:
    //
    // A root object requires a class of 'top'.
    //
    int ERROR_DS_ROOT_REQUIRES_CLASS_TOP = 8432;

    //
    // MessageId: ERROR_DS_REFUSING_FSMO_ROLES
    //
    // MessageText:
    //
    // This directory server is shutting down, and cannot take ownership of new
    // floating single-master operation roles.
    //
    int ERROR_DS_REFUSING_FSMO_ROLES = 8433;

    //
    // MessageId: ERROR_DS_MISSING_FSMO_SETTINGS
    //
    // MessageText:
    //
    // The directory service is missing mandatory configuration information, and
    // is unable to determine the ownership of floating single-master operation
    // roles.
    //
    int ERROR_DS_MISSING_FSMO_SETTINGS = 8434;

    //
    // MessageId: ERROR_DS_UNABLE_TO_SURRENDER_ROLES
    //
    // MessageText:
    //
    // The directory service was unable to transfer ownership of one or more
    // floating single-master operation roles to other servers.
    //
    int ERROR_DS_UNABLE_TO_SURRENDER_ROLES = 8435;

    //
    // MessageId: ERROR_DS_DRA_GENERIC
    //
    // MessageText:
    //
    // The replication operation failed.
    //
    int ERROR_DS_DRA_GENERIC = 8436;

    //
    // MessageId: ERROR_DS_DRA_INVALID_PARAMETER
    //
    // MessageText:
    //
    // An invalid parameter was specified for this replication operation.
    //
    int ERROR_DS_DRA_INVALID_PARAMETER = 8437;

    //
    // MessageId: ERROR_DS_DRA_BUSY
    //
    // MessageText:
    //
    // The directory service is too busy to complete the replication operation
    // at this time.
    //
    int ERROR_DS_DRA_BUSY = 8438;

    //
    // MessageId: ERROR_DS_DRA_BAD_DN
    //
    // MessageText:
    //
    // The distinguished name specified for this replication operation is
    // invalid.
    //
    int ERROR_DS_DRA_BAD_DN = 8439;

    //
    // MessageId: ERROR_DS_DRA_BAD_NC
    //
    // MessageText:
    //
    // The naming context specified for this replication operation is invalid.
    //
    int ERROR_DS_DRA_BAD_NC = 8440;

    //
    // MessageId: ERROR_DS_DRA_DN_EXISTS
    //
    // MessageText:
    //
    // The distinguished name specified for this replication operation already
    // exists.
    //
    int ERROR_DS_DRA_DN_EXISTS = 8441;

    //
    // MessageId: ERROR_DS_DRA_INTERNAL_ERROR
    //
    // MessageText:
    //
    // The replication system encountered an internal error.
    //
    int ERROR_DS_DRA_INTERNAL_ERROR = 8442;

    //
    // MessageId: ERROR_DS_DRA_INCONSISTENT_DIT
    //
    // MessageText:
    //
    // The replication operation encountered a database inconsistency.
    //
    int ERROR_DS_DRA_INCONSISTENT_DIT = 8443;

    //
    // MessageId: ERROR_DS_DRA_CONNECTION_FAILED
    //
    // MessageText:
    //
    // The server specified for this replication operation could not be
    // contacted.
    //
    int ERROR_DS_DRA_CONNECTION_FAILED = 8444;

    //
    // MessageId: ERROR_DS_DRA_BAD_INSTANCE_TYPE
    //
    // MessageText:
    //
    // The replication operation encountered an object with an invalid instance
    // type.
    //
    int ERROR_DS_DRA_BAD_INSTANCE_TYPE = 8445;

    //
    // MessageId: ERROR_DS_DRA_OUT_OF_MEM
    //
    // MessageText:
    //
    // The replication operation failed to allocate memory.
    //
    int ERROR_DS_DRA_OUT_OF_MEM = 8446;

    //
    // MessageId: ERROR_DS_DRA_MAIL_PROBLEM
    //
    // MessageText:
    //
    // The replication operation encountered an error with the mail system.
    //
    int ERROR_DS_DRA_MAIL_PROBLEM = 8447;

    //
    // MessageId: ERROR_DS_DRA_REF_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The replication reference information for the target server already
    // exists.
    //
    int ERROR_DS_DRA_REF_ALREADY_EXISTS = 8448;

    //
    // MessageId: ERROR_DS_DRA_REF_NOT_FOUND
    //
    // MessageText:
    //
    // The replication reference information for the target server does not
    // exist.
    //
    int ERROR_DS_DRA_REF_NOT_FOUND = 8449;

    //
    // MessageId: ERROR_DS_DRA_OBJ_IS_REP_SOURCE
    //
    // MessageText:
    //
    // The naming context cannot be removed because it is replicated to another
    // server.
    //
    int ERROR_DS_DRA_OBJ_IS_REP_SOURCE = 8450;

    //
    // MessageId: ERROR_DS_DRA_DB_ERROR
    //
    // MessageText:
    //
    // The replication operation encountered a database error.
    //
    int ERROR_DS_DRA_DB_ERROR = 8451;

    //
    // MessageId: ERROR_DS_DRA_NO_REPLICA
    //
    // MessageText:
    //
    // The naming context is in the process of being removed or is not
    // replicated from the specified server.
    //
    int ERROR_DS_DRA_NO_REPLICA = 8452;

    //
    // MessageId: ERROR_DS_DRA_ACCESS_DENIED
    //
    // MessageText:
    //
    // Replication access was denied.
    //
    int ERROR_DS_DRA_ACCESS_DENIED = 8453;

    //
    // MessageId: ERROR_DS_DRA_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The requested operation is not supported by this version of the directory
    // service.
    //
    int ERROR_DS_DRA_NOT_SUPPORTED = 8454;

    //
    // MessageId: ERROR_DS_DRA_RPC_CANCELLED
    //
    // MessageText:
    //
    // The replication remote procedure call was cancelled.
    //
    int ERROR_DS_DRA_RPC_CANCELLED = 8455;

    //
    // MessageId: ERROR_DS_DRA_SOURCE_DISABLED
    //
    // MessageText:
    //
    // The source server is currently rejecting replication requests.
    //
    int ERROR_DS_DRA_SOURCE_DISABLED = 8456;

    //
    // MessageId: ERROR_DS_DRA_SINK_DISABLED
    //
    // MessageText:
    //
    // The destination server is currently rejecting replication requests.
    //
    int ERROR_DS_DRA_SINK_DISABLED = 8457;

    //
    // MessageId: ERROR_DS_DRA_NAME_COLLISION
    //
    // MessageText:
    //
    // The replication operation failed due to a collision of object names.
    //
    int ERROR_DS_DRA_NAME_COLLISION = 8458;

    //
    // MessageId: ERROR_DS_DRA_SOURCE_REINSTALLED
    //
    // MessageText:
    //
    // The replication source has been reinstalled.
    //
    int ERROR_DS_DRA_SOURCE_REINSTALLED = 8459;

    //
    // MessageId: ERROR_DS_DRA_MISSING_PARENT
    //
    // MessageText:
    //
    // The replication operation failed because a required parent object is
    // missing.
    //
    int ERROR_DS_DRA_MISSING_PARENT = 8460;

    //
    // MessageId: ERROR_DS_DRA_PREEMPTED
    //
    // MessageText:
    //
    // The replication operation was preempted.
    //
    int ERROR_DS_DRA_PREEMPTED = 8461;

    //
    // MessageId: ERROR_DS_DRA_ABANDON_SYNC
    //
    // MessageText:
    //
    // The replication synchronization attempt was abandoned because of a lack
    // of updates.
    //
    int ERROR_DS_DRA_ABANDON_SYNC = 8462;

    //
    // MessageId: ERROR_DS_DRA_SHUTDOWN
    //
    // MessageText:
    //
    // The replication operation was terminated because the system is shutting
    // down.
    //
    int ERROR_DS_DRA_SHUTDOWN = 8463;

    //
    // MessageId: ERROR_DS_DRA_INCOMPATIBLE_PARTIAL_SET
    //
    // MessageText:
    //
    // Synchronization attempt failed because the destination DC is currently
    // waiting to synchronize new partial attributes from source. This condition
    // is normal if a recent schema change modified the partial attribute set.
    // The destination partial attribute set is not a subset of source partial
    // attribute set.
    //
    int ERROR_DS_DRA_INCOMPATIBLE_PARTIAL_SET = 8464;

    //
    // MessageId: ERROR_DS_DRA_SOURCE_IS_PARTIAL_REPLICA
    //
    // MessageText:
    //
    // The replication synchronization attempt failed because a master replica
    // attempted to sync from a partial replica.
    //
    int ERROR_DS_DRA_SOURCE_IS_PARTIAL_REPLICA = 8465;

    //
    // MessageId: ERROR_DS_DRA_EXTN_CONNECTION_FAILED
    //
    // MessageText:
    //
    // The server specified for this replication operation was contacted, but
    // that server was unable to contact an additional server needed to complete
    // the operation.
    //
    int ERROR_DS_DRA_EXTN_CONNECTION_FAILED = 8466;

    //
    // MessageId: ERROR_DS_INSTALL_SCHEMA_MISMATCH
    //
    // MessageText:
    //
    // The version of the directory service schema of the source forest is not
    // compatible with the version of directory service on this computer.
    //
    int ERROR_DS_INSTALL_SCHEMA_MISMATCH = 8467;

    //
    // MessageId: ERROR_DS_DUP_LINK_ID
    //
    // MessageText:
    //
    // Schema update failed: An attribute with the same link identifier already
    // exists.
    //
    int ERROR_DS_DUP_LINK_ID = 8468;

    //
    // MessageId: ERROR_DS_NAME_ERROR_RESOLVING
    //
    // MessageText:
    //
    // Name translation: Generic processing error.
    //
    int ERROR_DS_NAME_ERROR_RESOLVING = 8469;

    //
    // MessageId: ERROR_DS_NAME_ERROR_NOT_FOUND
    //
    // MessageText:
    //
    // Name translation: Could not find the name or insufficient right to see
    // name.
    //
    int ERROR_DS_NAME_ERROR_NOT_FOUND = 8470;

    //
    // MessageId: ERROR_DS_NAME_ERROR_NOT_UNIQUE
    //
    // MessageText:
    //
    // Name translation: Input name mapped to more than one output name.
    //
    int ERROR_DS_NAME_ERROR_NOT_UNIQUE = 8471;

    //
    // MessageId: ERROR_DS_NAME_ERROR_NO_MAPPING
    //
    // MessageText:
    //
    // Name translation: Input name found, but not the associated output format.
    //
    int ERROR_DS_NAME_ERROR_NO_MAPPING = 8472;

    //
    // MessageId: ERROR_DS_NAME_ERROR_DOMAIN_ONLY
    //
    // MessageText:
    //
    // Name translation: Unable to resolve completely, only the domain was
    // found.
    //
    int ERROR_DS_NAME_ERROR_DOMAIN_ONLY = 8473;

    //
    // MessageId: ERROR_DS_NAME_ERROR_NO_SYNTACTICAL_MAPPING
    //
    // MessageText:
    //
    // Name translation: Unable to perform purely syntactical mapping at the
    // client without going out to the wire.
    //
    int ERROR_DS_NAME_ERROR_NO_SYNTACTICAL_MAPPING = 8474;

    //
    // MessageId: ERROR_DS_CONSTRUCTED_ATT_MOD
    //
    // MessageText:
    //
    // Modification of a constructed attribute is not allowed.
    //
    int ERROR_DS_CONSTRUCTED_ATT_MOD = 8475;

    //
    // MessageId: ERROR_DS_WRONG_OM_OBJ_CLASS
    //
    // MessageText:
    //
    // The OM-Object-Class specified is incorrect for an attribute with the
    // specified syntax.
    //
    int ERROR_DS_WRONG_OM_OBJ_CLASS = 8476;

    //
    // MessageId: ERROR_DS_DRA_REPL_PENDING
    //
    // MessageText:
    //
    // The replication request has been posted; waiting for reply.
    //
    int ERROR_DS_DRA_REPL_PENDING = 8477;

    //
    // MessageId: ERROR_DS_DS_REQUIRED
    //
    // MessageText:
    //
    // The requested operation requires a directory service, and none was
    // available.
    //
    int ERROR_DS_DS_REQUIRED = 8478;

    //
    // MessageId: ERROR_DS_INVALID_LDAP_DISPLAY_NAME
    //
    // MessageText:
    //
    // The LDAP display name of the class or attribute contains non-ASCII
    // characters.
    //
    int ERROR_DS_INVALID_LDAP_DISPLAY_NAME = 8479;

    //
    // MessageId: ERROR_DS_NON_BASE_SEARCH
    //
    // MessageText:
    //
    // The requested search operation is only supported for base searches.
    //
    int ERROR_DS_NON_BASE_SEARCH = 8480;

    //
    // MessageId: ERROR_DS_CANT_RETRIEVE_ATTS
    //
    // MessageText:
    //
    // The search failed to retrieve attributes from the database.
    //
    int ERROR_DS_CANT_RETRIEVE_ATTS = 8481;

    //
    // MessageId: ERROR_DS_BACKLINK_WITHOUT_LINK
    //
    // MessageText:
    //
    // The schema update operation tried to add a backward link attribute that
    // has no corresponding forward link.
    //
    int ERROR_DS_BACKLINK_WITHOUT_LINK = 8482;

    //
    // MessageId: ERROR_DS_EPOCH_MISMATCH
    //
    // MessageText:
    //
    // Source and destination of a cross-domain move do not agree on the
    // object's epoch number. Either source or destination does not have the
    // latest version of the object.
    //
    int ERROR_DS_EPOCH_MISMATCH = 8483;

    //
    // MessageId: ERROR_DS_SRC_NAME_MISMATCH
    //
    // MessageText:
    //
    // Source and destination of a cross-domain move do not agree on the
    // object's current name. Either source or destination does not have the
    // latest version of the object.
    //
    int ERROR_DS_SRC_NAME_MISMATCH = 8484;

    //
    // MessageId: ERROR_DS_SRC_AND_DST_NC_IDENTICAL
    //
    // MessageText:
    //
    // Source and destination for the cross-domain move operation are identical.
    // Caller should use local move operation instead of cross-domain move
    // operation.
    //
    int ERROR_DS_SRC_AND_DST_NC_IDENTICAL = 8485;

    //
    // MessageId: ERROR_DS_DST_NC_MISMATCH
    //
    // MessageText:
    //
    // Source and destination for a cross-domain move are not in agreement on
    // the naming contexts in the forest. Either source or destination does not
    // have the latest version of the Partitions container.
    //
    int ERROR_DS_DST_NC_MISMATCH = 8486;

    //
    // MessageId: ERROR_DS_NOT_AUTHORITIVE_FOR_DST_NC
    //
    // MessageText:
    //
    // Destination of a cross-domain move is not authoritative for the
    // destination naming context.
    //
    int ERROR_DS_NOT_AUTHORITIVE_FOR_DST_NC = 8487;

    //
    // MessageId: ERROR_DS_SRC_GUID_MISMATCH
    //
    // MessageText:
    //
    // Source and destination of a cross-domain move do not agree on the
    // identity of the source object. Either source or destination does not have
    // the latest version of the source object.
    //
    int ERROR_DS_SRC_GUID_MISMATCH = 8488;

    //
    // MessageId: ERROR_DS_CANT_MOVE_DELETED_OBJECT
    //
    // MessageText:
    //
    // Object being moved across-domains is already known to be deleted by the
    // destination server. The source server does not have the latest version of
    // the source object.
    //
    int ERROR_DS_CANT_MOVE_DELETED_OBJECT = 8489;

    //
    // MessageId: ERROR_DS_PDC_OPERATION_IN_PROGRESS
    //
    // MessageText:
    //
    // Another operation which requires exclusive access to the PDC FSMO is
    // already in progress.
    //
    int ERROR_DS_PDC_OPERATION_IN_PROGRESS = 8490;

    //
    // MessageId: ERROR_DS_CROSS_DOMAIN_CLEANUP_REQD
    //
    // MessageText:
    //
    // A cross-domain move operation failed such that two versions of the moved
    // object exist - one each in the source and destination domains. The
    // destination object needs to be removed to restore the system to a
    // consistent state.
    //
    int ERROR_DS_CROSS_DOMAIN_CLEANUP_REQD = 8491;

    //
    // MessageId: ERROR_DS_ILLEGAL_XDOM_MOVE_OPERATION
    //
    // MessageText:
    //
    // This object may not be moved across domain boundaries either because
    // cross-domain moves for this class are disallowed, or the object has some
    // special characteristics, e.g.: trust account or restricted RID, which
    // prevent its move.
    //
    int ERROR_DS_ILLEGAL_XDOM_MOVE_OPERATION = 8492;

    //
    // MessageId: ERROR_DS_CANT_WITH_ACCT_GROUP_MEMBERSHPS
    //
    // MessageText:
    //
    // Can't move objects with memberships across domain boundaries as once
    // moved, this would violate the membership conditions of the account group.
    // Remove the object from any account group memberships and retry.
    //
    int ERROR_DS_CANT_WITH_ACCT_GROUP_MEMBERSHPS = 8493;

    //
    // MessageId: ERROR_DS_NC_MUST_HAVE_NC_PARENT
    //
    // MessageText:
    //
    // A naming context head must be the immediate child of another naming
    // context head, not of an interior node.
    //
    int ERROR_DS_NC_MUST_HAVE_NC_PARENT = 8494;

    //
    // MessageId: ERROR_DS_CR_IMPOSSIBLE_TO_VALIDATE
    //
    // MessageText:
    //
    // The directory cannot validate the proposed naming context name because it
    // does not hold a replica of the naming context above the proposed naming
    // context. Please ensure that the domain naming master role is held by a
    // server that is configured as a global catalog server, and that the server
    // is up to date with its replication partners. (Applies only to
    // Windows=2000 Domain Naming masters)
    //
    int ERROR_DS_CR_IMPOSSIBLE_TO_VALIDATE = 8495;

    //
    // MessageId: ERROR_DS_DST_DOMAIN_NOT_NATIVE
    //
    // MessageText:
    //
    // Destination domain must be in native mode.
    //
    int ERROR_DS_DST_DOMAIN_NOT_NATIVE = 8496;

    //
    // MessageId: ERROR_DS_MISSING_INFRASTRUCTURE_CONTAINER
    //
    // MessageText:
    //
    // The operation cannot be performed because the server does not have an
    // infrastructure container in the domain of interest.
    //
    int ERROR_DS_MISSING_INFRASTRUCTURE_CONTAINER = 8497;

    //
    // MessageId: ERROR_DS_CANT_MOVE_ACCOUNT_GROUP
    //
    // MessageText:
    //
    // Cross-domain move of non-empty account groups is not allowed.
    //
    int ERROR_DS_CANT_MOVE_ACCOUNT_GROUP = 8498;

    //
    // MessageId: ERROR_DS_CANT_MOVE_RESOURCE_GROUP
    //
    // MessageText:
    //
    // Cross-domain move of non-empty resource groups is not allowed.
    //
    int ERROR_DS_CANT_MOVE_RESOURCE_GROUP = 8499;

    //
    // MessageId: ERROR_DS_INVALID_SEARCH_FLAG
    //
    // MessageText:
    //
    // The search flags for the attribute are invalid. The ANR bit is valid only
    // on attributes of Unicode or Teletex strings.
    //
    int ERROR_DS_INVALID_SEARCH_FLAG = 8500;

    //
    // MessageId: ERROR_DS_NO_TREE_DELETE_ABOVE_NC
    //
    // MessageText:
    //
    // Tree deletions starting at an object which has an NC head as a descendant
    // are not allowed.
    //
    int ERROR_DS_NO_TREE_DELETE_ABOVE_NC = 8501;

    //
    // MessageId: ERROR_DS_COULDNT_LOCK_TREE_FOR_DELETE
    //
    // MessageText:
    //
    // The directory service failed to lock a tree in preparation for a tree
    // deletion because the tree was in use.
    //
    int ERROR_DS_COULDNT_LOCK_TREE_FOR_DELETE = 8502;

    //
    // MessageId: ERROR_DS_COULDNT_IDENTIFY_OBJECTS_FOR_TREE_DELETE
    //
    // MessageText:
    //
    // The directory service failed to identify the list of objects to delete
    // while attempting a tree deletion.
    //
    int ERROR_DS_COULDNT_IDENTIFY_OBJECTS_FOR_TREE_DELETE = 8503;

    //
    // MessageId: ERROR_DS_SAM_INIT_FAILURE
    //
    // MessageText:
    //
    // Security Accounts Manager initialization failed because of the following
    // error: %1.
    // Error Status: 0x%2. Click OK to shut down the system and reboot into
    // Directory Services Restore Mode. Check the event log for detailed
    // information.
    //
    int ERROR_DS_SAM_INIT_FAILURE = 8504;

    //
    // MessageId: ERROR_DS_SENSITIVE_GROUP_VIOLATION
    //
    // MessageText:
    //
    // Only an administrator can modify the membership list of an administrative
    // group.
    //
    int ERROR_DS_SENSITIVE_GROUP_VIOLATION = 8505;

    //
    // MessageId: ERROR_DS_CANT_MOD_PRIMARYGROUPID
    //
    // MessageText:
    //
    // Cannot change the primary group ID of a domain controller account.
    //
    int ERROR_DS_CANT_MOD_PRIMARYGROUPID = 8506;

    //
    // MessageId: ERROR_DS_ILLEGAL_BASE_SCHEMA_MOD
    //
    // MessageText:
    //
    // An attempt is made to modify the base schema.
    //
    int ERROR_DS_ILLEGAL_BASE_SCHEMA_MOD = 8507;

    //
    // MessageId: ERROR_DS_NONSAFE_SCHEMA_CHANGE
    //
    // MessageText:
    //
    // Adding a new mandatory attribute to an existing class, deleting a
    // mandatory attribute from an existing class, or adding an optional
    // attribute to the special class Top that is not a backlink attribute
    // (directly or through inheritance, for example, by adding or deleting an
    // auxiliary class) is not allowed.
    //
    int ERROR_DS_NONSAFE_SCHEMA_CHANGE = 8508;

    //
    // MessageId: ERROR_DS_SCHEMA_UPDATE_DISALLOWED
    //
    // MessageText:
    //
    // Schema update is not allowed on this DC because the DC is not the schema
    // FSMO Role Owner.
    //
    int ERROR_DS_SCHEMA_UPDATE_DISALLOWED = 8509;

    //
    // MessageId: ERROR_DS_CANT_CREATE_UNDER_SCHEMA
    //
    // MessageText:
    //
    // An object of this class cannot be created under the schema container. You
    // can only create attribute-schema and class-schema objects under the
    // schema container.
    //
    int ERROR_DS_CANT_CREATE_UNDER_SCHEMA = 8510;

    //
    // MessageId: ERROR_DS_INSTALL_NO_SRC_SCH_VERSION
    //
    // MessageText:
    //
    // The replica/child install failed to get the objectVersion attribute on
    // the schema container on the source DC. Either the attribute is missing on
    // the schema container or the credentials supplied do not have permission
    // to read it.
    //
    int ERROR_DS_INSTALL_NO_SRC_SCH_VERSION = 8511;

    //
    // MessageId: ERROR_DS_INSTALL_NO_SCH_VERSION_IN_INIFILE
    //
    // MessageText:
    //
    // The replica/child install failed to read the objectVersion attribute in
    // the SCHEMA section of the file schema.ini in the system32 directory.
    //
    int ERROR_DS_INSTALL_NO_SCH_VERSION_IN_INIFILE = 8512;

    //
    // MessageId: ERROR_DS_INVALID_GROUP_TYPE
    //
    // MessageText:
    //
    // The specified group type is invalid.
    //
    int ERROR_DS_INVALID_GROUP_TYPE = 8513;

    //
    // MessageId: ERROR_DS_NO_NEST_GLOBALGROUP_IN_MIXEDDOMAIN
    //
    // MessageText:
    //
    // You cannot nest global groups in a mixed domain if the group is
    // security-enabled.
    //
    int ERROR_DS_NO_NEST_GLOBALGROUP_IN_MIXEDDOMAIN = 8514;

    //
    // MessageId: ERROR_DS_NO_NEST_LOCALGROUP_IN_MIXEDDOMAIN
    //
    // MessageText:
    //
    // You cannot nest local groups in a mixed domain if the group is
    // security-enabled.
    //
    int ERROR_DS_NO_NEST_LOCALGROUP_IN_MIXEDDOMAIN = 8515;

    //
    // MessageId: ERROR_DS_GLOBAL_CANT_HAVE_LOCAL_MEMBER
    //
    // MessageText:
    //
    // A global group cannot have a local group as a member.
    //
    int ERROR_DS_GLOBAL_CANT_HAVE_LOCAL_MEMBER = 8516;

    //
    // MessageId: ERROR_DS_GLOBAL_CANT_HAVE_UNIVERSAL_MEMBER
    //
    // MessageText:
    //
    // A global group cannot have a universal group as a member.
    //
    int ERROR_DS_GLOBAL_CANT_HAVE_UNIVERSAL_MEMBER = 8517;

    //
    // MessageId: ERROR_DS_UNIVERSAL_CANT_HAVE_LOCAL_MEMBER
    //
    // MessageText:
    //
    // A universal group cannot have a local group as a member.
    //
    int ERROR_DS_UNIVERSAL_CANT_HAVE_LOCAL_MEMBER = 8518;

    //
    // MessageId: ERROR_DS_GLOBAL_CANT_HAVE_CROSSDOMAIN_MEMBER
    //
    // MessageText:
    //
    // A global group cannot have a cross-domain member.
    //
    int ERROR_DS_GLOBAL_CANT_HAVE_CROSSDOMAIN_MEMBER = 8519;

    //
    // MessageId: ERROR_DS_LOCAL_CANT_HAVE_CROSSDOMAIN_LOCAL_MEMBER
    //
    // MessageText:
    //
    // A local group cannot have another cross domain local group as a member.
    //
    int ERROR_DS_LOCAL_CANT_HAVE_CROSSDOMAIN_LOCAL_MEMBER = 8520;

    //
    // MessageId: ERROR_DS_HAVE_PRIMARY_MEMBERS
    //
    // MessageText:
    //
    // A group with primary members cannot change to a security-disabled group.
    //
    int ERROR_DS_HAVE_PRIMARY_MEMBERS = 8521;

    //
    // MessageId: ERROR_DS_STRING_SD_CONVERSION_FAILED
    //
    // MessageText:
    //
    // The schema cache load failed to convert the string default SD on a
    // class-schema object.
    //
    int ERROR_DS_STRING_SD_CONVERSION_FAILED = 8522;

    //
    // MessageId: ERROR_DS_NAMING_MASTER_GC
    //
    // MessageText:
    //
    // Only DSAs configured to be Global Catalog servers should be allowed to
    // hold the Domain Naming Master FSMO role. (Applies only to Windows=2000
    // servers)
    //
    int ERROR_DS_NAMING_MASTER_GC = 8523;

    //
    // MessageId: ERROR_DS_DNS_LOOKUP_FAILURE
    //
    // MessageText:
    //
    // The DSA operation is unable to proceed because of a DNS lookup failure.
    //
    int ERROR_DS_DNS_LOOKUP_FAILURE = 8524;

    //
    // MessageId: ERROR_DS_COULDNT_UPDATE_SPNS
    //
    // MessageText:
    //
    // While processing a change to the DNS Host Name for an object, the Service
    // Principal Name values could not be kept in sync.
    //
    int ERROR_DS_COULDNT_UPDATE_SPNS = 8525;

    //
    // MessageId: ERROR_DS_CANT_RETRIEVE_SD
    //
    // MessageText:
    //
    // The Security Descriptor attribute could not be read.
    //
    int ERROR_DS_CANT_RETRIEVE_SD = 8526;

    //
    // MessageId: ERROR_DS_KEY_NOT_UNIQUE
    //
    // MessageText:
    //
    // The object requested was not found, but an object with that key was
    // found.
    //
    int ERROR_DS_KEY_NOT_UNIQUE = 8527;

    //
    // MessageId: ERROR_DS_WRONG_LINKED_ATT_SYNTAX
    //
    // MessageText:
    //
    // The syntax of the linked attribute being added is incorrect. Forward
    // links can only have syntax=2.5.5.1,=2.5.5.7, and=2.5.5.14, and backlinks
    // can only have syntax=2.5.5.1
    //
    int ERROR_DS_WRONG_LINKED_ATT_SYNTAX = 8528;

    //
    // MessageId: ERROR_DS_SAM_NEED_BOOTKEY_PASSWORD
    //
    // MessageText:
    //
    // Security Account Manager needs to get the boot password.
    //
    int ERROR_DS_SAM_NEED_BOOTKEY_PASSWORD = 8529;

    //
    // MessageId: ERROR_DS_SAM_NEED_BOOTKEY_FLOPPY
    //
    // MessageText:
    //
    // Security Account Manager needs to get the boot key from floppy disk.
    //
    int ERROR_DS_SAM_NEED_BOOTKEY_FLOPPY = 8530;

    //
    // MessageId: ERROR_DS_CANT_START
    //
    // MessageText:
    //
    // Directory Service cannot start.
    //
    int ERROR_DS_CANT_START = 8531;

    //
    // MessageId: ERROR_DS_INIT_FAILURE
    //
    // MessageText:
    //
    // Directory Services could not start.
    //
    int ERROR_DS_INIT_FAILURE = 8532;

    //
    // MessageId: ERROR_DS_NO_PKT_PRIVACY_ON_CONNECTION
    //
    // MessageText:
    //
    // The connection between client and server requires packet privacy or
    // better.
    //
    int ERROR_DS_NO_PKT_PRIVACY_ON_CONNECTION = 8533;

    //
    // MessageId: ERROR_DS_SOURCE_DOMAIN_IN_FOREST
    //
    // MessageText:
    //
    // The source domain may not be in the same forest as destination.
    //
    int ERROR_DS_SOURCE_DOMAIN_IN_FOREST = 8534;

    //
    // MessageId: ERROR_DS_DESTINATION_DOMAIN_NOT_IN_FOREST
    //
    // MessageText:
    //
    // The destination domain must be in the forest.
    //
    int ERROR_DS_DESTINATION_DOMAIN_NOT_IN_FOREST = 8535;

    //
    // MessageId: ERROR_DS_DESTINATION_AUDITING_NOT_ENABLED
    //
    // MessageText:
    //
    // The operation requires that destination domain auditing be enabled.
    //
    int ERROR_DS_DESTINATION_AUDITING_NOT_ENABLED = 8536;

    //
    // MessageId: ERROR_DS_CANT_FIND_DC_FOR_SRC_DOMAIN
    //
    // MessageText:
    //
    // The operation couldn't locate a DC for the source domain.
    //
    int ERROR_DS_CANT_FIND_DC_FOR_SRC_DOMAIN = 8537;

    //
    // MessageId: ERROR_DS_SRC_OBJ_NOT_GROUP_OR_USER
    //
    // MessageText:
    //
    // The source object must be a group or user.
    //
    int ERROR_DS_SRC_OBJ_NOT_GROUP_OR_USER = 8538;

    //
    // MessageId: ERROR_DS_SRC_SID_EXISTS_IN_FOREST
    //
    // MessageText:
    //
    // The source object's SID already exists in destination forest.
    //
    int ERROR_DS_SRC_SID_EXISTS_IN_FOREST = 8539;

    //
    // MessageId: ERROR_DS_SRC_AND_DST_OBJECT_CLASS_MISMATCH
    //
    // MessageText:
    //
    // The source and destination object must be of the same type.
    //
    int ERROR_DS_SRC_AND_DST_OBJECT_CLASS_MISMATCH = 8540;

    //
    // MessageId: ERROR_SAM_INIT_FAILURE
    //
    // MessageText:
    //
    // Security Accounts Manager initialization failed because of the following
    // error: %1.
    // Error Status: 0x%2. Click OK to shut down the system and reboot into Safe
    // Mode. Check the event log for detailed information.
    //
    int ERROR_SAM_INIT_FAILURE = 8541;

    //
    // MessageId: ERROR_DS_DRA_SCHEMA_INFO_SHIP
    //
    // MessageText:
    //
    // Schema information could not be included in the replication request.
    //
    int ERROR_DS_DRA_SCHEMA_INFO_SHIP = 8542;

    //
    // MessageId: ERROR_DS_DRA_SCHEMA_CONFLICT
    //
    // MessageText:
    //
    // The replication operation could not be completed due to a schema
    // incompatibility.
    //
    int ERROR_DS_DRA_SCHEMA_CONFLICT = 8543;

    //
    // MessageId: ERROR_DS_DRA_EARLIER_SCHEMA_CONFLICT
    //
    // MessageText:
    //
    // The replication operation could not be completed due to a previous schema
    // incompatibility.
    //
    int ERROR_DS_DRA_EARLIER_SCHEMA_CONFLICT = 8544;

    //
    // MessageId: ERROR_DS_DRA_OBJ_NC_MISMATCH
    //
    // MessageText:
    //
    // The replication update could not be applied because either the source or
    // the destination has not yet received information regarding a recent
    // cross-domain move operation.
    //
    int ERROR_DS_DRA_OBJ_NC_MISMATCH = 8545;

    //
    // MessageId: ERROR_DS_NC_STILL_HAS_DSAS
    //
    // MessageText:
    //
    // The requested domain could not be deleted because there exist domain
    // controllers that still host this domain.
    //
    int ERROR_DS_NC_STILL_HAS_DSAS = 8546;

    //
    // MessageId: ERROR_DS_GC_REQUIRED
    //
    // MessageText:
    //
    // The requested operation can be performed only on a global catalog server.
    //
    int ERROR_DS_GC_REQUIRED = 8547;

    //
    // MessageId: ERROR_DS_LOCAL_MEMBER_OF_LOCAL_ONLY
    //
    // MessageText:
    //
    // A local group can only be a member of other local groups in the same
    // domain.
    //
    int ERROR_DS_LOCAL_MEMBER_OF_LOCAL_ONLY = 8548;

    //
    // MessageId: ERROR_DS_NO_FPO_IN_UNIVERSAL_GROUPS
    //
    // MessageText:
    //
    // Foreign security principals cannot be members of universal groups.
    //
    int ERROR_DS_NO_FPO_IN_UNIVERSAL_GROUPS = 8549;

    //
    // MessageId: ERROR_DS_CANT_ADD_TO_GC
    //
    // MessageText:
    //
    // The attribute is not allowed to be replicated to the GC because of
    // security reasons.
    //
    int ERROR_DS_CANT_ADD_TO_GC = 8550;

    //
    // MessageId: ERROR_DS_NO_CHECKPOINT_WITH_PDC
    //
    // MessageText:
    //
    // The checkpoint with the PDC could not be taken because there too many
    // modifications being processed currently.
    //
    int ERROR_DS_NO_CHECKPOINT_WITH_PDC = 8551;

    //
    // MessageId: ERROR_DS_SOURCE_AUDITING_NOT_ENABLED
    //
    // MessageText:
    //
    // The operation requires that source domain auditing be enabled.
    //
    int ERROR_DS_SOURCE_AUDITING_NOT_ENABLED = 8552;

    //
    // MessageId: ERROR_DS_CANT_CREATE_IN_NONDOMAIN_NC
    //
    // MessageText:
    //
    // Security principal objects can only be created inside domain naming
    // contexts.
    //
    int ERROR_DS_CANT_CREATE_IN_NONDOMAIN_NC = 8553;

    //
    // MessageId: ERROR_DS_INVALID_NAME_FOR_SPN
    //
    // MessageText:
    //
    // A Service Principal Name (SPN) could not be constructed because the
    // provided hostname is not in the necessary format.
    //
    int ERROR_DS_INVALID_NAME_FOR_SPN = 8554;

    //
    // MessageId: ERROR_DS_FILTER_USES_CONTRUCTED_ATTRS
    //
    // MessageText:
    //
    // A Filter was passed that uses constructed attributes.
    //
    int ERROR_DS_FILTER_USES_CONTRUCTED_ATTRS = 8555;

    //
    // MessageId: ERROR_DS_UNICODEPWD_NOT_IN_QUOTES
    //
    // MessageText:
    //
    // The unicodePwd attribute value must be enclosed in double quotes.
    //
    int ERROR_DS_UNICODEPWD_NOT_IN_QUOTES = 8556;

    //
    // MessageId: ERROR_DS_MACHINE_ACCOUNT_QUOTA_EXCEEDED
    //
    // MessageText:
    //
    // Your computer could not be joined to the domain. You have exceeded the
    // maximum number of computer accounts you are allowed to create in this
    // domain. Contact your system administrator to have this limit reset or
    // increased.
    //
    int ERROR_DS_MACHINE_ACCOUNT_QUOTA_EXCEEDED = 8557;

    //
    // MessageId: ERROR_DS_MUST_BE_RUN_ON_DST_DC
    //
    // MessageText:
    //
    // For security reasons, the operation must be run on the destination DC.
    //
    int ERROR_DS_MUST_BE_RUN_ON_DST_DC = 8558;

    //
    // MessageId: ERROR_DS_SRC_DC_MUST_BE_SP4_OR_GREATER
    //
    // MessageText:
    //
    // For security reasons, the source DC must be NT4SP4 or greater.
    //
    int ERROR_DS_SRC_DC_MUST_BE_SP4_OR_GREATER = 8559;

    //
    // MessageId: ERROR_DS_CANT_TREE_DELETE_CRITICAL_OBJ
    //
    // MessageText:
    //
    // Critical Directory Service System objects cannot be deleted during tree
    // delete operations. The tree delete may have been partially performed.
    //
    int ERROR_DS_CANT_TREE_DELETE_CRITICAL_OBJ = 8560;

    //
    // MessageId: ERROR_DS_INIT_FAILURE_CONSOLE
    //
    // MessageText:
    //
    // Directory Services could not start because of the following error: %1.
    // Error Status: 0x%2. Please click OK to shutdown the system. You can use
    // the recovery console to diagnose the system further.
    //
    int ERROR_DS_INIT_FAILURE_CONSOLE = 8561;

    //
    // MessageId: ERROR_DS_SAM_INIT_FAILURE_CONSOLE
    //
    // MessageText:
    //
    // Security Accounts Manager initialization failed because of the following
    // error: %1.
    // Error Status: 0x%2. Please click OK to shutdown the system. You can use
    // the recovery console to diagnose the system further.
    //
    int ERROR_DS_SAM_INIT_FAILURE_CONSOLE = 8562;

    //
    // MessageId: ERROR_DS_FOREST_VERSION_TOO_HIGH
    //
    // MessageText:
    //
    // The version of the operating system installed is incompatible with the
    // current forest functional level. You must upgrade to a new version of the
    // operating system before this server can become a domain controller in
    // this forest.
    //
    int ERROR_DS_FOREST_VERSION_TOO_HIGH = 8563;

    //
    // MessageId: ERROR_DS_DOMAIN_VERSION_TOO_HIGH
    //
    // MessageText:
    //
    // The version of the operating system installed is incompatible with the
    // current domain functional level. You must upgrade to a new version of the
    // operating system before this server can become a domain controller in
    // this domain.
    //
    int ERROR_DS_DOMAIN_VERSION_TOO_HIGH = 8564;

    //
    // MessageId: ERROR_DS_FOREST_VERSION_TOO_LOW
    //
    // MessageText:
    //
    // The version of the operating system installed on this server no longer
    // supports the current forest functional level. You must raise the forest
    // functional level before this server can become a domain controller in
    // this forest.
    //
    int ERROR_DS_FOREST_VERSION_TOO_LOW = 8565;

    //
    // MessageId: ERROR_DS_DOMAIN_VERSION_TOO_LOW
    //
    // MessageText:
    //
    // The version of the operating system installed on this server no longer
    // supports the current domain functional level. You must raise the domain
    // functional level before this server can become a domain controller in
    // this domain.
    //
    int ERROR_DS_DOMAIN_VERSION_TOO_LOW = 8566;

    //
    // MessageId: ERROR_DS_INCOMPATIBLE_VERSION
    //
    // MessageText:
    //
    // The version of the operating system installed on this server is
    // incompatible with the functional level of the domain or forest.
    //
    int ERROR_DS_INCOMPATIBLE_VERSION = 8567;

    //
    // MessageId: ERROR_DS_LOW_DSA_VERSION
    //
    // MessageText:
    //
    // The functional level of the domain (or forest) cannot be raised to the
    // requested value, because there exist one or more domain controllers in
    // the domain (or forest) that are at a lower incompatible functional level.
    //
    int ERROR_DS_LOW_DSA_VERSION = 8568;

    //
    // MessageId: ERROR_DS_NO_BEHAVIOR_VERSION_IN_MIXEDDOMAIN
    //
    // MessageText:
    //
    // The forest functional level cannot be raised to the requested value since
    // one or more domains are still in mixed domain mode. All domains in the
    // forest must be in native mode, for you to raise the forest functional
    // level.
    //
    int ERROR_DS_NO_BEHAVIOR_VERSION_IN_MIXEDDOMAIN = 8569;

    //
    // MessageId: ERROR_DS_NOT_SUPPORTED_SORT_ORDER
    //
    // MessageText:
    //
    // The sort order requested is not supported.
    //
    int ERROR_DS_NOT_SUPPORTED_SORT_ORDER = 8570;

    //
    // MessageId: ERROR_DS_NAME_NOT_UNIQUE
    //
    // MessageText:
    //
    // The requested name already exists as a unique identifier.
    //
    int ERROR_DS_NAME_NOT_UNIQUE = 8571;

    //
    // MessageId: ERROR_DS_MACHINE_ACCOUNT_CREATED_PRENT4
    //
    // MessageText:
    //
    // The machine account was created pre-NT4. The account needs to be
    // recreated.
    //
    int ERROR_DS_MACHINE_ACCOUNT_CREATED_PRENT4 = 8572;

    //
    // MessageId: ERROR_DS_OUT_OF_VERSION_STORE
    //
    // MessageText:
    //
    // The database is out of version store.
    //
    int ERROR_DS_OUT_OF_VERSION_STORE = 8573;

    //
    // MessageId: ERROR_DS_INCOMPATIBLE_CONTROLS_USED
    //
    // MessageText:
    //
    // Unable to continue operation because multiple conflicting controls were
    // used.
    //
    int ERROR_DS_INCOMPATIBLE_CONTROLS_USED = 8574;

    //
    // MessageId: ERROR_DS_NO_REF_DOMAIN
    //
    // MessageText:
    //
    // Unable to find a valid security descriptor reference domain for this
    // partition.
    //
    int ERROR_DS_NO_REF_DOMAIN = 8575;

    //
    // MessageId: ERROR_DS_RESERVED_LINK_ID
    //
    // MessageText:
    //
    // Schema update failed: The link identifier is reserved.
    //
    int ERROR_DS_RESERVED_LINK_ID = 8576;

    //
    // MessageId: ERROR_DS_LINK_ID_NOT_AVAILABLE
    //
    // MessageText:
    //
    // Schema update failed: There are no link identifiers available.
    //
    int ERROR_DS_LINK_ID_NOT_AVAILABLE = 8577;

    //
    // MessageId: ERROR_DS_AG_CANT_HAVE_UNIVERSAL_MEMBER
    //
    // MessageText:
    //
    // An account group cannot have a universal group as a member.
    //
    int ERROR_DS_AG_CANT_HAVE_UNIVERSAL_MEMBER = 8578;

    //
    // MessageId: ERROR_DS_MODIFYDN_DISALLOWED_BY_INSTANCE_TYPE
    //
    // MessageText:
    //
    // Rename or move operations on naming context heads or read-only objects
    // are not allowed.
    //
    int ERROR_DS_MODIFYDN_DISALLOWED_BY_INSTANCE_TYPE = 8579;

    //
    // MessageId: ERROR_DS_NO_OBJECT_MOVE_IN_SCHEMA_NC
    //
    // MessageText:
    //
    // Move operations on objects in the schema naming context are not allowed.
    //
    int ERROR_DS_NO_OBJECT_MOVE_IN_SCHEMA_NC = 8580;

    //
    // MessageId: ERROR_DS_MODIFYDN_DISALLOWED_BY_FLAG
    //
    // MessageText:
    //
    // A system flag has been set on the object and does not allow the object to
    // be moved or renamed.
    //
    int ERROR_DS_MODIFYDN_DISALLOWED_BY_FLAG = 8581;

    //
    // MessageId: ERROR_DS_MODIFYDN_WRONG_GRANDPARENT
    //
    // MessageText:
    //
    // This object is not allowed to change its grandparent container. Moves are
    // not forbidden on this object, but are restricted to sibling containers.
    //
    int ERROR_DS_MODIFYDN_WRONG_GRANDPARENT = 8582;

    //
    // MessageId: ERROR_DS_NAME_ERROR_TRUST_REFERRAL
    //
    // MessageText:
    //
    // Unable to resolve completely, a referral to another forest is generated.
    //
    int ERROR_DS_NAME_ERROR_TRUST_REFERRAL = 8583;

    //
    // MessageId: ERROR_NOT_SUPPORTED_ON_STANDARD_SERVER
    //
    // MessageText:
    //
    // The requested action is not supported on standard server.
    //
    int ERROR_NOT_SUPPORTED_ON_STANDARD_SERVER = 8584;

    //
    // MessageId: ERROR_DS_CANT_ACCESS_REMOTE_PART_OF_AD
    //
    // MessageText:
    //
    // Could not access a partition of the directory service located on a remote
    // server. Make sure at least one server is running for the partition in
    // question.
    //
    int ERROR_DS_CANT_ACCESS_REMOTE_PART_OF_AD = 8585;

    //
    // MessageId: ERROR_DS_CR_IMPOSSIBLE_TO_VALIDATE_V2
    //
    // MessageText:
    //
    // The directory cannot validate the proposed naming context (or partition)
    // name because it does not hold a replica nor can it contact a replica of
    // the naming context above the proposed naming context. Please ensure that
    // the parent naming context is properly registered in DNS, and at least one
    // replica of this naming context is reachable by the Domain Naming master.
    //
    int ERROR_DS_CR_IMPOSSIBLE_TO_VALIDATE_V2 = 8586;

    //
    // MessageId: ERROR_DS_THREAD_LIMIT_EXCEEDED
    //
    // MessageText:
    //
    // The thread limit for this request was exceeded.
    //
    int ERROR_DS_THREAD_LIMIT_EXCEEDED = 8587;

    //
    // MessageId: ERROR_DS_NOT_CLOSEST
    //
    // MessageText:
    //
    // The Global catalog server is not in the closest site.
    //
    int ERROR_DS_NOT_CLOSEST = 8588;

    //
    // MessageId: ERROR_DS_CANT_DERIVE_SPN_WITHOUT_SERVER_REF
    //
    // MessageText:
    //
    // The DS cannot derive a service principal name (SPN) with which to
    // mutually authenticate the target server because the corresponding server
    // object in the local DS database has no serverReference attribute.
    //
    int ERROR_DS_CANT_DERIVE_SPN_WITHOUT_SERVER_REF = 8589;

    //
    // MessageId: ERROR_DS_SINGLE_USER_MODE_FAILED
    //
    // MessageText:
    //
    // The Directory Service failed to enter single user mode.
    //
    int ERROR_DS_SINGLE_USER_MODE_FAILED = 8590;

    //
    // MessageId: ERROR_DS_NTDSCRIPT_SYNTAX_ERROR
    //
    // MessageText:
    //
    // The Directory Service cannot parse the script because of a syntax error.
    //
    int ERROR_DS_NTDSCRIPT_SYNTAX_ERROR = 8591;

    //
    // MessageId: ERROR_DS_NTDSCRIPT_PROCESS_ERROR
    //
    // MessageText:
    //
    // The Directory Service cannot process the script because of an error.
    //
    int ERROR_DS_NTDSCRIPT_PROCESS_ERROR = 8592;

    //
    // MessageId: ERROR_DS_DIFFERENT_REPL_EPOCHS
    //
    // MessageText:
    //
    // The directory service cannot perform the requested operation because the
    // servers
    // involved are of different replication epochs (which is usually related to
    // a
    // domain rename that is in progress).
    //
    int ERROR_DS_DIFFERENT_REPL_EPOCHS = 8593;

    //
    // MessageId: ERROR_DS_DRS_EXTENSIONS_CHANGED
    //
    // MessageText:
    //
    // The directory service binding must be renegotiated due to a change in the
    // server
    // extensions information.
    //
    int ERROR_DS_DRS_EXTENSIONS_CHANGED = 8594;

    //
    // MessageId: ERROR_DS_REPLICA_SET_CHANGE_NOT_ALLOWED_ON_DISABLED_CR
    //
    // MessageText:
    //
    // Operation not allowed on a disabled cross ref.
    //
    int ERROR_DS_REPLICA_SET_CHANGE_NOT_ALLOWED_ON_DISABLED_CR = 8595;

    //
    // MessageId: ERROR_DS_NO_MSDS_INTID
    //
    // MessageText:
    //
    // Schema update failed: No values for msDS-IntId are available.
    //
    int ERROR_DS_NO_MSDS_INTID = 8596;

    //
    // MessageId: ERROR_DS_DUP_MSDS_INTID
    //
    // MessageText:
    //
    // Schema update failed: Duplicate msDS-INtId. Retry the operation.
    //
    int ERROR_DS_DUP_MSDS_INTID = 8597;

    //
    // MessageId: ERROR_DS_EXISTS_IN_RDNATTID
    //
    // MessageText:
    //
    // Schema deletion failed: attribute is used in rDNAttID.
    //
    int ERROR_DS_EXISTS_IN_RDNATTID = 8598;

    //
    // MessageId: ERROR_DS_AUTHORIZATION_FAILED
    //
    // MessageText:
    //
    // The directory service failed to authorize the request.
    //
    int ERROR_DS_AUTHORIZATION_FAILED = 8599;

    //
    // MessageId: ERROR_DS_INVALID_SCRIPT
    //
    // MessageText:
    //
    // The Directory Service cannot process the script because it is invalid.
    //
    int ERROR_DS_INVALID_SCRIPT = 8600;

    //
    // MessageId: ERROR_DS_REMOTE_CROSSREF_OP_FAILED
    //
    // MessageText:
    //
    // The remote create cross reference operation failed on the Domain Naming
    // Master FSMO. The operation's error is in the extended data.
    //
    int ERROR_DS_REMOTE_CROSSREF_OP_FAILED = 8601;

    //
    // MessageId: ERROR_DS_CROSS_REF_BUSY
    //
    // MessageText:
    //
    // A cross reference is in use locally with the same name.
    //
    int ERROR_DS_CROSS_REF_BUSY = 8602;

    //
    // MessageId: ERROR_DS_CANT_DERIVE_SPN_FOR_DELETED_DOMAIN
    //
    // MessageText:
    //
    // The DS cannot derive a service principal name (SPN) with which to
    // mutually authenticate the target server because the server's domain has
    // been deleted from the forest.
    //
    int ERROR_DS_CANT_DERIVE_SPN_FOR_DELETED_DOMAIN = 8603;

    //
    // MessageId: ERROR_DS_CANT_DEMOTE_WITH_WRITEABLE_NC
    //
    // MessageText:
    //
    // Writeable NCs prevent this DC from demoting.
    //
    int ERROR_DS_CANT_DEMOTE_WITH_WRITEABLE_NC = 8604;

    //
    // MessageId: ERROR_DS_DUPLICATE_ID_FOUND
    //
    // MessageText:
    //
    // The requested object has a non-unique identifier and cannot be retrieved.
    //
    int ERROR_DS_DUPLICATE_ID_FOUND = 8605;

    //
    // MessageId: ERROR_DS_INSUFFICIENT_ATTR_TO_CREATE_OBJECT
    //
    // MessageText:
    //
    // Insufficient attributes were given to create an object. This object may
    // not exist because it may have been deleted and already garbage collected.
    //
    int ERROR_DS_INSUFFICIENT_ATTR_TO_CREATE_OBJECT = 8606;

    //
    // MessageId: ERROR_DS_GROUP_CONVERSION_ERROR
    //
    // MessageText:
    //
    // The group cannot be converted due to attribute restrictions on the
    // requested group type.
    //
    int ERROR_DS_GROUP_CONVERSION_ERROR = 8607;

    //
    // MessageId: ERROR_DS_CANT_MOVE_APP_BASIC_GROUP
    //
    // MessageText:
    //
    // Cross-domain move of non-empty basic application groups is not allowed.
    //
    int ERROR_DS_CANT_MOVE_APP_BASIC_GROUP = 8608;

    //
    // MessageId: ERROR_DS_CANT_MOVE_APP_QUERY_GROUP
    //
    // MessageText:
    //
    // Cross-domain move of non-empty query based application groups is not
    // allowed.
    //
    int ERROR_DS_CANT_MOVE_APP_QUERY_GROUP = 8609;

    //
    // MessageId: ERROR_DS_ROLE_NOT_VERIFIED
    //
    // MessageText:
    //
    // The FSMO role ownership could not be verified because its directory
    // partition has not replicated successfully with atleast one replication
    // partner.
    //
    int ERROR_DS_ROLE_NOT_VERIFIED = 8610;

    //
    // MessageId: ERROR_DS_WKO_CONTAINER_CANNOT_BE_SPECIAL
    //
    // MessageText:
    //
    // The target container for a redirection of a well known object container
    // cannot already be a special container.
    //
    int ERROR_DS_WKO_CONTAINER_CANNOT_BE_SPECIAL = 8611;

    //
    // MessageId: ERROR_DS_DOMAIN_RENAME_IN_PROGRESS
    //
    // MessageText:
    //
    // The Directory Service cannot perform the requested operation because a
    // domain rename operation is in progress.
    //
    int ERROR_DS_DOMAIN_RENAME_IN_PROGRESS = 8612;

    //
    // MessageId: ERROR_DS_EXISTING_AD_CHILD_NC
    //
    // MessageText:
    //
    // The directory service detected a child partition below the requested
    // partition name. The partition hierarchy must be created in a top down
    // method.
    //
    int ERROR_DS_EXISTING_AD_CHILD_NC = 8613;

    //
    // MessageId: ERROR_DS_REPL_LIFETIME_EXCEEDED
    //
    // MessageText:
    //
    // The directory service cannot replicate with this server because the time
    // since the last replication with this server has exceeded the tombstone
    // lifetime.
    //
    int ERROR_DS_REPL_LIFETIME_EXCEEDED = 8614;

    //
    // MessageId: ERROR_DS_DISALLOWED_IN_SYSTEM_CONTAINER
    //
    // MessageText:
    //
    // The requested operation is not allowed on an object under the system
    // container.
    //
    int ERROR_DS_DISALLOWED_IN_SYSTEM_CONTAINER = 8615;

    //
    // MessageId: ERROR_DS_LDAP_SEND_QUEUE_FULL
    //
    // MessageText:
    //
    // The LDAP servers network send queue has filled up because the client is
    // not processing the results of it's requests fast enough. No more requests
    // will be processed until the client catches up. If the client does not
    // catch up then it will be disconnected.
    //
    int ERROR_DS_LDAP_SEND_QUEUE_FULL = 8616;

    //
    // MessageId: ERROR_DS_DRA_OUT_SCHEDULE_WINDOW
    //
    // MessageText:
    //
    // The scheduled replication did not take place because the system was too
    // busy to execute the request within the schedule window. The replication
    // queue is overloaded. Consider reducing the number of partners or
    // decreasing the scheduled replication frequency.
    //
    int ERROR_DS_DRA_OUT_SCHEDULE_WINDOW = 8617;

    //
    // MessageId: ERROR_DS_POLICY_NOT_KNOWN
    //
    // MessageText:
    //
    // At this time, it cannot be determined if the branch replication policy is
    // available on the hub domain controller. Please retry at a later time to
    // account for replication latencies.
    //
    int ERROR_DS_POLICY_NOT_KNOWN = 8618;

    //
    // MessageId: ERROR_NO_SITE_SETTINGS_OBJECT
    //
    // MessageText:
    //
    // The site settings object for the specified site does not exist.
    //
    int ERROR_NO_SITE_SETTINGS_OBJECT = 8619;

    //
    // MessageId: ERROR_NO_SECRETS
    //
    // MessageText:
    //
    // The local account store does not contain secret material for the
    // specified account.
    //
    int ERROR_NO_SECRETS = 8620;

    //
    // MessageId: ERROR_NO_WRITABLE_DC_FOUND
    //
    // MessageText:
    //
    // Could not find a writable domain controller in the domain.
    //
    int ERROR_NO_WRITABLE_DC_FOUND = 8621;

    //
    // MessageId: ERROR_DS_NO_SERVER_OBJECT
    //
    // MessageText:
    //
    // The server object for the domain controller does not exist.
    //
    int ERROR_DS_NO_SERVER_OBJECT = 8622;

    //
    // MessageId: ERROR_DS_NO_NTDSA_OBJECT
    //
    // MessageText:
    //
    // The NTDS Settings object for the domain controller does not exist.
    //
    int ERROR_DS_NO_NTDSA_OBJECT = 8623;

    //
    // MessageId: ERROR_DS_NON_ASQ_SEARCH
    //
    // MessageText:
    //
    // The requested search operation is not supported for ASQ searches.
    //
    int ERROR_DS_NON_ASQ_SEARCH = 8624;

    //
    // MessageId: ERROR_DS_AUDIT_FAILURE
    //
    // MessageText:
    //
    // A required audit event could not be generated for the operation.
    //
    int ERROR_DS_AUDIT_FAILURE = 8625;

    //
    // MessageId: ERROR_DS_INVALID_SEARCH_FLAG_SUBTREE
    //
    // MessageText:
    //
    // The search flags for the attribute are invalid. The subtree index bit is
    // valid only on single valued attributes.
    //
    int ERROR_DS_INVALID_SEARCH_FLAG_SUBTREE = 8626;

    //
    // MessageId: ERROR_DS_INVALID_SEARCH_FLAG_TUPLE
    //
    // MessageText:
    //
    // The search flags for the attribute are invalid. The tuple index bit is
    // valid only on attributes of Unicode strings.
    //
    int ERROR_DS_INVALID_SEARCH_FLAG_TUPLE = 8627;

    //
    // MessageId: ERROR_DS_HIERARCHY_TABLE_TOO_DEEP
    //
    // MessageText:
    //
    // The address books are nested too deeply. Failed to build the hierarchy
    // table.
    //
    int ERROR_DS_HIERARCHY_TABLE_TOO_DEEP = 8628;

    // /////////////////////////////////////////////////
    // =/
    // =End of Active Directory Error Codes /
    // =/
    // =8000 to =8999=/
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =DNS Error codes=//
    // =//
    // =9000 to=9999=//
    // /////////////////////////////////////////////////

    // ====
    // Facility DNS Error Messages
    // ====

    //
    // DNS response codes.
    //

    int DNS_ERROR_RESPONSE_CODES_BASE = 9000;

    int DNS_ERROR_RCODE_NO_ERROR = NO_ERROR;

    int DNS_ERROR_MASK = 0x00002328; // =9000 or
                                                            // DNS_ERROR_RESPONSE_CODES_BASE

    // DNS_ERROR_RCODE_FORMAT_ERROR=0x00002329
    //
    // MessageId: DNS_ERROR_RCODE_FORMAT_ERROR
    //
    // MessageText:
    //
    // DNS server unable to interpret format.
    //
    int DNS_ERROR_RCODE_FORMAT_ERROR = 9001;

    // DNS_ERROR_RCODE_SERVER_FAILURE=0x0000232a
    //
    // MessageId: DNS_ERROR_RCODE_SERVER_FAILURE
    //
    // MessageText:
    //
    // DNS server failure.
    //
    int DNS_ERROR_RCODE_SERVER_FAILURE = 9002;

    // DNS_ERROR_RCODE_NAME_ERROR=0x0000232b
    //
    // MessageId: DNS_ERROR_RCODE_NAME_ERROR
    //
    // MessageText:
    //
    // DNS name does not exist.
    //
    int DNS_ERROR_RCODE_NAME_ERROR = 9003;

    // DNS_ERROR_RCODE_NOT_IMPLEMENTED 0x0000232c
    //
    // MessageId: DNS_ERROR_RCODE_NOT_IMPLEMENTED
    //
    // MessageText:
    //
    // DNS request not supported by name server.
    //
    int DNS_ERROR_RCODE_NOT_IMPLEMENTED = 9004;

    // DNS_ERROR_RCODE_REFUSED=0x0000232d
    //
    // MessageId: DNS_ERROR_RCODE_REFUSED
    //
    // MessageText:
    //
    // DNS operation refused.
    //
    int DNS_ERROR_RCODE_REFUSED = 9005;

    // DNS_ERROR_RCODE_YXDOMAIN=0x0000232e
    //
    // MessageId: DNS_ERROR_RCODE_YXDOMAIN
    //
    // MessageText:
    //
    // DNS name that ought not exist, does exist.
    //
    int DNS_ERROR_RCODE_YXDOMAIN = 9006;

    // DNS_ERROR_RCODE_YXRRSET=0x0000232f
    //
    // MessageId: DNS_ERROR_RCODE_YXRRSET
    //
    // MessageText:
    //
    // DNS RR set that ought not exist, does exist.
    //
    int DNS_ERROR_RCODE_YXRRSET = 9007;

    // DNS_ERROR_RCODE_NXRRSET=0x00002330
    //
    // MessageId: DNS_ERROR_RCODE_NXRRSET
    //
    // MessageText:
    //
    // DNS RR set that ought to exist, does not exist.
    //
    int DNS_ERROR_RCODE_NXRRSET = 9008;

    // DNS_ERROR_RCODE_NOTAUTH=0x00002331
    //
    // MessageId: DNS_ERROR_RCODE_NOTAUTH
    //
    // MessageText:
    //
    // DNS server not authoritative for zone.
    //
    int DNS_ERROR_RCODE_NOTAUTH = 9009;

    // DNS_ERROR_RCODE_NOTZONE=0x00002332
    //
    // MessageId: DNS_ERROR_RCODE_NOTZONE
    //
    // MessageText:
    //
    // DNS name in update or prereq is not in zone.
    //
    int DNS_ERROR_RCODE_NOTZONE = 9010;

    // DNS_ERROR_RCODE_BADSIG=0x00002338
    //
    // MessageId: DNS_ERROR_RCODE_BADSIG
    //
    // MessageText:
    //
    // DNS signature failed to verify.
    //
    int DNS_ERROR_RCODE_BADSIG = 9016;

    // DNS_ERROR_RCODE_BADKEY=0x00002339
    //
    // MessageId: DNS_ERROR_RCODE_BADKEY
    //
    // MessageText:
    //
    // DNS bad key.
    //
    int DNS_ERROR_RCODE_BADKEY = 9017;

    // DNS_ERROR_RCODE_BADTIME=0x0000233a
    //
    // MessageId: DNS_ERROR_RCODE_BADTIME
    //
    // MessageText:
    //
    // DNS signature validity expired.
    //
    int DNS_ERROR_RCODE_BADTIME = 9018;

    int DNS_ERROR_RCODE_LAST = DNS_ERROR_RCODE_BADTIME;

    //
    // Packet format
    //

    int DNS_ERROR_PACKET_FMT_BASE = 9500;

    // DNS_INFO_NO_RECORDS=0x0000251d
    //
    // MessageId: DNS_INFO_NO_RECORDS
    //
    // MessageText:
    //
    // No records found for given DNS query.
    //
    int DNS_INFO_NO_RECORDS = 9501;

    // DNS_ERROR_BAD_PACKET=0x0000251e
    //
    // MessageId: DNS_ERROR_BAD_PACKET
    //
    // MessageText:
    //
    // Bad DNS packet.
    //
    int DNS_ERROR_BAD_PACKET = 9502;

    // DNS_ERROR_NO_PACKET=0x0000251f
    //
    // MessageId: DNS_ERROR_NO_PACKET
    //
    // MessageText:
    //
    // No DNS packet.
    //
    int DNS_ERROR_NO_PACKET = 9503;

    // DNS_ERROR_RCODE=0x00002520
    //
    // MessageId: DNS_ERROR_RCODE
    //
    // MessageText:
    //
    // DNS error, check rcode.
    //
    int DNS_ERROR_RCODE = 9504;

    // DNS_ERROR_UNSECURE_PACKET=0x00002521
    //
    // MessageId: DNS_ERROR_UNSECURE_PACKET
    //
    // MessageText:
    //
    // Unsecured DNS packet.
    //
    int DNS_ERROR_UNSECURE_PACKET = 9505;

    int DNS_STATUS_PACKET_UNSECURE = DNS_ERROR_UNSECURE_PACKET;

    //
    // General API errors
    //

    int DNS_ERROR_NO_MEMORY = ERROR_OUTOFMEMORY;
    int DNS_ERROR_INVALID_NAME = ERROR_INVALID_NAME;
    int DNS_ERROR_INVALID_DATA = ERROR_INVALID_DATA;

    int DNS_ERROR_GENERAL_API_BASE = 9550;

    // DNS_ERROR_INVALID_TYPE=0x0000254f
    //
    // MessageId: DNS_ERROR_INVALID_TYPE
    //
    // MessageText:
    //
    // Invalid DNS type.
    //
    int DNS_ERROR_INVALID_TYPE = 9551;

    // DNS_ERROR_INVALID_IP_ADDRESS=0x00002550
    //
    // MessageId: DNS_ERROR_INVALID_IP_ADDRESS
    //
    // MessageText:
    //
    // Invalid IP address.
    //
    int DNS_ERROR_INVALID_IP_ADDRESS = 9552;

    // DNS_ERROR_INVALID_PROPERTY=0x00002551
    //
    // MessageId: DNS_ERROR_INVALID_PROPERTY
    //
    // MessageText:
    //
    // Invalid property.
    //
    int DNS_ERROR_INVALID_PROPERTY = 9553;

    // DNS_ERROR_TRY_AGAIN_LATER=0x00002552
    //
    // MessageId: DNS_ERROR_TRY_AGAIN_LATER
    //
    // MessageText:
    //
    // Try DNS operation again later.
    //
    int DNS_ERROR_TRY_AGAIN_LATER = 9554;

    // DNS_ERROR_NOT_UNIQUE=0x00002553
    //
    // MessageId: DNS_ERROR_NOT_UNIQUE
    //
    // MessageText:
    //
    // Record for given name and type is not unique.
    //
    int DNS_ERROR_NOT_UNIQUE = 9555;

    // DNS_ERROR_NON_RFC_NAME=0x00002554
    //
    // MessageId: DNS_ERROR_NON_RFC_NAME
    //
    // MessageText:
    //
    // DNS name does not comply with RFC specifications.
    //
    int DNS_ERROR_NON_RFC_NAME = 9556;

    // DNS_STATUS_FQDN=0x00002555
    //
    // MessageId: DNS_STATUS_FQDN
    //
    // MessageText:
    //
    // DNS name is a fully-qualified DNS name.
    //
    int DNS_STATUS_FQDN = 9557;

    // DNS_STATUS_DOTTED_NAME=0x00002556
    //
    // MessageId: DNS_STATUS_DOTTED_NAME
    //
    // MessageText:
    //
    // DNS name is dotted (multi-labe;.
    //
    int DNS_STATUS_DOTTED_NAME = 9558;

    // DNS_STATUS_SINGLE_PART_NAME=0x00002557
    //
    // MessageId: DNS_STATUS_SINGLE_PART_NAME
    //
    // MessageText:
    //
    // DNS name is a single-part name.
    //
    int DNS_STATUS_SINGLE_PART_NAME = 9559;

    // DNS_ERROR_INVALID_NAME_CHAR=0x00002558
    //
    // MessageId: DNS_ERROR_INVALID_NAME_CHAR
    //
    // MessageText:
    //
    // DNS name contains an invalid character.
    //
    int DNS_ERROR_INVALID_NAME_CHAR = 9560;

    // DNS_ERROR_NUMERIC_NAME=0x00002559
    //
    // MessageId: DNS_ERROR_NUMERIC_NAME
    //
    // MessageText:
    //
    // DNS name is entirely numeric.
    //
    int DNS_ERROR_NUMERIC_NAME = 9561;

    // DNS_ERROR_NOT_ALLOWED_ON_ROOT_SERVER 0x0000255A
    //
    // MessageId: DNS_ERROR_NOT_ALLOWED_ON_ROOT_SERVER
    //
    // MessageText:
    //
    // The operation requested is not permitted on a DNS root server.
    //
    int DNS_ERROR_NOT_ALLOWED_ON_ROOT_SERVER = 9562;

    // DNS_ERROR_NOT_ALLOWED_UNDER_DELEGATION 0x0000255B
    //
    // MessageId: DNS_ERROR_NOT_ALLOWED_UNDER_DELEGATION
    //
    // MessageText:
    //
    // The record could not be created because this part of the DNS namespace
    // has been delegated to another server.
    //
    int DNS_ERROR_NOT_ALLOWED_UNDER_DELEGATION = 9563;

    // DNS_ERROR_CANNOT_FIND_ROOT_HINTS 0x0000255C
    //
    // MessageId: DNS_ERROR_CANNOT_FIND_ROOT_HINTS
    //
    // MessageText:
    //
    // The DNS server could not find a set of root hints.
    //
    int DNS_ERROR_CANNOT_FIND_ROOT_HINTS = 9564;

    // DNS_ERROR_INCONSISTENT_ROOT_HINTS 0x0000255D
    //
    // MessageId: DNS_ERROR_INCONSISTENT_ROOT_HINTS
    //
    // MessageText:
    //
    // The DNS server found root hints but they were not consistent across all
    // adapters.
    //
    int DNS_ERROR_INCONSISTENT_ROOT_HINTS = 9565;

    // DNS_ERROR_DWORD_VALUE_TOO_SMALL 0x0000255E
    //
    // MessageId: DNS_ERROR_DWORD_VALUE_TOO_SMALL
    //
    // MessageText:
    //
    // The specified value is too small for this parameter.
    //
    int DNS_ERROR_DWORD_VALUE_TOO_SMALL = 9566;

    // DNS_ERROR_DWORD_VALUE_TOO_LARGE 0x0000255F
    //
    // MessageId: DNS_ERROR_DWORD_VALUE_TOO_LARGE
    //
    // MessageText:
    //
    // The specified value is too large for this parameter.
    //
    int DNS_ERROR_DWORD_VALUE_TOO_LARGE = 9567;

    // DNS_ERROR_BACKGROUND_LOADING 0x00002560
    //
    // MessageId: DNS_ERROR_BACKGROUND_LOADING
    //
    // MessageText:
    //
    // This operation is not allowed while the DNS server is loading zones in
    // the background. Please try again later.
    //
    int DNS_ERROR_BACKGROUND_LOADING = 9568;

    // DNS_ERROR_NOT_ALLOWED_ON_RODC 0x00002561
    //
    // MessageId: DNS_ERROR_NOT_ALLOWED_ON_RODC
    //
    // MessageText:
    //
    // The operation requested is not permitted on against a DNS server running
    // on a read-only DC.
    //
    int DNS_ERROR_NOT_ALLOWED_ON_RODC = 9569;

    // DNS_ERROR_NOT_ALLOWED_UNDER_DNAME 0x00002562
    //
    // MessageId: DNS_ERROR_NOT_ALLOWED_UNDER_DNAME
    //
    // MessageText:
    //
    // No data is allowed to exist underneath a DNAME record.
    //
    int DNS_ERROR_NOT_ALLOWED_UNDER_DNAME = 9570;

    //
    // Zone errors
    //

    int DNS_ERROR_ZONE_BASE = 9600;

    // DNS_ERROR_ZONE_DOES_NOT_EXIST=0x00002581
    //
    // MessageId: DNS_ERROR_ZONE_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // DNS zone does not exist.
    //
    int DNS_ERROR_ZONE_DOES_NOT_EXIST = 9601;

    // DNS_ERROR_NO_ZONE_INFO=0x00002582
    //
    // MessageId: DNS_ERROR_NO_ZONE_INFO
    //
    // MessageText:
    //
    // DNS zone information not available.
    //
    int DNS_ERROR_NO_ZONE_INFO = 9602;

    // DNS_ERROR_INVALID_ZONE_OPERATION 0x00002583
    //
    // MessageId: DNS_ERROR_INVALID_ZONE_OPERATION
    //
    // MessageText:
    //
    // Invalid operation for DNS zone.
    //
    int DNS_ERROR_INVALID_ZONE_OPERATION = 9603;

    // DNS_ERROR_ZONE_CONFIGURATION_ERROR 0x00002584
    //
    // MessageId: DNS_ERROR_ZONE_CONFIGURATION_ERROR
    //
    // MessageText:
    //
    // Invalid DNS zone configuration.
    //
    int DNS_ERROR_ZONE_CONFIGURATION_ERROR = 9604;

    // DNS_ERROR_ZONE_HAS_NO_SOA_RECORD 0x00002585
    //
    // MessageId: DNS_ERROR_ZONE_HAS_NO_SOA_RECORD
    //
    // MessageText:
    //
    // DNS zone has no start of authority (SOA) record.
    //
    int DNS_ERROR_ZONE_HAS_NO_SOA_RECORD = 9605;

    // DNS_ERROR_ZONE_HAS_NO_NS_RECORDS 0x00002586
    //
    // MessageId: DNS_ERROR_ZONE_HAS_NO_NS_RECORDS
    //
    // MessageText:
    //
    // DNS zone has no Name Server (NS) record.
    //
    int DNS_ERROR_ZONE_HAS_NO_NS_RECORDS = 9606;

    // DNS_ERROR_ZONE_LOCKED=0x00002587
    //
    // MessageId: DNS_ERROR_ZONE_LOCKED
    //
    // MessageText:
    //
    // DNS zone is locked.
    //
    int DNS_ERROR_ZONE_LOCKED = 9607;

    // DNS_ERROR_ZONE_CREATION_FAILED=0x00002588
    //
    // MessageId: DNS_ERROR_ZONE_CREATION_FAILED
    //
    // MessageText:
    //
    // DNS zone creation failed.
    //
    int DNS_ERROR_ZONE_CREATION_FAILED = 9608;

    // DNS_ERROR_ZONE_ALREADY_EXISTS=0x00002589
    //
    // MessageId: DNS_ERROR_ZONE_ALREADY_EXISTS
    //
    // MessageText:
    //
    // DNS zone already exists.
    //
    int DNS_ERROR_ZONE_ALREADY_EXISTS = 9609;

    // DNS_ERROR_AUTOZONE_ALREADY_EXISTS 0x0000258a
    //
    // MessageId: DNS_ERROR_AUTOZONE_ALREADY_EXISTS
    //
    // MessageText:
    //
    // DNS automatic zone already exists.
    //
    int DNS_ERROR_AUTOZONE_ALREADY_EXISTS = 9610;

    // DNS_ERROR_INVALID_ZONE_TYPE=0x0000258b
    //
    // MessageId: DNS_ERROR_INVALID_ZONE_TYPE
    //
    // MessageText:
    //
    // Invalid DNS zone type.
    //
    int DNS_ERROR_INVALID_ZONE_TYPE = 9611;

    // DNS_ERROR_SECONDARY_REQUIRES_MASTER_IP 0x0000258c
    //
    // MessageId: DNS_ERROR_SECONDARY_REQUIRES_MASTER_IP
    //
    // MessageText:
    //
    // Secondary DNS zone requires master IP address.
    //
    int DNS_ERROR_SECONDARY_REQUIRES_MASTER_IP = 9612;

    // DNS_ERROR_ZONE_NOT_SECONDARY=0x0000258d
    //
    // MessageId: DNS_ERROR_ZONE_NOT_SECONDARY
    //
    // MessageText:
    //
    // DNS zone not secondary.
    //
    int DNS_ERROR_ZONE_NOT_SECONDARY = 9613;

    // DNS_ERROR_NEED_SECONDARY_ADDRESSES 0x0000258e
    //
    // MessageId: DNS_ERROR_NEED_SECONDARY_ADDRESSES
    //
    // MessageText:
    //
    // Need secondary IP address.
    //
    int DNS_ERROR_NEED_SECONDARY_ADDRESSES = 9614;

    // DNS_ERROR_WINS_INIT_FAILED=0x0000258f
    //
    // MessageId: DNS_ERROR_WINS_INIT_FAILED
    //
    // MessageText:
    //
    // WINS initialization failed.
    //
    int DNS_ERROR_WINS_INIT_FAILED = 9615;

    // DNS_ERROR_NEED_WINS_SERVERS=0x00002590
    //
    // MessageId: DNS_ERROR_NEED_WINS_SERVERS
    //
    // MessageText:
    //
    // Need WINS servers.
    //
    int DNS_ERROR_NEED_WINS_SERVERS = 9616;

    // DNS_ERROR_NBSTAT_INIT_FAILED=0x00002591
    //
    // MessageId: DNS_ERROR_NBSTAT_INIT_FAILED
    //
    // MessageText:
    //
    // NBTSTAT initialization call failed.
    //
    int DNS_ERROR_NBSTAT_INIT_FAILED = 9617;

    // DNS_ERROR_SOA_DELETE_INVALID=0x00002592
    //
    // MessageId: DNS_ERROR_SOA_DELETE_INVALID
    //
    // MessageText:
    //
    // Invalid delete of start of authority (SOA)
    //
    int DNS_ERROR_SOA_DELETE_INVALID = 9618;

    // DNS_ERROR_FORWARDER_ALREADY_EXISTS 0x00002593
    //
    // MessageId: DNS_ERROR_FORWARDER_ALREADY_EXISTS
    //
    // MessageText:
    //
    // A conditional forwarding zone already exists for that name.
    //
    int DNS_ERROR_FORWARDER_ALREADY_EXISTS = 9619;

    // DNS_ERROR_ZONE_REQUIRES_MASTER_IP 0x00002594
    //
    // MessageId: DNS_ERROR_ZONE_REQUIRES_MASTER_IP
    //
    // MessageText:
    //
    // This zone must be configured with one or more master DNS server IP
    // addresses.
    //
    int DNS_ERROR_ZONE_REQUIRES_MASTER_IP = 9620;

    // DNS_ERROR_ZONE_IS_SHUTDOWN=0x00002595
    //
    // MessageId: DNS_ERROR_ZONE_IS_SHUTDOWN
    //
    // MessageText:
    //
    // The operation cannot be performed because this zone is shutdown.
    //
    int DNS_ERROR_ZONE_IS_SHUTDOWN = 9621;

    //
    // Datafile errors
    //

    int DNS_ERROR_DATAFILE_BASE = 9650;

    // DNS=0x000025b3
    //
    // MessageId: DNS_ERROR_PRIMARY_REQUIRES_DATAFILE
    //
    // MessageText:
    //
    // Primary DNS zone requires datafile.
    //
    int DNS_ERROR_PRIMARY_REQUIRES_DATAFILE = 9651;

    // DNS=0x000025b4
    //
    // MessageId: DNS_ERROR_INVALID_DATAFILE_NAME
    //
    // MessageText:
    //
    // Invalid datafile name for DNS zone.
    //
    int DNS_ERROR_INVALID_DATAFILE_NAME = 9652;

    // DNS=0x000025b5
    //
    // MessageId: DNS_ERROR_DATAFILE_OPEN_FAILURE
    //
    // MessageText:
    //
    // Failed to open datafile for DNS zone.
    //
    int DNS_ERROR_DATAFILE_OPEN_FAILURE = 9653;

    // DNS=0x000025b6
    //
    // MessageId: DNS_ERROR_FILE_WRITEBACK_FAILED
    //
    // MessageText:
    //
    // Failed to write datafile for DNS zone.
    //
    int DNS_ERROR_FILE_WRITEBACK_FAILED = 9654;

    // DNS=0x000025b7
    //
    // MessageId: DNS_ERROR_DATAFILE_PARSING
    //
    // MessageText:
    //
    // Failure while reading datafile for DNS zone.
    //
    int DNS_ERROR_DATAFILE_PARSING = 9655;

    //
    // Database errors
    //

    int DNS_ERROR_DATABASE_BASE = 9700;

    // DNS_ERROR_RECORD_DOES_NOT_EXIST 0x000025e5
    //
    // MessageId: DNS_ERROR_RECORD_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // DNS record does not exist.
    //
    int DNS_ERROR_RECORD_DOES_NOT_EXIST = 9701;

    // DNS_ERROR_RECORD_FORMAT=0x000025e6
    //
    // MessageId: DNS_ERROR_RECORD_FORMAT
    //
    // MessageText:
    //
    // DNS record format error.
    //
    int DNS_ERROR_RECORD_FORMAT = 9702;

    // DNS_ERROR_NODE_CREATION_FAILED=0x000025e7
    //
    // MessageId: DNS_ERROR_NODE_CREATION_FAILED
    //
    // MessageText:
    //
    // Node creation failure in DNS.
    //
    int DNS_ERROR_NODE_CREATION_FAILED = 9703;

    // DNS_ERROR_UNKNOWN_RECORD_TYPE=0x000025e8
    //
    // MessageId: DNS_ERROR_UNKNOWN_RECORD_TYPE
    //
    // MessageText:
    //
    // Unknown DNS record type.
    //
    int DNS_ERROR_UNKNOWN_RECORD_TYPE = 9704;

    // DNS_ERROR_RECORD_TIMED_OUT=0x000025e9
    //
    // MessageId: DNS_ERROR_RECORD_TIMED_OUT
    //
    // MessageText:
    //
    // DNS record timed out.
    //
    int DNS_ERROR_RECORD_TIMED_OUT = 9705;

    // DNS_ERROR_NAME_NOT_IN_ZONE=0x000025ea
    //
    // MessageId: DNS_ERROR_NAME_NOT_IN_ZONE
    //
    // MessageText:
    //
    // Name not in DNS zone.
    //
    int DNS_ERROR_NAME_NOT_IN_ZONE = 9706;

    // DNS_ERROR_CNAME_LOOP=0x000025eb
    //
    // MessageId: DNS_ERROR_CNAME_LOOP
    //
    // MessageText:
    //
    // CNAME loop detected.
    //
    int DNS_ERROR_CNAME_LOOP = 9707;

    // DNS_ERROR_NODE_IS_CNAME=0x000025ec
    //
    // MessageId: DNS_ERROR_NODE_IS_CNAME
    //
    // MessageText:
    //
    // Node is a CNAME DNS record.
    //
    int DNS_ERROR_NODE_IS_CNAME = 9708;

    // DNS_ERROR_CNAME_COLLISION=0x000025ed
    //
    // MessageId: DNS_ERROR_CNAME_COLLISION
    //
    // MessageText:
    //
    // A CNAME record already exists for given name.
    //
    int DNS_ERROR_CNAME_COLLISION = 9709;

    // DNS_ERROR_RECORD_ONLY_AT_ZONE_ROOT 0x000025ee
    //
    // MessageId: DNS_ERROR_RECORD_ONLY_AT_ZONE_ROOT
    //
    // MessageText:
    //
    // Record only at DNS zone root.
    //
    int DNS_ERROR_RECORD_ONLY_AT_ZONE_ROOT = 9710;

    // DNS_ERROR_RECORD_ALREADY_EXISTS 0x000025ef
    //
    // MessageId: DNS_ERROR_RECORD_ALREADY_EXISTS
    //
    // MessageText:
    //
    // DNS record already exists.
    //
    int DNS_ERROR_RECORD_ALREADY_EXISTS = 9711;

    // DNS_ERROR_SECONDARY_DATA=0x000025f0
    //
    // MessageId: DNS_ERROR_SECONDARY_DATA
    //
    // MessageText:
    //
    // Secondary DNS zone data error.
    //
    int DNS_ERROR_SECONDARY_DATA = 9712;

    // DNS_ERROR_NO_CREATE_CACHE_DATA=0x000025f1
    //
    // MessageId: DNS_ERROR_NO_CREATE_CACHE_DATA
    //
    // MessageText:
    //
    // Could not create DNS cache data.
    //
    int DNS_ERROR_NO_CREATE_CACHE_DATA = 9713;

    // DNS_ERROR_NAME_DOES_NOT_EXIST=0x000025f2
    //
    // MessageId: DNS_ERROR_NAME_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // DNS name does not exist.
    //
    int DNS_ERROR_NAME_DOES_NOT_EXIST = 9714;

    // DNS_WARNING_PTR_CREATE_FAILED=0x000025f3
    //
    // MessageId: DNS_WARNING_PTR_CREATE_FAILED
    //
    // MessageText:
    //
    // Could not create pointer (PTR) record.
    //
    int DNS_WARNING_PTR_CREATE_FAILED = 9715;

    // DNS_WARNING_DOMAIN_UNDELETED=0x000025f4
    //
    // MessageId: DNS_WARNING_DOMAIN_UNDELETED
    //
    // MessageText:
    //
    // DNS domain was undeleted.
    //
    int DNS_WARNING_DOMAIN_UNDELETED = 9716;

    // DNS_ERROR_DS_UNAVAILABLE=0x000025f5
    //
    // MessageId: DNS_ERROR_DS_UNAVAILABLE
    //
    // MessageText:
    //
    // The directory service is unavailable.
    //
    int DNS_ERROR_DS_UNAVAILABLE = 9717;

    // DNS_ERROR_DS_ZONE_ALREADY_EXISTS 0x000025f6
    //
    // MessageId: DNS_ERROR_DS_ZONE_ALREADY_EXISTS
    //
    // MessageText:
    //
    // DNS zone already exists in the directory service.
    //
    int DNS_ERROR_DS_ZONE_ALREADY_EXISTS = 9718;

    // DNS_ERROR_NO_BOOTFILE_IF_DS_ZONE 0x000025f7
    //
    // MessageId: DNS_ERROR_NO_BOOTFILE_IF_DS_ZONE
    //
    // MessageText:
    //
    // DNS server not creating or reading the boot file for the directory
    // service integrated DNS zone.
    //
    int DNS_ERROR_NO_BOOTFILE_IF_DS_ZONE = 9719;

    // DNS_ERROR_NODE_IS_DNAME=0x000025f8
    //
    // MessageId: DNS_ERROR_NODE_IS_DNAME
    //
    // MessageText:
    //
    // Node is a DNAME DNS record.
    //
    int DNS_ERROR_NODE_IS_DNAME = 9720;

    // DNS_ERROR_DNAME_COLLISION=0x000025f9
    //
    // MessageId: DNS_ERROR_DNAME_COLLISION
    //
    // MessageText:
    //
    // A DNAME record already exists for given name.
    //
    int DNS_ERROR_DNAME_COLLISION = 9721;

    // DNS_ERROR_ALIAS_LOOP=0x000025fa
    //
    // MessageId: DNS_ERROR_ALIAS_LOOP
    //
    // MessageText:
    //
    // An alias loop has been detected with either CNAME or DNAME records.
    //
    int DNS_ERROR_ALIAS_LOOP = 9722;

    //
    // Operation errors
    //

    int DNS_ERROR_OPERATION_BASE = 9750;

    // DNS_INFO_AXFR_COMPLETE=0x00002617
    //
    // MessageId: DNS_INFO_AXFR_COMPLETE
    //
    // MessageText:
    //
    // DNS AXFR (zone transfer) complete.
    //
    int DNS_INFO_AXFR_COMPLETE = 9751;

    // DNS_ERROR_AXFR=0x00002618
    //
    // MessageId: DNS_ERROR_AXFR
    //
    // MessageText:
    //
    // DNS zone transfer failed.
    //
    int DNS_ERROR_AXFR = 9752;

    // DNS_INFO_ADDED_LOCAL_WINS=0x00002619
    //
    // MessageId: DNS_INFO_ADDED_LOCAL_WINS
    //
    // MessageText:
    //
    // Added local WINS server.
    //
    int DNS_INFO_ADDED_LOCAL_WINS = 9753;

    //
    // Secure update
    //

    int DNS_ERROR_SECURE_BASE = 9800;

    // DNS_STATUS_CONTINUE_NEEDED=0x00002649
    //
    // MessageId: DNS_STATUS_CONTINUE_NEEDED
    //
    // MessageText:
    //
    // Secure update call needs to continue update request.
    //
    int DNS_STATUS_CONTINUE_NEEDED = 9801;

    //
    // Setup errors
    //

    int DNS_ERROR_SETUP_BASE = 9850;

    // DNS_ERROR_NO_TCPIP=0x0000267b
    //
    // MessageId: DNS_ERROR_NO_TCPIP
    //
    // MessageText:
    //
    // TCP/IP network protocol not installed.
    //
    int DNS_ERROR_NO_TCPIP = 9851;

    // DNS_ERROR_NO_DNS_SERVERS=0x0000267c
    //
    // MessageId: DNS_ERROR_NO_DNS_SERVERS
    //
    // MessageText:
    //
    // No DNS servers configured for local system.
    //
    int DNS_ERROR_NO_DNS_SERVERS = 9852;

    //
    // Directory partition (DP) errors
    //

    int DNS_ERROR_DP_BASE = 9900;

    // DNS_ERROR_DP_DOES_NOT_EXIST=0x000026ad
    //
    // MessageId: DNS_ERROR_DP_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // The specified directory partition does not exist.
    //
    int DNS_ERROR_DP_DOES_NOT_EXIST = 9901;

    // DNS_ERROR_DP_ALREADY_EXISTS=0x000026ae
    //
    // MessageId: DNS_ERROR_DP_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The specified directory partition already exists.
    //
    int DNS_ERROR_DP_ALREADY_EXISTS = 9902;

    // DNS_ERROR_DP_NOT_ENLISTED=0x000026af
    //
    // MessageId: DNS_ERROR_DP_NOT_ENLISTED
    //
    // MessageText:
    //
    // This DNS server is not enlisted in the specified directory partition.
    //
    int DNS_ERROR_DP_NOT_ENLISTED = 9903;

    // DNS_ERROR_DP_ALREADY_ENLISTED=0x000026b0
    //
    // MessageId: DNS_ERROR_DP_ALREADY_ENLISTED
    //
    // MessageText:
    //
    // This DNS server is already enlisted in the specified directory partition.
    //
    int DNS_ERROR_DP_ALREADY_ENLISTED = 9904;

    // DNS_ERROR_DP_NOT_AVAILABLE=0x000026b1
    //
    // MessageId: DNS_ERROR_DP_NOT_AVAILABLE
    //
    // MessageText:
    //
    // The directory partition is not available at this time. Please wait a few
    // minutes and try again.
    //
    int DNS_ERROR_DP_NOT_AVAILABLE = 9905;

    // DNS_ERROR_DP_FSMO_ERROR=0x000026b2
    //
    // MessageId: DNS_ERROR_DP_FSMO_ERROR
    //
    // MessageText:
    //
    // The application directory partition operation failed. The domain
    // controller holding the domain naming master role is down or unable to
    // service the request or is not running Windows Server=2003.
    //
    int DNS_ERROR_DP_FSMO_ERROR = 9906;

    // /////////////////////////////////////////////////
    // =//
    // =End of DNS Error Codes=//
    // =//
    // =9000 to=9999=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =WinSock Error Codes=//
    // =//
    // =10000 to=11999=//
    // /////////////////////////////////////////////////

    //
    // WinSock error codes are also defined in WinSock.h
    // and WinSock2.h, hence the IFDEF
    //
    int WSABASEERR = 10000;
    //
    // MessageId: WSAEINTR
    //
    // MessageText:
    //
    // A blocking operation was interrupted by a call to WSACancelBlockingCall.
    //
    int WSAEINTR = 10004;

    //
    // MessageId: WSAEBADF
    //
    // MessageText:
    //
    // The file handle supplied is not valid.
    //
    int WSAEBADF = 10009;

    //
    // MessageId: WSAEACCES
    //
    // MessageText:
    //
    // An attempt was made to access a socket in a way forbidden by its access
    // permissions.
    //
    int WSAEACCES = 10013;

    //
    // MessageId: WSAEFAULT
    //
    // MessageText:
    //
    // The system detected an invalid pointer address in attempting to use a
    // pointer argument in a call.
    //
    int WSAEFAULT = 10014;

    //
    // MessageId: WSAEINVAL
    //
    // MessageText:
    //
    // An invalid argument was supplied.
    //
    int WSAEINVAL = 10022;

    //
    // MessageId: WSAEMFILE
    //
    // MessageText:
    //
    // Too many open sockets.
    //
    int WSAEMFILE = 10024;

    //
    // MessageId: WSAEWOULDBLOCK
    //
    // MessageText:
    //
    // A non-blocking socket operation could not be completed immediately.
    //
    int WSAEWOULDBLOCK = 10035;

    //
    // MessageId: WSAEINPROGRESS
    //
    // MessageText:
    //
    // A blocking operation is currently executing.
    //
    int WSAEINPROGRESS = 10036;

    //
    // MessageId: WSAEALREADY
    //
    // MessageText:
    //
    // An operation was attempted on a non-blocking socket that already had an
    // operation in progress.
    //
    int WSAEALREADY = 10037;

    //
    // MessageId: WSAENOTSOCK
    //
    // MessageText:
    //
    // An operation was attempted on something that is not a socket.
    //
    int WSAENOTSOCK = 10038;

    //
    // MessageId: WSAEDESTADDRREQ
    //
    // MessageText:
    //
    // A required address was omitted from an operation on a socket.
    //
    int WSAEDESTADDRREQ = 10039;

    //
    // MessageId: WSAEMSGSIZE
    //
    // MessageText:
    //
    // A message sent on a datagram socket was larger than the internal message
    // buffer or some other network limit, or the buffer used to receive a
    // datagram into was smaller than the datagram itself.
    //
    int WSAEMSGSIZE = 10040;

    //
    // MessageId: WSAEPROTOTYPE
    //
    // MessageText:
    //
    // A protocol was specified in the socket function call that does not
    // support the semantics of the socket type requested.
    //
    int WSAEPROTOTYPE = 10041;

    //
    // MessageId: WSAENOPROTOOPT
    //
    // MessageText:
    //
    // An unknown, invalid, or unsupported option or level was specified in a
    // getsockopt or setsockopt call.
    //
    int WSAENOPROTOOPT = 10042;

    //
    // MessageId: WSAEPROTONOSUPPORT
    //
    // MessageText:
    //
    // The requested protocol has not been configured into the system, or no
    // implementation for it exists.
    //
    int WSAEPROTONOSUPPORT = 10043;

    //
    // MessageId: WSAESOCKTNOSUPPORT
    //
    // MessageText:
    //
    // The support for the specified socket type does not exist in this address
    // family.
    //
    int WSAESOCKTNOSUPPORT = 10044;

    //
    // MessageId: WSAEOPNOTSUPP
    //
    // MessageText:
    //
    // The attempted operation is not supported for the type of object
    // referenced.
    //
    int WSAEOPNOTSUPP = 10045;

    //
    // MessageId: WSAEPFNOSUPPORT
    //
    // MessageText:
    //
    // The protocol family has not been configured into the system or no
    // implementation for it exists.
    //
    int WSAEPFNOSUPPORT = 10046;

    //
    // MessageId: WSAEAFNOSUPPORT
    //
    // MessageText:
    //
    // An address incompatible with the requested protocol was used.
    //
    int WSAEAFNOSUPPORT = 10047;

    //
    // MessageId: WSAEADDRINUSE
    //
    // MessageText:
    //
    // Only one usage of each socket address (protocol/network address/port) is
    // normally permitted.
    //
    int WSAEADDRINUSE = 10048;

    //
    // MessageId: WSAEADDRNOTAVAIL
    //
    // MessageText:
    //
    // The requested address is not valid in its context.
    //
    int WSAEADDRNOTAVAIL = 10049;

    //
    // MessageId: WSAENETDOWN
    //
    // MessageText:
    //
    // A socket operation encountered a dead network.
    //
    int WSAENETDOWN = 10050;

    //
    // MessageId: WSAENETUNREACH
    //
    // MessageText:
    //
    // A socket operation was attempted to an unreachable network.
    //
    int WSAENETUNREACH = 10051;

    //
    // MessageId: WSAENETRESET
    //
    // MessageText:
    //
    // The connection has been broken due to keep-alive activity detecting a
    // failure while the operation was in progress.
    //
    int WSAENETRESET = 10052;

    //
    // MessageId: WSAECONNABORTED
    //
    // MessageText:
    //
    // An established connection was aborted by the software in your host
    // machine.
    //
    int WSAECONNABORTED = 10053;

    //
    // MessageId: WSAECONNRESET
    //
    // MessageText:
    //
    // An existing connection was forcibly closed by the remote host.
    //
    int WSAECONNRESET = 10054;

    //
    // MessageId: WSAENOBUFS
    //
    // MessageText:
    //
    // An operation on a socket could not be performed because the system lacked
    // sufficient buffer space or because a queue was full.
    //
    int WSAENOBUFS = 10055;

    //
    // MessageId: WSAEISCONN
    //
    // MessageText:
    //
    // A connect request was made on an already connected socket.
    //
    int WSAEISCONN = 10056;

    //
    // MessageId: WSAENOTCONN
    //
    // MessageText:
    //
    // A request to send or receive data was disallowed because the socket is
    // not connected and (when sending on a datagram socket using a sendto cal;
    // no address was supplied.
    //
    int WSAENOTCONN = 10057;

    //
    // MessageId: WSAESHUTDOWN
    //
    // MessageText:
    //
    // A request to send or receive data was disallowed because the socket had
    // already been shut down in that direction with a previous shutdown call.
    //
    int WSAESHUTDOWN = 10058;

    //
    // MessageId: WSAETOOMANYREFS
    //
    // MessageText:
    //
    // Too many references to some kernel object.
    //
    int WSAETOOMANYREFS = 10059;

    //
    // MessageId: WSAETIMEDOUT
    //
    // MessageText:
    //
    // A connection attempt failed because the connected party did not properly
    // respond after a period of time, or established connection failed because
    // connected host has failed to respond.
    //
    int WSAETIMEDOUT = 10060;

    //
    // MessageId: WSAECONNREFUSED
    //
    // MessageText:
    //
    // No connection could be made because the target machine actively refused
    // it.
    //
    int WSAECONNREFUSED = 10061;

    //
    // MessageId: WSAELOOP
    //
    // MessageText:
    //
    // Cannot translate name.
    //
    int WSAELOOP = 10062;

    //
    // MessageId: WSAENAMETOOLONG
    //
    // MessageText:
    //
    // Name component or name was too long.
    //
    int WSAENAMETOOLONG = 10063;

    //
    // MessageId: WSAEHOSTDOWN
    //
    // MessageText:
    //
    // A socket operation failed because the destination host was down.
    //
    int WSAEHOSTDOWN = 10064;

    //
    // MessageId: WSAEHOSTUNREACH
    //
    // MessageText:
    //
    // A socket operation was attempted to an unreachable host.
    //
    int WSAEHOSTUNREACH = 10065;

    //
    // MessageId: WSAENOTEMPTY
    //
    // MessageText:
    //
    // Cannot remove a directory that is not empty.
    //
    int WSAENOTEMPTY = 10066;

    //
    // MessageId: WSAEPROCLIM
    //
    // MessageText:
    //
    // A Windows Sockets implementation may have a limit on the number of
    // applications that may use it simultaneously.
    //
    int WSAEPROCLIM = 10067;

    //
    // MessageId: WSAEUSERS
    //
    // MessageText:
    //
    // Ran out of quota.
    //
    int WSAEUSERS = 10068;

    //
    // MessageId: WSAEDQUOT
    //
    // MessageText:
    //
    // Ran out of disk quota.
    //
    int WSAEDQUOT = 10069;

    //
    // MessageId: WSAESTALE
    //
    // MessageText:
    //
    // File handle reference is no longer available.
    //
    int WSAESTALE = 10070;

    //
    // MessageId: WSAEREMOTE
    //
    // MessageText:
    //
    // Item is not available locally.
    //
    int WSAEREMOTE = 10071;

    //
    // MessageId: WSASYSNOTREADY
    //
    // MessageText:
    //
    // WSAStartup cannot function at this time because the underlying system it
    // uses to provide network services is currently unavailable.
    //
    int WSASYSNOTREADY = 10091;

    //
    // MessageId: WSAVERNOTSUPPORTED
    //
    // MessageText:
    //
    // The Windows Sockets version requested is not supported.
    //
    int WSAVERNOTSUPPORTED = 10092;

    //
    // MessageId: WSANOTINITIALISED
    //
    // MessageText:
    //
    // Either the application has not called WSAStartup, or WSAStartup failed.
    //
    int WSANOTINITIALISED = 10093;

    //
    // MessageId: WSAEDISCON
    //
    // MessageText:
    //
    // Returned by WSARecv or WSARecvFrom to indicate the remote party has
    // initiated a graceful shutdown sequence.
    //
    int WSAEDISCON = 10101;

    //
    // MessageId: WSAENOMORE
    //
    // MessageText:
    //
    // No more results can be returned by WSALookupServiceNext.
    //
    int WSAENOMORE = 10102;

    //
    // MessageId: WSAECANCELLED
    //
    // MessageText:
    //
    // A call to WSALookupServiceEnd was made while this call was still
    // processing. The call has been canceled.
    //
    int WSAECANCELLED = 10103;

    //
    // MessageId: WSAEINVALIDPROCTABLE
    //
    // MessageText:
    //
    // The procedure call table is invalid.
    //
    int WSAEINVALIDPROCTABLE = 10104;

    //
    // MessageId: WSAEINVALIDPROVIDER
    //
    // MessageText:
    //
    // The requested service provider is invalid.
    //
    int WSAEINVALIDPROVIDER = 10105;

    //
    // MessageId: WSAEPROVIDERFAILEDINIT
    //
    // MessageText:
    //
    // The requested service provider could not be loaded or initialized.
    //
    int WSAEPROVIDERFAILEDINIT = 10106;

    //
    // MessageId: WSASYSCALLFAILURE
    //
    // MessageText:
    //
    // A system call that should never fail has failed.
    //
    int WSASYSCALLFAILURE = 10107;

    //
    // MessageId: WSASERVICE_NOT_FOUND
    //
    // MessageText:
    //
    // No such service is known. The service cannot be found in the specified
    // name space.
    //
    int WSASERVICE_NOT_FOUND = 10108;

    //
    // MessageId: WSATYPE_NOT_FOUND
    //
    // MessageText:
    //
    // The specified class was not found.
    //
    int WSATYPE_NOT_FOUND = 10109;

    //
    // MessageId: WSA_E_NO_MORE
    //
    // MessageText:
    //
    // No more results can be returned by WSALookupServiceNext.
    //
    int WSA_E_NO_MORE = 10110;

    //
    // MessageId: WSA_E_CANCELLED
    //
    // MessageText:
    //
    // A call to WSALookupServiceEnd was made while this call was still
    // processing. The call has been canceled.
    //
    int WSA_E_CANCELLED = 10111;

    //
    // MessageId: WSAEREFUSED
    //
    // MessageText:
    //
    // A database query failed because it was actively refused.
    //
    int WSAEREFUSED = 10112;

    //
    // MessageId: WSAHOST_NOT_FOUND
    //
    // MessageText:
    //
    // No such host is known.
    //
    int WSAHOST_NOT_FOUND = 11001;

    //
    // MessageId: WSATRY_AGAIN
    //
    // MessageText:
    //
    // This is usually a temporary error during hostname resolution and means
    // that the local server did not receive a response from an authoritative
    // server.
    //
    int WSATRY_AGAIN = 11002;

    //
    // MessageId: WSANO_RECOVERY
    //
    // MessageText:
    //
    // A non-recoverable error occurred during a database lookup.
    //
    int WSANO_RECOVERY = 11003;

    //
    // MessageId: WSANO_DATA
    //
    // MessageText:
    //
    // The requested name is valid, but no data of the requested type was found.
    //
    int WSANO_DATA = 11004;

    //
    // MessageId: WSA_QOS_RECEIVERS
    //
    // MessageText:
    //
    // At least one reserve has arrived.
    //
    int WSA_QOS_RECEIVERS = 11005;

    //
    // MessageId: WSA_QOS_SENDERS
    //
    // MessageText:
    //
    // At least one path has arrived.
    //
    int WSA_QOS_SENDERS = 11006;

    //
    // MessageId: WSA_QOS_NO_SENDERS
    //
    // MessageText:
    //
    // There are no senders.
    //
    int WSA_QOS_NO_SENDERS = 11007;

    //
    // MessageId: WSA_QOS_NO_RECEIVERS
    //
    // MessageText:
    //
    // There are no receivers.
    //
    int WSA_QOS_NO_RECEIVERS = 11008;

    //
    // MessageId: WSA_QOS_REQUEST_CONFIRMED
    //
    // MessageText:
    //
    // Reserve has been confirmed.
    //
    int WSA_QOS_REQUEST_CONFIRMED = 11009;

    //
    // MessageId: WSA_QOS_ADMISSION_FAILURE
    //
    // MessageText:
    //
    // Error due to lack of resources.
    //
    int WSA_QOS_ADMISSION_FAILURE = 11010;

    //
    // MessageId: WSA_QOS_POLICY_FAILURE
    //
    // MessageText:
    //
    // Rejected for administrative reasons - bad credentials.
    //
    int WSA_QOS_POLICY_FAILURE = 11011;

    //
    // MessageId: WSA_QOS_BAD_STYLE
    //
    // MessageText:
    //
    // Unknown or conflicting style.
    //
    int WSA_QOS_BAD_STYLE = 11012;

    //
    // MessageId: WSA_QOS_BAD_OBJECT
    //
    // MessageText:
    //
    // Problem with some part of the filterspec or providerspecific buffer in
    // general.
    //
    int WSA_QOS_BAD_OBJECT = 11013;

    //
    // MessageId: WSA_QOS_TRAFFIC_CTRL_ERROR
    //
    // MessageText:
    //
    // Problem with some part of the flowspec.
    //
    int WSA_QOS_TRAFFIC_CTRL_ERROR = 11014;

    //
    // MessageId: WSA_QOS_GENERIC_ERROR
    //
    // MessageText:
    //
    // General QOS error.
    //
    int WSA_QOS_GENERIC_ERROR = 11015;

    //
    // MessageId: WSA_QOS_ESERVICETYPE
    //
    // MessageText:
    //
    // An invalid or unrecognized service type was found in the flowspec.
    //
    int WSA_QOS_ESERVICETYPE = 11016;

    //
    // MessageId: WSA_QOS_EFLOWSPEC
    //
    // MessageText:
    //
    // An invalid or inconsistent flowspec was found in the QOS structure.
    //
    int WSA_QOS_EFLOWSPEC = 11017;

    //
    // MessageId: WSA_QOS_EPROVSPECBUF
    //
    // MessageText:
    //
    // Invalid QOS provider-specific buffer.
    //
    int WSA_QOS_EPROVSPECBUF = 11018;

    //
    // MessageId: WSA_QOS_EFILTERSTYLE
    //
    // MessageText:
    //
    // An invalid QOS filter style was used.
    //
    int WSA_QOS_EFILTERSTYLE = 11019;

    //
    // MessageId: WSA_QOS_EFILTERTYPE
    //
    // MessageText:
    //
    // An invalid QOS filter type was used.
    //
    int WSA_QOS_EFILTERTYPE = 11020;

    //
    // MessageId: WSA_QOS_EFILTERCOUNT
    //
    // MessageText:
    //
    // An incorrect number of QOS FILTERSPECs were specified in the
    // FLOWDESCRIPTOR.
    //
    int WSA_QOS_EFILTERCOUNT = 11021;

    //
    // MessageId: WSA_QOS_EOBJLENGTH
    //
    // MessageText:
    //
    // An object with an invalid ObjectLength field was specified in the QOS
    // provider-specific buffer.
    //
    int WSA_QOS_EOBJLENGTH = 11022;

    //
    // MessageId: WSA_QOS_EFLOWCOUNT
    //
    // MessageText:
    //
    // An incorrect number of flow descriptors was specified in the QOS
    // structure.
    //
    int WSA_QOS_EFLOWCOUNT = 11023;

    //
    // MessageId: WSA_QOS_EUNKOWNPSOBJ
    //
    // MessageText:
    //
    // An unrecognized object was found in the QOS provider-specific buffer.
    //
    int WSA_QOS_EUNKOWNPSOBJ = 11024;

    //
    // MessageId: WSA_QOS_EPOLICYOBJ
    //
    // MessageText:
    //
    // An invalid policy object was found in the QOS provider-specific buffer.
    //
    int WSA_QOS_EPOLICYOBJ = 11025;

    //
    // MessageId: WSA_QOS_EFLOWDESC
    //
    // MessageText:
    //
    // An invalid QOS flow descriptor was found in the flow descriptor list.
    //
    int WSA_QOS_EFLOWDESC = 11026;

    //
    // MessageId: WSA_QOS_EPSFLOWSPEC
    //
    // MessageText:
    //
    // An invalid or inconsistent flowspec was found in the QOS provider
    // specific buffer.
    //
    int WSA_QOS_EPSFLOWSPEC = 11027;

    //
    // MessageId: WSA_QOS_EPSFILTERSPEC
    //
    // MessageText:
    //
    // An invalid FILTERSPEC was found in the QOS provider-specific buffer.
    //
    int WSA_QOS_EPSFILTERSPEC = 11028;

    //
    // MessageId: WSA_QOS_ESDMODEOBJ
    //
    // MessageText:
    //
    // An invalid shape discard mode object was found in the QOS provider
    // specific buffer.
    //
    int WSA_QOS_ESDMODEOBJ = 11029;

    //
    // MessageId: WSA_QOS_ESHAPERATEOBJ
    //
    // MessageText:
    //
    // An invalid shaping rate object was found in the QOS provider-specific
    // buffer.
    //
    int WSA_QOS_ESHAPERATEOBJ = 11030;

    //
    // MessageId: WSA_QOS_RESERVED_PETYPE
    //
    // MessageText:
    //
    // A reserved policy element was found in the QOS provider-specific buffer.
    //
    int WSA_QOS_RESERVED_PETYPE = 11031;

    // /////////////////////////////////////////////////
    // =End of WinSock Error Codes=//
    // =//
    // =10000 to=11999=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =Available=//
    // =//
    // =12000 to=129999=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =Start of IPSec Error codes=//
    // =//
    // =13000 to=13999=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_IPSEC_QM_POLICY_EXISTS
    //
    // MessageText:
    //
    // The specified quick mode policy already exists.
    //
    int ERROR_IPSEC_QM_POLICY_EXISTS = 13000;

    //
    // MessageId: ERROR_IPSEC_QM_POLICY_NOT_FOUND
    //
    // MessageText:
    //
    // The specified quick mode policy was not found.
    //
    int ERROR_IPSEC_QM_POLICY_NOT_FOUND = 13001;

    //
    // MessageId: ERROR_IPSEC_QM_POLICY_IN_USE
    //
    // MessageText:
    //
    // The specified quick mode policy is being used.
    //
    int ERROR_IPSEC_QM_POLICY_IN_USE = 13002;

    //
    // MessageId: ERROR_IPSEC_MM_POLICY_EXISTS
    //
    // MessageText:
    //
    // The specified main mode policy already exists.
    //
    int ERROR_IPSEC_MM_POLICY_EXISTS = 13003;

    //
    // MessageId: ERROR_IPSEC_MM_POLICY_NOT_FOUND
    //
    // MessageText:
    //
    // The specified main mode policy was not found
    //
    int ERROR_IPSEC_MM_POLICY_NOT_FOUND = 13004;

    //
    // MessageId: ERROR_IPSEC_MM_POLICY_IN_USE
    //
    // MessageText:
    //
    // The specified main mode policy is being used.
    //
    int ERROR_IPSEC_MM_POLICY_IN_USE = 13005;

    //
    // MessageId: ERROR_IPSEC_MM_FILTER_EXISTS
    //
    // MessageText:
    //
    // The specified main mode filter already exists.
    //
    int ERROR_IPSEC_MM_FILTER_EXISTS = 13006;

    //
    // MessageId: ERROR_IPSEC_MM_FILTER_NOT_FOUND
    //
    // MessageText:
    //
    // The specified main mode filter was not found.
    //
    int ERROR_IPSEC_MM_FILTER_NOT_FOUND = 13007;

    //
    // MessageId: ERROR_IPSEC_TRANSPORT_FILTER_EXISTS
    //
    // MessageText:
    //
    // The specified transport mode filter already exists.
    //
    int ERROR_IPSEC_TRANSPORT_FILTER_EXISTS = 13008;

    //
    // MessageId: ERROR_IPSEC_TRANSPORT_FILTER_NOT_FOUND
    //
    // MessageText:
    //
    // The specified transport mode filter does not exist.
    //
    int ERROR_IPSEC_TRANSPORT_FILTER_NOT_FOUND = 13009;

    //
    // MessageId: ERROR_IPSEC_MM_AUTH_EXISTS
    //
    // MessageText:
    //
    // The specified main mode authentication list exists.
    //
    int ERROR_IPSEC_MM_AUTH_EXISTS = 13010;

    //
    // MessageId: ERROR_IPSEC_MM_AUTH_NOT_FOUND
    //
    // MessageText:
    //
    // The specified main mode authentication list was not found.
    //
    int ERROR_IPSEC_MM_AUTH_NOT_FOUND = 13011;

    //
    // MessageId: ERROR_IPSEC_MM_AUTH_IN_USE
    //
    // MessageText:
    //
    // The specified main mode authentication list is being used.
    //
    int ERROR_IPSEC_MM_AUTH_IN_USE = 13012;

    //
    // MessageId: ERROR_IPSEC_DEFAULT_MM_POLICY_NOT_FOUND
    //
    // MessageText:
    //
    // The specified default main mode policy was not found.
    //
    int ERROR_IPSEC_DEFAULT_MM_POLICY_NOT_FOUND = 13013;

    //
    // MessageId: ERROR_IPSEC_DEFAULT_MM_AUTH_NOT_FOUND
    //
    // MessageText:
    //
    // The specified default main mode authentication list was not found.
    //
    int ERROR_IPSEC_DEFAULT_MM_AUTH_NOT_FOUND = 13014;

    //
    // MessageId: ERROR_IPSEC_DEFAULT_QM_POLICY_NOT_FOUND
    //
    // MessageText:
    //
    // The specified default quick mode policy was not found.
    //
    int ERROR_IPSEC_DEFAULT_QM_POLICY_NOT_FOUND = 13015;

    //
    // MessageId: ERROR_IPSEC_TUNNEL_FILTER_EXISTS
    //
    // MessageText:
    //
    // The specified tunnel mode filter exists.
    //
    int ERROR_IPSEC_TUNNEL_FILTER_EXISTS = 13016;

    //
    // MessageId: ERROR_IPSEC_TUNNEL_FILTER_NOT_FOUND
    //
    // MessageText:
    //
    // The specified tunnel mode filter was not found.
    //
    int ERROR_IPSEC_TUNNEL_FILTER_NOT_FOUND = 13017;

    //
    // MessageId: ERROR_IPSEC_MM_FILTER_PENDING_DELETION
    //
    // MessageText:
    //
    // The Main Mode filter is pending deletion.
    //
    int ERROR_IPSEC_MM_FILTER_PENDING_DELETION = 13018;

    //
    // MessageId: ERROR_IPSEC_TRANSPORT_FILTER_PENDING_DELETION
    //
    // MessageText:
    //
    // The transport filter is pending deletion.
    //
    int ERROR_IPSEC_TRANSPORT_FILTER_PENDING_DELETION = 13019;

    //
    // MessageId: ERROR_IPSEC_TUNNEL_FILTER_PENDING_DELETION
    //
    // MessageText:
    //
    // The tunnel filter is pending deletion.
    //
    int ERROR_IPSEC_TUNNEL_FILTER_PENDING_DELETION = 13020;

    //
    // MessageId: ERROR_IPSEC_MM_POLICY_PENDING_DELETION
    //
    // MessageText:
    //
    // The Main Mode policy is pending deletion.
    //
    int ERROR_IPSEC_MM_POLICY_PENDING_DELETION = 13021;

    //
    // MessageId: ERROR_IPSEC_MM_AUTH_PENDING_DELETION
    //
    // MessageText:
    //
    // The Main Mode authentication bundle is pending deletion.
    //
    int ERROR_IPSEC_MM_AUTH_PENDING_DELETION = 13022;

    //
    // MessageId: ERROR_IPSEC_QM_POLICY_PENDING_DELETION
    //
    // MessageText:
    //
    // The Quick Mode policy is pending deletion.
    //
    int ERROR_IPSEC_QM_POLICY_PENDING_DELETION = 13023;

    //
    // MessageId: WARNING_IPSEC_MM_POLICY_PRUNED
    //
    // MessageText:
    //
    // The Main Mode policy was successfully added, but some of the requested
    // offers are not supported.
    //
    int WARNING_IPSEC_MM_POLICY_PRUNED = 13024;

    //
    // MessageId: WARNING_IPSEC_QM_POLICY_PRUNED
    //
    // MessageText:
    //
    // The Quick Mode policy was successfully added, but some of the requested
    // offers are not supported.
    //
    int WARNING_IPSEC_QM_POLICY_PRUNED = 13025;

    //
    // MessageId: ERROR_IPSEC_IKE_NEG_STATUS_BEGIN
    //
    // MessageText:
    //
    // ERROR_IPSEC_IKE_NEG_STATUS_BEGIN
    //
    int ERROR_IPSEC_IKE_NEG_STATUS_BEGIN = 13800;

    //
    // MessageId: ERROR_IPSEC_IKE_AUTH_FAIL
    //
    // MessageText:
    //
    // IKE authentication credentials are unacceptable
    //
    int ERROR_IPSEC_IKE_AUTH_FAIL = 13801;

    //
    // MessageId: ERROR_IPSEC_IKE_ATTRIB_FAIL
    //
    // MessageText:
    //
    // IKE security attributes are unacceptable
    //
    int ERROR_IPSEC_IKE_ATTRIB_FAIL = 13802;

    //
    // MessageId: ERROR_IPSEC_IKE_NEGOTIATION_PENDING
    //
    // MessageText:
    //
    // IKE Negotiation in progress
    //
    int ERROR_IPSEC_IKE_NEGOTIATION_PENDING = 13803;

    //
    // MessageId: ERROR_IPSEC_IKE_GENERAL_PROCESSING_ERROR
    //
    // MessageText:
    //
    // General processing error
    //
    int ERROR_IPSEC_IKE_GENERAL_PROCESSING_ERROR = 13804;

    //
    // MessageId: ERROR_IPSEC_IKE_TIMED_OUT
    //
    // MessageText:
    //
    // Negotiation timed out
    //
    int ERROR_IPSEC_IKE_TIMED_OUT = 13805;

    //
    // MessageId: ERROR_IPSEC_IKE_NO_CERT
    //
    // MessageText:
    //
    // IKE failed to find valid machine certificate. Contact your Network
    // Security Administrator about installing a valid certificate in the
    // appropriate Certificate Store.
    //
    int ERROR_IPSEC_IKE_NO_CERT = 13806;

    //
    // MessageId: ERROR_IPSEC_IKE_SA_DELETED
    //
    // MessageText:
    //
    // IKE SA deleted by peer before establishment completed
    //
    int ERROR_IPSEC_IKE_SA_DELETED = 13807;

    //
    // MessageId: ERROR_IPSEC_IKE_SA_REAPED
    //
    // MessageText:
    //
    // IKE SA deleted before establishment completed
    //
    int ERROR_IPSEC_IKE_SA_REAPED = 13808;

    //
    // MessageId: ERROR_IPSEC_IKE_MM_ACQUIRE_DROP
    //
    // MessageText:
    //
    // Negotiation request sat in Queue too long
    //
    int ERROR_IPSEC_IKE_MM_ACQUIRE_DROP = 13809;

    //
    // MessageId: ERROR_IPSEC_IKE_QM_ACQUIRE_DROP
    //
    // MessageText:
    //
    // Negotiation request sat in Queue too long
    //
    int ERROR_IPSEC_IKE_QM_ACQUIRE_DROP = 13810;

    //
    // MessageId: ERROR_IPSEC_IKE_QUEUE_DROP_MM
    //
    // MessageText:
    //
    // Negotiation request sat in Queue too long
    //
    int ERROR_IPSEC_IKE_QUEUE_DROP_MM = 13811;

    //
    // MessageId: ERROR_IPSEC_IKE_QUEUE_DROP_NO_MM
    //
    // MessageText:
    //
    // Negotiation request sat in Queue too long
    //
    int ERROR_IPSEC_IKE_QUEUE_DROP_NO_MM = 13812;

    //
    // MessageId: ERROR_IPSEC_IKE_DROP_NO_RESPONSE
    //
    // MessageText:
    //
    // No response from peer
    //
    int ERROR_IPSEC_IKE_DROP_NO_RESPONSE = 13813;

    //
    // MessageId: ERROR_IPSEC_IKE_MM_DELAY_DROP
    //
    // MessageText:
    //
    // Negotiation took too long
    //
    int ERROR_IPSEC_IKE_MM_DELAY_DROP = 13814;

    //
    // MessageId: ERROR_IPSEC_IKE_QM_DELAY_DROP
    //
    // MessageText:
    //
    // Negotiation took too long
    //
    int ERROR_IPSEC_IKE_QM_DELAY_DROP = 13815;

    //
    // MessageId: ERROR_IPSEC_IKE_ERROR
    //
    // MessageText:
    //
    // Unknown error occurred
    //
    int ERROR_IPSEC_IKE_ERROR = 13816;

    //
    // MessageId: ERROR_IPSEC_IKE_CRL_FAILED
    //
    // MessageText:
    //
    // Certificate Revocation Check failed
    //
    int ERROR_IPSEC_IKE_CRL_FAILED = 13817;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_KEY_USAGE
    //
    // MessageText:
    //
    // Invalid certificate key usage
    //
    int ERROR_IPSEC_IKE_INVALID_KEY_USAGE = 13818;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_CERT_TYPE
    //
    // MessageText:
    //
    // Invalid certificate type
    //
    int ERROR_IPSEC_IKE_INVALID_CERT_TYPE = 13819;

    //
    // MessageId: ERROR_IPSEC_IKE_NO_PRIVATE_KEY
    //
    // MessageText:
    //
    // IKE negotiation failed because the machine certificate used does not have
    // a private key. IPsec certificates require a private key. Contact your
    // Network Security administrator about replacing with a certificate that
    // has a private key.
    //
    int ERROR_IPSEC_IKE_NO_PRIVATE_KEY = 13820;

    //
    // MessageId: ERROR_IPSEC_IKE_DH_FAIL
    //
    // MessageText:
    //
    // Failure in Diffie-Helman computation
    //
    int ERROR_IPSEC_IKE_DH_FAIL = 13822;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_HEADER
    //
    // MessageText:
    //
    // Invalid header
    //
    int ERROR_IPSEC_IKE_INVALID_HEADER = 13824;

    //
    // MessageId: ERROR_IPSEC_IKE_NO_POLICY
    //
    // MessageText:
    //
    // No policy configured
    //
    int ERROR_IPSEC_IKE_NO_POLICY = 13825;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_SIGNATURE
    //
    // MessageText:
    //
    // Failed to verify signature
    //
    int ERROR_IPSEC_IKE_INVALID_SIGNATURE = 13826;

    //
    // MessageId: ERROR_IPSEC_IKE_KERBEROS_ERROR
    //
    // MessageText:
    //
    // Failed to authenticate using kerberos
    //
    int ERROR_IPSEC_IKE_KERBEROS_ERROR = 13827;

    //
    // MessageId: ERROR_IPSEC_IKE_NO_PUBLIC_KEY
    //
    // MessageText:
    //
    // Peer's certificate did not have a public key
    //
    int ERROR_IPSEC_IKE_NO_PUBLIC_KEY = 13828;

    // These must stay as a unit.
    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR
    //
    // MessageText:
    //
    // Error processing error payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR = 13829;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_SA
    //
    // MessageText:
    //
    // Error processing SA payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_SA = 13830;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_PROP
    //
    // MessageText:
    //
    // Error processing Proposal payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_PROP = 13831;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_TRANS
    //
    // MessageText:
    //
    // Error processing Transform payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_TRANS = 13832;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_KE
    //
    // MessageText:
    //
    // Error processing KE payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_KE = 13833;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_ID
    //
    // MessageText:
    //
    // Error processing ID payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_ID = 13834;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_CERT
    //
    // MessageText:
    //
    // Error processing Cert payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_CERT = 13835;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_CERT_REQ
    //
    // MessageText:
    //
    // Error processing Certificate Request payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_CERT_REQ = 13836;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_HASH
    //
    // MessageText:
    //
    // Error processing Hash payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_HASH = 13837;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_SIG
    //
    // MessageText:
    //
    // Error processing Signature payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_SIG = 13838;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_NONCE
    //
    // MessageText:
    //
    // Error processing Nonce payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_NONCE = 13839;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_NOTIFY
    //
    // MessageText:
    //
    // Error processing Notify payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_NOTIFY = 13840;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_DELETE
    //
    // MessageText:
    //
    // Error processing Delete Payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_DELETE = 13841;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_VENDOR
    //
    // MessageText:
    //
    // Error processing VendorId payload
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_VENDOR = 13842;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_PAYLOAD
    //
    // MessageText:
    //
    // Invalid payload received
    //
    int ERROR_IPSEC_IKE_INVALID_PAYLOAD = 13843;

    //
    // MessageId: ERROR_IPSEC_IKE_LOAD_SOFT_SA
    //
    // MessageText:
    //
    // Soft SA loaded
    //
    int ERROR_IPSEC_IKE_LOAD_SOFT_SA = 13844;

    //
    // MessageId: ERROR_IPSEC_IKE_SOFT_SA_TORN_DOWN
    //
    // MessageText:
    //
    // Soft SA torn down
    //
    int ERROR_IPSEC_IKE_SOFT_SA_TORN_DOWN = 13845;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_COOKIE
    //
    // MessageText:
    //
    // Invalid cookie received.
    //
    int ERROR_IPSEC_IKE_INVALID_COOKIE = 13846;

    //
    // MessageId: ERROR_IPSEC_IKE_NO_PEER_CERT
    //
    // MessageText:
    //
    // Peer failed to send valid machine certificate
    //
    int ERROR_IPSEC_IKE_NO_PEER_CERT = 13847;

    //
    // MessageId: ERROR_IPSEC_IKE_PEER_CRL_FAILED
    //
    // MessageText:
    //
    // Certification Revocation check of peer's certificate failed
    //
    int ERROR_IPSEC_IKE_PEER_CRL_FAILED = 13848;

    //
    // MessageId: ERROR_IPSEC_IKE_POLICY_CHANGE
    //
    // MessageText:
    //
    // New policy invalidated SAs formed with old policy
    //
    int ERROR_IPSEC_IKE_POLICY_CHANGE = 13849;

    //
    // MessageId: ERROR_IPSEC_IKE_NO_MM_POLICY
    //
    // MessageText:
    //
    // There is no available Main Mode IKE policy.
    //
    int ERROR_IPSEC_IKE_NO_MM_POLICY = 13850;

    //
    // MessageId: ERROR_IPSEC_IKE_NOTCBPRIV
    //
    // MessageText:
    //
    // Failed to enabled TCB privilege.
    //
    int ERROR_IPSEC_IKE_NOTCBPRIV = 13851;

    //
    // MessageId: ERROR_IPSEC_IKE_SECLOADFAIL
    //
    // MessageText:
    //
    // Failed to load SECURITY.DLL.
    //
    int ERROR_IPSEC_IKE_SECLOADFAIL = 13852;

    //
    // MessageId: ERROR_IPSEC_IKE_FAILSSPINIT
    //
    // MessageText:
    //
    // Failed to obtain security function table dispatch address from SSPI.
    //
    int ERROR_IPSEC_IKE_FAILSSPINIT = 13853;

    //
    // MessageId: ERROR_IPSEC_IKE_FAILQUERYSSP
    //
    // MessageText:
    //
    // Failed to query Kerberos package to obtain max token size.
    //
    int ERROR_IPSEC_IKE_FAILQUERYSSP = 13854;

    //
    // MessageId: ERROR_IPSEC_IKE_SRVACQFAIL
    //
    // MessageText:
    //
    // Failed to obtain Kerberos server credentials for ISAKMP/ERROR_IPSEC_IKE
    // service. Kerberos authentication will not function. The most likely
    // reason for this is lack of domain membership. This is normal if your
    // computer is a member of a workgroup.
    //
    int ERROR_IPSEC_IKE_SRVACQFAIL = 13855;

    //
    // MessageId: ERROR_IPSEC_IKE_SRVQUERYCRED
    //
    // MessageText:
    //
    // Failed to determine SSPI principal name for ISAKMP/ERROR_IPSEC_IKE
    // service (QueryCredentialsAttributes).
    //
    int ERROR_IPSEC_IKE_SRVQUERYCRED = 13856;

    //
    // MessageId: ERROR_IPSEC_IKE_GETSPIFAIL
    //
    // MessageText:
    //
    // Failed to obtain new SPI for the inbound SA from Ipsec driver. The most
    // common cause for this is that the driver does not have the correct
    // filter. Check your policy to verify the filters.
    //
    int ERROR_IPSEC_IKE_GETSPIFAIL = 13857;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_FILTER
    //
    // MessageText:
    //
    // Given filter is invalid
    //
    int ERROR_IPSEC_IKE_INVALID_FILTER = 13858;

    //
    // MessageId: ERROR_IPSEC_IKE_OUT_OF_MEMORY
    //
    // MessageText:
    //
    // Memory allocation failed.
    //
    int ERROR_IPSEC_IKE_OUT_OF_MEMORY = 13859;

    //
    // MessageId: ERROR_IPSEC_IKE_ADD_UPDATE_KEY_FAILED
    //
    // MessageText:
    //
    // Failed to add Security Association to IPSec Driver. The most common cause
    // for this is if the IKE negotiation took too long to complete. If the
    // problem persists, reduce the load on the faulting machine.
    //
    int ERROR_IPSEC_IKE_ADD_UPDATE_KEY_FAILED = 13860;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_POLICY
    //
    // MessageText:
    //
    // Invalid policy
    //
    int ERROR_IPSEC_IKE_INVALID_POLICY = 13861;

    //
    // MessageId: ERROR_IPSEC_IKE_UNKNOWN_DOI
    //
    // MessageText:
    //
    // Invalid DOI
    //
    int ERROR_IPSEC_IKE_UNKNOWN_DOI = 13862;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_SITUATION
    //
    // MessageText:
    //
    // Invalid situation
    //
    int ERROR_IPSEC_IKE_INVALID_SITUATION = 13863;

    //
    // MessageId: ERROR_IPSEC_IKE_DH_FAILURE
    //
    // MessageText:
    //
    // Diffie-Hellman failure
    //
    int ERROR_IPSEC_IKE_DH_FAILURE = 13864;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_GROUP
    //
    // MessageText:
    //
    // Invalid Diffie-Hellman group
    //
    int ERROR_IPSEC_IKE_INVALID_GROUP = 13865;

    //
    // MessageId: ERROR_IPSEC_IKE_ENCRYPT
    //
    // MessageText:
    //
    // Error encrypting payload
    //
    int ERROR_IPSEC_IKE_ENCRYPT = 13866;

    //
    // MessageId: ERROR_IPSEC_IKE_DECRYPT
    //
    // MessageText:
    //
    // Error decrypting payload
    //
    int ERROR_IPSEC_IKE_DECRYPT = 13867;

    //
    // MessageId: ERROR_IPSEC_IKE_POLICY_MATCH
    //
    // MessageText:
    //
    // Policy match error
    //
    int ERROR_IPSEC_IKE_POLICY_MATCH = 13868;

    //
    // MessageId: ERROR_IPSEC_IKE_UNSUPPORTED_ID
    //
    // MessageText:
    //
    // Unsupported ID
    //
    int ERROR_IPSEC_IKE_UNSUPPORTED_ID = 13869;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_HASH
    //
    // MessageText:
    //
    // Hash verification failed
    //
    int ERROR_IPSEC_IKE_INVALID_HASH = 13870;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_HASH_ALG
    //
    // MessageText:
    //
    // Invalid hash algorithm
    //
    int ERROR_IPSEC_IKE_INVALID_HASH_ALG = 13871;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_HASH_SIZE
    //
    // MessageText:
    //
    // Invalid hash size
    //
    int ERROR_IPSEC_IKE_INVALID_HASH_SIZE = 13872;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_ENCRYPT_ALG
    //
    // MessageText:
    //
    // Invalid encryption algorithm
    //
    int ERROR_IPSEC_IKE_INVALID_ENCRYPT_ALG = 13873;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_AUTH_ALG
    //
    // MessageText:
    //
    // Invalid authentication algorithm
    //
    int ERROR_IPSEC_IKE_INVALID_AUTH_ALG = 13874;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_SIG
    //
    // MessageText:
    //
    // Invalid certificate signature
    //
    int ERROR_IPSEC_IKE_INVALID_SIG = 13875;

    //
    // MessageId: ERROR_IPSEC_IKE_LOAD_FAILED
    //
    // MessageText:
    //
    // Load failed
    //
    int ERROR_IPSEC_IKE_LOAD_FAILED = 13876;

    //
    // MessageId: ERROR_IPSEC_IKE_RPC_DELETE
    //
    // MessageText:
    //
    // Deleted via RPC call
    //
    int ERROR_IPSEC_IKE_RPC_DELETE = 13877;

    //
    // MessageId: ERROR_IPSEC_IKE_BENIGN_REINIT
    //
    // MessageText:
    //
    // Temporary state created to perform reinit. This is not a real failure.
    //
    int ERROR_IPSEC_IKE_BENIGN_REINIT = 13878;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_RESPONDER_LIFETIME_NOTIFY
    //
    // MessageText:
    //
    // The lifetime value received in the Responder Lifetime Notify is below the
    // Windows=2000 configured minimum value. Please fix the policy on the peer
    // machine.
    //
    int ERROR_IPSEC_IKE_INVALID_RESPONDER_LIFETIME_NOTIFY = 13879;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_CERT_KEYLEN
    //
    // MessageText:
    //
    // Key length in certificate is too small for configured security
    // requirements.
    //
    int ERROR_IPSEC_IKE_INVALID_CERT_KEYLEN = 13881;

    //
    // MessageId: ERROR_IPSEC_IKE_MM_LIMIT
    //
    // MessageText:
    //
    // Max number of established MM SAs to peer exceeded.
    //
    int ERROR_IPSEC_IKE_MM_LIMIT = 13882;

    //
    // MessageId: ERROR_IPSEC_IKE_NEGOTIATION_DISABLED
    //
    // MessageText:
    //
    // IKE received a policy that disables negotiation.
    //
    int ERROR_IPSEC_IKE_NEGOTIATION_DISABLED = 13883;

    //
    // MessageId: ERROR_IPSEC_IKE_QM_LIMIT
    //
    // MessageText:
    //
    // Reached maximum quick mode limit for the main mode. New main mode will be
    // started.
    //
    int ERROR_IPSEC_IKE_QM_LIMIT = 13884;

    //
    // MessageId: ERROR_IPSEC_IKE_MM_EXPIRED
    //
    // MessageText:
    //
    // Main mode SA lifetime expired or peer sent a main mode delete.
    //
    int ERROR_IPSEC_IKE_MM_EXPIRED = 13885;

    //
    // MessageId: ERROR_IPSEC_IKE_PEER_MM_ASSUMED_INVALID
    //
    // MessageText:
    //
    // Main mode SA assumed to be invalid because peer stopped responding.
    //
    int ERROR_IPSEC_IKE_PEER_MM_ASSUMED_INVALID = 13886;

    //
    // MessageId: ERROR_IPSEC_IKE_CERT_CHAIN_POLICY_MISMATCH
    //
    // MessageText:
    //
    // Certificate doesn't chain to a trusted root in IPsec policy.
    //
    int ERROR_IPSEC_IKE_CERT_CHAIN_POLICY_MISMATCH = 13887;

    //
    // MessageId: ERROR_IPSEC_IKE_UNEXPECTED_MESSAGE_ID
    //
    // MessageText:
    //
    // Received unexpected message ID.
    //
    int ERROR_IPSEC_IKE_UNEXPECTED_MESSAGE_ID = 13888;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_AUTH_PAYLOAD
    //
    // MessageText:
    //
    // Received invalid authentication offers.
    //
    int ERROR_IPSEC_IKE_INVALID_AUTH_PAYLOAD = 13889;

    //
    // MessageId: ERROR_IPSEC_IKE_DOS_COOKIE_SENT
    //
    // MessageText:
    //
    // Sent DOS cookie notify to intiator.
    //
    int ERROR_IPSEC_IKE_DOS_COOKIE_SENT = 13890;

    //
    // MessageId: ERROR_IPSEC_IKE_SHUTTING_DOWN
    //
    // MessageText:
    //
    // IKE service is shutting down.
    //
    int ERROR_IPSEC_IKE_SHUTTING_DOWN = 13891;

    //
    // MessageId: ERROR_IPSEC_IKE_CGA_AUTH_FAILED
    //
    // MessageText:
    //
    // Could not verify binding between CGA address and certificate.
    //
    int ERROR_IPSEC_IKE_CGA_AUTH_FAILED = 13892;

    //
    // MessageId: ERROR_IPSEC_IKE_PROCESS_ERR_NATOA
    //
    // MessageText:
    //
    // Error processing NatOA payload.
    //
    int ERROR_IPSEC_IKE_PROCESS_ERR_NATOA = 13893;

    //
    // MessageId: ERROR_IPSEC_IKE_INVALID_MM_FOR_QM
    //
    // MessageText:
    //
    // Parameters of the main mode are invalid for this quick mode.
    //
    int ERROR_IPSEC_IKE_INVALID_MM_FOR_QM = 13894;

    //
    // MessageId: ERROR_IPSEC_IKE_QM_EXPIRED
    //
    // MessageText:
    //
    // Quick mode SA was expired by IPsec driver.
    //
    int ERROR_IPSEC_IKE_QM_EXPIRED = 13895;

    //
    // MessageId: ERROR_IPSEC_IKE_TOO_MANY_FILTERS
    //
    // MessageText:
    //
    // Too many dynamically added IKEEXT filters were detected.
    //
    int ERROR_IPSEC_IKE_TOO_MANY_FILTERS = 13896;

    //
    // MessageId: ERROR_IPSEC_IKE_NEG_STATUS_END
    //
    // MessageText:
    //
    // ERROR_IPSEC_IKE_NEG_STATUS_END
    //
    int ERROR_IPSEC_IKE_NEG_STATUS_END = 13897;

    //
    // Following error codes are returned by IPsec kernel.
    //
    //
    // MessageId: ERROR_IPSEC_BAD_SPI
    //
    // MessageText:
    //
    // The SPI in the packet does not match a valid IPsec SA.
    //
    int ERROR_IPSEC_BAD_SPI = 13910;

    //
    // MessageId: ERROR_IPSEC_SA_LIFETIME_EXPIRED
    //
    // MessageText:
    //
    // Packet was received on an IPsec SA whose lifetime has expired.
    //
    int ERROR_IPSEC_SA_LIFETIME_EXPIRED = 13911;

    //
    // MessageId: ERROR_IPSEC_WRONG_SA
    //
    // MessageText:
    //
    // Packet was received on an IPsec SA that doesn't match the packet
    // characteristics.
    //
    int ERROR_IPSEC_WRONG_SA = 13912;

    //
    // MessageId: ERROR_IPSEC_REPLAY_CHECK_FAILED
    //
    // MessageText:
    //
    // Packet sequence number replay check failed.
    //
    int ERROR_IPSEC_REPLAY_CHECK_FAILED = 13913;

    //
    // MessageId: ERROR_IPSEC_INVALID_PACKET
    //
    // MessageText:
    //
    // IPsec header and/or trailer in the packet is invalid.
    //
    int ERROR_IPSEC_INVALID_PACKET = 13914;

    //
    // MessageId: ERROR_IPSEC_INTEGRITY_CHECK_FAILED
    //
    // MessageText:
    //
    // IPsec integrity check failed.
    //
    int ERROR_IPSEC_INTEGRITY_CHECK_FAILED = 13915;

    //
    // MessageId: ERROR_IPSEC_CLEAR_TEXT_DROP
    //
    // MessageText:
    //
    // IPsec dropped a clear text packet.
    //
    int ERROR_IPSEC_CLEAR_TEXT_DROP = 13916;

    // /////////////////////////////////////////////////
    // =//
    // =End of IPSec Error codes=//
    // =//
    // =13000 to=13999=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =Start of Side By Side Error Codes //
    // =//
    // =14000 to=14999=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_SXS_SECTION_NOT_FOUND
    //
    // MessageText:
    //
    // The requested section was not present in the activation context.
    //
    int ERROR_SXS_SECTION_NOT_FOUND = 14000;

    //
    // MessageId: ERROR_SXS_CANT_GEN_ACTCTX
    //
    // MessageText:
    //
    // The application has failed to start because its side-by-side
    // configuration is incorrect. Please see the application event log for more
    // detail.
    //
    int ERROR_SXS_CANT_GEN_ACTCTX = 14001;

    //
    // MessageId: ERROR_SXS_INVALID_ACTCTXDATA_FORMAT
    //
    // MessageText:
    //
    // The application binding data format is invalid.
    //
    int ERROR_SXS_INVALID_ACTCTXDATA_FORMAT = 14002;

    //
    // MessageId: ERROR_SXS_ASSEMBLY_NOT_FOUND
    //
    // MessageText:
    //
    // The referenced assembly is not installed on your system.
    //
    int ERROR_SXS_ASSEMBLY_NOT_FOUND = 14003;

    //
    // MessageId: ERROR_SXS_MANIFEST_FORMAT_ERROR
    //
    // MessageText:
    //
    // The manifest file does not begin with the required tag and format
    // information.
    //
    int ERROR_SXS_MANIFEST_FORMAT_ERROR = 14004;

    //
    // MessageId: ERROR_SXS_MANIFEST_PARSE_ERROR
    //
    // MessageText:
    //
    // The manifest file contains one or more syntax errors.
    //
    int ERROR_SXS_MANIFEST_PARSE_ERROR = 14005;

    //
    // MessageId: ERROR_SXS_ACTIVATION_CONTEXT_DISABLED
    //
    // MessageText:
    //
    // The application attempted to activate a disabled activation context.
    //
    int ERROR_SXS_ACTIVATION_CONTEXT_DISABLED = 14006;

    //
    // MessageId: ERROR_SXS_KEY_NOT_FOUND
    //
    // MessageText:
    //
    // The requested lookup key was not found in any active activation context.
    //
    int ERROR_SXS_KEY_NOT_FOUND = 14007;

    //
    // MessageId: ERROR_SXS_VERSION_CONFLICT
    //
    // MessageText:
    //
    // A component version required by the application conflicts with another
    // component version already active.
    //
    int ERROR_SXS_VERSION_CONFLICT = 14008;

    //
    // MessageId: ERROR_SXS_WRONG_SECTION_TYPE
    //
    // MessageText:
    //
    // The type requested activation context section does not match the query
    // API used.
    //
    int ERROR_SXS_WRONG_SECTION_TYPE = 14009;

    //
    // MessageId: ERROR_SXS_THREAD_QUERIES_DISABLED
    //
    // MessageText:
    //
    // Lack of system resources has required isolated activation to be disabled
    // for the current thread of execution.
    //
    int ERROR_SXS_THREAD_QUERIES_DISABLED = 14010;

    //
    // MessageId: ERROR_SXS_PROCESS_DEFAULT_ALREADY_SET
    //
    // MessageText:
    //
    // An attempt to set the process default activation context failed because
    // the process default activation context was already set.
    //
    int ERROR_SXS_PROCESS_DEFAULT_ALREADY_SET = 14011;

    //
    // MessageId: ERROR_SXS_UNKNOWN_ENCODING_GROUP
    //
    // MessageText:
    //
    // The encoding group identifier specified is not recognized.
    //
    int ERROR_SXS_UNKNOWN_ENCODING_GROUP = 14012;

    //
    // MessageId: ERROR_SXS_UNKNOWN_ENCODING
    //
    // MessageText:
    //
    // The encoding requested is not recognized.
    //
    int ERROR_SXS_UNKNOWN_ENCODING = 14013;

    //
    // MessageId: ERROR_SXS_INVALID_XML_NAMESPACE_URI
    //
    // MessageText:
    //
    // The manifest contains a reference to an invalid URI.
    //
    int ERROR_SXS_INVALID_XML_NAMESPACE_URI = 14014;

    //
    // MessageId: ERROR_SXS_ROOT_MANIFEST_DEPENDENCY_NOT_INSTALLED
    //
    // MessageText:
    //
    // The application manifest contains a reference to a dependent assembly
    // which is not installed
    //
    int ERROR_SXS_ROOT_MANIFEST_DEPENDENCY_NOT_INSTALLED = 14015;

    //
    // MessageId: ERROR_SXS_LEAF_MANIFEST_DEPENDENCY_NOT_INSTALLED
    //
    // MessageText:
    //
    // The manifest for an assembly used by the application has a reference to a
    // dependent assembly which is not installed
    //
    int ERROR_SXS_LEAF_MANIFEST_DEPENDENCY_NOT_INSTALLED = 14016;

    //
    // MessageId: ERROR_SXS_INVALID_ASSEMBLY_IDENTITY_ATTRIBUTE
    //
    // MessageText:
    //
    // The manifest contains an attribute for the assembly identity which is not
    // valid.
    //
    int ERROR_SXS_INVALID_ASSEMBLY_IDENTITY_ATTRIBUTE = 14017;

    //
    // MessageId: ERROR_SXS_MANIFEST_MISSING_REQUIRED_DEFAULT_NAMESPACE
    //
    // MessageText:
    //
    // The manifest is missing the required default namespace specification on
    // the assembly element.
    //
    int ERROR_SXS_MANIFEST_MISSING_REQUIRED_DEFAULT_NAMESPACE = 14018;

    //
    // MessageId: ERROR_SXS_MANIFEST_INVALID_REQUIRED_DEFAULT_NAMESPACE
    //
    // MessageText:
    //
    // The manifest has a default namespace specified on the assembly element
    // but its value is not "urn:schemas-microsoft-com:asm.v1".
    //
    int ERROR_SXS_MANIFEST_INVALID_REQUIRED_DEFAULT_NAMESPACE = 14019;

    //
    // MessageId: ERROR_SXS_PRIVATE_MANIFEST_CROSS_PATH_WITH_REPARSE_POINT
    //
    // MessageText:
    //
    // The private manifest probed has crossed reparse-point-associated path
    //
    int ERROR_SXS_PRIVATE_MANIFEST_CROSS_PATH_WITH_REPARSE_POINT = 14020;

    //
    // MessageId: ERROR_SXS_DUPLICATE_DLL_NAME
    //
    // MessageText:
    //
    // Two or more components referenced directly or indirectly by the
    // application manifest have files by the same name.
    //
    int ERROR_SXS_DUPLICATE_DLL_NAME = 14021;

    //
    // MessageId: ERROR_SXS_DUPLICATE_WINDOWCLASS_NAME
    //
    // MessageText:
    //
    // Two or more components referenced directly or indirectly by the
    // application manifest have window classes with the same name.
    //
    int ERROR_SXS_DUPLICATE_WINDOWCLASS_NAME = 14022;

    //
    // MessageId: ERROR_SXS_DUPLICATE_CLSID
    //
    // MessageText:
    //
    // Two or more components referenced directly or indirectly by the
    // application manifest have the same COM server CLSIDs.
    //
    int ERROR_SXS_DUPLICATE_CLSID = 14023;

    //
    // MessageId: ERROR_SXS_DUPLICATE_IID
    //
    // MessageText:
    //
    // Two or more components referenced directly or indirectly by the
    // application manifest have proxies for the same COM interface IIDs.
    //
    int ERROR_SXS_DUPLICATE_IID = 14024;

    //
    // MessageId: ERROR_SXS_DUPLICATE_TLBID
    //
    // MessageText:
    //
    // Two or more components referenced directly or indirectly by the
    // application manifest have the same COM type library TLBIDs.
    //
    int ERROR_SXS_DUPLICATE_TLBID = 14025;

    //
    // MessageId: ERROR_SXS_DUPLICATE_PROGID
    //
    // MessageText:
    //
    // Two or more components referenced directly or indirectly by the
    // application manifest have the same COM ProgIDs.
    //
    int ERROR_SXS_DUPLICATE_PROGID = 14026;

    //
    // MessageId: ERROR_SXS_DUPLICATE_ASSEMBLY_NAME
    //
    // MessageText:
    //
    // Two or more components referenced directly or indirectly by the
    // application manifest are different versions of the same component which
    // is not permitted.
    //
    int ERROR_SXS_DUPLICATE_ASSEMBLY_NAME = 14027;

    //
    // MessageId: ERROR_SXS_FILE_HASH_MISMATCH
    //
    // MessageText:
    //
    // A component's file does not match the verification information present in
    // the component manifest.
    //
    int ERROR_SXS_FILE_HASH_MISMATCH = 14028;

    //
    // MessageId: ERROR_SXS_POLICY_PARSE_ERROR
    //
    // MessageText:
    //
    // The policy manifest contains one or more syntax errors.
    //
    int ERROR_SXS_POLICY_PARSE_ERROR = 14029;

    //
    // MessageId: ERROR_SXS_XML_E_MISSINGQUOTE
    //
    // MessageText:
    //
    // Manifest Parse Error : A string literal was expected, but no opening
    // quote character was found.
    //
    int ERROR_SXS_XML_E_MISSINGQUOTE = 14030;

    //
    // MessageId: ERROR_SXS_XML_E_COMMENTSYNTAX
    //
    // MessageText:
    //
    // Manifest Parse Error : Incorrect syntax was used in a comment.
    //
    int ERROR_SXS_XML_E_COMMENTSYNTAX = 14031;

    //
    // MessageId: ERROR_SXS_XML_E_BADSTARTNAMECHAR
    //
    // MessageText:
    //
    // Manifest Parse Error : A name was started with an invalid character.
    //
    int ERROR_SXS_XML_E_BADSTARTNAMECHAR = 14032;

    //
    // MessageId: ERROR_SXS_XML_E_BADNAMECHAR
    //
    // MessageText:
    //
    // Manifest Parse Error : A name contained an invalid character.
    //
    int ERROR_SXS_XML_E_BADNAMECHAR = 14033;

    //
    // MessageId: ERROR_SXS_XML_E_BADCHARINSTRING
    //
    // MessageText:
    //
    // Manifest Parse Error : A string literal contained an invalid character.
    //
    int ERROR_SXS_XML_E_BADCHARINSTRING = 14034;

    //
    // MessageId: ERROR_SXS_XML_E_XMLDECLSYNTAX
    //
    // MessageText:
    //
    // Manifest Parse Error : Invalid syntax for an xml declaration.
    //
    int ERROR_SXS_XML_E_XMLDECLSYNTAX = 14035;

    //
    // MessageId: ERROR_SXS_XML_E_BADCHARDATA
    //
    // MessageText:
    //
    // Manifest Parse Error : An Invalid character was found in text content.
    //
    int ERROR_SXS_XML_E_BADCHARDATA = 14036;

    //
    // MessageId: ERROR_SXS_XML_E_MISSINGWHITESPACE
    //
    // MessageText:
    //
    // Manifest Parse Error : Required white space was missing.
    //
    int ERROR_SXS_XML_E_MISSINGWHITESPACE = 14037;

    //
    // MessageId: ERROR_SXS_XML_E_EXPECTINGTAGEND
    //
    // MessageText:
    //
    // Manifest Parse Error : The character '>' was expected.
    //
    int ERROR_SXS_XML_E_EXPECTINGTAGEND = 14038;

    //
    // MessageId: ERROR_SXS_XML_E_MISSINGSEMICOLON
    //
    // MessageText:
    //
    // Manifest Parse Error : A semi colon character was expected.
    //
    int ERROR_SXS_XML_E_MISSINGSEMICOLON = 14039;

    //
    // MessageId: ERROR_SXS_XML_E_UNBALANCEDPAREN
    //
    // MessageText:
    //
    // Manifest Parse Error : Unbalanced parentheses.
    //
    int ERROR_SXS_XML_E_UNBALANCEDPAREN = 14040;

    //
    // MessageId: ERROR_SXS_XML_E_INTERNALERROR
    //
    // MessageText:
    //
    // Manifest Parse Error : Internal error.
    //
    int ERROR_SXS_XML_E_INTERNALERROR = 14041;

    //
    // MessageId: ERROR_SXS_XML_E_UNEXPECTED_WHITESPACE
    //
    // MessageText:
    //
    // Manifest Parse Error : Whitespace is not allowed at this location.
    //
    int ERROR_SXS_XML_E_UNEXPECTED_WHITESPACE = 14042;

    //
    // MessageId: ERROR_SXS_XML_E_INCOMPLETE_ENCODING
    //
    // MessageText:
    //
    // Manifest Parse Error : End of file reached in invalid state for current
    // encoding.
    //
    int ERROR_SXS_XML_E_INCOMPLETE_ENCODING = 14043;

    //
    // MessageId: ERROR_SXS_XML_E_MISSING_PAREN
    //
    // MessageText:
    //
    // Manifest Parse Error : Missing parenthesis.
    //
    int ERROR_SXS_XML_E_MISSING_PAREN = 14044;

    //
    // MessageId: ERROR_SXS_XML_E_EXPECTINGCLOSEQUOTE
    //
    // MessageText:
    //
    // Manifest Parse Error : A single or double closing quote character (\' or
    // \") is missing.
    //
    int ERROR_SXS_XML_E_EXPECTINGCLOSEQUOTE = 14045;

    //
    // MessageId: ERROR_SXS_XML_E_MULTIPLE_COLONS
    //
    // MessageText:
    //
    // Manifest Parse Error : Multiple colons are not allowed in a name.
    //
    int ERROR_SXS_XML_E_MULTIPLE_COLONS = 14046;

    //
    // MessageId: ERROR_SXS_XML_E_INVALID_DECIMAL
    //
    // MessageText:
    //
    // Manifest Parse Error : Invalid character for decimal digit.
    //
    int ERROR_SXS_XML_E_INVALID_DECIMAL = 14047;

    //
    // MessageId: ERROR_SXS_XML_E_INVALID_HEXIDECIMAL
    //
    // MessageText:
    //
    // Manifest Parse Error : Invalid character for hexidecimal digit.
    //
    int ERROR_SXS_XML_E_INVALID_HEXIDECIMAL = 14048;

    //
    // MessageId: ERROR_SXS_XML_E_INVALID_UNICODE
    //
    // MessageText:
    //
    // Manifest Parse Error : Invalid unicode character value for this platform.
    //
    int ERROR_SXS_XML_E_INVALID_UNICODE = 14049;

    //
    // MessageId: ERROR_SXS_XML_E_WHITESPACEORQUESTIONMARK
    //
    // MessageText:
    //
    // Manifest Parse Error : Expecting whitespace or '?'.
    //
    int ERROR_SXS_XML_E_WHITESPACEORQUESTIONMARK = 14050;

    //
    // MessageId: ERROR_SXS_XML_E_UNEXPECTEDENDTAG
    //
    // MessageText:
    //
    // Manifest Parse Error : End tag was not expected at this location.
    //
    int ERROR_SXS_XML_E_UNEXPECTEDENDTAG = 14051;

    //
    // MessageId: ERROR_SXS_XML_E_UNCLOSEDTAG
    //
    // MessageText:
    //
    // Manifest Parse Error : The following tags were not closed: %1.
    //
    int ERROR_SXS_XML_E_UNCLOSEDTAG = 14052;

    //
    // MessageId: ERROR_SXS_XML_E_DUPLICATEATTRIBUTE
    //
    // MessageText:
    //
    // Manifest Parse Error : Duplicate attribute.
    //
    int ERROR_SXS_XML_E_DUPLICATEATTRIBUTE = 14053;

    //
    // MessageId: ERROR_SXS_XML_E_MULTIPLEROOTS
    //
    // MessageText:
    //
    // Manifest Parse Error : Only one top level element is allowed in an XML
    // document.
    //
    int ERROR_SXS_XML_E_MULTIPLEROOTS = 14054;

    //
    // MessageId: ERROR_SXS_XML_E_INVALIDATROOTLEVEL
    //
    // MessageText:
    //
    // Manifest Parse Error : Invalid at the top level of the document.
    //
    int ERROR_SXS_XML_E_INVALIDATROOTLEVEL = 14055;

    //
    // MessageId: ERROR_SXS_XML_E_BADXMLDECL
    //
    // MessageText:
    //
    // Manifest Parse Error : Invalid xml declaration.
    //
    int ERROR_SXS_XML_E_BADXMLDECL = 14056;

    //
    // MessageId: ERROR_SXS_XML_E_MISSINGROOT
    //
    // MessageText:
    //
    // Manifest Parse Error : XML document must have a top level element.
    //
    int ERROR_SXS_XML_E_MISSINGROOT = 14057;

    //
    // MessageId: ERROR_SXS_XML_E_UNEXPECTEDEOF
    //
    // MessageText:
    //
    // Manifest Parse Error : Unexpected end of file.
    //
    int ERROR_SXS_XML_E_UNEXPECTEDEOF = 14058;

    //
    // MessageId: ERROR_SXS_XML_E_BADPEREFINSUBSET
    //
    // MessageText:
    //
    // Manifest Parse Error : Parameter entities cannot be used inside markup
    // declarations in an internal subset.
    //
    int ERROR_SXS_XML_E_BADPEREFINSUBSET = 14059;

    //
    // MessageId: ERROR_SXS_XML_E_UNCLOSEDSTARTTAG
    //
    // MessageText:
    //
    // Manifest Parse Error : Element was not closed.
    //
    int ERROR_SXS_XML_E_UNCLOSEDSTARTTAG = 14060;

    //
    // MessageId: ERROR_SXS_XML_E_UNCLOSEDENDTAG
    //
    // MessageText:
    //
    // Manifest Parse Error : End element was missing the character '>'.
    //
    int ERROR_SXS_XML_E_UNCLOSEDENDTAG = 14061;

    //
    // MessageId: ERROR_SXS_XML_E_UNCLOSEDSTRING
    //
    // MessageText:
    //
    // Manifest Parse Error : A string literal was not closed.
    //
    int ERROR_SXS_XML_E_UNCLOSEDSTRING = 14062;

    //
    // MessageId: ERROR_SXS_XML_E_UNCLOSEDCOMMENT
    //
    // MessageText:
    //
    // Manifest Parse Error : A comment was not closed.
    //
    int ERROR_SXS_XML_E_UNCLOSEDCOMMENT = 14063;

    //
    // MessageId: ERROR_SXS_XML_E_UNCLOSEDDECL
    //
    // MessageText:
    //
    // Manifest Parse Error : A declaration was not closed.
    //
    int ERROR_SXS_XML_E_UNCLOSEDDECL = 14064;

    //
    // MessageId: ERROR_SXS_XML_E_UNCLOSEDCDATA
    //
    // MessageText:
    //
    // Manifest Parse Error : A CDATA section was not closed.
    //
    int ERROR_SXS_XML_E_UNCLOSEDCDATA = 14065;

    //
    // MessageId: ERROR_SXS_XML_E_RESERVEDNAMESPACE
    //
    // MessageText:
    //
    // Manifest Parse Error : The namespace prefix is not allowed to start with
    // the reserved string "xml".
    //
    int ERROR_SXS_XML_E_RESERVEDNAMESPACE = 14066;

    //
    // MessageId: ERROR_SXS_XML_E_INVALIDENCODING
    //
    // MessageText:
    //
    // Manifest Parse Error : System does not support the specified encoding.
    //
    int ERROR_SXS_XML_E_INVALIDENCODING = 14067;

    //
    // MessageId: ERROR_SXS_XML_E_INVALIDSWITCH
    //
    // MessageText:
    //
    // Manifest Parse Error : Switch from current encoding to specified encoding
    // not supported.
    //
    int ERROR_SXS_XML_E_INVALIDSWITCH = 14068;

    //
    // MessageId: ERROR_SXS_XML_E_BADXMLCASE
    //
    // MessageText:
    //
    // Manifest Parse Error : The name 'xml' is reserved and must be lower case.
    //
    int ERROR_SXS_XML_E_BADXMLCASE = 14069;

    //
    // MessageId: ERROR_SXS_XML_E_INVALID_STANDALONE
    //
    // MessageText:
    //
    // Manifest Parse Error : The standalone attribute must have the value 'yes'
    // or 'no'.
    //
    int ERROR_SXS_XML_E_INVALID_STANDALONE = 14070;

    //
    // MessageId: ERROR_SXS_XML_E_UNEXPECTED_STANDALONE
    //
    // MessageText:
    //
    // Manifest Parse Error : The standalone attribute cannot be used in
    // external entities.
    //
    int ERROR_SXS_XML_E_UNEXPECTED_STANDALONE = 14071;

    //
    // MessageId: ERROR_SXS_XML_E_INVALID_VERSION
    //
    // MessageText:
    //
    // Manifest Parse Error : Invalid version number.
    //
    int ERROR_SXS_XML_E_INVALID_VERSION = 14072;

    //
    // MessageId: ERROR_SXS_XML_E_MISSINGEQUALS
    //
    // MessageText:
    //
    // Manifest Parse Error : Missing equals sign between attribute and
    // attribute value.
    //
    int ERROR_SXS_XML_E_MISSINGEQUALS = 14073;

    //
    // MessageId: ERROR_SXS_PROTECTION_RECOVERY_FAILED
    //
    // MessageText:
    //
    // Assembly Protection Error : Unable to recover the specified assembly.
    //
    int ERROR_SXS_PROTECTION_RECOVERY_FAILED = 14074;

    //
    // MessageId: ERROR_SXS_PROTECTION_PUBLIC_KEY_TOO_SHORT
    //
    // MessageText:
    //
    // Assembly Protection Error : The public key for an assembly was too short
    // to be allowed.
    //
    int ERROR_SXS_PROTECTION_PUBLIC_KEY_TOO_SHORT = 14075;

    //
    // MessageId: ERROR_SXS_PROTECTION_CATALOG_NOT_VALID
    //
    // MessageText:
    //
    // Assembly Protection Error : The catalog for an assembly is not valid, or
    // does not match the assembly's manifest.
    //
    int ERROR_SXS_PROTECTION_CATALOG_NOT_VALID = 14076;

    //
    // MessageId: ERROR_SXS_UNTRANSLATABLE_HRESULT
    //
    // MessageText:
    //
    // An HRESULT could not be translated to a corresponding Win32 error code.
    //
    int ERROR_SXS_UNTRANSLATABLE_HRESULT = 14077;

    //
    // MessageId: ERROR_SXS_PROTECTION_CATALOG_FILE_MISSING
    //
    // MessageText:
    //
    // Assembly Protection Error : The catalog for an assembly is missing.
    //
    int ERROR_SXS_PROTECTION_CATALOG_FILE_MISSING = 14078;

    //
    // MessageId: ERROR_SXS_MISSING_ASSEMBLY_IDENTITY_ATTRIBUTE
    //
    // MessageText:
    //
    // The supplied assembly identity is missing one or more attributes which
    // must be present in this context.
    //
    int ERROR_SXS_MISSING_ASSEMBLY_IDENTITY_ATTRIBUTE = 14079;

    //
    // MessageId: ERROR_SXS_INVALID_ASSEMBLY_IDENTITY_ATTRIBUTE_NAME
    //
    // MessageText:
    //
    // The supplied assembly identity has one or more attribute names that
    // contain characters not permitted in XML names.
    //
    int ERROR_SXS_INVALID_ASSEMBLY_IDENTITY_ATTRIBUTE_NAME = 14080;

    //
    // MessageId: ERROR_SXS_ASSEMBLY_MISSING
    //
    // MessageText:
    //
    // The referenced assembly could not be found.
    //
    int ERROR_SXS_ASSEMBLY_MISSING = 14081;

    //
    // MessageId: ERROR_SXS_CORRUPT_ACTIVATION_STACK
    //
    // MessageText:
    //
    // The activation context activation stack for the running thread of
    // execution is corrupt.
    //
    int ERROR_SXS_CORRUPT_ACTIVATION_STACK = 14082;

    //
    // MessageId: ERROR_SXS_CORRUPTION
    //
    // MessageText:
    //
    // The application isolation metadata for this process or thread has become
    // corrupt.
    //
    int ERROR_SXS_CORRUPTION = 14083;

    //
    // MessageId: ERROR_SXS_EARLY_DEACTIVATION
    //
    // MessageText:
    //
    // The activation context being deactivated is not the most recently
    // activated one.
    //
    int ERROR_SXS_EARLY_DEACTIVATION = 14084;

    //
    // MessageId: ERROR_SXS_INVALID_DEACTIVATION
    //
    // MessageText:
    //
    // The activation context being deactivated is not active for the current
    // thread of execution.
    //
    int ERROR_SXS_INVALID_DEACTIVATION = 14085;

    //
    // MessageId: ERROR_SXS_MULTIPLE_DEACTIVATION
    //
    // MessageText:
    //
    // The activation context being deactivated has already been deactivated.
    //
    int ERROR_SXS_MULTIPLE_DEACTIVATION = 14086;

    //
    // MessageId: ERROR_SXS_PROCESS_TERMINATION_REQUESTED
    //
    // MessageText:
    //
    // A component used by the isolation facility has requested to terminate the
    // process.
    //
    int ERROR_SXS_PROCESS_TERMINATION_REQUESTED = 14087;

    //
    // MessageId: ERROR_SXS_RELEASE_ACTIVATION_CONTEXT
    //
    // MessageText:
    //
    // A kernel mode component is releasing a reference on an activation
    // context.
    //
    int ERROR_SXS_RELEASE_ACTIVATION_CONTEXT = 14088;

    //
    // MessageId: ERROR_SXS_SYSTEM_DEFAULT_ACTIVATION_CONTEXT_EMPTY
    //
    // MessageText:
    //
    // The activation context of system default assembly could not be generated.
    //
    int ERROR_SXS_SYSTEM_DEFAULT_ACTIVATION_CONTEXT_EMPTY = 14089;

    //
    // MessageId: ERROR_SXS_INVALID_IDENTITY_ATTRIBUTE_VALUE
    //
    // MessageText:
    //
    // The value of an attribute in an identity is not within the legal range.
    //
    int ERROR_SXS_INVALID_IDENTITY_ATTRIBUTE_VALUE = 14090;

    //
    // MessageId: ERROR_SXS_INVALID_IDENTITY_ATTRIBUTE_NAME
    //
    // MessageText:
    //
    // The name of an attribute in an identity is not within the legal range.
    //
    int ERROR_SXS_INVALID_IDENTITY_ATTRIBUTE_NAME = 14091;

    //
    // MessageId: ERROR_SXS_IDENTITY_DUPLICATE_ATTRIBUTE
    //
    // MessageText:
    //
    // An identity contains two definitions for the same attribute.
    //
    int ERROR_SXS_IDENTITY_DUPLICATE_ATTRIBUTE = 14092;

    //
    // MessageId: ERROR_SXS_IDENTITY_PARSE_ERROR
    //
    // MessageText:
    //
    // The identity string is malformed. This may be due to a trailing comma,
    // more than two unnamed attributes, missing attribute name or missing
    // attribute value.
    //
    int ERROR_SXS_IDENTITY_PARSE_ERROR = 14093;

    //
    // MessageId: ERROR_MALFORMED_SUBSTITUTION_STRING
    //
    // MessageText:
    //
    // A string containing localized substitutable content was malformed. Either
    // a dollar sign ($) was follwed by something other than a left parenthesis
    // or another dollar sign or an substitution's right parenthesis was not
    // found.
    //
    int ERROR_MALFORMED_SUBSTITUTION_STRING = 14094;

    //
    // MessageId: ERROR_SXS_INCORRECT_PUBLIC_KEY_TOKEN
    //
    // MessageText:
    //
    // The public key token does not correspond to the public key specified.
    //
    int ERROR_SXS_INCORRECT_PUBLIC_KEY_TOKEN = 14095;

    //
    // MessageId: ERROR_UNMAPPED_SUBSTITUTION_STRING
    //
    // MessageText:
    //
    // A substitution string had no mapping.
    //
    int ERROR_UNMAPPED_SUBSTITUTION_STRING = 14096;

    //
    // MessageId: ERROR_SXS_ASSEMBLY_NOT_LOCKED
    //
    // MessageText:
    //
    // The component must be locked before making the request.
    //
    int ERROR_SXS_ASSEMBLY_NOT_LOCKED = 14097;

    //
    // MessageId: ERROR_SXS_COMPONENT_STORE_CORRUPT
    //
    // MessageText:
    //
    // The component store has been corrupted.
    //
    int ERROR_SXS_COMPONENT_STORE_CORRUPT = 14098;

    //
    // MessageId: ERROR_ADVANCED_INSTALLER_FAILED
    //
    // MessageText:
    //
    // An advanced installer failed during setup or servicing.
    //
    int ERROR_ADVANCED_INSTALLER_FAILED = 14099;

    //
    // MessageId: ERROR_XML_ENCODING_MISMATCH
    //
    // MessageText:
    //
    // The character encoding in the XML declaration did not match the encoding
    // used in the document.
    //
    int ERROR_XML_ENCODING_MISMATCH = 14100;

    //
    // MessageId: ERROR_SXS_MANIFEST_IDENTITY_SAME_BUT_CONTENTS_DIFFERENT
    //
    // MessageText:
    //
    // The identities of the manifests are identical but their contents are
    // different.
    //
    int ERROR_SXS_MANIFEST_IDENTITY_SAME_BUT_CONTENTS_DIFFERENT = 14101;

    //
    // MessageId: ERROR_SXS_IDENTITIES_DIFFERENT
    //
    // MessageText:
    //
    // The component identities are different.
    //
    int ERROR_SXS_IDENTITIES_DIFFERENT = 14102;

    //
    // MessageId: ERROR_SXS_ASSEMBLY_IS_NOT_A_DEPLOYMENT
    //
    // MessageText:
    //
    // The assembly is not a deployment.
    //
    int ERROR_SXS_ASSEMBLY_IS_NOT_A_DEPLOYMENT = 14103;

    //
    // MessageId: ERROR_SXS_FILE_NOT_PART_OF_ASSEMBLY
    //
    // MessageText:
    //
    // The file is not a part of the assembly.
    //
    int ERROR_SXS_FILE_NOT_PART_OF_ASSEMBLY = 14104;

    //
    // MessageId: ERROR_SXS_MANIFEST_TOO_BIG
    //
    // MessageText:
    //
    // The size of the manifest exceeds the maximum allowed.
    //
    int ERROR_SXS_MANIFEST_TOO_BIG = 14105;

    //
    // MessageId: ERROR_SXS_SETTING_NOT_REGISTERED
    //
    // MessageText:
    //
    // The setting is not registered.
    //
    int ERROR_SXS_SETTING_NOT_REGISTERED = 14106;

    //
    // MessageId: ERROR_SXS_TRANSACTION_CLOSURE_INCOMPLETE
    //
    // MessageText:
    //
    // One or more required members of the transaction are not present.
    //
    int ERROR_SXS_TRANSACTION_CLOSURE_INCOMPLETE = 14107;

    //
    // MessageId: ERROR_SMI_PRIMITIVE_INSTALLER_FAILED
    //
    // MessageText:
    //
    // The SMI primitive installer failed during setup or servicing.
    //
    int ERROR_SMI_PRIMITIVE_INSTALLER_FAILED = 14108;

    //
    // MessageId: ERROR_GENERIC_COMMAND_FAILED
    //
    // MessageText:
    //
    // A generic command executable returned a result that indicates failure.
    //
    int ERROR_GENERIC_COMMAND_FAILED = 14109;

    //
    // MessageId: ERROR_SXS_FILE_HASH_MISSING
    //
    // MessageText:
    //
    // A component is missing file verification information in its manifest.
    //
    int ERROR_SXS_FILE_HASH_MISSING = 14110;

    // /////////////////////////////////////////////////
    // =//
    // =End of Side By Side Error Codes //
    // =//
    // =14000 to=14999=//
    // /////////////////////////////////////////////////

    // /////////////////////////////////////////////////
    // =//
    // =Start of WinEvt Error codes=//
    // =//
    // =15000 to=15079=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_EVT_INVALID_CHANNEL_PATH
    //
    // MessageText:
    //
    // The specified channel path is invalid.
    //
    int ERROR_EVT_INVALID_CHANNEL_PATH = 15000;

    //
    // MessageId: ERROR_EVT_INVALID_QUERY
    //
    // MessageText:
    //
    // The specified query is invalid.
    //
    int ERROR_EVT_INVALID_QUERY = 15001;

    //
    // MessageId: ERROR_EVT_PUBLISHER_METADATA_NOT_FOUND
    //
    // MessageText:
    //
    // The publisher metadata cannot be found in the resource.
    //
    int ERROR_EVT_PUBLISHER_METADATA_NOT_FOUND = 15002;

    //
    // MessageId: ERROR_EVT_EVENT_TEMPLATE_NOT_FOUND
    //
    // MessageText:
    //
    // The template for an event definition cannot be found in the resource
    // (error =%1).
    //
    int ERROR_EVT_EVENT_TEMPLATE_NOT_FOUND = 15003;

    //
    // MessageId: ERROR_EVT_INVALID_PUBLISHER_NAME
    //
    // MessageText:
    //
    // The specified publisher name is invalid.
    //
    int ERROR_EVT_INVALID_PUBLISHER_NAME = 15004;

    //
    // MessageId: ERROR_EVT_INVALID_EVENT_DATA
    //
    // MessageText:
    //
    // The event data raised by the publisher is not compatible with the event
    // template definition in the publisher's manifest
    //
    int ERROR_EVT_INVALID_EVENT_DATA = 15005;

    //
    // MessageId: ERROR_EVT_CHANNEL_NOT_FOUND
    //
    // MessageText:
    //
    // The specified channel could not be found. Check channel configuration.
    //
    int ERROR_EVT_CHANNEL_NOT_FOUND = 15007;

    //
    // MessageId: ERROR_EVT_MALFORMED_XML_TEXT
    //
    // MessageText:
    //
    // The specified xml text was not well-formed. See Extended Error for more
    // details.
    //
    int ERROR_EVT_MALFORMED_XML_TEXT = 15008;

    //
    // MessageId: ERROR_EVT_SUBSCRIPTION_TO_DIRECT_CHANNEL
    //
    // MessageText:
    //
    // The caller is trying to subscribe to a direct channel which is not
    // allowed. The events for a direct channel go directly to a logfile and
    // cannot be subscribed to.
    //
    int ERROR_EVT_SUBSCRIPTION_TO_DIRECT_CHANNEL = 15009;

    //
    // MessageId: ERROR_EVT_CONFIGURATION_ERROR
    //
    // MessageText:
    //
    // Configuration error.
    //
    int ERROR_EVT_CONFIGURATION_ERROR = 15010;

    //
    // MessageId: ERROR_EVT_QUERY_RESULT_STALE
    //
    // MessageText:
    //
    // The query result is stale / invalid. This may be due to the log being
    // cleared or rolling over after the query result was created. Users should
    // handle this code by releasing the query result object and reissuing the
    // query.
    //
    int ERROR_EVT_QUERY_RESULT_STALE = 15011;

    //
    // MessageId: ERROR_EVT_QUERY_RESULT_INVALID_POSITION
    //
    // MessageText:
    //
    // Query result is currently at an invalid position.
    //
    int ERROR_EVT_QUERY_RESULT_INVALID_POSITION = 15012;

    //
    // MessageId: ERROR_EVT_NON_VALIDATING_MSXML
    //
    // MessageText:
    //
    // Registered MSXML doesn't support validation.
    //
    int ERROR_EVT_NON_VALIDATING_MSXML = 15013;

    //
    // MessageId: ERROR_EVT_FILTER_ALREADYSCOPED
    //
    // MessageText:
    //
    // An expression can only be followed by a change of scope operation if it
    // itself evaluates to a node set and is not already part of some other
    // change of scope operation.
    //
    int ERROR_EVT_FILTER_ALREADYSCOPED = 15014;

    //
    // MessageId: ERROR_EVT_FILTER_NOTELTSET
    //
    // MessageText:
    //
    // Can't perform a step operation from a term that does not represent an
    // element set.
    //
    int ERROR_EVT_FILTER_NOTELTSET = 15015;

    //
    // MessageId: ERROR_EVT_FILTER_INVARG
    //
    // MessageText:
    //
    // Left hand side arguments to binary operators must be either attributes,
    // nodes or variables and right hand side arguments must be constants.
    //
    int ERROR_EVT_FILTER_INVARG = 15016;

    //
    // MessageId: ERROR_EVT_FILTER_INVTEST
    //
    // MessageText:
    //
    // A step operation must involve either a node test or, in the case of a
    // predicate, an algebraic expression against which to test each node in the
    // node set identified by the preceeding node set can be evaluated.
    //
    int ERROR_EVT_FILTER_INVTEST = 15017;

    //
    // MessageId: ERROR_EVT_FILTER_INVTYPE
    //
    // MessageText:
    //
    // This data type is currently unsupported.
    //
    int ERROR_EVT_FILTER_INVTYPE = 15018;

    //
    // MessageId: ERROR_EVT_FILTER_PARSEERR
    //
    // MessageText:
    //
    // A syntax error occurred at position %1!d!
    //
    int ERROR_EVT_FILTER_PARSEERR = 15019;

    //
    // MessageId: ERROR_EVT_FILTER_UNSUPPORTEDOP
    //
    // MessageText:
    //
    // This operator is unsupported by this implementation of the filter.
    //
    int ERROR_EVT_FILTER_UNSUPPORTEDOP = 15020;

    //
    // MessageId: ERROR_EVT_FILTER_UNEXPECTEDTOKEN
    //
    // MessageText:
    //
    // The token encountered was unexpected.
    //
    int ERROR_EVT_FILTER_UNEXPECTEDTOKEN = 15021;

    //
    // MessageId: ERROR_EVT_INVALID_OPERATION_OVER_ENABLED_DIRECT_CHANNEL
    //
    // MessageText:
    //
    // The requested operation cannot be performed over an enabled direct
    // channel. The channel must first be disabled before performing the
    // requested operation.
    //
    int ERROR_EVT_INVALID_OPERATION_OVER_ENABLED_DIRECT_CHANNEL = 15022;

    //
    // MessageId: ERROR_EVT_INVALID_CHANNEL_PROPERTY_VALUE
    //
    // MessageText:
    //
    // Channel property %1!s! contains invalid value. The value has invalid
    // type, is outside of valid range, can't be updated or is not supported by
    // this type of channel.
    //
    int ERROR_EVT_INVALID_CHANNEL_PROPERTY_VALUE = 15023;

    //
    // MessageId: ERROR_EVT_INVALID_PUBLISHER_PROPERTY_VALUE
    //
    // MessageText:
    //
    // Publisher property %1!s! contains invalid value. The value has invalid
    // type, is outside of valid range, can't be updated or is not supported by
    // this type of publisher.
    //
    int ERROR_EVT_INVALID_PUBLISHER_PROPERTY_VALUE = 15024;

    //
    // MessageId: ERROR_EVT_CHANNEL_CANNOT_ACTIVATE
    //
    // MessageText:
    //
    // The channel fails to activate.
    //
    int ERROR_EVT_CHANNEL_CANNOT_ACTIVATE = 15025;

    //
    // MessageId: ERROR_EVT_FILTER_TOO_COMPLEX
    //
    // MessageText:
    //
    // The xpath expression exceeded supported complexity. Please symplify it or
    // split it into two or more simple expressions.
    //
    int ERROR_EVT_FILTER_TOO_COMPLEX = 15026;

    //
    // MessageId: ERROR_EVT_MESSAGE_NOT_FOUND
    //
    // MessageText:
    //
    // the message resource is present but the message is not found in the
    // string/message table
    //
    int ERROR_EVT_MESSAGE_NOT_FOUND = 15027;

    //
    // MessageId: ERROR_EVT_MESSAGE_ID_NOT_FOUND
    //
    // MessageText:
    //
    // The message id for the desired message could not be found.
    //
    int ERROR_EVT_MESSAGE_ID_NOT_FOUND = 15028;

    //
    // MessageId: ERROR_EVT_UNRESOLVED_VALUE_INSERT
    //
    // MessageText:
    //
    // The substitution string for insert index (%1) could not be found.
    //
    int ERROR_EVT_UNRESOLVED_VALUE_INSERT = 15029;

    //
    // MessageId: ERROR_EVT_UNRESOLVED_PARAMETER_INSERT
    //
    // MessageText:
    //
    // The description string for parameter reference (%1) could not be found.
    //
    int ERROR_EVT_UNRESOLVED_PARAMETER_INSERT = 15030;

    //
    // MessageId: ERROR_EVT_MAX_INSERTS_REACHED
    //
    // MessageText:
    //
    // The maximum number of replacements has been reached.
    //
    int ERROR_EVT_MAX_INSERTS_REACHED = 15031;

    //
    // MessageId: ERROR_EVT_EVENT_DEFINITION_NOT_FOUND
    //
    // MessageText:
    //
    // The event definition could not be found for event id (%1).
    //
    int ERROR_EVT_EVENT_DEFINITION_NOT_FOUND = 15032;

    //
    // MessageId: ERROR_EVT_MESSAGE_LOCALE_NOT_FOUND
    //
    // MessageText:
    //
    // The locale specific resource for the desired message is not present.
    //
    int ERROR_EVT_MESSAGE_LOCALE_NOT_FOUND = 15033;

    //
    // MessageId: ERROR_EVT_VERSION_TOO_OLD
    //
    // MessageText:
    //
    // The resource is too old to be compatible.
    //
    int ERROR_EVT_VERSION_TOO_OLD = 15034;

    //
    // MessageId: ERROR_EVT_VERSION_TOO_NEW
    //
    // MessageText:
    //
    // The resource is too new to be compatible.
    //
    int ERROR_EVT_VERSION_TOO_NEW = 15035;

    //
    // MessageId: ERROR_EVT_CANNOT_OPEN_CHANNEL_OF_QUERY
    //
    // MessageText:
    //
    // The channel at index %1!d! of the query can't be opened.
    //
    int ERROR_EVT_CANNOT_OPEN_CHANNEL_OF_QUERY = 15036;

    //
    // MessageId: ERROR_EVT_PUBLISHER_DISABLED
    //
    // MessageText:
    //
    // The publisher has been disabled and its resource is not avaiable. This
    // usually occurs when the publisher is in the process of being uninstalled
    // or upgraded.
    //
    int ERROR_EVT_PUBLISHER_DISABLED = 15037;

    //
    // MessageId: ERROR_EVT_FILTER_OUT_OF_RANGE
    //
    // MessageText:
    //
    // Attempted to create a numeric type that is outside of its valid range.
    //
    int ERROR_EVT_FILTER_OUT_OF_RANGE = 15038;

    // /////////////////////////////////////////////////
    // =//
    // =Start of Wecsvc Error codes=//
    // =//
    // =15080 to=15099=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_EC_SUBSCRIPTION_CANNOT_ACTIVATE
    //
    // MessageText:
    //
    // The subscription fails to activate.
    //
    int ERROR_EC_SUBSCRIPTION_CANNOT_ACTIVATE = 15080;

    //
    // MessageId: ERROR_EC_LOG_DISABLED
    //
    // MessageText:
    //
    // The log of the subscription is in disabled state, and can not be used to
    // forward events to. The log must first be enabled before the subscription
    // can be activated.
    //
    int ERROR_EC_LOG_DISABLED = 15081;

    //
    // MessageId: ERROR_EC_CIRCULAR_FORWARDING
    //
    // MessageText:
    //
    // When forwarding events from local machine to itself, the query of the
    // subscription can't contain target log of the subscription.
    //
    int ERROR_EC_CIRCULAR_FORWARDING = 15082;

    //
    // MessageId: ERROR_EC_CREDSTORE_FULL
    //
    // MessageText:
    //
    // The credential store that is used to save credentials is full.
    //
    int ERROR_EC_CREDSTORE_FULL = 15083;

    //
    // MessageId: ERROR_EC_CRED_NOT_FOUND
    //
    // MessageText:
    //
    // The credential used by this subscription can't be found in credential
    // store.
    //
    int ERROR_EC_CRED_NOT_FOUND = 15084;

    //
    // MessageId: ERROR_EC_NO_ACTIVE_CHANNEL
    //
    // MessageText:
    //
    // No active channel is found for the query.
    //
    int ERROR_EC_NO_ACTIVE_CHANNEL = 15085;

    // /////////////////////////////////////////////////
    // =//
    // =Start of MUI Error codes=//
    // =//
    // =15100 to=15199=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_MUI_FILE_NOT_FOUND
    //
    // MessageText:
    //
    // The resource loader failed to find MUI file.
    //
    int ERROR_MUI_FILE_NOT_FOUND = 15100;

    //
    // MessageId: ERROR_MUI_INVALID_FILE
    //
    // MessageText:
    //
    // The resource loader failed to load MUI file because the file fail to pass
    // validation.
    //
    int ERROR_MUI_INVALID_FILE = 15101;

    //
    // MessageId: ERROR_MUI_INVALID_RC_CONFIG
    //
    // MessageText:
    //
    // The RC Manifest is corrupted with garbage data or unsupported version or
    // missing required item.
    //
    int ERROR_MUI_INVALID_RC_CONFIG = 15102;

    //
    // MessageId: ERROR_MUI_INVALID_LOCALE_NAME
    //
    // MessageText:
    //
    // The RC Manifest has invalid culture name.
    //
    int ERROR_MUI_INVALID_LOCALE_NAME = 15103;

    //
    // MessageId: ERROR_MUI_INVALID_ULTIMATEFALLBACK_NAME
    //
    // MessageText:
    //
    // The RC Manifest has invalid ultimatefallback name.
    //
    int ERROR_MUI_INVALID_ULTIMATEFALLBACK_NAME = 15104;

    //
    // MessageId: ERROR_MUI_FILE_NOT_LOADED
    //
    // MessageText:
    //
    // The resource loader cache doesn't have loaded MUI entry.
    //
    int ERROR_MUI_FILE_NOT_LOADED = 15105;

    //
    // MessageId: ERROR_RESOURCE_ENUM_USER_STOP
    //
    // MessageText:
    //
    // User stopped resource enumeration.
    //
    int ERROR_RESOURCE_ENUM_USER_STOP = 15106;

    //
    // MessageId: ERROR_MUI_INTLSETTINGS_UILANG_NOT_INSTALLED
    //
    // MessageText:
    //
    // UI language installation failed.
    //
    int ERROR_MUI_INTLSETTINGS_UILANG_NOT_INSTALLED = 15107;

    //
    // MessageId: ERROR_MUI_INTLSETTINGS_INVALID_LOCALE_NAME
    //
    // MessageText:
    //
    // Locale installation failed.
    //
    int ERROR_MUI_INTLSETTINGS_INVALID_LOCALE_NAME = 15108;

    // /////////////////////////////////////////////////
    // =//
    // Start of Monitor Configuration API error codes//
    // =//
    // =15200 to=15249=//
    // /////////////////////////////////////////////////

    //
    // MessageId: ERROR_MCA_INVALID_CAPABILITIES_STRING
    //
    // MessageText:
    //
    // The monitor returned a DDC/CI capabilities string that did not comply
    // with the ACCESS.bus=3.0, DDC/CI=1.1 or MCCS=2 Revision=1 specification.
    //
    int ERROR_MCA_INVALID_CAPABILITIES_STRING = 15200;

    //
    // MessageId: ERROR_MCA_INVALID_VCP_VERSION
    //
    // MessageText:
    //
    // The monitor's VCP Version (0xDF) VCP code returned an invalid version
    // value.
    //
    int ERROR_MCA_INVALID_VCP_VERSION = 15201;

    //
    // MessageId: ERROR_MCA_MONITOR_VIOLATES_MCCS_SPECIFICATION
    //
    // MessageText:
    //
    // The monitor does not comply with the MCCS specification it claims to
    // support.
    //
    int ERROR_MCA_MONITOR_VIOLATES_MCCS_SPECIFICATION = 15202;

    //
    // MessageId: ERROR_MCA_MCCS_VERSION_MISMATCH
    //
    // MessageText:
    //
    // The MCCS version in a monitor's mccs_ver capability does not match the
    // MCCS version the monitor reports when the VCP Version (0xDF) VCP code is
    // used.
    //
    int ERROR_MCA_MCCS_VERSION_MISMATCH = 15203;

    //
    // MessageId: ERROR_MCA_UNSUPPORTED_MCCS_VERSION
    //
    // MessageText:
    //
    // The Monitor Configuration API only works with monitors that support the
    // MCCS=1.0 specification, MCCS=2.0 specification or the MCCS=2.0 Revision=1
    // specification.
    //
    int ERROR_MCA_UNSUPPORTED_MCCS_VERSION = 15204;

    //
    // MessageId: ERROR_MCA_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal Monitor Configuration API error occurred.
    //
    int ERROR_MCA_INTERNAL_ERROR = 15205;

    //
    // MessageId: ERROR_MCA_INVALID_TECHNOLOGY_TYPE_RETURNED
    //
    // MessageText:
    //
    // The monitor returned an invalid monitor technology type. CRT, Plasma and
    // LCD (TFT) are examples of monitor technology types. This error implies
    // that the monitor violated the MCCS=2.0 or MCCS=2.0 Revision=1
    // specification.
    //
    int ERROR_MCA_INVALID_TECHNOLOGY_TYPE_RETURNED = 15206;

    //
    // MessageId: ERROR_MCA_UNSUPPORTED_COLOR_TEMPERATURE
    //
    // MessageText:
    //
    // The caller of SetMonitorColorTemperature specified a color temperature
    // that the current monitor did not support. This error implies that the
    // monitor violated the MCCS=2.0 or MCCS=2.0 Revision=1 specification.
    //
    int ERROR_MCA_UNSUPPORTED_COLOR_TEMPERATURE = 15207;

    // ////////////////////////////////////////////////
    // =//
    // End of Monitor Configuration API error codes //
    // =//
    // =15200 to=15249=//
    // =//
    // ////////////////////////////////////////////////
    // ////////////////////////////////////////////////
    // =//
    // =Start of Syspart error codes=//
    // =15250 -=15299=//
    // =//
    // ////////////////////////////////////////////////

    //
    // MessageId: ERROR_AMBIGUOUS_SYSTEM_DEVICE
    //
    // MessageText:
    //
    // The requested system device cannot be identified due to multiple
    // indistinguishable devices potentially matching the identification
    // criteria.
    //
    int ERROR_AMBIGUOUS_SYSTEM_DEVICE = 15250;

    //
    // MessageId: ERROR_SYSTEM_DEVICE_NOT_FOUND
    //
    // MessageText:
    //
    // The requested system device cannot be found.
    //
    int ERROR_SYSTEM_DEVICE_NOT_FOUND = 15299;

    // //////////////////////////////////
    // =//
    // COM Error Codes=//
    // =//
    // //////////////////////////////////

    //
    // The return value of COM functions and methods is an HRESULT.
    // This is not a handle to anything, but is merely a=32-bit value
    // with several fields encoded in the value. The parts of an
    // HRESULT are shown below.
    //
    // Many of the macros and functions below were orginally defined to
    // operate on SCODEs. SCODEs are no longer used. The macros are
    // still present for compatibility and easy porting of Win16 code.
    // Newly written code should use the HRESULT macros and functions.
    //

    //
    // HRESULTs are=32 bit values layed out as follows:
    //
    // =3=3=2=2=2=2=2=2=2=2=2=2=1=1=1=1=1=1=1=1=1=1
    // =1 0=9=8=7=6=5=4=3=2=1 0=9=8=7=6=5=4=3=2=1 0=9=8=7=6=5=4=3=2=1 0
    // +-+-+-+-+-+---------------------+-------------------------------+
    // |S|R|C|N|r| Facility=|=Code=|
    // +-+-+-+-+-+---------------------+-------------------------------+
    //
    // where
    //
    // S - Severity - indicates success/fail
    //
    // =0 - Success
    // =1 - Fail (COERROR)
    //
    // R - reserved portion of the facility code, corresponds to NT's
    // =second severity bit.
    //
    // C - reserved portion of the facility code, corresponds to NT's
    // =C field.
    //
    // N - reserved portion of the facility code. Used to indicate a
    // =mapped NT status value.
    //
    // r - reserved portion of the facility code. Reserved for internal
    // =use. Used to indicate HRESULT values that are not status
    // =values, but are instead message ids for display strings.
    //
    // Facility - is the facility code
    //
    // Code - is the facility's status code
    //

    //
    // Severity values
    //

    int SEVERITY_SUCCESS = 0;
    int SEVERITY_ERROR = 1;

    //
    // Define bits here so macros are guaranteed to work

    int FACILITY_NT_BIT = 0x10000000;

    // ---------------------- HRESULT value definitions -----------------
    //
    // HRESULT definitions
    //

    int NOERROR = 0;

    //
    // Error definitions follow
    //

    //
    // Codes 0x4000-0x40ff are reserved for OLE
    //
    //
    // Error codes
    //
    //
    // MessageId: E_UNEXPECTED
    //
    // MessageText:
    //
    // Catastrophic failure
    //
    int E_UNEXPECTED = 0x8000FFFF;

    //
    // MessageId: E_NOTIMPL
    //
    // MessageText:
    //
    // Not implemented
    //
    int E_NOTIMPL = 0x80004001;

    //
    // MessageId: E_OUTOFMEMORY
    //
    // MessageText:
    //
    // Ran out of memory
    //
    int E_OUTOFMEMORY = 0x8007000E;

    //
    // MessageId: E_INVALIDARG
    //
    // MessageText:
    //
    // One or more arguments are invalid
    //
    int E_INVALIDARG = 0x80070057;

    //
    // MessageId: E_NOINTERFACE
    //
    // MessageText:
    //
    // No such interface supported
    //
    int E_NOINTERFACE = 0x80004002;

    //
    // MessageId: E_POINTER
    //
    // MessageText:
    //
    // Invalid pointer
    //
    int E_POINTER = 0x80004003;

    //
    // MessageId: E_HANDLE
    //
    // MessageText:
    //
    // Invalid handle
    //
    int E_HANDLE = 0x80070006;

    //
    // MessageId: E_ABORT
    //
    // MessageText:
    //
    // Operation aborted
    //
    int E_ABORT = 0x80004004;

    //
    // MessageId: E_FAIL
    //
    // MessageText:
    //
    // Unspecified error
    //
    int E_FAIL = 0x80004005;

    //
    // MessageId: E_ACCESSDENIED
    //
    // MessageText:
    //
    // General access denied error
    //
    int E_ACCESSDENIED = 0x80070005;

    //
    // MessageId: E_PENDING
    //
    // MessageText:
    //
    // The data necessary to complete this operation is not yet available.
    //
    int E_PENDING = 0x8000000A;

    //
    // MessageId: CO_E_INIT_TLS
    //
    // MessageText:
    //
    // Thread local storage failure
    //
    int CO_E_INIT_TLS = 0x80004006;

    //
    // MessageId: CO_E_INIT_SHARED_ALLOCATOR
    //
    // MessageText:
    //
    // Get shared memory allocator failure
    //
    int CO_E_INIT_SHARED_ALLOCATOR = 0x80004007;

    //
    // MessageId: CO_E_INIT_MEMORY_ALLOCATOR
    //
    // MessageText:
    //
    // Get memory allocator failure
    //
    int CO_E_INIT_MEMORY_ALLOCATOR = 0x80004008;

    //
    // MessageId: CO_E_INIT_CLASS_CACHE
    //
    // MessageText:
    //
    // Unable to initialize class cache
    //
    int CO_E_INIT_CLASS_CACHE = 0x80004009;

    //
    // MessageId: CO_E_INIT_RPC_CHANNEL
    //
    // MessageText:
    //
    // Unable to initialize RPC services
    //
    int CO_E_INIT_RPC_CHANNEL = 0x8000400A;

    //
    // MessageId: CO_E_INIT_TLS_SET_CHANNEL_CONTROL
    //
    // MessageText:
    //
    // Cannot set thread local storage channel control
    //
    int CO_E_INIT_TLS_SET_CHANNEL_CONTROL = 0x8000400B;

    //
    // MessageId: CO_E_INIT_TLS_CHANNEL_CONTROL
    //
    // MessageText:
    //
    // Could not allocate thread local storage channel control
    //
    int CO_E_INIT_TLS_CHANNEL_CONTROL = 0x8000400C;

    //
    // MessageId: CO_E_INIT_UNACCEPTED_USER_ALLOCATOR
    //
    // MessageText:
    //
    // The user supplied memory allocator is unacceptable
    //
    int CO_E_INIT_UNACCEPTED_USER_ALLOCATOR = 0x8000400D;

    //
    // MessageId: CO_E_INIT_SCM_MUTEX_EXISTS
    //
    // MessageText:
    //
    // The OLE service mutex already exists
    //
    int CO_E_INIT_SCM_MUTEX_EXISTS = 0x8000400E;

    //
    // MessageId: CO_E_INIT_SCM_FILE_MAPPING_EXISTS
    //
    // MessageText:
    //
    // The OLE service file mapping already exists
    //
    int CO_E_INIT_SCM_FILE_MAPPING_EXISTS = 0x8000400F;

    //
    // MessageId: CO_E_INIT_SCM_MAP_VIEW_OF_FILE
    //
    // MessageText:
    //
    // Unable to map view of file for OLE service
    //
    int CO_E_INIT_SCM_MAP_VIEW_OF_FILE = 0x80004010;

    //
    // MessageId: CO_E_INIT_SCM_EXEC_FAILURE
    //
    // MessageText:
    //
    // Failure attempting to launch OLE service
    //
    int CO_E_INIT_SCM_EXEC_FAILURE = 0x80004011;

    //
    // MessageId: CO_E_INIT_ONLY_SINGLE_THREADED
    //
    // MessageText:
    //
    // There was an attempt to call CoInitialize a second time while single
    // threaded
    //
    int CO_E_INIT_ONLY_SINGLE_THREADED = 0x80004012;

    //
    // MessageId: CO_E_CANT_REMOTE
    //
    // MessageText:
    //
    // A Remote activation was necessary but was not allowed
    //
    int CO_E_CANT_REMOTE = 0x80004013;

    //
    // MessageId: CO_E_BAD_SERVER_NAME
    //
    // MessageText:
    //
    // A Remote activation was necessary but the server name provided was
    // invalid
    //
    int CO_E_BAD_SERVER_NAME = 0x80004014;

    //
    // MessageId: CO_E_WRONG_SERVER_IDENTITY
    //
    // MessageText:
    //
    // The class is configured to run as a security id different from the caller
    //
    int CO_E_WRONG_SERVER_IDENTITY = 0x80004015;

    //
    // MessageId: CO_E_OLE1DDE_DISABLED
    //
    // MessageText:
    //
    // Use of Ole1 services requiring DDE windows is disabled
    //
    int CO_E_OLE1DDE_DISABLED = 0x80004016;

    //
    // MessageId: CO_E_RUNAS_SYNTAX
    //
    // MessageText:
    //
    // A RunAs specification must be <domain name>\<user name> or simply <user
    // name>
    //
    int CO_E_RUNAS_SYNTAX = 0x80004017;

    //
    // MessageId: CO_E_CREATEPROCESS_FAILURE
    //
    // MessageText:
    //
    // The server process could not be started. The pathname may be incorrect.
    //
    int CO_E_CREATEPROCESS_FAILURE = 0x80004018;

    //
    // MessageId: CO_E_RUNAS_CREATEPROCESS_FAILURE
    //
    // MessageText:
    //
    // The server process could not be started as the configured identity. The
    // pathname may be incorrect or unavailable.
    //
    int CO_E_RUNAS_CREATEPROCESS_FAILURE = 0x80004019;

    //
    // MessageId: CO_E_RUNAS_LOGON_FAILURE
    //
    // MessageText:
    //
    // The server process could not be started because the configured identity
    // is incorrect. Check the username and password.
    //
    int CO_E_RUNAS_LOGON_FAILURE = 0x8000401A;

    //
    // MessageId: CO_E_LAUNCH_PERMSSION_DENIED
    //
    // MessageText:
    //
    // The client is not allowed to launch this server.
    //
    int CO_E_LAUNCH_PERMSSION_DENIED = 0x8000401B;

    //
    // MessageId: CO_E_START_SERVICE_FAILURE
    //
    // MessageText:
    //
    // The service providing this server could not be started.
    //
    int CO_E_START_SERVICE_FAILURE = 0x8000401C;

    //
    // MessageId: CO_E_REMOTE_COMMUNICATION_FAILURE
    //
    // MessageText:
    //
    // This computer was unable to communicate with the computer providing the
    // server.
    //
    int CO_E_REMOTE_COMMUNICATION_FAILURE = 0x8000401D;

    //
    // MessageId: CO_E_SERVER_START_TIMEOUT
    //
    // MessageText:
    //
    // The server did not respond after being launched.
    //
    int CO_E_SERVER_START_TIMEOUT = 0x8000401E;

    //
    // MessageId: CO_E_CLSREG_INCONSISTENT
    //
    // MessageText:
    //
    // The registration information for this server is inconsistent or
    // incomplete.
    //
    int CO_E_CLSREG_INCONSISTENT = 0x8000401F;

    //
    // MessageId: CO_E_IIDREG_INCONSISTENT
    //
    // MessageText:
    //
    // The registration information for this interface is inconsistent or
    // incomplete.
    //
    int CO_E_IIDREG_INCONSISTENT = 0x80004020;

    //
    // MessageId: CO_E_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The operation attempted is not supported.
    //
    int CO_E_NOT_SUPPORTED = 0x80004021;

    //
    // MessageId: CO_E_RELOAD_DLL
    //
    // MessageText:
    //
    // A dll must be loaded.
    //
    int CO_E_RELOAD_DLL = 0x80004022;

    //
    // MessageId: CO_E_MSI_ERROR
    //
    // MessageText:
    //
    // A Microsoft Software Installer error was encountered.
    //
    int CO_E_MSI_ERROR = 0x80004023;

    //
    // MessageId: CO_E_ATTEMPT_TO_CREATE_OUTSIDE_CLIENT_CONTEXT
    //
    // MessageText:
    //
    // The specified activation could not occur in the client context as
    // specified.
    //
    int CO_E_ATTEMPT_TO_CREATE_OUTSIDE_CLIENT_CONTEXT = 0x80004024;

    //
    // MessageId: CO_E_SERVER_PAUSED
    //
    // MessageText:
    //
    // Activations on the server are paused.
    //
    int CO_E_SERVER_PAUSED = 0x80004025;

    //
    // MessageId: CO_E_SERVER_NOT_PAUSED
    //
    // MessageText:
    //
    // Activations on the server are not paused.
    //
    int CO_E_SERVER_NOT_PAUSED = 0x80004026;

    //
    // MessageId: CO_E_CLASS_DISABLED
    //
    // MessageText:
    //
    // The component or application containing the component has been disabled.
    //
    int CO_E_CLASS_DISABLED = 0x80004027;

    //
    // MessageId: CO_E_CLRNOTAVAILABLE
    //
    // MessageText:
    //
    // The common language runtime is not available
    //
    int CO_E_CLRNOTAVAILABLE = 0x80004028;

    //
    // MessageId: CO_E_ASYNC_WORK_REJECTED
    //
    // MessageText:
    //
    // The thread-pool rejected the submitted asynchronous work.
    //
    int CO_E_ASYNC_WORK_REJECTED = 0x80004029;

    //
    // MessageId: CO_E_SERVER_INIT_TIMEOUT
    //
    // MessageText:
    //
    // The server started, but did not finish initializing in a timely fashion.
    //
    int CO_E_SERVER_INIT_TIMEOUT = 0x8000402A;

    //
    // MessageId: CO_E_NO_SECCTX_IN_ACTIVATE
    //
    // MessageText:
    //
    // Unable to complete the call since there is no COM+ security context
    // inside IObjectControl.Activate.
    //
    int CO_E_NO_SECCTX_IN_ACTIVATE = 0x8000402B;

    //
    // MessageId: CO_E_TRACKER_CONFIG
    //
    // MessageText:
    //
    // The provided tracker configuration is invalid
    //
    int CO_E_TRACKER_CONFIG = 0x80004030;

    //
    // MessageId: CO_E_THREADPOOL_CONFIG
    //
    // MessageText:
    //
    // The provided thread pool configuration is invalid
    //
    int CO_E_THREADPOOL_CONFIG = 0x80004031;

    //
    // MessageId: CO_E_SXS_CONFIG
    //
    // MessageText:
    //
    // The provided side-by-side configuration is invalid
    //
    int CO_E_SXS_CONFIG = 0x80004032;

    //
    // MessageId: CO_E_MALFORMED_SPN
    //
    // MessageText:
    //
    // The server principal name (SPN) obtained during security negotiation is
    // malformed.
    //
    int CO_E_MALFORMED_SPN = 0x80004033;

    //
    // Success codes
    //
    HRESULT S_OK = new HRESULT(0);
    HRESULT S_FALSE = new HRESULT(1);

    // ******************
    // FACILITY_ITF
    // ******************

    //
    // Codes 0x0-0x01ff are reserved for the OLE group of
    // interfaces.
    //

    //
    // Generic OLE errors that may be returned by many inerfaces
    //

    int OLE_E_FIRST = 0x80040000;
    int OLE_E_LAST = 0x800400FF;
    int OLE_S_FIRST = 0x00040000;
    int OLE_S_LAST = 0x000400FF;

    //
    // Old OLE errors
    //
    //
    // MessageId: OLE_E_OLEVERB
    //
    // MessageText:
    //
    // Invalid OLEVERB structure
    //
    int OLE_E_OLEVERB = 0x80040000;

    //
    // MessageId: OLE_E_ADVF
    //
    // MessageText:
    //
    // Invalid advise flags
    //
    int OLE_E_ADVF = 0x80040001;

    //
    // MessageId: OLE_E_ENUM_NOMORE
    //
    // MessageText:
    //
    // Can't enumerate any more, because the associated data is missing
    //
    int OLE_E_ENUM_NOMORE = 0x80040002;

    //
    // MessageId: OLE_E_ADVISENOTSUPPORTED
    //
    // MessageText:
    //
    // This implementation doesn't take advises
    //
    int OLE_E_ADVISENOTSUPPORTED = 0x80040003;

    //
    // MessageId: OLE_E_NOCONNECTION
    //
    // MessageText:
    //
    // There is no connection for this connection ID
    //
    int OLE_E_NOCONNECTION = 0x80040004;

    //
    // MessageId: OLE_E_NOTRUNNING
    //
    // MessageText:
    //
    // Need to run the object to perform this operation
    //
    int OLE_E_NOTRUNNING = 0x80040005;

    //
    // MessageId: OLE_E_NOCACHE
    //
    // MessageText:
    //
    // There is no cache to operate on
    //
    int OLE_E_NOCACHE = 0x80040006;

    //
    // MessageId: OLE_E_BLANK
    //
    // MessageText:
    //
    // Uninitialized object
    //
    int OLE_E_BLANK = 0x80040007;

    //
    // MessageId: OLE_E_CLASSDIFF
    //
    // MessageText:
    //
    // Linked object's source class has changed
    //
    int OLE_E_CLASSDIFF = 0x80040008;

    //
    // MessageId: OLE_E_CANT_GETMONIKER
    //
    // MessageText:
    //
    // Not able to get the moniker of the object
    //
    int OLE_E_CANT_GETMONIKER = 0x80040009;

    //
    // MessageId: OLE_E_CANT_BINDTOSOURCE
    //
    // MessageText:
    //
    // Not able to bind to the source
    //
    int OLE_E_CANT_BINDTOSOURCE = 0x8004000A;

    //
    // MessageId: OLE_E_STATIC
    //
    // MessageText:
    //
    // Object is static; operation not allowed
    //
    int OLE_E_STATIC = 0x8004000B;

    //
    // MessageId: OLE_E_PROMPTSAVECANCELLED
    //
    // MessageText:
    //
    // User canceled out of save dialog
    //
    int OLE_E_PROMPTSAVECANCELLED = 0x8004000C;

    //
    // MessageId: OLE_E_INVALIDRECT
    //
    // MessageText:
    //
    // Invalid rectangle
    //
    int OLE_E_INVALIDRECT = 0x8004000D;

    //
    // MessageId: OLE_E_WRONGCOMPOBJ
    //
    // MessageText:
    //
    // compobj.dll is too old for the ole2.dll initialized
    //
    int OLE_E_WRONGCOMPOBJ = 0x8004000E;

    //
    // MessageId: OLE_E_INVALIDHWND
    //
    // MessageText:
    //
    // Invalid window handle
    //
    int OLE_E_INVALIDHWND = 0x8004000F;

    //
    // MessageId: OLE_E_NOT_INPLACEACTIVE
    //
    // MessageText:
    //
    // Object is not in any of the inplace active states
    //
    int OLE_E_NOT_INPLACEACTIVE = 0x80040010;

    //
    // MessageId: OLE_E_CANTCONVERT
    //
    // MessageText:
    //
    // Not able to convert object
    //
    int OLE_E_CANTCONVERT = 0x80040011;

    //
    // MessageId: OLE_E_NOSTORAGE
    //
    // MessageText:
    //
    // Not able to perform the operation because object is not given storage yet
    //
    int OLE_E_NOSTORAGE = 0x80040012;

    //
    // MessageId: DV_E_FORMATETC
    //
    // MessageText:
    //
    // Invalid FORMATETC structure
    //
    int DV_E_FORMATETC = 0x80040064;

    //
    // MessageId: DV_E_DVTARGETDEVICE
    //
    // MessageText:
    //
    // Invalid DVTARGETDEVICE structure
    //
    int DV_E_DVTARGETDEVICE = 0x80040065;

    //
    // MessageId: DV_E_STGMEDIUM
    //
    // MessageText:
    //
    // Invalid STDGMEDIUM structure
    //
    int DV_E_STGMEDIUM = 0x80040066;

    //
    // MessageId: DV_E_STATDATA
    //
    // MessageText:
    //
    // Invalid STATDATA structure
    //
    int DV_E_STATDATA = 0x80040067;

    //
    // MessageId: DV_E_LINDEX
    //
    // MessageText:
    //
    // Invalid lindex
    //
    int DV_E_LINDEX = 0x80040068;

    //
    // MessageId: DV_E_TYMED
    //
    // MessageText:
    //
    // Invalid tymed
    //
    int DV_E_TYMED = 0x80040069;

    //
    // MessageId: DV_E_CLIPFORMAT
    //
    // MessageText:
    //
    // Invalid clipboard format
    //
    int DV_E_CLIPFORMAT = 0x8004006A;

    //
    // MessageId: DV_E_DVASPECT
    //
    // MessageText:
    //
    // Invalid aspect(s)
    //
    int DV_E_DVASPECT = 0x8004006B;

    //
    // MessageId: DV_E_DVTARGETDEVICE_SIZE
    //
    // MessageText:
    //
    // tdSize parameter of the DVTARGETDEVICE structure is invalid
    //
    int DV_E_DVTARGETDEVICE_SIZE = 0x8004006C;

    //
    // MessageId: DV_E_NOIVIEWOBJECT
    //
    // MessageText:
    //
    // Object doesn't support IViewObject interface
    //
    int DV_E_NOIVIEWOBJECT = 0x8004006D;

    int DRAGDROP_E_FIRST = 0x80040100;
    int DRAGDROP_E_LAST = 0x8004010F;
    int DRAGDROP_S_FIRST = 0x00040100;
    int DRAGDROP_S_LAST = 0x0004010F;
    //
    // MessageId: DRAGDROP_E_NOTREGISTERED
    //
    // MessageText:
    //
    // Trying to revoke a drop target that has not been registered
    //
    int DRAGDROP_E_NOTREGISTERED = 0x80040100;

    //
    // MessageId: DRAGDROP_E_ALREADYREGISTERED
    //
    // MessageText:
    //
    // This window has already been registered as a drop target
    //
    int DRAGDROP_E_ALREADYREGISTERED = 0x80040101;

    //
    // MessageId: DRAGDROP_E_INVALIDHWND
    //
    // MessageText:
    //
    // Invalid window handle
    //
    int DRAGDROP_E_INVALIDHWND = 0x80040102;

    int CLASSFACTORY_E_FIRST = 0x80040110;
    int CLASSFACTORY_E_LAST = 0x8004011F;
    int CLASSFACTORY_S_FIRST = 0x00040110;
    int CLASSFACTORY_S_LAST = 0x0004011F;
    //
    // MessageId: CLASS_E_NOAGGREGATION
    //
    // MessageText:
    //
    // Class does not support aggregation (or class object is remote)
    //
    int CLASS_E_NOAGGREGATION = 0x80040110;

    //
    // MessageId: CLASS_E_CLASSNOTAVAILABLE
    //
    // MessageText:
    //
    // ClassFactory cannot supply requested class
    //
    int CLASS_E_CLASSNOTAVAILABLE = 0x80040111;

    //
    // MessageId: CLASS_E_NOTLICENSED
    //
    // MessageText:
    //
    // Class is not licensed for use
    //
    int CLASS_E_NOTLICENSED = 0x80040112;

    int MARSHAL_E_FIRST = 0x80040120;
    int MARSHAL_E_LAST = 0x8004012F;
    int MARSHAL_S_FIRST = 0x00040120;
    int MARSHAL_S_LAST = 0x0004012F;
    int DATA_E_FIRST = 0x80040130;
    int DATA_E_LAST = 0x8004013F;
    int DATA_S_FIRST = 0x00040130;
    int DATA_S_LAST = 0x0004013F;
    int VIEW_E_FIRST = 0x80040140;
    int VIEW_E_LAST = 0x8004014F;
    int VIEW_S_FIRST = 0x00040140;
    int VIEW_S_LAST = 0x0004014F;
    //
    // MessageId: VIEW_E_DRAW
    //
    // MessageText:
    //
    // Error drawing view
    //
    int VIEW_E_DRAW = 0x80040140;

    int REGDB_E_FIRST = 0x80040150;
    int REGDB_E_LAST = 0x8004015F;
    int REGDB_S_FIRST = 0x00040150;
    int REGDB_S_LAST = 0x0004015F;
    //
    // MessageId: REGDB_E_READREGDB
    //
    // MessageText:
    //
    // Could not read key from registry
    //
    int REGDB_E_READREGDB = 0x80040150;

    //
    // MessageId: REGDB_E_WRITEREGDB
    //
    // MessageText:
    //
    // Could not write key to registry
    //
    int REGDB_E_WRITEREGDB = 0x80040151;

    //
    // MessageId: REGDB_E_KEYMISSING
    //
    // MessageText:
    //
    // Could not find the key in the registry
    //
    int REGDB_E_KEYMISSING = 0x80040152;

    //
    // MessageId: REGDB_E_INVALIDVALUE
    //
    // MessageText:
    //
    // Invalid value for registry
    //
    int REGDB_E_INVALIDVALUE = 0x80040153;

    //
    // MessageId: REGDB_E_CLASSNOTREG
    //
    // MessageText:
    //
    // Class not registered
    //
    int REGDB_E_CLASSNOTREG = 0x80040154;

    //
    // MessageId: REGDB_E_IIDNOTREG
    //
    // MessageText:
    //
    // Interface not registered
    //
    int REGDB_E_IIDNOTREG = 0x80040155;

    //
    // MessageId: REGDB_E_BADTHREADINGMODEL
    //
    // MessageText:
    //
    // Threading model entry is not valid
    //
    int REGDB_E_BADTHREADINGMODEL = 0x80040156;

    int CAT_E_FIRST = 0x80040160;
    int CAT_E_LAST = 0x80040161;
    //
    // MessageId: CAT_E_CATIDNOEXIST
    //
    // MessageText:
    //
    // CATID does not exist
    //
    int CAT_E_CATIDNOEXIST = 0x80040160;

    //
    // MessageId: CAT_E_NODESCRIPTION
    //
    // MessageText:
    //
    // Description not found
    //
    int CAT_E_NODESCRIPTION = 0x80040161;

    // //////////////////////////////////
    // =//
    // Class Store Error Codes //
    // =//
    // //////////////////////////////////
    int CS_E_FIRST = 0x80040164;
    int CS_E_LAST = 0x8004016F;
    //
    // MessageId: CS_E_PACKAGE_NOTFOUND
    //
    // MessageText:
    //
    // No package in the software installation data in the Active Directory
    // meets this criteria.
    //
    int CS_E_PACKAGE_NOTFOUND = 0x80040164;

    //
    // MessageId: CS_E_NOT_DELETABLE
    //
    // MessageText:
    //
    // Deleting this will break the referential integrity of the software
    // installation data in the Active Directory.
    //
    int CS_E_NOT_DELETABLE = 0x80040165;

    //
    // MessageId: CS_E_CLASS_NOTFOUND
    //
    // MessageText:
    //
    // The CLSID was not found in the software installation data in the Active
    // Directory.
    //
    int CS_E_CLASS_NOTFOUND = 0x80040166;

    //
    // MessageId: CS_E_INVALID_VERSION
    //
    // MessageText:
    //
    // The software installation data in the Active Directory is corrupt.
    //
    int CS_E_INVALID_VERSION = 0x80040167;

    //
    // MessageId: CS_E_NO_CLASSSTORE
    //
    // MessageText:
    //
    // There is no software installation data in the Active Directory.
    //
    int CS_E_NO_CLASSSTORE = 0x80040168;

    //
    // MessageId: CS_E_OBJECT_NOTFOUND
    //
    // MessageText:
    //
    // There is no software installation data object in the Active Directory.
    //
    int CS_E_OBJECT_NOTFOUND = 0x80040169;

    //
    // MessageId: CS_E_OBJECT_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The software installation data object in the Active Directory already
    // exists.
    //
    int CS_E_OBJECT_ALREADY_EXISTS = 0x8004016A;

    //
    // MessageId: CS_E_INVALID_PATH
    //
    // MessageText:
    //
    // The path to the software installation data in the Active Directory is not
    // correct.
    //
    int CS_E_INVALID_PATH = 0x8004016B;

    //
    // MessageId: CS_E_NETWORK_ERROR
    //
    // MessageText:
    //
    // A network error interrupted the operation.
    //
    int CS_E_NETWORK_ERROR = 0x8004016C;

    //
    // MessageId: CS_E_ADMIN_LIMIT_EXCEEDED
    //
    // MessageText:
    //
    // The size of this object exceeds the maximum size set by the
    // Administrator.
    //
    int CS_E_ADMIN_LIMIT_EXCEEDED = 0x8004016D;

    //
    // MessageId: CS_E_SCHEMA_MISMATCH
    //
    // MessageText:
    //
    // The schema for the software installation data in the Active Directory
    // does not match the required schema.
    //
    int CS_E_SCHEMA_MISMATCH = 0x8004016E;

    //
    // MessageId: CS_E_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An error occurred in the software installation data in the Active
    // Directory.
    //
    int CS_E_INTERNAL_ERROR = 0x8004016F;

    int CACHE_E_FIRST = 0x80040170;
    int CACHE_E_LAST = 0x8004017F;
    int CACHE_S_FIRST = 0x00040170;
    int CACHE_S_LAST = 0x0004017F;
    //
    // MessageId: CACHE_E_NOCACHE_UPDATED
    //
    // MessageText:
    //
    // Cache not updated
    //
    int CACHE_E_NOCACHE_UPDATED = 0x80040170;

    int OLEOBJ_E_FIRST = 0x80040180;
    int OLEOBJ_E_LAST = 0x8004018F;
    int OLEOBJ_S_FIRST = 0x00040180;
    int OLEOBJ_S_LAST = 0x0004018F;
    //
    // MessageId: OLEOBJ_E_NOVERBS
    //
    // MessageText:
    //
    // No verbs for OLE object
    //
    int OLEOBJ_E_NOVERBS = 0x80040180;

    //
    // MessageId: OLEOBJ_E_INVALIDVERB
    //
    // MessageText:
    //
    // Invalid verb for OLE object
    //
    int OLEOBJ_E_INVALIDVERB = 0x80040181;

    int CLIENTSITE_E_FIRST = 0x80040190;
    int CLIENTSITE_E_LAST = 0x8004019F;
    int CLIENTSITE_S_FIRST = 0x00040190;
    int CLIENTSITE_S_LAST = 0x0004019F;
    //
    // MessageId: INPLACE_E_NOTUNDOABLE
    //
    // MessageText:
    //
    // Undo is not available
    //
    int INPLACE_E_NOTUNDOABLE = 0x800401A0;

    //
    // MessageId: INPLACE_E_NOTOOLSPACE
    //
    // MessageText:
    //
    // Space for tools is not available
    //
    int INPLACE_E_NOTOOLSPACE = 0x800401A1;

    int INPLACE_E_FIRST = 0x800401A0;
    int INPLACE_E_LAST = 0x800401AF;
    int INPLACE_S_FIRST = 0x000401A0;
    int INPLACE_S_LAST = 0x000401AF;
    int ENUM_E_FIRST = 0x800401B0;
    int ENUM_E_LAST = 0x800401BF;
    int ENUM_S_FIRST = 0x000401B0;
    int ENUM_S_LAST = 0x000401BF;
    int CONVERT10_E_FIRST = 0x800401C0;
    int CONVERT10_E_LAST = 0x800401CF;
    int CONVERT10_S_FIRST = 0x000401C0;
    int CONVERT10_S_LAST = 0x000401CF;
    //
    // MessageId: CONVERT10_E_OLESTREAM_GET
    //
    // MessageText:
    //
    // OLESTREAM Get method failed
    //
    int CONVERT10_E_OLESTREAM_GET = 0x800401C0;

    //
    // MessageId: CONVERT10_E_OLESTREAM_PUT
    //
    // MessageText:
    //
    // OLESTREAM Put method failed
    //
    int CONVERT10_E_OLESTREAM_PUT = 0x800401C1;

    //
    // MessageId: CONVERT10_E_OLESTREAM_FMT
    //
    // MessageText:
    //
    // Contents of the OLESTREAM not in correct format
    //
    int CONVERT10_E_OLESTREAM_FMT = 0x800401C2;

    //
    // MessageId: CONVERT10_E_OLESTREAM_BITMAP_TO_DIB
    //
    // MessageText:
    //
    // There was an error in a Windows GDI call while converting the bitmap to a
    // DIB
    //
    int CONVERT10_E_OLESTREAM_BITMAP_TO_DIB = 0x800401C3;

    //
    // MessageId: CONVERT10_E_STG_FMT
    //
    // MessageText:
    //
    // Contents of the IStorage not in correct format
    //
    int CONVERT10_E_STG_FMT = 0x800401C4;

    //
    // MessageId: CONVERT10_E_STG_NO_STD_STREAM
    //
    // MessageText:
    //
    // Contents of IStorage is missing one of the standard streams
    //
    int CONVERT10_E_STG_NO_STD_STREAM = 0x800401C5;

    //
    // MessageId: CONVERT10_E_STG_DIB_TO_BITMAP
    //
    // MessageText:
    //
    // There was an error in a Windows GDI call while converting the DIB to a
    // bitmap.
    // 
    //
    int CONVERT10_E_STG_DIB_TO_BITMAP = 0x800401C6;

    int CLIPBRD_E_FIRST = 0x800401D0;
    int CLIPBRD_E_LAST = 0x800401DF;
    int CLIPBRD_S_FIRST = 0x000401D0;
    int CLIPBRD_S_LAST = 0x000401DF;
    //
    // MessageId: CLIPBRD_E_CANT_OPEN
    //
    // MessageText:
    //
    // OpenClipboard Failed
    //
    int CLIPBRD_E_CANT_OPEN = 0x800401D0;

    //
    // MessageId: CLIPBRD_E_CANT_EMPTY
    //
    // MessageText:
    //
    // EmptyClipboard Failed
    //
    int CLIPBRD_E_CANT_EMPTY = 0x800401D1;

    //
    // MessageId: CLIPBRD_E_CANT_SET
    //
    // MessageText:
    //
    // SetClipboard Failed
    //
    int CLIPBRD_E_CANT_SET = 0x800401D2;

    //
    // MessageId: CLIPBRD_E_BAD_DATA
    //
    // MessageText:
    //
    // Data on clipboard is invalid
    //
    int CLIPBRD_E_BAD_DATA = 0x800401D3;

    //
    // MessageId: CLIPBRD_E_CANT_CLOSE
    //
    // MessageText:
    //
    // CloseClipboard Failed
    //
    int CLIPBRD_E_CANT_CLOSE = 0x800401D4;

    int MK_E_FIRST = 0x800401E0;
    int MK_E_LAST = 0x800401EF;
    int MK_S_FIRST = 0x000401E0;
    int MK_S_LAST = 0x000401EF;
    //
    // MessageId: MK_E_CONNECTMANUALLY
    //
    // MessageText:
    //
    // Moniker needs to be connected manually
    //
    int MK_E_CONNECTMANUALLY = 0x800401E0;

    //
    // MessageId: MK_E_EXCEEDEDDEADLINE
    //
    // MessageText:
    //
    // Operation exceeded deadline
    //
    int MK_E_EXCEEDEDDEADLINE = 0x800401E1;

    //
    // MessageId: MK_E_NEEDGENERIC
    //
    // MessageText:
    //
    // Moniker needs to be generic
    //
    int MK_E_NEEDGENERIC = 0x800401E2;

    //
    // MessageId: MK_E_UNAVAILABLE
    //
    // MessageText:
    //
    // Operation unavailable
    //
    int MK_E_UNAVAILABLE = 0x800401E3;

    //
    // MessageId: MK_E_SYNTAX
    //
    // MessageText:
    //
    // Invalid syntax
    //
    int MK_E_SYNTAX = 0x800401E4;

    //
    // MessageId: MK_E_NOOBJECT
    //
    // MessageText:
    //
    // No object for moniker
    //
    int MK_E_NOOBJECT = 0x800401E5;

    //
    // MessageId: MK_E_INVALIDEXTENSION
    //
    // MessageText:
    //
    // Bad extension for file
    //
    int MK_E_INVALIDEXTENSION = 0x800401E6;

    //
    // MessageId: MK_E_INTERMEDIATEINTERFACENOTSUPPORTED
    //
    // MessageText:
    //
    // Intermediate operation failed
    //
    int MK_E_INTERMEDIATEINTERFACENOTSUPPORTED = 0x800401E7;

    //
    // MessageId: MK_E_NOTBINDABLE
    //
    // MessageText:
    //
    // Moniker is not bindable
    //
    int MK_E_NOTBINDABLE = 0x800401E8;

    //
    // MessageId: MK_E_NOTBOUND
    //
    // MessageText:
    //
    // Moniker is not bound
    //
    int MK_E_NOTBOUND = 0x800401E9;

    //
    // MessageId: MK_E_CANTOPENFILE
    //
    // MessageText:
    //
    // Moniker cannot open file
    //
    int MK_E_CANTOPENFILE = 0x800401EA;

    //
    // MessageId: MK_E_MUSTBOTHERUSER
    //
    // MessageText:
    //
    // User input required for operation to succeed
    //
    int MK_E_MUSTBOTHERUSER = 0x800401EB;

    //
    // MessageId: MK_E_NOINVERSE
    //
    // MessageText:
    //
    // Moniker class has no inverse
    //
    int MK_E_NOINVERSE = 0x800401EC;

    //
    // MessageId: MK_E_NOSTORAGE
    //
    // MessageText:
    //
    // Moniker does not refer to storage
    //
    int MK_E_NOSTORAGE = 0x800401ED;

    //
    // MessageId: MK_E_NOPREFIX
    //
    // MessageText:
    //
    // No common prefix
    //
    int MK_E_NOPREFIX = 0x800401EE;

    //
    // MessageId: MK_E_ENUMERATION_FAILED
    //
    // MessageText:
    //
    // Moniker could not be enumerated
    //
    int MK_E_ENUMERATION_FAILED = 0x800401EF;

    int CO_E_FIRST = 0x800401F0;
    int CO_E_LAST = 0x800401FF;
    int CO_S_FIRST = 0x000401F0;
    int CO_S_LAST = 0x000401FF;
    //
    // MessageId: CO_E_NOTINITIALIZED
    //
    // MessageText:
    //
    // CoInitialize has not been called.
    //
    int CO_E_NOTINITIALIZED = 0x800401F0;

    //
    // MessageId: CO_E_ALREADYINITIALIZED
    //
    // MessageText:
    //
    // CoInitialize has already been called.
    //
    int CO_E_ALREADYINITIALIZED = 0x800401F1;

    //
    // MessageId: CO_E_CANTDETERMINECLASS
    //
    // MessageText:
    //
    // Class of object cannot be determined
    //
    int CO_E_CANTDETERMINECLASS = 0x800401F2;

    //
    // MessageId: CO_E_CLASSSTRING
    //
    // MessageText:
    //
    // Invalid class string
    //
    int CO_E_CLASSSTRING = 0x800401F3;

    //
    // MessageId: CO_E_IIDSTRING
    //
    // MessageText:
    //
    // Invalid interface string
    //
    int CO_E_IIDSTRING = 0x800401F4;

    //
    // MessageId: CO_E_APPNOTFOUND
    //
    // MessageText:
    //
    // Application not found
    //
    int CO_E_APPNOTFOUND = 0x800401F5;

    //
    // MessageId: CO_E_APPSINGLEUSE
    //
    // MessageText:
    //
    // Application cannot be run more than once
    //
    int CO_E_APPSINGLEUSE = 0x800401F6;

    //
    // MessageId: CO_E_ERRORINAPP
    //
    // MessageText:
    //
    // Some error in application program
    //
    int CO_E_ERRORINAPP = 0x800401F7;

    //
    // MessageId: CO_E_DLLNOTFOUND
    //
    // MessageText:
    //
    // DLL for class not found
    //
    int CO_E_DLLNOTFOUND = 0x800401F8;

    //
    // MessageId: CO_E_ERRORINDLL
    //
    // MessageText:
    //
    // Error in the DLL
    //
    int CO_E_ERRORINDLL = 0x800401F9;

    //
    // MessageId: CO_E_WRONGOSFORAPP
    //
    // MessageText:
    //
    // Wrong OS or OS version for application
    //
    int CO_E_WRONGOSFORAPP = 0x800401FA;

    //
    // MessageId: CO_E_OBJNOTREG
    //
    // MessageText:
    //
    // Object is not registered
    //
    int CO_E_OBJNOTREG = 0x800401FB;

    //
    // MessageId: CO_E_OBJISREG
    //
    // MessageText:
    //
    // Object is already registered
    //
    int CO_E_OBJISREG = 0x800401FC;

    //
    // MessageId: CO_E_OBJNOTCONNECTED
    //
    // MessageText:
    //
    // Object is not connected to server
    //
    int CO_E_OBJNOTCONNECTED = 0x800401FD;

    //
    // MessageId: CO_E_APPDIDNTREG
    //
    // MessageText:
    //
    // Application was launched but it didn't register a class factory
    //
    int CO_E_APPDIDNTREG = 0x800401FE;

    //
    // MessageId: CO_E_RELEASED
    //
    // MessageText:
    //
    // Object has been released
    //
    int CO_E_RELEASED = 0x800401FF;

    int EVENT_E_FIRST = 0x80040200;
    int EVENT_E_LAST = 0x8004021F;
    int EVENT_S_FIRST = 0x00040200;
    int EVENT_S_LAST = 0x0004021F;
    //
    // MessageId: EVENT_S_SOME_SUBSCRIBERS_FAILED
    //
    // MessageText:
    //
    // An event was able to invoke some but not all of the subscribers
    //
    int EVENT_S_SOME_SUBSCRIBERS_FAILED = 0x00040200;

    //
    // MessageId: EVENT_E_ALL_SUBSCRIBERS_FAILED
    //
    // MessageText:
    //
    // An event was unable to invoke any of the subscribers
    //
    int EVENT_E_ALL_SUBSCRIBERS_FAILED = 0x80040201;

    //
    // MessageId: EVENT_S_NOSUBSCRIBERS
    //
    // MessageText:
    //
    // An event was delivered but there were no subscribers
    //
    int EVENT_S_NOSUBSCRIBERS = 0x00040202;

    //
    // MessageId: EVENT_E_QUERYSYNTAX
    //
    // MessageText:
    //
    // A syntax error occurred trying to evaluate a query string
    //
    int EVENT_E_QUERYSYNTAX = 0x80040203;

    //
    // MessageId: EVENT_E_QUERYFIELD
    //
    // MessageText:
    //
    // An invalid field name was used in a query string
    //
    int EVENT_E_QUERYFIELD = 0x80040204;

    //
    // MessageId: EVENT_E_INTERNALEXCEPTION
    //
    // MessageText:
    //
    // An unexpected exception was raised
    //
    int EVENT_E_INTERNALEXCEPTION = 0x80040205;

    //
    // MessageId: EVENT_E_INTERNALERROR
    //
    // MessageText:
    //
    // An unexpected internal error was detected
    //
    int EVENT_E_INTERNALERROR = 0x80040206;

    //
    // MessageId: EVENT_E_INVALID_PER_USER_SID
    //
    // MessageText:
    //
    // The owner SID on a per-user subscription doesn't exist
    //
    int EVENT_E_INVALID_PER_USER_SID = 0x80040207;

    //
    // MessageId: EVENT_E_USER_EXCEPTION
    //
    // MessageText:
    //
    // A user-supplied component or subscriber raised an exception
    //
    int EVENT_E_USER_EXCEPTION = 0x80040208;

    //
    // MessageId: EVENT_E_TOO_MANY_METHODS
    //
    // MessageText:
    //
    // An interface has too many methods to fire events from
    //
    int EVENT_E_TOO_MANY_METHODS = 0x80040209;

    //
    // MessageId: EVENT_E_MISSING_EVENTCLASS
    //
    // MessageText:
    //
    // A subscription cannot be stored unless its event class already exists
    //
    int EVENT_E_MISSING_EVENTCLASS = 0x8004020A;

    //
    // MessageId: EVENT_E_NOT_ALL_REMOVED
    //
    // MessageText:
    //
    // Not all the objects requested could be removed
    //
    int EVENT_E_NOT_ALL_REMOVED = 0x8004020B;

    //
    // MessageId: EVENT_E_COMPLUS_NOT_INSTALLED
    //
    // MessageText:
    //
    // COM+ is required for this operation, but is not installed
    //
    int EVENT_E_COMPLUS_NOT_INSTALLED = 0x8004020C;

    //
    // MessageId: EVENT_E_CANT_MODIFY_OR_DELETE_UNCONFIGURED_OBJECT
    //
    // MessageText:
    //
    // Cannot modify or delete an object that was not added using the COM+ Admin
    // SDK
    //
    int EVENT_E_CANT_MODIFY_OR_DELETE_UNCONFIGURED_OBJECT = 0x8004020D;

    //
    // MessageId: EVENT_E_CANT_MODIFY_OR_DELETE_CONFIGURED_OBJECT
    //
    // MessageText:
    //
    // Cannot modify or delete an object that was added using the COM+ Admin SDK
    //
    int EVENT_E_CANT_MODIFY_OR_DELETE_CONFIGURED_OBJECT = 0x8004020E;

    //
    // MessageId: EVENT_E_INVALID_EVENT_CLASS_PARTITION
    //
    // MessageText:
    //
    // The event class for this subscription is in an invalid partition
    //
    int EVENT_E_INVALID_EVENT_CLASS_PARTITION = 0x8004020F;

    //
    // MessageId: EVENT_E_PER_USER_SID_NOT_LOGGED_ON
    //
    // MessageText:
    //
    // The owner of the PerUser subscription is not logged on to the system
    // specified
    //
    int EVENT_E_PER_USER_SID_NOT_LOGGED_ON = 0x80040210;

    int XACT_E_FIRST = 0x8004D000;
    int XACT_E_LAST = 0x8004D029;
    int XACT_S_FIRST = 0x0004D000;
    int XACT_S_LAST = 0x0004D010;
    //
    // MessageId: XACT_E_ALREADYOTHERSINGLEPHASE
    //
    // MessageText:
    //
    // Another single phase resource manager has already been enlisted in this
    // transaction.
    //
    int XACT_E_ALREADYOTHERSINGLEPHASE = 0x8004D000;

    //
    // MessageId: XACT_E_CANTRETAIN
    //
    // MessageText:
    //
    // A retaining commit or abort is not supported
    //
    int XACT_E_CANTRETAIN = 0x8004D001;

    //
    // MessageId: XACT_E_COMMITFAILED
    //
    // MessageText:
    //
    // The transaction failed to commit for an unknown reason. The transaction
    // was aborted.
    //
    int XACT_E_COMMITFAILED = 0x8004D002;

    //
    // MessageId: XACT_E_COMMITPREVENTED
    //
    // MessageText:
    //
    // Cannot call commit on this transaction object because the calling
    // application did not initiate the transaction.
    //
    int XACT_E_COMMITPREVENTED = 0x8004D003;

    //
    // MessageId: XACT_E_HEURISTICABORT
    //
    // MessageText:
    //
    // Instead of committing, the resource heuristically aborted.
    //
    int XACT_E_HEURISTICABORT = 0x8004D004;

    //
    // MessageId: XACT_E_HEURISTICCOMMIT
    //
    // MessageText:
    //
    // Instead of aborting, the resource heuristically committed.
    //
    int XACT_E_HEURISTICCOMMIT = 0x8004D005;

    //
    // MessageId: XACT_E_HEURISTICDAMAGE
    //
    // MessageText:
    //
    // Some of the states of the resource were committed while others were
    // aborted, likely because of heuristic decisions.
    //
    int XACT_E_HEURISTICDAMAGE = 0x8004D006;

    //
    // MessageId: XACT_E_HEURISTICDANGER
    //
    // MessageText:
    //
    // Some of the states of the resource may have been committed while others
    // may have been aborted, likely because of heuristic decisions.
    //
    int XACT_E_HEURISTICDANGER = 0x8004D007;

    //
    // MessageId: XACT_E_ISOLATIONLEVEL
    //
    // MessageText:
    //
    // The requested isolation level is not valid or supported.
    //
    int XACT_E_ISOLATIONLEVEL = 0x8004D008;

    //
    // MessageId: XACT_E_NOASYNC
    //
    // MessageText:
    //
    // The transaction manager doesn't support an asynchronous operation for
    // this method.
    //
    int XACT_E_NOASYNC = 0x8004D009;

    //
    // MessageId: XACT_E_NOENLIST
    //
    // MessageText:
    //
    // Unable to enlist in the transaction.
    //
    int XACT_E_NOENLIST = 0x8004D00A;

    //
    // MessageId: XACT_E_NOISORETAIN
    //
    // MessageText:
    //
    // The requested semantics of retention of isolation across retaining commit
    // and abort boundaries cannot be supported by this transaction
    // implementation, or isoF;ags was not equal to zero.
    //
    int XACT_E_NOISORETAIN = 0x8004D00B;

    //
    // MessageId: XACT_E_NORESOURCE
    //
    // MessageText:
    //
    // There is no resource presently associated with this enlistment
    //
    int XACT_E_NORESOURCE = 0x8004D00C;

    //
    // MessageId: XACT_E_NOTCURRENT
    //
    // MessageText:
    //
    // The transaction failed to commit due to the failure of optimistic
    // concurrency control in at least one of the resource managers.
    //
    int XACT_E_NOTCURRENT = 0x8004D00D;

    //
    // MessageId: XACT_E_NOTRANSACTION
    //
    // MessageText:
    //
    // The transaction has already been implicitly or explicitly committed or
    // aborted
    //
    int XACT_E_NOTRANSACTION = 0x8004D00E;

    //
    // MessageId: XACT_E_NOTSUPPORTED
    //
    // MessageText:
    //
    // An invalid combination of F;ags was specified
    //
    int XACT_E_NOTSUPPORTED = 0x8004D00F;

    //
    // MessageId: XACT_E_UNKNOWNRMGRID
    //
    // MessageText:
    //
    // The resource manager id is not associated with this transaction or the
    // transaction manager.
    //
    int XACT_E_UNKNOWNRMGRID = 0x8004D010;

    //
    // MessageId: XACT_E_WRONGSTATE
    //
    // MessageText:
    //
    // This method was called in the wrong state
    //
    int XACT_E_WRONGSTATE = 0x8004D011;

    //
    // MessageId: XACT_E_WRONGUOW
    //
    // MessageText:
    //
    // The indicated unit of work does not match the unit of work expected by
    // the resource manager.
    //
    int XACT_E_WRONGUOW = 0x8004D012;

    //
    // MessageId: XACT_E_XTIONEXISTS
    //
    // MessageText:
    //
    // An enlistment in a transaction already exists.
    //
    int XACT_E_XTIONEXISTS = 0x8004D013;

    //
    // MessageId: XACT_E_NOIMPORTOBJECT
    //
    // MessageText:
    //
    // An import object for the transaction could not be found.
    //
    int XACT_E_NOIMPORTOBJECT = 0x8004D014;

    //
    // MessageId: XACT_E_INVALIDCOOKIE
    //
    // MessageText:
    //
    // The transaction cookie is invalid.
    //
    int XACT_E_INVALIDCOOKIE = 0x8004D015;

    //
    // MessageId: XACT_E_INDOUBT
    //
    // MessageText:
    //
    // The transaction status is in doubt. A communication failure occurred, or
    // a transaction manager or resource manager has failed
    //
    int XACT_E_INDOUBT = 0x8004D016;

    //
    // MessageId: XACT_E_NOTIMEOUT
    //
    // MessageText:
    //
    // A time-out was specified, but time-outs are not supported.
    //
    int XACT_E_NOTIMEOUT = 0x8004D017;

    //
    // MessageId: XACT_E_ALREADYINPROGRESS
    //
    // MessageText:
    //
    // The requested operation is already in progress for the transaction.
    //
    int XACT_E_ALREADYINPROGRESS = 0x8004D018;

    //
    // MessageId: XACT_E_ABORTED
    //
    // MessageText:
    //
    // The transaction has already been aborted.
    //
    int XACT_E_ABORTED = 0x8004D019;

    //
    // MessageId: XACT_E_LOGFULL
    //
    // MessageText:
    //
    // The Transaction Manager returned a log full error.
    //
    int XACT_E_LOGFULL = 0x8004D01A;

    //
    // MessageId: XACT_E_TMNOTAVAILABLE
    //
    // MessageText:
    //
    // The Transaction Manager is not available.
    //
    int XACT_E_TMNOTAVAILABLE = 0x8004D01B;

    //
    // MessageId: XACT_E_CONNECTION_DOWN
    //
    // MessageText:
    //
    // A connection with the transaction manager was lost.
    //
    int XACT_E_CONNECTION_DOWN = 0x8004D01C;

    //
    // MessageId: XACT_E_CONNECTION_DENIED
    //
    // MessageText:
    //
    // A request to establish a connection with the transaction manager was
    // denied.
    //
    int XACT_E_CONNECTION_DENIED = 0x8004D01D;

    //
    // MessageId: XACT_E_REENLISTTIMEOUT
    //
    // MessageText:
    //
    // Resource manager reenlistment to determine transaction status timed out.
    //
    int XACT_E_REENLISTTIMEOUT = 0x8004D01E;

    //
    // MessageId: XACT_E_TIP_CONNECT_FAILED
    //
    // MessageText:
    //
    // This transaction manager failed to establish a connection with another
    // TIP transaction manager.
    //
    int XACT_E_TIP_CONNECT_FAILED = 0x8004D01F;

    //
    // MessageId: XACT_E_TIP_PROTOCOL_ERROR
    //
    // MessageText:
    //
    // This transaction manager encountered a protocol error with another TIP
    // transaction manager.
    //
    int XACT_E_TIP_PROTOCOL_ERROR = 0x8004D020;

    //
    // MessageId: XACT_E_TIP_PULL_FAILED
    //
    // MessageText:
    //
    // This transaction manager could not propagate a transaction from another
    // TIP transaction manager.
    //
    int XACT_E_TIP_PULL_FAILED = 0x8004D021;

    //
    // MessageId: XACT_E_DEST_TMNOTAVAILABLE
    //
    // MessageText:
    //
    // The Transaction Manager on the destination machine is not available.
    //
    int XACT_E_DEST_TMNOTAVAILABLE = 0x8004D022;

    //
    // MessageId: XACT_E_TIP_DISABLED
    //
    // MessageText:
    //
    // The Transaction Manager has disabled its support for TIP.
    //
    int XACT_E_TIP_DISABLED = 0x8004D023;

    //
    // MessageId: XACT_E_NETWORK_TX_DISABLED
    //
    // MessageText:
    //
    // The transaction manager has disabled its support for remote/network
    // transactions.
    //
    int XACT_E_NETWORK_TX_DISABLED = 0x8004D024;

    //
    // MessageId: XACT_E_PARTNER_NETWORK_TX_DISABLED
    //
    // MessageText:
    //
    // The partner transaction manager has disabled its support for
    // remote/network transactions.
    //
    int XACT_E_PARTNER_NETWORK_TX_DISABLED = 0x8004D025;

    //
    // MessageId: XACT_E_XA_TX_DISABLED
    //
    // MessageText:
    //
    // The transaction manager has disabled its support for XA transactions.
    //
    int XACT_E_XA_TX_DISABLED = 0x8004D026;

    //
    // MessageId: XACT_E_UNABLE_TO_READ_DTC_CONFIG
    //
    // MessageText:
    //
    // MSDTC was unable to read its configuration information.
    //
    int XACT_E_UNABLE_TO_READ_DTC_CONFIG = 0x8004D027;

    //
    // MessageId: XACT_E_UNABLE_TO_LOAD_DTC_PROXY
    //
    // MessageText:
    //
    // MSDTC was unable to load the dtc proxy dll.
    //
    int XACT_E_UNABLE_TO_LOAD_DTC_PROXY = 0x8004D028;

    //
    // MessageId: XACT_E_ABORTING
    //
    // MessageText:
    //
    // The local transaction has aborted.
    //
    int XACT_E_ABORTING = 0x8004D029;

    //
    // TXF & CRM errors start=4d080.
    //
    // MessageId: XACT_E_CLERKNOTFOUND
    //
    // MessageText:
    //
    // XACT_E_CLERKNOTFOUND
    //
    int XACT_E_CLERKNOTFOUND = 0x8004D080;

    //
    // MessageId: XACT_E_CLERKEXISTS
    //
    // MessageText:
    //
    // XACT_E_CLERKEXISTS
    //
    int XACT_E_CLERKEXISTS = 0x8004D081;

    //
    // MessageId: XACT_E_RECOVERYINPROGRESS
    //
    // MessageText:
    //
    // XACT_E_RECOVERYINPROGRESS
    //
    int XACT_E_RECOVERYINPROGRESS = 0x8004D082;

    //
    // MessageId: XACT_E_TRANSACTIONCLOSED
    //
    // MessageText:
    //
    // XACT_E_TRANSACTIONCLOSED
    //
    int XACT_E_TRANSACTIONCLOSED = 0x8004D083;

    //
    // MessageId: XACT_E_INVALIDLSN
    //
    // MessageText:
    //
    // XACT_E_INVALIDLSN
    //
    int XACT_E_INVALIDLSN = 0x8004D084;

    //
    // MessageId: XACT_E_REPLAYREQUEST
    //
    // MessageText:
    //
    // XACT_E_REPLAYREQUEST
    //
    int XACT_E_REPLAYREQUEST = 0x8004D085;

    // Begin XACT_DTC_CONSTANTS enumerated values defined in txdtc.h

    // SymbolicName=XACT_E_CONNECTION_REQUEST_DENIED
    //
    // MessageId: 0x8004D100; (No symbolic name defined)
    //
    // MessageText:
    //
    // The request to connect to the specified transaction coordinator was
    // denied.
    //

    // SymbolicName=XACT_E_TOOMANY_ENLISTMENTS
    //
    // MessageId: 0x8004D101; (No symbolic name defined)
    //
    // MessageText:
    //
    // The maximum number of enlistments for the specified transaction has been
    // reached.
    //

    // SymbolicName=XACT_E_DUPLICATE_GUID
    //
    // MessageId: 0x8004D102; (No symbolic name defined)
    //
    // MessageText:
    //
    // A resource manager with the same identifier is already registered with
    // the specified transaction coordinator.
    //

    // SymbolicName=XACT_E_NOTSINGLEPHASE
    //
    // MessageId: 0x8004D103; (No symbolic name defined)
    //
    // MessageText:
    //
    // The prepare request given was not eligible for single phase
    // optimizations.
    //

    // SymbolicName=XACT_E_RECOVERYALREADYDONE
    //
    // MessageId: 0x8004D104; (No symbolic name defined)
    //
    // MessageText:
    //
    // RecoveryComplete has already been called for the given resource manager.
    //

    // SymbolicName=XACT_E_PROTOCOL
    //
    // MessageId: 0x8004D105; (No symbolic name defined)
    //
    // MessageText:
    //
    // The interface call made was incorrect for the current state of the
    // protocol.
    //

    // SymbolicName=XACT_E_RM_FAILURE
    //
    // MessageId: 0x8004D106; (No symbolic name defined)
    //
    // MessageText:
    //
    // xa_open call failed for the XA resource.
    //

    // SymbolicName=XACT_E_RECOVERY_FAILED
    //
    // MessageId: 0x8004D107; (No symbolic name defined)
    //
    // MessageText:
    //
    // xa_recover call failed for the XA resource.
    //

    // SymbolicName=XACT_E_LU_NOT_FOUND
    //
    // MessageId: 0x8004D108; (No symbolic name defined)
    //
    // MessageText:
    //
    // The Logical Unit of Work specified cannot be found.
    //

    // SymbolicName=XACT_E_DUPLICATE_LU
    //
    // MessageId: 0x8004D109; (No symbolic name defined)
    //
    // MessageText:
    //
    // The specified Logical Unit of Work already exists.
    //

    // SymbolicName=XACT_E_LU_NOT_CONNECTED
    //
    // MessageId: 0x8004D10AL (No symbolic name defined)
    //
    // MessageText:
    //
    // Subordinate creation failed. The specified Logical Unit of Work was not
    // connected.
    //

    // SymbolicName=XACT_E_DUPLICATE_TRANSID
    //
    // MessageId: 0x8004D10BL (No symbolic name defined)
    //
    // MessageText:
    //
    // A transaction with the given identifier already exists.
    //

    // SymbolicName=XACT_E_LU_BUSY
    //
    // MessageId: 0x8004D10CL (No symbolic name defined)
    //
    // MessageText:
    //
    // The resource is in use.
    //

    // SymbolicName=XACT_E_LU_NO_RECOVERY_PROCESS
    //
    // MessageId: 0x8004D10DL (No symbolic name defined)
    //
    // MessageText:
    //
    // The LU Recovery process is down.
    //

    // SymbolicName=XACT_E_LU_DOWN
    //
    // MessageId: 0x8004D10EL (No symbolic name defined)
    //
    // MessageText:
    //
    // The remote session was lost.
    //

    // SymbolicName=XACT_E_LU_RECOVERING
    //
    // MessageId: 0x8004D10F; (No symbolic name defined)
    //
    // MessageText:
    //
    // The resource is currently recovering.
    //

    // SymbolicName=XACT_E_LU_RECOVERY_MISMATCH
    //
    // MessageId: 0x8004D110; (No symbolic name defined)
    //
    // MessageText:
    //
    // There was a mismatch in driving recovery.
    //

    // SymbolicName=XACT_E_RM_UNAVAILABLE
    //
    // MessageId: 0x8004D111; (No symbolic name defined)
    //
    // MessageText:
    //
    // An error occurred with the XA resource.
    //

    // End XACT_DTC_CONSTANTS enumerated values defined in txdtc.h

    //
    // OleTx Success codes.
    //
    //
    // MessageId: XACT_S_ASYNC
    //
    // MessageText:
    //
    // An asynchronous operation was specified. The operation has begun, but its
    // outcome is not known yet.
    //
    int XACT_S_ASYNC = 0x0004D000;

    //
    // MessageId: XACT_S_DEFECT
    //
    // MessageText:
    //
    // XACT_S_DEFECT
    //
    int XACT_S_DEFECT = 0x0004D001;

    //
    // MessageId: XACT_S_READONLY
    //
    // MessageText:
    //
    // The method call succeeded because the transaction was read-only.
    //
    int XACT_S_READONLY = 0x0004D002;

    //
    // MessageId: XACT_S_SOMENORETAIN
    //
    // MessageText:
    //
    // The transaction was successfully aborted. However, this is a coordinated
    // transaction, and some number of enlisted resources were aborted outright
    // because they could not support abort-retaining semantics
    //
    int XACT_S_SOMENORETAIN = 0x0004D003;

    //
    // MessageId: XACT_S_OKINFORM
    //
    // MessageText:
    //
    // No changes were made during this call, but the sink wants another chance
    // to look if any other sinks make further changes.
    //
    int XACT_S_OKINFORM = 0x0004D004;

    //
    // MessageId: XACT_S_MADECHANGESCONTENT
    //
    // MessageText:
    //
    // The sink is content and wishes the transaction to proceed. Changes were
    // made to one or more resources during this call.
    //
    int XACT_S_MADECHANGESCONTENT = 0x0004D005;

    //
    // MessageId: XACT_S_MADECHANGESINFORM
    //
    // MessageText:
    //
    // The sink is for the moment and wishes the transaction to proceed, but if
    // other changes are made following this return by other event sinks then
    // this sink wants another chance to look
    //
    int XACT_S_MADECHANGESINFORM = 0x0004D006;

    //
    // MessageId: XACT_S_ALLNORETAIN
    //
    // MessageText:
    //
    // The transaction was successfully aborted. However, the abort was
    // non-retaining.
    //
    int XACT_S_ALLNORETAIN = 0x0004D007;

    //
    // MessageId: XACT_S_ABORTING
    //
    // MessageText:
    //
    // An abort operation was already in progress.
    //
    int XACT_S_ABORTING = 0x0004D008;

    //
    // MessageId: XACT_S_SINGLEPHASE
    //
    // MessageText:
    //
    // The resource manager has performed a single-phase commit of the
    // transaction.
    //
    int XACT_S_SINGLEPHASE = 0x0004D009;

    //
    // MessageId: XACT_S_LOCALLY_OK
    //
    // MessageText:
    //
    // The local transaction has not aborted.
    //
    int XACT_S_LOCALLY_OK = 0x0004D00A;

    //
    // MessageId: XACT_S_LASTRESOURCEMANAGER
    //
    // MessageText:
    //
    // The resource manager has requested to be the coordinator (last resource
    // manager) for the transaction.
    //
    int XACT_S_LASTRESOURCEMANAGER = 0x0004D010;

    int CONTEXT_E_FIRST = 0x8004E000;
    int CONTEXT_E_LAST = 0x8004E02F;
    int CONTEXT_S_FIRST = 0x0004E000;
    int CONTEXT_S_LAST = 0x0004E02F;
    //
    // MessageId: CONTEXT_E_ABORTED
    //
    // MessageText:
    //
    // The root transaction wanted to commit, but transaction aborted
    //
    int CONTEXT_E_ABORTED = 0x8004E002;

    //
    // MessageId: CONTEXT_E_ABORTING
    //
    // MessageText:
    //
    // You made a method call on a COM+ component that has a transaction that
    // has already aborted or in the process of aborting.
    //
    int CONTEXT_E_ABORTING = 0x8004E003;

    //
    // MessageId: CONTEXT_E_NOCONTEXT
    //
    // MessageText:
    //
    // There is no MTS object context
    //
    int CONTEXT_E_NOCONTEXT = 0x8004E004;

    //
    // MessageId: CONTEXT_E_WOULD_DEADLOCK
    //
    // MessageText:
    //
    // The component is configured to use synchronization and this method call
    // would cause a deadlock to occur.
    //
    int CONTEXT_E_WOULD_DEADLOCK = 0x8004E005;

    //
    // MessageId: CONTEXT_E_SYNCH_TIMEOUT
    //
    // MessageText:
    //
    // The component is configured to use synchronization and a thread has timed
    // out waiting to enter the context.
    //
    int CONTEXT_E_SYNCH_TIMEOUT = 0x8004E006;

    //
    // MessageId: CONTEXT_E_OLDREF
    //
    // MessageText:
    //
    // You made a method call on a COM+ component that has a transaction that
    // has already committed or aborted.
    //
    int CONTEXT_E_OLDREF = 0x8004E007;

    //
    // MessageId: CONTEXT_E_ROLENOTFOUND
    //
    // MessageText:
    //
    // The specified role was not configured for the application
    //
    int CONTEXT_E_ROLENOTFOUND = 0x8004E00C;

    //
    // MessageId: CONTEXT_E_TMNOTAVAILABLE
    //
    // MessageText:
    //
    // COM+ was unable to talk to the Microsoft Distributed Transaction
    // Coordinator
    //
    int CONTEXT_E_TMNOTAVAILABLE = 0x8004E00F;

    //
    // MessageId: CO_E_ACTIVATIONFAILED
    //
    // MessageText:
    //
    // An unexpected error occurred during COM+ Activation.
    //
    int CO_E_ACTIVATIONFAILED = 0x8004E021;

    //
    // MessageId: CO_E_ACTIVATIONFAILED_EVENTLOGGED
    //
    // MessageText:
    //
    // COM+ Activation failed. Check the event log for more information
    //
    int CO_E_ACTIVATIONFAILED_EVENTLOGGED = 0x8004E022;

    //
    // MessageId: CO_E_ACTIVATIONFAILED_CATALOGERROR
    //
    // MessageText:
    //
    // COM+ Activation failed due to a catalog or configuration error.
    //
    int CO_E_ACTIVATIONFAILED_CATALOGERROR = 0x8004E023;

    //
    // MessageId: CO_E_ACTIVATIONFAILED_TIMEOUT
    //
    // MessageText:
    //
    // COM+ activation failed because the activation could not be completed in
    // the specified amount of time.
    //
    int CO_E_ACTIVATIONFAILED_TIMEOUT = 0x8004E024;

    //
    // MessageId: CO_E_INITIALIZATIONFAILED
    //
    // MessageText:
    //
    // COM+ Activation failed because an initialization function failed. Check
    // the event log for more information.
    //
    int CO_E_INITIALIZATIONFAILED = 0x8004E025;

    //
    // MessageId: CONTEXT_E_NOJIT
    //
    // MessageText:
    //
    // The requested operation requires that JIT be in the current context and
    // it is not
    //
    int CONTEXT_E_NOJIT = 0x8004E026;

    //
    // MessageId: CONTEXT_E_NOTRANSACTION
    //
    // MessageText:
    //
    // The requested operation requires that the current context have a
    // Transaction, and it does not
    //
    int CONTEXT_E_NOTRANSACTION = 0x8004E027;

    //
    // MessageId: CO_E_THREADINGMODEL_CHANGED
    //
    // MessageText:
    //
    // The components threading model has changed after install into a COM+
    // Application. Please re-install component.
    //
    int CO_E_THREADINGMODEL_CHANGED = 0x8004E028;

    //
    // MessageId: CO_E_NOIISINTRINSICS
    //
    // MessageText:
    //
    // IIS intrinsics not available. Start your work with IIS.
    //
    int CO_E_NOIISINTRINSICS = 0x8004E029;

    //
    // MessageId: CO_E_NOCOOKIES
    //
    // MessageText:
    //
    // An attempt to write a cookie failed.
    //
    int CO_E_NOCOOKIES = 0x8004E02A;

    //
    // MessageId: CO_E_DBERROR
    //
    // MessageText:
    //
    // An attempt to use a database generated a database specific error.
    //
    int CO_E_DBERROR = 0x8004E02B;

    //
    // MessageId: CO_E_NOTPOOLED
    //
    // MessageText:
    //
    // The COM+ component you created must use object pooling to work.
    //
    int CO_E_NOTPOOLED = 0x8004E02C;

    //
    // MessageId: CO_E_NOTCONSTRUCTED
    //
    // MessageText:
    //
    // The COM+ component you created must use object construction to work
    // correctly.
    //
    int CO_E_NOTCONSTRUCTED = 0x8004E02D;

    //
    // MessageId: CO_E_NOSYNCHRONIZATION
    //
    // MessageText:
    //
    // The COM+ component requires synchronization, and it is not configured for
    // it.
    //
    int CO_E_NOSYNCHRONIZATION = 0x8004E02E;

    //
    // MessageId: CO_E_ISOLEVELMISMATCH
    //
    // MessageText:
    //
    // The TxIsolation Level property for the COM+ component being created is
    // stronger than the TxIsolationLevel for the "root" component for the
    // transaction. The creation failed.
    //
    int CO_E_ISOLEVELMISMATCH = 0x8004E02F;

    //
    // MessageId: CO_E_CALL_OUT_OF_TX_SCOPE_NOT_ALLOWED
    //
    // MessageText:
    //
    // The component attempted to make a cross-context call between invocations
    // of EnterTransactionScopeand ExitTransactionScope. This is not allowed.
    // Cross-context calls cannot be made while inside of a transaction scope.
    //
    int CO_E_CALL_OUT_OF_TX_SCOPE_NOT_ALLOWED = 0x8004E030;

    //
    // MessageId: CO_E_EXIT_TRANSACTION_SCOPE_NOT_CALLED
    //
    // MessageText:
    //
    // The component made a call to EnterTransactionScope, but did not make a
    // corresponding call to ExitTransactionScope before returning.
    //
    int CO_E_EXIT_TRANSACTION_SCOPE_NOT_CALLED = 0x8004E031;

    //
    // Old OLE Success Codes
    //
    //
    // MessageId: OLE_S_USEREG
    //
    // MessageText:
    //
    // Use the registry database to provide the requested information
    //
    int OLE_S_USEREG = 0x00040000;

    //
    // MessageId: OLE_S_STATIC
    //
    // MessageText:
    //
    // Success, but static
    //
    int OLE_S_STATIC = 0x00040001;

    //
    // MessageId: OLE_S_MAC_CLIPFORMAT
    //
    // MessageText:
    //
    // Macintosh clipboard format
    //
    int OLE_S_MAC_CLIPFORMAT = 0x00040002;

    //
    // MessageId: DRAGDROP_S_DROP
    //
    // MessageText:
    //
    // Successful drop took place
    //
    int DRAGDROP_S_DROP = 0x00040100;

    //
    // MessageId: DRAGDROP_S_CANCEL
    //
    // MessageText:
    //
    // Drag-drop operation canceled
    //
    int DRAGDROP_S_CANCEL = 0x00040101;

    //
    // MessageId: DRAGDROP_S_USEDEFAULTCURSORS
    //
    // MessageText:
    //
    // Use the default cursor
    //
    int DRAGDROP_S_USEDEFAULTCURSORS = 0x00040102;

    //
    // MessageId: DATA_S_SAMEFORMATETC
    //
    // MessageText:
    //
    // Data has same FORMATETC
    //
    int DATA_S_SAMEFORMATETC = 0x00040130;

    //
    // MessageId: VIEW_S_ALREADY_FROZEN
    //
    // MessageText:
    //
    // View is already frozen
    //
    int VIEW_S_ALREADY_FROZEN = 0x00040140;

    //
    // MessageId: CACHE_S_FORMATETC_NOTSUPPORTED
    //
    // MessageText:
    //
    // FORMATETC not supported
    //
    int CACHE_S_FORMATETC_NOTSUPPORTED = 0x00040170;

    //
    // MessageId: CACHE_S_SAMECACHE
    //
    // MessageText:
    //
    // Same cache
    //
    int CACHE_S_SAMECACHE = 0x00040171;

    //
    // MessageId: CACHE_S_SOMECACHES_NOTUPDATED
    //
    // MessageText:
    //
    // Some cache(s) not updated
    //
    int CACHE_S_SOMECACHES_NOTUPDATED = 0x00040172;

    //
    // MessageId: OLEOBJ_S_INVALIDVERB
    //
    // MessageText:
    //
    // Invalid verb for OLE object
    //
    int OLEOBJ_S_INVALIDVERB = 0x00040180;

    //
    // MessageId: OLEOBJ_S_CANNOT_DOVERB_NOW
    //
    // MessageText:
    //
    // Verb number is valid but verb cannot be done now
    //
    int OLEOBJ_S_CANNOT_DOVERB_NOW = 0x00040181;

    //
    // MessageId: OLEOBJ_S_INVALIDHWND
    //
    // MessageText:
    //
    // Invalid window handle passed
    //
    int OLEOBJ_S_INVALIDHWND = 0x00040182;

    //
    // MessageId: INPLACE_S_TRUNCATED
    //
    // MessageText:
    //
    // Message is too long; some of it had to be truncated before displaying
    //
    int INPLACE_S_TRUNCATED = 0x000401A0;

    //
    // MessageId: CONVERT10_S_NO_PRESENTATION
    //
    // MessageText:
    //
    // Unable to convert OLESTREAM to IStorage
    //
    int CONVERT10_S_NO_PRESENTATION = 0x000401C0;

    //
    // MessageId: MK_S_REDUCED_TO_SELF
    //
    // MessageText:
    //
    // Moniker reduced to itself
    //
    int MK_S_REDUCED_TO_SELF = 0x000401E2;

    //
    // MessageId: MK_S_ME
    //
    // MessageText:
    //
    // Common prefix is this moniker
    //
    int MK_S_ME = 0x000401E4;

    //
    // MessageId: MK_S_HIM
    //
    // MessageText:
    //
    // Common prefix is input moniker
    //
    int MK_S_HIM = 0x000401E5;

    //
    // MessageId: MK_S_US
    //
    // MessageText:
    //
    // Common prefix is both monikers
    //
    int MK_S_US = 0x000401E6;

    //
    // MessageId: MK_S_MONIKERALREADYREGISTERED
    //
    // MessageText:
    //
    // Moniker is already registered in running object table
    //
    int MK_S_MONIKERALREADYREGISTERED = 0x000401E7;

    //
    // Task Scheduler errors
    //
    //
    // MessageId: SCHED_S_TASK_READY
    //
    // MessageText:
    //
    // The task is ready to run at its next scheduled time.
    //
    int SCHED_S_TASK_READY = 0x00041300;

    //
    // MessageId: SCHED_S_TASK_RUNNING
    //
    // MessageText:
    //
    // The task is currently running.
    //
    int SCHED_S_TASK_RUNNING = 0x00041301;

    //
    // MessageId: SCHED_S_TASK_DISABLED
    //
    // MessageText:
    //
    // The task will not run at the scheduled times because it has been
    // disabled.
    //
    int SCHED_S_TASK_DISABLED = 0x00041302;

    //
    // MessageId: SCHED_S_TASK_HAS_NOT_RUN
    //
    // MessageText:
    //
    // The task has not yet run.
    //
    int SCHED_S_TASK_HAS_NOT_RUN = 0x00041303;

    //
    // MessageId: SCHED_S_TASK_NO_MORE_RUNS
    //
    // MessageText:
    //
    // There are no more runs scheduled for this task.
    //
    int SCHED_S_TASK_NO_MORE_RUNS = 0x00041304;

    //
    // MessageId: SCHED_S_TASK_NOT_SCHEDULED
    //
    // MessageText:
    //
    // One or more of the properties that are needed to run this task on a
    // schedule have not been set.
    //
    int SCHED_S_TASK_NOT_SCHEDULED = 0x00041305;

    //
    // MessageId: SCHED_S_TASK_TERMINATED
    //
    // MessageText:
    //
    // The last run of the task was terminated by the user.
    //
    int SCHED_S_TASK_TERMINATED = 0x00041306;

    //
    // MessageId: SCHED_S_TASK_NO_VALID_TRIGGERS
    //
    // MessageText:
    //
    // Either the task has no triggers or the existing triggers are disabled or
    // not set.
    //
    int SCHED_S_TASK_NO_VALID_TRIGGERS = 0x00041307;

    //
    // MessageId: SCHED_S_EVENT_TRIGGER
    //
    // MessageText:
    //
    // Event triggers don't have set run times.
    //
    int SCHED_S_EVENT_TRIGGER = 0x00041308;

    //
    // MessageId: SCHED_E_TRIGGER_NOT_FOUND
    //
    // MessageText:
    //
    // Trigger not found.
    //
    int SCHED_E_TRIGGER_NOT_FOUND = 0x80041309;

    //
    // MessageId: SCHED_E_TASK_NOT_READY
    //
    // MessageText:
    //
    // One or more of the properties that are needed to run this task have not
    // been set.
    //
    int SCHED_E_TASK_NOT_READY = 0x8004130A;

    //
    // MessageId: SCHED_E_TASK_NOT_RUNNING
    //
    // MessageText:
    //
    // There is no running instance of the task.
    //
    int SCHED_E_TASK_NOT_RUNNING = 0x8004130B;

    //
    // MessageId: SCHED_E_SERVICE_NOT_INSTALLED
    //
    // MessageText:
    //
    // The Task Scheduler Service is not installed on this computer.
    //
    int SCHED_E_SERVICE_NOT_INSTALLED = 0x8004130C;

    //
    // MessageId: SCHED_E_CANNOT_OPEN_TASK
    //
    // MessageText:
    //
    // The task object could not be opened.
    //
    int SCHED_E_CANNOT_OPEN_TASK = 0x8004130D;

    //
    // MessageId: SCHED_E_INVALID_TASK
    //
    // MessageText:
    //
    // The object is either an invalid task object or is not a task object.
    //
    int SCHED_E_INVALID_TASK = 0x8004130E;

    //
    // MessageId: SCHED_E_ACCOUNT_INFORMATION_NOT_SET
    //
    // MessageText:
    //
    // No account information could be found in the Task Scheduler security
    // database for the task indicated.
    //
    int SCHED_E_ACCOUNT_INFORMATION_NOT_SET = 0x8004130F;

    //
    // MessageId: SCHED_E_ACCOUNT_NAME_NOT_FOUND
    //
    // MessageText:
    //
    // Unable to establish existence of the account specified.
    //
    int SCHED_E_ACCOUNT_NAME_NOT_FOUND = 0x80041310;

    //
    // MessageId: SCHED_E_ACCOUNT_DBASE_CORRUPT
    //
    // MessageText:
    //
    // Corruption was detected in the Task Scheduler security database; the
    // database has been reset.
    //
    int SCHED_E_ACCOUNT_DBASE_CORRUPT = 0x80041311;

    //
    // MessageId: SCHED_E_NO_SECURITY_SERVICES
    //
    // MessageText:
    //
    // Task Scheduler security services are available only on Windows NT.
    //
    int SCHED_E_NO_SECURITY_SERVICES = 0x80041312;

    //
    // MessageId: SCHED_E_UNKNOWN_OBJECT_VERSION
    //
    // MessageText:
    //
    // The task object version is either unsupported or invalid.
    //
    int SCHED_E_UNKNOWN_OBJECT_VERSION = 0x80041313;

    //
    // MessageId: SCHED_E_UNSUPPORTED_ACCOUNT_OPTION
    //
    // MessageText:
    //
    // The task has been configured with an unsupported combination of account
    // settings and run time options.
    //
    int SCHED_E_UNSUPPORTED_ACCOUNT_OPTION = 0x80041314;

    //
    // MessageId: SCHED_E_SERVICE_NOT_RUNNING
    //
    // MessageText:
    //
    // The Task Scheduler Service is not running.
    //
    int SCHED_E_SERVICE_NOT_RUNNING = 0x80041315;

    //
    // MessageId: SCHED_E_UNEXPECTEDNODE
    //
    // MessageText:
    //
    // The task XML contains an unexpected node.
    //
    int SCHED_E_UNEXPECTEDNODE = 0x80041316;

    //
    // MessageId: SCHED_E_NAMESPACE
    //
    // MessageText:
    //
    // The task XML contains an element or attribute from an unexpected
    // namespace.
    //
    int SCHED_E_NAMESPACE = 0x80041317;

    //
    // MessageId: SCHED_E_INVALIDVALUE
    //
    // MessageText:
    //
    // The task XML contains a value which is incorrectly formatted or out of
    // range.
    //
    int SCHED_E_INVALIDVALUE = 0x80041318;

    //
    // MessageId: SCHED_E_MISSINGNODE
    //
    // MessageText:
    //
    // The task XML is missing a required element or attribute.
    //
    int SCHED_E_MISSINGNODE = 0x80041319;

    //
    // MessageId: SCHED_E_MALFORMEDXML
    //
    // MessageText:
    //
    // The task XML is malformed.
    //
    int SCHED_E_MALFORMEDXML = 0x8004131A;

    //
    // MessageId: SCHED_S_SOME_TRIGGERS_FAILED
    //
    // MessageText:
    //
    // The task is registered, but not all specified triggers will start the
    // task.
    //
    int SCHED_S_SOME_TRIGGERS_FAILED = 0x0004131B;

    //
    // MessageId: SCHED_S_BATCH_LOGON_PROBLEM
    //
    // MessageText:
    //
    // The task is registered, but may fail to start. Batch logon privilege
    // needs to be enabled for the task principal.
    //
    int SCHED_S_BATCH_LOGON_PROBLEM = 0x0004131C;

    //
    // MessageId: SCHED_E_TOO_MANY_NODES
    //
    // MessageText:
    //
    // The task XML contains too many nodes of the same type.
    //
    int SCHED_E_TOO_MANY_NODES = 0x8004131D;

    //
    // MessageId: SCHED_E_PAST_END_BOUNDARY
    //
    // MessageText:
    //
    // The task cannot be started after the trigger's end boundary.
    //
    int SCHED_E_PAST_END_BOUNDARY = 0x8004131E;

    //
    // MessageId: SCHED_E_ALREADY_RUNNING
    //
    // MessageText:
    //
    // An instance of this task is already running.
    //
    int SCHED_E_ALREADY_RUNNING = 0x8004131F;

    //
    // MessageId: SCHED_E_USER_NOT_LOGGED_ON
    //
    // MessageText:
    //
    // The task will not run because the user is not logged on.
    //
    int SCHED_E_USER_NOT_LOGGED_ON = 0x80041320;

    //
    // MessageId: SCHED_E_INVALID_TASK_HASH
    //
    // MessageText:
    //
    // The task image is corrupt or has been tampered with.
    //
    int SCHED_E_INVALID_TASK_HASH = 0x80041321;

    //
    // MessageId: SCHED_E_SERVICE_NOT_AVAILABLE
    //
    // MessageText:
    //
    // The Task Scheduler service is not available.
    //
    int SCHED_E_SERVICE_NOT_AVAILABLE = 0x80041322;

    //
    // MessageId: SCHED_E_SERVICE_TOO_BUSY
    //
    // MessageText:
    //
    // The Task Scheduler service is too busy to handle your request. Please try
    // again later.
    //
    int SCHED_E_SERVICE_TOO_BUSY = 0x80041323;

    //
    // MessageId: SCHED_E_TASK_ATTEMPTED
    //
    // MessageText:
    //
    // The Task Scheduler service attempted to run the task, but the task did
    // not run due to one of the constraints in the task definition.
    //
    int SCHED_E_TASK_ATTEMPTED = 0x80041324;

    //
    // MessageId: SCHED_S_TASK_QUEUED
    //
    // MessageText:
    //
    // The Task Scheduler service has asked the task to run.
    //
    int SCHED_S_TASK_QUEUED = 0x00041325;

    //
    // MessageId: SCHED_E_TASK_DISABLED
    //
    // MessageText:
    //
    // The task is disabled.
    //
    int SCHED_E_TASK_DISABLED = 0x80041326;

    //
    // MessageId: SCHED_E_TASK_NOT_V1_COMPAT
    //
    // MessageText:
    //
    // The task has properties that are not compatible with previous versions of
    // Windows.
    //
    int SCHED_E_TASK_NOT_V1_COMPAT = 0x80041327;

    //
    // MessageId: SCHED_E_START_ON_DEMAND
    //
    // MessageText:
    //
    // The task settings do not allow the task to start on demand.
    //
    int SCHED_E_START_ON_DEMAND = 0x80041328;

    // ******************
    // FACILITY_WINDOWS
    // ******************
    //
    // Codes 0x0-0x01ff are reserved for the OLE group of
    // interfaces.
    //
    //
    // MessageId: CO_E_CLASS_CREATE_FAILED
    //
    // MessageText:
    //
    // Attempt to create a class object failed
    //
    int CO_E_CLASS_CREATE_FAILED = 0x80080001;

    //
    // MessageId: CO_E_SCM_ERROR
    //
    // MessageText:
    //
    // OLE service could not bind object
    //
    int CO_E_SCM_ERROR = 0x80080002;

    //
    // MessageId: CO_E_SCM_RPC_FAILURE
    //
    // MessageText:
    //
    // RPC communication failed with OLE service
    //
    int CO_E_SCM_RPC_FAILURE = 0x80080003;

    //
    // MessageId: CO_E_BAD_PATH
    //
    // MessageText:
    //
    // Bad path to object
    //
    int CO_E_BAD_PATH = 0x80080004;

    //
    // MessageId: CO_E_SERVER_EXEC_FAILURE
    //
    // MessageText:
    //
    // Server execution failed
    //
    int CO_E_SERVER_EXEC_FAILURE = 0x80080005;

    //
    // MessageId: CO_E_OBJSRV_RPC_FAILURE
    //
    // MessageText:
    //
    // OLE service could not communicate with the object server
    //
    int CO_E_OBJSRV_RPC_FAILURE = 0x80080006;

    //
    // MessageId: MK_E_NO_NORMALIZED
    //
    // MessageText:
    //
    // Moniker path could not be normalized
    //
    int MK_E_NO_NORMALIZED = 0x80080007;

    //
    // MessageId: CO_E_SERVER_STOPPING
    //
    // MessageText:
    //
    // Object server is stopping when OLE service contacts it
    //
    int CO_E_SERVER_STOPPING = 0x80080008;

    //
    // MessageId: MEM_E_INVALID_ROOT
    //
    // MessageText:
    //
    // An invalid root block pointer was specified
    //
    int MEM_E_INVALID_ROOT = 0x80080009;

    //
    // MessageId: MEM_E_INVALID_LINK
    //
    // MessageText:
    //
    // An allocation chain contained an invalid link pointer
    //
    int MEM_E_INVALID_LINK = 0x80080010;

    //
    // MessageId: MEM_E_INVALID_SIZE
    //
    // MessageText:
    //
    // The requested allocation size was too large
    //
    int MEM_E_INVALID_SIZE = 0x80080011;

    //
    // MessageId: CO_S_NOTALLINTERFACES
    //
    // MessageText:
    //
    // Not all the requested interfaces were available
    //
    int CO_S_NOTALLINTERFACES = 0x00080012;

    //
    // MessageId: CO_S_MACHINENAMENOTFOUND
    //
    // MessageText:
    //
    // The specified machine name was not found in the cache.
    //
    int CO_S_MACHINENAMENOTFOUND = 0x00080013;

    //
    // MessageId: CO_E_MISSING_DISPLAYNAME
    //
    // MessageText:
    //
    // The activation requires a display name to be present under the CLSID key.
    //
    int CO_E_MISSING_DISPLAYNAME = 0x80080015;

    //
    // MessageId: CO_E_RUNAS_VALUE_MUST_BE_AAA
    //
    // MessageText:
    //
    // The activation requires that the RunAs value for the application is
    // Activate As Activator.
    //
    int CO_E_RUNAS_VALUE_MUST_BE_AAA = 0x80080016;

    //
    // MessageId: CO_E_ELEVATION_DISABLED
    //
    // MessageText:
    //
    // The class is not configured to support Elevated activation.
    //
    int CO_E_ELEVATION_DISABLED = 0x80080017;

    // ******************
    // FACILITY_DISPATCH
    // ******************
    //
    // MessageId: DISP_E_UNKNOWNINTERFACE
    //
    // MessageText:
    //
    // Unknown interface.
    //
    int DISP_E_UNKNOWNINTERFACE = 0x80020001;

    //
    // MessageId: DISP_E_MEMBERNOTFOUND
    //
    // MessageText:
    //
    // Member not found.
    //
    int DISP_E_MEMBERNOTFOUND = 0x80020003;

    //
    // MessageId: DISP_E_PARAMNOTFOUND
    //
    // MessageText:
    //
    // Parameter not found.
    //
    int DISP_E_PARAMNOTFOUND = 0x80020004;

    //
    // MessageId: DISP_E_TYPEMISMATCH
    //
    // MessageText:
    //
    // Type mismatch.
    //
    int DISP_E_TYPEMISMATCH = 0x80020005;

    //
    // MessageId: DISP_E_UNKNOWNNAME
    //
    // MessageText:
    //
    // Unknown name.
    //
    int DISP_E_UNKNOWNNAME = 0x80020006;

    //
    // MessageId: DISP_E_NONAMEDARGS
    //
    // MessageText:
    //
    // No named arguments.
    //
    int DISP_E_NONAMEDARGS = 0x80020007;

    //
    // MessageId: DISP_E_BADVARTYPE
    //
    // MessageText:
    //
    // Bad variable type.
    //
    int DISP_E_BADVARTYPE = 0x80020008;

    //
    // MessageId: DISP_E_EXCEPTION
    //
    // MessageText:
    //
    // Exception occurred.
    //
    int DISP_E_EXCEPTION = 0x80020009;

    //
    // MessageId: DISP_E_OVERF;OW
    //
    // MessageText:
    //
    // Out of present range.
    //
    int DISP_E_OVERFLOW = 0x8002000A;

    //
    // MessageId: DISP_E_BADINDEX
    //
    // MessageText:
    //
    // Invalid index.
    //
    int DISP_E_BADINDEX = 0x8002000B;

    //
    // MessageId: DISP_E_UNKNOWNLCID
    //
    // MessageText:
    //
    // Unknown language.
    //
    int DISP_E_UNKNOWNLCID = 0x8002000C;

    //
    // MessageId: DISP_E_ARRAYISLOCKED
    //
    // MessageText:
    //
    // Memory is locked.
    //
    int DISP_E_ARRAYISLOCKED = 0x8002000D;

    //
    // MessageId: DISP_E_BADPARAMCOUNT
    //
    // MessageText:
    //
    // Invalid number of parameters.
    //
    int DISP_E_BADPARAMCOUNT = 0x8002000E;

    //
    // MessageId: DISP_E_PARAMNOTOPTIONAL
    //
    // MessageText:
    //
    // Parameter not optional.
    //
    int DISP_E_PARAMNOTOPTIONAL = 0x8002000F;

    //
    // MessageId: DISP_E_BADCALLEE
    //
    // MessageText:
    //
    // Invalid callee.
    //
    int DISP_E_BADCALLEE = 0x80020010;

    //
    // MessageId: DISP_E_NOTACOLLECTION
    //
    // MessageText:
    //
    // Does not support a collection.
    //
    int DISP_E_NOTACOLLECTION = 0x80020011;

    //
    // MessageId: DISP_E_DIVBYZERO
    //
    // MessageText:
    //
    // Division by zero.
    //
    int DISP_E_DIVBYZERO = 0x80020012;

    //
    // MessageId: DISP_E_BUFFERTOOSMALL
    //
    // MessageText:
    //
    // Buffer too small
    //
    int DISP_E_BUFFERTOOSMALL = 0x80020013;

    //
    // MessageId: TYPE_E_BUFFERTOOSMALL
    //
    // MessageText:
    //
    // Buffer too small.
    //
    int TYPE_E_BUFFERTOOSMALL = 0x80028016;

    //
    // MessageId: TYPE_E_FIELDNOTFOUND
    //
    // MessageText:
    //
    // Field name not defined in the record.
    //
    int TYPE_E_FIELDNOTFOUND = 0x80028017;

    //
    // MessageId: TYPE_E_INVDATAREAD
    //
    // MessageText:
    //
    // Old format or invalid type library.
    //
    int TYPE_E_INVDATAREAD = 0x80028018;

    //
    // MessageId: TYPE_E_UNSUPFORMAT
    //
    // MessageText:
    //
    // Old format or invalid type library.
    //
    int TYPE_E_UNSUPFORMAT = 0x80028019;

    //
    // MessageId: TYPE_E_REGISTRYACCESS
    //
    // MessageText:
    //
    // Error accessing the OLE registry.
    //
    int TYPE_E_REGISTRYACCESS = 0x8002801C;

    //
    // MessageId: TYPE_E_LIBNOTREGISTERED
    //
    // MessageText:
    //
    // Library not registered.
    //
    int TYPE_E_LIBNOTREGISTERED = 0x8002801D;

    //
    // MessageId: TYPE_E_UNDEFINEDTYPE
    //
    // MessageText:
    //
    // Bound to unknown type.
    //
    int TYPE_E_UNDEFINEDTYPE = 0x80028027;

    //
    // MessageId: TYPE_E_QUALIFIEDNAMEDISALLOWED
    //
    // MessageText:
    //
    // Qualified name disallowed.
    //
    int TYPE_E_QUALIFIEDNAMEDISALLOWED = 0x80028028;

    //
    // MessageId: TYPE_E_INVALIDSTATE
    //
    // MessageText:
    //
    // Invalid forward reference, or reference to uncompiled type.
    //
    int TYPE_E_INVALIDSTATE = 0x80028029;

    //
    // MessageId: TYPE_E_WRONGTYPEKIND
    //
    // MessageText:
    //
    // Type mismatch.
    //
    int TYPE_E_WRONGTYPEKIND = 0x8002802A;

    //
    // MessageId: TYPE_E_ELEMENTNOTFOUND
    //
    // MessageText:
    //
    // Element not found.
    //
    int TYPE_E_ELEMENTNOTFOUND = 0x8002802B;

    //
    // MessageId: TYPE_E_AMBIGUOUSNAME
    //
    // MessageText:
    //
    // Ambiguous name.
    //
    int TYPE_E_AMBIGUOUSNAME = 0x8002802C;

    //
    // MessageId: TYPE_E_NAMECONF;ICT
    //
    // MessageText:
    //
    // Name already exists in the library.
    //
    int TYPE_E_NAMECONFLICT = 0x8002802D;

    //
    // MessageId: TYPE_E_UNKNOWNLCID
    //
    // MessageText:
    //
    // Unknown LCID.
    //
    int TYPE_E_UNKNOWNLCID = 0x8002802E;

    //
    // MessageId: TYPE_E_DLLFUNCTIONNOTFOUND
    //
    // MessageText:
    //
    // Function not defined in specified DLL.
    //
    int TYPE_E_DLLFUNCTIONNOTFOUND = 0x8002802F;

    //
    // MessageId: TYPE_E_BADMODULEKIND
    //
    // MessageText:
    //
    // Wrong module kind for the operation.
    //
    int TYPE_E_BADMODULEKIND = 0x800288BD;

    //
    // MessageId: TYPE_E_SIZETOOBIG
    //
    // MessageText:
    //
    // Size may not exceed=64K.
    //
    int TYPE_E_SIZETOOBIG = 0x800288C5;

    //
    // MessageId: TYPE_E_DUPLICATEID
    //
    // MessageText:
    //
    // Duplicate ID in inheritance hierarchy.
    //
    int TYPE_E_DUPLICATEID = 0x800288C6;

    //
    // MessageId: TYPE_E_INVALIDID
    //
    // MessageText:
    //
    // Incorrect inheritance depth in standard OLE hmember.
    //
    int TYPE_E_INVALIDID = 0x800288CF;

    //
    // MessageId: TYPE_E_TYPEMISMATCH
    //
    // MessageText:
    //
    // Type mismatch.
    //
    int TYPE_E_TYPEMISMATCH = 0x80028CA0;

    //
    // MessageId: TYPE_E_OUTOFBOUNDS
    //
    // MessageText:
    //
    // Invalid number of arguments.
    //
    int TYPE_E_OUTOFBOUNDS = 0x80028CA1;

    //
    // MessageId: TYPE_E_IOERROR
    //
    // MessageText:
    //
    // I/O Error.
    //
    int TYPE_E_IOERROR = 0x80028CA2;

    //
    // MessageId: TYPE_E_CANTCREATETMPFILE
    //
    // MessageText:
    //
    // Error creating unique tmp file.
    //
    int TYPE_E_CANTCREATETMPFILE = 0x80028CA3;

    //
    // MessageId: TYPE_E_CANTLOADLIBRARY
    //
    // MessageText:
    //
    // Error loading type library/DLL.
    //
    int TYPE_E_CANTLOADLIBRARY = 0x80029C4A;

    //
    // MessageId: TYPE_E_INCONSISTENTPROPFUNCS
    //
    // MessageText:
    //
    // Inconsistent property functions.
    //
    int TYPE_E_INCONSISTENTPROPFUNCS = 0x80029C83;

    //
    // MessageId: TYPE_E_CIRCULARTYPE
    //
    // MessageText:
    //
    // Circular dependency between types/modules.
    //
    int TYPE_E_CIRCULARTYPE = 0x80029C84;

    // ******************
    // FACILITY_STORAGE
    // ******************
    //
    // MessageId: STG_E_INVALIDFUNCTION
    //
    // MessageText:
    //
    // Unable to perform requested operation.
    //
    int STG_E_INVALIDFUNCTION = 0x80030001;

    //
    // MessageId: STG_E_FILENOTFOUND
    //
    // MessageText:
    //
    // %1 could not be found.
    //
    int STG_E_FILENOTFOUND = 0x80030002;

    //
    // MessageId: STG_E_PATHNOTFOUND
    //
    // MessageText:
    //
    // The path %1 could not be found.
    //
    int STG_E_PATHNOTFOUND = 0x80030003;

    //
    // MessageId: STG_E_TOOMANYOPENFILES
    //
    // MessageText:
    //
    // There are insufficient resources to open another file.
    //
    int STG_E_TOOMANYOPENFILES = 0x80030004;

    //
    // MessageId: STG_E_ACCESSDENIED
    //
    // MessageText:
    //
    // Access Denied.
    //
    int STG_E_ACCESSDENIED = 0x80030005;

    //
    // MessageId: STG_E_INVALIDHANDLE
    //
    // MessageText:
    //
    // Attempted an operation on an invalid object.
    //
    int STG_E_INVALIDHANDLE = 0x80030006;

    //
    // MessageId: STG_E_INSUFFICIENTMEMORY
    //
    // MessageText:
    //
    // There is insufficient memory available to complete operation.
    //
    int STG_E_INSUFFICIENTMEMORY = 0x80030008;

    //
    // MessageId: STG_E_INVALIDPOINTER
    //
    // MessageText:
    //
    // Invalid pointer error.
    //
    int STG_E_INVALIDPOINTER = 0x80030009;

    //
    // MessageId: STG_E_NOMOREFILES
    //
    // MessageText:
    //
    // There are no more entries to return.
    //
    int STG_E_NOMOREFILES = 0x80030012;

    //
    // MessageId: STG_E_DISKISWRITEPROTECTED
    //
    // MessageText:
    //
    // Disk is write-protected.
    //
    int STG_E_DISKISWRITEPROTECTED = 0x80030013;

    //
    // MessageId: STG_E_SEEKERROR
    //
    // MessageText:
    //
    // An error occurred during a seek operation.
    //
    int STG_E_SEEKERROR = 0x80030019;

    //
    // MessageId: STG_E_WRITEFAULT
    //
    // MessageText:
    //
    // A disk error occurred during a write operation.
    //
    int STG_E_WRITEFAULT = 0x8003001D;

    //
    // MessageId: STG_E_READFAULT
    //
    // MessageText:
    //
    // A disk error occurred during a read operation.
    //
    int STG_E_READFAULT = 0x8003001E;

    //
    // MessageId: STG_E_SHAREVIOLATION
    //
    // MessageText:
    //
    // A share violation has occurred.
    //
    int STG_E_SHAREVIOLATION = 0x80030020;

    //
    // MessageId: STG_E_LOCKVIOLATION
    //
    // MessageText:
    //
    // A lock violation has occurred.
    //
    int STG_E_LOCKVIOLATION = 0x80030021;

    //
    // MessageId: STG_E_FILEALREADYEXISTS
    //
    // MessageText:
    //
    // %1 already exists.
    //
    int STG_E_FILEALREADYEXISTS = 0x80030050;

    //
    // MessageId: STG_E_INVALIDPARAMETER
    //
    // MessageText:
    //
    // Invalid parameter error.
    //
    int STG_E_INVALIDPARAMETER = 0x80030057;

    //
    // MessageId: STG_E_MEDIUMFULL
    //
    // MessageText:
    //
    // There is insufficient disk space to complete operation.
    //
    int STG_E_MEDIUMFULL = 0x80030070;

    //
    // MessageId: STG_E_PROPSETMISMATCHED
    //
    // MessageText:
    //
    // Illegal write of non-simple property to simple property set.
    //
    int STG_E_PROPSETMISMATCHED = 0x800300F0;

    //
    // MessageId: STG_E_ABNORMALAPIEXIT
    //
    // MessageText:
    //
    // An API call exited abnormally.
    //
    int STG_E_ABNORMALAPIEXIT = 0x800300FA;

    //
    // MessageId: STG_E_INVALIDHEADER
    //
    // MessageText:
    //
    // The file %1 is not a valid compound file.
    //
    int STG_E_INVALIDHEADER = 0x800300FB;

    //
    // MessageId: STG_E_INVALIDNAME
    //
    // MessageText:
    //
    // The name %1 is not valid.
    //
    int STG_E_INVALIDNAME = 0x800300FC;

    //
    // MessageId: STG_E_UNKNOWN
    //
    // MessageText:
    //
    // An unexpected error occurred.
    //
    int STG_E_UNKNOWN = 0x800300FD;

    //
    // MessageId: STG_E_UNIMPLEMENTEDFUNCTION
    //
    // MessageText:
    //
    // That function is not implemented.
    //
    int STG_E_UNIMPLEMENTEDFUNCTION = 0x800300FE;

    //
    // MessageId: STG_E_INVALIDF;AG
    //
    // MessageText:
    //
    // Invalid F;ag error.
    //
    int STG_E_INVALIDFLAG = 0x800300FF;

    //
    // MessageId: STG_E_INUSE
    //
    // MessageText:
    //
    // Attempted to use an object that is busy.
    //
    int STG_E_INUSE = 0x80030100;

    //
    // MessageId: STG_E_NOTCURRENT
    //
    // MessageText:
    //
    // The storage has been changed since the last commit.
    //
    int STG_E_NOTCURRENT = 0x80030101;

    //
    // MessageId: STG_E_REVERTED
    //
    // MessageText:
    //
    // Attempted to use an object that has ceased to exist.
    //
    int STG_E_REVERTED = 0x80030102;

    //
    // MessageId: STG_E_CANTSAVE
    //
    // MessageText:
    //
    // Can't save.
    //
    int STG_E_CANTSAVE = 0x80030103;

    //
    // MessageId: STG_E_OLDFORMAT
    //
    // MessageText:
    //
    // The compound file %1 was produced with an incompatible version of
    // storage.
    //
    int STG_E_OLDFORMAT = 0x80030104;

    //
    // MessageId: STG_E_OLDDLL
    //
    // MessageText:
    //
    // The compound file %1 was produced with a newer version of storage.
    //
    int STG_E_OLDDLL = 0x80030105;

    //
    // MessageId: STG_E_SHAREREQUIRED
    //
    // MessageText:
    //
    // Share.exe or equivalent is required for operation.
    //
    int STG_E_SHAREREQUIRED = 0x80030106;

    //
    // MessageId: STG_E_NOTFILEBASEDSTORAGE
    //
    // MessageText:
    //
    // Illegal operation called on non-file based storage.
    //
    int STG_E_NOTFILEBASEDSTORAGE = 0x80030107;

    //
    // MessageId: STG_E_EXTANTMARSHALLINGS
    //
    // MessageText:
    //
    // Illegal operation called on object with extant marshallings.
    //
    int STG_E_EXTANTMARSHALLINGS = 0x80030108;

    //
    // MessageId: STG_E_DOCFILECORRUPT
    //
    // MessageText:
    //
    // The docfile has been corrupted.
    //
    int STG_E_DOCFILECORRUPT = 0x80030109;

    //
    // MessageId: STG_E_BADBASEADDRESS
    //
    // MessageText:
    //
    // OLE32.DLL has been loaded at the wrong address.
    //
    int STG_E_BADBASEADDRESS = 0x80030110;

    //
    // MessageId: STG_E_DOCFILETOOLARGE
    //
    // MessageText:
    //
    // The compound file is too large for the current implementation
    //
    int STG_E_DOCFILETOOLARGE = 0x80030111;

    //
    // MessageId: STG_E_NOTSIMPLEFORMAT
    //
    // MessageText:
    //
    // The compound file was not created with the STGM_SIMPLE F;ag
    //
    int STG_E_NOTSIMPLEFORMAT = 0x80030112;

    //
    // MessageId: STG_E_INCOMPLETE
    //
    // MessageText:
    //
    // The file download was aborted abnormally. The file is incomplete.
    //
    int STG_E_INCOMPLETE = 0x80030201;

    //
    // MessageId: STG_E_TERMINATED
    //
    // MessageText:
    //
    // The file download has been terminated.
    //
    int STG_E_TERMINATED = 0x80030202;

    //
    // MessageId: STG_S_CONVERTED
    //
    // MessageText:
    //
    // The underlying file was converted to compound file format.
    //
    int STG_S_CONVERTED = 0x00030200;

    //
    // MessageId: STG_S_BLOCK
    //
    // MessageText:
    //
    // The storage operation should block until more data is available.
    //
    int STG_S_BLOCK = 0x00030201;

    //
    // MessageId: STG_S_RETRYNOW
    //
    // MessageText:
    //
    // The storage operation should retry immediately.
    //
    int STG_S_RETRYNOW = 0x00030202;

    //
    // MessageId: STG_S_MONITORING
    //
    // MessageText:
    //
    // The notified event sink will not inF;uence the storage operation.
    //
    int STG_S_MONITORING = 0x00030203;

    //
    // MessageId: STG_S_MULTIPLEOPENS
    //
    // MessageText:
    //
    // Multiple opens prevent consolidated. (commit succeeded).
    //
    int STG_S_MULTIPLEOPENS = 0x00030204;

    //
    // MessageId: STG_S_CONSOLIDATIONFAILED
    //
    // MessageText:
    //
    // Consolidation of the storage file failed. (commit succeeded).
    //
    int STG_S_CONSOLIDATIONFAILED = 0x00030205;

    //
    // MessageId: STG_S_CANNOTCONSOLIDATE
    //
    // MessageText:
    //
    // Consolidation of the storage file is inappropriate. (commit succeeded).
    //
    int STG_S_CANNOTCONSOLIDATE = 0x00030206;

    /*
     * ++
     * 
     * MessageId's 0x0305 - 0x031f (inclusive) are reserved for **STORAGE** copy
     * protection errors.
     * 
     * --
     */
    //
    // MessageId: STG_E_STATUS_COPY_PROTECTION_FAILURE
    //
    // MessageText:
    //
    // Generic Copy Protection Error.
    //
    int STG_E_STATUS_COPY_PROTECTION_FAILURE = 0x80030305;

    //
    // MessageId: STG_E_CSS_AUTHENTICATION_FAILURE
    //
    // MessageText:
    //
    // Copy Protection Error - DVD CSS Authentication failed.
    //
    int STG_E_CSS_AUTHENTICATION_FAILURE = 0x80030306;

    //
    // MessageId: STG_E_CSS_KEY_NOT_PRESENT
    //
    // MessageText:
    //
    // Copy Protection Error - The given sector does not have a valid CSS key.
    //
    int STG_E_CSS_KEY_NOT_PRESENT = 0x80030307;

    //
    // MessageId: STG_E_CSS_KEY_NOT_ESTABLISHED
    //
    // MessageText:
    //
    // Copy Protection Error - DVD session key not established.
    //
    int STG_E_CSS_KEY_NOT_ESTABLISHED = 0x80030308;

    //
    // MessageId: STG_E_CSS_SCRAMBLED_SECTOR
    //
    // MessageText:
    //
    // Copy Protection Error - The read failed because the sector is encrypted.
    //
    int STG_E_CSS_SCRAMBLED_SECTOR = 0x80030309;

    //
    // MessageId: STG_E_CSS_REGION_MISMATCH
    //
    // MessageText:
    //
    // Copy Protection Error - The current DVD's region does not correspond to
    // the region setting of the drive.
    //
    int STG_E_CSS_REGION_MISMATCH = 0x8003030A;

    //
    // MessageId: STG_E_RESETS_EXHAUSTED
    //
    // MessageText:
    //
    // Copy Protection Error - The drive's region setting may be permanent or
    // the number of user resets has been exhausted.
    //
    int STG_E_RESETS_EXHAUSTED = 0x8003030B;

    /*
     * ++
     * 
     * MessageId's 0x0305 - 0x031f (inclusive) are reserved for **STORAGE** copy
     * protection errors.
     * 
     * --
     */
    // ******************
    // FACILITY_RPC
    // ******************
    //
    // Codes 0x0-0x11 are propagated from=16 bit OLE.
    //
    //
    // MessageId: RPC_E_CALL_REJECTED
    //
    // MessageText:
    //
    // Call was rejected by callee.
    //
    int RPC_E_CALL_REJECTED = 0x80010001;

    //
    // MessageId: RPC_E_CALL_CANCELED
    //
    // MessageText:
    //
    // Call was canceled by the message filter.
    //
    int RPC_E_CALL_CANCELED = 0x80010002;

    //
    // MessageId: RPC_E_CANTPOST_INSENDCALL
    //
    // MessageText:
    //
    // The caller is dispatching an intertask SendMessage call and cannot call
    // out via PostMessage.
    //
    int RPC_E_CANTPOST_INSENDCALL = 0x80010003;

    //
    // MessageId: RPC_E_CANTCALLOUT_INASYNCCALL
    //
    // MessageText:
    //
    // The caller is dispatching an asynchronous call and cannot make an
    // outgoing call on behalf of this call.
    //
    int RPC_E_CANTCALLOUT_INASYNCCALL = 0x80010004;

    //
    // MessageId: RPC_E_CANTCALLOUT_INEXTERNALCALL
    //
    // MessageText:
    //
    // It is illegal to call out while inside message filter.
    //
    int RPC_E_CANTCALLOUT_INEXTERNALCALL = 0x80010005;

    //
    // MessageId: RPC_E_CONNECTION_TERMINATED
    //
    // MessageText:
    //
    // The connection terminated or is in a bogus state and cannot be used any
    // more. Other connections are still valid.
    //
    int RPC_E_CONNECTION_TERMINATED = 0x80010006;

    //
    // MessageId: RPC_E_SERVER_DIED
    //
    // MessageText:
    //
    // The callee (server [not server application]) is not available and
    // disappeared; all connections are invalid. The call may have executed.
    //
    int RPC_E_SERVER_DIED = 0x80010007;

    //
    // MessageId: RPC_E_CLIENT_DIED
    //
    // MessageText:
    //
    // The caller (client) disappeared while the callee (server) was processing
    // a call.
    //
    int RPC_E_CLIENT_DIED = 0x80010008;

    //
    // MessageId: RPC_E_INVALID_DATAPACKET
    //
    // MessageText:
    //
    // The data packet with the marshalled parameter data is incorrect.
    //
    int RPC_E_INVALID_DATAPACKET = 0x80010009;

    //
    // MessageId: RPC_E_CANTTRANSMIT_CALL
    //
    // MessageText:
    //
    // The call was not transmitted properly; the message queue was full and was
    // not emptied after yielding.
    //
    int RPC_E_CANTTRANSMIT_CALL = 0x8001000A;

    //
    // MessageId: RPC_E_CLIENT_CANTMARSHAL_DATA
    //
    // MessageText:
    //
    // The client (caller) cannot marshall the parameter data - low memory, etc.
    //
    int RPC_E_CLIENT_CANTMARSHAL_DATA = 0x8001000B;

    //
    // MessageId: RPC_E_CLIENT_CANTUNMARSHAL_DATA
    //
    // MessageText:
    //
    // The client (caller) cannot unmarshall the return data - low memory, etc.
    //
    int RPC_E_CLIENT_CANTUNMARSHAL_DATA = 0x8001000C;

    //
    // MessageId: RPC_E_SERVER_CANTMARSHAL_DATA
    //
    // MessageText:
    //
    // The server (callee) cannot marshall the return data - low memory, etc.
    //
    int RPC_E_SERVER_CANTMARSHAL_DATA = 0x8001000D;

    //
    // MessageId: RPC_E_SERVER_CANTUNMARSHAL_DATA
    //
    // MessageText:
    //
    // The server (callee) cannot unmarshall the parameter data - low memory,
    // etc.
    //
    int RPC_E_SERVER_CANTUNMARSHAL_DATA = 0x8001000E;

    //
    // MessageId: RPC_E_INVALID_DATA
    //
    // MessageText:
    //
    // Received data is invalid; could be server or client data.
    //
    int RPC_E_INVALID_DATA = 0x8001000F;

    //
    // MessageId: RPC_E_INVALID_PARAMETER
    //
    // MessageText:
    //
    // A particular parameter is invalid and cannot be (un)marshalled.
    //
    int RPC_E_INVALID_PARAMETER = 0x80010010;

    //
    // MessageId: RPC_E_CANTCALLOUT_AGAIN
    //
    // MessageText:
    //
    // There is no second outgoing call on same channel in DDE conversation.
    //
    int RPC_E_CANTCALLOUT_AGAIN = 0x80010011;

    //
    // MessageId: RPC_E_SERVER_DIED_DNE
    //
    // MessageText:
    //
    // The callee (server [not server application]) is not available and
    // disappeared; all connections are invalid. The call did not execute.
    //
    int RPC_E_SERVER_DIED_DNE = 0x80010012;

    //
    // MessageId: RPC_E_SYS_CALL_FAILED
    //
    // MessageText:
    //
    // System call failed.
    //
    int RPC_E_SYS_CALL_FAILED = 0x80010100;

    //
    // MessageId: RPC_E_OUT_OF_RESOURCES
    //
    // MessageText:
    //
    // Could not allocate some required resource (memory, events, ...)
    //
    int RPC_E_OUT_OF_RESOURCES = 0x80010101;

    //
    // MessageId: RPC_E_ATTEMPTED_MULTITHREAD
    //
    // MessageText:
    //
    // Attempted to make calls on more than one thread in single threaded mode.
    //
    int RPC_E_ATTEMPTED_MULTITHREAD = 0x80010102;

    //
    // MessageId: RPC_E_NOT_REGISTERED
    //
    // MessageText:
    //
    // The requested interface is not registered on the server object.
    //
    int RPC_E_NOT_REGISTERED = 0x80010103;

    //
    // MessageId: RPC_E_FAULT
    //
    // MessageText:
    //
    // RPC could not call the server or could not return the results of calling
    // the server.
    //
    int RPC_E_FAULT = 0x80010104;

    //
    // MessageId: RPC_E_SERVERFAULT
    //
    // MessageText:
    //
    // The server threw an exception.
    //
    int RPC_E_SERVERFAULT = 0x80010105;

    //
    // MessageId: RPC_E_CHANGED_MODE
    //
    // MessageText:
    //
    // Cannot change thread mode after it is set.
    //
    int RPC_E_CHANGED_MODE = 0x80010106;

    //
    // MessageId: RPC_E_INVALIDMETHOD
    //
    // MessageText:
    //
    // The method called does not exist on the server.
    //
    int RPC_E_INVALIDMETHOD = 0x80010107;

    //
    // MessageId: RPC_E_DISCONNECTED
    //
    // MessageText:
    //
    // The object invoked has disconnected from its clients.
    //
    int RPC_E_DISCONNECTED = 0x80010108;

    //
    // MessageId: RPC_E_RETRY
    //
    // MessageText:
    //
    // The object invoked chose not to process the call now. Try again later.
    //
    int RPC_E_RETRY = 0x80010109;

    //
    // MessageId: RPC_E_SERVERCALL_RETRYLATER
    //
    // MessageText:
    //
    // The message filter indicated that the application is busy.
    //
    int RPC_E_SERVERCALL_RETRYLATER = 0x8001010A;

    //
    // MessageId: RPC_E_SERVERCALL_REJECTED
    //
    // MessageText:
    //
    // The message filter rejected the call.
    //
    int RPC_E_SERVERCALL_REJECTED = 0x8001010B;

    //
    // MessageId: RPC_E_INVALID_CALLDATA
    //
    // MessageText:
    //
    // A call control interfaces was called with invalid data.
    //
    int RPC_E_INVALID_CALLDATA = 0x8001010C;

    //
    // MessageId: RPC_E_CANTCALLOUT_ININPUTSYNCCALL
    //
    // MessageText:
    //
    // An outgoing call cannot be made since the application is dispatching an
    // input-synchronous call.
    //
    int RPC_E_CANTCALLOUT_ININPUTSYNCCALL = 0x8001010D;

    //
    // MessageId: RPC_E_WRONG_THREAD
    //
    // MessageText:
    //
    // The application called an interface that was marshalled for a different
    // thread.
    //
    int RPC_E_WRONG_THREAD = 0x8001010E;

    //
    // MessageId: RPC_E_THREAD_NOT_INIT
    //
    // MessageText:
    //
    // CoInitialize has not been called on the current thread.
    //
    int RPC_E_THREAD_NOT_INIT = 0x8001010F;

    //
    // MessageId: RPC_E_VERSION_MISMATCH
    //
    // MessageText:
    //
    // The version of OLE on the client and server machines does not match.
    //
    int RPC_E_VERSION_MISMATCH = 0x80010110;

    //
    // MessageId: RPC_E_INVALID_HEADER
    //
    // MessageText:
    //
    // OLE received a packet with an invalid header.
    //
    int RPC_E_INVALID_HEADER = 0x80010111;

    //
    // MessageId: RPC_E_INVALID_EXTENSION
    //
    // MessageText:
    //
    // OLE received a packet with an invalid extension.
    //
    int RPC_E_INVALID_EXTENSION = 0x80010112;

    //
    // MessageId: RPC_E_INVALID_IPID
    //
    // MessageText:
    //
    // The requested object or interface does not exist.
    //
    int RPC_E_INVALID_IPID = 0x80010113;

    //
    // MessageId: RPC_E_INVALID_OBJECT
    //
    // MessageText:
    //
    // The requested object does not exist.
    //
    int RPC_E_INVALID_OBJECT = 0x80010114;

    //
    // MessageId: RPC_S_CALLPENDING
    //
    // MessageText:
    //
    // OLE has sent a request and is waiting for a reply.
    //
    int RPC_S_CALLPENDING = 0x80010115;

    //
    // MessageId: RPC_S_WAITONTIMER
    //
    // MessageText:
    //
    // OLE is waiting before retrying a request.
    //
    int RPC_S_WAITONTIMER = 0x80010116;

    //
    // MessageId: RPC_E_CALL_COMPLETE
    //
    // MessageText:
    //
    // Call context cannot be accessed after call completed.
    //
    int RPC_E_CALL_COMPLETE = 0x80010117;

    //
    // MessageId: RPC_E_UNSECURE_CALL
    //
    // MessageText:
    //
    // Impersonate on unsecure calls is not supported.
    //
    int RPC_E_UNSECURE_CALL = 0x80010118;

    //
    // MessageId: RPC_E_TOO_LATE
    //
    // MessageText:
    //
    // Security must be initialized before any interfaces are marshalled or
    // unmarshalled. It cannot be changed once initialized.
    //
    int RPC_E_TOO_LATE = 0x80010119;

    //
    // MessageId: RPC_E_NO_GOOD_SECURITY_PACKAGES
    //
    // MessageText:
    //
    // No security packages are installed on this machine or the user is not
    // logged on or there are no compatible security packages between the client
    // and server.
    //
    int RPC_E_NO_GOOD_SECURITY_PACKAGES = 0x8001011A;

    //
    // MessageId: RPC_E_ACCESS_DENIED
    //
    // MessageText:
    //
    // Access is denied.
    //
    int RPC_E_ACCESS_DENIED = 0x8001011B;

    //
    // MessageId: RPC_E_REMOTE_DISABLED
    //
    // MessageText:
    //
    // Remote calls are not allowed for this process.
    //
    int RPC_E_REMOTE_DISABLED = 0x8001011C;

    //
    // MessageId: RPC_E_INVALID_OBJREF
    //
    // MessageText:
    //
    // The marshaled interface data packet (OBJREF) has an invalid or unknown
    // format.
    //
    int RPC_E_INVALID_OBJREF = 0x8001011D;

    //
    // MessageId: RPC_E_NO_CONTEXT
    //
    // MessageText:
    //
    // No context is associated with this call. This happens for some custom
    // marshalled calls and on the client side of the call.
    //
    int RPC_E_NO_CONTEXT = 0x8001011E;

    //
    // MessageId: RPC_E_TIMEOUT
    //
    // MessageText:
    //
    // This operation returned because the timeout period expired.
    //
    int RPC_E_TIMEOUT = 0x8001011F;

    //
    // MessageId: RPC_E_NO_SYNC
    //
    // MessageText:
    //
    // There are no synchronize objects to wait on.
    //
    int RPC_E_NO_SYNC = 0x80010120;

    //
    // MessageId: RPC_E_FULLSIC_REQUIRED
    //
    // MessageText:
    //
    // Full subject issuer chain SSL principal name expected from the server.
    //
    int RPC_E_FULLSIC_REQUIRED = 0x80010121;

    //
    // MessageId: RPC_E_INVALID_STD_NAME
    //
    // MessageText:
    //
    // Principal name is not a valid MSSTD name.
    //
    int RPC_E_INVALID_STD_NAME = 0x80010122;

    //
    // MessageId: CO_E_FAILEDTOIMPERSONATE
    //
    // MessageText:
    //
    // Unable to impersonate DCOM client
    //
    int CO_E_FAILEDTOIMPERSONATE = 0x80010123;

    //
    // MessageId: CO_E_FAILEDTOGETSECCTX
    //
    // MessageText:
    //
    // Unable to obtain server's security context
    //
    int CO_E_FAILEDTOGETSECCTX = 0x80010124;

    //
    // MessageId: CO_E_FAILEDTOOPENTHREADTOKEN
    //
    // MessageText:
    //
    // Unable to open the access token of the current thread
    //
    int CO_E_FAILEDTOOPENTHREADTOKEN = 0x80010125;

    //
    // MessageId: CO_E_FAILEDTOGETTOKENINFO
    //
    // MessageText:
    //
    // Unable to obtain user info from an access token
    //
    int CO_E_FAILEDTOGETTOKENINFO = 0x80010126;

    //
    // MessageId: CO_E_TRUSTEEDOESNTMATCHCLIENT
    //
    // MessageText:
    //
    // The client who called IAccessControl::IsAccessPermitted was not the
    // trustee provided to the method
    //
    int CO_E_TRUSTEEDOESNTMATCHCLIENT = 0x80010127;

    //
    // MessageId: CO_E_FAILEDTOQUERYCLIENTBLANKET
    //
    // MessageText:
    //
    // Unable to obtain the client's security blanket
    //
    int CO_E_FAILEDTOQUERYCLIENTBLANKET = 0x80010128;

    //
    // MessageId: CO_E_FAILEDTOSETDACL
    //
    // MessageText:
    //
    // Unable to set a discretionary ACL into a security descriptor
    //
    int CO_E_FAILEDTOSETDACL = 0x80010129;

    //
    // MessageId: CO_E_ACCESSCHECKFAILED
    //
    // MessageText:
    //
    // The system function, AccessCheck, returned false
    //
    int CO_E_ACCESSCHECKFAILED = 0x8001012A;

    //
    // MessageId: CO_E_NETACCESSAPIFAILED
    //
    // MessageText:
    //
    // Either NetAccessDel or NetAccessAdd returned an error code.
    //
    int CO_E_NETACCESSAPIFAILED = 0x8001012B;

    //
    // MessageId: CO_E_WRONGTRUSTEENAMESYNTAX
    //
    // MessageText:
    //
    // One of the trustee strings provided by the user did not conform to the
    // <Domain>\<Name> syntax and it was not the "*" string
    //
    int CO_E_WRONGTRUSTEENAMESYNTAX = 0x8001012C;

    //
    // MessageId: CO_E_INVALIDSID
    //
    // MessageText:
    //
    // One of the security identifiers provided by the user was invalid
    //
    int CO_E_INVALIDSID = 0x8001012D;

    //
    // MessageId: CO_E_CONVERSIONFAILED
    //
    // MessageText:
    //
    // Unable to convert a wide character trustee string to a multibyte trustee
    // string
    //
    int CO_E_CONVERSIONFAILED = 0x8001012E;

    //
    // MessageId: CO_E_NOMATCHINGSIDFOUND
    //
    // MessageText:
    //
    // Unable to find a security identifier that corresponds to a trustee string
    // provided by the user
    //
    int CO_E_NOMATCHINGSIDFOUND = 0x8001012F;

    //
    // MessageId: CO_E_LOOKUPACCSIDFAILED
    //
    // MessageText:
    //
    // The system function, LookupAccountSID, failed
    //
    int CO_E_LOOKUPACCSIDFAILED = 0x80010130;

    //
    // MessageId: CO_E_NOMATCHINGNAMEFOUND
    //
    // MessageText:
    //
    // Unable to find a trustee name that corresponds to a security identifier
    // provided by the user
    //
    int CO_E_NOMATCHINGNAMEFOUND = 0x80010131;

    //
    // MessageId: CO_E_LOOKUPACCNAMEFAILED
    //
    // MessageText:
    //
    // The system function, LookupAccountName, failed
    //
    int CO_E_LOOKUPACCNAMEFAILED = 0x80010132;

    //
    // MessageId: CO_E_SETSERLHNDLFAILED
    //
    // MessageText:
    //
    // Unable to set or reset a serialization handle
    //
    int CO_E_SETSERLHNDLFAILED = 0x80010133;

    //
    // MessageId: CO_E_FAILEDTOGETWINDIR
    //
    // MessageText:
    //
    // Unable to obtain the Windows directory
    //
    int CO_E_FAILEDTOGETWINDIR = 0x80010134;

    //
    // MessageId: CO_E_PATHTOOLONG
    //
    // MessageText:
    //
    // Path too long
    //
    int CO_E_PATHTOOLONG = 0x80010135;

    //
    // MessageId: CO_E_FAILEDTOGENUUID
    //
    // MessageText:
    //
    // Unable to generate a uuid.
    //
    int CO_E_FAILEDTOGENUUID = 0x80010136;

    //
    // MessageId: CO_E_FAILEDTOCREATEFILE
    //
    // MessageText:
    //
    // Unable to create file
    //
    int CO_E_FAILEDTOCREATEFILE = 0x80010137;

    //
    // MessageId: CO_E_FAILEDTOCLOSEHANDLE
    //
    // MessageText:
    //
    // Unable to close a serialization handle or a file handle.
    //
    int CO_E_FAILEDTOCLOSEHANDLE = 0x80010138;

    //
    // MessageId: CO_E_EXCEEDSYSACLLIMIT
    //
    // MessageText:
    //
    // The number of ACEs in an ACL exceeds the system limit.
    //
    int CO_E_EXCEEDSYSACLLIMIT = 0x80010139;

    //
    // MessageId: CO_E_ACESINWRONGORDER
    //
    // MessageText:
    //
    // Not all the DENY_ACCESS ACEs are arranged in front of the GRANT_ACCESS
    // ACEs in the stream.
    //
    int CO_E_ACESINWRONGORDER = 0x8001013A;

    //
    // MessageId: CO_E_INCOMPATIBLESTREAMVERSION
    //
    // MessageText:
    //
    // The version of ACL format in the stream is not supported by this
    // implementation of IAccessControl
    //
    int CO_E_INCOMPATIBLESTREAMVERSION = 0x8001013B;

    //
    // MessageId: CO_E_FAILEDTOOPENPROCESSTOKEN
    //
    // MessageText:
    //
    // Unable to open the access token of the server process
    //
    int CO_E_FAILEDTOOPENPROCESSTOKEN = 0x8001013C;

    //
    // MessageId: CO_E_DECODEFAILED
    //
    // MessageText:
    //
    // Unable to decode the ACL in the stream provided by the user
    //
    int CO_E_DECODEFAILED = 0x8001013D;

    //
    // MessageId: CO_E_ACNOTINITIALIZED
    //
    // MessageText:
    //
    // The COM IAccessControl object is not initialized
    //
    int CO_E_ACNOTINITIALIZED = 0x8001013F;

    //
    // MessageId: CO_E_CANCEL_DISABLED
    //
    // MessageText:
    //
    // Call Cancellation is disabled
    //
    int CO_E_CANCEL_DISABLED = 0x80010140;

    //
    // MessageId: RPC_E_UNEXPECTED
    //
    // MessageText:
    //
    // An internal error occurred.
    //
    int RPC_E_UNEXPECTED = 0x8001FFFF;

    // ////////////////////////////////////
    // =//
    // Additional Security Status Codes //
    // =//
    // Facility=Security=//
    // =//
    // ////////////////////////////////////

    //
    // MessageId: ERROR_AUDITING_DISABLED
    //
    // MessageText:
    //
    // The specified event is currently not being audited.
    //
    int ERROR_AUDITING_DISABLED = 0xC0090001;

    //
    // MessageId: ERROR_ALL_SIDS_FILTERED
    //
    // MessageText:
    //
    // The SID filtering operation removed all SIDs.
    //
    int ERROR_ALL_SIDS_FILTERED = 0xC0090002;

    //
    // MessageId: ERROR_BIZRULES_NOT_ENABLED
    //
    // MessageText:
    //
    // Business rule scripts are disabled for the calling application.
    //
    int ERROR_BIZRULES_NOT_ENABLED = 0xC0090003;

    // ///////////////////////////////////////////
    // =//
    // end of Additional Security Status Codes //
    // =//
    // ///////////////////////////////////////////

    // ///////////////
    //
    // FACILITY_SSPI
    //
    // ///////////////

    //
    // MessageId: NTE_BAD_UID
    //
    // MessageText:
    //
    // Bad UID.
    //
    int NTE_BAD_UID = 0x80090001;

    //
    // MessageId: NTE_BAD_HASH
    //
    // MessageText:
    //
    // Bad Hash.
    //
    int NTE_BAD_HASH = 0x80090002;

    //
    // MessageId: NTE_BAD_KEY
    //
    // MessageText:
    //
    // Bad Key.
    //
    int NTE_BAD_KEY = 0x80090003;

    //
    // MessageId: NTE_BAD_LEN
    //
    // MessageText:
    //
    // Bad Length.
    //
    int NTE_BAD_LEN = 0x80090004;

    //
    // MessageId: NTE_BAD_DATA
    //
    // MessageText:
    //
    // Bad Data.
    //
    int NTE_BAD_DATA = 0x80090005;

    //
    // MessageId: NTE_BAD_SIGNATURE
    //
    // MessageText:
    //
    // Invalid Signature.
    //
    int NTE_BAD_SIGNATURE = 0x80090006;

    //
    // MessageId: NTE_BAD_VER
    //
    // MessageText:
    //
    // Bad Version of provider.
    //
    int NTE_BAD_VER = 0x80090007;

    //
    // MessageId: NTE_BAD_ALGID
    //
    // MessageText:
    //
    // Invalid algorithm specified.
    //
    int NTE_BAD_ALGID = 0x80090008;

    //
    // MessageId: NTE_BAD_F;AGS
    //
    // MessageText:
    //
    // Invalid F;ags specified.
    //
    int NTE_BAD_FLAGS = 0x80090009;

    //
    // MessageId: NTE_BAD_TYPE
    //
    // MessageText:
    //
    // Invalid type specified.
    //
    int NTE_BAD_TYPE = 0x8009000A;

    //
    // MessageId: NTE_BAD_KEY_STATE
    //
    // MessageText:
    //
    // Key not valid for use in specified state.
    //
    int NTE_BAD_KEY_STATE = 0x8009000B;

    //
    // MessageId: NTE_BAD_HASH_STATE
    //
    // MessageText:
    //
    // Hash not valid for use in specified state.
    //
    int NTE_BAD_HASH_STATE = 0x8009000C;

    //
    // MessageId: NTE_NO_KEY
    //
    // MessageText:
    //
    // Key does not exist.
    //
    int NTE_NO_KEY = 0x8009000D;

    //
    // MessageId: NTE_NO_MEMORY
    //
    // MessageText:
    //
    // Insufficient memory available for the operation.
    //
    int NTE_NO_MEMORY = 0x8009000E;

    //
    // MessageId: NTE_EXISTS
    //
    // MessageText:
    //
    // Object already exists.
    //
    int NTE_EXISTS = 0x8009000F;

    //
    // MessageId: NTE_PERM
    //
    // MessageText:
    //
    // Access denied.
    //
    int NTE_PERM = 0x80090010;

    //
    // MessageId: NTE_NOT_FOUND
    //
    // MessageText:
    //
    // Object was not found.
    //
    int NTE_NOT_FOUND = 0x80090011;

    //
    // MessageId: NTE_DOUBLE_ENCRYPT
    //
    // MessageText:
    //
    // Data already encrypted.
    //
    int NTE_DOUBLE_ENCRYPT = 0x80090012;

    //
    // MessageId: NTE_BAD_PROVIDER
    //
    // MessageText:
    //
    // Invalid provider specified.
    //
    int NTE_BAD_PROVIDER = 0x80090013;

    //
    // MessageId: NTE_BAD_PROV_TYPE
    //
    // MessageText:
    //
    // Invalid provider type specified.
    //
    int NTE_BAD_PROV_TYPE = 0x80090014;

    //
    // MessageId: NTE_BAD_PUBLIC_KEY
    //
    // MessageText:
    //
    // Provider's public key is invalid.
    //
    int NTE_BAD_PUBLIC_KEY = 0x80090015;

    //
    // MessageId: NTE_BAD_KEYSET
    //
    // MessageText:
    //
    // Keyset does not exist
    //
    int NTE_BAD_KEYSET = 0x80090016;

    //
    // MessageId: NTE_PROV_TYPE_NOT_DEF
    //
    // MessageText:
    //
    // Provider type not defined.
    //
    int NTE_PROV_TYPE_NOT_DEF = 0x80090017;

    //
    // MessageId: NTE_PROV_TYPE_ENTRY_BAD
    //
    // MessageText:
    //
    // Provider type as registered is invalid.
    //
    int NTE_PROV_TYPE_ENTRY_BAD = 0x80090018;

    //
    // MessageId: NTE_KEYSET_NOT_DEF
    //
    // MessageText:
    //
    // The keyset is not defined.
    //
    int NTE_KEYSET_NOT_DEF = 0x80090019;

    //
    // MessageId: NTE_KEYSET_ENTRY_BAD
    //
    // MessageText:
    //
    // Keyset as registered is invalid.
    //
    int NTE_KEYSET_ENTRY_BAD = 0x8009001A;

    //
    // MessageId: NTE_PROV_TYPE_NO_MATCH
    //
    // MessageText:
    //
    // Provider type does not match registered value.
    //
    int NTE_PROV_TYPE_NO_MATCH = 0x8009001B;

    //
    // MessageId: NTE_SIGNATURE_FILE_BAD
    //
    // MessageText:
    //
    // The digital signature file is corrupt.
    //
    int NTE_SIGNATURE_FILE_BAD = 0x8009001C;

    //
    // MessageId: NTE_PROVIDER_DLL_FAIL
    //
    // MessageText:
    //
    // Provider DLL failed to initialize correctly.
    //
    int NTE_PROVIDER_DLL_FAIL = 0x8009001D;

    //
    // MessageId: NTE_PROV_DLL_NOT_FOUND
    //
    // MessageText:
    //
    // Provider DLL could not be found.
    //
    int NTE_PROV_DLL_NOT_FOUND = 0x8009001E;

    //
    // MessageId: NTE_BAD_KEYSET_PARAM
    //
    // MessageText:
    //
    // The Keyset parameter is invalid.
    //
    int NTE_BAD_KEYSET_PARAM = 0x8009001F;

    //
    // MessageId: NTE_FAIL
    //
    // MessageText:
    //
    // An internal error occurred.
    //
    int NTE_FAIL = 0x80090020;

    //
    // MessageId: NTE_SYS_ERR
    //
    // MessageText:
    //
    // A base error occurred.
    //
    int NTE_SYS_ERR = 0x80090021;

    //
    // MessageId: NTE_SILENT_CONTEXT
    //
    // MessageText:
    //
    // Provider could not perform the action since the context was acquired as
    // silent.
    //
    int NTE_SILENT_CONTEXT = 0x80090022;

    //
    // MessageId: NTE_TOKEN_KEYSET_STORAGE_FULL
    //
    // MessageText:
    //
    // The security token does not have storage space available for an
    // additional container.
    //
    int NTE_TOKEN_KEYSET_STORAGE_FULL = 0x80090023;

    //
    // MessageId: NTE_TEMPORARY_PROFILE
    //
    // MessageText:
    //
    // The profile for the user is a temporary profile.
    //
    int NTE_TEMPORARY_PROFILE = 0x80090024;

    //
    // MessageId: NTE_FIXEDPARAMETER
    //
    // MessageText:
    //
    // The key parameters could not be set because the CSP uses fixed
    // parameters.
    //
    int NTE_FIXEDPARAMETER = 0x80090025;

    //
    // MessageId: NTE_INVALID_HANDLE
    //
    // MessageText:
    //
    // The supplied handle is invalid.
    //
    int NTE_INVALID_HANDLE = 0x80090026;

    //
    // MessageId: NTE_INVALID_PARAMETER
    //
    // MessageText:
    //
    // The parameter is incorrect.
    //
    int NTE_INVALID_PARAMETER = 0x80090027;

    //
    // MessageId: NTE_BUFFER_TOO_SMALL
    //
    // MessageText:
    //
    // The buffer supplied to a function was too small.
    //
    int NTE_BUFFER_TOO_SMALL = 0x80090028;

    //
    // MessageId: NTE_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The requested operation is not supported.
    //
    int NTE_NOT_SUPPORTED = 0x80090029;

    //
    // MessageId: NTE_NO_MORE_ITEMS
    //
    // MessageText:
    //
    // No more data is available.
    //
    int NTE_NO_MORE_ITEMS = 0x8009002A;

    //
    // MessageId: NTE_BUFFERS_OVERLAP
    //
    // MessageText:
    //
    // The supplied buffers overlap incorrectly.
    //
    int NTE_BUFFERS_OVERLAP = 0x8009002B;

    //
    // MessageId: NTE_DECRYPTION_FAILURE
    //
    // MessageText:
    //
    // The specified data could not be decrypted.
    //
    int NTE_DECRYPTION_FAILURE = 0x8009002C;

    //
    // MessageId: NTE_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal consistency check failed.
    //
    int NTE_INTERNAL_ERROR = 0x8009002D;

    //
    // MessageId: NTE_UI_REQUIRED
    //
    // MessageText:
    //
    // This operation requires input from the user.
    //
    int NTE_UI_REQUIRED = 0x8009002E;

    //
    // MessageId: NTE_HMAC_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The cryptographic provider does not support HMAC.
    //
    int NTE_HMAC_NOT_SUPPORTED = 0x8009002F;

    //
    // MessageId: SEC_E_INSUFFICIENT_MEMORY
    //
    // MessageText:
    //
    // Not enough memory is available to complete this request
    //
    int SEC_E_INSUFFICIENT_MEMORY = 0x80090300;

    //
    // MessageId: SEC_E_INVALID_HANDLE
    //
    // MessageText:
    //
    // The handle specified is invalid
    //
    int SEC_E_INVALID_HANDLE = 0x80090301;

    //
    // MessageId: SEC_E_UNSUPPORTED_FUNCTION
    //
    // MessageText:
    //
    // The function requested is not supported
    //
    int SEC_E_UNSUPPORTED_FUNCTION = 0x80090302;

    //
    // MessageId: SEC_E_TARGET_UNKNOWN
    //
    // MessageText:
    //
    // The specified target is unknown or unreachable
    //
    int SEC_E_TARGET_UNKNOWN = 0x80090303;

    //
    // MessageId: SEC_E_INTERNAL_ERROR
    //
    // MessageText:
    //
    // The Local Security Authority cannot be contacted
    //
    int SEC_E_INTERNAL_ERROR = 0x80090304;

    //
    // MessageId: SEC_E_SECPKG_NOT_FOUND
    //
    // MessageText:
    //
    // The requested security package does not exist
    //
    int SEC_E_SECPKG_NOT_FOUND = 0x80090305;

    //
    // MessageId: SEC_E_NOT_OWNER
    //
    // MessageText:
    //
    // The caller is not the owner of the desired credentials
    //
    int SEC_E_NOT_OWNER = 0x80090306;

    //
    // MessageId: SEC_E_CANNOT_INSTALL
    //
    // MessageText:
    //
    // The security package failed to initialize, and cannot be installed
    //
    int SEC_E_CANNOT_INSTALL = 0x80090307;

    //
    // MessageId: SEC_E_INVALID_TOKEN
    //
    // MessageText:
    //
    // The token supplied to the function is invalid
    //
    int SEC_E_INVALID_TOKEN = 0x80090308;

    //
    // MessageId: SEC_E_CANNOT_PACK
    //
    // MessageText:
    //
    // The security package is not able to marshall the logon buffer, so the
    // logon attempt has failed
    //
    int SEC_E_CANNOT_PACK = 0x80090309;

    //
    // MessageId: SEC_E_QOP_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The per-message Quality of Protection is not supported by the security
    // package
    //
    int SEC_E_QOP_NOT_SUPPORTED = 0x8009030A;

    //
    // MessageId: SEC_E_NO_IMPERSONATION
    //
    // MessageText:
    //
    // The security context does not allow impersonation of the client
    //
    int SEC_E_NO_IMPERSONATION = 0x8009030B;

    //
    // MessageId: SEC_E_LOGON_DENIED
    //
    // MessageText:
    //
    // The logon attempt failed
    //
    int SEC_E_LOGON_DENIED = 0x8009030C;

    //
    // MessageId: SEC_E_UNKNOWN_CREDENTIALS
    //
    // MessageText:
    //
    // The credentials supplied to the package were not recognized
    //
    int SEC_E_UNKNOWN_CREDENTIALS = 0x8009030D;

    //
    // MessageId: SEC_E_NO_CREDENTIALS
    //
    // MessageText:
    //
    // No credentials are available in the security package
    //
    int SEC_E_NO_CREDENTIALS = 0x8009030E;

    //
    // MessageId: SEC_E_MESSAGE_ALTERED
    //
    // MessageText:
    //
    // The message or signature supplied for verification has been altered
    //
    int SEC_E_MESSAGE_ALTERED = 0x8009030F;

    //
    // MessageId: SEC_E_OUT_OF_SEQUENCE
    //
    // MessageText:
    //
    // The message supplied for verification is out of sequence
    //
    int SEC_E_OUT_OF_SEQUENCE = 0x80090310;

    //
    // MessageId: SEC_E_NO_AUTHENTICATING_AUTHORITY
    //
    // MessageText:
    //
    // No authority could be contacted for authentication.
    //
    int SEC_E_NO_AUTHENTICATING_AUTHORITY = 0x80090311;

    //
    // MessageId: SEC_I_CONTINUE_NEEDED
    //
    // MessageText:
    //
    // The function completed successfully, but must be called again to complete
    // the context
    //
    int SEC_I_CONTINUE_NEEDED = 0x00090312;

    //
    // MessageId: SEC_I_COMPLETE_NEEDED
    //
    // MessageText:
    //
    // The function completed successfully, but CompleteToken must be called
    //
    int SEC_I_COMPLETE_NEEDED = 0x00090313;

    //
    // MessageId: SEC_I_COMPLETE_AND_CONTINUE
    //
    // MessageText:
    //
    // The function completed successfully, but both CompleteToken and this
    // function must be called to complete the context
    //
    int SEC_I_COMPLETE_AND_CONTINUE = 0x00090314;

    //
    // MessageId: SEC_I_LOCAL_LOGON
    //
    // MessageText:
    //
    // The logon was completed, but no network authority was available. The
    // logon was made using locally known information
    //
    int SEC_I_LOCAL_LOGON = 0x00090315;

    //
    // MessageId: SEC_E_BAD_PKGID
    //
    // MessageText:
    //
    // The requested security package does not exist
    //
    int SEC_E_BAD_PKGID = 0x80090316;

    //
    // MessageId: SEC_E_CONTEXT_EXPIRED
    //
    // MessageText:
    //
    // The context has expired and can no longer be used.
    //
    int SEC_E_CONTEXT_EXPIRED = 0x80090317;

    //
    // MessageId: SEC_I_CONTEXT_EXPIRED
    //
    // MessageText:
    //
    // The context has expired and can no longer be used.
    //
    int SEC_I_CONTEXT_EXPIRED = 0x00090317;

    //
    // MessageId: SEC_E_INCOMPLETE_MESSAGE
    //
    // MessageText:
    //
    // The supplied message is incomplete. The signature was not verified.
    //
    int SEC_E_INCOMPLETE_MESSAGE = 0x80090318;

    //
    // MessageId: SEC_E_INCOMPLETE_CREDENTIALS
    //
    // MessageText:
    //
    // The credentials supplied were not complete, and could not be verified.
    // The context could not be initialized.
    //
    int SEC_E_INCOMPLETE_CREDENTIALS = 0x80090320;

    //
    // MessageId: SEC_E_BUFFER_TOO_SMALL
    //
    // MessageText:
    //
    // The buffers supplied to a function was too small.
    //
    int SEC_E_BUFFER_TOO_SMALL = 0x80090321;

    //
    // MessageId: SEC_I_INCOMPLETE_CREDENTIALS
    //
    // MessageText:
    //
    // The credentials supplied were not complete, and could not be verified.
    // Additional information can be returned from the context.
    //
    int SEC_I_INCOMPLETE_CREDENTIALS = 0x00090320;

    //
    // MessageId: SEC_I_RENEGOTIATE
    //
    // MessageText:
    //
    // The context data must be renegotiated with the peer.
    //
    int SEC_I_RENEGOTIATE = 0x00090321;

    //
    // MessageId: SEC_E_WRONG_PRINCIPAL
    //
    // MessageText:
    //
    // The target principal name is incorrect.
    //
    int SEC_E_WRONG_PRINCIPAL = 0x80090322;

    //
    // MessageId: SEC_I_NO_LSA_CONTEXT
    //
    // MessageText:
    //
    // There is no LSA mode context associated with this context.
    //
    int SEC_I_NO_LSA_CONTEXT = 0x00090323;

    //
    // MessageId: SEC_E_TIME_SKEW
    //
    // MessageText:
    //
    // The clocks on the client and server machines are skewed.
    //
    int SEC_E_TIME_SKEW = 0x80090324;

    //
    // MessageId: SEC_E_UNTRUSTED_ROOT
    //
    // MessageText:
    //
    // The certificate chain was issued by an authority that is not trusted.
    //
    int SEC_E_UNTRUSTED_ROOT = 0x80090325;

    //
    // MessageId: SEC_E_ILLEGAL_MESSAGE
    //
    // MessageText:
    //
    // The message received was unexpected or badly formatted.
    //
    int SEC_E_ILLEGAL_MESSAGE = 0x80090326;

    //
    // MessageId: SEC_E_CERT_UNKNOWN
    //
    // MessageText:
    //
    // An unknown error occurred while processing the certificate.
    //
    int SEC_E_CERT_UNKNOWN = 0x80090327;

    //
    // MessageId: SEC_E_CERT_EXPIRED
    //
    // MessageText:
    //
    // The received certificate has expired.
    //
    int SEC_E_CERT_EXPIRED = 0x80090328;

    //
    // MessageId: SEC_E_ENCRYPT_FAILURE
    //
    // MessageText:
    //
    // The specified data could not be encrypted.
    //
    int SEC_E_ENCRYPT_FAILURE = 0x80090329;

    //
    // MessageId: SEC_E_DECRYPT_FAILURE
    //
    // MessageText:
    //
    // The specified data could not be decrypted.
    // 
    //
    int SEC_E_DECRYPT_FAILURE = 0x80090330;

    //
    // MessageId: SEC_E_ALGORITHM_MISMATCH
    //
    // MessageText:
    //
    // The client and server cannot communicate, because they do not possess a
    // common algorithm.
    //
    int SEC_E_ALGORITHM_MISMATCH = 0x80090331;

    //
    // MessageId: SEC_E_SECURITY_QOS_FAILED
    //
    // MessageText:
    //
    // The security context could not be established due to a failure in the
    // requested quality of service (e.g. mutual authentication or delegation).
    //
    int SEC_E_SECURITY_QOS_FAILED = 0x80090332;

    //
    // MessageId: SEC_E_UNFINISHED_CONTEXT_DELETED
    //
    // MessageText:
    //
    // A security context was deleted before the context was completed. This is
    // considered a logon failure.
    //
    int SEC_E_UNFINISHED_CONTEXT_DELETED = 0x80090333;

    //
    // MessageId: SEC_E_NO_TGT_REPLY
    //
    // MessageText:
    //
    // The client is trying to negotiate a context and the server requires
    // user-to-user but didn't send a TGT reply.
    //
    int SEC_E_NO_TGT_REPLY = 0x80090334;

    //
    // MessageId: SEC_E_NO_IP_ADDRESSES
    //
    // MessageText:
    //
    // Unable to accomplish the requested task because the local machine does
    // not have any IP addresses.
    //
    int SEC_E_NO_IP_ADDRESSES = 0x80090335;

    //
    // MessageId: SEC_E_WRONG_CREDENTIAL_HANDLE
    //
    // MessageText:
    //
    // The supplied credential handle does not match the credential associated
    // with the security context.
    //
    int SEC_E_WRONG_CREDENTIAL_HANDLE = 0x80090336;

    //
    // MessageId: SEC_E_CRYPTO_SYSTEM_INVALID
    //
    // MessageText:
    //
    // The crypto system or checksum function is invalid because a required
    // function is unavailable.
    //
    int SEC_E_CRYPTO_SYSTEM_INVALID = 0x80090337;

    //
    // MessageId: SEC_E_MAX_REFERRALS_EXCEEDED
    //
    // MessageText:
    //
    // The number of maximum ticket referrals has been exceeded.
    //
    int SEC_E_MAX_REFERRALS_EXCEEDED = 0x80090338;

    //
    // MessageId: SEC_E_MUST_BE_KDC
    //
    // MessageText:
    //
    // The local machine must be a Kerberos KDC (domain controller) and it is
    // not.
    //
    int SEC_E_MUST_BE_KDC = 0x80090339;

    //
    // MessageId: SEC_E_STRONG_CRYPTO_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The other end of the security negotiation is requires strong crypto but
    // it is not supported on the local machine.
    //
    int SEC_E_STRONG_CRYPTO_NOT_SUPPORTED = 0x8009033A;

    //
    // MessageId: SEC_E_TOO_MANY_PRINCIPALS
    //
    // MessageText:
    //
    // The KDC reply contained more than one principal name.
    //
    int SEC_E_TOO_MANY_PRINCIPALS = 0x8009033B;

    //
    // MessageId: SEC_E_NO_PA_DATA
    //
    // MessageText:
    //
    // Expected to find PA data for a hint of what etype to use, but it was not
    // found.
    //
    int SEC_E_NO_PA_DATA = 0x8009033C;

    //
    // MessageId: SEC_E_PKINIT_NAME_MISMATCH
    //
    // MessageText:
    //
    // The client certificate does not contain a valid UPN, or does not match
    // the client name
    // in the logon request. Please contact your administrator.
    //
    int SEC_E_PKINIT_NAME_MISMATCH = 0x8009033D;

    //
    // MessageId: SEC_E_SMARTCARD_LOGON_REQUIRED
    //
    // MessageText:
    //
    // Smartcard logon is required and was not used.
    //
    int SEC_E_SMARTCARD_LOGON_REQUIRED = 0x8009033E;

    //
    // MessageId: SEC_E_SHUTDOWN_IN_PROGRESS
    //
    // MessageText:
    //
    // A system shutdown is in progress.
    //
    int SEC_E_SHUTDOWN_IN_PROGRESS = 0x8009033F;

    //
    // MessageId: SEC_E_KDC_INVALID_REQUEST
    //
    // MessageText:
    //
    // An invalid request was sent to the KDC.
    //
    int SEC_E_KDC_INVALID_REQUEST = 0x80090340;

    //
    // MessageId: SEC_E_KDC_UNABLE_TO_REFER
    //
    // MessageText:
    //
    // The KDC was unable to generate a referral for the service requested.
    //
    int SEC_E_KDC_UNABLE_TO_REFER = 0x80090341;

    //
    // MessageId: SEC_E_KDC_UNKNOWN_ETYPE
    //
    // MessageText:
    //
    // The encryption type requested is not supported by the KDC.
    //
    int SEC_E_KDC_UNKNOWN_ETYPE = 0x80090342;

    //
    // MessageId: SEC_E_UNSUPPORTED_PREAUTH
    //
    // MessageText:
    //
    // An unsupported preauthentication mechanism was presented to the kerberos
    // package.
    //
    int SEC_E_UNSUPPORTED_PREAUTH = 0x80090343;

    //
    // MessageId: SEC_E_DELEGATION_REQUIRED
    //
    // MessageText:
    //
    // The requested operation cannot be completed. The computer must be trusted
    // for delegation and the current user account must be configured to allow
    // delegation.
    //
    int SEC_E_DELEGATION_REQUIRED = 0x80090345;

    //
    // MessageId: SEC_E_BAD_BINDINGS
    //
    // MessageText:
    //
    // Client's supplied SSPI channel bindings were incorrect.
    //
    int SEC_E_BAD_BINDINGS = 0x80090346;

    //
    // MessageId: SEC_E_MULTIPLE_ACCOUNTS
    //
    // MessageText:
    //
    // The received certificate was mapped to multiple accounts.
    //
    int SEC_E_MULTIPLE_ACCOUNTS = 0x80090347;

    //
    // MessageId: SEC_E_NO_KERB_KEY
    //
    // MessageText:
    //
    // SEC_E_NO_KERB_KEY
    //
    int SEC_E_NO_KERB_KEY = 0x80090348;

    //
    // MessageId: SEC_E_CERT_WRONG_USAGE
    //
    // MessageText:
    //
    // The certificate is not valid for the requested usage.
    //
    int SEC_E_CERT_WRONG_USAGE = 0x80090349;

    //
    // MessageId: SEC_E_DOWNGRADE_DETECTED
    //
    // MessageText:
    //
    // The system detected a possible attempt to compromise security. Please
    // ensure that you can contact the server that authenticated you.
    //
    int SEC_E_DOWNGRADE_DETECTED = 0x80090350;

    //
    // MessageId: SEC_E_SMARTCARD_CERT_REVOKED
    //
    // MessageText:
    //
    // The smartcard certificate used for authentication has been revoked.
    // Please contact your system administrator. There may be additional
    // information in the event log.
    //
    int SEC_E_SMARTCARD_CERT_REVOKED = 0x80090351;

    //
    // MessageId: SEC_E_ISSUING_CA_UNTRUSTED
    //
    // MessageText:
    //
    // An untrusted certificate authority was detected While processing the
    // smartcard certificate used for authentication. Please contact your system
    // administrator.
    //
    int SEC_E_ISSUING_CA_UNTRUSTED = 0x80090352;

    //
    // MessageId: SEC_E_REVOCATION_OFF;INE_C
    //
    // MessageText:
    //
    // The revocation status of the smartcard certificate used for
    // authentication could not be determined. Please contact your system
    // administrator.
    //
    int SEC_E_REVOCATION_OFFLINE_C = 0x80090353;

    //
    // MessageId: SEC_E_PKINIT_CLIENT_FAILURE
    //
    // MessageText:
    //
    // The smartcard certificate used for authentication was not trusted. Please
    // contact your system administrator.
    //
    int SEC_E_PKINIT_CLIENT_FAILURE = 0x80090354;

    //
    // MessageId: SEC_E_SMARTCARD_CERT_EXPIRED
    //
    // MessageText:
    //
    // The smartcard certificate used for authentication has expired. Please
    // contact your system administrator.
    //
    int SEC_E_SMARTCARD_CERT_EXPIRED = 0x80090355;

    //
    // MessageId: SEC_E_NO_S4U_PROT_SUPPORT
    //
    // MessageText:
    //
    // The Kerberos subsystem encountered an error. A service for user protocol
    // request was made against a domain controller which does not support
    // service for user.
    //
    int SEC_E_NO_S4U_PROT_SUPPORT = 0x80090356;

    //
    // MessageId: SEC_E_CROSSREALM_DELEGATION_FAILURE
    //
    // MessageText:
    //
    // An attempt was made by this server to make a Kerberos constrained
    // delegation request for a target outside of the server's realm. This is
    // not supported, and indicates a misconfiguration on this server's allowed
    // to delegate to list. Please contact your administrator.
    //
    int SEC_E_CROSSREALM_DELEGATION_FAILURE = 0x80090357;

    //
    // MessageId: SEC_E_REVOCATION_OFF;INE_KDC
    //
    // MessageText:
    //
    // The revocation status of the domain controller certificate used for
    // smartcard authentication could not be determined. There is additional
    // information in the system event log. Please contact your system
    // administrator.
    //
    int SEC_E_REVOCATION_OFFLINE_KDC = 0x80090358;

    //
    // MessageId: SEC_E_ISSUING_CA_UNTRUSTED_KDC
    //
    // MessageText:
    //
    // An untrusted certificate authority was detected while processing the
    // domain controller certificate used for authentication. There is
    // additional information in the system event log. Please contact your
    // system administrator.
    //
    int SEC_E_ISSUING_CA_UNTRUSTED_KDC = 0x80090359;

    //
    // MessageId: SEC_E_KDC_CERT_EXPIRED
    //
    // MessageText:
    //
    // The domain controller certificate used for smartcard logon has expired.
    // Please contact your system administrator with the contents of your system
    // event log.
    //
    int SEC_E_KDC_CERT_EXPIRED = 0x8009035A;

    //
    // MessageId: SEC_E_KDC_CERT_REVOKED
    //
    // MessageText:
    //
    // The domain controller certificate used for smartcard logon has been
    // revoked. Please contact your system administrator with the contents of
    // your system event log.
    //
    int SEC_E_KDC_CERT_REVOKED = 0x8009035B;

    //
    // MessageId: SEC_I_SIGNATURE_NEEDED
    //
    // MessageText:
    //
    // A signature operation must be performed before the user can authenticate.
    //
    int SEC_I_SIGNATURE_NEEDED = 0x0009035C;

    //
    // MessageId: SEC_E_INVALID_PARAMETER
    //
    // MessageText:
    //
    // One or more of the parameters passed to the function was invalid.
    //
    int SEC_E_INVALID_PARAMETER = 0x8009035D;

    //
    // MessageId: SEC_E_DELEGATION_POLICY
    //
    // MessageText:
    //
    // Client policy does not allow credential delegation to target server.
    //
    int SEC_E_DELEGATION_POLICY = 0x8009035E;

    //
    // MessageId: SEC_E_POLICY_NLTM_ONLY
    //
    // MessageText:
    //
    // Client policy does not allow credential delegation to target server with
    // NLTM only authentication.
    //
    int SEC_E_POLICY_NLTM_ONLY = 0x8009035F;

    //
    // MessageId: SEC_I_NO_RENEGOTIATION
    //
    // MessageText:
    //
    // The recipient rejected the renegotiation request.
    //
    int SEC_I_NO_RENEGOTIATION = 0x00090360;

    //
    // Provided for backwards compatibility
    //

    int SEC_E_NO_SPM = SEC_E_INTERNAL_ERROR;
    int SEC_E_NOT_SUPPORTED = SEC_E_UNSUPPORTED_FUNCTION;

    //
    // MessageId: CRYPT_E_MSG_ERROR
    //
    // MessageText:
    //
    // An error occurred while performing an operation on a cryptographic
    // message.
    //
    int CRYPT_E_MSG_ERROR = 0x80091001;

    //
    // MessageId: CRYPT_E_UNKNOWN_ALGO
    //
    // MessageText:
    //
    // Unknown cryptographic algorithm.
    //
    int CRYPT_E_UNKNOWN_ALGO = 0x80091002;

    //
    // MessageId: CRYPT_E_OID_FORMAT
    //
    // MessageText:
    //
    // The object identifier is poorly formatted.
    //
    int CRYPT_E_OID_FORMAT = 0x80091003;

    //
    // MessageId: CRYPT_E_INVALID_MSG_TYPE
    //
    // MessageText:
    //
    // Invalid cryptographic message type.
    //
    int CRYPT_E_INVALID_MSG_TYPE = 0x80091004;

    //
    // MessageId: CRYPT_E_UNEXPECTED_ENCODING
    //
    // MessageText:
    //
    // Unexpected cryptographic message encoding.
    //
    int CRYPT_E_UNEXPECTED_ENCODING = 0x80091005;

    //
    // MessageId: CRYPT_E_AUTH_ATTR_MISSING
    //
    // MessageText:
    //
    // The cryptographic message does not contain an expected authenticated
    // attribute.
    //
    int CRYPT_E_AUTH_ATTR_MISSING = 0x80091006;

    //
    // MessageId: CRYPT_E_HASH_VALUE
    //
    // MessageText:
    //
    // The hash value is not correct.
    //
    int CRYPT_E_HASH_VALUE = 0x80091007;

    //
    // MessageId: CRYPT_E_INVALID_INDEX
    //
    // MessageText:
    //
    // The index value is not valid.
    //
    int CRYPT_E_INVALID_INDEX = 0x80091008;

    //
    // MessageId: CRYPT_E_ALREADY_DECRYPTED
    //
    // MessageText:
    //
    // The content of the cryptographic message has already been decrypted.
    //
    int CRYPT_E_ALREADY_DECRYPTED = 0x80091009;

    //
    // MessageId: CRYPT_E_NOT_DECRYPTED
    //
    // MessageText:
    //
    // The content of the cryptographic message has not been decrypted yet.
    //
    int CRYPT_E_NOT_DECRYPTED = 0x8009100A;

    //
    // MessageId: CRYPT_E_RECIPIENT_NOT_FOUND
    //
    // MessageText:
    //
    // The enveloped-data message does not contain the specified recipient.
    //
    int CRYPT_E_RECIPIENT_NOT_FOUND = 0x8009100B;

    //
    // MessageId: CRYPT_E_CONTROL_TYPE
    //
    // MessageText:
    //
    // Invalid control type.
    //
    int CRYPT_E_CONTROL_TYPE = 0x8009100C;

    //
    // MessageId: CRYPT_E_ISSUER_SERIALNUMBER
    //
    // MessageText:
    //
    // Invalid issuer and/or serial number.
    //
    int CRYPT_E_ISSUER_SERIALNUMBER = 0x8009100D;

    //
    // MessageId: CRYPT_E_SIGNER_NOT_FOUND
    //
    // MessageText:
    //
    // Cannot find the original signer.
    //
    int CRYPT_E_SIGNER_NOT_FOUND = 0x8009100E;

    //
    // MessageId: CRYPT_E_ATTRIBUTES_MISSING
    //
    // MessageText:
    //
    // The cryptographic message does not contain all of the requested
    // attributes.
    //
    int CRYPT_E_ATTRIBUTES_MISSING = 0x8009100F;

    //
    // MessageId: CRYPT_E_STREAM_MSG_NOT_READY
    //
    // MessageText:
    //
    // The streamed cryptographic message is not ready to return data.
    //
    int CRYPT_E_STREAM_MSG_NOT_READY = 0x80091010;

    //
    // MessageId: CRYPT_E_STREAM_INSUFFICIENT_DATA
    //
    // MessageText:
    //
    // The streamed cryptographic message requires more data to complete the
    // decode operation.
    //
    int CRYPT_E_STREAM_INSUFFICIENT_DATA = 0x80091011;

    //
    // MessageId: CRYPT_I_NEW_PROTECTION_REQUIRED
    //
    // MessageText:
    //
    // The protected data needs to be re-protected.
    //
    int CRYPT_I_NEW_PROTECTION_REQUIRED = 0x00091012;

    //
    // MessageId: CRYPT_E_BAD_LEN
    //
    // MessageText:
    //
    // The length specified for the output data was insufficient.
    //
    int CRYPT_E_BAD_LEN = 0x80092001;

    //
    // MessageId: CRYPT_E_BAD_ENCODE
    //
    // MessageText:
    //
    // An error occurred during encode or decode operation.
    //
    int CRYPT_E_BAD_ENCODE = 0x80092002;

    //
    // MessageId: CRYPT_E_FILE_ERROR
    //
    // MessageText:
    //
    // An error occurred while reading or writing to a file.
    //
    int CRYPT_E_FILE_ERROR = 0x80092003;

    //
    // MessageId: CRYPT_E_NOT_FOUND
    //
    // MessageText:
    //
    // Cannot find object or property.
    //
    int CRYPT_E_NOT_FOUND = 0x80092004;

    //
    // MessageId: CRYPT_E_EXISTS
    //
    // MessageText:
    //
    // The object or property already exists.
    //
    int CRYPT_E_EXISTS = 0x80092005;

    //
    // MessageId: CRYPT_E_NO_PROVIDER
    //
    // MessageText:
    //
    // No provider was specified for the store or object.
    //
    int CRYPT_E_NO_PROVIDER = 0x80092006;

    //
    // MessageId: CRYPT_E_SELF_SIGNED
    //
    // MessageText:
    //
    // The specified certificate is self signed.
    //
    int CRYPT_E_SELF_SIGNED = 0x80092007;

    //
    // MessageId: CRYPT_E_DELETED_PREV
    //
    // MessageText:
    //
    // The previous certificate or CRL context was deleted.
    //
    int CRYPT_E_DELETED_PREV = 0x80092008;

    //
    // MessageId: CRYPT_E_NO_MATCH
    //
    // MessageText:
    //
    // Cannot find the requested object.
    //
    int CRYPT_E_NO_MATCH = 0x80092009;

    //
    // MessageId: CRYPT_E_UNEXPECTED_MSG_TYPE
    //
    // MessageText:
    //
    // The certificate does not have a property that references a private key.
    //
    int CRYPT_E_UNEXPECTED_MSG_TYPE = 0x8009200A;

    //
    // MessageId: CRYPT_E_NO_KEY_PROPERTY
    //
    // MessageText:
    //
    // Cannot find the certificate and private key for decryption.
    //
    int CRYPT_E_NO_KEY_PROPERTY = 0x8009200B;

    //
    // MessageId: CRYPT_E_NO_DECRYPT_CERT
    //
    // MessageText:
    //
    // Cannot find the certificate and private key to use for decryption.
    //
    int CRYPT_E_NO_DECRYPT_CERT = 0x8009200C;

    //
    // MessageId: CRYPT_E_BAD_MSG
    //
    // MessageText:
    //
    // Not a cryptographic message or the cryptographic message is not formatted
    // correctly.
    //
    int CRYPT_E_BAD_MSG = 0x8009200D;

    //
    // MessageId: CRYPT_E_NO_SIGNER
    //
    // MessageText:
    //
    // The signed cryptographic message does not have a signer for the specified
    // signer index.
    //
    int CRYPT_E_NO_SIGNER = 0x8009200E;

    //
    // MessageId: CRYPT_E_PENDING_CLOSE
    //
    // MessageText:
    //
    // Final closure is pending until additional frees or closes.
    //
    int CRYPT_E_PENDING_CLOSE = 0x8009200F;

    //
    // MessageId: CRYPT_E_REVOKED
    //
    // MessageText:
    //
    // The certificate is revoked.
    //
    int CRYPT_E_REVOKED = 0x80092010;

    //
    // MessageId: CRYPT_E_NO_REVOCATION_DLL
    //
    // MessageText:
    //
    // No Dll or exported function was found to verify revocation.
    //
    int CRYPT_E_NO_REVOCATION_DLL = 0x80092011;

    //
    // MessageId: CRYPT_E_NO_REVOCATION_CHECK
    //
    // MessageText:
    //
    // The revocation function was unable to check revocation for the
    // certificate.
    //
    int CRYPT_E_NO_REVOCATION_CHECK = 0x80092012;

    //
    // MessageId: CRYPT_E_REVOCATION_OFF;INE
    //
    // MessageText:
    //
    // The revocation function was unable to check revocation because the
    // revocation server was ofF;ine.
    //
    int CRYPT_E_REVOCATION_OFFLINE = 0x80092013;

    //
    // MessageId: CRYPT_E_NOT_IN_REVOCATION_DATABASE
    //
    // MessageText:
    //
    // The certificate is not in the revocation server's database.
    //
    int CRYPT_E_NOT_IN_REVOCATION_DATABASE = 0x80092014;

    //
    // MessageId: CRYPT_E_INVALID_NUMERIC_STRING
    //
    // MessageText:
    //
    // The string contains a non-numeric character.
    //
    int CRYPT_E_INVALID_NUMERIC_STRING = 0x80092020;

    //
    // MessageId: CRYPT_E_INVALID_PRINTABLE_STRING
    //
    // MessageText:
    //
    // The string contains a non-printable character.
    //
    int CRYPT_E_INVALID_PRINTABLE_STRING = 0x80092021;

    //
    // MessageId: CRYPT_E_INVALID_IA5_STRING
    //
    // MessageText:
    //
    // The string contains a character not in the=7 bit ASCII character set.
    //
    int CRYPT_E_INVALID_IA5_STRING = 0x80092022;

    //
    // MessageId: CRYPT_E_INVALID_X500_STRING
    //
    // MessageText:
    //
    // The string contains an invalid X500 name attribute key, oid, value or
    // delimiter.
    //
    int CRYPT_E_INVALID_X500_STRING = 0x80092023;

    //
    // MessageId: CRYPT_E_NOT_CHAR_STRING
    //
    // MessageText:
    //
    // The dwValueType for the CERT_NAME_VALUE is not one of the character
    // strings. Most likely it is either a CERT_RDN_ENCODED_BLOB or
    // CERT_TDN_OCTED_STRING.
    //
    int CRYPT_E_NOT_CHAR_STRING = 0x80092024;

    //
    // MessageId: CRYPT_E_FILERESIZED
    //
    // MessageText:
    //
    // The Put operation cannot continue. The file needs to be resized. However,
    // there is already a signature present. A complete signing operation must
    // be done.
    //
    int CRYPT_E_FILERESIZED = 0x80092025;

    //
    // MessageId: CRYPT_E_SECURITY_SETTINGS
    //
    // MessageText:
    //
    // The cryptographic operation failed due to a local security option
    // setting.
    //
    int CRYPT_E_SECURITY_SETTINGS = 0x80092026;

    //
    // MessageId: CRYPT_E_NO_VERIFY_USAGE_DLL
    //
    // MessageText:
    //
    // No DLL or exported function was found to verify subject usage.
    //
    int CRYPT_E_NO_VERIFY_USAGE_DLL = 0x80092027;

    //
    // MessageId: CRYPT_E_NO_VERIFY_USAGE_CHECK
    //
    // MessageText:
    //
    // The called function was unable to do a usage check on the subject.
    //
    int CRYPT_E_NO_VERIFY_USAGE_CHECK = 0x80092028;

    //
    // MessageId: CRYPT_E_VERIFY_USAGE_OFF;INE
    //
    // MessageText:
    //
    // Since the server was ofF;ine, the called function was unable to complete
    // the usage check.
    //
    int CRYPT_E_VERIFY_USAGE_OFFLINE = 0x80092029;

    //
    // MessageId: CRYPT_E_NOT_IN_CTL
    //
    // MessageText:
    //
    // The subject was not found in a Certificate Trust List (CT;.
    //
    int CRYPT_E_NOT_IN_CTL = 0x8009202A;

    //
    // MessageId: CRYPT_E_NO_TRUSTED_SIGNER
    //
    // MessageText:
    //
    // None of the signers of the cryptographic message or certificate trust
    // list is trusted.
    //
    int CRYPT_E_NO_TRUSTED_SIGNER = 0x8009202B;

    //
    // MessageId: CRYPT_E_MISSING_PUBKEY_PARA
    //
    // MessageText:
    //
    // The public key's algorithm parameters are missing.
    //
    int CRYPT_E_MISSING_PUBKEY_PARA = 0x8009202C;

    //
    // MessageId: CRYPT_E_OSS_ERROR
    //
    // MessageText:
    //
    // OSS Certificate encode/decode error code base
    // 
    // See asn1code.h for a definition of the OSS runtime errors. The OSS
    // error values are offset by CRYPT_E_OSS_ERROR.
    //
    int CRYPT_E_OSS_ERROR = 0x80093000;

    //
    // MessageId: OSS_MORE_BUF
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Output Buffer is too small.
    //
    int OSS_MORE_BUF = 0x80093001;

    //
    // MessageId: OSS_NEGATIVE_UINTEGER
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Signed integer is encoded as a unsigned integer.
    //
    int OSS_NEGATIVE_UINTEGER = 0x80093002;

    //
    // MessageId: OSS_PDU_RANGE
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Unknown ASN.1 data type.
    //
    int OSS_PDU_RANGE = 0x80093003;

    //
    // MessageId: OSS_MORE_INPUT
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Output buffer is too small, the decoded data has been
    // truncated.
    //
    int OSS_MORE_INPUT = 0x80093004;

    //
    // MessageId: OSS_DATA_ERROR
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_DATA_ERROR = 0x80093005;

    //
    // MessageId: OSS_BAD_ARG
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid argument.
    //
    int OSS_BAD_ARG = 0x80093006;

    //
    // MessageId: OSS_BAD_VERSION
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Encode/Decode version mismatch.
    //
    int OSS_BAD_VERSION = 0x80093007;

    //
    // MessageId: OSS_OUT_MEMORY
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Out of memory.
    //
    int OSS_OUT_MEMORY = 0x80093008;

    //
    // MessageId: OSS_PDU_MISMATCH
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Encode/Decode Error.
    //
    int OSS_PDU_MISMATCH = 0x80093009;

    //
    // MessageId: OSS_LIMITED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Internal Error.
    //
    int OSS_LIMITED = 0x8009300A;

    //
    // MessageId: OSS_BAD_PTR
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_BAD_PTR = 0x8009300B;

    //
    // MessageId: OSS_BAD_TIME
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_BAD_TIME = 0x8009300C;

    //
    // MessageId: OSS_INDEFINITE_NOT_SUPPORTED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Unsupported BER indefinite-length encoding.
    //
    int OSS_INDEFINITE_NOT_SUPPORTED = 0x8009300D;

    //
    // MessageId: OSS_MEM_ERROR
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Access violation.
    //
    int OSS_MEM_ERROR = 0x8009300E;

    //
    // MessageId: OSS_BAD_TABLE
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_BAD_TABLE = 0x8009300F;

    //
    // MessageId: OSS_TOO_LONG
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_TOO_LONG = 0x80093010;

    //
    // MessageId: OSS_CONSTRAINT_VIOLATED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_CONSTRAINT_VIOLATED = 0x80093011;

    //
    // MessageId: OSS_FATAL_ERROR
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Internal Error.
    //
    int OSS_FATAL_ERROR = 0x80093012;

    //
    // MessageId: OSS_ACCESS_SERIALIZATION_ERROR
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Multi-threading conF;ict.
    //
    int OSS_ACCESS_SERIALIZATION_ERROR = 0x80093013;

    //
    // MessageId: OSS_NULL_TBL
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_NULL_TBL = 0x80093014;

    //
    // MessageId: OSS_NULL_FCN
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_NULL_FCN = 0x80093015;

    //
    // MessageId: OSS_BAD_ENCRULES
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_BAD_ENCRULES = 0x80093016;

    //
    // MessageId: OSS_UNAVAIL_ENCRULES
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Encode/Decode function not implemented.
    //
    int OSS_UNAVAIL_ENCRULES = 0x80093017;

    //
    // MessageId: OSS_CANT_OPEN_TRACE_WINDOW
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Trace file error.
    //
    int OSS_CANT_OPEN_TRACE_WINDOW = 0x80093018;

    //
    // MessageId: OSS_UNIMPLEMENTED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Function not implemented.
    //
    int OSS_UNIMPLEMENTED = 0x80093019;

    //
    // MessageId: OSS_OID_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_OID_DLL_NOT_LINKED = 0x8009301A;

    //
    // MessageId: OSS_CANT_OPEN_TRACE_FILE
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Trace file error.
    //
    int OSS_CANT_OPEN_TRACE_FILE = 0x8009301B;

    //
    // MessageId: OSS_TRACE_FILE_ALREADY_OPEN
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Trace file error.
    //
    int OSS_TRACE_FILE_ALREADY_OPEN = 0x8009301C;

    //
    // MessageId: OSS_TABLE_MISMATCH
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_TABLE_MISMATCH = 0x8009301D;

    //
    // MessageId: OSS_TYPE_NOT_SUPPORTED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Invalid data.
    //
    int OSS_TYPE_NOT_SUPPORTED = 0x8009301E;

    //
    // MessageId: OSS_REAL_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_REAL_DLL_NOT_LINKED = 0x8009301F;

    //
    // MessageId: OSS_REAL_CODE_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_REAL_CODE_NOT_LINKED = 0x80093020;

    //
    // MessageId: OSS_OUT_OF_RANGE
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_OUT_OF_RANGE = 0x80093021;

    //
    // MessageId: OSS_COPIER_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_COPIER_DLL_NOT_LINKED = 0x80093022;

    //
    // MessageId: OSS_CONSTRAINT_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_CONSTRAINT_DLL_NOT_LINKED = 0x80093023;

    //
    // MessageId: OSS_COMPARATOR_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_COMPARATOR_DLL_NOT_LINKED = 0x80093024;

    //
    // MessageId: OSS_COMPARATOR_CODE_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_COMPARATOR_CODE_NOT_LINKED = 0x80093025;

    //
    // MessageId: OSS_MEM_MGR_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_MEM_MGR_DLL_NOT_LINKED = 0x80093026;

    //
    // MessageId: OSS_PDV_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_PDV_DLL_NOT_LINKED = 0x80093027;

    //
    // MessageId: OSS_PDV_CODE_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_PDV_CODE_NOT_LINKED = 0x80093028;

    //
    // MessageId: OSS_API_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_API_DLL_NOT_LINKED = 0x80093029;

    //
    // MessageId: OSS_BERDER_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_BERDER_DLL_NOT_LINKED = 0x8009302A;

    //
    // MessageId: OSS_PER_DLL_NOT_LINKED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_PER_DLL_NOT_LINKED = 0x8009302B;

    //
    // MessageId: OSS_OPEN_TYPE_ERROR
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Program link error.
    //
    int OSS_OPEN_TYPE_ERROR = 0x8009302C;

    //
    // MessageId: OSS_MUTEX_NOT_CREATED
    //
    // MessageText:
    //
    // OSS ASN.1 Error: System resource error.
    //
    int OSS_MUTEX_NOT_CREATED = 0x8009302D;

    //
    // MessageId: OSS_CANT_CLOSE_TRACE_FILE
    //
    // MessageText:
    //
    // OSS ASN.1 Error: Trace file error.
    //
    int OSS_CANT_CLOSE_TRACE_FILE = 0x8009302E;

    //
    // MessageId: CRYPT_E_ASN1_ERROR
    //
    // MessageText:
    //
    // ASN1 Certificate encode/decode error code base.
    // 
    // The ASN1 error values are offset by CRYPT_E_ASN1_ERROR.
    //
    int CRYPT_E_ASN1_ERROR = 0x80093100;

    //
    // MessageId: CRYPT_E_ASN1_INTERNAL
    //
    // MessageText:
    //
    // ASN1 internal encode or decode error.
    //
    int CRYPT_E_ASN1_INTERNAL = 0x80093101;

    //
    // MessageId: CRYPT_E_ASN1_EOD
    //
    // MessageText:
    //
    // ASN1 unexpected end of data.
    //
    int CRYPT_E_ASN1_EOD = 0x80093102;

    //
    // MessageId: CRYPT_E_ASN1_CORRUPT
    //
    // MessageText:
    //
    // ASN1 corrupted data.
    //
    int CRYPT_E_ASN1_CORRUPT = 0x80093103;

    //
    // MessageId: CRYPT_E_ASN1_LARGE
    //
    // MessageText:
    //
    // ASN1 value too large.
    //
    int CRYPT_E_ASN1_LARGE = 0x80093104;

    //
    // MessageId: CRYPT_E_ASN1_CONSTRAINT
    //
    // MessageText:
    //
    // ASN1 constraint violated.
    //
    int CRYPT_E_ASN1_CONSTRAINT = 0x80093105;

    //
    // MessageId: CRYPT_E_ASN1_MEMORY
    //
    // MessageText:
    //
    // ASN1 out of memory.
    //
    int CRYPT_E_ASN1_MEMORY = 0x80093106;

    //
    // MessageId: CRYPT_E_ASN1_OVERF;OW
    //
    // MessageText:
    //
    // ASN1 buffer overF;ow.
    //
    int CRYPT_E_ASN1_OVERFLOW = 0x80093107;

    //
    // MessageId: CRYPT_E_ASN1_BADPDU
    //
    // MessageText:
    //
    // ASN1 function not supported for this PDU.
    //
    int CRYPT_E_ASN1_BADPDU = 0x80093108;

    //
    // MessageId: CRYPT_E_ASN1_BADARGS
    //
    // MessageText:
    //
    // ASN1 bad arguments to function call.
    //
    int CRYPT_E_ASN1_BADARGS = 0x80093109;

    //
    // MessageId: CRYPT_E_ASN1_BADREAL
    //
    // MessageText:
    //
    // ASN1 bad real value.
    //
    int CRYPT_E_ASN1_BADREAL = 0x8009310A;

    //
    // MessageId: CRYPT_E_ASN1_BADTAG
    //
    // MessageText:
    //
    // ASN1 bad tag value met.
    //
    int CRYPT_E_ASN1_BADTAG = 0x8009310B;

    //
    // MessageId: CRYPT_E_ASN1_CHOICE
    //
    // MessageText:
    //
    // ASN1 bad choice value.
    //
    int CRYPT_E_ASN1_CHOICE = 0x8009310C;

    //
    // MessageId: CRYPT_E_ASN1_RULE
    //
    // MessageText:
    //
    // ASN1 bad encoding rule.
    //
    int CRYPT_E_ASN1_RULE = 0x8009310D;

    //
    // MessageId: CRYPT_E_ASN1_UTF8
    //
    // MessageText:
    //
    // ASN1 bad unicode (UTF8).
    //
    int CRYPT_E_ASN1_UTF8 = 0x8009310E;

    //
    // MessageId: CRYPT_E_ASN1_PDU_TYPE
    //
    // MessageText:
    //
    // ASN1 bad PDU type.
    //
    int CRYPT_E_ASN1_PDU_TYPE = 0x80093133;

    //
    // MessageId: CRYPT_E_ASN1_NYI
    //
    // MessageText:
    //
    // ASN1 not yet implemented.
    //
    int CRYPT_E_ASN1_NYI = 0x80093134;

    //
    // MessageId: CRYPT_E_ASN1_EXTENDED
    //
    // MessageText:
    //
    // ASN1 skipped unknown extension(s).
    //
    int CRYPT_E_ASN1_EXTENDED = 0x80093201;

    //
    // MessageId: CRYPT_E_ASN1_NOEOD
    //
    // MessageText:
    //
    // ASN1 end of data expected
    //
    int CRYPT_E_ASN1_NOEOD = 0x80093202;

    //
    // MessageId: CERTSRV_E_BAD_REQUESTSUBJECT
    //
    // MessageText:
    //
    // The request subject name is invalid or too long.
    //
    int CERTSRV_E_BAD_REQUESTSUBJECT = 0x80094001;

    //
    // MessageId: CERTSRV_E_NO_REQUEST
    //
    // MessageText:
    //
    // The request does not exist.
    //
    int CERTSRV_E_NO_REQUEST = 0x80094002;

    //
    // MessageId: CERTSRV_E_BAD_REQUESTSTATUS
    //
    // MessageText:
    //
    // The request's current status does not allow this operation.
    //
    int CERTSRV_E_BAD_REQUESTSTATUS = 0x80094003;

    //
    // MessageId: CERTSRV_E_PROPERTY_EMPTY
    //
    // MessageText:
    //
    // The requested property value is empty.
    //
    int CERTSRV_E_PROPERTY_EMPTY = 0x80094004;

    //
    // MessageId: CERTSRV_E_INVALID_CA_CERTIFICATE
    //
    // MessageText:
    //
    // The certification authority's certificate contains invalid data.
    //
    int CERTSRV_E_INVALID_CA_CERTIFICATE = 0x80094005;

    //
    // MessageId: CERTSRV_E_SERVER_SUSPENDED
    //
    // MessageText:
    //
    // Certificate service has been suspended for a database restore operation.
    //
    int CERTSRV_E_SERVER_SUSPENDED = 0x80094006;

    //
    // MessageId: CERTSRV_E_ENCODING_LENGTH
    //
    // MessageText:
    //
    // The certificate contains an encoded length that is potentially
    // incompatible with older enrollment software.
    //
    int CERTSRV_E_ENCODING_LENGTH = 0x80094007;

    //
    // MessageId: CERTSRV_E_ROLECONF;ICT
    //
    // MessageText:
    //
    // The operation is denied. The user has multiple roles assigned and the
    // certification authority is configured to enforce role separation.
    //
    int CERTSRV_E_ROLECONFLICT = 0x80094008;

    //
    // MessageId: CERTSRV_E_RESTRICTEDOFFICER
    //
    // MessageText:
    //
    // The operation is denied. It can only be performed by a certificate
    // manager that is allowed to manage certificates for the current requester.
    //
    int CERTSRV_E_RESTRICTEDOFFICER = 0x80094009;

    //
    // MessageId: CERTSRV_E_KEY_ARCHIVAL_NOT_CONFIGURED
    //
    // MessageText:
    //
    // Cannot archive private key. The certification authority is not configured
    // for key archival.
    //
    int CERTSRV_E_KEY_ARCHIVAL_NOT_CONFIGURED = 0x8009400A;

    //
    // MessageId: CERTSRV_E_NO_VALID_KRA
    //
    // MessageText:
    //
    // Cannot archive private key. The certification authority could not verify
    // one or more key recovery certificates.
    //
    int CERTSRV_E_NO_VALID_KRA = 0x8009400B;

    //
    // MessageId: CERTSRV_E_BAD_REQUEST_KEY_ARCHIVAL
    //
    // MessageText:
    //
    // The request is incorrectly formatted. The encrypted private key must be
    // in an unauthenticated attribute in an outermost signature.
    //
    int CERTSRV_E_BAD_REQUEST_KEY_ARCHIVAL = 0x8009400C;

    //
    // MessageId: CERTSRV_E_NO_CAADMIN_DEFINED
    //
    // MessageText:
    //
    // At least one security principal must have the permission to manage this
    // CA.
    //
    int CERTSRV_E_NO_CAADMIN_DEFINED = 0x8009400D;

    //
    // MessageId: CERTSRV_E_BAD_RENEWAL_CERT_ATTRIBUTE
    //
    // MessageText:
    //
    // The request contains an invalid renewal certificate attribute.
    //
    int CERTSRV_E_BAD_RENEWAL_CERT_ATTRIBUTE = 0x8009400E;

    //
    // MessageId: CERTSRV_E_NO_DB_SESSIONS
    //
    // MessageText:
    //
    // An attempt was made to open a Certification Authority database session,
    // but there are already too many active sessions. The server may need to be
    // configured to allow additional sessions.
    //
    int CERTSRV_E_NO_DB_SESSIONS = 0x8009400F;

    //
    // MessageId: CERTSRV_E_ALIGNMENT_FAULT
    //
    // MessageText:
    //
    // A memory reference caused a data alignment fault.
    //
    int CERTSRV_E_ALIGNMENT_FAULT = 0x80094010;

    //
    // MessageId: CERTSRV_E_ENROLL_DENIED
    //
    // MessageText:
    //
    // The permissions on this certification authority do not allow the current
    // user to enroll for certificates.
    //
    int CERTSRV_E_ENROLL_DENIED = 0x80094011;

    //
    // MessageId: CERTSRV_E_TEMPLATE_DENIED
    //
    // MessageText:
    //
    // The permissions on the certificate template do not allow the current user
    // to enroll for this type of certificate.
    //
    int CERTSRV_E_TEMPLATE_DENIED = 0x80094012;

    //
    // MessageId: CERTSRV_E_DOWNLEVEL_DC_SSL_OR_UPGRADE
    //
    // MessageText:
    //
    // The contacted domain controller cannot support signed LDAP traffic.
    // Update the domain controller or configure Certificate Services to use SSL
    // for Active Directory access.
    //
    int CERTSRV_E_DOWNLEVEL_DC_SSL_OR_UPGRADE = 0x80094013;

    //
    // MessageId: CERTSRV_E_UNSUPPORTED_CERT_TYPE
    //
    // MessageText:
    //
    // The requested certificate template is not supported by this CA.
    //
    int CERTSRV_E_UNSUPPORTED_CERT_TYPE = 0x80094800;

    //
    // MessageId: CERTSRV_E_NO_CERT_TYPE
    //
    // MessageText:
    //
    // The request contains no certificate template information.
    //
    int CERTSRV_E_NO_CERT_TYPE = 0x80094801;

    //
    // MessageId: CERTSRV_E_TEMPLATE_CONF;ICT
    //
    // MessageText:
    //
    // The request contains conF;icting template information.
    //
    int CERTSRV_E_TEMPLATE_CONFLICT = 0x80094802;

    //
    // MessageId: CERTSRV_E_SUBJECT_ALT_NAME_REQUIRED
    //
    // MessageText:
    //
    // The request is missing a required Subject Alternate name extension.
    //
    int CERTSRV_E_SUBJECT_ALT_NAME_REQUIRED = 0x80094803;

    //
    // MessageId: CERTSRV_E_ARCHIVED_KEY_REQUIRED
    //
    // MessageText:
    //
    // The request is missing a required private key for archival by the server.
    //
    int CERTSRV_E_ARCHIVED_KEY_REQUIRED = 0x80094804;

    //
    // MessageId: CERTSRV_E_SMIME_REQUIRED
    //
    // MessageText:
    //
    // The request is missing a required SMIME capabilities extension.
    //
    int CERTSRV_E_SMIME_REQUIRED = 0x80094805;

    //
    // MessageId: CERTSRV_E_BAD_RENEWAL_SUBJECT
    //
    // MessageText:
    //
    // The request was made on behalf of a subject other than the caller. The
    // certificate template must be configured to require at least one signature
    // to authorize the request.
    //
    int CERTSRV_E_BAD_RENEWAL_SUBJECT = 0x80094806;

    //
    // MessageId: CERTSRV_E_BAD_TEMPLATE_VERSION
    //
    // MessageText:
    //
    // The request template version is newer than the supported template
    // version.
    //
    int CERTSRV_E_BAD_TEMPLATE_VERSION = 0x80094807;

    //
    // MessageId: CERTSRV_E_TEMPLATE_POLICY_REQUIRED
    //
    // MessageText:
    //
    // The template is missing a required signature policy attribute.
    //
    int CERTSRV_E_TEMPLATE_POLICY_REQUIRED = 0x80094808;

    //
    // MessageId: CERTSRV_E_SIGNATURE_POLICY_REQUIRED
    //
    // MessageText:
    //
    // The request is missing required signature policy information.
    //
    int CERTSRV_E_SIGNATURE_POLICY_REQUIRED = 0x80094809;

    //
    // MessageId: CERTSRV_E_SIGNATURE_COUNT
    //
    // MessageText:
    //
    // The request is missing one or more required signatures.
    //
    int CERTSRV_E_SIGNATURE_COUNT = 0x8009480A;

    //
    // MessageId: CERTSRV_E_SIGNATURE_REJECTED
    //
    // MessageText:
    //
    // One or more signatures did not include the required application or
    // issuance policies. The request is missing one or more required valid
    // signatures.
    //
    int CERTSRV_E_SIGNATURE_REJECTED = 0x8009480B;

    //
    // MessageId: CERTSRV_E_ISSUANCE_POLICY_REQUIRED
    //
    // MessageText:
    //
    // The request is missing one or more required signature issuance policies.
    //
    int CERTSRV_E_ISSUANCE_POLICY_REQUIRED = 0x8009480C;

    //
    // MessageId: CERTSRV_E_SUBJECT_UPN_REQUIRED
    //
    // MessageText:
    //
    // The UPN is unavailable and cannot be added to the Subject Alternate name.
    //
    int CERTSRV_E_SUBJECT_UPN_REQUIRED = 0x8009480D;

    //
    // MessageId: CERTSRV_E_SUBJECT_DIRECTORY_GUID_REQUIRED
    //
    // MessageText:
    //
    // The Active Directory GUID is unavailable and cannot be added to the
    // Subject Alternate name.
    //
    int CERTSRV_E_SUBJECT_DIRECTORY_GUID_REQUIRED = 0x8009480E;

    //
    // MessageId: CERTSRV_E_SUBJECT_DNS_REQUIRED
    //
    // MessageText:
    //
    // The DNS name is unavailable and cannot be added to the Subject Alternate
    // name.
    //
    int CERTSRV_E_SUBJECT_DNS_REQUIRED = 0x8009480F;

    //
    // MessageId: CERTSRV_E_ARCHIVED_KEY_UNEXPECTED
    //
    // MessageText:
    //
    // The request includes a private key for archival by the server, but key
    // archival is not enabled for the specified certificate template.
    //
    int CERTSRV_E_ARCHIVED_KEY_UNEXPECTED = 0x80094810;

    //
    // MessageId: CERTSRV_E_KEY_LENGTH
    //
    // MessageText:
    //
    // The public key does not meet the minimum size required by the specified
    // certificate template.
    //
    int CERTSRV_E_KEY_LENGTH = 0x80094811;

    //
    // MessageId: CERTSRV_E_SUBJECT_EMAIL_REQUIRED
    //
    // MessageText:
    //
    // The EMail name is unavailable and cannot be added to the Subject or
    // Subject Alternate name.
    //
    int CERTSRV_E_SUBJECT_EMAIL_REQUIRED = 0x80094812;

    //
    // MessageId: CERTSRV_E_UNKNOWN_CERT_TYPE
    //
    // MessageText:
    //
    // One or more certificate templates to be enabled on this certification
    // authority could not be found.
    //
    int CERTSRV_E_UNKNOWN_CERT_TYPE = 0x80094813;

    //
    // MessageId: CERTSRV_E_CERT_TYPE_OVERLAP
    //
    // MessageText:
    //
    // The certificate template renewal period is longer than the certificate
    // validity period. The template should be reconfigured or the CA
    // certificate renewed.
    //
    int CERTSRV_E_CERT_TYPE_OVERLAP = 0x80094814;

    //
    // MessageId: CERTSRV_E_TOO_MANY_SIGNATURES
    //
    // MessageText:
    //
    // The certificate template requires too many RA signatures. Only one RA
    // signature is allowed.
    //
    int CERTSRV_E_TOO_MANY_SIGNATURES = 0x80094815;

    //
    // The range 0x5000-0x51ff is reserved for XENROLL errors.
    //
    //
    // MessageId: XENROLL_E_KEY_NOT_EXPORTABLE
    //
    // MessageText:
    //
    // The key is not exportable.
    //
    int XENROLL_E_KEY_NOT_EXPORTABLE = 0x80095000;

    //
    // MessageId: XENROLL_E_CANNOT_ADD_ROOT_CERT
    //
    // MessageText:
    //
    // You cannot add the root CA certificate into your local store.
    //
    int XENROLL_E_CANNOT_ADD_ROOT_CERT = 0x80095001;

    //
    // MessageId: XENROLL_E_RESPONSE_KA_HASH_NOT_FOUND
    //
    // MessageText:
    //
    // The key archival hash attribute was not found in the response.
    //
    int XENROLL_E_RESPONSE_KA_HASH_NOT_FOUND = 0x80095002;

    //
    // MessageId: XENROLL_E_RESPONSE_UNEXPECTED_KA_HASH
    //
    // MessageText:
    //
    // An unexpected key archival hash attribute was found in the response.
    //
    int XENROLL_E_RESPONSE_UNEXPECTED_KA_HASH = 0x80095003;

    //
    // MessageId: XENROLL_E_RESPONSE_KA_HASH_MISMATCH
    //
    // MessageText:
    //
    // There is a key archival hash mismatch between the request and the
    // response.
    //
    int XENROLL_E_RESPONSE_KA_HASH_MISMATCH = 0x80095004;

    //
    // MessageId: XENROLL_E_KEYSPEC_SMIME_MISMATCH
    //
    // MessageText:
    //
    // Signing certificate cannot include SMIME extension.
    //
    int XENROLL_E_KEYSPEC_SMIME_MISMATCH = 0x80095005;

    //
    // MessageId: TRUST_E_SYSTEM_ERROR
    //
    // MessageText:
    //
    // A system-level error occurred while verifying trust.
    //
    int TRUST_E_SYSTEM_ERROR = 0x80096001;

    //
    // MessageId: TRUST_E_NO_SIGNER_CERT
    //
    // MessageText:
    //
    // The certificate for the signer of the message is invalid or not found.
    //
    int TRUST_E_NO_SIGNER_CERT = 0x80096002;

    //
    // MessageId: TRUST_E_COUNTER_SIGNER
    //
    // MessageText:
    //
    // One of the counter signatures was invalid.
    //
    int TRUST_E_COUNTER_SIGNER = 0x80096003;

    //
    // MessageId: TRUST_E_CERT_SIGNATURE
    //
    // MessageText:
    //
    // The signature of the certificate cannot be verified.
    //
    int TRUST_E_CERT_SIGNATURE = 0x80096004;

    //
    // MessageId: TRUST_E_TIME_STAMP
    //
    // MessageText:
    //
    // The timestamp signature and/or certificate could not be verified or is
    // malformed.
    //
    int TRUST_E_TIME_STAMP = 0x80096005;

    //
    // MessageId: TRUST_E_BAD_DIGEST
    //
    // MessageText:
    //
    // The digital signature of the object did not verify.
    //
    int TRUST_E_BAD_DIGEST = 0x80096010;

    //
    // MessageId: TRUST_E_BASIC_CONSTRAINTS
    //
    // MessageText:
    //
    // A certificate's basic constraint extension has not been observed.
    //
    int TRUST_E_BASIC_CONSTRAINTS = 0x80096019;

    //
    // MessageId: TRUST_E_FINANCIAL_CRITERIA
    //
    // MessageText:
    //
    // The certificate does not meet or contain the Authenticode(tm) financial
    // extensions.
    //
    int TRUST_E_FINANCIAL_CRITERIA = 0x8009601E;

    //
    // Error codes for mssipotf.dll
    // Most of the error codes can only occur when an error occurs
    // during font file signing
    //
    //
    //
    // MessageId: MSSIPOTF_E_OUTOFMEMRANGE
    //
    // MessageText:
    //
    // Tried to reference a part of the file outside the proper range.
    //
    int MSSIPOTF_E_OUTOFMEMRANGE = 0x80097001;

    //
    // MessageId: MSSIPOTF_E_CANTGETOBJECT
    //
    // MessageText:
    //
    // Could not retrieve an object from the file.
    //
    int MSSIPOTF_E_CANTGETOBJECT = 0x80097002;

    //
    // MessageId: MSSIPOTF_E_NOHEADTABLE
    //
    // MessageText:
    //
    // Could not find the head table in the file.
    //
    int MSSIPOTF_E_NOHEADTABLE = 0x80097003;

    //
    // MessageId: MSSIPOTF_E_BAD_MAGICNUMBER
    //
    // MessageText:
    //
    // The magic number in the head table is incorrect.
    //
    int MSSIPOTF_E_BAD_MAGICNUMBER = 0x80097004;

    //
    // MessageId: MSSIPOTF_E_BAD_OFFSET_TABLE
    //
    // MessageText:
    //
    // The offset table has incorrect values.
    //
    int MSSIPOTF_E_BAD_OFFSET_TABLE = 0x80097005;

    //
    // MessageId: MSSIPOTF_E_TABLE_TAGORDER
    //
    // MessageText:
    //
    // Duplicate table tags or tags out of alphabetical order.
    //
    int MSSIPOTF_E_TABLE_TAGORDER = 0x80097006;

    //
    // MessageId: MSSIPOTF_E_TABLE_LONGWORD
    //
    // MessageText:
    //
    // A table does not start on a long word boundary.
    //
    int MSSIPOTF_E_TABLE_LONGWORD = 0x80097007;

    //
    // MessageId: MSSIPOTF_E_BAD_FIRST_TABLE_PLACEMENT
    //
    // MessageText:
    //
    // First table does not appear after header information.
    //
    int MSSIPOTF_E_BAD_FIRST_TABLE_PLACEMENT = 0x80097008;

    //
    // MessageId: MSSIPOTF_E_TABLES_OVERLAP
    //
    // MessageText:
    //
    // Two or more tables overlap.
    //
    int MSSIPOTF_E_TABLES_OVERLAP = 0x80097009;

    //
    // MessageId: MSSIPOTF_E_TABLE_PADBYTES
    //
    // MessageText:
    //
    // Too many pad bytes between tables or pad bytes are not 0.
    //
    int MSSIPOTF_E_TABLE_PADBYTES = 0x8009700A;

    //
    // MessageId: MSSIPOTF_E_FILETOOSMALL
    //
    // MessageText:
    //
    // File is too small to contain the last table.
    //
    int MSSIPOTF_E_FILETOOSMALL = 0x8009700B;

    //
    // MessageId: MSSIPOTF_E_TABLE_CHECKSUM
    //
    // MessageText:
    //
    // A table checksum is incorrect.
    //
    int MSSIPOTF_E_TABLE_CHECKSUM = 0x8009700C;

    //
    // MessageId: MSSIPOTF_E_FILE_CHECKSUM
    //
    // MessageText:
    //
    // The file checksum is incorrect.
    //
    int MSSIPOTF_E_FILE_CHECKSUM = 0x8009700D;

    //
    // MessageId: MSSIPOTF_E_FAILED_POLICY
    //
    // MessageText:
    //
    // The signature does not have the correct attributes for the policy.
    //
    int MSSIPOTF_E_FAILED_POLICY = 0x80097010;

    //
    // MessageId: MSSIPOTF_E_FAILED_HINTS_CHECK
    //
    // MessageText:
    //
    // The file did not pass the hints check.
    //
    int MSSIPOTF_E_FAILED_HINTS_CHECK = 0x80097011;

    //
    // MessageId: MSSIPOTF_E_NOT_OPENTYPE
    //
    // MessageText:
    //
    // The file is not an OpenType file.
    //
    int MSSIPOTF_E_NOT_OPENTYPE = 0x80097012;

    //
    // MessageId: MSSIPOTF_E_FILE
    //
    // MessageText:
    //
    // Failed on a file operation (open, map, read, write).
    //
    int MSSIPOTF_E_FILE = 0x80097013;

    //
    // MessageId: MSSIPOTF_E_CRYPT
    //
    // MessageText:
    //
    // A call to a CryptoAPI function failed.
    //
    int MSSIPOTF_E_CRYPT = 0x80097014;

    //
    // MessageId: MSSIPOTF_E_BADVERSION
    //
    // MessageText:
    //
    // There is a bad version number in the file.
    //
    int MSSIPOTF_E_BADVERSION = 0x80097015;

    //
    // MessageId: MSSIPOTF_E_DSIG_STRUCTURE
    //
    // MessageText:
    //
    // The structure of the DSIG table is incorrect.
    //
    int MSSIPOTF_E_DSIG_STRUCTURE = 0x80097016;

    //
    // MessageId: MSSIPOTF_E_PCONST_CHECK
    //
    // MessageText:
    //
    // A check failed in a partially constant table.
    //
    int MSSIPOTF_E_PCONST_CHECK = 0x80097017;

    //
    // MessageId: MSSIPOTF_E_STRUCTURE
    //
    // MessageText:
    //
    // Some kind of structural error.
    //
    int MSSIPOTF_E_STRUCTURE = 0x80097018;

    //
    // MessageId: ERROR_CRED_REQUIRES_CONFIRMATION
    //
    // MessageText:
    //
    // The requested credential requires confirmation.
    //
    int ERROR_CRED_REQUIRES_CONFIRMATION = 0x80097019;

    int NTE_OP_OK = 0;

    //
    // Note that additional FACILITY_SSPI errors are in issperr.h
    //
    // ******************
    // FACILITY_CERT
    // ******************
    //
    // MessageId: TRUST_E_PROVIDER_UNKNOWN
    //
    // MessageText:
    //
    // Unknown trust provider.
    //
    int TRUST_E_PROVIDER_UNKNOWN = 0x800B0001;

    //
    // MessageId: TRUST_E_ACTION_UNKNOWN
    //
    // MessageText:
    //
    // The trust verification action specified is not supported by the specified
    // trust provider.
    //
    int TRUST_E_ACTION_UNKNOWN = 0x800B0002;

    //
    // MessageId: TRUST_E_SUBJECT_FORM_UNKNOWN
    //
    // MessageText:
    //
    // The form specified for the subject is not one supported or known by the
    // specified trust provider.
    //
    int TRUST_E_SUBJECT_FORM_UNKNOWN = 0x800B0003;

    //
    // MessageId: TRUST_E_SUBJECT_NOT_TRUSTED
    //
    // MessageText:
    //
    // The subject is not trusted for the specified action.
    //
    int TRUST_E_SUBJECT_NOT_TRUSTED = 0x800B0004;

    //
    // MessageId: DIGSIG_E_ENCODE
    //
    // MessageText:
    //
    // Error due to problem in ASN.1 encoding process.
    //
    int DIGSIG_E_ENCODE = 0x800B0005;

    //
    // MessageId: DIGSIG_E_DECODE
    //
    // MessageText:
    //
    // Error due to problem in ASN.1 decoding process.
    //
    int DIGSIG_E_DECODE = 0x800B0006;

    //
    // MessageId: DIGSIG_E_EXTENSIBILITY
    //
    // MessageText:
    //
    // Reading / writing Extensions where Attributes are appropriate, and visa
    // versa.
    //
    int DIGSIG_E_EXTENSIBILITY = 0x800B0007;

    //
    // MessageId: DIGSIG_E_CRYPTO
    //
    // MessageText:
    //
    // Unspecified cryptographic failure.
    //
    int DIGSIG_E_CRYPTO = 0x800B0008;

    //
    // MessageId: PERSIST_E_SIZEDEFINITE
    //
    // MessageText:
    //
    // The size of the data could not be determined.
    //
    int PERSIST_E_SIZEDEFINITE = 0x800B0009;

    //
    // MessageId: PERSIST_E_SIZEINDEFINITE
    //
    // MessageText:
    //
    // The size of the indefinite-sized data could not be determined.
    //
    int PERSIST_E_SIZEINDEFINITE = 0x800B000A;

    //
    // MessageId: PERSIST_E_NOTSELFSIZING
    //
    // MessageText:
    //
    // This object does not read and write self-sizing data.
    //
    int PERSIST_E_NOTSELFSIZING = 0x800B000B;

    //
    // MessageId: TRUST_E_NOSIGNATURE
    //
    // MessageText:
    //
    // No signature was present in the subject.
    //
    int TRUST_E_NOSIGNATURE = 0x800B0100;

    //
    // MessageId: CERT_E_EXPIRED
    //
    // MessageText:
    //
    // A required certificate is not within its validity period when verifying
    // against the current system clock or the timestamp in the signed file.
    //
    int CERT_E_EXPIRED = 0x800B0101;

    //
    // MessageId: CERT_E_VALIDITYPERIODNESTING
    //
    // MessageText:
    //
    // The validity periods of the certification chain do not nest correctly.
    //
    int CERT_E_VALIDITYPERIODNESTING = 0x800B0102;

    //
    // MessageId: CERT_E_ROLE
    //
    // MessageText:
    //
    // A certificate that can only be used as an end-entity is being used as a
    // CA or visa versa.
    //
    int CERT_E_ROLE = 0x800B0103;

    //
    // MessageId: CERT_E_PATHLENCONST
    //
    // MessageText:
    //
    // A path length constraint in the certification chain has been violated.
    //
    int CERT_E_PATHLENCONST = 0x800B0104;

    //
    // MessageId: CERT_E_CRITICAL
    //
    // MessageText:
    //
    // A certificate contains an unknown extension that is marked 'critical'.
    //
    int CERT_E_CRITICAL = 0x800B0105;

    //
    // MessageId: CERT_E_PURPOSE
    //
    // MessageText:
    //
    // A certificate being used for a purpose other than the ones specified by
    // its CA.
    //
    int CERT_E_PURPOSE = 0x800B0106;

    //
    // MessageId: CERT_E_ISSUERCHAINING
    //
    // MessageText:
    //
    // A parent of a given certificate in fact did not issue that child
    // certificate.
    //
    int CERT_E_ISSUERCHAINING = 0x800B0107;

    //
    // MessageId: CERT_E_MALFORMED
    //
    // MessageText:
    //
    // A certificate is missing or has an empty value for an important field,
    // such as a subject or issuer name.
    //
    int CERT_E_MALFORMED = 0x800B0108;

    //
    // MessageId: CERT_E_UNTRUSTEDROOT
    //
    // MessageText:
    //
    // A certificate chain processed, but terminated in a root certificate which
    // is not trusted by the trust provider.
    //
    int CERT_E_UNTRUSTEDROOT = 0x800B0109;

    //
    // MessageId: CERT_E_CHAINING
    //
    // MessageText:
    //
    // A certificate chain could not be built to a trusted root authority.
    //
    int CERT_E_CHAINING = 0x800B010A;

    //
    // MessageId: TRUST_E_FAIL
    //
    // MessageText:
    //
    // Generic trust failure.
    //
    int TRUST_E_FAIL = 0x800B010B;

    //
    // MessageId: CERT_E_REVOKED
    //
    // MessageText:
    //
    // A certificate was explicitly revoked by its issuer.
    //
    int CERT_E_REVOKED = 0x800B010C;

    //
    // MessageId: CERT_E_UNTRUSTEDTESTROOT
    //
    // MessageText:
    //
    // The certification path terminates with the test root which is not trusted
    // with the current policy settings.
    //
    int CERT_E_UNTRUSTEDTESTROOT = 0x800B010D;

    //
    // MessageId: CERT_E_REVOCATION_FAILURE
    //
    // MessageText:
    //
    // The revocation process could not continue - the certificate(s) could not
    // be checked.
    //
    int CERT_E_REVOCATION_FAILURE = 0x800B010E;

    //
    // MessageId: CERT_E_CN_NO_MATCH
    //
    // MessageText:
    //
    // The certificate's CN name does not match the passed value.
    //
    int CERT_E_CN_NO_MATCH = 0x800B010F;

    //
    // MessageId: CERT_E_WRONG_USAGE
    //
    // MessageText:
    //
    // The certificate is not valid for the requested usage.
    //
    int CERT_E_WRONG_USAGE = 0x800B0110;

    //
    // MessageId: TRUST_E_EXPLICIT_DISTRUST
    //
    // MessageText:
    //
    // The certificate was explicitly marked as untrusted by the user.
    //
    int TRUST_E_EXPLICIT_DISTRUST = 0x800B0111;

    //
    // MessageId: CERT_E_UNTRUSTEDCA
    //
    // MessageText:
    //
    // A certification chain processed correctly, but one of the CA certificates
    // is not trusted by the policy provider.
    //
    int CERT_E_UNTRUSTEDCA = 0x800B0112;

    //
    // MessageId: CERT_E_INVALID_POLICY
    //
    // MessageText:
    //
    // The certificate has invalid policy.
    //
    int CERT_E_INVALID_POLICY = 0x800B0113;

    //
    // MessageId: CERT_E_INVALID_NAME
    //
    // MessageText:
    //
    // The certificate has an invalid name. The name is not included in the
    // permitted list or is explicitly excluded.
    //
    int CERT_E_INVALID_NAME = 0x800B0114;

    // *****************
    // FACILITY_SETUPAPI
    // *****************
    //
    // Since these error codes aren't in the standard Win32 range (i.e., 0-64K),
    // define a
    // macro to map either Win32 or SetupAPI error codes into an HRESULT.
    //
    // MessageId: SPAPI_E_EXPECTED_SECTION_NAME
    //
    // MessageText:
    //
    // A non-empty line was encountered in the INF before the start of a
    // section.
    //
    int SPAPI_E_EXPECTED_SECTION_NAME = 0x800F0000;

    //
    // MessageId: SPAPI_E_BAD_SECTION_NAME_LINE
    //
    // MessageText:
    //
    // A section name marker in the INF is not complete, or does not exist on a
    // line by itself.
    //
    int SPAPI_E_BAD_SECTION_NAME_LINE = 0x800F0001;

    //
    // MessageId: SPAPI_E_SECTION_NAME_TOO_LONG
    //
    // MessageText:
    //
    // An INF section was encountered whose name exceeds the maximum section
    // name length.
    //
    int SPAPI_E_SECTION_NAME_TOO_LONG = 0x800F0002;

    //
    // MessageId: SPAPI_E_GENERAL_SYNTAX
    //
    // MessageText:
    //
    // The syntax of the INF is invalid.
    //
    int SPAPI_E_GENERAL_SYNTAX = 0x800F0003;

    //
    // MessageId: SPAPI_E_WRONG_INF_STYLE
    //
    // MessageText:
    //
    // The style of the INF is different than what was requested.
    //
    int SPAPI_E_WRONG_INF_STYLE = 0x800F0100;

    //
    // MessageId: SPAPI_E_SECTION_NOT_FOUND
    //
    // MessageText:
    //
    // The required section was not found in the INF.
    //
    int SPAPI_E_SECTION_NOT_FOUND = 0x800F0101;

    //
    // MessageId: SPAPI_E_LINE_NOT_FOUND
    //
    // MessageText:
    //
    // The required line was not found in the INF.
    //
    int SPAPI_E_LINE_NOT_FOUND = 0x800F0102;

    //
    // MessageId: SPAPI_E_NO_BACKUP
    //
    // MessageText:
    //
    // The files affected by the installation of this file queue have not been
    // backed up for uninstall.
    //
    int SPAPI_E_NO_BACKUP = 0x800F0103;

    //
    // MessageId: SPAPI_E_NO_ASSOCIATED_CLASS
    //
    // MessageText:
    //
    // The INF or the device information set or element does not have an
    // associated install class.
    //
    int SPAPI_E_NO_ASSOCIATED_CLASS = 0x800F0200;

    //
    // MessageId: SPAPI_E_CLASS_MISMATCH
    //
    // MessageText:
    //
    // The INF or the device information set or element does not match the
    // specified install class.
    //
    int SPAPI_E_CLASS_MISMATCH = 0x800F0201;

    //
    // MessageId: SPAPI_E_DUPLICATE_FOUND
    //
    // MessageText:
    //
    // An existing device was found that is a duplicate of the device being
    // manually installed.
    //
    int SPAPI_E_DUPLICATE_FOUND = 0x800F0202;

    //
    // MessageId: SPAPI_E_NO_DRIVER_SELECTED
    //
    // MessageText:
    //
    // There is no driver selected for the device information set or element.
    //
    int SPAPI_E_NO_DRIVER_SELECTED = 0x800F0203;

    //
    // MessageId: SPAPI_E_KEY_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // The requested device registry key does not exist.
    //
    int SPAPI_E_KEY_DOES_NOT_EXIST = 0x800F0204;

    //
    // MessageId: SPAPI_E_INVALID_DEVINST_NAME
    //
    // MessageText:
    //
    // The device instance name is invalid.
    //
    int SPAPI_E_INVALID_DEVINST_NAME = 0x800F0205;

    //
    // MessageId: SPAPI_E_INVALID_CLASS
    //
    // MessageText:
    //
    // The install class is not present or is invalid.
    //
    int SPAPI_E_INVALID_CLASS = 0x800F0206;

    //
    // MessageId: SPAPI_E_DEVINST_ALREADY_EXISTS
    //
    // MessageText:
    //
    // The device instance cannot be created because it already exists.
    //
    int SPAPI_E_DEVINST_ALREADY_EXISTS = 0x800F0207;

    //
    // MessageId: SPAPI_E_DEVINFO_NOT_REGISTERED
    //
    // MessageText:
    //
    // The operation cannot be performed on a device information element that
    // has not been registered.
    //
    int SPAPI_E_DEVINFO_NOT_REGISTERED = 0x800F0208;

    //
    // MessageId: SPAPI_E_INVALID_REG_PROPERTY
    //
    // MessageText:
    //
    // The device property code is invalid.
    //
    int SPAPI_E_INVALID_REG_PROPERTY = 0x800F0209;

    //
    // MessageId: SPAPI_E_NO_INF
    //
    // MessageText:
    //
    // The INF from which a driver list is to be built does not exist.
    //
    int SPAPI_E_NO_INF = 0x800F020A;

    //
    // MessageId: SPAPI_E_NO_SUCH_DEVINST
    //
    // MessageText:
    //
    // The device instance does not exist in the hardware tree.
    //
    int SPAPI_E_NO_SUCH_DEVINST = 0x800F020B;

    //
    // MessageId: SPAPI_E_CANT_LOAD_CLASS_ICON
    //
    // MessageText:
    //
    // The icon representing this install class cannot be loaded.
    //
    int SPAPI_E_CANT_LOAD_CLASS_ICON = 0x800F020C;

    //
    // MessageId: SPAPI_E_INVALID_CLASS_INSTALLER
    //
    // MessageText:
    //
    // The class installer registry entry is invalid.
    //
    int SPAPI_E_INVALID_CLASS_INSTALLER = 0x800F020D;

    //
    // MessageId: SPAPI_E_DI_DO_DEFAULT
    //
    // MessageText:
    //
    // The class installer has indicated that the default action should be
    // performed for this installation request.
    //
    int SPAPI_E_DI_DO_DEFAULT = 0x800F020E;

    //
    // MessageId: SPAPI_E_DI_NOFILECOPY
    //
    // MessageText:
    //
    // The operation does not require any files to be copied.
    //
    int SPAPI_E_DI_NOFILECOPY = 0x800F020F;

    //
    // MessageId: SPAPI_E_INVALID_HWPROFILE
    //
    // MessageText:
    //
    // The specified hardware profile does not exist.
    //
    int SPAPI_E_INVALID_HWPROFILE = 0x800F0210;

    //
    // MessageId: SPAPI_E_NO_DEVICE_SELECTED
    //
    // MessageText:
    //
    // There is no device information element currently selected for this device
    // information set.
    //
    int SPAPI_E_NO_DEVICE_SELECTED = 0x800F0211;

    //
    // MessageId: SPAPI_E_DEVINFO_LIST_LOCKED
    //
    // MessageText:
    //
    // The operation cannot be performed because the device information set is
    // locked.
    //
    int SPAPI_E_DEVINFO_LIST_LOCKED = 0x800F0212;

    //
    // MessageId: SPAPI_E_DEVINFO_DATA_LOCKED
    //
    // MessageText:
    //
    // The operation cannot be performed because the device information element
    // is locked.
    //
    int SPAPI_E_DEVINFO_DATA_LOCKED = 0x800F0213;

    //
    // MessageId: SPAPI_E_DI_BAD_PATH
    //
    // MessageText:
    //
    // The specified path does not contain any applicable device INFs.
    //
    int SPAPI_E_DI_BAD_PATH = 0x800F0214;

    //
    // MessageId: SPAPI_E_NO_CLASSINSTALL_PARAMS
    //
    // MessageText:
    //
    // No class installer parameters have been set for the device information
    // set or element.
    //
    int SPAPI_E_NO_CLASSINSTALL_PARAMS = 0x800F0215;

    //
    // MessageId: SPAPI_E_FILEQUEUE_LOCKED
    //
    // MessageText:
    //
    // The operation cannot be performed because the file queue is locked.
    //
    int SPAPI_E_FILEQUEUE_LOCKED = 0x800F0216;

    //
    // MessageId: SPAPI_E_BAD_SERVICE_INSTALLSECT
    //
    // MessageText:
    //
    // A service installation section in this INF is invalid.
    //
    int SPAPI_E_BAD_SERVICE_INSTALLSECT = 0x800F0217;

    //
    // MessageId: SPAPI_E_NO_CLASS_DRIVER_LIST
    //
    // MessageText:
    //
    // There is no class driver list for the device information element.
    //
    int SPAPI_E_NO_CLASS_DRIVER_LIST = 0x800F0218;

    //
    // MessageId: SPAPI_E_NO_ASSOCIATED_SERVICE
    //
    // MessageText:
    //
    // The installation failed because a function driver was not specified for
    // this device instance.
    //
    int SPAPI_E_NO_ASSOCIATED_SERVICE = 0x800F0219;

    //
    // MessageId: SPAPI_E_NO_DEFAULT_DEVICE_INTERFACE
    //
    // MessageText:
    //
    // There is presently no default device interface designated for this
    // interface class.
    //
    int SPAPI_E_NO_DEFAULT_DEVICE_INTERFACE = 0x800F021A;

    //
    // MessageId: SPAPI_E_DEVICE_INTERFACE_ACTIVE
    //
    // MessageText:
    //
    // The operation cannot be performed because the device interface is
    // currently active.
    //
    int SPAPI_E_DEVICE_INTERFACE_ACTIVE = 0x800F021B;

    //
    // MessageId: SPAPI_E_DEVICE_INTERFACE_REMOVED
    //
    // MessageText:
    //
    // The operation cannot be performed because the device interface has been
    // removed from the system.
    //
    int SPAPI_E_DEVICE_INTERFACE_REMOVED = 0x800F021C;

    //
    // MessageId: SPAPI_E_BAD_INTERFACE_INSTALLSECT
    //
    // MessageText:
    //
    // An interface installation section in this INF is invalid.
    //
    int SPAPI_E_BAD_INTERFACE_INSTALLSECT = 0x800F021D;

    //
    // MessageId: SPAPI_E_NO_SUCH_INTERFACE_CLASS
    //
    // MessageText:
    //
    // This interface class does not exist in the system.
    //
    int SPAPI_E_NO_SUCH_INTERFACE_CLASS = 0x800F021E;

    //
    // MessageId: SPAPI_E_INVALID_REFERENCE_STRING
    //
    // MessageText:
    //
    // The reference string supplied for this interface device is invalid.
    //
    int SPAPI_E_INVALID_REFERENCE_STRING = 0x800F021F;

    //
    // MessageId: SPAPI_E_INVALID_MACHINENAME
    //
    // MessageText:
    //
    // The specified machine name does not conform to UNC naming conventions.
    //
    int SPAPI_E_INVALID_MACHINENAME = 0x800F0220;

    //
    // MessageId: SPAPI_E_REMOTE_COMM_FAILURE
    //
    // MessageText:
    //
    // A general remote communication error occurred.
    //
    int SPAPI_E_REMOTE_COMM_FAILURE = 0x800F0221;

    //
    // MessageId: SPAPI_E_MACHINE_UNAVAILABLE
    //
    // MessageText:
    //
    // The machine selected for remote communication is not available at this
    // time.
    //
    int SPAPI_E_MACHINE_UNAVAILABLE = 0x800F0222;

    //
    // MessageId: SPAPI_E_NO_CONFIGMGR_SERVICES
    //
    // MessageText:
    //
    // The Plug and Play service is not available on the remote machine.
    //
    int SPAPI_E_NO_CONFIGMGR_SERVICES = 0x800F0223;

    //
    // MessageId: SPAPI_E_INVALID_PROPPAGE_PROVIDER
    //
    // MessageText:
    //
    // The property page provider registry entry is invalid.
    //
    int SPAPI_E_INVALID_PROPPAGE_PROVIDER = 0x800F0224;

    //
    // MessageId: SPAPI_E_NO_SUCH_DEVICE_INTERFACE
    //
    // MessageText:
    //
    // The requested device interface is not present in the system.
    //
    int SPAPI_E_NO_SUCH_DEVICE_INTERFACE = 0x800F0225;

    //
    // MessageId: SPAPI_E_DI_POSTPROCESSING_REQUIRED
    //
    // MessageText:
    //
    // The device's co-installer has additional work to perform after
    // installation is complete.
    //
    int SPAPI_E_DI_POSTPROCESSING_REQUIRED = 0x800F0226;

    //
    // MessageId: SPAPI_E_INVALID_COINSTALLER
    //
    // MessageText:
    //
    // The device's co-installer is invalid.
    //
    int SPAPI_E_INVALID_COINSTALLER = 0x800F0227;

    //
    // MessageId: SPAPI_E_NO_COMPAT_DRIVERS
    //
    // MessageText:
    //
    // There are no compatible drivers for this device.
    //
    int SPAPI_E_NO_COMPAT_DRIVERS = 0x800F0228;

    //
    // MessageId: SPAPI_E_NO_DEVICE_ICON
    //
    // MessageText:
    //
    // There is no icon that represents this device or device type.
    //
    int SPAPI_E_NO_DEVICE_ICON = 0x800F0229;

    //
    // MessageId: SPAPI_E_INVALID_INF_LOGCONFIG
    //
    // MessageText:
    //
    // A logical configuration specified in this INF is invalid.
    //
    int SPAPI_E_INVALID_INF_LOGCONFIG = 0x800F022A;

    //
    // MessageId: SPAPI_E_DI_DONT_INSTALL
    //
    // MessageText:
    //
    // The class installer has denied the request to install or upgrade this
    // device.
    //
    int SPAPI_E_DI_DONT_INSTALL = 0x800F022B;

    //
    // MessageId: SPAPI_E_INVALID_FILTER_DRIVER
    //
    // MessageText:
    //
    // One of the filter drivers installed for this device is invalid.
    //
    int SPAPI_E_INVALID_FILTER_DRIVER = 0x800F022C;

    //
    // MessageId: SPAPI_E_NON_WINDOWS_NT_DRIVER
    //
    // MessageText:
    //
    // The driver selected for this device does not support Windows Vista.
    //
    int SPAPI_E_NON_WINDOWS_NT_DRIVER = 0x800F022D;

    //
    // MessageId: SPAPI_E_NON_WINDOWS_DRIVER
    //
    // MessageText:
    //
    // The driver selected for this device does not support Windows.
    //
    int SPAPI_E_NON_WINDOWS_DRIVER = 0x800F022E;

    //
    // MessageId: SPAPI_E_NO_CATALOG_FOR_OEM_INF
    //
    // MessageText:
    //
    // The third-party INF does not contain digital signature information.
    //
    int SPAPI_E_NO_CATALOG_FOR_OEM_INF = 0x800F022F;

    //
    // MessageId: SPAPI_E_DEVINSTALL_QUEUE_NONNATIVE
    //
    // MessageText:
    //
    // An invalid attempt was made to use a device installation file queue for
    // verification of digital signatures relative to other platforms.
    //
    int SPAPI_E_DEVINSTALL_QUEUE_NONNATIVE = 0x800F0230;

    //
    // MessageId: SPAPI_E_NOT_DISABLEABLE
    //
    // MessageText:
    //
    // The device cannot be disabled.
    //
    int SPAPI_E_NOT_DISABLEABLE = 0x800F0231;

    //
    // MessageId: SPAPI_E_CANT_REMOVE_DEVINST
    //
    // MessageText:
    //
    // The device could not be dynamically removed.
    //
    int SPAPI_E_CANT_REMOVE_DEVINST = 0x800F0232;

    //
    // MessageId: SPAPI_E_INVALID_TARGET
    //
    // MessageText:
    //
    // Cannot copy to specified target.
    //
    int SPAPI_E_INVALID_TARGET = 0x800F0233;

    //
    // MessageId: SPAPI_E_DRIVER_NONNATIVE
    //
    // MessageText:
    //
    // Driver is not intended for this platform.
    //
    int SPAPI_E_DRIVER_NONNATIVE = 0x800F0234;

    //
    // MessageId: SPAPI_E_IN_WOW64
    //
    // MessageText:
    //
    // Operation not allowed in WOW64.
    //
    int SPAPI_E_IN_WOW64 = 0x800F0235;

    //
    // MessageId: SPAPI_E_SET_SYSTEM_RESTORE_POINT
    //
    // MessageText:
    //
    // The operation involving unsigned file copying was rolled back, so that a
    // system restore point could be set.
    //
    int SPAPI_E_SET_SYSTEM_RESTORE_POINT = 0x800F0236;

    //
    // MessageId: SPAPI_E_INCORRECTLY_COPIED_INF
    //
    // MessageText:
    //
    // An INF was copied into the Windows INF directory in an improper manner.
    //
    int SPAPI_E_INCORRECTLY_COPIED_INF = 0x800F0237;

    //
    // MessageId: SPAPI_E_SCE_DISABLED
    //
    // MessageText:
    //
    // The Security Configuration Editor (SCE) APIs have been disabled on this
    // Embedded product.
    //
    int SPAPI_E_SCE_DISABLED = 0x800F0238;

    //
    // MessageId: SPAPI_E_UNKNOWN_EXCEPTION
    //
    // MessageText:
    //
    // An unknown exception was encountered.
    //
    int SPAPI_E_UNKNOWN_EXCEPTION = 0x800F0239;

    //
    // MessageId: SPAPI_E_PNP_REGISTRY_ERROR
    //
    // MessageText:
    //
    // A problem was encountered when accessing the Plug and Play registry
    // database.
    //
    int SPAPI_E_PNP_REGISTRY_ERROR = 0x800F023A;

    //
    // MessageId: SPAPI_E_REMOTE_REQUEST_UNSUPPORTED
    //
    // MessageText:
    //
    // The requested operation is not supported for a remote machine.
    //
    int SPAPI_E_REMOTE_REQUEST_UNSUPPORTED = 0x800F023B;

    //
    // MessageId: SPAPI_E_NOT_AN_INSTALLED_OEM_INF
    //
    // MessageText:
    //
    // The specified file is not an installed OEM INF.
    //
    int SPAPI_E_NOT_AN_INSTALLED_OEM_INF = 0x800F023C;

    //
    // MessageId: SPAPI_E_INF_IN_USE_BY_DEVICES
    //
    // MessageText:
    //
    // One or more devices are presently installed using the specified INF.
    //
    int SPAPI_E_INF_IN_USE_BY_DEVICES = 0x800F023D;

    //
    // MessageId: SPAPI_E_DI_FUNCTION_OBSOLETE
    //
    // MessageText:
    //
    // The requested device install operation is obsolete.
    //
    int SPAPI_E_DI_FUNCTION_OBSOLETE = 0x800F023E;

    //
    // MessageId: SPAPI_E_NO_AUTHENTICODE_CATALOG
    //
    // MessageText:
    //
    // A file could not be verified because it does not have an associated
    // catalog signed via Authenticode(tm).
    //
    int SPAPI_E_NO_AUTHENTICODE_CATALOG = 0x800F023F;

    //
    // MessageId: SPAPI_E_AUTHENTICODE_DISALLOWED
    //
    // MessageText:
    //
    // Authenticode(tm) signature verification is not supported for the
    // specified INF.
    //
    int SPAPI_E_AUTHENTICODE_DISALLOWED = 0x800F0240;

    //
    // MessageId: SPAPI_E_AUTHENTICODE_TRUSTED_PUBLISHER
    //
    // MessageText:
    //
    // The INF was signed with an Authenticode(tm) catalog from a trusted
    // publisher.
    //
    int SPAPI_E_AUTHENTICODE_TRUSTED_PUBLISHER = 0x800F0241;

    //
    // MessageId: SPAPI_E_AUTHENTICODE_TRUST_NOT_ESTABLISHED
    //
    // MessageText:
    //
    // The publisher of an Authenticode(tm) signed catalog has not yet been
    // established as trusted.
    //
    int SPAPI_E_AUTHENTICODE_TRUST_NOT_ESTABLISHED = 0x800F0242;

    //
    // MessageId: SPAPI_E_AUTHENTICODE_PUBLISHER_NOT_TRUSTED
    //
    // MessageText:
    //
    // The publisher of an Authenticode(tm) signed catalog was not established
    // as trusted.
    //
    int SPAPI_E_AUTHENTICODE_PUBLISHER_NOT_TRUSTED = 0x800F0243;

    //
    // MessageId: SPAPI_E_SIGNATURE_OSATTRIBUTE_MISMATCH
    //
    // MessageText:
    //
    // The software was tested for compliance with Windows Logo requirements on
    // a different version of Windows, and may not be compatible with this
    // version.
    //
    int SPAPI_E_SIGNATURE_OSATTRIBUTE_MISMATCH = 0x800F0244;

    //
    // MessageId: SPAPI_E_ONLY_VALIDATE_VIA_AUTHENTICODE
    //
    // MessageText:
    //
    // The file may only be validated by a catalog signed via Authenticode(tm).
    //
    int SPAPI_E_ONLY_VALIDATE_VIA_AUTHENTICODE = 0x800F0245;

    //
    // MessageId: SPAPI_E_DEVICE_INSTALLER_NOT_READY
    //
    // MessageText:
    //
    // One of the installers for this device cannot perform the installation at
    // this time.
    //
    int SPAPI_E_DEVICE_INSTALLER_NOT_READY = 0x800F0246;

    //
    // MessageId: SPAPI_E_DRIVER_STORE_ADD_FAILED
    //
    // MessageText:
    //
    // A problem was encountered while attempting to add the driver to the
    // store.
    //
    int SPAPI_E_DRIVER_STORE_ADD_FAILED = 0x800F0247;

    //
    // MessageId: SPAPI_E_DEVICE_INSTALL_BLOCKED
    //
    // MessageText:
    //
    // The installation of this device is forbidden by system policy. Contact
    // your system administrator.
    //
    int SPAPI_E_DEVICE_INSTALL_BLOCKED = 0x800F0248;

    //
    // MessageId: SPAPI_E_DRIVER_INSTALL_BLOCKED
    //
    // MessageText:
    //
    // The installation of this driver is forbidden by system policy. Contact
    // your system administrator.
    //
    int SPAPI_E_DRIVER_INSTALL_BLOCKED = 0x800F0249;

    //
    // MessageId: SPAPI_E_WRONG_INF_TYPE
    //
    // MessageText:
    //
    // The specified INF is the wrong type for this operation.
    //
    int SPAPI_E_WRONG_INF_TYPE = 0x800F024A;

    //
    // MessageId: SPAPI_E_FILE_HASH_NOT_IN_CATALOG
    //
    // MessageText:
    //
    // The hash for the file is not present in the specified catalog file. The
    // file is likely corrupt or the victim of tampering.
    //
    int SPAPI_E_FILE_HASH_NOT_IN_CATALOG = 0x800F024B;

    //
    // MessageId: SPAPI_E_DRIVER_STORE_DELETE_FAILED
    //
    // MessageText:
    //
    // A problem was encountered while attempting to delete the driver from the
    // store.
    //
    int SPAPI_E_DRIVER_STORE_DELETE_FAILED = 0x800F024C;

    //
    // MessageId: SPAPI_E_UNRECOVERABLE_STACK_OVERF;OW
    //
    // MessageText:
    //
    // An unrecoverable stack overF;ow was encountered.
    //
    int SPAPI_E_UNRECOVERABLE_STACK_OVERFLOW = 0x800F0300;

    //
    // MessageId: SPAPI_E_ERROR_NOT_INSTALLED
    //
    // MessageText:
    //
    // No installed components were detected.
    //
    int SPAPI_E_ERROR_NOT_INSTALLED = 0x800F1000;

    // *****************
    // FACILITY_SCARD
    // *****************
    //
    // ==
    // Facility SCARD Error Messages
    // ==
    //
    int SCARD_S_SUCCESS = NO_ERROR;
    //
    // MessageId: SCARD_F_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal consistency check failed.
    //
    int SCARD_F_INTERNAL_ERROR = 0x80100001;

    //
    // MessageId: SCARD_E_CANCELLED
    //
    // MessageText:
    //
    // The action was cancelled by an SCardCancel request.
    //
    int SCARD_E_CANCELLED = 0x80100002;

    //
    // MessageId: SCARD_E_INVALID_HANDLE
    //
    // MessageText:
    //
    // The supplied handle was invalid.
    //
    int SCARD_E_INVALID_HANDLE = 0x80100003;

    //
    // MessageId: SCARD_E_INVALID_PARAMETER
    //
    // MessageText:
    //
    // One or more of the supplied parameters could not be properly interpreted.
    //
    int SCARD_E_INVALID_PARAMETER = 0x80100004;

    //
    // MessageId: SCARD_E_INVALID_TARGET
    //
    // MessageText:
    //
    // Registry startup information is missing or invalid.
    //
    int SCARD_E_INVALID_TARGET = 0x80100005;

    //
    // MessageId: SCARD_E_NO_MEMORY
    //
    // MessageText:
    //
    // Not enough memory available to complete this command.
    //
    int SCARD_E_NO_MEMORY = 0x80100006;

    //
    // MessageId: SCARD_F_WAITED_TOO_LONG
    //
    // MessageText:
    //
    // An internal consistency timer has expired.
    //
    int SCARD_F_WAITED_TOO_LONG = 0x80100007;

    //
    // MessageId: SCARD_E_INSUFFICIENT_BUFFER
    //
    // MessageText:
    //
    // The data buffer to receive returned data is too small for the returned
    // data.
    //
    int SCARD_E_INSUFFICIENT_BUFFER = 0x80100008;

    //
    // MessageId: SCARD_E_UNKNOWN_READER
    //
    // MessageText:
    //
    // The specified reader name is not recognized.
    //
    int SCARD_E_UNKNOWN_READER = 0x80100009;

    //
    // MessageId: SCARD_E_TIMEOUT
    //
    // MessageText:
    //
    // The user-specified timeout value has expired.
    //
    int SCARD_E_TIMEOUT = 0x8010000A;

    //
    // MessageId: SCARD_E_SHARING_VIOLATION
    //
    // MessageText:
    //
    // The smart card cannot be accessed because of other connections
    // outstanding.
    //
    int SCARD_E_SHARING_VIOLATION = 0x8010000B;

    //
    // MessageId: SCARD_E_NO_SMARTCARD
    //
    // MessageText:
    //
    // The operation requires a Smart Card, but no Smart Card is currently in
    // the device.
    //
    int SCARD_E_NO_SMARTCARD = 0x8010000C;

    //
    // MessageId: SCARD_E_UNKNOWN_CARD
    //
    // MessageText:
    //
    // The specified smart card name is not recognized.
    //
    int SCARD_E_UNKNOWN_CARD = 0x8010000D;

    //
    // MessageId: SCARD_E_CANT_DISPOSE
    //
    // MessageText:
    //
    // The system could not dispose of the media in the requested manner.
    //
    int SCARD_E_CANT_DISPOSE = 0x8010000E;

    //
    // MessageId: SCARD_E_PROTO_MISMATCH
    //
    // MessageText:
    //
    // The requested protocols are incompatible with the protocol currently in
    // use with the smart card.
    //
    int SCARD_E_PROTO_MISMATCH = 0x8010000F;

    //
    // MessageId: SCARD_E_NOT_READY
    //
    // MessageText:
    //
    // The reader or smart card is not ready to accept commands.
    //
    int SCARD_E_NOT_READY = 0x80100010;

    //
    // MessageId: SCARD_E_INVALID_VALUE
    //
    // MessageText:
    //
    // One or more of the supplied parameters values could not be properly
    // interpreted.
    //
    int SCARD_E_INVALID_VALUE = 0x80100011;

    //
    // MessageId: SCARD_E_SYSTEM_CANCELLED
    //
    // MessageText:
    //
    // The action was cancelled by the system, presumably to log off or shut
    // down.
    //
    int SCARD_E_SYSTEM_CANCELLED = 0x80100012;

    //
    // MessageId: SCARD_F_COMM_ERROR
    //
    // MessageText:
    //
    // An internal communications error has been detected.
    //
    int SCARD_F_COMM_ERROR = 0x80100013;

    //
    // MessageId: SCARD_F_UNKNOWN_ERROR
    //
    // MessageText:
    //
    // An internal error has been detected, but the source is unknown.
    //
    int SCARD_F_UNKNOWN_ERROR = 0x80100014;

    //
    // MessageId: SCARD_E_INVALID_ATR
    //
    // MessageText:
    //
    // An ATR obtained from the registry is not a valid ATR string.
    //
    int SCARD_E_INVALID_ATR = 0x80100015;

    //
    // MessageId: SCARD_E_NOT_TRANSACTED
    //
    // MessageText:
    //
    // An attempt was made to end a non-existent transaction.
    //
    int SCARD_E_NOT_TRANSACTED = 0x80100016;

    //
    // MessageId: SCARD_E_READER_UNAVAILABLE
    //
    // MessageText:
    //
    // The specified reader is not currently available for use.
    //
    int SCARD_E_READER_UNAVAILABLE = 0x80100017;

    //
    // MessageId: SCARD_P_SHUTDOWN
    //
    // MessageText:
    //
    // The operation has been aborted to allow the server application to exit.
    //
    int SCARD_P_SHUTDOWN = 0x80100018;

    //
    // MessageId: SCARD_E_PCI_TOO_SMALL
    //
    // MessageText:
    //
    // The PCI Receive buffer was too small.
    //
    int SCARD_E_PCI_TOO_SMALL = 0x80100019;

    //
    // MessageId: SCARD_E_READER_UNSUPPORTED
    //
    // MessageText:
    //
    // The reader driver does not meet minimal requirements for support.
    //
    int SCARD_E_READER_UNSUPPORTED = 0x8010001A;

    //
    // MessageId: SCARD_E_DUPLICATE_READER
    //
    // MessageText:
    //
    // The reader driver did not produce a unique reader name.
    //
    int SCARD_E_DUPLICATE_READER = 0x8010001B;

    //
    // MessageId: SCARD_E_CARD_UNSUPPORTED
    //
    // MessageText:
    //
    // The smart card does not meet minimal requirements for support.
    //
    int SCARD_E_CARD_UNSUPPORTED = 0x8010001C;

    //
    // MessageId: SCARD_E_NO_SERVICE
    //
    // MessageText:
    //
    // The Smart card resource manager is not running.
    //
    int SCARD_E_NO_SERVICE = 0x8010001D;

    //
    // MessageId: SCARD_E_SERVICE_STOPPED
    //
    // MessageText:
    //
    // The Smart card resource manager has shut down.
    //
    int SCARD_E_SERVICE_STOPPED = 0x8010001E;

    //
    // MessageId: SCARD_E_UNEXPECTED
    //
    // MessageText:
    //
    // An unexpected card error has occurred.
    //
    int SCARD_E_UNEXPECTED = 0x8010001F;

    //
    // MessageId: SCARD_E_ICC_INSTALLATION
    //
    // MessageText:
    //
    // No Primary Provider can be found for the smart card.
    //
    int SCARD_E_ICC_INSTALLATION = 0x80100020;

    //
    // MessageId: SCARD_E_ICC_CREATEORDER
    //
    // MessageText:
    //
    // The requested order of object creation is not supported.
    //
    int SCARD_E_ICC_CREATEORDER = 0x80100021;

    //
    // MessageId: SCARD_E_UNSUPPORTED_FEATURE
    //
    // MessageText:
    //
    // This smart card does not support the requested feature.
    //
    int SCARD_E_UNSUPPORTED_FEATURE = 0x80100022;

    //
    // MessageId: SCARD_E_DIR_NOT_FOUND
    //
    // MessageText:
    //
    // The identified directory does not exist in the smart card.
    //
    int SCARD_E_DIR_NOT_FOUND = 0x80100023;

    //
    // MessageId: SCARD_E_FILE_NOT_FOUND
    //
    // MessageText:
    //
    // The identified file does not exist in the smart card.
    //
    int SCARD_E_FILE_NOT_FOUND = 0x80100024;

    //
    // MessageId: SCARD_E_NO_DIR
    //
    // MessageText:
    //
    // The supplied path does not represent a smart card directory.
    //
    int SCARD_E_NO_DIR = 0x80100025;

    //
    // MessageId: SCARD_E_NO_FILE
    //
    // MessageText:
    //
    // The supplied path does not represent a smart card file.
    //
    int SCARD_E_NO_FILE = 0x80100026;

    //
    // MessageId: SCARD_E_NO_ACCESS
    //
    // MessageText:
    //
    // Access is denied to this file.
    //
    int SCARD_E_NO_ACCESS = 0x80100027;

    //
    // MessageId: SCARD_E_WRITE_TOO_MANY
    //
    // MessageText:
    //
    // The smartcard does not have enough memory to store the information.
    //
    int SCARD_E_WRITE_TOO_MANY = 0x80100028;

    //
    // MessageId: SCARD_E_BAD_SEEK
    //
    // MessageText:
    //
    // There was an error trying to set the smart card file object pointer.
    //
    int SCARD_E_BAD_SEEK = 0x80100029;

    //
    // MessageId: SCARD_E_INVALID_CHV
    //
    // MessageText:
    //
    // The supplied PIN is incorrect.
    //
    int SCARD_E_INVALID_CHV = 0x8010002A;

    //
    // MessageId: SCARD_E_UNKNOWN_RES_MNG
    //
    // MessageText:
    //
    // An unrecognized error code was returned from a layered component.
    //
    int SCARD_E_UNKNOWN_RES_MNG = 0x8010002B;

    //
    // MessageId: SCARD_E_NO_SUCH_CERTIFICATE
    //
    // MessageText:
    //
    // The requested certificate does not exist.
    //
    int SCARD_E_NO_SUCH_CERTIFICATE = 0x8010002C;

    //
    // MessageId: SCARD_E_CERTIFICATE_UNAVAILABLE
    //
    // MessageText:
    //
    // The requested certificate could not be obtained.
    //
    int SCARD_E_CERTIFICATE_UNAVAILABLE = 0x8010002D;

    //
    // MessageId: SCARD_E_NO_READERS_AVAILABLE
    //
    // MessageText:
    //
    // Cannot find a smart card reader.
    //
    int SCARD_E_NO_READERS_AVAILABLE = 0x8010002E;

    //
    // MessageId: SCARD_E_COMM_DATA_LOST
    //
    // MessageText:
    //
    // A communications error with the smart card has been detected. Retry the
    // operation.
    //
    int SCARD_E_COMM_DATA_LOST = 0x8010002F;

    //
    // MessageId: SCARD_E_NO_KEY_CONTAINER
    //
    // MessageText:
    //
    // The requested key container does not exist on the smart card.
    //
    int SCARD_E_NO_KEY_CONTAINER = 0x80100030;

    //
    // MessageId: SCARD_E_SERVER_TOO_BUSY
    //
    // MessageText:
    //
    // The Smart card resource manager is too busy to complete this operation.
    //
    int SCARD_E_SERVER_TOO_BUSY = 0x80100031;

    //
    // These are warning codes.
    //
    //
    // MessageId: SCARD_W_UNSUPPORTED_CARD
    //
    // MessageText:
    //
    // The reader cannot communicate with the smart card, due to ATR
    // configuration conF;icts.
    //
    int SCARD_W_UNSUPPORTED_CARD = 0x80100065;

    //
    // MessageId: SCARD_W_UNRESPONSIVE_CARD
    //
    // MessageText:
    //
    // The smart card is not responding to a reset.
    //
    int SCARD_W_UNRESPONSIVE_CARD = 0x80100066;

    //
    // MessageId: SCARD_W_UNPOWERED_CARD
    //
    // MessageText:
    //
    // Power has been removed from the smart card, so that further communication
    // is not possible.
    //
    int SCARD_W_UNPOWERED_CARD = 0x80100067;

    //
    // MessageId: SCARD_W_RESET_CARD
    //
    // MessageText:
    //
    // The smart card has been reset, so any shared state information is
    // invalid.
    //
    int SCARD_W_RESET_CARD = 0x80100068;

    //
    // MessageId: SCARD_W_REMOVED_CARD
    //
    // MessageText:
    //
    // The smart card has been removed, so that further communication is not
    // possible.
    //
    int SCARD_W_REMOVED_CARD = 0x80100069;

    //
    // MessageId: SCARD_W_SECURITY_VIOLATION
    //
    // MessageText:
    //
    // Access was denied because of a security violation.
    //
    int SCARD_W_SECURITY_VIOLATION = 0x8010006A;

    //
    // MessageId: SCARD_W_WRONG_CHV
    //
    // MessageText:
    //
    // The card cannot be accessed because the wrong PIN was presented.
    //
    int SCARD_W_WRONG_CHV = 0x8010006B;

    //
    // MessageId: SCARD_W_CHV_BLOCKED
    //
    // MessageText:
    //
    // The card cannot be accessed because the maximum number of PIN entry
    // attempts has been reached.
    //
    int SCARD_W_CHV_BLOCKED = 0x8010006C;

    //
    // MessageId: SCARD_W_EOF
    //
    // MessageText:
    //
    // The end of the smart card file has been reached.
    //
    int SCARD_W_EOF = 0x8010006D;

    //
    // MessageId: SCARD_W_CANCELLED_BY_USER
    //
    // MessageText:
    //
    // The action was cancelled by the user.
    //
    int SCARD_W_CANCELLED_BY_USER = 0x8010006E;

    //
    // MessageId: SCARD_W_CARD_NOT_AUTHENTICATED
    //
    // MessageText:
    //
    // No PIN was presented to the smart card.
    //
    int SCARD_W_CARD_NOT_AUTHENTICATED = 0x8010006F;

    //
    // MessageId: SCARD_W_CACHE_ITEM_NOT_FOUND
    //
    // MessageText:
    //
    // The requested item could not be found in the cache.
    //
    int SCARD_W_CACHE_ITEM_NOT_FOUND = 0x80100070;

    //
    // MessageId: SCARD_W_CACHE_ITEM_STALE
    //
    // MessageText:
    //
    // The requested cache item is too old and was deleted from the cache.
    //
    int SCARD_W_CACHE_ITEM_STALE = 0x80100071;

    //
    // MessageId: SCARD_W_CACHE_ITEM_TOO_BIG
    //
    // MessageText:
    //
    // The new cache item exceeds the maximum per-item size defined for the
    // cache.
    //
    int SCARD_W_CACHE_ITEM_TOO_BIG = 0x80100072;

    // *****************
    // FACILITY_COMPLUS
    // *****************
    //
    // ==
    // Facility COMPLUS Error Messages
    // ==
    //
    //
    // The following are the subranges within the COMPLUS facility
    // 0x400 - 0x4ff=COMADMIN_E_CAT
    // 0x600 - 0x6ff=COMQC errors
    // 0x700 - 0x7ff=MSDTC errors
    // 0x800 - 0x8ff=Other COMADMIN errors
    //
    // COMPLUS Admin errors
    //
    //
    // MessageId: COMADMIN_E_OBJECTERRORS
    //
    // MessageText:
    //
    // Errors occurred accessing one or more objects - the ErrorInfo collection
    // may have more detail
    //
    int COMADMIN_E_OBJECTERRORS = 0x80110401;

    //
    // MessageId: COMADMIN_E_OBJECTINVALID
    //
    // MessageText:
    //
    // One or more of the object's properties are missing or invalid
    //
    int COMADMIN_E_OBJECTINVALID = 0x80110402;

    //
    // MessageId: COMADMIN_E_KEYMISSING
    //
    // MessageText:
    //
    // The object was not found in the catalog
    //
    int COMADMIN_E_KEYMISSING = 0x80110403;

    //
    // MessageId: COMADMIN_E_ALREADYINSTALLED
    //
    // MessageText:
    //
    // The object is already registered
    //
    int COMADMIN_E_ALREADYINSTALLED = 0x80110404;

    //
    // MessageId: COMADMIN_E_APP_FILE_WRITEFAIL
    //
    // MessageText:
    //
    // Error occurred writing to the application file
    //
    int COMADMIN_E_APP_FILE_WRITEFAIL = 0x80110407;

    //
    // MessageId: COMADMIN_E_APP_FILE_READFAIL
    //
    // MessageText:
    //
    // Error occurred reading the application file
    //
    int COMADMIN_E_APP_FILE_READFAIL = 0x80110408;

    //
    // MessageId: COMADMIN_E_APP_FILE_VERSION
    //
    // MessageText:
    //
    // Invalid version number in application file
    //
    int COMADMIN_E_APP_FILE_VERSION = 0x80110409;

    //
    // MessageId: COMADMIN_E_BADPATH
    //
    // MessageText:
    //
    // The file path is invalid
    //
    int COMADMIN_E_BADPATH = 0x8011040A;

    //
    // MessageId: COMADMIN_E_APPLICATIONEXISTS
    //
    // MessageText:
    //
    // The application is already installed
    //
    int COMADMIN_E_APPLICATIONEXISTS = 0x8011040B;

    //
    // MessageId: COMADMIN_E_ROLEEXISTS
    //
    // MessageText:
    //
    // The role already exists
    //
    int COMADMIN_E_ROLEEXISTS = 0x8011040C;

    //
    // MessageId: COMADMIN_E_CANTCOPYFILE
    //
    // MessageText:
    //
    // An error occurred copying the file
    //
    int COMADMIN_E_CANTCOPYFILE = 0x8011040D;

    //
    // MessageId: COMADMIN_E_NOUSER
    //
    // MessageText:
    //
    // One or more users are not valid
    //
    int COMADMIN_E_NOUSER = 0x8011040F;

    //
    // MessageId: COMADMIN_E_INVALIDUSERIDS
    //
    // MessageText:
    //
    // One or more users in the application file are not valid
    //
    int COMADMIN_E_INVALIDUSERIDS = 0x80110410;

    //
    // MessageId: COMADMIN_E_NOREGISTRYCLSID
    //
    // MessageText:
    //
    // The component's CLSID is missing or corrupt
    //
    int COMADMIN_E_NOREGISTRYCLSID = 0x80110411;

    //
    // MessageId: COMADMIN_E_BADREGISTRYPROGID
    //
    // MessageText:
    //
    // The component's progID is missing or corrupt
    //
    int COMADMIN_E_BADREGISTRYPROGID = 0x80110412;

    //
    // MessageId: COMADMIN_E_AUTHENTICATIONLEVEL
    //
    // MessageText:
    //
    // Unable to set required authentication level for update request
    //
    int COMADMIN_E_AUTHENTICATIONLEVEL = 0x80110413;

    //
    // MessageId: COMADMIN_E_USERPASSWDNOTVALID
    //
    // MessageText:
    //
    // The identity or password set on the application is not valid
    //
    int COMADMIN_E_USERPASSWDNOTVALID = 0x80110414;

    //
    // MessageId: COMADMIN_E_CLSIDORIIDMISMATCH
    //
    // MessageText:
    //
    // Application file CLSIDs or IIDs do not match corresponding DLLs
    //
    int COMADMIN_E_CLSIDORIIDMISMATCH = 0x80110418;

    //
    // MessageId: COMADMIN_E_REMOTEINTERFACE
    //
    // MessageText:
    //
    // Interface information is either missing or changed
    //
    int COMADMIN_E_REMOTEINTERFACE = 0x80110419;

    //
    // MessageId: COMADMIN_E_DLLREGISTERSERVER
    //
    // MessageText:
    //
    // DllRegisterServer failed on component install
    //
    int COMADMIN_E_DLLREGISTERSERVER = 0x8011041A;

    //
    // MessageId: COMADMIN_E_NOSERVERSHARE
    //
    // MessageText:
    //
    // No server file share available
    //
    int COMADMIN_E_NOSERVERSHARE = 0x8011041B;

    //
    // MessageId: COMADMIN_E_DLLLOADFAILED
    //
    // MessageText:
    //
    // DLL could not be loaded
    //
    int COMADMIN_E_DLLLOADFAILED = 0x8011041D;

    //
    // MessageId: COMADMIN_E_BADREGISTRYLIBID
    //
    // MessageText:
    //
    // The registered TypeLib ID is not valid
    //
    int COMADMIN_E_BADREGISTRYLIBID = 0x8011041E;

    //
    // MessageId: COMADMIN_E_APPDIRNOTFOUND
    //
    // MessageText:
    //
    // Application install directory not found
    //
    int COMADMIN_E_APPDIRNOTFOUND = 0x8011041F;

    //
    // MessageId: COMADMIN_E_REGISTRARFAILED
    //
    // MessageText:
    //
    // Errors occurred while in the component registrar
    //
    int COMADMIN_E_REGISTRARFAILED = 0x80110423;

    //
    // MessageId: COMADMIN_E_COMPFILE_DOESNOTEXIST
    //
    // MessageText:
    //
    // The file does not exist
    //
    int COMADMIN_E_COMPFILE_DOESNOTEXIST = 0x80110424;

    //
    // MessageId: COMADMIN_E_COMPFILE_LOADDLLFAIL
    //
    // MessageText:
    //
    // The DLL could not be loaded
    //
    int COMADMIN_E_COMPFILE_LOADDLLFAIL = 0x80110425;

    //
    // MessageId: COMADMIN_E_COMPFILE_GETCLASSOBJ
    //
    // MessageText:
    //
    // GetClassObject failed in the DLL
    //
    int COMADMIN_E_COMPFILE_GETCLASSOBJ = 0x80110426;

    //
    // MessageId: COMADMIN_E_COMPFILE_CLASSNOTAVAIL
    //
    // MessageText:
    //
    // The DLL does not support the components listed in the TypeLib
    //
    int COMADMIN_E_COMPFILE_CLASSNOTAVAIL = 0x80110427;

    //
    // MessageId: COMADMIN_E_COMPFILE_BADTLB
    //
    // MessageText:
    //
    // The TypeLib could not be loaded
    //
    int COMADMIN_E_COMPFILE_BADTLB = 0x80110428;

    //
    // MessageId: COMADMIN_E_COMPFILE_NOTINSTALLABLE
    //
    // MessageText:
    //
    // The file does not contain components or component information
    //
    int COMADMIN_E_COMPFILE_NOTINSTALLABLE = 0x80110429;

    //
    // MessageId: COMADMIN_E_NOTCHANGEABLE
    //
    // MessageText:
    //
    // Changes to this object and its sub-objects have been disabled
    //
    int COMADMIN_E_NOTCHANGEABLE = 0x8011042A;

    //
    // MessageId: COMADMIN_E_NOTDELETEABLE
    //
    // MessageText:
    //
    // The delete function has been disabled for this object
    //
    int COMADMIN_E_NOTDELETEABLE = 0x8011042B;

    //
    // MessageId: COMADMIN_E_SESSION
    //
    // MessageText:
    //
    // The server catalog version is not supported
    //
    int COMADMIN_E_SESSION = 0x8011042C;

    //
    // MessageId: COMADMIN_E_COMP_MOVE_LOCKED
    //
    // MessageText:
    //
    // The component move was disallowed, because the source or destination
    // application is either a system application or currently locked against
    // changes
    //
    int COMADMIN_E_COMP_MOVE_LOCKED = 0x8011042D;

    //
    // MessageId: COMADMIN_E_COMP_MOVE_BAD_DEST
    //
    // MessageText:
    //
    // The component move failed because the destination application no longer
    // exists
    //
    int COMADMIN_E_COMP_MOVE_BAD_DEST = 0x8011042E;

    //
    // MessageId: COMADMIN_E_REGISTERTLB
    //
    // MessageText:
    //
    // The system was unable to register the TypeLib
    //
    int COMADMIN_E_REGISTERTLB = 0x80110430;

    //
    // MessageId: COMADMIN_E_SYSTEMAPP
    //
    // MessageText:
    //
    // This operation cannot be performed on the system application
    //
    int COMADMIN_E_SYSTEMAPP = 0x80110433;

    //
    // MessageId: COMADMIN_E_COMPFILE_NOREGISTRAR
    //
    // MessageText:
    //
    // The component registrar referenced in this file is not available
    //
    int COMADMIN_E_COMPFILE_NOREGISTRAR = 0x80110434;

    //
    // MessageId: COMADMIN_E_COREQCOMPINSTALLED
    //
    // MessageText:
    //
    // A component in the same DLL is already installed
    //
    int COMADMIN_E_COREQCOMPINSTALLED = 0x80110435;

    //
    // MessageId: COMADMIN_E_SERVICENOTINSTALLED
    //
    // MessageText:
    //
    // The service is not installed
    //
    int COMADMIN_E_SERVICENOTINSTALLED = 0x80110436;

    //
    // MessageId: COMADMIN_E_PROPERTYSAVEFAILED
    //
    // MessageText:
    //
    // One or more property settings are either invalid or in conF;ict with each
    // other
    //
    int COMADMIN_E_PROPERTYSAVEFAILED = 0x80110437;

    //
    // MessageId: COMADMIN_E_OBJECTEXISTS
    //
    // MessageText:
    //
    // The object you are attempting to add or rename already exists
    //
    int COMADMIN_E_OBJECTEXISTS = 0x80110438;

    //
    // MessageId: COMADMIN_E_COMPONENTEXISTS
    //
    // MessageText:
    //
    // The component already exists
    //
    int COMADMIN_E_COMPONENTEXISTS = 0x80110439;

    //
    // MessageId: COMADMIN_E_REGFILE_CORRUPT
    //
    // MessageText:
    //
    // The registration file is corrupt
    //
    int COMADMIN_E_REGFILE_CORRUPT = 0x8011043B;

    //
    // MessageId: COMADMIN_E_PROPERTY_OVERF;OW
    //
    // MessageText:
    //
    // The property value is too large
    //
    int COMADMIN_E_PROPERTY_OVERFLOW = 0x8011043C;

    //
    // MessageId: COMADMIN_E_NOTINREGISTRY
    //
    // MessageText:
    //
    // Object was not found in registry
    //
    int COMADMIN_E_NOTINREGISTRY = 0x8011043E;

    //
    // MessageId: COMADMIN_E_OBJECTNOTPOOLABLE
    //
    // MessageText:
    //
    // This object is not poolable
    //
    int COMADMIN_E_OBJECTNOTPOOLABLE = 0x8011043F;

    //
    // MessageId: COMADMIN_E_APPLID_MATCHES_CLSID
    //
    // MessageText:
    //
    // A CLSID with the same GUID as the new application ID is already installed
    // on this machine
    //
    int COMADMIN_E_APPLID_MATCHES_CLSID = 0x80110446;

    //
    // MessageId: COMADMIN_E_ROLE_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // A role assigned to a component, interface, or method did not exist in the
    // application
    //
    int COMADMIN_E_ROLE_DOES_NOT_EXIST = 0x80110447;

    //
    // MessageId: COMADMIN_E_START_APP_NEEDS_COMPONENTS
    //
    // MessageText:
    //
    // You must have components in an application in order to start the
    // application
    //
    int COMADMIN_E_START_APP_NEEDS_COMPONENTS = 0x80110448;

    //
    // MessageId: COMADMIN_E_REQUIRES_DIFFERENT_PLATFORM
    //
    // MessageText:
    //
    // This operation is not enabled on this platform
    //
    int COMADMIN_E_REQUIRES_DIFFERENT_PLATFORM = 0x80110449;

    //
    // MessageId: COMADMIN_E_CAN_NOT_EXPORT_APP_PROXY
    //
    // MessageText:
    //
    // Application Proxy is not exportable
    //
    int COMADMIN_E_CAN_NOT_EXPORT_APP_PROXY = 0x8011044A;

    //
    // MessageId: COMADMIN_E_CAN_NOT_START_APP
    //
    // MessageText:
    //
    // Failed to start application because it is either a library application or
    // an application proxy
    //
    int COMADMIN_E_CAN_NOT_START_APP = 0x8011044B;

    //
    // MessageId: COMADMIN_E_CAN_NOT_EXPORT_SYS_APP
    //
    // MessageText:
    //
    // System application is not exportable
    //
    int COMADMIN_E_CAN_NOT_EXPORT_SYS_APP = 0x8011044C;

    //
    // MessageId: COMADMIN_E_CANT_SUBSCRIBE_TO_COMPONENT
    //
    // MessageText:
    //
    // Cannot subscribe to this component (the component may have been imported)
    //
    int COMADMIN_E_CANT_SUBSCRIBE_TO_COMPONENT = 0x8011044D;

    //
    // MessageId: COMADMIN_E_EVENTCLASS_CANT_BE_SUBSCRIBER
    //
    // MessageText:
    //
    // An event class cannot also be a subscriber component
    //
    int COMADMIN_E_EVENTCLASS_CANT_BE_SUBSCRIBER = 0x8011044E;

    //
    // MessageId: COMADMIN_E_LIB_APP_PROXY_INCOMPATIBLE
    //
    // MessageText:
    //
    // Library applications and application proxies are incompatible
    //
    int COMADMIN_E_LIB_APP_PROXY_INCOMPATIBLE = 0x8011044F;

    //
    // MessageId: COMADMIN_E_BASE_PARTITION_ONLY
    //
    // MessageText:
    //
    // This function is valid for the base partition only
    //
    int COMADMIN_E_BASE_PARTITION_ONLY = 0x80110450;

    //
    // MessageId: COMADMIN_E_START_APP_DISABLED
    //
    // MessageText:
    //
    // You cannot start an application that has been disabled
    //
    int COMADMIN_E_START_APP_DISABLED = 0x80110451;

    //
    // MessageId: COMADMIN_E_CAT_DUPLICATE_PARTITION_NAME
    //
    // MessageText:
    //
    // The specified partition name is already in use on this computer
    //
    int COMADMIN_E_CAT_DUPLICATE_PARTITION_NAME = 0x80110457;

    //
    // MessageId: COMADMIN_E_CAT_INVALID_PARTITION_NAME
    //
    // MessageText:
    //
    // The specified partition name is invalid. Check that the name contains at
    // least one visible character
    //
    int COMADMIN_E_CAT_INVALID_PARTITION_NAME = 0x80110458;

    //
    // MessageId: COMADMIN_E_CAT_PARTITION_IN_USE
    //
    // MessageText:
    //
    // The partition cannot be deleted because it is the default partition for
    // one or more users
    //
    int COMADMIN_E_CAT_PARTITION_IN_USE = 0x80110459;

    //
    // MessageId: COMADMIN_E_FILE_PARTITION_DUPLICATE_FILES
    //
    // MessageText:
    //
    // The partition cannot be exported, because one or more components in the
    // partition have the same file name
    //
    int COMADMIN_E_FILE_PARTITION_DUPLICATE_FILES = 0x8011045A;

    //
    // MessageId: COMADMIN_E_CAT_IMPORTED_COMPONENTS_NOT_ALLOWED
    //
    // MessageText:
    //
    // Applications that contain one or more imported components cannot be
    // installed into a non-base partition
    //
    int COMADMIN_E_CAT_IMPORTED_COMPONENTS_NOT_ALLOWED = 0x8011045B;

    //
    // MessageId: COMADMIN_E_AMBIGUOUS_APPLICATION_NAME
    //
    // MessageText:
    //
    // The application name is not unique and cannot be resolved to an
    // application id
    //
    int COMADMIN_E_AMBIGUOUS_APPLICATION_NAME = 0x8011045C;

    //
    // MessageId: COMADMIN_E_AMBIGUOUS_PARTITION_NAME
    //
    // MessageText:
    //
    // The partition name is not unique and cannot be resolved to a partition id
    //
    int COMADMIN_E_AMBIGUOUS_PARTITION_NAME = 0x8011045D;

    //
    // MessageId: COMADMIN_E_REGDB_NOTINITIALIZED
    //
    // MessageText:
    //
    // The COM+ registry database has not been initialized
    //
    int COMADMIN_E_REGDB_NOTINITIALIZED = 0x80110472;

    //
    // MessageId: COMADMIN_E_REGDB_NOTOPEN
    //
    // MessageText:
    //
    // The COM+ registry database is not open
    //
    int COMADMIN_E_REGDB_NOTOPEN = 0x80110473;

    //
    // MessageId: COMADMIN_E_REGDB_SYSTEMERR
    //
    // MessageText:
    //
    // The COM+ registry database detected a system error
    //
    int COMADMIN_E_REGDB_SYSTEMERR = 0x80110474;

    //
    // MessageId: COMADMIN_E_REGDB_ALREADYRUNNING
    //
    // MessageText:
    //
    // The COM+ registry database is already running
    //
    int COMADMIN_E_REGDB_ALREADYRUNNING = 0x80110475;

    //
    // MessageId: COMADMIN_E_MIG_VERSIONNOTSUPPORTED
    //
    // MessageText:
    //
    // This version of the COM+ registry database cannot be migrated
    //
    int COMADMIN_E_MIG_VERSIONNOTSUPPORTED = 0x80110480;

    //
    // MessageId: COMADMIN_E_MIG_SCHEMANOTFOUND
    //
    // MessageText:
    //
    // The schema version to be migrated could not be found in the COM+ registry
    // database
    //
    int COMADMIN_E_MIG_SCHEMANOTFOUND = 0x80110481;

    //
    // MessageId: COMADMIN_E_CAT_BITNESSMISMATCH
    //
    // MessageText:
    //
    // There was a type mismatch between binaries
    //
    int COMADMIN_E_CAT_BITNESSMISMATCH = 0x80110482;

    //
    // MessageId: COMADMIN_E_CAT_UNACCEPTABLEBITNESS
    //
    // MessageText:
    //
    // A binary of unknown or invalid type was provided
    //
    int COMADMIN_E_CAT_UNACCEPTABLEBITNESS = 0x80110483;

    //
    // MessageId: COMADMIN_E_CAT_WRONGAPPBITNESS
    //
    // MessageText:
    //
    // There was a type mismatch between a binary and an application
    //
    int COMADMIN_E_CAT_WRONGAPPBITNESS = 0x80110484;

    //
    // MessageId: COMADMIN_E_CAT_PAUSE_RESUME_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The application cannot be paused or resumed
    //
    int COMADMIN_E_CAT_PAUSE_RESUME_NOT_SUPPORTED = 0x80110485;

    //
    // MessageId: COMADMIN_E_CAT_SERVERFAULT
    //
    // MessageText:
    //
    // The COM+ Catalog Server threw an exception during execution
    //
    int COMADMIN_E_CAT_SERVERFAULT = 0x80110486;

    //
    // COMPLUS Queued component errors
    //
    //
    // MessageId: COMQC_E_APPLICATION_NOT_QUEUED
    //
    // MessageText:
    //
    // Only COM+ Applications marked "queued" can be invoked using the "queue"
    // moniker
    //
    int COMQC_E_APPLICATION_NOT_QUEUED = 0x80110600;

    //
    // MessageId: COMQC_E_NO_QUEUEABLE_INTERFACES
    //
    // MessageText:
    //
    // At least one interface must be marked "queued" in order to create a
    // queued component instance with the "queue" moniker
    //
    int COMQC_E_NO_QUEUEABLE_INTERFACES = 0x80110601;

    //
    // MessageId: COMQC_E_QUEUING_SERVICE_NOT_AVAILABLE
    //
    // MessageText:
    //
    // MSMQ is required for the requested operation and is not installed
    //
    int COMQC_E_QUEUING_SERVICE_NOT_AVAILABLE = 0x80110602;

    //
    // MessageId: COMQC_E_NO_IPERSISTSTREAM
    //
    // MessageText:
    //
    // Unable to marshal an interface that does not support IPersistStream
    //
    int COMQC_E_NO_IPERSISTSTREAM = 0x80110603;

    //
    // MessageId: COMQC_E_BAD_MESSAGE
    //
    // MessageText:
    //
    // The message is improperly formatted or was damaged in transit
    //
    int COMQC_E_BAD_MESSAGE = 0x80110604;

    //
    // MessageId: COMQC_E_UNAUTHENTICATED
    //
    // MessageText:
    //
    // An unauthenticated message was received by an application that accepts
    // only authenticated messages
    //
    int COMQC_E_UNAUTHENTICATED = 0x80110605;

    //
    // MessageId: COMQC_E_UNTRUSTED_ENQUEUER
    //
    // MessageText:
    //
    // The message was requeued or moved by a user not in the "QC Trusted User"
    // role
    //
    int COMQC_E_UNTRUSTED_ENQUEUER = 0x80110606;

    //
    // The range 0x700-0x7ff is reserved for MSDTC errors.
    //
    //
    // MessageId: MSDTC_E_DUPLICATE_RESOURCE
    //
    // MessageText:
    //
    // Cannot create a duplicate resource of type Distributed Transaction
    // Coordinator
    //
    int MSDTC_E_DUPLICATE_RESOURCE = 0x80110701;

    //
    // More COMADMIN errors from 0x8**
    //
    //
    // MessageId: COMADMIN_E_OBJECT_PARENT_MISSING
    //
    // MessageText:
    //
    // One of the objects being inserted or updated does not belong to a valid
    // parent collection
    //
    int COMADMIN_E_OBJECT_PARENT_MISSING = 0x80110808;

    //
    // MessageId: COMADMIN_E_OBJECT_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // One of the specified objects cannot be found
    //
    int COMADMIN_E_OBJECT_DOES_NOT_EXIST = 0x80110809;

    //
    // MessageId: COMADMIN_E_APP_NOT_RUNNING
    //
    // MessageText:
    //
    // The specified application is not currently running
    //
    int COMADMIN_E_APP_NOT_RUNNING = 0x8011080A;

    //
    // MessageId: COMADMIN_E_INVALID_PARTITION
    //
    // MessageText:
    //
    // The partition(s) specified are not valid.
    //
    int COMADMIN_E_INVALID_PARTITION = 0x8011080B;

    //
    // MessageId: COMADMIN_E_SVCAPP_NOT_POOLABLE_OR_RECYCLABLE
    //
    // MessageText:
    //
    // COM+ applications that run as NT service may not be pooled or recycled
    //
    int COMADMIN_E_SVCAPP_NOT_POOLABLE_OR_RECYCLABLE = 0x8011080D;

    //
    // MessageId: COMADMIN_E_USER_IN_SET
    //
    // MessageText:
    //
    // One or more users are already assigned to a local partition set.
    //
    int COMADMIN_E_USER_IN_SET = 0x8011080E;

    //
    // MessageId: COMADMIN_E_CANTRECYCLELIBRARYAPPS
    //
    // MessageText:
    //
    // Library applications may not be recycled.
    //
    int COMADMIN_E_CANTRECYCLELIBRARYAPPS = 0x8011080F;

    //
    // MessageId: COMADMIN_E_CANTRECYCLESERVICEAPPS
    //
    // MessageText:
    //
    // Applications running as NT services may not be recycled.
    //
    int COMADMIN_E_CANTRECYCLESERVICEAPPS = 0x80110811;

    //
    // MessageId: COMADMIN_E_PROCESSALREADYRECYCLED
    //
    // MessageText:
    //
    // The process has already been recycled.
    //
    int COMADMIN_E_PROCESSALREADYRECYCLED = 0x80110812;

    //
    // MessageId: COMADMIN_E_PAUSEDPROCESSMAYNOTBERECYCLED
    //
    // MessageText:
    //
    // A paused process may not be recycled.
    //
    int COMADMIN_E_PAUSEDPROCESSMAYNOTBERECYCLED = 0x80110813;

    //
    // MessageId: COMADMIN_E_CANTMAKEINPROCSERVICE
    //
    // MessageText:
    //
    // Library applications may not be NT services.
    //
    int COMADMIN_E_CANTMAKEINPROCSERVICE = 0x80110814;

    //
    // MessageId: COMADMIN_E_PROGIDINUSEBYCLSID
    //
    // MessageText:
    //
    // The ProgID provided to the copy operation is invalid. The ProgID is in
    // use by another registered CLSID.
    //
    int COMADMIN_E_PROGIDINUSEBYCLSID = 0x80110815;

    //
    // MessageId: COMADMIN_E_DEFAULT_PARTITION_NOT_IN_SET
    //
    // MessageText:
    //
    // The partition specified as default is not a member of the partition set.
    //
    int COMADMIN_E_DEFAULT_PARTITION_NOT_IN_SET = 0x80110816;

    //
    // MessageId: COMADMIN_E_RECYCLEDPROCESSMAYNOTBEPAUSED
    //
    // MessageText:
    //
    // A recycled process may not be paused.
    //
    int COMADMIN_E_RECYCLEDPROCESSMAYNOTBEPAUSED = 0x80110817;

    //
    // MessageId: COMADMIN_E_PARTITION_ACCESSDENIED
    //
    // MessageText:
    //
    // Access to the specified partition is denied.
    //
    int COMADMIN_E_PARTITION_ACCESSDENIED = 0x80110818;

    //
    // MessageId: COMADMIN_E_PARTITION_MSI_ONLY
    //
    // MessageText:
    //
    // Only Application Files (*.MSI files) can be installed into partitions.
    //
    int COMADMIN_E_PARTITION_MSI_ONLY = 0x80110819;

    //
    // MessageId: COMADMIN_E_LEGACYCOMPS_NOT_ALLOWED_IN_1_0_FORMAT
    //
    // MessageText:
    //
    // Applications containing one or more legacy components may not be exported
    // to=1.0 format.
    //
    int COMADMIN_E_LEGACYCOMPS_NOT_ALLOWED_IN_1_0_FORMAT = 0x8011081A;

    //
    // MessageId: COMADMIN_E_LEGACYCOMPS_NOT_ALLOWED_IN_NONBASE_PARTITIONS
    //
    // MessageText:
    //
    // Legacy components may not exist in non-base partitions.
    //
    int COMADMIN_E_LEGACYCOMPS_NOT_ALLOWED_IN_NONBASE_PARTITIONS = 0x8011081B;

    //
    // MessageId: COMADMIN_E_COMP_MOVE_SOURCE
    //
    // MessageText:
    //
    // A component cannot be moved (or copied) from the System Application, an
    // application proxy or a non-changeable application
    //
    int COMADMIN_E_COMP_MOVE_SOURCE = 0x8011081C;

    //
    // MessageId: COMADMIN_E_COMP_MOVE_DEST
    //
    // MessageText:
    //
    // A component cannot be moved (or copied) to the System Application, an
    // application proxy or a non-changeable application
    //
    int COMADMIN_E_COMP_MOVE_DEST = 0x8011081D;

    //
    // MessageId: COMADMIN_E_COMP_MOVE_PRIVATE
    //
    // MessageText:
    //
    // A private component cannot be moved (or copied) to a library application
    // or to the base partition
    //
    int COMADMIN_E_COMP_MOVE_PRIVATE = 0x8011081E;

    //
    // MessageId: COMADMIN_E_BASEPARTITION_REQUIRED_IN_SET
    //
    // MessageText:
    //
    // The Base Application Partition exists in all partition sets and cannot be
    // removed.
    //
    int COMADMIN_E_BASEPARTITION_REQUIRED_IN_SET = 0x8011081F;

    //
    // MessageId: COMADMIN_E_CANNOT_ALIAS_EVENTCLASS
    //
    // MessageText:
    //
    // Alas, Event Class components cannot be aliased.
    //
    int COMADMIN_E_CANNOT_ALIAS_EVENTCLASS = 0x80110820;

    //
    // MessageId: COMADMIN_E_PRIVATE_ACCESSDENIED
    //
    // MessageText:
    //
    // Access is denied because the component is private.
    //
    int COMADMIN_E_PRIVATE_ACCESSDENIED = 0x80110821;

    //
    // MessageId: COMADMIN_E_SAFERINVALID
    //
    // MessageText:
    //
    // The specified SAFER level is invalid.
    //
    int COMADMIN_E_SAFERINVALID = 0x80110822;

    //
    // MessageId: COMADMIN_E_REGISTRY_ACCESSDENIED
    //
    // MessageText:
    //
    // The specified user cannot write to the system registry
    //
    int COMADMIN_E_REGISTRY_ACCESSDENIED = 0x80110823;

    //
    // MessageId: COMADMIN_E_PARTITIONS_DISABLED
    //
    // MessageText:
    //
    // COM+ partitions are currently disabled.
    //
    int COMADMIN_E_PARTITIONS_DISABLED = 0x80110824;

    //
    // MessageId: ERROR_FLT_IO_COMPLETE
    //
    // MessageText:
    //
    // The IO was completed by a filter.
    //
    int ERROR_FLT_IO_COMPLETE = 0x001F0001;

    //
    // MessageId: ERROR_FLT_NO_HANDLER_DEFINED
    //
    // MessageText:
    //
    // A handler was not defined by the filter for this operation.
    //
    int ERROR_FLT_NO_HANDLER_DEFINED = 0x801F0001;

    //
    // MessageId: ERROR_FLT_CONTEXT_ALREADY_DEFINED
    //
    // MessageText:
    //
    // A context is already defined for this object.
    //
    int ERROR_FLT_CONTEXT_ALREADY_DEFINED = 0x801F0002;

    //
    // MessageId: ERROR_FLT_INVALID_ASYNCHRONOUS_REQUEST
    //
    // MessageText:
    //
    // Asynchronous requests are not valid for this operation.
    //
    int ERROR_FLT_INVALID_ASYNCHRONOUS_REQUEST = 0x801F0003;

    //
    // MessageId: ERROR_FLT_DISALLOW_FAST_IO
    //
    // MessageText:
    //
    // Disallow the Fast IO path for this operation.
    //
    int ERROR_FLT_DISALLOW_FAST_IO = 0x801F0004;

    //
    // MessageId: ERROR_FLT_INVALID_NAME_REQUEST
    //
    // MessageText:
    //
    // An invalid name request was made. The name requested cannot be retrieved
    // at this time.
    //
    int ERROR_FLT_INVALID_NAME_REQUEST = 0x801F0005;

    //
    // MessageId: ERROR_FLT_NOT_SAFE_TO_POST_OPERATION
    //
    // MessageText:
    //
    // Posting this operation to a worker thread for further processing is not
    // safe at this time because it could lead to a system deadlock.
    //
    int ERROR_FLT_NOT_SAFE_TO_POST_OPERATION = 0x801F0006;

    //
    // MessageId: ERROR_FLT_NOT_INITIALIZED
    //
    // MessageText:
    //
    // The Filter Manager was not initialized when a filter tried to register.
    // Make sure that the Filter Manager is getting loaded as a driver.
    //
    int ERROR_FLT_NOT_INITIALIZED = 0x801F0007;

    //
    // MessageId: ERROR_FLT_FILTER_NOT_READY
    //
    // MessageText:
    //
    // The filter is not ready for attachment to volumes because it has not
    // finished initializing (FLTStartFiltering has not been called).
    //
    int ERROR_FLT_FILTER_NOT_READY = 0x801F0008;

    //
    // MessageId: ERROR_FLT_POST_OPERATION_CLEANUP
    //
    // MessageText:
    //
    // The filter must cleanup any operation specific context at this time
    // because it is being removed from the system before the operation is
    // completed by the lower drivers.
    //
    int ERROR_FLT_POST_OPERATION_CLEANUP = 0x801F0009;

    //
    // MessageId: ERROR_FLT_INTERNAL_ERROR
    //
    // MessageText:
    //
    // The Filter Manager had an internal error from which it cannot recover,
    // therefore the operation has been failed. This is usually the result of a
    // filter returning an invalid value from a pre-operation callback.
    //
    int ERROR_FLT_INTERNAL_ERROR = 0x801F000A;

    //
    // MessageId: ERROR_FLT_DELETING_OBJECT
    //
    // MessageText:
    //
    // The object specified for this action is in the process of being deleted,
    // therefore the action requested cannot be completed at this time.
    //
    int ERROR_FLT_DELETING_OBJECT = 0x801F000B;

    //
    // MessageId: ERROR_FLT_MUST_BE_NONPAGED_POOL
    //
    // MessageText:
    //
    // Non-paged pool must be used for this type of context.
    //
    int ERROR_FLT_MUST_BE_NONPAGED_POOL = 0x801F000C;

    //
    // MessageId: ERROR_FLT_DUPLICATE_ENTRY
    //
    // MessageText:
    //
    // A duplicate handler definition has been provided for an operation.
    //
    int ERROR_FLT_DUPLICATE_ENTRY = 0x801F000D;

    //
    // MessageId: ERROR_FLT_CBDQ_DISABLED
    //
    // MessageText:
    //
    // The callback data queue has been disabled.
    //
    int ERROR_FLT_CBDQ_DISABLED = 0x801F000E;

    //
    // MessageId: ERROR_FLT_DO_NOT_ATTACH
    //
    // MessageText:
    //
    // Do not attach the filter to the volume at this time.
    //
    int ERROR_FLT_DO_NOT_ATTACH = 0x801F000F;

    //
    // MessageId: ERROR_FLT_DO_NOT_DETACH
    //
    // MessageText:
    //
    // Do not detach the filter from the volume at this time.
    //
    int ERROR_FLT_DO_NOT_DETACH = 0x801F0010;

    //
    // MessageId: ERROR_FLT_INSTANCE_ALTITUDE_COLLISION
    //
    // MessageText:
    //
    // An instance already exists at this altitude on the volume specified.
    //
    int ERROR_FLT_INSTANCE_ALTITUDE_COLLISION = 0x801F0011;

    //
    // MessageId: ERROR_FLT_INSTANCE_NAME_COLLISION
    //
    // MessageText:
    //
    // An instance already exists with this name on the volume specified.
    //
    int ERROR_FLT_INSTANCE_NAME_COLLISION = 0x801F0012;

    //
    // MessageId: ERROR_FLT_FILTER_NOT_FOUND
    //
    // MessageText:
    //
    // The system could not find the filter specified.
    //
    int ERROR_FLT_FILTER_NOT_FOUND = 0x801F0013;

    //
    // MessageId: ERROR_FLT_VOLUME_NOT_FOUND
    //
    // MessageText:
    //
    // The system could not find the volume specified.
    //
    int ERROR_FLT_VOLUME_NOT_FOUND = 0x801F0014;

    //
    // MessageId: ERROR_FLT_INSTANCE_NOT_FOUND
    //
    // MessageText:
    //
    // The system could not find the instance specified.
    //
    int ERROR_FLT_INSTANCE_NOT_FOUND = 0x801F0015;

    //
    // MessageId: ERROR_FLT_CONTEXT_ALLOCATION_NOT_FOUND
    //
    // MessageText:
    //
    // No registered context allocation definition was found for the given
    // request.
    //
    int ERROR_FLT_CONTEXT_ALLOCATION_NOT_FOUND = 0x801F0016;

    //
    // MessageId: ERROR_FLT_INVALID_CONTEXT_REGISTRATION
    //
    // MessageText:
    //
    // An invalid parameter was specified during context registration.
    //
    int ERROR_FLT_INVALID_CONTEXT_REGISTRATION = 0x801F0017;

    //
    // MessageId: ERROR_FLT_NAME_CACHE_MISS
    //
    // MessageText:
    //
    // The name requested was not found in Filter Manager's name cache and could
    // not be retrieved from the file system.
    //
    int ERROR_FLT_NAME_CACHE_MISS = 0x801F0018;

    //
    // MessageId: ERROR_FLT_NO_DEVICE_OBJECT
    //
    // MessageText:
    //
    // The requested device object does not exist for the given volume.
    //
    int ERROR_FLT_NO_DEVICE_OBJECT = 0x801F0019;

    //
    // MessageId: ERROR_FLT_VOLUME_ALREADY_MOUNTED
    //
    // MessageText:
    //
    // The specified volume is already mounted.
    //
    int ERROR_FLT_VOLUME_ALREADY_MOUNTED = 0x801F001A;

    //
    // MessageId: ERROR_FLT_ALREADY_ENLISTED
    //
    // MessageText:
    //
    // The specified Transaction Context is already enlisted in a transaction
    //
    int ERROR_FLT_ALREADY_ENLISTED = 0x801F001B;

    //
    // MessageId: ERROR_FLT_CONTEXT_ALREADY_LINKED
    //
    // MessageText:
    //
    // The specifiec context is already attached to another object
    //
    int ERROR_FLT_CONTEXT_ALREADY_LINKED = 0x801F001C;

    //
    // MessageId: ERROR_FLT_NO_WAITER_FOR_REPLY
    //
    // MessageText:
    //
    // No waiter is present for the filter's reply to this message.
    //
    int ERROR_FLT_NO_WAITER_FOR_REPLY = 0x801F0020;

    //
    // ==
    // Facility Graphics Error Messages
    // ==
    //
    //
    // The following are the subranges within the Graphics facility
    //
    // 0x0000 - 0x0fff Display Driver Loader driver & Video Port errors
    // (displdr.sys, videoprt.sys)
    // 0x1000 - 0x1fff Monitor Class Function driver errors=(monitor.sys)
    // 0x2000 - 0x2fff Windows Graphics Kernel Subsystem errors=(dxgkrnl.sys)
    // 0x3000 - 0x3fff=Desktop Window Manager errors
    // 0x2000 - 0x20ff Common errors
    // 0x2100 - 0x21ff Video Memory Manager (VidMM) subsystem errors
    // 0x2200 - 0x22ff Video GPU Scheduler (VidSch) subsystem errors
    // 0x2300 - 0x23ff Video Display Mode Management (VidDMM) subsystem errors
    //
    // Display Driver Loader driver & Video Port errors {0x0000..0x0fff}
    //
    //
    // MessageId: ERROR_HUNG_DISPLAY_DRIVER_THREAD
    //
    // MessageText:
    //
    // {Display Driver Stopped Responding}
    // The %hs display driver has stopped working normally. Save your work and
    // reboot the system to restore full display functionality.
    // The next time you reboot the machine a dialog will be displayed giving
    // you a chance to report this failure to Microsoft.
    //
    int ERROR_HUNG_DISPLAY_DRIVER_THREAD = 0x80260001;

    //
    // Desktop Window Manager errors {0x3000..0x3fff}
    //
    //
    // MessageId: DWM_E_COMPOSITIONDISABLED
    //
    // MessageText:
    //
    // {Desktop composition is disabled}
    // The operation could not be completed because desktop composition is
    // disabled.
    //
    int DWM_E_COMPOSITIONDISABLED = 0x80263001;

    //
    // MessageId: DWM_E_REMOTING_NOT_SUPPORTED
    //
    // MessageText:
    //
    // {Some desktop composition APIs are not supported while remoting}
    // The operation is not supported while running in a remote session.
    //
    int DWM_E_REMOTING_NOT_SUPPORTED = 0x80263002;

    //
    // MessageId: DWM_E_NO_REDIRECTION_SURFACE_AVAILABLE
    //
    // MessageText:
    //
    // {No DWM redirection surface is available}
    // The DWM was unable to provide a redireciton surface to complete the
    // DirectX present.
    //
    int DWM_E_NO_REDIRECTION_SURFACE_AVAILABLE = 0x80263003;

    //
    // MessageId: DWM_E_NOT_QUEUING_PRESENTS
    //
    // MessageText:
    //
    // {DWM is not queuing presents for the specified window}
    // The window specified is not currently using queued presents.
    //
    int DWM_E_NOT_QUEUING_PRESENTS = 0x80263004;

    //
    // Monitor class function driver errors {0x1000..0x1fff}
    //
    //
    // MessageId: ERROR_MONITOR_NO_DESCRIPTOR
    //
    // MessageText:
    //
    // Monitor descriptor could not be obtained.
    //
    int ERROR_MONITOR_NO_DESCRIPTOR = 0x80261001;

    //
    // MessageId: ERROR_MONITOR_UNKNOWN_DESCRIPTOR_FORMAT
    //
    // MessageText:
    //
    // Format of the obtained monitor descriptor is not supported by this
    // release.
    //
    int ERROR_MONITOR_UNKNOWN_DESCRIPTOR_FORMAT = 0x80261002;

    //
    // MessageId: ERROR_MONITOR_INVALID_DESCRIPTOR_CHECKSUM
    //
    // MessageText:
    //
    // Checksum of the obtained monitor descriptor is invalid.
    //
    int ERROR_MONITOR_INVALID_DESCRIPTOR_CHECKSUM = 0xC0261003;

    //
    // MessageId: ERROR_MONITOR_INVALID_STANDARD_TIMING_BLOCK
    //
    // MessageText:
    //
    // Monitor descriptor contains an invalid standard timing block.
    //
    int ERROR_MONITOR_INVALID_STANDARD_TIMING_BLOCK = 0xC0261004;

    //
    // MessageId: ERROR_MONITOR_WMI_DATABLOCK_REGISTRATION_FAILED
    //
    // MessageText:
    //
    // WMI data block registration failed for one of the MSMonitorClass WMI
    // subclasses.
    //
    int ERROR_MONITOR_WMI_DATABLOCK_REGISTRATION_FAILED = 0xC0261005;

    //
    // MessageId: ERROR_MONITOR_INVALID_SERIAL_NUMBER_MONDSC_BLOCK
    //
    // MessageText:
    //
    // Provided monitor descriptor block is either corrupted or does not contain
    // monitor's detailed serial number.
    //
    int ERROR_MONITOR_INVALID_SERIAL_NUMBER_MONDSC_BLOCK = 0xC0261006;

    //
    // MessageId: ERROR_MONITOR_INVALID_USER_FRIENDLY_MONDSC_BLOCK
    //
    // MessageText:
    //
    // Provided monitor descriptor block is either corrupted or does not contain
    // monitor's user friendly name.
    //
    int ERROR_MONITOR_INVALID_USER_FRIENDLY_MONDSC_BLOCK = 0xC0261007;

    //
    // MessageId: ERROR_MONITOR_NO_MORE_DESCRIPTOR_DATA
    //
    // MessageText:
    //
    // There is no monitor descriptor data at the specified (offset, size)
    // region.
    //
    int ERROR_MONITOR_NO_MORE_DESCRIPTOR_DATA = 0xC0261008;

    //
    // MessageId: ERROR_MONITOR_INVALID_DETAILED_TIMING_BLOCK
    //
    // MessageText:
    //
    // Monitor descriptor contains an invalid detailed timing block.
    //
    int ERROR_MONITOR_INVALID_DETAILED_TIMING_BLOCK = 0xC0261009;

    //
    // Windows Graphics Kernel Subsystem errors {0x2000..0x2fff}
    //
    // TODO: Add DXG Win32 errors here
    //
    // Common errors {0x2000..0x20ff}
    //
    //
    // MessageId: ERROR_GRAPHICS_NOT_EXCLUSIVE_MODE_OWNER
    //
    // MessageText:
    //
    // Exclusive mode ownership is needed to create unmanaged primary
    // allocation.
    //
    int ERROR_GRAPHICS_NOT_EXCLUSIVE_MODE_OWNER = 0xC0262000;

    //
    // MessageId: ERROR_GRAPHICS_INSUFFICIENT_DMA_BUFFER
    //
    // MessageText:
    //
    // The driver needs more DMA buffer space in order to complete the requested
    // operation.
    //
    int ERROR_GRAPHICS_INSUFFICIENT_DMA_BUFFER = 0xC0262001;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_DISPLAY_ADAPTER
    //
    // MessageText:
    //
    // Specified display adapter handle is invalid.
    //
    int ERROR_GRAPHICS_INVALID_DISPLAY_ADAPTER = 0xC0262002;

    //
    // MessageId: ERROR_GRAPHICS_ADAPTER_WAS_RESET
    //
    // MessageText:
    //
    // Specified display adapter and all of its state has been reset.
    //
    int ERROR_GRAPHICS_ADAPTER_WAS_RESET = 0xC0262003;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_DRIVER_MODEL
    //
    // MessageText:
    //
    // The driver stack doesn't match the expected driver model.
    //
    int ERROR_GRAPHICS_INVALID_DRIVER_MODEL = 0xC0262004;

    //
    // MessageId: ERROR_GRAPHICS_PRESENT_MODE_CHANGED
    //
    // MessageText:
    //
    // Present happened but ended up into the changed desktop mode
    //
    int ERROR_GRAPHICS_PRESENT_MODE_CHANGED = 0xC0262005;

    //
    // MessageId: ERROR_GRAPHICS_PRESENT_OCCLUDED
    //
    // MessageText:
    //
    // Nothing to present due to desktop occlusion
    //
    int ERROR_GRAPHICS_PRESENT_OCCLUDED = 0xC0262006;

    //
    // MessageId: ERROR_GRAPHICS_PRESENT_DENIED
    //
    // MessageText:
    //
    // Not able to present due to denial of desktop access
    //
    int ERROR_GRAPHICS_PRESENT_DENIED = 0xC0262007;

    //
    // MessageId: ERROR_GRAPHICS_CANNOTCOLORCONVERT
    //
    // MessageText:
    //
    // Not able to present with color convertion
    //
    int ERROR_GRAPHICS_CANNOTCOLORCONVERT = 0xC0262008;

    //
    // MessageId: ERROR_GRAPHICS_DRIVER_MISMATCH
    //
    // MessageText:
    //
    // The kernel driver detected a version mismatch between it and the user
    // mode driver.
    //
    int ERROR_GRAPHICS_DRIVER_MISMATCH = 0xC0262009;

    //
    // MessageId: ERROR_GRAPHICS_PARTIAL_DATA_POPULATED
    //
    // MessageText:
    //
    // Specified buffer is not big enough to contain entire requested dataset.
    // Partial data populated upto the size of the buffer.
    // Caller needs to provide buffer of size as specified in the partially
    // populated buffer's content (interface specific).
    //
    int ERROR_GRAPHICS_PARTIAL_DATA_POPULATED = 0x4026200A;

    //
    // Video Memory Manager (VidMM) subsystem errors {0x2100..0x21ff}
    //
    //
    // MessageId: ERROR_GRAPHICS_NO_VIDEO_MEMORY
    //
    // MessageText:
    //
    // Not enough video memory available to complete the operation.
    //
    int ERROR_GRAPHICS_NO_VIDEO_MEMORY = 0xC0262100;

    //
    // MessageId: ERROR_GRAPHICS_CANT_LOCK_MEMORY
    //
    // MessageText:
    //
    // Couldn't probe and lock the underlying memory of an allocation.
    //
    int ERROR_GRAPHICS_CANT_LOCK_MEMORY = 0xC0262101;

    //
    // MessageId: ERROR_GRAPHICS_ALLOCATION_BUSY
    //
    // MessageText:
    //
    // The allocation is currently busy.
    //
    int ERROR_GRAPHICS_ALLOCATION_BUSY = 0xC0262102;

    //
    // MessageId: ERROR_GRAPHICS_TOO_MANY_REFERENCES
    //
    // MessageText:
    //
    // An object being referenced has reach the maximum reference count already
    // and can't be reference further.
    //
    int ERROR_GRAPHICS_TOO_MANY_REFERENCES = 0xC0262103;

    //
    // MessageId: ERROR_GRAPHICS_TRY_AGAIN_LATER
    //
    // MessageText:
    //
    // A problem couldn't be solved due to some currently existing condition.
    // The problem should be tried again later.
    //
    int ERROR_GRAPHICS_TRY_AGAIN_LATER = 0xC0262104;

    //
    // MessageId: ERROR_GRAPHICS_TRY_AGAIN_NOW
    //
    // MessageText:
    //
    // A problem couldn't be solved due to some currently existing condition.
    // The problem should be tried again immediately.
    //
    int ERROR_GRAPHICS_TRY_AGAIN_NOW = 0xC0262105;

    //
    // MessageId: ERROR_GRAPHICS_ALLOCATION_INVALID
    //
    // MessageText:
    //
    // The allocation is invalid.
    //
    int ERROR_GRAPHICS_ALLOCATION_INVALID = 0xC0262106;

    //
    // MessageId: ERROR_GRAPHICS_UNSWIZZLING_APERTURE_UNAVAILABLE
    //
    // MessageText:
    //
    // No more unswizzling aperture are currently available.
    //
    int ERROR_GRAPHICS_UNSWIZZLING_APERTURE_UNAVAILABLE = 0xC0262107;

    //
    // MessageId: ERROR_GRAPHICS_UNSWIZZLING_APERTURE_UNSUPPORTED
    //
    // MessageText:
    //
    // The current allocation can't be unswizzled by an aperture.
    //
    int ERROR_GRAPHICS_UNSWIZZLING_APERTURE_UNSUPPORTED = 0xC0262108;

    //
    // MessageId: ERROR_GRAPHICS_CANT_EVICT_PINNED_ALLOCATION
    //
    // MessageText:
    //
    // The request failed because a pinned allocation can't be evicted.
    //
    int ERROR_GRAPHICS_CANT_EVICT_PINNED_ALLOCATION = 0xC0262109;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_ALLOCATION_USAGE
    //
    // MessageText:
    //
    // The allocation can't be used from it's current segment location for the
    // specified operation.
    //
    int ERROR_GRAPHICS_INVALID_ALLOCATION_USAGE = 0xC0262110;

    //
    // MessageId: ERROR_GRAPHICS_CANT_RENDER_LOCKED_ALLOCATION
    //
    // MessageText:
    //
    // A locked allocation can't be used in the current command buffer.
    //
    int ERROR_GRAPHICS_CANT_RENDER_LOCKED_ALLOCATION = 0xC0262111;

    //
    // MessageId: ERROR_GRAPHICS_ALLOCATION_CLOSED
    //
    // MessageText:
    //
    // The allocation being referenced has been closed permanently.
    //
    int ERROR_GRAPHICS_ALLOCATION_CLOSED = 0xC0262112;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_ALLOCATION_INSTANCE
    //
    // MessageText:
    //
    // An invalid allocation instance is being referenced.
    //
    int ERROR_GRAPHICS_INVALID_ALLOCATION_INSTANCE = 0xC0262113;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_ALLOCATION_HANDLE
    //
    // MessageText:
    //
    // An invalid allocation handle is being referenced.
    //
    int ERROR_GRAPHICS_INVALID_ALLOCATION_HANDLE = 0xC0262114;

    //
    // MessageId: ERROR_GRAPHICS_WRONG_ALLOCATION_DEVICE
    //
    // MessageText:
    //
    // The allocation being referenced doesn't belong to the current device.
    //
    int ERROR_GRAPHICS_WRONG_ALLOCATION_DEVICE = 0xC0262115;

    //
    // MessageId: ERROR_GRAPHICS_ALLOCATION_CONTENT_LOST
    //
    // MessageText:
    //
    // The specified allocation lost its content.
    //
    int ERROR_GRAPHICS_ALLOCATION_CONTENT_LOST = 0xC0262116;

    //
    // Video GPU Scheduler (VidSch) subsystem errors {0x2200..0x22ff}
    //
    //
    // MessageId: ERROR_GRAPHICS_GPU_EXCEPTION_ON_DEVICE
    //
    // MessageText:
    //
    // GPU exception is detected on the given device. The device is not able to
    // be scheduled.
    //
    int ERROR_GRAPHICS_GPU_EXCEPTION_ON_DEVICE = 0xC0262200;

    //
    // Video Present Network Management (VidPNMgr) subsystem errors
    // {0x2300..0x23ff}
    //
    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDPN_TOPOLOGY
    //
    // MessageText:
    //
    // Specified VidPN topology is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDPN_TOPOLOGY = 0xC0262300;

    //
    // MessageId: ERROR_GRAPHICS_VIDPN_TOPOLOGY_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Specified VidPN topology is valid but is not supported by this model of
    // the display adapter.
    //
    int ERROR_GRAPHICS_VIDPN_TOPOLOGY_NOT_SUPPORTED = 0xC0262301;

    //
    // MessageId: ERROR_GRAPHICS_VIDPN_TOPOLOGY_CURRENTLY_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Specified VidPN topology is valid but is not supported by the display
    // adapter at this time, due to current allocation of its resources.
    //
    int ERROR_GRAPHICS_VIDPN_TOPOLOGY_CURRENTLY_NOT_SUPPORTED = 0xC0262302;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDPN
    //
    // MessageText:
    //
    // Specified VidPN handle is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDPN = 0xC0262303;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_SOURCE
    //
    // MessageText:
    //
    // Specified video present source is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_SOURCE = 0xC0262304;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_TARGET
    //
    // MessageText:
    //
    // Specified video present target is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_TARGET = 0xC0262305;

    //
    // MessageId: ERROR_GRAPHICS_VIDPN_MODALITY_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Specified VidPN modality is not supported (e.g. at least two of the
    // pinned modes are not cofunctiona;.
    //
    int ERROR_GRAPHICS_VIDPN_MODALITY_NOT_SUPPORTED = 0xC0262306;

    //
    // MessageId: ERROR_GRAPHICS_MODE_NOT_PINNED
    //
    // MessageText:
    //
    // No mode is pinned on the specified VidPN source/target.
    //
    int ERROR_GRAPHICS_MODE_NOT_PINNED = 0x00262307;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDPN_SOURCEMODESET
    //
    // MessageText:
    //
    // Specified VidPN source mode set is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDPN_SOURCEMODESET = 0xC0262308;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDPN_TARGETMODESET
    //
    // MessageText:
    //
    // Specified VidPN target mode set is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDPN_TARGETMODESET = 0xC0262309;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_FREQUENCY
    //
    // MessageText:
    //
    // Specified video signal frequency is invalid.
    //
    int ERROR_GRAPHICS_INVALID_FREQUENCY = 0xC026230A;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_ACTIVE_REGION
    //
    // MessageText:
    //
    // Specified video signal active region is invalid.
    //
    int ERROR_GRAPHICS_INVALID_ACTIVE_REGION = 0xC026230B;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_TOTAL_REGION
    //
    // MessageText:
    //
    // Specified video signal total region is invalid.
    //
    int ERROR_GRAPHICS_INVALID_TOTAL_REGION = 0xC026230C;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_SOURCE_MODE
    //
    // MessageText:
    //
    // Specified video present source mode is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_SOURCE_MODE = 0xC0262310;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_TARGET_MODE
    //
    // MessageText:
    //
    // Specified video present target mode is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_TARGET_MODE = 0xC0262311;

    //
    // MessageId: ERROR_GRAPHICS_PINNED_MODE_MUST_REMAIN_IN_SET
    //
    // MessageText:
    //
    // Pinned mode must remain in the set on VidPN's cofunctional modality
    // enumeration.
    //
    int ERROR_GRAPHICS_PINNED_MODE_MUST_REMAIN_IN_SET = 0xC0262312;

    //
    // MessageId: ERROR_GRAPHICS_PATH_ALREADY_IN_TOPOLOGY
    //
    // MessageText:
    //
    // Specified video present path is already in VidPN's topology.
    //
    int ERROR_GRAPHICS_PATH_ALREADY_IN_TOPOLOGY = 0xC0262313;

    //
    // MessageId: ERROR_GRAPHICS_MODE_ALREADY_IN_MODESET
    //
    // MessageText:
    //
    // Specified mode is already in the mode set.
    //
    int ERROR_GRAPHICS_MODE_ALREADY_IN_MODESET = 0xC0262314;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDEOPRESENTSOURCESET
    //
    // MessageText:
    //
    // Specified video present source set is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDEOPRESENTSOURCESET = 0xC0262315;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDEOPRESENTTARGETSET
    //
    // MessageText:
    //
    // Specified video present target set is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDEOPRESENTTARGETSET = 0xC0262316;

    //
    // MessageId: ERROR_GRAPHICS_SOURCE_ALREADY_IN_SET
    //
    // MessageText:
    //
    // Specified video present source is already in the video present source
    // set.
    //
    int ERROR_GRAPHICS_SOURCE_ALREADY_IN_SET = 0xC0262317;

    //
    // MessageId: ERROR_GRAPHICS_TARGET_ALREADY_IN_SET
    //
    // MessageText:
    //
    // Specified video present target is already in the video present target
    // set.
    //
    int ERROR_GRAPHICS_TARGET_ALREADY_IN_SET = 0xC0262318;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDPN_PRESENT_PATH
    //
    // MessageText:
    //
    // Specified VidPN present path is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDPN_PRESENT_PATH = 0xC0262319;

    //
    // MessageId: ERROR_GRAPHICS_NO_RECOMMENDED_VIDPN_TOPOLOGY
    //
    // MessageText:
    //
    // Miniport has no recommendation for augmentation of the specified VidPN's
    // topology.
    //
    int ERROR_GRAPHICS_NO_RECOMMENDED_VIDPN_TOPOLOGY = 0xC026231A;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGESET
    //
    // MessageText:
    //
    // Specified monitor frequency range set is invalid.
    //
    int ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGESET = 0xC026231B;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGE
    //
    // MessageText:
    //
    // Specified monitor frequency range is invalid.
    //
    int ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGE = 0xC026231C;

    //
    // MessageId: ERROR_GRAPHICS_FREQUENCYRANGE_NOT_IN_SET
    //
    // MessageText:
    //
    // Specified frequency range is not in the specified monitor frequency range
    // set.
    //
    int ERROR_GRAPHICS_FREQUENCYRANGE_NOT_IN_SET = 0xC026231D;

    //
    // MessageId: ERROR_GRAPHICS_NO_PREFERRED_MODE
    //
    // MessageText:
    //
    // Specified mode set does not specify preference for one of its modes.
    //
    int ERROR_GRAPHICS_NO_PREFERRED_MODE = 0x0026231E;

    //
    // MessageId: ERROR_GRAPHICS_FREQUENCYRANGE_ALREADY_IN_SET
    //
    // MessageText:
    //
    // Specified frequency range is already in the specified monitor frequency
    // range set.
    //
    int ERROR_GRAPHICS_FREQUENCYRANGE_ALREADY_IN_SET = 0xC026231F;

    //
    // MessageId: ERROR_GRAPHICS_STALE_MODESET
    //
    // MessageText:
    //
    // Specified mode set is stale. Please reacquire the new mode set.
    //
    int ERROR_GRAPHICS_STALE_MODESET = 0xC0262320;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MONITOR_SOURCEMODESET
    //
    // MessageText:
    //
    // Specified monitor source mode set is invalid.
    //
    int ERROR_GRAPHICS_INVALID_MONITOR_SOURCEMODESET = 0xC0262321;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MONITOR_SOURCE_MODE
    //
    // MessageText:
    //
    // Specified monitor source mode is invalid.
    //
    int ERROR_GRAPHICS_INVALID_MONITOR_SOURCE_MODE = 0xC0262322;

    //
    // MessageId: ERROR_GRAPHICS_NO_RECOMMENDED_FUNCTIONAL_VIDPN
    //
    // MessageText:
    //
    // Miniport does not have any recommendation regarding the request to
    // provide a functional VidPN given the current display adapter
    // configuration.
    //
    int ERROR_GRAPHICS_NO_RECOMMENDED_FUNCTIONAL_VIDPN = 0xC0262323;

    //
    // MessageId: ERROR_GRAPHICS_MODE_ID_MUST_BE_UNIQUE
    //
    // MessageText:
    //
    // ID of the specified mode is already used by another mode in the set.
    //
    int ERROR_GRAPHICS_MODE_ID_MUST_BE_UNIQUE = 0xC0262324;

    //
    // MessageId: ERROR_GRAPHICS_EMPTY_ADAPTER_MONITOR_MODE_SUPPORT_INTERSECTION
    //
    // MessageText:
    //
    // System failed to determine a mode that is supported by both the display
    // adapter and the monitor connected to it.
    //
    int ERROR_GRAPHICS_EMPTY_ADAPTER_MONITOR_MODE_SUPPORT_INTERSECTION = 0xC0262325;

    //
    // MessageId: ERROR_GRAPHICS_VIDEO_PRESENT_TARGETS_LESS_THAN_SOURCES
    //
    // MessageText:
    //
    // Number of video present targets must be greater than or equal to the
    // number of video present sources.
    //
    int ERROR_GRAPHICS_VIDEO_PRESENT_TARGETS_LESS_THAN_SOURCES = 0xC0262326;

    //
    // MessageId: ERROR_GRAPHICS_PATH_NOT_IN_TOPOLOGY
    //
    // MessageText:
    //
    // Specified present path is not in VidPN's topology.
    //
    int ERROR_GRAPHICS_PATH_NOT_IN_TOPOLOGY = 0xC0262327;

    //
    // MessageId: ERROR_GRAPHICS_ADAPTER_MUST_HAVE_AT_LEAST_ONE_SOURCE
    //
    // MessageText:
    //
    // Display adapter must have at least one video present source.
    //
    int ERROR_GRAPHICS_ADAPTER_MUST_HAVE_AT_LEAST_ONE_SOURCE = 0xC0262328;

    //
    // MessageId: ERROR_GRAPHICS_ADAPTER_MUST_HAVE_AT_LEAST_ONE_TARGET
    //
    // MessageText:
    //
    // Display adapter must have at least one video present target.
    //
    int ERROR_GRAPHICS_ADAPTER_MUST_HAVE_AT_LEAST_ONE_TARGET = 0xC0262329;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MONITORDESCRIPTORSET
    //
    // MessageText:
    //
    // Specified monitor descriptor set is invalid.
    //
    int ERROR_GRAPHICS_INVALID_MONITORDESCRIPTORSET = 0xC026232A;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MONITORDESCRIPTOR
    //
    // MessageText:
    //
    // Specified monitor descriptor is invalid.
    //
    int ERROR_GRAPHICS_INVALID_MONITORDESCRIPTOR = 0xC026232B;

    //
    // MessageId: ERROR_GRAPHICS_MONITORDESCRIPTOR_NOT_IN_SET
    //
    // MessageText:
    //
    // Specified descriptor is not in the specified monitor descriptor set.
    //
    int ERROR_GRAPHICS_MONITORDESCRIPTOR_NOT_IN_SET = 0xC026232C;

    //
    // MessageId: ERROR_GRAPHICS_MONITORDESCRIPTOR_ALREADY_IN_SET
    //
    // MessageText:
    //
    // Specified descriptor is already in the specified monitor descriptor set.
    //
    int ERROR_GRAPHICS_MONITORDESCRIPTOR_ALREADY_IN_SET = 0xC026232D;

    //
    // MessageId: ERROR_GRAPHICS_MONITORDESCRIPTOR_ID_MUST_BE_UNIQUE
    //
    // MessageText:
    //
    // ID of the specified monitor descriptor is already used by another
    // descriptor in the set.
    //
    int ERROR_GRAPHICS_MONITORDESCRIPTOR_ID_MUST_BE_UNIQUE = 0xC026232E;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDPN_TARGET_SUBSET_TYPE
    //
    // MessageText:
    //
    // Specified video present target subset type is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDPN_TARGET_SUBSET_TYPE = 0xC026232F;

    //
    // MessageId: ERROR_GRAPHICS_RESOURCES_NOT_RELATED
    //
    // MessageText:
    //
    // Two or more of the specified resources are not related to each other, as
    // defined by the interface semantics.
    //
    int ERROR_GRAPHICS_RESOURCES_NOT_RELATED = 0xC0262330;

    //
    // MessageId: ERROR_GRAPHICS_SOURCE_ID_MUST_BE_UNIQUE
    //
    // MessageText:
    //
    // ID of the specified video present source is already used by another
    // source in the set.
    //
    int ERROR_GRAPHICS_SOURCE_ID_MUST_BE_UNIQUE = 0xC0262331;

    //
    // MessageId: ERROR_GRAPHICS_TARGET_ID_MUST_BE_UNIQUE
    //
    // MessageText:
    //
    // ID of the specified video present target is already used by another
    // target in the set.
    //
    int ERROR_GRAPHICS_TARGET_ID_MUST_BE_UNIQUE = 0xC0262332;

    //
    // MessageId: ERROR_GRAPHICS_NO_AVAILABLE_VIDPN_TARGET
    //
    // MessageText:
    //
    // Specified VidPN source cannot be used because there is no available VidPN
    // target to connect it to.
    //
    int ERROR_GRAPHICS_NO_AVAILABLE_VIDPN_TARGET = 0xC0262333;

    //
    // MessageId: ERROR_GRAPHICS_MONITOR_COULD_NOT_BE_ASSOCIATED_WITH_ADAPTER
    //
    // MessageText:
    //
    // Newly arrived monitor could not be associated with a display adapter.
    //
    int ERROR_GRAPHICS_MONITOR_COULD_NOT_BE_ASSOCIATED_WITH_ADAPTER = 0xC0262334;

    //
    // MessageId: ERROR_GRAPHICS_NO_VIDPNMGR
    //
    // MessageText:
    //
    // Display adapter in question does not have an associated VidPN manager.
    //
    int ERROR_GRAPHICS_NO_VIDPNMGR = 0xC0262335;

    //
    // MessageId: ERROR_GRAPHICS_NO_ACTIVE_VIDPN
    //
    // MessageText:
    //
    // VidPN manager of the display adapter in question does not have an active
    // VidPN.
    //
    int ERROR_GRAPHICS_NO_ACTIVE_VIDPN = 0xC0262336;

    //
    // MessageId: ERROR_GRAPHICS_STALE_VIDPN_TOPOLOGY
    //
    // MessageText:
    //
    // Specified VidPN topology is stale. Please reacquire the new topology.
    //
    int ERROR_GRAPHICS_STALE_VIDPN_TOPOLOGY = 0xC0262337;

    //
    // MessageId: ERROR_GRAPHICS_MONITOR_NOT_CONNECTED
    //
    // MessageText:
    //
    // There is no monitor connected on the specified video present target.
    //
    int ERROR_GRAPHICS_MONITOR_NOT_CONNECTED = 0xC0262338;

    //
    // MessageId: ERROR_GRAPHICS_SOURCE_NOT_IN_TOPOLOGY
    //
    // MessageText:
    //
    // Specified source is not part of the specified VidPN's topology.
    //
    int ERROR_GRAPHICS_SOURCE_NOT_IN_TOPOLOGY = 0xC0262339;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_PRIMARYSURFACE_SIZE
    //
    // MessageText:
    //
    // Specified primary surface size is invalid.
    //
    int ERROR_GRAPHICS_INVALID_PRIMARYSURFACE_SIZE = 0xC026233A;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VISIBLEREGION_SIZE
    //
    // MessageText:
    //
    // Specified visible region size is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VISIBLEREGION_SIZE = 0xC026233B;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_STRIDE
    //
    // MessageText:
    //
    // Specified stride is invalid.
    //
    int ERROR_GRAPHICS_INVALID_STRIDE = 0xC026233C;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_PIXELFORMAT
    //
    // MessageText:
    //
    // Specified pixel format is invalid.
    //
    int ERROR_GRAPHICS_INVALID_PIXELFORMAT = 0xC026233D;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_COLORBASIS
    //
    // MessageText:
    //
    // Specified color basis is invalid.
    //
    int ERROR_GRAPHICS_INVALID_COLORBASIS = 0xC026233E;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_PIXELVALUEACCESSMODE
    //
    // MessageText:
    //
    // Specified pixel value access mode is invalid.
    //
    int ERROR_GRAPHICS_INVALID_PIXELVALUEACCESSMODE = 0xC026233F;

    //
    // MessageId: ERROR_GRAPHICS_TARGET_NOT_IN_TOPOLOGY
    //
    // MessageText:
    //
    // Specified target is not part of the specified VidPN's topology.
    //
    int ERROR_GRAPHICS_TARGET_NOT_IN_TOPOLOGY = 0xC0262340;

    //
    // MessageId: ERROR_GRAPHICS_NO_DISPLAY_MODE_MANAGEMENT_SUPPORT
    //
    // MessageText:
    //
    // Failed to acquire display mode management interface.
    //
    int ERROR_GRAPHICS_NO_DISPLAY_MODE_MANAGEMENT_SUPPORT = 0xC0262341;

    //
    // MessageId: ERROR_GRAPHICS_VIDPN_SOURCE_IN_USE
    //
    // MessageText:
    //
    // Specified VidPN source is already owned by a DMM client and cannot be
    // used until that client releases it.
    //
    int ERROR_GRAPHICS_VIDPN_SOURCE_IN_USE = 0xC0262342;

    //
    // MessageId: ERROR_GRAPHICS_CANT_ACCESS_ACTIVE_VIDPN
    //
    // MessageText:
    //
    // Specified VidPN is active and cannot be accessed.
    //
    int ERROR_GRAPHICS_CANT_ACCESS_ACTIVE_VIDPN = 0xC0262343;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_PATH_IMPORTANCE_ORDINAL
    //
    // MessageText:
    //
    // Specified VidPN present path importance ordinal is invalid.
    //
    int ERROR_GRAPHICS_INVALID_PATH_IMPORTANCE_ORDINAL = 0xC0262344;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_PATH_CONTENT_GEOMETRY_TRANSFORMATION
    //
    // MessageText:
    //
    // Specified VidPN present path content geometry transformation is invalid.
    //
    int ERROR_GRAPHICS_INVALID_PATH_CONTENT_GEOMETRY_TRANSFORMATION = 0xC0262345;

    //
    // MessageId:
    // ERROR_GRAPHICS_PATH_CONTENT_GEOMETRY_TRANSFORMATION_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Specified content geometry transformation is not supported on the
    // respective VidPN present path.
    //
    int ERROR_GRAPHICS_PATH_CONTENT_GEOMETRY_TRANSFORMATION_NOT_SUPPORTED = 0xC0262346;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_GAMMA_RAMP
    //
    // MessageText:
    //
    // Specified gamma ramp is invalid.
    //
    int ERROR_GRAPHICS_INVALID_GAMMA_RAMP = 0xC0262347;

    //
    // MessageId: ERROR_GRAPHICS_GAMMA_RAMP_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Specified gamma ramp is not supported on the respective VidPN present
    // path.
    //
    int ERROR_GRAPHICS_GAMMA_RAMP_NOT_SUPPORTED = 0xC0262348;

    //
    // MessageId: ERROR_GRAPHICS_MULTISAMPLING_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Multi-sampling is not supported on the respective VidPN present path.
    //
    int ERROR_GRAPHICS_MULTISAMPLING_NOT_SUPPORTED = 0xC0262349;

    //
    // MessageId: ERROR_GRAPHICS_MODE_NOT_IN_MODESET
    //
    // MessageText:
    //
    // Specified mode is not in the specified mode set.
    //
    int ERROR_GRAPHICS_MODE_NOT_IN_MODESET = 0xC026234A;

    //
    // MessageId: ERROR_GRAPHICS_DATASET_IS_EMPTY
    //
    // MessageText:
    //
    // Specified data set (e.g. mode set, frequency range set, descriptor set,
    // topology, etc.) is empty.
    //
    int ERROR_GRAPHICS_DATASET_IS_EMPTY = 0x0026234B;

    //
    // MessageId: ERROR_GRAPHICS_NO_MORE_ELEMENTS_IN_DATASET
    //
    // MessageText:
    //
    // Specified data set (e.g. mode set, frequency range set, descriptor set,
    // topology, etc.) does not contain any more elements.
    //
    int ERROR_GRAPHICS_NO_MORE_ELEMENTS_IN_DATASET = 0x0026234C;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_VIDPN_TOPOLOGY_RECOMMENDATION_REASON
    //
    // MessageText:
    //
    // Specified VidPN topology recommendation reason is invalid.
    //
    int ERROR_GRAPHICS_INVALID_VIDPN_TOPOLOGY_RECOMMENDATION_REASON = 0xC026234D;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_PATH_CONTENT_TYPE
    //
    // MessageText:
    //
    // Specified VidPN present path content type is invalid.
    //
    int ERROR_GRAPHICS_INVALID_PATH_CONTENT_TYPE = 0xC026234E;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_COPYPROTECTION_TYPE
    //
    // MessageText:
    //
    // Specified VidPN present path copy protection type is invalid.
    //
    int ERROR_GRAPHICS_INVALID_COPYPROTECTION_TYPE = 0xC026234F;

    //
    // MessageId: ERROR_GRAPHICS_UNASSIGNED_MODESET_ALREADY_EXISTS
    //
    // MessageText:
    //
    // No more than one unassigned mode set can exist at any given time for a
    // given VidPN source/target.
    //
    int ERROR_GRAPHICS_UNASSIGNED_MODESET_ALREADY_EXISTS = 0xC0262350;

    //
    // MessageId: ERROR_GRAPHICS_PATH_CONTENT_GEOMETRY_TRANSFORMATION_NOT_PINNED
    //
    // MessageText:
    //
    // Specified content transformation is not pinned on the specified VidPN
    // present path.
    //
    int ERROR_GRAPHICS_PATH_CONTENT_GEOMETRY_TRANSFORMATION_NOT_PINNED = 0x00262351;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_SCANLINE_ORDERING
    //
    // MessageText:
    //
    // Specified scanline ordering type is invalid.
    //
    int ERROR_GRAPHICS_INVALID_SCANLINE_ORDERING = 0xC0262352;

    //
    // MessageId: ERROR_GRAPHICS_TOPOLOGY_CHANGES_NOT_ALLOWED
    //
    // MessageText:
    //
    // Topology changes are not allowed for the specified VidPN.
    //
    int ERROR_GRAPHICS_TOPOLOGY_CHANGES_NOT_ALLOWED = 0xC0262353;

    //
    // MessageId: ERROR_GRAPHICS_NO_AVAILABLE_IMPORTANCE_ORDINALS
    //
    // MessageText:
    //
    // All available importance ordinals are already used in specified topology.
    //
    int ERROR_GRAPHICS_NO_AVAILABLE_IMPORTANCE_ORDINALS = 0xC0262354;

    //
    // MessageId: ERROR_GRAPHICS_INCOMPATIBLE_PRIVATE_FORMAT
    //
    // MessageText:
    //
    // Specified primary surface has a different private format attribute than
    // the current primary surface
    //
    int ERROR_GRAPHICS_INCOMPATIBLE_PRIVATE_FORMAT = 0xC0262355;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MODE_PRUNING_ALGORITHM
    //
    // MessageText:
    //
    // Specified mode pruning algorithm is invalid
    //
    int ERROR_GRAPHICS_INVALID_MODE_PRUNING_ALGORITHM = 0xC0262356;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MONITOR_CAPABILITY_ORIGIN
    //
    // MessageText:
    //
    // Specified monitor capability origin is invalid.
    //
    int ERROR_GRAPHICS_INVALID_MONITOR_CAPABILITY_ORIGIN = 0xC0262357;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGE_CONSTRAINT
    //
    // MessageText:
    //
    // Specified monitor frequency range constraint is invalid.
    //
    int ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGE_CONSTRAINT = 0xC0262358;

    //
    // MessageId: ERROR_GRAPHICS_MAX_NUM_PATHS_REACHED
    //
    // MessageText:
    //
    // Maximum supported number of present paths has been reached.
    //
    int ERROR_GRAPHICS_MAX_NUM_PATHS_REACHED = 0xC0262359;

    //
    // MessageId: ERROR_GRAPHICS_CANCEL_VIDPN_TOPOLOGY_AUGMENTATION
    //
    // MessageText:
    //
    // Miniport requested that augmentation be cancelled for the specified
    // source of the specified VidPN's topology.
    //
    int ERROR_GRAPHICS_CANCEL_VIDPN_TOPOLOGY_AUGMENTATION = 0xC026235A;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_CLIENT_TYPE
    //
    // MessageText:
    //
    // Specified client type was not recognized.
    //
    int ERROR_GRAPHICS_INVALID_CLIENT_TYPE = 0xC026235B;

    //
    // MessageId: ERROR_GRAPHICS_CLIENTVIDPN_NOT_SET
    //
    // MessageText:
    //
    // Client VidPN is not set on this adapter (e.g. no user mode initiated mode
    // changes took place on this adapter yet).
    //
    int ERROR_GRAPHICS_CLIENTVIDPN_NOT_SET = 0xC026235C;

    //
    // Port specific status codes {0x2400..0x24ff}
    //
    //
    // MessageId: ERROR_GRAPHICS_SPECIFIED_CHILD_ALREADY_CONNECTED
    //
    // MessageText:
    //
    // Specified display adapter child device already has an external device
    // connected to it.
    //
    int ERROR_GRAPHICS_SPECIFIED_CHILD_ALREADY_CONNECTED = 0xC0262400;

    //
    // MessageId: ERROR_GRAPHICS_CHILD_DESCRIPTOR_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Specified display adapter child device does not support descriptor
    // exposure.
    //
    int ERROR_GRAPHICS_CHILD_DESCRIPTOR_NOT_SUPPORTED = 0xC0262401;

    //
    // MessageId: ERROR_GRAPHICS_UNKNOWN_CHILD_STATUS
    //
    // MessageText:
    //
    // Child device presence was not reliably detected.
    //
    int ERROR_GRAPHICS_UNKNOWN_CHILD_STATUS = 0x4026242F;

    //
    // MessageId: ERROR_GRAPHICS_NOT_A_LINKED_ADAPTER
    //
    // MessageText:
    //
    // The display adapter is not linked to any other adapters.
    //
    int ERROR_GRAPHICS_NOT_A_LINKED_ADAPTER = 0xC0262430;

    //
    // MessageId: ERROR_GRAPHICS_LEADLINK_NOT_ENUMERATED
    //
    // MessageText:
    //
    // Lead adapter in a linked configuration was not enumerated yet.
    //
    int ERROR_GRAPHICS_LEADLINK_NOT_ENUMERATED = 0xC0262431;

    //
    // MessageId: ERROR_GRAPHICS_CHAINLINKS_NOT_ENUMERATED
    //
    // MessageText:
    //
    // Some chain adapters in a linked configuration were not enumerated yet.
    //
    int ERROR_GRAPHICS_CHAINLINKS_NOT_ENUMERATED = 0xC0262432;

    //
    // MessageId: ERROR_GRAPHICS_ADAPTER_CHAIN_NOT_READY
    //
    // MessageText:
    //
    // The chain of linked adapters is not ready to start because of an unknown
    // failure.
    //
    int ERROR_GRAPHICS_ADAPTER_CHAIN_NOT_READY = 0xC0262433;

    //
    // MessageId: ERROR_GRAPHICS_CHAINLINKS_NOT_STARTED
    //
    // MessageText:
    //
    // An attempt was made to start a lead link display adapter when the chain
    // links were not started yet.
    //
    int ERROR_GRAPHICS_CHAINLINKS_NOT_STARTED = 0xC0262434;

    //
    // MessageId: ERROR_GRAPHICS_CHAINLINKS_NOT_POWERED_ON
    //
    // MessageText:
    //
    // An attempt was made to power up a lead link display adapter when the
    // chain links were powered down.
    //
    int ERROR_GRAPHICS_CHAINLINKS_NOT_POWERED_ON = 0xC0262435;

    //
    // MessageId: ERROR_GRAPHICS_INCONSISTENT_DEVICE_LINK_STATE
    //
    // MessageText:
    //
    // The adapter link was found to be in an inconsistent state. Not all
    // adapters are in an expected PNP/Power state.
    //
    int ERROR_GRAPHICS_INCONSISTENT_DEVICE_LINK_STATE = 0xC0262436;

    //
    // MessageId: ERROR_GRAPHICS_LEADLINK_START_DEFERRED
    //
    // MessageText:
    //
    // Starting the leadlink adapter has been deferred temporarily.
    //
    int ERROR_GRAPHICS_LEADLINK_START_DEFERRED = 0x40262437;

    //
    // MessageId: ERROR_GRAPHICS_NOT_POST_DEVICE_DRIVER
    //
    // MessageText:
    //
    // The driver trying to start is not the same as the driver for the POSTed
    // display adapter.
    //
    int ERROR_GRAPHICS_NOT_POST_DEVICE_DRIVER = 0xC0262438;

    //
    // MessageId: ERROR_GRAPHICS_POLLING_TOO_FREQUENTLY
    //
    // MessageText:
    //
    // The display adapter is being polled for children too frequently at the
    // same polling level.
    //
    int ERROR_GRAPHICS_POLLING_TOO_FREQUENTLY = 0x40262439;

    //
    // MessageId: ERROR_GRAPHICS_START_DEFERRED
    //
    // MessageText:
    //
    // Starting the adapter has been deferred temporarily.
    //
    int ERROR_GRAPHICS_START_DEFERRED = 0x4026243A;

    //
    // MessageId: ERROR_GRAPHICS_ADAPTER_ACCESS_NOT_EXCLUDED
    //
    // MessageText:
    //
    // An operation is being attempted that requires the display adapter to be
    // in a quiescent state.
    //
    int ERROR_GRAPHICS_ADAPTER_ACCESS_NOT_EXCLUDED = 0xC026243B;

    //
    // OPM, UAB and PVP specific error codes {0x2500..0x257f}
    //
    //
    // MessageId: ERROR_GRAPHICS_OPM_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The driver does not support OPM.
    //
    int ERROR_GRAPHICS_OPM_NOT_SUPPORTED = 0xC0262500;

    //
    // MessageId: ERROR_GRAPHICS_COPP_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The driver does not support COPP.
    //
    int ERROR_GRAPHICS_COPP_NOT_SUPPORTED = 0xC0262501;

    //
    // MessageId: ERROR_GRAPHICS_UAB_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The driver does not support UAB.
    //
    int ERROR_GRAPHICS_UAB_NOT_SUPPORTED = 0xC0262502;

    //
    // MessageId: ERROR_GRAPHICS_OPM_INVALID_ENCRYPTED_PARAMETERS
    //
    // MessageText:
    //
    // The specified encrypted parameters are invalid.
    //
    int ERROR_GRAPHICS_OPM_INVALID_ENCRYPTED_PARAMETERS = 0xC0262503;

    //
    // MessageId: ERROR_GRAPHICS_OPM_NO_VIDEO_OUTPUTS_EXIST
    //
    // MessageText:
    //
    // The GDI display device passed to this function does not have any active
    // video outputs.
    //
    int ERROR_GRAPHICS_OPM_NO_VIDEO_OUTPUTS_EXIST = 0xC0262505;

    //
    // MessageId: ERROR_GRAPHICS_OPM_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal error caused this operation to fail.
    //
    int ERROR_GRAPHICS_OPM_INTERNAL_ERROR = 0xC026250B;

    //
    // MessageId: ERROR_GRAPHICS_OPM_INVALID_HANDLE
    //
    // MessageText:
    //
    // The function failed because the caller passed in an invalid OPM user mode
    // handle.
    //
    int ERROR_GRAPHICS_OPM_INVALID_HANDLE = 0xC026250C;

    //
    // MessageId: ERROR_GRAPHICS_PVP_INVALID_CERTIFICATE_LENGTH
    //
    // MessageText:
    //
    // A certificate could not be returned because the certificate buffer passed
    // to the function was too small.
    //
    int ERROR_GRAPHICS_PVP_INVALID_CERTIFICATE_LENGTH = 0xC026250E;

    //
    // MessageId: ERROR_GRAPHICS_OPM_SPANNING_MODE_ENABLED
    //
    // MessageText:
    //
    // A video output could not be created because the frame buffer is in
    // spanning mode.
    //
    int ERROR_GRAPHICS_OPM_SPANNING_MODE_ENABLED = 0xC026250F;

    //
    // MessageId: ERROR_GRAPHICS_OPM_THEATER_MODE_ENABLED
    //
    // MessageText:
    //
    // A video output could not be created because the frame buffer is in
    // theater mode.
    //
    int ERROR_GRAPHICS_OPM_THEATER_MODE_ENABLED = 0xC0262510;

    //
    // MessageId: ERROR_GRAPHICS_PVP_HFS_FAILED
    //
    // MessageText:
    //
    // The function failed because the display adapter's Hardware Functionality
    // Scan failed to validate the graphics hardware.
    //
    int ERROR_GRAPHICS_PVP_HFS_FAILED = 0xC0262511;

    //
    // MessageId: ERROR_GRAPHICS_OPM_INVALID_SRM
    //
    // MessageText:
    //
    // The HDCP System Renewability Message passed to this function did not
    // comply with section=5 of the HDCP=1.1 specification.
    //
    int ERROR_GRAPHICS_OPM_INVALID_SRM = 0xC0262512;

    //
    // MessageId: ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_HDCP
    //
    // MessageText:
    //
    // The video output cannot enable the High-bandwidth Digital Content
    // Protection (HDCP) System because it does not support HDCP.
    //
    int ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_HDCP = 0xC0262513;

    //
    // MessageId: ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_ACP
    //
    // MessageText:
    //
    // The video output cannot enable Analogue Copy Protection (ACP) because it
    // does not support ACP.
    //
    int ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_ACP = 0xC0262514;

    //
    // MessageId: ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_CGMSA
    //
    // MessageText:
    //
    // The video output cannot enable the Content Generation Management System
    // Analogue (CGMS-A) protection technology because it does not support
    // CGMS-A.
    //
    int ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_CGMSA = 0xC0262515;

    //
    // MessageId: ERROR_GRAPHICS_OPM_HDCP_SRM_NEVER_SET
    //
    // MessageText:
    //
    // The IOPMVideoOutput::GetInformation method cannot return the version of
    // the SRM being used because the application never successfully passed an
    // SRM to the video output.
    //
    int ERROR_GRAPHICS_OPM_HDCP_SRM_NEVER_SET = 0xC0262516;

    //
    // MessageId: ERROR_GRAPHICS_OPM_RESOLUTION_TOO_HIGH
    //
    // MessageText:
    //
    // The IOPMVideoOutput::Configure method cannot enable the specified output
    // protection technology because the output's screen resolution is too high.
    //
    int ERROR_GRAPHICS_OPM_RESOLUTION_TOO_HIGH = 0xC0262517;

    //
    // MessageId: ERROR_GRAPHICS_OPM_ALL_HDCP_HARDWARE_ALREADY_IN_USE
    //
    // MessageText:
    //
    // The IOPMVideoOutput::Configure method cannot enable HDCP because the
    // display adapter's HDCP hardware is already being used by other physical
    // outputs.
    //
    int ERROR_GRAPHICS_OPM_ALL_HDCP_HARDWARE_ALREADY_IN_USE = 0xC0262518;

    //
    // MessageId: ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_NO_LONGER_EXISTS
    //
    // MessageText:
    //
    // The operating system asynchronously destroyed this OPM video output
    // because the operating system's state changed. This error typically occurs
    // because the monitor PDO associated with this video output was removed,
    // the monitor PDO associated with this video output was stopped, the video
    // output's session became a non-console session or the video output's
    // desktop became an inactive desktop.
    //
    int ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_NO_LONGER_EXISTS = 0xC026251A;

    //
    // MessageId: ERROR_GRAPHICS_OPM_SESSION_TYPE_CHANGE_IN_PROGRESS
    //
    // MessageText:
    //
    // The method failed because the session is changing its type. No
    // IOPMVideoOutput methods can be called when a session is changing its
    // type. There are currently three types of sessions: console, disconnected
    // and remote (RDP or ICA).
    //
    int ERROR_GRAPHICS_OPM_SESSION_TYPE_CHANGE_IN_PROGRESS = 0xC026251B;

    //
    // MessageId: ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_DOES_NOT_HAVE_COPP_SEMANTICS
    //
    // MessageText:
    //
    // Either the IOPMVideoOutput::COPPCompatibleGetInformation,
    // IOPMVideoOutput::GetInformation, or IOPMVideoOutput::Configure method
    // failed. This error is only returned if a video output has OPM semantics.
    // IOPMVideoOutput::COPPCompatibleGetInformation always returns this error
    // if a video output has OPM semantics. IOPMVideoOutput::GetInformation
    // returns this error code if the caller requested COPP specific
    // information. IOPMVideoOutput::Configure returns this error when the
    // caller tries to use a COPP specific command.
    //
    int ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_DOES_NOT_HAVE_COPP_SEMANTICS = 0xC026251C;

    //
    // MessageId: ERROR_GRAPHICS_OPM_INVALID_INFORMATION_REQUEST
    //
    // MessageText:
    //
    // The IOPMVideoOutput::GetInformation and
    // IOPMVideoOutput::COPPCompatibleGetInformation methods return this error
    // if the passed in sequence number is not the expected sequence number or
    // the passed in OMAC value is invalid.
    //
    int ERROR_GRAPHICS_OPM_INVALID_INFORMATION_REQUEST = 0xC026251D;

    //
    // MessageId: ERROR_GRAPHICS_OPM_DRIVER_INTERNAL_ERROR
    //
    // MessageText:
    //
    // The method failed because an unexpected error occurred inside of a
    // display driver.
    //
    int ERROR_GRAPHICS_OPM_DRIVER_INTERNAL_ERROR = 0xC026251E;

    //
    // MessageId: ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_DOES_NOT_HAVE_OPM_SEMANTICS
    //
    // MessageText:
    //
    // Either the IOPMVideoOutput::COPPCompatibleGetInformation,
    // IOPMVideoOutput::GetInformation, or IOPMVideoOutput::Configure method
    // failed. This error is only returned if a video output has COPP semantics.
    // IOPMVideoOutput::COPPCompatibleGetInformation returns this error code if
    // the caller requested OPM specific information.
    // IOPMVideoOutput::GetInformation always returns this error if a video
    // output has COPP semantics. IOPMVideoOutput::Configure returns this error
    // when the caller tries to use an OPM specific command.
    //
    int ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_DOES_NOT_HAVE_OPM_SEMANTICS = 0xC026251F;

    //
    // MessageId: ERROR_GRAPHICS_OPM_SIGNALING_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The IOPMVideoOutput::COPPCompatibleGetInformation or
    // IOPMVideoOutput::Configure method failed because the display driver does
    // not support the OPM_GET_ACP_AND_CGMSA_SIGNALING and
    // OPM_SET_ACP_AND_CGMSA_SIGNALING GUIDs.
    //
    int ERROR_GRAPHICS_OPM_SIGNALING_NOT_SUPPORTED = 0xC0262520;

    //
    // MessageId: ERROR_GRAPHICS_OPM_INVALID_CONFIGURATION_REQUEST
    //
    // MessageText:
    //
    // The IOPMVideoOutput::Configure function returns this error code if the
    // passed in sequence number is not the expected sequence number or the
    // passed in OMAC value is invalid.
    //
    int ERROR_GRAPHICS_OPM_INVALID_CONFIGURATION_REQUEST = 0xC0262521;

    //
    // Monitor Configuration API error codes {0x2580..0x25DF}
    //
    //
    // MessageId: ERROR_GRAPHICS_I2C_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The monitor connected to the specified video output does not have an I2C
    // bus.
    //
    int ERROR_GRAPHICS_I2C_NOT_SUPPORTED = 0xC0262580;

    //
    // MessageId: ERROR_GRAPHICS_I2C_DEVICE_DOES_NOT_EXIST
    //
    // MessageText:
    //
    // No device on the I2C bus has the specified address.
    //
    int ERROR_GRAPHICS_I2C_DEVICE_DOES_NOT_EXIST = 0xC0262581;

    //
    // MessageId: ERROR_GRAPHICS_I2C_ERROR_TRANSMITTING_DATA
    //
    // MessageText:
    //
    // An error occurred while transmitting data to the device on the I2C bus.
    //
    int ERROR_GRAPHICS_I2C_ERROR_TRANSMITTING_DATA = 0xC0262582;

    //
    // MessageId: ERROR_GRAPHICS_I2C_ERROR_RECEIVING_DATA
    //
    // MessageText:
    //
    // An error occurred while receiving data from the device on the I2C bus.
    //
    int ERROR_GRAPHICS_I2C_ERROR_RECEIVING_DATA = 0xC0262583;

    //
    // MessageId: ERROR_GRAPHICS_DDCCI_VCP_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The monitor does not support the specified VCP code.
    //
    int ERROR_GRAPHICS_DDCCI_VCP_NOT_SUPPORTED = 0xC0262584;

    //
    // MessageId: ERROR_GRAPHICS_DDCCI_INVALID_DATA
    //
    // MessageText:
    //
    // The data received from the monitor is invalid.
    //
    int ERROR_GRAPHICS_DDCCI_INVALID_DATA = 0xC0262585;

    //
    // MessageId:
    // ERROR_GRAPHICS_DDCCI_MONITOR_RETURNED_INVALID_TIMING_STATUS_BYTE
    //
    // MessageText:
    //
    // The function failed because a monitor returned an invalid Timing Status
    // byte when the operating system used the DDC/CI Get Timing Report & Timing
    // Message command to get a timing report from a monitor.
    //
    int ERROR_GRAPHICS_DDCCI_MONITOR_RETURNED_INVALID_TIMING_STATUS_BYTE = 0xC0262586;

    //
    // MessageId: ERROR_GRAPHICS_MCA_INVALID_CAPABILITIES_STRING
    //
    // MessageText:
    //
    // The monitor returned a DDC/CI capabilities string which did not comply
    // with the ACCESS.bus=3.0, DDC/CI=1.1, or MCCS=2 Revision=1 specification.
    //
    int ERROR_GRAPHICS_MCA_INVALID_CAPABILITIES_STRING = 0xC0262587;

    //
    // MessageId: ERROR_GRAPHICS_MCA_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal Monitor Configuration API error occured.
    //
    int ERROR_GRAPHICS_MCA_INTERNAL_ERROR = 0xC0262588;

    //
    // MessageId: ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_COMMAND
    //
    // MessageText:
    //
    // An operation failed because a DDC/CI message had an invalid value in its
    // command field.
    //
    int ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_COMMAND = 0xC0262589;

    //
    // MessageId: ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_LENGTH
    //
    // MessageText:
    //
    // An error occurred because the field length of a DDC/CI message contained
    // an invalid value.
    //
    int ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_LENGTH = 0xC026258A;

    //
    // MessageId: ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_CHECKSUM
    //
    // MessageText:
    //
    // An error occurred because the checksum field in a DDC/CI message did not
    // match the message's computed checksum value. This error implies that the
    // data was corrupted while it was being transmitted from a monitor to a
    // computer.
    //
    int ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_CHECKSUM = 0xC026258B;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_PHYSICAL_MONITOR_HANDLE
    //
    // MessageText:
    //
    // This function failed because an invalid monitor handle was passed to it.
    //
    int ERROR_GRAPHICS_INVALID_PHYSICAL_MONITOR_HANDLE = 0xC026258C;

    //
    // MessageId: ERROR_GRAPHICS_MONITOR_NO_LONGER_EXISTS
    //
    // MessageText:
    //
    // The operating system asynchronously destroyed the monitor which
    // corresponds to this handle because the operating system's state changed.
    // This error typically occurs because the monitor PDO associated with this
    // handle was removed, the monitor PDO associated with this handle was
    // stopped, or a display mode change occurred. A display mode change occurs
    // when windows sends a WM_DISPLAYCHANGE windows message to applications.
    //
    int ERROR_GRAPHICS_MONITOR_NO_LONGER_EXISTS = 0xC026258D;

    //
    // MessageId:
    // ERROR_GRAPHICS_DDCCI_CURRENT_CURRENT_VALUE_GREATER_THAN_MAXIMUM_VALUE
    //
    // MessageText:
    //
    // A continuous VCP code's current value is greater than its maximum value.
    // This error code indicates that a monitor returned an invalid value.
    //
    int ERROR_GRAPHICS_DDCCI_CURRENT_CURRENT_VALUE_GREATER_THAN_MAXIMUM_VALUE = 0xC02625D8;

    //
    // MessageId: ERROR_GRAPHICS_MCA_INVALID_VCP_VERSION
    //
    // MessageText:
    //
    // The monitor's VCP Version (0xDF) VCP code returned an invalid version
    // value.
    //
    int ERROR_GRAPHICS_MCA_INVALID_VCP_VERSION = 0xC02625D9;

    //
    // MessageId: ERROR_GRAPHICS_MCA_MONITOR_VIOLATES_MCCS_SPECIFICATION
    //
    // MessageText:
    //
    // The monitor does not comply with the MCCS specification it claims to
    // support.
    //
    int ERROR_GRAPHICS_MCA_MONITOR_VIOLATES_MCCS_SPECIFICATION = 0xC02625DA;

    //
    // MessageId: ERROR_GRAPHICS_MCA_MCCS_VERSION_MISMATCH
    //
    // MessageText:
    //
    // The MCCS version in a monitor's mccs_ver capability does not match the
    // MCCS version the monitor reports when the VCP Version (0xDF) VCP code is
    // used.
    //
    int ERROR_GRAPHICS_MCA_MCCS_VERSION_MISMATCH = 0xC02625DB;

    //
    // MessageId: ERROR_GRAPHICS_MCA_UNSUPPORTED_MCCS_VERSION
    //
    // MessageText:
    //
    // The Monitor Configuration API only works with monitors which support the
    // MCCS=1.0 specification, MCCS=2.0 specification or the MCCS=2.0 Revision=1
    // specification.
    //
    int ERROR_GRAPHICS_MCA_UNSUPPORTED_MCCS_VERSION = 0xC02625DC;

    //
    // MessageId: ERROR_GRAPHICS_MCA_INVALID_TECHNOLOGY_TYPE_RETURNED
    //
    // MessageText:
    //
    // The monitor returned an invalid monitor technology type. CRT, Plasma and
    // LCD (TFT) are examples of monitor technology types. This error implies
    // that the monitor violated the MCCS=2.0 or MCCS=2.0 Revision=1
    // specification.
    //
    int ERROR_GRAPHICS_MCA_INVALID_TECHNOLOGY_TYPE_RETURNED = 0xC02625DE;

    //
    // MessageId: ERROR_GRAPHICS_MCA_UNSUPPORTED_COLOR_TEMPERATURE
    //
    // MessageText:
    //
    // SetMonitorColorTemperature()'s caller passed a color temperature to it
    // which the current monitor did not support. This error implies that the
    // monitor violated the MCCS=2.0 or MCCS=2.0 Revision=1 specification.
    //
    int ERROR_GRAPHICS_MCA_UNSUPPORTED_COLOR_TEMPERATURE = 0xC02625DF;

    //
    // OPM, UAB, PVP and DDC/CI shared error codes {0x25E0..0x25ff}
    //
    //
    // MessageId: ERROR_GRAPHICS_ONLY_CONSOLE_SESSION_SUPPORTED
    //
    // MessageText:
    //
    // This function can only be used if a program is running in the local
    // console session. It cannot be used if the program is running on a remote
    // desktop session or on a terminal server session.
    //
    int ERROR_GRAPHICS_ONLY_CONSOLE_SESSION_SUPPORTED = 0xC02625E0;

    //
    // MessageId: ERROR_GRAPHICS_NO_DISPLAY_DEVICE_CORRESPONDS_TO_NAME
    //
    // MessageText:
    //
    // This function cannot find an actual GDI display device which corresponds
    // to the specified GDI display device name.
    //
    int ERROR_GRAPHICS_NO_DISPLAY_DEVICE_CORRESPONDS_TO_NAME = 0xC02625E1;

    //
    // MessageId: ERROR_GRAPHICS_DISPLAY_DEVICE_NOT_ATTACHED_TO_DESKTOP
    //
    // MessageText:
    //
    // The function failed because the specified GDI display device was not
    // attached to the Windows desktop.
    //
    int ERROR_GRAPHICS_DISPLAY_DEVICE_NOT_ATTACHED_TO_DESKTOP = 0xC02625E2;

    //
    // MessageId: ERROR_GRAPHICS_MIRRORING_DEVICES_NOT_SUPPORTED
    //
    // MessageText:
    //
    // This function does not support GDI mirroring display devices because GDI
    // mirroring display devices do not have any physical monitors associated
    // with them.
    //
    int ERROR_GRAPHICS_MIRRORING_DEVICES_NOT_SUPPORTED = 0xC02625E3;

    //
    // MessageId: ERROR_GRAPHICS_INVALID_POINTER
    //
    // MessageText:
    //
    // The function failed because an invalid pointer parameter was passed to
    // it. A pointer parameter is invalid if it is NULL, points to an invalid
    // address, points to a kernel mode address, or is not correctly aligned.
    //
    int ERROR_GRAPHICS_INVALID_POINTER = 0xC02625E4;

    //
    // MessageId: ERROR_GRAPHICS_NO_MONITORS_CORRESPOND_TO_DISPLAY_DEVICE
    //
    // MessageText:
    //
    // The function failed because the specified GDI device did not have any
    // monitors associated with it.
    //
    int ERROR_GRAPHICS_NO_MONITORS_CORRESPOND_TO_DISPLAY_DEVICE = 0xC02625E5;

    //
    // MessageId: ERROR_GRAPHICS_PARAMETER_ARRAY_TOO_SMALL
    //
    // MessageText:
    //
    // An array passed to the function cannot hold all of the data that the
    // function must copy into the array.
    //
    int ERROR_GRAPHICS_PARAMETER_ARRAY_TOO_SMALL = 0xC02625E6;

    //
    // MessageId: ERROR_GRAPHICS_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal error caused an operation to fail.
    //
    int ERROR_GRAPHICS_INTERNAL_ERROR = 0xC02625E7;

    //
    // MessageId: ERROR_GRAPHICS_SESSION_TYPE_CHANGE_IN_PROGRESS
    //
    // MessageText:
    //
    // The function failed because the current session is changing its type.
    // This function cannot be called when the current session is changing its
    // type. There are currently three types of sessions: console, disconnected
    // and remote (RDP or ICA).
    //
    int ERROR_GRAPHICS_SESSION_TYPE_CHANGE_IN_PROGRESS = 0xC02605E8;

    //
    // =
    // TPM Services and TPM Software Error Messages
    // =
    //
    // The TPM services and TPM software facilities are used by the various
    // TPM software components. There are two facilities because the services
    // errors are within the TCG-defined error space and the software errors
    // are not.
    //
    // The following are the subranges within the TPM Services facility.
    // The TPM hardware errors are defined in the document
    // TPM Main Specification=1.2 Part=2 TPM Structures.
    // The TBS errors are slotted into the TCG error namespace at the TBS layer.
    //
    // 0x0000 - 0x08ff TPM hardware errors
    // 0x4000 - 0x40ff TPM Base Services errors (tbssvc.dl;
    //
    // The following are the subranges within the TPM Software facility. The TBS
    // has two classes of errors - those that can be returned (the public
    // errors,
    // defined in the TBS spec), which are in the TPM services facility, and
    // those that are internal or implementation specific, which are here in the
    // TPM software facility.
    //
    // 0x0000 - 0x00ff TPM device driver errors (tpm.sys)
    // 0x0100 - 0x01ff TPM API errors (tpmapi.lib)
    // 0x0200 - 0x02ff TBS internal errors (tbssvc.dl;
    // 0x0300 - 0x03ff TPM Physical Presence errors
    //
    //
    // TPM hardware error codes {0x0000..0x08ff}
    // This space is further subdivided into hardware errors, vendor-specific
    // errors, and non-fatal errors.
    //
    //
    // TPM hardware errors {0x0000..0x003ff}
    //
    //
    // MessageId: TPM_E_ERROR_MASK
    //
    // MessageText:
    //
    // This is an error mask to convert TPM hardware errors to win errors.
    //
    int TPM_E_ERROR_MASK = 0x80280000;

    //
    // MessageId: TPM_E_AUTHFAIL
    //
    // MessageText:
    //
    // Authentication failed.
    //
    int TPM_E_AUTHFAIL = 0x80280001;

    //
    // MessageId: TPM_E_BADINDEX
    //
    // MessageText:
    //
    // The index to a PCR, DIR or other register is incorrect.
    //
    int TPM_E_BADINDEX = 0x80280002;

    //
    // MessageId: TPM_E_BAD_PARAMETER
    //
    // MessageText:
    //
    // One or more parameter is bad.
    //
    int TPM_E_BAD_PARAMETER = 0x80280003;

    //
    // MessageId: TPM_E_AUDITFAILURE
    //
    // MessageText:
    //
    // An operation completed successfully but the auditing of that operation
    // failed.
    //
    int TPM_E_AUDITFAILURE = 0x80280004;

    //
    // MessageId: TPM_E_CLEAR_DISABLED
    //
    // MessageText:
    //
    // The clear disable F;ag is set and all clear operations now require
    // physical access.
    //
    int TPM_E_CLEAR_DISABLED = 0x80280005;

    //
    // MessageId: TPM_E_DEACTIVATED
    //
    // MessageText:
    //
    // Activate the Trusted Platform Module (TPM).
    //
    int TPM_E_DEACTIVATED = 0x80280006;

    //
    // MessageId: TPM_E_DISABLED
    //
    // MessageText:
    //
    // Enable the Trusted Platform Module (TPM).
    //
    int TPM_E_DISABLED = 0x80280007;

    //
    // MessageId: TPM_E_DISABLED_CMD
    //
    // MessageText:
    //
    // The target command has been disabled.
    //
    int TPM_E_DISABLED_CMD = 0x80280008;

    //
    // MessageId: TPM_E_FAIL
    //
    // MessageText:
    //
    // The operation failed.
    //
    int TPM_E_FAIL = 0x80280009;

    //
    // MessageId: TPM_E_BAD_ORDINAL
    //
    // MessageText:
    //
    // The ordinal was unknown or inconsistent.
    //
    int TPM_E_BAD_ORDINAL = 0x8028000A;

    //
    // MessageId: TPM_E_INSTALL_DISABLED
    //
    // MessageText:
    //
    // The ability to install an owner is disabled.
    //
    int TPM_E_INSTALL_DISABLED = 0x8028000B;

    //
    // MessageId: TPM_E_INVALID_KEYHANDLE
    //
    // MessageText:
    //
    // The key handle cannot be intrepreted.
    //
    int TPM_E_INVALID_KEYHANDLE = 0x8028000C;

    //
    // MessageId: TPM_E_KEYNOTFOUND
    //
    // MessageText:
    //
    // The key handle points to an invalid key.
    //
    int TPM_E_KEYNOTFOUND = 0x8028000D;

    //
    // MessageId: TPM_E_INAPPROPRIATE_ENC
    //
    // MessageText:
    //
    // Unacceptable encryption scheme.
    //
    int TPM_E_INAPPROPRIATE_ENC = 0x8028000E;

    //
    // MessageId: TPM_E_MIGRATEFAIL
    //
    // MessageText:
    //
    // Migration authorization failed.
    //
    int TPM_E_MIGRATEFAIL = 0x8028000F;

    //
    // MessageId: TPM_E_INVALID_PCR_INFO
    //
    // MessageText:
    //
    // PCR information could not be interpreted.
    //
    int TPM_E_INVALID_PCR_INFO = 0x80280010;

    //
    // MessageId: TPM_E_NOSPACE
    //
    // MessageText:
    //
    // No room to load key.
    //
    int TPM_E_NOSPACE = 0x80280011;

    //
    // MessageId: TPM_E_NOSRK
    //
    // MessageText:
    //
    // There is no Storage Root Key (SRK) set.
    //
    int TPM_E_NOSRK = 0x80280012;

    //
    // MessageId: TPM_E_NOTSEALED_BLOB
    //
    // MessageText:
    //
    // An encrypted blob is invalid or was not created by this TPM.
    //
    int TPM_E_NOTSEALED_BLOB = 0x80280013;

    //
    // MessageId: TPM_E_OWNER_SET
    //
    // MessageText:
    //
    // The Trusted Platform Module (TPM) already has an owner.
    //
    int TPM_E_OWNER_SET = 0x80280014;

    //
    // MessageId: TPM_E_RESOURCES
    //
    // MessageText:
    //
    // The TPM has insufficient internal resources to perform the requested
    // action.
    //
    int TPM_E_RESOURCES = 0x80280015;

    //
    // MessageId: TPM_E_SHORTRANDOM
    //
    // MessageText:
    //
    // A random string was too short.
    //
    int TPM_E_SHORTRANDOM = 0x80280016;

    //
    // MessageId: TPM_E_SIZE
    //
    // MessageText:
    //
    // The TPM does not have the space to perform the operation.
    //
    int TPM_E_SIZE = 0x80280017;

    //
    // MessageId: TPM_E_WRONGPCRVAL
    //
    // MessageText:
    //
    // The named PCR value does not match the current PCR value.
    //
    int TPM_E_WRONGPCRVAL = 0x80280018;

    //
    // MessageId: TPM_E_BAD_PARAM_SIZE
    //
    // MessageText:
    //
    // The paramSize argument to the command has the incorrect value .
    //
    int TPM_E_BAD_PARAM_SIZE = 0x80280019;

    //
    // MessageId: TPM_E_SHA_THREAD
    //
    // MessageText:
    //
    // There is no existing SHA-1 thread.
    //
    int TPM_E_SHA_THREAD = 0x8028001A;

    //
    // MessageId: TPM_E_SHA_ERROR
    //
    // MessageText:
    //
    // The calculation is unable to proceed because the existing SHA-1 thread
    // has already encountered an error.
    //
    int TPM_E_SHA_ERROR = 0x8028001B;

    //
    // MessageId: TPM_E_FAILEDSELFTEST
    //
    // MessageText:
    //
    // Self-test has failed and the TPM has shutdown.
    //
    int TPM_E_FAILEDSELFTEST = 0x8028001C;

    //
    // MessageId: TPM_E_AUTH2FAIL
    //
    // MessageText:
    //
    // The authorization for the second key in a=2 key function failed
    // authorization.
    //
    int TPM_E_AUTH2FAIL = 0x8028001D;

    //
    // MessageId: TPM_E_BADTAG
    //
    // MessageText:
    //
    // The tag value sent to for a command is invalid.
    //
    int TPM_E_BADTAG = 0x8028001E;

    //
    // MessageId: TPM_E_IOERROR
    //
    // MessageText:
    //
    // An IO error occurred transmitting information to the TPM.
    //
    int TPM_E_IOERROR = 0x8028001F;

    //
    // MessageId: TPM_E_ENCRYPT_ERROR
    //
    // MessageText:
    //
    // The encryption process had a problem.
    //
    int TPM_E_ENCRYPT_ERROR = 0x80280020;

    //
    // MessageId: TPM_E_DECRYPT_ERROR
    //
    // MessageText:
    //
    // The decryption process did not complete.
    //
    int TPM_E_DECRYPT_ERROR = 0x80280021;

    //
    // MessageId: TPM_E_INVALID_AUTHHANDLE
    //
    // MessageText:
    //
    // An invalid handle was used.
    //
    int TPM_E_INVALID_AUTHHANDLE = 0x80280022;

    //
    // MessageId: TPM_E_NO_ENDORSEMENT
    //
    // MessageText:
    //
    // The TPM does not have an Endorsement Key (EK) installed.
    //
    int TPM_E_NO_ENDORSEMENT = 0x80280023;

    //
    // MessageId: TPM_E_INVALID_KEYUSAGE
    //
    // MessageText:
    //
    // The usage of a key is not allowed.
    //
    int TPM_E_INVALID_KEYUSAGE = 0x80280024;

    //
    // MessageId: TPM_E_WRONG_ENTITYTYPE
    //
    // MessageText:
    //
    // The submitted entity type is not allowed.
    //
    int TPM_E_WRONG_ENTITYTYPE = 0x80280025;

    //
    // MessageId: TPM_E_INVALID_POSTINIT
    //
    // MessageText:
    //
    // The command was received in the wrong sequence relative to TPM_Init and a
    // subsequent TPM_Startup.
    //
    int TPM_E_INVALID_POSTINIT = 0x80280026;

    //
    // MessageId: TPM_E_INAPPROPRIATE_SIG
    //
    // MessageText:
    //
    // Signed data cannot include additional DER information.
    //
    int TPM_E_INAPPROPRIATE_SIG = 0x80280027;

    //
    // MessageId: TPM_E_BAD_KEY_PROPERTY
    //
    // MessageText:
    //
    // The key properties in TPM_KEY_PARMs are not supported by this TPM.
    //
    int TPM_E_BAD_KEY_PROPERTY = 0x80280028;

    //
    // MessageId: TPM_E_BAD_MIGRATION
    //
    // MessageText:
    //
    // The migration properties of this key are incorrect.
    //
    int TPM_E_BAD_MIGRATION = 0x80280029;

    //
    // MessageId: TPM_E_BAD_SCHEME
    //
    // MessageText:
    //
    // The signature or encryption scheme for this key is incorrect or not
    // permitted in this situation.
    //
    int TPM_E_BAD_SCHEME = 0x8028002A;

    //
    // MessageId: TPM_E_BAD_DATASIZE
    //
    // MessageText:
    //
    // The size of the data (or blob) parameter is bad or inconsistent with the
    // referenced key.
    //
    int TPM_E_BAD_DATASIZE = 0x8028002B;

    //
    // MessageId: TPM_E_BAD_MODE
    //
    // MessageText:
    //
    // A mode parameter is bad, such as capArea or subCapArea for
    // TPM_GetCapability, phsicalPresence parameter for TPM_PhysicalPresence, or
    // migrationType for TPM_CreateMigrationBlob.
    //
    int TPM_E_BAD_MODE = 0x8028002C;

    //
    // MessageId: TPM_E_BAD_PRESENCE
    //
    // MessageText:
    //
    // Either the physicalPresence or physicalPresenceLock bits have the wrong
    // value.
    //
    int TPM_E_BAD_PRESENCE = 0x8028002D;

    //
    // MessageId: TPM_E_BAD_VERSION
    //
    // MessageText:
    //
    // The TPM cannot perform this version of the capability.
    //
    int TPM_E_BAD_VERSION = 0x8028002E;

    //
    // MessageId: TPM_E_NO_WRAP_TRANSPORT
    //
    // MessageText:
    //
    // The TPM does not allow for wrapped transport sessions.
    //
    int TPM_E_NO_WRAP_TRANSPORT = 0x8028002F;

    //
    // MessageId: TPM_E_AUDITFAIL_UNSUCCESSFUL
    //
    // MessageText:
    //
    // TPM audit construction failed and the underlying command was returning a
    // failure code also.
    //
    int TPM_E_AUDITFAIL_UNSUCCESSFUL = 0x80280030;

    //
    // MessageId: TPM_E_AUDITFAIL_SUCCESSFUL
    //
    // MessageText:
    //
    // TPM audit construction failed and the underlying command was returning
    // success.
    //
    int TPM_E_AUDITFAIL_SUCCESSFUL = 0x80280031;

    //
    // MessageId: TPM_E_NOTRESETABLE
    //
    // MessageText:
    //
    // Attempt to reset a PCR register that does not have the resettable
    // attribute.
    //
    int TPM_E_NOTRESETABLE = 0x80280032;

    //
    // MessageId: TPM_E_NOTLOCAL
    //
    // MessageText:
    //
    // Attempt to reset a PCR register that requires locality and locality
    // modifier not part of command transport.
    //
    int TPM_E_NOTLOCAL = 0x80280033;

    //
    // MessageId: TPM_E_BAD_TYPE
    //
    // MessageText:
    //
    // Make identity blob not properly typed.
    //
    int TPM_E_BAD_TYPE = 0x80280034;

    //
    // MessageId: TPM_E_INVALID_RESOURCE
    //
    // MessageText:
    //
    // When saving context identified resource type does not match actual
    // resource.
    //
    int TPM_E_INVALID_RESOURCE = 0x80280035;

    //
    // MessageId: TPM_E_NOTFIPS
    //
    // MessageText:
    //
    // The TPM is attempting to execute a command only available when in FIPS
    // mode.
    //
    int TPM_E_NOTFIPS = 0x80280036;

    //
    // MessageId: TPM_E_INVALID_FAMILY
    //
    // MessageText:
    //
    // The command is attempting to use an invalid family ID.
    //
    int TPM_E_INVALID_FAMILY = 0x80280037;

    //
    // MessageId: TPM_E_NO_NV_PERMISSION
    //
    // MessageText:
    //
    // The permission to manipulate the NV storage is not available.
    //
    int TPM_E_NO_NV_PERMISSION = 0x80280038;

    //
    // MessageId: TPM_E_REQUIRES_SIGN
    //
    // MessageText:
    //
    // The operation requires a signed command.
    //
    int TPM_E_REQUIRES_SIGN = 0x80280039;

    //
    // MessageId: TPM_E_KEY_NOTSUPPORTED
    //
    // MessageText:
    //
    // Wrong operation to load an NV key.
    //
    int TPM_E_KEY_NOTSUPPORTED = 0x8028003A;

    //
    // MessageId: TPM_E_AUTH_CONF;ICT
    //
    // MessageText:
    //
    // NV_LoadKey blob requires both owner and blob authorization.
    //
    int TPM_E_AUTH_CONFLICT = 0x8028003B;

    //
    // MessageId: TPM_E_AREA_LOCKED
    //
    // MessageText:
    //
    // The NV area is locked and not writtable.
    //
    int TPM_E_AREA_LOCKED = 0x8028003C;

    //
    // MessageId: TPM_E_BAD_LOCALITY
    //
    // MessageText:
    //
    // The locality is incorrect for the attempted operation.
    //
    int TPM_E_BAD_LOCALITY = 0x8028003D;

    //
    // MessageId: TPM_E_READ_ONLY
    //
    // MessageText:
    //
    // The NV area is read only and can't be written to.
    //
    int TPM_E_READ_ONLY = 0x8028003E;

    //
    // MessageId: TPM_E_PER_NOWRITE
    //
    // MessageText:
    //
    // There is no protection on the write to the NV area.
    //
    int TPM_E_PER_NOWRITE = 0x8028003F;

    //
    // MessageId: TPM_E_FAMILYCOUNT
    //
    // MessageText:
    //
    // The family count value does not match.
    //
    int TPM_E_FAMILYCOUNT = 0x80280040;

    //
    // MessageId: TPM_E_WRITE_LOCKED
    //
    // MessageText:
    //
    // The NV area has already been written to.
    //
    int TPM_E_WRITE_LOCKED = 0x80280041;

    //
    // MessageId: TPM_E_BAD_ATTRIBUTES
    //
    // MessageText:
    //
    // The NV area attributes conFLIct.
    //
    int TPM_E_BAD_ATTRIBUTES = 0x80280042;

    //
    // MessageId: TPM_E_INVALID_STRUCTURE
    //
    // MessageText:
    //
    // The structure tag and version are invalid or inconsistent.
    //
    int TPM_E_INVALID_STRUCTURE = 0x80280043;

    //
    // MessageId: TPM_E_KEY_OWNER_CONTROL
    //
    // MessageText:
    //
    // The key is under control of the TPM Owner and can only be evicted by the
    // TPM Owner.
    //
    int TPM_E_KEY_OWNER_CONTROL = 0x80280044;

    //
    // MessageId: TPM_E_BAD_COUNTER
    //
    // MessageText:
    //
    // The counter handle is incorrect.
    //
    int TPM_E_BAD_COUNTER = 0x80280045;

    //
    // MessageId: TPM_E_NOT_FULLWRITE
    //
    // MessageText:
    //
    // The write is not a complete write of the area.
    //
    int TPM_E_NOT_FULLWRITE = 0x80280046;

    //
    // MessageId: TPM_E_CONTEXT_GAP
    //
    // MessageText:
    //
    // The gap between saved context counts is too large.
    //
    int TPM_E_CONTEXT_GAP = 0x80280047;

    //
    // MessageId: TPM_E_MAXNVWRITES
    //
    // MessageText:
    //
    // The maximum number of NV writes without an owner has been exceeded.
    //
    int TPM_E_MAXNVWRITES = 0x80280048;

    //
    // MessageId: TPM_E_NOOPERATOR
    //
    // MessageText:
    //
    // No operator AuthData value is set.
    //
    int TPM_E_NOOPERATOR = 0x80280049;

    //
    // MessageId: TPM_E_RESOURCEMISSING
    //
    // MessageText:
    //
    // The resource pointed to by context is not loaded.
    //
    int TPM_E_RESOURCEMISSING = 0x8028004A;

    //
    // MessageId: TPM_E_DELEGATE_LOCK
    //
    // MessageText:
    //
    // The delegate administration is locked.
    //
    int TPM_E_DELEGATE_LOCK = 0x8028004B;

    //
    // MessageId: TPM_E_DELEGATE_FAMILY
    //
    // MessageText:
    //
    // Attempt to manage a family other then the delegated family.
    //
    int TPM_E_DELEGATE_FAMILY = 0x8028004C;

    //
    // MessageId: TPM_E_DELEGATE_ADMIN
    //
    // MessageText:
    //
    // Delegation table management not enabled.
    //
    int TPM_E_DELEGATE_ADMIN = 0x8028004D;

    //
    // MessageId: TPM_E_TRANSPORT_NOTEXCLUSIVE
    //
    // MessageText:
    //
    // There was a command executed outside of an exclusive transport session.
    //
    int TPM_E_TRANSPORT_NOTEXCLUSIVE = 0x8028004E;

    //
    // MessageId: TPM_E_OWNER_CONTROL
    //
    // MessageText:
    //
    // Attempt to context save a owner evict controlled key.
    //
    int TPM_E_OWNER_CONTROL = 0x8028004F;

    //
    // MessageId: TPM_E_DAA_RESOURCES
    //
    // MessageText:
    //
    // The DAA command has no resources availble to execute the command.
    //
    int TPM_E_DAA_RESOURCES = 0x80280050;

    //
    // MessageId: TPM_E_DAA_INPUT_DATA0
    //
    // MessageText:
    //
    // The consistency check on DAA parameter inputData0 has failed.
    //
    int TPM_E_DAA_INPUT_DATA0 = 0x80280051;

    //
    // MessageId: TPM_E_DAA_INPUT_DATA1
    //
    // MessageText:
    //
    // The consistency check on DAA parameter inputData1 has failed.
    //
    int TPM_E_DAA_INPUT_DATA1 = 0x80280052;

    //
    // MessageId: TPM_E_DAA_ISSUER_SETTINGS
    //
    // MessageText:
    //
    // The consistency check on DAA_issuerSettings has failed.
    //
    int TPM_E_DAA_ISSUER_SETTINGS = 0x80280053;

    //
    // MessageId: TPM_E_DAA_TPM_SETTINGS
    //
    // MessageText:
    //
    // The consistency check on DAA_tpmSpecific has failed.
    //
    int TPM_E_DAA_TPM_SETTINGS = 0x80280054;

    //
    // MessageId: TPM_E_DAA_STAGE
    //
    // MessageText:
    //
    // The atomic process indicated by the submitted DAA command is not the
    // expected process.
    //
    int TPM_E_DAA_STAGE = 0x80280055;

    //
    // MessageId: TPM_E_DAA_ISSUER_VALIDITY
    //
    // MessageText:
    //
    // The issuer's validity check has detected an inconsistency.
    //
    int TPM_E_DAA_ISSUER_VALIDITY = 0x80280056;

    //
    // MessageId: TPM_E_DAA_WRONG_W
    //
    // MessageText:
    //
    // The consistency check on w has failed.
    //
    int TPM_E_DAA_WRONG_W = 0x80280057;

    //
    // MessageId: TPM_E_BAD_HANDLE
    //
    // MessageText:
    //
    // The handle is incorrect.
    //
    int TPM_E_BAD_HANDLE = 0x80280058;

    //
    // MessageId: TPM_E_BAD_DELEGATE
    //
    // MessageText:
    //
    // Delegation is not correct.
    //
    int TPM_E_BAD_DELEGATE = 0x80280059;

    //
    // MessageId: TPM_E_BADCONTEXT
    //
    // MessageText:
    //
    // The context blob is invalid.
    //
    int TPM_E_BADCONTEXT = 0x8028005A;

    //
    // MessageId: TPM_E_TOOMANYCONTEXTS
    //
    // MessageText:
    //
    // Too many contexts held by the TPM.
    //
    int TPM_E_TOOMANYCONTEXTS = 0x8028005B;

    //
    // MessageId: TPM_E_MA_TICKET_SIGNATURE
    //
    // MessageText:
    //
    // Migration authority signature validation failure.
    //
    int TPM_E_MA_TICKET_SIGNATURE = 0x8028005C;

    //
    // MessageId: TPM_E_MA_DESTINATION
    //
    // MessageText:
    //
    // Migration destination not authenticated.
    //
    int TPM_E_MA_DESTINATION = 0x8028005D;

    //
    // MessageId: TPM_E_MA_SOURCE
    //
    // MessageText:
    //
    // Migration source incorrect.
    //
    int TPM_E_MA_SOURCE = 0x8028005E;

    //
    // MessageId: TPM_E_MA_AUTHORITY
    //
    // MessageText:
    //
    // Incorrect migration authority.
    //
    int TPM_E_MA_AUTHORITY = 0x8028005F;

    //
    // MessageId: TPM_E_PERMANENTEK
    //
    // MessageText:
    //
    // Attempt to revoke the EK and the EK is not revocable.
    //
    int TPM_E_PERMANENTEK = 0x80280061;

    //
    // MessageId: TPM_E_BAD_SIGNATURE
    //
    // MessageText:
    //
    // Bad signature of CMK ticket.
    //
    int TPM_E_BAD_SIGNATURE = 0x80280062;

    //
    // MessageId: TPM_E_NOCONTEXTSPACE
    //
    // MessageText:
    //
    // There is no room in the context list for additional contexts.
    //
    int TPM_E_NOCONTEXTSPACE = 0x80280063;

    //
    // TPM vendor specific hardware errors {0x0400..0x04ff}
    //
    //
    // MessageId: TPM_E_COMMAND_BLOCKED
    //
    // MessageText:
    //
    // The command was blocked.
    //
    int TPM_E_COMMAND_BLOCKED = 0x80280400;

    //
    // MessageId: TPM_E_INVALID_HANDLE
    //
    // MessageText:
    //
    // The specified handle was not found.
    //
    int TPM_E_INVALID_HANDLE = 0x80280401;

    //
    // MessageId: TPM_E_DUPLICATE_VHANDLE
    //
    // MessageText:
    //
    // The TPM returned a duplicate handle and the command needs to be
    // resubmitted.
    //
    int TPM_E_DUPLICATE_VHANDLE = 0x80280402;

    //
    // MessageId: TPM_E_EMBEDDED_COMMAND_BLOCKED
    //
    // MessageText:
    //
    // The command within the transport was blocked.
    //
    int TPM_E_EMBEDDED_COMMAND_BLOCKED = 0x80280403;

    //
    // MessageId: TPM_E_EMBEDDED_COMMAND_UNSUPPORTED
    //
    // MessageText:
    //
    // The command within the transport is not supported.
    //
    int TPM_E_EMBEDDED_COMMAND_UNSUPPORTED = 0x80280404;

    //
    // TPM non-fatal hardware errors {0x0800..0x08ff}
    //
    //
    // MessageId: TPM_E_RETRY
    //
    // MessageText:
    //
    // The TPM is too busy to respond to the command immediately, but the
    // command could be resubmitted at a later time.
    //
    int TPM_E_RETRY = 0x80280800;

    //
    // MessageId: TPM_E_NEEDS_SELFTEST
    //
    // MessageText:
    //
    // SelfTestFull has not been run.
    //
    int TPM_E_NEEDS_SELFTEST = 0x80280801;

    //
    // MessageId: TPM_E_DOING_SELFTEST
    //
    // MessageText:
    //
    // The TPM is currently executing a full selftest.
    //
    int TPM_E_DOING_SELFTEST = 0x80280802;

    //
    // MessageId: TPM_E_DEFEND_LOCK_RUNNING
    //
    // MessageText:
    //
    // The TPM is defending against dictionary attacks and is in a time-out
    // period.
    //
    int TPM_E_DEFEND_LOCK_RUNNING = 0x80280803;

    //
    // TPM Base Services error codes {0x4000..0x40ff}
    //
    //
    // MessageId: TBS_E_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal software error has been detected.
    //
    int TBS_E_INTERNAL_ERROR = 0x80284001;

    //
    // MessageId: TBS_E_BAD_PARAMETER
    //
    // MessageText:
    //
    // One or more input parameters is bad.
    //
    int TBS_E_BAD_PARAMETER = 0x80284002;

    //
    // MessageId: TBS_E_INVALID_OUTPUT_POINTER
    //
    // MessageText:
    //
    // A specified output pointer is bad.
    //
    int TBS_E_INVALID_OUTPUT_POINTER = 0x80284003;

    //
    // MessageId: TBS_E_INVALID_CONTEXT
    //
    // MessageText:
    //
    // The specified context handle does not refer to a valid context.
    //
    int TBS_E_INVALID_CONTEXT = 0x80284004;

    //
    // MessageId: TBS_E_INSUFFICIENT_BUFFER
    //
    // MessageText:
    //
    // A specified output buffer is too small.
    //
    int TBS_E_INSUFFICIENT_BUFFER = 0x80284005;

    //
    // MessageId: TBS_E_IOERROR
    //
    // MessageText:
    //
    // An error occurred while communicating with the TPM.
    //
    int TBS_E_IOERROR = 0x80284006;

    //
    // MessageId: TBS_E_INVALID_CONTEXT_PARAM
    //
    // MessageText:
    //
    // One or more context parameters is invalid.
    //
    int TBS_E_INVALID_CONTEXT_PARAM = 0x80284007;

    //
    // MessageId: TBS_E_SERVICE_NOT_RUNNING
    //
    // MessageText:
    //
    // The TBS service is not running and could not be started.
    //
    int TBS_E_SERVICE_NOT_RUNNING = 0x80284008;

    //
    // MessageId: TBS_E_TOO_MANY_TBS_CONTEXTS
    //
    // MessageText:
    //
    // A new context could not be created because there are too many open
    // contexts.
    //
    int TBS_E_TOO_MANY_TBS_CONTEXTS = 0x80284009;

    //
    // MessageId: TBS_E_TOO_MANY_RESOURCES
    //
    // MessageText:
    //
    // A new virtual resource could not be created because there are too many
    // open
    // virtual resources.
    //
    int TBS_E_TOO_MANY_RESOURCES = 0x8028400A;

    //
    // MessageId: TBS_E_SERVICE_START_PENDING
    //
    // MessageText:
    //
    // The TBS service has been started but is not yet running.
    //
    int TBS_E_SERVICE_START_PENDING = 0x8028400B;

    //
    // MessageId: TBS_E_PPI_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The physical presence interface is not supported.
    //
    int TBS_E_PPI_NOT_SUPPORTED = 0x8028400C;

    //
    // MessageId: TBS_E_COMMAND_CANCELED
    //
    // MessageText:
    //
    // The command was canceled.
    //
    int TBS_E_COMMAND_CANCELED = 0x8028400D;

    //
    // MessageId: TBS_E_BUFFER_TOO_LARGE
    //
    // MessageText:
    //
    // The input or output buffer is too large.
    //
    int TBS_E_BUFFER_TOO_LARGE = 0x8028400E;

    //
    // MessageId: TBS_E_TPM_NOT_FOUND
    //
    // MessageText:
    //
    // A compatible Trusted Platform Module (TPM) Security Device cannot be
    // found on this computer.
    //
    int TBS_E_TPM_NOT_FOUND = 0x8028400F;

    //
    // MessageId: TBS_E_SERVICE_DISABLED
    //
    // MessageText:
    //
    // The TBS service has been disabled.
    //
    int TBS_E_SERVICE_DISABLED = 0x80284010;

    //
    // TPM API error codes {0x0100..0x01ff}
    //
    //
    // MessageId: TPMAPI_E_INVALID_STATE
    //
    // MessageText:
    //
    // The command buffer is not in the correct state.
    //
    int TPMAPI_E_INVALID_STATE = 0x80290100;

    //
    // MessageId: TPMAPI_E_NOT_ENOUGH_DATA
    //
    // MessageText:
    //
    // The command buffer does not contain enough data to satisfy the request.
    //
    int TPMAPI_E_NOT_ENOUGH_DATA = 0x80290101;

    //
    // MessageId: TPMAPI_E_TOO_MUCH_DATA
    //
    // MessageText:
    //
    // The command buffer cannot contain any more data.
    //
    int TPMAPI_E_TOO_MUCH_DATA = 0x80290102;

    //
    // MessageId: TPMAPI_E_INVALID_OUTPUT_POINTER
    //
    // MessageText:
    //
    // One or more output parameters was NULL or invalid.
    //
    int TPMAPI_E_INVALID_OUTPUT_POINTER = 0x80290103;

    //
    // MessageId: TPMAPI_E_INVALID_PARAMETER
    //
    // MessageText:
    //
    // One or more input parameters is invalid.
    //
    int TPMAPI_E_INVALID_PARAMETER = 0x80290104;

    //
    // MessageId: TPMAPI_E_OUT_OF_MEMORY
    //
    // MessageText:
    //
    // Not enough memory was available to satisfy the request.
    //
    int TPMAPI_E_OUT_OF_MEMORY = 0x80290105;

    //
    // MessageId: TPMAPI_E_BUFFER_TOO_SMALL
    //
    // MessageText:
    //
    // The specified buffer was too small.
    //
    int TPMAPI_E_BUFFER_TOO_SMALL = 0x80290106;

    //
    // MessageId: TPMAPI_E_INTERNAL_ERROR
    //
    // MessageText:
    //
    // An internal error was detected.
    //
    int TPMAPI_E_INTERNAL_ERROR = 0x80290107;

    //
    // MessageId: TPMAPI_E_ACCESS_DENIED
    //
    // MessageText:
    //
    // The caller does not have the appropriate rights to perform the requested
    // operation.
    //
    int TPMAPI_E_ACCESS_DENIED = 0x80290108;

    //
    // MessageId: TPMAPI_E_AUTHORIZATION_FAILED
    //
    // MessageText:
    //
    // The specified authorization information was invalid.
    //
    int TPMAPI_E_AUTHORIZATION_FAILED = 0x80290109;

    //
    // MessageId: TPMAPI_E_INVALID_CONTEXT_HANDLE
    //
    // MessageText:
    //
    // The specified context handle was not valid.
    //
    int TPMAPI_E_INVALID_CONTEXT_HANDLE = 0x8029010A;

    //
    // MessageId: TPMAPI_E_TBS_COMMUNICATION_ERROR
    //
    // MessageText:
    //
    // An error occurred while communicating with the TBS.
    //
    int TPMAPI_E_TBS_COMMUNICATION_ERROR = 0x8029010B;

    //
    // MessageId: TPMAPI_E_TPM_COMMAND_ERROR
    //
    // MessageText:
    //
    // The TPM returned an unexpected result.
    //
    int TPMAPI_E_TPM_COMMAND_ERROR = 0x8029010C;

    //
    // MessageId: TPMAPI_E_MESSAGE_TOO_LARGE
    //
    // MessageText:
    //
    // The message was too large for the encoding scheme.
    //
    int TPMAPI_E_MESSAGE_TOO_LARGE = 0x8029010D;

    //
    // MessageId: TPMAPI_E_INVALID_ENCODING
    //
    // MessageText:
    //
    // The encoding in the blob was not recognized.
    //
    int TPMAPI_E_INVALID_ENCODING = 0x8029010E;

    //
    // MessageId: TPMAPI_E_INVALID_KEY_SIZE
    //
    // MessageText:
    //
    // The key size is not valid.
    //
    int TPMAPI_E_INVALID_KEY_SIZE = 0x8029010F;

    //
    // MessageId: TPMAPI_E_ENCRYPTION_FAILED
    //
    // MessageText:
    //
    // The encryption operation failed.
    //
    int TPMAPI_E_ENCRYPTION_FAILED = 0x80290110;

    //
    // MessageId: TPMAPI_E_INVALID_KEY_PARAMS
    //
    // MessageText:
    //
    // The key parameters structure was not valid
    //
    int TPMAPI_E_INVALID_KEY_PARAMS = 0x80290111;

    //
    // MessageId: TPMAPI_E_INVALID_MIGRATION_AUTHORIZATION_BLOB
    //
    // MessageText:
    //
    // The requested supplied data does not appear to be a valid migration
    // authorization blob.
    //
    int TPMAPI_E_INVALID_MIGRATION_AUTHORIZATION_BLOB = 0x80290112;

    //
    // MessageId: TPMAPI_E_INVALID_PCR_INDEX
    //
    // MessageText:
    //
    // The specified PCR index was invalid
    //
    int TPMAPI_E_INVALID_PCR_INDEX = 0x80290113;

    //
    // MessageId: TPMAPI_E_INVALID_DELEGATE_BLOB
    //
    // MessageText:
    //
    // The data given does not appear to be a valid delegate blob.
    //
    int TPMAPI_E_INVALID_DELEGATE_BLOB = 0x80290114;

    //
    // MessageId: TPMAPI_E_INVALID_CONTEXT_PARAMS
    //
    // MessageText:
    //
    // One or more of the specified context parameters was not valid.
    //
    int TPMAPI_E_INVALID_CONTEXT_PARAMS = 0x80290115;

    //
    // MessageId: TPMAPI_E_INVALID_KEY_BLOB
    //
    // MessageText:
    //
    // The data given does not appear to be a valid key blob
    //
    int TPMAPI_E_INVALID_KEY_BLOB = 0x80290116;

    //
    // MessageId: TPMAPI_E_INVALID_PCR_DATA
    //
    // MessageText:
    //
    // The specified PCR data was invalid.
    //
    int TPMAPI_E_INVALID_PCR_DATA = 0x80290117;

    //
    // MessageId: TPMAPI_E_INVALID_OWNER_AUTH
    //
    // MessageText:
    //
    // The format of the owner auth data was invalid.
    //
    int TPMAPI_E_INVALID_OWNER_AUTH = 0x80290118;

    //
    // MessageId: TPMAPI_E_FIPS_RNG_CHECK_FAILED
    //
    // MessageText:
    //
    // The random number generated did not pass FIPS RNG check.
    //
    int TPMAPI_E_FIPS_RNG_CHECK_FAILED = 0x80290119;

    //
    // TBS implementation error codes {0x0200..0x02ff}
    //
    //
    // MessageId: TBSIMP_E_BUFFER_TOO_SMALL
    //
    // MessageText:
    //
    // The specified buffer was too small.
    //
    int TBSIMP_E_BUFFER_TOO_SMALL = 0x80290200;

    //
    // MessageId: TBSIMP_E_CLEANUP_FAILED
    //
    // MessageText:
    //
    // The context could not be cleaned up.
    //
    int TBSIMP_E_CLEANUP_FAILED = 0x80290201;

    //
    // MessageId: TBSIMP_E_INVALID_CONTEXT_HANDLE
    //
    // MessageText:
    //
    // The specified context handle is invalid.
    //
    int TBSIMP_E_INVALID_CONTEXT_HANDLE = 0x80290202;

    //
    // MessageId: TBSIMP_E_INVALID_CONTEXT_PARAM
    //
    // MessageText:
    //
    // An invalid context parameter was specified.
    //
    int TBSIMP_E_INVALID_CONTEXT_PARAM = 0x80290203;

    //
    // MessageId: TBSIMP_E_TPM_ERROR
    //
    // MessageText:
    //
    // An error occurred while communicating with the TPM
    //
    int TBSIMP_E_TPM_ERROR = 0x80290204;

    //
    // MessageId: TBSIMP_E_HASH_BAD_KEY
    //
    // MessageText:
    //
    // No entry with the specified key was found.
    //
    int TBSIMP_E_HASH_BAD_KEY = 0x80290205;

    //
    // MessageId: TBSIMP_E_DUPLICATE_VHANDLE
    //
    // MessageText:
    //
    // The specified virtual handle matches a virtual handle already in use.
    //
    int TBSIMP_E_DUPLICATE_VHANDLE = 0x80290206;

    //
    // MessageId: TBSIMP_E_INVALID_OUTPUT_POINTER
    //
    // MessageText:
    //
    // The pointer to the returned handle location was NULL or invalid
    //
    int TBSIMP_E_INVALID_OUTPUT_POINTER = 0x80290207;

    //
    // MessageId: TBSIMP_E_INVALID_PARAMETER
    //
    // MessageText:
    //
    // One or more parameters is invalid
    //
    int TBSIMP_E_INVALID_PARAMETER = 0x80290208;

    //
    // MessageId: TBSIMP_E_RPC_INIT_FAILED
    //
    // MessageText:
    //
    // The RPC subsystem could not be initialized.
    //
    int TBSIMP_E_RPC_INIT_FAILED = 0x80290209;

    //
    // MessageId: TBSIMP_E_SCHEDULER_NOT_RUNNING
    //
    // MessageText:
    //
    // The TBS scheduler is not running.
    //
    int TBSIMP_E_SCHEDULER_NOT_RUNNING = 0x8029020A;

    //
    // MessageId: TBSIMP_E_COMMAND_CANCELED
    //
    // MessageText:
    //
    // The command was canceled.
    //
    int TBSIMP_E_COMMAND_CANCELED = 0x8029020B;

    //
    // MessageId: TBSIMP_E_OUT_OF_MEMORY
    //
    // MessageText:
    //
    // There was not enough memory to fulfill the request
    //
    int TBSIMP_E_OUT_OF_MEMORY = 0x8029020C;

    //
    // MessageId: TBSIMP_E_LIST_NO_MORE_ITEMS
    //
    // MessageText:
    //
    // The specified list is empty, or the iteration has reached the end of the
    // list.
    //
    int TBSIMP_E_LIST_NO_MORE_ITEMS = 0x8029020D;

    //
    // MessageId: TBSIMP_E_LIST_NOT_FOUND
    //
    // MessageText:
    //
    // The specified item was not found in the list.
    //
    int TBSIMP_E_LIST_NOT_FOUND = 0x8029020E;

    //
    // MessageId: TBSIMP_E_NOT_ENOUGH_SPACE
    //
    // MessageText:
    //
    // The TPM does not have enough space to load the requested resource.
    //
    int TBSIMP_E_NOT_ENOUGH_SPACE = 0x8029020F;

    //
    // MessageId: TBSIMP_E_NOT_ENOUGH_TPM_CONTEXTS
    //
    // MessageText:
    //
    // There are too many TPM contexts in use.
    //
    int TBSIMP_E_NOT_ENOUGH_TPM_CONTEXTS = 0x80290210;

    //
    // MessageId: TBSIMP_E_COMMAND_FAILED
    //
    // MessageText:
    //
    // The TPM command failed.
    //
    int TBSIMP_E_COMMAND_FAILED = 0x80290211;

    //
    // MessageId: TBSIMP_E_UNKNOWN_ORDINAL
    //
    // MessageText:
    //
    // The TBS does not recognize the specified ordinal.
    //
    int TBSIMP_E_UNKNOWN_ORDINAL = 0x80290212;

    //
    // MessageId: TBSIMP_E_RESOURCE_EXPIRED
    //
    // MessageText:
    //
    // The requested resource is no longer available.
    //
    int TBSIMP_E_RESOURCE_EXPIRED = 0x80290213;

    //
    // MessageId: TBSIMP_E_INVALID_RESOURCE
    //
    // MessageText:
    //
    // The resource type did not match.
    //
    int TBSIMP_E_INVALID_RESOURCE = 0x80290214;

    //
    // MessageId: TBSIMP_E_NOTHING_TO_UNLOAD
    //
    // MessageText:
    //
    // No resources can be unloaded.
    //
    int TBSIMP_E_NOTHING_TO_UNLOAD = 0x80290215;

    //
    // MessageId: TBSIMP_E_HASH_TABLE_FULL
    //
    // MessageText:
    //
    // No new entries can be added to the hash table.
    //
    int TBSIMP_E_HASH_TABLE_FULL = 0x80290216;

    //
    // MessageId: TBSIMP_E_TOO_MANY_TBS_CONTEXTS
    //
    // MessageText:
    //
    // A new TBS context could not be created because there are too many open
    // contexts.
    //
    int TBSIMP_E_TOO_MANY_TBS_CONTEXTS = 0x80290217;

    //
    // MessageId: TBSIMP_E_TOO_MANY_RESOURCES
    //
    // MessageText:
    //
    // A new virtual resource could not be created because there are too many
    // open virtual resources.
    //
    int TBSIMP_E_TOO_MANY_RESOURCES = 0x80290218;

    //
    // MessageId: TBSIMP_E_PPI_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The physical presence interface is not supported.
    //
    int TBSIMP_E_PPI_NOT_SUPPORTED = 0x80290219;

    //
    // MessageId: TBSIMP_E_TPM_INCOMPATIBLE
    //
    // MessageText:
    //
    // TBS is not compatible with the version of TPM found on the system.
    //
    int TBSIMP_E_TPM_INCOMPATIBLE = 0x8029021A;

    //
    // TPM Physical Presence implementation error codes {0x0300..0x03ff}
    //
    //
    // MessageId: TPM_E_PPI_ACPI_FAILURE
    //
    // MessageText:
    //
    // A general error was detected when attempting to acquire the BIOS's
    // response to a Physical Presence command.
    //
    int TPM_E_PPI_ACPI_FAILURE = 0x80290300;

    //
    // MessageId: TPM_E_PPI_USER_ABORT
    //
    // MessageText:
    //
    // The user failed to confirm the TPM operation request.
    //
    int TPM_E_PPI_USER_ABORT = 0x80290301;

    //
    // MessageId: TPM_E_PPI_BIOS_FAILURE
    //
    // MessageText:
    //
    // The BIOS failure prevented the successful execution of the requested TPM
    // operation (e.g. invalid TPM operation request, BIOS communication error
    // with the TPM).
    //
    int TPM_E_PPI_BIOS_FAILURE = 0x80290302;

    //
    // MessageId: TPM_E_PPI_NOT_SUPPORTED
    //
    // MessageText:
    //
    // The BIOS does not support the physical presence interface.
    //
    int TPM_E_PPI_NOT_SUPPORTED = 0x80290303;

    //
    // ==
    // Facility Performance Logs & Alerts (PLA) Error Messages
    // ==
    //
    //
    // MessageId: PLA_E_DCS_NOT_FOUND
    //
    // MessageText:
    //
    // Data Collector Set was not found.
    //
    int PLA_E_DCS_NOT_FOUND = 0x80300002;

    //
    // MessageId: PLA_E_DCS_IN_USE
    //
    // MessageText:
    //
    // The Data Collector Set or one of its dependencies is already in use.
    //
    int PLA_E_DCS_IN_USE = 0x803000AA;

    //
    // MessageId: PLA_E_TOO_MANY_FOLDERS
    //
    // MessageText:
    //
    // Unable to start Data Collector Set because there are too many folders.
    //
    int PLA_E_TOO_MANY_FOLDERS = 0x80300045;

    //
    // MessageId: PLA_E_NO_MIN_DISK
    //
    // MessageText:
    //
    // Not enough free disk space to start Data Collector Set.
    //
    int PLA_E_NO_MIN_DISK = 0x80300070;

    //
    // MessageId: PLA_E_DCS_ALREADY_EXISTS
    //
    // MessageText:
    //
    // Data Collector Set already exists.
    //
    int PLA_E_DCS_ALREADY_EXISTS = 0x803000B7;

    //
    // MessageId: PLA_S_PROPERTY_IGNORED
    //
    // MessageText:
    //
    // Property value will be ignored.
    //
    int PLA_S_PROPERTY_IGNORED = 0x00300100;

    //
    // MessageId: PLA_E_PROPERTY_CONFLICT
    //
    // MessageText:
    //
    // Property value conFLIct.
    //
    int PLA_E_PROPERTY_CONFLICT = 0x80300101;

    //
    // MessageId: PLA_E_DCS_SINGLETON_REQUIRED
    //
    // MessageText:
    //
    // The current configuration for this Data Collector Set requires that it
    // contain exactly one Data Collector.
    //
    int PLA_E_DCS_SINGLETON_REQUIRED = 0x80300102;

    //
    // MessageId: PLA_E_CREDENTIALS_REQUIRED
    //
    // MessageText:
    //
    // A user account is required in order to commit the current Data Collector
    // Set properties.
    //
    int PLA_E_CREDENTIALS_REQUIRED = 0x80300103;

    //
    // MessageId: PLA_E_DCS_NOT_RUNNING
    //
    // MessageText:
    //
    // Data Collector Set is not running.
    //
    int PLA_E_DCS_NOT_RUNNING = 0x80300104;

    //
    // MessageId: PLA_E_CONFLICT_INCL_EXCL_API
    //
    // MessageText:
    //
    // A conFLIct was detected in the list of include/exclude APIs. Do not
    // specify the same API in both the include list and the exclude list.
    //
    int PLA_E_CONFLICT_INCL_EXCL_API = 0x80300105;

    //
    // MessageId: PLA_E_NETWORK_EXE_NOT_VALID
    //
    // MessageText:
    //
    // The executable path you have specified refers to a network share or UNC
    // path.
    //
    int PLA_E_NETWORK_EXE_NOT_VALID = 0x80300106;

    //
    // MessageId: PLA_E_EXE_ALREADY_CONFIGURED
    //
    // MessageText:
    //
    // The executable path you have specified is already configured for API
    // tracing.
    //
    int PLA_E_EXE_ALREADY_CONFIGURED = 0x80300107;

    //
    // MessageId: PLA_E_EXE_PATH_NOT_VALID
    //
    // MessageText:
    //
    // The executable path you have specified does not exist. Verify that the
    // specified path is correct.
    //
    int PLA_E_EXE_PATH_NOT_VALID = 0x80300108;

    //
    // MessageId: PLA_E_DC_ALREADY_EXISTS
    //
    // MessageText:
    //
    // Data Collector already exists.
    //
    int PLA_E_DC_ALREADY_EXISTS = 0x80300109;

    //
    // MessageId: PLA_E_DCS_START_WAIT_TIMEOUT
    //
    // MessageText:
    //
    // The wait for the Data Collector Set start notification has timed out.
    //
    int PLA_E_DCS_START_WAIT_TIMEOUT = 0x8030010A;

    //
    // MessageId: PLA_E_DC_START_WAIT_TIMEOUT
    //
    // MessageText:
    //
    // The wait for the Data Collector to start has timed out.
    //
    int PLA_E_DC_START_WAIT_TIMEOUT = 0x8030010B;

    //
    // MessageId: PLA_E_REPORT_WAIT_TIMEOUT
    //
    // MessageText:
    //
    // The wait for the report generation tool to finish has timed out.
    //
    int PLA_E_REPORT_WAIT_TIMEOUT = 0x8030010C;

    //
    // MessageId: PLA_E_NO_DUPLICATES
    //
    // MessageText:
    //
    // Duplicate items are not allowed.
    //
    int PLA_E_NO_DUPLICATES = 0x8030010D;

    //
    // MessageId: PLA_E_EXE_FULL_PATH_REQUIRED
    //
    // MessageText:
    //
    // When specifying the executable that you want to trace, you must specify a
    // full path to the executable and not just a filename.
    //
    int PLA_E_EXE_FULL_PATH_REQUIRED = 0x8030010E;

    //
    // MessageId: PLA_E_INVALID_SESSION_NAME
    //
    // MessageText:
    //
    // The session name provided is invalid.
    //
    int PLA_E_INVALID_SESSION_NAME = 0x8030010F;

    //
    // MessageId: PLA_E_PLA_CHANNEL_NOT_ENABLED
    //
    // MessageText:
    //
    // The Event Log channel Microsoft-Windows-Diagnosis-PLA/Operational must be
    // enabled to perform this operation.
    //
    int PLA_E_PLA_CHANNEL_NOT_ENABLED = 0x80300110;

    //
    // MessageId: PLA_E_TASKSCHED_CHANNEL_NOT_ENABLED
    //
    // MessageText:
    //
    // The Event Log channel Microsoft-Windows-TaskScheduler must be enabled to
    // perform this operation.
    //
    int PLA_E_TASKSCHED_CHANNEL_NOT_ENABLED = 0x80300111;

    //
    // MessageId: PLA_E_RULES_MANAGER_FAILED
    //
    // MessageText:
    //
    // The execution of the Rules Manager failed.
    //
    int PLA_E_RULES_MANAGER_FAILED = 0x80300112;

    //
    // MessageId: PLA_E_CABAPI_FAILURE
    //
    // MessageText:
    //
    // An error occured while attemption to compress or extract the data.
    //
    int PLA_E_CABAPI_FAILURE = 0x80300113;

    //
    // ==
    // Full Volume Encryption Error Messages
    // ==
    //
    //
    // MessageId: FVE_E_LOCKED_VOLUME
    //
    // MessageText:
    //
    // This volume is locked by BitLocker Drive Encryption. Return to the
    // control panel to unlock volume.
    //
    int FVE_E_LOCKED_VOLUME = 0x80310000;

    //
    // MessageId: FVE_E_NOT_ENCRYPTED
    //
    // MessageText:
    //
    // The volume is not encrypted, no key is available.
    //
    int FVE_E_NOT_ENCRYPTED = 0x80310001;

    //
    // MessageId: FVE_E_NO_TPM_BIOS
    //
    // MessageText:
    //
    // The BIOS did not correctly communicate with the TPM. Contact the computer
    // manufacturer for BIOS upgrade instructions.
    //
    int FVE_E_NO_TPM_BIOS = 0x80310002;

    //
    // MessageId: FVE_E_NO_MBR_METRIC
    //
    // MessageText:
    //
    // The BIOS did not correctly communicate with the Master Boot Record (MBR).
    // Contact the computer manufacturer for BIOS upgrade instructions.
    //
    int FVE_E_NO_MBR_METRIC = 0x80310003;

    //
    // MessageId: FVE_E_NO_BOOTSECTOR_METRIC
    //
    // MessageText:
    //
    // If there is a bootable CD or DVD in your computer, remove it, restart the
    // computer and turn on BitLocker again. If the problem persists contact the
    // computer manufacturer for BIOS upgrade instructions.
    //
    int FVE_E_NO_BOOTSECTOR_METRIC = 0x80310004;

    //
    // MessageId: FVE_E_NO_BOOTMGR_METRIC
    //
    // MessageText:
    //
    // You have an incompatible boot sector. Update the boot manager (BOOTMGR).
    //
    int FVE_E_NO_BOOTMGR_METRIC = 0x80310005;

    //
    // MessageId: FVE_E_WRONG_BOOTMGR
    //
    // MessageText:
    //
    // You have an incompatible boot manager. Update the boot manager (BOOTMGR).
    //
    int FVE_E_WRONG_BOOTMGR = 0x80310006;

    //
    // MessageId: FVE_E_SECURE_KEY_REQUIRED
    //
    // MessageText:
    //
    // No secure key protector has been defined.
    //
    int FVE_E_SECURE_KEY_REQUIRED = 0x80310007;

    //
    // MessageId: FVE_E_NOT_ACTIVATED
    //
    // MessageText:
    //
    // BitLocker Drive Encryption is not enabled on this volume. Turn on
    // BitLocker.
    //
    int FVE_E_NOT_ACTIVATED = 0x80310008;

    //
    // MessageId: FVE_E_ACTION_NOT_ALLOWED
    //
    // MessageText:
    //
    // BitLocker Drive Encryption could not perform requested action. This
    // condition may occur when two requests are issued at the same time.
    //
    int FVE_E_ACTION_NOT_ALLOWED = 0x80310009;

    //
    // MessageId: FVE_E_AD_SCHEMA_NOT_INSTALLED
    //
    // MessageText:
    //
    // The Active Directory Domain Services forest does not contain the required
    // attributes and classes to host BitLocker Drive Encryption or Trusted
    // Platform Module information.
    //
    int FVE_E_AD_SCHEMA_NOT_INSTALLED = 0x8031000A;

    //
    // MessageId: FVE_E_AD_INVALID_DATATYPE
    //
    // MessageText:
    //
    // The type of the data obtained from Active Directory was not expected.
    //
    int FVE_E_AD_INVALID_DATATYPE = 0x8031000B;

    //
    // MessageId: FVE_E_AD_INVALID_DATASIZE
    //
    // MessageText:
    //
    // The size of the data obtained from Active Directory was not expected.
    //
    int FVE_E_AD_INVALID_DATASIZE = 0x8031000C;

    //
    // MessageId: FVE_E_AD_NO_VALUES
    //
    // MessageText:
    //
    // The attribute read from Active Directory has no (zero) values.
    //
    int FVE_E_AD_NO_VALUES = 0x8031000D;

    //
    // MessageId: FVE_E_AD_ATTR_NOT_SET
    //
    // MessageText:
    //
    // The attribute was not set.
    //
    int FVE_E_AD_ATTR_NOT_SET = 0x8031000E;

    //
    // MessageId: FVE_E_AD_GUID_NOT_FOUND
    //
    // MessageText:
    //
    // The specified GUID could not be found.
    //
    int FVE_E_AD_GUID_NOT_FOUND = 0x8031000F;

    //
    // MessageId: FVE_E_BAD_INFORMATION
    //
    // MessageText:
    //
    // The control block for the encrypted volume is not valid.
    //
    int FVE_E_BAD_INFORMATION = 0x80310010;

    //
    // MessageId: FVE_E_TOO_SMALL
    //
    // MessageText:
    //
    // The volume cannot be encrypted because it does not have enough free
    // space.
    //
    int FVE_E_TOO_SMALL = 0x80310011;

    //
    // MessageId: FVE_E_SYSTEM_VOLUME
    //
    // MessageText:
    //
    // The volume cannot be encrypted because it contains system boot
    // information.
    //
    int FVE_E_SYSTEM_VOLUME = 0x80310012;

    //
    // MessageId: FVE_E_FAILED_WRONG_FS
    //
    // MessageText:
    //
    // The volume cannot be encrypted because the file system is not supported.
    //
    int FVE_E_FAILED_WRONG_FS = 0x80310013;

    //
    // MessageId: FVE_E_FAILED_BAD_FS
    //
    // MessageText:
    //
    // The file system is corrupt. Run CHKDSK.
    //
    int FVE_E_FAILED_BAD_FS = 0x80310014;

    //
    // MessageId: FVE_E_NOT_SUPPORTED
    //
    // MessageText:
    //
    // This volume cannot be encrypted.
    //
    int FVE_E_NOT_SUPPORTED = 0x80310015;

    //
    // MessageId: FVE_E_BAD_DATA
    //
    // MessageText:
    //
    // Data supplied is malformed.
    //
    int FVE_E_BAD_DATA = 0x80310016;

    //
    // MessageId: FVE_E_VOLUME_NOT_BOUND
    //
    // MessageText:
    //
    // Volume is not bound to the system.
    //
    int FVE_E_VOLUME_NOT_BOUND = 0x80310017;

    //
    // MessageId: FVE_E_TPM_NOT_OWNED
    //
    // MessageText:
    //
    // You must take ownership of the Trusted Platform Module (TPM).
    //
    int FVE_E_TPM_NOT_OWNED = 0x80310018;

    //
    // MessageId: FVE_E_NOT_DATA_VOLUME
    //
    // MessageText:
    //
    // The volume specified is not a data volume.
    //
    int FVE_E_NOT_DATA_VOLUME = 0x80310019;

    //
    // MessageId: FVE_E_AD_INSUFFICIENT_BUFFER
    //
    // MessageText:
    //
    // The buffer supplied to a function was insufficient to contain the
    // returned data.
    //
    int FVE_E_AD_INSUFFICIENT_BUFFER = 0x8031001A;

    //
    // MessageId: FVE_E_CONV_READ
    //
    // MessageText:
    //
    // A read operation failed while converting the volume.
    //
    int FVE_E_CONV_READ = 0x8031001B;

    //
    // MessageId: FVE_E_CONV_WRITE
    //
    // MessageText:
    //
    // A write operation failed while converting the volume.
    //
    int FVE_E_CONV_WRITE = 0x8031001C;

    //
    // MessageId: FVE_E_KEY_REQUIRED
    //
    // MessageText:
    //
    // One or more key protectors are required for this volume.
    //
    int FVE_E_KEY_REQUIRED = 0x8031001D;

    //
    // MessageId: FVE_E_CLUSTERING_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Cluster configurations are not supported.
    //
    int FVE_E_CLUSTERING_NOT_SUPPORTED = 0x8031001E;

    //
    // MessageId: FVE_E_VOLUME_BOUND_ALREADY
    //
    // MessageText:
    //
    // The volume is already bound to the system.
    //
    int FVE_E_VOLUME_BOUND_ALREADY = 0x8031001F;

    //
    // MessageId: FVE_E_OS_NOT_PROTECTED
    //
    // MessageText:
    //
    // The operating system volume is not protected by BitLocker Drive
    // Encryption.
    //
    int FVE_E_OS_NOT_PROTECTED = 0x80310020;

    //
    // MessageId: FVE_E_PROTECTION_DISABLED
    //
    // MessageText:
    //
    // All protectors are effectively disabled (clear key exists).
    //
    int FVE_E_PROTECTION_DISABLED = 0x80310021;

    //
    // MessageId: FVE_E_RECOVERY_KEY_REQUIRED
    //
    // MessageText:
    //
    // A recovery key protector is required.
    //
    int FVE_E_RECOVERY_KEY_REQUIRED = 0x80310022;

    //
    // MessageId: FVE_E_FOREIGN_VOLUME
    //
    // MessageText:
    //
    // This volume cannot be bound to a TPM.
    //
    int FVE_E_FOREIGN_VOLUME = 0x80310023;

    //
    // MessageId: FVE_E_OVERLAPPED_UPDATE
    //
    // MessageText:
    //
    // The control block for the encrypted volume was updated by another thread.
    // Try again.
    //
    int FVE_E_OVERLAPPED_UPDATE = 0x80310024;

    //
    // MessageId: FVE_E_TPM_SRK_AUTH_NOT_ZERO
    //
    // MessageText:
    //
    // The authorization data for the Storage Root Key (SRK) of the Trusted
    // Platform Module (TPM) is not zero and is therefore incompatible with
    // BitLocker.
    //
    int FVE_E_TPM_SRK_AUTH_NOT_ZERO = 0x80310025;

    //
    // MessageId: FVE_E_FAILED_SECTOR_SIZE
    //
    // MessageText:
    //
    // The volume encryption algorithm cannot be used on this sector size.
    //
    int FVE_E_FAILED_SECTOR_SIZE = 0x80310026;

    //
    // MessageId: FVE_E_FAILED_AUTHENTICATION
    //
    // MessageText:
    //
    // The recovery information provided does not match required recovery
    // information to unlock the volume. Confirm the information and try again.
    //
    int FVE_E_FAILED_AUTHENTICATION = 0x80310027;

    //
    // MessageId: FVE_E_NOT_OS_VOLUME
    //
    // MessageText:
    //
    // The volume specified is not the operating system volume.
    //
    int FVE_E_NOT_OS_VOLUME = 0x80310028;

    //
    // MessageId: FVE_E_AUTOUNLOCK_ENABLED
    //
    // MessageText:
    //
    // BitLocker Drive Encryption cannot be turned off. Please turn off the auto
    // unlock on all other volumes before turning off BitLocker on the current
    // volume.
    //
    int FVE_E_AUTOUNLOCK_ENABLED = 0x80310029;

    //
    // MessageId: FVE_E_WRONG_BOOTSECTOR
    //
    // MessageText:
    //
    // The system partition boot sector does not perform TPM measurements.
    //
    int FVE_E_WRONG_BOOTSECTOR = 0x8031002A;

    //
    // MessageId: FVE_E_WRONG_SYSTEM_FS
    //
    // MessageText:
    //
    // BitLocker requires the file system to be NTFS. Convert the volume to
    // NTFS, then enable BitLocker.
    //
    int FVE_E_WRONG_SYSTEM_FS = 0x8031002B;

    //
    // MessageId: FVE_E_POLICY_PASSWORD_REQUIRED
    //
    // MessageText:
    //
    // Group policy requires a recovery password before encryption may begin.
    //
    int FVE_E_POLICY_PASSWORD_REQUIRED = 0x8031002C;

    //
    // MessageId: FVE_E_CANNOT_SET_FVEK_ENCRYPTED
    //
    // MessageText:
    //
    // The volume encryption algorithm and key cannot be set on an encrypted
    // volume.
    //
    int FVE_E_CANNOT_SET_FVEK_ENCRYPTED = 0x8031002D;

    //
    // MessageId: FVE_E_CANNOT_ENCRYPT_NO_KEY
    //
    // MessageText:
    //
    // A key must be specified before encryption may begin.
    //
    int FVE_E_CANNOT_ENCRYPT_NO_KEY = 0x8031002E;

    //
    // MessageId: FVE_E_BOOTABLE_CDDVD
    //
    // MessageText:
    //
    // BitLocker Drive Encryption detected bootable media (CD, DVD, or USB) in
    // the computer. Remove the media and restart the computer.
    //
    int FVE_E_BOOTABLE_CDDVD = 0x80310030;

    //
    // MessageId: FVE_E_PROTECTOR_EXISTS
    //
    // MessageText:
    //
    // An instance of this key protector already exists on the volume.
    //
    int FVE_E_PROTECTOR_EXISTS = 0x80310031;

    //
    // MessageId: FVE_E_RELATIVE_PATH
    //
    // MessageText:
    //
    // The file cannot be saved to a relative path.
    //
    int FVE_E_RELATIVE_PATH = 0x80310032;

    //
    // MessageId: FVE_E_PROTECTOR_NOT_FOUND
    //
    // MessageText:
    //
    // The specified key protector was not found on the volume. Try another key
    // protector.
    //
    int FVE_E_PROTECTOR_NOT_FOUND = 0x80310033;

    //
    // MessageId: FVE_E_INVALID_KEY_FORMAT
    //
    // MessageText:
    //
    // The recovery password file on the USB device is corrupt. Try another USB
    // device.
    //
    int FVE_E_INVALID_KEY_FORMAT = 0x80310034;

    //
    // MessageId: FVE_E_INVALID_PASSWORD_FORMAT
    //
    // MessageText:
    //
    // The format of the recovery password file is invalid. Select another
    // recovery password and try again.
    //
    int FVE_E_INVALID_PASSWORD_FORMAT = 0x80310035;

    //
    // MessageId: FVE_E_FIPS_RNG_CHECK_FAILED
    //
    // MessageText:
    //
    // The random number generator check test failed.
    //
    int FVE_E_FIPS_RNG_CHECK_FAILED = 0x80310036;

    //
    // MessageId: FVE_E_FIPS_PREVENTS_RECOVERY_PASSWORD
    //
    // MessageText:
    //
    // The group policy setting requiring FIPS compliance prevented the recovery
    // password from being generated or used. Please contact your domain
    // administrator for more information.
    //
    int FVE_E_FIPS_PREVENTS_RECOVERY_PASSWORD = 0x80310037;

    //
    // MessageId: FVE_E_FIPS_PREVENTS_EXTERNAL_KEY_EXPORT
    //
    // MessageText:
    //
    // The group policy setting requiring FIPS compliance prevented the recovery
    // password from being saved. Please contact your domain administrator for
    // more information.
    //
    int FVE_E_FIPS_PREVENTS_EXTERNAL_KEY_EXPORT = 0x80310038;

    //
    // MessageId: FVE_E_NOT_DECRYPTED
    //
    // MessageText:
    //
    // The volume must be fully decrypted to complete this operation.
    //
    int FVE_E_NOT_DECRYPTED = 0x80310039;

    //
    // MessageId: FVE_E_INVALID_PROTECTOR_TYPE
    //
    // MessageText:
    //
    // The specified key protector is not of the correct type.
    //
    int FVE_E_INVALID_PROTECTOR_TYPE = 0x8031003A;

    //
    // MessageId: FVE_E_NO_PROTECTORS_TO_TEST
    //
    // MessageText:
    //
    // No TPM protectors exist on the volume to perform the hardware test.
    //
    int FVE_E_NO_PROTECTORS_TO_TEST = 0x8031003B;

    //
    // MessageId: FVE_E_KEYFILE_NOT_FOUND
    //
    // MessageText:
    //
    // The BitLocker startup key or recovery password could not be read from the
    // USB device. Ensure the USB device is plugged into the computer, then turn
    // on BitLocker and try again. If the problem persists contact the computer
    // manufacturer for BIOS upgrade instructions.
    //
    int FVE_E_KEYFILE_NOT_FOUND = 0x8031003C;

    //
    // MessageId: FVE_E_KEYFILE_INVALID
    //
    // MessageText:
    //
    // The BitLocker startup key or recovery password file is corrupt or
    // invalid.
    //
    int FVE_E_KEYFILE_INVALID = 0x8031003D;

    //
    // MessageId: FVE_E_KEYFILE_NO_VMK
    //
    // MessageText:
    //
    // The BitLocker encryption key could not be obtained from the startup key
    // or recovery password.
    //
    int FVE_E_KEYFILE_NO_VMK = 0x8031003E;

    //
    // MessageId: FVE_E_TPM_DISABLED
    //
    // MessageText:
    //
    // The Trusted Platform Module (TPM) is disabled.
    //
    int FVE_E_TPM_DISABLED = 0x8031003F;

    //
    // MessageId: FVE_E_NOT_ALLOWED_IN_SAFE_MODE
    //
    // MessageText:
    //
    // BitLocker Drive Encryption can only be used for recovery purposes in
    // Safe-Mode.
    //
    int FVE_E_NOT_ALLOWED_IN_SAFE_MODE = 0x80310040;

    //
    // MessageId: FVE_E_TPM_INVALID_PCR
    //
    // MessageText:
    //
    // The Trusted Platform Module (TPM) was not able to unlock the volume
    // because the system boot information changed.
    //
    int FVE_E_TPM_INVALID_PCR = 0x80310041;

    //
    // MessageId: FVE_E_TPM_NO_VMK
    //
    // MessageText:
    //
    // The BitLocker encryption key could not be obtained from the Trusted
    // Platform Module (TPM).
    //
    int FVE_E_TPM_NO_VMK = 0x80310042;

    //
    // MessageId: FVE_E_PIN_INVALID
    //
    // MessageText:
    //
    // The BitLocker encryption key could not be obtained from the Trusted
    // Platform Module (TPM) and PIN.
    //
    int FVE_E_PIN_INVALID = 0x80310043;

    //
    // MessageId: FVE_E_AUTH_INVALID_APPLICATION
    //
    // MessageText:
    //
    // A boot application has changed since BitLocker was enabled.
    //
    int FVE_E_AUTH_INVALID_APPLICATION = 0x80310044;

    //
    // MessageId: FVE_E_AUTH_INVALID_CONFIG
    //
    // MessageText:
    //
    // The Boot Configuration Data (BCD) settings have changed since BitLocker
    // was enabled.
    //
    int FVE_E_AUTH_INVALID_CONFIG = 0x80310045;

    //
    // MessageId: FVE_E_FIPS_DISABLE_PROTECTION_NOT_ALLOWED
    //
    // MessageText:
    //
    // The group policy setting requiring FIPS compliance prevented BitLocker
    // from being disabled. Please contact your domain administrator for more
    // information.
    //
    int FVE_E_FIPS_DISABLE_PROTECTION_NOT_ALLOWED = 0x80310046;

    //
    // MessageId: FVE_E_FS_NOT_EXTENDED
    //
    // MessageText:
    //
    // The file system does not extend to the end of the volume.
    //
    int FVE_E_FS_NOT_EXTENDED = 0x80310047;

    //
    // MessageId: FVE_E_FIRMWARE_TYPE_NOT_SUPPORTED
    //
    // MessageText:
    //
    // BitLocker Drive Encryption cannot be enabled on this computer. Contact
    // the computer manufacturer for BIOS upgrade instructions.
    //
    int FVE_E_FIRMWARE_TYPE_NOT_SUPPORTED = 0x80310048;

    //
    // MessageId: FVE_E_NO_LICENSE
    //
    // MessageText:
    //
    // This Windows license does not support BitLocker Drive Encryption. To use
    // BitLocker Drive Encryption, please upgrade your Windows license.
    //
    int FVE_E_NO_LICENSE = 0x80310049;

    //
    // MessageId: FVE_E_NOT_ON_STACK
    //
    // MessageText:
    //
    // Critical BitLocker Drive Encryption system files are not available. Use
    // Windows Startup Repair to restore files.
    //
    int FVE_E_NOT_ON_STACK = 0x8031004A;

    //
    // MessageId: FVE_E_FS_MOUNTED
    //
    // MessageText:
    //
    // This operation cannot be performed while the volume is in use.
    //
    int FVE_E_FS_MOUNTED = 0x8031004B;

    //
    // MessageId: FVE_E_TOKEN_NOT_IMPERSONATED
    //
    // MessageText:
    //
    // The access token associated with the current thread is not an
    // impersonated token.
    //
    int FVE_E_TOKEN_NOT_IMPERSONATED = 0x8031004C;

    //
    // MessageId: FVE_E_DRY_RUN_FAILED
    //
    // MessageText:
    //
    // The BitLocker encryption key could not be obtained.
    //
    int FVE_E_DRY_RUN_FAILED = 0x8031004D;

    //
    // MessageId: FVE_E_REBOOT_REQUIRED
    //
    // MessageText:
    //
    // No action was taken as a system reboot is required.
    //
    int FVE_E_REBOOT_REQUIRED = 0x8031004E;

    //
    // MessageId: FVE_E_DEBUGGER_ENABLED
    //
    // MessageText:
    //
    // Boot debugging is enabled. Run bcdedit to turn it off.
    //
    int FVE_E_DEBUGGER_ENABLED = 0x8031004F;

    //
    // MessageId: FVE_E_RAW_ACCESS
    //
    // MessageText:
    //
    // No action was taken as BitLocker Drive Encryption is in raw access mode.
    //
    int FVE_E_RAW_ACCESS = 0x80310050;

    //
    // MessageId: FVE_E_RAW_BLOCKED
    //
    // MessageText:
    //
    // BitLocker Drive Encryption cannot enter raw access mode for this volume.
    //
    int FVE_E_RAW_BLOCKED = 0x80310051;

    //
    // MessageId: FVE_E_BCD_APPLICATIONS_PATH_INCORRECT
    //
    // MessageText:
    //
    // The path specified in the Boot Configuration Data (BCD) for a BitLocker
    // Drive Encryption integrity-protected application is incorrect. Please
    // verify and correct your BCD settings and try again.
    //
    int FVE_E_BCD_APPLICATIONS_PATH_INCORRECT = 0x80310052;

    //
    // MessageId: FVE_E_NOT_ALLOWED_IN_VERSION
    //
    // MessageText:
    //
    // BitLocker Drive Encryption can only be used for recovery purposes in this
    // version of Windows.
    //
    int FVE_E_NOT_ALLOWED_IN_VERSION = 0x80310053;

    //
    // ==
    // Windows Filtering Platform Error Messages
    // ==
    //
    //
    // MessageId: FWP_E_CALLOUT_NOT_FOUND
    //
    // MessageText:
    //
    // The callout does not exist.
    //
    int FWP_E_CALLOUT_NOT_FOUND = 0x80320001;

    //
    // MessageId: FWP_E_CONDITION_NOT_FOUND
    //
    // MessageText:
    //
    // The filter condition does not exist.
    //
    int FWP_E_CONDITION_NOT_FOUND = 0x80320002;

    //
    // MessageId: FWP_E_FILTER_NOT_FOUND
    //
    // MessageText:
    //
    // The filter does not exist.
    //
    int FWP_E_FILTER_NOT_FOUND = 0x80320003;

    //
    // MessageId: FWP_E_LAYER_NOT_FOUND
    //
    // MessageText:
    //
    // The layer does not exist.
    //
    int FWP_E_LAYER_NOT_FOUND = 0x80320004;

    //
    // MessageId: FWP_E_PROVIDER_NOT_FOUND
    //
    // MessageText:
    //
    // The provider does not exist.
    //
    int FWP_E_PROVIDER_NOT_FOUND = 0x80320005;

    //
    // MessageId: FWP_E_PROVIDER_CONTEXT_NOT_FOUND
    //
    // MessageText:
    //
    // The provider context does not exist.
    //
    int FWP_E_PROVIDER_CONTEXT_NOT_FOUND = 0x80320006;

    //
    // MessageId: FWP_E_SUBLAYER_NOT_FOUND
    //
    // MessageText:
    //
    // The sublayer does not exist.
    //
    int FWP_E_SUBLAYER_NOT_FOUND = 0x80320007;

    //
    // MessageId: FWP_E_NOT_FOUND
    //
    // MessageText:
    //
    // The object does not exist.
    //
    int FWP_E_NOT_FOUND = 0x80320008;

    //
    // MessageId: FWP_E_ALREADY_EXISTS
    //
    // MessageText:
    //
    // An object with that GUID or LUID already exists.
    //
    int FWP_E_ALREADY_EXISTS = 0x80320009;

    //
    // MessageId: FWP_E_IN_USE
    //
    // MessageText:
    //
    // The object is referenced by other objects so cannot be deleted.
    //
    int FWP_E_IN_USE = 0x8032000A;

    //
    // MessageId: FWP_E_DYNAMIC_SESSION_IN_PROGRESS
    //
    // MessageText:
    //
    // The call is not allowed from within a dynamic session.
    //
    int FWP_E_DYNAMIC_SESSION_IN_PROGRESS = 0x8032000B;

    //
    // MessageId: FWP_E_WRONG_SESSION
    //
    // MessageText:
    //
    // The call was made from the wrong session so cannot be completed.
    //
    int FWP_E_WRONG_SESSION = 0x8032000C;

    //
    // MessageId: FWP_E_NO_TXN_IN_PROGRESS
    //
    // MessageText:
    //
    // The call must be made from within an explicit transaction.
    //
    int FWP_E_NO_TXN_IN_PROGRESS = 0x8032000D;

    //
    // MessageId: FWP_E_TXN_IN_PROGRESS
    //
    // MessageText:
    //
    // The call is not allowed from within an explicit transaction.
    //
    int FWP_E_TXN_IN_PROGRESS = 0x8032000E;

    //
    // MessageId: FWP_E_TXN_ABORTED
    //
    // MessageText:
    //
    // The explicit transaction has been forcibly cancelled.
    //
    int FWP_E_TXN_ABORTED = 0x8032000F;

    //
    // MessageId: FWP_E_SESSION_ABORTED
    //
    // MessageText:
    //
    // The session has been cancelled.
    //
    int FWP_E_SESSION_ABORTED = 0x80320010;

    //
    // MessageId: FWP_E_INCOMPATIBLE_TXN
    //
    // MessageText:
    //
    // The call is not allowed from within a read-only transaction.
    //
    int FWP_E_INCOMPATIBLE_TXN = 0x80320011;

    //
    // MessageId: FWP_E_TIMEOUT
    //
    // MessageText:
    //
    // The call timed out while waiting to acquire the transaction lock.
    //
    int FWP_E_TIMEOUT = 0x80320012;

    //
    // MessageId: FWP_E_NET_EVENTS_DISABLED
    //
    // MessageText:
    //
    // Collection of network diagnostic events is disabled.
    //
    int FWP_E_NET_EVENTS_DISABLED = 0x80320013;

    //
    // MessageId: FWP_E_INCOMPATIBLE_LAYER
    //
    // MessageText:
    //
    // The operation is not supported by the specified layer.
    //
    int FWP_E_INCOMPATIBLE_LAYER = 0x80320014;

    //
    // MessageId: FWP_E_KM_CLIENTS_ONLY
    //
    // MessageText:
    //
    // The call is allowed for kernel-mode callers only.
    //
    int FWP_E_KM_CLIENTS_ONLY = 0x80320015;

    //
    // MessageId: FWP_E_LIFETIME_MISMATCH
    //
    // MessageText:
    //
    // The call tried to associate two objects with incompatible lifetimes.
    //
    int FWP_E_LIFETIME_MISMATCH = 0x80320016;

    //
    // MessageId: FWP_E_BUILTIN_OBJECT
    //
    // MessageText:
    //
    // The object is built in so cannot be deleted.
    //
    int FWP_E_BUILTIN_OBJECT = 0x80320017;

    //
    // MessageId: FWP_E_TOO_MANY_CALLOUTS
    //
    // MessageText:
    //
    // The maximum number of callouts has been reached.
    //
    int FWP_E_TOO_MANY_CALLOUTS = 0x80320018;

    //
    // MessageId: FWP_E_NOTIFICATION_DROPPED
    //
    // MessageText:
    //
    // A notification could not be delivered because a message queue is at its
    // maximum capacity.
    //
    int FWP_E_NOTIFICATION_DROPPED = 0x80320019;

    //
    // MessageId: FWP_E_TRAFFIC_MISMATCH
    //
    // MessageText:
    //
    // The traffic parameters do not match those for the security association
    // context.
    //
    int FWP_E_TRAFFIC_MISMATCH = 0x8032001A;

    //
    // MessageId: FWP_E_INCOMPATIBLE_SA_STATE
    //
    // MessageText:
    //
    // The call is not allowed for the current security association state.
    //
    int FWP_E_INCOMPATIBLE_SA_STATE = 0x8032001B;

    //
    // MessageId: FWP_E_NULL_POINTER
    //
    // MessageText:
    //
    // A required pointer is null.
    //
    int FWP_E_NULL_POINTER = 0x8032001C;

    //
    // MessageId: FWP_E_INVALID_ENUMERATOR
    //
    // MessageText:
    //
    // An enumerator is not valid.
    //
    int FWP_E_INVALID_ENUMERATOR = 0x8032001D;

    //
    // MessageId: FWP_E_INVALID_F;AGS
    //
    // MessageText:
    //
    // The F;ags field contains an invalid value.
    //
    int FWP_E_INVALID_FLAGS = 0x8032001E;

    //
    // MessageId: FWP_E_INVALID_NET_MASK
    //
    // MessageText:
    //
    // A network mask is not valid.
    //
    int FWP_E_INVALID_NET_MASK = 0x8032001F;

    //
    // MessageId: FWP_E_INVALID_RANGE
    //
    // MessageText:
    //
    // An FWP_RANGE is not valid.
    //
    int FWP_E_INVALID_RANGE = 0x80320020;

    //
    // MessageId: FWP_E_INVALID_INTERVAL
    //
    // MessageText:
    //
    // The time interval is not valid.
    //
    int FWP_E_INVALID_INTERVAL = 0x80320021;

    //
    // MessageId: FWP_E_ZERO_LENGTH_ARRAY
    //
    // MessageText:
    //
    // An array that must contain at least one element is zero length.
    //
    int FWP_E_ZERO_LENGTH_ARRAY = 0x80320022;

    //
    // MessageId: FWP_E_NULL_DISPLAY_NAME
    //
    // MessageText:
    //
    // The displayData.name field cannot be null.
    //
    int FWP_E_NULL_DISPLAY_NAME = 0x80320023;

    //
    // MessageId: FWP_E_INVALID_ACTION_TYPE
    //
    // MessageText:
    //
    // The action type is not one of the allowed action types for a filter.
    //
    int FWP_E_INVALID_ACTION_TYPE = 0x80320024;

    //
    // MessageId: FWP_E_INVALID_WEIGHT
    //
    // MessageText:
    //
    // The filter weight is not valid.
    //
    int FWP_E_INVALID_WEIGHT = 0x80320025;

    //
    // MessageId: FWP_E_MATCH_TYPE_MISMATCH
    //
    // MessageText:
    //
    // A filter condition contains a match type that is not compatible with the
    // operands.
    //
    int FWP_E_MATCH_TYPE_MISMATCH = 0x80320026;

    //
    // MessageId: FWP_E_TYPE_MISMATCH
    //
    // MessageText:
    //
    // An FWP_VALUE or FWPM_CONDITION_VALUE is of the wrong type.
    //
    int FWP_E_TYPE_MISMATCH = 0x80320027;

    //
    // MessageId: FWP_E_OUT_OF_BOUNDS
    //
    // MessageText:
    //
    // An integer value is outside the allowed range.
    //
    int FWP_E_OUT_OF_BOUNDS = 0x80320028;

    //
    // MessageId: FWP_E_RESERVED
    //
    // MessageText:
    //
    // A reserved field is non-zero.
    //
    int FWP_E_RESERVED = 0x80320029;

    //
    // MessageId: FWP_E_DUPLICATE_CONDITION
    //
    // MessageText:
    //
    // A filter cannot contain multiple conditions operating on a single field.
    //
    int FWP_E_DUPLICATE_CONDITION = 0x8032002A;

    //
    // MessageId: FWP_E_DUPLICATE_KEYMOD
    //
    // MessageText:
    //
    // A policy cannot contain the same keying module more than once.
    //
    int FWP_E_DUPLICATE_KEYMOD = 0x8032002B;

    //
    // MessageId: FWP_E_ACTION_INCOMPATIBLE_WITH_LAYER
    //
    // MessageText:
    //
    // The action type is not compatible with the layer.
    //
    int FWP_E_ACTION_INCOMPATIBLE_WITH_LAYER = 0x8032002C;

    //
    // MessageId: FWP_E_ACTION_INCOMPATIBLE_WITH_SUBLAYER
    //
    // MessageText:
    //
    // The action type is not compatible with the sublayer.
    //
    int FWP_E_ACTION_INCOMPATIBLE_WITH_SUBLAYER = 0x8032002D;

    //
    // MessageId: FWP_E_CONTEXT_INCOMPATIBLE_WITH_LAYER
    //
    // MessageText:
    //
    // The raw context or the provider context is not compatible with the layer.
    //
    int FWP_E_CONTEXT_INCOMPATIBLE_WITH_LAYER = 0x8032002E;

    //
    // MessageId: FWP_E_CONTEXT_INCOMPATIBLE_WITH_CALLOUT
    //
    // MessageText:
    //
    // The raw context or the provider context is not compatible with the
    // callout.
    //
    int FWP_E_CONTEXT_INCOMPATIBLE_WITH_CALLOUT = 0x8032002F;

    //
    // MessageId: FWP_E_INCOMPATIBLE_AUTH_METHOD
    //
    // MessageText:
    //
    // The authentication method is not compatible with the policy type.
    //
    int FWP_E_INCOMPATIBLE_AUTH_METHOD = 0x80320030;

    //
    // MessageId: FWP_E_INCOMPATIBLE_DH_GROUP
    //
    // MessageText:
    //
    // The Diffie-Hellman group is not compatible with the policy type.
    //
    int FWP_E_INCOMPATIBLE_DH_GROUP = 0x80320031;

    //
    // MessageId: FWP_E_EM_NOT_SUPPORTED
    //
    // MessageText:
    //
    // An IKE policy cannot contain an Extended Mode policy.
    //
    int FWP_E_EM_NOT_SUPPORTED = 0x80320032;

    //
    // MessageId: FWP_E_NEVER_MATCH
    //
    // MessageText:
    //
    // The enumeration template or subscription will never match any objects.
    //
    int FWP_E_NEVER_MATCH = 0x80320033;

    //
    // MessageId: FWP_E_PROVIDER_CONTEXT_MISMATCH
    //
    // MessageText:
    //
    // The provider context is of the wrong type.
    //
    int FWP_E_PROVIDER_CONTEXT_MISMATCH = 0x80320034;

    //
    // MessageId: FWP_E_INVALID_PARAMETER
    //
    // MessageText:
    //
    // The parameter is incorrect.
    //
    int FWP_E_INVALID_PARAMETER = 0x80320035;

    //
    // MessageId: FWP_E_TOO_MANY_SUBLAYERS
    //
    // MessageText:
    //
    // The maximum number of sublayers has been reached.
    //
    int FWP_E_TOO_MANY_SUBLAYERS = 0x80320036;

    //
    // MessageId: FWP_E_CALLOUT_NOTIFICATION_FAILED
    //
    // MessageText:
    //
    // The notification function for a callout returned an error.
    //
    int FWP_E_CALLOUT_NOTIFICATION_FAILED = 0x80320037;

    //
    // MessageId: FWP_E_INVALID_AUTH_TRANSFORM
    //
    // MessageText:
    //
    // The IPsec authentication transform is not valid.
    //
    int FWP_E_INVALID_AUTH_TRANSFORM = 0x80320038;

    //
    // MessageId: FWP_E_INVALID_CIPHER_TRANSFORM
    //
    // MessageText:
    //
    // The IPsec cipher transform is not valid.
    //
    int FWP_E_INVALID_CIPHER_TRANSFORM = 0x80320039;

    //
    // NDIS error codes (ndis.sys)
    //

    //
    // MessageId: ERROR_NDIS_INTERFACE_CLOSING
    //
    // MessageText:
    //
    // The binding to the network interface is being closed.
    //
    int ERROR_NDIS_INTERFACE_CLOSING = 0x80340002;

    //
    // MessageId: ERROR_NDIS_BAD_VERSION
    //
    // MessageText:
    //
    // An invalid version was specified.
    //
    int ERROR_NDIS_BAD_VERSION = 0x80340004;

    //
    // MessageId: ERROR_NDIS_BAD_CHARACTERISTICS
    //
    // MessageText:
    //
    // An invalid characteristics table was used.
    //
    int ERROR_NDIS_BAD_CHARACTERISTICS = 0x80340005;

    //
    // MessageId: ERROR_NDIS_ADAPTER_NOT_FOUND
    //
    // MessageText:
    //
    // Failed to find the network interface or network interface is not ready.
    //
    int ERROR_NDIS_ADAPTER_NOT_FOUND = 0x80340006;

    //
    // MessageId: ERROR_NDIS_OPEN_FAILED
    //
    // MessageText:
    //
    // Failed to open the network interface.
    //
    int ERROR_NDIS_OPEN_FAILED = 0x80340007;

    //
    // MessageId: ERROR_NDIS_DEVICE_FAILED
    //
    // MessageText:
    //
    // Network interface has encountered an internal unrecoverable failure.
    //
    int ERROR_NDIS_DEVICE_FAILED = 0x80340008;

    //
    // MessageId: ERROR_NDIS_MULTICAST_FULL
    //
    // MessageText:
    //
    // The multicast list on the network interface is full.
    //
    int ERROR_NDIS_MULTICAST_FULL = 0x80340009;

    //
    // MessageId: ERROR_NDIS_MULTICAST_EXISTS
    //
    // MessageText:
    //
    // An attempt was made to add a duplicate multicast address to the list.
    //
    int ERROR_NDIS_MULTICAST_EXISTS = 0x8034000A;

    //
    // MessageId: ERROR_NDIS_MULTICAST_NOT_FOUND
    //
    // MessageText:
    //
    // At attempt was made to remove a multicast address that was never added.
    //
    int ERROR_NDIS_MULTICAST_NOT_FOUND = 0x8034000B;

    //
    // MessageId: ERROR_NDIS_REQUEST_ABORTED
    //
    // MessageText:
    //
    // Netowork interface aborted the request.
    //
    int ERROR_NDIS_REQUEST_ABORTED = 0x8034000C;

    //
    // MessageId: ERROR_NDIS_RESET_IN_PROGRESS
    //
    // MessageText:
    //
    // Network interface can not process the request because it is being reset.
    //
    int ERROR_NDIS_RESET_IN_PROGRESS = 0x8034000D;

    //
    // MessageId: ERROR_NDIS_NOT_SUPPORTED
    //
    // MessageText:
    //
    // Netword interface does not support this request.
    //
    int ERROR_NDIS_NOT_SUPPORTED = 0x803400BB;

    //
    // MessageId: ERROR_NDIS_INVALID_PACKET
    //
    // MessageText:
    //
    // An attempt was made to send an invalid packet on a network interface.
    //
    int ERROR_NDIS_INVALID_PACKET = 0x8034000F;

    //
    // MessageId: ERROR_NDIS_ADAPTER_NOT_READY
    //
    // MessageText:
    //
    // Network interface is not ready to complete this operation.
    //
    int ERROR_NDIS_ADAPTER_NOT_READY = 0x80340011;

    //
    // MessageId: ERROR_NDIS_INVALID_LENGTH
    //
    // MessageText:
    //
    // The length of the buffer submitted for this operation is not valid.
    //
    int ERROR_NDIS_INVALID_LENGTH = 0x80340014;

    //
    // MessageId: ERROR_NDIS_INVALID_DATA
    //
    // MessageText:
    //
    // The data used for this operation is not valid.
    //
    int ERROR_NDIS_INVALID_DATA = 0x80340015;

    //
    // MessageId: ERROR_NDIS_BUFFER_TOO_SHORT
    //
    // MessageText:
    //
    // The length of buffer submitted for this operation is too small.
    //
    int ERROR_NDIS_BUFFER_TOO_SHORT = 0x80340016;

    //
    // MessageId: ERROR_NDIS_INVALID_OID
    //
    // MessageText:
    //
    // Network interface does not support this OID (Object Identifier)
    //
    int ERROR_NDIS_INVALID_OID = 0x80340017;

    //
    // MessageId: ERROR_NDIS_ADAPTER_REMOVED
    //
    // MessageText:
    //
    // The network interface has been removed.
    //
    int ERROR_NDIS_ADAPTER_REMOVED = 0x80340018;

    //
    // MessageId: ERROR_NDIS_UNSUPPORTED_MEDIA
    //
    // MessageText:
    //
    // Network interface does not support this media type.
    //
    int ERROR_NDIS_UNSUPPORTED_MEDIA = 0x80340019;

    //
    // MessageId: ERROR_NDIS_GROUP_ADDRESS_IN_USE
    //
    // MessageText:
    //
    // An attempt was made to remove a token ring group address that is in use
    // by other components.
    //
    int ERROR_NDIS_GROUP_ADDRESS_IN_USE = 0x8034001A;

    //
    // MessageId: ERROR_NDIS_FILE_NOT_FOUND
    //
    // MessageText:
    //
    // An attempt was made to map a file that can not be found.
    //
    int ERROR_NDIS_FILE_NOT_FOUND = 0x8034001B;

    //
    // MessageId: ERROR_NDIS_ERROR_READING_FILE
    //
    // MessageText:
    //
    // An error occured while NDIS tried to map the file.
    //
    int ERROR_NDIS_ERROR_READING_FILE = 0x8034001C;

    //
    // MessageId: ERROR_NDIS_ALREADY_MAPPED
    //
    // MessageText:
    //
    // An attempt was made to map a file that is alreay mapped.
    //
    int ERROR_NDIS_ALREADY_MAPPED = 0x8034001D;

    //
    // MessageId: ERROR_NDIS_RESOURCE_CONFLICT
    //
    // MessageText:
    //
    // An attempt to allocate a hardware resource failed because the resource is
    // used by another component.
    //
    int ERROR_NDIS_RESOURCE_CONFLICT = 0x8034001E;

    //
    // MessageId: ERROR_NDIS_MEDIA_DISCONNECTED
    //
    // MessageText:
    //
    // The I/O operation failed because network media is disconnected or
    // wireless access point is out of range.
    //
    int ERROR_NDIS_MEDIA_DISCONNECTED = 0x8034001F;

    //
    // MessageId: ERROR_NDIS_INVALID_ADDRESS
    //
    // MessageText:
    //
    // The network address used in the request is invalid.
    //
    int ERROR_NDIS_INVALID_ADDRESS = 0x80340022;

    //
    // MessageId: ERROR_NDIS_INVALID_DEVICE_REQUEST
    //
    // MessageText:
    //
    // The specified request is not a valid operation for the target device.
    //
    int ERROR_NDIS_INVALID_DEVICE_REQUEST = 0x80340010;

    //
    // MessageId: ERROR_NDIS_PAUSED
    //
    // MessageText:
    //
    // The ofF;oad operation on the network interface has been paused.
    //
    int ERROR_NDIS_PAUSED = 0x8034002A;

    //
    // MessageId: ERROR_NDIS_INTERFACE_NOT_FOUND
    //
    // MessageText:
    //
    // Network interface was not found.
    //
    int ERROR_NDIS_INTERFACE_NOT_FOUND = 0x8034002B;

    //
    // MessageId: ERROR_NDIS_UNSUPPORTED_REVISION
    //
    // MessageText:
    //
    // The revision number specified in the structure is not supported.
    //
    int ERROR_NDIS_UNSUPPORTED_REVISION = 0x8034002C;

    //
    // MessageId: ERROR_NDIS_INVALID_PORT
    //
    // MessageText:
    //
    // The specified port does not exist on this network interface.
    //
    int ERROR_NDIS_INVALID_PORT = 0x8034002D;

    //
    // MessageId: ERROR_NDIS_INVALID_PORT_STATE
    //
    // MessageText:
    //
    // The current state of the specified port on this network interface does
    // not support the requested operation.
    //
    int ERROR_NDIS_INVALID_PORT_STATE = 0x8034002E;

    //
    // MessageId: ERROR_NDIS_LOW_POWER_STATE
    //
    // MessageText:
    //
    // The miniport adapter is in low power state.
    //
    int ERROR_NDIS_LOW_POWER_STATE = 0x8034002F;

    //
    // NDIS error codes (802.11 wireless LAN)
    //

    //
    // MessageId: ERROR_NDIS_DOT11_AUTO_CONFIG_ENABLED
    //
    // MessageText:
    //
    // The wireless local area network interface is in auto configuration mode
    // and doesn't support the requested parameter change operation.
    //
    int ERROR_NDIS_DOT11_AUTO_CONFIG_ENABLED = 0x80342000;

    //
    // MessageId: ERROR_NDIS_DOT11_MEDIA_IN_USE
    //
    // MessageText:
    //
    // The wireless local area network interface is busy and can not perform the
    // requested operation.
    //
    int ERROR_NDIS_DOT11_MEDIA_IN_USE = 0x80342001;

    //
    // MessageId: ERROR_NDIS_DOT11_POWER_STATE_INVALID
    //
    // MessageText:
    //
    // The wireless local area network interface is power down and doesn't
    // support the requested operation.
    //
    int ERROR_NDIS_DOT11_POWER_STATE_INVALID = 0x80342002;

    //
    // NDIS informational code (ndis.sys)
    //

    //
    // MessageId: ERROR_NDIS_INDICATION_REQUIRED
    //
    // MessageText:
    //
    // The request will be completed later by NDIS status indication.
    //
    int ERROR_NDIS_INDICATION_REQUIRED = 0x00340001;
}
