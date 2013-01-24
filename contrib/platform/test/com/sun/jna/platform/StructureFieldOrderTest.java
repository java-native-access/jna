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
import junit.framework.TestCase;

import com.sun.jna.StructureFieldOrderInspector;

import java.util.ArrayList;
import java.util.List;

public class StructureFieldOrderTest extends TestCase {

    public void testMethodGetFieldOrder() {
        final List<String> ignoreConstructorError = new ArrayList<String>();

        if (Platform.isWindows()) {
            ignoreConstructorError.add(X11.class.getName() + "$");
        }

        StructureFieldOrderInspector.batchCheckStructureGetFieldOrder(FileUtils.class, ignoreConstructorError);
    }

}
