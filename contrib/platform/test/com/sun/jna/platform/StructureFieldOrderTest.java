/* Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform;


import com.sun.jna.Platform;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.DBT;
import junit.framework.TestCase;

import com.sun.jna.StructureFieldOrderInspector;

import java.util.ArrayList;
import java.util.List;

public class StructureFieldOrderTest extends TestCase {

    private String origPropJNANoSys;

    protected void setUp() {
        origPropJNANoSys = System.getProperty("jna.nosys");
        System.setProperty("jna.nosys", "true"); // would be set by ant script, set here for IDE usage
    }

    protected void tearDown() {
        if (origPropJNANoSys == null) {
            System.getProperties().remove("jna.nosys");
        } else {
            System.setProperty("jna.nosys", origPropJNANoSys);
        }
    }


    public void testMethodGetFieldOrder() {
        final List<String> ignoreConstructorError = new ArrayList<String>();

        if (Platform.isWindows()) {
            ignoreConstructorError.add(X11.class.getName() + "$");
        } else {
            ignoreConstructorError.add(com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1.class.getName());
            ignoreConstructorError.add(com.sun.jna.platform.win32.Winspool.PRINTER_INFO_4.class.getName());
            ignoreConstructorError.add(com.sun.jna.platform.win32.SetupApi.SP_DEVICE_INTERFACE_DATA.class.getName());
            ignoreConstructorError.add(com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA.class.getName());
        }

        ignoreConstructorError.add(DBT.DEV_BROADCAST_HANDLE.class.getName()); // manually validated by wolftobias
        ignoreConstructorError.add(DBT.DEV_BROADCAST_PORT.class.getName()); // manually validated by wolftobias

        StructureFieldOrderInspector.batchCheckStructureGetFieldOrder(FileUtils.class, ignoreConstructorError);
    }

// test below is helpful when investigating failure cause of a specific Structure class, it shows full causes and traces.
/*
    public void testMethodGetFieldOrderSingleClass() {
        final List<String> ignoreConstructorError = new ArrayList<String>();

        if (Platform.isWindows()) {
            ignoreConstructorError.add(X11.class.getName() + "$");
        }

        StructureFieldOrderInspector.checkMethodGetFieldOrder(com.sun.jna.platform.win32.SetupApi.SP_DEVICE_INTERFACE_DATA.class, ignoreConstructorError);
    }
//*/


}
