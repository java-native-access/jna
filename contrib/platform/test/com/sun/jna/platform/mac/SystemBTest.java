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

import junit.framework.TestCase;

import com.sun.jna.platform.mac.SystemB.HostCpuLoadInfo;
import com.sun.jna.platform.mac.SystemB.HostLoadInfo;
import com.sun.jna.platform.mac.SystemB.VMStatistics;
import com.sun.jna.platform.mac.SystemB.VMStatistics64;

import com.sun.jna.Memory;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Exercise the {@link SystemB} class.
 *
 * @author widdis@gmail.com
 */
// @SuppressWarnings("unused")
public class SystemBTest extends TestCase {

	public void testSysctl() {
		final String mibName = "hw.logicalcpu";
		final int nCpu = Runtime.getRuntime().availableProcessors();

		IntByReference size = new IntByReference(SystemB.INT_SIZE);
		Pointer p = new Memory(size.getValue());
		int ret = SystemB.INSTANCE.sysctlbyname(mibName, p, size, null, 0);
		assertEquals(ret, 0);
		// These values should be equal unless affinity is set, limiting nCpu
		assertTrue(p.getInt(0) >= nCpu);

		size = new IntByReference();
		ret = SystemB.INSTANCE.sysctlnametomib(mibName, null, size);
		assertEquals(ret, 0);
		// Size should be 2
		assertEquals(size.getValue(), 2);

		Pointer mibp = new Memory(size.getValue() * SystemB.INT_SIZE);
		ret = SystemB.INSTANCE.sysctlnametomib(mibName, mibp, size);
		assertEquals(ret, 0);
		// Size should be 2
		assertEquals(size.getValue(), 2);

		int[] mib = mibp.getIntArray(0, size.getValue());
		// mib should be { 6, 103(?) }
		assertEquals(mib.length, 2);
		assertEquals(mib[0], 6);

		size = new IntByReference(SystemB.INT_SIZE);
		p = new Memory(size.getValue());
		ret = SystemB.INSTANCE.sysctl(mib, mib.length, p, size, null, 0);
		assertTrue(p.getInt(0) >= nCpu);
	};

	public void testHostPageSize() {
		int machPort = SystemB.INSTANCE.mach_host_self();
		assertTrue(machPort > 0);

		LongByReference pPageSize = new LongByReference();
		int ret = SystemB.INSTANCE.host_page_size(machPort, pPageSize);
		assertEquals(ret, 0);
		// Probably 4096, definitely a power of 2
		assertTrue(pPageSize.getValue() > 0);
		assertEquals(pPageSize.getValue() & (pPageSize.getValue() - 1), 0);
	}

	public void testVMInfo() {
		int machPort = SystemB.INSTANCE.mach_host_self();
		assertTrue(machPort > 0);

		VMStatistics vmStats = new VMStatistics();
		int ret = SystemB.INSTANCE.host_statistics(machPort,
				SystemB.HOST_VM_INFO, vmStats,
				new IntByReference(vmStats.size() / SystemB.INT_SIZE));
		assertEquals(ret, 0);
		// Nonnegative
		assertTrue(vmStats.free_count >= 0);

		if (Platform.is64Bit()) {
			VMStatistics64 vmStats64 = new VMStatistics64();
			ret = SystemB.INSTANCE.host_statistics64(machPort,
					SystemB.HOST_VM_INFO, vmStats64, new IntByReference(
							vmStats64.size() / SystemB.INT_SIZE));
			assertEquals(ret, 0);
			// Nonnegative
			assertTrue(vmStats64.free_count >= 0);
		}
	}

	public void testCpuLoad() {
		int machPort = SystemB.INSTANCE.mach_host_self();
		assertTrue(machPort > 0);

		HostCpuLoadInfo cpuLoadInfo = new HostCpuLoadInfo();
		int ret = SystemB.INSTANCE.host_statistics(machPort,
				SystemB.HOST_CPU_LOAD_INFO, cpuLoadInfo, new IntByReference(
						cpuLoadInfo.size()));
		assertEquals(ret, 0);
		// Should be int[4]
		assertEquals(cpuLoadInfo.cpu_ticks.length, SystemB.CPU_STATE_MAX);
	}

	public void testHostLoad() {
		int machPort = SystemB.INSTANCE.mach_host_self();
		assertTrue(machPort > 0);

		HostLoadInfo hostLoadInfo = new HostLoadInfo();
		int ret = SystemB.INSTANCE.host_statistics(machPort,
				SystemB.HOST_CPU_LOAD_INFO, hostLoadInfo, new IntByReference(
						hostLoadInfo.size()));
		assertEquals(ret, 0);
		// Should be two int[3]'s
		assertEquals(hostLoadInfo.avenrun.length, 3);
		assertEquals(hostLoadInfo.mach_factor.length, 3);
		// Load factor can't be zero
		assertTrue(hostLoadInfo.avenrun[0] > 0);
	}

	public void testHostProcessorInfo() {
		int machPort = SystemB.INSTANCE.mach_host_self();
		assertTrue(machPort > 0);

		IntByReference procCount = new IntByReference();
		PointerByReference procCpuLoadInfo = new PointerByReference();
		IntByReference procInfoCount = new IntByReference();
		int ret = SystemB.INSTANCE.host_processor_info(machPort,
				SystemB.PROCESSOR_CPU_LOAD_INFO, procCount, procCpuLoadInfo,
        			procInfoCount);
		assertEquals(ret, 0);

		assertTrue(procCount.getValue() > 0);
		assertEquals(procCpuLoadInfo.getValue().getIntArray(0,
				procInfoCount.getValue()).length, procInfoCount.getValue());
	}
	
    public void testMachPorts() {
      int machPort = SystemB.INSTANCE.mach_host_self();
      assertTrue(machPort > 0);
      machPort = SystemB.INSTANCE.mach_task_self();
      assertTrue(machPort > 0);	  
      }

    public void testGetLoadAvg() {
      double[] loadavg = new double[3];
      int retval = SystemB.INSTANCE.getloadavg(loadavg, 3);
      assertEquals(retval, 3);
      assertTrue(loadavg[0] >= 0);
      assertTrue(loadavg[1] >= 0);
      assertTrue(loadavg[2] >= 0);
    }
	
	public static void main(java.lang.String[] argList) {
		junit.textui.TestRunner.run(SystemBTest.class);
	}

}
