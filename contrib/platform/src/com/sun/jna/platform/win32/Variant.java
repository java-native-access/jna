package com.sun.jna.platform.win32;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.sun.jna.IntegerType;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.OaIdl.CURRENCY;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.DECIMAL;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOLByReference;
import com.sun.jna.platform.win32.OaIdl._VARIANT_BOOLByReference;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.CHARByReference;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGByReference;
import com.sun.jna.platform.win32.WinDef.LONGLONG;
import com.sun.jna.platform.win32.WinDef.LONGLONGByReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.WinDef.SCODEByReference;
import com.sun.jna.platform.win32.WinDef.SHORT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinDef.ULONGLONG;
import com.sun.jna.platform.win32.WinDef.ULONGLONGByReference;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinDef.USHORTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IRecordInfo;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
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
    public static int VT_INT = 22;
    public static int VT_UINT = 23;
    public static int VT_VOID = 24;
    public static int VT_HRESULT = 25;
    public static int VT_PTR = 26;
    public static int VT_SAFEARRAY = 27;
    public static int VT_CARRAY = 28;
    public static int VT_USERDEFINED = 29;
    public static int VT_LPSTR = 30;
    public static int VT_LPWSTR = 31;
    public static int VT_RECORD = 36;
    public static int VT_INT_PTR = 37;
    public static int VT_UINT_PTR = 38;
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

    public final static long COM_DAYS_ADJUSTMENT = 25569L; // ((1969 - 1899) *
                                                           // 365) +1 + Leap
                                                           // years = Days
    public final static long MICRO_SECONDS_PER_DAY = 86400000L; // 24L * 60L *
                                                                // 60L * 1000L;

    public static class VARIANT extends Union {

        public static class ByReference extends VARIANT implements
                Structure.ByReference {
        }

        public _VARIANT _variant;

        public DECIMAL decVal;

        public VARIANT() {
            this.setType("_variant");
            this.read();
        }

        public VARIANT(Pointer pointer) {
            super(pointer);
            this.setType("_variant");
            this.read();
        }

        public VARIANT(BSTR value) {
            this();
            this.setValue(VT_BSTR, value);
        }

        public VARIANT(BSTRByReference value) {
            this();
            this.setValue(VT_BYREF | VT_BSTR, value);
        }

        public VARIANT(VARIANT_BOOL value) {
            this();
            this.setValue(VT_BOOL, new BOOL(value.intValue()));
        }

        public VARIANT(BOOL value) {
            this();
            this.setValue(VT_BOOL, value);
        }

        public VARIANT(BYTE value) {
            this();
            this.setValue(VT_UI1, value);
        }

        public VARIANT(WORD value) {
            this();
            this.setValue(VT_UI2, value);
        }

        public VARIANT(DWORD value) {
            this();
            this.setValue(VT_UI4, value);
        }

        public VARIANT(ULONGLONG value) {
            this();
            this.setValue(VT_UI8, value);
        }

        public VARIANT(CHAR value) {
            this();
            this.setValue(VT_I1, value);
        }

        public VARIANT(SHORT value) {
            this();
            this.setValue(VT_I2, value);
        }

        public VARIANT(LONG value) {
            this();
            this.setValue(VT_I4, value);
        }

        public VARIANT(LONGLONG value) {
            this();
            this.setValue(VT_I8, value);
        }

        public VARIANT(DATE value) {
            this();
            this.setValue(VT_DATE, value);
        }

        public VARIANT(byte value) {
            this();
            this.setValue(VT_I1, value);
        }

        public VARIANT(short value) {
            this();
            this.setValue(VT_I2, value);
        }

        public VARIANT(int value) {
            this();
            this.setValue(VT_I4, value);
        }

        public VARIANT(long value) {
            this();
            this.setValue(VT_I8, value);
        }

        public VARIANT(float value) {
            this();
            this.setValue(VT_R4, value);
        }

        public VARIANT(double value) {
            this();
            this.setValue(VT_R8, value);
        }

        public VARIANT(String value) {
            this();
            BSTR bstrValue = OleAuto.INSTANCE.SysAllocString(value);
            this.setValue(VT_BSTR, bstrValue);
        }

        public VARIANT(boolean value) {
            this();
            if (value)
                this.setValue(VT_BOOL, new BOOL(VARIANT_TRUE.intValue()));
            else
                this.setValue(VT_BOOL, new BOOL(VARIANT_FALSE.intValue()));
        }

        public VARIANT(IDispatch value) {
            this();
            this.setValue(Variant.VT_DISPATCH, value);
        }

        public VARIANT(Date value) {
            this();
            DATE date = this.fromJavaDate(value);
            this.setValue(VT_DATE, date);
        }

        public VARIANT(CURRENCY value) {
            this();
            this.setValue(VT_CY, value);
        }

        public VARTYPE getVarType() {
            this.read();
            return _variant.vt;
        }

        public void setVarType(short vt) {
            this._variant.vt = new VARTYPE(vt);
        }

        public void setValue(int vt, Object value) {
            this.setValue(new VARTYPE(vt), value);
        }

        public void setValue(VARTYPE vt, Object value) {
            switch (vt.intValue()) {
            case VT_I1:
                this._variant.__variant.writeField("cVal", value);
                break;
            case VT_I2:
                this._variant.__variant.writeField("iVal", value);
                break;
            case VT_INT:
                this._variant.__variant.writeField("intVal", value);
                break;
            case VT_I4:
                this._variant.__variant.writeField("lVal", value);
                break;
            case VT_I8:
                this._variant.__variant.writeField("llVal", value);
                break;
            case VT_UI1:
                this._variant.__variant.writeField("bVal", value);
                break;
            case VT_UI2:
                this._variant.__variant.writeField("uiVal", value);
                break;
            case VT_UINT:
                this._variant.__variant.writeField("uintVal", value);
                break;
            case VT_UI4:
                this._variant.__variant.writeField("ulVal", value);
                break;
            case VT_UI8:
                this._variant.__variant.writeField("ullVal", value);
                break;
            case VT_R4:
                this._variant.__variant.writeField("fltVal", value);
                break;
            case VT_R8:
                this._variant.__variant.writeField("dblVal", value);
                break;
            case VT_BOOL:
                this._variant.__variant.writeField("boolVal", value);
                break;
            case VT_HRESULT:
            case VT_ERROR:
                this._variant.__variant.writeField("scode", value);
                break;
            case VT_CY:
                this._variant.__variant.writeField("cyVal", value);
                break;
            case VT_DATE:
                this._variant.__variant.writeField("date", value);
                break;
            case VT_BSTR:
                this._variant.__variant.writeField("bstrVal", value);
                break;
            case VT_UNKNOWN:
                this._variant.__variant.writeField("punkVal", value);
                break;
            case VT_DISPATCH:
                this._variant.__variant.writeField("pdispVal", value);
                break;
            case VT_SAFEARRAY:
                this._variant.__variant.writeField("parray", value);
                break;
            case VT_ARRAY:
                this._variant.__variant.writeField("parray", value);
                break;
            case VT_BYREF:
                this._variant.__variant.writeField("byref", value);
                break;
            case VT_BYREF | VT_I1:
                this._variant.__variant.writeField("pcVal", value);
                break;
            case VT_BYREF | VT_I2:
                this._variant.__variant.writeField("piVal", value);
                break;
            case VT_BYREF | VT_INT:
                this._variant.__variant.writeField("pintVal", value);
                break;
            case VT_BYREF | VT_I4:
                this._variant.__variant.writeField("plVal", value);
                break;
            case VT_BYREF | VT_I8:
                this._variant.__variant.writeField("pllVal", value);
                break;
            case VT_BYREF | VT_UI1:
                this._variant.__variant.writeField("pbVal", value);
                break;
            case VT_BYREF | VT_UI2:
                this._variant.__variant.writeField("puiVal", value);
                break;
            case VT_BYREF | VT_UINT:
                this._variant.__variant.writeField("puintVal", value);
                break;
            case VT_BYREF | VT_UI4:
                this._variant.__variant.writeField("pulVal", value);
                break;
            case VT_BYREF | VT_UI8:
                this._variant.__variant.writeField("pullVal", value);
                break;
            case VT_BYREF | VT_R4:
                this._variant.__variant.writeField("pfltVal", value);
                break;
            case VT_BYREF | VT_R8:
                this._variant.__variant.writeField("pdblVal", value);
                break;
            case VT_BYREF | VT_BOOL:
                this._variant.__variant.writeField("pboolVal", value);
                break;
            case VT_BYREF | VT_ERROR:
                this._variant.__variant.writeField("pscode", value);
                break;
            case VT_BYREF | VT_CY:
                this._variant.__variant.writeField("pcyVal", value);
                break;
            case VT_BYREF | VT_DATE:
                this._variant.__variant.writeField("pdate", value);
                break;
            case VT_BYREF | VT_BSTR:
                this._variant.__variant.writeField("pbstrVal", value);
                break;
            case VT_BYREF | VT_UNKNOWN:
                this._variant.__variant.writeField("ppunkVal", value);
                break;
            case VT_BYREF | VT_DISPATCH:
                this._variant.__variant.writeField("ppdispVal", value);
                break;
            case VT_BYREF | VT_ARRAY:
                this._variant.__variant.writeField("pparray", value);
                break;
            case VT_BYREF | VT_VARIANT:
                this._variant.__variant.writeField("pvarVal", value);
                break;
            case VT_BYREF | VT_DECIMAL:
                this._variant.__variant.writeField("pdecVal", value);
                break;
            }

            this._variant.writeField("vt", vt);
            this.write();
        }

        public Object getValue() {
            this.read();
            switch (this.getVarType().intValue()) {
            case VT_I1:
                return this._variant.__variant.readField("cVal");
            case VT_I2:
                return this._variant.__variant.readField("iVal");
            case VT_INT:
              return this._variant.__variant.readField("intVal");
            case VT_I4:
                return this._variant.__variant.readField("lVal");
            case VT_I8:
                return this._variant.__variant.readField("llVal");
            case VT_UI1:
                return this._variant.__variant.readField("bVal");
            case VT_UI2:
                return this._variant.__variant.readField("uiVal");
            case VT_UINT:
              return this._variant.__variant.readField("uintVal");
            case VT_UI4:
                return this._variant.__variant.readField("ulVal");
            case VT_UI8:
                return this._variant.__variant.readField("ullVal");
            case VT_R4:
                return this._variant.__variant.readField("fltVal");
            case VT_R8:
                return this._variant.__variant.readField("dblVal");
            case VT_BOOL:
                return this._variant.__variant.readField("boolVal");
            case VT_HRESULT:
            case VT_ERROR:
                return this._variant.__variant.readField("scode");
            case VT_CY:
                return this._variant.__variant.readField("cyVal");
            case VT_DATE:
                return this._variant.__variant.readField("date");
            case VT_BSTR:
                return this._variant.__variant.readField("bstrVal");
            case VT_UNKNOWN:
                return this._variant.__variant.readField("punkVal");
            case VT_DISPATCH:
                return this._variant.__variant.readField("pdispVal");
            case VT_SAFEARRAY:
                return this._variant.__variant.readField("parray");
            case VT_ARRAY:
                return this._variant.__variant.readField("parray");
            case VT_BYREF:
                return this._variant.__variant.readField("byref");
            case VT_BYREF | VT_I1:
                return this._variant.__variant.readField("pcVal");
            case VT_BYREF | VT_I2:
                return this._variant.__variant.readField("piVal");
            case VT_BYREF | VT_INT:
                return this._variant.__variant.readField("pintVal");
            case VT_BYREF | VT_I4:
                return this._variant.__variant.readField("plVal");
            case VT_BYREF | VT_I8:
                return this._variant.__variant.readField("pllVal");
            case VT_BYREF | VT_UI1:
              return this._variant.__variant.readField("pbVal");
            case VT_BYREF | VT_UI2:
                return this._variant.__variant.readField("puiVal");
            case VT_BYREF | VT_UINT:
                return this._variant.__variant.readField("puintVal");
            case VT_BYREF | VT_UI4:
                return this._variant.__variant.readField("pulVal");
            case VT_BYREF | VT_UI8:
                return this._variant.__variant.readField("pullVal");
            case VT_BYREF | VT_R4:
                return this._variant.__variant.readField("pfltVal");
            case VT_BYREF | VT_R8:
                return this._variant.__variant.readField("pdblVal");
            case VT_BYREF | VT_BOOL:
                return this._variant.__variant.readField("pboolVal");
            case VT_BYREF | VT_ERROR:
                return this._variant.__variant.readField("pscode");
            case VT_BYREF | VT_CY:
                return this._variant.__variant.readField("pcyVal");
            case VT_BYREF | VT_DATE:
                return this._variant.__variant.readField("pdate");
            case VT_BYREF | VT_BSTR:
                return this._variant.__variant.readField("pbstrVal");
            case VT_BYREF | VT_UNKNOWN:
                return this._variant.__variant.readField("ppunkVal");
            case VT_BYREF | VT_DISPATCH:
                return this._variant.__variant.readField("ppdispVal");
            case VT_BYREF | VT_ARRAY:
                return this._variant.__variant.readField("pparray");
            case VT_BYREF | VT_VARIANT:
                return this._variant.__variant.readField("pvarVal");
            case VT_BYREF | VT_DECIMAL:
                return this._variant.__variant.readField("pdecVal");
            default:
                return null;
            }
        }

        public Number numberValue() {
            return (Number) this.getValue();
        }

        public int byteValue() {
            return ((Number) this.getValue()).byteValue();
        }

        public int shortValue() {
            return ((Number) this.getValue()).shortValue();
        }

        public int intValue() {
            return ((Number) this.getValue()).intValue();
        }

        public long longValue() {
          return ((Number) this.getValue()).longValue();
        }

        public float floatValue() {
          return ((Number) this.getValue()).floatValue();
        }

        public double doubleValue() {
          return ((Number) this.getValue()).doubleValue();
        }

        public String stringValue() {
            BSTR bstr = (BSTR) this.getValue();
            return bstr.getValue();
        }

        public boolean booleanValue() {
            return (Boolean) this.getValue();
        }

        public Date dateValue() {
            DATE varDate = (DATE) this.getValue();
            return this.toJavaDate(varDate);
        }

        protected Date toJavaDate(DATE varDate) {

            double doubleDate = varDate.date;
            long longDate = (long) doubleDate;

            double doubleTime = doubleDate - longDate;
            long longTime = (long) doubleTime * MICRO_SECONDS_PER_DAY;

            return new Date(
                    ((longDate - COM_DAYS_ADJUSTMENT) * MICRO_SECONDS_PER_DAY)
                            + longTime);
        }

        protected DATE fromJavaDate(Date javaDate) {
            long longTime = javaDate.getTime() % MICRO_SECONDS_PER_DAY;
            long longDate = ((javaDate.getTime() - longTime) / MICRO_SECONDS_PER_DAY)
                    + COM_DAYS_ADJUSTMENT;

            float floatTime = ((float) longTime)
                    / ((float) MICRO_SECONDS_PER_DAY);
            float floatDateTime = floatTime + longDate;
            return new DATE(floatDateTime);
        }

        public static class _VARIANT extends Structure {

            public VARTYPE vt;
            public short wReserved1;
            public short wReserved2;
            public short wReserved3;
            public __VARIANT __variant;

            public _VARIANT() {
            }

            public _VARIANT(Pointer pointer) {
                super(pointer);
                this.read();
            }

            public static class __VARIANT extends Union {
                // LONGLONG VT_I8
                public LONGLONG llVal;
                // LONG VT_I4
                public LONG lVal;
                // BYTE VT_UI1
                public BYTE bVal;
                // SHORT VT_I2
                public SHORT iVal;
                // FLOAT VT_R4
                public Float fltVal;
                // DOUBLE VT_R8
                public Double dblVal;
                // VARIANT_BOOL VT_BOOL
                public BOOL boolVal;
                // SCODE VT_ERROR
                public SCODE scode;
                // CY VT_CY
                public CURRENCY cyVal;
                // DATE VT_DATE
                public DATE date;
                // BSTR VT_BSTR
                public BSTR bstrVal;
                // IUnknown * VT_UNKNOWN
                public Unknown punkVal;
                // IDispatch * VT_DISPATCH
                public Dispatch pdispVal;
                // SAFEARRAY * VT_ARRAY
                public SAFEARRAY.ByReference parray;
                // BYTE * VT_BYREF|VT_UI1
                public ByteByReference pbVal;
                // SHORT * VT_BYREF|VT_I2
                public ShortByReference piVal;
                // LONG * VT_BYREF|VT_I4
                public LONGByReference plVal;
                // LONGLONG * VT_BYREF|VT_I8
                public LONGLONGByReference pllVal;
                // FLOAT * VT_BYREF|VT_R4
                public FloatByReference pfltVal;
                // DOUBLE * VT_BYREF|VT_R8
                public DoubleByReference pdblVal;
                // VARIANT_BOOL * VT_BYREF|VT_BOOL
                public VARIANT_BOOLByReference pboolVal;
                // VARIANT_BOOL * VT_BYREF|VT_BOOL
                public _VARIANT_BOOLByReference pbool;
                // SCODE * VT_BYREF|VT_ERROR
                public SCODEByReference pscode;
                // CY * VT_BYREF|VT_CY
                public CURRENCY.ByReference pcyVal;
                // DATE * VT_BYREF|VT_DATE
                public DATE.ByReference pdate;
                // BSTR * VT_BYREF|VT_BSTR
                public BSTR.ByReference pbstrVal;
                // IUnknown ** VT_BYREF|VT_UNKNOWN
                public Unknown.ByReference ppunkVal;
                // IDispatch ** VT_BYREF|VT_DISPATCH
                public Dispatch.ByReference ppdispVal;
                // SAFEARRAY ** VT_BYREF|VT_ARRAY
                public SAFEARRAY.ByReference pparray;
                // VARIANT * VT_BYREF|VT_VARIANT
                public VARIANT.ByReference pvarVal;
                // PVOID VT_BYREF (Generic ByRef)
                public PVOID byref;
                // CHAR VT_I1
                public CHAR cVal;
                // USHORT VT_UI2
                public USHORT uiVal;
                // ULONG VT_UI4
                public ULONG ulVal;
                // ULONGLONG VT_UI8
                public ULONGLONG ullVal;
                // INT VT_INT
                public Integer intVal;
                // UINT VT_UINT
                public UINT uintVal;
                // DECIMAL * VT_BYREF|VT_DECIMAL
                public DECIMAL.ByReference pdecVal;
                // CHAR * VT_BYREF|VT_I1
                public CHARByReference pcVal;
                // USHORT * VT_BYREF|VT_UI2
                public USHORTByReference puiVal;
                // ULONG * VT_BYREF|VT_UI4
                public ULONGByReference pulVal;
                // ULONGLONG * VT_BYREF|VT_UI8
                public ULONGLONGByReference pullVal;
                // INT * VT_BYREF|VT_INT
                public IntByReference pintVal;
                // UINT * VT_BYREF|VT_UINT
                public UINTByReference puintVal;

                public static class BRECORD extends Structure {
                    public static class ByReference extends BRECORD implements
                            Structure.ByReference {
                    }

                    public PVOID pvRecord;

                    public IRecordInfo pRecInfo;

                    public BRECORD() {
                    }

                    public BRECORD(Pointer pointer) {
                        super(pointer);
                    }

                    @Override
                    protected List getFieldOrder() {
                        return Arrays.asList(new String[] { "pvRecord",
                                "pRecInfo" });
                    }
                }

                public __VARIANT() {
                    super();
                    this.read();
                }

                public __VARIANT(Pointer pointer) {
                    super(pointer);
                    this.read();
                }
            }

            @Override
            protected List getFieldOrder() {
                return Arrays.asList(new String[] { "vt", "wReserved1",
                        "wReserved2", "wReserved3", "__variant" });
            }
        }
    }

    public static class VariantArg extends Structure {
        public static class ByReference extends VariantArg implements
                Structure.ByReference {

            public ByReference() {
            }

            public ByReference(VARIANT[] variantArg) {
                this.variantArg = variantArg;
            }
        }

        public VARIANT[] variantArg = new VARIANT[1];

        public VariantArg() {
        }

        public VariantArg(VARIANT[] variantArg) {
            this.variantArg = variantArg;
        }

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "variantArg" });
        }
    }
}
