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
import static org.junit.Assert.assertFalse;
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
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class DiskArbitrationTest {

    private static final DiskArbitration DA = DiskArbitration.INSTANCE;
    private static final CoreFoundation CF = CoreFoundation.INSTANCE;
    private static final IOKit IO = IOKit.INSTANCE;
    private static final SystemB SYS = SystemB.INSTANCE;

    @Test
    public void testDiskCreate() {
        IntByReference masterPortPtr = new IntByReference();
        assertEquals(0, IO.IOMasterPort(0, masterPortPtr));
        int masterPort = masterPortPtr.getValue();

        // Create some keys we'll need
        CFStringRef daMediaBSDName = CFStringRef.createCFString("DAMediaBSDName");
        CFStringRef daMediaWhole = CFStringRef.createCFString("DAMediaWhole");
        CFStringRef daMediaLeaf = CFStringRef.createCFString("DAMediaLeaf");
        CFStringRef daMediaSize = CFStringRef.createCFString("DAMediaSize");
        CFStringRef daMediaBlockSize = CFStringRef.createCFString("DAMediaBlockSize");
        CFStringRef wholeKey = CFStringRef.createCFString("Whole");

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
        IORegistryEntry media = iter.next();
        while (media != null) {
            CFTypeRef cfWhole = media.createCFProperty(wholeKey);
            assertNotNull(cfWhole);
            CFBooleanRef cfWholeBool = new CFBooleanRef(cfWhole.getPointer());
            assertEquals(CoreFoundation.BOOLEAN_TYPE_ID, cfWholeBool.getTypeID());
            if (cfWholeBool.booleanValue()) {
                // check that util boolean matches
                assertTrue(media.getBooleanProperty("Whole"));
                // check long, int, double values for major
                Long majorLong = media.getLongProperty("BSD Major");
                Integer majorInt = media.getIntegerProperty("BSD Major");
                Double majorDouble = media.getDoubleProperty("BSD Major");
                assertNotNull(majorLong);
                assertNotNull(majorInt);
                assertNotNull(majorDouble);
                assertEquals(majorLong.intValue(), majorInt.intValue());
                assertEquals(majorDouble.doubleValue(), majorInt.doubleValue(), 1e-15);

                DADiskRef disk = DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), session, media);
                bsdNames.add(DA.DADiskGetBSDName(disk));
                disk.release();
            } else {
                assertFalse(media.getBooleanProperty("Whole"));
            }
            cfWhole.release();
            assertEquals(0, media.release());
            media = iter.next();
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
            Pointer result = diskInfo.getValue(daMediaBSDName);
            CFStringRef bsdNamePtr = new CFStringRef(result);
            assertEquals(bsdName, bsdNamePtr.stringValue());
            result = diskInfo.getValue(daMediaWhole);
            CFBooleanRef bsdWholePtr = new CFBooleanRef(result);
            assertTrue(bsdWholePtr.booleanValue());

            // Size is a multiple of block size
            result = diskInfo.getValue(daMediaSize);
            CFNumberRef sizePtr = new CFNumberRef(result);
            long size = sizePtr.longValue();
            result = diskInfo.getValue(daMediaBlockSize);
            CFNumberRef blockSizePtr = new CFNumberRef(result);
            long blockSize = blockSizePtr.longValue();
            assertEquals(0, size % blockSize);

            diskInfo.release();
            disk.release();
        }
        wholeKey.release();
        daMediaBSDName.release();
        daMediaWhole.release();
        daMediaLeaf.release();
        daMediaSize.release();
        daMediaBlockSize.release();

        session.release();
        assertEquals(0, SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort));
    }
}
