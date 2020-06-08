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
package com.sun.jna.platform.unix;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

/**
 * Utility class supporting variable-width types in the C Library
 */
public class LibCUtil {

    private static final NativeLibrary LIBC = NativeLibrary.getInstance("c");

    /** Size of a native <code>off_t</code> type, in bytes. */
    public static final int OFF_T_SIZE;
    static {
        // Observations shows, that without LFS, on linux, solaris, aix, mac OS and
        // windows (apart from cygwin32) sizeof(off_t) == sizeof(long)
        int size = Native.LONG_SIZE;
        // On 64-bit, off_t is 64 bit, otherwise test compiler flags that would create
        // 64-bit versions of off_t functions
        if (size < 8) {
            try {
                LIBC.getFunction("mmap64", Function.THROW_LAST_ERROR);
                // on 32-bit, mmap64 only exists when off_t is 64-bit
                size = 8;
            } catch (UnsatisfiedLinkError ex) {
            }
        }
        OFF_T_SIZE = size;
    }

    private static Function mmap;
    static {
        try {
            mmap = LIBC.getFunction("mmap64", Function.THROW_LAST_ERROR);
        } catch (UnsatisfiedLinkError ex) {
            mmap = LIBC.getFunction("mmap", Function.THROW_LAST_ERROR);
        }
    }

    private LibCUtil() {
    }

    /**
     * Creates a new mapping in the virtual address space of the calling process.
     *
     * @param addr
     *            The starting address for the new mapping.
     *            <p>
     *            If {@code addr} is NULL, then the kernel chooses the
     *            (page-aligned) address at which to create the mapping; this is the
     *            most portable method of creating a new mapping. If {@code addr} is
     *            not NULL, then the kernel takes it as a hint about where to place
     *            the mapping; on Linux, the kernel will pick a nearby page boundary
     *            (but always above or equal to the value specified by
     *            {@code /proc/sys/vm/mmap_min_addr}) and attempt to create the
     *            mapping there. If another mapping already exists there, the kernel
     *            picks a new address that may or may not depend on the hint. The
     *            address of the new mapping is returned as the result of the call.
     * @param length
     *            Specifies the length of the mapping (which must be greater than
     *            0).
     * @param prot
     *            describes the desired memory protection of the mapping (and must
     *            not conflict with the open mode of the file). It is either
     *            {@code PROT_NONE} or the bitwise OR of one or more of
     *            {@code PROT_READ}, {@code PROT_WRITE}, or {@code PROT_EXEC}.
     * @param flags
     *            determines whether updates to the mapping are visible to other
     *            processes mapping the same region, and whether updates are carried
     *            through to the underlying file. This behavior is determined by
     *            including exactly one of {@code MAP_SHARED},
     *            {@code MAP_SHARED_VALIDATE}, or {@code MAP_PRIVATE}. In addition,
     *            0 or more additional flags can be ORed in {@code flags}.
     * @param fd
     *            The file descriptor for the object to be mapped. After the
     *            {@code mmap()} call has returned, the file descriptor can be
     *            closed immediately without invalidating the mapping.
     * @param offset
     *            The contents of a file mapping (as opposed to an anonymous
     *            mapping), are initialized using {@code length} bytes starting at
     *            offset {@code offset} in the file (or other object) referred to by
     *            the file descriptor, {@code fd}. {@code offset} must be a multiple
     *            of the page size as returned by {@code sysconf(_SC_PAGE_SIZE)}.
     * @return On success, returns a pointer to the mapped area. On error, the value
     *         {@code MAP_FAILED} (that is, (void *) -1) is returned, and
     *         {@code errno} is set to indicate the cause of the error.
     */
    public static Pointer mmap(Pointer addr, long length, int prot, int flags, int fd, long offset) {
        Object[] params = new Object[6];
        params[0] = addr;
        if (Native.SIZE_T_SIZE == 4) {
            require32Bit(length, "length");
            params[1] = (int) length;
        } else {
            params[1] = length;
        }
        params[2] = prot;
        params[3] = flags;
        params[4] = fd;
        if (OFF_T_SIZE == 4) {
            require32Bit(offset, "offset");
            params[5] = (int) offset;
        } else {
            params[5] = offset;
        }
        return mmap.invokePointer(params);
    }

    public static void require32Bit(long val, String value) {
        if (val > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(value + " exceeds 32bit");
        }
    }
}
