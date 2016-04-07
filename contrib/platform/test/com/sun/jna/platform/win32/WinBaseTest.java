/* Copyright (c) 2015 Markus Bollig, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import java.util.Calendar;
import java.util.Date;

import com.sun.jna.platform.win32.WinBase.DCB;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;

import junit.framework.TestCase;

public class WinBaseTest extends TestCase {
    public WinBaseTest() {
        super();
    }

    public WinBaseTest(String name) {
        super(name);
    }

    public void testFiletime() {
        // subtract to convert ms after 1/1/1970 to ms after 1/1/1601
        long epochDiff = 11644473600000L;
        // Construct filetimes for ms after 1/1/1601, check for 100-ns after
        assertEquals("Mismatched filetime for 2ms", (new FILETIME(new Date(2L - epochDiff))).toDWordLong().longValue(), 2L * 10000);
        assertEquals("Mismatched filetime for 2^16ms", (new FILETIME(new Date((1L << 16) - epochDiff))).toDWordLong().longValue(), (1L << 16) * 10000);
        assertEquals("Mismatched filetime for 2^32ms", (new FILETIME(new Date((1L << 32) - epochDiff))).toDWordLong().longValue(), (1L << 32) * 10000);
        assertEquals("Mismatched filetime for 2^49ms", (new FILETIME(new Date((1L << 49) - epochDiff))).toDWordLong().longValue(), (1L << 49) * 10000);
    }

    public void testCalendarToSystemTimeConversion() {
        Calendar expected = Calendar.getInstance();
        SYSTEMTIME sysTime = new SYSTEMTIME();
        sysTime.fromCalendar(expected);

        assertEquals("Mismatched systime year", expected.get(Calendar.YEAR), sysTime.wYear);
        assertEquals("Mismatched systime month", (1 + expected.get(Calendar.MONTH) - Calendar.JANUARY), sysTime.wMonth);
        assertEquals("Mismatched systime day", expected.get(Calendar.DAY_OF_MONTH), sysTime.wDay);
        assertEquals("Mismatched systime weekday", expected.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY, sysTime.wDayOfWeek);

        assertEquals("Mismatched systime hour", expected.get(Calendar.HOUR_OF_DAY), sysTime.wHour);
        assertEquals("Mismatched systime minute", expected.get(Calendar.MINUTE), sysTime.wMinute);
        assertEquals("Mismatched systime second", expected.get(Calendar.SECOND), sysTime.wSecond);
        // NOTE: we do not check millis due to clock granularity issues

        Calendar actual = sysTime.toCalendar();
        assertEquals("Mismatched calendar year", sysTime.wYear, actual.get(Calendar.YEAR));
        assertEquals("Mismatched calendar month", Calendar.JANUARY + (sysTime.wMonth - 1), actual.get(Calendar.MONTH));
        assertEquals("Mismatched calendar day", sysTime.wDay, actual.get(Calendar.DAY_OF_MONTH));
        assertEquals("Mismatched calendar weekday", sysTime.wDayOfWeek, actual.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY);

        assertEquals("Mismatched calendar hour", sysTime.wHour, actual.get(Calendar.HOUR_OF_DAY));
        assertEquals("Mismatched calendar minute", sysTime.wMinute, actual.get(Calendar.MINUTE));
        assertEquals("Mismatched calendar second", sysTime.wSecond, actual.get(Calendar.SECOND));
        // NOTE: we do not check millis due to clock granularity issues

        assertEquals("Mismatched reconstructed year", expected.get(Calendar.YEAR), actual.get(Calendar.YEAR));
        assertEquals("Mismatched reconstructed month", expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
        assertEquals("Mismatched reconstructed day", expected.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH));
        assertEquals("Mismatched reconstructed weekday", expected.get(Calendar.DAY_OF_WEEK), actual.get(Calendar.DAY_OF_WEEK));

        assertEquals("Mismatched reconstructed hour", expected.get(Calendar.HOUR_OF_DAY), actual.get(Calendar.HOUR_OF_DAY));
        assertEquals("Mismatched reconstructed minute", expected.get(Calendar.MINUTE), actual.get(Calendar.MINUTE));
        assertEquals("Mismatched reconstructed second", expected.get(Calendar.SECOND), actual.get(Calendar.SECOND));
        // NOTE: we do not check millis due to clock granularity issues
    }

    /**
     * Test the mapping of the {@link DCB} structure.
     * Particularly the mapping of the bit field is tested.
     */
    public void testDCBStructureMapping() {
        //first we test if the WinBase.DCB bitfiled mapping works as expected.
        WinBase.DCB lpDCB = new WinBase.DCB();
        lpDCB.controllBits.setValue(0);

        lpDCB.controllBits.setfBinary(true);
        assertEquals(1, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfBinary());
        lpDCB.controllBits.setfBinary(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfBinary());

        lpDCB.controllBits.setfParity(true);
        assertEquals(2, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfParity());
        lpDCB.controllBits.setfParity(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfParity());

        lpDCB.controllBits.setfOutxCtsFlow(true);
        assertEquals(4, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfOutxCtsFlow());
        lpDCB.controllBits.setfOutxCtsFlow(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfOutxCtsFlow());

        lpDCB.controllBits.setfOutxDsrFlow(true);
        assertEquals(8, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfOutxDsrFlow());
        lpDCB.controllBits.setfOutxDsrFlow(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfOutxDsrFlow());

        lpDCB.controllBits.setfDtrControl(WinBase.DTR_CONTROL_ENABLE);
        assertEquals(16, lpDCB.controllBits.longValue());
        assertEquals(WinBase.DTR_CONTROL_ENABLE, lpDCB.controllBits.getfDtrControl());
        lpDCB.controllBits.setfDtrControl(WinBase.DTR_CONTROL_HANDSHAKE);
        assertEquals(32, lpDCB.controllBits.longValue());
        assertEquals(WinBase.DTR_CONTROL_HANDSHAKE, lpDCB.controllBits.getfDtrControl());
        lpDCB.controllBits.setfDtrControl(WinBase.DTR_CONTROL_DISABLE);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(WinBase.DTR_CONTROL_DISABLE, lpDCB.controllBits.getfDtrControl());

        lpDCB.controllBits.setfDsrSensitivity(true);
        assertEquals(64, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfDsrSensitivity());
        lpDCB.controllBits.setfDsrSensitivity(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfDsrSensitivity());

        lpDCB.controllBits.setfTXContinueOnXoff(true);
        assertEquals(128, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfTXContinueOnXoff());
        lpDCB.controllBits.setfTXContinueOnXoff(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfTXContinueOnXoff());

        lpDCB.controllBits.setfOutX(true);
        assertEquals(256, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfOutX());
        lpDCB.controllBits.setfOutX(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfOutX());

        lpDCB.controllBits.setfInX(true);
        assertEquals(512, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfInX());
        lpDCB.controllBits.setfInX(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfInX());

        lpDCB.controllBits.setfErrorChar(true);
        assertEquals(1024, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfErrorChar());
        lpDCB.controllBits.setfErrorChar(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfErrorChar());

        lpDCB.controllBits.setfNull(true);
        assertEquals(2048, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfNull());
        lpDCB.controllBits.setfNull(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfNull());


        lpDCB.controllBits.setfRtsControl(WinBase.RTS_CONTROL_ENABLE);
        assertEquals(4096, lpDCB.controllBits.longValue());
        assertEquals(WinBase.RTS_CONTROL_ENABLE, lpDCB.controllBits.getfRtsControl());
        lpDCB.controllBits.setfRtsControl(WinBase.RTS_CONTROL_HANDSHAKE);
        assertEquals(8192, lpDCB.controllBits.longValue());
        assertEquals(WinBase.RTS_CONTROL_HANDSHAKE, lpDCB.controllBits.getfRtsControl());
        lpDCB.controllBits.setfRtsControl(WinBase.RTS_CONTROL_TOGGLE);
        assertEquals(12288, lpDCB.controllBits.longValue());
        assertEquals(WinBase.RTS_CONTROL_TOGGLE, lpDCB.controllBits.getfRtsControl());
        lpDCB.controllBits.setfRtsControl(WinBase.RTS_CONTROL_DISABLE);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(WinBase.RTS_CONTROL_DISABLE, lpDCB.controllBits.getfRtsControl());


        lpDCB.controllBits.setfAbortOnError(true);
        assertEquals(16384, lpDCB.controllBits.longValue());
        assertEquals(true, lpDCB.controllBits.getfAbortOnError());
        lpDCB.controllBits.setfAbortOnError(false);
        assertEquals(0, lpDCB.controllBits.longValue());
        assertEquals(false, lpDCB.controllBits.getfAbortOnError());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(WinBaseTest.class);
    }
}
