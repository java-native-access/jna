/* Copyright (c) 2014 Reinhard Pointner, All Rights Reserved
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
package com.sun.jna.platform.mac;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Memory;

public class XAttrUtil {

	public static List<String> listXAttr(String path) {
		// get required buffer size
		long bufferLength = XAttr.INSTANCE.listxattr(path, null, 0, 0);

		if (bufferLength < 0)
			return null;

		if (bufferLength == 0)
			return new ArrayList<String>(0);

		Memory valueBuffer = new Memory(bufferLength);
		long valueLength = XAttr.INSTANCE.listxattr(path, valueBuffer, bufferLength, 0);

		if (valueLength < 0)
			return null;

		return decodeStringSequence(valueBuffer.getByteBuffer(0, valueLength));
	}

	public static String getXAttr(String path, String name) {
		// get required buffer size
		long bufferLength = XAttr.INSTANCE.getxattr(path, name, null, 0, 0, 0);

		if (bufferLength < 0)
			return null;

		Memory valueBuffer = new Memory(bufferLength);
		long valueLength = XAttr.INSTANCE.getxattr(path, name, valueBuffer, bufferLength, 0, 0);

		if (valueLength < 0)
			return null;

		return decodeString(valueBuffer.getByteBuffer(0, valueLength - 1));
	}

	public static int setXAttr(String path, String name, String value) {
		Memory valueBuffer = encodeString(value);
		return XAttr.INSTANCE.setxattr(path, name, valueBuffer, valueBuffer.size(), 0, 0);
	}

	public static int removeXAttr(String path, String name) {
		return XAttr.INSTANCE.removexattr(path, name, 0);
	}

	protected static Memory encodeString(String s) {
		// create NULL-terminated UTF-8 String
		byte[] bb = s.getBytes(Charset.forName("UTF-8"));
		Memory valueBuffer = new Memory(bb.length + 1);
		valueBuffer.write(0, bb, 0, bb.length);
		valueBuffer.setByte(valueBuffer.size() - 1, (byte) 0);
		return valueBuffer;
	}

	protected static String decodeString(ByteBuffer bb) {
		return Charset.forName("UTF-8").decode(bb).toString();
	}

	protected static List<String> decodeStringSequence(ByteBuffer bb) {
		List<String> names = new ArrayList<String>();

		bb.mark(); // first key starts from here
		while (bb.hasRemaining()) {
			if (bb.get() == 0) {
				ByteBuffer nameBuffer = (ByteBuffer) bb.duplicate().limit(bb.position() - 1).reset();
				if (nameBuffer.hasRemaining()) {
					names.add(decodeString(nameBuffer));
				}
				bb.mark(); // next key starts from here
			}
		}

		return names;
	}

}
