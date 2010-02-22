package com.sun.jna.examples.win32.util;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.Secur32;
import com.sun.jna.examples.win32.W32Errors;
import com.sun.jna.ptr.IntByReference;

/**
 * Advapi32 API.
 */
public abstract class Secur32Util {

	/**
	 * Retrieves the name of the user or other security principal associated with the calling thread. 
	 * You can specify the format of the returned name.
	 * @return 
	 */
	public static String GetUserNameEx(int format) {
		char[] buffer = new char[128];
		IntByReference len = new IntByReference(buffer.length);
		boolean result = Secur32.INSTANCE.GetUserNameExW(format, buffer, len); 
		
		if (! result) {
			
			int rc = Kernel32.INSTANCE.GetLastError();

			switch(rc) {
			case W32Errors.ERROR_MORE_DATA:
				buffer = new char[len.getValue() + 1];
				break;
			default:
				throw new LastErrorException(Native.getLastError());
			}
			
			result = Secur32.INSTANCE.GetUserNameExW(format, buffer, len);
		}
		
		if (! result) {
			throw new LastErrorException(Native.getLastError());
		}
		
		return Native.toString(buffer);		
	}
}
