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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import com.sun.jna.Platform;
import com.sun.jna.examples.FileMonitor.FileEvent;
import com.sun.jna.examples.FileMonitor.FileListener;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.ptr.PointerByReference;

public class FileMonitorTest extends TestCase {

    Map events;
    FileListener listener;
    FileMonitor monitor;
    File tmpdir;
    
    protected void setUp() throws Exception {
        if (!Platform.isWindows()) return;

        events = new HashMap();
        listener = new FileListener() {
            public void fileChanged(FileEvent e) {
                events.put(new Integer(e.getType()), e);
            }
        };
        monitor = FileMonitor.getInstance();
        monitor.addFileListener(listener);
        tmpdir = new File(System.getProperty("java.io.tmpdir"));
    }
    
    protected void tearDown() {
        if (monitor != null) {
            monitor.dispose();
        }
    }
    
    public void testNotifyOnFileCreation() throws Exception {
        if (!Platform.isWindows()) return;

        monitor.addWatch(tmpdir);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        file.deleteOnExit();
        FileEvent event = (FileEvent)events.get(new Integer(FileMonitor.FILE_CREATED));
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000 && event == null) {
            Thread.sleep(10);
            event = (FileEvent)events.get(new Integer(FileMonitor.FILE_CREATED));
        }
        assertTrue("No events sent", events.size() != 0);
        assertNotNull("No creation event: " + events, event);
        assertEquals("Wrong target file for event", file, event.getFile());
    }
    
    public void testNotifyOnFileDelete() throws Exception {
        if (!Platform.isWindows()) return;

        monitor.addWatch(tmpdir);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        file.delete();
        FileEvent event = (FileEvent)events.get(new Integer(FileMonitor.FILE_DELETED));
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000 && event == null) {
            Thread.sleep(10);
            event = (FileEvent)events.get(new Integer(FileMonitor.FILE_DELETED));
        }
        assertTrue("No events sent", events.size() != 0);
        assertNotNull("No delete event: " + events, event);
        assertEquals("Wrong target file for event", file, event.getFile());
    }
    
    public void testNotifyOnFileRename() throws Exception {
        if (!Platform.isWindows()) return;

        monitor.addWatch(tmpdir);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        File newFile = new File(file.getParentFile(), "newfile");
        newFile.deleteOnExit();
        file.deleteOnExit();
        file.renameTo(newFile);
        FileEvent e1 = (FileEvent)events.get(new Integer(FileMonitor.FILE_NAME_CHANGED_OLD));
        FileEvent e2 = (FileEvent)events.get(new Integer(FileMonitor.FILE_NAME_CHANGED_NEW));
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000 && e1 == null && e2 == null) {
            Thread.sleep(10);
            e1 = (FileEvent)events.get(new Integer(FileMonitor.FILE_NAME_CHANGED_OLD));
            e2 = (FileEvent)events.get(new Integer(FileMonitor.FILE_NAME_CHANGED_NEW));
        }
        assertTrue("No events sent", events.size() != 0);
        assertNotNull("No rename event (old): " + events, e1);
        assertNotNull("No rename event (new): " + events, e2);
        assertEquals("Wrong target file for event (old)", file, e1.getFile());
        assertEquals("Wrong target file for event (new)", newFile, e2.getFile());
    }

    public void testNotifyOnFileModification() throws Exception {
        if (!Platform.isWindows()) return;

        monitor.addWatch(tmpdir);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        file.deleteOnExit();
        FileOutputStream os = new FileOutputStream(file);
        os.write(getName().getBytes());
        os.close();
        FileEvent event = (FileEvent)events.get(new Integer(FileMonitor.FILE_MODIFIED));
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000 && event == null) {
            Thread.sleep(10);
            event = (FileEvent)events.get(new Integer(FileMonitor.FILE_MODIFIED));
        }
        assertTrue("No events sent", events.size() != 0);
        assertNotNull("No file modified event: " + events, event);
        assertEquals("Wrong target file for event (old)", file, event.getFile());
    }
    
    private void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i=0;i < files.length;i++) {
                delete(files[i]);
            }
        }
        file.delete();
    }
    private File createSubdir(File dir, String name) throws IOException {
        File f = File.createTempFile(name, ".tmp", dir);
        f.delete();
        f.mkdirs();
        return f;
    }
    public void testMultipleWatches() throws Exception {
        if (!Platform.isWindows()) return;
        
        File subdir1 = createSubdir(tmpdir, "sub1");
        File subdir2 = createSubdir(tmpdir, "sub2");
        try {
            monitor.addWatch(subdir1);
            monitor.addWatch(subdir2);

            // trigger change in dir 1
            File file = File.createTempFile(getName(), ".tmp", subdir1);
            FileEvent event = (FileEvent)events.get(new Integer(FileMonitor.FILE_CREATED));
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 5000 && event == null) {
                Thread.sleep(10);
                event = (FileEvent)events.get(new Integer(FileMonitor.FILE_CREATED));
            }
            assertTrue("No events sent", events.size() != 0);
            assertNotNull("No creation event: " + events, event);
            assertEquals("Wrong target file for event", file, event.getFile());
            events.clear();

            // trigger change in dir 2
            file = File.createTempFile(getName(), ".tmp", subdir2);
            event = (FileEvent)events.get(new Integer(FileMonitor.FILE_CREATED));
            start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 5000 && event == null) {
                Thread.sleep(10);
                event = (FileEvent)events.get(new Integer(FileMonitor.FILE_CREATED));
            }
            assertTrue("No events sent", events.size() != 0);
            assertNotNull("No creation event: " + events, event);
            assertEquals("Wrong target file for event", file, event.getFile());
            events.clear();

            // trigger change in dir 1
            file = File.createTempFile(getName(), ".tmp", subdir1);
            event = (FileEvent)events.get(new Integer(FileMonitor.FILE_CREATED));
            start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 5000 && event == null) {
                Thread.sleep(10);
                event = (FileEvent)events.get(new Integer(FileMonitor.FILE_CREATED));
            }
            assertTrue("No events sent", events.size() != 0);
            assertNotNull("No creation event: " + events, event);
            assertEquals("Wrong target file for event", file, event.getFile());
            events.clear();
        }
        finally {
            delete(subdir1);
            delete(subdir2);
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FileMonitorTest.class);
    }
}
