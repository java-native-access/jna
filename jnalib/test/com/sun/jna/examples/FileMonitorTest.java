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
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import com.sun.jna.Platform;
import com.sun.jna.examples.FileMonitor.FileEvent;
import com.sun.jna.examples.FileMonitor.FileListener;

public class FileMonitorTest extends TestCase {

    Map events;
    FileListener listener;
    FileMonitor monitor;
    File dir;
    
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
        dir = new File(System.getProperty("java.io.tmpdir"));
        monitor.addWatch(dir);
    }
    
    public void testNotifyOnFileCreation() throws Exception {
        if (!Platform.isWindows()) return;

        File file = File.createTempFile(getName(), ".tmp", dir);
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

        File file = File.createTempFile(getName(), ".tmp", dir);
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

        File file = File.createTempFile(getName(), ".tmp", dir);
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

        File file = File.createTempFile(getName(), ".tmp", dir);
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

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FileUtilsTest.class);
    }
}
