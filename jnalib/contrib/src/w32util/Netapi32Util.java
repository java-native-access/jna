package w32util;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.examples.win32.*;
import com.sun.jna.examples.win32.Netapi32.*;

public abstract class Netapi32Util {
	
	/**
	 * Get information about a computer.
	 * @param computerName
	 * @return Domain or workgroup name.
	 */
	public static String GetDomainName(String computerName) {
		char[] lpServer = (computerName == null) ? null : Native.toCharArray(computerName);
		PointerByReference lpNameBuffer = new PointerByReference();
		IntByReference bufferType = new IntByReference();
		
		try {			
			int rc = Netapi32.INSTANCE.NetGetJoinInformation(lpServer, lpNameBuffer, bufferType);
			if (Netapi32.NERR_Success != rc) {
				throw new LastErrorException(rc);			
			}		
			// type of domain: bufferType.getValue()
			return lpNameBuffer.getValue().getString(0, true);
		} finally {
			if (lpNameBuffer.getPointer() != null) {
				int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getPointer());
				if (Netapi32.NERR_Success != rc) {
					throw new LastErrorException(rc);			
				}
			}
		}
	}
}
