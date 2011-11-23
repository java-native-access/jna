/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
}

