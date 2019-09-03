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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation.CFArrayRef;
import com.sun.jna.platform.mac.CoreFoundation.CFBooleanRef;
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

public class IOKitTest {

    private static final CoreFoundation CF = CoreFoundation.INSTANCE;
    private static final IOKit IO = IOKit.INSTANCE;

    @Test
    public void testMatching() {
        LongByReference masterPortPtr = new LongByReference();
        assertEquals(0, IO.IOMasterPort(0, masterPortPtr));
        long masterPort = masterPortPtr.getValue();

        String match = "matching BSD Name";
        CFMutableDictionaryRef dict = IO.IOBSDNameMatching(masterPort, 0, match);
        CFStringRef bsdNameKey = CFStringRef.toCFString("BSD Name");
        Pointer result = CF.CFDictionaryGetValue(dict, bsdNameKey);
        CFStringRef cfBsdName = new CFStringRef(result);
        assertEquals(match, CoreFoundationUtil.cfPointerToString(cfBsdName));
        release(bsdNameKey);
        release(dict);

        match = "matching IOClass Name";
        dict = IO.IOServiceNameMatching(match);
        CFStringRef classNameKey = CFStringRef.toCFString("IONameMatch");
        result = CF.CFDictionaryGetValue(dict, classNameKey);
        CFStringRef cfClassName = new CFStringRef(result);
        assertEquals(match, CoreFoundationUtil.cfPointerToString(cfClassName));
        release(classNameKey);
        release(dict);

        match = "IOPlatformExpertDevice";
        dict = IO.IOServiceMatching(match);
        CFStringRef classKey = CFStringRef.toCFString("IOProviderClass");
        result = CF.CFDictionaryGetValue(dict, classKey);
        CFStringRef cfClass = new CFStringRef(result);
        assertEquals(match, CoreFoundationUtil.cfPointerToString(cfClass));
        release(classKey);

        // Get matching service (consumes dict reference)
        long platformExpert = IO.IOServiceGetMatchingService(masterPort, dict);
        assertNotEquals(0, platformExpert);
        // Get a single key
        CFStringRef serialKey = CFStringRef.toCFString("IOPlatformSerialNumber");
        CFTypeRef cfSerialAsType = IO.IORegistryEntryCreateCFProperty(platformExpert, serialKey,
                CF.CFAllocatorGetDefault(), 0);
        assertNotNull(cfSerialAsType);
        CFStringRef cfSerial = new CFStringRef(cfSerialAsType.getPointer());
        String serialNumber = CoreFoundationUtil.cfPointerToString(cfSerial);
        release(cfSerialAsType);
        assertEquals(12, serialNumber.length());
        // Get all the keys
        PointerByReference properties = new PointerByReference();
        assertEquals(0,
                IO.IORegistryEntryCreateCFProperties(platformExpert, properties, CF.CFAllocatorGetDefault(), 0));
        dict = new CFMutableDictionaryRef();
        dict.setPointer(properties.getValue());
        assertTrue(CF.CFDictionaryGetValueIfPresent(dict, serialKey, null));
        result = CF.CFDictionaryGetValue(dict, serialKey);
        cfSerial = new CFStringRef(result);
        assertEquals(serialNumber, CoreFoundationUtil.cfPointerToString(cfSerial));
        release(dict);
        assertEquals(0, IO.IOObjectRelease(platformExpert));

        // Get a single key from a nested entry
        long root = IO.IORegistryGetRootEntry(masterPort);
        assertNotEquals(0, root);
        cfSerialAsType = IO.IORegistryEntrySearchCFProperty(root, "IOService", serialKey, CF.CFAllocatorGetDefault(),
                0);
        // without recursive search should be null
        assertNull(cfSerialAsType);
        cfSerialAsType = IO.IORegistryEntrySearchCFProperty(root, "IOService", serialKey, CF.CFAllocatorGetDefault(),
                IOKit.kIORegistryIterateRecursively);
        // with recursive search should return a match
        cfSerial = new CFStringRef(cfSerialAsType.getPointer());
        assertEquals(serialNumber, CoreFoundationUtil.cfPointerToString(cfSerial));
        release(serialKey);
        release(cfSerialAsType);

        assertEquals(0, IO.IOObjectRelease(root));
        assertEquals(0, IO.IOObjectRelease(masterPort));
    }

    @Test
    public void testIteratorParentChild() {
        LongByReference masterPortPtr = new LongByReference();
        assertEquals(0, IO.IOMasterPort(0, masterPortPtr));
        long masterPort = masterPortPtr.getValue();

        Set<Long> uniqueEntryIdSet = new HashSet<>();
        // Create matching dictionary for USB Controller class
        CFMutableDictionaryRef dict = IO.IOServiceMatching("IOUSBController");
        // Iterate over USB Controllers. All devices are children of one of
        // these controllers in the "IOService" plane
        LongByReference iter = new LongByReference();
        assertEquals(0, IO.IOServiceGetMatchingServices(masterPort, dict, iter));
        // iter is a pointer to first device; iterate until 0
        long controllerDevice = IO.IOIteratorNext(iter.getValue());
        while (controllerDevice != 0) {
            LongByReference id = new LongByReference();
            IO.IORegistryEntryGetRegistryEntryID(controllerDevice, id);
            // EntryIDs 0 thru 19 are reserved, all are unique
            assertTrue(id.getValue() > 19);
            assertFalse(uniqueEntryIdSet.contains(id.getValue()));
            uniqueEntryIdSet.add(id.getValue());

            // Get device name
            // Corresponds to io_name_t which is char[128]
            Memory buffer = new Memory(128);
            IO.IORegistryEntryGetName(controllerDevice, buffer);
            // Root controllers always begin with "AppleUSB"
            assertEquals("AppleUSB", buffer.getString(0).substring(0, 8));

            // Get the first child, to test vs. iterator
            LongByReference firstChild = new LongByReference();
            boolean testFirstChild = 0 == IO.IORegistryEntryGetChildEntry(controllerDevice, "IOService", firstChild);

            // Now iterate the children of this device in the "IOService" plane.
            LongByReference childIter = new LongByReference();
            IO.IORegistryEntryGetChildIterator(controllerDevice, "IOService", childIter);
            long childDevice = IO.IOIteratorNext(childIter.getValue());
            while (childDevice != 0) {
                assertTrue(IO.IOObjectConformsTo(childDevice, "IOUSBDevice"));

                LongByReference childId = new LongByReference();
                IO.IORegistryEntryGetRegistryEntryID(childDevice, childId);
                assertTrue(childId.getValue() > 19);
                assertFalse(uniqueEntryIdSet.contains(childId.getValue()));
                uniqueEntryIdSet.add(childId.getValue());

                // If first child, test and release
                if (testFirstChild) {
                    assertEquals(childDevice, firstChild.getValue());
                    IO.IOObjectRelease(firstChild.getValue());
                    testFirstChild = false;
                }

                // Get this device's parent in IOService plane, matches controller
                LongByReference parent = new LongByReference();
                IO.IORegistryEntryGetParentEntry(childDevice, "IOService", parent);
                assertEquals(controllerDevice, parent.getValue());
                IO.IOObjectRelease(parent.getValue());

                // Release this device and iterate to the next one
                IO.IOObjectRelease(childDevice);
                childDevice = IO.IOIteratorNext(childIter.getValue());
            }
            IO.IOObjectRelease(childIter.getValue());

            // Release this controller and iterate to the next one
            assertEquals(0, IO.IOObjectRelease(controllerDevice));
            controllerDevice = IO.IOIteratorNext(iter.getValue());
        }
        assertEquals(0, IO.IOObjectRelease(iter.getValue()));
        assertEquals(0, IO.IOObjectRelease(masterPort));
    }

    @Test
    public void testIOConnect() {
        LongByReference masterPortPtr = new LongByReference();
        assertEquals(0, IO.IOMasterPort(0, masterPortPtr));
        long masterPort = masterPortPtr.getValue();

        // Open a connection to SMC
        CFMutableDictionaryRef dict = IO.IOServiceMatching("AppleSMC");
        // consumes dict references
        long smcService = IO.IOServiceGetMatchingService(masterPort, dict);
        assertNotEquals(0, smcService);
        LongByReference conn = new LongByReference();
        assertEquals(0, IO.IOServiceOpen(smcService, SystemB.INSTANCE.mach_task_self(), 0, conn));

        IntByReference busy = new IntByReference(Integer.MIN_VALUE);
        IO.IOServiceGetBusyState(smcService, busy);
        assertTrue(busy.getValue() >= 0);

        IO.IOServiceClose(conn.getValue());
        IO.IOObjectRelease(smcService);
        assertEquals(0, IO.IOObjectRelease(masterPort));
    }

    @Test
    public void testPowerSources() {
        CFTypeRef powerSourcesInfo = IO.IOPSCopyPowerSourcesInfo();
        assertNotNull(powerSourcesInfo);
        CFArrayRef powerSourcesList = IO.IOPSCopyPowerSourcesList(powerSourcesInfo);
        assertNotNull(powerSourcesList);
        double timeRemaining = IO.IOPSGetTimeRemainingEstimate();
        assertTrue(timeRemaining > 0 || timeRemaining == IOKit.kIOPSTimeRemainingUnknown
                || timeRemaining == IOKit.kIOPSTimeRemainingUnlimited);

        CFStringRef isPresentKey = CFStringRef.toCFString("Is Present");
        CFStringRef currentCapacityKey = CFStringRef.toCFString("Current Capacity");
        CFStringRef maxCapacityKey = CFStringRef.toCFString("Max Capacity");
        int powerSourcesCount = CF.CFArrayGetCount(powerSourcesList);
        for (int ps = 0; ps < powerSourcesCount; ps++) {
            // Get the dictionary for that Power Source
            CFTypeRef powerSource = CoreFoundation.INSTANCE.CFArrayGetValueAtIndex(powerSourcesList, ps);
            CFDictionaryRef dictionary = IOKit.INSTANCE.IOPSGetPowerSourceDescription(powerSourcesInfo, powerSource);

            // Get values from dictionary (See IOPSKeys.h)
            // Skip if not present
            PointerByReference result = new PointerByReference();
            if (CF.CFDictionaryGetValueIfPresent(dictionary, isPresentKey, result)) {
                CFBooleanRef isPresentRef = new CFBooleanRef(result.getValue());
                if (CF.CFBooleanGetValue(isPresentRef)) {
                    int currentCapacity = 0;
                    if (CF.CFDictionaryGetValueIfPresent(dictionary, currentCapacityKey, result)) {
                        CFNumberRef cap = new CFNumberRef(result.getValue());
                        currentCapacity = CoreFoundationUtil.cfPointerToInt(cap);
                    }
                    int maxCapacity = 100;
                    if (CF.CFDictionaryGetValueIfPresent(dictionary, maxCapacityKey, result)) {
                        CFNumberRef cap = new CFNumberRef(result.getValue());
                        maxCapacity = CoreFoundationUtil.cfPointerToInt(cap);
                    }
                    assertTrue(currentCapacity <= maxCapacity);
                }
            }
        }
        release(isPresentKey);
        release(currentCapacityKey);
        release(maxCapacityKey);
        release(powerSourcesList);
        release(powerSourcesInfo);
    }
}
