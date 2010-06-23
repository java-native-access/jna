/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
import com.sun.jna.platform.win32.WinBase.MEMORYSTATUSEX;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFO;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFOEX;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/** Definition (incomplete) of <code>kernel32.dll</code>. */
public interface Kernel32 extends StdCallLibrary {

    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, 
    		W32APIOptions.UNICODE_OPTIONS);

    /**
     * Frees the specified local memory object and invalidates its handle.
     * @param hLocal
     *  A handle to the local memory object.
     * @return 
     * 	If the function succeeds, the return value is NULL.
     * 	If the function fails, the return value is equal to a handle to the local memory object.
     *  To get extended error information, call GetLastError.
     */
    Pointer LocalFree(Pointer hLocal);
    
    /**
     * Frees the specified global memory object and invalidates its handle.
     * @param hGlobal 
     *  A handle to the global memory object. 
     * @return 
     * 	If the function succeeds, the return value is NULL
     * 	If the function fails, the return value is equal to a handle to the global memory object.
     *  To get extended error information, call GetLastError.
     */
    Pointer GlobalFree(Pointer hGlobal);

    /**
     * The GetModuleHandle function retrieves a module handle for the specified module 
     * if the file has been mapped into the address space of the calling process.
     * @param name
     *  Pointer to a null-terminated string that contains the name of the module 
     *  (either a .dll or .exe file). 
     * @return
     *  If the function succeeds, the return value is a handle to the specified module. 
     *  If the function fails, the return value is NULL. To get extended error 
     *  information, call GetLastError.
     */
    HMODULE GetModuleHandle(String name);
    
    /**
     * The GetSystemTime function retrieves the current system date and time. 
     * The system time is expressed in Coordinated Universal Time (UTC).
     * @param lpSystemTime
     *  Pointer to a SYSTEMTIME structure to receive the current system date and time. 
     */
    void GetSystemTime(WinBase.SYSTEMTIME lpSystemTime);
    
    /**
     * The GetCurrentThreadId function retrieves the thread identifier of the calling thread.
     * @return The return value is the thread identifier of the calling thread.
     */
    int GetCurrentThreadId();
    
    /**
     * The GetCurrentThread function retrieves a pseudo handle for the current thread.
     * @return The return value is a pseudo handle for the current thread.
     */
    HANDLE GetCurrentThread();
    
    /**
     * This function returns the process identifier of the calling process. 
     * @return The return value is the process identifier of the calling process.
     */
    int GetCurrentProcessId();
    
    /**
     * This function returns a pseudohandle for the current process. 
     * @return The return value is a pseudohandle to the current process. 
     */
    HANDLE GetCurrentProcess();
    
    /**
     * The GetProcessId function retrieves the process identifier of the 
     * specified process.
     * @param process
     *  Handle to the process. The handle must have the PROCESS_QUERY_INFORMATION access right. 
     * @return
     *  If the function succeeds, the return value is the process identifier of the 
     *  specified process. If the function fails, the return value is zero. To get
     *  extended error information, call GetLastError.
     */
    int GetProcessId(HANDLE process);
    
    /**
     * The GetProcessVersion function retrieves the major and minor version numbers of the system 
     * on which the specified process expects to run.
     * @param processId
     *  Process identifier of the process of interest. A value of zero specifies the 
     *  calling process. 
     * @return
     *  If the function succeeds, the return value is the version of the system on 
     *  which the process expects to run. The high word of the return value contains 
     *  the major version number. The low word of the return value contains the minor
     *  version number. If the function fails, the return value is zero. To get extended
     *  error information, call GetLastError. The function fails if ProcessId is an 
     *  invalid value.
     */
    int GetProcessVersion(int processId);
    
    /**
     * The GetLastError function retrieves the calling thread's last-error code value.
     * The last-error code is maintained on a per-thread basis. Multiple threads do not
     * overwrite each other's last-error code.
     * @return
     *  The return value is the calling thread's last-error code value.
     */
    int GetLastError();
    
    /**
     * The SetLastError function sets the last-error code for the calling thread.
     * @param dwErrCode
     *  Last-error code for the thread. 
     */
    void SetLastError(int dwErrCode);
    
    /**
     * The GetDriveType function determines whether a disk drive is a removable,
     * fixed, CD-ROM, RAM disk, or network drive.
     * @param lpRootPathName
     *  Pointer to a null-terminated string that specifies the root directory of
     *  the disk to return information about. A trailing backslash is required. 
     *  If this parameter is NULL, the function uses the root of the current directory.
     * @return
     *  The return value specifies the type of drive.
     */
    int GetDriveType(String lpRootPathName);

    /**
     * The FormatMessage function formats a message string. The function requires a
     * message definition as input. The message definition can come from a buffer 
     * passed into the function. It can come from a message table resource in an 
     * already-loaded module. Or the caller can ask the function to search the 
     * system's message table resource(s) for the message definition. The function 
     * finds the message definition in a message table resource based on a message 
     * identifier and a language identifier. The function copies the formatted message 
     * text to an output buffer, processing any embedded insert sequences if requested.
     * @param dwFlags
     *  Formatting options, and how to interpret the lpSource parameter. The low-order
     *  byte of dwFlags specifies how the function handles line breaks in the output 
     *  buffer. The low-order byte can also specify the maximum width of a formatted
     *  output line. 
     * @param lpSource
     *  Location of the message definition.
     * @param dwMessageId
     *  Message identifier for the requested message.
     * @param dwLanguageId
     *  Language identifier for the requested message.
     * @param lpBuffer
     *  Pointer to a buffer that receives the null-terminated string that specifies the 
     *  formatted message.
     * @param nSize
     *  If the FORMAT_MESSAGE_ALLOCATE_BUFFER flag is not set, this parameter specifies
     *  the size of the output buffer, in TCHARs. If FORMAT_MESSAGE_ALLOCATE_BUFFER is 
     *  set, this parameter specifies the minimum number of TCHARs to allocate for an 
     *  output buffer.
     * @param va_list
     *  Pointer to an array of values that are used as insert values in the formatted message.
     * @return
     * 	If the function succeeds, the return value is the number of TCHARs stored in 
     * 	the output buffer, excluding the terminating null character. If the function 
     * 	fails, the return value is zero. To get extended error information, call 
     *  GetLastError.
     */
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, PointerByReference lpBuffer,
                      int nSize, Pointer va_list);
    
    /**
     * The FormatMessage function formats a message string. The function requires a
     * message definition as input. The message definition can come from a buffer 
     * passed into the function. It can come from a message table resource in an 
     * already-loaded module. Or the caller can ask the function to search the 
     * system's message table resource(s) for the message definition. The function 
     * finds the message definition in a message table resource based on a message 
     * identifier and a language identifier. The function copies the formatted message 
     * text to an output buffer, processing any embedded insert sequences if requested.
     * @param dwFlags
     *  Formatting options, and how to interpret the lpSource parameter. The low-order
     *  byte of dwFlags specifies how the function handles line breaks in the output 
     *  buffer. The low-order byte can also specify the maximum width of a formatted
     *  output line. 
     * @param lpSource
     *  Location of the message definition.
     * @param dwMessageId
     *  Message identifier for the requested message.
     * @param dwLanguageId
     *  Language identifier for the requested message.
     * @param lpBuffer
     *  Pointer to a buffer that receives the null-terminated string that specifies the 
     *  formatted message.
     * @param nSize
     *  If the FORMAT_MESSAGE_ALLOCATE_BUFFER flag is not set, this parameter specifies
     *  the size of the output buffer, in TCHARs. If FORMAT_MESSAGE_ALLOCATE_BUFFER is 
     *  set, this parameter specifies the minimum number of TCHARs to allocate for an 
     *  output buffer.
     * @param va_list
     *  Pointer to an array of values that are used as insert values in the formatted message.
     * @return
     * 	If the function succeeds, the return value is the number of TCHARs stored in 
     * 	the output buffer, excluding the terminating null character. If the function 
     * 	fails, the return value is zero. To get extended error information, call 
     *  GetLastError.
     */
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, Buffer lpBuffer,
                      int nSize, Pointer va_list);

    /**
     * The CreateFile function creates or opens a file, file stream, directory, physical
     * disk, volume, console buffer, tape drive, communications resource, mailslot, or 
     * named pipe. The function returns a handle that can be used to access an object.
     * @param lpFileName
     *  A pointer to a null-terminated string that specifies the name of an object to create or open. 
     * @param dwDesiredAccess
     *  The access to the object, which can be read, write, or both.
     * @param dwShareMode
     *  The sharing mode of an object, which can be read, write, both, or none.
     * @param lpSecurityAttributes
     *  A pointer to a SECURITY_ATTRIBUTES structure that determines whether or not 
     *  the returned handle can be inherited by child processes.  If lpSecurityAttributes 
     *  is NULL, the handle cannot be inherited. 
     * @param dwCreationDisposition
     *  An action to take on files that exist and do not exist. 
     * @param dwFlagsAndAttributes
     *  The file attributes and flags. 
     * @param hTemplateFile
     *  Handle to a template file with the GENERIC_READ access right. The template file
     *  supplies file attributes and extended attributes for the file that is being 
     *  created. This parameter can be NULL.
     * @return
     * 	If the function succeeds, the return value is an open handle to a specified file.
     *  If a specified file exists before the function call and dwCreationDisposition is
     *  CREATE_ALWAYS or OPEN_ALWAYS, a call to GetLastError returns ERROR_ALREADY_EXISTS,
     *  even when the function succeeds. If a file does not exist before the call, 
     *  GetLastError returns 0 (zero). If the function fails, the return value is 
     *  INVALID_HANDLE_VALUE. To get extended error information, call GetLastError.
     */
    HANDLE CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode, 
    		WinNT.SECURITY_ATTRIBUTES lpSecurityAttributes, int dwCreationDisposition, 
    		int dwFlagsAndAttributes, HANDLE hTemplateFile);
        
    
    /**
     * The CreateDirectory function creates a new directory. If the underlying file
     * system supports security on files and directories, the function applies a 
     * specified security descriptor to the new directory.
     * @param lpPathName
     *  Pointer to a null-terminated string that specifies the path of the directory 
     *  to be created. 
     * @param lpSecurityAttributes
     *  Pointer to a SECURITY_ATTRIBUTES structure. The lpSecurityDescriptor member 
     *  of the structure specifies a security descriptor for the new directory. If 
     *  lpSecurityAttributes is NULL, the directory gets a default security descriptor. 
     * @return
     *  If the function succeeds, the return value is nonzero. If the function fails, 
     *  the return value is zero. To get extended error information, call GetLastError. 
     */
    boolean CreateDirectory(String lpPathName, 
    		WinNT.SECURITY_ATTRIBUTES lpSecurityAttributes);

    /**
     * Creates an input/output (I/O) completion port and associates it with a specified
     * file handle, or creates an I/O completion port that is not yet associated with a
     * file handle, allowing association at a later time.
     * @param FileHandle
     *  An open file handle or INVALID_HANDLE_VALUE.
     * @param ExistingCompletionPort
     *  A handle to an existing I/O completion port or NULL.
     * @param CompletionKey
     *  The per-handle user-defined completion key that is included in every I/O completion 
     *  packet for the specified file handle.
     * @param NumberOfConcurrentThreads
     * 	The maximum number of threads that the operating system can allow to concurrently 
     * 	process I/O completion packets for the I/O completion port.
     * @return
     * 	If the function succeeds, the return value is the handle to an I/O completion port:
     *   If the ExistingCompletionPort parameter was NULL, the return value is a new handle.
     *   If the ExistingCompletionPort parameter was a valid I/O completion port handle, the return value is that same handle.
     *   If the FileHandle parameter was a valid handle, that file handle is now associated with the returned I/O completion port.
     *   If the function fails, the return value is NULL. To get extended error information, call the GetLastError function.
     */
    HANDLE CreateIoCompletionPort(HANDLE FileHandle, HANDLE ExistingCompletionPort,
    		Pointer CompletionKey, int NumberOfConcurrentThreads);

    /**
     * Attempts to dequeue an I/O completion packet from the specified I/O completion 
     * port. If there is no completion packet queued, the function waits for a pending 
     * I/O operation associated with the completion port to complete.
     * @param CompletionPort
     *  A handle to the completion port.
     * @param lpNumberOfBytes
     *  A pointer to a variable that receives the number of bytes transferred during 
     *  an I/O operation that has completed.
     * @param lpCompletionKey
     *  A pointer to a variable that receives the completion key value associated with 
     *  the file handle whose I/O operation has completed.
     * @param lpOverlapped
     *  A pointer to a variable that receives the address of the OVERLAPPED structure 
     *  that was specified when the completed I/O operation was started. 
     * @param dwMilliseconds
     *  The number of milliseconds that the caller is willing to wait for a completion 
     *  packet to appear at the completion port. 
     * @return
     *  Returns nonzero (TRUE) if successful or zero (FALSE) otherwise.
     */
    boolean GetQueuedCompletionStatus(HANDLE CompletionPort, 
    		IntByReference lpNumberOfBytes, ByReference lpCompletionKey, 
    		PointerByReference lpOverlapped, int dwMilliseconds);

    /**
     * Posts an I/O completion packet to an I/O completion port.
     * @param CompletionPort
     *  A handle to an I/O completion port to which the I/O completion packet is to be posted.
     * @param dwNumberOfBytesTransferred
     *  The value to be returned through the lpNumberOfBytesTransferred parameter of the GetQueuedCompletionStatus function.
     * @param dwCompletionKey
     *  The value to be returned through the lpCompletionKey parameter of the GetQueuedCompletionStatus function.
     * @param lpOverlapped
     *  The value to be returned through the lpOverlapped parameter of the GetQueuedCompletionStatus function.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError .
     */
    boolean PostQueuedCompletionStatus(HANDLE CompletionPort,
    		int dwNumberOfBytesTransferred, Pointer dwCompletionKey,
    		WinBase.OVERLAPPED lpOverlapped);
    
    /**
     * Waits until the specified object is in the signaled state or the time-out interval elapses.
	 * To enter an alertable wait state, use the WaitForSingleObjectEx function. 
	 * To wait for multiple objects, use the WaitForMultipleObjects.
	 * @param hHandle 
	 *  A handle to the object. For a list of the object types whose handles can be specified, see the following Remarks section.
	 *  If this handle is closed while the wait is still pending, the function's behavior is undefined.
	 *	The handle must have the SYNCHRONIZE access right. For more information, see Standard Access Rights.
     * @param dwMilliseconds
     * 	The time-out interval, in milliseconds. If a nonzero value is specified, the function waits until the object is signaled or the interval elapses. 
     * 	If dwMilliseconds is zero, the function does not enter a wait state if the object is not signaled; it always returns immediately. 
     * 	If dwMilliseconds is INFINITE, the function will return only when the object is signaled.
     * @return	
     *  If the function succeeds, the return value indicates the event that caused the function to return.
     */    
    int WaitForSingleObject(HANDLE hHandle, int dwMilliseconds);
    
    /**
     * Waits until one or all of the specified objects are in the signaled state or the time-out interval elapses.
     * To enter an alertable wait state, use the WaitForMultipleObjectsEx function.
     * @param nCount 
     * 	The number of object handles in the array pointed to by lpHandles. The maximum number of object handles is MAXIMUM_WAIT_OBJECTS.
     * @param hHandle
     * 	An array of object handles. For a list of the object types whose handles can be specified, see the following Remarks section. The array can contain handles to objects of different types. 
     * 	It may not contain multiple copies of the same handle.
     * 	If one of these handles is closed while the wait is still pending, the function's behavior is undefined.
     * 	The handles must have the SYNCHRONIZE access right. For more information, see Standard Access Rights.
     * @param bWaitAll
     * 	If this parameter is TRUE, the function returns when the state of all objects in the lpHandles array is signaled. 
     * 	If FALSE, the function returns when the state of any one of the objects is set to signaled. 
     * 	In the latter case, the return value indicates the object whose state caused the function to return.
     * @param dwMilliseconds
     *  The time-out interval, in milliseconds. If a nonzero value is specified, the function waits until the specified objects are signaled or the interval elapses. 
     * 	If dwMilliseconds is zero, the function does not enter a wait state if the specified objects are not signaled; it always returns immediately. 
     * 	If dwMilliseconds is INFINITE, the function will return only when the specified objects are signaled.
     * @return	
     *  If the function succeeds, the return value indicates the event that caused the function to return. 
     */
    int WaitForMultipleObjects(int nCount, HANDLE[] hHandle, boolean bWaitAll, int dwMilliseconds);
    
    /**
     * The DuplicateHandle function duplicates an object handle. 
     * 
     * @param hSourceProcessHandle
     *  Handle to the process with the handle to duplicate. 
     *  The handle must have the PROCESS_DUP_HANDLE access right.
     * @param hSourceHandle
     *  Handle to duplicate. This is an open object handle that is valid in the 
     *  context of the source process.
     * @param hTargetProcessHandle
     *  Handle to the process that is to receive the duplicated handle. 
     *  The handle must have the PROCESS_DUP_HANDLE access right. 
     * @param lpTargetHandle
     *  Pointer to a variable that receives the duplicate handle. This handle value is valid in 
     *  the context of the target process. If hSourceHandle is a pseudo handle returned by 
     *  GetCurrentProcess or GetCurrentThread, DuplicateHandle converts it to a real handle to 
     *  a process or thread, respectively.
     * @param dwDesiredAccess
     *  Access requested for the new handle. 
     * @param bInheritHandle
     *  Indicates whether the handle is inheritable.
     * @param dwOptions
     *  Optional actions.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    boolean DuplicateHandle(HANDLE hSourceProcessHandle, HANDLE hSourceHandle,
    		HANDLE hTargetProcessHandle, HANDLEByReference lpTargetHandle,
    		int dwDesiredAccess, boolean bInheritHandle, int dwOptions);
    
    /**
     * The CloseHandle function closes an open object handle.
     * 
     * @param hObject
     *  Handle to an open object. This parameter can be a pseudo handle or INVALID_HANDLE_VALUE.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    boolean CloseHandle(HANDLE hObject);
    
    // TODO: figure out how OVERLAPPED is used and apply an appropriate mapping
    interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallCallback {
        void callback(int errorCode, int nBytesTransferred,
        		WinBase.OVERLAPPED overlapped);
    }
    
    /**
     * Retrieves information that describes the changes within the specified directory. 
     * The function does not report changes to the specified directory itself.
     * Note: there's no ReadDirectoryChangesA.
     * @param directory
     *  A handle to the directory to be monitored. This directory must be opened with the 
     *  FILE_LIST_DIRECTORY access right.
     * @param info
     *  A pointer to the DWORD-aligned formatted buffer in which the read results are to be returned.
     * @param length
     *  The size of the buffer that is pointed to by the lpBuffer parameter, in bytes.
     * @param watchSubtree
     *  If this parameter is TRUE, the function monitors the directory tree rooted at the specified
     *  directory. If this parameter is FALSE, the function monitors only the directory specified by
     *  the hDirectory parameter.
     * @param notifyFilter
     *  The filter criteria that the function checks to determine if the wait operation has completed.
     * @param bytesReturned
     *  For synchronous calls, this parameter receives the number of bytes transferred into the 
     *  lpBuffer parameter. For asynchronous calls, this parameter is undefined. You must use an asynchronous 
     *  notification technique to retrieve the number of bytes transferred.
     * @param overlapped
     *  A pointer to an OVERLAPPED structure that supplies data to be used during asynchronous operation. 
     *  Otherwise, this value is NULL. The Offset and OffsetHigh members of this structure are not used.
     * @param completionRoutine
     * 	A pointer to a completion routine to be called when the operation has been completed or canceled and
     *  the calling thread is in an alertable wait state.
     * @return
     *  If the function succeeds, the return value is nonzero. For synchronous calls, this means that the 
     *  operation succeeded. For asynchronous calls, this indicates that the operation was successfully queued.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     *  If the network redirector or the target file system does not support this operation, the function 
     *  fails with ERROR_INVALID_FUNCTION.
     */
    public boolean ReadDirectoryChangesW(HANDLE directory, 
    		WinNT.FILE_NOTIFY_INFORMATION info, int length, boolean watchSubtree,
            int notifyFilter, IntByReference bytesReturned, WinBase.OVERLAPPED overlapped, 
            OVERLAPPED_COMPLETION_ROUTINE completionRoutine);

    /**
     * Retrieves the short path form of the specified path.
     * @param lpszLongPath
     *  The path string. 
     * @param lpdzShortPath
     *  A pointer to a buffer to receive the null-terminated short form of the path that lpszLongPath specifies.
     * @param cchBuffer
     *  The size of the buffer that lpszShortPath points to, in TCHARs.
     * @return
     *  If the function succeeds, the return value is the length, in TCHARs, of the string that is copied to 
     *  lpszShortPath, not including the terminating null character.
     *  If the lpszShortPath buffer is too small to contain the path, the return value is the size of the buffer, 
     *  in TCHARs, that is required to hold the path and the terminating null character. 
     *  If the function fails for any other reason, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    int GetShortPathName(String lpszLongPath, char[] lpdzShortPath, int cchBuffer);

    /**
     * The LocalAlloc function allocates the specified number of bytes from the heap. 
     * Windows memory management does not provide a separate local heap and global heap.
     * @param type
     *  Memory allocation attributes. The default is the LMEM_FIXED value.
     * @param cbInput
     *  Number of bytes to allocate. If this parameter is zero and the uFlags parameter 
     *  specifies LMEM_MOVEABLE, the function returns a handle to a memory object that 
     *  is marked as discarded. 
     * @return
     *  If the function succeeds, the return value is a handle to the newly allocated memory object.
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
     */
    Pointer LocalAlloc(int type, int cbInput);

    /**
     * Writes data to the specified file or input/output (I/O) device.
     * @param hFile
     *  A handle to the file or I/O device (for example, a file, file stream, physical disk, volume, 
     *  console buffer, tape drive, socket, communications resource, mailslot, or pipe). 
     * @param lpBuffer
     *  A pointer to the buffer containing the data to be written to the file or device.
     * @param nNumberOfBytesToWrite
     *  The number of bytes to be written to the file or device.
     * @param lpNumberOfBytesWritten
     *  A pointer to the variable that receives the number of bytes written when using a synchronous hFile parameter. 
     * @param lpOverlapped
     *  A pointer to an OVERLAPPED structure is required if the hFile parameter was opened with FILE_FLAG_OVERLAPPED, 
     *  otherwise this parameter can be NULL. 
     * @return
     *  If the function succeeds, the return value is nonzero (TRUE).
     *  If the function fails, or is completing asynchronously, the return value is zero (FALSE). 
     *  To get extended error information, call the GetLastError function. 
     */
    boolean WriteFile(HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToWrite,
                      IntByReference lpNumberOfBytesWritten,
                      WinBase.OVERLAPPED lpOverlapped);

    /**
     * Creates or opens a named or unnamed event object.
     * @param lpEventAttributes
     *  A pointer to a SECURITY_ATTRIBUTES structure. If this parameter is NULL, 
     *  the handle cannot be inherited by child processes.
     * @param bManualReset
     *  If this parameter is TRUE, the function creates a manual-reset event object,
     *  which requires the use of the ResetEvent function to set the event state to nonsignaled. 
     *  If this parameter is FALSE, the function creates an auto-reset event object, and system 
     *  automatically resets the event state to nonsignaled after a single waiting thread has
     *  been released. 
     * @param bInitialState
     *  If this parameter is TRUE, the initial state of the event object is signaled; otherwise, 
     *  it is nonsignaled. 
     * @param lpName
     *  The name of the event object. The name is limited to MAX_PATH characters. Name comparison 
     *  is case sensitive. 
     * @return
     *  If the function succeeds, the return value is a handle to the event object. If the named event 
     *  object existed before the function call, the function returns a handle to the existing object
     *  and GetLastError returns ERROR_ALREADY_EXISTS. 
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError. 
     */
    HANDLE CreateEvent(WinNT.SECURITY_ATTRIBUTES lpEventAttributes,
                       boolean bManualReset, boolean bInitialState,
                       String lpName);

    /**
     * Sets the specified event object to the signaled state.
     * @param hEvent
     *  A handle to the event object. The CreateEvent or OpenEvent function returns this handle.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean SetEvent(HANDLE hEvent);

    /**
     * Sets the specified event object to the signaled state and then resets it to the nonsignaled 
     * state after releasing the appropriate number of waiting threads.
     * @param hEvent
     *  A handle to the event object. The CreateEvent or OpenEvent function returns this handle. 
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean PulseEvent(HANDLE hEvent);

    /**
     * Creates or opens a named or unnamed file mapping object for a specified file.
     * @param hFile
     *  A handle to the file from which to create a file mapping object. 
     * @param lpAttributes
     *  A pointer to a SECURITY_ATTRIBUTES structure that determines whether a returned handle can be inherited by child processes. The lpSecurityDescriptor member of the SECURITY_ATTRIBUTES structure specifies a security descriptor for a new file mapping object. 
     * @param flProtect
     *  Specifies the page protection of the file mapping object. All mapped views of the object must be compatible with this protection. 
     * @param dwMaximumSizeHigh
     *  The high-order DWORD of the maximum size of the file mapping object.
     * @param dwMaximumSizeLow
     *  The low-order DWORD of the maximum size of the file mapping object. 
     * @param lpName
     *  The name of the file mapping object. 
     * @return
     *  If the function succeeds, the return value is a handle to the newly created file mapping object.
     *  If the object exists before the function call, the function returns a handle to the existing object (with its current size, not the specified size), and GetLastError returns ERROR_ALREADY_EXISTS. 
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
     */
    HANDLE CreateFileMapping(HANDLE hFile, WinNT.SECURITY_ATTRIBUTES lpAttributes,
                             int flProtect, int dwMaximumSizeHigh,
                             int dwMaximumSizeLow, String lpName);

    /**
     * Maps a view of a file mapping into the address space of a calling process.
     * @param hFileMappingObject
     *  A handle to a file mapping object. The CreateFileMapping and OpenFileMapping functions return this handle.
     * @param dwDesiredAccess
     *  The type of access to a file mapping object, which determines the protection of the pages.
     * @param dwFileOffsetHigh
     *  A high-order DWORD of the file offset where the view begins.
     * @param dwFileOffsetLow
     *  A low-order DWORD of the file offset where the view is to begin.
     * @param dwNumberOfBytesToMap
     *  The number of bytes of a file mapping to map to the view. 
     * @return
     *  If the function succeeds, the return value is the starting address of the mapped view.
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
     */
    Pointer MapViewOfFile(HANDLE hFileMappingObject, int dwDesiredAccess,
                          int dwFileOffsetHigh, int dwFileOffsetLow,
                          int dwNumberOfBytesToMap);

    /**
     * Unmaps a mapped view of a file from the calling process's address space.
     * @param lpBaseAddress
     *  A pointer to the base address of the mapped view of a file that is to be unmapped. 
     * @return
     *  If the function succeeds, the return value is the starting address of the mapped view.
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
     */
    boolean UnmapViewOfFile(Pointer lpBaseAddress);
    
    /**
     * Retrieves only the NetBIOS name of the local computer.
     * 
     * @param buffer 
     * 	A pointer to a buffer that receives the computer name or the cluster virtual server
     *  name. The buffer size should be large enough to contain MAX_COMPUTERNAME_LENGTH + 1
     *  characters.
     * @param lpnSize
     * 	On input, specifies the size of the buffer, in TCHARs. On output, the number of TCHARs 
     * 	copied to the destination buffer, not including the terminating null character. If 
     *  the buffer is too small, the function fails and GetLastError returns 
     *  ERROR_BUFFER_OVERFLOW. The lpnSize parameter specifies the size of the buffer required,
     *  including the terminating null character.
     * @return
     * 	If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    public boolean GetComputerName(char[] buffer, IntByReference lpnSize);
    
    /**
     * The OpenThread function opens an existing thread object.
     * @param dwDesiredAccess
     *  Access to the thread object. This access right is checked against any security 
     *  descriptor for the thread.
     * @param bInheritHandle
     *  If this parameter is TRUE, the new process inherits the handle. If the parameter 
     *  is FALSE, the handle is not inherited. 
     * @param dwThreadId
     *  Identifier of the thread to be opened. 
     * @return
     *  If the function succeeds, the return value is an open handle to the specified process.
     *  If the function fails, the return value is NULL. To get extended error information, 
     *  call GetLastError.
     */
    HANDLE OpenThread(int dwDesiredAccess, boolean bInheritHandle, int dwThreadId);
    
    /**
     * This function returns a handle to an existing process object.
     * @param fdwAccess
     *  Not supported; set to zero. 
     * @param fInherit
     *  Not supported; set to FALSE. 
     * @param IDProcess
     *  Specifies the process identifier of the process to open. 
     * @return
     *  An open handle to the specified process indicates success. 
     *  NULL indicates failure. 
     *  To get extended error information, call GetLastError. 
     */
    HANDLE OpenProcess(int fdwAccess, boolean fInherit, int IDProcess);    

    /**
     * The GetTempPath function retrieves the path of the directory designated 
     * for temporary files.
     * @param nBufferLength
     *  Size of the string buffer identified by lpBuffer, in TCHARs.
     * @param buffer
     *  Pointer to a string buffer that receives the null-terminated string specifying the 
     *  temporary file path. The returned string ends with a backslash, for example, 
     *  C:\TEMP\. 
     * @return
     *  If the function succeeds, the return value is the length, in TCHARs, of the string 
     *  copied to lpBuffer, not including the terminating null character. If the return value 
     *  is greater than nBufferLength, the return value is the length, in TCHARs, of the 
     *  buffer required to hold the path.
     *  
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    DWORD GetTempPath(DWORD nBufferLength, char[] buffer);
    
    /**
     * The GetVersion function returns the current version number of the operating system.
     * @return
     *  If the function succeeds, the return value includes the major and minor version numbers 
     *  of the operating system in the low order word, and information about the operating system 
     *  platform in the high order word.
     */
    DWORD GetVersion();

    /**
     * The GetVersionEx function obtains extended information about the version of the operating 
     * system that is currently running.
     * @param lpVersionInfo
     *  Pointer to an OSVERSIONINFO data structure that the function fills with operating system 
     *  version information. 
     * @return
     *  If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError. The function fails if you specify an invalid value for the 
     *  dwOSVersionInfoSize member of the OSVERSIONINFO or OSVERSIONINFOEX structure.
     */
    boolean GetVersionEx(OSVERSIONINFO lpVersionInfo);
    
    /**
     * The GetVersionEx function obtains extended information about the version of the operating 
     * system that is currently running.
     * @param lpVersionInfo
     *  Pointer to an OSVERSIONINFOEX data structure that the function fills with operating system 
     *  version information. 
     * @return
     *  If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError. The function fails if you specify an invalid value for the 
     *  dwOSVersionInfoSize member of the OSVERSIONINFO or OSVERSIONINFOEX structure.
     */
    boolean GetVersionEx(OSVERSIONINFOEX lpVersionInfo);
    
    /**
     * The GetSystemInfo function returns information about the current system.
     * @param lpSystemInfo
     *  Pointer to a SYSTEM_INFO structure that receives the information. 
     */
    void GetSystemInfo(SYSTEM_INFO lpSystemInfo);
    
    /**
     * The GetNativeSystemInfo function retrieves information about the current system to an 
     * application running under WOW64. If the function is called from a 64-bit application, 
     * it is equivalent to the GetSystemInfo function.
     * @param lpSystemInfo
     *  Pointer to a SYSTEM_INFO structure that receives the information. 
     */
    void GetNativeSystemInfo(SYSTEM_INFO lpSystemInfo);
    
    /**
     * The IsWow64Process function determines whether the specified process is running under WOW64.
     * @param hProcess
     *  Handle to a process. 
     * @param Wow64Process
     *  Pointer to a value that is set to TRUE if the process is running under WOW64. 
     *  Otherwise, the value is set to FALSE. 
     * @return
     *  If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean IsWow64Process(HANDLE hProcess, IntByReference Wow64Process);
    
    /**
     * Retrieves information about the system's current usage of both physical and virtual memory.
     * @param lpBuffer
     *  A pointer to a MEMORYSTATUSEX structure that receives information about current memory availability.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    boolean GlobalMemoryStatusEx(MEMORYSTATUSEX lpBuffer);
    
    /**
     * The GetLogicalDriveStrings function fills a buffer with strings that specify 
     * valid drives in the system.
     * @param nBufferLength
     *  Maximum size of the buffer pointed to by lpBuffer, in TCHARs. This size does not include 
     *  the terminating null character. If this parameter is zero, lpBuffer is not used.
     * @param lpBuffer
     *  Pointer to a buffer that receives a series of null-terminated strings, one for each valid 
     *  drive in the system, plus with an additional null character. Each string is a device name.
     * @return
     *  If the function succeeds, the return value is the length, in characters, of the strings 
     *  copied to the buffer, not including the terminating null character. Note that an ANSI-ASCII 
     *  null character uses one byte, but a Unicode null character uses two bytes.
     *  If the buffer is not large enough, the return value is greater than nBufferLength. It is 
     *  the size of the buffer required to hold the drive strings.
     *  If the function fails, the return value is zero. To get extended error information, use 
     *  the GetLastError function.
     */
    DWORD GetLogicalDriveStrings(DWORD nBufferLength, char[] lpBuffer);
    
    /**
     * The GetDiskFreeSpaceEx function retrieves information about the amount of space that is 
     * available on a disk volume, which is the total amount of space, the total amount of free 
     * space, and the total amount of free space available to the user that is associated with 
     * the calling thread.
     * @param lpDirectoryName
     *  A pointer to a null-terminated string that specifies a directory on a disk. 
     *  If this parameter is NULL, the function uses the root of the current disk. 
     *  If this parameter is a UNC name, it must include a trailing backslash, for example,
     *   \\MyServer\MyShare\.
     *   This parameter does not have to specify the root directory on a disk. The function 
     *   accepts any directory on a disk. 
     * @param lpFreeBytesAvailable
     *   A pointer to a variable that receives the total number of free bytes on a disk that
     *   are available to the user who is associated with the calling thread.  
     *   This parameter can be NULL.
     * @param lpTotalNumberOfBytes
     *  A pointer to a variable that receives the total number of bytes on a disk that are 
     *  available to the user who is associated with the calling thread. 
     *  This parameter can be NULL.
     * @param lpTotalNumberOfFreeBytes
     *  A pointer to a variable that receives the total number of free bytes on a disk. 
     *  This parameter can be NULL.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is 0 (zero). To get extended error information, 
     *  call GetLastError.
     */
    boolean GetDiskFreeSpaceEx(String lpDirectoryName, LARGE_INTEGER.ByReference lpFreeBytesAvailable,
    		LARGE_INTEGER.ByReference lpTotalNumberOfBytes, LARGE_INTEGER.ByReference lpTotalNumberOfFreeBytes);
}
