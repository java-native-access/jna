/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sun.jna.LastErrorException;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Kernel32 utility API.
 *
 * @author dblock[at]dblock.org
 * @author markus[at]headcrashing[dot]eu
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public abstract class Kernel32Util implements WinDef {

    /**
     * Get current computer NetBIOS name.
     *
     * @return Netbios name.
     */
    public static String getComputerName() {
        char buffer[] = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
        IntByReference lpnSize = new IntByReference(buffer.length);
        if (!Kernel32.INSTANCE.GetComputerName(buffer, lpnSize)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString(buffer);
    }

    /**
     * Invokes {@link Kernel32#LocalFree(Pointer)} and checks if it succeeded.
     *
     * @param ptr The {@link Pointer} to the memory to be released - ignored if NULL
     * @throws Win32Exception if non-{@code ERROR_SUCCESS} code reported
     */
    public static void freeLocalMemory(Pointer ptr) {
        Pointer res = Kernel32.INSTANCE.LocalFree(ptr);
        if (res != null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    /**
     * Invokes {@link Kernel32#GlobalFree(Pointer)} and checks if it succeeded.
     *
     * @param ptr The {@link Pointer} to the memory to be released - ignored if NULL
     * @throws Win32Exception if non-{@code ERROR_SUCCESS} code reported
     */
    public static void freeGlobalMemory(Pointer ptr) {
        Pointer res = Kernel32.INSTANCE.GlobalFree(ptr);
        if (res != null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    /**
     * Closes all referenced handles. If an exception is thrown for
     * a specific handle, then it is accumulated until all
     * handles have been closed. If more than one exception occurs,
     * then it is added as a suppressed exception to the first one.
     * Once closed all handles, the accumulated exception (if any) is thrown
     *
     * @param refs The references to close
     * @see #closeHandleRef(WinNT.HANDLEByReference)
     */
    public static void closeHandleRefs(HANDLEByReference... refs) {
        Win32Exception err = null;
        for (HANDLEByReference r : refs) {
            try {
                closeHandleRef(r);
            } catch(Win32Exception e) {
                if (err == null) {
                    err = e;
                } else {
                    err.addSuppressedReflected(e);
                }
            }
        }

        if (err != null) {
            throw err;
        }
    }
    /**
     * Closes the handle in the reference
     *
     * @param ref The handle reference - ignored if {@code null}
     * @see #closeHandle(WinNT.HANDLE)
     */
    public static void closeHandleRef(HANDLEByReference ref) {
        closeHandle((ref == null) ? null : ref.getValue());
    }

    /**
     * Invokes {@link #closeHandle(WinNT.HANDLE)} on each handle. If an exception
     * is thrown for a specific handle, then it is accumulated until all
     * handles have been closed. If more than one exception occurs, then it
     * is added as a suppressed exception to the first one. Once closed all
     * handles, the accumulated exception (if any) is thrown
     *
     * @param handles The handles to be closed
     * @see Throwable#getSuppressed()
     */
    public static void closeHandles(HANDLE... handles) {
        Win32Exception err = null;
        for (HANDLE h : handles) {
            try {
                closeHandle(h);
            } catch(Win32Exception e) {
                if (err == null) {
                    err = e;
                } else {
                    err.addSuppressedReflected(e);
                }
            }
        }

        if (err != null) {
            throw err;
        }
    }

    /**
     * Invokes {@link Kernel32#CloseHandle(WinNT.HANDLE)} and checks the success code.
     * If not successful, then throws a {@link Win32Exception} with the
     * {@code GetLastError} value
     *
     * @param h The handle to be closed - ignored if {@code null}
     */
    public static void closeHandle(HANDLE h) {
        if (h == null) {
            return;
        }

        if (!Kernel32.INSTANCE.CloseHandle(h)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    /**
     * Format a message from the value obtained from
     * {@link Kernel32#GetLastError()} or {@link Native#getLastError()}.
     *
     * @param code The error code
     * @return Formatted message.
     */
    public static String formatMessage(int code) {
        PointerByReference buffer = new PointerByReference();
        int nLen = Kernel32.INSTANCE.FormatMessage(
                WinBase.FORMAT_MESSAGE_ALLOCATE_BUFFER
                | WinBase.FORMAT_MESSAGE_FROM_SYSTEM
                | WinBase.FORMAT_MESSAGE_IGNORE_INSERTS,
                null,
                code,
                0, // TODO: // MAKELANGID(LANG_NEUTRAL,SUBLANG_DEFAULT)
                buffer, 0, null);
        if (nLen == 0) {
            throw new LastErrorException(Native.getLastError());
        }

        Pointer ptr = buffer.getValue();
        try {
            String s = ptr.getWideString(0);
            return s.trim();
        } finally {
            freeLocalMemory(ptr);
        }
    }

    /**
     * Format a message from an HRESULT.
     *
     * @param code
     *            HRESULT
     * @return Formatted message.
     */
    public static String formatMessage(HRESULT code) {
        return formatMessage(code.intValue());
    }

    /**
     * Format a system message from an error code.
     *
     * @param code
     *            Error code, typically a result of GetLastError.
     * @return Formatted message.
     */
    public static String formatMessageFromLastErrorCode(int code) {
        return formatMessage(W32Errors.HRESULT_FROM_WIN32(code));
    }

    /**
     * @return Obtains the human-readable error message text from the last error
     *         that occurred by invocating {@code Kernel32.GetLastError()}.
     */
    public static String getLastErrorMessage() {
        return Kernel32Util.formatMessageFromLastErrorCode(Kernel32.INSTANCE
                .GetLastError());
    }

    /**
     * Return the path designated for temporary files.
     *
     * @return Path.
     */
    public static String getTempPath() {
        DWORD nBufferLength = new DWORD(WinDef.MAX_PATH);
        char[] buffer = new char[nBufferLength.intValue()];
        if (Kernel32.INSTANCE.GetTempPath(nBufferLength, buffer).intValue() == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString(buffer);
    }

    public static void deleteFile(String filename) {
        if (!Kernel32.INSTANCE.DeleteFile(filename)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    /**
     * Returns valid drives in the system.
     *
     * @return A {@link List} of valid drives.
     */
    public static List<String> getLogicalDriveStrings() {
        DWORD dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(new DWORD(0), null);
        if (dwSize.intValue() <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        char buf[] = new char[dwSize.intValue()];
        dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(dwSize, buf);
        int bufSize = dwSize.intValue();
        if (bufSize <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        return Native.toStringList(buf, 0, bufSize);
    }

    /**
     * Retrieves file system attributes for a specified file or directory.
     *
     * @param fileName
     *            The name of the file or directory.
     * @return The attributes of the specified file or directory.
     */
    public static int getFileAttributes(String fileName) {
        int fileAttributes = Kernel32.INSTANCE.GetFileAttributes(fileName);
        if (fileAttributes == WinBase.INVALID_FILE_ATTRIBUTES) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return fileAttributes;
    }

    /**
     * Retrieves the result of GetFileType, provided the file exists.
     * @param fileName file name
     * @return file type
     * @throws FileNotFoundException if file not found
     */
    public static int getFileType(String fileName) throws FileNotFoundException {
        File f = new File(fileName);
        if (!f.exists()) {
            throw new FileNotFoundException(fileName);
        }

        HANDLE hFile = null;
        Win32Exception err = null;
        try {
            hFile = Kernel32.INSTANCE.CreateFile(fileName, WinNT.GENERIC_READ,
                    WinNT.FILE_SHARE_READ, new WinBase.SECURITY_ATTRIBUTES(),
                    WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL,
                    new HANDLEByReference().getValue());

            if (WinBase.INVALID_HANDLE_VALUE.equals(hFile)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            int type = Kernel32.INSTANCE.GetFileType(hFile);
            switch (type) {
                case WinNT.FILE_TYPE_UNKNOWN:
                    int rc = Kernel32.INSTANCE.GetLastError();
                    switch (rc) {
                        case WinError.NO_ERROR:
                            break;
                        default:
                            throw new Win32Exception(rc);
                    }
                // fall-thru

            default:
                return type;
            }
        } catch(Win32Exception e) {
            err = e;
            throw err;  // re-throw so finally block executed
        } finally {
            try {
                closeHandle(hFile);
            } catch(Win32Exception e) {
                if (err == null) {
                    err = e;
                } else {
                    err.addSuppressedReflected(e);
                }
            }

            if (err != null) {
                throw err;
            }
        }
    }

    /**
     * @param rootName name of root drive
     * @return One of the WinBase.DRIVE_* constants.
     */
    public static int getDriveType(String rootName) {
        return Kernel32.INSTANCE.GetDriveType(rootName);
    }

    /**
     * Get the value of an environment variable.
     *
     * @param name
     *            Name of the environment variable.
     * @return Value of an environment variable.
     */
    public static String getEnvironmentVariable(String name) {
        // obtain the buffer size
        int size = Kernel32.INSTANCE.GetEnvironmentVariable(name, null, 0);
        if (size == 0) {
            return null;
        } else if (size < 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        // obtain the value
        char[] buffer = new char[size];
        size = Kernel32.INSTANCE.GetEnvironmentVariable(name, buffer,
                buffer.length);
        if (size <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString(buffer);
    }

    /**
     * Uses the {@link Kernel32#GetEnvironmentStrings()} to retrieve and
     * parse the current process environment
     * @return The current process environment as a {@link Map}.
     * @throws LastErrorException if failed to get or free the environment
     * data block
     * @see #getEnvironmentVariables(Pointer, long)
     */
    public static Map<String,String> getEnvironmentVariables() {
        Pointer lpszEnvironmentBlock=Kernel32.INSTANCE.GetEnvironmentStrings();
        if (lpszEnvironmentBlock == null) {
            throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
        }

        try {
            return getEnvironmentVariables(lpszEnvironmentBlock, 0L);
        } finally {
            if (!Kernel32.INSTANCE.FreeEnvironmentStrings(lpszEnvironmentBlock)) {
                throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
            }
        }
    }

    /**
     * @param lpszEnvironmentBlock The environment block as received from the
     * <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/ms683187(v=vs.85).aspx">GetEnvironmentStrings</A>
     * function
     * @param offset Offset within the block to parse the data
     * @return A {@link Map} of the parsed <code>name=value</code> pairs.
     * <B>Note:</B> if the environment block is {@code null} then {@code null}
     * is returned instead of an empty map since we want to distinguish
     * between the case that the data block is {@code null} and when there are
     * no environment variables (as unlikely as it may be)
     */
    public static Map<String,String> getEnvironmentVariables(Pointer lpszEnvironmentBlock, long offset) {
        if (lpszEnvironmentBlock == null) {
            return null;
        }
        
        Map<String,String>  vars=new TreeMap<String,String>();
        boolean             asWideChars=isWideCharEnvironmentStringBlock(lpszEnvironmentBlock, offset);
        long                stepFactor=asWideChars ? 2L : 1L;
        for (long    curOffset=offset; ; ) {
            String  nvp=readEnvironmentStringBlockEntry(lpszEnvironmentBlock, curOffset, asWideChars);
            int     len=nvp.length();
            if (len == 0) { // found the ending '\0'
                break;
            }

            int pos=nvp.indexOf('=');
            if (pos < 0) {
                throw new IllegalArgumentException("Missing variable value separator in " + nvp);
            }

            String  name=nvp.substring(0, pos), value=nvp.substring(pos + 1);
            vars.put(name, value);

            curOffset += (len + 1 /* skip the ending '\0' */) * stepFactor;
        }

        return vars;
    }

    /**
     * @param lpszEnvironmentBlock The environment block as received from the
     * <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/ms683187(v=vs.85).aspx">GetEnvironmentStrings</A>
     * function
     * @param offset Offset within the block to look for the entry
     * @param asWideChars If {@code true} then the block contains {@code wchar_t}
     * instead of &quot;plain old&quot; {@code char}s
     * @return A {@link String} containing the <code>name=value</code> pair or
     * empty if reached end of block
     * @see #isWideCharEnvironmentStringBlock
     * @see #findEnvironmentStringBlockEntryEnd
     */
    public static String readEnvironmentStringBlockEntry(Pointer lpszEnvironmentBlock, long offset, boolean asWideChars) {
        long endOffset=findEnvironmentStringBlockEntryEnd(lpszEnvironmentBlock, offset, asWideChars);
        int  dataLen=(int) (endOffset - offset);
        if (dataLen == 0) {
            return "";
        }

        int         charsLen=asWideChars ? (dataLen / 2) : dataLen;
        char[]      chars=new char[charsLen];
        long        curOffset=offset, stepSize=asWideChars ? 2L : 1L;
        ByteOrder   byteOrder=ByteOrder.nativeOrder();
        for (int index=0; index < chars.length; index++, curOffset += stepSize) {
            byte b=lpszEnvironmentBlock.getByte(curOffset);
            if (asWideChars) {
                byte x=lpszEnvironmentBlock.getByte(curOffset + 1L);
                if (ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
                    chars[index] = (char) (((x << Byte.SIZE) & 0xFF00) | (b & 0x00FF));
                } else {    // unlikely, but handle it
                    chars[index] = (char) (((b << Byte.SIZE) & 0xFF00) | (x & 0x00FF));
                }
            } else {
                chars[index] = (char) (b & 0x00FF);
            }
        }

        return new String(chars);
    }

    /**
     * @param lpszEnvironmentBlock The environment block as received from the
     * <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/ms683187(v=vs.85).aspx">GetEnvironmentStrings</A>
     * function
     * @param offset Offset within the block to look for the entry
     * @param asWideChars If {@code true} then the block contains {@code wchar_t}
     * instead of &quot;plain old&quot; {@code char}s
     * @return The offset of the <U>first</U> {@code '\0'} in the data block
     * starting at the specified offset - can be the start offset itself if empty
     * string.
     * @see #isWideCharEnvironmentStringBlock
     */
    public static long findEnvironmentStringBlockEntryEnd(Pointer lpszEnvironmentBlock, long offset, boolean asWideChars) {
        for (long curOffset=offset, stepSize=asWideChars ? 2L : 1L; ; curOffset += stepSize) {
            byte b=lpszEnvironmentBlock.getByte(curOffset);
            if (b == 0) {
                return curOffset;
            }
        }
    }

    /**
     * <P>Attempts to determine whether the data block uses {@code wchar_t}
     * instead of &quot;plain old&quot; {@code char}s. It does that by reading
     * 2 bytes from the specified offset - the character value and its charset
     * indicator - and examining them as follows:</P>
     * <UL>
     *      <LI>
     *      If the charset indicator is non-zero then it is assumed to be
     *      a &quot;plain old&quot; {@code char}s data block. <B>Note:</B>
     *      the assumption is that the environment variable <U>name</U> (at
     *      least) is ASCII.
     *      </LI>
     *
     *      <LI>
     *      Otherwise (i.e., zero charset indicator), it is assumed to be
     *      a {@code wchar_t}
     *      </LI>
     * </UL>
     * <B>Note:</B> the code takes into account the {@link ByteOrder} even though
     * only {@link ByteOrder#LITTLE_ENDIAN} is the likely one
     * @param lpszEnvironmentBlock The environment block as received from the
     * <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/ms683187(v=vs.85).aspx">GetEnvironmentStrings</A>
     * function
     * @param offset offset
     * @return {@code true} if the block contains {@code wchar_t} instead of
     * &quot;plain old&quot; {@code char}s
     */
    public static boolean isWideCharEnvironmentStringBlock(Pointer lpszEnvironmentBlock, long offset) {
        byte        b0=lpszEnvironmentBlock.getByte(offset);
        byte        b1=lpszEnvironmentBlock.getByte(offset + 1L);
        ByteOrder   byteOrder=ByteOrder.nativeOrder();
        if (ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
            return isWideCharEnvironmentStringBlock(b1);
        } else {
            return isWideCharEnvironmentStringBlock(b0);
        }
    }

    private static boolean isWideCharEnvironmentStringBlock(byte charsetIndicator) {
        // assume wchar_t for environment variables represents ASCII letters
        if (charsetIndicator != 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Retrieves an integer associated with a key in the specified section of an
     * initialization file.
     *
     * @param appName
     *            The name of the section in the initialization file.
     * @param keyName
     *            The name of the key whose value is to be retrieved. This value
     *            is in the form of a string; the
     *            {@link Kernel32#GetPrivateProfileInt} function converts the
     *            string into an integer and returns the integer.
     * @param defaultValue
     *            The default value to return if the key name cannot be found in
     *            the initialization file.
     * @param fileName
     *            The name of the initialization file. If this parameter does
     *            not contain a full path to the file, the system searches for
     *            the file in the Windows directory.
     * @return The retrieved integer, or the default if not found.
     */
    public static final int getPrivateProfileInt(final String appName,
            final String keyName, final int defaultValue, final String fileName) {
        return Kernel32.INSTANCE.GetPrivateProfileInt(appName, keyName,
                defaultValue, fileName);
    }

    /**
     * Retrieves a string from the specified section in an initialization file.
     *
     * @param lpAppName
     *            The name of the section containing the key name. If this
     *            parameter is {@code null}, the
     *            {@link Kernel32#GetPrivateProfileString} function copies all
     *            section names in the file to the supplied buffer.
     * @param lpKeyName
     *            The name of the key whose associated string is to be
     *            retrieved. If this parameter is {@code null}, all key names in
     *            the section specified by the {@code lpAppName} parameter are
     *            returned.
     * @param lpDefault
     *            A default string. If the {@code lpKeyName} key cannot be found
     *            in the initialization file,
     *            {@link Kernel32#GetPrivateProfileString} returns the default.
     *            If this parameter is {@code null}, the default is an empty
     *            string, {@code ""}.
     *            <p>
     *            Avoid specifying a default string with trailing blank
     *            characters. The function inserts a {@code null} character in
     *            the {@code lpReturnedString} buffer to strip any trailing
     *            blanks.
     *            </p>
     * @param lpFileName
     *            The name of the initialization file. If this parameter does
     *            not contain a full path to the file, the system searches for
     *            the file in the Windows directory.
     * @return <p>
     *         If neither {@code lpAppName} nor {@code lpKeyName} is
     *         {@code null} and the destination buffer is too small to hold the
     *         requested string, the string is truncated.
     *         </p>
     *         <p>
     *         If either {@code lpAppName} or {@code lpKeyName} is {@code null}
     *         and the destination buffer is too small to hold all the strings,
     *         the last string is truncated and followed by two {@code null}
     *         characters.
     *         </p>
     *         <p>
     *         In the event the initialization file specified by
     *         {@code lpFileName} is not found, or contains invalid values, this
     *         function will set errorno with a value of '0x2' (File Not Found).
     *         To retrieve extended error information, call
     *         {@link Kernel32#GetLastError}.
     *         </p>
     */
    public static final String getPrivateProfileString(final String lpAppName,
            final String lpKeyName, final String lpDefault,
            final String lpFileName) {
        final char buffer[] = new char[1024];
        Kernel32.INSTANCE.GetPrivateProfileString(lpAppName, lpKeyName,
                lpDefault, buffer, new DWORD(buffer.length), lpFileName);
        return Native.toString(buffer);
    }

    public static final void writePrivateProfileString(final String appName,
            final String keyName, final String string, final String fileName) {
        if (!Kernel32.INSTANCE.WritePrivateProfileString(appName, keyName,
                string, fileName))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    }

    /**
     * Convenience method to get the processor information. Takes care of
     * auto-growing the array.
     *
     * @return the array of processor information.
     */
    public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] getLogicalProcessorInformation() {
        int sizePerStruct = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION()
                .size();
        WinDef.DWORDByReference bufferSize = new WinDef.DWORDByReference(
                new WinDef.DWORD(sizePerStruct));
        Memory memory;
        while (true) {
            memory = new Memory(bufferSize.getValue().intValue());
            if (!Kernel32.INSTANCE.GetLogicalProcessorInformation(memory,
                    bufferSize)) {
                int err = Kernel32.INSTANCE.GetLastError();
                if (err != WinError.ERROR_INSUFFICIENT_BUFFER)
                    throw new Win32Exception(err);
            } else {
                break;
            }
        }
        WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION firstInformation = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION(
                memory);
        int returnedStructCount = bufferSize.getValue().intValue()
                / sizePerStruct;
        return (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[]) firstInformation
                .toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[returnedStructCount]);
    }

    /**
     * Retrieves all the keys and values for the specified section of an initialization file.
     *
     * <p>
     * Each string has the following format: {@code key=string}.
     * </p>
     * <p>
     * This operation is atomic; no updates to the specified initialization file are allowed while this method is executed.
     * </p>
     *
     * @param appName
     *            The name of the section in the initialization file.
     * @param fileName
     *            The name of the initialization file. If this parameter does not contain a full path to the file, the system searches for the file in the
     *            Windows directory.
     * @return The key name and value pairs associated with the named section.
     */
    public static final String[] getPrivateProfileSection(final String appName, final String fileName) {
        final char buffer[] = new char[32768]; // Maximum section size according to MSDN (http://msdn.microsoft.com/en-us/library/windows/desktop/ms724348(v=vs.85).aspx)
        if (Kernel32.INSTANCE.GetPrivateProfileSection(appName, buffer, new DWORD(buffer.length), fileName).intValue() == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return new String(buffer).split("\0");
    }

    /**
     * Retrieves the names of all sections in an initialization file.
     * <p>
     * This operation is atomic; no updates to the initialization file are allowed while this method is executed.
     * </p>
     *
     * @param fileName
     *            The name of the initialization file. If this parameter is {@code NULL}, the function searches the Win.ini file. If this parameter does not
     *            contain a full path to the file, the system searches for the file in the Windows directory.
     * @return the section names associated with the named file.
     */
    public static final String[] getPrivateProfileSectionNames(final String fileName) {
        final char buffer[] = new char[65536]; // Maximum INI file size according to MSDN (http://support.microsoft.com/kb/78346)
        if (Kernel32.INSTANCE.GetPrivateProfileSectionNames(buffer, new DWORD(buffer.length), fileName).intValue() == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return new String(buffer).split("\0");
    }

    /**
     * @param appName
     *            The name of the section in which data is written. This section name is typically the name of the calling application.
     * @param strings
     *            The new key names and associated values that are to be written to the named section. Each entry must be of the form {@code key=value}.
     * @param fileName
     *            The name of the initialization file. If this parameter does not contain a full path for the file, the function searches the Windows directory
     *            for the file. If the file does not exist and lpFileName does not contain a full path, the function creates the file in the Windows directory.
     */
    public static final void writePrivateProfileSection(final String appName, final String[] strings, final String fileName) {
        final StringBuilder buffer = new StringBuilder();
        for (final String string : strings)
            buffer.append(string).append('\0');
        buffer.append('\0');
        if (! Kernel32.INSTANCE.WritePrivateProfileSection(appName, buffer.toString(), fileName)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    /**
     * Invokes the {@link Kernel32#QueryDosDevice(String, char[], int)} method
     * and parses the result
     * @param lpszDeviceName The device name
     * @param maxTargetSize The work buffer size to use for the query
     * @return The parsed result
     */
    public static final List<String> queryDosDevice(String lpszDeviceName, int maxTargetSize) {
        char[] lpTargetPath = new char[maxTargetSize];
        int dwSize = Kernel32.INSTANCE.QueryDosDevice(lpszDeviceName, lpTargetPath, lpTargetPath.length);
        if (dwSize == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        return Native.toStringList(lpTargetPath, 0, dwSize);
    }

    /**
     * Invokes and parses the result of {@link Kernel32#GetVolumePathNamesForVolumeName(String, char[], int, IntByReference)}
     * @param lpszVolumeName The volume name
     * @return The parsed result
     * @throws Win32Exception If failed to retrieve the required information
     */
    public static final List<String> getVolumePathNamesForVolumeName(String lpszVolumeName) {
        char[] lpszVolumePathNames = new char[WinDef.MAX_PATH + 1];
        IntByReference lpcchReturnLength = new IntByReference();

        if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(lpszVolumeName, lpszVolumePathNames, lpszVolumePathNames.length, lpcchReturnLength)) {
            int hr = Kernel32.INSTANCE.GetLastError();
            if (hr != WinError.ERROR_MORE_DATA) {
                throw new Win32Exception(hr);
            }

            int required = lpcchReturnLength.getValue();
            lpszVolumePathNames = new char[required];
            // this time we MUST succeed
            if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(lpszVolumeName, lpszVolumePathNames, lpszVolumePathNames.length, lpcchReturnLength)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }

        int bufSize = lpcchReturnLength.getValue();
        return Native.toStringList(lpszVolumePathNames, 0, bufSize);
    }

    // prefix and suffix of a volume GUID path
    public static final String VOLUME_GUID_PATH_PREFIX = "\\\\?\\Volume{";
    public static final String VOLUME_GUID_PATH_SUFFIX = "}\\";

    /**
     * Parses and returns the pure GUID value of a volume name obtained
     * from {@link Kernel32#FindFirstVolume(char[], int)} or
     * {@link Kernel32#FindNextVolume} calls
     *
     * @param volumeGUIDPath
     *              The volume GUID path as returned by one of the above mentioned calls
     * @return The pure GUID value after stripping the &quot;\\?\&quot; prefix and
     * removing the trailing backslash.
     * @throws IllegalArgumentException if bad format encountered
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa365248(v=vs.85).aspx">Naming a Volume</A>
     */
    public static final String extractVolumeGUID(String volumeGUIDPath) {
        if ((volumeGUIDPath == null)
         || (volumeGUIDPath.length() <= (VOLUME_GUID_PATH_PREFIX.length() + VOLUME_GUID_PATH_SUFFIX.length()))
         || (!volumeGUIDPath.startsWith(VOLUME_GUID_PATH_PREFIX))
         || (!volumeGUIDPath.endsWith(VOLUME_GUID_PATH_SUFFIX))) {
            throw new IllegalArgumentException("Bad volume GUID path format: " + volumeGUIDPath);
        }

        return volumeGUIDPath.substring(VOLUME_GUID_PATH_PREFIX.length(), volumeGUIDPath.length() - VOLUME_GUID_PATH_SUFFIX.length());
    }

    /**
     *
     * This function retrieves the full path of the executable file of a given process.
     *
     * @param hProcess
     *          Handle for the running process
     * @param dwFlags
     *          0 - The name should use the Win32 path format.
     *          1(WinNT.PROCESS_NAME_NATIVE) - The name should use the native system path format.
     *
     * @return the full path of the process's executable file of null if failed. To get extended error information,
     *         call GetLastError.
     */
    public static final String QueryFullProcessImageName(HANDLE hProcess, int dwFlags) {
        char[] path = new char[WinDef.MAX_PATH];
        IntByReference lpdwSize = new IntByReference(path.length);
        if (Kernel32.INSTANCE.QueryFullProcessImageName(hProcess, 0, path, lpdwSize))
            return new String(path).substring(0, lpdwSize.getValue());
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    }

    /**
     * Gets the specified resource out of the specified executable file
     *
     * @param path
     *            The path to the executable file
     * @param type
     *            The type of the resource (either a type name or type ID is
     *            allowed)
     * @param name
     *            The name or ID of the resource
     * @return The resource bytes, or null if no such resource exists.
     * @throws IllegalStateException if the call to LockResource fails
     */
    public static byte[] getResource(String path, String type, String name) {
        HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, null, Kernel32.LOAD_LIBRARY_AS_DATAFILE);

        if (target == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        Win32Exception err = null;
        Pointer start = null;
        int length = 0;
        byte[] results = null;
        try {
            Pointer t = null;
            try {
                t = new Pointer(Long.parseLong(type));
            } catch (NumberFormatException e) {
                t = new Memory(Native.WCHAR_SIZE * (type.length() + 1));
                t.setWideString(0, type);
            }

            Pointer n = null;
            try {
                n = new Pointer(Long.parseLong(name));
            } catch (NumberFormatException e) {
                n = new Memory(Native.WCHAR_SIZE * (name.length() + 1));
                n.setWideString(0, name);
            }

            HRSRC hrsrc = Kernel32.INSTANCE.FindResource(target, n, t);
            if (hrsrc == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            // according to MSDN, on 32 bit Windows or newer, calling FreeResource() is not necessary - and in fact does nothing but return false.
            HANDLE loaded = Kernel32.INSTANCE.LoadResource(target, hrsrc);
            if (loaded == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            length = Kernel32.INSTANCE.SizeofResource(target, hrsrc);
            if (length == 0) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            // MSDN: It is not necessary to unlock resources because the system automatically deletes them when the process that created them terminates.
            // MSDN does not say that LockResource sets GetLastError
            start = Kernel32.INSTANCE.LockResource(loaded);
            if (start == null) {
                throw new IllegalStateException("LockResource returned null.");
            }
            // have to capture it into a byte array before you free the library, otherwise bad things happen.
            results = start.getByteArray(0, length);
        } catch (Win32Exception we) {
            err = we;
        } finally {
            // from what I can tell on MSDN, the only thing that needs cleanup on this is the HMODULE from LoadLibrary
            if (target != null) {
                if (!Kernel32.INSTANCE.FreeLibrary(target)) {
                    Win32Exception we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                    if (err != null) {
                        we.addSuppressedReflected(err);
                    }
                    throw we;
                }
            }
        }

        if (err != null) {
            throw err;
        }

        return results;
    }

    /**
     * Gets a list of all resources from the specified executable file
     *
     * @param path
     *            The path to the executable file
     * @return A map of resource type name/ID =&gt; resources.<br>
     *         A map key + a single list item + the path to the executable can
     *         be handed off to getResource() to actually get the resource.
     */
    public static Map<String, List<String>> getResourceNames(String path) {
        HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, null, Kernel32.LOAD_LIBRARY_AS_DATAFILE);

        if (target == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        final List<String> types = new ArrayList<String>();
        final Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();

        WinBase.EnumResTypeProc ertp = new WinBase.EnumResTypeProc() {

            @Override
            public boolean invoke(HMODULE module, Pointer type, Pointer lParam) {
                // simulate IS_INTRESOURCE macro defined in WinUser.h
                // basically that means that if "type" is less than or equal to 65,535
                // it assumes it's an ID.
                // otherwise it assumes it's a pointer to a string
                if (Pointer.nativeValue(type) <= 65535) {
                    types.add(Pointer.nativeValue(type) + "");
                } else {
                    types.add(type.getWideString(0));
                }
                return true;
            }
        };

        WinBase.EnumResNameProc ernp = new WinBase.EnumResNameProc() {

            @Override
            public boolean invoke(HMODULE module, Pointer type, Pointer name, Pointer lParam) {
                String typeName = "";

                if (Pointer.nativeValue(type) <= 65535) {
                    typeName = Pointer.nativeValue(type) + "";
                } else {
                    typeName = type.getWideString(0);
                }

                if (Pointer.nativeValue(name) < 65535) {
                    result.get(typeName).add(Pointer.nativeValue(name) + "");
                } else {
                    result.get(typeName).add(name.getWideString(0));
                }

                return true;
            }
        };


        Win32Exception err = null;
        try {
            if (!Kernel32.INSTANCE.EnumResourceTypes(target, ertp, null)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            for (final String typeName : types) {
                result.put(typeName, new ArrayList<String>());

                // simulate MAKEINTRESOURCE macro in WinUser.h
                // basically, if the value passed in can be parsed as a number then convert it into one and run with that.
                // otherwise, assume it's a string and construct a pointer to said string.
                Pointer pointer = null;
                try {
                    pointer = new Pointer(Long.parseLong(typeName));
                } catch (NumberFormatException e) {
                    pointer = new Memory(Native.WCHAR_SIZE * (typeName.length() + 1));
                    pointer.setWideString(0, typeName);
                }

                boolean callResult = Kernel32.INSTANCE.EnumResourceNames(target, pointer, ernp, null);

                if (!callResult) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            }
        } catch (Win32Exception e) {
            err = e;
        } finally {
            // from what I can tell on MSDN, the only thing that needs cleanup
            // on this is the HMODULE from LoadLibrary
            if (target != null) {
                if (!Kernel32.INSTANCE.FreeLibrary(target)) {
                    Win32Exception we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                    if (err != null) {
                        we.addSuppressedReflected(err);
                    }
                    throw we;
                }
            }
        }

        if (err != null) {
            throw err;
        }
        return result;
    }

    /**
     * Returns all the executable modules for a given process ID.<br>
     *
     * @param processID
     *            The process ID to get executable modules for
     * @return All the modules in the process.
     */
    public static List<Tlhelp32.MODULEENTRY32W> getModules(int processID) {
        HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new DWORD(processID));
        if (snapshot == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        Win32Exception we = null;
        try {
            Tlhelp32.MODULEENTRY32W first = new Tlhelp32.MODULEENTRY32W();

            if (!Kernel32.INSTANCE.Module32FirstW(snapshot, first)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            List<Tlhelp32.MODULEENTRY32W> modules = new ArrayList<Tlhelp32.MODULEENTRY32W>();
            modules.add(first);

            Tlhelp32.MODULEENTRY32W next = new Tlhelp32.MODULEENTRY32W();
            while (Kernel32.INSTANCE.Module32NextW(snapshot, next)) {
                modules.add(next);
                next = new Tlhelp32.MODULEENTRY32W();
            }

            int lastError = Kernel32.INSTANCE.GetLastError();
            // if we got a false from Module32Next,
            // check to see if it returned false because we're genuinely done
            // or if something went wrong.
            if (lastError != W32Errors.ERROR_SUCCESS && lastError != W32Errors.ERROR_NO_MORE_FILES) {
                throw new Win32Exception(lastError);
            }

            return modules;
        } catch (Win32Exception e) {
            we = e;
            throw we;   // re-throw so finally block is executed
        } finally {
            try {
                closeHandle(snapshot);
            } catch(Win32Exception e) {
                if (we == null) {
                    we = e;
                } else {
                    we.addSuppressedReflected(e);
                }
            }

            if (we != null) {
                throw we;
            }
        }
    }
}
