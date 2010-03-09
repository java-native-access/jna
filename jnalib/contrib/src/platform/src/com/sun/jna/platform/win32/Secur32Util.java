package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

/**
 * Secur32 Utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Secur32Util {

	/**
	 * Retrieves the name of the user or other security principal associated 
	 * with the calling thread.
	 * 
	 * @param format User name format.
	 * @return User name in a given format.
	 */
	public static String getUserNameEx(int format) {
		char[] buffer = new char[128];
		IntByReference len = new IntByReference(buffer.length);
		boolean result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len); 
		
		if (! result) {
			
			int rc = Kernel32.INSTANCE.GetLastError();

			switch(rc) {
			case W32Errors.ERROR_MORE_DATA:
				buffer = new char[len.getValue() + 1];
				break;
			default:
				throw new LastErrorException(Native.getLastError());
			}
			
			result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len);
		}
		
		if (! result) {
			throw new LastErrorException(Native.getLastError());
		}
		
		return Native.toString(buffer);		
	}
}
