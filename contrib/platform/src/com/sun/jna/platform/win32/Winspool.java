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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Ported from Winspool.h.
 * Windows SDK 6.0a
 * @author dblock[at]dblock.org 
 */
public interface Winspool extends StdCallLibrary {

    Winspool INSTANCE = (Winspool) Native.loadLibrary("Winspool.drv", Winspool.class, 
                                                      W32APIOptions.UNICODE_OPTIONS);

    /**
     * The EnumPrinters function enumerates available printers, print servers, domains, or print providers.
     * @param Flags
     *  The types of print objects that the function should enumerate.
     * @param Name
     *  If Level is 1, Flags contains PRINTER_ENUM_NAME, and Name is non-NULL, then Name is a pointer 
     *  to a null-terminated string that specifies the name of the object to enumerate. This string can 
     *  be the name of a server, a domain, or a print provider.
     *  If Level is 1, Flags contains PRINTER_ENUM_NAME, and Name is NULL, then the function enumerates 
     *  the available print providers.
     *  If Level is 1, Flags contains PRINTER_ENUM_REMOTE, and Name is NULL, then the function enumerates 
     *  the printers in the user's domain.
     *  If Level is 2 or 5,Name is a pointer to a null-terminated string that specifies the name of a 
     *  server whose printers are to be enumerated. If this string is NULL, then the function enumerates
     *  the printers installed on the local computer.
     *  If Level is 4, Name should be NULL. The function always queries on the local computer.
     *  When Name is NULL, setting Flags to PRINTER_ENUM_LOCAL | PRINTER_ENUM_CONNECTIONS enumerates
     *  printers that are installed on the local machine. These printers include those that are physically
     *  attached to the local machine as well as remote printers to which it has a network connection.
     *  When Name is not NULL, setting Flags to PRINTER_ENUM_LOCAL | PRINTER_ENUM_NAME enumerates the 
     *  local printers that are installed on the server Name.
     * @param Level
     *  The type of data structures pointed to by pPrinterEnum. Valid values are 1, 2, 4, and 5, which 
     *  correspond to the PRINTER_INFO_1, PRINTER_INFO_2 , PRINTER_INFO_4, and PRINTER_INFO_5 data 
     *  structures.
     * @param pPrinterEnum
     *  A pointer to a buffer that receives an array of PRINTER_INFO_1, PRINTER_INFO_2, PRINTER_INFO_4, 
     *  or PRINTER_INFO_5 structures. Each structure contains data that describes an available print 
     *  object.
     *  If Level is 1, the array contains PRINTER_INFO_1 structures. If Level is 2, the array contains 
     *  PRINTER_INFO_2 structures. If Level is 4, the array contains PRINTER_INFO_4 structures. If Level 
     *  is 5, the array contains PRINTER_INFO_5 structures.
     *  The buffer must be large enough to receive the array of data structures and any strings or other 
     *  data to which the structure members point. If the buffer is too small, the pcbNeeded parameter 
     *  returns the required buffer size.
     * @param cbBuf
     *  The size, in bytes, of the buffer pointed to by pPrinterEnum.
     * @param pcbNeeded
     *  A pointer to a value that receives the number of bytes copied if the function succeeds or the
     *  number of bytes required if cbBuf is too small.
     * @param pcReturned
     *  A pointer to a value that receives the number of PRINTER_INFO_1, PRINTER_INFO_2 , PRINTER_INFO_4, 
     *  or PRINTER_INFO_5 structures that the function returns in the array to which pPrinterEnum points.
     * @return
     *  If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. 
     */
    boolean EnumPrinters(int Flags, String Name, int Level, Pointer pPrinterEnum,
                         int cbBuf, IntByReference pcbNeeded, IntByReference pcReturned);
	
    public static class PRINTER_INFO_1 extends Structure {
        public int Flags;
        public String pDescription;
        public String pName;
        public String pComment;
	    
        public PRINTER_INFO_1() {
	    	
        }
	    
        public PRINTER_INFO_1(int size) {
            super(new Memory(size));
        }
    }

    public static class PRINTER_INFO_4 extends Structure {
        public String pPrinterName;
        public String pServerName;
        public DWORD Attributes;
	    
        public PRINTER_INFO_4() {
	    	
        }
	    
        public PRINTER_INFO_4(int size) {
            super(new Memory(size));
        }
    }
	
    int PRINTER_ENUM_DEFAULT = 0x00000001;
    int PRINTER_ENUM_LOCAL = 0x00000002;
    int PRINTER_ENUM_CONNECTIONS = 0x00000004;
    int PRINTER_ENUM_FAVORITE = 0x00000004;
    int PRINTER_ENUM_NAME = 0x00000008;
    int PRINTER_ENUM_REMOTE = 0x00000010;
    int PRINTER_ENUM_SHARED = 0x00000020;
    int PRINTER_ENUM_NETWORK = 0x00000040;

    int PRINTER_ENUM_EXPAND = 0x00004000;
    int PRINTER_ENUM_CONTAINER = 0x00008000;

    int PRINTER_ENUM_ICONMASK = 0x00ff0000;
    int PRINTER_ENUM_ICON1 = 0x00010000;
    int PRINTER_ENUM_ICON2 = 0x00020000;
    int PRINTER_ENUM_ICON3 = 0x00040000;
    int PRINTER_ENUM_ICON4 = 0x00080000;
    int PRINTER_ENUM_ICON5 = 0x00100000;
    int PRINTER_ENUM_ICON6 = 0x00200000;
    int PRINTER_ENUM_ICON7 = 0x00400000;
    int PRINTER_ENUM_ICON8 = 0x00800000;
    int PRINTER_ENUM_HIDE = 0x01000000;
}
