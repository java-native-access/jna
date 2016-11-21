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

package com.sun.jna.contrib.demo;

import com.sun.jna.Memory;
import com.sun.jna.platform.EnumUtils;
import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI;
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
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;
import com.sun.jna.platform.win32.WinUser.MONITORINFOEX;

/**
 * A small demo that tests the Win32 monitor API.
 * All available physical and virtual monitors are enumerated and
 * their capabilities printed to stdout
 * @author Martin Steiger
 */
public class MonitorInfoDemo
{
	/**
	 * @param args (ignored)
	 */
	public static void main(String[] args)
	{
		System.out.println("Installed Physical Monitors: " + User32.INSTANCE.GetSystemMetrics(WinUser.SM_CMONITORS));
		
		User32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

			@Override
			public int apply(HMONITOR hMonitor, HDC hdc, RECT rect, LPARAM lparam)
			{
				enumerate(hMonitor);

				return 1;
			}
			
		}, new LPARAM(0));
	}

	static void enumerate(HMONITOR hMonitor)
	{
		System.out.println("Found HMONITOR: " + hMonitor.getPointer().toString());

		MONITORINFOEX info = new MONITORINFOEX();
		User32.INSTANCE.GetMonitorInfo(hMonitor, info);
		System.out.println("Screen " + info.rcMonitor);
		System.out.println("Work area " + info.rcWork);
		boolean isPrimary = (info.dwFlags & WinUser.MONITORINFOF_PRIMARY) != 0;
		System.out.println("Primary? " + (isPrimary ? "yes" : "no"));
		System.out.println("Device " + new String(info.szDevice));
		
		DWORDByReference pdwNumberOfPhysicalMonitors = new DWORDByReference();
		Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, pdwNumberOfPhysicalMonitors);
		int monitorCount = pdwNumberOfPhysicalMonitors.getValue().intValue();
		
		System.out.println("HMONITOR is linked to " + monitorCount + " physical monitors");
		
		PHYSICAL_MONITOR[] physMons = new PHYSICAL_MONITOR[monitorCount];
		Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons);
		
		for (int i = 0; i < monitorCount; i++)
		{
			HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;
			System.out.println("Monitor " + i + " - " + new String(physMons[i].szPhysicalMonitorDescription));
		
			enumeratePhysicalMonitor(hPhysicalMonitor);
		}
		
		Dxva2.INSTANCE.DestroyPhysicalMonitors(monitorCount, physMons);
	}

	/**
	 * @param hPhysicalMonitor
	 */
	private static void enumeratePhysicalMonitor(HANDLE hPhysicalMonitor)
	{
		MC_DISPLAY_TECHNOLOGY_TYPE.ByReference techType = new MC_DISPLAY_TECHNOLOGY_TYPE.ByReference();
		Dxva2.INSTANCE.GetMonitorTechnologyType(hPhysicalMonitor, techType);
		System.out.println("TECHTYPE: " + techType.getValue());
	
		DWORDByReference temps = new DWORDByReference();
		DWORDByReference caps = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorCapabilities(hPhysicalMonitor, caps, temps);
		System.out.println("CAPS " + EnumUtils.setFromInteger(caps.getValue().intValue(), HighLevelMonitorConfigurationAPI.MC_CAPS.class));
		System.out.println("Temps " + temps.getValue());
		
		// Brightness
		DWORDByReference pdwMinimumBrightness = new DWORDByReference();
		DWORDByReference pdwCurrentBrightness = new DWORDByReference();
		DWORDByReference pdwMaximumBrightness = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorBrightness(hPhysicalMonitor, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);
		
		System.out.println("Brightness Min: " + pdwMinimumBrightness.getValue());
		System.out.println("Brightness Current: " + pdwCurrentBrightness.getValue());
		System.out.println("Brightness Max: " + pdwMaximumBrightness.getValue());

		// Contrast
		DWORDByReference pdwMinimumContrast = new DWORDByReference();
		DWORDByReference pdwCurrentContrast = new DWORDByReference();
		DWORDByReference pdwMaximumContrast = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorContrast(hPhysicalMonitor, pdwMinimumContrast, pdwCurrentContrast, pdwMaximumContrast);
		
		System.out.println("Contrast Min: " + pdwMinimumContrast.getValue());
		System.out.println("Contrast Current: " + pdwCurrentContrast.getValue());
		System.out.println("Contrast Max: " + pdwMaximumContrast.getValue());
	
		// Temperature
		MC_COLOR_TEMPERATURE.ByReference pctCurrentColorTemperature = new MC_COLOR_TEMPERATURE.ByReference();
		Dxva2.INSTANCE.GetMonitorColorTemperature(hPhysicalMonitor, pctCurrentColorTemperature);
		System.out.println("Current Temp: " + pctCurrentColorTemperature.getValue());

		// Capabilities string
		DWORDByReference pdwCapabilitiesStringLengthInCharacters = new DWORDByReference();
		Dxva2.INSTANCE.GetCapabilitiesStringLength(hPhysicalMonitor, pdwCapabilitiesStringLengthInCharacters);
		DWORD capStrLen = pdwCapabilitiesStringLengthInCharacters.getValue();
		
		LPSTR pszASCIICapabilitiesString = new LPSTR(new Memory(capStrLen.intValue()));
		Dxva2.INSTANCE.CapabilitiesRequestAndCapabilitiesReply(hPhysicalMonitor, pszASCIICapabilitiesString, capStrLen);
		System.out.println("Cap-String:" + new String(pszASCIICapabilitiesString.getPointer().getString(0)));

		// Position
		MC_POSITION_TYPE ptPositionType = MC_POSITION_TYPE.MC_HORIZONTAL_POSITION;
		DWORDByReference pdwMinimumPosition = new DWORDByReference();
		DWORDByReference pdwCurrentPosition = new DWORDByReference();
		DWORDByReference pdwMaximumPosition = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorDisplayAreaPosition(hPhysicalMonitor, ptPositionType, pdwMinimumPosition, pdwCurrentPosition, pdwMaximumPosition);

		System.out.println("Position (horz) Min: " + pdwMinimumPosition.getValue());
		System.out.println("Position (horz) Current: " + pdwCurrentPosition.getValue());
		System.out.println("Position (horz) Max: " + pdwMaximumPosition.getValue());
		
		// Size
		MC_SIZE_TYPE ptSizeType = MC_SIZE_TYPE.MC_WIDTH;
		DWORDByReference pdwMinimumSize = new DWORDByReference();
		DWORDByReference pdwCurrentSize = new DWORDByReference();
		DWORDByReference pdwMaximumSize = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorDisplayAreaSize(hPhysicalMonitor, ptSizeType, pdwMinimumSize, pdwCurrentSize, pdwMaximumSize);

		System.out.println("Width Min: " + pdwMinimumSize.getValue());
		System.out.println("Width Current: " + pdwCurrentSize.getValue());
		System.out.println("Width Max: " + pdwMaximumSize.getValue());
		
		// Gain
		MC_GAIN_TYPE ptGainType = MC_GAIN_TYPE.MC_RED_GAIN;
		DWORDByReference pdwMinimumGain = new DWORDByReference();
		DWORDByReference pdwCurrentGain = new DWORDByReference();
		DWORDByReference pdwMaximumGain = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorRedGreenOrBlueGain(hPhysicalMonitor, ptGainType, pdwMinimumGain, pdwCurrentGain, pdwMaximumGain);

		System.out.println("Red Gain Min: " + pdwMinimumSize.getValue());
		System.out.println("Red Gain Current: " + pdwCurrentSize.getValue());
		System.out.println("Red Gain Max: " + pdwMaximumSize.getValue());
		
		// Drive
		MC_DRIVE_TYPE ptDriveType = MC_DRIVE_TYPE.MC_RED_DRIVE;
		DWORDByReference pdwMinimumDrive = new DWORDByReference();
		DWORDByReference pdwCurrentDrive = new DWORDByReference();
		DWORDByReference pdwMaximumDrive = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorRedGreenOrBlueDrive(hPhysicalMonitor, ptDriveType, pdwMinimumDrive, pdwCurrentDrive, pdwMaximumDrive);

		System.out.println("Red Drive Min: " + pdwMinimumSize.getValue());
		System.out.println("Red Drive Current: " + pdwCurrentSize.getValue());
		System.out.println("Red Drive Max: " + pdwMaximumSize.getValue());

		// Timing Report
		MC_TIMING_REPORT pmtrMonitorTimingReport = new MC_TIMING_REPORT();
		Dxva2.INSTANCE.GetTimingReport(hPhysicalMonitor, pmtrMonitorTimingReport);
		System.out.println("HorizontalFrequencyInHZ " + pmtrMonitorTimingReport.dwHorizontalFrequencyInHZ);
		System.out.println("VerticalFrequencyInHZ " + pmtrMonitorTimingReport.dwVerticalFrequencyInHZ);
		
		System.out.println("--------------------------------------");
	}
	
}
