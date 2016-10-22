/*
 * Copyright 2014 Martin Steiger
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

import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.EnumUtils;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.DWORD;

/**
 * Conversion of LowLevelMonitorConfigurationAPI.h
 * @author Martin Steiger
 */
public interface LowLevelMonitorConfigurationAPI
{
    /**
     * Contains information from a monitor's timing report.
     */
    class MC_TIMING_REPORT extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwHorizontalFrequencyInHZ", "dwVerticalFrequencyInHZ", "bTimingStatusByte");
        /**
         * The monitor's horizontal synchronization frequency in Hz.
         */
        public DWORD dwHorizontalFrequencyInHZ;

        /**
         * The monitor's vertical synchronization frequency in Hz.
         */
        public DWORD dwVerticalFrequencyInHZ;

        /**
         * Timing status byte. For more information about this value, see the Display Data Channel Command
         * Interface (DDC/CI) standard.
         */
        public BYTE bTimingStatusByte;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Describes a Virtual Control Panel (VCP) code type.
     */
    enum MC_VCP_CODE_TYPE
    {
        /**
         * Momentary VCP code. Sending a command of this type causes the monitor to initiate a self-timed
         * operation and then revert to its original state. Examples include display tests and degaussing.
         */
        MC_MOMENTARY,

        /**
         * Set Parameter VCP code. Sending a command of this type changes some aspect of the monitor's operation.
         */
        MC_SET_PARAMETER;

        /**
         * Defines a Reference to the enum
         */
        public static class ByReference extends com.sun.jna.ptr.ByReference {

            /**
             * Create an uninitialized reference
             */
            public ByReference() {
                super(4);
            }

            /**
             * Instantiates a new reference.
             * @param value the value
             */
            public ByReference(MC_VCP_CODE_TYPE value) {
                super(4);
                setValue(value);
            }

            /**
             * Sets the value.
             * @param value the new value
             */
            public void setValue(MC_VCP_CODE_TYPE value) {
                getPointer().setInt(0, EnumUtils.toInteger(value));
            }

            /**
             * Gets the value.
             * @return the value
             */
            public MC_VCP_CODE_TYPE getValue() {
                return EnumUtils.fromInteger(getPointer().getInt(0), MC_VCP_CODE_TYPE.class);
            }
        }
    }
}

