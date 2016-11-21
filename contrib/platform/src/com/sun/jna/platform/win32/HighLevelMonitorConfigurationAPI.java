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

import com.sun.jna.platform.EnumUtils;

/**
 * A conversion of HighLevelMonitorConfigurationAPI.h
 * @author Martin Steiger
 */
public interface HighLevelMonitorConfigurationAPI
{
    /**
     * Monitor capabilities - retrieved by GetMonitorCapabilities
     */
    enum MC_CAPS implements FlagEnum
    {
        /**
         * The monitor does not support any monitor settings.
         */
        MC_CAPS_NONE                                             (0x00000000),

        /**
         * The monitor supports the GetMonitorTechnologyType function.
         */
        MC_CAPS_MONITOR_TECHNOLOGY_TYPE                          (0x00000001),

        /**
         * The monitor supports the GetMonitorBrightness and SetMonitorBrightness functions.
         */
        MC_CAPS_BRIGHTNESS                                       (0x00000002),

        /**
         * The monitor supports the GetMonitorContrast and SetMonitorContrast functions.
         */
        MC_CAPS_CONTRAST                                         (0x00000004),

        /**
         * The monitor supports the GetMonitorColorTemperature and SetMonitorColorTemperature functions.
         */
        MC_CAPS_COLOR_TEMPERATURE                                (0x00000008),

        /**
         * The monitor supports the GetMonitorRedGreenOrBlueGain and SetMonitorRedGreenOrBlueGain functions.
         */
        MC_CAPS_RED_GREEN_BLUE_GAIN                              (0x00000010),

        /**
         * The monitor supports the GetMonitorRedGreenOrBlueDrive and SetMonitorRedGreenOrBlueDrive functions.
         */
        MC_CAPS_RED_GREEN_BLUE_DRIVE                             (0x00000020),

        /**
         * The monitor supports the DegaussMonitor function.
         */
        MC_CAPS_DEGAUSS                                          (0x00000040),

        /**
         * The monitor supports the GetMonitorDisplayAreaPosition and SetMonitorDisplayAreaPosition functions.
         */
        MC_CAPS_DISPLAY_AREA_POSITION                            (0x00000080),

        /**
         * The monitor supports the GetMonitorDisplayAreaSize and SetMonitorDisplayAreaSize functions.
         */
        MC_CAPS_DISPLAY_AREA_SIZE                                (0x00000100),

        /**
         * The monitor supports the RestoreMonitorFactoryDefaults function.
         */
        MC_CAPS_RESTORE_FACTORY_DEFAULTS                         (0x00000400),

        /**
         * The monitor supports the RestoreMonitorFactoryColorDefaults function.
         */
        MC_CAPS_RESTORE_FACTORY_COLOR_DEFAULTS                   (0x00000800),

        /**
         * If this flag is present, calling the RestoreMonitorFactoryDefaults function enables all of 
         * the monitor settings used by the high-level monitor configuration functions. For more 
         * information, see the Remarks section in RestoreMonitorFactoryDefaults.
         */
        MC_RESTORE_FACTORY_DEFAULTS_ENABLES_MONITOR_SETTINGS     (0x00001000);         

        private int flag;

        MC_CAPS(int flag)
        {
            this.flag = flag;
        }

        @Override
        public int getFlag()
        {
            return flag;
        }
    }

    /**
     * Monitor capabilities - retrieved by GetMonitorCapabilities
     */
    enum MC_SUPPORTED_COLOR_TEMPERATURE implements FlagEnum
    {
        /**
         * No color temperatures are supported.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_NONE                      (0x00000000),

        /**
         * The monitor supports 4,000 kelvins (K) color temperature.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_4000K                     (0x00000001),

        /**
         * The monitor supports 5,000 K color temperature.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_5000K                     (0x00000002),

        /**
         * The monitor supports 6,500 K color temperature.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_6500K                     (0x00000004),

        /**
         * The monitor supports 7,500 K color temperature.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_7500K                     (0x00000008),

        /**
         * The monitor supports 8,200 K color temperature.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_8200K                     (0x00000010),

        /**
         * The monitor supports 9,300 K color temperature.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_9300K                     (0x00000020),

        /**
         * The monitor supports 10,000 K color temperature.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_10000K                    (0x00000040),

        /**
         * The monitor supports 11,500 K color temperature.
         */
        MC_SUPPORTED_COLOR_TEMPERATURE_11500K                    (0x00000080);

        private int flag;

        MC_SUPPORTED_COLOR_TEMPERATURE(int flag)
        {
            this.flag = flag;
        }

        @Override
        public int getFlag()
        {
            return flag;
        }
    }

    // ******************************************************************************
    //   Enumerations
    // ******************************************************************************

    /**
     * Identifies monitor display technologies.
     */
    public enum MC_DISPLAY_TECHNOLOGY_TYPE
    {
        /**
         * Shadow-mask cathode ray tube (CRT).
         */
        MC_SHADOW_MASK_CATHODE_RAY_TUBE,

        /**
         * Aperture-grill CRT.
         */
        MC_APERTURE_GRILL_CATHODE_RAY_TUBE,

        /**
         * Thin-film transistor (TFT) display.
         */
        MC_THIN_FILM_TRANSISTOR,

        /**
         * Liquid crystal on silicon (LCOS) display.
         */
        MC_LIQUID_CRYSTAL_ON_SILICON,

        /**
         * Plasma display.
         */
        MC_PLASMA,

        /**
         * Organic light emitting diode (LED) display.
         */
        MC_ORGANIC_LIGHT_EMITTING_DIODE,

        /**
         * Electroluminescent display.
         */
        MC_ELECTROLUMINESCENT,

        /**
         * Microelectromechanical display.
         */
        MC_MICROELECTROMECHANICAL,

        /**
         * Field emission device (FED) display.
         */
        MC_FIELD_EMISSION_DEVICE;

        /**
         * Defines a Reference to the enum
         */
        public static class ByReference extends com.sun.jna.ptr.ByReference {

            /**
             * Create an uninitialized reference
             */
            public ByReference() {
                super(4);
                getPointer().setInt(0, EnumUtils.UNINITIALIZED);
            }

            /**
             * Instantiates a new reference.
             * @param value the value
             */
            public ByReference(MC_DISPLAY_TECHNOLOGY_TYPE value) {
                super(4);
                setValue(value);
            }

            /**
             * Sets the value.
             * @param value the new value
             */
            public void setValue(MC_DISPLAY_TECHNOLOGY_TYPE value) {
                getPointer().setInt(0, EnumUtils.toInteger(value));
            }

            /**
             * Gets the value.
             * @return the value
             */
            public MC_DISPLAY_TECHNOLOGY_TYPE getValue() {
                return EnumUtils.fromInteger(getPointer().getInt(0), MC_DISPLAY_TECHNOLOGY_TYPE.class);
            }
        }
    }

    /**
     * Specifies whether to set or get a monitor's red, green, or blue drive.
     */
    public enum MC_DRIVE_TYPE
    {
        /**
         * Red drive
         */
        MC_RED_DRIVE,

        /**
         * Green drive
         */
        MC_GREEN_DRIVE,

        /**
         * Blue drive
         */
        MC_BLUE_DRIVE
    }

    /**
     * Specifies whether to get or set a monitor's red, green, or blue gain.
     */
    public enum MC_GAIN_TYPE
    {
        /**
         * Red gain
         */
        MC_RED_GAIN,

        /**
         * Green gain
         */
        MC_GREEN_GAIN,

        /**
         * Blue gain
         */
        MC_BLUE_GAIN
    }

    /**
     * Specifies whether to get or set the vertical or horizontal position of a monitor's display area.
     */
    public enum MC_POSITION_TYPE
    {
        /**
         * Horizontal position
         */
        MC_HORIZONTAL_POSITION,

        /**
         * Vertical position
         */
        MC_VERTICAL_POSITION 

    }

    /**
     * Specifies whether to get or set the width or height of a monitor's display area.
     */
    public enum MC_SIZE_TYPE
    {
        /**
         * Width
         */
        MC_WIDTH,

        /**
         * Height
         */
        MC_HEIGHT

    }

    /**
     * Describes a monitor's color temperature.
     */
    public enum MC_COLOR_TEMPERATURE
    {
        /**
         * Unknown temperature. 
         */
        MC_COLOR_TEMPERATURE_UNKNOWN,

        /**
         * 4,000 kelvins (K). 
         */
        MC_COLOR_TEMPERATURE_4000K, 

        /**
         * 5,000 kelvins (K). 
         */
        MC_COLOR_TEMPERATURE_5000K, 

        /**
         * 6,500 kelvins (K). 
         */
        MC_COLOR_TEMPERATURE_6500K, 

        /**
         * 7,500 kelvins (K). 
         */
        MC_COLOR_TEMPERATURE_7500K, 

        /**
         * 8,200 kelvins (K). 
         */
        MC_COLOR_TEMPERATURE_8200K, 

        /**
         * 9,300 kelvins (K). 
         */
        MC_COLOR_TEMPERATURE_9300K, 

        /**
         * 10,000 kelvins (K). 
         */
        MC_COLOR_TEMPERATURE_10000K,

        /**
         * 11,500 kelvins (K). 
         */
        MC_COLOR_TEMPERATURE_11500K;

        /**
         * Defines a Reference to the enum
         */
        public static class ByReference extends com.sun.jna.ptr.ByReference {

            /**
             * Create an uninitialized reference
             */
            public ByReference() {
                super(4);
                getPointer().setInt(0, EnumUtils.UNINITIALIZED);
            }

            /**
             * Instantiates a new reference.
             * @param value the value
             */
            public ByReference(MC_COLOR_TEMPERATURE value) {
                super(4);
                setValue(value);
            }

            /**
             * Sets the value.
             * @param value the new value
             */
            public void setValue(MC_COLOR_TEMPERATURE value) {
                getPointer().setInt(0, EnumUtils.toInteger(value));
            }

            /**
             * Gets the value.
             * @return the value
             */
            public MC_COLOR_TEMPERATURE getValue() {
                return EnumUtils.fromInteger(getPointer().getInt(0), MC_COLOR_TEMPERATURE.class);
            }
        }
    }
}
