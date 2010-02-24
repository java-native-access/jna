package com.sun.jna.examples.win32;

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
