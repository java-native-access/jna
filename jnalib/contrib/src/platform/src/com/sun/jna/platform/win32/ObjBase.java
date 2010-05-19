/*
 * Copyright 2010 Digital Rapids Corp.
 */

/* Copyright (c) 2010 Timothy Wall, All Rights Reserved
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

import com.sun.jna.platform.win32.WTypes;

/**
 * Definitions from ObjBase.h
 * @author scott.palmer
 */
public interface ObjBase {

public static final int CLSCTX_INPROC = (WTypes.CLSCTX_INPROC_SERVER | WTypes.CLSCTX_INPROC_HANDLER);

// With DCOM, CLSCTX_REMOTE_SERVER should be included
public static final int CLSCTX_ALL = (WTypes.CLSCTX_INPROC_SERVER
        | WTypes.CLSCTX_INPROC_HANDLER
        | WTypes.CLSCTX_LOCAL_SERVER
        | WTypes.CLSCTX_REMOTE_SERVER);

public static final int CLSCTX_SERVER = (WTypes.CLSCTX_INPROC_SERVER
        | WTypes.CLSCTX_LOCAL_SERVER
        | WTypes.CLSCTX_REMOTE_SERVER);
}
