package com.sun.jna.platform.win32.office;

import java.util.ArrayList;

import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.COMUtils.COMInfo;

public class COMInfoUtil {

	public static void main(String[] args) {
		ArrayList<COMInfo> comInfos = COMUtils.getAllCOMInfoOnSystem();

		for (COMInfo comInfo : comInfos) {
			String result = "CLSID: " + comInfo.clsid + "\n";
			result += "InprocHandler32: " + comInfo.inprocHandler32 + "\n";
			result += "InprocServer32: " + comInfo.inprocServer32 + "\n";
			result += "LocalServer32: " + comInfo.localServer32 + "\n";
			result += "ProgID: " + comInfo.progID + "\n";
			result += "ProgTypeLibID: " + comInfo.typeLib + "\n";

			System.out.println(result + "\n");
		}

		System.out.println("Found CLSID`s on the system: " + comInfos.size());
	}
}
