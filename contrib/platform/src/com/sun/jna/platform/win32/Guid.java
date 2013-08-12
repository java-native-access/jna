/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Ported from Guid.h. Microsoft Windows SDK 6.0A.
 *
 * @author dblock[at]dblock.org
 */
public interface Guid {
	
	public final static IID IID_NULL = new IID();
	
	/**
	 * The Class GUID.
	 */
	public static class GUID extends Structure {

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends GUID implements
				Structure.ByReference {

			/**
			 * Instantiates a new by reference.
			 */
			public ByReference() {
			}

			/**
			 * Instantiates a new by reference.
			 *
			 * @param guid
			 *            the guid
			 */
			public ByReference(GUID guid) {
				super(guid.getPointer());

				Data1 = guid.Data1;
				Data2 = guid.Data2;
				Data3 = guid.Data3;
				Data4 = guid.Data4;
			}

			/**
			 * Instantiates a new by reference.
			 *
			 * @param memory
			 *            the memory
			 */
			public ByReference(Pointer memory) {
				super(memory);
			}
		}

		/** The Data1. */
		public int Data1;

		/** The Data2. */
		public short Data2;

		/** The Data3. */
		public short Data3;

		/** The Data4. */
		public byte[] Data4 = new byte[8];

		/**
		 * Instantiates a new guid.
		 */
		public GUID() {
		}

		/**
		 * Instantiates a new guid.
		 *
		 * @param guid
		 *            the guid
		 */
		public GUID(GUID guid) {
			this.Data1 = guid.Data1;
			this.Data2 = guid.Data2;
			this.Data3 = guid.Data3;
			this.Data4 = guid.Data4;

			this.writeFieldsToMemory();
		}

		/**
		 * Instantiates a new guid.
		 *
		 * @param guid
		 *            the guid
		 */
		public GUID(String guid) {
			this(fromString(guid));
		}

		/**
		 * Instantiates a new guid.
		 *
		 * @param data
		 *            the data
		 */
		public GUID(byte[] data) {
			this(fromBinary(data));
		}

		/**
		 * Instantiates a new guid.
		 *
		 * @param memory
		 *            the memory
		 */
		public GUID(Pointer memory) {
			super(memory);
			read();
		}

		/**
		 * From binary.
		 *
		 * @param data
		 *            the data
		 * @return the guid
		 */
		public static GUID fromBinary(byte[] data) {
			if (data.length != 16) {
				throw new IllegalArgumentException("Invalid data length: "
						+ data.length);
			}

			GUID newGuid = new GUID();
			long data1Temp = data[0] & 0xff;
			data1Temp <<= 8;
			data1Temp |= data[1] & 0xff;
			data1Temp <<= 8;
			data1Temp |= data[2] & 0xff;
			data1Temp <<= 8;
			data1Temp |= data[3] & 0xff;
			newGuid.Data1 = (int) data1Temp;

			int data2Temp = data[4] & 0xff;
			data2Temp <<= 8;
			data2Temp |= data[5] & 0xff;
			newGuid.Data2 = (short) data2Temp;

			int data3Temp = data[6] & 0xff;
			data3Temp <<= 8;
			data3Temp |= data[7] & 0xff;
			newGuid.Data3 = (short) data3Temp;

			newGuid.Data4[0] = data[8];
			newGuid.Data4[1] = data[9];
			newGuid.Data4[2] = data[10];
			newGuid.Data4[3] = data[11];
			newGuid.Data4[4] = data[12];
			newGuid.Data4[5] = data[13];
			newGuid.Data4[6] = data[14];
			newGuid.Data4[7] = data[15];

			newGuid.writeFieldsToMemory();

			return newGuid;
		}

		/**
		 * From string.
		 *
		 * @param guid
		 *            the guid
		 * @return the guid
		 */
		public static GUID fromString(String guid) {
			int y = 0;
			char[] _cnewguid = new char[32];
			char[] _cguid = guid.toCharArray();
			byte[] bdata = new byte[16];
			GUID newGuid = new GUID();

			// we not accept a string longer than 38 chars
			if (guid.length() > 38) {
				throw new IllegalArgumentException("Invalid guid length: "
						+ guid.length());
			}

			// remove '{', '}' and '-' from guid string
			for (int i = 0; i < _cguid.length; i++) {
				if ((_cguid[i] != '{') && (_cguid[i] != '-')
						&& (_cguid[i] != '}'))
					_cnewguid[y++] = _cguid[i];
			}

			// convert char to byte
			for (int i = 0; i < 32; i += 2) {
				bdata[i / 2] = (byte) ((Character.digit(_cnewguid[i], 16) << 4)
						+ Character.digit(_cnewguid[i + 1], 16) & 0xff);
			}

			if (bdata.length != 16) {
				throw new IllegalArgumentException("Invalid data length: "
						+ bdata.length);
			}

			long data1Temp = bdata[0] & 0xff;
			data1Temp <<= 8;
			data1Temp |= bdata[1] & 0xff;
			data1Temp <<= 8;
			data1Temp |= bdata[2] & 0xff;
			data1Temp <<= 8;
			data1Temp |= bdata[3] & 0xff;
			newGuid.Data1 = (int) data1Temp;

			int data2Temp = bdata[4] & 0xff;
			data2Temp <<= 8;
			data2Temp |= bdata[5] & 0xff;
			newGuid.Data2 = (short) data2Temp;

			int data3Temp = bdata[6] & 0xff;
			data3Temp <<= 8;
			data3Temp |= bdata[7] & 0xff;
			newGuid.Data3 = (short) data3Temp;

			newGuid.Data4[0] = bdata[8];
			newGuid.Data4[1] = bdata[9];
			newGuid.Data4[2] = bdata[10];
			newGuid.Data4[3] = bdata[11];
			newGuid.Data4[4] = bdata[12];
			newGuid.Data4[5] = bdata[13];
			newGuid.Data4[6] = bdata[14];
			newGuid.Data4[7] = bdata[15];

			newGuid.writeFieldsToMemory();

			return newGuid;
		}

		/**
		 * Generates a new guid. Code taken from the standard jdk
		 * implementation (see UUID class).
		 *
		 * @return the guid
		 */
		public static GUID newGuid() {
			SecureRandom ng = new SecureRandom();
			byte[] randomBytes = new byte[16];

			ng.nextBytes(randomBytes);
			randomBytes[6] &= 0x0f;
			randomBytes[6] |= 0x40;
			randomBytes[8] &= 0x3f;
			randomBytes[8] |= 0x80;

			return new GUID(randomBytes);
		}

		/**
		 * To byte array.
		 * 
		 * @return the byte[]
		 */
		public byte[] toByteArray() {
			byte[] guid = new byte[16];

			byte[] bytes1 = new byte[4];
			bytes1[0] = (byte) (Data1 >> 24);
			bytes1[1] = (byte) (Data1 >> 16);
			bytes1[2] = (byte) (Data1 >> 8);
			bytes1[3] = (byte) (Data1 >> 0);

			byte[] bytes2 = new byte[4];
			bytes2[0] = (byte) (Data2 >> 24);
			bytes2[1] = (byte) (Data2 >> 16);
			bytes2[2] = (byte) (Data2 >> 8);
			bytes2[3] = (byte) (Data2 >> 0);

			byte[] bytes3 = new byte[4];
			bytes3[0] = (byte) (Data3 >> 24);
			bytes3[1] = (byte) (Data3 >> 16);
			bytes3[2] = (byte) (Data3 >> 8);
			bytes3[3] = (byte) (Data3 >> 0);

			System.arraycopy(bytes1, 0, guid, 0, 4);
			System.arraycopy(bytes2, 2, guid, 4, 2);
			System.arraycopy(bytes3, 2, guid, 6, 2);
			System.arraycopy(Data4, 0, guid, 8, 8);

			return guid;
		}

		/**
		 * The value of this Guid, formatted as follows:
		 * xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.
		 *
		 * @return the string
		 */
		public String toGuidString() {
			final String HEXES = "0123456789ABCDEF";
			byte[] bGuid = toByteArray();

			final StringBuilder hexStr = new StringBuilder(2 * bGuid.length);
			hexStr.append("{");

			for (int i = 0; i < bGuid.length; i++) {
				char ch1 = HEXES.charAt((bGuid[i] & 0xF0) >> 4);
				char ch2 = HEXES.charAt(bGuid[i] & 0x0F);
				hexStr.append(ch1).append(ch2);

				if ((i == 3) || (i == 5) || (i == 7) || (i == 9))
					hexStr.append("-");
			}

			hexStr.append("}");
			return hexStr.toString();
		}

		/**
		 * Write fields to backing memory.
		 */
		protected void writeFieldsToMemory() {
			this.writeField("Data1");
			this.writeField("Data2");
			this.writeField("Data3");
			this.writeField("Data4");
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sun.jna.Structure#getFieldOrder()
		 */
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "Data1", "Data2", "Data3",
					"Data4" });
		}
	}

	/**
	 * The Class CLSID.
	 */
	public static class CLSID extends GUID {

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends GUID {

			/**
			 * Instantiates a new by reference.
			 */
			public ByReference() {
			}

			/**
			 * Instantiates a new by reference.
			 * 
			 * @param guid
			 *            the guid
			 */
			public ByReference(GUID guid) {
				super(guid);
			}

			/**
			 * Instantiates a new by reference.
			 * 
			 * @param memory
			 *            the memory
			 */
			public ByReference(Pointer memory) {

			}
		}

		/**
		 * Instantiates a new clsid.
		 */
		public CLSID() {
		}
	}

	/**
	 * The Class REFIID.
	 */
	public class REFIID extends IID {

		/**
		 * Instantiates a new refiid.
		 */
		public REFIID() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Instantiates a new refiid.
		 * 
		 * @param memory
		 *            the memory
		 */
		public REFIID(Pointer memory) {
			super(memory);
			// TODO Auto-generated constructor stub
		}

		/**
		 * Instantiates a new refiid.
		 * 
		 * @param data
		 *            the data
		 */
		public REFIID(byte[] data) {
			super(data);
			// TODO Auto-generated constructor stub
		}

	}

	/**
	 * The Class IID.
	 */
	public class IID extends GUID {

		/**
		 * Instantiates a new iid.
		 */
		public IID() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Instantiates a new iid.
		 * 
		 * @param memory
		 *            the memory
		 */
		public IID(Pointer memory) {
			super(memory);
			// TODO Auto-generated constructor stub
		}

		public IID(String iid) {
			super(iid);
			// TODO Auto-generated constructor stub
		}

		/**
		 * Instantiates a new iid.
		 * 
		 * @param data
		 *            the data
		 */
		public IID(byte[] data) {
			super(data);
			// TODO Auto-generated constructor stub
		}
	}
}
