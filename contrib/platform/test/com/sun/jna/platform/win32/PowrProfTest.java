/* Copyright (c) 2019 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.PowrProf.POWER_INFORMATION_LEVEL;
import com.sun.jna.platform.win32.PowrProf.ProcessorPowerInformation;
import com.sun.jna.platform.win32.PowrProf.SystemBatteryState;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;

import junit.framework.TestCase;

public class PowrProfTest extends TestCase {

    public void testProcessorPowerInformation() {
        SYSTEM_INFO info = new SYSTEM_INFO();
        Kernel32.INSTANCE.GetSystemInfo(info);
        int numProcs = info.dwNumberOfProcessors.intValue();

        ProcessorPowerInformation ppi = new ProcessorPowerInformation();
        long[] freqs = new long[numProcs];
        int bufferSize = ppi.size() * numProcs;
        Memory mem = new Memory(bufferSize);
        assertEquals(NTStatus.STATUS_SUCCESS, PowrProf.INSTANCE
                .CallNtPowerInformation(POWER_INFORMATION_LEVEL.PROCESSOR_INFORMATION, null, 0,
                mem, bufferSize));
        for (int i = 0; i < freqs.length; i++) {
            ppi = new ProcessorPowerInformation(mem.share(i * (long) ppi.size()));
            assertTrue(ppi.currentMhz <= ppi.maxMhz);
        }
    }

    public void testSystemBatteryState() {
        int size = new SystemBatteryState().size();
        Memory mem = new Memory(size);
        assertEquals(NTStatus.STATUS_SUCCESS, 
                PowrProf.INSTANCE.CallNtPowerInformation(POWER_INFORMATION_LEVEL.SYSTEM_BATTERY_STATE, null, 0, mem, size));
        SystemBatteryState batteryState = new SystemBatteryState(mem);
        if (batteryState.batteryPresent > 0) {
            if (batteryState.acOnLine == 0 && batteryState.charging == 0 && batteryState.discharging > 0) {
                assertTrue(batteryState.estimatedTime >= 0);
            }
            assertTrue(batteryState.remainingCapacity <= batteryState.maxCapacity);
        }
    }
}
