/*
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
package com.sun.jna.platform.win32;

import java.util.Date;
import java.util.List;

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
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
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

    public static class VARIANT extends Union {

        public static class ByReference extends VARIANT implements
                Structure.ByReference {
            public ByReference(VARIANT variant) {
                setValue(variant.getVarType(), variant.getValue());
            }

            public ByReference(Pointer variant) {
                super(variant);
            }

            public ByReference() {
                super();
            }
        }

        public static class ByValue extends VARIANT implements
                Structure.ByValue {
            public ByValue(VARIANT variant) {
                setValue(variant.getVarType(), variant.getValue());
            }

            public ByValue(Pointer variant) {
                super(variant);
            }

            public ByValue() {
                super();
            }
        }

        public static final VARIANT VARIANT_MISSING;

        static {
                VARIANT_MISSING = new VARIANT();
                VARIANT_MISSING.setValue(VT_ERROR, new SCODE(WinError.DISP_E_PARAMNOTFOUND));
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
            this.setValue(VT_BOOL, value);
        }

        public VARIANT(BOOL value) {
            this(value.booleanValue());
        }

        public VARIANT(LONG value) {
            this();
            this.setValue(VT_I4, value);
        }

        public VARIANT(SHORT value) {
            this();
            this.setValue(VT_I2, value);
        }

        public VARIANT(DATE value) {
            this();
            this.setValue(VT_DATE, value);
        }

        public VARIANT(byte value) {
            this(new BYTE(value));
        }

        public VARIANT(BYTE value) {
            this();
            this.setValue(Variant.VT_UI1, value);
        }

        public VARIANT(char value) {
            this();
            this.setValue(VT_UI2, new USHORT(value));
        }
        
        public VARIANT(CHAR value) {
            this();
            this.setValue(Variant.VT_I1, value);
        }

        public VARIANT(short value) {
            this();
            this.setValue(VT_I2, new SHORT(value));
        }
        
        public VARIANT(int value) {
            this();
            this.setValue(VT_I4, new LONG(value));
        }

        public VARIANT(long value) {
            this();
            this.setValue(VT_I8, new LONGLONG(value));
        }

        public VARIANT(float value) {
            this();
            this.setValue(VT_R4, value);
        }

        public VARIANT(double value) {
            this();
            this.setValue(VT_R8, value);
        }

        /**
         * Create a new VARIANT wrapping the supplied string.
         * 
         * <p><i>Implementation note:</i> the string is wrapped as a BSTR value,
         * that is allocated using {@link com.sun.jna.platform.win32.OleAuto#SysAllocString}
         * and needs to be freed using
         * {@link com.sun.jna.platform.win32.OleAuto#SysFreeString} by the user</p>
         * 
         * @param value  to be wrapped
         */
        public VARIANT(String value) {
            this();
            BSTR bstrValue = OleAuto.INSTANCE.SysAllocString(value);
            this.setValue(VT_BSTR, bstrValue);
        }

        public VARIANT(boolean value) {
            this();
            this.setValue(VT_BOOL, new VARIANT_BOOL(value));
        }

        public VARIANT(IDispatch value) {
            this();
            this.setValue(Variant.VT_DISPATCH, value);
        }

        public VARIANT(Date value) {
            this();
            this.setValue(VT_DATE, new DATE(value));
        }

        public VARIANT(SAFEARRAY array) {
            this();
            this.setValue(array);
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
        
        public void setValue(SAFEARRAY array) {
            this.setValue(array.getVarType().intValue() | VT_ARRAY, array);
        }

        public void setValue(VARTYPE vt, Object value) {
            int varType = vt.intValue();
            switch (varType) {
            case VT_UI1:
                this._variant.__variant.writeField("bVal", value);
                break;
            case VT_I2:
                this._variant.__variant.writeField("iVal", value);
                break;
            case VT_I4:
                this._variant.__variant.writeField("lVal", value);
                break;
            case VT_I8:
                this._variant.__variant.writeField("llVal", value);
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
            case VT_BYREF | VT_UI1:
                this._variant.__variant.writeField("pbVal", value);
                break;
            case VT_BYREF | VT_I2:
                this._variant.__variant.writeField("piVal", value);
                break;
            case VT_BYREF | VT_I4:
                this._variant.__variant.writeField("plVal", value);
                break;
            case VT_BYREF | VT_I8:
                this._variant.__variant.writeField("pllVal", value);
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
            case VT_BYREF | VT_VARIANT:
                this._variant.__variant.writeField("pvarVal", value);
                break;
            case VT_BYREF:
                this._variant.__variant.writeField("byref", value);
                break;
            case VT_I1:
                this._variant.__variant.writeField("cVal", value);
                break;
            case VT_UI2:
                this._variant.__variant.writeField("uiVal", value);
                break;
            case VT_UI4:
                this._variant.__variant.writeField("ulVal", value);
                break;
            case VT_UI8:
                this._variant.__variant.writeField("ullVal", value);
                break;
            case VT_INT:
                this._variant.__variant.writeField("intVal", value);
                break;
            case VT_UINT:
                this._variant.__variant.writeField("uintVal", value);
                break;
            case VT_BYREF | VT_DECIMAL:
                this._variant.__variant.writeField("pdecVal", value);
                break;
            case VT_BYREF | VT_I1:
                this._variant.__variant.writeField("pcVal", value);
                break;
            case VT_BYREF | VT_UI2:
                this._variant.__variant.writeField("puiVal", value);
                break;
            case VT_BYREF | VT_UI4:
                this._variant.__variant.writeField("pulVal", value);
                break;
            case VT_BYREF | VT_UI8:
                this._variant.__variant.writeField("pullVal", value);
                break;
            case VT_BYREF | VT_INT:
                this._variant.__variant.writeField("pintVal", value);
                break;
            case VT_BYREF | VT_UINT:
                this._variant.__variant.writeField("puintVal", value);
                break;
            case VT_RECORD:
                this._variant.__variant.writeField("pvRecord", value);
                break;
            default:
                if ((varType & VT_ARRAY) > 0) {
                    if ((varType & VT_BYREF) > 0) {
                        this._variant.__variant.writeField("pparray", value);
                    } else {
                        this._variant.__variant.writeField("parray", value);
                    }
                }
            }

            this._variant.writeField("vt", vt);
            this.write();
        }

        public Object getValue() {
            this.read();
            int varType = this.getVarType().intValue();
            switch (this.getVarType().intValue()) {
            case VT_UI1:
                return this._variant.__variant.readField("bVal");
            case VT_I2:
                return this._variant.__variant.readField("iVal");
            case VT_I4:
                return this._variant.__variant.readField("lVal");
            case VT_I8:
                return this._variant.__variant.readField("llVal");
            case VT_R4:
                return this._variant.__variant.readField("fltVal");
            case VT_R8:
                return this._variant.__variant.readField("dblVal");
            case VT_BOOL:
                return this._variant.__variant.readField("boolVal");
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
            case VT_BYREF | VT_UI1:
                return this._variant.__variant.readField("pbVal");
            case VT_BYREF | VT_I2:
                return this._variant.__variant.readField("piVal");
            case VT_BYREF | VT_I4:
                return this._variant.__variant.readField("plVal");
            case VT_BYREF | VT_I8:
                return this._variant.__variant.readField("pllVal");
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
            case VT_BYREF | VT_VARIANT:
                return this._variant.__variant.readField("pvarVal");
            case VT_BYREF:
                return this._variant.__variant.readField("byref");
            case VT_I1:
                return this._variant.__variant.readField("cVal");
            case VT_UI2:
                return this._variant.__variant.readField("uiVal");
            case VT_UI4:
                return this._variant.__variant.readField("ulVal");
            case VT_UI8:
                return this._variant.__variant.readField("ullVal");
            case VT_INT:
                return this._variant.__variant.readField("intVal");
            case VT_UINT:
                return this._variant.__variant.readField("uintVal");
            case VT_BYREF | VT_DECIMAL:
                return this._variant.__variant.readField("pdecVal");
            case VT_BYREF | VT_I1:
                return this._variant.__variant.readField("pcVal");
            case VT_BYREF | VT_UI2:
                return this._variant.__variant.readField("puiVal");
            case VT_BYREF | VT_UI4:
                return this._variant.__variant.readField("pulVal");
            case VT_BYREF | VT_UI8:
                return this._variant.__variant.readField("pullVal");
            case VT_BYREF | VT_INT:
                return this._variant.__variant.readField("pintVal");
            case VT_BYREF | VT_UINT:
                return this._variant.__variant.readField("puintVal");
            case VT_RECORD:
                return this._variant.__variant.readField("pvRecord");
            default:
                if((varType & VT_ARRAY) > 0) {
                    if((varType & VT_BYREF) > 0) {
                        return this._variant.__variant.readField("pparray");
                    } else {
                        return this._variant.__variant.readField("parray");
                    }
                }
                return null;
            }
        }

        public byte byteValue() {
            return ((Number) this.getValue()).byteValue();
        }
        
        public short shortValue() {
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
            if(bstr == null) {
                return null;
            } else {
                return bstr.getValue();
            }
        }

        public boolean booleanValue() {
            // getValue returns a VARIANT_BOOL
            return ((VARIANT_BOOL) this.getValue()).booleanValue();
        }

        public Date dateValue() {
            DATE varDate = (DATE) this.getValue();
            if(varDate == null) {
                return null;
            } else {
                return varDate.getAsJavaDate();
            }
        }

        public static class _VARIANT extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("vt",
                    "wReserved1", "wReserved2", "wReserved3", "__variant");

            public static class __VARIANT extends Union {
                public static class BRECORD extends Structure {
                    public static class ByReference extends BRECORD implements
                            Structure.ByReference {
                    }

                    public static final List<String> FIELDS = createFieldsOrder("pvRecord", "pRecInfo");

                    public PVOID pvRecord;
                    public Pointer pRecInfo;

                    public BRECORD() {
                        super();
                    }

                    public BRECORD(Pointer pointer) {
                        super(pointer);
                    }

                    @Override
                    protected List<String> getFieldOrder() {
                        return FIELDS;
                    }
                }

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
                public VARIANT_BOOL boolVal;
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
                // BRECORD VT_RECORD
                public BRECORD pvRecord;

                public __VARIANT() {
                    super();
                    this.read();
                }

                public __VARIANT(Pointer pointer) {
                    super(pointer);
                    this.read();
                }
            }

            public VARTYPE vt;
            public short wReserved1;
            public short wReserved2;
            public short wReserved3;
            public __VARIANT __variant;

            public _VARIANT() {
                super();
            }

            public _VARIANT(Pointer pointer) {
                super(pointer);
                this.read();
            }

            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
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

        public static final List<String> FIELDS = createFieldsOrder("variantArg");
        public VARIANT[] variantArg = new VARIANT[1];

        public VariantArg() {
            super();
        }

        /**
         * construct VariantArg cast onto pre-allocated memory
         * @param pointer base address
         */
        public VariantArg(Pointer pointer) {
        	super(pointer);
        }

        public VariantArg(VARIANT[] variantArg) {
            this.variantArg = variantArg;
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public void setArraySize(int size) {
        	this.variantArg = new VARIANT[size];
        	this.read();
        }


    }
}
