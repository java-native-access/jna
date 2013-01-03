package com.sun.jna.platform.win32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.OaIdl.CURRENCY;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.DECIMAL;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OaIdl._VARIANT_BOOL;
import com.sun.jna.platform.win32.Variant.VARIANT._VARIANT.__VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.WinDef.SHORT;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IRecordInfo;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.ShortByReference;

public interface Variant {

	public static VARTYPE VT_EMPTY = new VARTYPE(0);
	public static VARTYPE VT_NULL = new VARTYPE(1);
	public static VARTYPE VT_I2 = new VARTYPE(2);
	public static VARTYPE VT_I4 = new VARTYPE(3);
	public static VARTYPE VT_R4 = new VARTYPE(4);
	public static VARTYPE VT_R8 = new VARTYPE(5);
	public static VARTYPE VT_CY = new VARTYPE(6);
	public static VARTYPE VT_DATE = new VARTYPE(7);
	public static VARTYPE VT_BSTR = new VARTYPE(8);
	public static VARTYPE VT_DISPATCH = new VARTYPE(9);
	public static VARTYPE VT_ERROR = new VARTYPE(10);
	public static VARTYPE VT_BOOL = new VARTYPE(11);
	public static VARTYPE VT_VARIANT = new VARTYPE(12);
	public static VARTYPE VT_UNKNOWN = new VARTYPE(13);
	public static VARTYPE VT_DECIMAL = new VARTYPE(14);
	public static VARTYPE VT_I1 = new VARTYPE(16);
	public static VARTYPE VT_UI1 = new VARTYPE(17);
	public static VARTYPE VT_UI2 = new VARTYPE(18);
	public static VARTYPE VT_UI4 = new VARTYPE(19);
	public static VARTYPE VT_I8 = new VARTYPE(20);
	public static VARTYPE VT_UI8 = new VARTYPE(21);
	public static VARTYPE VT_VARTYPE = new VARTYPE(22);
	public static VARTYPE VT_UVARTYPE = new VARTYPE(23);
	public static VARTYPE VT_VOID = new VARTYPE(24);
	public static VARTYPE VT_HRESULT = new VARTYPE(25);
	public static VARTYPE VT_PTR = new VARTYPE(26);
	public static VARTYPE VT_SAFEARRAY = new VARTYPE(27);
	public static VARTYPE VT_CARRAY = new VARTYPE(28);
	public static VARTYPE VT_USERDEFINED = new VARTYPE(29);
	public static VARTYPE VT_LPSTR = new VARTYPE(30);
	public static VARTYPE VT_LPWSTR = new VARTYPE(31);
	public static VARTYPE VT_RECORD = new VARTYPE(36);
	public static VARTYPE VT_VARTYPE_PTR = new VARTYPE(37);
	public static VARTYPE VT_UVARTYPE_PTR = new VARTYPE(38);
	public static VARTYPE VT_FILETIME = new VARTYPE(64);
	public static VARTYPE VT_BLOB = new VARTYPE(65);
	public static VARTYPE VT_STREAM = new VARTYPE(66);
	public static VARTYPE VT_STORAGE = new VARTYPE(67);
	public static VARTYPE VT_STREAMED_OBJECT = new VARTYPE(68);
	public static VARTYPE VT_STORED_OBJECT = new VARTYPE(69);
	public static VARTYPE VT_BLOB_OBJECT = new VARTYPE(70);
	public static VARTYPE VT_CF = new VARTYPE(71);
	public static VARTYPE VT_CLSID = new VARTYPE(72);
	public static VARTYPE VT_VERSIONED_STREAM = new VARTYPE(73);
	public static VARTYPE VT_BSTR_BLOB = new VARTYPE(0xfff);
	public static VARTYPE VT_VECTOR = new VARTYPE(0x1000);
	public static VARTYPE VT_ARRAY = new VARTYPE(0x2000);
	public static VARTYPE VT_BYREF = new VARTYPE(0x4000);
	public static VARTYPE VT_RESERVED = new VARTYPE(0x8000);
	public static VARTYPE VT_ILLEGAL = new VARTYPE(0xffff);
	public static VARTYPE VT_ILLEGALMASKED = new VARTYPE(0xfff);
	public static VARTYPE VT_TYPEMASK = new VARTYPE(0xfff);

	public static VARIANT_BOOL VARIANT_TRUE = new VARIANT_BOOL(0xFFFF);
	public static VARIANT_BOOL VARIANT_FALSE = new VARIANT_BOOL(0x0000);

	public static class VARIANT extends Union {

		public static class ByReference extends VARIANT implements
				Structure.ByReference {

			public ByReference() {
			}

			public ByReference(VARIANT variant) {
				super(Pointer.SIZE);
				setValue(variant);
			}

			public void setValue(VARIANT variant) {
				getPointer().setPointer(0,
						variant != null ? variant.getPointer() : null);
			}

			public VARIANT getValue() {
				Pointer p = getPointer().getPointer(0);
				if (p == null)
					return null;
				VARIANT variant = new VARIANT(p);
				return variant;
			}
		}

		public _VARIANT _variant = new _VARIANT();
		public DECIMAL decVal;

		public VARIANT() {
			this.setType(_VARIANT.class);
		}

		public VARIANT(VARTYPE vt) {
			this();
			this._variant = new _VARIANT(vt);
		}

		public VARIANT(VARTYPE vt, __VARIANT __variant) {
			this();
			this._variant = new _VARIANT(vt, __variant);
		}

		public VARIANT(int pointer) {
			super(new Pointer(pointer));
		}

		public VARIANT(Pointer pointer) {
			super(pointer);
		}

		public VARIANT(BSTR str) {
			this.setBSTR(str);
		}

		public VARIANT(VARIANT_BOOL boolVal) {
			this.setBoolVal(boolVal);
		}

		public VARTYPE getVarType() {
			return _variant.vt;
		}

		public void setVarType(VARTYPE vt) {
			this._variant.vt = vt;
		}

		public VARIANT_BOOL getBoolVal() {
			return this._variant.__variant.boolVal;
		}

		public void setBoolVal(VARIANT_BOOL boolVal) {
			this.setVarType(VT_BOOL);
			this._variant.__variant.boolVal = boolVal;
		}

		public void setBSTR(BSTR str) {
			this._variant.vt = VT_BSTR;
			this._variant.__variant.bstrVal = str;
		}

		public BSTR getBSTR() {
			return this._variant.__variant.bstrVal;
		}

		public void setDispVal(IDispatch pdispVal) {
			this._variant.vt = VT_DISPATCH;
			this._variant.__variant.pdispVal = pdispVal;
		}

		public IDispatch getDispVal() {
			return this._variant.__variant.pdispVal;
		}

		public void setVariant(VARIANT.ByReference variant) {
			this._variant.vt = VT_VARIANT;
			this._variant.__variant.pvarVal = variant;
		}

		public SAFEARRAY.ByReference getSAFEARRAY() {
			return this._variant.__variant.pparray;
		}

		public void setSAFEARRAY(SAFEARRAY.ByReference variantArg) {
			this._variant.vt = VT_ARRAY;
			this._variant.__variant.pparray = variantArg;
		}

		public VARIANT.ByReference getVariant() {
			return this._variant.__variant.pvarVal;
		}

		public void setI4(int value) {
			this._variant.vt = VT_I4;
			this._variant.__variant.iVal = value;
		}

		public int getI4() {
			return this._variant.__variant.iVal;
		}

		public static class _VARIANT extends Structure {

			public VARTYPE vt;
			public short wReserved1;
			public short wReserved2;
			public short wReserved3;
			public __VARIANT __variant;
			public BRECORD bRecord;

			public _VARIANT() {
			}

			public _VARIANT(VARTYPE vt) {
				this.vt = vt;
			}

			public _VARIANT(VARTYPE vt, __VARIANT __variant) {
				this.vt = vt;
				this.__variant = __variant;
			}

			public static class __VARIANT extends Union {
				public Long llVal;
				public NativeLong lVal;
				public Byte bVal;
				public Integer iVal;
				public Float fltVal;
				public Double dblVal;
				// / C type : VARIANT_BOOL
				public VARIANT_BOOL boolVal;
				// / C type : _VARIANT_BOOL
				public _VARIANT_BOOL bool;
				// / C type : SCODE
				public SCODE scode;
				// / C type : CY
				public CURRENCY cyVal;
				// / C type : DATE
				public DATE date;
				// / C type : BSTR
				public BSTR bstrVal;
				// / C type : IUnknown*
				public IUnknown punkVal;
				// / C type : IDispatch*
				public IDispatch pdispVal;
				// / C type : SAFEARRAY*
				public SAFEARRAY parray;
				// / C type : BYTE*
				public Pointer pbVal;
				// / C type : short*
				public ShortByReference piVal;
				// / C type : long*
				public NativeLongByReference plVal;
				// / C type : LONGLONG*
				public LongByReference pllVal;
				// / C type : FLOAT*
				public FloatByReference pfltVal;
				// / C type : DOUBLE*
				public DoubleByReference pdblVal;
				// / C type : VARIANT_BOOL*
				public VARIANT_BOOL pboolVal;
				// / C type : _VARIANT_BOOL*
				public _VARIANT_BOOL pbool;
				// / C type : SCODE*
				public SCODE pscode;
				// / C type : CY*
				public CURRENCY pcyVal;
				// / C type : DATE*
				public DATE pdate;
				// / C type : BSTR*
				public BSTR pbstrVal;
				// / C type : IUnknown**
				public IUnknown[] ppunkVal = new IUnknown[1];
				// / C type : IDispatch**
				public IDispatch[] ppdispVal = new IDispatch[1];
				// / C type : SAFEARRAY**
				public SAFEARRAY.ByReference pparray;
				// / C type : VARIANT*
				public VARIANT.ByReference pvarVal;
				// / C type : PVOID
				public PVOID byref;
				// / C type : CHAR
				public CHAR cVal;
				public SHORT uiVal;
				public NativeLong ulVal;
				public long ullVal;
				public Integer intVal;
				public int uintVal;
				// / C type : DECIMAL*
				public DECIMAL pdecVal;
				// / C type : CHAR*
				public Character pcVal;
				// / C type : USHORT*
				public ShortByReference puiVal;
				// / C type : ULONG*
				public NativeLongByReference pulVal;
				// / C type : ULONGLONG*
				public LongByReference pullVal;
				// / C type : INT*
				public IntByReference pintVal;
				// / C type : UINT*
				public IntByReference puintVal;

				public __VARIANT() {
					this.setType(Integer.class);
				}
			}

			public static class BRECORD extends Structure {
				public PVOID pvRecord;
				public IRecordInfo pRecInfo;

				@Override
				protected List getFieldOrder() {
					return Arrays
							.asList(new String[] { "pvRecord", "pRecInfo" });
				}
			}

			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] { "vt", "wReserved1",
						"wReserved2", "wReserved3", "__variant", "bRecord" });
			}
		}
	}
}
