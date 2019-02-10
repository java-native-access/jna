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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

/**
 * Functions used with power management.
 */
public interface PowrProf extends Library {
    PowrProf INSTANCE = Native.load("PowrProf", PowrProf.class);

    /**
     * Indicates power level information.
     */
    public interface POWER_INFORMATION_LEVEL {
        int LAST_SLEEP_TIME = 15;
        int LAST_WAKE_TIME = 14;
        int PROCESSOR_INFORMATION = 11;
        int SYSTEM_BATTERY_STATE = 5;
        int SYSTEM_EXECUTION_STATE = 16;
        int SYSTEM_POWER_CAPABILITIES = 4;
        int SYSTEM_POWER_INFORMATION = 12;
        int SYSTEM_POWER_POLICY_AC = 0;
        int SYSTEM_POWER_POLICY_CURRENT = 8;
        int SYSTEM_POWER_POLICY_DC = 1;
        int SYSTEM_RESERVE_HIBER_FILE = 10;
    }

    /**
     * Contains information about the current state of the system battery.
     */
    @FieldOrder({ "AcOnLine", "BatteryPresent", "Charging", "Discharging", "Spare1", "Tag", "MaxCapacity",
            "RemainingCapacity", "Rate", "EstimatedTime", "DefaultAlert1", "DefaultAlert2" })
    class SystemBatteryState extends Structure {
        public byte AcOnLine;
        public byte BatteryPresent;
        public byte Charging;
        public byte Discharging;
        public byte[] Spare1 = new byte[3];
        public byte Tag;
        public int MaxCapacity;
        public int RemainingCapacity;
        public int Rate;
        public int EstimatedTime;
        public int DefaultAlert1;
        public int DefaultAlert2;

        public SystemBatteryState(Pointer p) {
            super(p);
            read();
        }

        public SystemBatteryState() {
            super();
        }
    }

    /**
     * Contains information about a processor.
     */
    @FieldOrder({ "Number", "MaxMhz", "CurrentMhz", "MhzLimit", "MaxIdleState", "CurrentIdleState" })
    class ProcessorPowerInformation extends Structure {
        public int Number;
        public int MaxMhz;
        public int CurrentMhz;
        public int MhzLimit;
        public int MaxIdleState;
        public int CurrentIdleState;

        public ProcessorPowerInformation(Pointer p) {
            super(p);
            read();
        }

        public ProcessorPowerInformation() {
            super();
        }
    }

    /**
     * Sets or retrieves power information.
     * <p>
     * Changes made to the current system power policy using
     * {@link #CallNtPowerInformation()} are immediate, but they are not
     * persistent; that is, the changes are not stored as part of a power
     * scheme. Any changes to system power policy made with
     * {@link #CallNtPowerInformation()} may be overwritten by changes to a
     * policy scheme made by the user in the Power Options control panel
     * program, or by subsequent calls to {@code WritePwrScheme},
     * {@code SetActivePwrScheme}, or other power scheme functions.
     * 
     * @param informationLevel
     *            The information level requested. This value indicates the
     *            specific power information to be set or retrieved. This
     *            parameter must be one of the following
     *            {@link POWER_INFORMATION_LEVEL} enumeration type values:
     *            {@link POWER_INFORMATION_LEVEL#LAST_SLEEP_TIME},
     *            {@link POWER_INFORMATION_LEVEL#LAST_WAKE_TIME},
     *            {@link POWER_INFORMATION_LEVEL#PROCESSOR_INFORMATION},
     *            {@link POWER_INFORMATION_LEVEL#SYSTEM_BATTERY_STATE},
     *            {@link POWER_INFORMATION_LEVEL#SYSTEM_EXECUTION_STATE},
     *            {@link POWER_INFORMATION_LEVEL#SYSTEM_POWER_CAPABILITIES},
     *            {@link POWER_INFORMATION_LEVEL#SYSTEM_POWER_INFORMATION},
     *            {@link POWER_INFORMATION_LEVEL#SYSTEM_POWER_POLICY_AC},
     *            {@link POWER_INFORMATION_LEVEL#SYSTEM_POWER_POLICY_CURRENT},
     *            {@link POWER_INFORMATION_LEVEL#SYSTEM_POWER_POLICY_DC}, or
     *            {@link POWER_INFORMATION_LEVEL#SYSTEM_RESERVE_HIBER_FILE}.
     * @param lpInputBuffer
     *            A pointer to an optional input buffer. The data type of this
     *            buffer depends on the information level requested in the
     *            {@code informationLevel} parameter.
     * @param nInputBufferSize
     *            The size of the input buffer, in bytes.
     * @param lpOutputBuffer
     *            A pointer to an optional output buffer. The data type of this
     *            buffer depends on the information level requested in the
     *            {@code informationLevel} parameter. If the buffer is too small
     *            to contain the information, the function returns
     *            {@link NTStatus#STATUS_BUFFER_TOO_SMALL}.
     * @param nOutputBufferSize
     *            The size of the output buffer, in bytes. Depending on the
     *            information level requested, this may be a variably sized
     *            buffer.
     * @return If the function succeeds, the return value is
     *         {@link NTStatus#STATUS_SUCCESS}. If the function fails, the
     *         return value can be one the following status codes:
     *         {@link NTStatus#STATUS_BUFFER_TOO_SMALL} if the output buffer is
     *         of insufficient size to contain the data to be returned.
     *         {@link NTStatus#STATUS_ACCESS_DENIED} if the caller had
     *         insufficient access rights to perform the requested action.
     */
    int CallNtPowerInformation(int informationLevel, Pointer lpInputBuffer, int nInputBufferSize,
            Pointer lpOutputBuffer, int nOutputBufferSize);
}

