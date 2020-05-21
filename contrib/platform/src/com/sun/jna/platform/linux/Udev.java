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

    /**
     * All functions require a libudev context to operate. This context can be
     * created via {@link #udev_new}. It is used to track library state and link
     * objects together. No global state is used by libudev, everything is always
     * linked to a udev context. Furthermore, multiple different udev contexts can
     * be used in parallel by multiple threads. However, a single context must not
     * be accessed by multiple threads in parallel. The caller is responsible for
     * providing suitable locking if they intend to use it from multiple threads.
     */
    class UdevContext extends PointerType {
        /**
         * Acquire a further reference to this object.
         *
         * @return this object, unmodified.
         */
        UdevContext ref() {
            return INSTANCE.udev_ref(this);
        }

        /**
         * Drop a reference to this object. Once the reference count hits 0, the context
         * object is destroyed and freed.
         */
        public void unref() {
            INSTANCE.udev_unref(this);
        }

        /**
         * Create a udev enumerate object. Initially, the reference count of the
         * enumerate object is 1.
         *
         * @return On success, returns the allocated enumerator. On failure, NULL is
         *         returned.
         */
        public UdevEnumerate enumerateNew() {
            return INSTANCE.udev_enumerate_new(this);
        }

        /**
         * Creates a udev device object based on information found in {@code /sys},
         * annotated with properties from the udev-internal device database. Initially,
         * the reference count of the device is 1.
         *
         * @param syspath
         *            The path of the device in {@code /sys}.
         * @return the allocated udev device. On failure, NULL is returned, and
         *         {@code errno} is set appropriately.
         */
        public UdevDevice deviceNewFromSyspath(String syspath) {
            return INSTANCE.udev_device_new_from_syspath(this, syspath);
        }
    }

    /**
     * To enumerate local devices on the system, an enumeration object can be
     * created via {@link UdevContext#enumerateNew()}.
     */
    class UdevEnumerate extends PointerType {
        /**
         * Acquire a further reference to this object.
         *
         * @return this object, unmodified.
         */
        public UdevEnumerate ref() {
            return INSTANCE.udev_enumerate_ref(this);
        }

        /**
         * Drop a reference to this object. Once the reference count hits 0, the context
         * object is destroyed and freed.
         */
        public void unref() {
            INSTANCE.udev_enumerate_unref(this);
        }

        /**
         * Modify filters of this object to match a subsystem.
         *
         * @param subsystem
         *            The subsystem to match
         * @return an integer greater than, or equal to, 0 on success.
         */
        public int addMatchSubsystem(String subsystem) {
            return INSTANCE.udev_enumerate_add_match_subsystem(this, subsystem);
        }

        /**
         * Query this object. Scans {@code /sys} for all devices which match the given
         * filters. No filters will return all currently available devices.
         *
         * @return an integer greater than, or equal to, 0 on success.
         */
        public int scanDevices() {
            return INSTANCE.udev_enumerate_scan_devices(this);
        }

        /**
         * Get the first list entry from this object.
         *
         * @return On success, returns the first entry in the list of found devices. If
         *         the list is empty, or on failure, NULL is returned.
         */
        public UdevListEntry getListEntry() {
            return INSTANCE.udev_enumerate_get_list_entry(this);
        }
    }

    /**
     * Whenever libudev returns a list of objects, the {@code udev_list_entry} API
     * should be used to iterate, access and modify those lists.
     */
    class UdevListEntry extends PointerType {
        /**
         * Gets the next entry in the enumeration.
         *
         * @return On success, returns the next list entry. If no such entry can be
         *         found, or on failure, NULL is returned.
         */
        public UdevListEntry getNext() {
            return INSTANCE.udev_list_entry_get_next(this);
        }

        /**
         * Get the name of this entry, which is the path of the device in {@code /sys}.
         *
         * @return A string representing the syspath. On failure, NULL is returned.
         */
        public String getName() {
            return INSTANCE.udev_list_entry_get_name(this);
        }
    }

    /**
     * To introspect a local device on a system, a udev device object can be created
     * via {@link UdevContext#deviceNewFromSyspath(String)} and friends. The device
     * object allows one to query current state, read and write attributes and
     * lookup properties of the device in question.
     */
    class UdevDevice extends PointerType {
        /**
         * Acquire a further reference to this object.
         *
         * @return this object, unmodified.
         */
        public UdevDevice ref() {
            return INSTANCE.udev_device_ref(this);
        }

        /**
         * Drop a reference to this object. Once the reference count hits 0, the context
         * object is destroyed and freed.
         */
        public void unref() {
            INSTANCE.udev_device_unref(this);
        }

        /**
         * Gets the parent of this device
         *
         * @return the parent device. No additional reference to this device is
         *         acquired, but the child device owns a reference to the parent device.
         *         On failure, NULL is returned.
         */
        public UdevDevice getParent() {
            return INSTANCE.udev_device_get_parent(this);
        }

        /**
         * Gets the parent of this device matching a subsystem and devtype
         *
         * @param subsystem
         *            The subsystem to match
         * @param devtype
         *            The device type to match
         * @return the parent device. No additional reference to this device is
         *         acquired, but the child device owns a reference to the parent device.
         *         On failure, NULL is returned.
         */
        public UdevDevice getParentWithSubsystemDevtype(String subsystem, String devtype) {
            return INSTANCE.udev_device_get_parent_with_subsystem_devtype(this, subsystem, devtype);
        }

        /**
         * Gets the syspath of this device
         *
         * @return a string that describes the syspath. On failure, may return NULL.
         */
        public String getSyspath() {
            return INSTANCE.udev_device_get_syspath(this);
        }

        /**
         * Gets the sysname of this device
         *
         * @return a string that describes the sysname. On failure, may return NULL.
         */
        public String getSysname() {
            return INSTANCE.udev_device_get_syspath(this);
        }

        /**
         * Gets the devnode of this device
         *
         * @return a string that describes the devnode. On failure, may return NULL.
         */
        public String getDevnode() {
            return INSTANCE.udev_device_get_devnode(this);
        }

        /**
         * Gets the devtype of this device
         *
         * @return a string that describes the devtype. On failure, may return NULL.
         */
        public String getDevtype() {
            return INSTANCE.udev_device_get_devtype(this);
        }

        /**
         * Gets the subsystem of this device
         *
         * @return a string that describes the subsystem. On failure, may return NULL.
         */
        public String getSubsystem() {
            return INSTANCE.udev_device_get_subsystem(this);
        }

        /**
         * Retrieves a device attribute from this device
         *
         * @param sysattr
         *            The attribute to retrieve.
         * @return a string of the requested value. On error, NULL is returned.
         *         Attributes that may contain NUL bytes should not be retrieved with
         *         udev_device_get_sysattr_value(); instead, read them directly from the
         *         files within the device's syspath.
         */
        public String getSysattrValue(String sysattr) {
            return INSTANCE.udev_device_get_sysattr_value(this, sysattr);
        }

        /**
         * Retrieves a device property from this device
         *
         * @param key
         *            The property to retrieve.
         * @return a string of the requested value. On error, NULL is returned.
         */
        public String getPropertyValue(String key) {
            return INSTANCE.udev_device_get_property_value(this, key);
        }
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
     */
    void udev_unref(UdevContext udev);

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
     * Create a udev enumerate object. Initially, the reference count of the
     * enumerate object is 1.
     *
     * @param udev
     *            A udev context object.
     * @return On success, returns a pointer to the allocated udev monitor. On
     *         failure, NULL is returned.
     */
    UdevEnumerate udev_enumerate_new(UdevContext udev);

    /**
     * Acquire further references to a udev enumerate object.
     *
     * @param udev_enumerate
     *            A udev enumerate object.
     * @return the argument that was passed, unmodified.
     */
    UdevEnumerate udev_enumerate_ref(UdevEnumerate udev_enumerate);

    /**
     * Drop a reference to a udev enumerate object. Once the reference count hits 0,
     * the enumerate object is destroyed and freed.
     *
     * @param udev_enumerate
     *            A udev enumerate object.
     */
    void udev_enumerate_unref(UdevEnumerate udev_enumerate);

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
     *            the current list entry
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
     * Acquire further references to a udev device object.
     *
     * @param udev_device
     *            A udev device object.
     * @return the argument that was passed, unmodified.
     */
    UdevDevice udev_device_ref(UdevDevice udev_device);

    /**
     * Drop a reference to a udev device object. Once the reference count hits 0,
     * the device object is destroyed and freed.
     *
     * @param udev_device
     *            A udev device object.
     */
    void udev_device_unref(UdevDevice udev_device);

    /**
     * Gets the parent of a udev device
     *
     * @param udev_device
     *            A udev device object.
     * @return a pointer to the parent device. No additional reference to this
     *         device is acquired, but the child device owns a reference to such a
     *         parent device. On failure, NULL is returned.
     */
    UdevDevice udev_device_get_parent(UdevDevice udev_device);

    /**
     * Gets the parent of a udev device matching a subsystem and devtype
     *
     * @param udev_device
     *            A udev device object.
     * @param subsystem
     *            The subsystem to match
     * @param devtype
     *            The device type to match
     * @return a pointer to the parent device. No additional reference to this
     *         device is acquired, but the child device owns a reference to such a
     *         parent device. On failure, NULL is returned.
     */
    UdevDevice udev_device_get_parent_with_subsystem_devtype(UdevDevice udev_device, String subsystem, String devtype);

    /**
     * Gets the syspath of a udev device
     *
     * @param udev_device
     *            A udev device object.
     * @return a pointer to a constant string that describes the syspath. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_syspath(UdevDevice udev_device);

    /**
     * Gets the sysname of a udev device
     *
     * @param udev_device
     *            A udev device object.
     * @return a pointer to a constant string that describes the sysname. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_sysname(UdevDevice udev_device);

    /**
     * Gets the devnode of a udev device
     *
     * @param udev_device
     *            A udev device object.
     * @return a pointer to a constant string that describes the devnode. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_devnode(UdevDevice udev_device);

    /**
     * Gets the devtype of a udev device
     *
     * @param udev_device
     *            A udev device object.
     * @return a pointer to a constant string that describes the devtype. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_devtype(UdevDevice udev_device);

    /**
     * Gets the subsystem of a udev device
     *
     * @param udev_device
     *            A udev device object.
     * @return a pointer to a constant string that describes the subsystem. The
     *         lifetime of this string is bound to the device it was requested on.
     *         On failure, may return NULL.
     */
    String udev_device_get_subsystem(UdevDevice udev_device);

    /**
     * Retrieves a device attributesfrom a udev device.
     *
     * @param udev_device
     *            A udev device object.
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
     *            A udev device object.
     * @param key
     *            The property to retrieve.
     * @return a pointer to a constant string of the requested value. On error, NULL
     *         is returned.
     */
    String udev_device_get_property_value(UdevDevice udev_device, String key);
}