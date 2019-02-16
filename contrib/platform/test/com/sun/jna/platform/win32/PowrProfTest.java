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
import com.sun.jna.platform.win32.PowrProf.PROCESSOR_POWER_INFORMATION;
import com.sun.jna.platform.win32.PowrProf.SYSTEM_BATTERY_STATE;
import com.sun.jna.platform.win32.PowrProf.SYSTEM_POWER_CAPABILITIES;
import com.sun.jna.platform.win32.PowrProf.SYSTEM_POWER_INFORMATION;
import com.sun.jna.platform.win32.PowrProf.SYSTEM_POWER_POLICY;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinNT.POWER_ACTION;
import com.sun.jna.platform.win32.WinNT.SYSTEM_POWER_STATE;

import junit.framework.TestCase;

public class PowrProfTest extends TestCase {

    public void testProcessorPowerInformation() {
        // MSDN docs for CallNTPowerInformation specify use of GetSystemInfo to
        // count logical processors for this InformationLevel. The GetSystemInfo
        // function and this function only count logical processors on the
        // current Processor Group, so the array will never have more than 64
        // elements
        SYSTEM_INFO info = new SYSTEM_INFO();
        Kernel32.INSTANCE.GetSystemInfo(info);
        int numProcs = info.dwNumberOfProcessors.intValue();

        // Try with too small buffer
        int bufferSize = 1;
        Memory mem = new Memory(bufferSize);
        assertEquals(NTStatus.STATUS_BUFFER_TOO_SMALL, PowrProf.INSTANCE
                .CallNtPowerInformation(POWER_INFORMATION_LEVEL.ProcessorInformation, null, 0, mem, bufferSize));

        PROCESSOR_POWER_INFORMATION ppi = new PROCESSOR_POWER_INFORMATION();
        bufferSize = ppi.size() * numProcs;
        mem = new Memory(bufferSize);
        assertEquals(NTStatus.STATUS_SUCCESS, PowrProf.INSTANCE
                .CallNtPowerInformation(POWER_INFORMATION_LEVEL.ProcessorInformation, null, 0, mem, bufferSize));

        long[] freqs = new long[numProcs];
        for (int i = 0; i < freqs.length; i++) {
            ppi = new PROCESSOR_POWER_INFORMATION(mem.share(i * (long) ppi.size()));
            assertTrue(ppi.CurrentMhz <= ppi.MaxMhz);
        }
    }

    public void testSystemBatteryStateAndPowerCapabilities() {
        int size = new SYSTEM_BATTERY_STATE().size();
        Memory mem = new Memory(size);
        assertEquals(NTStatus.STATUS_SUCCESS, PowrProf.INSTANCE
                .CallNtPowerInformation(POWER_INFORMATION_LEVEL.SystemBatteryState, null, 0, mem, size));
        SYSTEM_BATTERY_STATE batteryState = new SYSTEM_BATTERY_STATE(mem);
        if (batteryState.BatteryPresent > 0) {
            if (batteryState.AcOnLine == 0 && batteryState.Charging == 0 && batteryState.Discharging > 0) {
                assertTrue(batteryState.EstimatedTime >= 0);
            }
            assertTrue(batteryState.RemainingCapacity <= batteryState.MaxCapacity);
        }

        size = new SYSTEM_POWER_CAPABILITIES().size();
        mem = new Memory(size);
        assertEquals(NTStatus.STATUS_SUCCESS, PowrProf.INSTANCE
                .CallNtPowerInformation(POWER_INFORMATION_LEVEL.SystemPowerCapabilities, null, 0, mem, size));
        SYSTEM_POWER_CAPABILITIES powerCapabilities = new SYSTEM_POWER_CAPABILITIES(mem);
        assertEquals(powerCapabilities.SystemBatteriesPresent > 0, batteryState.BatteryPresent > 0);
    }

    public void testSystemPowerInformation() {
        int size = new SYSTEM_POWER_INFORMATION().size();
        Memory mem = new Memory(size);
        assertEquals(NTStatus.STATUS_SUCCESS, PowrProf.INSTANCE
                .CallNtPowerInformation(POWER_INFORMATION_LEVEL.SystemPowerInformation, null, 0, mem, size));
        SYSTEM_POWER_INFORMATION powerInfo = new SYSTEM_POWER_INFORMATION(mem);
        assertTrue(powerInfo.MaxIdlenessAllowed <= 100);
        assertTrue(powerInfo.Idleness <= 100);
        assertTrue(powerInfo.CoolingMode >= 0); // must be 0,1,2
        assertTrue(powerInfo.CoolingMode <= 2);
    }

    public void testSystemPowerPolicy() {
        int size = new SYSTEM_POWER_POLICY().size();
        Memory mem = new Memory(size);
        assertEquals(NTStatus.STATUS_SUCCESS, PowrProf.INSTANCE
                .CallNtPowerInformation(POWER_INFORMATION_LEVEL.SystemPowerPolicyCurrent, null, 0, mem, size));
        SYSTEM_POWER_POLICY powerPolicy = new SYSTEM_POWER_POLICY(mem);

        // Test selected elements including nested structures
        assertTrue(powerPolicy.PowerButton.Action >= 0);
        assertTrue(powerPolicy.PowerButton.Action <= POWER_ACTION.PowerActionDisplayOff); // Max
        assertTrue(powerPolicy.MinSleep <= powerPolicy.MaxSleep);
        assertTrue(powerPolicy.MaxSleep <= SYSTEM_POWER_STATE.PowerSystemMaximum);
        assertTrue(powerPolicy.DischargePolicy[0].BatteryLevel <= 100); // percentage
    }
}
