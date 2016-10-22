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

import java.util.List;

import com.sun.jna.Structure;

/**
 * Definitions related to {@code getrlimit}/{@code setrlimit}
 * @author Lyor Goldstein
 */
public interface Resource {
    /** Per-process CPU limit, in seconds.  */
    int RLIMIT_CPU = 0;

    /** Largest file that can be created, in bytes.  */
    int RLIMIT_FSIZE = 1;

    /** Maximum size of data segment, in bytes.  */
    int RLIMIT_DATA = 2;

    /** Maximum size of stack segment, in bytes.  */
    int RLIMIT_STACK = 3;

    /** Largest core file that can be created, in bytes.  */
    int RLIMIT_CORE = 4;

    /**
     * Largest resident set size, in bytes. This affects swapping; processes
     * that are exceeding their resident set size will be more likely to have
     * physical memory taken from them.
     */
    int RLIMIT_RSS = 5;

    /** Number of open files.  */
    int RLIMIT_NOFILE = 7;

    /** Address space limit.  */
    int RLIMIT_AS = 9;

    /** Number of processes.  */
    int RLIMIT_NPROC = 6;

    /** Locked-in-memory address space.  */
    int RLIMIT_MEMLOCK = 8;

    /** Maximum number of file locks.  */
    int RLIMIT_LOCKS = 10;

    /** Maximum number of pending signals.  */
    int RLIMIT_SIGPENDING = 11;

    /** Maximum bytes in POSIX message queues.  */
    int RLIMIT_MSGQUEUE = 12;

    /**
     * Maximum nice priority allowed to raise to. Nice levels 19 .. -20
     * correspond to 0 .. 39 values of this resource limit.
     */
    int RLIMIT_NICE = 13;
    int RLIMIT_RTPRIO = 14;

    /**
     * Maximum CPU time in microseconds that a process scheduled under a
     * real-time scheduling policy may consume without making a blocking
     * system call before being forcibly de-scheduled.
     */
    int RLIMIT_RTTIME = 15;

    /** Number of {@code rlimit} values */
    int RLIMIT_NLIMITS = 16;

    public static class Rlimit extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("rlim_cur", "rlim_max");

        /** The current (soft) limit.  */
        public long rlim_cur;

        /** The hard limit.  */
        public long rlim_max;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    // see man(2) rlimit
    int getrlimit(int resource, Rlimit rlim);
    int setrlimit(int resource, Rlimit rlim);
}
