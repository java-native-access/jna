/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.ByReference;

/**
 * This module contains the function prototypes and constant, type and structure 
 * definitions for the Windows 32-Bit Registry API.
 * Ported from WinReg.h
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface WinReg {
	
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
            super(Native.POINTER_SIZE);
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
	
    HKEY HKEY_CLASSES_ROOT = new HKEY(0x80000000);
    HKEY HKEY_CURRENT_USER = new HKEY(0x80000001);
    HKEY HKEY_LOCAL_MACHINE = new HKEY(0x80000002);
    HKEY HKEY_USERS = new HKEY(0x80000003);
    HKEY HKEY_PERFORMANCE_DATA= new HKEY(0x80000004);
    HKEY HKEY_PERFORMANCE_TEXT= new HKEY(0x80000050);
    HKEY HKEY_PERFORMANCE_NLSTEXT = new HKEY(0x80000060);
    HKEY HKEY_CURRENT_CONFIG  = new HKEY(0x80000005);
    HKEY HKEY_DYN_DATA = new HKEY(0x80000006);
}
