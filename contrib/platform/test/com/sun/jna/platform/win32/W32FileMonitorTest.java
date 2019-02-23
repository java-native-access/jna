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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.sun.jna.Platform;
import com.sun.jna.platform.FileMonitor;
import com.sun.jna.platform.FileMonitor.FileEvent;
import com.sun.jna.platform.FileMonitor.FileListener;

public class W32FileMonitorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(W32FileMonitorTest.class);
    }

    private Map<Integer, FileEvent> events;
    private FileMonitor monitor;
    private File tmpdir;

    protected void setUp() throws Exception {
        events = new HashMap<Integer, FileEvent>();
        final FileListener listener = new FileListener() {
            public void fileChanged(FileEvent e) {
                events.put(Integer.valueOf(e.getType()), e);
            }
        };
        monitor = FileMonitor.getInstance();
        monitor.addFileListener(listener);
        tmpdir = new File(Kernel32Util.getTempPath());
    }

    protected void tearDown() {
        if (monitor != null) {
            monitor.dispose();
        }
    }

    public void testNotifyOnFileCreation() throws Exception {
        monitor.addWatch(tmpdir);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        file.deleteOnExit();
        assertFileEventCreated(file);
    }

    public void testNotifyOnFileDelete() throws Exception {
        monitor.addWatch(tmpdir);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        file.delete();

        final FileEvent event = waitForFileEvent(FileMonitor.FILE_DELETED);
        assertNotNull("No delete event: " + events, event);
        assertEquals("Wrong target file for event", file, event.getFile());
    }

    public void testNotifyOnFileDeleteViaAddWatchMask() throws Exception {
        if (!Platform.isWindows()) return;

        monitor.addWatch(tmpdir, FileMonitor.FILE_DELETED);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        file.delete();

        final FileEvent event = waitForFileEvent(FileMonitor.FILE_DELETED);
        assertNotNull("No delete event: " + events, event);
        assertEquals("Wrong target file for event", file, event.getFile());
    }

    public void testNotifyOnFileRename() throws Exception {
        monitor.addWatch(tmpdir);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        File newFile = new File(file.getParentFile(), "newfile");
        newFile.deleteOnExit();
        file.deleteOnExit();
        file.renameTo(newFile);

        final FileEvent eventFilenameOld = waitForFileEvent(FileMonitor.FILE_NAME_CHANGED_OLD);
        final FileEvent eventFilenameNew = waitForFileEvent(FileMonitor.FILE_NAME_CHANGED_NEW);
        assertNotNull("No rename event (old): " + events, eventFilenameOld);
        assertNotNull("No rename event (new): " + events, eventFilenameNew);
        assertEquals("Wrong target file for event (old)", file, eventFilenameOld.getFile());
        assertEquals("Wrong target file for event (new)", newFile, eventFilenameNew.getFile());
    }

    public void testNotifyOnFileModification() throws Exception {
        if (!Platform.isWindows()) return;

        monitor.addWatch(tmpdir);
        File file = File.createTempFile(getName(), ".tmp", tmpdir);
        file.deleteOnExit();
        final FileOutputStream os = new FileOutputStream(file);
        try {
            os.write(getName().getBytes());
        } finally {
            os.close();
        }
        final FileEvent event = waitForFileEvent(FileMonitor.FILE_MODIFIED);
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
        File subdir1 = createSubdir(tmpdir, "sub1-");
        File subdir2 = createSubdir(tmpdir, "sub2-");
        try {
            monitor.addWatch(subdir1);
            monitor.addWatch(subdir2);

            // trigger change in dir 1
            assertFileEventCreated(File.createTempFile(getName(), ".tmp", subdir1));

            // trigger change in dir 2
            assertFileEventCreated(File.createTempFile(getName(), ".tmp", subdir2));

            // trigger change in dir 1
            assertFileEventCreated(File.createTempFile(getName(), ".tmp", subdir1));
        }
        finally {
            delete(subdir1);
            delete(subdir2);
        }
    }

    public void testMultipleConsecutiveWatches() throws Exception {
        File subdir1 = createSubdir(tmpdir, "sub1-");
        File subdir2 = createSubdir(tmpdir, "sub2-");
        try {
            monitor.addWatch(subdir1);
            monitor.addWatch(subdir2);
            // trigger change in dir 1
            assertFileEventCreated(File.createTempFile(getName(), ".tmp", subdir1));
            monitor.removeWatch(subdir1);
            // trigger change in dir 2
            assertFileEventCreated(File.createTempFile(getName(), ".tmp", subdir2));
            monitor.removeWatch(subdir2);
            monitor.addWatch(subdir1);
            // assertion below has intermittent failures on slow W2K box w/out sleep
            Thread.sleep(10);
            // trigger change in dir 1
            assertFileEventCreated(File.createTempFile(getName(), ".tmp", subdir1));
            monitor.removeWatch(subdir1);
        }
        finally {
            delete(subdir1);
            delete(subdir2);
        }
    }

    private void assertFileEventCreated(final File expectedFile)
            throws InterruptedException {

        final FileEvent actualEvent = waitForFileEvent(FileMonitor.FILE_CREATED);

        assertNotNull("No creation event: " + events, actualEvent);
        assertEquals("Wrong target file for event", expectedFile, actualEvent.getFile());
        events.clear();
    }

    private FileEvent waitForFileEvent(final int expectedFileEvent)
            throws InterruptedException {

        final Integer expectedFileEventInteger = Integer.valueOf(expectedFileEvent);

        FileEvent actualEvent = (FileEvent)events.get(expectedFileEventInteger);
        final long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000 && actualEvent == null) {
            Thread.sleep(10);
            actualEvent = (FileEvent) events.get(expectedFileEventInteger);
        }

        assertTrue("No events sent", events.size() != 0);
        return actualEvent;
    }
}
