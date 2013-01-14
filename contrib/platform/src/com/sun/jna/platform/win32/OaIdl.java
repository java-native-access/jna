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
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGLONG;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGLONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.LongByReference;

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
		public String bstrSource;

		/** The bstr description. */
		public String bstrDescription;

		/** The bstr help file. */
		public String bstrHelpFile;

		/** The dw help context. */
		public DWORD dwHelpContext;

		/** The pv reserved. */
		public PVOID pvReserved;

		/** The pfn deferred fill in. */
		public HRESULT pfnDeferredFillIn;

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
		public static final int SIZE = 8;

		public VARIANT_BOOL() {
			this(0);
		}

		public VARIANT_BOOL(long value) {
			super(2, value, false);
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
			getPointer().setLong(0, value.longValue());
		}

		public VARIANT_BOOL getValue() {
			return new VARIANT_BOOL(getPointer().getLong(0));
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

	public static class DATE extends IntegerType {
		public static final int SIZE = 8;
		public DATE() {
			this(0);
		}

		public DATE(long value) {
			super(8, value, true);
		}
	}

	public class DATEByReference extends ByReference {
		public DATEByReference() {
			this(new DATE(0));
		}

		public DATEByReference(DATE value) {
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
	 * The Class VARIANTARG.
	 */
	public class VARIANTARG extends VARIANT {

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends VARIANTARG implements
				Structure.ByReference {
		}

		/**
		 * Instantiates a new variantarg.
		 */
		public VARIANTARG() {
		}
	}

	/**
	 * The Class DISPID.
	 */
	public static class DISPID extends LONG {

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends LongByReference {
			public ByReference() {
				this(0L);
			}

			public ByReference(long value) {
				super(8);
				setValue(value);
			}

			public void setValue(long value) {
				getPointer().setLong(0, value);
			}

			public long getValue() {
				return getPointer().getLong(0);
			}

			public DISPID getDISPID() {
				return new DISPID(getValue());
			}
		}

		/**
		 * Instantiates a new dispid.
		 */
		public DISPID() {
			this(0);
		}

		/**
		 * Instantiates a new dispid.
		 * 
		 * @param value
		 *            the value
		 */
		public DISPID(long value) {
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

		public SAFEARRAY(Pointer p) {
			super(p);
			// TODO Auto-generated constructor stub
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

		public int cElements;
		public int lLbound;

		public SAFEARRAYBOUND() {
			// TODO Auto-generated constructor stub
		}

		public SAFEARRAYBOUND(int cElements, int lLbound) {
			this.cElements = cElements;
			this.lLbound = lLbound;
		}

		public SAFEARRAYBOUND(Pointer p) {
			super(p);
			// TODO Auto-generated constructor stub
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
		}

		public CURRENCY(Pointer pointer) {
			super(pointer);
		}

		public static class _CURRENCY extends Structure {
			public ULONG Lo;
			public LONG Hi;

			public _CURRENCY() {
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
		}

		public DECIMAL(Pointer pointer) {
			super(pointer);
		}

		public short wReserved;
		public _DECIMAL1 decimal1;
		public NativeLong Hi32;
		public _DECIMAL2 decimal2;

		public static class _DECIMAL1 extends Union {

			public _DECIMAL1() {
			}

			public _DECIMAL1(Pointer pointer) {
				super(pointer);
			}

			public static class _DECIMAL1_DECIMAL extends Structure {
				public BYTE scale;
				public BYTE sign;

				public _DECIMAL1_DECIMAL() {
				}

				public _DECIMAL1_DECIMAL(Pointer pointer) {
					super(pointer);
				}

				@Override
				protected List getFieldOrder() {
					return Arrays.asList(new String[] { "scale", "sign" });
				}
			}

			public USHORT signscale;
		}

		public static class _DECIMAL2 extends Union {

			public class _DECIMAL2_DECIMAL extends Structure {
				public BYTE Lo32;
				public BYTE Mid32;

				public _DECIMAL2_DECIMAL() {
				}

				public _DECIMAL2_DECIMAL(Pointer pointer) {
					super(pointer);
				}

				@Override
				protected List getFieldOrder() {
					return Arrays.asList(new String[] { "Lo32", "Mid32" });
				}
			}

			public ULONGLONG Lo64;
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "wReserved", "decimal1",
					"Hi32", "decimal2" });
		}
	}
}
