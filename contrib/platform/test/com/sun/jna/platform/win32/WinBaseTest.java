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

import com.sun.jna.platform.win32.WinBase.DCB;

import junit.framework.TestCase;

public class WinBaseTest extends TestCase {

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
