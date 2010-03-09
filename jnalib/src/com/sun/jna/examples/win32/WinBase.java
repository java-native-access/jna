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
package com.sun.jna.examples.win32;

import com.sun.jna.Platform;
import com.sun.jna.Structure;

/**
 * Ported from Winbase.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public abstract class WinBase {

	public static final int WAIT_FAILED = 0xFFFFFFFF;
	public static final int WAIT_OBJECT_0 = ((NTStatus.STATUS_WAIT_0 ) + 0 );
	public static final int WAIT_ABANDONED = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
	public static final int WAIT_ABANDONED_0 = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
	
	/**
	 * Maximum computer name length.
	 * @return 15 on MAC, 31 on everything else.
	 */
	public static int MAX_COMPUTERNAME_LENGTH() {
		if (Platform.isMac()) {
			return 15;
		} else {
			return 31;			
		}
	}
}
