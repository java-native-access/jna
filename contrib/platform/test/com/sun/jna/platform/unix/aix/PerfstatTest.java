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

import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_cpu_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_cpu_total_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_disk_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_id_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_memory_total_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_netinterface_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_partition_config_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_process_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_protocol_t;

import junit.framework.TestCase;

/**
 * Exercise the {@link Perfstat} class.
 */
public class PerfstatTest extends TestCase {

    private static final Perfstat PERF = Platform.isAIX() ? Perfstat.INSTANCE : null;

    public void testPerfstatParitionConfig() {
        if (Platform.isAIX()) {
            perfstat_partition_config_t config = new perfstat_partition_config_t();
            assertEquals("Error reading partition config", 1,
                    PERF.perfstat_partition_config(null, config, config.size(), 1));
            assertEquals("Incorrect OS name", System.getProperty("os.name"), Native.toString(config.OSName));
            assertEquals("Incorrect OS version", System.getProperty("os.version"), Native.toString(config.OSVersion));
        }
    }

    public void testPerfstatCpuTotal() {
        if (Platform.isAIX()) {
            perfstat_cpu_total_t cpuTotal = new perfstat_cpu_total_t();
            assertEquals("Error reading cpu total", 1, PERF.perfstat_cpu_total(null, cpuTotal, cpuTotal.size(), 1));
            assertTrue("Must have at least one active cpu", 0 < cpuTotal.ncpus);
        }
    }

    public void testPerfstatCpu() {
        if (Platform.isAIX()) {
            perfstat_cpu_t cpu = new perfstat_cpu_t();
            // With null, null, ..., 0, returns total # of elements
            int cputotal = PERF.perfstat_cpu(null, null, cpu.size(), 0);
            assertTrue("Must have at least one active cpu", 0 < cputotal);

            perfstat_cpu_t[] statp = (perfstat_cpu_t[]) cpu.toArray(cputotal);
            perfstat_id_t firstcpu = new perfstat_id_t(); // name is ""
            assertEquals("Error reading cpus", cputotal, PERF.perfstat_cpu(firstcpu, statp, cpu.size(), cputotal));
            for (int i = 0; i < statp.length; i++) {
                assertTrue("Must have nonempty cpu name", 0 < Native.toString(statp[i].name).length());
            }
        }
    }

    public void testPerfstatDisk() {
        if (Platform.isAIX()) {
            perfstat_disk_t disk_stats = new perfstat_disk_t();
            // With null, null, ..., 0, returns total # of elements
            int total = PERF.perfstat_disk(null, null, disk_stats.size(), 0);
            assertNotEquals("Error fetching disk stats", -1, total);
            // Just in case there's no disk
            if (total > 0) {
                perfstat_disk_t[] statp = (perfstat_disk_t[]) disk_stats.toArray(total);
                perfstat_id_t firstdisk_stats = new perfstat_id_t(); // name is ""
                assertEquals("Error fetching disks", total,
                        PERF.perfstat_disk(firstdisk_stats, statp, disk_stats.size(), total));
                for (int i = 0; i < statp.length; i++) {
                    assertTrue("Must have nonempty disk name", 0 < Native.toString(statp[i].name).length());
                }
            }
        }
    }

    public void testPerfstatMemory() {
        if (Platform.isAIX()) {
            perfstat_memory_total_t memory = new perfstat_memory_total_t();
            assertEquals("Error reading memory total", 1, PERF.perfstat_memory_total(null, memory, memory.size(), 1));
            assertTrue("Total Memory must be nonzero", 0 < memory.real_total);
            assertTrue("In Use Memory must be nonzero", 0 < memory.real_inuse);
            assertTrue("Available must not exceed total memory", memory.real_avail <= memory.real_total);
            assertTrue("Free must not exceed total memory", memory.real_free <= memory.real_total);
            assertTrue("In se must not exceed total memory", memory.real_inuse <= memory.real_total);
        }
    }

    public void testPerfstatNetInterfaces() {
        if (Platform.isAIX()) {
            perfstat_netinterface_t netinterface = new perfstat_netinterface_t();
            // With null, null, ..., 0, returns total # of elements
            int total = PERF.perfstat_netinterface(null, null, netinterface.size(), 0);
            assertNotEquals("Error fetching network interface stats", -1, total);
            // Just in case there's no net interface
            if (total > 0) {
                perfstat_netinterface_t[] statp = (perfstat_netinterface_t[]) netinterface.toArray(total);
                perfstat_id_t firstnetinterface = new perfstat_id_t(); // name is ""
                assertEquals("Error fetching network interfaces", total,
                        PERF.perfstat_netinterface(firstnetinterface, statp, netinterface.size(), total));
                for (int i = 0; i < statp.length; i++) {
                    assertTrue("Must have nonempty network interface name",
                            0 < Native.toString(statp[i].name).length());
                }
            }
        }
    }

    public void testPerfstatProcesses() {
        if (Platform.isAIX()) {
            perfstat_process_t process = new perfstat_process_t();
            // With null, null, ..., 0, returns total # of elements
            int procCount = PERF.perfstat_process(null, null, process.size(), 0);
            assertTrue("Must have at least one process", 0 < procCount);
            perfstat_process_t[] proct = (perfstat_process_t[]) process.toArray(procCount);
            perfstat_id_t firstprocess = new perfstat_id_t(); // name is ""
            int ret = PERF.perfstat_process(firstprocess, proct, process.size(), procCount);
            assertTrue("Error fetching processes", 0 < ret);
            // due to race condition ret may return fewer processes than asked for
            assertTrue("Fetched too many processes", ret <= procCount);
            Set<Long> pidSet = new HashSet<>();
            for (int i = 0; i < ret; i++) {
                assertTrue("Must have nonempty process name", 0 < Native.toString(proct[i].proc_name).length());
                assertFalse("Pid must be unique", pidSet.contains(proct[i].pid));
                pidSet.add(proct[i].pid);
                assertTrue("Process must have at least one thread", 0 < proct[i].num_threads);
            }
        }
    }

    public void testPerfstatProtocols() {
        if (Platform.isAIX()) {
            // Valid protocol names are union field names
            Set<String> protocolNames = new HashSet<>();
            for (Field f : perfstat_protocol_t.AnonymousUnionPayload.class.getDeclaredFields()) {
                protocolNames.add(f.getName());
            }

            perfstat_protocol_t protocol = new perfstat_protocol_t();
            // With null, null, ..., 0, returns total # of elements
            int total = PERF.perfstat_protocol(null, null, protocol.size(), 0);
            assertNotEquals("Error fetching protocol total", -1, total);
            if (total > 0) {
                perfstat_protocol_t[] statp = (perfstat_protocol_t[]) protocol.toArray(total);
                perfstat_id_t firstprotocol = new perfstat_id_t(); // name is ""
                assertEquals("Error fetching protocol stats", total,
                        PERF.perfstat_protocol(firstprotocol, statp, protocol.size(), total));
                for (int i = 0; i < total; i++) {
                    assertTrue("Invalid protocol", protocolNames.contains(Native.toString(statp[i].name)));
                    // Some protocols aren't implemented and return all 0, So testing that union
                    // reads properly and gives nonzero results must be a manual exercise
                }
            }
        }
    }
}
