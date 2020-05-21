/* Copyright (c) 2020 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.linux;

import org.junit.Test;

import com.sun.jna.platform.linux.Udev.UdevContext;
import com.sun.jna.platform.linux.Udev.UdevDevice;
import com.sun.jna.platform.linux.Udev.UdevEnumerate;
import com.sun.jna.platform.linux.Udev.UdevListEntry;

import junit.framework.TestCase;

/**
 * Exercise the {@link Udev} class.
 */
public class UdevTest extends TestCase {

    @Test
    public void testEnumerateDevices() {
        // Start with the context object
        UdevContext udev = Udev.INSTANCE.udev_new();
        try {
            assertNotNull("Failed to create udev context", udev);
            // Create an enumerator
            UdevEnumerate enumerate = udev.enumerateNew();
            try {
                assertNotNull("Failed to create udev enumerator", enumerate);
                // It's a good assumption the machine we're on has a disk and partition
                // Add a filter for block devices and scan them
                enumerate.addMatchSubsystem("block");
                enumerate.scanDevices();
                // Enumerator now points to block devices.
                // Iterate the list
                for (UdevListEntry entry = enumerate.getListEntry(); entry != null; entry = entry.getNext()) {
                    String syspath = entry.getName();
                    assertEquals("Udev entry name should start with /sys", "/sys", syspath.substring(0, 4));
                    UdevDevice device = udev.deviceNewFromSyspath(syspath);
                    if (device != null) {
                        try {
                            // devnode is what we use as name, like /dev/sda
                            String devnode = device.getDevnode();
                            // Ignore loopback and ram disks
                            if (devnode != null && !devnode.startsWith("/dev/loop")
                                    && !devnode.startsWith("/dev/ram")) {
                                String devType = device.getDevtype();
                                // For partition, grab values of parent disk
                                String parentSize = null;
                                String parentMajor = null;
                                if ("partition".equals(devType)) {
                                    UdevDevice parent = device.getParentWithSubsystemDevtype("block", "disk");
                                    UdevDevice parent2 = device.getParent();
                                    // No additional reference is acquired from getParent
                                    if (parent != null && parent2 != null) {
                                        // Devnode should match same parent without restricting to block and disk
                                        assertEquals("Partition parent should match with and without filter",
                                                parent.getDevnode(), parent2.getDevnode());
                                        // Save the size and major
                                        parentSize = parent.getSysattrValue("size");
                                        parentMajor = parent.getPropertyValue("MAJOR");
                                    }
                                }
                                String size = device.getSysattrValue("size");
                                assertTrue("Size must be nonnegative", 0 <= Long.parseLong(size));
                                if (parentSize != null) {
                                    assertTrue("Partition can't be bigger than its disk",
                                            Long.parseLong(size) <= Long.parseLong(parentSize));
                                }
                                String major = device.getPropertyValue("MAJOR");
                                assertTrue("Major value must be nonnegative", 0 <= Long.parseLong(major));
                                if (parentMajor != null) {
                                    assertEquals("Partition and its parent disk should have same major number", major,
                                            parentMajor);
                                }
                                assertEquals("DevType mismatch", devType, device.getDevtype());
                                assertEquals("Subsystem mismatch", "block", device.getSubsystem());
                                assertEquals("Syspath mismatch", syspath, device.getSyspath());
                                assertTrue("Syspath should end with name", syspath.endsWith(device.getSysname()));
                            }
                        } finally {
                            // Release the reference and iterate to the next device
                            device.unref();
                        }
                    }
                }
            } finally {
                enumerate.unref();
            }
        } finally {
            udev.unref();
        }
    }
}
