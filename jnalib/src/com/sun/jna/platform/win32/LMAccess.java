package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;

/**
 * Ported from LMAccess.h.
 * Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface LMAccess {

	public static class LOCALGROUP_INFO_0 extends Structure {
		public LOCALGROUP_INFO_0() {
			super();
		}

		public LOCALGROUP_INFO_0(Pointer memory) {
			useMemory(memory);
			read();
		}

		public WString lgrui0_name;
	}
	
	//
	// bit masks for the NetUserEnum filter parameter.
	//

	public static final int FILTER_TEMP_DUPLICATE_ACCOUNT = 0x0001;
	public static final int FILTER_NORMAL_ACCOUNT = 0x0002;
	// public static final int FILTER_PROXY_ACCOUNT = 0x0004;
	public static final int FILTER_INTERDOMAIN_TRUST_ACCOUNT = 0x0008;
	public static final int FILTER_WORKSTATION_TRUST_ACCOUNT = 0x0010;
	public static final int FILTER_SERVER_TRUST_ACCOUNT = 0x0020;	
}
