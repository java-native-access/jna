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

import com.sun.jna.Pointer;

/**
 * Definitions for POSIX memory map interface from {@code mman.h}
 */
public interface Mman {
    /*
     * Protections are chosen from these bits, OR'd together. The implementation
     * does not necessarily support PROT_EXEC or PROT_WRITE without PROT_READ. The
     * only guarantees are that no writing will be allowed without PROT_WRITE and no
     * access will be allowed for PROT_NONE.
     */
    int PROT_READ = 0x1; // Page can be read.
    int PROT_WRITE = 0x2; // Page can be written.
    int PROT_EXEC = 0x4; // Page can be executed.
    int PROT_NONE = 0x0; // Page can not be accessed.
    int PROT_GROWSDOWN = 0x01000000; // Extend change to start of growsdown vma (mprotect only).
    int PROT_GROWSUP = 0x02000000; // Extend change to start of growsup vma (mprotect only).

    /* Sharing types (must choose one and only one of these). */
    int MAP_SHARED = 0x01; // Share changes.
    int MAP_PRIVATE = 0x02; // Changes are private.
    int MAP_SHARED_VALIDATE = 0x03; // share + validate extension flags
    int MAP_TYPE = 0x0f; // Mask for type of mapping

    /* Other flags. */
    int MAP_FILE = 0; // Compatibility flag. Ignored.
    int MAP_FIXED = 0x10; // Interpret addr exactly.
    int MAP_ANONYMOUS = 0x20; // Don't use a file.
    int MAP_ANON = MAP_ANONYMOUS;
    int MAP_32BIT = 0x40; // Only give out 32-bit addresses.

    /* These are Linux-specific. */
    int MAP_GROWSDOWN = 0x00100; // Stack-like segment.
    int MAP_DENYWRITE = 0x00800; // ETXTBSY
    int MAP_EXECUTABLE = 0x01000; // Mark it as an executable.
    int MAP_LOCKED = 0x02000; // Lock the mapping.
    int MAP_NORESERVE = 0x04000; // Don't check for reservations.
    int MAP_POPULATE = 0x08000; // Populate (prefault) pagetables.
    int MAP_NONBLOCK = 0x10000; // Do not block on IO.
    int MAP_STACK = 0x20000; // Allocation is for a stack.
    int MAP_HUGETLB = 0x40000; // create a huge page mapping
    int MAP_SYNC = 0x80000; // perform synchronous page faults for the mapping
    int MAP_FIXED_NOREPLACE = 0x100000; // MAP_FIXED which doesn't unmap underlying mapping

    Pointer MAP_FAILED = new Pointer(-1); // ((void *)-1)

    /* Flags for msync. */
    int MS_ASYNC = 1;
    int MS_SYNC = 2;
    int MS_INVALIDATE = 4;
}
