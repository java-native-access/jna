package com.sun.jna.platform.win32;

import static com.sun.jna.Native.getLastError;
import static com.sun.jna.platform.win32.SetupApi.DICS_FLAG_GLOBAL;
import static com.sun.jna.platform.win32.SetupApi.DIGCF_DEVICEINTERFACE;
import static com.sun.jna.platform.win32.SetupApi.DIGCF_PRESENT;
import static com.sun.jna.platform.win32.SetupApi.DIREG_DEV;
import static com.sun.jna.platform.win32.SetupApi.GUID_DEVINTERFACE_COMPORT;
import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;
import static com.sun.jna.platform.win32.WinError.ERROR_NO_MORE_ITEMS;
import static com.sun.jna.platform.win32.WinNT.KEY_QUERY_VALUE;
import junit.framework.TestCase;

import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class SetupApiTest extends TestCase {
	
	private final SetupApi setupApi=SetupApi.INSTANCE;
	private final Advapi32 advapi32= Advapi32.INSTANCE;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(SetupApiTest.class);
	}
	
	
	
	public void testSetupDiOpenDevRegKey() throws Exception {
		
		HANDLE hDevInfoSet = setupApi.SetupDiGetClassDevs(GUID_DEVINTERFACE_COMPORT, null, null, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);
		assertTrue("For this test we need a valid HANDLE!",hDevInfoSet != INVALID_HANDLE_VALUE);
		
		SP_DEVINFO_DATA devInfo = new SP_DEVINFO_DATA();
		boolean succeed = setupApi.SetupDiEnumDeviceInfo(hDevInfoSet, 0, devInfo);
		assertTrue("For this test we need at least one com-port device",succeed);
		
		HKEY hDeviceKey = setupApi.SetupDiOpenDevRegKey(hDevInfoSet, devInfo, DICS_FLAG_GLOBAL, 0, DIREG_DEV, KEY_QUERY_VALUE);
		if (hDeviceKey != INVALID_HANDLE_VALUE)
			advapi32.RegCloseKey(hDeviceKey);


	}
	
	public void testSetupDiEnumDeviceInfo(){
		HANDLE hDevInfoSet = setupApi.SetupDiGetClassDevs(GUID_DEVINTERFACE_COMPORT, null, null, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);
		assertTrue("For this test we need a valid HANDLE!",hDevInfoSet != INVALID_HANDLE_VALUE);
		
		SP_DEVINFO_DATA devInfo = new SP_DEVINFO_DATA();
		boolean succeed = setupApi.SetupDiEnumDeviceInfo(hDevInfoSet, 0, devInfo);
		if (!succeed)
			assertTrue(getLastError()==ERROR_NO_MORE_ITEMS);
		else
			assertTrue(succeed);
	}
}
