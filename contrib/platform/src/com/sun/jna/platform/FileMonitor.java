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
package com.sun.jna.platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.W32FileMonitor;

/** Provides notification of file system changes.  Actual capabilities may
 * vary slightly by platform.
 * <p>
 * Watched files which are removed from the filesystem are no longer watched.
 * @author twall
 */

@SuppressWarnings("serial")
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
    
    private final Map<File, Integer> watched = new HashMap<File, Integer>();
    private List<FileListener> listeners = new ArrayList<FileListener>();
    
    protected abstract void watch(File file, int mask, boolean recursive) throws IOException ;
    protected abstract void unwatch(File file);
    public abstract void dispose();

    public void addWatch(File dir) throws IOException {
        addWatch(dir, FILE_ANY);
    }
    
    public void addWatch(File dir, int mask) throws IOException {
        addWatch(dir, mask, dir.isDirectory());
    }
    
    public void addWatch(File dir, int mask, boolean recursive) throws IOException {
        watched.put(dir, Integer.valueOf(mask));
        watch(dir, mask, recursive);
    }

    public void removeWatch(File file) {
        if (watched.remove(file) != null) {
            unwatch(file);
        }
    }
    
    protected void notify(FileEvent e) {
    	for (FileListener listener : listeners) {
    		listener.fileChanged(e);
    	}
    }
    
    public synchronized void addFileListener(FileListener listener) {
        List<FileListener> list = new ArrayList<FileListener>(listeners);
        list.add(listener);
        listeners = list;
    }
    
    public synchronized void removeFileListener(FileListener x) {
        List<FileListener> list = new ArrayList<FileListener>(listeners);
        list.remove(x);
        listeners = list;
    }
    
    protected void finalize() {
    	for (File watchedFile : watched.keySet()) {
    		removeWatch(watchedFile);
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
 }
