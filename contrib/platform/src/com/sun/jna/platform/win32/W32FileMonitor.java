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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.platform.FileMonitor;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTRByReference;
import com.sun.jna.platform.win32.WinBase.OVERLAPPED;
import com.sun.jna.platform.win32.WinNT.FILE_NOTIFY_INFORMATION;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class W32FileMonitor extends FileMonitor {

    private static final int BUFFER_SIZE = 4096;

    private class FileInfo {
        public final File file;
        public final HANDLE handle;
        public final int notifyMask;
        public final boolean recursive;
        public final FILE_NOTIFY_INFORMATION info = new FILE_NOTIFY_INFORMATION(BUFFER_SIZE);
        public final IntByReference infoLength = new IntByReference();
        public final OVERLAPPED overlapped = new OVERLAPPED();
        public FileInfo(File f, HANDLE h, int mask, boolean recurse) {
            this.file = f;
            this.handle = h;
            this.notifyMask = mask;
            this.recursive = recurse;
        }
    }
    private Thread watcher;
    private HANDLE port;
    private final Map<File, FileInfo> fileMap = new HashMap<File, FileInfo>();
    private final Map<HANDLE, FileInfo> handleMap = new HashMap<HANDLE, FileInfo>();
    private boolean disposing = false;

    private void handleChanges(FileInfo finfo) throws IOException {
        Kernel32 klib = Kernel32.INSTANCE;
        FILE_NOTIFY_INFORMATION fni = finfo.info;
        // Need an explicit read, since data was filled in asynchronously
        fni.read();
        do {
            FileEvent event = null;
            File file = new File(finfo.file, fni.getFilename());
            switch(fni.Action) {
            case 0:
            	break;
            case WinNT.FILE_ACTION_MODIFIED:
                event = new FileEvent(file, FILE_MODIFIED);
                break;
            case WinNT.FILE_ACTION_ADDED:
                event = new FileEvent(file, FILE_CREATED);
                break;
            case WinNT.FILE_ACTION_REMOVED:
                event = new FileEvent(file, FILE_DELETED);
                break;
            case WinNT.FILE_ACTION_RENAMED_OLD_NAME:
                event = new FileEvent(file, FILE_NAME_CHANGED_OLD);
                break;
            case WinNT.FILE_ACTION_RENAMED_NEW_NAME:
                event = new FileEvent(file, FILE_NAME_CHANGED_NEW);
                break;
            default:
                // TODO: other actions...
                System.err.println("Unrecognized file action '" + fni.Action + "'");
            }

            if (event != null) {
                notify(event);
            }

            fni = fni.next();
        } while (fni != null);

        // trigger the next read
        if (!finfo.file.exists()) {
            unwatch(finfo.file);
            return;
        }

        if (!klib.ReadDirectoryChangesW(finfo.handle, finfo.info,
        		finfo.info.size(), finfo.recursive, finfo.notifyMask,
        		finfo.infoLength, finfo.overlapped, null)) {
        	if (! disposing) {
        		int err = klib.GetLastError();
        		throw new IOException("ReadDirectoryChangesW failed on "
                                  + finfo.file + ": '"
                                  + Kernel32Util.formatMessageFromLastErrorCode(err)
                                  + "' (" + err + ")");
        	}
        }
    }

    private FileInfo waitForChange() {
        IntByReference rcount = new IntByReference();
        ULONG_PTRByReference rkey = new ULONG_PTRByReference();
        PointerByReference roverlap = new PointerByReference();
        if (! Kernel32.INSTANCE.GetQueuedCompletionStatus(port, rcount, rkey, roverlap, WinBase.INFINITE)) {
            return null;
        }
        synchronized (this) {
            return handleMap.get(new HANDLE(rkey.getValue().toPointer()));
        }
    }

    private int convertMask(int mask) {
        int result = 0;
        if ((mask & FILE_CREATED) != 0) {
            result |= WinNT.FILE_NOTIFY_CHANGE_CREATION;
        }
        if ((mask & FILE_DELETED) != 0) {
            result |= WinNT.FILE_NOTIFY_CHANGE_NAME;
        }
        if ((mask & FILE_MODIFIED) != 0) {
            result |= WinNT.FILE_NOTIFY_CHANGE_LAST_WRITE;
        }
        if ((mask & FILE_RENAMED) != 0) {
            result |= WinNT.FILE_NOTIFY_CHANGE_NAME;
        }
        if ((mask & FILE_SIZE_CHANGED) != 0) {
            result |= WinNT.FILE_NOTIFY_CHANGE_SIZE;
        }
        if ((mask & FILE_ACCESSED) != 0) {
            result |= WinNT.FILE_NOTIFY_CHANGE_LAST_ACCESS;
        }
        if ((mask & FILE_ATTRIBUTES_CHANGED) != 0) {
            result |= WinNT.FILE_NOTIFY_CHANGE_ATTRIBUTES;
        }
        if ((mask & FILE_SECURITY_CHANGED) != 0) {
            result |= WinNT.FILE_NOTIFY_CHANGE_SECURITY;
        }
        return result;
    }

    private static int watcherThreadID;

    @Override
    protected synchronized void watch(File file, int eventMask, boolean recursive) throws IOException {
        File dir = file;
        if (!dir.isDirectory()) {
            recursive = false;
            dir = file.getParentFile();
        }
        while (dir != null && !dir.exists()) {
            recursive = true;
            dir = dir.getParentFile();
        }
        if (dir == null) {
            throw new FileNotFoundException("No ancestor found for " + file);
        }
        Kernel32 klib = Kernel32.INSTANCE;
        int mask = WinNT.FILE_SHARE_READ
            | WinNT.FILE_SHARE_WRITE | WinNT.FILE_SHARE_DELETE;
        int flags = WinNT.FILE_FLAG_BACKUP_SEMANTICS
            | WinNT.FILE_FLAG_OVERLAPPED;
        HANDLE handle = klib.CreateFile(file.getAbsolutePath(),
        		WinNT.FILE_LIST_DIRECTORY,
        		mask, null, WinNT.OPEN_EXISTING,
                flags, null);
        if (WinBase.INVALID_HANDLE_VALUE.equals(handle)) {
            throw new IOException("Unable to open " + file + " ("
                                  + klib.GetLastError() + ")");
        }
        int notifyMask = convertMask(eventMask);
        FileInfo finfo = new FileInfo(file, handle, notifyMask, recursive);
        fileMap.put(file, finfo);
        handleMap.put(handle, finfo);
        // Existing port is returned
        port = klib.CreateIoCompletionPort(handle, port, handle.getPointer(), 0);
        if (WinBase.INVALID_HANDLE_VALUE.equals(port)) {
            throw new IOException("Unable to create/use I/O Completion port "
                    + "for " + file + " ("
                    + klib.GetLastError() + ")");
        }
        // TODO: use FileIOCompletionRoutine callback method instead of a
        // dedicated thread
        if (!klib.ReadDirectoryChangesW(handle, finfo.info, finfo.info.size(),
                                        recursive, notifyMask, finfo.infoLength,
                                        finfo.overlapped, null)) {
            int err = klib.GetLastError();
            throw new IOException("ReadDirectoryChangesW failed on "
                                  + finfo.file + ", handle " + handle
                                  + ": '" + Kernel32Util.formatMessageFromLastErrorCode(err)
                                  + "' (" + err + ")");
        }
        if (watcher == null) {
            watcher = new Thread("W32 File Monitor-" + (watcherThreadID++)) {
                @Override
                public void run() {
                    FileInfo finfo;
                    while (true) {
                       finfo = waitForChange();
                       if (finfo == null) {
                          synchronized(W32FileMonitor.this) {
                             if (fileMap.isEmpty()) {
                                watcher = null;
                                break;
                             }
                          }
                          continue;
                        }

                        try {
                            handleChanges(finfo);
                        }
                        catch(IOException e) {
                            // TODO: how is this best handled?
                            e.printStackTrace();
                        }
                    }
                }
            };
            watcher.setDaemon(true);
            watcher.start();
        }
    }

    @Override
    protected synchronized void unwatch(File file) {
        FileInfo finfo = fileMap.remove(file);
        if (finfo != null) {
            handleMap.remove(finfo.handle);
            Kernel32 klib = Kernel32.INSTANCE;
            // bug: the watcher may still be processing this file
            klib.CloseHandle(finfo.handle); // TODO check error code if failed to close
        }
    }

    @Override
    public synchronized void dispose() {
    	disposing = true;

        // unwatch any remaining files in map, allows watcher thread to exit
        int i = 0;
        for (Object[] keys = fileMap.keySet().toArray(); !fileMap.isEmpty();) {
            unwatch((File)keys[i++]);
        }

        Kernel32 klib = Kernel32.INSTANCE;
        klib.PostQueuedCompletionStatus(port, 0, null, null);
        klib.CloseHandle(port); // TODO check error code if failed to close
        port = null;
        watcher = null;
    }
}
