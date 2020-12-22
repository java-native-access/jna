/* Copyright (c) 2020 Matthias Bläsing
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
package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_DEVICEINTERFACE;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_DEVNODE;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_HANDLE;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_HDR;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_NET;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_OEM;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_PORT;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_VOLUME;
import org.junit.Test;

public class DBTTest {
    @Test
    public void testInstantiation() {
        // Ensure, that the structures can be instantiated
        Structure.newInstance(DEV_BROADCAST_DEVICEINTERFACE.class);
        Structure.newInstance(DEV_BROADCAST_DEVNODE.class);
        Structure.newInstance(DEV_BROADCAST_HANDLE.class);
        Structure.newInstance(DEV_BROADCAST_HDR.class);
        Structure.newInstance(DEV_BROADCAST_NET.class);
        Structure.newInstance(DEV_BROADCAST_OEM.class);
        Structure.newInstance(DEV_BROADCAST_PORT.class);
        Structure.newInstance(DEV_BROADCAST_VOLUME.class);
    }
}
