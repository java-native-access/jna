/*
 * Copyright (c) 2015 Daniel Widdis
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
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

public interface SystemB extends LibCAPI, Library {

    SystemB INSTANCE = Native.load("System", SystemB.class);

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

    // params.h
    int MAXCOMLEN = 16;
    int MAXPATHLEN = 1024;
    int PROC_PIDPATHINFO_MAXSIZE = MAXPATHLEN * 4;

    // proc_info.h
    int PROC_ALL_PIDS = 1;
    int PROC_PIDTASKALLINFO = 2;
    int PROC_PIDTBSDINFO = 3;
    int PROC_PIDTASKINFO = 4;
    int PROC_PIDVNODEPATHINFO = 9;

    // length of fs type name including null
    int MFSTYPENAMELEN = 16;
    // length of buffer for returned name
    int MNAMELEN = MAXPATHLEN;

    // fsstat paths
    int MNT_WAIT = 0x0001;
    int MNT_NOWAIT = 0x0010;
    int MNT_DWAIT = 0x0100;

    // resource.h
    int RUSAGE_INFO_V2 = 2;

    @Structure.FieldOrder({ "cpu_ticks" })
    public static class HostCpuLoadInfo extends Structure {
        public int cpu_ticks[] = new int[CPU_STATE_MAX];
    }

    @Structure.FieldOrder({ "avenrun", "mach_factor" })
    public static class HostLoadInfo extends Structure {
        public int[] avenrun = new int[3]; // scaled by LOAD_SCALE
        public int[] mach_factor = new int[3]; // scaled by LOAD_SCALE
    }

    @Structure.FieldOrder({ "free_count", "active_count", "inactive_count", "wire_count", "zero_fill_count",
            "reactivations", "pageins", "pageouts", "faults", "cow_faults", "lookups", "hits", "purgeable_count",
            "purges", "speculative_count" })
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
    }

    @Structure.FieldOrder({ "free_count", "active_count", "inactive_count", "wire_count", "zero_fill_count",
            "reactivations", "pageins", "pageouts", "faults", "cow_faults", "lookups", "hits", "purges",
            "purgeable_count", "speculative_count", "decompressions", "compressions", "swapins", "swapouts",
            "compressor_page_count", "throttled_count", "external_page_count", "internal_page_count",
            "total_uncompressed_pages_in_compressor" })
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
    }

    @Structure.FieldOrder({ "pbsd", "ptinfo" })
    class ProcTaskAllInfo extends Structure {
        public ProcBsdInfo pbsd;
        public ProcTaskInfo ptinfo;
    }

    @Structure.FieldOrder({ "pbi_flags", "pbi_status", "pbi_xstatus", "pbi_pid", "pbi_ppid", "pbi_uid", "pbi_gid",
            "pbi_ruid", "pbi_rgid", "pbi_svuid", "pbi_svgid", "rfu_1", "pbi_comm", "pbi_name", "pbi_nfiles", "pbi_pgid",
            "pbi_pjobc", "e_tdev", "e_tpgid", "pbi_nice", "pbi_start_tvsec", "pbi_start_tvusec" })
    class ProcBsdInfo extends Structure {
        public int pbi_flags;
        public int pbi_status;
        public int pbi_xstatus;
        public int pbi_pid;
        public int pbi_ppid;
        public int pbi_uid;
        public int pbi_gid;
        public int pbi_ruid;
        public int pbi_rgid;
        public int pbi_svuid;
        public int pbi_svgid;
        public int rfu_1;
        public byte[] pbi_comm = new byte[MAXCOMLEN];
        public byte[] pbi_name = new byte[2 * MAXCOMLEN];
        public int pbi_nfiles;
        public int pbi_pgid;
        public int pbi_pjobc;
        public int e_tdev;
        public int e_tpgid;
        public int pbi_nice;
        public long pbi_start_tvsec;
        public long pbi_start_tvusec;
    }

    @Structure.FieldOrder({ "pti_virtual_size", "pti_resident_size", "pti_total_user", "pti_total_system",
            "pti_threads_user", "pti_threads_system", "pti_policy", "pti_faults", "pti_pageins", "pti_cow_faults",
            "pti_messages_sent", "pti_messages_received", "pti_syscalls_mach", "pti_syscalls_unix", "pti_csw",
            "pti_threadnum", "pti_numrunning", "pti_priority" })
    class ProcTaskInfo extends Structure {
        public long pti_virtual_size; /* virtual memory size (bytes) */
        public long pti_resident_size; /* resident memory size (bytes) */
        public long pti_total_user; /* total time (nanoseconds) */
        public long pti_total_system;
        public long pti_threads_user; /* existing threads only */
        public long pti_threads_system;
        public int pti_policy; /* default policy for new threads */
        public int pti_faults; /* number of page faults */
        public int pti_pageins; /* number of actual pageins */
        public int pti_cow_faults; /* number of copy-on-write faults */
        public int pti_messages_sent; /* number of messages sent */
        public int pti_messages_received; /* number of messages received */
        public int pti_syscalls_mach; /* number of mach system calls */
        public int pti_syscalls_unix; /* number of unix system calls */
        public int pti_csw; /* number of context switches */
        public int pti_threadnum; /* number of threads in the task */
        public int pti_numrunning; /* number of running threads */
        public int pti_priority; /* task priority */
    }

    @Structure.FieldOrder({ "v_swtch", "v_trap", "v_syscall", "v_intr", "v_soft", "v_faults", "v_lookups", "v_hits",
            "v_vm_faults", "v_cow_faults", "v_swpin", "v_swpout", "v_pswpin", "v_pswpout", "v_pageins", "v_pageouts",
            "v_pgpgin", "v_pgpgout", "v_intrans", "v_reactivated", "v_rev", "v_scan", "v_dfree", "v_pfree", "v_zfod",
            "v_nzfod", "v_page_size", "v_kernel_pages", "v_free_target", "v_free_min", "v_free_count", "v_wire_count",
            "v_active_count", "v_inactive_target", "v_inactive_count" })
    class VMMeter extends Structure {
        /*
         * General system activity.
         */
        public int v_swtch; /* context switches */
        public int v_trap; /* calls to trap */
        public int v_syscall; /* calls to syscall() */
        public int v_intr; /* device interrupts */
        public int v_soft; /* software interrupts */
        public int v_faults; /* total faults taken */
        /*
         * Virtual memory activity.
         */
        public int v_lookups; /* object cache lookups */
        public int v_hits; /* object cache hits */
        public int v_vm_faults; /* number of address memory faults */
        public int v_cow_faults; /* number of copy-on-writes */
        public int v_swpin; /* swapins */
        public int v_swpout; /* swapouts */
        public int v_pswpin; /* pages swapped in */
        public int v_pswpout; /* pages swapped out */
        public int v_pageins; /* number of pageins */
        public int v_pageouts; /* number of pageouts */
        public int v_pgpgin; /* pages paged in */
        public int v_pgpgout; /* pages paged out */
        public int v_intrans; /* intransit blocking page faults */
        public int v_reactivated; /*
                                   * number of pages reactivated from free list
                                   */
        public int v_rev; /* revolutions of the hand */
        public int v_scan; /* scans in page out daemon */
        public int v_dfree; /* pages freed by daemon */
        public int v_pfree; /* pages freed by exiting processes */
        public int v_zfod; /* pages zero filled on demand */
        public int v_nzfod; /* number of zfod's created */
        /*
         * Distribution of page usages.
         */
        public int v_page_size; /* page size in bytes */
        public int v_kernel_pages; /* number of pages in use by kernel */
        public int v_free_target; /* number of pages desired free */
        public int v_free_min; /* minimum number of pages desired free */
        public int v_free_count; /* number of pages free */
        public int v_wire_count; /* number of pages wired down */
        public int v_active_count; /* number of pages active */
        public int v_inactive_target; /* number of pages desired inactive */
        public int v_inactive_count; /* number of pages inactive */
    }

    @Structure.FieldOrder({ "ri_uuid", "ri_user_time", "ri_system_time", "ri_pkg_idle_wkups", "ri_interrupt_wkups",
            "ri_pageins", "ri_wired_size", "ri_resident_size", "ri_phys_footprint", "ri_proc_start_abstime",
            "ri_proc_exit_abstime", "ri_child_user_time", "ri_child_system_time", "ri_child_pkg_idle_wkups",
            "ri_child_interrupt_wkups", "ri_child_pageins", "ri_child_elapsed_abstime", "ri_diskio_bytesread",
            "ri_diskio_byteswritten" })
    class RUsageInfoV2 extends Structure {
        public byte[] ri_uuid = new byte[16];
        public long ri_user_time;
        public long ri_system_time;
        public long ri_pkg_idle_wkups;
        public long ri_interrupt_wkups;
        public long ri_pageins;
        public long ri_wired_size;
        public long ri_resident_size;
        public long ri_phys_footprint;
        public long ri_proc_start_abstime;
        public long ri_proc_exit_abstime;
        public long ri_child_user_time;
        public long ri_child_system_time;
        public long ri_child_pkg_idle_wkups;
        public long ri_child_interrupt_wkups;
        public long ri_child_pageins;
        public long ri_child_elapsed_abstime;
        public long ri_diskio_bytesread;
        public long ri_diskio_byteswritten;
    }

    @Structure.FieldOrder({ "vip_vi", "vip_path" })
    class VnodeInfoPath extends Structure {
        public byte[] vip_vi = new byte[152]; // vnode_info but we don't
                                              // need its data
        public byte[] vip_path = new byte[MAXPATHLEN];
    }

    @Structure.FieldOrder({ "pvi_cdir", "pvi_rdir" })
    class VnodePathInfo extends Structure {
        public VnodeInfoPath pvi_cdir;
        public VnodeInfoPath pvi_rdir;
    }

    /**
     * The statfs() routine returns information about a mounted file system. The
     * path argument is the path name of any file or directory within the mounted
     * file system. The buf argument is a pointer to a statfs structure.
     */
    @Structure.FieldOrder({ "f_bsize", "f_iosize", "f_blocks", "f_bfree", "f_bavail", "f_files", "f_ffree", "f_fsid",
            "f_owner", "f_type", "f_flags", "f_fssubtype", "f_fstypename", "f_mntonname", "f_mntfromname",
            "f_reserved" })
    class Statfs extends Structure {
        public int f_bsize; /* fundamental file system block size */
        public int f_iosize; /* optimal transfer block size */
        public long f_blocks; /* total data blocks in file system */
        public long f_bfree; /* free blocks in fs */
        public long f_bavail; /* free blocks avail to non-superuser */
        public long f_files; /* total file nodes in file system */
        public long f_ffree; /* free file nodes in fs */
        public int[] f_fsid = new int[2]; /* file system id */
        public int f_owner; /* user that mounted the filesystem */
        public int f_type; /* type of filesystem */
        public int f_flags; /* copy of mount exported flags */
        public int f_fssubtype; /* fs sub-type (flavor) */
        /* fs type name */
        public byte[] f_fstypename = new byte[MFSTYPENAMELEN];
        /* directory on which mounted */
        public byte[] f_mntonname = new byte[MAXPATHLEN];
        /* mounted filesystem */
        public byte[] f_mntfromname = new byte[MAXPATHLEN];
        public int[] f_reserved = new int[8]; /* For future use */
    }

    /**
     * Return type for sysctl vm.swapusage
     */
    @Structure.FieldOrder({ "xsu_total", "xsu_avail", "xsu_used", "xsu_pagesize", "xsu_encrypted" })
    class XswUsage extends Structure {
        public long xsu_total;
        public long xsu_avail;
        public long xsu_used;
        public int xsu_pagesize;
        public boolean xsu_encrypted;
    }

    /**
     * Data type as part of IFmsgHdr
     */
    @Structure.FieldOrder({ "ifi_type", "ifi_typelen", "ifi_physical", "ifi_addrlen", "ifi_hdrlen", "ifi_recvquota",
            "ifi_xmitquota", "ifi_unused1", "ifi_mtu", "ifi_metric", "ifi_baudrate", "ifi_ipackets", "ifi_ierrors",
            "ifi_opackets", "ifi_oerrors", "ifi_collisions", "ifi_ibytes", "ifi_obytes", "ifi_imcasts", "ifi_omcasts",
            "ifi_iqdrops", "ifi_noproto", "ifi_recvtiming", "ifi_xmittiming", "ifi_lastchange", "ifi_unused2",
            "ifi_hwassist", "ifi_reserved1", "ifi_reserved2" })
    class IFdata extends Structure {
        public byte ifi_type; // ethernet, tokenring, etc
        public byte ifi_typelen; // Length of frame type id
        public byte ifi_physical; // e.g., AUI, Thinnet, 10base-T, etc
        public byte ifi_addrlen; // media address length
        public byte ifi_hdrlen; // media header length
        public byte ifi_recvquota; // polling quota for receive intrs
        public byte ifi_xmitquota; // polling quota for xmit intrs
        public byte ifi_unused1; // for future use
        public int ifi_mtu; // maximum transmission unit
        public int ifi_metric; // routing metric (external only)
        public int ifi_baudrate; // linespeed
        public int ifi_ipackets; // packets received on interface
        public int ifi_ierrors; // input errors on interface
        public int ifi_opackets; // packets sent on interface
        public int ifi_oerrors; // output errors on interface
        public int ifi_collisions; // collisions on csma interfaces
        public int ifi_ibytes; // total number of octets received
        public int ifi_obytes; // total number of octets sent
        public int ifi_imcasts; // packets received via multicast
        public int ifi_omcasts; // packets sent via multicast
        public int ifi_iqdrops; // dropped on input, this interface
        public int ifi_noproto; // destined for unsupported protocol
        public int ifi_recvtiming; // usec spent receiving when timing
        public int ifi_xmittiming; // usec spent xmitting when timing
        public Timeval ifi_lastchange; // time of last administrative change
        public int ifi_unused2; // used to be the default_proto
        public int ifi_hwassist; // HW offload capabilities
        public int ifi_reserved1; // for future use
        public int ifi_reserved2; // for future use
    }

    /**
     * Return type for sysctl CTL_NET,PF_ROUTE
     */
    @Structure.FieldOrder({ "ifm_msglen", "ifm_version", "ifm_type", "ifm_addrs", "ifm_flags", "ifm_index",
            "ifm_data" })
    class IFmsgHdr extends Structure {
        public short ifm_msglen; // to skip over non-understood messages
        public byte ifm_version; // future binary compatability
        public byte ifm_type; // message type
        public int ifm_addrs; // like rtm_addrs
        public int ifm_flags; // value of if_flags
        public short ifm_index; // index for associated ifp
        public IFdata ifm_data; // statistics and other data about if

        public IFmsgHdr() {
            super();
        }

        public IFmsgHdr(Pointer p) {
            super(p);
        }
    }

    /**
     * Data type as part of IFmsgHdr
     */
    @Structure.FieldOrder({ "ifi_type", "ifi_typelen", "ifi_physical", "ifi_addrlen", "ifi_hdrlen", "ifi_recvquota",
            "ifi_xmitquota", "ifi_unused1", "ifi_mtu", "ifi_metric", "ifi_baudrate", "ifi_ipackets", "ifi_ierrors",
            "ifi_opackets", "ifi_oerrors", "ifi_collisions", "ifi_ibytes", "ifi_obytes", "ifi_imcasts", "ifi_omcasts",
            "ifi_iqdrops", "ifi_noproto", "ifi_recvtiming", "ifi_xmittiming", "ifi_lastchange" })
    class IFdata64 extends Structure {
        public byte ifi_type; // ethernet, tokenring, etc
        public byte ifi_typelen; // Length of frame type id
        public byte ifi_physical; // e.g., AUI, Thinnet, 10base-T, etc
        public byte ifi_addrlen; // media address length
        public byte ifi_hdrlen; // media header length
        public byte ifi_recvquota; // polling quota for receive intrs
        public byte ifi_xmitquota; // polling quota for xmit intrs
        public byte ifi_unused1; // for future use
        public int ifi_mtu; // maximum transmission unit
        public int ifi_metric; // routing metric (external only)
        public long ifi_baudrate; // linespeed
        public long ifi_ipackets; // packets received on interface
        public long ifi_ierrors; // input errors on interface
        public long ifi_opackets; // packets sent on interface
        public long ifi_oerrors; // output errors on interface
        public long ifi_collisions; // collisions on csma interfaces
        public long ifi_ibytes; // total number of octets received
        public long ifi_obytes; // total number of octets sent
        public long ifi_imcasts; // packets received via multicast
        public long ifi_omcasts; // packets sent via multicast
        public long ifi_iqdrops; // dropped on input, this interface
        public long ifi_noproto; // destined for unsupported protocol
        public int ifi_recvtiming; // usec spent receiving when timing
        public int ifi_xmittiming; // usec spent xmitting when timing
        public Timeval ifi_lastchange; // time of last administrative change
    }

    /**
     * Return type for sysctl CTL_NET,PF_ROUTE
     */
    @Structure.FieldOrder({ "ifm_msglen", "ifm_version", "ifm_type", "ifm_addrs", "ifm_flags", "ifm_index",
            "ifm_snd_len", "ifm_snd_maxlen", "ifm_snd_drops", "ifm_timer", "ifm_data" })
    class IFmsgHdr2 extends Structure {
        public short ifm_msglen; // to skip over non-understood messages
        public byte ifm_version; // future binary compatability
        public byte ifm_type; // message type
        public int ifm_addrs; // like rtm_addrs
        public int ifm_flags; // value of if_flags
        public short ifm_index; // index for associated ifp
        public int ifm_snd_len; // instantaneous length of send queue
        public int ifm_snd_maxlen; // maximum length of send queue
        public int ifm_snd_drops; // number of drops in send queue
        public int ifm_timer; // time until if_watchdog called
        public IFdata64 ifm_data; // statistics and other data about if

        public IFmsgHdr2(Pointer p) {
            super(p);
        }
    }

    /**
     * Return type for getpwuid
     */
    @Structure.FieldOrder({ "pw_name", "pw_passwd", "pw_uid", "pw_gid", "pw_change", "pw_class", "pw_gecos", "pw_dir",
            "pw_shell", "pw_expire", "pw_fields" })
    class Passwd extends Structure {
        public String pw_name; // user name
        public String pw_passwd; // encrypted password
        public int pw_uid; // user uid
        public int pw_gid; // user gid
        public NativeLong pw_change; // password change time
        public String pw_class; // user access class
        public String pw_gecos; // Honeywell login info
        public String pw_dir; // home directory
        public String pw_shell; // default shell
        public NativeLong pw_expire; // account expiration
        public int pw_fields; // internal: fields filled in
    }

    /**
     * Return type for getgrgid
     */
    @Structure.FieldOrder({ "gr_name", "gr_passwd", "gr_gid", "gr_mem" })
    class Group extends Structure {
        public String gr_name; /* group name */
        public String gr_passwd; /* group password */
        public int gr_gid; /* group id */
        public PointerByReference gr_mem; /* group members */
    }

    /**
     * Time value
     */
    @Structure.FieldOrder({ "tv_sec", "tv_usec" })
    class Timeval extends Structure {
        public NativeLong tv_sec; // seconds
        public int tv_usec; // microseconds
    }

    /**
     * Time Zone
     */
    @Structure.FieldOrder({ "tz_minuteswest", "tz_dsttime" })
    class Timezone extends Structure {
        public int tz_minuteswest; /* of Greenwich */
        public int tz_dsttime; /* type of dst correction to apply */
    }

    /**
     * The system's notion of the current Greenwich time and the current time zone
     * is obtained with the gettimeofday() call, and set with the settimeofday()
     * call. The time is expressed in seconds and microseconds since midnight (0
     * hour), January 1, 1970. The resolution of the system clock is hardware
     * dependent, and the time may be updated continuously or in ``ticks.'' If tp is
     * NULL and tzp is non-NULL, gettimeofday() will populate the timezone struct in
     * tzp. If tp is non-NULL and tzp is NULL, then only the timeval struct in tp is
     * populated. If both tp and tzp are NULL, nothing is returned.
     *
     * @param tp
     *            Timeval structure
     * @param tzp
     *            Timezone structure
     * @return A 0 return value indicates that the call succeeded. A -1 return value
     *         indicates an error occurred, and in this case an error code is stored
     *         into the global variable errno.
     */
    int gettimeofday(Timeval tp, Timezone tzp);

    /**
     * The mach_host_self system call returns the calling thread's host name port.
     * It has an effect equivalent to receiving a send right for the host port.
     *
     * @return the host's name port
     */
    int mach_host_self();

    /**
     * The mach_task_self system call returns the calling thread's task_self port.
     * It has an effect equivalent to receiving a send right for the task's kernel
     * port.
     *
     * @return the task's kernel port
     */
    int mach_task_self();

    /**
     * Decrement the target port right's user reference count.
     *
     * @param port
     *            The port holding the right.
     * @param name
     *            The port's name for the right.
     * @return 0 if successful, a {@code kern_return_t} code otherwise.
     */
    int mach_port_deallocate(int port, int name);

    /**
     * The host_page_size function returns the page size for the given host.
     *
     * @param hostPort
     *            The name (or control) port for the host for which the page size is
     *            desired.
     * @param pPageSize
     *            The host's page size (in bytes), set on success.
     * @return 0 on success; sets errno on failure
     */
    int host_page_size(int hostPort, LongByReference pPageSize);

    /**
     * The host_statistics function returns scheduling and virtual memory statistics
     * concerning the host as specified by hostStat.
     *
     * @param hostPort
     *            The control port for the host for which information is to be
     *            obtained.
     * @param hostStat
     *            The type of statistics desired ({@link #HOST_LOAD_INFO},
     *            {@link #HOST_VM_INFO}, or {@link #HOST_CPU_LOAD_INFO})
     * @param stats
     *            Statistics about the specified host.
     * @param count
     *            On input, the maximum size of the buffer; on output, the size
     *            returned (in natural-sized units).
     * @return 0 on success; sets errno on failure
     */
    int host_statistics(int hostPort, int hostStat, Structure stats, IntByReference count);

    /**
     * The host_statistics64 function returns 64-bit virtual memory statistics
     * concerning the host as specified by hostStat.
     *
     * @param hostPort
     *            The control port for the host for which information is to be
     *            obtained.
     * @param hostStat
     *            The type of statistics desired ({@link #HOST_VM_INFO64})
     * @param stats
     *            Statistics about the specified host.
     * @param count
     *            On input, the maximum size of the buffer; on output, the size
     *            returned (in natural-sized units).
     * @return 0 on success; sets errno on failure
     */
    int host_statistics64(int hostPort, int hostStat, Structure stats, IntByReference count);

    /**
     * The sysctl() function retrieves system information and allows processes with
     * appropriate privileges to set system information. The information available
     * from sysctl() consists of integers, strings, and tables.
     * <p>
     * The state is described using a "Management Information Base" (MIB) style
     * name, listed in name, which is a namelen length array of integers.
     * <p>
     * The information is copied into the buffer specified by oldp. The size of the
     * buffer is given by the location specified by oldlenp before the call, and
     * that location gives the amount of data copied after a successful call and
     * after a call that returns with the error code ENOMEM. If the amount of data
     * available is greater than the size of the buffer supplied, the call supplies
     * as much data as fits in the buffer provided and returns with the error code
     * ENOMEM. If the old value is not desired, oldp and oldlenp should be set to
     * NULL.
     * <p>
     * The size of the available data can be determined by calling sysctl() with the
     * NULL argument for oldp. The size of the available data will be returned in
     * the location pointed to by oldlenp. For some operations, the amount of space
     * may change often. For these operations, the system attempts to round up so
     * that the returned size is large enough for a call to return the data shortly
     * thereafter.
     * <p>
     * To set a new value, newp is set to point to a buffer of length newlen from
     * which the requested value is to be taken. If a new value is not to be set,
     * newp should be set to NULL and newlen set to 0.
     *
     * @param name
     *            a Management Information Base (MIB) array of integers
     * @param namelen
     *            the length of the array in {@code name}
     * @param oldp
     *            A buffer to hold the information retrieved
     * @param oldlenp
     *            Size of the buffer, a pointer to a {@link size_t} value
     * @param newp
     *            To set a new value, a buffer of information to be written. May be
     *            null if no value is to be set.
     * @param newlen
     *            Size of the information to be written. May be 0 if no value is to
     *            be set.
     * @return 0 on success; sets errno on failure
     */
    int sysctl(int[] name, int namelen, Pointer oldp, size_t.ByReference oldlenp, Pointer newp, size_t newlen);

    /**
     * @deprecated Use
     *             {@link #sysctl(int[], int, Pointer, Pointer, Pointer, com.sun.jna.platform.unix.LibCAPI.size_t)}
     */
    @Deprecated
    int sysctl(int[] name, int namelen, Pointer oldp, IntByReference oldlenp, Pointer newp, int newlen);

    /**
     * The sysctlbyname() function accepts an ASCII representation of the name and
     * internally looks up the integer name vector. Apart from that, it behaves the
     * same as the standard sysctl() function.
     *
     * @param name
     *            ASCII representation of the MIB name
     * @param oldp
     *            A buffer to hold the information retrieved
     * @param oldlenp
     *            Size of the buffer, a pointer to a {@link size_t} value
     * @param newp
     *            To set a new value, a buffer of information to be written. May be
     *            null if no value is to be set.
     * @param newlen
     *            Size of the information to be written. May be 0 if no value is to
     *            be set.
     * @return 0 on success; sets errno on failure
     */
    int sysctlbyname(String name, Pointer oldp, size_t.ByReference oldlenp, Pointer newp, size_t newlen);

    /**
     * @deprecated Use
     *             {@link #sysctlbyname(String, Pointer, Pointer, Pointer, com.sun.jna.platform.unix.LibCAPI.size_t)}
     */
    @Deprecated
    int sysctlbyname(String name, Pointer oldp, IntByReference oldlenp, Pointer newp, int newlen);

    /**
     * This function accepts an ASCII representation of the name, looks up the
     * integer name vector, and returns the numeric representation in the mib array
     * pointed to by mibp. The number of elements in the mib array is given by the
     * location specified by sizep before the call, and that location gives the
     * number of entries copied after a successful call. The resulting mib and size
     * may be used in subsequent sysctl() calls to get the data associated with the
     * requested ASCII name. This interface is intended for use by applications that
     * want to repeatedly request the same variable (the sysctl() function runs in
     * about a third the time as the same request made via the sysctlbyname()
     * function).
     * <p>
     * The number of elements in the mib array can be determined by calling
     * sysctlnametomib() with the NULL argument for mibp.
     * <p>
     * The sysctlnametomib() function is also useful for fetching mib prefixes. If
     * size on input is greater than the number of elements written, the array still
     * contains the additional elements which may be written programmatically.
     *
     * @param name
     *            ASCII representation of the name
     * @param mibp
     *            Integer array containing the corresponding name vector.
     * @param sizep
     *            On input, number of elements in the returned array; on output, the
     *            number of entries copied.
     * @return 0 on success; sets errno on failure
     */
    int sysctlnametomib(String name, Pointer mibp, size_t.ByReference sizep);

    /**
     * @deprecated Use {@link #sysctlnametomib(String, Pointer, Pointer)}
     */
    @Deprecated
    int sysctlnametomib(String name, Pointer mibp, IntByReference sizep);

    /**
     * The host_processor_info function returns information about processors.
     *
     * @param hostPort
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
    int host_processor_info(int hostPort, int flavor, IntByReference procCount, PointerByReference procInfo,
            IntByReference procInfoCount);

    /**
     * This function searches the password database for the given user uid, always
     * returning the first one encountered.
     *
     * @param uid
     *            The user ID
     * @return a Passwd structure matching that user
     */
    Passwd getpwuid(int uid);

    /**
     * This function searches the group database for the given group name pointed to
     * by the group id given by gid, returning the first one encountered. Identical
     * group gids may result in undefined behavior.
     *
     * @param gid
     *            The group ID
     * @return a Group structure matching that group
     */
    Group getgrgid(int gid);

    /**
     * Search through the current processes
     *
     * @param type
     *            types of processes to be searched
     * @param typeinfo
     *            adjunct information for type
     * @param buffer
     *            a C array of int-sized values to be filled with process
     *            identifiers that hold an open file reference matching the
     *            specified path or volume. Pass NULL to obtain the minimum buffer
     *            size needed to hold the currently active processes.
     * @param buffersize
     *            the size (in bytes) of the provided buffer.
     * @return the number of bytes of data returned in the provided buffer; -1 if an
     *         error was encountered;
     */
    int proc_listpids(int type, int typeinfo, int[] buffer, int buffersize);

    /**
     * Return in buffer a proc_*info structure corresponding to the flavor for the
     * specified process
     *
     * @param pid
     *            the process identifier
     * @param flavor
     *            the type of information requested
     * @param arg
     *            argument possibly needed for some flavors
     * @param buffer
     *            holds results
     * @param buffersize
     *            size of results
     * @return the number of bytes of data returned in the provided buffer; -1 if an
     *         error was encountered;
     */
    int proc_pidinfo(int pid, int flavor, long arg, Structure buffer, int buffersize);

    /**
     * Return in buffer the name of the specified process
     *
     * @param pid
     *            the process identifier
     * @param buffer
     *            holds results
     * @param buffersize
     *            size of results
     * @return the length of the name returned in buffer if successful; 0 otherwise
     */
    int proc_pidpath(int pid, Pointer buffer, int buffersize);

    /**
     * Return resource usage information for the given pid, which can be a live
     * process or a zombie.
     *
     * @param pid
     *            the process identifier
     * @param flavor
     *            the type of information requested
     * @param buffer
     *            holds results
     * @return 0 on success; or -1 on failure, with errno set to indicate the
     *         specific error.
     */
    int proc_pid_rusage(int pid, int flavor, RUsageInfoV2 buffer);

    /**
     * The getfsstat() function returns information about all mounted file systems.
     * The buf argument is a pointer to an array of statfs structures.
     *
     * Fields that are undefined for a particular file system are set to -1. The
     * buffer is filled with an array of statfs structures, one for each mounted
     * file system up to the size specified by bufsize.
     *
     * @param buf
     *            Array of statfs structures that will be filled with results. If
     *            buf is given as NULL, getfsstat() returns just the number of
     *            mounted file systems.
     * @param bufsize
     *            Size of the buffer to fill
     * @param flags
     *            If flags is set to MNT_NOWAIT, getfsstat() will directly return
     *            the information retained in the kernel to avoid delays caused by
     *            waiting for updated information from a file system that is perhaps
     *            temporarily unable to respond. Some of the information returned
     *            may be out of date, however; if flags is set to MNT_WAIT or
     *            MNT_DWAIT instead, getfsstat() will request updated information
     *            from each mounted filesystem before returning.
     * @return Upon successful completion, the number of statfs structures is
     *         returned. Otherwise, -1 is returned and the global variable errno is
     *         set to indicate the error.
     */
    int getfsstat64(Statfs[] buf, int bufsize, int flags);

    /**
     * Returns the process ID of the calling process. The ID is guaranteed to be
     * unique and is useful for constructing temporary file names.
     *
     * @return the process ID of the calling process.
     */
    int getpid();
}
