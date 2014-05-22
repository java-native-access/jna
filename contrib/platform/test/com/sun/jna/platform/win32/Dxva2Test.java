/*
 * Copyright 2014 Martin Steiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.jna.platform.win32;


import junit.framework.TestCase;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_DISPLAY_TECHNOLOGY_TYPE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE;
import com.sun.jna.platform.win32.LowLevelMonitorConfigurationAPI.MC_TIMING_REPORT;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;


/**
 * @author Martin Steiger
 */
public class Dxva2Test extends TestCase {

    public void testAll() {
        User32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

            @Override
            public int apply(HMONITOR hMonitor, HDC hdc, RECT rect, LPARAM lparam)
            {
                enumerate(hMonitor);
                return 0;           // stop 
            }
        }, new LPARAM(0));
    }

    static void enumerate(HMONITOR hMonitor)
    {
        DWORDByReference pdwNumberOfPhysicalMonitors = new DWORDByReference();
        Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, pdwNumberOfPhysicalMonitors);
        int monitorCount = pdwNumberOfPhysicalMonitors.getValue().intValue();

        PHYSICAL_MONITOR[] physMons = new PHYSICAL_MONITOR[monitorCount];
        Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons);

        for (int i = 0; i < monitorCount; i++)
        {
            HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;
            enumeratePhysicalMonitor(hPhysicalMonitor);
        }
    }

    private static void enumeratePhysicalMonitor(HANDLE hPhysicalMonitor)
    {
        MC_DISPLAY_TECHNOLOGY_TYPE.ByReference techType = new MC_DISPLAY_TECHNOLOGY_TYPE.ByReference();
        Dxva2.INSTANCE.GetMonitorTechnologyType(hPhysicalMonitor, techType);

        DWORDByReference temps = new DWORDByReference();
        DWORDByReference caps = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorCapabilities(hPhysicalMonitor, caps, temps);

        // Brightness
        DWORDByReference pdwMinimumBrightness = new DWORDByReference();
        DWORDByReference pdwCurrentBrightness = new DWORDByReference();
        DWORDByReference pdwMaximumBrightness = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorBrightness(hPhysicalMonitor, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);

        // Contrast
        DWORDByReference pdwMinimumContrast = new DWORDByReference();
        DWORDByReference pdwCurrentContrast = new DWORDByReference();
        DWORDByReference pdwMaximumContrast = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorContrast(hPhysicalMonitor, pdwMinimumContrast, pdwCurrentContrast, pdwMaximumContrast);

        // Temperature
        MC_COLOR_TEMPERATURE.ByReference pctCurrentColorTemperature = new MC_COLOR_TEMPERATURE.ByReference();
        Dxva2.INSTANCE.GetMonitorColorTemperature(hPhysicalMonitor, pctCurrentColorTemperature);

        // Capabilities string
        DWORDByReference pdwCapabilitiesStringLengthInCharacters = new DWORDByReference();
        Dxva2.INSTANCE.GetCapabilitiesStringLength(hPhysicalMonitor, pdwCapabilitiesStringLengthInCharacters);
        DWORD capStrLen = pdwCapabilitiesStringLengthInCharacters.getValue();

        LPSTR pszASCIICapabilitiesString = new LPSTR(new Memory(capStrLen.intValue()));
        Dxva2.INSTANCE.CapabilitiesRequestAndCapabilitiesReply(hPhysicalMonitor, pszASCIICapabilitiesString, capStrLen);

        // Position
        MC_POSITION_TYPE ptPositionType = MC_POSITION_TYPE.MC_HORIZONTAL_POSITION;
        DWORDByReference pdwMinimumPosition = new DWORDByReference();
        DWORDByReference pdwCurrentPosition = new DWORDByReference();
        DWORDByReference pdwMaximumPosition = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorDisplayAreaPosition(hPhysicalMonitor, ptPositionType, pdwMinimumPosition, pdwCurrentPosition, pdwMaximumPosition);

        // Size
        MC_SIZE_TYPE ptSizeType = MC_SIZE_TYPE.MC_WIDTH;
        DWORDByReference pdwMinimumSize = new DWORDByReference();
        DWORDByReference pdwCurrentSize = new DWORDByReference();
        DWORDByReference pdwMaximumSize = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorDisplayAreaSize(hPhysicalMonitor, ptSizeType, pdwMinimumSize, pdwCurrentSize, pdwMaximumSize);

        // Gain
        MC_GAIN_TYPE ptGainType = MC_GAIN_TYPE.MC_RED_GAIN;
        DWORDByReference pdwMinimumGain = new DWORDByReference();
        DWORDByReference pdwCurrentGain = new DWORDByReference();
        DWORDByReference pdwMaximumGain = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorRedGreenOrBlueGain(hPhysicalMonitor, ptGainType, pdwMinimumGain, pdwCurrentGain, pdwMaximumGain);

        // Drive
        MC_DRIVE_TYPE ptDriveType = MC_DRIVE_TYPE.MC_RED_DRIVE;
        DWORDByReference pdwMinimumDrive = new DWORDByReference();
        DWORDByReference pdwCurrentDrive = new DWORDByReference();
        DWORDByReference pdwMaximumDrive = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorRedGreenOrBlueDrive(hPhysicalMonitor, ptDriveType, pdwMinimumDrive, pdwCurrentDrive, pdwMaximumDrive);

        // Timing Report
        MC_TIMING_REPORT pmtrMonitorTimingReport = new MC_TIMING_REPORT();
        Dxva2.INSTANCE.GetTimingReport(hPhysicalMonitor, pmtrMonitorTimingReport);
    }
}
