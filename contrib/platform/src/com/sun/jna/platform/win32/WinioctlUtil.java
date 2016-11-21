/* Copyright (c) 2016 Adam Marcionek, All Rights Reserved
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

/**
 * Winioctl Utility API. Use WinioctlFunction to construct the full control codes for the
 * FSCTL_* functions defined in Winioctl.h
 * 
 * @author amarcionek[at]gmail.com
 */
public abstract class WinioctlUtil {

	/**
	 * Simulates the macro CTL_CODE from Winioctl.h
	 * @param DeviceType the device type
	 * @param Function the function
	 * @param Method the method
	 * @param Access the access
	 * @return int with the resulting control code
	 */
	public static int CTL_CODE(int DeviceType, int Function, int Method, int Access) {
		return ((DeviceType) << 16) | ((Access) << 14) | ((Function) << 2) | (Method);
	}

	/**
	 * Base interface for a Winiotcl function used to construct the control code
	 */
	public interface WinioctlFunction {
		/**
		 * @return the control code given the IOTCL's parameters
		 */
		public abstract int getControlCode();
	}

	public static class FSCTL_GET_COMPRESSION implements WinioctlFunction {
		public int getControlCode() {
			return WinioctlUtil.CTL_CODE(Winioctl.FILE_DEVICE_FILE_SYSTEM, Winioctl.FSCTL_GET_COMPRESSION,  Winioctl.METHOD_BUFFERED, Winioctl.FILE_ANY_ACCESS);
		}
	}

	public static class FSCTL_SET_COMPRESSION implements WinioctlFunction {
		public int getControlCode() {
			return WinioctlUtil.CTL_CODE(Winioctl.FILE_DEVICE_FILE_SYSTEM, Winioctl.FSCTL_SET_COMPRESSION,  Winioctl.METHOD_BUFFERED, WinNT.FILE_READ_DATA | WinNT.FILE_WRITE_DATA);
		}
	}

	public static class FSCTL_SET_REPARSE_POINT implements WinioctlFunction {
		public int getControlCode() {
			return WinioctlUtil.CTL_CODE(Winioctl.FILE_DEVICE_FILE_SYSTEM, Winioctl.FSCTL_SET_REPARSE_POINT,  Winioctl.METHOD_BUFFERED, Winioctl.FILE_SPECIAL_ACCESS);
		}
	}

	public static class FSCTL_GET_REPARSE_POINT implements WinioctlFunction {
		public int getControlCode() {
			return WinioctlUtil.CTL_CODE(Winioctl.FILE_DEVICE_FILE_SYSTEM, Winioctl.FSCTL_GET_REPARSE_POINT,  Winioctl.METHOD_BUFFERED, Winioctl.FILE_ANY_ACCESS);
		}
	}

	public static class FSCTL_DELETE_REPARSE_POINT implements WinioctlFunction {
		public int getControlCode() {
			return WinioctlUtil.CTL_CODE(Winioctl.FILE_DEVICE_FILE_SYSTEM, Winioctl.FSCTL_DELETE_REPARSE_POINT,  Winioctl.METHOD_BUFFERED, Winioctl.FILE_SPECIAL_ACCESS);
		}
	}
}