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

import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.IOKit.IOService;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Provides utilities for IOKit.
 */
public class IOKitUtil {
    private static final IOKit IO = IOKit.INSTANCE;
    private static final SystemB SYS = SystemB.INSTANCE;

    private IOKitUtil() {
    }

    /**
     * Gets a pointer to the Mach Master Port.
     *
     * @return The master port.
     *         <p>
     *         Multiple calls to {@link #getMasterPort} will not result in leaking
     *         ports (each call to {@link IOKit#IOMasterPort} adds another send
     *         right to the port) but it is considered good programming practice to
     *         deallocate the port when you are finished with it, using
     *         {@link SystemB#mach_port_deallocate}.
     */
    public static int getMasterPort() {
        IntByReference port = new IntByReference();
        IO.IOMasterPort(0, port);
        return port.getValue();
    }

    /**
     * Gets the IO Registry root.
     *
     * @return a handle to the IORoot. Callers should release when finished, using
     *         {@link IOKit#IOObjectRelease}.
     */
    public static IORegistryEntry getRoot() {
        int masterPort = getMasterPort();
        IORegistryEntry root = IO.IORegistryGetRootEntry(masterPort);
        SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
        return root;
    }

    /**
     * Opens a the first IOService matching a service name.
     *
     * @param serviceName
     *            The service name to match
     * @return a handle to an IOService if successful, {@code null} if failed.
     *         Callers should release when finished, using
     *         {@link IOKit#IOObjectRelease}.
     */
    public static IOService getMatchingService(String serviceName) {
        CFMutableDictionaryRef dict = IO.IOServiceMatching(serviceName);
        if (dict != null) {
            return getMatchingService(dict);
        }
        return null;
    }

    /**
     * Opens a the first IOService matching a dictionary.
     *
     * @param matchingDictionary
     *            The dictionary to match. This method will consume a reference to
     *            the dictionary.
     * @return a handle to an IOService if successful, {@code null} if failed.
     *         Callers should release when finished, using
     *         {@link IOKit#IOObjectRelease}.
     */
    public static IOService getMatchingService(CFDictionaryRef matchingDictionary) {
        int masterPort = getMasterPort();
        IOService service = IO.IOServiceGetMatchingService(masterPort, matchingDictionary);
        SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
        return service;
    }

    /**
     * Convenience method to get IOService objects matching a service name.
     *
     * @param serviceName
     *            The service name to match
     * @return a handle to an IOIterator if successful, {@code null} if failed.
     *         Callers should release when finished, using
     *         {@link IOKit#IOObjectRelease}.
     */
    public static IOIterator getMatchingServices(String serviceName) {
        CFMutableDictionaryRef dict = IO.IOServiceMatching(serviceName);
        if (dict != null) {
            return getMatchingServices(dict);
        }
        return null;
    }

    /**
     * Convenience method to get IOService objects matching a dictionary.
     *
     * @param matchingDictionary
     *            The dictionary to match. This method will consume a reference to
     *            the dictionary.
     * @return a handle to an IOIterator if successful, {@code null} if failed.
     *         Callers should release when finished, using
     *         {@link IOKit#IOObjectRelease}.
     */
    public static IOIterator getMatchingServices(CFDictionaryRef matchingDictionary) {
        int masterPort = getMasterPort();
        PointerByReference serviceIterator = new PointerByReference();
        int result = IO.IOServiceGetMatchingServices(masterPort, matchingDictionary, serviceIterator);
        SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
        if (result == 0 && serviceIterator.getValue() != null) {
            return new IOIterator(serviceIterator.getValue());
        }
        return null;
    }

    /**
     * Convenience method to get the IO dictionary matching a bsd name.
     *
     * @param bsdName
     *            The bsd name of the registry entry
     * @return The dictionary ref if successful, {@code null} if failed. Callers
     *         should release when finished, using {@link IOKit#IOObjectRelease}.
     */
    public static CFMutableDictionaryRef getBSDNameMatchingDict(String bsdName) {
        int masterPort = getMasterPort();
        CFMutableDictionaryRef result = IO.IOBSDNameMatching(masterPort, 0, bsdName);
        SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
        return result;
    }
}
