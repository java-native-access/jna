package com.sun.jna.platform.win32.office;

import com.sun.jna.platform.win32.COM.COMUtils;

public class COMUtil {

	public static void main(String[] args) {
		System.out.println("AllProgIDsOnSystem: " + COMUtils.getAllProgIDsOnSystem());
	}
}
