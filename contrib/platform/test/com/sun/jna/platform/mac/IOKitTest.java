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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation.CFArrayRef;
import com.sun.jna.platform.mac.CoreFoundation.CFBooleanRef;
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.platform.mac.IOKit.IOConnect;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.IOKit.IOService;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class IOKitTest {

    private static final IOKit IO = IOKit.INSTANCE;
    private static final SystemB SYS = SystemB.INSTANCE;

    private static final String IO_SERVICE = "IOService";
    private static final String IOUSB = "IOUSB";

    @Test
    public void testMatching() {
        int masterPort = IOKitUtil.getMasterPort();

        String match = "matching BSD Name";
        CFMutableDictionaryRef dict = IO.IOBSDNameMatching(masterPort, 0, match);
        CFStringRef bsdNameKey = CFStringRef.createCFString("BSD Name");
        Pointer result = dict.getValue(bsdNameKey);
        CFStringRef cfBsdName = new CFStringRef(result);
        assertEquals(match, cfBsdName.stringValue());
        bsdNameKey.release();
        dict.release();

        match = "matching IOClass Name";
        dict = IO.IOServiceNameMatching(match);
        CFStringRef classNameKey = CFStringRef.createCFString("IONameMatch");
        result = dict.getValue(classNameKey);
        CFStringRef cfClassName = new CFStringRef(result);
        assertEquals(match, cfClassName.stringValue());
        classNameKey.release();
        dict.release();

        match = "IOPlatformExpertDevice";
        dict = IO.IOServiceMatching(match);
        CFStringRef classKey = CFStringRef.createCFString("IOProviderClass");
        result = dict.getValue(classKey);
        CFStringRef cfClass = new CFStringRef(result);
        assertEquals(match, cfClass.stringValue());
        classKey.release();

        // Get matching service (consumes dict reference)
        IORegistryEntry platformExpert = IO.IOServiceGetMatchingService(masterPort, dict);
        // Get a single key
        CFStringRef serialKey = CFStringRef.createCFString("IOPlatformSerialNumber");
        CFTypeRef cfSerialAsType = platformExpert.createCFProperty(serialKey);
        assertNotNull(cfSerialAsType);
        CFStringRef cfSerial = new CFStringRef(cfSerialAsType.getPointer());
        String serialNumber = cfSerial.stringValue();

        // Test util method for the same thing
        String serialNumberViaUtil = platformExpert.getStringProperty("IOPlatformSerialNumber");
        assertEquals(serialNumber, serialNumberViaUtil);

        assertEquals(12, serialNumber.length());
        // Get all the keys
        dict = platformExpert.createCFProperties();
        assertNotEquals(0, dict.getValueIfPresent(serialKey, null));
        result = dict.getValue(serialKey);
        cfSerial = new CFStringRef(result);
        assertEquals(serialNumber, cfSerial.stringValue());
        dict.release();
        assertEquals(0, platformExpert.release());

        // Get a single key from a nested entry
        IORegistryEntry root = IOKitUtil.getRoot();
        assertNotNull(root);
        // Root should have no parent
        assertNull(root.getParentEntry(IO_SERVICE));
        // Follow down the chain for a child, shouldn't reach a depth of 50
        int treeDepth = 0;
        IORegistryEntry child = root.getChildEntry(IO_SERVICE);
        while (child != null && ++treeDepth < 50) {
            child = child.getChildEntry(IO_SERVICE);
        }
        assertNotEquals(50, treeDepth);

        cfSerialAsType = root.searchCFProperty(IO_SERVICE, serialKey, 0);
        // without recursive search should be null
        assertNull(cfSerialAsType);
        cfSerialAsType = root.searchCFProperty(IO_SERVICE, serialKey, IOKit.kIORegistryIterateRecursively);
        // with recursive search should return a match
        cfSerial = new CFStringRef(cfSerialAsType.getPointer());
        assertEquals(serialNumber, cfSerial.stringValue());
        serialKey.release();
        cfSerialAsType.release();

        assertEquals(0, root.release());
        assertEquals(0, SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort));
    }

    @Test
    public void testIteratorParentChild() {
        int masterPort = IOKitUtil.getMasterPort();

        Set<Long> uniqueEntryIdSet = new HashSet<Long>();
        // Iterate over USB Controllers. All devices are children of one of
        // these controllers in the "IOService" plane
        // On M1 the IOUSBController service doesn't exist so we navigate from root of
        // IOUSB plane to find the Root Hubs which have a 1:1 correspondence with
        // controllers
        IORegistryEntry root = IOKitUtil.getRoot();
        IOIterator iter = root.getChildIterator(IOUSB);
        assertNotNull(iter);
        IORegistryEntry rootHubDevice = iter.next();
        while (rootHubDevice != null) {
            // The parent of this device in IOService plane is the controller
            IORegistryEntry controllerDevice = rootHubDevice.getParentEntry(IO_SERVICE);
            assertEquals(0, rootHubDevice.release());

            long id = controllerDevice.getRegistryEntryID();
            // EntryIDs 0 thru 19 are reserved, all are unique
            assertTrue(id > 19);
            assertFalse(uniqueEntryIdSet.contains(id));
            uniqueEntryIdSet.add(id);

            // Get device name
            String controllerName = controllerDevice.getName();
            // Root controllers always begin with "AppleUSB"
            assertEquals("AppleUSB", controllerName.substring(0, 8));

            // Get the first child, to test vs. iterator
            boolean testFirstChild = true;
            IORegistryEntry firstChild = controllerDevice.getChildEntry(IO_SERVICE);

            // Now iterate the children of this device in the "IOService" plane.
            IOIterator childIter = controllerDevice.getChildIterator(IO_SERVICE);
            IORegistryEntry childDevice = childIter.next();
            while (childDevice != null) {
                assertTrue(childDevice.conformsTo("IOUSBDevice"));
                long childId = childDevice.getRegistryEntryID();
                assertTrue(childId > 19);
                assertFalse(uniqueEntryIdSet.contains(childId));
                uniqueEntryIdSet.add(childId);

                // If first child, test and release the retained first child pointer
                if (testFirstChild) {
                    assertEquals(childDevice, firstChild);
                    assertEquals(0, firstChild.release());
                    testFirstChild = false;
                }

                // Get this device's parent in IOService plane, matches controller
                IORegistryEntry parent = childDevice.getParentEntry(IO_SERVICE);
                assertEquals(controllerDevice, parent);
                assertEquals(0, parent.release());

                // Release this device and iterate to the next one
                assertEquals(0, childDevice.release());
                childDevice = childIter.next();
            }
            assertEquals(0, childIter.release());

            // Release this controller and iterate to the next one
            assertEquals(0, controllerDevice.release());
            rootHubDevice = iter.next();
        }
        assertEquals(0, iter.release());
        assertEquals(0, root.release());
        assertEquals(0, SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort));
    }

    @Test
    public void testIOConnect() {
        int masterPort = IOKitUtil.getMasterPort();

        IOService smcService = IOKitUtil.getMatchingService("AppleSMC");
        assertNotNull(smcService);

        PointerByReference connPtr = new PointerByReference();
        int taskSelf = SYS.mach_task_self();
        assertEquals(0, IO.IOServiceOpen(smcService, taskSelf, 0, connPtr));
        IOConnect conn = new IOConnect(connPtr.getValue());

        IntByReference busy = new IntByReference(Integer.MIN_VALUE);
        IO.IOServiceGetBusyState(smcService, busy);
        assertTrue(busy.getValue() >= 0);

        IO.IOServiceClose(conn);
        assertEquals(0, smcService.release());
        assertEquals(0, SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort));
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

        CFStringRef isPresentKey = CFStringRef.createCFString("Is Present");
        CFStringRef currentCapacityKey = CFStringRef.createCFString("Current Capacity");
        CFStringRef maxCapacityKey = CFStringRef.createCFString("Max Capacity");
        int powerSourcesCount = powerSourcesList.getCount();
        for (int ps = 0; ps < powerSourcesCount; ps++) {
            // Get the dictionary for that Power Source
            Pointer pwrSrcPtr = powerSourcesList.getValueAtIndex(ps);
            CFTypeRef powerSource = new CFTypeRef(pwrSrcPtr);
            CFDictionaryRef dictionary = IOKit.INSTANCE.IOPSGetPowerSourceDescription(powerSourcesInfo, powerSource);

            // Get values from dictionary (See IOPSKeys.h)
            // Skip if not present
            PointerByReference result = new PointerByReference();
            if (dictionary.getValueIfPresent(isPresentKey, result)) {
                CFBooleanRef isPresentRef = new CFBooleanRef(result.getValue());
                if (isPresentRef.booleanValue()) {
                    int currentCapacity = 0;
                    if (dictionary.getValueIfPresent(currentCapacityKey, result)) {
                        CFNumberRef cap = new CFNumberRef(result.getValue());
                        currentCapacity = cap.intValue();
                    }
                    int maxCapacity = 100;
                    if (dictionary.getValueIfPresent(maxCapacityKey, result)) {
                        CFNumberRef cap = new CFNumberRef(result.getValue());
                        maxCapacity = cap.intValue();
                    }
                    assertTrue(currentCapacity <= maxCapacity);
                }
            }
        }
        isPresentKey.release();
        currentCapacityKey.release();
        maxCapacityKey.release();
        powerSourcesList.release();
        powerSourcesInfo.release();
    }
}
