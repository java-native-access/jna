/* Copyright (c) 2016 Adam Marcionek, All Rights Reserved
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

	public static final int FSCTL_GET_COMPRESSION = CTL_CODE(
            Winioctl.FILE_DEVICE_FILE_SYSTEM,
            Winioctl.FSCTL_GET_COMPRESSION,
            Winioctl.METHOD_BUFFERED,
            Winioctl.FILE_ANY_ACCESS);

	public static final int FSCTL_SET_COMPRESSION = CTL_CODE(
	        Winioctl.FILE_DEVICE_FILE_SYSTEM,
	        Winioctl.FSCTL_SET_COMPRESSION,
	        Winioctl.METHOD_BUFFERED,
	        WinNT.FILE_READ_DATA | WinNT.FILE_WRITE_DATA);

    public static final int FSCTL_SET_REPARSE_POINT = CTL_CODE(
            Winioctl.FILE_DEVICE_FILE_SYSTEM,
            Winioctl.FSCTL_SET_REPARSE_POINT,
            Winioctl.METHOD_BUFFERED,
            Winioctl.FILE_SPECIAL_ACCESS);

    public static final int FSCTL_GET_REPARSE_POINT = CTL_CODE(
            Winioctl.FILE_DEVICE_FILE_SYSTEM,
            Winioctl.FSCTL_GET_REPARSE_POINT,
            Winioctl.METHOD_BUFFERED,
            Winioctl.FILE_ANY_ACCESS);

    public static final int FSCTL_DELETE_REPARSE_POINT = CTL_CODE(
            Winioctl.FILE_DEVICE_FILE_SYSTEM,
            Winioctl.FSCTL_DELETE_REPARSE_POINT,
            Winioctl.METHOD_BUFFERED,
            Winioctl.FILE_SPECIAL_ACCESS);
}