/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.examples;

import com.sun.jna.Library;
import com.sun.jna.Native;

/** Sample implementation of C library access. */
public interface CLibrary extends Library {
    
    CLibrary INSTANCE = (CLibrary)
        Native.loadLibrary((System.getProperty("os.name").startsWith("Windows")
                            ? "msvcrt" : "c"), CLibrary.class);

    int atol(String s);
}
