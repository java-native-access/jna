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

    int NUM_DISCHARGE_POLICIES = 4;

    /**
     * Enum which indicates the power information level requested from
     * {@link #CallNtPowerInformation()}. This value indicates the specific
     * power information to be set or retrieved.
     */
    public interface POWER_INFORMATION_LEVEL {
        /**
         * The {@code lpInBuffer} parameter must be {@code NULL}; otherwise, the
         * function returns {@link WinError#ERROR_INVALID_PARAMETER}.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a {@link WinDef#ULONGLONG}
         * that specifies the interrupt-time count, in 100-nanosecond units, at
         * the last system sleep time.
         */
        int LastSleepTime = 15;

        /**
         * The {@code lpInBuffer} parameter must be {@code NULL}; otherwise, the
         * function returns {@link WinError#ERROR_INVALID_PARAMETER}.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a {@link WinDef#ULONGLONG}
         * that specifies the interrupt-time count, in 100-nanosecond units, at
         * the last system wake time.
         */
        int LastWakeTime = 14;

        /**
         * The {@code lpInBuffer} parameter must be {@code NULL}; otherwise, the
         * function returns {@link WinError#ERROR_INVALID_PARAMETER}.
         * <p>
         * The {@code lpOutputBuffer} buffer receives one
         * {@link PROCESSOR_POWER_INFORMATION} structure for each processor that
         * is installed on the system. Use the {@link Kernel32#GetSystemInfo()}
         * function to retrieve the number of processors, which will be the
         * number of logical processors on the current Processor Group.
         */
        int ProcessorInformation = 11;

        /**
         * The {@code lpInBuffer} parameter must be {@code NULL}; otherwise, the
         * function returns {@link WinError#ERROR_INVALID_PARAMETER}.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a
         * {@link SYSTEM_BATTERY_STATE} structure containing information about
         * the current system battery.
         */
        int SystemBatteryState = 5;

        /**
         * The {@code lpInBuffer} parameter must be {@code NULL}; otherwise, the
         * function returns {@link WinError#ERROR_INVALID_PARAMETER}.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a {@link WinDef#ULONG}
         * value containing the system execution state buffer. This value may
         * contain any combination of the following values:
         * {@link WinBase#ES_SYSTEM_REQUIRED},
         * {@link WinBase#ES_DISPLAY_REQUIRED}, or
         * {@link WinBase#ES_USER_PRESENT}. For more information, see the
         * {@link {@link Kernel32#SetThreadExecutionState()} function.
         */
        int SystemExecutionState = 16;

        /**
         * The {@code lpInBuffer} parameter must be {@code NULL}; otherwise, the
         * function returns {@link WinError#ERROR_INVALID_PARAMETER}.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a
         * {@link SYSTEM_POWER_CAPABILITIES} structure containing the current
         * system power capabilities.
         * <p>
         * This information represents the currently supported power
         * capabilities. It may change as drivers are installed in the system.
         * For example, installation of legacy device drivers that do not
         * support power management disables all system sleep states.
         */
        int SystemPowerCapabilities = 4;

        /**
         * The {@code lpInBuffer} parameter must be {@code NULL}; otherwise, the
         * function returns {@link WinError#ERROR_INVALID_PARAMETER}.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a
         * {@link SYSTEM_POWER_INFORMATION} structure.
         * <p>
         * Applications can use this level to retrieve information about the
         * idleness of the system.
         */
        int SystemPowerInformation = 12;

        /**
         * If {@code lpInBuffer} is not {@code NULL}, the function applies the
         * {@link SYSTEM_POWER_POLICY} values passed in {@code lpInBuffer} to
         * the current system power policy used while the system is running on
         * AC (utility) power.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a
         * {@link SYSTEM_POWER_POLICY} structure containing the current system
         * power policy used while the system is running on AC (utility) power.
         */
        int SystemPowerPolicyAc = 0;

        /**
         * The {@code lpInBuffer} parameter must be {@code NULL}; otherwise, the
         * function returns {@link WinError#ERROR_INVALID_PARAMETER}.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a
         * {@link SYSTEM_POWER_POLICY} structure containing the current system
         * power policy used while the system is running on AC (utility) power.
         */
        int SystemPowerPolicyCurrent = 8;

        /**
         * If {@code lpInBuffer} is not {@code NULL}, the function applies the
         * {@link SYSTEM_POWER_POLICY} values passed in {@code lpInBuffer} to
         * the current system power policy used while the system is running on
         * battery power.
         * <p>
         * The {@code lpOutputBuffer} buffer receives a
         * {@link SYSTEM_POWER_POLICY} structure containing the current system
         * power policy used while the system is running on battery power.
         */
        int SystemPowerPolicyDc = 1;

        /**
         * If {@code lpInBuffer} is not {@code NULL} and the current user has
         * sufficient privileges, the function commits or decommits the storage
         * required to hold the hibernation image on the boot volume.
         * <p>
         * The lpInBuffer parameter must point to a {@code BOOLEAN} value
         * indicating the desired request. If the value is {@code TRUE}, the
         * hibernation file is reserved; if the value is {@code FALSE}, the
         * hibernation file is removed.
         */
        int SystemReserveHiberFile = 10;
    }

    /**
     * Contains information about the current state of the system battery.
     */
    @FieldOrder({ "AcOnLine", "BatteryPresent", "Charging", "Discharging", "Spare1", "Tag", "MaxCapacity",
            "RemainingCapacity", "Rate", "EstimatedTime", "DefaultAlert1", "DefaultAlert2" })
    class SYSTEM_BATTERY_STATE extends Structure {
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

        public SYSTEM_BATTERY_STATE(Pointer p) {
            super(p);
            read();
        }

        public SYSTEM_BATTERY_STATE() {
            super();
        }
    }

    /**
     * Contains information about a processor.
     */
    @FieldOrder({ "Number", "MaxMhz", "CurrentMhz", "MhzLimit", "MaxIdleState", "CurrentIdleState" })
    class PROCESSOR_POWER_INFORMATION extends Structure {
        public int Number;
        public int MaxMhz;
        public int CurrentMhz;
        public int MhzLimit;
        public int MaxIdleState;
        public int CurrentIdleState;

        public PROCESSOR_POWER_INFORMATION(Pointer p) {
            super(p);
            read();
        }

        public PROCESSOR_POWER_INFORMATION() {
            super();
        }
    }

    /**
     * Contains the granularity of the battery capacity.
     */
    @FieldOrder({ "Granularity", "Capacity" })
    class BATTERY_REPORTING_SCALE extends Structure {
        public int Granularity;
        public int Capacity;
    }

    /**
     * Contains information about the power capabilities of the system.
     */
    @FieldOrder({ "PowerButtonPresent", "SleepButtonPresent", "LidPresent", "SystemS1", "SystemS2", "SystemS3",
            "SystemS4", "SystemS5", "HiberFilePresent", "FullWake", "VideoDimPresent", "ApmPresent", "UpsPresent",
            "ThermalControl", "ProcessorThrottle", "ProcessorMinThrottle", "ProcessorMaxThrottle", "FastSystemS4",
            "Hiberboot", "WakeAlarmPresent", "AoAc", "DiskSpinDown", "HiberFileType", "AoAcConnectivitySupported",
            "spare3", "SystemBatteriesPresent", "BatteriesAreShortTerm", "BatteryScale", "AcOnLineWake", "SoftLidWake",
            "RtcWake", "MinDeviceWakeState", "DefaultLowLatencyWake" })
    class SYSTEM_POWER_CAPABILITIES extends Structure {
        // Misc supported system features
        public byte PowerButtonPresent;
        public byte SleepButtonPresent;
        public byte LidPresent;
        public byte SystemS1;
        public byte SystemS2;
        public byte SystemS3;
        public byte SystemS4; // hibernate
        public byte SystemS5; // off
        public byte HiberFilePresent;
        public byte FullWake;
        public byte VideoDimPresent;
        public byte ApmPresent;
        public byte UpsPresent;

        // Processors
        public byte ThermalControl;
        public byte ProcessorThrottle;
        public byte ProcessorMinThrottle;

        // Prior to WinXP, next 5 bytes are ProcessorThrottleScale
        // followed by 4 spare bytes
        public byte ProcessorMaxThrottle;
        public byte FastSystemS4;
        public byte Hiberboot;
        public byte WakeAlarmPresent;
        public byte AoAc;

        // Disk
        public byte DiskSpinDown;

        // HiberFile (Pre-Win10 next 2 bytes are spare)
        public byte HiberFileType;
        public byte AoAcConnectivitySupported;
        public byte[] spare3 = new byte[6];

        // System Battery
        public byte SystemBatteriesPresent;
        public byte BatteriesAreShortTerm;
        public BATTERY_REPORTING_SCALE[] BatteryScale = new BATTERY_REPORTING_SCALE[3];

        // Wake
        public int /* SYSTEM_POWER_STATE */ AcOnLineWake;
        public int /* SYSTEM_POWER_STATE */ SoftLidWake;
        public int /* SYSTEM_POWER_STATE */ RtcWake;
        public int /* SYSTEM_POWER_STATE */ MinDeviceWakeState;
        public int /* SYSTEM_POWER_STATE */ DefaultLowLatencyWake;

        public SYSTEM_POWER_CAPABILITIES(Pointer p) {
            super(p);
            read();
        }

        public SYSTEM_POWER_CAPABILITIES() {
            super();
        }
    }

    /**
     * Contains information about the idleness of the system.
     */
    @FieldOrder({ "MaxIdlenessAllowed", "Idleness", "TimeRemaining", "CoolingMode" })
    class SYSTEM_POWER_INFORMATION extends Structure {
        public int MaxIdlenessAllowed;
        public int Idleness;
        public int TimeRemaining;
        public byte CoolingMode;

        public SYSTEM_POWER_INFORMATION(Pointer p) {
            super(p);
            read();
        }

        public SYSTEM_POWER_INFORMATION() {
            super();
        }
    }

    /**
     * Contains information used to set the system power state.
     */
    @FieldOrder({ "Action", "Flags", "EventCode" })
    class POWER_ACTION_POLICY extends Structure {
        public int /* POWER_ACTION */ Action;
        public int Flags;
        public int EventCode;
    }

    /**
     * Contains information about system battery drain policy settings.
     */
    @FieldOrder({ "Enable", "Spare", "BatteryLevel", "PowerPolicy", "MinSystemState" })
    class SYSTEM_POWER_LEVEL extends Structure {
        public byte Enable;
        public byte[] Spare = new byte[3];
        public int BatteryLevel;
        public POWER_ACTION_POLICY PowerPolicy;
        public int /* SYSTEM_POWER_STATE */ MinSystemState;
    }

    /**
     * Contains information about the current system power policy.
     */
    @FieldOrder({ "Revision", "PowerButton", "SleepButton", "LidClose", "LidOpenWake", "Reserved", "Idle",
            "IdleTimeout", "IdleSensitivity", "DynamicThrottle", "Spare2", "MinSleep", "MaxSleep",
            "ReducedLatencySleep", "WinLogonFlags", "Spare3", "DozeS4Timeout", "BroadcastCapacityResolution",
            "DischargePolicy", "VideoTimeout", "VideoDimDisplay", "VideoReserved", "SpindownTimeout",
            "OptimizeForPower", "FanThrottleTolerance", "ForcedThrottle", "MinThrottle", "OverThrottled" })
    class SYSTEM_POWER_POLICY extends Structure {
        public int Revision;
        public POWER_ACTION_POLICY PowerButton;
        public POWER_ACTION_POLICY SleepButton;
        public POWER_ACTION_POLICY LidClose;
        public int /* SYSTEM_POWER_STATE */ LidOpenWake;
        public int Reserved;
        public POWER_ACTION_POLICY Idle;
        public int IdleTimeout;
        public byte IdleSensitivity;
        public byte DynamicThrottle;
        public byte[] Spare2 = new byte[2];
        public int /* SYSTEM_POWER_STATE */ MinSleep;
        public int /* SYSTEM_POWER_STATE */ MaxSleep;
        public int /* SYSTEM_POWER_STATE */ ReducedLatencySleep;
        public int WinLogonFlags;
        public int Spare3;
        public int DozeS4Timeout;
        public int BroadcastCapacityResolution;
        public SYSTEM_POWER_LEVEL[] DischargePolicy = new SYSTEM_POWER_LEVEL[NUM_DISCHARGE_POLICIES];
        public int VideoTimeout;
        public byte VideoDimDisplay;
        public int[] VideoReserved = new int[3];
        public int SpindownTimeout;
        public byte OptimizeForPower;
        public byte FanThrottleTolerance;
        public byte ForcedThrottle;
        public byte MinThrottle;
        public POWER_ACTION_POLICY OverThrottled;

        public SYSTEM_POWER_POLICY(Pointer p) {
            super(p);
            read();
        }

        public SYSTEM_POWER_POLICY() {
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
     *            {@link POWER_INFORMATION_LEVEL#LastSleepTime},
     *            {@link POWER_INFORMATION_LEVEL#LastWakeTime},
     *            {@link POWER_INFORMATION_LEVEL#ProcessorInformation},
     *            {@link POWER_INFORMATION_LEVEL#SystemBatteryState},
     *            {@link POWER_INFORMATION_LEVEL#SystemExecutionState},
     *            {@link POWER_INFORMATION_LEVEL#SystemPowerCapabilities},
     *            {@link POWER_INFORMATION_LEVEL#SystemPowerInformation},
     *            {@link POWER_INFORMATION_LEVEL#SystemPowerPolicyAc},
     *            {@link POWER_INFORMATION_LEVEL#SystemPowerPolicyCurrent},
     *            {@link POWER_INFORMATION_LEVEL#SystemPowerPolicyDc}, or
     *            {@link POWER_INFORMATION_LEVEL#SystemReserveHiberFile}.
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

