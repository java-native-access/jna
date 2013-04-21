/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
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
package com.sun.jna;

public interface Paths {
    String BUILDDIR = Platform.isWindowsCE() 
        ? "/Storage Card"
        : System.getProperty("jna.builddir",
                             "build" + (Platform.is64Bit() ? "-d64" : "")); 
    String CLASSES = BUILDDIR + (Platform.isWindowsCE() ? "" : "/classes");
    String JNAJAR = BUILDDIR + "/jna.jar";
    
    String TESTPATH = Platform.isWindowsCE() ? "/Storage Card/" : BUILDDIR + "/native/";
    String TESTJAR = BUILDDIR + "/jna-test.jar";
}
