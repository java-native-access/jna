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
package com.sun.jna.platform.unix.aix;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;

/**
 * The perfstat API uses the perfstat kernel extension to extract various AIX®
 * performance metrics.
 *
 * System component information is also retrieved from the Object Data Manager
 * (ODM) and returned with the performance metrics.
 *
 * The perfstat API is thread–safe, and does not require root authority.
 */
public interface Perfstat extends Library {

    Perfstat INSTANCE = SharedObjectLoader.getPerfstatInstance();

    int IDENTIFIER_LENGTH = 64;

    @FieldOrder({ "name" })
    class perfstat_id_t extends Structure {
        public byte[] name = new byte[IDENTIFIER_LENGTH];
    }

    @FieldOrder({ "ncpus", "ncpus_cfg", "description", "processorHZ", "user", "sys", "idle", "wait", "pswitch",
            "syscall", "sysread", "syswrite", "sysfork", "sysexec", "readch", "writech", "devintrs", "softintrs",
            "lbolt", "loadavg", "runque", "swpque", "bread", "bwrite", "lread", "lwrite", "phread", "phwrite", "runocc",
            "swpocc", "iget", "namei", "dirblk", "msg", "sema", "rcvint", "xmtint", "mdmint", "tty_rawinch",
            "tty_caninch", "tty_rawoutch", "ksched", "koverf", "kexit", "rbread", "rcread", "rbwrt", "rcwrt", "traps",
            "ncpus_high", "puser", "psys", "pidle", "pwait", "decrintrs", "mpcrintrs", "mpcsintrs", "phantintrs",
            "idle_donated_purr", "idle_donated_spurr", "busy_donated_purr", "busy_donated_spurr", "idle_stolen_purr",
            "idle_stolen_spurr", "busy_stolen_purr", "busy_stolen_spurr", "iowait", "physio", "twait", "hpi", "hpit",
            "puser_spurr", "psys_spurr", "pidle_spurr", "pwait_spurr", "spurrflag", "version", "tb_last",
            "purr_coalescing", "spurr_coalescing" })
    class perfstat_cpu_total_t extends Structure {
        public int ncpus; // number of active logical processors
        public int ncpus_cfg; // number of configured processors
        public byte[] description = new byte[IDENTIFIER_LENGTH]; // processor description (type/official name)
        public long processorHZ; // processor speed in Hz
        public long user; // raw total number of clock ticks spent in user mode
        public long sys; // raw total number of clock ticks spent in system mode
        public long idle; // raw total number of clock ticks spent idle
        public long wait; // raw total number of clock ticks spent waiting for I/O
        public long pswitch; // number of process switches (change in currently running process)
        public long syscall; // number of system calls executed
        public long sysread; // number of read system calls executed
        public long syswrite; // number of write system calls executed
        public long sysfork; // number of forks system calls executed
        public long sysexec; // number of execs system calls executed
        public long readch; // number of characters tranferred with read system call
        public long writech; // number of characters tranferred with write system call
        public long devintrs; // number of device interrupts
        public long softintrs; // number of software interrupts
        public NativeLong lbolt; // number of ticks since last reboot
        public long[] loadavg = new long[3]; // (1<<SBITS) times the average number of runnables processes during the
                                             // last 1, 5 and 15 minutes. To calculate the load average, divide the
                                             // numbers by (1<<SBITS). SBITS is defined in <sys/proc.h>.
        public long runque; // length of the run queue (processes ready)
        public long swpque; // ength of the swap queue (processes waiting to be paged in)
        public long bread; // number of blocks read
        public long bwrite; // number of blocks written
        public long lread; // number of logical read requests
        public long lwrite; // number of logical write requests
        public long phread; // number of physical reads (reads on raw devices)
        public long phwrite; // number of physical writes (writes on raw devices)
        public long runocc; // updated whenever runque is updated, i.e. the runqueue is occupied. This can
                            // be used to compute the simple average of ready processes
        public long swpocc; // updated whenever swpque is updated. i.e. the swpqueue is occupied. This can
                            // be used to compute the simple average processes waiting to be paged in
        public long iget; // number of inode lookups
        public long namei; // number of vnode lookup from a path name
        public long dirblk; // number of 512-byte block reads by the directory search routine to locate an
                            // entry for a file
        public long msg; // number of IPC message operations
        public long sema; // number of IPC semaphore operations
        public long rcvint; // number of tty receive interrupts
        public long xmtint; // number of tyy transmit interrupts
        public long mdmint; // number of modem interrupts
        public long tty_rawinch; // number of raw input characters
        public long tty_caninch; // number of canonical input characters (always zero)
        public long tty_rawoutch; // number of raw output characters
        public long ksched; // number of kernel processes created
        public long koverf; // kernel process creation attempts where:
                            // -the user has forked to their maximum limit
                            // -the configuration limit of processes has been reached
        public long kexit; // number of kernel processes that became zombies
        public long rbread; // number of remote read requests
        public long rcread; // number of cached remote reads
        public long rbwrt; // number of remote writes
        public long rcwrt; // number of cached remote writes
        public long traps; // number of traps
        public int ncpus_high; // index of highest processor online
        public long puser; // raw number of physical processor tics in user mode
        public long psys; // raw number of physical processor tics in system mode
        public long pidle; // raw number of physical processor tics idle
        public long pwait; // raw number of physical processor tics waiting for I/O
        public long decrintrs; // number of decrementer tics interrupts
        public long mpcrintrs; // number of mpc's received interrupts
        public long mpcsintrs; // number of mpc's sent interrupts
        public long phantintrs; // number of phantom interrupts
        public long idle_donated_purr; // number of idle cycles donated by a dedicated partition enabled for donation
        public long idle_donated_spurr; // number of idle spurr cycles donated by a dedicated partition enabled for
                                        // donation
        public long busy_donated_purr; // number of busy cycles donated by a dedicated partition enabled for donation
        public long busy_donated_spurr; // number of busy spurr cycles donated by a dedicated partition enabled for
                                        // donation
        public long idle_stolen_purr; // number of idle cycles stolen by the hypervisor from a dedicated partition
        public long idle_stolen_spurr; // number of idle spurr cycles stolen by the hypervisor from a dedicated
                                       // partition
        public long busy_stolen_purr; // number of busy cycles stolen by the hypervisor from a dedicated partition
        public long busy_stolen_spurr; // number of busy spurr cycles stolen by the hypervisor from a dedicated
                                       // partition
        public short iowait; // number of processes that are asleep waiting for buffered I/O
        public short physio; // number of processes waiting for raw I/O
        public long twait; // number of threads that are waiting for filesystem direct(cio)
        public long hpi; // number of hypervisor page-ins
        public long hpit; // Time spent in hypervisor page-ins (in nanoseconds)
        public long puser_spurr; // number of spurr cycles spent in user mode
        public long psys_spurr; // number of spurr cycles spent in kernel mode
        public long pidle_spurr; // number of spurr cycles spent in idle mode
        public long pwait_spurr; // number of spurr cycles spent in wait mode
        public int spurrflag; // set if running in spurr mode
        public long version; // version number (1, 2, etc.,)
        public long tb_last; // time base counter
        public long purr_coalescing; // If the calling partition is authorized to see pool wide statistics then PURR
                                     // cycles consumed to coalesce dataelse set to zero.
        public long spurr_coalescing; // If the calling partition isauthorized to see pool wide statistics then SPURR
                                      // cycles consumed to coalesce data else set to zero.
    }

    @FieldOrder({ "name", "user", "sys", "idle", "wait", "pswitch", "syscall", "sysread", "syswrite", "sysfork",
            "sysexec", "readch", "writech", "bread", "bwrite", "lread", "lwrite", "phread", "phwrite", "iget", "namei",
            "dirblk", "msg", "sema", "minfaults", "majfaults", "puser", "psys", "pidle", "pwait", "redisp_sd0",
            "redisp_sd1", "redisp_sd2", "redisp_sd3", "redisp_sd4", "redisp_sd5", "migration_push", "migration_S3grq",
            "migration_S3pul", "invol_cswitch", "vol_cswitch", "runque", "bound", "decrintrs", "mpcrintrs", "mpcsintrs",
            "devintrs", "softintrs", "phantintrs", "idle_donated_purr", "idle_donated_spurr", "busy_donated_purr",
            "busy_donated_spurr", "idle_stolen_purr", "idle_stolen_spurr", "busy_stolen_purr", "busy_stolen_spurr",
            "hpi", "hpit", "puser_spurr", "psys_spurr", "pidle_spurr", "pwait_spurr", "spurrflag", "localdispatch",
            "neardispatch", "fardispatch", "cswitches", "version", "tb_last" })
    class perfstat_cpu_t extends Structure {
        public byte[] name = new byte[IDENTIFIER_LENGTH]; // logical processor name (cpu0, cpu1, ..)
        public long user; // raw number of clock ticks spent in user mode
        public long sys; // raw number of clock ticks spent in system mode
        public long idle; // raw number of clock ticks spent idle
        public long wait; // raw number of clock ticks spent waiting for I/O
        public long pswitch; // number of context switches (changes of currently running process)
        public long syscall; // number of system calls executed
        public long sysread; // number of read system calls executed
        public long syswrite; // number of write system calls executed
        public long sysfork; // number of fork system call executed
        public long sysexec; // number of exec system call executed
        public long readch; // number of characters tranferred with read system call
        public long writech; // number of characters tranferred with write system call
        public long bread; // number of block reads
        public long bwrite; // number of block writes
        public long lread; // number of logical read requests
        public long lwrite; // number of logical write requests
        public long phread; // number of physical reads (reads on raw device)
        public long phwrite; // number of physical writes (writes on raw device)
        public long iget; // number of inode lookups
        public long namei; // number of vnode lookup from a path name
        public long dirblk; // number of 512-byte block reads by the directory search routine to locate an
                            // entry for a file
        public long msg; // number of IPC message operations
        public long sema; // number of IPC semaphore operations
        public long minfaults; // number of page faults with no I/O
        public long majfaults; // number of page faults with disk I/O
        public long puser; // raw number of physical processor tics in user mode
        public long psys; // raw number of physical processor tics in system mode
        public long pidle; // raw number of physical processor tics idle
        public long pwait; // raw number of physical processor tics waiting for I/O
        public long redisp_sd0; // number of thread redispatches within the scheduler affinity domain 0
        public long redisp_sd1; // number of thread redispatches within the scheduler affinity domain 1
        public long redisp_sd2; // number of thread redispatches within the scheduler affinity domain 2
        public long redisp_sd3; // number of thread redispatches within the scheduler affinity domain 3
        public long redisp_sd4; // number of thread redispatches within the scheduler affinity domain 4
        public long redisp_sd5; // number of thread redispatches within the scheduler affinity domain 5
        public long migration_push; // number of thread migrations from the local runque to another queue due to
                                    // starvation load balancing
        public long migration_S3grq; // number of thread migrations from the global runque to the local runque
                                     // resulting in a move accross scheduling domain 3
        public long migration_S3pul; // number of thread migrations from another processor's runque resulting in a
                                     // move accross scheduling domain 3
        public long invol_cswitch; // number of involuntary thread context switches
        public long vol_cswitch; // number of voluntary thread context switches
        public long runque; // number of threads on the runque
        public long bound; // number of bound threads
        public long decrintrs; // number of decrementer tics interrupts
        public long mpcrintrs; // number of mpc's received interrupts
        public long mpcsintrs; // number of mpc's sent interrupts
        public long devintrs; // number of device interrupts
        public long softintrs; // number of offlevel handlers called
        public long phantintrs; // number of phantom interrupts
        public long idle_donated_purr; // number of idle cycles donated by a dedicated partition enabled for donation
        public long idle_donated_spurr; // number of idle spurr cycles donated by a dedicated partition enabled for
                                        // donation
        public long busy_donated_purr; // number of busy cycles donated by a dedicated partition enabled for donation
        public long busy_donated_spurr; // number of busy spurr cycles donated by a dedicated partition enabled for
                                        // donation
        public long idle_stolen_purr; // number of idle cycles stolen by the hypervisor from a dedicated partition
        public long idle_stolen_spurr; // number of idle spurr cycles stolen by the hypervisor from a dedicated
                                       // partition
        public long busy_stolen_purr; // number of busy cycles stolen by the hypervisor from a dedicated partition
        public long busy_stolen_spurr; // number of busy spurr cycles stolen by the hypervisor from a dedicated
                                       // partition
        public long hpi; // number of hypervisor page-ins
        public long hpit; // Time spent in hypervisor page-ins (in nanoseconds)
        public long puser_spurr; // number of spurr cycles spent in user mode
        public long psys_spurr; // number of spurr cycles spent in kernel mode
        public long pidle_spurr; // number of spurr cycles spent in idle mode
        public long pwait_spurr; // number of spurr cycles spent in wait mode
        public int spurrflag; // set if running in spurr mode

        public long localdispatch; // number of local thread dispatches on this logical CPU
        public long neardispatch; // number of near thread dispatches on this logical CPU
        public long fardispatch; // number of far thread dispatches on this logical CPU
        public long cswitches; // Context switches
        public long version; // version number (1, 2, etc.,)
        public long tb_last; // timebase counter
    }

    @FieldOrder({ "virt_total", "real_total", "real_free", "real_pinned", "real_inuse", "pgbad", "pgexct", "pgins",
            "pgouts", "pgspins", "pgspouts", "scans", "cycles", "pgsteals", "numperm", "pgsp_total", "pgsp_free",
            "pgsp_rsvd", "real_system", "real_user", "real_process", "virt_active", "iome", "iomu", "iohwm", "pmem",
            "comprsd_total", "comprsd_wseg_pgs", "cpgins", "cpgouts", "true_size", "expanded_memory",
            "comprsd_wseg_size", "target_cpool_size", "max_cpool_size", "min_ucpool_size", "cpool_size", "ucpool_size",
            "cpool_inuse", "ucpool_inuse", "version", "real_avail", "bytes_coalesced", "bytes_coalesced_mempool" })
    class perfstat_memory_total_t extends Structure {
        public long virt_total; // total virtual memory (in 4KB pages)
        public long real_total; // total real memory (in 4KB pages)
        public long real_free; // free real memory (in 4KB pages)
        public long real_pinned; // real memory which is pinned (in 4KB pages)
        public long real_inuse; // real memory which is in use (in 4KB pages)
        public long pgbad; // number of bad pages
        public long pgexct; // number of page faults
        public long pgins; // number of pages paged in
        public long pgouts; // number of pages paged out
        public long pgspins; // number of page ins from paging space
        public long pgspouts; // number of page outs from paging space
        public long scans; // number of page scans by clock
        public long cycles; // number of page replacement cycles
        public long pgsteals; // number of page steals
        public long numperm; // number of frames used for files (in 4KB pages)
        public long pgsp_total; // total paging space (in 4KB pages)
        public long pgsp_free; // free paging space (in 4KB pages)
        public long pgsp_rsvd; // reserved paging space (in 4KB pages)
        public long real_system; // real memory used by system segments (in 4KB pages). This is the sum of all
                                 // the used pages in segment marked for system usage. Since segment
                                 // classifications are not always guaranteed to be accurate, this number is only
                                 // an approximation.
        public long real_user; // real memory used by non-system segments (in 4KB pages). This is the sum of
                               // all pages used in segments not marked for system usage. Since segment
                               // classifications are not always guaranteed to be accurate, this number is only
                               // an approximation.
        public long real_process; // real memory used by process segments (in 4KB pages). This is
                                  // real_total-real_free-numperm-real_system. Since real_system is an
                                  // approximation, this number is too.
        public long virt_active; // Active virtual pages. Virtual pages are considered active if they have been
                                 // accessed
        public long iome; // I/O memory entitlement of the partition in bytes
        public long iomu; // I/O memory entitlement of the partition in use in bytes
        public long iohwm; // High water mark of I/O memory entitlement used in bytes
        public long pmem; // Amount of physical mmeory currently backing partition's logical memory in
                          // bytes

        public long comprsd_total; // Total numbers of pages in compressed pool (in 4KB pages)
        public long comprsd_wseg_pgs; // Number of compressed working storage pages
        public long cpgins; // number of page ins to compressed pool
        public long cpgouts; // number of page outs from compressed pool

        public long true_size; // True Memory Size in 4KB pages
        public long expanded_memory; // Expanded Memory Size in 4KB pages
        public long comprsd_wseg_size; // Total size of the compressed working storage pages in the pool
        public long target_cpool_size; // Target Compressed Pool Size in bytes
        public long max_cpool_size; // Max Size of Compressed Pool in bytes
        public long min_ucpool_size; // Min Size of Uncompressed Pool in bytes
        public long cpool_size; // Compressed Pool size in bytes
        public long ucpool_size; // Uncompressed Pool size in bytes
        public long cpool_inuse; // Compressed Pool Used in bytes
        public long ucpool_inuse; // Uncompressed Pool Used in bytes
        public long version; // version number (1, 2, etc.,)
        public long real_avail; // number of pages (in 4KB pages) of memory available without paging out working
                                // segments
        public long bytes_coalesced; // The number of bytes of the calling partition's logical real memory coalesced
                                     // because they contained duplicated data
        public long bytes_coalesced_mempool; // If the calling partition is authorized to see pool wide statistics then
                                             // the number of bytes of logical real memory coalesced because they
                                             // contained duplicated data in the calling partition's memory pool else
                                             // set to zero.
    }

    @FieldOrder({ "version", "pid", "proc_name", "proc_priority", "num_threads", "proc_uid", "proc_classid",
            "proc_size", "proc_real_mem_data", "proc_real_mem_text", "proc_virt_mem_data", "proc_virt_mem_text",
            "shared_lib_data_size", "heap_size", "real_inuse", "virt_inuse", "pinned", "pgsp_inuse", "filepages",
            "real_inuse_map", "virt_inuse_map", "pinned_inuse_map", "ucpu_time", "scpu_time", "last_timebase",
            "inBytes", "outBytes", "inOps", "outOps" })
    class perfstat_process_t extends Structure {
        public long version; // version number (1, 2, etc.,)
        public long pid; // Process ID
        public byte[] proc_name = new byte[64]; // Name of The Process
        public int proc_priority; // Process Priority
        public long num_threads; // Thread Count
        public long proc_uid; // Owner Info
        public long proc_classid; // WLM Class Name
        public long proc_size; // Virtual Size of the Process in KB(Exclusive Usage, Leaving all Shared Library
                               // Text & Shared File Pages, Shared Memory, Memory Mapped)
        public long proc_real_mem_data; // Real Memory used for Data in KB
        public long proc_real_mem_text; // Real Memory used for Text in KB
        public long proc_virt_mem_data; // Virtual Memory used to Data in KB
        public long proc_virt_mem_text; // Virtual Memory used for Text in KB
        public long shared_lib_data_size; // Data Size from Shared Library in KB
        public long heap_size; // Heap Size in KB
        public long real_inuse; // The Real memory in use(in KB) by the process including all kind of segments
                                // (excluding system segments). This includes Text, Data, Shared Library Text,
                                // Shared Library Data, File Pages, Shared Memory & Memory Mapped
        public long virt_inuse; // The Virtual memory in use(in KB) by the process including all kind of
                                // segments (excluding system segments). This includes Text, Data, Shared
                                // Library Text, Shared Library Data, File Pages, Shared Memory & Memory Mapped
        public long pinned; // Pinned Memory(in KB) for this process inclusive of all segments
        public long pgsp_inuse; // Paging Space used(in KB) inclusive of all segments
        public long filepages; // File Pages used(in KB) including shared pages
        public long real_inuse_map; // Real memory used(in KB) for Shared Memory and Memory Mapped regions
        public long virt_inuse_map; // Virtual Memory used(in KB) for Shared Memory and Memory Mapped regions
        public long pinned_inuse_map; // Pinned memory(in KB) for Shared Memory and Memory Mapped regions
        public double ucpu_time; // User Mode CPU time will be in percentage or milliseconds based on, whether it
                                 // is filled by perfstat_process_util or perfstat_process respectively.
        public double scpu_time; // System Mode CPU time will be in percentage or milliseconds based on, whether
                                 // it is filled by perfstat_process_util or perfstat_process respectively.
        public long last_timebase; // Timebase Counter
        public long inBytes; // Bytes Read from Disk
        public long outBytes; // Bytes Written to Disk
        public long inOps; // In Operations from Disk
        public long outOps; // Out Operations from Disk
    }

    @FieldOrder({ "name", "description", "vgname", "size", "free", "bsize", "xrate", "xfers", "wblks", "rblks",
            "qdepth", "time", "adapter", "paths_count", "q_full", "rserv", "rtimeout", "rfailed", "min_rserv",
            "max_rserv", "wserv", "wtimeout", "wfailed", "min_wserv", "max_wserv", "wq_depth", "wq_sampled", "wq_time",
            "wq_min_time", "wq_max_time", "q_sampled", "wpar_id", "version", "dk_type" })
    class perfstat_disk_t extends Structure {
        public byte[] name = new byte[IDENTIFIER_LENGTH]; // name of the disk
        public byte[] description = new byte[IDENTIFIER_LENGTH]; // disk description (from ODM)
        public byte[] vgname = new byte[IDENTIFIER_LENGTH]; // volume group name (from ODM)
        public long size; // size of the disk (in MB)
        public long free; // free portion of the disk (in MB)
        public long bsize; // disk block size (in bytes)
        public long xrate; // OBSOLETE: xrate capability
        public long xfers; // number of transfers to/from disk
        public long wblks; // number of blocks written to disk
        public long rblks; // number of blocks read from disk
        public long qdepth; // instantaneous "service" queue depth (number of requests sent to disk and not
                            // completed yet)
        public long time; // amount of time disk is active
        public byte[] adapter = new byte[IDENTIFIER_LENGTH]; // disk adapter name
        public int paths_count; // number of paths to this disk
        public long q_full; // "service" queue full occurrence count (number of times the disk is not
                            // accepting any more request)
        public long rserv; // read or receive service time
        public long rtimeout; // number of read request timeouts
        public long rfailed; // number of failed read requests
        public long min_rserv; // min read or receive service time
        public long max_rserv; // max read or receive service time
        public long wserv; // write or send service time
        public long wtimeout; // number of write request timeouts
        public long wfailed; // number of failed write requests
        public long min_wserv; // min write or send service time
        public long max_wserv; // max write or send service time
        public long wq_depth; // instantaneous wait queue depth (number of requests waiting to be sent to
                              // disk)
        public long wq_sampled; // accumulated sampled dk_wq_depth
        public long wq_time; // accumulated wait queueing time
        public long wq_min_time; // min wait queueing time
        public long wq_max_time; // max wait queueing time
        public long q_sampled; // accumulated sampled dk_q_depth
        public short wpar_id; // WPAR identifier. cid_t is unsigned short
        // Pad of 3 short is available here
        public long version; // version number (1, 2, etc.,)
        public int dk_type; // Holds more information about the disk. 32-bit union perfstat_dktype_t
    }

    @FieldOrder({ "online", "max", "min", "desired" })
    class perfstat_value_t extends Structure {
        public long online;
        public long max;
        public long min;
        public long desired;
    }

    @FieldOrder({ "version", "partitionname", "nodename", "conf", "partitionnum", "groupid", "processorFamily",
            "processorModel", "machineID", "processorMHz", "numProcessors", "OSName", "OSVersion", "OSBuild", "lcpus",
            "smtthreads", "drives", "nw_adapters", "cpucap", "cpucap_weightage", "entitled_proc_capacity", "vcpus",
            "processor_poolid", "activecpusinpool", "cpupool_weightage", "sharedpcpu", "maxpoolcap", "entpoolcap",
            "mem", "mem_weightage", "totiomement", "mempoolid", "hyperpgsize", "exp_mem", "targetmemexpfactor",
            "targetmemexpsize" })
    class perfstat_partition_config_t extends Structure {
        public long version; // Version number
        public byte[] partitionname = new byte[64]; // Partition Name
        public byte[] nodename = new byte[64]; // Node Name
        public int conf; // Partition Properties (perfstat_partition_type_t 32-bit union)
        public int partitionnum; // Partition Number
        public int groupid; // Group ID

        /* Hardware Configuration */
        public byte[] processorFamily = new byte[64]; // Processor Type
        public byte[] processorModel = new byte[64]; // Processor Model
        public byte[] machineID = new byte[64]; // Machine ID
        public double processorMHz; // Processor Clock Speed in MHz
        public perfstat_value_t numProcessors; // Number of Configured Physical Processors in frame

        /* Software Configuration */
        public byte[] OSName = new byte[64]; // Name of Operating System
        public byte[] OSVersion = new byte[64]; // Version of operating System
        public byte[] OSBuild = new byte[64]; // Build of Operating System

        /* Lpar Configuration */
        public int lcpus; // Number of Logical CPUs
        public int smtthreads; // Number of SMT Threads
        public int drives; // Total Number of Drives
        public int nw_adapters; // Total Number of Network Adapters

        /* Physical CPU related Configuration */
        public perfstat_value_t cpucap; // Min, Max and Online CPU Capacity
        public int cpucap_weightage; // Variable Processor Capacity Weightage
        public int entitled_proc_capacity; // number of processor units this partition is entitled to receive
        /* Virtual CPU related Configuration */
        public perfstat_value_t vcpus; // Min, Max and Online Virtual CPUs

        /* Processor Pool Related Configuration */
        public int processor_poolid; // Shared Pool ID of physical processors, to which this partition belongs
        public int activecpusinpool; // Count of physical CPUs in the shared processor pool, to which this partition
                                     // belongs
        public int cpupool_weightage; // Pool Weightage
        public int sharedpcpu; // Number of physical processors allocated for shared processor use
        public int maxpoolcap; // Maximum processor capacity of partition's pool
        public int entpoolcap; // Entitled processor capacity of partition's pool

        /* Memory Related Configuration */
        public perfstat_value_t mem; // Min, Max and Online Memory
        public int mem_weightage; // Variable Memory Capacity Weightage

        /* AMS Related Configuration */
        public long totiomement; // I/O Memory Entitlement of the partition in bytes
        public int mempoolid; // AMS pool id of the pool the LPAR belongs to
        public long hyperpgsize; // Hypervisor page size in KB

        /* AME Related Configuration */
        public perfstat_value_t exp_mem; // Min, Max and Online Expanded Memory
        public long targetmemexpfactor; // Target Memory Expansion Factor scaled by 100
        public long targetmemexpsize; // Expanded Memory Size in MB
    }

    @FieldOrder({ "name", "description", "type", "mtu", "ipackets", "ibytes", "ierrors", "opackets", "obytes",
            "oerrors", "collisions", "bitrate", "xmitdrops", "version", "if_iqdrops", "if_arpdrops" })
    class perfstat_netinterface_t extends Structure {
        public byte[] name = new byte[IDENTIFIER_LENGTH]; // name of the interface
        public byte[] description = new byte[IDENTIFIER_LENGTH]; // interface description
                                                                 // (from ODM, similar to lscfg output)
        public byte type; // ethernet, tokenring, etc. interpretation can be done using
                          // /usr/include/net/if_types.h
        public long mtu; // network frame size
        public long ipackets; // number of packets received on interface
        public long ibytes; // number of bytes received on interface
        public long ierrors; // number of input errors on interface
        public long opackets; // number of packets sent on interface
        public long obytes; // number of bytes sent on interface
        public long oerrors; // number of output errors on interface
        public long collisions; // number of collisions on csma interface
        public long bitrate; // adapter rating in bit per second
        public long xmitdrops; // number of packets not transmitted
        public long version; // version number (1, 2, etc.,)
        public long if_iqdrops; // Dropped on input, this interface
        public long if_arpdrops; // Dropped because no arp response
    }

    @FieldOrder({ "name", "u", "version" })
    class perfstat_protocol_t extends Structure {
        // One of: ip, ipv6, icmp, icmpv6, udp, tcp, rpc, nfs, nfsv2, nfsv3, nfsv4
        public byte[] name = new byte[IDENTIFIER_LENGTH];
        // Relevant union field based on name field
        public AnonymousUnionPayload u;
        public long version; // version number (1, 2, etc.,)

        @Override
        public void read() {
            super.read();
            String type = Native.toString(this.name);
            if (!type.isEmpty()) {
                u.setType(type);
            }
            u.read();
        }

        public static class AnonymousUnionPayload extends Union {
            public AnonymousStructIP ip;
            public AnonymousStructIPv6 ipv6;
            public AnonymousStructICMP icmp;
            public AnonymousStructICMPv6 icmpv6;
            public AnonymousStructUDP udp;
            public AnonymousStructTCP tcp;
            public AnonymousStructRPC rpc;
            public AnonymousStructNFS nfs;
            public AnonymousStructNFSv2 nfsv2;
            public AnonymousStructNFSv3 nfsv3;
            public AnonymousStructNFSv4 nfsv4;
        }

        @FieldOrder({ "ipackets", "ierrors", "iqueueoverflow", "opackets", "oerrors" })
        public static class AnonymousStructIP extends Structure {
            public long ipackets; // number of input packets
            public long ierrors; // number of input errors
            public long iqueueoverflow; // number of input queue overflows
            public long opackets; // number of output packets
            public long oerrors; // number of output errors
        }

        @FieldOrder({ "ipackets", "ierrors", "iqueueoverflow", "opackets", "oerrors" })
        public static class AnonymousStructIPv6 extends Structure {
            public long ipackets; // number of input packets
            public long ierrors; // number of input errors
            public long iqueueoverflow; // number of input queue overflows
            public long opackets; // number of output packets
            public long oerrors; // number of output errors
        }

        @FieldOrder({ "received", "sent", "errors" })
        public static class AnonymousStructICMP extends Structure {
            public long received; // number of packets received
            public long sent; // number of packets sent
            public long errors; // number of errors
        }

        @FieldOrder({ "received", "sent", "errors" })
        public static class AnonymousStructICMPv6 extends Structure {
            public long received; // number of packets received
            public long sent; // number of packets sent
            public long errors; // number of errors
        }

        @FieldOrder({ "ipackets", "ierrors", "opackets", "no_socket" })
        public static class AnonymousStructUDP extends Structure {
            public long ipackets; // number of input packets
            public long ierrors; // number of input errors
            public long opackets; // number of output packets
            public long no_socket; // number of packets dropped due to no socket
        }

        @FieldOrder({ "ipackets", "ierrors", "opackets", "initiated", "accepted", "established", "dropped" })
        public static class AnonymousStructTCP extends Structure {
            public long ipackets; // number of input packets
            public long ierrors; // number of input errors
            public long opackets; // number of output packets
            public long initiated; // number of connections initiated
            public long accepted; // number of connections accepted
            public long established; // number of connections established
            public long dropped; // number of connections dropped
        }

        @FieldOrder({ "client", "server" })
        public static class AnonymousStructRPC extends Structure {
            public AnonymousStructRPCclient client;
            public AnonymousStructRPCserver server;
        }

        @FieldOrder({ "stream", "dgram" })
        public static class AnonymousStructRPCclient extends Structure {
            public AnonymousStructRPCclientstream stream;
            public AnonymousStructRPCclientdgram dgram;
        }

        @FieldOrder({ "calls", "badcalls", "badxids", "timeouts", "newcreds", "badverfs", "timers", "nomem", "cantconn",
                "interrupts" })
        public static class AnonymousStructRPCclientstream extends Structure {
            public long calls; // total NFS client RPC connection-oriented calls
            public long badcalls; // rejected NFS client RPC calls
            public long badxids; // bad NFS client RPC call responses
            public long timeouts; // timed out NFS client RPC calls with no reply
            public long newcreds; // total NFS client RPC authentication refreshes
            public long badverfs; // total NFS client RPC bad verifier in response
            public long timers; // NFS client RPC timeout greater than timeout value
            public long nomem; // NFS client RPC calls memory allocation failure
            public long cantconn; // failed NFS client RPC calls
            public long interrupts; // NFS client RPC calls fail due to interrupt
        }

        @FieldOrder({ "calls", "badcalls", "retrans", "badxids", "timeouts", "newcreds", "badverfs", "timers", "nomem",
                "cantsend" })
        public static class AnonymousStructRPCclientdgram extends Structure {
            public long calls; // total NFS client RPC connectionless calls
            public long badcalls; // rejected NFS client RPC calls
            public long retrans; // retransmitted NFS client RPC calls
            public long badxids; // bad NFS client RPC call responses
            public long timeouts; // timed out NFS client RPC calls with no reply
            public long newcreds; // total NFS client RPC authentication refreshes
            public long badverfs; // total NFS client RPC bad verifier in response
            public long timers; // NFS client RPC timeout greater than timeout value
            public long nomem; // NFS client RPC calls memory allocation failure
            public long cantsend; // NFS client RPC calls not sent
        }

        @FieldOrder({ "stream", "dgram" })
        public static class AnonymousStructRPCserver extends Structure {
            public AnonymousStructRPCserverstream stream;
            public AnonymousStructRPCserverdgram dgram;
        }

        @FieldOrder({ "calls", "badcalls", "nullrecv", "badlen", "xdrcall", "dupchecks", "dupreqs" })
        public static class AnonymousStructRPCserverstream extends Structure {
            public long calls; // total NFS server RPC connection-oriented requests
            public long badcalls; // rejected NFS server RPC requests
            public long nullrecv; // NFS server RPC calls failed due to unavailable packet
            public long badlen; // NFS server RPC requests failed due to bad length
            public long xdrcall; // NFS server RPC requests failed due to bad header
            public long dupchecks; // NFS server RPC calls found in request cache
            public long dupreqs; // total NFS server RPC call duplicates
        }

        @FieldOrder({ "calls", "badcalls", "nullrecv", "badlen", "xdrcall", "dupchecks", "dupreqs" })
        public static class AnonymousStructRPCserverdgram extends Structure {
            public long calls; // total NFS server RPC connectionless requests
            public long badcalls; // rejected NFS server RPC requests
            public long nullrecv; // NFS server RPC calls failed due to unavailable packet
            public long badlen; // NFS server RPC requests failed due to bad length
            public long xdrcall; // NFS server RPC requests failed due to bad header
            public long dupchecks; // NFS server RPC calls found in request cache
            public long dupreqs; // total NFS server RPC call duplicates
        }

        @FieldOrder({ "client", "server" })
        public static class AnonymousStructNFS extends Structure {
            public AnonymousStructNFSclient client;
            public AnonymousStructNFSserver server;
        }

        @FieldOrder({ "calls", "badcalls", "clgets", "cltoomany" })
        public static class AnonymousStructNFSclient extends Structure {
            public long calls; // total NFS client requests
            public long badcalls; // total NFS client failed calls
            public long clgets; // total number of client nfs clgets
            public long cltoomany; // total number of client nfs cltoomany
        }

        @FieldOrder({ "calls", "badcalls", "public_v2", "public_v3" })
        public static class AnonymousStructNFSserver extends Structure {
            public long calls; // total NFS server requests
            public long badcalls; // total NFS server failed calls
            public long public_v2; // total number of nfs version 2 server calls
            public long public_v3; // total number of nfs version 3 server calls
        }

        @FieldOrder({ "client", "server" })
        public static class AnonymousStructNFSv2 extends Structure {
            public AnonymousStructNFSv2client client;
            public AnonymousStructNFSv2server server;
        }

        @FieldOrder({ "calls", "nullreq", "getattr", "setattr", "root", "lookup", "readlink", "read", "writecache",
                "write", "create", "remove", "rename", "link", "symlink", "mkdir", "rmdir", "readdir", "statfs" })
        public static class AnonymousStructNFSv2client extends Structure {
            public long calls; // NFS V2 client requests
            public long nullreq; // NFS V2 client null requests
            public long getattr; // NFS V2 client getattr requests
            public long setattr; // NFS V2 client setattr requests
            public long root; // NFS V2 client root requests
            public long lookup; // NFS V2 client file name lookup requests
            public long readlink; // NFS V2 client readlink requests
            public long read; // NFS V2 client read requests
            public long writecache; // NFS V2 client write cache requests
            public long write; // NFS V2 client write requests
            public long create; // NFS V2 client file creation requests
            public long remove; // NFS V2 client file removal requests
            public long rename; // NFS V2 client file rename requests
            public long link; // NFS V2 client link creation requests
            public long symlink; // NFS V2 client symbolic link creation requests
            public long mkdir; // NFS V2 client directory creation requests
            public long rmdir; // NFS V2 client directory removal requests
            public long readdir; // NFS V2 client read-directory requests
            public long statfs; // NFS V2 client file stat requests
        }

        @FieldOrder({ "calls", "nullreq", "getattr", "setattr", "root", "lookup", "readlink", "read", "writecache",
                "write", "create", "remove", "rename", "link", "symlink", "mkdir", "rmdir", "readdir", "statfs" })
        public static class AnonymousStructNFSv2server extends Structure {
            public long calls; // NFS V2 server requests
            public long nullreq; // NFS V2 server null requests
            public long getattr; // NFS V2 server getattr requests
            public long setattr; // NFS V2 server setattr requests
            public long root; // NFS V2 server root requests
            public long lookup; // NFS V2 server file name lookup requests
            public long readlink; // NFS V2 server readlink requests
            public long read; // NFS V2 server read requests
            public long writecache; // NFS V2 server cache requests
            public long write; // NFS V2 server write requests
            public long create; // NFS V2 server file creation requests
            public long remove; // NFS V2 server file removal requests
            public long rename; // NFS V2 server file rename requests
            public long link; // NFS V2 server link creation requests
            public long symlink; // NFS V2 server symbolic link creation requests
            public long mkdir; // NFS V2 server directory creation requests
            public long rmdir; // NFS V2 server directory removal requests
            public long readdir; // NFS V2 server read-directory requests
            public long statfs; // NFS V2 server file stat requests
        }

        @FieldOrder({ "client", "server" })
        public static class AnonymousStructNFSv3 extends Structure {
            public AnonymousStructNFSv3client client;
            public AnonymousStructNFSv3server server;
        }

        @FieldOrder({ "calls", "nullreq", "getattr", "setattr", "lookup", "access", "readlink", "read", "write",
                "create", "mkdir", "symlink", "mknod", "remove", "rmdir", "rename", "link", "readdir", "readdirplus",
                "fsstat", "fsinfo", "pathconf", "commit" })
        public static class AnonymousStructNFSv3client extends Structure {
            public long calls; // NFS V3 client calls
            public long nullreq; // NFS V3 client null requests
            public long getattr; // NFS V3 client getattr requests
            public long setattr; // NFS V3 client setattr requests
            public long lookup; // NFS V3 client file name lookup requests
            public long access; // NFS V3 client access requests
            public long readlink; // NFS V3 client readlink requests
            public long read; // NFS V3 client read requests
            public long write; // NFS V3 client write requests
            public long create; // NFS V3 client file creation requests
            public long mkdir; // NFS V3 client directory creation requests
            public long symlink; // NFS V3 client symbolic link creation requests
            public long mknod; // NFS V3 client mknod creation requests
            public long remove; // NFS V3 client file removal requests
            public long rmdir; // NFS V3 client directory removal requests
            public long rename; // NFS V3 client file rename requests
            public long link; // NFS V3 client link creation requests
            public long readdir; // NFS V3 client read-directory requests
            public long readdirplus; // NFS V3 client read-directory plus requests
            public long fsstat; // NFS V3 client file stat requests
            public long fsinfo; // NFS V3 client file info requests
            public long pathconf; // NFS V3 client path configure requests
            public long commit; // NFS V3 client commit requests
        }

        @FieldOrder({ "calls", "nullreq", "getattr", "setattr", "lookup", "access", "readlink", "read", "write",
                "create", "mkdir", "symlink", "mknod", "remove", "rmdir", "rename", "link", "readdir", "readdirplus",
                "fsstat", "fsinfo", "pathconf", "commit" })
        public static class AnonymousStructNFSv3server extends Structure {
            public long calls; // NFS V3 server requests
            public long nullreq; // NFS V3 server null requests
            public long getattr; // NFS V3 server getattr requests
            public long setattr; // NFS V3 server setattr requests
            public long lookup; // NFS V3 server file name lookup requests
            public long access; // NFS V3 server file access requests
            public long readlink; // NFS V3 server readlink requests
            public long read; // NFS V3 server read requests
            public long write; // NFS V3 server write requests
            public long create; // NFS V3 server file creation requests
            public long mkdir; // NFS V3 server director6 creation requests
            public long symlink; // NFS V3 server symbolic link creation requests
            public long mknod; // NFS V3 server mknode creation requests
            public long remove; // NFS V3 server file removal requests
            public long rmdir; // NFS V3 server directory removal requests
            public long rename; // NFS V3 server file rename requests
            public long link; // NFS V3 server link creation requests
            public long readdir; // NFS V3 server read-directory requests
            public long readdirplus; // NFS V3 server read-directory plus requests
            public long fsstat; // NFS V3 server file stat requests
            public long fsinfo; // NFS V3 server file info requests
            public long pathconf; // NFS V3 server path configure requests
            public long commit; // NFS V3 server commit requests
        }

        @FieldOrder({ "client", "server" })
        public static class AnonymousStructNFSv4 extends Structure {
            public AnonymousStructNFSv4client client;
            public AnonymousStructNFSv4server server;
        }

        @FieldOrder({ "operations", "nullreq", "getattr", "setattr", "lookup", "access", "readlink", "read", "write",
                "create", "mkdir", "symlink", "mknod", "remove", "rmdir", "rename", "link", "readdir", "statfs",
                "finfo", "commit", "open", "open_confirm", "open_downgrade", "close", "lock", "unlock", "lock_test",
                "set_clientid", "renew", "client_confirm", "secinfo", "release_lock", "replicate", "pcl_stat",
                "acl_stat_l", "pcl_stat_l", "acl_read", "pcl_read", "acl_write", "pcl_write", "delegreturn" })
        public static class AnonymousStructNFSv4client extends Structure {
            public long operations; // NFS V4 client operations
            public long nullreq; // NFS V4 client null operations
            public long getattr; // NFS V4 client getattr operations
            public long setattr; // NFS V4 client setattr operations
            public long lookup; // NFS V4 client lookup operations
            public long access; // NFS V4 client access operations
            public long readlink; // NFS V4 client read link operations
            public long read; // NFS V4 client read operations
            public long write; // NFS V4 client write operations
            public long create; // NFS V4 client create operations
            public long mkdir; // NFS V4 client mkdir operations
            public long symlink; // NFS V4 client symlink operations
            public long mknod; // NFS V4 client mknod operations
            public long remove; // NFS V4 client remove operations
            public long rmdir; // NFS V4 client rmdir operations
            public long rename; // NFS V4 client rename operations
            public long link; // NFS V4 client link operations
            public long readdir; // NFS V4 client readdir operations
            public long statfs; // NFS V4 client statfs operations
            public long finfo; // NFS V4 client file info operations
            public long commit; // NFS V4 client commit operations
            public long open; // NFS V4 client open operations
            public long open_confirm; // NFS V4 client open confirm operations
            public long open_downgrade; // NFS V4 client open downgrade operations
            public long close; // NFS V4 client close operations
            public long lock; // NFS V4 client lock operations
            public long unlock; // NFS V4 client unlock operations
            public long lock_test; // NFS V4 client lock test operations
            public long set_clientid; // NFS V4 client set client id operations
            public long renew; // NFS V4 client renew operations
            public long client_confirm; // NFS V4 client confirm operations
            public long secinfo; // NFS V4 client secinfo operations
            public long release_lock; // NFS V4 client release lock operations
            public long replicate; // NFS V4 client replicate operations
            public long pcl_stat; // NFS V4 client pcl_stat operations
            public long acl_stat_l; // NFS V4 client acl_stat long operations
            public long pcl_stat_l; // NFS V4 client pcl_stat long operations
            public long acl_read; // NFS V4 client acl_read operations
            public long pcl_read; // NFS V4 client pcl_read operations
            public long acl_write; // NFS V4 client acl_write operations
            public long pcl_write; // NFS V4 client pcl_write operations
            public long delegreturn; // NFS V4 client delegreturn operations
        }

        @FieldOrder({ "nullreq", "compound", "operations", "access", "close", "commit", "create", "delegpurge",
                "delegreturn", "getattr", "getfh", "link", "lock", "lockt", "locku", "lookup", "lookupp", "nverify",
                "open", "openattr", "open_confirm", "open_downgrade", "putfh", "putpubfh", "putrootfh", "read",
                "readdir", "readlink", "remove", "rename", "renew", "restorefh", "savefh", "secinfo",
                "setattr",
                "set_clientid", "clientid_confirm", "verify", "write", "release_lock" })
        public static class AnonymousStructNFSv4server extends Structure {
            public long nullreq; // NFS V4 server null calls
            public long compound; // NFS V4 server compound calls
            public long operations; // NFS V4 server operations
            public long access; // NFS V4 server access operations
            public long close; // NFS V4 server close operations
            public long commit; // NFS V4 server commit operations
            public long create; // NFS V4 server create operations
            public long delegpurge; // NFS V4 server del_purge operations
            public long delegreturn; // NFS V4 server del_ret operations
            public long getattr; // NFS V4 server getattr operations
            public long getfh; // NFS V4 server getfh operations
            public long link; // NFS V4 server link operations
            public long lock; // NFS V4 server lock operations
            public long lockt; // NFS V4 server lockt operations
            public long locku; // NFS V4 server locku operations
            public long lookup; // NFS V4 server lookup operations
            public long lookupp; // NFS V4 server lookupp operations
            public long nverify; // NFS V4 server nverify operations
            public long open; // NFS V4 server open operations
            public long openattr; // NFS V4 server openattr operations
            public long open_confirm; // NFS V4 server confirm operations
            public long open_downgrade; // NFS V4 server downgrade operations
            public long putfh; // NFS V4 server putfh operations
            public long putpubfh; // NFS V4 server putpubfh operations
            public long putrootfh; // NFS V4 server putrotfh operations
            public long read; // NFS V4 server read operations
            public long readdir; // NFS V4 server readdir operations
            public long readlink; // NFS V4 server readlink operations
            public long remove; // NFS V4 server remove operations
            public long rename; // NFS V4 server rename operations
            public long renew; // NFS V4 server renew operations
            public long restorefh; // NFS V4 server restorefh operations
            public long savefh; // NFS V4 server savefh operations
            public long secinfo; // NFS V4 server secinfo operations
            public long setattr; // NFS V4 server setattr operations
            public long set_clientid; // NFS V4 server setclid operations
            public long clientid_confirm; // NFS V4 server clid_cfm operations
            public long verify; // NFS V4 server verify operations
            public long write; // NFS V4 server write operations
            public long release_lock; // NFS V4 server release_lo operations
        }
    }

    /**
     * Retrieves total processor usage metrics
     *
     * @param name
     *            Reserved for future use, must be NULL
     * @param cpu
     *            Populated with structure
     * @param sizeof_struct
     *            Should be set to sizeof(perfstat_cpu_total_t)
     * @param desired_number
     *            Reserved for future use, must be set to 0 or 1
     * @return The return value is -1 in case of errors. Otherwise, the number of
     *         structures copied is returned. This is always 1.
     */
    int perfstat_cpu_total(perfstat_id_t name, perfstat_cpu_total_t cpu, int sizeof_struct, int desired_number);

    /**
     * Retrieves individual processor usage metrics
     *
     * @param name
     *            Structure containing empty string when collecting all cpu stats,
     *            or null to count CPUs
     * @param cpu
     *            Populated with structures, or null to count CPUs
     * @param sizeof_struct
     *            Should be set to sizeof(perfstat_cpu_t)
     * @param desired_number
     *            Set to 0 to count CPUs, set to number of cpus to return otherwise
     * @return The return value is -1 in case of errors. Otherwise, the number of
     *         structures copied is returned.
     */
    int perfstat_cpu(perfstat_id_t name, perfstat_cpu_t[] cpu, int sizeof_struct, int desired_number);

    /**
     * Retrieves total memory-related metrics
     *
     * @param name
     *            Reserved for future use, must be NULL
     * @param mem
     *            Populated with structure
     * @param sizeof_struct
     *            Should be set to sizeof(perfstat_memory_total_t)
     * @param desired_number
     *            Reserved for future use, must be set to 0 or 1
     * @return The return value is -1 in case of errors. Otherwise, the number of
     *         structures copied is returned. This is always 1.
     */
    int perfstat_memory_total(perfstat_id_t name, perfstat_memory_total_t mem, int sizeof_struct, int desired_number);

    /**
     * Retrieves process-related metrics
     *
     * @param name
     *            Structure containing empty string when collecting all process
     *            stats, or null to count processes
     * @param procs
     *            Populated with structure
     * @param sizeof_struct
     *            Should be set to sizeof(perfstat_process_t)
     * @param desired_number
     *            Set to 0 to count processes, set to number of processes to return
     *            otherwise
     * @return The return value is -1 in case of errors. Otherwise, the number of
     *         structures copied is returned.
     */
    int perfstat_process(perfstat_id_t name, perfstat_process_t[] procs, int sizeof_struct, int desired_number);

    /**
     * Retrieves disk statistics
     *
     * @param name
     *            Structure containing empty string when collecting all disk stats,
     *            or null to count block disks
     * @param disks
     *            Populated with structures, or null to count disks
     * @param sizeof_struct
     *            Should be set to sizeof(perfstat_disk_t)
     * @param desired_number
     *            Set to 0 to count disks, set to number of disks to return
     *            otherwise
     * @return The return value is -1 in case of errors. Otherwise, the number of
     *         structures copied is returned.
     */
    int perfstat_disk(perfstat_id_t name, perfstat_disk_t[] disks, int sizeof_struct, int desired_number);

    /**
     * Retrieves total memory-related metrics
     *
     * @param name
     *            Reserved for future use, must be NULL
     * @param config
     *            Populated with structure
     * @param sizeof_struct
     *            Should be set to sizeof(perfstat_partition_config_t)
     * @param desired_number
     *            Reserved for future use, must be set to 0 or 1
     * @return The return value is -1 in case of errors. Otherwise, the number of
     *         structures copied is returned. This is always 1.
     */
    int perfstat_partition_config(perfstat_id_t name, perfstat_partition_config_t config, int sizeof_struct,
            int desired_number);

    /**
     * Retrieves network interfaces
     *
     * @param name
     *            Structure containing empty string when collecting all interface
     *            stats, or null to count interfaces
     * @param netints
     *            Populated with structures, or null to count interfaces
     * @param sizeof_struct
     *            Should be set to sizeof(perfstat_netinterface_t)
     * @param desired_number
     *            Set to 0 to count network interfaces, set to number of interfaces
     *            to return otherwise
     * @return The return value is -1 in case of errors. Otherwise, the number of
     *         structures copied is returned.
     */
    int perfstat_netinterface(perfstat_id_t name, perfstat_netinterface_t[] netints, int sizeof_struct,
            int desired_number);

    /**
     * Retrieves protocol data
     *
     * @param name
     *            Structure containing empty string when collecting all protocol
     *            stats, or null to count stats
     * @param protocols
     *            Populated with structures, or null to count protocols
     * @param sizeof_struct
     *            Should be set to sizeof(perfstat_protocol_t)
     * @param desired_number
     *            Set to 0 to count protocols, set to number of protocols to return
     *            otherwise
     * @return The return value is -1 in case of errors. Otherwise, the number of
     *         structures copied is returned.
     */
    int perfstat_protocol(perfstat_id_t name, perfstat_protocol_t[] protocols, int sizeof_struct, int desired_number);
}
