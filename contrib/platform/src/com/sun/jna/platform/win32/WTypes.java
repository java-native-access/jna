/*
 * Copyright 2010 Digital Rapids Corp.
 */

/* Copyright (c) 2010 Timothy Wall, All Rights Reserved
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

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.USHORT;

/**
 * Constant defined in WTypes.h
 * 
 * @author scott.palmer
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */

public interface WTypes {

	public static int CLSCTX_INPROC_SERVER = 0x1;
	public static int CLSCTX_INPROC_HANDLER = 0x2;
	public static int CLSCTX_LOCAL_SERVER = 0x4;
	public static int CLSCTX_INPROC_SERVER16 = 0x8;
	public static int CLSCTX_REMOTE_SERVER = 0x10;
	public static int CLSCTX_INPROC_HANDLER16 = 0x20;
	public static int CLSCTX_RESERVED1 = 0x40;
	public static int CLSCTX_RESERVED2 = 0x80;
	public static int CLSCTX_RESERVED3 = 0x100;
	public static int CLSCTX_RESERVED4 = 0x200;
	public static int CLSCTX_NO_CODE_DOWNLOAD = 0x400;
	public static int CLSCTX_RESERVED5 = 0x800;
	public static int CLSCTX_NO_CUSTOM_MARSHAL = 0x1000;
	public static int CLSCTX_ENABLE_CODE_DOWNLOAD = 0x2000;
	public static int CLSCTX_NO_FAILURE_LOG = 0x4000;
	public static int CLSCTX_DISABLE_AAA = 0x8000;
	public static int CLSCTX_ENABLE_AAA = 0x10000;
	public static int CLSCTX_FROM_DEFAULT_CONTEXT = 0x20000;
	public static int CLSCTX_ACTIVATE_32_BIT_SERVER = 0x40000;
	public static int CLSCTX_ACTIVATE_64_BIT_SERVER = 0x80000;
	public static int CLSCTX_ENABLE_CLOAKING = 0x100000;
	public static int CLSCTX_APPCONTAINER = 0x400000;
	public static int CLSCTX_ACTIVATE_AAA_AS_IU = 0x800000;
	public static int CLSCTX_PS_DLL = 0x80000000;

	public static class VARTYPE extends USHORT {

		public VARTYPE() {
			this(0);
		}

		public VARTYPE(long value) {
			super(value);
		}
	}

	public static class BSTR extends Structure {

		public static class ByReference extends BSTR implements
				Structure.ByReference {
		}

		public WString string;

		public BSTR() {
		}

		public BSTR(String s) {
			if (s == null)
				throw new NullPointerException(
						"String initializer must be non-null");
			this.string = new WString(s);
		}

		public String toString() {
			return string.toString();
		}

		public boolean equals(Object o) {
			return o instanceof WString && toString().equals(o.toString());
		}

		public int hashCode() {
			return toString().hashCode();
		}

		public int compareTo(Object o) {
			return toString().compareTo(o.toString());
		}

		public int length() {
			return toString().length();
		}

		public char charAt(int index) {
			return toString().charAt(index);
		}

		public CharSequence subSequence(int start, int end) {
			return CharBuffer.wrap(toString()).subSequence(start, end);
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "string" });
		}
	}

	public class LPCOLESTR extends BSTR {

		public LPCOLESTR() {
			super("");
		}

		public LPCOLESTR(String str) {
			super(str);
		}
	}
}
