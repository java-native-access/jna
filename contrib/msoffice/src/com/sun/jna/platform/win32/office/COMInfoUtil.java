package com.sun.jna.platform.win32.office;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.COMUtils.COMInfo;

public class COMInfoUtil {

	public static void main(String[] args) {
		try {
			String filename = "C:\\TEMP\\CLSIDs.txt";
			ArrayList<COMInfo> comInfos = COMUtils.getAllCOMInfoOnSystem();
			FileWriter writer = new FileWriter(filename);

			for (COMInfo comInfo : comInfos) {
				String result = "CLSID: " + comInfo.clsid + "\n";
				result += "InprocHandler32: " + comInfo.inprocHandler32 + "\n";
				result += "InprocServer32: " + comInfo.inprocServer32 + "\n";
				result += "LocalServer32: " + comInfo.localServer32 + "\n";
				result += "ProgID: " + comInfo.progID + "\n";
				result += "ProgTypeLibID: " + comInfo.typeLib + "\n";

				writer.write(result + "\n");
			}

			System.out.println("file written to: " + filename);
			System.out.println("Found CLSID`s on the system: "
					+ comInfos.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
