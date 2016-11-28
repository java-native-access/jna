/* Copyright (c) 2007, 2013 Timothy Wall, Markus Karg, All Rights Reserved
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

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Interface definitions for <code>kernel32.dll</code>. Includes additional
 * alternate mappings from {@link WinNT} which make use of NIO buffers,
 * {@link Wincon} for console API.
 */
public interface Kernel32 extends StdCallLibrary, WinNT, Wincon {

    /** The instance. */
    Kernel32 INSTANCE = Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * <strong>LOAD_LIBRARY_AS_DATAFILE</strong> <br>
     * 0x00000002<br>
     * If this value is used, the system maps the file into the calling
     * process's virtual address space as if it were a data file.<br>
     * Nothing is done to execute or prepare to execute the mapped file.<br>
     * Therefore, you cannot call functions like <a href=
     * "https://msdn.microsoft.com/en-us/library/windows/desktop/ms683197(v=vs.85).aspx">
     * <strong xmlns="http://www.w3.org/1999/xhtml">GetModuleFileName</strong>
     * </a>, <a href=
     * "https://msdn.microsoft.com/en-us/library/windows/desktop/ms683199(v=vs.85).aspx">
     * <strong xmlns="http://www.w3.org/1999/xhtml">GetModuleHandle</strong></a>
     * or <a href=
     * "https://msdn.microsoft.com/en-us/library/windows/desktop/ms683212(v=vs.85).aspx">
     * <strong xmlns="http://www.w3.org/1999/xhtml">GetProcAddress</strong></a>
     * with this DLL. Using this value causes writes to read-only memory to
     * raise an access violation.<br>
     * Use this flag when you want to load a DLL only to extract messages or
     * resources from it.<br>
     * This value can be used with
     * <strong>LOAD_LIBRARY_AS_IMAGE_RESOURCE</strong>. For more information,
     * see Remarks.
     */
    int LOAD_LIBRARY_AS_DATAFILE = 0x2;

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
    boolean ReadFile(HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToRead,
            IntByReference lpNumberOfBytesRead, WinBase.OVERLAPPED lpOverlapped);

    /**
     * Frees the specified local memory object and invalidates its handle.
     *
     * @param hMem
     *            A handle to the local memory object. If the <tt>hMem</tt> parameter
     *            is NULL, {@code LocalFree} ignores the parameter and returns NULL.
     * @return If the function succeeds, the return value is NULL. If the
     *         function fails, the return value is equal to a handle to the
     *         local memory object. To get extended error information, call
     *         {@code GetLastError}.
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366730(v=vs.85).aspx">LocalFree</A>
     */
    Pointer LocalFree(Pointer hMem);

    /**
     * Frees the specified global memory object and invalidates its handle.
     *
     * @param hGlobal
     *            A handle to the global memory object.
     * @return If the function succeeds, the return value is NULL If the
     *         function fails, the return value is equal to a handle to the
     *         global memory object. To get extended error information, call
     *         {@code GetLastError}.
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366579(v=vs.85).aspx">GlobalFree</A>
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
     *            Pointer to a {@link WinBase.SYSTEMTIME} structure to receive the current
     *            system date and time.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms724390(v=vs.85).aspx">GetSystemTime documentation</a>
     */
    void GetSystemTime(SYSTEMTIME lpSystemTime);

    /**
     * The SetSystemTime function modifies the current system date and time.
     * The system time is expressed in Coordinated Universal Time (UTC).
     *
     * @param lpSystemTime
     *            Pointer to a {@link WinBase.SYSTEMTIME} structure holding the new
     *            system date and time. <B>Note:</B> The {@code wDayOfWeek}
     *            member of the SYSTEMTIME structure is ignored.
     * @return {@code true} if the function succeeds, {@code false} otherwise.
     *         If the function fails, call {@link #GetLastError()} to get extended error
     *         information.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms724942(v=vs.85).aspx">SetSystemTime documentation</a>
     */
    boolean SetSystemTime(SYSTEMTIME lpSystemTime);

    /**
     * Retrieves the current local date and time.
     *
     * @param lpSystemTime
     *            A pointer to a {@link WinBase.SYSTEMTIME} structure to receive the current
     *            local date and time.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms724338(v=vs.85).aspx">GetLocalTime documentation</a>
     */
    void GetLocalTime(WinBase.SYSTEMTIME lpSystemTime);

    /**
     * Sets the current local time and date
     *
     * @param lpSystemTime
     *            Pointer to a {@link WinBase.SYSTEMTIME} structure holding the new
     *            system date and time. <B>Note:</B> The {@code wDayOfWeek}
     *            member of the SYSTEMTIME structure is ignored.
     * @return {@code true} if the function succeeds, {@code false} otherwise.
     *         If the function fails, call {@link #GetLastError()} to get extended error
     *         information.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms724936(v=vs.85).aspx">SetLocalTime documentation</a>
     */
    boolean SetLocalTime(SYSTEMTIME lpSystemTime);

    /**
     * Retrieves system timing information. On a multiprocessor system, the
     * values returned are the sum of the designated times across all
     * processors.
     *
     * @param lpIdleTime
     *            A pointer to a {@link WinBase.FILETIME} structure that
     *            receives the amount of time that the system has been idle.
     * @param lpKernelTime
     *            A pointer to a {@link WinBase.FILETIME} structure that
     *            receives the amount of time that the system has spent
     *            executing in Kernel mode (including all threads in all
     *            processes, on all processors). This time value also includes
     *            the amount of time the system has been idle.
     * @param lpUserTime
     *            A pointer to a {@link WinBase.FILETIME} structure that
     *            receives the amount of time that the system has spent
     *            executing in User mode (including all threads in all
     *            processes, on all processors).
     * @return {@code true} if the function succeeds, {@code false} otherwise.
     *         If the function fails, call {@link #GetLastError()} to get extended error
     *         information.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms724400(v=vs.85).aspx">GetSystemTimes documentation</a>
     */
    boolean GetSystemTimes(WinBase.FILETIME lpIdleTime, WinBase.FILETIME lpKernelTime, WinBase.FILETIME lpUserTime);

    /**
     * The GetTickCount function retrieves the number of milliseconds that have
     * elapsed since the system was started, up to 49.7 days.
     *
     * @return Number of milliseconds that have elapsed since the system was
     *         started.
     */
    int GetTickCount();

    /**
     * The GetTickCount64 function retrieves the number of milliseconds that 
     * have elapsed since the system was started.
     *
     * @return Number of milliseconds that have elapsed since the system was
     *         started.
     */
    long GetTickCount64();
  
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
     * Determines whether a disk drive is a removable, fixed, CD-ROM, RAM
     * disk, or network drive.
     *
     * @param lpRootPathName
     *            Pointer to a null-terminated string that specifies the root
     *            directory of the disk to return information about. A trailing
     *            backslash is required. If this parameter is NULL, the function
     *            uses the root of the current directory.
     * @return The return value specifies the type of drive.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364939(v=vs.85).aspx">GetDriveType</a>
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
     *            <p>
     *            This version of the function assumes
     *            FORMAT_MESSAGE_ALLOCATE_BUFFER is set.</p>
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
     * Closes an open object handle.
     *
     * @param hObject
     *            Handle to an open object. This parameter can be a pseudo
     *            handle or INVALID_HANDLE_VALUE.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call {@code GetLastError}.
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/ms724211(v=vs.85).aspx">CloseHandle</A>
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
    Pointer LocalAlloc(int /* UINT */uFlags, int /* SIZE_T */uBytes);

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
     * Flushes the buffers of a specified file and causes all buffered data
     * to be written to a file.
     * @param hFile A handle to the open file. If a handle to a communications
     * device, the function only flushes the transmit buffer. If a handle to the
     * server end of a named pipe, the function does not return until the client
     * has read all buffered data from the pipe.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364439(v=vs.85).aspx">FlushFileBuffers documentation</a>
     */
    boolean FlushFileBuffers(HANDLE hFile);

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
     * Resets (to non-signaled state) the specified event object.
     *
     * @param hEvent
     *            A handle to the event object
     *
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean ResetEvent(HANDLE hEvent);

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
     * Retrieves a NetBIOS or DNS name associated with the local computer,
     * according to the <code>nameType</code> enumeration value
     *
     * @param nameType
     *            An enumeration value specifying the type of name to be
     *            retrieved - this parameter is a value from the
     *            COMPUTER_NAME_FORMAT in the WinBase definitions
     * @param buffer
     *            A pointer to a buffer that receives the computer name or the
     *            cluster virtual server name. The length of the name may
     *            be greater than MAX_COMPUTERNAME_LENGTH characters because DNS
     *            allows longer names. To ensure that this buffer is large enough,
     *            set this parameter to NULL and use the required buffer size
     *            returned in the lpnSize parameter.
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
    boolean GetComputerNameEx(int nameType, char[] buffer, IntByReference lpnSize);

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
     *            The command line to be executed. The maximum length of this
     *            string is 32,768 characters, including the Unicode terminating
     *            null character. If <i>lpApplicationName</i> is NULL, the
     *            module name portion of <i>lpCommandLine</i> is limited to
     *            MAX_PATH characters.
     *            <p>
     *            The Unicode version of this function, {@link #CreateProcessW},
     *            can modify the contents of this string. Therefore, this
     *            parameter cannot be a pointer to read-only memory (such as a
     *            const variable or a literal string). If this parameter is a
     *            constant string, the function may cause an access violation.
     *            </p><p>
     *            The <i>lpCommandLine</i> parameter can be NULL. In that case,
     *            the function uses the string pointed to by
     *            <i>lpApplicationName</i> as the command line.
     *            </p><p>
     *            If both <i>lpApplicationName</i> and <i>lpCommandLine</i> are
     *            non-NULL, the null-terminated string pointed to by
     *            <i>lpApplicationName</i> specifies the module to execute, and
     *            the null-terminated string pointed to by <i>lpCommandLine</i>
     *            specifies the command line. The new process can use
     *            GetCommandLine to retrieve the entire command line. Console
     *            processes written in C can use the argc and argv arguments to
     *            parse the command line. Because argv[0] is the module name, C
     *            programmers generally repeat the module name as the first
     *            token in the command line.
     *            </p>
     *            If <i>lpApplicationName</i> is NULL, the first white
     *            space-delimited token of the command line specifies the module
     *            name. If you are using a long file name that contains a space,
     *            use quoted strings to indicate where the file name ends and
     *            the arguments begin (see the explanation for the
     *            <i>lpApplicationName</i> parameter). If the file name does not
     *            contain an extension, .exe is appended. Therefore, if the file
     *            name extension is .com, this parameter must include the .com
     *            extension. If the file name ends in a period (.) with no
     *            extension, or if the file name contains a path, .exe is not
     *            appended. If the file name does not contain a directory path,
     *            the system searches for the executable file in the following
     *            sequence:
     *            <ul>
     *            <li>The directory from which the application loaded.
     *            <li>The current directory for the parent process.
     *            <li>The 32-bit Windows system directory. Use the
     *            GetSystemDirectory function to get the path of this directory.
     *            <li>The 16-bit Windows system directory. There is no function
     *            that obtains the path of this directory, but it is searched.
     *            The name of this directory is System.
     *            <li>The Windows directory. Use the GetWindowsDirectory
     *            function to get the path of this directory.
     *            <li>The directories that are listed in the PATH environment
     *            variable. Note that this function does not search the
     *            per-application path specified by the App Paths registry key.
     *            To include this per-application path in the search sequence,
     *            use the ShellExecute function.
     *            </ul>
     *            The system adds a terminating null character to the
     *            command-line string to separate the file name from the
     *            arguments. This divides the original string into two strings
     *            for internal processing.
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
     * @param fdwAccess The access to the process object. This access right is
     * checked against the security descriptor for the process. This parameter
     * can be one or more of the process access rights.
     * <p>
     * If the caller has enabled the SeDebugPrivilege privilege, the requested
     * access is granted regardless of the contents of the security
     * descriptor.</p>
     * 
     * @param fInherit If this value is TRUE, processes created by this process will inherit the handle. Otherwise, the processes do not inherit this handle.
     * 
     * @param IDProcess
     *            Specifies the process identifier of the process to open.
     * @return An open handle to the specified process indicates success. NULL
     *         indicates failure. To get extended error information, call
     *         GetLastError.
     */
    HANDLE OpenProcess(int fdwAccess, boolean fInherit, int IDProcess);

    /**
     * This function retrieves the full path of the executable file of a given process.
     *
     * @param hProcess
     *          Handle for the running process
     * @param dwFlags
     *          0 - The name should use the Win32 path format.
     *          1(WinNT.PROCESS_NAME_NATIVE) - The name should use the native system path format.
     * @param lpExeName
     *          pre-allocated character buffer for the returned path
     * @param lpdwSize
     *          input: the size of the allocated buffer
     *          output: the length of the returned path in characters
     *
     * @return true if successful false if not. To get extended error information,
     *         call GetLastError.
     */
    boolean QueryFullProcessImageName(HANDLE hProcess, int dwFlags, char[] lpExeName, IntByReference lpdwSize);


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
     * @param buffer
     *            a buffer which receives an array of
     *            {@link WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION} structures.
     * @param returnLength
     *            on input, specifies the length of the buffer in bytes. On
     *            output, receives the number of bytes actually returned, or if
     *            {@link #GetLastError()} returned
     *            {@link WinError#ERROR_INSUFFICIENT_BUFFER}, the number of
     *            bytes wanted for the call to work.
     * @return {@code true} on success, {@code false} on failure. To get
     *         extended error information, call {@link #GetLastError()}.
     */
    boolean GetLogicalProcessorInformation(Pointer buffer,
            DWORDByReference returnLength);

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
     * Retrieves file information for the specified file.
     * To set file information using a file handle, see SetFileInformationByHandle.
     * @param hFile
     * 				A handle to the file that contains the information to be retrieved.
     * @param FileInformationClass
     * 				A FILE_INFO_BY_HANDLE_CLASS enumeration value that specifies the type of
     * 				information to be retrieved.
     * @param lpFileInformation
     * 				A pointer to the buffer that receives the requested file information.
     * 				The structure that is returned corresponds to the class that is specified
     * 				by FileInformationClass.
     * @param dwBufferSize
     * 				The size of the lpFileInformation buffer, in bytes.
     * @return If the function succeeds, the return value is nonzero and file information
     * 		   data is contained in the buffer pointed to by the lpFileInformation parameter.
     *         If the function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean GetFileInformationByHandleEx(HANDLE hFile, int FileInformationClass, Pointer lpFileInformation, DWORD dwBufferSize);

    /**
     * Sets the file information for the specified file. To retrieve file information using a
     * file handle, see GetFileInformationByHandleEx.
     * @param hFile
     * 				A handle to the file for which to change information.
     * 				This handle must be opened with the appropriate permissions for the
     * 				requested change. This handle should not be a pipe handle.
     * @param FileInformationClass
     * 				A FILE_INFO_BY_HANDLE_CLASS enumeration value that specifies the type of information to be changed.
     * 				Valid values are FILE_BASIC_INFO, FILE_RENAME_INFO, FILE_DISPOSITION_INFO, FILE_ALLOCATION_INFO,
     * 				FILE_END_OF_FILE_INFO, and FILE_IO_PRIORITY_HINT_INFO
     * @param lpFileInformation
     * 				A pointer to the buffer that contains the information to change for the specified file
     * 				information class. The structure that this parameter points to corresponds to the class
     * 				that is specified by FileInformationClass.
     * @param dwBufferSize
     * 				The size of the lpFileInformation buffer, in bytes.
     * @return Returns nonzero if successful or zero otherwise. To get extended error information, call GetLastError.
     */
    boolean SetFileInformationByHandle(HANDLE hFile, int FileInformationClass, Pointer lpFileInformation, DWORD dwBufferSize);

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
    boolean GetFileTime(HANDLE hFile, WinBase.FILETIME lpCreationTime,
            WinBase.FILETIME lpLastAccessTime, WinBase.FILETIME lpLastWriteTime);

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
     * Retrieves information about the specified disk, including the amount of
     * free space on the disk
     *
     * @param lpRootPathName
     *          The root directory of the disk for which information is to be
     *          returned. If this parameter is NULL, the function uses the root
     *          of the current disk. If this parameter is a UNC name, it must
     *          include a trailing backslash (for example, &quot;\\MyServer\MyShare\&quot;).
     *          Furthermore, a drive specification must have a trailing backslash
     *          (for example, &quot;C:\&quot;). The calling application must
     *          have FILE_LIST_DIRECTORY access rights for this directory.
     * @param lpSectorsPerCluster
     *          A variable that receives the number of sectors per cluster.
     * @param lpBytesPerSector
     *          A variable that receives the number of bytes per sector.
     * @param lpNumberOfFreeClusters
     *          A variable that receives the total number of free clusters on the
     *          disk that are available to the user who is associated with the
     *          calling thread. If per-user disk quotas are in use, this value
     *          may be less than the total number of free clusters on the disk.
     * @param lpTotalNumberOfClusters
     *           A variable that receives the total number of clusters on the
     *           disk that are available to the user who is associated with the
     *           calling thread. If per-user disk quotas are in use, this value
     *           may be less than the total number of clusters on the disk.
     * @return {@code true} if the function succeeds - to get extended error
     * information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364935(v=vs.85).aspx">GetDiskFreeSpace</a>
     */
    boolean GetDiskFreeSpace(String lpRootPathName,
            DWORDByReference lpSectorsPerCluster,
            DWORDByReference lpBytesPerSector,
            DWORDByReference lpNumberOfFreeClusters,
            DWORDByReference lpTotalNumberOfClusters);

    /**
     * The GetDiskFreeSpaceEx function retrieves information about the amount of
     * space that is available on a disk volume, which is the total amount of
     * space, the total amount of free space, and the total amount of free space
     * available to the user that is associated with the calling thread.
     *
     * @param lpDirectoryName
     *            A string that specifies a directory on a disk. If this parameter
     *            is NULL, the function uses the root of the current disk. If this
     *            parameter is a UNC name, it must include a trailing backslash,
     *            for example,
     *            {@code \\MyServer\MyShare\}. This parameter does not have to specify
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
     * @return {@code true} if the function succeeds - to get extended error
     * information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364937(v=vs.85).aspx">GetDiskFreeSpaceEx</a>
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
     * Connects to a message-type pipe (and waits if an instance of the pipe is
     * not available), writes to and reads from the pipe, and then closes the pipe.
     * @param lpNamedPipeName The pipe name.
     * @param lpInBuffer The data to be written to the pipe.
     * @param nInBufferSize The size of the write buffer, in bytes.
     * @param lpOutBuffer The buffer that receives the data read from the pipe.
     * @param nOutBufferSize The size of the read buffer, in bytes.
     * @param lpBytesRead A variable that receives the number of bytes read from
     * the pipe.
     * @param nTimeOut The number of milliseconds to wait for the named pipe to
     * be available.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365144(v=vs.85).aspx">CallNamedPipe documentation</a>
     */
    public boolean CallNamedPipe(String lpNamedPipeName,
                          byte[] lpInBuffer, int nInBufferSize,
                          byte[] lpOutBuffer, int nOutBufferSize,
                          IntByReference lpBytesRead, int nTimeOut);

    /**
     * Enables a named pipe server process to wait for a client process to connect
     * to an instance of a named pipe
     * @param hNamedPipe A handle to the server end of a named pipe instance.
     * @param lpOverlapped A pointer to an {@link WinBase.OVERLAPPED} structure.
     * @return <P>If the operation is synchronous, does not return until the operation
     * has completed. If the function succeeds, the return value is {@code true}. If
     * the function fails, the return value is {@code false}. To get extended error
     * information, call {@link #GetLastError()}.</P>
     * <P>If the operation is asynchronous, returns immediately. If the operation is
     * still pending, the return value is {@code false} and {@link #GetLastError()}
     * returns {@link #ERROR_IO_PENDING}.</P>
     * <P>If a client connects before the function is called, the function returns
     * {@code false} and {@link #GetLastError()} returns {@link #ERROR_PIPE_CONNECTED}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365146(v=vs.85).aspx">ConnectNamedPipe documentation</a>
     */
    public boolean ConnectNamedPipe(HANDLE hNamedPipe, OVERLAPPED lpOverlapped);

    int MAX_PIPE_NAME_LENGTH=256;

    /**
     * @param lpName The unique pipe name. This string must have the following form:
     * <P>
     * <code>
     *        \\.\pipe\pipename
     * </code>
     * </P>
     * <P>The <I>pipename</I> part of the name can include any character other than a backslash,
     * including numbers and special characters. The entire pipe name string can be up to
     * {@link #MAX_PIPE_NAME_LENGTH} characters long. Pipe names are not case sensitive.</P>
     * @param dwOpenMode The open mode. The function fails if specifies anything other than
     * 0 or the allowed flags
     * @param dwPipeMode The pipe mode. The function fails if specifies anything other than
     * 0 or the allowed flags
     * @param nMaxInstances The maximum number of instances that can be created for this pipe.
     * Acceptable values are in the range 1 through {@link #PIPE_UNLIMITED_INSTANCES}
     * @param nOutBufferSize The number of bytes to reserve for the output buffer.
     * @param nInBufferSize The number of bytes to reserve for the input buffer.
     * @param nDefaultTimeOut The default time-out value, in milliseconds. A value of zero will
     * result in a default time-out of 50 milliseconds
     * @param lpSecurityAttributes A pointer to a {@link WinBase.SECURITY_ATTRIBUTES} structure that
     * specifies a security descriptor for the new named pipe. If {@code null} the named pipe
     * gets a default security descriptor and the handle cannot be inherited.
     * @return If the function succeeds, the return value is a handle to the server end of a
     * named pipe instance. If the function fails, the return value is {@link #INVALID_HANDLE_VALUE}.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365150(v=vs.85).aspx">CreateNamedPipe documentation</a>
     */
    public HANDLE CreateNamedPipe(String lpName, int dwOpenMode, int dwPipeMode, int nMaxInstances,
                                  int nOutBufferSize, int nInBufferSize, int nDefaultTimeOut,
                                  SECURITY_ATTRIBUTES lpSecurityAttributes);

    /**
     * Disconnects the server end of a named pipe instance from a client process.
     * @param hNamedPipe A handle to an instance of a named pipe.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365166(v=vs.85).aspx">DisconnectNamedPipe documentation</a>
     */
    public boolean DisconnectNamedPipe(HANDLE hNamedPipe);

    /**
     * Retrieves the client computer name for the specified named pipe.
     * @param Pipe A handle to an instance of a named pipe.
     * @param ClientComputerName The buffer to receive the computer name.
     * <B>Note:</B> use {@link Native#toString(char[])} to convert it
     * to a {@link String}
     * @param ClientComputerNameLength The size of the <tt>ClientComputerName</tt>
     * buffer, in bytes.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365437(v=vs.85).aspx">GetNamedPipeClientComputerName documentation</a>
     */
    public boolean GetNamedPipeClientComputerName(HANDLE Pipe, char[] ClientComputerName, int ClientComputerNameLength);

    /**
     * @param Pipe A handle to an instance of a named pipe.
     * @param ClientProcessId Recipient of the process identifier
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365440(v=vs.85).aspx">GetNamedPipeClientProcessId documentation</a>
     */
    public boolean GetNamedPipeClientProcessId(HANDLE Pipe, ULONGByReference ClientProcessId);

    /**
     * @param Pipe A handle to an instance of a named pipe.
     * @param ClientSessionId Recipient of the session identifier
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365442(v=vs.85).aspx">GetNamedPipeClientProcessId documentation</a>
     */
    public boolean GetNamedPipeClientSessionId(HANDLE Pipe, ULONGByReference ClientSessionId);

    /**
     * Retrieves information about a specified named pipe.
     * @param hNamedPipe A handle to the named pipe for which information is wanted.
     * @param lpState A pointer to a variable that indicates the current
     * state of the handle. This parameter can be {@code null} if this information
     * is not needed.
     * @param lpCurInstances A pointer to a variable that receives the number
     * of current pipe instances. This parameter can be {@code null} if this
     * information is not needed.
     * @param lpMaxCollectionCount A pointer to a variable that receives the
     * maximum number of bytes to be collected on the client's computer before
     * transmission to the server. This parameter can be {@code null} if this
     * information is not needed.
     * @param lpCollectDataTimeout A pointer to a variable that receives the
     * maximum time, in milliseconds, that can pass before a remote named pipe
     * transfers information over the network. This parameter can be {@code null}
     * if this information is not needed.
     * @param lpUserName A buffer that receives the user name string associated
     * with the client application.  This parameter can be {@code null} if this
     * information is not needed.
     * @param nMaxUserNameSize The size of the buffer specified by the <tt>lpUserName</tt>
     * parameter. This parameter is ignored if <tt>lpUserName</tt> is {@code null}.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365443(v=vs.85).aspx">GetNamedPipeHandleState documentation</a>
     */
    public boolean GetNamedPipeHandleState(HANDLE hNamedPipe, IntByReference lpState,
              IntByReference lpCurInstances, IntByReference lpMaxCollectionCount,
              IntByReference lpCollectDataTimeout, char[] lpUserName, int nMaxUserNameSize);

    /**
     * Retrieves information about the specified named pipe.
     * @param hNamedPipe A handle to the named pipe instance.
     * @param lpFlags A pointer to a variable that receives the type of the
     * named pipe. This parameter can be {@code null} if this information is
     * not needed.
     * @param lpOutBufferSize A pointer to a variable that receives the size
     * of the buffer for outgoing data, in bytes. This parameter can be
     * {@code null} if this information is not needed.
     * @param lpInBufferSize A pointer to a variable that receives the size of
     * the buffer for incoming data, in bytes. This parameter can be {@code null}
     * if this information is not needed.
     * @param lpMaxInstances A pointer to a variable that receives the maximum
     * number of pipe instances that can be created. This parameter can be {@code null}
     * if this information is not needed.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365445(v=vs.85).aspx">GetNamedPipeInfo documentation</a>
     */
    public boolean GetNamedPipeInfo(HANDLE hNamedPipe, IntByReference lpFlags,
              IntByReference lpOutBufferSize, IntByReference lpInBufferSize,
              IntByReference lpMaxInstances);

    /**
     * @param Pipe A handle to an instance of a named pipe.
     * @param ServerProcessId Recipient of the process identifier.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365446(v=vs.85).aspx">GetNamedPipeServerProcessId documentation</a>
     */
    public boolean GetNamedPipeServerProcessId(HANDLE Pipe, ULONGByReference ServerProcessId);

    /**
     * @param Pipe A handle to an instance of a named pipe.
     * @param ServerSessionId session identifier.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365569(v=vs.85).aspx">GetNamedPipeServerSessionId documentation</a>
     */
    public boolean GetNamedPipeServerSessionId(HANDLE Pipe, ULONGByReference ServerSessionId);

    /**
     * Copies data from a named or anonymous pipe into a buffer without
     * removing it from the pipe.
     * @param hNamedPipe A handle to the pipe.
     * @param lpBuffer A buffer that receives data read from the pipe.
     * This parameter can be {@code null} if no data is to be read.
     * @param nBufferSize The size of the buffer specified by the
     * <tt>lpBuffer parameter</tt>, in bytes. This parameter is ignored if
     * <tt>lpBuffer</tt> is {@code null}.
     * @param lpBytesRead A variable that receives the number of bytes read
     * from the pipe. This parameter can be {@code null} if no data is to be read.
     * @param lpTotalBytesAvail A variable that receives the total number of
     * bytes available to be read from the pipe. This parameter can be {@code null}
     * if no data is to be read.
     * @param lpBytesLeftThisMessage A variable that receives the number of
     * bytes remaining in this message. This parameter can be {@code null}
     * if no data is to be read.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365779(v=vs.85).aspx">PeekNamedPipe documentation</a>
     */
    public boolean PeekNamedPipe(HANDLE hNamedPipe, byte[] lpBuffer, int nBufferSize,
              IntByReference lpBytesRead,IntByReference lpTotalBytesAvail, IntByReference lpBytesLeftThisMessage);

    /**
     * Sets the read mode and the blocking mode of the specified named pipe.
     * @param hNamedPipe A handle to the named pipe instance.
     * @param lpMode The new pipe mode. The mode is a combination of a read-mode
     * flag and a wait-mode flag. This parameter can be {@code null} if the
     * mode is not being set.
     * @param lpMaxCollectionCount The maximum number of bytes collected on
     * the client computer before transmission to the server. This parameter
     * can be {@code null} if the count is not being set.
     * @param lpCollectDataTimeout The maximum time, in milliseconds, that can
     * pass before a remote named pipe transfers information over the network.
     * This parameter can be {@code null} if the timeout is not being set.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365787(v=vs.85).aspx">SetNamedPipeHandleState documentation</a>
     */
    public boolean SetNamedPipeHandleState(HANDLE hNamedPipe, IntByReference lpMode,
              IntByReference lpMaxCollectionCount, IntByReference lpCollectDataTimeout);

    /**
     * Combines the functions that write a message to and read a message from
     * the specified named pipe into a single network operation.
     * @param hNamedPipe A handle to the named pipe
     * @param lpInBuffer The buffer containing the data to be written to the pipe.
     * @param nInBufferSize The size of the input buffer, in bytes.
     * @param lpOutBuffer The buffer that receives the data read from the pipe.
     * @param nOutBufferSize The size of the output buffer, in bytes.
     * @param lpBytesRead Variable that receives the number of bytes read from the pipe.
     * @param lpOverlapped A pointer to an {@link WinBase.OVERLAPPED} structure. Can
     * be {@code null} if pipe not opened with {@link #FILE_FLAG_OVERLAPPED}.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365790(v=vs.85).aspx">TransactNamedPipe documentation</a>
     */
    public boolean TransactNamedPipe(HANDLE hNamedPipe,
                      byte[] lpInBuffer, int nInBufferSize,
                      byte[] lpOutBuffer, int nOutBufferSize,
                      IntByReference lpBytesRead, OVERLAPPED lpOverlapped);

    /**
     * Waits until either a time-out interval elapses or an instance of the
     * specified named pipe is available for connection - i.e., the pipe's
     * server process has a pending {@link #ConnectNamedPipe}
     * operation on the pipe.
     * @param lpNamedPipeName The name of the named pipe. The string must
     * include the name of the computer on which the server process is executing.
     * The following pipe name format is used:
     * <P><code>
     *         \\servername\pipe\pipename
     * </code></P>
     * <P>A period may be used for the <I>servername</I> if the pipe is local.</P>
     * @param nTimeOut The number of milliseconds that the function will wait for
     * an instance of the named pipe to be available.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365800(v=vs.85).aspx">WaitNamedPipe documentation</a>
     */
    public boolean WaitNamedPipe(String lpNamedPipeName, int nTimeOut);

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
    boolean Process32First(HANDLE hSnapshot, Tlhelp32.PROCESSENTRY32 lppe);

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
    boolean Process32Next(HANDLE hSnapshot, Tlhelp32.PROCESSENTRY32 lppe);

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
     * <P>Retrieves the environment variables for the current process. The
     * block of variables format is as follows:</P>
     * <p><code>
     *      Var1=Value1\0
     *      Var2=Value2\0
     *      Var3=Value3\0
     *      ...
     *      VarN=ValueN\0\0
     * </code></p>
     * @return If the function succeeds, the return value is a {@link Pointer}.
     * to the environment block of the current process. If fails, then
     * {@code null} is returned. When the data is no longer needed the memory
     * block must be released using {@link #FreeEnvironmentStrings(Pointer)}
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms683187(v=vs.85).aspx">GetEnvironmentStrings documentation</a>
     */
    Pointer GetEnvironmentStrings();

    /**
     * @param lpszEnvironmentBlock A pointer to a block of environment strings
     * obtained by calling the {@link #GetEnvironmentStrings()} function
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms683151(v=vs.85).aspx">FreeEnvironmentStrings documentation</a>
     */
    boolean FreeEnvironmentStrings(Pointer lpszEnvironmentBlock);

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
     * Retrieves an integer associated with a key in the specified section of an
     * initialization file.
     *
     * @param appName
     *            The name of the section in the initialization file.
     * @param keyName
     *            The name of the key whose value is to be retrieved. This value
     *            is in the form of a string; the {@link #GetPrivateProfileInt}
     *            function converts the string into an integer and returns the
     *            integer.
     * @param defaultValue
     *            The default value to return if the key name cannot be found in
     *            the initialization file.
     * @param fileName
     *            The name of the initialization file. If this parameter does
     *            not contain a full path to the file, the system searches for
     *            the file in the Windows directory.
     * @return The return value is the integer equivalent of the string
     *         following the specified key name in the specified initialization
     *         file. If the key is not found, the return value is the specified
     *         default value.
     */
    int GetPrivateProfileInt(String appName, String keyName, int defaultValue,
            String fileName);

    /**
     * Retrieves a string from the specified section in an initialization file.
     *
     * @param lpAppName
     *            The name of the section containing the key name. If this
     *            parameter is {@code null}, the
     *            {@link #GetPrivateProfileString} function copies all section
     *            names in the file to the supplied buffer.
     * @param lpKeyName
     *            The name of the key whose associated string is to be
     *            retrieved. If this parameter is {@code null}, all key names in
     *            the section specified by the {@code lpAppName} parameter are
     *            copied to the buffer specified by the {@code lpReturnedString}
     *            parameter.
     * @param lpDefault
     *            A default string. If the {@code lpKeyName} key cannot be found
     *            in the initialization file, {@link #GetPrivateProfileString}
     *            copies the default string to the {@code lpReturnedString}
     *            buffer. If this parameter is {@code null}, the default is an
     *            empty string, {@code ""}.
     *            <p>
     *            Avoid specifying a default string with trailing blank
     *            characters. The function inserts a {@code null} character in
     *            the {@code lpReturnedString} buffer to strip any trailing
     *            blanks.
     *            </p>
     * @param lpReturnedString
     *            A pointer to the buffer that receives the retrieved string.
     * @param nSize
     *            The size of the buffer pointed to by the
     *            {@code lpReturnedString} parameter, in characters.
     * @param lpFileName
     *            The name of the initialization file. If this parameter does
     *            not contain a full path to the file, the system searches for
     *            the file in the Windows directory.
     * @return The return value is the number of characters copied to the
     *         buffer, not including the terminating {@code null} character.
     *         <p>
     *         If neither {@code lpAppName} nor {@code lpKeyName} is
     *         {@code null} and the supplied destination buffer is too small to
     *         hold the requested string, the string is truncated and followed
     *         by a {@code null} character, and the return value is equal to
     *         {@code nSize} minus one.
     *         </p>
     *         <p>
     *         If either {@code lpAppName} or {@code lpKeyName} is {@code null}
     *         and the supplied destination buffer is too small to hold all the
     *         strings, the last string is truncated and followed by two
     *         {@code null} characters. In this case, the return value is equal
     *         to {@code nSize} minus two.
     *         </p>
     *         <p>
     *         In the event the initialization file specified by
     *         {@code lpFileName} is not found, or contains invalid values, this
     *         function will set errorno with a value of '0x2' (File Not Found).
     *         To retrieve extended error information, call
     *         {@link #GetLastError}.
     *         </p>
     */
    DWORD GetPrivateProfileString(String lpAppName, String lpKeyName,
            String lpDefault, char[] lpReturnedString, DWORD nSize,
            String lpFileName);

    /**
     * Copies a string into the specified section of an initialization file.
     *
     * If the file was created using Unicode characters, the function writes
     * Unicode characters to the file. Otherwise, the function writes ANSI
     * characters.
     *
     * @param lpAppName
     *            The name of the section to which the string will be copied. If
     *            the section does not exist, it is created. The name of the
     *            section is case-independent; the string can be any combination
     *            of uppercase and lowercase letters.
     * @param lpKeyName
     *            The name of the key to be associated with a string. If the key
     *            does not exist in the specified section, it is created. If
     *            this parameter is {@code null}, the entire section, including
     *            all entries within the section, is deleted.
     * @param lpString
     *            A string to be written to the file. If this parameter is
     *            {@code null}, the key pointed to by the {@code lpKeyName}
     *            parameter is deleted.
     * @param lpFileName
     *            The name of the initialization file.
     * @return If the function successfully copies the string to the
     *         initialization file, the return value is {@code true}.
     *         <p>
     *         If the function fails, or if it flushes the cached version of the
     *         most recently accessed initialization file, the return value is
     *         {@code false}. To get extended error information, call
     *         {@link #GetLastError}.
     *         </p>
     */
    boolean WritePrivateProfileString(String lpAppName, String lpKeyName,
            String lpString, String lpFileName);

    /**
     * Retrieves all the keys and values for the specified section of an
     * initialization file.
     *
     * <p>
     * Each string has the following format: {@code key=string}.
     * </p>
     * <p>
     * This operation is atomic; no updates to the specified initialization file
     * are allowed while the key name and value pairs for the section are being
     * copied to the buffer pointed to by the {@code lpReturnedString}
     * parameter.
     * </p>
     *
     * @param lpAppName
     *            The name of the section in the initialization file.
     * @param lpReturnedString
     *            A buffer that receives the key name and value pairs associated
     *            with the named section. The buffer is filled with one or more
     *            {@code null} -terminated strings; the last string is followed
     *            by a second {@code null} character.
     * @param nSize
     *            The size of the buffer pointed to by the
     *            {@code lpReturnedString} parameter, in characters. The maximum
     *            profile section size is 32,767 characters.
     * @param lpFileName
     *            The name of the initialization file. If this parameter does
     *            not contain a full path to the file, the system searches for
     *            the file in the Windows directory.
     * @return The number of characters copied to the buffer, not including the
     *         terminating null character. If the buffer is not large enough to
     *         contain all the key name and value pairs associated with the
     *         named section, the return value is equal to {@code nSize} minus
     *         two.
     */
    DWORD GetPrivateProfileSection(String lpAppName, char[] lpReturnedString,
            DWORD nSize, String lpFileName);

    /**
     * Retrieves the names of all sections in an initialization file.
     * <p>
     * This operation is atomic; no updates to the initialization file are
     * allowed while the section names are being copied to the buffer.
     * </p>
     *
     * @param lpszReturnBuffer
     *            A pointer to a buffer that receives the section names
     *            associated with the named file. The buffer is filled with one
     *            or more {@code null} -terminated strings; the last string is
     *            followed by a second {@code null} character.
     * @param nSize
     *            size of the buffer pointed to by the {@code lpszReturnBuffer}
     *            parameter, in characters.
     * @param lpFileName
     *            The name of the initialization file. If this parameter is
     *            {@code NULL}, the function searches the Win.ini file. If this
     *            parameter does not contain a full path to the file, the system
     *            searches for the file in the Windows directory.
     * @return The return value specifies the number of characters copied to the
     *         specified buffer, not including the terminating {@code null}
     *         character. If the buffer is not large enough to contain all the
     *         section names associated with the specified initialization file,
     *         the return value is equal to the size specified by {@code nSize}
     *         minus two.
     */
    DWORD GetPrivateProfileSectionNames(char[] lpszReturnBuffer, DWORD nSize,
            String lpFileName);

    /**
     * @param lpAppName
     *            The name of the section in which data is written. This section
     *            name is typically the name of the calling application.
     * @param lpString
     *            The new key names and associated values that are to be written
     *            to the named section. This string is limited to 65,535 bytes.
     *            Must be filled with zero or many {@code null}-terminated
     *            strings of the form {@code key=value}, appended by an
     *            additional {@code null} byte to terminate the list.
     * @param lpFileName
     *            The name of the initialization file. If this parameter does
     *            not contain a full path for the file, the function searches
     *            the Windows directory for the file. If the file does not exist
     *            and lpFileName does not contain a full path, the function
     *            creates the file in the Windows directory.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero.
     */
    boolean WritePrivateProfileSection(String lpAppName, String lpString,
            String lpFileName);

    /**
     * Converts a file time to a local file time.
     *
     * @param lpFileTime
     *            [in] A pointer to a FILETIME structure containing the
     *            UTC-based file time to be converted into a local file time.
     * @param lpLocalFileTime
     *            [out] A pointer to a FILETIME structure to receive the
     *            converted local file time. This parameter cannot be the same
     *            as the lpFileTime parameter.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean FileTimeToLocalFileTime(FILETIME lpFileTime,
            FILETIME lpLocalFileTime);

    /**
     * Converts a time in Coordinated Universal Time (UTC) to a specified time
     * zone's corresponding local time.
     *
     * @param lpTimeZone
     *            [in, optional] A pointer to a TIME_ZONE_INFORMATION structure
     *            that specifies the time zone of interest. If lpTimeZone is
     *            NULL, the function uses the currently active time zone.
     * @param lpUniversalTime
     *            [in] A pointer to a SYSTEMTIME structure that specifies the
     *            UTC time to be converted. The function converts this universal
     *            time to the specified time zone's corresponding local time.
     * @param lpLocalTime [out] A pointer to a SYSTEMTIME structure that
     *         receives the local time.
     * @return status
     */
    boolean SystemTimeToTzSpecificLocalTime(TIME_ZONE_INFORMATION lpTimeZone,
                                            SYSTEMTIME lpUniversalTime, SYSTEMTIME lpLocalTime);

    /**
     * Converts a system time to file time format. System time is based on
     * Coordinated Universal Time (UTC).
     *
     * @param lpSystemTime
     *            [in] A pointer to a SYSTEMTIME structure that contains the
     *            system time to be converted from UTC to file time format.
     * @param lpFileTime
     *            [out] A pointer to a FILETIME structure to receive the
     *            converted system time.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call GetLastError.
     */
    boolean SystemTimeToFileTime(SYSTEMTIME lpSystemTime, FILETIME lpFileTime);

    /**
     * Converts a file time to system time format. System time is based on Coordinated Universal Time (UTC).
     * @param lpFileTime
     * 				[in] A pointer to a FILETIME structure containing the file time to be converted to system (UTC) date and time format.
					This value must be less than 0x8000000000000000. Otherwise, the function fails.
     * @param lpSystemTime
     * 				A pointer to a SYSTEMTIME structure to receive the converted file time.
     * @return If the function succeeds, the return value is nonzero. If the function fails, the return value is zero.
     * 				To get extended error information, call GetLastError.
     */
    boolean FileTimeToSystemTime(FILETIME lpFileTime, SYSTEMTIME lpSystemTime);

    /**
     * Creates a thread that runs in the virtual address space of another process.
     *
     * @param hProcess A handle to the process in which the thread is to be created.
     * @param lpThreadAttributes The {@link WinBase.SECURITY_ATTRIBUTES} structure that
     * specifies a security descriptor for the new thread. If {@code null}, the
     * thread gets a default security descriptor and the handle cannot be inherited.
     * @param dwStackSize The initial size of the stack, in bytes. The system rounds
     * this value to the nearest page. If this parameter is 0 (zero), the new thread
     * uses the default size for the executable.
     * @param lpStartAddress The application-defined {@link WinBase.FOREIGN_THREAD_START_ROUTINE}
     * to be executed by the thread and represents the starting address of the
     * thread in the remote process. The function must exist in the remote process.
     * @param lpParameter A pointer to a variable to be passed to the thread function.
     * @param dwCreationFlags The flags that control the creation of the thread.
     * @param lpThreadId A variable that receives the thread identifier. If this
     * parameter is {@code null}, the thread identifier is not returned.
     * @return If the function succeeds, the return value is a handle to the new thread.
     * If the function fails, the return value is {@code null}. To get extended
     * error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms682437(v=vs.85).aspx">CreateRemoteThread documentation</a>
     */
    HANDLE CreateRemoteThread(HANDLE hProcess, WinBase.SECURITY_ATTRIBUTES lpThreadAttributes, int dwStackSize, FOREIGN_THREAD_START_ROUTINE lpStartAddress, Pointer lpParameter, DWORD dwCreationFlags, Pointer lpThreadId);

    /**
     * Writes data to an area of memory in a specified process. The entire area
     * to be written to must be accessible or the operation fails.
     * @param hProcess A handle to the process memory to be modified.
     * @param lpBaseAddress The base address in the specified process to which
     * data is written.
     * @param lpBuffer The buffer that contains data to be written in the
     * address space of the specified process.
     * @param nSize The number of bytes to be written to the specified process.
     * @param lpNumberOfBytesWritten A variable that receives the number of bytes
     * transferred into the specified process.  If {@code null} the parameter is ignored.
     * @return {@code true} if successful, {@code false} otherwise.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms681674(v=vs.85).aspx">WriteProcessMemory documentation</a>
     */
    boolean WriteProcessMemory(HANDLE hProcess, Pointer lpBaseAddress, Pointer lpBuffer, int nSize, IntByReference lpNumberOfBytesWritten);

    /**
     * Reads data from an area of memory in a specified process. The entire area
     * to be read must be accessible or the operation fails.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms680553(v=vs.85).aspx">MSDN</a>
     * @param hProcess
     *            A handle to the process with memory that is being read. The
     *            handle must have PROCESS_VM_READ access to the process.
     * @param lpBaseAddress
     *            A pointer to the base address in the specified process from
     *            which to read. <br>
     *            Before any data transfer occurs, the system verifies that all
     *            data in the base address and memory of the specified size is
     *            accessible for read access, and if it is not accessible the
     *            function fails.
     * @param lpBuffer
     *            A pointer to a buffer that receives the contents from the
     *            address space of the specified process.
     * @param nSize
     *            The number of bytes to be read from the specified process.
     * @param lpNumberOfBytesRead
     *            A pointer to a variable that receives the number of bytes
     *            transferred into the specified buffer. If lpNumberOfBytesRead
     *            is NULL, the parameter is ignored.
     * @return If the function succeeds, the return value is nonzero.<br>
     *         If the function fails, the return value is 0 (zero). To get
     *         extended error information, call GetLastError.<br>
     *         The function fails if the requested read operation crosses into
     *         an area of the process that is inaccessible.
     */
    boolean ReadProcessMemory(HANDLE hProcess, Pointer lpBaseAddress, Pointer lpBuffer, int nSize, IntByReference lpNumberOfBytesRead);

    /**
     * Retrieves information about a range of pages within the virtual address
     * space of a specified process.
     * @param hProcess A handle to the process whose memory information is queried.
     * @param lpAddress The base address of the region of pages to be queried.
     * @param lpBuffer A {@link WinNT.MEMORY_BASIC_INFORMATION} structure in which
     * information about the specified page range is returned.
     * @param dwLength The size of the buffer pointed to by the <tt>lpBuffer</tt>
     * parameter, in bytes.
     * @return The return value is the actual number of bytes returned in the information buffer.
     * If the function fails, the return value is zero. To get extended error information,
     * call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa366907(v=vs.85).aspx">VirtualQueryEx documentation</a>
     */
    SIZE_T VirtualQueryEx(HANDLE hProcess, Pointer lpAddress, MEMORY_BASIC_INFORMATION lpBuffer, SIZE_T dwLength);

    /**
     * Defines, redefines, or deletes MS-DOS device names.
     * @param dwFlags The controllable aspects of the function - see the
     * various {@code DDD_XXX} constants
     * @param lpDeviceName The MS-DOS device name string specifying the device
     * the function is defining, redefining, or deleting. The device name string
     * must not have a colon as the last character, unless a drive letter is
     * being defined, redefined, or deleted. For example, drive {@code C} would
     * be the string &quot;C:&quot;. In no case is a trailing backslash
     * (&quot;\&quot;) allowed.
     * @param lpTargetPath  The path string that will implement this device.
     * The string is an MS-DOS path string unless the {@code DDD_RAW_TARGET_PATH}
     * flag is specified, in which case this string is a path string.
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa363904(v=vs.85).aspx">DefineDosDevice</a>
     */
    boolean DefineDosDevice(int dwFlags, String lpDeviceName, String lpTargetPath);

    /**
     * Retrieves information about MS-DOS device names
     * @param lpDeviceName An MS-DOS device name string specifying the target
     * of the query. The device name cannot have a trailing backslash; for
     * example, use &quot;C:&quot;, not &quot;C:\&quot;. This parameter can be
     * NULL. In that case, the function will store a list of all existing MS-DOS
     * device names into the buffer.
     * @param lpTargetPath A buffer that will receive the result of the query.
     * The function fills this buffer with one or more null-terminated strings.
     * The final null-terminated string is followed by an additional NULL. If
     * device name is non-NULL, the function retrieves information about the
     * particular MS-DOS device. The first null-terminated string stored into
     * the buffer is the current mapping for the device. The other null-terminated
     * strings represent undeleted prior mappings for the device. Each
     * null-terminated string stored into the buffer is the name of an existing
     * MS-DOS device, for example, {@code \Device\HarddiskVolume1} or {@code \Device\Floppy0}.
     * @param ucchMax The maximum number of characters that can be stored into the buffer
     * @return If the function succeeds, the return value is the number of characters stored
     * into the buffer, otherwise zero. Use {@link #GetLastError()} to get extended
     * error information. If the buffer is too small, the function fails and the last error
     * code is {@code ERROR_INSUFFICIENT_BUFFER}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365461(v=vs.85).aspx">QueryDosDevice</a>
     */
    int QueryDosDevice(String lpDeviceName, char[] lpTargetPath, int ucchMax);

    /**
     * Searches a directory for a file or subdirectory with a name that matches a specific name (or partial name if wildcards are used).
     * To specify additional attributes to use in a search, use the FindFirstFileEx function.
     * @param lpFileName
     * 				The directory or path, and the file name. The file name can include wildcard characters,
     * 				for example, an asterisk (*) or a question mark (?).
     * 				This parameter should not be NULL, an invalid string (for example, an empty string or a string that is
     * 				missing the terminating null character), or end in a trailing backslash (\).
     * 				If the string ends with a wildcard, period, or directory name, the user must have access to the root
     * 				and all subdirectories on the path.
     * 				In the ANSI version of this function, the name is limited to MAX_PATH characters. To extend this
     * 				limit to approximately 32,000 wide characters, call the Unicode version of the function (FindFirstFileExW),
     * 				and prepend "\\?\" to the path. For more information, see Naming a File.
     * 				<b>Tip</b> Starting in Windows 10, version 1607, for the unicode version of this function (FindFirstFileExW),
     * 				you can opt-in to remove the MAX_PATH character limitation without prepending "\\?\".
     * 				See the "Maximum Path Limitation" section of Naming Files, Paths, and Namespaces for details.
     * @param lpFindFileData
     * 				A pointer to the buffer that receives the file data. The pointer type is determined by the level of
     * 				information that is specified in the fInfoLevelId parameter.
     * @return If the function succeeds, the return value is a search handle used in a subsequent call to FindNextFile or FindClose,
     * 				and the lpFindFileData parameter contains information about the first file or directory found.
     * 				If the function fails or fails to locate files from the search string in the lpFileName parameter, the return
     * 				value is INVALID_HANDLE_VALUE and the contents of lpFindFileData are indeterminate.
     * 				To get extended error information, call the GetLastError function.
     * 				If the function fails because no matching files can be found, the GetLastError function returns ERROR_FILE_NOT_FOUND.
     */
    HANDLE FindFirstFile(String lpFileName, Pointer lpFindFileData);

    /**
     * Searches a directory for a file or subdirectory with a name and attributes that match those specified. For the most basic
     * version of this function, see FindFirstFile.
     * @param lpFileName
     * 				The directory or path, and the file name. The file name can include wildcard characters,
     * 				for example, an asterisk (*) or a question mark (?).
     * 				This parameter should not be NULL, an invalid string (for example, an empty string or a string that is
     * 				missing the terminating null character), or end in a trailing backslash (\).
     * 				If the string ends with a wildcard, period, or directory name, the user must have access to the root
     * 				and all subdirectories on the path.
     * 				In the ANSI version of this function, the name is limited to MAX_PATH characters. To extend this
     * 				limit to approximately 32,000 wide characters, call the Unicode version of the function (FindFirstFileExW),
     * 				and prepend "\\?\" to the path. For more information, see Naming a File.
     * 				<b>Tip</b> Starting in Windows 10, version 1607, for the unicode version of this function (FindFirstFileExW),
     * 				you can opt-in to remove the MAX_PATH character limitation without prepending "\\?\".
     * 				See the "Maximum Path Limitation" section of Naming Files, Paths, and Namespaces for details.
     * @param fInfoLevelId
     * 				The information level of the returned data. This parameter is one of the FINDEX_INFO_LEVELS enumeration values.
     * @param lpFindFileData
     * 				A pointer to the buffer that receives the file data. The pointer type is determined by the level of
     * 				information that is specified in the fInfoLevelId parameter.
     * @param fSearchOp
     * 				The type of filtering to perform that is different from wildcard matching. This parameter is one of
     * 				the FINDEX_SEARCH_OPS enumeration values.
     * @param lpSearchFilter
     * 				A pointer to the search criteria if the specified fSearchOp needs structured search information.
     * 				At this time, none of the supported fSearchOp values require extended search information. Therefore,
     * 				this pointer must be NULL.
     * @param dwAdditionalFlags
     * 				Specifies additional flags that control the search.
     * 				FIND_FIRST_EX_CASE_SENSITIVE (0x01) - Searches are case-sensitive.
     * 				FIND_FIRST_EX_LARGE_FETCH (0x02) - Uses a larger buffer for directory queries, which can increase performance
     * 				of the find operation. <b>Windows Server 2008, Windows Vista, Windows Server 2003 and Windows XP:  This value
     * 				is not supported until Windows Server 2008 R2 and Windows 7.</b>
     * @return If the function succeeds, the return value is a search handle used in a subsequent call to FindNextFile or FindClose,
     * 				and the lpFindFileData parameter contains information about the first file or directory found.
     * 				If the function fails or fails to locate files from the search string in the lpFileName parameter, the return
     * 				value is INVALID_HANDLE_VALUE and the contents of lpFindFileData are indeterminate.
     * 				To get extended error information, call the GetLastError function.
     * 				If the function fails because no matching files can be found, the GetLastError function returns ERROR_FILE_NOT_FOUND.
     */
    HANDLE FindFirstFileEx(String lpFileName, int fInfoLevelId, Pointer lpFindFileData, int fSearchOp, Pointer lpSearchFilter, DWORD dwAdditionalFlags);

    /**
     * Continues a file search from a previous call to the FindFirstFile, FindFirstFileEx, or FindFirstFileTransacted functions.
     * @param hFindFile
     * 				The search handle returned by a previous call to the FindFirstFile or FindFirstFileEx function.
     * @param lpFindFileData
     * 				A pointer to the WIN32_FIND_DATA structure that receives information about the found file or subdirectory.
     * @return If the function succeeds, the return value is nonzero and the lpFindFileData parameter contains
     * 				information about the next file or directory found. If the function fails, the return value is zero and the
     * 				contents of lpFindFileData are indeterminate. To get extended error information, call the GetLastError function.
     * 				If the function fails because no more matching files can be found, the GetLastError function returns ERROR_NO_MORE_FILES.
     */
    boolean FindNextFile(HANDLE hFindFile, Pointer lpFindFileData);

    /**
     * Closes a file search handle opened by the FindFirstFile, FindFirstFileEx, FindFirstFileNameW, FindFirstFileNameTransactedW,
     * FindFirstFileTransacted, FindFirstStreamTransactedW, or FindFirstStreamW functions.
     * @param hFindFile
     * 			The file search handle.
     * @return If the function succeeds, the return value is nonzero. If the function fails, the return value is zero.
     * 			To get extended error information, call GetLastError.
     */
    boolean FindClose(HANDLE hFindFile);

    /**
     * Retrieves the name of a mounted folder on the specified volume - used
     * to begin scanning the mounted folders on a volume
     * @param lpszRootPathName A volume GUID path for the volume to scan for
     * mounted folders. A trailing backslash is required.
     * @param lpszVolumeMountPoint A buffer that receives the name of the first
     * mounted folder that is found.
     * @param cchBufferLength The length of the buffer that receives the path
     * to the mounted folder
     * @return If succeeds, a search handle used in a subsequent call to the
     * FindNextVolumeMountPoint and FindVolumeMountPointClose
     * functions. Otherwise, the return value is the {@link #INVALID_HANDLE_VALUE}.
     * To get extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364426(v=vs.85).aspx">FindFirstVolumeMountPoint</a>
     */
    HANDLE FindFirstVolumeMountPoint(String lpszRootPathName, char[] lpszVolumeMountPoint, int cchBufferLength);

    /**
     * Continues a mounted folder search started by a call to the
     * {@link #FindFirstVolumeMountPoint(String, char[], int)} function - finds one
     * (next) mounted folder per call.
     * @param hFindVolumeMountPoint A mounted folder search handle returned by
     * a previous call to the {@link #FindFirstVolumeMountPoint(String, char[], int)}
     * function.
     * @param lpszVolumeMountPoint A buffer that receives the name of the (next)
     * mounted folder that is found.
     * @param cchBufferLength The length of the buffer that receives the path
     * to the mounted folder
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information. If no more mount points found then the reported
     * error is {@code ERROR_NO_MORE_FILES}. In this case, simply call
     * {@link #FindVolumeMountPointClose(com.sun.jna.platform.win32.WinNT.HANDLE)}
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364432(v=vs.85).aspx">FindNextVolumeMountPoint</a>
     */
    boolean FindNextVolumeMountPoint(HANDLE hFindVolumeMountPoint, char[] lpszVolumeMountPoint, int cchBufferLength);

    /**
     * Closes the specified mounted folder search handle.
     * @param hFindVolumeMountPoint A mounted folder search handle returned by
     * a previous call to the {@link #FindFirstVolumeMountPoint(String, char[], int)}
     * function.
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364435(v=vs.85).aspx">FindVolumeMountPointClose</a>
     */
    boolean FindVolumeMountPointClose(HANDLE hFindVolumeMountPoint);

    /**
     * Retrieves a volume GUID path for the volume that is associated with the
     * specified volume mount point (drive letter, volume GUID path, or mounted
     * folder).
     * @param lpszVolumeMountPoint A string that contains the path of a mounted
     * folder (e.g., &quot;Y:\MountX\&quot;) or a drive letter (for example,
     * &quot;X:\&quot;). The string must end with a trailing backslash.
     * @param lpszVolumeName A buffer that receives the volume GUID path - if
     * there is more than one volume GUID path for the volume, only the first
     * one in the mount manager's cache is returned.
     * @param cchBufferLength The length of the output buffer - a reasonable size
     * for the buffer to accommodate the largest possible volume GUID path is
     * at 50 characters
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364994(v=vs.85).aspx">GetVolumeNameForVolumeMountPoint</a>
     */
    boolean GetVolumeNameForVolumeMountPoint(String lpszVolumeMountPoint, char[] lpszVolumeName, int cchBufferLength);

    /**
     * Sets the label of a file system volume.
     * @param lpRootPathName The volume's drive letter (for example, {@code X:\})
     * or the path of a mounted folder that is associated with the volume (for
     * example, {@code Y:\MountX\}). The string must end with a trailing backslash.
     * If this parameter is NULL, the root of the current directory is used.
     * @param lpVolumeName The new label for the volume. If this parameter is NULL,
     * the function deletes any existing label from the specified volume and does
     * not assign a new label.
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365560(v=vs.85).aspx">SetVolumeLabel</a>
     */
    boolean SetVolumeLabel(String lpRootPathName, String lpVolumeName);

    /**
     * Associates a volume with a drive letter or a directory on another volume.
     * @param lpszVolumeMountPoint The user-mode path to be associated with the
     * volume. This may be a drive letter (for example, &quot;X:\&quot;) or a
     * directory on another volume (for example, &quot;Y:\MountX\&quot;). The
     * string must end with a trailing backslash.
     * @param lpszVolumeName A volume GUID path for the volume.
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365561(v=vs.85).aspx">SetVolumeMountPoint</a>
     */
    boolean SetVolumeMountPoint(String lpszVolumeMountPoint, String lpszVolumeName);

    /**
     * Deletes a drive letter or mounted folder
     * @param lpszVolumeMountPoint The drive letter or mounted folder to be deleted.
     * A trailing backslash is required, for example, &quot;X:\&quot; or &quot;Y:\MountX\&quot;.
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa363927(v=vs.85).aspx">DeleteVolumeMountPoint</a>
     */
    boolean DeleteVolumeMountPoint(String lpszVolumeMountPoint);

    /**
     * @param lpRootPathName A string that contains the root directory of the
     * volume to be described. If this parameter is {@code null}, the root of
     * the current directory is used. A trailing backslash is required. For example,
     * you specify &quot;\\MyServer\MyShare\&quot;, or &quot;C:\&quot;.
     * @param lpVolumeNameBuffer If not {@code null} then receives the name of
     * the specified volume. The buffer size is specified by the <tt>nVolumeNameSize</tt>
     * parameter.
     * @param nVolumeNameSize The length of the volume name buffer - max. size is
     * {@link WinDef#MAX_PATH} + 1 - ignored if no volume name buffer provided
     * @param lpVolumeSerialNumber Receives the volume serial number - can be
     * {@code null} if the serial number is not required
     * @param lpMaximumComponentLength Receives the maximum length of a file name
     * component that the underlying file system supports - can be {@code null}
     * if this data is not required
     * @param lpFileSystemFlags Receives flags associated with the file system
     *  - can be {@code null} if this data is not required
     * @param lpFileSystemNameBuffer If not {@code null} then receives the name
     * of the file system. The buffer size is specified by the <tt>nFileSystemNameSize</tt>
     * parameter.
     * @param nFileSystemNameSize The length of the file system name buffer -
     * max. size is {@link WinDef#MAX_PATH} + 1 - ignored if no file system name
     * buffer provided
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364993(v=vs.85).aspx">GetVolumeInformation</a>
     */
    boolean GetVolumeInformation(String lpRootPathName,
                    char[] lpVolumeNameBuffer, int nVolumeNameSize,
                    IntByReference lpVolumeSerialNumber,
                    IntByReference lpMaximumComponentLength,
                    IntByReference lpFileSystemFlags,
                    char[] lpFileSystemNameBuffer, int nFileSystemNameSize);

    /**
     * Retrieves the volume mount point where the specified path is mounted.
     * @param lpszFileName The input path string. Both absolute and relative
     * file and directory names, for example &quot;..&quot;, are acceptable in
     * this path. If you specify a relative directory or file name without a
     * volume qualifier, returns the drive letter of the boot volume. If this
     * parameter is an empty string, the function fails but the last error is
     * set to {@code ERROR_SUCCESS}.
     * @param lpszVolumePathName Buffer receives the volume mount point for the
     * input path.
     * @param cchBufferLength The length of the output buffer
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364996(v=vs.85).aspx">GetVolumePathName</a>
     */
    boolean GetVolumePathName(String lpszFileName, char[] lpszVolumePathName, int cchBufferLength);

    /**
     * Retrieves a list of drive letters and mounted folder paths for the specified volume
     * @param lpszVolumeName A volume GUID path for the volume
     * @param lpszVolumePathNames A buffer that receives the list of drive
     * letters and mounted folder paths. The list is an array of null-terminated
     * strings terminated by an additional NULL character. If the buffer is
     * not large enough to hold the complete list, the buffer holds as much of
     * the list as possible.
     * @param cchBufferLength The available length of the buffer - including all
     * NULL characters.
     * @param lpcchReturnLength If the call is successful, this parameter is the
     * number of character copied to the buffer. Otherwise, this parameter is the
     * size of the buffer required to hold the complete list
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information. If the buffer is not large enough to hold
     * the complete list, the error code is {@code ERROR_MORE_DATA} and the
     * <tt>lpcchReturnLength</tt> parameter receives the required buffer size.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364998(v=vs.85).aspx">GetVolumePathNamesForVolumeName</a>
     */
    boolean GetVolumePathNamesForVolumeName(String lpszVolumeName,
                    char[] lpszVolumePathNames, int cchBufferLength,
                    IntByReference lpcchReturnLength);

    /**
     * Retrieves the name of a volume on a computer - used to begin scanning the
     * volumes of a computer
     * @param lpszVolumeName A buffer that receives a null-terminated string that
     * specifies a volume GUID path for the first volume that is found
     * @param cchBufferLength The length of the buffer to receive the volume GUID path
     * @return If the function succeeds, the return value is a search handle
     * used in a subsequent call to the {@link #FindNextVolume(com.sun.jna.platform.win32.WinNT.HANDLE, char[], int)}
     * and {@link #FindVolumeClose(com.sun.jna.platform.win32.WinNT.HANDLE)} functions.
     * Otherwise, the return value is the {@link #INVALID_HANDLE_VALUE}. To get
     * extended error information, call {@link #GetLastError()}.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364425(v=vs.85).aspx">FindFirstVolume</a>
     * @see Kernel32Util#extractVolumeGUID(String)
     */
    HANDLE FindFirstVolume(char[] lpszVolumeName, int cchBufferLength);

    /**
     * Continues a volume search started by a call to the {@link #FindFirstVolume(char[], int)}
     * function - finds one volume per call.
     * @param hFindVolume The volume search handle returned by a previous call to the
     * {@link #FindFirstVolume(char[], int)}.
     * @param lpszVolumeName A buffer that receives a null-terminated string that
     * specifies a volume GUID path for the (next) path that is found
     * @param cchBufferLength The length of the buffer to receive the volume GUID path
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information. If no more volumes found then the reported
     * error is {@code ERROR_NO_MORE_FILES}. In this case, simply call {@link #FindVolumeClose(com.sun.jna.platform.win32.WinNT.HANDLE)}
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364431(v=vs.85).aspx">FindNextVolume</a>
     * @see Kernel32Util#extractVolumeGUID(String)
     */
    boolean FindNextVolume(HANDLE hFindVolume, char[] lpszVolumeName, int cchBufferLength);

    /**
     * Closes the specified volume search handle.
     * @param hFindVolume The volume search handle returned by a previous call to the
     * {@link #FindFirstVolume(char[], int)}.
     * @return {@code true} if succeeds. If fails then call {@link #GetLastError()}
     * to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa364433(v=vs.85).aspx">FindVolumeClose</a>
     */
    boolean FindVolumeClose(HANDLE hFindVolume);

    /**
     * Retrieves the current control settings for a specified communications
     * device.
     *
     * @param hFile
     *            [in] A handle to the communications device.<br>
     *            The
     *            {@link com.sun.jna.platform.win32.Kernel32#CreateFile(String, int, int, com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES, int, int, com.sun.jna.platform.win32.WinNT.HANDLE)}
     *            function returns this {@link WinNT.HANDLE}.
     * @param lpDCB
     *            [in, out] A pointer to a {@link WinBase.DCB} structure that
     *            receives the control settings information.
     *
     * @return If the function succeeds, the return value is nonzero. <br>
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError()}.
     *
     */
    boolean GetCommState(HANDLE hFile, WinBase.DCB lpDCB);

    /**
     *
     * Retrieves the time-out parameters for all read and write operations on a
     * specified communications device.<br>
     * <br>
     * For more information about time-out values for communications devices,
     * see the {@link Kernel32#SetCommTimeouts} function.
     *
     * @param hFile
     *            [in] A handle to the communications device. The
     *            {@link com.sun.jna.platform.win32.Kernel32#CreateFile(String, int, int, com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES, int, int, com.sun.jna.platform.win32.WinNT.HANDLE)}
     *            function returns this handle.
     *
     * @param lpCommTimeouts
     *            [in] A pointer to a {@link WinBase.COMMTIMEOUTS} structure in
     *            which the time-out information is returned.
     * @return If the function succeeds, the return value is nonzero.
     *
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError()}.
     *
     *
     *
     */
    boolean GetCommTimeouts(HANDLE hFile, WinBase.COMMTIMEOUTS lpCommTimeouts);

    /**
     * Configures a communications device according to the specifications in a
     * device-control block (a {@link WinBase.DCB} structure). The function
     * reinitializes all hardware and control settings, but it does not empty
     * output or input queues.
     *
     * @param hFile
     *            [in] A handle to the communications device. The
     *            {@link com.sun.jna.platform.win32.Kernel32#CreateFile(String, int, int, com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES, int, int, com.sun.jna.platform.win32.WinNT.HANDLE)}
     *            function returns this handle.
     * @param lpDCB
     *            [in] A pointer to a {@link WinBase.DCB} structure that
     *            contains the configuration information for the specified
     *            communications device.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call {@link Kernel32#GetLastError()}.
     */
    boolean SetCommState(HANDLE hFile, WinBase.DCB lpDCB);

    /**
     * Sets the time-out parameters for all read and write operations on a
     * specified communications device.
     *
     * @param hFile
     *            [in] A handle to the communications device. The
     *            {@link com.sun.jna.platform.win32.Kernel32#CreateFile(String, int, int, com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES, int, int, com.sun.jna.platform.win32.WinNT.HANDLE)}
     *            function returns this handle.
     * @param lpCommTimeouts
     *            [in] A pointer to a {@link WinBase.COMMTIMEOUTS} structure
     *            that contains the new time-out values.
     * @return If the function succeeds, the return value is nonzero. <br>
     *         If the function fails, the return value is zero. To get extended
     *         error information, call {@link Kernel32#GetLastError()}.
     */
    boolean SetCommTimeouts(HANDLE hFile, WinBase.COMMTIMEOUTS lpCommTimeouts);

    /**
     * http://msdn.microsoft.com/en-us/library/aa382990(v=vs.85).aspx<br>
     * <br>
     * Retrieves the Remote Desktop Services session associated with a specified
     * process.<br>
     * <br>
     * <pre><code>BOOL ProcessIdToSessionId(_In_ DWORD dwProcessId, _Out_ DWORD *pSessionId);</code></pre><br>
     *
     * @param dwProcessId
     *            Specifies a process identifier.<br>
     *            Use the GetCurrentProcessId function to retrieve the process
     *            identifier for the current process.
     * @param pSessionId
     *            Pointer to a variable that receives the identifier of the
     *            Remote Desktop Services session under which the specified
     *            process is running.<br>
     *            To retrieve the identifier of the session currently attached
     *            to the console, use the WTSGetActiveConsoleSessionId function.
     * @return If the function succeeds, the return value is true. <br>
     *         If the function fails, the return value is false. To get extended
     *         error information, call GetLastError.
     */
    boolean ProcessIdToSessionId(int dwProcessId, IntByReference pSessionId);

    /**
     * Loads the specified module into the address space of the calling process.
     * The specified module may cause other modules to be loaded.
     *
     * <pre>
     * <code>
     * HMODULE WINAPI LoadLibraryEx(
     *   _In_       LPCTSTR lpFileName,
     *   _Reserved_ HANDLE  hFile,
     *   _In_       DWORD   dwFlags
     * );
     * </code>
     * </pre>
     *
     * @param lpFileName
     *            A string that specifies the file name of the module to load.
     *            This name is not related to the name stored in a library
     *            module itself, as specified by the LIBRARY keyword in the
     *            module-definition (.def) file. <br>
     *            The module can be a library module (a .dll file) or an
     *            executable module (an .exe file). If the specified module is
     *            an executable module, static imports are not loaded; instead,
     *            the module is loaded as if DONT_RESOLVE_DLL_REFERENCES was
     *            specified. See the <em>dwFlags</em> parameter for more
     *            information. <br>
     *            If the string specifies a module name without a path and the
     *            file name extension is omitted, the function appends the
     *            default library extension .dll to the module name. To prevent
     *            the function from appending .dll to the module name, include a
     *            trailing point character (.) in the module name string. <br>
     *            If the string specifies a fully qualified path, the function
     *            searches only that path for the module. When specifying a
     *            path, be sure to use backslashes (\), not forward slashes (/).
     *            For more information about paths, see "Naming Files, Paths, and Namespaces" on MSDN. <br>
     *            If the string specifies a module name without a path and more
     *            than one loaded module has the same base name and extension,
     *            the function returns a handle to the module that was loaded
     *            first. <br>
     *            If the string specifies a module name without a path and a
     *            module of the same name is not already loaded, or if the
     *            string specifies a module name with a relative path, the
     *            function searches for the specified module. The function also
     *            searches for modules if loading the specified module causes
     *            the system to load other associated modules (that is, if the
     *            module has dependencies). The directories that are searched
     *            and the order in which they are searched depend on the
     *            specified path and the <em>dwFlags</em> parameter. For more
     *            information, see Remarks. <br>
     *            If the function cannot find the module or one of its
     *            dependencies, the function fails.
     * @param hFile
     *            This parameter is reserved for future use. It must be NULL.
     * @param flags
     *            The action to be taken when loading the module.<br>
     *            If no flags are specified, the behavior of this function is
     *            identical to that of the LoadLibrary function.<br>
     *            This parameter can be one of the following values. <br>
     *            <br>
     *            DONT_RESOLVE_DLL_REFERENCES: 0x00000001<br>
     *            If this value is used, and the executable module is a DLL, the
     *            system does not call DllMain for process and thread
     *            initialization and termination. Also, the system does not load
     *            additional executable modules that are referenced by the
     *            specified module. <br>
     *            Do not use this value; it is provided only for backward
     *            compatibility. If you are planning to access only data or
     *            resources in the DLL, use LOAD_LIBRARY_AS_DATAFILE_EXCLUSIVE
     *            or LOAD_LIBRARY_AS_IMAGE_RESOURCE or both. Otherwise, load the
     *            library as a DLL or executable module using the LoadLibrary
     *            function.<br>
     *            <br>
     *            LOAD_IGNORE_CODE_AUTHZ_LEVEL: 0x00000010<br>
     *            If this value is used, the system does not check AppLocker
     *            rules or apply Software Restriction Policies for the DLL.
     *            AppLocker was introduced in Windows 7 and Windows Server 2008
     *            R2. This action applies only to the DLL being loaded and not
     *            to its dependencies. This value is recommended for use in
     *            setup programs that must run extracted DLLs during
     *            installation. <br>
     *            Windows Server 2008 R2 and Windows 7: On systems with
     *            KB2532445 installed, the caller must be running as
     *            "LocalSystem" or "TrustedInstaller"; otherwise the system
     *            ignores this flag.<br>
     *            <br>
     *            LOAD_LIBRARY_AS_DATAFILE: 0x00000002<br>
     *            If this value is used, the system maps the file into the
     *            calling process's virtual address space as if it were a data
     *            file. Nothing is done to execute or prepare to execute the
     *            mapped file. Therefore, you cannot call functions like
     *            GetModuleFileName, GetModuleHandle or GetProcAddress with this
     *            DLL. Using this value causes writes to read-only memory to
     *            raise an access violation. Use this flag when you want to load
     *            a DLL only to extract messages or resources from it. <br>
     *            This value can be used with LOAD_LIBRARY_AS_IMAGE_RESOURCE.
     *            For more information, see Remarks <br>
     *            <br>
     *            LOAD_LIBRARY_AS_DATAFILE_EXCLUSIVE: 0x00000040<br>
     *            Similar to LOAD_LIBRARY_AS_DATAFILE, except that the DLL file
     *            is opened with exclusive write access for the calling process.
     *            Other processes cannot open the DLL file for write access
     *            while it is in use. However, the DLL can still be opened by
     *            other processes. <br>
     *            This value can be used with LOAD_LIBRARY_AS_IMAGE_RESOURCE.
     *            This value is not supported until Windows Vista. <br>
     *            <br>
     *            LOAD_LIBRARY_AS_IMAGE_RESOURCE: 0x00000020 <br>
     *            If this value is used, the system maps the file into the
     *            process's virtual address space as an image file. However, the
     *            loader does not load the static imports or perform the other
     *            usual initialization steps. Use this flag when you want to
     *            load a DLL only to extract messages or resources from it. <br>
     *            Unless the application depends on the file having the
     *            in-memory layout of an image, this value should be used with
     *            either LOAD_LIBRARY_AS_DATAFILE_EXCLUSIVE or
     *            LOAD_LIBRARY_AS_DATAFILE. This value is not supported until
     *            Windows Vista. <br>
     *            <br>
     *            LOAD_LIBRARY_SEARCH_APPLICATION_DIR: 0x00000200<br>
     *            If this value is used, the application's installation
     *            directory is searched for the DLL and its dependencies.
     *            Directories in the standard search path are not searched. This
     *            value cannot be combined with LOAD_WITH_ALTERED_SEARCH_PATH.
     *            <br>
     *            Windows 7, Windows Server 2008 R2, Windows Vista, and Windows
     *            Server 2008: This value requires KB2533623 to be installed.
     *            <br>
     *            <br>
     *            LOAD_LIBRARY_SEARCH_DEFAULT_DIRS: 0x00001000 <br>
     *            This value is a combination of
     *            LOAD_LIBRARY_SEARCH_APPLICATION_DIR,
     *            LOAD_LIBRARY_SEARCH_SYSTEM32, and
     *            LOAD_LIBRARY_SEARCH_USER_DIRS. Directories in the standard
     *            search path are not searched. This value cannot be combined
     *            with LOAD_WITH_ALTERED_SEARCH_PATH. <br>
     *            This value represents the recommended maximum number of
     *            directories an application should include in its DLL search
     *            path. <br>
     *            Windows 7, Windows Server 2008 R2, Windows Vista, and Windows
     *            Server 2008: This value requires KB2533623 to be installed.
     *            <br>
     *            <br>
     *            LOAD_LIBRARY_SEARCH_DLL_LOAD_DIR: 0x00000100<br>
     *
     *            If this value is used, the directory that contains the DLL is
     *            temporarily added to the beginning of the list of directories
     *            that are searched for the DLL's dependencies. Directories in
     *            the standard search path are not searched. <br>
     *            The lpFileName parameter must specify a fully qualified path.
     *            This value cannot be combined with
     *            LOAD_WITH_ALTERED_SEARCH_PATH. <br>
     *            For example, if Lib2.dll is a dependency of C:\Dir1\Lib1.dll,
     *            loading Lib1.dll with this value causes the system to search
     *            for Lib2.dll only in C:\Dir1. To search for Lib2.dll in
     *            C:\Dir1 and all of the directories in the DLL search path,
     *            combine this value with LOAD_LIBRARY_DEFAULT_DIRS. <br>
     *            Windows 7, Windows Server 2008 R2, Windows Vista, and Windows
     *            Server 2008: This value requires KB2533623 to be installed.
     *            <br>
     *            <br>
     *            LOAD_LIBRARY_SEARCH_SYSTEM32: 0x00000800<br>
     *            If this value is used, %windows%\system32 is searched for the
     *            DLL and its dependencies. Directories in the standard search
     *            path are not searched. This value cannot be combined with
     *            LOAD_WITH_ALTERED_SEARCH_PATH. <br>
     *            Windows 7, Windows Server 2008 R2, Windows Vista, and Windows
     *            Server 2008: This value requires KB2533623 to be installed.
     *            <br>
     *            <br>
     *            LOAD_LIBRARY_SEARCH_USER_DIRS: 0x00000400<br>
     *            If this value is used, directories added using the
     *            AddDllDirectory or the SetDllDirectory function are searched
     *            for the DLL and its dependencies. If more than one directory
     *            has been added, the order in which the directories are
     *            searched is unspecified. Directories in the standard search
     *            path are not searched. This value cannot be combined with
     *            LOAD_WITH_ALTERED_SEARCH_PATH. <br>
     *            Windows 7, Windows Server 2008 R2, Windows Vista, and Windows
     *            Server 2008: This value requires KB2533623 to be installed.
     *            <br>
     *            <br>
     *            LOAD_WITH_ALTERED_SEARCH_PATH: 0x00000008<br>
     *            If this value is used and <em>lpFileName</em> specifies an
     *            absolute path, the system uses the alternate file search
     *            strategy discussed in the Remarks section to find associated
     *            executable modules that the specified module causes to be
     *            loaded. If this value is used and <em>lpFileName</em>
     *            specifies a relative path, the behavior is undefined. <br>
     *            If this value is not used, or if <em>lpFileName</em> does not
     *            specify a path, the system uses the standard search strategy
     *            discussed in the Remarks section to find associated executable
     *            modules that the specified module causes to be loaded. <br>
     *            This value cannot be combined with any LOAD_LIBRARY_SEARCH
     *            flag.
     * @return If the function succeeds, the return value is a handle to the
     *         loaded module.<br>
     *         If the function fails, the return value is NULL. To get extended
     *         error information, call GetLastError.
     */
    HMODULE LoadLibraryEx(String lpFileName, HANDLE hFile, int flags);

    /**
     * Determines the location of a resource with the specified type and name in
     * the specified module.<br>
     * To specify a language, use the FindResourceEx function.
     *
     * @param hModule
     *            A handle to the module whose portable executable file or an
     *            accompanying MUI file contains the resource. <br>
     *            If this parameter is NULL, the function searches the module
     *            used to create the current process.
     * @param name
     *            The name of the resource.<br>
     *            Alternately, rather than a pointer, this parameter can be
     *            MAKEINTRESOURCE(ID), where ID is the integer identifier of the
     *            resource. <br>
     *            For more information, see the Remarks section below.
     * @param type
     *            The resource type.<br>
     *            Alternately, rather than a pointer, this parameter can be
     *            MAKEINTRESOURCE(ID), where ID is the integer identifier of the
     *            given resource type.<br>
     *            For standard resource types, see Resource Types. For more
     *            information, see the Remarks section below.
     * @return If the function succeeds, the return value is a handle to the
     *         specified resource's information block.<br>
     *         To obtain a handle to the resource, pass this handle to the
     *         LoadResource function. <br>
     *         If the function fails, the return value is NULL.<br>
     *         To get extended error information, call GetLastError.
     */
    HRSRC FindResource(HMODULE hModule, Pointer name, Pointer type);

    /**
     * Retrieves a handle that can be used to obtain a pointer to the first byte
     * of the specified resource in memory.
     *
     * @param hModule
     *            A handle to the module whose executable file contains the
     *            resource. <br>
     *            If hModule is NULL, the system loads the resource from the
     *            module that was used to create the current process.
     * @param hResource
     *            A handle to the resource to be loaded. <br>
     *            This handle is returned by the FindResource or FindResourceEx
     *            function.
     * @return If the function succeeds, the return value is a handle to the
     *         data associated with the resource.<br>
     *         If the function fails, the return value is NULL. <br>
     *         To get extended error information, call GetLastError.
     */
    HANDLE LoadResource(HMODULE hModule, HRSRC hResource);

    /**
     * Retrieves a pointer to the specified resource in memory.
     *
     * @param hResource
     *            A handle to the resource to be accessed. <br>
     *            The LoadResource function returns this handle.<br>
     *            Note that this parameter is listed as an HGLOBAL variable only
     *            for backward compatibility.<br>
     *            Do not pass any value as a parameter other than a successful
     *            return value from the LoadResource function.
     * @return If the loaded resource is available, the return value is a
     *         pointer to the first byte of the resource; otherwise, it is NULL.
     */
    Pointer LockResource(HANDLE hResource);

    /**
     * @param hModule
     *            A handle to the module whose executable file contains the
     *            resource.
     * @param hResource
     *            A handle to the resource. This handle must be created by using
     *            the FindResource or FindResourceEx function.
     * @return If the function succeeds, the return value is the number of bytes
     *         in the resource. <br>
     *         If the function fails, the return value is zero. To get extended
     *         error information, call GetLastError.
     */
    int SizeofResource(HMODULE hModule, HANDLE hResource);


    /**
     * Frees the loaded dynamic-link library (DLL) module and, if necessary,
     * decrements its reference count. When the reference count reaches zero,
     * the module is unloaded from the address space of the calling process and
     * the handle is no longer valid.
     *
     * @param module
     *            A handle to the loaded library module. The LoadLibrary,
     *            LoadLibraryEx, GetModuleHandle, or GetModuleHandleEx function
     *            returns this handle.
     * @return If the function succeeds, the return value is nonzero. If the
     *         function fails, the return value is zero. To get extended error
     *         information, call the GetLastError function.
     */
    boolean FreeLibrary(HMODULE module);

    /**
     * Enumerates resource types within a binary module.<br>
     * Starting with Windows Vista, this is typically a language-neutral
     * Portable Executable (LN file), and the enumeration also includes
     * resources from one of the corresponding language-specific resource files
     * (.mui files)-if one exists-that contain localizable language resources.
     * It is also possible to use hModule to specify a .mui file, in which case
     * only that file is searched for resource types.<br>
     * Alternately, applications can call EnumResourceTypesEx, which provides
     * more precise control over which resource files to enumerate.
     *
     * @param hModule
     *            A handle to a module to be searched.<br>
     *            This handle must be obtained through LoadLibrary or
     *            LoadLibraryEx.<br>
     *            See Remarks for more information. If this parameter is NULL,
     *            that is equivalent to passing in a handle to the module used
     *            to create the current process.
     * @param proc
     *            A pointer to the callback function to be called for each
     *            enumerated resource type.<br>
     *            For more information, see the EnumResTypeProc function.
     * @param lParam
     *            An application-defined value passed to the callback function.
     * @return Returns TRUE if successful; otherwise, FALSE. To get extended
     *         error information, call GetLastError.
     */
    boolean EnumResourceTypes(HMODULE hModule, WinBase.EnumResTypeProc proc, Pointer lParam);

    /**
     * Enumerates resources of a specified type within a binary module. <br>
     * For Windows Vista and later, this is typically a language-neutral
     * Portable Executable (LN file), and the enumeration will also include
     * resources from the corresponding language-specific resource files (.mui
     * files) that contain localizable language resources.<br>
     * It is also possible for hModule to specify an .mui file, in which case
     * only that file is searched for resources.
     *
     * @param hModule
     *            A handle to a module to be searched. <br>
     *            Starting with Windows Vista, if this is an LN file, then
     *            appropriate .mui files (if any exist) are included in the
     *            search.<br>
     *            If this parameter is NULL, that is equivalent to passing in a
     *            handle to the module used to create the current process.
     * @param type
     *            The type of the resource for which the name is being
     *            enumerated.<br>
     *            Alternately, rather than a pointer, this parameter can be
     *            MAKEINTRESOURCE(ID), where ID is an integer value representing
     *            a predefined resource type.<br>
     *            For a list of predefined resource types, see Resource Types.
     *            For more information, see the Remarks section below.
     * @param proc
     *            A pointer to the callback function to be called for each
     *            enumerated resource name or ID. For more information, see
     *            EnumResNameProc.
     * @param lParam
     *            An application-defined value passed to the callback function.
     *            This parameter can be used in error checking.
     * @return The return value is TRUE if the function succeeds or FALSE if the
     *         function does not find a resource of the type specified, or if
     *         the function fails for another reason. To get extended error
     *         information, call GetLastError.
     */
    boolean EnumResourceNames(HMODULE hModule, Pointer type, WinBase.EnumResNameProc proc, Pointer lParam);

    /**
     * Retrieves information about the first module associated with a process.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms684218(v=vs.85).aspx">MSDN</a>
     * @param hSnapshot
     *            A handle to the snapshot returned from a previous call to the
     *            CreateToolhelp32Snapshot function.
     * @param lpme
     *            A pointer to a MODULEENTRY32 structure.
     * @return Returns TRUE if the first entry of the module list has been
     *         copied to the buffer or FALSE otherwise.<br>
     *         The ERROR_NO_MORE_FILES error value is returned by the
     *         GetLastError function if no modules exist or the snapshot does
     *         not contain module information.
     */
    boolean Module32FirstW(HANDLE hSnapshot, Tlhelp32.MODULEENTRY32W lpme);

    /**
     * Retrieves information about the next module associated with a process or
     * thread.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms684221(v=vs.85).aspx">MSDN</a>
     * @param hSnapshot
     *            A handle to the snapshot returned from a previous call to the
     *            CreateToolhelp32Snapshot function.
     * @param lpme
     *            A pointer to a MODULEENTRY32 structure.
     * @return Returns TRUE if the first entry of the module list has been
     *         copied to the buffer or FALSE otherwise.<br>
     *         The ERROR_NO_MORE_FILES error value is returned by the
     *         GetLastError function if no modules exist or the snapshot does
     *         not contain module information.
     */
    boolean Module32NextW(HANDLE hSnapshot, Tlhelp32.MODULEENTRY32W lpme);
    
    /**
     * Controls whether the system will handle the specified types of serious
     * errors or whether the process will handle them.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms680621(v=vs.85).aspx">MSDN</a>
     * 
     * @param umode
     *            The process error mode.
     * @return The return value is the previous state of the error-mode bit
     *         flags.
     */
    int SetErrorMode(int umode);
    
    /**
     * Retrieves the address of an exported function or variable from the
     * specified dynamic-link library (DLL).
     *
     * <p>
     * This function is mapped to enable accessing function on win32 systems
     * only accessible by their ordinal value.</p>
     *
     * <p>
     * To access functions by their name, please use
     * NativeLibrary#getFunction.</p>
     *
     * @param hmodule A handle to the DLL module that contains the function or
     *                variable. The LoadLibrary, LoadLibraryEx,
     *                LoadPackagedLibrary, or GetModuleHandle function returns
     *                this handle.
     * @param ordinal ordinal value of the function export
     * @return address of the exported function
     */
    Pointer GetProcAddress(HMODULE hmodule, int ordinal) throws LastErrorException;
    
    /**
     * Enables an application to inform the system that it is in use, thereby
     * preventing the system from entering sleep or turning off the display
     * while the application is running.
     *
     * @param esFlags The thread's execution requirements. This parameter can be
     *                one or more of the following values (ORed together)
     * 
     * <ul>
     * <li>{@link com.sun.jna.platform.win32.WinBase#ES_AWAYMODE_REQUIRED}</li>
     * <li>{@link com.sun.jna.platform.win32.WinBase#ES_CONTINUOUS}</li>
     * <li>{@link com.sun.jna.platform.win32.WinBase#ES_DISPLAY_REQUIRED}</li>
     * <li>{@link com.sun.jna.platform.win32.WinBase#ES_SYSTEM_REQUIRED}</li>
     * <li>{@link com.sun.jna.platform.win32.WinBase#ES_USER_PRESENT}</li>
     * </ul>
     *
     * @return If the function succeeds, the return value is the previous thread
     *         execution state.
     * <p>
     * If the function fails, the return value is 0</p>
     */
    int SetThreadExecutionState(int esFlags);
}
