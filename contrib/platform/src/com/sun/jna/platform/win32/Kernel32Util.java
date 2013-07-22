/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.platform.win32;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.LastErrorException;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Kernel32 utility API.
 * @author dblock[at]dblock.org
 * @author markus[at]headcrashing[dot]eu
 */
public abstract class Kernel32Util implements WinDef {
	
    /**
     * Get current computer NetBIOS name.
     * @return 
     *  Netbios name.
     */
    public static String getComputerName() {
    	char buffer[] = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
    	IntByReference lpnSize = new IntByReference(buffer.length);
    	if (! Kernel32.INSTANCE.GetComputerName(buffer, lpnSize)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}
    	return Native.toString(buffer);
    }
	
    /**
     * Format a message from the value obtained from {@link
     * Kernel32#GetLastError} or {@link Native#getLastError}.
     * @param code
     *  int
     * @return
     *  Formatted message.
     */
    public static String formatMessage(int code) {
        PointerByReference buffer = new PointerByReference();        
        if (0 == Kernel32.INSTANCE.FormatMessage(
                                                 WinBase.FORMAT_MESSAGE_ALLOCATE_BUFFER
                                                 | WinBase.FORMAT_MESSAGE_FROM_SYSTEM 
                                                 | WinBase.FORMAT_MESSAGE_IGNORE_INSERTS, 
                                                 null,
                                                 code,
                                                 0, // TODO: MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT)
                                                 buffer, 
                                                 0, 
                                                 null)) {
            throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
        }	       
    	String s = buffer.getValue().getString(0, ! Boolean.getBoolean("w32.ascii"));
    	Kernel32.INSTANCE.LocalFree(buffer.getValue());
    	return s.trim();		
    }

    /**
     * Format a message from an HRESULT.
     * @param code
     *  HRESULT
     * @return
     *  Formatted message.
     */
    public static String formatMessage(HRESULT code) {
        return formatMessage(code.intValue());
    }

    /** @deprecated use {@link #formatMessage(WinNT.HRESULT)} instead. */
    public static String formatMessageFromHR(HRESULT code) {
        return formatMessage(code.intValue());
    }
	
    /**
     * Format a system message from an error code.
     * @param code
     *  Error code, typically a result of GetLastError.
     * @return
     *  Formatted message.
     */
    public static String formatMessageFromLastErrorCode(int code) {
        return formatMessageFromHR(W32Errors.HRESULT_FROM_WIN32(code));
    }
	
    /**
     * Return the path designated for temporary files.
     * @return
     *  Path.
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
    	if (! Kernel32.INSTANCE.DeleteFile(filename)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}
    }
	
    /**
     * Returns valid drives in the system.
     * @return
     *  An array of valid drives.
     */
    public static String[] getLogicalDriveStrings() {
    	DWORD dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(new DWORD(0), null);
    	if (dwSize.intValue() <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}
    	
    	char buf[] = new char[dwSize.intValue()];
    	dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(dwSize, buf);
    	if (dwSize.intValue() <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}

    	List<String> drives = new ArrayList<String>();    	
    	String drive = "";
    	// the buffer is double-null-terminated
    	for(int i = 0; i < buf.length - 1; i++) {
            if (buf[i] == 0) {
                drives.add(drive);
                drive = "";
            } else {
                drive += buf[i];
            }
    	}
    	return drives.toArray(new String[0]);
    }	
	
    /**
     * Retrieves file system attributes for a specified file or directory.
     * @param fileName
     * 	The name of the file or directory.
     * @return
     *  The attributes of the specified file or directory.
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
     */
    public static int getFileType(String fileName) throws FileNotFoundException {
        File f = new File(fileName);
        if (!f.exists()) {
            throw new FileNotFoundException(fileName);
        }

        HANDLE hFile = null;
        try {
            hFile = Kernel32.INSTANCE.CreateFile(fileName,
                                                 WinNT.GENERIC_READ,
                                                 WinNT.FILE_SHARE_READ,
                                                 new WinBase.SECURITY_ATTRIBUTES(),
                                                 WinNT.OPEN_EXISTING,
                                                 WinNT.FILE_ATTRIBUTE_NORMAL,
                                                 new HANDLEByReference().getValue());

            if (WinBase.INVALID_HANDLE_VALUE.equals(hFile)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            int type = Kernel32.INSTANCE.GetFileType(hFile);
            switch(type) {
            case WinNT.FILE_TYPE_UNKNOWN:
                int err = Kernel32.INSTANCE.GetLastError();
                switch(err) {
                case WinError.NO_ERROR:
                    break;
                default:
                    throw new Win32Exception(err);
                }
                // fall-thru

            default:
                return type;
            }
        } finally {
            if (hFile != null) {
                if (! Kernel32.INSTANCE.CloseHandle(hFile)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            }
        }
    }

    /**
     * @return One of the WinBase.DRIVE_* constants.
     */
    public static int getDriveType(String rootName) {
        return Kernel32.INSTANCE.GetDriveType(rootName);
    }
	
    /**
     * Get the value of an environment variable.
     * @param name
     * 	Name of the environment variable.
     * @return 
     *  Value of an environment variable.
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
        size = Kernel32.INSTANCE.GetEnvironmentVariable(name, buffer, buffer.length);
        if (size <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    	return Native.toString(buffer);
    }

    /**
     * Retrieves an integer associated with a key in the specified section of an initialization file.
     *
     * @param appName
     *            The name of the section in the initialization file.
     * @param keyName
     *            The name of the key whose value is to be retrieved. This value is in the form of a string; the {@link Kernel32#GetPrivateProfileInt} function converts
     *            the string into an integer and returns the integer.
     * @param defaultValue
     *            The default value to return if the key name cannot be found in the initialization file.
     * @param fileName
     *            The name of the initialization file. If this parameter does not contain a full path to the file, the system searches for the file in the
     *            Windows directory.
     * @return The retrieved integer, or the default if not found.
     */
    public static final int getPrivateProfileInt(final String appName, final String keyName, final int defaultValue, final String fileName) {
        return Kernel32.INSTANCE.GetPrivateProfileInt(appName, keyName, defaultValue, fileName);
    }

    /**
     * Retrieves a string from the specified section in an initialization file.
     * 
     * @param lpAppName
     *            The name of the section containing the key name. If this parameter is {@code null}, the {@link Kernel32#GetPrivateProfileString} function copies all
     *            section names in the file to the supplied buffer.
     * @param lpKeyName
     *            The name of the key whose associated string is to be retrieved. If this parameter is {@code null}, all key names in the section specified by
     *            the {@code lpAppName} parameter are returned.
     * @param lpDefault
     *            A default string. If the {@code lpKeyName} key cannot be found in the initialization file, {@link Kernel32#GetPrivateProfileString} returns the
     *            default. If this parameter is {@code null}, the default is an empty string, {@code ""}.
     *            <p>
     *            Avoid specifying a default string with trailing blank characters. The function inserts a {@code null} character in the
     *            {@code lpReturnedString} buffer to strip any trailing blanks.
     *            </p>
     * @param lpFileName
     *            The name of the initialization file. If this parameter does not contain a full path to the file, the system searches for the file in the
     *            Windows directory.
     * @return <p>
     *         If neither {@code lpAppName} nor {@code lpKeyName} is {@code null} and the destination buffer is too small to hold the requested string, the
     *         string is truncated.
     *         </p>
     *         <p>
     *         If either {@code lpAppName} or {@code lpKeyName} is {@code null} and the destination buffer is too small to hold all the strings, the last string
     *         is truncated and followed by two {@code null} characters.
     *         </p>
     *         <p>
     *         In the event the initialization file specified by {@code lpFileName} is not found, or contains invalid values, this function will set errorno
     *         with a value of '0x2' (File Not Found). To retrieve extended error information, call {@link Kernel32#GetLastError}.
     *         </p>
     */
    public static final String getPrivateProfileString(final String lpAppName, final String lpKeyName, final String lpDefault, final String lpFileName) {
        final char buffer[] = new char[1024];
        Kernel32.INSTANCE.GetPrivateProfileString(lpAppName, lpKeyName, lpDefault, buffer, new DWORD(buffer.length), lpFileName);
        return Native.toString(buffer);
    }

    public static final void writePrivateProfileString(final String appName, final String keyName, final String string, final String fileName) {
        if (!Kernel32.INSTANCE.WritePrivateProfileString(appName, keyName, string, fileName))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
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
    public static final List<String> getPrivateProfileSection(final String appName, final String fileName) {
        final char buffer[] = new char[32768]; // Maximum section size according to MSDN (http://msdn.microsoft.com/en-us/library/windows/desktop/ms724348(v=vs.85).aspx)
        if (Kernel32.INSTANCE.GetPrivateProfileSection(appName, buffer, new DWORD(buffer.length), fileName).intValue() == 0)
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        return asList(Native.toStrings(buffer));
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
    public static final List<String> getPrivateProfileSectionNames(final String fileName) {
        final char buffer[] = new char[65536]; // Maximum INI file size according to MSDN (http://support.microsoft.com/kb/78346)
        if (Kernel32.INSTANCE.GetPrivateProfileSectionNames(buffer, new DWORD(buffer.length), fileName).intValue() == 0)
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        return asList(Native.toStrings(buffer));
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
    public static final void writePrivateProfileSection(final String appName, final List<String> strings, final String fileName) {
        final StringBuilder buffer = new StringBuilder();
        for (final String string : strings)
            buffer.append(string).append('\0');
        buffer.append('\0');
        if (!(Kernel32.INSTANCE.WritePrivateProfileSection(appName, buffer.toString(), fileName)))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    }
    
    /**
     * Convenience method to get the processor information. Takes care of auto-growing the array.
     *
     * @return the array of processor information.
     */
    public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] getLogicalProcessorInformation()
    {
        int sizePerStruct = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION().size();
        WinDef.DWORDByReference bufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(sizePerStruct));
        Memory memory;
        while (true)
        {
            memory = new Memory(bufferSize.getValue().intValue());
            if (! Kernel32.INSTANCE.GetLogicalProcessorInformation(memory, bufferSize))
            {
                int err = Kernel32.INSTANCE.GetLastError();
                if (err != WinError.ERROR_INSUFFICIENT_BUFFER)
                	throw new Win32Exception(err);
            }
            else
            {
                break;
            }
        }
        WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION firstInformation = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION(memory);
        int returnedStructCount = bufferSize.getValue().intValue() / sizePerStruct;
        return (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[]) firstInformation.toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[returnedStructCount]);
    }
}
