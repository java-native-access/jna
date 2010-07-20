/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * This module contains the function prototypes and constant, type and structure 
 * definitions for the Windows 32-Bit Registry API.
 * Ported from WinReg.h
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface WinReg extends StdCallLibrary {
	
	public static class HKEY extends HANDLE {
        public HKEY() { }
        public HKEY(Pointer p) { super(p); }
        public HKEY(int value) { super(new Pointer(value)); }
	}
	
    public static class HKEYByReference extends ByReference {
        public HKEYByReference() {
            this(null);
        }
        
        public HKEYByReference(HKEY h) {
            super(Pointer.SIZE);
            setValue(h);
        }
        
        public void setValue(HKEY h) {
            getPointer().setPointer(0, h != null ? h.getPointer() : null);
        }
        
        public HKEY getValue() {
            Pointer p = getPointer().getPointer(0);
            if (p == null)
                return null;
            if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) 
                return (HKEY) WinBase.INVALID_HANDLE_VALUE;
            HKEY h = new HKEY();
            h.setPointer(p);
            return h;
        }
    }
	
	public static final HKEY HKEY_CLASSES_ROOT = new HKEY(0x80000000);
	public static final HKEY HKEY_CURRENT_USER = new HKEY(0x80000001);
	public static final HKEY HKEY_LOCAL_MACHINE = new HKEY(0x80000002);
	public static final HKEY HKEY_USERS = new HKEY(0x80000003);
	public static final HKEY HKEY_PERFORMANCE_DATA= new HKEY(0x80000004);
	public static final HKEY HKEY_PERFORMANCE_TEXT= new HKEY(0x80000050);
	public static final HKEY HKEY_PERFORMANCE_NLSTEXT = new HKEY(0x80000060);
	public static final HKEY HKEY_CURRENT_CONFIG  = new HKEY(0x80000005);
	public static final HKEY HKEY_DYN_DATA = new HKEY(0x80000006);
}
