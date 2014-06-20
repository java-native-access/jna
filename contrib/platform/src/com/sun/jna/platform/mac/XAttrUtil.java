/* Copyright (c) 2014 Reinhard Pointner, All Rights Reserved
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
