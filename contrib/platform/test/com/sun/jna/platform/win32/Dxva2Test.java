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

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_DISPLAY_TECHNOLOGY_TYPE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE;
import com.sun.jna.platform.win32.LowLevelMonitorConfigurationAPI.MC_TIMING_REPORT;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.HMONITOR;


/**
 * @author Martin Steiger
 */
public class Dxva2Test {

	private int monitorCount;
	private PHYSICAL_MONITOR[] physMons;

    @Before
	public void setUp()
    {
        HMONITOR hMonitor = User32.INSTANCE.MonitorFromWindow(User32.INSTANCE.GetDesktopWindow(), WinUser.MONITOR_DEFAULTTOPRIMARY);

        DWORDByReference pdwNumberOfPhysicalMonitors = new DWORDByReference();
        assertTrue(Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, pdwNumberOfPhysicalMonitors).booleanValue());

        monitorCount = pdwNumberOfPhysicalMonitors.getValue().intValue();
        physMons = new PHYSICAL_MONITOR[monitorCount];
        
        assumeTrue(Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons).booleanValue());
    }

    @After
	public void tearDown()
    {
        Dxva2.INSTANCE.DestroyPhysicalMonitors(monitorCount, physMons);
    }

    @Test
    public void testGetMonitorTechnologyType()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        MC_DISPLAY_TECHNOLOGY_TYPE.ByReference techType = new MC_DISPLAY_TECHNOLOGY_TYPE.ByReference();
        Dxva2.INSTANCE.GetMonitorTechnologyType(hPhysicalMonitor, techType);
    }

    @Test
    public void testGetMonitorCapabilities()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        DWORDByReference temps = new DWORDByReference();
        DWORDByReference caps = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorCapabilities(hPhysicalMonitor, caps, temps);
    }

    @Test
    public void testGetMonitorBrightness()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        DWORDByReference pdwMinimumBrightness = new DWORDByReference();
        DWORDByReference pdwCurrentBrightness = new DWORDByReference();
        DWORDByReference pdwMaximumBrightness = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorBrightness(hPhysicalMonitor, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);
    }

    @Test
    public void testGetMonitorContrast()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        DWORDByReference pdwMinimumContrast = new DWORDByReference();
        DWORDByReference pdwCurrentContrast = new DWORDByReference();
        DWORDByReference pdwMaximumContrast = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorContrast(hPhysicalMonitor, pdwMinimumContrast, pdwCurrentContrast, pdwMaximumContrast);
    }

    @Test
    public void testGetMonitorColorTemperature()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        MC_COLOR_TEMPERATURE.ByReference pctCurrentColorTemperature = new MC_COLOR_TEMPERATURE.ByReference();
        Dxva2.INSTANCE.GetMonitorColorTemperature(hPhysicalMonitor, pctCurrentColorTemperature);
    }

    @Test
    public void testCapabilitiesRequestAndCapabilitiesReply()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        DWORDByReference pdwCapabilitiesStringLengthInCharacters = new DWORDByReference();
        BOOL success = Dxva2.INSTANCE.GetCapabilitiesStringLength(hPhysicalMonitor, pdwCapabilitiesStringLengthInCharacters);
        if(success.booleanValue()) {
            // VirtualBox is known to report an empty string
            DWORD capStrLen = pdwCapabilitiesStringLengthInCharacters.getValue();
            LPSTR pszASCIICapabilitiesString = new LPSTR(new Memory(capStrLen.intValue()));
            Dxva2.INSTANCE.CapabilitiesRequestAndCapabilitiesReply(hPhysicalMonitor, pszASCIICapabilitiesString, capStrLen);
        } else {
            System.err.println("GetCapabilitiesStringLength failed with errorcode: " + Kernel32.INSTANCE.GetLastError());
        }
    }

    @Test
    public void testGetMonitorDisplayAreaPosition()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        MC_POSITION_TYPE ptPositionType = MC_POSITION_TYPE.MC_HORIZONTAL_POSITION;
        DWORDByReference pdwMinimumPosition = new DWORDByReference();
        DWORDByReference pdwCurrentPosition = new DWORDByReference();
        DWORDByReference pdwMaximumPosition = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorDisplayAreaPosition(hPhysicalMonitor, ptPositionType, pdwMinimumPosition, pdwCurrentPosition, pdwMaximumPosition);
    }

    @Test
    public void testGetMonitorDisplayAreaSize()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        MC_SIZE_TYPE ptSizeType = MC_SIZE_TYPE.MC_WIDTH;
        DWORDByReference pdwMinimumSize = new DWORDByReference();
        DWORDByReference pdwCurrentSize = new DWORDByReference();
        DWORDByReference pdwMaximumSize = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorDisplayAreaSize(hPhysicalMonitor, ptSizeType, pdwMinimumSize, pdwCurrentSize, pdwMaximumSize);
    }

    @Test
    public void testGetMonitorRedGreenOrBlueGain()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        MC_GAIN_TYPE ptGainType = MC_GAIN_TYPE.MC_RED_GAIN;
        DWORDByReference pdwMinimumGain = new DWORDByReference();
        DWORDByReference pdwCurrentGain = new DWORDByReference();
        DWORDByReference pdwMaximumGain = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorRedGreenOrBlueGain(hPhysicalMonitor, ptGainType, pdwMinimumGain, pdwCurrentGain, pdwMaximumGain);
    }

    @Test
    public void testGetMonitorRedGreenOrBlueDrive()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        MC_DRIVE_TYPE ptDriveType = MC_DRIVE_TYPE.MC_RED_DRIVE;
        DWORDByReference pdwMinimumDrive = new DWORDByReference();
        DWORDByReference pdwCurrentDrive = new DWORDByReference();
        DWORDByReference pdwMaximumDrive = new DWORDByReference();
        Dxva2.INSTANCE.GetMonitorRedGreenOrBlueDrive(hPhysicalMonitor, ptDriveType, pdwMinimumDrive, pdwCurrentDrive, pdwMaximumDrive);
    }

    @Test
    public void testGetTimingReport()
    {
        HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;

        // the method returns FALSE if the monitor driver doesn't support it,
        // but verifies that the JNA mapping is correct (no exception)
        MC_TIMING_REPORT pmtrMonitorTimingReport = new MC_TIMING_REPORT();
        Dxva2.INSTANCE.GetTimingReport(hPhysicalMonitor, pmtrMonitorTimingReport);
    }
}
