/*
 *
 */
package com.sun.jna.platform.win32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.IntegerType;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.Variant.VariantArg;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGLONG;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGLONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.COM.ITypeComp;
import com.sun.jna.ptr.ByReference;

/**
 * The Interface OaIdl.
 */
public interface OaIdl {

	/**
	 * The Class EXCEPINFO.
	 */
	public class EXCEPINFO extends Structure {

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends EXCEPINFO implements
				Structure.ByReference {
		}

		/** The w code. */
		public WORD wCode;

		/** The w reserved. */
		public WORD wReserved;

		/** The bstr source. */
		public BSTR bstrSource;

		/** The bstr description. */
		public BSTR bstrDescription;

		/** The bstr help file. */
		public BSTR bstrHelpFile;

		/** The dw help context. */
		public DWORD dwHelpContext;

		/** The pv reserved. */
		public PVOID pvReserved;

		/** The pfn deferred fill in. */
		public EXCEPINFO.ByReference pfnDeferredFillIn;

		/** The scode. */
		public SCODE scode;

		/**
		 * Instantiates a new excepinfo.
		 */
		public EXCEPINFO() {
		}

		/**
		 * Instantiates a new excepinfo.
		 * 
		 * @param p
		 *            the p
		 */
		public EXCEPINFO(Pointer p) {
			super(p);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.sun.jna.Structure#getFieldOrder()
		 */
		@Override
		protected List getFieldOrder() {
			return Arrays
					.asList(new String[] { "wCode", "wReserved", "bstrSource",
							"bstrDescription", "bstrHelpFile", "dwHelpContext",
							"pvReserved", "pfnDeferredFillIn", "scode" });
		}
	}

	public static class VARIANT_BOOL extends IntegerType {
		public static final int SIZE = 2;

		public VARIANT_BOOL() {
			this(0);
		}

		public VARIANT_BOOL(long value) {
			super(2, value);
		}
	}

	public static class _VARIANT_BOOL extends VARIANT_BOOL {

		public _VARIANT_BOOL() {
			this(0);
		}

		public _VARIANT_BOOL(long value) {
			super(value);
		}
	}

	public class VARIANT_BOOLByReference extends ByReference {
		public VARIANT_BOOLByReference() {
			this(new VARIANT_BOOL(0));
		}

		public VARIANT_BOOLByReference(VARIANT_BOOL value) {
			super(VARIANT_BOOL.SIZE);
			setValue(value);
		}

		public void setValue(VARIANT_BOOL value) {
			getPointer().setShort(0, value.shortValue());
		}

		public VARIANT_BOOL getValue() {
			return new VARIANT_BOOL(getPointer().getShort(0));
		}
	}

	public class _VARIANT_BOOLByReference extends VARIANT_BOOLByReference {
		public _VARIANT_BOOLByReference() {
			this(new _VARIANT_BOOL(0));
		}

		public _VARIANT_BOOLByReference(_VARIANT_BOOL value) {
			super(value);
		}
	}

	public static class DATE extends Structure {
		public static class ByReference extends DATE implements
				Structure.ByReference {
		}

		public double date;

		public DATE() {
		}

		public DATE(double date) {
			this.date = date;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.sun.jna.Structure#getFieldOrder()
		 */
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "date" });
		}
	}

	/**
	 * The Class DISPID.
	 */
	public static class DISPID extends LONG {
		public DISPID() {
			this(0);
		}

		public DISPID(int value) {
			super(value);
		}
	}

	public class DISPIDByReference extends ByReference {
		public DISPIDByReference() {
			this(new DISPID(0));
		}

		public DISPIDByReference(DISPID value) {
			super(DISPID.SIZE);
			setValue(value);
		}

		public void setValue(DISPID value) {
			getPointer().setInt(0, value.intValue());
		}

		public DISPID getValue() {
			return new DISPID(getPointer().getInt(0));
		}
	}

	public static class MEMBERID extends DISPID {
		public MEMBERID() {
			this(0);
		}

		public MEMBERID(int value) {
			super(value);
		}
	}

	public class MEMBERIDByReference extends ByReference {
		public MEMBERIDByReference() {
			this(new MEMBERID(0));
		}

		public MEMBERIDByReference(MEMBERID value) {
			super(MEMBERID.SIZE);
			setValue(value);
		}

		public void setValue(MEMBERID value) {
			getPointer().setInt(0, value.intValue());
		}

		public MEMBERID getValue() {
			return new MEMBERID(getPointer().getInt(0));
		}
	}

	// The Collect property. You use this property if the method you are calling
	// through Invoke is an accessor function.
	/** The Constant DISPID_COLLECT. */
	public final static DISPID DISPID_COLLECT = new DISPID(-8);

	// The C++ constructor function for the object.
	/** The Constant DISPID_CONSTRUCTOR. */
	public final static DISPID DISPID_CONSTRUCTOR = new DISPID(-6);

	// The C++ destructor function for the object.
	/** The Constant DISPID_DESTRUCTOR. */
	public final static DISPID DISPID_DESTRUCTOR = new DISPID(-7);

	// The Evaluate method. This method is implicitly invoked when the ActiveX
	// client encloses the arguments in square brackets. For example, the
	// following two lines are equivalent:
	/** The Constant DISPID_EVALUATE. */
	public final static DISPID DISPID_EVALUATE = new DISPID(-5);

	// The _NewEnum property. This special, restricted property is required for
	// collection objects. It returns an enumerator object that supports
	// IEnumVARIANT, and should have the restricted attribute specified.
	/** The Constant DISPID_NEWENUM. */
	public final static DISPID DISPID_NEWENUM = new DISPID(-4);

	// The parameter that receives the value of an assignment in a PROPERTYPUT.
	/** The Constant DISPID_PROPERTYPUT. */
	public final static DISPID DISPID_PROPERTYPUT = new DISPID(-3);

	// The value returned by IDispatch::GetIDsOfNames to indicate that a member
	// or parameter name was not found.
	/** The Constant DISPID_UNKNOWN. */
	public final static DISPID DISPID_UNKNOWN = new DISPID(-1);

	// The default member for the object. This property or method is invoked
	// when an ActiveX client specifies the object name without a property or
	// method.
	/** The Constant DISPID_VALUE. */
	public final static DISPID DISPID_VALUE = new DISPID(0);

	/** An array that is allocated on the stack. */
	public final static int FADF_AUTO = 0x0001;

	/** An array that is statically allocated. */
	public final static int FADF_STATIC = 0x0002;

	/** An array that is embedded in a structure. */
	public final static int FADF_EMBEDDED = 0x0004;

	/** An array that may not be resized or reallocated. */
	public final static int FADF_FIXEDSIZE = 0x0010;

	/**
	 * An array that contains records. When set, there will be a pointer to the
	 * IRecordInfo interface at negative offset 4 in the array descriptor.
	 */
	public final static int FADF_RECORD = 0x0020;

	/**
	 * An array that has an IID identifying interface. When set, there will be a
	 * GUID at negative offset 16 in the safe array descriptor. Flag is set only
	 * when FADF_DISPATCH or FADF_UNKNOWN is also set.
	 */
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

	public static class TYPEKIND extends IntegerType {
		// / <i>native declaration : line 4</i>
		public static final int TKIND_ENUM = 0;
		// / <i>native declaration : line 5</i>
		public static final int TKIND_RECORD = TYPEKIND.TKIND_ENUM + 1;
		// / <i>native declaration : line 6</i>
		public static final int TKIND_MODULE = TYPEKIND.TKIND_RECORD + 1;
		// / <i>native declaration : line 7</i>
		public static final int TKIND_INTERFACE = TYPEKIND.TKIND_MODULE + 1;
		// / <i>native declaration : line 8</i>
		public static final int TKIND_DISPATCH = TYPEKIND.TKIND_INTERFACE + 1;
		// / <i>native declaration : line 9</i>
		public static final int TKIND_COCLASS = TYPEKIND.TKIND_DISPATCH + 1;
		// / <i>native declaration : line 10</i>
		public static final int TKIND_ALIAS = TYPEKIND.TKIND_COCLASS + 1;
		// / <i>native declaration : line 11</i>
		public static final int TKIND_UNION = TYPEKIND.TKIND_ALIAS + 1;
		// / <i>native declaration : line 12</i>
		public static final int TKIND_MAX = TYPEKIND.TKIND_UNION + 1;

		public TYPEKIND() {
			super(4);
		}
	};

	public static class DESCKIND {
		public static class ByReference extends DESCKIND implements
				Structure.ByReference {
		}

		// / <i>native declaration : line 4</i>
		public static final int DESCKIND_NONE = 0;
		// / <i>native declaration : line 5</i>
		public static final int DESCKIND_FUNCDESC = DESCKIND.DESCKIND_NONE + 1;
		// / <i>native declaration : line 6</i>
		public static final int DESCKIND_VARDESC = DESCKIND.DESCKIND_FUNCDESC + 1;
		// / <i>native declaration : line 7</i>
		public static final int DESCKIND_TYPECOMP = DESCKIND.DESCKIND_VARDESC + 1;
		// / <i>native declaration : line 8</i>
		public static final int DESCKIND_IMPLICITAPPOBJ = DESCKIND.DESCKIND_TYPECOMP + 1;
		// / <i>native declaration : line 9</i>
		public static final int DESCKIND_MAX = DESCKIND.DESCKIND_IMPLICITAPPOBJ + 1;
	};

	public class SAFEARRAY extends Structure {
		public static class ByReference extends SAFEARRAY implements
				Structure.ByReference {
		}

		public USHORT cDims;
		public USHORT fFeatures;
		public ULONG cbElements;
		public ULONG cLocks;
		public PVOID pvData;

		/** The rgsabound. */
		public SAFEARRAYBOUND[] rgsabound = new SAFEARRAYBOUND[1];

		public SAFEARRAY() {
		}

		public SAFEARRAY(Pointer pointer) {
			super(pointer);
			this.read();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "cDims", "fFeatures",
					"cbElements", "cLocks", "pvData", "rgsabound" });
		}
	}

	public static class SAFEARRAYBOUND extends Structure {

		public static class ByReference extends SAFEARRAYBOUND implements
				Structure.ByReference {
		}

		public ULONG cElements;

		public LONG lLbound;

		public SAFEARRAYBOUND() {
		}

		public SAFEARRAYBOUND(Pointer pointer) {
			super(pointer);
			this.read();
		}

		public SAFEARRAYBOUND(int cElements, int lLbound) {
			this.cElements = new ULONG(cElements);
			this.lLbound = new LONG(lLbound);
			this.write();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "cElements", "lLbound" });
		}
	}

	public static class CURRENCY extends Union {

		public static class ByReference extends CURRENCY implements
				Structure.ByReference {
		};

		public _CURRENCY currency;
		public LONGLONG int64;

		public CURRENCY() {
			super();
		}

		public CURRENCY(Pointer pointer) {
			super(pointer);
		}

		public static class _CURRENCY extends Structure {
			public ULONG Lo;
			public LONG Hi;

			public _CURRENCY() {
				super();
			}

			public _CURRENCY(Pointer pointer) {
				super(pointer);
			}

			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] { "Lo", "Hi" });
			}
		}
	}

	public static class DECIMAL extends Structure {

		public static class ByReference extends DECIMAL implements
				Structure.ByReference {
		};

		public DECIMAL() {
			super();
		}

		public DECIMAL(Pointer pointer) {
			super(pointer);
		}

		public short wReserved;
		public _DECIMAL1 decimal1;
		public NativeLong Hi32;
		public _DECIMAL2 decimal2;

		public static class _DECIMAL1 extends Union {

			public USHORT signscale;
			public _DECIMAL1_DECIMAL decimal1_DECIMAL;

			public _DECIMAL1() {
				super();
				this.setType("signscale");
			}

			public _DECIMAL1(Pointer pointer) {
				super(pointer);
				this.setType("signscale");
				this.read();
			}

			public static class _DECIMAL1_DECIMAL extends Structure {
				public BYTE scale;
				public BYTE sign;

				public _DECIMAL1_DECIMAL() {
					super();
				}

				public _DECIMAL1_DECIMAL(Pointer pointer) {
					super(pointer);
				}

				@Override
				protected List getFieldOrder() {
					return Arrays.asList(new String[] { "scale", "sign" });
				}
			}
		}

		public static class _DECIMAL2 extends Union {

			public ULONGLONG Lo64;
			public _DECIMAL2_DECIMAL decimal2_DECIMAL;

			public _DECIMAL2() {
				this.setType("Lo64");
			}

			public _DECIMAL2(Pointer pointer) {
				super(pointer);
				this.setType("Lo64");
				this.read();
			}

			public static class _DECIMAL2_DECIMAL extends Structure {
				public BYTE Lo32;
				public BYTE Mid32;

				public _DECIMAL2_DECIMAL() {
					super();
				}

				public _DECIMAL2_DECIMAL(Pointer pointer) {
					super(pointer);
				}

				@Override
				protected List getFieldOrder() {
					return Arrays.asList(new String[] { "Lo32", "Mid32" });
				}
			}
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "wReserved", "decimal1",
					"Hi32", "decimal2" });
		}
	}

	public static class TLIBATTR extends Structure {
		public static class ByReference extends TLIBATTR implements
				Structure.ByReference {
		};

		public GUID guid;
		public LCID lcid;
		public int syskind;
		public WORD wMajorVerNum;
		public WORD wMinorVerNum;
		public WORD wLibFlags;

		public TLIBATTR() {
			super();
		}

		public TLIBATTR(Pointer pointer) {
			super(pointer);
			this.read();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "guid", "lcid", "syskind",
					"wMajorVerNum", "wMinorVerNum", "wLibFlags" });
		}
	}

	public static class BINDPTR extends Union {
		public static class ByReference extends BINDPTR implements
				Structure.ByReference {
		};

		// / C type : FUNCDESC*
		public FUNCDESC lpfuncdesc;
		// / C type : VARDESC*
		public VARDESC lpvardesc;
		// / C type : ITypeComp*
		public ITypeComp lptcomp;

		public BINDPTR() {
			super();
		}

		// / @param lpvardesc C type : VARDESC*
		public BINDPTR(VARDESC lpvardesc) {
			super();
			this.lpvardesc = lpvardesc;
			setType(VARDESC.class);
		}

		// / @param lptcomp C type : ITypeComp*
		public BINDPTR(ITypeComp lptcomp) {
			super();
			this.lptcomp = lptcomp;
			setType(ITypeComp.class);
		}

		// / @param lpfuncdesc C type : FUNCDESC*
		public BINDPTR(FUNCDESC lpfuncdesc) {
			super();
			this.lpfuncdesc = lpfuncdesc;
			setType(FUNCDESC.class);
		}
	}

	public static class FUNCDESC extends Structure {
		public static class ByReference extends FUNCDESC implements
				Structure.ByReference {
		};

		// / C type : MEMBERID
		public MEMBERID memid;
		/**
		 * [size_is]<br>
		 * C type : SCODE*
		 */
		public SCODE lprgscode;
		/**
		 * [size_is]<br>
		 * C type : ELEMDESC*
		 */
		public ELEMDESC lprgelemdescParam;
		// / C type : FUNCKIND
		public FUNCKIND funckind;
		// / C type : INVOKEKIND
		public INVOKEKIND invkind;
		// / C type : CALLCONV
		public CALLCONV callconv;
		public short cParams;
		public short cParamsOpt;
		public short oVft;
		public short cScodes;
		// / C type : ELEMDESC
		public ELEMDESC elemdescFunc;
		public short wFuncFlags;

		public FUNCDESC() {
			super();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "memid", "lprgscode",
					"lprgelemdescParam", "funckind", "invkind", "callconv",
					"cParams", "cParamsOpt", "oVft", "cScodes", "elemdescFunc",
					"wFuncFlags" });
		}
	}

	public static class VARDESC extends Structure {
		public static class ByReference extends VARDESC implements
				Structure.ByReference {

		};

		// / C type : MEMBERID
		public MEMBERID memid;
		// / C type : LPOLESTR
		public WString lpstrSchema;
		/**
		 * [switch_is][switch_type]<br>
		 * C type : DUMMYUNIONNAMEUnion
		 */
		public _Union union;
		// / C type : ELEMDESC
		public ELEMDESC elemdescVar;
		public short wVarFlags;
		// / C type : VARKIND
		public VARKIND varkind;

		// / <i>native declaration : line 6</i>
		// / <i>native declaration : line 6</i>
		public static class _Union extends Union {

			public static class ByReference extends _Union implements
					Structure.ByReference {
			};

			// / [case()]
			public NativeLong oInst;
			/**
			 * [case()]<br>
			 * C type : VARIANT*
			 */
			public VARIANT lpvarValue;

			public _Union() {
				super();
			}

			/**
			 * @param lpvarValue
			 *            [case()]<br>
			 *            C type : VARIANT*
			 */
			public _Union(VARIANT lpvarValue) {
				super();
				this.lpvarValue = lpvarValue;
				setType(VARIANT.class);
			}

			// / @param oInst [case()]
			public _Union(NativeLong oInst) {
				super();
				this.oInst = oInst;
				setType(NativeLong.class);
			}
		};

		public VARDESC() {
			super();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "memid", "lpstrSchema",
					"union", "elemdescVar", "wVarFlags", "varkind" });
		}

		/**
		 * @param memid
		 *            C type : MEMBERID<br>
		 * @param lpstrSchema
		 *            C type : LPOLESTR<br>
		 * @param union
		 *            [switch_is][switch_type]<br>
		 *            C type : DUMMYUNIONNAMEUnion<br>
		 * @param elemdescVar
		 *            C type : ELEMDESC<br>
                 * @param wVarFlags
                 *            C type : short
		 * @param varkind
		 *            C type : VARKIND
		 */
		public VARDESC(MEMBERID memid, WString lpstrSchema, _Union union,
				ELEMDESC elemdescVar, short wVarFlags, VARKIND varkind) {
			super();
			this.memid = memid;
			this.lpstrSchema = lpstrSchema;
			this.union = union;
			this.elemdescVar = elemdescVar;
			this.wVarFlags = wVarFlags;
			this.varkind = varkind;
		}
	}

	public class ELEMDESC extends Structure {
		public static class ByReference extends ELEMDESC implements
				Structure.ByReference {
		};

		/**
		 * the type of the element<br>
		 * C type : TYPEDESC
		 */
		public TYPEDESC tdesc;
		// / C type : DUMMYUNIONNAMEUnion
		public _Union union;

		// / <i>native declaration : line 4</i>
		// / <i>native declaration : line 4</i>
		public static class _Union extends Union {
			public static class ByReference extends _Union implements
					Structure.ByReference {
			};

			/**
			 * info for remoting the element<br>
			 * C type : IDLDESC
			 */
			public IDLDESC idldesc;
			/**
			 * info about the parameter<br>
			 * C type : PARAMDESC
			 */
			public PARAMDESC paramdesc;

			public _Union() {
				super();
			}

			/**
			 * @param paramdesc
			 *            info about the parameter<br>
			 *            C type : PARAMDESC
			 */
			public _Union(PARAMDESC paramdesc) {
				super();
				this.paramdesc = paramdesc;
				setType(PARAMDESC.class);
			}

			/**
			 * @param idldesc
			 *            info for remoting the element<br>
			 *            C type : IDLDESC
			 */
			public _Union(IDLDESC idldesc) {
				super();
				this.idldesc = idldesc;
				setType(IDLDESC.class);
			}
		};

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "tdesc", "union" });
		}

		public ELEMDESC() {
		}

		public ELEMDESC(TYPEDESC tdesc, _Union union) {
			super();
			this.tdesc = tdesc;
			this.union = union;
		}
	}

	public static class FUNCKIND extends Structure {
		public static class ByReference extends FUNCKIND implements
				Structure.ByReference {
		};

		// / <i>native declaration : line 20</i>
		public static final int FUNC_VIRTUAL = 0;
		// / <i>native declaration : line 21</i>
		public static final int FUNC_PUREVIRTUAL = FUNC_VIRTUAL + 1;
		// / <i>native declaration : line 22</i>
		public static final int FUNC_NONVIRTUAL = FUNC_PUREVIRTUAL + 1;
		// / <i>native declaration : line 23</i>
		public static final int FUNC_STATIC = FUNC_NONVIRTUAL + 1;
		// / <i>native declaration : line 24</i>
		public static final int FUNC_DISPATCH = FUNC_STATIC + 1;

		public int value;

		public FUNCKIND() {
		}

		public FUNCKIND(int value) {
			this.value = value;

		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "value" });
		}
	};

	public static class INVOKEKIND extends Structure {
		public static class ByReference extends INVOKEKIND implements
				Structure.ByReference {
		};

		// / <i>native declaration : line 30</i>
		public static final INVOKEKIND INVOKE_FUNC = new INVOKEKIND(1);
		// / <i>native declaration : line 31</i>
		public static final INVOKEKIND INVOKE_PROPERTYGET = new INVOKEKIND(2);
		// / <i>native declaration : line 32</i>
		public static final INVOKEKIND INVOKE_PROPERTYPUT = new INVOKEKIND(4);
		// / <i>native declaration : line 33</i>
		public static final INVOKEKIND INVOKE_PROPERTYPUTREF = new INVOKEKIND(8);

		public int value;

		public INVOKEKIND() {
		}

		public INVOKEKIND(int value) {
			this.value = value;

		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "value" });
		}
	};

	public static class CALLCONV extends Structure {
		public static class ByReference extends CALLCONV implements
				Structure.ByReference {
		};

		// / <i>native declaration : line 4</i>
		public static final int CC_FASTCALL = 0;
		// / <i>native declaration : line 5</i>
		public static final int CC_CDECL = 1;
		// / <i>native declaration : line 6</i>
		public static final int CC_MSCPASCAL = CALLCONV.CC_CDECL + 1;
		// / <i>native declaration : line 7</i>
		public static final int CC_PASCAL = CALLCONV.CC_MSCPASCAL;
		// / <i>native declaration : line 8</i>
		public static final int CC_MACPASCAL = CALLCONV.CC_PASCAL + 1;
		// / <i>native declaration : line 9</i>
		public static final int CC_STDCALL = CALLCONV.CC_MACPASCAL + 1;
		// / <i>native declaration : line 10</i>
		public static final int CC_FPFASTCALL = CALLCONV.CC_STDCALL + 1;
		// / <i>native declaration : line 11</i>
		public static final int CC_SYSCALL = CALLCONV.CC_FPFASTCALL + 1;
		// / <i>native declaration : line 12</i>
		public static final int CC_MPWCDECL = CALLCONV.CC_SYSCALL + 1;
		// / <i>native declaration : line 13</i>
		public static final int CC_MPWPASCAL = CALLCONV.CC_MPWCDECL + 1;
		// / <i>native declaration : line 14</i>
		public static final int CC_MAX = CALLCONV.CC_MPWPASCAL + 1;

		public int value;

		public CALLCONV() {
		}

		public CALLCONV(int value) {
			this.value = value;
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "value" });
		}
	};

	public static class VARKIND extends Structure {
		public static class ByReference extends VARKIND implements
				Structure.ByReference {
		};

		// / <i>native declaration : line 4</i>
		public static final int VAR_PERINSTANCE = 0;
		// / <i>native declaration : line 5</i>
		public static final int VAR_STATIC = VAR_PERINSTANCE + 1;
		// / <i>native declaration : line 6</i>
		public static final int VAR_CONST = VAR_STATIC + 1;
		// / <i>native declaration : line 7</i>
		public static final int VAR_DISPATCH = VAR_CONST + 1;

		public int value;

		public VARKIND() {
		}

		public VARKIND(int value) {
			this.value = value;
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "value" });
		}
	};

	public class TYPEDESC extends Structure {
		public static class ByReference extends TYPEDESC implements
				Structure.ByReference {
                    public ByReference(TYPEDESC d) { super(d.getPointer()); }
                    public ByReference() { }
		};

		public _TYPEDESC _typeDesc;

		public VARTYPE vt;

		public static class _TYPEDESC extends Union {

			public TYPEDESC.ByReference lptdesc;

			public ARRAYDESC.ByReference lpadesc;

			public HREFTYPEByReference hreftype;

			public _TYPEDESC() {
				super();
				setType("lptdesc");
				this.read();
			}

			public _TYPEDESC(Pointer pointer) {
				super(pointer);
				this.read();
			}

			public _TYPEDESC(ARRAYDESC lpadesc) {
				super();
				this.lpadesc = new ARRAYDESC.ByReference(lpadesc);
				setType("lpadesc");
			}

			public _TYPEDESC(HREFTYPEByReference hreftype) {
				super();
				this.hreftype = hreftype;
				setType("hreftype");
			}

			public _TYPEDESC(TYPEDESC lptdesc) {
				super();
				this.lptdesc = new TYPEDESC.ByReference(lptdesc);
				setType("lptdesc");
			}
		};

		public TYPEDESC() {
		}

		public TYPEDESC(Pointer pointer) {
			super(pointer);
		}

		public TYPEDESC(_TYPEDESC _typeDesc, VARTYPE vt) {
			super();
			this._typeDesc = _typeDesc;
			this.vt = vt;
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "_typeDesc", "vt" });
		}
	}

	public class IDLDESC extends Structure {
		public static class ByReference extends IDLDESC implements
				Structure.ByReference {
		};

		// / C type : ULONG_PTR
		public ULONG_PTR dwReserved;
		public short wIDLFlags;

		public IDLDESC() {
			super();
		}

		// / @param dwReserved C type : ULONG_PTR
		public IDLDESC(ULONG_PTR dwReserved, short wIDLFlags) {
			super();
			this.dwReserved = dwReserved;
			this.wIDLFlags = wIDLFlags;
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "dwReserved", "wIDLFlags" });
		}
	}

	public class ARRAYDESC extends Structure {
		public static class ByReference extends ARRAYDESC implements
				Structure.ByReference {
                    public ByReference(ARRAYDESC o) { super(o.getPointer()); }
                    public ByReference() { }
		};

		public TYPEDESC.ByReference tdescElem;

		public USHORT cDims;

		public ARRAYDESC() {
			super();
		}

		public ARRAYDESC(Pointer p) {
			super(p);
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "tdescElem", "cDims" });
		}
	}

	public class PARAMDESC extends Structure {
		public static class ByReference extends PARAMDESC implements
				Structure.ByReference {
		};

		public PARAMDESCEX.ByReference pparamdescex;
		public USHORT wParamFlags;

		public PARAMDESC() {
			super();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays
					.asList(new String[] { "pparamdescex", "wParamFlags" });
		}
	}

	public class PARAMDESCEX extends Structure {
		public static class ByReference extends PARAMDESCEX implements
				Structure.ByReference {
		};

		public ULONG cBytes;
		public VariantArg varDefaultValue;

		public PARAMDESCEX() {
			super();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "cBytes", "varDefaultValue" });
		}
	}

	public static class HREFTYPE extends DWORD {
		public HREFTYPE() {
			super();
		}

		public HREFTYPE(long value) {
			super(value);
		}
	}

	public class HREFTYPEByReference extends DWORDByReference {
		public HREFTYPEByReference() {
			this(new HREFTYPE(0));
		}

		public HREFTYPEByReference(DWORD value) {
			super(value);
		}

		public void setValue(HREFTYPE value) {
			getPointer().setInt(0, value.intValue());
		}

		public HREFTYPE getValue() {
			return new HREFTYPE(getPointer().getInt(0));
		}
	}

	public class TYPEATTR extends Structure {
		public static class ByReference extends TYPEATTR implements
				Structure.ByReference {

			public ByReference() {
			}

			public ByReference(Pointer memory) {
				super(memory);
			}
		};

		public GUID guid;
		public LCID lcid;
		public DWORD dwReserved;
		public MEMBERID memidConstructor;
		public MEMBERID memidDestructor;
		public WString lpstrSchema;
		public ULONG cbSizeInstance;
		public TYPEKIND typekind;
		public WORD cFuncs;
		public WORD cVars;
		public WORD cImplTypes;
		public WORD cbSizeVft;
		public WORD cbAlignment;
		public WORD wTypeFlags;
		public WORD wMajorVerNum;
		public WORD wMinorVerNum;
		public TYPEDESC tdescAlias;
		public IDLDESC idldescType;

		public TYPEATTR() {
		}

		public TYPEATTR(Pointer pointer) {
			super(pointer);
			this.read();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays
					.asList(new String[] { "guid", "lcid", "dwReserved",
							"memidConstructor", "memidDestructor",
							"lpstrSchema", "cbSizeInstance", "typekind",
							"cFuncs", "cVars", "cImplTypes", "cbSizeVft",
							"cbAlignment", "wTypeFlags", "wMajorVerNum",
							"wMinorVerNum", "tdescAlias", "idldescType" });
		}
	}
}
