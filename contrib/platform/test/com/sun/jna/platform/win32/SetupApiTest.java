/*
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
import static org.junit.Assert.assertNotEquals;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class SetupApiTest extends TestCase {

    /**
     * member index for the first device, see
     * {@link SetupApi#SetupDiEnumDeviceInfo(HANDLE, int, SP_DEVINFO_DATA)}
     */
    private static final int FIRST_MEMBER = 0;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SetupApiTest.class);
    }

    /**
     * Tests the mapping of
     * {@link SetupApi#SetupDiOpenDevRegKey(HANDLE, SP_DEVINFO_DATA, int, int, int, int)}
     * .
     * <p>
     * The test pass if SetupDiOpenDevRegKey(..) returns a valid {@link HKEY}
     * pointing to the first found device on the current machine.
     * <p>
     * NOTE: We only test the mapping of SetupDiOpenDevRegKey(..), not it's
     * functionality.
     */
    public void testSetupDiOpenDevRegKey() {
        // hDevInfoSet repesents a list of installed devices for all device
        // setup classes or all device interface classes
        HANDLE hDevInfoSet = SetupApi.INSTANCE.SetupDiGetClassDevs(null, null, null, DIGCF_ALLCLASSES);
        assertNotEquals(INVALID_HANDLE_VALUE, hDevInfoSet);

        SP_DEVINFO_DATA devInfo = new SP_DEVINFO_DATA();
        // there must be least one device (drive,processor,pci,usb,...) on the
        // current machine
        assertTrue(SetupApi.INSTANCE.SetupDiEnumDeviceInfo(hDevInfoSet, FIRST_MEMBER, devInfo));

        HKEY hDeviceKey = SetupApi.INSTANCE.SetupDiOpenDevRegKey(hDevInfoSet, devInfo, DICS_FLAG_GLOBAL, 0, DIREG_DEV, KEY_QUERY_VALUE);
        assertNotEquals(INVALID_HANDLE_VALUE, hDeviceKey);

        Advapi32.INSTANCE.RegCloseKey(hDeviceKey);
    }

    /**
     * Tests the mapping of
     * {@link SetupApi#SetupDiEnumDeviceInfo(HANDLE, int, SP_DEVINFO_DATA)} .
     * <p>
     * There are 2 different results possible, depending availability of an
     * COM-Port on the current machine:
     * <ul>
     * <li>If the current machine has no COM-Port the method must fail and the
     * the last error indicate that there are no more values / COM-Ports.
     * <li>If the current machine has at least one COM-Port the method must
     * succeed. The test pass if no exception is thrown.
     * </ul>
     */
    public void testSetupDiEnumDeviceInfo() {
        HANDLE hDevInfoSet = SetupApi.INSTANCE.SetupDiGetClassDevs(GUID_DEVINTERFACE_COMPORT, null, null, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);
        SP_DEVINFO_DATA devInfo = new SP_DEVINFO_DATA();
        boolean succeed = SetupApi.INSTANCE.SetupDiEnumDeviceInfo(hDevInfoSet, FIRST_MEMBER, devInfo);
        boolean hasNoMoreItems = (getLastError() == ERROR_NO_MORE_ITEMS);

        assertTrue(succeed || hasNoMoreItems);
    }
}
