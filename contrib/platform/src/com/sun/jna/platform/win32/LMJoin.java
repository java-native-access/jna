/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

/**
 * Ported from LMJoin.h.
 * Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface LMJoin {

    /**
     * Status of a workstation.
     */
    public abstract class NETSETUP_JOIN_STATUS {
        public static final int NetSetupUnknownStatus = 0;
        public static final int NetSetupUnjoined = 1;
        public static final int NetSetupWorkgroupName = 2;
        public static final int NetSetupDomainName = 3;		
    };

}
