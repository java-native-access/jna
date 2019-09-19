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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.mac.CoreFoundation.CFAllocatorRef;
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.platform.mac.IOKit.IOObject;

/**
 * Disk Arbitration is a low-level framework based on Core Foundation. The Disk
 * Arbitration framework provides the ability to get various pieces of
 * information about a volume.
 */
public interface DiskArbitration extends Library {

    DiskArbitration INSTANCE = Native.load("DiskArbitration", DiskArbitration.class);

    /**
     * Type of a reference to {@code DASession} instances.
     */
    class DASessionRef extends CFTypeRef {
    }

    /**
     * Type of a reference to {@code DADisk} instances.
     */
    class DADiskRef extends CFTypeRef {
    }

    /**
     * Creates a new session. The caller of this function receives a reference to
     * the returned object.
     * <p>
     * The caller also implicitly retains the object and is responsible for
     * releasing it with {@link CoreFoundation#CFRelease}.
     *
     * @param alloc
     *            The allocator object to be used to allocate memory.
     * @return A reference to a new {@code DASession}.
     */
    DASessionRef DASessionCreate(CFAllocatorRef alloc);

    /**
     * Creates a new disk object. The caller of this function receives a reference
     * to the returned object.
     * <p>
     * The caller also implicitly retains the object and is responsible for
     * releasing it with {@link CoreFoundation#CFRelease}.
     *
     * @param alloc
     *            The allocator object to be used to allocate memory.
     * @param session
     *            The {@code DASession} in which to contact Disk Arbitration.
     * @param diskName
     *            the BSD device name.
     * @return A reference to a new {@code DADisk}.
     */
    DADiskRef DADiskCreateFromBSDName(CFAllocatorRef alloc, DASessionRef session, String diskName);

    /**
     * Creates a new disk object. The caller of this function receives a reference
     * to the returned object.
     * <p>
     * The caller also implicitly retains the object and is responsible for
     * releasing it with {@link CoreFoundation#CFRelease}.
     *
     * @param allocator
     *            The allocator object to be used to allocate memory.
     * @param session
     *            The {@code DASession} in which to contact Disk Arbitration.
     * @param media
     *            The I/O Kit media object.
     * @return A reference to a new {@code DADisk}.
     */
    DADiskRef DADiskCreateFromIOMedia(CFAllocatorRef allocator, DASessionRef session, IOObject media);

    /**
     * Obtains the Disk Arbitration description of the specified disk. This function
     * will contact Disk Arbitration to acquire the latest description of the
     * specified disk, unless this function is called on a disk object passed within
     * the context of a registered callback, in which case the description is
     * current as of that callback event.
     * <p>
     * The caller of this function receives a reference to the returned object. The
     * caller also implicitly retains the object and is responsible for releasing it
     * with {@link CoreFoundation#CFRelease}.
     *
     * @param disk
     *            The {@code DADisk} for which to obtain the Disk Arbitration
     *            description.
     * @return The disk's Disk Arbitration description.
     */
    CFDictionaryRef DADiskCopyDescription(DADiskRef disk);

    /**
     * Obtains the BSD device name for the specified disk.
     *
     * @param disk
     *            The {@code DADisk} for which to obtain the BSD device name.
     * @return The disk's BSD device name.
     */
    String DADiskGetBSDName(DADiskRef disk);
}
