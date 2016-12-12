/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

public interface NTStatus {
	
    int STATUS_SUCCESS = 0x00000000; // ntsubauth
    int STATUS_BUFFER_TOO_SMALL  = 0xC0000023;

    //
    // MessageId: STATUS_WAIT_0
    //
    // MessageText:
    //
    //  STATUS_WAIT_0
    //
    int  STATUS_WAIT_0 = 0x00000000;    // winnt

    //
    // MessageId: STATUS_WAIT_1
    //
    // MessageText:
    //
    //  STATUS_WAIT_1
    //
    int  STATUS_WAIT_1 = 0x00000001;

    //
    // MessageId: STATUS_WAIT_2
    //
    // MessageText:
    //
    //  STATUS_WAIT_2
    //
    int  STATUS_WAIT_2 = 0x00000002;

    //
    // MessageId: STATUS_WAIT_3
    //
    // MessageText:
    //
    //  STATUS_WAIT_3
    //
    int  STATUS_WAIT_3 = 0x00000003;

    //
    // MessageId: STATUS_WAIT_63
    //
    // MessageText:
    //
    //  STATUS_WAIT_63
    //
    int  STATUS_WAIT_63 = 0x0000003F;

    //
    // The success status codes 128 - 191 are reserved for wait completion
    // status with an abandoned mutant object.
    //
    int  STATUS_ABANDONED = 0x00000080;

    //
    // MessageId: STATUS_ABANDONED_WAIT_0
    //
    // MessageText:
    //
    //  STATUS_ABANDONED_WAIT_0
    //
    int  STATUS_ABANDONED_WAIT_0 = 0x00000080;    // winnt

    //
    // MessageId: STATUS_ABANDONED_WAIT_63
    //
    // MessageText:
    //
    //  STATUS_ABANDONED_WAIT_63
    //
    int  STATUS_ABANDONED_WAIT_63 = 0x000000BF;

    //
    // MessageId: STATUS_INVALID_OWNER
    //
    // MessageText:
    //
    //  Indicates a particular Security ID may not be assigned as the owner of an object.
    //
    int  STATUS_INVALID_OWNER = 0xC000005A;
}

