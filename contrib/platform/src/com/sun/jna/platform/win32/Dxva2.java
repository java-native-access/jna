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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.EnumConverter;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.win32.StdCallLibrary;

/**
 * A port of dxva2.dll
 * @author Martin Steiger
 */
public interface Dxva2 extends StdCallLibrary, PhysicalMonitorEnumerationAPI, HighLevelMonitorConfigurationAPI, LowLevelMonitorConfigurationAPI {
	Map<String, Object> DXVA_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() {
		private static final long serialVersionUID = -1987971664975780480L;

		{
			put(Library.OPTION_TYPE_MAPPER, new DefaultTypeMapper()
			{
				{
					addTypeConverter(MC_POSITION_TYPE.class, new EnumConverter<MC_POSITION_TYPE>(MC_POSITION_TYPE.class));
					addTypeConverter(MC_SIZE_TYPE.class, new EnumConverter<MC_SIZE_TYPE>(MC_SIZE_TYPE.class));
					addTypeConverter(MC_GAIN_TYPE.class, new EnumConverter<MC_GAIN_TYPE>(MC_GAIN_TYPE.class));
					addTypeConverter(MC_DRIVE_TYPE.class, new EnumConverter<MC_DRIVE_TYPE>(MC_DRIVE_TYPE.class));
				}
			});
		}
	});

    /**
     * The only instance of the library
     */
    Dxva2 INSTANCE = Native.loadLibrary("Dxva2", Dxva2.class, DXVA_OPTIONS);


    /******************************************************************************
        Monitor capability functions
    ******************************************************************************/
    /**
     * Retrieves the configuration capabilities of a monitor. Call this function to find out which high-level
     * monitor configuration functions are supported by the monitor.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call
     *        {@link #GetPhysicalMonitorsFromHMONITOR}
     * @param pdwMonitorCapabilities Receives a bitwise OR of capabilities flags. (MC_CAPS_*)
     * @param pdwSupportedColorTemperatures Receives a bitwise OR of color temperature flags.
     *        (MC_SUPPORTED_COLOR_TEMPERATURE_*)
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is
     *        FALSE. To get extended error information, call GetLastError.
     *
     *        <p>The function fails if the monitor does not support DDC/CI.</p>
     */
    BOOL GetMonitorCapabilities(HANDLE hMonitor, DWORDByReference pdwMonitorCapabilities, DWORDByReference pdwSupportedColorTemperatures);

    /******************************************************************************
        Monitor setting persistence functions
    ******************************************************************************/

    /**
     * Saves the current monitor settings to the display's nonvolatile storage.
     * <p>
     * This function takes about 200 milliseconds to return.
     * This high-level function is identical to the low-level function SaveCurrentSettings.</p>
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL SaveCurrentMonitorSettings(HANDLE hMonitor);

    /******************************************************************************
        Monitor meta-data functions
    ******************************************************************************/

    /**
     * Retrieves the type of technology used by a monitor.
     * This function does not support every display technology. If a monitor uses a display technology that is
     * supported by this function, the GetMonitorCapabilities function returns the MC_CAPS_DISPLAY_TECHNOLOGY_TYPE
     * flag. If that flag is absent, the GetMonitorTechnologyType function fails.
     * Some monitor technologies do not support certain monitor configuration functions. For example,
     * the DegaussMonitor function is supported only for cathode ray tube (CRT) monitors. To find out whether a
     * specific function is supported, call GetMonitorCapabilities.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param pdtyDisplayTechnologyType Receives the technology type as defined in {@link HighLevelMonitorConfigurationAPI.MC_DISPLAY_TECHNOLOGY_TYPE}.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL GetMonitorTechnologyType(HANDLE hMonitor, MC_DISPLAY_TECHNOLOGY_TYPE.ByReference pdtyDisplayTechnologyType);

    /******************************************************************************
        Monitor image calibration functions
    ******************************************************************************/

    /**
     * Retrieves a monitor's minimum, maximum, and current brightness settings.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_BRIGHTNESS flag.
     * This function takes about 40 milliseconds to return. The brightness setting is a continuous monitor setting.</p>
     * @param hMonitor Handle to a physical monitor
     * @param pdwMinimumBrightness Receives the monitor's minimum brightness.
     * @param pdwCurrentBrightness Receives the monitor's current brightness.
     * @param pdwMaximumBrightness Receives the monitor's maximum brightness.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL GetMonitorBrightness(HANDLE hMonitor, DWORDByReference pdwMinimumBrightness,
            DWORDByReference pdwCurrentBrightness, DWORDByReference pdwMaximumBrightness);

    /**
     * Retrieves a monitor's minimum, maximum, and current contrast settings.
     * @param hMonitor Handle to a physical monitor.
     * @param pdwMinimumContrast Receives the monitor's minimum contrast.
     * @param pdwCurrentContrast Receives the monitor's current contrast.
     * @param pdwMaximumContrast Receives the monitor's maximum contrast.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL GetMonitorContrast(HANDLE hMonitor, DWORDByReference pdwMinimumContrast, DWORDByReference pdwCurrentContrast,
            DWORDByReference pdwMaximumContrast);

    /**
     * Retrieves a monitor's current color temperature.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_COLOR_TEMPERATURE flag.
     * This function takes between 0 and 80 milliseconds to return.</p>
     * @param hMonitor Handle to a physical monitor.
     * @param pctCurrentColorTemperature Receives the monitor's current color temperature.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL GetMonitorColorTemperature(HANDLE hMonitor, MC_COLOR_TEMPERATURE.ByReference pctCurrentColorTemperature);

    /**
     * Retrieves a monitor's red, green, or blue drive value.
     * <p>
     * Drive settings are generally used to adjust the monitor's white point. Drive and black level are different
     * names for the same monitor setting. If this function is supported, the GetMonitorCapabilities function returns
     * the MC_CAPS_RED_GREEN_BLUE_DRIVE flag.</p>
     * @param hMonitor Handle to a physical monitor.
     * @param dtDriveType A member of the MC_DRIVE_TYPE enumeration, specifying whether to retrieve the red, green, or blue drive value.
     * @param pdwMinimumDrive Receives the minimum red, green, or blue drive value.
     * @param pdwCurrentDrive Receives the current red, green, or blue drive value.
     * @param pdwMaximumDrive Receives the maximum red, green, or blue drive value.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL GetMonitorRedGreenOrBlueDrive(HANDLE hMonitor, MC_DRIVE_TYPE dtDriveType, DWORDByReference pdwMinimumDrive,
            DWORDByReference pdwCurrentDrive, DWORDByReference pdwMaximumDrive);

    /**
     * Retrieves a monitor's red, green, or blue gain value.
     * <p>
     * Gain settings are generally used to adjust the monitor's white point. If this function is supported, the
     * GetMonitorCapabilities function returns the MC_CAPS_RED_GREEN_BLUE_GAIN flag. This function takes about 40 milliseconds to return.
     * The gain settings are continuous monitor settings.</p>
     * @param hMonitor Handle to a physical monitor.
     * @param gtGainType A member of the MC_GAIN_TYPE enumeration, specifying whether to retrieve the red, green, or blue gain value.
     * @param pdwMinimumGain Receives the minimum red, green, or blue gain value.
     * @param pdwCurrentGain Receives the current red, green, or blue gain value.
     * @param pdwMaximumGain Receives the maximum red, green, or blue gain value.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL GetMonitorRedGreenOrBlueGain(HANDLE hMonitor, MC_GAIN_TYPE gtGainType, DWORDByReference pdwMinimumGain,
            DWORDByReference pdwCurrentGain, DWORDByReference pdwMaximumGain);

    /**
     * Sets a monitor's brightness value.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_BRIGHTNESS flag.
     * This function takes about 50 milliseconds to return.
     * The brightness setting is a continuous monitor setting. </p>
     * @param hMonitor Handle to a physical monitor.
     * @param dwNewBrightness Brightness value. To get the monitor's minimum and maximum brightness values, call GetMonitorBrightness.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL SetMonitorBrightness(HANDLE hMonitor, int dwNewBrightness);

    /**
     * Sets a monitor's contrast value.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_CONTRAST flag.
     * This function takes about 50 milliseconds to return. The brightness setting is a continuous monitor setting.</p>
     * @param hMonitor Handle to a physical monitor.
     * @param dwNewContrast Contrast value. To get the monitor's minimum and maximum contrast values, call GetMonitorContrast.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL SetMonitorContrast(HANDLE hMonitor, int dwNewContrast);

    /**
     * Sets a monitor's color temperature.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_COLOR_TEMPERATURE flag.
     * The GetMonitorCapabilities function also returns the range of color temperatures that the monitor supports.
     * The ctCurrentColorTemperature parameter must correspond to one of these values. Changing the color temperature
     * changes the monitor's white point. It can also change the current drive and gain settings. To get the new drive
     * and gain settings, call GetMonitorRedGreenOrBlueDrive and GetMonitorRedGreenOrBlueGain, respectively.
     * This function takes from 50 to 90 milliseconds to return.</p>
     * @param hMonitor Handle to a physical monitor.
     * @param ctCurrentColorTemperature Color temperature, specified as a member of the MC_COLOR_TEMPERATURE enumeration.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL SetMonitorColorTemperature(HANDLE hMonitor, MC_COLOR_TEMPERATURE ctCurrentColorTemperature);

    /**
     * Sets a monitor's red, green, or blue drive value.
     * <p>
     * Drive settings are generally used to adjust the
     * monitor's white point. Drive and black level are different names for the same monitor setting. If this function
     * is supported, the GetMonitorCapabilities function returns the MC_CAPS_RED_GREEN_BLUE_DRIVE flag. This function
     * takes about 50 milliseconds to return. Changing the drive settings can change the color temperature. To get the
     * new color temperature, call GetMonitorColorTemperature. The drive settings are continuous monitor settings</p>
     * @param hMonitor Handle to a physical monitor.
     * @param dtDriveType A member of the MC_DRIVE_TYPE enumeration, specifying whether to set the red, green, or blue
     * drive value.
     * @param dwNewDrive Red, green, or blue drive value. To get the monitor's minimum and maximum drive values, call
     * GetMonitorRedGreenOrBlueDrive.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL SetMonitorRedGreenOrBlueDrive(HANDLE hMonitor, MC_DRIVE_TYPE dtDriveType, int dwNewDrive);

    /**
     * Sets a monitor's red, green, or blue gain value.
     * <p>
     * Gain settings are generally used to adjust the
     * monitor's white point. If this function is supported, the GetMonitorCapabilities function returns the
     * MC_CAPS_RED_GREEN_BLUE_GAIN flag. This function takes about 50 milliseconds to return. Changing the gain settings
     * can change the color temperature. To get the new color temperature, call GetMonitorColorTemperature. The gain
     * settings are continuous monitor settings.</p>
     * @param hMonitor Handle to a physical monitor.
     * @param gtGainType A member of the MC_GAIN_TYPE enumeration, specifying whether to set the red, green, or blue
     * gain.
     * @param dwNewGain Red, green, or blue gain value. To get the monitor's minimum and maximum gain values, call
     * GetMonitorRedGreenOrBlueGain.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL SetMonitorRedGreenOrBlueGain(HANDLE hMonitor, MC_GAIN_TYPE gtGainType, int dwNewGain);

    /**
     * Degausses a monitor.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_DEGAUSS flag. Degaussing
     * is supported only by cathode ray tube (CRT) monitors. This function takes about 50 milliseconds to return. This
     * function should not be called frequently, because calling it frequently will not noticeably improve the monitor's
     * image quality or color fidelity.</p>
     * @param hMonitor Handle to a physical monitor.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL DegaussMonitor(HANDLE hMonitor);

    /******************************************************************************
     * Monitor image size and position calibration functions
     ******************************************************************************/

    /**
     * Retrieves a monitor's minimum, maximum, and current width or height.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_DISPLAY_AREA_SIZE flag.
     * This function takes about 40 milliseconds to return. The width and height settings are continuous monitor settings. </p>
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param stSizeType A member of the MC_SIZE_TYPE enumeration, specifying whether to retrieve the width or the height.
     * @param pdwMinimumWidthOrHeight Receives the minimum width or height.
     * @param pdwCurrentWidthOrHeight Receives the current width or height.
     * @param pdwMaximumWidthOrHeight Receives the maximum width or height.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL GetMonitorDisplayAreaSize(HANDLE hMonitor, MC_SIZE_TYPE stSizeType, DWORDByReference pdwMinimumWidthOrHeight,
            DWORDByReference pdwCurrentWidthOrHeight, DWORDByReference pdwMaximumWidthOrHeight);

    /**
     * Retrieves a monitor's minimum, maximum, and current horizontal or vertical position.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_DISPLAY_AREA_POSITION flag.
     * This function takes about 40 milliseconds to return. The horizontal and vertical position are continuous monitor settings.</p>
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param ptPositionType A member of the MC_POSITION_TYPE enumeration, specifying whether to retrieve the horizontal position or the vertical position.
     * @param pdwMinimumPosition Receives the minimum horizontal or vertical position.
     * @param pdwCurrentPosition Receives the current horizontal or vertical position.
     * @param pdwMaximumPosition Receives the maximum horizontal or vertical position.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL GetMonitorDisplayAreaPosition(HANDLE hMonitor, MC_POSITION_TYPE ptPositionType,
            DWORDByReference pdwMinimumPosition, DWORDByReference pdwCurrentPosition,
            DWORDByReference pdwMaximumPosition);

    /**
     * Sets the width or height of a monitor's display area.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_DISPLAY_AREA_SIZE flag.
     * This function takes about 50 milliseconds to return. The width and height settings are continuous monitor settings.</p>
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param stSizeType A member of the MC_SIZE_TYPE enumeration, specifying whether to set the width or the height.
     * @param dwNewDisplayAreaWidthOrHeight Display area width or height. To get the minimum and maximum width and height,
     *        call GetMonitorDisplayAreaSize.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL SetMonitorDisplayAreaSize(HANDLE hMonitor, MC_SIZE_TYPE stSizeType, int dwNewDisplayAreaWidthOrHeight);

    /**
     * Sets the horizontal or vertical position of a monitor's display area.
     * <p>
     * If this function is supported, the GetMonitorCapabilities function returns the MC_CAPS_DISPLAY_AREA_POSITION flag.
     * This function takes about 50 milliseconds to return. The horizontal and vertical position are continuous monitor settings. </p>
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param ptPositionType A member of the MC_POSITION_TYPE enumeration, specifying whether to set the horizontal position or the vertical position.
     * @param dwNewPosition Horizontal or vertical position. To get the minimum and maximum position, call GetMonitorDisplayAreaPosition.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL SetMonitorDisplayAreaPosition(HANDLE hMonitor, MC_POSITION_TYPE ptPositionType, int dwNewPosition);

    /******************************************************************************
     * Restore to defaults functions
     ******************************************************************************/

    /**
     * Restores a monitor's color settings to their factory defaults. This function potentially changes the current
     * value of the monitor's brightness, contrast, color temperature, drive, and gain. The current value of each
     * setting is changed to its factory default. The default settings depend on the manufacturer. This function can
     * also change the range of supported values for each of these settings. The function does not enable any monitor
     * settings that were disabled. If this function is supported, the GetMonitorCapabilities function returns the
     * MC_CAPS_RESTORE_FACTORY_COLOR_DEFAULTS flag. This function takes about 5 seconds to return. This function might
     * reset monitor settings that are not accessible through the high-level monitor configuration functions. Whether
     * this occurs depends on the specific model of monitor. The following settings are not affected by this function:
     * <ul><li>Display area size</li>
     * <li>Display area position</li>
     * <li>Capabilities flags</li></ul>
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL RestoreMonitorFactoryColorDefaults(HANDLE hMonitor);

    /**
     * Restores a monitor's settings to their factory defaults. This function restores all of the settings that are
     * supported by the high-level monitor configuration functions. It might also restore settings that are available
     * only through the low-level functions and are not supported by the high-level functions. The current value of each
     * setting is changed to its factory default. The exact settings that change, and the default values of those
     * settings, depend on the manufacturer. This function can also change the range of supported values for some
     * settings. If this function is supported, the GetMonitorCapabilities function returns the
     * MC_CAPS_RESTORE_FACTORY_DEFAULTS flag. This function takes about 5 seconds to return. If GetMonitorCapabilities
     * returns the MC_RESTORE_FACTORY_DEFAULTS_ENABLES_MONITOR_SETTINGS flag, this function also enables all of the
     * monitor settings that are supported by the high-level functions. It is sometimes possible for an application to
     * disable certain settings by calling the low-level functions. It is also possible for the user to disable certain
     * settings by adjusting settings on the monitor's physical control panel. If that happens, the setting can only be
     * re-enabled through the control panel or by calling RestoreMonitorFactoryDefaults. It is not possible to disable
     * any settings by using the high-level functions.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL RestoreMonitorFactoryDefaults(HANDLE hMonitor);

    // LowLevelMonitorConfigurationAPI.h

    /**
     * Retrieves the current value, maximum value, and code type of a Virtual Control Panel (VCP) code for a monitor.
     * This function corresponds to the "Get VCP Feature &amp; VCP Feature Reply" command from the Display Data
     * Channel Command Interface (DDC/CI) standard. Vendor-specific VCP codes can be used with this function.
     * This function takes about 40 milliseconds to return.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param bVCPCode VCP code to query. The VCP codes are Include the VESA Monitor Control Command Set (MCCS)
     *        standard, versions 1.0 and 2.0. This parameter must specify a continuous or non-continuous VCP, or a
     *        vendor-specific code. It should not be a table control code.
     * @param pvct Receives the VCP code type, as a member of the MC_VCP_CODE_TYPE enumeration. This parameter can be NULL.
     * @param pdwCurrentValue Receives the current value of the VCP code. This parameter can be NULL.
     * @param pdwMaximumValue If bVCPCode specifies a continuous VCP code, this parameter receives the maximum value of
     *        the VCP code. If bVCPCode specifies a non-continuous VCP code, the value received in this parameter
     *        is undefined. This parameter can be NULL.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL GetVCPFeatureAndVCPFeatureReply(HANDLE hMonitor, BYTE bVCPCode, MC_VCP_CODE_TYPE.ByReference pvct,
            DWORDByReference pdwCurrentValue, DWORDByReference pdwMaximumValue);

    /**
     * Sets the value of a Virtual Control Panel (VCP) code for a monitor. This function corresponds to the
     * "Set VCP Feature" command from the Display Data Channel Command Interface (DDC/CI) standard. This function takes
     * about 50 milliseconds to return.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param bVCPCode VCP code to set. The VCP codes are defined in the VESA Monitor Control Command Set (MCCS)
     * standard, version 1.0 and 2.0. This parameter must specify a continuous or non-continuous VCP, or a
     * vendor-specific code. It should not be a table control code.
     * @param dwNewValue Value of the VCP code.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL SetVCPFeature(HANDLE hMonitor, BYTE bVCPCode, DWORD dwNewValue);

    /**
     * Saves the current monitor settings to the display's nonvolatile storage. This function corresponds to the
     * "Save Current Settings" function from the Display Data Channel Command Interface (DDC/CI) standard. This function
     * takes about 200 milliseconds to return. This low-level function is identical to the high-level function
     * SaveCurrentMonitorSettings.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL SaveCurrentSettings(HANDLE hMonitor);

    /**
     * Retrieves the length of a monitor's capabilities string.
     * This function usually returns quickly, but sometimes it can take several seconds to complete.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param pdwCapabilitiesStringLengthInCharacters Receives the length of the capabilities string, in characters, including the terminating null character.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL GetCapabilitiesStringLength(HANDLE hMonitor, DWORDByReference pdwCapabilitiesStringLengthInCharacters);

    /**
     * Retrieves a string describing a monitor's capabilities. This function corresponds to the
     * "Capabilities Request &amp; Capabilities Reply" command from the Display Data Channel Command Interface (DDC/CI)
     * standard. For more information about the capabilities string, refer to the DDC/CI standard. This function usually
     * returns quickly, but sometimes it can take several seconds to complete. You can update a monitor's capabilities
     * string by adding an AddReg directive to the monitor's INF file. Add a registry key named "CapabilitiesString" to
     * the monitor's driver key. The value of the registry key is the capabilities string. The registry data type is
     * REG_SZ.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param pszASCIICapabilitiesString Pointer to a buffer that receives the monitor's capabilities string. The caller
     *        must allocate this buffer. To get the size of the string, call GetCapabilitiesStringLength. The capabilities
     *        string is always an ASCII string. The buffer must include space for the terminating null character.
     * @param dwCapabilitiesStringLengthInCharacters Size of pszASCIICapabilitiesString in characters, including the
     *        terminating null character.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL CapabilitiesRequestAndCapabilitiesReply(HANDLE hMonitor, LPSTR pszASCIICapabilitiesString,
            DWORD dwCapabilitiesStringLengthInCharacters);

    /**
     * Retrieves a monitor's horizontal and vertical synchronization frequencies.
     * @param hMonitor Handle to a physical monitor. To get the monitor handle, call GetPhysicalMonitorsFromHMONITOR
     * @param pmtrMonitorTimingReport Pointer to an MC_TIMING_REPORT structure that receives the timing information.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE.
     */
    BOOL GetTimingReport(HANDLE hMonitor, MC_TIMING_REPORT pmtrMonitorTimingReport);

    // PhysicalMonitorEnumerationAPI.h

    /******************************************************************************
     * Physical Monitor Enumeration Functions
     ******************************************************************************/

    /**
     * Retrieves the number of physical monitors associated with an HMONITOR monitor handle. Call this function before
     * calling GetPhysicalMonitorsFromHMONITOR.
     * @param hMonitor A monitor handle. Monitor handles are returned by several Multiple Display Monitor functions,
     *        including EnumDisplayMonitors and MonitorFromWindow, which are part of the graphics device interface (GDI).
     * @param pdwNumberOfPhysicalMonitors Receives the number of physical monitors associated with the monitor handle.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL GetNumberOfPhysicalMonitorsFromHMONITOR(HMONITOR hMonitor, DWORDByReference pdwNumberOfPhysicalMonitors);

    // HRESULT GetNumberOfPhysicalMonitorsFromIDirect3DDevice9
    // (
    // IDirect3DDevice9* pDirect3DDevice9,
    // DWORDByReference pdwNumberOfPhysicalMonitors
    // );

    /**
     * Retrieves the physical monitors associated with an HMONITOR monitor handle. A single HMONITOR handle can be
     * associated with more than one physical monitor. This function returns a handle and a text description for each
     * physical monitor. When you are done using the monitor handles, close them by passing the pPhysicalMonitorArray
     * array to the DestroyPhysicalMonitors function.
     * @param hMonitor A monitor handle. Monitor handles are returned by several Multiple Display Monitor functions,
     *        including EnumDisplayMonitors and MonitorFromWindow, which are part of the graphics device interface (GDI).
     * @param dwPhysicalMonitorArraySize Number of elements in pPhysicalMonitorArray. To get the required size of the
     *        array, call GetNumberOfPhysicalMonitorsFromHMONITOR.
     * @param pPhysicalMonitorArray Pointer to an array of PHYSICAL_MONITOR structures. The caller must allocate the
     *        array.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL GetPhysicalMonitorsFromHMONITOR(HMONITOR hMonitor, int dwPhysicalMonitorArraySize,
            PHYSICAL_MONITOR[] pPhysicalMonitorArray);

    // HRESULT GetPhysicalMonitorsFromIDirect3DDevice9
    // (
    // __in IDirect3DDevice9* pDirect3DDevice9,
    // __in DWORD dwPhysicalMonitorArraySize,
    // __out_ecount(dwPhysicalMonitorArraySize) LPPHYSICAL_MONITOR pPhysicalMonitorArray
    // );

    /**
     * Closes a handle to a physical monitor.
     * Call this function to close a monitor handle obtained from the GetPhysicalMonitorsFromHMONITOR
     * @param hMonitor Handle to a physical monitor.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL DestroyPhysicalMonitor(HANDLE hMonitor);

    /**
     * Closes an array of physical monitor handles.
     * Call this function to close an array of monitor handles obtained from the GetPhysicalMonitorsFromHMONITOR
     * @param dwPhysicalMonitorArraySize Number of elements in the pPhysicalMonitorArray array.
     * @param pPhysicalMonitorArray Pointer to an array of PHYSICAL_MONITOR structures.
     * @return If the function succeeds, the return value is TRUE. If the function fails, the return value is FALSE
     */
    BOOL DestroyPhysicalMonitors(int dwPhysicalMonitorArraySize, PHYSICAL_MONITOR[] pPhysicalMonitorArray);
}
