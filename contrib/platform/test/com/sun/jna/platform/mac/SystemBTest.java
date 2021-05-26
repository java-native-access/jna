/* Copyright (c) 2015 Daniel Widdis
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

import static org.junit.Assert.assertNotEquals;

import java.util.Date;
import java.util.Map;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.SystemB.Group;
import com.sun.jna.platform.mac.SystemB.HostCpuLoadInfo;
import com.sun.jna.platform.mac.SystemB.HostLoadInfo;
import com.sun.jna.platform.mac.SystemB.IFmsgHdr;
import com.sun.jna.platform.mac.SystemB.IFmsgHdr2;
import com.sun.jna.platform.mac.SystemB.Passwd;
import com.sun.jna.platform.mac.SystemB.ProcTaskAllInfo;
import com.sun.jna.platform.mac.SystemB.RUsageInfoV2;
import com.sun.jna.platform.mac.SystemB.Statfs;
import com.sun.jna.platform.mac.SystemB.Timeval;
import com.sun.jna.platform.mac.SystemB.Timezone;
import com.sun.jna.platform.mac.SystemB.VMMeter;
import com.sun.jna.platform.mac.SystemB.VMStatistics;
import com.sun.jna.platform.mac.SystemB.VMStatistics64;
import com.sun.jna.platform.mac.SystemB.VnodeInfoPath;
import com.sun.jna.platform.mac.SystemB.VnodePathInfo;
import com.sun.jna.platform.mac.SystemB.XswUsage;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

import junit.framework.TestCase;
import static com.sun.jna.platform.unix.LibCAPI.size_t;
/**
 * Exercise the {@link SystemB} class.
 */
public class SystemBTest extends TestCase {

    public void testSysctl() {
        final String mibName = "hw.logicalcpu";
        final int nCpu = Runtime.getRuntime().availableProcessors();

        // This sysctl returns a 32-bit integer cpu count
        size_t size = new size_t(Integer.BYTES);
        Pointer p = new Memory(size.longValue());
        size_t.ByReference plen = new size_t.ByReference(size);
        assertEquals(0, SystemB.INSTANCE.sysctlbyname(mibName, p, plen, null, size_t.ZERO));
        // These values should be equal unless affinity is set, limiting nCpu
        assertTrue(p.getInt(0) >= nCpu);

        // Get the same value by converting the string to the MIB array
        // Get the size of the MIB array
        size = new size_t(Integer.BYTES);
        size_t.ByReference sizep = new size_t.ByReference(size);
        assertEquals(0, SystemB.INSTANCE.sysctlnametomib(mibName, null, sizep));
        // Array size should be 2
        int sizepval = sizep.getValue().intValue();
        assertEquals(2, sizepval);

        // Allocate the correct size and fill the MIB array
        Pointer mibp = new Memory(sizepval * SystemB.INT_SIZE);
        assertEquals(0, SystemB.INSTANCE.sysctlnametomib(mibName, mibp, sizep));
        // Array size should still be 2
        sizepval = sizep.getValue().intValue();
        assertEquals(2, sizepval);

        int[] mib = mibp.getIntArray(0, sizepval);
        // mib should be { 6, 103(?) }
        assertEquals(mib.length, 2);
        assertEquals(mib[0], 6);

        size = new size_t(Integer.BYTES);
        p = new Memory(size.longValue());
        plen = new size_t.ByReference(size);
        assertEquals(0, SystemB.INSTANCE.sysctl(mib, mib.length, p, plen, null, size_t.ZERO));
        assertTrue(p.getInt(0) >= nCpu);
    }

    public void testHostPageSize() {
        int hostPort = SystemB.INSTANCE.mach_host_self();
        assertNotEquals(0, hostPort);

        LongByReference pPageSize = new LongByReference();
        int ret = SystemB.INSTANCE.host_page_size(hostPort, pPageSize);
        assertEquals(ret, 0);
        // Probably 4096, definitely a power of 2
        assertTrue(pPageSize.getValue() > 0);
        assertEquals(pPageSize.getValue() & (pPageSize.getValue() - 1), 0);
    }

    public void testVMInfo() {
        int hostPort = SystemB.INSTANCE.mach_host_self();
        assertNotEquals(0, hostPort);

        VMStatistics vmStats = new VMStatistics();
        int ret = SystemB.INSTANCE.host_statistics(hostPort, SystemB.HOST_VM_INFO, vmStats,
                new IntByReference(vmStats.size() / SystemB.INT_SIZE));
        assertEquals(ret, 0);
        // Nonnegative
        assertTrue(vmStats.free_count >= 0);

        if (Platform.is64Bit()) {
            VMStatistics64 vmStats64 = new VMStatistics64();
            ret = SystemB.INSTANCE.host_statistics64(hostPort, SystemB.HOST_VM_INFO, vmStats64,
                    new IntByReference(vmStats64.size() / SystemB.INT_SIZE));
            assertEquals(ret, 0);
            // Nonnegative
            assertTrue(vmStats64.free_count >= 0);
        }
    }

    public void testCpuLoad() {
        int hostPort = SystemB.INSTANCE.mach_host_self();
        assertNotEquals(0, hostPort);

        HostCpuLoadInfo cpuLoadInfo = new HostCpuLoadInfo();
        int ret = SystemB.INSTANCE.host_statistics(hostPort, SystemB.HOST_CPU_LOAD_INFO, cpuLoadInfo,
                new IntByReference(cpuLoadInfo.size()));
        assertEquals(ret, 0);
        // Should be int[4]
        assertEquals(cpuLoadInfo.cpu_ticks.length, SystemB.CPU_STATE_MAX);
    }

    public void testHostLoad() {
        int hostPort = SystemB.INSTANCE.mach_host_self();
        assertNotEquals(0, hostPort);

        HostLoadInfo hostLoadInfo = new HostLoadInfo();
        int ret = SystemB.INSTANCE.host_statistics(hostPort, SystemB.HOST_CPU_LOAD_INFO, hostLoadInfo,
                new IntByReference(hostLoadInfo.size()));
        assertEquals(ret, 0);
        // Should be two int[3]'s
        assertEquals(hostLoadInfo.avenrun.length, 3);
        assertEquals(hostLoadInfo.mach_factor.length, 3);
        // Load factor can't be zero
        assertTrue(hostLoadInfo.avenrun[0] > 0);
    }

    public void testHostProcessorInfo() {
        int hostPort = SystemB.INSTANCE.mach_host_self();
        assertNotEquals(0, hostPort);

        IntByReference procCount = new IntByReference();
        PointerByReference procCpuLoadInfo = new PointerByReference();
        IntByReference procInfoCount = new IntByReference();
        int ret = SystemB.INSTANCE.host_processor_info(hostPort, SystemB.PROCESSOR_CPU_LOAD_INFO, procCount,
                procCpuLoadInfo, procInfoCount);
        assertEquals(ret, 0);

        assertTrue(procCount.getValue() > 0);
        assertEquals(procCpuLoadInfo.getValue().getIntArray(0, procInfoCount.getValue()).length,
                procInfoCount.getValue());
    }

    // From Unix LibCAPI
    public void testGetenv() {
        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> ee : env.entrySet()) {
            String name = ee.getKey();
            String expected = ee.getValue();
            String actual = SystemB.INSTANCE.getenv(name);
            assertEquals(name, expected, actual);
        }
    }

    // From Unix LibCAPI
    public void testSetenv() {
        String name = "SystemBTestEnv";
        try {
            String expected = new Date(System.currentTimeMillis()).toString();
            assertEquals("setenv", 0, SystemB.INSTANCE.setenv(name, expected, 1));
            assertEquals("Mismatched values", expected, SystemB.INSTANCE.getenv(name));
            assertEquals("unsetenv", 0, SystemB.INSTANCE.unsetenv(name));
        } finally {
            SystemB.INSTANCE.unsetenv(name);
        }
    }

    // From Unix LibCAPI
    public void testGetLoadAvg() {
        double[] loadavg = new double[3];
        int retval = SystemB.INSTANCE.getloadavg(loadavg, 3);
        assertEquals(retval, 3);
        assertTrue(loadavg[0] >= 0);
        assertTrue(loadavg[1] >= 0);
        assertTrue(loadavg[2] >= 0);
    }

    // From Unix LibCAPI
    public void testGethostnameGetdomainname() {
        byte[] buffer = new byte[256];
        assertEquals("gethostname", 0, SystemB.INSTANCE.gethostname(buffer, buffer.length));
        String hostname = Native.toString(buffer);
        assertTrue(hostname.length() > 0);
        assertEquals("getdomainname", 0, SystemB.INSTANCE.getdomainname(buffer, buffer.length));
        // May have length 0
    }

    public void testTimeofDay() {
        Timeval tp = new Timeval();
        long before = System.currentTimeMillis();
        assertEquals(0, SystemB.INSTANCE.gettimeofday(tp, null));
        long timeofday = tp.tv_sec.longValue() * 1000L + tp.tv_usec / 1000;
        long after = System.currentTimeMillis();
        assertTrue(before <= timeofday);
        assertTrue(after >= timeofday);

        Timezone tzp = new Timezone();
        assertEquals(0, SystemB.INSTANCE.gettimeofday(null, tzp));
        // DST is 0 or 1
        assertTrue(tzp.tz_dsttime <= 1);
        // Timezones are GMT -12 (west) to +14 hours
        assertTrue(tzp.tz_minuteswest <= 12 * 60);
        assertTrue(tzp.tz_minuteswest >= -14 * 60);
    }

    public void testVMMeter() {
        int hostPort = SystemB.INSTANCE.mach_host_self();
        assertNotEquals(0, hostPort);
        VMMeter vmstats = new VMMeter();
        assertEquals(0, SystemB.INSTANCE.host_statistics(hostPort, SystemB.HOST_VM_INFO, vmstats,
                new IntByReference(vmstats.size())));
        assertTrue(vmstats.v_lookups >= 0);
    }

    public void testStatfs() {
        // Use statfs to populate mount point map
        int numfs = SystemB.INSTANCE.getfsstat64(null, 0, 0);
        Statfs[] fs = new Statfs[numfs];
        // Fill array with results
        SystemB.INSTANCE.getfsstat64(fs, numfs * new Statfs().size(), SystemB.MNT_NOWAIT);
        // Iterate all mounted file systems
        for (Statfs f : fs) {
            assertTrue(f.f_bfree <= f.f_blocks);
            assertTrue(f.f_ffree <= f.f_files);
        }
    }

    public void testXswUsage() {
        XswUsage xswUsage = new XswUsage();
        assertEquals(0, SystemB.INSTANCE.sysctlbyname("vm.swapusage", xswUsage.getPointer(),
                new size_t.ByReference(xswUsage.size()), null, size_t.ZERO));
        xswUsage.read();
        assertTrue(xswUsage.xsu_used <= xswUsage.xsu_total);
    }

    public void testProcessStructures() {
        // Calc max # of processes
        size_t.ByReference size = new size_t.ByReference(4);
        Memory mem = new Memory(4);
        assertEquals(0, SystemB.INSTANCE.sysctlbyname("kern.maxproc", mem, size, null, size_t.ZERO));
        int maxProc = mem.getInt(0);

        // Get list of pids
        int[] pids = new int[maxProc];
        int bytesReturned = SystemB.INSTANCE.proc_listpids(SystemB.PROC_ALL_PIDS, 0, pids, maxProc * 4);
        assertTrue(bytesReturned > 0);
        assertEquals(0, bytesReturned % 4);

        // Current pid should be in list
        int pid = SystemB.INSTANCE.getpid();
        assertTrue(pid > 0);
        boolean foundPid = false;
        for (int i = 0; i < pids.length; i++) {
            if (pids[i] == pid) {
                foundPid = true;
                break;
            }
        }
        assertTrue(foundPid);

        // ProcTaskAllInfo
        ProcTaskAllInfo taskAllInfo = new ProcTaskAllInfo();
        bytesReturned = SystemB.INSTANCE.proc_pidinfo(pid, SystemB.PROC_PIDTASKALLINFO, 0, taskAllInfo,
                taskAllInfo.size());
        assertTrue(bytesReturned > 0);
        assertEquals(0, bytesReturned % Native.getNativeSize(ProcTaskAllInfo.class));

        Pointer buf = new Memory(SystemB.PROC_PIDPATHINFO_MAXSIZE);
        bytesReturned = SystemB.INSTANCE.proc_pidpath(pid, buf, SystemB.PROC_PIDPATHINFO_MAXSIZE);
        String path = buf.getString(0).trim();
        assertEquals(bytesReturned, path.length());

        // ProcTaskInfo
        assertTrue(taskAllInfo.ptinfo.pti_threadnum > 0);

        // ProcBsdInfo
        Passwd user = SystemB.INSTANCE.getpwuid(taskAllInfo.pbsd.pbi_uid);
        assertEquals(user.pw_uid, taskAllInfo.pbsd.pbi_uid);

        Group group = SystemB.INSTANCE.getgrgid(taskAllInfo.pbsd.pbi_gid);
        assertEquals(group.gr_gid, taskAllInfo.pbsd.pbi_gid);

        RUsageInfoV2 rUsageInfoV2 = new RUsageInfoV2();
        assertEquals(0, SystemB.INSTANCE.proc_pid_rusage(pid, SystemB.RUSAGE_INFO_V2, rUsageInfoV2));
        assertTrue(rUsageInfoV2.ri_diskio_bytesread >= 0);

        VnodePathInfo vpi = new VnodePathInfo();
        bytesReturned = SystemB.INSTANCE.proc_pidinfo(pid, SystemB.PROC_PIDVNODEPATHINFO, 0, vpi, vpi.size());
        assertTrue(bytesReturned > 0);
        assertEquals(0, bytesReturned % Native.getNativeSize(VnodeInfoPath.class));

        String cwd = Native.toString(vpi.pvi_cdir.vip_path);
        assertTrue(cwd.length() > 0);
    }

    public void testIFs() {
        int RTM_IFINFO2 = 0x12;
        // Get buffer of all interface information
        int CTL_NET = 4;
        int PF_ROUTE = 17;
        int NET_RT_IFLIST2 = 6;
        int[] mib = { CTL_NET, PF_ROUTE, 0, 0, NET_RT_IFLIST2, 0 };

        size_t.ByReference len = new size_t.ByReference();
        assertEquals(0, SystemB.INSTANCE.sysctl(mib, 6, null, len, null, size_t.ZERO));
        // Add enough room for max size of IFmsgHdr to avoid JNA bounds check
        // problems with worst case structure size
        Memory buf = new Memory(len.getValue().longValue() + 112);
        assertEquals(0, SystemB.INSTANCE.sysctl(mib, 6, buf, len, null, size_t.ZERO));

        // Iterate offset from buf's pointer up to limit of buf
        int lim = len.getValue().intValue();
        int next = 0;
        while (next < lim) {
            // Get pointer to current native part of buf
            Pointer p = buf.share(next);
            // Cast pointer to if_msghdr
            IFmsgHdr ifm = new IFmsgHdr(p);
            ifm.read();
            // Advance next
            next += ifm.ifm_msglen;
            // Skip messages which are not the right format
            if (ifm.ifm_type != RTM_IFINFO2) {
                continue;
            }
            // Cast pointer to if_msghdr2
            IFmsgHdr2 if2m = new IFmsgHdr2(p);
            if2m.read();

            assertTrue(if2m.ifm_index >= 0);
            assertTrue(if2m.ifm_data.ifi_ibytes >= 0);
            assertTrue(if2m.ifm_data.ifi_lastchange.tv_usec >= 0);
        }
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(SystemBTest.class);
    }
}
