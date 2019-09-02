/*
 * Copyright (c) 2019 Daniel Widdis
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
package com.sun.jna.platform.mac;

import static com.sun.jna.platform.mac.CoreFoundationUtil.release;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.platform.mac.DiskArbitration.DADiskRef;
import com.sun.jna.platform.mac.DiskArbitration.DASessionRef;
import com.sun.jna.ptr.LongByReference;

public class DiskArbitrationTest {

    private static final DiskArbitration DA = DiskArbitration.INSTANCE;
    private static final CoreFoundation CF = CoreFoundation.INSTANCE;
    private static final IOKit IO = IOKit.INSTANCE;

    @Test
    public void testDiskCreate() {
        LongByReference masterPortPtr = new LongByReference();
        assertEquals(0, IO.IOMasterPort(0, masterPortPtr));
        long masterPort = masterPortPtr.getValue();

        // Create some keys we'll need
        CFStringRef daMediaBSDName = CFStringRef.toCFString("DAMediaBSDName");
        CFStringRef daMediaWhole = CFStringRef.toCFString("DAMediaWhole");
        CFStringRef daMediaSize = CFStringRef.toCFString("DAMediaSize");
        CFStringRef daMediaBlockSize = CFStringRef.toCFString("DAMediaBlockSize");

        // Open a DiskArbitration session
        DASessionRef session = DA.DASessionCreate(CF.CFAllocatorGetDefault());
        assertNotNull(session);

        // Get IOMedia objects representing whole drives and save the BSD Name
        List<String> bsdNames = new ArrayList<>();
        LongByReference iter = new LongByReference();

        CFMutableDictionaryRef dict = IOKit.INSTANCE.IOServiceMatching("IOMedia");
        // Consumes a reference to dict
        assertEquals(0, IO.IOServiceGetMatchingServices(masterPort, dict, iter));
        long media = IO.IOIteratorNext(iter.getValue());
        while (media != 0) {
            CFStringRef wholeKey = CFStringRef.toCFString("Whole");
            CFTypeRef cfWhole = IO.IORegistryEntryCreateCFProperty(media, wholeKey, CF.CFAllocatorGetDefault(), 0);
            CoreFoundationUtil.release(wholeKey);
            assertNotNull(cfWhole);

            if (CoreFoundationUtil.cfPointerToBoolean(cfWhole)) {
                DADiskRef disk = DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), session, media);
                bsdNames.add(DA.DADiskGetBSDName(disk));
                release(disk);
            }
            CoreFoundationUtil.release(cfWhole);
            IO.IOObjectRelease(media);
            media = IO.IOIteratorNext(iter.getValue());
        }
        IO.IOObjectRelease(iter.getValue());

        // Now iterate the bsdNames
        for (String bsdName : bsdNames) {
            // Get a reference to the disk - only matching /dev/disk*
            String path = "/dev/" + bsdName;
            File f = new File(path);
            assertTrue(f.exists());
            // Get the DiskArbitration dictionary for this disk, which has size (capacity)
            DADiskRef disk = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), session, path);
            assertNotNull(disk);
            CFDictionaryRef diskInfo = DA.DADiskCopyDescription(disk);
            assertNotNull(diskInfo);

            // Since we looked up "whole" BSD disks these should match
            CFTypeRef bsdNamePtr = CF.CFDictionaryGetValue(diskInfo, daMediaBSDName);
            assertEquals(bsdName, CoreFoundationUtil.cfPointerToString(bsdNamePtr));
            CFTypeRef bsdWholePtr = CF.CFDictionaryGetValue(diskInfo, daMediaWhole);
            assertTrue(CoreFoundationUtil.cfPointerToBoolean(bsdWholePtr));

            // Size is a multiple of block size
            CFTypeRef sizePtr = CF.CFDictionaryGetValue(diskInfo, daMediaSize);
            long size = CoreFoundationUtil.cfPointerToLong(sizePtr);
            CFTypeRef blockSizePtr = CF.CFDictionaryGetValue(diskInfo, daMediaBlockSize);
            long blockSize = CoreFoundationUtil.cfPointerToLong(blockSizePtr);
            assertEquals(0, size % blockSize);

            release(diskInfo);
            release(disk);
        }
        release(daMediaBSDName);
        release(daMediaWhole);
        release(daMediaSize);
        release(daMediaBlockSize);

        release(session);

        assertEquals(0, IO.IOObjectRelease(masterPort));
    }
}
