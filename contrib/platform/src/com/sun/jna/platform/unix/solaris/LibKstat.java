/* Copyright (c) 2017 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.unix.solaris;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;

/**
 * Kstat library. The kstat facility is a general-purpose mechanism for
 * providing kernel statistics to users.
 *
 * @author widdis[at]gmail[dot]com
 */
public interface LibKstat extends Library {

    LibKstat INSTANCE = Native.loadLibrary("kstat", LibKstat.class);

    /*
     * Kstat Data Types
     */
    // The "raw" kstat type is just treated as an array of bytes. This is
    // generally used to export well-known structures, like sysinfo.
    byte KSTAT_TYPE_RAW = 0;

    byte KSTAT_TYPE_NAMED = 1; // name/value pairs

    byte KSTAT_TYPE_INTR = 2; // interrupt statistics

    byte KSTAT_TYPE_IO = 3; // I/O statistics

    byte KSTAT_TYPE_TIMER = 4; // event timers

    /*
     * KstatNamed Data Types (for selecting from Union)
     */
    byte KSTAT_DATA_CHAR = 0;

    byte KSTAT_DATA_INT32 = 1;

    byte KSTAT_DATA_UINT32 = 2;

    byte KSTAT_DATA_INT64 = 3;

    byte KSTAT_DATA_UINT64 = 4;

    byte KSTAT_DATA_STRING = 9;

    /*
     * KstatIntr Interrupts
     */
    int KSTAT_INTR_HARD = 0;

    int KSTAT_INTR_SOFT = 1;

    int KSTAT_INTR_WATCHDOG = 2;

    int KSTAT_INTR_SPURIOUS = 3;

    int KSTAT_INTR_MULTSVC = 4;

    int KSTAT_NUM_INTRS = 5;

    int KSTAT_STRLEN = 31; // 30 chars + NULL; must be 16 * n - 1

    int EAGAIN = 11; // Temporarily busy

    /**
     * The kernel maintains a linked list of statistics structures, or kstats.
     * Each kstat has a common header section and a type-specific data section.
     * The header section is defined by the kstat_t structure
     */
    class Kstat extends Structure {

        // Fields relevant to both kernel and user
        public long ks_crtime; // creation time

        public Pointer ks_next; // kstat chain linkage

        public int ks_kid; // unique kstat ID

        public byte[] ks_module = new byte[KSTAT_STRLEN]; // module name

        public byte ks_resv; // reserved

        public int ks_instance; // module's instance

        public byte[] ks_name = new byte[KSTAT_STRLEN]; // kstat name

        public byte ks_type; // kstat data type

        public byte[] ks_class = new byte[KSTAT_STRLEN]; // kstat class

        public byte ks_flags; // kstat flags

        public Pointer ks_data; // kstat type-specific data

        public int ks_ndata; // # of data records

        public long ks_data_size; // size of kstat data section

        public long ks_snaptime; // time of last data snapshot

        // Fields relevant to kernel only
        public int ks_update; // dynamic update function

        public Pointer ks_private; // provider-private data

        public int ks_snapshot; // snapshot function

        public Pointer ks_lock; // protects this kstat's data

        public Kstat next() {
            if (ks_next == null) {
                return null;
            }
            Kstat n = new Kstat();
            n.useMemory(ks_next);
            n.read();
            return n;
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "ks_crtime", "ks_next", "ks_kid", "ks_module", "ks_resv", "ks_instance",
                    "ks_name", "ks_type", "ks_class", "ks_flags", "ks_data", "ks_ndata", "ks_data_size", "ks_snaptime",
                    "ks_update", "ks_private", "ks_snapshot", "ks_lock" });
        }
    }

    /**
     * A list of arbitrary name=value statistics.
     */
    class KstatNamed extends Structure {

        public byte[] name = new byte[KSTAT_STRLEN]; // name of counter

        public byte data_type; // data type

        public UNION value; // value of counter

        public static class UNION extends Union {

            public byte[] charc = new byte[16]; // enough for 128-bit ints

            public int i32;

            public int ui32;

            public long i64;

            public long ui64;

            public STR str;

            public static class STR extends Structure {

                public Pointer addr;

                public int len; // length of string

                @Override
                protected List<String> getFieldOrder() {
                    return Arrays.asList(new String[] { "addr", "len" });
                }
            }
        }

        public KstatNamed() {
            super();
        }

        public KstatNamed(Pointer p) {
            super(p);
            read();
        }

        @Override
        public void read() {
            super.read();
            switch (data_type) {
            case KSTAT_DATA_CHAR:
                value.setType(byte[].class);
                break;
            case KSTAT_DATA_STRING:
                value.setType(UNION.STR.class);
                break;
            case KSTAT_DATA_INT32:
            case KSTAT_DATA_UINT32:
                value.setType(int.class);
                break;
            case KSTAT_DATA_INT64:
            case KSTAT_DATA_UINT64:
                value.setType(long.class);
                break;
            default:
                break;
            }
            value.read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "name", "data_type", "value" });
        }
    }

    /**
     * Interrupt statistics. An interrupt is a hard interrupt (sourced from the
     * hardware device itself), a soft interrupt (induced by the system via the
     * use of some system interrupt source), a watchdog interrupt (induced by a
     * periodic timer call), spurious (an interrupt entry point was entered but
     * there was no interrupt to service), or multiple service (an interrupt was
     * detected and serviced just prior to returning from any of the other
     * types).
     */
    class KstatIntr extends Structure {

        public int[] intrs = new int[KSTAT_NUM_INTRS]; // interrupt counters

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "intrs" });
        }
    }

    /**
     * Event timer statistics. These provide basic counting and timing
     * information for any type of event.
     */
    class KstatTimer extends Structure {

        public byte[] name = new byte[KSTAT_STRLEN]; // event name

        public byte resv; // reserved

        public long num_events; // number of events

        public long elapsed_time; // cumulative elapsed time

        public long min_time; // shortest event duration

        public long max_time; // longest event duration

        public long start_time; // previous event start time

        public long stop_time; // previous event stop time

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "name", "resv", "num_events", "elapsed_time", "min_time", "max_time",
                    "start_time", "stop_time" });
        }
    }

    /**
     * IO Statistics.
     */
    class KstatIO extends Structure {

        // Basic counters.
        public long nread; // number of bytes read

        public long nwritten; // number of bytes written

        public int reads; // number of read operations

        public int writes; // number of write operations
        /*-
        * Accumulated time and queue length statistics.
        *
        * Time statistics are kept as a running sum of "active" time.
        * Queue length statistics are kept as a running sum of the
        * product of queue length and elapsed time at that length --
        * that is, a Riemann sum for queue length integrated against time.
        *       ^
        *       |           _________
        *       8           | i4    |
        *       |           |   |
        *   Queue   6           |   |
        *   Length  |   _________   |   |
        *       4   | i2    |_______|   |
        *       |   |   i3      |
        *       2_______|           |
        *       |    i1             |
        *       |_______________________________|
        *       Time--  t1  t2  t3  t4
        *
        * At each change of state (entry or exit from the queue),
        * we add the elapsed time (since the previous state change)
        * to the active time if the queue length was non-zero during
        * that interval; and we add the product of the elapsed time
        * times the queue length to the running length*time sum.
        *
        * This method is generalizable to measuring residency
        * in any defined system: instead of queue lengths, think
        * of "outstanding RPC calls to server X".
        *
        * A large number of I/O subsystems have at least two basic
        * "lists" of transactions they manage: one for transactions
        * that have been accepted for processing but for which processing
        * has yet to begin, and one for transactions which are actively
        * being processed (but not done). For this reason, two cumulative
        * time statistics are defined here: pre-service (wait) time,
        * and service (run) time.
        *
        * The units of cumulative busy time are accumulated nanoseconds.
        * The units of cumulative length*time products are elapsed time
        * times queue length.
        */

        public long wtime; // cumulative wait (pre-service) time

        public long wlentime; // cumulative wait length*time product

        public long wlastupdate; // last time wait queue changed

        public long rtime; // cumulative run (service) time

        public long rlentime; // cumulative run length*time product

        public long rlastupdate; // last time run queue changed

        public int wcnt; // count of elements in wait state

        public int rcnt; // count of elements in run state

        public KstatIO() {
            super();
        }

        public KstatIO(Pointer p) {
            super(p);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "nread", "nwritten", "reads", "writes", "wtime", "wlentime",
                    "wlastupdate", "rtime", "rlentime", "rlastupdate", "wcnt", "rcnt" });
        }
    }

    class KstatCtl extends Structure {

        public int kc_chain_id; // current kstat chain ID

        public Kstat kc_chain; // pointer to kstat chain

        public int kc_kd; // /dev/kstat descriptor - not public interface

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "kc_chain_id", "kc_chain", "kc_kd" });
        }
    }

    /**
     * The kstat_open() function initializes a kstat control structure, which
     * provides access to the kernel statistics library.
     *
     * @return A pointer to this structure, which must be supplied as the kc
     *         argument in subsequent libkstat function calls.
     */
    KstatCtl kstat_open();

    /**
     * The kstat_close() function frees all resources that were associated with
     * kc.
     *
     * @param kc
     *            a kstat control structure
     * @return 0 on success and -1 on failure.
     */
    int kstat_close(KstatCtl kc);

    /**
     * The kstat_chain_update() function brings the user's kstat header chain in
     * sync with that of the kernel. The kstat chain is a linked list of kstat
     * headers (kstat_t's) pointed to by kc.kc_chain, which is initialized by
     * kstat_open(3KSTAT). This chain constitutes a list of all kstats currently
     * in the system. During normal operation, the kernel creates new kstats and
     * delete old ones as various device instances are added and removed,
     * thereby causing the user's copy of the kstat chain to become out of date.
     * The kstat_chain_update() function detects this condition by comparing the
     * kernel's current kstat chain ID(KCID), which is incremented every time
     * the kstat chain changes, to the user's KCID, kc.kc_chain_id. If the KCIDs
     * match, kstat_chain_update() does nothing. Otherwise, it deletes any
     * invalid kstat headers from the user's kstat chain, adds any new ones, and
     * sets kc.kc_chain_id to the new KCID. All other kstat headers in the
     * user's kstat chain are unmodified.
     *
     * @param kc
     *            a kstat control structure
     * @return the new KCID if the kstat chain has changed, 0 if it hasn't, or
     *         -1 on failure.
     */
    int kstat_chain_update(KstatCtl kc);

    /**
     * kstat_read() gets data from the kernel for the kstat pointed to by ksp.
     * ksp.ks_data is automatically allocated (or reallocated) to be large
     * enough to hold all of the data. ksp.ks_ndata is set to the number of data
     * fields, ksp.ks_data_size is set to the total size of the data, and
     * ksp.ks_snaptime is set to the high-resolution time at which the data
     * snapshot was taken.
     *
     * @param kc
     *            The kstat control structure
     * @param ksp
     *            The kstat from which to retrieve data
     * @param p
     *            If buf is non-NULL , the data is copied from ksp.ks_data into
     *            buf.
     * @return On success, return the current kstat chain ID (KCID). On failure,
     *         return -1.
     */
    int kstat_read(KstatCtl kc, Kstat ksp, Pointer p);

    /**
     * kstat_write() writes data from buf, or from ksp.ks_data if buf is NULL,
     * to the corresponding kstat in the kernel. Only the superuser can use
     * kstat_write() .
     *
     * @param kc
     *            The kstat control structure
     * @param ksp
     *            The kstat on which to set data
     * @param buf
     *            If buf is non-NULL, the data is copied from buf into
     *            ksp.ks_data.
     * @return On success, return the current kstat chain ID (KCID). On failure,
     *         return -1.
     */
    int kstat_write(KstatCtl kc, Kstat ksp, Pointer buf);

    /**
     * The kstat_lookup() function traverses the kstat chain, kc.kc_chain,
     * searching for a kstat with the same ks_module, ks_instance, and ks_name
     * fields; this triplet uniquely identifies a kstat. If ks_module is NULL,
     * ks_instance is -1, or ks_name is NULL, then those fields will be ignored
     * in the search. For example, kstat_lookup(kc, NULL, -1, "foo") will simply
     * find the first kstat with name "foo".
     *
     * @param kc
     *            The kstat control structure
     * @param ks_module
     *            The kstat module to search
     * @param ks_instance
     *            The kstat instance number
     * @param ks_name
     *            The kstat name to search
     * @return a pointer to the requested kstat if it is found, or NULL if it is
     *         not.
     */
    Kstat kstat_lookup(KstatCtl kc, String ks_module, int ks_instance, String ks_name);

    /**
     * The kstat_data_lookup() function searches the kstat's data section for
     * the record with the specified name . This operation is valid only for
     * kstat types which have named data records. Currently, only the
     * KSTAT_TYPE_NAMED and KSTAT_TYPE_TIMER kstats have named data records.
     *
     * @param ksp
     *            The kstat to search
     * @param name
     *            The key for the name-value pair, or name of the timer as
     *            applicable
     * @return a pointer to the requested data record if it is found. If the
     *         requested record is not found, or if the kstat type is invalid,
     *         returns NULL.
     */
    Pointer kstat_data_lookup(Kstat ksp, String name);
}
