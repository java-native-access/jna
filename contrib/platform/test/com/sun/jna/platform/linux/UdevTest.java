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

    private static final Udev UDEV = Udev.INSTANCE;

    @Test
    public void testEnumerateDevices() {
        // Start with the context object
        UdevContext udev = UDEV.udev_new();
        assertNotNull("Failed to create udev context", udev);
        // Create an enumerator
        UdevEnumerate enumerate = UDEV.udev_enumerate_new(udev);
        assertNotNull("Failed to create udev enumerator", enumerate);
        // It's a good assumption the machine we're on has a disk and partition
        // Add a filter for block devices and scan them
        UDEV.udev_enumerate_add_match_subsystem(enumerate, "block");
        UDEV.udev_enumerate_scan_devices(enumerate);
        // Enumerator now points to block devices.

        // Iterate the list
        UdevListEntry entry = UDEV.udev_enumerate_get_list_entry(enumerate);
        assertNotNull("Failed to get an enumerator entry", entry);
        String entryName = UDEV.udev_list_entry_get_name(entry);
        assertEquals("Udev entry name should start with /sys", "/sys", entryName.substring(0, 4));
        UdevDevice device = UDEV.udev_device_new_from_syspath(udev, entryName);
        while (device != null) {
            // devnode is what we use as name, like /dev/sda
            String devnode = UDEV.udev_device_get_devnode(device);
            // Ignore loopback and ram disks
            if (devnode != null && !devnode.startsWith("/dev/loop") && !devnode.startsWith("/dev/ram")) {
                String devType = UDEV.udev_device_get_devtype(device);
                // For partition, grab values of parent disk
                String parentSize = null;
                String parentMajor = null;
                if ("partition".equals(devType)) {
                    UdevDevice parent = UDEV.udev_device_get_parent_with_subsystem_devtype(device, "block", "disk");
                    String parentName = UDEV.udev_device_get_devnode(parent);
                    // These should match same parent without restricting to block and disk
                    UdevDevice parent2 = UDEV.udev_device_get_parent(device);
                    String parentName2 = UDEV.udev_device_get_devnode(parent2);
                    assertEquals("Partition parent should match with and without filter", parentName, parentName2);
                    // Save the size and major
                    parentSize = UDEV.udev_device_get_sysattr_value(parent, "size");
                    parentMajor = UDEV.udev_device_get_property_value(parent, "MAJOR");
                }
                String size = UDEV.udev_device_get_sysattr_value(device, "size");
                assertTrue("Size must be nonnegative", 0 <= Long.parseLong(size));
                if (parentSize != null) {
                    assertTrue("Partition can't be bigger than its disk",
                            Long.parseLong(size) <= Long.parseLong(parentSize));
                }
                String major = UDEV.udev_device_get_property_value(device, "MAJOR");
                assertTrue("Major value must be nonnegative", 0 <= Long.parseLong(major));
                if (parentMajor != null) {
                    assertEquals("Partition and its parent disk should have same major number", major, parentMajor);
                }
                assertEquals("DevType mismatch", devType, UDEV.udev_device_get_devtype(device));
                assertEquals("Subsystem mismatch", "block", UDEV.udev_device_get_subsystem(device));
                assertEquals("Syspath mismatch", entryName, UDEV.udev_device_get_syspath(device));
                assertTrue("Syspath should end with name", entryName.endsWith(UDEV.udev_device_get_sysname(device)));
            }
            UDEV.udev_device_unref(device);
            // Release the reference and iterate to the next device
            entry = UDEV.udev_list_entry_get_next(entry);
            entryName = UDEV.udev_list_entry_get_name(entry);
            if (entry != null) {
                assertEquals("Udev entry name should start with /sys", "/sys", entryName.substring(0, 4));
            }
            device = UDEV.udev_device_new_from_syspath(udev, entryName);
        }
        UDEV.udev_enumerate_unref(enumerate);
        UDEV.udev_unref(udev);
    }
}
