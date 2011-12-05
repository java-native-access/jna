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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFO;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFOEX;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.nio.Buffer;

/** Interface definitions for <code>kernel32.dll</code>.
 * Includes additional alternate mappings from {@link WinNT} which make use
 * of NIO buffers.
 */
public interface Kernel32 extends WinNT {

    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, 
    		W32APIOptions.UNICODE_OPTIONS);

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
     *  output line. <p/>
     * This version of the function assumes FORMAT_MESSAGE_ALLOCATE_BUFFER is
     *  <em>not</em> set.
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
     *  This this parameter specifies the size of the output buffer, in TCHARs. If FORMAT_MESSAGE_ALLOCATE_BUFFER is 
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
     * Reads data from the specified file or input/output (I/O) device. Reads
     * occur at the position specified by the file pointer if supported by the
     * device.
     * 
     * This function is designed for both synchronous and asynchronous
     * operations. For a similar function designed solely for asynchronous
     * operation, see ReadFileEx
     * 
     * @param hFile A handle to the device (for example, a file, file stream,
     *  physical disk, volume, console buffer, tape drive, socket, communications
     *  resource, mailslot, or pipe).
     * @param lpBuffer A pointer to the buffer that receives the data read from a file or device.
     * @param nNumberOfBytesToRead The maximum number of bytes to be read.
     * @param lpNumberOfBytesRead A pointer to the variable that receives the number of bytes
     *  read when using a synchronous hFile parameter
     * @param lpOverlapped A pointer to an OVERLAPPED structure is required if the hFile
     *  parameter was opened with FILE_FLAG_OVERLAPPED, otherwise it can be NULL.
     * @return If the function succeeds, the return value is nonzero (TRUE).
     *  If the function fails, or is completing asynchronously, the return value is zero (FALSE).
     *  To get extended error information, call the GetLastError function.
     *  
     *  Note  The GetLastError code ERROR_IO_PENDING is not a failure; it designates the read
     *  operation is pending completion asynchronously. For more information, see Remarks.
     */
    boolean ReadFile(
    		HANDLE hFile,
    		Buffer lpBuffer,
    		int nNumberOfBytesToRead,
    		IntByReference lpNumberOfBytesRead,
    		WinBase.OVERLAPPED lpOverlapped);
}
