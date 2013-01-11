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
import com.sun.jna.platform.win32.WTypes.BSTR;
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

	public static int VT_EMPTY = 0;
	public static int VT_NULL = 1;
	public static int VT_I2 = 2;
	public static int VT_I4 = 3;
	public static int VT_R4 = 4;
	public static int VT_R8 = 5;
	public static int VT_CY = 6;
	public static int VT_DATE = 7;
	public static int VT_BSTR = 8;
	public static int VT_DISPATCH = 9;
	public static int VT_ERROR = 10;
	public static int VT_BOOL = 11;
	public static int VT_VARIANT = 12;
	public static int VT_UNKNOWN = 13;
	public static int VT_DECIMAL = 14;
	public static int VT_I1 = 16;
	public static int VT_UI1 = 17;
	public static int VT_UI2 = 18;
	public static int VT_UI4 = 19;
	public static int VT_I8 = 20;
	public static int VT_UI8 = 21;
	public static int VT_int = 22;
	public static int VT_Uint = 23;
	public static int VT_VOID = 24;
	public static int VT_HRESULT = 25;
	public static int VT_PTR = 26;
	public static int VT_SAFEARRAY = 27;
	public static int VT_CARRAY = 28;
	public static int VT_USERDEFINED = 29;
	public static int VT_LPSTR = 30;
	public static int VT_LPWSTR = 31;
	public static int VT_RECORD = 36;
	public static int VT_int_PTR = 37;
	public static int VT_Uint_PTR = 38;
	public static int VT_FILETIME = 64;
	public static int VT_BLOB = 65;
	public static int VT_STREAM = 66;
	public static int VT_STORAGE = 67;
	public static int VT_STREAMED_OBJECT = 68;
	public static int VT_STORED_OBJECT = 69;
	public static int VT_BLOB_OBJECT = 70;
	public static int VT_CF = 71;
	public static int VT_CLSID = 72;
	public static int VT_VERSIONED_STREAM = 73;
	public static int VT_BSTR_BLOB = 0xfff;
	public static int VT_VECTOR = 0x1000;
	public static int VT_ARRAY = 0x2000;
	public static int VT_BYREF = 0x4000;
	public static int VT_RESERVED = 0x8000;
	public static int VT_ILLEGAL = 0xffff;
	public static int VT_ILLEGALMASKED = 0xfff;
	public static int VT_TYPEMASK = 0xfff;

	public static VARIANT_BOOL VARIANT_TRUE = new VARIANT_BOOL(0xFFFF);
	public static VARIANT_BOOL VARIANT_FALSE = new VARIANT_BOOL(0x0000);

	public static class VARIANT extends Union {

		public static class ByReference extends VARIANT implements
				Structure.ByReference {
		}

		public _VARIANT _variant = new _VARIANT();

		public DECIMAL decVal;

		public VARIANT() {
			this.setType("_variant");
		}

		public VARIANT(long pointer) {
			super(new Pointer(pointer));
			this.setType("_variant");
		}

		public VARIANT(Pointer pointer) {
			super(pointer);
			this.setType("_variant");
		}

		public VARIANT(BSTR value) {
			this();
			this.setValue(VT_BSTR, value);
		}

		public VARIANT(VARIANT_BOOL value) {
			this();
			this.setValue(VT_BOOL, value);
		}

		public VARIANT(int value) {
			this();
			this.setValue(VT_I4, value);
		}

		public int getVarType() {
			this.read();
			return _variant.vt;
		}

		public void setVarType(int vt) {
			this._variant.vt = vt;
		}

		public void setValue(int vt, Object value) {
			switch (vt) {
			case VT_I4:
				this._variant.__variant.writeField("iVal", value);
				break;
			case VT_BSTR:
				this._variant.__variant.writeField("bstrVal", value);
				break;
			case VT_DISPATCH:
				this._variant.__variant.writeField("pdispVal", value);
				break;
			case VT_BOOL:
				this._variant.__variant.writeField("boolVal", value);
				break;
			case VT_SAFEARRAY:
				this._variant.__variant.writeField("parray", value);
				break;
			case VT_ARRAY:
				this._variant.__variant.writeField("parray", value);
				break;
			}

			this._variant.writeField("vt", vt);
			this.write();
		}

		public Object getValue() {
			this.read();

			switch (this.getVarType()) {
			case VT_I4:
				return this._variant.__variant.readField("iVal");
			case VT_BSTR:
				return this._variant.__variant.readField("bstrVal");
			case VT_DISPATCH:
				return this._variant.__variant.readField("pdispVal");
			case VT_BOOL:
				return this._variant.__variant.readField("boolVal");
			case VT_SAFEARRAY:
				return this._variant.__variant.readField("parray");
			default:
				return null;
			}
		}

		public static class _VARIANT extends Structure {

			public int vt;
			public short wReserved1;
			public short wReserved2;
			public short wReserved3;
			public __VARIANT __variant = new __VARIANT();
			public BRECORD bRecord;

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
				public IUnknown.ByReference ppunkVal;
				// / C type : IDispatch**
				public IDispatch.ByReference ppdispVal;
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
				public Long ullVal;
				public Integer intVal;
				public Integer uintVal;
				// / C type : DECIMAL*
				public DECIMAL.ByReference pdecVal;
				// / C type : CHAR*
				public CHAR.ByReference pcVal;
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
					this.setType("iVal");
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
