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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;

/**
 * libudev.h provides APIs to introspect and enumerate devices on the local
 * system. Udev objects are opaque and must not be accessed by the caller via
 * different means than functions provided by libudev.
 */
public interface Udev extends Library {

    Udev INSTANCE = Native.load("udev", Udev.class);

    /*
     * Opaque udev pointers are implemented via PointerType to restrict their usage
     * to this API.
     */

    /**
     * All functions require a libudev context to operate. This context can be
     * create via {@link #udev_new}. It is used to track library state and link
     * objects together. No global state is used by libudev, everything is always
     * linked to a udev context. Furthermore, multiple different udev contexts can
     * be used in parallel by multiple threads. However, a single context must not
     * be accessed by multiple threads in parallel. The caller is responsible for
     * providing suitable locking if they intend to use it from multiple threads.
     */
    class UdevContext extends PointerType {
    }

    /**
     * To enumerate local devices on the system, an enumeration object can be
     * created via {@link #udev_enumerate_new}.
     */
    class UdevEnumerate extends PointerType {
    }

    /**
     * Whenever libudev returns a list of objects, the {@code udev_list_entry} API
     * should be used to iterate, access and modify those lists.
     */
    class UdevListEntry extends PointerType {
    }

    /**
     * To introspect a local device on a system, a udev device object can be created
     * via {@link udev_device_new_from_syspath} and friends. The device object
     * allows one to query current state, read and write attributes and lookup
     * properties of the device in question.
     */
    class UdevDevice extends PointerType {
    }

    /**
     * Allocates a new udev context object and returns a pointer to it. This object
     * is opaque and must not be accessed by the caller via different means than
     * functions provided by libudev. Initially, the reference count of the context
     * is 1.
     *
     * @return On success, returns a pointer to the allocated udev context. On
     *         failure, NULL is returned.
     */
    UdevContext udev_new();

    /**
     * Acquire further references to a udev context object.
     *
     * @param udev
     *            A udev context object.
     * @return the argument that was passed, unmodified.
     */
    UdevContext udev_ref(UdevContext udev);

    /**
     * Drop a reference to a udev context object. Once the reference count hits 0,
     * the context object is destroyed and freed.
     *
     * @param udev
     *            A udev context object.
     * @return always returns NULL.
     */
    UdevContext udev_unref(UdevContext udev);

    /**
     * Create a udev enumerate object. Initially, the reference count of the context
     * is 1.
     *
     * @return On success, returns a pointer to the returns a pointer to the
     *         allocated udev monitor. On failure, NULL is returned.
     */
    UdevEnumerate udev_enumerate_new(UdevContext udev);

    /**
     * Acquire further references to a udev enumerate object.
     *
     * @param udev
     *            A udev context object.
     * @return the argument that was passed, unmodified.
     */
    UdevEnumerate udev_enumerate_ref(UdevEnumerate udev_enumerate);

    /**
     * Drop a reference to a udev enumerate object. Once the reference count hits 0,
     * the enumerate object is destroyed and freed.
     *
     * @param udev
     *            A udev context object.
     * @return always returns NULL.
     */
    UdevEnumerate udev_enumerate_unref(UdevEnumerate udev_enumerate);

    /**
     * Modify filters of a udev enumerate object to match a subsystem.
     *
     * @param udev_enumerate
     *            The udev enumerate object to modify.
     * @param subsystem
     *            The subsystem to match
     * @return an integer greater than, or equal to, 0 on success.
     */
    int udev_enumerate_add_match_subsystem(UdevEnumerate udev_enumerate, String subsystem);

    /**
     * Query a udev enumerate object. Scans {@code /sys} for all devices which match
     * the given filters. No matches will return all currently available devices.
     *
     * @param udev_enumerate
     *            The udev enumerate object, with optional filters.
     * @return an integer greater than, or equal to, 0 on success.
     */
    int udev_enumerate_scan_devices(UdevEnumerate udev_enumerate);

    /**
     * Get the first list entry from a udev enumerate object.
     *
     * @param udev_enumerate
     *            The udev enumerate object.
     * @return On success, returns a pointer to the first entry in the list of found
     *         devices. If the list is empty, or on failure, NULL is returned.
     */
    UdevListEntry udev_enumerate_get_list_entry(UdevEnumerate udev_enumerate);

    /**
     * Gets the next entry in the enumeration.
     *
     * @param list_entry
     *            the current device.
     * @return On success, returns a pointer to the next list entry. If no such
     *         entry can be found, or on failure, NULL is returned.
     */
    UdevListEntry udev_list_entry_get_next(UdevListEntry list_entry);

    /**
     * Get the name of the udev list entry
     *
     * @param list_entry
     *            A udev list entry
     * @return a pointer to a constant string representing the requested value. The
     *         string is bound to the lifetime of the list entry itself. On failure,
     *         NULL is returned.
     */
    String udev_list_entry_get_name(UdevListEntry list_entry);

    /**
     * Allocates a new udev device object and returns a pointer to it. This object
     * is opaque and must not be accessed by the caller via different means than
     * functions provided by libudev. Initially, the reference count of the device
     * is 1.
     * <p>
     * Creates the device object based on information found in {@code /sys},
     * annotated with properties from the udev-internal device database. A syspath
     * is any subdirectory of {@code /sys}, with the restriction that a subdirectory
     * of {@code /sys/devices} (or a symlink to one) represents a real device and as
     * such must contain a uevent file.
     *
     * @param udev
     *            A udev context object.
     * @param syspath
     *            The path of the device in {@code /sys}.
     * @return a pointer to the allocated udev device. On failure, NULL is returned,
     *         and {@code errno} is set appropriately.
     */
    UdevDevice udev_device_new_from_syspath(UdevContext udev, String syspath);

    /**
     * Acquire further references to a udev device object.
     *
     * @param udev
     *            A udev device object.
     * @return the argument that was passed, unmodified.
     */
    UdevDevice udev_device_ref(UdevDevice udev_device);

    /**
     * Drop a reference to a udev device object. Once the reference count hits 0,
     * the device object is destroyed and freed.
     *
     * @param udev
     *            A udev device object.
     * @return always returns NULL.
     */
    UdevDevice udev_device_unref(UdevDevice udev_device);

    /**
     * Gets the parent of a udev device
     *
     * @param udev_device
     *            THe udev device
     * @return a pointer to the parent device. No additional reference to this
     *         device is acquired, but the child device owns a reference to such a
     *         parent device. On failure, NULL is returned.
     */
    UdevDevice udev_device_get_parent(UdevDevice udev_device);

    /**
     * Gets the parent of a udev device matching a subsystem and devtype
     *
     * @param udev_device
     *            THe udev device
     * @return a pointer to the parent device. No additional reference to this
     *         device is acquired, but the child device owns a reference to such a
     *         parent device. On failure, NULL is returned.
     */
    UdevDevice udev_device_get_parent_with_subsystem_devtype(UdevDevice udev_device, String subsystem, String devtype);

    /**
     * Gets the syspath of a udev device
     *
     * @param udev_device
     *            The udev device
     * @return a pointer to a constant string that describes the syspath. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_syspath(UdevDevice udev_device);

    /**
     * Gets the sysname of a udev device
     *
     * @param udev_device
     *            The udev device
     * @return a pointer to a constant string that describes the sysname. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_sysname(UdevDevice udev_device);

    /**
     * Gets the devnode of a udev device
     *
     * @param udev_device
     *            The udev device
     * @return a pointer to a constant string that describes the devnode. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_devnode(UdevDevice udev_device);

    /**
     * Gets the devtype of a udev device
     *
     * @param udev_device
     *            The udev device
     * @return a pointer to a constant string that describes the devtype. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_devtype(UdevDevice udev_device);

    /**
     * Gets the subsystem of a udev device
     *
     * @param udev_device
     *            The udev device
     * @return a pointer to a constant string that describes the subsystem. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_subsystem(UdevDevice udev_device);

    /**
     * Retrieves a device attributesfrom a udev device.
     *
     * @param udev_device
     *            The udev device
     * @param sysattr
     *            The attribute to retrieve.
     * @return a pointer to a constant string of the requested value. On error, NULL
     *         is returned. Attributes that may contain NUL bytes should not be
     *         retrieved with udev_device_get_sysattr_value(); instead, read them
     *         directly from the files within the device's syspath.
     */
    String udev_device_get_sysattr_value(UdevDevice udev_device, String sysattr);

    /**
     * Retrieves a device property from a udev device.
     *
     * @param udev_device
     *            The udev device
     * @param sysattr
     *            The attribute to retrieve.
     * @return a pointer to a constant string of the requested value. On error, NULL
     *         is returned.
     */
    String udev_device_get_property_value(UdevDevice udev_device, String key);
}
