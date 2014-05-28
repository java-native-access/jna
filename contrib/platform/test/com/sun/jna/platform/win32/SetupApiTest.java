/*
 * Copyright [2014] [Christian Schwarz]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.jna.platform.win32;

import static com.sun.jna.Native.getLastError;
import static com.sun.jna.platform.win32.SetupApi.DICS_FLAG_GLOBAL;
import static com.sun.jna.platform.win32.SetupApi.DIGCF_ALLCLASSES;
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

/**
 * Tests the mappings of {@link SetupApi}
 * 
 * @author Christian Schwarz
 *
 */
public class SetupApiTest extends TestCase {
	/** This is the class under test */
	private final SetupApi setupApi = SetupApi.INSTANCE;
	/** */
	private final Advapi32 advapi32 = Advapi32.INSTANCE;

	/** member index for the first device, see {@link SetupApi#SetupDiEnumDeviceInfo(HANDLE, int, SP_DEVINFO_DATA)} */
	private static final int FIRST_MEMBER = 0;

	/** used by multiple test, to get device informations */
	private SP_DEVINFO_DATA devInfo;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SetupApiTest.class);
	}

	@Override
	protected void setUp() throws Exception {
		devInfo = new SP_DEVINFO_DATA();
	}

	/**
	 * Tests the mapping of {@link SetupApi#SetupDiOpenDevRegKey(HANDLE, SP_DEVINFO_DATA, int, int, int, int)}.
	 * <p>
	 * The test pass if SetupDiOpenDevRegKey(..) returns a valid {@link HKEY} pointing to the first found device on the
	 * current machine.
	 * <p>
	 * NOTE: We only test the mapping of SetupDiOpenDevRegKey(..), not it's functionality.
	 */
	public void testSetupDiOpenDevRegKey() {
		// hDevInfoSet repesents a list of installed devices for all device setup classes or all device interface
		// classes
		HANDLE hDevInfoSet = setupApi.SetupDiGetClassDevs(null, null, null, DIGCF_ALLCLASSES);
		assertTrue(hDevInfoSet != INVALID_HANDLE_VALUE);

		// there must be least one device (drive,processor,pci,usb,...) on the current machine
		assertTrue(setupApi.SetupDiEnumDeviceInfo(hDevInfoSet, FIRST_MEMBER, devInfo));

		HKEY hDeviceKey = setupApi.SetupDiOpenDevRegKey(hDevInfoSet, devInfo, DICS_FLAG_GLOBAL, 0, DIREG_DEV, KEY_QUERY_VALUE);
		assertTrue(hDeviceKey != INVALID_HANDLE_VALUE);

		advapi32.RegCloseKey(hDeviceKey);

	}

	/**
	 * Tests the mapping of {@link SetupApi#SetupDiEnumDeviceInfo(HANDLE, int, SP_DEVINFO_DATA)} .
	 * <p>
	 * There are 2 different results possible, depending availability of an COM-Port on the current machine:
	 * <ul>
	 * <li>If the current machine has no COM-Port the method must fail and the the last error indicate that there are no
	 * more values / COM-Ports.
	 * <li>If the current machine has at least one COM-Port the method must succeed. The test pass if no exception is
	 * thrown.
	 * </ul>
	 */
	public void testSetupDiEnumDeviceInfo() {
		HANDLE hDevInfoSet = setupApi.SetupDiGetClassDevs(GUID_DEVINTERFACE_COMPORT, null, null, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);
		boolean succeed = setupApi.SetupDiEnumDeviceInfo(hDevInfoSet, FIRST_MEMBER, devInfo);
		if (!succeed)
			assertTrue(getLastError() == ERROR_NO_MORE_ITEMS);

	}
}
