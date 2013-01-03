/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAYBOUND;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Oleaut32.dll Interface.
 * 
 * @author scott.palmer
 */
public interface OleAut32 extends StdCallLibrary {

	/* Flags for IDispatch::Invoke */
	public final static int DISPATCH_METHOD = 0x1;
	public final static int DISPATCH_PROPERTYGET = 0x2;
	public final static int DISPATCH_PROPERTYPUT = 0x4;
	public final static int DISPATCH_PROPERTYPUTREF = 0x8;

	OleAut32 INSTANCE = (OleAut32) Native.loadLibrary("OleAut32",
			OleAut32.class, W32APIOptions.UNICODE_OPTIONS);

	/**
	 * This function allocates a new string and copies the passed string into
	 * it.
	 * 
	 * @param sz
	 *            Null-terminated UNICODE string to copy.
	 * @return Null if there is insufficient memory or if a null pointer is
	 *         passed in.
	 */
	public BSTR SysAllocString(String sz);

	/**
	 * This function frees a string allocated previously by SysAllocString,
	 * SysAllocStringByteLen, SysReAllocString, SysAllocStringLen, or
	 * SysReAllocStringLen.
	 * 
	 * @param bstr
	 *            Unicode string that was allocated previously, or NULL. Setting
	 *            this parameter to NULL causes the function to simply return.
	 */
	public void SysFreeString(BSTR bstr);

	public void VariantInit(VARIANT.ByReference pvarg);

	public void VariantInit(VARIANT pvarg);

	public SAFEARRAY.ByReference SafeArrayCreate(VARTYPE vt, int cDims,
			SAFEARRAYBOUND[] rgsabound);

	public class DISPPARAMS extends Structure {

		public static class ByReference extends DISPPARAMS implements
				Structure.ByReference {
		}

		public VARIANT[] rgvarg = new VARIANT[1];
		public DISPID[] rgdispidNamedArgs = new DISPID[1];
		public int cArgs = 0;
		public int cNamedArgs = 0;

		public DISPPARAMS() {
		}

		public DISPPARAMS(Pointer memory) {
			super(memory);
			this.cArgs = (Integer) this.readField("cArgs");
			this.rgvarg = new VARIANT[cArgs];
			this.cNamedArgs = (Integer) this.readField("cNamedArgs");
			this.rgdispidNamedArgs = new DISPID[cNamedArgs];
			read();
		}

		public void writeFieldsToMemory() {
			this.writeField("rgvarg");
			this.writeField("rgdispidNamedArgs");
			this.writeField("cArgs");
			this.writeField("cNamedArgs");
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "rgvarg", "rgdispidNamedArgs",
					"cArgs", "cNamedArgs" });
		}
	}
}
