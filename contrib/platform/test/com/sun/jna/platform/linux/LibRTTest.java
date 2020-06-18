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

import static com.sun.jna.platform.linux.ErrNo.EEXIST;
import static com.sun.jna.platform.linux.Fcntl.O_CREAT;
import static com.sun.jna.platform.linux.Fcntl.O_EXCL;
import static com.sun.jna.platform.linux.Fcntl.O_RDWR;
import static com.sun.jna.platform.linux.Fcntl.S_IRWXU;
import static com.sun.jna.platform.linux.Mman.MAP_FAILED;
import static com.sun.jna.platform.linux.Mman.MAP_SHARED;
import static com.sun.jna.platform.linux.Mman.MS_SYNC;
import static com.sun.jna.platform.linux.Mman.PROT_READ;
import static com.sun.jna.platform.linux.Mman.PROT_WRITE;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import org.junit.Test;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI.size_t;
import com.sun.jna.platform.unix.LibCUtil;

import junit.framework.TestCase;

public class LibRTTest extends TestCase {

    public static LibC LIBC = LibC.INSTANCE;
    public static LibRT LIBRT = LibRT.INSTANCE;

    @Test
    public void testMmapToShm() throws IOException {
        // Get a suitably random filename of the form "/somename" to use as both the
        // share virtual filename and the string to store
        // Use same algorithm as File.CreateTempFile without creating a file
        long n = new SecureRandom().nextLong();
        if (n == Long.MIN_VALUE) {
            n = 0; // corner case
        } else {
            n = Math.abs(n);
        }
        String share = "/mmapToShm" + Long.toString(n) + "test";
        // Get a file descriptor to the share.
        int fd = LIBRT.shm_open(share, O_RDWR | O_CREAT | O_EXCL, S_IRWXU);
        assertNotEquals("Failed to shm_open " + share + ". Error: " + Native.getLastError(), -1, fd);
        try {
            // Multiply by 4 to handle all possible encodings
            int bufLen = 4 * (share.length() + 1);
            size_t length = new size_t(bufLen);
            // Allocate memory to the share (fills with null bytes)
            int ret = LibCUtil.ftruncate(fd, bufLen);
            assertNotEquals("Failed to ftruncate. Error: " + Native.getLastError(), -1, ret);
            // Map a pointer to the share. Offset must be a multiple of page size
            Pointer p = LibCUtil.mmap(null, bufLen, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
            assertNotEquals("Failed mmap to new share. Error: " + Native.getLastError(), MAP_FAILED, p);
            // We can now close the file descriptor
            ret = LIBC.close(fd);
            assertNotEquals("Failed to close file descriptor. Error: " + Native.getLastError(), -1, ret);
            // Write some bytes to the share. The name is a suitable candidate
            p.setString(0, share);
            // Sync from memory to share
            ret = LIBC.msync(p, length, MS_SYNC);
            assertNotEquals("Failed msync. Error: " + Native.getLastError(), -1, ret);
            // Unmap the share
            ret = LIBC.munmap(p, length);
            assertNotEquals("Failed munmap. Error: " + Native.getLastError(), -1, ret);
            // p now points to invalid memory
            p = null;

            // Get another file descriptor to the same share.
            // Calling with both O_CREAT | O_EXCL should fail since the share already exists
            fd = LIBRT.shm_open(share, O_RDWR | O_CREAT | O_EXCL, S_IRWXU);
            assertEquals("Re-creating existing share should have failed", -1, fd);
            assertEquals("Re-creating existing share errno should be EEXIST", EEXIST, Native.getLastError());
            // So let's not recreate it, instead get a file descriptor to the existing share
            fd = LIBRT.shm_open(share, O_RDWR, S_IRWXU);
            assertNotEquals("Failed to re-open " + share + ". Error: " + Native.getLastError(), -1, fd);
            // Map another pointer to the share. Use the util version to test it
            Pointer q = LibCUtil.mmap(null, bufLen, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
            assertNotEquals("Failed mmap to existing share. Error: " + Native.getLastError(), MAP_FAILED, q);
            // Close the file descriptor
            ret = LIBC.close(fd);
            assertNotEquals("Failed to close file descriptor. Error: " + Native.getLastError(), -1, ret);
            // Check that the bytes we wrote are still there
            assertEquals("Bytes written to share don't match", share, q.getString(0));
            // Unmap the share
            ret = LIBC.munmap(q, length);
            assertNotEquals("Failed munmap. Error: " + Native.getLastError(), -1, ret);
            // q now points to invalid memory
            q = null;

            // Unlink the share
            ret = LIBRT.shm_unlink(share);
            assertNotEquals("Failed to shm_unlink " + share + ". Error: " + Native.getLastError(), -1, ret);
            // Should be able to re-create now
            fd = LIBRT.shm_open(share, O_RDWR | O_CREAT | O_EXCL, S_IRWXU);
            assertNotEquals("Failed to reopen unlinked " + share + ". Error: " + Native.getLastError(), -1, fd);
        } finally {
            LIBRT.shm_unlink(share);
        }
    }
}
