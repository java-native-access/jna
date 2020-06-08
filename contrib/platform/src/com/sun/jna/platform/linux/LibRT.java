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

/**
 * POSIX.1b Realtime Extensions library (librt). Functions in this library
 * provide most of the interfaces specified by the POSIX.1b Realtime Extension.
 */
public interface LibRT extends Library {

    LibRT INSTANCE = Native.load("rt", LibRT.class);

    /**
     * Creates and opens a new, or opens an existing, POSIX shared memory object. A
     * POSIX shared memory object is in effect a handle which can be used by
     * unrelated processes to {@code mmap()} the same region of shared memory.
     *
     * @param name
     *            The shared memory object to be created or opened. For portable
     *            use, a shared memory object should be identified by a name of the
     *            form {@code /somename} that is, a null-terminated string of up to
     *            {@code NAME_MAX} (i.e., 255) characters consisting of an initial
     *            slash, followed by one or more characters, none of which are
     *            slashes.
     * @param oflag
     *            A bit mask created by ORing together exactly one of
     *            {@code O_RDONLY} or {@code O_RDWR} and any of the other flags
     *            {@code O_CREAT}, {@code O_EXCL}, or {@code O_TRUNC}.
     * @param mode
     *            When {@code oflag} includes {@code O_CREAT}, the object's
     *            permission bits are set according to the low-order 9 bits of mode,
     *            except that those bits set in the process file mode creation mask
     *            (see {@code umask(2)}) are cleared for the new object.
     * @return On success, returns a file descriptor (a nonnegative integer). On
     *         failure, returns -1. On failure, {@code errno} is set to indicate the
     *         cause of the error.
     */
    int shm_open(String name, int oflag, int mode);

    /**
     * Removes an object previously created by {@link #shm_open}.
     *
     * @param name
     *            The shared memory object to be unlinked.
     * @return returns 0 on success, or -1 on error. On failure, {@code errno} is
     *         set to indicate the cause of the error.
     */
    int shm_unlink(String name);
}
