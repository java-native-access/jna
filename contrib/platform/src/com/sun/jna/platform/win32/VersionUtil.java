/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Version.dll utility API.
 * 
 * @author mlfreeman[at]gmail.com
 */
public class VersionUtil {

	/**
	 * Gets the file's version info
	 * 
	 * @param filePath
	 *            The path to the file
	 * @return Either an array of {major, minor, build, revision} or null if no
	 *         version info could be obtained.
	 */
	public static int[] getFileVersionDetails(String filePath) {
		IntByReference dwDummy = new IntByReference();
		dwDummy.setValue(0);

		int versionlength = Version.INSTANCE.GetFileVersionInfoSize(filePath, dwDummy);

		// no version info to read
		if (versionlength == 0) {
			return null;
		}

		byte[] bufferarray = new byte[versionlength];
		Pointer lpData = new Memory(bufferarray.length);
		PointerByReference lplpBuffer = new PointerByReference();
		IntByReference puLen = new IntByReference();

		if (Version.INSTANCE.GetFileVersionInfo(filePath, 0, versionlength, lpData)
				&& Version.INSTANCE.VerQueryValue(lpData, "\\", lplpBuffer, puLen)) {
			VS_FIXEDFILEINFO lplpBufStructure = new VS_FIXEDFILEINFO(lplpBuffer.getValue());
			lplpBufStructure.read();

			int[] v = new int[4];
			v[0] = lplpBufStructure.dwFileVersionMS.intValue() >> 16;
			v[1] = lplpBufStructure.dwFileVersionMS.intValue() & 0xffff;
			v[2] = lplpBufStructure.dwFileVersionLS.intValue() >> 16;
			v[3] = lplpBufStructure.dwFileVersionLS.intValue() & 0xffff;

			return v;
		}
		return null;
	}

}
