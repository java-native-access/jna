package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.HWND;

public class GuidTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(GuidTest.class);
	}

	public GuidTest() {
	}

	public void testGuid() {
		System.out.println("ref. guid          : " + "{A5DCBF10-6530-11D2-901F-00C04FB951ED}");
		GUID guid = Ole32Util.getGUIDFromString("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");
		System.out.println("getGUIDFromString(): " + guid.toGuidString());
		
		guid = new GUID("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");
		byte[] byteArray = guid.toByteArray();
		guid = new GUID(byteArray);
		System.out.println("toGuidString()     : " + guid.toGuidString());
		
		guid = new GUID("{A5DCBF10-6530-11D2-901F-00C04FB951ED}");
		System.out.println("getStringFromGUID  : " + Ole32Util.getStringFromGUID(guid));
	}
}
