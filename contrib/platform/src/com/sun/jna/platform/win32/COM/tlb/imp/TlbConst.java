/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM.tlb.imp;

public interface TlbConst {

    public final static String CR = "\n";

    public final static String CRCR = "\n\n";

    public final static String TYPELIB_ID_SHELL = "{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}";

    public final static String TYPELIB_MAJOR_VERSION_SHELL = "1";
    
    public final static String TYPELIB_MINOR_VERSION_SHELL = "0";

    public final static String TYPELIB_ID_WORD = "{00020905-0000-0000-C000-000000000046}";

    public final static String TYPELIB_MAJOR_VERSION_WORD = "8";
    
    public final static String TYPELIB_MINOR_VERSION_WORD = "4";

    public final static String TYPELIB_ID_OFFICE = "{2DF8D04C-5BFA-101B-BDE5-00AA0044DE52}";

    public final static String TYPELIB_MAJOR_VERSION_OFFICE = "2";
    
    public final static String TYPELIB_MINOR_VERSION_OFFICE = "5";
    
    public final static String CMD_ARG_TYPELIB_ID = "tlb.id";
    
    public final static String CMD_ARG_BINDING_MODE = "bind.mode";
    
    public final static String BINDING_MODE_VTABLE = "vtable";

    public final static String BINDING_MODE_DISPID = "dispid";
    
    public final static String CMD_ARG_TYPELIB_MAJOR_VERSION = "tlb.major.version";
    
    public final static String CMD_ARG_TYPELIB_MINOR_VERSION = "tlb.minor.version";
    
    public final static String CMD_ARG_TYPELIB_FILE = "tlb.file";
    
    public final static String CMD_ARG_OUTPUT_DIR = "output.dir";
}
