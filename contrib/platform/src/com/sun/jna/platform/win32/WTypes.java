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

/**
 * Constant defined in WTypes.h
 * @author scott.palmer
 */
public interface WTypes {
    int CLSCTX_INPROC_SERVER = 0x1;
    int CLSCTX_INPROC_HANDLER = 0x2;
    int CLSCTX_LOCAL_SERVER = 0x4;
    int CLSCTX_INPROC_SERVER16 = 0x8;
    int CLSCTX_REMOTE_SERVER = 0x10;
    int CLSCTX_INPROC_HANDLER16 = 0x20;
    int CLSCTX_RESERVED1 = 0x40;
    int CLSCTX_RESERVED2 = 0x80;
    int CLSCTX_RESERVED3 = 0x100;
    int CLSCTX_RESERVED4 = 0x200;
    int CLSCTX_NO_CODE_DOWNLOAD = 0x400;
    int CLSCTX_RESERVED5 = 0x800;
    int CLSCTX_NO_CUSTOM_MARSHAL = 0x1000;
    int CLSCTX_ENABLE_CODE_DOWNLOAD = 0x2000;
    int CLSCTX_NO_FAILURE_LOG = 0x4000;
    int CLSCTX_DISABLE_AAA = 0x8000;
    int CLSCTX_ENABLE_AAA = 0x10000;
    int CLSCTX_FROM_DEFAULT_CONTEXT = 0x20000;
    int CLSCTX_ACTIVATE_32_BIT_SERVER = 0x40000;
    int CLSCTX_ACTIVATE_64_BIT_SERVER = 0x80000;
}
