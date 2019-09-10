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

import com.sun.jna.Memory;
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
import com.sun.jna.platform.mac.IOKit.IOObject;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.IOKit.IOService;
import com.sun.jna.platform.mac.IOKit.MachPort;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

public class IOKitTest {

    private static final CoreFoundation CF = CoreFoundation.INSTANCE;
    private static final IOKit IO = IOKit.INSTANCE;

    @Test
    public void testMatching() {
        PointerByReference masterPortPtr = new PointerByReference();
        assertEquals(0, IO.IOMasterPort(IOKit.MACH_PORT_NULL, masterPortPtr));
        MachPort masterPort = new MachPort(masterPortPtr.getValue());

        String match = "matching BSD Name";
        CFMutableDictionaryRef dict = IO.IOBSDNameMatching(masterPort, 0, match);
        CFStringRef bsdNameKey = CFStringRef.createCFString("BSD Name");
        Pointer result = CF.CFDictionaryGetValue(dict, bsdNameKey);
        CFStringRef cfBsdName = new CFStringRef(result);
        assertEquals(match, cfBsdName.stringValue());
        bsdNameKey.release();
        dict.release();

        match = "matching IOClass Name";
        dict = IO.IOServiceNameMatching(match);
        CFStringRef classNameKey = CFStringRef.createCFString("IONameMatch");
        result = CF.CFDictionaryGetValue(dict, classNameKey);
        CFStringRef cfClassName = new CFStringRef(result);
        assertEquals(match, cfClassName.stringValue());
        classNameKey.release();
        dict.release();

        match = "IOPlatformExpertDevice";
        dict = IO.IOServiceMatching(match);
        CFStringRef classKey = CFStringRef.createCFString("IOProviderClass");
        result = CF.CFDictionaryGetValue(dict, classKey);
        CFStringRef cfClass = new CFStringRef(result);
        assertEquals(match, cfClass.stringValue());
        classKey.release();

        // Get matching service (consumes dict reference)
        IOService platformExpertSvc = IO.IOServiceGetMatchingService(masterPort, dict);
        assertNotNull(platformExpertSvc.getPointer());
        IORegistryEntry platformExpert = new IORegistryEntry(platformExpertSvc.getPointer());
        // Get a single key
        CFStringRef serialKey = CFStringRef.createCFString("IOPlatformSerialNumber");
        CFTypeRef cfSerialAsType = IO.IORegistryEntryCreateCFProperty(platformExpert, serialKey,
                CF.CFAllocatorGetDefault(), 0);
        assertNotNull(cfSerialAsType);
        CFStringRef cfSerial = new CFStringRef(cfSerialAsType.getPointer());
        String serialNumber = cfSerial.stringValue();
        cfSerialAsType.release();

        assertEquals(12, serialNumber.length());
        // Get all the keys
        PointerByReference properties = new PointerByReference();
        assertEquals(0,
                IO.IORegistryEntryCreateCFProperties(platformExpert, properties, CF.CFAllocatorGetDefault(), 0));
        dict = new CFMutableDictionaryRef();
        dict.setPointer(properties.getValue());
        assertNotEquals(0, CF.CFDictionaryGetValueIfPresent(dict, serialKey, null));
        result = CF.CFDictionaryGetValue(dict, serialKey);
        cfSerial = new CFStringRef(result);
        assertEquals(serialNumber, cfSerial.stringValue());
        dict.release();
        assertEquals(0, platformExpert.release());

        // Get a single key from a nested entry
        IORegistryEntry root = IO.IORegistryGetRootEntry(masterPort);
        assertNotEquals(0, root);
        cfSerialAsType = IO.IORegistryEntrySearchCFProperty(root, "IOService", serialKey, CF.CFAllocatorGetDefault(),
                0);
        // without recursive search should be null
        assertNull(cfSerialAsType);
        cfSerialAsType = IO.IORegistryEntrySearchCFProperty(root, "IOService", serialKey, CF.CFAllocatorGetDefault(),
                IOKit.kIORegistryIterateRecursively);
        // with recursive search should return a match
        cfSerial = new CFStringRef(cfSerialAsType.getPointer());
        assertEquals(serialNumber, cfSerial.stringValue());
        serialKey.release();
        cfSerialAsType.release();

        assertEquals(0, root.release());
    }

    @Test
    public void testIteratorParentChild() {
        PointerByReference masterPortPtr = new PointerByReference();
        assertEquals(0, IO.IOMasterPort(IOKit.MACH_PORT_NULL, masterPortPtr));
        MachPort masterPort = new MachPort(masterPortPtr.getValue());

        Set<Long> uniqueEntryIdSet = new HashSet<>();
        // Create matching dictionary for USB Controller class
        CFMutableDictionaryRef dict = IO.IOServiceMatching("IOUSBController");
        // Iterate over USB Controllers. All devices are children of one of
        // these controllers in the "IOService" plane
        PointerByReference iterPtr = new PointerByReference();
        assertEquals(0, IO.IOServiceGetMatchingServices(masterPort, dict, iterPtr));
        IOIterator iter = new IOIterator(iterPtr.getValue());
        // iter is a pointer to first device; iterate until null
        IOObject controllerDeviceObj = iter.next();
        while (controllerDeviceObj != null) {
            IORegistryEntry controllerDevice = new IORegistryEntry(controllerDeviceObj.getPointer());
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
            PointerByReference firstChildPtr = new PointerByReference();
            boolean testFirstChild = true;
            // If this returns 0, we have at least one child entry to test
            // If not, the iterator will never check whether to test
            IO.IORegistryEntryGetChildEntry(controllerDevice, "IOService", firstChildPtr);

            // Now iterate the children of this device in the "IOService" plane.
            PointerByReference childIterPtr = new PointerByReference();
            IO.IORegistryEntryGetChildIterator(controllerDevice, "IOService", childIterPtr);
            IOIterator childIter = new IOIterator(childIterPtr.getValue());
            IOObject childDeviceObj = childIter.next();
            while (childDeviceObj != null) {
                assertTrue(IO.IOObjectConformsTo(childDeviceObj, "IOUSBDevice"));

                IORegistryEntry childDevice = new IORegistryEntry(childDeviceObj.getPointer());
                LongByReference childId = new LongByReference();
                IO.IORegistryEntryGetRegistryEntryID(childDevice, childId);
                assertTrue(childId.getValue() > 19);
                assertFalse(uniqueEntryIdSet.contains(childId.getValue()));
                uniqueEntryIdSet.add(childId.getValue());

                // If first child, test and release the retained first child pointer
                if (testFirstChild) {
                    IOObject firstChild = new IOObject(firstChildPtr.getValue());
                    assertEquals(childDevice, firstChild);
                    assertEquals(0, firstChild.release());
                    testFirstChild = false;
                }

                // Get this device's parent in IOService plane, matches controller
                PointerByReference parentPtr = new PointerByReference();
                IO.IORegistryEntryGetParentEntry(childDevice, "IOService", parentPtr);
                IORegistryEntry parent = new IORegistryEntry(parentPtr.getValue());
                assertEquals(controllerDevice, parent);
                assertEquals(0, parent.release());

                // Release this device and iterate to the next one
                assertEquals(0, childDevice.release());
                childDeviceObj = childIter.next();
            }
            assertEquals(0, childIter.release());

            // Release this controller and iterate to the next one
            assertEquals(0, controllerDevice.release());
            controllerDeviceObj = iter.next();
        }
        assertEquals(0, iter.release());
    }

    @Test
    public void testIOConnect() {
        PointerByReference masterPortPtr = new PointerByReference();
        assertEquals(0, IO.IOMasterPort(IOKit.MACH_PORT_NULL, masterPortPtr));
        MachPort masterPort = new MachPort(masterPortPtr.getValue());

        // Open a connection to SMC
        CFMutableDictionaryRef dict = IO.IOServiceMatching("AppleSMC");
        // consumes dict references
        IOService smcService = IO.IOServiceGetMatchingService(masterPort, dict);
        assertNotEquals(0, smcService);
        PointerByReference connPtr = new PointerByReference();

        MachPort taskSelf = SystemB.INSTANCE.mach_task_self_ptr();
        assertEquals(0, IO.IOServiceOpen(smcService, taskSelf, 0, connPtr));
        IOConnect conn = new IOConnect(connPtr.getValue());

        IntByReference busy = new IntByReference(Integer.MIN_VALUE);
        IO.IOServiceGetBusyState(smcService, busy);
        assertTrue(busy.getValue() >= 0);

        IO.IOServiceClose(conn);
        assertEquals(0, smcService.release());
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
        long powerSourcesCount = CF.CFArrayGetCount(powerSourcesList);
        for (int ps = 0; ps < powerSourcesCount; ps++) {
            // Get the dictionary for that Power Source
            Pointer pwrSrcPtr = CoreFoundation.INSTANCE.CFArrayGetValueAtIndex(powerSourcesList, ps);
            CFTypeRef powerSource = new CFTypeRef();
            powerSource.setPointer(pwrSrcPtr);
            CFDictionaryRef dictionary = IOKit.INSTANCE.IOPSGetPowerSourceDescription(powerSourcesInfo, powerSource);

            // Get values from dictionary (See IOPSKeys.h)
            // Skip if not present
            PointerByReference result = new PointerByReference();
            if (0 != CF.CFDictionaryGetValueIfPresent(dictionary, isPresentKey, result)) {
                CFBooleanRef isPresentRef = new CFBooleanRef(result.getValue());
                if (isPresentRef.booleanValue()) {
                    int currentCapacity = 0;
                    if (0 != CF.CFDictionaryGetValueIfPresent(dictionary, currentCapacityKey, result)) {
                        CFNumberRef cap = new CFNumberRef(result.getValue());
                        currentCapacity = cap.intValue();
                    }
                    int maxCapacity = 100;
                    if (0 != CF.CFDictionaryGetValueIfPresent(dictionary, maxCapacityKey, result)) {
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
