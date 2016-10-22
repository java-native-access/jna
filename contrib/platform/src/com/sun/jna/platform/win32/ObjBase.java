/*
 * Copyright 2010 Digital Rapids Corp.
 */
/* Copyright (c) 2010 Timothy Wall, All Rights Reserved
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


import com.sun.jna.platform.win32.WTypes;


/**
 * Definitions from ObjBase.h
 * @author scott.palmer
 */
public interface ObjBase {


    /** The clsctx inproc. */
    int CLSCTX_INPROC = (WTypes.CLSCTX_INPROC_SERVER | WTypes.CLSCTX_INPROC_HANDLER);
    
    // With DCOM, CLSCTX_REMOTE_SERVER should be included
    /** The clsctx all. */
    int CLSCTX_ALL = (WTypes.CLSCTX_INPROC_SERVER
                      | WTypes.CLSCTX_INPROC_HANDLER
                      | WTypes.CLSCTX_LOCAL_SERVER
                      | WTypes.CLSCTX_REMOTE_SERVER);


    /** The clsctx server. */
    int CLSCTX_SERVER = (WTypes.CLSCTX_INPROC_SERVER
                         | WTypes.CLSCTX_LOCAL_SERVER
                         | WTypes.CLSCTX_REMOTE_SERVER);
}

 
 








