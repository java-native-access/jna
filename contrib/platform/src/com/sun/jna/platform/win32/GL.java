/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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

/**
 * Definitions for WinOpenGL
 */
public interface GL {
    public final int GL_VENDOR = 0x1F00;
    public final int GL_RENDERER = 0x1F01;
    public final int GL_VERSION = 0x1F02;
    public final int GL_EXTENSIONS = 0x1F03;
}
