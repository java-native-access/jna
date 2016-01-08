/*
 * Copyright (c) 2015 Daniel Widdis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sun.jna.platform.mac;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Author: Daniel Widdis Date: 6/5/15
 */
public interface SystemB extends Library {

    SystemB INSTANCE = Native.loadLibrary("System", SystemB.class);

    // host_statistics()
    int HOST_LOAD_INFO = 1;// System loading stats
    int HOST_VM_INFO = 2; // Virtual memory stats
    int HOST_CPU_LOAD_INFO = 3;// CPU load stats

    // host_statistics64()
    int HOST_VM_INFO64 = 4; // 64-bit virtual memory stats

    // host_cpu_load_info()
    int CPU_STATE_MAX = 4;
    int CPU_STATE_USER = 0;
    int CPU_STATE_SYSTEM = 1;
    int CPU_STATE_IDLE = 2;
    int CPU_STATE_NICE = 3;

    // host_processor_info() flavor
    int PROCESSOR_BASIC_INFO = 1;
    int PROCESSOR_CPU_LOAD_INFO = 2;

    // Data size
    int UINT64_SIZE = Native.getNativeSize(long.class);
    int INT_SIZE = Native.getNativeSize(int.class);

    public static class HostCpuLoadInfo extends Structure {
        public int cpu_ticks[] = new int[CPU_STATE_MAX];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("cpu_ticks");
        }
    }

    public static class HostLoadInfo extends Structure {
        public int[] avenrun = new int[3]; // scaled by LOAD_SCALE
        public int[] mach_factor = new int[3]; // scaled by LOAD_SCALE

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("avenrun", "mach_factor");
        }
    }

    public static class VMStatistics extends Structure {
        public int free_count; // # of pages free
        public int active_count; // # of pages active
        public int inactive_count; // # of pages inactive
        public int wire_count; // # of pages wired down
        public int zero_fill_count; // # of zero fill pages
        public int reactivations; // # of pages reactivated
        public int pageins; // # of pageins
        public int pageouts; // # of pageouts
        public int faults; // # of faults
        public int cow_faults; // # of copy-on-writes
        public int lookups; // object cache lookups
        public int hits; // object cache hits
        public int purgeable_count; // # of pages purgeable
        public int purges; // # of pages purged
        // # of pages speculative (included in free_count)
        public int speculative_count;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("free_count", "active_count",
                                 "inactive_count", "wire_count", "zero_fill_count",
                                 "reactivations", "pageins", "pageouts", "faults",
                                 "cow_faults", "lookups", "hits", "purgeable_count",
                                 "purges", "speculative_count");
        }
    }

    public static class VMStatistics64 extends Structure {
        public int free_count; // # of pages free
        public int active_count; // # of pages active
        public int inactive_count; // # of pages inactive
        public int wire_count; // # of pages wired down
        public long zero_fill_count; // # of zero fill pages
        public long reactivations; // # of pages reactivated
        public long pageins; // # of pageins
        public long pageouts; // # of pageouts
        public long faults; // # of faults
        public long cow_faults; // # of copy-on-writes
        public long lookups; // object cache lookups
        public long hits; // object cache hits
        public long purges; // # of pages purged
        public int purgeable_count;
        public int speculative_count;
        public long decompressions; // # of pages decompressed
        public long compressions; // # of pages compressed
        // # of pages swapped in (via compression segments)
        public long swapins;
        // # of pages swapped out (via compression segments)
        public long swapouts;
        // # of pages used by the compressed pager to hold all the
        // compressed data
        public int compressor_page_count;
        public int throttled_count; // # of pages throttled
        // # of pages that are file-backed (non-swap)
        public int external_page_count;
        public int internal_page_count; // # of pages that are anonymous
        // # of pages (uncompressed) held within the compressor.
        public long total_uncompressed_pages_in_compressor;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("free_count", "active_count",
                                 "inactive_count", "wire_count",
                                 "zero_fill_count", "reactivations",
                                 "pageins", "pageouts",
                                 "faults", "cow_faults",
                                 "lookups", "hits",
                                 "purges",
                                 "purgeable_count", "speculative_count",
                                 "decompressions", "compressions",
                                 "swapins", "swapouts",
                                 "compressor_page_count", "throttled_count",
                                 "external_page_count", "internal_page_count",
                                 "total_uncompressed_pages_in_compressor");
        }
    }

    /**
     * The mach_host_self system call returns the calling thread's host name
     * port. It has an effect equivalent to receiving a send right for the host
     * port.
     *
     * @return the host's name port
     */
    int mach_host_self();

    /**
     * The host_page_size function returns the page size for the given host.
     *
     * @param machPort
     *            The name (or control) port for the host for which the page
     *            size is desired.
     * @param pPageSize
     *            The host's page size (in bytes), set on success.
     * @return 0 on success; sets errno on failure
     */
    int host_page_size(int machPort, LongByReference pPageSize);

    /**
     * The host_statistics function returns scheduling and virtual memory
     * statistics concerning the host as specified by hostStat.
     *
     * @param machPort
     *            The control port for the host for which information is to be
     *            obtained.
     * @param hostStat
     *            The type of statistics desired (HOST_LOAD_INFO, HOST_VM_INFO,
     *            or HOST_CPU_LOAD_INFO)
     * @param stats
     *            Statistics about the specified host.
     * @param count
     *            On input, the maximum size of the buffer; on output, the size
     *            returned (in natural-sized units).
     * @return 0 on success; sets errno on failure
     */
    int host_statistics(int machPort, int hostStat, Structure stats, IntByReference count);

    /**
     * The host_statistics64 function returns 64-bit virtual memory statistics
     * concerning the host as specified by hostStat.
     *
     * @param machPort
     *            The control port for the host for which information is to be
     *            obtained.
     * @param hostStat
     *            The type of statistics desired (HOST_VM_INFO64)
     * @param stats
     *            Statistics about the specified host.
     * @param count
     *            On input, the maximum size of the buffer; on output, the size
     *            returned (in natural-sized units).
     * @return 0 on success; sets errno on failure
     */
    int host_statistics64(int machPort, int hostStat, Structure stats, IntByReference count);

    /**
     * The sysctl() function retrieves system information and allows processes
     * with appropriate privileges to set system information. The information
     * available from sysctl() consists of integers, strings, and tables.
     *
     * The state is described using a "Management Information Base" (MIB) style
     * name, listed in name, which is a namelen length array of integers.
     *
     * The information is copied into the buffer specified by oldp. The size of
     * the buffer is given by the location specified by oldlenp before the call,
     * and that location gives the amount of data copied after a successful call
     * and after a call that returns with the error code ENOMEM. If the amount
     * of data available is greater than the size of the buffer supplied, the
     * call supplies as much data as fits in the buffer provided and returns
     * with the error code ENOMEM. If the old value is not desired, oldp and
     * oldlenp should be set to NULL.
     *
     * The size of the available data can be determined by calling sysctl() with
     * the NULL argument for oldp. The size of the available data will be
     * returned in the location pointed to by oldlenp. For some operations, the
     * amount of space may change often. For these operations, the system
     * attempts to round up so that the returned size is large enough for a call
     * to return the data shortly thereafter.
     *
     * To set a new value, newp is set to point to a buffer of length newlen
     * from which the requested value is to be taken. If a new value is not to
     * be set, newp should be set to NULL and newlen set to 0.
     *
     * @param name
     *            MIB array of integers
     * @param namelen
     *            length of the MIB array
     * @param oldp
     *            Information retrieved
     * @param oldlenp
     *            Size of information retrieved
     * @param newp
     *            Information to be written
     * @param newlen
     *            Size of information to be written
     * @return 0 on success; sets errno on failure
     */
    int sysctl(int[] name, int namelen, Pointer oldp, IntByReference oldlenp,
               Pointer newp, int newlen);

    /**
     * The sysctlbyname() function accepts an ASCII representation of the name
     * and internally looks up the integer name vector. Apart from that, it
     * behaves the same as the standard sysctl() function.
     *
     * @param name
     *            ASCII representation of the MIB name
     * @param oldp
     *            Information retrieved
     * @param oldlenp
     *            Size of information retrieved
     * @param newp
     *            Information to be written
     * @param newlen
     *            Size of information to be written
     * @return 0 on success; sets errno on failure
     */
    int sysctlbyname(String name, Pointer oldp, IntByReference oldlenp,
                     Pointer newp, int newlen);

    /**
     * The sysctlnametomib() function accepts an ASCII representation of the
     * name, looks up the integer name vector, and returns the numeric
     * representation in the mib array pointed to by mibp. The number of
     * elements in the mib array is given by the location specified by sizep
     * before the call, and that location gives the number of entries copied
     * after a successful call. The resulting mib and size may be used in
     * subsequent sysctl() calls to get the data associated with the requested
     * ASCII name. This interface is intended for use by applications that want
     * to repeatedly request the same variable (the sysctl() function runs in
     * about a third the time as the same request made via the sysctlbyname()
     * function).
     *
     * The number of elements in the mib array can be determined by calling
     * sysctlnametomib() with the NULL argument for mibp.
     *
     * The sysctlnametomib() function is also useful for fetching mib prefixes.
     * If size on input is greater than the number of elements written, the
     * array still contains the additional elements which may be written
     * programmatically.
     *
     * @param name
     *            ASCII representation of the name
     * @param mibp
     *            Integer array containing the corresponding name vector.
     * @param size
     *            On input, number of elements in the returned array; on output,
     *            the number of entries copied.
     * @return 0 on success; sets errno on failure
     */
    int sysctlnametomib(String name, Pointer mibp, IntByReference size);

    /**
     * The host_processor_info function returns information about processors.
     *
     * @param machPort
     *            The control port for the host for which information is to be
     *            obtained.
     * @param flavor
     *            The type of information requested.
     * @param procCount
     *            Pointer to the number of processors
     * @param procInfo
     *            Pointer to the structure corresponding to the requested flavor
     * @param procInfoCount
     *            Pointer to number of elements in the returned structure
     * @return 0 on success; sets errno on failure
     */
    int host_processor_info(int machPort, int flavor, IntByReference procCount,
        PointerByReference procInfo, IntByReference procInfoCount);
}
