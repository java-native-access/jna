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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation.CFBooleanRef;
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.platform.mac.DiskArbitration.DADiskRef;
import com.sun.jna.platform.mac.DiskArbitration.DASessionRef;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IOObject;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.IOKit.MachPort;
import com.sun.jna.ptr.PointerByReference;

public class DiskArbitrationTest {

    private static final DiskArbitration DA = DiskArbitration.INSTANCE;
    private static final CoreFoundation CF = CoreFoundation.INSTANCE;
    private static final IOKit IO = IOKit.INSTANCE;

    @Test
    public void testDiskCreate() {
        PointerByReference masterPortPtr = new PointerByReference();
        assertEquals(0, IO.IOMasterPort(IOKit.MACH_PORT_NULL, masterPortPtr));
        MachPort masterPort = new MachPort(masterPortPtr.getValue());

        // Create some keys we'll need
        CFStringRef daMediaBSDName = CFStringRef.createCFString("DAMediaBSDName");
        CFStringRef daMediaWhole = CFStringRef.createCFString("DAMediaWhole");
        CFStringRef daMediaLeaf = CFStringRef.createCFString("DAMediaLeaf");
        CFStringRef daMediaSize = CFStringRef.createCFString("DAMediaSize");
        CFStringRef daMediaBlockSize = CFStringRef.createCFString("DAMediaBlockSize");

        // Open a DiskArbitration session
        DASessionRef session = DA.DASessionCreate(CF.CFAllocatorGetDefault());
        assertNotNull(session);

        // Get IOMedia objects representing whole drives and save the BSD Name
        List<String> bsdNames = new ArrayList<>();
        PointerByReference iterPtr = new PointerByReference();

        CFMutableDictionaryRef dict = IOKit.INSTANCE.IOServiceMatching("IOMedia");
        // Consumes a reference to dict
        assertEquals(0, IO.IOServiceGetMatchingServices(masterPort, dict, iterPtr));
        IOIterator iter = new IOIterator(iterPtr.getValue());
        IOObject mediaObj = iter.next();
        while (mediaObj != null) {
            IORegistryEntry media = new IORegistryEntry(mediaObj.getPointer());
            CFStringRef wholeKey = CFStringRef.createCFString("Whole");
            CFTypeRef cfWhole = IO.IORegistryEntryCreateCFProperty(media, wholeKey, CF.CFAllocatorGetDefault(), 0);
            wholeKey.release();
            assertNotNull(cfWhole);
            CFBooleanRef cfWholeBool = new CFBooleanRef(cfWhole.getPointer());
            if (cfWholeBool.booleanValue()) {
                DADiskRef disk = DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), session, media);
                bsdNames.add(DA.DADiskGetBSDName(disk));
                disk.release();
            }
            cfWhole.release();
            assertEquals(0, media.release());
            mediaObj = iter.next();
        }
        assertEquals(0, iter.release());

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
            Pointer result = CF.CFDictionaryGetValue(diskInfo, daMediaBSDName);
            CFStringRef bsdNamePtr = new CFStringRef(result);
            assertEquals(bsdName, bsdNamePtr.stringValue());
            result = CF.CFDictionaryGetValue(diskInfo, daMediaWhole);
            CFBooleanRef bsdWholePtr = new CFBooleanRef(result);
            assertTrue(bsdWholePtr.booleanValue());

            // Size is a multiple of block size
            result = CF.CFDictionaryGetValue(diskInfo, daMediaSize);
            CFNumberRef sizePtr = new CFNumberRef(result);
            long size = sizePtr.longValue();
            result = CF.CFDictionaryGetValue(diskInfo, daMediaBlockSize);
            CFNumberRef blockSizePtr = new CFNumberRef(result);
            long blockSize = blockSizePtr.longValue();
            assertEquals(0, size % blockSize);

            diskInfo.release();
            disk.release();
        }
        daMediaBSDName.release();
        daMediaWhole.release();
        daMediaLeaf.release();
        daMediaSize.release();
        daMediaBlockSize.release();

        session.release();
    }
}
