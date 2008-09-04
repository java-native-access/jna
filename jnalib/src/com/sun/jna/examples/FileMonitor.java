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
package com.sun.jna.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.Kernel32.FILE_NOTIFY_INFORMATION;
import com.sun.jna.examples.win32.Kernel32.OVERLAPPED;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/** Provides notification of file system changes.  Actual capabilities may
 * vary slightly by platform.
 * <p>
 * Watched files which are removed from the filesystem are no longer watched.
 * @author twall
 */

public abstract class FileMonitor {

    public static final int FILE_CREATED = 0x1;
    public static final int FILE_DELETED = 0x2;
    public static final int FILE_MODIFIED = 0x4;
    public static final int FILE_ACCESSED = 0x8;
    public static final int FILE_NAME_CHANGED_OLD = 0x10;
    public static final int FILE_NAME_CHANGED_NEW = 0x20;
    public static final int FILE_RENAMED = FILE_NAME_CHANGED_OLD|FILE_NAME_CHANGED_NEW;
    public static final int FILE_SIZE_CHANGED = 0x40;
    public static final int FILE_ATTRIBUTES_CHANGED = 0x80;
    public static final int FILE_SECURITY_CHANGED = 0x100;
    public static final int FILE_ANY = 0x1FF;

    public interface FileListener {
        public void fileChanged(FileEvent e);
    }
    
    public class FileEvent extends EventObject {
        private final File file;
        private final int type;
        public FileEvent(File file, int type) {
            super(FileMonitor.this);
            this.file = file;
            this.type = type;
        }
        public File getFile() { return file; }
        public int getType() { return type; }
        public String toString() {
            return "FileEvent: " + file + ":" + type;
        }
    }
    
    private final Map watched = new HashMap();
    private List listeners = new ArrayList();
    
    protected abstract void watch(File file, int mask, boolean recursive) throws IOException ;
    protected abstract void unwatch(File file);
    protected abstract void dispose();

    public void addWatch(File dir) throws IOException {
        addWatch(dir, FILE_ANY);
    }
    
    public void addWatch(File dir, int mask) throws IOException {
        addWatch(dir, mask, dir.isDirectory());
    }
    
    public void addWatch(File dir, int mask, boolean recursive) throws IOException {
        watched.put(dir, new Integer(mask));
        watch(dir, mask, recursive);
    }

    public void removeWatch(File file) {
        if (watched.remove(file) != null) {
            unwatch(file);
        }
    }
    
    protected void notify(FileEvent e) {
        for (Iterator i=listeners.iterator();i.hasNext();) {
            ((FileListener)i.next()).fileChanged(e);
        }
    }
    
    public synchronized void addFileListener(FileListener x) {
        List list = new ArrayList(listeners);
        list.add(x);
        listeners = list;
    }
    
    public synchronized void removeFileListener(FileListener x) {
        List list = new ArrayList(listeners);
        list.remove(x);
        listeners = list;
    }
    
    protected void finalize() {
        for (Iterator i=watched.keySet().iterator();i.hasNext();) {
            removeWatch((File)i.next());
        }
        dispose();
    }
    
    /** Canonical lazy loading of a singleton. */
    private static class Holder {
        public static final FileMonitor INSTANCE;
        static {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                INSTANCE = new W32FileMonitor();
            }
            else {
                throw new Error("FileMonitor not implemented for " + os);
            }
        }
    }
    
    public static FileMonitor getInstance() {
        return Holder.INSTANCE;
    }
    
    private static class W32FileMonitor extends FileMonitor {
        
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
        private final Map fileMap = new HashMap();
        private final Map handleMap = new HashMap();
        
        private void handleChanges(FileInfo finfo) throws IOException {
            Kernel32 klib = Kernel32.INSTANCE;
            FILE_NOTIFY_INFORMATION fni = finfo.info;
            // Need an explicit read, since data was filled in asynchronously
            fni.read();
            do {
                FileEvent event = null;
                File file = new File(finfo.file, fni.getFilename());
                switch(fni.Action) {
                case Kernel32.FILE_ACTION_MODIFIED:
                    event = new FileEvent(file, FILE_MODIFIED); break;
                case Kernel32.FILE_ACTION_ADDED:
                    event = new FileEvent(file, FILE_CREATED); break;
                case Kernel32.FILE_ACTION_REMOVED:
                    event = new FileEvent(file, FILE_DELETED); break;
                case Kernel32.FILE_ACTION_RENAMED_OLD_NAME:
                    event = new FileEvent(file, FILE_NAME_CHANGED_OLD); break;
                case Kernel32.FILE_ACTION_RENAMED_NEW_NAME:
                    event = new FileEvent(file, FILE_NAME_CHANGED_NEW); break;
                default:
                    // TODO: other actions...
                    System.err.println("Unrecognized file action '" + fni.Action + "'");
                }
                if (event != null)
                    notify(event);
                fni = fni.next();
            } while (fni != null);
            // Trigger the next read
            if (!finfo.file.exists()) {
                unwatch(finfo.file);
                return;
            }
            
            if (!klib.ReadDirectoryChangesW(finfo.handle, finfo.info,
                                            finfo.info.size(), finfo.recursive,
                                            finfo.notifyMask, finfo.infoLength, 
                                            finfo.overlapped, null)) {
                int err = klib.GetLastError();
                throw new IOException("ReadDirectoryChangesW failed on "
                                      + finfo.file + ": '" 
                                      + getSystemError(err)
                                      + "' (" + err + ")");
            }
        }
        private FileInfo waitForChange() {
            Kernel32 klib = Kernel32.INSTANCE;
            IntByReference rcount = new IntByReference();
            HANDLEByReference rkey = new HANDLEByReference();
            PointerByReference roverlap = new PointerByReference();
            klib.GetQueuedCompletionStatus(port, rcount, rkey, roverlap, Kernel32.INFINITE);
            
            synchronized (this) { 
                return (FileInfo)handleMap.get(rkey.getValue());
            }
        }
        private int convertMask(int mask) {
            int result = 0;
            if ((mask & FILE_CREATED) != 0) {
                result |= Kernel32.FILE_NOTIFY_CHANGE_CREATION;
            }
            if ((mask & FILE_DELETED) != 0) {
                result |= Kernel32.FILE_NOTIFY_CHANGE_NAME;
            }
            if ((mask & FILE_MODIFIED) != 0) {
                result |= Kernel32.FILE_NOTIFY_CHANGE_LAST_WRITE;
            }
            if ((mask & FILE_RENAMED) != 0) {
                result |= Kernel32.FILE_NOTIFY_CHANGE_NAME;
            }
            if ((mask & FILE_SIZE_CHANGED) != 0) {
                result |= Kernel32.FILE_NOTIFY_CHANGE_SIZE;
            }
            if ((mask & FILE_ACCESSED) != 0) {
                result |= Kernel32.FILE_NOTIFY_CHANGE_LAST_ACCESS;
            }
            if ((mask & FILE_ATTRIBUTES_CHANGED) != 0) {
                result |= Kernel32.FILE_NOTIFY_CHANGE_ATTRIBUTES;
            }
            if ((mask & FILE_SECURITY_CHANGED) != 0) {
                result |= Kernel32.FILE_NOTIFY_CHANGE_SECURITY;
            }
            return result;
        }

        private static int watcherThreadID;

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
            int mask = Kernel32.FILE_SHARE_READ
                | Kernel32.FILE_SHARE_WRITE | Kernel32.FILE_SHARE_DELETE;
            int flags = Kernel32.FILE_FLAG_BACKUP_SEMANTICS
                | Kernel32.FILE_FLAG_OVERLAPPED;
            HANDLE handle = klib.CreateFile(file.getAbsolutePath(), 
                                            Kernel32.FILE_LIST_DIRECTORY,
                                            mask, null, Kernel32.OPEN_EXISTING,
                                            flags, null);
            if (Kernel32.INVALID_HANDLE_VALUE.equals(handle)) {
                throw new IOException("Unable to open " + file + " (" 
                                      + klib.GetLastError() + ")");
            }
            int notifyMask = convertMask(eventMask);
            FileInfo finfo = new FileInfo(file, handle, notifyMask, recursive);
            fileMap.put(file, finfo);
            handleMap.put(handle, finfo);
            // Existing port is returned
            port = klib.CreateIoCompletionPort(handle, port, handle.getPointer(), 0);
            if (Kernel32.INVALID_HANDLE_VALUE.equals(port)) {
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
                                      + ": '" + getSystemError(err)
                                      + "' (" + err + ")");
            }
            if (watcher == null) {
                watcher = new Thread("W32 File Monitor-" + (watcherThreadID++)) {
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

        protected synchronized void unwatch(File file) {
            FileInfo finfo = (FileInfo)fileMap.remove(file);
            if (finfo != null) {
                handleMap.remove(finfo.handle);
                Kernel32 klib = Kernel32.INSTANCE;
                klib.CloseHandle(finfo.handle);
            }
        }
        protected synchronized void dispose() {
            // unwatch any remaining files in map, allows watcher thread to exit
            int i = 0;
            for (Object[] keys = fileMap.keySet().toArray(); !fileMap.isEmpty();) {
                unwatch((File)keys[i++]);
            }
            
            Kernel32 klib = Kernel32.INSTANCE;
            klib.PostQueuedCompletionStatus(port, 0, null, null);
            klib.CloseHandle(port);
            port = null;
            watcher = null;
        }
        private String getSystemError(int code) {
            Kernel32 lib = Kernel32.INSTANCE;
            PointerByReference pref = new PointerByReference();
            lib.FormatMessage(Kernel32.FORMAT_MESSAGE_ALLOCATE_BUFFER
                              |Kernel32.FORMAT_MESSAGE_FROM_SYSTEM
                              |Kernel32.FORMAT_MESSAGE_IGNORE_INSERTS, 
                              null, code, 
                              0, pref, 0, null);
            String s = pref.getValue().getString(0, !Boolean.getBoolean("w32.ascii"));
            s = s.replace(".\r",".").replace(".\n",".");
            lib.LocalFree(pref.getValue());
            return s;
        }
    }
    
    private static class KQueueFileMonitor extends FileMonitor {
        protected void watch(File file, int mask, boolean recursive) {
            
        }
        protected void unwatch(File file) {
        }
        protected void dispose() {
        }
    }
    
    private static class INotifyFileMonitor extends FileMonitor {
        protected void watch(File file, int mask, boolean recursive) {
            
        }
        protected void unwatch(File file) {
        }
        protected void dispose() {
        }
    }
}
