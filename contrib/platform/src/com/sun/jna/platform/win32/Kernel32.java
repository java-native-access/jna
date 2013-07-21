/* Copyright (c) 2007, 2013 Timothy Wall, Markus Karg, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import java.nio.Buffer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;

import com.sun.jna.platform.win32.WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION;

/**
 * Interface definitions for <code>kernel32.dll</code>. Includes additional
 * alternate mappings from {@link WinNT} which make use of NIO buffers.
 */
public interface Kernel32 extends WinNT {

    /** The instance. */
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32",
                                                      Kernel32.class, W32APIOptions.UNICODE_OPTIONS);

    /**
     * The FormatMessage function formats a message string. The function
     * requires a message definition as input. The message definition can come
     * from a buffer passed into the function. It can come from a message table
     * resource in an already-loaded module. Or the caller can ask the function
     * to search the system's message table resource(s) for the message
     * definition. The function finds the message definition in a message table
     * resource based on a message identifier and a language identifier. The
     * function copies the formatted message text to an output buffer,
     * processing any embedded insert sequences if requested.
     *
     * @param dwFlags
     *            Formatting options, and how to interpret the lpSource
     *            parameter. The low-order byte of dwFlags specifies how the
     *            function handles line breaks in the output buffer. The
     *            low-order byte can also specify the maximum width of a
     *            formatted output line.
     *            <p/>
     *            This version of the function assumes
     *            FORMAT_MESSAGE_ALLOCATE_BUFFER is <em>not</em> set.
     * @param lpSource
     *            Location of the message definition.
     * @param dwMessageId
     *            Message identifier for the requested message.
     * @param dwLanguageId
     *            Language identifier for the requested message.
     * @param lpBuffer
     *            Pointer to a buffer that receives the null-terminated string
     *            that specifies the formatted message.
     * @param nSize
     *            This this parameter specifies the size of the output buffer,
     *            in TCHARs. If FORMAT_MESSAGE_ALLOCATE_BUFFER is
     * @param va_list
     *            Pointer to an array of values that are used as insert values
     *            in the formatted message.
     * @return If the function succeeds, the return value is the number of
     *         TCHARs stored in the output buffer, excluding the terminating
     *         null character. If the function fails, the return value is zero.
     *         To get extended error information, call GetLastError.
     */
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, Buffer lpBuffer, int nSize, Pointer va_list);

    /**
     * Reads data from the specified file or input/output (I/O) device. Reads
     * occur at the position specified by the file pointer if supported by the
     * device.
     *
     * This function is designed for both synchronous and asynchronous
     * operations. For a similar function designed solely for asynchronous
     * operation, see ReadFileEx
     *
     * @param hFile
     *            A handle to the device (for example, a file, file stream,
     *            physical disk, volume, console buffer, tape drive, socket,
     *            communications resource, mailslot, or pipe).
     * @param lpBuffer
     *            A pointer to the buffer that receives the data read from a
     *            file or device.
     * @param nNumberOfBytesToRead
     *            The maximum number of bytes to be read.
     * @param lpNumberOfBytesRead
     *            A pointer to the variable that receives the number of bytes
     *            read when using a synchronous hFile parameter
     * @param lpOverlapped
     *            A pointer to an OVERLAPPED structure is required if the hFile
     *            parameter was opened with FILE_FLAG_OVERLAPPED, otherwise it
     *            can be NULL.
     * @return If the function succeeds, the return value is nonzero (TRUE). If
     *         the function fails, or is completing asynchronously, the return
     *         value is zero (FALSE). To get extended error information, call
     *         the GetLastError function.
     *
     *         Note The GetLastError code ERROR_IO_PENDING is not a failure; it
     *         designates the read operation is pending completion
     *         asynchronously. For more information, see Remarks.
     */
    boolean ReadFile(HANDLE hFile, Buffer lpBuffer, int nNumberOfBytesToRead,
                     IntByReference lpNumberOfBytesRead, WinBase.OVERLAPPED lpOverlapped);

    /**
     * Frees the specified local memory object and invalidates its handle.
     *
     * @param hLocal
     *            A handle to the local memory object.
     * @return If the function succeeds, the return value is NULL. If the
     *         function fails, the return value is equal to a handle to the
     *         local memory object. To get extended error information, call
     *         GetLastError.
     */
    Pointer LocalFree(Pointer hLocal);

    /**
     * Frees the specified global memory object and invalidates its handle.
     *
     * @param hGlobal
     *            A handle to the global memory object.
     * @return If the function succeeds, the return value is NULL If the
     *         function fails, the return value is equal to a handle to the
     *         global memory object. To get extended error information, call
     *         GetLastError.
     */
    Pointer GlobalFree(Pointer hGlobal);

    /**
     * The GetModuleHandle function retrieves a module handle for the specified
     * module if the file has been mapped into the address space of the calling
     * process.
     *
     * @param name
     *            Pointer to a null-terminated string that contains the name of
     *            the module (either a .dll or .exe file).
     * @return If the function succeeds, the return value is a handle to the
     *         specified module. If the function fails, the return value is
     *         NULL. To get extended error information, call GetLastError.
     */
    HMODULE GetModuleHandle(String name);

    /**
     * The GetSystemTime function retrieves the current system date and time.
     * The system time is expressed in Coordinated Universal Time (UTC).
     *
     * @param lpSystemTime
     *            Pointer to a SYSTEMTIME structure to receive the current
     *            system date and time.
     */
    void GetSystemTime(WinBase.SYSTEMTIME lpSystemTime);

    /**
     * Retrieves the current local date and time.
     *
     * @param lpSystemTime
     *            A pointer to a SYSTEMTIME structure to receive the current
     *            local date and time.
     */
    void GetLocalTime(WinBase.SYSTEMTIME lpSystemTime);

    /**
     * The GetTickCount function retrieves the number of milliseconds that have
     * elapsed since the system was started, up to 49.7 days.
     *
     * @return Number of milliseconds that have elapsed since the system was
     *         started.
     */
    int GetTickCount();

    /**
     * The GetCurrentThreadId function retrieves the thread identifier of the
     * calling thread.
     *
     * @return The return value is the thread identifier of the calling thread.
     */
    int GetCurrentThreadId();

    /**
     * The GetCurrentThread function retrieves a pseudo handle for the current
     * thread.
     *
     * @return The return value is a pseudo handle for the current thread.
     */
    HANDLE GetCurrentThread();

    /**
     * This function returns the process identifier of the calling process.
     *
     * @return The return value is the process identifier of the calling
     *         process.
     */
    int GetCurrentProcessId();

    /**
     * This function returns a pseudohandle for the current process.
     *
     * @return The return value is a pseudohandle to the current process.
     */
    HANDLE GetCurrentProcess();

    /**
     * The GetProcessId function retrieves the process identifier of the
     * specified process.
     *
     * @param process
     *            Handle to the process. The handle must have the
     *            PROCESS_QUERY_INFORMATION access right.
     * @return If the function succeeds, the return value is the process
     *         identifier of the specified process. If the function fails, the
     *         return value is zero. To get extended error information, call
     *         GetLastError.
     */
    int GetProcessId(HANDLE process);

    /**
     * The GetProcessVersion function retrieves the major and minor version
     * numbers of the system on which the specified process expects to run.
     *
     * @param processId
     *            Process identifier of the process of interest. A value of zero
     *            specifies the calling process.
     * @return If the function succeeds, the return value is the version of the
     *         system on which the process expects to run. The high word of the
     *         return value contains the major version number. The low word of
     *         the return value contains the minor version number. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError. The function fails if ProcessId
     *         is an invalid value.
     */
    int GetProcessVersion(int processId);

    /**
     * Retrieves the termination status of the specified process.
     *
     * @param hProcess
     *            A handle to the process.
     * @param lpExitCode
     *            A pointer to a variable to receive the process termination
     *            status.
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean GetExitCodeProcess(HANDLE hProcess, IntByReference lpExitCode);

    /**
     * Terminates the specified process and all of its threads.
     *
     * @param hProcess
     *            A handle to the process to be terminated.
     * @param uExitCode
     *            The exit code to be used by the process and threads terminated
     *            as a result of this call.
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean TerminateProcess(HANDLE hProcess, int uExitCode);

    /**
     * The GetLastError function retrieves the calling thread's last-error code
     * value. The last-error code is maintained on a per-thread basis. Multiple
     * threads do not overwrite each other's last-error code.
     *
     * @return The return value is the calling thread's last-error code value.
     */
    int GetLastError();

    /**
     * The SetLastError function sets the last-error code for the calling
     * thread.
     *
     * @param dwErrCode
     *            Last-error code for the thread.
     */
    void SetLastError(int dwErrCode);

    /**
     * The GetDriveType function determines whether a disk drive is a removable,
     * fixed, CD-ROM, RAM disk, or network drive.
     *
     * @param lpRootPathName
     *            Pointer to a null-terminated string that specifies the root
     *            directory of the disk to return information about. A trailing
     *            backslash is required. If this parameter is NULL, the function
     *            uses the root of the current directory.
     * @return The return value specifies the type of drive.
     */
    int GetDriveType(String lpRootPathName);

    /**
     * The FormatMessage function formats a message string. The function
     * requires a message definition as input. The message definition can come
     * from a buffer passed into the function. It can come from a message table
     * resource in an already-loaded module. Or the caller can ask the function
     * to search the system's message table resource(s) for the message
     * definition. The function finds the message definition in a message table
     * resource based on a message identifier and a language identifier. The
     * function copies the formatted message text to an output buffer,
     * processing any embedded insert sequences if requested.
     *
     * @param dwFlags
     *            Formatting options, and how to interpret the lpSource
     *            parameter. The low-order byte of dwFlags specifies how the
     *            function handles line breaks in the output buffer. The
     *            low-order byte can also specify the maximum width of a
     *            formatted output line.
     *            <p/>
     *            This version of the function assumes
     *            FORMAT_MESSAGE_ALLOCATE_BUFFER is <em>not</em> set.
     * @param lpSource
     *            Location of the message definition.
     * @param dwMessageId
     *            Message identifier for the requested message.
     * @param dwLanguageId
     *            Language identifier for the requested message.
     * @param lpBuffer
     *            Pointer to a buffer that receives the null-terminated string
     *            that specifies the formatted message.
     * @param nSize
     *            This this parameter specifies the size of the output buffer,
     *            in TCHARs. If FORMAT_MESSAGE_ALLOCATE_BUFFER is
     * @param va_list
     *            Pointer to an array of values that are used as insert values
     *            in the formatted message.
     * @return If the function succeeds, the return value is the number of
     *         TCHARs stored in the output buffer, excluding the terminating
     *         null character. If the function fails, the return value is zero.
     *         To get extended error information, call GetLastError.
     */
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, Pointer lpBuffer, int nSize, Pointer va_list);

    /**
     * The FormatMessage function formats a message string. The function
     * requires a message definition as input. The message definition can come
     * from a buffer passed into the function. It can come from a message table
     * resource in an already-loaded module. Or the caller can ask the function
     * to search the system's message table resource(s) for the message
     * definition. The function finds the message definition in a message table
     * resource based on a message identifier and a language identifier. The
     * function copies the formatted message text to an output buffer,
     * processing any embedded insert sequences if requested.
     *
     * @param dwFlags
     *            Formatting options, and how to interpret the lpSource
     *            parameter. The low-order byte of dwFlags specifies how the
     *            function handles line breaks in the output buffer. The
     *            low-order byte can also specify the maximum width of a
     *            formatted output line.
     *            <p/>
     *            This version of the function assumes
     *            FORMAT_MESSAGE_ALLOCATE_BUFFER is set.
     * @param lpSource
     *            Location of the message definition.
     * @param dwMessageId
     *            Message identifier for the requested message.
     * @param dwLanguageId
     *            Language identifier for the requested message.
     * @param lpBuffer
     *            Pointer to a pointer that receives the allocated buffer in
     *            which the null-terminated string that specifies the formatted
     *            message is written.
     * @param nSize
     *            This parameter specifies the minimum number of TCHARs to
     *            allocate for an output buffer.
     * @param va_list
     *            Pointer to an array of values that are used as insert values
     *            in the formatted message.
     * @return If the function succeeds, the return value is the number of
     *         TCHARs stored in the output buffer, excluding the terminating
     *         null character. If the function fails, the return value is zero.
     *         To get extended error information, call GetLastError.
     */
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, PointerByReference lpBuffer, int nSize,
                      Pointer va_list);

    /**
     * The CreateFile function creates or opens a file, file stream, directory,
     * physical disk, volume, console buffer, tape drive, communications
     * resource, mailslot, or named pipe. The function returns a handle that can
     * be used to access an object.
     *
     * @param lpFileName
     *            A pointer to a null-terminated string that specifies the name
     *            of an object to create or open.
     * @param dwDesiredAccess
     *            The access to the object, which can be read, write, or both.
     * @param dwShareMode
     *            The sharing mode of an object, which can be read, write, both,
     *            or none.
     * @param lpSecurityAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that determines
     *            whether or not the returned handle can be inherited by child
     *            processes. If lpSecurityAttributes is NULL, the handle cannot
     *            be inherited.
     * @param dwCreationDisposition
     *            An action to take on files that exist and do not exist.
     * @param dwFlagsAndAttributes
     *            The file attributes and flags.
     * @param hTemplateFile
     *            Handle to a template file with the GENERIC_READ access right.
     *            The template file supplies file attributes and extended
     *            attributes for the file that is being created. This parameter
     *            can be NULL.
     * @return If the function succeeds, the return value is an open handle to a
     *         specified file. If a specified file exists before the function
     *         call and dwCreationDisposition is CREATE_ALWAYS or OPEN_ALWAYS, a
     *         call to GetLastError returns ERROR_ALREADY_EXISTS, even when the
     *         function succeeds. If a file does not exist before the call,
     *         GetLastError returns 0 (zero). If the function fails, the return
     *         value is INVALID_HANDLE_VALUE. To get extended error information,
     *         call GetLastError.
     */
    HANDLE CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode,
                      WinBase.SECURITY_ATTRIBUTES lpSecurityAttributes,
                      int dwCreationDisposition, int dwFlagsAndAttributes,
                      HANDLE hTemplateFile);

    /**
     * Copies an existing file to a new file.
     *
     * @param lpExistingFileName
     *            The name of an existing file.
     *
     *            The name is limited to MAX_PATH characters. To extend this
     *            limit to 32,767 wide characters, prepend "\\?\" to the path.
     *            For more information, see Naming a File.
     *
     *            If lpExistingFileName does not exist, CopyFile fails, and
     *            GetLastError returns ERROR_FILE_NOT_FOUND.
     *
     * @param lpNewFileName
     *            The name of the new file.
     *
     *            The name is limited to MAX_PATH characters. To extend this
     *            limit to 32,767 wide characters, prepend "\\?\" to the path.
     *            For more information, see Naming a File.
     *
     * @param bFailIfExists
     *            If this parameter is TRUE and the new file specified by
     *            lpNewFileName already exists, the function fails. If this
     *            parameter is FALSE and the new file already exists, the
     *            function overwrites the existing file and succeeds.
     *
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean CopyFile(String lpExistingFileName, String lpNewFileName,
                     boolean bFailIfExists);

    /**
     * Moves an existing file or a directory, including its children.
     *
     * @param lpExistingFileName
     *            The current name of the file or directory on the local
     *            computer.
     *
     *            The name is limited to MAX_PATH characters. To extend this
     *            limit to 32,767 wide characters, prepend "\\?\" to the path.
     *            For more information, see Naming a File.
     * @param lpNewFileName
     *            The new name for the file or directory. The new name must not
     *            already exist. A new file may be on a different file system or
     *            drive. A new directory must be on the same drive.
     *
     *            The name is limited to MAX_PATH characters. To extend this
     *            limit to 32,767 wide characters, prepend "\\?\" to the path.
     *            For more information, see Naming a File.
     * @return true, if successful If the function succeeds, the return value is
     *         nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean MoveFile(String lpExistingFileName, String lpNewFileName);

    /**
     * Moves an existing file or directory, including its children, with various
     * move options.
     *
     * @param lpExistingFileName
     *            The current name of the file or directory on the local
     *            computer.
     *
     *            If dwFlags specifies MOVEFILE_DELAY_UNTIL_REBOOT, the file
     *            cannot exist on a remote share, because delayed operations are
     *            performed before the network is available.
     *
     *            The name is limited to MAX_PATH characters. To extend this
     *            limit to 32,767 wide characters, prepend "\\?\" to the path.
     *            For more information, see Naming a File
     *
     *            Windows 2000: If you prepend the file name with "\\?\", you
     *            cannot also specify the MOVEFILE_DELAY_UNTIL_REBOOT flag for
     *            dwFlags.
     * @param lpNewFileName
     *            The new name of the file or directory on the local computer.
     *
     *            When moving a file, the destination can be on a different file
     *            system or volume. If the destination is on another drive, you
     *            must set the MOVEFILE_COPY_ALLOWED flag in dwFlags.
     *
     *            When moving a directory, the destination must be on the same
     *            drive.
     *
     *            If dwFlags specifies MOVEFILE_DELAY_UNTIL_REBOOT and
     *            lpNewFileName is NULL, MoveFileEx registers the
     *            lpExistingFileName file to be deleted when the system
     *            restarts. If lpExistingFileName refers to a directory, the
     *            system removes the directory at restart only if the directory
     *            is empty.
     * @param dwFlags
     *            This parameter can be one or more of the following values.
     * @return true, if successful If the function succeeds, the return value is
     *         nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean MoveFileEx(String lpExistingFileName, String lpNewFileName,
                       DWORD dwFlags);

    /**
     * The CreateDirectory function creates a new directory. If the underlying
     * file system supports security on files and directories, the function
     * applies a specified security descriptor to the new directory.
     *
     * @param lpPathName
     *            Pointer to a null-terminated string that specifies the path of
     *            the directory to be created.
     * @param lpSecurityAttributes
     *            Pointer to a SECURITY_ATTRIBUTES structure. The
     *            lpSecurityDescriptor member of the structure specifies a
     *            security descriptor for the new directory. If
     *            lpSecurityAttributes is NULL, the directory gets a default
     *            security descriptor.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean CreateDirectory(String lpPathName,
                            WinBase.SECURITY_ATTRIBUTES lpSecurityAttributes);

    /**
     * Reads data from the specified file or input/output (I/O) device. Reads
     * occur at the position specified by the file pointer if supported by the
     * device.
     *
     * This function is designed for both synchronous and asynchronous
     * operations. For a similar function designed solely for asynchronous
     * operation, see ReadFileEx
     *
     * @param hFile
     *            A handle to the device (for example, a file, file stream,
     *            physical disk, volume, console buffer, tape drive, socket,
     *            communications resource, mailslot, or pipe).
     * @param lpBuffer
     *            A pointer to the buffer that receives the data read from a
     *            file or device.
     * @param nNumberOfBytesToRead
     *            The maximum number of bytes to be read.
     * @param lpNumberOfBytesRead
     *            A pointer to the variable that receives the number of bytes
     *            read when using a synchronous hFile parameter
     * @param lpOverlapped
     *            A pointer to an OVERLAPPED structure is required if the hFile
     *            parameter was opened with FILE_FLAG_OVERLAPPED, otherwise it
     *            can be NULL.
     * @return If the function succeeds, the return value is nonzero (TRUE). If
     *         the function fails, or is completing asynchronously, the return
     *         value is zero (FALSE). To get extended error information, call
     *         the GetLastError function.
     *
     *         Note The GetLastError code ERROR_IO_PENDING is not a failure; it
     *         designates the read operation is pending completion
     *         asynchronously. For more information, see Remarks.
     */
    boolean ReadFile(HANDLE hFile, Pointer lpBuffer, int nNumberOfBytesToRead,
                     IntByReference lpNumberOfBytesRead, WinBase.OVERLAPPED lpOverlapped);

    /**
     * Creates an input/output (I/O) completion port and associates it with a
     * specified file handle, or creates an I/O completion port that is not yet
     * associated with a file handle, allowing association at a later time.
     *
     * @param FileHandle
     *            An open file handle or INVALID_HANDLE_VALUE.
     * @param ExistingCompletionPort
     *            A handle to an existing I/O completion port or NULL.
     * @param CompletionKey
     *            The per-handle user-defined completion key that is included in
     *            every I/O completion packet for the specified file handle.
     * @param NumberOfConcurrentThreads
     *            The maximum number of threads that the operating system can
     *            allow to concurrently process I/O completion packets for the
     *            I/O completion port.
     * @return If the function succeeds, the return value is the handle to an
     *         I/O completion port: If the ExistingCompletionPort parameter was
     *         NULL, the return value is a new handle. If the
     *         ExistingCompletionPort parameter was a valid I/O completion port
     *         handle, the return value is that same handle. If the FileHandle
     *         parameter was a valid handle, that file handle is now associated
     *         with the returned I/O completion port. If the function fails, the
     *         return value is NULL. To get extended error information, call the
     *         GetLastError function.
     */
    HANDLE CreateIoCompletionPort(HANDLE FileHandle,
                                  HANDLE ExistingCompletionPort, Pointer CompletionKey,
                                  int NumberOfConcurrentThreads);

    /**
     * Attempts to dequeue an I/O completion packet from the specified I/O
     * completion port. If there is no completion packet queued, the function
     * waits for a pending I/O operation associated with the completion port to
     * complete.
     *
     * @param CompletionPort
     *            A handle to the completion port.
     * @param lpNumberOfBytes
     *            A pointer to a variable that receives the number of bytes
     *            transferred during an I/O operation that has completed.
     * @param lpCompletionKey
     *            A pointer to a variable that receives the completion key value
     *            associated with the file handle whose I/O operation has
     *            completed.
     * @param lpOverlapped
     *            A pointer to a variable that receives the address of the
     *            OVERLAPPED structure that was specified when the completed I/O
     *            operation was started.
     * @param dwMilliseconds
     *            The number of milliseconds that the caller is willing to wait
     *            for a completion packet to appear at the completion port.
     * @return Returns nonzero (TRUE) if successful or zero (FALSE) otherwise.
     */
    boolean GetQueuedCompletionStatus(HANDLE CompletionPort,
                                      IntByReference lpNumberOfBytes,
                                      ULONG_PTRByReference lpCompletionKey,
                                      PointerByReference lpOverlapped, int dwMilliseconds);

    /**
     * Posts an I/O completion packet to an I/O completion port.
     *
     * @param CompletionPort
     *            A handle to an I/O completion port to which the I/O completion
     *            packet is to be posted.
     * @param dwNumberOfBytesTransferred
     *            The value to be returned through the
     *            lpNumberOfBytesTransferred parameter of the
     *            GetQueuedCompletionStatus function.
     * @param dwCompletionKey
     *            The value to be returned through the lpCompletionKey parameter
     *            of the GetQueuedCompletionStatus function.
     * @param lpOverlapped
     *            The value to be returned through the lpOverlapped parameter of
     *            the GetQueuedCompletionStatus function.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError .
     */
    boolean PostQueuedCompletionStatus(HANDLE CompletionPort,
                                       int dwNumberOfBytesTransferred, Pointer dwCompletionKey,
                                       WinBase.OVERLAPPED lpOverlapped);

    /**
     * Waits until the specified object is in the signaled state or the time-out
     * interval elapses. To enter an alertable wait state, use the
     * WaitForSingleObjectEx function. To wait for multiple objects, use the
     * WaitForMultipleObjects.
     *
     * @param hHandle
     *            A handle to the object. For a list of the object types whose
     *            handles can be specified, see the following Remarks section.
     *            If this handle is closed while the wait is still pending, the
     *            function's behavior is undefined. The handle must have the
     *            SYNCHRONIZE access right. For more information, see Standard
     *            Access Rights.
     * @param dwMilliseconds
     *            The time-out interval, in milliseconds. If a nonzero value is
     *            specified, the function waits until the object is signaled or
     *            the interval elapses. If dwMilliseconds is zero, the function
     *            does not enter a wait state if the object is not signaled; it
     *            always returns immediately. If dwMilliseconds is INFINITE, the
     *            function will return only when the object is signaled.
     * @return If the function succeeds, the return value indicates the event
     *         that caused the function to return.
     */
    int WaitForSingleObject(HANDLE hHandle, int dwMilliseconds);

    /**
     * Waits until one or all of the specified objects are in the signaled state
     * or the time-out interval elapses. To enter an alertable wait state, use
     * the WaitForMultipleObjectsEx function.
     *
     * @param nCount
     *            The number of object handles in the array pointed to by
     *            lpHandles. The maximum number of object handles is
     *            MAXIMUM_WAIT_OBJECTS.
     * @param hHandle
     *            An array of object handles. For a list of the object types
     *            whose handles can be specified, see the following Remarks
     *            section. The array can contain handles to objects of different
     *            types. It may not contain multiple copies of the same handle.
     *            If one of these handles is closed while the wait is still
     *            pending, the function's behavior is undefined. The handles
     *            must have the SYNCHRONIZE access right. For more information,
     *            see Standard Access Rights.
     * @param bWaitAll
     *            If this parameter is TRUE, the function returns when the state
     *            of all objects in the lpHandles array is signaled. If FALSE,
     *            the function returns when the state of any one of the objects
     *            is set to signaled. In the latter case, the return value
     *            indicates the object whose state caused the function to
     *            return.
     * @param dwMilliseconds
     *            The time-out interval, in milliseconds. If a nonzero value is
     *            specified, the function waits until the specified objects are
     *            signaled or the interval elapses. If dwMilliseconds is zero,
     *            the function does not enter a wait state if the specified
     *            objects are not signaled; it always returns immediately. If
     *            dwMilliseconds is INFINITE, the function will return only when
     *            the specified objects are signaled.
     * @return If the function succeeds, the return value indicates the event
     *         that caused the function to return.
     */
    int WaitForMultipleObjects(int nCount, HANDLE[] hHandle, boolean bWaitAll,
                               int dwMilliseconds);

    /**
     * The DuplicateHandle function duplicates an object handle.
     *
     * @param hSourceProcessHandle
     *            Handle to the process with the handle to duplicate. The handle
     *            must have the PROCESS_DUP_HANDLE access right.
     * @param hSourceHandle
     *            Handle to duplicate. This is an open object handle that is
     *            valid in the context of the source process.
     * @param hTargetProcessHandle
     *            Handle to the process that is to receive the duplicated
     *            handle. The handle must have the PROCESS_DUP_HANDLE access
     *            right.
     * @param lpTargetHandle
     *            Pointer to a variable that receives the duplicate handle. This
     *            handle value is valid in the context of the target process. If
     *            hSourceHandle is a pseudo handle returned by GetCurrentProcess
     *            or GetCurrentThread, DuplicateHandle converts it to a real
     *            handle to a process or thread, respectively.
     * @param dwDesiredAccess
     *            Access requested for the new handle.
     * @param bInheritHandle
     *            Indicates whether the handle is inheritable.
     * @param dwOptions
     *            Optional actions.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean DuplicateHandle(HANDLE hSourceProcessHandle, HANDLE hSourceHandle,
                            HANDLE hTargetProcessHandle, HANDLEByReference lpTargetHandle,
                            int dwDesiredAccess, boolean bInheritHandle, int dwOptions);

    /**
     * The CloseHandle function closes an open object handle.
     *
     * @param hObject
     *            Handle to an open object. This parameter can be a pseudo
     *            handle or INVALID_HANDLE_VALUE.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean CloseHandle(HANDLE hObject);

    /**
     * Retrieves information that describes the changes within the specified
     * directory. The function does not report changes to the specified
     * directory itself. Note: there's no ReadDirectoryChangesA.
     *
     * @param directory
     *            A handle to the directory to be monitored. This directory must
     *            be opened with the FILE_LIST_DIRECTORY access right.
     * @param info
     *            A pointer to the DWORD-aligned formatted buffer in which the
     *            read results are to be returned.
     * @param length
     *            The size of the buffer that is pointed to by the lpBuffer
     *            parameter, in bytes.
     * @param watchSubtree
     *            If this parameter is TRUE, the function monitors the directory
     *            tree rooted at the specified directory. If this parameter is
     *            FALSE, the function monitors only the directory specified by
     *            the hDirectory parameter.
     * @param notifyFilter
     *            The filter criteria that the function checks to determine if
     *            the wait operation has completed.
     * @param bytesReturned
     *            For synchronous calls, this parameter receives the number of
     *            bytes transferred into the lpBuffer parameter. For
     *            asynchronous calls, this parameter is undefined. You must use
     *            an asynchronous notification technique to retrieve the number
     *            of bytes transferred.
     * @param overlapped
     *            A pointer to an OVERLAPPED structure that supplies data to be
     *            used during asynchronous operation. Otherwise, this value is
     *            NULL. The Offset and OffsetHigh members of this structure are
     *            not used.
     * @param completionRoutine
     *            A pointer to a completion routine to be called when the
     *            operation has been completed or canceled and the calling
     *            thread is in an alertable wait state.
     * @return If the function succeeds, the return value is nonzero. For
     *         synchronous calls, this means that the operation succeeded. For
     *         asynchronous calls, this indicates that the operation was
     *         successfully queued. If the function fails, the return value is
     *         zero. To get extended error information, call GetLastError. If
     *         the network redirector or the target file system does not support
     *         this operation, the function fails with ERROR_INVALID_FUNCTION.
     */
    public boolean ReadDirectoryChangesW(HANDLE directory,
                                         WinNT.FILE_NOTIFY_INFORMATION info, int length,
                                         boolean watchSubtree, int notifyFilter,
                                         IntByReference bytesReturned, WinBase.OVERLAPPED overlapped,
                                         OVERLAPPED_COMPLETION_ROUTINE completionRoutine);

    /**
     * Retrieves the short path form of the specified path.
     *
     * @param lpszLongPath
     *            The path string.
     * @param lpdzShortPath
     *            A pointer to a buffer to receive the null-terminated short
     *            form of the path that lpszLongPath specifies.
     * @param cchBuffer
     *            The size of the buffer that lpszShortPath points to, in
     *            TCHARs.
     * @return If the function succeeds, the return value is the length, in
     *         TCHARs, of the string that is copied to lpszShortPath, not
     *         including the terminating null character. If the lpszShortPath
     *         buffer is too small to contain the path, the return value is the
     *         size of the buffer, in TCHARs, that is required to hold the path
     *         and the terminating null character. If the function fails for any
     *         other reason, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    int GetShortPathName(String lpszLongPath, char[] lpdzShortPath,
                         int cchBuffer);

    /**
     * The LocalAlloc function allocates the specified number of bytes from the
     * heap. Windows memory management does not provide a separate local heap
     * and global heap.
     *
     * @param uFlags
     *            Memory allocation attributes. The default is the LMEM_FIXED
     *            value.
     * @param uBytes
     *            Number of bytes to allocate. If this parameter is zero and the
     *            uFlags parameter specifies LMEM_MOVEABLE, the function returns
     *            a handle to a memory object that is marked as discarded.
     * @return If the function succeeds, the return value is a handle to the
     *         newly allocated memory object. If the function fails, the return
     *         value is NULL. To get extended error information, call
     *         GetLastError.
     */
    Pointer LocalAlloc(int /* UINT */ uFlags, int /* SIZE_T */ uBytes);

    /**
     * Writes data to the specified file or input/output (I/O) device.
     *
     * @param hFile
     *            A handle to the file or I/O device (for example, a file, file
     *            stream, physical disk, volume, console buffer, tape drive,
     *            socket, communications resource, mailslot, or pipe).
     * @param lpBuffer
     *            A pointer to the buffer containing the data to be written to
     *            the file or device.
     * @param nNumberOfBytesToWrite
     *            The number of bytes to be written to the file or device.
     * @param lpNumberOfBytesWritten
     *            A pointer to the variable that receives the number of bytes
     *            written when using a synchronous hFile parameter.
     * @param lpOverlapped
     *            A pointer to an OVERLAPPED structure is required if the hFile
     *            parameter was opened with FILE_FLAG_OVERLAPPED, otherwise this
     *            parameter can be NULL.
     * @return If the function succeeds, the return value is nonzero (TRUE). If
     *         the function fails, or is completing asynchronously, the return
     *         value is zero (FALSE). To get extended error information, call
     *         the GetLastError function.
     */
    boolean WriteFile(HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToWrite,
                      IntByReference lpNumberOfBytesWritten,
                      WinBase.OVERLAPPED lpOverlapped);

    /**
     * Creates or opens a named or unnamed event object.
     *
     * @param lpEventAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure. If this
     *            parameter is NULL, the handle cannot be inherited by child
     *            processes.
     * @param bManualReset
     *            If this parameter is TRUE, the function creates a manual-reset
     *            event object, which requires the use of the ResetEvent
     *            function to set the event state to nonsignaled. If this
     *            parameter is FALSE, the function creates an auto-reset event
     *            object, and system automatically resets the event state to
     *            nonsignaled after a single waiting thread has been released.
     * @param bInitialState
     *            If this parameter is TRUE, the initial state of the event
     *            object is signaled; otherwise, it is nonsignaled.
     * @param lpName
     *            The name of the event object. The name is limited to MAX_PATH
     *            characters. Name comparison is case sensitive.
     * @return If the function succeeds, the return value is a handle to the
     *         event object. If the named event object existed before the
     *         function call, the function returns a handle to the existing
     *         object and GetLastError returns ERROR_ALREADY_EXISTS. If the
     *         function fails, the return value is NULL. To get extended error
     *         information, call GetLastError.
     */
    HANDLE CreateEvent(WinBase.SECURITY_ATTRIBUTES lpEventAttributes,
                       boolean bManualReset, boolean bInitialState, String lpName);

    /**
     * Sets the specified event object to the signaled state.
     *
     * @param hEvent
     *            A handle to the event object. The CreateEvent or OpenEvent
     *            function returns this handle.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean SetEvent(HANDLE hEvent);

    /**
     * Sets the specified event object to the signaled state and then resets it
     * to the nonsignaled state after releasing the appropriate number of
     * waiting threads.
     *
     * @param hEvent
     *            A handle to the event object. The CreateEvent or OpenEvent
     *            function returns this handle.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean PulseEvent(HANDLE hEvent);

    /**
     * Creates or opens a named or unnamed file mapping object for a specified
     * file.
     *
     * @param hFile
     *            A handle to the file from which to create a file mapping
     *            object.
     * @param lpAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that determines
     *            whether a returned handle can be inherited by child processes.
     *            The lpSecurityDescriptor member of the SECURITY_ATTRIBUTES
     *            structure specifies a security descriptor for a new file
     *            mapping object.
     * @param flProtect
     *            Specifies the page protection of the file mapping object. All
     *            mapped views of the object must be compatible with this
     *            protection.
     * @param dwMaximumSizeHigh
     *            The high-order DWORD of the maximum size of the file mapping
     *            object.
     * @param dwMaximumSizeLow
     *            The low-order DWORD of the maximum size of the file mapping
     *            object.
     * @param lpName
     *            The name of the file mapping object.
     * @return If the function succeeds, the return value is a handle to the
     *         newly created file mapping object. If the object exists before
     *         the function call, the function returns a handle to the existing
     *         object (with its current size, not the specified size), and
     *         GetLastError returns ERROR_ALREADY_EXISTS. If the function fails,
     *         the return value is NULL. To get extended error information, call
     *         GetLastError.
     */
    HANDLE CreateFileMapping(HANDLE hFile,
                             WinBase.SECURITY_ATTRIBUTES lpAttributes, int flProtect,
                             int dwMaximumSizeHigh, int dwMaximumSizeLow, String lpName);

    /**
     * Maps a view of a file mapping into the address space of a calling
     * process.
     *
     * @param hFileMappingObject
     *            A handle to a file mapping object. The CreateFileMapping and
     *            OpenFileMapping functions return this handle.
     * @param dwDesiredAccess
     *            The type of access to a file mapping object, which determines
     *            the protection of the pages.
     * @param dwFileOffsetHigh
     *            A high-order DWORD of the file offset where the view begins.
     * @param dwFileOffsetLow
     *            A low-order DWORD of the file offset where the view is to
     *            begin.
     * @param dwNumberOfBytesToMap
     *            The number of bytes of a file mapping to map to the view.
     * @return If the function succeeds, the return value is the starting
     *         address of the mapped view. If the function fails, the return
     *         value is NULL. To get extended error information, call
     *         GetLastError.
     */
    Pointer MapViewOfFile(HANDLE hFileMappingObject, int dwDesiredAccess,
                          int dwFileOffsetHigh, int dwFileOffsetLow, int dwNumberOfBytesToMap);

    /**
     * Unmaps a mapped view of a file from the calling process's address space.
     *
     * @param lpBaseAddress
     *            A pointer to the base address of the mapped view of a file
     *            that is to be unmapped.
     * @return If the function succeeds, the return value is the starting
     *         address of the mapped view. If the function fails, the return
     *         value is NULL. To get extended error information, call
     *         GetLastError.
     */
    boolean UnmapViewOfFile(Pointer lpBaseAddress);

    /**
     * Retrieves only the NetBIOS name of the local computer.
     *
     * @param buffer
     *            A pointer to a buffer that receives the computer name or the
     *            cluster virtual server name. The buffer size should be large
     *            enough to contain MAX_COMPUTERNAME_LENGTH + 1 characters.
     * @param lpnSize
     *            On input, specifies the size of the buffer, in TCHARs. On
     *            output, the number of TCHARs copied to the destination buffer,
     *            not including the terminating null character. If the buffer is
     *            too small, the function fails and GetLastError returns
     *            ERROR_BUFFER_OVERFLOW. The lpnSize parameter specifies the
     *            size of the buffer required, including the terminating null
     *            character.
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    public boolean GetComputerName(char[] buffer, IntByReference lpnSize);

    /**
     * The OpenThread function opens an existing thread object.
     *
     * @param dwDesiredAccess
     *            Access to the thread object. This access right is checked
     *            against any security descriptor for the thread.
     * @param bInheritHandle
     *            If this parameter is TRUE, the new process inherits the
     *            handle. If the parameter is FALSE, the handle is not
     *            inherited.
     * @param dwThreadId
     *            Identifier of the thread to be opened.
     * @return If the function succeeds, the return value is an open handle to
     *         the specified process. If the function fails, the return value is
     *         NULL. To get extended error information, call GetLastError.
     */
    HANDLE OpenThread(int dwDesiredAccess, boolean bInheritHandle,
                      int dwThreadId);

    /**
     * Creates a new process and its primary thread. The new process runs in the
     * security context of the calling process.
     *
     * @param lpApplicationName
     *            The name of the module to be executed.
     * @param lpCommandLine
     *            The command line to be executed.
     * @param lpProcessAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that determines
     *            whether the returned handle to the new process object can be
     *            inherited by child processes. If lpProcessAttributes is NULL,
     *            the handle cannot be inherited.
     *
     * @param lpThreadAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that determines
     *            whether the returned handle to the new thread object can be
     *            inherited by child processes. If lpThreadAttributes is NULL,
     *            the handle cannot be inherited.
     *
     * @param bInheritHandles
     *            If this parameter TRUE, each inheritable handle in the calling
     *            process is inherited by the new process. If the parameter is
     *            FALSE, the handles are not inherited. Note that inherited
     *            handles have the same value and access rights as the original
     *            handles.
     *
     * @param dwCreationFlags
     *            The flags that control the priority class and the creation of
     *            the process.
     * @param lpEnvironment
     *            A pointer to the environment block for the new process. If
     *            this parameter is NULL, the new process uses the environment
     *            of the calling process.
     *
     * @param lpCurrentDirectory
     *            The full path to the current directory for the process.
     * @param lpStartupInfo
     *            A pointer to a STARTUPINFO or STARTUPINFOEX structure.
     * @param lpProcessInformation
     *            A pointer to a PROCESS_INFORMATION structure that receives
     *            identification information about the new process.
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean CreateProcess(String lpApplicationName, String lpCommandLine,
                          WinBase.SECURITY_ATTRIBUTES lpProcessAttributes,
                          WinBase.SECURITY_ATTRIBUTES lpThreadAttributes,
                          boolean bInheritHandles, DWORD dwCreationFlags,
                          Pointer lpEnvironment, String lpCurrentDirectory,
                          WinBase.STARTUPINFO lpStartupInfo,
                          WinBase.PROCESS_INFORMATION lpProcessInformation);

    /**
     * Creates a new process and its primary thread. The new process runs in the
     * security context of the calling process.
     * 
     * @param lpApplicationName
     *            The name of the module to be executed.
     * @param lpCommandLine
     *            The command line to be executed. The maximum length of
     *            this string is 32,768 characters, including the Unicode
     *            terminating null character. If <i>lpApplicationName</i> is
     *            NULL, the module name portion of <i>lpCommandLine</i> is
     *            limited to MAX_PATH characters.
     *            <p/>
     *            The Unicode version of this function, {@link #CreateProcessW},
     *            can modify the contents of this string. Therefore, this
     *            parameter cannot be a pointer to read-only memory (such
     *            as a const variable or a literal string). If this
     *            parameter is a constant string, the function may cause
     *            an access violation.
     *            <p/>
     *            The <i>lpCommandLine</i> parameter can be NULL. In that case,
     *            the function uses the string pointed to by
     *            <i>lpApplicationName</i> as the command line.
     *            <p/>
     *            If both <i>lpApplicationName</i> and <i>lpCommandLine</i> are
     *            non-NULL, the null-terminated string pointed to by
     *            <i>lpApplicationName</i> specifies the module to execute, and
     *            the null-terminated string pointed to by <i>lpCommandLine</i>
     *            specifies the command line. The new process can use
     *            GetCommandLine to retrieve the entire command
     *            line. Console processes written in C can use the argc
     *            and argv arguments to parse the command line. Because
     *            argv[0] is the module name, C programmers generally
     *            repeat the module name as the first token in the command
     *            line. 
     *            <p/>
     *            If <i>lpApplicationName</i> is NULL, the first white
     *            space-delimited token of the command line specifies the
     *            module name. If you are using a long file name that
     *            contains a space, use quoted strings to indicate where
     *            the file name ends and the arguments begin (see the
     *            explanation for the <i>lpApplicationName</i> parameter). If the
     *            file name does not contain an extension, .exe is
     *            appended. Therefore, if the file name extension is .com,
     *            this parameter must include the .com extension. If the
     *            file name ends in a period (.) with no extension, or if
     *            the file name contains a path, .exe is not appended. If
     *            the file name does not contain a directory path, the
     *            system searches for the executable file in the following
     *            sequence: 
     *            <ul>
     *            <li>The directory from which the application loaded.
     *            <li>The current directory for the parent process.
     *            <li>The 32-bit Windows system directory. Use the
     *            GetSystemDirectory function to get the path of this
     *            directory. 
     *            <li>The 16-bit Windows system directory. There is no
     *            function that obtains the path of this directory, but it
     *            is searched. The name of this directory is System. 
     *            <li>The Windows directory. Use the GetWindowsDirectory
     *            function to get the path of this directory. 
     *            <li>The directories that are listed in the PATH
     *            environment variable. Note that this function does not
     *            search the per-application path specified by the App
     *            Paths registry key. To include this per-application path
     *            in the search sequence, use the ShellExecute function. 
     *            </ul>
     *            The system adds a terminating null character to the
     *            command-line string to separate the file name from the
     *            arguments. This divides the original string into two
     *            strings for internal processing. 
     * @param lpProcessAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that determines
     *            whether the returned handle to the new process object can be
     *            inherited by child processes. If lpProcessAttributes is NULL,
     *            the handle cannot be inherited.
     * @param lpThreadAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that determines
     *            whether the returned handle to the new thread object can be
     *            inherited by child processes. If lpThreadAttributes is NULL,
     *            the handle cannot be inherited.
     * @param bInheritHandles
     *            If this parameter TRUE, each inheritable handle in the calling
     *            process is inherited by the new process. If the parameter is
     *            FALSE, the handles are not inherited. Note that inherited
     *            handles have the same value and access rights as the original
     *            handles.
     * @param dwCreationFlags
     *            The flags that control the priority class and the creation of
     *            the process.
     * @param lpEnvironment
     *            A pointer to the environment block for the new process. If
     *            this parameter is NULL, the new process uses the environment
     *            of the calling process.
     * @param lpCurrentDirectory
     *            The full path to the current directory for the process.
     * @param lpStartupInfo
     *            A pointer to a STARTUPINFO or STARTUPINFOEX structure.
     * @param lpProcessInformation
     *            A pointer to a PROCESS_INFORMATION structure that receives
     *            identification information about the new process.
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean CreateProcessW(String lpApplicationName, char[] lpCommandLine,
                           WinBase.SECURITY_ATTRIBUTES lpProcessAttributes,
                           WinBase.SECURITY_ATTRIBUTES lpThreadAttributes,
                           boolean bInheritHandles, DWORD dwCreationFlags,
                           Pointer lpEnvironment, String lpCurrentDirectory,
                           WinBase.STARTUPINFO lpStartupInfo,
                           WinBase.PROCESS_INFORMATION lpProcessInformation);

    /**
     * This function returns a handle to an existing process object.
     *
     * @param fdwAccess
     *            Not supported; set to zero.
     * @param fInherit
     *            Not supported; set to FALSE.
     * @param IDProcess
     *            Specifies the process identifier of the process to open.
     * @return An open handle to the specified process indicates success. NULL
     *         indicates failure. To get extended error information, call
     *         GetLastError.
     */
    HANDLE OpenProcess(int fdwAccess, boolean fInherit, int IDProcess);

    /**
     * The GetTempPath function retrieves the path of the directory designated
     * for temporary files.
     *
     * @param nBufferLength
     *            Size of the string buffer identified by lpBuffer, in TCHARs.
     * @param buffer
     *            Pointer to a string buffer that receives the null-terminated
     *            string specifying the temporary file path. The returned string
     *            ends with a backslash, for example, C:\TEMP\.
     * @return If the function succeeds, the return value is the length, in
     *         TCHARs, of the string copied to lpBuffer, not including the
     *         terminating null character. If the return value is greater than
     *         nBufferLength, the return value is the length, in TCHARs, of the
     *         buffer required to hold the path.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    DWORD GetTempPath(DWORD nBufferLength, char[] buffer);

    /**
     * The GetVersion function returns the current version number of the
     * operating system.
     *
     * @return If the function succeeds, the return value includes the major and
     *         minor version numbers of the operating system in the low order
     *         word, and information about the operating system platform in the
     *         high order word.
     */
    DWORD GetVersion();

    /**
     * The GetVersionEx function obtains extended information about the version
     * of the operating system that is currently running.
     *
     * @param lpVersionInfo
     *            Pointer to an OSVERSIONINFO data structure that the function
     *            fills with operating system version information.
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero. To get extended
     *         error information, call GetLastError. The function fails if you
     *         specify an invalid value for the dwOSVersionInfoSize member of
     *         the OSVERSIONINFO or OSVERSIONINFOEX structure.
     */
    boolean GetVersionEx(OSVERSIONINFO lpVersionInfo);

    /**
     * The GetVersionEx function obtains extended information about the version
     * of the operating system that is currently running.
     *
     * @param lpVersionInfo
     *            Pointer to an OSVERSIONINFOEX data structure that the function
     *            fills with operating system version information.
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero. To get extended
     *         error information, call GetLastError. The function fails if you
     *         specify an invalid value for the dwOSVersionInfoSize member of
     *         the OSVERSIONINFO or OSVERSIONINFOEX structure.
     */
    boolean GetVersionEx(OSVERSIONINFOEX lpVersionInfo);

    /**
     * The GetSystemInfo function returns information about the current system.
     *
     * @param lpSystemInfo
     *            Pointer to a SYSTEM_INFO structure that receives the
     *            information.
     */
    void GetSystemInfo(SYSTEM_INFO lpSystemInfo);

    /**
     * The GetNativeSystemInfo function retrieves information about the current
     * system to an application running under WOW64. If the function is called
     * from a 64-bit application, it is equivalent to the GetSystemInfo
     * function.
     *
     * @param lpSystemInfo
     *            Pointer to a SYSTEM_INFO structure that receives the
     *            information.
     */
    void GetNativeSystemInfo(SYSTEM_INFO lpSystemInfo);

    /**
     * The IsWow64Process function determines whether the specified process is
     * running under WOW64.
     *
     * @param hProcess
     *            Handle to a process.
     * @param Wow64Process
     *            Pointer to a value that is set to TRUE if the process is
     *            running under WOW64. Otherwise, the value is set to FALSE.
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean IsWow64Process(HANDLE hProcess, IntByReference Wow64Process);

    /**
     * Retrieves information about logical processors and related hardware.
     *
     * @param buffer a buffer which receives an array of {@link SYSTEM_LOGICAL_PROCESSOR_INFORMATION} structures.
     * @param returnLength on input, specifies the length of the buffer in bytes. On output, receives the number of
     *        bytes actually returned, or if {@link #GetLastError()} returned
     *        {@link WinError#ERROR_INSUFFICIENT_BUFFER}, the number of bytes wanted for the call to work.
     * @return {@code true} on success, {@code false} on failure. To get extended error information, call
     *         {@link #GetLastError()}.
     */
    boolean GetLogicalProcessorInformation(Pointer buffer, DWORDByReference returnLength);

    /**
     * Retrieves information about the system's current usage of both physical
     * and virtual memory.
     *
     * @param lpBuffer
     *            A pointer to a MEMORYSTATUSEX structure that receives
     *            information about current memory availability.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GlobalMemoryStatusEx(MEMORYSTATUSEX lpBuffer);

    /**
     * Retrieves the date and time that a file or directory was created, last
     * accessed, and last modified.
     *
     * @param hFile
     *            A handle to the file or directory for which dates and times
     *            are to be retrieved. The handle must have been created using
     *            the CreateFile function with the GENERIC_READ access right.
     *
     * @param lpCreationTime
     *            A pointer to a FILETIME structure to receive the date and time
     *            the file or directory was created. This parameter can be NULL
     *            if the application does not require this information.
     *
     * @param lpLastAccessTime
     *            A pointer to a FILETIME structure to receive the date and time
     *            the file or directory was last accessed. The last access time
     *            includes the last time the file or directory was written to,
     *            read from, or, in the case of executable files, run. This
     *            parameter can be NULL if the application does not require this
     *            information.
     *
     * @param lpLastWriteTime
     *            A pointer to a FILETIME structure to receive the date and time
     *            the file or directory was last written to, truncated, or
     *            overwritten (for example, with WriteFile or SetEndOfFile).
     *            This date and time is not updated when file attributes or
     *            security descriptors are changed. This parameter can be NULL
     *            if the application does not require this information.
     *
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GetFileTime(HANDLE hFile,
			WinBase.FILETIME lpCreationTime,
			WinBase.FILETIME lpLastAccessTime,
			WinBase.FILETIME lpLastWriteTime);

    /**
     * Sets the date and time that the specified file or directory was created,
     * last accessed, or last modified.
     *
     * @param hFile
     *            A handle to the file or directory. The handle must have been
     *            created using the CreateFile function with the
     *            FILE_WRITE_ATTRIBUTES access right. For more information, see
     *            File Security and Access Rights.
     * @param lpCreationTime
     *            A pointer to a FILETIME structure that contains the new
     *            creation date and time for the file or directory. This
     *            parameter can be NULL if the application does not need to
     *            change this information.
     * @param lpLastAccessTime
     *            A pointer to a FILETIME structure that contains the new last
     *            access date and time for the file or directory. The last
     *            access time includes the last time the file or directory was
     *            written to, read from, or (in the case of executable files)
     *            run. This parameter can be NULL if the application does not
     *            need to change this information.
     *
     *            To preserve the existing last access time for a file even
     *            after accessing a file, call SetFileTime immediately after
     *            opening the file handle with this parameter's FILETIME
     *            structure members initialized to 0xFFFFFFFF.
     * @param lpLastWriteTime
     *            A pointer to a FILETIME structure that contains the new last
     *            modified date and time for the file or directory. This
     *            parameter can be NULL if the application does not need to
     *            change this information.
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    int SetFileTime(HANDLE hFile, WinBase.FILETIME lpCreationTime,
                    WinBase.FILETIME lpLastAccessTime, WinBase.FILETIME lpLastWriteTime);

    /**
     * Sets the attributes for a file or directory.
     *
     * @param lpFileName
     *            The name of the file whose attributes are to be set.
     *
     *            The name is limited to MAX_PATH characters. To extend this
     *            limit to 32,767 wide characters, prepend "\\?\" to the path.
     *
     * @param dwFileAttributes
     *            The file attributes to set for the file. This parameter can be
     *            one or more values, combined using the bitwise-OR operator.
     *            However, all other values override FILE_ATTRIBUTE_NORMAL.
     *
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean SetFileAttributes(String lpFileName, DWORD dwFileAttributes);

    /**
     * The GetLogicalDriveStrings function fills a buffer with strings that
     * specify valid drives in the system.
     *
     * @param nBufferLength
     *            Maximum size of the buffer pointed to by lpBuffer, in TCHARs.
     *            This size does not include the terminating null character. If
     *            this parameter is zero, lpBuffer is not used.
     * @param lpBuffer
     *            Pointer to a buffer that receives a series of null-terminated
     *            strings, one for each valid drive in the system, plus with an
     *            additional null character. Each string is a device name.
     * @return If the function succeeds, the return value is the length, in
     *         characters, of the strings copied to the buffer, not including
     *         the terminating null character. Note that an ANSI-ASCII null
     *         character uses one byte, but a Unicode null character uses two
     *         bytes. If the buffer is not large enough, the return value is
     *         greater than nBufferLength. It is the size of the buffer required
     *         to hold the drive strings. If the function fails, the return
     *         value is zero. To get extended error information, use the
     *         GetLastError function.
     */
    DWORD GetLogicalDriveStrings(DWORD nBufferLength, char[] lpBuffer);

    /**
     * The GetDiskFreeSpaceEx function retrieves information about the amount of
     * space that is available on a disk volume, which is the total amount of
     * space, the total amount of free space, and the total amount of free space
     * available to the user that is associated with the calling thread.
     *
     * @param lpDirectoryName
     *            A pointer to a null-terminated string that specifies a
     *            directory on a disk. If this parameter is NULL, the function
     *            uses the root of the current disk. If this parameter is a UNC
     *            name, it must include a trailing backslash, for example,
     *            \\MyServer\MyShare\. This parameter does not have to specify
     *            the root directory on a disk. The function accepts any
     *            directory on a disk.
     * @param lpFreeBytesAvailable
     *            A pointer to a variable that receives the total number of free
     *            bytes on a disk that are available to the user who is
     *            associated with the calling thread. This parameter can be
     *            NULL.
     * @param lpTotalNumberOfBytes
     *            A pointer to a variable that receives the total number of
     *            bytes on a disk that are available to the user who is
     *            associated with the calling thread. This parameter can be
     *            NULL.
     * @param lpTotalNumberOfFreeBytes
     *            A pointer to a variable that receives the total number of free
     *            bytes on a disk. This parameter can be NULL.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is 0 (zero). To get extended
     *         error information, call GetLastError.
     */
    boolean GetDiskFreeSpaceEx(String lpDirectoryName,
                               LARGE_INTEGER lpFreeBytesAvailable,
                               LARGE_INTEGER lpTotalNumberOfBytes,
                               LARGE_INTEGER lpTotalNumberOfFreeBytes);

    /**
     * Deletes an existing file.
     *
     * @param filename
     *            The name of the file to be deleted.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero (0). To get extended
     *         error information, call GetLastError.
     */
    public boolean DeleteFile(String filename);

    /**
     * Creates an anonymous pipe, and returns handles to the read and write ends
     * of the pipe.
     *
     * @param hReadPipe
     *            A pointer to a variable that receives the read handle for the
     *            pipe.
     * @param hWritePipe
     *            A pointer to a variable that receives the write handle for the
     *            pipe.
     * @param lpPipeAttributes
     *            A pointer to a SECURITY_ATTRIBUTES structure that determines
     *            whether the returned handle can be inherited by child
     *            processes.
     * @param nSize
     *            The size of the buffer for the pipe, in bytes.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    public boolean CreatePipe(HANDLEByReference hReadPipe,
                              HANDLEByReference hWritePipe,
                              WinBase.SECURITY_ATTRIBUTES lpPipeAttributes, int nSize);

    /**
     * Sets certain properties of an object handle.
     *
     * @param hObject
     *            A handle to an object whose information is to be set.
     * @param dwMask
     *            A mask that specifies the bit flags to be changed. Use the
     *            same constants shown in the description of dwFlags.
     * @param dwFlags
     *            Set of bit flags that specifies properties of the object
     *            handle.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean SetHandleInformation(HANDLE hObject, int dwMask, int dwFlags);

    /**
     * Retrieves file system attributes for a specified file or directory.
     *
     * @param lpFileName
     *            The name of the file or directory. Prepend \\?\ to the path
     *            for names up to 32,767 wide characters
     * @return INVALID_FILE_ATTRIBUTES if the function fails, otherwise the file
     *         attributes WinNT.FILE_ATTRIBUTE_*
     */
    public int GetFileAttributes(String lpFileName);

    /**
     * Retrieves the file type of the specified file.
     *
     * @param hFile
     *            A handle to the file.
     * @return FILE_TYPE_UNKNOWN if the function fails, or if the type is
     *         unknown. You can distinguish between a "valid" return of
     *         FILE_TYPE_UNKNOWN and its return due to a calling error (for
     *         example, passing an invalid handle to GetFileType) by calling
     *         GetLastError. If the function worked properly and
     *         FILE_TYPE_UNKNOWN was returned, a call to GetLastError will
     *         return NO_ERROR.
     */
    public int GetFileType(HANDLE hFile);

    /**
     * Sends a control code directly to a specified device driver, causing the
     * corresponding device to perform the corresponding operation.
     *
     * @param hDevice
     *            A handle to the device on which the operation is to be
     *            performed. The device is typically a volume, directory, file,
     *            or stream. To retrieve a device handle, use the CreateFile
     *            function. For more information, see Remarks.
     *
     * @param dwIoControlCode
     *            The control code for the operation. This value identifies the
     *            specific operation to be performed and the type of device on
     *            which to perform it. For a list of the control codes, see
     *            Remarks. The documentation for each control code provides
     *            usage details for the lpInBuffer, nInBufferSize, lpOutBuffer,
     *            and nOutBufferSize parameters.
     *
     * @param lpInBuffer
     *            A pointer to the input buffer that contains the data required
     *            to perform the operation. The format of this data depends on
     *            the value of the dwIoControlCode parameter. This parameter can
     *            be NULL if dwIoControlCode specifies an operation that does
     *            not require input data.
     *
     * @param nInBufferSize
     *            The size of the input buffer, in bytes.
     *
     * @param lpOutBuffer
     *            A pointer to the output buffer that is to receive the data
     *            returned by the operation. The format of this data depends on
     *            the value of the dwIoControlCode parameter. This parameter can
     *            be NULL if dwIoControlCode specifies an operation that does
     *            not return data.
     *
     * @param nOutBufferSize
     *            The size of the output buffer, in bytes.
     *
     * @param lpBytesReturned
     *            A pointer to a variable that receives the size of the data
     *            stored in the output buffer, in bytes. If the output buffer is
     *            too small to receive any data, the call fails, GetLastError
     *            returns ERROR_INSUFFICIENT_BUFFER, and lpBytesReturned is
     *            zero. If the output buffer is too small to hold all of the
     *            data but can hold some entries, some drivers will return as
     *            much data as fits. In this case, the call fails, GetLastError
     *            returns ERROR_MORE_DATA, and lpBytesReturned indicates the
     *            amount of data received. Your application should call
     *            DeviceIoControl again with the same operation, specifying a
     *            new starting point. If lpOverlapped is NULL, lpBytesReturned
     *            cannot be NULL. Even when an operation returns no output data
     *            and lpOutBuffer is NULL, DeviceIoControl makes use of
     *            lpBytesReturned. After such an operation, the value of
     *            lpBytesReturned is meaningless. If lpOverlapped is not NULL,
     *            lpBytesReturned can be NULL. If this parameter is not NULL and
     *            the operation returns data, lpBytesReturned is meaningless
     *            until the overlapped operation has completed. To retrieve the
     *            number of bytes returned, call GetOverlappedResult. If hDevice
     *            is associated with an I/O completion port, you can retrieve
     *            the number of bytes returned by calling
     *            GetQueuedCompletionStatus.
     *
     * @param lpOverlapped
     *            A pointer to an OVERLAPPED structure. If hDevice was opened
     *            without specifying FILE_FLAG_OVERLAPPED, lpOverlapped is
     *            ignored. If hDevice was opened with the FILE_FLAG_OVERLAPPED
     *            flag, the operation is performed as an overlapped
     *            (asynchronous) operation. In this case, lpOverlapped must
     *            point to a valid OVERLAPPED structure that contains a handle
     *            to an event object. Otherwise, the function fails in
     *            unpredictable ways. For overlapped operations, DeviceIoControl
     *            returns immediately, and the event object is signaled when the
     *            operation has been completed. Otherwise, the function does not
     *            return until the operation has been completed or an error
     *            occurs.
     *
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    boolean DeviceIoControl(HANDLE hDevice, int dwIoControlCode,
                            Pointer lpInBuffer, int nInBufferSize, Pointer lpOutBuffer,
                            int nOutBufferSize, IntByReference lpBytesReturned,
                            Pointer lpOverlapped);

    /**
     * Retrieves information about the amount of space that is available on a
     * disk volume, which is the total amount of space, the total amount of free
     * space, and the total amount of free space available to the user that is
     * associated with the calling thread.
     *
     * @param lpDirectoryName
     *            the lp directory name
     * @param lpFreeBytesAvailable
     *            the lp free bytes available
     * @param lpTotalNumberOfBytes
     *            the lp total number of bytes
     * @param lpTotalNumberOfFreeBytes
     *            the lp total number of free bytes
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero (0). To get
     *         extended error information, call GetLastError.
     */
    boolean GetDiskFreeSpaceEx(String lpDirectoryName,
                               LongByReference lpFreeBytesAvailable,
                               LongByReference lpTotalNumberOfBytes,
                               LongByReference lpTotalNumberOfFreeBytes);

    /**
     * Takes a snapshot of the specified processes, as well as the heaps,
     * modules, and threads used by these processes.
     *
     * @param dwFlags
     *            The portions of the system to be included in the snapshot.
     *
     * @param th32ProcessID
     *            The process identifier of the process to be included in the
     *            snapshot. This parameter can be zero to indicate the current
     *            process. This parameter is used when the TH32CS_SNAPHEAPLIST,
     *            TH32CS_SNAPMODULE, TH32CS_SNAPMODULE32, or TH32CS_SNAPALL
     *            value is specified. Otherwise, it is ignored and all processes
     *            are included in the snapshot.
     *
     *            If the specified process is the Idle process or one of the
     *            CSRSS processes, this function fails and the last error code
     *            is ERROR_ACCESS_DENIED because their access restrictions
     *            prevent user-level code from opening them.
     *
     *            If the specified process is a 64-bit process and the caller is
     *            a 32-bit process, this function fails and the last error code
     *            is ERROR_PARTIAL_COPY (299).
     *
     * @return If the function succeeds, it returns an open handle to the
     *         specified snapshot.
     *
     *         If the function fails, it returns INVALID_HANDLE_VALUE. To get
     *         extended error information, call GetLastError. Possible error
     *         codes include ERROR_BAD_LENGTH.
     */
    HANDLE CreateToolhelp32Snapshot(DWORD dwFlags, DWORD th32ProcessID);

    /**
     * Retrieves information about the first process encountered in a system
     * snapshot.
     *
     * @param hSnapshot
     *            A handle to the snapshot returned from a previous call to the
     *            CreateToolhelp32Snapshot function.
     * @param lppe
     *            A pointer to a PROCESSENTRY32 structure. It contains process
     *            information such as the name of the executable file, the
     *            process identifier, and the process identifier of the parent
     *            process.
     * @return Returns TRUE if the first entry of the process list has been
     *         copied to the buffer or FALSE otherwise. The ERROR_NO_MORE_FILES
     *         error value is returned by the GetLastError function if no
     *         processes exist or the snapshot does not contain process
     *         information.
     */
    boolean Process32First(HANDLE hSnapshot,
                           Tlhelp32.PROCESSENTRY32 lppe);

    /**
     * Retrieves information about the next process recorded in a system
     * snapshot.
     *
     * @param hSnapshot
     *            A handle to the snapshot returned from a previous call to the
     *            CreateToolhelp32Snapshot function.
     * @param lppe
     *            A pointer to a PROCESSENTRY32 structure.
     * @return Returns TRUE if the next entry of the process list has been
     *         copied to the buffer or FALSE otherwise. The ERROR_NO_MORE_FILES
     *         error value is returned by the GetLastError function if no
     *         processes exist or the snapshot does not contain process
     *         information.
     */
    boolean Process32Next(HANDLE hSnapshot,
                          Tlhelp32.PROCESSENTRY32 lppe);

    /**
     * The SetEnvironmentVariable function sets the contents of the specified
     * environment variable for the current process.
     *
     * @param lpName
     *            Pointer to a string containing the name of the environment
     *            variable to set.
     * @param lpValue
     *            Pointer to a string containing the value to set it to. if this
     *            value is NULL, the variable is deleted from the current
     *            process' environment.
     *
     * @return If the function succeeds, the return value is non-zero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean SetEnvironmentVariable(String lpName, String lpValue);

    /**
     * Retrieves the contents of the specified variable from the environment
     * block of the calling process.
     *
     * @param lpName
     *            The name of the environment variable.
     * @param lpBuffer
     *            A pointer to a buffer that receives the contents of the
     *            specified environment variable as a null-terminated string. An
     *            environment variable has a maximum size limit of 32,767
     *            characters, including the null-terminating character.
     * @param nSize
     *            The size of the buffer pointed to by the lpBuffer parameter,
     *            including the null-terminating character, in characters.
     * @return If the function succeeds, the return value is the number of
     *         characters stored in the buffer pointed to by lpBuffer, not
     *         including the terminating null character. If lpBuffer is not
     *         large enough to hold the data, the return value is the buffer
     *         size, in characters, required to hold the string and its
     *         terminating null character and the contents of lpBuffer are
     *         undefined. If the function fails, the return value is zero. To
     *         get extended error information, call GetLastError.
     */
    int GetEnvironmentVariable(String lpName, char[] lpBuffer, int nSize);

    /**
     * Returns the locale identifier for the system locale.
     *
     * @return Returns the locale identifier for the system default locale,
     *         identified by LOCALE_SYSTEM_DEFAULT.
     */
    LCID GetSystemDefaultLCID();

    /**
     * Returns the locale identifier for the user default locale.
     *
     * @return Returns the locale identifier for the user default locale,
     *         represented as LOCALE_USER_DEFAULT. If the user default locale is
     *         a custom locale, this function always returns
     *         LOCALE_CUSTOM_DEFAULT, regardless of the custom locale that is
     *         selected. For example, whether the user locale is Hawaiian (US),
     *         haw-US, or Fijiian (Fiji), fj-FJ, the function returns the same
     *         value.
     */
    LCID GetUserDefaultLCID();

    /**
     * Retrieves an integer associated with a key in the specified section of an initialization file.
     *
     * @param appName
     *            The name of the section in the initialization file.
     * @param keyName
     *            The name of the key whose value is to be retrieved. This value is in the form of a string; the {@link #GetPrivateProfileInt} function converts
     *            the string into an integer and returns the integer.
     * @param defaultValue
     *            The default value to return if the key name cannot be found in the initialization file.
     * @param fileName
     *            The name of the initialization file. If this parameter does not contain a full path to the file, the system searches for the file in the
     *            Windows directory.
     * @return The return value is the integer equivalent of the string following the specified key name in the specified initialization file. If the key is not
     *         found, the return value is the specified default value.
     */
    int GetPrivateProfileInt(String appName, String keyName, int defaultValue, String fileName);

    /**
     * Retrieves a string from the specified section in an initialization file.
     *
     * @param lpAppName
     *            The name of the section containing the key name. If this parameter is {@code null}, the {@link #GetPrivateProfileString} function copies all
     *            section names in the file to the supplied buffer.
     * @param lpKeyName
     *            The name of the key whose associated string is to be retrieved. If this parameter is {@code null}, all key names in the section specified by
     *            the {@code lpAppName} parameter are copied to the buffer specified by the {@code lpReturnedString} parameter.
     * @param lpDefault
     *            A default string. If the {@code lpKeyName} key cannot be found in the initialization file, {@link #GetPrivateProfileString} copies the default
     *            string to the {@code lpReturnedString} buffer. If this parameter is {@code null}, the default is an empty string, {@code ""}.
     *            <p>
     *            Avoid specifying a default string with trailing blank characters. The function inserts a {@code null} character in the
     *            {@code lpReturnedString} buffer to strip any trailing blanks.
     *            </p>
     * @param lpReturnedString
     *            A pointer to the buffer that receives the retrieved string.
     * @param nSize
     *            The size of the buffer pointed to by the {@code lpReturnedString} parameter, in characters.
     * @param lpFileName
     *            The name of the initialization file. If this parameter does not contain a full path to the file, the system searches for the file in the
     *            Windows directory.
     * @return The return value is the number of characters copied to the buffer, not including the terminating {@code null} character.
     *         <p>
     *         If neither {@code lpAppName} nor {@code lpKeyName} is {@code null} and the supplied destination buffer is too small to hold the requested string,
     *         the string is truncated and followed by a {@code null} character, and the return value is equal to {@code nSize} minus one.
     *         </p>
     *         <p>
     *         If either {@code lpAppName} or {@code lpKeyName} is {@code null} and the supplied destination buffer is too small to hold all the strings, the
     *         last string is truncated and followed by two {@code null} characters. In this case, the return value is equal to {@code nSize} minus two.
     *         </p>
     *         <p>
     *         In the event the initialization file specified by {@code lpFileName} is not found, or contains invalid values, this function will set errorno
     *         with a value of '0x2' (File Not Found). To retrieve extended error information, call {@link #GetLastError}.
     *         </p>
     */
    DWORD GetPrivateProfileString(String lpAppName, String lpKeyName, String lpDefault, char[] lpReturnedString, DWORD nSize, String lpFileName);

    /**
     * Copies a string into the specified section of an initialization file.
     *
     * If the file was created using Unicode characters, the function writes Unicode characters to the file. Otherwise, the function writes ANSI characters.
     *
     * @param lpAppName
     *            The name of the section to which the string will be copied. If the section does not exist, it is created. The name of the section is
     *            case-independent; the string can be any combination of uppercase and lowercase letters.
     * @param lpKeyName
     *            The name of the key to be associated with a string. If the key does not exist in the specified section, it is created. If this parameter is
     *            {@code null}, the entire section, including all entries within the section, is deleted.
     * @param lpString
     *            A string to be written to the file. If this parameter is {@code null}, the key pointed to by the {@code lpKeyName} parameter is deleted.
     * @param lpFileName
     *            The name of the initialization file.
     * @return If the function successfully copies the string to the initialization file, the return value is {@code true}.
     *         <p>
     *         If the function fails, or if it flushes the cached version of the most recently accessed initialization file, the return value is {@code false}.
     *         To get extended error information, call {@link #GetLastError}.
     *         </p>
     */
    boolean WritePrivateProfileString(String lpAppName, String lpKeyName, String lpString, String lpFileName);

    /**
     * Retrieves all the keys and values for the specified section of an initialization file.
     *
     * <p>
     * Each string has the following format: {@code key=string}.
     * </p>
     * <p>
     * This operation is atomic; no updates to the specified initialization file are allowed while the key name and value pairs for the section are being copied
     * to the buffer pointed to by the {@code lpReturnedString} parameter.
     * </p>
     *
     * @param lpAppName
     *            The name of the section in the initialization file.
     * @param lpReturnedString
     *            A buffer that receives the key name and value pairs associated with the named section. The buffer is filled with one or more {@code null}
     *            -terminated strings; the last string is followed by a second {@code null} character.
     * @param nSize
     *            The size of the buffer pointed to by the {@code lpReturnedString} parameter, in characters. The maximum profile section size is 32,767
     *            characters.
     * @param lpFileName
     *            The name of the initialization file. If this parameter does not contain a full path to the file, the system searches for the file in the
     *            Windows directory.
     * @return The number of characters copied to the buffer, not including the terminating null character. If the buffer is not large enough to contain all the
     *         key name and value pairs associated with the named section, the return value is equal to {@code nSize} minus two.
     */
    DWORD GetPrivateProfileSection(String lpAppName, char[] lpReturnedString, DWORD nSize, String lpFileName);

    /**
     * Retrieves the names of all sections in an initialization file.
     * <p>
     * This operation is atomic; no updates to the initialization file are allowed while the section names are being copied to the buffer.
     * </p>
     *
     * @param lpszReturnBuffer
     *            A pointer to a buffer that receives the section names associated with the named file. The buffer is filled with one or more {@code null}
     *            -terminated strings; the last string is followed by a second {@code null} character.
     * @param nSize
     *            size of the buffer pointed to by the {@code lpszReturnBuffer} parameter, in characters.
     * @param lpFileName
     *            The name of the initialization file. If this parameter is {@code NULL}, the function searches the Win.ini file. If this parameter does not
     *            contain a full path to the file, the system searches for the file in the Windows directory.
     * @return The return value specifies the number of characters copied to the specified buffer, not including the terminating {@code null} character. If the
     *         buffer is not large enough to contain all the section names associated with the specified initialization file, the return value is equal to the
     *         size specified by {@code nSize} minus two.
     */
    DWORD GetPrivateProfileSectionNames(char[] lpszReturnBuffer, DWORD nSize, String lpFileName);

    /**
     * @param lpAppName
     *            The name of the section in which data is written. This section name is typically the name of the calling application.
     * @param lpString
     *            The new key names and associated values that are to be written to the named section. This string is limited to 65,535 bytes. Must be filled
     *            with zero or many {@code null}-terminated strings of the form {@code key=value}, appended by an additional {@code null} byte to terminate the
     *            list.
     * @param lpFileName
     *            The name of the initialization file. If this parameter does not contain a full path for the file, the function searches the Windows directory
     *            for the file. If the file does not exist and lpFileName does not contain a full path, the function creates the file in the Windows directory.
     * @return If the function succeeds, the return value is nonzero. If the function fails, the return value is zero.
     */
    boolean WritePrivateProfileSection(String lpAppName, String lpString, String lpFileName);
}
