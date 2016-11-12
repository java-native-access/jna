/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
