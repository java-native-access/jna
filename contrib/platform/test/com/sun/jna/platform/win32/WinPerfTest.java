/* Copyright (c) 2018 Daniel Widdis, All Rights Reserved
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
// package com.sun.jna.platform.win32;
package com.sun.jna.platform.win32;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinPerf.PERF_COUNTER_BLOCK;
import com.sun.jna.platform.win32.WinPerf.PERF_COUNTER_DEFINITION;
import com.sun.jna.platform.win32.WinPerf.PERF_DATA_BLOCK;
import com.sun.jna.platform.win32.WinPerf.PERF_INSTANCE_DEFINITION;
import com.sun.jna.platform.win32.WinPerf.PERF_OBJECT_TYPE;
import com.sun.jna.ptr.IntByReference;

/**
 * Tests structures in WinPerf
 *
 * @author dbwiddis
 */
public class WinPerfTest extends AbstractWin32TestSupport {
    @Test
    public void testPerfDataBLock() {
        // This method tests:
        // PERF_DATA_BLOCK,
        // PERF_OBJECT_TYPE,
        // PERF_COUNTER_DEFINITION,
        // PERF_INSTANCE_DEFINITION,
        // PERF_COUNTER_BLOCK
        //
        // See Performance Data format at
        // https://msdn.microsoft.com/en-us/library/windows/desktop/aa373105(v=vs.85).aspx
        // [ ] Perf_Data_block
        // Then one or more objects repeating everything below:
        // [ ] Object Type
        // [ ][ ][ ] Multiple counter definitions
        // Then multiple instances repeating:
        // [ ] Instance Definition
        // [ ] Instance name
        // [ ] Counter Block
        // [ ][ ][ ] Counter data for each definition above

        if (AbstractWin32TestSupport.isEnglishLocale) {
            // To test the structures we'll get the data block for Process
            // performance data
            DWORDByReference index = new DWORDByReference();
            Pdh.INSTANCE.PdhLookupPerfIndexByName(null, "Process", index);
            int processIndex = index.getValue().intValue();
            String processIndexStr = Integer.toString(processIndex);
            // And one of the counters, for PID:
            Pdh.INSTANCE.PdhLookupPerfIndexByName(null, "ID Process", index);
            int idProcessIndex = index.getValue().intValue();

            // now load the Process registry entry
            // Sequentially increase the buffer until everything fits.
            int perfDataBufferSize = 4096;
            IntByReference lpcbData = new IntByReference(perfDataBufferSize);
            Pointer pPerfData = new Memory(perfDataBufferSize);
            while (WinError.ERROR_MORE_DATA == Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA,
                    processIndexStr, 0, null, pPerfData, lpcbData)) {
                perfDataBufferSize += 4096;
                lpcbData.setValue(perfDataBufferSize);
                pPerfData = new Memory(perfDataBufferSize);
            }
            // When this loop exits, pPerfData points to the PERF_DATA_BLOCK.

            // PERF_DATA_BLOCK test
            // Test the signature, a char[] containing [P, E, R, F]
            PERF_DATA_BLOCK perfData = new PERF_DATA_BLOCK(pPerfData);
            assertEquals("PERF", Native.toString(perfData.Signature));

            // PERF_OBJECT_TYPE test
            // Process object title index should match process object
            // Object type begins after header offset
            long perfObjectOffset = perfData.HeaderLength;
            PERF_OBJECT_TYPE perfObject = new PERF_OBJECT_TYPE(pPerfData.share(perfObjectOffset));
            assertEquals(processIndex, perfObject.ObjectNameTitleIndex);

            // PERF_COUNTER_DEFINITION test
            // Counter offsets will be at least 4, and a multiple of 4
            // Identify where counter definitions start
            long perfCounterOffset = perfObjectOffset + perfObject.HeaderLength;
            // Iterate through counter definitions to get the offset for the PID
            int idProcessOffset = 0;
            for (int counter = 0; counter < perfObject.NumCounters; counter++) {
                PERF_COUNTER_DEFINITION perfCounter = new PERF_COUNTER_DEFINITION(pPerfData.share(perfCounterOffset));
                assertTrue(perfCounter.CounterOffset >= 4);
                assertEquals(0, perfCounter.CounterOffset % 4);
                if (perfCounter.CounterNameTitleIndex == idProcessIndex) {
                    idProcessOffset = perfCounter.CounterOffset;
                }
                // Increment for next Counter
                perfCounterOffset += perfCounter.ByteLength;
            }

            // PERF_INSTANCE_DEFINITION test
            long perfInstanceOffset = perfObjectOffset + perfObject.DefinitionLength;
            Set<Integer> pidSet = new HashSet<>();
            for (int inst = 0; inst < perfObject.NumInstances; inst++) {
                PERF_INSTANCE_DEFINITION perfInstance = new PERF_INSTANCE_DEFINITION(
                        pPerfData.share(perfInstanceOffset));
                // Definitions align to 8 byte boundaries
                assertEquals(0, perfInstance.ByteLength % 8);

                // PERF_COUNTER_BLOCK test
                // ByteLength should be at least pid offset + 4,
                // and a multiple of 4
                long perfCounterBlockOffset = perfInstanceOffset + perfInstance.ByteLength;
                PERF_COUNTER_BLOCK perfCounterBlock = new PERF_COUNTER_BLOCK(pPerfData.share(perfCounterBlockOffset));
                assertTrue(perfCounterBlock.ByteLength >= 4 + idProcessOffset);
                assertEquals(0, perfCounterBlock.ByteLength % 4);
                int pid = pPerfData.getInt(perfCounterBlockOffset + idProcessOffset);
                // PID 0 exists as idle process and will repeat for the _Total
                // instance. All other PIDs should be unique
                assertTrue(pid >= 0);
                if (pid > 0) {
                    assertFalse(pidSet.contains(pid));
                    pidSet.add(pid);
                }
            }
        }
    }
}
