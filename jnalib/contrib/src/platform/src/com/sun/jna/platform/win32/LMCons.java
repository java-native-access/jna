package com.sun.jna.platform.win32;

/**
 * Ported from LMCons.h.
 * @author dblock[at]dblock.org
 * Windows SDK 6.0A
 */
public interface LMCons {
	public static final int  NETBIOS_NAME_LEN = 16;            // NetBIOS net name (bytes)

	/**
	 * Value to be used with APIs which have a "preferred maximum length" parameter.
	 * This value indicates that the API should just allocate "as much as it takes."
	 */
	public static final int  MAX_PREFERRED_LENGTH = -1;
}
