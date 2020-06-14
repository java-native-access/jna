/* Copyright (c) 2015 Goldstein Lyor, All Rights Reserved
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

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * Note: we are using this &quot;intermediate&quot; API in order to allow
 * Linux-like O/S-es to implement the same API, but maybe using a different
 * library name
 * @author Lyor Goldstein
 */
public interface LibCAPI extends Reboot, Resource {

    /**
     * This is an unsigned integer type used to represent the sizes of objects.
     */
    class size_t extends IntegerType {
        public static final size_t ZERO = new size_t();

        private static final long serialVersionUID = 1L;

        public size_t() {
            this(0);
        }

        public size_t(long value) {
            super(Native.SIZE_T_SIZE, value, true);
        }
    }

    /**
     * This is a signed integer type used for a count of bytes or an error
     * indication.
     */
    class ssize_t extends IntegerType {
        public static final ssize_t ZERO = new ssize_t();

        private static final long serialVersionUID = 1L;

        public ssize_t() {
            this(0);
        }

        public ssize_t(long value) {
            super(Native.SIZE_T_SIZE, value, false);
        }
    }

    /**
     * This is a signed integer type used to represent file sizes.
     * <p>
     * Authors of portable applications should be aware that on 32-bit operating
     * systems, the bit width of this type may be dependent on compile-time options
     * in the end-user's library. The parameter {@code ilp32OffBig} permits this
     * type to be defined as 64-bit on a 32-bit operating system.
     * <p>
     * Portable versions of C library methods which ensure appropriate bit width for
     * {@code off_t} arguments are defined in {@link LibCUtil}.
     *
     * @see <A HREF=
     *      "https://pubs.opengroup.org/onlinepubs/009695399/utilities/c99.html#tagtcjh_11">IEEE
     *      Std 1003.1, 2004 (POSIXv6)</A>
     * @see <A HREF=
     *      "https://pubs.opengroup.org/onlinepubs/9699919799/utilities/c99.html#tagtcjh_24">IEEE
     *      Std 1003.1-2017 (POSIX v7)</A>
     */
    class off_t extends IntegerType {

        private static final long serialVersionUID = 1L;

        /**
         * Create a new {@code off_t} using the default bit width.
         */
        public off_t() {
            this(0, false);
        }

        /**
         * Create a new {@code off_t} using the default bit width or 64-bit if
         * specified.
         *
         * @param ilp32OffBig
         *            If {@code true}, use 64-bit width.
         */
        public off_t(boolean ilp32OffBig) {
            this(0, ilp32OffBig);
        }

        /**
         * Create a new {@code off_t} using the default bit width.
         *
         * @param value
         *            The value to set.
         */
        public off_t(long value) {
            this(value, false);
        }

        /**
         * Create a new {@code off_t} using the default bit width or 64-bit if
         * specified.
         *
         * @param value
         *            The value to set.
         * @param ilp32OffBig
         *            If {@code true}, use 64-bit width.
         */
        public off_t(long value, boolean ilp32OffBig) {
            super(ilp32OffBig ? 8 : Native.LONG_SIZE, value);
        }
    }

    // see man(2) get/set uid/gid
    int getuid();
    int geteuid();
    int getgid();
    int getegid();

    int setuid(int uid);
    int seteuid(int uid);
    int setgid(int gid);
    int setegid(int gid);

    // see man(2) get/set hostname
    int HOST_NAME_MAX = 255; // not including the '\0'
    int gethostname(byte[] name, int len);
    int sethostname(String name, int len);

    // see man(2) get/set domainname
    int getdomainname(byte[] name, int len);
    int setdomainname(String name, int len);

    /**
     * @param name Environment variable name
     * @return Returns the value in the environment, or {@code null} if there
     * is no match for the name
     * @see <A HREF="https://www.freebsd.org/cgi/man.cgi?query=setenv&sektion=3">getenv(3)</A>
     */
    String getenv(String name);

    /**
     * Update or add a variable in the environment of the calling process.
     * @param name Environment variable name
     * @param value Required value
     * @param overwrite  If the environment variable already exists and the
     * value of {@code overwrite} is non-zero, the function shall return
     * success and the environment shall be updated. If the environment
     * variable already exists and the value of {@code overwrite} is zero, the
     * function shall return success and the environment shall remain unchanged.
     * @return Upon successful completion, zero shall be returned. Otherwise,
     * -1 shall be returned, {@code errno} set to indicate the error, and the
     * environment shall be unchanged
     * @see <A HREF="https://www.freebsd.org/cgi/man.cgi?query=setenv&sektion=3">getenv(3)</A>
     */
    int setenv(String name, String value, int overwrite);

    /**
     * @param name Environment variable name - If the named variable does not
     * exist in the current environment, the environment shall be unchanged
     * and the function is considered to have completed successfully.
     * @return Upon successful completion, zero shall be returned. Otherwise,
     * -1 shall be returned, {@code errno} set to indicate the error, and the
     * environment shall be unchanged
     * @see <A HREF="https://www.freebsd.org/cgi/man.cgi?query=setenv&sektion=3">getenv(3)</A>
     */
    int unsetenv(String name);

    /**
     * The getloadavg() function returns the number of processes in the system
     * run queue averaged over various periods of time.  Up to nelem samples are
     * retrieved and assigned to successive elements of loadavg[].  The system
     * imposes a maximum of 3 samples, representing averages over the last 1, 5,
     * and 15 minutes, respectively.
     * @param loadavg An array of doubles which will be filled with the results
     * @param nelem Number of samples to return
     * @return If the load average was unobtainable, -1 is returned; otherwise,
     * the number of samples actually retrieved is returned.
     * @see <A HREF="https://www.freebsd.org/cgi/man.cgi?query=getloadavg&sektion=3">getloadavg(3)</A>
     */
    int getloadavg(double[] loadavg, int nelem);

    /**
     * Closes a file descriptor, so that it no longer refers to any file and may be
     * reused. Any record locks held on the file it was associated with, and owned
     * by the process, are removed (regardless of the file descriptor that was used
     * to obtain the lock).
     * <p>
     * If {@code fd} is the last file descriptor referring to the underlying open
     * file description, the resources associated with the open file description are
     * freed; if the file descriptor was the last reference to a file which has been
     * removed using {@code unlink}, the file is deleted.
     *
     * @param fd
     *            a file descriptor
     * @return returns zero on success. On error, -1 is returned, and {@code errno}
     *         is set appropriately.
     *         <p>
     *         {@code close()} should not be retried after an error.
     */
    int close(int fd);

    /**
     * Flushes changes made to the in-core copy of a file that was mapped into
     * memory using {@link LibCUtil#mmap(Pointer, long, int, int, int, long)} back
     * to the filesystem. Without use of this call, there is no guarantee that
     * changes are written back before {@link #munmap(Pointer, size_t)} is called.
     * To be more precise, the part of the file that corresponds to the memory area
     * starting at {@code addr} and having length {@code length} is updated.
     *
     * @param addr
     *            The start of the memory area to sync to the filesystem.
     * @param length
     *            The length of the memory area to sync to the filesystem.
     * @param flags
     *            The flags argument should specify exactly one of {@code MS_ASYNC}
     *            and {@code MS_SYNC}, and may additionally include the
     *            {@code MS_INVALIDATE} bit.
     * @return On success, zero is returned. On error, -1 is returned, and
     *         {@code errno} is set appropriately.
     */
    int msync(Pointer addr, size_t length, int flags);

    /**
     * Deletes the mappings for the specified address range, and causes further
     * references to addresses within the range to generate invalid memory
     * references. The region is also automatically unmapped when the process is
     * terminated. On the other hand, closing the file descriptor does not unmap the
     * region.
     * <p>
     * It is not an error if the indicated range does not contain any mapped pages.
     *
     * @param addr
     *            The base address from which to delete mappings. The address addr
     *            must be a multiple of the page size (but length need not be).
     * @param length
     *            The length from the base address to delete mappings. All pages
     *            containing a part of the indicated range are unmapped, and
     *            subsequent references to these pages will generate
     *            {@code SIGSEGV}.
     * @return On success, returns 0. On failure, it returns -1, and {@code errno}
     *         is set to indicate the cause of the error (probably to
     *         {@code EINVAL}).
     */
    int munmap(Pointer addr, size_t length);
}
