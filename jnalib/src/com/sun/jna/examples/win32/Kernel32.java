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
package com.sun.jna.examples.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/** Minimal definition of <code>kernel32.dll</code>. */
public interface Kernel32 extends StdCallLibrary, W32Errors {
    
    Kernel32 INSTANCE = (Kernel32)
        Native.loadLibrary("kernel32", Kernel32.class);

    
    public static class SYSTEMTIME extends Structure {
        public short wYear;
        public short wMonth;
        public short wDayOfWeek;
        public short wDay;
        public short wHour;
        public short wMinute;
        public short wSecond;
        public short wMilliseconds;
    }
    
    void GetSystemTime(SYSTEMTIME result);
    int GetCurrentThreadId();
    int GetCurrentProcessId();
    int GetLastError();
    int FORMAT_MESSAGE_FROM_SYSTEM = 0x1000;
    int FORMAT_MESSAGE_IGNORE_INSERTS = 0x200;
    int FormatMessageA(int dwFlags, Pointer lpSource, int dwMessageId, 
                       int dwLanguageId, byte[] lpBuffer, int nSize,
                       Pointer va_list);

    Pointer INVALID_HANDLE_VALUE = Pointer.PM1;
    
    int FILE_LIST_DIRECTORY = 0x00000001;
    
    int FILE_SHARE_READ = 1;
    int FILE_SHARE_WRITE = 2;
    int FILE_SHARE_DELETE = 4;
    
    int CREATE_ALWAYS = 2;
    int OPEN_EXISTING = 3; 
    int OPEN_ALWAYS = 4;
    
    int FILE_FLAG_BACKUP_SEMANTICS = 33554432;
    int FILE_FLAG_OVERLAPPED = 1073741824;
    
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
    
    int GENERIC_WRITE = 0x40000000;
    public static class SECURITY_ATTRIBUTES extends Structure {
        public int nLength;
        public Pointer lpSecurityDescriptor;
        public int bInheritHandle;
    }
    Pointer CreateFileA(String lpFileName, int dwDesiredAccess, int dwShareMode,
                        SECURITY_ATTRIBUTES lpSecurityAttributes,
                        int dwCreationDisposition, int dwFlagsAndAttributes,
                        Pointer hTemplateFile);
    boolean CreateDirectoryA();

    Pointer CreateIoCompletionPort(Pointer FileHandle, 
                                   Pointer ExistingCompletionPort,
                                   Pointer CompletionKey, 
                                   int NumberOfConcurrentThreads);
    int INFINITE = 0xFFFFFFFF;
    boolean GetQueuedCompletionStatus(Pointer CompletionPort, 
                                      IntByReference lpNumberOfBytes,
                                      PointerByReference lpCompletionKey,
                                      PointerByReference lpOverlapped, 
                                      int dwMilliseconds);
    boolean PostQueuedCompletionStatus(Pointer CompletionPort, 
                                       int dwNumberOfBytesTransferred,
                                       Pointer dwCompletionKey, 
                                       OVERLAPPED lpOverlapped);
    int WaitForSingleObject(Pointer hHandle, int dwMilliseconds);
    boolean CloseHandle(Pointer hObject);
    
    int FILE_ACTION_ADDED = 1;
    int FILE_ACTION_REMOVED = 2;
    int FILE_ACTION_MODIFIED = 3;
    int FILE_ACTION_RENAMED_OLD_NAME = 4;
    int FILE_ACTION_RENAMED_NEW_NAME = 5;
    
    int FILE_NOTIFY_CHANGE_FILE_NAME = 1;
    int FILE_NOTIFY_CHANGE_DIR_NAME = 2;
    int FILE_NOTIFY_CHANGE_NAME = 3;
    int FILE_NOTIFY_CHANGE_ATTRIBUTES = 4;
    int FILE_NOTIFY_CHANGE_SIZE = 8;
    int FILE_NOTIFY_CHANGE_LAST_WRITE = 16;
    int FILE_NOTIFY_CHANGE_LAST_ACCESS = 32;
    int FILE_NOTIFY_CHANGE_CREATION = 64;
    int FILE_NOTIFY_CHANGE_EA = 128;
    int FILE_NOTIFY_CHANGE_SECURITY = 256;
    int FILE_NOTIFY_CHANGE_STREAM_NAME = 512;
    int FILE_NOTIFY_CHANGE_STREAM_SIZE = 1024;
    int FILE_NOTIFY_CHANGE_STREAM_WRITE = 2048;
    /** This structure is non-trivial since it is a pattern stamped
     * into a large block of result memory rather than something that stands
     * alone or is used for input.
     */
    public static class FILE_NOTIFY_INFORMATION extends Structure {
        public int NextEntryOffset;
        public int Action;
        public int FileNameLength;
        // filename is not nul-terminated, so we can't use a String/WString
        public char[] FileName = new char[0];
        private FILE_NOTIFY_INFORMATION() { } 
        public FILE_NOTIFY_INFORMATION(int size) {
            if (size < size())
                throw new IllegalArgumentException("Size must greater than "
                                                   + size() + ", requested " 
                                                   + size);
            allocateMemory(size);
        }
        public String getFilename() {
            return new String(FileName, 0, FileNameLength/2);
        }
        public void read() {
            // avoid reading filename until we know how long it is
            FileName = new char[0];
            super.read();
            FileName = getMemory().getCharArray(12, FileNameLength/2);
        }
        public FILE_NOTIFY_INFORMATION next() {
            if (NextEntryOffset == 0)
                return null;
            FILE_NOTIFY_INFORMATION next = new FILE_NOTIFY_INFORMATION();
            next.useMemory(getMemory(), NextEntryOffset);
            next.read();
            return next;
        }
    }
    public static class OVERLAPPED extends Structure {
        public int Internal;
        public int InternalHigh;
        public int Offset;
        public int OffsetHigh;
        public Pointer hEvent;
    }
    // TODO: figure out how OVERLAPPED is used and apply an appropriate mapping
    public static interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallCallback {
        void callback(int errorCode, int nBytesTransferred, OVERLAPPED overlapped);
    }
    boolean ReadDirectoryChangesW(Pointer directory, 
                                  FILE_NOTIFY_INFORMATION info, 
                                  int length,
                                  boolean watchSubtree, 
                                  int notifyFilter,
                                  IntByReference bytesReturned,
                                  OVERLAPPED overlapped, 
                                  OVERLAPPED_COMPLETION_ROUTINE completionRoutine);
}
