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
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

// TODO: Auto-generated Javadoc
/**
 * Oleaut32.dll Interface.
 *
 * @author scott.palmer
 */
public interface OleAut32 extends StdCallLibrary {

	/* Flags for IDispatch::Invoke */
	/** The Constant DISPATCH_METHOD. */
	public final static int DISPATCH_METHOD = 0x1;

	/** The Constant DISPATCH_PROPERTYGET. */
	public final static int DISPATCH_PROPERTYGET = 0x2;

	/** The Constant DISPATCH_PROPERTYPUT. */
	public final static int DISPATCH_PROPERTYPUT = 0x4;

	/** The Constant DISPATCH_PROPERTYPUTREF. */
	public final static int DISPATCH_PROPERTYPUTREF = 0x8;

	/** An array that is allocated on the stac. */
	public final static int FADF_AUTO = 0x0001;

	/** An array that is statically allocated. */
	public final static int FADF_STATIC = 0x0002;

	/** An array that is embedded in a structure. */
	public final static int FADF_EMBEDDED = 0x0004;

	/** An array that is embedded in a structure. */
	public final static int FADF_FIXEDSIZE = 0x0010;

	/** An array that is embedded in a structure. */
	public final static int FADF_RECORD = 0x0020;

	/** An array that is embedded in a structure. */
	public final static int FADF_HAVEIID = 0x0040;

	/**
	 * An array that has a variant type. The variant type can be retrieved with
	 * SafeArrayGetVartype.
	 */
	public final static int FADF_HAVEVARTYPE = 0x0080;

	/** An array of BSTRs. */
	public final static int FADF_BSTR = 0x0100;

	/** An array of IUnknown*. */
	public final static int FADF_UNKNOWN = 0x0200;

	/** An array of IDispatch*. */
	public final static int FADF_DISPATCH = 0x0400;

	/** An array of VARIANTs. */
	public final static int FADF_VARIANT = 0x0800;

	/** Bits reserved for future use. */
	public final static int FADF_RESERVED = 0xF008;

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

	/**
	 * Variant init.
	 *
	 * @param pvarg
	 *            the pvarg
	 */
	public void VariantInit(VARIANT.ByReference pvarg);

	/**
	 * Variant init.
	 *
	 * @param pvarg
	 *            the pvarg
	 */
	public void VariantInit(VARIANT pvarg);

	HRESULT VariantCopy(Pointer pvargDest, VARIANT pvargSrc);

	HRESULT VariantClear(Pointer pvarg);

	/**
	 * Safe array create.
	 *
	 * @param vt
	 *            the vt
	 * @param cDims
	 *            the c dims
	 * @param rgsabound
	 *            the rgsabound
	 * @return the safearray
	 */
	public SAFEARRAY.ByReference SafeArrayCreate(VARTYPE vt, int cDims,
			SAFEARRAYBOUND[] rgsabound);

	/**
	 * Safe array put element.
	 *
	 * @param psa
	 *            the psa
	 * @param idx
	 *            the idx
	 * @param pv
	 *            the pv
	 * @return the hresult
	 */
	public HRESULT SafeArrayPutElement(SAFEARRAY psa, long[] idx, VARIANT pv);

	/**
	 * Safe array get element.
	 *
	 * @param psa
	 *            the psa
	 * @param rgIndices
	 *            the rg indices
	 * @param pv
	 *            the pv
	 * @return the hresult
	 */
	public HRESULT SafeArrayGetElement(SAFEARRAY psa, long[] rgIndices,
			Pointer pv);

	public HRESULT SafeArrayLock(SAFEARRAY psa);

	public HRESULT SafeArrayUnLock(SAFEARRAY psa);

	/**
	 * The Class DISPPARAMS.
	 */
	public class DISPPARAMS extends Structure {

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends DISPPARAMS implements
				Structure.ByReference {
		}

		/** The rgvarg. */
		public SAFEARRAY.ByReference rgvarg;

		/** The rgdispid named args. */
		public DISPID rgdispidNamedArgs;

		/** The c args. */
		public UINT cArgs;

		/** The c named args. */
		public UINT cNamedArgs;

		/**
		 * Instantiates a new dispparams.
		 */
		public DISPPARAMS() {
			super();
		}

		/**
		 * Instantiates a new dispparams.
		 *
		 * @param memory
		 *            the memory
		 */
		public DISPPARAMS(Pointer memory) {
			super(memory);
			this.read();
		}

		public DISPPARAMS(SAFEARRAY.ByReference rgvarg,
				DISPID rgdispidNamedArgs, int cArgs, int cNamedArgs) {
			this();
			this.rgvarg = rgvarg;
			this.rgdispidNamedArgs = rgdispidNamedArgs;
			this.cArgs = new UINT(cArgs);
			this.cNamedArgs = new UINT(cNamedArgs);
			this.write();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sun.jna.Structure#getFieldOrder()
		 */
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "rgvarg", "rgdispidNamedArgs",
					"cArgs", "cNamedArgs" });
		}
	}
}
