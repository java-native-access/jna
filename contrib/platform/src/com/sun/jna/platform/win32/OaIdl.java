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
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGLONG;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGLONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.ptr.ByReference;

// TODO: Auto-generated Javadoc
/**
 * The Interface OaIdl.
 */
public interface OaIdl {

	/** The Constant CC_FASTCALL. */
	public final static int CC_FASTCALL = 0;

	/** The Constant CC_CDECL. */
	public final static int CC_CDECL = 1;

	/** The Constant CC_MSCPASCAL. */
	public final static int CC_MSCPASCAL = CC_CDECL + 1;

	/** The Constant CC_PASCAL. */
	public final static int CC_PASCAL = CC_MSCPASCAL;

	/** The Constant CC_MACPASCAL. */
	public final static int CC_MACPASCAL = CC_PASCAL + 1;

	/** The Constant CC_STDCALL. */
	public final static int CC_STDCALL = CC_MACPASCAL + 1;

	/** The Constant CC_FPFASTCALL. */
	public final static int CC_FPFASTCALL = CC_STDCALL + 1;

	/** The Constant CC_SYSCALL. */
	public final static int CC_SYSCALL = CC_FPFASTCALL + 1;

	/** The Constant CC_MPWCDECL. */
	public final static int CC_MPWCDECL = CC_SYSCALL + 1;

	/** The Constant CC_MPWPASCAL. */
	public final static int CC_MPWPASCAL = CC_MPWCDECL + 1;

	/** The Constant CC_MAX. */
	public final static int CC_MAX = CC_MPWPASCAL + 1;

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

	public class VARIANT_BOOLbyReference extends ByReference {
		public VARIANT_BOOLbyReference() {
			this(new VARIANT_BOOL(0));
		}

		public VARIANT_BOOLbyReference(VARIANT_BOOL value) {
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

	public class _VARIANT_BOOLbyReference extends VARIANT_BOOLbyReference {
		public _VARIANT_BOOLbyReference() {
			this(new _VARIANT_BOOL(0));
		}

		public _VARIANT_BOOLbyReference(_VARIANT_BOOL value) {
			super(value);
		}
	}

	public static class DATE extends IntegerType {
		public static final int SIZE = 8;

		public DATE() {
			this(0);
		}

		public DATE(long value) {
			super(8, value, true);
		}
	}

	public class DATEbyReference extends ByReference {
		public DATEbyReference() {
			this(new DATE(0));
		}

		public DATEbyReference(DATE value) {
			super(DATE.SIZE);
			setValue(value);
		}

		public void setValue(DATE value) {
			getPointer().setLong(0, value.longValue());
		}

		public DATE getValue() {
			return new DATE(getPointer().getLong(0));
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

	public class DISPIDbyReference extends ByReference {
		public DISPIDbyReference() {
			this(new DISPID(0));
		}

		public DISPIDbyReference(DISPID value) {
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

	public final static int TKIND_ENUM = 0;
	public final static int TKIND_RECORD = TKIND_ENUM + 1;
	public final static int TKIND_MODULE = TKIND_RECORD + 1;
	public final static int TKIND_INTERFACE = TKIND_MODULE + 1;
	public final static int TKIND_DISPATCH = TKIND_INTERFACE + 1;
	public final static int TKIND_COCLASS = TKIND_DISPATCH + 1;
	public final static int TKIND_ALIAS = TKIND_COCLASS + 1;
	public final static int TKIND_UNION = TKIND_ALIAS + 1;
	public final static int TKIND_MAX = TKIND_UNION + 1;

	public final static int SYS_WIN16 = 0;
	public final static int SYS_WIN32 = SYS_WIN16 + 1;
	public final static int SYS_MAC = SYS_WIN32 + 1;
	public final static int SYS_WIN64 = SYS_MAC + 1;
	
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
			// TODO Auto-generated constructor stub
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
}
