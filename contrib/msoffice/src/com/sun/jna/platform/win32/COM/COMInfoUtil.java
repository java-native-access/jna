/*
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
