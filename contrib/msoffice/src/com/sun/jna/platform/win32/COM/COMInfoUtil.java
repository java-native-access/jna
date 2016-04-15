package com.sun.jna.platform.win32.COM;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.jna.platform.win32.COM.COMUtils.COMInfo;
import java.io.File;

public class COMInfoUtil {

	public static void main(String[] args) {
		FileWriter writer = null;
		try {
			String filename = new File(Helper.tempDir, "CLSIDs.txt").getAbsolutePath();
			ArrayList<COMInfo> comInfos = COMUtils.getAllCOMInfoOnSystem();
			writer = new FileWriter(filename);

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
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
