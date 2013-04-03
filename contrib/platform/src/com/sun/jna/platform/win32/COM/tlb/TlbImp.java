package com.sun.jna.platform.win32.COM.tlb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.sun.jna.platform.win32.COM.ITypeInfoUtil;
import com.sun.jna.platform.win32.COM.ITypeLibUtil;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbClass;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbDispatch;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbEnum;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbInterface;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEDESC;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.WTypes.BSTR;

public class TlbImp {

	public final static String CR = "\n";

	public final static String CRCR = "\n\n";

	public final static String TYPELIB_ID_SHELL = "{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}";
	
	public final static String TYPELIB_ID_WORD = "{00020905-0000-0000-C000-000000000046}";
	
	private ITypeLibUtil typeLibUtil;

	private PrintStream out;
	
	private StringBuffer contentBuffer = new StringBuffer();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TlbImp().startCOM2Java();
	}

	public void startCOM2Java() {
		try {
			this.typeLibUtil = new ITypeLibUtil(
					TYPELIB_ID_SHELL, 1, 0);
			
			this.initPrintStream();
			
			for (int i = 0; i < typeLibUtil.getTypeInfoCount(); ++i) {
				TYPEKIND typekind = typeLibUtil.getTypeInfoType(i);

				if (typekind.value == TYPEKIND.TKIND_ENUM) {
					StringBuffer buffer = this.createCOMEnum(i, typeLibUtil);
					contentBuffer.append(buffer + CR);
				} else if (typekind.value == TYPEKIND.TKIND_RECORD) {
					System.out.println("TKIND_RECORD");
				} else if (typekind.value == TYPEKIND.TKIND_MODULE) {
					System.out.println("TKIND_MODULE");
				} else if (typekind.value == TYPEKIND.TKIND_INTERFACE) {
					StringBuffer buffer = this.createCOMInterface(i, typeLibUtil);
					contentBuffer.append(buffer + CR);
				} else if (typekind.value == TYPEKIND.TKIND_DISPATCH) {
					StringBuffer buffer = this.createCOMDispatch(i, typeLibUtil);
					contentBuffer.append(buffer + CR);
				} else if (typekind.value == TYPEKIND.TKIND_COCLASS) {
					System.out.println("TKIND_COCLASS");					
				} else if (typekind.value == TYPEKIND.TKIND_ALIAS) {
					System.out.println("TKIND_ALIAS");
				} else if (typekind.value == TYPEKIND.TKIND_UNION) {

				}
			}
			
			String packageName = "myPackage." + this.typeLibUtil.getName().toLowerCase();			
			TlbClass tlbClass = new TlbClass(-1, typeLibUtil);
			tlbClass.createPackage(packageName);
			tlbClass.createContent(contentBuffer.toString());
			this.out.print(tlbClass.getClassBuffer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initPrintStream() throws FileNotFoundException {
		String tmp = System.getProperty("java.io.tmpdir");
		File javaDir = new File(tmp + "_jnaCOM_" + System.currentTimeMillis()
				+ "\\myPackage\\" + this.typeLibUtil.getName().toLowerCase() + "\\");

		if (javaDir.exists())
			javaDir.delete();

		File javaFile = new File(javaDir, this.typeLibUtil.getName() + ".java");
		if (javaDir.mkdirs()) {
			logInfo("Output directory sucessfully created to: "
					+ javaDir.toString());
			this.out = new PrintStream(javaFile);
		} else {
			throw new FileNotFoundException(
					"Output directory NOT sucessfully created to: "
							+ javaDir.toString());
		}
	}

	private StringBuffer createCOMEnum(int index, ITypeLibUtil typeLibUtil) {
		TlbEnum tlbEnum = new TlbEnum(index, typeLibUtil);
		return tlbEnum.getClassBuffer();
	}

	private StringBuffer createCOMInterface(int index, ITypeLibUtil typeLibUtil) {
		TlbInterface tlbInterface = new TlbInterface(index, typeLibUtil);
		return tlbInterface.getClassBuffer();
	}

	private StringBuffer createCOMDispatch(int index, ITypeLibUtil typeLibUtil) {
		TlbDispatch tlbDispatch = new TlbDispatch(index, typeLibUtil);
		return tlbDispatch.getClassBuffer();
	}

	public static void logInfo(String msg) {
		System.out.println(msg);
	}
}
